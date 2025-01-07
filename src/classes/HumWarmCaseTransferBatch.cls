/*******************************************************************************************************************************
Apex Class Name :  HumWarmCaseTransferBatch
Version: 1.0
Created On      : 05/10/2016
Function        :   To execute the batch job to update the cases in pending- transfer status
                  A detailed analysis was done on date '12/05/2017' by developer "Vijaya Koriprolu" on Sharing without option for this class.
        During the analysis it was found that this Isis  used To update the Status, Transferred_Date__c, Transferred_From__c, User__c And Warm_Case_Transfer_Actions__c  For existing data On cases, restrictions to this class will break the functionality.
                  This was further discussed with Architects Amrish And Jeremy And got approval to keep the "Without Sharing" mode of this class as it Is.
Test Class      : HumWarmCaseTransferBatchTest

        Modification Log:  
* Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
*   Shikhar Mehta                                      05/10/2016                   Req - 282376(Original Version)                                          
*   Vamsi Kondragunta          47603                   11/11/2016                   Defect fix for #244725.0001
*   Vamsi Kondragunta          59634									 02/01/2017                   Changes for REQ - 296903;updated the scope of the query.	
*******************************************************************************************************************************/
 Global Class HumWarmCaseTransferBatch Implements Database.Batchable<sObject>,Schedulable
  {  
        
     Global Database.QueryLocator start(Database.BatchableContext BC)
     {
       DateTime hrBefore = System.Now().addHours(-24); 
       String strStatus = 'Pending - Transfer';
       String query ='Select Id,Warm_Case_Transfer_Action__c,Case_Owner__c,Status,CaseNumber, User__r.Name,OwnerId, Transferred_From__r.Name from Case where Warm_Case_Transfer_Action__c = null AND Transferred_Date__c <=:hrBefore AND status= :strStatus';  
        Return Database.getQueryLocator(query);
     }
     
     Global void execute(Database.BatchableContext BC, List<Case> Scope)
     {
       For (Case c : scope)
       {
         c.Status = System.label.HUMInProgress;
         c.User__c = NULL;
         c.Transferred_From__c = NULL;
         c.Warm_Case_Transfer_Action__c = NULL;
         c.Transferred_Date__c = NULL;
       }
       update scope;
     } 
     
     /*
    * Method Name : execute
    * Description : This Is used to execute the batch.
    * Return type : void
    * Paramater   : SchedulableContext
    */
    Public void execute(SchedulableContext SC)
    {           
        Database.ExecuteBatch(New HumWarmCaseTransferBatch());                   
    }
     
     Global void finish(Database.BatchableContext BC)
     {
      
     }
     
   }