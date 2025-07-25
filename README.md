# Form137 API

This project provides mock endpoints for the Form 137 system. By default it runs without authentication.

## Architecture

The API uses a MongoDB database with separate collections for better scalability:

- `form137_requests` - Main request documents
- `form137_comments` - Comments linked to requests via `requestId`

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

## Deploying to Kubernetes

### Using Helm Charts (Recommended)

The application now includes Helm charts for easier deployment and configuration management.

#### Prerequisites
- Helm 3.x installed
- Access to a Kubernetes cluster
- kubectl configured for your cluster

#### Quick Start

1. **Clone the repository**:
   ```bash
   git clone https://github.com/jasoncalalang/form137-api.git
   cd form137-api
   ```

2. **Configure your values**:
   ```bash
   cp form137-api/values-example.yaml form137-api/values-prod.yaml
   # Edit values-prod.yaml with your specific configuration
   ```

3. **Create secrets for MongoDB**:
   ```bash
   # Option 1: Using kubectl directly
   kubectl create secret generic form137-api-secret \
     --from-literal=MONGO_URI="your-mongodb-connection-string"
   
   # Option 2: Or configure in values-prod.yaml (base64 encoded)
   echo -n "your-mongodb-connection-string" | base64
   ```

4. **Deploy with Helm**:
   ```bash
   # Deploy to default namespace
   helm install form137-api ./form137-api -f form137-api/values-prod.yaml
   
   # Or deploy to specific namespace
   helm install form137-api ./form137-api -n production -f form137-api/values-prod.yaml --create-namespace
   ```

#### Helm Commands

```bash
# Check deployment status
helm status form137-api

# Upgrade deployment
helm upgrade form137-api ./form137-api -f form137-api/values-prod.yaml

# Rollback to previous version
helm rollback form137-api

# Uninstall
helm uninstall form137-api

# View all values
helm get values form137-api

# Test the deployment
helm test form137-api
```

#### Configuration

Key configuration values in `values.yaml`:

- `replicaCount`: Number of pod replicas
- `image.repository` and `image.tag`: Container image details
- `ingress.hosts`: Your domain configuration
- `app.mongodb.database`: MongoDB database name
- `app.auth0.issuerUri` and `app.auth0.audience`: Auth0 configuration
- `secret.data.MONGO_URI`: Base64 encoded MongoDB connection string

### Using Raw Kubernetes Manifests (Legacy)

Kubernetes manifests are still available in the `k8s` directory for reference:

```bash
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
kubectl apply -f k8s/ingress.yaml
```

Note: You'll need to create the required ConfigMap and Secret manually when using raw manifests.
 