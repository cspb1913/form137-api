# PATCH /api/requests/:id/status Endpoint Implementation

## Issue Summary
The frontend was calling `PATCH /api/requests/{id}/status` but this endpoint did not exist in the API, causing 4xx errors and preventing status updates from Processing to Completed.

## Solution Implemented

### 1. Created New Controller
- **File**: `src/main/java/ph/edu/cspb/form137/controller/RequestController.java`
- **Endpoint**: `PATCH /api/requests/{id}/status`
- **Authentication**: Required (Bearer token)

### 2. Status Validation
- **Valid statuses**: `PENDING`, `PROCESSING`, `COMPLETED`, `REJECTED`, `CANCELLED`
- **Input normalization**: Converts to uppercase for validation
- **Required field check**: Validates status field is present

### 3. Response Codes
- **200 OK**: Status updated successfully
- **404 Not Found**: Request ID doesn't exist
- **422 Unprocessable Entity**: Invalid status value or missing status field
- **500 Internal Server Error**: Database/server errors

### 4. Audit Logging
- Creates system comment for each status change
- Records old status → new status transition
- Timestamp and admin attribution

### 5. Database Updates
- Updates `status` field in Form137Request
- Sets `updatedAt` timestamp
- Persists audit comment in separate collection

## API Usage

### Request
```bash
PATCH /api/requests/{id}/status
Authorization: Bearer <JWT>
Content-Type: application/json

{
  "status": "COMPLETED"
}
```

### Success Response (200 OK)
```json
{
  "id": "507f1f77bcf86cd799439011",
  "status": "COMPLETED", 
  "updatedAt": "2024-01-01T14:00:00Z",
  "message": "Status updated successfully"
}
```

### Error Response (422)
```json
{
  "error": "Status update failed.",
  "message": "Invalid status. Allowed values: PENDING, PROCESSING, COMPLETED, REJECTED, CANCELLED"
}
```

## Security
- Requires valid JWT Bearer token in Authorization header
- Only authenticated admin users can update status
- Input validation prevents injection attacks

## Testing
- All existing tests continue to pass (21/21)
- Code compiles successfully with Java 21
- OpenAPI/Swagger documentation included
- Test script provided for manual verification

## Compatibility
- Maintains backward compatibility
- No changes to existing endpoints
- Uses existing Form137Request and Comment models
- Follows existing code patterns and style

The endpoint now matches exactly what the frontend expects and should resolve the 4xx errors when updating request status from Processing to Completed.