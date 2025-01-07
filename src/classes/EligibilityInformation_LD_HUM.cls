/*****************************************************************************************
Apex class Name    : EligibilityInformation_LD_HUM
Version            : 1.0
Function           : data layer for retrieving eligibility info
Created Date       : 03/12/2021
Test Class         : EligibilityInformation_LT_HUM
Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
*Joel George                                            05/03/2021                  initial version
*Joel George                                            05/17/2021                  Added Policy Object change
****************************************************************************************************************/

public with sharing class EligibilityInformation_LD_HUM 
{
    public static  List<MemberPlan> getMemberPlan(String recId)
    {
        List<MemberPlan> lstMemberPlan = [Select id, Plan.Source_Cust_Cov_Key__c ,Member_Id_Base__c from MemberPlan where id=:recId Limit 1];
        return lstMemberPlan;
    }
    
}