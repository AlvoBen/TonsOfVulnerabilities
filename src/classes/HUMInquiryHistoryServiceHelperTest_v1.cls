/*************************************************************************************************************************
Apex Class Name  : HUMInquiryHistoryServiceHelperTest_v1
Version          : 1.0
Created Date     : 8th Feb 2017
Function         : Test class to HUMInquiryHistoryServiceHelper_v1 class

* Modification Log :
* Modification ID     Developer                Code Review            Date                 Description
* ----------------------------------------------------------------------------------------------------------------------------------------
* 1.0                Pallavi R                                      08/02/2017           Original Version
* 1.1                Vijay Gurusamy                                 08/03/2018           REQ - 367015 - Added test methods for HP users.
*****************************************************************************************************************************************/
@isTest
private class HUMInquiryHistoryServiceHelperTest_v1
{
    /*
     * Method Name : prepareTestData
     * Description : Used to create wrapper class data
     * Return type : void
     * Paramater   : NA
     */
     @testSetup static void prepareTestData() 
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertEquals(oCCSUser.LastName,'test User1');
        
        System.runAs (oCCSUser) 
        {   
           Test.setMock(WebServiceMock.class, new HUMInqTempUriOrgAsyncServiceMockTest());
            HUMTestDataHelperTest.getHUMConstantsData();
            HUM_Webservice_Callout__c vfs = new HUM_Webservice_Callout__c(Name = 'RetrieveInquiry', Certificate_Name__c = 'TestCertificate', Timeout__c = '120000', End_Point_URL__c = 'https://int-crmservicews.humana.com/InquirySearchService.svc', Active__c=true);
            insert vfs;
            System.assertEquals(vfs.Name,'RetrieveInquiry');
            System.assertEquals(vfs.Certificate_Name__c,'TestCertificate');
            
            HUM_Webservice_Callout__c cstGCP = new HUM_Webservice_Callout__c(Name = 'HUMLaunchGCP', Certificate_Name__c = 'TestCertificate1', Timeout__c = '3000', End_Point_URL__c = 'http://test-gcp.humana.com/utilities/LaunchGCP.aspx', Active__c=true);
            insert cstGCP ;
            System.assertEquals(cstGCP.Name,'HUMLaunchGCP');
            System.assertEquals(cstGCP.Certificate_Name__c,'TestCertificate1');
            
            Account objAcc = HUMTestDataHelperTest.getAccount(); 
            objAcc.FirstName = 'Test';
            objAcc.LastName = 'Account';
            objAcc.Tenant_Id__c = '00';
            objAcc.Enterprise_ID__c = '1005577525';
            objAcc.General_Account__c = true;
            objAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();
            insert objAcc;
            System.assertEquals(objAcc.Tenant_Id__c,'00');
            
            Account oAcc = HUMTestDataHelperTest.getAccount(); 
            oAcc.Name = 'Test Group';
            oAcc.Enterprise_ID__c = '7420004174017';
            oAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUM_Group_Account_Record_Type).getRecordTypeId();
            insert oAcc;
            System.assertEquals(oAcc.Enterprise_ID__c,'7420004174017');

            
            Product2 oProd = new Product2(Name = 'Laptop X200',Segment_Type__c='MCR',ProductCode='MED', Major_LOB__c='MER');
            insert oProd;
            System.assertNotEquals(oProd.Id, NULL, 'Value not matched');
            
            Policy__c objpol = HUMTestDataHelperTest.getPolicy();
            objpol.Name='testPolicy';
            objpol.Group_Name__c = objAcc.Id;
            objpol.Major_LOB__c = 'MER';
            objpol.Source_Cust_Cov_Key__c = '123456789';
            objpol.Product__c = oProd.Id;
            objpol.Product__r = oProd;
            objpol.Platform__c = 'LV';
            insert objpol;
            system.assertEquals(objpol.Source_Cust_Cov_Key__c,'123456789');
            
            Policy_Member__c objPolMem = new Policy_Member__c();
            objPolMem.Name = 'testpolmem11';
            objPolMem.Relationship__c = 'Self';
            objPolMem.Subscriber__c = objPolMem.Id;
            objPolMem.Effective_Date__c=Date.today().addDays(-1).format();
            objPolMem.End_Date__c=Date.today().addDays(1).format();
            objPolMem.Policy_Member_External_ID__c='00|ODS|6925004434817|12345678|34567|2013-07-01';
            objPolMem.Member__c = objAcc.Id;
            objPolMem.Policy__c = objpol.Id;
            objPolMem.Policy__r = objpol;
            insert objPolMem;
            system.assertEquals(objPolMem.Relationship__c, 'Self');
        }

		User oCCSUser2 = HUMTestDataHelperTest.createTestUser('User2', 'Humana Pharmacy Specialist');
		insert oCCSUser2;
		system.assertEquals(oCCSUser2.LastName, 'test User2');

		List<PermissionSet> hpInsuranceDataPS = [Select id, Name from PermissionSet where name = 'CRMS_301_HPInsuranceData'];
		if (hpInsuranceDataPS != null)
		{
			insert new PermissionSetAssignment(assigneeId = oCCSUser2.id, PermissionSetId = hpInsuranceDataPS[0].id);
		}


		System.runAs(oCCSUser2)
		{
			Test.setMock(WebServiceMock.class, new HUMInqTempUriOrgAsyncServiceMockTest());
			HUMTestDataHelperTest.getHUMConstantsData();
			HUM_Webservice_Callout__c vfs = new HUM_Webservice_Callout__c(Name = 'RetrieveInquiry', Certificate_Name__c = 'TestCertificate', Timeout__c = '120000', End_Point_URL__c = 'https://int-crmservicews.humana.com/InquirySearchService.svc', Active__c = true);
			insert vfs;
			System.assertEquals(vfs.Name, 'RetrieveInquiry');
			System.assertEquals(vfs.Certificate_Name__c, 'TestCertificate');

			HUM_Webservice_Callout__c cstGCP = new HUM_Webservice_Callout__c(Name = 'HUMLaunchGCP', Certificate_Name__c = 'TestCertificate1', Timeout__c = '3000', End_Point_URL__c = 'http://test-gcp.humana.com/utilities/LaunchGCP.aspx', Active__c = true);
			insert cstGCP;
			System.assertEquals(cstGCP.Name, 'HUMLaunchGCP');
			System.assertEquals(cstGCP.Certificate_Name__c, 'TestCertificate1');

			Account objAcc2 = HUMTestDataHelperTest.getAccount();
			objAcc2.FirstName = 'Test2';
			objAcc2.LastName = 'Account2';
			objAcc2.Tenant_Id__c = '00';
			objAcc2.Enterprise_ID__c = '1005577525';
			objAcc2.Mbr_Gen_Key__c = '9998004461817';
			objAcc2.General_Account__c = true;
			objAcc2.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();
			insert objAcc2;
			System.assertEquals(objAcc2.Tenant_Id__c, '00');

			Account oAcc = HUMTestDataHelperTest.getAccount();
			oAcc.Name = 'Test Group2';
			oAcc.Enterprise_ID__c = '7420004174017';
			oAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUM_Group_Account_Record_Type).getRecordTypeId();
			insert oAcc;
			System.assertEquals(oAcc.Enterprise_ID__c, '7420004174017');


			Product2 oProd2 = new Product2(Name = 'Laptop X2002', Segment_Type__c = 'MCR', ProductCode = 'MED', Major_LOB__c = 'MER');
			insert oProd2;
			System.assertNotEquals(oProd2.Id, NULL, 'Value not matched');

			Policy__c objpol2 = HUMTestDataHelperTest.getPolicy();
			objpol2.Name = 'testPolicy2';
			objpol2.Group_Name__c = objAcc2.Id;
			objpol2.Major_LOB__c = 'MER';
			objpol2.Source_Cust_Cov_Key__c = '123456789';
			objpol2.Product__c = oProd2.Id;
			objpol2.Product__r = oProd2;
			objpol2.Platform__c = 'LV';
			insert objpol2;
			system.assertEquals(objpol2.Source_Cust_Cov_Key__c, '123456789');

			Policy_Member__c objPolMem2 = new Policy_Member__c();
			objPolMem2.Name = 'testpolmem12';
			objPolMem2.Relationship__c = 'Self';
			objPolMem2.Subscriber__c = objPolMem2.Id;
			objPolMem2.Effective_Date__c = Date.today().addDays(-1).format();
			objPolMem2.End_Date__c = Date.today().addDays(1).format();
			objPolMem2.Policy_Member_External_ID__c = '00|ODS|69250044348172|12345678|34567|2013-07-01';
			objPolMem2.Member__c = objAcc2.Id;
			objPolMem2.Member__r = objAcc2;
			objPolMem2.Policy__c = objpol2.Id;
			objPolMem2.Policy__r = objpol2;
			insert objPolMem2;
			system.assertEquals(objPolMem2.Relationship__c, 'Self');
		}

		User oCCSUser3 = HUMTestDataHelperTest.createTestUser('User3', 'Humana Pharmacy Specialist');
		insert oCCSUser3;
		system.assertEquals(oCCSUser3.LastName, 'test User3');


		System.runAs(oCCSUser3)
		{
			Test.setMock(WebServiceMock.class, new HUMInqTempUriOrgAsyncServiceMockTest());
			HUMTestDataHelperTest.getHUMConstantsData();
			HUM_Webservice_Callout__c vfs = new HUM_Webservice_Callout__c(Name = 'RetrieveInquiry', Certificate_Name__c = 'TestCertificate', Timeout__c = '120000', End_Point_URL__c = 'https://int-crmservicews.humana.com/InquirySearchService.svc', Active__c = true);
			insert vfs;
			System.assertEquals(vfs.Name, 'RetrieveInquiry');
			System.assertEquals(vfs.Certificate_Name__c, 'TestCertificate');

			HUM_Webservice_Callout__c cstGCP = new HUM_Webservice_Callout__c(Name = 'HUMLaunchGCP', Certificate_Name__c = 'TestCertificate1', Timeout__c = '3000', End_Point_URL__c = 'http://test-gcp.humana.com/utilities/LaunchGCP.aspx', Active__c = true);
			insert cstGCP;
			System.assertEquals(cstGCP.Name, 'HUMLaunchGCP');
			System.assertEquals(cstGCP.Certificate_Name__c, 'TestCertificate1');

			Account objAcc3 = HUMTestDataHelperTest.getAccount();
			objAcc3.FirstName = 'Test3';
			objAcc3.LastName = 'Account3';
			objAcc3.Tenant_Id__c = '00';
			objAcc3.Enterprise_ID__c = '1005577525';
			objAcc3.General_Account__c = true;
			objAcc3.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();
			insert objAcc3;
			System.assertEquals(objAcc3.Tenant_Id__c, '00');

			Account oAcc = HUMTestDataHelperTest.getAccount();
			oAcc.Name = 'Test Group3';
			oAcc.Enterprise_ID__c = '7420004174017';
			oAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUM_Group_Account_Record_Type).getRecordTypeId();
			insert oAcc;
			System.assertEquals(oAcc.Enterprise_ID__c, '7420004174017');


			Product2 oProd3 = new Product2(Name = 'Laptop X2003', Segment_Type__c = 'MCR', ProductCode = 'MED', Major_LOB__c = 'MER');
			insert oProd3;
			System.assertNotEquals(oProd3.Id, NULL, 'Value not matched');

			Policy__c objpol3 = HUMTestDataHelperTest.getPolicy();
			objpol3.Name = 'testPolicy3';
			objpol3.Group_Name__c = objAcc3.Id;
			objpol3.Major_LOB__c = 'MER';
			objpol3.Source_Cust_Cov_Key__c = '123456789';
			objpol3.Product__c = oProd3.Id;
			objpol3.Product__r = oProd3;
			objpol3.Platform__c = 'LV';
			insert objpol3;
			system.assertEquals(objpol3.Source_Cust_Cov_Key__c, '123456789');

			Policy_Member__c objPolMem3 = new Policy_Member__c();
			objPolMem3.Name = 'testpolmem13';
			objPolMem3.Relationship__c = 'Self';
			objPolMem3.Subscriber__c = objPolMem3.Id;
			objPolMem3.Effective_Date__c = Date.today().addDays(-1).format();
			objPolMem3.End_Date__c = Date.today().addDays(1).format();
			objPolMem3.Policy_Member_External_ID__c = '00|ODS|69250044348173|12345678|34567|2013-07-01';
			objPolMem3.Member__c = objAcc3.Id;
			objPolMem3.Member__r = objAcc3;
			objPolMem3.Policy__c = objpol3.Id;
			objPolMem3.Policy__r = objpol3;
			insert objPolMem3;
			system.assertEquals(objPolMem3.Relationship__c, 'Self');
		}


    }
    
   /*
     * Method Name : runPositiveData
     * Description : Used to run positive scenerios of test data
     * Return type : void
     * Paramater   : NA
     */ 
    static testMethod void runPositiveData()
    {    
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        
        System.runAs(oCCSUser)
        {
            Account oAccount = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account'];  
            System.assertEquals(oAccount.Tenant_Id__c, '00');
            
            String sAccRecType = oAccount.RecordType.Name;
            System.assertEquals(sAccRecType,'Member');
            
            Policy_Member__c oPolMem =  [Select Id,Subscriber__c ,Relationship__c,Policy__r.Group_Name__r.Home_Office_Account__c, Member__c, Policy__c, Policy_Member_External_ID__c , Policy__r.Product__r.ProductCode from Policy_Member__c where Member__c =: oAccount.Id];
            System.assertEquals(oPolMem.Relationship__c, 'Self');
             
            String sPageId = ApexPages.CurrentPage().getparameters().put('id', oAccount.Id);
            Test.startTest();
                Test.setMock(WebServiceMock.class, new HUMInqTempUriOrgAsyncServiceMockTest()); 
                HUMInquiryHistoryServiceHelper_v1 oHumHisServiceHelper = new  HUMInquiryHistoryServiceHelper_v1();
                HUMInqTempUriOrgAsyncService.Asyncsoap Service = new HUMInqTempUriOrgAsyncService.Asyncsoap();
                HUMInqSchemasDataContractOrgService.InquirySearchResultDTO sObjInquiryOutput = new HUMInqSchemasDataContractOrgService.InquirySearchResultDTO();
                HUMInqSchemasDataContractOrgService.InquiryDTO inqDto = new HUMInqSchemasDataContractOrgService.InquiryDTO();
                HUMInquiryHistoryController_v1 inqHistory = new HUMInquiryHistoryController_v1();
                inqHistory.sFromDate ='01/01/2012';
                inqHistory.sToDate ='12/12/2017';
                inqHistory.bFamilyMem = true;
                inqHistory.iTableSize = 1;
                inqHistory.retrieveInquiries();
                Continuation objCon = New Continuation(120);
                objCon.continuationMethod='processResponse';
                oHumHisServiceHelper.constructWrapper(sAccRecType,sObjInquiryOutput);
                oHumHisServiceHelper.constructWrapperHelper(inqDto);
                List<HUMInquiryHistoryWrapperHelper> lstInquiryHistorylist = inqHistory.lstReqInquiryHistorylist;
             Test.stopTest();
        }
    }

	/*
	* Method Name : runPositiveData
	* Description : Used to run positive scenerios of test data
	* Return type : void
	* Paramater   : NA
	*/
	static testMethod void runPositiveDataHPSIns()
	{
		User oCCSUser2 = [Select id, UserName from User where LastName = 'test User2' LIMIT 1];
		system.assertEquals(oCCSUser2.UserName, 'user2user2@humcrmstest.com');

		System.runAs(oCCSUser2)
		{
			Account oAccount = [Select Id, Name, Tenant_Id__c, RecordType.Name, Enterprise_ID__c  from Account where LastName = 'Account2'];
			System.assertEquals(oAccount.Tenant_Id__c, '00');

			String sAccRecType = oAccount.RecordType.Name;
			System.assertEquals(sAccRecType, 'Member');

			Policy_Member__c oPolMem = [Select Id, Subscriber__c, Relationship__c, Policy__r.Group_Name__r.Home_Office_Account__c, Member__c, Policy__c, Policy_Member_External_ID__c, Policy__r.Product__r.ProductCode from Policy_Member__c where Member__c = :oAccount.Id];
			System.assertEquals(oPolMem.Relationship__c, 'Self');

			String sPageId = ApexPages.CurrentPage().getparameters().put('id', oAccount.Id);
			Test.startTest();
			Test.setMock(WebServiceMock.class, new HUMInqTempUriOrgAsyncServiceMockTest());
			HUMInquiryHistoryServiceHelper_v1 oHumHisServiceHelper = new  HUMInquiryHistoryServiceHelper_v1();
			HUMInqTempUriOrgAsyncService.Asyncsoap Service = new HUMInqTempUriOrgAsyncService.Asyncsoap();
			HUMInqSchemasDataContractOrgService.InquirySearchResultDTO sObjInquiryOutput = new HUMInqSchemasDataContractOrgService.InquirySearchResultDTO();
			HUMInqSchemasDataContractOrgService.InquiryDTO inqDto = new HUMInqSchemasDataContractOrgService.InquiryDTO();
			HUMInquiryHistoryController_v1 inqHistory = new HUMInquiryHistoryController_v1();
			inqHistory.sFromDate = '01/01/2012';
			inqHistory.sToDate = '12/12/2017';
			inqHistory.bFamilyMem = true;
			inqHistory.iTableSize = 1;
			inqHistory.retrieveInquiries();
			Continuation objCon = New Continuation(120);
			objCon.continuationMethod = 'processResponse';
			oHumHisServiceHelper.constructWrapper(sAccRecType, sObjInquiryOutput);
			oHumHisServiceHelper.constructWrapperHelper(inqDto);
			List<HUMInquiryHistoryWrapperHelper> lstInquiryHistorylist = inqHistory.lstReqInquiryHistorylist;
			Test.stopTest();
		}


	}

	/*
	* Method Name : runPositiveData
	* Description : Used to run positive scenerios of test data
	* Return type : void
	* Paramater   : NA
	*/
	static testMethod void runPositiveDataHPS()
	{
		User oCCSUser3 = [Select id, UserName from User where LastName = 'test User3' LIMIT 1];
		system.assertEquals(oCCSUser3.UserName, 'user3user3@humcrmstest.com');

		System.runAs(oCCSUser3)
		{
			Account oAccount = [Select Id, Name, Tenant_Id__c, RecordType.Name, Enterprise_ID__c  from Account where LastName = 'Account3'];
			System.assertEquals(oAccount.Tenant_Id__c, '00');

			String sAccRecType = oAccount.RecordType.Name;
			System.assertEquals(sAccRecType, 'Member');

			Policy_Member__c oPolMem = [Select Id, Subscriber__c, Relationship__c, Policy__r.Group_Name__r.Home_Office_Account__c, Member__c, Policy__c, Policy_Member_External_ID__c, Policy__r.Product__r.ProductCode from Policy_Member__c where Member__c = :oAccount.Id];
			System.assertEquals(oPolMem.Relationship__c, 'Self');

			String sPageId = ApexPages.CurrentPage().getparameters().put('id', oAccount.Id);
			Test.startTest();
			Test.setMock(WebServiceMock.class, new HUMInqTempUriOrgAsyncServiceMockTest());
			HUMInquiryHistoryServiceHelper_v1 oHumHisServiceHelper = new  HUMInquiryHistoryServiceHelper_v1();
			HUMInqTempUriOrgAsyncService.Asyncsoap Service = new HUMInqTempUriOrgAsyncService.Asyncsoap();
			HUMInqSchemasDataContractOrgService.InquirySearchResultDTO sObjInquiryOutput = new HUMInqSchemasDataContractOrgService.InquirySearchResultDTO();
			HUMInqSchemasDataContractOrgService.InquiryDTO inqDto = new HUMInqSchemasDataContractOrgService.InquiryDTO();
			HUMInquiryHistoryController_v1 inqHistory = new HUMInquiryHistoryController_v1();
			inqHistory.sFromDate = '01/01/2012';
			inqHistory.sToDate = '12/12/2017';
			inqHistory.bFamilyMem = true;
			inqHistory.iTableSize = 1;
			inqHistory.retrieveInquiries();
			Continuation objCon = New Continuation(120);
			objCon.continuationMethod = 'processResponse';
			oHumHisServiceHelper.constructWrapper(sAccRecType, sObjInquiryOutput);
			oHumHisServiceHelper.constructWrapperHelper(inqDto);
			List<HUMInquiryHistoryWrapperHelper> lstInquiryHistorylist = inqHistory.lstReqInquiryHistorylist;
			Test.stopTest();
		}


	}

}