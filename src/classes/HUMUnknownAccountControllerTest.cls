/***********************************************************************************************************
Apex Class Name  : HUMUnknownAccountControllerTest
Version          : 1.0
Created Date     : July 6 2014
Function         : Test class for HUMUnknownAccountController
*************************************************************************************************************************************

Modification Log:

* Developer Name                Code Review           Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------------
* Chaitanya Kumar V             17172                 08/06/2014                   Original Version
* Pradeep Raju                  17172                 08/07/2014                   Updated the code for negative scenario
* Shruthi Karanth               17172                 08/09/2014                   Added method for newly added provider classification code
* Manish Kumar                  17534                 08/27/2014                   Test Class Updated
* Tanushree Hazari              17886                 09/12/2014                   updated test class as per fields being used in Unknown Member VF page
* Chaitanya Kumar               17933                 09/15/2014                   Created testConsumerTaxID 
                                                                                   Modified State url parameter in all methods to a 2 letter code   
* Chaitanya Kumar               18059                 09/19/2014                   Updated values in testConsumerTaxID as per new validation rules in main controller
* Manish Kumar                  18362                 10/08/2014                   Modified code for review#18362
* Vishal Verma                  19583                 11/27/2014                   Modified code to increase the coverage for insertConsumerTaxIds method.
* Tanushree Hazari              19690                 12/01/2014                   Added as per REQ-70935
* Ranjeeth Nagishetty           19730                 12/08/2014                   CodeReview Comments Fixed
* Ranjeeth Nagishetty           19850                 12/11/2014                   test class updated
* Ranjeeth Nagishetty           23434                 05/27/2015                   test class updated
* Moshitha Gunasekaran          262118                06/28/2018                   REQ - 350472 ID Number field on the Unknown Account tab
* Sunil Bukya                                         01/24/2019                   Removed Platform switch
* Santhi Mandava                                      03/12/2019                   Removed "DpaaSWebServiceMockImpl" usage.
* Santhi Mandava                355559                05/10/2019                   Removed "ENCRYPTIONTYPE" custom setting creation
*************************************************************************************************************************************************************/

@isTest
private class HUMUnknownAccountControllerTest 
{
    /*
    * Method Name : testHUMUnknownMemberAccountController
    * Description : Test method for Account record validation onclick of save
    * Return type : void
    * Paramater   : NA
    */
    static testMethod void testHUMUnknownMemberAccountController() 
    {
        //Load all Constants data
        HUMTestDataHelperTest.getHUMConstantsData();

        Test.setCurrentPage(Page.HUMUnknownMemberAccountEdit);
        
        Account oAcc = new Account();
        oAcc.FirstName = 'FirstName123';
        oAcc.LastName = 'LastName';
        ApexPages.StandardController oSC = new ApexPages.StandardController(oAcc);
        System.assertNotEquals(oSC, NULL, 'Value not matched');
        
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1', 'Customer Care Specialist');
        insert oCCSUser;
        system.assert(oCCSUser.Id != NULL);
        
        List<HUMCipherCloudSettings__c> lstApplicationName = new List<HUMCipherCloudSettings__c>();
        lstApplicationName.add(new HUMCipherCloudSettings__c(Name = 'APPLICATION_NAME', Value__c = 'CRMR1'));
        insert lstApplicationName;
        system.assertNotEquals(lstApplicationName.size(), 0);
        
        //Load Constants from Custom Setting
        HUMTestDataHelperTest.getHUMConstantsData();
        HUMUnknownAccountController oUnknownController = NULL;
        
        system.runAs(oCCSUser) 
        {
            Test.startTest();
            
            //Set the Type of Unknown Controller as Member
            ApexPages.currentPage().getParameters().put('type','M');
            ApexPages.currentPage().getParameters().put('type','p');
            oUnknownController = new HUMUnknownAccountController(oSC);
            oAcc.BirthDate__c = 'asdfghas';
            oUnknownController.validateBirthdate();
            System.assertEquals(oUnknownController.account.BirthDate__c, 'asdfghas');
            oAcc.BirthDate__c = '01/32/1999';
            oUnknownController.validateBirthdate();
            System.assertEquals(oUnknownController.account.BirthDate__c, '01/32/1999');
            oAcc.BirthDate__c = '02/29/1999';
            oUnknownController.validateBirthdate();
            System.assertEquals(oUnknownController.account.BirthDate__c, '02/29/1999');
            oAcc.BirthDate__c = '022919990';
            oUnknownController.validateBirthdate();
            System.assertEquals(oUnknownController.account.BirthDate__c, '022919990');
            oAcc.BirthDate__c = '09/29/2014';
            oUnknownController.validateBirthdate();
            System.assertEquals(oUnknownController.account.BirthDate__c, '09/29/2014');
            oAcc.PersonMailingPostalCode = 'asdwe';
            oUnknownController.validatePostalCode();
            System.assertEquals(oUnknownController.account.PersonMailingPostalCode, 'asdwe');
            oAcc.PersonMailingPostalCode = '123';
            oUnknownController.validatePostalCode();
            System.assertEquals(oUnknownController.account.PersonMailingPostalCode, '123');
            oAcc.PersonHomePhone = 'asd';
            oUnknownController.validatePhone();
            System.assertEquals(oUnknownController.account.PersonHomePhone, 'asd');
            oAcc.PersonHomePhone = '123456qwas';
            oUnknownController.validatePhone();
            System.assertEquals(oUnknownController.account.PersonHomePhone, '123456qwas');
            oAcc.PersonHomePhone = '(900) 072-2208';
            oAcc.PersonMailingPostalCode = '12345';
            oAcc.BirthDate__c = '09/01/2014';
            oUnknownController.save();
            System.assertEquals(oUnknownController.account.PersonHomePhone, '(900) 072-2208');
            System.assertEquals(oUnknownController.account.PersonMailingPostalCode, '12345');
            oAcc.FirstName = '@Test@';
            oAcc.LastName = '@Test@';
            oUnknownController.validateFirstName();
            System.assertEquals(oUnknownController.account.Firstname, '@Test@');
            oUnknownController.validateLastName();
            System.assertEquals(oUnknownController.account.Lastname, '@Test@');
            oAcc.Individual_First_Name__c = '@Test@testtettesttsetetettest';
            oUnknownController.validateFirstName();
            System.assertEquals(oUnknownController.account.Individual_First_Name__c, '@Test@testtettesttsetetettest');
            oAcc.Individual_Last_Name__c = 'aa';
            oUnknownController.validateLastName();
            System.assertEquals(oUnknownController.account.Individual_Last_Name__c, 'aa');
            oAcc.NPI_ID__c ='123456';
            oUnknownController.validateNPILength();
            oAcc.NPI_ID__c ='123456@';
            System.assertEquals(oUnknownController.account.NPI_ID__c , '123456@');
            oUnknownController.validateNPI() ;
            oAcc.billingpostalcode = '123';
            system.assertEquals(oAcc.billingpostalcode,'123');
            oUnknownController.validatePostalCode();
            system.assert(oUnknownController.validatePostalCode()!=null);    
            oUnknownController.resetValuesOnAgentTypeChange();
            oUnknownController.resetValuesOnProviderTypeChange();
            Test.stopTest();
        }
    }
     
    /*
    * Method Name : testHUMUnknownAccountController
    * Description : Test method for Account record creation onclick of save
    * Return type : void
    * Paramater   : NA
    */
    static testMethod void testHUMUnknownAccountController() 
    {        
        //Load all Constants data
        HUMTestDataHelperTest.getHUMConstantsData();
       
        Test.setCurrentPage(Page.HUMUnknownMemberAccountEdit);
        
        List<HUMCipherCloudSettings__c> lstApplicationName = new List<HUMCipherCloudSettings__c> ();
        lstApplicationName.add(new HUMCipherCloudSettings__c(Name = 'APPLICATION_NAME', Value__c = 'CRMR1'));
        insert lstApplicationName;
        System.assertNotEquals(lstApplicationName, NULL, 'Value not matched');
        //Load Constants from Custom Setting
        HUMTestDataHelperTest.getHUMConstantsData();
        
        //Set the Type of Unknonw Controller as Agent
        ApexPages.currentPage().getParameters().put('type','G');
        ApexPages.currentPage().getParameters().put('gName','P1234');
        ApexPages.currentPage().getParameters().put('gNo','12345');
     
        Account oAcc = new Account();
        System.assertNotEquals(oAcc, NULL, 'Value not matched');
        ApexPages.StandardController oSC = new ApexPages.StandardController(oAcc);
        System.assertNotEquals(oSC, NULL, 'Value not matched');
        
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        System.assertNotEquals(oCCSUser.Id, NULL, 'Value not matched');
        
        HUMUnknownAccountController oUnknownController;
        
        system.runAs(oCCSUser) 
        {
            Test.startTest();
            
            oUnknownController = new HUMUnknownAccountController(oSC);
            system.assertNotEquals(oUnknownController, NULL, 'Value not matched');
            oUnknownController.save();
            system.assertNotEquals(oUnknownController.account, NULL, 'Value not matched');
            
            Test.stopTest();
        }
    }
    
    /*
    * Method Name : testHUMUnknownAccount
    * Description : Test method to validate created accounts
    * Return type : void
    * Paramater   : NA
    */
    static testMethod void testHUMUnknownAccount()
    {
        //Load all Constants data
        HUMTestDataHelperTest.getHUMConstantsData();
        
        Test.setCurrentPage(Page.HUMUnknownMemberAccountEdit);
        
        List<HUMCipherCloudSettings__c> lstApplicationName = new List<HUMCipherCloudSettings__c> ();
        lstApplicationName.add(new HUMCipherCloudSettings__c(Name = 'APPLICATION_NAME', Value__c = 'CRMR1'));
        insert lstApplicationName;
        system.assertNotEquals(lstApplicationName, NULL, 'Value not matched');
        //Load Constants from Custom Setting
        HUMTestDataHelperTest.getHUMConstantsData();
        
        //Set the Type of Unknonw Controller as Agent
        ApexPages.currentPage().getParameters().put('type','A');
        ApexPages.currentPage().getParameters().put('type','M');
        ApexPages.currentPage().getParameters().put('aId','P1234');
        ApexPages.currentPage().getParameters().put('State','MI');
        ApexPages.currentPage().getParameters().put('fName','NameFirst');
        ApexPages.currentPage().getParameters().put('lName','LastName');
        ApexPages.currentPage().getParameters().put('agName','AgentName');
        ApexPages.currentPage().getParameters().put('aType','Agency');
        
        Account oAcc = new Account();
        system.assertNotEquals(oAcc, NULL, 'Value not matched');
        ApexPages.StandardController oSC = new ApexPages.StandardController(oAcc);
        system.assertNotEquals(oSC, NULL, 'Value not matched');
        
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1', 'Customer Care Specialist');
        insert oCCSUser;
        system.assertNotEquals(oCCSUser.Id, NULL, 'Value not matched');
        HUMUnknownAccountController oUnknownController;
        
        system.runAs(oCCSUser) 
        {            
            Test.startTest();
            
            oUnknownController = new HUMUnknownAccountController(oSC);
            oUnknownController.account.Provider_Classification__c='Individual';
            oUnknownController.save();
            
            system.assert( oUnknownController.account!=null);
            
            oUnknownController.account.Provider_Classification__c='Facility';
            oUnknownController.save();
            system.assert( oUnknownController.account!=null);
            oUnknownController.account.Provider_Classification__c='Provider Group';
            oUnknownController.save();
            
            system.assert( oUnknownController.account!=null);
            
            Test.stopTest();
        }
    }
    
    /*
    * Method Name : testNegativeTestScenario
    * Description : Test method for validating the negative scenarios
    * Return type : void
    * Paramater   : NA
    */
    static testMethod void testNegativeTestScenario()
    {   
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        System.assertNotEquals(oCCSUser, NULL, 'Value not matched');

        List<HUMCipherCloudSettings__c> lstApplicationName = new List<HUMCipherCloudSettings__c> ();
        System.assertNotEquals(lstApplicationName, NULL, 'Value not matched');
        lstApplicationName.add(new HUMCipherCloudSettings__c(Name = 'APPLICATION_NAME',  Value__c = 'CRMR1'));
        insert lstApplicationName;
        System.assertNotEquals(lstApplicationName, NULL, 'Value not matched');
        
        //Load Constants from Custom Setting
        HUMTestDataHelperTest.getHUMConstantsData();
        
        system.runAs(oCCSUser)
        {
            Test.startTest();
            //Load all Constants data
            HUMTestDataHelperTest.getHUMConstantsData();
            
            Test.setCurrentPage(Page.HUMUnknownMemberAccountEdit);
            
            HUMUnknownAccountController oUnknownController ;
            Account oAcc = new Account();
            System.assertNotEquals(oAcc, NULL, 'Value not matched');
            ApexPages.StandardController oSC = new ApexPages.StandardController(oAcc);
            System.assertNotEquals(oSC, NULL, 'Value not matched');
            
            oUnknownController = new HUMUnknownAccountController(oSC);
            System.assertNotEquals(oUnknownController, NULL, 'Value not matched');
            oUnknownController.account = null;
            oUnknownController.save();
            List<Error_Log__c> error = new List<Error_Log__c>();
            oUnknownController.listErrorLogs = error;
            List<Error_Log__c> errorlogs = oUnknownController.listErrorLogs;    

            Test.stopTest();
            system.assert(oUnknownController.bIsErrorOnPage == true);
        }
    }
    
    /*
    * Method Name : testProviderClassificationScenario
    * Description : Test method for validating the the provider classifcation list
    * Return type : void
    * Paramater   : NA
    */
    static testMethod void testProviderClassificationScenario()
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        System.assertNotEquals(oCCSUser, NULL, 'Value not matched');
        
        List<HUMCipherCloudSettings__c> lstApplicationName = new List<HUMCipherCloudSettings__c> ();
        lstApplicationName.add(new HUMCipherCloudSettings__c(Name = 'APPLICATION_NAME',  Value__c = 'CRMR1'));
        insert lstApplicationName;
        System.assertNotEquals(lstApplicationName, NULL, 'Value not matched');
        
        //Load Constants from Custom Setting
        HUMTestDataHelperTest.getHUMConstantsData();
        
        system.runAs(oCCSUser)
        {
            Test.startTest();
            //Load all Constants data
            HUMTestDataHelperTest.getHUMConstantsData();
            Account oAcc = new Account();
            System.assertNotEquals(oAcc, NULL, 'Value not matched');
            ApexPages.StandardController oSC = new ApexPages.StandardController(oAcc);
            System.assertNotEquals(oSC, NULL, 'Value not matched');
            HUMUnknownAccountController oUnknownController = new HUMUnknownAccountController(oSC);
            System.assertNotEquals(oUnknownController, NULL, 'Value not matched');
            
            //Create Custom settings
            insert new HumProviderClassifications__c( Name = 'Facility' , ClassificationTypeCode__c = 'F');
            List<SelectOption> lstProviderClassification = new List<SelectOption>();
            System.assertNotEquals(lstProviderClassification, NULL, 'Value not matched');
            oUnknownController.RetrieveProviderClassification();
            
            Test.stopTest();
        }
    }
    
    /*
    * Method Name : testConsumerTaxID
    * Description : Test method to validate the creation of ConsumerID records
    * Return type : void
    * Paramater   : NA
    */
    static testMethod void testConsumerTaxID()
    {
        Test.setCurrentPage(Page.HUMUnknownMemberAccountEdit);
        
        List<HUMCipherCloudSettings__c> lstApplicationName = new List<HUMCipherCloudSettings__c> ();
        lstApplicationName.add(new HUMCipherCloudSettings__c(Name = 'APPLICATION_NAME',  Value__c = 'CRMR1'));
        insert lstApplicationName;
        System.assertNotEquals(lstApplicationName, NULL, 'Value not matched');
        
        //Load Constants from Custom Setting
        HUMTestDataHelperTest.getHUMConstantsData();
        
        //Set the Type of Unknonw Controller as Agent
        ApexPages.currentPage().getParameters().put('type','A');
        ApexPages.currentPage().getParameters().put('aId','1234');
        ApexPages.currentPage().getParameters().put('State','MI');
        ApexPages.currentPage().getParameters().put('fName','NameFirst');
        ApexPages.currentPage().getParameters().put('lName','LastName');
        ApexPages.currentPage().getParameters().put('agName','AgentName');
        ApexPages.currentPage().getParameters().put('aType','Agency');
        
        Account oAcc = new Account(Name = 'Test');
        System.assertNotEquals(oAcc, NULL, 'Value not matched');
        oAcc.Provider_Classification__c = 'F';
        ApexPages.StandardController oSC = new ApexPages.StandardController(oAcc);
        System.assertNotEquals(oSC, NULL, 'Value not matched');
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        System.assertNotEquals(oCCSUser, NULL, 'Value not matched');
        
        HUMUnknownAccountController oUnknownController;
        
        system.runAs(oCCSUser) 
        {            
            Test.startTest();
            insert new HumProviderClassifications__c( Name = 'Facility' , ClassificationTypeCode__c = 'F');
            oUnknownController = new HUMUnknownAccountController(oSC);
            oUnknownController.sTaxID = '1234567890';
            oUnknownController.idNumber = '123456789';
            oUnknownController.consumerIDType = 'SSN';
            oUnknownController.save();
            oUnknownController.insertConsumerTaxIds('1234567890' , oAcc.Id);
            oUnknownController.isIDNumberRequired();
            oUnknownController.idNumber = '';
            oUnknownController.consumerIDType = '';
            oUnknownController.isIDNumberRequired();
            oUnknownController.errorLevel = 'Errror';
            string erroMsg = oUnknownController.stateErrorMessage;
            List<String> errorList = new List<String>{erroMsg};
            String JSONMsg = System.JSON.serialize(errorList);
            oUnknownController.sErrMsgJSON = JSONMsg;
            oUnknownController.showMessage();
            
            Test.stopTest();
            
            List<ConsumerId__c> lstConsumerIDs = [Select id from ConsumerId__c where account__c =: oUnknownController.account.id];
        }
    }

    /*
    * Method Name : testConsumerTaxIDForMember
    * Description : Test method to validate the creation of ConsumerID records
    * Return type : void
    * Paramater   : NA
    */
    static testMethod void testConsumerTaxIDForMember()
    {
        Test.setCurrentPage(Page.HUMUnknownMemberAccountEdit);
        
        List<HUMCipherCloudSettings__c> lstApplicationName = new List<HUMCipherCloudSettings__c> ();
        lstApplicationName.add(new HUMCipherCloudSettings__c(Name = 'APPLICATION_NAME',  Value__c = 'CRMR1'));
        insert lstApplicationName;
        System.assertNotEquals(lstApplicationName, NULL, 'Value not matched');
        
        //Load Constants from Custom Setting
        HUMTestDataHelperTest.getHUMConstantsData();
        
        //Set the Type of Unknonw Controller as Agent
        ApexPages.currentPage().getParameters().put('type','M');
        ApexPages.currentPage().getParameters().put('aId','12345');
        ApexPages.currentPage().getParameters().put('State','MI');
        ApexPages.currentPage().getParameters().put('fName','NameFirst');
        ApexPages.currentPage().getParameters().put('lName','LastName');
        ApexPages.currentPage().getParameters().put('idType','HumanaId');
        
        Account oAcc = new Account(Name = 'Test');
        oAcc.FirstName = 'FirstName123';
        oAcc.LastName = 'LastName';
        oAcc.BirthDate__c = '09/29/2014';
        oAcc.PersonHomePhone = '(900) 072-2208';
        oAcc.PersonMailingPostalCode = '12345';
        oAcc.NPI_ID__c ='123456';
        oAcc.billingpostalcode = '12345';
        System.assertNotEquals(oAcc, NULL, 'Value not matched');
        ApexPages.StandardController oSC = new ApexPages.StandardController(oAcc);
        System.assertNotEquals(oSC, NULL, 'Value not matched');
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        System.assertNotEquals(oCCSUser, NULL, 'Value not matched');
        
        HUMUnknownAccountController oUnknownController;
        
        system.runAs(oCCSUser) 
        {            
            Test.startTest();
            insert new HumProviderClassifications__c( Name = 'IndividualName' , ClassificationTypeCode__c = 'I');
            oUnknownController = new HUMUnknownAccountController(oSC);
            oUnknownController.sAccountType = 'M';
            oUnknownController.sTaxID = '1234567890';
            oUnknownController.idNumber = '1234567890';  
            oUnknownController.consumerIDType = oUnknownController.lstConsumerIDTypes[2].getValue();
            oUnknownController.save();
            oUnknownController.insertConsumerTaxIds('1234567890' , oAcc.Id);
            
            Test.stopTest();
            
            List<ConsumerId__c> lstConsumerIDs = [Select id from ConsumerId__c where account__c =: oUnknownController.account.id];
        }
    }

        /*
    * Method Name : testConsumerTaxIDForMemberNegative
    * Description : Test method to validate the creation of ConsumerID records for negative
    * Return type : void
    * Paramater   : NA
    */
    static testMethod void testConsumerTaxIDForMemberNegative()
    {
        Test.setCurrentPage(Page.HUMUnknownMemberAccountEdit);
        
        List<HUMCipherCloudSettings__c> lstApplicationName = new List<HUMCipherCloudSettings__c> ();
        lstApplicationName.add(new HUMCipherCloudSettings__c(Name = 'APPLICATION_NAME',  Value__c = 'CRMR1'));
        insert lstApplicationName;
        System.assertNotEquals(lstApplicationName, NULL, 'Value not matched');
        
        //Load Constants from Custom Setting
        HUMTestDataHelperTest.getHUMConstantsData();
        
        //Set the Type of Unknonw Controller as Agent
        ApexPages.currentPage().getParameters().put('type','M');
        ApexPages.currentPage().getParameters().put('aId','12345');
        ApexPages.currentPage().getParameters().put('State','MI');
        ApexPages.currentPage().getParameters().put('fName','NameFirst$#');
        ApexPages.currentPage().getParameters().put('lName','LastName');
        ApexPages.currentPage().getParameters().put('idType','HumanaId');
        
        Account oAcc = new Account(Name = 'Test');
        oAcc.FirstName = 'FirstName123#';
        oAcc.LastName = 'LastName';
        oAcc.BirthDate__c = '09/29/2014';
        oAcc.PersonHomePhone = '(900) 072-2208';
        oAcc.PersonMailingPostalCode = '12345';
        oAcc.NPI_ID__c ='123456';
        oAcc.billingpostalcode = '12345';
        System.assertNotEquals(oAcc, NULL, 'Value not matched');
        ApexPages.StandardController oSC = new ApexPages.StandardController(oAcc);
        System.assertNotEquals(oSC, NULL, 'Value not matched');
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        System.assertNotEquals(oCCSUser, NULL, 'Value not matched');
        
        HUMUnknownAccountController oUnknownController;
        
        system.runAs(oCCSUser) 
        {            
            Test.startTest();
           
            oUnknownController = new HUMUnknownAccountController(oSC);
            oUnknownController.save();
            
            
            Test.stopTest();
            
            List<ConsumerId__c> lstConsumerIDs = [Select id from ConsumerId__c where account__c =: oUnknownController.account.id];
        }
    }

        static testMethod void testConsumerTaxIDForMemberFirstLastName()
    {
        Test.setCurrentPage(Page.HUMUnknownMemberAccountEdit);
        
        List<HUMCipherCloudSettings__c> lstApplicationName = new List<HUMCipherCloudSettings__c> ();
        lstApplicationName.add(new HUMCipherCloudSettings__c(Name = 'APPLICATION_NAME',  Value__c = 'CRMR1'));
        insert lstApplicationName;
        System.assertNotEquals(lstApplicationName, NULL, 'Value not matched');
        
        //Load Constants from Custom Setting
        HUMTestDataHelperTest.getHUMConstantsData();
        
        //Set the Type of Unknonw Controller as Agent
        ApexPages.currentPage().getParameters().put('type','UM');
        ApexPages.currentPage().getParameters().put('aId','12345');
        ApexPages.currentPage().getParameters().put('State','MI');
        ApexPages.currentPage().getParameters().put('fName','NameFirst$#12233@@@@@@@aaaaaaaaaaaaaaaaaaaaaa');
        ApexPages.currentPage().getParameters().put('lName','LastName@#bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb');
        ApexPages.currentPage().getParameters().put('idType','HumanaId');
        
        Account oAcc = new Account(Name = '');
        oAcc.Individual_First_Name__c = 'NameFirst$#12233@@@@@@@aaaaaaaaaaaaaaaaaaaaaa';
        oAcc.Individual_Last_Name__c = 'LastName@#bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb';
        oAcc.BirthDate__c = '09/29/2014';
        oAcc.PersonHomePhone = '(900) 072-2208';
        oAcc.PersonMailingPostalCode = '12345';
        oAcc.NPI_ID__c ='123456';
        oAcc.billingpostalcode = '12345';
        System.assertNotEquals(oAcc, NULL, 'Value not matched');
        ApexPages.StandardController oSC = new ApexPages.StandardController(oAcc);
        System.assertNotEquals(oSC, NULL, 'Value not matched');
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        System.assertNotEquals(oCCSUser, NULL, 'Value not matched');
        
        HUMUnknownAccountController oUnknownController;
        
        system.runAs(oCCSUser) 
        {            
            Test.startTest();
           
            oUnknownController = new HUMUnknownAccountController(oSC);
            //oUnknownController.sAccountType = 'UM';
            oUnknownController.save();
            
            
            Test.stopTest();
            
            List<ConsumerId__c> lstConsumerIDs = [Select id from ConsumerId__c where account__c =: oUnknownController.account.id];
        }
    }
    static testMethod void testConsumerTaxIDForProvider()
    {
        Test.setCurrentPage(Page.HUMUnknownMemberAccountEdit);
        
        List<HUMCipherCloudSettings__c> lstApplicationName = new List<HUMCipherCloudSettings__c> ();
        lstApplicationName.add(new HUMCipherCloudSettings__c(Name = 'APPLICATION_NAME',  Value__c = 'CRMR1'));
        insert lstApplicationName;
        System.assertNotEquals(lstApplicationName, NULL, 'Value not matched');
        
        //Load Constants from Custom Setting
        HUMTestDataHelperTest.getHUMConstantsData();
        
        //Set the Type of Unknonw Controller as Agent
        ApexPages.currentPage().getParameters().put('type','P');
        ApexPages.currentPage().getParameters().put('aId','12345');
        ApexPages.currentPage().getParameters().put('State','MI');
        ApexPages.currentPage().getParameters().put('fName','NameFirst$#');
        ApexPages.currentPage().getParameters().put('lName','LastName');
        ApexPages.currentPage().getParameters().put('idType','HumanaId');
        
        Account oAcc = new Account(Name = 'Test');
        oAcc.FirstName = 'FirstName123#';
        oAcc.LastName = 'LastName';
        oAcc.BirthDate__c = '09/29/2014';
        oAcc.PersonHomePhone = '(900) 072-2208';
        oAcc.PersonMailingPostalCode = '12345';
        oAcc.NPI_ID__c ='123456';
        oAcc.billingpostalcode = '12345';
        System.assertNotEquals(oAcc, NULL, 'Value not matched');
        ApexPages.StandardController oSC = new ApexPages.StandardController(oAcc);
        System.assertNotEquals(oSC, NULL, 'Value not matched');
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        System.assertNotEquals(oCCSUser, NULL, 'Value not matched');
        
        HUMUnknownAccountController oUnknownController;
        
        system.runAs(oCCSUser) 
        {            
            Test.startTest();
           
            oUnknownController = new HUMUnknownAccountController(oSC);
            oUnknownController.save();
            
            
            Test.stopTest();
            
            List<ConsumerId__c> lstConsumerIDs = [Select id from ConsumerId__c where account__c =: oUnknownController.account.id];
        }
    }
    static testMethod void testConsumerTaxIDForGroup()
    {
        Test.setCurrentPage(Page.HUMUnknownMemberAccountEdit);
        
        List<HUMCipherCloudSettings__c> lstApplicationName = new List<HUMCipherCloudSettings__c> ();
        lstApplicationName.add(new HUMCipherCloudSettings__c(Name = 'APPLICATION_NAME',  Value__c = 'CRMR1'));
        insert lstApplicationName;
        System.assertNotEquals(lstApplicationName, NULL, 'Value not matched');
        
        //Load Constants from Custom Setting
        HUMTestDataHelperTest.getHUMConstantsData();
        
        //Set the Type of Unknonw Controller as Agent
        ApexPages.currentPage().getParameters().put('type','G');
        ApexPages.currentPage().getParameters().put('aId','12345');
        ApexPages.currentPage().getParameters().put('State','MI');
        ApexPages.currentPage().getParameters().put('fName','NameFirst$#');
        ApexPages.currentPage().getParameters().put('lName','LastName');
        ApexPages.currentPage().getParameters().put('idType','HumanaId');
        
        Account oAcc = new Account(Name = 'Test');
        oAcc.FirstName = 'FirstName123#';
        oAcc.LastName = 'LastName';
        oAcc.BirthDate__c = '09/29/2014';
        oAcc.PersonHomePhone = '(900) 072-2208';
        oAcc.PersonMailingPostalCode = '12345';
        oAcc.NPI_ID__c ='123456';
        oAcc.billingpostalcode = '123456';
        oAcc.group_number__c = '123456';
        
        System.assertNotEquals(oAcc, NULL, 'Value not matched');
        ApexPages.StandardController oSC = new ApexPages.StandardController(oAcc);
        System.assertNotEquals(oSC, NULL, 'Value not matched');
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        System.assertNotEquals(oCCSUser, NULL, 'Value not matched');
        
        HUMUnknownAccountController oUnknownController;
        
        system.runAs(oCCSUser) 
        {            
            Test.startTest();
           
            oUnknownController = new HUMUnknownAccountController(oSC);
            //oUnknownController.sAccountType = 'G';
            oUnknownController.save();
            Test.stopTest();
            
            List<ConsumerId__c> lstConsumerIDs = [Select id from ConsumerId__c where account__c =: oUnknownController.account.id];
        }
    }
    static testMethod void testConsumerTaxIDForAgent()
    {
        Test.setCurrentPage(Page.HUMUnknownMemberAccountEdit);
        
        List<HUMCipherCloudSettings__c> lstApplicationName = new List<HUMCipherCloudSettings__c> ();
        lstApplicationName.add(new HUMCipherCloudSettings__c(Name = 'APPLICATION_NAME',  Value__c = 'CRMR1'));
        insert lstApplicationName;
        System.assertNotEquals(lstApplicationName, NULL, 'Value not matched');
        
        //Load Constants from Custom Setting
        HUMTestDataHelperTest.getHUMConstantsData();
        
        //Set the Type of Unknonw Controller as Agent
        ApexPages.currentPage().getParameters().put('type','A');
        ApexPages.currentPage().getParameters().put('aId','12345');
        ApexPages.currentPage().getParameters().put('State','MI');
        ApexPages.currentPage().getParameters().put('fName','NameFirst$#');
        ApexPages.currentPage().getParameters().put('lName','LastName');
        ApexPages.currentPage().getParameters().put('idType','HumanaId');
        
        Account oAcc = new Account(Name = 'Test'); 
        oAcc.FirstName = 'FirstName123#';
        oAcc.LastName = 'LastName';
        oAcc.BirthDate__c = '09/29/2014';
        oAcc.PersonHomePhone = '(900) 072-2208';
        oAcc.PersonMailingPostalCode = '12345';
        oAcc.NPI_ID__c ='123456';
        oAcc.billingpostalcode = '12345';
        System.assertNotEquals(oAcc, NULL, 'Value not matched');
        ApexPages.StandardController oSC = new ApexPages.StandardController(oAcc);
        System.assertNotEquals(oSC, NULL, 'Value not matched');
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        System.assertNotEquals(oCCSUser, NULL, 'Value not matched');
        
        HUMUnknownAccountController oUnknownController;
        
        system.runAs(oCCSUser) 
        {            
            Test.startTest();
           
            oUnknownController = new HUMUnknownAccountController(oSC);
            oUnknownController.save();
            
            
            Test.stopTest();
            
            List<ConsumerId__c> lstConsumerIDs = [Select id from ConsumerId__c where account__c =: oUnknownController.account.id];
        }
    }
}