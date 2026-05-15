# service_json_soap

Small SOA lab workspace:

- `user-soap-service`: SOAP authentication service.
- `user-json-service`: REST profile service with DigitalOcean Spaces image upload.
- `frontend-app`: static frontend that talks only to the API Gateway.
- `api-gateway`: Lab 08 gateway with Redis caching.

See `DEPLOYMENT.md` for DigitalOcean environment variables, firewall shape, and run commands.
