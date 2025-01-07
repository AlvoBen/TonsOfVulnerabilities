/**
 * @File Name          : AddressStandardizeList_S_HUM.cls
 * @Description        : Service Class responsible for construction the HttpRequest for AddressStandardization call
 * @Author             : Chetan Sindhe
 * @Group              : Team Thunder
 * @Last Modified On   : 5/14/2020, 8:36:07 PM
 * @Modification Log   : 
 * Ver       Date            Author      		    Modification
 * 1.0    5/11/2020        Chetan Sindhe           Initial Version
 * 2.0    12/28/2020       Firoja Begam           US1755360 - EAS Service side changes - CR1876 (CR1808 Part 2) - SF
**/
public with sharing class AddressStandardizeList_S_HUM {
    public static string sClassName = 'AddressStandardizeList_S_HUM';

    /*
    * @description: This method will construct HttpRequest for AddressStandardization call
    * @param: addressInput
    * @return: HttpRequest
    */
    public static HttpRequest getAddressStandardizeRequest(string addressInput) {
        HttpRequest request = new HttpRequest();
        Boolean switchOnOffEASSplit = HUMUtilityHelper.isCRMFunctionalityON('1755360');
        try {
            if(switchOnOffEASSplit)
            {
             request = generateHTTPRequestStructure(GLOBAL_CONSTANT_HUM.MM_DU_ADDRSTDZNAME_HUM_V2);
            }else{
             request = generateHTTPRequestStructure(GLOBAL_CONSTANT_HUM.MM_DU_ADDRSTDZNAME_HUM);
            }
            if(request != null) {
                AddressStandardizeRequest_DTO_HUM dtoRequest = new AddressStandardizeRequest_DTO_HUM();
                AddressStandardizeRequest_DTO_HUM.StandardizeAddressRequest dtoAddr = (AddressStandardizeRequest_DTO_HUM.StandardizeAddressRequest)System.JSON.deserialize(addressInput, AddressStandardizeRequest_DTO_HUM.StandardizeAddressRequest.class);
                if(switchOnOffEASSplit){
                dtoAddr.IncludeAddressLine1Split = Label.AddressLine1Split;
                }
                dtoRequest.StandardizeAddressRequest = dtoAddr;
                request.setBody(JSON.serialize(dtoRequest));
            }
            return request;
        }
        catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex, sClassName, 'getAddressStandardizeRequest');
            return null;
        }
    }

    /*
    * @description: This method will construct HttpRequest for AddressStandardization call
    * @param: servicename
    * @return: HttpRequest
    */
    private static HttpRequest generateHTTPRequestStructure(string serviceName) {
        HttpRequest request = new HttpRequest();
        HUM_Webservice_Callout__c objAddrStdzService;
        try {
            objAddrStdzService = HUM_Webservice_Callout__c.getValues(serviceName);
            if(objAddrStdzService != null) {
                string certName = objAddrStdzService.Certificate_Name__c;
                request.setMethod(objAddrStdzService.Request_Type__c);
                if(!Test.isRunningTest() && String.isNotBlank(certName)) {
                    request.setClientCertificateName(certName);
                }
                if(String.isNotBlank(objAddrStdzService.Content_Type__c)) {
                    request.setHeader(
                        GLOBAL_CONSTANT_HUM.MM_DU_SER_CONTENTTYPE_HUM, 
                        objAddrStdzService.Content_Type__c
                    );
                }
                request.setHeader(
                    GLOBAL_CONSTANT_HUM.MM_DU_SER_CONECTION_HUM, 
                    GLOBAL_CONSTANT_HUM.MM_DU_SER_KEEPALIVE_HUM
                );
                request.setEndpoint(
                    objAddrStdzService.End_Point_URL__c
                );
                if(objAddrStdzService.Timeout__c != null) {
                    request.setTimeout(Integer.valueOf(objAddrStdzService.Timeout__c));
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