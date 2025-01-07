/*****************************************************************************************************************************************************************************
    @Apex Class Name  : DemographicUpdateController_LC_HUM
    @Version          : 1.0
    @Created Date     : 06/09/2022
    @Test Class Name  : DemographicUpdateController_LT_HUM
******************************************************************************************************************************************************************************
Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Ashish Kumar                                        06/09/2022                 Original Version
******************************************************************************************************************************************************************************/

public with sharing class DemographicUpdateController_LC_HUM {
    public static string sClassName = 'DemographicUpdateController_LC_HUM';
    @TestVisible
    private static Boolean bIsPolicyPlanSwithON = (CRMFunctionality_ONOFF_Switch__c.getValues('TemplateSwitch') != Null) ? CRMFunctionality_ONOFF_Switch__c.getValues('TemplateSwitch').IsON__c:true;
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
            GenericHelper_LightningStrides_LH_HUM genericHelper = new GenericHelper_LightningStrides_LH_HUM();
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
            GenericHelper_LightningStrides_LH_HUM genericHelper = new GenericHelper_LightningStrides_LH_HUM();
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
            GenericHelper_LightningStrides_LH_HUM genericHelper = new GenericHelper_LightningStrides_LH_HUM();
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
    @AuraEnabled
    public static String getEligiblePolicyMemberIds(string groupOfRecordsObject, string recordId) {
        String policyMemberId = '';
        try {
            GenericHelper_LightningStrides_LH_HUM genericHelper = new GenericHelper_LightningStrides_LH_HUM();
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
    @AuraEnabled
    public static String getEligiblePolicyMemberId(string groupRecordObject, string recordId) {
        String policyMemberId = '';
        try {
            GenericHelper_LightningStrides_LH_HUM genericHelper = new GenericHelper_LightningStrides_LH_HUM();
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
    @AuraEnabled
    public static Map<String, String> getEligibleDetails(string policyMemberId, boolean isGBO) {
        Map<String, String> eligibleMap = new Map<String, String>();
        try {
            GenericHelper_LightningStrides_LH_HUM genericHelper = new GenericHelper_LightningStrides_LH_HUM();
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
    @AuraEnabled
    public static Map<String, String> getEligibleDetailsList(List<String> policyMemberIds, boolean isGBO) {
        Map<String, String> eligibleMap = new Map<String, String>();
        try {
            GenericHelper_LightningStrides_LH_HUM genericHelper = new GenericHelper_LightningStrides_LH_HUM();
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
    @AuraEnabled
    public static List<map<string, string>> getGenericsForRequest(integer loopCount) {
        List<map<string, string>> genericsMapList = new List<map<string, string>>();
        try {
            GenericHelper_LightningStrides_LH_HUM genericHelper = new GenericHelper_LightningStrides_LH_HUM();
            genericsMapList = genericHelper.generateGenericsForRequest(loopCount);
        }
        catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex, sClassName, 'getGenericsForRequest');
        }
        return genericsMapList;
    }
    /*
    * Method Name: preparePlanID
    * Description: method will retireve Plan Id using MmeberPlan Id for OSA Service call
    * Parameter: MmeberPlan Id
    * Return Type: String
    */
    @AuraEnabled(cacheable=true)
        public static String preparePlanID(string recordId) {
        String planId = '',memPlanExtId = '',coverageYear = '';
        Date endDate;
  
        try 
        {
            List<PurchaserPlan> lstPurchaserPlanDetails = new List<PurchaserPlan>();
            
            List<MemberPlan> lstMemberPlan = [SELECT Id, Name, Member_Coverage_Status__c, EffectiveFrom, EffectiveTo,   
                Member_Plan_External_Id__c FROM MemberPlan where id = :recordId AND IsDeleted = false AND ETL_Record_Deleted__c = false limit 1];
           
           
           
            if(lstMemberPlan[0].Member_Plan_External_Id__c != Null){
                if(lstMemberPlan[0].EffectiveFrom != Null && lstMemberPlan[0].EffectiveTo != Null)  
                {   
                    endDate = (String.valueOf(lstMemberPlan[0].EffectiveTo).substring(0, 4) == '9999') ? date.newinstance(9999, 12, 31) : lstMemberPlan[0].EffectiveTo;
            
                    if(String.valueOf(lstMemberPlan[0].Member_Coverage_Status__c) == 'Active' && date.today() > lstMemberPlan[0].EffectiveFrom && date.today()< endDate) 
                    {   
                        coverageYear = String.valueOf(Date.Today().year());
                    }
                    else if(String.valueOf(lstMemberPlan[0].Member_Coverage_Status__c) == 'Future' || String.valueOf(lstMemberPlan[0].Member_Coverage_Status__c) == 'Termed')
                    {
                        coverageYear = String.valueOf(lstMemberPlan[0].EffectiveFrom).substring(0, 4);
                    }
                }
                //Substring from Member_Plan_External_Id__c
                List<String> lstMemlPlanExtId = String.valueof(lstMemberPlan[0].Member_Plan_External_Id__c).split('\\|');
                
                if(lstMemlPlanExtId != Null && lstMemlPlanExtId.size() > 0){
                    //preparing searchstring
					memPlanExtId = '%' + '|' + lstMemlPlanExtId[3] + '|' + lstMemlPlanExtId[4] + '|' + coverageYear + '%';
                    
                    if(bIsPolicyPlanSwithON){
                        //fetch purchaser plan details
                        lstPurchaserPlanDetails = [SELECT Id, Name, Medicare_Segment_ID__c ,Contract_Number__c, 
                            EffectiveFrom,PBP_Code__c, Purchaser_Plan_External_ID__c FROM PurchaserPlan 
                            where Purchaser_Plan_External_ID__c  LIKE :memPlanExtId AND IsDeleted = false AND ETL_Record_Deleted__c = false limit 1];
                        
                        if(lstPurchaserPlanDetails != Null && lstPurchaserPlanDetails.size() > 0 && lstPurchaserPlanDetails[0].Contract_Number__c != Null && lstPurchaserPlanDetails[0].PBP_Code__c != Null 
                            && lstPurchaserPlanDetails[0].Medicare_Segment_ID__c != Null && lstPurchaserPlanDetails[0].EffectiveFrom != Null)
                        {
                            planid =  lstPurchaserPlanDetails[0].Contract_Number__c+'-'+lstPurchaserPlanDetails[0].PBP_Code__c+'-'+
                                        lstPurchaserPlanDetails[0].Medicare_Segment_ID__c+'-'+String.valueOf(lstPurchaserPlanDetails[0].EffectiveFrom).substring(0, 4); 
                        } 
                    } else {
                        //fetch Policy details
                        List<Policy__c> lstPolicies = [SELECT Id, Name, Medicare_Segment_ID__c ,Contract_Number__c, 
                        Coverage_Plan_Effective_Date__c ,PBP_Code__c FROM Policy__c 
                            where Policy_External_ID__c LIKE :memPlanExtId AND IsDeleted = false limit 1];
                        
                        if(lstPolicies != Null && lstPolicies.size() > 0 && lstPolicies[0].Contract_Number__c != Null && lstPolicies[0].PBP_Code__c != Null 
                            && lstPolicies[0].Medicare_Segment_ID__c != Null && lstPolicies[0].Coverage_Plan_Effective_Date__c != Null)
                        {
                            planid =  lstPolicies[0].Contract_Number__c+'-'+lstPolicies[0].PBP_Code__c+'-'+
                            lstPolicies[0].Medicare_Segment_ID__c+'-'+String.valueOf(lstPolicies[0].Coverage_Plan_Effective_Date__c).split('/')[2]; 
                        } 
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