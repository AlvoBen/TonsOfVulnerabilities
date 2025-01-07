/*****************************************************************************************************************************************************************************
    @Apex Class Name  : GrievanceAndAppealComponent_T_RT_HUM
    @Version          : 1.0
    @Created Date     : Nov 7, 2018
    @Function         : Test class for GrievanceAndAppealComponent_C_RT_HUM
******************************************************************************************************************************************************************************
Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Ajay Chakradhar                                   11/07/2018                    Original Version
* Santhi Mandava                                    04/15/2019                    Replaced "Member_Id_Value__c" with Name    
******************************************************************************************************************************************************************************/
@isTest
private class GrievanceAndAppealComponent_T_RT_HUM
{
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
        
        System.runAs (oCCSUser) 
        {
            Account objAcc = HUMTestDataHelperTest.getAccount(); 
            objAcc.FirstName = 'Test';
            objAcc.LastName = 'Account';
            objAcc.Tenant_Id__c = '00';
            objAcc.Enterprise_ID__c = '1005577525';
            objAcc.General_Account__c = true;
            objAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();
            insert objAcc;
            System.assertEquals(objAcc.Tenant_Id__c,'00');
            
            Account objAcc1 = HUMTestDataHelperTest.getAccount(); 
            objAcc1.FirstName = 'Test again';
            objAcc1.LastName = 'Account1';
            objAcc1.Tenant_Id__c = '00';
            objAcc1.Enterprise_ID__c = '1005577525';
            objAcc1.General_Account__c = true;
            objAcc1.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();
            insert objAcc1;
            System.assertEquals(objAcc1.Tenant_Id__c,'00');
            
            Account objAcc2 = HUMTestDataHelperTest.getAccount(); 
            objAcc2.FirstName = 'Test memberid';
            objAcc2.LastName = 'Account2';
            objAcc2.Tenant_Id__c = '00';
            objAcc2.Enterprise_ID__c = '1005587525';
            objAcc2.General_Account__c = true;
            objAcc2.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();
            insert objAcc2;
            System.assertEquals(objAcc2.Tenant_Id__c,'00');
            
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
            
            Policy__c objpol1 = HUMTestDataHelperTest.getPolicy();
            objpol1.Name='testPolicy';
            objpol1.Group_Name__c = objAcc1.Id;
            objpol1.Major_LOB__c = 'MER';
            objpol1.Source_Cust_Cov_Key__c = '123456789';
            objpol1.Product__c = oProd.Id;
            objpol1.Product__r = oProd;
            objpol1.Platform__c = 'LV';
            insert objpol1;
            system.assertEquals(objpol1.Source_Cust_Cov_Key__c,'123456789');
            
            Policy__c objpol2 = HUMTestDataHelperTest.getPolicy();
            objpol2.Name='testPolicy1';
            objpol2.Group_Name__c = objAcc2.Id;
            objpol2.Major_LOB__c = 'MER';
            objpol2.Source_Cust_Cov_Key__c = '123456789';
            objpol2.Product__c = oProd.Id;
            objpol2.Product__r = oProd;
            objpol2.Platform__c = 'LV';
            insert objpol2;
            system.assertEquals(objpol2.Source_Cust_Cov_Key__c,'123456789');

            
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
            
            Policy_Member__c objPolMem1 = new Policy_Member__c();
            objPolMem1.Name = 'testpolmem111';
            objPolMem1.Relationship__c = 'Self';
            objPolMem1.Relationship_Code__c = '00';
            objPolMem1.Subscriber__c = objPolMem.Id;
            objPolMem1.Effective_Date__c=Date.today().addDays(-1).format();
            objPolMem1.End_Date__c=Date.today().addDays(1).format();
            objPolMem1.Policy_Member_External_ID__c='00|ODS|7925004434817|12345678|34567|2013-07-01';
            objPolMem1.Member__c = objAcc1.Id;
            objPolMem1.Policy__c = objpol1.Id;
            objPolMem1.Policy__r = objpol1;
            insert objPolMem1;
            system.assertEquals(objPolMem1.Relationship__c, 'Self');
            
            Policy_Member__c objPolMem2 = new Policy_Member__c();
            objPolMem2.Name = 'testpolmem114';
            objPolMem2.Relationship__c = 'Self';
            objPolMem2.Relationship_Code__c = '00';
            objPolMem2.Subscriber__c = objPolMem1.Id;
            objPolMem2.Effective_Date__c=Date.today().addDays(-1).format();
            objPolMem2.End_Date__c=Date.today().addDays(1).format();
            objPolMem2.Policy_Member_External_ID__c='00|ODS|7925004435817|12345678|34567|2013-07-01';
            objPolMem2.Member__c = objAcc2.Id;
            objPolMem2.Policy__c = objpol2.Id;
            objPolMem2.Policy__r = objpol2;
            insert objPolMem2;
            system.assertEquals(objPolMem2.Relationship__c, 'Self');


                     
            Member_ID__c objMemberID1 = new Member_ID__c();
            objMemberID1.policy_member__c = objPolMem.id;
            objMemberID1.policy__c = objpol.Id;
            objMemberID1.Name = '12345';
            objMemberID1.Member_Card_External_ID__c = 'ODS12345'; 
            objMemberID1.Member_Dependent_Code__c  = '03';
            objMemberID1.Type__c = 'SSN';        
            insert objMemberID1;
            system.assertequals('12345',objMemberID1.Name );
            
            Member_ID__c objMemberID2 = new Member_ID__c();
            objMemberID2.policy_member__c = objPolMem1.id;
            objMemberID2.policy__c = objpol1.Id;
            objMemberID2.Name = 'H12345';
            objMemberID2.Member_Card_External_ID__c = 'ODS123456'; 
            objMemberID2.Member_Dependent_Code__c  = '03';
            objMemberID2.Type__c = 'Member-Id-Base';        
            insert objMemberID2;
            system.assertequals('H12345',objMemberID2.Name);
            
            Member_ID__c objMemberID3 = new Member_ID__c();
            objMemberID3.policy_member__c = objPolMem2.id;
            objMemberID3.policy__c = objpol2.Id;
            objMemberID3.Name = 'H123453';
            objMemberID3.Member_Card_External_ID__c = 'ODS1234564'; 
            objMemberID3.Member_Dependent_Code__c  = '03';
            objMemberID3.Type__c = 'Member-Id-Base';        
            insert objMemberID3;
            system.assertequals('H123453',objMemberID3.Name);
            
            HUM_Webservice_Callout__c calloutSettings = new HUM_Webservice_Callout__c();
            calloutSettings.Name = 'GrievanceAndAppeal';
            calloutSettings.Active__c = True;
            calloutSettings.Enable_Service_Tester__c = True;
            calloutSettings.Certificate_Name__c = 'Humana_GuidanceCare_Pileus';
            calloutSettings.Content_Type__c = 'application/json';
            calloutSettings.Request_Type__c = 'Post';
            calloutSettings.Timeout__c = '12000';
            calloutSettings.End_Point_URL__c = 'https://dev-crmservicews.humana.com/XXX';
            insert calloutSettings;
             
        }
    }        
    /*
    * Method to create the batch job
    * @param        None
    * @return       None
    */
    private static testmethod void testBatch()
    {
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        system.runAs(testUser)
        { 
            Policy_Member__c objPolMem = [select id,Name,Member__c from Policy_Member__c where Name = 'testpolmem11'];
            Test.StartTest();
              PageReference pageRef = Page.GrievanceAndAppeal_VF_HUM;
              Test.setCurrentPage(pageRef);
              ApexPages.currentPage().getParameters().put('id',objPolMem.id);
             GrievanceAndAppealComponent_C_RT_HUM comp = new GrievanceAndAppealComponent_C_RT_HUM();
             GrievanceAndAppealComponent_C_RT_HUM.getArchiveAttachments(objPolMem.id);
              HttpResponse response = new HttpResponse();
             response.setBody('{ "getGrievanceAndAppealByCriteriaResponse":{ "cases":[ { "caseNumber":"A18292338641", "caseType":"Pre-Service", "category":"Medical Necessity", "caseCategory":"MedicalAppeals", "communication":[ { "attempt":"1", "commDate":"2018-10-19T12:25:35-04:00", "commMethod":"Phone", "commOutcome":"Completed", "commType":"Member", "contactFirstName":"Test", "contactLastName":"", "details":"Test of member call" }, { "attempt":"1", "commDate":"2018-10-19T12:25:59-04:00", "commMethod":"Phone", "commOutcome":"Unable to Reach", "commType":"Primary Care Provider", "contactFirstName":"Provider First Name", "contactLastName":"", "details":"Unable to reach provider" } ], "createDate":"2018-10-19T12:24:35-04:00", "createUser":{ "userId":"mvaghasia", "firstName":"Mahendra", "lastName":"Vaghasia", "title":"Mr", "email":"mvaghasia@medhok.com", "terminated":"false", "accountLocked":"false", "comment":"test", "siteLocation":"Tampa", "lastLogin":"2018-06-25T13:21:43-04:00", "clientName":"0" }, "decisionDate":"2018-11-12T17:08:00-05:00","dueDate":"2018-11-18T11:24:01-05:00", "member":{ "age":"77", "benefitPackageDescription":"", "dateOfBirth":"1941-09-17", "daysLastDischarge":"-1", "effectiveDate":"2017-09-01T00:00:00-04:00", "enrollmentStatus":"ACTIVE", "ethinicity":"", "externalId":"1086069019", "familyId":"", "firstName":"RHOESHAUN", "fullName":"ROULETTE, RHOESHAUN G", "gender":"Female", "lastName":"ROULETTE", "medhokId":"901404", "memberAddresses":{ "address":[ { "address1":"6277 CLIFFORD STREET", "address2":"8888 SARDIS STA", "address3":"", "addressType":"Mailing", "alternatePhone":"", "city":"SITKA", "country":"", "county":"SITKA", "dstAdjust":"true", "effectivedate":"2018-10-18T00:00:00-04:00", "email":"", "emergencyPhone":"", "eveningPhone":"", "extId":"7222b0765da2ac354218100a1c47fd51", "fax":"", "firstName":"", "island":"", "lastName":"", "latitude":"56.9471", "longitude":"-135.0426", "medhokId":"80425", "phone":"", "state":"AK", "termdate":"", "timeZone":"", "zip":"99835" } ] }, "memberEligibilities":{ "eligibilityLine":[ { "company":"LiNET", "companyAccountGroup":"", "companyAccounts":"", "companyLob":"MEDICARE RISK PDP/RETRO POS LIS", "contractNumber":"X0001", "divCode":"", "effectivedate":"2017-06-01T00:00:00-04:00", "eligGroup":"HUMANA INSURANCE COMPANY", "eligPlan":"MEDICARE PDP", "enrollmentSource":"", "extId":"1419904252b297850eccd0a3fd50074d", "ipaGroup":"", "medCenter":"", "medhokId":"1982224", "medicaid":"", "medicareno":"1TR4YR8KH70", "memberQmbSlmb":"", "pbpNumber":"", "planCode":"", "planName":"", "primaryCarrierFlag":"", "regionCenter":"", "riderCode":"", "ssn":"", "subsidyLevel":"", "termdate":"2017-08-31T00:00:00-04:00" }, { "company":"Medicare", "companyAccountGroup":"", "companyAccounts":"", "companyLob":"MEDICARE RISK PRESCRIPTION DRUG PLAN IND", "contractNumber":"S5884", "divCode":"", "effectivedate":"2017-09-01T00:00:00-04:00", "eligGroup":"HUMANA INSURANCE CO.", "eligPlan":"MEDICARE PDP", "enrollmentSource":"", "extId":"a0db7e294c573d9f1099c63b7cb3729f", "ipaGroup":"", "medCenter":"", "medhokId":"1777583", "medicaid":"", "medicareno":"1TR4YR8KH70", "memberQmbSlmb":"", "pbpNumber":"", "planCode":"", "planName":"", "primaryCarrierFlag":"", "regionCenter":"", "riderCode":"", "ssn":"", "subsidyLevel":"", "termdate":"" } ] }, "middleName":"G", "otherLanguage":"", "otherLanguageSource":"", "pcp":{ "address1":"", "address2":"", "city":"", "fax":"", "federalTaxID":"", "firstName":"RHOESHAUN", "fullName":"ROULETTE, RHOESHAUN G", "groupNpi":"", "lastName":"ROULETTE", "licenseNumber":"", "npi":"", "organizationName":"", "phone":"", "providerID":"", "providerType":"", "region":"", "state":"", "zip":"" }, "memberId":"1086069019", "ssn":"", "subscriberId":"H31620582", "termDate":"" }, "medhokID":"3330", "notification":[ { "createdBy":"mvaghasia", "createdDate":"2018-10-19T12:25:12-04:00", "note":"This is a appeal note test", "noteDate":"2018-10-19T00:00:00-04:00", "noteType":"Appeal Note" }, { "createdBy":"mvaghasia", "createdDate":"2018-10-19T12:25:29-04:00", "note":"This is decision note test", "noteDate":"2018-10-19T00:00:00-04:00", "noteType":"Decision Note" }, { "createdBy":"mvaghasia", "createdDate":"2018-10-19T12:29:06-04:00", "note":"", "noteDate":"2018-10-19T00:00:00-04:00", "noteType":"APL Withdrawal" }, { "createdBy":"smulheron", "createdDate":"2018-11-06T10:54:00-05:00", "note":"", "noteDate":"2018-11-06T00:00:00-05:00", "noteType":"APL RMI Request" }, { "createdBy":"smulheron", "createdDate":"2018-11-07T14:06:26-05:00", "note":"", "noteDate":"2018-11-07T00:00:00-05:00", "noteType":"External Template Notify" }, { "createdBy":"smulheron", "createdDate":"2018-11-07T14:06:49-05:00", "note":"", "noteDate":"2018-11-07T00:00:00-05:00", "noteType":"APL RMI Request- Fax" } ], "priority":"Standard", "receivedDate":"2018-10-19T12:24:35-04:00", "requestType":"Reconsideration", "status":"Open", "statusReason":"Pending Initial Review", "subCategory":"Ambulance", "updateUser":{ "userId":"", "firstName":"", "lastName":"", "title":"", "email":"", "phone":"", "fax":"", "terminated":"", "accountLocked":"", "comment":"", "siteLocation":"", "lastLogin":"", "clientName":"" }, "validCase":"true" }, { "caseNumber":"G18292892177", "caseType":"Access", "category":"", "caseCategory":"Grievance", "communication":[  ], "createDate":"2018-10-19T12:32:52-04:00", "createUser":{ "userId":"280", "firstName":"Mahendra", "lastName":"Vaghasia", "title":"", "email":"", "terminated":"false", "accountLocked":"false", "comment":"", "siteLocation":"", "lastLogin":"", "clientName":"" }, "dueDate":"2018-11-18T11:32:04-05:00", "member":{ "age":"", "benefitPackageDescription":"", "dateOfBirth":"1941-09-17", "daysLastDischarge":"", "effectiveDate":"", "enrollmentStatus":"", "ethinicity":"", "externalId":"1086069019", "familyId":"", "firstName":"RHOESHAUN", "fullName":"", "gender":"", "lastName":"ROULETTE", "medhokId":"", "memberAddresses":{ "address":[  ] }, "memberEligibilities":{ "eligibilityLine":[  ] }, "middleName":"", "otherLanguage":"", "otherLanguageSource":"", "pcp":{ "address1":"", "address2":"", "city":"", "fax":"", "federalTaxID":"", "firstName":"RHOESHAUN", "fullName":"", "groupNpi":"", "lastName":"ROULETTE", "licenseNumber":"", "npi":"", "organizationName":"", "phone":"", "providerID":"", "providerType":"", "region":"", "state":"", "zip":"" }, "memberId":"", "ssn":"", "subscriberId":"", "termDate":"" }, "medhokID":"1688", "notification":[  ], "priority":"Standard", "receivedDate":"", "requestType":"Part C Grievance", "status":"Open", "statusReason":"Pending Initial Review", "subCategory":"", "updateUser":{ "userId":"", "firstName":"", "lastName":"", "title":"", "email":"", "phone":"", "fax":"", "terminated":"", "accountLocked":"", "comment":"", "siteLocation":"", "lastLogin":"", "clientName":"" }, "validCase":"" } ] } }');   
             Test.setContinuationResponse('label', response);
             GrievanceAndAppealComponent_C_RT_HUM.processGreivanceAndAppealResponse(new List<string>{(String.valueOf(response))},null);
             comp.showPageMessage();
             Test.StopTest();
        }
    }
    
}