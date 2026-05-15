# Lab 07 and Lab 08 Deployment Notes

## Services

- `user-soap-service`: SOAP auth service. Runs on `PORT` or `8081`.
- `user-json-service`: REST profile and image upload service. Runs on `PORT` or `8082`.
- `frontend-app`: static frontend. Sends all requests to the API Gateway.
- `api-gateway`: Node.js gateway for Lab 08. Proxies `/api/users/**` and `/api/soap`, and caches `GET` responses in Redis.

## Lab 07 Environment Variables

Set these in DigitalOcean App Platform for `user-json-service`.

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://YOUR_DB_HOST:25060/defaultdb
SPRING_DATASOURCE_USERNAME=YOUR_DB_USERNAME
SPRING_DATASOURCE_PASSWORD=YOUR_DB_PASSWORD
S3_ACCESS_KEY=YOUR_SPACES_ACCESS_KEY
S3_SECRET_KEY=YOUR_SPACES_SECRET_KEY
S3_ENDPOINT_URL=https://sgp1.digitaloceanspaces.com
S3_REGION=sgp1
S3_BUCKET_NAME=YOUR_BUCKET
S3_PUBLIC_URL=https://YOUR_BUCKET.sgp1.digitaloceanspaces.com
SOAP_SERVICE_URL=http://SOAP_PRIVATE_OR_PUBLIC_HOST:8081/ws
```

For `user-soap-service`, only `PORT` is required unless you add persistent auth storage later.

```env
PORT=8081
```

## Lab 08 Gateway Environment Variables

Run Redis on the Gateway Droplet:

```bash
sudo apt update
sudo apt install redis-server
redis-cli ping
```

Run the gateway:

```bash
cd api-gateway
JSON_SERVICE_URL=http://JSON_PRIVATE_IP:8082 \
SOAP_SERVICE_URL=http://SOAP_PRIVATE_IP:8081 \
FILE_SERVICE_URL=http://JSON_PRIVATE_IP:8082 \
REDIS_HOST=127.0.0.1 \
REDIS_PORT=6379 \
PORT=3000 \
npm start
```

Expected gateway paths:

- `POST /api/soap` -> SOAP service `/ws`
- `GET /api/users/me` -> JSON service `/users/me`
- `POST /api/users/me/image` -> JSON service `/users/me/image`

Gateway logs print `Cache Hit` and `Cache Miss` for `GET` requests. Use those logs for the Lab 08 report screenshot.

## Frontend Gateway URL

Before deploying `frontend-app` as a static site, set the gateway base URL in `index.html`:

```js
const API_BASE = window.API_BASE_URL || "https://hammerhead-app-fqp4n.ondigitalocean.app";
```

After this, the frontend should not call JSON or SOAP services directly.

## DigitalOcean Firewall Shape

- Gateway Droplet: allow public inbound `3000` or `80/443`.
- SOAP/JSON/File services: allow inbound service ports only from the Gateway Droplet private IP or gateway tag.
- Redis: keep local only on the Gateway Droplet, `127.0.0.1:6379`.
