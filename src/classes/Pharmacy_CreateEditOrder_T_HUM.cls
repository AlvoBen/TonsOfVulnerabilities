/*******************************************************************************************************************************
Apex Class Name : Pharmacy_CreateEditOrder_T_HUM.cls
Version         : 1.0
Created On      : 2017
Function        : Test Class for  Pharmacy_CreateEditOrder_C_HUM.cls

Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Rajesh Chandgothia        89722                      04/18/2017                 REQ - 305689  Original ersion
* Rajesh Keswani            89095                      05/02/2017                 REQ - 310758; Add a new item link for Converting Order Edit to Subtab
* Rajesh Keswani            89095                      05/02/2017                 REQ - 310759;  Modify Shipping address in Converting Order Edit/create to Subtab
* Rajesh Keswani            89630                      05/02/2017                 REQ - 310760;  add And update credit cards -- Converting Order Edit/create to Subtab
* Sharan Shanmugam			114275					   07/27/2017				  REQ - 323542 : Exception handling
* Rajesh Chandgothia	    140400 			               10/18/2017			      REQ - 332357 increase Test Coverage
* Ravi Prakash kantamneni    345989                    03/21/2019                 REQ-383718 - Automatically Record Order Details to Case: 'Save & Log' to New Case & Existing Case(RXRF)
* Sumit Pharande            345615                  03/27/2019                   REQ - 362063 - Payment Card Project: do not allow users to inactivate a cc that’s attached to an order in progress
*****************************************************************************************************************************************/
@isTest
private class Pharmacy_CreateEditOrder_T_HUM
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
    
    /*
	* Method Name : testPharmacyGetOrderDetail
	* Description : This method Is used To test the Pharmacy Demographicget order details funtionality
	* Return type : None
	* Parameter   : None
	*/
   static testMethod  void testPharmacyGetOrderDetail()
    {
        User testUser = [Select Id, Name from User where LastName='test User1'];
        policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        System.runAs(testUser)
        {
            Test.startTest();
            PageReference tpageRef = page.Pharmacy_CreateEditOrder_VF_HUM;
            Test.setCurrentPage(tpageRef);
            ApexPages.currentPage().getParameters().put('CEflag','create');
            ApexPages.CurrentPage().getParameters().put('orderNo','567890');
            ApexPages.CurrentPage().getParameters().put('EnterpriseID','978563478956');
            ApexPages.CurrentPage().getParameters().put('osStartDate','12233455');
            ApexPages.CurrentPage().getParameters().put('osEndDate','5345345');
            ApexPages.CurrentPage().getParameters().put('payer','test');
            ApexPages.CurrentPage().getParameters().put('scripts','test');
            ApexPages.CurrentPage().getParameters().put('pmId', policyObj.Id);
            ApexPages.CurrentPage().getParameters().put('rxMap','Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10)');
            Pharmacy_C_HUM objPharmacy = new Pharmacy_C_HUM();
            objPharmacy.PharmacyDetail ='';
            objPharmacy.refilVariable();
            objPharmacy.lstLogErrors = new List<Error_Log__c>();
            system.assert(objPharmacy.lstLogErrors <> null);
            Pharmacy_D_HUM oDataPharmacy = new Pharmacy_D_HUM();
            ApexPages.StandardController scon = new ApexPages.StandardController(policyObj);
            Pharmacy_CreateEditOrder_C_HUM pharmacyDemographic = new Pharmacy_CreateEditOrder_C_HUM ();
            Pharmacy_CreateEditOrder_S_HUM oServicePharmacy = new Pharmacy_CreateEditOrder_S_HUM ();
            Continuation continueObj = (Continuation)Pharmacy_CreateEditOrder_C_HUM.invokeOrderDetail('114238930','Parent','RXK4284','0015C000002T9uJ');
            continueObj.continuationMethod = 'parseRSGetOrderDetailResponse';
            continueObj.state = new PharmacyOrderDetRes_DTO_HUM (null,null,'Parent','RXK4284','0015C000002T9uJ');
            HttpResponse resDemographic = new Httpresponse();
            String Demog = '{ "GetOrderDetailResponse":{ "OrderKey":"114238930", "ExternalOrderKey":"", "OrderCreationDate":"12/29/2016", "OrderShipDate":"", "CurrentQueue":"DR. CALL TECH FAX", "CurrentQueueDate":"2016-12-29 09:00:32:650", "OrderSource":"WEB", "CancelReasonCode":"", "CreditCards":{ "CreditCard":[ { "CreditCardKey":"5405401", "CreditCardType":"V", "CreditCardTypeLiteral":"VISA", "FirstName":"THOMAS", "LastName":"ONDERKO", "MiddleName":"", "ExpirationMonth":"10", "ExpirationYear":"2016", "IsActive":"true", "IsAutoCharge":"false", "CreditCardLast4Digits":"5498", "IsSpendingAccount":"false" } ] }, "Queues":{ "Queue":[ { "QueueName":"DR. CALL TECH FAX", "QueueUser":"SYSTEM", "QueueDate":"2016-12-29 09:00:32:653" } ] }, "ShippingInformation":{ "ShippingMethod":"BEST METHOD-REGULAR MAIL", "ShippingTrackingNumber":"", "DeliveryInstructions":"" }, "Addresses":{ "AddressLine1":"13146 LAKE LN", "AddressLine2":"", "AddressName":"", "City":"EUSTACE", "StateCode":"TX", "ZipCode":"75124" }, "FirstName":"FAYEZ", "LastName":"TANNOUS", "OrderItems":[ { "CoPayCost":{ "ItemAmount":"0.0", "TaxAmount":"0.0" }, "PlanCost":{ "ItemAmount":"0.0", "TaxAmount":"0.0" }, "ScriptKey":"242439463", "OrderItemStatus":"O", "OrderItemStatusLiteral":"OPEN", "Items":[ { "ItemType":"W", "ItemLabel":"ATORVASTATIN 20MG TAB", "ExternalOrderKey":"" }, { "ItemType":"D", "ItemLabel":"ATORVASTATIN 20MG TAB", "ExternalOrderKey":"" } ], "Strength":"20" }, { "CoPayCost":{ "ItemAmount":"0.0", "TaxAmount":"0.0" }, "PlanCost":{ "ItemAmount":"0.0", "TaxAmount":"0.0" }, "ScriptKey":"242439154", "OrderItemStatus":"C", "OrderItemStatusLiteral":"CANCELLED", "Items":[ { "ItemType":"W", "ItemLabel":"ATENOLOL  25MG TAB", "ExternalOrderKey":"" }, { "ItemType":"D", "ItemLabel":"ATENOLOL  25MG TAB", "ExternalOrderKey":"" } ], "Strength":"25" }, { "CoPayCost":{ "ItemAmount":"0.0", "TaxAmount":"0.0" }, "PlanCost":{ "ItemAmount":"0.0", "TaxAmount":"0.0" }, "ScriptKey":"242439464", "OrderItemStatus":"O", "OrderItemStatusLiteral":"OPEN", "Items":[ { "ItemType":"W", "ItemLabel":"METFORMIN  500MG TAB", "ExternalOrderKey":"" }, { "ItemType":"D", "ItemLabel":"METFORMIN  500MG TAB", "ExternalOrderKey":"" } ], "Strength":"500" }, { "CoPayCost":{ "ItemAmount":"0.0", "TaxAmount":"0.0" }, "PlanCost":{ "ItemAmount":"0.0", "TaxAmount":"0.0" }, "ScriptKey":"242439804", "OrderItemStatus":"O", "OrderItemStatusLiteral":"OPEN", "Items":[ { "ItemType":"W", "ItemLabel":"GABAPENTIN 600MG TAB", "ExternalOrderKey":"" }, { "ItemType":"D", "ItemLabel":"GABAPENTIN 600MG TAB", "ExternalOrderKey":"" } ], "Strength":"600" } ], "StaleData":"0" } }';
            resDemographic.setBody(Demog);
            system.assert(resDemographic.getBody() == Demog);
            Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
            String reqType = '';
            for(String s : mapDetails.keyset())
            {
                reqType = s;
            } 
            List<String>labelGetmember = new List<String>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,resDemographic);
            Pharmacy_CreateEditOrder_C_HUM.parseGetOrderDetailResponse(labelGetmember,new PharmacyOrderDetRes_DTO_HUM (null,null,'Parent','RXK4284','0015C000002T9uJ'));
            Test.stopTest();
        }
    }
    
        
    /*
	* Method Name : testPharmacyCreateEditOrder
	* Description : This method Is used To test the Pharmacy Create Edit Order funtionality
	* Return type : None
	* Parameter   : None
	*/
  	static testMethod  void testPharmacyCreateEditOrder()
    {
        User testUser = [Select Id, Name from User where LastName='test User1'];
        policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        System.runAs(testUser)
        {
            Test.startTest();
            PageReference tpageRef = page.Pharmacy_CreateEditOrder_VF_HUM;
            Test.setCurrentPage(tpageRef);
            ApexPages.currentPage().getParameters().put('CEflag','editOrderSummary');
            ApexPages.CurrentPage().getParameters().put('orderNo','567890');
            ApexPages.CurrentPage().getParameters().put('EnterpriseID','978563478956');
            ApexPages.CurrentPage().getParameters().put('osStartDate','12233455');
            ApexPages.CurrentPage().getParameters().put('osEndDate','5345345');
            ApexPages.CurrentPage().getParameters().put('payer','test');
            ApexPages.CurrentPage().getParameters().put('scripts','test');
            Pharmacy_CreateEditOrder_C_HUM objPharmacy = new Pharmacy_CreateEditOrder_C_HUM();
            objPharmacy.createEditflag = ApexPages.currentPage().getParameters().get('CEflag'); 
            objPharmacy.orderNo = ApexPages.currentPage().getParameters().get('orderNo'); 
            objPharmacy.openAddPrespopUp();
            objPharmacy.cancelModConfirmAddress();
            objPharmacy.toggleModConfirmFlag();
            objPharmacy.closeAddPrespopUp();
            objPharmacy.showModCusPopUp();
            objPharmacy.closeConfirmPopUp();
            objPharmacy.showZipCityMsg();
            objPharmacy.disableModCusPopUp();
            objPharmacy.toggleConfirmFlag();
            objPharmacy.cancelConfirmAddress();
            objPharmacy.cancelSoftMsgPopUp();
            objPharmacy.openSoftMsgPopUp();
            objPharmacy.showNewCreditCardSection();
            objPharmacy.hideNewCreditCardSection();
            objPharmacy.showUpdateCreditCardSection();
            objPharmacy.closeUpdatePopUp();
            Continuation continueObj;
            HttpResponse httpRes;
            String resBodyString;
            String reqType;
            Map<String, HTTPRequest> mapDetails;
            List<String>labelGetmember;
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
            
            continueObj = (Continuation)Pharmacy_CreateEditOrder_C_HUM.invokeEASService(addObj);
            continueObj.continuationMethod = 'addressResponse';
            continueObj.state = addObj;
            resDemographic = new Httpresponse ();
            String reqEASResponse = '{ "StandardizeAddressResponse":{ "SuccessFlag":"Y", "AddressLine1":"9863 GEORGE WASHINGTON MEMORIAL HWY", "City":"GLOUCESTER", "StateCode":"VA", "ZipCode":"23061", "StateID":"51", "ZipCodePlus":"4100", "CountyName":"GLOUCESTER", "CountyID":"073", "Valid":"true", "ChangeIndicator":"GOOD" } }';

            resDemographic.setBody(reqEASResponse );
            system.assert(resDemographic.getBody() == reqEASResponse );
            mapDetails = continueObj.getRequests();
            String reqEAS = '';
            for(String s : mapDetails.keyset())
            {
                reqEAS = s;
            } 
            labelGetmember = new List<String>();
            labelGetmember.add(reqEAS );
            Test.setContinuationResponse(reqEAS ,resDemographic);
            Pharmacy_CreateEditOrder_C_HUM.addressResponse(labelGetmember,addObj);
            //Edit Member
            continueObj = (Continuation)Pharmacy_CreateEditOrder_C_HUM.updateGetMember('1070177218','test','test','test','test','RXK4284','0015C000002T9uJ',null);
            continueObj.continuationMethod = 'retrieveUpdatedGetMember';
            continueObj.state = '1070177218_RXK4284_0015C000002T9uJ';
            resDemographic = new Httpresponse ();
            String EditRes = 'test';
            resDemographic.setBody(EditRes);
            system.assert(resDemographic.getBody() == EditRes);
            mapDetails = continueObj.getRequests();
            String reqEdit = '';
            for(String s : mapDetails.keyset())
            {
                reqEdit = s;
            } 
            labelGetmember = new List<String>();
            labelGetmember.add(reqEdit);
            Test.setContinuationResponse(reqEdit,resDemographic);
            Pharmacy_CreateEditOrder_C_HUM.retrieveUpdatedGetMember(labelGetmember,continueObj);
            
            //card details
            continueObj = (Continuation)Pharmacy_CreateEditOrder_C_HUM.invokeGetEditMemService('1048868178','test','test','test','test','test','test','test','test','test','test','test','false','RXK4284','0015C000002T9uJ');
            continueObj.continuationMethod = 'retrieveEditMember';
            continueObj.state = 'Both_RXK4284_0015C000002T9uJ';
            String fix =  'Both_RXK4284_0015C000002T9uJ';
            httpRes = new Httpresponse ();
            resBodyString= '{ "EditMemberResponse":{ "transactionKey":"1120000275944", "transactionTimeStamp":"Tue, Mar 21 2017 08:55:28 313 EDT", "UID":"" } }';
            httpRes.setBody(resBodyString);
            system.assert(httpRes.getBody() == resBodyString);
            mapDetails = continueObj.getRequests();
            reqType = '';
            For (String s : mapDetails.keyset())
            {
                reqType = s;
            } 
            labelGetmember = new List<String>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,httpRes);
            continueObj = (Continuation)Pharmacy_CreateEditOrder_C_HUM.retrieveEditMember(labelGetmember,fix);
            continueObj.continuationMethod = 'parseRSGetMemberCreditResponse';
            continueObj.state = 'Both_RXk4283_0015C000002T9uJ';
            String fixOne = 'Both_RXk4283_0015C000002T9uJ';
            httpRes = new Httpresponse ();
            resBodyString= '{ "EditMemberResponse":{ "transactionKey":"1120000275944", "transactionTimeStamp":"Tue, Mar 21 2017 08:55:28 313 EDT", "UID":"" } }';
            httpRes.setBody(resBodyString);
            system.assert(httpRes.getBody() == resBodyString);
            mapDetails = continueObj.getRequests();
            reqType = '';
            For (String s : mapDetails.keyset())
            {
                reqType = s;
            } 
            labelGetmember = new List<String>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,httpRes);
            Pharmacy_CreateEditOrder_C_HUM.parseRSGetMemberCreditResponse(labelGetmember,fixOne);
            Pharmacy_CreateEditOrder_C_HUM.parseRSGetMemberResponse(labelGetmember,fixOne);
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
            Test.starttest();
           //Edit order
            RSCreateAndEditOrderRequest_DTO_HUM oRequest = new RSCreateAndEditOrderRequest_DTO_HUM();
            RSCreateAndEditOrderRequest_DTO_HUM.CreateAndEditOrderRequest objCreateAndEditOrderRequest = new RSCreateAndEditOrderRequest_DTO_HUM.CreateAndEditOrderRequest();
            RSCreateAndEditOrderRequest_DTO_HUM.Address Addrss = new RSCreateAndEditOrderRequest_DTO_HUM.Address();
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
            HttpResponse resDemographic = new Httpresponse();
            Continuation continueObj = (Continuation)Pharmacy_CreateEditOrder_C_HUM.invokecreateEditOrderDetail(oRequest,'0015C000002T9uJ');
            continueObj.continuationMethod = 'parseCreateEditOrderResponse';
            continueObj.state = 'Both_rxk4283_0015C000002T9uJ';
            resDemographic = new Httpresponse ();
            String reqCEResponse = '{ "GetMemberReponse":{ "Customers":{ "Customer":[ { "CustomerIdentity":{ "BirthDate":"04/24/1950", "CustomerKeys":{ "CustomerKey":[ { "CustomerType":"AC", "CustomerValue":"6727721" }, { "CustomerType":"PK", "CustomerValue":"4012011" }, { "CustomerType":"PM", "CustomerValue":"1070125929" } ] }, "FirstName":"NANCY", "MiddleName":"B", "LastName":"NEGRON", "Email":"" }, "CustomerPreference":{ "CapType":"S", "CapTypeLiteral":"SAFETY" }, "CustomerDetail":{ "NeedsMemberConsent":"false", "ConsentBeginDate":"", "ConsentEndDate":"", "MemberConsentStatus":"", "CMRStatus":"No", "CMRStatusLiteral":"No" }, "Addresses":{ "Address":[  ] }, "Phones":{ "Phone":[ { "PhoneKey":"6508778", "PhoneType":"AL", "PhoneValue":"5025801000" }, { "PhoneKey":"7909869", "PhoneType":"AL", "PhoneValue":"5025805807" }, { "PhoneKey":"7909865", "PhoneType":"AL", "PhoneValue":"1234567890" }, { "PhoneKey":"7910030", "PhoneType":"AL", "PhoneValue":"5225845822" }, { "PhoneKey":"7910053", "PhoneType":"AL", "PhoneValue":"9952012417" }, { "PhoneKey":"7910124", "PhoneType":"AL", "PhoneValue":"5445678722" }, { "PhoneKey":"7910055", "PhoneType":"AL", "PhoneValue":"4025805676" }, { "PhoneKey":"7910038", "PhoneType":"AL", "PhoneValue":"9950012411" }, { "PhoneKey":"7910127", "PhoneType":"AL", "PhoneValue":"5445676644" }, { "PhoneKey":"7910075", "PhoneType":"CN", "PhoneValue":"6767888911" }, { "PhoneKey":"7910024", "PhoneType":"AL", "PhoneValue":"4025805122" }, { "PhoneKey":"7910071", "PhoneType":"AL", "PhoneValue":"9950012412" }, { "PhoneKey":"7909866", "PhoneType":"AL", "PhoneValue":"5025801002" }, { "PhoneKey":"7910067", "PhoneType":"AL", "PhoneValue":"4025805811" }, { "PhoneKey":"7910074", "PhoneType":"CN", "PhoneValue":"6767888999" }, { "PhoneKey":"7910135", "PhoneType":"PP", "PhoneValue":"5445556767" }, { "PhoneKey":"7910084", "PhoneType":"AL", "PhoneValue":"5445678784" }, { "PhoneKey":"7910034", "PhoneType":"AL", "PhoneValue":"4025805678" }, { "PhoneKey":"7910070", "PhoneType":"AL", "PhoneValue":"5445342111" }, { "PhoneKey":"7910006", "PhoneType":"AL", "PhoneValue":"4025805802" }, { "PhoneKey":"7910040", "PhoneType":"AL", "PhoneValue":"4025805555" }, { "PhoneKey":"7910025", "PhoneType":"AL", "PhoneValue":"8978989898" }, { "PhoneKey":"7910036", "PhoneType":"AL", "PhoneValue":"4025805801" }, { "PhoneKey":"7910023", "PhoneType":"AL", "PhoneValue":"5225845888" }, { "PhoneKey":"7909867", "PhoneType":"AL", "PhoneValue":"5025800000" }, { "PhoneKey":"7909868", "PhoneType":"AL", "PhoneValue":"5025805800" }, { "PhoneKey":"7910068", "PhoneType":"AL", "PhoneValue":"9952012418" }, { "PhoneKey":"7909870", "PhoneType":"AL", "PhoneValue":"4025805807" }, { "PhoneKey":"7909886", "PhoneType":"AL", "PhoneValue":"5025805855" }, { "PhoneKey":"7910049", "PhoneType":"AL", "PhoneValue":"4025805672" }, { "PhoneKey":"7910086", "PhoneType":"AL", "PhoneValue":"5445678721" }, { "PhoneKey":"7910044", "PhoneType":"AL", "PhoneValue":"4025805701" }, { "PhoneKey":"7910128", "PhoneType":"AL", "PhoneValue":"5445676643" }, { "PhoneKey":"7910061", "PhoneType":"AL", "PhoneValue":"4025805778" }, { "PhoneKey":"7910125", "PhoneType":"AL", "PhoneValue":"5445676622" }, { "PhoneKey":"7910021", "PhoneType":"AL", "PhoneValue":"5225845999" }, { "PhoneKey":"7910032", "PhoneType":"AL", "PhoneValue":"4025805675" }, { "PhoneKey":"7910033", "PhoneType":"AL", "PhoneValue":"4025805702" }, { "PhoneKey":"7910077", "PhoneType":"CN", "PhoneValue":"6767888998" }, { "PhoneKey":"7910060", "PhoneType":"AL", "PhoneValue":"5225845851" }, { "PhoneKey":"7910058", "PhoneType":"AL", "PhoneValue":"9950012415" }, { "PhoneKey":"7909887", "PhoneType":"AL", "PhoneValue":"5025845855" }, { "PhoneKey":"7910022", "PhoneType":"AL", "PhoneValue":"9950012413" }, { "PhoneKey":"7910059", "PhoneType":"AL", "PhoneValue":"4025805674" }, { "PhoneKey":"7910062", "PhoneType":"AL", "PhoneValue":"4025805777" }, { "PhoneKey":"7910063", "PhoneType":"AL", "PhoneValue":"5225845834" }, { "PhoneKey":"7910054", "PhoneType":"AL", "PhoneValue":"4025805111" }, { "PhoneKey":"7910076", "PhoneType":"CN", "PhoneValue":"6767888912" }, { "PhoneKey":"7910065", "PhoneType":"AL", "PhoneValue":"5225845858" }, { "PhoneKey":"7910042", "PhoneType":"AL", "PhoneValue":"5225845811" }, { "PhoneKey":"7910045", "PhoneType":"AL", "PhoneValue":"5225845856" }, { "PhoneKey":"7910066", "PhoneType":"AL", "PhoneValue":"4025805673" }, { "PhoneKey":"7910078", "PhoneType":"CN", "PhoneValue":"8976768787" }, { "PhoneKey":"7909888", "PhoneType":"AL", "PhoneValue":"5225845855" }, { "PhoneKey":"7910004", "PhoneType":"AL", "PhoneValue":"5225845852" }, { "PhoneKey":"7910083", "PhoneType":"AL", "PhoneValue":"5445678785" }, { "PhoneKey":"7910129", "PhoneType":"CN", "PhoneValue":"8976768786" }, { "PhoneKey":"7910037", "PhoneType":"AL", "PhoneValue":"5225845857" }, { "PhoneKey":"7910069", "PhoneType":"AL", "PhoneValue":"9952012413" }, { "PhoneKey":"7910052", "PhoneType":"AL", "PhoneValue":"5445678787" }, { "PhoneKey":"7910017", "PhoneType":"AL", "PhoneValue":"4025805803" } ] }, "StaleData":"0", "AccountNumber":{ "CustomerValue":"6727721" }, "Finance":{ "CreditLimit":"30.01", "OutstandingBalance":"2.99", "PaymentSchedules":{ "PaymentSchedule":[ { "PaymentDueDate":"06/02/2015", "PaymentMadeDate":"06/02/2015", "PaymentType":"OT", "SourceApplicationCode":"SIL", "SourceApplicationCodeLiteral":"SILVERLINK" }, { "PaymentDueDate":"05/09/2016", "PaymentMadeDate":"05/09/2016", "PaymentType":"OT", "SourceApplicationCode":"ANS", "SourceApplicationCodeLiteral":"EPOSTRX" }, { "PaymentDueDate":"04/27/2016", "PaymentMadeDate":"05/09/2016", "PaymentType":"OT", "SourceApplicationCode":"ANS", "SourceApplicationCodeLiteral":"EPOSTRX" } ] } }, "Allergies":{ "Allergy":[ { "HealthKey":"00153", "HealthDescription":"No Known Drug Allergy" } ] }, "CreditCards":{ "CreditCard":[  ] }, "HealthConditions":{ "HealthCondition":[ { "HealthDescription":"Thyroid Disease" }, { "HealthDescription":"No Known Medical History" } ] }, "LogNotes":{ "LogNote":[ { "LogNoteCode":"90 DAY SUPPLY COLD PACK NO RESPONSE", "LogNoteDate":"01/23/2017", "LogNoteKey":"64665338", "LogNoteMessage":"update for testing 90CPNR", "LogNoteUser":"SXA1954" }, { "LogNoteCode":"90 DAY SUPPLY COLD PACK NO RESPONSE", "LogNoteDate":"02/15/2017", "LogNoteKey":"64665770", "LogNoteMessage":"update for testing 90CPNR", "LogNoteUser":"SXA1954" }, { "LogNoteCode":"OTHER", "LogNoteDate":"02/17/2017", "LogNoteKey":"64665798", "LogNoteMessage":"update for testing 90CPNR", "LogNoteUser":"SXA1954" } ] }, "Plans":{ "Plan":[ { "PlanEffectiveDate":"", "PlanTerminationDate":"", "PlanType":"CASH RX", "Payer":"Humana" }, { "PlanEffectiveDate":"05/05/2015", "PlanTerminationDate":"", "PlanType":"MEDIMPACT OTC", "Payer":"Humana" }, { "PlanEffectiveDate":"05/05/2015", "PlanTerminationDate":"12/31/9999", "PlanType":"MA", "Payer":"HUMANA INSURANCE COMPANY" } ] } } ] } } }';
            resDemographic.setBody(reqCEResponse);
            system.assert(resDemographic.getBody() == reqCEResponse );
            Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
            String reqType = '';
            mapDetails = continueObj.getRequests();
            String reqCEEdit = '';
            for(String s : mapDetails.keyset())
            {
                reqCEEdit= s;
            } 
            List<String>labelGetmember = new List<String>();
            labelGetmember = new List<String>();
            labelGetmember.add(reqCEEdit);
            Test.setContinuationResponse(reqCEEdit,resDemographic);
            try{
            Pharmacy_CreateEditOrder_C_HUM.parseCreateEditOrderResponse(labelGetmember,continueObj); 
            }
            catch(Exception e){}
            
            //GetMemberAndGetScript
            HttpResponse resGetMember = new Httpresponse();
            HttpResponse resGetScript = new Httpresponse();
            continueObj = (Continuation)Pharmacy_CreateEditOrder_C_HUM.getMemberAndScriptData('test','test','test','test','RXK4284','0015C000002T9uJ');
            continueObj.continuationMethod = 'parseMemberAndScriptResponse';
            continueObj.state = 'Both_RXK4284_0015C000002T9uJ';
            String Demog = '{"GetMemberReponse":{"Customers":{"Customer":[{"StaleData":"","SpecialtyVariantObjects":{"CapType":"S"},"Plans":{"Plan":[{"PlanType":"CASH RX","PlanTerminationDate":"","PlanEffectiveDate":""},{"PlanType":"MEDIMPACT OTC","PlanTerminationDate":"","PlanEffectiveDate":"07/29/2015"},{"PlanType":"MA","PlanTerminationDate":"","PlanEffectiveDate":"07/29/2015"}]},"Phones":{"Phone":[{"PhoneValue":"5025801000","PhoneType":"PP","PhoneKey":"7292489"}]},"Payment":{"NonCreditCard":{"Type":"","PaymentNumber":""}},"LogNotes":{"LogNote":[]},"HealthConditions":{"HealthCondition":[{"HealthDescription":"NO KNOWN MEDICAL HISTORY"}]},"Finance":{"PaymentSchedules":{"PaymentSchedule":[]},"OutstandingBalance":"","CreditLimit":"","Adjustments":{"Adjustment":[]}},"CustomerIdentity":{"MiddleName":"","LastName":"DAGGETT","FirstName":"KATHLEEN","Email":"gsheppard@humana.com","CustomerKeys":{"CustomerKey":[{"CustomerValue":"4193538","CustomerType":"PK"},{"CustomerValue":"1031063837","CustomerType":"PM"},{"CustomerValue":"6895629","CustomerType":"AC"}]},"BirthDate":"09/12/1944"},"CustomerDetails":null,"CreditCards":{"CreditCard":[{"IsAutoCharge":"false","IsActive":"true","ExpirationYear":"2018","ExpirationMonth":"11","CreditCardType":"M","CreditCardLast4Digits":"4315830000415489"},{"IsAutoCharge":"false","IsActive":"true","ExpirationYear":"2018","ExpirationMonth":"11","CreditCardType":"M","CreditCardLast4Digits":"4315830000415494"},{"IsAutoCharge":"false","IsActive":"true","ExpirationYear":"2018","ExpirationMonth":"11","CreditCardType":"M","CreditCardLast4Digits":"4315830000415497"},{"IsAutoCharge":"false","IsActive":"true","ExpirationYear":"2016","ExpirationMonth":"10","CreditCardType":"V","CreditCardLast4Digits":"3371953113715499"},{"IsAutoCharge":"false","IsActive":"true","ExpirationYear":"2018","ExpirationMonth":"11","CreditCardType":"M","CreditCardLast4Digits":"4315830000415498"}]},"Allergies":{"Allergy":[{"HealthKey":"00153","HealthDescription":"NO KNOWN DRUG ALLERGY"}]},"Addresses":{"Address":[{"ZipCode":"95404","StateCode":"CA","IsActive":"true","City":"SANTA ROSA","AddressTypeLiteral":"","AddressType":"A","AddressLine2":"","AddressLine1":"3227 CANYON CREEK LN"},{"ZipCode":"32615","StateCode":"FL","IsActive":"true","City":"ALACHUA","AddressTypeLiteral":"","AddressType":"P","AddressLine2":"","AddressLine1":"VOX90"},{"ZipCode":"75124","StateCode":"TX","IsActive":"true","City":"EUSTACE","AddressTypeLiteral":"","AddressType":"B","AddressLine2":"","AddressLine1":"13146 LAKE LN"},{"ZipCode":"75124","StateCode":"TX","IsActive":"true","City":"EUSTACE","AddressTypeLiteral":"","AddressType":"S","AddressLine2":"","AddressLine1":"13146 LAKE LN"},{"ZipCode":"75124","StateCode":"TX","IsActive":"true","City":"EUSTACE","AddressTypeLiteral":"","AddressType":"A","AddressLine2":"","AddressLine1":"13146 LAKE LNT"}]},"AccountNumber":{"CustomerValue":"6895629"}}]}}}';
            resGetMember.setBody(Demog);
            system.assert(resGetMember.getBody() == Demog);
            String script = '{"GetScriptsResponse": {   "EnterprisePersonID": "1048868178",   "Scripts": {"Script": [   {      "ConsentStatus": "CA",      "ConsentDateTime": "09/11/2015",      "ScriptKey": "242058738",      "ScriptStatus": "O",      "ScriptStatusLiteral": "OPEN",      "IsAutoRefill": "false",      "AutoRefillEligible": "false",      "DrugType": "STANDARD",      "Items": {"Item":       [                  {            "ItemType": "W",            "ItemLabel": "METOPROLOL TART  25MG TAB"         },                  {            "ItemType": "D",            "ItemLabel": "METOPROLOL TART  25MG TAB"         }      ]},      "Quantity": "90",      "RefillsRemaining": "1",      "DaysSupply": "90",      "LastFillDate": "09/14/2015",      "NextFillDate": "02/21/2017",      "NextFillMinDate": "02/21/2017",      "ScriptExpirationDate": "09/10/2016",      "physician":       {         "FirstName": "STEVEN",         "LastName": "KOERTH",         "Phones": {"Phone":          [                        {               "PhoneType": "WP",             "PhoneValue": "2102001345"            },                        {               "PhoneType": "WP",               "PhoneValue": "2102001345"            },                        {               "PhoneType": "FN",               "PhoneValue": "2102001345"            },                        {               "PhoneType": "FN",               "PhoneValue": "2102001345"            }         ]}      }   }]}}}';
            resGetScript.setBody(script);
            system.assert(resGetScript.getBody() == script);
            mapDetails = continueObj.getRequests();
            reqType = '';
            mapDetails = continueObj.getRequests();
            List<String> reqCE = new List<String>();
            for(String s : mapDetails.keyset())
            {
                reqCE.add(s);
            } 
            labelGetmember = new List<String>();
            labelGetmember.add(reqCE[0]);
            labelGetmember.add(reqCE[1]);
            Test.setContinuationResponse(reqCE[0],resGetMember);
            Test.setContinuationResponse(reqCE[1],resGetScript);
            try{
            Pharmacy_CreateEditOrder_C_HUM.parseMemberAndScriptResponse(labelGetmember,continueObj);
            }
            
            catch(Exception e){} 
            continueObj = (Continuation)Pharmacy_CreateEditOrder_C_HUM.invokeLogNoteRequest(2,'test','test','test');
            String responseStr = 'test';
            resGetScript.setBody(responseStr);
            mapDetails = continueObj.getRequests();
            reqCE = new List<String>();
            for(String s : mapDetails.keyset())
            {
                reqCE.add(s);
            } 
            
            labelGetmember = new List<String>();
            labelGetmember.add(reqCE[0]);
            String id = String.valueOf(policyObj.id);
            Test.setContinuationResponse(reqCE[0],resGetMember);
            Pharmacy_CreateEditOrder_C_HUM.logNoteResponse(labelGetmember,id);
            Test.stopTest();
            
        }
    }
     Static testMethod void testExceptionPharmacyDemographic()
        {
        User testUser = [Select Id, Name from User where LastName='test User1'];
        policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        List<HUM_Webservice_Callout__c > lstWebCallout = [Select id from HUM_Webservice_Callout__c];
        System.runAs(testUser)
        {
            Test.startTest();
           //Edit order
            RSCreateAndEditOrderRequest_DTO_HUM oRequest = new RSCreateAndEditOrderRequest_DTO_HUM();
            RSCreateAndEditOrderRequest_DTO_HUM.CreateAndEditOrderRequest objCreateAndEditOrderRequest = new RSCreateAndEditOrderRequest_DTO_HUM.CreateAndEditOrderRequest();
            RSCreateAndEditOrderRequest_DTO_HUM.Address Addrss = new RSCreateAndEditOrderRequest_DTO_HUM.Address();
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
            Pharmacy_CreateEditOrder_C_HUM pc = new Pharmacy_CreateEditOrder_C_HUM();
            Pharmacy_CreateEditOrder_D_HUM pd = new Pharmacy_CreateEditOrder_D_HUM();
            Pharmacy_CreateEditOrder_S_HUM ps = new Pharmacy_CreateEditOrder_S_HUM();
            delete lstWebCallout;
            objCreateAndEditOrderRequest.EnterprisePersonID = 'test';
            objCreateAndEditOrderRequest.orderKey = 'test';    
            objCreateAndEditOrderRequest.address = Addrss;
            oRequest.CreateAndEditOrderRequest = objCreateAndEditOrderRequest;
            HttpResponse resDemographic = new Httpresponse();
            Continuation continueObj = (Continuation)Pharmacy_CreateEditOrder_C_HUM.invokecreateEditOrderDetail(oRequest,'0015C000002T9uJ');
            //continueObj.continuationMethod = 'parseCreateEditOrderResponse';
            //continueObj.state = 'Both_rxk4283_0015C000002T9uJ';
            resDemographic = new Httpresponse ();
            String reqCEResponse = '{ "GetMemberReponse":{ "Customers":{ "Customer":[ { "CustomerIdentity":{ "BirthDate":"04/24/1950", "CustomerKeys":{ "CustomerKey":[ { "CustomerType":"AC", "CustomerValue":"6727721" }, { "CustomerType":"PK", "CustomerValue":"4012011" }, { "CustomerType":"PM", "CustomerValue":"1070125929" } ] }, "FirstName":"NANCY", "MiddleName":"B", "LastName":"NEGRON", "Email":"" }, "CustomerPreference":{ "CapType":"S", "CapTypeLiteral":"SAFETY" }, "CustomerDetail":{ "NeedsMemberConsent":"false", "ConsentBeginDate":"", "ConsentEndDate":"", "MemberConsentStatus":"", "CMRStatus":"No", "CMRStatusLiteral":"No" }, "Addresses":{ "Address":[  ] }, "Phones":{ "Phone":[ { "PhoneKey":"6508778", "PhoneType":"AL", "PhoneValue":"5025801000" }, { "PhoneKey":"7909869", "PhoneType":"AL", "PhoneValue":"5025805807" }, { "PhoneKey":"7909865", "PhoneType":"AL", "PhoneValue":"1234567890" }, { "PhoneKey":"7910030", "PhoneType":"AL", "PhoneValue":"5225845822" }, { "PhoneKey":"7910053", "PhoneType":"AL", "PhoneValue":"9952012417" }, { "PhoneKey":"7910124", "PhoneType":"AL", "PhoneValue":"5445678722" }, { "PhoneKey":"7910055", "PhoneType":"AL", "PhoneValue":"4025805676" }, { "PhoneKey":"7910038", "PhoneType":"AL", "PhoneValue":"9950012411" }, { "PhoneKey":"7910127", "PhoneType":"AL", "PhoneValue":"5445676644" }, { "PhoneKey":"7910075", "PhoneType":"CN", "PhoneValue":"6767888911" }, { "PhoneKey":"7910024", "PhoneType":"AL", "PhoneValue":"4025805122" }, { "PhoneKey":"7910071", "PhoneType":"AL", "PhoneValue":"9950012412" }, { "PhoneKey":"7909866", "PhoneType":"AL", "PhoneValue":"5025801002" }, { "PhoneKey":"7910067", "PhoneType":"AL", "PhoneValue":"4025805811" }, { "PhoneKey":"7910074", "PhoneType":"CN", "PhoneValue":"6767888999" }, { "PhoneKey":"7910135", "PhoneType":"PP", "PhoneValue":"5445556767" }, { "PhoneKey":"7910084", "PhoneType":"AL", "PhoneValue":"5445678784" }, { "PhoneKey":"7910034", "PhoneType":"AL", "PhoneValue":"4025805678" }, { "PhoneKey":"7910070", "PhoneType":"AL", "PhoneValue":"5445342111" }, { "PhoneKey":"7910006", "PhoneType":"AL", "PhoneValue":"4025805802" }, { "PhoneKey":"7910040", "PhoneType":"AL", "PhoneValue":"4025805555" }, { "PhoneKey":"7910025", "PhoneType":"AL", "PhoneValue":"8978989898" }, { "PhoneKey":"7910036", "PhoneType":"AL", "PhoneValue":"4025805801" }, { "PhoneKey":"7910023", "PhoneType":"AL", "PhoneValue":"5225845888" }, { "PhoneKey":"7909867", "PhoneType":"AL", "PhoneValue":"5025800000" }, { "PhoneKey":"7909868", "PhoneType":"AL", "PhoneValue":"5025805800" }, { "PhoneKey":"7910068", "PhoneType":"AL", "PhoneValue":"9952012418" }, { "PhoneKey":"7909870", "PhoneType":"AL", "PhoneValue":"4025805807" }, { "PhoneKey":"7909886", "PhoneType":"AL", "PhoneValue":"5025805855" }, { "PhoneKey":"7910049", "PhoneType":"AL", "PhoneValue":"4025805672" }, { "PhoneKey":"7910086", "PhoneType":"AL", "PhoneValue":"5445678721" }, { "PhoneKey":"7910044", "PhoneType":"AL", "PhoneValue":"4025805701" }, { "PhoneKey":"7910128", "PhoneType":"AL", "PhoneValue":"5445676643" }, { "PhoneKey":"7910061", "PhoneType":"AL", "PhoneValue":"4025805778" }, { "PhoneKey":"7910125", "PhoneType":"AL", "PhoneValue":"5445676622" }, { "PhoneKey":"7910021", "PhoneType":"AL", "PhoneValue":"5225845999" }, { "PhoneKey":"7910032", "PhoneType":"AL", "PhoneValue":"4025805675" }, { "PhoneKey":"7910033", "PhoneType":"AL", "PhoneValue":"4025805702" }, { "PhoneKey":"7910077", "PhoneType":"CN", "PhoneValue":"6767888998" }, { "PhoneKey":"7910060", "PhoneType":"AL", "PhoneValue":"5225845851" }, { "PhoneKey":"7910058", "PhoneType":"AL", "PhoneValue":"9950012415" }, { "PhoneKey":"7909887", "PhoneType":"AL", "PhoneValue":"5025845855" }, { "PhoneKey":"7910022", "PhoneType":"AL", "PhoneValue":"9950012413" }, { "PhoneKey":"7910059", "PhoneType":"AL", "PhoneValue":"4025805674" }, { "PhoneKey":"7910062", "PhoneType":"AL", "PhoneValue":"4025805777" }, { "PhoneKey":"7910063", "PhoneType":"AL", "PhoneValue":"5225845834" }, { "PhoneKey":"7910054", "PhoneType":"AL", "PhoneValue":"4025805111" }, { "PhoneKey":"7910076", "PhoneType":"CN", "PhoneValue":"6767888912" }, { "PhoneKey":"7910065", "PhoneType":"AL", "PhoneValue":"5225845858" }, { "PhoneKey":"7910042", "PhoneType":"AL", "PhoneValue":"5225845811" }, { "PhoneKey":"7910045", "PhoneType":"AL", "PhoneValue":"5225845856" }, { "PhoneKey":"7910066", "PhoneType":"AL", "PhoneValue":"4025805673" }, { "PhoneKey":"7910078", "PhoneType":"CN", "PhoneValue":"8976768787" }, { "PhoneKey":"7909888", "PhoneType":"AL", "PhoneValue":"5225845855" }, { "PhoneKey":"7910004", "PhoneType":"AL", "PhoneValue":"5225845852" }, { "PhoneKey":"7910083", "PhoneType":"AL", "PhoneValue":"5445678785" }, { "PhoneKey":"7910129", "PhoneType":"CN", "PhoneValue":"8976768786" }, { "PhoneKey":"7910037", "PhoneType":"AL", "PhoneValue":"5225845857" }, { "PhoneKey":"7910069", "PhoneType":"AL", "PhoneValue":"9952012413" }, { "PhoneKey":"7910052", "PhoneType":"AL", "PhoneValue":"5445678787" }, { "PhoneKey":"7910017", "PhoneType":"AL", "PhoneValue":"4025805803" } ] }, "StaleData":"0", "AccountNumber":{ "CustomerValue":"6727721" }, "Finance":{ "CreditLimit":"30.01", "OutstandingBalance":"2.99", "PaymentSchedules":{ "PaymentSchedule":[ { "PaymentDueDate":"06/02/2015", "PaymentMadeDate":"06/02/2015", "PaymentType":"OT", "SourceApplicationCode":"SIL", "SourceApplicationCodeLiteral":"SILVERLINK" }, { "PaymentDueDate":"05/09/2016", "PaymentMadeDate":"05/09/2016", "PaymentType":"OT", "SourceApplicationCode":"ANS", "SourceApplicationCodeLiteral":"EPOSTRX" }, { "PaymentDueDate":"04/27/2016", "PaymentMadeDate":"05/09/2016", "PaymentType":"OT", "SourceApplicationCode":"ANS", "SourceApplicationCodeLiteral":"EPOSTRX" } ] } }, "Allergies":{ "Allergy":[ { "HealthKey":"00153", "HealthDescription":"No Known Drug Allergy" } ] }, "CreditCards":{ "CreditCard":[  ] }, "HealthConditions":{ "HealthCondition":[ { "HealthDescription":"Thyroid Disease" }, { "HealthDescription":"No Known Medical History" } ] }, "LogNotes":{ "LogNote":[ { "LogNoteCode":"90 DAY SUPPLY COLD PACK NO RESPONSE", "LogNoteDate":"01/23/2017", "LogNoteKey":"64665338", "LogNoteMessage":"update for testing 90CPNR", "LogNoteUser":"SXA1954" }, { "LogNoteCode":"90 DAY SUPPLY COLD PACK NO RESPONSE", "LogNoteDate":"02/15/2017", "LogNoteKey":"64665770", "LogNoteMessage":"update for testing 90CPNR", "LogNoteUser":"SXA1954" }, { "LogNoteCode":"OTHER", "LogNoteDate":"02/17/2017", "LogNoteKey":"64665798", "LogNoteMessage":"update for testing 90CPNR", "LogNoteUser":"SXA1954" } ] }, "Plans":{ "Plan":[ { "PlanEffectiveDate":"", "PlanTerminationDate":"", "PlanType":"CASH RX", "Payer":"Humana" }, { "PlanEffectiveDate":"05/05/2015", "PlanTerminationDate":"", "PlanType":"MEDIMPACT OTC", "Payer":"Humana" }, { "PlanEffectiveDate":"05/05/2015", "PlanTerminationDate":"12/31/9999", "PlanType":"MA", "Payer":"HUMANA INSURANCE COMPANY" } ] } } ] } } }';
            resDemographic.setBody(reqCEResponse);
            system.assert(resDemographic.getBody() == reqCEResponse );
            //Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
            String reqType = '';
            // mapDetails = continueObj.getRequests();
            String reqCEEdit = '';
            List < String > labelGetmember = new List < String > ();
            labelGetmember = new List<String>();
            labelGetmember.add(null);
            Test.setContinuationResponse(reqCEEdit,resDemographic);
            Pharmacy_CreateEditOrder_C_HUM.parseCreateEditOrderResponse(labelGetmember,null); 
              Test.stopTest();
            }
            }
        /*
    * Method Name : ExceptiontestPharmacyCreateEditOrder
    * Description : This method Is used To test the Pharmacy Create Edit Order funtionality
    * Return type : None
    * Parameter   : None
    */
    Static testMethod  void ExceptiontestPharmacyCreateEditOrder_getEditMember()
        {
            User testUser = [Select Id, Name from User where LastName='test User1'];
            policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
            List<HUM_Webservice_Callout__c > lstWebCallout = [Select id from HUM_Webservice_Callout__c];
            System.runAs(testUser)
            {
                Test.startTest();
                //card details
                PageReference tpageRef = page.Pharmacy_CreateEditOrder_VF_HUM;
            Test.setCurrentPage(tpageRef);
            ApexPages.currentPage().getParameters().put('CEflag','editOrderSummary');
            ApexPages.CurrentPage().getParameters().put('orderNo','567890');
            ApexPages.CurrentPage().getParameters().put('EnterpriseID','978563478956');
            ApexPages.CurrentPage().getParameters().put('osStartDate','12233455');
            ApexPages.CurrentPage().getParameters().put('osEndDate','5345345');
            ApexPages.CurrentPage().getParameters().put('payer','test');
            ApexPages.CurrentPage().getParameters().put('scripts','test');
            Pharmacy_CreateEditOrder_C_HUM pc = new Pharmacy_CreateEditOrder_C_HUM();
            Pharmacy_CreateEditOrder_D_HUM pd = new Pharmacy_CreateEditOrder_D_HUM();
            Pharmacy_CreateEditOrder_S_HUM ps = new Pharmacy_CreateEditOrder_S_HUM();
            delete lstWebCallout;
            
                HttpResponse resGetMember = new Httpresponse();
            HttpResponse resGetScript = new Httpresponse();
            Continuation continueObj;
            continueObj = (Continuation)Pharmacy_CreateEditOrder_C_HUM.getMemberAndScriptData('test','test','test','test','RXK4284','0015C000002T9uJ');
            String Demog = '{"GetMemberReponse":{"Customers":{"Customer":[{"StaleData":"","SpecialtyVariantObjects":{"CapType":"S"},"Plans":{"Plan":[{"PlanType":"CASH RX","PlanTerminationDate":"","PlanEffectiveDate":""},{"PlanType":"MEDIMPACT OTC","PlanTerminationDate":"","PlanEffectiveDate":"07/29/2015"},{"PlanType":"MA","PlanTerminationDate":"","PlanEffectiveDate":"07/29/2015"}]},"Phones":{"Phone":[{"PhoneValue":"5025801000","PhoneType":"PP","PhoneKey":"7292489"}]},"Payment":{"NonCreditCard":{"Type":"","PaymentNumber":""}},"LogNotes":{"LogNote":[]},"HealthConditions":{"HealthCondition":[{"HealthDescription":"NO KNOWN MEDICAL HISTORY"}]},"Finance":{"PaymentSchedules":{"PaymentSchedule":[]},"OutstandingBalance":"","CreditLimit":"","Adjustments":{"Adjustment":[]}},"CustomerIdentity":{"MiddleName":"","LastName":"DAGGETT","FirstName":"KATHLEEN","Email":"gsheppard@humana.com","CustomerKeys":{"CustomerKey":[{"CustomerValue":"4193538","CustomerType":"PK"},{"CustomerValue":"1031063837","CustomerType":"PM"},{"CustomerValue":"6895629","CustomerType":"AC"}]},"BirthDate":"09/12/1944"},"CustomerDetails":null,"CreditCards":{"CreditCard":[{"IsAutoCharge":"false","IsActive":"true","ExpirationYear":"2018","ExpirationMonth":"11","CreditCardType":"M","CreditCardLast4Digits":"4315830000415489"},{"IsAutoCharge":"false","IsActive":"true","ExpirationYear":"2018","ExpirationMonth":"11","CreditCardType":"M","CreditCardLast4Digits":"4315830000415494"},{"IsAutoCharge":"false","IsActive":"true","ExpirationYear":"2018","ExpirationMonth":"11","CreditCardType":"M","CreditCardLast4Digits":"4315830000415497"},{"IsAutoCharge":"false","IsActive":"true","ExpirationYear":"2016","ExpirationMonth":"10","CreditCardType":"V","CreditCardLast4Digits":"3371953113715499"},{"IsAutoCharge":"false","IsActive":"true","ExpirationYear":"2018","ExpirationMonth":"11","CreditCardType":"M","CreditCardLast4Digits":"4315830000415498"}]},"Allergies":{"Allergy":[{"HealthKey":"00153","HealthDescription":"NO KNOWN DRUG ALLERGY"}]},"Addresses":{"Address":[{"ZipCode":"95404","StateCode":"CA","IsActive":"true","City":"SANTA ROSA","AddressTypeLiteral":"","AddressType":"A","AddressLine2":"","AddressLine1":"3227 CANYON CREEK LN"},{"ZipCode":"32615","StateCode":"FL","IsActive":"true","City":"ALACHUA","AddressTypeLiteral":"","AddressType":"P","AddressLine2":"","AddressLine1":"VOX90"},{"ZipCode":"75124","StateCode":"TX","IsActive":"true","City":"EUSTACE","AddressTypeLiteral":"","AddressType":"B","AddressLine2":"","AddressLine1":"13146 LAKE LN"},{"ZipCode":"75124","StateCode":"TX","IsActive":"true","City":"EUSTACE","AddressTypeLiteral":"","AddressType":"S","AddressLine2":"","AddressLine1":"13146 LAKE LN"},{"ZipCode":"75124","StateCode":"TX","IsActive":"true","City":"EUSTACE","AddressTypeLiteral":"","AddressType":"A","AddressLine2":"","AddressLine1":"13146 LAKE LNT"}]},"AccountNumber":{"CustomerValue":"6895629"}}]}}}';
            resGetMember.setBody(Demog);
            system.assert(resGetMember.getBody() == Demog);
            String script = '{"GetScriptsResponse": {   "EnterprisePersonID": "1048868178",   "Scripts": {"Script": [   {      "ConsentStatus": "CA",      "ConsentDateTime": "09/11/2015",      "ScriptKey": "242058738",      "ScriptStatus": "O",      "ScriptStatusLiteral": "OPEN",      "IsAutoRefill": "false",      "AutoRefillEligible": "false",      "DrugType": "STANDARD",      "Items": {"Item":       [                  {            "ItemType": "W",            "ItemLabel": "METOPROLOL TART  25MG TAB"         },                  {            "ItemType": "D",            "ItemLabel": "METOPROLOL TART  25MG TAB"         }      ]},      "Quantity": "90",      "RefillsRemaining": "1",      "DaysSupply": "90",      "LastFillDate": "09/14/2015",      "NextFillDate": "02/21/2017",      "NextFillMinDate": "02/21/2017",      "ScriptExpirationDate": "09/10/2016",      "physician":       {         "FirstName": "STEVEN",         "LastName": "KOERTH",         "Phones": {"Phone":          [                        {               "PhoneType": "WP",             "PhoneValue": "2102001345"            },                        {               "PhoneType": "WP",               "PhoneValue": "2102001345"            },                        {               "PhoneType": "FN",               "PhoneValue": "2102001345"            },                        {               "PhoneType": "FN",               "PhoneValue": "2102001345"            }         ]}      }   }]}}}';
            resGetScript.setBody(script);
            system.assert(resGetScript.getBody() == script);
            String reqType = '';
            String reqCE1 = '';
        String reqCE2 = '';
            List < String > labelGetmember = new List < String > ();
            labelGetmember.add(null );
            labelGetmember.add(null);
            Test.setContinuationResponse(reqCE1 ,resGetMember);
            Test.setContinuationResponse(reqCE2,resGetScript);
            Pharmacy_CreateEditOrder_C_HUM.parseMemberAndScriptResponse(labelGetmember,null);
            continueObj = (Continuation)Pharmacy_CreateEditOrder_C_HUM.invokeLogNoteRequest(2,'test','test','test');
            String responseStr = 'test';
            resGetScript.setBody(responseStr);
            labelGetmember = new List<String>();
            labelGetmember.add(null);
            String id = null;
            Test.setContinuationResponse(reqCE1,resGetMember);
            Pharmacy_CreateEditOrder_C_HUM.logNoteResponse(labelGetmember,id);
                Test.stopTest();
         }
    }
	/*
    * Method Name : testCreateGetOrderDetail
    * Description : This method Is used To test positive scenario of createGetOrderDetailRequest
    * Return type : None
    * Parameter   : None
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
           
            }

    }
        
      /*
    * Method Name : testCreateGetOrderDetail
    * Description : This method Is used To test Negative scenario of createGetOrderDetailRequest
    * Return type : None
    * Parameter   : None
    */
    static testMethod void testNegativeEPostOrder()    
    {
    User testUser = [Select Id, Name from User where LastName='test User1'];
            policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
            List<HUM_Webservice_Callout__c > lstWebCallout = [Select id from HUM_Webservice_Callout__c];
            System.runAs(testUser)
            {
                Test.startTest();
                //card details
                PageReference tpageRef = page.Pharmacy_CreateEditOrder_VF_HUM;
            Test.setCurrentPage(tpageRef);
            ApexPages.StandardController scon = new ApexPages.StandardController(policyObj);
            Pharmacy_CreateEditOrder_C_HUM pc = new Pharmacy_CreateEditOrder_C_HUM();
            Pharmacy_CreateEditOrder_D_HUM pd = new Pharmacy_CreateEditOrder_D_HUM();
            Pharmacy_CreateEditOrder_S_HUM ps = new Pharmacy_CreateEditOrder_S_HUM();
            Continuation continueObj = (Continuation)Pharmacy_CreateEditOrder_C_HUM.invokeEpostOrder('1070125929','nxc1262','0015C000002T9uJ');
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

            Pharmacy_CreateEditOrder_C_HUM.parseRSGetOrderResponse(labelPostOrder,State);
            Test.stopTest();
    }
    }
    
      /*
    * Method Name : testCreateGetOrderDetail
    * Description : This method Is used To test positive scenario of invokeEPostOrder
        * Return type : None
    * Parameter   : None
    */
      static testMethod void testEPostOrder()    
    {
    User testUser = [Select Id, Name from User where LastName='test User1'];
            policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
            List<HUM_Webservice_Callout__c > lstWebCallout = [Select id from HUM_Webservice_Callout__c];
            System.runAs(testUser)
            {
                Test.startTest();
                //card details
                PageReference tpageRef = page.Pharmacy_CreateEditOrder_VF_HUM;
            Test.setCurrentPage(tpageRef);
            ApexPages.StandardController scon = new ApexPages.StandardController(policyObj);
            Pharmacy_CreateEditOrder_C_HUM pc = new Pharmacy_CreateEditOrder_C_HUM();
            Pharmacy_CreateEditOrder_D_HUM pd = new Pharmacy_CreateEditOrder_D_HUM();
            Pharmacy_CreateEditOrder_S_HUM ps = new Pharmacy_CreateEditOrder_S_HUM();

            Continuation continueObj = (Continuation)Pharmacy_CreateEditOrder_C_HUM.invokeEpostOrder('1070125929','nxc1262','0015C000002T9uJ');
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

            Pharmacy_CreateEditOrder_C_HUM.parseRSGetOrderResponse(labelPostOrder,State);
            Test.stopTest();
    }
    }
    
      /*
    * Method Name : testCreateGetOrderDetail
    * Description : This method Is used To test positive scenario of invokeGetMemServiceFinance
        * Return type : None
    * Parameter   : None
    */
    static testMethod void testGetMember()    
    {
            User testUser = [Select Id, Name from User where LastName='test User1'];
            policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
            List<HUM_Webservice_Callout__c > lstWebCallout = [Select id from HUM_Webservice_Callout__c];
            System.runAs(testUser)
            {
                Test.startTest();
                //card details
                PageReference tpageRef = page.Pharmacy_CreateEditOrder_VF_HUM;
            Test.setCurrentPage(tpageRef);
            ApexPages.StandardController scon = new ApexPages.StandardController(policyObj);
            Pharmacy_CreateEditOrder_C_HUM pc = new Pharmacy_CreateEditOrder_C_HUM();
            Pharmacy_CreateEditOrder_D_HUM pd = new Pharmacy_CreateEditOrder_D_HUM();
            Pharmacy_CreateEditOrder_S_HUM ps = new Pharmacy_CreateEditOrder_S_HUM();

            Continuation continueObj = (Continuation)Pharmacy_CreateEditOrder_C_HUM.invokeGetMemServiceFinance('1070125929','nxc1262','0015C000002T9uJ');
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

            Pharmacy_CreateEditOrder_C_HUM.parseRSGetMemberResponseFinance(labelGetMember,State);
            Test.stopTest();

            }
    }

    /*
    * Method Name : testCreateGetOrderDetail
    * Description : This method Is used To test negative scenario of invokeGetMemServiceFinance
        * Return type : None
    * Parameter   : None
    */

    static testMethod void testNegativeGetMember()    
    {
            User testUser = [Select Id, Name from User where LastName='test User1'];
            policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
            List<HUM_Webservice_Callout__c > lstWebCallout = [Select id from HUM_Webservice_Callout__c];
            System.runAs(testUser)
            {
                Test.startTest();
                //card details
                PageReference tpageRef = page.Pharmacy_CreateEditOrder_VF_HUM;
            Test.setCurrentPage(tpageRef);
            ApexPages.StandardController scon = new ApexPages.StandardController(policyObj);
            Pharmacy_CreateEditOrder_C_HUM pc = new Pharmacy_CreateEditOrder_C_HUM();
            Pharmacy_CreateEditOrder_D_HUM pd = new Pharmacy_CreateEditOrder_D_HUM();
            Pharmacy_CreateEditOrder_S_HUM ps = new Pharmacy_CreateEditOrder_S_HUM();
            Continuation continueObj = (Continuation)Pharmacy_CreateEditOrder_C_HUM.invokeGetMemServiceFinance('1070125929','nxc1262','0015C000002T9uJ');
            continueObj.continuationMethod = 'SendRequestGetMemberResponseFinance';
            Object State='0015C000002T9uJ';
            Httpresponse httpRes = new Httpresponse ();            
            string memberResponse= '';
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
            Pharmacy_CreateEditOrder_C_HUM.parseRSGetMemberResponseFinance(labelGetMember,State);
            Test.stopTest();

            }
    }

    /*
    * Method Name : testPharmacyEditMember_Order_DTO_T_HUM
    * Description : This method Is used To test testPharmacyEditMember_Order_DTO_HUM
        * Return type : None
    * Parameter   : None
    */
    static testMethod void testPharmacyEditMember_Order_DTO_T_HUM()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser)
        {
          Test.StartTest();
              
              PharmacyEditMember_Order_DTO_HUM response=new PharmacyEditMember_Order_DTO_HUM();
              PharmacyEditMember_Order_DTO_HUM.PharmacyEditMember_Order_wrapper_HUM dtoEdit=new PharmacyEditMember_Order_DTO_HUM.PharmacyEditMember_Order_wrapper_HUM();
              system.assert(Response !=null);
          Test.StopTest();
        }
    }
}