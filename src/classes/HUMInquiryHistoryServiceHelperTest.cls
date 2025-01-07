/*************************************************************************************************************************
Apex Class Name  : HUMInquiryHistoryServiceHelperTest
Version          : 1.0
Created Date     : April 06 2015
Function         : Test class to HUMInquiryHistoryServiceHelper class

* Modification Log :
* Modification ID     Developer                Code Review            Date                 Description
* ----------------------------------------------------------------------------------------------------------------------------------------
* 1.0                Vishal Verma               22273               04/06/2015           Original Version
* 1.1                Tanushree Hazari           26433               09/16/2015           Test class modified as per the new standards.
* 1.2                Tanushree Hazari           28298               11/26/2015           Code modified to increase the code coverage.
*****************************************************************************************************************************************/

@isTest
private class HUMInquiryHistoryServiceHelperTest
{
        /*
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
            Test.setMock(WebServiceMock.class, new HUMInqTempUriOrgTest());
            HUMTestDataHelperTest.getHUMConstantsData();
            HUM_Webservice_Callout__c vfs = new HUM_Webservice_Callout__c(Name = 'RetrieveInquiry', Certificate_Name__c = 'TestCertificate', Timeout__c = '120000', End_Point_URL__c = 'https://int-crmservicews.humana.com/InquirySearchService.svc', Active__c=true);
            insert vfs;
            System.assertEquals(vfs.Name,'RetrieveInquiry');
            System.assertEquals(vfs.Certificate_Name__c,'TestCertificate');
            
            HUM_Webservice_Callout__c cstGCP = new HUM_Webservice_Callout__c(Name = 'HUMLaunchGCP', Certificate_Name__c = 'TestCertificate1', Timeout__c = '3000', End_Point_URL__c = 'http://test-gcp.humana.com/utilities/LaunchGCP.aspx', Active__c=true);
            insert cstGCP ;
            System.assertEquals(cstGCP.Name,'HUMLaunchGCP');
            System.assertEquals(cstGCP.Certificate_Name__c,'TestCertificate1');
            
            Group oGroup1 = HUMTestDataHelperTest.getQueues('Group one');
            insert oGroup1;
            System.assertEquals(oGroup1.Name,'Group one');
            
            GroupMember GroupMem = HUMTestDataHelperTest.getGrpMems(oGroup1.id, oCCSUser.id);
            insert GroupMem;
            System.assertEquals(GroupMem.GroupId,oGroup1.id);

            
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
                Test.setMock(WebServiceMock.class, new HUMInquiryServiceMockResponseTest());
                
                HUMInquiryHistoryServiceHelper oHumHisServiceHelper = new  HUMInquiryHistoryServiceHelper();
                HUMInqTempUriOrgService.soap Service = new HUMInqTempUriOrgService.soap();
                HUMInqSchemasDataContractOrgService.InquirySearchResultDTO sObjInquiryOutput = new HUMInqSchemasDataContractOrgService.InquirySearchResultDTO();
                HUMInqSchemasDataContractOrgService.InquiryDTO inqDto = new HUMInqSchemasDataContractOrgService.InquiryDTO();
                HUMInquiryHistoryController inqHistory = new HUMInquiryHistoryController();
                inqHistory.sFromDate ='12/02/2010';
                inqHistory.sToDate ='12/12/2010';
                inqHistory.bFamilyMem = true;
                inqHistory.iTableSize = 1;
                inqHistory.retrieveLegacyData();
                oHumHisServiceHelper.constructWrapper(sAccRecType,sObjInquiryOutput);
                oHumHisServiceHelper.constructWrapperHelper(inqDto);
                List<HUMInquiryHistoryWrapperHelper> lstInquiryHistorylist = inqHistory.lstReqInquiryHistorylist;
                system.assert(lstInquiryHistorylist != null);
            Test.stopTest();
        }
    }
    
    /*
     * Method Name : runDataForGrpAccount
     * Description : Used to run scenerios for group test data
     * Return type : void
     * Paramater   : NA
     */ 
    static testMethod void runDataForGrpAccount()
    { 
        User oCCSUser = [Select id,Username from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        
        System.runAs(oCCSUser)
        {
            Account oAccountGrp = [Select Id,Name,Tenant_Id__c,Enterprise_ID__c  from Account where Name = 'Test Group'];  
            System.assertEquals(oAccountGrp.Enterprise_ID__c, '7420004174017');
            
            ApexPages.CurrentPage().getparameters().put('id', oAccountGrp.Id);
            Test.startTest();
                Test.setMock(WebServiceMock.class, new HUMInquiryServiceMockResponseTest());
                HUMInquiryHistoryServiceHelper oHumHisServiceHelper = new  HUMInquiryHistoryServiceHelper();
                HUMInquiryHistoryController inqHistory = new HUMInquiryHistoryController();
                inqHistory.sFromDate ='';
                inqHistory.sToDate ='';
                inqHistory.bFamilyMem = false;
                inqHistory.iTableSize = 1;
                inqHistory.retrieveLegacyData();
                list<HUMInquiryHistoryWrapperHelper> lstInquiryHistorylist = inqHistory.lstReqInquiryHistorylist;
                system.assert(lstInquiryHistorylist != null);
                
            Test.stopTest();
        }
    }
}