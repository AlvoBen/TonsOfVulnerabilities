/*****************************************************************************************
Apex class Name    : AccLegacyContactHistory_LC_HUM
Version            : 1.0
Function           : SOQL on Account   
Created Date       : 03/08/2021 
Test Class         : AccLegacyContactHistory_LT_HUM

Modification Log:
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------   
    Ritik Agarwal                                      03/08/2021          Initial version(#2039867)
 *********************************************************************************************************************************/
public with sharing class AccLegacyContactHistory_LD_HUM {
   
    /*
     * Method Name : getAccDetails
     * Description : Query to fetch Account records.
     * Return type : List of Account
     * Parameter   : AccountID
     */
    public static List<Account> getAccDetails(String recId)
    {
      return [SELECT Id, Enterprise_Id__c, Mbr_Gen_Key__c FROM Account WHERE Id =: recId LIMIT 1];  
    }
}