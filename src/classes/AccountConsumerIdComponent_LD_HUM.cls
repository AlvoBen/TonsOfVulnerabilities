/**************************************************************************** 
Apex Class Name  : AccountConsumerIdComponent_LD_HUM 
Version          : 1.0  
Function         : This class serves as data layer class for AccountConsumerIdComponent_LC_HUM

 * Developer Name           Code Review                  Date                       Description
 *---------------------------------------------------------------------------------------------------------------------
 * Joel George                                           03/03/2021                 Original Version
 * Kajal Namdev                                          05/05/2021                  Add query limit in SOQL
********************************************************************************************/ 
public with sharing class AccountConsumerIdComponent_LD_HUM 
{
     Public static Integer limitValue = Integer.valueOf(system.label.iPaginator);

    /*  
     * Method name : retrieveConsumerIds
     * Description : The Consumer Ids related to the Account are retrieved
     * Return Type : String
     * Parameter :   N/A
     */ 
    public static list<ConsumerID__c> retrieveConsumerIds(String AccountId)
    {        
        list<ConsumerID__c> lstConsumerIds = new list<ConsumerID__c>();
        lstConsumerIds = [Select Id, Consumer_ID__c, ID_Type__c from ConsumerID__c where Account__c = :AccountId order by ID_Type__c LIMIT: limitValue];
        return lstConsumerIds;    
    }
}