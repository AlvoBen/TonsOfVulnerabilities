/*******************************************************************************************************************************
Apex Class Name : HUMCaseTaskListViewController_B
Version         : 1.0
Created On      : 10/21/2016
Function        : Batch Class to Update the Count for the Total_Work_Task_Count__c And Open_Work_Task_Count__c for existing Data on cases
                  A detailed analysis was done on date "25/10/2017" Prathyu Tushar Talabattula on Sharing without option for this class.
                  During the analysis it was found that this class Is batch class which Is declared with global which Is used to update
                  the fields Total_Work_Task_Count__c And  Open_Work_Task_Count__c. Restrictions to this class will break the functionality.
                  This was further discussed with Architects Amrish And Jeremy And got approval to keep the "Without Sharing" mode of this class as Is.
                  This class has been approved to remain in Without Sharing mode.

        Test Class      : HUMCaseTaskListViewController_B_Test

        Modification Log:  
* Developer Name              Code Review                   Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
*Vamsi Kondragunta				44606         			  10/21/2016              Update the Existing data for Req:72833
*Vamsi Kondragunta			           			          01/27/2017              Update data for REQ - 296902
*Prathyu Tushar                 144586                    10/25/2017              Updated the Header part with reasons for keeping this function with "without sharing" for REQ - 320477
*******************************************************************************************************************************/

//Database.ExecuteBatch(new HUMCaseTaskListViewController_B ());
global class HUMCaseTaskListViewController_B implements Database.Batchable<sObject>
{          
     global Database.QueryLocator start(Database.BatchableContext BC)
     { 
        String query ='select whatid from task';
        return Database.getQueryLocator(query);
         
    }
     
     global void execute(Database.BatchableContext BC, List<task> Scope)
     {
         String sClosed = HUMConstants__c.getInstance('HUMClosed').StringValue__c;
         String sWorkTask = HUMConstants__c.getInstance('HUMWorkTask').StringValue__c;
         List<Case> lstUpdateNew = new List<Case>(); 
         Set<Id> setCaseIds = new Set<Id>();
         Case tempCase;
         Integer totalTasks=0;
         Integer openTasks=0;
         
         for(Task tsk : Scope) 
         {
             setCaseIds.add(tsk.whatid);
         }
         
         Map<Id, Case> mapCase = new Map<Id, Case>([select Id, (Select Id,Status From Tasks Where Type = :sWorkTask) from Case Where Id IN : setCaseIds and Total_Work_Task_Count__c=NULL ]);
         
         for(Case objcase : mapCase.values()) 
         {
         	totalTasks = 0;
            openTasks = 0;
            for(Task tsk : objcase.Tasks)
            {
             	if(tsk.status != sClosed)
                {
                	openTasks++;
                }
                totalTasks++;
            }
            tempCase = new Case();
            tempCase.Id = objcase.id;
            tempCase.Open_Work_Task_Count__c = openTasks;
            tempCase.Total_Work_Task_Count__c = totalTasks;
            lstUpdateNew.add(tempCase);
         }  
         
         if(lstUpdateNew.size() > 0 )
         {
         	update lstUpdateNew;
         }
     } 
     
     global void finish(Database.BatchableContext BC)
     {
      
     }

}