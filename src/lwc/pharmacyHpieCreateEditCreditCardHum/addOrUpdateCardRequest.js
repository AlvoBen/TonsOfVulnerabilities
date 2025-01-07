/*
LWC Name        : addOrUpdateCardRequest.js
Function        : helper file for credit card functionalities.
Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Jonathan Dickinson			  09/04/2023					User Story 4999697: T1PRJ0870026 MF27456 HPIE/CRM - SF Tech- Mail Order Management - Pharmacy - Finance tab - Lightning - Edit Credit Card, One time payment
****************************************************************************************************************************/
export class addOrUpdateCardRequest {
    constructor(organizationPatientId, userId, organization, cardKey, cardTypeCode, cardTypeDesc, expMonth, expYear, firstName, middleName, lastName, active, autoCharge, spendingAccount, tokenKey) {
        this.userId = userId;
        this.requestTime = new Date().toISOString();
        this.organization = organization;
        this.organizationPatientId = organizationPatientId;
        this.cardKey = cardKey;
        this.cardTypeCode = cardTypeCode;
        this.cardTypeDesc = cardTypeDesc;
        this.expMonth = expMonth;
        this.expYear = expYear;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.active = active;
        this.autoCharge = autoCharge;
        this.spendingAccount = spendingAccount;
        this.tokenKey = tokenKey;
    }
}