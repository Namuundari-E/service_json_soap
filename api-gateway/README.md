# SOA API Gateway

Dependency-free Node.js gateway for Lab 08.

## Routes

- `/api/users/**` proxies to `JSON_SERVICE_URL`.
- `/api/files/**` proxies to `FILE_SERVICE_URL`.
- `/api/soap` proxies to `SOAP_SERVICE_URL/ws`.

`GET` responses are cached in Redis for `CACHE_TTL_SECONDS`, default `60`.

## Run

```bash
npm start
```

Useful environment variables:

```env
PORT=3000
JSON_SERVICE_URL=http://localhost:8082
SOAP_SERVICE_URL=http://localhost:8081
FILE_SERVICE_URL=http://localhost:8082
REDIS_HOST=127.0.0.1
REDIS_PORT=6379
CACHE_TTL_SECONDS=60
```
