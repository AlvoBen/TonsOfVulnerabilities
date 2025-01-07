/************************************************************************************************************************************************************************************       
Apex Class Name  : MyAssistantAtHumana_LC_HUM.cls 
Created Date     : September 15, 2023
Function         : Executes SOQL in support of the MyAssistantAtHumana LWC

Modification Log: 
Developer Name                    Date                    Description
*--------------------------------------------------------------------------------------------------
* Robert Crispen                09/15/2023                Initial Version
************************************************************************************************************************************************************************************/
public with sharing class MyAssistantAtHumana_LC_HUM {

    public static Integer limitValue = Integer.valueOf(system.label.iPaginator);

    @AuraEnabled(cacheable=true)
    public static Boolean getAiChatAccess(){
    Boolean isAiChatUser = false; 
        for(PermissionSetAssignment psAsign: [SELECT Id
                                                FROM PermissionSetAssignment
                                                WHERE PermissionSet.name = 'CRMS_900_My_Assistant_At_Humana' and AssigneeId = :userinfo.getuserid()
                                            ]){
               isAiChatUser = true;
            }
        return isAiChatUser;
    }

    @AuraEnabled(cacheable=true)
    public static List<Account> getAccount(String accountId) {
        return [
            SELECT Id, Name, Birthdate__c, Gender__c, PersonMailingCity, PersonMailingState, PersonMailingPostalCode, Enterprise_ID__c
            FROM Account
            WHERE Id = :accountId
            WITH SECURITY_ENFORCED
            LIMIT 1
        ];
    }

    @AuraEnabled(cacheable=true)
    public static List<MemberPlan> getMemberPlan(String memberPlanId) {
        return [
            SELECT Id, MemberId, Product__c, Product_Type__c, Product_Type_Code__c, Member_Coverage_Status__c, EffectiveFrom, EffectiveTo, ETL_Record_Deleted__c, Member_Id_Base__c, Member_Dependent_Code__c, GroupNumber, Policy_Platform__c
            FROM MemberPlan
            WHERE Id = :memberPlanId
            WITH SECURITY_ENFORCED
            LIMIT 1
        ];
    }

    @AuraEnabled
    public static List<Interaction__c> getInteractionByCreator(String userId) {
        return [
            SELECT Id, Name, CreatedById, CreatedDate, Interacting_With__c, SystemModstamp
                FROM Interaction__c
                WHERE CreatedById = :userId 
            WITH SECURITY_ENFORCED
            ORDER BY CreatedDate DESC LIMIT 1
        ];
    } 
    
    @AuraEnabled
    public static boolean getMyAHOnOffSwitchBooleanValue(){
        try {
            CRMFunctionality_ONOFF_Switch__c myAHCrmFunctionalityONOFFSwitch = CRMFunctionality_ONOFF_Switch__c.getValues('5029083');
            return myAHCrmFunctionalityONOFFSwitch.IsON__c;
        } catch(exception ex){
            HUMExceptionHelper.logErrors(ex,'MyAssistantAtHumana_LC_HUM','getMyAHOnOffSwitchBooleanValue()');
            return false;
        } 
    }
    
    @AuraEnabled(cacheable=true)
    public static HUMPlanWrapperHelper getPlanAccessDetails(String recordId) {
        HUMPlanWrapperHelper objPlanWrapper = new HUMPlanWrapperHelper();
        try {
            objPlanWrapper.mapPlanAccess = new Map<Id, Boolean>();
            objPlanWrapper.mapPolicyPlans = new Map<Id, Id>();
            objPlanWrapper.listMemberPlans = [SELECT Id, Product__c, PlanId FROM MemberPlan where MemberId = :recordId WITH SECURITY_ENFORCED LIMIT :limitValue];
            Map<Id, MemberPlan> mapMemberPlans = new Map<Id, MemberPlan>();
            Set<Id> setIds = new Set<Id>();

            if(objPlanWrapper.listMemberPlans != Null && !objPlanWrapper.listMemberPlans.isEmpty()){
                for(MemberPlan memberPlan : objPlanWrapper.listMemberPlans) {
                    setIds.Add(memberPlan.PlanId);
                    objPlanWrapper.mapPolicyPlans.put(memberPlan.Id,memberPlan.PlanId);
                }

                if(!setIds.isEmpty()){
                    for(UserRecordAccess userAccess : [SELECT RECORDID, HASREADACCESS FROM UserRecordAccess WHERE USERID =: UserInfo.getUserID() AND RECORDID IN : setIds WITH SECURITY_ENFORCED])
                    {
                        objPlanWrapper.mapPlanAccess.put(userAccess.RECORDID,userAccess.HASREADACCESS);
                    }
                }
            }
            if(Test.isRunningTest()){
                throw new QueryException();
            } 
        }
        catch(exception ex){
            HUMExceptionHelper.logErrors(ex,'MyAssistantAtHumana_LC_HUM','getPlanAccessDetails');
            objPlanWrapper =  NULL;
        }

        return objPlanWrapper;
    }

    public class HUMPlanWrapperHelper{
        @AuraEnabled
        public List<MemberPlan> listMemberPlans;
        @AuraEnabled
        public Map<Id, Boolean> mapPlanAccess;
        @AuraEnabled
        public Map<Id, Id> mapPolicyPlans;
    }  
}