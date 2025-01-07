/*******************************************************************************************************************************
Apex Class Name : PharmacyOrderDetail_LS_HUM.cls
Version         : 1.0
Created On      : 2021
Function        : Service Class for PharmacyOrderDetail_LC_HUM.

Modification Log:
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------*
* Abhishek Mangutkar                                   10/19/2021                 REQ 2674165 Original Version
*Aishwarya Pawar										1/22/2022				Df-4313
*Abhishek Mangutkar                                     03/04/2022              US-3103531 
* Monali Jagtap                                         09/29/2023              US- 4429836 T1PRJ0891339 - MF 1 - SF -TECH - Error Handling - Make sure all exceptions are handled with appropriate messages
*********************************************************************************************************************************/
Public with sharing Class PharmacyOrderDetail_LS_HUM {
            
Public Static final String PHARMACY_PROCESS_ORD_RES_HUM = 'ProcessOrderResponse';
Public Static final String PHARMACY_PROCESS_ORD_DET_RES_HUM = 'ProcessOrderDetailResponse';    
Public Static final String PHARMACY_SUMMARY_HUM = 'Summary';
Public Static final String VOB_FAULT_CODE = 'faultcode';
Public Static final Integer HTTP_OK_HUM = 200;
Public Static final Integer HTTP_ACCEPTED_HUM = 202; 
Public Static final String PHAR_PROCESSCHILDORDERDETAILS_HUM = 'processChildOrderDetails';
Public Static final String PHARMACY_GETORDER_RES_HUM = 'processGetOrderResponse';
Public Static final String PHARMACY_RELORDER_RES_HUM = 'processRelOrderResponse';  
Public Static final String HUM_MEMBER_NOT_FOUND = 'Member not found';
Public Static final String HUM_RSS_SER_KEEPALIVE = 'keep-alive';
Public Static final String HUM_RSS_SER_SOAPACTION = 'SOAPAction';
Public Static final String HUM_RSS_SER_CONTENTTYPE = 'content-type';
Public Static final String HUM_RSS_SER_CONECTION = 'Connection';
    /**
     * Returns HttpRequest request with request body
     * <p>
     * Method to prepare httpRequest details And certification details
     *
     * @param  HUM_Webservice_Callout__c record .
     * @return HttpRequest
     */
    public static HttpRequest builtHttpRequest(HUM_Webservice_Callout__c ServiceObj) {
        HttpRequest request = new HttpRequest();
        if (ServiceObj != null && ServiceObj.Active__c) 
        {
            request.setMethod(ServiceObj.Request_Type__c);
            if (!Test.isRunningTest() && String.isNotBlank(ServiceObj.Certificate_Name__c)) request.setClientCertificateName(ServiceObj.Certificate_Name__c);
            if (String.isNotBlank(ServiceObj.Content_Type__c)) request.setHeader(HUM_RSS_SER_CONTENTTYPE, ServiceObj.Content_Type__c);
            request.setHeader(HUM_RSS_SER_CONECTION, HUM_RSS_SER_KEEPALIVE);
            request.setEndpoint(ServiceObj.End_Point_URL__c);
            request.setHeader(HUM_RSS_SER_SOAPACTION, '');
        }
        return request;
    }
    /**
     * Returns HttpRequest request with request body
     * <p>
     * Method to prepare httpRequest details And certification details
     *
     * @param  HUM_Webservice_Callout__c record .
     * @return HttpRequest
     */
    public static Object invokeOrderDetailRx(String orderKeyValue, String source, String startDate, String endDate, String enterpriseId, string networkID, String sRecordId) {
		try
		{                  
			HUM_Webservice_Callout__c RSServiceDetails = HUM_Webservice_Callout__c.getValues('RighSourceService_GetOrderDetails');
			integer iTIMEOUT_INT_SECS = Integer.valueof(RSServiceDetails.Timeout__c) / 1000;
			Continuation con = new Continuation(iTIMEOUT_INT_SECS);
			con.continuationMethod = PHARMACY_PROCESS_ORD_DET_RES_HUM;
			HttpRequest httpRSGetOrderDetailReqObj = builtHttpRequest(RSServiceDetails);
			httpRSGetOrderDetailReqObj.setBody(JSON.serialize(PharmacyOrderDetail_LD_HUM.requestStructureGetOrderDetailResponse(orderKeyValue,networkID)));
			con.addHttpRequest(httpRSGetOrderDetailReqObj);
			return con;
		}
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'PharmacyOrderDetail_LS_HUM', 'invokeOrderDetail');
            throw new AuraHandledException(ex.getMessage());
        }
    }
    /**
     * Returns continuation object
     * <p>
     * Method to call continuation serive for the serive for GetOrderResponse services .
     *
     * @param pharmacyCls                     Pharmacy_C_HUM class instance
     * @return                                continuation
     */
    public static Object invokeOrderDetail(String orderKeyValue, String source, String startDate, String endDate, String enterpriseId, string networkID, String sRecordId) {
		try
		{                  
			HUM_Webservice_Callout__c RSServiceDetails = HUM_Webservice_Callout__c.getValues('RighSourceService_GetOrderDetails');
			integer iTIMEOUT_INT_SECS = Integer.valueof(RSServiceDetails.Timeout__c) / 1000;
			Continuation con = new Continuation(iTIMEOUT_INT_SECS);
			con.continuationMethod = PHARMACY_PROCESS_ORD_RES_HUM;
			If (String.isBlank(source) || source != PHARMACY_SUMMARY_HUM) 
			{
				HUM_Webservice_Callout__c RSServicegetOrderDetails = HUM_Webservice_Callout__c.getValues('RighSourceService_GetOrders');
				HttpRequest httpRSGetOrderReqObj = builtHttpRequest(RSServicegetOrderDetails);
				httpRSGetOrderReqObj.setBody(JSON.serialize(PharmacyOrderDetail_LD_HUM.requestStructureGetOrderResponse(enterpriseId, startDate, endDate,networkID)));
				con.addHttpRequest(httpRSGetOrderReqObj);
			}
			HttpRequest httpRSGetOrderDetailReqObj = builtHttpRequest(RSServiceDetails);
			httpRSGetOrderDetailReqObj.setBody(JSON.serialize(PharmacyOrderDetail_LD_HUM.requestStructureGetOrderDetailResponse(orderKeyValue,networkID)));
			con.addHttpRequest(httpRSGetOrderDetailReqObj);
			con.state = new PharmacyOrderWrapper_DTO_HUM(null, null, null, source, orderKeyValue,networkID,sRecordId);
			return con;
		}
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'PharmacyOrderDetail_LS_HUM', 'invokeOrderDetail');
            throw new AuraHandledException(ex.getMessage());
        }
    }
    
    /**
     * Returns responses of continuation object
     * <p>
     * parseGetAdjPayResponse Method to parse the service response
     *
     * @param List<string> labels .
     * @param  object State
     * @return Object
     */
    public static Object ProcessOrderResponse(List < string > labels, Object State) {
        try
        {
                
			PharmacyOrderWrapper_DTO_HUM objOrderWrapDTO = new PharmacyOrderWrapper_DTO_HUM(null, null, null, '', '','','');
			Integer conLableCount = 0;
			HttpResponse responseOrderSummary = Continuation.getResponse(labels[conLableCount]);  
			
			RightSourceGetOrderResponse_DTO_HUM oResponseGetOrder;
			If (responseOrderSummary != null && !responseOrderSummary.getBody().containsIgnoreCase(VOB_FAULT_CODE) && ((responseOrderSummary.getStatusCode() == HTTP_OK_HUM || responseOrderSummary.getStatusCode() == HTTP_ACCEPTED_HUM) || Test.isRunningTest()))	   
			{
				oResponseGetOrder = (RightSourceGetOrderResponse_DTO_HUM) System.JSON.deserialize(responseOrderSummary.getBody(), RightSourceGetOrderResponse_DTO_HUM.class);
				objOrderWrapDTO = PharmacyOrderDetail_LD_HUM.getOrderFromOrderSummary(oResponseGetOrder, objOrderWrapDTO);
			}
			conLableCount++;
			RightSourceGetOrderDetResponse_DTO_HUM.GetOrderDetailResponse oResponse = new RightSourceGetOrderDetResponse_DTO_HUM.GetOrderDetailResponse();
			HttpResponse orderDetailResponse = Continuation.getResponse(labels[conLableCount]);
			String changedOrderkey = '';
			If(orderDetailResponse != null && !orderDetailResponse.getBody().containsIgnoreCase(VOB_FAULT_CODE) && ((orderDetailResponse.getStatusCode() == HTTP_OK_HUM || orderDetailResponse.getStatusCode() == HTTP_ACCEPTED_HUM) || Test.isRunningTest()))
			{
				objOrderWrapDTO.objParentOrder = PharmacyOrderDetail_LD_HUM.prepareOrderDetails((RightSourceGetOrderDetResponse_DTO_HUM) System.JSON.deserialize(orderDetailResponse.getbody(), RightSourceGetOrderDetResponse_DTO_HUM.class));                    
			}
			else
			{
				Pharmacy_H_HUM.logHpFaultError(responseOrderSummary,'PharmacyOrderDetail_LS_HUM', 'processOrderResponse', '');
			} 
		   
			return objOrderWrapDTO;
         }
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'PharmacyOrderDetail_LS_HUM', 'ProcessOrderResponse');
            throw new AuraHandledException(ex.getMessage());
        }
        
    } 
    
    /**
     * Returns responses of continuation object
     * <p>
     * parseGetAdjPayResponse Method to parse the service response
     *
     * @param List<string> labels .
     * @param  object State
     * @return Object
     */
     public static Object ProcessOrderDetailResponse(List < string > labels, Object State) {
        try
        {
                
			PharmacyOrderWrapper_DTO_HUM objOrderWrapDTO = new PharmacyOrderWrapper_DTO_HUM(null, null, null, '', '','','');
			Integer conLableCount = 0;
			HttpResponse responseOrderSummary = Continuation.getResponse(labels[conLableCount]);  
			
			RightSourceGetOrderDetResponse_DTO_HUM.GetOrderDetailResponse oResponse = new RightSourceGetOrderDetResponse_DTO_HUM.GetOrderDetailResponse();
			HttpResponse orderDetailResponse = Continuation.getResponse(labels[conLableCount]);
			String changedOrderkey = '';
			If(orderDetailResponse != null && !orderDetailResponse.getBody().containsIgnoreCase(VOB_FAULT_CODE) && ((orderDetailResponse.getStatusCode() == HTTP_OK_HUM || orderDetailResponse.getStatusCode() == HTTP_ACCEPTED_HUM) || Test.isRunningTest()))
			{
				oResponse = PharmacyOrderDetail_LD_HUM.prepareOrderDetails((RightSourceGetOrderDetResponse_DTO_HUM) System.JSON.deserialize(orderDetailResponse.getbody(), RightSourceGetOrderDetResponse_DTO_HUM.class));                    
			}
			else
			{
				Pharmacy_H_HUM.logHpFaultError(responseOrderSummary,'PharmacyOrderDetail_LS_HUM', 'ProcessOrderDetailResponse', '');
			} 
		   
			return oResponse;
         }
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'PharmacyOrderDetail_LS_HUM', 'ProcessOrderDetailResponse');
            throw new AuraHandledException(ex.getMessage());
        }
        
    }
	
	public static Object sendRequestReleaseAndCancelOrder(RSCreateAndEditOrderRequest_DTO_HUM createEditObj, String sRecordId)
    {
         try
        {            
          HUM_Webservice_Callout__c RSServiceDetails = HUM_Webservice_Callout__c.getValues('RighSourceService_CreateAndEditOrder');       
          // Create continuation with a timeout
          integer iTIMEOUT_INT_SECS = Integer.valueof(RSServiceDetails.Timeout__c) / 1000;
          Continuation con = new Continuation(iTIMEOUT_INT_SECS);
          con.continuationMethod = PHARMACY_RELORDER_RES_HUM;
          con.state = sRecordId;
          HttpRequest httpRSCreateEditOrderObj = builtHttpRequest(RSServiceDetails);
          httpRSCreateEditOrderObj.setBody(JSON.serialize(createEditObj));
          con.addHttpRequest(httpRSCreateEditOrderObj);
          return con;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'PharmacyOrderDetail_LS_HUM', 'sendRequestReleaseAndCancelOrder');
            throw new AuraHandledException(ex.getMessage());
        }
    }
    
    
    
    /**
     * Returns responses of continuation object
     * <p>
     * parseRSGetScriptResponse Method to parse the service response
     *
     * @param  object state,List<string> labels .
     * @return Object
     */
    public static object processRelOrderResponse(List <string> labels, Object State) {
        Boolean bRelCancelSuccess = false;        
        try
        {
            HttpResponse resRSRelOrder = Continuation.getResponse(labels[0]);
            
        RSCreateAndEditOrderResponse_DTO_HUM obResRelOrder = new RSCreateAndEditOrderResponse_DTO_HUM();
            if (resRSRelOrder != null && !resRSRelOrder.getBody().containsIgnoreCase(VOB_FAULT_CODE) &&  ((resRSRelOrder.getStatusCode() == HTTP_OK_HUM || resRSRelOrder.getStatusCode() == HTTP_ACCEPTED_HUM) || Test.isRunningTest()))
            
            {            
          obResRelOrder = (RSCreateAndEditOrderResponse_DTO_HUM)System.JSON.deserialize(resRSRelOrder.getBody(), RSCreateAndEditOrderResponse_DTO_HUM.class);
                if (obResRelOrder != null && null != obResRelOrder.CreateAndEditOrderResponse && String.isNotBlank(obResRelOrder.CreateAndEditOrderResponse.transactionKey)) bRelCancelSuccess = true;
            }
            else
            {            
              // Fault Code
              Pharmacy_H_HUM.logHpFaultError(resRSRelOrder,'PharmacyOrderDetail_LS_HUM', 'processRelOrderResponse', '');
            }
    
        if (null != obResRelOrder && null != obResRelOrder.CreateAndEditOrderResponse && obResRelOrder.CreateAndEditOrderResponse.ErrorDescription != HUM_MEMBER_NOT_FOUND)
          return bRelCancelSuccess;
        else
          return null;
         }
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'PharmacyOrderDetail_LS_HUM', 'processRelOrderResponse');
            throw new AuraHandledException(ex.getMessage());
        }
    }
}