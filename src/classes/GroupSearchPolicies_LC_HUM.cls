/*******************************************************************************************************************************
Apex Class Name : GroupSearchPolicies_LC_HUM
Version         : 1.0
Created On      : 09/24/2020
Function        : This class is used for performing the Lightning Group Search for Policies
Test Class      : GroupSearchPolicies_LT_HUM

Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Ashish Kumar                                  	09/24/2020                	Original Version
*	 2.0		   Ankima											03/19/2021					Removed Try-catch block as it was not required
*	 3.0		   Ashish/Ritik										05/17/2021					Replaced references of Policy__c object with PurchaserPlan Object
*    4.0           Ankima                                           06/18/2021                  Checkmarx fix
*    5.0           Ashish Kumar                                  	08/12/2021                	Added getPurchaserPlanForGroup method for getting PurchaserPlan for Group Plan detail page
*******************************************************************************************************************************************************************/

public with sharing class GroupSearchPolicies_LC_HUM 
{
    /*  
    * Method name : getPoliciesForGroup
    * Description : This  method determines the policy level of access of the logged in user. 
    * Return Type : List<PurchaserPlan>
    * Parameter   : String
    */
    @AuraEnabled
    public static List<PurchaserPlan> getPoliciesForGroup(String sAccId) {
        List<PurchaserPlan> lstPolicies= new List<PurchaserPlan>();
        lstPolicies=GroupSearchPolicies_LD_HUM.requestPoliciesForGroup(sAccId);
         
        return lstPolicies;
    }
    
        /*  
    * Method name : getPoliciesForGroup
    * Description : This  method determines the policy level of access of the logged in user. 
    * Return Type : List<PurchaserPlan>
    * Parameter   : String
    */
    @AuraEnabled
    public static GroupSearchPolicies_LD_HUM.planWrapper getPurchaserPlanForGroup(String sPlanId) {
   
        return GroupSearchPolicies_LD_HUM.requestGroupPlan(sPlanId);
    }
}