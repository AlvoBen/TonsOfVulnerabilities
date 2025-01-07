/*******************************************************************************************************************************
Apex Class Name :  Pharmacy_T_HUM.cls
Version: 1.0
Created On      : Dec 27,2016
Function        : Class contains test methods Pharmacy_C_HUM Class.

Modification Log :  
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Rajesh Keswani               57637                12/20/2016                 Original Version
* Rajesh Chandgothia           55992                12/29/2016                 Demographic functionality (REQ 291083)
* Rajesh Keswani               58175                01/22/2017                 Implementation of REQ - 296652 - Order Summary details from getOrder EPostRX
* Rajesh Keswani                                    02/20/2017                 Implementation of REQ - 291082 - Edit Member from EPostRX
* Rajesh Chandgothia                                02/14/2017                 Member prescription Req# REQ - 296752
* Rajesh Keswani               74554  	            03/03/2017                 REQ - 299426 Add And Edit address section
* Rajesh Chandgothia           75938                03/01/2017                 Edit Order Req# REQ - 296651
* Avinash Choubey                                   04/06/2017                 5/11 merge req 301370
* Vishal Verma                 90076                04/13/2017                 Test Method for Helper Class
* Sharan Shanmugam									07/27/2017				   REQ - 323542 : Exception handling
* Vijay Gurusamy		       272865		        08/03/2018				   REQ - 367015 : Added test methods For HP Insurance users, PDP pilot users And existing code related To fault code processing which was Not covered In testing.
* Joel George										10/31/2018				   REQ - 367769
* AshokKumar Nutalapati        330644               01/25/2019                 REQ - 383716 & 383717: Ability to Read Rx#, Written&Dispensed Drug Included in Order from Humana Pharmacy Page (RxRF)
* Deepankar Binjola            42502                08/28/2019                 REQ - 408254 Remove PharmacyCreatEditOrder_CMP_HUM component
* Lakshmi Madduri									10/30/2019					REQ-410211
* Pallavi Shewale                                   06/08/2021			USER STORY 1491784: T1PRJ0003815- CR 01 - Edit/Cancel/Release Links for Finance	
* AshokKumar Nutalapati   							07/12/2021           	   REQ - 1907149 T1PRJ0002517 - MF 2 - Display Icon When Pharmacy Authorization on File (RxRF) (ID# 131)
* Ashok Kumar Nutalapati            				09/30/2021            	   REQ - 2593758 T1PRJ0002517 - MF 2 - New case button, create case, & open new case in a subtab (RXRF) (ID# 35b)
* Lavanya Rao										10/14/2021				   User Story 2731234: T1PRJ0002606- 2021 MF6 - Swat-SF-Tech-HumConstant and Global Constant Fix
* Thaurn Madishetti                                         04/14/2023                 US-3837439: T1PRJ0714381- MF1: (PLT) 100 Day - CRM Supply Eligibility Indicator (Classic)
********************************************************************************************************************************/
@isTest
Private Class Pharmacy_T_HUM 
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
		
		User objUserHumanaSpecialist = HUMTestDataHelperTest.createTestUser('User2','Humana Pharmacy Specialist');   
        insert objUserHumanaSpecialist;
        
        User objUserHumanaSupervisor = HUMTestDataHelperTest.createTestUser('User3','Humana Pharmacy Specialist');   
        insert objUserHumanaSupervisor;
        
        List<PermissionSet> humanaSupervisorPS = [Select id , Name from PermissionSet where name ='CRMS_300_Humana_Pharmacy_Supervisor'];
        if(humanaSupervisorPS != null	)
        {
        	insert new PermissionSetAssignment(assigneeId = objUserHumanaSupervisor.id , PermissionSetId =humanaSupervisorPS[0].id);
        }
		User objUserHumanaSpecialistWithInsurance = HUMTestDataHelperTest.createTestUser('User4', 'Humana Pharmacy Specialist');
		insert objUserHumanaSpecialistWithInsurance;

		List<PermissionSet> hpInsuranceDataPS = [Select id, Name from PermissionSet where name = 'CRMS_301_HPInsuranceData'];
		if (hpInsuranceDataPS != null)
		{
			insert new PermissionSetAssignment(assigneeId = objUserHumanaSpecialistWithInsurance.id, PermissionSetId = hpInsuranceDataPS[0].id);
		}

		User objUserPDPPilot = HUMTestDataHelperTest.createTestUser('User5', 'Humana Pharmacy Specialist');
		insert objUserPDPPilot;

        List<PermissionSet> hpPDPPS = [Select id, Name from PermissionSet where name = 'CRMS_205_CCSPDPPharmacyPilot'];
        if (hpInsuranceDataPS != null)
        {
            insert new PermissionSetAssignment(assigneeId = objUserPDPPilot.id, PermissionSetId = hpPDPPS[0].id);
        }
        
        User objUserHumanaSpecialistWithHPTraditionalInsurance = HUMTestDataHelperTest.createTestUser('User6', 'Humana Pharmacy Specialist');
        insert objUserHumanaSpecialistWithHPTraditionalInsurance;

        List<PermissionSet> hpTraditonalInsuranceDataPS = [Select id, Name from PermissionSet where name = 'CRMS_302_HPTraditionalInsuranceData'];
        if (hpTraditonalInsuranceDataPS != null)
        {
            insert new PermissionSetAssignment(assigneeId = objUserHumanaSpecialistWithHPTraditionalInsurance.id, PermissionSetId = hpTraditonalInsuranceDataPS[0].id);
        }

        system.runAs(objUser)
        {
			Pharmacy_Supervisor_HUM__c supervisorCS = New Pharmacy_Supervisor_HUM__c(Name = 'CRMS_300_Humana_Pharmacy_Supervisor');
	        insert supervisorCS;
			
		    HUM_Webservice_Callout__c RighSourceServiceDetails = New HUM_Webservice_Callout__c(Name = 'RighSourceService_GetMember',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c ='https://int-crmservicews.humana.com/RighSourceService_GetMember/' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
			insert RighSourceServiceDetails;
			HUM_Webservice_Callout__c RighSourceOrderServiceDetails = New HUM_Webservice_Callout__c(Name = 'RighSourceService_GetOrders',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c ='https://int-crmservicews.humana.com/RighSourceService_GetMember/' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
			insert RighSourceOrderServiceDetails;
			HUM_Webservice_Callout__c RighSourceEditServiceDetails = New HUM_Webservice_Callout__c(Name = 'RighSourceService_EditMember',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c ='https://int-crmservicews.humana.com/RighSourceService_GetMember/' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
			insert RighSourceEditServiceDetails;
			HUM_Webservice_Callout__c RighSourcePresServiceDetails = New HUM_Webservice_Callout__c(Name = 'RighSourceService_GetScripts',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c ='https://int-crmservicews.humana.com/RighSourceService_GetMember/' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
			insert RighSourcePresServiceDetails;
			HUM_Webservice_Callout__c RighSourceEASServiceDetails = New HUM_Webservice_Callout__c(Name = 'Enterprise_Address_Service',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c ='https://int-crmservicews.humana.com/RighSourceService_GetMember/' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
			insert RighSourceEASServiceDetails;
			HUM_Webservice_Callout__c RighSourceOrderDetailServiceDetails = New HUM_Webservice_Callout__c(Name = 'RighSourceService_GetOrderDetails',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c ='https://int-crmservicews.humana.com/RighSourceService_GetMember/' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
			insert RighSourceOrderDetailServiceDetails;
			HUM_Webservice_Callout__c RighSourceCreateEditOrderServiceDetails = New HUM_Webservice_Callout__c(Name = 'RighSourceService_CreateAndEditOrder',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c ='https://int-crmservicews.humana.com/RighSourceService_GetMember/' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
			insert RighSourceCreateEditOrderServiceDetails ;
			HUM_Webservice_Callout__c RighSourceEditScriptDetails = New HUM_Webservice_Callout__c(Name = 'RighSourceService_EditScript',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c ='https://int-crmservicews.humana.com/RighSourceService_GetMember/' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
			insert RighSourceEditScriptDetails;
            HUM_Webservice_Callout__c IsMemElig100DayPrescrip = New HUM_Webservice_Callout__c(Name = 'MemElig100DayPrescrip',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c ='https://int-crmservicews.humana.com/PreSaleService/IsMemElig100DayPrescrip' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
			insert IsMemElig100DayPrescrip;
			system.assertEquals(objUser.lastname == 'test User1',true);
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
            oMedicalPolicy.Coverage_Plan_Effective_Date__c='01/01/2023';         
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
	* Method Name : testMedicalPharmacyFin
	* Description : This method Is used To test the Pharmacy Financial funtionality
	* Return type : None
	* Parameter   : None
	*/
    Static testMethod void testMedicalPharmacyFin()
    {
        User testUser = [Select Id, Name from User where LastName='test User1'];
        policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        List<HUM_Webservice_Callout__c > lstWebCallout = [Select id from HUM_Webservice_Callout__c];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.Pharmacy_VF_HUM);
            ApexPages.currentPage().getParameters().put('Id',policyObj.Id);
            system.assert(policyObj.Id<> null);
            Pharmacy_C_HUM objPharmFin = New Pharmacy_C_HUM();
            objPharmFin.getThis();
            objPharmFin.getRecords();
            objPharmFin.getRecord();
            objPharmFin.toggleOrderCusPopFlag();
            objPharmFin.cancelModConfirmAddress();
            objPharmFin.showZipCityMsg();
            objPharmFin.openAddPrespopUp();
            objPharmFin.closeAddPrespopUp();
            objPharmFin.cancelSoftMsgPopUp();
            objPharmFin.openSoftMsgPopUp();
            objPharmFin.toggleModConfirmFlag();
            objPharmFin.toggleMemberConsentConfirmPopUpFlag();
            objPharmFin.toggleShowConfirmPopUpForEachOrderFlag();
            objPharmFin.disableRequestConsentForEachOrderPopUp();
            objPharmFin.showMTMPop();
            objPharmFin.hideMTMPop();
            objPharmFin.showModCusPopUp();
            objPharmFin.disableModCusPopUp();
            objPharmFin.showcaptypepopup();
            objPharmFin.bcaptype();
            objPharmFin.showautorefilconfirm();
            objPharmFin.hideautorefilconfirm();
            objPharmFin.hidecaptypepopup();
            //Address validation
            AddressValidationHelper_DTO_HUM addObj = New AddressValidationHelper_DTO_HUM();
            addObj.enterpriseID = 'test';
            addObj.statusValue = 'test';
            addObj.addresstype = 'test';
           	addObj.addressValidatedFlag = 'test';
            addObj.overrideReasonCode = '0';
            EAS_AddressStandardizationRequest_H_HUM.EAS_AddressStandardRequest_H_HUM dto = New EAS_AddressStandardizationRequest_H_HUM.EAS_AddressStandardRequest_H_HUM();
            dto.AddressLine1 = 'test';
            dto.AddressLine2 = 'test';
            dto.City = 'test';
            dto.StateCode = 'test';
            dto.ZipCode = 'test';
            addObj.addressReq = dto;
            
            continuation continueObj = (Continuation)Pharmacy_C_HUM.invokeEASService(addObj);
            continueObj.continuationMethod = 'addressResponse';
            continueObj.state = addObj;
            Httpresponse HttpResEAS = New Httpresponse();
            String reqEASResponse = '{ "StandardizeAddressResponse":{ "SuccessFlag":"Y", "AddressLine1":"9863 GEORGE WASHINGTON MEMORIAL HWY", "City":"GLOUCESTER", "StateCode":"VA", "ZipCode":"23061", "StateID":"51", "ZipCodePlus":"4100", "CountyName":"GLOUCESTER", "CountyID":"073", "Valid":"true", "ChangeIndicator":"Bad" } }';

            HttpResEAS.setBody(reqEASResponse );
            system.assert(HttpResEAS.getBody() == reqEASResponse );
            Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
            String reqEAS = '';
            for(String s : mapDetails.keyset())
            {
                reqEAS = s;
            } 
            List<string>labelGetmember = New List<string>();
            labelGetmember.add(reqEAS );
            Test.setContinuationResponse(reqEAS ,HttpResEAS);
            Pharmacy_C_HUM.addressResponse(labelGetmember,addObj);
            Test.stopTest();
        }
    }
    
    /*
	* Method Name : testMedicalPharmacyFin
	* Description : This method Is used To test the Pharmacy Financial funtionality
	* Return type : None
	* Parameter   : None
	*/
    Static testMethod void testNegativeAddressResponse()
    {
        User testUser = [Select Id, Name from User where LastName='test User1'];
        policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        List<HUM_Webservice_Callout__c > lstWebCallout = [Select id from HUM_Webservice_Callout__c];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.Pharmacy_VF_HUM);
            ApexPages.currentPage().getParameters().put('Id',policyObj.Id);
            system.assert(policyObj.Id<> null);
            Pharmacy_C_HUM objPharmFin = New Pharmacy_C_HUM();
            //Address validation
            AddressValidationHelper_DTO_HUM addObj = New AddressValidationHelper_DTO_HUM();
            addObj.enterpriseID = 'test';
            addObj.statusValue = 'test';
            addObj.addresstype = 'test';
           	addObj.addressValidatedFlag = 'test';
            addObj.overrideReasonCode = '0';
            EAS_AddressStandardizationRequest_H_HUM.EAS_AddressStandardRequest_H_HUM dto = New EAS_AddressStandardizationRequest_H_HUM.EAS_AddressStandardRequest_H_HUM();
            dto.AddressLine1 = 'test';
            dto.AddressLine2 = 'test';
            dto.City = 'test';
            dto.StateCode = 'test';
            dto.ZipCode = 'test';
            addObj.addressReq = dto;
            
            continuation continueObj = (Continuation)Pharmacy_C_HUM.invokeEASService(addObj);
            continueObj.continuationMethod = 'addressResponse';
            continueObj.state = addObj;
            Httpresponse HttpResEAS = New Httpresponse();
            String reqEASResponse = '{ "Fault":{ "faultcode":"\n          Code-67.8.1", "faultstring":"\n          error_service_name :\"RightSourceService &#124;\n          error_service_method : GetAdjustmentsAndPayments &#124;\n          fault_message : ", "detail":{ "transactionInfo":{ "transaction-id":"207260733", "transaction-client":"127.0.0.1", "URL-in":"http://127.0.0.1:8316/RightSourceService_V1.0/GetAdjustmentsAndPayments", "processor-name":"JSONServicesGateway", "processor-type":"Multiprotocol Gateway" }, "errorInfo":{ "error-code":"0x00d30003", "error-headers":"HTTP/1.1 500 Internal Server Error\r\nContent-Type: text/xml; charset=UTF-8\r\nX-Backside-Transport: OK OK\r\nConnection: close\r\n\r\n", "error-message":"Member not found", "error-protocol-reason-phrase":"", "error-protocol-response":"0", "error-subcode":"0x00d30003" } } } }';

            HttpResEAS.setBody(reqEASResponse );
            system.assert(HttpResEAS.getBody() == reqEASResponse );
            Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
            String reqEAS = '';
            for(String s : mapDetails.keyset())
            {
                reqEAS = s;
            } 
            List<string>labelGetmember = New List<string>();
            labelGetmember.add(reqEAS );
            Test.setContinuationResponse(reqEAS ,HttpResEAS);
            Pharmacy_C_HUM.addressResponse(labelGetmember,addObj);
            Test.stopTest();
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
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.Pharmacy_VF_HUM);
            Pharmacy_C_HUM objPharmacy = New Pharmacy_C_HUM();
            objPharmacy.PharmacyDetail ='';
            objPharmacy.refilVariable();
            objPharmacy.lstLogErrors = New List<Error_Log__c>();
            system.assert(objPharmacy.lstLogErrors <> null);
            Pharmacy_D_HUM oDataPharmacy = New Pharmacy_D_HUM();
            ApexPages.StandardController scon = New ApexPages.StandardController(policyObj);
            Pharmacy_C_HUM pharmacyDemographic = New Pharmacy_C_HUM();
            Pharmacy_S_HUM oServicePharmacy = New Pharmacy_S_HUM();
            Continuation continueObj = (Continuation)Pharmacy_C_HUM.invokeGetMemberService('1070177218','AXB0001','0015C000002T9uJ');
            continueObj.continuationMethod = 'parseRSGetMemberResponse';
            continueObj.state = 'Both';
            HttpResponse resDemographic = New Httpresponse();
			String Demog ='{"GetMemberReponse":{ "Customers":{ "Customer":[ { "CustomerIdentity":{ "BirthDate":"10/02/1950", "CustomerKeys":{ "CustomerKey":[ { "CustomerType":"PM", "CustomerValue":"1086943639" }, { "CustomerType":"PC", "CustomerValue":"02" }, { "CustomerType":"PK", "CustomerValue":"6327008" }, { "CustomerType":"AC", "CustomerValue":"8903086" }, { "CustomerType":"NP", "CustomerValue":"107303489" } ] }, "FirstName":"ROYCETTE", "MiddleName":"M", "LastName":"BUTTREY", "Email":"abc@xyz.com" }, "CustomerPreference":{ "CapType":"E", "CapTypeLiteral":"EASY" }, "CustomerDetail":{ "NeedsMemberConsent":"false", "ConsentBeginDate":"", "ConsentEndDate":"", "MemberConsentStatus":"false", "CMRStatus":"", "CMRStatusLiteral":"" }, "Addresses":{ "Address":[ { "AddressKey":"16399980", "AddressType":"B", "AddressTypeLiteral":"BILLING", "AddressLine1":"9080 ABNER ROAD", "AddressLine2":"7469 LYNN AVENUE", "AddressName":"ROYCETTE BUTTREY", "City":"MONROE", "StateCode":"LA", "ZipCode":"71201", "IsActive":"true" }, { "AddressKey":"16399980", "AddressType":"P", "AddressTypeLiteral":"PERMANENT", "AddressLine1":"9080 ABNER ROAD", "AddressLine2":"7469 LYNN AVENUE", "AddressName":"ROYCETTE BUTTREY", "City":"MONROE", "StateCode":"LA", "ZipCode":"71201", "IsActive":"true" }, { "AddressKey":"17229497", "AddressType":"S", "AddressTypeLiteral":"SHIPPING", "AddressLine1":"13146 LAKE LN", "AddressLine2":"", "AddressName":"ROYCETTE BUTTREY", "City":"EUSTACE", "StateCode":"TX", "ZipCode":"75124", "IsActive":"true" }, { "AddressKey":"17229500", "AddressType":"A", "AddressTypeLiteral":"ALTERNATE", "AddressLine1":"PO BOX", "AddressLine2":"508 SPRING VIEW ST SW", "AddressName":"ROYCETTE BUTTREY", "City":"SUAMICO", "StateCode":"WI", "ZipCode":"54313", "IsActive":"true" } ] }, "Phones":{ "Phone":[ { "PhoneKey":"16218856", "PhoneType":"AL", "PhoneValue":"5025801000" }, { "PhoneKey":"19053575", "PhoneType":"CN", "PhoneValue":"5025801600" }, { "PhoneKey":"19053572", "PhoneType":"PP", "PhoneValue":"4545641234" } ] }, "StaleData":"0", "AccountNumber":{ "CustomerValue":"8903086" }, "Finance":{ "CreditLimit":"0.0", "OutstandingBalance":"50.6", "PaymentSchedules":{ "PaymentSchedule":[ { "PaymentDueDate":"", "PaymentMadeDate":"", "PaymentType":"", "SourceApplicationCode":"", "SourceApplicationCodeLiteral":"" } ] } }, "Allergies":{ "Allergy":[ { "HealthKey":"00153", "HealthDescription":"No Known Drug Allergy" } ] }, "CreditCards":{ "CreditCard":[ { "CreditCardKey":"7781077", "CreditCardType":"M", "CreditCardTypeLiteral":"MASTERCARD", "FirstName":"ROYCETTE", "LastName":"BUTTREY", "MiddleName":"L", "ExpirationMonth":"1", "ExpirationYear":"2021", "IsActive":"true", "IsAutoCharge":"false", "CreditCardLast4Digits":"6380", "IsSpendingAccount":"false" }, { "CreditCardKey":"7873701", "CreditCardType":"V", "CreditCardTypeLiteral":"VISA", "FirstName":"THOMAS", "LastName":"ONDERKO", "MiddleName":"", "ExpirationMonth":"10", "ExpirationYear":"2016", "IsActive":"true", "IsAutoCharge":"false", "CreditCardLast4Digits":"8394", "IsSpendingAccount":"false" }, { "CreditCardKey":"7873703", "CreditCardType":"A", "CreditCardTypeLiteral":"AMERICAN EXPRESS", "FirstName":"SONIA", "LastName":"ASENCIORAMOS", "MiddleName":"", "ExpirationMonth":"12", "ExpirationYear":"2022", "IsActive":"true", "IsAutoCharge":"true", "CreditCardLast4Digits":"8431", "IsSpendingAccount":"false" } ] }, "HealthConditions":{ "HealthCondition":[ { "HealthDescription":"Thyroid Disease" } ] }, "LogNotes":{ "LogNote":[  ] }, "Plans":{ "Plan":[ { "PlanEffectiveDate":"01/01/2018", "PlanTerminationDate":"12/31/9999", "PlanType":"COMM", "Payer":"HUMANA INC." } ] }, "communicationPreferences":{ "communicationPreference":[ { "commPrefType":"EMDCL", "commPrefValue":"true" } ] } } ] }, "DuplicateCustomers":{ "CustomerKey":[ { "customerType":"AC", "customerTypeLiteral":"ACCOUNT ID", "customerValue":"8903086" }, { "customerType":"PK", "customerTypeLiteral":"PRIMARY KEY", "customerValue":"6327008" }, { "customerType":"NP", "customerTypeLiteral":"NEW POLICY ID", "customerValue":"107303489" }, { "customerType":"AC", "customerTypeLiteral":"ACCOUNT ID", "customerValue":"8903086" }, { "customerType":"PK", "customerTypeLiteral":"PRIMARY KEY", "customerValue":"6421002" }, { "customerType":"NP", "customerTypeLiteral":"NEW POLICY ID", "customerValue":"107303489" } ] } } }';

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
            Pharmacy_C_HUM.parseRSGetMemberResponse(labelGetmember,continueObj);
            // getOrder
            continueObj = (Continuation)Pharmacy_C_HUM.invokeEpostOrder('1070177218','test','test','AXB0001','0015C000002T9uJ');
            continueObj.continuationMethod = 'parseRSGetOrderResponse';
            continueObj.state = 'Both';
            resDemographic = New Httpresponse ();
            String orderRes = '{"GetOrdersReponse":{"EnterprisePersonID":"1065590075","Orders":{"Order":[{"OrderNumber":"114238192","OrderCreationDate":"08/05/2016","OrderReleaseDate":"","OrderSource":"MAIL","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"90476811","OrderCreationDate":"01/05/2015","OrderReleaseDate":"","OrderSource":"MAIL","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"114234280","OrderCreationDate":"03/22/2016","OrderReleaseDate":"","OrderSource":"MAIL","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"104081567","OrderCreationDate":"06/30/2015","OrderReleaseDate":"","OrderSource":"CCP/GCP","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"97044257","OrderCreationDate":"03/31/2015","OrderReleaseDate":"","OrderSource":"CCP/GCP","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"114234382","OrderCreationDate":"03/23/2016","OrderReleaseDate":"","OrderSource":"MAIL","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"114234293","OrderCreationDate":"03/22/2016","OrderReleaseDate":"","OrderSource":"MAIL","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"114234381","OrderCreationDate":"03/23/2016","OrderReleaseDate":"","OrderSource":"MAIL","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"114202926","OrderCreationDate":"09/26/2015","OrderReleaseDate":"","OrderSource":"IVR","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"114234289","OrderCreationDate":"03/22/2016","OrderReleaseDate":"Test","OrderSource":"MAIL","QueueName":"test","ExternalOrderKey":"test","OrderTotalAmount":"test","OrderpayAmount":"test","OrderTaxAmount":"test","StaleData":"test"}]}}}';
            resDemographic.setBody(orderRes);
            system.assert(resDemographic.getBody() == orderRes);
            mapDetails = continueObj.getRequests();
            String reqTypeOrder = '';
            for(String s : mapDetails.keyset())
            {
                reqTypeOrder = s;
            } 
            labelGetmember = New List<string>();
            labelGetmember.add(reqTypeOrder);
            Test.setContinuationResponse(reqTypeOrder,resDemographic);
            Pharmacy_C_HUM.parseRSGetOrderResponse(labelGetmember,continueObj);
            //get OMS Notes
            continueObj = (Continuation)Pharmacy_C_HUM.invokeOMSLogNotes('1070177218','test','test','AXB0001','0015C000002T9uJ');
            continueObj.continuationMethod = 'parseRSGetMemberResponse';
            continueObj.state = 'Both';
            resDemographic = New Httpresponse ();
			String getMemRes ='{"GetMemberReponse":{ "Customers":{ "Customer":[ { "CustomerIdentity":{ "BirthDate":"10/02/1950", "CustomerKeys":{ "CustomerKey":[ { "CustomerType":"PM", "CustomerValue":"1086943639" }, { "CustomerType":"PC", "CustomerValue":"02" }, { "CustomerType":"PK", "CustomerValue":"6327008" }, { "CustomerType":"AC", "CustomerValue":"8903086" }, { "CustomerType":"NP", "CustomerValue":"107303489" } ] }, "FirstName":"ROYCETTE", "MiddleName":"M", "LastName":"BUTTREY", "Email":"abc@xyz.com" }, "CustomerPreference":{ "CapType":"E", "CapTypeLiteral":"EASY" }, "CustomerDetail":{ "NeedsMemberConsent":"false", "ConsentBeginDate":"", "ConsentEndDate":"", "MemberConsentStatus":"false", "CMRStatus":"", "CMRStatusLiteral":"" }, "Addresses":{ "Address":[ { "AddressKey":"16399980", "AddressType":"B", "AddressTypeLiteral":"BILLING", "AddressLine1":"9080 ABNER ROAD", "AddressLine2":"7469 LYNN AVENUE", "AddressName":"ROYCETTE BUTTREY", "City":"MONROE", "StateCode":"LA", "ZipCode":"71201", "IsActive":"true" }, { "AddressKey":"16399980", "AddressType":"P", "AddressTypeLiteral":"PERMANENT", "AddressLine1":"9080 ABNER ROAD", "AddressLine2":"7469 LYNN AVENUE", "AddressName":"ROYCETTE BUTTREY", "City":"MONROE", "StateCode":"LA", "ZipCode":"71201", "IsActive":"true" }, { "AddressKey":"17229497", "AddressType":"S", "AddressTypeLiteral":"SHIPPING", "AddressLine1":"13146 LAKE LN", "AddressLine2":"", "AddressName":"ROYCETTE BUTTREY", "City":"EUSTACE", "StateCode":"TX", "ZipCode":"75124", "IsActive":"true" }, { "AddressKey":"17229500", "AddressType":"A", "AddressTypeLiteral":"ALTERNATE", "AddressLine1":"PO BOX", "AddressLine2":"508 SPRING VIEW ST SW", "AddressName":"ROYCETTE BUTTREY", "City":"SUAMICO", "StateCode":"WI", "ZipCode":"54313", "IsActive":"true" } ] }, "Phones":{ "Phone":[ { "PhoneKey":"16218856", "PhoneType":"AL", "PhoneValue":"5025801000" }, { "PhoneKey":"19053575", "PhoneType":"CN", "PhoneValue":"5025801600" }, { "PhoneKey":"19053572", "PhoneType":"PP", "PhoneValue":"4545641234" } ] }, "StaleData":"0", "AccountNumber":{ "CustomerValue":"8903086" }, "Finance":{ "CreditLimit":"0.0", "OutstandingBalance":"50.6", "PaymentSchedules":{ "PaymentSchedule":[ { "PaymentDueDate":"", "PaymentMadeDate":"", "PaymentType":"", "SourceApplicationCode":"", "SourceApplicationCodeLiteral":"" } ] } }, "Allergies":{ "Allergy":[ { "HealthKey":"00153", "HealthDescription":"No Known Drug Allergy" } ] }, "CreditCards":{ "CreditCard":[ { "CreditCardKey":"7781077", "CreditCardType":"M", "CreditCardTypeLiteral":"MASTERCARD", "FirstName":"ROYCETTE", "LastName":"BUTTREY", "MiddleName":"L", "ExpirationMonth":"1", "ExpirationYear":"2021", "IsActive":"true", "IsAutoCharge":"false", "CreditCardLast4Digits":"6380", "IsSpendingAccount":"false" }, { "CreditCardKey":"7873701", "CreditCardType":"V", "CreditCardTypeLiteral":"VISA", "FirstName":"THOMAS", "LastName":"ONDERKO", "MiddleName":"", "ExpirationMonth":"10", "ExpirationYear":"2016", "IsActive":"true", "IsAutoCharge":"false", "CreditCardLast4Digits":"8394", "IsSpendingAccount":"false" }, { "CreditCardKey":"7873703", "CreditCardType":"A", "CreditCardTypeLiteral":"AMERICAN EXPRESS", "FirstName":"SONIA", "LastName":"ASENCIORAMOS", "MiddleName":"", "ExpirationMonth":"12", "ExpirationYear":"2022", "IsActive":"true", "IsAutoCharge":"true", "CreditCardLast4Digits":"8431", "IsSpendingAccount":"false" } ] }, "HealthConditions":{ "HealthCondition":[ { "HealthDescription":"Thyroid Disease" } ] }, "LogNotes":{ "LogNote":[  ] }, "Plans":{ "Plan":[ { "PlanEffectiveDate":"01/01/2018", "PlanTerminationDate":"12/31/9999", "PlanType":"COMM", "Payer":"HUMANA INC." } ] }, "communicationPreferences":{ "communicationPreference":[ { "commPrefType":"EMDCL", "commPrefValue":"true" } ] } } ] }, "DuplicateCustomers":{ "CustomerKey":[ { "customerType":"AC", "customerTypeLiteral":"ACCOUNT ID", "customerValue":"8903086" }, { "customerType":"PK", "customerTypeLiteral":"PRIMARY KEY", "customerValue":"6327008" }, { "customerType":"NP", "customerTypeLiteral":"NEW POLICY ID", "customerValue":"107303489" }, { "customerType":"AC", "customerTypeLiteral":"ACCOUNT ID", "customerValue":"8903086" }, { "customerType":"PK", "customerTypeLiteral":"PRIMARY KEY", "customerValue":"6421002" }, { "customerType":"NP", "customerTypeLiteral":"NEW POLICY ID", "customerValue":"107303489" } ] } } }';
            resDemographic.setBody(getMemRes);
            system.assert(resDemographic.getBody() == getMemRes);
            mapDetails = continueObj.getRequests();
            String reqOMS = '';
            for(String s : mapDetails.keyset())
            {
                reqOMS = s;
            } 
            labelGetmember = New List<string>();
            labelGetmember.add(reqOMS);
            Test.setContinuationResponse(reqOMS,resDemographic);
            Pharmacy_C_HUM.parseRSGetMemberResponse(labelGetmember,continueObj);
            //Edit member
            continueObj = (Continuation)Pharmacy_C_HUM.updateGetMember('1070177218','test','test','test','test','AXB0001','0015C000002T9uJ',null,'true');
            continueObj.continuationMethod = 'retrieveUpdatedGetMember';
            continueObj.state = '1070177218_test_test';
            resDemographic = New Httpresponse ();
            String EditRes = '{"EditMemberRequest":{"members":[{"EnterprisePersonID":"1048868178","addresses":{"address":[{"addressKey":"-1","addressLine1":"13146 LAKE LN","addressLine2":"","addressType":"S","addressName":"RONALD MAHAN","city":"EUSTACE","isActive":"true","overrideReasonCode":"","stateCode":"TX","uspsValidate":"true","zipCode":"75124"}]},"creditCards":{"creditCard":[{"creditCardKey":"-1","creditCardType":"V","expirationMonth":"10","expirationYear":"2016","firstName":"THOMAS","isActive":"true","isAutoCharge":"false","lastName":"ONDERKO","middleName":"","tokenKey":"4315830000418394"}]},"customerDetail":{"cmrStatus":"DNC","email":"abc@xyz.com"},"logNotes":{"logNote":[{"logNoteCode":"CONSENT","logNoteDate":"12/07/2016","logNoteKey":"-1","logNoteMessage":"update for testing","logNoteUser":"ANSUSR"}]},"phones":{"phone":[{"phoneKey":"-1","phoneType":"PP","phoneValue":"4545641234"}]}	}]}}';
            resDemographic.setBody(EditRes);
            system.assert(resDemographic.getBody() == EditRes);
            mapDetails = continueObj.getRequests();
            String reqEdit = '';
            for(String s : mapDetails.keyset())
            {
                reqEdit = s;
            } 
            labelGetmember = New List<string>();
            labelGetmember.add(reqEdit);
            Test.setContinuationResponse(reqEdit,resDemographic);
            Pharmacy_C_HUM.retrieveUpdatedGetMember(labelGetmember,'1070177218_test_test'); 
            objPharmacy.toggleCusPopFlag();
            objPharmacy.closePopUp();
            objPharmacy.toggleConfirmFlag();
            objPharmacy.cancelConfirmAddress();
            objPharmacy.closeConfirmPopUp();
             //GetScript
            continueObj = (Continuation)Pharmacy_C_HUM.invokeEpostMemberPrescription('1048868178','test','test','test','AXB0001','0015C000002T9uJ');
            continueObj.continuationMethod = 'parseRSGetScriptResponse';
            continueObj.state = 'Both';
            resDemographic = New Httpresponse ();
            String reqSRResponse = '{"GetScriptsResponse": {   "EnterprisePersonID": "1048868178",   "Scripts": {"Script": [   {      "ConsentStatus": "CA",      "ConsentDateTime": "09/11/2015",      "ScriptKey": "242058738",      "ScriptStatus": "O",      "ScriptStatusLiteral": "OPEN",      "IsAutoRefill": "false",      "AutoRefillEligible": "false",      "DrugType": "STANDARD",      "Items": {"Item":       [                  {            "ItemType": "W",            "ItemLabel": "METOPROLOL TART  25MG TAB"         },                  {            "ItemType": "D",            "ItemLabel": "METOPROLOL TART  25MG TAB"         }      ]},      "Quantity": "90",      "RefillsRemaining": "1",      "DaysSupply": "90",      "LastFillDate": "09/14/2015",      "NextFillDate": "02/21/2017",      "NextFillMinDate": "02/21/2017",      "ScriptExpirationDate": "09/10/2016",      "physician":       {         "FirstName": "STEVEN",         "LastName": "KOERTH",         "Phones": {"Phone":          [                        {               "PhoneType": "WP",             "PhoneValue": "2102001345"            },                        {               "PhoneType": "WP",               "PhoneValue": "2102001345"            },                        {               "PhoneType": "FN",               "PhoneValue": "2102001345"            },                        {               "PhoneType": "FN",               "PhoneValue": "2102001345"            }         ]}      }   }]}}}';

            resDemographic.setBody(reqSRResponse );
            system.assert(resDemographic.getBody() == reqSRResponse );
            mapDetails = continueObj.getRequests();
            String reqSREdit = '';
            for(String s : mapDetails.keyset())
            {
                reqSREdit = s;
            } 
            labelGetmember = New List<string>();
            labelGetmember.add(reqSREdit );
            Test.setContinuationResponse(reqSREdit ,resDemographic);
            Pharmacy_C_HUM.parseRSGetScriptResponse(labelGetmember,continueObj); 
            
            //Address validation
            AddressValidationHelper_DTO_HUM addObj = New AddressValidationHelper_DTO_HUM();
            addObj.enterpriseID = 'test';
            addObj.statusValue = 'test';
            addObj.addresstype = 'test';
           	addObj.addressValidatedFlag = 'test';
            addObj.overrideReasonCode = '0';
            EAS_AddressStandardizationRequest_H_HUM.EAS_AddressStandardRequest_H_HUM dto = New EAS_AddressStandardizationRequest_H_HUM.EAS_AddressStandardRequest_H_HUM();
            dto.AddressLine1 = 'test';
            dto.AddressLine2 = 'test';
            dto.City = 'test';
            dto.StateCode = 'test';
            dto.ZipCode = 'test';
            addObj.addressReq = dto;
            
            continueObj = (Continuation)Pharmacy_C_HUM.invokeEASService(addObj);
            continueObj.continuationMethod = 'addressResponse';
            continueObj.state = addObj;
            resDemographic = New Httpresponse ();
            String reqEASResponse = '{ "StandardizeAddressResponse":{ "SuccessFlag":"Y", "AddressLine1":"9863 GEORGE WASHINGTON MEMORIAL HWY", "City":"GLOUCESTER", "StateCode":"VA", "ZipCode":"23061", "StateID":"51", "ZipCodePlus":"4100", "CountyName":"GLOUCESTER", "CountyID":"073", "Valid":"true", "ChangeIndicator":"GOOD" } }';

            resDemographic.setBody(reqEASResponse );
            system.assert(resDemographic.getBody() == reqEASResponse );
            mapDetails = continueObj.getRequests();
            String reqEAS = '';
            for(String s : mapDetails.keyset())
            {
                reqEAS = s;
            } 
            labelGetmember = New List<string>();
            labelGetmember.add(reqEAS );
            Test.setContinuationResponse(reqEAS ,resDemographic);
            Pharmacy_C_HUM.addressResponse(labelGetmember,addObj);
			
			 //Edit order
            RSCreateAndEditOrderRequest_DTO_HUM oRequest = New RSCreateAndEditOrderRequest_DTO_HUM();
            RSCreateAndEditOrderRequest_DTO_HUM.CreateAndEditOrderRequest objCreateAndEditOrderRequest = New RSCreateAndEditOrderRequest_DTO_HUM.CreateAndEditOrderRequest();
            RSCreateAndEditOrderRequest_DTO_HUM.Address Addrss = New RSCreateAndEditOrderRequest_DTO_HUM.Address();
            Addrss.addressKey = '-1';
            Addrss.addressLine1 = 'test';
            Addrss.addressLine2 = '';
            Addrss.addressType = 'test';
            Addrss.city = 'test';
            Addrss.addressName= 'test';
            Addrss.isActive = 'test';
            Addrss.overrideReasonCode = '';
            Addrss.stateCode = 'test';
            Addrss.uspsValidate = 'test';
            Addrss.zipCode = 'test';
            objCreateAndEditOrderRequest.EnterprisePersonID = 'test';
            objCreateAndEditOrderRequest.orderKey = 'test';    
            objCreateAndEditOrderRequest.address = Addrss;
            oRequest.CreateAndEditOrderRequest = objCreateAndEditOrderRequest;
            continueObj = (Continuation)Pharmacy_C_HUM.invokecreateEditOrderDetail(oRequest,'0015C000002T9uJ');
            continueObj.continuationMethod = 'parseCreateEditOrderResponse';
            continueObj.state = 'Both';
            resDemographic = New Httpresponse ();
            String reqCEResponse = '{" CreateAndEditOrderRequest": {"EnterprisePersonId":"1048868178","orderKey":"112411487","orderPlatform":"Trad","cancelOrder":"True","cancelReasonCode":""}}';
            resDemographic.setBody(reqCEResponse);
            system.assert(resDemographic.getBody() == reqCEResponse );
            mapDetails = continueObj.getRequests();
            String reqCEEdit = '';
            for(String s : mapDetails.keyset())
            {
                reqCEEdit= s;
            } 
            labelGetmember = New List<string>();
            labelGetmember.add(reqCEEdit);
            Test.setContinuationResponse(reqCEEdit,resDemographic);
            Pharmacy_C_HUM.parseCreateEditOrderResponse(labelGetmember,continueObj); 
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
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.Pharmacy_VF_HUM);
            Pharmacy_C_HUM objPharmacy = New Pharmacy_C_HUM();
            objPharmacy.PharmacyDetail ='';
            objPharmacy.refilVariable();
            objPharmacy.lstLogErrors = New List<Error_Log__c>();
            system.assert(objPharmacy.lstLogErrors <> null);
            Pharmacy_D_HUM oDataPharmacy = New Pharmacy_D_HUM();
            ApexPages.StandardController scon = New ApexPages.StandardController(policyObj);
            Pharmacy_C_HUM pharmacyDemographic = New Pharmacy_C_HUM();
            Pharmacy_S_HUM oServicePharmacy = New Pharmacy_S_HUM();
            Continuation continueObj = (Continuation)Pharmacy_C_HUM.invokeGetMemberService('1070177218','AXB0001','0015C000002T9uJ');
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
            Pharmacy_C_HUM.parseRSGetMemberResponse(labelGetmember,continueObj);
            // getOrder
            continueObj = (Continuation)Pharmacy_C_HUM.invokeEpostOrder('1070177218','test','test','AXB0001','0015C000002T9uJ');
            continueObj.continuationMethod = 'parseRSGetOrderResponse';
            continueObj.state = 'Both';
            resDemographic = New Httpresponse ();
            String orderRes = '{ "Fault":{ "faultcode":"\n          Code-67.8.1", "faultstring":"\n          error_service_name :\"RightSourceService &#124;\n          error_service_method : GetAdjustmentsAndPayments &#124;\n          fault_message : ", "detail":{ "transactionInfo":{ "transaction-id":"207260733", "transaction-client":"127.0.0.1", "URL-in":"http://127.0.0.1:8316/RightSourceService_V1.0/GetAdjustmentsAndPayments", "processor-name":"JSONServicesGateway", "processor-type":"Multiprotocol Gateway" }, "errorInfo":{ "error-code":"0x00d30003", "error-headers":"HTTP/1.1 500 Internal Server Error\r\nContent-Type: text/xml; charset=UTF-8\r\nX-Backside-Transport: OK OK\r\nConnection: close\r\n\r\n", "error-message":"Member not found", "error-protocol-reason-phrase":"", "error-protocol-response":"0", "error-subcode":"0x00d30003" } } } }';
            resDemographic.setBody(orderRes);
            system.assert(resDemographic.getBody() == orderRes);
            mapDetails = continueObj.getRequests();
            String reqTypeOrder = '';
            for(String s : mapDetails.keyset())
            {
                reqTypeOrder = s;
            } 
            labelGetmember = New List<string>();
            labelGetmember.add(reqTypeOrder);
            Test.setContinuationResponse(reqTypeOrder,resDemographic);
            Pharmacy_C_HUM.parseRSGetOrderResponse(labelGetmember,continueObj);
            //get OMS Notes
            continueObj = (Continuation)Pharmacy_C_HUM.invokeOMSLogNotes('1070177218','test','test','AXB0001','0015C000002T9uJ');
            continueObj.continuationMethod = 'parseRSGetMemberResponse';
            continueObj.state = 'Both';
            resDemographic = New Httpresponse ();
            String getMemRes = '{ "Fault":{ "faultcode":"\n          Code-67.8.1", "faultstring":"\n          error_service_name :\"RightSourceService &#124;\n          error_service_method : GetAdjustmentsAndPayments &#124;\n          fault_message : ", "detail":{ "transactionInfo":{ "transaction-id":"207260733", "transaction-client":"127.0.0.1", "URL-in":"http://127.0.0.1:8316/RightSourceService_V1.0/GetAdjustmentsAndPayments", "processor-name":"JSONServicesGateway", "processor-type":"Multiprotocol Gateway" }, "errorInfo":{ "error-code":"0x00d30003", "error-headers":"HTTP/1.1 500 Internal Server Error\r\nContent-Type: text/xml; charset=UTF-8\r\nX-Backside-Transport: OK OK\r\nConnection: close\r\n\r\n", "error-message":"Member not found", "error-protocol-reason-phrase":"", "error-protocol-response":"0", "error-subcode":"0x00d30003" } } } }';
            resDemographic.setBody(getMemRes);
            system.assert(resDemographic.getBody() == getMemRes);
            mapDetails = continueObj.getRequests();
            String reqOMS = '';
            for(String s : mapDetails.keyset())
            {
                reqOMS = s;
            } 
            labelGetmember = New List<string>();
            labelGetmember.add(reqOMS);
            Test.setContinuationResponse(reqOMS,resDemographic);
            Pharmacy_C_HUM.parseRSGetMemberResponse(labelGetmember,continueObj);
            //Edit member
            continueObj = (Continuation)Pharmacy_C_HUM.updateGetMember('1070177218','test','test','test','test','AXB0001','0015C000002T9uJ',null,'true');
            continueObj.continuationMethod = 'retrieveUpdatedGetMember';
            continueObj.state = '1070177218_test_test';
            resDemographic = New Httpresponse ();
            String EditRes = '{ "Fault":{ "faultcode":"\n          Code-67.8.1", "faultstring":"\n          error_service_name :\"RightSourceService &#124;\n          error_service_method : GetAdjustmentsAndPayments &#124;\n          fault_message : ", "detail":{ "transactionInfo":{ "transaction-id":"207260733", "transaction-client":"127.0.0.1", "URL-in":"http://127.0.0.1:8316/RightSourceService_V1.0/GetAdjustmentsAndPayments", "processor-name":"JSONServicesGateway", "processor-type":"Multiprotocol Gateway" }, "errorInfo":{ "error-code":"0x00d30003", "error-headers":"HTTP/1.1 500 Internal Server Error\r\nContent-Type: text/xml; charset=UTF-8\r\nX-Backside-Transport: OK OK\r\nConnection: close\r\n\r\n", "error-message":"Member not found", "error-protocol-reason-phrase":"", "error-protocol-response":"0", "error-subcode":"0x00d30003" } } } }';
            resDemographic.setBody(EditRes);
            system.assert(resDemographic.getBody() == EditRes);
            mapDetails = continueObj.getRequests();
            String reqEdit = '';
            for(String s : mapDetails.keyset())
            {
                reqEdit = s;
            } 
            labelGetmember = New List<string>();
            labelGetmember.add(reqEdit);
            Test.setContinuationResponse(reqEdit,resDemographic);
            Pharmacy_C_HUM.retrieveUpdatedGetMember(labelGetmember,'1070177218_test_test'); 
            objPharmacy.toggleCusPopFlag();
            objPharmacy.closePopUp();
            objPharmacy.toggleConfirmFlag();
            objPharmacy.cancelConfirmAddress();
            objPharmacy.closeConfirmPopUp();
             //GetScript
            continueObj = (Continuation)Pharmacy_C_HUM.invokeEpostMemberPrescription('1048868178','test','test','test','AXB0001','0015C000002T9uJ');
            continueObj.continuationMethod = 'parseRSGetScriptResponse';
            continueObj.state = 'Both';
            resDemographic = New Httpresponse ();
            String reqSRResponse = '{ "Fault":{ "faultcode":"\n          Code-67.8.1", "faultstring":"\n          error_service_name :\"RightSourceService &#124;\n          error_service_method : GetAdjustmentsAndPayments &#124;\n          fault_message : ", "detail":{ "transactionInfo":{ "transaction-id":"207260733", "transaction-client":"127.0.0.1", "URL-in":"http://127.0.0.1:8316/RightSourceService_V1.0/GetAdjustmentsAndPayments", "processor-name":"JSONServicesGateway", "processor-type":"Multiprotocol Gateway" }, "errorInfo":{ "error-code":"0x00d30003", "error-headers":"HTTP/1.1 500 Internal Server Error\r\nContent-Type: text/xml; charset=UTF-8\r\nX-Backside-Transport: OK OK\r\nConnection: close\r\n\r\n", "error-message":"Member not found", "error-protocol-reason-phrase":"", "error-protocol-response":"0", "error-subcode":"0x00d30003" } } } }';

            resDemographic.setBody(reqSRResponse );
            system.assert(resDemographic.getBody() == reqSRResponse );
            mapDetails = continueObj.getRequests();
            String reqSREdit = '';
            for(String s : mapDetails.keyset())
            {
                reqSREdit = s;
            } 
            labelGetmember = New List<string>();
            labelGetmember.add(reqSREdit );
            Test.setContinuationResponse(reqSREdit ,resDemographic);
            Pharmacy_C_HUM.parseRSGetScriptResponse(labelGetmember,continueObj); 
            
            //Address validation
            AddressValidationHelper_DTO_HUM addObj = New AddressValidationHelper_DTO_HUM();
            addObj.enterpriseID = 'test';
            addObj.statusValue = 'test';
            addObj.addresstype = 'test';
           	addObj.addressValidatedFlag = 'test';
            addObj.overrideReasonCode = '0';
            EAS_AddressStandardizationRequest_H_HUM.EAS_AddressStandardRequest_H_HUM dto = New EAS_AddressStandardizationRequest_H_HUM.EAS_AddressStandardRequest_H_HUM();
            dto.AddressLine1 = 'test';
            dto.AddressLine2 = 'test';
            dto.City = 'test';
            dto.StateCode = 'test';
            dto.ZipCode = 'test';
            addObj.addressReq = dto;
            
            continueObj = (Continuation)Pharmacy_C_HUM.invokeEASService(addObj);
            continueObj.continuationMethod = 'addressResponse';
            continueObj.state = addObj;
            resDemographic = New Httpresponse ();
            String reqEASResponse = '{ "Fault":{ "faultcode":"\n          Code-67.8.1", "faultstring":"\n          error_service_name :\"RightSourceService &#124;\n          error_service_method : GetAdjustmentsAndPayments &#124;\n          fault_message : ", "detail":{ "transactionInfo":{ "transaction-id":"207260733", "transaction-client":"127.0.0.1", "URL-in":"http://127.0.0.1:8316/RightSourceService_V1.0/GetAdjustmentsAndPayments", "processor-name":"JSONServicesGateway", "processor-type":"Multiprotocol Gateway" }, "errorInfo":{ "error-code":"0x00d30003", "error-headers":"HTTP/1.1 500 Internal Server Error\r\nContent-Type: text/xml; charset=UTF-8\r\nX-Backside-Transport: OK OK\r\nConnection: close\r\n\r\n", "error-message":"Member not found", "error-protocol-reason-phrase":"", "error-protocol-response":"0", "error-subcode":"0x00d30003" } } } }';

            resDemographic.setBody(reqEASResponse );
            system.assert(resDemographic.getBody() == reqEASResponse );
            mapDetails = continueObj.getRequests();
            String reqEAS = '';
            for(String s : mapDetails.keyset())
            {
                reqEAS = s;
            } 
            labelGetmember = New List<string>();
            labelGetmember.add(reqEAS );
            Test.setContinuationResponse(reqEAS ,resDemographic);
            Pharmacy_C_HUM.addressResponse(labelGetmember,addObj);
			
			 //Edit order
            RSCreateAndEditOrderRequest_DTO_HUM oRequest = New RSCreateAndEditOrderRequest_DTO_HUM();
            RSCreateAndEditOrderRequest_DTO_HUM.CreateAndEditOrderRequest objCreateAndEditOrderRequest = New RSCreateAndEditOrderRequest_DTO_HUM.CreateAndEditOrderRequest();
            RSCreateAndEditOrderRequest_DTO_HUM.Address Addrss = New RSCreateAndEditOrderRequest_DTO_HUM.Address();
            Addrss.addressKey = '-1';
            Addrss.addressLine1 = 'test';
            Addrss.addressLine2 = '';
            Addrss.addressType = 'test';
            Addrss.city = 'test';
            Addrss.addressName= 'test';
            Addrss.isActive = 'test';
            Addrss.overrideReasonCode = '';
            Addrss.stateCode = 'test';
            Addrss.uspsValidate = 'test';
            Addrss.zipCode = 'test';
            objCreateAndEditOrderRequest.EnterprisePersonID = 'test';
            objCreateAndEditOrderRequest.orderKey = 'test';    
            objCreateAndEditOrderRequest.address = Addrss;
            oRequest.CreateAndEditOrderRequest = objCreateAndEditOrderRequest;
            continueObj = (Continuation)Pharmacy_C_HUM.invokecreateEditOrderDetail(oRequest,'0015C000002T9uJ');
            continueObj.continuationMethod = 'parseCreateEditOrderResponse';
            continueObj.state = 'Both';
            resDemographic = New Httpresponse ();
            String reqCEResponse = '{ "Fault":{ "faultcode":"\n          Code-67.8.1", "faultstring":"\n          error_service_name :\"RightSourceService &#124;\n          error_service_method : GetAdjustmentsAndPayments &#124;\n          fault_message : ", "detail":{ "transactionInfo":{ "transaction-id":"207260733", "transaction-client":"127.0.0.1", "URL-in":"http://127.0.0.1:8316/RightSourceService_V1.0/GetAdjustmentsAndPayments", "processor-name":"JSONServicesGateway", "processor-type":"Multiprotocol Gateway" }, "errorInfo":{ "error-code":"0x00d30003", "error-headers":"HTTP/1.1 500 Internal Server Error\r\nContent-Type: text/xml; charset=UTF-8\r\nX-Backside-Transport: OK OK\r\nConnection: close\r\n\r\n", "error-message":"Member not found", "error-protocol-reason-phrase":"", "error-protocol-response":"0", "error-subcode":"0x00d30003" } } } }';
            resDemographic.setBody(reqCEResponse);
            system.assert(resDemographic.getBody() == reqCEResponse );
            mapDetails = continueObj.getRequests();
            String reqCEEdit = '';
            for(String s : mapDetails.keyset())
            {
                reqCEEdit= s;
            } 
            labelGetmember = New List<string>();
            labelGetmember.add(reqCEEdit);
            Test.setContinuationResponse(reqCEEdit,resDemographic);
            Pharmacy_C_HUM.parseCreateEditOrderResponse(labelGetmember,continueObj); 
            Test.stopTest();
        }
    }
    
    /*
	* Method Name : testPharmacyGetOrderDetail
	* Description : This method Is used To test the Pharmacy Demographicget order details funtionality
	* Return type : None
	* Parameter   : None
	*/
	Static testMethod void testPharmacyGetOrderDetail()
    {
        User testUser = [Select Id, Name from User where LastName='test User1'];
        policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        List<HUM_Webservice_Callout__c > lstWebCallout = [Select id from HUM_Webservice_Callout__c];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.Pharmacy_VF_HUM);
            Pharmacy_C_HUM objPharmacy = New Pharmacy_C_HUM();
            objPharmacy.PharmacyDetail ='';
            objPharmacy.refilVariable();
            objPharmacy.lstLogErrors = New List<Error_Log__c>();
            system.assert(objPharmacy.lstLogErrors <> null);
            Pharmacy_D_HUM oDataPharmacy = New Pharmacy_D_HUM();
            ApexPages.StandardController scon = New ApexPages.StandardController(policyObj);
            Pharmacy_C_HUM pharmacyDemographic = New Pharmacy_C_HUM();
            Pharmacy_S_HUM oServicePharmacy = New Pharmacy_S_HUM();
            Continuation continueObj = (Continuation)Pharmacy_C_HUM.invokeOrderDetail('114238930','Parent','AXB0001','0015C000002T9uJ');
            continueObj.continuationMethod = 'parseRSGetOrderDetailResponse';
            continueObj.state = New PharmacyOrderDetRes_DTO_HUM (null,null,'Parent','AXB0001','0015C000002T9uJ');
            HttpResponse resDemographic = New Httpresponse();
            String Demog = '{ "GetOrderDetailResponse":{ "OrderKey":"114238930", "ExternalOrderKey":"", "OrderCreationDate":"12/29/2016", "OrderShipDate":"", "CurrentQueue":"DR. CALL TECH FAX", "CurrentQueueDate":"2016-12-29 09:00:32:650", "OrderSource":"WEB", "CancelReasonCode":"", "CreditCards":{ "CreditCard":[ { "CreditCardKey":"5405401", "CreditCardType":"V", "CreditCardTypeLiteral":"VISA", "FirstName":"THOMAS", "LastName":"ONDERKO", "MiddleName":"", "ExpirationMonth":"10", "ExpirationYear":"2016", "IsActive":"true", "IsAutoCharge":"false", "CreditCardLast4Digits":"5498", "IsSpendingAccount":"false" } ] }, "Queues":{ "Queue":[ { "QueueName":"DR. CALL TECH FAX", "QueueUser":"SYSTEM", "QueueDate":"2016-12-29 09:00:32:653" } ] }, "ShippingInformation":{ "ShippingMethod":"BEST METHOD-REGULAR MAIL", "ShippingTrackingNumber":"", "DeliveryInstructions":"" }, "Addresses":{ "AddressLine1":"13146 LAKE LN", "AddressLine2":"", "AddressName":"", "City":"EUSTACE", "StateCode":"TX", "ZipCode":"75124" }, "FirstName":"FAYEZ", "LastName":"TANNOUS", "OrderItems":[ { "CoPayCost":{ "ItemAmount":"0.0", "TaxAmount":"0.0" }, "PlanCost":{ "ItemAmount":"0.0", "TaxAmount":"0.0" }, "ScriptKey":"242439463", "OrderItemStatus":"O", "OrderItemStatusLiteral":"OPEN", "Items":[ { "ItemType":"W", "ItemLabel":"ATORVASTATIN 20MG TAB", "ExternalOrderKey":"" }, { "ItemType":"D", "ItemLabel":"ATORVASTATIN 20MG TAB", "ExternalOrderKey":"" } ], "Strength":"20" }, { "CoPayCost":{ "ItemAmount":"0.0", "TaxAmount":"0.0" }, "PlanCost":{ "ItemAmount":"0.0", "TaxAmount":"0.0" }, "ScriptKey":"242439154", "OrderItemStatus":"C", "OrderItemStatusLiteral":"CANCELLED", "Items":[ { "ItemType":"W", "ItemLabel":"ATENOLOL  25MG TAB", "ExternalOrderKey":"" }, { "ItemType":"D", "ItemLabel":"ATENOLOL  25MG TAB", "ExternalOrderKey":"" } ], "Strength":"25" }, { "CoPayCost":{ "ItemAmount":"0.0", "TaxAmount":"0.0" }, "PlanCost":{ "ItemAmount":"0.0", "TaxAmount":"0.0" }, "ScriptKey":"242439464", "OrderItemStatus":"O", "OrderItemStatusLiteral":"OPEN", "Items":[ { "ItemType":"W", "ItemLabel":"METFORMIN  500MG TAB", "ExternalOrderKey":"" }, { "ItemType":"D", "ItemLabel":"METFORMIN  500MG TAB", "ExternalOrderKey":"" } ], "Strength":"500" }, { "CoPayCost":{ "ItemAmount":"0.0", "TaxAmount":"0.0" }, "PlanCost":{ "ItemAmount":"0.0", "TaxAmount":"0.0" }, "ScriptKey":"242439804", "OrderItemStatus":"O", "OrderItemStatusLiteral":"OPEN", "Items":[ { "ItemType":"W", "ItemLabel":"GABAPENTIN 600MG TAB", "ExternalOrderKey":"" }, { "ItemType":"D", "ItemLabel":"GABAPENTIN 600MG TAB", "ExternalOrderKey":"" } ], "Strength":"600" } ], "StaleData":"0" } }';
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
            Pharmacy_C_HUM.parseRSGetOrderDetailResponse(labelGetmember,New PharmacyOrderDetRes_DTO_HUM (null,null,'Parent','AXB0001','0015C000002T9uJ'));
            Test.stopTest();
        }
    }
    
    /*
	* Method Name : testPharmacyGetOrderDetail
	* Description : This method Is used To test the Pharmacy Demographicget order details funtionality
	* Return type : None
	* Parameter   : None
	*/
	Static testMethod void testFaultPharmacyGetOrderDetail()
    {
        User testUser = [Select Id, Name from User where LastName='test User1'];
        policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        List<HUM_Webservice_Callout__c > lstWebCallout = [Select id from HUM_Webservice_Callout__c];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.Pharmacy_VF_HUM);
            Pharmacy_C_HUM objPharmacy = New Pharmacy_C_HUM();
            objPharmacy.PharmacyDetail ='';
            objPharmacy.refilVariable();
            objPharmacy.lstLogErrors = New List<Error_Log__c>();
            system.assert(objPharmacy.lstLogErrors <> null);
            Pharmacy_D_HUM oDataPharmacy = New Pharmacy_D_HUM();
            ApexPages.StandardController scon = New ApexPages.StandardController(policyObj);
            Pharmacy_C_HUM pharmacyDemographic = New Pharmacy_C_HUM();
            Pharmacy_S_HUM oServicePharmacy = New Pharmacy_S_HUM();
            Continuation continueObj = (Continuation)Pharmacy_C_HUM.invokeOrderDetail('114238930','Parent','AXB0001','0015C000002T9uJ');
            continueObj.continuationMethod = 'parseRSGetOrderDetailResponse';
            continueObj.state = New PharmacyOrderDetRes_DTO_HUM (null,null,'Parent','AXB0001','0015C000002T9uJ');
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
            Pharmacy_C_HUM.parseRSGetOrderDetailResponse(labelGetmember,New PharmacyOrderDetRes_DTO_HUM (null,null,'Parent','AXB0001','0015C000002T9uJ'));
            Test.stopTest();
        }
    }
    
    /**
    * Negative test method for Pharmacy get order funtionality.
    *<p>
    * @param            NULL
    * @return           void
    */
	static testMethod void testNegativePharmacyGetOrderDetail()
    {
        User testUser = [Select Id, Name from User where LastName='test User1'];
        policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        List<HUM_Webservice_Callout__c > lstWebCallout = [Select id from HUM_Webservice_Callout__c];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.Pharmacy_VF_HUM);
            Pharmacy_C_HUM objPharmacy = New Pharmacy_C_HUM();
            objPharmacy.PharmacyDetail ='';
            objPharmacy.refilVariable();
            objPharmacy.lstLogErrors = New List<Error_Log__c>();
            system.assert(objPharmacy.lstLogErrors <> null);
            Pharmacy_D_HUM oDataPharmacy = New Pharmacy_D_HUM();
            ApexPages.StandardController scon = New ApexPages.StandardController(policyObj);
            Pharmacy_C_HUM pharmacyDemographic = New Pharmacy_C_HUM();
            Pharmacy_S_HUM oServicePharmacy = New Pharmacy_S_HUM();
            delete lstWebCallout;
            Continuation continueObj = (Continuation)Pharmacy_C_HUM.invokeOrderDetail('114238930','Parent','AXB0001','0015C000002T9uJ');
            HttpResponse resDemographic = New Httpresponse();
            String Demog = '{ "GetOrderDetailResponse":{ "OrderKey":"114238930", "ExternalOrderKey":"", "OrderCreationDate":"12/29/2016", "OrderShipDate":"", "CurrentQueue":"DR. CALL TECH FAX", "CurrentQueueDate":"2016-12-29 09:00:32:650", "OrderSource":"WEB", "CancelReasonCode":"", "CreditCards":{ "CreditCard":[ { "CreditCardKey":"5405401", "CreditCardType":"V", "CreditCardTypeLiteral":"VISA", "FirstName":"THOMAS", "LastName":"ONDERKO", "MiddleName":"", "ExpirationMonth":"10", "ExpirationYear":"2016", "IsActive":"true", "IsAutoCharge":"false", "CreditCardLast4Digits":"5498", "IsSpendingAccount":"false" } ] }, "Queues":{ "Queue":[ { "QueueName":"DR. CALL TECH FAX", "QueueUser":"SYSTEM", "QueueDate":"2016-12-29 09:00:32:653" } ] }, "ShippingInformation":{ "ShippingMethod":"BEST METHOD-REGULAR MAIL", "ShippingTrackingNumber":"", "DeliveryInstructions":"" }, "Addresses":{ "AddressLine1":"13146 LAKE LN", "AddressLine2":"", "AddressName":"", "City":"EUSTACE", "StateCode":"TX", "ZipCode":"75124" }, "FirstName":"FAYEZ", "LastName":"TANNOUS", "OrderItems":[ { "CoPayCost":{ "ItemAmount":"0.0", "TaxAmount":"0.0" }, "PlanCost":{ "ItemAmount":"0.0", "TaxAmount":"0.0" }, "ScriptKey":"242439463", "OrderItemStatus":"O", "OrderItemStatusLiteral":"OPEN", "Items":[ { "ItemType":"W", "ItemLabel":"ATORVASTATIN 20MG TAB", "ExternalOrderKey":"" }, { "ItemType":"D", "ItemLabel":"ATORVASTATIN 20MG TAB", "ExternalOrderKey":"" } ], "Strength":"20" }, { "CoPayCost":{ "ItemAmount":"0.0", "TaxAmount":"0.0" }, "PlanCost":{ "ItemAmount":"0.0", "TaxAmount":"0.0" }, "ScriptKey":"242439154", "OrderItemStatus":"C", "OrderItemStatusLiteral":"CANCELLED", "Items":[ { "ItemType":"W", "ItemLabel":"ATENOLOL  25MG TAB", "ExternalOrderKey":"" }, { "ItemType":"D", "ItemLabel":"ATENOLOL  25MG TAB", "ExternalOrderKey":"" } ], "Strength":"25" }, { "CoPayCost":{ "ItemAmount":"0.0", "TaxAmount":"0.0" }, "PlanCost":{ "ItemAmount":"0.0", "TaxAmount":"0.0" }, "ScriptKey":"242439464", "OrderItemStatus":"O", "OrderItemStatusLiteral":"OPEN", "Items":[ { "ItemType":"W", "ItemLabel":"METFORMIN  500MG TAB", "ExternalOrderKey":"" }, { "ItemType":"D", "ItemLabel":"METFORMIN  500MG TAB", "ExternalOrderKey":"" } ], "Strength":"500" }, { "CoPayCost":{ "ItemAmount":"0.0", "TaxAmount":"0.0" }, "PlanCost":{ "ItemAmount":"0.0", "TaxAmount":"0.0" }, "ScriptKey":"242439804", "OrderItemStatus":"O", "OrderItemStatusLiteral":"OPEN", "Items":[ { "ItemType":"W", "ItemLabel":"GABAPENTIN 600MG TAB", "ExternalOrderKey":"" }, { "ItemType":"D", "ItemLabel":"GABAPENTIN 600MG TAB", "ExternalOrderKey":"" } ], "Strength":"600" } ], "StaleData":"0" } }';
            resDemographic.setBody(Demog);
            system.assert(resDemographic.getBody() == Demog);
            String reqType = '';
            List<string>labelGetmember = New List<string>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,resDemographic);
            Pharmacy_C_HUM.parseRSGetOrderDetailResponse(labelGetmember,New PharmacyOrderDetRes_DTO_HUM (null,null,'Parent','AXB0001','0015C000002T9uJ'));
            Test.stopTest();
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
        policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.Pharmacy_VF_HUM);
            Pharmacy_C_HUM objPharmacy = New Pharmacy_C_HUM();
            objPharmacy.PharmacyDetail ='';
            objPharmacy.refilVariable();
            objPharmacy.lstLogErrors = New List<Error_Log__c>();
            system.assert(objPharmacy.lstLogErrors <> null);
            Pharmacy_D_HUM oDataPharmacy = New Pharmacy_D_HUM();
            ApexPages.StandardController scon = New ApexPages.StandardController(policyObj);
            Pharmacy_C_HUM pharmacyDemographic = New Pharmacy_C_HUM();
            Pharmacy_S_HUM oServicePharmacy = New Pharmacy_S_HUM();
            Continuation continueObj = (Continuation)Pharmacy_C_HUM.updateCaptype('114238930','Parent','AXB0001','0015C000002T9uJ');
            continueObj.continuationMethod = 'retrieveUpdatedCapType';
            continueObj.state = 'Both_AXB0001_0015C000002T9uJ';
            HttpResponse resDemographic = New Httpresponse();
            String Demog = '{"EditMemberRequest":{"members":[{"EnterprisePersonID":"1048868178","addresses":{"address":[{"addressKey":"-1","addressLine1":"13146 LAKE LN","addressLine2":"","addressType":"S","addressName":"RONALD MAHAN","city":"EUSTACE","isActive":"true","overrideReasonCode":"","stateCode":"TX","uspsValidate":"true","zipCode":"75124"}]},"creditCards":{"creditCard":[{"creditCardKey":"-1","creditCardType":"V","expirationMonth":"10","expirationYear":"2016","firstName":"THOMAS","isActive":"true","isAutoCharge":"false","lastName":"ONDERKO","middleName":"","tokenKey":"4315830000418394"}]},"customerDetail":{"cmrStatus":"DNC","email":"abc@xyz.com"},"logNotes":{"logNote":[{"logNoteCode":"CONSENT","logNoteDate":"12/07/2016","logNoteKey":"-1","logNoteMessage":"update for testing","logNoteUser":"ANSUSR"}]},"phones":{"phone":[{"phoneKey":"-1","phoneType":"PP","phoneValue":"4545641234"}]}	}]}}';
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
            Pharmacy_C_HUM.retrieveUpdatedCapType(labelGetmember,'Both_AXB0001_0015C000002T9uJ');
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
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.Pharmacy_VF_HUM);
            Pharmacy_C_HUM objPharmacy = New Pharmacy_C_HUM();
            objPharmacy.PharmacyDetail ='';
            objPharmacy.refilVariable();
            objPharmacy.lstLogErrors = New List<Error_Log__c>();
            system.assert(objPharmacy.lstLogErrors <> null);
            Pharmacy_D_HUM oDataPharmacy = New Pharmacy_D_HUM();
            ApexPages.StandardController scon = New ApexPages.StandardController(policyObj);
            Pharmacy_C_HUM pharmacyDemographic = New Pharmacy_C_HUM();
            Pharmacy_S_HUM oServicePharmacy = New Pharmacy_S_HUM();
            Continuation continueObj = (Continuation)Pharmacy_C_HUM.updateCaptype('114238930','Parent','AXB0001','0015C000002T9uJ');
            continueObj.continuationMethod = 'retrieveUpdatedCapType';
            continueObj.state = 'Both_AXB0001_0015C000002T9uJ';
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
            Pharmacy_C_HUM.retrieveUpdatedCapType(labelGetmember,'Both_AXB0001_0015C000002T9uJ');
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
        policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        List<HUM_Webservice_Callout__c > lstWebCallout = [Select id from HUM_Webservice_Callout__c];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.Pharmacy_VF_HUM);
            Pharmacy_C_HUM objPharmacy = New Pharmacy_C_HUM();
            objPharmacy.PharmacyDetail ='';
            objPharmacy.refilVariable();
            objPharmacy.lstLogErrors = New List<Error_Log__c>();
            system.assert(objPharmacy.lstLogErrors <> null);
            Pharmacy_D_HUM oDataPharmacy = New Pharmacy_D_HUM();
            ApexPages.StandardController scon = New ApexPages.StandardController(policyObj);
            Pharmacy_C_HUM pharmacyDemographic = New Pharmacy_C_HUM();
            Pharmacy_S_HUM oServicePharmacy = New Pharmacy_S_HUM();
            delete lstWebCallout;
            Continuation continueObj = (Continuation)Pharmacy_C_HUM.updateCaptype('114238930','Parent','AXB0001','0015C000002T9uJ');
            HttpResponse resDemographic = New Httpresponse();
            String Demog = '{"EditMemberRequest":{"members":[{"EnterprisePersonID":"1048868178","addresses":{"address":[{"addressKey":"-1","addressLine1":"13146 LAKE LN","addressLine2":"","addressType":"S","addressName":"RONALD MAHAN","city":"EUSTACE","isActive":"true","overrideReasonCode":"","stateCode":"TX","uspsValidate":"true","zipCode":"75124"}]},"creditCards":{"creditCard":[{"creditCardKey":"-1","creditCardType":"V","expirationMonth":"10","expirationYear":"2016","firstName":"THOMAS","isActive":"true","isAutoCharge":"false","lastName":"ONDERKO","middleName":"","tokenKey":"4315830000418394"}]},"customerDetail":{"cmrStatus":"DNC","email":"abc@xyz.com"},"logNotes":{"logNote":[{"logNoteCode":"CONSENT","logNoteDate":"12/07/2016","logNoteKey":"-1","logNoteMessage":"update for testing","logNoteUser":"ANSUSR"}]},"phones":{"phone":[{"phoneKey":"-1","phoneType":"PP","phoneValue":"4545641234"}]}	}]}}';
            resDemographic.setBody(Demog);
            system.assert(resDemographic.getBody() == Demog);
            String reqType = '';
            List<string>labelGetmember = New List<string>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,resDemographic);
            Pharmacy_C_HUM.retrieveUpdatedCapType(labelGetmember,'Both_AXB0001_0015C000002T9uJ');
            Test.stopTest();
        }
    }
	/*
	* Method Name : testNegativePharmacyUpdateCapTypeForFault
	* Description : This method Is used To test the negative scenarios of update cap type funtionality with Fault
	* Return type : None
	* Parameter   : None
	*/
	Static testMethod void testNegativePharmacyUpdateCapTypeForFault()
	{
		User testUser = [Select Id, Name from User where LastName = 'test User1'];
		policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
		List<HUM_Webservice_Callout__c > lstWebCallout = [Select id from HUM_Webservice_Callout__c];
		System.runAs(testUser)
		{
			Test.startTest();
			Test.setCurrentPage(page.Pharmacy_VF_HUM);
			HttpResponse resDemographic = New Httpresponse();
			String Demog = '{ "Fault":{ "faultcode":"Code-67.8.1", "faultstring":"Test", "detail":{ "transactionInfo":{ "transaction-id":"207260733", "transaction-client":"127.0.0.1", "URL-in":"Test URL", "processor-name":"JSONServicesGateway", "processor-type":"Multiprotocol Gateway" }, "errorInfo":{ "error-code":"0x00d30003", "error-headers":"TestHeader", "error-message":"Member not found", "error-protocol-reason-phrase":"", "error-protocol-response":"0", "error-subcode":"0x00d30003" } } } }';
			resDemographic.setBody(Demog);
			system.assert(resDemographic.getBody() == Demog);

			Pharmacy_H_HUM.logHpFaultError(resDemographic, 'sClassName', 'sMethodName', '5003B000003bt45');
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
            Test.setCurrentPage(page.Pharmacy_VF_HUM);
            Pharmacy_C_HUM objPharmacy = New Pharmacy_C_HUM();
            objPharmacy.PharmacyDetail ='';
            objPharmacy.refilVariable();
            objPharmacy.lstLogErrors = New List<Error_Log__c>();
            system.assert(objPharmacy.lstLogErrors <> null);
            Pharmacy_D_HUM oDataPharmacy = New Pharmacy_D_HUM();
            ApexPages.StandardController scon = New ApexPages.StandardController(policyObj);
            Pharmacy_C_HUM pharmacyDemographic = New Pharmacy_C_HUM();
            Pharmacy_S_HUM oServicePharmacy = New Pharmacy_S_HUM();
            Continuation continueObj = (Continuation)Pharmacy_C_HUM.updateautorefil('114238930','Parent','AXB0001','0015C000002T9uJ','Parent','AXB0001','0015C000002T9uJ');
            continueObj.continuationMethod = 'retrieveautorefil';
            continueObj.state = 'Both_AXB0001_0015C000002T9uJ_Both_AXB0001_0015C000002T9uJ';
            HttpResponse resDemographic = New Httpresponse();
            String Demog = '{"EditScriptRequest":{"ScriptKey":"233582403","AutoRefill":"true","CustomerServiceId":"nxc1262"}}';
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
            Pharmacy_C_HUM.retrieveautorefil(labelGetmember,'Both_AXB0001_0015C000002T9uJ_Both_AXB0001_0015C000002T9uJ');
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
            Test.setCurrentPage(page.Pharmacy_VF_HUM);
            Pharmacy_C_HUM objPharmacy = New Pharmacy_C_HUM();
            objPharmacy.PharmacyDetail ='';
            objPharmacy.refilVariable();
            objPharmacy.lstLogErrors = New List<Error_Log__c>();
            system.assert(objPharmacy.lstLogErrors <> null);
            Pharmacy_D_HUM oDataPharmacy = New Pharmacy_D_HUM();
            ApexPages.StandardController scon = New ApexPages.StandardController(policyObj);
            Pharmacy_C_HUM pharmacyDemographic = New Pharmacy_C_HUM();
            Pharmacy_S_HUM oServicePharmacy = New Pharmacy_S_HUM();
            Continuation continueObj = (Continuation)Pharmacy_C_HUM.updateautorefil('114238930','Parent','AXB0001','0015C000002T9uJ','Parent','AXB0001','0015C000002T9uJ');
            continueObj.continuationMethod = 'retrieveautorefil';
            continueObj.state = 'Both_AXB0001_0015C000002T9uJ_Both_AXB0001_0015C000002T9uJ';
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
            Pharmacy_C_HUM.retrieveautorefil(labelGetmember,'Both_AXB0001_0015C000002T9uJ_Both_AXB0001_0015C000002T9uJ');
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
            Test.setCurrentPage(page.Pharmacy_VF_HUM);
            Pharmacy_C_HUM objPharmacy = New Pharmacy_C_HUM();
            objPharmacy.PharmacyDetail ='';
            objPharmacy.refilVariable();
            objPharmacy.lstLogErrors = New List<Error_Log__c>();
            system.assert(objPharmacy.lstLogErrors <> null);
            Pharmacy_D_HUM oDataPharmacy = New Pharmacy_D_HUM();
            ApexPages.StandardController scon = New ApexPages.StandardController(policyObj);
            Pharmacy_C_HUM pharmacyDemographic = New Pharmacy_C_HUM();
            Pharmacy_S_HUM oServicePharmacy = New Pharmacy_S_HUM();
            delete lstWebCallout;
            Continuation continueObj = (Continuation)Pharmacy_C_HUM.updateautorefil('114238930','Parent','AXB0001','0015C000002T9uJ','Parent','AXB0001','0015C000002T9uJ');
            HttpResponse resDemographic = New Httpresponse();
            String Demog = '{"EditScriptRequest":{"ScriptKey":"233582403","AutoRefill":"true","CustomerServiceId":"nxc1262"}}';
            resDemographic.setBody(Demog);
            system.assert(resDemographic.getBody() == Demog);
            String reqType = '';
            List<string>labelGetmember = New List<string>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,resDemographic);
            Pharmacy_C_HUM.retrieveautorefil(labelGetmember,'Both_AXB0001_0015C000002T9uJ_Both_AXB0001_0015C000002T9uJ');
            Test.stopTest();
        }
    }
    
    /*
	* Method Name : testPharmacyUpdateMTM
	* Description : This method Is used to test the Pharmacy update MTM funtionality
	* Return type : None
	* Parameter   : None
	*/
	Static testMethod void testPharmacyUpdateMTM()
    {
        User testUser = [Select Id, Name from User where LastName='test User1'];
        policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.Pharmacy_VF_HUM);
            Pharmacy_C_HUM objPharmacy = New Pharmacy_C_HUM();
            objPharmacy.PharmacyDetail ='';
            objPharmacy.refilVariable();
            objPharmacy.lstLogErrors = New List<Error_Log__c>();
            system.assert(objPharmacy.lstLogErrors <> null);
            Pharmacy_D_HUM oDataPharmacy = New Pharmacy_D_HUM();
            ApexPages.StandardController scon = New ApexPages.StandardController(policyObj);
            Pharmacy_C_HUM pharmacyDemographic = New Pharmacy_C_HUM();
            Pharmacy_S_HUM oServicePharmacy = New Pharmacy_S_HUM();
            Continuation continueObj = (Continuation)Pharmacy_C_HUM.updateMTM('114238930','AXB0001','0015C000002T9uJ');
            continueObj.continuationMethod = 'retrieveUpdatedMTM';
            continueObj.state = 'Both_AXB0001_0015C000002T9uJ';
            HttpResponse resDemographic = New Httpresponse();
            String Demog = '{"EditMemberRequest":{"members":[{"EnterprisePersonID":"1048868178","addresses":{"address":[{"addressKey":"-1","addressLine1":"13146 LAKE LN","addressLine2":"","addressType":"S","addressName":"RONALD MAHAN","city":"EUSTACE","isActive":"true","overrideReasonCode":"","stateCode":"TX","uspsValidate":"true","zipCode":"75124"}]},"creditCards":{"creditCard":[{"creditCardKey":"-1","creditCardType":"V","expirationMonth":"10","expirationYear":"2016","firstName":"THOMAS","isActive":"true","isAutoCharge":"false","lastName":"ONDERKO","middleName":"","tokenKey":"4315830000418394"}]},"customerDetail":{"cmrStatus":"DNC","email":"abc@xyz.com"},"logNotes":{"logNote":[{"logNoteCode":"CONSENT","logNoteDate":"12/07/2016","logNoteKey":"-1","logNoteMessage":"update for testing","logNoteUser":"ANSUSR"}]},"phones":{"phone":[{"phoneKey":"-1","phoneType":"PP","phoneValue":"4545641234"}]}	}]}}';
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
            Pharmacy_C_HUM.retrieveUpdatedMTM(labelGetmember,'Both_AXB0001_0015C000002T9uJ');
            Test.stopTest();
        }
    }
    
    /*
	* Method Name : testFaultPharmacyUpdateMTM
	* Description : This method Is used to test the faults in Pharmacy update MTM funtionality
	* Return type : None
	* Parameter   : None
	*/
	Static testMethod void testFaultPharmacyUpdateMTM()
    {
        User testUser = [Select Id, Name from User where LastName='test User1'];
        policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.Pharmacy_VF_HUM);
            Pharmacy_C_HUM objPharmacy = New Pharmacy_C_HUM();
            objPharmacy.PharmacyDetail ='';
            objPharmacy.refilVariable();
            objPharmacy.lstLogErrors = New List<Error_Log__c>();
            system.assert(objPharmacy.lstLogErrors <> null);
            Pharmacy_D_HUM oDataPharmacy = New Pharmacy_D_HUM();
            ApexPages.StandardController scon = New ApexPages.StandardController(policyObj);
            Pharmacy_C_HUM pharmacyDemographic = New Pharmacy_C_HUM();
            Pharmacy_S_HUM oServicePharmacy = New Pharmacy_S_HUM();
            Continuation continueObj = (Continuation)Pharmacy_C_HUM.updateMTM('114238930','AXB0001','0015C000002T9uJ');
            continueObj.continuationMethod = 'retrieveUpdatedMTM';
            continueObj.state = 'Both_AXB0001_0015C000002T9uJ';
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
            Pharmacy_C_HUM.retrieveUpdatedMTM(labelGetmember,'Both_AXB0001_0015C000002T9uJ');
            Test.stopTest();
        }
    }
    
    /*
	* Method Name : testNegativePharmacyUpdateMTM
	* Description : This method Is used to test the negative scenarios of Pharmacy update MTM funtionality
	* Return type : None
	* Parameter   : None
	*/
	Static testMethod void testNegativePharmacyUpdateMTM()
    {
        User testUser = [Select Id, Name from User where LastName='test User1'];
        policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        List<HUM_Webservice_Callout__c > lstWebCallout = [Select id from HUM_Webservice_Callout__c];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.Pharmacy_VF_HUM);
            Pharmacy_C_HUM objPharmacy = New Pharmacy_C_HUM();
            objPharmacy.PharmacyDetail ='';
            objPharmacy.refilVariable();
            objPharmacy.lstLogErrors = New List<Error_Log__c>();
            system.assert(objPharmacy.lstLogErrors <> null);
            Pharmacy_D_HUM oDataPharmacy = New Pharmacy_D_HUM();
            ApexPages.StandardController scon = New ApexPages.StandardController(policyObj);
            Pharmacy_C_HUM pharmacyDemographic = New Pharmacy_C_HUM();
            Pharmacy_S_HUM oServicePharmacy = New Pharmacy_S_HUM();
            delete lstWebCallout;
            Continuation continueObj = (Continuation)Pharmacy_C_HUM.updateMTM('114238930','AXB0001','0015C000002T9uJ');
            HttpResponse resDemographic = New Httpresponse();
            String Demog = '{"EditMemberRequest":{"members":[{"EnterprisePersonID":"1048868178","addresses":{"address":[{"addressKey":"-1","addressLine1":"13146 LAKE LN","addressLine2":"","addressType":"S","addressName":"RONALD MAHAN","city":"EUSTACE","isActive":"true","overrideReasonCode":"","stateCode":"TX","uspsValidate":"true","zipCode":"75124"}]},"creditCards":{"creditCard":[{"creditCardKey":"-1","creditCardType":"V","expirationMonth":"10","expirationYear":"2016","firstName":"THOMAS","isActive":"true","isAutoCharge":"false","lastName":"ONDERKO","middleName":"","tokenKey":"4315830000418394"}]},"customerDetail":{"cmrStatus":"DNC","email":"abc@xyz.com"},"logNotes":{"logNote":[{"logNoteCode":"CONSENT","logNoteDate":"12/07/2016","logNoteKey":"-1","logNoteMessage":"update for testing","logNoteUser":"ANSUSR"}]},"phones":{"phone":[{"phoneKey":"-1","phoneType":"PP","phoneValue":"4545641234"}]}	}]}}';
            resDemographic.setBody(Demog);
            system.assert(resDemographic.getBody() == Demog);
            String reqType = '';
            List<string>labelGetmember = New List<string>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,resDemographic);
            Pharmacy_C_HUM.retrieveUpdatedMTM(labelGetmember,'Both_AXB0001_0015C000002T9uJ');
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
        policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.Pharmacy_VF_HUM);
            Pharmacy_C_HUM objPharmacy = New Pharmacy_C_HUM();
            objPharmacy.PharmacyDetail ='';
            objPharmacy.refilVariable();
            objPharmacy.lstLogErrors = New List<Error_Log__c>();
            system.assert(objPharmacy.lstLogErrors <> null);
            Pharmacy_D_HUM oDataPharmacy = New Pharmacy_D_HUM();
            ApexPages.StandardController scon = New ApexPages.StandardController(policyObj);
            Pharmacy_C_HUM pharmacyDemographic = New Pharmacy_C_HUM();
            Pharmacy_S_HUM oServicePharmacy = New Pharmacy_S_HUM();
            Continuation continueObj = (Continuation)Pharmacy_C_HUM.updateConsentBeginDate('114238930','AXB0001','0015C000002T9uJ');
            continueObj.continuationMethod = 'retrieveUpdatedMemberConsent';
            continueObj.state = 'Both_0015C000002T9uJ';
            HttpResponse resDemographic = New Httpresponse();
            String Demog = '{"EditMemberRequest":{"members":[{"EnterprisePersonID":"1048868178","addresses":{"address":[{"addressKey":"-1","addressLine1":"13146 LAKE LN","addressLine2":"","addressType":"S","addressName":"RONALD MAHAN","city":"EUSTACE","isActive":"true","overrideReasonCode":"","stateCode":"TX","uspsValidate":"true","zipCode":"75124"}]},"creditCards":{"creditCard":[{"creditCardKey":"-1","creditCardType":"V","expirationMonth":"10","expirationYear":"2016","firstName":"THOMAS","isActive":"true","isAutoCharge":"false","lastName":"ONDERKO","middleName":"","tokenKey":"4315830000418394"}]},"customerDetail":{"cmrStatus":"DNC","email":"abc@xyz.com"},"logNotes":{"logNote":[{"logNoteCode":"CONSENT","logNoteDate":"12/07/2016","logNoteKey":"-1","logNoteMessage":"update for testing","logNoteUser":"ANSUSR"}]},"phones":{"phone":[{"phoneKey":"-1","phoneType":"PP","phoneValue":"4545641234"}]}	}]}}';
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
            Pharmacy_C_HUM.retrieveUpdatedMemberConsent(labelGetmember,'Both_0015C000002T9uJ');
            
            continueObj = (Continuation)Pharmacy_C_HUM.updateConsentEndDate('114238930','AXB0001','0015C000002T9uJ');
            continueObj.continuationMethod = 'retrieveUpdatedMemberConsent';
            continueObj.state = 'Both_0015C000002T9uJ';
            HttpResponse resDemographic1 = New Httpresponse();
            Demog = '{"EditMemberRequest":{"members":[{"EnterprisePersonID":"1048868178","addresses":{"address":[{"addressKey":"-1","addressLine1":"13146 LAKE LN","addressLine2":"","addressType":"S","addressName":"RONALD MAHAN","city":"EUSTACE","isActive":"true","overrideReasonCode":"","stateCode":"TX","uspsValidate":"true","zipCode":"75124"}]},"creditCards":{"creditCard":[{"creditCardKey":"-1","creditCardType":"V","expirationMonth":"10","expirationYear":"2016","firstName":"THOMAS","isActive":"true","isAutoCharge":"false","lastName":"ONDERKO","middleName":"","tokenKey":"4315830000418394"}]},"customerDetail":{"cmrStatus":"DNC","email":"abc@xyz.com"},"logNotes":{"logNote":[{"logNoteCode":"CONSENT","logNoteDate":"12/07/2016","logNoteKey":"-1","logNoteMessage":"update for testing","logNoteUser":"ANSUSR"}]},"phones":{"phone":[{"phoneKey":"-1","phoneType":"PP","phoneValue":"4545641234"}]}	}]}}';
            resDemographic1.setBody(Demog);
            system.assert(resDemographic1.getBody() == Demog);
            mapDetails = continueObj.getRequests();
            reqType = '';
            for(String s : mapDetails.keyset())
            {
                reqType = s;
            } 
            List<string>labelGetmember1 = New List<string>();
            labelGetmember1.add(reqType);
            Test.setContinuationResponse(reqType,resDemographic1);
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
        policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.Pharmacy_VF_HUM);
            Pharmacy_C_HUM objPharmacy = New Pharmacy_C_HUM();
            objPharmacy.PharmacyDetail ='';
            objPharmacy.refilVariable();
            objPharmacy.lstLogErrors = New List<Error_Log__c>();
            system.assert(objPharmacy.lstLogErrors <> null);
            Pharmacy_D_HUM oDataPharmacy = New Pharmacy_D_HUM();
            ApexPages.StandardController scon = New ApexPages.StandardController(policyObj);
            Pharmacy_C_HUM pharmacyDemographic = New Pharmacy_C_HUM();
            Pharmacy_S_HUM oServicePharmacy = New Pharmacy_S_HUM();
            Continuation continueObj = (Continuation)Pharmacy_C_HUM.updateConsentBeginDate('114238930','AXB0001','0015C000002T9uJ');
            continueObj.continuationMethod = 'retrieveUpdatedMemberConsent';
            continueObj.state = 'Both_0015C000002T9uJ';
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
            Pharmacy_C_HUM.retrieveUpdatedMemberConsent(labelGetmember,'Both_0015C000002T9uJ');
            
            continueObj = (Continuation)Pharmacy_C_HUM.updateConsentEndDate('114238930','AXB0001','0015C000002T9uJ');
            continueObj.continuationMethod = 'retrieveUpdatedMemberConsent';
            continueObj.state = 'Both_0015C000002T9uJ';
            HttpResponse resDemographic1 = New Httpresponse();
            Demog = '{ "Fault":{ "faultcode":"\n          Code-67.8.1", "faultstring":"\n          error_service_name :\"RightSourceService &#124;\n          error_service_method : GetAdjustmentsAndPayments &#124;\n          fault_message : ", "detail":{ "transactionInfo":{ "transaction-id":"207260733", "transaction-client":"127.0.0.1", "URL-in":"http://127.0.0.1:8316/RightSourceService_V1.0/GetAdjustmentsAndPayments", "processor-name":"JSONServicesGateway", "processor-type":"Multiprotocol Gateway" }, "errorInfo":{ "error-code":"0x00d30003", "error-headers":"HTTP/1.1 500 Internal Server Error\r\nContent-Type: text/xml; charset=UTF-8\r\nX-Backside-Transport: OK OK\r\nConnection: close\r\n\r\n", "error-message":"Member not found", "error-protocol-reason-phrase":"", "error-protocol-response":"0", "error-subcode":"0x00d30003" } } } }';
            resDemographic1.setBody(Demog);
            system.assert(resDemographic1.getBody() == Demog);
            mapDetails = continueObj.getRequests();
            reqType = '';
            for(String s : mapDetails.keyset())
            {
                reqType = s;
            } 
            List<string>labelGetmember1 = New List<string>();
            labelGetmember1.add(reqType);
            Test.setContinuationResponse(reqType,resDemographic1);
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
        policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        List<HUM_Webservice_Callout__c > lstWebCallout = [Select id from HUM_Webservice_Callout__c];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.Pharmacy_VF_HUM);
            Pharmacy_C_HUM objPharmacy = New Pharmacy_C_HUM();
            objPharmacy.PharmacyDetail ='';
            objPharmacy.refilVariable();
            objPharmacy.lstLogErrors = New List<Error_Log__c>();
            system.assert(objPharmacy.lstLogErrors <> null);
            Pharmacy_D_HUM oDataPharmacy = New Pharmacy_D_HUM();
            ApexPages.StandardController scon = New ApexPages.StandardController(policyObj);
            Pharmacy_C_HUM pharmacyDemographic = New Pharmacy_C_HUM();
            Pharmacy_S_HUM oServicePharmacy = New Pharmacy_S_HUM();
            delete lstWebCallout;
            Continuation continueObj = (Continuation)Pharmacy_C_HUM.updateConsentBeginDate('114238930','AXB0001','0015C000002T9uJ');
            HttpResponse resDemographic = New Httpresponse();
            String Demog = '{"EditMemberRequest":{"members":[{"EnterprisePersonID":"1048868178","addresses":{"address":[{"addressKey":"-1","addressLine1":"13146 LAKE LN","addressLine2":"","addressType":"S","addressName":"RONALD MAHAN","city":"EUSTACE","isActive":"true","overrideReasonCode":"","stateCode":"TX","uspsValidate":"true","zipCode":"75124"}]},"creditCards":{"creditCard":[{"creditCardKey":"-1","creditCardType":"V","expirationMonth":"10","expirationYear":"2016","firstName":"THOMAS","isActive":"true","isAutoCharge":"false","lastName":"ONDERKO","middleName":"","tokenKey":"4315830000418394"}]},"customerDetail":{"cmrStatus":"DNC","email":"abc@xyz.com"},"logNotes":{"logNote":[{"logNoteCode":"CONSENT","logNoteDate":"12/07/2016","logNoteKey":"-1","logNoteMessage":"update for testing","logNoteUser":"ANSUSR"}]},"phones":{"phone":[{"phoneKey":"-1","phoneType":"PP","phoneValue":"4545641234"}]}	}]}}';
            resDemographic.setBody(Demog);
            system.assert(resDemographic.getBody() == Demog);
            String reqType = '';
            List<string>labelGetmember = New List<string>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,resDemographic);
            Pharmacy_C_HUM.retrieveUpdatedMemberConsent(labelGetmember,'Both_0015C000002T9uJ');
            
            continueObj = (Continuation)Pharmacy_C_HUM.updateConsentEndDate('114238930','AXB0001','0015C000002T9uJ');
            HttpResponse resDemographic1 = New Httpresponse();
            Demog = '{"EditMemberRequest":{"members":[{"EnterprisePersonID":"1048868178","addresses":{"address":[{"addressKey":"-1","addressLine1":"13146 LAKE LN","addressLine2":"","addressType":"S","addressName":"RONALD MAHAN","city":"EUSTACE","isActive":"true","overrideReasonCode":"","stateCode":"TX","uspsValidate":"true","zipCode":"75124"}]},"creditCards":{"creditCard":[{"creditCardKey":"-1","creditCardType":"V","expirationMonth":"10","expirationYear":"2016","firstName":"THOMAS","isActive":"true","isAutoCharge":"false","lastName":"ONDERKO","middleName":"","tokenKey":"4315830000418394"}]},"customerDetail":{"cmrStatus":"DNC","email":"abc@xyz.com"},"logNotes":{"logNote":[{"logNoteCode":"CONSENT","logNoteDate":"12/07/2016","logNoteKey":"-1","logNoteMessage":"update for testing","logNoteUser":"ANSUSR"}]},"phones":{"phone":[{"phoneKey":"-1","phoneType":"PP","phoneValue":"4545641234"}]}	}]}}';
            resDemographic1.setBody(Demog);
            system.assert(resDemographic1.getBody() == Demog);
            reqType = '';
            List<string>labelGetmember1 = New List<string>();
            labelGetmember1.add(reqType);
            Test.setContinuationResponse(reqType,resDemographic1);
            Test.stopTest();
        }
    }
    
    /*
	* Method Name : testPharmacyInvokeOrderDetail
	* Description : This method is used to test the Pharmacy invoke order funtionality
	* Return type : None
	* Parameter   : None
	*/
	Static testMethod void testPharmacyInvokeOrderDetail()
    {
        User testUser = [Select Id, Name from User where LastName='test User1'];
        policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.Pharmacy_VF_HUM);
            Pharmacy_C_HUM objPharmacy = New Pharmacy_C_HUM();
            objPharmacy.PharmacyDetail ='';
            objPharmacy.refilVariable();
            objPharmacy.lstLogErrors = New List<Error_Log__c>();
            system.assert(objPharmacy.lstLogErrors <> null);
            Pharmacy_D_HUM oDataPharmacy = New Pharmacy_D_HUM();
            ApexPages.StandardController scon = New ApexPages.StandardController(policyObj);
            Pharmacy_C_HUM pharmacyDemographic = New Pharmacy_C_HUM();
            Pharmacy_S_HUM oServicePharmacy = New Pharmacy_S_HUM();
            Continuation continueObj = (Continuation)Pharmacy_C_HUM.invokeOrderDetail('114238930','114238930','AXB0001','0015C000002T9uJ');
            continueObj.continuationMethod = 'parseRSGetOrderDetailResponse';
            continueObj.state = new PharmacyOrderDetRes_DTO_HUM (null,null,'114238930','AXB0001','0015C000002T9uJ');
            HttpResponse resDemographic = New Httpresponse();
            String Demog = '{ "GetOrderDetailResponse":{ "OrderKey":"114238930", "ExternalOrderKey":"", "OrderCreationDate":"12/29/2016", "OrderShipDate":"", "CurrentQueue":"DR. CALL TECH FAX", "CurrentQueueDate":"2016-12-29 09:00:32:650", "OrderSource":"WEB", "CancelReasonCode":"", "CreditCards":{ "CreditCard":[ { "CreditCardKey":"5405401", "CreditCardType":"V", "CreditCardTypeLiteral":"VISA", "FirstName":"THOMAS", "LastName":"ONDERKO", "MiddleName":"", "ExpirationMonth":"10", "ExpirationYear":"2016", "IsActive":"true", "IsAutoCharge":"false", "CreditCardLast4Digits":"5498", "IsSpendingAccount":"false" } ] }, "Queues":{ "Queue":[ { "QueueName":"DR. CALL TECH FAX", "QueueUser":"SYSTEM", "QueueDate":"2016-12-29 09:00:32:653" } ] }, "ShippingInformation":{ "ShippingMethod":"BEST METHOD-REGULAR MAIL", "ShippingTrackingNumber":"", "DeliveryInstructions":"" }, "Addresses":{ "AddressLine1":"13146 LAKE LN", "AddressLine2":"", "AddressName":"", "City":"EUSTACE", "StateCode":"TX", "ZipCode":"75124" }, "FirstName":"FAYEZ", "LastName":"TANNOUS", "OrderItems":[ {"ChangedOrder":{"ChangedOrderKey": "114242080","ChangedOrderType": "M","ChangedOrderTypeLiteral": "MERGED ORDER","ChangedReasonCode": ""},{ "CoPayCost":{ "ItemAmount":"0.0", "TaxAmount":"0.0" }, "PlanCost":{ "ItemAmount":"0.0", "TaxAmount":"0.0" }, "ScriptKey":"242439463", "OrderItemStatus":"O", "OrderItemStatusLiteral":"OPEN", "Items":[ { "ItemType":"W", "ItemLabel":"ATORVASTATIN 20MG TAB", "ExternalOrderKey":"" }, { "ItemType":"D", "ItemLabel":"ATORVASTATIN 20MG TAB", "ExternalOrderKey":"" } ], "Strength":"20" }, { "CoPayCost":{ "ItemAmount":"0.0", "TaxAmount":"0.0" }, "PlanCost":{ "ItemAmount":"0.0", "TaxAmount":"0.0" }, "ScriptKey":"242439154", "OrderItemStatus":"C", "OrderItemStatusLiteral":"CANCELLED", "Items":[ { "ItemType":"W", "ItemLabel":"ATENOLOL  25MG TAB", "ExternalOrderKey":"" }, { "ItemType":"D", "ItemLabel":"ATENOLOL  25MG TAB", "ExternalOrderKey":"" } ], "Strength":"25" }, { "CoPayCost":{ "ItemAmount":"0.0", "TaxAmount":"0.0" }, "PlanCost":{ "ItemAmount":"0.0", "TaxAmount":"0.0" }, "ScriptKey":"242439464", "OrderItemStatus":"O", "OrderItemStatusLiteral":"OPEN", "Items":[ { "ItemType":"W", "ItemLabel":"METFORMIN  500MG TAB", "ExternalOrderKey":"" }, { "ItemType":"D", "ItemLabel":"METFORMIN  500MG TAB", "ExternalOrderKey":"" } ], "Strength":"500" }, { "CoPayCost":{ "ItemAmount":"0.0", "TaxAmount":"0.0" }, "PlanCost":{ "ItemAmount":"0.0", "TaxAmount":"0.0" }, "ScriptKey":"242439804", "OrderItemStatus":"O", "OrderItemStatusLiteral":"OPEN", "Items":[ { "ItemType":"W", "ItemLabel":"GABAPENTIN 600MG TAB", "ExternalOrderKey":"" }, { "ItemType":"D", "ItemLabel":"GABAPENTIN 600MG TAB", "ExternalOrderKey":"" } ], "Strength":"600" } ], "StaleData":"0" } }';
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
            Pharmacy_C_HUM.parseRSGetOrderDetailResponse(labelGetmember,new PharmacyOrderDetRes_DTO_HUM (null,null,'114238930','AXB0001','0015C000002T9uJ'));
            Pharmacy_C_HUM.processChildOrder(labelGetmember,new PharmacyOrderDetRes_DTO_HUM (null,null,'114238930','AXB0001','0015C000002T9uJ'));
            Test.stopTest();
        }
    }
    
    /*
	* Method Name : testFaultPharmacyInvokeOrderDetail
	* Description : This method Is used To test the faults in Pharmacy invoke order details funtionality
	* Return type : None
	* Parameter   : None
	*/
	Static testMethod void testFaultPharmacyInvokeOrderDetail()
    {
        User testUser = [Select Id, Name from User where LastName='test User1'];
        policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.Pharmacy_VF_HUM);
            Pharmacy_C_HUM objPharmacy = New Pharmacy_C_HUM();
            objPharmacy.PharmacyDetail ='';
            objPharmacy.refilVariable();
            objPharmacy.lstLogErrors = New List<Error_Log__c>();
            system.assert(objPharmacy.lstLogErrors <> null);
            Pharmacy_D_HUM oDataPharmacy = New Pharmacy_D_HUM();
            ApexPages.StandardController scon = New ApexPages.StandardController(policyObj);
            Pharmacy_C_HUM pharmacyDemographic = New Pharmacy_C_HUM();
            Pharmacy_S_HUM oServicePharmacy = New Pharmacy_S_HUM();
            Continuation continueObj = (Continuation)Pharmacy_C_HUM.invokeOrderDetail('114238930','114238930','AXB0001','0015C000002T9uJ');
            continueObj.continuationMethod = 'parseRSGetOrderDetailResponse';
            continueObj.state = new PharmacyOrderDetRes_DTO_HUM (null,null,'114238930','AXB0001','0015C000002T9uJ');
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
            Pharmacy_C_HUM.parseRSGetOrderDetailResponse(labelGetmember,new PharmacyOrderDetRes_DTO_HUM (null,null,'114238930','AXB0001','0015C000002T9uJ'));
            Pharmacy_C_HUM.processChildOrder(labelGetmember,new PharmacyOrderDetRes_DTO_HUM (null,null,'114238930','AXB0001','0015C000002T9uJ'));
            Test.stopTest();
        }
    }
    
    /*
	* Method Name : testNegativePharmacyInvokeOrderDetail
	* Description : This method is used to test the negative scenarios of Pharmacy invoke order details funtionality
	* Return type : None
	* Parameter   : None
	*/
	Static testMethod void testNegativePharmacyInvokeOrderDetail()
    {
        User testUser = [Select Id, Name from User where LastName='test User1'];
        policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        List<HUM_Webservice_Callout__c > lstWebCallout = [Select id from HUM_Webservice_Callout__c];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.Pharmacy_VF_HUM);
            Pharmacy_C_HUM objPharmacy = New Pharmacy_C_HUM();
            objPharmacy.PharmacyDetail ='';
            objPharmacy.refilVariable();
            objPharmacy.lstLogErrors = New List<Error_Log__c>();
            system.assert(objPharmacy.lstLogErrors <> null);
            Pharmacy_D_HUM oDataPharmacy = New Pharmacy_D_HUM();
            ApexPages.StandardController scon = New ApexPages.StandardController(policyObj);
            Pharmacy_C_HUM pharmacyDemographic = New Pharmacy_C_HUM();
            Pharmacy_S_HUM oServicePharmacy = New Pharmacy_S_HUM();
            delete lstWebCallout;
            Continuation continueObj = (Continuation)Pharmacy_C_HUM.invokeOrderDetail('114238930','114238930','AXB0001','0015C000002T9uJ');
            HttpResponse resDemographic = New Httpresponse();
            String Demog = '{ "GetOrderDetailResponse":{ "OrderKey":"114238930", "ExternalOrderKey":"", "OrderCreationDate":"12/29/2016", "OrderShipDate":"", "CurrentQueue":"DR. CALL TECH FAX", "CurrentQueueDate":"2016-12-29 09:00:32:650", "OrderSource":"WEB", "CancelReasonCode":"", "CreditCards":{ "CreditCard":[ { "CreditCardKey":"5405401", "CreditCardType":"V", "CreditCardTypeLiteral":"VISA", "FirstName":"THOMAS", "LastName":"ONDERKO", "MiddleName":"", "ExpirationMonth":"10", "ExpirationYear":"2016", "IsActive":"true", "IsAutoCharge":"false", "CreditCardLast4Digits":"5498", "IsSpendingAccount":"false" } ] }, "Queues":{ "Queue":[ { "QueueName":"DR. CALL TECH FAX", "QueueUser":"SYSTEM", "QueueDate":"2016-12-29 09:00:32:653" } ] }, "ShippingInformation":{ "ShippingMethod":"BEST METHOD-REGULAR MAIL", "ShippingTrackingNumber":"", "DeliveryInstructions":"" }, "Addresses":{ "AddressLine1":"13146 LAKE LN", "AddressLine2":"", "AddressName":"", "City":"EUSTACE", "StateCode":"TX", "ZipCode":"75124" }, "FirstName":"FAYEZ", "LastName":"TANNOUS", "OrderItems":[ {"ChangedOrder":{"ChangedOrderKey": "114242080","ChangedOrderType": "M","ChangedOrderTypeLiteral": "MERGED ORDER","ChangedReasonCode": ""},{ "CoPayCost":{ "ItemAmount":"0.0", "TaxAmount":"0.0" }, "PlanCost":{ "ItemAmount":"0.0", "TaxAmount":"0.0" }, "ScriptKey":"242439463", "OrderItemStatus":"O", "OrderItemStatusLiteral":"OPEN", "Items":[ { "ItemType":"W", "ItemLabel":"ATORVASTATIN 20MG TAB", "ExternalOrderKey":"" }, { "ItemType":"D", "ItemLabel":"ATORVASTATIN 20MG TAB", "ExternalOrderKey":"" } ], "Strength":"20" }, { "CoPayCost":{ "ItemAmount":"0.0", "TaxAmount":"0.0" }, "PlanCost":{ "ItemAmount":"0.0", "TaxAmount":"0.0" }, "ScriptKey":"242439154", "OrderItemStatus":"C", "OrderItemStatusLiteral":"CANCELLED", "Items":[ { "ItemType":"W", "ItemLabel":"ATENOLOL  25MG TAB", "ExternalOrderKey":"" }, { "ItemType":"D", "ItemLabel":"ATENOLOL  25MG TAB", "ExternalOrderKey":"" } ], "Strength":"25" }, { "CoPayCost":{ "ItemAmount":"0.0", "TaxAmount":"0.0" }, "PlanCost":{ "ItemAmount":"0.0", "TaxAmount":"0.0" }, "ScriptKey":"242439464", "OrderItemStatus":"O", "OrderItemStatusLiteral":"OPEN", "Items":[ { "ItemType":"W", "ItemLabel":"METFORMIN  500MG TAB", "ExternalOrderKey":"" }, { "ItemType":"D", "ItemLabel":"METFORMIN  500MG TAB", "ExternalOrderKey":"" } ], "Strength":"500" }, { "CoPayCost":{ "ItemAmount":"0.0", "TaxAmount":"0.0" }, "PlanCost":{ "ItemAmount":"0.0", "TaxAmount":"0.0" }, "ScriptKey":"242439804", "OrderItemStatus":"O", "OrderItemStatusLiteral":"OPEN", "Items":[ { "ItemType":"W", "ItemLabel":"GABAPENTIN 600MG TAB", "ExternalOrderKey":"" }, { "ItemType":"D", "ItemLabel":"GABAPENTIN 600MG TAB", "ExternalOrderKey":"" } ], "Strength":"600" } ], "StaleData":"0" } }';
            resDemographic.setBody(Demog);
            system.assert(resDemographic.getBody() == Demog);
            String reqType = '';
            List<string>labelGetmember = New List<string>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,resDemographic);
            Pharmacy_C_HUM.parseRSGetOrderDetailResponse(labelGetmember,new PharmacyOrderDetRes_DTO_HUM (null,null,'114238930','AXB0001','0015C000002T9uJ'));
            Pharmacy_C_HUM.processChildOrder(labelGetmember,new PharmacyOrderDetRes_DTO_HUM (null,null,'114238930','AXB0001','0015C000002T9uJ'));
            Test.stopTest();
        }
    }
    
     /*
	* Method Name : testPharmacyReleaseCancelOrder
	* Description : This method Is used To test the Pharmacy release and cancel order details funtionality
	* Return type : None
	* Parameter   : None
	*/
	Static testMethod void testPharmacyReleaseCancelOrder()
    {
        User testUser = [Select Id, Name from User where LastName='test User1'];
        policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.Pharmacy_VF_HUM);
            Pharmacy_C_HUM objPharmacy = New Pharmacy_C_HUM();
            objPharmacy.PharmacyDetail ='';
            objPharmacy.refilVariable();
            objPharmacy.lstLogErrors = New List<Error_Log__c>();
            system.assert(objPharmacy.lstLogErrors <> null);
            Pharmacy_D_HUM oDataPharmacy = New Pharmacy_D_HUM();
            ApexPages.StandardController scon = New ApexPages.StandardController(policyObj);
            Pharmacy_C_HUM pharmacyDemographic = New Pharmacy_C_HUM();
            Pharmacy_S_HUM oServicePharmacy = New Pharmacy_S_HUM();
            Continuation continueObj = (Continuation)Pharmacy_C_HUM.sendRequestReleaseAndCancelOrder('114238930','114238930',true,'AXB0001','0015C000002T9uJ', false, false,'376895855');
            continueObj.continuationMethod = 'processRelOrderResponse';
            continueObj.state = '0015C000002T9uJ';
            HttpResponse resDemographic = New Httpresponse();
            String Demog = '{" CreateAndEditOrderRequest": {"EnterprisePersonId":"1048868178","orderKey":"112411487","orderPlatform":"Trad","cancelOrder":"True","cancelReasonCode":""}}';
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
            PharmacyOrderDetail_C_HUM.processRelOrderResponse(labelGetmember,'0015C000002T9uJ');
            Pharmacy_C_HUM.processRelOrderResponse(labelGetmember,'0015C000002T9uJ');
            Test.stopTest();
        }
    }
    
    /*
	* Method Name : testFaultPharmacyReleaseCancelOrder
	* Description : This method is used to test the faults in Pharmacy release and cancel order funtionality
	* Return type : None
	* Parameter   : None
	*/
	Static testMethod void testFaultPharmacyReleaseCancelOrder()
    {
        User testUser = [Select Id, Name from User where LastName='test User1'];
        policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.Pharmacy_VF_HUM);
            Pharmacy_C_HUM objPharmacy = New Pharmacy_C_HUM();
            objPharmacy.PharmacyDetail ='';
            objPharmacy.refilVariable();
            objPharmacy.lstLogErrors = New List<Error_Log__c>();
            system.assert(objPharmacy.lstLogErrors <> null);
            Pharmacy_D_HUM oDataPharmacy = New Pharmacy_D_HUM();
            ApexPages.StandardController scon = New ApexPages.StandardController(policyObj);
            Pharmacy_C_HUM pharmacyDemographic = New Pharmacy_C_HUM();
            Pharmacy_S_HUM oServicePharmacy = New Pharmacy_S_HUM();
            Continuation continueObj = (Continuation)Pharmacy_C_HUM.sendRequestReleaseAndCancelOrder('114238930','114238930',true,'AXB0001','0015C000002T9uJ', false, false,'376895855');
            continueObj.continuationMethod = 'processRelOrderResponse';
            continueObj.state = '0015C000002T9uJ';
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
            PharmacyOrderDetail_C_HUM.processRelOrderResponse(labelGetmember,'0015C000002T9uJ');
            Pharmacy_C_HUM.processRelOrderResponse(labelGetmember,'0015C000002T9uJ');
            Test.stopTest();
        }
    }
    
    /*
	* Method Name : testNegativePharmacyReleaseCancelOrder
	* Description : This method is used to test the negative scenarios of Pharmacy invoke  order funtionality
	* Return type : None
	* Parameter   : None
	*/
	Static testMethod void testNegativePharmacyReleaseCancelOrder()
    {
        User testUser = [Select Id, Name from User where LastName='test User1'];
        policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        List<HUM_Webservice_Callout__c > lstWebCallout = [Select id from HUM_Webservice_Callout__c];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.Pharmacy_VF_HUM);
            Pharmacy_C_HUM objPharmacy = New Pharmacy_C_HUM();
            objPharmacy.PharmacyDetail ='';
            objPharmacy.refilVariable();
            objPharmacy.lstLogErrors = New List<Error_Log__c>();
            system.assert(objPharmacy.lstLogErrors <> null);
            Pharmacy_D_HUM oDataPharmacy = New Pharmacy_D_HUM();
            ApexPages.StandardController scon = New ApexPages.StandardController(policyObj);
            Pharmacy_C_HUM pharmacyDemographic = New Pharmacy_C_HUM();
            Pharmacy_S_HUM oServicePharmacy = New Pharmacy_S_HUM();
            delete lstWebCallout;
            Continuation continueObj = (Continuation)Pharmacy_C_HUM.sendRequestReleaseAndCancelOrder('114238930','114238930',true,'AXB0001','0015C000002T9uJ', false, false,'376895855');
            HttpResponse resDemographic = New Httpresponse();
            String Demog = '{" CreateAndEditOrderRequest": {"EnterprisePersonId":"1048868178","orderKey":"112411487","orderPlatform":"Trad","cancelOrder":"True","cancelReasonCode":""}}';
            resDemographic.setBody(Demog);
            system.assert(resDemographic.getBody() == Demog);
            String reqType = '';
            List<string>labelGetmember = New List<string>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,resDemographic);
            PharmacyOrderDetail_C_HUM.processRelOrderResponse(labelGetmember,'0015C000002T9uJ');
            Pharmacy_C_HUM.processRelOrderResponse(labelGetmember,'0015C000002T9uJ');
            Test.stopTest();
        }
    }
    
    /*
	* Method Name : testPharmacyCallGetOrder
	* Description : This method Is used To test the Pharmacy call get order funtionality
	* Return type : None
	* Parameter   : None
	*/
	Static testMethod void testPharmacyCallGetOrder()
    {
        User testUser = [Select Id, Name from User where LastName='test User1'];
        policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.Pharmacy_VF_HUM);
            Pharmacy_C_HUM objPharmacy = New Pharmacy_C_HUM();
            objPharmacy.PharmacyDetail ='';
            objPharmacy.refilVariable();
            objPharmacy.lstLogErrors = New List<Error_Log__c>();
            system.assert(objPharmacy.lstLogErrors <> null);
            Pharmacy_D_HUM oDataPharmacy = New Pharmacy_D_HUM();
            ApexPages.StandardController scon = New ApexPages.StandardController(policyObj);
            Pharmacy_C_HUM pharmacyDemographic = New Pharmacy_C_HUM();
            Pharmacy_S_HUM oServicePharmacy = New Pharmacy_S_HUM();
            Continuation continueObj = (Continuation)Pharmacy_C_HUM.callGetOrder('114238930','114238930','AXB0001','0015C000002T9uJ','114238930','AXB0001','0015C000002T9uJ');
            continueObj.continuationMethod = 'processGetOrderResponse';
            continueObj.state = new List < String > { '114238930','AXB0001','0015C000002T9uJ' };
            HttpResponse resDemographic = New Httpresponse();
            String Demog = '{"GetOrdersReponse":{"EnterprisePersonID":"1065590075","Orders":{"Order":[{"OrderNumber":"114238192","OrderCreationDate":"08/05/2016","OrderReleaseDate":"","OrderSource":"MAIL","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"90476811","OrderCreationDate":"01/05/2015","OrderReleaseDate":"","OrderSource":"MAIL","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"114234280","OrderCreationDate":"03/22/2016","OrderReleaseDate":"","OrderSource":"MAIL","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"104081567","OrderCreationDate":"06/30/2015","OrderReleaseDate":"","OrderSource":"CCP/GCP","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"97044257","OrderCreationDate":"03/31/2015","OrderReleaseDate":"","OrderSource":"CCP/GCP","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"114234382","OrderCreationDate":"03/23/2016","OrderReleaseDate":"","OrderSource":"MAIL","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"114234293","OrderCreationDate":"03/22/2016","OrderReleaseDate":"","OrderSource":"MAIL","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"114234381","OrderCreationDate":"03/23/2016","OrderReleaseDate":"","OrderSource":"MAIL","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"114202926","OrderCreationDate":"09/26/2015","OrderReleaseDate":"","OrderSource":"IVR","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"114234289","OrderCreationDate":"03/22/2016","OrderReleaseDate":"Test","OrderSource":"MAIL","QueueName":"test","ExternalOrderKey":"test","OrderTotalAmount":"test","OrderpayAmount":"test","OrderTaxAmount":"test","StaleData":"test"}]}}}';
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
            PharmacyOrderDetail_C_HUM.processGetOrderResponse(labelGetmember,new List < String > { '114238930','AXB0001','0015C000002T9uJ' });
            Pharmacy_C_HUM.processGetOrderResponse(labelGetmember,new List < String > { '114238930','AXB0001','0015C000002T9uJ' });
            Test.stopTest();
        }
    }
    
    /*
	* Method Name : testFaultPharmacyCallGetOrder
	* Description : This method Is used To test the faults in Pharmacy call get order funtionality
	* Return type : None
	* Parameter   : None
	*/
	Static testMethod void testFaultPharmacyCallGetOrder()
    {
        User testUser = [Select Id, Name from User where LastName='test User1'];
        policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.Pharmacy_VF_HUM);
            Pharmacy_C_HUM objPharmacy = New Pharmacy_C_HUM();
            objPharmacy.PharmacyDetail ='';
            objPharmacy.refilVariable();
            objPharmacy.lstLogErrors = New List<Error_Log__c>();
            system.assert(objPharmacy.lstLogErrors <> null);
            Pharmacy_D_HUM oDataPharmacy = New Pharmacy_D_HUM();
            ApexPages.StandardController scon = New ApexPages.StandardController(policyObj);
            Pharmacy_C_HUM pharmacyDemographic = New Pharmacy_C_HUM();
            Pharmacy_S_HUM oServicePharmacy = New Pharmacy_S_HUM();
            Continuation continueObj = (Continuation)Pharmacy_C_HUM.callGetOrder('114238930','114238930','AXB0001','0015C000002T9uJ','114238930','AXB0001','0015C000002T9uJ');
            continueObj.continuationMethod = 'processGetOrderResponse';
            continueObj.state = new List < String > { '114238930','AXB0001','0015C000002T9uJ' };
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
            PharmacyOrderDetail_C_HUM.processGetOrderResponse(labelGetmember,new List < String > { '114238930','AXB0001','0015C000002T9uJ' });
            Pharmacy_C_HUM.processGetOrderResponse(labelGetmember,new List < String > { '114238930','AXB0001','0015C000002T9uJ' });
            Test.stopTest();
        }
    }
    
    /*
	* Method Name : testNegativePharmacyCallGetOrder
	* Description : This method Is used To test the negative scenarios of Pharmacy call get order funtionality
	* Return type : None
	* Parameter   : None
	*/
	Static testMethod void testNegativePharmacyCallGetOrder()
    {
        User testUser = [Select Id, Name from User where LastName='test User1'];
        policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        List<HUM_Webservice_Callout__c > lstWebCallout = [Select id from HUM_Webservice_Callout__c];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.Pharmacy_VF_HUM);
            Pharmacy_C_HUM objPharmacy = New Pharmacy_C_HUM();
            objPharmacy.PharmacyDetail ='';
            objPharmacy.refilVariable();
            objPharmacy.lstLogErrors = New List<Error_Log__c>();
            system.assert(objPharmacy.lstLogErrors <> null);
            Pharmacy_D_HUM oDataPharmacy = New Pharmacy_D_HUM();
            ApexPages.StandardController scon = New ApexPages.StandardController(policyObj);
            Pharmacy_C_HUM pharmacyDemographic = New Pharmacy_C_HUM();
            Pharmacy_S_HUM oServicePharmacy = New Pharmacy_S_HUM();
            delete lstWebCallout;
            Continuation continueObj = (Continuation)Pharmacy_C_HUM.callGetOrder('114238930','114238930','AXB0001','0015C000002T9uJ','114238930','AXB0001','0015C000002T9uJ');
            HttpResponse resDemographic = New Httpresponse();
            String Demog = '{"GetOrdersReponse":{"EnterprisePersonID":"1065590075","Orders":{"Order":[{"OrderNumber":"114238192","OrderCreationDate":"08/05/2016","OrderReleaseDate":"","OrderSource":"MAIL","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"90476811","OrderCreationDate":"01/05/2015","OrderReleaseDate":"","OrderSource":"MAIL","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"114234280","OrderCreationDate":"03/22/2016","OrderReleaseDate":"","OrderSource":"MAIL","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"104081567","OrderCreationDate":"06/30/2015","OrderReleaseDate":"","OrderSource":"CCP/GCP","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"97044257","OrderCreationDate":"03/31/2015","OrderReleaseDate":"","OrderSource":"CCP/GCP","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"114234382","OrderCreationDate":"03/23/2016","OrderReleaseDate":"","OrderSource":"MAIL","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"114234293","OrderCreationDate":"03/22/2016","OrderReleaseDate":"","OrderSource":"MAIL","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"114234381","OrderCreationDate":"03/23/2016","OrderReleaseDate":"","OrderSource":"MAIL","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"114202926","OrderCreationDate":"09/26/2015","OrderReleaseDate":"","OrderSource":"IVR","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"114234289","OrderCreationDate":"03/22/2016","OrderReleaseDate":"Test","OrderSource":"MAIL","QueueName":"test","ExternalOrderKey":"test","OrderTotalAmount":"test","OrderpayAmount":"test","OrderTaxAmount":"test","StaleData":"test"}]}}}';
            resDemographic.setBody(Demog);
            system.assert(resDemographic.getBody() == Demog);
            String reqType = '';
            List<string>labelGetmember = New List<string>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,resDemographic);
            PharmacyOrderDetail_C_HUM.processGetOrderResponse(labelGetmember,new List < String > { '114238930','AXB0001','0015C000002T9uJ' });
            Pharmacy_C_HUM.processGetOrderResponse(labelGetmember,new List < String > { '114238930','AXB0001','0015C000002T9uJ' });
            Test.stopTest();
        }
    }
    
	/*
	* Method Name : testHumanaPharmacyUserNegative
	* Description : This method Is used To test the Negative scenarios for Humana Pharmacy User
	* Return type : None
	* Parameter   : None
	*/
    Static testMethod void testHumanaPharmacyUserNegative()
    {
        User testUser = [Select Id, Name from User where LastName='test User1'];
        System.runAs(testUser)
        {
        	String sProfile = Pharmacy_H_HUM.isPharmacyUser();
        	system.assert(sProfile == Pharmacy_H_HUM.NONPHARMACY_USER_HUM);
        }
     }
    
    /*
    * Method Name : testHumanaPharmacySupervisorUser
    * Description : This method Is used To test Humana Pharmacy Specialist User
    * Return type : None
    * Parameter   : None
    */ 
   
         Static testMethod void testHumanaPharmacySpecialistUser()
    {
        User testUser = [Select Id, Name from User where LastName='test User2'];
        System.runAs(testUser)
        {
        	String sProfile = Pharmacy_H_HUM.isPharmacyUser();
        	 system.assertEquals(Pharmacy_H_HUM.PHARMACYSPECIALIST_USER_HUM ,sProfile );	
        }
     }
    
    /*
    * Method Name : testHumanaPharmacySpecialistUser
    * Description : This method Is used To test Humana Pharmacy Supervisor User
    * Return type : None
    * Parameter   : None
    */  
    Static testMethod void testHumanaPharmacySupervisorUser()
    {
        User testUser = [Select Id, Name from User where LastName='test User3'];
        System.runAs(testUser)
        {
            String sProfile = Pharmacy_H_HUM.isPharmacyUser();
        
           system.assertEquals(Pharmacy_H_HUM.PHARMACYSUPERVISOR_USER_HUM ,sProfile );
        }
     }
	/*
	* Method Name : testHPInsuranceUser
	* Description : This method Is used To test Humana Pharmacy Insurance User
	* Return type : None
	* Parameter   : None
	*/
	Static testMethod void testHPInsuranceUser()
	{
		User testUser = [Select Id, Name from User where LastName = 'test User4'];
		System.runAs(testUser)
		{
			boolean sProfile = Pharmacy_H_HUM.isHPInsurancePSUser();
			system.assert(sProfile == true);
		}
	}
	/*
	* Method Name : testHPPDPUser
	* Description : This method Is used To test PDP Pilot user
	* Return type : None
	* Parameter   : None
	*/
	Static testMethod void testHPPDP()
	{
		User testUser = [Select Id, Name from User where LastName = 'test User5'];
		System.runAs(testUser)
		{
			boolean sProfile = Pharmacy_H_HUM.isPDPPilotUser();
            		system.assert(sProfile == true);
        	}
   	}
    
    	Static testMethod void testTradtionalHPInsuranceUser()
    	{
        	User testUser = [Select Id, Name from User where LastName = 'test User6'];
        	System.runAs(testUser)
        	{
            		boolean sProfile = Pharmacy_H_HUM.isHPTraditionalInsurancePSUser();
			system.assert(sProfile == true);
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
        policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        System.runAs(testUser)
        {
            Test.startTest();
            Pharmacy_C_HUM objPharmacy = New Pharmacy_C_HUM();
            objPharmacy.PharmacyDetail ='';
            objPharmacy.refilVariable();
            objPharmacy.lstLogErrors = New List<Error_Log__c>();
            system.assert(objPharmacy.lstLogErrors <> null);
            ApexPages.StandardController scon = New ApexPages.StandardController(policyObj);
            Continuation continueObj = (Continuation)Pharmacy_C_HUM.getOrderData('114238930','114238930','AXB0001','0015C000002T9uJ','114238930','AXB0001','0015C000002T9uJ');
            continueObj.continuationMethod = 'ProcessOrderResponse';
            continueObj.state =  new List < String > { '114238930','AXB0001','0015C000002T9uJ' };
            HttpResponse resOrder = new Httpresponse ();
            String orderRes= '{"GetOrdersReponse":{"EnterprisePersonID":"1065590075","Orders":{"Order":[{"OrderNumber":"114238192","OrderCreationDate":"08/05/2016","OrderReleaseDate":"","OrderSource":"MAIL","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"90476811","OrderCreationDate":"01/05/2015","OrderReleaseDate":"","OrderSource":"MAIL","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"114234280","OrderCreationDate":"03/22/2016","OrderReleaseDate":"","OrderSource":"MAIL","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"104081567","OrderCreationDate":"06/30/2015","OrderReleaseDate":"","OrderSource":"CCP/GCP","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"97044257","OrderCreationDate":"03/31/2015","OrderReleaseDate":"","OrderSource":"CCP/GCP","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"114234382","OrderCreationDate":"03/23/2016","OrderReleaseDate":"","OrderSource":"MAIL","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"114234293","OrderCreationDate":"03/22/2016","OrderReleaseDate":"","OrderSource":"MAIL","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"114234381","OrderCreationDate":"03/23/2016","OrderReleaseDate":"","OrderSource":"MAIL","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"114202926","OrderCreationDate":"09/26/2015","OrderReleaseDate":"","OrderSource":"IVR","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"114234289","OrderCreationDate":"03/22/2016","OrderReleaseDate":"Test","OrderSource":"MAIL","QueueName":"test","ExternalOrderKey":"test","OrderTotalAmount":"test","OrderpayAmount":"test","OrderTaxAmount":"test","StaleData":"test"}]}}}';
            resOrder.setBody(orderRes);
            system.assert(resOrder.getBody() == orderRes);
            Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
            String reqType = '';
            for(String s : mapDetails.keyset())
            {
                reqType = s;
            } 
            List<string>labelGetmember = New List<string>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,resOrder);
            PharmacyOrderDetail_C_HUM.processGetOrderResponse(labelGetmember,new List < String > { '114238930','AXB0001','0015C000002T9uJ' });
            Pharmacy_C_HUM.ProcessOrderResponse(labelGetmember,new List < String > { '114238930','AXB0001','0015C000002T9uJ' });
            Test.stopTest();
        }
    }
    //service to get OTC key in right source
    @isTest static void testOTCInfoRightSource()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
		policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        System.runAs(testUser)
        {
            Test.startTest();
            Pharmacy_C_HUM opharmacy = new Pharmacy_C_HUM();            
            Continuation continueObj = (Continuation)Pharmacy_C_HUM.invokeGetMemberService('1086187553','AXB0001',policyObj.Id);
            continueObj.continuationMethod = 'parseRSGetMemberResponse';
            HttpResponse httpRes = new Httpresponse ();
            String resBodyString= '{ "GetMemberReponse":{ "Customers":{ "Customer":[ { "CustomerIdentity":{ "BirthDate":"11/11/1960", "CustomerKeys":{ "CustomerKey":[ { "CustomerType":"OC", "CustomerValue":"3712101109095173", "CustomerTypeLiteral":"OTC CARD" }, { "CustomerType":"PM", "CustomerValue":"1086187553", "CustomerTypeLiteral":"PERSON MASTER KEY" }, { "CustomerType":"AC", "CustomerValue":"9907060", "CustomerTypeLiteral":"ACCOUNT ID" }, { "CustomerType":"PK", "CustomerValue":"7367122", "CustomerTypeLiteral":"PRIMARY KEY" } ] }, "FirstName":"LAURYN-", "MiddleName":"", "LastName":"MATTEO", "Email":"gsheppard@humana.com" }, "CustomerPreference":{ "CapType":"S", "CapTypeLiteral":"SAFETY" }, "CustomerDetail":{ "NeedsMemberConsent":"false", "ConsentBeginDate":"", "ConsentEndDate":"", "MemberConsentStatus":"false", "CMRStatus":"", "CMRStatusLiteral":"" }, "Addresses":{ "Address":[ ] }, "Phones":{ "Phone":[ { "PhoneKey":"20241192", "PhoneType":"PP", "PhoneValue":"5025801000" } ] }, "StaleData":"0", "AccountNumber":{ "CustomerValue":"9907060" }, "Finance":{ "CreditLimit":"100.0", "OutstandingBalance":"0.0", "PaymentSchedules":{ "PaymentSchedule":[ { "PaymentDueDate":"", "PaymentMadeDate":"", "PaymentType":"", "SourceApplicationCode":"", "SourceApplicationCodeLiteral":"" } ] } }, "Allergies":{ "Allergy":[ { "HealthKey":"00153", "HealthDescription":"NO KNOWN DRUG ALLERGY" } ] }, "CreditCards":{ "CreditCard":[ ] }, "HealthConditions":{ "HealthCondition":[ { "HealthDescription":"NO KNOWN MEDICAL HISTORY" } ] }, "LogNotes":{ "LogNote":[ ] }, "Plans":{ "Plan":[ { "PlanEffectiveDate":"01/01/2019", "PlanTerminationDate":"", "PlanType":"COMM", "Payer":"HUMANA MEDICAL PLAN," } ] }, "communicationPreferences":{ "communicationPreference":[ ] } } ] }, "DuplicateCustomers":{ "CustomerKey":[ ] } } }';			
			httpRes.setBody(resBodyString);
            system.assert(httpRes.getBody() == resBodyString);
            Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
            string reqType = '';
            for(String s : mapDetails.keyset())
            {
                reqType = s;
            } 
            List<string> labelGetmember = new List<string>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,httpRes);
            Pharmacy_C_HUM.parseRSGetMemberResponse(labelGetmember,continueObj);
            Test.stopTest();
        }
    }
	
	//service to get OTC key in right source
    @isTest static void testOTCInfoRightSourceNegative()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
		policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        System.runAs(testUser)
        {
            Test.startTest();
            Pharmacy_C_HUM opharmacy = new Pharmacy_C_HUM();            
            Continuation continueObj = (Continuation)Pharmacy_C_HUM.invokeGetMemberService('1086187553','AXB0001',policyObj.Id);
            continueObj.continuationMethod = 'parseRSGetMemberResponse';
            HttpResponse httpRes = new Httpresponse ();
            String resBodyString= '{ "GetMemberReponse":{ "Customers":{ "Customer":[ { "CustomerIdentity":{ "BirthDate":"11/11/1960", "CustomerKeys":{ "CustomerKey":[ { "CustomerType":"PM", "CustomerValue":"1086187553", "CustomerTypeLiteral":"PERSON MASTER KEY" }, { "CustomerType":"AC", "CustomerValue":"9907060", "CustomerTypeLiteral":"ACCOUNT ID" }, { "CustomerType":"PK", "CustomerValue":"7367122", "CustomerTypeLiteral":"PRIMARY KEY" } ] }, "FirstName":"LAURYN-", "MiddleName":"", "LastName":"MATTEO", "Email":"gsheppard@humana.com" }, "CustomerPreference":{ "CapType":"S", "CapTypeLiteral":"SAFETY" }, "CustomerDetail":{ "NeedsMemberConsent":"false", "ConsentBeginDate":"", "ConsentEndDate":"", "MemberConsentStatus":"false", "CMRStatus":"", "CMRStatusLiteral":"" }, "Addresses":{ "Address":[ ] }, "Phones":{ "Phone":[ { "PhoneKey":"20241192", "PhoneType":"PP", "PhoneValue":"5025801000" } ] }, "StaleData":"0", "AccountNumber":{ "CustomerValue":"9907060" }, "Finance":{ "CreditLimit":"100.0", "OutstandingBalance":"0.0", "PaymentSchedules":{ "PaymentSchedule":[ { "PaymentDueDate":"", "PaymentMadeDate":"", "PaymentType":"", "SourceApplicationCode":"", "SourceApplicationCodeLiteral":"" } ] } }, "Allergies":{ "Allergy":[ { "HealthKey":"00153", "HealthDescription":"NO KNOWN DRUG ALLERGY" } ] }, "CreditCards":{ "CreditCard":[ ] }, "HealthConditions":{ "HealthCondition":[ { "HealthDescription":"NO KNOWN MEDICAL HISTORY" } ] }, "LogNotes":{ "LogNote":[ ] }, "Plans":{ "Plan":[ { "PlanEffectiveDate":"01/01/2019", "PlanTerminationDate":"", "PlanType":"COMM", "Payer":"HUMANA MEDICAL PLAN," } ] }, "communicationPreferences":{ "communicationPreference":[ ] } } ] }, "DuplicateCustomers":{ "CustomerKey":[ ] } } }';			
			httpRes.setBody(resBodyString);
            system.assert(httpRes.getBody() == resBodyString);
            Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
            string reqType = '';
            for(String s : mapDetails.keyset())
            {
                reqType = s;
            } 
            List<string> labelGetmember = new List<string>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,httpRes);
            Pharmacy_C_HUM.parseRSGetMemberResponse(labelGetmember,continueObj);
            Test.stopTest();
        }
    }
	
	/**
	* Test method for Pharmacy Calls Authorization funtionality.
	*<p>
	* @param            NULL
	* @return           void
	*/
	static testMethod void testPharmacyCallsAuthorization()
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
			Test.setCurrentPage(page.Pharmacy_VF_HUM);
			ApexPages.StandardController scon = new ApexPages.StandardController(policyObj);
			Pharmacy_C_HUM pharmacyCreditCard = new Pharmacy_C_HUM();
			PharmacyBenefits_S_HUM oServicePharmacy = new PharmacyBenefits_S_HUM();
			delete lstWebCallout;
			Continuation continueObj = (Continuation)Pharmacy_C_HUM.invokeGetpAuthService('1014741145',accID);
			HttpResponse httpRes = new Httpresponse ();
			String resBodyString= '{\"GetPriorAuthStatusHistoryResponse\":{\"PriorAuthDetails\":[{\"ArgusAuthDetails\":[{\"AuthDescription\":\"PRE\",\"AuthAddDate\":\"1/4/2012\",\"AuthDecisionDate\":\"1/4/2012\",\"DrugName\":\"SERTRALINEHCL\"},{\"AuthDescription\":\"PRE\",\"AuthAddDate\":\"8/4/2010\",\"AuthDecisionDate\":\"8/4/2010\",\"DrugName\":\"HYDROCODONE/ACETAMINOPHEN\"}]},{\"AgadiaAuthDetails\":[{\"AuthDescription\":\"EOCABORTED\",\"EOCCreationDate\":\"1/12/2012\",\"EOCDecisionDate\":\"1/22/2012\",\"DrugName\":\"BDINSULINSYR0.5ML30GX1/2\\\"\"}]}]}}';
			httpRes.setBody(resBodyString);
			system.assert(httpRes.getBody() == resBodyString);
			string reqType = '';
			List<string>labelGetmember = new List<string>();
			labelGetmember.add(reqType);
			Test.setContinuationResponse(reqType,httpRes);
			Pharmacy_C_HUM.parseGetPAuthResponse(labelGetmember,continueObj);
			
			Test.stopTest();
		}
	}
	
	/*
	* Method Name : testgetInteractionQueryResults
	* Description : This method Is used To test Interaction Query Results.
	* Return type : void
	* Parameter   : NA
	*/
	Static testMethod void testgetInteractionQueryResults() 
	{    
		User testUser = [Select Id, Name from User where LastName='test User1'];
		List<Interaction__c> intId =[SELECT Id, Name FROM Interaction__c limit 1];
		String intractId = intID[0].id;
		
		System.runAs(testUser)
		{
			test.startTest();
			Pharmacy_C_HUM.getInteractionQueryResults(intractId);
			test.stopTest();
		}
	}

     /**
  * test method to cover MemElig100DayPrescrip
  *<p>
  * @param            NULL
  * @return           void
  */
 @isTest static void testinvokeMemEligService()
 {
     User testUser = [Select Id, Name from User where LastName='test User1'];
     policy_member__c policyObj = [select id from policy_member__c limit 1];

      System.runAs(testUser)
      {
          Test.startTest();
           Pharmacy_C_HUM objPharm = New Pharmacy_C_HUM();
            Pharmacy_D_HUM oDPharmacy = New Pharmacy_D_HUM();
            Pharmacy_S_HUM oSPharmacy = New Pharmacy_S_HUM();
          Continuation continueObj = (Continuation)Pharmacy_C_HUM.invokeMemElig100DayPrescripService(policyObj.id);
          continueObj.continuationMethod = 'parseIsMemElig100DayServiceResponse';
          continueObj.state = '24423';
          HttpResponse httpRes = new Httpresponse();
          String resBodyString='{"SearchResponse":{"ResponseStatus":{"Success": true},"Is100DaysPrescribed":true}}';
          httpRes.setBody(resBodyString);
          system.assert(httpRes.getBody() == resBodyString);
          string reqType = 'Continuation-1';
          List<string> labelGetmember = new List<string>();
          labelGetmember.add(reqType);
          Test.setContinuationResponse(reqType,httpRes);          
          Object result = Pharmacy_C_HUM.parseIsMemElig100DayServiceResponse(labelGetmember,continueObj);
          Boolean bDataExist = Json.serialize(result).containsIgnoreCase('Y') ? true : false;
          System.assertEquals(true, bDataExist);
          Test.stopTest();
      }
  }
    
    
       /**
  * test method to cover MemElig100DayPrescrip service Request DTO
  *<p>
  * @param            NULL
  * @return           void
  */
  
   @isTest static void testIsMemElig100DayPrescripRequest_DTO_HUM()
  {
      User testUser = [Select Id,Name from User where LastName='test User1'];
      System.runAs(testUser)
      {
          Test.startTest();
          
          IsMemElig100DayPrescripRequest_DTO_HUM oRequest = new IsMemElig100DayPrescripRequest_DTO_HUM();
          IsMemElig100DayPrescripRequest_DTO_HUM.SearchRequest oReqSearch = new IsMemElig100DayPrescripRequest_DTO_HUM.SearchRequest();
          oReqSearch.PlanID='H7621-002-000-2023';
          system.assertEquals(oReqSearch.PlanID=='H7621-002-000-2023',true);
          Test.stopTest();
      }
  }

   /**
  * test method to cover  MemElig100DayPrescrip service Response DTO
  *<p>
  * @param            NULL
  * @return           void
  */
  @isTest static void testIsMemElig100DayPrescripResponse_DTO_HUM()
  {
      User testUser = [Select Id,Name from User where LastName='test User1'];
      System.runAs(testUser)
      {
          Test.startTest();
          IsMemElig100DayPrescripResponse_DTO_HUM.SearchResponse oSearchResponse = new IsMemElig100DayPrescripResponse_DTO_HUM.SearchResponse();
          oSearchResponse.Is100DaysPrescribed=true;
          
          IsMemElig100DayPrescripResponse_DTO_HUM.ResponseStatus oResponseStatus = new IsMemElig100DayPrescripResponse_DTO_HUM.ResponseStatus();
          oResponseStatus.Success = true;
          
          IsMemElig100DayPrescripResponse_DTO_HUM.Error oError = new IsMemElig100DayPrescripResponse_DTO_HUM.Error();
          oError.ErrorCode='001';
          oError.ErrorDescription='PharmacyBenefit Not available for this member';
          system.assertEquals( oSearchResponse.Is100DaysPrescribed==true,true);
          Test.stopTest();
      }
  }
}