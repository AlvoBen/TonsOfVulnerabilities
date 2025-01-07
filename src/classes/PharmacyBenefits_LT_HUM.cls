/*******************************************************************************************************************************
Apex Class Name : PharmacyBenefits_LT_HUM.cls
Version         : 1.0
Created On      : 10/14/2021
Function        : Class contains test methods PharmacyBenefits_LC_HUM class.

Modification Log: 
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Nirmal Garg                                 			10/14/2021                 Original Version
* Jonathan Dickinson                                   10/20/2021                 Added test methods from non-lightning classes 
*                                                                                 And modified them to work with the New lightning classes
* M K Manoj                                            11/04/2022                 US-3578373, 3578548
* Pinky Vijur                                           01/23/2023                User Story 3578587: T1PRJ0870026 Solutran Integrations - CRM FR5.01 - TECH - Remove CRM Service coding related to InComm OTC Balance
* Tharun M                                               02/03/2023                User Story 4138214: T1PRJ0870026 Solutran Integrations - CRM FR2.01 - Update RxNova Link to Auto - Prefill SSO and Member's Details
********************************************************************************************************************************/
@isTest
public class PharmacyBenefits_LT_HUM {
    
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
        system.assertEquals(objUser.lastname == 'test User1',true);
        HUM_Webservice_Callout__c PAAuthHubDetails = new HUM_Webservice_Callout__c(Name = 'PriorAuthHub',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c = 'https://int-crmservicews.humana.com/PriorAuthHubService_V2.0/GetPriorAuthStatusHistory' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
        insert PAAuthHubDetails;
        HUMTriggerSwitch__c btriggerSwitch = new HUMTriggerSwitch__c(Name='PharmacyBenefits',Exeute_Trigger__c=true);
        insert btriggerSwitch;
        system.assertEquals(btriggerSwitch.Name == 'PharmacyBenefits',true);
        HUMCertificateURL__c certificateUrl = new HUMCertificateURL__c(Name='PharmacyBenefits_IncommUrl',CertificateListURL__c='https://www.otcnetwork.com/Management/');
        insert certificateUrl;
        system.assertEquals(certificateUrl.Name == 'PharmacyBenefits_IncommUrl',true);
        //REQ-729137
        HUMCertificateURL__c certificateUrlRx = new HUMCertificateURL__c(Name='PharmacyBenefits_RxNovaUrl',CertificateListURL__c='https://beta-secure.argushealth.com/ua3/member-callcenter-web/pages/home.xhtml');
        insert certificateUrlRx;
        system.assertEquals(certificateUrlRx.Name == 'PharmacyBenefits_RxNovaUrl',true);
        
        HUM_Webservice_Callout__c soltrnOTC = new HUM_Webservice_Callout__c(Name = 'PharmacyBenefits_SolutranOTC_V3',Certificate_Name__c = 'Humana_GuidanceCare_Rigel' ,Active__c = true,End_Point_URL__c = 'https://dev-crmservicews.humana.com/OTCBalanceService_V3/GetOTCBalance' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
        insert soltrnOTC;
        system.assertEquals(soltrnOTC.Name == 'PharmacyBenefits_SolutranOTC_V3',true);
        
        HUMCertificateURL__c certificateUrlSoltrn = new HUMCertificateURL__c(Name='PharmacyBenefits_SolutranUrl',CertificateListURL__c='https://psp.solutran.com/psp/cardholdersearch');
        insert certificateUrlSoltrn;
        system.assertEquals(certificateUrlSoltrn.Name == 'PharmacyBenefits_SolutranUrl',true);
        
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
            //Product
            Product2 oproduct = new Product2();
            oproduct.Name='MED';
            oproduct.Product_Type__c='MA';           
            insert oproduct;
            system.assertEquals(oproduct.Name == 'MED', true);
            // Policy
            Policy__c oMedicalPolicy = HUMTestDataHelperTest.getPolicy();
            oMedicalPolicy.Name = 'Medical Product';
            oMedicalPolicy.source_cust_cov_key__c = '12345678901234567890' ; 
            oMedicalPolicy.Group_Name__c = objMemAccount.Id;
            oMedicalPolicy.Major_LOB__c ='EPO';
            oMedicalPolicy.Platform__c='LV';
            oMedicalPolicy.Product__c=oproduct.Id;
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
            
            PurchaserPlan oPolicy = new PurchaserPlan(name = 'Test Policy VIS', PayerId = objMemAccount.Id, Policy_Group_Number__c='11111',Platform__c='LV');
            insert oPolicy;  
            system.assert(oPolicy.id != null);
            
            MemberPlan objPolMem1 = new MemberPlan();
            objPolMem1.Name = 'testpolmem';
            objPolMem1.Member_Id_Base__c = '';
            objPolMem1.MemberId = objMemAccount.Id;
            objPolMem1.Member_Dependent_Code__c = '01';
            objPolMem1.Member_Id_Base__c = 'H62753913';
            objPolMem1.PlanId = oPolicy.Id;
            objPolMem1.Plan = oPolicy;
            insert objPolMem1;
            
            Product2 oproduct2 = new Product2();
            oproduct2.Name='DEN';
            oproduct2.Product_Type__c='CA';           
            insert oproduct2;
            system.assertEquals(oproduct2.Name == 'DEN',true);
            
            Policy__c oMedicalPolicy2 = HUMTestDataHelperTest.getPolicy();
            oMedicalPolicy2.Name = 'Medical Product';
            oMedicalPolicy2.Group_Name__c = objMemAccount.Id;
            oMedicalPolicy2.Major_LOB__c ='EPO';
            oMedicalPolicy2.Platform__c='CB';
            oMedicalPolicy2.Product__c=oproduct2.Id;
            insert oMedicalPolicy2;
            system.assertEquals(oMedicalPolicy2.Platform__c == 'CB',true);
            
            Policy_Member__c objMedPolMem2 = HUMTestDataHelperTest.getPolicyMember();
            objMedPolMem2.Member__c = objMemAccount.Id;
            objMedPolMem2.Policy__c = oMedicalPolicy2.Id;
            objMedPolMem2.Relationship__c = 'not self';  
            objMedPolMem2.end_date__c = '12/31/2015'; 
            objMedPolMem2.Effective_Date__c='01/01/2015';       
            insert objMedPolMem2;
            system.assertEquals(objMedPolMem2.Relationship__c == 'not self',true);
            
            //REQ-729137
            Member_ID__c objMemId = new Member_ID__c();
            objMemId.Policy_Member__c=objMedPolMem.id;
            objMemId.Policy__c=oMedicalPolicy.id;
            objMemId.Name='12345';
            objMemId.Type__c='Member-Id-Base';
            insert objMemId;
            system.assertEquals(objMemId.Type__c == 'Member-Id-Base',true);
            
            List<HUMConstants__c> lstConstanst = new List<HUMConstants__c>();
            
            lstConstanst.Add(new HUMConstants__c(Name='PromptPA URL',StringValue__c='ABC',IntegerValue__c=0));
            
            insert lstConstanst;
            System.assert(lstConstanst.size() > 0);
        }
    }
    
    /**
    * Test method for pharmacy data.
    *<p>
    * @param            NULL
    * @return           void
    */
    static testMethod void testPolicyMember()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        Account acc = [select Id, name, LastName from account where LastName = 'LName' limit 1 ];
        system.assertEquals(acc.LastName,'LName');  
        System.runAs(testUser)
        {
            Test.startTest();
            PharmacyBenefits_LC_HUM objPharmFin = new PharmacyBenefits_LC_HUM();
            List<MemberPlan> lstMemberPlan = PharmacyBenefits_LC_HUM.getPolicyMemberData(acc.Id);
            if(lstMemberPlan != null && lstMemberPlan.size() > 0){
                system.assertEquals('H62753913',lstMemberPlan[0].Member_Id_Base__c); 
            	system.assertNotEquals(0, lstMemberPlan.size());
            }else{
               system.assertEquals(null, lstMemberPlan); 
            }            
            Test.stopTest();
        }
    }
    
    /**
    * Test method for pharmacy data.
    *<p>
    * @param            NULL
    * @return           void
    */
    static testMethod void testPolicyMemberNull1()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        Account acc = [select Id, name, LastName from account where LastName = 'LName' limit 1 ];
        system.assertEquals(acc.LastName,'LName');  
        System.runAs(testUser)
        {
            Test.startTest();
            PharmacyBenefits_LC_HUM objPharmFin = new PharmacyBenefits_LC_HUM();
            List<MemberPlan> lstMemberPlan = PharmacyBenefits_LC_HUM.getPolicyMemberData(null); 
            system.assertEquals(null, lstMemberPlan);
            Test.stopTest();
        }
    }
    
    
    /**
    * NegativeTest method for pharmacy data.
    *<p>
    * @param            NULL
    * @return           void
    */
    static testMethod void testPolicyMemberNull()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        Account acc = [select Id, name, LastName from account where LastName = 'LName' limit 1 ];
        system.assertEquals(acc.LastName,'LName');  
        System.runAs(testUser)
        {
            Test.startTest();
            PharmacyBenefits_LC_HUM objPharmFin = new PharmacyBenefits_LC_HUM();
            List<MemberPlan> lstMemberPlan = PharmacyBenefits_LC_HUM.getPolicyMemberData('');
            system.assertEquals(null,lstMemberPlan); 
            Test.stopTest();
        }
    }
    
    
    /**
    * Positive test method for Pharmacy Calls Authorization funtionality.
    *<p>
    * @param            NULL
    * @return           void
    */
    static testMethod void testPharmacyCallsAuthorization()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        Account accObj = new Account();
        accObj.Name = 'Test Account';
        insert accObj;
        String accID = accObj.Id;
        System.runAs(testUser)
        {
            Test.startTest();            
            PharmacyBenefits_LC_HUM pharmacybenefits = new PharmacyBenefits_LC_HUM();
            PharmacyBenefits_LS_HUM oServicePharmacy = new PharmacyBenefits_LS_HUM();
            Continuation continueObj = (Continuation)PharmacyBenefits_LC_HUM.invokeGetpAuthService('1014741145');
            continueObj.continuationMethod = 'parseGetPAuthResponse';
            HttpResponse httpRes = new Httpresponse();
            String resBodyString= '{"GetPriorAuthStatusHistoryResponse": {"PriorAuthDetails": [{"AgadiaAuthDetails": [{"AuthDescription": "DENIED","EOCCreationDate": "08/19/2021","EOCDecisionDate": "08/23/2021","DrugName": "VELPHORO 500 MG CHEWABLE TAB","HCSEventType": "INITIAL/PRIMARY","DenialLanguage": "TEST","DecisionRoute": "ABORT","PrescriberFirstName": "AGADIA","PrescriberLastName": "RELEASE","PrescriberFaxNumber": "215-689-0872","DrugDetails": [{"Quantity": "640","DrugDaysSupply": "30"}]}]}]}}';
            httpRes.setBody(resBodyString);
            continueObj.state = 'Continuation-1';
            string reqType = 'Continuation-1';        
            List<string> labelGetmember = new List<string>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,httpRes);
            object result = PharmacyBenefits_LC_HUM.parseGetPAuthResponse(labelGetmember,resBodyString);
            Boolean bDrugExist = Json.serialize(result).containsIgnoreCase('VELPHORO 500 MG CHEWABLE TAB') ? true : false;
            system.assertEquals(true, bDrugExist);
            system.assertNotEquals(resBodyString, (string)result);
            system.assertEquals(httpRes.getBody(), resBodyString);
            Test.stopTest();
        }
    }
    
    /**
    * Null test method for Pharmacy Calls Authorization funtionality.
    *<p>
    * @param            NULL
    * @return           void
    */
    static testMethod void testPharmacyCallsAuthorizationNull()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        Account accObj = new Account();
        accObj.Name = 'Test Account';
        insert accObj;
        String accID = accObj.Id;
        System.runAs(testUser)
        {
            Test.startTest();
            try {
                PharmacyBenefits_LC_HUM pharmacybenefits = new PharmacyBenefits_LC_HUM();
                PharmacyBenefits_LS_HUM oServicePharmacy = new PharmacyBenefits_LS_HUM();
                Continuation continueObj = (Continuation)PharmacyBenefits_LC_HUM.invokeGetpAuthService('1014741145');
                continueObj.continuationMethod = 'parseGetPAuthResponse';
                HttpResponse httpRes = new Httpresponse ();
                String resBodyString= '{"GetPriorAuthStatusHistoryResponse": {"PriorAuthDetails": ';
                httpRes.setBody(resBodyString);
                continueObj.state = 'Continuation-1';
                string reqType = 'Continuation-1';        
                List<string> labelGetmember = new List<string>();
                labelGetmember.add(reqType);
                Test.setContinuationResponse(reqType,httpRes);
                Object result = PharmacyBenefits_LC_HUM.parseGetPAuthResponse(labelGetmember,resBodyString);
                system.assertEquals('null', result);
            } catch (Exception e) {
                system.assertEquals('Script-thrown exception', e.getMessage(),'success');
            }            
            Test.stopTest();
        }
    }
    
    
    /**
    * Fault test method for Pharmacy Calls Authorization funtionality.
    *<p>
    * @param            NULL
    * @return           void
    */
    static testMethod void testPharmacyCallsAuthorizationFault()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        Account accObj = new Account();
        accObj.Name = 'Test Account';
        insert accObj;
        String accID = accObj.Id;
        System.runAs(testUser)
        {
            Test.startTest();            
            PharmacyBenefits_LC_HUM pharmacybenefits = new PharmacyBenefits_LC_HUM();
            PharmacyBenefits_LS_HUM oServicePharmacy = new PharmacyBenefits_LS_HUM();
            Continuation continueObj = (Continuation)PharmacyBenefits_LC_HUM.invokeGetpAuthService('1014741145');
            continueObj.continuationMethod = 'parseGetPAuthResponse';
            HttpResponse httpRes = new Httpresponse ();
            String resBodyString= '{"Fault": {   "faultcode": "\n          Code-110..1",   "faultstring": "\n          error_service_name :\"PriorAuthHubService |\n          error_service_method : GetPriorAuthStatusHistory |\n          fault_message : ",   "detail":    {      "transactionInfo":       {         "transaction-id": "724757268",         "transaction-client": "10.114.96.251",         "URL-in": "http://127.0.0.1:8317/PriorAuthHubService_V2.0/GetPriorAuthStatusHistory",         "processor-name": "JSONServicesGatewayNonXML",         "processor-type": "Multiprotocol Gateway"      },      "errorInfo":       {         "error-code": "0x00d30003",         "error-headers": "HTTP/1.1 500 Internal Server Error\r\nX-Backside-Transport: OK OK\r\nContent-Type: text/xml\r\nConnection: close\r\n\r\n",         "error-message": "105-Member does not have any Prior Auth history",         "error-protocol-reason-phrase": "",         "error-protocol-response": "0",         "error-subcode": "0x00d30003"      }   }}}';
            httpRes.setBody(resBodyString);
            continueObj.state = 'Continuation-1';
            string reqType = 'Continuation-1';        
            List<string> labelGetmember = new List<string>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,httpRes);
            Object result = PharmacyBenefits_LC_HUM.parseGetPAuthResponse(labelGetmember,resBodyString);
            system.assertNotEquals(null, result);
            Test.stopTest();
        }
    }
    
    //NEW
    
    /**
    * Negative test method for Pharmacy Calls Authorization funtionality.
    *<p>
    * @param            NULL
    * @return           void
    */
    static testMethod void testCatchPharmacyCallsAuthorization()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        List<HUM_Webservice_Callout__c > lstWebCallout = [Select id from HUM_Webservice_Callout__c];
        Account accObj = new Account();
        accObj.Name = 'Test Account';
        insert accObj;
        String accID = accObj.Id;
        System.runAs(testUser)
        {
            Test.startTest();
            try{
                PharmacyBenefits_C_HUM pharmacyCreditCard = new PharmacyBenefits_C_HUM();
                PharmacyBenefits_S_HUM oServicePharmacy = new PharmacyBenefits_S_HUM();
                delete lstWebCallout;
                Continuation continueObj = (Continuation)PharmacyBenefits_LC_HUM.invokeGetpAuthService('1014741145');
                HttpResponse httpRes = new Httpresponse ();
                String resBodyString= '{\"GetPriorAuthStatusHistoryResponse\":{\"PriorAuthDetails\":[{\"ArgusAuthDetails\":[{\"AuthDescription\":\"PRE\",\"AuthAddDate\":\"1/4/2012\",\"AuthDecisionDate\":\"1/4/2012\",\"DrugName\":\"SERTRALINEHCL\"},{\"AuthDescription\":\"PRE\",\"AuthAddDate\":\"8/4/2010\",\"AuthDecisionDate\":\"8/4/2010\",\"DrugName\":\"HYDROCODONE/ACETAMINOPHEN\"}]},{\"AgadiaAuthDetails\":[{\"AuthDescription\":\"EOCABORTED\",\"EOCCreationDate\":\"1/12/2012\",\"EOCDecisionDate\":\"1/22/2012\",\"DrugName\":\"BDINSULINSYR0.5ML30GX1/2\\\"\"}]}]}}';
                httpRes.setBody(resBodyString);
                system.assert(httpRes.getBody() == resBodyString);
                string reqType = '';
                List<string>labelGetmember = new List<string>();
                labelGetmember.add(reqType);
                Test.setContinuationResponse(reqType,httpRes);
                PharmacyBenefits_LC_HUM.parseGetPAuthResponse(labelGetmember,continueObj);
            }            
            catch(exception e){
                system.assertEquals('Script-thrown exception', e.getMessage(),'success');
            }
            Test.stopTest();
        }
    }
    
    /**
    * test method to cover Solutran OTC service
    *<p>
    * @param            NULL
    * @return           void
    */
    @isTest static void testgetSoltrnOTCBalanceV3()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        List<HUM_Webservice_Callout__c > lstWebCallout = [Select id,Name from HUM_Webservice_Callout__c where Name = 'PharmacyBenefits_SolutranOTC_V3'];
        System.runAs(testUser)
        {
            Test.startTest();
            PharmacyBenefits_LC_HUM pharmacyCreditCard = new PharmacyBenefits_LC_HUM();
            PharmacyBenefits_LS_HUM oServicePharmacy = new PharmacyBenefits_LS_HUM();
            Continuation continueObj = (Continuation)PharmacyBenefits_LC_HUM.getSoltrnOTCBalanceV3('H23423', '118', 'H1245', '079 711');
            continueObj.continuationMethod = 'parseSoltrnOTCBalanceV3';
            continueObj.state = '24423';
            HttpResponse httpRes = new Httpresponse();
            String resBodyString='{"BalanceInformation":{"Balance": [{"Vendor": "SS&C","StartDate": "2022-07-01","EndDate": "2022-09-30","AvailableBalance": 525.00,"RollOverFlag": true,"BalanceType": "Over-the-Counter Benefit","PlanInformation": {"ContractId": "H5619","PlanNumber": "093","PlanOptionNumber": "076 148"},"Cards": [{"CardNumber": "55555500000001987","Last4": 5621,"Status": "Active"}],"ResponseCode": "00","ResponseMessage": "Success"}],"error": null}}';
            httpRes.setBody(resBodyString);
            system.assert(httpRes.getBody() == resBodyString);
            string reqType = 'Continuation-1';
            List<string> labelGetmember = new List<string>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,httpRes);          
            Object result = PharmacyBenefits_LC_HUM.parseSoltrnOTCBalanceV3(labelGetmember,continueObj);
            Boolean bDataExist = Json.serialize(result).containsIgnoreCase('25') ? true : false;
            System.assertEquals(true, bDataExist);
            Test.stopTest();
        }
    }
    
    /**
    * test method to cover Solutran OTC service
    *<p>
    * @param            NULL
    * @return           void
    */
    @isTest static void testgetSoltrn2OTCBalanceV3()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        List<HUM_Webservice_Callout__c > lstWebCallout = [Select id,Name from HUM_Webservice_Callout__c where Name = 'PharmacyBenefits_SolutranOTC_V3'];
        System.runAs(testUser)
        {
            Test.startTest();
            PharmacyBenefits_LC_HUM pharmacyCreditCard = new PharmacyBenefits_LC_HUM();
            PharmacyBenefits_LS_HUM oServicePharmacy = new PharmacyBenefits_LS_HUM();
            Continuation continueObj = (Continuation)PharmacyBenefits_LC_HUM.getSoltrnOTCBalanceV3('H23423', '118', 'H1245', '079 711');
            continueObj.continuationMethod = 'parseSoltrnOTCBalanceV3';
            continueObj.state = '24423';
            HttpResponse httpRes = new Httpresponse();
            String resBodyString='{"BalanceInformation":{"Balance": [{"Vendor": "SS&C","StartDate": "2022-07-01","EndDate": "2022-09-30","AvailableBalance": 525.00,"RollOverFlag": true,"BalanceType": "Over-the-Counter Benefit","PlanInformation": {"ContractId": "H5619","PlanNumber": "093","PlanOptionNumber": "076 148"},"Cards": [{"CardNumber": "55555500000001987","Last4": 5621,"Status": "Active"}],"ResponseCode": "00","ResponseMessage": "Success"},{"Vendor": "Solutran","StartDate": "2022-07-01","EndDate": "2022-09-30","AvailableBalance": 525.00,"RollOverFlag": true,"BalanceType": "Over-the-Counter Benefit","PlanInformation": {"ContractId": "H5619","PlanNumber": "093","PlanOptionNumber": "076 148"},"Cards": [{"CardNumber": "55555500000001987","Last4": 5621,"Status": "Active"}],"ResponseCode": "00","ResponseMessage": "Success"}],"error": null}}';
            httpRes.setBody(resBodyString);
            system.assert(httpRes.getBody() == resBodyString);
            string reqType = 'Continuation-1';
            List<string> labelGetmember = new List<string>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,httpRes);          
            Object result = PharmacyBenefits_LC_HUM.parseSoltrnOTCBalanceV3(labelGetmember,continueObj);
            Boolean bDataExist = Json.serialize(result).containsIgnoreCase('25') ? true : false;
            System.assertEquals(true, bDataExist);
            Test.stopTest();
        }
    }
    
    /**
    * test method to cover Solutran OTC service
    *<p>
    * @param            NULL
    * @return           void
    */
    @isTest static void testgetSoltrnOTCBalanceV3Null()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        List<HUM_Webservice_Callout__c > lstWebCallout = [Select id,Name from HUM_Webservice_Callout__c where Name = 'PharmacyBenefits_SolutranOTC_V3'];
        Policy_Member__c polMem = [Select id from Policy_Member__c limit 1];
        System.runAs(testUser)
        {
            Test.startTest();
            PharmacyBenefits_LC_HUM pharmacyCreditCard = new PharmacyBenefits_LC_HUM();
            PharmacyBenefits_LS_HUM oServicePharmacy = new PharmacyBenefits_LS_HUM();
            Continuation continueObj = (Continuation)PharmacyBenefits_LC_HUM.getSoltrnOTCBalanceV3('H23423', '118', 'H1245', '079 711');
            continueObj.continuationMethod = 'parseSoltrnOTCBalanceV3';
            continueObj.state = '24423';
            HttpResponse httpRes = new Httpresponse();
            String resBodyString= '{"BalanceInformation":{"Balance": ';
            httpRes.setBody(resBodyString);
            system.assert(httpRes.getBody() == resBodyString);
            string reqType = 'Continuation-1';
            List<string> labelGetmember = new List<string>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,httpRes);          
            Object result = PharmacyBenefits_LC_HUM.parseSoltrnOTCBalanceV3(labelGetmember,continueObj);
            System.assertEquals('null', result);
            Test.stopTest();
        }
    }
    
    /**
    * test method to cover SOLUTRAN OTC service Request DTO
    *<p>
    * @param            NULL
    * @return           void
    */
    
    @isTest static void testPharmacyBenefitsSoltrnRequest_H_HUM()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        System.runAs(testUser)
        {
            Test.startTest();
            PharmacyBenefitsSoltrnRequest_H_HUM oRequest = new PharmacyBenefitsSoltrnRequest_H_HUM();
            oRequest.MemberIdentityQualifier = 'HumanaMemberId';
            oRequest.MemberIdentity = 'H23423';
            PharmacyBenefitsSoltrnRequest_H_HUM.PlanInformation oPlanInformation = new PharmacyBenefitsSoltrnRequest_H_HUM.PlanInformation();
            oPlanInformation.ContractId = '12355';
            oPlanInformation.PlanNumber = '524475';
            oPlanInformation.PlanOptionNumber = 'test123';
            system.assertEquals(oPlanInformation.PlanOptionNumber == 'test123',true);
            Test.stopTest();
        }
    }
    
    /**
    * test method to cover Solutran OTC service Response DTO
    *<p>
    * @param            NULL
    * @return           void
    */
    @isTest static void testPharmacyBenefitsSoltrnResponse_H_HUM()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        System.runAs(testUser)
        {
            Test.startTest();
            
            PharmacyBenefitsSoltrnResponse_H_HUM.Balance oBalance = new PharmacyBenefitsSoltrnResponse_H_HUM.Balance();
            oBalance.AvailableBalance = 100.00;
            oBalance.EndDate = '12/5/2020';
            oBalance.StartDate = '2/8/2022';
            oBalance.Vendor = 'testit';
            oBalance.RollOverFlag=false;
            oBalance.BalanceType='';
            oBalance.ResponseCode = '00052';
            oBalance.ResponseMessage = 'testing message';
            PharmacyBenefitsSoltrnResponse_H_HUM.planInformation oPlanInfo = new PharmacyBenefitsSoltrnResponse_H_HUM.planInformation();
            oPlanInfo.ContractId = '125524';
            oPlanInfo.PlanNumber = '5568574';
            oPlanInfo.PlanOptionNumber = '2568878';
            PharmacyBenefitsSoltrnResponse_H_HUM.Cards oCard = new PharmacyBenefitsSoltrnResponse_H_HUM.Cards();
            oCard.CardNumber = '125524';
            oCard.Last4 = '7890';
            oCard.Status = 'Active';
            PharmacyBenefitsSoltrnResponse_H_HUM.BalanceInformation oResponse = new PharmacyBenefitsSoltrnResponse_H_HUM.BalanceInformation();
            oResponse.error = 'abctest';
            system.assertEquals(oBalance.AvailableBalance == 100.00,true);
            Test.stopTest();
        }
    }
    
    @isTest static void testgetServiceURL(){
        User testUser = [Select Id,Name from User where LastName='test User1'];
        System.runAs(testUser)
        {
            Test.startTest();
            System.assertEquals(true,PharmacyBenefits_LC_HUM.getServiceURL('30510804301','01/01/2023').containsIgnoreCase('RxNova'));
            System.assertEquals(true,PharmacyBenefits_LC_HUM.getServiceURL('30510804301','01/01/2023').containsIgnoreCase('InComm'));
            System.assertEquals(true,PharmacyBenefits_LC_HUM.getServiceURL('30510804301','01/01/2023').containsIgnoreCase('Solutran'));
            Test.stopTest();
        }
    }    
}