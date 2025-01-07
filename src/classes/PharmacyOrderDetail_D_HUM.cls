/*******************************************************************************************************************************
Apex Class Name : PharmacyOrderDetail_D_HUM.cls
Version         : 1.0
Created On      : 2017
Function        : Data Layer Class for PharmacyOrderDetail_C_HUM.

Modification Log:
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Avinash Choubey           87810                       04/14/2017                REQ 305689 Original version 
* Rajesh Keswani            112036	                    07/17/2017                Adding network ID in service Request
* Rajesh Chandgothia		114002 				        07/25/2017				  REQ - 323485, CMO CR 5227 (CSI 587)-- 1) remove MTM hover; 2) add 'order status' column; 3) stop translating to OPEN; 4) add 'finished date' to summary
* Riya Agarwal								            08/18/2020			      REQ - 760189-PR00091606 - MF 4 - Finance Code Order Detail Page
* Pinky Vijur                                  05/11/2021                  REQ 2230065: T1PRJ0002606 -2021 MF1- SF - Humana Constants Cleanup Humana Pharmacy
* Pallavi Shewale				 	06/08/2021                 US-1491784 Changes - Finance Queue
* Lavanya Rao				 							10/11/2021                 User Story 2731234: T1PRJ0002606- 2021 MF6 - Swat-SF-Tech-HumConstant And Global Constant Fix
*********************************************************************************************************************************/
public with sharing class PharmacyOrderDetail_D_HUM
{

    Public Static final String PHARMACY_CURRENT_QUEUE = 'FINANCE';
    Public Static final String PHARMACY_TRAD_HUM = 'trad';
	/**
     * Returns RightSourceGetOrderRequest_DTO_HUM  object that can then be utilized for sending the webservice request.
     * This method Is to create the request Structure for GetMemberResponseservice .
     *
     * @param    
     * @return  RightSourceGetOrderRequest_DTO_HUM  request structure of the wrapper class RightSourceGetOrderRequest_DTO_HUM 
     */
    public static RightSourceGetOrderRequest_DTO_HUM requestStructureGetOrderResponse(String memID, String startDate, String endDate, string networkID)
    {
        RightSourceGetOrderRequest_DTO_HUM oRequest = new RightSourceGetOrderRequest_DTO_HUM();
        RightSourceGetOrderRequest_DTO_HUM.OrdersRequest orderRequest = new RightSourceGetOrderRequest_DTO_HUM.OrdersRequest();
        orderRequest.EnterprisePersonID = memID;
        orderRequest.OrderStartDate = startDate; 
        orderRequest.OrderEndDate = endDate;
        orderRequest.CustomerServiceId = networkID;
        oRequest.GetOrdersRequest = orderRequest;
        return oRequest;
    }
     /**
     * Returns RightSourceGetOrderRequest_DTO_HUM  object that can then be utilized for sending the webservice request.
     * This method is to create the request Structure for GetMemberResponseservice .
     *
     * @param    
     * @return  RightSourceGetOrderRequest_DTO_HUM  request structure of the wrapper class RightSourceGetOrderRequest_DTO_HUM 
     */
    public static RightSourceGetOrderDetRequest_DTO_HUM requestStructureGetOrderDetailResponse(String sOrderKey, string networkID)
    {
        RightSourceGetOrderDetRequest_DTO_HUM oRequest = new RightSourceGetOrderDetRequest_DTO_HUM();
        RightSourceGetOrderDetRequest_DTO_HUM.GetOrderDetailsRequest orderDetailRequest = new RightSourceGetOrderDetRequest_DTO_HUM.GetOrderDetailsRequest();
        orderDetailRequest.OrderKey = sOrderKey;
        orderDetailRequest.IncludeStaleData ='';
        orderDetailRequest.CustomerServiceId = networkID;
        oRequest.GetOrderDetailsRequest = orderDetailRequest;
        return oRequest;
    }
    
       /**
     * Returns PharmacyOrderSummary_DTO_HUM  object that can then be utilized for sending the webservice request.
     * This method is to create the request Structure for GetOrderResponseservice .
     *
     * @param    
     * @return  PharmacyOrderSummary_DTO_HUM  request structure of the wrapper class RightSourceGetOrderRequest_DTO_HUM 
     */
    public static PharmacyOrderWrapper_DTO_HUM getOrderFromOrderSummary(RightSourceGetOrderResponse_DTO_HUM oResponseGetOrder, PharmacyOrderWrapper_DTO_HUM objOrderWrapDTO)
    {
        if (oResponseGetOrder != null && oResponseGetOrder.GetOrdersReponse != null)
        {
            RightSourceGetOrderResponse_DTO_HUM.OrdersReponse orderRes = oResponseGetOrder.GetOrdersReponse;
            if (orderRes.Orders != null)
            {                
                RightSourceGetOrderResponse_DTO_HUM.OrdersDetail orderObj = orderRes.Orders;
                if (orderObj.Order != null && orderObj.Order.size() > 0)
                {  
                    for (RightSourceGetOrderResponse_DTO_HUM.OrderDetail obj: orderObj.Order)
                    {
                       if(obj.OrderNumber == objOrderWrapDTO.sOrderNumber)
	                   {
	                      PharmacyOrderWrapper_DTO_HUM.PharmacyOrderSummary_wrapper_HUM  wrapperObj = new PharmacyOrderWrapper_DTO_HUM.PharmacyOrderSummary_wrapper_HUM();
	                        if (string.isNotBlank(obj.OrderNumber)) wrapperObj.OrderNumber = obj.OrderNumber;
	                        if (string.isNotBlank(obj.OrderCreationDate)) wrapperObj.OrderCreationDate = obj.OrderCreationDate;
	                        if (string.isNotBlank(obj.OrderReleaseDate) && obj.OrderCreationDate != obj.OrderReleaseDate) wrapperObj.OrderReleaseDate = obj.OrderReleaseDate;
	                        if (string.isNotBlank(obj.OrderSource)) wrapperObj.OrderSource = obj.OrderSource;
	                        if (string.isNotBlank(obj.QueueName)) wrapperObj.QueueName = obj.QueueName;
	                        if (string.isNotBlank(obj.OrderTotalAmount)) wrapperObj.OrderTotalAmount = obj.OrderTotalAmount;
	                        if (string.isNotBlank(obj.OrderpayAmount)) wrapperObj.OrderpayAmount = obj.OrderpayAmount;
	                        if (string.isNotBlank(obj.OrderTaxAmount)) wrapperObj.OrderTaxAmount = obj.OrderTaxAmount;
	                        if (string.isNotBlank(obj.ExternalOrderKey)) wrapperObj.OCP = obj.ExternalOrderKey;
	                        If (String.isNotBlank(obj.OrderType))  wrapperObj.OrderType = obj.OrderType;
                            If (String.isNotBlank(obj.OrderStatusLiteral))  wrapperObj.OrderStatusLiteral = obj.OrderStatusLiteral ;
                            If (String.isNotBlank(obj.OrderStatus))  wrapperObj.OrderStatus = obj.OrderStatus ;
                            If (String.isNotBlank(obj.OrderShipDate))  wrapperObj.OrderType = obj.OrderShipDate ;
	                        objOrderWrapDTO.objOrderSummary = wrapperObj;
	                        break;
	                   }                       
                    }
                }
            }
        }
       return objOrderWrapDTO;
    }
    
       /**
     * Returns PharmacyOrderSummary_DTO_HUM  object that can then be utilized for sending the webservice request.
     * This method is to create the request Structure for GetOrderResponseservice .
     *
     * @param    
     * @return  PharmacyOrderSummary_DTO_HUM  request structure of the wrapper class RightSourceGetOrderRequest_DTO_HUM 
     */
    public static Boolean  checkCurrentQueue (RightSourceGetOrderResponse_DTO_HUM oResponseGetOrder , List<String> lstData)
    {
        if (oResponseGetOrder != null && oResponseGetOrder.GetOrdersReponse != null)
        {
            RightSourceGetOrderResponse_DTO_HUM.OrdersReponse orderRes = oResponseGetOrder.GetOrdersReponse;
            if (orderRes.Orders != null)
            {                
                RightSourceGetOrderResponse_DTO_HUM.OrdersDetail orderObj = orderRes.Orders;
                if (orderObj.Order != null && orderObj.Order.size() > 0)
                {  
                    for (RightSourceGetOrderResponse_DTO_HUM.OrderDetail obj: orderObj.Order)
                    {
                       if(obj.OrderNumber == lstData[0] && obj.QueueName == lstData[1])
	                   {
	                     return true;
	                   }                       
                    }
                }
            }
        }
       return false;
    }
    
     /**
     * Returns RSGetAdjustmentAndPaymentResponseDTO_HUM.PaymentDetails objectfor populating payment details.
     * This method is to create the data for PaymentDetails from GetAdjustmentAndPayment .
     *
     * @param   RSGetAdjustmentAndPaymentResponseDTO_HUM DTO Response 
     * @return  RSGetAdjustmentAndPaymentResponseDTO_HUM.PaymentDetails request structure of the wrapper class RSGetAdjustmentAndPaymentResponseDTO_HUM 
     */
    public static RightSourceGetOrderDetResponse_DTO_HUM.GetOrderDetailResponse prepareOrderDetails(RightSourceGetOrderDetResponse_DTO_HUM oResponseGetMember)
    {
		Boolean bSwitch = HUMUtilityHelper.isCRMFunctionalityON('760189');
        RightSourceGetOrderDetResponse_DTO_HUM.GetOrderDetailResponse oResponse = new RightSourceGetOrderDetResponse_DTO_HUM.GetOrderDetailResponse(); 
        if(oResponseGetMember != NULL && oResponseGetMember.GetOrderDetailResponse != NULL)
        {
            if(oResponseGetMember.GetOrderDetailResponse.OrderKey != NULL) oResponse.OrderKey = oResponseGetMember.GetOrderDetailResponse.OrderKey;
            if(oResponseGetMember.GetOrderDetailResponse.ExternalOrderKey != NULL) oResponse.ExternalOrderKey = oResponseGetMember.GetOrderDetailResponse.ExternalOrderKey;
            if(oResponseGetMember.GetOrderDetailResponse.OrderCreationDate != NULL) oResponse.OrderCreationDate = oResponseGetMember.GetOrderDetailResponse.OrderCreationDate;
            if(oResponseGetMember.GetOrderDetailResponse.OrderShipDate != NULL) oResponse.OrderShipDate = oResponseGetMember.GetOrderDetailResponse.OrderShipDate;
            if(oResponseGetMember.GetOrderDetailResponse.CurrentQueue != NULL) oResponse.CurrentQueue = oResponseGetMember.GetOrderDetailResponse.CurrentQueue;
            if(oResponseGetMember.GetOrderDetailResponse.CurrentQueueDate != NULL) oResponse.CurrentQueueDate = oResponseGetMember.GetOrderDetailResponse.CurrentQueueDate;
            if(oResponseGetMember.GetOrderDetailResponse.OrderSource != NULL) oResponse.OrderSource = oResponseGetMember.GetOrderDetailResponse.OrderSource;
            if(oResponseGetMember.GetOrderDetailResponse.CancelReasonCode != NULL) oResponse.CancelReasonCode = oResponseGetMember.GetOrderDetailResponse.CancelReasonCode;
            if(oResponseGetMember.GetOrderDetailResponse.CreditCardKey != NULL) oResponse.CreditCardKey = oResponseGetMember.GetOrderDetailResponse.CreditCardKey;
            if(oResponseGetMember.GetOrderDetailResponse.Queues != NULL) oResponse.Queues = oResponseGetMember.GetOrderDetailResponse.Queues;
            if(oResponseGetMember.GetOrderDetailResponse.ShippingInformation != NULL) oResponse.ShippingInformation = oResponseGetMember.GetOrderDetailResponse.ShippingInformation;
            if(oResponseGetMember.GetOrderDetailResponse.Addresses != NULL) oResponse.Addresses = oResponseGetMember.GetOrderDetailResponse.Addresses;
            if(oResponseGetMember.GetOrderDetailResponse.FirstName != NULL) oResponse.FirstName = oResponseGetMember.GetOrderDetailResponse.FirstName;
            if(oResponseGetMember.GetOrderDetailResponse.LastName != NULL) oResponse.LastName = oResponseGetMember.GetOrderDetailResponse.LastName;
            if(oResponseGetMember.GetOrderDetailResponse.OrderItems != NULL) oResponse.OrderItems = oResponseGetMember.GetOrderDetailResponse.OrderItems;
            if(oResponseGetMember.GetOrderDetailResponse.CreditCards != NULL) oResponse.CreditCards = oResponseGetMember.GetOrderDetailResponse.CreditCards;
            if(oResponseGetMember.GetOrderDetailResponse.StaleData != NULL) oResponse.StaleData = oResponseGetMember.GetOrderDetailResponse.StaleData;
          if(bSwitch)
            {
                RightSourceGetOrderDetResponse_DTO_HUM.Queues oQueues = new RightSourceGetOrderDetResponse_DTO_HUM.Queues(); 
                RightSourceGetOrderDetResponse_DTO_HUM.QueueDetails oQueueDet =  new RightSourceGetOrderDetResponse_DTO_HUM.QueueDetails();
                List<RightSourceGetOrderDetResponse_DTO_HUM.QueueDetail> lstQueueDetail = new List<RightSourceGetOrderDetResponse_DTO_HUM.QueueDetail>();
            
                oQueues = oResponse.Queues;
             if(oQueues!= null){ 
             for(RightSourceGetOrderDetResponse_DTO_HUM.Queue objQueue : oQueues.Queue)
                {
                    if(objQueue != null){   
                    oQueueDet = objQueue.QueueDetails;
                     if(oQueueDet!= null){
                    lstQueueDetail = oQueueDet.QueueDetail;
                     }
                    }
                } 
              }     
                if(oResponse.CurrentQueue == PHARMACY_CURRENT_QUEUE){ 
                  for(Integer i=0; i < lstQueueDetail.size(); i++){                  
                  if(string.isNotBlank(lstQueueDetail[i].exceptionIdentifier)){
                    if(!oResponse.exceptionIdentifier.Contains(lstQueueDetail[i].exceptionIdentifier)){
                        oResponse.exceptionIdentifier += lstQueueDetail[i].exceptionIdentifier +',' ; 
                    }
                  }
                 }

                }
            }
          
          
        }
        return oResponse;
    }
    /**
     * Returns RSCreateAndEditOrderRequest_DTO_HUM  object that can then be utilized for sending the webservice request.
     * This method is to create the request Structure for RSCreateAndEditOrder .
     *
     * @param    
     * @return  RSCreateAndEditOrderRequest_DTO_HUM  request structure of the wrapper class RSCreateAndEditOrderRequest_DTO_HUM  
     */
    public static RSCreateAndEditOrderRequest_DTO_HUM  requestStructureRSReleaseAndCancelOrder(String orderKey,String EnterpriseId, boolean isCancel, string networkID , boolean overrideconsent,boolean copayconsent,string lstScriptKey)
    {
       Boolean bSwitchERC  = HUMUtilityHelper.isCRMFunctionalityON('1491784');

      RSCreateAndEditOrderRequest_DTO_HUM oRequest = null;
       if(null != orderKey && null != EnterpriseId)
           {
            oRequest = new RSCreateAndEditOrderRequest_DTO_HUM ();
            RSCreateAndEditOrderRequest_DTO_HUM.CreateAndEditOrderRequest objCreateAndEditOrderRequest = new RSCreateAndEditOrderRequest_DTO_HUM.CreateAndEditOrderRequest();           
          
            objCreateAndEditOrderRequest.EnterprisePersonID =  EnterpriseId;  
            objCreateAndEditOrderRequest.CustomerServiceId = networkID;            
            objCreateAndEditOrderRequest.orderKey = orderKey;
            
            
            if(isCancel)
            {
                 objCreateAndEditOrderRequest.cancelOrder ='true';
                 objCreateAndEditOrderRequest.orderPlatform = PHARMACY_TRAD_HUM;          
            }
            else
            {
                if(bSwitchERC && overrideconsent){
                    objCreateAndEditOrderRequest.userDefined1 = lstScriptKey;
					RSCreateAndEditOrderRequest_DTO_HUM.ScriptOrders oScriptOrders = new RSCreateAndEditOrderRequest_DTO_HUM.ScriptOrders();
	                RSCreateAndEditOrderRequest_DTO_HUM.Script oScript = new RSCreateAndEditOrderRequest_DTO_HUM.Script();
	                oScriptOrders.script = new List<RSCreateAndEditOrderRequest_DTO_HUM.Script>();
                    if(copayconsent)
                    oScript.coPayConsent = copayconsent;
                    oScript.scriptKey = lstScriptKey;
                    oScriptOrders.script.add(oScript);
                    objCreateAndEditOrderRequest.scriptOrders = oScriptOrders;
                    objCreateAndEditOrderRequest.overrideQueueConsent = overrideconsent;
                }
                else{
                     RSCreateAndEditOrderRequest_DTO_HUM.MoveToRouting moveToRouting = new RSCreateAndEditOrderRequest_DTO_HUM.MoveToRouting();
                        moveToRouting.orderKey  = new List<String>();
                        moveToRouting.orderKey.add(orderKey);
                        objCreateAndEditOrderRequest.moveToRouting = moveToRouting;
                }
                
            }
            oRequest.CreateAndEditOrderRequest = objCreateAndEditOrderRequest;
          }
          return oRequest;
    }
}