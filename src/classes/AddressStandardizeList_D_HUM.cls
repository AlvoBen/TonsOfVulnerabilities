/**
 * @File Name          : AddressStandardizeList_D_HUM.cls
 * @Description        : Data Class responsible for making and processing the AddressStandardization call
 * @Author             : Chetan Sindhe
 * @Group              : Team Thunder
 * @Last Modified On   : 5/15/2020, 2:09:58 AM
 * @Modification Log   : 
 * Ver       Date            Author      		    Modification
 * 1.0    5/11/2020        Chetan Sindhe           Initial Version
**/
public with sharing class AddressStandardizeList_D_HUM {
    public static string sClassName = 'AddressStandardizeList_D_HUM';
    public class ServiceDownException extends Exception {}
    
    /*
    * Method Name: retrieveAddrStdzResult
    * Description: This AuraEnabled() method will retrieve Address Standardization Results
    * Parameter: addressInput comprising of addressline, city, StateCode and ZipCode
    * Return Type: stringified json response
    */
    @AuraEnabled()
    public static string retrieveAddrStdzResult(string addressInput) {
        HttpRequest request = new HttpRequest();
        Http http = new Http();
        Map<String, String> errReturnMap = new Map<String, String>();
        AddressStandardizeList_DTO_HUM addrStdzWrapper = new AddressStandardizeList_DTO_HUM();
        try {
            request = AddressStandardizeList_S_HUM.getAddressStandardizeRequest(addressInput);
            HttpResponse response = http.send(request);
            boolean isResponseValid = checkResponseValidity(response);
            if(isResponseValid) {
                AddressStandardizeResponse_DTO_HUM responseDTO = 
                    (AddressStandardizeResponse_DTO_HUM)System.JSON.deserialize(
                        response.getBody(), 
                        AddressStandardizeResponse_DTO_HUM.class
                    );
                addrStdzWrapper.addressStandardizedResponse = responseDTO;
                addrStdzWrapper.calloutErrored = false;
                addrStdzWrapper.serviceCalloutError = 'Success';
            }
            else {
                FaultCode_DTO_HUM dtoFaultCode = FaultCode_DTO_HUM.parse(response.getBody());
                addrStdzWrapper.faultResponseDTO = dtoFaultCode;
                addrStdzWrapper.calloutErrored = true;
                addrStdzWrapper.serviceCalloutError = dtoFaultCode.Fault.detail.errorInfo.error_message;
                HUMExceptionHelper.logErrors(New ServiceDownException('SERVICEERROR'+response.getBody()), sClassName, 'retrieveAddrStdzResult');
            }
        }
        catch(Exception ex) {
            addrStdzWrapper.calloutErrored = true;
            addrStdzWrapper.serviceCalloutError = ex.getMessage();
            HUMExceptionHelper.logErrors(ex, sClassName, 'retrieveAddrStdzResult');
        }
        return JSON.serialize(addrStdzWrapper);
    }

     /*
    * Method Name: checkResponseValidity
    * Description: This method will check whether response is valid or not.
    * Parameter: HttpResponse received in the method retrieveAddrStdzResult.
    * Return Type: boolean to determine valid or not.
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