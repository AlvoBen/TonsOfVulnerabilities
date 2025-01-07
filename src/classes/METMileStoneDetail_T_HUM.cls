/***************************************************************************************************
@ Author Prasanthi Kandula
@ Name METMileStoneDetail_T_HUM
@ CreateDate 01/03/2017
@ Description Class contains test methods for METMileStoneDetail_C_HUM controller
@ Version 1.0
@ Reference NA

Modification Log :
* Developer              Code Review           Date                      Description
* -----------------------------------------------------------------------------------------------                 
* Prasanthi Kandula         55297             01/03/2017                Original Version
* Syed Mubarak              115044           08/01/2017                 Issue fixes after REQ - 296817
* Manoj Chaurasia           202594            03/29/2018                REQ - 354470 Ability to display TAS combination in CRM
* Vamshi Krishna Pemberthi                    07/08/2021                Improved Code Coverage for MET Triggers
***************************************************************************************************/
@isTest
private class METMileStoneDetail_T_HUM
{
    /*
    * Method Name : setup
    * Description : This method is used to create test data for the class 
    * Return type : void
    * Parameter   : NA
    */
    @testSetup static void setup() 
    {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        oUser.Current_Queue__c = 'Cincinnati CRU1';
        oUser.Network_User_Id__c = 'SXS2222';
        insert oUser;
        PermissionSet ps = [SELECT ID From PermissionSet WHERE Name = 'CRMS_605_BizConfig_MedicareEnrollment_Edit'];
        insert New PermissionSetAssignment(AssigneeId = oUser.id, PermissionSetId = ps.Id );
        System.assertEquals(oUser.LastName, 'test User1');
        system.runAs(oUser)
        {
            MET_Task__c oTask = new MET_Task__c();
            oTask.Name = 'Test Task';
            oTask.Is_Active__c = true;
            oTask.Case_Subtype__c = 'Routine';
            insert oTask;
            System.assertEquals(oTask.Name,'Test Task');
            
            MET_Task__c oTaskData = new MET_Task__c();
            oTaskData .Name = 'Test Task1';
            oTaskData .Is_Active__c = true;
            oTaskData .Case_Subtype__c = 'Routine';
            insert oTaskData ;
            System.assertEquals(oTaskData.Name,'Test Task1');
            
            MET_Action__c oAction = new MET_Action__c();
            oAction.Name = 'Test Action';
            oAction.Is_Active__c = true;
            insert oAction;
            System.assertEquals(oAction.Name,'Test Action');

            MET_Action__c oActionData = new MET_Action__c();
            oActionData.Name = 'Test Action1';
            oActionData.Is_Active__c = true;
            insert oActionData;
            System.assertEquals(oActionData.Name,'Test Action1');

            MET_Source__c oSource = new MET_Source__c();
            oSource.Name = 'Test Source';
            oSource.Is_Active__c = true; 
            insert oSource; 
            System.assertEquals(oSource.Name,'Test Source');
            
            MET_Source__c oSourceData = new MET_Source__c();
            oSourceData.Name = 'Test Source1';
            oSourceData.Is_Active__c = true; 
            insert oSourceData; 
            System.assertEquals(oSourceData.Name,'Test Source1');

            MET_Milestone__c oMileStone = new MET_Milestone__c();
            oMileStone.Name = '123';
            oMilestone.Case_Type__c = 'Customer Inquiry';
            oMileStone.Case_Subtype__c = 'Routine';
            oMileStone.Action__c = oAction.id;
            oMileStone.Source_Name__c =  oSource.id;
            oMileStone.Task__c =  oTask.id;
            oMileStone.Is_Active__c = true; 
            oMileStone.Is_Milestone__c = true;
            insert oMileStone;
            System.assertEquals(oMileStone.Case_Subtype__c,'Routine');

            /* insert new HUMTriggerSwitch__c (name='MET_Milestone__c', Exeute_Trigger__c=true);
            insert new HUMTriggerSwitch__c (name='MET_Task__c', Exeute_Trigger__c=true);
            insert new HUMTriggerSwitch__c (name='MET_Action__c', Exeute_Trigger__c=true); */
        }
    }
    
    /*
    * Method Name : testTaskLookup
    * Description : This method is used to test the Task filter
    * Return type : void
    * Parameter   : NA
    */
    static testMethod void testTaskLookup()
    {
        User oUser = [Select Id, Current_Queue__c,Network_User_Id__c  from User where LastName = 'test User1'];
        System.assertEquals('Cincinnati CRU1', oUser.Current_Queue__c);
        System.runAs(oUser)
        {
            MET_Milestone__c oMileStone = [SELECT id,Case_Type__c, Case_Subtype__c ,Name,Task__c,Action__c,Source_Name__c from MET_Milestone__c where Name = '123'];
            Apexpages.StandardController stdController = new Apexpages.StandardController(oMileStone);
            METMileStoneDetail_C_HUM oMileStoneDetail = new METMileStoneDetail_C_HUM(stdController);
            PageReference pgRef = Page.METMileStoneDetail_VF_HUM; 
            Test.setCurrentPage(pgRef); 
            oMileStoneDetail.sMileStoneID = ApexPages.currentPage().getParameters().put('id', oMileStone.Id);
            oMileStoneDetail.sActionName = oMileStone.Action__c;
            oMileStoneDetail.sSourceName = oMileStone.Source_Name__c;
            oMileStoneDetail.sCaseTypeName = oMileStone.Case_Type__c;
            oMileStoneDetail.sCaseSubtypeName = oMileStone.Case_Subtype__c;
            oMileStoneDetail.oMileStone = oMileStone;
            oMileStoneDetail.sTaskName = 'Test';
            PageReference oPgRef = oMileStoneDetail.Save();
            
            System.assertNotEquals(oPgRef,null);
        }
    }
    
    /*
    * Method Name : testDuplicateTasksLookup
    * Description : This method is used to test data for duplicate tasks 
    * Return type : void
    * Parameter   : NA
    */
    static testMethod void testDuplicateTasksLookup()
    {
        User oUser = [Select Id, Current_Queue__c,Network_User_Id__c  from User where LastName = 'test User1'];
        System.assertEquals('Cincinnati CRU1', oUser.Current_Queue__c);
        System.runAs(oUser)
        {
            MET_Task__c oTask = [SELECT id, Name, Case_Subtype__c, Is_Active__c FROM MET_Task__c WHERE Name = 'Test Task'];
            MET_Task__c oTaskDuplicate = new MET_Task__c();
            oTaskDuplicate.Name = 'Test Task1';
            oTaskDuplicate.Is_Active__c = true;
            oTaskDuplicate.Case_Subtype__c = 'Routine';
            insert oTaskDuplicate;
            System.assertEquals(oTaskDuplicate.Name,'Test Task1');
            System.assertEquals(oTask.Name,'Test Task');
            MET_Milestone__c oMileStone = [SELECT id, Case_Type__c, Case_Subtype__c ,Name,Task__c,Action__c,Source_Name__c from MET_Milestone__c where Name = '123'];
            System.assertEquals(oMileStone.Case_Subtype__c,'Routine');
            Apexpages.StandardController stdController = new Apexpages.StandardController(oMileStone);
            METMileStoneDetail_C_HUM oMileStoneDetail = new METMileStoneDetail_C_HUM(stdController);
            oMileStoneDetail.iTaskId = oTask.id;
            oMileStoneDetail.sMileStoneID = oMileStone.Id;
            oMileStoneDetail.oMileStone = oMileStone;
            oMileStoneDetail.setSortDirection('ASC');
            System.assertEquals('ASC',oMileStoneDetail.getSortDirection());
            oMileStoneDetail.getSortDirection();
            oMileStoneDetail.sSortExpression = 'Name';
            oMileStoneDetail.sTaskName = 'Test';
            PageReference oPgRef = oMileStoneDetail.Save();
            System.assertEquals(oPgRef,null);
            oMileStoneDetail.sTaskName = '12333';
            oMileStoneDetail.Save();
            System.assertEquals(oPgRef,null);
        }
    }   
    
    /*
    * Method Name : testAction
    * Description : This method is used to test the Action Filter
    * Return type : void
    * Parameter   : NA
    */
    static testMethod void testAction()
    {
        User oUser = [Select Id, Current_Queue__c,Network_User_Id__c  from User where LastName = 'test User1'];
        System.assertEquals('Cincinnati CRU1', oUser.Current_Queue__c);
        System.runAs(oUser)
        {
            MET_Task__c oTaskNew = [SELECT id, Name, Case_Subtype__c, Is_Active__c FROM MET_Task__c WHERE Name = 'Test Task'];
            System.assertEquals(oTaskNew.Case_Subtype__c, 'Routine');
            MET_Action__c  oAction = [SELECT id, Name, Is_Active__c FROM MET_Action__c  WHERE Name = 'Test Action'];
            System.assertEquals(oAction.Is_Active__c, true);
            MET_Action__c  oActionDuplicate = [SELECT id, Name, Is_Active__c FROM MET_Action__c  WHERE Name = 'Test Action1'];
            System.assertEquals(oActionDuplicate.Name, 'Test Action1');
            MET_Milestone__c oMileStone = [SELECT id,Case_Type__c,Case_Subtype__c ,Name,Task__c,Action__c,Source_Name__c from MET_Milestone__c where Name = '123'];
            Apexpages.StandardController stdController = new Apexpages.StandardController(oMileStone);
            METMileStoneDetail_C_HUM oMileStoneDetail = new METMileStoneDetail_C_HUM(stdController);
            oMileStoneDetail.iActionId = oAction.id;
            oMileStoneDetail.sSortDirection = '';
            oMileStoneDetail.getSortDirection();
            oMileStoneDetail.sMileStoneID = oMileStone.Id;
            oMileStoneDetail.oMileStone = oMileStone;
            oMileStoneDetail.sTaskName = 'Test Task';
            oMileStoneDetail.sActionName = 'Test';
            PageReference oPgRef = oMileStoneDetail.Save();
           System.assertEquals(oPgRef,null);
        }
    } 
    
    /*
    * Method Name : testSource
    * Description : This method is used to test the Source Filter Lookup
    * Return type : void
    * Parameter   : NA
    */
    static testMethod void testSource()
    {
        User oUser = [Select Id, Current_Queue__c,Network_User_Id__c  from User where LastName = 'test User1'];
        System.assertEquals('Cincinnati CRU1', oUser.Current_Queue__c);
        System.runAs(oUser)
        {
            MET_Action__c  oAction = [SELECT id, Name, Is_Active__c FROM MET_Action__c  WHERE Name = 'Test Action'];
            System.assertEquals(oAction.Is_Active__c, true);
            MET_Source__c oSource = [SELECT id, Name, Is_Active__c FROM MET_Source__c WHERE Name = 'Test Source'];
            System.assertEquals(oSource.Name, 'Test Source');
            MET_Milestone__c oMileStone = [SELECT id,Case_Type__c,Case_Subtype__c ,Name,Task__c,Action__c,Source_Name__c from MET_Milestone__c where Name = '123'];
            Apexpages.StandardController stdController = new Apexpages.StandardController(oMileStone);
            METMileStoneDetail_C_HUM oMileStoneDetail = new METMileStoneDetail_C_HUM(stdController);
            oMileStoneDetail.iSourceId = oSource.id;
            oMileStoneDetail.sSourceName = 'Test Source';
            oMileStoneDetail.sActionName = 'Test Action';
            oMileStoneDetail.sMileStoneID = oMileStone.Id;
            oMileStoneDetail.oMileStone = oMileStone;
            PageReference oPgRef = oMileStoneDetail.Save();
            System.assertEquals(oPgRef,null);
        }
    }
    
    /*
    * Method Name : testMultipleSource
    * Description : This method is used to test the filter on Multiple Source records
    * Return type : void
    * Parameter   : NA
    */
    static testMethod void testMultipleSource()
    {
        User oUser = [Select Id, Current_Queue__c,Network_User_Id__c  from User where LastName = 'test User1'];
        System.assertEquals('Cincinnati CRU1', oUser.Current_Queue__c);
        System.runAs(oUser)
        {
            MET_Task__c  oTask = [SELECT id, Name, Is_Active__c FROM MET_Task__c  WHERE Name = 'Test Task'];
            MET_Action__c  oAction = [SELECT id, Name, Is_Active__c FROM MET_Action__c  WHERE Name = 'Test Action'];
            MET_Source__c oSource = [SELECT id, Name, Is_Active__c FROM MET_Source__c WHERE Name = 'Test Source'];
            MET_Source__c oSource1 = [SELECT id, Name, Is_Active__c FROM MET_Source__c WHERE Name = 'Test Source1'];
            MET_Milestone__c oMileStone = [SELECT id,Case_Type__c,Case_Subtype__c ,Name,Task__c,Action__c,Source_Name__c from MET_Milestone__c where Name = '123'];
            Apexpages.StandardController stdController = new Apexpages.StandardController(oMileStone);
            METMileStoneDetail_C_HUM oMileStoneDetail = new METMileStoneDetail_C_HUM(stdController);
            oMileStoneDetail.sMileStoneID = oMileStone.Id;
            oMileStoneDetail.oMileStone = oMileStone;
            oMileStoneDetail.sSourceName = 'Test';
            oMileStoneDetail.sActionName = 'Test Action';
            oMileStoneDetail.Save();
            oMileStoneDetail.sActionName = '';
            oMileStoneDetail.sSourceName = '45612';
            PageReference oPgRef = oMileStoneDetail.Save();
            System.assertEquals(oPgRef,null);
        }
    }
    
    /*
    * Method Name : testCancel
    * Description : This method is used to test the Cancel button logic on MileStone New/Edit page
    * Return type : void
    * Parameter   : NA
    */
    static testMethod void testCancel()
    {
        User oUser = [Select Id, Current_Queue__c,Network_User_Id__c  from User where LastName = 'test User1'];
        System.assertEquals('Cincinnati CRU1', oUser.Current_Queue__c);
        System.runAs(oUser)
        {
            MET_Milestone__c oMileStone = [SELECT id,Case_Type__c,Case_Subtype__c ,Name,Task__c,Action__c,Source_Name__c from MET_Milestone__c where Name = '123'];
            System.assertEquals(oMileStone.Case_Subtype__c,'Routine');
            Apexpages.StandardController stdController = new Apexpages.StandardController(oMileStone);
            METMileStoneDetail_C_HUM oMileStoneDetail = new METMileStoneDetail_C_HUM(stdController);
            oMileStoneDetail.sMileStoneID = oMileStone.Id;
            PageReference oPgRef = oMileStoneDetail.Cancel();
            System.assertNotEquals(oPgRef,null);
            oMileStoneDetail.sMileStoneID = '';
            PageReference oPgRef1 = oMileStoneDetail.Cancel();
            System.assertNotEquals(oPgRef1,null);
        }
    }
    
    /*
    * Method Name : testSaveAndNew
    * Description : This method is used to test the Save & New button logic on MileStone New/Edit page
    * Return type : void
    * Parameter   : NA
    */
    static testMethod void testSaveAndNew()
    {
        User oUser = [Select Id, Current_Queue__c,Network_User_Id__c  from User where LastName = 'test User1'];
        System.assertEquals('Cincinnati CRU1', oUser.Current_Queue__c);
        System.runAs(oUser)
        {
            MET_Milestone__c oMileStone = [SELECT id,Case_Type__c,Case_Subtype__c ,Name,Task__c,Action__c,Source_Name__c from MET_Milestone__c where Name = '123'];
            oMileStone.Task__c=null;
            oMileStone.Action__c=null;
            oMileStone.Source_Name__c =null;
            System.assertEquals(oMileStone.Case_Subtype__c,'Routine');
            Apexpages.StandardController stdController = new Apexpages.StandardController(oMileStone);            
            METMileStoneDetail_C_HUM oMileStoneDetail = new METMileStoneDetail_C_HUM(stdController);
            oMileStoneDetail.sMileStoneID = oMileStone.Id;
            oMileStoneDetail.sActionName = 'Test Action';
            oMileStoneDetail.sSourceName = 'Test Source';
            oMileStoneDetail.sCaseTypeName = oMileStone.Case_Type__c;
            oMileStoneDetail.sCaseSubtypeName = oMileStone.Case_Subtype__c;
            oMileStoneDetail.oMileStone = oMileStone;
            oMileStoneDetail.sTaskName = 'Test Task';
            PageReference oPgRef = oMileStoneDetail.SaveAndNew();
            System.assertEquals(oPgref,null);
            
            oMileStoneDetail.sActionName = 'Test Action2';
            oMileStoneDetail.sSourceName = 'Test Source2';
            oMileStoneDetail.sTaskName = 'Test Task2';
            oPgRef = oMileStoneDetail.SaveAndNew();
            System.assertEquals(oPgref,null);

            oMileStoneDetail.sActionName = 'Test Action1';
            oMileStoneDetail.sSourceName = 'Test Source1';
            oMileStoneDetail.sTaskName = 'Test Task1';
            oPgRef = oMileStoneDetail.SaveAndNew();
            System.assertNotEquals(oPgref,null);
        }
    }
    
    /*
    * Method Name : testMileStoneIdValidation
    * Description : This method is used to test the negative scenario of MileStone Id
    * Return type : void
    * Parameter   : NA
    */
    static testMethod void testMileStoneIdValidation()
    {
        User oUser = [Select Id, Current_Queue__c,Network_User_Id__c  from User where LastName = 'test User1'];
        System.assertEquals('Cincinnati CRU1', oUser.Current_Queue__c);
        System.runAs(oUser)
        {
            MET_Milestone__c oMileStone = new MET_Milestone__c();
            oMileStone.Name = 'abc';
            oMilestone.Case_Type__c = 'Customer Inquiry';
            oMileStone.Case_Subtype__c = 'Routine';
            insert oMileStone;
            MET_Task__c oTaskNew = [SELECT id, Name, Case_Subtype__c, Is_Active__c FROM MET_Task__c WHERE Name = 'Test Task'];
            Apexpages.StandardController stdController = new Apexpages.StandardController(oMileStone);
            METMileStoneDetail_C_HUM oMileStoneDetail = new METMileStoneDetail_C_HUM(stdController);
            oMileStoneDetail.sMileStoneID = oMileStone.Id;
            oMileStoneDetail.oMileStone = oMileStone;
            oMileStoneDetail.sTaskName = 'Test Task';
            PageReference oPgRef = oMileStoneDetail.Save();
            System.assertEquals(oPgRef,null);
            
            oMileStone = [SELECT id,Case_Type__c,Case_Subtype__c ,Name,Task__c,Action__c,Source_Name__c from MET_Milestone__c where Name = '123'];
            System.assertEquals(oMileStone.Case_Subtype__c,'Routine');
            oMileStone.Task__c=null;
            oMileStone.Action__c=null;
            oMileStone.Source_Name__c =null;
            oMileStone.Case_Type__c=null;
            oMileStone.Case_Subtype__c =null;
            stdController = new Apexpages.StandardController(oMileStone);            
            oMileStoneDetail = new METMileStoneDetail_C_HUM(stdController);
            oMileStoneDetail.sMileStoneID = oMileStone.Id;
            oMileStoneDetail.sActionName = null;
            oMileStoneDetail.sSourceName = null;
            oMileStoneDetail.sCaseTypeName = null;
            oMileStoneDetail.sCaseSubtypeName = null;
            oMileStoneDetail.oMileStone = oMileStone;
            oMileStoneDetail.sTaskName = null;
            oPgRef = oMileStoneDetail.SaveAndNew();
            System.assertEquals(oPgref,null);
        }
    } 

    /*
    * Method Name : testMETTriggers
    * Description : This method is used to test the functionality of MET related triggers
    * Return type : void
    * Parameter   : NA
    */
    static testMethod void testMETTriggers()
    {
        User oUser = [Select Id, Current_Queue__c,Network_User_Id__c  from User where LastName = 'test User1'];
        System.assertEquals('Cincinnati CRU1', oUser.Current_Queue__c);
        System.runAs(oUser)
        {
            insert new HUMTriggerSwitch__c (name='MET_Milestone__c', Exeute_Trigger__c=true);
            insert new HUMTriggerSwitch__c (name='MET_Task__c', Exeute_Trigger__c=true);
            insert new HUMTriggerSwitch__c (name='MET_Action__c', Exeute_Trigger__c=true);

            MET_Milestone__c upMET = [SELECT ID, Name,Case_Type__c, Case_Subtype__C,Action__c,Source_Name__c,Task__c from MET_Milestone__c where name = '123'];

            MET_Task__c oTask = new MET_Task__c();
            oTask.Name = 'Test Task2';
            oTask.Is_Active__c = false;
            oTask.Case_Subtype__c = 'Routine';
            insert oTask;
            System.assertEquals(oTask.Name,'Test Task2');

            MET_Action__c oAction = new MET_Action__c();
            oAction.Name = 'Test Action2';
            oAction.Is_Active__c = false;
            insert oAction;
            System.assertEquals(oAction.Name,'Test Action2');

            MET_Source__c oSource = new MET_Source__c();
            oSource.Name = 'Test Source2';
            oSource.Is_Active__c = true; 
            insert oSource; 
            System.assertEquals(oSource.Name,'Test Source2');

            MET_Milestone__c oMileStone = new MET_Milestone__c();
            oMileStone.Name = '1234';
            oMilestone.Case_Type__c = 'Customer Inquiry';
            oMileStone.Case_Subtype__c = 'Routine';
            oMileStone.Action__c = oAction.id;
            oMileStone.Source_Name__c =  oSource.id;
            oMileStone.Task__c =  oTask.id;
            oMileStone.Is_Active__c = false; 
            oMileStone.Is_Milestone__c = true;
            insert oMileStone;
            System.assertEquals(oMileStone.Case_Subtype__c,'Routine');

            oTask.Is_Active__c = true;
            update oTask;
            oAction.Is_Active__c = true;
            update oAction;
            oMileStone.Is_Active__c = true; 
            update oMileStone;

            upMET.Action__c = oAction.id;
            upMET.Task__c =  oTask.id;
            update upMET;

            try {
                MET_Task__c oTask1 = new MET_Task__c();
                oTask1.Name = 'Test Task2';
                oTask1.Is_Active__c = true;
                oTask1.Case_Subtype__c = 'Routine';
                insert oTask1;
                System.assertEquals(oTask1.Name,'Test Task2');                
            } catch (Exception e) {

            }
        }
    }
    static testMethod void testMETTriggersException()
    {
        User oUser = [Select Id, Current_Queue__c,Network_User_Id__c  from User where LastName = 'test User1'];
        System.assertEquals('Cincinnati CRU1', oUser.Current_Queue__c);
        System.runAs(oUser)
        {
            insert new HUMTriggerSwitch__c (name='MET_Milestone__c', Exeute_Trigger__c=true);
            insert new HUMTriggerSwitch__c (name='MET_Task__c', Exeute_Trigger__c=true);
            insert new HUMTriggerSwitch__c (name='MET_Action__c', Exeute_Trigger__c=true);

            MET_Task__c oTask = new MET_Task__c();
            oTask.Name = null;
            oTask.Is_Active__c = false;
            oTask.Case_Subtype__c = 'Routine';
            insert oTask;

            MET_Action__c oAction = new MET_Action__c();
            oAction.Name = null;
            oAction.Is_Active__c = false;
            insert oAction;

            MET_Source__c oSource = new MET_Source__c();
            oSource.Name = null;
            oSource.Is_Active__c = true; 
            insert oSource; 
            MET_Source__c oSource1 = new MET_Source__c();
            oSource1.Name = 'Test Source3';
            //oSource1.Is_Active__c = false; 
            insert oSource1; 
            System.assertEquals(oSource1.Name,'Test Source3');

            MET_Milestone__c oMileStone = new MET_Milestone__c();
            oMileStone.Name = '12345';
            oMilestone.Case_Type__c = 'Customer Inquiry';
            oMileStone.Case_Subtype__c = 'Routine';
            oMileStone.Action__c = oAction.id;
            oMileStone.Source_Name__c =  oSource.id;
            oMileStone.Task__c =  oTask.id;
            oMileStone.Is_Active__c = false; 
            oMileStone.Is_Milestone__c = true;
            oMIleStone.Met_Entries_Assoc_Order__c = null;
            insert oMileStone;
            System.assertEquals(oMileStone.Case_Subtype__c,'Routine');

            MET_Milestone__c oMileStone1 = new MET_Milestone__c();
            oMileStone1.Name = '12345';
            oMilestone1.Case_Type__c = 'Customer Inquiry';
            oMileStone1.Case_Subtype__c = 'Routine';
            oMileStone1.Action__c = oAction.id;
            oMileStone1.Source_Name__c =  oSource1.id;
            oMileStone1.Task__c =  oTask.id;
            oMileStone1.Is_Active__c = false; 
            oMileStone1.Is_Milestone__c = true;
            oMIleStone1.Met_Entries_Assoc_Order__c = null;
            insert oMileStone1;
            System.assertEquals(oMileStone1.Case_Subtype__c,'Routine');

            oMileStone.Is_Active__c = true;
            update oMileStone;
            oMileStone1.Name = null;
            update oMileStone1;

            Apexpages.StandardController stdController = new Apexpages.StandardController(oMileStone);
            METMileStoneDetail_C_HUM oMileStoneDetail = new METMileStoneDetail_C_HUM(stdController);
            oMileStoneDetail.sMileStoneID = ApexPages.currentPage().getParameters().put('id', oMileStone.Id);
            oMileStoneDetail.sActionName = 'oMileStone';
            oMileStoneDetail.sSourceName = 'oMileStone';
            oMileStoneDetail.sCaseTypeName = 'oMileStone';
            oMileStoneDetail.sCaseSubtypeName = 'oMileStone';
            oMileStoneDetail.oMileStone = oMileStone;
            oMileStoneDetail.sTaskName = 'Test';
            oMileStoneDetail.oMileStone = new MET_Milestone__c();
            PageReference oPgRef = oMileStoneDetail.Save();

        }
    }

    static testMethod void testMETTriggersException1()
    {
        User oUser = [Select Id, Current_Queue__c,Network_User_Id__c  from User where LastName = 'test User1'];
        System.assertEquals('Cincinnati CRU1', oUser.Current_Queue__c);
        System.runAs(oUser)
        {
            insert new HUMTriggerSwitch__c (name='MET_Milestone__c', Exeute_Trigger__c=true);
            insert new HUMTriggerSwitch__c (name='MET_Task__c', Exeute_Trigger__c=true);
            insert new HUMTriggerSwitch__c (name='MET_Action__c', Exeute_Trigger__c=true);

            MET_Task__c oTask = [SELECT id from MET_Task__c where name = 'Test Task'];
            //MET_Action__c oAction = [SELECT id from MET_Action__c where name = 'Test Action'];
            MET_Source__c oSource = [SELECT id from MET_Source__c where name = 'Test Source'];

            MET_Action__c oAction = new MET_Action__c();
            oAction.Name = 'Test Action4';
            oAction.Is_Active__c = false;
            insert oAction;

            MET_Milestone__c oMileStone = new MET_Milestone__c();
            oMileStone.Name = '123';
            oMilestone.Case_Type__c = 'Customer Inquiry';
            oMileStone.Case_Subtype__c = 'Dual Eligible Auto Assign';
            oMileStone.Action__c = oAction.id;
            oMileStone.Source_Name__c =  oSource.id;
            oMileStone.Task__c =  oTask.id;
            oMileStone.Is_Active__c = true; 
            oMileStone.Is_Milestone__c = true;
            insert oMileStone;
        }
    }
}