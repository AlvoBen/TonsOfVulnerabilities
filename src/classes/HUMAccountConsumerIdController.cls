/**************************************************************************** 
Apex Class Name  : HUMAccountConsumerIdController 
Version          : 1.0  
Created Date     : October 28th 2014 
Function         : This class shows the consumer ID related list on an Account detail page.
Test Class       : HUMAccountConsumerIdControllerTest

 * Developer Name           Code Review                  Date                       Description
 *---------------------------------------------------------------------------------------------------------------------
 * Manuel Johnson           18823                        10/28/2014                  Original Version
********************************************************************************************/ 

public with sharing class HUMAccountConsumerIdController 
{
    /*
     * Method Name : HUMAccountConsumerIdController
     * Description : Constructor for the class
     * Return type : NA
     * Paramater   : NA     
     */
    public HUMAccountConsumerIdController(ApexPages.StandardController controller)
    {
        parentAccount = (Account)controller.getRecord();
        consumerIdsList = new List<ConsumerID__c>();          
    }       
  
    public Account parentAccount {get;set;}
    public List<ConsumerID__c> consumerIdsList {get;set;}  
   
    /*  
     * Method name : retrieveConsumerIds
     * Description : The Consumer Ids related to the Account are retrieved
     * Return Type : void
     * Parameter :   N/A
     */ 
    public void retrieveConsumerIds()
    {        
        //Retrieve the policy member Ids associated to the member  
        consumerIdsList = [Select Id, Consumer_ID__c, ID_Type__c from ConsumerID__c where Account__c = :parentAccount.Id order by ID_Type__c];    
    }
}