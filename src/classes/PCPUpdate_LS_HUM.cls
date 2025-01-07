/*****************************************************************************************
Apex class Name    : PCPUpdate_LS_HUM
Version            : 1.0
Function           : controller for displaying PCP update template data.
Created Date       : 04/12/2023
Test Class         : PCPUpdate_LT_HUM
Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Nirmal Garg                         				 	04/12/2023                 	 Initial Version
* Swapnali Sonawane                                     09/25/2023                   US-5073478 PCP Change- Auto update CI/CAS
****************************************************************************************************************/
public with sharing class PCPUpdate_LS_HUM {
    public static final String VOB_FAULT_CODE = 'faultcode';
    public static final Integer HTTP_OK = 200;
    public static final Integer HTTP_ACCEPTED = 202;
    public static final String HUM_RSS_SER_CONTENTTYPE = 'content-type';
    public static final String HUM_RSS_SER_CONECTION = 'Connection';
    public static final String HUM_RSS_SER_KEEPALIVE = 'keep-alive';
    public static final String HUM_RSS_SER_SOAPACTION = 'SOAPAction';
    public class ServiceDownException extends Exception {}
    
    public static HttpRequest builtHttpRequest(HUM_Webservice_Callout__c serviceObj)
    {
        HttpRequest request = new HttpRequest();
        if (serviceObj != null && serviceObj.Active__c)
        {
            request.setMethod(serviceObj.Request_Type__c);
            request.setHeader(HUM_RSS_SER_SOAPACTION, '');
            request.setHeader(HUM_RSS_SER_CONECTION, HUM_RSS_SER_KEEPALIVE);
            if (!Test.isRunningTest() && String.isNotBlank(serviceObj.Certificate_Name__c)){
                request.setClientCertificateName(serviceObj.Certificate_Name__c);
            }
            if (String.isNotBlank(serviceObj.Content_Type__c)){
                request.setHeader(HUM_RSS_SER_CONTENTTYPE, serviceObj.Content_Type__c);
            }            
            if (String.isNotBlank(serviceObj.Service_Name__c))
            {
                request.setEndpoint(serviceObj.End_Point_URL__c + serviceObj.Service_Name__c);
            }
            else
            { 
                request.setEndpoint(serviceObj.End_Point_URL__c);
            }            
            if(serviceObj.Timeout__c != null) {
                request.setTimeout(Integer.valueOf(serviceObj.Timeout__c));
            }
        }
        return request;
    }

    @AuraEnabled
    public static Continuation getProviders(string request){
        try {
            HUM_Webservice_Callout__c providersearchservice = HUM_Webservice_Callout__c.getValues('ProviderSearchServiceV1');
			integer timeout = Integer.valueof(providersearchservice.Timeout__c) / 1000;
			Continuation con = new Continuation(timeout);
			con.continuationMethod = 'processResponse';
			HttpRequest httpProviderReqObj = builtHttpRequest(providersearchservice);
            httpProviderReqObj.setBody(request);       	
        	con.addHttpRequest(httpProviderReqObj);    
        	return con;
        } catch (Exception e) {
            HUMExceptionHelper.logErrors(e, 'PCPUpdate_LC_HUM', 'getProviders');
            throw new AuraHandledException(e.getMessage());
        }
    }


    @AuraEnabled
    public static object processResponse(List<string> labels, Object State){
        HttpResponse response = Continuation.getResponse(Labels[0]);
        if (response != null && (response.getStatusCode() == HTTP_OK || response.getStatusCode() == HTTP_ACCEPTED)){
            return response.getBody();
        }else{
            return null;
        }
    }
    
    @AuraEnabled(continuation=true)
    public static Continuation getNetworkId(string requestbody){
        try {
            HUM_Webservice_Callout__c providerNetworkDetails = HUM_Webservice_Callout__c.getValues('ProviderDirectorySearchServiceNetworks');
            // Create continuation with a timeout
            integer timeout = Integer.valueof(providerNetworkDetails.Timeout__c) / 1000;
            Continuation con = new Continuation(timeout);
            con.continuationMethod = 'parseNetworkResponse';
            HttpRequest httpRSGetMemberReqObj = builtHttpRequest(providerNetworkDetails);
            httpRSGetMemberReqObj.setBody(requestbody);
            con.addHttpRequest(httpRSGetMemberReqObj);
            return con;
        } catch (Exception e) {
            throw new AuraHandledException(e.getMessage());
        }
    }
    
    @AuraEnabled
    public static Object parseNetworkResponse(List<String>labels,Object state){
        try {
            HttpResponse networkResponse = Continuation.getResponse(labels[0]);
            if(networkResponse!= null && !networkResponse.getBody().containsIgnoreCase(VOB_FAULT_CODE) &&
               ((networkResponse.getStatusCode() == HTTP_OK || networkResponse.getStatusCode() == HTTP_ACCEPTED)
                || Test.isRunningTest())){
                    Network_Response_DTO_HUM networkdata = (Network_Response_DTO_HUM)
                        System.JSON.deserialize(networkResponse.getBody(), Network_Response_DTO_HUM.class);
                    return networkdata;
                }else{
                    throw New ServiceDownException('SERVICEERROR'+networkResponse.getBody());
                }
        } catch (Exception e) {
			HUMExceptionHelper.logErrors(e, 'PCPUpdate_LC_HUM', 'parseNetworkResponse');
            throw new AuraHandledException(e.getMessage());
        }
    }

    @AuraEnabled(continuation=true)
    public static Continuation validatePCP(string requestbody){
        try {
            HUM_Webservice_Callout__c mmpService = HUM_Webservice_Callout__c.getValues('MMPServiceRest');
            integer timeout = Integer.valueof(mmpService.Timeout__c) / 1000;
            Continuation con = new Continuation(timeout);
            con.continuationMethod = 'parseValidatePCPResponse';
            HttpRequest httpRSGetMemberReqObj = builtHttpRequest(mmpService);
            httpRSGetMemberReqObj.setBody(requestbody);
            con.addHttpRequest(httpRSGetMemberReqObj);
            return con;
        } catch (Exception e) {
            HUMExceptionHelper.logErrors(e, 'PCPUpdate_LS_HUM', 'validatePCP');
            throw new AuraHandledException(e.getMessage());
        }
    }

    @AuraEnabled
    public static Object parseValidatePCPResponse(List<String>labels,Object state){
        try {
            HttpResponse validatePCPResponse = Continuation.getResponse(labels[0]);
            if(validatePCPResponse!= null && !validatePCPResponse.getBody().containsIgnoreCase(VOB_FAULT_CODE) &&
               ((validatePCPResponse.getStatusCode() == HTTP_OK || validatePCPResponse.getStatusCode() == HTTP_ACCEPTED)
                || Test.isRunningTest())){
                     return validatePCPResponse.getBody();
                }else{
                    throw New ServiceDownException('SERVICEERROR'+validatePCPResponse.getBody());
                }
        } catch (Exception e) {
			HUMExceptionHelper.logErrors(e, 'PCPUpdate_LC_HUM', 'parseValidatePCPResponse');
            throw new AuraHandledException(e.getMessage());
        }
    }

    @AuraEnabled(continuation=true)
    public static Continuation updateMember(string requestbody){
        try {
            HUM_Webservice_Callout__c serviceDetails = HUM_Webservice_Callout__c.getValues('MMPUpdateMemberService');
            integer timeout = Integer.valueof(serviceDetails.Timeout__c) / 1000;
            Continuation con = new Continuation(timeout);
            con.continuationMethod = 'parseUpdateMemberResponse';
            HttpRequest httpRSGetMemberReqObj = builtHttpRequest(serviceDetails);
            httpRSGetMemberReqObj.setMethod('POST');
            httpRSGetMemberReqObj.setBody(requestbody);
            con.addHttpRequest(httpRSGetMemberReqObj);
            return con;
        } catch (Exception e) {
            HUMExceptionHelper.logErrors(e, 'PCPUpdate_LS_HUM', 'updateMember');
            throw new AuraHandledException(e.getMessage());
        }
    }

    @AuraEnabled
    public static Object parseUpdateMemberResponse(List<String>labels,Object state){
        try {
            HttpResponse response = Continuation.getResponse(labels[0]);
            if(response!= null && !response.getBody().containsIgnoreCase(VOB_FAULT_CODE) &&
               ((response.getStatusCode() == HTTP_OK || response.getStatusCode() == HTTP_ACCEPTED)
                || Test.isRunningTest())){
                     return response.getBody();
                }else{
                    throw New ServiceDownException('SERVICEERROR'+response.getBody());
                }
        } catch (Exception e) {
			HUMExceptionHelper.logErrors(e, 'PCPUpdate_LS_HUM', 'parseUpdateMemberResponse');
            throw new AuraHandledException(e.getMessage());
        }
    }
}