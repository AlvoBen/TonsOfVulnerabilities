/***********************************************************************************************************
Apex Class Name  : HUMTaskHistoryHelperTest
Version          : 1.0
Created Date     : Mar 3rd 2015
Function         : Test class for HUMTaskHistoryHelper
************************************************************************************************************

Modification Log:

* Developer Name                Code Review           Date                        Description
*-----------------------------------------------------------------------------------------------------------
** Asha Deepika Koyyana         21275                03/03/2015                  Original Version
** Asha Deepika Koyyana         22083                04/03/2015                  Updated the code for defect fix #178778.0001
** Santhi Mandava               22274                04/10/2015                  Code Modified to fix test class failure
** Asha Deepika Koyyana         22312                04/13/2015                  Fixed test class failure issue 
** Prachi Bhasin                33402                07/18/2016                  Changes made as part of REQ- 270349 
** Pradeepkumar Dani            99570                06/08/2017                  Intent__c.Classification__c & Intent__c.Etl_Idtfr__c Clean up
** Pradeepkumar Dani            100996               06/13/2017                  Classification__c.Classification_Type__c & Etl_Idftr__c Clean Up
** Raja Sekhar Reddy M V        152248               11/29/2017                  Code Modified to fix test class failure
** Amol Patil                                        08/03/2018					 Req - 367846 Code modified to cover code coverage 
** Pooja Kumbhar								     04/15/2021					 Fixing Code Coverage for Checkmarx fixes
** Lakshmi Madduri									 02/23/2024				     US-5604142 PCO App Deprecation
************************************************************************************************************/
@isTest
private class HUMTaskHistoryHelperTest
{
    /**
     * Test method used to create wrapper class data
     * <p>
     * @return   void
     * @param    NA
     */
     @testSetup static void prepareTestData() 
    { 
      User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Supervisor');
        oCCSUser.Current_Queue__c = 'Cincinnati CRU';
        insert oCCSUser;
        system.assertNotEquals(oCCSUser, NULL, 'Value not matched');
        
        System.runAs(oCCSUser)
        {
            HUMTestDataHelperTest.dataForQueuesAndViews();
            
            Test.loadData(HUMConstants__c.sObjectType,'HUMConsTestData'); 
            
            HUMTaskNotAllowedProfiles__c objHumTaskTrgProf = new HUMTaskNotAllowedProfiles__c();
            objHumTaskTrgProf.Name = 'CRMRetail User';
            insert objHumTaskTrgProf;
                
            HUMQueueViewBatchSettings__c obj = new HUMQueueViewBatchSettings__c(Name='QueueViewBatchSetup',TriggerExecutionRequired__c=true);
            insert obj ;
            
            HUMObject_History_Metadata__c oHUMhisObj = new HUMObject_History_Metadata__c(Name = 'Task_Priority', Parent_Tracked_Object__c = 'Task', Tracked_Field__c = 'Priority');
            insert oHUMhisObj ;
            System.assertNotEquals(oHUMhisObj , NULL, 'History Metadata not inserted');
            
            Account oAcc = HUMTestDataHelperTest.getAccount(); 
            oAcc.Name='TestAccount1';
            oAcc.Source_Platform_Code__c = 'CA';
            oAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
            insert oAcc;            
            system.assertNotEquals(oAcc, null, 'Value not matched');
            
            HUMTriggerSwitch__c objTriggerSwitch = new HUMTriggerSwitch__c();
            objTriggerSwitch.Exeute_Trigger__c = True;
            objTriggerSwitch.Name = 'Task';
            Insert objTriggerSwitch;
            
            Product2 oProd = HUMTestDataHelperTest.getProduct();
            oProd.Name ='Mediclaim';
            oProd.Product_Type__c = 'Mediclaim';
            insert oProd;            
            system.assertNotEquals(oProd, null, 'Value not matched');
            
            Policy__c oPolicy1 = HUMTestDataHelperTest.getPolicy();
            oPolicy1.Name='test123';
            oPolicy1.Group_Name__c=oAcc.Id;
            oPolicy1.Funding_Type_Code__c='Y';
            oPolicy1.Product__c=oProd.Id;
            oPolicy1.Issue_State__c='IL';  
            insert oPolicy1;            
            system.assertNotEquals(oPolicy1, null, 'Value not matched');
            
            Policy_Member__c oPolMem1 = HUMTestDataHelperTest.getPolicyMember();
            oPolMem1.Name = '12345';
            oPolMem1.Member__c = oAcc.Id;
            oPolMem1.Policy__c = oPolicy1.Id;            
            oPolMem1.Relationship__c = 'Self';            
            insert oPolMem1;            
            system.assertNotEquals(oPolMem1, null, 'Value not matched');
            
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

            Case oCase1 = new Case(AccountId  = oAcc.Id, Intent_Id__c = lstIntent[0].id, Classification_Id__c =lstClassification[0].id, Policy_Member__c = oPolMem1.Id ,
               Interacting_With_Type__c = 'Group', Interacting_With__c = oAcc.Id, Due_Date__c = Date.Today(),
               Case_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU', Work_Queue_View_Name__c='Cincinati CRU',
               Service_Center__c = 'Cincinnati', Department__c = 'CRU', status = 'Critical');
            insert oCase1;            
            system.assertNotEquals(oCase1, null,'Value not matched');    
            
            
        }
    } 
    /**
    * Test method to test methods in HUMTaskHistoryHelper
    * <p>
    * @return       void
    * @param        NA
    */
    static testMethod void testTrackHistory()
    {
      User oCCSUser = [Select id,UserName from User where LastName ='test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        
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
            system.assertNotEquals(oTask1, null,'Value not matched');            
            insert oTask2;            
            system.assertNotEquals(oTask2, null,'Value not matched');
            
            Task_Field_History__c oTaskFieldHistory = new Task_Field_History__c (Created_At__c=System.now(),SObjFieldName__c='Priority',SObjFieldValue__c='Critical', SObjID__c=oTask1.Id);
            insert oTaskFieldHistory;
            system.assertNotEquals(oTaskFieldHistory, null, 'Value not matched');
            
            Task_Field_History__c oTaskFieldHistory1 = new Task_Field_History__c (Created_At__c=System.now(),SObjFieldName__c='Priority',SObjFieldValue__c='High', SObjID__c=oTask1.Id);
            insert oTaskFieldHistory1;
            system.assertNotEquals(oTaskFieldHistory1, null, 'Value not matched');
            
            test.startTest();
            oTask1.Priority = 'High';
            oTask1.Status = 'Closed';
			HUMTaskHistoryHelper.bIsAlreadyInserted=false;
            update oTask1;
            test.stopTest();
        }
    } 
    /**
    * Test method to test methods in HUMTaskHistoryHelper
    * <p>
    * @return       void
    * @param        NA
    */
    static testMethod void testTrackHistorySwitch()
    {
      User oCCSUser = [Select id,UserName from User where LastName ='test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        
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
            system.assertNotEquals(oTask1, null,'Value not matched');            
            insert oTask2;            
            system.assertNotEquals(oTask2, null,'Value not matched');
            
            Task_Field_History__c oTaskFieldHistory = new Task_Field_History__c (Created_At__c=System.now(),SObjFieldName__c='Priority',SObjFieldValue__c='Critical', SObjID__c=oTask1.Id);
            insert oTaskFieldHistory;
            system.assertNotEquals(oTaskFieldHistory, null, 'Value not matched');
            
            Task_Field_History__c oTaskFieldHistory1 = new Task_Field_History__c (Created_At__c=System.now(),SObjFieldName__c='Priority',SObjFieldValue__c='High', SObjID__c=oTask1.Id);
            insert oTaskFieldHistory1;
            system.assertNotEquals(oTaskFieldHistory1, null, 'Value not matched');
            
            test.startTest();
            oTask1.Priority = 'High';
            oTask1.Status = 'Closed';
			      HUMTaskHistoryHelper.bIsAlreadyInserted=false;
            update oTask1;
            test.stopTest();
        }
    } 
 }