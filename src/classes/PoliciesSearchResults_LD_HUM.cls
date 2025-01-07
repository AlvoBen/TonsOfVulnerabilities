/*******************************************************************************************************************************
Apex Class Name : PoliciesSearchResults_LD_HUM
Version         : 1.3
Created On      : 11/02/2020
Function        : This class is used for SOQL purpose

Modification Log:
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Ritik Agarwal                                    11/02/2020              Original Version
*    1.1           Ritik Agarwal                                    03/10/2021              Added AccountId in SOQL of MemberPlan object
*    1.2           Kajal Namdev                                     04/01/2021              Added Limit in all the SOQL
*    1.3          	Kajal Namdev                                    05/17/2021              Replaced references of policy with purchaserplan
*    1.4          	Ankima                                        	04/22/2021              Added fetchPolicesDetails method
*    1.5          	Ritik Agarwal                                   08/18/2021              Added sorting for PDP subtable
*    1.6		  	Kajal Namdev                                    08/18/2021          	Removed limit from single record query
*    1.7           	Kajal Namdev                                    08/20/2021          	Added limitOne in query
*				   	Aishwarya Pawar                                 05/13/2022              REQ - 3285223 Alert Configuration on Plan Member Page
*                 	Nirmal Garg                                     12/06/2022            	US-3975339 - RTI Icon Logic Check
*					Bhakti Vispute									02/01/2023				User Story 4127354: T1PRJ0865978 -21642 RCC C13, Lightning - Core Remove Dual Eligible field and Icon from the IL MMP Plans (CRM)
*					Muthukumar										09/07/2023				DF-8086 fix
*******************************************************************************************************************************************************************/
public with sharing class PoliciesSearchResults_LD_HUM
 {
     Public static Integer limitValue = Integer.valueOf(system.label.iPaginator);
     public static final Integer iLimitOne = GLOBAL_CONSTANT_LH_HUM.iLimitONe;

    /*
    * Method name : fetchPolices
    * Description : This method will return the list of MemberPlan object
    * Parameter   : Member Account Id
    */
     public static List<MemberPlan> fetchPolices(String recId)
     {

         List<MemberPlan> lstOfPolicies =  [SELECT id, Name,Policy_Platform__c, Product__c,OSB_Indicator__c,GroupNumber,
                                               Plan.Product_Type__c, Product_Type__c, EffectiveFrom, EffectiveTo, ASO__c, Plan.iab_description__c,
                                               Display_Group_Name__c,MemberId,Policy__c,Product_Type_Code__c,ETL_Record_Deleted__c, EHB_Term_Date__c, Member_Coverage_Status__c,
                                               Member_Id_Base__c, Member_Dependent_Code__c, Plan.Name, Member.MedicareId__c, LastModifiedDate
                                               FROM MemberPlan where MemberId = :recId  ORDER BY Member_Coverage_Status__c ASC,Product__c ASC, EffectiveFrom DESC Limit :limitValue];
         return lstOfPolicies;
     }

      /*
    * Method name : getpurchaserplandetails
    * Description : This method will return the list of PurchaserPlan object
    * Parameter   : Member Account Id
    */
        @AuraEnabled
        public static List<PurchaserPlan> getpurchaserplandetails(String mpId)
        {
            List<PurchaserPlan> lstOfPurchaserPlan =  [SELECT id, Name,EffectiveFrom,EffectiveTo
                                                       FROM PurchaserPlan where Policy__c = :mpId
                                                       order by EffectiveFrom desc Limit :limitValue];
            return lstOfPurchaserPlan;
        }

     /*
    * Method name : getMemberPlanDetails
    * Description : This method will return the MemberPlan details
    * Parameter   : Member Plan Id
    */
        @AuraEnabled
        public static List<MemberPlan> getMemberPlanDetails(String mpId)
        {
            List<MemberPlan> lstMemberPlans =  [SELECT id, Plan.PBP_Code__c , Plan.Contract_Number__c ,
                                                Plan.Medicare_Segment_ID__c, Product__c,Product_Type__c FROM MemberPlan
                                                where Id = :mpId Limit :iLimitOne];
            if(!lstMemberPlans.isEmpty())
            {
                return lstMemberPlans;
            }
           return null;
        }

      /*
    * Method name : getGroupAccPolicies
    * Description : This method will return the list of PurchaserPlan object
    * Parameter   : Group Account Id
    */
        @AuraEnabled
        public static List<PurchaserPlan> getGroupAccPolicies(String recId)
        {
            List<PurchaserPlan> lstOfPolicies =  [SELECT id, Name,ASO__c, Product__r.Name,Benefit_Coverage__c
                                              ,Platform__c,Major_LOB__c,Product_Description__c
                                              , EffectiveFrom, EffectiveTo
                                              ,Business_Segment__c, PayerId, Plan_Status__c,Issue_State__c
                                              FROM PurchaserPlan where PayerId = :recId  order by
                                              Plan_Status__c asc,Product__r.Name asc,EffectiveTo DESC Limit :limitValue];

            return lstOfPolicies;
        }


      /*
    * Method name : fetchPolicesDetails
    * Description : This method will return the Member Plan object
    * Parameter   : Policy Id
    */
     @AuraEnabled
      public static MemberPlan fetchPolicesDetails(String sPolicyId)
     {

         MemberPlan MemPlanObj =  [SELECT Id,Issue_State__c,Plan.Name,Plan.iab_description__c, Member_Id_Base__c , Name,ASO__c, GroupNumber,Display_Group_Name__c,
                                            Status, EffectiveTo, EffectiveFrom, Product_Type_Code__c, Product_Type__c,
                                            Product__c, Policy__c, Plan.Contract_Number__c,Plan.PBP_Code__c,Plan.Medicare_Segment_ID__c, ETL_Record_Deleted__c , Product_Description__c,Member_Coverage_Status__c, EHB_Term_Date__c, Member_Dependent_Code__c, member.name,
                                            Member.RecordType.Name, Policy_Platform__c, Plan.Client_Number__c
                                            FROM MemberPlan where Id = :sPolicyId Limit : iLimitOne];
         return MemPlanObj;
     }
 }