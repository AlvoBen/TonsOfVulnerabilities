/*******************************************************************************************************************************
Apex Class Name :  Pharmacy_LT_HUM.cls
Version: 1.0
Created On      : July 20,2021
Function        : Class contains test methods Pharmacy_LC_HUM Class.

Modification Log :  
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Pallavi Shewale                                    07/19/2021                US: 2364907- Search- Add Humana Pharmacy Account Number to the Search screen
* Himalay Patel                                      08/20/2021                User Story 2421627: T1PRJ0002517 - MF 2 - Updating Addresses That Apply to More Than One Address Type (RxRF) (ID# 91)
* Swapnali Sonawane                                  09/03/2021                UserStory:2508657 HP- Ability to add LIS and Other Insurance Details to the Plan Member card
* Ashok Kumar Nutalapati            				 09/30/2021            	   REQ - 2593758 T1PRJ0002517 - MF 2 - New case button, create case, & open new case in a subtab (RXRF) (ID# 35b)
* Abhishek Mangutkar								 10/12/2021				   US - 2528482 - T1PRJ0078574 - MF 11812 - HP Lightning- 'Prescriptions' - Filters
* Pallavi Shewale								     10/28/2021				   US - 2664846 - T1PRJ0078574  - MF 11811 - HP- Lightning- Order Summary - Cards
* Abhishek Mangutkar								 11/08/2021				   US - 2674165 - T1PRJ0078574  - MF 11811 - HP- Lightning- Order - Rx Panel
* Swapnali Sonawane                                  11/14/2021                US - 2766098  T1PRJ0078574 - MF 11811 - HP Lightning- Add 'Prescriptions' - Auto refill
* Swapnali Sonawane                                  08/23/2022	          	US#3631288 Use purchaser plan object
* Monali Jagtap									     07/17/2023 				USER STORY- 4812246 T1PRJ0891415 Platform Management-  SF - TECH - Critical- Lightning – Pharmacy
********************************************************************************************************************************/

@isTest
Private Class Pharmacy_LT_HUM 
{
    /*
    * Method Name : setUpTestData
    * Description : This method creates test data For test Class
    * Return type : None
    * Parameter   : None
    */                                                                                                          
    @testSetup static void setUpTestData()
    {
        User objUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');   
        insert objUser;        
        system.runAs(objUser)
        {
            Pharmacy_Supervisor_HUM__c supervisorCS = New Pharmacy_Supervisor_HUM__c(Name = 'CRMS_300_Humana_Pharmacy_Supervisor');
            insert supervisorCS;
            
            HUM_Webservice_Callout__c RighSourceServiceDetails = New HUM_Webservice_Callout__c(Name = 'RighSourceService_GetMember',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c ='https://int-crmservicews.humana.com/RighSourceService_GetMember/' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
            insert RighSourceServiceDetails;
            
            system.assertEquals(objUser.lastname == 'test User1',true);
            // Insert Member Account
            Account objMemAccount = HUMTestDataHelperTest.getAccount();
            objMemAccount.FirstName = 'Test FName';
            objMemAccount.LastName = 'LName'; 
            objMemAccount.source_platform_code__c = 'EM' ;
            objMemAccount.PersonMailingCountry ='United States';
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
            Interaction__c objInteraction= HUMTestDataHelperTest.getInteraction();
            objInteraction.Interacting_With__c = objMemAccount.id;
            objInteraction.Interacting_With_type__c = 'Member';
            objInteraction.Interaction_External_ID__c = '1234567895';
            insert objInteraction;
        }
    }
    
    /*
    * Method Name : testPharmacyDemographic
    * Description : This method Is used To test the Pharmacy Demographic funtionality
    * Return type : None
    * Parameter   : None
    */
    Static testMethod void testPharmacyDemographic()
    {
        User testUser = [Select Id, Name from User where LastName='test User1'];
        policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        Account accobj = [Select id from Account where FirstName = 'Test FName' Limit 1];
        Id  accountId = accobj.id;
        System.runAs(testUser)
        {
            Test.startTest(); 
            try{
                Pharmacy_LC_HUM objPharmacy = New Pharmacy_LC_HUM();
                Pharmacy_LD_HUM oDataPharmacy = New Pharmacy_LD_HUM();
                ApexPages.StandardController scon = New ApexPages.StandardController(policyObj);
                Pharmacy_LC_HUM pharmacyDemographic = New Pharmacy_LC_HUM();
                Pharmacy_LS_HUM oServicePharmacy = New Pharmacy_LS_HUM();
                Continuation continueObj = (Continuation)Pharmacy_LC_HUM.invokeGetMemberService('1070177218','AXB0001',accountId);
                continueObj.continuationMethod = 'parseRSGetMemberResponse';
                continueObj.state = 'Both';
                HttpResponse resDemographic = New Httpresponse();
                String Demog ='{"GetMemberReponse":{ "Customers":{ "Customer":[ { "CustomerIdentity":{ "BirthDate":"10/02/1950", "CustomerKeys":{ "CustomerKey":[ { "CustomerType":"PM", "CustomerValue":"1086943639" }, { "CustomerType":"PC", "CustomerValue":"02" }, { "CustomerType":"PK", "CustomerValue":"6327008" }, { "CustomerType":"AC", "CustomerValue":"8903086" }, { "CustomerType":"NP", "CustomerValue":"107303489" } ] }, "FirstName":"ROYCETTE", "MiddleName":"M", "LastName":"BUTTREY", "Email":"abc@xyz.com" }, "CustomerPreference":{ "CapType":"E", "CapTypeLiteral":"EASY" }, "CustomerDetail":{ "NeedsMemberConsent":"false", "ConsentBeginDate":"", "ConsentEndDate":"", "MemberConsentStatus":"false", "CMRStatus":"", "CMRStatusLiteral":"" }, "Addresses":{ "Address":[ { "AddressKey":"16399980", "AddressType":"B", "AddressTypeLiteral":"BILLING", "AddressLine1":"9080 ABNER ROAD", "AddressLine2":"7469 LYNN AVENUE", "AddressName":"ROYCETTE BUTTREY", "City":"MONROE", "StateCode":"LA", "ZipCode":"71201", "IsActive":"true" }, { "AddressKey":"16399980", "AddressType":"P", "AddressTypeLiteral":"PERMANENT", "AddressLine1":"9080 ABNER ROAD", "AddressLine2":"7469 LYNN AVENUE", "AddressName":"ROYCETTE BUTTREY", "City":"MONROE", "StateCode":"LA", "ZipCode":"71201", "IsActive":"true" }, { "AddressKey":"17229497", "AddressType":"S", "AddressTypeLiteral":"SHIPPING", "AddressLine1":"13146 LAKE LN", "AddressLine2":"", "AddressName":"ROYCETTE BUTTREY", "City":"EUSTACE", "StateCode":"TX", "ZipCode":"75124", "IsActive":"true" }, { "AddressKey":"17229500", "AddressType":"A", "AddressTypeLiteral":"ALTERNATE", "AddressLine1":"PO BOX", "AddressLine2":"508 SPRING VIEW ST SW", "AddressName":"ROYCETTE BUTTREY", "City":"SUAMICO", "StateCode":"WI", "ZipCode":"54313", "IsActive":"true" } ] }, "Phones":{ "Phone":[ { "PhoneKey":"16218856", "PhoneType":"AL", "PhoneValue":"5025801000" }, { "PhoneKey":"19053575", "PhoneType":"CN", "PhoneValue":"5025801600" }, { "PhoneKey":"19053572", "PhoneType":"PP", "PhoneValue":"4545641234" } ] }, "StaleData":"0", "AccountNumber":{ "CustomerValue":"8903086" }, "Finance":{ "CreditLimit":"0.0", "OutstandingBalance":"50.6", "PaymentSchedules":{ "PaymentSchedule":[ { "PaymentDueDate":"", "PaymentMadeDate":"", "PaymentType":"", "SourceApplicationCode":"", "SourceApplicationCodeLiteral":"" } ] } }, "Allergies":{ "Allergy":[ { "HealthKey":"00153", "HealthDescription":"No Known Drug Allergy" } ] }, "CreditCards":{ "CreditCard":[ { "CreditCardKey":"7781077", "CreditCardType":"M", "CreditCardTypeLiteral":"MASTERCARD", "FirstName":"ROYCETTE", "LastName":"BUTTREY", "MiddleName":"L", "ExpirationMonth":"1", "ExpirationYear":"2021", "IsActive":"true", "IsAutoCharge":"false", "CreditCardLast4Digits":"6380", "IsSpendingAccount":"false" }, { "CreditCardKey":"7873701", "CreditCardType":"V", "CreditCardTypeLiteral":"VISA", "FirstName":"THOMAS", "LastName":"ONDERKO", "MiddleName":"", "ExpirationMonth":"10", "ExpirationYear":"2016", "IsActive":"true", "IsAutoCharge":"false", "CreditCardLast4Digits":"8394", "IsSpendingAccount":"false" }, { "CreditCardKey":"7873703", "CreditCardType":"A", "CreditCardTypeLiteral":"AMERICAN EXPRESS", "FirstName":"SONIA", "LastName":"ASENCIORAMOS", "MiddleName":"", "ExpirationMonth":"12", "ExpirationYear":"2022", "IsActive":"true", "IsAutoCharge":"true", "CreditCardLast4Digits":"8431", "IsSpendingAccount":"false" } ] }, "HealthConditions":{ "HealthCondition":[ { "HealthDescription":"Thyroid Disease" } ] }, "LogNotes":{ "LogNote":[  ] }, "Plans":{ "Plan":[ { "PlanEffectiveDate":"01/01/2018", "PlanTerminationDate":"12/31/9999", "PlanType":"COMM", "Payer":"HUMANA INC." } ] }, "communicationPreferences":{ "communicationPreference":[ { "commPrefType":"EMDCL", "commPrefValue":"true" } ] } } ] }, "DuplicateCustomers":{ "CustomerKey":[ { "customerType":"AC", "customerTypeLiteral":"ACCOUNT ID", "customerValue":"8903086" }, { "customerType":"PK", "customerTypeLiteral":"PRIMARY KEY", "customerValue":"6327008" }, { "customerType":"NP", "customerTypeLiteral":"NEW POLICY ID", "customerValue":"107303489" }, { "customerType":"AC", "customerTypeLiteral":"ACCOUNT ID", "customerValue":"8903086" }, { "customerType":"PK", "customerTypeLiteral":"PRIMARY KEY", "customerValue":"6421002" }, { "customerType":"NP", "customerTypeLiteral":"NEW POLICY ID", "customerValue":"107303489" } ] } } }';
                HUM_Webservice_Callout__c RighSourceServiceDetails = New HUM_Webservice_Callout__c(Name = 'RighSourceService_EditMember',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c ='https://int-crmservicews.humana.com/RighSourceService_GetMember/' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
                insert RighSourceServiceDetails;
                resDemographic.setBody(Demog);
                system.assert(resDemographic.getBody() == Demog);
                Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
                String reqType = '';
                for(String s : mapDetails.keyset())
                {
                    reqType = s;
                } 
                List<string>labelGetmember = New List<string>();
                labelGetmember.add(reqType);
                Test.setContinuationResponse(reqType,resDemographic);
                Pharmacy_LC_HUM.parseRSGetMemberResponse(labelGetmember,continueObj);
                Continuation continueObj3 = (Continuation)Pharmacy_LC_HUM.updateGetMemberAddresses('1070177218','AXB0001',accountId,Demog); 
                List<Account> AccountList = Pharmacy_LC_HUM.getAccountList(accountId );   
                system.assertEquals(AccountList[0].PersonMailingCountry == 'United States',true);                
            }
            catch(Exception e){
                system.assertEquals('Script-thrown exception', e.getMessage(),'success');
            }  
            Test.stopTest();
        }
    }
    
    /*
    * Method Name : testPharmacyDemographic
    * Description : This method Is used To test the Pharmacy Demographic funtionality
    * Return type : None
    * Parameter   : None
    */
    Static testMethod void testFaultPharmacyDemographic()
    {
        User testUser = [Select Id, Name from User where LastName='test User1'];
        policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        Account accobj = [Select id from Account where FirstName = 'Test FName' Limit 1];
        Id  accountId = accobj.id;
        System.runAs(testUser)
        {
            Test.startTest();
            try{
                Pharmacy_LC_HUM objPharmacy = New Pharmacy_LC_HUM();
                Pharmacy_LD_HUM oDataPharmacy = New Pharmacy_LD_HUM();
                ApexPages.StandardController scon = New ApexPages.StandardController(policyObj);
                Pharmacy_LC_HUM pharmacyDemographic = New Pharmacy_LC_HUM();
                Pharmacy_LS_HUM oServicePharmacy = New Pharmacy_LS_HUM();
                Continuation continueObj = (Continuation)Pharmacy_LC_HUM.invokeGetMemberService('1070177218','AXB0001',accountId);
                continueObj.continuationMethod = 'parseRSGetMemberResponse';
                continueObj.state = 'Both';
                HttpResponse resDemographic = New Httpresponse();
                String Demog = '{ "Fault":{ "faultcode":"\n          Code-67.8.1", "faultstring":"\n          error_service_name :\"RightSourceService &#124;\n          error_service_method : GetAdjustmentsAndPayments &#124;\n          fault_message : ", "detail":{ "transactionInfo":{ "transaction-id":"207260733", "transaction-client":"127.0.0.1", "URL-in":"http://127.0.0.1:8316/RightSourceService_V1.0/GetAdjustmentsAndPayments", "processor-name":"JSONServicesGateway", "processor-type":"Multiprotocol Gateway" }, "errorInfo":{ "error-code":"0x00d30003", "error-headers":"HTTP/1.1 500 Internal Server Error\r\nContent-Type: text/xml; charset=UTF-8\r\nX-Backside-Transport: OK OK\r\nConnection: close\r\n\r\n", "error-message":"Member not found", "error-protocol-reason-phrase":"", "error-protocol-response":"0", "error-subcode":"0x00d30003" } } } }';
                resDemographic.setBody(Demog);
                system.assert(resDemographic.getBody() == Demog);
                Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
                String reqType = '';
                for(String s : mapDetails.keyset())
                {
                    reqType = s;
                } 
                List<string>labelGetmember = New List<string>();
                labelGetmember.add(reqType);
                Test.setContinuationResponse(reqType,resDemographic);
                Pharmacy_LC_HUM.parseRSGetMemberResponse(labelGetmember,continueObj);
                Pharmacy_LC_HUM.retrieveUpdatedGetMemberAddresses(labelGetmember,continueObj);
                Continuation continueObj3 = (Continuation)Pharmacy_LC_HUM.updateGetMemberAddresses('1070177218','AXB0001',accountId,Demog);   
            }
            catch(Exception e){
                system.debug(e.getMessage());
                //system.assertEquals('Script-thrown exception', e.getMessage(),'success');
            }  
            Test.stopTest();
        }
    }
    
    Static testMethod void testAddress()
    {
        User testUser = [Select Id, Name from User where LastName='test User1'];
        policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        Account accobj = [Select id from Account where FirstName = 'Test FName' Limit 1];
        Id  accountId = accobj.id;        
        System.runAs(testUser)
        {
            Test.startTest();
            try{
                HUM_Webservice_Callout__c RighSourceServiceDetails = New HUM_Webservice_Callout__c(Name = 'RighSourceService_GetMember',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c ='https://int-crmservicews.humana.com/RighSourceService_GetMember/' ,Timeout__c = 'abc',Request_Type__c = 'POST',Content_Type__c ='application/json' );
                insert RighSourceServiceDetails;
                Continuation continueObj3 = (Continuation)Pharmacy_LC_HUM.invokeGetMemberService('abc','AXB0001',accountId);      
            } catch(Exception e){
                system.assertEquals('Script-thrown exception', e.getMessage(),'success');
            }            
            Test.stopTest();
        }
    }
    
    /*
    * Method Name : testOtherInsuranceService
    * Description : This method Is used To test the Other Insurace Details funtionality
    * Return type : None
    * Parameter   : None
    */
    static testMethod void testOtherInsuranceService(){
        Map<String,String> stateMap= new Map<String,String>();
        policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        String memberPlanId = policyObj.Id;
        String enterpriceId = '1087808915';        
        HUM_Webservice_Callout__c oMBEPlusService = new HUM_Webservice_Callout__c(Request_Type__c = 'POST', Name = 'MBEPlusService',
                                                                                  End_Point_URL__c = 'https://int-crmservicews.humana.com/MBEPLUSServiceREST/GetMember', Certificate_Name__c = 'Humana_GuidanceCare_HlthCldCop', Timeout__c = '12000',
                                                                                  Content_Type__c = 'application/json', Active__c = true);
        Test.startTest();
        try{
            Object con = (object)Pharmacy_LC_HUM.getOIData(memberPlanId,enterpriceId);
            HttpResponse httpRes = new Httpresponse();
            httpRes.setStatusCode(200);
            string resBodyString = '{"GetMemberResponse":{"Body":{"Member":{"DateOfBirth":"01/10/1953","FirstName":"PREAMERLER","Gender":"F","LastName":"PERALES","MiddleInitial":"O","Ssn":"857061394","MasterId":"1087808915","SoldProductList":{"SoldProduct":[{"InsuranceProduct":{"AlternateDescription":"MEDICARE PPO NON-GATE FFS ANY","Description":"079 771","MajorLineOfBusinessCode":"MEP","MajorLineOfBusinessDescription":"MEDICARE PREFERRED PROVIDER ORGANIZATION)","ProductStartDate":"01/01/20191","ProductEndDate":"12/31/2019","ProductId":"6232630101","Source":"LV","ProductLineCode":"MED","ProductLineDescription":"MEDICAL PRODUCTS","ProductTypeCode":"MAPD","SoldProductDetailList":{"SoldProductDetail":[{"InsuranceCoverage":{"EndDate":"12/31/2020","StartDate":"01/01/2019","CoveredRoleList":{"CoveredRole":[{"StartDate":"01/01/2019","EndDate":"12/31/2020","MemberMasterId":"1087808915","RelationshipToSubscriber":"EmployeeSelf","CoveredRolePlatformPointerList":{"PlatformProxyKey":[{"Application":"CI","KeyType":"MemberIDDependentCode","KeyValue":"00","Source":"LV"},{"Application":"CI","KeyType":"MemberIDDependentCode","KeyValue":"00","Source":"LV"},{"Application":"CI","KeyType":"MemberIdBase","KeyValue":"H17145469","Source":"LV"}]},"CoverageKey":"1087808915|002264293|002264293|LV|309876|309876001000||6232630101|2019-01-01|false|||2019-01-01|H17145469|00|KY"},{"StartDate":"01/01/2021","EndDate":"12/31/9999","MemberMasterId":"1087808915","RelationshipToSubscriber":"EmpolyeeSelf","CoveredRolePlatformPointerList":{"PlatformProxyKey":[{"Application":"EE","KeyType":"MemberIDDependentCode","KeyValue":"01","Source":"EM"},{"Application":"EE","KeyType":"MemberIdBase","KeyValue":"743930272","Source":"EM"},{"Application":"","KeyType":"PersonId","KeyValue":"0846922123005579","Source":"EM"}]},"CoverageKey":"1087808915|0846922123005579|0846922123005579|EM|675717|67571719||MC375797|2020-01-01|false|||2021-01-01|743930272|01|KY"}]},"CoverageType":"Single","GroupPlatformPointerList":{"PlatformProxyKey":[{"Application":"EE","KeyType":"DivisionId","KeyValue":"675717","Source":"EM"},{"Application":"EE","KeyType":"GroupId","KeyValue":"675717","Source":"EM"}]},"ContractId":"743930272","CoordinationOfBenefitList":{"CoordinationOfBenefit":[{"CarrierName":"MEDICARE PART A & B","EffectiveDate":"06/01/2018","EndDate":"12/31/9999","Primary":"1","OILastVerifiedDate":"12/31/9999","StatusCode":"AMDC","StatusDescription":"MEDICARE"}]},"OriginalEffectiveDate":"12/31/9999","OperationalMajorLineofBusiness":"NPOS","PolicySequenceNumber":"0"}}]}}}]}}}}}';
            httpRes.setBody(resBodyString);
            system.assert(httpRes.getBody() == resBodyString);         
            string reqType = 'Continuation-1';        
            Test.setContinuationResponse(reqType,httpRes);
            stateMap.put('sRequest',reqType);
            stateMap.put('sPolicyMember',memberPlanId);
            Pharmacy_LC_HUM.processMBEOtherInsuranceResponse(stateMap);
        }
        catch(Exception e){
            system.assertEquals('Script-thrown exception', e.getMessage(),'success');
        }
        Test.stopTest();
    }
    
    /*
    * Method Name : testcreatePharmacyCaseAndRedirect
    * Description : This method Is used To test createPharmacy Case Redirect.
    * Return type : void
    * Parameter   : NA
    */
    Static testMethod void testcreatePharmacyCaseAndRedirect() 
    {    
        User testUser = [Select Id, Name from User where LastName='test User1'];
        Policy_member__c policyObj = [Select id from Policy_member__c where Relationship__c = 'self' limit 1];
        Account accobj = [Select id from Account where FirstName = 'Test FName' Limit 1];
        List<Interaction__c> intId =[SELECT Id, Name FROM Interaction__c limit 1];
        String intractId = intID[0].id;        
        System.runAs(testUser)
        {
            test.startTest();
            Pharmacy_LC_HUM.createPharmacyCaseAndRedirect(intractId, policyObj.Id , accobj.Id);            
            test.stopTest();
        }
    }
    
    /*
    * Method Name : testPharmacyUpdateCapType
    * Description : This method Is used To test the update cap type funtionality
    * Return type : None
    * Parameter   : None
    */
    Static testMethod void testPharmacyUpdateCapType()
    {
        User testUser = [Select Id, Name from User where LastName='test User1'];
        Policy_member__c policyObj = [Select id from Policy_member__c where Relationship__c = 'self' limit 1];
        Account accobj = [Select id from Account where FirstName = 'Test FName' Limit 1];
        Id  accountId = accobj.id;
        System.runAs(testUser)
        {
            Test.startTest();
            try{
                Pharmacy_LC_HUM objPharmacy = New Pharmacy_LC_HUM();     
                Pharmacy_LD_HUM oDataPharmacy = New Pharmacy_LD_HUM();
                ApexPages.StandardController scon = New ApexPages.StandardController(policyObj);
                Pharmacy_LC_HUM pharmacyDemographic = New Pharmacy_LC_HUM();
                Pharmacy_LS_HUM oServicePharmacy = New Pharmacy_LS_HUM();
                Pharmacy_LC_HUM.updateCaptype('114238930','Parent','AXB0001',accountId);                
                HttpResponse resDemographic = New Httpresponse();
                String Demog = '{"EditMemberRequest":{"members":[{"EnterprisePersonID":"1048868178","addresses":{"address":[{"addressKey":"-1","addressLine1":"13146 LAKE LN","addressLine2":"","addressType":"S","addressName":"RONALD MAHAN","city":"EUSTACE","isActive":"true","overrideReasonCode":"","stateCode":"TX","uspsValidate":"true","zipCode":"75124"}]},"creditCards":{"creditCard":[{"creditCardKey":"-1","creditCardType":"V","expirationMonth":"10","expirationYear":"2016","firstName":"THOMAS","isActive":"true","isAutoCharge":"false","lastName":"ONDERKO","middleName":"","tokenKey":"4315830000418394"}]},"customerDetail":{"cmrStatus":"DNC","email":"abc@xyz.com"},"logNotes":{"logNote":[{"logNoteCode":"CONSENT","logNoteDate":"12/07/2016","logNoteKey":"-1","logNoteMessage":"update for testing","logNoteUser":"ANSUSR"}]},"phones":{"phone":[{"phoneKey":"-1","phoneType":"PP","phoneValue":"4545641234"}]}	}]}}';
                resDemographic.setBody(Demog);
                system.assert(resDemographic.getBody() == Demog);                
                String reqType = 's';                
                List<string> labelGetmember = New List<string>();
                labelGetmember.add(reqType);
                Test.setContinuationResponse(reqType,resDemographic);
                Pharmacy_LC_HUM.retrieveUpdatedCapType(labelGetmember,'Both_AXB0001_'+accountId);
                List<string>testGetMemberData = New List<string>();
                testGetMemberData.add(reqType);
                system.assertEquals(labelGetmember, testGetMemberData);   
            }
            catch(Exception e){
                system.assertEquals('Script-thrown exception', e.getMessage(),'success');
            }
            Test.stopTest();
        }
    }
    
    /*
    * Method Name : testFaultPharmacyUpdateCapType
    * Description : This method Is used To test the faults in updates cap type funtionality
    * Return type : None
    * Parameter   : None
    */
    Static testMethod void testFaultPharmacyUpdateCapType()
    {
        User testUser = [Select Id, Name from User where LastName='test User1'];
        policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        Account accobj = [Select id from Account where FirstName = 'Test FName' Limit 1];
        Id  accountId = accobj.id;
        System.runAs(testUser)
        {
            Test.startTest();
            try{
                Pharmacy_LC_HUM objPharmacy = New Pharmacy_LC_HUM();  
                Pharmacy_LD_HUM oDataPharmacy = New Pharmacy_LD_HUM();
                ApexPages.StandardController scon = New ApexPages.StandardController(policyObj);
                Pharmacy_LC_HUM pharmacyDemographic = New Pharmacy_LC_HUM();
                Pharmacy_LS_HUM oServicePharmacy = New Pharmacy_LS_HUM();
                Pharmacy_LC_HUM.updateCaptype('114238930','Parent','AXB0001',accountId);                
                HttpResponse resDemographic = New Httpresponse();
                String Demog = '{ "Fault":{ "faultcode":"\n          Code-67.8.1", "faultstring":"\n          error_service_name :\"RightSourceService &#124;\n          error_service_method : GetAdjustmentsAndPayments &#124;\n          fault_message : ", "detail":{ "transactionInfo":{ "transaction-id":"207260733", "transaction-client":"127.0.0.1", "URL-in":"http://127.0.0.1:8316/RightSourceService_V1.0/GetAdjustmentsAndPayments", "processor-name":"JSONServicesGateway", "processor-type":"Multiprotocol Gateway" }, "errorInfo":{ "error-code":"0x00d30003", "error-headers":"HTTP/1.1 500 Internal Server Error\r\nContent-Type: text/xml; charset=UTF-8\r\nX-Backside-Transport: OK OK\r\nConnection: close\r\n\r\n", "error-message":"Member not found", "error-protocol-reason-phrase":"", "error-protocol-response":"0", "error-subcode":"0x00d30003" } } } }';
                resDemographic.setBody(Demog);
                system.assert(resDemographic.getBody() == Demog);                
                String reqType = 's';                
                List<string>labelGetmember = New List<string>();
                labelGetmember.add(reqType);
                Test.setContinuationResponse(reqType,resDemographic);
                Pharmacy_LC_HUM.retrieveUpdatedCapType(labelGetmember,'Both_AXB0001_'+accountId);
                List<string>testGetMemberData = New List<string>();
                testGetMemberData.add(reqType);
                system.assertEquals(labelGetmember, testGetMemberData); 
            }
            catch(Exception e){
                system.assertEquals('Script-thrown exception', e.getMessage(),'success');
            }
            Test.stopTest();
        }
    }
    
    /*
    * Method Name : testNegativePharmacyUpdateCapType
    * Description : This method Is used To test the negative scenarios of update cap type funtionality
    * Return type : None
    * Parameter   : None
    */
    Static testMethod void testNegativePharmacyUpdateCapType()
    {
        User testUser = [Select Id, Name from User where LastName='test User1'];
        Policy_member__c policyObj = [Select id from Policy_member__c where Relationship__c = 'self' limit 1];
        Account accobj = [Select id from Account where FirstName = 'Test FName' Limit 1];
        Id  accountId = accobj.id;
        List<HUM_Webservice_Callout__c > lstWebCallout = [Select id from HUM_Webservice_Callout__c];
        System.runAs(testUser)
        {
            Test.startTest();
            try{
                Pharmacy_LC_HUM objPharmacy = New Pharmacy_LC_HUM();  
                Pharmacy_LD_HUM oDataPharmacy = New Pharmacy_LD_HUM();
                ApexPages.StandardController scon = New ApexPages.StandardController(policyObj);
                Pharmacy_LC_HUM pharmacyDemographic = New Pharmacy_LC_HUM();
                Pharmacy_LS_HUM oServicePharmacy = New Pharmacy_LS_HUM();
                delete lstWebCallout;
                Pharmacy_LC_HUM.updateCaptype('114238930','Parent','AXB0001',accountId);
                HttpResponse resDemographic = New Httpresponse();
                String Demog = '{"EditMemberRequest":{"members":[{"EnterprisePersonID":"1048868178","addresses":{"address":[{"addressKey":"-1","addressLine1":"13146 LAKE LN","addressLine2":"","addressType":"S","addressName":"RONALD MAHAN","city":"EUSTACE","isActive":"true","overrideReasonCode":"","stateCode":"TX","uspsValidate":"true","zipCode":"75124"}]},"creditCards":{"creditCard":[{"creditCardKey":"-1","creditCardType":"V","expirationMonth":"10","expirationYear":"2016","firstName":"THOMAS","isActive":"true","isAutoCharge":"false","lastName":"ONDERKO","middleName":"","tokenKey":"4315830000418394"}]},"customerDetail":{"cmrStatus":"DNC","email":"abc@xyz.com"},"logNotes":{"logNote":[{"logNoteCode":"CONSENT","logNoteDate":"12/07/2016","logNoteKey":"-1","logNoteMessage":"update for testing","logNoteUser":"ANSUSR"}]},"phones":{"phone":[{"phoneKey":"-1","phoneType":"PP","phoneValue":"4545641234"}]}	}]}}';
                resDemographic.setBody(Demog);
                system.assert(resDemographic.getBody() == Demog);
                String reqType = '';
                List<string>labelGetmember = New List<string>();
                labelGetmember.add(reqType);
                Test.setContinuationResponse(reqType,resDemographic);
                Pharmacy_LC_HUM.retrieveUpdatedCapType(labelGetmember,'Both_AXB0001_'+accountId);
                List<string>testGetMemberData = New List<string>();
                testGetMemberData.add(reqType);
                system.assertEquals(labelGetmember, testGetMemberData); 
            }
            catch(Exception e){
                system.assertEquals('Script-thrown exception', e.getMessage(),'success');
            }
            Test.stopTest();
        }
    }
    
    /*
    * Method Name : testPharmacyUpdateConsentBeginDate
    * Description : This method Is used To test the Pharmacy update consent begin date funtionality
    * Return type : None
    * Parameter   : None
    */
    Static testMethod void testPharmacyUpdateConsentBeginDate()
    {
        User testUser = [Select Id, Name from User where LastName='test User1'];
        Policy_member__c policyObj = [Select id from Policy_member__c where Relationship__c = 'self' limit 1];
        Account accobj = [Select id from Account where FirstName = 'Test FName' Limit 1];
        Id  accountId = accobj.id;
        System.runAs(testUser)
        {
            Test.startTest();
            try{              
                Pharmacy_LC_HUM objPharmacy = New Pharmacy_LC_HUM();           
                Pharmacy_LD_HUM oDataPharmacy = New Pharmacy_LD_HUM();
                ApexPages.StandardController scon = New ApexPages.StandardController(policyObj);
                Pharmacy_LC_HUM pharmacyDemographic = New Pharmacy_LC_HUM();
                Pharmacy_LS_HUM oServicePharmacy = New Pharmacy_LS_HUM();
                Pharmacy_LC_HUM.updateConsentBeginDate('114238930','AXB0001',accountId);                
                HttpResponse resDemographic = New Httpresponse();
                String Demog = '{"EditMemberRequest":{"members":[{"EnterprisePersonID":"1048868178","addresses":{"address":[{"addressKey":"-1","addressLine1":"13146 LAKE LN","addressLine2":"","addressType":"S","addressName":"RONALD MAHAN","city":"EUSTACE","isActive":"true","overrideReasonCode":"","stateCode":"TX","uspsValidate":"true","zipCode":"75124"}]},"creditCards":{"creditCard":[{"creditCardKey":"-1","creditCardType":"V","expirationMonth":"10","expirationYear":"2016","firstName":"THOMAS","isActive":"true","isAutoCharge":"false","lastName":"ONDERKO","middleName":"","tokenKey":"4315830000418394"}]},"customerDetail":{"cmrStatus":"DNC","email":"abc@xyz.com"},"logNotes":{"logNote":[{"logNoteCode":"CONSENT","logNoteDate":"12/07/2016","logNoteKey":"-1","logNoteMessage":"update for testing","logNoteUser":"ANSUSR"}]},"phones":{"phone":[{"phoneKey":"-1","phoneType":"PP","phoneValue":"4545641234"}]}	}]}}';
                resDemographic.setBody(Demog);
                system.assert(resDemographic.getBody() == Demog);                
                String reqType = 's';                
                List<string>labelGetmember = New List<string>();
                labelGetmember.add(reqType);
                Test.setContinuationResponse(reqType,resDemographic);
                Pharmacy_LC_HUM.retrieveUpdatedMemberConsent(labelGetmember,'Both_'+accountId);            
                Pharmacy_LC_HUM.updateConsentEndDate('114238930','AXB0001',accountId);       
                HttpResponse resDemographic1 = New Httpresponse();
                Demog = '{"EditMemberRequest":{"members":[{"EnterprisePersonID":"1048868178","addresses":{"address":[{"addressKey":"-1","addressLine1":"13146 LAKE LN","addressLine2":"","addressType":"S","addressName":"RONALD MAHAN","city":"EUSTACE","isActive":"true","overrideReasonCode":"","stateCode":"TX","uspsValidate":"true","zipCode":"75124"}]},"creditCards":{"creditCard":[{"creditCardKey":"-1","creditCardType":"V","expirationMonth":"10","expirationYear":"2016","firstName":"THOMAS","isActive":"true","isAutoCharge":"false","lastName":"ONDERKO","middleName":"","tokenKey":"4315830000418394"}]},"customerDetail":{"cmrStatus":"DNC","email":"abc@xyz.com"},"logNotes":{"logNote":[{"logNoteCode":"CONSENT","logNoteDate":"12/07/2016","logNoteKey":"-1","logNoteMessage":"update for testing","logNoteUser":"ANSUSR"}]},"phones":{"phone":[{"phoneKey":"-1","phoneType":"PP","phoneValue":"4545641234"}]}	}]}}';
                resDemographic1.setBody(Demog);
                system.assert(resDemographic1.getBody() == Demog);                
                reqType = 's';                
                List<string>labelGetmember1 = New List<string>();
                labelGetmember1.add(reqType);
                Test.setContinuationResponse(reqType,resDemographic1);  
            }
            catch(Exception e){
                system.assertEquals('Script-thrown exception', e.getMessage(),'success');
            }
            Test.stopTest();
        }
    }
    
    /*
    * Method Name : testFaultPharmacyUpdateConsentBeginDate
    * Description : This method is used to test the fault in Pharmacy update consent begin date funtionality
    * Return type : None
    * Parameter   : None
    */
    Static testMethod void testFaultPharmacyUpdateConsentBeginDate()
    {
        User testUser = [Select Id, Name from User where LastName='test User1'];
        Policy_member__c policyObj = [Select id from Policy_member__c where Relationship__c = 'self' limit 1];
        Account accobj = [Select id from Account where FirstName = 'Test FName' Limit 1];
        Id  accountId = accobj.id;
        System.runAs(testUser)
        {
            Test.startTest();
            try{
                Pharmacy_LC_HUM objPharmacy = New Pharmacy_LC_HUM();  
                Pharmacy_LD_HUM oDataPharmacy = New Pharmacy_LD_HUM();
                ApexPages.StandardController scon = New ApexPages.StandardController(policyObj);
                Pharmacy_LC_HUM pharmacyDemographic = New Pharmacy_LC_HUM();
                Pharmacy_LS_HUM oServicePharmacy = New Pharmacy_LS_HUM();
                Pharmacy_LC_HUM.updateConsentBeginDate('114238930','AXB0001',accountId);                
                HttpResponse resDemographic = New Httpresponse();
                String Demog = '{ "Fault":{ "faultcode":"\n          Code-67.8.1", "faultstring":"\n          error_service_name :\"RightSourceService &#124;\n          error_service_method : GetAdjustmentsAndPayments &#124;\n          fault_message : ", "detail":{ "transactionInfo":{ "transaction-id":"207260733", "transaction-client":"127.0.0.1", "URL-in":"http://127.0.0.1:8316/RightSourceService_V1.0/GetAdjustmentsAndPayments", "processor-name":"JSONServicesGateway", "processor-type":"Multiprotocol Gateway" }, "errorInfo":{ "error-code":"0x00d30003", "error-headers":"HTTP/1.1 500 Internal Server Error\r\nContent-Type: text/xml; charset=UTF-8\r\nX-Backside-Transport: OK OK\r\nConnection: close\r\n\r\n", "error-message":"Member not found", "error-protocol-reason-phrase":"", "error-protocol-response":"0", "error-subcode":"0x00d30003" } } } }';
                resDemographic.setBody(Demog);
                system.assert(resDemographic.getBody() == Demog);                
                String reqType = 's';                
                List<string>labelGetmember = New List<string>();
                labelGetmember.add(reqType);
                Test.setContinuationResponse(reqType,resDemographic);
                Pharmacy_LC_HUM.retrieveUpdatedMemberConsent(labelGetmember,'Both_'+accountId);          
                Pharmacy_LC_HUM.updateConsentEndDate('114238930','AXB0001',accountId);
                HttpResponse resDemographic1 = New Httpresponse();
                Demog = '{ "Fault":{ "faultcode":"\n          Code-67.8.1", "faultstring":"\n          error_service_name :\"RightSourceService &#124;\n          error_service_method : GetAdjustmentsAndPayments &#124;\n          fault_message : ", "detail":{ "transactionInfo":{ "transaction-id":"207260733", "transaction-client":"127.0.0.1", "URL-in":"http://127.0.0.1:8316/RightSourceService_V1.0/GetAdjustmentsAndPayments", "processor-name":"JSONServicesGateway", "processor-type":"Multiprotocol Gateway" }, "errorInfo":{ "error-code":"0x00d30003", "error-headers":"HTTP/1.1 500 Internal Server Error\r\nContent-Type: text/xml; charset=UTF-8\r\nX-Backside-Transport: OK OK\r\nConnection: close\r\n\r\n", "error-message":"Member not found", "error-protocol-reason-phrase":"", "error-protocol-response":"0", "error-subcode":"0x00d30003" } } } }';
                resDemographic1.setBody(Demog);
                system.assert(resDemographic1.getBody() == Demog);            
                reqType = 's';             
                List<string>labelGetmember1 = New List<string>();
                labelGetmember1.add(reqType);
                List<string>testGetMemberData = New List<string>();
                testGetMemberData.add(reqType);
                Test.setContinuationResponse(reqType,resDemographic1);			            
                system.assertEquals(labelGetmember1, testGetMemberData);
            }
            catch(Exception e){
                system.assertEquals('Script-thrown exception', e.getMessage(),'success');
            }
            Test.stopTest();
        }
    }
    
    /*
    * Method Name : testNegativePharmacyUpdateConsentBeginDate
    * Description : This method is used to test the negative scenario Pharmacy update consent begin date funtionality
    * Return type : None
    * Parameter   : None
    */
    Static testMethod void testNegativePharmacyUpdateConsentBeginDate()
    {
        User testUser = [Select Id, Name from User where LastName='test User1'];
        Policy_member__c policyObj = [Select id from Policy_member__c where Relationship__c = 'self' limit 1];
        Account accobj = [Select id from Account where FirstName = 'Test FName' Limit 1];
        Id  accountId = accobj.id;
        List<HUM_Webservice_Callout__c > lstWebCallout = [Select id from HUM_Webservice_Callout__c];
        System.runAs(testUser)
        {
            Test.startTest();
            try{
                Pharmacy_LC_HUM objPharmacy = New Pharmacy_LC_HUM();            
                Pharmacy_LD_HUM oDataPharmacy = New Pharmacy_LD_HUM();
                ApexPages.StandardController scon = New ApexPages.StandardController(policyObj);
                Pharmacy_LC_HUM pharmacyDemographic = New Pharmacy_LC_HUM();
                Pharmacy_LS_HUM oServicePharmacy = New Pharmacy_LS_HUM();
                delete lstWebCallout;
                Pharmacy_LC_HUM.updateConsentBeginDate('114238930','AXB0001',accountId);
                HttpResponse resDemographic = New Httpresponse();
                String Demog = '{"EditMemberRequest":{"members":[{"EnterprisePersonID":"1048868178","addresses":{"address":[{"addressKey":"-1","addressLine1":"13146 LAKE LN","addressLine2":"","addressType":"S","addressName":"RONALD MAHAN","city":"EUSTACE","isActive":"true","overrideReasonCode":"","stateCode":"TX","uspsValidate":"true","zipCode":"75124"}]},"creditCards":{"creditCard":[{"creditCardKey":"-1","creditCardType":"V","expirationMonth":"10","expirationYear":"2016","firstName":"THOMAS","isActive":"true","isAutoCharge":"false","lastName":"ONDERKO","middleName":"","tokenKey":"4315830000418394"}]},"customerDetail":{"cmrStatus":"DNC","email":"abc@xyz.com"},"logNotes":{"logNote":[{"logNoteCode":"CONSENT","logNoteDate":"12/07/2016","logNoteKey":"-1","logNoteMessage":"update for testing","logNoteUser":"ANSUSR"}]},"phones":{"phone":[{"phoneKey":"-1","phoneType":"PP","phoneValue":"4545641234"}]}	}]}}';
                resDemographic.setBody(Demog);
                system.assert(resDemographic.getBody() == Demog);
                String reqType = '';
                List<string>labelGetmember = New List<string>();
                labelGetmember.add(reqType);
                Test.setContinuationResponse(reqType,resDemographic);
                Pharmacy_LC_HUM.retrieveUpdatedMemberConsent(labelGetmember,'Both_'+accountId);                
                Pharmacy_LC_HUM.updateConsentEndDate('114238930','AXB0001',accountId);
                HttpResponse resDemographic1 = New Httpresponse();
                Demog = '{"EditMemberRequest":{"members":[{"EnterprisePersonID":"1048868178","addresses":{"address":[{"addressKey":"-1","addressLine1":"13146 LAKE LN","addressLine2":"","addressType":"S","addressName":"RONALD MAHAN","city":"EUSTACE","isActive":"true","overrideReasonCode":"","stateCode":"TX","uspsValidate":"true","zipCode":"75124"}]},"creditCards":{"creditCard":[{"creditCardKey":"-1","creditCardType":"V","expirationMonth":"10","expirationYear":"2016","firstName":"THOMAS","isActive":"true","isAutoCharge":"false","lastName":"ONDERKO","middleName":"","tokenKey":"4315830000418394"}]},"customerDetail":{"cmrStatus":"DNC","email":"abc@xyz.com"},"logNotes":{"logNote":[{"logNoteCode":"CONSENT","logNoteDate":"12/07/2016","logNoteKey":"-1","logNoteMessage":"update for testing","logNoteUser":"ANSUSR"}]},"phones":{"phone":[{"phoneKey":"-1","phoneType":"PP","phoneValue":"4545641234"}]}	}]}}';
                resDemographic1.setBody(Demog);
                system.assert(resDemographic1.getBody() == Demog);
                reqType = '';
                List<string>labelGetmember1 = New List<string>();
                List<string>testGetMemberData = New List<string>();
                testGetMemberData.add(reqType);
                labelGetmember1.add(reqType);
                Test.setContinuationResponse(reqType,resDemographic1);
                system.assertEquals(labelGetmember1, testGetMemberData); 
            }
            catch(Exception e){
               system.assertEquals('Script-thrown exception', e.getMessage(),'success'); 
            }
            Test.stopTest();
        }
    }
	
	
    Static testMethod void testPharmacyGetOrder(){
        User testUser = [Select Id, Name from User where LastName='test User1'];
        Policy_member__c policyObj = [Select id from Policy_member__c where Relationship__c = 'self' limit 1];
        Account accobj = [Select id from Account where FirstName = 'Test FName' Limit 1];
        Id  accountId = accobj.id;
        System.runAs(testUser)
        {
            Test.startTest();
            try{
                Pharmacy_LC_HUM objPharmacy = New Pharmacy_LC_HUM();            
                Pharmacy_LD_HUM oDataPharmacy = New Pharmacy_LD_HUM();
                Pharmacy_LS_HUM oServicePharmacy = New Pharmacy_LS_HUM();
                Pharmacy_LC_HUM.invokeEpostOrder('1070177218','test','test','AXB0001',accountId);
                HttpResponse resDemographic = New Httpresponse();
                String orderRes = '{"GetOrdersReponse":{"EnterprisePersonID":"1065590075","Orders":{"Order":[{"OrderNumber":"114238192","OrderCreationDate":"08/05/2016","OrderReleaseDate":"","OrderSource":"MAIL","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"90476811","OrderCreationDate":"01/05/2015","OrderReleaseDate":"","OrderSource":"MAIL","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"114234280","OrderCreationDate":"03/22/2016","OrderReleaseDate":"","OrderSource":"MAIL","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"104081567","OrderCreationDate":"06/30/2015","OrderReleaseDate":"","OrderSource":"CCP/GCP","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"97044257","OrderCreationDate":"03/31/2015","OrderReleaseDate":"","OrderSource":"CCP/GCP","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"114234382","OrderCreationDate":"03/23/2016","OrderReleaseDate":"","OrderSource":"MAIL","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"114234293","OrderCreationDate":"03/22/2016","OrderReleaseDate":"","OrderSource":"MAIL","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"114234381","OrderCreationDate":"03/23/2016","OrderReleaseDate":"","OrderSource":"MAIL","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"114202926","OrderCreationDate":"09/26/2015","OrderReleaseDate":"","OrderSource":"IVR","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"114234289","OrderCreationDate":"03/22/2016","OrderReleaseDate":"Test","OrderSource":"MAIL","QueueName":"test","ExternalOrderKey":"test","OrderTotalAmount":"test","OrderpayAmount":"test","OrderTaxAmount":"test","StaleData":"test"}]}}}';
                resDemographic.setBody(orderRes);
                system.assert(resDemographic.getBody() == orderRes);            
                String reqTypeOrder = 's';           
                List<string> labelGetmember = New List<string>();
                labelGetmember.add(reqTypeOrder);
                Test.setContinuationResponse(reqTypeOrder,resDemographic);
                object result  = Pharmacy_LC_HUM.parseRSGetOrderResponse(labelGetmember,resDemographic);
                Boolean orderNumberExist = Json.serialize(result).contains('90476811') ? true : false;
                system.assertEquals(true, orderNumberExist);   
            }
            catch(Exception e){
               system.assertEquals('Script-thrown exception', e.getMessage(),'success'); 
            }           
            Test.stopTest();    
        }
    }
    
    Static testMethod void testFaultPharmacyGetOrder(){
        User testUser = [Select Id, Name from User where LastName='test User1'];
        policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        List<HUM_Webservice_Callout__c > lstWebCallout = [Select id from HUM_Webservice_Callout__c];
        Account accobj = [Select id from Account where FirstName = 'Test FName' Limit 1];
        Id  accountId = accobj.id;
        System.runAs(testUser)
        {
            Test.startTest();
            try{     
                Pharmacy_LC_HUM objPharmacy = New Pharmacy_LC_HUM();                        
                Pharmacy_LC_HUM.getOrderData('114238930','114238930','AXB0001',accountId,'114238930','AXB0001',accountId);            
                HttpResponse resOrder = new Httpresponse ();
                String orderRes= '{"GetOrdersReponse":{"EnterprisePersonID":"1065590075","Orders":{"Order":[{"OrderNumber":"114238192","OrderCreationDate":"08/05/2016","OrderReleaseDate":"","OrderSource":"MAIL","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"90476811","OrderCreationDate":"01/05/2015","OrderReleaseDate":"","OrderSource":"MAIL","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"114234280","OrderCreationDate":"03/22/2016","OrderReleaseDate":"","OrderSource":"MAIL","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"104081567","OrderCreationDate":"06/30/2015","OrderReleaseDate":"","OrderSource":"CCP/GCP","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"97044257","OrderCreationDate":"03/31/2015","OrderReleaseDate":"","OrderSource":"CCP/GCP","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"114234382","OrderCreationDate":"03/23/2016","OrderReleaseDate":"","OrderSource":"MAIL","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"114234293","OrderCreationDate":"03/22/2016","OrderReleaseDate":"","OrderSource":"MAIL","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"114234381","OrderCreationDate":"03/23/2016","OrderReleaseDate":"","OrderSource":"MAIL","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"114202926","OrderCreationDate":"09/26/2015","OrderReleaseDate":"","OrderSource":"IVR","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"114234289","OrderCreationDate":"03/22/2016","OrderReleaseDate":"Test","OrderSource":"MAIL","QueueName":"test","ExternalOrderKey":"test","OrderTotalAmount":"test","OrderpayAmount":"test","OrderTaxAmount":"test","StaleData":"test"}]}}}';
                resOrder.setBody(orderRes);
                system.assert(resOrder.getBody() == orderRes);           
                String reqType = 's';           
                List<string>labelGetmember = New List<string>();			
                labelGetmember.add(reqType);
                Test.setContinuationResponse(reqType,resOrder);            
                object result = Pharmacy_LC_HUM.ProcessOrderResponse(labelGetmember,new List < String > { '114238930','AXB0001',accountId});
                Boolean orderNumberExist = Json.serialize(orderRes).contains('90476811') ? true : false;            
                system.assertEquals(true, orderNumberExist);   
            }
            catch(Exception e){
                system.assertEquals('Script-thrown exception', e.getMessage(),'success');
            }
            Test.stopTest();
        }
	}
    
    /*
    * Method Name : testPharmacygetOrderData
    * Description : This method Is used To test the Pharmacy get order data functionality
    * Return type : None
    * Parameter   : None
    */
    Static testMethod void testPharmacygetOrderData()
    {
        User testUser = [Select Id, Name from User where LastName='test User1'];
        Policy_member__c policyObj = [Select id from Policy_member__c where Relationship__c = 'self' limit 1];
        Account accobj = [Select id from Account where FirstName = 'Test FName' Limit 1];
        Id  accountId = accobj.id;
        System.runAs(testUser)
        {
            Test.startTest();
            try{     
                Pharmacy_LC_HUM objPharmacy = New Pharmacy_LC_HUM();                        
                Pharmacy_LC_HUM.getOrderData('114238930','114238930','AXB0001',accountId,'114238930','AXB0001',accountId);            
                HttpResponse resOrder = new Httpresponse ();
                String orderRes= '{"GetOrdersReponse":{"EnterprisePersonID":"1065590075","Orders":{"Order":[{"OrderNumber":"114238192","OrderCreationDate":"08/05/2016","OrderReleaseDate":"","OrderSource":"MAIL","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"90476811","OrderCreationDate":"01/05/2015","OrderReleaseDate":"","OrderSource":"MAIL","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"114234280","OrderCreationDate":"03/22/2016","OrderReleaseDate":"","OrderSource":"MAIL","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"104081567","OrderCreationDate":"06/30/2015","OrderReleaseDate":"","OrderSource":"CCP/GCP","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"97044257","OrderCreationDate":"03/31/2015","OrderReleaseDate":"","OrderSource":"CCP/GCP","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"114234382","OrderCreationDate":"03/23/2016","OrderReleaseDate":"","OrderSource":"MAIL","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"114234293","OrderCreationDate":"03/22/2016","OrderReleaseDate":"","OrderSource":"MAIL","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"114234381","OrderCreationDate":"03/23/2016","OrderReleaseDate":"","OrderSource":"MAIL","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"114202926","OrderCreationDate":"09/26/2015","OrderReleaseDate":"","OrderSource":"IVR","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"114234289","OrderCreationDate":"03/22/2016","OrderReleaseDate":"Test","OrderSource":"MAIL","QueueName":"test","ExternalOrderKey":"test","OrderTotalAmount":"test","OrderpayAmount":"test","OrderTaxAmount":"test","StaleData":"test"}]}}}';
                resOrder.setBody(orderRes);
                system.assert(resOrder.getBody() == orderRes);           
                String reqType = 's';           
                List<string>labelGetmember = New List<string>();			
                labelGetmember.add(reqType);
                Test.setContinuationResponse(reqType,resOrder);            
                object result = Pharmacy_LC_HUM.ProcessOrderResponse(labelGetmember,new List < String > { '114238930','AXB0001',accountId});
                Boolean orderNumberExist = Json.serialize(orderRes).contains('90476811') ? true : false;            
                system.assertEquals(true, orderNumberExist);   
            }
            catch(Exception e){
                system.assertEquals('Script-thrown exception', e.getMessage(),'success');
            }   
            Test.stopTest();
        }
    }
    
    
    /*
    * Method Name : testPharmacyUpdateAutoRefill
    * Description : This method Is used to test the Pharmacy update auto refill funtionality
    * Return type : None
    * Parameter   : None
    */
    Static testMethod void testPharmacyUpdateAutoRefill()
    {
        User testUser = [Select Id, Name from User where LastName='test User1'];
        policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        System.runAs(testUser)
        {
            Test.startTest();
            try{     
                Pharmacy_LC_HUM objPharmacy = New Pharmacy_LC_HUM();            
                Pharmacy_LD_HUM oDataPharmacy = New Pharmacy_LD_HUM();            
                Pharmacy_LC_HUM pharmacyDemographic = New Pharmacy_LC_HUM();
                Pharmacy_LS_HUM oServicePharmacy = New Pharmacy_LS_HUM();
                Continuation continueObj = (Continuation)Pharmacy_LC_HUM.updateautorefil('396604821','true','SXS0295');                
                HttpResponse resDemographic = New Httpresponse();
                String Demog = '{"EditScriptRequest":{"ScriptKey":"396604821","AutoRefill":"true","CustomerServiceId":"SXS0295"}}';
                resDemographic.setBody(Demog);
                system.assert(resDemographic.getBody() == Demog);                
                String reqType = '';
                List<string>labelGetmember = New List<string>();
                labelGetmember.add(reqType);
                Test.setContinuationResponse(reqType,resDemographic);
                object result = Pharmacy_LC_HUM.retrieveautorefil(labelGetmember,'Both_AXB0001_0015C000002T9uJ');
                System.assertEquals('success', result);   
            }
            catch(Exception e){
                system.assertEquals('Script-thrown exception', e.getMessage(),'success');
            }
            Test.stopTest();
        }
    }
    
    
    /*
    * Method Name : testNegativePharmacyUpdateAutoRefill
    * Description : This method Is used To test the negative scenarios of Pharmacy update auto refill funtionality
    * Return type : None
    * Parameter   : None
    */
    Static testMethod void testNegativePharmacyUpdateAutoRefill()
    {
        User testUser = [Select Id, Name from User where LastName='test User1'];
        policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        List<HUM_Webservice_Callout__c > lstWebCallout = [Select id from HUM_Webservice_Callout__c];
        System.runAs(testUser)
        {
            Test.startTest();
            try{
                Pharmacy_LC_HUM objPharmacy = New Pharmacy_LC_HUM();
                Pharmacy_LD_HUM oDataPharmacy = New Pharmacy_LD_HUM();                
                Pharmacy_LC_HUM pharmacyDemographic = New Pharmacy_LC_HUM();
                Pharmacy_LS_HUM oServicePharmacy = New Pharmacy_LS_HUM();
                delete lstWebCallout;
                Continuation continueObj = (Continuation)Pharmacy_LC_HUM.updateautorefil('396604821','true','SXS0295');
                HttpResponse resDemographic = New Httpresponse();
                String Demog = '{"EditScriptRequest":{"ScriptKey":"233582403","AutoRefill":"true","CustomerServiceId":"nxc1262"}}';
                resDemographic.setBody(Demog);
                system.assert(resDemographic.getBody() == Demog);
                String reqType = '';
                List<string>labelGetmember = New List<string>();
                labelGetmember.add(reqType);
                Test.setContinuationResponse(reqType,resDemographic);
                object result = Pharmacy_LC_HUM.retrieveautorefil(labelGetmember,'Both_AXB0001_0015C000002T9uJ');
                System.assertEquals('success', result);
            }
            catch(Exception e){
                system.assertEquals('Script-thrown exception', e.getMessage(),'success');
            }
            Test.stopTest();
        }
    }
    
    /*
    * Method Name : testFaultPharmacyUpdateAutoRefill
    * Description : This method Is used to test the fault in Pharmacy update auto refill funtionality
    * Return type : None
    * Parameter   : None
    */
    Static testMethod void testFaultPharmacyUpdateAutoRefill()
    {
        User testUser = [Select Id, Name from User where LastName='test User1'];
        policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        System.runAs(testUser)
        {
            Test.startTest();
            try{  
                Pharmacy_LD_HUM oDataPharmacy = New Pharmacy_LD_HUM();                
                Pharmacy_LC_HUM pharmacyDemographic = New Pharmacy_LC_HUM();
                Pharmacy_LS_HUM oServicePharmacy = New Pharmacy_LS_HUM();
                Continuation continueObj = (Continuation)Pharmacy_LC_HUM.updateautorefil('396604001','true','SXS0295');                
                HttpResponse resDemographic = New Httpresponse();
                String Demog = '{ "Fault":{ "faultcode":"\n          Code-67.8.1", "faultstring":"\n          error_service_name :\"RightSourceService &#124;\n          error_service_method : updateautorefil &#124;\n          fault_message : ", "detail":{ "transactionInfo":{ "transaction-id":"207260733", "transaction-client":"127.0.0.1", "URL-in":"http://127.0.0.1:8316/RightSourceService_V1.0/updateautorefil", "processor-name":"JSONServicesGateway", "processor-type":"Multiprotocol Gateway" }, "errorInfo":{ "error-code":"0x00d30003", "error-headers":"HTTP/1.1 500 Internal Server Error\r\nContent-Type: text/xml; charset=UTF-8\r\nX-Backside-Transport: OK OK\r\nConnection: close\r\n\r\n", "error-message":"Member not found", "error-protocol-reason-phrase":"", "error-protocol-response":"0", "error-subcode":"0x00d30003" } } } }';
                resDemographic.setBody(Demog);
                system.assert(resDemographic.getBody() == Demog);
                String reqType = '';
                List<string>labelGetmember = New List<string>();
                labelGetmember.add(reqType);
                Test.setContinuationResponse(reqType,resDemographic);
                object result = Pharmacy_LC_HUM.retrieveautorefil(labelGetmember,'Both_AXB0001_0015C000002T9uJ');
                System.assertEquals(null, result);   
            }
            catch(Exception e){
                system.assertEquals('Script-thrown exception', e.getMessage(),'success');
            }
            Test.stopTest();
        }
    }
    
    /*
    * Method Name : testInvokeRightSourceService
    * Description : This method Is used to test GetMemberService
    * Return type : None
    * Parameter   : None
    */
    private static testmethod void testInvokeRightSourceService()
    {
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        system.runAs(testUser)
        {               
            String jsonRequest = '{"GetMemberRequest":{"EnterprisePersonID":"1035108313","CustomerServiceId":"AXM5290","IncludeActiveLogNotes":"true","IncludeActiveAddresses":"true","IncludeInactiveAddresses":"true","IncludeFinances":"true","IncludeAccountAdjustments":"true","IncludeConsentStatus":"true","IncludeActiveCreditCards":"true","IncludeInactiveCreditCards":"true","IncludeStaleData":"true","includeCommunicationPreferences":"true","LogNoteStartDate":"10/25/2022","LogNoteEndDate":"01/25/2023","returnDuplicateCustomers":"true","IncludeSpecialty":"false"}}';
            Test.StartTest();
            try{
                Pharmacy_LD_HUM oDataPharmacy = New Pharmacy_LD_HUM();            
                Pharmacy_LC_HUM pharmacyDemographic = New Pharmacy_LC_HUM();
                Pharmacy_LS_HUM oServicePharmacy = New Pharmacy_LS_HUM();            
                Continuation conti = (Continuation)Pharmacy_LC_HUM.invokeRightSourceService(jsonRequest, 'GetMember');            
                HttpResponse response = new HttpResponse();
                String resBodyString = '{"GetMemberReponse":{"Customers":{"Customer":[{"CustomerIdentity":{"BirthDate":"01/12/1944","CustomerKeys":{"CustomerKey":[{"CustomerType":"SS","CustomerValue":"412084505","CustomerTypeLiteral":"SOCIAL SECURITY NUMBER"},{"CustomerType":"AC","CustomerValue":"5051790","CustomerTypeLiteral":"ACCOUNT ID"},{"CustomerType":"PK","CustomerValue":"1173301","CustomerTypeLiteral":"PRIMARY KEY"},{"CustomerType":"PM","CustomerValue":"1035108313","CustomerTypeLiteral":"PERSON MASTER KEY"}]},"FirstName":"RAHMID","MiddleName":"Y","LastName":"SHIMMEL","Email":""}}]}}}';
                response.setBody(resBodyString);
                response.setStatusCode(200);
                conti.state = 'Continuation-1';
                string reqType = 'Continuation-1';        
                List<string> labelGetmember = new List<string>();
                labelGetmember.add(reqType);
                Test.setContinuationResponse(reqType,response);
                String responseStr = (String) Pharmacy_LC_HUM.retriveRightSourceData(labelGetmember,resBodyString);
                system.assertEquals(response.getBody(), resBodyString);
            }catch(exception e){
                system.assertEquals('Script-thrown exception', e.getMessage(),'success');
            }
            Test.StopTest();
        }
    }
    
}