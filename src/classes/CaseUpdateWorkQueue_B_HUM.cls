/*******************************************************************************************************************************
Apex Class Name : CaseUpdateWorkQueue_B_HUM 
Version         : 1.0
Created On      : 08/14/2018
Function        : Used for case Work Queue Transfering cases and updating view values to case.
Test Class      : CaseTaskUpdateWorkQueue_T_HUM 

Modification Log: 
* Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Prathyu Tushar               272718                   08/14/2018              To update the Service Center, Department, Owner Queue & Topic based on Edit or Deactivation of  
* Suresh Sunkari               285276                   09/12/2018              REQ - 370618 : To schedule the batch job.
* Pooja Kumbhar				   							06/30/2021				User story :- 2393353 Checkmarx error remidiation remediation
* Mohammed Noor										    08/06/2021				Changes done for USER STORY 2498249.
* Kiran Kumar											06/07/2022				USER STORY 3384916: INC1667806  -Fix Platform Job 
*******************************************************************************************************************************/
global with sharing class CaseUpdateWorkQueue_B_HUM implements Database.Batchable<sObject>, Schedulable, Database.stateful, Database.AllowsCallouts {
    public DateTime startTime = System.now();
    public DateTime endTime = System.now().addHours(4);
    
    CaseWQTransfer_H_HUM oCaseHelper = new CaseWQTransfer_H_HUM();
    HUMQueueViewBatchSettings__c objBatchSetup = HUMQueueViewBatchSettings__c.getValues('QueueViewBatchSetup');
    List<string> lstQueueNames = new List<String>();
    List<string> lstViewChangeNames = new List<String>();
    
    Map<String,Map<String,Object>> mapFinalChanges = new Map<String,Map<String,Object>>();
    Map<String,Map<String,WQS_Case_Transfer_History__c>> mapUniqueValues = new Map<String,Map<String,WQS_Case_Transfer_History__c>>();
    
    
    /**
    * Interface method to call run method
    * 
    * @param    scxt            SchedulableContext
    * @return   void
    */
    public void execute(SchedulableContext scxt)
    {
        Database.executeBatch((CaseUpdateWorkQueue_B_HUM)Type.forName('CaseUpdateWorkQueue_B_HUM').newInstance(), 200);
    }
    
    
    
    /**
       * Returns open cases that needs to be reassigned 
       * <p>
       * Method to execute pre logic
       *
       * @param     BC                      Database.BatchableContext
       * @return    Database.QueryLocator            
    */
    global Database.QueryLocator start(Database.BatchableContext BC)
    {
    
        Integer iJobs = [Select count() from AsyncApexJob where JobType ='BatchApex' AND Status = 'Holding' AND Id != : BC.getJobId()];
        if (iJobs > 100)
        {
            return null;
        }
        else
        {
            lstQueueNames = oCaseHelper.populateViewChanges();
            lstViewChangeNames.addAll(lstQueueNames);
            mapFinalChanges = oCaseHelper.PopulateWQChanes();
            mapUniqueValues = oCaseHelper.PopulateWQTransferChanges();
            lstQueueNames.addAll(mapUniqueValues.keySet());
            lstQueueNames.addAll(mapFinalChanges.keySet());
            if(Schema.SObjectType.HUMQueueViewBatchSettings__c.fields.TriggerExecutionRequired__c.isUpdateable() && Schema.SObjectType.HUMQueueViewBatchSettings__c.fields.BatchExecutionDate__c.isUpdateable() )
          {
            objBatchSetup.TriggerExecutionRequired__c=false; 
            objBatchSetup.BatchExecutionDate__c = System.now(); 
            update objBatchSetup;
            }
            
            return Database.getQueryLocator([Select  Id, Type, Subtype__c, Classification_Id__c,Classification_Id__r.Name, Funding_Type__c, Plan_Issue_State__c, Interacting_With_Type__c,State_Mem_Resid_Add__c,Intent_Id__c,Intent_Id__r.Name,
                        Plan_Segment_Size__c, Employer_Group_Name__c, Group_Number__c, Created_By_Queue__c,	Status,Topic__c,Policy_Platform__c,Owner_Queue__c, CTCI_List__c,CTCI_List__r.Classification__c, CTCI_List__r.Intent__c,LastModifiedby_Queue__c,Previous_Owner_Queue__c,
                        Service_Center__c, Department__c, Product_Type__c, Contract_Protected_Case__c, Previous_Case_Owner_Role__c,Complaint_Type__c,Group_Billing_State_Code__c,Policy_Product_Code__c,Provider_Shipping_State_Code__c,Work_Queue_View_Name__c,Case_Owner__c From Case where Owner_Queue__c in :lstQueueNames and IsClosed = false]);
        }
    }
    
     /**
       * Method to process the records
       *
       * @param     BC          Database.BatchableContext
       * @param     batch       List of case records
       * @return    void            
    */
    global void execute(Database.BatchableContext BC,List<sObject> batch) 
    {
        oCaseHelper.WQCaseChanges(batch,mapFinalChanges,mapUniqueValues);                     
        
    }
    
    /**
       * Method to execute post logic
       *
       * @param     BC          Database.BatchableContext
       * @return    void            
    */
    global void finish(Database.BatchableContext BC)
    {
        Database.ExecuteBatch(new  TaskUpdateWorkQueue_B_HUM(lstQueueNames, mapFinalChanges, mapUniqueValues, endTime));
        try
        {
            List<Map<String,WQS_Case_Transfer_History__c>> lstMapWQSTransferHistory = mapUniqueValues.values();
            List<WQS_Case_Transfer_History__c> lstWQSTransferHistory = new List<WQS_Case_Transfer_History__c>();
            for(Map<String,WQS_Case_Transfer_History__c> oMapWQS :  lstMapWQSTransferHistory){
                for(WQS_Case_Transfer_History__c oWQSTransferHistory : oMapWQS.values()){
                    oWQSTransferHistory.Transfer_Status__c ='Complete';
                    lstWQSTransferHistory.add(oWQSTransferHistory);
                }
            }
            Database.SaveResult[] lstSaveResult = Database.Update(lstWQSTransferHistory,false);
            List<Queue_View__c> lstQueueViews = [SELECT Id, Name, Queue_View_Name__c, Work_Queue_Name__r.Name, Criteria_Updated__c FROM Queue_View__c where Criteria_Updated__c = true and Work_Queue_Name__r.Name in :lstViewChangeNames ];
            if(!lstQueueViews.IsEmpty()){
                for( Queue_View__c oQueueView : lstQueueViews ){
                    oQueueView.Criteria_Updated__c = false;
                }
                Database.SaveResult[] lstSaveQVResult = Database.Update (lstQueueViews,false);
            }
               
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'CaseUpdateWorkQueue_B_HUM','finish');
        }
    }  
}