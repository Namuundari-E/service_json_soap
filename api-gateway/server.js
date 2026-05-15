const http = require("http");
const https = require("https");
const net = require("net");

const PORT = Number(process.env.PORT || 3000);
const JSON_SERVICE_URL = process.env.JSON_SERVICE_URL || "http://localhost:8082";
const SOAP_SERVICE_URL = process.env.SOAP_SERVICE_URL || "http://localhost:8081";
const FILE_SERVICE_URL = process.env.FILE_SERVICE_URL || JSON_SERVICE_URL;
const CACHE_TTL_SECONDS = Number(process.env.CACHE_TTL_SECONDS || 60);
const REDIS_HOST = process.env.REDIS_HOST || "127.0.0.1";
const REDIS_PORT = Number(process.env.REDIS_PORT || 6379);

function send(res, statusCode, body, headers = {}) {
  res.writeHead(statusCode, {
    "access-control-allow-origin": "*",
    "access-control-allow-methods": "GET,POST,PUT,DELETE,OPTIONS",
    "access-control-allow-headers": "Content-Type,Authorization",
    ...headers,
  });
  res.end(body);
}

function readRequestBody(req) {
  return new Promise((resolve, reject) => {
    const chunks = [];
    req.on("data", (chunk) => chunks.push(chunk));
    req.on("end", () => resolve(Buffer.concat(chunks)));
    req.on("error", reject);
  });
}

function encodeRedisCommand(args) {
  return `*${args.length}\r\n${args.map((arg) => {
    const value = String(arg);
    return `$${Buffer.byteLength(value)}\r\n${value}\r\n`;
  }).join("")}`;
}

function parseRedisResponse(data) {
  if (!data || data[0] === "$" && data.startsWith("$-1")) {
    return null;
  }

  if (data[0] === "$") {
    const separator = data.indexOf("\r\n");
    const length = Number(data.slice(1, separator));
    if (length < 0) {
      return null;
    }
    return data.slice(separator + 2, separator + 2 + length);
  }

  if (data[0] === "*") {
    return data
      .split("\r\n")
      .filter((line) => line && !line.startsWith("*") && !line.startsWith("$"));
  }

  if (data[0] === "+" || data[0] === ":") {
    return data.slice(1).trim();
  }

  return null;
}

function redis(args) {
  return new Promise((resolve) => {
    const socket = net.createConnection({ host: REDIS_HOST, port: REDIS_PORT });
    const chunks = [];
    let settled = false;

    const finish = (value) => {
      if (!settled) {
        settled = true;
        socket.destroy();
        resolve(value);
      }
    };

    socket.setTimeout(500);
    socket.on("connect", () => socket.write(encodeRedisCommand(args)));
    socket.on("data", (data) => {
      chunks.push(data);
      finish(parseRedisResponse(Buffer.concat(chunks).toString("utf8")));
    });
    socket.on("end", () => finish(parseRedisResponse(Buffer.concat(chunks).toString("utf8"))));
    socket.on("timeout", () => finish(null));
    socket.on("error", () => finish(null));
  });
}

async function getCached(key) {
  return redis(["GET", key]);
}

async function setCached(key, value) {
  await redis(["SETEX", key, String(CACHE_TTL_SECONDS), value]);
}

async function invalidateUsersCache() {
  const keys = await redis(["KEYS", "GET:/api/users*"]);
  if (!keys) {
    return;
  }

  await Promise.all(
    keys.filter(Boolean).map((key) => redis(["DEL", key]))
  );
}

function routeFor(pathname) {
  if (pathname.startsWith("/api/users")) {
    return {
      baseUrl: JSON_SERVICE_URL,
      path: pathname.replace(/^\/api\/users/, "/users"),
    };
  }

  if (pathname.startsWith("/api/files")) {
    return {
      baseUrl: FILE_SERVICE_URL,
      path: pathname.replace(/^\/api\/files/, "/users"),
    };
  }

  if (pathname.startsWith("/api/soap")) {
    return {
      baseUrl: SOAP_SERVICE_URL,
      path: pathname.replace(/^\/api\/soap/, "/ws"),
    };
  }

  return null;
}

async function proxy(req, res) {
  if (req.method === "OPTIONS") {
    send(res, 204, "");
    return;
  }

  const incomingUrl = new URL(req.url, `http://${req.headers.host}`);
  const route = routeFor(incomingUrl.pathname);

  if (!route) {
    send(res, 404, JSON.stringify({ error: "Route not found" }), {
      "content-type": "application/json",
    });
    return;
  }

  const targetUrl = new URL(route.path + incomingUrl.search, route.baseUrl);
  const cacheKey = `${req.method}:${incomingUrl.pathname}${incomingUrl.search}`;
  const hasAuthorization = Boolean(req.headers.authorization);

  if (req.method === "GET" && !hasAuthorization) {
    const cached = await getCached(cacheKey);
    if (cached) {
      console.log(`Cache Hit: ${cacheKey}`);
      send(res, 200, cached, {
        "content-type": "application/json",
        "x-cache": "HIT",
      });
      return;
    }
    console.log(`Cache Miss: ${cacheKey}`);
  }

  const body = await readRequestBody(req);
  const headers = { ...req.headers };
  delete headers.host;
  headers["content-length"] = String(body.length);

  const transport = targetUrl.protocol === "https:" ? https : http;
  const proxyReq = transport.request(
    targetUrl,
    {
      method: req.method,
      headers,
    },
    async (proxyRes) => {
      const chunks = [];
      proxyRes.on("data", (chunk) => chunks.push(chunk));
      proxyRes.on("end", async () => {
        const responseBody = Buffer.concat(chunks);
        const responseText = responseBody.toString("utf8");
        const responseHeaders = { ...proxyRes.headers, "x-cache": "MISS" };

        if (req.method === "GET" && !hasAuthorization && proxyRes.statusCode >= 200 && proxyRes.statusCode < 300) {
          await setCached(cacheKey, responseText);
        }

        if (["POST", "PUT", "DELETE"].includes(req.method) && incomingUrl.pathname.startsWith("/api/users")) {
          await invalidateUsersCache();
        }

        send(res, proxyRes.statusCode || 502, responseBody, responseHeaders);
      });
    }
  );

  proxyReq.on("error", (error) => {
    send(res, 502, JSON.stringify({ error: "Bad gateway", detail: error.message }), {
      "content-type": "application/json",
    });
  });

  proxyReq.end(body);
}

http.createServer(proxy).listen(PORT, () => {
  console.log(`API Gateway listening on :${PORT}`);
  console.log(`JSON -> ${JSON_SERVICE_URL}`);
  console.log(`SOAP -> ${SOAP_SERVICE_URL}`);
  console.log(`Files -> ${FILE_SERVICE_URL}`);
});
