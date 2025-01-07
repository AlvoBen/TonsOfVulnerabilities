/****************************************************************************
Apex Class Name  : MemberMbeService_LD_HUM
Version          : 1.0  
Created Date     :05/24/2021
Function         : Data layer class for SOQLs.
Test Class       : 
****************************************************************************
Modification Log:
*Developer Name          Code Review #         Date                       Description
*------------------------------------------------------------------------------------------------------------
* Ritik Agarwal                               05/25/2021                   Original Version
* Kajal Namdev                                09/29/2021          	       Added limitOne in query
* Tharun Madishetti							  03/01/2024				   US-5318077 : Consumer Mgt-Classic-VIT Remediation Member Plan and Verify Demographics; VIT37935988,VIT37935989
***************************************************************************************************/
public with sharing class MemberMbeService_LD_HUM {
    public static final Integer iLimitOne = GLOBAL_CONSTANT_LH_HUM.iLimitONe;
    
/* Method Name  : fetchPolicy()
* Description  : Method to fetch accountId related to memberplan.
* return Type  : String
* Parameters : memberplanId
*/
    public static String fetchAccountId(String memberplanId){
        List<MemberPlan> lstMemberPlan = [Select ID,MemberId from MemberPlan where ID =: memberplanId LIMIT: iLimitOne]; 
        if(lstMemberPlan != null){
        	return lstMemberPlan[0].MemberId;
        }
        else
            return null;
    }
    
    /*
* Method Name  : fetchPolicy()
* Description  : Method to fetch memberplan records.
* return Type  : List<MemberPlan>
* Parameters : memberplanId
*/
    public static List<MemberPlan> fetchPolicy(String memberplanId){
        return [Select ID,Name,Has_Vitality__c, Policy_Platform__c,Plan.Product__r.Major_LOB__c, EffectiveFrom, EffectiveTo, 
                Plan.Product__r.ProductCode from MemberPlan where ID =: memberplanId LIMIT: iLimitOne];    
    }
    
    /*
* Method Name  : fetchMemberCache()
* Description  : Method to fetch Member_Cache__c records.
* return Type  : List<Member_Cache__c>
* Parameters : sAccountID
*/
    public static List<Member_Cache__c> fetchMemberCache(String sAccountID){ 
        return [select id,Web_Last_Login__c ,Cache_Expiration_Date_Vitality__c,Cache_Expiration_Date_Web_Last_Login__c,Cache_Expiration_Date_Is_Web_Registered__c , 
                Cache_Expiration_Date_Member_Gen_Key__c,Member_Gen_Key__c,Is_Web_Registered__c,Vitality_Entity_ID__c from Member_Cache__c where AccountID__c =: sAccountID];
    } 
    
     /*
* Method Name  : getVitalityMemberPlan()
* Description  : Method to fetch MemberPlan records.
* return Type  : List<MemberPlan>
* Parameters : MemberpLanId
*/
    public static List<MemberPlan> getVitalityMemberPlan(String memPlanId){ 
      return [Select Has_Vitality__c from MemberPlan where id=:memPlanId LIMIT: iLimitOne];
    }

}