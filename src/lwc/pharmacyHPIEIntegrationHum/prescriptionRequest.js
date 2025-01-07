export class PrescriptionReqeust {
    constructor(sourceApplication, userId, startDate, endDate, organization, organizationPatientId) {
        this.sourceApplication = sourceApplication;
        this.userId = userId;
        this.requestTime = new Date().toISOString();
        this.startDate = startDate;
        this.endDate = endDate;
        this.organization = organization;
        this.organizationPatientId = organizationPatientId;
    }
}