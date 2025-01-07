/*****************************************************************************************
Apex class Name    : PCPUpdate_LT_HUM
Version            : 1.0
Function           : controller for displaying PCP update template data.
Created Date       : 09/09/2022
Test Class         : PCPUpdate_LT_HUM
Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
*Divya Bhamre                                        04/25/2023                 Fixing Test Class
****************************************************************************************************************/

@isTest
public class PCPUpdate_LT_HUM {
    
    
    @testSetup static void setUpTestData(){
        User objUser = HUMTestDataHelperTest.createTestUser('User1', 'Customer Care Specialist');
        insert objUser;
        System.assertEquals(objUser.lastname == 'test User1', true);
        System.runAs(objUser){
            
            Account objMemAccount = HUMTestDataHelperTest.getAccount();
            objMemAccount.FirstName = 'Test FName';
            objMemAccount.LastName = 'LName';
            objMemAccount.source_platform_code__c = 'EM';
            objMemAccount.Enterprise_Id__c = String.valueof(2000);
            objMemAccount.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            objMemAccount.Birthdate__c = String.valueOf(System.today().month() + '/' + System.today().day() + '/' + System.today().year());
            objMemAccount.PersonMailingPostalCode = '40302';
            
            insert objMemAccount;
            System.assert(objMemAccount.LastName == 'LName');

            CRMFunctionality_ONOFF_Switch__c onOffSwitch = new CRMFunctionality_ONOFF_Switch__c(
                Name = 'providerSearchSwitch',
                IsON__c = true);
                insert onOffSwitch;
            
            Product2 objMedProduct = new Product2();
            objMedProduct.Name ='MED';
            objMedProduct.Product_Type__c = 'Med';
            objMedProduct.productCode = 'MED';
            objMedProduct.Sold_Product_Key_Value__c = 'MC3HH016';
            insert objMedProduct;
            System.assert(objMedProduct.Product_Type__c == 'Med');
            
            PurchaserPlan objPurchaserPlan = New PurchaserPlan();
            objPurchaserPlan.Name = 'MED';
            objPurchaserPlan.Major_LOB__c = 'MED';
            objPurchaserPlan.Source_Major_LOB__c = 'MCD';
            objPurchaserPlan.Product__c = objMedProduct.id;
            insert objPurchaserPlan;
            // Insert Member Account
            
            MemberPlan objMemberPlan = New MemberPlan();
            objMemberPlan.Member = objMemAccount;
            objMemberPlan.planId = objPurchaserPlan.id;
            objMemberPlan.MemberId = objMemAccount.Id;
            objMemberPlan.EffectiveTo =Date.valueOf('2025-12-31');
            objMemberPlan.EffectiveFrom = Date.valueOf('2015-12-31');
            objMemberPlan.Member_Plan_External_Id__c  = '00|ODS|123456789|89|89|02-01-2001';
            objMemberPlan.name = '123456789';
            insert objMemberPlan;
            System.assert(objMemberPlan.Member_Plan_External_Id__c == '00|ODS|123456789|89|89|02-01-2001');
            
            Case cs = new Case();
            cs.Status = 'In Progress';
            cs.AccountId = objMemAccount.Id;
            //cs.planId = objPurchaserPlan.id;
            cs.Member_Plan_Id__c = objMemberPlan.Id;
            insert cs;
            
            Template__c templatePCPUpdate = new Template__c();
            templatePCPUpdate.Unique_Template_Name__c = 'TN-0078';
            templatePCPUpdate.Description__c = 'PCP Update/Change';
            insert templatePCPUpdate;
            
            
            
            Template_Field__c oTemplateField = new Template_Field__c(Name= 'Data', Data_Type__c= 'String',Template__c = templatePCPUpdate.Id );
            insert oTemplateField ;
            system.assertNotEquals(oTemplateField ,null,'Value not matched'); 
            system.assertEquals(oTemplateField.Name,'Data'); 
            
			Template_Submission_Owner__c  oTemplateSubmsissionOwner = new Template_Submission_Owner__c(Object_Owner_ID__c = cs.Id,  Object_Owner_Type__c = 'Case')  ;
            insert oTemplateSubmsissionOwner;
            system.assertNotEquals(oTemplateSubmsissionOwner,null,'Value not matched'); 
            system.assertEquals(oTemplateSubmsissionOwner.Object_Owner_Type__c  ,'Case'); 
        
             
            
            Template_Submission__c oTempalteSubmsision = new Template_Submission__c(Template__c =templatePCPUpdate.Id, Submission_Owner__c = oTemplateSubmsissionOwner.Id, Version__c = 1.00 );
            insert oTempalteSubmsision;
            system.assertNotEquals(oTempalteSubmsision,null,'Value not matched');  
            
            Template_Submission_Data__c oTemplateSubmissionData = new Template_Submission_Data__c(Template_Field__c = oTemplateField.Id , Template_Submission__c = oTempalteSubmsision.Id ,value__c ='TestValue' );
            insert oTemplateSubmissionData ;
            system.assertNotEquals(oTemplateSubmissionData ,null,'Value not matched'); 
            
            HUM_Webservice_Callout__c calloutSettings1 = new HUM_Webservice_Callout__c();
            calloutSettings1.Name = 'ProviderSearchServiceV1';
            calloutSettings1.Active__c = True;
            calloutSettings1.Enable_Service_Tester__c = True;
            calloutSettings1.Certificate_Name__c = 'Humana_GuidanceCare_Pileus';
            calloutSettings1.Content_Type__c = 'application/json';
            calloutSettings1.Request_Type__c = 'Post';
            calloutSettings1.Timeout__c = '12000';
            calloutSettings1.End_Point_URL__c = 'https://int-crmservicews.humana.com/ProviderDirectorySearchService/GetProviderSearchResults';
            insert calloutSettings1;
            
            HUM_Webservice_Callout__c PBEServiceDetails = new HUM_Webservice_Callout__c(Name = 'ProviderDirectorySearchServiceNetworks', Certificate_Name__c = 'Test_Certificate', Active__c = true, End_Point_URL__c = 'https://int-crmservicews.humana.com/ProviderDirectorySearchService/GetNetworks', Timeout__c = '12000', Request_Type__c = 'POST', Content_Type__c = 'application/json');
            insert PBEServiceDetails;
            System.assert(HUM_Webservice_Callout__c.getValues('ProviderDirectorySearchServiceNetworks') != NULL);

            
        }
    }
    static testMethod void testPCPUpdate(){
        Test.StartTest();
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        System.runAs(testUser)
        {
            String jsonRequest = '"{ \\"ProviderDirectoryResults\\":{ \\"errorMessage\\":[ \\"Invalid Latitude.\\", \\"Invalid Longitude.\\", \\"Invalid State Fips.\\", \\"Invalid County Fips.\\" ] } }"';
            Continuation con = (Continuation)PCPUpdate_LC_HUM.getProviders(jsonRequest);
            con.continuationMethod = 'processResponse';
            HttpResponse response = new HttpResponse();
            response.setStatusCode(200);
            String resBodyString = '{"baseSearch":{"customerId":1,"zip":"40223","zipPlus":"","coverage":{"networkId":121},"generalFilter":{"isFacet":false,"distance":3,"SelectLocation":"distance","PrimaryCareRequest":{"PcpProductMarket":71766,"PcpPlan":"091","PcpEffectiveDate":"2022-09-02","PcpOption":"701","pcpValidationType":"Full","MemberBirthDate":""}},"searchCriteria":{"searchCategory":"all","searchText":""}},"pagination":{"offset":2,"limit":1000}}';
            response.setBody(resBodyString);
            con.state = 'Continuation-1';
            string reqType = 'Continuation-1';        
            List<string> labelGetmember = new List<string>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,response);
            //Test.setContinuationResponse('labels', response);
            String responseStr = (String) PCPUpdate_LC_HUM.processResponse(labelGetmember,resBodyString);
            Boolean bDataExist = responseStr.containsIgnoreCase('40223') ? true : false;
            system.assertEquals(true, bDataExist);
            system.assertEquals(response.getBody(), resBodyString);
            Test.StopTest();            
        }
    }
    static testMethod void testPCPUpdateNull(){
        Test.StartTest();
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        System.runAs(testUser)
        {
            String jsonRequest = '"{ \\"ProviderDirectoryResults\\":{ \\"errorMessage\\":[ \\"Invalid Latitude.\\", \\"Invalid Longitude.\\", \\"Invalid State Fips.\\", \\"Invalid County Fips.\\" ] } }"';
            Continuation con = (Continuation)PCPUpdate_LC_HUM.getProviders(jsonRequest);
            con.continuationMethod = 'processResponse';
            HttpResponse response = new HttpResponse();
            response.setStatusCode(2000);
            String resBodyString = '';
            response.setBody(resBodyString);
            con.state = 'Continuation-1';
            string reqType = 'Continuation-1';        
            List<string> labelGetmember = new List<string>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,response);
            Object responseStr = PCPUpdate_LC_HUM.processResponse(labelGetmember,resBodyString);
            system.assertEquals(null, responseStr);
            Test.StopTest();            
        }
    }
    
    static testMethod void testPCPUpdateException(){
        Test.StartTest();
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        HUM_Webservice_Callout__c providersearchservice = HUM_Webservice_Callout__c.getValues('ProviderSearchServiceV1');
        providersearchservice.Request_Type__c = null;
        update providersearchservice;
        System.runAs(testUser)
        {
            try{
                String jsonRequest = '"{ \\"ProviderDirectoryResults\\":{ \\"errorMessage\\":[ \\"Invalid Latitude.\\", \\"Invalid Longitude.\\", \\"Invalid State Fips.\\", \\"Invalid County Fips.\\" ] } }"';
                Continuation con = (Continuation)PCPUpdate_LC_HUM.getProviders(jsonRequest);
                con.continuationMethod = 'processResponse';
                HttpResponse response = new HttpResponse();
                response.setStatusCode(2000);
                String resBodyString = '';
                response.setBody(resBodyString);
                con.state = 'Continuation-1';
                string reqType = 'Continuation-1';        
                List<string> labelGetmember = new List<string>();
                labelGetmember.add(reqType);
                Test.setContinuationResponse(reqType,response);
                Object responseStr = PCPUpdate_LC_HUM.processResponse(labelGetmember,resBodyString);
                system.assertEquals(null, responseStr);
            }catch(exception ex){
                system.debug(ex.getMessage());
            }
            
            Test.StopTest();            
        }
    }
    
    
    static testMethod void testgetNetworkId(){
        User testUser = [Select Id, Name from User where LastName = 'test User1'];
        System.runAs(testUser){
            Continuation con = (Continuation)PCPUpdate_LC_HUM.getNetworkId('{"GetNetworks": {"customerId": 1,"ledgerNumber": "38913"}}');
        	
            String PCPNetworkIdReq = '{"GetNetworks": {"customerId": 1,"ledgerNumber": "38913"}}';
            HttpRequest PCPRequest = new HttpRequest();
            PCPRequest.setBody('{"GetNetworks": {"customerId": 1,"ledgerNumber": "38913"}}');
            
            string PCPNetworkIdRes = '{"NetworksResponse":{}}';
            HttpResponse PCPRes = new Httpresponse();
            PCPRes.setBody('{"NetworksResponse":{}}');
            
            System.assert(PCPRequest.getBody() == PCPNetworkIdReq);
            System.assert(PCPRes.getBody() == PCPNetworkIdRes);
            
            
            Continuation continueObj = new Continuation(120);
            continueObj.continuationMethod = 'parseNetworkResponse';
            continueObj.State = '';
            PCPRes.setStatusCode(200);
            
            
            List<string> labels = new List<string>();
            string reqType = 'Continuation-1';
            labels.add(reqType);
        	Test.setContinuationResponse(reqType,PCPRes);
                 
            Object networkIdResponse = PCPUpdate_LC_HUM.parseNetworkResponse(labels,PCPRes);
            System.assertNotEquals(null, networkIdResponse);
            
                       
        }
    }
    
    static testMethod void testgetNetworkIdNull(){
        User testUser = [Select Id, Name from User where LastName = 'test User1'];
        System.runAs(testUser){
            try{
            Continuation con = (Continuation)PCPUpdate_LC_HUM.getNetworkId('{"GetNetworks": {"customerId": 1,"ledgerNumber": "38913"}}');
        	
            String PCPNetworkIdReq = '{"GetNetworks": {"customerId": 1,"ledgerNumber": "38913"}}';
            HttpRequest PCPRequest = new HttpRequest();
            PCPRequest.setBody('{"GetNetworks": {"customerId": 1,"ledgerNumber": "38913"}}');
            
            string PCPNetworkIdRes = '';
            HttpResponse PCPRes = new Httpresponse();
            PCPRes.setBody('');
            
            System.assert(PCPRequest.getBody() == PCPNetworkIdReq);
            System.assert(PCPRes.getBody() == PCPNetworkIdRes);
            
            
            Continuation continueObj = new Continuation(120);
            continueObj.continuationMethod = 'parseNetworkResponse';
            continueObj.State = '';
            PCPRes.setStatusCode(200);
            
            
            List<string> labels = new List<string>();
            string reqType = 'Continuation-1';
            labels.add(reqType);
        	Test.setContinuationResponse(reqType,PCPRes);
                 
            
                Object networkIdResponse= PCPUpdate_LC_HUM.parseNetworkResponse(labels,PCPRes);
               system.assertEquals(null, networkIdResponse); 
            }catch(exception ex){
                system.debug(ex.getMessage());
            }
            
        }
    }
    
    static testMethod void testgetNetworkIdException(){
        User testUser = [Select Id, Name from User where LastName = 'test User1'];
        System.runAs(testUser){
       try{
            Continuation con = (Continuation)PCPUpdate_LC_HUM.getNetworkId('testing');
        	
            String PCPNetworkIdReq = 'testing';
            HttpRequest PCPRequest = new HttpRequest();
            PCPRequest.setBody('testing');
            
            string PCPNetworkIdRes = 'testing';
            HttpResponse PCPRes = new Httpresponse();
            PCPRes.setBody('testing');
            
            System.assert(PCPRequest.getBody() == PCPNetworkIdReq);
            System.assert(PCPRes.getBody() == PCPNetworkIdRes);
            
            
            Continuation continueObj = new Continuation(120);
            continueObj.continuationMethod = 'parseNetworkResponse';
            continueObj.State = '';
            PCPRes.setStatusCode(200);
            
            
            List<string> labels = new List<string>();
            string reqType = 'Continuation-1';
            labels.add(reqType);
        	Test.setContinuationResponse(reqType,PCPRes);
                 
            
           Object networkIdResponse= PCPUpdate_LC_HUM.parseNetworkResponse(labels,PCPRes);
               system.assertEquals('Error', networkIdResponse); 
           
       }catch(exception ex){
                system.debug(ex.getMessage());
          }
                       
        }
    }
    
    
    static testMethod void testgetProviderSearchVisibilty(){
        Test.StartTest();
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        System.runAs(testUser)
        {
            Boolean switchValue = PCPUpdate_LC_HUM.getProviderSearchVisibilty();
            Assert.isTrue(switchValue);
            Test.StopTest();            
        }
    }
}