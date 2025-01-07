/***************************************************************************************************************************************************
Apex Class Name  : OmniRuleCriteria_T_HUM
Version          : 1.0
Created Date     : 12/22/2017
Function         : Test Class for OmniRuleCriteria_X_HUM
****************************************************************************************************************************************************
Modification Log:
* Developer Name            Review Number            Date               Description
----------------------------------------------------------------------------------------------------------------------------------------------------
* Mohammed Noor             161414                  12/22/2017          Original Version 

****************************************************************************************************************************************************/
@isTest
private class OmniRuleCriteria_T_HUM 
{
	@testSetup static void setUpTestData()
    {
		User objUser = HUMTestDataHelperTest.createTestUser('User1','System Administrator');
        insert objUser;
        
        User objUser2 = HUMTestDataHelperTest.createTestUser('User2','Customer Care Specialist');
        insert objUser2;
                
        
        PermissionSet perm = [SELECT Name, Label, Id FROM PermissionSet where name = :GLOBAL_CONSTANT_HUM.METENROLLMENTMEDICAREEDIT_PERMISSIONSET_HUM Limit 1];
        insert new PermissionSetAssignment(AssigneeId = objUser2.id, PermissionSetId = perm.id);        
        
        system.assertEquals(objUser.lastname=='test User1',true);
        
        system.runAs(objUser)
        {             
            //create OmniQueue
            Group oQueue = new Group();
            oQueue.name = 'Test omni Queue';
            oQueue.type = 'Queue';
            insert oQueue;
             
            //Create StaginQueue
            Group sQueue = new Group();
            sQueue.name = 'Test omni Queue_Stg';
            sQueue.type = 'Queue';
            insert sQueue;             
                          						             
			OmniQueue__c omniData = new OmniQueue__c();
            omniData.Name = 'Test Omni Queue';
            omniData.Maximum_Cases__c = 50;
            omniData.Omni_Queue_ID__c = oQueue.Id;
            omniData.Staging_Queue_ID__c = sQueue.Id;
            omniData.Staging_Queue_Name__c = sQueue.Name;            
            insert omniData;                        
            
            Omni_Case_Assignment_Rule__c testCaseAssignmentRule = new Omni_Case_Assignment_Rule__c(); 
            testCaseAssignmentRule.Name = 'Test Case Assignment Rule';
            testCaseAssignmentRule.Evaluation_Order__c = 1;
            testCaseAssignmentRule.Is_Active__c = true;
            testCaseAssignmentRule.Omni_Queue__c = omniData.Id;
            insert testCaseAssignmentRule;
            
            Omni_Rule_Criteria__c testCriteria = new Omni_Rule_Criteria__c();
            testCriteria.Criteria_Field__c = 'Test Field';
            testCriteria.Criteria_Operator__c = 'equals';
            testCriteria.Criteria_Field_Value__c = 'Test Value';
            testCriteria.Omni_Case_Assignment_Rule__c = testCaseAssignmentRule.Id;
            insert testCriteria;
                        
        }
        
    }
    
    /*
    * This method is used to test the Detail page
    * @param   None
    * @return  None
    */
    static testMethod void testNewOmniQueue()
    {
    	User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test User1'];
    	User testUser2 = [Select Id,Name, Current_Queue__C from User where LastName='test User2'];    	
        
        System.runAs(testUser2)
		{
        	Test.startTest();			
            Test.setCurrentPage(page.OmniRuleCriteria_VF_HUM);
            Omni_Case_Assignment_Rule__c newOmniCAR = new Omni_Case_Assignment_Rule__c();
            newOmniCAR = [SELECT Id, Name, Evaluation_Order__c, Is_Active__c, Omni_Queue__c FROM Omni_Case_Assignment_Rule__c Limit 1];			
            ApexPages.StandardController scon = new ApexPages.StandardController(newOmniCAR);                 
            OmniRuleCriteria_X_HUM oRuleCriteriaCon = new OmniRuleCriteria_X_HUM(scon);
            			                   
            oRuleCriteriaCon.getRuleCriterias();
            
            System.assertNotEquals(oRuleCriteriaCon.jsonData, null);
            
            Test.stopTest();               
		}	
    } 
    

}