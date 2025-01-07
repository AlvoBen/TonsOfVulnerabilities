/*******************************************************************************************************************************
Modification Log:
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------*
* Monali Jagtap                                        09/29/2023              US- 4429836 T1PRJ0891339 - MF 1 - SF -TECH - Error Handling - Make sure all exceptions are handled with appropriate messages
*********************************************************************************************************************************/
public with sharing class PharmacyCreateEditOrder_LS_HUM {
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
     * Method to call continuation serive for the serive for CreateEditOrder services .
     *
     * @param createEditObj                     RSCreateAndEditOrderRequest_DTO_HUM
     * @param sRecordId							String
     * @return                                	continuation
     */
    public static Object sendRequestCreateEditOrderDetailRequest(RSCreateAndEditOrderRequest_DTO_HUM createEditObj, String sRecordId)
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
            HUMExceptionHelper.logErrors(ex, 'PharmacyCreateEditOrder_LS_HUM', 'sendRequestCreateEditOrderDetailRequest');
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
            	String sErrorMessage = Pharmacy_H_HUM.logHpFaultError(responseCROrderSummary,'PharmacyCreateEditOrder_LS_HUM', 'parseCreateEditOrderResponse', State != null ? String.valueof(State) : '');
                return sErrorMessage;
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'PharmacyCreateEditOrder_LS_HUM', 'parseCreateEditOrderResponse');
            throw new AuraHandledException(ex.getMessage());
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
            HUMExceptionHelper.logErrors(ex, 'PharmacyCreateEditOrder_LS_HUM', 'builtHttpRequest');
            throw new AuraHandledException(ex.getMessage());
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
    public static Object sendRequestGetEditMemberForScripts(Integer numberOfConsentGiven, string sMemID, string networkID, String sRecordId)
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
            throw new AuraHandledException(ex.getMessage());
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
                result = null;
	        }        
	        return result;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'Pharmacy_CreateEditOrder_S_HUM', 'parseLogNoteResponse');
            throw new AuraHandledException(ex.getMessage());
        }
    }
}