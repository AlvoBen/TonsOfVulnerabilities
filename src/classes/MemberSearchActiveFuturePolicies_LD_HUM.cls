/*******************************************************************************************************************************
Apex Class Name : MemberSearchActiveFuturePolicies_LC_HUM
Version         : 1.0
Created On      : 09/18/2020
Function        : This class is used for performing the Lightning Member Search
Test Class      : MemberSearchActiveFuturePolicies_LT_HUM

Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0          Ritik Agarwal                                       02/26/2021                 Initial version
*    1.1          Ashish Kumar                                        03/26/2021                 Replaced Group_Name with Display_Group_Name__C for MemberPlan Object
*    1.2          Kajal Namdev                                        05/05/2021                 Add query limit in SOQL
*    1.3          Kajal Namdev                                        05/13/2021                 Removed reference of policy object
*    1.5          Santhi Mandava                                      09/27/2022                 US3418577 - Home Office/ Contract Protected Data Changes
*******************************************************************************************************************************************************************/
public without sharing class MemberSearchActiveFuturePolicies_LD_HUM {
   
    Public static Integer limitValue = Integer.valueOf(system.label.iPaginator);
/*  
* Method name : determinePolicyAccess
* Description : This  method determines the policy level of access of the logged in user. 
* Return Type : List<> 
* Parameter :   recId
*/
public static List<MemberPlan> determinePolicyAccess(String sAccId) 
{
   
    return [SELECT id, Name,PlanId,Plan.Payer.Security_Groups__c, Plan.Name,Plan.iab_description__c,Plan.Contract_Number__c,Plan.PBP_Code__c,Plan.Medicare_Segment_ID__c,Policy_Platform__c, Product__c, OSB_Indicator__c, GroupNumber,
                                Product_Type__c, EffectiveFrom, EffectiveTo , ASO__c, Display_Group_Name__c, EHB_Term_Date__c, Member_Coverage_Status__c,
                                Member.MedicareId__c,Member.Security_Groups__c,Member_Dependent_Code__c, Member_Id_Base__c, Product_Type_Code__c,ETL_Record_Deleted__c
                                FROM MemberPlan WHERE MemberId = :sAccId order by Member_Coverage_Status__c asc,Product__c asc LIMIT: limitValue];
}
}