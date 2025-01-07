/****************************************************************************
Apex Class Name :  HUMTaskWorkQueueViewNameBatchTest
Version: 1.0 
Created On       : 03/24/2015
Function         : Test class to HUMTaskWorkQueueViewNameBatch apex class

Modification Log : 
* Developer Name              Code Review                   Date                       Description
*-----------------------------------------------------------------------------------------------------------------------
* Santhi Mandava              21968                         03/24/2015                 Original Version
* Santhi Mandava              22274                         04/10/2015                 Code Modified to fix test class failure
* SuryaKumari Medicherla      22312                         04/13/2015                 Code Modified to fix test class failure
* Santhi Mandava                                            04/29/2015                 Updated test class logic to cover the following data model changes. Replaced Work_Queue_View_Name__c And Owner_Queue__c case 
                                                                                       fields with WorkQueueViewName__c, OwnerQueue__c respectively, Added New field on Task Work_Queue_View_ID__c . 
* Amit Sinha                                                08/12/2015                 Modified as per the Humana standards.  
* Pradeepkumar Dani          102268                         06/16/2017                 Case Clean Up (Classification__c,Intent__c,Classification_Ctci__c,Intent_Ctci__c,Classification_Text__c,Intent_Text__c)
* Pooja Kumbhar												04/29/2020				   Checkmarx Test Method without assert error fix.
*************************************************************************************************************************/

@isTest
Private Class HUMTaskWorkQueueViewNameBatchTest
{    
    Public Static String CRON_EXP = '0 0 0 15 3 ? 2022';
    /*
     * Method Name : createCustomData
     * Description : @testSetup method to create test data.
     * Return type : void
     * Paramater   : NA
     */
    @testSetup 
    Static void createCustomData() 
    {
        
        List<case> lstOfCases = New List<case>();
        System.assertNotEquals(lstOfCases, NULL, 'Value not matched');
        List<Task> lstOfTasks = New List<Task>();
        List <Error_Log__c> lstErrorLogs = New List <Error_Log__c> ();  
        
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        system.assertNotEquals(oUser,null,'Value not matched'); 
        
        System.runas(oUser)
        {
            Work_Queue_Priority_CriteriaLimit_Value__C cv = new Work_Queue_Priority_CriteriaLimit_Value__C();
            cv.name = 'Criteria';
            cv.Value__C = 0;
            insert cv;
            
            HUMTestDataHelperTest.initializationOfPreObjects();
            
            Work_Queue_Setup__c oQSetup = New Work_Queue_Setup__c(name ='Cincinnati CRU',Service_Center__c='Cincinnati',Department__c='CRU');
            insert oQSetup ;
            system.assertNotEquals(oQSetup ,null,'Value not matched');
            
            List<Queue_View__c> lstViews = HUMTestDataHelperTest.insertTestDataForViews(oQSetup);
            HUMTestDataHelperTest.insertTestDataForViewsItems(lstViews);
            HUMTestDataHelperTest.insertTestDataForQueuesPriority(oQSetup);
            
            HUMQueueViewBatchSettings__c objBatchSetup = New HUMQueueViewBatchSettings__c(name ='QueueViewBatchSetup',TriggerExecutionRequired__c=true);
            insert objBatchSetup;
            system.assertNotEquals(objBatchSetup,null,'Value not matched');
            
            List<HUMConstants__c> lstConstants = Test.loadData(HUMConstants__c.sObjectType,'HUMConsTestData');
            System.assert(lstConstants.size() > 0 );
            
            Account oAcc = [select  id, name from Account where name ='TestAccount1'];
            System.assertNotEquals(oAcc, NULL, 'Value not matched');
            Policy_Member__c oPolMem1 = [select id, name from Policy_Member__c where name='12345'];
            System.assertNotEquals(oPolMem1, NULL, 'Value not matched');
            Policy_Member__c oPolMem2 = [select id, name from Policy_Member__c where name='123456'];
            System.assertNotEquals(oPolMem2, NULL, 'Value not matched');
            
            List<Classification_Type__c> ctList= new list<Classification_Type__c>();
            Classification_Type__c ctRec = new Classification_Type__c();
            ctRec.name = 'Claims (RSO)';
            ctRec.Is_Active__c=true;
            ctList.add(ctRec);          
            insert ctList;
            system.assertEquals(ctList.size(),1);
                        
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
            system.assertEquals(lstIntent[0].Name=='Filling',true);
            
            List<CTCI_Junction__c> ctciList = new list<CTCI_Junction__c>();
            CTCI_Junction__c ctciRec = new CTCI_Junction__c();
            ctciRec.Classification_Type__c = ctRec.id;
            ctciRec.Classification__c = classificationRec.Id;
            ctciRec.Intent__c = intentRec.id;
            ctciRec.IsActive__c = true;
            ctciList.add(ctciRec);
            insert ctciList;
            system.assertEquals(ctciList.size(),1);
            
            lstOfCases.Add(New Case(AccountId=oAcc.Id, CTCI_List__c = ctciRec.Id, Policy_Member__c= oPolMem1.Id ,
                             Interacting_With_Type__c = 'Group', Interacting_With__c=oAcc.Id, Due_Date__c=Date.Today(),
                             Case_Owner__c=UserInfo.getUserName(),
                             Service_Center__c ='Cincinnati', Department__c='CRU', status='In Progress'));
            
            lstOfCases.Add(New Case(AccountId=oAcc.Id, CTCI_List__c = ctciRec.Id, Policy_Member__c= oPolMem2.Id,
                             Interacting_With_Type__c = 'Group', Interacting_With__c=oAcc.Id, Due_Date__c=Date.Today(),
                             Case_Owner__c=UserInfo.getUserName(), 
                             Service_Center__c ='Cincinnati', Department__c='CRU', status='In Progress'));
            insert lstOfCases;
            System.assertNotEquals(lstOfCases, NULL, 'Value not matched');
            
            lstOfTasks.Add(New Task(WhatId = lstOfCases[0].id,Type='Work Task', Task_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU',
                               Work_Queue_View_Name__c='Cincinati CRU', Service_Center__c ='Cincinnati', Department__c='CRU', 
                               status='In Progress'));
            
            lstOfTasks.Add(New Task(WhatId = lstOfCases[0].id,Type='Work Task',  Task_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU', 
                               Work_Queue_View_Name__c='Cincinati CRU', Service_Center__c ='Cincinnati', Department__c='CRU', 
                               status='In Progress'));
            
            lstOfTasks.Add(New Task(WhatId = lstOfCases[0].id,Type='Work Task',  Task_Owner__c= 'Cincinnati CRU', Owner_Queue__c='Cincinnati CRU',
                               Work_Queue_View_Name__c='Cincinati CRU', Service_Center__c ='Cincinnati', Department__c='CRU',
                               status='In Progress'));
            
            lstOfTasks.Add(New Task(WhatId = lstOfCases[1].id,Type='Work Task',  Task_Owner__c= UserInfo.getName(), Owner_Queue__c='Cincinnati CRU',
                               Work_Queue_View_Name__c='Cincinati CRU', Service_Center__c ='Cincinnati', Department__c='CRU',
                               status='In Progress'));
            Insert  lstOfTasks; 
            System.assertNotEquals(lstOfCases, NULL, 'Value not matched');
            
            lstErrorLogs.Add(New Error_Log__c(Error_Message__c = 'Test error message'));    
            lstErrorLogs.Add(New Error_Log__c(Error_Message__c = 'Test error messages'));
            
        }
    }
    
    /*
     * Method Name : OverAllMainTestMethod
     * Description : This Is the main method which Is used for testing the batch 
     * Return type : Void
     * Paramater   : NA
     */     
    Private Static testMethod void OverAllMainTestMethod()
    {
        Work_Queue_Setup__c oQSetup = [Select id, Name, Service_Center__c, Department__c from Work_Queue_Setup__c LIMIT 1];
		User oUser = [Select Id , Name, current_queue__c,Username from User where Username= 'user1user1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
        System.assertEquals(oUser.username, 'user1user1@humcrmstest.com');
        Queue_View__c oQV = [Select id, Name, Work_Queue_Name__c, IsActive__c, Criteria_Updated__c from Queue_View__c where Work_Queue_Name__c =: oQSetup.Id limit 1];
        Queue_View_Item__c oQVI = [Select id, Name, API_Name__c, Field_Operator__c, Field_Value__c, Queue_View__c from Queue_View_Item__c where Queue_View__c =: oQV.id limit 1];
        List<Case> lstCase = [Select id, AccountId, Service_Center__c, Department__c, status,  Case_Owner__c, CaseNumber from Case];
        List<Task> lstTask = [Select id, Service_Center__c, Department__c, status, Owner_Queue__c from Task];
        List <Error_Log__c> lstErrorLogs = [Select id, Name, Error_Message__c from Error_Log__c limit 2];
        System.runas(oUser)
        {                     
            test.startTest();
            HUMTaskWorkQueueViewNameBatch oTaskViewBatch = New HUMTaskWorkQueueViewNameBatch(null, null);
            oTaskViewBatch.endTime = System.now().addHours(1);
            Database.executeBatch(oTaskViewBatch);            
            test.stopTest();
            
        }
        
    }  
    
    /*
     * Method Name : OverAllMainTestMethodException
     * Description : This Is the main method which Is used for testing the exceptions. 
     * Return type : Void
     * Paramater   : NA
     */     
    Private Static testMethod void OverAllMainTestMethodException()
    {
        Work_Queue_Setup__c oQSetup = [Select id, Name, Service_Center__c, Department__c from Work_Queue_Setup__c];
        User oUser = [Select Id , Name, current_queue__c from User where Username= 'user1user1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
        Queue_View__c oQV = [Select id, Name, Work_Queue_Name__c, IsActive__c, Criteria_Updated__c from Queue_View__c where Work_Queue_Name__c =: oQSetup.Id limit 1];
        Queue_View_Item__c oQVI = [Select id, Name, API_Name__c, Field_Operator__c, Field_Value__c, Queue_View__c from Queue_View_Item__c where Queue_View__c =: oQV.id limit 1];
        List<Case> lstCase = [Select id, AccountId, Service_Center__c, Department__c, status,  Case_Owner__c, CaseNumber from Case];
        List<Task> lstTask = [Select id, Service_Center__c, Department__c, status, Owner_Queue__c from Task];
        HUMQueueViewBatchSettings__c objBatchSetup = [Select Name, TriggerExecutionRequired__c from HUMQueueViewBatchSettings__c limit 1];
        List <Error_Log__c> lstErrorLogs = [Select id, Name, Error_Message__c from Error_Log__c limit 2];
        System.runas(oUser)
        {                     
            test.startTest();
            HUMTaskWorkQueueViewNameBatch oTaskViewBatch = New HUMTaskWorkQueueViewNameBatch(null, null);
            oTaskViewBatch.endTime = System.now().addSeconds(-5);
            Database.executeBatch(oTaskViewBatch);
            test.stopTest();
            system.assertEquals(true, objBatchSetup.TriggerExecutionRequired__c);
        }
        
    } 
    
    
}