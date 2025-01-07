/**
 * @File Name          : UpdateMemberDemographics_D_HUM.cls
 * @Description        : Dto class for template data operation
 * @Author             : Chetan Sindhe
 * @Group              : Team Thunder
 * @Last Modified On   : 6/28/2020, 7:25:52 PM
 * @Modification Log   : 
 * Ver       Date            Author      		    Modification
 * 1.0    6/28/2020        Chetan Sindhe           Initial Version
**/
public with sharing class UpdateMemberDemographics_D_HUM {
    public static string sClassName = 'UpdateMemberDemographics_D_HUM';
    public class ServiceDownException extends Exception {}
    
    /*
    * Method Name: retrieveUpdateResponse
    * Description: method is used to retrieve MemberUpdateResponse
    * Parameter: requestInput
    * Return Type: String
    */
    @AuraEnabled()
    public static string retrieveUpdateResponse(string requestInput) {
        HttpRequest request = new HttpRequest();
        Http http = new Http();
        UpdateMemberDemographics_DTO_HUM updateWrapperDTO = new UpdateMemberDemographics_DTO_HUM();
        try {
            request = UpdateMemberDemographics_S_HUM.constructRequest(requestInput);
            if(request != null) {
                HttpResponse response = http.send(request);
                boolean isResponseValid = checkResponseValidity(response);
                if(isResponseValid) {
                    UpdateMemberDemographicsResponse_DTO_HUM responseDTO = 
                        (UpdateMemberDemographicsResponse_DTO_HUM)System.JSON.deserialize(
                            response.getBody(), 
                            UpdateMemberDemographicsResponse_DTO_HUM.class
                        );
                    updateWrapperDTO.updateResponseDTO = responseDTO;
                    updateWrapperDTO.calloutErrored = false;
                    updateWrapperDTO.serviceCalloutError = 'Success';
                    updateWrapperDTO.updateRequestDTO = request.getBody();
                }
                else {
                    FaultCode_DTO_HUM dtoFaultCode = FaultCode_DTO_HUM.parse(response.getBody());
                    updateWrapperDTO.faultResponseDTO = dtoFaultCode;
                    updateWrapperDTO.calloutErrored = true;
                    updateWrapperDTO.serviceCalloutError = dtoFaultCode.Fault.detail.errorInfo.error_message;
                    updateWrapperDTO.updateRequestDTO = request.getBody();
                    HUMExceptionHelper.logErrors(New ServiceDownException('SERVICEERROR'+response.getBody()), sClassName, 'retrieveUpdateResponse');
                }
            }
            else {
                HUMExceptionHelper.logErrors(New ServiceDownException('REQUESTFORMATERROR - Request cannot be null'), sClassName, 'retrieveUpdateResponse');
            }
        }
        catch(Exception ex) {
            updateWrapperDTO.calloutErrored = true;
            updateWrapperDTO.serviceCalloutError = ex.getMessage();
            if(request != null) {
                updateWrapperDTO.updateRequestDTO = request.getBody();
            }
            HUMExceptionHelper.logErrors(ex, sClassName, 'retrieveUpdateResponse');
        }
        return JSON.serialize(updateWrapperDTO);
    }

    /*
    * Method Name: checkResponseValidity
    * Description: method is used to validate response
    * Parameter: HttpResponse
    * Return Type: boolean
    */
    private static boolean checkResponseValidity(HttpResponse response) {
        boolean returnValue = false;
        try {
            if(response != null) {
                integer statusCode = response.getStatusCode();
                Boolean isStatusOk = (statusCode == GLOBAL_CONSTANT_HUM.HTTP_OK_HUM) ? true : (statusCode == GLOBAL_CONSTANT_HUM.HTTP_ACCEPTED_HUM) ? true : false;
                Boolean isNoFault = (response.getBody().containsIgnoreCase(GLOBAL_CONSTANT_HUM.VOB_FAULT_CODE)) ? false : true;
                returnValue = (isStatusOk && isNoFault) ? true : false;
            }
        }
        catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex, sClassName, 'checkResponseValidity');
            returnValue = false;
        }
        return returnValue;
    }

}