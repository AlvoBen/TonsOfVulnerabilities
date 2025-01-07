/*****************************************************************************************************************************************************************************
    @Apex Class Name  : accountDetailPageProtected_LC_Hum
    @Version          : 1.0
    @Created Date     : 09/19/2022
    @Test Class Name  : accountDetailPageProtected_LT_Hum
******************************************************************************************************************************************************************************
Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Muthukumar                                		  09/19/2022                 Original Version
********************************************************************************************************************************************************/
public without sharing class accountDetailPageProtected_LC_Hum{
    
/*
* Method Name : getAccountData
* Description : Method to get the account Data
* Return type : List<Account>
* Parameter   : Account Record Id
*/ 
	@AuraEnabled
    public Static List<Account> getAccountData(String recordId){
       
        List<Account> accRecord = [SELECT Name, firstName, Salutation, LastName, Birthdate__c, RecordType.Name, Security_Groups__c 
                                   FROM Account WHERE Id =: recordId];
    return accRecord;
    }
    
/*
* Method Name : getMemberPlanData
* Description : Method to get the MemberPan Data
* Return type : List<MemberPlan>
* Parameter   : Account Record Id
*/
    @AuraEnabled
    public Static HUMMemberPlanWrapper getMemberPlanData(String recordId){
        HUMMemberPlanWrapper objWrapper = new HUMMemberPlanWrapper();
        objWrapper.mapPolicyPlans = new Map<Id,Id>();
        objWrapper.mapRecordAccess = new Map<Id,Boolean>();
        Set<Id> setIds = new Set<Id>();
        objWrapper.isGenesysUser = false;
        try{
                objWrapper.lstMemberPlans = [SELECT Id,Name,PlanId,Plan.Payer.Security_Groups__c,Product__c,Product_Type__c,Product_Type_Code__c,EffectiveFrom,EffectiveTo
                                           ,ETL_Record_Deleted__c,Display_Group_Name__c,GroupNumber,Policy_Platform__c,Plan.Name,Member.Name,MemberId,Member.MedicareId__c 
                                            FROM MemberPlan WHERE MemberId =: recordId];
                if(!objWrapper.lstMemberPlans.isEmpty()){
                    for(MemberPlan obj : objWrapper.lstMemberPlans){
                        setIds.add(obj.PlanId);
                        objWrapper.mapPolicyPlans.put(obj.Id,obj.PlanId);
                    }
                }
                if(!setIds.isEmpty()){
                    for(UserRecordAccess userAccess : [SELECT RECORDID, HASREADACCESS FROM UserRecordAccess WHERE USERID =: UserInfo.getUserID() AND RECORDID IN : setIds])
                    {
                        objWrapper.mapRecordAccess.put(userAccess.RECORDID,userAccess.HASREADACCESS);
                    }
                }

                User oCurrentUser = [select Id, CallCenterId  from User where id =: Userinfo.getUserId() Limit 1];
                If(oCurrentUser != null){
                    String sCallcenterId = oCurrentUser.CallCenterId;
                    if(String.IsNotBlank(sCallcenterId))  objWrapper.isGenesysUser = true;
                }

            }catch(exception ex){
                HUMExceptionHelper.logErrors(ex,'accountDetailPageProtected_LC_Hum','getMemberPlanData');
                objWrapper = NULL;
            }
        return objWrapper;
    }

    public class HUMMemberPlanWrapper{
        @AuraEnabled
        public List<MemberPlan> lstMemberPlans;
        @AuraEnabled
        public Map<Id, Boolean> mapRecordAccess;
        @AuraEnabled
        public Map<Id, Id> mapPolicyPlans;
        @AuraEnabled
        public boolean isGenesysUser;
    }
}