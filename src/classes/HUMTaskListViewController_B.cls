/*******************************************************************************************************************************
Apex Class Name : HUMTaskListViewController_B
Version         : 1.0
Created On      : 10/21/2016
Function        : Batch class to Update the Count for the Total_count__c and open_count__c for existing Data on tasks
Test Class      : HUMTaskListViewController_B_Test 

Modification Log: 
* Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
*Vamsi Kondragunta				44606			  10/21/2016              Update the Existing data for Req:72833
*Sagar MN                                         01/14/2019              REQ - 416054 Checmarx Sharing Vulnerability Fix 
*Pooja Kumbhar								      04/08/2021			  User story 2163293: T1PRJ0002736 / PR00094232 - 2021 MF6 - Case Management Checkmarx Security violations - Task Edit 
*******************************************************************************************************************************/

//Database.ExecuteBatch(new HUMTaskListViewController_B ());

Public with sharing class HUMTaskListViewController_B implements Database.Batchable<sObject>
{          
     Public Database.QueryLocator start(Database.BatchableContext BC)
     {  
      	String query ='select id from HUM_Task_Heirarchy__c';
     	return Database.getQueryLocator(query);
     }
     
     Public void execute(Database.BatchableContext BC, List<HUM_Task_Heirarchy__c> Scope)
     {
         List<HUM_Task_Heirarchy__c> lstUpdate = new List<HUM_Task_Heirarchy__c>();
         HUM_Task_Heirarchy__c updTaskH;
         
     	 for(HUM_Task_Heirarchy__c tasH : scope)
         {
         	updTaskH = new HUM_Task_Heirarchy__c();
            updTaskH.id=tasH.Id;
            lstUpdate.add(updTaskH);
         }
         
         if(lstUpdate.size() > 0 && Schema.sObjectType.HUM_Task_Heirarchy__c.fields.id.isUpdateable())
         {
             update lstUpdate;
         }
     } 
     
     Public void finish(Database.BatchableContext BC)
     {
      
     }

   }