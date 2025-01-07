/*******************************************************************************************************************************
Apex Class Name : PurchaserPlanDetail_LC_HUM
Version         : 1.0
Created On      : 08/10/2021 
Function        : This is the controller class for Purchaser Plan Page
Test Class      : PurchaserPlanDetail_LT_HUM

Modification Log: 
* Version Number             Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
*     1.0                    Joel George                                           08/10/2021                  Original
*     2.0					 Ashish Kumar										   08/25/2021				   Implementation for Vendor service
*     3.0                    Joel George                                           08/10/2021                  Removing Try catch for code coverage
*     4.0                    Ashish Kumar                                          11/02/2021                     Rollback changes for Group Vendor

*************************************************************************************************************************************************/

public with sharing class PurchaserPlanDetail_LC_HUM 
{

    /*
    *  This method  is used to return details of a Purchaser Plan
    *  @name   getPurchaserPlan
    *  @param  ID - Record ID of Purchaser Plan for which claims are to be displayed
    *  @return List<PurchaserPlan> -  List of PurchaserPlan records
    *  @throws getPurchaserPlan
    */
    @AuraEnabled
    public static List<PurchaserPlan> getPurchaserPlan(String sRecId)
    {
            return PurchaserPlanDetail_LD_HUM.getPurchaserPlan(sRecId);
    } 
}