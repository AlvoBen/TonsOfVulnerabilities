/*******************************************************************************************************************************
Apex Class Name : MemberSearchActiveFuturePolicies_LC_HUM
Version         : 1.0
Created On      : 09/18/2020
Function        : This class is used for performing the Lightning Member Search
Test Class      : MemberSearchActiveFuturePolicies_LT_HUM

Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Ashish Kumar                                     09/18/2020                Original Version
*    1.1           Ritik Agarwal                                    02/10/2021                Implement sorting logic
*    1.2           Joel George                                      02/24/2021                DF-2445 
*    1.3           Ritik Agarwal                                    02/26/2021                shifted the SOQL in data layer classes
*    1.4           Ankima Srivastava                                06/11/2021                checkmarx fix related to with sharing
*    1.5           Santhi Mandava                                   09/27/2022                US3418577 - Home Office/ Contract Protected Data Changes
*******************************************************************************************************************************************************************/

public without sharing class MemberSearchActiveFuturePolicies_LC_HUM 
{
    /*  
    * Method name : determinePolicyAccess
    * Description : This  method determines the policy level of access of the logged in user. 
    * Return Type : List<> 
    * Parameter :   String
    */
    @AuraEnabled(cacheable=true)
    public static List<MemberPlan> determinePolicyAccess(String sAccId) 
    {
        List<MemberPlan> listMemberPolicies;
        if(String.isNotBlank(sAccId)) 
        {
            listMemberPolicies = PoliciesSearchResults_LC_HUM.recordsSorting(MemberSearchActiveFuturePolicies_LD_HUM.determinePolicyAccess(sAccId)) ; 
            return listMemberPolicies;
        } 
        return null ;
        
    }
	@AuraEnabled(cacheable=true)
    public static HUMPolicyWrapperHelper getMemberPlanDetails(String sAccId) {
        HUMPolicyWrapperHelper objPolicyWrapper = new HUMPolicyWrapperHelper();
        try{
            objPolicyWrapper.mapRecordAccess = new Map<Id, Boolean>();
            objPolicyWrapper.mapPolicyPlans = new Map<Id, Id>();
            Map<Id, MemberPlan> mapMemberPlans = new Map<Id, MemberPlan>();
    
            Set<Id> setIds = new Set<Id>();
            objPolicyWrapper.lstMemberPlans = determinePolicyAccess(sAccId);
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
            HUMExceptionHelper.logErrors(ex,'MemberSearchActiveFuturePolicies_LC_HUM','getMemberPlanDetails');
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
    }
}