/*****************************************************************************************************************************************************************************
    @Apex Class Name  : EligiblityRequest_LD_HUM
    @Version          : 1.0
    @Created Date     : 06/09/2022
    @Test Class Name  : EligiblityRequest_LT_HUM
******************************************************************************************************************************************************************************
Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Kajal Namdev                                 		  06/09/2022                 Original Version
******************************************************************************************************************************************************************************/
public class EligiblityRequest_LD_HUM {
	public static string sClassName = 'EligiblityRequest_LD_HUM';
    public class ServiceDownException extends Exception {}

    /**
    * @description this method triggers the Validate Eligibility
    * @param String, String, String
    * @return String
    */
    @AuraEnabled()
    public static string retrieveEligibility(string caseRecordId, string platformValue, string templateName) {
        HttpRequest request = new HttpRequest();
        Http http = new Http();
        Eligiblity_DTO_HUM eligibilityWrapperDTO = new Eligiblity_DTO_HUM();
        try {
            request = EligiblityRequest_LS_HUM.getValidateEligiblityRequest(caseRecordId, platformValue);
            if(request != null) {
                HttpResponse response = http.send(request);
                boolean isResponseValid = checkResponseValidity(response);
                if(isResponseValid) {
                    EligiblityResponse_DTO_HUM responseDTO = 
                        (EligiblityResponse_DTO_HUM)System.JSON.deserialize(
                            response.getBody(), 
                            EligiblityResponse_DTO_HUM.class
                        );
                    eligibilityWrapperDTO.eligibilityResponseDTO = responseDTO;
                    eligibilityWrapperDTO.calloutErrored = false;
                    eligibilityWrapperDTO.serviceCalloutError = 'Success';
                    eligibilityWrapperDTO.eligibilityRequestDTO = request.getBody();
                    
                    if(eligibilityWrapperDTO.eligibilityResponseDTO.ValidateEligibilityResponse.result != 'OK') {
                        string sErrMsg = eligibilityWrapperDTO.eligibilityResponseDTO.ValidateEligibilityResponse.errormessage;
                        HUMExceptionHelper.logErrors(New ServiceDownException('SERVICERESPONSEINVALID'+sErrMsg), sClassName, 'retrieveEligibility');
                    }
                    else {
                        eligibilityWrapperDTO.metadataDTO = processEligibility(responseDTO, templateName, caseRecordId);
                    }
                }
                else {
                    FaultCode_DTO_HUM dtoFaultCode = FaultCode_DTO_HUM.parse(response.getBody());
                    eligibilityWrapperDTO.faultResponseDTO = dtoFaultCode;
                    eligibilityWrapperDTO.calloutErrored = true;
                    eligibilityWrapperDTO.serviceCalloutError = dtoFaultCode.Fault.detail.errorInfo.error_message;
                    eligibilityWrapperDTO.eligibilityRequestDTO = request.getBody();
                    HUMExceptionHelper.logErrors(New ServiceDownException('SERVICEERROR'+response.getBody()), sClassName, 'retrieveEligibility');
                }
            }
            else {
                eligibilityWrapperDTO.calloutErrored = true;
                eligibilityWrapperDTO.serviceCalloutError = 'Eligibility Service could not be made as there are no valid policies to proceed further';
                HUMExceptionHelper.logErrors(New ServiceDownException('REQUESTFORMATERROR - Request cannot be null'), sClassName, 'retrieveEligibility');
            }
        }
        catch(Exception ex) {
            eligibilityWrapperDTO.calloutErrored = true;
            eligibilityWrapperDTO.serviceCalloutError = ex.getMessage();
            if(request != null) {
                eligibilityWrapperDTO.eligibilityRequestDTO = request.getBody();
            }
           HUMExceptionHelper.logErrors(ex, sClassName, 'retrieveEligibility');
           
        }
        
        return JSON.serialize(eligibilityWrapperDTO);
    }

    /**
    * @description this method checks whether response is valid or not
    * @param HttpResponse
    * @return boolean
    */
    private static boolean checkResponseValidity(HttpResponse response) {
        boolean returnValue = false;
        try{
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

    /**
    * @description this method acts as the engine to start the rest of the eligibility processes
    * @param EligiblityResponse_DTO_HUM, String, String
    * @return String
    */
    private static string processEligibility(EligiblityResponse_DTO_HUM responseDTO, string templateName, string caseRecordId) {
        string returnValue = '';
        if(HUMUtilityHelper.isCRMFunctionalityON('1900555')) {
            EligibilityResponse_LH_HUM  genericService = new EligibilityResponse_LH_HUM(responseDTO, templateName, caseRecordId);
            returnValue = genericService.processEligiblity();
        }
        else {
            returnValue = '{ "off": true }';
        }
        return returnValue;
    }

}