/****************************************************************************************************
Apex Class Name  : UnifiedSearchUtility_T_HUM
Version          : 1.0
Created Date     : 6/25/2021
Function         : Test class for UnifiedSearchUtility_H_HUM
*****************************************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*--------------------------------------------------------------------------------------------------
*  Lakshmi Madduri                             6/25/2021                original Version
*  Lakshmi Madduri                             10/09/2021               Cover Exception block in test class
*****************************************************************************************************/
@isTest
private class UnifiedSearchUtility_T_HUM 
{
    @testsetup
    static void testData()
    {
        User objUser = HUMTestDataHelperTest.createTestUser('unifiedsearchutility','Customer Care Specialist');
        insert objUser; 
        system.assertEquals('test unifiedsearchutility', objUser.LastName);
        CRMFunctionality_ONOFF_Switch__c oSwitch = new CRMFunctionality_ONOFF_Switch__c(Name='Unified Search Switch',IsON__c=true);
        insert oSwitch;
        system.assertEquals(true, oSwitch.IsON__c);
    }  
    @isTest
    static void testEligibilityPositive()
    {
        User objUser = [SELECT Id,LastName from User where LastName = 'test unifiedsearchutility'];
        PermissionSet objPermission = [SELECT Id,Name From PermissionSet where Name='CRMS_310_UnifiedDocumentSearch'];
        PermissionSetAssignment objAssignment = new PermissionSetAssignment(
            AssigneeId = objUser.Id,
            PermissionSetId = objPermission.Id
        );
        insert objAssignment;
        system.assertEquals(objUser.Id, objAssignment.AssigneeId);
        System.runAs(objUser)
        {   
            Test.startTest();            
            Boolean isResult = UnifiedSearchUtility_H_HUM.isUnifiedSearchEligible();
            system.assertEquals(true, isResult);
            Test.stopTest();
        }
    }
    @isTest
    static void testEligibility()
    {
        User objUser = [SELECT Id,LastName from User where LastName = 'test unifiedsearchutility'];
        System.runAs(objUser)
        {   
            Test.startTest();
            Boolean isResult = UnifiedSearchUtility_H_HUM.isUnifiedSearchEligible();
            system.assertEquals(false, isResult);
            Test.stopTest();
        }
    }
    @isTest
    static void testException()
    {
        User objUser = [SELECT Id,LastName from User where LastName = 'test unifiedsearchutility'];
        System.runAs(objUser)
        {   
            Test.startTest();
            UnifiedSearchUtility_H_HUM.sCoverException = true;
            Boolean isResult = UnifiedSearchUtility_H_HUM.isUnifiedSearchEligible(); 
            system.assertEquals(false, isResult);
            Test.stopTest();
        }
    }
}