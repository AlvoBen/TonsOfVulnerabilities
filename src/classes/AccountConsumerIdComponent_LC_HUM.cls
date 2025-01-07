/**************************************************************************** 
Apex Class Name  : AccountConsumerIdComponent_LC_HUM 
Version          : 1.0  
Function         : This class shows the consumer ID related list on an Account detail page.
Test Class       : AccountConsumerIdComponent_LC_HUMTest

 * Developer Name           Code Review                  Date                       Description
 *---------------------------------------------------------------------------------------------------------------------
 * Akshay Kashyap                                      11/8/2020                  Original Version
 * Joel George                                         03/03/2021                 Code Refactoing
********************************************************************************************/ 
public with sharing class AccountConsumerIdComponent_LC_HUM 
{
    /*  
     * Method name : retrieveConsumerIds
     * Description : The Consumer Ids related to the Account are retrieved
     * Return Type : String
     * Parameter :   N/A
     */ 
    @AuraEnabled
    public static list<ConsumerID__c> retrieveConsumerIds(String AccountId)
    {        
       return AccountConsumerIdComponent_LD_HUM.retrieveConsumerIds(AccountId);  
    }
}