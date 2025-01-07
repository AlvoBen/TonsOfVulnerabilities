/*******************************************************************************************************************************
Apex Class Name : PurchaserPlanDetail_LD_HUM
Version         : 1.0
Created On      : 08/10/2021
Function        : This is the data layer class for Purchaser Plan 
Test Class      : PurchaserPlanDetail_LT_HUM

Modification Log: 
* Version Number             Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
*     1.0                    Joel George                                            08/10/2021                 Original
*     2.0  				     Ashish Kumar											08/30/2021				   Added fields to the query
*     3.0                    Kajal Namdev                                           09/29/2021          	   Added limitOne in query
******************************************************************************************************************************************************************************/

public with sharing class PurchaserPlanDetail_LD_HUM
{ 

     /*
    *  This method  is used to return details of a Purchaser Plan
    *  @name   getPurchaserPlan
    *  @param  ID - Record ID of Purchaser Plan for which claims are to be displayed
    *  @return List<PurchaserPlan> -  List of PurchaserPlan records
    *  @throws getPurchaserPlan
    */
    public static List<PurchaserPlan> getPurchaserPlan(String sRecId)
    { 

            List<PurchaserPlan> lstPurchPlan = new List<PurchaserPlan>();
            if(sRecId != null && String.isNotBlank(sRecId))
            {
                lstPurchPlan = [SELECT Id,Product_Type__c,Issue_State__c,Exchange_Indicator__c,Exchange__c,Exchange_Type__c,Metallic_Tier__c,Product__r.name,
                payer.name,payer.Group_Number__c,Major_LOB__c,effectiveFrom,effectiveTo,Source_Cust_Cov_Key__c,Platform__c
                FROM PurchaserPlan WHERE ID=:sRecId LIMIT 1];
            }
            return lstPurchPlan;
    }   

}