/*******************************************************************************************************************************
Apex Class Name : HUMCaseWorkQueueViewNameBatch 
Version         : 1.0
Created On      : 13/03/2015
Function        : Used for case queue view name calculation.
Test Class      : HUMCaseWorkQueueViewNameBatchTest 

Modification Log: 
* Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Santhi Mandava               21968                    13/03/2015                 Original Version
* Santhi Mandava                                        04/29/2015                 Instead of retrieveing Work_Queue_View_Name__c and Owner_Queue__c from case 
                                                                                   we are retrieving replaced fields OwnerQueue__c now.
* Amit Sinha                                            03/05/2015                 Made changes for the REQ - 186072, Deleting of Queue View Criteria. 
* Prachi Bhasin                33337                    18/07/2016                 Changes made as part of REQ- 270349
* Sharan Shanmugam                                      10/04/2016                 Changes made for WQ enhancements
* Harshith Mandya              50226                    11/30/2106                 Implemented REQ - 290990 -  removed all lookup related fields from 
                                                                                   Case/Task/Interaction/Attachment to Work Queue Setup and Queue View
* Pallavi Ravishankar          78710                    03/27/2017                 soql change as per ctci model for REQ 304793
* Vijay Bhasker P                                       06/27/2017                 soql change as to include missing parameters (LastModifiedby_Queue__c,Previous_Owner_Queue__c)
* Santhi Mandava                                        10/15/2020                 Passing loggedin users profile name to populateviewname method
* Mohammed Noor										    08/06/2021				   Changes done for USER STORY 2498249.  
*******************************************************************************************************************************/

public class HUMCaseWorkQueueViewNameBatch implements Database.Batchable<sObject>,Database.Stateful,Database.AllowsCallouts
{
    public DateTime startTime = System.now();
    public DateTime endTime = System.now().addHours(4);
    Set<String> setQueueNames=new Set<String>();
    List<Queue_View__c> lstQueueView = new List<Queue_View__c>();
    List<Work_Queue_Setup__c> lstWorkQueue = new List<Work_Queue_Setup__c>();    
    HUMUpdateCaseHelper oCaseHelper = new HUMUpdateCaseHelper();
    HUMQueueViewBatchSettings__c objBatchSetup = null;
        
     /**
       * Returns open cases that needs to be reassigned 
       * <p>
       * Method to execute pre logic
       *
       * @param     BC                      Database.BatchableContext
       * @return    Database.QueryLocator            
    */
    public Database.QueryLocator start(Database.BatchableContext BC)
    {
    
        Integer iJobs = [Select count() from AsyncApexJob where JobType ='BatchApex'
                            AND Status = 'Holding'
                            AND Id != : BC.getJobId()
                            ];
            
            // Apex flex Queue can hold 100 batches on hold
            if (iJobs > 100)
            {
                return null;
            }
            else
            {
                objBatchSetup=HUMQueueViewBatchSettings__c.getValues('QueueViewBatchSetup');
                setQueueNames  = HUMCaseTaskBatchHelper.getUpdatedQueueNames();
                return Database.getQueryLocator([Select  Id, Type, Subtype__c, Classification_Id__c,Classification_Id__r.Name, Funding_Type__c, Plan_Issue_State__c, Interacting_With_Type__c,State_Mem_Resid_Add__c,Intent_Id__c,Intent_Id__r.Name,
                            Plan_Segment_Size__c, Employer_Group_Name__c, Group_Number__c, Created_By_Queue__c, Status,Topic__c,Policy_Platform__c,Owner_Queue__c, CTCI_List__c,CTCI_List__r.Classification__c, CTCI_List__r.Intent__c,LastModifiedby_Queue__c,Previous_Owner_Queue__c,
                            Service_Center__c, Department__c, Product_Type__c, Previous_Case_Owner_Role__c,Complaint_Type__c,Group_Billing_State_Code__c,Policy_Product_Code__c,Provider_Shipping_State_Code__c,Work_Queue_View_Name__c from Case where (Owner_Queue__c in :setQueueNames AND Status != 'Closed' AND Status != 'Cancelled') limit :(limits.getLimitQueryRows() - limits.getQueryRows())]);

            }
    }
    
    /**
       * Method to process the records
       *
       * @param     BC          Database.BatchableContext
       * @param     batch       List of case records
       * @return    void            
    */
    public void execute(Database.BatchableContext BC,List<sObject> batch) 
    {
        if(endTime > System.now())
        {
            String profileName = HumUtilityHelper.getCurrentUserProfileName(); 
            HUMUpdateCaseHelper.populateViewName(batch,false,setQueueNames,profileName );                      
        }
        else
        {
            objBatchSetup.TriggerExecutionRequired__c = true;
            update objBatchSetup;
            system.abortjob(BC.getJobID());
        }
    }
    
     /**
       * Method to execute post logic
       *
       * @param     BC          Database.BatchableContext
       * @return    void            
    */
    public void finish(Database.BatchableContext BC)
    {
        Database.ExecuteBatch(new  HUMTaskWorkQueueViewNameBatch(setQueueNames, endTime));
        try
        {
            for(Queue_View__c QueueView : [Select id from Queue_View__c where Criteria_Updated__c = true and IsActive__c = true])
            {
                QueueView.Criteria_Updated__c = false;
                lstQueueView.add(QueueView);
            }
            Database.SaveResult[] lstSaveResult = Database.Update (lstQueueView,false);
            for(Work_Queue_Setup__c oWQS : [Select id from Work_Queue_Setup__c where Batch_Update_Required__c = true AND IsActive__c = TRUE])
            {
                oWQS.Batch_Update_Required__c = false;
                lstWorkQueue.add(oWQS);     
            }
            Database.SaveResult[] lstSaveResultWorkQueue = Database.Update (lstWorkQueue,false);
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'HUMCaseWorkQueueViewNameBatch','finish');
        }
    }
}