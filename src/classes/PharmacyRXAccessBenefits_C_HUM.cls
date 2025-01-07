/**************************************************************************** 
Apex Class Name  : PharmacyRXAccessBenefits_C_HUM   
Version          : 1.0  
Created Date     : May 6, 2021
Function         : This controller Class contains methods for KMDocumentServiceREST services to fetch  document information.
******************************************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*------------------------------------------------------------------------------------------------------------
* Suraj Patil     				               05/06/2021                 REQ - 1003582 - 1011772 - 1694036
***************************************************************************************************/
public with sharing class PharmacyRXAccessBenefits_C_HUM 
{    
	public string MentorHomeURL{get;set;}
    public PharmacyRXAccessBenefits_C_HUM (){
        HUMCertificateURL__c objAppURL = HUMCertificateURL__c.getValues('MentorHomeURL');
        MentorHomeURL = objAppURL.CertificateListURL__c;
    }
    /**
	* Returns request object of continuation object     
	* Method callAccessBenefitMentorService to invoke the 
	* @parameter policyMemberId
	* @return continuation object
	*/
    
    @AuraEnabled(continuation=true)
    Public static object callAccessBenefitMentorService(string policyMemberId){    
        return PharmacyRXAccessBenefits_S_HUM.invokeAccessBenefitsMentorService(policyMemberId);    
    } 
    
    /**
	* Returns responses of continuation object    
	* Callback Method processAccessBenefitMentorService to parse the service response
	* @parameter labels, object   
	* @return object
	*/
    @AuraEnabled
    public static Object processAccessBenefitMentorService(List<string> labels, object state){        
        return PharmacyRXAccessBenefits_S_HUM.processAccessBenefitMentorService(labels, state);       
    }
}