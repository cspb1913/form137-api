# Form137 API

This project provides mock endpoints for the Form 137 system. By default it runs without authentication.

## Architecture

The API uses a MongoDB database with separate collections for better scalability:

- `form137_requests` - Main request documents
- `form137-comments` - Comments linked to requests via `requestId`

Comments were moved to a separate collection to improve performance and provide better comment management capabilities. The API maintains backward compatibility by automatically fetching and merging comments in responses.

## API Endpoints

### Public Endpoints
- `POST /api/form137/submit` - Submit a new Form 137 transcript request (creates initial comment automatically)
- `GET /api/form137/status/{ticketNumber}` - Get status of a request by ticket number
- `GET /api/health/liveness` - Health check
- `GET /api/health/readiness` - Readiness check

### Dashboard Endpoints (Authentication Required)
- `GET /api/dashboard/requests` - List all requests with statistics and comments
- `GET /api/dashboard/request/{id}` - Get specific request details with comments  
- `GET /api/dashboard/request/{id}/comments` - Get all comments for a specific request
- `POST /api/dashboard/request/{id}/comment` - Add comment to a request

## Sample Usage

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

Expected Response:
```json
{
  "success": true,
  "ticketNumber": "REQ-1732201234567",
  "message": "Form 137 request submitted successfully",
  "submittedAt": "2024-01-01T10:00:00Z"
}
```

## Auth0 Integration

To secure the API with Auth0, enable the `auth` feature and configure the issuer URI.

Set the following properties (for example in environment variables or a `application.yml` file):

```
AUTH0_ISSUER_URI=https://YOUR_AUTH0_DOMAIN/
AUTH_ENABLED=true
```

### MongoDB Atlas

Configure the connection string for your Atlas cluster using the standard
Spring Boot properties:

```
SPRING_DATA_MONGODB_URI=mongodb+srv://<user>:<pass>@<cluster>.mongodb.net/form137?retryWrites=true&w=majority
SPRING_DATA_MONGODB_DATABASE=form137
```

When `auth.enabled=true`, all endpoints except `/api/health/**` require a valid JWT issued by Auth0.

For local development without authentication the default configuration keeps `auth.enabled=false`.

## Running

Use the provided Gradle tasks:

```
./gradlew bootRunDev         # runs with the dev profile
./gradlew test              # runs unit tests
```

## Deploying to IKS

Kubernetes manifests are provided in the `k8s` directory. Apply them to deploy the API
on IBM Cloud Kubernetes Service. An ingress configuration can be added separately
when needed.

```bash
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
```
 