# API Architecture & Endpoint Reference

> [!NOTE]
> **Swagger is the Source of Truth**: This document serves as a high-level architectural overview of the API for design evaluation. For exact schemas, payload attributes, and complete endpoint parameters, please refer to the interactive [Swagger UI](https://api.genesis-app.uk/swagger-ui/index.html) or the OpenAPI spec at `/v3/api-docs`.

---

## 1. API Overview

The Genesis API is organized into five major functional domains:
- **Authentication**: JWT-based user lifecycle and session security.
- **Projects**: Core workspace management, metadata database tracking, and preview provisioning.
- **AI Chat**: Interactive agent code generation with streamed responses.
- **Workspace Files**: Real-time virtual file tree traversal and source content reads.
- **Billing**: Stripe subscriptions, Webhook event processing, and Checkout routing.

To deliver an interactive, responsive code editor experience, AI response generation is streamed from the backend to the client browser in real-time using **Server-Sent Events (SSE)**.

---

## 2. JWT Authentication

Genesis uses stateless JWT (JSON Web Tokens) to secure resource access:
- **Token Issuance**: The token is generated and returned to the client upon successful email/password verification at the `/api/auth/login` endpoint.
- **Header Structure**: The client must include the JWT in the `Authorization` header of all subsequent API requests.

### Authentication Header Example
```http
Authorization: Bearer <your_jwt_token_here>
```

---

## 3. Core API Domains

### Authentication (`/api/auth`)
Handles stateless session access.
- `POST /api/auth/signup` — Registers a new user account and creates their record in PostgreSQL.
- `POST /api/auth/login` — Verifies user credentials and returns a JWT token.

### Projects & Live Previews (`/api/projects`)
Orchestrates project workspaces and claims Kubernetes preview environments.
- `GET /api/projects` — Fetches a list of projects owned by or shared with the authenticated user.
- `POST /api/projects` — Scaffolds a new project workspace by seeding a React template from S3 storage.
- `POST /api/projects/{id}/deploy` — Claims an idle runner pod, synchronizes files from object storage, starts the Vite preview server, and registers routing mappings in Redis.
- `POST /api/projects/{id}/stop` — Releases the claimed runner pod back to the idle pool and clears active routing maps in Redis.
- `POST /api/projects/{id}/heartbeat` — Refreshes the last-active timestamp for the project's runner pod to prevent garbage collection/reaping.

### AI Chat Agent (`/api/chat`)
Streams real-time prompt generation using Spring AI.
- `POST /api/chat/stream` — Initiates an SSE channel to stream incremental AI-generated code snippets and message responses.
- `GET /api/chat/{projectId}` — Retrieves the historical log of prompt/response interactions for a specific project.

### Workspace Files (`/api/projects/{id}/files`)
Exposes virtual workspace contents for the client code editor.
- `GET /api/projects/{id}/files` — Returns a hierarchical JSON structure representing the directory file tree.
- `GET /api/projects/{id}/files/content?path=...` — Reads and returns the raw file contents of a specific file inside the workspace.

### Billing & Stripe (`/api/plans`, `/api/payments`)
Connects subscription structures to checkout endpoints.
- `GET /api/plans` — Lists subscription plan features and price metadata.
- `POST /api/payments/checkout` — Generates a Stripe Checkout redirect URL for subscription upgrades.
- `POST /api/payments/portal` — Generates a Stripe Customer Portal link for subscription management.
- `POST /api/payments/webhook` — Asynchronously processes Stripe events (payment success, subscription updates, or cancellations).

---

## 4. Server-Sent Events (SSE) Streaming

### Why Streaming?
Generating code files using LLMs takes significant time (often 10–30 seconds for a complete response). Standard blocking HTTP requests would result in a poor user experience, leading to timeouts or browser freeze. SSE enables the server to stream code changes token-by-token as they are generated, providing immediate visual feedback to the user.

### Consumer Pattern
Clients establish the stream connection by sending a `POST` request with an `Accept: text/event-stream` header. The server holds the request open and pushes sequential events formatted as:
```text
data: {"content":"import React from 'react';","type":"CONTENT"}
```
The stream is terminated when the server sends a final `[DONE]` signal or closes the connection.

---

## 5. Architectural Endpoint Examples

### Login (`POST /api/auth/login`)
**Request Payload:**
```json
{
  "email": "developer@example.com",
  "password": "securepassword123"
}
```

**Response Payload (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJkZXZlbG9wZXJAZXhhbXBsZS5jb20iLCJpYXQiOjE2NzI1MzExOTksImV4cCI6MTY3MjYxNzU5OX0.abc123xyz...",
  "user": {
    "id": 1,
    "email": "developer@example.com",
    "name": "Jane Developer"
  }
}
```

### Initiate Chat Stream (`POST /api/chat/stream`)
**Request Headers:**
```http
Accept: text/event-stream
Content-Type: application/json
```
**Request Payload:**
```json
{
  "projectId": 12,
  "message": "Add a modern dark mode button to the navbar"
}
```

**Streamed Response Chunk (Example):**
```text
event: message
data: {"chunk": "const ThemeToggle = () => {", "finishReason": "none"}

event: message
data: {"chunk": "  const [dark, setDark] = useState(false);", "finishReason": "none"}
```

### Claim Live Preview (`POST /api/projects/{id}/deploy`)
**Response Payload (200 OK):**
```json
{
  "projectId": 12,
  "status": "DEPLOYED",
  "previewUrl": "https://project-12.genesis-app.uk",
  "assignedPodIp": "10.244.2.14"
}
```

---

## 6. HTTP Status Codes

| Code | Status | Description / Genesis Context |
|---|---|---|
| **200** | OK | Request succeeded. Returned on successful CRUD, preview allocation, and session token issuance. |
| **401** | Unauthorized | Authentication header is missing, malformed, or the JWT token has expired. |
| **403** | Forbidden | User is authenticated but does not have the required role/permissions for the project (e.g., a VIEWER trying to delete files). |
| **404** | Not Found | The requested resource (project, file, user) does not exist in the database or storage. |
