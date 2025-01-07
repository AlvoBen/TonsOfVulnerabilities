/************************************************************************************************************************************************************************************       
Apex class Name      : RxConnect_T_HUM
Created Date         : 11/11/2019
Function             : Test Class for RxConnect
Coverage  			 : 94%

**Modification Log
* Developer Name          Code Review #         Date                       Description
************************************************************************************************************************************************************************************
* Satyam Malviya								11/11/2019				   REQ-404584 - Original Version - Created new class for RX connect functionality
************************************************************************************************************************************************************************************/
@isTest
private class RxConnect_T_HUM 
{
    @testSetUp static void setupTestData()
    {
         User objUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
         insert objUser;
         System.assertEquals(objUser.lastname=='test User1',true);
         System.runAs(objUser)
         {

             //Insert Member Account
             Account objMemAccount = HUMTestDataHelperTest.getAccount();
             objMemAccount.FirstName = 'Test FName';
             objMemAccount.LastName = 'LName';
             objMemAccount.source_platform_code__c = 'EM';
             objMemAccount.Enterprise_Id__c = String.valueof(2000);
             objMemAccount.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
             objMemAccount.Birthdate__c = String.valueOf(System.today().month() + '/' + System.today().day() + '/' + System.today().year());
             insert objMemAccount;
             System.assert(objMemAccount.LastName == 'LName');
             
             //Medical Product
             Product2 objMedProduct = new Product2();
             objMedProduct.Name ='MED';
             objMedProduct.Product_Type__c = 'Med';
             objMedProduct.productCode = 'MED';
             objMedProduct.Sold_Product_Key_Value__c = 'MC3HH016';
             insert objMedProduct;
             System.assert(objMedProduct.Product_Type__c == 'Med');
             
             //Medical Policy
             Policy__c oMedicalPolicy = HUMTestDataHelperTest.getPolicy();
             oMedicalPolicy.Name = 'Medical Product';
             oMedicalPolicy.source_cust_cov_key__c = '12345678901234567890';
             oMedicalPolicy.Group_Name__c = objMemAccount.Id;
             oMedicalPolicy.Product__c = objMedProduct.Id;
             oMedicalPolicy.Major_LOB__c = 'EPO';
             insert oMedicalPolicy;
             System.assert(oMedicalPolicy.Name == 'Medical Product');
             
             //Medical Policy Member
             Policy_Member__c objMedPolMem = HUMTestDataHelperTest.getPolicyMember();
             objMedPolMem.Member__c = objMemAccount.Id;
             objMedPolMem.Name = 'Test Policy';
             objMedPolMem.Policy__c = oMedicalPolicy.Id;
             objMedPolMem.Relationship__c = 'self';
             objMedPolMem.end_date__c = '12/31/2015';
             objMedPolMem.Effective_Date__c = '01/01/2015';
             objMedPolMem.Policy_Member_External_ID__c = '00|ODS|123456789|89|89|02-01-2001';
             insert objMedPolMem;
             System.assert(objMedPolMem.Relationship__c == 'self');
             
             Member_ID__c objMemID = new Member_ID__c();
             objMemID.Name = 'Test Member ID';
             objMemID.Policy__c = oMedicalPolicy.Id;
             objMemID.Type__c = 'Member-ID-Base';
             objMemID.Policy_Member__c = objMedPolMem.Id;
             insert objMemID;
             
             HUMCertificateURL__c RxConnectPro = new HUMCertificateURL__c();
             RxConnectPro.Name = 'RXConnectProURL';
             RxConnectPro.CertificateListURL__c = 'https://rxconnect.apps.qa-cf.humana.com/?mid=';
             insert RxConnectPro;
         }  
    }
    @isTest
    static void testEncrypt()
    {
        User testUser = [Select Id, Name from User where LastName = 'test User1'];
        policy_member__c policyObj = [select id, name, member__c, end_date__c, Effective_Date__c from policy_member__c where name = 'Test Policy' limit 1];
        Member_ID__c memID = [select id, Name, Type__c, policy_member__c from Member_ID__c where Type__c = 'Member-ID-Base' and Name = 'Test Member ID'];
        System.runAs(testUser)
        {
			HUM_Webservice_Callout__c EncryptServiceDetails = new HUM_Webservice_Callout__c(Name = 'EncryptService', Certificate_Name__c = 'Test_Certificate', Active__c = true, End_Point_URL__c = 'https://int-crmservicews.humana.com/EncryptRestService_V1.0/encrypt', Timeout__c = '12000', Request_Type__c = 'POST', Content_Type__c = 'application/json');
			insert EncryptServiceDetails;
			System.assert(HUM_Webservice_Callout__c.getValues('EncryptService') != NULL);
			 
			HUMCertificateURL__c certiURL = New HUMCertificateURL__c(Name = 'RXLookUpURL', CertificateListURL__c = 'http://test-hss.humana.com/RxLookup/Default.aspx?processcode=2179&hm=');
			insert certiURL;
			System.assert(certiURL.Name == 'RXLookUpURL' );
			
			HUMCertificateURL__c certiURLnew = New HUMCertificateURL__c(Name = 'RXConnectProURL', CertificateListURL__c = 'https://rxconnect.apps.qa-cf.humana.com/?mid=');
			insert certiURLnew;
			System.assert(certiURLnew.Name == 'RXConnectProURL' );
			
			Endpoint_Url_HUM__c endpoint = New Endpoint_Url_HUM__c(Name = 'SSO_LOGINURL', Remote_Url__c = 'http://test-hss.humana.com/ITSecurity/HSSOneClick/LaunchHSS.aspx');
			insert endpoint;
            
            Test.StartTest();
            Test.setCurrentPage(page.RxConnect_VF_HUM);
            ApexPages.StandardController scon = new ApexPages.StandardController(policyObj);
            ApexPages.currentPage().getParameters().put('source', 'scrm');
            ApexPages.currentPage().getParameters().put('Id', policyObj.Id);
            RxConnect_C_HUM rxURL = new RxConnect_C_HUM();
            RxConnect_D_HUM rxData = new RxConnect_D_HUM();
            rxURL.sURL='Test URL';
            rxURL.IdForMember = 'H1238890';
            String MemberID = 'H1238890';
            List<Error_Log__c> lstLogErrors = rxURL.lstLogErrors;
            Continuation con = (Continuation)RxConnect_C_HUM.invokeEncryptService(MemberID);
            con.ContinuationMethod = 'ProcessEncryptService';
            HttpResponse resEncrypt = new Httpresponse();
            String req = '{"EncryptResponse":{"EncryptResult":{"EncryptValue":"sP0g3U6a8m28aTgBMyTBAQ=="}}}';
            resEncrypt.setBody(req);
            Map<String, HTTPRequest> mapDetails = con.getRequests();
            string reqType = '';
            Object State;
            List<string> labels = new List<string>();
            for(String s : mapDetails.keyset())
            {
                reqType = s;
                labels.add(reqType);
            }   
            Test.setContinuationResponse(labels[0],resEncrypt);
            RxConnect_C_HUM.ProcessEncryptService(labels, State);
            Test.StopTest();
        }
    }
    @isTest
    static void testEncryptNegative()
    {
        User testUser = [Select Id, Name from User where LastName = 'test User1'];
        policy_member__c policyObj = [select id, name, member__c, end_date__c, Effective_Date__c from policy_member__c where name = 'Test Policy' limit 1];
        Member_ID__c memID = [select id, Name, Type__c, policy_member__c from Member_ID__c where Type__c = 'Member-ID-Base' and Name = 'Test Member ID'];
        System.runAs(testUser)
        {
             HUM_Webservice_Callout__c EncryptServiceDetails = new HUM_Webservice_Callout__c(Name = 'EncryptService', Certificate_Name__c = 'Test_Certificate', Active__c = true, End_Point_URL__c = 'https://int-crmservicews.humana.com/EncryptRestService_V1.0/encrypt', Timeout__c = '12000', Request_Type__c = 'POST', Content_Type__c = 'application/json');
             insert EncryptServiceDetails;
             System.assert(HUM_Webservice_Callout__c.getValues('EncryptService') != NULL);
             
             HUMCertificateURL__c certiURL = New HUMCertificateURL__c(Name = 'RXLookUpURL', CertificateListURL__c = 'http://test-hss.humana.com/RxLookup/Default.aspx?processcode=2179&hm=');
             insert certiURL;
             System.assert(certiURL.Name == 'RXLookUpURL' );

             Endpoint_Url_HUM__c endpoint = New Endpoint_Url_HUM__c(Name = 'SSO_LOGINURL', Remote_Url__c = 'http://test-hss.humana.com/ITSecurity/HSSOneClick/LaunchHSS.aspx');
             insert endpoint;
            
            Test.StartTest();
            Test.setCurrentPage(page.RxConnect_VF_HUM);
            ApexPages.StandardController scon = new ApexPages.StandardController(policyObj);
            ApexPages.currentPage().getParameters().put('Id', policyObj.Id);
            RxConnect_D_HUM rxData = new RxConnect_D_HUM();
            RxConnect_C_HUM rxURL = new RxConnect_C_HUM();
            rxURL.sURL='Test URL';
            rxURL.IdForMember = 'H1238890';
            rxData.init(rxURL);
            String MemberID = 'H1238890';
            List<Error_Log__c> lstLogErrors = rxURL.lstLogErrors;
            Continuation con = (Continuation)RxConnect_C_HUM.invokeEncryptService(MemberID);
            con.ContinuationMethod = 'ProcessEncryptService';
            HttpResponse resEncrypt = new Httpresponse();
            String req = '{"EncryptResponse":{"EncryptResult":{"EncryptValue":"sP0g3U6a8m28aTgBMyTBAQ=="}}}';
            resEncrypt.setBody(req);
            Map<String, HTTPRequest> mapDetails = con.getRequests();
            string reqType = '';
            Object State;
            List<string> labels = new List<string>();
            for(String s : mapDetails.keyset())
            {
                reqType = s;
                labels.add(reqType);
            }   
            Test.setContinuationResponse(labels[0],resEncrypt);
            RxConnect_C_HUM.ProcessEncryptService(labels, State);
            Test.StopTest();
        }
    }
}