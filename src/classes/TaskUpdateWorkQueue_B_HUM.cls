/*******************************************************************************************************************************
Apex Class Name : TaskUpdateWorkQueue_B_HUm 
Version         : 1.0
Created On      : 08/14/2018
Function        : Used to refelect the work Queue changes in tasks and calculate the view
Test Class      : CaseTaskUpdateWorkQueue_T_HUM 

Modification Log: 
* Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Prathyu Tushar			   272718					08/14/2018				To update the Service Center, Department, Owner Queue & Topic based on Edit or Deactivation of  
* Pooja Kumbhar				   							06/30/2021				User story :- 2393353 Checkmarx error remidiation remediation
* Mohammed Noor										    08/06/2021				Changes done for USER STORY 2498249.
*******************************************************************************************************************************/
public with sharing class TaskUpdateWorkQueue_B_HUM implements Database.Batchable<sObject>,Database.Stateful, Database.AllowsCallouts{
    
    public DateTime endTime = null;
    List<String> setQueueNames = null;
    CaseWQTransfer_H_HUM oCaseHelper = new CaseWQTransfer_H_HUM();
    HUMQueueViewBatchSettings__c objBatchSetup = HUMQueueViewBatchSettings__c.getValues('QueueViewBatchSetup');
    Map<String,Map<String,Object>> mapFinalChanges = new Map<String,Map<String,Object>>();
    Map<String,Map<String,WQS_Case_Transfer_History__c>> mapUniqueValues = new Map<String,Map<String,WQS_Case_Transfer_History__c>>();   
    
    /**
    * Constructor for HUMTaskWorkQueueViewNameBatch class
    * <p>
    * @return    NA
    * @param     Set<String>
    */
    public TaskUpdateWorkQueue_B_HUm(List<String> setUpdatedQueueNames,Map<String,Map<String,Object>> FinalChanges, Map<String,Map<String,WQS_Case_Transfer_History__c>> mapUniqueChanges, DateTime finishTime)
    {
        setQueueNames = setUpdatedQueueNames;
        mapFinalChanges = FinalChanges;
        mapUniqueValues = mapUniqueChanges;  
        endTime = finishTime;
    }
    
    /**
    * Test method used to execute pre logic.
    * <p>
    * @return    Database.QueryLocator
    * @param     BatchableContext
    */
    public Database.QueryLocator start(Database.BatchableContext BC)
    {
      
        return Database.getQueryLocator([Select  Id, Type, Subtype__c, Classification__c, Funding_Type__c, Plan_Issue_State__c, Interacting_With_Type__c,State_Mem_Resid_Add__c,Intent__c,
                        Plan_Segment_Size__c, Employer_Group_Name__c,Status,Topic__c, Group_Number__c, Created_By_Queue__c, LastModifiedby_Queue__c, Policy_Platform__c, Owner_Queue__c, Service_Center__c, 
                         Department__c, Product_Type__c, Previous_Case_Owner_Role__c, Work_Queue_View_Name__c,Previous_Owner_Queue__c,Group_Billing_State_Code__c,Policy_Product_Code__c,Provider_Shipping_State_Code__c,Task_Owner__c from Task where (Owner_Queue__c in :setQueueNames AND Status != 'Closed' AND Status != 'Completed')]);
    }
    
    /**
    * Test method used to process records.
    * <p>
    * @return   void
    * @param    BatchableContext,List<sObject>
    */
    public void execute(Database.BatchableContext BC,List<sObject> batch) 
    {       
        
        oCaseHelper.WQTaskChanges(batch,mapFinalChanges,mapUniqueValues);        
    }
    
    /**
    * Test method used to execute post logic.
    * <p>
    * @return   void
    * @param    BatchableContext
    */
    public void finish(Database.BatchableContext BC)
    {
        if(Schema.SObjectType.HUMQueueViewBatchSettings__c.fields.TriggerExecutionRequired__c.isUpdateable())
        {
        objBatchSetup.TriggerExecutionRequired__c = true; 
        update objBatchSetup;
        }
        HUMQueueViewBatchSettings__c objBatchHelper = HUMQueueViewBatchSettings__c.getValues('WorkQueueTransferChanges');
        if(Schema.SObjectType.HUMQueueViewBatchSettings__c.fields.BatchExecutionDate__c.isUpdateable())
        {
        objBatchHelper.BatchExecutionDate__c = System.now(); 
        update objBatchHelper;  
        }       
    }
    
}