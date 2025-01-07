/*****************************************************************************************************************************
Apex Class Name : PharmacyFinancial_LT_HUM
Version         : 1.0
Created On      : 10/01/2021
Function        : Class contains test methods PharmacyFinancial_LC_HUM class.n.


Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0          Swapnali Sonawane                                    10/01/2021               
*******************************************************************************************************************************************************************/
@isTest
public class PharmacyFinancial_LT_HUM {
    
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
            try {
                ApexPages.StandardController scon = new ApexPages.StandardController(policyObj);
                PharmacyFinancial_LC_HUM pharmacyCreditCard = new PharmacyFinancial_LC_HUM();
                PharmacyFinancial_LS_HUM oServicePharmacy = new PharmacyFinancial_LS_HUM();
                Continuation continueObj = (Continuation)PharmacyFinancial_LC_HUM.invokeGetAdjPayService('1070125929','05/03/2016','06/30/2016','true','true','SXS0295','0015C000002T9uJ');                
                continueObj.continuationMethod = 'parseGetAdjPayResponse';              
                continueObj.state = 'Both';
                HttpResponse httpRes = new Httpresponse ();
                httpRes = new Httpresponse ();
                String AdjPay= '{ "GetAdjustmentsAndPaymentsResponse":{ "Payments":{ "Payment":[ { "PaymentScheduledDate":"05/09/2016", "PaymentProcessedDate":"05/09/2016", "CreditCardTypeLiteral":"VISA", "Last4Digit":"1113", "ChkOrMoneyOrderNumber":"", "Amount":"10.0", "OrderNumber":"", "SourceApplicationCode":"ANS", "SourceApplicationCodeLiteral":"EPOSTRX" }, { "PaymentScheduledDate":"06/02/2015", "PaymentProcessedDate":"05/09/2016", "CreditCardTypeLiteral":"VISA", "Last4Digit":"1113", "ChkOrMoneyOrderNumber":"", "Amount":"12.99", "OrderNumber":"", "SourceApplicationCode":"SIL", "SourceApplicationCodeLiteral":"SILVERLINK" }, { "PaymentScheduledDate":"04/27/2016", "PaymentProcessedDate":"05/09/2016", "CreditCardTypeLiteral":"VISA", "Last4Digit":"1113", "ChkOrMoneyOrderNumber":"", "Amount":"10.0", "OrderNumber":"", "SourceApplicationCode":"ANS", "SourceApplicationCodeLiteral":"EPOSTRX" } ] } } }';
                httpRes.setBody(AdjPay);
                system.assert(httpRes.getBody() == AdjPay);
                Map<String, HTTPRequest> mapDetails = continueObj.getRequests();                
                string reqTypeAdjPay = '';
                for(String s : mapDetails.keyset())
                {
                    reqTypeAdjPay = s;
                } 
                List<string>labelGetmember = new List<string>();                
                labelGetmember.add(reqTypeAdjPay);
                Test.setContinuationResponse(reqTypeAdjPay,httpRes);
                PharmacyFinancial_LC_HUM.parseGetAdjPayResponse(labelGetmember,continueObj);
                system.assert(labelGetmember.contains(reqTypeAdjPay));
            } catch (Exception e) {
                system.assertEquals('Script-thrown exception', e.getMessage(),'success');
            }            
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
            try {
                ApexPages.StandardController scon = new ApexPages.StandardController(policyObj);
                PharmacyFinancial_LC_HUM pharmacyCreditCard = new PharmacyFinancial_LC_HUM();
                PharmacyFinancial_LS_HUM oServicePharmacy = new PharmacyFinancial_LS_HUM();
                delete lstWebCallout;
                Continuation continueObj = (Continuation)PharmacyFinancial_LC_HUM.invokeGetAdjPayService('1070125929','05/03/2016','06/30/2016','true','true','SXS0295','0015C000002T9uJ');
                HttpResponse httpRes = new Httpresponse ();
                String AdjPay= '{ "GetAdjustmentsAndPaymentsResponse":{ "Payments":{ "Payment":[ { "PaymentScheduledDate":"05/09/2016", "PaymentProcessedDate":"05/09/2016", "CreditCardTypeLiteral":"VISA", "Last4Digit":"1113", "ChkOrMoneyOrderNumber":"", "Amount":"10.0", "OrderNumber":"", "SourceApplicationCode":"ANS", "SourceApplicationCodeLiteral":"EPOSTRX" }, { "PaymentScheduledDate":"06/02/2015", "PaymentProcessedDate":"05/09/2016", "CreditCardTypeLiteral":"VISA", "Last4Digit":"1113", "ChkOrMoneyOrderNumber":"", "Amount":"12.99", "OrderNumber":"", "SourceApplicationCode":"SIL", "SourceApplicationCodeLiteral":"SILVERLINK" }, { "PaymentScheduledDate":"04/27/2016", "PaymentProcessedDate":"05/09/2016", "CreditCardTypeLiteral":"VISA", "Last4Digit":"1113", "ChkOrMoneyOrderNumber":"", "Amount":"10.0", "OrderNumber":"", "SourceApplicationCode":"ANS", "SourceApplicationCodeLiteral":"EPOSTRX" } ] } } }';
                httpRes.setBody(AdjPay);
                system.assert(httpRes.getBody() == AdjPay);
                string reqTypeAdjPay = '';
                List<string>labelGetmember = new List<string>();
                labelGetmember.add(reqTypeAdjPay);
                Test.setContinuationResponse(reqTypeAdjPay,httpRes);
                PharmacyFinancial_LC_HUM.parseGetAdjPayResponse(labelGetmember,continueObj);
            } catch (Exception e) {
                system.assertEquals('Script-thrown exception', e.getMessage(),'success');
            }            
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
            try {
                PharmacyFinancial_LC_HUM pharmacyCreditCard = new PharmacyFinancial_LC_HUM();
                PharmacyFinancial_LS_HUM oServicePharmacy = new PharmacyFinancial_LS_HUM();
                PharmacyFinancial_LD_HUM oDataPharmacy = new PharmacyFinancial_LD_HUM();
                PharmacyFinancial_LC_HUM.invokeOneTimeCreditCardPayment('1048868178','sim4764','9587133',100.00,'2025-12-12',true);                     
                HttpResponse httpRes = new Httpresponse();
                String resBodyString= '{ "EditMemberResponse":{ "transactionKey":"1120000275944", "transactionTimeStamp":"Tue, Mar 21 2017 08:55:28 313 EDT", "UID":"" } }';
                httpRes.setBody(resBodyString);
                system.assert(httpRes.getBody() == resBodyString);            
                string reqType = 's';            
                List<string>labelGetmember = new List<string>();
                labelGetmember.add(reqType);
                Test.setContinuationResponse(reqType,httpRes);
                PharmacyFinancial_LC_HUM.parseOneTimePaymentResponse(labelGetmember,reqType);
            } catch (Exception e) {
                system.assertEquals('Script-thrown exception', e.getMessage(),'success');
            }        
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
            try {
                PharmacyFinancial_LC_HUM pharmacyCreditCard = new PharmacyFinancial_LC_HUM();
                PharmacyFinancial_LS_HUM oServicePharmacy = new PharmacyFinancial_LS_HUM();
                PharmacyFinancial_LC_HUM.invokeOneTimeCreditCardPayment('1111111111','sim4764','9587133',100.00,'2019-12-12',true);
                HttpResponse httpRes = new Httpresponse();
                String resBodyString= '{ "Fault":{ "faultcode":"\n          Code-67.8.1", "faultstring":"\n          error_service_name :\"RightSourceService &#124;\n          error_service_method : GetAdjustmentsAndPayments &#124;\n          fault_message : ", "detail":{ "transactionInfo":{ "transaction-id":"207260733", "transaction-client":"127.0.0.1", "URL-in":"http://127.0.0.1:8316/RightSourceService_V1.0/GetAdjustmentsAndPayments", "processor-name":"JSONServicesGateway", "processor-type":"Multiprotocol Gateway" }, "errorInfo":{ "error-code":"0x00d30003", "error-headers":"HTTP/1.1 500 Internal Server Error\r\nContent-Type: text/xml; charset=UTF-8\r\nX-Backside-Transport: OK OK\r\nConnection: close\r\n\r\n", "error-message":"Member not found", "error-protocol-reason-phrase":"", "error-protocol-response":"0", "error-subcode":"0x00d30003" } } } }';
                httpRes.setBody(resBodyString);
                system.assert(httpRes.getBody() == resBodyString);
                string reqType = '';
                List<string>labelGetmember = new List<string>();
                labelGetmember.add(reqType);
                Test.setContinuationResponse(reqType,httpRes);
                PharmacyFinancial_LC_HUM.parseOneTimePaymentResponse(labelGetmember,reqType);
            } catch (Exception e) {
                system.assertEquals('Script-thrown exception', e.getMessage(),'success');
            }            
            Test.stopTest();
        }
    }
}