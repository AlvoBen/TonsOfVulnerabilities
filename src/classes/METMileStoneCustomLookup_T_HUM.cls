/***************************************************************************************************
 @Author Prasanthi Kandula
 @Name METMileStoneCustomLookup_T_HUM
 @CreateDate 01/03/2017
 @Description Class contains test methods for METMileStoneCustomLookup_C_HUM controller
 @Version 1.0
 @Reference NA
   
 Modification Log :
 * Developer              Code Review           Date                      Description
 * -----------------------------------------------------------------------------------------------                 
 * Prasanthi Kandula        55297             01/03/2017                Original Version
 * Manoj Chaurasia          202594            03/29/2018              REQ - 3544700 Ability to display TAS combination in CRM
***************************************************************************************************/

@isTest
private class METMileStoneCustomLookup_T_HUM
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
        }
    }
    
    /*
    * Method Name : testTaskLookup
    * Description : This method is used to create test data for Task Lookup 
    * Return type : void
    * Parameter   : NA
    */
    static testMethod void testTaskLookup()
    {
        User oUser = [Select Id, Current_Queue__c,Network_User_Id__c  from User where LastName = 'test User1'];
        System.assertEquals('Cincinnati CRU1', oUser.Current_Queue__c);
        System.runAs(oUser)
        {
            MET_Task__c oTask = [SELECT id, Name, Case_Subtype__c, Is_Active__c FROM MET_Task__c WHERE Name = 'Test Task'];
            System.assertEquals(oTask.Name,'Test Task');
            
            METMileStoneCustomLookup_C_HUM oCustomlookupController = new METMileStoneCustomLookup_C_HUM();
            oCustomlookupController.sCaseSubType = 'Routine';
            oCustomlookupController.sTaskId = oTask.Name;
            oCustomlookupController.searchString = 'Test';
            oCustomlookupController.sObjectName = 'Task';
            oCustomlookupController.setSortDirection('ASC');
            System.assertEquals('ASC',oCustomlookupController.getSortDirection());
            oCustomlookupController.getSortDirection();
            oCustomlookupController.sSortExpression = 'Name';
            oCustomlookupController.fetchDataForMileStone();
            oCustomlookupController.previous();
            oCustomlookupController.first();
            oCustomlookupController.last();
            oCustomlookupController.next();
            boolean bisNext = oCustomlookupController.hasNext;
            boolean bisPrevious =oCustomlookupController.hasPrevious;
            System.assertEquals(bisPrevious, false);
            List<MileStoneWrappper_DTO_HUM> lstWrapper = oCustomlookupController.getlstWrapperData();
            System.assertEquals(lstWrapper[0].sName ,'Test Task');
            System.assertEquals(lstWrapper[0].sID,oTask.Id);
            oCustomlookupController.stdsetcon =null;
            System.assertEquals(null,oCustomlookupController.hasNext);
            System.assertEquals(null,oCustomlookupController.hasPrevious );
            
        }
     }
    
    /*
    * Method Name : testActionLookup
    * Description : This method is used to create test data for Action Lookup 
    * Return type : void
    * Parameter   : NA
    */ 
    static testMethod void testActionLookup()
    {
        User oUser = [Select Id, Current_Queue__c,Network_User_Id__c  from User where LastName = 'test User1'];
        System.assertEquals('Cincinnati CRU1', oUser.Current_Queue__c);
        System.runAs(oUser)
        {
            MET_Task__c oTask = [SELECT id, Name, Case_Subtype__c, Is_Active__c FROM MET_Task__c WHERE Name = 'Test Task'];
            MET_Action__c  oAction = [SELECT id, Name, Is_Active__c FROM MET_Action__c WHERE Name = 'Test Action'];
                      
            METMileStoneCustomLookup_C_HUM oCustomlookupController = new METMileStoneCustomLookup_C_HUM();
            oCustomlookupController.sCaseSubType = 'Routine';
            oCustomlookupController.sTaskId = oTask.Name;
            oCustomlookupController.searchString = 'Test';
            oCustomlookupController.sObjectName = 'Action';
            oCustomlookupController.fetchDataForMileStone();
            System.assertEquals(2,oCustomlookupController.lstsObject.size());
            oCustomlookupController.sTaskId = '';
            oCustomlookupController.fetchDataForMileStone();
            System.assertEquals(2,oCustomlookupController.lstsObject.size());
            
        }
     }
     
    /*
    * Method Name : testSourceLookup
    * Description : This method is used to create test data for Source Lookup 
    * Return type : void
    * Parameter   : NA
    */ 
    static testMethod void testSourceLookup()
    {
        User oUser = [Select Id, Current_Queue__c,Network_User_Id__c  from User where LastName = 'test User1'];
        System.assertEquals('Cincinnati CRU1', oUser.Current_Queue__c);
        System.runAs(oUser)
        {
            MET_Task__c oTask = [SELECT id, Name, Case_Subtype__c, Is_Active__c FROM MET_Task__c WHERE Name = 'Test Task'];
            MET_Action__c  oAction = [SELECT id, Name, Is_Active__c FROM MET_Action__c WHERE Name = 'Test Action'];
            MET_Source__c oSource = [SELECT id, Name, Is_Active__c FROM MET_Source__c WHERE Name = 'Test Source'];
            MET_Milestone__c oMetMilestone = [SELECT id, Action__c, Source_Name__c, Is_Active__c FROM MET_Milestone__c WHERE Action__c = :oAction.id];
            METMileStoneCustomLookup_C_HUM oCustomlookupController = new METMileStoneCustomLookup_C_HUM();
            oCustomlookupController.sCaseSubType = 'Routine';
            oCustomlookupController.sTaskId = oTask.Name;
            oCustomlookupController.searchString = 'Test';
            oCustomlookupController.sActionId = oAction.Name;
            oCustomlookupController.sObjectName = 'Source';
            oCustomlookupController.fetchDataForMileStone();
            System.assertEquals(2,oCustomlookupController.lstsObject.size());
            oCustomlookupController.sActionId = '';
            oCustomlookupController.fetchDataForMileStone();
            System.assertEquals(2,oCustomlookupController.lstsObject.size());
            
        }
     }
    }