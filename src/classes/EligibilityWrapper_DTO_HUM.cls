/*******************************************************************************************************************************
Apex Class Name : EligibilityWrapper_DTO_HUM
Version         : 1.0
Created On      : 06/08/2021
Function        : Used to display Eligibility section on Member Plan
Test Class      : MemberPlanEligibility_LT_HUM 

Modification Log: 
* Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Ankima/Kajal                                      06/08/2021                   Original Version
*Ankima                                             06/29/2021                   Eligibility Switch Implementation
*Ankima                                             07/01/2021                   Cost Share Protected Field Implementation
*Ankima                                             07/26/2021                   Cost Share Protected Field Rollback
*Ankima                                             09/01/2021                   Cost Share Protected Field Implementation for new requirement
*Ankima                                             09/06/2021                   Cost Share Protected Field Implementation for new requirement
*Ankima                                             09/13/2021                   Cost Share Protected Field Rollback
*Ankima                                             09/20/2021                   Cost Share Protected Field Re-Deploy
*Ankima                                             09/30/2021                   Eligibility By Product Type Changes
*******************************************************************************************************************************/  
public with sharing class EligibilityWrapper_DTO_HUM {
     @AuraEnabled public Map<String,Map<String,String>> EligibilityServiceResponse {get;set;}
     @AuraEnabled public List<String> EligibilitySection {get;set;}
     @AuraEnabled public List<String> EligibilityField {get;set;}
     @AuraEnabled public MemberPlan MemPlanInfo{get;set;}
     @AuraEnabled public Boolean EligibilitySwitch{get;set;}
     @AuraEnabled public Boolean showCostShare{get;set;}
     @AuraEnabled public String costShareInfo{get;set;}
     @AuraEnabled public Boolean showEligibilitySections{get;set;} 
     @AuraEnabled public List<String> showContractInfo{get;set;}
     
}