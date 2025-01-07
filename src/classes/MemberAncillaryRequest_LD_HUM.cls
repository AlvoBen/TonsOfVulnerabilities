/*******************************************************************************************************************************
Apex Class Name : MemberAncillaryRequest_LD_HUM
Version         : 1.0
Created On      : 05/01/2021
Function        : This is the data layer class for Medicaid Plan Details section on the Policy Detail page
Test Class      : MemberPlanDetail_LT_HUM

Modification Log: 
* Version Number             Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
*     1.0 					 Ashish Kumar 											05/01/2021
*     2.0                    Joel George                                            05/18/2021
*     2.0                    Joel George                                            06/01/2021					DF 3168
*     4.0                    Kajal Namdev                                           09/29/2021          	   Added limitOne in query
******************************************************************************************************************************************************************************/

public with sharing class MemberAncillaryRequest_LD_HUM
{                 
    /*
    *  This method  is used to return details of a Member Plan
    *  @name   getMemberName
    *  @param  ID - Record ID of Member Plan for which claims are to be displayed
    *  @return List<MemberPlan> -  List of MemberPlan records
    *  @throws getMemberName
    */
    public static List<MemberPlan> getMemberName(ID sRecId)
    {   
        List<MemberPlan> lstpolmem = new List<MemberPlan>();
        if(sRecId != null && String.isNotBlank(sRecId))
        {
            lstpolmem = [SELECT Id, Name, Plan.Client_Number__c,Medicaid_Id__c, GroupNumber, Policy_Platform__c, Member.Name, Plan.Product__r.Sold_Product_Key_Value__c, Plan.Payer.Source_Customer_Key__c, Product_Type__c, Issue_State__c, Member_Dependent_Code__c,Member_Id_base__c ,effectiveFrom, 
            Product__c,Product_Description__c,ETL_Record_Deleted__c,Plan.Selling_Market_Number__c, Plan.iab_description__c ,Plan.Name ,Display_Group_Name__c , Plan.Benefit_Coverage__c , Product_Type_Code__c ,Exchange__c , EHB_Term_Date__c ,SubscriberPlanId__c , Plan.SourceSystem , RelationshipToSubscriber ,EffectiveTo,PlanId, Member_Coverage_Status__c ,Plan.Payer.Source_Platform_Code__c , ASO__c, SubscriberPlanId__r.Name 
            FROM MemberPlan WHERE ID=:sRecId LIMIT 1];
        }
        return lstpolmem;
    } 
}