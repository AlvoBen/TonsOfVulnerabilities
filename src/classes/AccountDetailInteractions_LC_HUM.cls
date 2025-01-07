/*******************************************************************************************************************************
Apex Class Name : AccountDetailInteractions_LC_HUM
Version         : 1.0
Created On      : 09/25/2020
Function        : This Class serves as test class to AccountDetailInteractions_LC_HUM. 
Test Class      : AccountDetailInteractions_LT_HUM
Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Pradeep Dani                                             10/14/2020                initial version
* Ashish Kumar                                             11/26/2020                Modified query
* Ritik Agarwal                                            03/03/2021               shifted SOQL to data layer class(AccountDetailInteractions_LD_HUM)
*********************************************************************************************************************************/
public with sharing class AccountDetailInteractions_LC_HUM
{
    /*
     * Method Name : getInteractions
     * Description : fetch data from interaction object
     * Return type : Object
     * Parameter   : accountId
     */
    @AuraEnabled
    public static Object getInteractions(string sAccId)
    {  
       return AccountDetailInteractions_LD_HUM.getInteractions(sAccId);
    }
}