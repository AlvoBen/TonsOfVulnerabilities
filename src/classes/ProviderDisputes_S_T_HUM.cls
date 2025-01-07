/*****************************************************************************************************************************************************************************
    @Apex Class Name  : ProviderDisputes_S_T_HUM
    @Version          : 1.0
    @Function         : Test class for ProviderDisputes_S_HUM
******************************************************************************************************************************************************************************
Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Ajay Chakradhar                                  10/07/2019                    Original Version
******************************************************************************************************************************************************************************/
@isTest
private class ProviderDisputes_S_T_HUM
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
            calloutSettings.Name = 'ProviderCorrespondenceDisputes';
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
            Account accountObj = [Select id from account where LastName = 'Account1'];
            Test.StartTest();
            Continuation conti = (Continuation)ProviderDisputes_S_HUM.sendProviderDisputeCasesRequest(accountObj.Id);
       
            string requestStr = ProviderDisputes_S_HUM.buildRequestProviderDispute(accountObj.Id);
                        
            HttpResponse response = new HttpResponse();
            response.setBody('{"getProviderDisputesByCriteriaResponse": {"providerDisputes": [ { "allegation": "Update the case", "aor": { "firstName": "", "lastName": "", "type": "Other" }, "appealsStatusReason": "Pending Initial Review", "caseCategory": "Provider Dispute", "caseNumber": "D19126873199", "category": "Billing", "completionDate": "", "createDate": "2019-05-06T11:45:07-04:00", "createUser": { "userId": "HumanaTrain2", "firstName": "Humana", "lastName": "Train", "title": ".", "email": "training2medhok.com", "terminated": "false", "accountLocked": "true", "comment": "Training", "siteLocation": "Tampa", "lastLogin": "2019-10-17T14:47:47-04:00", "clientName": "Humana" }, "disputeType": "Complaint", "dueDate": "2019-06-04T23:59:59-04:00", "intakeDate": "2019-05-06T04:13:09-04:00", "intakeMode": "Mail", "manualReceivedDate": "", "medhokID": "39", "medicalClaims": [], "member": { "medhokId": "3140328", "firstName": "LIUSMILA", "lastName": "SIVAK", "middleName": "I", "fullName": "SIVAK, LIUSMILA I", "age": "75", "dateOfBirth": "1944-09-04", "daysLastDischarge": "-1", "memberId": "1007711483", "externalId": "1007711483", "gender": "Female", "subscriberId": "1007711483", "memberExtendedAttributes": {"attribute1": "N"} }, "notification": [ { "createdBy": "mvaghasia", "createdDate": "2019-09-06T10:47:11-04:00", "note": "Test of notes from devzone", "noteDate": "2019-07-01T00:00:00-04:00", "noteType": "General Note" }, { "createdBy": "mvaghasia", "createdDate": "2019-09-06T11:02:30-04:00", "note": "Test of notes from devzone", "noteDate": "2019-07-01T00:00:00-04:00", "noteType": "General Note" }, { "createdBy": "mvaghasia", "createdDate": "2019-09-07T02:56:20-04:00", "note": "Test of notes from devzone", "noteDate": "2019-07-01T00:00:00-04:00", "noteType": "General Note" }, { "createdBy": "mvaghasia", "createdDate": "2019-09-08T01:49:46-04:00", "note": "Test of notes from devzone", "noteDate": "2019-07-01T00:00:00-04:00", "noteType": "General Note" }, { "createdBy": "mvaghasia", "createdDate": "2019-09-25T02:54:58-04:00", "note": "Test of notes from devzone", "noteDate": "2019-07-01T00:00:00-04:00", "noteType": "General Note" } ], "priority": "Standard", "product": "Commercial Med - AZ", "receivedDate": "2019-05-06T04:13:09-04:00", "requestType": "Dispute", "status": "Open", "statusReason": "Pending Initial Review", "subCategory": "Anesthesia Units", "updateDate": "2019-10-17T00:01:24-04:00", "updateUser": { "userId": "mvaghasia", "firstName": "Mahendra", "lastName": "Vaghasia", "title": "Mr", "email": "mvaghasia@medhok.com", "terminated": "false", "accountLocked": "true", "comment": "test", "siteLocation": "Tampa", "lastLogin": "2019-11-04T15:20:28-05:00", "clientName": "0" }, "wsMemberEligExtAttribute": { "attribute1": "KY", "attribute3": "Humana Medical Plan, Inc.", "attribute4": "00", "attribute5": "H56986430", "attribute6": "00", "attribute7": "G", "attribute8": "MER", "attribute9": "LV", "attribute11": "X1745001", "attribute12": "N", "attribute13": "39053", "attribute14": "Y", "attribute19": "7910004359618", "attribute20": "7910004359618", "attribute21": "022566213", "attribute22": "565036899-0", "attribute23": "MCR", "attribute24": "MAPD", "attribute25": "LV", "attribute26": "MEDR", "attribute27": "SNC", "attribute28": "CCSNP", "attribute29": "N", "attribute30": "000614L", "attribute32": "N", "attribute33": "000" }, "wsMemberEligibility": { "medhokId": "5564663", "eligGroup": "HUMANA MEDICAL PLAN, INC.", "company": "Medicare", "companyLob": "SNP - CHRONIC HMO", "eligPlan": "MER RISK GATE CAP EXC", "medicareno": "7YP6QH2DE43", "effectivedate": "2018-12-01T00:00:00-05:00", "contractNumber": "H1036", "extId": "0b44efd38fadc613bbb4a31a9a121345" }, "dateOFServiceDesc": "", "wsProvider": { "organizationName": "", "firstName": "Robert", "lastName": "Meister", "fullName": "Meister, Robert", "npi": "1114992229", "federalTaxID": "364147437", "providerID": "PIM323559", "status": "", "providerDisplayName": "Robert Meister" } }, { "aor": { "address2": "Ste 204", "city": "Peoria", "firstName": "", "lastName": "", "phone": "3096831200", "state": "IL", "type": "Provider", "zip": "616145021" }, "appealsStatusReason": "Pending Initial Review", "caseCategory": "Provider Dispute", "caseNumber": "D19126794364", "category": "Non-Covered Service", "completionDate": "", "createDate": "2019-05-06T11:47:39-04:00", "createUser": { "userId": "HumanaTrain2", "firstName": "Humana", "lastName": "Train", "title": ".", "email": "training2medhok.com", "terminated": "false", "accountLocked": "true", "comment": "Training", "siteLocation": "Tampa", "lastLogin": "2019-10-17T14:47:47-04:00", "clientName": "Humana" }, "disputeType": "Post Service", "dueDate": "2019-06-05T04:13:09-04:00", "intakeDate": "2019-05-06T04:13:09-04:00", "intakeMode": "Mail", "manualReceivedDate": "", "medhokID": "40", "medicalClaims": [], "member": { "medhokId": "3140328", "firstName": "LIUSMILA", "lastName": "SIVAK", "middleName": "I", "fullName": "SIVAK, LIUSMILA I", "age": "75", "dateOfBirth": "1944-09-04", "daysLastDischarge": "-1", "memberId": "1007711483", "externalId": "1007711483", "gender": "Female", "subscriberId": "1007711483", "memberExtendedAttributes": {"attribute1": "N"} }, "notification": [ { "createdBy": "mvaghasia", "createdDate": "2019-09-25T02:42:32-04:00", "note": "bhjkololololol", "noteDate": "2019-07-01T00:00:00-04:00", "noteType": "General Note" }], "priority": "Standard", "product": "Medicare", "receivedDate": "2019-05-06T04:13:09-04:00", "requestType": "Dispute", "status": "Open", "statusReason": "Pending Initial Review", "subCategory": "Cosmetic", "updateDate": "2019-05-06T11:47:42-04:00", "updateUser": { "userId": "HumanaTrain2", "firstName": "Humana", "lastName": "Train", "title": ".", "email": "training2medhok.com", "terminated": "false", "accountLocked": "true", "comment": "Training", "siteLocation": "Tampa", "lastLogin": "2019-10-17T14:47:47-04:00", "clientName": "Humana" }, "wsMemberEligExtAttribute": { "attribute1": "KY", "attribute3": "Humana Medical Plan, Inc.", "attribute4": "01", "attribute5": "007119021", "attribute6": "01", "attribute7": "G", "attribute8": "POSD", "attribute9": "EM", "attribute11": "675717", "attribute12": "N", "attribute13": "36355", "attribute14": "N", "attribute15": "HMP", "attribute16": "KY-LOU", "attribute17": "DMDR", "attribute18": "HDMEDICARE", "attribute19": "7910004359618", "attribute20": "7910004359618", "attribute21": "0504070822401340", "attribute22": "007119021 0", "attribute24": "MDCHMO", "attribute25": "EM", "attribute26": "DMDR", "attribute27": "DFM" }, "wsMemberEligibility": { "medhokId": "5564665", "medicareno": "7YP6QH2DE43", "effectivedate": "2019-01-01T00:00:00-05:00" }, "dateOFServiceDesc": "", "wsProvider": { "organizationName": "", "firstName": "Robert", "lastName": "Meister", "fullName": "Meister, Robert", "npi": "1114992229", "federalTaxID": "364147437", "providerID": "PIM323559", "status": "", "providerDisplayName": "Robert Meister" } } ]}}');
            Test.setContinuationResponse('label', response);
            ProviderDisputes_S_HUM.processResponse((ProviderDisputesResponse_H_HUM)System.JSON.deserialize(response.getbody(),ProviderDisputesResponse_H_HUM.class));
            ProviderDisputes_S_HUM.processProviderDisputeCasesResponse(new List<string>{(String.valueOf(response))},null);
            Test.StopTest();
        }
    }
    
    /*
    * Method to create the batch job
    * @param        None
    * @return       None
    */
    private static testmethod void testBatch1()
    {
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        system.runAs(testUser)
        { 
            Account accountObj = [Select id from account where LastName = 'Account1'];
            Test.StartTest();
            Continuation conti = (Continuation)ProviderDisputes_S_HUM.sendProviderDisputeCasesRequest(accountObj.Id);
       
            string requestStr = ProviderDisputes_S_HUM.buildRequestProviderDispute(accountObj.Id);
                        
            HttpResponse response = new HttpResponse();
            response.setBody('{"getProviderDisputesByCriteriaResponse": {"providerDisputes": [ { "allegation": "", "aor": { "firstName": "", "lastName": "", "type": "" }, "appealsStatusReason": "", "caseCategory": "", "caseNumber": "", "category": "", "completionDate": "2019-05-06T11:47:39-04:00", "createDate": "", "createUser": { "userId": "", "firstName": "", "lastName": "", "title": "", "email": "", "terminated": "", "accountLocked": "", "comment": "", "siteLocation": "", "lastLogin": "", "clientName": "" }, "disputeType": "", "dueDate": "", "intakeDate": "", "intakeMode": "", "manualReceivedDate": "", "medhokID": "", "medicalClaims": [], "member": { "medhokId": "", "firstName": "", "lastName": "", "middleName": "", "fullName": "", "age": "", "dateOfBirth": "", "daysLastDischarge": "", "memberId": "", "externalId": "", "gender": "", "subscriberId": "", "memberExtendedAttributes": {"attribute1": ""} }, "notification": [ { "createdBy": "", "createdDate": "", "note": "Test of notes from devzone", "noteDate": "2019-07-01T00:00:00-04:00", "noteType": "General Note" }, { "createdBy": "mvaghasia", "createdDate": "2019-09-06T11:02:30-04:00", "note": "Test of notes from devzone", "noteDate": "2019-07-01T00:00:00-04:00", "noteType": "General Note" }, { "createdBy": "mvaghasia", "createdDate": "2019-09-07T02:56:20-04:00", "note": "Test of notes from devzone", "noteDate": "2019-07-01T00:00:00-04:00", "noteType": "General Note" }, { "createdBy": "mvaghasia", "createdDate": "2019-09-08T01:49:46-04:00", "note": "Test of notes from devzone", "noteDate": "2019-07-01T00:00:00-04:00", "noteType": "General Note" }, { "createdBy": "mvaghasia", "createdDate": "2019-09-25T02:54:58-04:00", "note": "Test of notes from devzone", "noteDate": "2019-07-01T00:00:00-04:00", "noteType": "General Note" } ], "priority": "Standard", "product": "Commercial Med - AZ", "receivedDate": "2019-05-06T04:13:09-04:00", "requestType": "Dispute", "status": "Open", "statusReason": "Pending Initial Review", "subCategory": "Anesthesia Units", "updateDate": "2019-10-17T00:01:24-04:00", "updateUser": { "userId": "mvaghasia", "firstName": "Mahendra", "lastName": "Vaghasia", "title": "Mr", "email": "mvaghasia@medhok.com", "terminated": "false", "accountLocked": "true", "comment": "test", "siteLocation": "Tampa", "lastLogin": "2019-11-04T15:20:28-05:00", "clientName": "0" }, "wsMemberEligExtAttribute": { "attribute1": "KY", "attribute3": "Humana Medical Plan, Inc.", "attribute4": "00", "attribute5": "H56986430", "attribute6": "00", "attribute7": "G", "attribute8": "MER", "attribute9": "LV", "attribute11": "X1745001", "attribute12": "N", "attribute13": "39053", "attribute14": "Y", "attribute19": "7910004359618", "attribute20": "7910004359618", "attribute21": "022566213", "attribute22": "565036899-0", "attribute23": "MCR", "attribute24": "MAPD", "attribute25": "LV", "attribute26": "MEDR", "attribute27": "SNC", "attribute28": "CCSNP", "attribute29": "N", "attribute30": "000614L", "attribute32": "N", "attribute33": "000" }, "wsMemberEligibility": { "medhokId": "5564663", "eligGroup": "HUMANA MEDICAL PLAN, INC.", "company": "Medicare", "companyLob": "SNP - CHRONIC HMO", "eligPlan": "MER RISK GATE CAP EXC", "medicareno": "7YP6QH2DE43", "effectivedate": "2018-12-01T00:00:00-05:00", "contractNumber": "H1036", "extId": "0b44efd38fadc613bbb4a31a9a121345" }, "dateOFServiceDesc": "2019-10-17T14:47:47-04:00", "wsProvider": { "organizationName": "", "firstName": "Robert", "lastName": "Meister", "fullName": "Meister, Robert", "npi": "", "federalTaxID": "364147437", "providerID": "PIM323559", "status": "", "providerDisplayName": "" } }, { "aor": { "address2": "Ste 204", "city": "Peoria", "firstName": "", "lastName": "", "phone": "3096831200", "state": "IL", "type": "Provider", "zip": "616145021" }, "appealsStatusReason": "Pending Initial Review", "caseCategory": "", "caseNumber": "", "category": "Non-Covered Service", "completionDate": "2019-05-06T11:47:39-04:00", "createDate": "2019-05-06T11:47:39-04:00", "createUser": { "userId": "HumanaTrain2", "firstName": "Humana", "lastName": "Train", "title": ".", "email": "training2medhok.com", "terminated": "false", "accountLocked": "true", "comment": "Training", "siteLocation": "Tampa", "lastLogin": "2019-10-17T14:47:47-04:00", "clientName": "Humana" }, "disputeType": "Post Service", "dueDate": "2019-06-05T04:13:09-04:00", "intakeDate": "2019-05-06T04:13:09-04:00", "intakeMode": "Mail", "manualReceivedDate": "", "medhokID": "", "medicalClaims": [], "member": { "medhokId": "3140328", "firstName": "LIUSMILA", "lastName": "SIVAK", "middleName": "I", "fullName": "SIVAK, LIUSMILA I", "age": "75", "dateOfBirth": "1944-09-04", "daysLastDischarge": "-1", "memberId": "1007711483", "externalId": "1007711483", "gender": "Female", "subscriberId": "1007711483", "memberExtendedAttributes": {"attribute1": "N"} }, "notification": [ { "createdBy": "mvaghasia", "createdDate": "2019-09-25T02:42:32-04:00", "note": "bhjkololololol", "noteDate": "2019-07-01T00:00:00-04:00", "noteType": "General Note" }], "priority": "", "product": "", "receivedDate": "", "requestType": "", "status": "", "statusReason": "", "subCategory": "", "updateDate": "2019-05-06T11:47:42-04:00", "updateUser": { "userId": "HumanaTrain2", "firstName": "", "lastName": "", "title": ".", "email": "training2medhok.com", "terminated": "false", "accountLocked": "true", "comment": "Training", "siteLocation": "Tampa", "lastLogin": "2019-10-17T14:47:47-04:00", "clientName": "" }, "wsMemberEligExtAttribute": { "attribute1": "KY", "attribute3": "Humana Medical Plan, Inc.", "attribute4": "01"}, "wsMemberEligibility": { "medhokId": "", "medicareno": "", "effectivedate": "" }, "dateOFServiceDesc": "2019-10-17T14:47:47-04:00", "wsProvider": { "organizationName": "", "firstName": "", "lastName": "", "fullName": "", "npi": "", "federalTaxID": "", "providerID": "", "status": "", "providerDisplayName": "" } } ]}}');
            Test.setContinuationResponse('label', response);
            ProviderDisputes_S_HUM.processResponse((ProviderDisputesResponse_H_HUM)System.JSON.deserialize(response.getbody(),ProviderDisputesResponse_H_HUM.class));
            ProviderDisputes_S_HUM.processProviderDisputeCasesResponse(new List<string>{(String.valueOf(response))},null);
            Test.StopTest();
        }
    }
    
}