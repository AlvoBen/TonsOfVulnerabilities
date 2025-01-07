/******************************************************************************************************************
Apex Class Name :   WorkItems_LC_HUMTest
Version: 1.0
Function           : Test Class for WorkItems_LC_HUM
Created On         : February 16 2023

Modification Log:  
*   Developer                   Code Review         Date                Description
* ----------------------------------------------------------------------------------------------------------------- 
* Mayur Pardeshi								16/02/2023		Original Version->REQ-4129681 Lightning - Today's Task (My Due Work)
*******************************************************************************************************************/

@isTest
Private Class WorkItems_LC_HUMTest
{
 @testSetup	 static void setUpTestData()
	 { 
          
	 	User oCCSUser1 = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser1;
        Assert.areEqual('test User1', oCCSUser1.LastName);
        
      
        User oCCSUser2 = HUMTestDataHelperTest.createTestUser('User2','Customer Care Specialist');
        insert oCCSUser2;
		Assert.areEqual('test User2', oCCSUser2.LastName);
         
        system.runAs(oCCSUser1)
        {
            Account oAccount = New Account();
            oAccount.Name = 'New Case Account';
            oAccount.Group_Number__c  = '123';
            oAccount.BillingState = 'New York';
            oAccount.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
            insert oAccount;
            Assert.areEqual('New Case Account', oAccount.Name);
            
            List<Classification__c> lstClassification = New List<Classification__c>();
	        Classification__c classificationRec = New Classification__c();
	        classificationRec.Name = 'Claims';
	        classificationRec.Is_Active__c = true;
	        lstClassification.add(classificationRec);  
	        insert lstClassification;
	        Assert.isTrue(lstClassification[0].Name=='Claims');
	        
	        List<Intent__c> lstIntent = New List<Intent__c>();
	        Intent__c intentRec = New Intent__c();
	        intentRec.Name = 'Filling';
	        intentRec.Is_Active__c = true;
	        lstIntent.add(intentRec);
	        insert lstIntent;
            Assert.areEqual('Filling', intentRec.Name);

	        List<Case> lstCase = New List<Case>();	        
	        For (Integer i = 0 ; i<4 ;i++)
	        {
	        	Integer j = i < 2 ? 0 :2;
	        	lstCase.add(New Case(Follow_Up_Due_Date__c= system.today()+j , AccountId=oAccount.Id, Intent_Id__c = lstIntent[0].id,General_Case__c = true, Classification_Id__c =lstClassification[0].id,Interacting_With_Type__c = 'Group',Interacting_With__c=oAccount.Id, Case_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU1', Work_Queue_View_Name__c='Cincinati CRU1',Service_Center__c ='Cincinnati',Department__c='CRU1', status='In Progress'));
	        }
	        
	        If (!lstCase.isEmpty()) insert lstCase;
            Assert.isTrue(lstCase.size() == 4);
	        
	        List<Task> lstTask = New  List<Task>();
	        For (Case cs:lstCase) 
	        {
	        	lstTask.add(New Task(WhatId = cs.id, Task_Owner__c = UserInfo.getUserName(), QueueOrUserId__c= UserInfo.getUserid(),Owner_Queue__c = 'Cincinnati CRU',Work_Queue_View_Name__c = 'Cincinati CRU', Service_Center__c = 'Cincinnati', Department__c = 'CRU', status = 'In Progress' ,Description = 'Hi Comment' , ActivityDate = cs.Follow_Up_Due_Date__c));
	        }
	        
            If (!lstTask.isEmpty()) insert lstTask;  
            Assert.isTrue(lstTask.size() == 4);
          
            List<CaseComment> caseCommentLst= New List<CaseComment>();
            Integer commentCount = 0;
            For (Case c:[select id from Case]){
                 CaseComment ccObj = New CaseComment();
            ccObj.CommentBody = '(TEST) Comment '+commentCount;
            ccObj.ParentId = c.Id;
                caseCommentLst.add(ccObj);
                commentCount++;
            }
           
            insert caseCommentLst;
            Assert.areEqual(caseCommentLst[0].CommentBody, '(TEST) Comment 0');
        }
	 }
	 
	
	
    @isTest
	Static void testOverDueWorkItems()
    {
        User testUser = [Select Id, Name from User where LastName='test User1'];
        system.runAs(testUser)
        {
        	createNewCaseTask(0);
        	
        	Test.StartTest();
            WorkItems_LC_HUM obj = New WorkItems_LC_HUM();
        	WorkItems_LC_HUM.populateWorkItems();
            Assert.areEqual(6,WorkItems_LC_HUM.iOverDueCount);
        	Test.StopTest();
           
    	}           
    }
      
      @isTest
    Static void testNearDueWork()
    {
        User testUser = [Select Id, Name from User where LastName='test User1'];
        system.runAs(testUser)
        {
        	createNewCaseTask(2);
        	
        	Test.StartTest();
            WorkItems_LC_HUM obj = New WorkItems_LC_HUM();
          	WorkItems_LC_HUM.populateWorkItems();
            Assert.areEqual(6,WorkItems_LC_HUM.iNearDueCount);
        	Test.StopTest();
        }  
    }
    
    Static void createNewCaseTask(Integer j)
    {
     	Account oAccount = [Select Id from Account limit 1];
     	List<Classification__c> lstClassification = [Select id from Classification__c limit 1];
     	list<Intent__c> lstIntent = [Select id from Intent__c limit 1];
     	Case cs = New Case(Follow_Up_Due_Date__c = system.today() + j, AccountId = oAccount.Id, Intent_Id__c = lstIntent[0].id, General_Case__c = True, Classification_Id__c = lstClassification[0].id, Interacting_With_Type__c = 'Group',Interacting_With__c=oAccount.Id, Case_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU1', Work_Queue_View_Name__c='Cincinati CRU1',Service_Center__c ='Cincinnati',Department__c='CRU1', status='In Progress');
        insert cs;
		Assert.isTrue(cs.Follow_Up_Due_Date__c == system.today()+j);
        
          CaseComment ccObj1 = New CaseComment();
            ccObj1.CommentBody = '(TEST) Comment';
            ccObj1.ParentId = cs.Id;
            insert ccObj1;
            Assert.areEqual(ccObj1.CommentBody, '(TEST) Comment');
        
        
     	Task tsk = New Task(WhatId = cs.id, Task_Owner__c = UserInfo.getUserName(), QueueOrUserId__c = UserInfo.getUserid(), Owner_Queue__c = 'Cincinnati CRU',Work_Queue_View_Name__c = 'Cincinati CRU', Service_Center__c = 'Cincinnati', Department__c = 'CRU', status = 'In Progress' ,Description = 'Hi Comment' , ActivityDate = cs.Follow_Up_Due_Date__c);
        insert tsk;
     	Assert.isTrue(tsk.ActivityDate == system.today()+j);
          
	}

}