/***********************************************************************************************************
Apex Class Name  : taskViewControllerHumTest
Version          : 1.0
Function         : Test class for taskViewControllerHUM ApexClass
************************************************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Manoj M                   Vinay L             30/06/22                Test class for taskViewControllerHum.cls
************************************************************************************************************/

@isTest
public class taskViewControllerHumTest { 
    
/*
* This method creates test data for test class
* @param None
* @return None
*/    
    @testSetup static void setUpTestData()
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
* Test method to test positive scenerios for taskViewControllerHUM class
* <p>
* @return : NA
* @param : NA
*/
    static testMethod void testtaskViewControllerHumPositive()
    {            
        User oUser = [Select id,Name,Current_queue__c from User where LastName = 'test User1' LIMIT 1 ] ;
        System.runAs(oUser)
        {   
            Case oCase1 = [select id from case limit 1];                                
            Task oTask = new Task(WhatId = oCase1.id, Task_Owner__c = UserInfo.getUserName(), Owner_Queue__c = 'Cincinnati CRU',
                                  Work_Queue_View_Name__c = 'Cincinati CRU', Service_Center__c = 'Cincinnati',Department__c = 'CRU', status = 'In Progress', ActivityDate = Date.Today());
            insert oTask; 
            system.assertNotEquals(oTask, null, 'Value not matched');
            
            
            list<Task_Field_History__c> lstTaskFieldHistory =new list<Task_Field_History__c>();
            
            Task_Field_History__c oTaskFieldHistory = new Task_Field_History__c (Created_At__c=System.now(),SObjFieldName__c='Priority',SObjFieldValue__c=null,SObjID__c=oTask.id);
            insert oTaskFieldHistory;
            system.assertNotEquals(oTaskFieldHistory, null, 'Value not matched');
            lstTaskFieldHistory.add(oTaskFieldHistory);
            
            Task_Field_History__c oTaskFieldHistory1 = new Task_Field_History__c (Created_At__c=System.now(),SObjFieldName__c='Priority',SObjFieldValue__c='Critical',ParentFieldHistory_Id__c = oTaskFieldHistory.Id,SObjID__c=oTask.id);
            insert oTaskFieldHistory1;
            system.assertNotEquals(oTaskFieldHistory1, null, 'Value not matched');
            lstTaskFieldHistory.add(oTaskFieldHistory1);
            
            Task_Field_History__c oTaskFieldHistory2 = new Task_Field_History__c (Created_At__c=System.now(),SObjFieldName__c='ActivityDate',SObjFieldValue__c='2022-06-03 00:00:00',SObjID__c=oTask.id);
            insert oTaskFieldHistory2;
            system.assertNotEquals(oTaskFieldHistory2, null, 'Value not matched');
            lstTaskFieldHistory.add(oTaskFieldHistory2);
            
            Task_Field_History__c oTaskFieldHistory3 = new Task_Field_History__c (Created_At__c=System.now(),SObjFieldName__c='ActivityDate',SObjFieldValue__c='',ParentFieldHistory_Id__c = oTaskFieldHistory2.Id,SObjID__c=oTask.id);
            insert oTaskFieldHistory3;
            system.assertNotEquals(oTaskFieldHistory3, null, 'Value not matched');
            lstTaskFieldHistory.add(oTaskFieldHistory3);
            
            Task_Field_History__c oTaskFieldHistory4 = new Task_Field_History__c (Created_At__c=System.now(),SObjFieldName__c='LastModifiedby_Queue__c',SObjFieldValue__c='Critical',ParentFieldHistory_Id__c = oTaskFieldHistory.Id,SObjID__c=oTask.id);
            insert oTaskFieldHistory4;
            system.assertNotEquals(oTaskFieldHistory4, null, 'Value not matched');
            lstTaskFieldHistory.add(oTaskFieldHistory4);
            
            Task_Field_History__c oTaskFieldHistory5 = new Task_Field_History__c (Created_At__c=System.now(),SObjFieldName__c='LastModifiedby_Queue__c',SObjFieldValue__c='',ParentFieldHistory_Id__c = oTaskFieldHistory.Id,SObjID__c=oTask.id);
            insert oTaskFieldHistory5;
            system.assertNotEquals(oTaskFieldHistory5, null, 'Value not matched');
            lstTaskFieldHistory.add(oTaskFieldHistory5);
            
            Task_Field_History__c oTaskFieldHistory6 = new Task_Field_History__c (Created_At__c=System.now(),SObjFieldName__c='ActivityDate',SObjFieldValue__c='',SObjID__c=oTask.id);
            insert oTaskFieldHistory6;
            system.assertNotEquals(oTaskFieldHistory6, null, 'Value not matched');
            lstTaskFieldHistory.add(oTaskFieldHistory6);
            
            Task_Field_History__c oTaskFieldHistory7 = new Task_Field_History__c (Created_At__c=System.now(),SObjFieldName__c='ActivityDate',SObjFieldValue__c='2022-06-15 00:00:00',ParentFieldHistory_Id__c = oTaskFieldHistory6.Id,SObjID__c=oTask.id);
            insert oTaskFieldHistory7;
            system.assertNotEquals(oTaskFieldHistory7, null, 'Value not matched');
            lstTaskFieldHistory.add(oTaskFieldHistory7);
                    
            Task_Field_History__c oTaskFieldHistory8 = new Task_Field_History__c (Created_At__c=System.now(),SObjFieldName__c='ActivityDate',SObjFieldValue__c='2022-06-03 00:00:00',SObjID__c=oTask.id);
            insert oTaskFieldHistory8;
            system.assertNotEquals(oTaskFieldHistory8, null, 'Value not matched');
            lstTaskFieldHistory.add(oTaskFieldHistory8);
            
            Task_Field_History__c oTaskFieldHistory9 = new Task_Field_History__c (Created_At__c=System.now(),SObjFieldName__c='Priority',SObjFieldValue__c='',ParentFieldHistory_Id__c = oTaskFieldHistory.Id,SObjID__c=oTask.id);
            insert oTaskFieldHistory9;
            system.assertNotEquals(oTaskFieldHistory9, null, 'Value not matched');
            lstTaskFieldHistory.add(oTaskFieldHistory9);
            
            Task_Field_History__c oTaskFieldHistory10 = new Task_Field_History__c (Created_At__c=System.now(),SObjFieldName__c='ActivityDate',SObjFieldValue__c='2022-06-16 00:00:00',ParentFieldHistory_Id__c = oTaskFieldHistory8.Id,SObjID__c=oTask.id);
            insert oTaskFieldHistory10;
            system.assertNotEquals(oTaskFieldHistory10, null, 'Value not matched');
            lstTaskFieldHistory.add(oTaskFieldHistory10);            
    
            Test.startTest();
            taskViewControllerHUM.getTaskFieldTracking(oTask.Id);
            taskViewControllerHUM.trackLastModifiedQueue(lstTaskFieldHistory);
            Test.stopTest(); 
        } 
    }
/**
* Test method to test for Negative Scenarios for taskViewControllerHUM class
* <p>
* @return : NA
* @param : NA
*/
    static testMethod void testtaskViewControllerHumNegative()
    {            
        User oUser = [Select id,Name from User where LastName = 'test User1' LIMIT 1 ] ;
       
        System.runAs(oUser)
        { 

            Task oTask1 = new Task( Task_Owner__c = UserInfo.getUserName(), Owner_Queue__c = 'Cincinnati CRU',
                                   Work_Queue_View_Name__c = 'Cincinati CRU', Service_Center__c = 'Cincinnati',Department__c = 'CRU', status = 'In Progress', ActivityDate = Date.Today());
            insert oTask1; 
            system.assertNotEquals(oTask1, null, 'Value not matched');

            list<Task_Field_History__c> lstTaskFieldHistory =new list<Task_Field_History__c>();
                       
            Task_Field_History__c oTaskFieldHistory = new Task_Field_History__c (Created_At__c=System.now(),SObjFieldName__c='Priority',SObjFieldValue__c=null,SObjID__c=oTask1.id);
            insert oTaskFieldHistory;
            system.assertNotEquals(oTaskFieldHistory, null, 'Value not matched');
            lstTaskFieldHistory.add(oTaskFieldHistory);
            
            Test.startTest();
            taskViewControllerHUM.getTaskFieldTracking(oTask1.Id);
            taskViewControllerHUM.trackHistoryData();
            Test.stopTest(); 
        }
    } 
    /**
* Test method to cover null part for taskViewControllerHUM class
* <p>
* @return : NA
* @param : NA
*/
    static testMethod void testtaskViewControllerHumNegative1()
    {            
        User oUser = [Select id,Name from User where LastName = 'test User1' LIMIT 1 ] ;
        try{
        System.runAs(oUser)
           { 
           
            Task oTask1 = new Task( Task_Owner__c = UserInfo.getUserName(), Owner_Queue__c = 'Cincinnati CRU',
                                   Work_Queue_View_Name__c = 'Cincinati CRU', Service_Center__c = 'Cincinnati',Department__c = 'CRU', status = 'In Progress', ActivityDate = Date.Today());
            insert oTask1; 
            
            list<Task_Field_History__c> lstTaskFieldHistoryempty =new list<Task_Field_History__c>();            
            Test.startTest();
            taskViewControllerHUM.getTaskFieldTracking(oTask1.Id);
            taskViewControllerHUM.getTaskFieldTracking('1234');
            Test.stopTest(); 
           }       
        }
        catch(Exception ex){
         HUMExceptionHelper.logErrors(ex, 'taskViewControllerHumTest', 'testtaskViewControllerHumNegative1');
    }
    }
}