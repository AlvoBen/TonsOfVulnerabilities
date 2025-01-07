/*****************************************************************************************************************************************************************************
    @Apex Class Name  : HealthResource_LC_HUM
    @Version          : 1.0
    @Created Date     : 
    @Test class       : HealthResource_LT_HUM
******************************************************************************************************************************************************************************
Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Kajal Namdev                                                         Original Version, Added logic for Available Resources 
* Ashish Kumar														   Added logic for Enrolled Resources
******************************************************************************************************************************************************************************/
public with sharing class HealthResource_LC_HUM {
    
    public static string sMemberID;
    public static string sMemberGenKey;
    
    @AuraEnabled(continuation=true)  
    public static Object getHealthResource(String MemberPlanId){
        try{
            ClinicalProgramResponse_LH_HUM ClinicalPrgms = new ClinicalProgramResponse_LH_HUM(); 
            Boolean bClinicalSwitch= CRMFunctionalityenabler_H_HUM.Checkflagvalue('Activate_GetClinicalV2_Service');
            if(bClinicalSwitch){
                MemberPlan memberPlanObj = HealthResource_LD_HUM.getMemberPlanRecord(MemberPlanId);
                //Method to populate memberId and MemberGenKey
                assignKeys(memberPlanObj);
                return HealthResource_LS_HUM.sendHealthResourceRequest(sMemberID,sMemberGenKey);    
            }else{
                return ClinicalPrgms;
            }
        }catch(Exception e){
            HUMExceptionHelper.logErrors(e, 'HealthResource_LC_HUM', 'getHealthResource');
            return null;
        }
    }
    @AuraEnabled
    public static Object parseResponseClinicalProgram(List<string> labels, object State){
        return HealthResource_LS_HUM.parseResponseClinicalProgram(labels,State);  
    }
    
    public static void assignKeys(MemberPlan memberPlanObj){
        if(memberPlanObj!=null){
            if(String.isNotBlank(memberPlanObj.Member.Mbr_Gen_Key__c)){                    
                sMemberID = memberPlanObj.Member.Mbr_Gen_Key__c;
                sMemberGenKey = 'membergenkey';
            }else{
                sMemberID = memberPlanObj.Member.Enterprise_ID__c;
                sMemberGenKey = 'EnterprisePersonID';
            }
        }
    }
}