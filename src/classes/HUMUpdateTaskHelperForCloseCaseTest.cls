/****************************************************************************
Apex Class Name  : HUMUpdateTaskHelperForCloseCaseTest
Version          : 1.0 
Created On       : 03/31/2015
Function         : Test class to HUMUpdateTaskHelperForCloseCase apex class

Modification Log :
* Developer Name             Code Review               Date                          Description
*-----------------------------------------------------------------------------------------------------------------------
* SuryaKumari Medicherla     22085                     03/31/2015                    Original Version
* SuryaKumari Medicherla     22312                     04/13/2015                    Fixed code for test class failure
* Prachi Bhasin                                        07/18/2016                    Changes made as part of REQ- 270349
* Pradeepkumar Dani			 99570				       06/08/2017					 Intent__c.Classification__c & Intent__c.Etl_Idtfr__c Clean up
* Pooja Kumbhar										   04/21/2021					 Test Class coverage for Chcekmarx chages
* Lakshmi Madduri					   				   02/23/2024              	 	 US-5604142 PCO App Deprecation
******************************************************************************************************************************/

@isTest
private class HUMUpdateTaskHelperForCloseCaseTest
{    
   /**
	* Test method to setup common test data for all test methods 
	*
	* @param None 	 	
	* @return  Void
	*/               
     @testSetup static void testSetup() 
     {
     	User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        oUser.Current_Queue__c = 'Cincinnati CRU';
        insert oUser;
        system.assert(oUser.Current_Queue__c == 'Cincinnati CRU');  
        
        system.runAs(oUser)
        {
            Account oAcc = HUMTestDataHelperTest.getAccount(); 
            oAcc.Name='TestAccount1';
            oAcc.Source_Platform_Code__c = 'CA';
            oAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
            insert oAcc;            
            system.assert(oAcc.Name == 'TestAccount1');
            
            Product2 oProd = HUMTestDataHelperTest.getProduct();
            oProd.Name ='Mediclaim';
            oProd.Product_Type__c = 'Mediclaim';
            insert oProd;            
            system.assert(oProd.Product_Type__c == 'Mediclaim');
            
            Policy__c oPolicy1 = HUMTestDataHelperTest.getPolicy();
            oPolicy1.Name='test123';
            oPolicy1.Group_Name__c=oAcc.Id;
            oPolicy1.Funding_Type_Code__c='Y';
            oPolicy1.Product__c=oProd.Id;
            oPolicy1.Issue_State__c='IL';  
            insert oPolicy1;            
            system.assert( oPolicy1.Name == 'test123');
            
            Policy_Member__c oPolMem1 = HUMTestDataHelperTest.getPolicyMember();
            oPolMem1.Name = '12345';
            oPolMem1.Member__c = oAcc.Id;
            oPolMem1.Policy__c = oPolicy1.Id;            
            oPolMem1.Relationship__c = 'Self';            
            insert oPolMem1;            
            system.assert(oPolMem1.Name == '12345');
            
            HUMTestDataHelperTest.dataForQueuesAndViews();  
            Test.loadData(HUMConstants__c.sObjectType,'HUMConsTestData'); 
            
            HUMQueueViewBatchSettings__c obj = new HUMQueueViewBatchSettings__c(Name='QueueViewBatchSetup',TriggerExecutionRequired__c=true);
            insert obj ;
            system.assert(obj.Name == 'QueueViewBatchSetup');
            
            HUMTaskNotAllowedProfiles__c objHumTaskTrgProf = new HUMTaskNotAllowedProfiles__c();
            objHumTaskTrgProf.Name = 'CRMRetail User';
            insert objHumTaskTrgProf;
            
            HUMTriggerSwitch__c objTriggerSwitch = new HUMTriggerSwitch__c();
            objTriggerSwitch.Exeute_Trigger__c = True;
            objTriggerSwitch.Name = 'Task';
            Insert objTriggerSwitch;
            
            Classification__c classificationRec = new Classification__c();
	        classificationRec.Name = 'Claims';
	        classificationRec.Is_Active__c = true;
	        insert classificationRec;
	        system.assert(classificationRec.Name == 'Claims');
	        
	        Intent__c intentRec = new Intent__c();
	        intentRec.Name = 'Filling';
	        intentRec.Is_Active__c = true;
	        insert intentRec;
	        system.assert(intentRec.Name == 'Filling');
            
            Case oCase1 = new Case(AccountId  = oAcc.Id, Intent_Id__c = intentRec.Id, Classification_Id__c = classificationRec.Id, Policy_Member__c = oPolMem1.Id ,
                               Interacting_With_Type__c = 'Group', Interacting_With__c = oAcc.Id, Due_Date__c = Date.Today(),
                               Case_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU', Work_Queue_View_Name__c='Cincinati CRU',
                               Service_Center__c = 'Cincinnati', Department__c = 'CRU', status = 'In Progress');
            
            insert oCase1;            
            system.assert(oCase1.Service_Center__c == 'Cincinnati');            
            
            Task oTask1 = new Task(WhatId = oCase1.id, Task_Owner__c='Cincinnati CRU',Type ='Work Task', Owner_Queue__c='Cincinnati CRU',
                               Work_Queue_View_Name__c='Cincinati CRU', Service_Center__c ='Cincinnati', Department__c='CRU', 
                               status='In Progress');
                               
            Task oTask2 = new Task(WhatId = oCase1.id, Task_Owner__c='Cincinnati CRU',Type ='Notification Task', Owner_Queue__c='Cincinnati CRU',
                               Work_Queue_View_Name__c='Cincinati CRU', Service_Center__c ='Cincinnati', Department__c='CRU', 
                               status='In Progress', ActivityDate = Date.Today());
            
            insert oTask1;
            system.assert(oTask1.Type == 'Work Task');            
            insert oTask2;
            system.assert(oTask2.Type =='Notification Task'); 
        }	
     }

    /*
     * Test Method to test queue view assignment 
     * @ Param    : None
     * @ Return   : None 
     */     
    private static testMethod void OverAllMainTestMethod()
    {
    	User testUser = [Select Id,Name from User where LastName='test User1'];
        System.runAs(testUser)
        {  
        	Test.StartTest();
        	        
	            Task oTask1 = [Select Id ,Status , Isdeleted from task where Type = 'Work Task' limit 1 ] ;
	            oTask1.status = 'Closed';
	            update oTask1;
	            
	            Task oTask2 = [Select Id ,Status , Isdeleted from task where Type = 'Notification Task' limit 1 ] ;
	            oTask2.Type ='Work Task';
	            update oTask2;
	            
	            Task objTask = [Select Id, Status From Task Where Id =: oTask2.Id];
	            system.assertEquals(objTask.Status, 'In Progress','Value not matched');
            
            Test.StopTest();
        }
    }      
}