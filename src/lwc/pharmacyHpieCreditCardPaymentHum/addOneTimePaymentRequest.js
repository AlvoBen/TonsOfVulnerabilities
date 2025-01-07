/*
LWC Name        : addOneTimePaymentRequest.js
Function        : helper file for credit card functionalities.
Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Jonathan Dickinson			  09/04/2023					User Story 4999697: T1PRJ0870026 MF27456 HPIE/CRM - SF Tech- Mail Order Management - Pharmacy - Finance tab - Lightning - Edit Credit Card, One time payment
****************************************************************************************************************************/

export class addOneTimePaymentRequest {
    constructor(organizationPatientId, userId, organization, key, amount, date, authorized) {
        this.organizationPatientId = organizationPatientId;
        this.organization = organization;
        this.requestTime = new Date().toISOString();
        this.userId = userId;
        this.key = key;
        this.amount = amount;
        this.z0date = date;
        this.authorized = authorized;
    }
}