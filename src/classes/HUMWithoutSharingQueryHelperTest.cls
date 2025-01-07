/***************************************************************************************
Apex Class Name  : HUMWithoutSharingQueryHelperTest
Version          : 1.0
Created Date     : Sep 09 2014
Function         : This is test class for HUMWithoutSharingQueryHelper
*****************************************************************************************
Modification Log:
 * Developer Name           Code Review                    Date                       Description
 *------------------------------------------------------------------------------------------------------------
 * Manish Kumar              17832                       09/09/2014                    Original Version
 * Manish Kumar              18262                       10/01/2014                    Added testMethod for getListOfsObjects, Fixed defect#155745
 * Ranjeeth Nagishetty                                   01/10/2014                    As per the validation rule REQ-95950
 * Harshada Kamble/Anil Pavithran                        05/05/2023                    US4461937 T1PRJ0036776: SFDC Ability to Manually Modify Authentication Status (Multi Members) in Lightning - Genesys
 * Harshada Kamble                                       06/22/2023                    User Story 4696940: T1PRJ0036776: Classic - Multi-member transfer call handling
 * Harshada Kamble                                       06/22/2023                    User Story 4707880: T1PRJ0036776: Lightning - Multi-member transfer call handling - Provider (Genesys)
 ***************************************************************************************************/
@isTest
private class HUMWithoutSharingQueryHelperTest
{
    /*
    * Method Name   :    testFetchAccounts
    * Description   :    Test method for fetching Account details    
    * Return Type   :    void
    * Parameters    :    NA
    */
    static testMethod void testFetchAccounts()
    {
        Test.startTest();
        
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertNotEquals(oCCSUser, NULL, 'Value not matched');
        
        System.RunAs(oCCSUser)
        {
            Account oAcc = new Account(Name='TestAccount');
            insert oAcc;
            System.assertNotEquals(oAcc, NULL);
            
            List<Account> lstAcc = HUMWithoutSharingQueryHelper.fetchAccounts(oAcc.Id);
            System.assertNotEquals(lstAcc.size(), 0);
        }
        
        Test.stopTest();
    }
    
    /*
    * Method Name   :    testFetchInteractionMembers
    * Description   :    Test method for fetching InteractionMember details    
    * Return Type   :    void
    * Parameters    :    NA
    */
    static testMethod void testFetchInteractionMembers()
    {
        Test.startTest();
        
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertNotEquals(oCCSUser, NULL, 'Value not matched');
        
        System.RunAs(oCCSUser)
        {
            Account oAcc = new Account(Name='TestAccount');
            oAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Provider').getRecordTypeId();
            insert oAcc;
            System.assertNotEquals(oAcc, NULL);
            
            Interaction__c oInteraction = new Interaction__c(Interacting_With__c=oAcc.Id,Interacting_With_type__c ='Provider',Interaction_Origin__c='Inbound Call');
            insert oInteraction;
            System.assertNotEquals(oInteraction, NULL);
            
            Interaction_Member__c oInteractionMem = new Interaction_Member__c(Interacting_About__c=oAcc.Id,Interacting_About_Type__c='Provider', Interaction__c=oInteraction.Id, Authenticated__c= false);
            insert oInteractionMem;
            System.assertNotEquals(oInteractionMem, NULL);
            
            List<Interaction_Member__c> lstSelectedMembers = HUMWithoutSharingQueryHelper.fetchInteractionMembers(oInteraction.Id);
            System.assertNotEquals(lstSelectedMembers.size(), 0);
        }
        
        Test.stopTest();
    }
    
    /*
    * Method Name   :    testGetListOfRecords
    * Description   :    Test method for getting list of records for Account   
    * Return Type   :    void
    * Parameters    :    NA
    */
    static testMethod void testGetListOfRecords()
    {
        Test.startTest();
        
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertNotEquals(oCCSUser, NULL, 'Value not matched');
        
        System.RunAs(oCCSUser)
        {
            String sQuery = 'Select Id, Name from Account limit 1';
            ApexPages.StandardSetController oStdController = HUMWithoutSharingQueryHelper.getListOfRecords(sQuery, NULL, NULL, NULL);
            System.assertNotEquals(oStdController, NULL);
        }
        
        Test.stopTest();
    }
    
    /*
    * Method Name   :    testInsertInteraction
    * Description   :    Test method for inserting Interaction object record   
    * Return Type   :    void
    * Parameters    :    NA
    */
    static testMethod void testInsertInteraction()
    {
        Test.startTest();
        
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertNotEquals(oCCSUser, NULL, 'Value not matched');
        
        System.RunAs(oCCSUser)
        {
            Account oAcc = new Account(Name='TestAccount');
            insert oAcc;
            System.assertNotEquals(oAcc, NULL);
            
            Interaction__c oInteraction = new Interaction__c(Interacting_With__c=oAcc.Id,Interaction_Origin__c='Inbound Call');
            System.assertEquals(oInteraction.Interacting_With__c, oAcc.Id);
            Database.upsertResult oSvr = HUMWithoutSharingQueryHelper.inserInteraction(oInteraction);
            System.assertNotEquals(oSvr, NULL);
        }
        
        Test.stopTest();
    }
    
    /*
    * Method Name   :    testGetListOfsObjects
    * Description   :    Test method for retrieving list of sObjects   
    * Return Type   :    void
    * Parameters    :    NA
    */
    static testMethod void testGetListOfsObjects()
    {
        Test.startTest();
        
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertNotEquals(oCCSUser, NULL, 'Value not matched');
        
        System.RunAs(oCCSUser)
        {
            Account oAcc = new Account(Name='TestAccount');
            insert oAcc;
            System.assertNotEquals(oAcc, NULL);
            
            String sQuery = 'Select Id, Name from Account Limit 1';
            List<Account> lstAccount = (List<Account>)HUMWithoutSharingQueryHelper.getListOfsObjects(sQuery, NULL, NULL, NULL);
            System.assertNotEquals(lstAccount.size(), 0);
        }
        
        Test.stopTest();
    }

    /*
    * Method Name   :    testgetAccount
    * Description   :    Test method for fetching Account details    
    * Return Type   :    void
    * Parameters    :    NA
    */
    static testMethod void testgetAccount()
    {
        
        Test.startTest();
        
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertNotEquals(oCCSUser, NULL, 'Value not matched');
        
        System.RunAs(oCCSUser)
        {
            Account oAcc = new Account(Name='TestAccount');
            oAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Provider').getRecordTypeId();
            insert oAcc;
            System.assertNotEquals(oAcc, NULL);
            Account AccSO = HUMWithoutSharingQueryHelper.getAccount(oAcc.Id);
            System.assertNotEquals(AccSO, NULL);
        }
        
        Test.stopTest();
    }

    /*
    * Method Name   :    testgetMapping
    * Description   :    Test method for fetching integartingMap values and details    
    * Return Type   :    void
    * Parameters    :    NA
    */
    static testMethod void testgetMapping()
    {
        
        Test.startTest();
        
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertNotEquals(oCCSUser, NULL, 'Value not matched');
        
            //Create Interaction Data for soft phone
            Integration__c intRec = new Integration__c(Name=System.Label.HUMIntegrationTypeSoftPhone);
            insert intRec;
            
            //Assert the integration data is created
            system.assert(intRec.id<>null);
                             
            //Create integration mapping record for call transfer
            
            Integration_Mapping__c intMap = new Integration_Mapping__c();
            intMap.Mapping__c = 'Mapping:test;HUM';
            intMap.Integration_ID__c = intRec.id;
            intMap.Record_Id__c = 'Test UUID';
            insert intMap;
            system.assertEquals(intMap.Mapping__c == 'Mapping:test;HUM',true);
        
        System.RunAs(oCCSUser)
        {
            String UUID = 'Test UUID';
            Map<String,String> IntegrationMap = HUMWithoutSharingQueryHelper.getMapping(UUID);
            System.assertNotEquals(IntegrationMap, NULL);
        }
        
        Test.stopTest();
    }

    /*
    * Method Name   :    fetchCurrSelectedIntAbt
    * Description   :    Test method for fetching interaction details    
    * Return Type   :    void
    * Parameters    :    NA
    */
    static testMethod void testfetchCurrSelectedIntAbt()
    {
        
        Test.startTest();
        
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertNotEquals(oCCSUser, NULL, 'Value not matched');
        CRMFunctionality_ONOFF_Switch__c objCS = new CRMFunctionality_ONOFF_Switch__c(Name = '4696940', IsON__c = true);
        insert objCS;
        
        System.RunAs(oCCSUser)
        {
            String UUID = 'Test UUID Provider';
            Account oAcc = new Account(Name='TestAccount');
            oAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Provider').getRecordTypeId();
            insert oAcc;
            System.assertNotEquals(oAcc, NULL);
            String AccounID = oAcc.id;
            List<Interaction__c> InteractionSO = HUMWithoutSharingQueryHelper.fetchCurrSelectedIntAbt(UUID, AccounID);
            System.assertEquals(InteractionSO.size(), 0);
        }
        
        Test.stopTest();
    }

/*
    * Method Name   :    fetchInteractionwithItsMembers
    * Description   :    Test method for fetching interactionMember details based on UUID  
    * Return Type   :    void
    * Parameters    :    NA
    */
    static testMethod void testfetchInteractionwithItsMembers()
    {
        
        Test.startTest();
        
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertNotEquals(oCCSUser, NULL, 'Value not matched');
        
        System.RunAs(oCCSUser)
        {
            String UUID = 'Test UUID Provider';
            List<Interaction__c> InteractionSO = HUMWithoutSharingQueryHelper.fetchInteractionwithItsMembers(UUID);
            System.assertEquals(InteractionSO.size(), 0);
        }
        
        Test.stopTest();
    }
    
    static testMethod void testgetAccountNeg()
    {
        
        Test.startTest();
        
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User2','Customer Care Specialist');
        insert oCCSUser;
        system.assertNotEquals(oCCSUser, NULL, 'Value not matched');
        
        System.RunAs(oCCSUser)
        {
            Account oAcc = new Account(Name='TestAccount');
            oAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Provider').getRecordTypeId();
            insert oAcc;
            System.assertNotEquals(oAcc, NULL);
            Account AccSO = HUMWithoutSharingQueryHelper.getAccount('');
            System.assertEquals(AccSO, NULL);
        }
        
        Test.stopTest();
    }
    
}