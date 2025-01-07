/*
Class Name       : StateCodeUtility_WS_HUM
Created Date     : 08/31/2021
Function         : Webservice Test Class for EnterpriseAddress Service for GetCountyDetails call
Modification Log :
*Version  Developer          Code Review         Date                 Description
* ------------------------------------------------------------------------------------------------------------------------------
*1.0	  Ankit Avula						     08/31/2021			  US2365934: T1PRJ0002855/PR00094919 - Account Management - Unknown Records - Prefill State Dropdown when user Inputs Zip, Create Unknown Member, Provider, and Group
-------------------------------------------------------------------------------------------------------------------------------*/

public class StateCodeUtility_WS_HUM {
    @AuraEnabled
    public static string searchStateCode(String zipCode){
        try{
            HUM_Webservice_Callout__c serviceDetails = HUM_Webservice_Callout__c.getValues('EnterpriseAddress_GetCountyDetails');
            Http http = new Http();
            HttpRequest request = new HttpRequest();
            request.setEndpoint(serviceDetails.End_Point_URL__c);
            request.setMethod(serviceDetails.Request_Type__c);
            request.setHeader('Content-Type',serviceDetails.Content_Type__c);
            request.setTimeout(Integer.valueOf(serviceDetails.Timeout__c));
            if(!test.isRunningTest()){
                request.setClientCertificateName(serviceDetails.Certificate_Name__c);
            }
            StateCodeRequest_DTO_HUM req = new StateCodeRequest_DTO_HUM();
            req.GetCountyDetailsRequest = new StateCodeRequest_DTO_HUM.GetCountyDetailsRequest();
            req.GetCountyDetailsRequest.ZipCode = zipCode;
            request.setBody(json.serialize(req));
            HttpResponse response = http.send(request);
            system.debug('response'+response.getBody());
            StateCodeResponse_DTO_HUM res = (StateCodeResponse_DTO_HUM)json.deserialize(response.getBody(), StateCodeResponse_DTO_HUM.class);
            system.debug('res'+res);
            return res.getCountyDetailsResponse.primaryState;
        }
        catch(Exception ex){
            HUMExceptionHelper.logErrors(ex,'StateCodeUtility_WS_HUM', 'searchStateCode');
            return null;
        }
    }
}