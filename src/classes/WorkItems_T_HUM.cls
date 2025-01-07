/******************************************************************************************************************
Apex class Name    : WorkItems_T_HUM
Version            : 1.0
Function           : Test Class for WorkItems_C_HUM , WorkItems_DTO_HUM
Created On         : September 19 2016

Modification Log:
*   Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------                
*   Vishal Verma			       40074          09/19/2016          Original Version -> REQ-244451 : Due items on Home Tab 
*   Vishal Verma					              10/13/2016          Defect 243988.0001 : Replaced Due_Date__c to Follow_up_date__c on Case  
*   Vamsi Kondragunta                             12/13/2016          Defect fix for 252935.0001
*	Pradeepkumar Dani			   99570		  06/08/2017          Intent__c.Classification__c & Intent__c.Etl_Idtfr__c Clean up
*	Pradeepkumar Dani			  100996 		  06/13/2017		  Classification__c.Classification_Type__c & Etl_Idftr__c Clean Up
*   Karthik Varma N R                			  07/23/2021          User Story 2503627: T1PRJ0001827 - IR 5050 - Tech Debt - Clean up switch for past releases(CRM)
******************************************************************************************************************/
@isTest
private class WorkItems_T_HUM
{
	/**
    * Test Setup method to create all the data required for testing.
    *
    * @param     NULL
    * @return    void 
    */
	 @testSetup	 static void setUpTestData()
	 {
	 	// Create User 1 and insert Case and Task as USER 1
	 	User oCCSUser1 = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser1;
        system.assert(oCCSUser1.LastName == 'test User1');
        
        // Create User 2 and no Case and TASK records will be created . This user will be used to Test no Due Items. 
        User oCCSUser2 = HUMTestDataHelperTest.createTestUser('User2','Customer Care Specialist');
        insert oCCSUser2;
        system.assert(oCCSUser2.LastName == 'test User2');
        
        system.runAs(oCCSUser1)
        {
            Account oAccount = new Account();
            oAccount.Name = 'New Case Account';
            oAccount.Group_Number__c  = '123';
            oAccount.BillingState = 'New York';
            oAccount.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
            insert oAccount;
            system.assert(oAccount.Name == 'New Case Account');
            
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
			System.assertEquals(intentRec.Name, 'Filling');

	        List<Case> lstCase = new List<Case>();	        
	        for(integer i = 0 ; i<4 ;i++) // Insert 2 Over Due Cases and 2 Near Due Cases
	        {
	        	Integer j = i==0 || i==1 ? 0 :2;
	        	lstCase.add(new Case(Follow_Up_Due_Date__c= system.today()+j , AccountId=oAccount.Id, Intent_Id__c = lstIntent[0].id,General_Case__c = true, Classification_Id__c =lstClassification[0].id,Interacting_With_Type__c = 'Group',Interacting_With__c=oAccount.Id, Case_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU1', Work_Queue_View_Name__c='Cincinati CRU1',Service_Center__c ='Cincinnati',Department__c='CRU1', status='In Progress'));
	        }
	        
	        if(!lstCase.isEmpty()) insert lstCase;
	        system.assert(lstCase.size() == 4);
	        
	        List<Task> lstTask = new  List<Task>();
	        for(Case cs:lstCase) // Insert 2 Over Due Task and 2 Near Due Task
	        {
	        	lstTask.add(new Task(WhatId = cs.id, Task_Owner__c = UserInfo.getUserName(), QueueOrUserId__c= UserInfo.getUserid(),Owner_Queue__c = 'Cincinnati CRU',Work_Queue_View_Name__c = 'Cincinati CRU', Service_Center__c = 'Cincinnati', Department__c = 'CRU', status = 'In Progress' ,Description = 'Hi Comment' , ActivityDate = cs.Follow_Up_Due_Date__c));
	        }
	        
            if(!lstTask.isEmpty()) insert lstTask;  
            system.assert(lstTask.size() == 4);
        }
	 }
	 
	/**
    * This method is used to Test when there are no Over as well as no Near Work Items
    *
    * @param     NULL
    * @return    void 
    */
	static testMethod void testNoWorkItems()
	{
		User testUser = [Select Id,Name from User where LastName='test User2'];
        system.runAs(testUser)
        {
        	Test.StartTest();
        	
	        	WorkItems_C_HUM workItem =  new WorkItems_C_HUM();
	        	workItem.populateWorkItems();
	        	system.assert(workItem.sMessage == 'You have no due work items.');
	        	system.assert( workItem.sErrorMessage == '');
	        	
        	Test.StopTest();
    	}
	}
	
	/**
    * This method is used to Test when there are more than 5 Over Due Work Items and less than 5 Near Due Work Items
    *
    * @param     NULL
    * @return    void 
    */ 
	static testMethod void testOverDueWorkItems()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        system.runAs(testUser)
        {
        	createNewCaseTask(0); // Creat new Case and Task as Over Due Items
        	
        	Test.StartTest();
        	
	        	WorkItems_C_HUM workItem =  new WorkItems_C_HUM();
	        	workItem.populateWorkItems();
	        	system.assert(workItem.sMessage == '');
	        	system.assert( workItem.sErrorMessage == 'ATTENTION: Only Top 5 Over Due work is listed below.  Please go to the Cases/Tasks tab for the full list of over due work items.');
	        	
        	Test.StopTest();
    	}
    }
     
   /**
    * This method is used to Test when there are less than 5 Over Due Work Items and more than 5 Near Due Work Items
    *
    * @param     NULL
    * @return    void 
    */ 
    static testMethod void testNearDueWork()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        system.runAs(testUser)
        {
        	createNewCaseTask(2); // Creat new Case and Task as Near Due Items
        	
        	Test.StartTest();
        	
	        	WorkItems_C_HUM workItem =  new WorkItems_C_HUM();
	        	workItem.populateWorkItems();
	        	system.assert(workItem.sMessage == '');
	        	system.assert( workItem.sErrorMessage == 'ATTENTION:  Only Top 5 Nearing Due work is listed below.  Please go to the Cases/Tasks tab for the full list of nearing due work items.');
        	Test.StopTest();
        }
    }
    
    /**
    * This method is used to Test when there are more than 5 Over Due Work Items and more than 5 Near Due Work Items
    *
    * @param     NULL
    * @return    void 
    */  
    static testMethod void testOverANDNearDueWork()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        system.runAs(testUser)
        {
        	createNewCaseTask(0); // Creat new Case and Task as Over Due Items
        	createNewCaseTask(2); // Creat new Case and Task as Near Due Items
        	
        	Test.StartTest();
        	
	        	WorkItems_C_HUM workItem =  new WorkItems_C_HUM();
	        	workItem.populateWorkItems();
	        	system.assert(workItem.sMessage == '');
	        	system.assert( workItem.sErrorMessage == 'ATTENTION: Only Top 5 Over Due/Nearing Due work is listed below.  Please go to the Cases/Tasks tab for the full list of over due and nearing due work items.');
        	Test.StopTest();
     	}
	}
    
    /**
    * This method is used to Test refresh functionality
    *
    * @param     NULL
    * @return    void 
    */ 
    static testMethod void testRefresh()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        system.runAs(testUser)
        {
        	WorkItems_C_HUM workItem =  new WorkItems_C_HUM();
        	workItem.populateWorkItems();
        	system.assert(workItem.sMessage == '');
	        system.assert( workItem.sErrorMessage == '');
	        	
        	createNewCaseTask(0); // Creat new Case and Task as Over Due Items
        	createNewCaseTask(1); // Creat new Case and Task as Near Due Items
        	
        	Test.StartTest();
        	
	        	workItem =  new WorkItems_C_HUM();
	        	workItem.populateWorkItems();
	        	system.assert(workItem.sMessage == '');
	        	system.assert( workItem.sErrorMessage == 'ATTENTION: Only Top 5 Over Due/Nearing Due work is listed below.  Please go to the Cases/Tasks tab for the full list of over due and nearing due work items.');
	        	
        	Test.StopTest();
        }
	}
     
    /**
    * This method is used to Test when there are less than 5 Over Due Work Items and less than 5 Near Due Work Items
    *
    * @param     NULL
    * @return    void 
    */ 
	static testMethod void testNoDueWork()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        system.runAs(testUser)
        {
        	Test.StartTest();
        	
	        	WorkItems_C_HUM workItem =  new WorkItems_C_HUM();
	        	workItem.populateWorkItems();
	        	system.assert(workItem.sMessage == '');
	        	system.assert( workItem.sErrorMessage == '');
	        	
        	Test.StopTest();
    	}
	}
    
    /**
    * This method is used to create case and task record from test methods to test various scenarios
    *
    * @param     NULL
    * @return    void 
    */ 
    static void createNewCaseTask(Integer j)
    {
     	Account oAccount = [Select Id from Account limit 1];
     	List<Classification__c> lstClassification = [Select id from Classification__c limit 1];
     	list<Intent__c> lstIntent = [Select id from Intent__c limit 1];
     	Case cs = new Case(Follow_Up_Due_Date__c= system.today()+j , AccountId=oAccount.Id, Intent_Id__c = lstIntent[0].id,General_Case__c = true, Classification_Id__c =lstClassification[0].id,Interacting_With_Type__c = 'Group',Interacting_With__c=oAccount.Id, Case_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU1', Work_Queue_View_Name__c='Cincinati CRU1',Service_Center__c ='Cincinnati',Department__c='CRU1', status='In Progress');
     	insert cs;
     	system.assert(cs.Follow_Up_Due_Date__c == system.today()+j);
     	Task tsk = new Task(WhatId = cs.id, Task_Owner__c = UserInfo.getUserName(), QueueOrUserId__c= UserInfo.getUserid(),Owner_Queue__c = 'Cincinnati CRU',Work_Queue_View_Name__c = 'Cincinati CRU', Service_Center__c = 'Cincinnati', Department__c = 'CRU', status = 'In Progress' ,Description = 'Hi Comment' , ActivityDate = cs.Follow_Up_Due_Date__c);
     	insert tsk;
     	system.assert(tsk.ActivityDate == system.today()+j);
	}
}