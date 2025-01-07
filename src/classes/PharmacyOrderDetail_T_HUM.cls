/*******************************************************************************************************************************
Apex Class Name : PharmacyOrderDetail_T_HUM.cls
Version         : 1.0
Created On      : Apr 22,2017
Function        : Class contains test methods PharmacyOrderDetail_T_HUM class.

Modification Log: 
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Avinash Choubey           87810	                    04/22/2017                 Original Version
* Rajesh Keswani           112036	                    07/20/2017                 Implementation of REQ - 323389 - adding networkId in service request
* AshokKumar Nutalapati    319140                       01/10/2019                 REQ - 377476: Adding 'Order Total' and 'Order Tax' Fields to the Order Details Page (RxRF) 
* AshokKumar Nutalapati    338598                       02/21/2019                 Defect Fix 402874.0001
* Ravi Prakash kantamneni  345989                       03/21/2019                 REQ-383718 - Automatically Record Order Details to Case: 'Save & Log' to New Case & Existing Case(RXRF)
* Riya Agarwal								            08/18/2020			       REQ - 760189-PR00091606 - MF 4 - Finance Code Order Detail Page
* AshokKumar Nutalapati                     			09/01/2020                 REQ - 1327436 - Display OMS Originated Notes & Case Comments on the Order Details Page(RxRF)
* AshokKumar Nutalapati                                 01/19/2021                 REQ - 1826594 Display OMS Originated Notes & Case Comments on the Order Details Page - Query Null Check (CRMS)
* Pallavi Shewale					                    06/08/2021                 US-1491784 Changes - Finance Queue
* Prasuna Pattabhi                           07/31/2023                 Test Class error fix - build fix
********************************************************************************************************************************/
@isTest
private class PharmacyOrderDetail_T_HUM {
/*
* Method Name : setUpTestData
* Description : This method creates test data for test class
* Return type : None
* Parameter   : None
*/
    @testSetup static void setUpTestData()
    {
        User objUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');   
        insert objUser;
        HUM_Webservice_Callout__c RighSourceOrderServiceDetails = new HUM_Webservice_Callout__c(Name = 'RighSourceService_GetOrders',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c ='https://int-crmservicews.humana.com/RighSourceService_GetMember/' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
        insert RighSourceOrderServiceDetails;
        HUM_Webservice_Callout__c RighSourceOrderDetailServiceDetails = new HUM_Webservice_Callout__c(Name = 'RighSourceService_GetOrderDetails',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c ='https://int-crmservicews.humana.com/RighSourceService_GetMember/' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
        insert RighSourceOrderDetailServiceDetails;
        HUM_Webservice_Callout__c RighSourceCreateEditOrderServiceDetails = new HUM_Webservice_Callout__c(Name = 'RighSourceService_CreateAndEditOrder',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c ='https://int-crmservicews.humana.com/RighSourceService_GetMember/' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
        insert RighSourceCreateEditOrderServiceDetails ;
		CRMFunctionality_ONOFF_Switch__c switchDetails = new CRMFunctionality_ONOFF_Switch__c(Name='760189',IsON__c=true,Release_Name__c='09/11/2020',Scrum_Team__c='Lego',Story_Title__c='This switch to enable or disable the functionality of Finance Queue');
        insert switchDetails;
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

            
            List<Classification__c> lstClassification = New List<Classification__c>();
            Classification__c classificationRec = New Classification__c();
            classificationRec.Name = 'Benefits';
            classificationRec.Is_Active__c = true;
            lstClassification.add(classificationRec);
            
            Classification__c classificationRecDec = New Classification__c();
            classificationRecDec.Name = 'Humana Pharmacy';
            classificationRecDec.Is_Active__c = true;
            lstClassification.add(classificationRecDec);
            
            Classification__c classificationRecDec1 = New Classification__c();
            classificationRecDec1.Name = 'G&A';
            classificationRecDec1.Is_Active__c = true;
            lstClassification.add(classificationRecDec1);

            Classification__c classificationRecDec2 = New Classification__c();
            classificationRecDec2.Name = 'Rx';
            classificationRecDec2.Is_Active__c = true;
            lstClassification.add(classificationRecDec2);
            insert lstClassification;
            system.assertEquals(lstClassification.size(),4);
            
            List<Intent__c> lstIntent = New List<Intent__c>();
            Intent__c intentRec = New Intent__c();
            intentRec.Name = 'Rewards';
            intentRec.Is_Active__c = true;
            lstIntent.add(intentRec);
            
            Intent__c intentRecDec = New Intent__c();
            intentRecDec.Name = 'Estimate';
            intentRecDec.Is_Active__c = true;
            lstIntent.add(intentRecDec);
            
            
            Intent__c intentRecDec1 = New Intent__c();
            intentRecDec1.Name = 'Expedited';
            intentRecDec1.Is_Active__c = true;
            lstIntent.add(intentRecDec1);
        
            
            Intent__c intentRecDec2 = New Intent__c();
            intentRecDec2.Name = 'Part D Redetermination';
            intentRecDec1.Is_Active__c = true;
            lstIntent.add(intentRecDec2);
            insert lstIntent;
            system.assertEquals(lstIntent.size(),4);
			
			policy_member__c policyObjcase = [select id from policy_member__c where Relationship__c = 'self' limit 1];
            Case caseRecord = New Case();
            caseRecord.Owner_Queue__c = 'Louisville RSO Calls Support';
            caseRecord.Status = 'In Progress';
            caseRecord.AccountId = objMemAccount.id;
            caseRecord.Classification_Type__c = 'calls';
            caseRecord.Classification_id__c=lstClassification[0].id;
            caseRecord.Intent_id__c = lstIntent[0].id;
            caseRecord.Interacting_With__c = objMemAccount.id;
            caseRecord.Interacting_With_Type__c = 'Member';
            caseRecord.Policy_Member__c = policyObjcase.Id;
            caseRecord.Call_Benefit_Category__c = 'Medicare Part C';
            caseRecord.Autoroute_Status__c = 'Evaluate';
            caseRecord.Origin = 'Inbound Call';
            caseRecord.Interacting_About_Type__c = 'Member';
            insert caseRecord;
            
            CaseComment objCaseComment = New CaseComment();
            objCaseComment.ParentId = caseRecord.Id;
            objCaseComment.CommentBody = 'Placed order 114238192 for the following: Rx# 312673017/ATORVASTATIN 10MG TAB (10),Rx# 312673017/ATORVASTATIN 10MG TAB (10). Payment Method: Invoice, Shipping Address: 7491 PINNICKINICK STREET,SANDY OR 97055, Release Date: 03/22/2019';
            insert objCaseComment;

        }
    }  

/*
* Method Name : testPharmacyDemographic
* Description : This method is used to test the Pharmacy Demographic funtionality
* Return type : None
* Parameter   : None
*/
  static testMethod void testPharmacyDemographic()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.PharmacyOrderDetails_VF_HUM);
            ApexPages.currentPage().getParameters().put('isDetail','true');  
   	   		ApexPages.currentPage().getParameters().put('capType','S');
	   	   	ApexPages.currentPage().getParameters().put('orderNumber','114238192');
	   	    ApexPages.currentPage().getParameters().put('orderTotal','13');
            ApexPages.currentPage().getParameters().put('orderCopay','12');
            ApexPages.currentPage().getParameters().put('orderTax','1');   	   
   	    	ApexPages.currentPage().getParameters().put('ostartDate','test');   	   
			ApexPages.currentPage().getParameters().put('oEndDate','test');   	   
			ApexPages.currentPage().getParameters().put('enterPriseID','1065590075');   	   
			ApexPages.currentPage().getParameters().put('memId',policyObj.id);   	   
			ApexPages.currentPage().getParameters().put('webOrderNumber','2131244,1232');
			ApexPages.currentPage().getParameters().put('OcreationDate','test');
			ApexPages.currentPage().getParameters().put('OTC','yes');
			ApexPages.currentPage().getParameters().put('oReleaseDate','test');
			ApexPages.currentPage().getParameters().put('CurrentQueue','test');   
			ApexPages.currentPage().getParameters().put('source','Summary'); 
			ApexPages.currentPage().getParameters().put('payer','humana');
			ApexPages.currentPage().getParameters().put('pmId',policyObj.id);
			   	   
			   	   
            PharmacyOrderDetail_C_HUM objPharmacy = new PharmacyOrderDetail_C_HUM();
            ApexPages.StandardController scon = new ApexPages.StandardController(policyObj);
            // getOrder
           	Continuation  continueObj = (Continuation)PharmacyOrderDetail_C_HUM.getOrderData('114238192','Detail','test','test','1065590075','RXK4284','0015C000002T9uJ');
            continueObj.continuationMethod = 'ProcessOrderResponse';
            //String ordDetailres = '{ "GetOrderDetailResponse":{ "OrderKey":"114238192", "ExternalOrderKey":"", "OrderCreationDate":"12/29/2016", "OrderShipDate":"", "CurrentQueue":"DR. CALL TECH FAX", "CurrentQueueDate":"2016-12-29 09:00:32:650", "OrderSource":"WEB", "CancelReasonCode":"", "CreditCards":{ "CreditCard":[ { "CreditCardKey":"5405401", "CreditCardType":"V", "CreditCardTypeLiteral":"VISA", "FirstName":"THOMAS", "LastName":"ONDERKO", "MiddleName":"", "ExpirationMonth":"10", "ExpirationYear":"2016", "IsActive":"true", "IsAutoCharge":"false", "CreditCardLast4Digits":"5498", "IsSpendingAccount":"false" } ] }, "Queues":{ "Queue":[ { "QueueName":"DR. CALL TECH FAX", "QueueUser":"SYSTEM", "QueueDate":"2016-12-29 09:00:32:653" } ] }, "ShippingInformation":{ "ShippingMethod":"BEST METHOD-REGULAR MAIL", "ShippingTrackingNumber":"", "DeliveryInstructions":"" }, "Addresses":{ "AddressLine1":"13146 LAKE LN", "AddressLine2":"", "AddressName":"", "City":"EUSTACE", "StateCode":"TX", "ZipCode":"75124" }, "FirstName":"FAYEZ", "LastName":"TANNOUS", "OrderItems":[  { "ChangedOrder":  { "ChangedOrderKey": "114242080", "ChangedOrderType": "C","ChangedOrderTypeLiteral": "Child ORDER", "ChangedReasonCode": ""   },"CoPayCost":{ "ItemAmount":"0.0", "TaxAmount":"0.0" }, "PlanCost":{ "ItemAmount":"0.0", "TaxAmount":"0.0" }, "ScriptKey":"242439463", "OrderItemStatus":"O", "OrderItemStatusLiteral":"OPEN", "Items":[ { "ItemType":"W", "ItemLabel":"ATORVASTATIN 20MG TAB", "ExternalOrderKey":"" }, { "ItemType":"D", "ItemLabel":"ATORVASTATIN 20MG TAB", "ExternalOrderKey":"" } ], "Strength":"20" }, { "CoPayCost":{ "ItemAmount":"0.0", "TaxAmount":"0.0" }, "PlanCost":{ "ItemAmount":"0.0", "TaxAmount":"0.0" }, "ScriptKey":"242439154", "OrderItemStatus":"C", "OrderItemStatusLiteral":"CANCELLED", "Items":[ { "ItemType":"W", "ItemLabel":"ATENOLOL  25MG TAB", "ExternalOrderKey":"" }, { "ItemType":"D", "ItemLabel":"ATENOLOL  25MG TAB", "ExternalOrderKey":"" } ], "Strength":"25" }, { "CoPayCost":{ "ItemAmount":"0.0", "TaxAmount":"0.0" }, "PlanCost":{ "ItemAmount":"0.0", "TaxAmount":"0.0" }, "ScriptKey":"242439464", "OrderItemStatus":"O", "OrderItemStatusLiteral":"OPEN", "Items":[ { "ItemType":"W", "ItemLabel":"METFORMIN  500MG TAB", "ExternalOrderKey":"" }, { "ItemType":"D", "ItemLabel":"METFORMIN  500MG TAB", "ExternalOrderKey":"" } ], "Strength":"500" }, { "CoPayCost":{ "ItemAmount":"0.0", "TaxAmount":"0.0" }, "PlanCost":{ "ItemAmount":"0.0", "TaxAmount":"0.0" }, "ScriptKey":"242439804", "OrderItemStatus":"O", "OrderItemStatusLiteral":"OPEN", "Items":[ { "ItemType":"W", "ItemLabel":"GABAPENTIN 600MG TAB", "ExternalOrderKey":"" }, { "ItemType":"D", "ItemLabel":"GABAPENTIN 600MG TAB", "ExternalOrderKey":"" } ], "Strength":"600" } ], "StaleData":"0" } }';
		    //Updated Response for REQ-760189
            String ordDetailres ='{ "GetOrderDetailResponse":{ "OrderKey":"114238192", "ExternalOrderKey":"", "OrderCreationDate":"12/29/2016", "OrderShipDate":"", "CurrentQueue":"DR. CALL TECH FAX", "CurrentQueueDate":"2016-12-29 09:00:32:650", "OrderSource":"WEB", "CancelReasonCode":"", "CreditCards":{ "CreditCard":[ { "CreditCardKey":"5405401", "CreditCardType":"V", "CreditCardTypeLiteral":"VISA", "FirstName":"THOMAS", "LastName":"ONDERKO", "MiddleName":"", "ExpirationMonth":"10", "ExpirationYear":"2016", "IsActive":"true", "IsAutoCharge":"false", "CreditCardLast4Digits":"5498", "IsSpendingAccount":"false" } ] }, "Queues": {"Queue":[{"QueueName": "FAILED CLAIMS","QueueUser": "GENERIC064","QueueDate": "2020-06-15 01:29:33:520","QueueDetails": {"QueueDetail":[{"scriptKey": "355906917","exceptionIdentifier": "CPAY_250","Description": ""},{"scriptKey": "","exceptionIdentifier": "BAL_FCL","Description": ""}]}},{"QueueName": "SCREENING RPH", "QueueUser": "GENERIC064","QueueDate": "2020-06-15 01:30:07:367","QueueDetails": {"QueueDetail":[{"scriptKey": "355906917","exceptionIdentifier": "CPAY_250","Description": ""},{"scriptKey": "","exceptionIdentifier": "BAL_FCL","Description": ""}]}},{"QueueName": "FINANCE","QueueUser": "SYSTEM","QueueDate": "2020-06-15 01:31:34:460","QueueDetails": {"QueueDetail":[{"scriptKey": "355906917","exceptionIdentifier": "CPAY_250","Description": ""},{"scriptKey": "","exceptionIdentifier": "BAL_FCL","Description": ""}]}}]}, "ShippingInformation":{ "ShippingMethod":"BEST METHOD-REGULAR MAIL", "ShippingTrackingNumber":"", "DeliveryInstructions":"" }, "Addresses":{ "AddressLine1":"13146 LAKE LN", "AddressLine2":"", "AddressName":"", "City":"EUSTACE", "StateCode":"TX", "ZipCode":"75124" }, "FirstName":"FAYEZ", "LastName":"TANNOUS", "OrderItems":[  { "ChangedOrder":  { "ChangedOrderKey": "114242080", "ChangedOrderType": "C","ChangedOrderTypeLiteral": "Child ORDER", "ChangedReasonCode": ""   },"CoPayCost":{ "ItemAmount":"0.0", "TaxAmount":"0.0" }, "PlanCost":{ "ItemAmount":"0.0", "TaxAmount":"0.0" }, "ScriptKey":"242439463", "OrderItemStatus":"O", "OrderItemStatusLiteral":"OPEN", "Items":[ { "ItemType":"W", "ItemLabel":"ATORVASTATIN 20MG TAB", "ExternalOrderKey":"" }, { "ItemType":"D", "ItemLabel":"ATORVASTATIN 20MG TAB", "ExternalOrderKey":"" } ], "Strength":"20" }, { "CoPayCost":{ "ItemAmount":"0.0", "TaxAmount":"0.0" }, "PlanCost":{ "ItemAmount":"0.0", "TaxAmount":"0.0" }, "ScriptKey":"242439154", "OrderItemStatus":"C", "OrderItemStatusLiteral":"CANCELLED", "Items":[ { "ItemType":"W", "ItemLabel":"ATENOLOL  25MG TAB", "ExternalOrderKey":"" }, { "ItemType":"D", "ItemLabel":"ATENOLOL  25MG TAB", "ExternalOrderKey":"" } ], "Strength":"25" }, { "CoPayCost":{ "ItemAmount":"0.0", "TaxAmount":"0.0" }, "PlanCost":{ "ItemAmount":"0.0", "TaxAmount":"0.0" }, "ScriptKey":"242439464", "OrderItemStatus":"O", "OrderItemStatusLiteral":"OPEN", "Items":[ { "ItemType":"W", "ItemLabel":"METFORMIN  500MG TAB", "ExternalOrderKey":"" }, { "ItemType":"D", "ItemLabel":"METFORMIN  500MG TAB", "ExternalOrderKey":"" } ], "Strength":"500" }, { "CoPayCost":{ "ItemAmount":"0.0", "TaxAmount":"0.0" }, "PlanCost":{ "ItemAmount":"0.0", "TaxAmount":"0.0" }, "ScriptKey":"242439804", "OrderItemStatus":"O", "OrderItemStatusLiteral":"OPEN", "Items":[ { "ItemType":"W", "ItemLabel":"GABAPENTIN 600MG TAB", "ExternalOrderKey":"" }, { "ItemType":"D", "ItemLabel":"GABAPENTIN 600MG TAB", "ExternalOrderKey":"" } ], "Strength":"600" } ], "StaleData":"0" } }';
          //Covering Order Detail Response DTO 
			  RightSourceGetOrderDetResponse_DTO_HUM.Queue lstQueue = new RightSourceGetOrderDetResponse_DTO_HUM.Queue();
			  RightSourceGetOrderDetResponse_DTO_HUM.QueueDetail lstQueueDetail = new RightSourceGetOrderDetResponse_DTO_HUM.QueueDetail(); 
			  RightSourceGetOrderDetResponse_DTO_HUM.Addresses objAddress= new RightSourceGetOrderDetResponse_DTO_HUM.Addresses();
			  RightSourceGetOrderDetResponse_DTO_HUM.OrderItems objOrderItems = new RightSourceGetOrderDetResponse_DTO_HUM.OrderItems();
			  RightSourceGetOrderDetResponse_DTO_HUM.ShippingInformation objShippingInformation = new RightSourceGetOrderDetResponse_DTO_HUM.ShippingInformation();
			  RightSourceGetOrderDetResponse_DTO_HUM.CoPayCost objCoPayCost = new RightSourceGetOrderDetResponse_DTO_HUM.CoPayCost();
			  RightSourceGetOrderDetResponse_DTO_HUM.ChangedOrder objChangedOrder= new  RightSourceGetOrderDetResponse_DTO_HUM.ChangedOrder();
			  RightSourceGetOrderDetResponse_DTO_HUM.Items objItems= new RightSourceGetOrderDetResponse_DTO_HUM.Items();
			  RightSourceGetOrderDetResponse_DTO_HUM.CreditCardDetail objCreditCardDetail= new RightSourceGetOrderDetResponse_DTO_HUM.CreditCardDetail();
			  RightSourceGetOrderDetResponse_DTO_HUM.PlanCost objPlanCost= new  RightSourceGetOrderDetResponse_DTO_HUM.PlanCost();
            PharmacyOrderWrapper_DTO_HUM objOrderWrapDTO = new PharmacyOrderWrapper_DTO_HUM (null,null,null,'Detail','114238192','RXK4284','0015C000002T9uJ');
            objOrderWrapDTO.objParentOrder = PharmacyOrderDetail_D_HUM.prepareOrderDetails((RightSourceGetOrderDetResponse_DTO_HUM)System.JSON.deserialize(ordDetailres,RightSourceGetOrderDetResponse_DTO_HUM.class));
            continueObj.state =  objOrderWrapDTO;
            HttpResponse resOrder = new Httpresponse ();
            String orderRes= '{"GetOrdersReponse":{"EnterprisePersonID":"1065590075","Orders":{"Order":[{"OrderNumber":"114238192","OrderCreationDate":"08/05/2016","OrderReleaseDate":"","OrderSource":"MAIL","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"90476811","OrderCreationDate":"01/05/2015","OrderReleaseDate":"","OrderSource":"MAIL","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"114234280","OrderCreationDate":"03/22/2016","OrderReleaseDate":"","OrderSource":"MAIL","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"104081567","OrderCreationDate":"06/30/2015","OrderReleaseDate":"","OrderSource":"CCP/GCP","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"97044257","OrderCreationDate":"03/31/2015","OrderReleaseDate":"","OrderSource":"CCP/GCP","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"114234382","OrderCreationDate":"03/23/2016","OrderReleaseDate":"","OrderSource":"MAIL","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"114234293","OrderCreationDate":"03/22/2016","OrderReleaseDate":"","OrderSource":"MAIL","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"114234381","OrderCreationDate":"03/23/2016","OrderReleaseDate":"","OrderSource":"MAIL","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"114202926","OrderCreationDate":"09/26/2015","OrderReleaseDate":"","OrderSource":"IVR","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"114234289","OrderCreationDate":"03/22/2016","OrderReleaseDate":"Test","OrderSource":"MAIL","QueueName":"test","ExternalOrderKey":"test","OrderTotalAmount":"test","OrderpayAmount":"test","OrderTaxAmount":"test","StaleData":"test"}]}}}';
            resOrder.setBody(orderRes);
            HttpResponse resOrderDet = new Httpresponse ();
            resOrderDet.setBody(ordDetailres);
            system.assert(resOrder.getBody() == orderRes);
            Map<String, HTTPRequest> mapDetails =  continueObj.getRequests();
            List<String> labelGetOrder = new List<string>();
            for(String s : mapDetails.keyset())
            {
               labelGetOrder.add(s);
            } 
           
            Test.setContinuationResponse(labelGetOrder[0],resOrder);
            Test.setContinuationResponse(labelGetOrder[1],resOrderDet);
        	Continuation con1 =  (Continuation) PharmacyOrderDetail_C_HUM.ProcessOrderResponse(labelGetOrder,objOrderWrapDTO);
         	con1.continuationMethod = 'processChildOrderDetails';
            objOrderWrapDTO = new PharmacyOrderWrapper_DTO_HUM (null,null,null,'Detail','114238192','RXK4284','0015C000002T9uJ');
            con1.state =  objOrderWrapDTO;
            HttpResponse resOrderChild = new Httpresponse ();
            resOrderChild.setBody(ordDetailres);
            system.assert(resOrderChild.getBody() == ordDetailres);
             mapDetails =  con1.getRequests();
            labelGetOrder = new List<string>();
            for(String s : mapDetails.keyset())
            {
               labelGetOrder.add(s);
            } 
           
            Test.setContinuationResponse(labelGetOrder[0],resOrderChild);
            PharmacyOrderDetail_C_HUM.processChildOrderDetails(labelGetOrder,objOrderWrapDTO);
            Continuation con2 = (Continuation)PharmacyOrderDetail_C_HUM.sendRequestReleaseAndCancelOrder('112411487', '1048868178', true, 'RXK4284','0015C000002T9uJ', false, false,'376895855');
            con2.continuationMethod = 'processRelOrderResponse';
            con2.state = 'Both';
            Httpresponse  resRelCancel = new Httpresponse ();
            String reqCEResponse = '{" CreateAndEditOrderRequest": {"EnterprisePersonId":"1048868178","orderKey":"112411487","orderPlatform":"Trad","cancelOrder":"True","cancelReasonCode":""}}';
            resRelCancel.setBody(reqCEResponse  );
            system.assert(resRelCancel.getBody() == reqCEResponse );
            mapDetails = con2.getRequests();
            string reqCEEdit = '';
            for(String s : mapDetails.keyset())
            {
                reqCEEdit= s;
            } 
            labelGetOrder = new List<string>();
            labelGetOrder.add(reqCEEdit);
            Test.setContinuationResponse(reqCEEdit,resRelCancel);
            PharmacyOrderDetail_C_HUM.processRelOrderResponse(labelGetOrder,continueObj); 
            Continuation  con3 = (Continuation)PharmacyOrderDetail_C_HUM.callGetOrder('114234289','test','test','1065590075','test','RXK4284','0015C000002T9uJ');
            con3.continuationMethod = 'processGetOrderResponse';
            con3.state = new List<String>{'114234289','test','RXK4284','0015C000002T9uJ'};
            HttpResponse resCallOrder = new Httpresponse ();
            String callOrderRes= '{"GetOrdersReponse":{"EnterprisePersonID":"1065590075","Orders":{"Order":[{"OrderNumber":"114238192","OrderCreationDate":"08/05/2016","OrderReleaseDate":"","OrderSource":"MAIL","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"90476811","OrderCreationDate":"01/05/2015","OrderReleaseDate":"","OrderSource":"MAIL","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"114234280","OrderCreationDate":"03/22/2016","OrderReleaseDate":"","OrderSource":"MAIL","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"104081567","OrderCreationDate":"06/30/2015","OrderReleaseDate":"","OrderSource":"CCP/GCP","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"97044257","OrderCreationDate":"03/31/2015","OrderReleaseDate":"","OrderSource":"CCP/GCP","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"114234382","OrderCreationDate":"03/23/2016","OrderReleaseDate":"","OrderSource":"MAIL","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"114234293","OrderCreationDate":"03/22/2016","OrderReleaseDate":"","OrderSource":"MAIL","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"114234381","OrderCreationDate":"03/23/2016","OrderReleaseDate":"","OrderSource":"MAIL","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"114202926","OrderCreationDate":"09/26/2015","OrderReleaseDate":"","OrderSource":"IVR","QueueName":"","ExternalOrderKey":"","OrderTotalAmount":"","OrderpayAmount":"","OrderTaxAmount":"","StaleData":""},{"OrderNumber":"114234289","OrderCreationDate":"03/22/2016","OrderReleaseDate":"Test","OrderSource":"MAIL","QueueName":"test","ExternalOrderKey":"test","OrderTotalAmount":"test","OrderpayAmount":"test","OrderTaxAmount":"test","StaleData":"test"}]}}}';
            resCallOrder.setBody(callOrderRes);
            system.assert(resCallOrder.getBody() == callOrderRes);
             mapDetails =  con3.getRequests();
            List<String> labelCallOrder = new List<string>();
            for(String s : mapDetails.keyset())
            {
               labelCallOrder.add(s);
            } 
           
            Test.setContinuationResponse(labelCallOrder[0],resCallOrder);
            PharmacyOrderDetail_C_HUM.processGetOrderResponse(labelCallOrder, new List<String>{'114234289','test','RXK4284','0015C000002T9uJ'});
           Test.stopTest();
        }
    }
	
	/*
	* Method Name : testMergeCaseCommentsOMSDataDTO
	* Description : This method is used to test the Merging CaseComments OMSData funtionality
	* Return type : None
	* Parameter   : None
	*/
	static testMethod void testMergeCaseCommentsOMSDataDTO()
	{
		User testUser = [Select Id,Name from User where LastName='test User1'];
		policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
		System.runAs(testUser)
		{
			Test.startTest();Test.setCurrentPage(page.PharmacyOrderDetails_VF_HUM);
			ApexPages.currentPage().getParameters().put('oMSNotesDTO','[{"LogNoteCode":"DC TECH OUTBOUND CALLS TASK","LogNoteDate":"11/25/2019","LogNoteKey":"182610953","LogNoteMessage":"Test Oms comments 114238192","LogNoteUser":"User1"}]');
			ApexPages.currentPage().getParameters().put('policyId',policyObj.id);  
			ApexPages.currentPage().getParameters().put('orderNumber','114238192');
			PharmacyOrderDetail_C_HUM objPharmacy = new PharmacyOrderDetail_C_HUM();
			objPharmacy.caseCommentsFailed = true;
			objPharmacy.mergeCaseCommentsOMSDataDTO();
			objPharmacy.sCaseCommentsJSON = null;
			objPharmacy.refreshDetail();
			Test.stopTest();
		}
	}
}