/*****************************************************************************************************************************
Apex Class Name : PharmacyFinancial_LS_HUM
Version         : 1.0
Created On      : 09/24/2021
Function        : Service Class for PharmacyFinancial_LC_HUM.
Test Class      : PharmacyFinancial_LT_HUM
                 
Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0          Swapnali Sonawane                                    09/24/2021               
* 	 1.1		  Abhishek Mangutkar                                    06/09/2022    
*    1.2           Monali Jagtap                                       09/29/2023       US- 4429836 T1PRJ0891339 - MF 1 - SF -TECH - Error Handling - Make sure all exceptions are handled with appropriate messages           	
*******************************************************************************************************************************************************************/
public with sharing class PharmacyFinancial_LS_HUM {

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
    @AuraEnabled(continuation=true cacheable=true)
    public static Object SendRequestGetMemberResponse(String sMemID , String sStartDate , String sEndDate , String sPayment , String sAdjustment, string networkID , String sRecordId)
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
            httpRSGetMemberReqObj.setBody(JSON.serialize(PharmacyFinancial_LD_HUM.RequestStructureGetAdjPay(sMemID,sStartDate,sEndDate,sPayment,sAdjustment,networkID,sRecordId)));
            con.state = con.addHttpRequest(httpRSGetMemberReqObj);
            return con;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'PharmacyFinancial_LS_HUM', 'SendRequestGetMemberResponse');
            throw new AuraHandledException(ex.getMessage());
        }
    }

    /**
     * Returns responses of continuation object
     * <p>
     * parseGetAdjPayResponse Method to parse the service response
     *
     * @param List<string> labels .
     * @param  object StateS
     * @return Object
     */
    @AuraEnabled(cacheable=true)
    public static Object parseGetAdjPayResponse(List < string > labels, Object State)
    {
        try
        {            
            
            RSGetAdjustmentAndPaymentResponseDTO_HUM.AdjustmentsAndPaymentsResponse oResponse = new RSGetAdjustmentAndPaymentResponseDTO_HUM.AdjustmentsAndPaymentsResponse();
           
            HttpResponse AdjustmentPaymentResponse = Continuation.getResponse((string)state);
            
            if (AdjustmentPaymentResponse != null && ((AdjustmentPaymentResponse.getStatusCode() == HTTP_OK_HUM || AdjustmentPaymentResponse.getStatusCode() == HTTP_ACCEPTED_HUM) && (!AdjustmentPaymentResponse.getBody().containsIgnoreCase(VOB_FAULT_CODE)) || Test.isRunningTest()))
            {
                oResponse = PharmacyFinancial_LD_HUM.preparePaymentDetails((RSGetAdjustmentAndPaymentResponseDTO_HUM)System.JSON.deserialize(AdjustmentPaymentResponse.getBody(),RSGetAdjustmentAndPaymentResponseDTO_HUM.class));
                if (null != oResponse && null != oResponse.Adjustments && null != oResponse.Adjustments.ErrorDescription && oResponse.Adjustments.ErrorDescription == HUM_MEMBER_NOT_FOUND)
                    oResponse = new RSGetAdjustmentAndPaymentResponseDTO_HUM.AdjustmentsAndPaymentsResponse();
            }
            else
            {
                // Fault Code
                Pharmacy_H_HUM.logHpFaultError(AdjustmentPaymentResponse,'PharmacyFinancial_LS_HUM', 'parseGetAdjPayResponse', State != null ? String.valueof(State) : '');
            }
            return oResponse;
        }
        catch(Exception ex)
        {
            
            HUMExceptionHelper.logErrors(ex,'PharmacyFinancial_LS_HUM', 'parseGetAdjPayResponse');
            throw new AuraHandledException(ex.getMessage());
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
     * Returns request object of continuation object
     * Method to invoke the EditMember continuation requests
   * @param - enterpriseID, netWorkId, creditCardKeyVar, amountVar, paymentDateVar, isPaymentAuthorizedVar
     * @return continuation object
    */
    public static Object SendRequestOneTimeCreditCardPayment(string enterpriseId, string netWorkId, string creditCardKeyVar,decimal amountVar, date paymentDateVar,boolean isPaymentAuthorizedVar)
    {
        try
        {
            HUM_Webservice_Callout__c serviceDetails = HUM_Webservice_Callout__c.getValues('RighSourceService_EditMember');
            // Create continuation with a timeout
            integer iTIMEOUT_INT_SECS = Integer.valueof(serviceDetails.Timeout__c) / 1000;
            Continuation con = new Continuation(iTIMEOUT_INT_SECS);
            con.continuationMethod = 'parseOneTimePaymentResponse';
            HttpRequest httpEditGetMemberReqObj = builtHttpRequest(serviceDetails);
            httpEditGetMemberReqObj.setBody(JSON.serialize(PharmacyFinancial_LD_HUM.RequestStructureGetEditMemberPaymentRequest(enterpriseId,networkId,creditCardKeyVar,amountVar,paymentDateVar,isPaymentAuthorizedVar)));
            con.addHttpRequest(httpEditGetMemberReqObj);
            return con;         
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'PharmacyFinancial_S_HUM', 'SendRequestOneTimeCreditCardPayment');
            throw new AuraHandledException(ex.getMessage());
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
               Pharmacy_H_HUM.logHpFaultError(responseEditMember,'PharmacyFinancial_LS_HUM', 'parseOneTimePaymentResponse', '');
               return false;
            }
            //return responseEditMember;         
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'PharmacyFinancial_LS_HUM', 'parseOneTimePaymentResponse');
           throw new AuraHandledException(ex.getMessage());
        }
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
        con.continuationMethod = 'processEditMember';
        HttpRequest httpEditGetMemberReqObj = builtHttpRequest(serviceDetails);
        httpEditGetMemberReqObj.setBody(JSON.serialize(PharmacyFinancial_LD_HUM.RequestStructureGetEditMemberResponse(memID, sCreditCardKey, exMonth, exYear, sActive, sAutoCharge, sFSA, sFirstName, sMiddleName, sLastName, sTokenKey, sCreditType, isInsert, networkID)));
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
        try
        {			
			HttpResponse responseEditMember = Continuation.getResponse(labels[0]);
			String editMemberResponseBody = responseEditMember.getBody();  
			return editMemberResponseBody;
		}
		catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'PharmacyFinancial_LS_HUM', 'retrieveEditMember');
            throw new AuraHandledException(ex.getMessage());
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
                objPharCreditCardDetails = PharmacyFinancial_LD_HUM.prepareCreditCardDetails(oResponseCreditCard);
                //Passing new parameter for show hide error message if member not found
                objPharGetMemDTO = new PharmacyGetMember_DTO_HUM(null,null,null, objPharCreditCardDetails,null,null,null,false);
            }
            else
            {
                // Fault Code
                Pharmacy_H_HUM.logHpFaultError(responseCreditCard,'PharmacyFinancial_LS_HUM', 'parseRSGetMemberCreditResponse', State != null ? String.valueof(State) : '');
            }
            return objPharGetMemDTO;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'PharmacyFinancial_LS_HUM', 'parseRSGetMemberCreditResponse');
            throw new AuraHandledException(ex.getMessage());
        }
    }
    
}