/*******************************************************************************************************************************
Apex Class Name : BenefitsVerificationRedirect_C_HUM.cls
Version         : 1.0
Created On      : 2020
Function        : Class contains methods for redirecting to the Benefits Verification (Pilot) and passing parameters.

Modification Log: 
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Sayali Gavane                            				10/16/2020                   Original Version 
*****************************************************************************************************************************************/
public with sharing class BenefitsVerificationRedirect_C_HUM
{
	public String memberID {get;set;}
	public String sBaseURL= null;
	public String benVerifUrl = null;
	
	public BenefitsVerificationRedirect_C_HUM() 
	{
		memberID = Apexpages.currentPage().getParameters().get('Id'); 	
	}   

	public PageReference redirectBenefitsVerification()
	{      
		PageReference returnPage = null;
		benVerifUrl = Endpoint_Url_HUM__c.getInstance('Benefits_Verification_URL').Remote_Url__c;		
		if(memberID !=null)
		{
			List<Policy_member__c> lstPolMem= new List<Policy_member__c>();          
			lstPolMem = [SELECT  Member__r.Enterprise_ID__c FROM Policy_Member__c where Id=:memberID ];
			if(lstPolMem != null && lstPolMem[0].Member__r.Enterprise_ID__c !=null)
			{
				String enterpriseID = lstPolMem[0].Member__r.Enterprise_ID__c;		
				Blob blobEnterpriseId = Blob.valueOf(enterpriseID);
				enterpriseID = EncodingUtil.base64Encode(blobEnterpriseId);
				sBaseURL= benVerifUrl+'enterpriseId='+enterpriseID ;    
			}
			else
			{
				sBaseURL= benVerifUrl;    
			}		
			returnPage = new PageReference(sBaseURL);
			returnPage.setRedirect(true);           
		}
		return returnPage;
	}
}