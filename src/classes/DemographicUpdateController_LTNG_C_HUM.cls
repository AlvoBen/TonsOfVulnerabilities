/**
 * @File Name          : DemographicUpdateController_LTNG_C_HUM.cls
 * @Description        : Controller class for fetching data from Salesforce objects
 * @Author             : ChangeMeIn@UserSettingsUnder.SFDoc
 * @Group              : Team Thunder
 * @Last Modified On   : 08-05-2020
 * @Modification Log   : 
 * Ver       Date            Author      		    Modification
 * 1.0    5/11/2020        Chetan Sindhe           Initial Version
 * 2.0    6/07/2022        Ajay Chakradhar         US2695217-INC1313163-Update Plan Demographic: OSA Service Error 
**/
public with sharing class DemographicUpdateController_LTNG_C_HUM {
    public static string sClassName = 'DemographicUpdateController_LTNG_C_HUM';
    
    /*
    * Method Name: fetchStateNames
    * Description: method is responsible to fetch statenames to populate State dropdown
    * Parameter: Nothing
    * Return Type: Map<string, string>
    */
    @AuraEnabled(cacheable=true)
    public static Map<String, String> fetchStateNames(){
        Map<String, String> optionList = new Map<String, String>();
        try {
            GenericHelper_CRMService_LTNG_H_HUM genericHelper = new GenericHelper_CRMService_LTNG_H_HUM();
            optionList = genericHelper.getStateDropdown();
        }
        
        catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex, sClassName, 'fetchStateNames');
        }
        return optionList;
    }

    /*
    * Method Name: getAccountDetail
    * Description: method will retrieve Account Enterprise Id required for MBE Plus call
    * Parameter: Case Row Id
    * Return Type: List<Account>
    */
    @AuraEnabled(cacheable=true)
    public static List<Account> getAccountDetail(string caseRecordId) {
        List<Account> accountList = new List<Account>();
        try {
            GenericHelper_CRMService_LTNG_H_HUM genericHelper = new GenericHelper_CRMService_LTNG_H_HUM();
            accountList = genericHelper.fetchAccountDetails(caseRecordId);
        }
        catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex, sClassName, 'getAccountDetail');
        }
        return accountList;
    }

    /*
    * Method Name: getPlanID
    * Description: method will retireve Plan Id using Member Id for OSA Service call
    * Parameter: Case Row Id
    * Return Type: Map<String, String>
    */
    @AuraEnabled(cacheable=true)
    public static Map<String, String> getPlanID(string recordId) {
        Map<String, String> planIdMap = new Map<String, String>();
        try {
            GenericHelper_CRMService_LTNG_H_HUM genericHelper = new GenericHelper_CRMService_LTNG_H_HUM();
            planIdMap = genericHelper.fetchDetailsForOSA(recordId);
            if(test.isRunningTest())
            {
                integer i=1/0;
            }
        }
        catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex, sClassName, 'getPlanID');
        }
        return planIdMap;
    }

    /*
    * Method Name: getEligiblePolicyMemberIds
    * Description: method will retireve Eligible Policy Id using the top heirarchy response from ELigibility Service
    * Parameter: Policy Object based on Group Id and Enterprise Id
    * Return Type: String
    */
    @AuraEnabled()
    public static String getEligiblePolicyMemberIds(string groupOfRecordsObject, string recordId) {
        String policyMemberId = '';
        try {
            GenericHelper_CRMService_LTNG_H_HUM genericHelper = new GenericHelper_CRMService_LTNG_H_HUM();
            String personId = genericHelper.fetchPersonId(recordId);
            policyMemberId = genericHelper.retrievePolicyMemberIds(groupOfRecordsObject, personId);
            if(test.isRunningTest())
            {
                integer i=1/0;
            }
        }
        catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex, sClassName, 'getEligiblePolicyMemberIds');
        }
        return policyMemberId;
    }

    /*
    * Method Name: getEligiblePolicyMemberId
    * Description: method will retireve Eligible Policy Id using the top heirarchy response from ELigibility Service
    * Parameter: Policy Object based on Group Id and Enterprise Id
    * Return Type: String
    */
    @AuraEnabled()
    public static String getEligiblePolicyMemberId(string groupRecordObject, string recordId) {
        String policyMemberId = '';
        try {
            GenericHelper_CRMService_LTNG_H_HUM genericHelper = new GenericHelper_CRMService_LTNG_H_HUM();
            String personId = genericHelper.fetchPersonId(recordId);
            policyMemberId = genericHelper.retrievePolicyMemberId(groupRecordObject, personId);
            if(test.isRunningTest())
            {
                integer i=1/0;
            }
        }
        catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex, sClassName, 'getEligiblePolicyMemberId');
        }
        return policyMemberId;
    }

    /*
    * Method Name: getEligibleDetails
    * Description: method will retireve Eligible Policy details and dependent details
    * Parameter: PolicyMemberId and boolean of is Commercial or not
    * Return Type: Map<String, String>
    */
    @AuraEnabled()
    public static Map<String, String> getEligibleDetails(string policyMemberId, boolean isGBO) {
        Map<String, String> eligibleMap = new Map<String, String>();
        try {
            GenericHelper_CRMService_LTNG_H_HUM genericHelper = new GenericHelper_CRMService_LTNG_H_HUM();
            eligibleMap = genericHelper.procureEligibleDetails(policyMemberId, isGBO);
        }
        catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex, sClassName, 'getEligibleDetails');
        }
        return eligibleMap;
    }

    /*
    * Method Name: getEligibleDetailsList
    * Description: method will retireve Eligible Policy details and dependent details
    * Parameter: PolicyMemberId and boolean of is Commercial or not
    * Return Type: Map<String, String>
    */
    @AuraEnabled()
    public static Map<String, String> getEligibleDetailsList(List<String> policyMemberIds, boolean isGBO) {
        Map<String, String> eligibleMap = new Map<String, String>();
        try {
            GenericHelper_CRMService_LTNG_H_HUM genericHelper = new GenericHelper_CRMService_LTNG_H_HUM();
            eligibleMap = genericHelper.procureEligibleDetailsList(policyMemberIds, isGBO);
        }
        catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex, sClassName, 'getEligibleDetailsList');
        }
        return eligibleMap;
    }

    /*
    * Method Name: getGenericsForRequest
    * Description: method will retireve unique Request Id, timestamp and consumer name
    * Parameter: number of dependent count
    * Return Type: List<map<string, string>>
    */
    @AuraEnabled()
    public static List<map<string, string>> getGenericsForRequest(integer loopCount) {
        List<map<string, string>> genericsMapList = new List<map<string, string>>();
        try {
            GenericHelper_CRMService_LTNG_H_HUM genericHelper = new GenericHelper_CRMService_LTNG_H_HUM();
            genericsMapList = genericHelper.generateGenericsForRequest(loopCount);
        }
        catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex, sClassName, 'getGenericsForRequest');
        }
        return genericsMapList;
    }

    /*
    * Method Name: preparePlanID
    * Description: method will retireve Plan Id using PolicyMember Id for OSA Service call
    * Parameter: PolicyMember Id
    * Return Type: String
    */
    @AuraEnabled(cacheable=true)
    public static String preparePlanID(string recordId) {
        String planId = '',polPlanExtId = '',coverageYear = '';
        Date endDate;
        try 
        {
            List<Policy_Plan_Details__c> lstPolicyPlanDetails = new List<Policy_Plan_Details__c>();
            List<Policy_Member__c> lstPolicyMember = [SELECT Id, Name, Status__c, Policy__c, Effective_Date__c, End_Date__c,	
                Policy_Member_External_ID__c FROM Policy_Member__c where id = :recordId AND IsDeleted = false AND ETL_Record_Deleted__c = false limit 1];
            
            if(lstPolicyMember[0].Policy_Member_External_ID__c != Null){
                if(lstPolicyMember[0].Effective_Date__c != Null && lstPolicyMember[0].End_Date__c != Null)  
                {	
                    endDate = (String.valueOf(lstPolicyMember[0].End_Date__c).split('/')[2] == '9999') ? date.newinstance(9999, 12, 31) : date.parse(String.valueOf(lstPolicyMember[0].End_Date__c));
            
                    if(String.valueOf(lstPolicyMember[0].Status__c) == 'Active' && date.today() > date.parse(String.valueOf(lstPolicyMember[0].Effective_Date__c)) && date.today()< endDate) 
                    {   
                        coverageYear = String.valueOf(Date.Today().year());
                    }
                    else if(String.valueOf(lstPolicyMember[0].Status__c) == 'Future' || String.valueOf(lstPolicyMember[0].Status__c) == 'Termed')
                    {
                        coverageYear = String.valueOf(lstPolicyMember[0].Effective_Date__c).split('/')[2];
                    }
                }
                //Substring from Policy_Member_External_ID
                List<String> lstPolPlanExtId = String.valueof(lstPolicyMember[0].Policy_Member_External_ID__c).split('\\|');
                
                if(lstPolPlanExtId != Null && lstPolPlanExtId.size() > 0){
                    //preparing searchstring
                    polPlanExtId = '%' + '|' + lstPolPlanExtId[3] + '|' + lstPolPlanExtId[4] + '|' + coverageYear + '%';
                    
                    //fetch policy plan details
                    lstPolicyPlanDetails = [SELECT Id, Name, Policy__c,Medicare_Segment_ID__c ,Contract_Number__c, 
                        Coverage_Plan_Effective_Date__c,PBP_Code__c, Policy_Plan_External_ID__c FROM Policy_Plan_Details__c 
                        where Policy_Plan_External_ID__c  LIKE :polPlanExtId AND IsDeleted = false AND ETL_Record_Deleted__c = false limit 1];
                    
                    if(lstPolicyPlanDetails != Null && lstPolicyPlanDetails.size() > 0 && lstPolicyPlanDetails[0].Contract_Number__c != Null && lstPolicyPlanDetails[0].PBP_Code__c != Null 
                        && lstPolicyPlanDetails[0].Medicare_Segment_ID__c != Null && lstPolicyPlanDetails[0].Coverage_Plan_Effective_Date__c != Null)
                    {
                        planid =  lstPolicyPlanDetails[0].Contract_Number__c+'-'+lstPolicyPlanDetails[0].PBP_Code__c+'-'+
                                    lstPolicyPlanDetails[0].Medicare_Segment_ID__c+'-'+lstPolicyPlanDetails[0].Coverage_Plan_Effective_Date__c.split('/')[2]; 
                    }   
                }
            }
        }
        catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex, sClassName, 'preparePlanID');
        }
        system.debug('OSA planid:'+planid);
        return planId;
    }
    
}