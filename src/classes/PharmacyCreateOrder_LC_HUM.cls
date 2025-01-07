/*******************************************************************************************************************************
Modification Log:
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------*
* Monali Jagtap                                        09/29/2023              US- 4429836 T1PRJ0891339 - MF 1 - SF -TECH - Error Handling - Make sure all exceptions are handled with appropriate messages
*********************************************************************************************************************************/
public with sharing class PharmacyCreateOrder_LC_HUM {
    
    @AuraEnabled(continuation=true cacheable=true)
    public static Object invokeEASService(string addressDto){
        try {
            AddressValidationHelper_DTO_HUM addDto = (AddressValidationHelper_DTO_HUM)system.JSON.deserialize(addressDto, AddressValidationHelper_DTO_HUM.class);
            return Pharmacy_LS_HUM.validateAddress(addDto);
        } catch (Exception e) {
            HUMExceptionHelper.logErrors(e,'PharmacyCreateOrder_LC_HUM','invokeEASService');
            Throw New AuraHandledException(e.getMessage());
        }
    }

    /**
     * Returns responses of continuation object
     * <p>
     * Callback Method to parse the service response
     *
     * @return Object
     */
    @AuraEnabled(cacheable=true)
    public static object addressResponse(List <string> labels, object State)
    {
        return json.serialize(Pharmacy_LS_HUM.addressResponse(labels, State));
    }

    @AuraEnabled(continuation=true cacheable=true)
    public static Object updateGetMember(string enterprise, string phone, string AltPhone, string Email, string captype, string networkId, String sRecordId, string addressDto)
    {
        AddressValidationHelper_DTO_HUM addDto = (AddressValidationHelper_DTO_HUM)system.JSON.deserialize(addressDto, AddressValidationHelper_DTO_HUM.class);
        return Pharmacy_LS_HUM.prepareEditGetMember(enterprise,phone,AltPhone,Email,captype,networkId,sRecordId,addDto,'');
    }

    /**
     * Returns responses of continuation object
     * <p>
     * Callback Method to parse the service response
     *
     * @return Object
     */
    @AuraEnabled(cacheable=true)
    public static object retrieveUpdatedGetMember(List <string> labels, object State)
    {
        return json.serialize(Pharmacy_LS_HUM.retrieveUpdatedGetMember(labels, State));
    }

    /**
     * Returns request object of continuation object
     * <p>
     * Method to invoke the Epost createEditOrder details continuation requests
     *
     * @return continuation object
     */
    @AuraEnabled(continuation=true)
    public static Object invokecreateEditOrderDetail(string createEditObj, String sRecordId)
    {
        RSCreateAndEditOrderRequest_DTO_HUM creteOrderRequest = (RSCreateAndEditOrderRequest_DTO_HUM)system.JSON.deserialize(createEditObj, RSCreateAndEditOrderRequest_DTO_HUM.class);
        return PharmacyCreateEditOrder_LS_HUM.sendRequestCreateEditOrderDetailRequest(creteOrderRequest, sRecordId);
    }

     
    /**
     * Returns responses of continuation object
     * <p>
     * Callback Method to parse the service response
     *
     * @return Object
     */
    @AuraEnabled
    public static object parseCreateEditOrderResponse(List < string > labels, object State)
    {
        return JSON.serialize(PharmacyCreateEditOrder_LS_HUM.parseCreateEditOrderResponse(labels, State));
    }
    
    /**
     * Returns request object of continuation object
     * <p>
     * Method to invoke the Epost Log Note continuation requests
     *
     * @return continuation object
     */
    @AuraEnabled(continuation=true)
    public static Object invokeLogNoteRequest(Integer numberOfConsentGiven, string sMemID, string networkID, String sRecordId)
    {
        return PharmacyCreateEditOrder_LS_HUM.sendRequestGetEditMemberForScripts(numberOfConsentGiven, sMemID, networkID, sRecordId);
    }
    
    /**
     * Returns responses of continuation object
     * <p>
     * Callback Method to the Log note service response
     *
     * @return Object
     */
    @AuraEnabled
    public static object logNoteResponse(List < string > labels, object State)
    {
        return  PharmacyCreateEditOrder_LS_HUM.parseLogNoteResponse(labels, State);
    }
}