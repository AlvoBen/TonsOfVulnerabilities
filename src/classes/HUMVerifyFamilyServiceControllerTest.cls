/***********************************************************************************************************
Apex Class Name  : HUMVerifyFamilyServiceControllerTest
Version          : 1.0
Created Date     : Aug 4 2014
Function         : Test class for HUMVerifyFamilyServiceController
************************************************************************************************************

Modification Log:

* Developer Name                Code Review           Date                        Description
*-----------------------------------------------------------------------------------------------------------
* Manish Kumar                  17076                 08/04/2014                   Original Version
* Asha Deepika Koyyana          17674                 09/09/2014                   Defect fix for defect #152088
* Asha Deepika Koyyana          18218                 09/27/2014                   Defect fix defect #156048 
* Asha Deepika Koyyana          18289                 10/01/2014                   Defect fix defect #155408 
* Asha Deepika Koyyana          18355                 10/06/2014                   To meet requirement #2411 added method testVerifyFamily 
* Asha Deepika Koyyana          18447                 10/10/2014                   To fix CC comment #18447
* Manish Kumar                  19728                 12/05/2014                   Fixed production error
* Asha Deepika Koyyana          21123                 02/24/2015                   As per REQ - 148668,updated the code to use HUMVerifyFamilyCOBService
* Harika Devi Kalidindi         112000                07/20/2017                   REQ - 323435 :Added Policy_Group_Number__c field value while inserting policy
* Ajay Chakradhar          			                  07/29/2020          	       REQ - 1065536 Replace the Person Service on Verify demographics from SOAP to REST
************************************************************************************************************/
@isTest
private class HUMVerifyFamilyServiceControllerTest
{
    /*
    * Method Name   :    testGetDependents
    * Description   :    This method is used to insert the inputs required to test GetDependents
    * Return Type   :    void
    * Parameters    :    NA
    */
    static testMethod void testGetDependents()
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertNotEquals(oCCSUser, NULL, 'Value not matched');
        
        System.runAs(oCCSUser)
        {
            //This causes a fake response to be generated
            Test.setMock(WebServiceMock.class, new HUMVerifyFamilyCOBServiceMockTest());
                   
            Account oAccount = new Account();
            oAccount.Name ='Test Account';
            oAccount.Enterprise_ID__c = '5328004527022';
            insert oAccount;
            System.assertNotEquals(oAccount, NULL, 'Account is not inserted');
            
            Account oAccount1 = new Account();
            oAccount1.Name ='Subscriber Account';
            oAccount1.Enterprise_ID__c = '5328004527023';
            insert oAccount1;
            System.assertNotEquals(oAccount1, NULL, 'Account is not inserted');
            
            Policy__c oPolicy = new Policy__c(Group_Name__c = oAccount.id, Name = 'TestPolicy',Policy_Group_Number__c='11111');
            insert oPolicy;
            System.assertNotEquals(oPolicy , NULL, 'Policy is not inserted');
            
            Policy_Member__c oPolicyMem = new Policy_Member__c(Policy__c = oPolicy.Id, Name = 'TestPolictMem', Member__c = oAccount.Id);
            insert oPolicyMem;
            System.assertNotEquals(oPolicyMem , NULL, 'Policy Member is not inserted');
            
            Policy_Member__c oPolicyMem1 = new Policy_Member__c(Policy__c = oPolicy.Id, Name = 'TestPolictMem1', Member__r = oAccount1,Member__c = oAccount.Id, Subscriber__c = oPolicyMem.Id  );
            insert oPolicyMem1;
            System.assertNotEquals(oPolicyMem1 , NULL, 'Policy Member is not inserted');
            
            ApexPages.currentPage().getParameters().put('SaccountId', oAccount.id);  
            ApexPages.currentPage().getParameters().put('ids', oPolicyMem.id + ',');
            
            test.startTest();
            HUMVerifyFamilyServiceController oVerifyFamily = new HUMVerifyFamilyServiceController();
            HUMVerifyFamilyServiceController_R1 oVerifyFamily1 = new HUMVerifyFamilyServiceController_R1();
            System.assertNotEquals(oVerifyFamily  , NULL, 'Object instantiated');
            test.stopTest(); 
        }
    }
    
    /*
    * Method Name   :    testGetLastVerifyDates
    * Description   :    This method is used to insert the inputs required to test GetLastVerifyDates
    * Return Type   :    void
    * Parameters    :    NA
    */
    static testMethod void testGetLastVerifyDates()
    { 
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertNotEquals(oCCSUser, NULL, 'Value not matched');
        
        System.runAs(oCCSUser)
        {
            Test.setMock(WebServiceMock.class, new HUMVerifyFamilyCOBServiceMockTest());
            
            Account oAccount = new Account();
            oAccount.Name ='Test Account';
            oAccount.Enterprise_ID__c = '5328004527022';
            insert oAccount;
            System.assertNotEquals(oAccount, NULL, 'Account is not inserted');
            
            Policy__c oPolicy = new Policy__c(Group_Name__c = oAccount.id, Name = 'TestPolicy',Policy_Group_Number__c='11111');
            insert oPolicy;
            System.assertNotEquals(oPolicy , NULL, 'Policy is not inserted');
            
            Policy_Member__c oPolicyMem = new Policy_Member__c(Policy__c = oPolicy.Id, Name = 'TestPolictMem', Member__c = oAccount.Id);
            insert oPolicyMem;
            System.assertNotEquals(oPolicyMem , NULL, 'Policy Member is not inserted');
            
            HUM_Webservice_Callout__c oVfs = new HUM_Webservice_Callout__c(Name = 'AddressVerification', Certificate_Name__c = 'TestCertificate', Timeout__c = '3000', End_Point_URL__c = 'SampleURL');
            insert oVfs ;
            System.assertNotEquals(oVfs , NULL, 'WebService Certification name is not inserted');
            
            HUM_CBIS_Enabled__c oHUMCbisFlag = new HUM_CBIS_Enabled__c(Name = 'HUM_CBIS_Flag', CBIS_Flag__c = true );
            insert oHUMCbisFlag ;
            System.assertNotEquals(oHUMCbisFlag , NULL, 'CBIS flag is not inserted');
            ApexPages.currentPage().getParameters().put('accountId', oAccount.id);
            ApexPages.currentPage().getParameters().put('ids', oPolicyMem.id + ',');
            
            HUMVerifyFamilyServiceController oVerifyFamily = new HUMVerifyFamilyServiceController();
            HUMVerifyFamilyServiceController_R1 oVerifyFamily1 = new HUMVerifyFamilyServiceController_R1();
            System.assertNotEquals(oVerifyFamily  , NULL, 'Object instantiated');
            
            test.startTest();
            oVerifyFamily.getLastVerifyDates();
            oVerifyFamily1.getLastVerifyDates();
            oVerifyFamily1.processGetLastVerifyDates();
            System.assert(true,oVerifyFamily.getLastVerifyDates()!= NULL);  
            test.stopTest();  
        }      
    }
    
    /*
    * Method Name   :    testVerifyDemographics
    * Description   :    This method is used to insert the inputs required to test Updatedemographics
    * Return Type   :    void
    * Parameters    :    NA
    */
    static testMethod void testVerifyDemographics()
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertNotEquals(oCCSUser, NULL, 'Value not matched');
        
        System.runAs(oCCSUser) 
        {
            Test.setMock(WebServiceMock.class, new HUMVerifyFamilyCOBServiceMockTest());
            
            Account oAccount1 = new Account();
            oAccount1.Name ='Test Account';
            oAccount1.Enterprise_ID__c = '5328004527022';
            insert oAccount1;
            System.assertNotEquals(oAccount1, NULL, 'Account is not inserted');
            
            HUM_Webservice_Callout__c oVfs = new HUM_Webservice_Callout__c(Name = 'AddressVerification', Certificate_Name__c = 'TestCertificate', Timeout__c = '3000', End_Point_URL__c = 'SampleURL');
            insert oVfs;
            System.assertNotEquals(oVfs , NULL, 'WebService Certification name is not inserted');
            
            ApexPages.currentPage().getParameters().put('accountId', oAccount1.id);
            
            HUMVerifyFamilyServiceController oVerifyFamily = new HUMVerifyFamilyServiceController();
            HUMVerifyFamilyServiceController_R1 oVerifyFamily1 = new HUMVerifyFamilyServiceController_R1();
            System.assertNotEquals(oVerifyFamily, NULL, 'Object instantiated');
            
            HUM_CBIS_Enabled__c oHUMCbisFlag = new HUM_CBIS_Enabled__c(Name = 'HUM_CBIS_Flag', CBIS_Flag__c = true );
            insert oHUMCbisFlag ;
            System.assertNotEquals(oHUMCbisFlag , NULL, 'CBIS flag is not inserted');
            
            test.startTest();
            oVerifyFamily.verifyDemographics();
            oVerifyFamily1.verifyDemographics();
            oVerifyFamily1.processverifyDemographics();
            System.assert(true,oVerifyFamily.verifyDemographics()!= NULL);
            test.stopTest();
        }
    } 
    
    /*
    * Method Name   :    testVerifyFamily
    * Description   :    This method is used to insert the inputs required to test HUMVerifyFamilyServiceController class.
    * Return Type   :    void
    * Parameters    :    NA
    */    
    static testMethod void testVerifyFamily()
    {        
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertNotEquals(oCCSUser, NULL, 'Value not matched');
        
        System.runAs(oCCSUser) 
        {
            Test.setMock(WebServiceMock.class, new HUMVerifyFamilyCOBServiceMockTest());
            
            Account oAccount = new Account(Name ='Test Account',Source_Platform_Code__c ='CB');
            insert oAccount;
            System.assertNotEquals(oAccount, NULL, 'Account is not inserted');
             
            Account oAccountGroup = new Account(Name ='Test Group',Source_Platform_Code__c ='CB');
            insert oAccountGroup;
            System.assertNotEquals(oAccountGroup , NULL, 'Account is not inserted');
            
            Policy__c oPolicy = new Policy__c(name = 'Test Policy', Group_Name__c = oAccountGroup.Id,Policy_Group_Number__c='11111' );
            insert oPolicy;
            System.assertNotEquals(oPolicy , NULL, 'Policy is not inserted');
             
            Policy__c oPolicy1 = new Policy__c(name = 'Test Policy 1', Group_Name__c = oAccountGroup.Id,Policy_Group_Number__c='11111');
            insert oPolicy1; 
            System.assertNotEquals(oPolicy1, NULL, ' Policy is not inserted');
            
            Policy__c oPolicy2 = new Policy__c(name = 'Test Policy 2', Group_Name__c = oAccountGroup.Id,Policy_Group_Number__c='11111' );
            insert oPolicy2 ; 
            System.assertNotEquals(oPolicy2 , NULL, 'Policy is not inserted');
             
            Policy_Member__c oPolicyMem = new Policy_Member__c(name = 'Test Policy Mem', Policy__c = oPolicy.Id, Member__c = oAccount.id );
            insert oPolicyMem; 
            system.assertNotEquals(oPolicyMem,null,'Policy Member is not inserted');
             
            Policy_Member__c oPolicyMem1 = new Policy_Member__c(name = 'Test Policy Mem1',Policy__c = oPolicy.Id, Member__c =  oAccount.id );
            insert oPolicyMem1; 
            System.assertNotEquals(oPolicyMem1, NULL, 'Policy Member is not inserted');
                   
            Policy_Member__c oPolicyMem2 = new Policy_Member__c(name = 'Test Policy Mem2',Policy__c = oPolicy1.Id, Member__c =  oAccountGroup.id,Subscriber__c = oPolicyMem.id );
            insert oPolicyMem2;
            System.assertNotEquals(oPolicyMem2 , NULL, 'Policy Member is not inserted');
            
            Policy_Member__c oPolicyMem3 = new Policy_Member__c(name = 'Test Policy Mem3',Policy__c = oPolicy2.Id, Member__c =  oAccountGroup.id,Subscriber__c = oPolicyMem1.id );
            insert oPolicyMem3;       
            System.assertNotEquals(oPolicyMem3 , NULL, 'Policy Member is not inserted');
                   
            ApexPages.currentPage().getParameters().put('Id',oAccount.id);
             
            test.startTest();
            
            HUM_CBIS_Enabled__c oHUMCbisFlag = new HUM_CBIS_Enabled__c(Name = 'HUM_CBIS_Flag', CBIS_Flag__c = true );
            insert oHUMCbisFlag ;
            System.assertNotEquals(oHUMCbisFlag , NULL, 'CBIS flag is not inserted');
           
            HUMVerifyFamilyServiceController  oVFController = new HUMVerifyFamilyServiceController(new ApexPages.StandardController(oAccount));
            System.assertNotEquals(oVFController, NULL, 'Object instantiated');
            
            HUMVerifyFamilyServiceController_R1  oVFController1 = new HUMVerifyFamilyServiceController_R1(new ApexPages.StandardController(oAccount));
            System.assertNotEquals(oVFController1, NULL, 'Object instantiated');
             ApexPages.currentPage().getParameters().put('accountId',oAccount.id);  
             ApexPages.currentPage().getParameters().put('ids',oPolicy1.id+','+oPolicy2.id);
            
            HUMVerifyFamilyServiceController oVerifyFamily = new HUMVerifyFamilyServiceController();
            HUMVerifyFamilyServiceController_R1 oVerifyFamily1 = new HUMVerifyFamilyServiceController_R1();
            System.assertNotEquals(oVerifyFamily  , NULL, 'Object instantiated');
            test.stopTest();
        }
    }
    /*
    * Method Name   :    testGetLastVerifyDates
    * Description   :    This method is used to insert the inputs required to test GetLastVerifyDates
    * Return Type   :    void
    * Parameters    :    NA
    */
    static testMethod void testGetLastVerifyDatesRest()
    { 
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertNotEquals(oCCSUser, NULL, 'Value not matched');
        System.runAs(oCCSUser)
        {
            //Test.setMock(WebServiceMock.class, new HUMVerifyFamilyCOBServiceMockTest());
            Account oAccount = new Account();
            oAccount.Name ='Test Account';
            oAccount.Enterprise_ID__c = '1000065119';
            insert oAccount;
            System.assertNotEquals(oAccount, NULL, 'Account is not inserted');
            Policy__c oPolicy = new Policy__c(Group_Name__c = oAccount.id, Name = 'TestPolicy',Policy_Group_Number__c='11111');
            insert oPolicy;
            System.assertNotEquals(oPolicy , NULL, 'Policy is not inserted');
            Policy_Member__c oPolicyMem = new Policy_Member__c(Policy__c = oPolicy.Id, Name = 'TestPolictMem', Member__c = oAccount.Id);
            insert oPolicyMem;
            System.assertNotEquals(oPolicyMem , NULL, 'Policy Member is not inserted');
            HUM_Webservice_Callout__c calloutSettings = new HUM_Webservice_Callout__c();
            calloutSettings.Name = 'GetVerifyDemographics';
            calloutSettings.Active__c = True;
            calloutSettings.Enable_Service_Tester__c = True;
            calloutSettings.Certificate_Name__c = 'Humana_GuidanceCare_Pileus';
            calloutSettings.Content_Type__c = 'application/json';
            calloutSettings.Request_Type__c = 'Post';
            calloutSettings.Timeout__c = '12000';
            calloutSettings.End_Point_URL__c = 'https://dev-crmservicews.humana.com/XXX';
            insert calloutSettings;
            System.assertNotEquals(calloutSettings , NULL);
            ApexPages.currentPage().getParameters().put('accountId', oAccount.id);
            ApexPages.currentPage().getParameters().put('ids', oPolicyMem.id + ',');
            HUM_CBIS_Enabled__c oHUMCbisFlag = new HUM_CBIS_Enabled__c(Name = 'HUM_CBIS_Flag', CBIS_Flag__c = true );
            insert oHUMCbisFlag ;
            System.assertNotEquals(oHUMCbisFlag , NULL, 'CBIS flag is not inserted');
            HUMVerifyFamilyServiceController oVerifyFamily = new HUMVerifyFamilyServiceController();
            HUMVerifyFamilyServiceController_R1 oVerifyFamily1 = new HUMVerifyFamilyServiceController_R1();
            System.assertNotEquals(oVerifyFamily  , NULL, 'Object instantiated');
            test.startTest();
            oVerifyFamily.getLastVerifyDatesRest();
            System.assert(true,oVerifyFamily.getLastVerifyDatesRest()!= NULL);
            oVerifyFamily1.getLastVerifyDatesRest();
            HttpResponse response = new HttpResponse();
            response.setBody('{"ResponseHeader":{"RequestId": "GETreq1","SessionId": "GETses1","ResponseDuration": "12.155436","CoreApiHttpStatusCode": "200 OK"},"getverificationstatusResponse": {"demographics":{"lastVerifiedDate": "2020-05-16","source": "CRM","verifiedBy": "POLIMERA12345"}}}');
            Test.setContinuationResponse('label', response);
            oVerifyFamily.processResponse((VerifyDemographicGetResponse_DTO_HUM)System.JSON.deserialize(response.getbody(),VerifyDemographicGetResponse_DTO_HUM.class));
            oVerifyFamily.processGetVerifyDemographicsResponse();
            oVerifyFamily1.processResponse((VerifyDemographicGetResponse_DTO_HUM)System.JSON.deserialize(response.getbody(),VerifyDemographicGetResponse_DTO_HUM.class));
            oVerifyFamily1.processGetVerifyDemographicsResponse();
            test.stopTest();  
        }      
    }
    /*
    * Method Name   :    testVerifyDemographics
    * Description   :    This method is used to insert the inputs required to test Updatedemographics
    * Return Type   :    void
    * Parameters    :    NA
    */
    static testMethod void testVerifyDemographicsRest()
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertNotEquals(oCCSUser, NULL, 'Value not matched');
        System.runAs(oCCSUser) 
        {
            Account oAccount1 = new Account();
            oAccount1.Name ='Test Account';
            oAccount1.Enterprise_ID__c = '1000065119';
            insert oAccount1;
            System.assertNotEquals(oAccount1, NULL, 'Account is not inserted');
            Policy__c oPolicy = new Policy__c(Group_Name__c = oAccount1.id, Name = 'TestPolicy',Policy_Group_Number__c='11111');
            insert oPolicy;
            System.assertNotEquals(oPolicy , NULL, 'Policy is not inserted');
            Policy_Member__c oPolicyMem = new Policy_Member__c(Policy__c = oPolicy.Id, Name = 'TestPolictMem', Member__c = oAccount1.Id);
            insert oPolicyMem;
            System.assertNotEquals(oPolicyMem , NULL, 'Policy Member is not inserted');
            HUM_Webservice_Callout__c calloutSettings = new HUM_Webservice_Callout__c();
            calloutSettings.Name = 'UpdateVerifyDemographics';
            calloutSettings.Active__c = True;
            calloutSettings.Enable_Service_Tester__c = True;
            calloutSettings.Certificate_Name__c = 'Humana_GuidanceCare_Pileus';
            calloutSettings.Content_Type__c = 'application/json';
            calloutSettings.Request_Type__c = 'Post';
            calloutSettings.Timeout__c = '12000';
            calloutSettings.End_Point_URL__c = 'https://dev-crmservicews.humana.com/XXX';
            insert calloutSettings;
            System.assertNotEquals(calloutSettings , NULL);
            ApexPages.currentPage().getParameters().put('accountId', oAccount1.id);

            HUMVerifyFamilyDemoWrapperHelper oDispAccounts = new HUMVerifyFamilyDemoWrapperHelper(oAccount1, 'Brother');
            oDispAccounts.Account = oAccount1;
            oDispAccounts.bSelected = False;
            oDispAccounts.sRelation = 'Brother';
            
            List<HUMVerifyFamilyDemoWrapperHelper> lstDispAccounts = new List<HUMVerifyFamilyDemoWrapperHelper>();
            lstDispAccounts.add(oDispAccounts);

            HUMVerifyFamilyServiceController oVerifyFamily = new HUMVerifyFamilyServiceController();
            HUMVerifyFamilyServiceController_R1 oVerifyFamily1 = new HUMVerifyFamilyServiceController_R1();
            System.assertNotEquals(oVerifyFamily, NULL, 'Object instantiated');
            HUM_CBIS_Enabled__c oHUMCbisFlag = new HUM_CBIS_Enabled__c(Name = 'HUM_CBIS_Flag', CBIS_Flag__c = true );
            insert oHUMCbisFlag ;
            System.assertNotEquals(oHUMCbisFlag , NULL, 'CBIS flag is not inserted');
            test.startTest();
            oVerifyFamily.verifyFamilyDemographics();
            oVerifyFamily.buildRequestVerifyFamilyDemographics(lstDispAccounts);
            oVerifyFamily1.verifyDemographicsRest();
            System.assert(true,oVerifyFamily.verifyFamilyDemographics()!= NULL);
            HttpResponse response = new HttpResponse();
            response.setBody('{"ResponseHeader":{"RequestId": "PUTreq12","SessionId": "PUTses12","ResponseDuration": "177.884201","CoreApiHttpStatusCode": "200 OK"},"putverificationstatusResponse": {"Status": "success"}}');
            Test.setContinuationResponse('label', response);
            oVerifyFamily.processVerifyFamilyDemographicsResponse();
            oVerifyFamily.processDefaultVerifyResponse();
            oVerifyFamily1.processDefaultVerifyResponse();
            oVerifyFamily1.processUpdateVerifyDemographicsResponse();
            test.stopTest();
        }
    }
}