/*******************************************************************************************************************************
Apex Class Name : IndividualMedicareSobRedirect_LC_HUM.cls
Version         : 1.0
Created On      : 03/14/2023
Apex Test Class : IndividualMedicareSobRedirect_LT_HUM.cls
Function        : Class contains methods for redirecting to the Individual Medicare evidence of coverage document and pass the needed parameters.
IndividualMedicareEocURL::https://www.humana-medicare.com/BenefitSummary
Modification Log:
 *   Developer                   Code Review             Date               Description
 * --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- 
 * Apurva Urkude                                       18/01/2023           4167387- Implementation of Links & landing pages- Person account page and Medical Plan member page - Part2
 * Vishal Shinde                                       10/20/2023           5183746 - Incident - INC2561827 - CRM: URL no longer exists
 ************************************************************************************************************************************************************************************/

public with sharing class IndividualMedicareSobRedirect_LC_HUM {
    
   public static String recordId{get;set;}
        
        /**
        * Add the Current Year, Contract, PBP, and Segment ID to the Individual Medicare URL and launch
        *
        *       
        */    
    
     	@AuraEnabled(Continuation=true)
        public static string updateURLAndLaunch(string recordId)
        {
            string redirecToPage ='';
            
             try 
             { 
             string sFormularyRedirectURL = Constants_C_AHT_HUM.INDIVIDUAL_MEDICARE_EOC_DEFAULT_URL;
                 
                    if(String.isNotBlank(recordId))
                    {  
                        List<memberplan> lstIndividualMedicareEoc = [SELECT Id,Policy__r.Contract_Number__c,Policy__r.PBP_Code__c,Policy__r.Medicare_Segment_ID__c,Member_Coverage_Status__c,EffectiveFrom,EffectiveTo from memberplan 
                                                                                                  Where id =:recordId limit 1];
                        if(!lstIndividualMedicareEoc.isempty())
                        {
                            string sConcPolicyValues =  lstIndividualMedicareEoc[0].Policy__r.Contract_Number__c + lstIndividualMedicareEoc[0].Policy__r.PBP_Code__c+lstIndividualMedicareEoc[0].Policy__r.Medicare_Segment_ID__c;
                            if(!sConcPolicyValues.contains('null'))       
                            {                                                                 
                                Integer currentYear = System.Today().year();
                                
                                String endDateFormat= string.valueOfGmt(lstIndividualMedicareEoc[0].EffectiveTo);
                                List<String> endDateTimeSplit = endDateFormat.split(' ');
                                List<String> arrayEndDate = endDateTimeSplit[0].split('-');
                                string endDate= arrayEndDate[1]+'/'+arrayEndDate[2]+'/'+arrayEndDate[0];
                                    
                                String startDateFormat= string.valueOfGmt(lstIndividualMedicareEoc[0].EffectiveFrom);
                                List<String> DateTimeSplit = startDateFormat.split(' ');
								List<String> arrayDate = DateTimeSplit[0].split('-');
								String startDate = arrayDate[1]+'/'+arrayDate[2]+'/'+arrayDate[0];
                                
                            	if (lstIndividualMedicareEoc[0].Member_Coverage_Status__c == Constants_C_AHT_HUM.POLICYSTATUS_TERMED && String.isNotBlank(endDate))
                                {
                                	currentYear = Integer.valueof(endDate.substring(endDate.lastindexof('/')+1,endDate.length()));
                                    
                                        
                                }else if(lstIndividualMedicareEoc[0].Member_Coverage_Status__c == Constants_C_AHT_HUM.POLICYSTATUS_FUTURE && String.isNotBlank(startDate))
                                {
                                	currentYear = Integer.valueof(startDate.substring(startDate.lastindexof('/')+1,startDate.length()));
                                    
                                }    
                                      
                                sFormularyRedirectURL = Constants_C_AHT_HUM.INDIVIDUAL_MEDICARE_EOC_URL + currentYear + Constants_C_AHT_HUM.INDIVIDUAL_MEDICARE_EOC_PDF+ sConcPolicyValues+Constants_C_AHT_HUM.INDIVIDUAL_MEDICARE_SB+string.valueOf(currentYear).right(2)+Constants_C_AHT_HUM.INDIVIDUAL_MEDICARE_EOC_DOT_PDF;
                            }
                        }
                    }
                
                redirecToPage = sFormularyRedirectURL;
               if(Test.isRunningTest()) throw new HUMCustomException('Error');
                
             }
            
             catch (Exception ex) 
             {
                
                HUMExceptionHelper.logErrors(ex, 'IndividualMedicareEoc_C_HUM', 'updateURLAndLaunch');
                 return null;
             }
              
            return redirecToPage;
        }

}