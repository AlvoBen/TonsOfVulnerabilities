/******************************************************************************************************************
Apex class Name    : PharmacyRXClaimsSummary_T_HUM
Version            : 1.0
Function           : This is Test Class covered C,S,D classes related to Pharmacy Rx Claims Summary information.
Created On         : July 14 2020
*******************************************************************************************************************
Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Shailesh Mali                                      07/14/2020                 Original Version - REQ - 891651 -- PR00094920- MF 1- Rx Claims- Pharmacy (Rx) Claims Summary page (RxXP) 
* Prashant Moghe									 08/20/2021				    US- 1928764 - T1PRJ0002736 - 2021 MF6 - Security  - CR AEP readiness - Information Security Issues
*******************************************************************************************************************/

@isTest
private class PharmacyRXClaimsSummary_T_HUM {
 @testSetup static void prepareTestData() 
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('RxClaimSummaryUser1','Customer Care Specialist');
        insert oCCSUser;
        system.assertEquals(oCCSUser.LastName,'test RxClaimSummaryUser1');
        
        HUM_Webservice_Callout__c AccumsServiceDetails = New HUM_Webservice_Callout__c(Name = 'RxClaimSummary_GetClaims',Certificate_Name__c = 'Humana_GuidanceCare_Surge' ,Active__c = true,End_Point_URL__c ='https://dev-crmservicews.humana.com/PharmacyClaimsService/GetClaims' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
        insert AccumsServiceDetails;
        system.assert(HUM_Webservice_Callout__c.getValues('RxClaimSummary_GetClaims').Name == 'RxClaimSummary_GetClaims');
        
        System.runAs (oCCSUser) 
        {
            Account objAcc = HUMTestDataHelperTest.getAccount(); 
            objAcc.FirstName = 'Test';
            objAcc.LastName = 'Account';
            objAcc.Tenant_Id__c = '00';
            objAcc.Enterprise_ID__c = '1005577525';
            objAcc.General_Account__c = true;
            objAcc.Mbr_Gen_Key__c = '0000104665522';
            objAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();
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
            
             Member_ID__c objMemberID1 = new Member_ID__c();
            objMemberID1.policy_member__c = objPolMem.id;
            objMemberID1.policy__c = objpol.Id;
            objMemberID1.Name = '12345';
            objMemberID1.Member_Card_External_ID__c = 'ODS12345'; 
            objMemberID1.Member_Dependent_Code__c  = '03';
            objMemberID1.Type__c = 'SSN';        
            insert objMemberID1;
            system.assertequals('12345',objMemberID1.Name );
        }
    }
    @isTest
    Static void testRxClaimSummary()
    {
        User testUser = [Select Id, Name,LastName from User where LastName ='test RxClaimSummaryUser1'];
        Policy_Member__c policyObj = [select id, name, Effective_Date__c, member__c, member__r.Mbr_Gen_Key__c,member__r.Enterprise_ID__C ,end_date__c, policy__c, policy__r.Group_Name__r.source_platform_code__c, policy__r.source_cust_cov_key__c, policy__r.Coverage_Plan_Effective_Date__c, policy__r.Coverage_Plan_End_Date__c from policy_member__c where name = 'testpolmem11' limit 1];
        
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.PharmacyRXClaimsSummary_VF_HUM);
            ApexPages.StandardController scon = New ApexPages.StandardController(policyObj);
            ApexPages.currentPage().getParameters().put('Id',policyObj.Id);
            PharmacyRXClaimsSummary_C_HUM c = new PharmacyRXClaimsSummary_C_HUM();
            Continuation con = (Continuation)PharmacyRXClaimsSummary_C_HUM.GetClaims(policyObj.member__r.Mbr_Gen_Key__c,policyObj.Effective_Date__c,policyObj.end_date__c);
            con.continuationMethod = 'processPharmacyRXClaimSummaryResponse';
            HttpResponse resPharmacyRXClaimSummary = New Httpresponse();
            string claimSummaryResponse2 = '[{"ActualProcessDate": "2020-05-26T13:10:08","AuthorizationNumber":"405477696291","ClaimID":"405477696291","PatientPaid":"10.0","ClaimStatus":"PAID","NDC": "68382011414","RxNumber": "000007674013","PriorAuthorizationNumberSubmitted": "","RxQuantity": 90,"RxDaySupply": "90", "RxClaimDrug":{"DrugName": "risperidone 1 mg tablet","GCN": "16136"}, "RxClaimPatient":{"CustomerId": "0320","ClientId": "00006"},"RxClaimPharmacy": {"PharmacyName": "WALMART PHARMACY #101461"}}]';
            resPharmacyRXClaimSummary.setBody(claimSummaryResponse2);
			System.assertEquals(claimSummaryResponse2,resPharmacyRXClaimSummary.getBody());
            resPharmacyRXClaimSummary.setStatusCode(200);
            Map<String, HTTPRequest> mapDetails = con.getRequests();
            String reqType = '';
            for(String s : mapDetails.keyset())
            {
                reqType = s;
            }
            List<String>labelGetmember = new List<String>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,resPharmacyRXClaimSummary);
            PharmacyRXClaimsSummary_C_HUM.processPharmacyRXClaimSummaryResponse(labelGetmember,con);
           
            Test.stopTest();
        }
    }      
    
    @isTest
    static void textNegativeRXClaimSummary(){
        User testUser = [Select Id, Name,LastName from User where LastName ='test RxClaimSummaryUser1'];
        Policy_Member__c policyObj = [select id, name, Effective_Date__c, member__c, member__r.Mbr_Gen_Key__c,member__r.Enterprise_ID__C ,end_date__c, policy__c, policy__r.Group_Name__r.source_platform_code__c, policy__r.source_cust_cov_key__c, policy__r.Coverage_Plan_Effective_Date__c, policy__r.Coverage_Plan_End_Date__c from policy_member__c where name = 'testpolmem11' limit 1];
        
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.PharmacyRXClaimsSummary_VF_HUM);
            ApexPages.StandardController scon = New ApexPages.StandardController(policyObj);
            ApexPages.currentPage().getParameters().put('Id',policyObj.Id);
            PharmacyRXClaimsSummary_C_HUM c = new PharmacyRXClaimsSummary_C_HUM();
            Continuation con = (Continuation)PharmacyRXClaimsSummary_C_HUM.GetClaims('',policyObj.Effective_Date__c,policyObj.end_date__c);
            con.continuationMethod = 'processPharmacyRXClaimSummaryResponse';
            HttpResponse resPharmacyRXClaimSummary = New Httpresponse();
            string claimSummaryResponse2 = '[{"ActualProcessDate": "1234567890","AuthorizationNumber":"405477696291","ClaimID":405477696291,"PatientPaid":"10.0","ClaimStatus":"PAID","NDC": "68382011414","RxNumber": "000007674013","PriorAuthorizationNumberSubmitted": "","RxQuantity": 90,"RxDaySupply": "90", "RxClaimDrug":{"DrugName": "risperidone 1 mg tablet","GCN": "16136"}, "RxClaimPatient":{"CustomerId": "0320","ClientId": "00006"},"RxClaimPharmacy": {"PharmacyName": "WALMART PHARMACY #101461"}}]';
            String claimSummaryResponse3 = '{ "Fault":{ "faultcode":"Code-67.8.1", "faultstring":"","detail":{ "transactionInfo":{ "transaction-id":"207260733", "transaction-client":"127.0.0.1", "URL-in":"http://127.0.0.1:8316/PharmacyClaimsService/GetClaims", "processor-name":"JSONServicesGateway", "processor-type":"Multiprotocol Gateway" },  "errorInfo":{ "error-code":"0x00d30003", "error-headers":"HTTP/1.1 500 Internal Server Error", "error-message":"Member not found", "error-protocol-reason-phrase":"", "error-protocol-response":"0", "error-subcode":"0x00d30003" } } } }';
            resPharmacyRXClaimSummary.setBody(claimSummaryResponse2);
            resPharmacyRXClaimSummary.setStatusCode(500);
            Map<String, HTTPRequest> mapDetails = con.getRequests();
            String reqType = '';
            for(String s : mapDetails.keyset())
            {
                reqType = s;
            }
            List<String>labelGetmember = new List<String>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,resPharmacyRXClaimSummary);
            PharmacyRXClaimsSummary_C_HUM.processPharmacyRXClaimSummaryResponse(labelGetmember,con);
            PharmacyRXClaimsSummary_S_HUM.insertFaultExceptionToErrorLog(claimSummaryResponse3,'500','ClassName','Method name');
            PharmacyRXClaimsSummary_S_HUM.returnExceptionMesssage('99');
			System.assertEquals('InternalErrorUnknown', PharmacyRXClaimsSummary_S_HUM.returnExceptionMesssage('99'));
            PharmacyRXClaimsSummary_S_HUM.returnExceptionMesssage('302');
            PharmacyRXClaimsSummary_S_HUM.returnExceptionMesssage('500');
            PharmacyRXClaimsSummary_S_HUM.returnExceptionMesssage('2002');
            Test.stopTest();
        }
    } 
    
     @isTest
    static void textNegativeRXClaimSummary1(){
        User testUser = [Select Id, Name,LastName from User where LastName ='test RxClaimSummaryUser1'];
        Policy_Member__c policyObj = [select id, name, Effective_Date__c, member__c, member__r.Mbr_Gen_Key__c,member__r.Enterprise_ID__C ,end_date__c, policy__c, policy__r.Group_Name__r.source_platform_code__c, policy__r.source_cust_cov_key__c, policy__r.Coverage_Plan_Effective_Date__c, policy__r.Coverage_Plan_End_Date__c from policy_member__c where name = 'testpolmem11' limit 1];
        
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.PharmacyRXClaimsSummary_VF_HUM);
            ApexPages.StandardController scon = New ApexPages.StandardController(policyObj);
            ApexPages.currentPage().getParameters().put('Id',policyObj.Id);
            PharmacyRXClaimsSummary_C_HUM c = new PharmacyRXClaimsSummary_C_HUM();
            Continuation con = (Continuation)PharmacyRXClaimsSummary_C_HUM.GetClaims('',policyObj.Effective_Date__c,policyObj.end_date__c);
            con.continuationMethod = 'processPharmacyRXClaimSummaryResponse';
            HttpResponse resPharmacyRXClaimSummary = New Httpresponse();
            String claimSummaryResponse2 = '{ "Fault":{ "faultcode":"Code-67.8.1", "faultstring":"","detail":{ "transactionInfo":{ "transaction-id":"207260733", "transaction-client":"127.0.0.1", "URL-in":"http://127.0.0.1:8316/PharmacyClaimsService/GetClaims", "processor-name":"JSONServicesGateway", "processor-type":"Multiprotocol Gateway" },  "errorInfo":{ "error-code":"0x00d30003", "error-headers":"HTTP/1.1 500 Internal Server Error", "error-message":"Member not found", "error-protocol-reason-phrase":"", "error-protocol-response":"0", "error-subcode":"0x00d30003" } } } }';
            resPharmacyRXClaimSummary.setBody(claimSummaryResponse2);
            resPharmacyRXClaimSummary.setStatusCode(500);
            Map<String, HTTPRequest> mapDetails = con.getRequests();
            String reqType = '';
            for(String s : mapDetails.keyset())
            {
                reqType = s;
            }
            List<String>labelGetmember = new List<String>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,resPharmacyRXClaimSummary);
            PharmacyRXClaimsSummary_C_HUM.processPharmacyRXClaimSummaryResponse(labelGetmember,con);
            PharmacyRXClaimsSummary_S_HUM.insertFaultExceptionToErrorLog(claimSummaryResponse2,'500','ClassName','MethodName');
            PharmacyRXClaimsSummary_S_HUM.returnExceptionMesssage('99');
			System.assertEquals('InternalErrorUnknown', PharmacyRXClaimsSummary_S_HUM.returnExceptionMesssage('99'));
            PharmacyRXClaimsSummary_S_HUM.returnExceptionMesssage('302');
            PharmacyRXClaimsSummary_S_HUM.returnExceptionMesssage('500');
            PharmacyRXClaimsSummary_S_HUM.returnExceptionMesssage('2002');
            Test.stopTest();
        }
    }  
}