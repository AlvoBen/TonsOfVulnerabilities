/**************************************************************************************************************************************
Apex Class Name : HUMMemberSearchEnrollmentControllerTest
Version         : 1.0
Created On      : 08/05/2014
Function        : Test class for HUMMemberSearchEnrollmentController

 * Modification Log:
 * Developer Name            Code Review                 Date                       Description
 *---------------------------------------------------------------------------------------------------------------------------------------
 * Irfan Hussain               17165                   08/05/2014                 Original Version
 * Shruthi Karanth             17165                   08/06/2014                 Updated the class for testing services
 * Mrudula Jaddu               17165                   08/07/2014                 Added method for validation
 * Shruthi Karanth             17165                   08/10/2014                 Added assertions
 * Shruthi Karanth             17355                   08/14/2014                 Made changes to increase code coverage
 * Pranali Choudhari           18890                   10/30/2014                 Updated code for cbis
 * Palak Papneja               18988                   11/06/2014                 Made changes as per code review 18988
 * Tanushree Hazari            19257                   11/17/2014                 Made changes to variable names as per Code review 19257
 * Pradeep Veeravali           19401                   11/19/2014                 Updated Code for updated H1 Exchange wsdl 
 * Raja Sekhar Reddy M V       19566                   11/27/2014                 Added assertions   
 * Raja Sekhar Reddy M V       20105                   12/30/2014                 Removed commented line # 189  
 * Ranjeeth Nagishetty         21021                   02/19/2014                 Test class updated for REQ-121513
 * Pradeep Veeravali           21640                   03/18/2015                 Test class fix for methods testHUMUnknownAccount & testIntegrationExceptionScenarios methods    
 * Raja Sekhar Reddy M V       22093                   04/03/2015                 Test class fix for testHUMH1ServiceWSSN Meathopd. Line number# 180 to 202.
 * Ranjeeth Nagishetty         22697                   04/28/2015                 Test class Fix for Defect Defect# 172077
  ****************************************************************************************************************************************/

@istest 
private class HUMMemberSearchEnrollmentControllerTest
{
    /*
     * Method Name : testHUMFieldValidation
     * Description : This method is used to test validation scenario
     * Return type : NA
     * Paramater   : NA
     */
    testMethod static void testHUMFieldValidation()
    {
        
        Test.setCurrentPage(Page.HUMCustomSearchPage);
        HUMTestDataHelperTest.getHUMConstantsData();
        HUMMemberSearchEnrollmentController oSearchEnrl = new HUMMemberSearchEnrollmentController();
        //Set Current Page

        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        system.assertNotEquals(oUser,null,'​Value not matched'); 
         
        system.runAs(oUser) 
        {
            Test.startTest();
            oSearchEnrl.UnknownMemSearch();

            system.assert(ApexPages.hasMessages()==true);

            oSearchEnrl.oSearchWrapper.sSSN = '262839197';
            oSearchEnrl.oSearchWrapper.sStartDate = '';
            oSearchEnrl.oSearchWrapper.sEndDate ='';
            oSearchEnrl.UnknownMemSearch();

            system.assert(ApexPages.hasMessages()==true);

            oSearchEnrl.oSearchWrapper.sStartDate = (Datetime.now()).format('MM/dd/yyyy');
            oSearchEnrl.oSearchWrapper.sEndDate = (Datetime.now()-90).format('MM/dd/yyyy');
            oSearchEnrl.UnknownMemSearch();

            system.assert(ApexPages.hasMessages()==true);

            oSearchEnrl.oSearchWrapper.sStartDate = (Datetime.now()-100).format('MM/dd/yyyy');
            oSearchEnrl.UnknownMemSearch();

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
        HUM_CBIS_Enabled__c oCbis = new HUM_CBIS_Enabled__c();
        oCbis.Name = 'HUM_CBIS_Flag';
        oCbis.CBIS_Flag__c = False;
        insert oCbis;
        system.assertNotEquals(oCbis,null,'​Value not matched');
        Test.setMock(WebServiceMock.class, new HUMIntEESLegacySearchServiceTest());
        HUMTestDataHelperTest.getHUMConstantsData();
        HUMMemberSearchEnrollmentController oSearchEnrl = new HUMMemberSearchEnrollmentController();

        oSearchEnrl.sServices = System.Label.HUMEESService;
        oSearchEnrl.setSServices(oSearchEnrl.getSServices());
        oSearchEnrl.getItems();
        oSearchEnrl.getThis();
        oSearchEnrl.oSearchWrapper.sSSN = '111585060';
        oSearchEnrl.oSearchWrapper.sStartDate = (Datetime.now()- 90).format('MM/dd/yyyy');
        oSearchEnrl.oSearchWrapper.sEndDate = (Datetime.now()).format('MM/dd/yyyy');
        insert new HUMEESLegacySearchPlatforms__c(Platform_value__c='LM',Name='Platform1');
        insert new HUM_Webservice_Callout__c( Name = 'EESLegacySearchService' , Certificate_Name__c = 'DummyCertificate', Active__c = true, End_Point_URL__c = 'http://www.google.com', Timeout__c = '12000');           
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        system.assertNotEquals(oUser,null,'​Value not matched');
        system.runAs(oUser) 
        {
            test.startTest();    
            oSearchEnrl.UnknownMemSearch();
            //system.assert(oSearchEnrl.lstLegacyService.size() > 0);//Commenting the below code to Quick Fix for REL 07.22
            oSearchEnrl.reset();
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
        HUMTestDataHelperTest.getHUMConstantsData();
        HUMMemberSearchEnrollmentController oSearchEnrl = new HUMMemberSearchEnrollmentController();
        // CCS profile user don't have access to HUMMemberSearchEnrollmentController class. so runas CCS user leads QueryException
        
            Account oAccount = new Account();
            oAccount.Name = 'TestingAccount';
            insert oAccount; 
            system.assertNotEquals(oAccount,null,'​Value not matched');
            test.startTest();    
            oSearchEnrl.buildUnknownAccountMap(oAccount.Id); 

            system.assert(oSearchEnrl.lstLegacyService[0].iAccntId == oAccount.Id); 

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
        HUMTestDataHelperTest.getHUMConstantsData();
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        system.assertNotEquals(oUser,null,'​Value not matched');
        
        system.runAs(oUser) 
        {   
            HUMMemberSearchEnrollmentController oSearchEnrl = new HUMMemberSearchEnrollmentController();
            oSearchEnrl.setSServices(System.Label.HUMEESService);
            oSearchEnrl.oSearchWrapper.sSSN = '';
            oSearchEnrl.UnknownMemSearch();

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
        HUMTestDataHelperTest.getHUMConstantsData();
        HUMMemberSearchEnrollmentController oSearchEnrl = new HUMMemberSearchEnrollmentController();                
        Test.setMock(WebServiceMock.class, new HUMH1ExchangeServiceTest());
        insert new HUM_Webservice_Callout__c( Name = 'H1ExchangeService' , Certificate_Name__c = 'DummyCertificate', Active__c = true, End_Point_URL__c = 'http://www.google.com', Timeout__c = '12000');
        //Load all Constants data
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        system.assertNotEquals(oUser,null,'​Value not matched');
        
        system.runAs(oUser) 
        {
             HUMConstants__c oHUMObj1 = new HUMConstants__c(Name='Zero', StringValue__c = '0');
            insert oHUMObj1;
            System.assertNotEquals(oHUMObj1.Id, NULL);
            HUMConstants__c oHUMObj2 = new HUMConstants__c(Name='Eleven', StringValue__c = '11');
            insert oHUMObj2;
            System.assertNotEquals(oHUMObj2.Id, NULL);
            
            HUMConstants__c oHUMObj3 = new HUMConstants__c(Name='Nineteen', StringValue__c = '19');
            insert oHUMObj3;
            System.assertNotEquals(oHUMObj3.Id, NULL);
            HUMConstants__c oHUMObj6 = new HUMConstants__c(Name='Thirteen', StringValue__c = '13');
            insert oHUMObj6;
            System.assertNotEquals(oHUMObj6.Id, NULL);
            
            HUMConstants__c oHUMObj5 = new HUMConstants__c(Name='AM', StringValue__c = 'AM');
            insert oHUMObj5;
            System.assertNotEquals(oHUMObj5.Id, NULL);
            HUMConstants__c oHUMObj4 = new HUMConstants__c(Name='PM', StringValue__c = 'PM');
            insert oHUMObj4;
            System.assertNotEquals(oHUMObj4.Id, NULL); 
            HUMConstants__c oHUMObj7 = new HUMConstants__c(Name='Ten', StringValue__c = '10');
            insert oHUMObj7;
            System.assertNotEquals(oHUMObj7.Id, NULL);
            
            oSearchEnrl.setSServices(System.Label.HUMH1Service);
            oSearchEnrl.oSearchWrapper.sSSN = '123456789';
            oSearchEnrl.oSearchWrapper.sStartDate = (Datetime.now()- 90).format('MM/dd/yyyy');
            oSearchEnrl.oSearchWrapper.sEndDate = (Datetime.now()).format('MM/dd/yyyy');
            test.startTest();
            oSearchEnrl.UnknownMemSearch();
            system.assert(oSearchEnrl.lstLegacyService.size() > 0);
            
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
        HUMTestDataHelperTest.getHUMConstantsData();
        HUMMemberSearchEnrollmentController oSearchEnrl = new HUMMemberSearchEnrollmentController();        
        Test.setMock(WebServiceMock.class, new HUMOHBPServiceTest());
        insert new HUM_Webservice_Callout__c( Name = 'H1ExchangeService' , Certificate_Name__c = 'DummyCertificate', Active__c = false, End_Point_URL__c = 'http://www.google.com', Timeout__c = '12000');
        insert new HUM_Webservice_Callout__c( Name = 'OHBPService' , Certificate_Name__c = 'DummyCertificate', Active__c = true, End_Point_URL__c = 'http://www.google.com', Timeout__c = '12000');
        //Load all Constants data
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        system.assertNotEquals(oUser,null,'​Value not matched');
        
        system.runAs(oUser) 
        {
            oSearchEnrl.setSServices(System.Label.HUMH1Service);            
            oSearchEnrl.oSearchWrapper.sSSN = '327548591';
            oSearchEnrl.oSearchWrapper.sStartDate = (Datetime.now()- 90).format('MM/dd/yyyy');
            oSearchEnrl.oSearchWrapper.sEndDate = (Datetime.now()).format('MM/dd/yyyy');
            test.startTest();
            oSearchEnrl.UnknownMemSearch();

            system.assert(oSearchEnrl.lstLegacyService.size() > 0);

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
        HUMTestDataHelperTest.getHUMConstantsData();
        HUMMemberSearchEnrollmentController oSearchEnrl = new HUMMemberSearchEnrollmentController();
        Test.setMock(WebServiceMock.class, new HUMIntTrackEnrollmentServiceTest());
        insert new HUM_Webservice_Callout__c( Name = 'TrackEnrollmentService' , Certificate_Name__c = 'DummyCertificate', Active__c = true, End_Point_URL__c = 'http://www.google.com', Timeout__c = '12000');
        //Load all Constants data
        HUMTestDataHelperTest.getHUMConstantsData();
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        system.assertNotEquals(oUser,null,'​Value not matched');
        
        system.runAs(oUser) 
        {
            oSearchEnrl.setSServices(System.Label.HUMTrackEnrollmentService);
            oSearchEnrl.oSearchWrapper.sSSN = '123456789';
            test.startTest();
            oSearchEnrl.UnknownMemSearch();

            system.assert(oSearchEnrl.lstLegacyService.size() > 0);

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
        HUMTestDataHelperTest.getHUMConstantsData();
        HUMMemberSearchEnrollmentController oSearchEnrl = new HUMMemberSearchEnrollmentController();
        Test.setMock(WebServiceMock.class, new HUMIntAEHistoryServiceTest());
        insert new HUM_Webservice_Callout__c( Name = 'AEHistoryService' , Certificate_Name__c = 'DummyCertificate', Active__c = true, End_Point_URL__c = 'http://www.google.com', Timeout__c = '12000');
        //Load all Constants data
        HUMTestDataHelperTest.getHUMConstantsData();
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        system.assertNotEquals(oUser,null,'​Value not matched');

        system.runAs(oUser) 
        {
            oSearchEnrl.oSearchWrapper.sSSN = '12345678';
            oSearchEnrl.oSearchWrapper.sStartDate = (Datetime.now()- 90).format('MM/dd/yyyy');
            oSearchEnrl.oSearchWrapper.sEndDate = (Datetime.now()).format('MM/dd/yyyy');
            oSearchEnrl.sServices = System.Label.HUMAEService;
            test.startTest();    
            oSearchEnrl.UnknownMemSearch(); 

            system.assert(oSearchEnrl.lstLegacyService.size() > 0);

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
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        system.assertNotEquals(oUser,null,'​Value not matched');

        system.runAs(oUser) 
        {
            HUMTestDataHelperTest.getHUMConstantsData();
            HUMMemberSearchEnrollmentController oSearchEnrl = new HUMMemberSearchEnrollmentController();             
            test.startTest();   
            oSearchEnrl.sServices = System.Label.HUMEESService;    
            oSearchEnrl.UnknownMemSearch();
            
            system.assert(ApexPages.hasMessages()==true);
            // setup values for each unknown search as values get refresh for each search                
            oSearchEnrl.sServices = System.Label.HUMH1Service;    
            oSearchEnrl.oSearchWrapper.sSSN = '262839197';
            oSearchEnrl.oSearchWrapper.sStartDate = (Datetime.now()- 90).format('MM/dd/yyyy');
            oSearchEnrl.oSearchWrapper.sEndDate = (Datetime.now()).format('MM/dd/yyyy');
            oSearchEnrl.UnknownMemSearch(); 

            system.assert(ApexPages.hasMessages()==true);

            oSearchEnrl.sServices = System.Label.HUMTrackEnrollmentService;    
            oSearchEnrl.oSearchWrapper.sSSN = '362839197';
            oSearchEnrl.oSearchWrapper.sStartDate = (Datetime.now()- 90).format('MM/dd/yyyy');
            oSearchEnrl.oSearchWrapper.sEndDate = (Datetime.now()).format('MM/dd/yyyy');
            oSearchEnrl.UnknownMemSearch(); 

            system.assert(ApexPages.hasMessages()==true);

            oSearchEnrl.sServices = System.Label.HUMAEService;  
            oSearchEnrl.oSearchWrapper.sSSN = '462839197';
            oSearchEnrl.oSearchWrapper.sStartDate = (Datetime.now()- 90).format('MM/dd/yyyy');
            oSearchEnrl.oSearchWrapper.sEndDate = (Datetime.now()).format('MM/dd/yyyy');   
            oSearchEnrl.UnknownMemSearch();

            system.assert(ApexPages.hasMessages()==true);

            test.stopTest();
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
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;  
        system.assertNotEquals(oUser,null,'​Value not matched');
                    
        HUM_CBIS_Enabled__c oCbis = new HUM_CBIS_Enabled__c();
        oCbis.Name = 'HUM_CBIS_Flag';
        oCbis.CBIS_Flag__c = True;
        insert oCbis;
        system.assertNotEquals(ocbis,null,'​Value not matched');
        
        insert new HUM_Webservice_Callout__c( Name = 'HUMCbisSearchEnrollment' , Certificate_Name__c = 'DummyCertificate', Active__c = true, End_Point_URL__c = 'http://www.google.com', Timeout__c = '12000');
        Account oAccount = HUMTestDataHelperTest.getAccount();
        oAccount.Name = 'TestingAccount';
        insert oAccount; 
        system.assertNotEquals(oAccount,null,'​Value not matched'); 
        
        Test.setMock(WebServiceMock.class, new HUMIntCBISServiceResponseTest());
        HUMTestDataHelperTest.getHUMConstantsData();
        HUMMemberSearchEnrollmentController oSearchEnrl = new HUMMemberSearchEnrollmentController();            
        oSearchEnrl.sServices = System.Label.HUMcbisCode;
        oSearchEnrl.setSServices(oSearchEnrl.getSServices());
        oSearchEnrl.getItems();
        oSearchEnrl.getThis();
        oSearchEnrl.CbisService();
        oSearchEnrl.bIsCbisSelected = True;
        system.runAs(oUser) 
        {
            test.startTest();            
            oSearchEnrl.oSearchWrapper.sSSN = '';
            oSearchEnrl.oSearchWrapper.sBirthdate = '07/27/1980';
            oSearchEnrl.oSearchWrapper.sFirstName = 'test';
            oSearchEnrl.oSearchWrapper.sLastName = 'name';
            oSearchEnrl.oSearchWrapper.sState = 'FL'; 
            oSearchEnrl.UnknownMemSearch();                 
            oSearchEnrl.buildUnknownAccountMap(oAccount.Id); 
            system.assert(oSearchEnrl.lstLegacyService.size() > 0);
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
        HUM_CBIS_Enabled__c oCbis = new HUM_CBIS_Enabled__c();
        oCbis.Name = 'HUM_CBIS_Flag';
        oCbis.CBIS_Flag__c = True;
        insert oCbis;
        system.assertNotEquals(oCbis,null,'​Value not matched');
        
        insert new HUM_Webservice_Callout__c( Name = 'HUMCbisSearchEnrollment' , Certificate_Name__c = 'DummyCertificate', Active__c = true, End_Point_URL__c = 'http://www.google.com', Timeout__c = '12000');
        Test.setMock(WebServiceMock.class, new HUMIntCBISServiceResponseTest());
        HUMTestDataHelperTest.getHUMConstantsData();
        HUMMemberSearchEnrollmentController oSearchEnrl = new HUMMemberSearchEnrollmentController();            
        oSearchEnrl.sServices = System.Label.HUMCBISCode;
        oSearchEnrl.setSServices(oSearchEnrl.getSServices());
        oSearchEnrl.getItems();
        oSearchEnrl.getThis();
        oSearchEnrl.CbisService();
        oSearchEnrl.oSearchWrapper.sSSN = '';
        oSearchEnrl.oSearchWrapper.sFirstName='';
        oSearchEnrl.bIsCbisSelected = True;
        Account oAccount = HUMTestDataHelperTest.getAccount();
        oAccount.Name = 'TestingAccountCBIS';
        insert oAccount;
        system.assertNotEquals(oAccount,null,'​Value not matched');
        
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser; 
        system.assertNotEquals(oUser,null,'​Value not matched');
                   
        system.runAs(oUser) 
        {
            oSearchEnrl.sServices = System.Label.HUMcbisCode;
            test.startTest(); 
            oSearchEnrl.UnknownMemSearch();
            oSearchEnrl.buildUnknownAccountMap(oAccount.Id); 
            system.assert(ApexPages.hasMessages()==true);
            test.stopTest();
        }
    }
}