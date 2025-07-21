# Form137 API Swagger Documentation

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
- `GET /api/dashboard/requests` - List all requests with statistics and comments
- `GET /api/dashboard/request/{id}` - Get specific request details with comments
- `GET /api/dashboard/request/{id}/comments` - Get all comments for a specific request
- `POST /api/dashboard/request/{id}/comment` - Add comment to a request

## Comments Architecture

Comments are now stored in a separate MongoDB collection (`form137-comments`) instead of being embedded within the request documents. This provides better scalability and allows for more advanced comment management features.

- Comments are linked to requests via the `requestId` field
- When a new Form 137 request is submitted, an initial system comment is automatically created
- Dashboard endpoints fetch and merge comments from the separate collection
- API responses maintain backward compatibility with existing clients

## Authentication

The dashboard endpoints require JWT authentication. The swagger endpoints (`/api/swagger`, `/v3/api-docs`, `/swagger-ui/**`) are excluded from authentication and are publicly accessible.

## Sample API Calls

### Submit a Form 137 Request

```bash
curl -X POST http://localhost:8080/api/form137/submit \
  -H "Content-Type: application/json" \
  -d '{
    "learnerReferenceNumber": "123456789012",
    "firstName": "Juan",
    "middleName": "Santos",
    "lastName": "Dela Cruz",
    "dateOfBirth": "2000-01-01",
    "lastGradeLevel": "Grade 12",
    "lastSchoolYear": "2018-2019",
    "previousSchool": "CSPB High School",
    "purposeOfRequest": "College admission",
    "deliveryMethod": "Email",
    "requestType": "Original",
    "learnerName": "Juan Santos Dela Cruz",
    "requesterName": "Maria Dela Cruz",
    "relationshipToLearner": "Mother",
    "emailAddress": "maria@email.com",
    "mobileNumber": "09123456789"
  }'
```

### Get All Requests (Dashboard)

```bash
curl -X GET http://localhost:8080/api/dashboard/requests \
  -H "Authorization: Bearer your-jwt-token"
```

### Get Request Comments

```bash
curl -X GET http://localhost:8080/api/dashboard/request/{requestId}/comments \
  -H "Authorization: Bearer your-jwt-token"
```

## Configuration

The Swagger configuration is defined in:
- `src/main/java/ph/edu/cspb/form137/config/OpenApiConfig.java`
- `src/main/resources/application.properties` (swagger path configuration)
- `src/main/java/ph/edu/cspb/form137/config/SecurityConfig.java` (security exclusions)