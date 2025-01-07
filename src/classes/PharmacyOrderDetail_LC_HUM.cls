/*******************************************************************************************************************************
Apex Class Name : PharmacyOrderDetail_LC_HUM
Version         : 1.0
Created On      : 03/04/2022
Function        : This Class serves pharmacy order details functionality. 
Test Class      : PharmacyCreateEditOrder_LT_HUM
Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Abhishek Mangutkar	                                  03/04/2022                 US-3103531
* Monali Jagtap                                           09/29/2023                 US- 4429836 T1PRJ0891339 - MF 1 - SF -TECH - Error Handling - Make sure all exceptions are handled with appropriate messages
*********************************************************************************************************************************/

Public with sharing Class PharmacyOrderDetail_LC_HUM {
    /**
     * Returns request object of continuation object
     * <p>
     * Method to invoke the Epost Get Order details continuation requests
     *
     * @return continuation object
     */
    @AuraEnabled(continuation=true cacheable=true)
    public static object getOrderData(String orderKeyValue,String source,String startDate,String endDate,String enterpriseId, string networkID, String sRecordId)
    {
        return PharmacyOrderDetail_LS_HUM.invokeOrderDetail( orderKeyValue, source, startDate, endDate, enterpriseId, networkID, sRecordId);
    }
    /**
     * Returns request object of continuation object
     * <p>
     * Method to invoke the Epost Get Order details continuation requests
     *
     * @return continuation object
     */
     @AuraEnabled(continuation=true cacheable=true)
    public static Object getOrderDetails(String orderKeyValue,String source,String startDate,String endDate,String enterpriseId, string networkID, String sRecordId)
    {
        return PharmacyOrderDetail_LS_HUM.invokeOrderDetailRx( orderKeyValue, source, startDate, endDate, enterpriseId, networkID, sRecordId);
    }
    /**
     * Returns responses of continuation object
     * <p>
     * Callback Method to parse the service response
     *
     * @return Object
     */
     @AuraEnabled(cacheable=true)
    Public Static Object ProcessOrderDetailResponse(List < String > labels, Object State)
    {
        Return Json.serialize(PharmacyOrderDetail_LS_HUM.ProcessOrderDetailResponse(labels, State));
    }
    /**
     * Returns responses of continuation object
     * <p>
     * Callback Method to parse the service response
     *
     * @return Object
     */
    @AuraEnabled(cacheable=true)
    public static object ProcessOrderResponse(List < string > labels, object State)
    {
        return PharmacyOrderDetail_LS_HUM.ProcessOrderResponse(labels, State);
    }
	
	/**
     * Returns request object of continuation object
     * <p>
     * Method to invoke the Epost createEditOrder details continuation requests
     *
     * @return continuation object
     */
    @AuraEnabled(continuation=true)
    public static Object sendRequestReleaseAndCancelOrder(string createEditObj, String sRecordId)
    {
        RSCreateAndEditOrderRequest_DTO_HUM creteOrderRequest = (RSCreateAndEditOrderRequest_DTO_HUM)system.JSON.deserialize(createEditObj, RSCreateAndEditOrderRequest_DTO_HUM.class);        
        return PharmacyOrderDetail_LS_HUM.sendRequestReleaseAndCancelOrder(creteOrderRequest, sRecordId);
    }
    
    /**
     * Returns responses of continuation object
     * <p>
     * Callback Method to parse the service response
     *
     * @return Object
     */
    @AuraEnabled
    public static object  processRelOrderResponse(List < string > labels, Object State)
    {
        return PharmacyOrderDetail_LS_HUM.processRelOrderResponse( labels,  State);
    }

}