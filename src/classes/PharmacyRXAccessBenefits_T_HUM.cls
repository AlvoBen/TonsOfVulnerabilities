/**************************************************************************** 
Apex Class Name  : PharmacyRXAccessBenefits_T_HUM   
Version          : 1.0  
Created Date     : May 6, 2021
Function         : This Class covers the code coverage for PharmacyRXAccessBenefits_C_HUM
******************************************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*------------------------------------------------------------------------------------------------------------
* Suraj Patil     				               05/06/2021                 REQ - 1003582 - 1011772 - 1694036
***************************************************************************************************/
@isTest
public with sharing class PharmacyRXAccessBenefits_T_HUM {
    public static Integer RESPONSE_CODE = 200;
    public static String RESPONSE_STATUS = 'Complete';
    public static String RESPONSE_BODY = '{"KMDSearchResults": {"Documents":    [{"FileType": "pdf","Href": "https://test-dctm.humana.com/Mentor/Web/v.aspx/PaidTimeOff(PTO)?chronicleID=090009298081dcc3&dl=&searchID=km-search-link&row=0","Title": "Paid Time Off (PTO)"},{"FileType": "doc","Href": "https://test-dctm.humana.com/Mentor/Web/v.aspx/B-54601?chronicleID=090009298081d240&dl=&searchID=km-search-link&row=0","Title": "B-54601"},{"FileType": "","Href": "https://test-dctm.humana.com/Mentor/Web/v.aspx/?chronicleID=&dl=&searchID=km-search-link&row=0","Title": ""},{"FileType": "html","Href": "https://test-dctm.humana.com/Mentor/Web/v.aspx/test_4_coveo_topic?chronicleID=0900092980d96d1f&dl=&searchID=km-search-link&row=0","Title": "test_4_coveo_topic"}],"Error":    {"ErrorCode": "","ErrorMessage": "","Severity": ""},"NumberRetrieved": 4,"SearchReferenceID": "0|viv_IX37M1","TotalDocumentsFound": 4}}';

     @testSetup static void prepareTestData(){
        
        User oCCSUser = HUMTestDataHelperTest.createTestUser('RxClaimSummaryUser1','Customer Care Specialist');
        insert oCCSUser;
        system.assertEquals(oCCSUser.LastName,'test RxClaimSummaryUser1');
        
        System.runAs (oCCSUser) 
        {
            HUM_Webservice_Callout__c accessBenifitService = new HUM_Webservice_Callout__c();
            accessBenifitService.name = 'KMDocumentServiceREST_DoSearch';        
            accessBenifitService.Active__c = true;
            accessBenifitService.Certificate_Name__c = 'testts';
            accessBenifitService.End_Point_URL__c = 'https://dev-crmservicews.humana.com/KMDocumentServiceREST/DoSearch';
            accessBenifitService.Timeout__c = '120000';
            accessBenifitService.Content_Type__c = 'application/json';
            accessBenifitService.Request_Type__c = 'POST';
            insert accessBenifitService;
            system.assert(accessBenifitService.id != null);
            system.assert(accessBenifitService.name == 'KMDocumentServiceREST_DoSearch');
            
            Account objAcc = new Account(); 
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
            
            Policy__c objpol = new Policy__c();
            objpol.Name='testPolicy';
            objpol.Group_Name__c = objAcc.Id;
            objpol.Major_LOB__c = 'MER';
            objpol.Source_Cust_Cov_Key__c = '123456789';
            objpol.Product__c = oProd.Id;
            objpol.Product__r = oProd;
            objpol.Platform__c = 'LV';
            objpol.Client_Number__c = '57';
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
            objPolMem.Relationship_Code__c = '00';           
            insert objPolMem;
            system.assertEquals(objPolMem.Relationship__c, 'Self');
			
			HUMCertificateURL__c MentorCertificateURL = new HUMCertificateURL__c(Name = 'MentorHomeURL',CertificateListURL__c = 'http://test-mentorsearch.humana.com/web/mentor.aspx');
            insert MentorCertificateURL;
            system.assert(HUMCertificateURL__c.getValues('MentorHomeURL') != NULL);
        }
    }
    
    static testMethod void testAccessBenifitsRequest(){
        
        User testUser = [Select Id, Name,LastName from User where LastName ='test RxClaimSummaryUser1'];
        Policy_Member__c oPolMem = [select id, name, Effective_Date__c, member__c, member__r.Mbr_Gen_Key__c,member__r.Enterprise_ID__C ,end_date__c, policy__c, policy__r.Group_Name__r.source_platform_code__c, policy__r.source_cust_cov_key__c, policy__r.Coverage_Plan_Effective_Date__c, policy__r.Coverage_Plan_End_Date__c from policy_member__c where name = 'testpolmem11' limit 1];
        
        System.runAs(testUser)
        {
            Test.startTest();
            
            PharmacyRXAccessBenefits_C_HUM prc=new PharmacyRXAccessBenefits_C_HUM();
            String policyMemId = oPolMem.id;
            string searchId = '324'; 
            integer resultsPerPage =10; 
            Integer pageNo = 1;
            boolean firstTimeLoad = Boolean.valueOf('true');
            Continuation con = (Continuation)PharmacyRXAccessBenefits_C_HUM.callAccessBenefitMentorService(policyMemId);
            con.continuationMethod = 'invokeAccessBenefitsMentorService';
            //con.continuationMethod = 'processAccessBenefitMentorService';
            HTTPRequest req = new HTTPRequest();
            PharmacyRXAccessBenifitsMockTest_T_HUM fakeResponse = new PharmacyRXAccessBenifitsMockTest_T_HUM();
            Test.setMock(HttpCalloutMock.class, fakeResponse);
            Map<String, HTTPRequest> mapDetails = con.getRequests();
            String reqType = '';
            for(String s : mapDetails.keyset())
            {
                reqType = s;
            }
            List<String>labelGetmember = new List<String>();
            labelGetmember.add(reqType);
            
            Test.setContinuationResponse(reqType,fakeResponse.respond(req));
            PharmacyRXAccessBenefits_C_HUM.processAccessBenefitMentorService(labelGetmember,con);
            HTTPResponse response = fakeResponse.respond(req);
            System.assertEquals(RESPONSE_CODE, response.getStatusCode());
            System.assertEquals(RESPONSE_STATUS, response.getStatus());
            PharmacyRXAccessBenefits_Response_HUM pp = new PharmacyRXAccessBenefits_Response_HUM();  
            //PharmacyRXAccessBenefits_Response_HUM.parse(response.getBody());
            //Test.stopTest();
            
        } 
    }
    
    static testMethod void testAccessBenifitRequestCatch(){
        User testUser = [Select Id, Name,LastName from User where LastName ='test RxClaimSummaryUser1'];
        Policy_Member__c oPolMem = [select id, name, Effective_Date__c, member__c, member__r.Mbr_Gen_Key__c,member__r.Enterprise_ID__C ,end_date__c, policy__c, policy__r.Group_Name__r.source_platform_code__c, policy__r.source_cust_cov_key__c, policy__r.Coverage_Plan_Effective_Date__c, policy__r.Coverage_Plan_End_Date__c from policy_member__c where name = 'testpolmem11' limit 1];
        
        System.runAs(testUser)
        {
            Test.startTest();
            string searchId = '324'; 
            integer resultsPerPage =10; 
            Integer pageNo = 1;
            boolean firstTimeLoad = Boolean.valueOf('true');
            PharmacyRXAccessBenefits_C_HUM prc=new PharmacyRXAccessBenefits_C_HUM();            
            String policyMemId = oPolMem.id;
            HUM_Webservice_Callout__c oSvcHeaderPayment = [Select Request_Type__c,End_Point_URL__c,Certificate_Name__c,Timeout__c,Content_Type__c From HUM_Webservice_Callout__c where name='KMDocumentServiceREST_DoSearch' Limit 1];
            oSvcHeaderPayment.Name = 'ClaimDtailsTest';
            update oSvcHeaderPayment;
            system.assertEquals(oSvcHeaderPayment.Name,'ClaimDtailsTest');
            PharmacyRXAccessBenefits_C_HUM.callAccessBenefitMentorService(policyMemId);
            Test.stopTest();
        }             
    } 
	
     static testMethod void testAccessBenifitRequestError(){
        
        User testUser = [Select Id, Name,LastName from User where LastName ='test RxClaimSummaryUser1'];
        Policy_Member__c oPolMem = [select id, name, Effective_Date__c, member__c, member__r.Mbr_Gen_Key__c,member__r.Enterprise_ID__C ,end_date__c, policy__c, policy__r.Group_Name__r.source_platform_code__c, policy__r.source_cust_cov_key__c, policy__r.Coverage_Plan_Effective_Date__c, policy__r.Coverage_Plan_End_Date__c from policy_member__c where name = 'testpolmem11' limit 1];
        
        System.runAs(testUser)
        {
            Test.startTest();
            
            PharmacyRXAccessBenefits_C_HUM prc=new PharmacyRXAccessBenefits_C_HUM();
            String policyMemId = oPolMem.id;
            string searchId = '324'; 
            integer resultsPerPage =10; 
            Integer pageNo = 1;
            boolean firstTimeLoad = Boolean.valueOf('true');
            Continuation con = (Continuation)PharmacyRXAccessBenefits_C_HUM.callAccessBenefitMentorService(policyMemId);
            con.continuationMethod = 'invokeAccessBenefitsMentorService';
            HTTPRequest req = new HTTPRequest();
            PharmacyRXAccessBenifitsMockTest_T_HUM fakeResponse = new PharmacyRXAccessBenifitsMockTest_T_HUM();
            Test.setMock(HttpCalloutMock.class, fakeResponse);
            Map<String, HTTPRequest> mapDetails = con.getRequests();
            String reqType = '';
            for(String s : mapDetails.keyset())
            {
                reqType = s;
            }
            List<String>labelGetmember = new List<String>();
            labelGetmember.add(reqType);
            
            Test.setContinuationResponse(reqType,fakeResponse.respondError(req));
            PharmacyRXAccessBenefits_C_HUM.processAccessBenefitMentorService(labelGetmember,con);
            HTTPResponse response = fakeResponse.respondError(req);
            PharmacyRXAccessBenefits_Response_HUM pp = new PharmacyRXAccessBenefits_Response_HUM();  
            //PharmacyRXAccessBenefits_Response_HUM.parse(response.getBody());
            Test.stopTest();
             
        } 
    } 
}