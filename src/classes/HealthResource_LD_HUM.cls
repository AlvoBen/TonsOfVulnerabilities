/*****************************************************************************************************************************************************************************
@Apex Class Name  : HealthResource_LD_HUM
@Version          : 1.0
@Created Date     : 
@Test class       : HealthResource_LT_HUM
******************************************************************************************************************************************************************************
Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Kajal Namdev/Ashish                                                            Original Version  
******************************************************************************************************************************************************************************/
public with sharing class HealthResource_LD_HUM {
    public static MemberPlan getMemberPlanRecord(String MemberPlanId){
        MemberPlan memberPlanObj = new MemberPlan();
        if(String.isNotBlank(MemberPlanId)){
            memberPlanObj = [SELECT Id,Member.Mbr_Gen_Key__c,Member.Enterprise_ID__c FROM MemberPlan WHERE id =: MemberPlanId];
        }
        return memberPlanObj;
    }
    
}