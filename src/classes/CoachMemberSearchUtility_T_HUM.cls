/*******************************************************************************************************************************
Apex Class Name : CoachMemberSearchUtility_T_HUM
Version         : 1.0
Created On      : 03/08/2021
Function        : This is the Test class for CoachMemberSearchUtility_H_HUM


Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Jasmeen Shangari                                    03/08/2021                Original Version
*******************************************************************************************************************************************************************/

@isTest
private class CoachMemberSearchUtility_T_HUM {
    @testSetup static void setup() 
    {    
        User oUser = HUMTestDataHelperTest.createTestUser('CoachUser','Customer Care Specialist');
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
            
            PermissionSet permissionSet = new PermissionSet(Label = 'mockPs', Name = 'mockPs');
            insert permissionSet;
            
            PermissionSetAssignment permissionSetAssignment = new PermissionSetAssignment(
                AssigneeId = oUser.Id,
                PermissionSetId = permissionSet.Id);
            insert permissionSetAssignment;
            
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
        User oUser = [select id, name,lastname from user where lastname='test CoachUser' limit 1];
        system.assertEquals(oUser.lastname =='test CoachUser',true); 
        system.runAs(oUser)
        {
            Boolean deduceosb;
            Test.startTest();
            
            deduceOSB = CoachMemberSearchUtility_H_HUM.deduceOSB(true,'O');
            deduceOSB= CoachMemberSearchUtility_H_HUM.deduceOSB(false,'O');
            
            Test.stopTest();
        }
    }
    
    /*
    * Method Name : testIsUserHavingMedicareCustomerServiceAccess
    * Description : 
    * Return type : void
    * Paramater   : void
    */
    static testMethod void testIsUserHavingMedicareCustomerServiceAccess() 
    {   
        Test.startTest();
        CoachMemberSearchUtility_H_HUM.isUserHavingMedicareCustomerServiceAccess();
        Test.stopTest();
    }
    
    /*
    * Method Name : testisUserhavingPermissionSetAccess
    * Description : 
    * Return type : void
    * Paramater   : void
    */
    static testMethod void testisUserhavingPermissionSetAccess() 
    {   
        Test.startTest();
        CoachMemberSearchUtility_H_HUM.isUserhavingPermissionSetAccess('mockPs');
        Test.stopTest();
    }
    
    /*
    * Method Name : testvalidateInputDataCombinationsMedicare
    * Description :  
    * Return type : void
    * Paramater   : void
    */
    static testMethod void testvalidateInputDataCombinationsMedicare() 
    {
        MemberSearchWrapper_CH_HUM validInput = new MemberSearchWrapper_CH_HUM();
        validInput.sBirthdate = '02/02/2010';
        validInput.sLastName = 'Test';
        validInput.sFirstname = '';
        validInput.sMemberId = '123456';
        validInput.sGroupNumber = '123456';
        validInput.sPhone = '123456';
        validInput.sPostalCode = '12355';
        validInput.sSuffix = '123';
        MemberSearchWrapper_CH_HUM validInputTwo = new MemberSearchWrapper_CH_HUM();
        validInputTwo.sBirthdate = '02/02/2010';
        validInputTwo.sLastName = '';
        validInputTwo.sFirstname = '';
        validInputTwo.sMemberId = '';
        validInputTwo.sGroupNumber = '';
        validInputTwo.sPhone = '';
        validInputTwo.sPostalCode = '';
        validInputTwo.sSuffix = '';
        Test.startTest();
        CoachMemberSearchUtility_H_HUM.validateInputDataCombinationsMedicare(validInput,false);
        try{
            CoachMemberSearchUtility_H_HUM.validateInputDataCombinationsMedicare(validInputTwo,false);
        }catch(Exception e){
            system.assert((e.getMessage()).contains(HUMConstants.HumErrorBirthDateSearchMedicare));
        }
        Test.stopTest();
    }
    
    /*
    * Method Name : testMemberSearchFieldsEnteredMedicare
    * Description : 
    * Return type : void
    * Paramater   : void
    */
    static testMethod void testMemberSearchFieldsEnteredMedicare() 
    {
        MemberSearchWrapper_CH_HUM validInput = new MemberSearchWrapper_CH_HUM();
        validInput.sBirthdate = '';
        validInput.sLastName = '';
        validInput.sFirstname = '';
        validInput.sMemberId = '';
        validInput.sGroupNumber = '';
        validInput.sPhone = '';
        validInput.sPostalCode = '';
        validInput.sSuffix = '';
        validInput.sState='None';
        
       MemberSearchWrapper_CH_HUM invalidInput1 = new MemberSearchWrapper_CH_HUM();
        invalidInput1.sBirthdate = '';
        invalidInput1.sLastName = '';
        invalidInput1.sFirstname = '';
        invalidInput1.sMemberId = '';
        invalidInput1.sGroupNumber = '';
        invalidInput1.sPhone = '';
        invalidInput1.sPostalCode = '';
        invalidInput1.sSuffix = '';
        invalidInput1.sState='Kent';
        

        MemberSearchWrapper_CH_HUM invalidInput2 = new MemberSearchWrapper_CH_HUM();
        invalidInput2.sFirstname = '';
        invalidInput2.sBirthdate = '';
        invalidInput2.sLastName = '';
        invalidInput2.sMemberId = '';
        invalidInput2.sGroupNumber = '';
        invalidInput2.sPhone = '';
        invalidInput2.sPostalCode = '23232';
        invalidInput2.sSuffix = '';
        invalidInput2.sState='';
   
        MemberSearchWrapper_CH_HUM invalidInput3 = new MemberSearchWrapper_CH_HUM();
        invalidInput3.sFirstname = '';
        invalidInput3.sBirthdate = '';
        invalidInput3.sLastName = '';
        invalidInput3.sMemberId = '';
        invalidInput3.sGroupNumber = '';
        invalidInput3.sPhone = '';
        invalidInput3.sPostalCode = '';
        invalidInput3.sSuffix = 'Mr';
        invalidInput3.sState='';  
        
        MemberSearchWrapper_CH_HUM invalidInput4 = new MemberSearchWrapper_CH_HUM();
        invalidInput4.sFirstname = 'Sama';
        invalidInput4.sBirthdate = '';
        invalidInput4.sLastName = '';
        invalidInput4.sMemberId = '';
        invalidInput4.sGroupNumber = '';
        invalidInput4.sPhone = '';
        invalidInput4.sPostalCode = '';
        invalidInput4.sSuffix = '';
        invalidInput4.sState='';  
        
        
        Test.startTest();
        try{
            CoachMemberSearchUtility_H_HUM.memberSearchFieldsEnteredMedicare(validInput);
        }catch(Exception e){
            system.assert((e.getMessage()).contains(HUMConstants.HUM_Member_Search_Fields_Not_Entered));
        }
         
        try{
            CoachMemberSearchUtility_H_HUM.validateInputDataCombinationsMedicare(invalidInput1,false);
        }catch(Exception e){
            system.assert((e.getMessage()).contains(HUMConstants.HumErrorStateSearchMedicare));
        }  
                
        try{
            CoachMemberSearchUtility_H_HUM.validateInputDataCombinationsMedicare(invalidInput2,false);
        }catch(Exception e){
            system.assert((e.getMessage()).contains(HUMConstants.HumErrorZipCodeSearchMedicare));
        } 
        
        try{
            CoachMemberSearchUtility_H_HUM.validateInputDataCombinationsMedicare(invalidInput3,false);
        }catch(Exception e){
            system.assert((e.getMessage()).contains(HUMConstants.HumSearchSuffixMemberIdValidation));
        }      

       try{
            CoachMemberSearchUtility_H_HUM.validateInputDataCombinationsMedicare(invalidInput4,false);
        }catch(Exception e){
            system.assert((e.getMessage()).contains(HUMConstants.HumErrorFirstNameSearchMedicare));
        }      

        Test.stopTest();
    }
    

        
    /*
    * Method Name : testErrorBirthDateSearchMedicare
    * Description :  
    * Return type : void
    * Paramater   : void
    */
    static testMethod void testErrorBirthDateSearchMedicare() 
    {
        MemberSearchWrapper_CH_HUM validInput = new MemberSearchWrapper_CH_HUM();
        validInput.sBirthdate = '10/10/2020';
        validInput.sLastName = '';
        validInput.sFirstname = '';
        validInput.sMemberId = '';
        validInput.sGroupNumber = '';
        validInput.sPhone = '';
        validInput.sPostalCode = '123456';
        validInput.sSuffix = '';
        validInput.sState='';
        
        MemberSearchWrapper_CH_HUM validInputTwo = new MemberSearchWrapper_CH_HUM();
        validInput.sBirthdate = '10/10/2020';
        validInput.sLastName = 'test';
        validInput.sFirstname = '';
        validInput.sMemberId = '';
        validInput.sGroupNumber = '';
        validInput.sPhone = '';
        validInput.sPostalCode = '123456';
        validInput.sSuffix = '';
        validInput.sState='';
        
        Test.startTest();
        try{
            CoachMemberSearchUtility_H_HUM.errorBirthDateSearchMedicare(validInput);
        }catch(Exception e){
            system.assert((e.getMessage()).contains(HUMConstants.HumErrorBirthDateSearchCombination));
        }
        CoachMemberSearchUtility_H_HUM.errorBirthDateSearchMedicare(validInputTwo);
        Test.stopTest();
    }
    
    /*
    * Method Name : testErrorStateSearchMedicare
    * Description :  
    * Return type : void
    * Paramater   : void
    */
    static testMethod void testErrorStateSearchMedicare() 
    {
        MemberSearchWrapper_CH_HUM validInput = new MemberSearchWrapper_CH_HUM();
        validInput.sBirthdate = '';
        validInput.sLastName = '';
        validInput.sFirstname = '';
        validInput.sMemberId = '';
        validInput.sGroupNumber = '';
        validInput.sPhone = '';
        validInput.sPostalCode = '123456';
        validInput.sSuffix = '';
        validInput.sState='Test';
        
        MemberSearchWrapper_CH_HUM validInputTwo = new MemberSearchWrapper_CH_HUM();
        validInput.sBirthdate = '';
        validInput.sLastName = 'test';
        validInput.sFirstname = '';
        validInput.sMemberId = '';
        validInput.sGroupNumber = '';
        validInput.sPhone = '';
        validInput.sPostalCode = '123456';
        validInput.sSuffix = '';
        validInput.sState='Test';
        
        Test.startTest();
        try{
            CoachMemberSearchUtility_H_HUM.errorStateSearchMedicare(validInput);
        }catch(Exception e){
            system.assert((e.getMessage()).contains(HUMConstants.HumErrorStateSearchCombination));
        }
        CoachMemberSearchUtility_H_HUM.errorStateSearchMedicare(validInputTwo);
        
        Test.stopTest();
    }
    
    /*
    * Method Name : testErrorFirstLastNameSearchMedicare
    * Description :  
    * Return type : void
    * Paramater   : void
    */
    static testMethod void testErrorFirstLastNameSearchMedicare() 
    {
        MemberSearchWrapper_CH_HUM validInput = new MemberSearchWrapper_CH_HUM();
        validInput.sBirthdate = '';
        validInput.sLastName = '';
        validInput.sFirstname = 'Test';
        validInput.sMemberId = '';
        validInput.sGroupNumber = '';
        validInput.sPhone = '';
        validInput.sPostalCode = '';
        validInput.sSuffix = '';
        validInput.sState='None';
        
        MemberSearchWrapper_CH_HUM validInputTwo = new MemberSearchWrapper_CH_HUM();
        validInput.sBirthdate = '';
        validInput.sLastName = 'test';
        validInput.sFirstname = '';
        validInput.sMemberId = '';
        validInput.sGroupNumber = '';
        validInput.sPhone = '';
        validInput.sPostalCode = '';
        validInput.sSuffix = '';
        validInput.sState='None';
        
        Test.startTest();
        try{
            CoachMemberSearchUtility_H_HUM.errorFirstLastNameSearchMedicare(validInput);
        }catch(Exception e){
            system.assert((e.getMessage()).contains(HUMConstants.HumErrorLastNameSearchMedicare));
        }
        
        CoachMemberSearchUtility_H_HUM.errorFirstLastNameSearchMedicare(validInputTwo);
        Test.stopTest();
    }
    
    /*
    * Method Name : testErrorZipCodeSearchMedicare
    * Description :  
    * Return type : void
    * Paramater   : void
    */
    static testMethod void testErrorZipCodeSearchMedicare() 
    {
        MemberSearchWrapper_CH_HUM validInput = new MemberSearchWrapper_CH_HUM();
        validInput.sBirthdate = '10/10/2020';
        validInput.sLastName = '';
        validInput.sFirstname = '';
        validInput.sMemberId = '';
        validInput.sGroupNumber = '';
        validInput.sPhone = '';
        validInput.sPostalCode = '123456';
        validInput.sSuffix = '';
        validInput.sState='None';
        
        MemberSearchWrapper_CH_HUM validInputTwo = new MemberSearchWrapper_CH_HUM();
        validInput.sBirthdate = '10/10/2020';
        validInput.sLastName = 'Test';
        validInput.sFirstname = '';
        validInput.sMemberId = '';
        validInput.sGroupNumber = '';
        validInput.sPhone = '';
        validInput.sPostalCode = '123456';
        validInput.sSuffix = '';
        validInput.sState='None';
        Test.startTest();
        
        try{
            CoachMemberSearchUtility_H_HUM.errorZipCodeSearchMedicare(validInput);
        }catch(Exception e){
            system.assert((e.getMessage()).contains(HUMConstants.HumErrorPostalCodeSearchCombination));
        }
        CoachMemberSearchUtility_H_HUM.errorZipCodeSearchMedicare(validInputTwo);
        
        Test.stopTest();
    }

    /*
    * Method Name : testisCRMfunctionalityON
    * Description : This method is used to validate CRM Functionality ON switch
    * Return type : void
    * Paramater   : void
    */
    static testMethod void testisCRMfunctionalityON()
    {
        User oUser = [select id, name,lastname from user where lastname='test CoachUser' limit 1];
        system.assertEquals(oUser.lastname =='test CoachUser',true); 
        system.runAs(oUser)
        {
            CRMFunctionality_ONOFF_Switch__c OSBONOFF= new CRMFunctionality_ONOFF_Switch__c();
            OSBONOFF.IsON__c=true;
            OSBONOFF.Name='738134';
            OSBONOFF.Story_Title__c='PR00091318 - MF 9 - Medicare OSB Vision and Fitness';
            Insert OSBONOFF;
            Boolean deduceosb;
            Test.startTest();
            
            deduceosb = CoachMemberSearchUtility_H_HUM.isCRMFunctionalityON('738134');
            
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
        User oUser = [select id, name,lastname from user where lastname='test CoachUser' limit 1];
        system.assertEquals(oUser.lastname =='test CoachUser',true); 
        system.runAs(oUser)
        {
            CRMFunctionality_ONOFF_Switch__c OSBONOFF= new CRMFunctionality_ONOFF_Switch__c();
            OSBONOFF.IsON__c=false;
            OSBONOFF.Name='738134';
            OSBONOFF.Story_Title__c='PR00091318 - MF 9 - Medicare OSB Vision and Fitness';
            Insert OSBONOFF;
            Boolean deduceosb;
            Test.startTest();
            
            deduceosb = CoachMemberSearchUtility_H_HUM.isCRMFunctionalityON('738134');
            
            Test.stopTest();
        }
    }
    
    /*
    * Method Name : testvalidateInputDataCombinations
    * Description :  This method is used to validate combinational search in Member screen
    * Return type : void
    * Paramater   : void
    */
    static testMethod void tesvalidateInputDataCombinations() 
    {
        MemberSearchWrapper_CH_HUM validInput = new MemberSearchWrapper_CH_HUM();
        validInput.sBirthdate = '02/02/2010';
        validInput.sLastName = 'Test';
        validInput.sFirstname = 'Test';
        validInput.sMemberId = '123456';
        validInput.sGroupNumber = '123456';
        validInput.sPhone = '123456';
        validInput.sPostalCode = '12355';
        validInput.sSuffix = '123';
        
        Boolean validInputCombination = CoachMemberSearchUtility_H_HUM.validateInputDataCombinations(validInput);
        System.assertEquals(validInputCombination,true);
        
        MemberSearchWrapper_CH_HUM invalidInput = new MemberSearchWrapper_CH_HUM();
        invalidInput.sBirthdate = '';
        invalidInput.sLastName = '';
        invalidInput.sFirstname = '';
        invalidInput.sMemberId = '';
        invalidInput.sGroupNumber = '';
        invalidInput.sPhone = '';
        invalidInput.sPostalCode = '';
        invalidInput.sSuffix = '';
        
        Boolean invalidInputCombination = CoachMemberSearchUtility_H_HUM.errorZipCodeSearch(invalidInput);
        System.assertEquals(invalidInputCombination,true);
        
       MemberSearchWrapper_CH_HUM invalidInput1 = new MemberSearchWrapper_CH_HUM();
        invalidInput1.sBirthdate = '01/01/1980';
        invalidInput1.sLastName = '';
        invalidInput1.sFirstname = '';
        invalidInput1.sMemberId = '';
        invalidInput1.sGroupNumber = '';
        invalidInput1.sPhone = '';
        invalidInput1.sPostalCode = '';
        invalidInput1.sSuffix = '';
        
        Boolean invalidInputCombination1 = CoachMemberSearchUtility_H_HUM.errorZipCodeSearch(invalidInput1);
        System.assertEquals(invalidInputCombination1,true);     
        
          
        
     


    }
    
    /*
    * Method Name : validateSuffixSearch
    * Description : This method is used to validate member id and suffix combination. 
    * Return type : void
    * Paramater   : void
    */
    static testMethod void testvalidateSuffixSearch() 
    {
        MemberSearchWrapper_CH_HUM memberWrapper = new MemberSearchWrapper_CH_HUM();
        memberWrapper.sSuffix = '123';
        memberWrapper.sMemberId = '123';
        Boolean validSuffixSearch = CoachMemberSearchUtility_H_HUM.validateSuffixSearch(memberWrapper);
        System.assertEquals(validSuffixSearch,true);
        try
        { 
            MemberSearchWrapper_CH_HUM memberWrapperInvalid = new MemberSearchWrapper_CH_HUM();
            memberWrapperInvalid.sSuffix = '123';
            memberWrapperInvalid.sMemberId = '';
            Boolean invalidateSuffixSearch = CoachMemberSearchUtility_H_HUM.validateSuffixSearch(memberWrapperInvalid);
        }
        catch(Exception e)
        {
            System.assertEquals(HUMConstants.HumSearchSuffixMemberIdValidation,e.getMessage());
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
        MemberSearchWrapper_CH_HUM validErrorBirthDate = new MemberSearchWrapper_CH_HUM();
        validErrorBirthDate.sBirthdate = '02/02/2010';
        validErrorBirthDate.sLastName = 'Test';
        validErrorBirthDate.sFirstname = 'Test';
        validErrorBirthDate.sMemberId = '123456';
        validErrorBirthDate.sGroupNumber = '123456';
        validErrorBirthDate.sPhone = '123456';
        
        Boolean validBirthDate = CoachMemberSearchUtility_H_HUM.errorBirthDateSearch(validErrorBirthDate);
        System.assertEquals(validBirthDate,true);
        
        try
        { 
            MemberSearchWrapper_CH_HUM invalidErrorBirthDate = new MemberSearchWrapper_CH_HUM();
            invalidErrorBirthDate.sBirthdate = '02/02/2010';
            invalidErrorBirthDate.sLastName = '';
            invalidErrorBirthDate.sFirstname = '';
            invalidErrorBirthDate.sMemberId = '';
            invalidErrorBirthDate.sGroupNumber = '';
            invalidErrorBirthDate.sPhone = '';
            
            Boolean invalidateErrorBirthDate1 = CoachMemberSearchUtility_H_HUM.errorBirthDateSearch(invalidErrorBirthDate);
        }
        catch(Exception e)
        {
            System.assertEquals(HUMConstants.HumErrorBirthDateSearch,e.getMessage());
        }
        
        try
        { 
            MemberSearchWrapper_CH_HUM invalidErrorBirthDate2 = new MemberSearchWrapper_CH_HUM();
            invalidErrorBirthDate2.sBirthdate = '02/02/2010';
            invalidErrorBirthDate2.sLastName = '';
            invalidErrorBirthDate2.sFirstname = '';
            invalidErrorBirthDate2.sMemberId = '123434';
            invalidErrorBirthDate2.sGroupNumber = '23423423';
            invalidErrorBirthDate2.sPhone = '(123) 456-789';
            
            Boolean invalidateErrorBirthDate2 = CoachMemberSearchUtility_H_HUM.errorBirthDateSearch(invalidErrorBirthDate2);
        }
        catch(Exception e)
        {
            System.assertEquals(HUMConstants.HumErrorBirthDateSearchCombination,e.getMessage());
        }
    }
    
    /*
    * Method Name : testgetQueryMapSoftphone
    * Description : This method is used to validate combinational search
    * Return type : void
    * Paramater   : void
    */
    static testMethod void testgetQueryMapSoftphone() 
    {   
        Map<String,String> mapOfString= new Map<String,String>();
        mapOfString.put('FirstName', 'test');
        
        Test.startTest();
        CoachMemberSearchUtility_H_HUM.getQueryMapSoftphone(mapOfString);
        Test.stopTest();
    }
    
    /*
    * Method Name : testerrorZipCodeSearch
    * Description : This method is used to validate combinational search
    * Return type : void
    * Paramater   : void
    */
    static testMethod void testerrorZipCodeSearch() 
    {
        MemberSearchWrapper_CH_HUM validZipCode = new MemberSearchWrapper_CH_HUM();
        validZipCode.sBirthdate = '02/02/2010';
        validZipCode.sLastName = 'Test';
        validZipCode.sFirstname = 'Test';
        validZipCode.sMemberId = '123456';
        validZipCode.sGroupNumber = '123456';
        validZipCode.sPhone = '123456';
        validZipCode.sPostalCode = '12355';
        
        Boolean validZip = CoachMemberSearchUtility_H_HUM.errorZipCodeSearch(validZipCode);
        System.assertEquals(validZip,true);
        
        try
        { 
            MemberSearchWrapper_CH_HUM invalidZipCode1 = new MemberSearchWrapper_CH_HUM();
            invalidZipCode1.sBirthdate = '02/02/2010';
            invalidZipCode1.sLastName = '';
            invalidZipCode1.sFirstname = '';
            invalidZipCode1.sMemberId = '';
            invalidZipCode1.sGroupNumber = '';
            invalidZipCode1.sPhone = '';
            invalidZipCode1.sPostalCode = '12355';
            Boolean invalidZip1 = CoachMemberSearchUtility_H_HUM.errorZipCodeSearch(invalidZipCode1);
        }
        catch(Exception e)
        {
            System.assertEquals(HUMConstants.HumErrorZipCodeSearch,e.getMessage());
        }
        
        try
        { 
            MemberSearchWrapper_CH_HUM invalidZipCode2 = new MemberSearchWrapper_CH_HUM();
            invalidZipCode2.sBirthdate = '02/02/2010';
            invalidZipCode2.sLastName = '';
            invalidZipCode2.sFirstname = '';
            invalidZipCode2.sMemberId = '123434';
            invalidZipCode2.sGroupNumber = '23423423';
            invalidZipCode2.sPhone = '(123) 456-789';
            invalidZipCode2.sPostalCode = '12355';
            Boolean invalidZip2 = CoachMemberSearchUtility_H_HUM.errorZipCodeSearch(invalidZipCode2);
        }
        catch(Exception e)
        {
            System.assertEquals(HUMConstants.HumErrorPostalCodeSearchCombination,e.getMessage());
        }
    }
    
    /*
    * Method Name : testerrorStateSearch
    * Description : This method is used to validate if state field is filled or not
    * Return type : void
    * Paramater   : void
    */
    static testMethod void testerrorStateSearch() 
    {
        MemberSearchWrapper_CH_HUM validState = new MemberSearchWrapper_CH_HUM();
        validState.sBirthdate = '02/02/2010';
        validState.sLastName = 'Test';
        validState.sFirstname = 'Test';
        validState.sMemberId = '123456';
        validState.sGroupNumber = '123456';
        validState.sPhone = '123456';
        validState.sPostalCode = '12355';
        validState.sState = 'Alabama';
        
        Boolean validStateSearch = CoachMemberSearchUtility_H_HUM.errorZipCodeSearch(validState);
        
        try
        { 
            MemberSearchWrapper_CH_HUM invalidState1 = new MemberSearchWrapper_CH_HUM();
            invalidState1.sBirthdate = '';
            invalidState1.sLastName = '';
            invalidState1.sFirstname = '';
            invalidState1.sMemberId = '';
            invalidState1.sGroupNumber = '';
            invalidState1.sPhone = '';
            invalidState1.sPostalCode = '';
            invalidState1.sState = 'Alabama';
            
            Boolean invalidStateSearch1 = CoachMemberSearchUtility_H_HUM.errorStateSearch(invalidState1);
        }
        catch(Exception e)
        {
            System.assertEquals(HUMConstants.HumErrorStateSearch,e.getMessage());
        }
        try
        { 
            MemberSearchWrapper_CH_HUM invalidState2 = new MemberSearchWrapper_CH_HUM();
            invalidState2.sBirthdate = '';
            invalidState2.sLastName = '';
            invalidState2.sFirstname = '';
            invalidState2.sMemberId = '123456';
            invalidState2.sGroupNumber = '';
            invalidState2.sPhone = '';
            invalidState2.sPostalCode = '';
            invalidState2.sState = 'Alabama';
            
            Boolean invalidStateSearch2 = CoachMemberSearchUtility_H_HUM.errorStateSearch(invalidState2);
        }
        catch(Exception e)
        {
            System.assertEquals(HUMConstants.HumErrorStateSearchCombination,e.getMessage());
        }   
        
        MemberSearchWrapper_CH_HUM invalidState3 = new MemberSearchWrapper_CH_HUM();
        invalidState3.sBirthdate = '';
        invalidState3.sLastName = '';
        invalidState3.sFirstname = '';
        invalidState3.sMemberId = '123456';
        invalidState3.sGroupNumber = '';
        invalidState3.sPhone = '';
        invalidState3.sPostalCode = '';
        invalidState3.sState = 'None';
        
        Boolean invalidStateSearch3 = CoachMemberSearchUtility_H_HUM.errorStateSearch(invalidState3);
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
        MemberSearchWrapper_CH_HUM validFirstLastName = new MemberSearchWrapper_CH_HUM();
        validFirstLastName.sBirthdate = '02/02/2010';
        validFirstLastName.sLastName = 'Test';
        validFirstLastName.sFirstname = 'Test';
        validFirstLastName.sMemberId = '123456';
        validFirstLastName.sGroupNumber = '123456';
        validFirstLastName.sPhone = '123456';
        validFirstLastName.sPostalCode = '12355';
        validFirstLastName.sState = 'Alabama';
        
        Boolean validFNLN = CoachMemberSearchUtility_H_HUM.errorFirstLastNameSearch(validFirstLastName);
        System.assertEquals(validFNLN,true);
        
        try
        { 
            MemberSearchWrapper_CH_HUM invalidFirstLastName = new MemberSearchWrapper_CH_HUM();
            invalidFirstLastName.sBirthdate = '';
            invalidFirstLastName.sLastName = '';
            invalidFirstLastName.sFirstname = 'Test';
            invalidFirstLastName.sMemberId = '';
            invalidFirstLastName.sGroupNumber = '';
            invalidFirstLastName.sPhone = '';
            invalidFirstLastName.sPostalCode = '';
            invalidFirstLastName.sState = 'None';
            
            Boolean invalidFNLN = CoachMemberSearchUtility_H_HUM.errorFirstLastNameSearch(invalidFirstLastName);
        }
        catch(Exception e)
        {
            System.assertEquals(HUMConstants.HumErrorFirstNameSearch,e.getMessage());
        }
        try
        { 
            MemberSearchWrapper_CH_HUM invalidFirstLastName = new MemberSearchWrapper_CH_HUM();
            invalidFirstLastName.sBirthdate = '';
            invalidFirstLastName.sLastName = 'Test';
            invalidFirstLastName.sFirstname = '';
            invalidFirstLastName.sMemberId = '';
            invalidFirstLastName.sGroupNumber = '';
            invalidFirstLastName.sPhone = '';
            invalidFirstLastName.sPostalCode = '';
            invalidFirstLastName.sState = 'None';
            
            Boolean invalidFNLN = CoachMemberSearchUtility_H_HUM.errorFirstLastNameSearch(invalidFirstLastName);
        }
        catch(Exception e)
        {
            System.assertEquals(HUMConstants.HumErrorLastNameSearch,e.getMessage());
        }   
    }
}