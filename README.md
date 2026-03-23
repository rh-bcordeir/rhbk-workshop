# RHBK Workshop

A hands-on workshop that demonstrates **Keycloak-based authentication and authorization** on **OpenShift** using **Red Hat build of Keycloak (RHBK) 26.4**. It includes a secured REST API, a Next.js frontend, infrastructure manifests, and a custom Keycloak container image with a branded login theme.

## Architecture

```
┌──────────────────┐       OIDC        ┌──────────────────────┐
│  movies-frontend │◄─────────────────►│   Red Hat Keycloak   │
│  (Next.js 16)    │                   │   (RHBK 26.4)        │
└────────┬─────────┘                   └──────────┬───────────┘
         │ Bearer token                           │
         ▼                                        │ OIDC token
┌─────────────────┐                               │ validation
│   ms-movies     │◄──────────────────────────────┘
│  (Quarkus 3.32) │
└─────────────────┘
```

- **movies-frontend** authenticates users via Keycloak (OIDC) and proxies API calls to the backend with the access token.
- **ms-movies** validates the bearer token against Keycloak and enforces role-based access (`admin` role required to create movies).

## Repository Structure

```
rhbk-workshop/
├── infra/                  # OpenShift / Kubernetes manifests
│   ├── 1-postgres.yaml     #   PostgreSQL 15 StatefulSet + Secret
│   ├── 2-keycloak-svc-route.yaml  #   Service + Route for Keycloak
│   └── 3-keycloak.yaml     #   Keycloak CR (Operator-managed)
├── ms-movies/              # Quarkus REST API (Java 21)
├── movies-frontend/        # Next.js 16 frontend (TypeScript, Tailwind CSS)
├── rhbk-image/             # Custom Keycloak container image + login theme
│   ├── Containerfile
│   └── themes/redhat/      #   Custom Red Hat branded login theme
└── rhbk-26.4.10/           # RHBK server distribution (git-ignored)
```

## Components

### ms-movies (Backend)

A Quarkus 3.32 REST API secured with OIDC bearer token authentication.

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| `GET` | `/movies` | `@Authenticated` | List all movies |
| `POST` | `/movies` | `@RolesAllowed("admin")` | Create a new movie |

**Movie schema:**

```json
{
  "title": "The Matrix",
  "director": "Wachowski Sisters",
  "year": 1999
}
```

**Running locally:**

```bash
cd ms-movies
./mvnw quarkus:dev
```

The API starts on `http://localhost:8080`. Configure Keycloak connection via environment variables or the `.env` file:

| Variable | Default |
|----------|---------|
| `KEYCLOAK_SERVER_URL` | `keycloak-custom-service.rhbk.svc.cluster.local:8443` |
| `KEYCLOAK_CLIENT_ID` | `ms-movies` |

### movies-frontend (Frontend)

A Next.js 16 application using Auth.js (NextAuth v5) with the Keycloak provider. It proxies backend API calls through server-side route handlers so the access token never reaches the browser.

**Running locally:**

```bash
cd movies-frontend
npm install
npm run dev
```

The app starts on `http://localhost:3000`.

**Environment variables** (`.env.local`):

| Variable | Description |
|----------|-------------|
| `NEXTAUTH_URL` | Application URL (e.g. `http://localhost:3000`) |
| `NEXTAUTH_SECRET` | Secret for encrypting session cookies |
| `KEYCLOAK_CLIENT_ID` | OIDC client ID (e.g. `movies-frontend`) |
| `KEYCLOAK_CLIENT_SECRET` | OIDC client secret (confidential clients only) |
| `KEYCLOAK_ISSUER` | Keycloak realm URL (e.g. `https://<host>/realms/workshop`) |
| `MS_MOVIES_URL` | Backend URL (e.g. `http://localhost:8080`) |
| `KEYCLOAK_TLS_INSECURE` | Set to `true` to accept self-signed certs (dev only) |

### rhbk-image (Custom Keycloak Image)

A multi-stage container build based on `registry.redhat.io/rhbk/keycloak-rhel9:26.4` that pre-builds the Keycloak server with PostgreSQL support and includes a custom Red Hat branded login theme with English and Brazilian Portuguese translations.

**Building the image:**

```bash
cd rhbk-image
podman build -t keycloak-custom:26.4 .
```

### infra (OpenShift Manifests)

Deploy in order:

```bash
oc apply -f infra/1-postgres.yaml
oc apply -f infra/2-keycloak-svc-route.yaml
oc apply -f infra/3-keycloak.yaml
```

This creates:
1. A PostgreSQL 15 database for Keycloak
2. A Service and Route exposing Keycloak with TLS reencrypt
3. A Keycloak instance managed by the Keycloak Operator

## Keycloak Configuration

After deploying Keycloak, create the following in the admin console:

### Realm

Create a realm named **`workshop`**.

### Clients

| Client ID | Type | Purpose |
|-----------|------|---------|
| `ms-movies` | Bearer-only / Service | Backend API token validation |
| `movies-frontend` | Confidential (OpenID Connect) | Frontend OIDC login |

For the `movies-frontend` client:
- **Client authentication**: ON
- **Valid Redirect URIs**: `http://localhost:3000/api/auth/callback/keycloak`
- **Web Origins**: `http://localhost:3000`

### Roles

Create a realm role named **`admin`** and assign it to users who should be allowed to create movies.

### Users

Create at least one user with a password. Assign the `admin` role to test movie creation.

## Prerequisites

- **Java 21** and **Maven** (for ms-movies)
- **Node.js 20+** and **npm** (for movies-frontend)
- **Podman** or **Docker** (for building the Keycloak image)
- **OpenShift cluster** with the **Keycloak Operator** installed (for deployment)
- Access to the **Red Hat container registry** (`registry.redhat.io`) for the RHBK base image

## License

This project is intended for workshop and educational purposes.
