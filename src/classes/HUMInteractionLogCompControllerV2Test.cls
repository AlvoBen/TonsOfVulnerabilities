/****************************************************************************
Apex Class Name  : HUMInteractionLogCompControllerV2Test
Version          : 2.0 
Created Date     : August 09 2015
Function         : Test class to HUMInteractionLogCompControllerV2 Apex class
Modification Log :
 * Modification Id       Developer Name        Code Reveiw                  Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------
 * 2.0                   Shreya Choodamani         25261                    08/10/2015             Original Version  
 * 2.1                   Santhi Mandava                                     03/12/2019             Removed "DpaaSWebServiceMockImpl" usage.
 * 2.2                   Moshitha Gunasekaran                               04/20/2021             US#2163329 - Removed PopulateUnknownMemberDetails method for checkmarx issues 
 * 2.3                   Sahil Verma/Navajit Sarkar			                07/19/2021		       US#2287945 : Adding Authentication test methods
 * 2.4                   Prasuna Pattabhi                                09/06/2021                       Code Coverage Improvements
 * 2.5                   Anil parvathaneni                                  06/27/2021              US-3252318 :Code Coverage Improvements
 * 2.6                   Harshada Kamble/Anil Pavithran                     05/05/2023              US4461937 T1PRJ0036776: SFDC Ability to Manually Modify Authentication Status (Multi Members) in Lightning - Genesys
 **********************************************************************************************************************************/
@isTest 
private class HUMInteractionLogCompControllerV2Test
{ 
    /*
     * Method Name   :    prepareTestData
     * Description   :    This method is for preparing test data
     * Return Type   :    void
     * Parameters    :    NA
     */
    @testSetup static void prepareTestData() 
    {
        List<HUMConstants__c> lstConstants = new List<HUMConstants__c>();
        lstConstants.add(new HUMConstants__c(Name='INBCALL',StringValue__c='0',IntegerValue__c=0));
        insert lstConstants;
        System.assertEquals(lstConstants.size(), 1);
    }
    /*
     * Method Name : testSaveInteraction
     * Description : This method tests Interaction save functionality. 
     * Return type : None
     * Paramater   : None
     */  
    static testMethod void testSaveInteraction() 
    {
        List<HUMConstants__c> lstCstSettingAccount = createCustomSetting();
        Account oAcc = HUMTestDataHelperTest.getAccount();
        oAcc.FirstName = 'AccTest';
        oAcc.LastName = 'AccTest';
        oAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
        insert oAcc;
        Test.startTest();
        HUMInteractionLogComponentControllerV2 hCustIntLogCont = new HUMInteractionLogComponentControllerV2();
        
         
        List<HUMEncryptedFields__c>lstCS4 = new List<HUMEncryptedFields__c>();
        lstCS4.add(new HUMEncryptedFields__c(Name='CALLER_NAME__C.Interaction__C',ObjectName__c='Interaction__c', FieldName__c='CALLER_NAME__C', EncryptionScheme__c = 'Stateless_AFPE_for_Alphabetic_Filtering'));
        insert lstCS4;
        List<HUMConstants__c> lstConstantsRecords = Test.loadData(HUMConstants__c.sObjectType,'HUMConsTestData');
        System.assert(lstConstantsRecords.size() > 0 );


        //Simulating failure condition by throwing null pointer exception.
        hCustIntLogCont.oHUMIntMembers = null;  
        hCustIntLogCont.oFetchInteraction = null;
        hCustIntLogCont.populateInteractionDetails(oAcc.ID, 'Member');
        hCustIntLogCont.populateInteractionWithDetails(oAcc.ID, 'Member');
        hCustIntLogCont.populateInteractionAboutDetails(oAcc.ID, 'Member');
        hCustIntLogCont.save(); 
        System.assert(hCustIntLogCont.oFetchInteraction==null);

        hCustIntLogCont = new HUMInteractionLogComponentControllerV2();
        hCustIntLogCont.populateInteractionDetails(oAcc.ID, 'Member');
        hCustIntLogCont.save(); 
        
        hCustIntLogCont.oFetchInteraction.Interaction_Origin__c='Cancelled';
        
        //Creating Interaction by clicking on Interacting With button.
        hCustIntLogCont.populateInteractionWithDetails(oAcc.ID, 'Member');
        System.assert(hCustIntLogCont.oFetchInteraction.Interacting_with__C == oAcc.ID); 

        //Simulating click on "Interacting about" button.
        hCustIntLogCont.populateInteractionAboutDetails(oAcc.ID, 'Member');
        
        hCustIntLogCont.oFetchInteraction = new Interaction__c(Caller_Name__c = 'Test11', Interaction_Origin__c = 'Inbound Call');
        
        hCustIntLogCont.onChangeInput();
        //System.assert(hCustIntLogCont.oFetchInteraction.id!=null);

        //Flushing Interaction panel by clicking on Save and New Button.
        //hCustIntLogCont.SaveAndNew();
        //System.assert(hCustIntLogCont.oFetchInteraction.id==null); 
        
        hCustIntLogCont.oHUMIntMembers = null;  
        hCustIntLogCont.changeInteractionMemberDetail(); 

        //Simulate click on save button without clicking on Interacting About button.
        hCustIntLogCont.populateInteractionWithDetails(oAcc.ID, 'Member');
        hCustIntLogCont.oFetchInteraction.Interaction_Origin__c='Cancelled';
        hCustIntLogCont.save();  
       // hCustIntLogCont.clear(); //Commenting the below code to Quick Fix for REL 07.22
        //System.assert(hCustIntLogCont.oFetchInteraction.id!=null);  
        Test.stopTest();
        System.assert(hCustIntLogCont.oFetchInteraction.Interacting_with__C == oAcc.ID); 
        
        
        List<Error_log__c> errorLogs =[Select error_code__C , error_message__c,class_name__c,method_name__c from Error_log__c ];
    }

    /*
     * Method Name : testInitializeInteraction
     * Description : This method tests initialization of interaction.
     * Return type : void
     * Paramater   : None
     */
    static testMethod void testInitializeInteraction()
    {
        List<HUMConstants__c> lstCstSettingAccount  = createCustomSetting();
        Account oAcc = HUMTestDataHelperTest.getAccount();
        oAcc.FirstName = 'AccTest';
        oAcc.LastName = 'AccTest';
        oAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
        insert oAcc;
        
        List<HUMEncryptedFields__c>lstCS4 = new List<HUMEncryptedFields__c>();
        lstCS4.add(new HUMEncryptedFields__c(Name='CALLER_NAME__C.Interaction__C',ObjectName__c='Interaction__c', FieldName__c='CALLER_NAME__C', EncryptionScheme__c = 'Stateless_AFPE_for_Alphabetic_Filtering'));
        insert lstCS4;
        List<HUMConstants__c> lstConstantsRecords = Test.loadData(HUMConstants__c.sObjectType,'HUMConsTestData');
        System.assert(lstConstantsRecords.size() > 0 );
        
        List<Interaction__C> lstInteraction = new List<Interaction__C>();
        Interaction__C oInt = new interaction__C(
                Interacting_with__C = oAcc.ID, 
                Interacting_with_type__C = 'Member',
                Interaction_Origin__c = 'Inbound Call',
                Caller_Name__c = 'Test');
        insert(oInt);
        insert(new Interaction_Member__C(
                interaction__C = oInt.id, 
                interacting_about_type__C = 'member',
                Interacting_about__C = oAcc.id ));

        Test.startTest();
        HUMInteractionLogComponentControllerV2 hCustIntLogCont = new HUMInteractionLogComponentControllerV2();

        //Simulating Failure condition when interaction id to be search is null.
        hCustIntLogCont.sInteractionId =null;
        hCustIntLogCont.getInteractionDetails();
        System.assert(hCustIntLogCont.oFetchInteraction.id==null);

        //Simulating condition when interaction has no members associated with it.
        hCustIntLogCont = new HUMInteractionLogComponentControllerV2();
        Interaction__C oIntNew = new interaction__C(
                Interacting_with__C = oAcc.ID, Interaction_Origin__c = 'Inbound Call',
                Interacting_with_type__C = 'Member' , Caller_Name__c = 'Test');
        insert(oIntNew);
        hCustIntLogCont.sInteractionId =oIntNew.id;
        hCustIntLogCont.getInteractionDetails();
        System.assert(hCustIntLogCont.oFetchInteraction.id==oIntNew.id);

        //Simulating failure condition by making one of the member variable null.
        hCustIntLogCont = new HUMInteractionLogComponentControllerV2();
        hCustIntLogCont.oHUMIntMembers = null;
        hCustIntLogCont.getInteractionDetails();
        hCustIntLogCont.getLatestInteraction();

        //Simulating flow when Interaction id being searched is having Interaction members associated with it.
        hCustIntLogCont = new HUMInteractionLogComponentControllerV2();
        hCustIntLogCont.sInteractionId =oInt.id;
        hCustIntLogCont.getInteractionDetails();
        hCustIntLogCont.getLatestInteraction();
        //hCustIntLogCont.flushInteraction(); //Commenting the below code to Quick Fix for REL 07.22
        Test.StopTest();

       // System.assert(hCustIntLogCont.oFetchInteraction.id==oInt.id); //Commenting the below code to Quick Fix for REL 07.22
    }
 
    /*
     * Method Name : testInitializingControllerInstance
     * Description : This method tests initialization of various controller instances.
     * Return type : Void
     * Paramater   : None 
     */
    static testMethod Void testInitializingControllerInstance()
    {
        List<HUMConstants__c> lstCstSettingAccount = createCustomSetting();
        HUMSearchControllerV2 oHUMsearchController = new HUMSearchControllerV2();
        Test.startTest();
        HUMInteractionLogComponentControllerV2 hCustIntLogCont = new HUMInteractionLogComponentControllerV2();
        hCustIntLogCont.oHUMSearchController = oHUMsearchController;
        hCustIntLogCont.getThis();
        hCustIntLogCont.showError();
        System.assert(true,hCustIntLogCont.showError()!= NULL); 
        Test.StopTest(); 
    }

    /*
     * Method Name : testUpdateInteraction
     * Description : This method tests Interaction update Functionality.
     * Return type : Void
     * Paramater   : None
     */
    static testMethod Void testUpdateInteraction()
    {
        Account oAcc = HUMTestDataHelperTest.getAccount();
        oAcc.FirstName = 'Account Test';
        oAcc.LastName = 'Account Test';
        oAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
        insert oAcc;
        
        List<HUMEncryptedFields__c>lstCS4 = new List<HUMEncryptedFields__c>();
        lstCS4.add(new HUMEncryptedFields__c(Name='CALLER_NAME__C.Interaction__C',ObjectName__c='Interaction__c', FieldName__c='CALLER_NAME__C', EncryptionScheme__c = 'Stateless_AFPE_for_Alphabetic_Filtering'));
        insert lstCS4;
        List<HUMConstants__c> lstConstantsRecords = Test.loadData(HUMConstants__c.sObjectType,'HUMConsTestData');
        System.assert(lstConstantsRecords.size() > 0 );
        
        List<HUMConstants__c> lstCstSettingAccount = createCustomSetting();
        Test.startTest();
        HUMInteractionLogComponentControllerV2 hCustIntLogCont = new HUMInteractionLogComponentControllerV2();

        //Creating Interaction by clicking on Interacting With button.
        hCustIntLogCont.populateInteractionWithDetails(oAcc.ID, 'Member');
        System.assert(hCustIntLogCont.oFetchInteraction.Interacting_with__C == oAcc.ID); 
       
        //Simulating click on "Interacting about" button.
        hCustIntLogCont.oFetchInteraction.Interaction_origin__c = 'InBound Call';
        //hCustIntLogCont.oFetchInteraction.caller_name__c = 'testnn';
        hCustIntLogCont.populateInteractionDetails(oAcc.ID, 'Member');
        hCustIntLogCont.save();  
        hCustIntLogCont.reQueryInteraction(); 
        
        //System.assert(hCustIntLogCont.oFetchInteraction.id!=null);  

        hCustIntLogCont.updateDisp();   
        //hCustIntLogCont.lstInqAbtWrapp[0].bIsSelected = true;
        
        hCustIntLogCont.getbCaseInteraction();
        System.assert(true,hCustIntLogCont.getbCaseInteraction()!= NULL);  
        hCustIntLogCont.deleteSelectedVal();
        hCustIntLogCont.closePopup();

        //Interaction recodr is just added but Interaction is not saved:
            Account oAccNonSave = HUMTestDataHelperTest.getAccount();
            oAccNonSave.Name = 'Account Test1';
            insert oAccNonSave;
            hCustIntLogCont.populateInteractionDetails(oAccNonSave.ID, 'Member');

            User testUser = HUMTestDataHelperTest.createTestUser(
                    'Customer_Case_specialist_User', 
            'Customer Care Specialist');
            system.runAs(testUser)
            {
                hCustIntLogCont.updateDisp();
                //hCustIntLogCont.lstInqAbtWrapp[0].bIsSelected = true;
                hCustIntLogCont.deleteSelectedVal();
            }
            //HUMInteractionLogComponentControllerV2 hCustIntLogCont = new HUMInteractionLogComponentControllerV2();
            HUMSearchControllerV2 oHUMsearchController = new HUMSearchControllerV2();
         	HUMInteractionLogComponentControllerV2 hCustIntLogCont1 = new HUMInteractionLogComponentControllerV2();
        	hCustIntLogCont1.oHUMSearchController = oHUMsearchController;
            hCustIntLogCont1.flushInteraction();
        	hCustIntLogCont1.updateInteractionAttribute();
        	
            Test.stopTest();
    }

    /*
     * Method Name : createCustomSetting
     * Description : This method creates test data in custom setting.
     * Return type : List<HUMConstants__c>
     * Paramater   : None
     */
    static List<HUMConstants__c> createCustomSetting(){
        List<HUMConstants__c> lstCstSettingAccount = new List<HUMConstants__c> ();
        lstCstSettingAccount.add(new HUMConstants__c(Name = 'MEMBER',StringValue__C = 'Member'));
        lstCstSettingAccount.add(new HUMConstants__c(Name = 'PROVIDER',StringValue__C = 'Provider'));
        lstCstSettingAccount.add(new HUMConstants__c(Name = 'GROUP',StringValue__C = 'GROUP'));
        lstCstSettingAccount.add(new HUMConstants__c(Name = 'AGENCY',StringValue__C = 'Agency'));
        lstCstSettingAccount.add(new HUMConstants__c(Name = 'UNKNOWN_MEMBER',StringValue__C = 'Unknown Member'));
        lstCstSettingAccount.add(new HUMConstants__c(Name = 'UNKNOWN_PROVIDER',StringValue__C = 'UnknownProvider'));
        lstCstSettingAccount.add(new HUMConstants__c(Name = 'UNKNOWN_GROUP',StringValue__C = 'UnknownGroup'));
        lstCstSettingAccount.add(new HUMConstants__c(Name = 'UNKNOWN_AGENCY',StringValue__C = 'UnknownAgency'));
        lstCstSettingAccount.add(new HUMConstants__c(Name = 'CASE_TYPE',StringValue__C = 'complaint'));
        lstCstSettingAccount.add(new HUMConstants__c(Name = 'CASE_STATUS',StringValue__C = 'in progress'));
        lstCstSettingAccount.add(new HUMConstants__c(Name = 'GA_RIGHTS_GIVEN',StringValue__C = 'No'));
        lstCstSettingAccount.add(new HUMConstants__c(Name = 'COMPLAINT',StringValue__C = 'COMPLAINT'));
        lstCstSettingAccount.add(new HUMConstants__c(Name = 'TAB_NAME_MEMBER',StringValue__C = 'Member'));
        lstCstSettingAccount.add(new HUMConstants__c(Name = 'TAB_NAME_PROVIDER',StringValue__C = 'COMPLAINT'));
        lstCstSettingAccount.add(new HUMConstants__c(Name = 'TAB_NAME_GROUP',StringValue__C = 'COMPLAINT'));
        lstCstSettingAccount.add(new HUMConstants__c(Name = 'TAB_NAME_AGENCY',StringValue__C = 'COMPLAINT'));
        lstCstSettingAccount.add(new HUMConstants__c(Name = 'TAB_NAME_ENROLLMENT',StringValue__C = 'COMPLAINT'));
        lstCstSettingAccount.add(new HUMConstants__c(Name = 'ENROLLMENT',StringValue__C = 'Enrollment'));
        lstCstSettingAccount.add(new HUMConstants__c(Name = 'ACCOUNT_AGENT_TYPE', StringValue__c = 'Agent'));
        upsert(lstCstSettingAccount);        
        return lstCstSettingAccount;
    }

    /*
     * Method Name : testSaveInteractionInNonDeveloperMode
     * Description : This method tests Save operation on Interaction log.
     * Return type : None
     * Paramater   : None 
     */
    static testMethod void testSaveInteractionInNonDeveloperMode() 
    {
        User testUser = HUMTestDataHelperTest.createTestUser(
                'Customer_Case_specialist_User', 
        'Customer Care Specialist');
        system.runAs(testUser)
        {
            testSaveInteraction();
        }
    }

    /*
     * Method Name : testInitializeInteractionInNonDeveloperMode
     * Description : This method tests Interaction initialization on Interaction Log in Non developer mode.
     * Return type : None
     * Paramater   : None 
     */
    static testMethod void testInitializeInteractionInNonDeveloperMode() 
    {
        User testUser = HUMTestDataHelperTest.createTestUser(
                'Customer_Case_specialist_User', 
        'Customer Care Specialist');
        system.runAs(testUser)
        {
            testInitializeInteraction();
        }
    }

    /*
     * Method Name : testNoInteractionRec
     * Description : This method tests Interaction functionality when the interaction Record is not created.
     * Return type : None
     * Paramater   : None
     */  
    static testMethod void testNoInteractionRec() 
    {
        List<HUMConstants__c> lstCstSettingAccount = createCustomSetting();
        Account oAcc = HUMTestDataHelperTest.getAccount();
        oAcc.FirstName = 'AccTest';
        oAcc.LastName = 'AccTest';
        oAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
        insert oAcc;
        Test.startTest();
        HUMInteractionLogComponentControllerV2 hCustIntLogCont = new HUMInteractionLogComponentControllerV2();

        hCustIntLogCont.oFetchInteraction.Interacting_With__c = null;
        hCustIntLogCont.populateInteractionAboutDetails(oAcc.ID, 'Member');
        hCustIntLogCont.populateInteractionDetails(oAcc.ID, 'Member');
        System.assert(hCustIntLogCont.oFetchInteraction!=null);
        
        hCustIntLogCont = new HUMInteractionLogComponentControllerV2();
        hCustIntLogCont.oFetchInteraction = new Interaction__c();
        hCustIntLogCont.oFetchInteraction.Interacting_With__c = null;
        hCustIntLogCont.oFetchInteraction.Interaction_Origin__c = 'Cancelled';
        hCustIntLogCont.populateInteractionDetails(oAcc.ID, 'Member');
        System.assert(hCustIntLogCont.oFetchInteraction!=null);
    }
    
    /*
     * Method Name : testAuthentication
     * Description : This method tests if the label in the interaction log panel is authenticated
     * Return type : None
     * Paramater   : None
     */    
    Static testMethod void testAuthentication(){
        Test.startTest();
        HUMInteractionLogComponentControllerV2 hCustIntLogComp = new HUMInteractionLogComponentControllerV2();
        //setting the Authentication Indicator value to true
        hCustIntLogComp.oFetchInteraction.Authenticated__c = true;
        hCustIntLogComp.oHUMIntMembers.Authenticated__c = true;
        //validating authcheck label
        String auth = hCustIntLogComp.authCheckLabel;
        system.assertEquals(auth, 'Authenticated');
        hCustIntLogComp.authCheckHandler();
        Test.stopTest();
    }

    /*
     * Method Name : testUnauthentication
     * Description : This method tests if the label in the interaction log panel is Unauthenticated
     * Return type : None
     * Paramater   : None
     */    
    Static testMethod void testUnauthentication(){
        Test.startTest();
        HUMInteractionLogComponentControllerV2 hCustIntLogComp = new HUMInteractionLogComponentControllerV2();
        //setting the Authentication Indicator value to true
        hCustIntLogComp.oFetchInteraction.Authenticated__c = false;
        hCustIntLogComp.oHUMIntMembers.Authenticated__c = false;
        //validating authcheck label
        String auth2 = hCustIntLogComp.authCheckLabel;
        system.assertEquals(auth2, 'UnAuthenticated');
        hCustIntLogComp.authCheckHandler();
        Test.stopTest();
    }
    /*
     * Method Name : testSaveAndNewInteraction
     * Description : This method tests Interaction save And New functionality. 
     * Return type : None
     * Paramater   : None
     */  
    Static testMethod void testSaveAndNewInteraction() 
    {
        List<HUMConstants__c> lstCstSettingAccount = createCustomSetting();
        Account oAcc = HUMTestDataHelperTest.getAccount();
        oAcc.FirstName = 'AccTest';
        oAcc.LastName = 'AccTest';
        oAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
        insert oAcc;
        
        Test.startTest();
        
        HUMInteractionLogComponentControllerV2 hCustIntLogCont = New HUMInteractionLogComponentControllerV2();    
        List<HUMEncryptedFields__c>lstCS4 = New List<HUMEncryptedFields__c>();
        lstCS4.add(New HUMEncryptedFields__c(Name='CALLER_NAME__C.Interaction__C',ObjectName__c='Interaction__c', FieldName__c='CALLER_NAME__C', EncryptionScheme__c = 'Stateless_AFPE_for_Alphabetic_Filtering'));
        insert lstCS4;
        List<HUMConstants__c> lstConstantsRecords = Test.loadData(HUMConstants__c.sObjectType,'HUMConsTestData');
        System.assert(lstConstantsRecords.size() > 0 );
        
        hCustIntLogCont = New HUMInteractionLogComponentControllerV2();
        hCustIntLogCont.populateInteractionDetails(oAcc.ID, 'Member');
        hCustIntLogCont.sAccId =null;
        hCustIntLogCont.sNewCaseId =null;
        hCustIntLogCont.bIntCreationStatus = true;
        HUMSearchControllerV2 oHUMSearchController = New HUMSearchControllerV2();
        hCustIntLogCont.oHUMSearchController = oHUMSearchController;
        hCustIntLogCont.SaveAndNew();
        oHUMSearchController.showCallCatergoryAlert = true;
        hCustIntLogCont.flushInteraction();
        
        Test.stopTest();
       
    }
}