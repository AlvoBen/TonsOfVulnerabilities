/******************************************************************************************************************************
Apex Class Name  : CRMRetail_SDoH_Helper_T_HUM
Version          : 1.0 
Created Date     : 06/28/2021
Function         : Test for CRMRetail_SDoH_Helper_HUM           

Modification Log :
*    Developer             Code Review            Date                 Description
* ------------------------------------------------------------------------------------------------------------------------------                
*  Abhishek Maurya                               06/26/2021          User Story 2308421 -CRM Storefront SDoH: Action Required Prompt
*  Vinoth L                      				 06/26/2021          User Story 2308568 -CRM Storefront SDoH: Track DH&A Screening Results
*  Vinoth L                                      09/02/2021          User Story - 2508432 T1PRJ0001894 - MF 7 - CRM Storefront SDoH: Share Screening Results - Call DH&A 
*  Abhishek Maurya                               09/02/2021          User Story - 2599455 T1PRJ0001894 - MF 7 - CRM Storefront SDoH: Share Screening Results - Update Existing 1x1 Tracking
*  Lakshmi Madduri                               6/14/2022           US-3258682 T1PRJ0154546 / SF / MF9 Storefront - SNP: Indicators Field - Data Population
*  Deepak khandelwal                             05/03/2024          User story - 5318748 T1PRJ0154546 / SF / MF9 Storefront - DP Migration to Mulesoft (SDOH)  
*********************************************************************************************************************************/
@isTest
public class CRMRetail_SDoH_Helper_T_HUM {
    
    public class CRMRetail_SDoH_MockImpl implements HttpCalloutMock {
        public HTTPResponse respond(HTTPRequest req) {
            HttpResponse res = new HttpResponse();
            res.setHeader('Content-Type', 'application/json');
            res.setBody('[{"sdrPersonId":15,"scoreDate":"2019-12-31","metricName":"loneliness","metricRating":"4","metricDescription":"Likely","metricSource":"Predictive Model"},{"sdrPersonId":15,"scoreDate":"2019-12-31","metricName":"loneliness","metricRating":"4","metricDescription":"Likely","metricSource":"Predictive Model"}]');
            res.setStatusCode(200);
            return res;
        }
    }
    public class CRMRetail_DHNA_MockImpl implements HttpCalloutMock{
        public HTTPResponse respond(HTTPRequest req) {
            HttpResponse res = new HttpResponse();
            res.setHeader('Content-Type', 'application/json');
            res.setBody('[{ "sdrPersonId": 15,"screeningDate": "2020-05-31","metricName": "loneliness","metricRating": "9","metricDescription": "Very likely","metricSource": "Member Care Assessment - Screening","timestamp": "2021-08-12T12:45:04.302Z"},{ "sdrPersonId": 16,"screeningDate": "2020-05-31","metricName": "Financial Strain","metricRating": "1","metricDescription": "Very Unlikely","metricSource": "Member Care Assessment - Screening","timestamp": "2021-08-12T12:45:04.302Z"}]');
            res.setStatusCode(200);
            return res;
        }
    }
    public class CRMRetail_SDoH_MockImpl_Error_Handling implements HttpCalloutMock {
        public HTTPResponse respond(HTTPRequest req) {
            HttpResponse res = new HttpResponse();
            res.setHeader('Content-Type', 'application/json');
            String body='{'+
                +'"timestamp": "2021-09-29T12:52:45.601+0000",'+
                +'"status": 404,'+
                +'"error": "Not Found",'+
                +'"message": "Member ID: 1097538492 not Found!",'+
                +'"path": "/member/1097538492/sdoh"'+
                +'}';
            res.setBody(body);
            res.setStatusCode(200);
            return res;
        }
    }
    public class CRMRetail_SDoH_MockImpl_Member_Not_Found implements HttpCalloutMock {
        public HTTPResponse respond(HTTPRequest req) {
            HttpResponse res = new HttpResponse();
            res.setHeader('Content-Type', 'application/json');
            String body='{'+
                +'"timestamp": "2021-09-29T12:52:45.601+0000",'+
                +'"status": 404,'+                
                +'"message": "Member ID: 1097538492 not Found!",'+
                +'"path": "/member/1097538492/sdoh"'+
                +'}';
            res.setBody(body);
            res.setStatusCode(200);
            return res;
        }
    }
    public class CRMRetail_ConsolidatedService_Success implements HttpCalloutMock{
        public HTTPResponse respond(HTTPRequest req) {
            HttpResponse res = new HttpResponse();
            res.setHeader('Content-Type', 'application/json');
            String sBody = '{"SDoHSuccess":[{"sdrPersonId":1060920019,"scoreDate":"2021-05-31","metricName":"loneliness","metricRating":"5","metricDescription":"Verylikely","metricSource":"MemberCareAssessmentScreening"},{"sdrPersonId":1060920019,"scoreDate":"2021-09-13","metricName":"HousingInsecurity","metricRating":"1","metricDescription":"VeryUnlikely","metricSource":"HumanaNeighborhoodCenter"},{"sdrPersonId":1060920019,"scoreDate":"2021-09-13","metricName":"FinancialStrain","metricRating":"5","metricDescription":"VeryLikely","metricSource":"HumanaNeighborhoodCenter"}],"SDoHError":null,"EngagementIndexSuccess":{"sdrPersonId":1060920019,"srcPlatformCd":"LV","modelName":"medr-engagement-index","modelVersion":2,"modelLookback":12,"scoreDate":"2021-09-30","actionTimestamp":"2022-04-11T06:12:22.178355","score":"11.6709995270","scaledScore":null,"scoringType":"other-fixedalgorithm","label":"MAPDEngagementIndex","triggerEventIdName":null,"triggerEventId":null,"finalPrediction":"Unengaged","finalPredictionThreshold":"31.1|57.0","supplementalFields":{"year_mth":"202109","engagement_quintile":"2","go365_contrib":"0.0","rtsrce_contrib":"0.0","programs_contrib":"8.336428571428572","logins_contrib":"0.0","amlk_model_contrib":"3.334571428571429","hedis_contrib":null,"engagement_cohort":"tm2","mco_contract_nbr":"H1468","plan_benefit_package_id":"013","ind_new_mbr":"0"}},"EngagementIndexError":null,"MedicareMemberInfoSuccess":{"CIMedicareResponse":{"GetMedicareMemberInfo":[{"MedicareMemberLISSNPInfoResponse":{"MedicareID":"","LISIndicator":"","SubsidyEffectDate":"","SubsidyEndDate":"","LISSubsidyLevel":"","LISCopayCategory":"","LTSSIndicator":"","ESRDIndicator":"","SPAPIndicator":"","SNPIndicator":"Y","SNPTypeCode":"DE","POAIndicator":"","POALastName":"","POAFirstName":"","POAPhoneNumber":"","POAType":"","POAApplDate":"","Errors":{"Error":{}}}}]}},"MedicareMemberInfoError":null}';
            res.setBody(sBody);
            res.setStatusCode(200);
            return res;
        }
    }
    public class CRMRetail_ConsolidatedService_Error implements HttpCalloutMock{
        public HTTPResponse respond(HTTPRequest req) {
            HttpResponse res = new HttpResponse();
            res.setHeader('Content-Type', 'application/json');
            String sBody = '{"SDoHSuccess":null,"SDoHError":{"timestamp":"2022-06-06T12:14:58.041+00:00","status":404,"error":"NotFound","message":"MemberID:10609209notFound!","path":"/ecom/member/10609209/sdoh"},"EngagementIndexSuccess":null,"EngagementIndexError":{"timestamp":"2022-06-06T12:14:57.971+00:00","status":404,"error":"NotFound","message":"EngagementIndexinfonotfoundforsrdPersonId:10609209","path":"/lhr/members/10609209/engagementIndex"},"MedicareMemberInfoSuccess":{"CIMedicareResponse":{"GetMedicareMemberInfo":[{"MedicareMemberLISSNPInfoResponse":{"MedicareID":"","LISIndicator":"","SubsidyEffectDate":"","SubsidyEndDate":"","LISSubsidyLevel":"","LISCopayCategory":"","LTSSIndicator":"","ESRDIndicator":"","SPAPIndicator":"","SNPIndicator":"","SNPTypeCode":"","POAIndicator":"","POALastName":"","POAFirstName":"","POAPhoneNumber":"","POAType":"","POAApplDate":"","Errors":{"Error":{"Code":",E0002","Description":",MRPS007-MEM-CARD-IDCOULDNOTBERETRIEVEDINCI"}}}}]}},"MedicareMemberInfoError":null}';
            res.setBody(sBody);
            res.setStatusCode(200);
            return res;
        }
    }
    public class CRMRetail_ConsolidatedService_SNPError implements HttpCalloutMock{
        public HTTPResponse respond(HTTPRequest req){
            HttpResponse res = new HttpResponse();
            res.setHeader('Content-Type', 'application/json');
            String sBody = '{"SDoHSuccess":null,"SDoHError":{"timestamp":"2022-06-17T07:16:17.957+00:00","status":404,"error":"NotFound","message":"MemberID:1098156900notFound!","path":"/ecom/member/1098156900/sdoh"},"EngagementIndexSuccess":null,"EngagementIndexError":null,"MedicareMemberInfoSuccess":null,"MedicareMemberInfoError":{"Fault":{"faultcode":"ntttttCode-..1","faultstring":"nttttterror_service_name:|nttttterror_service_method:|ntttttfault_message:","detail":{"transactionInfo":{"transaction-id":"501819376","transaction-client":"127.0.0.1","URL-in":"http://127.0.0.1:8332/CIMedicareMultipleMemberDetails","processor-name":"CIMedicareMultipleMemberService","processor-type":"MultiprotocolGateway"},"errorInfo":{"error-code":"0x00c3000f","error-headers":"HTTP/1.1500InternalServerErrorrnContent-Type:text/xmlrnX-Backside-Transport:FAILFAILrnConnection:closernrn","error-message":"<results>elementmustcontainatleastone<url>childelement.","error-protocol-reason-phrase":"","error-protocol-response":"0","error-subcode":"0x00c3000f"}}}}}';
        	res.setBody(sBody);
            res.setStatusCode(200);
            return res;
        }
    }
     public class CRMRetail_SDoH_MockImplServiceUnavailable implements HttpCalloutMock {
        public HTTPResponse respond(HTTPRequest req) {
            HttpResponse res = new HttpResponse();
            res.setStatusCode(400);
            return res;
        } 
    }
    
    /*
    * Method Name : prepareTestData
    * Description : Create common test Data
    * Return type : void
    * Parameter   : NA
    */
    @testSetup static void prepareTestData() 
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','System Administrator');
        insert oCCSUser;
        system.assertEquals(oCCSUser.LastName,'test User1');
        
        CRMRetailSDoH_DTO_HUM sDoHWrapper = new CRMRetailSDoH_DTO_HUM();
        CRMRetailSDoH_DTO_HUM.CRMRetail_DHNA_Response_DTO res =new CRMRetailSDoH_DTO_HUM.CRMRetail_DHNA_Response_DTO();
        
        System.runAs (oCCSUser) 
        {
            List<Account> listOfAccount=new List<Account>();
            ID oCRMRetailVisitorTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_RETAIL_VISITOR).getRecordTypeId();
            Account oAcc1 = CRMRetail_TestDataFactory_H_HUM.createObjAccount(oCRMRetailVisitorTypeId,'04/06/1985','Daphiner','Superman','12209','Daphiner@gmail.com','','arizona',false);
            oAcc1.Enterprise_ID__c='15';
            Account oAcc2 =CRMRetail_TestDataFactory_H_HUM.createObjAccount(oCRMRetailVisitorTypeId,'04/06/1985','Dean','Ambrose','12209','DEAN@gmail.com','','arizona',false);
            oAcc2.Enterprise_ID__c='17';
            Account oAcc3 =CRMRetail_TestDataFactory_H_HUM.createObjAccount(oCRMRetailVisitorTypeId,'04/06/1985','John','Smith','12209','JSmith@gmail.com','','arizona',false);
            oAcc2.Enterprise_ID__c='';
            listOfAccount.add(oAcc1);
            listOfAccount.add(oAcc2);
            listOfAccount.add(oAcc3);
            insert listOfAccount;
            system.assertEquals(listOfAccount.size(), 3);
            
            List<HUM_Webservice_Callout__c> lstOfCstmSetting = new List<HUM_Webservice_Callout__c>();
            HUM_Webservice_Callout__c customSDoh = CRMRetail_TestDataFactory_H_HUM.createHUMWebServiceCstmStng(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_SDOH_CUSTOM_SETTING ,'POST', 'DummyCertificate', true, 'http://www.google.com', '12000');  
            HUM_Webservice_Callout__c cRMRetail_SDoH_Create = CRMRetail_TestDataFactory_H_HUM.createHUMWebServiceCstmStng(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DHNA_CREATE , 'POST', 'DummyCertificate', true, 'http://www.google.com', '12000');           
            HUM_Webservice_Callout__c cRMRetail_SDoH_Update = CRMRetail_TestDataFactory_H_HUM.createHUMWebServiceCstmStng(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DHNA_UPDATE , 'POST', 'DummyCertificate', true, 'http://www.google.com', '12000');           
            HUM_Webservice_Callout__c cRMRetail_SDoH_Delete = CRMRetail_TestDataFactory_H_HUM.createHUMWebServiceCstmStng(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DHNA_DELETE , 'POST', 'DummyCertificate', true, 'http://www.google.com', '12000');           
            HUM_Webservice_Callout__c cRMRetail_SDoH_GetAllData = CRMRetail_TestDataFactory_H_HUM.createHUMWebServiceCstmStng(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_GETALLDATA , 'POST', 'DummyCertificate', true, 'http://www.google.com', '12000');           
            HUM_Webservice_Callout__c CRMRetail_SDoH_Mulesoft = CRMRetail_TestDataFactory_H_HUM.createHUMWebServiceCstmStng(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_SDOH_CUSTOM_SETTING_MULESOFT , 'POST', 'DummyCertificate', true, 'http://www.google.com', '12000');           
            HUM_Webservice_Callout__c cRMRetail_SDoH_Create_Mulesoft = CRMRetail_TestDataFactory_H_HUM.createHUMWebServiceCstmStng(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DHNA_CREATE_MULESOFT , 'POST', 'DummyCertificate', true, 'http://www.google.com', '12000');           
            HUM_Webservice_Callout__c cRMRetail_SDoH_Update_Mulesoft = CRMRetail_TestDataFactory_H_HUM.createHUMWebServiceCstmStng(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DHNA_UPDATE_MULESOFT , 'POST', 'DummyCertificate', true, 'http://www.google.com', '12000');           
            HUM_Webservice_Callout__c cRMRetail_SDoH_Delete_Mulesoft = CRMRetail_TestDataFactory_H_HUM.createHUMWebServiceCstmStng(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DHNA_DELETE_MULESOFT , 'POST', 'DummyCertificate', true, 'http://www.google.com', '12000');           
            HUM_Webservice_Callout__c cRMRetail_SDoH_GetAllData_Mulesoft = CRMRetail_TestDataFactory_H_HUM.createHUMWebServiceCstmStng(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_GETALLDATA_MULESOFT , 'POST', 'DummyCertificate', true, 'http://www.google.com', '12000');           
            lstOfCstmSetting.add(customSDoh);
            lstOfCstmSetting.add(cRMRetail_SDoH_Create);
            lstOfCstmSetting.add(cRMRetail_SDoH_Update);
            lstOfCstmSetting.add(cRMRetail_SDoH_Delete);
            lstOfCstmSetting.add(cRMRetail_SDoH_GetAllData);
            lstOfCstmSetting.add(CRMRetail_SDoH_Mulesoft);
            lstOfCstmSetting.add(cRMRetail_SDoH_Create_Mulesoft);
            lstOfCstmSetting.add(cRMRetail_SDoH_Update_Mulesoft);
            lstOfCstmSetting.add(cRMRetail_SDoH_Delete_Mulesoft);
            lstOfCstmSetting.add(cRMRetail_SDoH_GetAllData_Mulesoft);
            insert lstOfCstmSetting;
            system.assertEquals(lstOfCstmSetting[0].name, 'CRMRetail_SDoH');
            system.assertEquals(lstOfCstmSetting[1].name, 'CRMRetail_SDoH_Create');
            system.assertEquals(lstOfCstmSetting[2].name, 'CRMRetail_SDoH_Update');
            system.assertEquals(lstOfCstmSetting[3].name, 'CRMRetail_SDoH_Delete');
            system.assertEquals(lstOfCstmSetting[4].name, 'CRMRetail_GetAllData');                        
            
            List<CRMFunctionality_ONOFF_Switch__c> lstOfCRMFuncSwtchs = new List<CRMFunctionality_ONOFF_Switch__c>();
            CRMFunctionality_ONOFF_Switch__c Switch_2654300 = new CRMFunctionality_ONOFF_Switch__c(Name = '2654300',IsON__c=true);
            CRMFunctionality_ONOFF_Switch__c Switch_5318748 = new CRMFunctionality_ONOFF_Switch__c(Name = '5318748',IsON__c=false);
            lstOfCRMFuncSwtchs.add(Switch_2654300);
            lstOfCRMFuncSwtchs.add(Switch_5318748);
            insert lstOfCRMFuncSwtchs;
            assert.areNotEqual(0,lstOfCRMFuncSwtchs.size());
            assert.areEqual(true,lstOfCRMFuncSwtchs[0].IsON__c);       
            
        }
    }
    @isTest
    static  void testGetSDohData() {
        Account accRef=[SELECT Id,Enterprise_Id__c,FirstName FROM Account WHERE FirstName='Daphiner'];
        system.assertEquals(accRef.FirstName, 'Daphiner');
        
        User oCCSUser=[SELECT Id,LastName FROM User Where LastName='test User1' LIMIT 1];
        system.assertEquals(oCCSUser.LastName, 'test User1');
        
        system.runAs(oCCSUser){
            Test.setMock(HttpCalloutMock.class, new CRMRetail_SDoH_MockImpl()); 
            Test.startTest();
            
            CRMRetail_SDoH_Helper_HUM.getSDoHData(new Map<String,String>{accRef.Enterprise_Id__c=>accRef.Id}, false,system.today());
            system.assertEquals(CRMRetail_SDoH_Helper_HUM.enterpriseIdToSDohMetricAcronym.size(),1);
        }    
        Test.stopTest();
    }
    @isTest
    static  void testCreateDeterminentRecord() {
        Account accRef=[SELECT Id,Enterprise_Id__c,FirstName FROM Account WHERE FirstName='Daphiner'];
        system.assertEquals(accRef.FirstName, 'Daphiner');
        if(!Cache.Session.contains('currentInteractionDate')) {
            Datetime myDateTime = system.Now();
            String formatted = myDateTime.formatGMT('yyyy-MM-dd');
            Cache.Session.Put('currentInteractionDate', formatted);
        }
        User oCCSUser=[SELECT Id,LastName FROM User Where LastName='test User1' LIMIT 1];
        system.assertEquals(oCCSUser.LastName, 'test User1');
        system.runAs(oCCSUser){
        Test.setMock(HttpCalloutMock.class, new CRMRetail_SDoH_MockImpl());  
        Test.startTest();
        CRMRetail_SDoH_Helper_HUM.getSDoHData(new Map<String,String>{accRef.Enterprise_Id__c=>accRef.Id}, true,system.today());
        system.assertEquals(CRMRetail_SDoH_Helper_HUM.listOfDetermntToInsert.size(),1);
        }
        Test.stopTest();
    }
    @isTest
    static  void testCreateDeterminentRecordServiceUnavailable() {
        Account accRef=[SELECT Id,Enterprise_Id__c,FirstName FROM Account WHERE FirstName='Dean'];
        system.assertEquals(accRef.FirstName, 'Dean');
        if(!Cache.Session.contains('currentInteractionDate')) {
            Datetime myDateTime = system.Now();
            String formatted = myDateTime.formatGMT('yyyy-MM-dd');
            Cache.Session.Put('currentInteractionDate', formatted);
        }
        User oCCSUser=[SELECT Id,LastName FROM User Where LastName='test User1' LIMIT 1];
        system.assertEquals(oCCSUser.LastName, 'test User1');
        system.runAs(oCCSUser){
        Test.setMock(HttpCalloutMock.class, new CRMRetail_SDoH_MockImplServiceUnavailable());  
        Test.startTest();
        CRMRetail_SDoH_Helper_HUM.getSDoHData(new Map<String,String>{accRef.Enterprise_Id__c=>accRef.Id}, true,system.today());
        system.assertEquals(CRMRetail_SDoH_Helper_HUM.listOfDetermntToInsert.size(),1);
        }
        Test.stopTest();
    }
    @isTest
    static  void testCreateDeterminentRecordErrorHandling() {
        Account accRef=[SELECT Id,Enterprise_Id__c,FirstName FROM Account WHERE FirstName='Daphiner'];
        system.assertEquals(accRef.FirstName, 'Daphiner');
        
        User oCCSUser=[SELECT Id,LastName FROM User Where LastName='test User1' LIMIT 1];
        system.assertEquals(oCCSUser.LastName, 'test User1');
        
        system.runAs(oCCSUser){
            Test.setMock(HttpCalloutMock.class, new CRMRetail_SDoH_MockImpl_Error_Handling()); 
            Test.startTest();
            
            CRMRetail_SDoH_Helper_HUM.getSDoHData(new Map<String,String>{accRef.Enterprise_Id__c=>accRef.Id}, true,system.today());        	
            system.assertEquals(0,CRMRetail_SDoH_Helper_HUM.enterpriseIdToSDohMetricAcronym.size());                        
        }                           
        Test.stopTest();
    }
    @isTest
    static  void testSDoHErrorHandling() {
        Account accRef=[SELECT Id,Enterprise_Id__c,FirstName FROM Account WHERE FirstName='John'];
        system.assertEquals(accRef.FirstName, 'John');
        
        User oCCSUser=[SELECT Id,LastName FROM User Where LastName='test User1' LIMIT 1];
        system.assertEquals(oCCSUser.LastName, 'test User1');
        
        system.runAs(oCCSUser){
            Test.setMock(HttpCalloutMock.class, new CRMRetail_SDoH_MockImpl_Member_Not_Found()); 
            Test.startTest();                        
                        
            CRMRetail_SDoH_Helper_HUM.getSDoHData(new Map<String,String>{accRef.Enterprise_Id__c=>accRef.Id}, true,system.today());        	
            system.assertEquals(0,CRMRetail_SDoH_Helper_HUM.enterpriseIdToSDohMetricAcronym.size());
        }
    }
    @isTest
    static  void testDHNAServiceAvailableAPI() {
        Account accRef=[SELECT Id,Enterprise_Id__c,FirstName FROM Account WHERE FirstName='Dean'];
        system.assertEquals(accRef.FirstName, 'Dean');
        if(!Cache.Session.contains('currentInteractionDate')) {
            Datetime myDateTime = system.Now();
            String formatted = myDateTime.formatGMT('yyyy-MM-dd');
            Cache.Session.Put('currentInteractionDate', formatted);
        }
        User oCCSUser=[SELECT Id,LastName FROM User Where LastName='test User1' LIMIT 1];
        system.assertEquals(oCCSUser.LastName, 'test User1');
        system.runAs(oCCSUser){
            Test.setMock(HttpCalloutMock.class, new CRMRetail_DHNA_MockImpl());  
            Test.startTest();
            boolean result1 = CRMRetail_SDoH_Helper_HUM.callSDoHServiceAPIOnShare(createDHNAData(System.now(),new List<String> {'loneliness','loneliness'},GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_VERY_LIKELY,accRef.Enterprise_Id__c,'UPDATE'),GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DHNA_UPDATE);
            system.assertEquals(result1,True);
            boolean result2 = CRMRetail_SDoH_Helper_HUM.callSDoHServiceAPIOnShare(createDHNAData(System.now(),new List<String> {'loneliness','loneliness'},GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_VERY_LIKELY,accRef.Enterprise_Id__c,'INSERT'),GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DHNA_CREATE);
            system.assertEquals(result2,true);
            boolean result3 = CRMRetail_SDoH_Helper_HUM.callSDoHServiceAPIOnShare(createDHNAData(System.now(),new List<String> {'loneliness','loneliness'},'Very UnLikely',accRef.Enterprise_Id__c,'DELETE'),GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DHNA_DELETE);
            system.assertEquals(result3,true);                               
        }
    }
	@isTest
    static  void testDHNAServiceAvailableAPIMuleSoft() {
        List<CRMFunctionality_ONOFF_Switch__c> lstOfCrmSwtch = [select id,name,isOn__c from CRMFunctionality_ONOFF_Switch__c where Name='5318748'];
        assert.areEqual(1,lstOfCrmSwtch.size());        
        lstOfCrmSwtch[0].isOn__c = true;
        update lstOfCrmSwtch;
        assert.areEqual(true,lstOfCrmSwtch[0].isOn__c);
        
        Account accRef=[SELECT Id,Enterprise_Id__c,FirstName FROM Account WHERE FirstName='Dean'];
        system.assertEquals(accRef.FirstName, 'Dean');
        if(!Cache.Session.contains('currentInteractionDate')) {
            Datetime myDateTime = system.Now();
            String formatted = myDateTime.formatGMT('yyyy-MM-dd');
            Cache.Session.Put('currentInteractionDate', formatted);
        }
        User oCCSUser=[SELECT Id,LastName FROM User Where LastName='test User1' LIMIT 1];
        system.assertEquals(oCCSUser.LastName, 'test User1');
        system.runAs(oCCSUser){
            Test.setMock(HttpCalloutMock.class, new CRMRetail_DHNA_MockImpl());  
            Test.startTest();
            boolean result1 = CRMRetail_SDoH_Helper_HUM.callSDoHServiceAPIOnShare(createDHNAData(System.now(),new List<String> {'loneliness','loneliness'},GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_VERY_LIKELY,accRef.Enterprise_Id__c,'UPDATE'),GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DHNA_UPDATE);
            system.assertEquals(result1,True);
            boolean result2 = CRMRetail_SDoH_Helper_HUM.callSDoHServiceAPIOnShare(createDHNAData(System.now(),new List<String> {'loneliness','loneliness'},GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_VERY_LIKELY,accRef.Enterprise_Id__c,'INSERT'),GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DHNA_CREATE);
            system.assertEquals(result2,true);
            boolean result3 = CRMRetail_SDoH_Helper_HUM.callSDoHServiceAPIOnShare(createDHNAData(System.now(),new List<String> {'loneliness','loneliness'},'Very UnLikely',accRef.Enterprise_Id__c,'DELETE'),GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DHNA_DELETE);
            system.assertEquals(result3,true);                               
        }               
    }
    @isTest
    static  void testDHNAServiceUnAvailableInsertAPI() {
        Account accRef=[SELECT Id,Enterprise_Id__c,FirstName FROM Account WHERE FirstName='Dean'];
        system.assertEquals(accRef.FirstName, 'Dean');
        if(!Cache.Session.contains('currentInteractionDate')) {
            Datetime myDateTime = system.Now();
            String formatted = myDateTime.formatGMT('yyyy-MM-dd');
            Cache.Session.Put('currentInteractionDate', formatted);
        }
        User oCCSUser=[SELECT Id,LastName FROM User Where LastName='test User1' LIMIT 1];
        system.assertEquals(oCCSUser.LastName, 'test User1');
        system.runAs(oCCSUser){
        Test.setMock(HttpCalloutMock.class, new CRMRetail_SDoH_MockImplServiceUnavailable());  
        Test.startTest();
        boolean result1 = CRMRetail_SDoH_Helper_HUM.callSDoHServiceAPIOnShare(createDHNAData(System.now(),new List<String> {'Financial Strain','Food Insecurity'},GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_VERY_LIKELY,accRef.Enterprise_Id__c,'UPDATE'),GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DHNA_UPDATE);
        system.assertEquals(result1,false);
              
        }
        Test.stopTest();
    }
    @isTest
    static  void testDHNAServiceUnAvailableUpdateAPI() {
        Account accRef=[SELECT Id,Enterprise_Id__c,FirstName FROM Account WHERE FirstName='Dean'];
        system.assertEquals(accRef.FirstName, 'Dean');
        if(!Cache.Session.contains('currentInteractionDate')) {
            Datetime myDateTime = system.Now();
            String formatted = myDateTime.formatGMT('yyyy-MM-dd');
            Cache.Session.Put('currentInteractionDate', formatted);
        }
        User oCCSUser=[SELECT Id,LastName FROM User Where LastName='test User1' LIMIT 1];
        system.assertEquals(oCCSUser.LastName, 'test User1');
        system.runAs(oCCSUser){
        Test.setMock(HttpCalloutMock.class, new CRMRetail_SDoH_MockImplServiceUnavailable());  
        Test.startTest();
        boolean result2 = CRMRetail_SDoH_Helper_HUM.callSDoHServiceAPIOnShare(createDHNAData(System.now(),new List<String> {'Financial Strain','Food Insecurity'},GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_VERY_LIKELY,accRef.Enterprise_Id__c,'INSERT'),GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DHNA_CREATE);
        system.assertEquals(result2,false);
              
        }
        Test.stopTest();
    }
    @isTest
    static  void testDHNAServiceUnAvailableDelAPI() {
        Account accRef=[SELECT Id,Enterprise_Id__c,FirstName FROM Account WHERE FirstName='Dean'];
        system.assertEquals(accRef.FirstName, 'Dean');
        if(!Cache.Session.contains('currentInteractionDate')) {
            Datetime myDateTime = system.Now();
            String formatted = myDateTime.formatGMT('yyyy-MM-dd');
            Cache.Session.Put('currentInteractionDate', formatted);
        }
        User oCCSUser=[SELECT Id,LastName FROM User Where LastName='test User1' LIMIT 1];
        system.assertEquals(oCCSUser.LastName, 'test User1');
        system.runAs(oCCSUser){
        Test.setMock(HttpCalloutMock.class, new CRMRetail_SDoH_MockImplServiceUnavailable());  
        Test.startTest();
        boolean result3 = CRMRetail_SDoH_Helper_HUM.callSDoHServiceAPIOnShare(createDHNAData(System.now(),new List<String> {'Financial Strain','Food Insecurity'},'Very UnLikely',accRef.Enterprise_Id__c,'DELETE'),GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DHNA_DELETE);
        system.assertEquals(result3,false);        
        }
        Test.stopTest();
    }
    
    static List<CRMRetailSDoH_DTO_HUM> createDHNAData(DateTime createdDate,List<String> listOfDeterminants, String desrciption, String enterpriseId,String eventType){
        List<CRMRetailSDoH_DTO_HUM> lstOfReqBodies = new List<CRMRetailSDoH_DTO_HUM>();
        CRMRetailSDoH_DTO_HUM dtoRef;
        for(String determinant : listOfDeterminants){
            dtoRef = new CRMRetailSDoH_DTO_HUM();
            dtoRef.sdrPersonId = enterpriseId;
            if(eventType == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_UPDATE_EVENT_TYPE){
                if(createdDate != NULL){
                dtoRef.screeningDate = String.valueOf(createdDate.date());
                }
                else{
                dtoRef.screeningDate = String.valueOf(system.Today());      
                }
            }
            else{
                dtoRef.screeningDate = String.valueOf(system.Today());  
            }
            dtoRef.metricName = determinant;
            if(desrciption ==GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_VERY_LIKELY){
                dtoRef.metricRating = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_RATING_FOR_CHOSEN_LIST;
            }else{
                dtoRef.metricRating = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_RATING_FOR_AVAILABLE_LIST;
            }
            dtoRef.metricDescription = desrciption;
            dtoRef.metricSource = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_HNC;
            dtoRef.timestamp = system.now();
            lstOfReqBodies.add(dtoRef);
        }
        return lstOfReqBodies;
    }
    @isTest 
    static void testConsolidatedService(){
        List<CRMRetail_ConsolidatedReq_DTO_HUM> lstServiceRq = new List<CRMRetail_ConsolidatedReq_DTO_HUM>();
        CRMRetail_ConsolidatedReq_DTO_HUM oReq = new CRMRetail_ConsolidatedReq_DTO_HUM();
        oReq.accountId = '0012345678';
        oReq.benefitSequenceNumber='001';
        oReq.enterpriseId='1000236689';
        oReq.groupId='312567';
        oReq.makeSNPRequest=true;
        oReq.memberId='H12345678';
        lstServiceRq.add(oReq);
        Datetime myDateTime = system.Now();
        String formatted = myDateTime.formatGMT('yyyy-MM-dd');
        User oCCSUser=[SELECT Id,LastName FROM User Where LastName='test User1' LIMIT 1];
        system.assertEquals(oCCSUser.LastName, 'test User1');
        system.runAs(oCCSUser)
        {
            Test.setMock(HttpCalloutMock.class, new CRMRetail_ConsolidatedService_Success());  
            Test.startTest();
            CRMRetail_SDoH_Helper_HUM.requestConsolidatedService(lstServiceRq,false,Date.today());
            system.assertEquals(CRMRetail_SDoH_Helper_HUM.sJSONResponse, '{"Special Needs Plan (SNP)":"DE","Engagement Prediction":"Unengaged"}');     
        }
        Test.stopTest();
    }
    @isTest 
    static void testConsolidatedServiceError(){
        List<CRMRetail_ConsolidatedReq_DTO_HUM> lstServiceRq = new List<CRMRetail_ConsolidatedReq_DTO_HUM>();
        CRMRetail_ConsolidatedReq_DTO_HUM oReq = new CRMRetail_ConsolidatedReq_DTO_HUM();
        oReq.accountId = '0012345678';
        oReq.benefitSequenceNumber='001';
        oReq.enterpriseId='1000236689';
        oReq.groupId='312567';
        oReq.makeSNPRequest=true;
        oReq.memberId='H12345678';
        lstServiceRq.add(oReq);
        Datetime myDateTime = system.Now();
        String formatted = myDateTime.formatGMT('yyyy-MM-dd');
        User oCCSUser=[SELECT Id,LastName FROM User Where LastName='test User1' LIMIT 1];
        system.assertEquals(oCCSUser.LastName, 'test User1');
        system.runAs(oCCSUser)
        {
            Test.setMock(HttpCalloutMock.class, new CRMRetail_ConsolidatedService_Error());  
            Test.startTest();
            CRMRetail_SDoH_Helper_HUM.requestConsolidatedService(lstServiceRq,false,Date.today());
            system.assertEquals(CRMRetail_SDoH_Helper_HUM.sJSONResponse, ''); 
            Test.stopTest();
        }        
    }
    @isTest 
    static void testConsolidatedServiceSNPError(){
        List<CRMRetail_ConsolidatedReq_DTO_HUM> lstServiceRq = new List<CRMRetail_ConsolidatedReq_DTO_HUM>();
        CRMRetail_ConsolidatedReq_DTO_HUM oReq = new CRMRetail_ConsolidatedReq_DTO_HUM();
        oReq.accountId = '0012345678';
        oReq.benefitSequenceNumber='001';
        oReq.enterpriseId='1000236689';
        oReq.groupId='312567';
        oReq.makeSNPRequest=true;
        oReq.memberId='H12345678';
        lstServiceRq.add(oReq);
        Datetime myDateTime = system.Now();
        String formatted = myDateTime.formatGMT('yyyy-MM-dd');
        User oCCSUser=[SELECT Id,LastName FROM User Where LastName='test User1' LIMIT 1];
        system.assertEquals(oCCSUser.LastName, 'test User1');
        system.runAs(oCCSUser)
        {
            Test.setMock(HttpCalloutMock.class, new CRMRetail_ConsolidatedService_SNPError());  
            Test.startTest();
            CRMRetail_SDoH_Helper_HUM.requestConsolidatedService(lstServiceRq,false,Date.today());
            system.assertEquals(CRMRetail_SDoH_Helper_HUM.sJSONResponse, ''); 
            Test.stopTest();
        }        
    }
}