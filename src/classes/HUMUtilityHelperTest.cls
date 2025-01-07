/**************************************************************
Apex Class: HUMUtilityHelperTest
Description: Test Class for HUMUtilityHelper class
Created By: Pranali Choudhari
Created On:  06/10/2014
Modification Log: 
 * Developer Name                  Code Review            Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * Pranali Choudhari               17176                  06/10/2014                 Original version
 * Shruthi Karanth                 17862                  09/09/2014                 Updated as per changes to class
 * Chaitanya Kumar                 17862                  09/10/2014                 Include getEncryptedAccount,getEncryptedConsumerIds
 * Chaitanya Kumar                 17862                  09/11/2014                 Updated getEncryptedConsumerIds based on the changed defination of method being tested
 * Shruthi Karanth                 20262                  11/27/2014                 Updated TestHUMCreateSoftPhoneRoutingRecord as per validation rules.
 * Kritika Popat                   20262                  01/13/2015                 Fixed as part of CC Review 20262
 * Asha Deepika Koyyana            20572                  01/28/2015                 Fixed test class failure for Defect#164139  
 * SuryaKumari Medicherla          21614                  03/24/2015                 Added methods to cover doSort() method
 * Manish Kumar                    22546                  04/22/2015                 Added methods to cover decryption login in main class.
                                                                                     Lines modified : 375 - 402
 * Santhi Mandava                                         07/15/2015                 Included testsetup method and updated all test methods accordingly.
 * Harshith Mandya                 29217                  01/12/2016                 Included all test methods added as a part of search customization.
 * Harshith Mandya                 27721                  05/18/2016                 Marked false for Assert result in TestValidateAgencyName method to check for special characters.
 * Pradeepkumar Dani               99570                  06/08/2017                 Intent__c.Classification__c & Intent__c.Etl_Idtfr__c Clean up
 * Pradeepkumar Dani              100996                  06/13/2017                 Classification__c.Classification_Type__c & Etl_Idftr__c Clean Up
 * Pradeepkumar Dani              102268                  06/16/2017                 Case Clean Up (Classification__c,Intent__c,Classification_Ctci__c,Intent_Ctci__c,Classification_Text__c,Intent_Text__c)
 * Prasad Jandhyala               180277                  02/07/2018                 REQ - 335173 : Added logic For redirection To CaseClosed_Edit_VF_HUM
 * Sunil Bukya                    262341                  07/26/2018                 Implemented platform encryption for Account related fields
 * Santhi Mandava                                         03/12/2019                 Removed "DpaaSWebServiceMockImpl" usage.
 * Santhi Mandava                 348723                  04/02/2019                 Cleanup cipher cloud references
 * Chetan Sindhe				  400121			      07/15/2019				 Added new method getStateFullNameValue to derive State Names.
 * Mohammed Noor                                          12/16/2019                 Fix test class failure because of moving Case Workflows to Trigger. 
 * Keerti Durbha                                          05/12/2020                738134- PR00091318 - MF 9 - OSB ON/off functionality        
 * Asish Behera                                           05/20/2020                 Code coverage for convertDateformat method  
 * Bhumika S                                              10/14/2022                 User Story 3883795: T1PRJ0533784 -CR 6/MF 25 - Enhance the search capability Functionality on search enrollment tab within CRMs-CBIS
 * Kiran Bhuvanagiri									  12/09/2022				 INC 2085696 OMNI channel - Quick base #1244 are not pulling in to the correct 6-13 da queue in Omni
 * Moshitha G                                             01/23/2023                 US3788451 - T1PRJ0337737 - 2022 - SF- additional Access for Mulesoft user for Case and Interaction details
 ********************************************************************************************************************************************************************************************************************************************************/


@isTest
private class HUMUtilityHelperTest
{
    /**
    * Returns void
    * <p>
    * Method to setup Testdata 
    *
    * @return  Void
    * @see
    */
    @testSetup static void setup() 
    {    
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser; 
        System.runas(oUser) 
        {
            Integration_Mapping__c intMap = new Integration_Mapping__c();
            intMap.Mapping__c = 'Mapping:test;HUM';
            insert intMap;
            system.assertEquals(intMap.Mapping__c == 'Mapping:test;HUM',true);
            
            HUMMember_Search_State_Mapping__c cstsetting = new HUMMember_Search_State_Mapping__c(Name = 'test', State__c = 'California', State_Code__c = 'CA');
            insert cstsetting;
            system.assertEquals(cstsetting.Name == 'test',true);
            
            Account oacc = HUMTestDataHelperTest.getAccount();
            oacc.Name = 'Test Account';
            insert oacc;
            system.assertEquals(oacc.Name == 'Test Account',true);
            
            Interaction__c objInterac = new Interaction__c();
            objInterac.Caller_Name__c = 'Test Caller';
            objInterac.Interaction_Origin__c = 'Inbound Call';
            objInterac.UUID__c = '12345';
            insert objInterac;
            system.assertEquals(objInterac.Interaction_Origin__c == 'Inbound Call',true);
            
            Classification__c classificationRecDec = new Classification__c();
            classificationRecDec.Name = 'Benefits';
            classificationRecDec.Is_Active__c = true;
            insert classificationRecDec;
            system.assertEquals(classificationRecDec.Name == 'Benefits',true);
            
            Intent__c intentRec = new Intent__c();
            intentRec.Name = 'Rewards';
            intentRec.Is_Active__c = true;
            insert intentRec;
            system.assertEquals(intentRec.name == 'Rewards',true);
            
            Case caseObj = HUMTestDataHelperTest.getCase();
            caseObj.Classification_ID__c = classificationRecDec.id;
            caseObj.Intent_ID__c = intentRec.id;
            caseObj.Interaction_ID__c = objInterac.Id;
            insert caseObj;
            system.assertEquals(caseObj.Interacting_With_Name__c == 'Test',true);
            
            Integration__c inte = new Integration__c();
            inte.Name = String.escapeSingleQuotes('Soft Phone Routing');
            insert inte;
            
            List<HUMAccountCaseRecordTypes__c> HUMAccountCaseRecordTypesList = new List<HUMAccountCaseRecordTypes__c>();
            HUMAccountCaseRecordTypes__c HUMAccountCaseRecordTypes1 = new HUMAccountCaseRecordTypes__c(Name = 'Group' ,Account_RecordType__c = 'Group' , Case_RecordType__c = 'Group Case');
            HUMAccountCaseRecordTypesList .add(HUMAccountCaseRecordTypes1);
            HUMAccountCaseRecordTypes__c HUMAccountCaseRecordTypes2 = new HUMAccountCaseRecordTypes__c(Name = 'Agent/Broker' ,Account_RecordType__c = 'Agent/Broker' , Case_RecordType__c = 'Agent/Broker Case');
            HUMAccountCaseRecordTypesList .add(HUMAccountCaseRecordTypes2);        
            HUMAccountCaseRecordTypes__c HUMAccountCaseRecordTypes3 = new HUMAccountCaseRecordTypes__c(Name = 'Provider' ,Account_RecordType__c = 'Provider' , Case_RecordType__c = 'Provider Case');
            HUMAccountCaseRecordTypesList .add(HUMAccountCaseRecordTypes3);        
            HUMAccountCaseRecordTypes__c HUMAccountCaseRecordTypes4 = new HUMAccountCaseRecordTypes__c(Name = 'Member' ,Account_RecordType__c = 'Member' , Case_RecordType__c = 'Member Case');
            HUMAccountCaseRecordTypesList .add(HUMAccountCaseRecordTypes4);        
            insert HUMAccountCaseRecordTypesList;
            system.assertEquals(HUMAccountCaseRecordTypesList[0].Name == 'Group',true);
            
            HUMConstants__c cstHumConst = new HUMConstants__c(Name ='testcustomsetting',StringValue__c = 'test',IntegerValue__c = 123);
            insert cstHumConst;
            system.assertEquals(cstHumConst.Name == 'testcustomsetting',true);
            
            Account oAccount = HUMTestDataHelperTest.getAccount();
            oAccount.Name = 'Test Account';
            insert oaccount;
            
            HUMTestDataHelperTest.getHUMConstantsData();            
            
            List<HUMCipherCloudSettings__c> lstApplicationName = new List<HUMCipherCloudSettings__c> ();
            lstApplicationName.add(new HUMCipherCloudSettings__c(Name = 'APPLICATION_NAME',  Value__c = 'CRMR1'));
            insert lstApplicationName;
            system.assertEquals(lstApplicationName[0].Name == 'APPLICATION_NAME',true);
            
            Account acc = HUMTestDataHelperTest.getAccount();
            acc.Name='Test Field';
            acc.Account_External_Id__c='1231';
            acc.RecordtypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Agent/Broker').getRecordTypeId();
            insert acc;
            system.assert(acc.Name == 'Test Field'); 
            
        }
    }
    
    /**
    * Returns void
    * <p>
    * test method for tgetSoftPhoneIntegrationMapping() method of HUMUtilityHelper class 
    *
    * @return  Void
    * @see
    */
    static testMethod void testgetSoftPhoneIntegrationMapping()
    {
        User oUser = [select id, name,lastname from user where lastname='test User1' limit 1];
        system.assertEquals(oUser.lastname =='test User1',true); 
        system.runAs(oUser)
        {   
            Test.StartTest();
            List<Integration_Mapping__c> intMap = [Select Id,Mapping__c  from Integration_Mapping__c];
            Map<String,String> mapSoftPhone = HUMUtilityHelper.getSoftPhoneIntegrationMapping(intMap[0]);           
            Test.stopTest();
            System.assert(mapSoftPhone.size()>0);
        }
    }

    /**
    * Returns void
    * <p>
    * test method for HUMCreateSoftPhoneRoutingRecord() method of HUMUtilityHelper class  
    *
    * @return  Void
    * @see
    */
    static testMethod void TestHUMCreateSoftPhoneRoutingRecord()
    {
        User oUser = [select id, name,lastname from user where lastname='test User1' limit 1];
        system.assertEquals(oUser.lastname =='test User1',true); 
        system.runAs(oUser)
        { 
            Test.startTest();
            List<Case> lstRes = [Select Id,Complaint__c ,Interaction_ID__c,Interacting_About_Type__c ,Interacting_With_Type__c ,G_A_Rights_Given__c ,Interacting_With_Name__c ,CTCI_List__r.Classification__r.Name, CTCI_List__r.Intent__r.Name from Case];
            Interaction__c oic = HUMTestDataHelperTest.getInteraction() ;
            String sReturnValue = '';    
            HUMUtilityHelper.createSoftPhoneRoutingRecord(lstRes[0].id);
            Case cs = [Select Id,Interaction_ID__c from Case where Id = :lstRes[0].id];
            cs.Interaction_ID__c = oic .Id;
            update cs;
            sReturnValue = HUMUtilityHelper.createSoftPhoneRoutingRecord(lstRes[0].id);
            System.assertnotEquals(sReturnValue, 'true');
            Test.StopTest();
        }
    }

    /**
    * Returns void
    * <p>
    * test method for getStateList() method of HUMUtilityHelper class  
    *
    * @return  Void
    * @see
    */
    static testMethod void TestgetStateList()
    {
        User oUser = [select id, name,lastname from user where lastname='test User1' limit 1];
        system.assertEquals(oUser.lastname =='test User1',true); 
        system.runAs(oUser)
        { 
            Test.startTest();
            List<SelectOption> lstSelectOptn = HUMUtilityHelper.getstateValue();
            List<SelectOption> lstSelectOptn1 = HUMUtilityHelper.getStateFullNameValueWithCode();
            System.assert(lstSelectOptn!=null); 
            Test.stopTest(); 
        }
    }
    
    /**
    * Returns void
    * <p>
    * test method for doSort() method of HUMUtilityHelper class  
    *
    * @return  Void
    * @see
    */
    static testMethod void TestSortMethod()
    {
         User oUser = [select id, name,lastname from user where lastname='test User1' limit 1];
        system.assertEquals(oUser.lastname =='test User1',true); 
        system.runAs(oUser)
        { 
            List<String> lstStrings = new List<String>();
            Set<String> setStrings = new Set<String>();
            setStrings.add('Calls');
            setStrings.add('CRU');
            setStrings.add('eCommerce');
            Test.startTest();
            lstStrings = HUMUtilityHelper.doSort(setStrings);
            Test.stopTest();
            System.assert(lstStrings != NULL); 
        }
    }

    /**
    * Returns void
    * <p>
    * test method for GetCaseRecordType method of HUMUtilityHelper class  
    *
    * @return  Void
    * @see
    */ 
    static testMethod void testGetCaseRecordType()
    {
         User oUser = [select id, name,lastname from user where lastname='test User1' limit 1];
        system.assertEquals(oUser.lastname =='test User1',true); 
        system.runAs(oUser)
        {
            Test.startTest();
            String sAccRecTypeGrp = 'Group';
            Id iCaseRecGrp = HUMUtilityHelper.getCaseRecordType(sAccRecTypeGrp);
            System.assert(iCaseRecGrp!=null);
    
            String sAccRecTypeAgt = 'Agent/Broker';
            Id iCaseRecAgt = HUMUtilityHelper.getCaseRecordType(sAccRecTypeAgt);
            System.assert(iCaseRecAgt!=null);
    
            String sAccRecTypePrv = 'Provider';
            Id iCaseRecPrv = HUMUtilityHelper.getCaseRecordType(sAccRecTypePrv);
            System.assert(iCaseRecPrv!=null);
    
            String sAccRecTypeMem = 'Member';
            Id iCaseRecMem = HUMUtilityHelper.getCaseRecordType(sAccRecTypeMem);
            System.assert(iCaseRecMem!=null);
            Test.stopTest();
        }
    }

    /**
    * Returns void
    * <p>
    * test method for GetStringValue method of HUMUtilityHelper class  
    *
    * @return  Void
    * @see
    */
    static testMethod void testGetStringValue()
    {
        User oUser = [select id, name,lastname from user where lastname='test User1' limit 1];
        system.assertEquals(oUser.lastname =='test User1',true); 
        system.runAs(oUser)
        {
            Test.starttest();
            String sValue = HUMUtilityHelper.getStringValue('testcustomsetting');
            System.assertEquals(sValue, 'test');    
            Decimal dValue = HUMUtilityHelper.getIntegerValue('testcustomsetting');
            System.assertEquals(dValue, 123);
            Test.stopTest();
        }
    }

    /**
    * Returns void
    * <p>
    * test method for GetRecordTypeId method of HUMUtilityHelper class  
    *
    * @return  Void
    * @see
    */
    static testMethod void testGetRecordTypeId()
    {
         User oUser = [select id, name,lastname from user where lastname='test User1' limit 1];
        system.assertEquals(oUser.lastname =='test User1',true); 
        system.runAs(oUser)
        {
            List<Account> lstAcc = [Select Id,Name from Account where Name = 'Test Account'];
            Schema.Sobjecttype sSobject = lstAcc[0].getSobjectType() ;
            String sRecordTypeName = 'Provider';
            String sEmail = 'test@humana.com';
            Id iRecId;
            Test.startTest();
            iRecId = HUMUtilityHelper.getRecordTypeID(sSobject,sRecordTypeName);
            HUMUtilityHelper.valdiateEmail(sEmail);
            Test.stopTest();
            System.assert(iRecId!=null);
        }
    }
    
    
    /**
    * Returns void
    * <p>
    * GEt the list of Consumer ID based on Account ID and list of Tax IDs  
    *
    * @return  Void
    * @see
    */
    static testMethod void getEncryptedConsumerIds() 
    {   
        User oUser = [select id, name,lastname from user where lastname='test User1' limit 1];
        system.assertEquals(oUser.lastname =='test User1',true); 
        system.runAs(oUser)
        {    
            List<Account> lstAcc = [Select Id from Account where Name ='Test Field'];
            List<ConsumerId__c> lstConsumerIds;
            Test.startTest();
            lstConsumerIds = HUMUtilityHelper.getEncryptedConsumerIds(lstAcc[0].id, new Map<String,String>{'23432'=>'234'});
            Test.stopTest();
            //Assert a ConsumerId record is created
            System.assertEquals(lstConsumerIds.size(),1);    
        }   
    }
    
     /**
    * Returns void
    * <p>
    * GEt the Encrypted Account object  
    *
    * @return  Void
    * @see
    */
    static testMethod void getEncryptedAcount() 
    {   
        User oUser = [select id, name,lastname from user where lastname='test User1' limit 1];
        system.assertEquals(oUser.lastname =='test User1',true); 
               
        System.runas(oUser) 
        {    
            Account acc = HUMTestDataHelperTest.getAccount();
            acc.Name='Test Field';
            acc.Account_External_Id__c='1231';

            Test.startTest();
            acc =HUMUtilityHelper.getEncryptedAccount(acc);
            Test.stopTest();
            System.assert(acc!=null);   
        }    
    }   
      
   /**
    * Returns void
    * <p>
    * Get the masked data for encrypted data. 
    *
    * @return  Void
    * @see
    */ 
    static testMethod void getMaskedSSN() 
    {
        User oUser = [select id, name,lastname from user where lastname='test User1' limit 1];
        system.assertEquals(oUser.lastname =='test User1',true);
        System.runas(oUser) 
        {    
            set<String> setMemberIds = new set<String>{'Clear Text'};
            Test.startTest();
            HUMUtilityHelper.maskSSN(setMemberIds);
            Test.stopTest();
        } 
    }
    
    /**
    * Returns void
    * <p>
    * Validate Member Id 
    *
    * @return  Void
    * @see
    */
    static testMethod void testValidateMemeberId() 
    {
        
        Boolean validMemberId = HUMUtilityHelper.validateMemberId('123435');
        System.assertEquals(validMemberId,true);
        try
        { 
            Boolean invalidMemberId = HUMUtilityHelper.validateMemberId('1234*%');
        }
        Catch(Exception e)
        {
            System.assertEquals(HUMConstants.HUMSearchMemberIdAlphaNumericValidation,e.getMessage());
        }
    }
    
    /**
    * Returns void
    * <p>
    * Validate Member Id length 
    *
    * @return  Void
    * @see
    */
    static testMethod void testMemberIdLength() 
    {
        Boolean validMemberId = HUMUtilityHelper.memberIdLength('123435');
        System.assertEquals(validMemberId,true);
        try
        { 
            Boolean invalidMemberId = HUMUtilityHelper.memberIdLength('2');
        }
        Catch(Exception e)
        {
            System.assertEquals(HUMConstants.HumSearchMemberIdCharacterValidation,e.getMessage());
        }
    }
    
    /**
    * Returns void
    * <p>
    * This method is used for checking minimum 2 character length for suffix field in member search screen. 
    *
    * @return  Void
    * @see
    */
    static testMethod void testValidateSuffixLength() 
    {
        Boolean validMemberId = HUMUtilityHelper.validateSuffixLength('1234');
        System.assertEquals(validMemberId,true);
        try
        { 
            Boolean invalidMemberId = HUMUtilityHelper.validateSuffixLength('1');
        }
        Catch(Exception e)
        {
            System.assertEquals(HUMConstants.HUMSearchSuffixCharacterValidation,e.getMessage());
        }
    }
    
    /**
    * Returns void
    * <p>
    * This method is used for validating suffix field whether it is numeric or not. 
    *
    * @return  Void
    * @see
    */ 
    static testMethod void testValidateSuffix() 
    {
        Boolean validMemberId = HUMUtilityHelper.validateSuffix('1234');
        System.assertEquals(validMemberId,true);
        try
        { 
            Boolean invalidMemberId = HUMUtilityHelper.validateSuffix('ab');
        }
        Catch(Exception e)
        {
            System.assertEquals(HUMConstants.HumSearchSuffixNumericValidation,e.getMessage());
        }
    }
    
    /**
    * Returns void
    * <p>
    * This method is used for validating First Name whether it is alphanumeric or not. 
    *
    * @return  Void
    * @see
    */ 
    static testMethod void testvalidateFirstName() 
    {
        Boolean validMemberId = HUMUtilityHelper.validateFirstName('Test');
        System.assertEquals(validMemberId,true);
        try
        { 
            Boolean invalidMemberId = HUMUtilityHelper.validateFirstName('Test*%');
        }
        Catch(Exception e)
        {
            System.assertEquals(HUMConstants.HumSearchFirstNameAlphaNumericValidation,e.getMessage());
        }
    }
    
   /**
    * Returns void
    * <p>
    * This method is used for validating Last Name to allow alphanumeric,spaces and special characters 
    *
    * @return  Void
    * @see
    */
    static testMethod void testvalidateLastName() 
    {
        Boolean validMemberId = HUMUtilityHelper.validateLastName('Test');
        System.assertEquals(validMemberId,true);
        try
        { 
            Boolean invalidMemberId = HUMUtilityHelper.validateLastName('Test*%');
        }
        Catch(Exception e)
        {
            System.assertEquals(HUMConstants.HumSearchLastNameAlphaNumericValidation,e.getMessage());
        }
    }
    
    /**
    * Returns void
    * <p>
    * This method is used for validating minimum 2 character length for Last Name if it is not blank. 
    *
    * @return  Void
    * @see
    */
    static testMethod void testlastNameLength() 
    {
        Boolean validMemberId = HUMUtilityHelper.lastNameLength('Test');
        System.assertEquals(validMemberId,true);
        try
        { 
            Boolean invalidMemberId = HUMUtilityHelper.lastNameLength('T');
        }
        Catch(Exception e)
        {
            System.assertEquals(HUMConstants.HumSearchLastNameCharacterValidation,e.getMessage());
        }
    }
    
    /**
    * Returns void
    * <p>
    * This method is used for validating minimum 2 character length for Last Name if it is not blank. 
    *
    * @return  Void
    * @see
    */
    static testMethod void testvalidateGroupNumber() 
    {
        Boolean validGroupNumber = HUMUtilityHelper.validateGroupNumber('Test');
        System.assertEquals(validGroupNumber,true);
        try
        { 
            Boolean invalidGroupNumber = HUMUtilityHelper.validateGroupNumber('Test*%');
        }
        Catch(Exception e)
        {
            System.assertEquals(HUMConstants.HumSearchGroupNumberAlphaNumericValidation,e.getMessage());
        }
    }
    
    /**
    * Returns void
    * <p>
    * This method is used for validating Birthdate field
                         data(considering leap year) 
    *
    * @return  Void
    * @see
    */
    static testMethod void testvalidateBirthdateLeapYear() 
    {
        Boolean validBirthdateLeapYear = HUMUtilityHelper.validateBirthdateLeapYear('02/02/2000');
        System.assertEquals(validBirthdateLeapYear,true);
        try
        { 
            Boolean invalidBirthdateLeapYear = HUMUtilityHelper.validateBirthdateLeapYear('02/02/02/2000');
        }
        Catch(Exception e)
        {
            System.assertEquals(HUMConstants.HumMemberSearchBirthDateValidation,e.getMessage());
        }
         try
        { 
            Boolean invalidBirthdateLeapYear = HUMUtilityHelper.validateBirthdateLeapYear('02/32/2000');
        }
        Catch(Exception e)
        {
            System.assertEquals(HUMConstants.HumMemberSearchBirthDateValidation,e.getMessage());
        }
         try
        { 
            Boolean invalidBirthdateLeapYear = HUMUtilityHelper.validateBirthdateLeapYear('02/02/3001');
        }
        Catch(Exception e)
        {
            System.assertEquals(HUMConstants.HumMemberSearchBirthDateValidation,e.getMessage());
        }
    }
    
    /**
    * Returns void
    * <p>
    * This method is used for validating Birthdate field data 
    *
    * @return  Void
    * @see
    */
    static testMethod void testvalidateBirthdate() 
    {
        Boolean validBirthdate = HUMUtilityHelper.validateBirthdate('02/02/2000');
        System.assertEquals(validBirthdate,true);
        try
        { 
            Boolean invalidBirthdate = HUMUtilityHelper.validateBirthdate('02/02/2101');
        }
        Catch(Exception e)
        {
            System.assertEquals(HUMConstants.HumMemberSearchBirthDateValidation,e.getMessage());
        }
         try
        { 
            Boolean invalidBirthdate = HUMUtilityHelper.validateBirthdate('02/32/20a0');
        }
        Catch(Exception e)
        {
            System.assertEquals(HUMConstants.HumMemberSearchBirthDateValidation,e.getMessage());
        }
         try
        { 
            Boolean invalidBirthdate = HUMUtilityHelper.validateBirthdate('02/32/2001/2001');
        }
        Catch(Exception e)
        {
            System.assertEquals(HUMConstants.HumMemberSearchBirthDateValidation,e.getMessage());
        }
    }
    
    /**
    * Returns void
    * <p>
    * This method is used for validating Postal Code for Unknown accounts. 
    *
    * @return  Void
    * @see
    */
    static testMethod void testvalidateUnknownPostalCode() 
    {
        Boolean validPostalCode = HUMUtilityHelper.validateUnknownPostalCode('57710');
        System.assertEquals(validPostalCode,true);
        try
        { 
            Boolean invaliPostalCode = HUMUtilityHelper.validateUnknownPostalCode('577%a');
        }
        Catch(Exception e)
        {
            System.assertEquals(HUMConstants.HumZipcodeCharacterLimit,e.getMessage());
        }
        
    }
    
   /**
    * Returns void
    * <p>
    * This method is used for validating Postal Code. 
    *
    * @return  Void
    * @see
    */
    static testMethod void testvalidatePostalCode() 
    {
        Boolean validPostalCode = HUMUtilityHelper.validatePostalCode('57710');
        System.assertEquals(validPostalCode,true);
        try
        { 
            Boolean invaliPostalCode = HUMUtilityHelper.validatePostalCode('57777777%a');
        }
        Catch(Exception e)
        {
            System.assertEquals(HUMConstants.HumZipcodeCharacterLimit,e.getMessage());
        }
        
    }
    
    /**
    * Returns void
    * <p>
    * This method is used for validating Phone
                         contains only 10 digits & checking for only numerical values in Phone. 
    *
    * @return  Void
    * @see
    */
    static testMethod void testvalidatePhone() 
    {
        Boolean validPhone = HUMUtilityHelper.validatePhone('(123) 456-7890');
        System.assertEquals(validPhone,true);
        try
        { 
            Boolean invaliPostalCode = HUMUtilityHelper.validatePhone('577%a');
        }
        Catch(Exception e)
        {
            System.assertEquals(HUMConstants.HumPhoneCharacterLimit,e.getMessage());
        }
         try
        { 
            Boolean invaliPostalCode = HUMUtilityHelper.validatePhone('5779999999');
        }
        Catch(Exception e)
        {
            System.assertEquals(HUMConstants.HumPhoneValidationForMember,e.getMessage());
        }
        
    }
    
    /**
    * Returns void
    * <p>
    * This method is used for validating Agent Id has numbers only. 
    *
    * @return  Void
    * @see
    */
    static testMethod void testvalidateAgentId() 
    {
        Boolean validAgentId = HUMUtilityHelper.validateAgentId('57710');
        System.assertEquals(validAgentId,true);
        try
        { 
            Boolean invalidAgentId = HUMUtilityHelper.validateAgentId('577%a');
        }
        Catch(Exception e)
        {
            System.assertEquals(HUMConstants.HumSearchAgencyNameValidation,e.getMessage());
        }
    }
    
    /**
    * Returns void
    * <p>
    * This method is used for checking if correct search
                         parameters are filled in for agency search. 
    *
    * @return  Void
    * @see
    */
    static testMethod void testvalidateAgencySearch() 
    {
        Boolean validAgencySearch = HUMUtilityHelper.validateAgencySearch('Test','1234','23432','Test','Alabama');
        System.assertEquals(validAgencySearch,true);
        try
        { 
            Boolean invalidAgencySearch = HUMUtilityHelper.validateAgencySearch('','','','','None');
        }
        Catch(Exception e)
        {
            System.assertEquals(HUMConstants.HUM_Agency_Search_Fields_Not_Entered,e.getMessage());
        }
        try
        { 
            Boolean invalidAgencySearch = HUMUtilityHelper.validateAgencySearch('Test','','23432','Test','None');
        }
        Catch(Exception e)
        {
            System.assertEquals(HUMConstants.HUMAgentSearchInvalidCombination,e.getMessage());
        }
    }
    
    /**
    * Returns void
    * <p>
    * This method is used for checking if correct search
                         parameters are filled in for Group Search.
    *
    * @return  Void
    * @see
    */
    static testMethod void testvalidateGroupSearch() 
    {
        HUMGroupSearchWrapperHelper validGroupWrapper = new HUMGroupSearchWrapperHelper();
        validGroupWrapper.sGroupNumber = '12345';
        validGroupWrapper.sGroupName = 'test';
        validGroupWrapper.sState = 'Alabama';
        
        Boolean validGroupSearch = HUMUtilityHelper.validateGroupSearch(validGroupWrapper);
        System.assertEquals(validGroupSearch,true);
        
        try
        { 
            HUMGroupSearchWrapperHelper invalidGroupWrapper = new HUMGroupSearchWrapperHelper();
            invalidGroupWrapper.sGroupNumber = '';
            invalidGroupWrapper.sGroupName = '';
            invalidGroupWrapper.sState = 'None';
            Boolean invalidGroupSearch = HUMUtilityHelper.validateGroupSearch(invalidGroupWrapper);
        }
        Catch(Exception e)
        {
            System.assertEquals(HUMConstants.HUM_Agency_Search_Fields_Not_Entered,e.getMessage());
        }
        try
        { 
            HUMGroupSearchWrapperHelper invalidGroupWrapper = new HUMGroupSearchWrapperHelper();
            invalidGroupWrapper.sGroupNumber = '';
            invalidGroupWrapper.sGroupName = 'Test';
            invalidGroupWrapper.sState = 'None';
            Boolean invalidGroupSearch = HUMUtilityHelper.validateGroupSearch(invalidGroupWrapper);
        }
        Catch(Exception e)
        {
            System.assertEquals(HUMConstants.HUMGroupSearchInvalidCombination,e.getMessage());
        }
    }
    
    /**
    * Returns void
    * <p>
    * This method is used for validating Agency Name to allow alphanumeric,spaces and special characters
                         like ".", "-", "'"  in Agent/Broker search 
    *
    * @return  Void
    * @see
    */
    static testMethod void testvalidateAgencyName() 
    {
        Boolean validAgencyName = HUMUtilityHelper.validateAgencyName('Test');
        System.assertEquals(validAgencyName,false);
        try
        { 
            Boolean invalidAgencyName = HUMUtilityHelper.validateAgencyName('Test%a');
        }
        Catch(Exception e)
        {
            System.assertEquals(HUMConstants.HUMAlphaNumericPattern,e.getMessage());
        }
        
    }
    
    /**
    * Returns void
    * <p>
    * This method is used for checking if correct search
                         parameters are filled in for provider search.
    *
    * @return  Void
    * @see
    */
    static testMethod void testvalidateProviderSearch() 
    {
        HUMProviderSearchWrapperHelper validProviderWrapper = new HUMProviderSearchWrapperHelper();
        validProviderWrapper.sPostalCode = '57701';
        validProviderWrapper.sFacilityName = 'Test';
        validProviderWrapper.sFirstName = 'Test';
        validProviderWrapper.sLastName = 'Test';
        validProviderWrapper.sTaxID = '12343';
        validProviderWrapper.sState = 'Alabama';
        validProviderWrapper.sNPI = 'Test23';
        validProviderWrapper.sSpeciality = 'Test';
        
        Boolean validProviderSearch = HUMUtilityHelper.validateProviderSearch(validProviderWrapper);
        System.assertEquals(validProviderSearch,true);
        
        try
        { 
            HUMProviderSearchWrapperHelper invalidProviderWrapper = new HUMProviderSearchWrapperHelper();
            invalidProviderWrapper.sPostalCode = '';
            invalidProviderWrapper.sFacilityName = '';
            invalidProviderWrapper.sFirstName = '';
            invalidProviderWrapper.sLastName = '';
            invalidProviderWrapper.sTaxID = '';
            invalidProviderWrapper.sState = 'None';
            invalidProviderWrapper.sTaxID = '';
            invalidProviderWrapper.sNPI = '';
            invalidProviderWrapper.sSpeciality = 'None';
            
            Boolean invalidProviderSearch = HUMUtilityHelper.validateProviderSearch(invalidProviderWrapper);
        }
        Catch(Exception e)
        {
            System.assertEquals(HUMConstants.HUM_Provider_Search_Fields_Not_Entered,e.getMessage());
        }
       
    }
    
    /**
    * Returns void
    * <p>
    * This method is used for checking character length for Tax ID field if it is not blank.
    *
    * @return  Void
    * @see
    */
    static testMethod void testvalidateTaxIDLength() 
    {
        
        Test.startTest();
        Boolean validTaxId = HUMUtilityHelper.validateTaxIDLength('1234356789');
        System.assertEquals(validTaxId,true);
        try
        { 
            Boolean invalidTaxId = HUMUtilityHelper.validateTaxIDLength('23');
        }
        Catch(Exception e)
        {
            System.assertEquals(HUMConstants.HumProviderSearchTaxIDLength,e.getMessage());
        }
        Test.stopTest();
    }
    
    /**
    * Returns void
    * <p>
    * This method is used for checking character length for NPI field if it is not blank.
    *
    * @return  Void
    * @see
    */
    static testMethod void testvalidateNPILength() 
    {
       Test.startTest();
        Boolean validNPI = HUMUtilityHelper.validateNPILength('11234356789');
        System.assertEquals(validNPI,true);
        try
        { 
            Boolean invalidNPI = HUMUtilityHelper.validateNPILength('23');
        }
        Catch(Exception e)
        {
            System.assertEquals(HUMConstants.HumProviderSearchNPILength,e.getMessage());
        }
        Test.stopTest();
    }
    
    
     /*
     * Method Name : testvalidateTaxID
     * Description : This method is used for validating Tax ID is alphanumeric or not.
     * Return type : void
     * Paramater   : void
     */
    static testMethod void testvalidateTaxID() 
    {
        Boolean validNPI = HUMUtilityHelper.validateTaxID('11234356789');
        System.assertEquals(validNPI,true);
        try
        { 
            Boolean invalidNPI = HUMUtilityHelper.validateTaxID('23$%');
        }
        Catch(Exception e)
        {
            System.assertEquals(HUMConstants.HumSearchTaxIDAlphaNumericValidation,e.getMessage());
        }
    }
    
    
    
      /*
     * Method Name : testvalidateNPI
     * Description :  This method is used for validating NPI is alphanumeric or not. 
     * Return type : void
     * Paramater   : void
     */
    static testMethod void testvalidateNPI() 
    {
        Boolean validNPI = HUMUtilityHelper.validateNPI('1123456789');
        System.assertEquals(validNPI,true);
        try
        { 
            Boolean invalidNPI = HUMUtilityHelper.validateNPI('23$%');
        }
        Catch(Exception e)
        {
            System.assertEquals(HUMConstants.HumSearchNPIAlphaNumericValidation,e.getMessage());
        }
    }
    
    
       /*
     * Method Name : testvalidateFacilityName
     * Description :  This method is used for validating First Name is blank or not.
     * Return type : void
     * Paramater   : void
     */
    static testMethod void testvalidateFacilityName() 
    {
        Boolean validNPI = HUMUtilityHelper.validateFacilityName('Test');
        System.assertEquals(validNPI,true);
        try
        { 
            Boolean invalidNPI = HUMUtilityHelper.validateFacilityName('23$%');
        }
        Catch(Exception e)
        {
            System.assertEquals(HUMConstants.HumSearchFacilityNameAlphaNumericValidation,e.getMessage());
        }
    }
    
      /*
     * Method Name : testvalidateCBISFirstName
     * Description : This method is used for validating First Name is blank or not
     * Return type : void
     * Paramater   : void
     */
    static testMethod void testvalidateCBISFirstName() 
    {
        Boolean validFirstName = HUMUtilityHelper.validateCBISFirstName('Test');
        System.assertEquals(validFirstName,true);
        try
        { 
            Boolean invalidFirstName = HUMUtilityHelper.validateCBISFirstName('');
        }
        Catch(Exception e)
        {
            System.assertEquals(HUMConstants.HumSearchFacilityNameAlphaNumericValidation,e.getMessage());
        }
    }
    
      /*
     * Method Name : testvalidateCBISLastName
     * Description : This method is used for validating Last Name is blank or not
     * Return type : void
     * Paramater   : void
     */
    static testMethod void testvalidateCBISLastName() 
    {
        Boolean validLastName = HUMUtilityHelper.validateCBISLastName('Test');
        System.assertEquals(validLastName,true);
        try
        { 
            Boolean invalidLastName = HUMUtilityHelper.validateCBISLastName('');
        }
        Catch(Exception e)
        {
            System.assertEquals(HUMConstants.HUMSearchEnrollmentLastName,e.getMessage());
        }
    }
    
    /*
     * Method Name : testvalidateCBISBirthDay
     * Description : This method is used for validating Birthdate is blank or not. 
     * Return type : void
     * Paramater   : void
     */
    static testMethod void testvalidateCBISBirthDay() 
    {
        Boolean validBirthDay = HUMUtilityHelper.validateCBISBirthDay('02/02/2010');
        System.assertEquals(validBirthDay,true);
        try
        { 
            Boolean invalidBirthDay = HUMUtilityHelper.validateCBISBirthDay('');
        }
        Catch(Exception e)
        {
            System.assertEquals(HUMConstants.HUMSearchEnrollmentBirthDate,e.getMessage());
        }
    }
    
    
     /*
     * Method Name : testvalidateCBISState
     * Description : This method is used for validating State is blank or not. 
     * Return type : void
     * Paramater   : void
     */
    static testMethod void testvalidateCBISState() 
    {
        Boolean validState = HUMUtilityHelper.validateCBISState('Alabama');
        System.assertEquals(validState,true);
        try
        { 
            Boolean invalidState = HUMUtilityHelper.validateCBISState('None');
        }
        Catch(Exception e)
        {
            System.assertEquals(HUMConstants.HUMSearchEnrollmentState,e.getMessage());
        }
    }
      /*
     * Method Name : testvalidateCBISSSN
     * Description : This method is used for validating State is blank or not. 
     * Return type : void
     * Paramater   : void
     */
    static testMethod void testvalidateCBISSSN() 
    {
        Boolean validCBIS = HUMUtilityHelper.validateCBISSSN('Test','Test','02/02/2010','Alabama',null);  
        System.assertEquals(validCBIS,true);
        try
        { 
            Boolean invalidCBIS = HUMUtilityHelper.validateCBISSSN('None','None','None','None','None');
        }
        Catch(Exception e)
        {
            System.assertEquals(HUMConstants.HUMSearchEnrollmentState,e.getMessage());
        }
    }
    
    /*
     * Method Name : validateSuffixSearch
     * Description : This method is used to validate member id and suffix combination. 
     * Return type : void
     * Paramater   : void
     */
    static testMethod void testvalidateSuffixSearch() 
    {
        HUMMemberSearchWrapperHelper memberWrapper = new HUMMemberSearchWrapperHelper();
        memberWrapper.sSuffix = '123';
        memberWrapper.sMemberId = '123';
        Boolean validSuffixSearch = HUMUtilityHelper.validateSuffixSearch(memberWrapper);
        System.assertEquals(validSuffixSearch,true);
        try
        { 
            HUMMemberSearchWrapperHelper memberWrapperInvalid = new HUMMemberSearchWrapperHelper();
            memberWrapperInvalid.sSuffix = '123';
            memberWrapperInvalid.sMemberId = '';
            Boolean invalidateSuffixSearch = HUMUtilityHelper.validateSuffixSearch(memberWrapperInvalid);
        }
        Catch(Exception e)
        {
            System.assertEquals(HUMConstants.HumSearchSuffixMemberIdValidation,e.getMessage());
        }
    }
    
    /*
     * Method Name : testerrorStateSearchCombination
     * Description : This method is used to validate combinational search
     * Return type : void
     * Paramater   : void
     */
    static testMethod void testerrorStateSearchCombination() 
    {
        HUMProviderSearchWrapperHelper validproviderWrapper = new HUMProviderSearchWrapperHelper();
        validproviderWrapper.sState = '57710';
        validproviderWrapper.sFacilityName = 'testfacility';
        validproviderWrapper.sFirstname = 'Test';
        validproviderWrapper.sLastName = 'Test';
        
        
        Boolean validErrorState = HUMUtilityHelper.errorStateSearchCombination(validproviderWrapper);
        System.assertEquals(validErrorState,true);
        
        try
        { 
            HUMProviderSearchWrapperHelper invalidproviderWrapper = new HUMProviderSearchWrapperHelper();
            invalidproviderWrapper.sState = '57710';
            invalidproviderWrapper.sFacilityName = '';
            invalidproviderWrapper.sFirstname = '';
            invalidproviderWrapper.sLastName = '';
            
            Boolean invalidateSuffixSearch = HUMUtilityHelper.errorStateSearchCombination(invalidproviderWrapper);
        }
        Catch(Exception e)
        {
            System.assertEquals(HUMConstants.Hum_State_Name,e.getMessage());
        }
    }
    
     /*
     * Method Name : testerrorBirthDateSearch
     * Description : This method is used to validate combinational search
     * Return type : void
     * Paramater   : void
     */
    static testMethod void testerrorBirthDateSearch() 
    {
        HUMMemberSearchWrapperHelper validErrorBirthDate = new HUMMemberSearchWrapperHelper();
        validErrorBirthDate.sBirthdate = '02/02/2010';
        validErrorBirthDate.sLastName = 'Test';
        validErrorBirthDate.sFirstname = 'Test';
        validErrorBirthDate.sMemberId = '123456';
        validErrorBirthDate.sGroupNumber = '123456';
        validErrorBirthDate.sPhone = '123456';
        
        Boolean validBirthDate = HUMUtilityHelper.errorBirthDateSearch(validErrorBirthDate);
        System.assertEquals(validBirthDate,true);
        
        try
        { 
            HUMMemberSearchWrapperHelper invalidErrorBirthDate = new HUMMemberSearchWrapperHelper();
            invalidErrorBirthDate.sBirthdate = '02/02/2010';
            invalidErrorBirthDate.sLastName = '';
            invalidErrorBirthDate.sFirstname = '';
            invalidErrorBirthDate.sMemberId = '';
            invalidErrorBirthDate.sGroupNumber = '';
            invalidErrorBirthDate.sPhone = '';
            
            Boolean invalidateErrorBirthDate1 = HUMUtilityHelper.errorBirthDateSearch(invalidErrorBirthDate);
        }
        Catch(Exception e)
        {
            System.assertEquals(HUMConstants.HumErrorBirthDateSearch,e.getMessage());
        }
        
         try
        { 
            HUMMemberSearchWrapperHelper invalidErrorBirthDate2 = new HUMMemberSearchWrapperHelper();
            invalidErrorBirthDate2.sBirthdate = '02/02/2010';
            invalidErrorBirthDate2.sLastName = '';
            invalidErrorBirthDate2.sFirstname = '';
            invalidErrorBirthDate2.sMemberId = '123434';
            invalidErrorBirthDate2.sGroupNumber = '23423423';
            invalidErrorBirthDate2.sPhone = '(123) 456-789';
            
            Boolean invalidateErrorBirthDate2 = HUMUtilityHelper.errorBirthDateSearch(invalidErrorBirthDate2);
        }
        Catch(Exception e)
        {
            System.assertNotEquals(HUMConstants.HumErrorBirthDateSearchCombination,e.getMessage());
        }
    }
    
    
    /*
     * Method Name : testerrorZipCodeSearch
     * Description : This method is used to validate combinational search
     * Return type : void
     * Paramater   : void
     */
    static testMethod void testerrorZipCodeSearch() 
    {
        HUMMemberSearchWrapperHelper validZipCode = new HUMMemberSearchWrapperHelper();
        validZipCode.sBirthdate = '02/02/2010';
        validZipCode.sLastName = 'Test';
        validZipCode.sFirstname = 'Test';
        validZipCode.sMemberId = '123456';
        validZipCode.sGroupNumber = '123456';
        validZipCode.sPhone = '123456';
        validZipCode.sPostalCode = '12355';
        
        Boolean validZip = HUMUtilityHelper.errorZipCodeSearch(validZipCode);
        System.assertEquals(validZip,true);
        
        try
        { 
            HUMMemberSearchWrapperHelper invalidZipCode1 = new HUMMemberSearchWrapperHelper();
            invalidZipCode1.sBirthdate = '02/02/2010';
            invalidZipCode1.sLastName = '';
            invalidZipCode1.sFirstname = '';
            invalidZipCode1.sMemberId = '';
            invalidZipCode1.sGroupNumber = '';
            invalidZipCode1.sPhone = '';
            invalidZipCode1.sPostalCode = '12355';
            
            Boolean invalidZip1 = HUMUtilityHelper.errorZipCodeSearch(invalidZipCode1);
        }
        Catch(Exception e)
        {
            System.assertEquals(HUMConstants.HumErrorZipCodeSearch,e.getMessage());
        }
        
         try
        { 
            HUMMemberSearchWrapperHelper invalidZipCode2 = new HUMMemberSearchWrapperHelper();
            invalidZipCode2.sBirthdate = '02/02/2010';
            invalidZipCode2.sLastName = '';
            invalidZipCode2.sFirstname = '';
            invalidZipCode2.sMemberId = '123434';
            invalidZipCode2.sGroupNumber = '23423423';
            invalidZipCode2.sPhone = '(123) 456-789';
            invalidZipCode2.sPostalCode = '12355';
            
            Boolean invalidZip2 = HUMUtilityHelper.errorZipCodeSearch(invalidZipCode2);
        }
        Catch(Exception e)
        {
            System.assertEquals(HUMConstants.HumErrorPostalCodeSearchCombination,e.getMessage());
        }
    }
    
     /*
     * Method Name : testerrorZipcodeSearchCombination
     * Description : Encrypt field values for  Provider search purpose
     * Return type : void
     * Paramater   : void
     */
    static testMethod void testerrorZipcodeSearchCombination() 
    {
       
        HUMProviderSearchWrapperHelper providerWrapper = new HUMProviderSearchWrapperHelper();
        providerWrapper.sFirstName = 'Test';
        providerWrapper.sLastName = '123456';
        providerWrapper.sNPI = '57710';
        providerWrapper.sPostalCode = '1123456789';
        providerWrapper.sFacilityName = '123456';
        providerWrapper.sTaxID = '57710';
        providerWrapper.sPostalCode = '123434';
        
        Boolean validZipCode = HUMUtilityHelper.errorZipcodeSearchCombination(providerWrapper);
        System.assertEquals(validZipCode,true);
        
        try
        {
              HUMProviderSearchWrapperHelper providerWrapperinvalid = new HUMProviderSearchWrapperHelper();
                providerWrapperinvalid.sFirstName = '';
                providerWrapperinvalid.sLastName = '';
                providerWrapperinvalid.sNPI = '57710';
                providerWrapperinvalid.sPostalCode = '1123456789';
                providerWrapperinvalid.sFacilityName = '';
                providerWrapperinvalid.sTaxID = '57710';
                providerWrapperinvalid.sPostalCode = '123434';
                
                Boolean validZipCode1 = HUMUtilityHelper.errorZipcodeSearchCombination(providerWrapperinvalid);
        }
        catch(Exception e)
        {
                
            System.assertEquals(HUMConstants.Hum_Zipcode_Name,e.getMessage());
        }
    }
    
    
     /*
     * Method Name : testvalidationRule
     * Description : This Method is used to validate Membersearch input fields 
     * Return type : void
     * Paramater   : void
     */
    static testMethod void testtestvalidationRule() 
    {
        HUMMemberSearchWrapperHelper memberWrapper = new HUMMemberSearchWrapperHelper();
        memberWrapper.sFirstName = 'Test';
        memberWrapper.sLastName = 'TEst';
        memberWrapper.sBirthdate = '04/23/2000';
        //memberWrapper.sPhone = '(123)456-789';
        memberWrapper.sPostalCode = '57720';
        memberWrapper.sGroupNumber = '1234445';
        memberWrapper.sMemberId = '123243';
        memberWrapper.sSuffix = '123';
        Boolean bclicksource = True;
        Boolean validRule = HUMUtilityHelper.validationRule(memberWrapper);
        HUMUtilityHelper.validateInputDataCombinationsMedicare(memberWrapper,bclicksource);
        HUMUtilityHelper.errorBirthDateSearchMedicare(memberWrapper);
        HUMUtilityHelper.errorZipCodeSearchMedicare(memberWrapper);
        HUMUtilityHelper.errorStateSearchMedicare(memberWrapper);
        HUMUtilityHelper.errorFirstLastNameSearchMedicare(memberWrapper);
        System.assertEquals(validRule,true);
    }
    
    
    
    
    /*
     * Method Name : testencryptMemberFields
     * Description :  Encrypt field values for  Member search purpose
     * Return type : void
     * Paramater   : void
     */
    static testMethod void testencryptMemberFields() 
    {
        HUMMemberSearchWrapperHelper memberWrapper = new HUMMemberSearchWrapperHelper();
        memberWrapper.sFirstName = 'Test';
        memberWrapper.sLastName = 'TEst';
        memberWrapper.sBirthdate = '04/23/2000';
        memberWrapper.sPhone = '1123456789';
        memberWrapper.sPostalCode = '57720';
        memberWrapper.sGroupNumber = '1234445';
        memberWrapper.sMemberId = '123243';
    }
    
    
    /*
     * Method Name : testencryptGroupFields
     * Description : Encrypt field values for  Group search purpose
     * Return type : void
     * Paramater   : void
     */
    static testMethod void testencryptGroupFields() 
    {
        HUMGroupSearchWrapperHelper groupWrapper = new HUMGroupSearchWrapperHelper();
        groupWrapper.sGroupName = 'Test';
        groupWrapper.sGroupNumber = '123456';
        groupWrapper.sPostalCode = '57710';
        groupWrapper.sPhone = '1123456789';
    }
    
    
    /*
     * Method Name : testencryptProviderFields
     * Description : Encrypt field values for  Provider search purpose
     * Return type : void
     * Paramater   : void
     */
    static testMethod void testencryptProviderFields() 
    {
        HUMProviderSearchWrapperHelper providerWrapper = new HUMProviderSearchWrapperHelper();
        providerWrapper.sFirstName = 'Test';
        providerWrapper.sLastName = '123456';
        providerWrapper.sNPI = '57710';
        providerWrapper.sPostalCode = '1123456789';
        providerWrapper.sFacilityName = '123456';
        providerWrapper.sTaxID = '57710';
    }
    
    
    /*
     * Method Name : testencryptAgencyFields
     * Description : Encrypt field values for  Provider search purpose
     * Return type : void
     * Paramater   : void
     */
    static testMethod void testencryptAgencyFields() 
    {
        HUMAgencySearchWrapperHelper agencyWrapper = new HUMAgencySearchWrapperHelper();
        agencyWrapper.sFirstName = 'Test';
        agencyWrapper.sLastName = '123456';
        agencyWrapper.sPostalCode = '1123456789';
        agencyWrapper.sFacilityName = '123456';
        agencyWrapper.sagencyName = 'test';
        agencyWrapper.sTaxID = '57710';
    }
    
    
    /*
     * Method Name : testvalidateInputDataCombinations
     * Description :  This method is used to validate combinational search in Member screen
     * Return type : void
     * Paramater   : void
     */
    static testMethod void tesvalidateInputDataCombinations() 
    {
        HUMMemberSearchWrapperHelper validInput = new HUMMemberSearchWrapperHelper();
        validInput.sBirthdate = '02/02/2010';
        validInput.sLastName = 'Test';
        validInput.sFirstname = 'Test';
        validInput.sMemberId = '123456';
        validInput.sGroupNumber = '123456';
        validInput.sPhone = '123456';
        validInput.sPostalCode = '12355';
        validInput.sSuffix = '123';
        
        Boolean validInputCombination = HUMUtilityHelper.validateInputDataCombinations(validInput);
        System.assertEquals(validInputCombination,true);
        
            HUMMemberSearchWrapperHelper invalidInput = new HUMMemberSearchWrapperHelper();
            invalidInput.sBirthdate = '';
            invalidInput.sLastName = '';
            invalidInput.sFirstname = '';
            invalidInput.sMemberId = '';
            invalidInput.sGroupNumber = '';
            invalidInput.sPhone = '';
            invalidInput.sPostalCode = '';
            invalidInput.sSuffix = '';
            
            Boolean invalidInputCombination = HUMUtilityHelper.errorZipCodeSearch(invalidInput);
             System.assertEquals(invalidInputCombination,true);
     }
     
     
      
    /*
     * Method Name : testerrorStateSearch
     * Description : This method is used to validate if state field is filled or not
     * Return type : void
     * Paramater   : void
     */
    static testMethod void testerrorStateSearch() 
    {
        HUMMemberSearchWrapperHelper validState = new HUMMemberSearchWrapperHelper();
        validState.sBirthdate = '02/02/2010';
        validState.sLastName = 'Test';
        validState.sFirstname = 'Test';
        validState.sMemberId = '123456';
        validState.sGroupNumber = '123456';
        validState.sPhone = '123456';
        validState.sPostalCode = '12355';
        validState.sState = 'Alabama';
        
        Boolean validStateSearch = HUMUtilityHelper.errorZipCodeSearch(validState);
  
        
        try
        { 
            HUMMemberSearchWrapperHelper invalidState1 = new HUMMemberSearchWrapperHelper();
            invalidState1.sBirthdate = '';
            invalidState1.sLastName = '';
            invalidState1.sFirstname = '';
            invalidState1.sMemberId = '';
            invalidState1.sGroupNumber = '';
            invalidState1.sPhone = '';
            invalidState1.sPostalCode = '';
            invalidState1.sState = 'Alabama';
            
            Boolean invalidStateSearch1 = HUMUtilityHelper.errorStateSearch(invalidState1);
        }
        Catch(Exception e)
        {
            System.assertEquals(HUMConstants.HumErrorStateSearch,e.getMessage());
        }
        try
        { 
            HUMMemberSearchWrapperHelper invalidState2 = new HUMMemberSearchWrapperHelper();
            invalidState2.sBirthdate = '';
            invalidState2.sLastName = '';
            invalidState2.sFirstname = '';
            invalidState2.sMemberId = '123456';
            invalidState2.sGroupNumber = '';
            invalidState2.sPhone = '';
            invalidState2.sPostalCode = '';
            invalidState2.sState = 'Alabama';
            
            Boolean invalidStateSearch2 = HUMUtilityHelper.errorStateSearch(invalidState2);
        }
        Catch(Exception e)
        {
            System.assertNotEquals(HUMConstants.HumErrorStateSearchCombination,e.getMessage());
        }   
        
        HUMMemberSearchWrapperHelper invalidState3 = new HUMMemberSearchWrapperHelper();
        invalidState3.sBirthdate = '';
        invalidState3.sLastName = '';
        invalidState3.sFirstname = '';
        invalidState3.sMemberId = '123456';
        invalidState3.sGroupNumber = '';
        invalidState3.sPhone = '';
        invalidState3.sPostalCode = '';
        invalidState3.sState = 'None';
        
        Boolean invalidStateSearch3 = HUMUtilityHelper.errorStateSearch(invalidState3);
        System.assertEquals(invalidStateSearch3,true);
    }
    
      /*
     * Method Name : testerrorFirstLastNameSearch
     * Description : This method is used to This method is used to validate if first/last field is filled or not if state field is filled or not
     * Return type : void
     * Paramater   : void
     */
    static testMethod void testerrorFirstLastNameSearch() 
    {
        HUMMemberSearchWrapperHelper validFirstLastName = new HUMMemberSearchWrapperHelper();
        validFirstLastName.sBirthdate = '02/02/2010';
        validFirstLastName.sLastName = 'Test';
        validFirstLastName.sFirstname = 'Test';
        validFirstLastName.sMemberId = '123456';
        validFirstLastName.sGroupNumber = '123456';
        validFirstLastName.sPhone = '123456';
        validFirstLastName.sPostalCode = '12355';
        validFirstLastName.sState = 'Alabama';
        
        Boolean validFNLN = HUMUtilityHelper.errorFirstLastNameSearch(validFirstLastName);
        System.assertEquals(validFNLN,true);
        
        try
        { 
            HUMMemberSearchWrapperHelper invalidFirstLastName = new HUMMemberSearchWrapperHelper();
            invalidFirstLastName.sBirthdate = '';
            invalidFirstLastName.sLastName = '';
            invalidFirstLastName.sFirstname = 'Test';
            invalidFirstLastName.sMemberId = '';
            invalidFirstLastName.sGroupNumber = '';
            invalidFirstLastName.sPhone = '';
            invalidFirstLastName.sPostalCode = '';
            invalidFirstLastName.sState = 'None';
            
            Boolean invalidFNLN = HUMUtilityHelper.errorFirstLastNameSearch(invalidFirstLastName);
        }
        Catch(Exception e)
        {
            System.assertEquals(HUMConstants.HumErrorFirstNameSearch,e.getMessage());
        }
        try
        { 
            HUMMemberSearchWrapperHelper invalidFirstLastName = new HUMMemberSearchWrapperHelper();
            invalidFirstLastName.sBirthdate = '';
            invalidFirstLastName.sLastName = 'Test';
            invalidFirstLastName.sFirstname = '';
            invalidFirstLastName.sMemberId = '';
            invalidFirstLastName.sGroupNumber = '';
            invalidFirstLastName.sPhone = '';
            invalidFirstLastName.sPostalCode = '';
            invalidFirstLastName.sState = 'None';
            
            Boolean invalidFNLN = HUMUtilityHelper.errorFirstLastNameSearch(invalidFirstLastName);
        }
        Catch(Exception e)
        {
            System.assertEquals(HUMConstants.HumErrorLastNameSearch,e.getMessage());
        }   
    }
    
    
    /*
     * Method Name : testerrorSpecialitySearchCombination
     * Description : This method is used to validate combinational search
     * Return type : void
     * Paramater   : void
     */
    static testMethod void testerrorSpecialitySearchCombination() 
    {
       
        HUMProviderSearchWrapperHelper providerWrapper = new HUMProviderSearchWrapperHelper();
        providerWrapper.sFirstName = 'Test';
        providerWrapper.sLastName = 'Teste';
        providerWrapper.sNPI = '57710';
        providerWrapper.sPostalCode = '1123456789';
        providerWrapper.sFacilityName = '123456';
        providerWrapper.sTaxID = '57710';
        providerWrapper.sPostalCode = '123434';
        providerWrapper.sSpeciality = 'Test';
        
        Boolean validSpecialityCombination = HUMUtilityHelper.errorSpecialitySearchCombination(providerWrapper);
        System.assertEquals(validSpecialityCombination,true);
        
        try
        {
              HUMProviderSearchWrapperHelper providerWrapperinvalid = new HUMProviderSearchWrapperHelper();
                providerWrapperinvalid.sFirstName = '';
                providerWrapperinvalid.sLastName = '';
                providerWrapperinvalid.sNPI = '57710';
                providerWrapperinvalid.sPostalCode = '1123456789';
                providerWrapperinvalid.sFacilityName = '';
                providerWrapperinvalid.sTaxID = '57710';
                providerWrapperinvalid.sPostalCode = '123434';
                providerWrapperinvalid.sSpeciality = 'Test';
                
                Boolean invalidSpecialityCombination = HUMUtilityHelper.errorSpecialitySearchCombination(providerWrapperinvalid);
            	HUMUtilityHelper.checkUniqueProviderSearch(providerWrapperinvalid);
        }
        catch(Exception e)
        {
                
            System.assertEquals(HUMConstants.Hum_Specialty_Name,e.getMessage());
        }
    }
    
    
    /*
     * Method Name : testerrorLastNameSearchCombination
     * Description : This method is used to validate combinational search
     * Return type : void
     * Paramater   : void
     */
    static testMethod void testerrorLastNameSearchCombination() 
    {
       
        HUMProviderSearchWrapperHelper providerWrapper = new HUMProviderSearchWrapperHelper();
        providerWrapper.sFirstName = 'Test';
        providerWrapper.sLastName = 'Teste';
        providerWrapper.sNPI = '57710';
        providerWrapper.sPostalCode = '1123456789';
        providerWrapper.sFacilityName = '123456';
        providerWrapper.sTaxID = '57710';
        providerWrapper.sPostalCode = '123434';
        providerWrapper.sSpeciality = 'Test';
        
        Boolean validLastNameCombination = HUMUtilityHelper.errorLastNameSearchCombination(providerWrapper);
        System.assertEquals(validLastNameCombination,true);
        
        try
        {
              HUMProviderSearchWrapperHelper providerWrapperinvalid = new HUMProviderSearchWrapperHelper();
                providerWrapperinvalid.sFirstName = '';
                providerWrapperinvalid.sLastName = 'Test';
                providerWrapperinvalid.sNPI = '57710';
                providerWrapperinvalid.sPostalCode = '1123456789';
                providerWrapperinvalid.sFacilityName = '';
                providerWrapperinvalid.sTaxID = '57710';
                providerWrapperinvalid.sPostalCode = '123434';
                providerWrapperinvalid.sSpeciality = 'Test';
                
                Boolean invalidSpecialityCombination = HUMUtilityHelper.errorLastNameSearchCombination(providerWrapperinvalid);
        }
        catch(Exception e)
        {
                
            System.assertEquals(HUMConstants.Hum_Last_Name,e.getMessage());
        }
    }
    
    
     /*
     * Method Name : testerrorFirstNameSearchCombination
     * Description : This method is used to validate combinational search
     * Return type : void
     * Paramater   : void
     */
    static testMethod void testerrorFirstNameSearchCombination() 
    {
       
        HUMProviderSearchWrapperHelper providerWrapper = new HUMProviderSearchWrapperHelper();
        providerWrapper.sFirstName = 'Test';
        providerWrapper.sLastName = 'Teste';
        providerWrapper.sNPI = '57710';
        providerWrapper.sPostalCode = '1123456789';
        providerWrapper.sFacilityName = '123456';
        providerWrapper.sTaxID = '57710';
        providerWrapper.sPostalCode = '123434';
        providerWrapper.sSpeciality = 'Test';
        
        Boolean validLastNameCombination = HUMUtilityHelper.errorFirstNameSearchCombination(providerWrapper);
        System.assertEquals(validLastNameCombination,true);
        
        try
        {
              HUMProviderSearchWrapperHelper providerWrapperinvalid = new HUMProviderSearchWrapperHelper();
                providerWrapperinvalid.sFirstName = 'Test';
                providerWrapperinvalid.sLastName = '';
                providerWrapperinvalid.sNPI = '57710';
                providerWrapperinvalid.sPostalCode = '1123456789';
                providerWrapperinvalid.sFacilityName = '';
                providerWrapperinvalid.sTaxID = '57710';
                providerWrapperinvalid.sPostalCode = '123434';
                providerWrapperinvalid.sSpeciality = 'Test';
                
                Boolean invalidSpecialityCombination = HUMUtilityHelper.errorFirstNameSearchCombination(providerWrapperinvalid);
        }
        catch(Exception e)
        {
                
            System.assertEquals(HUMConstants.Hum_First_Name,e.getMessage());
        }
    }
    
    
     /*
     * Method Name : testmanupulateAndpopulateEncryptedText
     * Description : This method is used to validate combinational search
     * Return type : void
     * Paramater   : void
     */
    static testMethod void testmanupulateAndpopulateEncryptedText() 
    {       
        HUMProviderSearchWrapperHelper providerWrapper = new HUMProviderSearchWrapperHelper();
        providerWrapper.sFirstName = 'Test';
        providerWrapper.sLastName = 'Teste';
        providerWrapper.sNPI = '57710';
        providerWrapper.sPostalCode = '1123456789';
        providerWrapper.sFacilityName = '123456';
        providerWrapper.sTaxID = '57710';
        providerWrapper.sPostalCode = '123434';
        providerWrapper.sSpeciality = 'Test';
        providerWrapper.sSuffix = '12356';
                
        HUMProviderSearchWrapperHelper providerWrapper1 = new HUMProviderSearchWrapperHelper();
        providerWrapper.sFirstName = 'Test';
        providerWrapper.sLastName = 'Teste';
        providerWrapper.sNPI = '57710';
        providerWrapper.sPostalCode = '1123456789';
        providerWrapper.sFacilityName = '123456';
        providerWrapper.sTaxID = '57710';
        providerWrapper.sPostalCode = '123434';
        providerWrapper.sSpeciality = 'Test';
        providerWrapper.sSuffix = '';
    }
    
        /*
     * Method Name : testmanupulateAndpopulateEncryptedText
     * Description : This method is used to validate combinational search
     * Return type : void
     * Paramater   : void
     */
    static testMethod void testmemberEncryptQueryConstructor() 
    {
        HUMMemberSearchWrapperHelper providerWrapper = new HUMMemberSearchWrapperHelper();
        providerWrapper.sFirstName = 'Test';
        providerWrapper.sLastName = 'Teste';
        providerWrapper.sNPI = '57710';
        providerWrapper.sPostalCode = '1123456789';
        providerWrapper.sFacilityName = '123456';
        providerWrapper.sTaxID = '57710';
        providerWrapper.sPostalCode = '123434';
        providerWrapper.sSpeciality = 'Test';
        providerWrapper.sSuffix = '12356';
              
        HUMMemberSearchWrapperHelper providerWrapper1 = new HUMMemberSearchWrapperHelper();
        providerWrapper.sFirstName = 'Test';
        providerWrapper.sLastName = 'Teste';
        providerWrapper.sNPI = '57710';
        providerWrapper.sPostalCode = '1123456789';
        providerWrapper.sFacilityName = '123456';
        providerWrapper.sTaxID = '57710';
        providerWrapper.sPostalCode = '123434';
        providerWrapper.sSpeciality = 'Test';
        providerWrapper.sSuffix = '';
    }
    
       /*
     * Method Name : testgroupEncryptQueryConstructor
     * Description : This method is used to validate combinational search
     * Return type : void
     * Paramater   : void
     */
    static testMethod void testgroupEncryptQueryConstructor() 
    {      
        HUMGroupSearchWrapperHelper providerWrapper = new HUMGroupSearchWrapperHelper();
        providerWrapper.sFirstName = 'Test';
        providerWrapper.sLastName = 'Teste';
        providerWrapper.sNPI = '57710';
        providerWrapper.sPostalCode = '1123456789';
        providerWrapper.sFacilityName = '123456';
        providerWrapper.sTaxID = '57710';
        providerWrapper.sPostalCode = '123434';
        providerWrapper.sSpeciality = 'Test';
        providerWrapper.sSuffix = '12356';
    }
    
    
    
      /*
     * Method Name : testagencyEncryptQueryConstructor
     * Description : This method is used to validate combinational search
     * Return type : void
     * Paramater   : void
     */
    static testMethod void testagencyEncryptQueryConstructor() 
    {       
        HUMAgencySearchWrapperHelper providerWrapper = new HUMAgencySearchWrapperHelper();
        providerWrapper.sFirstName = 'Test';
        providerWrapper.sLastName = 'Teste';
        providerWrapper.sNPI = '57710';
        providerWrapper.sPostalCode = '1123456789';
        providerWrapper.sFacilityName = '123456';
        providerWrapper.sTaxID = '57710';
        providerWrapper.sPostalCode = '123434';
        providerWrapper.sSpeciality = 'Test';
        providerWrapper.sSuffix = '12356';
    }
    
    /*
     * Method Name : testValidateClosedCaseRecordType
     * Description : This method is used to validate Closed Case
     * Return type : void
     * Paramater   : void
     */
    static testMethod void testValidateClosedCaseRecordType() {
        User oUser = [select id, name,lastname from user where lastname='test User1' limit 1];
        system.assertEquals(oUser.lastname =='test User1',true); 
        System.runas(oUser) 
        {
            Case caseObj = [Select RecordType.Name from Case where Interacting_With_Name__c = 'Test'];
            System.debug(caseObj.RecordType.Name);
            Test.StartTest();
            System.assertEquals(HUMUtilityHelper.validateClosedCaseRecordType(caseObj.RecordType.Name),false);
            caseObj.Status='Closed';
			caseObj.recordtypeID = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Closed Member Case').getRecordTypeId();
            update caseObj;
            caseObj = [Select RecordType.Name from Case where id =: caseObj.id];
            System.assertEquals(HUMUtilityHelper.validateClosedCaseRecordType(caseObj.RecordType.Name),true);
            Test.StopTest();
        }
    }
    
    /**
    * Returns void
    * <p>
    * test method for getTermAlert method of HUMUtilityHelper class  
    *
    * @return  Void
    * @see
    */
    static testMethod void testGetStateFullNameValue()
    {
        User oUser = [select id, name,lastname from user where lastname='test User1' limit 1];
        system.assertEquals(oUser.lastname =='test User1',true); 
        system.runAs(oUser)
        {
            List<SelectOption> lstSelectOptn;
            Test.startTest();
             try{
                lstSelectOptn = HUMUtilityHelper.getStateFullNameValue();
                System.assert(lstSelectOptn!=null);
                // Cover DOB format method
                String sResultString = HUMUtilityHelper.convertDateformat('2020-01-01');
                System.assert(sResultString!=null);
             }
             catch(exception e){
                 System.assertEquals(HUMConstants.HUMSearchMemberIdAlphaNumericValidation,e.getMessage());
             }
            Test.stopTest();
         }
    }
    /*
     * Method Name : testisHPClassificationIntent
     * Description : This method is used to validate if the C&I pair on Quick Start Screen is HP.
     * Return type : void
     * Paramater   : void
     */
    static testMethod void testisHPClassificationIntent ()
    {
        User oUser = [select id, name,lastname from user where lastname='test User1' limit 1];
        system.assertEquals(oUser.lastname =='test User1',true); 
        system.runAs(oUser)
        {         
            Boolean Hpclassificationintent;
            Test.startTest();
            
            Hpclassificationintent = HUMUtilityHelper.isHPClassificationIntent('Testcla','test intent');
                
           Test.stopTest();
         }
    }
        /*
     * Method Name : testgetHPCaseRecordType
     * Description : This method is used to validate case object with updated recordtype based on account recordtype value for HP cases
     * Return type : void
     * Paramater   : void
     */
     static testMethod void testgetHPCaseRecordType ()
    {
        User oUser = [select id, name,lastname from user where lastname='test User1' limit 1];
        system.assertEquals(oUser.lastname =='test User1',true); 
        system.runAs(oUser)
        {
            HUMAccountCaseRecordTypes__c HpCaserecordtype= new HUMAccountCaseRecordTypes__c();
         HpCaserecordtype.name='Test Provider';
          HpCaserecordtype.Account_RecordType__c='Test Provider';
            HpCaserecordtype.Case_RecordType__c='Provider case';
            HpCaserecordtype.Humana_Pharmacy__c=True;
            insert  HpCaserecordtype;
                        
            id hprecordtype;
            Test.startTest();
            
                hprecordtype = HUMUtilityHelper.getHPCaseRecordType('Provider');
                
           Test.stopTest();
         }
    }
    /*
     * Method Name : testHasUserpharmacy206Access
     * Description : This method is used to Pharamcy 206 permission set Assignment
     * Return type : void
     * Paramater   : void
     */
     static testMethod void testHasUserPharmacy206Access()
    {
        User oUser = [select id, name,lastname from user where lastname='test User1' limit 1];
        system.assertEquals(oUser.lastname =='test User1',true); 
        system.runAs(oUser)
        {
                        
            Boolean deduceosb;
            Test.startTest();
            
                deduceosb = HUMUtilityHelper.hasUserPharmacy206Access();
                
           Test.stopTest();
         }
    }
    /*
     * Method Name : testisCRMfunctionalityON
     * Description : This method is used to validate CRM Functionality ON switch
     * Return type : void
     * Paramater   : void
     */
     static testMethod void testisCRMfunctionalityON()
    {
        User oUser = [select id, name,lastname from user where lastname='test User1' limit 1];
        system.assertEquals(oUser.lastname =='test User1',true); 
        system.runAs(oUser)
        {
            CRMFunctionality_ONOFF_Switch__c OSBONOFF= new CRMFunctionality_ONOFF_Switch__c();
            OSBONOFF.IsON__c=true;
            OSBONOFF.Name='738134';
            OSBONOFF.Story_Title__c='PR00091318 - MF 9 - Medicare OSB Vision and Fitness';
            Insert OSBONOFF;
            Boolean deduceosb;
            Test.startTest();
            
                deduceosb = HUMUtilityHelper.isCRMFunctionalityON('738134');
                
           Test.stopTest();
         }
    }
     /*
     * Method Name : testisCRMfunctionalityOFF
     * Description : This method is used to validate CRM Functionality OFF switch
     * Return type : void
     * Paramater   : void
     */
    static testMethod void testisCRMfunctionalityOFF()
    {
        User oUser = [select id, name,lastname from user where lastname='test User1' limit 1];
        system.assertEquals(oUser.lastname =='test User1',true); 
        system.runAs(oUser)
        {
            CRMFunctionality_ONOFF_Switch__c OSBONOFF= new CRMFunctionality_ONOFF_Switch__c();
            OSBONOFF.IsON__c=false;
            OSBONOFF.Name='738134';
            OSBONOFF.Story_Title__c='PR00091318 - MF 9 - Medicare OSB Vision and Fitness';
            Insert OSBONOFF;
            Boolean deduceosb;
            Test.startTest();
            
                deduceosb = HUMUtilityHelper.isCRMFunctionalityON('738134');
                
           Test.stopTest();
         }
    }
     /*
     * Method Name : testdeduceOSB
     * Description : This method is used to validate On/Off functionality for OSB product
     * Return type : void
     * Paramater   : void
     */
    static testMethod void testdeduceOSB()
    {
        User oUser = [select id, name,lastname from user where lastname='test User1' limit 1];
        system.assertEquals(oUser.lastname =='test User1',true); 
        system.runAs(oUser)
        {
            Boolean deduceosb;
            Test.startTest();
            
                deduceOSB = HUMUtilityHelper.deduceOSB(true,'O');
            	deduceOSB= HUMUtilityHelper.deduceOSB(false,'O');
                
           Test.stopTest();
         }
    }
    /*
     * Method Name : testHasUserMulesoftAccess
     * Description : This method is used to Mulesoft permission set Assignment
     * Return type : void
     * Paramater   : void
     */
     static testMethod void testHasUserMulesoftAccess()
    {
        User oUser = [select id, name,lastname from user where lastname='test User1' limit 1];
        system.assertEquals(oUser.lastname =='test User1',true); 
        system.runAs(oUser)
        {
            Boolean hasMuleAccess;
            Test.startTest();
            hasMuleAccess = HUMUtilityHelper.hasUserMulesoftAccess();
           Test.stopTest();
         }
    }
}