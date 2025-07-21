package ph.edu.cspb.form137.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * MongoDB entity representing a Form 137 request submission.
 * <p>
 * Comments are now stored in a separate collection (form137-comments) and linked
 * to this entity via the request ID.
 * </p>
 * <p>
 * The connection details are provided via the {@code SPRING_DATA_MONGODB_URI}
 * and {@code SPRING_DATA_MONGODB_DATABASE} environment variables.
 * </p>
 */
@Document(collection = "form137_requests")
public class Form137Request {

    @Id
    private String id;
    private String ticketNumber;
    private String status;
    private String submittedAt;
    private String updatedAt;
    private String notes;
    // Comments are now stored in separate collection (form137-comments)

    private String learnerReferenceNumber;
    private String firstName;
    private String middleName;
    private String lastName;
    private String dateOfBirth;
    private String lastGradeLevel;
    private String lastSchoolYear;
    private String previousSchool;
    private String purposeOfRequest;
    private String deliveryMethod;
    private String estimatedCompletion;
    private String requestType;
    private String learnerName;
    private String requesterName;
    private String relationshipToLearner;
    @JsonProperty("emailAddress")
    private String requesterEmail;
    private String mobileNumber;

    // getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTicketNumber() { return ticketNumber; }
    public void setTicketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(String submittedAt) { this.submittedAt = submittedAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    // Comments are managed in separate collection via CommentRepository
    
    public String getLearnerReferenceNumber() { return learnerReferenceNumber; }
    public void setLearnerReferenceNumber(String learnerReferenceNumber) { this.learnerReferenceNumber = learnerReferenceNumber; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getLastGradeLevel() { return lastGradeLevel; }
    public void setLastGradeLevel(String lastGradeLevel) { this.lastGradeLevel = lastGradeLevel; }
    public String getLastSchoolYear() { return lastSchoolYear; }
    public void setLastSchoolYear(String lastSchoolYear) { this.lastSchoolYear = lastSchoolYear; }
    public String getPreviousSchool() { return previousSchool; }
    public void setPreviousSchool(String previousSchool) { this.previousSchool = previousSchool; }
    public String getPurposeOfRequest() { return purposeOfRequest; }
    public void setPurposeOfRequest(String purposeOfRequest) { this.purposeOfRequest = purposeOfRequest; }
    public String getDeliveryMethod() { return deliveryMethod; }
    public void setDeliveryMethod(String deliveryMethod) { this.deliveryMethod = deliveryMethod; }
    public String getEstimatedCompletion() { return estimatedCompletion; }
    public void setEstimatedCompletion(String estimatedCompletion) { this.estimatedCompletion = estimatedCompletion; }
    public String getRequestType() { return requestType; }
    public void setRequestType(String requestType) { this.requestType = requestType; }
    public String getLearnerName() { return learnerName; }
    public void setLearnerName(String learnerName) { this.learnerName = learnerName; }
    public String getRequesterName() { return requesterName; }
    public void setRequesterName(String requesterName) { this.requesterName = requesterName; }
    public String getRelationshipToLearner() { return relationshipToLearner; }
    public void setRelationshipToLearner(String relationshipToLearner) { this.relationshipToLearner = relationshipToLearner; }
    public String getRequesterEmail() { return requesterEmail; }
    public void setRequesterEmail(String requesterEmail) { this.requesterEmail = requesterEmail; }
    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }
}
