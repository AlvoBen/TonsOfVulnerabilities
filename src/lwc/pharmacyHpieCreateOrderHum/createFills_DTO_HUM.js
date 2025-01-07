export class createFills_DTO_HUM {
    constructor(userId, organizationPatientId, organization, prescriptions) {
        this.sourceApplication = 'CRM', 
        this.userId = userId,
        this.requestTime = new Date().toISOString(), 
        this.organizationPatientId = organizationPatientId,
        this.organization = organization, 
        this.Fills = new fills_DTO_HUM(prescriptions);
    }
}
    class fills_DTO_HUM{
        constructor(prescriptions) { 
            let tmp = [];
            prescriptions.forEach(p => {  
            tmp.push(new fill_Line_DTO_HUM(p)); 
        })
            return tmp;
        }
    }

    class fill_Line_DTO_HUM{
        constructor(prescription) {
            this.prescriptionKey =prescription.key;
        }
    }