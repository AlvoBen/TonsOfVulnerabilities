/*******************************************************************************************************************************
Apex Class Name : Pharmacy_CreateEditOrder_S_HUM.cls
Version         : 1.0
Created On      : 2017
Function        : Service Class for Pharmacy_CreateEditOrder_C_HUM.

Modification Log:
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------*
* Rajesh Chandgothia                                   04/18/2017                  REQ 305689 Original Version
* Sharan Shanmugam									   07/27/2017				   REQ - 323542 : Exception handling
* Sushmetha Arumugam         271572                    08/14/2018                  REQ - 363500 - Displaying error message and preventing the user from saving the order for P.O.Box
* Melkisan Selvaraj			 341652				       03/04/2019                  REQ - 387544 : Splunk error logging Issues
* Sumit Pharande             345615                    03/27/2019                  REQ - 362063 - Payment Card Project: Do Not allow users to inactivate a cc that's attached to an order in progress
* Pinky Vijur                                  05/11/2021                  REQ 2230065: T1PRJ0002606 -2021 MF1- SF - Humana Constants Cleanup Humana Pharmacy
*********************************************************************************************************************************/

public with sharing Class Pharmacy_CreateEditOrder_S_HUM
{
    Public Static final Integer HTTP_ACCEPTED_HUM = 202; 
    Public Static final Integer HTTP_OK_HUM = 200;
    Public Static final String VOB_FAULT_CODE = 'faultcode';
    Public Static final String HUM_CREATEEDITORDER= 'parseCreateEditOrderResponse';
    Public Static final String HUM_GETORDERRES = 'parseRSGetOrderResponse'; 
    Public Static final String HUM_MEMBER_NOT_FOUND = 'Member not found';
    Public Static final String HUM_LOGNOTERESPONSE= 'logNoteResponse';
    Public Static final String HUM_CreateEditOrder_Response = 'parseMemberAndScriptResponse';
    Public Static final String HUM_PCP_SER_CONTENTTYPE = 'content-type';
    Public Static final String HUM_PCP_SER_CONECTION = 'Connection';
    Public Static final String HUM_PCP_SER_KEEPALIVE = 'keep-alive';
    Public Static final String HUM_PCP_SER_SOAPACTION = 'SOAPAction';

   	/**
     * Returns continuation object
     * <p>
     * Method to call continuation serive for the serive for GetScript And GetMember services .
     *
     * @param MemId                     String
     * @param ScriptKey                 String
     * @param startDate                 String
     * @param endDate                   String
     * @param networkID                 String
     * @param sRecordId                 String
     * @return                          continuation
     */																																				
 	public static Continuation SendRequestCreateEditOrderPage(String memID,String scriptKey,String startDate,String endDate, string networkID, String sRecordId)
	{
      try
      {
	      String memberID = memID;
          HUM_Webservice_Callout__c CEO_GetMember = HUM_Webservice_Callout__c.getValues('RighSourceService_GetMember');
	      HUM_Webservice_Callout__c CEO_GetScript = HUM_Webservice_Callout__c.getValues('RighSourceService_GetScripts');
           // Create continuation with a timeout
	      integer iTIMEOUT_INT_SECS = Integer.valueof(CEO_GetMember.Timeout__c) / 1000;
	      Continuation con = new Continuation(iTIMEOUT_INT_SECS);
	      // Set callback method should be common for services
	      con.continuationMethod = HUM_CreateEditOrder_Response ;
	      con.state = sRecordId;
	      //Build getmember Request 
	      HttpRequest httpCEO_getMemReqObj = Pharmacy_CreateEditOrder_S_HUM.builtHttpRequest(CEO_GetMember);
	      String objJSON = JSON.serialize(Pharmacy_CreateEditOrder_D_HUM.RequestStructureGetMember(memID,networkID));
	      httpCEO_getMemReqObj.setBody(objJSON );
	      // Build getScript Request 
	      HttpRequest httpCEO_getScriptObj = Pharmacy_CreateEditOrder_S_HUM.builtHttpRequest(CEO_GetScript );
	     httpCEO_getScriptObj.setBody(JSON.serialize(Pharmacy_D_HUM.requestStructureGetScriptResponse(memID,scriptKey,startDate, endDate,networkID)));
       // Add callout requests to continuation for requests
	      con.addHttpRequest(httpCEO_getMemReqObj);
	      con.addHttpRequest(httpCEO_getScriptObj);
	      return con;
      }
		catch(Exception ex)
		{
		    HUMExceptionHelper.logErrors(ex, 'Pharmacy_CreateEditOrder_S_HUM', 'SendRequestCreateEditOrderPage');
		    return null;
		}
  }

  
    /**
     * Returns HttpRequest
     * <p>
     * Method to build the HttpRequest
     *
     * @param ServiceObj                     HUM_Webservice_Callout__c
     * @return                          	 continuation
     */
    public static HttpRequest builtHttpRequest(HUM_Webservice_Callout__c ServiceObj)
  	{
        try
        {
	        HttpRequest request = new HttpRequest();
	        if (ServiceObj != null && ServiceObj.Active__c)
	        {
	            request.setMethod(ServiceObj.Request_Type__c);
	            if (!Test.isRunningTest() && String.isNotBlank(ServiceObj.Certificate_Name__c)) request.setClientCertificateName(ServiceObj.Certificate_Name__c);
	            if (String.isNotBlank(ServiceObj.Content_Type__c)) request.setHeader(HUM_PCP_SER_CONTENTTYPE, ServiceObj.Content_Type__c);
	            if (String.isNotBlank(ServiceObj.Content_Type__c)) request.setHeader(HUM_PCP_SER_CONTENTTYPE, ServiceObj.Content_Type__c);
	            request.setHeader(HUM_PCP_SER_CONECTION, HUM_PCP_SER_KEEPALIVE);
	            request.setEndpoint(ServiceObj.End_Point_URL__c);
	            request.setHeader(HUM_PCP_SER_SOAPACTION, '');
	            if (ServiceObj.Timeout__c != null) request.setTimeout(Integer.valueOf(ServiceObj.Timeout__c));
	        }
	        return request;
       }
       catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'Pharmacy_CreateEditOrder_S_HUM', 'builtHttpRequest');
            return null;
        }
	}
	
  /**
     * Returns  object of Pharmacy_CreateEditOrder_DTO_HUM
     * <p>
     * Method to parse response of Getmember and Getscript
     * @param pharmacyCls                     Pharmacy_CreateEditOrder_C_HUM class instance
     * @return                                Pharmacy_CreateEditOrder_DTO_HUM
     */
  public static Object parseMemberAndScriptResponse(List < string > labels, Object State)
  {
	try
	{
		Pharmacy_CreateEditOrder_DTO_HUM  objCreateEditDTO = null;
		PharmacyGetMember_Demographic_DTO_HUM objPharDemographicDetails;
		Integer conLableCount = 0;
		HttpResponse responseGetMember = Continuation.getResponse(labels[conLableCount]);
		RightSourceGetMemberResponse_DTO_HUM oResponseDemographic;
        if (responseGetMember != null && ((responseGetMember.getStatusCode() == HTTP_OK_HUM || responseGetMember.getStatusCode() == HTTP_ACCEPTED_HUM) && (!responseGetMember.getBody().containsIgnoreCase(VOB_FAULT_CODE)) || Test.isRunningTest()))
		{
		    oResponseDemographic = (RightSourceGetMemberResponse_DTO_HUM) System.JSON.deserialize(responseGetMember.getBody(), RightSourceGetMemberResponse_DTO_HUM.class);
		    objPharDemographicDetails = Pharmacy_D_HUM.prepareDemographicDetails(oResponseDemographic);
		    //getScript
		    list <PharmacyMemberPrescription_DTO_HUM> responseDTOList;
		    HttpResponse responseGetScript = Continuation.getResponse(labels[conLableCount+1]);
		    RightSourceGetScriptResponse_DTO_HUM oResponseGetMemberPrescription;
		    oResponseGetMemberPrescription= (RightSourceGetScriptResponse_DTO_HUM) System.JSON.deserialize(responseGetScript.getBody(), RightSourceGetScriptResponse_DTO_HUM.class);
		    responseDTOList = Pharmacy_D_HUM.preparememerPrescriptionResponse(oResponseGetMemberPrescription);
		    objCreateEditDTO = new Pharmacy_CreateEditOrder_DTO_HUM(objPharDemographicDetails,responseDTOList);
		}
		else
		{
			// Fault Code
			Pharmacy_H_HUM.logHpFaultError(responseGetMember,'Pharmacy_CreateEditOrder_S_HUM', 'parseMemberAndScriptResponse', State != null ? String.valueof(State) : '');
		}
	    return objCreateEditDTO ;
	}
	catch(Exception ex)
    {
        HUMExceptionHelper.logErrors(ex, 'Pharmacy_CreateEditOrder_S_HUM', 'parseMemberAndScriptResponse');
        return null;
    }
  }
  
   /**
     * Returns continuation object
     * <p>
     * Method to call continuation serive for the serive for GetOrderResponse services .
     *
     * @param orderKey                  	 String
     * @param OrderLevel                  	 String
     * @param networkID                  	 String
     * @param sRecordId                  	 String
     * @return                                continuation
     */
    public static Continuation sendRequestGetOrderDetailResponse(String orderKey, String OrderLevel, string networkID, String sRecordId)
    {
        TRY
        {
	        HUM_Webservice_Callout__c RSServiceDetails = HUM_Webservice_Callout__c.getValues('RighSourceService_GetOrderDetails');
	        // Create continuation with a timeout
	        integer iTIMEOUT_INT_SECS = Integer.valueof(RSServiceDetails.Timeout__c) / 1000;
	        Continuation con = new Continuation(iTIMEOUT_INT_SECS);
	        con.continuationMethod = 'parseGetOrderDetailResponse';
	        HttpRequest httpRSGetOrderDetailReqObj = builtHttpRequest(RSServiceDetails);
	        httpRSGetOrderDetailReqObj.setBody(JSON.serialize(Pharmacy_D_HUM.requestStructureGetOrderDetailResponse(orderKey, networkID)));
	        con.addHttpRequest(httpRSGetOrderDetailReqObj);
	        con.state = new PharmacyOrderDetRes_DTO_HUM (null,null,OrderLevel,networkID, sRecordId);
	        return con;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'Pharmacy_CreateEditOrder_S_HUM', 'sendRequestGetOrderDetailResponse');
            return null;
        }
    }
    
  /**
     * Returns responses of continuation object
     * <p>
     * parseGetOrderDetailResponse Method to parse the service response
     *
     * @param List<string> labels .
     * @param  object State
     * @return Object
     */
    public static Object parseGetOrderDetailResponse(List <string> labels, Object State)
    {
        try
        {
	        PharmacyOrderDetRes_DTO_HUM objOrderDetResDTO = (PharmacyOrderDetRes_DTO_HUM) state;
	        RightSourceGetOrderDetResponse_DTO_HUM.GetOrderDetailResponse oResponse = new RightSourceGetOrderDetResponse_DTO_HUM.GetOrderDetailResponse();
	        HttpResponse orderDetailResponse = Continuation.getResponse(labels[0]);
	        String changedOrderkey = '';
	        
	        if (orderDetailResponse != null && ((orderDetailResponse.getStatusCode() == HTTP_OK_HUM || orderDetailResponse.getStatusCode() == HTTP_ACCEPTED_HUM) && (!orderDetailResponse.getBody().containsIgnoreCase(VOB_FAULT_CODE)) || Test.isRunningTest()))
	        {
	           objOrderDetResDTO = new PharmacyOrderDetRes_DTO_HUM(Pharmacy_D_HUM.prepareOrderDetails((RightSourceGetOrderDetResponse_DTO_HUM)System.JSON.deserialize(orderDetailResponse.getbody(),RightSourceGetOrderDetResponse_DTO_HUM.class)),null,objOrderDetResDTO.orderLevel,'','');                 
	        }
	        else
	        {
	        	// Fault Code
	        	Pharmacy_H_HUM.logHpFaultError(orderDetailResponse,'Pharmacy_CreateEditOrder_S_HUM', 'parseGetOrderDetailResponse', objOrderDetResDTO.sRecordId);
	        }
	        
	        return objOrderDetResDTO;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'Pharmacy_CreateEditOrder_S_HUM', 'parseGetOrderDetailResponse');
            return null;
        }
       
    } 
         
   
     /**
     * Returns continuation object
     * <p>
     * Method to call continuation serive for the serive for CreateEditOrder services .
     *
     * @param createEditObj                     RSCreateAndEditOrderRequest_DTO_HUM
     * @param sRecordId							String
     * @return                                	continuation
     */
    public static Continuation sendRequestCreateEditOrderDetailRequest(RSCreateAndEditOrderRequest_DTO_HUM createEditObj, String sRecordId)
    {
        try
        {
	        HUM_Webservice_Callout__c RSServiceDetails = HUM_Webservice_Callout__c.getValues('RighSourceService_CreateAndEditOrder');       
	        // Create continuation with a timeout
	        integer iTIMEOUT_INT_SECS = Integer.valueof(RSServiceDetails.Timeout__c) / 1000;
	        Continuation con = new Continuation(iTIMEOUT_INT_SECS);
	        con.continuationMethod = HUM_CREATEEDITORDER;
	        con.state = sRecordId;
	        HttpRequest httpRSCreateEditOrderObj = builtHttpRequest(RSServiceDetails);
	        httpRSCreateEditOrderObj.setBody(JSON.serialize(createEditObj));
	        con.addHttpRequest(httpRSCreateEditOrderObj);
	        return con;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'Pharmacy_CreateEditOrder_S_HUM', 'sendRequestCreateEditOrderDetailRequest');
            return null;
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
    public static object parseCreateEditOrderResponse(List <string> labels, Object State)
    {
        try
        {
	        HttpResponse responseCROrderSummary = Continuation.getResponse(labels[0]);
	        RSCreateAndEditOrderResponse_DTO_HUM  responseCreateEditOrder = null;
	        if (responseCROrderSummary != null && ((responseCROrderSummary.getStatusCode() == HTTP_OK_HUM || responseCROrderSummary.getStatusCode() == HTTP_ACCEPTED_HUM) && (!responseCROrderSummary.getBody().containsIgnoreCase(VOB_FAULT_CODE)) || Test.isRunningTest()))
        	{
	        	String createEditResponseBody = responseCROrderSummary.getBody();
	        	responseCreateEditOrder = (RSCreateAndEditOrderResponse_DTO_HUM) System.JSON.deserialize(createEditResponseBody , RSCreateAndEditOrderResponse_DTO_HUM.class);
				if (null != responseCreateEditOrder && null != responseCreateEditOrder.CreateAndEditOrderResponse && responseCreateEditOrder.CreateAndEditOrderResponse.ErrorDescription != HUM_MEMBER_NOT_FOUND)
					return responseCreateEditOrder.CreateAndEditOrderResponse;
				else
					return null;				
        	}
	        else
            {
            	// Fault Code
            	String sErrorMessage = Pharmacy_H_HUM.logHpFaultError(responseCROrderSummary,'Pharmacy_CreateEditOrder_S_HUM', 'parseCreateEditOrderResponse', State != null ? String.valueof(State) : '');
            	return sErrorMessage;
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'Pharmacy_CreateEditOrder_S_HUM', 'parseCreateEditOrderResponse');
            return null;
        }
    }
      /**
     * Returns continuation object
     * <p>
     * Method to call continuation service for editMember to Log Notes.
     *
     * @param numberOfConsentGiven                     Integer
     * @param sMemID                     			   String
     * @param networkID                     		   String
     * @param sRecordId                     		   String
     * @return                                		   continuation
     */
    public static Continuation sendRequestGetEditMemberForScripts(Integer numberOfConsentGiven, string sMemID, string networkID, String sRecordId)
    {
        try
        {
	        HUM_Webservice_Callout__c serviceDetails = HUM_Webservice_Callout__c.getValues('RighSourceService_EditMember');
	        // Create continuation with a timeout
	        integer iTIMEOUT_INT_SEC = Integer.valueof(serviceDetails.Timeout__c) / 1000;
	        Continuation con = new Continuation(iTIMEOUT_INT_SEC);
	        con.continuationMethod = HUM_LOGNOTERESPONSE;
	        con.state = sRecordId;
	        HttpRequest httpEditGetMemberReqObj = builtHttpRequest(serviceDetails);
	        httpEditGetMemberReqObj.setBody(JSON.serialize(Pharmacy_CreateEditOrder_D_HUM.RequestStructureGetEditMemberForScripts(numberOfConsentGiven, sMemID, networkID)));
	        con.addHttpRequest(httpEditGetMemberReqObj);
	        return con;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'Pharmacy_CreateEditOrder_S_HUM', 'parseCreateEditOrderResponse');
            return null;
        }
    }
    
    /**
     * Returns responses of continuation object
     * <p>
     * parseLogNoteResponse Method to parse the service response
     *
     * @param  object state,List<string> labels .
     * @return Object
     */
    public static object parseLogNoteResponse(List <string> labels, Object State)
    {
        try
        {
	        HttpResponse responseEditMember = Continuation.getResponse(labels[0]);
	        String editMemberResponseBody = responseEditMember.getBody();
	        string memId = string.valueOf(state);
	        string result = null;
	        if (!editMemberResponseBody.containsIgnoreCase(VOB_FAULT_CODE) && responseEditMember != null && ((responseEditMember.getStatusCode() == HTTP_OK_HUM || responseEditMember.getStatusCode() == HTTP_ACCEPTED_HUM) || Test.isRunningTest()))
	        {
	            result = editMemberResponseBody;
	        }
	        else
	        {
	        	// Fault Code
	        	Pharmacy_H_HUM.logHpFaultError(responseEditMember,'Pharmacy_CreateEditOrder_S_HUM', 'parseLogNoteResponse', State != null ? String.valueof(State) : '');
	        }        
	        return result;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'Pharmacy_CreateEditOrder_S_HUM', 'parseLogNoteResponse');
            return null;
        }
    }
	
	/**
     * Returns continuation object
     * <p>
     * Method to call continuation service for editMember to Log Notes.
     * @param sMemID                                   String
     * @param networkID                                String
     * @param sRecordId                                String
     * @return                                         continuation
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
            HUMExceptionHelper.logErrors(ex,'Pharmacy_CreateEditOrder_S_HUM', 'sendRequestGetOrderResponse');
            return null;
        }
    }
    
    /**
     * Returns responses of continuation object
     * <p>
     * parseRSGetOrderResponse Method to parse the service response
     *
     * @param  object state,List<string> labels .
     * @return Object
     */
    public static object parseRSGetOrderResponse(List <string> labels, Object State)
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
                responseDTOList = Pharmacy_CreateEditOrder_D_HUM.prepareOrderSummaryResponse(oResponseGetOrder);
            }
            else
            {
                // Fault Code
                Pharmacy_H_HUM.logHpFaultError(responseOrderSummary,'Pharmacy_CreateEditOrder_S_HUM', 'parseRSGetOrderResponse', State != null ? String.valueof(State) : '');
            }
            return responseDTOList;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'Pharmacy_CreateEditOrder_S_HUM', 'parseRSGetOrderResponse');
            return null;
        }
    }
    
    /**
     * Returns continuation object
     * <p>
     * Method to call continuation service for editMember to Log Notes.
     * @param sMemID                                   String
     * @param networkID                                String
     * @param sRecordId                                String
     * @return                                         continuation
     */
    public static Continuation SendRequestGetMemberResponseFinance(String sMemID, string networkID, string sRecordId)
    {
        try
        {
            HUM_Webservice_Callout__c RSServiceDetails = HUM_Webservice_Callout__c.getValues('RighSourceService_GetMember');
            // Create continuation with a timeout
            integer iTIMEOUT_INT_SECS = Integer.valueof(RSServiceDetails.Timeout__c) / 1000;
            Continuation con = new Continuation(iTIMEOUT_INT_SECS);
            con.continuationMethod = 'parseRSGetMemberResponseFinance';
            con.state = sRecordId;  
            HttpRequest httpRSGetMemberReqObj = builtHttpRequest(RSServiceDetails);
            httpRSGetMemberReqObj.setBody(JSON.serialize(Pharmacy_CreateEditOrder_D_HUM.RequestStructureGetMember(sMemID, networkID)));
            con.addHttpRequest(httpRSGetMemberReqObj);
            return con;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'Pharmacy_CreateEditOrder_S_HUM', 'SendRequestGetMemberResponse');
            return null;
        }
    }
    
    /**
     * Returns responses of continuation object
     * <p>
     * parseRSGetMemberResponseFinance Method to parse the service response
     *
     * @param  object state,List<string> labels .
     * @return Object
     */
    
     public static object parseRSGetMemberResponseFinance(List <string> labels, Object State)
     {
        try
        {
            PharmacyGetMember_Finance_DTO_HUM responseDTOListGetMember;
            HttpResponse responseGetMember = Continuation.getResponse(labels[0]);
            String getMemberResponseBody = responseGetMember.getBody();
            RightSourceGetMemberResponse_DTO_HUM oResponseGetMember ;
            if (!getMemberResponseBody .containsIgnoreCase(VOB_FAULT_CODE) && responseGetMember != null && ((responseGetMember.getStatusCode() == HTTP_OK_HUM || responseGetMember.getStatusCode() == HTTP_ACCEPTED_HUM) || Test.isRunningTest()))
            {
                oResponseGetMember = (RightSourceGetMemberResponse_DTO_HUM) System.JSON.deserialize(getMemberResponseBody, RightSourceGetMemberResponse_DTO_HUM.class);
                responseDTOListGetMember= Pharmacy_CreateEditOrder_D_HUM.prepareGetMember(oResponseGetMember);
             }
            else
            {
                // Fault Code
                Pharmacy_H_HUM.logHpFaultError(responseGetMember ,'Pharmacy_CreateEditOrder_S_HUM', 'parseRSGetMemberResponseFinance', State != null ? String.valueof(State) : '');
            }
            return responseDTOListGetMember;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'Pharmacy_CreateEditOrder_S_HUM', 'parseRSGetMemberResponseFinance');
            return null;
        }
    }
    
}