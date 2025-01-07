/***********************************************************************************************************
Apex Class Name  : HUMPolicyNameWrapperHelperTest
Version          : 1.0
Created Date     : Sep 27 2014
Function         : Test class for HUMPolicyNameWrapperHelper 
************************************************************************************************************

Modification Log:

* Developer Name                Code Review           Date                       Description
*-----------------------------------------------------------------------------------------------------------
* Asha Deepika Koyyana          18256                 09/27/2014                 Original Version 
* Asha Deepika Koyyana          18447                 10/09/2014                 Updated as per defect fix(#157315)  
* Manish Kumar                 	19728                 12/05/2014                 Fixed production error 
* Harika Devi Kalidindi         112000                07/20/2017                 REQ - 323435 :Added Policy_Group_Number__c field value while inserting policy
************************************************************************************************************/
@isTest
private class HUMPolicyNameWrapperHelperTest
{
    /*
    * Method Name   :    testPolicyWrapper
    * Description   :    This method is used to call HUMPolicyNameWrapperHelper constructor
    * Return Type   :    NA
    * Parameters    :    NA
    */
    static testMethod void testPolicyWrapper()
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertNotEquals(oCCSUser, NULL, 'Value not matched');
        
        System.runAs ( oCCSUser )
        {
            Account oAccount = new Account(Name ='Test Account');
            insert oAccount;
            system.assertNotEquals(oAccount,null,'Account not Inserted');
            
            Account oAccountGroup = new Account(Name ='Test Group');
            insert oAccountGroup;
            System.assertNotEquals(oAccountGroup, NULL, 'Account is not inserted') ;
            
            Policy__c oPolicy = new Policy__c(name = 'Test Policy', Group_Name__c = oAccountGroup.Id,Policy_Group_Number__c='11111');
            insert oPolicy; 
            System.assertNotEquals(oPolicy, NULL, 'Policy is not inserted');
            
            Policy_Member__c oPolicyMem = new Policy_Member__c(name = 'Test Policy Mem', Policy__c = oPolicy.Id, Member__c = oAccount.id );
            insert oPolicyMem; 
            system.assertNotEquals(oPolicyMem,null,'PolicyMember not Inserted');
            
            test.startTest();
            HUMPolicyNameWrapperHelper oPolicyNameWrapper = new HUMPolicyNameWrapperHelper (oPolicyMem);
            test.stopTest();
            
            System.assertNotEquals(oPolicyNameWrapper , NULL, 'Object is not instantiated');
        }  
    }
}