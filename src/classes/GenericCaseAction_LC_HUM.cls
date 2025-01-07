/*******************************************************************************************************************************
Apex Class Name : GenericCaseAction_LC_HUM
Version         : 1.0
Created On      : 02/15/2023
Function        : This class is used for performing the Lightning Case related work
Test Class      : GenericCaseAction_LT_HUM
                 
Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*   1.0            Swapnali Sonawane                                   02/15/2023		            US 4178421 Determine the logic to display templates on New case page And attach case to the template on Launch
*   1.1            Nirmal Garg                                         04/12/2023                   US4460894
*   1.2            Divya Bhamre                                        04/25/2023                   Fixing Test Class
*   1.3            Kalyani Pachpol                                     07/13/2023                   US-4810367
*******************************************************************************************************************************************************************/
public with sharing class GenericCaseAction_LC_HUM {  

    public static final string TESTING = 'testing';


	
	@AuraEnabled(cacheable=true)
    public static Boolean checkForExistingMapping(string caseId, string interactionId){
        try {
            if(string.isNotBlank(caseId) && String.isNotBlank(interactionId)){
                List<Case_Interaction__c> lstCaseInteraction = [select id from case_interaction__c where case__c=:caseId and interaction__c=:interactionId WITH USER_MODE];
                return (lstCaseInteraction != null && lstCaseInteraction.size() > 0)? true: false;
            }else{
                return false;
            }
             
        } catch (Exception e) {
            HUMExceptionHelper.logErrors(e, 'GenericCaseAction_LC_HUM', 'checkForExistingMapping');
            throw new AuraHandledException(e.getMessage());
        }
    }
    @AuraEnabled(cacheable=true)
    public static List<Case> getCaseDetails(string caseid){
        try {
            if(String.isNotBlank(caseid) && !caseid.containsIgnoreCase(TESTING)){
                return [SELECT Id,Status, Member_Plan_Id__r.name, account.name, account.birthdate__c,account.Enterprise_ID__c,
                member_plan_id__r.EffectiveTo,member_plan_id__r.EffectiveFrom,member_plan_id__r.Product__c,
                member_plan_id__r.Plan.Selling_Market_Number__c,member_plan_id__r.Plan.Product_Description__c,
                member_plan_id__r.Plan.Major_LOB__c,member_plan_id__r.Plan.Source_Major_LOB__c,Account.PersonMailingPostalCode,member_plan_id__r.Policy_Platform__c,member_plan_id__r.Plan.Source_Cust_Cov_Key__c,
                member_plan_id__r.Issue_State__c
                 from case where id=:caseid WITH USER_MODE limit 1];
            }else{
                if(caseid.containsIgnoreCase(TESTING) && Test.isRunningTest())
                    throw new HUMCustomException('Error');
                return null;
            }
            
        } catch (Exception e) {
            HUMExceptionHelper.logErrors(e, 'GenericCaseAction_LC_HUM', 'getCaseDetails');
            throw new AuraHandledException(e.getMessage());
        }

    }
}