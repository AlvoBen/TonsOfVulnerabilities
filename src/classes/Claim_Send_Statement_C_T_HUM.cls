/**************************************************************************** 
Apex Class Name  : Claim_Send_Statement_C_T_HUM
Version          : 1.0  
Created Date     : September 21 2018
Function         : Test class for Claim_Send_Statement_C_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Vijaya Koriprolu                            10/04/2018                  Original Version
* Kritika Popat           295053              09/28/2018                  Updated test Class for new code changes
* Prasanthi Kandula                           04/20/2020                  Updatedtest class for new code changes 
* Trina  Ghosh                                07/27/2020                  US# 965352
* Prasanthi Kandula                           08/13/2020                  Updated Test class to increase code coverage  
* Rahul Chaurasia                             03/05/2021                  REQ - 1905086 - T1PRJ0002606- SF- Mobius Services old version Decommission
* Ranadheer                                   05/18/2021                  Code coverage
* Prasanthi Kandula                           05/28/2021                  Increased code coverage   
***************************************************************************************************/
@isTest
private Class Claim_Send_Statement_C_T_HUM
{
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
            
            Policy_Member__c objPolMem = new Policy_Member__c();
            objPolMem.Name = 'testpolmem11';
            objPolMem.Relationship__c = 'Self';
            objPolMem.Subscriber__c = objPolMem.Id;
            objPolMem.Effective_Date__c=Date.today().addDays(-1).format();
            objPolMem.End_Date__c=Date.today().addDays(1).format();
            objPolMem.Policy_Member_External_ID__c='00|ODS|6925004434817|12345678|34567|2013-07-01';
            objPolMem.Member__c = objAcc.Id;
            objPolMem.Policy__c = objpol.Id;
            objPolMem.Policy__r = objpol;
            objPolMem.Relationship_Code__c = '00';
            insert objPolMem;
            system.assertEquals(objPolMem.Relationship__c, 'Self');
            
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
            PageReference pageRef = Page.Claim_Send_Statement_VF_HUM;
            Test.setCurrentPage(pageRef);
            ApexPages.currentPage().getParameters().put('pid',objPolMem.Id);
        }
    }
    /*
* Method Name : TestDetailsRequest
* Description : Used to validate detail request functionality.
* Return type : void
* Parameter   : NA
*/
    static testMethod void testDetailsRequest()
    {    
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User2','Customer Care Specialist');
        insert oCCSUser;
        System.runAs(oCCSUser) 
        { 
            Test.StartTest();            
            Claim_Send_Statement_C_HUM memClaims = new Claim_Send_Statement_C_HUM();
            memClaims.faxNumber = '123-456-789';
            memClaims.isFaxNull = true;
            memClaims.selectedValue = 'Mail';
            MemClaims.sRecipientType = 'trest';
            memClaims.repName = 'test';
            memClaims.sRefAddressLine1 = ' ';
            Integer result = memClaims.faxNumber.length();
            memClaims.getString();
            memClaims.setString(memClaims.selectedValue);
            memClaims.initiateRequest();
            memClaims.processResponse();
            memClaims.sRefAddressLine1 ='';
            memClaims.sRefAddressLine2='';
            memClaims.sRefCity ='';
            memClaims.sRefStatecode='';
            memClaims.sRefZipcode='';
            memClaims.Validate();
            memClaims.faxNumber = '123-456-789000';
            memClaims.ValidateFax();
            Test.stopTest();  
        }
        
    }
    
    /*
* Method Name : testSendRequest
* Description : Used to validate detail request functionality.
* Return type : void
* Parameter   : NA
*/
    static testMethod void testSendRequest()
    {    
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        System.runAs(oCCSUser) 
        { 
            Test.StartTest();
            Claim_Send_Statement_C_HUM memClaims = new Claim_Send_Statement_C_HUM();
            memClaims.showErrorMsg();
            Account oAccount = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account'];  
            Policy_Member__c oPolMem =  [Select Id,Subscriber__c ,Relationship__c,Policy__r.Group_Name__r.Home_Office_Account__c, Member__c, Policy__c, Policy_Member_External_ID__c , Policy__r.Product__r.ProductCode from Policy_Member__c where Member__c =: oAccount.Id];
            memClaims.getAccDetails(oPolMem.Id);
            memClaims.sRTIPageId = oAccount.id;
            memClaims.selectedValue = 'Mail';
            memClaims.selectedValue = 'Fax';
            memClaims.sRefAddressLine1 = 'A123';  
            memClaims.sRefAddressLine2 = 'A123';
            memClaims.sRefCity = 'Sample';
            memClaims.sRefStatecode= '12345'; 
            memClaims.sRefZipcode= '12345';
            memClaims.faxNumber = '12345';
            memClaims.sMailrepName= '12345'; 
            Test.setCurrentPage(Page.Claim_Send_Statement_VF_HUM);
            ApexPages.currentPage().getParameters().put('DocKey','A123DocumentKey123');
            ApexPages.currentPage().getParameters().put('statementType','Member');
            ApexPages.currentPage().getParameters().put('pid',oPolMem.Id);
            ApexPages.currentPage().getParameters().put('pCode','LV');
            ApexPages.currentPage().getParameters().put('mid','H123-00');            
            HUM_Webservice_Callout__c oServiceCallout = [select Name, Certificate_Name__c, End_Point_URL__c, Timeout__c, Content_Type__c, Request_Type__c from HUM_Webservice_Callout__c where name='CommunicationReprint_V3' limit 1];   
            System.assertEquals(oServiceCallout.Certificate_Name__c,'testts');
            memClaims.initiateSendStmtRequest();
            Continuation objCon = New Continuation(120);
            objCon.continuationMethod='processSendStmtResponse';
            memClaims.processSendStmtResponse();
            
            HttpResponse oResponse = New HttpResponse();
            oResponse.setHeader('Content-Type', 'application/json');
            oResponse.setHeader('SOAPAction', '');    
            oResponse.setHeader('Connection','keep-alive');
            
            String sJson = '{"DistributionChannel":"Print","SystemOfRecordId":"AFP VIEWDIRECTSS|AFPTEST|20180807064220|11550046501222018060120180630Z|20","RecipientFirstName":"test","RecipientMiddleName" :"","RecipientLastName":"test","RecipientAddressLine1":"Louiseville","RecipientAddressLine2":"hiuu","RecipientCity":"test","RecipientState":"tx","RecipientZip":"40404","RecipientFaxNumber":"123456","CCPInquiryNumber":"","SourceTransactionId":"","RecipientRoleMember":{"MemberCardid":"H68518747","DependentCode":"00","PlatformCode":"LV","SDRPersonId":"1085445415","MemberGenKey":"1155004650122"}}';
            oResponse.setBody(sJson);
            oResponse.setStatusCode(200); 
            memClaims.responseParseSendStmt(oResponse);
            
            HttpResponse oResponse1 = New HttpResponse();
            oResponse1.setHeader('Content-Type', 'application/json');
            oResponse1.setHeader('SOAPAction', '');    
            oResponse1.setHeader('Connection','keep-alive');
            oResponse.setStatusCode(200);
            oResponse1.setBody('{ "ReprintResponse":{ "ReprintResult":{ "Status":"Printed", "Description":"Abc" } } }');
            memClaims.responseParseSendStmt(oResponse1);
            oResponse.setStatusCode(800); 
            memClaims.responseParseSendStmt(oResponse);
            Test.stopTest(); 
        }
    } 
    
    /*
* Method Name : testMemberStatement
* Description : Used to validate Member statement type.
* Return type : void
* Parameter   : NA
*/
    static testMethod void testMemberStatement()
    {    
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        System.runAs(oCCSUser) 
        { 
            Test.StartTest();
            Account oAccount = [Select Id,Name,LastName,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account'];
            System.assertEquals(oAccount.LastName,'Account');  
            Policy_Member__c oPolMem =  [Select Id,Subscriber__c ,Relationship__c,Policy__r.Group_Name__r.Home_Office_Account__c, Member__c, Policy__c, Policy_Member_External_ID__c , Policy__r.Product__r.ProductCode from Policy_Member__c where Member__c =: oAccount.Id];
            Test.setCurrentPage(Page.Claim_Send_Statement_VF_HUM);
            ApexPages.currentPage().getParameters().put('DocKey','A123DocumentKey123');
            ApexPages.currentPage().getParameters().put('statementType','Member');
            ApexPages.currentPage().getParameters().put('pid',oPolMem.Id);
            ApexPages.currentPage().getParameters().put('pCode','LV');
            ApexPages.currentPage().getParameters().put('mid','H123-00');
            ApexPages.currentPage().getParameters().put('rtiPageId',oAccount.id);
            ApexPages.currentPage().getParameters().put('fromPage','RTIDetail');
            Claim_Send_Statement_C_HUM memClaims = new Claim_Send_Statement_C_HUM();
            memClaims.showErrorMsg();            
            memClaims.getAccDetails(oPolMem.Id);
            memClaims.sRTIPageId = oPolMem.id;
            memClaims.bIsPageFromRTI = true;
            memClaims.sPolMemID = oPolMem.id;
            memClaims.selectedValue = 'Mail';
            memClaims.selectedValue = 'Fax';
            memClaims.sRefAddressLine1 = 'A123';  
            memClaims.sRefAddressLine2 = 'A123';
            memClaims.sRefCity = 'Sample';
            memClaims.sRefStatecode= '12345'; 
            memClaims.sRefZipcode= '12345';
            memClaims.faxNumber = '12345';
            memClaims.sMailrepName= '12345'; 
            
            HUM_Webservice_Callout__c oServiceCallout = [select Name, Certificate_Name__c, End_Point_URL__c, Timeout__c, Content_Type__c, Request_Type__c from HUM_Webservice_Callout__c where name='CommunicationReprint_V3' limit 1];   
            System.assertEquals(oServiceCallout.Certificate_Name__c,'testts');            
            memClaims.initiateSendStmtRequest();
            Continuation objCon = New Continuation(120);
            objCon.continuationMethod='processSendStmtResponse';
            memClaims.processSendStmtResponse();
            
            HttpResponse oResponse = New HttpResponse();
            oResponse.setHeader('Content-Type', 'application/json');
            oResponse.setHeader('SOAPAction', '');    
            oResponse.setHeader('Connection','keep-alive');
            String sJson = '{"DistributionChannel":"Print","SystemOfRecordId":"AFP VIEWDIRECTSS|AFPTEST|20180807064220|11550046501222018060120180630Z|20","RecipientFirstName":"test","RecipientMiddleName" :"","RecipientLastName":"test","RecipientAddressLine1":"Louiseville","RecipientAddressLine2":"hiuu","RecipientCity":"test","RecipientState":"tx","RecipientZip":"40404","RecipientFaxNumber":"123456","CCPInquiryNumber":"","SourceTransactionId":"","RecipientRoleMember":{"MemberCardid":"H68518747","DependentCode":"00","PlatformCode":"LV","SDRPersonId":"1085445415","MemberGenKey":"1155004650122"}}';
            oResponse.setBody(sJson);
            oResponse.setStatusCode(200); 
            memClaims.responseParseSendStmt(oResponse);
            
            HttpResponse oInvalidResp= New HttpResponse();
            oInvalidResp.setHeader('Content-Type', 'application/json');
            oInvalidResp.setHeader('SOAPAction', '');    
            oInvalidResp.setHeader('Connection','keep-alive');
            oResponse.setStatusCode(200);
            oInvalidResp.setBody('{ "ReprintResponse":{ "ReprintResult":{ "Status":"Printed", "Description":"Abc" } } }');
            memClaims.responseParseSendStmt(oInvalidResp);
            oResponse.setStatusCode(800); 
            memClaims.responseParseSendStmt(oResponse);
            
            Template__c templateObj = new Template__c();
            templateObj.Description__c = 'Send Print Item: Member';
            insert templateObj;
            
            //Insert Template Field Data
            Template_Field__c templateFieldObj = New Template_Field__c();
            templateFieldObj.Template__c = templateObj.Id;
            templateFieldObj.Data_Type__c ='String';
            templateFieldObj.Name = 'Send Print Item: Member';
            insert templateFieldObj;
            
            HttpResponse oReprintResp = New HttpResponse();
            oReprintResp.setHeader('Content-Type', 'application/json');
            oReprintResp.setHeader('SOAPAction', '');    
            oReprintResp.setHeader('Connection','keep-alive');
            String sReprintJson = '{"ReprintResponse": {"ReprintResult": {"Status": "Success", "Description": "Message Fulfillment Id :AFP VIEWDIRECTSS|AFPTEST|20180807064220|11550046501222018060120180630Z|20 Reprint request successfully submitted"}}}';
            oReprintResp.setBody(sReprintJson);
            oReprintResp.setStatusCode(200); 
            memClaims.responseParseSendStmt(oReprintResp);
            memClaims.selectedValue = 'Mail';
            memclaims.populateTemplatedata();
            memClaims.selectedValue = 'Fax';
            memclaims.populateTemplatedata();
            Test.stopTest(); 
        }
    } 
    
    /*
* Method Name : testProviderStatement
* Description : Used to validate Provider statement type.
* Return type : void
* Parameter   : NA
*/
    static testMethod void testProviderStatement()
    {    
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        System.runAs(oCCSUser) 
        { 
            Test.StartTest();
            Account oAccount = [Select Id,Name,LastName,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account'];
            System.assertEquals(oAccount.LastName,'Account');  
            Policy_Member__c oPolMem =  [Select Id,Subscriber__c ,Relationship__c,Policy__r.Group_Name__r.Home_Office_Account__c, Member__c, Policy__c, Policy_Member_External_ID__c , Policy__r.Product__r.ProductCode from Policy_Member__c where Member__c =: oAccount.Id];
            Test.setCurrentPage(Page.Claim_Send_Statement_VF_HUM);
            ApexPages.currentPage().getParameters().put('DocKey','A123DocumentKey123');
            ApexPages.currentPage().getParameters().put('statementType','Provider');
            ApexPages.currentPage().getParameters().put('pid',oPolMem.Id);
            ApexPages.currentPage().getParameters().put('pCode','LV');
            ApexPages.currentPage().getParameters().put('mid','H123-00');
            ApexPages.currentPage().getParameters().put('fromPage','RTIDetail');
            ApexPages.currentPage().getParameters().put('rtiPageId',oAccount.id);
            Claim_Send_Statement_C_HUM memClaims = new Claim_Send_Statement_C_HUM();
            memClaims.showErrorMsg();
            memClaims.sMailrepName = 'Test';
            memClaims.initiateRequest();
            memClaims.sRefAddressLine1 = 'Test';
            memClaims.initiateRequest();
            memClaims.sRefCity = 'Hyd';
            memClaims.initiateRequest();
            memClaims.sRefStateCode = 'CET'; 
            memClaims.initiateRequest();
            memClaims.sRefZipcode = '123';
            memClaims.initiateRequest();
            memClaims.sRefZipcode = '12345';  
            memClaims.initiateRequest();
            memClaims.sRefStateCode = 'CE';
            memClaims.initiateRequest();
            memClaims.isFullAddress = false;
            
            HttpResponse oAddResponseChange = new HttpResponse();
            oAddResponseChange.setHeader('Content-Type', 'application/json');
            string sRespChangeIndicator = '{"StandardizeAddressResponse": {"SuccessFlag": "Y","AddressLine1": "6809 S 32ND ST","City": "PHOENIX","StateCode": "AZ","ZipCode": "85042","StateID": "04","ZipCodePlus": "6003","CountyName": "MARICOPA","CountyID": "013","Valid": "true","ChangeIndicator": "Change"}}';
            oAddResponseChange.setBody(sRespChangeIndicator);
            oAddResponseChange.setStatusCode(200);
            memClaims.responseParse(oAddResponseChange); 
            
            
            HttpResponse oAddResponseGood = new HttpResponse();
            oAddResponseGood.setHeader('Content-Type', 'application/json');
            string sRespGoodIndicator = '{"StandardizeAddressResponse": {"SuccessFlag": "Y","AddressLine1": "6809 S 32ND ST","City": "PHOENIX","StateCode": "AZ","ZipCode": "85042","StateID": "04","ZipCodePlus": "6003","CountyName": "MARICOPA","CountyID": "013","Valid": "true","ChangeIndicator": "Good"}}';
            oAddResponseGood.setBody(sRespGoodIndicator);
            oAddResponseGood.setStatusCode(200);
            memClaims.isAddress = false;
            memClaims.responseParse(oAddResponseGood); 
            
            HttpResponse oResponseValidFalse = new HttpResponse();
            oResponseValidFalse.setHeader('Content-Type', 'application/json');
            string sValidFalseJSON = '{"StandardizeAddressResponse": {"SuccessFlag": "Y","AddressLine1": "6809 S 32ND ST","City": "PHOENIX","StateCode": "AZ","ZipCode": "85042","StateID": "04","ZipCodePlus": "6003","CountyName": "MARICOPA","CountyID": "013","Valid": "false","ChangeIndicator": "Good"}}';
            oResponseValidFalse.setBody(sValidFalseJSON);
            oResponseValidFalse.setStatusCode(200);
            memClaims.sRefAddressline2 = 'S 32ND ST';
            memClaims.responseParse(oResponseValidFalse); 
            memClaims.closePopup();
            memClaims.redirectPopup();
            memClaims.initiateSendStmtRequest();
            memClaims.processSendStmtResponse();
            
            Template__c otemp = new Template__c();
            otemp.Description__c = 'Send Print Item: Member';
            insert otemp;
            
            HttpResponse oReprintResp = New HttpResponse();
            oReprintResp.setHeader('Content-Type', 'application/json');
            oReprintResp.setHeader('SOAPAction', '');    
            oReprintResp.setHeader('Connection','keep-alive');
            String sReprintJson = '{"ReprintResponse": {"ReprintResult": {"Status": "Success", "Description": "Message Fulfillment Id :AFP VIEWDIRECTSS|AFPTEST|20180807064220|11550046501222018060120180630Z|20 Reprint request successfully submitted"}}}';
            oReprintResp.setBody(sReprintJson);
            oReprintResp.setStatusCode(200); 
            memClaims.responseParseSendStmt(oReprintResp);
            Test.stopTest(); 
        }
    } 
    
    /*
* Method Name : testRTIMemberStmtType
* Description : Used to validate RTI Member Statement Type
* Return type : void
* Parameter   : NA
*/
    static testMethod void testRTIMemberStmtType()
    {    
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        System.runAs(oCCSUser) 
        { 
            Test.StartTest();
            Account oAccount = [Select Id,Name,LastName,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account'];
            System.assertEquals(oAccount.LastName,'Account');  
            Policy_Member__c oPolMem =  [Select Id,Subscriber__c ,Relationship__c,Policy__r.Group_Name__r.Home_Office_Account__c, Member__c, Policy__c, Policy_Member_External_ID__c , Policy__r.Product__r.ProductCode from Policy_Member__c where Member__c =: oAccount.Id];
            Test.setCurrentPage(Page.Claim_Send_Statement_VF_HUM);
            ApexPages.currentPage().getParameters().put('DocKey','A123DocumentKey123');
            ApexPages.currentPage().getParameters().put('statementType','rtiMember');
            ApexPages.currentPage().getParameters().put('pid',oPolMem.Id);
            ApexPages.currentPage().getParameters().put('pCode','LV');
            ApexPages.currentPage().getParameters().put('mid','H123-00');
            ApexPages.currentPage().getParameters().put('fulfilId','12345');
            ApexPages.currentPage().getParameters().put('dependentCd','00');
            ApexPages.currentPage().getParameters().put('personGenkey','12345');
            ApexPages.currentPage().getParameters().put('sdrId','12345');
            ApexPages.currentPage().getParameters().put('platformCd','LV');
            ApexPages.currentPage().getParameters().put('sSourceCode','EMCON');
            ApexPages.currentPage().getParameters().put('catDesc','EOB');
            Claim_Send_Statement_C_HUM memClaims = new Claim_Send_Statement_C_HUM();
            memClaims.initiateSendStmtRequest(); 
            memClaims.processSendStmtResponse();
            memClaims.sSourceCode = 'CP';
            memClaims.initiateSendStmtRequest(); 
            memClaims.sSourceCode = 'EMME';
            memClaims.sCategoryDesc='EXPLANATION OF BENEFITS';
            memClaims.sStatementTypeMobValue = 'EOB';
            memClaims.initiateSendStmtRequest(); 
            memClaims.processSendStmtResponse();
            Test.stopTest(); 
        }
    } 
    
    /*
* Method Name : testProviderTemplate
* Description : Used to validate Template functionality.
* Return type : void
* Parameter   : NA
*/
    
    static testMethod void testProviderTemplate()
    {    
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        System.runAs(oCCSUser) 
        { 
            Test.StartTest();
            Account oAccount = [Select Id,Name,LastName,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account'];
            System.assertEquals(oAccount.LastName,'Account');  
            Policy_Member__c oPolMem =  [Select Id,Subscriber__c ,Relationship__c,Policy__r.Group_Name__r.Home_Office_Account__c, Member__c, Policy__c, Policy_Member_External_ID__c , Policy__r.Product__r.ProductCode from Policy_Member__c where Member__c =: oAccount.Id];
            Test.setCurrentPage(Page.Claim_Send_Statement_VF_HUM);
            ApexPages.currentPage().getParameters().put('DocKey','A123DocumentKey123');
            ApexPages.currentPage().getParameters().put('statementType','Provider');
            ApexPages.currentPage().getParameters().put('pid',oPolMem.Id);
            ApexPages.currentPage().getParameters().put('pCode','LV');
            ApexPages.currentPage().getParameters().put('mid','H123-00');
            ApexPages.currentPage().getParameters().put('rtiPageId',oAccount.id);
            Claim_Send_Statement_C_HUM memClaims = new Claim_Send_Statement_C_HUM();
            memClaims.showErrorMsg();
            memClaims.closePopup();
            memClaims.redirectPopup();
            memClaims.initiateSendStmtRequest();
            memClaims.processSendStmtResponse();
            Template__c otemp = new Template__c();
            otemp.Description__c = 'Send Claim Statement: Provider';
            insert otemp;
            
            HttpResponse oResponse = New HttpResponse();
            oResponse.setHeader('Content-Type', 'application/json');
            oResponse.setHeader('SOAPAction', '');    
            oResponse.setHeader('Connection','keep-alive');
            String sReprintServ = '{"ReprintResponse": {"ReprintResult": {"Status": "Success", "Description": "Message Fulfillment Id :AFP VIEWDIRECTSS|AFPTEST|20180807064220|11550046501222018060120180630Z|20 Reprint request successfully submitted"}}}';
            oResponse.setBody(sReprintServ);
            oResponse.setStatusCode(200);             
            memClaims.responseParseSendStmt(oResponse);
            memClaims.createtemplateObjects();
            memClaims.sDocumentKey = 'AFP VIEWDIRECTSS|AFPTEST|20180807064220|1';
            memClaims.selectedValue = 'Mail';
            memclaims.populateTemplatedata();
            memClaims.selectedValue = 'Fax';
            memclaims.populateTemplatedata();
            memClaims.disButton();
            memClaims.shidepopup = 'Test';
            memClaims.bShowServiceError = false;
            memClaims.sObjName = 'Account';
            memClaims.checkSelected();
            Test.stopTest(); 
        }
    }
    
    /*
* Method Name : testServiceResponseClass
* Description : Used to validate ClaimSendStmt_Mbr_ServiceResponse_H_HUM class.
* Return type : void
* Parameter   : NA
*/
    
    static testMethod void testServiceResponseClass()
    {    
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        System.runAs(oCCSUser) 
        { 
            Test.StartTest();
            ClaimSendStmt_Mbr_ServiceResponse_H_HUM servResp = new ClaimSendStmt_Mbr_ServiceResponse_H_HUM();
            ClaimSendStmt_Mbr_ServiceResponse_H_HUM.ReprintResult result = new ClaimSendStmt_Mbr_ServiceResponse_H_HUM.ReprintResult();
            result.Status = 'Printed';
            result.Description = 'Test Desc';
            ClaimSendStmt_Mbr_ServiceResponse_H_HUM.ReprintResponse resp = new ClaimSendStmt_Mbr_ServiceResponse_H_HUM.ReprintResponse();
            resp.ReprintResult = result;
            servResp.ReprintResponse = resp;
            Test.stopTest(); 
        }
    }
    
    /*
* Method Name : testRTIMemberStatementTemplate
* Description : Used to validate RTI template
* Return type : void
* Parameter   : NA
*/
    static testMethod void testRTIMemberStatementTemplate()
    {    
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        System.runAs(oCCSUser) 
        { 
            Test.StartTest();
            Account oAccount = [Select Id,Name,LastName,Tenant_Id__c,RecordType.Name,Enterprise_ID__c,PersonHomePhone  from Account where LastName = 'Account'];
            System.assertEquals(oAccount.LastName,'Account');  
            Policy_Member__c oPolMem =  [Select Id,Subscriber__c ,Relationship__c,Policy__r.Group_Name__r.Home_Office_Account__c, Member__c, Policy__c, Policy_Member_External_ID__c , Policy__r.Product__r.ProductCode from Policy_Member__c where Member__c =: oAccount.Id];
            Test.setCurrentPage(Page.Claim_Send_Statement_VF_HUM);
            ApexPages.currentPage().getParameters().put('viewlink','test1');
            List<Account> lstAcc = new List<Account>();
            lstAcc.add(oAccount);
            Claim_Send_Statement_C_HUM memClaims = new Claim_Send_Statement_C_HUM();
            memClaims.showErrorMsg();  
            memClaims.lstLogErrors = new List<Error_Log__c>();
            memClaims.accIDTochk = lstAcc; 
            memClaims.sRTIPageId = oPolMem.id;
            memClaims.bIsPageFromRTI = true;
            memClaims.sPolMemID = oPolMem.id;
            memClaims.selectedValue = 'Mail';
            memClaims.sRefAddressLine1 = 'A123';  
            memClaims.sRefAddressLine2 = 'A123';
            memClaims.sRefCity = 'Sample';
            memClaims.sRefStatecode= '12345'; 
            memClaims.sRefZipcode= '12345';
            memClaims.repname = 'Test';
            memClaims.sMessageName = 'Test';
            memClaims.sRecipientType = 'Test';
            memClaims.sCategoryDesc= 'Test';            
            memClaims.sMailrepName= '12345'; 
            
            Template__c templateObj = new Template__c();
            templateObj.Description__c = 'Send Print Item: Member';
            insert templateObj;
            
            List<Template_Field__c> templateFieldObj = new List<Template_Field__c>();
            templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'RTIPrintMessageName'));
            templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'RTIPrintRecipientName'));
            templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'RTIPrintRecipientType'));
            templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'RTIPrintState'));        
            templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'RTIPrintWorkQueueName'));
            templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'RTIPrintZip'));
            templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'Data'));
            templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'RTIPrintAddressLine1'));
            templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'RTIPrintAddressLine2'));
            templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'RTIPrintApplicationKey'));
            templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'RTIPrintCategoryType'));
            templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'RTIPrintCity'));
            templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'RTIPrintDateTime'));
            templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'RTIPrintDeliveryMethod'));
            templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'RTIPrintDocumentKey'));
            insert templateFieldObj;
            
            HttpResponse oReprintResp = New HttpResponse();
            oReprintResp.setHeader('Content-Type', 'application/json');
            oReprintResp.setHeader('SOAPAction', '');    
            oReprintResp.setHeader('Connection','keep-alive');
            String sReprintJson = '{"ReprintResponse": {"ReprintResult": {"Status": "Success", "Description": "Message Fulfillment Id :AFP VIEWDIRECTSS|AFPTEST|20180807064220|11550046501222018060120180630Z|20 Reprint request successfully submitted"}}}';
            oReprintResp.setBody(sReprintJson);
            oReprintResp.setStatusCode(200); 
            memClaims.responseParseSendStmt(oReprintResp);
            memClaims.faxNumber = '';
            memClaims.ValidateFax();
            memClaims.faxNumber = '12345678';
            memClaims.ValidateFax();
            memClaims.sStatementType = 'Provider';
            memClaims.faxNumber = '1234567890111';
            memClaims.ValidateFax();
            Test.stopTest(); 
        }
    } 
    
    
    /*
* Method Name : testClaimStatementTemplate
* Description : Used to validate Member claim statement template.
* Return type : void
* Parameter   : NA
*/
    static testMethod void testClaimStatementTemplate()
    {    
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        System.runAs(oCCSUser) 
        { 
            Test.StartTest();
            Account oAccount = [Select Id,Name,LastName,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account'];
            System.assertEquals(oAccount.LastName,'Account');  
            Policy_Member__c oPolMem =  [Select Id,Subscriber__c ,Relationship__c,Policy__r.Group_Name__r.Home_Office_Account__c, Member__c, Policy__c, Policy_Member_External_ID__c , Policy__r.Product__r.ProductCode from Policy_Member__c where Member__c =: oAccount.Id];
            Test.setCurrentPage(Page.Claim_Send_Statement_VF_HUM);
            ApexPages.currentPage().getParameters().put('DocKey','A123DocumentKey123');
            ApexPages.currentPage().getParameters().put('statementType','Member');
            ApexPages.currentPage().getParameters().put('pid',oPolMem.Id);
            ApexPages.currentPage().getParameters().put('pCode','LV');
            ApexPages.currentPage().getParameters().put('mid','H123-00');
            Claim_Send_Statement_C_HUM memClaims = new Claim_Send_Statement_C_HUM();
            memClaims.showErrorMsg();
            memClaims.sMailrepName = 'Test';
            memClaims.sRefAddressLine1 = 'Test';
            memClaims.sRefCity = 'Hyd';
            memClaims.sRefZipcode = '12345';  
            memClaims.sRefStateCode = 'CE';
            memClaims.isFullAddress = false;
            memClaims.sRefAddressline2 = 'S 32ND ST';
            memClaims.bIsPageFromRTI= false;
            memClaims.sEndStatementPeriod='12';
            memClaims.sClaimNumber='12345';
            memClaims.sRemitId='12';
            memClaims.sRecipientType ='Test';
            memClaims.sDocumentKey = 'DocumentKey=AFP VIEWDIRECTSS|AFPTEST|20180807064220|1';
            memClaims.sApplicationKey='12';
            memClaims.sStatementTypeMobValue='12';
            memClaims.sStatementPeriod='123';
            
            Template__c templateObj = new Template__c();
            templateObj.Description__c = 'Send Claim Statement: Member';
            insert templateObj;
            
            List<Template_Field__c> templateFieldObj = new List<Template_Field__c>();
            templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'Data'));
            templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ClaimNumber'));
            templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'RemittanceID'));
            templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'DeliveryMethod'));        
            templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'RecipientName'));
            templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'RecipientType'));
            templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'AddressLine1'));
            templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'AddressLine2'));
            templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'City'));
            templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'State'));
            templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'Zip'));
            templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'FaxNumber'));
            templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'WorkQueueName'));
            templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'DocumentKey'));
            templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'ApplicationKey'));
            templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'StatementType'));
            templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'BeginStatementPeriod'));
            templateFieldObj.add(new Template_Field__c(Template__c=templateObj.Id,Data_Type__c='String',Name = 'EndStatementPeriod'));
            insert templateFieldObj;
            
            HttpResponse oReprintResp = New HttpResponse();
            oReprintResp.setHeader('Content-Type', 'application/json');
            oReprintResp.setHeader('SOAPAction', '');    
            oReprintResp.setHeader('Connection','keep-alive');
            String sReprintJson = '{"ReprintResponse": {"ReprintResult": {"Status": "Success", "Description": "Message Fulfillment Id :AFP VIEWDIRECTSS|AFPTEST|20180807064220|11550046501222018060120180630Z|20 Reprint request successfully submitted"}}}';
            oReprintResp.setBody(sReprintJson);
            oReprintResp.setStatusCode(200); 
            memClaims.responseParseSendStmt(oReprintResp);
            Test.stopTest(); 
        }
    } 
}