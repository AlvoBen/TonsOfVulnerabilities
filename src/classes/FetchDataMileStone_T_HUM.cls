/***************************************************************************************************
@ Author Prasanthi Kandula
@ Name FetchDataMileStone_T_HUM
@ CreateDate 01/06/2017
@ Description Class contains test methods for FetchDataMileStone_D_HUM controller
@ Version 1.0
@ Reference NA

Modification Log :
* Developer              Code Review           Date                      Description
* -----------------------------------------------------------------------------------------------                 
* Prasanthi Kandula         55297            01/06/2017                Original Version
* Manoj Chaurasia           202594           03/29/2018                REQ - 354470 Ability to display TAS combination in CRM
***************************************************************************************************/
@isTest
private class FetchDataMileStone_T_HUM
{
    @testSetup static void setup() 
    {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        oUser.Current_Queue__c = 'Cincinnati CRU1';
        oUser.Network_User_Id__c = 'SXS2222';
        insert oUser;
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
    * Method Name : testQueryMileStone
    * Description : This method is used to test the Data Layer controller
    * Return type : void
    * Parameter   : NA
    */
    static testMethod void testQueryMileStone()
    {
        User oUser = [Select Id, Current_Queue__c,Network_User_Id__c  from User where LastName = 'test User1'];
        System.assertEquals('Cincinnati CRU1', oUser.Current_Queue__c);
        System.runAs(oUser)
        {
            MET_Milestone__c oMileStone = [SELECT id,Case_Subtype__c ,Name,Task__c,Action__c,Source_Name__c from MET_Milestone__c where Name = '123'];
            MET_Task__c oTaskNew = [SELECT id, Name, Case_Subtype__c, Is_Active__c FROM MET_Task__c WHERE Name = 'Test Task'];
            MET_Action__c  oActionDuplicate = [SELECT id, Name, Is_Active__c FROM MET_Action__c  WHERE Name = 'Test Action1'];
            MET_Source__c oSource = [SELECT id, Name, Is_Active__c FROM MET_Source__c WHERE Name = 'Test Source'];
            System.assertEquals(oActionDuplicate.Name, 'Test Action1');
            FetchDataMileStone_D_HUM oFetchData = new FetchDataMileStone_D_HUM();
            List<MET_Task__c> lstFetchTask = oFetchData.queryTaskData('Routine','Test','Name','ASC');
            System.assertEquals(lstFetchTask[0].Name,'Test Task');
            oFetchData.queryActionDataWithoutTask('Test Action1','Name','DESC');
            oFetchData.querySourceDataWithoutAction('Test Source','Name','ASC');
            MET_Milestone__c oMileStoneId = oFetchData.queryMileStoneData(oMileStone.id);
            System.assertEquals(oMileStoneId.Case_Subtype__c,'Routine');
        }
        
    }
    
    /*
    * Method Name : testQueryActionData
    * Description : This method is used to test the Actionn Method
    * Return type : void
    * Parameter   : NA
    */
    static testMethod void testQueryActionData()
    {
        User oUser = [Select Id, Current_Queue__c,Network_User_Id__c  from User where LastName = 'test User1'];
        System.assertEquals('Cincinnati CRU1', oUser.Current_Queue__c);
        System.runAs(oUser)
        {
            MET_Milestone__c oMileStone = [SELECT id,Case_Subtype__c ,Name,Task__c,Action__c,Source_Name__c from MET_Milestone__c where Name = '123'];
            MET_Task__c oTaskNew = [SELECT id, Name, Case_Subtype__c, Is_Active__c FROM MET_Task__c WHERE Name = 'Test Task'];
            MET_Action__c  oAction = [SELECT id, Name, Is_Active__c FROM MET_Action__c  WHERE Name = 'Test Action1'];
            Set<Id> setActionId = new Set<Id>();
            setActionId.add(oAction.id);
            MET_Source__c oSource = [SELECT id, Name, Is_Active__c FROM MET_Source__c WHERE Name = 'Test Source'];
            Set<Id> setSourceId = new Set<Id>();
            setSourceId.add(oSource.id);
            System.assertEquals(oAction.Name, 'Test Action1');
            FetchDataMileStone_D_HUM oFetchData = new FetchDataMileStone_D_HUM();
            List<MET_Action__c> lstAction = oFetchData.queryActionData(setActionId ,'Name','ASC');
            System.assertEquals(lstAction[0].Name,'Test Action1');
            List<MET_Source__c> lstSource =  oFetchData.querySourceData(setSourceId,'Name','ASC');
            System.assertEquals(lstSource[0].Name,'Test Source');
            
        }
    }
}