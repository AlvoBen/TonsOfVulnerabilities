/*******************************************************************************************************************************
Apex Class Name : PharmacyBenefits_LS_HUM.cls
Version         : 1.0
Created On      : 10/14/2021
Function        : Class contains methods for services to fetch Benefits Information.
TestClass       : PharmacyBenefits_LT_HUM

Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Nirmal Garg											10/14/2021				  Initial Version - US26489489 - Adding for Pharmacy lightning
* Jonathan Dickinson                                    10/20/2021                Added methods from non-lightning classes 
*                                                                                 and modified them to work with the new lightning classes
* M K Manoj                                             11/04/2022               US-3578373 and US-3578548
* Pinky Vijur                                           01/23/2023                User Story 3578587: T1PRJ0870026 Solutran Integrations - CRM FR5.01 - TECH - Remove CRM Service coding related to InComm OTC Balance
* Monali Jagtap                                         09/29/2023                US- 4429836 T1PRJ0891339 - MF 1 - SF -TECH - Error Handling - Make sure all exceptions are handled with appropriate messages
****************************************************************************************************************************************/
public with sharing class PharmacyBenefits_LS_HUM 
{     
     Public Static final String HUM_PRIORAUTH_RESPONSE = 'parseGetPAuthResponse';
     Public Static final String Content_type_HUM = 'content-type';
     Public Static final String Connection_HUM = 'Connection';
     Public Static final String Keep_alive_HUM = 'keep-alive';
     Public Static final String SOAPAction_HUM = 'SOAPAction';
     Public Static final String VOB_FAULT_CODE = 'faultcode';
     Public Static final Integer HTTP_OK_HUM = 200;
     Public Static final Integer HTTP_ACCEPTED_HUM = 202;
     Public Static final String PHARMACYBENEFITSSOLUTRAN_PARSEMETHOD='parseSoltrnOTCBalanceV3';
    public static string requestlabel = '';
    /**
     * Returns continuation object
     * <p>
     * Method to call continuation serive for the serive for GetpAuthResponse services .
     *
     * @param   String          Enterprise Id
     * @return  Continuation
     */
    
    public static object SendRequestGetpAuthResponse(String sMemID)
    {
        try
        {
            HUM_Webservice_Callout__c PAHubDetails = HUM_Webservice_Callout__c.getValues('PriorAuthHub');
            
            // Create continuation with a timeout
            integer iTIMEOUT_INT_SECS = Integer.valueof(PAHubDetails.Timeout__c) / 1000;
            Continuation con = new Continuation(iTIMEOUT_INT_SECS);
            con.continuationMethod = HUM_PRIORAUTH_RESPONSE; 
            HttpRequest httpPAuthReqObj = builtHttpRequest(PAHubDetails);
            httpPAuthReqObj.setBody(JSON.serialize(PharmacyBenefits_LD_HUM.RequestStructureGetpAuthResponse(sMemID)));
            con.state = sMemID;
           	con.addHttpRequest(httpPAuthReqObj);
            return con;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'PharmacyBenefits_LS_HUM', 'SendRequestGetpAuthResponse');
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
     * Returns responses of continuation object
     * <p>
     * parseGetPAuthResponse Method to parse the service response
     *
     * @param  object state,List<string> labels .
     * @return Object
     */
    
    public static object parseGetPAuthResponse(List <string> labels, Object State)
    {
        
        try
        {
            List<PriorAuthHubWrapper> oResponse = new List<PriorAuthHubWrapper>();
            HttpResponse pAResponse = Continuation.getResponse(labels[0]);
      		String sFaultResponse = '';
			PriorAuthHubResponse_DTO_HUM PAHubInstance;
            if (pAResponse != null && !pAResponse.getBody().containsIgnoreCase(VOB_FAULT_CODE)  && ((pAResponse.getStatusCode() == HTTP_OK_HUM || pAResponse.getStatusCode() == HTTP_ACCEPTED_HUM) || Test.isRunningTest()))
            {
                String paHubResponseBody = pAResponse.getBody();                
                PAHubInstance = (PriorAuthHubResponse_DTO_HUM)System.JSON.deserialize(paHubResponseBody,PriorAuthHubResponse_DTO_HUM.class);
            }else
            {
              // Fault Code
              sFaultResponse = Pharmacy_H_HUM.logHpFaultError(pAResponse,'PharmacyBenefits_LS_HUM', 'parseGetPAuthResponse', State != null ? String.valueof(State) : '').contains('Member not found') ? 'true' : 'false';
            }
            return PAHubInstance;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'PharmacyBenefits_LS_HUM', 'parseGetPAuthResponse');
            throw new AuraHandledException(ex.getMessage());
        }
    }

	/**
     * Returns continuation object
     * <p>
     * Method to call continuation for Soltrn OTC Balance 
     *
     * @param   String sMemberid ,sPBPCode ,sContractNumber ,sProductDescription
     * @return  Continuation
     */
     
    public static object requestSoltrnOTCBalanceV3(String sMemberid, String sPBPCode, string sContractNumber, string sProductDescription)
    {
        try
        {
            PharmacyBenefitsSoltrnRequest_H_HUM oSoltrnReq = new PharmacyBenefitsSoltrnRequest_H_HUM();
           oSoltrnReq = PharmacyBenefits_LD_HUM.requestStructureGetSoltrnRequestV3(sMemberid, sPBPCode, sContractNumber, sProductDescription);
            HUM_Webservice_Callout__c PBSoltrnOTC = HUM_Webservice_Callout__c.getValues('PharmacyBenefits_SolutranOTC_V3');
            integer iTIMEOUT_INT_SECS = Integer.valueof(PBSoltrnOTC.Timeout__c) / 1000;
            Continuation con = new Continuation(iTIMEOUT_INT_SECS);
            con.continuationMethod = PHARMACYBENEFITSSOLUTRAN_PARSEMETHOD;
            con.state = sMemberid;
            HttpRequest httpRequestOTC = builtHttpRequest(PBSoltrnOTC);
            String sRequest = JSON.serialize(oSoltrnReq);
            httpRequestOTC.setBody(sRequest);
            con.addHttpRequest(httpRequestOTC);
            return con;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'PharmacyBenefits_LS_HUM', 'requestSoltrnOTCBalanceV3');
            return null;
        }
        
    }



/**
     * Returns responses of continuation object
     * <p>
     * parseSoltrnOTCBalanceV3 Method to parse the service response
     *
     * @param  object state,List<string> labels .
     * @return String
     */
    public static Object parseSoltrnOTCBalanceV3(List<string> labels,object state)
    {
    	try
    	{ 
    		HttpResponse otcResponse = Continuation.getResponse(labels[0]);
    		if (otcResponse != null && !otcResponse.getBody().containsIgnoreCase(VOB_FAULT_CODE)  && ((otcResponse.getStatusCode() == HTTP_OK_HUM || otcResponse.getStatusCode() == HTTP_ACCEPTED_HUM) || Test.isRunningTest()))
            {
                String OTCResponseBody = otcResponse.getBody();
                PharmacyBenefitsSoltrnResponse_H_HUM oResponseOTC;
                oResponseOTC = (PharmacyBenefitsSoltrnResponse_H_HUM)System.JSON.deserialize(OTCResponseBody,PharmacyBenefitsSoltrnResponse_H_HUM.class);
                if(oResponseOTC != null && oResponseOTC.BalanceInformation.Balance != null && !oResponseOTC.BalanceInformation.Balance.isEmpty()) 
                { 
                        return oResponseOTC;
				}
				else
				{
					return null;
				}
			}
		}
    	catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'PharmacyBenefits_LS_HUM', 'parseSoltrnOTCBalanceV3');
        }
    	return null;
    }

}