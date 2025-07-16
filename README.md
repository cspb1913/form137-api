# Form137 API

This project provides mock endpoints for the Form 137 system. By default it runs without authentication.

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
 