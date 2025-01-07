/**************************************************************************** 
Apex Class Name  : Claim_Send_Statement_LT_HUM
Version          : 1.0  
Created Date     : 07/12/2022
Function         : Test class for Claim_Send_Statement_LC_HUM
****************************************************************************
Modification Log:
* Developer Name                                  Date                       Description
*---------------------------------------------------------------------------------------------------
* Swapnali Sonawane 							  7/12/2022				     US-3406771 Lightning- RTI - Resend functionality
* Anuradha Gajbhe								  03/01/2024				 US-5480525: DF- 8386: Address is not updating on Resend for Provider/Member Claim Statements on Case Pop-up.
***************************************************************************************************/
@isTest
public with sharing class Claim_Send_Statement_LT_HUM {
/*
* Method Name : prepareTestData
* Description : Used to create wrapper class data
* Return type : void
* Parameter   : NA
*/
@testSetup static void prepareTestData() 
{
    User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
    insert oCCSUser;
    system.assertEquals(oCCSUser.LastName,'test User1');
    
    System.runAs (oCCSUser) 
    {
        Account objAcc = HUMTestDataHelperTest.getAccount(); 
        objAcc.FirstName = 'Test';
        objAcc.LastName = 'Account';
        objAcc.Tenant_Id__c = '00';
        objAcc.Enterprise_ID__c = '1005577525';
        objAcc.General_Account__c = true;
        objAcc.PersonHomePhone = '1234567890';
        objAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();
        insert objAcc;
        System.assertEquals(objAcc.Tenant_Id__c,'00');            
        
        
        Account provAccount = new Account( Enterprise_Id__c = '1254785', NPI_ID__c = '1235452151', DBA__c = 'TestProvider', Name = 'Provider Test', ETL_Record_Deleted__c = false , recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Provider').getRecordTypeId());            
        insert provAccount;
        Product2 oProd = new Product2(Name = 'Laptop X200',Segment_Type__c='MCR',ProductCode='MED', Major_LOB__c='MER');
        insert oProd;
        System.assertNotEquals(oProd.Id, NULL, 'Value not matched');        
        
        Interaction__c objInteraction= HUMTestDataHelperTest.getInteraction();
        objInteraction.Interacting_With__c = provAccount.id;
        objInteraction.Interacting_With_type__c = 'Provider';
        objInteraction.Interaction_External_ID__c = '1234567895';
        insert objInteraction;
        
        Interaction_Member__c InteractionMember = new Interaction_Member__c();
        InteractionMember.Interacting_About__c = provAccount.id;
        InteractionMember.Interaction__c = objInteraction.id;
        InteractionMember.Interacting_About_Type__c ='Provider';
        InteractionMember.Interacting_About_Type__c ='Provider';
        insert InteractionMember;
        System.assertNotEquals(InteractionMember, null, 'InteractionMember not created');
        
        Policy__c objpol = HUMTestDataHelperTest.getPolicy();
        objpol.Name='testPolicy';
        objpol.Group_Name__c = objAcc.Id;
        objpol.Major_LOB__c = 'MER';
        objpol.Source_Cust_Cov_Key__c = '123456789';
        objpol.Product__c = oProd.Id;
        objpol.Product__r = oProd;
        objpol.Platform__c = 'LV';
        objpol.Client_Number__c = '57';
        insert objpol;
        system.assertEquals(objpol.Source_Cust_Cov_Key__c,'123456789');

        MemberPlan objMemberPlan = New MemberPlan();
        objMemberPlan.Member = objAcc;
        objMemberPlan.Policy__r = objpol;
        objMemberPlan.Policy__c = objpol.Id;
        objMemberPlan.EffectiveTo = Date.today().addDays(1);//Date.valueOf('2025-12-31');
        objMemberPlan.EffectiveFrom = Date.today().addDays(-1);//Date.valueOf('2015-12-31');
        objMemberPlan.Member_Plan_External_Id__c  = '00|ODS|6925004434817|12345678|34567|2013-07-01';
        objMemberPlan.name = 'testpolmem11';
        objMemberPlan.MemberId = objAcc.Id;
        insert objMemberPlan;
        System.assert(objMemberPlan.Member_Plan_External_Id__c == '00|ODS|6925004434817|12345678|34567|2013-07-01');
        Document document;

        
        HUM_Webservice_Callout__c oHUMTaskDetailService = new HUM_Webservice_Callout__c();
        oHUMTaskDetailService.name='CommunicationReprint_V3';
        oHUMTaskDetailService.Active__c=true;
        oHUMTaskDetailService.Certificate_Name__c='testts';
        oHUMTaskDetailService.End_Point_URL__c='https://testst-cmservicews.hum.com';
        oHUMTaskDetailService.Timeout__c='120000';
        oHUMTaskDetailService.Content_Type__c='application/json';
        oHUMTaskDetailService.Request_Type__c='POST';
        insert oHUMTaskDetailService;
        system.assert(oHUMTaskDetailService.id!=null);
        system.assert(oHUMTaskDetailService.name == 'CommunicationReprint_V3');
        
        HUM_Webservice_Callout__c oClaimReprintService = new HUM_Webservice_Callout__c();
        oClaimReprintService.name='CommunicationReprint_V3';
        oClaimReprintService.Active__c=true;
        oClaimReprintService.Certificate_Name__c='testts';
        oClaimReprintService.End_Point_URL__c='https://testst-cmservicews.hum.com';
        oClaimReprintService.Timeout__c='120000';
        oClaimReprintService.Content_Type__c='application/json';
        oClaimReprintService.Request_Type__c='POST';
        insert oClaimReprintService;
        system.assert(oClaimReprintService.id!=null);
        system.assert(oClaimReprintService.name == 'CommunicationReprint_V3');
        
        HUM_Webservice_Callout__c oClaimsAddress = new HUM_Webservice_Callout__c();
        oClaimsAddress.name='Claims_AddressStandardization';
        oClaimsAddress.Active__c=true;
        oClaimsAddress.Certificate_Name__c='testts';
        oClaimsAddress.End_Point_URL__c='https://testst-cmservicews.hum.com';
        oClaimsAddress.Timeout__c='120000';
        oClaimsAddress.Content_Type__c='application/json';
        oClaimsAddress.Request_Type__c='POST';
        insert oClaimsAddress;
        system.assert(oClaimsAddress.id!=null);
        system.assert(oClaimsAddress.name == 'Claims_AddressStandardization');

        HUM_Webservice_Callout__c oHUMTaskHierarchyService = new HUM_Webservice_Callout__c();
        oHUMTaskHierarchyService.name='Claim_Send';
        oHUMTaskHierarchyService.Active__c=true;
        oHUMTaskHierarchyService.Certificate_Name__c='testts';
        oHUMTaskHierarchyService.End_Point_URL__c='https://testst-cmservicews.hum.com';
        oHUMTaskHierarchyService.Timeout__c='120000';
        oHUMTaskHierarchyService.Content_Type__c='application/json';
        oHUMTaskHierarchyService.Request_Type__c='POST';
        insert oHUMTaskHierarchyService;
        system.assert(oHUMTaskHierarchyService.id!=null);
        system.assert(oHUMTaskHierarchyService.name == 'Claim_Send');
       
    }
}

	@isTest Static void testgetMemberPlanDetails(){
    User testUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
    Account oAccount = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account'];
    System.runAs(testUser)
        {
            list<MemberPlan>  lstMemberPlan = Claim_Send_Statement_LC_HUM.getMemberPlanDetails(oAccount.Id);
            System.assertEquals(lstMemberPlan, Claim_Send_Statement_LC_HUM.getMemberPlanDetails(oAccount.Id));
            System.assertNotEquals(null, Claim_Send_Statement_LC_HUM.getMemberPlanDetails(oAccount.Id));
            Claim_Send_Statement_LC_HUM.getMemberPlanDetails('');
        }

    }

	@isTest Static void testgetAccountDetails(){
    User testUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
    Account oAccount = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account']; 
    MemberPlan oMemPlan = [Select ID from MemberPlan Limit 1];
    System.runAs(testUser)
        {
            Account objTestAcc = new Account();
            objTestAcc= Claim_Send_Statement_LC_HUM.getAccountDetails(oAccount.Id);
            Claim_Send_Statement_LC_HUM.getAccountDetails(oMemPlan.Id);
            System.assertEquals(objTestAcc, Claim_Send_Statement_LC_HUM.getAccountDetails(oAccount.Id));
            System.assertNotEquals(null, Claim_Send_Statement_LC_HUM.getAccountDetails(oAccount.Id));
        }

    }

	
	@isTest Static void testbuildRequest()
	{
        User testUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        System.runAs(testUser)
        {   
            String sAddressLine1='8900 shelbyville road';
            String sAddressLine2='apt 5';
            String sStateCode='louisville"';
            String sCity ='ky'; 
            String sZipCode ='41105';
            
            Claim_Send_Statement_LS_HUM oClmSend = new Claim_Send_Statement_LS_HUM();
            string sRequest = oClmSend.buildRequest(sAddressLine1,sAddressLine2,sCity,sStateCode,sZipCode);
            Claim_Send_Statement_Request_H_HUM getSendStmtRequest = new Claim_Send_Statement_Request_H_HUM();
            getSendStmtRequest = (Claim_Send_Statement_Request_H_HUM)System.JSON.deserialize(sRequest,Claim_Send_Statement_Request_H_HUM.class); 
            
            if (getSendStmtRequest != null &&  getSendStmtRequest.StandardizeAddressRequest != null)
            {
            	System.assertEquals(sAddressLine1,getSendStmtRequest.StandardizeAddressRequest.AddressLine1);
                System.assertEquals(sAddressLine2,getSendStmtRequest.StandardizeAddressRequest.AddressLine2);
                System.assertEquals(sStateCode,getSendStmtRequest.StandardizeAddressRequest.StateCode);
                System.assertEquals(sCity,getSendStmtRequest.StandardizeAddressRequest.City);
                System.assertEquals(sZipCode,getSendStmtRequest.StandardizeAddressRequest.ZipCode);
            }
            
        }
	}    
      @isTest Static void testsetupRequest()
      {
        User testUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        System.runAs(testUser)
        {
            HUM_Webservice_Callout__c oServiceCallout = [select Name, Certificate_Name__c, End_Point_URL__c, Timeout__c, Content_Type__c, Request_Type__c from HUM_Webservice_Callout__c where name='Claim_Send' limit 1];   
            System.assertEquals(oServiceCallout.Certificate_Name__c,'testts');
            
            String sAddressLine1='8900 shelbyville road';
            String sAddressLine2='apt 5';
            String sStateCode='louisville"';
            String sCity ='ky'; 
            String sZipCode ='41105';
            
            
            Claim_Send_Statement_LS_HUM oClmSend = new Claim_Send_Statement_LS_HUM();
            HttpRequest oRequest = new HttpRequest();
            HttpRequest oRequest1 = new HttpRequest();
            oRequest1 = oClmSend.setupRequest(oRequest,oServiceCallout,sAddressLine1,sAddressLine2,sStateCode,sCity,sZipCode);
            
            if (oRequest1 != null)
            {
                Claim_Send_Statement_Request_H_HUM getSendStmtRequest = new Claim_Send_Statement_Request_H_HUM();
                getSendStmtRequest = (Claim_Send_Statement_Request_H_HUM)System.JSON.deserialize(oRequest1.getBody(),Claim_Send_Statement_Request_H_HUM.class); 
                
                if (getSendStmtRequest != null &&  getSendStmtRequest.StandardizeAddressRequest != null)
                {
                    System.assertEquals(sAddressLine1,getSendStmtRequest.StandardizeAddressRequest.AddressLine1);
                    System.assertEquals(sAddressLine2,getSendStmtRequest.StandardizeAddressRequest.AddressLine2);
                    System.assertEquals(sStateCode,getSendStmtRequest.StandardizeAddressRequest.StateCode);
                    System.assertEquals(sCity,getSendStmtRequest.StandardizeAddressRequest.City);
                    System.assertEquals(sZipCode,getSendStmtRequest.StandardizeAddressRequest.ZipCode);
                }
            }
            System.assertEquals(null, oClmSend.setupRequest(oRequest,oServiceCallout,sAddressLine1,sAddressLine2,sStateCode,sCity,sZipCode));
        }
	}    
    
    @isTest Static void testinitiateRequest()
	{
        User testUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        System.runAs(testUser)
        {
            HUM_Webservice_Callout__c oServiceCallout = [select Name, Certificate_Name__c, End_Point_URL__c, Timeout__c, Content_Type__c, Request_Type__c from HUM_Webservice_Callout__c where name='Claim_Send' limit 1];   
            System.assertEquals(oServiceCallout.Certificate_Name__c,'testts');
            
            String sAddressLine1='8900 shelbyville road';
            String sAddressLine2='apt 5';
            String sStateCode='louisville"';
            String sCity ='ky'; 
            String sZipCode ='41105';
            
            System.assertNotEquals(null, Claim_Send_Statement_LC_HUM.initiateRequest(sAddressLine1,sAddressLine2,sStateCode,sCity,sZipCode));
            
            Continuation objCon = (Continuation)Claim_Send_Statement_LC_HUM.initiateRequest(sAddressLine1,sAddressLine2,sStateCode,sCity,sZipCode);
            objCon.continuationMethod='processResponse';
            objCon.state='test';
            string labelValue= 'Continuation-1';
            HttpResponse oResponse = new HttpResponse();
            oResponse.setHeader('Content-Type', 'application/json');
            oResponse.setHeader('SOAPAction', '');    
            oResponse.setHeader('Connection','keep-alive');
            string sJson = '{"StandardizeAddressResponse": {    "SuccessFlag": "Y",    "AddressLine1": "8900 SHELBYVILLE RD APT 5",    "City": "LOUISVILLE",    "StateCode": "KY",    "ZipCode": "40222",    "StateID": "21",    "ZipCodePlus": "8596",    "CountyName": "JEFFERSON",    "CountyID": "111",    "Valid": "true",    "ChangeIndicator": "Change" }}}}';
            oResponse.setBody(sJson);
            oResponse.setStatusCode(200);
            Claim_Send_Statement_LC_HUM.responseParse(oResponse);
            List<string> newStr = new List<string> ();
            Claim_Send_Statement_LC_HUM.processResponse(newStr,'State');
        }
	} 
 
    static testMethod void testprocessResponse()
    {    
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        
        System.runAs (oCCSUser) 
        { 
            HUM_Webservice_Callout__c oServiceCallout = [select Name, Certificate_Name__c, End_Point_URL__c, Timeout__c, Content_Type__c, Request_Type__c from HUM_Webservice_Callout__c where name='Claim_Send' limit 1];   
            System.assertEquals(oServiceCallout.Certificate_Name__c,'testts');
            
            Claim_Send_Statement_LS_HUM oCaseDetail1 = new Claim_Send_Statement_LS_HUM();
            HttpResponse oResponse = New HttpResponse();
            oResponse.setHeader('Content-Type', 'application/json');
            oResponse.setHeader('SOAPAction', '');    
            oResponse.setHeader('Connection','keep-alive');
            Test.StartTest();
            String sJson = '{ "StandardizeAddressRequest": { "AddressLine1": "8900 shelbyville road", "AddressLine2": "apt 5", "City": "louisville", "StateCode": "ky", "ZipCode": "41105" } } ';
            oResponse.setBody(sJson );
            oResponse.setStatusCode(200); 
            System.assertEquals(sJson, oResponse.getBody());
            Test.stopTest();  
        }
    }   
     
    static testMethod void testSendRequest()
    {    
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        System.runAs(oCCSUser) 
        { 
            Test.StartTest();

            Claim_Send_Statement_LC_HUM.showErrorMsg(true);
            Account oAccount = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account'];  
            Policy__c policy = HUMTestDataHelperTest.getPolicy();
            policy.Group_Name__c = oAccount.id;
            insert policy;
        
            MemberPlan oMemberPlan = getMemberPlan();
            oMemberPlan.EffectiveFrom =Date.parse(Date.today().addDays(-1).format());
            oMemberPlan.EffectiveTo = Date.parse(Date.today().addDays(1).format());
            oMemberPlan.Name='testpolmem';
            oMemberPlan.Has_Vitality__c ='Y';       
            oMemberPlan.Policy__c = policy.id;
            oMemberPlan.MemberId = oAccount.id;
            oMemberPlan.Member_Id_Base__c = oAccount.id;
            insert oMemberPlan ;
            
            Map<Object, String>  AddressData = new Map<Object, String>();
            Map<Object, String> RtiData = new Map<Object, String>();
            Map<Object, String> RtiData1 = new Map<Object, String>();
			Map<Object, String> RtiData2 = new Map<Object, String>(); 
            Map<Object, String> RtiData3 = new Map<Object, String>(); 
            
            AddressData.put('AddressLine1', 'A123'); 
            AddressData.put('AddressLine2', '');
            AddressData.put('StateCode','1234');
            AddressData.put('City','Sample');
            AddressData.put('ZipCode','12345' );
            AddressData.put('FirstName','XXX');
            AddressData.put('LastName','YYY');
            AddressData.put('FaxNo','1234') ;
            AddressData.put('Name','X Y') ;
            AddressData.put('RecipientName','');
            
            RtiData.put('CatDesc','');
            RtiData.put('MsgName','');
            RtiData.put('rtiPageId',oAccount.id);
            RtiData.put('SourceCode','');
            RtiData.put('fulfilId','');
          
            RtiData.put('DateSent', '');
            RtiData.put('Viewlink','');
            RtiData.put('recipientType','Member');
            
            RtiData1.put('DateSent', '');
            RtiData1.put('Viewlink','');
            RtiData1.put('recipientType','Member');
            RtiData1.put('CatDesc','');
            RtiData1.put('MsgName','');
            RtiData1.put('DocKey','20221221045553');
            RtiData1.put('rtiPageId',oAccount.id);
            RtiData1.put('SourceCode','');
            RtiData1.put('fulfilId','');
            RtiData1.put('DateSent', '');
            RtiData1.put('Viewlink','');
            RtiData1.put('sStatementType','Member');
            RtiData1.put('selectedValue','Fax');
            RtiData1.put('recipientType','Member');
            RtiData1.put('sPolMemID',oMemberPlan.id);
            
            
            RtiData2.put('CatDesc','');
            RtiData2.put('MsgName','');
            RtiData2.put('rtiPageId','');
            RtiData2.put('SourceCode','');
            RtiData2.put('fulfilId','');
            RtiData2.put('DocKey','20221221045553');
            RtiData2.put('selectedValue','Mail');
            RtiData2.put('sStatementType','Provider');
            RtiData2.put('selectedValue','test');
            RtiData2.put('recipientType','Provider');
            RtiData2.put('sPolMemID',oMemberPlan.id);
            
            RtiData3.put('CatDesc','');
            RtiData3.put('MsgName','');
            RtiData3.put('rtiPageId',oMemberPlan.id);
            RtiData3.put('SourceCode','');
            RtiData3.put('fulfilId','');
            RtiData3.put('DocKey','20221221045553');
            RtiData3.put('selectedValue','test');
            RtiData3.put('sStatementType','rtiMember');
            RtiData3.put('selectedValue','test');
            RtiData3.put('recipientType','Provider');
            RtiData3.put('sPolMemID',oMemberPlan.id);
            
            Map<String, String> rtiDataTemplate = new Map<String, String>();
            rtiDataTemplate.put('RefCity','Sample' );
            rtiDataTemplate.put('RefStatecode', '1234');
            rtiDataTemplate.put('RTIPageId','');
            rtiDataTemplate.put('MailrepName','');
            rtiDataTemplate.put('RecipientType','');
            rtiDataTemplate.put('RefAddressLine1','A123');
            rtiDataTemplate.put('RefAddressLine2','');
            rtiDataTemplate.put('RefZipcode','12345');
            rtiDataTemplate.put('CategoryDesc','');
            rtiDataTemplate.put('MessageName', '');
            rtiDataTemplate.put('DateSent', '');
            rtiDataTemplate.put('ViewLink', '');
                 
            HUM_Webservice_Callout__c oServiceCallout = [select Name, Certificate_Name__c, End_Point_URL__c, Timeout__c, Content_Type__c, Request_Type__c from HUM_Webservice_Callout__c where name='CommunicationReprint_V3' limit 1];   
            System.assertEquals(oServiceCallout.Certificate_Name__c,'testts');
            system.assertEquals(null, Claim_Send_Statement_LC_HUM.initiateSendStmtRequest(AddressData,RtiData));
            
            Claim_Send_Statement_LC_HUM.initiateSendStmtRequest(AddressData,RtiData1);
            
            Continuation conti = (Continuation) Claim_Send_Statement_LC_HUM.initiateSendStmtRequest(AddressData,RtiData2);
            Continuation objCon = new Continuation(120);
           	objCon.continuationMethod='processSendStmtResponse';
           	objCon.state='test';
           	string labelValue= 'Continuation-1';
           	HttpResponse oResponse = new HttpResponse();
           	oResponse.setHeader('Content-Type', 'application/json');
           	oResponse.setHeader('SOAPAction', '');    
            oResponse.setHeader('Connection','keep-alive');
			string sjson = '{"ReprintResponse": {"ReprintResult": {    "Status": "Printed",    "Description": "Message Fulfillment Id :AFP ViewDirectTest|EORSTMT   |20221221045553|202212215000481EORLV          |2 Reprint request successfully submitted" }}}';
            oResponse.setBody(sJson);
            oResponse.setStatusCode(200);
            Claim_Send_Statement_LC_HUM.responseParseSendStmt(oResponse,rtiDataTemplate);
            //Negative scenarios
            Continuation conti1 = (Continuation) Claim_Send_Statement_LC_HUM.initiateSendStmtRequest(AddressData,RtiData2);
            Continuation objCon1 = new Continuation(120);
           	objCon1.continuationMethod='processSendStmtResponse';
           	objCon1.state='test';
           	string labelValue1= 'Continuation-1';
           	HttpResponse oResponse1 = new HttpResponse();
           	oResponse1.setHeader('Content-Type', 'application/json');
           	oResponse1.setHeader('SOAPAction', '');    
            oResponse1.setHeader('Connection','keep-alive');
			string sjson1 = '{"ReprintResponse": {"ReprintResult": {    "Status": "Printed",    "Description": "Message Fulfillment Id :AFP ViewDirectTest|EORSTMT   |20221221045553|202212215000481EORLV          |2 Reprint request successfully submitted" }}}';
            oResponse1.setBody(sJson1);
            oResponse1.setStatusCode(201);
            Claim_Send_Statement_LC_HUM.responseParseSendStmt(oResponse1,rtiDataTemplate);
            
            string sjson2 = '{"ReprintResponse": {"ReprintResult": {    "Status": "Printed",    "Description": "Message Fulfillment Id :AFP ViewDirectTest|EORSTMT   |20221221045553|202212215000481EORLV          |2 Reprint request successfully submitted" }}}';
            oResponse1.setBody(sJson2);
            oResponse1.setStatusCode(200);
            rtiDataTemplate.put('bIsPageFromClm','true');
            Claim_Send_Statement_LC_HUM.responseParseSendStmt(oResponse1,rtiDataTemplate);
            string sjson3 = '{"ReprintResponse": {"ReprintResult": {    "Status": "Failed",    "Description": "Message Fulfillment Id :AFP ViewDirectTest|EORSTMT   |20221221045553|202212215000481EORLV          |2 Reprint request successfully submitted" }}}';
            oResponse1.setBody(sJson3);
            Claim_Send_Statement_LC_HUM.responseParseSendStmt(oResponse1,rtiDataTemplate);
            Continuation conti2 = (Continuation) Claim_Send_Statement_LC_HUM.initiateSendStmtRequest(AddressData,RtiData3);
            
            string sjson4 = '{"ReprintResponse": {}}';
            oResponse1.setBody(sJson4);
            Claim_Send_Statement_LC_HUM.responseParseSendStmt(oResponse1,rtiDataTemplate);
            
            List<string> lbls = new List<string> ();
            Claim_Send_Statement_LC_HUM.processSendStmtResponse(lbls,'State');
            
            Claim_Send_Statement_LC_HUM.faxNumber = '123-456-7890';
            Claim_Send_Statement_LC_HUM.providerAddress(oMemberPlan.id);
            Test.stopTest(); 
        }
    } 
    
    static testMethod void testcreateCaseInteraction()
    {
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        System.runAs(oCCSUser) 
        {
            Test.StartTest();
            Case objCase = HUMTestDataHelperTest.getCase();
            objCase.Call_Benefit_Category__c = 'Medicare Part D';
            insert objCase;
            system.assertNotEquals(objCase,null,'Value not matched');
            List<Interaction__c> lstInteraction = [Select Id,Interacting_With_type__c,Interacting_With__c from Interaction__c limit 1];
            Claim_Send_Statement_LC_HUM.createCaseInteraction(objCase.Id,lstInteraction[0].Id);
            Claim_Send_Statement_LC_HUM.createCaseInteraction('','');
            Claim_Send_Statement_LC_HUM.createCaseInteraction('','1');
            Claim_Send_Statement_LC_HUM.updateInteractionOnCase(objCase.Id,lstInteraction[0].Id);
            Test.stopTest(); 
        }
    }
    
   /*
    * Method Name   :    getMemberPlan
    * Description   :    create Policy Member Object record
    * Return Type   :   MemberPlan
    * Parameters    :   NA
    */
    Public Static MemberPlan getMemberPlan()
    {
        MemberPlan oMemberPlan = New MemberPlan();
        oMemberPlan.RelationshipToSubscriber = 'Self';
        oMemberPlan.EffectiveFrom = Date.parse(Date.today().addDays(-1).format());
        oMemberPlan.EffectiveTo =  Date.parse(Date.today().addDays(1).format());
        Return oMemberPlan;
    }
    
    static testMethod void testResponseParseSendStmt()
    {    
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        System.runAs(oCCSUser) 
        { 
            Test.StartTest();

            Claim_Send_Statement_LC_HUM.showErrorMsg(false);
            Account oAccount = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account'];  
            Map<Object, String>  AddressData = new Map<Object, String>();
            Map<Object, String> RtiData = new Map<Object, String>();
            
            AddressData.put('AddressLine1', 'A123'); 
            AddressData.put('AddressLine2', '');
            AddressData.put('StateCode','1234');
            AddressData.put('City','Sample');
            AddressData.put('ZipCode','12345' );
            AddressData.put('FirstName','XXX');
            AddressData.put('LastName','YYY');
            AddressData.put('FaxNo','1234') ;
            AddressData.put('Name','X Y') ;
            AddressData.put('RecipientName','');
            
            RtiData.put('CatDesc','');
            RtiData.put('MsgName','');
            RtiData.put('rtiPageId',oAccount.id);
            RtiData.put('SourceCode','');
            RtiData.put('fulfilId','');
          
            RtiData.put('DateSent', '');
            RtiData.put('Viewlink','');
            RtiData.put('recipientType','Member');
                 
            
            HUM_Webservice_Callout__c oServiceCallout = [select Name, Certificate_Name__c, End_Point_URL__c, Timeout__c, Content_Type__c, Request_Type__c from HUM_Webservice_Callout__c where name='CommunicationReprint_V3' limit 1];   
            System.assertEquals(oServiceCallout.Certificate_Name__c,'testts');
            
            Continuation objCon = New Continuation(120);
            objCon.continuationMethod='processSendStmtResponse';
            
            HttpResponse oResponse = New HttpResponse();
            oResponse.setHeader('Content-Type', 'application/json');
            oResponse.setHeader('SOAPAction', '');    
            oResponse.setHeader('Connection','keep-alive');
            string sResMsg = 'Send Print Item Request successfully submitted.';
            String sJson = '{"DistributionChannel":"Print","SystemOfRecordId":"AFP VIEWDIRECTSS|AFPTEST|20180807064220|11550046501222018060120180630Z|20","RecipientFirstName":"test","RecipientMiddleName" :"","RecipientLastName":"test","RecipientAddressLine1":"Louiseville","RecipientAddressLine2":"hiuu","RecipientCity":"test","RecipientState":"tx","RecipientZip":"40404","RecipientFaxNumber":"123456","CCPInquiryNumber":"","SourceTransactionId":"","RecipientRoleMember":{"MemberCardid":"H68518747","DependentCode":"00","PlatformCode":"LV","SDRPersonId":"1085445415","MemberGenKey":"1155004650122"}}';
            oResponse.setBody(sJson);
            oResponse.setStatusCode(200); 
            string sResMsg1 ='Send Print Item Request failed - Please try again. If the issue persists contact Help Desk.';
            HttpResponse oResponse1 = New HttpResponse();
            oResponse1.setHeader('Content-Type', 'application/json');
            oResponse1.setHeader('SOAPAction', '');    
            oResponse1.setHeader('Connection','keep-alive');
            oResponse.setStatusCode(200);
            oResponse1.setBody('{ "ReprintResponse":{ "ReprintResult":{ "Status":"Printed", "Description":"Abc" } } }');
            Test.stopTest(); 
        }
    } 
    
    @isTest Static void testgetAccDetails(){
		User testUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
		Account oAccount = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account'];  
		System.runAs(testUser)
        {
            List<MemberPlan> objTestAcc = new List<MemberPlan>();
            objTestAcc= Claim_Send_Statement_LC_HUM.getAccDetails(oAccount.Id);
            System.assertEquals(objTestAcc, Claim_Send_Statement_LC_HUM.getAccDetails(oAccount.Id));
            System.assertNotEquals(null, Claim_Send_Statement_LC_HUM.getAccDetails(oAccount.Id));
        }

    }
    
    @isTest Static void testgetRTIDeliveryMethod(){
		User testUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
		List<RTI_Delivery_Method__mdt> RTIDeliveryMethodList = [select Method_Name__c,Method_Name_Value__c from RTI_Delivery_Method__mdt order by Method_Name__c desc limit 100];  
		System.runAs(testUser)
        {
            List<RTI_Delivery_Method__mdt> objTestRTI = new List<RTI_Delivery_Method__mdt>();
            objTestRTI= Claim_Send_Statement_LC_HUM.getRTIDeliveryMethod();
            System.assertEquals(RTIDeliveryMethodList, objTestRTI);
        }

    }
    
    static testMethod void testException()
    {
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        HUM_Webservice_Callout__c webSer = [select Id, name from HUM_Webservice_Callout__c where name ='Claims_AddressStandardization' limit 1];
        delete webSer;
        System.runAs(oCCSUser) 
        {
            Claim_Send_Statement_LC_HUM.getUserQueue(oCCSUser.Id);
            Claim_Send_Statement_LC_HUM.getUserQueue(null);
            
            String sAddressLine1='8900 shelbyville road';
            String sAddressLine2='apt 5';
            String sStateCode='louisville"';
            String sCity ='ky'; 
            String sZipCode ='41105';
            
            System.assertEquals(null, Claim_Send_Statement_LC_HUM.initiateRequest(sAddressLine1,sAddressLine2,sStateCode,sCity,sZipCode));
            
        }
    }
    
}