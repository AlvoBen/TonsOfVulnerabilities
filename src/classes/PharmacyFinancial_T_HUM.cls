/*******************************************************************************************************************************
Apex Class Name : PharmacyFinancial_T_HUM.cls
Version         : 1.0
Created On      : Dec 20,2016
Function        : Class contains test methods PharmacyFinancial_C_HUM class.

Modification Log: 
 * Developer Name            Code Review                Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Avinash Choubey            70274                 12/20/2016                   Original Version (REQ 291096)
 * Sharan Shanmugam		      70274					02/16/2017					 As part of REQ - 296663 and REQ - 296664, modified code to cover GetMember details and GetMemberFinanceAndAdjustments details
 * Vishal Verma				  74192     	        08/03/2017					 Implementation of REQ - 296665 : View adjustment information from GetMemberFinanceAndAdjustments details
 * Sharan Shanmugam           74192                 03/21/2017                   REQ - 296877 - Update Credit Card information
 * Sharan Shnamugam           74192                 03/22/2017                   REQ - 300147 : Create finance request 
 * Vishal Verma               73803                 03/24/2017                   REQ - 296881: Add New Credit Card 	
 * Vishal Verma               82890                 04/07/2017                   REQ - 301358 : Send Email for financial request
 * Sharan Shanmugam									07/27/2017					 REQ - 323542 : Exception handling  
 * Sumit Pharande            345615                 03/27/2019                   REQ - 362063 - Payment Card Project: do not allow users to inactivate a cc that’s attached to an order in progress
 * Shailesh Mali                                    08/26/2020                   REQ - 1152078 - PR00094920- MF 1  - Humana Pharmacy- Ability to create realtime one time credit card charges in CRM  (RxXP) (ID# 13)
 ********************************************************************************************************************************/
@isTest
private class PharmacyFinancial_T_HUM 
{
    
	/**
    * @testSetup method to create test data
    *<p>
    * @param            NULL
    * @return           void
    */
    @testSetup static void setUpTestData()
    {
        User objUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert objUser;
         User objUser1 = HUMTestDataHelperTest.createTestUser('FinancialUser1','Customer Care Specialist');
        insert objUser1;
        HUM_Webservice_Callout__c RighSourceServiceDetails = new HUM_Webservice_Callout__c(Name = 'RighSourceService_GetMember',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c ='https://int-crmservicews.humana.com/RighSourceService_GetMember/' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
        insert RighSourceServiceDetails;
        HUM_Webservice_Callout__c RighSourceAdjPayServiceDetails = new HUM_Webservice_Callout__c(Name = 'RS_GetAdjustmentsAndPayments',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c ='https://int-crmservicews.humana.com/RS_GetAdjustmentsAndPayments/' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
        insert RighSourceAdjPayServiceDetails;
        HUM_Webservice_Callout__c RighSourceEditServiceDetails = new HUM_Webservice_Callout__c(Name = 'RighSourceService_EditMember',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c ='https://int-crmservicews.humana.com/RighSourceService_GetMember/' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
        insert RighSourceEditServiceDetails;
		HUM_Webservice_Callout__c emailUtility = new HUM_Webservice_Callout__c(Name = 'EmailUtilityService',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c ='https://int-crmservicews.humana.com/RighSourceService_GetMember/' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );		
        insert emailUtility;
        HUM_Webservice_Callout__c PCI = new HUM_Webservice_Callout__c(Name = 'PCI',End_Point_URL__c ='https://int-crmservicews.humana.com/RighSourceService_GetMember/' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
        insert PCI;
		Hum_Webservice_Callout__c RightSource=new HUM_Webservice_Callout__c(Name='RighSourceService_GetOrderDetails',End_Point_URL__c='https://dev-crmservicews.humana.com/RightSourceService_V1.0/GetOrderDetails',Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json'); 
        insert RightSource;
        Hum_Webservice_Callout__c RightSourceOrder=new HUM_Webservice_Callout__c(Name='RighSourceService_GetOrders',End_Point_URL__c='https://dev-crmservicews.humana.com/RightSourceService_V1.0/GetOrders',Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json'); 
        insert RightSourceOrder;
        system.assertEquals(objUser.lastname == 'test User1',true);
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
      
	/**
    * Test method for pharmacy data.
    *<p>
    * @param            NULL
    * @return           void
    */
    static testMethod void testMedicalPharmacy()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        policy_member__c policyObj = [select id,Relationship__c,name from policy_member__c where Relationship__c = 'self' limit 1];
        system.assertEquals(policyObj.Relationship__c,'self');  
        System.runAs(testUser)
        {
            Test.startTest();
                Test.setCurrentPage(page.PharmacyFinancial_VF_HUM);
                ApexPages.currentPage().getParameters().put('Id',policyObj.Id);
                PharmacyFinancial_C_HUM objPharmFin = new PharmacyFinancial_C_HUM();
                objPharmFin.getPolicyMemberData();
                system.assertEquals(policyObj.name,objPharmFin.objPolMem.name);  
                objPharmFin.getThis();
                objPharmFin.getRecords();
                objPharmFin.getRecord();
            Test.stopTest();
        }
    }
    
	/**
    * Positive test method for Pharmacy Credit Card and Payment funtionality.
    *<p>
    * @param            NULL
    * @return           void
    */
	static testMethod void testPharmacyFinancial()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.PharmacyFinancial_VF_HUM);
            ApexPages.StandardController scon = new ApexPages.StandardController(policyObj);
            PharmacyFinancial_C_HUM pharmacyCreditCard = new PharmacyFinancial_C_HUM();
            PharmacyFinancial_S_HUM oServicePharmacy = new PharmacyFinancial_S_HUM();
            Continuation continueObj = (Continuation)PharmacyFinancial_C_HUM.invokeGetMemService('1048868178','AXB0001','0015C000002T9uJ');
            continueObj.continuationMethod = 'parseRSGetMemberCreditResponse';
            continueObj.state = 'Both';
            HttpResponse httpRes = new Httpresponse ();
            String resBodyString= '{"GetMemberReponse":{"Customers":{"Customer":[{"StaleData":"","SpecialtyVariantObjects":{"CapType":"S"},"Plans":{"Plan":[{"PlanType":"CASH RX","PlanTerminationDate":"","PlanEffectiveDate":""},{"PlanType":"MEDIMPACT OTC","PlanTerminationDate":"","PlanEffectiveDate":"07/29/2015"},{"PlanType":"MA","PlanTerminationDate":"","PlanEffectiveDate":"07/29/2015"}]},"Phones":{"Phone":[{"PhoneValue":"5025801000","PhoneType":"PP","PhoneKey":"7292489"}]},"Payment":{"NonCreditCard":{"Type":"","PaymentNumber":""}},"LogNotes":{"LogNote":[]},"HealthConditions":{"HealthCondition":[{"HealthDescription":"NO KNOWN MEDICAL HISTORY"}]},"Finance":{"PaymentSchedules":{"PaymentSchedule":[]},"OutstandingBalance":"","CreditLimit":"","Adjustments":{"Adjustment":[]}},"CustomerIdentity":{"MiddleName":"","LastName":"DAGGETT","FirstName":"KATHLEEN","Email":"gsheppard@humana.com","CustomerKeys":{"CustomerKey":[{"CustomerValue":"4193538","CustomerType":"PK"},{"CustomerValue":"1031063837","CustomerType":"PM"},{"CustomerValue":"6895629","CustomerType":"AC"}]},"BirthDate":"09/12/1944"},"CustomerDetails":null,"CreditCards":{"CreditCard":[{"IsAutoCharge":"false","IsActive":"true","ExpirationYear":"2018","ExpirationMonth":"11","CreditCardType":"M","CreditCardLast4Digits":"4315830000415489"},{"IsAutoCharge":"false","IsActive":"true","ExpirationYear":"2018","ExpirationMonth":"11","CreditCardType":"M","CreditCardLast4Digits":"4315830000415494"},{"IsAutoCharge":"false","IsActive":"true","ExpirationYear":"2018","ExpirationMonth":"11","CreditCardType":"M","CreditCardLast4Digits":"4315830000415497"},{"IsAutoCharge":"false","IsActive":"true","ExpirationYear":"2016","ExpirationMonth":"10","CreditCardType":"V","CreditCardLast4Digits":"3371953113715499"},{"IsAutoCharge":"false","IsActive":"true","ExpirationYear":"2018","ExpirationMonth":"11","CreditCardType":"M","CreditCardLast4Digits":"4315830000415498"}]},"Allergies":{"Allergy":[{"HealthKey":"00153","HealthDescription":"NO KNOWN DRUG ALLERGY"}]},"Addresses":{"Address":[{"ZipCode":"95404","StateCode":"CA","IsActive":"true","City":"SANTA ROSA","AddressTypeLiteral":"","AddressType":"A","AddressLine2":"","AddressLine1":"3227 CANYON CREEK LN"},{"ZipCode":"32615","StateCode":"FL","IsActive":"true","City":"ALACHUA","AddressTypeLiteral":"","AddressType":"P","AddressLine2":"","AddressLine1":"VOX90"},{"ZipCode":"75124","StateCode":"TX","IsActive":"true","City":"EUSTACE","AddressTypeLiteral":"","AddressType":"B","AddressLine2":"","AddressLine1":"13146 LAKE LN"},{"ZipCode":"75124","StateCode":"TX","IsActive":"true","City":"EUSTACE","AddressTypeLiteral":"","AddressType":"S","AddressLine2":"","AddressLine1":"13146 LAKE LN"},{"ZipCode":"75124","StateCode":"TX","IsActive":"true","City":"EUSTACE","AddressTypeLiteral":"","AddressType":"A","AddressLine2":"","AddressLine1":"13146 LAKE LNT"}]},"AccountNumber":{"CustomerValue":"6895629"}}]}}}';
            httpRes.setBody(resBodyString);
            system.assert(httpRes.getBody() == resBodyString);
            Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
            string reqType = '';
            for(String s : mapDetails.keyset())
            {
                reqType = s;
            } 
            List<string>labelGetmember = new List<string>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,httpRes);
            PharmacyFinancial_C_HUM.parseRSGetMemberCreditResponse(labelGetmember,continueObj);
            
            //Payment service
            continueObj = (Continuation)PharmacyFinancial_C_HUM.invokeGetAdjPayService('1070125929','05/03/2016','06/30/2016','true','true','AXB0001','0015C000002T9uJ');
            continueObj.continuationMethod = 'parseGetAdjPayResponse';
            continueObj.state = 'Both';
            httpRes = new Httpresponse ();
            String AdjPay= '{ "GetAdjustmentsAndPaymentsResponse":{ "Payments":{ "Payment":[ { "PaymentScheduledDate":"05/09/2016", "PaymentProcessedDate":"05/09/2016", "CreditCardTypeLiteral":"VISA", "Last4Digit":"1113", "ChkOrMoneyOrderNumber":"", "Amount":"10.0", "OrderNumber":"", "SourceApplicationCode":"ANS", "SourceApplicationCodeLiteral":"EPOSTRX" }, { "PaymentScheduledDate":"06/02/2015", "PaymentProcessedDate":"05/09/2016", "CreditCardTypeLiteral":"VISA", "Last4Digit":"1113", "ChkOrMoneyOrderNumber":"", "Amount":"12.99", "OrderNumber":"", "SourceApplicationCode":"SIL", "SourceApplicationCodeLiteral":"SILVERLINK" }, { "PaymentScheduledDate":"04/27/2016", "PaymentProcessedDate":"05/09/2016", "CreditCardTypeLiteral":"VISA", "Last4Digit":"1113", "ChkOrMoneyOrderNumber":"", "Amount":"10.0", "OrderNumber":"", "SourceApplicationCode":"ANS", "SourceApplicationCodeLiteral":"EPOSTRX" } ] } } }';
            httpRes.setBody(AdjPay);
            system.assert(httpRes.getBody() == AdjPay);
            mapDetails = continueObj.getRequests();
            string reqTypeAdjPay = '';
            for(String s : mapDetails.keyset())
            {
                reqTypeAdjPay = s;
            } 
            labelGetmember = new List<string>();
            labelGetmember.add(reqTypeAdjPay);
            Test.setContinuationResponse(reqTypeAdjPay,httpRes);
            PharmacyFinancial_C_HUM.parseGetAdjPayResponse(labelGetmember,continueObj);
            Test.stopTest();
        }
    }
    
    /**
    * Negative test method for Pharmacy Credit Card and Payment funtionality.
    *<p>
    * @param            NULL
    * @return           void
    */
	static testMethod void testNegativePharmacyFinancial()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        List<HUM_Webservice_Callout__c > lstWebCallout = [Select id from HUM_Webservice_Callout__c];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.PharmacyFinancial_VF_HUM);
            ApexPages.StandardController scon = new ApexPages.StandardController(policyObj);
            PharmacyFinancial_C_HUM pharmacyCreditCard = new PharmacyFinancial_C_HUM();
            PharmacyFinancial_S_HUM oServicePharmacy = new PharmacyFinancial_S_HUM();
            delete lstWebCallout;
            Continuation continueObj = (Continuation)PharmacyFinancial_C_HUM.invokeGetMemService('1048868178','AXB0001','0015C000002T9uJ');
            HttpResponse httpRes = new Httpresponse ();
            String resBodyString= '{"GetMemberReponse":{"Customers":{"Customer":[{"StaleData":"","SpecialtyVariantObjects":{"CapType":"S"},"Plans":{"Plan":[{"PlanType":"CASH RX","PlanTerminationDate":"","PlanEffectiveDate":""},{"PlanType":"MEDIMPACT OTC","PlanTerminationDate":"","PlanEffectiveDate":"07/29/2015"},{"PlanType":"MA","PlanTerminationDate":"","PlanEffectiveDate":"07/29/2015"}]},"Phones":{"Phone":[{"PhoneValue":"5025801000","PhoneType":"PP","PhoneKey":"7292489"}]},"Payment":{"NonCreditCard":{"Type":"","PaymentNumber":""}},"LogNotes":{"LogNote":[]},"HealthConditions":{"HealthCondition":[{"HealthDescription":"NO KNOWN MEDICAL HISTORY"}]},"Finance":{"PaymentSchedules":{"PaymentSchedule":[]},"OutstandingBalance":"","CreditLimit":"","Adjustments":{"Adjustment":[]}},"CustomerIdentity":{"MiddleName":"","LastName":"DAGGETT","FirstName":"KATHLEEN","Email":"gsheppard@humana.com","CustomerKeys":{"CustomerKey":[{"CustomerValue":"4193538","CustomerType":"PK"},{"CustomerValue":"1031063837","CustomerType":"PM"},{"CustomerValue":"6895629","CustomerType":"AC"}]},"BirthDate":"09/12/1944"},"CustomerDetails":null,"CreditCards":{"CreditCard":[{"IsAutoCharge":"false","IsActive":"true","ExpirationYear":"2018","ExpirationMonth":"11","CreditCardType":"M","CreditCardLast4Digits":"4315830000415489"},{"IsAutoCharge":"false","IsActive":"true","ExpirationYear":"2018","ExpirationMonth":"11","CreditCardType":"M","CreditCardLast4Digits":"4315830000415494"},{"IsAutoCharge":"false","IsActive":"true","ExpirationYear":"2018","ExpirationMonth":"11","CreditCardType":"M","CreditCardLast4Digits":"4315830000415497"},{"IsAutoCharge":"false","IsActive":"true","ExpirationYear":"2016","ExpirationMonth":"10","CreditCardType":"V","CreditCardLast4Digits":"3371953113715499"},{"IsAutoCharge":"false","IsActive":"true","ExpirationYear":"2018","ExpirationMonth":"11","CreditCardType":"M","CreditCardLast4Digits":"4315830000415498"}]},"Allergies":{"Allergy":[{"HealthKey":"00153","HealthDescription":"NO KNOWN DRUG ALLERGY"}]},"Addresses":{"Address":[{"ZipCode":"95404","StateCode":"CA","IsActive":"true","City":"SANTA ROSA","AddressTypeLiteral":"","AddressType":"A","AddressLine2":"","AddressLine1":"3227 CANYON CREEK LN"},{"ZipCode":"32615","StateCode":"FL","IsActive":"true","City":"ALACHUA","AddressTypeLiteral":"","AddressType":"P","AddressLine2":"","AddressLine1":"VOX90"},{"ZipCode":"75124","StateCode":"TX","IsActive":"true","City":"EUSTACE","AddressTypeLiteral":"","AddressType":"B","AddressLine2":"","AddressLine1":"13146 LAKE LN"},{"ZipCode":"75124","StateCode":"TX","IsActive":"true","City":"EUSTACE","AddressTypeLiteral":"","AddressType":"S","AddressLine2":"","AddressLine1":"13146 LAKE LN"},{"ZipCode":"75124","StateCode":"TX","IsActive":"true","City":"EUSTACE","AddressTypeLiteral":"","AddressType":"A","AddressLine2":"","AddressLine1":"13146 LAKE LNT"}]},"AccountNumber":{"CustomerValue":"6895629"}}]}}}';
            httpRes.setBody(resBodyString);
            system.assert(httpRes.getBody() == resBodyString);
            string reqType = '';
            List<string>labelGetmember = new List<string>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,httpRes);
            PharmacyFinancial_C_HUM.parseRSGetMemberCreditResponse(labelGetmember,continueObj);
            
            //Payment service
            continueObj = (Continuation)PharmacyFinancial_C_HUM.invokeGetAdjPayService('1070125929','05/03/2016','06/30/2016','true','true','AXB0001','0015C000002T9uJ');
            httpRes = new Httpresponse ();
            String AdjPay= '{ "GetAdjustmentsAndPaymentsResponse":{ "Payments":{ "Payment":[ { "PaymentScheduledDate":"05/09/2016", "PaymentProcessedDate":"05/09/2016", "CreditCardTypeLiteral":"VISA", "Last4Digit":"1113", "ChkOrMoneyOrderNumber":"", "Amount":"10.0", "OrderNumber":"", "SourceApplicationCode":"ANS", "SourceApplicationCodeLiteral":"EPOSTRX" }, { "PaymentScheduledDate":"06/02/2015", "PaymentProcessedDate":"05/09/2016", "CreditCardTypeLiteral":"VISA", "Last4Digit":"1113", "ChkOrMoneyOrderNumber":"", "Amount":"12.99", "OrderNumber":"", "SourceApplicationCode":"SIL", "SourceApplicationCodeLiteral":"SILVERLINK" }, { "PaymentScheduledDate":"04/27/2016", "PaymentProcessedDate":"05/09/2016", "CreditCardTypeLiteral":"VISA", "Last4Digit":"1113", "ChkOrMoneyOrderNumber":"", "Amount":"10.0", "OrderNumber":"", "SourceApplicationCode":"ANS", "SourceApplicationCodeLiteral":"EPOSTRX" } ] } } }';
            httpRes.setBody(AdjPay);
            system.assert(httpRes.getBody() == AdjPay);
            string reqTypeAdjPay = '';
            labelGetmember = new List<string>();
            labelGetmember.add(reqTypeAdjPay);
            Test.setContinuationResponse(reqTypeAdjPay,httpRes);
            PharmacyFinancial_C_HUM.parseGetAdjPayResponse(labelGetmember,continueObj);
            Test.stopTest();
        }
    }
    
    /**
    * Negative test method for Pharmacy Credit Card and Payment funtionality.
    *<p>
    * @param            NULL
    * @return           void
    */
	static testMethod void testNegativePharmacyFinancialMember()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        List<HUM_Webservice_Callout__c > lstWebCallout = [Select id from HUM_Webservice_Callout__c];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.PharmacyFinancial_VF_HUM);
            ApexPages.StandardController scon = new ApexPages.StandardController(policyObj);
            PharmacyFinancial_C_HUM pharmacyCreditCard = new PharmacyFinancial_C_HUM();
            PharmacyFinancial_S_HUM oServicePharmacy = new PharmacyFinancial_S_HUM();
            delete lstWebCallout;
            Continuation continueObj = (Continuation)PharmacyFinancial_C_HUM.invokeGetMemService('1048868178','AXB0001','0015C000002T9uJ');
            HttpResponse httpRes = new Httpresponse ();
            String resBodyString= '{ "Fault":{ "faultcode":"\n          Code-67.8.1", "faultstring":"\n          error_service_name :\"RightSourceService &#124;\n          error_service_method : GetAdjustmentsAndPayments &#124;\n          fault_message : ", "detail":{ "transactionInfo":{ "transaction-id":"207260733", "transaction-client":"127.0.0.1", "URL-in":"http://127.0.0.1:8316/RightSourceService_V1.0/GetAdjustmentsAndPayments", "processor-name":"JSONServicesGateway", "processor-type":"Multiprotocol Gateway" }, "errorInfo":{ "error-code":"0x00d30003", "error-headers":"HTTP/1.1 500 Internal Server Error\r\nContent-Type: text/xml; charset=UTF-8\r\nX-Backside-Transport: OK OK\r\nConnection: close\r\n\r\n", "error-message":"Member not found", "error-protocol-reason-phrase":"", "error-protocol-response":"0", "error-subcode":"0x00d30003" } } } }';
            httpRes.setBody(resBodyString);
            system.assert(httpRes.getBody() == resBodyString);
            string reqType = '';
            List<string>labelGetmember = new List<string>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,httpRes);
            PharmacyFinancial_C_HUM.parseRSGetMemberCreditResponse(labelGetmember,continueObj);
            
            //Payment service
            continueObj = (Continuation)PharmacyFinancial_C_HUM.invokeGetAdjPayService('1070125929','05/03/2016','06/30/2016','true','true','AXB0001','0015C000002T9uJ');
            httpRes = new Httpresponse ();
            String AdjPay= '{ "Fault":{ "faultcode":"\n          Code-67.8.1", "faultstring":"\n          error_service_name :\"RightSourceService &#124;\n          error_service_method : GetAdjustmentsAndPayments &#124;\n          fault_message : ", "detail":{ "transactionInfo":{ "transaction-id":"207260733", "transaction-client":"127.0.0.1", "URL-in":"http://127.0.0.1:8316/RightSourceService_V1.0/GetAdjustmentsAndPayments", "processor-name":"JSONServicesGateway", "processor-type":"Multiprotocol Gateway" }, "errorInfo":{ "error-code":"0x00d30003", "error-headers":"HTTP/1.1 500 Internal Server Error\r\nContent-Type: text/xml; charset=UTF-8\r\nX-Backside-Transport: OK OK\r\nConnection: close\r\n\r\n", "error-message":"Member not found", "error-protocol-reason-phrase":"", "error-protocol-response":"0", "error-subcode":"0x00d30003" } } } }';
            httpRes.setBody(AdjPay);
            system.assert(httpRes.getBody() == AdjPay);
            string reqTypeAdjPay = '';
            labelGetmember = new List<string>();
            labelGetmember.add(reqTypeAdjPay);
            Test.setContinuationResponse(reqTypeAdjPay,httpRes);
            PharmacyFinancial_C_HUM.parseGetAdjPayResponse(labelGetmember,continueObj);
            Test.stopTest();
        }
    }
    
    /**
    * Positive test method for Pharmacy add and update Credit Card funtionalities.
    *<p>
    * @param            NULL
    * @return           void
    */
	static testMethod void testPharmacyFinancialCreditCard()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.PharmacyFinancial_VF_HUM);
            ApexPages.StandardController scon = new ApexPages.StandardController(policyObj);
            PharmacyFinancial_C_HUM pharmacyCreditCard = new PharmacyFinancial_C_HUM();
            PharmacyFinancial_S_HUM oServicePharmacy = new PharmacyFinancial_S_HUM();
            Continuation continueObj = (Continuation)PharmacyFinancial_C_HUM.invokeGetEditMemService('1048868178','test','test','test','test','test','test','test','test','test','test','test','false','AXB0001','0015C000002T9uJ');
            continueObj.continuationMethod = 'retrieveEditMember';
            string state = 'Both_AXB0001_0015C000002T9uJ';
            HttpResponse httpRes = new Httpresponse ();
            String resBodyString= '{ "EditMemberResponse":{ "transactionKey":"1120000275944", "transactionTimeStamp":"Tue, Mar 21 2017 08:55:28 313 EDT", "UID":"" } }';
            httpRes.setBody(resBodyString);
            system.assert(httpRes.getBody() == resBodyString);
            Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
            string reqType = '';
            for(String s : mapDetails.keyset())
            {
                reqType = s;
            } 
            List<string>labelGetmember = new List<string>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,httpRes);
            PharmacyFinancial_C_HUM.retrieveEditMember(labelGetmember,state);
            pharmacyCreditCard.closeUpdatePopUp();
            pharmacyCreditCard.showUpdateCreditCardSection();
            pharmacyCreditCard.showNewCreditCardSection();
            pharmacyCreditCard.hideNewCreditCardSection();
            
            //Add credit card
            continueObj = (Continuation)PharmacyFinancial_C_HUM.invokeGetEditMemService('1048868178','test','test','test','test','test','test','test','test','test','test','test','true','AXB0001','0015C000002T9uJ');
            continueObj.continuationMethod = 'retrieveEditMember';
            state = 'Both_AXB0001_0015C000002T9uJ';
            httpRes = new Httpresponse ();
            String addCredit= '{ "EditMemberResponse":{ "transactionKey":"1120000275944", "transactionTimeStamp":"Tue, Mar 21 2017 08:55:28 313 EDT", "UID":"" } }';
            httpRes.setBody(addCredit);
            system.assert(httpRes.getBody() == addCredit);
            mapDetails = continueObj.getRequests();
            string reqTypeAddCredit = '';
            for(String s : mapDetails.keyset())
            {
                reqTypeAddCredit = s;
            } 
            labelGetmember = new List<string>();
            labelGetmember.add(reqTypeAddCredit);
            Test.setContinuationResponse(reqTypeAddCredit,httpRes);
            PharmacyFinancial_C_HUM.retrieveEditMember(labelGetmember,state);
            Test.stopTest();
        }
    }
	
	/**
    * Test method for Email Service
    *<p>
    * @param            NULl
    * @return           void
    */
    static testMethod void testEmailService()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.PharmacyFinancial_VF_HUM);
            ApexPages.StandardController scon = new ApexPages.StandardController(policyObj);
            PharmacyFinancial_C_HUM pharmacyCreditCard = new PharmacyFinancial_C_HUM();
            PharmacyFinancial_S_HUM oServicePharmacy = new PharmacyFinancial_S_HUM();
            Continuation continueObj = (Continuation)PharmacyFinancial_C_HUM.createFinancialRequest('sMemName' , 'sHumanaIdNum' , 'sAccNum' , 'sOrderNumber' , 'sRxNumber' , 'sConcern' , 'sNetworkUserId' ,'1048868178', 'sRecordId');
            continueObj.continuationMethod = 'parseEmailUtilityResponse';
            Map<String,String> mapState = new Map<String,String>();
            mapState.put('EnterpriseId','sEnterpriseId');
            mapState.put('Concern','sConcern');
            mapState.put('NetworkUserId','sNetworkUserId');
            mapState.put('RecordId','sRecordId');
            continueObj.state = 'BOTH';
            HttpResponse httpRes = new Httpresponse ();
            String resBodyString= '{"SendEmailResponse":{"Type": "Information","Source": "Enterprise Email Utility Service","Description": "Send email successful"}}';
            httpRes.setBody(resBodyString);
            system.assert(httpRes.getBody() == resBodyString);
            Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
            string reqType = '';
            for(String s : mapDetails.keyset())
            {
                reqType = s;
            } 
            List<string>labelGetmember = new List<string>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,httpRes);
            Map<string,string>mapObj = new Map<string,string>();
            mapObj.put('Concern','test');
            mapObj.put('EnterpriseId','test');
            mapObj.put('NetworkUserId','test');
            mapState.put('RecordId','test');
            PharmacyFinancial_C_HUM.parseEmailUtilityResponse(labelGetmember,mapObj);
            PharmacyFinancial_C_HUM.parseLogNoteResponse(labelGetmember,mapObj);
            Test.stopTest();
        }
    }
    
    /**
    * Test method for Negative Email Service
    *<p>
    * @param            NULl
    * @return           void
    */
    static testMethod void testNegativeEmailService()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.PharmacyFinancial_VF_HUM);
            ApexPages.StandardController scon = new ApexPages.StandardController(policyObj);
            PharmacyFinancial_C_HUM pharmacyCreditCard = new PharmacyFinancial_C_HUM();
            PharmacyFinancial_S_HUM oServicePharmacy = new PharmacyFinancial_S_HUM();
            Continuation continueObj = (Continuation)PharmacyFinancial_C_HUM.createFinancialRequest('sMemName' , 'sHumanaIdNum' , 'sAccNum' , 'sOrderNumber' , 'sRxNumber' , 'sConcern' , 'sNetworkUserId' ,'1048868178', 'sRecordId');
            continueObj.continuationMethod = 'parseEmailUtilityResponse';
            Map<String,String> mapState = new Map<String,String>();
            mapState.put('EnterpriseId','sEnterpriseId');
            mapState.put('Concern','sConcern');
            mapState.put('NetworkUserId','sNetworkUserId');
            mapState.put('RecordId','sRecordId');
            continueObj.state = 'BOTH';
            HttpResponse httpRes = new Httpresponse ();
            String resBodyString= '{ "Fault":{ "faultcode":"\n          Code-67.8.1", "faultstring":"\n          error_service_name :\"RightSourceService &#124;\n          error_service_method : GetAdjustmentsAndPayments &#124;\n          fault_message : ", "detail":{ "transactionInfo":{ "transaction-id":"207260733", "transaction-client":"127.0.0.1", "URL-in":"http://127.0.0.1:8316/RightSourceService_V1.0/GetAdjustmentsAndPayments", "processor-name":"JSONServicesGateway", "processor-type":"Multiprotocol Gateway" }, "errorInfo":{ "error-code":"0x00d30003", "error-headers":"HTTP/1.1 500 Internal Server Error\r\nContent-Type: text/xml; charset=UTF-8\r\nX-Backside-Transport: OK OK\r\nConnection: close\r\n\r\n", "error-message":"Member not found", "error-protocol-reason-phrase":"", "error-protocol-response":"0", "error-subcode":"0x00d30003" } } } }';
            httpRes.setBody(resBodyString);
            system.assert(httpRes.getBody() == resBodyString);
            Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
            string reqType = '';
            for(String s : mapDetails.keyset())
            {
                reqType = s;
            } 
            List<string>labelGetmember = new List<string>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,httpRes);
            Map<string,string>mapObj = new Map<string,string>();
            mapObj.put('Concern','test');
            mapObj.put('EnterpriseId','test');
            mapObj.put('NetworkUserId','test');
            mapState.put('RecordId','test');
            PharmacyFinancial_C_HUM.parseEmailUtilityResponse(labelGetmember,mapObj);
            PharmacyFinancial_C_HUM.parseLogNoteResponse(labelGetmember,mapObj);
            Test.stopTest();
        }
    }
	
	 /**
    * Test method for CreateGetOrderDetail
    *<p>
    * @param            NULl
    * @return           void
    */
    static testMethod void testCreateGetOrderDetail()
    {
    User testUser = [Select Id,Name from User where LastName='test User1'];
        policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.PharmacyFinancial_VF_HUM);
            ApexPages.StandardController scon = new ApexPages.StandardController(policyObj);
            PharmacyFinancial_C_HUM pharmacyCreditCard = new PharmacyFinancial_C_HUM();
            PharmacyFinancial_S_HUM oServicePharmacy = new PharmacyFinancial_S_HUM();
            Continuation continueObj = (Continuation)PharmacyFinancial_C_HUM.createGetOrderDetailRequest('sOrderNumber', 'snetworkId');
            continueObj.continuationMethod = 'prepareGetOrderDetail';
            Object State= new PharmacyOrderWrapper_DTO_HUM(null, null, null, null, '1',null,null);
            Httpresponse httpRes = new Httpresponse ();
            String orderDetails= '';
            httpRes.setBody(orderDetails);
            system.assert(httpRes.getBody() == orderDetails);
            Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
            mapDetails = continueObj.getRequests();
            string reqTypeAddCredit = '';
            for(String s : mapDetails.keyset())
            {
                reqTypeAddCredit = s;
            } 
            List<string> labelGetmember = new List<string>();
            labelGetmember.add(reqTypeAddCredit);
            Test.setContinuationResponse(reqTypeAddCredit,httpRes);

            PharmacyFinancial_C_HUM.processOrder(labelGetmember,State);
            Test.stopTest();
            }

    }
    
        /**
    * Test method for negative scenario of invokeEpostOrder
    *<p>
    * @param            NULl
    * @return           void
    */
    static testMethod void testNegativeEPostOrder()    
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.PharmacyFinancial_VF_HUM);
            ApexPages.StandardController scon = new ApexPages.StandardController(policyObj);
            PharmacyFinancial_C_HUM pharmacyCreditCard = new PharmacyFinancial_C_HUM();
            PharmacyFinancial_S_HUM oServicePharmacy = new PharmacyFinancial_S_HUM();
            Continuation continueObj = (Continuation)PharmacyFinancial_C_HUM.invokeEpostOrder('1070125929','nxc1262','0015C000002T9uJ');
            continueObj.continuationMethod = 'sendRequestGetOrderResponse';
            Object State='0015C000002T9uJ';
            Httpresponse httpRes = new Httpresponse ();
            String postOrder= '';            
            httpRes.setBody(postOrder);
            system.assert(httpRes.getBody() == postOrder);
            Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
            mapDetails = continueObj.getRequests();
            string orderDetails= '';
            for(String s : mapDetails.keyset())
            {
                orderDetails= s;
            } 
            List<string> labelPostOrder = new List<string>();
            labelPostOrder.add(orderDetails);
            Test.setContinuationResponse(orderDetails,httpRes);

            PharmacyFinancial_C_HUM.parseRSGetOrderResponse(labelPostOrder,State);
            Test.stopTest();
        }
    }
    
     /**
    * Test method for positive scenario of invokeEpostOrder
    *<p>
    * @param            NULl
    * @return           void
    */
    
    static testMethod void testEPostOrder()    
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.PharmacyFinancial_VF_HUM);
            ApexPages.StandardController scon = new ApexPages.StandardController(policyObj);
            PharmacyFinancial_C_HUM pharmacyCreditCard = new PharmacyFinancial_C_HUM();
            PharmacyFinancial_S_HUM oServicePharmacy = new PharmacyFinancial_S_HUM();
            Continuation continueObj = (Continuation)PharmacyFinancial_C_HUM.invokeEpostOrder('1070125929','nxc1262','0015C000002T9uJ');
            continueObj.continuationMethod = 'sendRequestGetOrderResponse';
            Object State='0015C000002T9uJ';
            Httpresponse httpRes = new Httpresponse ();            
            string postOrder1='{"GetOrdersReponse": {"EnterprisePersonID": "1070125929","Orders": {"Order":[{"OrderNumber": "114236859","OrderType": "NEWRX","OrderCreationDate": "05/09/2016","OrderReleaseDate": "","OrderSource": "FACSIMILE","OrderStatus": "C","OrderStatusLiteral": "CANCELLED","QueueName": "","ExternalOrderKey": "","OrderTotalAmount": "0.0","OrderPayAmount": "0.0","OrderTaxAmount": "0.0",  "StaleData": "0"},{"OrderNumber": "114236889","OrderType": "NEWRX","OrderCreationDate": "05/09/2016","OrderReleaseDate": "","OrderSource": "FACSIMILE","OrderStatus": "C","OrderStatusLiteral": "CANCELLED","QueueName": "","ExternalOrderKey": "","OrderTotalAmount": "0.0","OrderPayAmount": "0.0","OrderTaxAmount": "0.0","StaleData": "0"}]}}}';
            httpRes.setBody(postOrder1);
            system.assert(httpRes.getBody() == postOrder1);
            Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
            mapDetails = continueObj.getRequests();
            string orderDetails1= '';
            for(String s : mapDetails.keyset())
            {
                orderDetails1= s;
            } 
            List<string> labelPostOrder = new List<string>();
            labelPostOrder.add(orderDetails1);
            Test.setContinuationResponse(orderDetails1,httpRes);

            PharmacyFinancial_C_HUM.parseRSGetOrderResponse(labelPostOrder,State);
            Test.stopTest();
        }
    }
    
    /**
    * Test method for positive scenario of invokeGetMemServiceFinance
    *<p>
    * @param            NULl
    * @return           void
    */
    static testMethod void testGetMember()    
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.PharmacyFinancial_VF_HUM);
            ApexPages.StandardController scon = new ApexPages.StandardController(policyObj);
            PharmacyFinancial_C_HUM pharmacyCreditCard = new PharmacyFinancial_C_HUM();
            PharmacyFinancial_S_HUM oServicePharmacy = new PharmacyFinancial_S_HUM();
            Continuation continueObj = (Continuation)PharmacyFinancial_C_HUM.invokeGetMemServiceFinance('1070125929','nxc1262','0015C000002T9uJ');
            continueObj.continuationMethod = 'SendRequestGetMemberResponseFinance';
            Object State='0015C000002T9uJ';
            Httpresponse httpRes = new Httpresponse ();            
            string memberResponse= '{ "GetMemberReponse":{ "Customers":{ "Customer":[ { "CustomerIdentity":{ "BirthDate":"04/24/1950", "CustomerKeys":{ "CustomerKey":[ { "CustomerType":"AC", "CustomerValue":"6727721" }, { "CustomerType":"PK", "CustomerValue":"4012011" }, { "CustomerType":"PM", "CustomerValue":"1070125929" } ] }, "FirstName":"NANCY", "MiddleName":"B", "LastName":"NEGRON", "Email":"" }, "CustomerPreference":{ "CapType":"S", "CapTypeLiteral":"SAFETY" }, "CustomerDetail":{ "NeedsMemberConsent":"false", "ConsentBeginDate":"", "ConsentEndDate":"", "MemberConsentStatus":"", "CMRStatus":"No", "CMRStatusLiteral":"No" }, "Addresses":{ "Address":[  ] }, "Phones":{ "Phone":[ { "PhoneKey":"6508778", "PhoneType":"AL", "PhoneValue":"5025801000" }, { "PhoneKey":"7909869", "PhoneType":"AL", "PhoneValue":"5025805807" }, { "PhoneKey":"7909865", "PhoneType":"AL", "PhoneValue":"1234567890" }, { "PhoneKey":"7910030", "PhoneType":"AL", "PhoneValue":"5225845822" }, { "PhoneKey":"7910053", "PhoneType":"AL", "PhoneValue":"9952012417" }, { "PhoneKey":"7910124", "PhoneType":"AL", "PhoneValue":"5445678722" }, { "PhoneKey":"7910055", "PhoneType":"AL", "PhoneValue":"4025805676" }, { "PhoneKey":"7910038", "PhoneType":"AL", "PhoneValue":"9950012411" }, { "PhoneKey":"7910127", "PhoneType":"AL", "PhoneValue":"5445676644" }, { "PhoneKey":"7910075", "PhoneType":"CN", "PhoneValue":"6767888911" }, { "PhoneKey":"7910024", "PhoneType":"AL", "PhoneValue":"4025805122" }, { "PhoneKey":"7910071", "PhoneType":"AL", "PhoneValue":"9950012412" }, { "PhoneKey":"7909866", "PhoneType":"AL", "PhoneValue":"5025801002" }, { "PhoneKey":"7910067", "PhoneType":"AL", "PhoneValue":"4025805811" }, { "PhoneKey":"7910074", "PhoneType":"CN", "PhoneValue":"6767888999" }, { "PhoneKey":"7910135", "PhoneType":"PP", "PhoneValue":"5445556767" }, { "PhoneKey":"7910084", "PhoneType":"AL", "PhoneValue":"5445678784" }, { "PhoneKey":"7910034", "PhoneType":"AL", "PhoneValue":"4025805678" }, { "PhoneKey":"7910070", "PhoneType":"AL", "PhoneValue":"5445342111" }, { "PhoneKey":"7910006", "PhoneType":"AL", "PhoneValue":"4025805802" }, { "PhoneKey":"7910040", "PhoneType":"AL", "PhoneValue":"4025805555" }, { "PhoneKey":"7910025", "PhoneType":"AL", "PhoneValue":"8978989898" }, { "PhoneKey":"7910036", "PhoneType":"AL", "PhoneValue":"4025805801" }, { "PhoneKey":"7910023", "PhoneType":"AL", "PhoneValue":"5225845888" }, { "PhoneKey":"7909867", "PhoneType":"AL", "PhoneValue":"5025800000" }, { "PhoneKey":"7909868", "PhoneType":"AL", "PhoneValue":"5025805800" }, { "PhoneKey":"7910068", "PhoneType":"AL", "PhoneValue":"9952012418" }, { "PhoneKey":"7909870", "PhoneType":"AL", "PhoneValue":"4025805807" }, { "PhoneKey":"7909886", "PhoneType":"AL", "PhoneValue":"5025805855" }, { "PhoneKey":"7910049", "PhoneType":"AL", "PhoneValue":"4025805672" }, { "PhoneKey":"7910086", "PhoneType":"AL", "PhoneValue":"5445678721" }, { "PhoneKey":"7910044", "PhoneType":"AL", "PhoneValue":"4025805701" }, { "PhoneKey":"7910128", "PhoneType":"AL", "PhoneValue":"5445676643" }, { "PhoneKey":"7910061", "PhoneType":"AL", "PhoneValue":"4025805778" }, { "PhoneKey":"7910125", "PhoneType":"AL", "PhoneValue":"5445676622" }, { "PhoneKey":"7910021", "PhoneType":"AL", "PhoneValue":"5225845999" }, { "PhoneKey":"7910032", "PhoneType":"AL", "PhoneValue":"4025805675" }, { "PhoneKey":"7910033", "PhoneType":"AL", "PhoneValue":"4025805702" }, { "PhoneKey":"7910077", "PhoneType":"CN", "PhoneValue":"6767888998" }, { "PhoneKey":"7910060", "PhoneType":"AL", "PhoneValue":"5225845851" }, { "PhoneKey":"7910058", "PhoneType":"AL", "PhoneValue":"9950012415" }, { "PhoneKey":"7909887", "PhoneType":"AL", "PhoneValue":"5025845855" }, { "PhoneKey":"7910022", "PhoneType":"AL", "PhoneValue":"9950012413" }, { "PhoneKey":"7910059", "PhoneType":"AL", "PhoneValue":"4025805674" }, { "PhoneKey":"7910062", "PhoneType":"AL", "PhoneValue":"4025805777" }, { "PhoneKey":"7910063", "PhoneType":"AL", "PhoneValue":"5225845834" }, { "PhoneKey":"7910054", "PhoneType":"AL", "PhoneValue":"4025805111" }, { "PhoneKey":"7910076", "PhoneType":"CN", "PhoneValue":"6767888912" }, { "PhoneKey":"7910065", "PhoneType":"AL", "PhoneValue":"5225845858" }, { "PhoneKey":"7910042", "PhoneType":"AL", "PhoneValue":"5225845811" }, { "PhoneKey":"7910045", "PhoneType":"AL", "PhoneValue":"5225845856" }, { "PhoneKey":"7910066", "PhoneType":"AL", "PhoneValue":"4025805673" }, { "PhoneKey":"7910078", "PhoneType":"CN", "PhoneValue":"8976768787" }, { "PhoneKey":"7909888", "PhoneType":"AL", "PhoneValue":"5225845855" }, { "PhoneKey":"7910004", "PhoneType":"AL", "PhoneValue":"5225845852" }, { "PhoneKey":"7910083", "PhoneType":"AL", "PhoneValue":"5445678785" }, { "PhoneKey":"7910129", "PhoneType":"CN", "PhoneValue":"8976768786" }, { "PhoneKey":"7910037", "PhoneType":"AL", "PhoneValue":"5225845857" }, { "PhoneKey":"7910069", "PhoneType":"AL", "PhoneValue":"9952012413" }, { "PhoneKey":"7910052", "PhoneType":"AL", "PhoneValue":"5445678787" }, { "PhoneKey":"7910017", "PhoneType":"AL", "PhoneValue":"4025805803" } ] }, "StaleData":"0", "AccountNumber":{ "CustomerValue":"6727721" }, "Finance":{ "CreditLimit":"30.01", "OutstandingBalance":"2.99", "PaymentSchedules":{ "PaymentSchedule":[ { "PaymentDueDate":"06/02/2015", "PaymentMadeDate":"06/02/2015", "PaymentType":"OT", "SourceApplicationCode":"SIL", "SourceApplicationCodeLiteral":"SILVERLINK" }, { "PaymentDueDate":"05/09/2016", "PaymentMadeDate":"05/09/2016", "PaymentType":"OT", "SourceApplicationCode":"ANS", "SourceApplicationCodeLiteral":"EPOSTRX" }, { "PaymentDueDate":"04/27/2016", "PaymentMadeDate":"05/09/2016", "PaymentType":"OT", "SourceApplicationCode":"ANS", "SourceApplicationCodeLiteral":"EPOSTRX" } ] } }, "Allergies":{ "Allergy":[ { "HealthKey":"00153", "HealthDescription":"No Known Drug Allergy" } ] }, "CreditCards":{ "CreditCard":[  ] }, "HealthConditions":{ "HealthCondition":[ { "HealthDescription":"Thyroid Disease" }, { "HealthDescription":"No Known Medical History" } ] }, "LogNotes":{ "LogNote":[ { "LogNoteCode":"90 DAY SUPPLY COLD PACK NO RESPONSE", "LogNoteDate":"01/23/2017", "LogNoteKey":"64665338", "LogNoteMessage":"update for testing 90CPNR", "LogNoteUser":"SXA1954" }, { "LogNoteCode":"90 DAY SUPPLY COLD PACK NO RESPONSE", "LogNoteDate":"02/15/2017", "LogNoteKey":"64665770", "LogNoteMessage":"update for testing 90CPNR", "LogNoteUser":"SXA1954" }, { "LogNoteCode":"OTHER", "LogNoteDate":"02/17/2017", "LogNoteKey":"64665798", "LogNoteMessage":"update for testing 90CPNR", "LogNoteUser":"SXA1954" } ] }, "Plans":{ "Plan":[ { "PlanEffectiveDate":"", "PlanTerminationDate":"", "PlanType":"CASH RX", "Payer":"Humana" }, { "PlanEffectiveDate":"05/05/2015", "PlanTerminationDate":"", "PlanType":"MEDIMPACT OTC", "Payer":"Humana" }, { "PlanEffectiveDate":"05/05/2015", "PlanTerminationDate":"12/31/9999", "PlanType":"MA", "Payer":"HUMANA INSURANCE COMPANY" } ] } } ] } } }';
            httpRes.setBody(memberResponse);
            system.assert(httpRes.getBody() == memberResponse);
            Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
            mapDetails = continueObj.getRequests();
            string orderDetails1= '';
            for(String s : mapDetails.keyset())
            {
                orderDetails1= s;
            } 
            List<string> labelGetMember = new List<string>();
            labelGetMember.add(orderDetails1);
            Test.setContinuationResponse(orderDetails1,httpRes);

            PharmacyFinancial_C_HUM.parseRSGetMemberResponseFinance(labelGetMember,State);
			Test.stopTest();
               }
     }

 /**
    * Test method for negative scenario of invokeGetMemServiceFinance
    *<p>
    * @param            NULl
    * @return           void
    */
      static testMethod void testNegativeGetMember()    
       {
            User testUser = [Select Id,Name from User where LastName='test User1'];
            policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.PharmacyFinancial_VF_HUM);
            ApexPages.StandardController scon = new ApexPages.StandardController(policyObj);
            PharmacyFinancial_C_HUM pharmacyCreditCard = new PharmacyFinancial_C_HUM();
            PharmacyFinancial_S_HUM oServicePharmacy = new PharmacyFinancial_S_HUM();
            Continuation continueObj = (Continuation)PharmacyFinancial_C_HUM.invokeGetMemServiceFinance('1070125929','nxc1262','0015C000002T9uJ');
            continueObj.continuationMethod = 'SendRequestGetMemberResponseFinance';
            Object State='0015C000002T9uJ';
            Httpresponse httpRes = new Httpresponse ();            
            String memberResponse= '';
            httpRes.setBody(memberResponse);
            system.assert(httpRes.getBody() == memberResponse);
            Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
            mapDetails = continueObj.getRequests();
            string orderDetails1= '';
            for(String s : mapDetails.keyset())
            {
                orderDetails1= s;
            } 
            List<string> labelGetMember = new List<string>();
            labelGetMember.add(orderDetails1);
            Test.setContinuationResponse(orderDetails1,httpRes);

            PharmacyFinancial_C_HUM.parseRSGetMemberResponseFinance(labelGetMember,State);
			Test.stopTest();


        }
    }

    /**
    * Positive test method for Pharmacy One Time Credit Card Payment functionalities.
    *<p>
    * @param            NULL
    * @return           void
    */
	static testMethod void testPharmacyFinancialOneTimeCreditCardPayment()
    {
        User testUser = [Select Id,Name from User where LastName='test FinancialUser1'];
        policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.PharmacyFinancial_VF_HUM);
            ApexPages.StandardController scon = new ApexPages.StandardController(policyObj);
            PharmacyFinancial_C_HUM pharmacyCreditCard = new PharmacyFinancial_C_HUM();
            PharmacyFinancial_S_HUM oServicePharmacy = new PharmacyFinancial_S_HUM();
            Continuation continueObj = (Continuation)PharmacyFinancial_C_HUM.invokeOneTimeCreditCardPayment('1048868178','sim4764','9587133',100.00,'2025-12-12',true);
            continueObj.continuationMethod = 'parseOneTimePaymentResponse';            
            HttpResponse httpRes = new Httpresponse();
            String resBodyString= '{ "EditMemberResponse":{ "transactionKey":"1120000275944", "transactionTimeStamp":"Tue, Mar 21 2017 08:55:28 313 EDT", "UID":"" } }';
            httpRes.setBody(resBodyString);
            system.assert(httpRes.getBody() == resBodyString);
            Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
            string reqType = '';
            for(String s : mapDetails.keyset())
            {
                reqType = s;
            } 
            List<string>labelGetmember = new List<string>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,httpRes);
            PharmacyFinancial_C_HUM.parseOneTimePaymentResponse(labelGetmember,continueObj);
            pharmacyCreditCard.amount = 100.00;
            pharmacyCreditCard.cardType = 'visa';
            pharmacyCreditCard.creditCardLast4Digit = '6300';
            pharmacyCreditCard.creditCardExpYear = '2020';
            pharmacyCreditCard.ShowPopup();
            pharmacyCreditCard.ClosePopup();
            pharmacyCreditCard.showSucessMessage();
            pharmacyCreditCard.showErrorMessage();
            pharmacyCreditCard.CloseErrorMsg();
            Test.stopTest();
        }
    }
	
     /**
    * Negative test method for Pharmacy One Time Credit Card Payment functionalities.
    *<p>
    * @param            NULL
    * @return           void
    */
	static testMethod void testNegativePharmacyFinancialOneTimeCreditCardPayment()
    {
        User testUser = [Select Id,Name from User where LastName='test FinancialUser1'];
        policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.PharmacyFinancial_VF_HUM);
            ApexPages.StandardController scon = new ApexPages.StandardController(policyObj);
            PharmacyFinancial_C_HUM pharmacyCreditCard = new PharmacyFinancial_C_HUM();
            PharmacyFinancial_S_HUM oServicePharmacy = new PharmacyFinancial_S_HUM();
            Continuation continueObj = (Continuation)PharmacyFinancial_C_HUM.invokeOneTimeCreditCardPayment('1111111111','sim4764','9587133',100.00,'2019-12-12',true);
            HttpResponse httpRes = new Httpresponse();
            String resBodyString= '{ "Fault":{ "faultcode":"\n          Code-67.8.1", "faultstring":"\n          error_service_name :\"RightSourceService &#124;\n          error_service_method : GetAdjustmentsAndPayments &#124;\n          fault_message : ", "detail":{ "transactionInfo":{ "transaction-id":"207260733", "transaction-client":"127.0.0.1", "URL-in":"http://127.0.0.1:8316/RightSourceService_V1.0/GetAdjustmentsAndPayments", "processor-name":"JSONServicesGateway", "processor-type":"Multiprotocol Gateway" }, "errorInfo":{ "error-code":"0x00d30003", "error-headers":"HTTP/1.1 500 Internal Server Error\r\nContent-Type: text/xml; charset=UTF-8\r\nX-Backside-Transport: OK OK\r\nConnection: close\r\n\r\n", "error-message":"Member not found", "error-protocol-reason-phrase":"", "error-protocol-response":"0", "error-subcode":"0x00d30003" } } } }';
            httpRes.setBody(resBodyString);
            system.assert(httpRes.getBody() == resBodyString);
            string reqType = '';
            List<string>labelGetmember = new List<string>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,httpRes);
            PharmacyFinancial_C_HUM.parseOneTimePaymentResponse(labelGetmember,continueObj);
            Test.stopTest();
        }
    }

}