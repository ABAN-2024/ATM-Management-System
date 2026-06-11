# Deploying ATM backend (Render) and frontend (Vercel)

## Backend (Render) - Docker

1. Push this repo to your Git provider and connect it on Render.
2. In Render, create a new service -> Web Service -> Connect repo.
3. Choose 'Docker' as the environment and point to the `Dockerfile` in the repo root.
4. Build command: `mvn -DskipTests package`
5. Start command: `java -Dserver.port=$PORT -jar /app/app.jar`
6. Set the environment variables in the Render dashboard (Environment):
   - `ATM_DATASOURCE_URL`
   - `ATM_DATASOURCE_USERNAME`
   - `ATM_DATASOURCE_PASSWORD`
7. Deploy and check logs.

## Frontend (Vercel)

1. Connect the repo/project to Vercel and set the root to the `frontend` folder.
2. In Project Settings -> Environment Variables set:
   - `BACKEND_URL` = `https://<your-render-service>` (Production)
3. Deploy and verify the frontend calls the backend.

## Notes

- Ensure `spring.jpa.hibernate.ddl-auto=validate` in production.
- Use TLS/SSL for DB connections.
- Use platform secrets rather than committing `.env`.
