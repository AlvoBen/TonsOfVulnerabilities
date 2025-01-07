/*****************************************************************************************************************************************************************************
    @Apex Class Name  : DemographicMedicareAddress_S_T_HUM
    @Version          : 1.0
    @Created Date     : June 28th, 2020
    @Function         : Test class for DemographicMedicareAddress_S_HUM
******************************************************************************************************************************************************************************
Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Ajay Chakradhar                                  28/06/2020                    Original Version
******************************************************************************************************************************************************************************/
@isTest
private class DemographicMedicareAddress_S_T_HUM {
    /*
    * This method creates test data for test class
    * @param        None
    * @return       None
    */
  @testSetup static void prepareTestData() 
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertEquals(oCCSUser.LastName,'test User1');
        
        System.runAs(oCCSUser) 
        {
            Account objAcc = HUMTestDataHelperTest.getAccount(); 
            objAcc.FirstName = 'Test';
            objAcc.LastName = 'Account';
            objAcc.Tenant_Id__c = '00';
            objAcc.Enterprise_ID__c = '1005577525';
            objAcc.General_Account__c = true;
            insert objAcc;
            System.assertEquals(objAcc.Tenant_Id__c,'00');
            
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
            objPolMem.Relationship_Code__c = '00';
            objPolMem.Subscriber__c = objPolMem.Id;
            objPolMem.Effective_Date__c=Date.today().addDays(-1).format();
            objPolMem.End_Date__c=Date.today().addDays(1).format();
            objPolMem.Policy_Member_External_ID__c='00|ODS|6925004434817|12345678|34567|2013-07-01';
            objPolMem.Member__c = objAcc.Id;
            objPolMem.Policy__c = objpol.Id;
            objPolMem.Policy__r = objpol;
            insert objPolMem;
            system.assertEquals(objPolMem.Relationship__c, 'Self');
            
            Member_ID__c objMemberID2 = new Member_ID__c();
            objMemberID2.policy_member__c = objPolMem.id;
            objMemberID2.policy__c = objpol.Id;
            objMemberID2.Name = 'H12345';
            objMemberID2.Member_Card_External_ID__c = 'ODS123456'; 
            objMemberID2.Member_Dependent_Code__c  = '03';
            objMemberID2.Type__c = 'Member-Id-Base';        
            insert objMemberID2;
            system.assertequals('H12345',objMemberID2.Name);
            
            Case ocjCase = new Case();
            ocjCase.AccountId = objAcc.id;
            insert ocjCase;
            system.assertNotequals(Null,ocjCase.AccountId);
            
            HUM_Webservice_Callout__c objTemplateService = new HUM_Webservice_Callout__c();
            objTemplateService.Name = 'AVFTemplate';
            objTemplateService.Active__c = True;
            objTemplateService.Enable_Service_Tester__c = True;
            objTemplateService.Certificate_Name__c = 'Humana_GuidanceCare_neptune';
            objTemplateService.Content_Type__c = 'application/json';
            objTemplateService.Request_Type__c = 'POST';
            objTemplateService.Timeout__c = '120000';
            objTemplateService.End_Point_URL__c = 'https://int-crmservicews.humana.com/TemplateService_V1.0/SaveAVFTemplate';
            objTemplateService.Service_Name__c = '/AVFTemplate';
            insert objTemplateService;
            system.assertequals('AVFTemplate',objTemplateService.Name);
        }
    }
    
    /*
    * Method to test success response for MedicareAddress
    * @param        None
    * @return       None
    */
    private static testmethod void testSuccessMedicareAddress()
    {
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        system.runAs(testUser)
        { 
            Account accountObj = [Select id from account where LastName = 'Account'];
            Case caseObj = [Select id from Case where Accountid =: accountObj.id];
            Policy_Member__c pmObj = [Select id from Policy_Member__c where Member__c =: accountObj.id];
            Test.StartTest();
            String sMedicareAddressRequest = '{"templateBase":[{"data":{"rsoMAU":{"PersonSpeakingWith":"MARIE RODGERS","RelationToMember":"MEMBER","PermanentResidentialPhoneNumber":"(876) 756-9769","WhatAddressMemberCallingToUpdate":"temporary","PermanentResidentialAddress":"1723 KEENELAND COURT","PermanentResidentialCityName":"MURFREESBORO","PermanentResidentialStateCode":"TN","PermanentResidentialZipCode":"37217","PermanentResidentialCountyName":"RUTHERFORD","PermanentResidentialStartDate":"2020-06-11","DoesMemberWantMailSentToTemporaryAddress":"No","DoesMemberWantMailAddressDiffFromResAddress":"No","MailingAddress":"1723 KEENELAND COURT","MailingCityName":"MURFREESBORO","MailingStateCode":"TN","MailingZipCode":"37217","MailingCounty":"RUTHERFORD","TemporaryAddress":"1201 Christ RD","TemporaryCityName":"MURFREESBORO","TemporaryStateCode":"TN","TemporaryZipCode":"37217","TemporaryCountyName":"RUTHERFORD","TemporaryOSAStartDate":"2020-06-01","TemporaryOSAEndDate":"2020-06-26"}},"templateName":"Address Update - Medicare","key":"mau"}]}';
          	String caseNumber = caseObj.Id;
            String policyId = pmObj.id;
            Test.setMock(HttpCalloutMock.class, new DemographicMAUSuccessMockResponse_T_HUM());
            DemographicMedicareAddress_S_HUM.sendMedicareAddressService(caseNumber, sMedicareAddressRequest, policyId);
            System.assertNotEquals(null, sMedicareAddressRequest);    
            Test.StopTest();
        }
    } 
    /*
    * Method to test Failure for MedicareAddress
    * @param        None
    * @return       None
    */
    private static testmethod void testFailureMedicareAddress()
    {
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        system.runAs(testUser)
        { 
            Account accountObj = [Select id from account where LastName = 'Account'];
            Case caseObj = [Select id from Case where Accountid =: accountObj.id];
            Policy_Member__c pmObj = [Select id from Policy_Member__c where Member__c =: accountObj.id];
            Test.StartTest();
            String sMedicareAddressRequest = '{"templateBase":[{"data":{"rsoMAU":{"PersonSpeakingWith":"MARIE RODGERS","RelationToMember":"MEMBER","PermanentResidentialPhoneNumber":"(876) 756-9769","WhatAddressMemberCallingToUpdate":"temporary","PermanentResidentialAddress":"1723 KEENELAND COURT","PermanentResidentialCityName":"MURFREESBORO","PermanentResidentialStateCode":"TN","PermanentResidentialZipCode":"37217","PermanentResidentialCountyName":"RUTHERFORD","PermanentResidentialStartDate":"","DoesMemberWantMailSentToTemporaryAddress":"No","DoesMemberWantMailAddressDiffFromResAddress":"No","MailingAddress":"1723 KEENELAND COURT","MailingCityName":"MURFREESBORO","MailingStateCode":"TN","MailingZipCode":"37217","MailingCounty":"RUTHERFORD","TemporaryAddress":"1201 Christ RD","TemporaryCityName":"MURFREESBORO","TemporaryStateCode":"TN","TemporaryZipCode":"37217","TemporaryCountyName":"RUTHERFORD","TemporaryOSAStartDate":"NaN/NaN/NaN","TemporaryOSAEndDate":"NaN/NaN/NaN"}},"templateName":"Address Update - Medicare","key":"mau"}]}';
          	String caseNumber = caseObj.Id;
            String policyId = pmObj.id;
            DemographicMedicareAddress_S_HUM.sendMedicareAddressService(caseNumber, sMedicareAddressRequest, policyId);
            System.assertNotEquals(null, sMedicareAddressRequest);    
            Test.StopTest();
        }
    } 
	/*
    * Method to test Exception for MedicareAddress
    * @param        None
    * @return       None
    */
    private static testmethod void testExceptionMedicareAddress()
    {
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        system.runAs(testUser)
        { 
            Account accountObj = [Select id from account where LastName = 'Account'];
            Case caseObj = [Select id from Case where Accountid =: accountObj.id];
            Policy_Member__c pmObj = [Select id from Policy_Member__c where Member__c =: accountObj.id];
            Test.StartTest();
            String sMedicareAddressRequest = '{"templateBase":[{"data":{"rsoMAU":{"PersonSpeakingWith":"MARIE RODGERS","RelationToMember":"MEMBER","PermanentResidentialPhoneNumber":"(876) 756-9769","WhatAddressMemberCallingToUpdate":"temporary","PermanentResidentialAddress":"1723 KEENELAND COURT","PermanentResidentialCityName":"MURFREESBORO","PermanentResidentialStateCode":"TN","PermanentResidentialZipCode":"37217","PermanentResidentialCountyName":"RUTHERFORD","PermanentResidentialStartDate":"2020-06-11","DoesMemberWantMailSentToTemporaryAddress":"No","DoesMemberWantMailAddressDiffFromResAddress":"No","MailingAddress":"1723 KEENELAND COURT","MailingCityName":"MURFREESBORO","MailingStateCode":"TN","MailingZipCode":"37217","MailingCounty":"RUTHERFORD","TemporaryAddress":"1201 Christ RD","TemporaryCityName":"MURFREESBORO","TemporaryStateCode":"TN","TemporaryZipCode":"37217","TemporaryCountyName":"RUTHERFORD","TemporaryOSAStartDate":"2020-06-01","TemporaryOSAEndDate":"2020-06-26"}},"templateName":"Address Update - Medicare","key":"mau"}]}';
          	String caseNumber = caseObj.Id;
            String policyId = pmObj.id;
            DemographicMedicareAddress_S_HUM.sendMedicareAddressService(caseNumber, sMedicareAddressRequest, policyId);
            System.assertNotEquals(null, sMedicareAddressRequest);    
            Test.StopTest();
        }
    } 
}