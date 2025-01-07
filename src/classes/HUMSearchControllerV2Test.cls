/****************************************************************************
Apex Class Name  : HUMSearchControllerV2Test
Version          : 1.0 
Created Date     : AUGUST 08 2014 
Function         : Test class to HUMSearchController Apex class
Modification Log :
 * Developer Name        Code Reveiw                  Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Harshith Mandya          25261                      07/31/2015                  Removed abstraction layer - extending HUMSearchFieldHelper
 * Shreya Choodamani                                   08/27/2015                  Fixed the test class failures
 * Subhamay Mandal                                     02/13/2017                  Validate resetAll method
 * Subhamay Mandal                                     02/22/2017                  Validate populateInteractionWithAndAboutDetails method
 * Prafull Verma            84636                      04/05/2017                  Added testSetup method and changes for REQ - 306322 : SF - TECH - Softphone routing optimization
 * Mohammed Noor            96594                      05/18/2017                  REQ - 311432 Softphone / CRM Results for All Family Members by searching using Member Id only and launch optimization 
 * Mohammed Noor                                       08/09/2017                  REQ - 326174 CA incident #6803887 - Fix for Agent search 
 * Santhi Mandava           262341                     07/26/2018                  Implemented platform encryption for account fields.
*  Mukul Kumar                                         09/25/2018                  REQ - 373386 - Display Password Prior to Interaction Being Created 
* Santhi Mandava                                       03/12/2019                  Removed "DpaaSWebServiceMockImpl" usage.
* Santhi Mandava            355559                     05/10/2019                  Removed "ENCRYPTIONTYPE" custom setting creation
* Moshitha Gunasekaran                                 04/20/2021                  US#2163329 - Removed PopulateUnknownMemberDetails method for checkmarx issues
* Ajay Chakradhar                                      05/25/2021                  Fixed the test class failures
* Harshada Kamble/Anil Pavithran                       05/05/2023                  US4461937 T1PRJ0036776: SFDC Ability to Manually Modify Authentication Status (Multi Members) in Lightning - Genesys
 **********************************************************************************************************************************/
@isTest
private class HUMSearchControllerV2Test 
{
    /*
    * Method Name : setupTestData
    * Description : Method to set up test data
    * Return type : void
    * Paramater   : NA
    */
    @testSetup static void setupTestData()
    {
        insert new SOFTPHONE_PRPID_HUM__c( Name ='AccountName' , Value__c = 'Test');
        insert new SOFTPHONE_PRPID_HUM__c( Name = 'AccountName2' , Value__c = 'Test');
        insert new SOFTPHONE_PRPID_HUM__c( Name = 'IFirstName' , Value__c = 'Test');
        insert new SOFTPHONE_PRPID_HUM__c( Name = 'IFirstName2' , Value__c = 'Test');
        insert new SOFTPHONE_PRPID_HUM__c( Name = 'ILastName' , Value__c = 'Test');
        insert new SOFTPHONE_PRPID_HUM__c( Name = 'ILastName2' , Value__c = 'Test');
        insert new SOFTPHONE_PRPID_HUM__c( Name = 'TaxId' , Value__c = 'Test');
        insert new SOFTPHONE_PRPID_HUM__c( Name = 'TaxId2' , Value__c = 'Test');
        insert new SOFTPHONE_PRPID_HUM__c( Name = 'GroupNumber' , Value__c = 'Test');
        insert new SOFTPHONE_PRPID_HUM__c( Name = 'GroupNumber2' , Value__c = 'Test');
        insert new SOFTPHONE_PRPID_HUM__c( Name = 'FirstName' , Value__c = 'Test');
        insert new SOFTPHONE_PRPID_HUM__c( Name = 'FirstName2' , Value__c = 'Test');
        insert new SOFTPHONE_PRPID_HUM__c( Name = 'LastName' , Value__c = 'Test');
        insert new SOFTPHONE_PRPID_HUM__c( Name = 'LastName2' , Value__c = 'Test');
        insert new SOFTPHONE_PRPID_HUM__c( Name = 'DOB' , Value__c = 'Test');
        insert new SOFTPHONE_PRPID_HUM__c( Name = 'DOB2' , Value__c = 'Test');
        insert new SOFTPHONE_PRPID_HUM__c( Name = 'MemberId' , Value__c = 'Test');
        insert new SOFTPHONE_PRPID_HUM__c( Name = 'MemberId2' , Value__c = 'Test');
        insert new SOFTPHONE_PRPID_HUM__c( Name = 'NpiId' , Value__c = 'Test');
        insert new SOFTPHONE_PRPID_HUM__c( Name = 'NpiId2' , Value__c = 'Test');
        insert new SOFTPHONE_PRPID_HUM__c( Name = 'CallCategory' , Value__c = 'Test');        
         Account oAccc = HUMTestDataHelperTest.getAccount();
            oAccc.LastName = 'Account Testing';
            oAccc.Account_External_ID__c = '123452';
            oAccc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            oAccc.Account_Security_Answer__c = 'testing';
            oAccc.Account_Security_Question__c = 'What is your name ?';
            oAccc.Account_Security_Access__c = 'test12';
            oAccc.Account_Security_EndDate__c = Date.newInstance(2025, 12, 9);
            insert oAccc;
    }
    
    /*
     * Method Name   :    testSearchInteractionMember
     * Description   :    This method tests Search functionality/softphone routing for Member type of accounts.   
     * Return Type   :    void
     * Parameters    :    NA
     */     
    static testMethod void testSearchInteractionMember() 
    {         

        User u = HUMTestDataHelperTest.createTestUser('TestUser1','System Administrator'); 
        insert u;
                
        List<HUMCipherCloudSettings__c> lstApplicationName = new List<HUMCipherCloudSettings__c> ();
        lstApplicationName.add(new HUMCipherCloudSettings__c(Name = 'APPLICATION_NAME',  Value__c = 'CRMR1'));
        insert lstApplicationName;
        
        HUMSearchLimit__c searchlimittest = new HUMSearchLimit__c(Name = 'Member_SearchLimit');           
        searchlimittest.Search_Limit__c = 123;
        insert searchlimittest;
        
        List<HUMConstants__c> lstHUMConstants = new List<HUMConstants__c>();
        lstHUMConstants.add(new HUMConstants__c( Name = 'HUMCallerName' , StringValue__c = 'CALLER_NAME__C'));
        lstHUMConstants.add(new HUMConstants__c( Name = 'HUMInteraction' , StringValue__c = 'Interaction__c'));
        insert lstHUMConstants;
        
        List<HUMEncryptedFields__c> lstHUMEncryptedFields = new List<HUMEncryptedFields__c>();
        lstHUMEncryptedFields.add(new HUMEncryptedFields__c(Name = 'CALLER_NAME__C.Interaction__c' , FieldName__c = 'CALLER_NAME__C', EncryptionScheme__c = 'Stateless_AFPE_for_Alphabetic_Filtering', ObjectName__c = 'Interaction__c', QuestionExclude__c = ''));
        insert lstHUMEncryptedFields;
        
        System.runas(u) 
        {               
            Account oAcc = HUMTestDataHelperTest.getAccount();
            oAcc.LastName = 'Account Test';
            oAcc.Account_External_ID__c = '12345';
            oAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            insert oAcc;
            HUMTestDataHelperTest.getHUMConstantsData();

            HUMMemberSearchControllerV2 oHUMMem = new HUMMemberSearchControllerV2();
            HUMAgencySearchControllerV2 oHUMAgency = new HUMAgencySearchControllerV2();
            HUMGroupSearchControllerV2 oHUMgroup = new HUMGroupSearchControllerV2();
            HUMProviderSearchControllerV2 oHUMProvider = new HUMProviderSearchControllerV2();
            Test.startTest();

            //  set current page with controller
            PageReference testPageRef = Page.HUMCustomSearchV2;
            Test.setCurrentPage(testPageRef);
            
            //Created Custom setting records for test context. 
            HUMTestDataHelperTest.getHUMConstantsData();  

            HUMSearchControllerV2 oHUMSearch = new HUMSearchControllerV2();
            oHUMSearch.switchTab();
            oHUMSearch.getThis();  

            //  validate new Reset all method
            PageReference pageRefResetAll = oHUMSearch.resetAll();
            System.assertNotEquals(pageRefResetAll, null, 'Reset serach page.');
            
            //Setting conroller type for Component based design approach.
            oHUMSearch.setComponentControllerMap('Member', oHUMMem);
            oHUMSearch.setComponentControllerMap('Agency', oHUMAgency);
            oHUMSearch.setComponentControllerMap('Group', oHUMgroup);
            oHUMSearch.setComponentControllerMap('Provider', oHUMProvider);

            //Asserting whether Map is set for member component.
            system.assert(oHUMSearch.searchControllerMap.get('Member') != null);

            //Simulating user clicks on Interacting with button.
            oHUMSearch.strSearchContextName = 'Member';
            oHUMSearch.setComponentControllerMap('Member', oHUMMem);

            //Asserting whether Map is set for member component.
            system.assert(oHUMSearch.searchControllerMap.get('Member') != null);
            
            oHUMSearch.interactionLogController = new HUMInteractionLogComponentControllerV2();
            oHUMSearch.strInteractionWithID = oAcc.id;
            oHUMSearch.bIsDataFromExternalSystem = true;
            oHUMSearch.interactionLogController.oFetchInteraction.Authenticated__c = true;
            oHUMSearch.interactionLogController.oHUMIntMembers.Authenticated__c = true;
            oHUMSearch.strJsonAccObject = JSON.serialize(oAcc);

            oHUMSearch.populateInteractionWithDetails();
            oHUMSearch.updateAuthCheck();
            oHUMSearch.populateInteractionAboutDetails();
            oHUMSearch.populateInteractionDetails();
            oHUMSearch.populateUnknownAccountDetail();

            //Asserting whether Interaction is populated with proper details.
            system.assert(oHUMSearch.interactionLogController.oFetchInteraction.Interacting_with__c == oAcc.id);

            //Search results retunrns record withing salesforce.
            oHUMSearch = new HUMSearchControllerV2();
            oHUMSearch.strSearchContextName = 'Member';
            oHUMSearch.setComponentControllerMap('Member', oHUMMem); 
            oHUMSearch.setComponentControllerMap('Agency', oHUMAgency);
            oHUMSearch.setComponentControllerMap('Group', oHUMgroup);
            oHUMSearch.setComponentControllerMap('Provider', oHUMProvider);
            //Asserting for Search component is set to Member.
            system.assert(oHUMSearch.searchControllerMap.get('Member') != null);

            oHUMSearch.interactionLogController = new HUMInteractionLogComponentControllerV2();
            oHUMSearch.strInteractionWithID = oAcc.id;
            oHUMSearch.bIsDataFromExternalSystem = false;
            oHUMSearch.strJsonAccObject = JSON.serialize(oAcc);
            oHUMSearch.interactionLogController.multiMemberAuthSwitch = false;

            oHUMSearch.populateInteractionWithDetails();
            oHUMSearch.updateAuthCheck();
            oHUMSearch.populateInteractionAboutDetails();
            oHUMSearch.populateInteractionDetails();
            oHUMSearch.closePopup();
            oHUMSearch.populateUnknownAccountDetail();
            
            //Asserting whether Interaction is populated with proper details.
            system.assert(oHUMSearch.interactionLogController.oFetchInteraction.Interacting_with__c == oAcc.id); 
            system.assertEquals(oHUMSearch.interactionLogController.oFetchInteraction.Interacting_with_type__c, 'Member');            
            //Softphone Routing
            oHUMSearch.transferJsonObject = 'uuID=070714000093919000040X8886667730&iAboutFirstName=';
            oHUMSearch.transferJsonObject+= 'Daisy&iAboutLastName=Duck&iAboutType=M&iAboutID=1234&firstName=James&lastName=Thong&callerType=';
            oHUMSearch.transferJsonObject+= 'OutboundCall&iWithID=2343242&iWithFirstName=Daisy&iWithLastName=Duck&iWithType=G&Test=1234';
            oHUMSearch.softPhoneRoutingSearch();
            
            //Assert whether softphone details are populated in the interaction.        
            system.assertEquals(oHUMSearch.interactionLogController.oFetchInteraction.Interaction_Origin__c, 'Inbound Call');
    
            //Softphone Routing
            oHUMSearch.transferJsonObject = 'uuID=070714000093919000040X8886667730&iAboutFirstName = ';
            oHUMSearch.transferJsonObject+= 'Daisy&iAboutLastName=Duck&iAboutType=M&iAboutID=1234&firstName=Jimmy&lastName=Thong&callerType = ';
            oHUMSearch.transferJsonObject+= 'InBoundCall&iWithID=2343242&iWithFirstName=Daisy&iWithLastName=Duck&iWithType=G';
            oHUMSearch.softPhoneRoutingSearch();

            //Assert whether softphone details are populated in the interaction.          
            system.assertEquals(oHUMSearch.interactionLogController.oFetchInteraction.Interaction_Origin__c, 'Inbound Call');

            oHUMSearch.transferJsonObject = 'uuID=070714000093919000040X8886667730&iAboutFirstName=';
            oHUMSearch.transferJsonObject+= 'Daisy&iAboutLastName=Duck&iAboutType=M&iAboutID=1234&firstName=Jeremy&lastName=Thong&callerType=';
            oHUMSearch.transferJsonObject+= 'OutboundCall&iWithID=2343242&iWithFirstName=Daisy&iWithLastName=Duck&iWithType=A';
            oHUMSearch.softPhoneRoutingSearch();

            //Assert whether softphone details are populated in the interaction.           
            system.assertEquals(oHUMSearch.interactionLogController.oFetchInteraction.Interaction_Origin__c, 'Inbound Call');

            //Softphone Routing
            oHUMSearch.transferJsonObject = 'uuID=070714000093919000040X8886667730&iAboutFirstName=';
            oHUMSearch.transferJsonObject+= 'Daisy&iAboutLastName=Duck&iAboutType=P&iAboutID=1234&firstName=prafull&lastName=kale&callerType=';
            oHUMSearch.transferJsonObject+= 'OutboundCall&iWithID=2343242&iWithFirstName=DaisyN&iWithLastName=Duckcrick&iWithType=M';
            oHUMSearch.softPhoneRoutingSearch();                
            system.assertEquals(oHUMSearch.interactionLogController.oFetchInteraction.Interaction_Origin__c, 'Inbound Call');      
            
            Test.stopTest();
        } 
    } 


/*
     * Method Name   :    testpopulateInteractionWithAndAboutDetails
     * Description   :    This method tests Search functionality/softphone routing for Member type of accounts.   
     * Return Type   :    void
     * Parameters    :    NA
     */     
    static testMethod void testpopulateInteractionWithAndAboutDetails() 
    {         

        User u = HUMTestDataHelperTest.createTestUser('TestUser1','System Administrator'); 
        insert u;
                
        List<HUMCipherCloudSettings__c> lstApplicationName = new List<HUMCipherCloudSettings__c> ();
        lstApplicationName.add(new HUMCipherCloudSettings__c(Name = 'APPLICATION_NAME',  Value__c = 'CRMR1'));
        insert lstApplicationName;
        
        HUMSearchLimit__c searchlimittest = new HUMSearchLimit__c(Name = 'Member_SearchLimit');           
        searchlimittest.Search_Limit__c = 123;
        insert searchlimittest;
        
        List<HUMConstants__c> lstHUMConstants = new List<HUMConstants__c>();
        lstHUMConstants.add(new HUMConstants__c( Name = 'HUMCallerName' , StringValue__c = 'CALLER_NAME__C'));
        lstHUMConstants.add(new HUMConstants__c( Name = 'HUMInteraction' , StringValue__c = 'Interaction__c'));
        insert lstHUMConstants;
        
        List<HUMEncryptedFields__c> lstHUMEncryptedFields = new List<HUMEncryptedFields__c>();
        lstHUMEncryptedFields.add(new HUMEncryptedFields__c(Name = 'CALLER_NAME__C.Interaction__c' , FieldName__c = 'CALLER_NAME__C', EncryptionScheme__c = 'Stateless_AFPE_for_Alphabetic_Filtering', ObjectName__c = 'Interaction__c', QuestionExclude__c = ''));
        insert lstHUMEncryptedFields;

        System.runas(u) 
        {               
            Account oAcc = HUMTestDataHelperTest.getAccount();
            oAcc.LastName = 'Account Test';
            oAcc.Account_External_ID__c = '12345';
            oAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            insert oAcc;
            HUMTestDataHelperTest.getHUMConstantsData();

            HUMMemberSearchControllerV2 oHUMMem = new HUMMemberSearchControllerV2();   

            Test.startTest();
            //  set current page with controller
            PageReference testPageRef = Page.HUMCustomSearchV2;
            Test.setCurrentPage(testPageRef);
        
            
            //Created Custom setting records for test context. 
            HUMTestDataHelperTest.getHUMConstantsData();  

            HUMSearchControllerV2 oHUMSearch = new HUMSearchControllerV2();
            oHUMSearch.switchTab();
            oHUMSearch.getThis();  
            
            //  validate new Reset all method
            PageReference pageRefResetAll = oHUMSearch.resetAll();
            System.assertNotEquals(pageRefResetAll, null, 'Reset serach page.');
         

            //Setting conroller type for Component based design approach.
            oHUMSearch.setComponentControllerMap('Member', oHUMMem);

            //Asserting whether Map is set for member component.
            system.assert(oHUMSearch.searchControllerMap.get('Member') != null);

            //Simulating user clicks on Interacting with button.
            oHUMSearch.strSearchContextName = 'Member';
            oHUMSearch.setComponentControllerMap('Member', oHUMMem);

            //Asserting whether Map is set for member component.
            system.assert(oHUMSearch.searchControllerMap.get('Member') != null);

            oHUMSearch.interactionLogController = new HUMInteractionLogComponentControllerV2();
            oHUMSearch.strInteractionWithID = oAcc.id;
            oHUMSearch.bIsDataFromExternalSystem = true;
            oHUMSearch.strJsonAccObject = JSON.serialize(oAcc);
                        
            oHUMSearch.populateInteractionWithAndAboutDetails();
            oHUMSearch.populateUnknownAccountDetail();

            //Asserting whether Interaction is populated with proper details.
            system.assert(oHUMSearch.interactionLogController.oFetchInteraction.Interacting_with__c == oAcc.id);            

            //Search results retunrns record withing salesforce.
            oHUMSearch = new HUMSearchControllerV2();
            oHUMSearch.strSearchContextName = 'Member';
            oHUMSearch.setComponentControllerMap('Member', oHUMMem);

            //Asserting for Search component is set to Member.
            system.assert(oHUMSearch.searchControllerMap.get('Member') != null);

            oHUMSearch.interactionLogController = new HUMInteractionLogComponentControllerV2();
            oHUMSearch.strInteractionWithID = oAcc.id;
            oHUMSearch.bIsDataFromExternalSystem = false;
            oHUMSearch.strJsonAccObject = JSON.serialize(oAcc);

            oHUMSearch.populateInteractionWithAndAboutDetails();
            oHUMSearch.closePopup();
            oHUMSearch.populateUnknownAccountDetail();

            //Asserting whether Interaction is populated with proper details.
            system.assert(oHUMSearch.interactionLogController.oFetchInteraction.Interacting_with__c == oAcc.id); 

            //Softphone Routing
            oHUMSearch.transferJsonObject = 'uuID=070714000093919000040X8886667730&iAboutFirstName=';
            oHUMSearch.transferJsonObject+= 'Daisy&iAboutLastName=Duck&iAboutType=M&iAboutID=1234&firstName=James&lastName=Thong&callerType=';
            oHUMSearch.transferJsonObject+= 'OutboundCall&iWithID=2343242&iWithFirstName=Daisy&iWithLastName=Duck&iWithType=G&Test=Test';
            oHUMSearch.softPhoneRoutingSearch();

            //Assert whether softphone details are populated in the interaction.
            system.assert(oHUMSearch.interactionLogController.oFetchInteraction.Interacting_with_type__c == 'Member');

            //Softphone Routing
            oHUMSearch.transferJsonObject = 'uuID=070714000093919000040X8886667730&iAboutFirstName = ';
            oHUMSearch.transferJsonObject+= 'Daisy&iAboutLastName=Duck&iAboutType=M&iAboutID=1234&firstName=Jimmy&lastName=Thong&callerType = ';
            oHUMSearch.transferJsonObject+= 'InBoundCall&iWithID=2343242&iWithFirstName=Daisy&iWithLastName=Duck&iWithType=G&Test=Test';
            oHUMSearch.softPhoneRoutingSearch();

            //Assert whether softphone details are populated in the interaction.
            system.assert(oHUMSearch.interactionLogController.oFetchInteraction.Interacting_with_type__c == 'Member');

            oHUMSearch.transferJsonObject = 'uuID=070714000093919000040X8886667730&iAboutFirstName=';
            oHUMSearch.transferJsonObject+= 'Daisy&iAboutLastName=Duck&iAboutType=M&iAboutID=1234&firstName=Jeremy&lastName=Thong&callerType=';
            oHUMSearch.transferJsonObject+= 'OutboundCall&iWithID=2343242&iWithFirstName=Daisy&iWithLastName=Duck&iWithType=A&Test=Test';
            oHUMSearch.softPhoneRoutingSearch();

            //Assert whether softphone details are populated in the interaction.
            system.assert(oHUMSearch.interactionLogController.oFetchInteraction.Interacting_with_type__c == 'Member');      

            Test.stopTest();
        }  
    } 
    /*
     * Method Name   :    testSearchInteractionProvider
     * Description   :    This method tests Search functionality/softphone routing for Provider type of accounts.   
     * Return Type   :    void
     * Parameters    :    NA
     */
        static testMethod void testSearchInteractionProvider() 
    {
        User u = HUMTestDataHelperTest.createTestUser('TestUser1','System Administrator'); 
        insert u;
                
        List<HUMCipherCloudSettings__c> lstApplicationName = new List<HUMCipherCloudSettings__c> ();
        lstApplicationName.add(new HUMCipherCloudSettings__c(Name = 'APPLICATION_NAME',  Value__c = 'CRMR1'));
        insert lstApplicationName;
        
        List<HUMConstants__c> lstHUMConstants = new List<HUMConstants__c>();
        lstHUMConstants.add(new HUMConstants__c( Name = 'HUMCallerName' , StringValue__c = 'CALLER_NAME__C'));
        lstHUMConstants.add(new HUMConstants__c( Name = 'HUMInteraction' , StringValue__c = 'Interaction__c'));
        insert lstHUMConstants;
        
        List<HUMEncryptedFields__c> lstHUMEncryptedFields = new List<HUMEncryptedFields__c>();
        lstHUMEncryptedFields.add(new HUMEncryptedFields__c(Name = 'CALLER_NAME__C.Interaction__c' , FieldName__c = 'CALLER_NAME__C', EncryptionScheme__c = 'Stateless_AFPE_for_Alphabetic_Filtering', ObjectName__c = 'Interaction__c', QuestionExclude__c = ''));
        insert lstHUMEncryptedFields;
        
        System.runas(u) 
        { 
            Account oAcc = HUMTestDataHelperTest.getAccount();
            oAcc.Name = 'Account Test';
            oAcc.Account_External_ID__c = '12345';
            oAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Provider').getRecordTypeId();      
            insert oAcc;
            HUMTestDataHelperTest.getHUMConstantsData();

            HUMProviderSearchControllerV2 oHUMProv = new HUMProviderSearchControllerV2();

            Test.startTest();

            //Created Custom setting records for test context. 
            HUMTestDataHelperTest.getHUMConstantsData();  

            HUMSearchControllerV2 oHUMSearch = new HUMSearchControllerV2(); 

            //Simulating user clicks on Interacting with button on provider screen.
            oHUMSearch.strSearchContextName = 'Provider';
            oHUMSearch.setComponentControllerMap('Provider', oHUMProv);

            //Asserting whether Search Controller Map is set for Provider Component.
            system.assert(oHUMSearch.searchControllerMap.get('Provider') != null);
            
            //Initializing Interaction Log
            oHUMSearch.interactionLogController = new HUMInteractionLogComponentControllerV2();
            oHUMSearch.strInteractionWithID = oAcc.id;
            oHUMSearch.bIsDataFromExternalSystem = true;
            oHUMSearch.strJsonAccObject = JSON.serialize(oAcc);
            oHUMSearch.populateInteractionWithDetails();
            oHUMSearch.populateInteractionAboutDetails();
            oHUMSearch.populateInteractionDetails();
            oHUMSearch.populateUnknownAccountDetail();

            //Asserting whether Interaction is populated with proper details.
            system.assert(oHUMSearch.interactionLogController.oFetchInteraction.Interacting_with__c == oAcc.id); 
            
            //Search results retunrns record within salesforce.
            oHUMSearch = new HUMSearchControllerV2();

            oHUMSearch.strSearchContextName = 'Provider';
            oHUMSearch.setComponentControllerMap('Provider', oHUMProv);

            //Asserting whether Search Controller Map is set for Provider Component.
            system.assert(oHUMSearch.searchControllerMap.get('Provider') != null);

            oHUMSearch.interactionLogController = new HUMInteractionLogComponentControllerV2();
            oHUMSearch.strInteractionWithID = oAcc.id;
            oHUMSearch.bIsDataFromExternalSystem = false;
            oHUMSearch.strJsonAccObject = JSON.serialize(oAcc);            
            oHUMSearch.populateInteractionWithDetails(); 
            oHUMSearch.populateInteractionAboutDetails();
            oHUMSearch.populateInteractionDetails();
            oHUMSearch.populateUnknownAccountDetail();

            //Asserting whether Interaction is populated with proper details.
            system.assert(oHUMSearch.interactionLogController.oFetchInteraction.Interacting_with__c == oAcc.id); 
            
            oHUMSearch.transferJsonObject = 'uuID=070714000093919000040X8886667730&iAboutFirstName=';
            oHUMSearch.transferJsonObject+= 'Daisy&iAboutLastName=Duck&iAboutType=P&iAboutID=1234&firstName=James&lastName=Thong&callerType=';
            oHUMSearch.transferJsonObject+= 'OutboundCall&iWithID=2343242&iWithFirstName=Daisy&iWithLastName=Duck&iWithType=G&Test=Test1';
            oHUMSearch.softPhoneRoutingSearch();

            //Assert whether softphone details are populated in the interaction.
            system.assert(oHUMSearch.interactionLogController.oFetchInteraction.Interacting_with_type__c == 'Provider');

            Test.stopTest();

        }  
    } 

    /*
     * Method Name   :    testSearchInteractionAgency
     * Description   :    This method tests Search functionality/softphone routing for Agency type of accounts.   
     * Return Type   :    void
     * Parameters    :    NA
     */     
    static testMethod void testSearchInteractionAgency() 
    {
        User u = HUMTestDataHelperTest.createTestUser('TestUser1','System Administrator'); 
        insert u;
                
        List<HUMCipherCloudSettings__c> lstApplicationName = new List<HUMCipherCloudSettings__c> ();
        lstApplicationName.add(new HUMCipherCloudSettings__c(Name = 'APPLICATION_NAME',  Value__c = 'CRMR1'));
        insert lstApplicationName;
        
        List<HUMConstants__c> lstHUMConstants = new List<HUMConstants__c>();
        lstHUMConstants.add(new HUMConstants__c( Name = 'HUMCallerName' , StringValue__c = 'CALLER_NAME__C'));
        lstHUMConstants.add(new HUMConstants__c( Name = 'HUMInteraction' , StringValue__c = 'Interaction__c'));
        insert lstHUMConstants;
        
        List<HUMEncryptedFields__c> lstHUMEncryptedFields = new List<HUMEncryptedFields__c>();
        lstHUMEncryptedFields.add(new HUMEncryptedFields__c(Name = 'CALLER_NAME__C.Interaction__c' , FieldName__c = 'CALLER_NAME__C', EncryptionScheme__c = 'Stateless_AFPE_for_Alphabetic_Filtering', ObjectName__c = 'Interaction__c', QuestionExclude__c = ''));
        insert lstHUMEncryptedFields;
        
        System.runas(u) 
        { 
            Account oAcc = HUMTestDataHelperTest.getAccount();
            oAcc.Name = 'Account Test';
            oAcc.Account_External_ID__c = '12345';
            oAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Agent/Broker').getRecordTypeId();        
            insert oAcc;
            HUMTestDataHelperTest.getHUMConstantsData();

            HUMAgencySearchControllerV2 oHUMAgent = new HUMAgencySearchControllerV2();

            Test.startTest();

            //Created Custom setting records for test context. 
            HUMTestDataHelperTest.getHUMConstantsData();  
            
            HUMSearchControllerV2 oHUMSearch = new HUMSearchControllerV2(); 

            //Simulating user clicks on Interacting with button on provider screen.
            oHUMSearch.strSearchContextName = 'Agency';
            oHUMSearch.setComponentControllerMap('Agency', oHUMAgent);

            //Asserting whether Search Controller Map is set for Provider Component.
            system.assert(oHUMSearch.searchControllerMap.get('Agency') != null);

            //Initializing Interaction Log
            oHUMSearch.interactionLogController = new HUMInteractionLogComponentControllerV2();
            oHUMSearch.strInteractionWithID = oAcc.id;
            oHUMSearch.bIsDataFromExternalSystem = true;
            oHUMSearch.strJsonAccObject = JSON.serialize(oAcc);
            oHUMSearch.populateInteractionWithDetails();
            oHUMSearch.populateInteractionAboutDetails();
            oHUMSearch.populateInteractionDetails();
            oHUMSearch.populateUnknownAccountDetail();

            //Asserting whether Interaction is populated with proper details.
            system.assert(oHUMSearch.interactionLogController.oFetchInteraction.Interacting_with__c == oAcc.id); 

            //Search results returns record withing salesforce.
            oHUMSearch = new HUMSearchControllerV2();

            oHUMSearch.strSearchContextName = 'Agency';
            oHUMSearch.setComponentControllerMap('Agency', oHUMAgent);

            //Asserting whether Search Controller Map is set for Provider Component.
            system.assert(oHUMSearch.searchControllerMap.get('Agency') != null);

            oHUMSearch.interactionLogController = new HUMInteractionLogComponentControllerV2();
            oHUMSearch.strInteractionWithID = oAcc.id;
            oHUMSearch.bIsDataFromExternalSystem = false;
            oHUMSearch.strJsonAccObject = JSON.serialize(oAcc);
            oHUMSearch.populateInteractionWithDetails(); 
            oHUMSearch.populateInteractionAboutDetails();
            oHUMSearch.populateInteractionDetails();
            oHUMSearch.populateUnknownAccountDetail();

            //Asserting whether Interaction is populated with proper details.
            system.assert(oHUMSearch.interactionLogController.oFetchInteraction.Interacting_with__c == oAcc.id); 

            oHUMSearch.transferJsonObject = 'uuID=070714000093919000040X8886667730&iAboutFirstName=';
            oHUMSearch.transferJsonObject+= 'Daisy&iAboutLastName=Duck&iAboutType=A&iAboutID=1234&firstName=James&lastName=Thong&callerType=';
            oHUMSearch.transferJsonObject+= 'OutboundCall&iWithID=12343242&iWithFirstName=Daisy&iWithLastName=Duck&iWithType=G&Test=Test';
            oHUMSearch.softPhoneRoutingSearch();

            //Assert whether softphone details are populated in the interaction.
            system.assert(oHUMSearch.interactionLogController.oFetchInteraction.Interacting_with_type__c == 'Agent');

            oHUMSearch.transferJsonObject = 'uuID=070714000093919000040X8886667730&iAboutFirstName=';
            oHUMSearch.transferJsonObject+= 'Daisy&iAboutLastName=Duck&iAboutType=A&iAboutID=123456789&firstName=Jimmy&lastName=&callerType=';
            oHUMSearch.transferJsonObject+= 'OutboundCall&iWithID=234324211&iWithFirstName=Daisy&iWithLastName=Duck&iWithType=G';
            oHUMSearch.transferJsonObject = EncodingUtil.base64Encode(blob.valueof(oHUMSearch.transferJsonObject));
            oHUMSearch.softPhoneRoutingSearch();

            //Assert whether softphone details are populated in the interaction.
            system.assert(oHUMSearch.interactionLogController.oFetchInteraction.Interacting_with_type__c == 'Agent');


            Test.stopTest();
        } 
    } 

    /*
     * Method Name   :    testSearchInteractionGroup
     * Description   :    This method tests Search functionality/softphone routing for Group type of accounts.   
     * Return Type   :    void
     * Parameters    :    NA
     */     
    static testMethod void testSearchInteractionGroup() 
    {
        User u = HUMTestDataHelperTest.createTestUser('TestUser1','System Administrator'); 
        insert u;
        
        List<HUMCipherCloudSettings__c> lstApplicationName = new List<HUMCipherCloudSettings__c> ();
        lstApplicationName.add(new HUMCipherCloudSettings__c(Name = 'APPLICATION_NAME',  Value__c = 'CRMR1'));
        insert lstApplicationName;
        
        List<HUMConstants__c> lstHUMConstants = new List<HUMConstants__c>();
        lstHUMConstants.add(new HUMConstants__c( Name = 'HUMCallerName' , StringValue__c = 'CALLER_NAME__C'));
        lstHUMConstants.add(new HUMConstants__c( Name = 'HUMInteraction' , StringValue__c = 'Interaction__c'));
        insert lstHUMConstants;
        
        List<HUMEncryptedFields__c> lstHUMEncryptedFields = new List<HUMEncryptedFields__c>();
        lstHUMEncryptedFields.add(new HUMEncryptedFields__c(Name = 'CALLER_NAME__C.Interaction__c' , FieldName__c = 'CALLER_NAME__C', EncryptionScheme__c = 'Stateless_AFPE_for_Alphabetic_Filtering', ObjectName__c = 'Interaction__c', QuestionExclude__c = ''));
        insert lstHUMEncryptedFields;

        System.runas(u) 
        { 
            Account oAcc = HUMTestDataHelperTest.getAccount();
            oAcc.Name = 'Account Test';
            oAcc.Account_External_ID__c = '12345';
            oAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
            insert oAcc;
            HUMTestDataHelperTest.getHUMConstantsData();

            HUMGroupSearchControllerV2 oHUMGroup = new HUMGroupSearchControllerV2();
            Test.startTest();

            //Created Custom setting records for test context. 
            HUMTestDataHelperTest.getHUMConstantsData();  

            HUMSearchControllerV2 oHUMSearch = new HUMSearchControllerV2(); 

            //Simulating user clicks on Interacting with button on provider screen.
            oHUMSearch.strSearchContextName = 'Group';
            oHUMSearch.setComponentControllerMap('Group', oHUMGroup);

            //Assetring whether Search Controller Map is set for Provider Component.
            system.assert(oHUMSearch.searchControllerMap.get('Group') != null);

            //Initializing Interaction Log
            oHUMSearch.interactionLogController = new HUMInteractionLogComponentControllerV2();
            oHUMSearch.strInteractionWithID = oAcc.id;
            oHUMSearch.bIsDataFromExternalSystem = true;
            oHUMSearch.strJsonAccObject = JSON.serialize(oAcc);
            oHUMSearch.populateInteractionWithDetails();
            oHUMSearch.populateInteractionAboutDetails();
            oHUMSearch.populateInteractionDetails();
            oHUMSearch.populateUnknownAccountDetail();

            //Asserting whether Interaction is populated with proper details.
            system.assert(oHUMSearch.interactionLogController.oFetchInteraction.Interacting_with__c == oAcc.id); 

            //Search results returns record withing salesforce.
            oHUMSearch = new HUMSearchControllerV2();

            oHUMSearch.strSearchContextName = 'Group';
            oHUMSearch.setComponentControllerMap('Group', oHUMGroup);

            //Asserting whether Search Controller Map is set for Provider Component.
            system.assert(oHUMSearch.searchControllerMap.get('Group') != null);

            oHUMSearch.interactionLogController = new HUMInteractionLogComponentControllerV2();
            oHUMSearch.strInteractionWithID = oAcc.id;
            oHUMSearch.bIsDataFromExternalSystem = false;
            oHUMSearch.strJsonAccObject = JSON.serialize(oAcc);
            oHUMSearch.populateInteractionWithDetails(); 
            oHUMSearch.populateInteractionAboutDetails();
            oHUMSearch.populateInteractionDetails();
            oHUMSearch.populateUnknownAccountDetail();

            //Asserting whether Interaction is populated with proper details.
            system.assert(oHUMSearch.interactionLogController.oFetchInteraction.Interacting_with__c == oAcc.id); 

            oHUMSearch.transferJsonObject = 'uuID=070714000093919000040X8886667730&iAboutFirstName=';
            oHUMSearch.transferJsonObject+= 'Daisy&iAboutLastName=Duck&iAboutType=G&iAboutID=1234&firstName=James&lastName=Thong&callerType=';
            oHUMSearch.transferJsonObject+= 'OutboundCall&iWithID=2343242&iWithFirstName=Daisy&iWithLastName=Duck&iWithType=G&Test=Test';
            oHUMSearch.softPhoneRoutingSearch();

            //Assert whether softphone details are populated in the interaction.
        //    system.assert(oHUMSearch.interactionLogController.oFetchInteraction.caller_name__c == 'James Thong');

            Test.stopTest();
        }          
    } 


    /*
     * Method Name   :    testConsumerTaxIDProvider 
     * Description   :    Insert Consumer Tax ID for Provider   
     * Return Type   :    void
     * Parameters    :    NA
     */     
    static testMethod void testConsumerTaxIDProvider() 
    {         

        User u = HUMTestDataHelperTest.createTestUser('TestUser1','System Administrator'); 
        insert u;
        
        List<HUMCipherCloudSettings__c> lstApplicationName = new List<HUMCipherCloudSettings__c> ();
        lstApplicationName.add(new HUMCipherCloudSettings__c(Name = 'APPLICATION_NAME',  Value__c = 'CRMR1'));
        insert lstApplicationName;
        
        HUMSearchLimit__c searchlimittest = new HUMSearchLimit__c(Name = 'Member_SearchLimit');           
        searchlimittest.Search_Limit__c = 123;
        insert searchlimittest;
            
        System.runas(u) 
        {               
            Account oAcc = HUMTestDataHelperTest.getAccount();
            oAcc.LastName = 'Account Test';
            oAcc.Account_External_ID__c = '12345';
            oAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            insert oAcc;
            HUMTestDataHelperTest.getHUMConstantsData();

            HUMMemberSearchControllerV2 oHUMMem = new HUMMemberSearchControllerV2();
            HUMGroupSearchControllerV2 oHUMGroup = new HUMGroupSearchControllerV2();
            HUMAgencySearchControllerV2 oHUMAgent = new HUMAgencySearchControllerV2();
            HUMProviderSearchControllerV2 oHUMProv = new HUMProviderSearchControllerV2();

            Test.startTest();

            HUMSearchControllerV2 oHUMSearch = new HUMSearchControllerV2();

            //Setting conroller type for Component based design approach.
            oHUMSearch.setComponentControllerMap('Member', oHUMMem);
            oHUMSearch.setComponentControllerMap('Group', oHUMGroup);
            oHUMSearch.setComponentControllerMap('Provider', oHUMProv);
            oHUMSearch.setComponentControllerMap('Agency', oHUMAgent);

            oHUMSearch.strSearchContextName = 'Provider';

            //Initializing Interaction Log
            oHUMSearch.interactionLogController = new HUMInteractionLogComponentControllerV2();
            oHUMSearch.bIsDataFromExternalSystem = true;
            oHUMSearch.sConsumerIDList = '2343242';
            oHUMSearch.strJsonAccObject = JSON.serialize(oAcc);
            oHUMSearch.populateInteractionWithDetails();


            Test.stopTest();
        }
        System.assertEquals(1,[Select ID from ConsumerID__c limit 1].size());  
    } 
      /*
     * Method Name   :    testshowAckPopUp 
     * Description   :    Method to test showAckPopUp()    
     * Return Type   :    void
     * Parameters    :    NA
     */
    static testMethod void testshowAckPopUp()  {
        User usr = HUMTestDataHelperTest.createTestUser('TestUser1','System Administrator'); 
        insert usr;    
        System.runas(usr) 
        {                        
            Test.startTest();
            Account acc = [select Id,Group_Number__c from account where LastName = 'Account Testing' limit 1]; 
             System.assertEquals(acc != null,true);
             HUMSearchControllerV2 oHUMSearch = new HUMSearchControllerV2();
             oHUMSearch.sInteractionAbout = acc.Id;
             oHUMSearch.showAckPopup();
             oHUMSearch.closeAckPopup();
             Test.stopTest();
        }
    }
}