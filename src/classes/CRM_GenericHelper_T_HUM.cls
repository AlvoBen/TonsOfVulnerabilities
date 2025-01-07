/*****************************************************************************************************************************************************************************
Apex Class Name : CRM_GenericHelper_T_HUM
Version         : 1.0
Created On      : 09/20/2021
Test Class      : CRM_GenericHelper_T_HUM
Modification Log: 

* Developer Name               Code Review                         Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
*T Sai Kumar                                                 09/20/2021             Generic method for FLS security check.
* Nikhil M												     12/17/2021             Refactored code because of REQ - 2571380.
*Nikhil M													 2/24/2022				REQ - 2824031 Assigning CDO Administrator Permission set to test User.
*T. Sai Kumar												 4/5/2022				Added new methods to cover FLS check returning string as a parameter.
*Arpit Jain                                                  02/23/2024				US-5604142 Changed all the reference of CDO to Storefront
**************************************************************************************************************************************************/
@isTest
private class CRM_GenericHelper_T_HUM {
    
     /**
    * Test Setup method to create all the data required for testing.
    *
    * @param     NULL
    * @return    void 
    */
    @testSetup static void initialize()
    {
        list<User> userList = new list<User>();
        userList.add(HUMTestDataHelperTest.createTestUser('UserRetail','CRMRetail User'));
        userList.add(HUMTestDataHelperTest.createTestUser('otherProfileUser','Standard Platform User'));
        insert userList;
        System.assert(userList.size() == 2);
        
        PermissionSet SuperAdmin = [SELECT Id FROM PermissionSet WHERE Name =: GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_400SUPERADMIN];
        List<PermissionSetAssignment> psAssign = new List<PermissionSetAssignment>();
        
        PermissionSetAssignment supAdmin = new PermissionSetAssignment(AssigneeId = userList[0].id, PermissionSetId = SuperAdmin.Id);
        psAssign.add(supAdmin);
        insert psAssign;
        
        system.assertEquals(psAssign.size(),1);
        system.assertEquals(psAssign[0].AssigneeId, userList[0].id); 
        
        System.runAs (userList[0]) 
        {
            Id oCRMRetailVisitorType = Schema.SObjectType.Account.getRecordTypeInfosByName().get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_RETAIL_VISITOR).getRecordTypeId();
            
            List<Account> lstOfAccounts = new List<Account>();
            Date waiverDate = Date.today()-100;        
            Account oAcc = CRMRetail_TestDataFactory_H_HUM.createAccountInstance('GC7654321', oCRMRetailVisitorType,Date.newInstance(1984, 01, 01),'01/01/1984', 'Test', 'Visitor_Account', 'O', '1060920019',false, 'New York', '56605', '(123) 123-1231','test1234',waiverDate,'confirmed-veteran',NULL);
            oAcc.PersonMailingPostalCode = '12345';
            oAcc.Birthdate__c = '01/02/1999';
            lstOfAccounts.add(oAcc);
            insert lstOfAccounts;
            system.assertEquals(1,lstOfAccounts.size());
        }
    }
    
     /**
    * Test method to check the Object Access for the user.
    *
    * @param     NULL
    * @return    void 
    */
    
    @isTest static void testObjectFLSAccess()
    {
        User testUser = [Select Id,LastName from User where LastName='test otherProfileUser' Limit 1 ]; 
        system.assertEquals('test otherProfileUser', testUser.LastName);
        system.runas(testUser)
        {
            test.startTest();
            Storefront_I_Type__c interactionType = new Storefront_I_Type__c();
            List<Storefront_I_Type__c> InteractionTypeList = new List<Storefront_I_Type__c>();
            interactionType =  CRMRetail_TestDataFactory_H_HUM.createInteractionType('Inquiry');
            InteractionTypeList.add(interactionType);
            System.assertEquals(1,InteractionTypeList.size());
            string accessibilityOutput = CRM_GenericHelper_H_HUM.flsCheck(AccessType.CREATABLE,InteractionTypeList);
            System.assertEquals(accessibilityOutput.contains('does not have sufficient access to'), true);
            test.stopTest();
         }
    }
    
    /**
    * Test method to check the Field Access for the user.
    *
    * @param     NULL
    * @return    void 
    */
    
    @isTest static void testFLSAccessScenario()
    {
     User testUser = [Select Id,LastName from User where LastName='test otherProfileUser' Limit 1 ]; 
        system.assertEquals('test otherProfileUser', testUser.LastName);
        system.runas(testUser)
        {
            test.startTest();
            Id oCRMRetailVisitorType = Schema.SObjectType.Account.getRecordTypeInfosByName().get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_RETAIL_VISITOR).getRecordTypeId();
            List<Account> lstOfAccounts = new List<Account>();
            Date waiverDate = Date.today()-100;        
            Account oAcc = CRMRetail_TestDataFactory_H_HUM.createAccountInstance('GC7654311', oCRMRetailVisitorType,Date.newInstance(1984, 01, 01),'01/01/1984', 'Test', 'Visitor_Account1', 'O', '1060920019',false, 'New York', '56605', '(123) 123-1231','test1234',waiverDate,'confirmed-veteran',NULL);
            lstOfAccounts.add(oAcc);
            System.assertEquals('Visitor_Account1', oAcc.lastName);
            string accessibilityOutput = CRM_GenericHelper_H_HUM.flsCheck(AccessType.CREATABLE,lstOfAccounts);
           	System.assertEquals(accessibilityOutput.contains('does not have access to field(s)'), true);
            test.stopTest();
         }
    }
    
    /**
    * Test method to cover the generic exception Scenario.
    *
    * @param     NULL
    * @return    void 
    */
     @isTest static void testFLSGenericExceptionScenario()
    {
        User testUser = [Select Id,LastName from User where LastName='test UserRetail' Limit 1 ];
        system.assertEquals('test UserRetail', testUser.LastName);
        system.runas(testUser)
        {
            test.startTest();
            List<Account> lstAccount = new List<Account>();
         	lstAccount = [Select Id, lastName from Account where lastName = 'Visitor_Account' limit 1];
            System.assertEquals('Visitor_Account', lstAccount[0].lastName);
            string accessibilityOutput = CRM_GenericHelper_H_HUM.flsCheck(Null,lstAccount);
            System.assertNotEquals(accessibilityOutput, Null);
            test.stopTest();
        }
    }
    
    /**
    * Test method to check the Field and Object Access for the user (Positive Scenario).
    *
    * @param     NULL
    * @return    void 
    */
    @isTest static void testObjectandFieldFLSAccess()
    {
        User testUser = [Select Id,LastName from User where LastName='test UserRetail' Limit 1 ];
        system.assertEquals('test UserRetail', testUser.LastName);
        system.runas(testUser)
        {
            test.startTest();
            CRM_GenericHelper_H_HUM.accessibilityOutput accessibilityOutput = new CRM_GenericHelper_H_HUM.accessibilityOutput();
            List<Account> lstAccount = new List<Account>();
         	lstAccount = [Select Id, lastName from Account where lastName = 'Visitor_Account' limit 1];
            
            System.assertEquals('Visitor_Account', lstAccount[0].lastName);
            accessibilityOutput = CRM_GenericHelper_H_HUM.securityCheckFLS(AccessType.CREATABLE, lstAccount);
            System.assertEquals(accessibilityOutput.areFieldsAccessible,true);
            System.assertEquals(accessibilityOutput.isobjectAccessible,true);
            test.stopTest();
         }
    }    
}