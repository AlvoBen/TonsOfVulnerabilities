/*******************************************************************************************************************************
Apex Class Name : CoachMemberActiveFuturePolicies_C_HUM
Version         : 1.0
Created On      : 03/08/2021
Function        : This class is used for performing the Lightning Member Search for Wellness Coaching App
Test Class      : CoachMemberActiveFuturePolicies_T_HUM

Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Jasmeen SHangari                                   03/08/2021                Original Version
*    1.1           Jasmeen Shangari                                   03/31/2021                Replace Group_name__c with Display_Group_Name__C
*    1.2           Jasmeen Shangari                                   04/16/2021                Fix for Defect-2851
*******************************************************************************************************************************************************************/

public with sharing class CoachMemberActiveFuturePolicies_C_HUM 
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
            listMemberPolicies = [SELECT id, Name, Plan.Name, Policy_Platform__c, Product__c, OSB_Indicator__c, GroupNumber,
                                    Product_Type__c, EffectiveFrom, EffectiveTo , ASO__c, Display_Group_Name__c, EHB_Term_Date__c, Member_Coverage_Status__c,
                                    policy__r.iab_description__c, policy__r.Product_Type__c,  Member.MedicareId__c,
                                    Member_Dependent_Code__c, Member_Id_Base__c, Product_Type_Code__c
                                    FROM MemberPlan 
                                    WHERE MemberId = :sAccId order by Member_Coverage_Status__c asc,Product__c asc];            
        } 
        return listMemberPolicies ;
    }
}