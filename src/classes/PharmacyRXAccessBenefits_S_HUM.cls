/**************************************************************************** 
Apex Class Name  : PharmacyRXAccessBenefits_S_HUM   
Version          : 1.0  
Created Date     : May 6, 2021
Function         : This service Class contains methods for KMDocumentServiceREST services to call the service and get the response
******************************************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*------------------------------------------------------------------------------------------------------------
* Suraj Patil     				               05/06/2021                 REQ - 1003582 - 1011772 - 1694036
***************************************************************************************************/
public with sharing class PharmacyRXAccessBenefits_S_HUM {
    
    /**
	* Returns request object of continuation object     
	* Method invokeAccessBenefitsMentorService to invoke the KMDocumentServiceREST/DoSearch to show Documents from mentor.
	* @parameter policyMemberId
	* @return continuation object
	*/    
    @AuraEnabled(continuation=true)
	public static Continuation invokeAccessBenefitsMentorService(string policyMemberId)
	{    
        try
        {
			HUM_Webservice_Callout__c pharmacyRxAccessBenefitsService;
			pharmacyRxAccessBenefitsService = HUM_Webservice_Callout__c.getValues('KMDocumentServiceREST_DoSearch');

			integer iTIMEOUT_INT_SECS = Integer.valueof(pharmacyRxAccessBenefitsService.Timeout__c) / 1000;
			Continuation con = new Continuation(iTIMEOUT_INT_SECS);
			con.continuationMethod = 'processAccessBenefitMentorService';           
			HttpRequest httpPharmacyRXAccessBenefitsMentorRequest = builtHttpRequest(pharmacyRxAccessBenefitsService);
			httpPharmacyRXAccessBenefitsMentorRequest.setBody(JSON.serialize(PharmacyRXAccessBenefits_D_HUM.requestStructurePharmacyRXAccessBenefits(policyMemberId)));

			con.addHttpRequest(httpPharmacyRXAccessBenefitsMentorRequest);           
			return con;
        }
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'PharmacyRXAccessBenefits_S_HUM', 'invokeAccessBenefitsMentorService');
            return null;
        }
    }
    
    /**
	* Returns responses of continuation object    
	* Callback Method processAccessBenefitMentorService to parse the KMDocumentServiceREST/DoSearch service response
	* @parameter labels, object   
	* @return object
	*/
    @AuraEnabled
    public static Object processAccessBenefitMentorService(List < string > labels, Object State) 
	{
        try
        {
            HttpResponse response = Continuation.getResponse(labels[0]);  
            String mentorServiceResponse = response.getBody();
             
            if (!mentorServiceResponse.containsIgnoreCase(GLOBAL_CONSTANT_HUM.VOB_FAULT_CODE) && response != null && ((response.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_OK_HUM || response.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_ACCEPTED_HUM) || Test.isRunningTest()))
            {
                PharmacyRXAccessBenefits_Wrapper_HUM res = PharmacyRXAccessBenefits_D_HUM.responseStructurePharmacyRXAccessBenefits(response.getbody());
                return res;
            }
            else{
                PharmacyRXAccessBenefits_D_HUM.insertFaultExceptionToErrorLog(''+response.getStatusCode(), 'service Issue');
                return null;
            }
                
        } catch (Exception ex)
        {                
            HUMExceptionHelper.logErrors(ex, 'PharmacyRXAccessBenefits_S_HUM', 'processAccessBenefitMentorService');
            return null;
        }
    }
    
    
	/**
	* Returns HttpRequest object    
	* builtHttpRequest Method to parse HUM_Webservice_Callout__c RxClaimSummary_GetClaims
	* @parameter HUM_Webservice_Callout__c ServiceObj  
	* @return HttpRequest
	*/
    public static HttpRequest builtHttpRequest(HUM_Webservice_Callout__c ServiceObj) 
	{
        HttpRequest request = new HttpRequest();
        if (ServiceObj != null && ServiceObj.Active__c) 
        {
            request.setMethod(ServiceObj.Request_Type__c);
            if (!Test.isRunningTest() && String.isNotBlank(ServiceObj.Certificate_Name__c)) {                 
                request.setClientCertificateName(ServiceObj.Certificate_Name__c);                
            }
            
            if (String.isNotBlank(ServiceObj.Content_Type__c)) request.setHeader(HUMConstants.HUM_RSS_SER_CONTENTTYPE, ServiceObj.Content_Type__c);
			
            request.setHeader(HUMConstants.HUM_RSS_SER_CONECTION, HUMConstants.HUM_RSS_SER_KEEPALIVE);
            request.setEndpoint(ServiceObj.End_Point_URL__c);
            request.setHeader(HUMConstants.HUM_RSS_SER_SOAPACTION, '');
        }
        return request;
    }
}