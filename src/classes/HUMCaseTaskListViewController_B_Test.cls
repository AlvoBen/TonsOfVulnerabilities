/*******************************************************************************************************************************
Apex Class Name : HUMCaseTaskListViewController_B_Test
Version         : 1.0
Created On      : 10/21/2016
Function        : Test class for Batch class HUMCaseTaskListViewController_B

Modification Log: 
* Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Vamsi Kondragunta				44606			  10/21/2016              Update the Existing data for Req:72833
* Pradeepkumar Dani				99570			  06/08/2017			  Intent__c.Classification__c & Intent__c.Etl_Idtfr__c Clean up
* Pradeepkumar Dani				100996 			  06/13/2017			  Classification__c.Classification_Type__c & Etl_Idftr__c Clean Up
* Rajesh Chandgothia			139246 			  10/18/2017			  REQ - 332391 increase Test Coverage
*******************************************************************************************************************************/
@isTest
public class HUMCaseTaskListViewController_B_Test {
    
    Static Testmethod void UnitTest(){
        User oUser = HUMTestDataHelperTest.createTestUser('User12','Customer Care Specialist');
    	oUser.Current_Queue__c = 'Cincinnati CRU';
        HUM_Task_Heirarchy__c humHierarchy = new HUM_Task_Heirarchy__c();
        System.runAs(oUser){
             insert new HUMConstants__c( Name = 'HUMClosed' , StringValue__c = 'Closed');
            insert new HUMConstants__c( Name = 'HUMWorkTask' , StringValue__c = 'Work Task');
            
            Account oAcc = HUMTestDataHelperTest.getAccount(); 
            oAcc.Name='TestAccount1';
            oAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
            insert oAcc;
            
            Classification__c classificationRecDec = new Classification__c();
	        classificationRecDec.Name = 'Claims';
	        classificationRecDec.Is_Active__c = true;
	        insert classificationRecDec;
	        system.assertEquals(classificationRecDec.Name == 'Claims',true);
	        
	        Intent__c intentRec = new Intent__c();
	        intentRec.Name = 'Filling';
	        intentRec.Is_Active__c = true;
	        insert intentRec;
	        system.assertEquals(intentRec.name == 'Filling',true);
           
            List<Case> testcases = new List<Case>();
            Case oCase1 = new Case (AccountId = oAcc.Id, Intent_id__c = intentRec.id, Classification_ID__c = classificationRecDec.id,
                               Interacting_With_Type__c = 'Group', Interacting_With__c = oAcc.Id, Due_Date__c = Date.Today(),
                               Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c ='Cincinnati CRU', Work_Queue_View_Name__c='Cincinati CRU', Open_Work_Task_Count__c=1,
                               Total_Work_Task_Count__c = NULL, Service_Center__c = 'Cincinnati', Department__c = 'CRU', status = 'In Progress');
            testcases.add(oCase1);
            insert testcases;                        
            system.assertEquals(testcases[0].status == 'In Progress',true);
            
            List<Task> testTasks = new List<Task>();    
            Task oTask1 = new Task(WhatId = oCase1.id,type='Work Task', Task_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU',
                               Work_Queue_View_Name__c='Cincinati CRU', Service_Center__c ='Cincinnati', Department__c='CRU', Open_Work_Task_Count__c=NULL,
                               Total_Work_Task_Count__c=NULL,status='In Progress');
            //testTasks.add(oTask1); 
            insert oTask1;
            Task oTask2 = new Task(WhatId = oCase1.id,type='Work Task',  Task_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU', 
                               Work_Queue_View_Name__c='Cincinati CRU', Service_Center__c ='Cincinnati', Department__c='CRU', Open_Work_Task_Count__c=NULL,
                               Total_Work_Task_Count__c=NULL,status='Closed');
            //testTasks.add(oTask2);  
            insert oTask2;  
            
            Database.ExecuteBatch(new HUMCaseTaskListViewController_B ());
        }
    }
    
//@testSetup
    static void setUpTestData(){
    	User oUser = HUMTestDataHelperTest.createTestUser('User12','Customer Care Specialist');
    	oUser.Current_Queue__c = 'Cincinnati CRU';
        HUM_Task_Heirarchy__c humHierarchy = new HUM_Task_Heirarchy__c();
        //insert oUser;
        system.assertEquals(oUser.lastname =='test User12',true);    	
        system.runAs(oUser) {
        	Account oAcc = HUMTestDataHelperTest.getAccount(); 
            oAcc.Name='TestAccount1';
            oAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
            insert oAcc;   
             
            Classification__c classificationRecDec = new Classification__c();
	        classificationRecDec.Name = 'Claims';
	        classificationRecDec.Is_Active__c = true;
	        insert classificationRecDec;
	        system.assertEquals(classificationRecDec.Name == 'Claims',true);
	        
	        Intent__c intentRec = new Intent__c();
	        intentRec.Name = 'Filling';
	        intentRec.Is_Active__c = true;
	        insert intentRec;
	        system.assertEquals(intentRec.name == 'Filling',true);
           
            List<Case> testcases = new List<Case>();
            Case oCase1 = new Case(AccountId  = oAcc.Id, Intent_id__c = intentRec.id, Classification_ID__c = classificationRecDec.id, 
                               Interacting_With_Type__c = 'Group', Interacting_With__c = oAcc.Id, Due_Date__c = Date.Today(),
                               Case_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU', Work_Queue_View_Name__c='Cincinati CRU', Open_Work_Task_Count__c=0,
                               Total_Work_Task_Count__c=NULL,Service_Center__c = 'Cincinnati', Department__c = 'CRU', status = 'In Progress');
            testcases.add(oCase1);
            insert testcases;                        
            system.assertEquals(testcases[0].status == 'In Progress',true);
            
            List<Task> testTasks = new List<Task>();    
            Task oTask1 = new Task(WhatId = oCase1.id,type='Work Task', Task_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU',
                               Work_Queue_View_Name__c='Cincinati CRU', Service_Center__c ='Cincinnati', Department__c='CRU', Open_Work_Task_Count__c=NULL,
                               Total_Work_Task_Count__c=NULL,status='In Progress');
            //testTasks.add(oTask1); 
            insert oTask1;
            Task oTask2 = new Task(WhatId = oCase1.id,type='Work Task',  Task_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU', 
                               Work_Queue_View_Name__c='Cincinati CRU', Service_Center__c ='Cincinnati', Department__c='CRU', Open_Work_Task_Count__c=NULL,
                               Total_Work_Task_Count__c=NULL,status='Closed');
            //testTasks.add(oTask2);  
            insert oTask2;              
         }
    }
	private static void OverAllMainTestMethod(){
        List<Task> lstOfTasksToUpdate = new List<Task>();
         setUpTestData();
        User oUser = [select id, name,lastname from user where lastname='test User12' limit 1];
        system.assertEquals(oUser.lastname =='test User12',true); 
        
        system.runAs(oUser){
               /* Test.startTest();
                HUMCaseTaskListViewController_B job = new HUMCaseTaskListViewController_B();
                Database.executeBatch(job);
                Test.stopTest();*/
           
            Database.ExecuteBatch(new HUMCaseTaskListViewController_B ());
        }
    }     
}