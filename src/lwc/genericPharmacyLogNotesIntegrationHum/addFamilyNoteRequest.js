/*
LWC Name: genericPharmacyLogNotesIntegrationHum.html
Function: Generic Component for pharmacy log notes HPIE integration
Modification Log:
* Developer Name                  Date                         Description
* Jonathan Dickinson              02/29/2024                    Initial Version
****************************************************************************************************************************/
export class AddFamilyNote_DTO_HUM {
    constructor(userId, organizationPatientId, organization, accountId, notes) {
        this.userId = userId,
        this.organizationPatientId = organizationPatientId,
        this.organization = organization,
        this.accountId = accountId,
        this.notes = notes
    }
}