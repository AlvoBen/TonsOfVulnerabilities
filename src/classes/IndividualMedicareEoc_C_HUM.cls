/*******************************************************************************************************************************
Apex Class Name : IndividualMedicareEoc_C_HUM.cls
Version         : 1.0
Created On      : 04/16/2018
Apex Test Class : IndividualMedicareEoc_C_HUM.cls
Function        : Class contains methods for redirecting to the Individual Medicare evidence of coverage document and pass the needed parameters.
IndividualMedicareEocURL::https://www.humana-medicare.com/BenefitSummary

Modification Log: 
* Developer Name     Date               Description
*------------------------------------------------------------------------------------------------------------------------------
* Dinesh Subramaniyan       04/16/2018          Original Version 
* Asish Behera              06/11/2018          REQ - 366090 - Ability to Prefill the Year from the Policy Effective Date into the Individual Evidence of Coverage URL (RF)
* Asish Behera              07/06/2018          REQ - 367782 - AHT - Ability to Prefill the Year from the Policy Effective Date into the Individual Evidence of Coverage URL (CRM)
*****************************************************************************************************************************************/

public with sharing class IndividualMedicareEoc_C_HUM {


        
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
             string sPolMemId;
             string sFormularyRedirectURL = Constants_C_AHT_HUM.INDIVIDUAL_MEDICARE_EOC_DEFAULT_URL;
                    
                    if(ApexPages.currentpage().getParameters().containsKey('Id'))
                    {
                        sPolMemId = ApexPages.currentpage().getParameters().get('Id');
                    } 
                    if(String.isNotBlank(sPolMemId))
                    {
                        List<Policy_Member__c> lstIndividualMedicareEoc = [SELECT Id,Policy__r.Contract_Number__c,Policy__r.PBP_Code__c,Policy__r.Medicare_Segment_ID__c,status__c,Effective_Date__c,End_Date__c
                                                                                                  FROM Policy_Member__c 
                                                                                                  Where id =:sPolMemId limit 1];
                        if(!lstIndividualMedicareEoc.isempty())
                        {
                            string sConcPolicyValues =  lstIndividualMedicareEoc[0].Policy__r.Contract_Number__c + lstIndividualMedicareEoc[0].Policy__r.PBP_Code__c+lstIndividualMedicareEoc[0].Policy__r.Medicare_Segment_ID__c;
                            if(!sConcPolicyValues.contains('null'))       
                            {                                                                 
                                Integer currentYear = System.Today().year();            
                            	if (lstIndividualMedicareEoc[0].status__c == Constants_C_AHT_HUM.POLICYSTATUS_TERMED && String.isNotBlank(lstIndividualMedicareEoc[0].End_Date__c))
                                {
                                	currentYear = Integer.valueof(lstIndividualMedicareEoc[0].End_Date__c.substring(lstIndividualMedicareEoc[0].End_Date__c.lastindexof('/')+1,lstIndividualMedicareEoc[0].End_Date__c.length()));
                                }else if(lstIndividualMedicareEoc[0].status__c == Constants_C_AHT_HUM.POLICYSTATUS_FUTURE && String.isNotBlank(lstIndividualMedicareEoc[0].Effective_Date__c))
                                {
                                	currentYear = Integer.valueof(lstIndividualMedicareEoc[0].Effective_Date__c.substring(lstIndividualMedicareEoc[0].Effective_Date__c.lastindexof('/')+1,lstIndividualMedicareEoc[0].Effective_Date__c.length()));   
                                }    
                                      
                                sFormularyRedirectURL = Constants_C_AHT_HUM.INDIVIDUAL_MEDICARE_EOC_URL + currentYear + Constants_C_AHT_HUM.INDIVIDUAL_MEDICARE_EOC_PDF+ sConcPolicyValues+Constants_C_AHT_HUM.INDIVIDUAL_MEDICARE_EOC+string.valueOf(currentYear).right(2)+Constants_C_AHT_HUM.INDIVIDUAL_MEDICARE_EOC_DOT_PDF;
                            }
                        }
                    }
                
                redirecToPage = new PageReference(sFormularyRedirectURL);
                redirecToPage.setRedirect(true);    
                
             }
             catch (Exception ex) 
             {
                
                HUMExceptionHelper.logErrors(ex, 'IndividualMedicareEoc_C_HUM', 'updateURLAndLaunch');
             }
              
             return redirecToPage;
        }
}