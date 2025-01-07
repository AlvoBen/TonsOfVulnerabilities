/**
 * @File Name          : OSAService_S_HUM.cls
 * @Description        : Service class to generate the httprequest for OSA Service
 * @Author             : Chetan Sindhe
 * @Group              : Team Thunder
 * @Last Modified On   : 5/15/2020, 2:32:07 AM
 * @Modification Log   : 
 * Ver       Date            Author      		    Modification
 * 1.0    5/11/2020        Chetan Sindhe           Initial Version
**/
public with sharing class OSAService_S_HUM {
    public static string sClassName = 'OSAService_S_HUM';

    /*
    * Method Name: getOSAServiceRequest
    * Description: method is used to generate HttpRequest for OSA Service call w.r.t to request DTO
    * Parameter: stringified Plan Id
    * Return Type: HttpRequest
    */
    public static HttpRequest getOSAServiceRequest(string osaInput) {
        HttpRequest request = new HttpRequest();
        try {
            request = generateHTTPRequestStructure(GLOBAL_CONSTANT_HUM.MM_DU_OSANAME_HUM);
            if(request != null) {
                OSAServiceRequest_DTO_HUM dtoRequest = 
                    (OSAServiceRequest_DTO_HUM)System.JSON.deserialize(
                        osaInput, 
                        OSAServiceRequest_DTO_HUM.class
                    );
                request.setBody(JSON.serialize(dtoRequest));
            }
            return request;
        }
        catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex, sClassName, 'getOSAServiceRequest');
            return null;
        }
    }

    /*
    * Method Name: generateHTTPRequestStructure
    * Description: private method is used to generate HttpRequest for OSA Service call w.r.t to request DTO
    * Parameter: string consisiting of ServiceName
    * Return Type: HttpRequest
    */
    private static HttpRequest generateHTTPRequestStructure(string serviceName) {
        HttpRequest request = new HttpRequest();
        HUM_Webservice_Callout__c objOSAService;
        try {
            objOSAService = HUM_Webservice_Callout__c.getValues(serviceName);
            if(objOSAService != null) {
                string certName = objOSAService.Certificate_Name__c;
                request.setMethod(objOSAService.Request_Type__c);
                if(!Test.isRunningTest() && String.isNotBlank(certName)) {
                    request.setClientCertificateName(certName);
                }
                if(String.isNotBlank(objOSAService.Content_Type__c)) {
                    request.setHeader(
                        GLOBAL_CONSTANT_HUM.MM_DU_SER_CONTENTTYPE_HUM, 
                        objOSAService.Content_Type__c
                    );
                }
                request.setHeader(
                    GLOBAL_CONSTANT_HUM.MM_DU_SER_CONECTION_HUM, 
                    GLOBAL_CONSTANT_HUM.MM_DU_SER_KEEPALIVE_HUM
                );
                request.setEndpoint(
                    objOSAService.End_Point_URL__c + 
                    objOSAService.Service_Name__c
                );
                if(objOSAService.Timeout__c != null) {
                    request.setTimeout(Integer.valueOf(objOSAService.Timeout__c));
                }
            }
            return request;
        }
        catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex, sClassName, 'generateHTTPRequestStructure');
            return null;
        }
    }
}