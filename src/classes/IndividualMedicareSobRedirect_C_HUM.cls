/*******************************************************************************************************************************
Apex Class Name : IndividualMedicareSobRedirect_C_HUM.cls
Version         : 1.0
Created On      : 04/16/2018
Apex Test Class : 
Function        : Class contains methods for redirecting to the Individual Medicare summary of benefits document and pass the needed parameters.
IndividualMedicareSObURL::https://www.humana-medicare.com/BenefitSummary

Modification Log: 
 * Developer Name     Date               Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Asish Behera       06/05/2018          REQ - 366095 -Add Individual Medicare Summary of Benefits link to the Member Policy Page (RF)
 * Asish Behera       07/06/2018          REQ - 367902 -AHT - Ability to Prefill the Year from the Policy Effective Date into the Individual Medicare Summary of Benefits URL (RF)
 * Asish   Behera     01/22/2019          REQ - 382809 AHT - Add Individual Medicare Summary of Benefits link to the Member Policy Page (CRM)
 *****************************************************************************************************************************************/
public with sharing class IndividualMedicareSobRedirect_C_HUM {


	/**
	 * Add the Current Year, Contract, PBP, and Segment ID to the Individual Medicare URL and launch
	 *
	 * @return  pageURl      
	 */    
	public pageReference updateURLAndLaunch()
	{
		PageReference redirecToPage = null;
		try 
		{
			String sPolMemId = ApexPages.currentpage().getParameters().get('Id');
			String sFormularyRedirectURL = Constants_C_AHT_HUM.INDIVIDUAL_MEDICARE_EOC_DEFAULT_URL;

			if(String.isNotBlank(sPolMemId))
			{
				                                                   
				List<Policy_Member__c> lstIndividualMedicareSob = [SELECT Id,Policy__r.Contract_Number__c,Policy__r.PBP_Code__c,Policy__r.Medicare_Segment_ID__c,status__c,Effective_Date__c,End_Date__c
                                                                                                  FROM Policy_Member__c 
                                                                                                  Where id =:sPolMemId limit 1];
				if(!lstIndividualMedicareSob.isempty())
				{
					String sConcPolicyValues =  lstIndividualMedicareSob[0].Policy__r.Contract_Number__c + lstIndividualMedicareSob[0].Policy__r.PBP_Code__c+lstIndividualMedicareSob[0].Policy__r.Medicare_Segment_ID__c;
					if(String.isNotBlank(sConcPolicyValues))       
					{                                                                 
						Integer currentYear = System.Today().year();  
							
							if (lstIndividualMedicareSob[0].status__c == Constants_C_AHT_HUM.POLICYSTATUS_TERMED && String.isNotBlank(lstIndividualMedicareSob[0].End_Date__c)){
                             
                                	currentYear = Integer.valueof(lstIndividualMedicareSob[0].End_Date__c.substring(lstIndividualMedicareSob[0].End_Date__c.lastindexof('/')+1,lstIndividualMedicareSob[0].End_Date__c.length()));
                            }else if(lstIndividualMedicareSob[0].status__c == Constants_C_AHT_HUM.POLICYSTATUS_FUTURE && String.isNotBlank(lstIndividualMedicareSob[0].Effective_Date__c)){
                                
                                	currentYear = Integer.valueof(lstIndividualMedicareSob[0].Effective_Date__c.substring(lstIndividualMedicareSob[0].Effective_Date__c.lastindexof('/')+1,lstIndividualMedicareSob[0].Effective_Date__c.length()));   
                            }   
						sFormularyRedirectURL = Constants_C_AHT_HUM.INDIVIDUAL_MEDICARE_EOC_URL + currentYear + Constants_C_AHT_HUM.INDIVIDUAL_MEDICARE_EOC_PDF+ sConcPolicyValues+Constants_C_AHT_HUM.INDIVIDUAL_MEDICARE_SB+string.valueOf(currentYear).right(2)+Constants_C_AHT_HUM.INDIVIDUAL_MEDICARE_EOC_DOT_PDF;

					}
				}
			}
			redirecToPage = new PageReference(sFormularyRedirectURL);
			redirecToPage.setRedirect(true);    

		}
		catch (Exception ex) 
		{
			HUMExceptionHelper.logErrors(ex, 'IndividualMedicareSobRedirect_C_HUM', 'updateURLAndLaunch');
		}

		return redirecToPage;
	}
}