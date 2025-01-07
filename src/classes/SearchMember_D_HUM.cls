/**
 * @description       : 
 * @author            : Chetan Sindhe
 * @group             : 
 * @last modified on  : 11-09-2020
 * @last modified by  : ChangeMeIn@UserSettingsUnder.SFDoc
 * Modifications Log 
 * Ver   Date         Author                               Modification
 * 1.0   11-03-2020   Chetan Sindhe                        Initial Version
**/
public with sharing class SearchMember_D_HUM {
    public static string sClassName = 'SearchMember_D_HUM';
    public class ServiceDownException extends Exception {}
    
    /*
    * Method Name: retrieveSearchResponse
    * Description: method is used to retrieve SearchMemberResponse
    * Parameter: memberId, platformValue
    * Return Type: String
    */
    @AuraEnabled()
    public static string retrieveSearchResponse(string memberId, string platformValue) {
        HttpRequest request = new HttpRequest();
        Http http = new Http();
        SearchMember_DTO_HUM searchWrapperDTO = new SearchMember_DTO_HUM();
        try {
            request = SearchMember_S_HUM.constructRequest(memberId, platformValue);
            if(request != null) {
                HttpResponse response = http.send(request);
                boolean isResponseValid = checkResponseValidity(response);
                if(isResponseValid) {
                    SearchMemberResponse_DTO_HUM responseDTO = 
                        (SearchMemberResponse_DTO_HUM)System.JSON.deserialize(
                            response.getBody(), 
                            SearchMemberResponse_DTO_HUM.class
                        );
                    searchWrapperDTO.searchResponseDTO = responseDTO;
                    searchWrapperDTO.calloutErrored = false;
                    searchWrapperDTO.serviceCalloutError = 'Success';
                    searchWrapperDTO.searchRequestDTO = request.getBody();
                }
                else {
                    FaultCode_DTO_HUM dtoFaultCode = FaultCode_DTO_HUM.parse(response.getBody());
                    searchWrapperDTO.faultResponseDTO = dtoFaultCode;
                    searchWrapperDTO.calloutErrored = true;
                    searchWrapperDTO.serviceCalloutError = dtoFaultCode.Fault.detail.errorInfo.error_message;
                    searchWrapperDTO.searchRequestDTO = request.getBody();
                    HUMExceptionHelper.logErrors(New ServiceDownException('SERVICEERROR'+response.getBody()), sClassName, 'retrieveSearchResponse');
                }
            }
            else {
                searchWrapperDTO.faultResponseDTO = null;
                searchWrapperDTO.calloutErrored = true;
                searchWrapperDTO.serviceCalloutError = 'REQUESTFORMATERROR - Request cannot be null';
                searchWrapperDTO.searchRequestDTO = request.getBody();
                HUMExceptionHelper.logErrors(New ServiceDownException('REQUESTFORMATERROR - Request cannot be null'), sClassName, 'retrieveSearchResponse');
            }
        }
        catch(Exception ex) {
            searchWrapperDTO.calloutErrored = true;
            searchWrapperDTO.serviceCalloutError = ex.getMessage();
            if(request != null) {
                searchWrapperDTO.searchRequestDTO = request.getBody();
            }
            HUMExceptionHelper.logErrors(ex, sClassName, 'retrieveSearchResponse');
        }
        return JSON.serialize(searchWrapperDTO);
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