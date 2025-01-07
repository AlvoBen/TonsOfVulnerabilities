/*
  Apex Class Name  : Omni_CaseAssignmentRuleEdit_T_HUM   
  Version          : 1.0 
  Created Date     : OCT 25 2017
  Function         : Test class for Omni_CaseAssignmentRuleEdit_X_HUM,Omni_CaseAssignmentRuleEdit_S_HUM,Omni_CaseAssignmentRuleEdit_D_HUM 
*********************************************************************************************************************************************************************************
Modification Log:

* Modification Id           Developer Name             Review Number               Date                       Description
*---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* 1.0                       Syed Mubarak T             163314                   12/27/2017                      Original Version
* 1.1                       Mohammed Noor              170613                   01/16/2017                      Increase Code Coverage and add Assert Statements.
* 1.2                       Mohammed Noor              219044                   04/23/2017                      Update Test Classes for  Defect# 354993 Fix.
* Syed Mubarak T									03/19/2019			  REQ - 386810 - Omni Configuration Item changes
* Syed Mubarak T									03/31/2019			  REQ - 386735 - Adding OR Operator to Rule Criteria
* Pooja Kumbhar										04/29/2020		      Checkmarx TestMethod without assert error fix.
*---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/

@isTest
private class Omni_CaseAssignmentRuleEdit_T_HUM
{
    /*
    * This method Is to setup test data
    * @param NA
    * @return NA
    */
    
     @testSetup static void setUpTestData()
    {
        User objUser = HUMTestDataHelperTest.createTestUser('User1','System Administrator');
        insert objUser;
        User objUser2 = HUMTestDataHelperTest.createTestUser('User2','Customer Care Supervisor');
        insert objUser2;
        PermissionSet ps = [SELECT ID From PermissionSet WHERE Name = 'CRMS_605_BizConfig_MedicareEnrollment_Edit'];
        insert new PermissionSetAssignment(AssigneeId = objUser2.id, PermissionSetId = ps.Id );
        system.assertEquals(objUser.lastname=='test User1',true);
        
        system.runAs(objUser)
        {
                       
             Group oGrp = NULL; 
            oGrp=new Group(Name='Cincinnati_Calls1', Type='QUEUE');
            insert oGrp;
            
            Group queueData = new Group();
             queueData.name = 'Test omni Queue';
             queueData.type = 'Queue';
             insert queueData;

            QueuesObject oQueueObject = new QueuesObject (QueueID = oGrp.id, SobjectType ='Work_Queue_Setup__c');
            insert oQueueObject ; 
            
            OmniQueue__c oQueue = new OmniQueue__c();
            oQueue.Name = 'test Queue1';
            oQueue.Maximum_Cases__c = 100;
            oQueue.Omni_Queue_ID__c = queueData.Id;
            insert oQueue;
            
            Work_Queue_Setup__c oWQS = new Work_Queue_Setup__c(Public_Group_Name__c = 'Group one', Service_Center__c = 'Cincinnati', Department__c = 'Calls1', Name = 'Cincinnati_Calls1');
            oWQS.OwnerId = oGrp.Id;
            insert oWQS;
            System.assert(oWQS != null);
            System.assertEquals(oWQS.Public_Group_Name__c, 'Group one');
            
            Queue_View__c qView = new Queue_View__c();
            qView.Name = 'work_queue_view';
            qView.IsActive__c = true;
            qView.Work_Queue_Name__c = oWQS.Id;
            insert qView;
        }
        
    }
    
    static testMethod void testOmni_CaseAssignmentRuleEditConstructor()
    {
         User testUser = [Select Id,Name, LastName, Current_Queue__C from User where LastName='test User2'];
         System.assertEquals(testUser.LastName, 'test User2');
            System.runAs(testUser)
            {
                Test.startTest();
                Test.setCurrentPage(page.Omni_CaseAssignmentRuleEdit_VF_HUM);
                Omni_Routing_Configuration__c oData =  new Omni_Routing_Configuration__c();
                ApexPages.StandardController scon = new ApexPages.StandardController(oData);
                Omni_CaseAssignmentRuleEdit_X_HUM oCaseAssignment = new Omni_CaseAssignmentRuleEdit_X_HUM(scon);
                oCaseAssignment.getRuleCriterias();
                oCaseAssignment.saveRecord();
				oCaseAssignment.selectedObjectName = 'Case';
				oCaseAssignment.getFieldsListJSON();
                Test.stopTest();
                
            }
    }
    
    static testMethod void testSaveMethod()
    {
         User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test User2'];
            System.runAs(testUser)
            {
                Test.startTest();
                
                // Case Assignment Rule Id is null
                Test.setCurrentPage(page.Omni_CaseAssignmentRuleEdit_VF_HUM);
                Omni_Case_Assignment_Rule__c oData =  new Omni_Case_Assignment_Rule__c();
                ApexPages.StandardController scon = new ApexPages.StandardController(oData);
                Omni_CaseAssignmentRuleEdit_X_HUM oCaseAssignment = new Omni_CaseAssignmentRuleEdit_X_HUM(scon);
                oCaseAssignment.omniRule.Evaluation_Order__c = null;
				oCaseAssignment.bUnsavedRCError = false;
                oCaseAssignment.saveRecord();
                
                
                OmniQueue__c oQueue = [SELECT Id FROM OmniQueue__c WHERE Name = 'test Queue1' LIMIT 1];
                Work_Queue_Setup__c oWQS = [SELECT Id FROM Work_Queue_Setup__c WHERE Name ='Cincinnati_Calls1' LIMIT 1];
                Queue_View__c qView = [SELECT Id FROM Queue_View__c WHERE Name = 'work_queue_view' LIMIT 1];
                
                Omni_Case_Assignment_Rule__c oCAR = new Omni_Case_Assignment_Rule__c();
                oCAR.Name =  'Test Case Assignment Rule';
                oCAR.Is_Active__c = true;
                oCAR.WorkQueue__c = oWQS.Id;
                oCAR.WorkQueueView__c = qView.Id;
                oCAR.Omni_Queue__c = oQueue.Id;
                oCAR.Evaluation_Order__c = 1;
                insert oCAR;
                
                List<Omni_Rule_Criteria__c> JSONRuleCriteria = new List<Omni_Rule_Criteria__c>();
                
                Omni_Rule_Criteria__c oRCriteria = new Omni_Rule_Criteria__c();
                oRCriteria.Criteria_Field__c = 'Type';
                oRCriteria.Criteria_Field_Value__c = 'test Type';
                oRCriteria.Criteria_Operator__c = 'Equals';
                oRCriteria.Omni_Case_Assignment_Rule__c = oCAR.Id;
				oRCriteria.Criteria_Set__c = '1';
                
                Omni_Rule_Criteria__c oRCriteria2 = new Omni_Rule_Criteria__c();
                oRCriteria2.Criteria_Field__c = 'SubType__c';
                oRCriteria2.Criteria_Field_Value__c = 'test Sub Type';
                oRCriteria2.Criteria_Operator__c = 'Equals';
                oRCriteria2.Omni_Case_Assignment_Rule__c = oCAR.Id;
				oRCriteria2.Criteria_Set__c = '1,2';
                
                JSONRuleCriteria.add(oRCriteria);
                JSONRuleCriteria.add(oRCriteria2);
                
                oCaseAssignment.updtCriteriaJSON = JSON.serialize(JSONRuleCriteria);
                oCaseAssignment.omniRule.Name = 'Test Case Assignment Rule';
                oCaseAssignment.omniRule.Omni_Queue__c = oQueue.Id;
                oCaseAssignment.omniRule.Evaluation_Order__c = 1;
                oCaseAssignment.saveRecord();
                
                
                // Case Assignment Rule Id is not null
                Test.setCurrentPage(page.Omni_CaseAssignmentRuleEdit_VF_HUM);
                Omni_Case_Assignment_Rule__c oData2 =  [SELECT Id,Name,Omni_Queue__c,Evaluation_Order__c, Case_Assignment_Rule_Criteria__c FROM Omni_Case_Assignment_Rule__c WHERE Name = 'Test Case Assignment Rule' LIMIT 1];
                ApexPages.StandardController scon2 = new ApexPages.StandardController(oData2);
                Omni_CaseAssignmentRuleEdit_X_HUM oCaseAssignment2 = new Omni_CaseAssignmentRuleEdit_X_HUM(scon2);
                
                oCaseAssignment2.updtCriteriaJSON = JSON.serialize(JSONRuleCriteria);
                oCaseAssignment2.omniRule.Name = 'Test Case Assignment Rule2';
                oCaseAssignment2.omniRule.Omni_Queue__c = oQueue.Id;
                oCaseAssignment2.omniRule.Evaluation_Order__c = 1;
				oCaseAssignment2.bUnsavedRCError = false;
				oCaseAssignment2.saveRecord();
                oCaseAssignment2.save();
                oCaseAssignment2.getRuleCriterias();
				
				System.assertNotEquals(null,oCaseAssignment2.omniRule);
                System.assertEquals(false,String.isBlank(oCaseAssignment2.omniRule.Case_Assignment_Rule_Criteria__c));
                List<Omni_Rule_Criteria__c> ruleCriteria1 = [Select Id, Name, Omni_Case_Assignment_Rule__c  from Omni_Rule_Criteria__c where Omni_Case_Assignment_Rule__c = :oCaseAssignment2.omniRule.Id];
				System.assertNotEquals(true, ruleCriteria1.isEmpty());
				System.assertEquals(2, ruleCriteria1.size());
				                
                oCaseAssignment2.updtCriteriaJSON = JSON.serialize(JSONRuleCriteria);
                oCaseAssignment2.omniRule.Name = 'Test Case Assignment Rule2';
                oCaseAssignment2.omniRule.Omni_Queue__c = oQueue.Id;
                oCaseAssignment2.omniRule.Evaluation_Order__c = 4;
                oCaseAssignment2.saveRecord();
				
				System.assertNotEquals(null,oCaseAssignment2.omniRule);
                System.assertEquals(false,String.isBlank(oCaseAssignment2.omniRule.Case_Assignment_Rule_Criteria__c));
                ruleCriteria1 = [Select Id, Name, Omni_Case_Assignment_Rule__c  from Omni_Rule_Criteria__c where Omni_Case_Assignment_Rule__c = :oCaseAssignment2.omniRule.Id];
				System.assertNotEquals(true, ruleCriteria1.isEmpty());
				System.assertEquals(4, ruleCriteria1.size());				
                
                // Case Assignment Rule Id is null - to evaluate Evaluation_Order__c
                Omni_Case_Assignment_Rule__c oData3 =  new Omni_Case_Assignment_Rule__c();
                ApexPages.StandardController scon3 = new ApexPages.StandardController(oData3);
                Omni_CaseAssignmentRuleEdit_X_HUM oCaseAssignment3 = new Omni_CaseAssignmentRuleEdit_X_HUM(scon3);
                oCaseAssignment3.updtCriteriaJSON = JSON.serialize(JSONRuleCriteria);
                oCaseAssignment3.omniRule.Name = 'Test Case Assignment Rule3';
                oCaseAssignment3.omniRule.Omni_Queue__c = oQueue.Id;
                oCaseAssignment3.omniRule.Evaluation_Order__c = 5;
				oCaseAssignment3.bUnsavedRCError = false;
                oCaseAssignment3.saveRecord();
				
				System.assertNotEquals(null,oCaseAssignment3.omniRule);
                System.assertEquals(false,String.isBlank(oCaseAssignment3.omniRule.Case_Assignment_Rule_Criteria__c));
                ruleCriteria1 = [Select Id, Name, Omni_Case_Assignment_Rule__c  from Omni_Rule_Criteria__c where Omni_Case_Assignment_Rule__c = :oCaseAssignment3.omniRule.Id];
				System.assertNotEquals(true, ruleCriteria1.isEmpty());
				System.assertEquals(2, ruleCriteria1.size());
				
				//Test for no Rule Criterias
                String emptyJSON = null;                
                oCaseAssignment3.updtCriteriaJSON = JSON.serialize(emptyJSON);                
                oCaseAssignment3.saveRecord();
 				                                                               
                System.assertEquals(true,String.isBlank(oCaseAssignment3.omniRule.Case_Assignment_Rule_Criteria__c));                
				ruleCriteria1 = [Select Id, Name, Omni_Case_Assignment_Rule__c  from Omni_Rule_Criteria__c where Omni_Case_Assignment_Rule__c = :oCaseAssignment3.omniRule.Id];				
				System.assertEquals(false, ruleCriteria1.isEmpty());				
				
				oCaseAssignment3.bNameError = true;
				oCaseAssignment3.bEvalOrderError = true;
				oCaseAssignment3.saveRecord();
				System.assertEquals(true,oCaseAssignment3.bError);
				
                oCaseAssignment3.showErrorMessage(); 				
            }
    }
}