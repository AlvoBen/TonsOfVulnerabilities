/*******************************************************************************************************************************
Apex Class Name : PharmacyCreateEditOrder_LT_HUM
Version         : 1.0
Created On      : 03/04/2022
Function        : This Class serves as test class to PharmacyCreateEditOrder_LC_HUM. 
Test Class      : PharmacyCreateEditOrder_LT_HUM
Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Abhishek Mangutkar	                                  03/04/2022                 US-3103531
* Monali Jagtap									          07/17/2023 				 USER STORY 4812246 T1PRJ0891415 Platform Management-  SF - TECH - Critical- Lightning – Pharmacy
*********************************************************************************************************************************/

@isTest
public class PharmacyCreateEditOrder_LT_HUM {
    
	
    @testSetup static void setUpTestData()
    {
        User objUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');   
        insert objUser;
        HUM_Webservice_Callout__c RighSourceServiceDetails = new HUM_Webservice_Callout__c(Name = 'RighSourceService_GetMember',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c ='https://int-crmservicews.humana.com/RighSourceService_GetMember/' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
        insert RighSourceServiceDetails;
        HUM_Webservice_Callout__c RighSourceGETSCRIPTServiceDetails = new HUM_Webservice_Callout__c(Name = 'RighSourceService_GetScripts',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c ='https://int-crmservicews.humana.com/RighSourceService_GetScripts/' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
        insert RighSourceGETSCRIPTServiceDetails ;
        HUM_Webservice_Callout__c RighSourceORDERdETAILServiceDetails = new HUM_Webservice_Callout__c(Name = 'RighSourceService_GetOrderDetails',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c ='https://int-crmservicews.humana.com/RighSourceService_GetOrderDetails/' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
        insert RighSourceORDERdETAILServiceDetails ;
        HUM_Webservice_Callout__c RighSourcePCIServiceDetails = new HUM_Webservice_Callout__c(Name = 'PCI',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c ='https://int-crmservicews.humana.com/RighSourceService_GetOrderDetails/' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
        insert RighSourcePCIServiceDetails;
        HUM_Webservice_Callout__c RighSourceService_CreateAndEditOrder = new HUM_Webservice_Callout__c(Name = 'RighSourceService_CreateAndEditOrder',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c ='https://int-crmservicews.humana.com/RighSourceService_GetOrderDetails/' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
        insert RighSourceService_CreateAndEditOrder;
        HUM_Webservice_Callout__c RighSourceEASServiceDetails = new HUM_Webservice_Callout__c(Name = 'Enterprise_Address_Service',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c ='https://int-crmservicews.humana.com/RighSourceService_GetMember/' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
        insert RighSourceEASServiceDetails;
        HUM_Webservice_Callout__c RighSourceEditServiceDetails = new HUM_Webservice_Callout__c(Name = 'RighSourceService_EditMember',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c ='https://int-crmservicews.humana.com/RighSourceService_GetMember/' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
        insert RighSourceEditServiceDetails;
        Hum_Webservice_Callout__c RightSourceOrder=new HUM_Webservice_Callout__c(Name='RighSourceService_GetOrders',End_Point_URL__c='https://dev-crmservicews.humana.com/RightSourceService_V1.0/GetOrders',Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json'); 
        insert RightSourceOrder;
        Hum_Webservice_Callout__c test1=new HUM_Webservice_Callout__c(Name='test1',End_Point_URL__c='https://dev-crmservicews.humana.com/RightSourceService_V1.0/GetOrders',Timeout__c = '12000',Request_Type__c = null,Content_Type__c ='application/json'); 
        insert test1;
        system.runAs(objUser)
        {
            // Insert Member Account
            Account objMemAccount = HUMTestDataHelperTest.getAccount();
            objMemAccount.FirstName = 'Test FName';
            objMemAccount.LastName = 'LName'; 
            objMemAccount.source_platform_code__c = 'EM' ;
            objMemAccount.Enterprise_Id__c = String.valueof(2000);
            objMemAccount.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            objMemAccount.Birthdate__c = String.valueOf(System.today().month()+'/'+system.today().day()+'/'+System.today().year());
            objMemAccount.Phone='7869439456';
            objMemAccount.PersonOtherPhone='7754215436';
            
            insert objMemAccount;
            system.assertEquals(objMemAccount.LastName == 'LName', true);
            // Policy
            Policy__c oMedicalPolicy = HUMTestDataHelperTest.getPolicy();
            oMedicalPolicy.Name = 'Medical Product';
            oMedicalPolicy.source_cust_cov_key__c = '12345678901234567890' ; 
            oMedicalPolicy.Group_Name__c = objMemAccount.Id;
            oMedicalPolicy.Major_LOB__c ='EPO';          
            insert oMedicalPolicy;
            system.assertEquals(oMedicalPolicy.Name == 'Medical Product', true);
            // Policy Member
            Policy_Member__c objMedPolMem = HUMTestDataHelperTest.getPolicyMember();
            objMedPolMem.Member__c = objMemAccount.Id;
            objMedPolMem.Policy__c = oMedicalPolicy.Id;
            objMedPolMem.Relationship__c = 'self';    
            objMedPolMem.end_date__c = '12/31/2015'; 
            objMedPolMem.Effective_Date__c='01/01/2015';       
            objMedPolMem.Policy_Member_External_ID__c = '00|ODS|123456789|89|89|02-01-2001';
            insert objMedPolMem;
            system.assertEquals(objMedPolMem.Relationship__c == 'self',true);
        }
    } 
    
   	
    static testMethod void testAddressService()    
    {
        User testUser = [Select Id, Name from User where LastName='test User1'];
        System.runAs(testUser)
        {
            Test.startTest();
            try {
                Continuation continueObj;
                HttpResponse httpRes;
                String resBodyString;
                String reqType;
                Map<String, HTTPRequest> mapDetails;
                Httpresponse resDemographic;
                AddressValidationHelper_DTO_HUM addObj = new AddressValidationHelper_DTO_HUM();
                addObj.enterpriseID = 'test';
                addObj.statusValue = 'test';
                addObj.addresstype = 'test';
                addObj.addressValidatedFlag = 'test';
                addObj.overrideReasonCode = '0';
                EAS_AddressStandardizationRequest_H_HUM.EAS_AddressStandardRequest_H_HUM dto = new EAS_AddressStandardizationRequest_H_HUM.EAS_AddressStandardRequest_H_HUM();
                dto.AddressLine1 = 'test';
                dto.AddressLine2 = 'test';
                dto.City = 'test';
                dto.StateCode = 'test';
                dto.ZipCode = 'test';
                addObj.addressReq = dto;
                string addressDTO = JSON.serialize(addObj);
                continueObj = (Continuation)PharmacyCreateOrder_LC_HUM.invokeEASService(addressDTO);
                continueObj.continuationMethod = 'addressResponse';
                continueObj.state = addObj;
                resDemographic = new Httpresponse ();
                String reqEASResponse = '{ "StandardizeAddressResponse":{ "SuccessFlag":"Y", "AddressLine1":"9863 GEORGE WASHINGTON MEMORIAL HWY", "City":"GLOUCESTER", "StateCode":"VA", "ZipCode":"23061", "StateID":"51", "ZipCodePlus":"4100", "CountyName":"GLOUCESTER", "CountyID":"073", "Valid":"true", "ChangeIndicator":"GOOD" } }';
                resDemographic.setBody(reqEASResponse );
                system.assert(resDemographic.getBody() == reqEASResponse);
                reqType = 'Continuation-1';        
                List<string> labelGetmember = new List<string>();
                labelGetmember.add(reqType);
                Test.setContinuationResponse(reqType,resDemographic);
                object result = PharmacyCreateOrder_LC_HUM.addressResponse(labelGetmember,continueObj);
                Boolean bDataExist =  Json.serialize(result).containsIgnoreCase('9863') ? true : false;
                System.assertEquals(false, bDataExist);
            } catch (Exception e) {
                system.assertEquals('Script-thrown exception', e.getMessage(),'success');
            }            
            Test.stopTest();
        }        
    }
    
    static testmethod void testAddressnull(){
        User testUser = [Select Id, Name from User where LastName='test User1'];
        System.runAs(testUser)
        {
            Test.startTest();
            try{
                Continuation continueObj;
                HttpResponse httpRes;
                String resBodyString;
                String reqType;
                continueObj = (Continuation)PharmacyCreateOrder_LC_HUM.invokeEASService(null);
                continueObj.continuationMethod = 'addressResponse';
                continueObj.state = 'hello';
                httpRes = new Httpresponse ();
                String reqEASResponse = '{ "StandardizeAddressResponse":{ "SuccessFlag":"Y", "AddressLine1":"9863 GEORGE WASHINGTON MEMORIAL HWY", "City":"GLOUCESTER", "StateCode":"VA", "ZipCode":"23061", "StateID":"51", "ZipCodePlus":"4100", "CountyName":"GLOUCESTER", "CountyID":"073", "Valid":"true", "ChangeIndicator":"GOOD" } }';
                httpRes.setBody(reqEASResponse );
                system.assert(httpRes.getBody() == reqEASResponse);
                reqType = 'Continuation-1';        
                List<string> labelGetmember = new List<string>();
                labelGetmember.add(reqType);
                Test.setContinuationResponse(reqType,httpRes);
                object result = PharmacyCreateOrder_LC_HUM.addressResponse(labelGetmember,continueObj);
                Test.stopTest();
            }
            catch(Exception ex){
                string message = ex.getStackTraceString();
                System.assertEquals(true, message.containsIgnoreCase('invokeEASService'));
            }
        }
    }
    
    static testMethod void testAddressServiceNegative()    
    {
        User testUser = [Select Id, Name from User where LastName='test User1'];
        System.runAs(testUser)
        {
            Test.startTest();
            try {
                Continuation continueObj;
                HttpResponse httpRes;
                String resBodyString;
                String reqType;
                Map<String, HTTPRequest> mapDetails;
                Httpresponse resDemographic;
                AddressValidationHelper_DTO_HUM addObj = new AddressValidationHelper_DTO_HUM();
                addObj.enterpriseID = 'test';
                addObj.statusValue = 'test';
                addObj.addresstype = 'test';
                addObj.addressValidatedFlag = 'test';
                addObj.overrideReasonCode = '0';
                EAS_AddressStandardizationRequest_H_HUM.EAS_AddressStandardRequest_H_HUM dto = new EAS_AddressStandardizationRequest_H_HUM.EAS_AddressStandardRequest_H_HUM();
                dto.AddressLine1 = 'test';
                dto.AddressLine2 = 'test';
                dto.City = 'test';
                dto.StateCode = 'test';
                dto.ZipCode = 'test';
                addObj.addressReq = dto;
                string addressDTO = JSON.serialize(addObj);
                continueObj = (Continuation)PharmacyCreateOrder_LC_HUM.invokeEASService(addressDTO);
                continueObj.continuationMethod = 'addressResponse';
                continueObj.state = dto;
                resDemographic = new Httpresponse ();
                String reqEASResponse = '{ "StandardizeAddressResponse":{ "SuccessFlag":"Y", "AddressLine1":"9863 GEORGE WASHINGTON MEMORIAL HWY", "City":"GLOUCESTER", "StateCode":"VA", "ZipCode":"23061", "StateID":"51", "ZipCodePlus":"4100", "CountyName":"GLOUCESTER", "CountyID":"073", "Valid":"true", "ChangeIndicator":"GOOD" } }';
                resDemographic.setBody(reqEASResponse );
                system.assert(resDemographic.getBody() == reqEASResponse);
                reqType = 'Continuation-1';        
                List<string> labelGetmember = new List<string>();
                labelGetmember.add(reqType);
                Test.setContinuationResponse(reqType,resDemographic);
                object result = PharmacyCreateOrder_LC_HUM.addressResponse(labelGetmember,continueObj);
                Boolean bDataExist =  Json.serialize(result).containsIgnoreCase('9863') ? true : false;
                System.assertEquals(false, bDataExist); 
            } catch (Exception e) {
                system.assertEquals('Script-thrown exception', e.getMessage(),'success');
            }            
            Test.stopTest();
        }        
    }
    
    static testMethod void testupdateGetMember(){        
        User testUser = [Select Id, Name from User where LastName='test User1'];
        Account account = [select Id,Enterprise_ID__c from Account limit 1];
        System.runAs(testUser)
		{
            Test.startTest();
            try {
                Continuation continueObj;
                Httpresponse resDemographic;
                String reqType;
                AddressValidationHelper_DTO_HUM addObj = new AddressValidationHelper_DTO_HUM();
                addObj.enterpriseID = 'test';
                addObj.statusValue = 'test';
                addObj.addresstype = 'test';
                addObj.addressValidatedFlag = 'test';
                addObj.overrideReasonCode = '0';
                string addressDTO = JSON.serialize(addObj);
                continueObj = (Continuation)PharmacyCreateOrder_LC_HUM.updateGetMember('1070177218','test','test','test','test','RXK4284',account.Id,addressDTO);
                continueObj.continuationMethod = 'retrieveUpdatedGetMember';
                continueObj.state = '1070177218_RXK4284_'+account.Id;
                resDemographic = new Httpresponse ();
                String EditRes = '{ "EditMemberResponse":{ "transactionKey":"1120000275944", "transactionTimeStamp":"Tue, Mar 21 2017 08:55:28 313 EDT", "UID":"" } }';
                resDemographic.setBody(EditRes);
                system.assert(resDemographic.getBody() == EditRes);
                reqType = 'Continuation-1';        
                List<string> labelGetmember = new List<string>();
                labelGetmember.add(reqType);
                Test.setContinuationResponse(reqType,resDemographic);
                Object result = PharmacyCreateOrder_LC_HUM.retrieveUpdatedGetMember(labelGetmember,continueObj);
                Boolean bDataExist =  Json.serialize(result).containsIgnoreCase('1120000275944') ? true : false;
                System.assertEquals(true, bDataExist);
            } catch (Exception e) {
                system.assertEquals('Script-thrown exception', e.getMessage(),'success');
            }            
            Test.stopTest();
        }
    }
    
    static testMethod void testupdateGetMemberFault(){        
        User testUser = [Select Id, Name from User where LastName='test User1'];
        Account account = [select Id,Enterprise_ID__c from Account limit 1];
        System.runAs(testUser)
		{
            Test.startTest();
            try {
                Continuation continueObj;
                Httpresponse resDemographic;
                String reqType;
                AddressValidationHelper_DTO_HUM addObj = new AddressValidationHelper_DTO_HUM();
                addObj.enterpriseID = 'test';
                addObj.statusValue = 'test';
                addObj.addresstype = 'test';
                addObj.addressValidatedFlag = 'test';
                addObj.overrideReasonCode = '0';
                string addressDTO = JSON.serialize(addObj);
                continueObj = (Continuation)PharmacyCreateOrder_LC_HUM.updateGetMember('1070177218','test','test','test','test','RXK4284',account.Id,addressDTO);
                continueObj.continuationMethod = 'retrieveUpdatedGetMember';
                continueObj.state = '1070177218_RXK4284_'+account.Id;
                resDemographic = new Httpresponse ();
                String EditRes = 'ADDR0{"Fault": {   "faultcode": "\n          Code-110..1",   "faultstring": "\n          error_service_name :\"PriorAuthHubService |\n          error_service_method : GetPriorAuthStatusHistory |\n          fault_message : ",   "detail":    {      "transactionInfo":       {         "transaction-id": "724757268",         "transaction-client": "10.114.96.251",         "URL-in": "http://127.0.0.1:8317/PriorAuthHubService_V2.0/GetPriorAuthStatusHistory",         "processor-name": "JSONServicesGatewayNonXML",         "processor-type": "Multiprotocol Gateway"      },      "errorInfo":       {         "error-code": "0x00d30003",         "error-headers": "HTTP/1.1 500 Internal Server Error\r\nX-Backside-Transport: OK OK\r\nContent-Type: text/xml\r\nConnection: close\r\n\r\n",         "error-message": "105-Member does not have any Prior Auth history",         "error-protocol-reason-phrase": "",         "error-protocol-response": "0",         "error-subcode": "0x00d30003"      }   }}}';
                resDemographic.setBody(EditRes);
                system.assert(resDemographic.getBody() == EditRes);
                reqType = 'Continuation-1';        
                List<string> labelGetmember = new List<string>();
                labelGetmember.add(reqType);
                Test.setContinuationResponse(reqType,resDemographic);
                Object result = PharmacyCreateOrder_LC_HUM.retrieveUpdatedGetMember(labelGetmember,continueObj);
                Boolean bDataExist =  Json.serialize(result).containsIgnoreCase('IncompatibleZipCity') ? true : false;
                System.assertEquals(true, bDataExist);
            } catch (Exception e) {
                system.assertEquals('Script-thrown exception', e.getMessage(),'success');
            }            
            Test.stopTest();
        }
    }
    
    
    static testMethod void testCreateOrder(){
        User testUser = [Select Id, Name from User where LastName='test User1'];
        Account account = [select Id,Enterprise_ID__c from Account limit 1];
        System.runAs(testUser)
		{
            Test.startTest();
            try {
                Continuation continueObj;
                Httpresponse resDemographic;
                String reqType;
                string createOrderRequest = '{"CreateAndEditOrderRequest":{"EnterprisePersonID":"1087808915","CustomerServiceId":"ARM5290","orderKey":"","orderReleaseDate":"11/15/2021","deliveryInstruction":"","moveToRouting":{"orderKey":[]},"orderPlatform":"","address":{"addressKey":"-1","addressLine1":"9836 CAMDEN STREET","addressLine2":"","addressName":"QUENEOSHI MOURINO","addressType":"S","city":"DWALE","isActive":"true","overrideReasonCode":"","stateCode":"KY","uspsValidate":"true","zipCode":"41621"},"paymentMethod":[{"paymentMethodType":"Invoice"}],"scriptOrders":{"script":[{"scriptKey":"394925392","consentStatus":"Approved"}]},"shippingMethodCode":"1"}}';
                continueObj = (Continuation)PharmacyCreateOrder_LC_HUM.invokecreateEditOrderDetail(createOrderRequest,account.Id);
                continueObj.continuationMethod = 'parseCreateEditOrderResponse';
                continueObj.state = '1070177218_RXK4284_'+account.Id;
                resDemographic = new Httpresponse ();
                String EditRes = '{ "CreateAndEditOrderResponse":{ "transactionKey":"1120000275944", "transactionTimeStamp":"Tue, Mar 21 2017 08:55:28 313 EDT", "UID":"", "ErrorDescription":"", "orderKey":"3924764"  } }';
                resDemographic.setBody(EditRes);
                system.assert(resDemographic.getBody() == EditRes);
                reqType = 'Continuation-1';        
                List<string> labelGetmember = new List<string>();
                labelGetmember.add(reqType);
                Test.setContinuationResponse(reqType,resDemographic);
                Object result = PharmacyCreateOrder_LC_HUM.parseCreateEditOrderResponse(labelGetmember,continueObj);
                Boolean bDataExist =  Json.serialize(result).containsIgnoreCase('3924764') ? true : false;
                System.assertEquals(true, bDataExist);
            } catch (Exception e) {
                system.assertEquals('Script-thrown exception', e.getMessage(),'success');
            }            
            Test.stopTest();
        }
    }
    
    static testMethod void testCreateOrderNegative(){        
        User testUser = [Select Id, Name from User where LastName='test User1'];
        Account account = [select Id,Enterprise_ID__c from Account limit 1];
        System.runAs(testUser)
		{
            Test.startTest();
            try {
                Continuation continueObj;
                Httpresponse resDemographic;
                String reqType;
                string createOrderRequest = '{"CreateAndEditOrderRequest":{"EnterprisePersonID":"1087808915","CustomerServiceId":"ARM5290","orderKey":"","orderReleaseDate":"11/15/2021","deliveryInstruction":"","moveToRouting":{"orderKey":[]},"orderPlatform":"","address":{"addressKey":"-1","addressLine1":"9836 CAMDEN STREET","addressLine2":"","addressName":"QUENEOSHI MOURINO","addressType":"S","city":"DWALE","isActive":"true","overrideReasonCode":"","stateCode":"KY","uspsValidate":"true","zipCode":"41621"},"paymentMethod":[{"paymentMethodType":"Invoice"}],"scriptOrders":{"script":[{"scriptKey":"394925392","consentStatus":"Approved"}]},"shippingMethodCode":"1"}}';
                continueObj = (Continuation)PharmacyCreateOrder_LC_HUM.invokecreateEditOrderDetail(createOrderRequest,account.Id);
                continueObj.continuationMethod = 'parseCreateEditOrderResponse';
                continueObj.state = '1070177218_RXK4284_'+account.Id;
                resDemographic = new Httpresponse ();
                String EditRes = '{ "CreateAndEditOrderResponse":';
                resDemographic.setBody(EditRes);
                system.assert(resDemographic.getBody() == EditRes);
                reqType = 'Continuation-1';        
                List<string> labelGetmember = new List<string>();
                labelGetmember.add(reqType);
                Test.setContinuationResponse(reqType,resDemographic);
                Object result = PharmacyCreateOrder_LC_HUM.parseCreateEditOrderResponse(labelGetmember,continueObj);
                Boolean bDataExist =  Json.serialize(result).containsIgnoreCase('3924764') ? true : false;
                System.assertEquals(false, bDataExist);
                System.assertEquals('null', result);
            } catch (Exception e) {
                system.assertEquals('Script-thrown exception', e.getMessage(),'success');
            }            
            Test.stopTest();
        }
        
    }
    
    static testMethod void testCreateOrderFault(){        
        User testUser = [Select Id, Name from User where LastName='test User1'];
        Account account = [select Id,Enterprise_ID__c from Account limit 1];
        System.runAs(testUser)
		{
            Test.startTest();
            try {
                Continuation continueObj;
                Httpresponse resDemographic;
                String reqType;
                string createOrderRequest = '{"CreateAndEditOrderRequest":{"EnterprisePersonID":"1087808915","CustomerServiceId":"ARM5290","orderKey":"","orderReleaseDate":"11/15/2021","deliveryInstruction":"","moveToRouting":{"orderKey":[]},"orderPlatform":"","address":{"addressKey":"-1","addressLine1":"9836 CAMDEN STREET","addressLine2":"","addressName":"QUENEOSHI MOURINO","addressType":"S","city":"DWALE","isActive":"true","overrideReasonCode":"","stateCode":"KY","uspsValidate":"true","zipCode":"41621"},"paymentMethod":[{"paymentMethodType":"Invoice"}],"scriptOrders":{"script":[{"scriptKey":"394925392","consentStatus":"Approved"}]},"shippingMethodCode":"1"}}';
                continueObj = (Continuation)PharmacyCreateOrder_LC_HUM.invokecreateEditOrderDetail(createOrderRequest,account.Id);
                continueObj.continuationMethod = 'parseCreateEditOrderResponse';
                continueObj.state = '1070177218_RXK4284_'+account.Id;
                resDemographic = new Httpresponse ();
                String EditRes = '{"Fault": {   "faultcode": "\n          Code-110..1",   "faultstring": "\n          error_service_name :\"PriorAuthHubService |\n          error_service_method : GetPriorAuthStatusHistory |\n          fault_message : ",   "detail":    {      "transactionInfo":       {         "transaction-id": "724757268",         "transaction-client": "10.114.96.251",         "URL-in": "http://127.0.0.1:8317/PriorAuthHubService_V2.0/GetPriorAuthStatusHistory",         "processor-name": "JSONServicesGatewayNonXML",         "processor-type": "Multiprotocol Gateway"      },      "errorInfo":       {         "error-code": "0x00d30003",         "error-headers": "HTTP/1.1 500 Internal Server Error\r\nX-Backside-Transport: OK OK\r\nContent-Type: text/xml\r\nConnection: close\r\n\r\n",         "error-message": "105-Member does not have any Prior Auth history",         "error-protocol-reason-phrase": "",         "error-protocol-response": "0",         "error-subcode": "0x00d30003"      }   }}}';
                resDemographic.setBody(EditRes);
                system.assert(resDemographic.getBody() == EditRes);
                reqType = 'Continuation-1';        
                List<string> labelGetmember = new List<string>();
                labelGetmember.add(reqType);
                Test.setContinuationResponse(reqType,resDemographic);
                Object result = PharmacyCreateOrder_LC_HUM.parseCreateEditOrderResponse(labelGetmember,continueObj);
                Boolean bDataExist =  Json.serialize(result).containsIgnoreCase('3924764') ? true : false;
                System.assertEquals(false, bDataExist);
                System.assertEquals('null', result);
            } catch (Exception e) {
                system.assertEquals('Script-thrown exception', e.getMessage(),'success');
            }            
            Test.stopTest();
        }
        
    }
    
    static testMethod void testLogNote(){
        User testUser = [Select Id, Name from User where LastName='test User1'];
        Account account = [select Id,Enterprise_ID__c from Account limit 1];
        System.runAs(testUser)
		{
            Test.startTest();
            try {
                Continuation continueObj;
                Httpresponse resDemographic;
                String reqType;
                continueObj = (Continuation)PharmacyCreateOrder_LC_HUM.invokeLogNoteRequest(0,'1070177218','1070177218',account.Id);
                continueObj.continuationMethod = 'logNoteResponse';
                continueObj.state = '1070177218_RXK4284_'+account.Id;
                resDemographic = new Httpresponse ();
                String EditRes = '{ "CreateAndEditOrderResponse":{ "transactionKey":"1120000275944", "transactionTimeStamp":"Tue, Mar 21 2017 08:55:28 313 EDT", "UID":"", "ErrorDescription":"", "orderKey":"3924764"  } }';
                resDemographic.setBody(EditRes);
                system.assert(resDemographic.getBody() == EditRes);
                reqType = 'Continuation-1';        
                List<string> labelGetmember = new List<string>();
                labelGetmember.add(reqType);
                Test.setContinuationResponse(reqType,resDemographic);
                Object result = PharmacyCreateOrder_LC_HUM.logNoteResponse(labelGetmember,continueObj);
                Boolean bDataExist =  Json.serialize(result).containsIgnoreCase('3924764') ? true : false;
                System.assertEquals(true, bDataExist);
            } catch (Exception e) {
                system.assertEquals('Script-thrown exception', e.getMessage(),'success');
            }            
            Test.stopTest();
        }
    }
    
    static testMethod void testLogNoteFault(){        
        User testUser = [Select Id, Name from User where LastName='test User1'];
        Account account = [select Id,Enterprise_ID__c from Account limit 1];
        System.runAs(testUser)
		{
            Test.startTest();
            try {
                Continuation continueObj;
                Httpresponse resDemographic;
                String reqType;
                continueObj = (Continuation)PharmacyCreateOrder_LC_HUM.invokeLogNoteRequest(0,'1070177218','1070177218',account.Id);
                continueObj.continuationMethod = 'logNoteResponse';
                continueObj.state = '1070177218_RXK4284_'+account.Id;
                resDemographic = new Httpresponse ();
                String EditRes = '{"Fault": {   "faultcode": "\n          Code-110..1",   "faultstring": "\n          error_service_name :\"PriorAuthHubService |\n          error_service_method : GetPriorAuthStatusHistory |\n          fault_message : ",   "detail":    {      "transactionInfo":       {         "transaction-id": "724757268",         "transaction-client": "10.114.96.251",         "URL-in": "http://127.0.0.1:8317/PriorAuthHubService_V2.0/GetPriorAuthStatusHistory",         "processor-name": "JSONServicesGatewayNonXML",         "processor-type": "Multiprotocol Gateway"      },      "errorInfo":       {         "error-code": "0x00d30003",         "error-headers": "HTTP/1.1 500 Internal Server Error\r\nX-Backside-Transport: OK OK\r\nContent-Type: text/xml\r\nConnection: close\r\n\r\n",         "error-message": "105-Member does not have any Prior Auth history",         "error-protocol-reason-phrase": "",         "error-protocol-response": "0",         "error-subcode": "0x00d30003"      }   }}}';
                resDemographic.setBody(EditRes);
                system.assert(resDemographic.getBody() == EditRes);
                reqType = 'Continuation-1';        
                List<string> labelGetmember = new List<string>();
                labelGetmember.add(reqType);
                Test.setContinuationResponse(reqType,resDemographic);
                Object result = PharmacyCreateOrder_LC_HUM.logNoteResponse(labelGetmember,continueObj);
                Boolean bDataExist =  Json.serialize(result).containsIgnoreCase('3924764') ? true : false;
                System.assertEquals(false, bDataExist);
                System.assertEquals(null, result);  
            } catch (Exception e) {
                system.assertEquals('Script-thrown exception', e.getMessage(),'success');
            }            
            Test.stopTest();
        }        
    }
    
    static testMethod void testReleaseOrder(){
        User testUser = [Select Id, Name from User where LastName='test User1'];
        Account account = [select Id,Enterprise_ID__c from Account limit 1];
        System.runAs(testUser)
		{
            Test.startTest();
            try {
                Continuation continueObj;
                Httpresponse resDemographic;
                String reqType;
                string createOrderRequest = '{"CreateAndEditOrderRequest":{"EnterprisePersonID":"1086968117","CustomerServiceId":"ARM5290","orderKey":"393437902","userDefined1":"401189222,","scriptOrders":{"script":[{"scriptKey":"401189222,","coPayConsent":true}]},"overrideQueueConsent":true}}';
                continueObj = (Continuation)PharmacyOrderDetail_LC_HUM.sendRequestReleaseAndCancelOrder(createOrderRequest,account.Id);
                continueObj.continuationMethod = 'processRelOrderResponse';
                continueObj.state = '1070177218_RXK4284_'+account.Id;
                resDemographic = new Httpresponse ();
                String EditRes = 'System.HttpResponse[Status=null, StatusCode=200]';
                resDemographic.setBody(EditRes);
                system.assert(resDemographic.getBody() == EditRes);
                reqType = 'Continuation-1';        
                List<string> labelGetmember = new List<string>();
                labelGetmember.add(reqType);
                Test.setContinuationResponse(reqType,resDemographic);
                Object result = PharmacyOrderDetail_LC_HUM.processRelOrderResponse(labelGetmember,continueObj);           
                Boolean bDataExist =  Json.serialize(result).containsIgnoreCase('null') ? true : false;
                System.assertEquals(true, bDataExist);
            } catch (Exception e) {
                system.assertEquals('Script-thrown exception', e.getMessage(),'success');
            }            
            Test.stopTest();
        }
    }
	
	
    static testMethod void testReleaseOrderNegative(){        
        User testUser = [Select Id, Name from User where LastName='test User1'];
        Account account = [select Id,Enterprise_ID__c from Account limit 1];
        System.runAs(testUser)
		{
            Test.startTest();
            try {
                Continuation continueObj;
                Httpresponse resDemographic;
                String reqType;
                string createOrderRequest = '{"CreateAndEditOrderRequest":{"EnterprisePersonID":"1086968117","CustomerServiceId":"ARM5290","orderKey":"393437902","userDefined1":"401189222,","scriptOrders":{"script":[{"scriptKey":"401189222,","coPayConsent":true}]},"overrideQueueConsent":true}}';
                continueObj = (Continuation)PharmacyOrderDetail_LC_HUM.sendRequestReleaseAndCancelOrder(createOrderRequest,account.Id);
                continueObj.continuationMethod = 'processRelOrderResponse';
                continueObj.state = '1070177218_RXK4284_'+account.Id;
                resDemographic = new Httpresponse ();
                String EditRes = 'System.HttpResponse[Status=null, StatusCode=200]';
                resDemographic.setBody(EditRes);
                system.assert(resDemographic.getBody() == EditRes);
                reqType = 'Continuation-1';        
                List<string> labelGetmember = new List<string>();
                labelGetmember.add(reqType);
                Test.setContinuationResponse(reqType,resDemographic);
                Object result = PharmacyOrderDetail_LC_HUM.processRelOrderResponse(labelGetmember,continueObj);
                Boolean bDataExist =  Json.serialize(result).containsIgnoreCase('3924764') ? true : false;
                System.assertEquals(false, bDataExist);   
            }catch (Exception e) {
                system.assertEquals('Script-thrown exception', e.getMessage(),'success');
            }                    
            Test.stopTest();
        }        
    }
    
    static testMethod void testCancelOrder(){
        User testUser = [Select Id, Name from User where LastName='test User1'];
        Account account = [select Id,Enterprise_ID__c from Account limit 1];
        System.runAs(testUser)
		{
            Test.startTest();
            try {
                Continuation continueObj;
                Httpresponse resDemographic;
                String reqType;
                string createOrderRequest = '{"CreateAndEditOrderRequest":{"EnterprisePersonID":"1086968117","CustomerServiceId":"ARM5290","orderKey":"393437902","cancelOrder":"true","orderPlatform":"trad"}}';
                continueObj = (Continuation)PharmacyOrderDetail_LC_HUM.sendRequestReleaseAndCancelOrder(createOrderRequest,account.Id);
                continueObj.continuationMethod = 'processRelOrderResponse';
                continueObj.state = '1070177218_RXK4284_'+account.Id;
                resDemographic = new Httpresponse ();
                String EditRes = 'System.HttpResponse[Status=null, StatusCode=200]';
                resDemographic.setBody(EditRes);
                system.assert(resDemographic.getBody() == EditRes);
                reqType = 'Continuation-1';        
                List<string> labelGetmember = new List<string>();
                labelGetmember.add(reqType);
                Test.setContinuationResponse(reqType,resDemographic);
                Object result = PharmacyOrderDetail_LC_HUM.processRelOrderResponse(labelGetmember,continueObj);           
                Boolean bDataExist =  Json.serialize(result).containsIgnoreCase('null') ? true : false;
                System.assertEquals(true, bDataExist);
            } catch (Exception e) {
                system.assertEquals('Script-thrown exception', e.getMessage(),'success');
            }            
            Test.stopTest();
        }
    }
	
	
    static testMethod void testCancelOrderNegative(){        
        User testUser = [Select Id, Name from User where LastName='test User1'];
        Account account = [select Id,Enterprise_ID__c from Account limit 1];
        System.runAs(testUser)
		{
            Test.startTest();   
            try {
                Continuation continueObj;
                Httpresponse resDemographic;
                String reqType;
                string createOrderRequest = '{"CreateAndEditOrderRequest":{"EnterprisePersonID":"1086968117","CustomerServiceId":"ARM5290","orderKey":"393437902","cancelOrder":"true","orderPlatform":"trad"}}';
                continueObj = (Continuation)PharmacyOrderDetail_LC_HUM.sendRequestReleaseAndCancelOrder(createOrderRequest,account.Id);
                continueObj.continuationMethod = 'processRelOrderResponse';
                continueObj.state = '1070177218_RXK4284_'+account.Id;
                resDemographic = new Httpresponse ();
                String EditRes = 'System.HttpResponse[Status=null, StatusCode=200]';
                resDemographic.setBody(EditRes);
                system.assert(resDemographic.getBody() == EditRes);
                reqType = 'Continuation-1';        
                List<string> labelGetmember = new List<string>();
                labelGetmember.add(reqType);
                Test.setContinuationResponse(reqType,resDemographic);
                Object result = PharmacyOrderDetail_LC_HUM.processRelOrderResponse(labelGetmember,continueObj);
                Boolean bDataExist =  Json.serialize(result).containsIgnoreCase('3924764') ? true : false;
                System.assertEquals(false, bDataExist);      
            } catch (Exception e) {
                system.assertEquals('Script-thrown exception', e.getMessage(),'success');
            }
            Test.stopTest();
        }        
    }
}