/*****************************************************************************************
Apex class Name    : TaskHeirarchyCase_T_HUM
Version            : 1.0
Function           : This is test class for TaskHeirarchyCase_C_HUM and TaskHeirarchyTask_C_HUM
Created Date       : 17 Sept 2016

Modification Log:
*   Developer                           Code Review             Date                   Description
* ------------------------------------------------------------------------------------------------------------------------------                
*   Avinash Choubey                     40794                  09/17/2016              Original version REQ- 252450
*   Avinash Choubey                                            10/03/2016              added getTaskComment method REQ - 252573
*   Pradeepkumar Dani                   99570                  06/08/2017              Intent__c.Classification__c & Intent__c.Etl_Idtfr__c Clean up
*   Pradeepkumar Dani                   100996                 06/13/2017              Classification__c.Classification_Type__c & Etl_Idftr__c Clean Up
*   Santhi Mandava                                             03/12/2019              Removed "DpaaSWebServiceMockImpl" usage.
********************************************************************************************************************************/
@isTest
private class TaskHeirarchyCase_T_HUM
{
   
   /**
    * testsetup method to create test data
    *<p>
    * @param            NULL
    * @return           void
    */
    @testSetup static void prepareTestData()
    {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        System.assert(oUser.LanguageLocaleKey == 'en_US');
        
        System.runAs (oUser) 
        {
            Account oAccount = HUMTestDataHelperTest.getAccount();
            oAccount.Name = 'New Case Account';
            oAccount.Group_Number__c  = '123';
            oAccount.BillingState = 'New York';
            oAccount.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
            insert oAccount;
            System.assert(oAccount.BillingState == 'New York');
            
            List<HUMConstants__c> lstHUMConstants = new List<HUMConstants__c>();
            lstHUMConstants.add(new HUMConstants__c(Name='HUMConstantOne', StringValue__c='1', IntegerValue__c = 1));
            lstHUMConstants.add(new HUMConstants__c(Name='HUMLISTSIZEZERO', StringValue__c='',IntegerValue__c = 0));
            lstHUMConstants.add(new HUMConstants__c(Name='HUMCompleted',StringValue__c='Completed'));
            lstHUMConstants.add(new HUMConstants__c(Name='HUMClosed',StringValue__c='Closed'));
            lstHUMConstants.add(new HUMConstants__c(Name='HUMWorkTask',StringValue__c='Work Task'));
            lstHUMConstants.add(new HUMConstants__c(Name='HUMNotificationTask',StringValue__c='Notification Task'));
            insert lstHUMConstants;
            System.assert(lstHUMConstants.size() > 0 );
             
            HUMTaskRecordTypes__c oRecTypes =  new HUMTaskRecordTypes__c(Name='RecordType');
            oRecTypes.ServiceCRMRecTypeName__c = 'Service CRM';
            oRecTypes.CSIRecordTypeName__c = 'Service CRM';
            insert oRecTypes;
            system.assert(oRecTypes.CSIRecordTypeName__c == 'Service CRM');
            
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

            Case oCase = new Case(AccountId=oAccount.Id, Intent_Id__c = lstIntent[0].id, Classification_Id__c =lstClassification[0].id, General_Case__c = true,Interacting_With_Type__c = 'Group',Interacting_With__c=oAccount.Id, Due_Date__c=Date.Today(), Case_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU1', Work_Queue_View_Name__c='Cincinati CRU1',Service_Center__c ='Cincinnati',Department__c='CRU1', status='In Progress');
            insert oCase;
            system.assert(oCase.AccountId == oAccount.Id);
           
            
            Task oTask = HUMTestDataHelperTest.getTaskRecord();
            oTask.WhatId = oCase.Id;
            oTask.Task_Owner__c = 'User1';
            oTask.Owner_Queue__c ='Cincinnati CRU1';            
            oTask.status = 'In Progress';
            oTask.QueueOrUserId__c = oUser.Id;
            oTask.ActivityDate = Date.Today();
            oTask.Priority ='High';
            oTask.Type = 'Work Task';
            
            insert oTask;
            System.assert(oTask.status == 'In Progress');
            
            Task oTask2 = HUMTestDataHelperTest.getTaskRecord();
            oTask2.WhatId = oCase.Id;
            oTask2.Task_Owner__c = 'User1';
            oTask2.Owner_Queue__c ='Cincinnati CRU1';            
            oTask2.status = 'Closed';
            oTask2.QueueOrUserId__c = oUser.Id;
            oTask2.Date_Time_Closed__c = System.Now();
            oTask2.ActivityDate = Date.Today();
            oTask2.Priority ='Normal';
            oTask2.Type = 'Work Task';
            insert oTask2;
            System.assert(oTask2.status == 'Closed');
            
            Task oTaskRec = HUMTestDataHelperTest.getTaskRecord();
            oTaskRec.WhatId = oCase.Id;
            oTaskRec.Task_Owner__c = 'Cincinnati CRU1';
            oTaskRec.Owner_Queue__c ='Cincinnati CRU1';            
            oTaskRec.status = 'In Progress';
            oTaskRec.QueueOrUserId__c = oUser.Id;
            oTaskRec.Priority ='Normal';
            oTaskRec.Type = 'Work Task';
             oTask2.ActivityDate = null;
            insert oTaskRec;
            System.assert(oTaskRec.Owner_Queue__c == 'Cincinnati CRU1');
            
            List<HUM_Task_Heirarchy__c>  lstTH = new List<HUM_Task_Heirarchy__c>();
            HUM_Task_Heirarchy__c objTH1 = new HUM_Task_Heirarchy__c();
            objTH1.HUM_Parent_TaskID__c = oTask.id;
            objTH1.HUM_Dependent_TaskID__c = oTask2.id;
            objTH1.Hum_CaseID__c = oTask2.whatId;
            lstTH.add(objTH1);
            
            HUM_Task_Heirarchy__c objTH2 = new HUM_Task_Heirarchy__c();
            objTH2.HUM_Parent_TaskID__c = oTask2.id;
            objTH2.HUM_Dependent_TaskID__c = oTaskRec.id;
            objTH2.Hum_CaseID__c = oTaskRec.whatId;
            lstTH.add(objTH2);
            insert lstTH;
            System.assert(lstTH[0].HUM_Dependent_TaskID__c == oTask2.id);
            
        }
    }     
  
    /**
    * testMethodForPositive
    *<p>
    *
    * @param            NULL
    * @return           void
    */
    static testMethod void testTaskHeirarchy()
    {       
        User oUserR = [ select id,Current_Queue__c from user where LastName = 'test User1' limit 1 ];
        System.runAs (oUserR) 
        {
            Task parentTaskID = [select id from task where Priority ='High' limit 1 ];
            Case ParentCase = [select id from case where Interacting_With_Type__c = 'Group' limit 1];
            
            Test.startTest();
            List<TaskHeirarchyWrapper_DTO_HUM>  lstTHWCase =  TaskHeirarchyCase_C_HUM.getTreeDetails(ParentCase.id);
            System.assert(lstTHWCase.size() == 3);
            
            List<TaskHeirarchyWrapper_DTO_HUM>  lstTHWTask =  TaskHeirarchyTask_C_HUM.getTreeDetails(ParentCase.id,parentTaskID.Id);
            System.assert(lstTHWTask.size() == 2);
            
            List<TaskHeirarchyWrapper_DTO_HUM>  lstTHWTask1  =  TaskHeirarchyCase_C_HUM.getTreeDetails('InvalidID');
            List<TaskHeirarchyWrapper_DTO_HUM>  lstTHWTask2 =   TaskHeirarchyTask_C_HUM.getTreeDetails('InvalidID','InvalidID');
            
            Test.stopTest();
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
            Test.startTest();
            DateTime currentTime = System.now();
            TaskHeirarchyCase_C_HUM objCaseTaskHei = new TaskHeirarchyCase_C_HUM();
            TaskHeirarchyTask_C_HUM objTaskHei = new TaskHeirarchyTask_C_HUM();
            Task otask = [select ID from Task where Owner_Queue__c = 'Cincinnati CRU1'  limit 1];
            ApexPages.currentPage().getParameters().put(GLOBAL_CONSTANT_HUM.CASEMGMT_TASKID_HUM,(String)otask.id);
            
            objCaseTaskHei.getComments();
            system.assert(objCaseTaskHei.sTaskComment == System.Label.TASK_NO_COMMENT_HUM);
            
            objTaskHei.getComments();
            system.assert(objTaskHei.sTaskComment == System.Label.TASK_NO_COMMENT_HUM);
            
            
            Task_Field_History__c oTaskFieldHistory = new Task_Field_History__c (Created_At__c=currentTime,SObjFieldName__c = GLOBAL_CONSTANT_HUM.CASEMGMT_DESCRIPTION_HUM,SObjFieldValue__c='Comment 1', SObjID__c= otask.id);
            insert oTaskFieldHistory;
            system.assert(oTaskFieldHistory.SObjFieldValue__c == 'Comment 1');
            
            objTaskHei.getComments();
            system.assert(objTaskHei.sTaskComment == 'Comment 1');
            objCaseTaskHei.getComments();
            system.assert(objCaseTaskHei.sTaskComment == 'Comment 1');
            
            objTaskHei.getComments();
            system.assert(objTaskHei.sTaskComment == 'Comment 1');
            
            
            Task_Field_History__c oTaskFieldHistory2 = new Task_Field_History__c (Created_At__c=currentTime.addSeconds(1),SObjFieldName__c=GLOBAL_CONSTANT_HUM.CASEMGMT_DESCRIPTION_HUM,SObjFieldValue__c='Comment 2', SObjID__c= otask.id);
            insert oTaskFieldHistory2;
            system.assert(oTaskFieldHistory2.SObjFieldValue__c =='Comment 2');
            
            objCaseTaskHei.getComments();
            system.assert(objCaseTaskHei.sTaskComment == 'Comment 2');
            
            objTaskHei.getComments();
            system.assert(objTaskHei.sTaskComment == 'Comment 2');
            
            Task_Field_History__c oTaskFieldHistory3 = new Task_Field_History__c (Created_At__c=currentTime.addSeconds(2),SObjFieldName__c=GLOBAL_CONSTANT_HUM.CASEMGMT_DESCRIPTION_HUM,SObjFieldValue__c='Comment 3', SObjID__c= otask.id);
            insert oTaskFieldHistory3;
            system.assert(oTaskFieldHistory3.SObjFieldValue__c =='Comment 3');
            
            Task_Field_History__c oTaskFieldHistory4 = new Task_Field_History__c (Created_At__c=currentTime.addSeconds(3),SObjFieldName__c=GLOBAL_CONSTANT_HUM.CASEMGMT_DESCRIPTION_HUM,SObjFieldValue__c='Comment 4', SObjID__c= otask.id);
            insert oTaskFieldHistory4;
            system.assert(oTaskFieldHistory4.SObjFieldValue__c =='Comment 4');
            
            Task_Field_History__c oTaskFieldHistory5 = new Task_Field_History__c (Created_At__c=currentTime.addSeconds(4),SObjFieldName__c=GLOBAL_CONSTANT_HUM.CASEMGMT_DESCRIPTION_HUM,SObjFieldValue__c='Comment 5', SObjID__c= otask.id);
            insert oTaskFieldHistory5;
            system.assert(oTaskFieldHistory5.SObjFieldValue__c =='Comment 5');
            
            objCaseTaskHei.getComments();
            system.assert(objCaseTaskHei.sTaskComment == 'Comment 5');
            
            objTaskHei.getComments();
            system.assert(objTaskHei.sTaskComment == 'Comment 5');
            
            Test.StopTest();
           
        }
    }
}