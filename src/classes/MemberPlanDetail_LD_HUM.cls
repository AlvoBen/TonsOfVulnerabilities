/*******************************************************************************************************************************
Apex Class Name : MemberPlanDetail_LD_HUM
Version         : 1.0
Created On      : 05/18/2021
Function        : This is the data layer class for Member Plan Details section on the Policy Detail page
Test Class      : MemberPlanDetail_LT_HUM

Modification Log: 
* Version Number             Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
*     1.0                    Joel George                                            05/18/2021                  PBE Call
*     2.0                    Joel George                                            06/04/2021                  Vendor, EDI service 
*     3.0                    Joel George                                            06/10/2021                  Rollback PBE Service 
*	  4.0					 Kajal Namdev											06/21/2021					Added getPurchaserPlanOtherInsurance method
*     5.0                    Kajal Namdev                                           09/29/2021          	   Added limitOne in query
*     6.0                    Ankima Srivastava                                      10/25/2021                  US: 2581786 LastModifiedDate added in the query
*     7.0                    Muthukumar                                             03/15/2022                  Added Member plan object fields in the query under "getMemberName" method
******************************************************************************************************************************************************************************/

public with sharing class MemberPlanDetail_LD_HUM
{ 
    
    /*
*  This method  is used to return details of a Member Plan
*  @name   getMemberName
*  @param  ID - Record ID of Member Plan for which claims are to be displayed
*  @return List<MemberPlan> -  List of MemberPlan records
*  @throws getMemberName
*/
    public static List<MemberPlan> getMemberName(String sRecId)
    {   
        List<MemberPlan> lstpolmem = new List<MemberPlan>();
        if(sRecId != null && String.isNotBlank(sRecId))
        {
            lstpolmem = [SELECT Id, Name, Member.Enterprise_ID__c,Plan.Client_Number__c,Medicaid_Id__c, GroupNumber, Policy_Platform__c, Member.Name, Plan.Product__r.Sold_Product_Key_Value__c, Plan.Payer.Source_Customer_Key__c, Product_Type__c, Issue_State__c, Member_Dependent_Code__c,Member_Id_base__c ,effectiveFrom, 
            Product__c,Product_Description__c,ETL_Record_Deleted__c,Plan.Selling_Market_Number__c, Plan.iab_description__c ,Plan.Name ,Display_Group_Name__c , Plan.Benefit_Coverage__c , Product_Type_Code__c ,Exchange__c , EHB_Term_Date__c ,SubscriberPlanId__c , Plan.SourceSystem , RelationshipToSubscriber ,EffectiveTo,PlanId, Member_Coverage_Status__c ,Plan.Payer.Source_Platform_Code__c , ASO__c, Metallic_Tier__c , Exchange_Type__c , Plan.Source_Cust_Cov_Key__c,SubscriberPlanId__r.Name,Policy__c,LastModifiedDate,
			Policy__r.Name,MemberId,Term_Reason_Code__c,SubscriberId,Subscriber.Name,SourceSystemIdentifier,Segment_Indicator__c,Alternate_Description__c
            ,Dual_Status_Indicator__c,Vitality_Entity_ID__c,Term_Reason_Description__c FROM MemberPlan WHERE ID=:sRecId LIMIT 1];
        }
        return lstpolmem;
    } 

       /*
*  This method  is used to return details of a Member Plan
*  @name   getMemberName
*  @param  ID - Record ID of Member Plan for which claims are to be displayed
*  @return List<MemberPlan> -  List of MemberPlan records
*  @throws getMemberName
*/
    public static String getEDIGroupNumber( List<MemberPlan>  lstMemberPlan)
    {   
       
        String returnGroupID;
        
        if(!lstMemberPlan.IsEmpty())
        {
            if(lstMemberPlan[0].Plan.Payer.Source_Platform_Code__c == 'LV') //JOELCHECK if this can fail
            {
                returnGroupID = lstMemberPlan[0].Plan.Source_Cust_Cov_Key__c;
                if(returnGroupID != null && returnGroupID !='' )
                {
                    returnGroupID = returnGroupID.substring(0,6);
                    return returnGroupID;
                }
            }
            else
            {
                return lstMemberPlan[0].GroupNumber;
            }
        }
        
        return null;
    } 
    
    //================================= Vendor Service Start ======================================================  
     /*
    *  This method  is used to return details of a policy PLAN
    *  @name   getPolicyPlanVendor
    *  @param  Policy Member 
    *  @return List<Policy_Plan_Details__c> -  List of Policy pLAN records
    *  @throws getPolicyPlanVendor
    */
    public static List<PurchaserPlan> getPolicyPlanVendor(MemberPlan policyMember)
    {   
        List<PurchaserPlan> lstpolplan = new List<PurchaserPlan>();
        try
        {
            if(policyMember != null)
            {
                lstpolplan = [select id, Exchange__c, Business_Segment__c, Exchange_Indicator__c, Exchange_Type__c, Metallic_Tier__c, EffectiveFrom, EffectiveTo, Product__r.Sold_Product_Key_Value__c, Policy__r.Group_Name__r.Source_Customer_Key__c, Platform__c,Source_Cust_Cov_Key__c,
                               Policy__r.Last_GBE_Call_Date__c, Policy__c,Policy__r.Group_Name__c, Last_GBE_Call_Date__c,Policy_Group_Number__c,payer.Group_Number__c  from PurchaserPlan
                               where Policy__c =: policyMember.policy__c and ETL_Record_Deleted__c=false ];
            }
        }  
        catch (Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'MemberPlanDetail_LD_HUM', 'getPolicyPlanVendor');
        }
        return lstpolplan;
    } 
    //================================= Vendor Service Stop ======================================================  
    /*
    *  This method  is used to return details of a policy PLAN
    *  @name   getPolicyPlanVendor
    *  @param  Policy Member 
    *  @return List<PurchaserPlan> -  List of Policy pLAN records
    *  @throws getPolicyPlanVendor
    */
    public static List<PurchaserPlan> getPurchaserPlanOtherInsurance(set<Id> planIds)
    { 
    	return [SELECT Id, Name, EffectiveFrom, EffectiveTo, Policy__c, 
                                          Product__c,Product__r.Sold_Product_Key_Value__c, Policy_Group_Number__c, Source_Cust_Cov_Key__c 
                                          FROM PurchaserPlan Where Id IN :planIds];
    } 
}