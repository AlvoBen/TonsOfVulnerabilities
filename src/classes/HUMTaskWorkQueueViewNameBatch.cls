/*******************************************************************************************************************************
Apex Class Name : HUMTaskWorkQueueViewNameBatch 
Version         : 1.0
Created On      : 24/03/2015
Function        : Used for task queue view name calculation.
Test Class      : HUMTaskWorkQueueViewNameBatchTest 

Modification Log: 
* Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Santhi Mandava              21968                      13/03/2015                 Original Version
* Santhi Mandava                                         04/29/2015                 Retrieving Work_Queue_View_ID__c field from task object 
                                                                                    which was created newly to store queue view id.
* Sharan Shanmugam                                       08/04/2016                 REQ 215747 - Changed Behavioral_Health__c to Complaint_Type__c                                                                                    
* Vijay Bhasker P                                        06/27/2017                 soql change as to include missing parameters (Previous_Owner_Queue__c)
* Mohammed Noor										     08/06/2021					Changes done for USER STORY 2498249.
*******************************************************************************************************************************/

public class HUMTaskWorkQueueViewNameBatch implements Database.Batchable<sObject>,Database.Stateful, Database.AllowsCallouts
{
    public DateTime endTime = null;
    Set<String> setQueueNames = null;
    HUMQueueViewBatchSettings__c objBatchSetup = null;    
    
    /**
    * Constructor for HUMTaskWorkQueueViewNameBatch class
    * <p>
    * @return    NA
    * @param     Set<String>
    */
    public HUMTaskWorkQueueViewNameBatch (Set<String> setUpdatedQueueNames, DateTime finishTime)
    {
        setQueueNames = setUpdatedQueueNames;        
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
        objBatchSetup=HUMQueueViewBatchSettings__c.getValues('QueueViewBatchSetup');
        if(setQueueNames ==null) setQueueNames  = HUMCaseTaskBatchHelper.getUpdatedQueueNames();
        
        return Database.getQueryLocator([Select  Id, Type, Subtype__c, Classification__c, Funding_Type__c, Plan_Issue_State__c, Interacting_With_Type__c,State_Mem_Resid_Add__c,Intent__c,
                        Plan_Segment_Size__c, Employer_Group_Name__c,Status,Topic__c, Group_Number__c, Created_By_Queue__c, LastModifiedby_Queue__c, Policy_Platform__c, Owner_Queue__c, Service_Center__c, 
                         Department__c, Product_Type__c, Previous_Case_Owner_Role__c, Work_Queue_View_Name__c,Previous_Owner_Queue__c,Group_Billing_State_Code__c,Policy_Product_Code__c,Provider_Shipping_State_Code__c from Task where (Owner_Queue__c in :setQueueNames AND Status != 'Closed' AND Status != 'Completed')]);
    }
    
    /**
    * Test method used to process records.
    * <p>
    * @return   void
    * @param    BatchableContext,List<sObject>
    */
    public void execute(Database.BatchableContext BC,List<sObject> batch) 
    {       
        if(endTime>System.now())
        {
            HUMUpdateTaskHelper oTaskHelper = new HUMUpdateTaskHelper();
            oTaskHelper.populateViewName(batch,false,setQueueNames);                      
        }
        else
        {                       
            objBatchSetup.TriggerExecutionRequired__c=true;
            update objBatchSetup; 
            system.abortjob(BC.getJobID());            
        }        
    }
    
    /**
    * Test method used to execute post logic.
    * <p>
    * @return   void
    * @param    BatchableContext
    */
    public void finish(Database.BatchableContext BC)
    {
        objBatchSetup.TriggerExecutionRequired__c = true;
        update objBatchSetup;         
    }
    
}