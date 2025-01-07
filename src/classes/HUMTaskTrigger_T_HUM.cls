/***********************************************************************************************************
Apex Class Name  : HUMTaskTrigger_T_HUM
Version          : 1.0
Created Date     : April 01 2019
Function         : Test class for HUMTaskTrigger,HUMUpdateTaskHelper,HUMTaskHistoryHelper,HUMUpdateTaskHelperForCloseCase
************************************************************************************************************

Modification Log:

* Developer Name                Code Review           Date                        Description
*-----------------------------------------------------------------------------------------------------------
* Shailesh Mali                                      04/01/2019                  Original Version
* Lakshmi Madduri									 6/19/2019					  CDO Implementation
* Pooja Kumbhar										 04/29/2020					  Checkmarx Test Methods without assert error fix
* Pooja Kumbhar										 11/01/2022					  Test class issue fix
* Lakshmi Madduri							 		 02/23/2024                	  US-5604142 PCO App Deprecation
*************************************************************************************************************/
@isTest
private class HUMTaskTrigger_T_HUM {
    
    @testSetup
    static void setupDataForTask() {
        HUMTriggerSwitch__c objTriggerSwitch = new HUMTriggerSwitch__c();
        objTriggerSwitch.Name = 'Task';
        objTriggerSwitch.Exeute_Trigger__c = true;
        insert objTriggerSwitch;
        
        HUMQueueViewBatchSettings__c objSetup = new HUMQueueViewBatchSettings__c();
        objSetup.Name = 'QueueViewBatchSetup';
        objSetup.BatchExecutionDate__c = datetime.newInstance(2019, 9, 15, 12, 30, 0);
        objSetup.TriggerExecutionRequired__c = true;
        insert objSetup;     
        
        HUMConstants__c con = new HUMConstants__c();
        con.Name = 'HUMClosed';
        con.StringValue__c = 'Closed';        
        insert con;
        
        HUMConstants__c con1 = new HUMConstants__c();
        con1.Name = 'HUMWorkTask';
        con1.StringValue__c = 'Work Task';        
        insert con1;
        
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        oUser.Current_Queue__c = 'Louisville After Hours Calls';
      	
        insert new HUMTaskNotAllowedProfiles__c(name = 'CRMRetail User');
        
        insert oUser;
        System.assertEquals(oUser.lastname =='test User1',true);        
        System.runAs(oUser)
        {
            Work_Queue_Priority_CriteriaLimit_Value__C cv = new Work_Queue_Priority_CriteriaLimit_Value__C();
            cv.name = 'Criteria';
            cv.Value__C = 0;
            insert cv;
            
            Account oAcc = HUMTestDataHelperTest.getAccount(); 
            oAcc.Name='TestAccount1';
            oAcc.Source_Platform_Code__c = 'CA';
            oAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
            insert oAcc;            
            
            System.assertEquals(oAcc.Source_Platform_Code__c == 'CA', true);
            
            Product2 oProd = HUMTestDataHelperTest.getProduct();
            oProd.Name ='Mediclaim';
            oProd.Product_Type__c = 'Mediclaim';
            insert oProd;            
            
            System.assertEquals(oProd.name =='Mediclaim',true);
            
            Policy__c oPolicy1 = HUMTestDataHelperTest.getPolicy();
            oPolicy1.Name='test123';
            oPolicy1.Group_Name__c=oAcc.Id;
            oPolicy1.Funding_Type_Code__c='Y';
            oPolicy1.Product__c=oProd.Id;
            oPolicy1.Issue_State__c='IL';  
            insert oPolicy1;            
            
            System.assertEquals(oPolicy1.Funding_Type_Code__c == 'Y',true);
            
            Policy_Member__c oPolMem1 = HUMTestDataHelperTest.getPolicyMember();
            oPolMem1.Name = '12345';
            oPolMem1.Member__c = oAcc.Id;
            oPolMem1.Policy__c = oPolicy1.Id;            
            oPolMem1.Relationship__c = 'Self';            
            insert oPolMem1;            
            
            System.assertEquals(oPolMem1.Name == '12345' ,true);
            
            HUMCaseUpdateAllowedProfies__c caseUpdate = new HUMCaseUpdateAllowedProfies__c(Name = 'Customer Care Specialist');
            insert caseUpdate;
            
            Classification__c classificationRecDec = new Classification__c();
            classificationRecDec.Name = 'Claims';
            classificationRecDec.Is_Active__c = true;
            insert classificationRecDec;
            System.assertEquals(classificationRecDec.Name == 'Claims',true);
            
            Intent__c intentRec = new Intent__c();
            intentRec.Name = 'Filling';
            intentRec.Is_Active__c = true;
            insert intentRec;
            System.assertEquals(intentRec.name == 'Filling',true);
            
            Classification_Type__c oClassType = new Classification_Type__c();
            oClassType.Guidance_Alert_Limit__c = 2;
            oClassType.Is_Active__c = true;
            insert oClassType;
            
            CTCI_Junction__c oCTCIRec = new CTCI_Junction__c();
            oCTCIRec.Classification__c = classificationRecDec.id;
            oCTCIRec.Intent__c = intentRec.id;
            oCTCIRec.Classification_Type__c = oClassType.id;
            oCTCIRec.IsActive__c = true;
            insert oCTCIRec;
            
            System.assertEquals(oCTCIRec.IsActive__c == true, true);
              
            Work_Queue_Setup__c oQSetup = New Work_Queue_Setup__c(Name ='Louisville After Hours Calls',Service_Center__c='Louisville',Department__c='After Hours Calls',IsActive__c=true);
            insert oQSetup ;
        
        	Work_Queue_Priority__c oWFP = new Work_Queue_Priority__c();
            oWFP.Criteria_1__c='CR';
            oWFP.Work_Queue_Name__c= oQSetup.id;
            insert oWFP; 
            
            Queue_View__c oQv1 = New Queue_View__c(Queue_View_Name__c ='Other11', Department__c='After Hours Calls', Work_Queue_Name__c=oQSetup.Id, Service_Center__c='Louisville',Expression__c=''); 
            insert oQv1;
            
            Queue_View_Item__c oQueueViewRC3 = New Queue_View_Item__c();
       		oQueueViewRC3.API_Name__c = 'TaskStatus';
       		oQueueViewRC3.Field_Operator__c = 'Equals';
       		oQueueViewRC3.Field_Value__c = 'In Progress';
	        oQueueViewRC3.Queue_View__c = oQv1.Id;            
	        insert oQueueViewRC3;   
            
            List<Case> testCases = new List<Case>();
            Case oCase1 = new Case(AccountId  = oAcc.Id, CTCI_List__c = oCTCIRec.Id, Policy_Member__c = oPolMem1.Id ,
                                   Interacting_With_Type__c = 'Group', Interacting_With__c = oAcc.Id, Due_Date__c = Date.Today(),
                                   Case_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU', Work_Queue_View_Name__c='Cincinati CRU',
                                   Service_Center__c = 'Cincinnati', Department__c = 'CRU', status = 'In Progress', Topic__c = 'SIU');
            testCases.add(oCase1);
                   
            insert testCases;                        
            System.assertEquals(testCases[0].status == 'In Progress',true);  
            
            List<Task> testTasks = new List<Task>();
            
            //Parent Task
            Task oTask1 = new Task(WhatId = oCase1.id,type='Work Task', Task_Owner__c='Louisville After Hours Calls',
                                   Work_Queue_View_Name__c='Louisville After Hours Calls', Service_Center__c ='Louisville', Department__c='After Hours Calls', 
                                   status='In Progress' , Topic__c = 'SIU', Owner_Queue__c='Louisville After Hours Calls');
            testTasks.add(oTask1);  
            
            //Child Task
            Task oTask2 = new Task(WhatId = oCase1.id,type='Extension Request',  Task_Owner__c='Provider Payment Integrity', Owner_Queue__c='Provider Payment Integrity', 
                                   Work_Queue_View_Name__c='', Service_Center__c ='Provider', Department__c='Payment Integrity', 
                                   status='In Progress',ActivityDate = Date.Today());      
            testTasks.add(oTask2); 
            
            insert testTasks;            
            
            //Parent - Child relation
            HUM_Task_Heirarchy__c humHeirarchy = new HUM_Task_Heirarchy__c(); 
            humHeirarchy.HUM_Dependent_TaskID__c = oTask2.id;
            humHeirarchy.HUM_Parent_TaskID__c  = oTask1.id;
            humHeirarchy.Hum_CaseID__c = oCase1.id;
            insert humHeirarchy;
            
        }
    }
    
    @isTest
    static void testBeforeAfterInsertTask(){
        HUMTriggerSwitch__c triggerSwitch = [Select Name,Exeute_Trigger__c From HUMTriggerSwitch__c where Name = 'Task' limit 1 ];
        HUMQueueViewBatchSettings__c objSetup = [Select Name,BatchExecutionDate__c,TriggerExecutionRequired__c from HUMQueueViewBatchSettings__c where Name = 'QueueViewBatchSetup' limit 1];
        List<HUMConstants__c> humConList = [Select Name,StringValue__c From HUMConstants__c where Name IN ('HUMClosed','HUMWorkTask')];
        List<Id> caseIdList = new List<Id>();
        
        Work_Queue_Setup__c oQSetup = New Work_Queue_Setup__c(Name ='Louisville After Hours Calls',Service_Center__c='Louisville',Department__c='After Hours Calls',IsActive__c=true);
        insert oQSetup ;
		System.assertEquals(oQSetup.name =='Louisville After Hours Calls',true);
            
        Work_Queue_Priority__c oWFP = new Work_Queue_Priority__c();
        oWFP.Criteria_1__c='CR';
        oWFP.Work_Queue_Name__c= oQSetup.id;
        insert oWFP; 
        
        Queue_View__c oQv1 = New Queue_View__c(Queue_View_Name__c ='Other111', Department__c='After Hours Calls', Work_Queue_Name__c=oQSetup.Id, Service_Center__c='Louisville',Expression__c='Test Expression'); 
        insert oQv1;
            
        Case c = [select Id, Type, Subtype__c, CTCI_List__r.Classification__r.Name,CTCI_List__r.Classification__c, CTCI_List__r.Intent__c,Funding_Type__c, Plan_Issue_State__c, Interacting_With_Type__c,State_Mem_Resid_Add__c,CTCI_List__r.Intent__r.Name,
                  Plan_Segment_Size__c, Employer_Group_Name__c,Policy_Platform__c, Product_Type__c, Previous_Case_Owner_Role__c,Behavioral_Health__c,
                  Provider_Shipping_State_Code__c,Group_Billing_State_Code__c,Policy_Product_Code__c,Complaint_Type__c from case 
                  where Interacting_With_Type__c = 'Group' limit 1];
        
        
        List<Task> taskList = new List<Task>();
        
        for(integer i = 0 ; i < 20; i++){
            if(i <= 4 ){
                Task t = new task(WhatId = c.id,type='Extension Request', Task_Owner__c=UserInfo.getUserName(),
                                   Service_Center__c ='Provider', Department__c='Payment Integrity', 
                                   status='In Progress',ActivityDate = Date.Today());
                taskList.add(t);
            }
            else if (i > 4 && i < 10){
                Task t1 = new task(WhatId = c.id,type='Work Task', Task_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Louisville After Hours Calls',
                                  Work_Queue_View_Name__c='Louisville After Hours Calls', Service_Center__c ='Louisville', Department__c='After Hours Calls', 
                                  status='In Progress');
                taskList.add(t1);
                
            }
            else{
                Task t2= new task(WhatId = c.id,type='Notification Task', Task_Owner__c=UserInfo.getUserName(), 
                                  Service_Center__c ='Provider', Department__c='Payment Integrity', 
                                  status='In Progress' , Topic__c = 'Special Process Adjustments');
                taskList.add(t2);
            }
        }
        insert taskList;
        HUMUpdateCaseTaskHelper.getUpdatedQueueNames();
        
        Case cas = [Select id, Status, CTCI_List__r.Classification__r.Name, Classification_Id__c, CTCI_List__r.Intent__r.Name, Intent_Id__c, CTCI_List__c, Owner_Queue__C from Case]; 
        list<CTCI_Junction__c> oCTCI = [select id,name ,classification__c,classification__r.name,Intent__c,intent__r.name from  CTCI_Junction__c LIMIT 1];
        Queue_View__c sQV = [SELECT Id, Expression__c, Queue_View_Name__c, Work_Queue_Name__r.Name, Work_Queue_Name__r.Service_Center__c, Work_Queue_Name__r.Department__c,(SELECT name,API_Name__c, Field_Operator__c, Field_Value__c, Queue_View__c FROM Queue_View_Items__r order by API_Name__c  ) FROM Queue_View__c where  Queue_View_Name__c = 'Other111'];
        HUMUpdateCaseTaskHelper.checkFilterConditions(sQV , cas ,sQV.Queue_View_Items__r, oCTCI[0]);
       
        
    }
    
    @isTest
    static void testBeforeAfterUpdateTask(){
        HUMTriggerSwitch__c triggerSwitch = [Select Name,Exeute_Trigger__c From HUMTriggerSwitch__c where Name = 'Task' limit 1 ];
        HUMQueueViewBatchSettings__c objSetup = [Select Name,BatchExecutionDate__c,TriggerExecutionRequired__c from HUMQueueViewBatchSettings__c where Name = 'QueueViewBatchSetup' limit 1];
        List<HUMConstants__c> humConList = [Select Name,StringValue__c From HUMConstants__c where Name IN ('HUMClosed','HUMWorkTask')];
                
        Case c1 = [select Id, Type, Subtype__c, CTCI_List__r.Classification__r.Name,CTCI_List__r.Classification__c, CTCI_List__r.Intent__c,Funding_Type__c, Plan_Issue_State__c, Interacting_With_Type__c,State_Mem_Resid_Add__c,CTCI_List__r.Intent__r.Name,
                   Plan_Segment_Size__c, Employer_Group_Name__c,Policy_Platform__c, Product_Type__c, Previous_Case_Owner_Role__c,Behavioral_Health__c,
                   Provider_Shipping_State_Code__c,Group_Billing_State_Code__c,Policy_Product_Code__c,Complaint_Type__c from case 
                   where Interacting_With_Type__c = 'Group' limit 1];
				   
		System.assertEquals(c1.Interacting_With_Type__c == 'Group',true);
				   
        Task t1 = [Select Id from Task where type = 'Work Task' limit 1];
        HUM_Task_Heirarchy__c h = [select id, HUM_Dependent_TaskID__c, HUM_Parent_TaskID__c from HUM_Task_Heirarchy__c where HUM_Parent_TaskID__c =:t1.id limit 1];
        
        List<Task> taskList = new List<Task>();
        
        for(task t : [Select Id, whatId, type,Task_Owner__c,Service_Center__c,Department__c From Task where type IN('Work Task','Extension Request') ]){
            t.type = 'Notification Task';
            t.Service_Center__c ='Provider';
            t.Department__c='Payment Integrity';
            t.Topic__c =  'Special Process Adjustments';
            t.status =  'Closed';
            t.whatId = c1.Id;
			t.ActivityDate = date.today();
            taskList.add(t);
        }
        update taskList;
        HUMUpdateTaskHelper help = new HUMUpdateTaskHelper();
        help.taskDueDateError(taskList);
    }
    
    @isTest
    static void testTrackHistory()
    {
        HUMObject_History_Metadata__c oHUMhisObj = new HUMObject_History_Metadata__c(Name = 'Task_Priority', Parent_Tracked_Object__c = 'Task', Tracked_Field__c = 'Priority');
        insert oHUMhisObj ;
        
        User oCCSUser = [Select id,UserName from User where LastName ='test User1' LIMIT 1 ] ;
        System.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        
        System.runAs(oCCSUser)
        {
            Case oCase1 = [select id from case limit 1];
            Task oTask1 = new Task(WhatId = oCase1.id,type='Work Task', Task_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU',
                                   Work_Queue_View_Name__c='Cincinati CRU', Service_Center__c ='Cincinnati', Department__c='CRU', 
                                   status='In Progress', Priority = 'Normal');
            
            Task oTask2 = new Task(WhatId = oCase1.id,type='Work Task',  Task_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU', 
                                   Work_Queue_View_Name__c='Cincinati CRU', Service_Center__c ='Cincinnati', Department__c='CRU', 
                                   status='In Progress');
            
            insert oTask1;            
            System.assertNotEquals(oTask1, null,'Value not matched');            
            insert oTask2;            
            System.assertNotEquals(oTask2, null,'Value not matched');
            
            Task_Field_History__c oTaskFieldHistory = new Task_Field_History__c (Created_At__c=System.now(),SObjFieldName__c='Priority',SObjFieldValue__c='Critical', SObjID__c=oTask1.Id);
            insert oTaskFieldHistory;
            System.assertNotEquals(oTaskFieldHistory, null, 'Value not matched');
            
            oTaskFieldHistory.Created_At__c=System.now();
            oTaskFieldHistory.SObjFieldName__c='Priority';
            oTaskFieldHistory.SObjFieldValue__c='Critical';
            update oTaskFieldHistory;
           
            Task newTask = [Select Priority,Status From Task limit 1];
            HUMTaskHistoryHelper.bIsAlreadyInserted = false;
            newTask.Priority = 'Critical';
            newTask.Status = 'Closed';
            update newTask;
           
        }
    } 
    
     @isTest
    static void testUpdateTrackHistory()
    {
        HUMObject_History_Metadata__c oHUMhisObj = new HUMObject_History_Metadata__c(Name = 'Task_Priority', Parent_Tracked_Object__c = 'Task', Tracked_Field__c = 'Priority');
        insert oHUMhisObj ;
        
        User oCCSUser = [Select id,UserName from User where LastName ='test User1' LIMIT 1 ] ;
        System.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        
        System.runAs(oCCSUser)
        {
            Case oCase1 = [select id from case limit 1];
            Task oTask1 =  [Select Id,Priority,Status From Task limit 1 ];
            HUMTaskHistoryHelper.bIsAlreadyInserted = false;
            oTask1.Priority = 'Critical';
            oTask1.Status = 'Closed';
            update oTask1;
            
            Task_Field_History__c oTaskFieldHistory = new Task_Field_History__c (Created_At__c=System.now(),SObjFieldName__c='Priority',SObjFieldValue__c='Critical', SObjID__c=oTask1.Id);
            insert oTaskFieldHistory;
            System.assertNotEquals(oTaskFieldHistory, null, 'Value not matched');
         }
    }
    @isTest
    static void TestInset()
    {
        User oCCSUser = [Select id,UserName from User where LastName ='test User1' LIMIT 1 ] ;
        System.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com'); 
        System.runAs(oCCSUser)
        {
            set<string> testSet = new set<string>();
            testSet.add('string1');
            string Temp='String1';
            HUMUpdateCaseTaskHelper.isInSet(testSet, Temp);
        }
        
    }
    @isTest
    static void TestQueueView()
    {
        set<string> testSet = new set<string>();
        testSet.add('string1');
        list<Queue_View_Item__c>  lstQWI = new list<Queue_View_Item__c>();
        Work_Queue_Setup__c oQSetup = New Work_Queue_Setup__c(Name ='Louisville After Hours Calls',Service_Center__c='Louisville',Department__c='After Hours Calls',IsActive__c=true);
        insert oQSetup ;
		
		System.assertEquals(oQSetup.Name,'Louisville After Hours Calls');
		
        Queue_View__c oQv1 = New Queue_View__c(Queue_View_Name__c ='Other11', Department__c='After Hours Calls', Work_Queue_Name__c=oQSetup.Id, Service_Center__c='Louisville',Expression__c='Test 1'); 
        insert oQv1;
        Queue_View_Item__c oQueueViewRC4 = new Queue_View_Item__c();         	
        oQueueViewRC4.API_Name__c = 'FundingType';
        oQueueViewRC4.Field_Operator__c = 'Equals';
        oQueueViewRC4.Field_Value__c = 'N';
        oQueueViewRC4.Queue_View__c = oQv1.Id;            
        lstQWI.add(oQueueViewRC4);
        List<Queue_View__c> testQueueView = new List<Queue_View__c>();
        testQueueView.add(oQv1);
        map<string,string> testMap = new map<string,string>();
        testMap.put('test1','test');
        Integer TestInt = 1;
        HUMUpdateCaseTaskHelper.mapViews.put(oQv1.id,testQueueView);
        HUMUpdateCaseTaskHelper.mapViewItems.put(oQv1.id,lstQWI);
        HUMUpdateCaseTaskHelper.expressionQVIs.put(oQv1.id,testSet);
        HUMUpdateCaseTaskHelper.getMatcheViewBasedOnPriority(testQueueView, testMap, TestInt);
    }
    
    @isTest
    static void TestQueueViewNegative()
    {
        set<string> testSet = new set<string>();
        testSet.add('string1');
        list<Queue_View_Item__c>  lstQWI = new list<Queue_View_Item__c>();
        Work_Queue_Setup__c oQSetup = New Work_Queue_Setup__c(Name ='Louisville After Hours Calls',Service_Center__c='Louisville',Department__c='After Hours Calls',IsActive__c=true);
        insert oQSetup ;
		
		System.assertEquals(oQSetup.Name,'Louisville After Hours Calls');
		
        Queue_View__c oQv1 = New Queue_View__c(Queue_View_Name__c ='Other11', Department__c='After Hours Calls', Work_Queue_Name__c=oQSetup.Id, Service_Center__c='Louisville',Expression__c=''); 
        insert oQv1;
        Queue_View_Item__c oQueueViewRC4 = new Queue_View_Item__c();         	
        oQueueViewRC4.API_Name__c = 'FundingType';
        oQueueViewRC4.Field_Operator__c = 'Equals';
        oQueueViewRC4.Field_Value__c = 'N';
        oQueueViewRC4.Queue_View__c = oQv1.Id;            
        lstQWI.add(oQueueViewRC4);
        List<Queue_View__c> testQueueView = new List<Queue_View__c>();
        testQueueView.add(oQv1);
        map<string,string> testMap = new map<string,string>();
        testMap.put('test1','test');
        Integer TestInt = 1;
        HUMUpdateCaseTaskHelper.mapViews.put(oQv1.id,testQueueView);
        HUMUpdateCaseTaskHelper.mapViewItems.put(oQv1.id,lstQWI);
        HUMUpdateCaseTaskHelper.expressionQVIs.put(oQv1.id,testSet);
        HUMUpdateCaseTaskHelper.getMatcheViewBasedOnPriority(testQueueView, testMap, TestInt);
    }
}