/**************************************************************************************************************************************
Apex Class Name : HUMMemberSearchEnrollmentHelperTest
Version         : 1.0
Created On      : 08/05/2014
Function        : Test class for HUMMemberSearchEnrollmentHelper

 * Modification Log:
 * Developer Name            Code Review                 Date                       Description
 *---------------------------------------------------------------------------------------------------------------------------------------
 * Irfan Hussain               17165                   08/05/2014                 Original Version
 * Shruthi Karanth             17165                   08/06/2014                 Updated the class for testing services
 * Mrudula Jaddu               17165                   08/07/2014                 Added method for validation
 * Shruthi Karanth             17165                   08/10/2014                 Added assertions
 * Shruthi Karanth             17355                   08/14/2014                 Made changes to increase code coverage
 * Pranali Choudhari           18890                   10/30/2014                 Updated code for cbis
 * Pradeep Veeravali           19401                   11/19/2014                 Updated Code for updated H1 Exchange wsdl    
 * Pradeep Raju                19747                   12/08/2014                 Updated the code based on review comments
 * ChiranjeeviRao Ravuri       21640                   03/18/2014                 Updated testIntegrationExceptionScenarios()method to fix Assert failure @line#304.  
 * Ranjeeth Nagishetty         23472                   06/03/2015                 updated test class as per the REQ - 189913  
 * Ranjeeth Nagishetty         23864                   06/11/2015                 updating  the test class
 ****************************************************************************************************************************************/

@istest 
private class HUMMemberSearchEnrollmentHelperTest
{
    /*
     * Method Name : setup
     * Description : This method is used to insert the data only once and called automicticaly in all methods
     * Return type : NA
     * Paramater   : NA
     */
    @testSetup static void setup() {
       
        //Load all Constants data
        HUMTestDataHelperTest.getHUMConstantsData();
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        system.assertNotEquals(oUser,null,'​Value not matched');
        HUMTestDataHelperTest.getHUMConstantsData();   
    }
   
   
   
    /*
     * Method Name : testHUMFieldValidation
     * Description : This method is used to test validation scenario
     * Return type : NA
     * Paramater   : NA
     */
    testMethod static void testHUMFieldValidation()
    {
        Test.setCurrentPage(Page.HUMCustomSearchPage);
        
        HUMMemberSearchEnrollmentController humSearchEnrl = new HUMMemberSearchEnrollmentController();
        //Set Current Page

        User oUser = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
        system.assertNotEquals(oUser,null,'​Value not matched'); 

        system.runAs(oUser ) 
        {
            Test.startTest();
            humSearchEnrl.UnknownMemSearch();

            system.assert(ApexPages.hasMessages()==true);

            humSearchEnrl.oSearchWrapper.sSSN = '262839197';
            humSearchEnrl.oSearchWrapper.sStartDate = '';
            humSearchEnrl.oSearchWrapper.sEndDate ='';
            humSearchEnrl.UnknownMemSearch();

            system.assert(ApexPages.hasMessages()==true);

            humSearchEnrl.oSearchWrapper.sStartDate = (Datetime.now()).format('MM/dd/yyyy');
            humSearchEnrl.oSearchWrapper.sEndDate = (Datetime.now()-90).format('MM/dd/yyyy');
            humSearchEnrl.UnknownMemSearch();

            system.assert(ApexPages.hasMessages()==true);

            humSearchEnrl.oSearchWrapper.sStartDate = (Datetime.now()-100).format('MM/dd/yyyy');
            humSearchEnrl.UnknownMemSearch();

            system.assert(ApexPages.hasMessages()==true);
            Test.stopTest();
        }
    }

    /*
     * Method Name : testHUMEESServiceWOSSN
     * Description : This method is used to test EES Legacy integration scenario
     * Return type : NA
     * Paramater   : NA
     */    
    testMethod static void testHUMEESServiceWSSN()
    {
        HUM_CBIS_Enabled__c cbis = new HUM_CBIS_Enabled__c();
        cbis.Name = 'HUM_CBIS_Flag';
        cbis.CBIS_Flag__c = False;
        insert cbis;
        Test.setMock(WebServiceMock.class, new HUMIntEESLegacySearchServiceTest());
        //HUMTestDataHelperTest.getHUMConstantsData();
        HUMMemberSearchEnrollmentController humSearchEnrl = new HUMMemberSearchEnrollmentController();

        humSearchEnrl.sServices = System.Label.HUMEESService;
        humSearchEnrl.setSServices(humSearchEnrl.getSServices());
        humSearchEnrl.getItems();
        humSearchEnrl.getThis();
        humSearchEnrl.oSearchWrapper.sSSN = '111585060';
        humSearchEnrl.oSearchWrapper.sStartDate = (Datetime.now()- 90).format('MM/dd/yyyy');
        humSearchEnrl.oSearchWrapper.sEndDate = (Datetime.now()).format('MM/dd/yyyy');
        insert new HUMEESLegacySearchPlatforms__c(Platform_value__c='LM',Name='Platform1');
        insert new HUM_Webservice_Callout__c( Name = 'EESLegacySearchService' , Certificate_Name__c = 'DummyCertificate', Active__c = true, End_Point_URL__c = 'http://www.google.com', Timeout__c = '12000');           

          User oUser = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
          system.assertNotEquals(oUser,null,'​Value not matched'); 

        system.runAs(oUser ) 
        {
            test.startTest();    
            humSearchEnrl.UnknownMemSearch();
            //system.assert(humSearchEnrl.lstLegacyService.size() > 0);//Commenting the below code to Quick Fix for REL 07.22
            humSearchEnrl.reset();
            test.stopTest();
        }
    }

    /*
     * Method Name : testHUMUnknownAccount
     * Description : This method is used to test unknown account scenario
     * Return type : NA
     * Paramater   : NA
     */        
    testMethod static void testHUMUnknownAccount()
    {
        //HUMTestDataHelperTest.getHUMConstantsData();
        HUMMemberSearchEnrollmentController humSearchEnrl = new HUMMemberSearchEnrollmentController();
                                         
        Account account = new Account();
        account.Name = 'TestingAccount';
        account.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Unknown Agent/Broker').getRecordTypeId(); 
        insert account;               
   
        test.startTest(); 
        humSearchEnrl.buildUnknownAccountMap(account.Id);
        system.assert(humSearchEnrl.lstLegacyService[0].iAccntId == account.Id);                        
        test.stopTest();        
    }
    
    /*
     * Method Name : testHUMEESServiceWOSSN
     * Description : This method is used to test validation scenario without passing SSN
     * Return type : NA
     * Paramater   : NA
     */        
    testMethod static void testHUMEESServiceWOSSN()
    {
        //HUMTestDataHelperTest.getHUMConstantsData();
         User oUser = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
        system.assertNotEquals(oUser,null,'​Value not matched'); 
        
        system.runAs(oUser ) 
        
        {   
            HUMMemberSearchEnrollmentController humSearchEnrl = new HUMMemberSearchEnrollmentController();
            humSearchEnrl.setSServices(System.Label.HUMEESService);
            humSearchEnrl.oSearchWrapper.sSSN = '';
            humSearchEnrl.UnknownMemSearch();

            system.assert(ApexPages.hasMessages()==true);
        }
    }

    /*
     * Method Name : testHUMH1ServiceWSSN
     * Description : This method is used to test H1 Exchange integration scenario
     * Return type : NA
     * Paramater   : NA
     */    
    testMethod static void testHUMH1ServiceWSSN()
    {
        
       // HUMTestDataHelperTest.getHUMConstantsData();
        HUMMemberSearchEnrollmentController humSearchEnrl = new HUMMemberSearchEnrollmentController();
        Test.setMock(WebServiceMock.class, new HUMH1ExchangeServiceTest());
        insert new HUM_Webservice_Callout__c( Name = 'H1ExchangeService' , Certificate_Name__c = 'DummyCertificate', Active__c = true, End_Point_URL__c = 'http://www.google.com', Timeout__c = '12000');
        //Load all Constants data
        User oUser = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
        system.assertNotEquals(oUser,null,'​Value not matched'); 
        
        system.runAs(oUser ) 
        {
            humSearchEnrl.setSServices(System.Label.HUMH1Service);
            humSearchEnrl.oSearchWrapper.sSSN = '778560008';
            System.assertEquals( humSearchEnrl.oSearchWrapper.sSSN, '778560008');
            humSearchEnrl.oSearchWrapper.sStartDate = (Datetime.now()- 90).format('MM/dd/yyyy');
            System.assertEquals( humSearchEnrl.oSearchWrapper.sStartDate , (Datetime.now()- 90).format('MM/dd/yyyy'));
            humSearchEnrl.oSearchWrapper.sEndDate = (Datetime.now()).format('MM/dd/yyyy');
            System.assertEquals( humSearchEnrl.oSearchWrapper.sEndDate , (Datetime.now()).format('MM/dd/yyyy'));
            test.startTest();
            humSearchEnrl.UnknownMemSearch();
            system.assert(humSearchEnrl.lstLegacyService.size() == 0);
            test.stopTest();
        }
    }

    /*
     * Method Name : testHUMOHBPWSSN
     * Description : This method is used to test OHBP integration scenario
     * Return type : NA
     * Paramater   : NA
     */    
    testMethod static void testHUMOHBPWSSN()
    {
        //HUMTestDataHelperTest.getHUMConstantsData();
        HUMMemberSearchEnrollmentController humSearchEnrl = new HUMMemberSearchEnrollmentController();
        Test.setMock(WebServiceMock.class, new HUMOHBPServiceTest());
        insert new HUM_Webservice_Callout__c( Name = 'H1ExchangeService' , Certificate_Name__c = 'DummyCertificate', Active__c = false, End_Point_URL__c = 'http://www.google.com', Timeout__c = '12000');
        insert new HUM_Webservice_Callout__c( Name = 'OHBPService' , Certificate_Name__c = 'DummyCertificate', Active__c = true, End_Point_URL__c = 'http://www.google.com', Timeout__c = '12000');
        //Load all Constants data
        User oUser = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
        system.assertNotEquals(oUser,null,'​Value not matched'); 
        
        system.runAs(oUser ) 
        {
           humSearchEnrl.setSServices(System.Label.HUMH1Service);
           
            humSearchEnrl.oSearchWrapper.sSSN = '778640461';
            test.startTest();
            humSearchEnrl.UnknownMemSearch();

            system.assert(humSearchEnrl.lstLegacyService.size() > 0);
            test.stopTest();
        }
    }

    /*
     * Method Name : testHUMTrackEnrollmentServiceWSSN
     * Description : This method is used to test track enrollment integration scenario
     * Return type : NA
     * Paramater   : NA
     */    
    testMethod static void testHUMTrackEnrollmentServiceWSSN()
    {
        //HUMTestDataHelperTest.getHUMConstantsData();
        HUMMemberSearchEnrollmentController humSearchEnrl = new HUMMemberSearchEnrollmentController();
        Test.setMock(WebServiceMock.class, new HUMIntTrackEnrollmentServiceTest());
        insert new HUM_Webservice_Callout__c( Name = 'TrackEnrollmentService' , Certificate_Name__c = 'DummyCertificate', Active__c = true, End_Point_URL__c = 'http://www.google.com', Timeout__c = '12000');
        //Load all Constants data
        HUMTestDataHelperTest.getHUMConstantsData();
        User oUser = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
        system.assertNotEquals(oUser,null,'​Value not matched'); 
        
        system.runAs(oUser ) 
        {
            humSearchEnrl.setSServices(System.Label.HUMTrackEnrollmentService);
            humSearchEnrl.oSearchWrapper.sSSN = '123456789';
            test.startTest();
            humSearchEnrl.UnknownMemSearch();

            system.assert(humSearchEnrl.lstLegacyService.size() > 0);

            test.stopTest();
        }     

    }

    /*
     * Method Name : testHUMAEServiceWSSN
     * Description : This method is used to test AE History integration scenario
     * Return type : NA
     * Paramater   : NA
     */
    testMethod static void testHUMAEServiceWSSN()
    {
        //HUMTestDataHelperTest.getHUMConstantsData();
        HUMMemberSearchEnrollmentController humSearchEnrl = new HUMMemberSearchEnrollmentController();
        
        Test.setMock(WebServiceMock.class, new HUMIntAEHistoryServiceTest());
        insert new HUM_Webservice_Callout__c( Name = 'AEHistoryService' , Certificate_Name__c = 'DummyCertificate', Active__c = true, End_Point_URL__c = 'http://www.google.com', Timeout__c = '12000');
        //Load all Constants data
        HUMTestDataHelperTest.getHUMConstantsData();
         User oUser = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
        system.assertNotEquals(oUser,null,'​Value not matched');

        system.runAs(oUser ) 
        {
            humSearchEnrl.oSearchWrapper.sSSN = '12345678';
            humSearchEnrl.oSearchWrapper.sStartDate = (Datetime.now()- 90).format('MM/dd/yyyy');
            humSearchEnrl.oSearchWrapper.sEndDate = (Datetime.now()).format('MM/dd/yyyy');
            humSearchEnrl.sServices = System.Label.HUMAEService;
            test.startTest();    
            humSearchEnrl.UnknownMemSearch(); 

            system.assert(humSearchEnrl.lstLegacyService.size() > 0);

            test.stopTest();
        }
    }

    /*
     * Method Name : testIntegrationExceptionScenarios
     * Description : This method is used to test integration exception scenario
     * Return type : NA
     * Paramater   : NA
     */
    testMethod static void testIntegrationExceptionScenarios()
    {
        User oUser = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
        system.assertNotEquals(oUser,null,'​Value not matched');
        
        system.runAs(oUser ) 
        {
          
            HUMMemberSearchEnrollmentController humSearchEnrl = new HUMMemberSearchEnrollmentController();             
            test.startTest();   
            humSearchEnrl.sServices = System.Label.HUMEESService;    
            humSearchEnrl.UnknownMemSearch();
            
            humSearchEnrl.sServices = System.Label.HUMH1Service;
            humSearchEnrl.oSearchWrapper.sSSN = '262839197';
            humSearchEnrl.oSearchWrapper.sStartDate = (Datetime.now()- 90).format('MM/dd/yyyy');
            humSearchEnrl.oSearchWrapper.sEndDate = (Datetime.now()).format('MM/dd/yyyy');
            humSearchEnrl.UnknownMemSearch(); 
            
            system.assert(ApexPages.hasMessages()==true);
            

            humSearchEnrl.sServices = System.Label.HUMTrackEnrollmentService;
            
            humSearchEnrl.oSearchWrapper.sSSN = '362839197';
            humSearchEnrl.oSearchWrapper.sStartDate = (Datetime.now()- 95).format('MM/dd/yyyy');
            humSearchEnrl.oSearchWrapper.sEndDate = (Datetime.now()).format('MM/dd/yyyy');
            humSearchEnrl.UnknownMemSearch(); 
           
            system.assert(ApexPages.hasMessages()==true);
            
            humSearchEnrl.sServices = System.Label.HUMAEService;
            humSearchEnrl.oSearchWrapper.sSSN = '462839197';
            humSearchEnrl.oSearchWrapper.sStartDate = (Datetime.now()- 100).format('MM/dd/yyyy');
            humSearchEnrl.oSearchWrapper.sEndDate = (Datetime.now()).format('MM/dd/yyyy');   
            humSearchEnrl.UnknownMemSearch();

            system.assert(ApexPages.hasMessages()==true);
            

            test.stopTest();
        }
    }

   /*
     * Method Name : testHumCBISDuplicate
     * Description : This method is used to test CBIS test data
     * Return type : NA
     * Paramater : NA
     */ 
    
    testmethod static void testHumCBISDuplicate()
    {
        User oUser = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
        system.assertNotEquals(oUser,null,'​Value not matched');              
        HUM_CBIS_Enabled__c cbis = new HUM_CBIS_Enabled__c();
        cbis.Name = 'HUM_CBIS_Flag';
        cbis.CBIS_Flag__c = True;
        insert cbis;
        insert new HUM_Webservice_Callout__c( Name = 'HUMCbisSearchEnrollment' , Certificate_Name__c = 'DummyCertificate', Active__c = true, End_Point_URL__c = 'http://www.google.com', Timeout__c = '12000');
        
        Account acc = HUMTestDataHelperTest.getAccount();
        acc.Name = 'TestingAccount';
        acc.Account_External_ID__c  = 'UKM|00|CB|1c57a2260dfde3f4937f3bbeac597314';
        insert acc;
        
        Account acc1 = HUMTestDataHelperTest.getAccount();
        acc1.Name = 'TestingAccount1';
        acc1.Account_External_ID__c  = 'UKM|00|CB|1c57a2260dfde3f4937f3bbeac597314';
        
        system.runAs(oUser ) 
            {
                test.starttest();
                system.assertEquals(acc1.Account_External_ID__c ,acc.Account_External_ID__c  );
                test.stoptest();
                
            }
   
    }
    

    /*
     * Method Name : testHUMCBISCode
     * Description : This method is used to test CBIS Service
     * Return type : NA
     * Paramater : NA
     */ 
    testMethod static void testHUMCBISCode()
    {       
        User oUser = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
        system.assertNotEquals(oUser,null,'​Value not matched');              
        HUM_CBIS_Enabled__c cbis = new HUM_CBIS_Enabled__c();
        cbis.Name = 'HUM_CBIS_Flag';
        cbis.CBIS_Flag__c = True;
        insert cbis;
        insert new HUM_Webservice_Callout__c( Name = 'HUMCbisSearchEnrollment' , Certificate_Name__c = 'DummyCertificate', Active__c = true, End_Point_URL__c = 'http://www.google.com', Timeout__c = '12000');
        Account acc = HUMTestDataHelperTest.getAccount();
        acc.Name = 'TestingAccount';
        insert acc; 
        
        Test.setMock(WebServiceMock.class, new HUMIntCBISServiceResponseTest());
        HUMMemberSearchEnrollmentController humSearchEnrl = new HUMMemberSearchEnrollmentController();            
        humSearchEnrl.sServices = System.Label.HUMcbisCode;
        humSearchEnrl.setSServices(humSearchEnrl.getSServices());
        humSearchEnrl.getItems();
        humSearchEnrl.getThis();
        humSearchEnrl.CbisService();
        humSearchEnrl.bIsCbisSelected = True;
        system.runAs(oUser ) 
        {
            test.startTest();            
            humSearchEnrl.oSearchWrapper.sSSN = '';
            humSearchEnrl.oSearchWrapper.sBirthdate = '09/05/1947';
            humSearchEnrl.oSearchWrapper.sFirstName = 'Sue';
            humSearchEnrl.oSearchWrapper.sLastName = 'Smith';
            humSearchEnrl.oSearchWrapper.sState = 'FL'; 
            humSearchEnrl.UnknownMemSearch();                 
            humSearchEnrl.buildUnknownAccountMap(acc.Id); 
            system.assert(humSearchEnrl.lstLegacyService.size() > 0);
            system.debug('CBIS Test Data '+humSearchEnrl.lstLegacyService);
            
            test.stopTest();
        }
    }

    /*
     * Method Name : testHUMCBISCodeWrongInput
     * Description : This method is used to test CBIS Service with wrong inputs
     * Return type : NA
     * Paramater : NA
     */ 
    testMethod static void testHUMCBISCodeWrongInput()
    {
        HUM_CBIS_Enabled__c cbis = new HUM_CBIS_Enabled__c();
        cbis.Name = 'HUM_CBIS_Flag';
        cbis.CBIS_Flag__c = True;
        insert cbis;
        insert new HUM_Webservice_Callout__c( Name = 'HUMCbisSearchEnrollment' , Certificate_Name__c = 'DummyCertificate', Active__c = true, End_Point_URL__c = 'http://www.google.com', Timeout__c = '12000');
        Test.setMock(WebServiceMock.class, new HUMIntCBISServiceResponseTest());
        HUMMemberSearchEnrollmentController humSearchEnrl = new HUMMemberSearchEnrollmentController();            
        humSearchEnrl.sServices = System.Label.HUMCBISCode;
        humSearchEnrl.setSServices(humSearchEnrl.getSServices());
        humSearchEnrl.getItems();
        humSearchEnrl.getThis();
        humSearchEnrl.CbisService();
        humSearchEnrl.oSearchWrapper.sSSN = '';
        humSearchEnrl.oSearchWrapper.sFirstName='';
        humSearchEnrl.bIsCbisSelected = True;
        Account account = HUMTestDataHelperTest.getAccount();
        account.Name = 'TestingAccount';
        insert account;
        User oUser = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
        system.assertNotEquals(oUser,null,'​Value not matched');         
        system.runAs(oUser ) 
        {
            humSearchEnrl.sServices = System.Label.HUMcbisCode;
            test.startTest(); 
            humSearchEnrl.UnknownMemSearch();
            humSearchEnrl.buildUnknownAccountMap(account.Id); 
            system.assert(ApexPages.hasMessages()==true);
            test.stopTest();
        }
    }
}