/**************************************************************************** 
Apex Class Name  : GenericMemberPlanDetails_LC_HUM
Version          : 1.0  
Created Date     : 03/14/2023
Function         : Controller class for getting member plan details. 
Test class       : GenericMemberPlanDetails_LT_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Nirmal Garg                               3/14/2023                  Original Version
* Anusha C                                   07/17/2023                 US-4812126 Critical- Lightning - Generic             
***************************************************************************************************/
public with sharing class GenericMemberPlanDetails_LC_HUM {
    public static final string TESTING = 'testing';
    
    @AuraEnabled(cacheable=true)
    public static List<MemberPlan> getMemberPlanDetails(string memberplanid){
        try {
            if(String.isNotBlank(memberplanid) && memberplanid.length() == 18
            && memberplanid.startsWith(Schema.SObjectType.MemberPlan.KeyPrefix) 
            && !memberplanid.containsIgnoreCase(TESTING)){
                return [Select Id,Name, EffectiveFrom,EffectiveTo, Source_Coverage_ID__c, Plan.Product__r.Name,
                Policy__c, Status, Plan.Product__r.ProductCode, Plan.Source_Major_LOB__c, Plan.Major_LOB__c, Policy__r.Major_LOB_Frm__c, Policy__r.Major_LOB__c,
                               Plan.Payer.Source_Platform_Code__c, Plan.Source_Cust_Cov_Key__c,
                               Plan.EffectiveFrom, Plan.EffectiveTo,
                               Plan.Benefit_Coverage__c, Plan.Purchaser_Plan_External_ID__c,
                               Plan.Selling_Market_Number__c,Display_Group_Name__c,
                               Plan.Product_Description__c,
                               Member.Enterprise_Id__c, Plan.Product__r.Sold_Product_Key_Value__c,
                               Plan.Platform__c, Plan.Contract_Number__c, Plan.PBP_Code__c,
                               Product__c, Product_Type__c, ASO__c, GroupNumber,
                               Product_Type_Code__c, Exchange_type__c, Policy_Platform__c,
                               Plan.Medicare_Segment_ID__c, Plan.Policy_Group_Number__c,
                               Product_Description__c, Issue_State__c, Member.ShippingStreet,
                               Member.ShippingCity, Member.ShippingState, Member.ShippingPostalCode,
                               Member.Name, Member.billingaddress,
                               Member.PersonMailingPostalCode,
							   Member.Mbr_Gen_Key__c,Plan.Product__r.Segment_Type__c,
                               Plan.Payer.Enterprise_Id__c,
                               SubscriberPlanId__c, SubscriberPlanId__r.Member.Mbr_Gen_Key__c,
                               Member.ShippingCountry, Member.Birthdate__c, Member.PersonMailingAddress From MemberPlan where Id =: memberplanid WITH USER_MODE limit 1];
            }else{
                if(String.isNotBlank(memberplanid) && Test.isRunningTest() && memberplanid.containsIgnoreCase(TESTING))  
                    throw new HUMCustomException('Error');
                 return null;
            }
            
        } catch (Exception e) {
            HUMExceptionHelper.logErrors(e, 'GenericMemberPlanDetails_LC_HUM', 'getMemberPlanDetails');
            throw new AuraHandledException(e.getMessage());
        }
    }
}