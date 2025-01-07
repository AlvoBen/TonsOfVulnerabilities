/*****************************************************************************************
Apex class Name    : HUMTaskHistoryControllerTest
Version            : 1.0
Function           : This is test class for HUMTaskHistoryController
Created Date       : 24 Feb 2015

Modification Log:
*   Developer                    Code Review         Date               Description
* ------------------------------------------------------------------------------------------------------------------------------  
* Tanushree Hazari               21275              03/02/2015          Original version
* Kritika P Popat                21743              03/23/2015          Fixed for defect 176756.0001
* Prachi Bhasin                  33402              07/18/2016          Changes made as part of REQ- 270349 
* Pradeepkumar Dani              99570              06/08/2017          Intent__c.Classification__c & Intent__c.Etl_Idtfr__c Clean up
* Pradeepkumar Dani              100996              06/13/2017          Classification__c.Classification_Type__c & Etl_Idftr__c Clean Up
* Prudhvi Pamarthi               154304               01/19/2018            REQ - 328929 Fix for HUMExceptionHelper: DML 
* Santhi Mandava                                    03/12/2019           Removed "DpaaSWebServiceMockImpl" usage.
*****************************************************************************************************************************/
@isTest
private class HUMTaskHistoryControllerTest
{   
    /**
    * Test Method to set up test data
    * <p>
    * @return   Id
    * @param    NA
    */
    @testSetup static void setup() 
    {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        System.assert(oUser.id != null);
        
        System.runAs(oUser){
            List<HUMObject_History_Metadata__c> lstHistoryMetadata = new List<HUMObject_History_Metadata__c>();
            lstHistoryMetadata.add(new HUMObject_History_Metadata__c(Name='Task_ActivityDate',Tracked_Field__c='ActivityDate',Parent_Tracked_Object__c='Task'));
            lstHistoryMetadata.add(new HUMObject_History_Metadata__c(Name='Task_LastModifiedby_Queue',Tracked_Field__c='LastModifiedby_Queue__c',Parent_Tracked_Object__c='Task'));
            lstHistoryMetadata.add(new HUMObject_History_Metadata__c(Name='Task_OwnerQueue',Tracked_Field__c='Owner_Queue__c',Parent_Tracked_Object__c='Task'));
            lstHistoryMetadata.add(new HUMObject_History_Metadata__c(Name='Task_Priority',Tracked_Field__c='Priority',Parent_Tracked_Object__c='Task'));
            lstHistoryMetadata.add(new HUMObject_History_Metadata__c(Name='Task_Status',Tracked_Field__c='Status',Parent_Tracked_Object__c='Task'));
            lstHistoryMetadata.add(new HUMObject_History_Metadata__c(Name='Task_TaskOwner',Tracked_Field__c='Task_Owner__c',Parent_Tracked_Object__c='Task'));
            lstHistoryMetadata.add(new HUMObject_History_Metadata__c(Name='Task_Type',Tracked_Field__c='Type',Parent_Tracked_Object__c='Task'));
            insert lstHistoryMetadata;
            System.assertEquals(lstHistoryMetadata.size(), 7);
            
            List<HUMConstants__c> lstConstants = new List<HUMConstants__c>();
            lstConstants.add(new HUMConstants__c(Name='TaskHistoryQueryLimitSize',StringValue__c='350'));
            lstConstants.add(new HUMConstants__c(Name='TaskActivityDate',StringValue__c='ActivityDate'));
            lstConstants.add(new HUMConstants__c(Name='TaskHistoryChanged',StringValue__c='Changed'));
            lstConstants.add(new HUMConstants__c(Name='TaskHistoryFrom',StringValue__c='from'));
            lstConstants.add(new HUMConstants__c(Name='TaskHistoryTo',StringValue__c='to'));
            lstConstants.add(new HUMConstants__c(Name='TaskHistoryDeleted',StringValue__c='Deleted'));
            lstConstants.add(new HUMConstants__c(Name='TaskHistoryIn',StringValue__c='in'));
            lstConstants.add(new HUMConstants__c(Name='TaskHistoryDateFormat',StringValue__c='MM/dd/YYYY'));
            lstConstants.add(new HUMConstants__c(Name='TaskHistoryZero',StringValue__c='0'));
            lstConstants.add(new HUMConstants__c(Name='TaskHistoryListSize',StringValue__c='5'));
            lstConstants.add(new HUMConstants__c(Name='TaskHistoryConstantOne',StringValue__c='1'));
            lstConstants.add(new HUMConstants__c(Name='TaskHistoryConstantTwo',StringValue__c='2'));
            lstConstants.add(new HUMConstants__c(Name='FLAG_FALSE',StringValue__c='false'));
            lstConstants.add(new HUMConstants__c(Name='FLAG_TRUE',StringValue__c='true'));
            lstConstants.add(new HUMConstants__c(Name='TaskHistoryTotalPageSize',StringValue__c='25'));
            insert lstConstants;
            System.assertEquals(lstConstants.size(), 15);
            
            Account oAcc = HUMTestDataHelperTest.getAccount(); 
            oAcc.Name='TestAccounoTask';
            oAcc.Source_Platform_Code__c = 'CA';
            oAcc.Tenant_Id__c = '00';
            oAcc.General_Account__c = true;
            insert oAcc;                
            system.assertNotEquals(oAcc,null,'Value not matched');
            
            List<Classification__c> lstClassification = new List<Classification__c>();
            Classification__c classificationRec = new Classification__c();
            classificationRec.Name = 'Claims';
            classificationRec.Is_Active__c = true;
            lstClassification.add(classificationRec);           
            
            insert lstClassification;
            system.assertEquals(lstClassification[0].Name=='Claims',true);
            
            List<Intent__c> lstIntent = new List<Intent__c>();
            Intent__c intentRec = new Intent__c();
            intentRec.Name = 'Filling';
            intentRec.Is_Active__c = true;
            lstIntent.add(intentRec);

            insert lstIntent;
            system.assertEquals(intentRec.name == 'Filling', true);
            Case oCase = new Case(AccountId = oAcc.Id, Intent_Id__c = lstIntent[0].id, Classification_Id__c =lstClassification[0].id, General_Case__c = true, 
                               Interacting_With_Type__c = 'Member', Interacting_With__c = oAcc.Id, Due_Date__c = Date.Today(),
                               Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c = 'Cincinnati CRU', Work_Queue_View_Name__c = 'Cincinati CRU', 
                               Service_Center__c = 'Cincinnati', Department__c = 'CRU', status = 'In Progress');
            insert oCase;               
            system.assertNotEquals(oCase,null,'Value not matched');     
        }
     } 
    /**
    * Test method to prepare test data for HUMTaskHistoryController class
    * <p>
    * @return : NA
    * @param : NA
    */
    static testMethod void preparTestData()
    {            
         User oUser = [Select id,Name,Current_queue__c from User where LastName = 'test User1' LIMIT 1 ] ;
          String sBaseURL = URL.getSalesforceBaseUrl().toExternalForm();
        
        System.runAs(oUser)
        {   
            Case oCase1 = [select id from case limit 1];                                
            Task oTask = new Task(WhatId = oCase1.id, Task_Owner__c = UserInfo.getUserName(), Owner_Queue__c = 'Cincinnati CRU',
                               Work_Queue_View_Name__c = 'Cincinati CRU', Service_Center__c = 'Cincinnati',Department__c = 'CRU', status = 'In Progress', ActivityDate = Date.Today());
            insert oTask; 
            system.assertNotEquals(oTask, null, 'Value not matched');
                 
            ApexPages.currentPage().getParameters().put('id', oTask.id);
            
            Task_Field_History__c oTaskFieldHistory = new Task_Field_History__c (Created_At__c=System.now(),SObjFieldName__c='Priority',SObjFieldValue__c='High',SObjID__c=oTask.id);
            insert oTaskFieldHistory;
            system.assertNotEquals(oTaskFieldHistory, null, 'Value not matched');
            
            Task_Field_History__c oTaskFieldHistory1 = new Task_Field_History__c (Created_At__c=System.now(),SObjFieldName__c='Priority',SObjFieldValue__c='Critical',ParentFieldHistory_Id__c = oTaskFieldHistory.Id,SObjID__c=oTask.id);
            insert oTaskFieldHistory1;
            system.assertNotEquals(oTaskFieldHistory1, null, 'Value not matched');
            
            Task_Field_History__c oTaskFieldHistory2 = new Task_Field_History__c (Created_At__c=System.now(),SObjFieldName__c='ActivityDate',SObjFieldValue__c='2015-06-03 00:00:00',SObjID__c=oTask.id);
            insert oTaskFieldHistory2;
            system.assertNotEquals(oTaskFieldHistory2, null, 'Value not matched');
            
            Task_Field_History__c oTaskFieldHistory3 = new Task_Field_History__c (Created_At__c=System.now(),SObjFieldName__c='ActivityDate',SObjFieldValue__c='2015-03-03 00:00:00',ParentFieldHistory_Id__c = oTaskFieldHistory2.Id,SObjID__c=oTask.id);
            insert oTaskFieldHistory3;
            system.assertNotEquals(oTaskFieldHistory3, null, 'Value not matched');
            
            HUMTaskHistoryController oHistoryController = new  HUMTaskHistoryController();
            Test.startTest();
                oHistoryController.getActivityDate(oTaskFieldHistory2,oTaskFieldHistory3 );
                oHistoryController.getFieldsRecord();
                oHistoryController.getFirst();
                oHistoryController.getDisableFirst();
                oHistoryController.getNext();
                oHistoryController.getDisableNext();
                oHistoryController.getPrevious();
                oHistoryController.getDisablePrevious();
                oHistoryController.getLast();
                oHistoryController.getDisableLast();
                oHistoryController.getFirstRecordOf_Page();
                oHistoryController.getLastRecordOf_Page();
                oHistoryController.getTotal_SizeRecord(); 
             List<Error_Log__c> lstLog = new List<Error_Log__c>();
            Error_Log__c error = new Error_Log__c(Status__c = 'CaseEdit');
            lstLog.add(error);
            
            oHistoryController.listErrorLogs = lstLog;
            System.assert(oHistoryController.listErrorLogs.size() > 0);   
            Test.stopTest(); 
        }    
    }
}