package ph.edu.cspb.form137.model;

public class Form137Request {
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
    private String requesterName;
    private String relationshipToLearner;
    private String emailAddress;
    private String mobileNumber;

    // getters and setters
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
    public String getRequesterName() { return requesterName; }
    public void setRequesterName(String requesterName) { this.requesterName = requesterName; }
    public String getRelationshipToLearner() { return relationshipToLearner; }
    public void setRelationshipToLearner(String relationshipToLearner) { this.relationshipToLearner = relationshipToLearner; }
    public String getEmailAddress() { return emailAddress; }
    public void setEmailAddress(String emailAddress) { this.emailAddress = emailAddress; }
    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }
}
