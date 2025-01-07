/*****************************************************************************************
Apex class Name    : Pharmacy_HPIEMockTest_LT_HUM
Version            : 1.0
Function           : Testing helper to Pharmacy_HPIE_LT_HUM.
Created Date       : 07/18/2023
Test Class         : Pharmacy_HPIE_LT_HUM
Modification Log: 
Developer Name           Code Review                       Date                       Description 
*--------------------------------------------------------------------------------------------------------------------------------
* Jonathan Dickinson                    				    02/29/2024                Initial version
****************************************************************************************************************/
public class Pharmacy_HPIEMockTest_LT_HUM implements HttpCalloutMock{
	public String jsonSuccessDemographics ='{"ShippingAddress":[{"addressLine1":"9995 CHAPMANS LANE","addressLine2":"","city":"CONWAY","stateCode":"SC","zipCode":"29526","active":true,"uspsValidated":true}],"PrimaryPhone":[{"number":"1000"}],"AlternatePhone":[{"number":"1000"}],"PermanentAddress":[{"addressLine1":"9995 CHAPMANS LANE","addressLine2":"","city":"CONWAY","stateCode":"SC","zipCode":"29526","active":true,"uspsValidated":true}],"Addresses":[{"type":{"code":"strin","description":"HOME"},"key":0,"addressLine1":"string","addressLine2":"string","city":"string","stateCode":"st","zipCode":"string","overrideReason":"string","overrideReasonCode":0,"uspsValidated":true,"active":true}]}';
    public string jsonSuccessFinance = '{"accountCreditLimit":200,"accountOutstandingBalance":41,"adjustments":[{"amount":0,"date":"","type":""}],"creditLimit":50300,"outstandingBalance":20.46,"paymentSchedules":[{"amount":5.4,"paymentType":{"code":"OT","description":""},"paymentDueDate":"2022-08-17","paymentDate":"2022-08-17","paymentStatus":"","creditCardKey":9234397554,"sourceApplication":"CRM"}],"paymentCards":[{"key":1365253,"type":{"code":"1","description":"SAT"},"expirationMonth":4,"expirationYear":2018,"firstName":"LZE","middleName":"","lastName":"BrOM","active":false,"autoCharge":false,"spendingAccount":false,"tokenKey":"4000418394"}]}';
    public string jsonSuccessProfile = '{"AccountId":"2368102","Email":"abc@gmail.com","HealthConditions":{"allergies":[{"key":0,"status":{"code":"s","description":"Active"},"description":"string","icdCode":"string","referenceNumber":"string","date":"2023-07-21T08:34:04.901Z"}],"healthConditions":[{"key":0,"status":{"code":"s","description":"Active"},"description":"string","icdCode":"string","referenceNumber":"string","date":"2023-07-21T08:34:04.901Z"}]},"FirstName":"LEE","DateofBirth":"1152-08-17","MiddleInitial":"R","LastName":"CHAEZ"}';
    public string jsonSuccessPreferences = '{"preference":{"capType":{"code":"s","description":"EASY"},"autoRefillEnabled":true,"useGenerics":true,"preferredPaymentMethod":"string","medicationNameInclusion":{"code":"string","status":true,"description":"string"},"emailDeclined":{"code":"string","status":true,"description":"string"},"consents":{"copayConsentRequired ":true,"memberConsentRequired":true,"copayConsentAmount":0,"consentBeginDate":"2023-07-21","consentEndDate":"2023-07-21"}}}';
    public string jsonSuccessPlans ='{ "Payor": [{ "name": "HMN PAYOR", "group": { "terminationDate": "2024-03-30", "number": "322852", "name": "HUMANA INSURANCE COMPANY", "effectiveDate": "2022-01-01" } }] }';
    public string jsonSuccessPrescriptions = '{ "value": { "prescriptions": [{ "key": "string", "writtenDate": "string", "createdDate": "string", "expirationDate": "2023-07-17", "status": { "code": 0, "description": "Profiled" }, "daysSupply": 0, "quantity": 0, "lastFillDate": "2023-07-17", "nextFillMinDate": "2023-07-17", "nextFillMaxDate": "2023-07-17", "numberOfRefills": 0, "refillsUsed": 0, "refillsRemaining": 0, "autoRefill": true, "autoRefillEligible": true, "futureFillEligible": true, "isExpiredRx": true, "isNewRx": true, "isReNewRx": true, "isRefillable": true, "isActive": true, "isRefillTooSoon": true, "isArchived": true, "isWorkRx": true, "isNonFillable": true, "copay": { "copayConsentGiven": true, "consentStatus": { "code": "A", "description": "string" }, "whoConsented": null, "consentDate": "2021-08-03", "lastCopayCost": { "amount": 0, "taxAmount": 0 } }, "consent": { "status": { "code": "strin", "description": "Consent Accepted" }, "date": "string", "userId": "string" }, "physician": { "firstName": "string", "lastName": "string", "phones": [{ "key": 0, "type": "string", "value": "string" }] }, "product": { "drug": { "type": { "code": "string", "description": "Over The Counter" }, "ndc": "string", "label": "string" } }, "fills": [{ "type": "string", "fillNumber": 0, "itemLabel": "String", "drug": { "type": { "code": "string", "description": "Over The Counter" }, "label": "string" } }] }] } }';
    public string jsonSuccessUpdatePrescriptions = '{ "identifier": { "organization": "string", "identity": { "patientId": 0, "organizationPatientId": "string", "memberId": "string", "personCode": "string" } }, "prescriptions": [{ "prescriptionKey": "string", "fillNumber": 0, "pharmacyNPI": "string" }] }';
    public string jsonSuccessAddPrescriptions = '{ "identifier": { "organization": "string", "identity": { "patientId": 0, "organizationPatientId": "string", "memberId": "string", "personCode": "string" } }, "prescriptions": [{ "prescriptionKey": "string", "fillNumber": 0, "pharmacyNPI": "string" }] }';
    public String jsonError ='{"errorMessage": "Internal server error","statusCode": "500-Internal server error"}';
    public String jsonSuccessUpdatePreference = '{ "identifier": { "organization": "HUMANA", "identity": { "memberId": "H19664005", "personCode": "00", "patientId": 672619, "organizationPatientId": "1012829915" } } }';
    public String jsonSuccessHealthHistory = '{ "HealthConditions": { "allergies": [{ "key": 0, "status": { "code": "s", "description": "Active" }, "description": "string", "icdCode": "string", "referenceNumber": "string", "date": "2023-07-21T08:34:04.901Z" }], "healthConditions": [{ "key": 0, "status": { "code": "s", "description": "Active" }, "description": "string", "icdCode": "string", "referenceNumber": "string", "date": "2023-07-21T08:34:04.901Z" }] } }';
    public String jsonSuccessAddFills = '{ "identifier": { "organization": "string", "identity": { "patientId": 0, "organizationPatientId": "string", "memberId": "string", "personCode": "string" } }, "fills": [{ "prescriptionKey": "string", "fillNumber": 0, "pharmacyNPI": "string" }], "transaction": { "id": "string", "requestTimeStamp": "2023-12-20T13:14:18.265Z", "responseTimeStamp": "2023-12-20T13:14:18.265Z", "duration": 0 } }';
    public String jsonSuccessGetOrderSummary = '{ "orders": [{ "orderId": "string", "pharmacyNPI": "string", "type": { "code": 0, "description": "Over The Counter" }, "patientId": 0, "createdDate": "2023-07-18T11:01:44.694Z", "releaseDate": "2023-07-18T11:01:44.694Z", "shippedDate": "2023-07-18T11:01:44.694Z", "cancelDate": "2023-07-18T11:01:44.694Z", "replaceDate": "2023-07-18T11:01:44.694Z", "returnDate": "2023-07-18T11:01:44.694Z", "status": { "code": 0, "description": "OPEN", "reason": { "code": 0, "description": "NO_LETTER OTC BENEFIT EXCEEDED" } }, "externalOrderId": "string", "source": "string", "lines": [{ "status": { "code": 0, "description": "OPEN", "reason": { "code": 0, "description": "NO_LETTER OTC BENEFIT EXCEEDED" } }, "product": { "identifiers": [{ "id": { "type": "NDC", "value": "string" } }], "quantity": 0, "name": "string", "prescription": { "prescriptionKey": "string", "fillNumber": 0 } }, "cost": { "planCost": { "amount": 0, "taxAmount": 0 }, "copayCost": { "amount": 0, "taxAmount": 0 } }, "changedOrder": { "type": "string", "orderId": "string", "reasonCode": 0, "reasonDescription": "string" } }], "billing": { "address": { "key": 0 }, "payment": { "method": { "code": 0, "description": "CASH" }, "date": "2023-07-18", "card": { "key": 0, "amount": 0 } }, "benefit": { "provider": "string", "form": { "code": 0, "description": "FL MAA" }, "period": 0 } }, "shipping": { "cost": { "amount": 0, "taxAmount": 0 }, "method": { "code": 0, "description": "string" }, "priority": true, "trackingNumber": "string", "shipperType": "string", "address": { "key": 0, "type": { "code": 0, "description": "HOME" } } } }] }';
    public String jsonSuccessGetOrderDetail = '{ "order": { "cancelDate": null, "pharmacyNPI": null, "orderId": "495862782", "patientId": 8619441, "releaseDate": null, "externalOrderId": null, "source": "AUTO REFILLER", "type": { "code": 29, "description": "REFILLRX" }, "billing": { "address": { "key": 20417990 }, "payment": { "date": null, "method": { "code": 2, "description": "PAYMENT CARD" }, "card": { "amount": null, "key": 12374197 } }, "benefit": { "period": null, "ppNum": null, "form": { "code": null, "description": null }, "provider": null } }, "returnDate": null, "createdDate": "2023-11-27 03:16:34.893", "shipping": { "shipperType": "N/A", "cost": { "amount": 0.0, "taxAmount": 0.0 }, "address": { "type": { "code": 11, "description": "SHIPPING" }, "key": 20417990 }, "method": { "code": 1, "description": "BEST METHOD-REGULAR MAIL" }, "priority": true, "trackingNumber": null }, "lines": [ { "product": { "quantity": 180, "prescription": { "fillNumber": 2, "prescriptionKey": "466802422" }, "identifiers": [ { "id": { "type": "NDC", "value": "00003089321" } } ], "name": "ELIQUIS 2.5MG TAB" }, "changedOrder": { "reasonDescription": null, "orderId": "495170792", "reasonCode": null, "type": "M" }, "cost": { "copayCost": { "amount": 0.0, "taxAmount": 0.0 }, "planCost": { "amount": 0.0, "taxAmount": 0.0 } }, "status": { "reason": { "code": null, "description": null }, "code": 6, "description": "SPLIT" } }, { "product": { "quantity": 180, "prescription": { "fillNumber": 2, "prescriptionKey": "466802394" }, "identifiers": [ { "id": { "type": "NDC", "value": "00078065920" } } ], "name": "ENTRESTO 24-26MG TAB" }, "changedOrder": { "reasonDescription": null, "orderId": "495170792", "reasonCode": null, "type": "M" }, "cost": { "copayCost": { "amount": 0.0, "taxAmount": 0.0 }, "planCost": { "amount": 0.0, "taxAmount": 0.0 } }, "status": { "reason": { "code": null, "description": null }, "code": 6, "description": "SPLIT" } }, { "product": { "quantity": 90, "prescription": { "fillNumber": 2, "prescriptionKey": "466802400" }, "identifiers": [ { "id": { "type": "NDC", "value": "16729004417" } } ], "name": "ATORVASTATIN 10MG TAB" }, "changedOrder": { "reasonDescription": null, "orderId": "495170792", "reasonCode": null, "type": "M" }, "cost": { "copayCost": { "amount": 0.0, "taxAmount": 0.0 }, "planCost": { "amount": 0.0, "taxAmount": 0.0 } }, "status": { "reason": { "code": null, "description": null }, "code": 6, "description": "SPLIT" } } ], "shippedDate": null, "replaceDate": "", "status": { "reason": { "code": null, "description": null }, "code": 20, "description": "CLOSED" } } }';
    public String jsonSuccessGetEvents = '{ "events": [{ "orderId": "string", "events": [{ "id": 0, "name": "string", "user": "string", "date": "2023-07-18T10:04:00.373Z", "reason": "string", "code": "string" }] }] }';
    public String jsonSuccessGetTasks = '{  "tasks": [{ "orderId": "string", "task": { "id": 0, "name": "string", "date": "2023-07-25T13:38:30.879Z", "reason": "string", "code": "string" }, "lines": [{ "product": { "identifiers": [{ "id": { "type": "NDC", "value": "string" } }], "quantity": 0, "name": "string", "prescriptionKey": "string" }, "task": { "id": 0, "name": "string", "date": "2023-07-25T13:38:30.879Z", "reason": "string", "code": "string" } }] }] }';
    public String jsonSuccessGetPatientLogNotes = '{ "PatientLogNotes": { "PatientLogNote": [{ "PatientLogNoteCode": "efg", "PatientLogNoteDate": "2023-07-19T10:21:39.487Z", "PatientLogNotePriority": "Y", "PatientLogNoteMessage": "Hello", "PatientLogNoteUser": "MXG0195" }] } }';
    public String jsonSuccessAddPatientLogNotes = '{ "OrganizationPatientId": "1019838720", "Success": true }';
    public String jsonSuccessGetFamilyLogNotes = '{ "FamilyLogNotes": { "FamilyLogNote": [{ "FamilyLogNoteCode": "efg", "FamilyLogNoteDate": "2023-07-19T10:21:39.487Z", "FamilyLogNotePriority": "Y", "FamilyLogNoteMessage": "Hello", "FamilyLogNoteUser": "MXG0195" }] } }';
    public String jsonSuccessAddFamilyLogNotes = '{ "OrganizationPatientId": "1019838720", "Success": true }';
    public String jsonSuccessUpdatePhone = '{ "identifier": { "organization": "HUMANA", "identity": { "memberId": "H19664005", "personCode": "00", "patientId": 672619, "organizationPatientId": "1012829915" } } }';
    public String jsonSuccessUpdateAlternatePhone = '{ "identifier": { "organization": "HUMANA", "identity": { "memberId": "H19664005", "personCode": "00", "patientId": 672619, "organizationPatientId": "1012829915" } } }';
    public String jsonSuccessUpdateProfile = '{ "identifier": { "organization": "string", "identity": { "organizationPatientId": "string", "memberId": "string", "personCode": "string", "patientId": 0 } } }';
    public String jsonSuccessAddFinance = '{ "identifier": { "organization": "string", "identity": { "organizationPatientId": "string", "memberId": "string", "personCode": "string", "patientId": 0 } }, "paymentCards": [{ "key": 0, "tokenKey": "strin" }] }';
    public String jsonSuccessUpdateFinance = '{ "identifier": { "identity": { "organizationPatientId": "1028244424", "patientId": 522, "personCode": "00", "memberId": "H33845367" }, "organization": "HUMANA" } }';
    public String jsonSuccessOneTimePayment = '{ "identifier": { "identity": { "organizationPatientId": "1028244424", "patientId": 522, "personCode": "00", "memberId": "H33845367" }, "organization": "HUMANA" } }';
    public String jsonSuccessAddAddress = '{ "identifier": { "organization": "String", "identity": { "memberId": "String", "personCode": "String", "patientId": 123, "organizationPatientId": "1091252368" } } }';
    public String jsonSuccessUpdateAddress = '{ "identifier": { "organization": "HUMANA", "identity": { "memberId": "H13056187", "personCode": "00", "patientId": 561, "organizationPatientId": "1022317433" } }, "addresses": [{ "key": 35740099, "type": { "code": "10", "description": "BILLING" } }] }';
    public String jsonSuccessUpdateOrder = '{ "identifier": { "organization": "string", "identity": { "patientId": 0, "organizationPatientId": "string", "memberId": "string", "personCode": "string" } }, "order": { "orderId": "string", "pharmacyNPI": "string" } }';
    public String jsonSuccessCreateOrder ='{ "value": { "order": { "orderId": "string", "pharmacyNPI": "string", "type": { "code": 0, "description": "Over The Counter" }, "patientId": 0, "createdDate": "2023-07-18T11:01:44.708Z", "releaseDate": "2023-07-18T11:01:44.708Z", "shippedDate": "2023-07-18T11:01:44.708Z", "cancelDate": "2023-07-18T11:01:44.708Z", "replaceDate": "2023-07-18T11:01:44.708Z", "returnDate": "2023-07-18T11:01:44.708Z", "status": { "code": 0, "description": "OPEN", "reason": { "code": 0, "description": "NO_LETTER OTC BENEFIT EXCEEDED" } }, "externalOrderId": "string", "source": "string", "lines": [{ "status": { "code": 0, "description": "OPEN", "reason": { "code": 0, "description": "NO_LETTER OTC BENEFIT EXCEEDED" } }, "product": { "identifiers": [{ "id": { "type": "NDC", "value": "string" } }], "quantity": 0, "name": "string", "prescription": { "prescriptionKey": "string", "fillNumber": 0 } }, "cost": { "planCost": { "amount": 0, "taxAmount": 0 }, "copayCost": { "amount": 0, "taxAmount": 0 } }, "changedOrder": { "type": "string", "orderId": "string", "reasonCode": 0, "reasonDescription": "string" } }], "billing": { "address": { "key": 0 }, "payment": { "method": { "code": 0, "description": "CASH" }, "date": "2023-07-18", "card": { "key": 0, "amount": 0 } }, "benefit": { "provider": "string", "form": { "code": 0, "description": "FL MAA" }, "period": 0 } }, "shipping": { "cost": { "amount": 0, "taxAmount": 0 }, "method": { "code": 0, "description": "string" }, "priority": true, "trackingNumber": "string", "shipperType": "string", "address": { "key": 0, "type": { "code": 0, "description": "HOME" } } } } } }';
    
    public HTTPResponse respond(HTTPRequest req)
    {
        
        HttpResponse resp = new HttpResponse();
        resp.setStatusCode(201);
        if(req.getEndpoint().contains('getfinance')){
        	resp.setBody(jsonSuccessFinance);	    
        }
        if(req.getEndpoint().contains('getdemographic')){
            resp.setBody(jsonSuccessDemographics);
        }
        if(req.getEndpoint().contains('getprofiledetail')){
            resp.setBody(jsonSuccessProfile);
        }
        if(req.getEndpoint().contains('getpreference')){
            resp.setBody(jsonSuccessPreferences);
        }
        if(req.getEndpoint().contains('getPlans')){
            resp.setBody(jsonSuccessPlans);
        }
        
        if(req.getEndpoint().contains('getprescriptionsummary')){
            resp.setBody(jsonSuccessPrescriptions);
        }

        if(req.getEndpoint().contains('updateprescriptionsummary')){
            resp.setBody(jsonSuccessUpdatePrescriptions);
        }

        if(req.getEndpoint().contains('addprescriptionsummary')){
            resp.setBody(jsonSuccessAddPrescriptions);
        }

        if(req.getEndpoint().contains('updatePreference')){
            resp.setBody(jsonSuccessUpdatePreference);
        }

        if(req.getEndpoint().contains('gethealthhistory')){
            resp.setBody(jsonSuccessHealthHistory);
        }

        if(req.getEndpoint().contains('fills')){
            resp.setBody(jsonSuccessAddFills);
        }

        if(req.getEndpoint().contains('getordersummary')){
            resp.setBody(jsonSuccessGetOrderSummary);
        }

        if(req.getEndpoint().contains('getorderdetail')){
            resp.setBody(jsonSuccessGetOrderDetail);
        }

        if(req.getEndpoint().contains('getevents')){
            resp.setBody(jsonSuccessGetEvents);
        }

        if(req.getEndpoint().contains('gettasks')){
            resp.setBody(jsonSuccessGetTasks);
        }

        if(req.getEndpoint().contains('getPatientLogNotes')){
            resp.setBody(jsonSuccessGetPatientLogNotes);
        }

        if(req.getEndpoint().contains('addLogNotes')){
            resp.setBody(jsonSuccessAddPatientLogNotes);
        }

        if(req.getEndpoint().contains('getFamilyLogNotes')){
            resp.setBody(jsonSuccessGetFamilyLogNotes);
        }

        if(req.getEndpoint().contains('addFamilyLogNotes')){
            resp.setBody(jsonSuccessAddFamilyLogNotes);
        }

        if(req.getEndpoint().contains('updatePhone')){
            resp.setBody(jsonSuccessUpdatePhone);
        }

        if(req.getEndpoint().contains('updatePhone')){
            resp.setBody(jsonSuccessUpdateAlternatePhone);
        }

        if(req.getEndpoint().contains('updateProfile')){
            resp.setBody(jsonSuccessUpdateProfile);
        }

        if(req.getEndpoint().contains('addFinance')){
            resp.setBody(jsonSuccessAddFinance);
        }

        if(req.getEndpoint().contains('updateFinance')){
            resp.setBody(jsonSuccessUpdateFinance);
        }

        if(req.getEndpoint().contains('addonetimepayment')){
            resp.setBody(jsonSuccessOneTimePayment);
        }

        if(req.getEndpoint().contains('addAddresses')){
            resp.setBody(jsonSuccessAddAddress);
        }

        if(req.getEndpoint().contains('updateAddresses')){
            resp.setBody(jsonSuccessUpdateAddress);
        }

        if(req.getEndpoint().contains('updateorderdetail')){
            resp.setBody(jsonSuccessUpdateOrder);
        }

        if(req.getEndpoint().contains('addorderdetail')){
            resp.setBody(jsonSuccessCreateOrder);
        }

        resp.setStatus('Success');
		
        return resp;
    }

   
}