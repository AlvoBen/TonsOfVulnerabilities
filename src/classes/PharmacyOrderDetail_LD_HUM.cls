/*******************************************************************************************************************************
Apex Class Name : PharmacyOrderDetail_LD_HUM.cls
Version         : 1.0
Created On      : 2021
Function        : Data Layer Class for PharmacyOrderDetail_LC_HUM.

Modification Log:
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Abhishek Mangutkar                                   10/19/2021                REQ 2674165 Original version 
* Monali Jagtap									       07/17/2023 		         USER STORY- 4812246 T1PRJ0891415 Platform Management-  SF - TECH - Critical- Lightning – Pharmacy                                                                

*********************************************************************************************************************************/
public with sharing class PharmacyOrderDetail_LD_HUM
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
        RightSourceGetOrderResponse_DTO_HUM.OrdersReponse orderRes = (oResponseGetOrder != null && oResponseGetOrder.GetOrdersReponse != null)?oResponseGetOrder.GetOrdersReponse:null;
        RightSourceGetOrderResponse_DTO_HUM.OrdersDetail orderObj=(orderRes.Orders != null)?orderRes.Orders:null;
        objOrderWrapDTO.objOrderSummary = (orderObj.Order != null && orderObj.Order.size() > 0)?
            getOrderSummary(orderObj,objOrderWrapDTO):null;
       return objOrderWrapDTO;
    }
    
    public static PharmacyOrderWrapper_DTO_HUM.PharmacyOrderSummary_wrapper_HUM getOrderSummary(RightSourceGetOrderResponse_DTO_HUM.OrdersDetail orderObj,PharmacyOrderWrapper_DTO_HUM objOrderWrapDTO)
    {

         PharmacyOrderWrapper_DTO_HUM.PharmacyOrderSummary_wrapper_HUM  wrapperObj;
         for (RightSourceGetOrderResponse_DTO_HUM.OrderDetail obj: orderObj.Order)
            {
                if(obj.OrderNumber == objOrderWrapDTO.sOrderNumber)
                {
					wrapperObj = new PharmacyOrderWrapper_DTO_HUM.PharmacyOrderSummary_wrapper_HUM();
                    wrapperObj.OrderNumber = (string.isNotBlank(obj.OrderNumber))?obj.OrderNumber: ' ';
                    wrapperObj.OrderCreationDate = (string.isNotBlank(obj.OrderCreationDate))?obj.OrderCreationDate: ' ';
                    wrapperObj.OrderReleaseDate = (string.isNotBlank(obj.OrderReleaseDate) && obj.OrderCreationDate != obj.OrderReleaseDate)? obj.OrderReleaseDate: ' ';
                    wrapperObj.OrderSource = (string.isNotBlank(obj.OrderSource))? obj.OrderSource: ' ';
                    wrapperObj.QueueName = (string.isNotBlank(obj.QueueName))? obj.QueueName: ' ';
                    wrapperObj.OrderTotalAmount = (string.isNotBlank(obj.OrderTotalAmount))?obj.OrderTotalAmount: ' ';
                    wrapperObj.OrderpayAmount = (string.isNotBlank(obj.OrderpayAmount))?obj.OrderpayAmount: ' ';
                    wrapperObj.OrderTaxAmount = (string.isNotBlank(obj.OrderTaxAmount))?obj.OrderTaxAmount: ' ';
                    wrapperObj.OCP = (string.isNotBlank(obj.ExternalOrderKey))?obj.ExternalOrderKey: ' ';
                    wrapperObj.OrderType = (String.isNotBlank(obj.OrderType))?obj.OrderType: ' ';
                    wrapperObj.OrderStatusLiteral = (String.isNotBlank(obj.OrderStatusLiteral))?obj.OrderStatusLiteral: ' ';
                    wrapperObj.OrderStatus = (String.isNotBlank(obj.OrderStatus))?obj.OrderStatus: ' ' ;
                    wrapperObj.OrderType = (String.isNotBlank(obj.OrderShipDate))?obj.OrderShipDate: ' ' ;
                    objOrderWrapDTO.objOrderSummary = wrapperObj;
                    break;
                }
            }
        return wrapperObj;
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
        RightSourceGetOrderDetResponse_DTO_HUM.GetOrderDetailResponse oResponse = new RightSourceGetOrderDetResponse_DTO_HUM.GetOrderDetailResponse();             oResponse.OrderKey = (oResponseGetMember != NULL && oResponseGetMember.GetOrderDetailResponse != NULL && oResponseGetMember.GetOrderDetailResponse.OrderKey != NULL)?oResponseGetMember.GetOrderDetailResponse.OrderKey:null;
             oResponse.ExternalOrderKey = (oResponseGetMember != NULL && oResponseGetMember.GetOrderDetailResponse != NULL && oResponseGetMember.GetOrderDetailResponse.ExternalOrderKey != NULL) ?oResponseGetMember.GetOrderDetailResponse.ExternalOrderKey:null;
             oResponse.OrderCreationDate =(oResponseGetMember != NULL && oResponseGetMember.GetOrderDetailResponse != NULL && oResponseGetMember.GetOrderDetailResponse.OrderCreationDate != NULL) ? oResponseGetMember.GetOrderDetailResponse.OrderCreationDate:null;
             oResponse.OrderShipDate = (oResponseGetMember != NULL && oResponseGetMember.GetOrderDetailResponse != NULL && oResponseGetMember.GetOrderDetailResponse.OrderShipDate != NULL)?oResponseGetMember.GetOrderDetailResponse.OrderShipDate:null;
             oResponse.CurrentQueue = (oResponseGetMember != NULL && oResponseGetMember.GetOrderDetailResponse != NULL && oResponseGetMember.GetOrderDetailResponse.CurrentQueue != NULL)?oResponseGetMember.GetOrderDetailResponse.CurrentQueue:null;
             oResponse.CurrentQueueDate =(oResponseGetMember != NULL && oResponseGetMember.GetOrderDetailResponse != NULL && oResponseGetMember.GetOrderDetailResponse.CurrentQueueDate != NULL)? oResponseGetMember.GetOrderDetailResponse.CurrentQueueDate:null;
             oResponse.OrderSource =(oResponseGetMember != NULL && oResponseGetMember.GetOrderDetailResponse != NULL && oResponseGetMember.GetOrderDetailResponse.OrderSource != NULL)? oResponseGetMember.GetOrderDetailResponse.OrderSource:null;
             oResponse.CancelReasonCode =(oResponseGetMember != NULL && oResponseGetMember.GetOrderDetailResponse != NULL && oResponseGetMember.GetOrderDetailResponse.CancelReasonCode != NULL)? oResponseGetMember.GetOrderDetailResponse.CancelReasonCode:null;
             oResponse.CreditCardKey =(oResponseGetMember != NULL && oResponseGetMember.GetOrderDetailResponse != NULL && oResponseGetMember.GetOrderDetailResponse.CreditCardKey != NULL)? oResponseGetMember.GetOrderDetailResponse.CreditCardKey:null;
             oResponse.Queues =(oResponseGetMember != NULL && oResponseGetMember.GetOrderDetailResponse != NULL && oResponseGetMember.GetOrderDetailResponse.Queues != NULL)? oResponseGetMember.GetOrderDetailResponse.Queues:null;
             oResponse.ShippingInformation =(oResponseGetMember != NULL && oResponseGetMember.GetOrderDetailResponse != NULL && oResponseGetMember.GetOrderDetailResponse.ShippingInformation != NULL)? oResponseGetMember.GetOrderDetailResponse.ShippingInformation:null;
             oResponse.Addresses =(oResponseGetMember != NULL && oResponseGetMember.GetOrderDetailResponse != NULL && oResponseGetMember.GetOrderDetailResponse.Addresses != NULL)? oResponseGetMember.GetOrderDetailResponse.Addresses:null;
             oResponse.FirstName = (oResponseGetMember != NULL && oResponseGetMember.GetOrderDetailResponse != NULL && oResponseGetMember.GetOrderDetailResponse.FirstName != NULL)?oResponseGetMember.GetOrderDetailResponse.FirstName:null;
             oResponse.LastName = (oResponseGetMember != NULL && oResponseGetMember.GetOrderDetailResponse != NULL && oResponseGetMember.GetOrderDetailResponse.LastName != NULL)?oResponseGetMember.GetOrderDetailResponse.LastName:null;
             oResponse.OrderItems = (oResponseGetMember != NULL && oResponseGetMember.GetOrderDetailResponse != NULL && oResponseGetMember.GetOrderDetailResponse.OrderItems != NULL)?oResponseGetMember.GetOrderDetailResponse.OrderItems:null;
             oResponse.CreditCards =(oResponseGetMember != NULL && oResponseGetMember.GetOrderDetailResponse != NULL && oResponseGetMember.GetOrderDetailResponse.CreditCards != NULL)? oResponseGetMember.GetOrderDetailResponse.CreditCards:null;
             oResponse.StaleData = (oResponseGetMember != NULL && oResponseGetMember.GetOrderDetailResponse != NULL && oResponseGetMember.GetOrderDetailResponse.StaleData != NULL)?oResponseGetMember.GetOrderDetailResponse.StaleData:null;
          if(bSwitch)
            {
                RightSourceGetOrderDetResponse_DTO_HUM.Queues oQueues = new RightSourceGetOrderDetResponse_DTO_HUM.Queues(); 
                RightSourceGetOrderDetResponse_DTO_HUM.QueueDetails oQueueDet =  new RightSourceGetOrderDetResponse_DTO_HUM.QueueDetails();
                List<RightSourceGetOrderDetResponse_DTO_HUM.QueueDetail> lstQueueDetail = new List<RightSourceGetOrderDetResponse_DTO_HUM.QueueDetail>();
            
                oQueues = oResponse.Queues;
             if(oQueues!= null){ 
             for(RightSourceGetOrderDetResponse_DTO_HUM.Queue objQueue : oQueues.Queue)
                {
                  
                    oQueueDet = (objQueue != null)?objQueue.QueueDetails:null;
                    lstQueueDetail = ((objQueue != null)&&(oQueueDet!= null))?oQueueDet.QueueDetail:null;
                } 
              }     
            if(oResponse.CurrentQueue == PHARMACY_CURRENT_QUEUE)
            {  for(Integer i=0; i < lstQueueDetail.size(); i++)
                  {                  
                 oResponse.exceptionIdentifier =((string.isNotBlank(lstQueueDetail[i].exceptionIdentifier)) && (!oResponse.exceptionIdentifier.Contains(lstQueueDetail[i].exceptionIdentifier)))? (oResponse.exceptionIdentifier+lstQueueDetail[i].exceptionIdentifier +',') :''; 
               }
            }
          
          
        }
        return oResponse;
    }    
}