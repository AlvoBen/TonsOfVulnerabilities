/***********************************************************************************************************
Apex Class Name  : HUMTaskCommentsControllerTest
Version          : 1.0
Created Date     : Feb 27th 2015
Function         : Test class for HUMTaskCommentsController
************************************************************************************************************

Modification Log:

* Developer Name                Code Review           Date                        Description
*-----------------------------------------------------------------------------------------------------------
*  Asha Deepika Koyyana           21256               02/27/2015                  Original Version
*  Asha Deepika Koyyana           21857               03/26/2015                  Fixed defect #177621.0001 
*  Prachi Bhasin                  33486               07/18/2016                  Changes made as part of REQ- 270349
*  Pradeepkumar Dani              99570               06/08/2017                  Intent__c.Classification__c & Intent__c.Etl_Idtfr__c Clean up
*  Pradeepkumar Dani             100996               06/13/2017                  Classification__c.Classification_Type__c & Etl_Idftr__c Clean Up
*  Prudhvi Pamarthi               154304              01/19/2018                  REQ - 328929 Fix for HUMExceptionHelper: DML
*  Santhi Mandava                                     03/12/2019                  Removed "DpaaSWebServiceMockImpl" usage. 
************************************************************************************************************/
@isTest
private class HUMTaskCommentsControllerTest
{
    /**
    * testsetup method to create test data
    *<p>
    * @param            NULL
    * @return           void
    */
     @testSetup static void prepareTestData() 
    {   
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertNotEquals(oCCSUser, NULL, 'Value not matched');
        
        System.runAs(oCCSUser)
        {
            Account oAccount = new Account();
            oAccount.Name = 'New Case Account';
            oAccount.Group_Number__c  = '123';
            oAccount.BillingState = 'New York';
            oAccount.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
            insert oAccount;
            System.assert(oAccount.BillingState == 'New York');
            
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
            system.assertEquals(intentRec.Name == 'Filling', true);

            Case oCase = new Case(AccountId=oAccount.Id, Intent_Id__c = lstIntent[0].id,General_Case__c = true, Classification_Id__c =lstClassification[0].id,Interacting_With_Type__c = 'Group',Interacting_With__c=oAccount.Id, Due_Date__c=Date.Today(), Case_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU1', Work_Queue_View_Name__c='Cincinati CRU1',Service_Center__c ='Cincinnati',Department__c='CRU1', status='In Progress');
            insert oCase;
            system.assert(oCase.AccountId == oAccount.Id);           
            
        }
    }
    
    /**
    * testTaskComments
    *<p>
    * This method to test methods in HUMTaskCommentsController
    * @param            NULL
    * @return           void
    */
    static testMethod void testTaskComments()
    {
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        
        System.runAs(oCCSUser)
        {
            Case oCase = [Select id from case];
            Task oTask = new Task(WhatId = oCase.id, Task_Owner__c = UserInfo.getUserName(), Owner_Queue__c = 'Cincinnati CRU',Work_Queue_View_Name__c = 'Cincinati CRU', Service_Center__c = 'Cincinnati', Department__c = 'CRU', status = 'In Progress' ,Description = 'Hi Comment' );
            insert oTask;               
            system.assert(oTask.Owner_Queue__c == 'Cincinnati CRU'); 
            
            Task_Field_History__c oTaskFieldHistory = new Task_Field_History__c (Created_At__c=System.now(),SObjFieldName__c='Description',SObjFieldValue__c='Comment 1', SObjID__c='Task');
            insert oTaskFieldHistory;
            system.assert(oTaskFieldHistory.SObjFieldName__c =='Description');
            
            Task_Field_History__c oTaskFieldHistory1 = new Task_Field_History__c (Created_At__c=System.now(),SObjFieldName__c='LastModifiedby_Queue__c ',SObjFieldValue__c='Cincinnati CRU', SObjID__c='Task');
            insert oTaskFieldHistory1;
            system.assert(oTaskFieldHistory1.SObjID__c == 'Task');
            
            Task_Field_History__c oTaskFieldHistory2 = new Task_Field_History__c (Created_At__c=System.now(),SObjFieldName__c='Description',SObjFieldValue__c='Comment 2', SObjID__c='Task');
            insert oTaskFieldHistory2;
            system.assert(oTaskFieldHistory2.SObjFieldName__c == 'Description');

            Task_Field_History__c oTaskFieldHistory3 = new Task_Field_History__c (Created_At__c=System.now(),SObjFieldName__c='LastModifiedby_Queue__c ',SObjFieldValue__c='Cincinnati Calls', SObjID__c='Task');
            insert oTaskFieldHistory3;
            system.assert(oTaskFieldHistory3.SObjFieldName__c == 'LastModifiedby_Queue__c ');
            
            List<Task_Field_History__c> lstFieldHis = new List<Task_Field_History__c>();
            lstFieldHis.add(oTaskFieldHistory);
            lstFieldHis.add(oTaskFieldHistory1);
            lstFieldHis.add(oTaskFieldHistory2);
            lstFieldHis.add(oTaskFieldHistory3);
            System.AssertNotEquals(lstFieldHis.size(), 0);
            
            HUMConstants__c oHUMObj = new HUMConstants__c(Name='HUMConstantOne', IntegerValue__c = 1);
            insert oHUMObj;
            System.assert(oHUMObj.Name == 'HUMConstantOne');
            
            HUMConstants__c oHUMObj1 = new HUMConstants__c(Name='HUMLISTSIZEZERO', IntegerValue__c = 0);
            insert oHUMObj1;
            System.assert(oHUMObj1.Name == 'HUMLISTSIZEZERO');
            
            HUMConstants__c oHUMObj2 = new HUMConstants__c(Name='ListSize', IntegerValue__c = 5);
            insert oHUMObj2;
            System.assert(oHUMObj2.Name == 'ListSize');
            
            HUMConstants__c oHUMObj3 = new HUMConstants__c(Name='HUMTaskDescriptionLimitSize', IntegerValue__c = 50);
            insert oHUMObj3;
            System.assert(oHUMObj3.Name == 'HUMTaskDescriptionLimitSize');

            test.startTest();
            
            HUMTaskCommentsController oTaskComments = new HUMTaskCommentsController();
            System.assertNotEquals(oTaskComments , NULL, 'Object instantiated');
            
            oTaskComments.getModifiedQueue(lstFieldHis);
            Boolean  bFlag = oTaskComments.getDisableNext();
            System.assertNotEquals(bFlag, false);
            
            oTaskComments.lstFieldUpdates =  lstFieldHis;
            oTaskComments.getComments();
            oTaskComments.getPrevious();
            System.assert(true,oTaskComments.getPrevious()!= NULL); 
            
            oTaskComments.getNext();
            System.assert(true,oTaskComments.getNext()!= NULL); 
            
            bFlag  = oTaskComments.getDisablePrevious();
            System.assertEquals(bFlag, true);
            
            oTaskComments.getFirstList();
            System.assert(true,oTaskComments.getPrevious()!= NULL); 
            
            oTaskComments.getLastList();
            System.assert(true,oTaskComments.getPrevious()!= NULL); 
            
            oTaskComments.getPageLastRecord();
            oTaskComments.getTotal_size();
            integer iCounter = oTaskComments.getPageFirstRecord();
            System.assertNotEquals(iCounter, 0);
            
            List<Error_Log__c> lstLog = new List<Error_Log__c>();            
            Error_Log__c error = new Error_Log__c(Status__c = 'HUMTaskCommentsController');            
            lstLog.add(error);                        
            oTaskComments.listErrorLogs = lstLog;            
            System.assert(oTaskComments.listErrorLogs.size() > 0);
                 
            test.stopTest(); 
        }
    } 
}