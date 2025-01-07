/*******************************************************************************************************************************
Apex Class Name : PoliciesSearchResults_LC_HUM
Version         : 1.3
Created On      : 11/02/2020
Function        : This class is used for showing policies table 

Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Ritik Agarwal                                        11/02/2020               Original Version
*    1.1           Ritik Agarwal                                        11/07/2020               Added method for fetch group account policies
*    1.2           Akshay K                                             11/23/2020               Added method for child records for Policies
*    1.3           Ritik Agarwal                                        01/28/2021               Added sorting logic by using generic MemberSearch_LC_HUM.MemberRecordSorting class
*    1.4           Ankima                                        		04/22/2021               
*    1.5           Ashish Kumar                                         04/22/2021               Added method to get the base Salesforce URL
*    1.6           Kajal Namdev                                         05/17/2021               Replaced references of policy with purchaserpla
*    1.7           Vardhman Jain                                        12/22/2022               US3879280: Lightning - Consumer/Implement for CPD/General Search & Person Account Page
*    1.8	       Gowthami Thota					                    06/29/2023		         US4625841: Case Management (Provider) - Case Edit Page- Implement Memberplan Lookup on RelatedAccount Section
*    1.9           Prasuna Pattabhi                                     10/19/2023               User Story 4828065: T1PRJ0865978 - C06; Case Management; Pharmacy - Case Edit- Block Access to Non-Medical Plans
*******************************************************************************************************************************************************************/
    public with sharing class PoliciesSearchResults_LC_HUM 
    {
   /*
    * Method name : getResults
    * Description : This method will return the sorted list of MemberPlan object
    * Parameter   : Member Account Id
    */
        @AuraEnabled
        public static List<MemberPlan> getResults(String recId)
        {
            ID objID = recId;
            If(recId.startsWith('500')){
                Case oCase=[select id, AccountId, Account.RecordType.name from Case where id=: objID limit 1];
                objID = oCase.AccountId;
                
            }else if(recId.startsWith('001')){
                objID = recId;
            }
            return recordsSorting(PoliciesSearchResults_LD_HUM.fetchPolices(objID));
        } 
        
        /*
    * Method name : getpurchaserplandetails
    * Description : This method will return the list of PurchaserPlan object
    * Parameter   : Member Account Id
    */
        @AuraEnabled
        public static List<PurchaserPlan> getpurchaserplandetails(String mpId)
        {
            Id memplanId = Id.valueOf(mpId);    
            return PoliciesSearchResults_LD_HUM.getpurchaserplandetails(memplanId);
        } 
        
        /*
    * Method name : getMemberPlanDetails
    * Description : This method will return the MemberPlan details
    * Parameter   : Member Plan Id
    */
        @AuraEnabled
        public static MemberPlan getMemberPlanDetails(String mpId)
        {
            Id memplanId = Id.valueOf(mpId);
            MemberPlan oMemPlan;
            List<MemberPlan> lstMemberPlans = PoliciesSearchResults_LD_HUM.getMemberPlanDetails(memplanId);   
            if(!lstMemberPlans.isEmpty())
            {
                oMemPlan = lstMemberPlans.get(0);
            }
            return oMemPlan;
        } 
        
        /*
    * Method name : getGroupAccPolicies
    * Description : This method will return the list of PurchaserPlan object
    * Parameter   : Group Account Id
    */
        @AuraEnabled
        public static List<PurchaserPlan> getGroupAccPolicies(String recId)
        {
            return PoliciesSearchResults_LD_HUM.getGroupAccPolicies(recId);
        }
        
   /*
    * Method name : recordsSorting
    * Description : This method will return the list of custom sorted(i.e., neither ascending nor descending) salesforce object records
    * Parameter   : list of any salesforce object records
    */
        public static List<sObject> recordsSorting(List<sObject> lstOfPolicies)
        {
            List<MemberSearch_LC_HUM.MemberRecordSorting> lstSorted = new List<MemberSearch_LC_HUM.MemberRecordSorting>();
            MemberSearch_LC_HUM.MemberRecordSorting sortClass;
            for(sObject obj : lstOfPolicies){
                if(obj.get('ETL_Record_Deleted__c')==true)
                {
                    sortClass = new MemberSearch_LC_HUM.MemberRecordSorting('Z',obj);  
                } 
                else
                {
                    String state = (String)obj.get('Member_Coverage_Status__c');
                    sortClass = new MemberSearch_LC_HUM.MemberRecordSorting(state,obj);
                }
                lstSorted.add(sortClass);
            }
            
            lstSorted.sort();
            
            List<sObject> lstSobj = new List<sObject>();
            for(MemberSearch_LC_HUM.MemberRecordSorting objSort : lstSorted)
            {
                lstSobj.add(objSort.oAcc);
            }
            return lstSobj;
        }
       
     /*
    * Method name : fetchPolicesDetails
    * Description : This method will return the Member Plan object
    * Parameter   : Policy Id
    */
        @AuraEnabled
        public static MemberPlan getPoliciesHighlightPanel(String recId)
        {
            return PoliciesSearchResults_LD_HUM.fetchPolicesDetails(recId);
        }
         
                     /*
    * Method name : getSalesforceBaseUrl
    * Description : This method will return the base Salesfroce URL
    * Parameter   : NA
    */
        @AuraEnabled
        public static String getSalesforceBaseUrl()
        {
            return URL.getSalesforceBaseUrl().toExternalForm() ;
        }
         

        @AuraEnabled(cacheable=true)
        public static HUMPolicyWrapperHelper getMemberPlanDtls(String recId) {
        HUMPolicyWrapperHelper objPolicyWrapper = new HUMPolicyWrapperHelper();
        try{
            objPolicyWrapper.mapRecordAccess = new Map<Id, Boolean>();
            objPolicyWrapper.mapPolicyPlans = new Map<Id, Id>();
            Map<Id, MemberPlan> mapMemberPlans = new Map<Id, MemberPlan>();
            Set<Id> setIds = new Set<Id>();
            objPolicyWrapper.bIsPharmacyUser = IsPharmacyUser();
            objPolicyWrapper.lstMemberPlans = getResults(recId);
            if(objPolicyWrapper.lstMemberPlans != Null && !objPolicyWrapper.lstMemberPlans.isEmpty()){
                for(MemberPlan obj : objPolicyWrapper.lstMemberPlans){
                   setIds.Add(obj.PlanId);
                    objPolicyWrapper.mapPolicyPlans.put(obj.Id,obj.PlanId);
                }
                if(!setIds.isEmpty()){
                    for(UserRecordAccess userAccess : [SELECT RECORDID, HASREADACCESS FROM UserRecordAccess WHERE USERID =: UserInfo.getUserID() AND RECORDID IN : setIds])
                    {
                        objPolicyWrapper.mapRecordAccess.put(userAccess.RECORDID,userAccess.HASREADACCESS);
                    }
                }
            }
        }catch(exception ex){
            HUMExceptionHelper.logErrors(ex,'PoliciesSearchResults_LC_HUM','getMemberPlanDtls');
            return NULL;
        }
        return objPolicyWrapper;
    }
        public class HUMPolicyWrapperHelper{
            @AuraEnabled
            public List<MemberPlan> lstMemberPlans;
            @AuraEnabled
            public Map<Id, Boolean> mapRecordAccess;
            @AuraEnabled
            public Map<Id, Id> mapPolicyPlans;
            @AuraEnabled
            public Boolean bIsPharmacyUser;
        } 
        /*
            * Method name : IsPharmacyUser
            * Description : This method will return user profile is pharmacy or not
            * Parameter   : NA
            * Return : Boolean
        */
        private static boolean IsPharmacyUser(){
            boolean isPharmacy = false;
            string sUserProfile = [SELECT Id, Name FROM Profile WHERE Id =:UserInfo.getProfileId()].Name;
            if(sUserProfile == HumConstants_LWC.PHARMACYSPECIALIST_PROFILE_HUM ||  HUMUtilityHelper_LWC.isPDPPilotUser()){
                isPharmacy = true;   
            }else{
                isPharmacy = false;
            }
            return isPharmacy;        
        }  
    }