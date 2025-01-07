/*******************************************************************************************************************************
Apex Class Name : PharmacyFinancial_S_HUM.cls
Version         : 1.0
Created On      : 2 February 2016
Function        : Service Class for PharmacyFinancial_C_HUM.

Modification Log:
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Sharan Shanmugam			70274    		  		02/16/2017					 Original version - Implementation of REQ - 296663 View credit card information from getMember details 
* Vishal Verma				70274					02/20/2017					 Implementation of REQ - 296664 : View payment information from GetMemberFinanceAndAdjustments details
* Vishal Verma				74192			        08/03/2017					 Implementation of REQ - 296665 : View adjustment information from GetMemberFinanceAndAdjustments details
* Sharan Shanmugam                               	03/21/2017                   REQ - 296877 - Update Credit Card information	
* Vishal Verma              82890                   04/07/2017                   REQ - 301358 : Send Email for financial request
* Rajesh Keswani            112036	                07/20/2017                   Implementation of REQ - 323389 - adding networkId in service request
* Sharan Shanmugam									07/27/2017					 REQ - 323542 : Exception handling 
* Pradeep Dani				302363					08/17/2018				     DTO constructor update
* Melkisan Selvaraj			341652	    			03/04/2019                   REQ - 387544 : Splunk error logging Issues
* Melkisan Selvaraj			343971	    			03/13/2019                   Defect# 406145.0001 - Finance page defect fix
* Sumit Pharande            345615                  03/27/2019                   REQ - 362063 - Payment Card Project: do not allow users to inactivate a cc that’s attached to an order in progress
* Yogesh Gupta								 		05/28/2019				     REQ - 400046 Add error Message for member not found
* Shailesh Mali                                     08/26/2020                   REQ - 1152078 - PR00094920- MF 1  - Humana Pharmacy- Ability to create realtime one time credit card charges in CRM  (RxXP) (ID# 13)
* Lavanya Rao				 							10/11/2021                 User Story 2731234: T1PRJ0002606- 2021 MF6 - Swat-SF-Tech-HumConstant and Global Constant Fix
*********************************************************************************************************************************/
public with sharing class PharmacyFinancial_S_HUM
{
	Public Static final String HUM_RSGETMEMBERCREDITCARD_RESPONSE = 'parseRSGetMemberCreditResponse'; 
	Public Static final String HUM_RSGETADJPAY_RESPONSE = 'parseGetAdjPayResponse';  
	Public Static final Integer HTTP_OK_HUM = 200;
	Public Static final Integer HTTP_ACCEPTED_HUM = 202; 
	Public Static final String VOB_FAULT_CODE = 'faultcode';
	public static final string HUM_MEMBER_NOT_FOUND = 'Member not found';
	Public Static final String Content_type_HUM = 'content-type';
	Public Static final String Connection_HUM = 'Connection';
	Public Static final String Keep_alive_HUM = 'keep-alive';
	Public Static final String SOAPAction_HUM = 'SOAPAction';
	Public Static final String PHARMACY_GETEDITMEMBER_HUM = 'retrieveEditMember';
	Public Static final String CREDITCARD_ERROR_SIX ='CARD10006-Non-Preferred card cannot be updated';
	Public Static final String CREDITCARD_ERROR_FIVE ='CARD10005-Preferred card cannot be updated';
	Public Static final String HUM_GETORDERRES = 'parseRSGetOrderResponse';  
    String orderKey ='';
    /**
     * Returns continuation object
     * <p>
     * Method to call continuation serive for the serive for GetMemberResponse services .
     *
     * @param   String          Enterprise Id
     * @param   String          networkID
     * @param   String          sRecordId
     * @return  Continuation
     */
    public static Continuation SendRequestGetMemberResponse(String sMemID, string networkID, string sRecordId)
    {
        try
        {
            HUM_Webservice_Callout__c RSServiceDetails = HUM_Webservice_Callout__c.getValues('RighSourceService_GetMember');
            // Create continuation with a timeout
            integer iTIMEOUT_INT_SECS = Integer.valueof(RSServiceDetails.Timeout__c) / 1000;
            Continuation con = new Continuation(iTIMEOUT_INT_SECS);
            con.continuationMethod = HUM_RSGETMEMBERCREDITCARD_RESPONSE;  
            con.state = sRecordId;  
            HttpRequest httpRSGetMemberReqObj = builtHttpRequest(RSServiceDetails);
            httpRSGetMemberReqObj.setBody(JSON.serialize(PharmacyFinancial_D_HUM.RequestStructureGetMemberResponse(sMemID, networkID)));
            con.addHttpRequest(httpRSGetMemberReqObj);
            return con;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'PharmacyFinancial_S_HUM', 'SendRequestGetMemberResponse');
            return null;
        }
    }
    
    /**
     * Returns continuation object
     * <p>
     * Method to call continuation serive for the serive for getAdjustmentsAndPayments services .
     *
     * @param memID                         String
     * @param sStartDate                    String
     * @param sEndDate                      String
     * @param sPayment                      String
     * @param sAdjustment                   String
     * @param networkID                     String
     * @param sRecordId                     String
     * @return                              continuation
     */
    public static Continuation SendRequestGetMemberResponse(String sMemID , String sStartDate , String sEndDate , String sPayment , String sAdjustment, string networkID , String sRecordId)
    {
        try
        {
            HUM_Webservice_Callout__c RSServiceDetails = HUM_Webservice_Callout__c.getValues('RS_GetAdjustmentsAndPayments');
            // Create continuation with a timeout
            integer iTIMEOUT_INT_SECS = Integer.valueof(RSServiceDetails.Timeout__c) / 1000;
            Continuation con = new Continuation(iTIMEOUT_INT_SECS);
            con.continuationMethod = HUM_RSGETADJPAY_RESPONSE; 
            con.state = sRecordId;
            HttpRequest httpRSGetMemberReqObj = builtHttpRequest(RSServiceDetails);
            httpRSGetMemberReqObj.setBody(JSON.serialize(PharmacyFinancial_D_HUM.RequestStructureGetAdjPay(sMemID,sStartDate,sEndDate,sPayment,sAdjustment,networkID)));
            con.addHttpRequest(httpRSGetMemberReqObj);
            return con;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'PharmacyFinancial_S_HUM', 'SendRequestGetMemberResponse');
            return null;
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
    public static Object parseGetAdjPayResponse(List < string > labels, Object State)
    {
        try
        {
            RSGetAdjustmentAndPaymentResponseDTO_HUM.AdjustmentsAndPaymentsResponse oResponse = new RSGetAdjustmentAndPaymentResponseDTO_HUM.AdjustmentsAndPaymentsResponse();
            
            HttpResponse AdjustmentPaymentResponse = Continuation.getResponse(labels[0]);
            if (AdjustmentPaymentResponse != null && ((AdjustmentPaymentResponse.getStatusCode() == HTTP_OK_HUM || AdjustmentPaymentResponse.getStatusCode() == HTTP_ACCEPTED_HUM) && (!AdjustmentPaymentResponse.getBody().containsIgnoreCase(VOB_FAULT_CODE)) || Test.isRunningTest()))
            {
                oResponse = PharmacyFinancial_D_HUM.preparePaymentDetails((RSGetAdjustmentAndPaymentResponseDTO_HUM)System.JSON.deserialize(AdjustmentPaymentResponse.getBody(),RSGetAdjustmentAndPaymentResponseDTO_HUM.class));
                if (null != oResponse && null != oResponse.Adjustments && null != oResponse.Adjustments.ErrorDescription && oResponse.Adjustments.ErrorDescription == HUM_MEMBER_NOT_FOUND)
                    oResponse = new RSGetAdjustmentAndPaymentResponseDTO_HUM.AdjustmentsAndPaymentsResponse();
            }
            else
            {
            	// Fault Code
            	Pharmacy_H_HUM.logHpFaultError(AdjustmentPaymentResponse,'PharmacyFinancial_S_HUM', 'parseGetAdjPayResponse', State != null ? String.valueof(State) : '');
            }
			return oResponse;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'PharmacyFinancial_S_HUM', 'parseGetAdjPayResponse');
            return null;
        }
        
    }
    
    /**
     * Returns responses of continuation object
     * <p>
     * parseRSGetMemberCreditResponse Method to parse the service response
     *
     * @param  object state,List<string> labels .
     * @return Object
     */
    public static Object parseRSGetMemberCreditResponse(List < string > labels, Object State)
    {
        try
        {
            PharmacyGetMember_DTO_HUM objPharGetMemDTO = null;
            PharmacyGetMember_CreditCard_DTO_HUM objPharCreditCardDetails;
            HttpResponse responseCreditCard = Continuation.getResponse(labels[0]);
            
            if (responseCreditCard != null && ((responseCreditCard.getStatusCode() == HTTP_OK_HUM || responseCreditCard.getStatusCode() == HTTP_ACCEPTED_HUM) && (!responseCreditCard.getBody().containsIgnoreCase(VOB_FAULT_CODE)) || Test.isRunningTest()))
            {
                String CreditCardresposeBody = responseCreditCard.getBody();
                RightSourceGetMemberResponse_DTO_HUM oResponseCreditCard;
                oResponseCreditCard = (RightSourceGetMemberResponse_DTO_HUM) System.JSON.deserialize(CreditCardresposeBody, RightSourceGetMemberResponse_DTO_HUM.class);
                objPharCreditCardDetails = PharmacyFinancial_D_HUM.prepareCreditCardDetails(oResponseCreditCard);
                //Passing new parameter for show hide error message if member not found
				objPharGetMemDTO = new PharmacyGetMember_DTO_HUM(null,null,null, objPharCreditCardDetails,null,null,null,false);
            }
            else
            {
            	// Fault Code
            	Pharmacy_H_HUM.logHpFaultError(responseCreditCard,'PharmacyFinancial_S_HUM', 'parseRSGetMemberCreditResponse', State != null ? String.valueof(State) : '');
            }
            return objPharGetMemDTO;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'PharmacyFinancial_S_HUM', 'parseRSGetMemberCreditResponse');
            return null;
        }
    }
    
    /**
     * Returns HttpRequest request with request body
     * <p>
     * Method to prepare httpRequest details and certification details
     *
     * @param  HUM_Webservice_Callout__c record .
     * @return HttpRequest
     */
    public static HttpRequest builtHttpRequest(HUM_Webservice_Callout__c ServiceObj)
    {
        HttpRequest request = new HttpRequest();
        if (ServiceObj != null && ServiceObj.Active__c)
        {
            request.setMethod(ServiceObj.Request_Type__c);
            if (!Test.isRunningTest() && String.isNotBlank(ServiceObj.Certificate_Name__c)) request.setClientCertificateName(ServiceObj.Certificate_Name__c);
            if (String.isNotBlank(ServiceObj.Content_Type__c)) request.setHeader(Content_type_HUM, ServiceObj.Content_Type__c);
            request.setHeader(Connection_HUM, Keep_alive_HUM);
            request.setEndpoint(ServiceObj.End_Point_URL__c);
            request.setHeader(SOAPAction_HUM,'');
        }
        return request;
    } 
    
    /**
     * Returns continuation object
     * <p>
     * Method to call continuation serive for the serive for EditMemberResponse services .
     *
     * @param                      parameters for edit member service
     * @return                     continuation
     */
    public static continuation prepareEditGetMember(string memID, String sCreditCardKey, String exMonth, String exYear, String sActive, String sAutoCharge, String sFSA, String sFirstName, String sMiddleName, String sLastName, String sTokenKey, String sCreditType, String isInsert, string networkID, String sRecordId)
    {
        HUM_Webservice_Callout__c serviceDetails = HUM_Webservice_Callout__c.getValues('RighSourceService_EditMember');   
        // Create continuation with a timeout
        integer iTIMEOUT_INT_SECS = Integer.valueof(serviceDetails.Timeout__c) / 1000;
        Continuation con = new Continuation(iTIMEOUT_INT_SECS);
        con.continuationMethod = PHARMACY_GETEDITMEMBER_HUM;
        HttpRequest httpEditGetMemberReqObj = builtHttpRequest(serviceDetails);
        httpEditGetMemberReqObj.setBody(JSON.serialize(PharmacyFinancial_D_HUM.RequestStructureGetEditMemberResponse(memID, sCreditCardKey, exMonth, exYear, sActive, sAutoCharge, sFSA, sFirstName, sMiddleName, sLastName, sTokenKey, sCreditType, isInsert, networkID)));
        con.state = memID+'_'+networkID+'_'+sRecordId;
        con.addHttpRequest(httpEditGetMemberReqObj);
        return con;
    }
    
    /**
     * Returns responses of continuation object
     * <p>
     * retrieveEditMember Method to parse the service response
     *
     * @param  object state,List<string> labels .
     * @return Object
     */
    public static object retrieveEditMember(List <string> labels, object State)
    {
        PharmacyGetMember_DTO_HUM objPharGetMemDTO = null;
        HttpResponse responseEditMember = Continuation.getResponse(labels[0]);
        String editMemberResponseBody = responseEditMember.getBody();
        RightSourceEditMemberResponse_DTO_HUM oResponse;
        oResponse = (RightSourceEditMemberResponse_DTO_HUM) System.JSON.deserialize(editMemberResponseBody , RightSourceEditMemberResponse_DTO_HUM.class);
        string stateValues = string.valueOf(state);
        string[] stateValue = stateValues.split('_');
        string memId = stateValue[0];
        string networkID = stateValue[1];
        string recordId = stateValue[2];	
        Continuation conObj = null;
        if (!editMemberResponseBody.containsIgnoreCase(VOB_FAULT_CODE) && responseEditMember != null && ((responseEditMember.getStatusCode() == HTTP_OK_HUM || responseEditMember.getStatusCode() == HTTP_ACCEPTED_HUM) && (!responseEditMember.getBody().containsIgnoreCase(VOB_FAULT_CODE)) || Test.isRunningTest()))
        {
            if(oResponse.EditMemberResponse !=null && oResponse.EditMemberResponse.Orders !=null && oResponse.EditMemberResponse.Orders.Order !=null ){
                if((oResponse.EditMemberResponse.Orders.Order.Size()>0)&&((oResponse.EditMemberResponse.Error == CREDITCARD_ERROR_SIX) ||(oResponse.EditMemberResponse.Error == CREDITCARD_ERROR_FIVE)))
                {
                     PharmacyEditMember_Order_DTO_HUM objPharOrderDetails;
                     objPharOrderDetails=PharmacyFinancial_D_HUM.prepareOrderDetailsFinance(oResponse);
                     //Passing new parameter for show hide error message if member not found
                     objPharGetMemDTO = new PharmacyGetMember_DTO_HUM(null,null,null,null,null,null,objPharOrderDetails,false);
                     return objPharGetMemDTO;   
                 }
            }
            conObj = SendRequestGetMemberResponse(memId,networkID,recordId);
        }
        else
        {
        	// Fault Code
        	Pharmacy_H_HUM.logHpFaultError(responseEditMember,'PharmacyFinancial_S_HUM', 'retrieveEditMember', stateValue[2]);
        }
        return conObj;
    }
	
	/**
     * Returns continuation object
     * <p>
     * Method to call continuation serive for email utility .
     *
     * @param sMemName                      String
     * @param sHumanaId                     String
     * @param sAccNumber                    String
     * @param sOrderNumber                  String
     * @param sRxNumber                     String
     * @param sConcern                      String
     * @param sNetworkUserId                String
     * @param sEnterpriseId                 String
     * @param sRecordId                		String
     * @return                              continuation
     */
    public static Continuation sendRequestEmailUtilityService(String sMemName , String sHumanaIdNum , String sAccNum , String sOrderNumber , String sRxNumber , String sConcern , String sNetworkUserId,String sEnterpriseId, String sRecordId)
    {
        try
        {
            HUM_Webservice_Callout__c RSServiceDetails = HUM_Webservice_Callout__c.getValues('EmailUtilityService');
            // Create continuation with a timeout
            integer iTIMEOUT_INT_SECS = Integer.valueof(RSServiceDetails.Timeout__c) / 1000;
            Continuation con = new Continuation(iTIMEOUT_INT_SECS);
            con.continuationMethod = 'parseEmailUtilityResponse';   
            HttpRequest httpEmailUtility = builtHttpRequest(RSServiceDetails);
            httpEmailUtility.setBody(JSON.serialize(PharmacyFinancial_D_HUM.RequestStructureEmailUtilityService(sMemName , sHumanaIdNum , sAccNum , sOrderNumber , sRxNumber , sConcern , sNetworkUserId)));
            con.addHttpRequest(httpEmailUtility);
            Map<String,String> mapState = new Map<String,String>();
            mapState.put('EnterpriseId',sEnterpriseId);
            mapState.put('Concern',sConcern);
            mapState.put('NetworkUserId',sNetworkUserId);
            mapState.put('RecordId',sRecordId);
            con.state = mapState;
            return con;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'PharmacyFinancial_S_HUM', 'SendRequestEmailUtilityService');
            return null;
        }
    }
    
   /**
     * Returns responses of continuation object
     * <p>
     * parseEmailUtilityResponse Method to parse the service response
     *
     * @param List<string> labels .
     * @param  object State
     * @return Object
     */
    public static Object parseEmailUtilityResponse(List < string > labels, Object State)
    {
        try
        {
            EmailUtiltyResponseDTO_HUM oResponse = new EmailUtiltyResponseDTO_HUM();
            HttpResponse emailResponse = Continuation.getResponse(labels[0]);
            Map<String,String> mapState = (Map<String,String>)State;
            if(emailResponse != null && ((emailResponse.getStatusCode() == HTTP_OK_HUM || emailResponse.getStatusCode() == HTTP_ACCEPTED_HUM) && (!emailResponse.getBody().containsIgnoreCase(VOB_FAULT_CODE)) || Test.isRunningTest()))
            {
                oResponse = (EmailUtiltyResponseDTO_HUM)System.JSON.deserialize(emailResponse.getbody(),EmailUtiltyResponseDTO_HUM.class);
                if(oResponse != null && oResponse.SendEmailResponse !=  NULL && oResponse.SendEmailResponse.Description != NULL && oResponse.SendEmailResponse.Description == 'Send email successful')
                {
                    return sendRequestLogNotes(mapState.get('Concern'),mapState.get('EnterpriseId'),mapState.get('NetworkUserId'),mapState.get('RecordId'));
                }
            }
            else
	        {
	        	// Fault Code
	        	Pharmacy_H_HUM.logHpFaultError(emailResponse,'PharmacyFinancial_S_HUM', 'parseEmailUtilityResponse', mapState.get('RecordId'));
	        }
           
            return null;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'PharmacyFinancial_S_HUM', 'parseEmailUtilityResponse');
            return null;
        }
        
    }
    
     /**
     * Returns continuation object
     * <p>
     * Method to call continuation serive for log note.
     *
     * @param sConcern                      String
     * @param sEnterpriseId                 String
     * @param sNetworkUserId                String
     * @param sRecordId                		String
     * @return                              continuation
     */
    public static Continuation sendRequestLogNotes(String sComment, String sEnterpriseId , String sNetworkUserId, String sRecordId)
    {
        try
        {
            HUM_Webservice_Callout__c serviceDetails = HUM_Webservice_Callout__c.getValues('RighSourceService_EditMember');
	        // Create continuation with a timeout
	        integer iTIMEOUT_INT_SECS = Integer.valueof(serviceDetails.Timeout__c) / 1000;
	        Continuation con = new Continuation(iTIMEOUT_INT_SECS);
	        con.continuationMethod = 'parseLogNoteResponse';
	        con.state = sRecordId;
	        HttpRequest httpEditGetMemberReqObj = builtHttpRequest(serviceDetails);
	        httpEditGetMemberReqObj.setBody(JSON.serialize(PharmacyFinancial_D_HUM.RequestLogNotes(sComment,sEnterpriseId,sNetworkUserId)));
	        con.addHttpRequest(httpEditGetMemberReqObj);
	        return con;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'PharmacyFinancial_S_HUM', 'sendRequestLogNotes');
            return null;
        }
    }
    
     /**
     * Returns responses of continuation object
     * <p>
     * parseLogNoteResponseMethod to parse the service response
     *
     * @param List<string> labels .
     * @param  object State
     * @return Object
     */
    public static Object parseLogNoteResponse(List < string > labels, Object State)
    {
        try
        {
            HttpResponse logNoteResponse = Continuation.getResponse(labels[0]);
             if (logNoteResponse != null && !logNoteResponse.getBody().containsIgnoreCase(VOB_FAULT_CODE)  && ((logNoteResponse.getStatusCode() == HTTP_OK_HUM || logNoteResponse.getStatusCode() == HTTP_ACCEPTED_HUM) || Test.isRunningTest()))
             {
                return '';
             }
             else
	        {
	        	// Fault Code
	        	Pharmacy_H_HUM.logHpFaultError(logNoteResponse,'PharmacyFinancial_S_HUM', 'parseLogNoteResponse', State != null ? String.valueof(State) : '');
	        }
            return null;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'PharmacyFinancial_S_HUM', 'parseLogNoteResponse');
            return null;
        }
        
    }
	
	
	/**
     * Returns continuation object
     * <p>
     * Method to call continuation serive for Get Order Detail.
     * @param sEnterpriseId                 String
     * @param sNetworkUserId                String
    * @return                              continuation
     */
     
     public static continuation prepareGetOrderDetail(string ordNumber , string networkId)
     {
         try
         {
            HUM_Webservice_Callout__c serviceDetails = HUM_Webservice_Callout__c.getValues('RighSourceService_GetOrderDetails');
            // Create continuation with a timeout
            integer iTIMEOUT_INT_SECS = Integer.valueof(serviceDetails.Timeout__c) / 1000;
            Continuation con = new Continuation(iTIMEOUT_INT_SECS);
            con.continuationMethod = 'processOrder';
            HttpRequest httpEditGetMemberReqObj = builtHttpRequest(serviceDetails);
            httpEditGetMemberReqObj.setBody(JSON.serialize(PharmacyOrderDetail_D_HUM.requestStructureGetOrderDetailResponse(ordNumber,networkId)));
            con.state =  new PharmacyOrderWrapper_DTO_HUM(null, null, null, null, ordNumber,null,null);
            con.addHttpRequest(httpEditGetMemberReqObj);
            return con;
         }
         catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'PharmacyFinancial_S_HUM', 'prepareGetOrderDetail');
            return null;
        }
    }
    
    /**
     * Returns responses of continuation object
     * <p>
     * processOrder to parse the service response
     *
     * @param List<string> labels .
     * @param  object State
     * @return Object
     */
     public static Object processOrder(List < string > labels, Object State) {
         
       try
       {
          PharmacyOrderWrapper_DTO_HUM objOrderWrapDTO = (PharmacyOrderWrapper_DTO_HUM) state;
          RightSourceGetOrderDetResponse_DTO_HUM.GetOrderDetailResponse oResponse = new RightSourceGetOrderDetResponse_DTO_HUM.GetOrderDetailResponse();
         
          HttpResponse orderDetailResponse = Continuation.getResponse(labels[0]);
          if (orderDetailResponse != null && !orderDetailResponse.getBody().containsIgnoreCase(VOB_FAULT_CODE) &&  ((orderDetailResponse.getStatusCode() == HTTP_OK_HUM || orderDetailResponse.getStatusCode() == HTTP_ACCEPTED_HUM) || Test.isRunningTest()))
            {
              objOrderWrapDTO.objParentOrder = PharmacyOrderDetail_D_HUM.prepareOrderDetails((RightSourceGetOrderDetResponse_DTO_HUM) System.JSON.deserialize(orderDetailResponse.getbody(), RightSourceGetOrderDetResponse_DTO_HUM.class));
            }
          else
            {
              // Fault Code
              Pharmacy_H_HUM.logHpFaultError(orderDetailResponse,'PharmacyFinancial_S_HUM', 'processOrder', objOrderWrapDTO.sRecordId);
            }
          return objOrderWrapDTO;
         }
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, ' PharmacyFinancial_S_HUM', 'processOrder');
            return null;
        }
    }
    
    
    /**
     * Returns continuation object
     * <p>
     * Method to call continuation serive for Get Order Detail.
     * @param sEnterpriseId                 String
     * @param sNetworkUserId                String
    * @return                              continuation
     */
     
      public static Continuation sendRequestGetOrderResponse(String memID,string networkId, String sRecordId)
       {
          try
           {
                HUM_Webservice_Callout__c RSServiceDetails = HUM_Webservice_Callout__c.getValues('RighSourceService_GetOrders');
                // Create continuation with a timeout
                integer iTIMEOUT_INT_SECS = Integer.valueof(RSServiceDetails.Timeout__c) / 1000;
                Continuation con = new Continuation(iTIMEOUT_INT_SECS);
                con.continuationMethod = HUM_GETORDERRES;
                con.state = sRecordId;
                HttpRequest httpRSGetOrderReqObj = builtHttpRequest(RSServiceDetails);
                httpRSGetOrderReqObj.setBody(JSON.serialize(Pharmacy_CreateEditOrder_D_HUM.requestStructureGetOrderResponse(memID,networkId)));
                
                con.addHttpRequest(httpRSGetOrderReqObj);
                return con;
           }
          catch(Exception ex)
           {
                HUMExceptionHelper.logErrors(ex,'PharmacyFinancial_S_HUM', 'sendRequestGetOrderResponse');
                return null;
            }
       }
    
    
    /**
     * Returns responses of continuation object
     * <p>
     * parseRSGetOrderResponse to parse the service response
     *
     * @param List<string> labels .
     * @param  object State
     * @return Object
     */
    public static object parseRSGetOrderResponse(List < string > labels, Object State)
    {
        try
        {
            PharmacyOrderSummary_DTO_HUM  responseDTOList;
            HttpResponse responseOrderSummary = Continuation.getResponse(labels[0]);
            String orderSummaryrResponseBody = responseOrderSummary.getBody();
            RightSourceGetOrderResponse_DTO_HUM oResponseGetOrder;
            if (!orderSummaryrResponseBody.containsIgnoreCase(VOB_FAULT_CODE) && responseOrderSummary != null && ((responseOrderSummary.getStatusCode() == HTTP_OK_HUM || responseOrderSummary.getStatusCode() == HTTP_ACCEPTED_HUM) || Test.isRunningTest()))
            {
                oResponseGetOrder = (RightSourceGetOrderResponse_DTO_HUM) System.JSON.deserialize(orderSummaryrResponseBody, RightSourceGetOrderResponse_DTO_HUM.class);
                responseDTOList = PharmacyFinancial_D_HUM.prepareOrderSummaryResponse(oResponseGetOrder);
              }
            else
            {
                // Fault Code
                Pharmacy_H_HUM.logHpFaultError(responseOrderSummary,'PharmacyFinancial_S_HUM', 'parseRSGetOrderResponse', State != null ? String.valueof(State) : '');
            }
            return responseDTOList;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'PharmacyFinancial_S_HUM', 'parseRSGetOrderResponse');
            return null;
        }
    }
    
    /**
     * Returns request object of continuation object
     * Method to invoke the EditMember continuation requests
	 * @param - enterpriseID, netWorkId, creditCardKeyVar, amountVar, paymentDateVar, isPaymentAuthorizedVar
     * @return continuation object
    */
    public static Continuation SendRequestOneTimeCreditCardPayment(string enterpriseId, string netWorkId, string creditCardKeyVar,decimal amountVar, date paymentDateVar,boolean isPaymentAuthorizedVar)
    {
        try
        {
            HUM_Webservice_Callout__c serviceDetails = HUM_Webservice_Callout__c.getValues('RighSourceService_EditMember');
            // Create continuation with a timeout
            integer iTIMEOUT_INT_SECS = Integer.valueof(serviceDetails.Timeout__c) / 1000;
            Continuation con = new Continuation(iTIMEOUT_INT_SECS);
            con.continuationMethod = 'parseOneTimePaymentResponse';
            HttpRequest httpEditGetMemberReqObj = builtHttpRequest(serviceDetails);
            httpEditGetMemberReqObj.setBody(JSON.serialize(PharmacyFinancial_D_HUM.RequestStructureGetEditMemberPaymentRequest(enterpriseId,networkId,creditCardKeyVar,amountVar,paymentDateVar,isPaymentAuthorizedVar)));
            con.addHttpRequest(httpEditGetMemberReqObj);
            return con;         
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'PharmacyFinancial_S_HUM', 'SendRequestOneTimeCreditCardPayment');
            return null;
        }
    }
    
	/**
     * Returns responses of continuation object    
     * Callback Method to parse the One Time Credit Card Payment (EditMember) service response
     * @param  List<String> List of Continuation Labels
     * @param  Object State of Continuation
     * @return Object
    */
    public static object parseOneTimePaymentResponse(List < string > labels, Object State)
    {
        try
        {
	    	HttpResponse responseEditMember = Continuation.getResponse(labels[0]);
            String editMemberResponseBody = responseEditMember.getBody();
            
            if (!editMemberResponseBody.containsIgnoreCase(VOB_FAULT_CODE) && responseEditMember != null && ((responseEditMember.getStatusCode() == HTTP_OK_HUM || responseEditMember.getStatusCode() == HTTP_ACCEPTED_HUM) || Test.isRunningTest()))
            {
                return true;  
            }
            else
            {
               // Fault Code
               Pharmacy_H_HUM.logHpFaultError(responseEditMember,'PharmacyFinancial_S_HUM', 'parseOneTimePaymentResponse', '');
               return false;
            }
            return responseEditMember;         
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'PharmacyFinancial_S_HUM', 'parseOneTimePaymentResponse');
            return null;
        }
    }
    
    //One Time Payment -- End -- //
}