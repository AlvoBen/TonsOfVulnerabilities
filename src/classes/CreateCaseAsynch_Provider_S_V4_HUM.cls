/************************************************************************************************************************************************************************************       
Apex Class Name  : CreateCaseAsynch_Provider_S_V4_HUM.cls 
Created Date     : october 10, 2020
Function         : Queable class to perform DML operations
Test Class       : 

Modification Log:
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
* Sagar MN                                						10/10/2020                  Intital Version
************************************************************************************************************************************************************************************/
public class CreateCaseAsynch_Provider_S_V4_HUM implements Queueable, Database.AllowsCallouts{
	
    Map<String,Account> accountMapToUpsertKnown = new Map<String,Account>();    
    
    public CreateCaseAsynch_Provider_S_V4_HUM(Map<String,Account> accountMapToUpsert){
        if(accountMapToUpsert!=NULL){
            accountMapToUpsertKnown.putAll(accountMapToUpsert);
        }
    }
    
      /*  
    * Method name : execute
    * Description : This method Is used to do callout asynchronously.
    * Return Type : Void
    * Parameter   : QueueableContext    
    */
    public void execute(QueueableContext context) {
       CaseServiceProvider_v4_H_HUM.performUpsertForKnown(accountMapToUpsertKnown);        
    }
}