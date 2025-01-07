/*******************************************************************************************************************************
Apex Class Name : Pharmacy_CreateEditOrder_D_HUM.cls
Version         : 1.0
Created On      : 2017
Function        : Data Layer Class for Pharmacy_CreateEditOrder_C_HUM.

Modification Log:
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Rajesh Chandgothia                                   04/18/2017                REQ 305689 Original version 
* Sharan Shanmugam									   07/27/2017				 REQ - 323542 : Exception handling
* Sumit Pharande            345615                     03/27/2019                   REQ - 362063 - Payment Card Project: Do Not allow users to inactivate a cc that's attached to an order in progress
* Pinky Vijur                                  05/11/2021                  REQ 2230065: T1PRJ0002606 -2021 MF1- SF - Humana Constants Cleanup Humana Pharmacy
*********************************************************************************************************************************/

public with sharing class Pharmacy_CreateEditOrder_D_HUM
{
   Public Static final String PHARMACY_MEMBERCONSENT_LOGNOTECODE_HUM = 'CONSENT';
   Public Static final String PHARMACY_MEMBERCONSENT_LOGNOTEMESSAGEFIRSTHALF_HUM ='Member consent given on ';
   Public Static final String PHARMACY_MEMBERCONSENT_LOGNOTEMESSAGESECONDHALF_HUM = ' est. Consent will expire in 365 days (Note EPOST WILL HANDLE CONSENT EXPIRATION)';
   Public Static final String PHARMACY_MEMBERCONSENT_LOGNOTEAT_HUM = ' at '; 

 	/**
     * Returns RightSourceGetMemberResponse_DTO_HUM  object that can then be utilized for sending the webservice request.
     * This method Is to create the request Structure for GetMemberResponseservice .
     *
     * @param    
     * @return  RightSourceGetMemberResponse_DTO_HUM  request structure of the wrapper class RightSourceGetMemberResponse_DTO_HUM 
     */
    public static RightSourceGetMemberRequest_DTO_HUM RequestStructureGetMember(String memberID, string networkID)
    {
        RightSourceGetMemberRequest_DTO_HUM oMemRequest = new RightSourceGetMemberRequest_DTO_HUM();
        RightSourceGetMemberRequest_DTO_HUM.MemberRequest getmemRequest = new RightSourceGetMemberRequest_DTO_HUM.MemberRequest();
        getmemRequest.EnterprisePersonID = memberID;  
        getmemRequest.CustomerServiceId = networkID;
        getmemRequest.IncludeActiveLogNotes ='true';
        getmemRequest.IncludeActiveAddresses = 'true';
        getmemRequest.IncludeInactiveAddresses = 'true';
        getmemRequest.IncludeFinances = 'true';
        getmemRequest.IncludeAccountAdjustments = 'false';
        getmemRequest.IncludeConsentStatus = 'true';
        getmemRequest.IncludeActiveCreditCards = 'true';
        getmemRequest.IncludeInactiveCreditCards = 'false';
        getmemRequest.IncludeStaleData = 'true';
        oMemRequest.GetMemberRequest = getmemRequest ;
        return oMemRequest;
    }
    
    /**
     * Returns RightSourceEditMemberRequest_DTO_HUM  object that can then be utilized for sending the webservice request.
     * This method is to create the request Structure for GeEditMemberResponseservice .
     *
     * @param    
     * @return  RightSourceEditMemberRequest_DTO_HUM  request structure of the wrapper class RightSourceEditMemberRequest_DTO_HUM 
     */
    public static RightSourceEditMemberRequest_DTO_HUM RequestStructureGetEditMemberForScripts(Integer numberOfConsentGiven, String sMemId, string networkID)
    {
        String userNetworkId = networkID;
        RightSourceEditMemberRequest_DTO_HUM.logNoteDetail logDetail;
        RightSourceEditMemberRequest_DTO_HUM oRequest = new RightSourceEditMemberRequest_DTO_HUM();
        RightSourceEditMemberRequest_DTO_HUM.GetEditMemberRequest getEditMembReqObj = new RightSourceEditMemberRequest_DTO_HUM.GetEditMemberRequest();
        RightSourceEditMemberRequest_DTO_HUM.memberDetail memDetailObj = new RightSourceEditMemberRequest_DTO_HUM.memberDetail();
        RightSourceEditMemberRequest_DTO_HUM.customerDetail custDetail = new RightSourceEditMemberRequest_DTO_HUM.customerDetail();
        RightSourceEditMemberRequest_DTO_HUM.logNoteDetails logNoteDetails = new RightSourceEditMemberRequest_DTO_HUM.logNoteDetails();
        List<RightSourceEditMemberRequest_DTO_HUM.logNoteDetail> logDetailList = new  List<RightSourceEditMemberRequest_DTO_HUM.logNoteDetail>();
        memDetailObj.EnterprisePersonID = sMemId;
        for(Integer i=0; i<numberOfConsentGiven; i++) 
        {
            logDetail = new RightSourceEditMemberRequest_DTO_HUM.logNoteDetail();
            logDetail.logNoteCode = PHARMACY_MEMBERCONSENT_LOGNOTECODE_HUM;
            logDetail.logNoteKey ='-1';
            logDetail.logNoteMessage =  PHARMACY_MEMBERCONSENT_LOGNOTEMESSAGEFIRSTHALF_HUM + formatRequestDate(System.today()) + PHARMACY_MEMBERCONSENT_LOGNOTEAT_HUM + getCurrentTime() + PHARMACY_MEMBERCONSENT_LOGNOTEMESSAGESECONDHALF_HUM ;
            if(userNetworkId != null)
                logDetail.logNoteUser = userNetworkId;
            logDetailList.add(logDetail);
    	}
        logNoteDetails.logNote = logDetailList;
        memDetailObj.logNotes = logNoteDetails;
        getEditMembReqObj.members = new List<RightSourceEditMemberRequest_DTO_HUM.memberDetail>();
        getEditMembReqObj.CustomerServiceId = userNetworkId;
        getEditMembReqObj.members.add(memDetailObj);
        oRequest.EditMemberRequest = getEditMembReqObj;        
        return oRequest;
    }
    
    /**
     * Returns the formatted string by parsing the date 
     *
     * @param   Date 
     * @return  Formatted String Date 
     */
    private static String formatRequestDate(Date requestDate)
    {
        return requestDate.Month() +'/' + requestDate .Day() + '/' + requestDate.Year();    
    }
    /**
     * Returns the formatted current time 
     *
     * @param   None
     * @return  Formatted current time 
     */
    private static String getCurrentTime()
    {
        Datetime currentDateTime = Datetime.now();
        String currentTime = currentDateTime.format('h:mm a');
        return currentTime;    
    }
	
	/**
     * Returns RightSourceGetOrderResponse_DTO_HUM object that can then be utilized for sending the webservice request.
     * This method is to create the request Structure for GetOrderResponseservice .
     *
     * @param    
     * @return  RightSourceGetOrderResponse_DTO_HUM request structure of the wrapper class RightSourceGetOrderResponse_DTO_HUM 
     */
    Public Static RightSourceGetOrderRequest_DTO_HUM requestStructureGetOrderResponse(String memID,String networkId)
    {
        RightSourceGetOrderRequest_DTO_HUM oRequest = new RightSourceGetOrderRequest_DTO_HUM();
        RightSourceGetOrderRequest_DTO_HUM.OrdersRequest orderRequest = new RightSourceGetOrderRequest_DTO_HUM.OrdersRequest();
        orderRequest.EnterprisePersonID = memID; 
        orderRequest.IncludeStaleData ='true';
        orderRequest.CustomerServiceId = networkId;
        oRequest.GetOrdersRequest = orderRequest;
        return oRequest;
    }
    
    /**
     * Returns the PharmacyOrderSummary_DTO_HUM class Object
     *
     * @param   RightSourceGetOrderResponse_DTO_HUM 
     * @return  PharmacyOrderSummary_DTO_HUM  
     */
    public static PharmacyOrderSummary_DTO_HUM prepareOrderSummaryResponse(RightSourceGetOrderResponse_DTO_HUM oResponseGetOrder)
    {
        PharmacyOrderSummary_DTO_HUM orderObject ; 
        list < PharmacyOrderSummary_DTO_HUM.PharmacyOrderSummary_wrapper_HUM  > wrapperObjList =  new list < PharmacyOrderSummary_DTO_HUM.PharmacyOrderSummary_wrapper_HUM  > ();
        PharmacyOrderSummary_DTO_HUM.PharmacyOrderSummary_wrapper_HUM wrapperObj;
        if (oResponseGetOrder != null && oResponseGetOrder.GetOrdersReponse != null)
        {
            orderObject = new PharmacyOrderSummary_DTO_HUM(); 
            RightSourceGetOrderResponse_DTO_HUM.OrdersReponse orderRes = oResponseGetOrder.GetOrdersReponse;
            if (orderRes.Orders != null)
            {
                RightSourceGetOrderResponse_DTO_HUM.OrdersDetail orderObj = orderRes.Orders;
                if (string.isNotBlank(orderRes.StaleData)) orderObject.staleData = orderRes.StaleData;
                if (orderObj.Order != null && orderObj.Order.size() > 0)
                {
                    for (RightSourceGetOrderResponse_DTO_HUM.OrderDetail obj: orderObj.Order)
                    {
                        wrapperObj = new PharmacyOrderSummary_DTO_HUM.PharmacyOrderSummary_wrapper_HUM();
                        if (String.isNotBlank(obj.OrderNumber)) wrapperObj.OrderNumber = obj.OrderNumber;
                        if (String.isNotBlank(obj.OrderCreationDate)) wrapperObj.OrderCreationDate = obj.OrderCreationDate;
                        if (String.isNotBlank(obj.OrderReleaseDate) && obj.OrderCreationDate != obj.OrderReleaseDate) wrapperObj.OrderReleaseDate = obj.OrderReleaseDate;
                        if (String.isNotBlank(obj.OrderSource)) wrapperObj.OrderSource = obj.OrderSource;
                        if (String.isNotBlank(obj.QueueName)) wrapperObj.QueueName = obj.QueueName;
                        if (String.isNotBlank(obj.OrderTotalAmount)) wrapperObj.OrderTotalAmount = obj.OrderTotalAmount;
                        if (String.isNotBlank(obj.OrderpayAmount)) wrapperObj.OrderpayAmount = obj.OrderpayAmount;
                        if (String.isNotBlank(obj.OrderTaxAmount)) wrapperObj.OrderTaxAmount = obj.OrderTaxAmount;
                        if (String.isNotBlank(obj.ExternalOrderKey)) wrapperObj.OCP = obj.ExternalOrderKey;
                        if (String.isNotBlank(obj.OrderType)) wrapperObj.OrderType = obj.OrderType;
                        if (String.isNotBlank(obj.OrderStatusLiteral)) wrapperObj.OrderStatusLiteral = obj.OrderStatusLiteral;
                        if (String.isNotBlank(obj.OrderStatus)) wrapperObj.OrderStatus = obj.OrderStatus;
                        if (String.isNotBlank(obj.OrderShipDate)) wrapperObj.OrderShipDate = obj.OrderShipDate;
                        wrapperObjList.add(wrapperObj);
                    }
                }
            }
        }
       orderObject.dtoList =  wrapperObjList;
       
       return orderObject;
    }
    
    
    
    /**
     * Returns the Get Members Details
     *
     * @param   RightSourceGetMemberResponse_DTO_HUM
     * @return  PharmacyGetMember_Finance_DTO_HUM
     */
    public static PharmacyGetMember_Finance_DTO_HUM prepareGetMember(RightSourceGetMemberResponse_DTO_HUM objMemberRseponse)
    {
        PharmacyGetMember_Finance_DTO_HUM getMemberObject= new PharmacyGetMember_Finance_DTO_HUM();
        if(null != objMemberRseponse &&  null !=  objMemberRseponse.GetMemberReponse)
        {
            if(null != objMemberRseponse &&  null !=  objMemberRseponse.GetMemberReponse && null !=  objMemberRseponse.GetMemberReponse.Customers && null != objMemberRseponse.GetMemberReponse.Customers.Customer && !objMemberRseponse.GetMemberReponse.Customers.Customer.isEmpty() )
            {
                 RightSourceGetMemberResponse_DTO_HUM.CustomerDetail objCustomer = objMemberRseponse.GetMemberReponse.Customers.Customer[0];
                 if (null != objCustomer && null != objCustomer.StaleData)
                 {
                     getMemberObject.StaleData =objCustomer.StaleData; 
                 }
                 if (null != objCustomer && null != objCustomer.Plans && null != objCustomer.Plans.plan && !objCustomer.Plans.plan.isEmpty())
                  {
                      RightSourceGetMemberResponse_DTO_HUM.PlanDetail custPlan = objCustomer.Plans.Plan[0];
                      if (null != custPlan && null != custPlan.Payer )
                      {
                        getMemberObject.Payer =custPlan.Payer;
                      }
                  }
              if (objCustomer.CustomerPreference != null)
               {
                  RightSourceGetMemberResponse_DTO_HUM.CustomerPreference custvarientDetail = objCustomer.CustomerPreference;
                  if (string.isNotBlank(custvarientDetail.CapType)) getMemberObject.CapType = custvarientDetail.CapType;
               }
            }
        }
       return getMemberObject ;
    }
}