/**************************************************************************** 
Apex Class Name  : AccountDetailViewAlerts_LD_HUM
Version          : 1.0  
Created Date     : August 05/07/2021
Function         : class for dispaly Alerts on account detail page.
Test class       : AccountDetailViewAlerts_LT_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Ritik Agarwal									05/07/2021                 Original Version
* Kajal Namdev                                  08/18/2021          	   Removed limit from single record query
* Kajal Namdev                                  08/20/2021          	   Added limitOne in query
***************************************************************************************************/
public with sharing class AccountDetailViewAlerts_LD_HUM {
    public static final Integer iLimitOne = GLOBAL_CONSTANT_LH_HUM.iLimitONe;
    public static List<account> getAccountList(String sAccountId){
        List<account> lstAccount = [SELECT id, Birthdate__c, Enterprise_Id__C FROM Account where id =: sAccountId LIMIT :iLimitOne];
        return lstAccount;
    }

}