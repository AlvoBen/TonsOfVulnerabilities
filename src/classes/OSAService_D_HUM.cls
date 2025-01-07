/**
 * @File Name          : OSAService_D_HUM.cls
 * @Description        : Data class to call and process OSA Service Call
 * @Author             : Chetan Sindhe
 * @Group              : Team Thunder
 * @Last Modified On   : 5/15/2020, 2:29:07 AM
 * @Modification Log   : 
 * Ver       Date            Author      		    Modification
 * 1.0    5/11/2020        Chetan Sindhe           Initial Version
**/
public with sharing class OSAService_D_HUM {
    public static string sClassName = 'OSAService_D_HUM';
    public class ServiceDownException extends Exception {}

    /*
    * Method Name: retrieveOSAResult
    * Description: method is used to process HttpResponse for OSA Service call w.r.t. to response DTO
    * Parameter: plan id in string format
    * Return Type: stringified JSON response
    */
    @AuraEnabled()
    public static string retrieveOSAResult(string osaInput) {
        HttpRequest request = new HttpRequest();
        Http http = new Http();
        Map<String, String> errReturnMap = new Map<String, String>();
        OSAService_DTO_HUM osaWrapper = new OSAService_DTO_HUM();
        try {
            request = OSAService_S_HUM.getOSAServiceRequest(osaInput);
            HttpResponse response = http.send(request);
            boolean isResponseValid = checkResponseValidity(response);
            if(isResponseValid) {
                OSAServiceResponse_DTO_HUM responseDTO = 
                    (OSAServiceResponse_DTO_HUM)System.JSON.deserialize(
                        response.getBody(), 
                        OSAServiceResponse_DTO_HUM.class
                    );
                osaWrapper.osaServiceResponse = responseDTO;
                osaWrapper.calloutErrored = false; 
                osaWrapper.serviceCalloutError = 'Success';
            }
            else {
                FaultCode_DTO_HUM dtoFaultCode = FaultCode_DTO_HUM.parse(response.getBody());
                osaWrapper.faultResponseDTO = dtoFaultCode; 
                osaWrapper.calloutErrored = true; 
                osaWrapper.serviceCalloutError = dtoFaultCode.Fault.detail.errorInfo.error_message;
                HUMExceptionHelper.logErrors(New ServiceDownException('SERVICEERROR'+response.getBody()), sClassName, 'retrieveOSAResult');
            }
        }
        catch(Exception ex) {
            osaWrapper.calloutErrored = true; 
            osaWrapper.serviceCalloutError = ex.getMessage();
            HUMExceptionHelper.logErrors(ex, sClassName, 'retrieveOSAResult');
        }
        return JSON.serialize(osaWrapper); 
    }
    
    /*
    * Method Name: checkResponseValidity
    * Description: method to determine validity of response
    * Parameter: HttpResponse
    * Return Type: Boolean
    */
    private static boolean checkResponseValidity(HttpResponse response) {
        boolean returnValue = false;
        if(response != null) {
            integer statusCode = response.getStatusCode();
            Boolean isStatusOk = (statusCode == GLOBAL_CONSTANT_HUM.HTTP_OK_HUM) ? true : (statusCode == GLOBAL_CONSTANT_HUM.HTTP_ACCEPTED_HUM) ? true : false;
            Boolean isNoFault = (response.getBody().containsIgnoreCase(GLOBAL_CONSTANT_HUM.VOB_FAULT_CODE)) ? false : true;
            returnValue = (isStatusOk && isNoFault) ? true : false;
        }
        return returnValue;
    }
}