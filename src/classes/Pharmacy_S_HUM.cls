/*******************************************************************************************************************************
Apex Class Name : Pharmacy_S_HUM.cls
Version         : 1.0
Created On      : 2016
Function        : Service Class for Pharmacy_C_HUM.

Modification Log:
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Rajesh Keswani               57637                12/20/2016                 Original Version
* Rajesh Chandgothia           55992                12/29/2016                 Demographic functionality (REQ 291083)
* Rajesh Keswani               58175                01/22/2017                 Implementation of REQ - 296652 - Order Summary details from getOrder EPostRX
* Shiva Pasumarty              60234                02/02/2017                 OMS Log Notes Req# REQ - 272225
* Rajesh Keswani               66266                02/21/2017                 Implementation of REQ - 291082 - Edit Demographic section
* Rajesh Chandgothia           70352                02/14/2017                 Member prescription Req# REQ - 296752
* Sharan Shanmugam			   74899				03/09/2017				   Implementation of REQ - 296650 - Order details
* Rajesh Keswani               74554  	            03/03/2017                 REQ - 299426 Add and Edit address section
* Rajesh Chandgothia           75938                03/08/2017                 Edit Order Req# REQ - 296651
* Vishal Verma                 78304                03/16/2017                 REQ-296926 : Added MTM Indicator
* Albin Arul                   78959                03/24/2017                 REQ - 296721 : Implemented Member Prescription section - Member Consent changes
* Vamsi Kondragunta			   81140				03/31/2017				   REQ - 301578 Added for Autorefil and captype update
* Rajesh Keswani               74554  	            04/27/2017                 5/11 merge offline mode
* Avinash Choubey                                   04/06/2017                 5/11 merge req 301370
* Rajesh Keswani               89095                05/02/2017            	   REQ - 310759;  Modify Shipping address in Converting Order Edit/create to Subtab
* Rajesh Keswani               112036	            07/20/2017                 Implementation of REQ - 323389 - adding networkId in service request
* Sharan Shanmugam									07/27/2017				   REQ - 323542 : Exception handling
* Joel George										10/31/2018				   REQ - 367769.
* Praveen Kumar Parimi   	   317200				12/06/2018				   REQ - 379727 aka CA Ticket 7808355 - Adding Additional Fault code for Address Validation.
* Melkisan Selvaraj								    03/04/2019                 REQ - 387544 : Splunk error logging Issues
* Lavanya Rao				 							10/11/2021                 User Story 2731234: T1PRJ0002606- 2021 MF6 - Swat-SF-Tech-HumConstant and Global Constant Fix
* Thaurn Madishetti                                         04/14/2023                 US-3837439: T1PRJ0714381- MF1: (PLT) 100 Day - CRM Supply Eligibility Indicator (Classic)
*********************************************************************************************************************************/
public with sharing class Pharmacy_S_HUM
{
Public Static final String VOB_FAULT_CODE = 'faultcode';
Public Static final Integer HTTP_ACCEPTED_HUM = 202; 
Public Static final Integer HTTP_OK_HUM = 200;
Public Static final String HUMADDRESSERROR='CONFIRM';
Public Static final String PHARMACY_EDITMEMBER_HUM='retrieveUpdatedGetMember';
Public Static final String HUM_GETORDERRES = 'parseRSGetOrderResponse';  
Public Static final String PHARMACY_ZIP_CITY_ERROR =  'IncompatibleZipCity';
Public Static final String HUM_GETSCRIPTS= 'parseRSGetScriptResponse';
public static final string HUM_MEMBER_NOT_FOUND = 'Member not found';
Public Static final String HUM_GETORDERDETAILRES = 'parseRSGetOrderDetailResponse'; 
Public Static final String PHAR_PROCESSCHILDORDER_HUM = 'processChildOrder';
Public Static final String HUM_CREATEEDITORDER= 'parseCreateEditOrderResponse';

 Public Static final String HUM_RSGetMember_Response = 'parseRSGetMemberResponse'; 
 Public Static final String HUM_RSS_SER_CONTENTTYPE = 'content-type';
 Public Static final String HUM_RSS_SER_CONECTION = 'Connection';
 Public Static final String HUM_RSS_SER_KEEPALIVE = 'keep-alive';
 Public Static final String HUM_RSS_SER_SOAPACTION = 'SOAPAction';
    private Pharmacy_D_HUM oDataPharmacy = new Pharmacy_D_HUM();

    /**
    * constructor method for Pharmacy_C_HUM
    * <p>
    * Method to set the values for Pharmacy_C_HUM.
    *
    @param pharmacyCls Pharmacy_HUM class instance
    */
    public void init(Pharmacy_C_HUM pharmacyCls)
    {
        pharmacyCls.objPolMem = new Policy_Member__c();
        assignValues(pharmacyCls);
    }

    /**
    * assigning values to variables on load of page
    * <p>
    * Method to set the values for Pharmacy_C_HUM.
    *
    @param pharmacyCls Pharmacy_C_HUM class instance
    */
    private void assignValues(Pharmacy_C_HUM pharmacyCls)
    {
        if (ApexPages.currentpage().getParameters().containsKey('Id'))
        {
            pharmacyCls.sPolMemId = ApexPages.currentpage().getParameters().get('Id');
            pharmacyCls.objPolMem = oDataPharmacy.getPolicyMember_SOQL(pharmacyCls);
        }
    }
    
    /**
    * Request Structure for EAS service
    * <p>
    * Method to set the values for Pharmacy_C_HUM.
    *
    @param addressDto AddressValidationHelper_DTO_HUM class instance
    */
    public static continuation validateAddress(AddressValidationHelper_DTO_HUM addressDto)
    {
        try
        {
	        HUM_Webservice_Callout__c serviceAddressDetails = HUM_Webservice_Callout__c.getValues('Enterprise_Address_Service');
	        // Create continuation with a timeout
	        integer iTIMEOUT_INT_SECS = Integer.valueof(serviceAddressDetails.Timeout__c) / 1000;
	        Continuation conAddObj;
	        if(addressDto != null)
	        {
	            conAddObj = new Continuation(iTIMEOUT_INT_SECS);
	            conAddObj.continuationMethod = 'addressResponse';
	            HttpRequest httpAddReqObj = builtHttpRequest(serviceAddressDetails);
	            conAddObj.state = addressDto;
	            EAS_AddressStandardizationRequest_H_HUM requestObj = new EAS_AddressStandardizationRequest_H_HUM();
	            requestObj.StandardizeAddressRequest = addressDto.addressReq;
	            httpAddReqObj.setBody(JSON.serialize(requestObj));
	            conAddObj.addHttpRequest(httpAddReqObj);
	        }
	        return conAddObj;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'Pharmacy_S_HUM', 'validateAddress');
            return null;
        }
    }
    
    /**
    * Response Structure for EAS service
    * <p>
    * Method to set the values for Pharmacy_C_HUM.
    *
    *@param object state,List<string> labels .
    *@return Object
    */
     public static object addressResponse(List <string> labels, object state)
    {
        try
        {
            HttpResponse responseAddress = Continuation.getResponse(labels[0]);
            String AddressResponseBody = responseAddress.getBody();
            EAS_AddressStandardizationRespone_H_HUM oResponseValidateAddress;
            AddressValidationHelper_DTO_HUM addressDto = (AddressValidationHelper_DTO_HUM)state;
            Continuation conresObj = null;
              if (!AddressResponseBody.containsIgnoreCase(VOB_FAULT_CODE) && responseAddress != null && ((responseAddress.getStatusCode() == HTTP_OK_HUM || responseAddress.getStatusCode() == HTTP_ACCEPTED_HUM) || Test.isRunningTest()))
            
            {
                oResponseValidateAddress = (EAS_AddressStandardizationRespone_H_HUM) System.JSON.deserialize(AddressResponseBody, EAS_AddressStandardizationRespone_H_HUM.class);
                if(oResponseValidateAddress != null && oResponseValidateAddress.StandardizeAddressResponse != null && string.isNotBlank(oResponseValidateAddress.StandardizeAddressResponse.ChangeIndicator) &&'Good'.equalsIgnoreCase(oResponseValidateAddress.StandardizeAddressResponse.ChangeIndicator))
                { 
                     addressDto.addressValidatedFlag = 'true';
                     addressDto.overrideReasonCode = '0';
                     return prepareEditGetMember(addressDto.enterpriseID,'','','','','',addressDto.networkID,addressDto,'');
                }
                else if(oResponseValidateAddress != null && oResponseValidateAddress.StandardizeAddressResponse != null && string.isNotBlank(oResponseValidateAddress.StandardizeAddressResponse.ChangeIndicator) && ('Bad'.equalsIgnoreCase(oResponseValidateAddress.StandardizeAddressResponse.ChangeIndicator) || 'Change'.equalsIgnoreCase(oResponseValidateAddress.StandardizeAddressResponse.ChangeIndicator)))
                {   
                     return HUMADDRESSERROR+'_'+1;
                }
                else return HUMADDRESSERROR+'_'+3;
            }
            else
            {
            	// Fault Code
            	Pharmacy_H_HUM.logHpFaultError(responseAddress,'Pharmacy_S_HUM', 'addressResponse', addressDto.sRecordId);
            	return null;
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'Pharmacy_S_HUM', 'addressResponse');
            return null;
        }
    }
    /**
     * Returns continuation object
     * <p>
     * Method to call continuation service for Edit script.
     *
     * @param sMemId                     String
     * @param sScriptKey                 String
     * @param sAutoRefill                String
     * @param startDate                  String
     * @param endDate                    String
     * @param networkID                  String
     * @param sRecordId                  String
     * @return                           continuation
     */
    public static continuation updatescriptautorefil(string memId,String sScriptKey,String sAutoRefill,string startDate, string endDate, string networkID, String sRecordId)
    {
        try
        {
	        HUM_Webservice_Callout__c serviceDetails = HUM_Webservice_Callout__c.getValues('RighSourceService_EditScript');
	        // Create continuation with a timeout
	        integer iTIMEOUT_INT_SECS = Integer.valueof(serviceDetails.Timeout__c) / 1000;
	        Continuation con = new Continuation(iTIMEOUT_INT_SECS);
	        con.continuationMethod = 'retrieveautorefil';
	        HttpRequest httpEditscriptautorefilReqObj = builtHttpRequest(serviceDetails);
	        httpEditscriptautorefilReqObj.setBody(JSON.serialize(Pharmacy_D_HUM.RequestStructureGetAutorefilResponse(sScriptKey,sAutoRefill,networkID)));
	        con.state = sScriptKey+'_'+memId+'_'+startDate+'_'+endDate+'_'+networkID+'_'+sRecordId;
	        con.addHttpRequest(httpEditscriptautorefilReqObj);
	        return con;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'Pharmacy_S_HUM', 'updatescriptautorefil');
            return null;
        }
        
    }
    /**
     * Returns responses of continuation object
     * <p>
     * retrieveautorefil Method to parse the service response
     *
     * @param  object state,List<string> labels .
     * @return Object
     */
    public static object retrieveautorefil(List <string> labels, object State)
    {
        try
        {
	        HttpResponse responseEditScript = Continuation.getResponse(labels[0]);
	        String editScriptResponseBody = responseEditScript.getBody();	        
	        string inputvalues = string.valueOf(state);	        
	        continuation conObj;
	        if(string.isNotBlank(inputvalues))
	        {
	            list<string> valuesInput = inputvalues.split('_');
	            string scriptKey = '';//
	            string enterpriseId = valuesInput[1];
	            string startDate = valuesInput[2];
	            string endDate = valuesInput[3];
	            string networkValue = valuesInput[4];
	            string sRecordId = valuesInput[5];
	        
                if (!editScriptResponseBody.containsIgnoreCase(VOB_FAULT_CODE) && responseEditScript != null && ((responseEditScript.getStatusCode() == HTTP_OK_HUM || responseEditScript.getStatusCode() == HTTP_ACCEPTED_HUM) || Test.isRunningTest()))
		        {
		             conObj = sendRequestGetScript(enterpriseId,scriptKey,startDate,endDate,networkValue,sRecordId);
		        }
		        else
		        {
		        	// Fault Code
		        	Pharmacy_H_HUM.logHpFaultError(responseEditScript,'Pharmacy_S_HUM', 'retrieveautorefil', sRecordId);
		        }
	        }
	        return conObj;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'Pharmacy_S_HUM', 'retrieveautorefil');
            return null;
        }
    }
    
    /**
     * Returns continuation object
     * <p>
     * Method to call continuation service for Edit member.
     *
     * @param sMemId                     String
     * @param capType                    String
     * @param networkID                  String
     * @param sRecordId                  String
     * @return                           continuation
     */
    public static continuation updatemembercaptype(string sMemId, string capType, string networkID, String sRecordId)
    {
        try
        {
	        HUM_Webservice_Callout__c serviceDetails = HUM_Webservice_Callout__c.getValues('RighSourceService_EditMember'); 
            // Create continuation with a timeout
            integer iTIMEOUT_INT_SECS = Integer.valueof(serviceDetails.Timeout__c) / 1000;
            Continuation con = new Continuation(iTIMEOUT_INT_SECS);
            con.continuationMethod = 'retrieveUpdatedCapType';
            HttpRequest httpEditGetMemberReqObj = builtHttpRequest(serviceDetails);
            httpEditGetMemberReqObj.setBody(JSON.serialize(Pharmacy_D_HUM.RequestStructureGetcaptypeResponse(sMemID,capType, networkID)));         
            con.state = sMemID+'_'+networkID+'_'+sRecordId;
            con.addHttpRequest(httpEditGetMemberReqObj);
            return con;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'Pharmacy_S_HUM', 'updatemembercaptype');
            return null;
        }
    }
    
     /**
     * Returns continuation object
     * <p>
     * Method to call continuation serive for the serive for Edit member services .
     *
     * @param sMemId                  	 String
     * @param networkID                  String
     * @param sRecordId                  String
     * @return                           continuation
     */
    public static continuation prepareUpdateMTM(string sMemId, string networkID, String sRecordId)
    {
        try
        {
	        HUM_Webservice_Callout__c serviceDetails = HUM_Webservice_Callout__c.getValues('RighSourceService_EditMember');
            // Create continuation with a timeout
            integer iTIMEOUT_INT_SECS = Integer.valueof(serviceDetails.Timeout__c) / 1000;
            Continuation con = new Continuation(iTIMEOUT_INT_SECS);
            con.continuationMethod = 'retrieveUpdatedMTM';
            HttpRequest httpEditGetMemberReqObj = builtHttpRequest(serviceDetails);
            httpEditGetMemberReqObj.setBody(JSON.serialize(Pharmacy_D_HUM.RequestStructureGetMTMResponse(sMemID, networkID)));
            con.state = sMemID+'_'+networkID+'_'+sRecordId;
            con.addHttpRequest(httpEditGetMemberReqObj);
            return con;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'Pharmacy_S_HUM', 'prepareUpdateMTM');
            return null;
        }
    }
    
      /**
     * Returns responses of continuation object
     * <p>
     * retrieveUpdatedMTM Method to parse the service response
     *
     * @param  object state,List<string> labels .
     * @return Object
     */
    public static object retrieveUpdatedMTM(List <string> labels, object State)
    {
        try
        {
            HttpResponse responseEditMember = Continuation.getResponse(labels[0]);
            String editMemberResponseBody = responseEditMember.getBody();
            string stateValues = string.valueOf(state);
            string [] stateValue = stateValues.split('_');
            string memId = stateValue[0];
            string networkID = stateValue[1];
            string sRecordId = stateValue[2];
            Continuation conObj = null;
            if (!editMemberResponseBody.containsIgnoreCase(VOB_FAULT_CODE) && responseEditMember != null && ((responseEditMember.getStatusCode() == HTTP_OK_HUM || responseEditMember.getStatusCode() == HTTP_ACCEPTED_HUM) || Test.isRunningTest()))
            {
                 conObj = SendRequestGetMemberResponse(memId,networkID,sRecordId);
            }
            else
            {
                // Fault Code
                Pharmacy_H_HUM.logHpFaultError(responseEditMember,'Pharmacy_S_HUM', 'retrieveUpdatedMTM', sRecordId);
            }
            return conObj;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'Pharmacy_S_HUM', 'retrieveUpdatedMTM');
            return null;
        }
    }
    
     /**
     * Returns continuation object
     * <p>
     * Method to call continuation service for Edit Member.
     *
     * @param sMemId                  	 String
     * @param networkId                  String
     * @param sRecordId                  String
     * @return                           continuation
     */
    public static continuation prepareUpdateConsentBeginDate(string sMemId, string networkId, String sRecordId)
    {
        try
        {
	        HUM_Webservice_Callout__c serviceDetails = HUM_Webservice_Callout__c.getValues('RighSourceService_EditMember');
            // Create continuation with a timeout
            integer iTIMEOUT_INT_SECS = Integer.valueof(serviceDetails.Timeout__c) / 1000;
            Continuation con = new Continuation(iTIMEOUT_INT_SECS);
            con.continuationMethod = 'retrieveUpdatedMemberConsent';
            HttpRequest httpEditGetMemberReqObj = builtHttpRequest(serviceDetails);
            httpEditGetMemberReqObj.setBody(JSON.serialize(Pharmacy_D_HUM.RequestStructureGetEditMemberRequestForConsentBeginDate(sMemID, networkId)));
            con.state = sMemID+'_'+sRecordId;
            con.addHttpRequest(httpEditGetMemberReqObj);
            return con;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'Pharmacy_S_HUM', 'prepareUpdateConsentBeginDate');
            return null;
        }
    }
    
    /**
     * Returns continuation object
     * <p>
     * Method to call continuation service for Edit member.
     *
     * @param sMemId                  	 String
     * @param networkID                  String
     * @param sRecordId                  String
     * @return                           continuation
     */
    public static continuation prepareUpdateConsentEndDate(string sMemId, string networkID, String sRecordId)
    {
        try
        {
            HUM_Webservice_Callout__c serviceDetails = HUM_Webservice_Callout__c.getValues('RighSourceService_EditMember');
            // Create continuation with a timeout
            integer iTIMEOUT_INT_SECS = Integer.valueof(serviceDetails.Timeout__c) / 1000;
            Continuation con = new Continuation(iTIMEOUT_INT_SECS);
            con.continuationMethod = 'retrieveUpdatedMemberConsent';
            HttpRequest httpEditGetMemberReqObj = builtHttpRequest(serviceDetails);
            httpEditGetMemberReqObj.setBody(JSON.serialize(Pharmacy_D_HUM.RequestStructureGetEditMemberRequestForConsentEndDate(sMemID, networkID)));
            con.state = sMemID+'_'+sRecordId;
            con.addHttpRequest(httpEditGetMemberReqObj);
            return con;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'Pharmacy_S_HUM', 'prepareUpdateConsentEndDate');
            return null;
        }
    }
    
    /**
     * Returns responses of continuation object
     * <p>
     * retrieveUpdatedMemberConsent Method to parse the service response
     *
     * @param  object state,List<string> labels .
     * @return Object
     */
    public static string retrieveUpdatedMemberConsent(List <string> labels, object State)
    {        
        try
        {
            HttpResponse responseEditMember = Continuation.getResponse(labels[0]);
            String editMemberResponseBody = responseEditMember.getBody();
            string stateValues = string.valueOf(state);
            string[] stateValue = stateValues.split('_');
            string memId = stateValue[0];
            string sRecordId = stateValue[1];   
            string result = null;
            if (!editMemberResponseBody.containsIgnoreCase(VOB_FAULT_CODE) && responseEditMember != null && ((responseEditMember.getStatusCode() == HTTP_OK_HUM || responseEditMember.getStatusCode() == HTTP_ACCEPTED_HUM) || Test.isRunningTest()))
            {
                result = editMemberResponseBody;
            }
            else
            {
                // Fault Code
                Pharmacy_H_HUM.logHpFaultError(responseEditMember,'Pharmacy_S_HUM', 'retrieveUpdatedMemberConsent', sRecordId);
            }   
            
            return result;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'Pharmacy_S_HUM', 'retrieveUpdatedMemberConsent');
            return null;
        }
    }
    
    /**
     * Returns continuation object
     * <p>
     * Method to call continuation serive for the serive for GetMemberResponse services .
     *
     * @param memID                  	 String
     * @param phone                  	 String
     * @param AltPhone                 	 String
     * @param Email                  	 String
     * @param captype                  	 String
     * @param networkId                  String
     * @param sRecordId                  String
     * @param addressData                AddressValidationHelper_DTO_HUM
	  * @param IsDeclined                String
     * @return                           continuation
     */
    public static continuation prepareEditGetMember(string memID, string phone, string AltPhone, string Email, string captype, string networkId, String sRecordId, AddressValidationHelper_DTO_HUM addressData,string IsDeclined)
    {
       try
        {
            HUM_Webservice_Callout__c serviceDetails = HUM_Webservice_Callout__c.getValues('RighSourceService_EditMember');
            // Create continuation with a timeout
            integer iTIMEOUT_INT_SECS = Integer.valueof(serviceDetails.Timeout__c) / 1000;
            Continuation con = new Continuation(iTIMEOUT_INT_SECS);
            con.continuationMethod = PHARMACY_EDITMEMBER_HUM;
            HttpRequest httpEditGetMemberReqObj = builtHttpRequest(serviceDetails);
            httpEditGetMemberReqObj.setBody(JSON.serialize(Pharmacy_D_HUM.RequestStructureGetEditMemberResponse(memID,phone,AltPhone,Email,captype,networkId,addressData,IsDeclined)));
            con.state = memID+'_'+networkId+'_'+sRecordId;
            con.addHttpRequest(httpEditGetMemberReqObj);
            return con;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'Pharmacy_S_HUM', 'prepareEditGetMember');
            return null;
        }
    }
    
    
    /**
     * Returns responses of continuation object
     * <p>
     * retrieveUpdatedGetMember Method to parse the service response
     *
     * @param  object state,List<string> labels .
     * @return Object
     */
    public static object retrieveUpdatedGetMember(List <string> labels, object State)
    {
        try
        {
            HttpResponse responseEditMember = Continuation.getResponse(labels[0]);
            String editMemberResponseBody = responseEditMember.getBody();
            string stateValues = string.valueOf(state);
            string[] stateValue = stateValues.split('_');
            string memId = stateValue[0];
            string networkID = stateValue[1];
            string sRecordId = stateValue[2];
            Continuation conObj = null;
            if (!editMemberResponseBody.containsIgnoreCase(VOB_FAULT_CODE) && responseEditMember != null && ((responseEditMember.getStatusCode() == HTTP_OK_HUM || responseEditMember.getStatusCode() == HTTP_ACCEPTED_HUM) || Test.isRunningTest()))
            {
                 return conObj = SendRequestGetMemberResponse(memId,networkID,sRecordId);
            }
            else if(editMemberResponseBody.containsIgnoreCase(VOB_FAULT_CODE) && (editMemberResponseBody.containsIgnoreCase('ADDR0') || (editMemberResponseBody.containsIgnoreCase('SERV00001') && editMemberResponseBody.containsIgnoreCase('does not exist in ePostRx CityStateZip table')))) 
            {
                Pharmacy_H_HUM.logHpFaultError(responseEditMember,'Pharmacy_S_HUM', 'retrieveUpdatedGetMember', sRecordId);
                return PHARMACY_ZIP_CITY_ERROR;
            }
            else return conObj;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'Pharmacy_S_HUM', 'retrieveUpdatedGetMember');
            return null;
        }
    }
   
    /**
     * Returns continuation object
     * <p>
     * Method to call continuation serive for the serive for GetMemberResponse services .
     *
     * @param memID                  	 String
     * @param networkId                  String
     * @param sRecordId                  String
     * @return                           continuation
     */
    public static Continuation SendRequestGetMemberResponse(String memID, string networkId, String sRecordId)
    {
        try
        {
            HUM_Webservice_Callout__c RSServiceDetails = HUM_Webservice_Callout__c.getValues('RighSourceService_GetMember');
            // Create continuation with a timeout
            integer iTIMEOUT_INT_SECS = Integer.valueof(RSServiceDetails.Timeout__c) / 1000;
            Continuation con = new Continuation(iTIMEOUT_INT_SECS);
            con.continuationMethod = HUM_RSGetMember_Response;
            con.state = sRecordId;
            HttpRequest httpRSGetMemberReqObj = builtHttpRequest(RSServiceDetails);
            httpRSGetMemberReqObj.setBody(JSON.serialize(Pharmacy_D_HUM.RequestStructureGetMemberResponse(memID, networkId)));
            con.addHttpRequest(httpRSGetMemberReqObj);
            return con;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'Pharmacy_S_HUM', 'SendRequestGetMemberResponse');
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
            if (String.isNotBlank(ServiceObj.Content_Type__c)) request.setHeader(HUM_RSS_SER_CONTENTTYPE, ServiceObj.Content_Type__c);
            request.setHeader(HUM_RSS_SER_CONECTION, HUM_RSS_SER_KEEPALIVE);
            request.setEndpoint(ServiceObj.End_Point_URL__c);
            request.setHeader(HUM_RSS_SER_SOAPACTION, '');
        }
        return request;
    }

    /**
     * Returns continuation object
     * <p>
     * Method to call continuation serive for the serive for GetOrderResponse services .
     *
     * @param memID                  	 String
     * @param startDate                  String
     * @param endDate                  	 String
     * @param networkId                  String
     * @param sRecordId                  String
     * @return                           continuation
     */
    public static Continuation sendRequestGetOrderResponse(String memID, String startDate, String endDate, string networkId, String sRecordId)
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
            httpRSGetOrderReqObj.setBody(JSON.serialize(Pharmacy_D_HUM.requestStructureGetOrderResponse(memID, startDate, endDate,networkId)));
            
            con.addHttpRequest(httpRSGetOrderReqObj);
            return con;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'Pharmacy_S_HUM', 'sendRequestGetOrderResponse');
            return null;
        }
    }

    /**
     * Returns continuation object
     * <p>
     * Method to call continuation serive for the serive for GetMemberResponse services .
     *
     * @param memID                  	 String
     * @param startDate                  String
     * @param endDate                  	 String
     * @param networkId                  String
     * @param sRecordId                  String
     * @return                           continuation
     */
    public static Continuation sendRequestGetMemberResponse(String memID, String startDate, String endDate, string networkId, String sRecordId)
    {
        try
        {
            HUM_Webservice_Callout__c RSServiceDetails = HUM_Webservice_Callout__c.getValues('RighSourceService_GetMember');
            // Create continuation with a timeout
            integer iTIMEOUT_INT_SECS = Integer.valueof(RSServiceDetails.Timeout__c) / 1000;
            Continuation con = new Continuation(iTIMEOUT_INT_SECS);
            con.continuationMethod = HUM_RSGetMember_Response;
            con.state = sRecordId;
            HttpRequest httpRSGetMemReqObj = builtHttpRequest(RSServiceDetails);
            httpRSGetMemReqObj.setBody(JSON.serialize(Pharmacy_D_HUM.requestStructureGetMemberResponse(memID, startDate, endDate,networkId)));
            con.addHttpRequest(httpRSGetMemReqObj);
            return con;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'Pharmacy_S_HUM', 'sendRequestGetMemberResponse');
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
				if (null != oResponseGetOrder && null != oResponseGetOrder.GetOrdersReponse && oResponseGetOrder.GetOrdersReponse.ErrorDescription != HUM_MEMBER_NOT_FOUND)
					responseDTOList = Pharmacy_D_HUM.prepareOrderSummaryResponse(oResponseGetOrder);
	        }
	        else
            {
            	// Fault Code
            	Pharmacy_H_HUM.logHpFaultError(responseOrderSummary,'Pharmacy_S_HUM', 'parseRSGetOrderResponse', State != null ? String.valueof(State) : '');
            }
	        return responseDTOList;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'Pharmacy_S_HUM', 'parseRSGetOrderResponse');
            return null;
        }
    }
	  /**
     * Returns continuation object
     * <p>
     * Method to call continuation serive for the serive for GetScript services .
     *
     * @param memID                  	 String
     * @param scriptKey                  String
     * @param startDate                  String
     * @param endDate                  	 String
     * @param networkID                  String
     * @param sRecordId                  String
     * @return                           continuation
     */
    public static Continuation sendRequestGetScript(String memID, String scriptKey, String startDate, String endDate, string networkID, String sRecordId)
    {
        try
        {
	        HUM_Webservice_Callout__c RSServiceDetails = HUM_Webservice_Callout__c.getValues('RighSourceService_GetScripts');
	        // Create continuation with a timeout
	        integer iTIMEOUT_INT_SECS = Integer.valueof(RSServiceDetails.Timeout__c) / 1000;
	        Continuation con = new Continuation(iTIMEOUT_INT_SECS);
	        con.continuationMethod = HUM_GETSCRIPTS;
	        con.state = sRecordId;
	        HttpRequest httpRSGetScriptObj = builtHttpRequest(RSServiceDetails);
	        httpRSGetScriptObj.setBody(JSON.serialize(Pharmacy_D_HUM.requestStructureGetScriptResponse(memID,scriptKey,startDate, endDate, networkID)));
	        con.addHttpRequest(httpRSGetScriptObj);
	        return con;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'Pharmacy_S_HUM', 'sendRequestGetScript');
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
    public static object parseRSGetScriptResponse(List < string > labels, Object State)
    {
        try
        {
	        list <PharmacyMemberPrescription_DTO_HUM> responseDTOList;
	        HttpResponse responsePrescriptionSummary = Continuation.getResponse(labels[0]);
	        String prescriptionSummaryrResponseBody = responsePrescriptionSummary.getBody();
	        
	        RightSourceGetScriptResponse_DTO_HUM oResponseGetMemberPrescription;
	        if (responsePrescriptionSummary != null && ((responsePrescriptionSummary.getStatusCode() == HTTP_OK_HUM || responsePrescriptionSummary.getStatusCode() == HTTP_ACCEPTED_HUM) && (!responsePrescriptionSummary.getBody().containsIgnoreCase(VOB_FAULT_CODE)) || Test.isRunningTest()))
	        {
		        oResponseGetMemberPrescription= (RightSourceGetScriptResponse_DTO_HUM) System.JSON.deserialize(prescriptionSummaryrResponseBody, RightSourceGetScriptResponse_DTO_HUM.class);
		        responseDTOList = Pharmacy_D_HUM.preparememerPrescriptionResponse(oResponseGetMemberPrescription);
	        }
	        else
            {
            	// Fault Code
            	Pharmacy_H_HUM.logHpFaultError(responsePrescriptionSummary,'Pharmacy_S_HUM', 'parseRSGetScriptResponse', State != null ? String.valueof(State) : '');
            }
	        return responseDTOList;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'Pharmacy_S_HUM', 'parseRSGetScriptResponse');
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
     * @return                               continuation
     */
    public static Continuation sendRequestGetOrderDetailResponse(String orderKey, String OrderLevel, string networkID, String sRecordId)
    {
        try
        {
	        HUM_Webservice_Callout__c RSServiceDetails = HUM_Webservice_Callout__c.getValues('RighSourceService_GetOrderDetails');
	        // Create continuation with a timeout
	        integer iTIMEOUT_INT_SECS = Integer.valueof(RSServiceDetails.Timeout__c) / 1000;
	        Continuation con = new Continuation(iTIMEOUT_INT_SECS);
	        con.continuationMethod = HUM_GETORDERDETAILRES;
	        HttpRequest httpRSGetOrderDetailReqObj = builtHttpRequest(RSServiceDetails);
	        httpRSGetOrderDetailReqObj.setBody(JSON.serialize(Pharmacy_D_HUM.requestStructureGetOrderDetailResponse(orderKey, networkID)));
	        
	        con.addHttpRequest(httpRSGetOrderDetailReqObj);
	        con.state = new PharmacyOrderDetRes_DTO_HUM (null,null,OrderLevel,networkID,sRecordId);
	        return con;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'Pharmacy_S_HUM', 'sendRequestGetOrderDetailResponse');
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
    public static Object parseRSGetOrderDetailResponse(List < string > labels, Object State)
    {
        try
        {
            PharmacyOrderDetRes_DTO_HUM objOrderDetResDTO = (PharmacyOrderDetRes_DTO_HUM) state;
            RightSourceGetOrderDetResponse_DTO_HUM.GetOrderDetailResponse oResponse = new RightSourceGetOrderDetResponse_DTO_HUM.GetOrderDetailResponse();
            HttpResponse orderDetailResponse = Continuation.getResponse(labels[0]);
            String changedOrderkey ='';
            if (!orderDetailResponse.getBody().containsIgnoreCase(VOB_FAULT_CODE) && orderDetailResponse != null && ((orderDetailResponse.getStatusCode() == HTTP_OK_HUM || orderDetailResponse.getStatusCode() == HTTP_ACCEPTED_HUM) || Test.isRunningTest()))
            {
               objOrderDetResDTO = new PharmacyOrderDetRes_DTO_HUM(Pharmacy_D_HUM.prepareOrderDetails((RightSourceGetOrderDetResponse_DTO_HUM)System.JSON.deserialize(orderDetailResponse.getbody(),RightSourceGetOrderDetResponse_DTO_HUM.class)),null,objOrderDetResDTO.orderLevel,objOrderDetResDTO.networkID,'');                 
            }
            else
            {
            	// Fault Code
            	Pharmacy_H_HUM.logHpFaultError(orderDetailResponse,'Pharmacy_S_HUM', 'parseRSGetOrderDetailResponse', objOrderDetResDTO.sRecordId);
            }
	        if(null != objOrderDetResDTO.objParentOrder && !objOrderDetResDTO.objParentOrder.OrderItems.IsEmpty())
	        {
	            
	             for (RightSourceGetOrderDetResponse_DTO_HUM.OrderItems orItem : objOrderDetResDTO.objParentOrder.OrderItems)
	             {
	                if(null != orItem.ChangedOrder && null != orItem.ChangedOrder.ChangedOrderKey && null != orItem.ChangedOrder.ChangedOrderType  && !String.IsBlank(orItem.ChangedOrder.ChangedOrderType) && !String.IsBlank(orItem.ChangedOrder.ChangedOrderKey) &&  orItem.ChangedOrder.ChangedOrderType == 'C')
	                {
	                    changedOrderkey =  orItem.ChangedOrder.ChangedOrderKey;
	                    break;
	                }  
	             }
	        }
	         if(!String.IsBlank(changedOrderkey))
	         {
	                HUM_Webservice_Callout__c RSServiceDetails = HUM_Webservice_Callout__c.getValues('RighSourceService_GetOrderDetails');
	                // Create continuation with a timeout
	                integer iTIMEOUT_INT_SECS = Integer.valueof(RSServiceDetails.Timeout__c) / 1000;
	                Continuation con = new Continuation(iTIMEOUT_INT_SECS);
	                con.continuationMethod = PHAR_PROCESSCHILDORDER_HUM;
	                HttpRequest httpRSGetOrderDetailReqObj = builtHttpRequest(RSServiceDetails);
	                httpRSGetOrderDetailReqObj.setBody(JSON.serialize(Pharmacy_D_HUM.requestStructureGetOrderDetailResponse(changedOrderkey, objOrderDetResDTO.networkID)));
	                con.addHttpRequest(httpRSGetOrderDetailReqObj);
	                con.state = objOrderDetResDTO;
	                return con;
	         }
	         else
	         {
	             return objOrderDetResDTO;
	         }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'Pharmacy_S_HUM', 'parseRSGetOrderDetailResponse');
            return null;
        }
    }

     /**
     * Returns responses of continuation object
     * <p>
     * processChildOrder Method to parse the service response
     *
     * @param List<string> labels .
     * @param  object State
     * @return Object
     */
    public static Object processChildOrder(List < string > labels, Object State)
    {
        try
        {
            PharmacyOrderDetRes_DTO_HUM objOrderDetResDTO = (PharmacyOrderDetRes_DTO_HUM) state;
            RightSourceGetOrderDetResponse_DTO_HUM.GetOrderDetailResponse oResponse = new RightSourceGetOrderDetResponse_DTO_HUM.GetOrderDetailResponse();
            HttpResponse orderDetailResponse = Continuation.getResponse(labels[0]);
            if (!orderDetailResponse.getBody().containsIgnoreCase(VOB_FAULT_CODE) && orderDetailResponse != null && ((orderDetailResponse.getStatusCode() == HTTP_OK_HUM || orderDetailResponse.getStatusCode() == HTTP_ACCEPTED_HUM) || Test.isRunningTest()))
            {
               objOrderDetResDTO = new PharmacyOrderDetRes_DTO_HUM(objOrderDetResDTO.objParentOrder,Pharmacy_D_HUM.prepareOrderDetails((RightSourceGetOrderDetResponse_DTO_HUM)System.JSON.deserialize(orderDetailResponse.getbody(),RightSourceGetOrderDetResponse_DTO_HUM.class)),objOrderDetResDTO.orderLevel,'','');                 
            }
            else
            {
            	// Fault Code
            	Pharmacy_H_HUM.logHpFaultError(orderDetailResponse,'Pharmacy_S_HUM', 'processChildOrder', objOrderDetResDTO.sRecordId);
            }
	        return objOrderDetResDTO;
		}
		catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'Pharmacy_S_HUM', 'parseRSGetOrderDetailResponse');
            return null;
        }
    }
    
    /**
     * Returns continuation object
     * <p>
     * Method to call continuation serive for the serive for CreateEditOrder services .
     *
     * @param createEditObj                  RSCreateAndEditOrderRequest_DTO_HUM
     * @param sRecordId                  	 String
     * @return                               continuation
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
            HUMExceptionHelper.logErrors(ex,'Pharmacy_S_HUM', 'sendRequestCreateEditOrderDetailRequest');
            return null;
        }
    }

    /**
     * Returns responses of continuation object
     * <p>
     * parseCreateEditOrderResponse Method to parse the service response
     *
     * @param  object state,List<string> labels .
     * @return Object
     */
    public static object parseCreateEditOrderResponse(List < string > labels, Object State)
    {
        try
        {
            HttpResponse responseCROrderSummary = Continuation.getResponse(labels[0]);
            if (responseCROrderSummary != null && ((responseCROrderSummary.getStatusCode() == HTTP_OK_HUM || responseCROrderSummary.getStatusCode() == HTTP_ACCEPTED_HUM) && (!responseCROrderSummary.getBody().containsIgnoreCase(VOB_FAULT_CODE)) || Test.isRunningTest()))
            {
                String createEditResponseBody = responseCROrderSummary.getBody();
                RSCreateAndEditOrderResponse_DTO_HUM  responseCreateEditOrder;
                responseCreateEditOrder = (RSCreateAndEditOrderResponse_DTO_HUM) System.JSON.deserialize(createEditResponseBody , RSCreateAndEditOrderResponse_DTO_HUM.class);
                if (null != responseCreateEditOrder && null != responseCreateEditOrder.CreateAndEditOrderResponse && responseCreateEditOrder.CreateAndEditOrderResponse.ErrorDescription != HUM_MEMBER_NOT_FOUND)
                    return responseCreateEditOrder.CreateAndEditOrderResponse;
                else
                    return null;
            }
	        else
            {
            	// Fault Code
            	Pharmacy_H_HUM.logHpFaultError(responseCROrderSummary,'Pharmacy_S_HUM', 'parseCreateEditOrderResponse', State != null ? String.valueof(State) : '');
            	return null;
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'Pharmacy_S_HUM', 'parseCreateEditOrderResponse');
            return null;
        }
    }
     /**
     * Returns responses of continuation object
     * <p>
     * parseRSGetMemberResponse Method to parse the service response
     *
     * @param  object state,List<string> labels .
     * @return Object
     */
    public static object retrieveUpdatedCapType(List <string> labels, object State)
    {
        try
        {
	        HttpResponse responseEditMember = Continuation.getResponse(labels[0]);
	        String editMemberResponseBody = responseEditMember.getBody();
	        string stateValues = string.valueOf(state);
	        string[] stateValue = stateValues.split('_');
	        string memId = stateValue[0];
	        string networkID = stateValue[1];
	        string sRecordId = stateValue[2];
	        Continuation conObj = null;
	        if (!editMemberResponseBody.containsIgnoreCase(VOB_FAULT_CODE) && responseEditMember != null && ((responseEditMember.getStatusCode() == HTTP_OK_HUM || responseEditMember.getStatusCode() == HTTP_ACCEPTED_HUM) || Test.isRunningTest()))
	        {
	             conObj = SendRequestGetMemberResponse(memId,networkID,sRecordId);
	        }
	        else
	        {
	        	// Fault Code
	        	Pharmacy_H_HUM.logHpFaultError(responseEditMember,'Pharmacy_S_HUM', 'retrieveUpdatedCapType', sRecordId);
	        }
	        return conObj;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'Pharmacy_S_HUM', 'retrieveUpdatedCapType');
            return null;
        }
    }


       /**
     * Returns continuation object
     * <p>
     * Method to call continuation serive for the serive for IsMemElig100DayPrescrip services .
     *
     * @param oPolMem                      String
     * @return                               continuation
     */
    
    public static continuation sendRequestIsMemElig100DayPrescrip(String oPolMem)
    {
        try
        {
            HUM_Webservice_Callout__c IsMemElig100DayPrescrip = HUM_Webservice_Callout__c.getValues('MemElig100DayPrescrip');
            // Create continuation with a timeout
            integer iTIMEOUT_INT_SECS = Integer.valueof(IsMemElig100DayPrescrip.Timeout__c) / 1000;
            Continuation con = new Continuation(iTIMEOUT_INT_SECS);
            
            HttpRequest httpIsMemElig100DayPrescrip = builtHttpRequest(IsMemElig100DayPrescrip);
            httpIsMemElig100DayPrescrip.setBody(JSON.serialize(Pharmacy_D_HUM.requestStructureIsMemElig100DayPrescrip(oPolMem)));
            con.state = oPolMem;
            con.addHttpRequest(httpIsMemElig100DayPrescrip);
            con.continuationMethod = 'parseIsMemElig100DayServiceResponse';
            return con;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'Pharmacy_S_HUM', 'sendRequestIsMemElig100DayPrescrip');
            return null;
        }
    }  
    
    
        /**
    * Returns responses of continuation object
    * <p>
    * parseMemElig100DayServiceResponse Method to parse the service response
    *
    * @param  object state,List<string> labels .
    * @return Object
    */
    public static object parseIsMemElig100DayServiceResponse(List < string > labels, Object State)
    {
        try
        {     
         IsMemElig100DayPrescripResponse_DTO_HUM oResponseDTO ;
         HttpResponse oResponse = Continuation.getResponse(labels[0]);
         String responseBody = oResponse.getBody();
         
     
        if(oResponse != null && ((oResponse.getStatusCode() == HTTP_OK_HUM || oResponse.getStatusCode() == HTTP_ACCEPTED_HUM) && (!oResponse.getBody().containsIgnoreCase(VOB_FAULT_CODE)) || Test.isRunningTest()))
         {
             oResponseDTO = (IsMemElig100DayPrescripResponse_DTO_HUM)System.JSON.deserialize(responseBody, IsMemElig100DayPrescripResponse_DTO_HUM.class);
             if(oResponseDTO.SearchResponse.ResponseStatus.Success){
             if(oResponseDTO.SearchResponse.Is100DaysPrescribed)
             {
              Return 'Y';
             }
             else
             {
              Return 'N';
             }
             }
             
         }
         return null;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'Pharmacy_S_HUM', 'parseIsMemElig100DayServiceResponse');
            return null;
        }
    }
}