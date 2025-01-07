/*******************************************************************************************************************************
Apex Class Name : StandardQuickFindLinks_LC_HUM
Version         : 1.0
Created On      : 09/25/2020
Function        : This Class serves to fetch quick find links for account record types. 
Test Class      : StandardQuickFindLinks_LT_HUM
Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Pradeep Dani                                             10/14/2020                initial version
* Joel George                                              03/03/2021               initial version
*********************************************************************************************************************************/
public with sharing class StandardQuickFindLinks_LC_HUM 
{
    @AuraEnabled(cacheable=true)
    public static List<Link__c> getLinks(string sKey)
    {   
        return StandardQuickFindLinks_LD_HUM.getLinks(sKey);
    }
}