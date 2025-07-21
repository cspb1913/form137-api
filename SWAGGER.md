# Form 137 API Swagger Documentation

This API provides Swagger/OpenAPI documentation accessible at `/api/swagger`.

## Accessing the Swagger UI

Once the application is running, you can access the Swagger documentation at:

- **Swagger UI**: `http://localhost:8080/api/swagger`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

## API Endpoints

The following API endpoints are documented:

### Form 137 Requests
- `POST /api/form137/submit` - Submit a new Form 137 transcript request
- `GET /api/form137/status/{ticketNumber}` - Get status of a request by ticket number

### Health Check
- `GET /api/health/liveness` - Check if the application is running
- `GET /api/health/readiness` - Check if the application is ready to serve requests

### Dashboard (Requires Authentication)
- `GET /api/dashboard/requests` - List all requests with statistics
- `GET /api/dashboard/request/{id}` - Get specific request details
- `POST /api/dashboard/request/{id}/comment` - Add comment to a request

## Authentication

The dashboard endpoints require JWT authentication. The swagger endpoints (`/api/swagger`, `/v3/api-docs`, `/swagger-ui/**`) are excluded from authentication and are publicly accessible.

## Configuration

The Swagger configuration is defined in:
- `src/main/java/ph/edu/cspb/form137/config/OpenApiConfig.java`
- `src/main/resources/application.properties` (swagger path configuration)
- `src/main/java/ph/edu/cspb/form137/config/SecurityConfig.java` (security exclusions)