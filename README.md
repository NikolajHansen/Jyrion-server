# Jyrion-server

The Java-based Lyrion server alternative.

## Project structure

```
jyrion-server/
├── pom.xml          # Maven parent (multi-module)
├── server/          # Spring Boot 3 / Java 21 backend
│   └── src/main/java/com/jyrion/server/
│       ├── JyrionServerApplication.java
│       └── controller/
│           ├── HealthController.java   # GET /api/health
│           └── SpaController.java     # SPA fallback → /index.html
└── ui-react/        # React 18 + Vite frontend
    └── src/
        ├── api/apiSlice.ts   # RTK Query endpoints
        ├── store/            # Redux Toolkit store + uiSlice
        ├── App.tsx
        └── main.tsx
```

## Prerequisites

| Tool | Version |
|------|---------|
| JDK  | 21      |
| Maven | 3.9+  |
| Node | 22 LTS *(only needed for UI dev mode — Maven installs it automatically for the full build)* |
| npm  | 10+     |

## Production build (Maven builds everything)

```bash
mvn -B verify
```

Maven will:
1. Use **frontend-maven-plugin** to install Node 22 locally and run `npm ci` + `npm run build` in `ui-react/`.
2. Copy the Vite `dist/` output into `server/target/classes/static/`.
3. Compile and test the Spring Boot module.
4. Produce a runnable JAR at `server/target/jyrion-server-0.0.1-SNAPSHOT.jar`.

Run the production JAR:

```bash
java -jar server/target/jyrion-server-0.0.1-SNAPSHOT.jar
# Open http://localhost:8080
```

The React UI is served at `/` and all client-side routes are forwarded to `/index.html` (SPA fallback). The `/api/*` namespace is reserved for backend endpoints.

## Development mode (hot-reload)

Run the Spring Boot backend and the Vite dev server in separate terminals:

```bash
# Terminal 1 — Spring Boot backend (auto-restart on class changes)
cd server
mvn spring-boot:run

# Terminal 2 — Vite dev server (HMR)
cd ui-react
npm install          # first time only
npm run dev
# Open http://localhost:5173
```

Vite is pre-configured to proxy `/api/*` requests to `http://localhost:8080`, so the UI and API work together without CORS issues.

## API endpoints

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/health` | Returns `{"status":"UP","timestamp":"..."}` |

More endpoints will be added as features are implemented.

## CI

GitHub Actions runs `mvn -B verify` on every push and pull request. See [`.github/workflows/ci.yml`](.github/workflows/ci.yml).

## License

Apache-2.0 — see [LICENSE](LICENSE).

