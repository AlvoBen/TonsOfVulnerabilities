/**************************************************************************** 
Apex Class Name  : ClaimDetails_T_HUM_V3
Version          : 1.0  
Created Date     : July 20 2020
Function         : Test class for ClaimDetails_C_HUM_V3
****************************************************************************
Modification Log:
* Developer Name          Code Review #        Date                       Description
*---------------------------------------------------------------------------------------------------
* Atia Uzma                                    05/05/2021                 User Story 2230288: T1PRJ0001827 - MF 1 - SF - TECH - On/Off Switch Cleanup for 04/16/2021, 05/14/2021 and 05/28/2021 releases (CRM)
* Anuradha Gajbhe                              06/01/2021                 User Story 2275290: Increased code coverage
* Prasanthi Kandula                            09/01/2021                 Increased code coverage
* Anuradha Gajbhe                              02/07/2022                 US 2732965, US 2732975 Increased Code Coverage
* Vamshi Krishna Pemberthi                     02/07/2022                 US 2732959 - Improved Code Coverage
* Anil parvathaneni                            06/27/2022                 US 3252318-  Improved Code Coverage 
* Pinky Vijur                                02/22/2023         User Story 4200769: T1PRJ0883127- Author Shutdown- MF 2- Display and Resend Author generated tEOB�s in CRM Service (Classic)
***************************************************************************************************/
@isTest
private Class ClaimDetails_T_HUM_V3
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
            HUM_Webservice_Callout__c oHUMClaimSummaryService = new HUM_Webservice_Callout__c();
            oHUMClaimSummaryService.name = 'ClaimsService_GetClaimsDetail_V3';
            oHUMClaimSummaryService.Active__c = true;
            oHUMClaimSummaryService.Certificate_Name__c = 'testts';
            oHUMClaimSummaryService.End_Point_URL__c = 'https://testst-cmservicews.hum.com/';
            oHUMClaimSummaryService.Timeout__c = '120000';
            oHUMClaimSummaryService.Content_Type__c = 'application/json';
            oHUMClaimSummaryService.Request_Type__c = 'POST';
            insert oHUMClaimSummaryService;
            system.assert(oHUMClaimSummaryService.id != null);
            system.assert(oHUMClaimSummaryService.name == 'ClaimsService_GetClaimsDetail_V3');
            
            HUM_Webservice_Callout__c oHUMClaimPaymentService = new HUM_Webservice_Callout__c();
            oHUMClaimPaymentService.name = 'ClaimsService_GetClaimsPayment_V3';
            oHUMClaimPaymentService.Active__c = true;
            oHUMClaimPaymentService.Certificate_Name__c = 'testts';
            oHUMClaimPaymentService.End_Point_URL__c = 'https://testst-cmservicews.hum.com/';
            oHUMClaimPaymentService.Timeout__c = '120000';
            oHUMClaimPaymentService.Content_Type__c = 'application/json';
            oHUMClaimPaymentService.Request_Type__c = 'POST';
            insert oHUMClaimPaymentService;
            system.assert(oHUMClaimPaymentService.id != null);
            system.assert(oHUMClaimPaymentService.name == 'ClaimsService_GetClaimsPayment_V3');
            
            HUM_Webservice_Callout__c oHUMMTVService = new HUM_Webservice_Callout__c();
            oHUMMTVService.name = 'MTVRemarksService_GetClaimsMTVRemarks';
            oHUMMTVService.Active__c = true;
            oHUMMTVService.Certificate_Name__c = 'testCert';
            oHUMMTVService.End_Point_URL__c = 'https://testst-cmservicews.hum.com/';
            oHUMMTVService.Timeout__c = '120000';
            oHUMMTVService.Content_Type__c = 'application/json';
            oHUMMTVService.Request_Type__c = 'POST';
            insert oHUMMTVService;
            system.assert(oHUMMTVService.id != null);
            system.assert(oHUMMTVService.name == 'MTVRemarksService_GetClaimsMTVRemarks');
            
            HUM_Webservice_Callout__c oDocumentList = new HUM_Webservice_Callout__c(Request_Type__c = 'POST', Name='MobiusService_GetDocumentList',
            End_Point_URL__c = 'test',
            Certificate_Name__c = 'Humana_GuidanceCare',
            Timeout__c = '12000',
            Content_Type__c = 'text/application');
            insert oDocumentList;
            System.assertEquals(oDocumentList.Timeout__c, '12000');
            
            HUM_Webservice_Callout__c oClaimDetails = new HUM_Webservice_Callout__c(Request_Type__c = 'POST', Name='ClaimsService_GetClaimsDetail_V3',
            End_Point_URL__c = 'test',
            Certificate_Name__c = 'Humana_GuidanceCare',
            Timeout__c = '12000',
            Content_Type__c = 'text/application');
            insert oClaimDetails;
            System.assertEquals(oDocumentList.Timeout__c, '12000');
            
            HUM_Webservice_Callout__c cstGCP = new HUM_Webservice_Callout__c();
            cstGCP.Name='HUMLaunchGCP';
            cstGCP.End_Point_URL__c = 'http://gcp.humana.com/utilities/LaunchGCP.aspx';
            insert cstGCP;
            system.assert(cstGCP.id!=null);
            system.assert(cstGCP.name == 'HUMLaunchGCP');
            
            HUM_Webservice_Callout__c cstCASWEB = new HUM_Webservice_Callout__c();
            cstCASWEB.Name='HUMLaunchCASWEBUI';
            cstCASWEB.End_Point_URL__c = 'https://claims.humana.com/CASUI/Launch/Launch';
            insert cstCASWEB;      
            system.assert(cstCASWEB.id!=null);
            system.assert(cstCASWEB.name == 'HUMLaunchCASWEBUI');
            
            if(cstGCP!=null) string sGCPUrl = cstGCP.End_Point_URL__c;
            if(cstCASWEB!=null)  string sCASWEBUrl = cstCASWEB.End_Point_URL__c;
            
            DocumentServer__c oDocServerSS = new DocumentServer__c(Name='DocumentServerSS', Value__c = 'AFP ViewDirectSS');
            DocumentServer__c oDocServerEOB = new DocumentServer__c(Name='DocumentServerEOB', Value__c = 'AFP ViewDirectTest');
            DocumentServer__c oDocServerEOR = new DocumentServer__c(Name='DocumentServerEOR', Value__c = 'AFP ViewDirectTest');            
            
            insert oDocServerSS;
            insert oDocServerEOB;            
            insert oDocServerEOR;                        
           
            Endpoint_Url_HUM__c oUrl = new Endpoint_Url_HUM__c();
            oURL.name='SSO_LOGINURL';
            oUrL.Remote_Url__c='test';
            insert oUrl;
            System.assertEquals(oURL.name,'SSO_LOGINURL');
            
            Endpoint_Url_HUM__c oCodeEditUrl = new Endpoint_Url_HUM__c();
            oCodeEditUrl.name='ClaimInquiryToolLink';
            oCodeEditUrl.Remote_Url__c='test';
            insert oCodeEditUrl;
            System.assertEquals(oCodeEditUrl.name,'ClaimInquiryToolLink');
    
            Endpoint_Url_HUM__c oCheckUrl=new Endpoint_Url_HUM__c();
            oCheckURL.name='CheckStatusToolLink';
            oCheckUrL.Remote_Url__c='test2';
            insert oCheckUrl;
            System.assertEquals(oCheckURL.name,'CheckStatusToolLink');             
               
            Account objAcc = HUMTestDataHelperTest.getAccount(); 
            objAcc.FirstName = 'Test';
            objAcc.LastName = 'Account';
            objAcc.Tenant_Id__c = '00';
            objAcc.Enterprise_ID__c = '1005577525';
            objAcc.General_Account__c = true;
            objAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();
            insert objAcc;
            System.assertEquals(objAcc.Tenant_Id__c,'00');            
                        
            Product2 oProd = new Product2(Name = 'Laptop X200',Segment_Type__c='MCR',ProductCode='MED', Major_LOB__c='MER');
            insert oProd;
            System.assertNotEquals(oProd.Id, NULL, 'Value not matched');        
            
            
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
            
            Account objPrv = new Account();
            objPrv.Name = 'Provider';
            objPrv.Tenant_Id__c = '00';
            objPrv.Enterprise_ID__c = '1005577526';
            objPrv.General_Account__c = true;
            objPrv.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMLaunchGCPCCP_Provider).getRecordTypeId();
            insert objPrv;
            System.assertEquals(objPrv.Tenant_Id__c,'00');
            
            Policy__c objPpol = HUMTestDataHelperTest.getPolicy();
            objPpol.Name='testPolicyPrv';
            objPpol.Group_Name__c = objPrv.Id;
            objPpol.Major_LOB__c = 'MER';
            objPpol.Source_Cust_Cov_Key__c = '123456789';
            objPpol.Product__c = oProd.Id;
            objPpol.Product__r = oProd;
            objPpol.Platform__c = 'LV';
            objPpol.Client_Number__c = '57';
            insert objPpol;
            system.assertEquals(objPpol.Source_Cust_Cov_Key__c,'123456789');
            
            Policy_Member__c objPPolMem = new Policy_Member__c();
            objPPolMem.Name = 'testpolmemPrv';
            objPPolMem.Relationship__c = 'Self';
            objPPolMem.Subscriber__c = objPPolMem.Id;
            objPPolMem.Effective_Date__c=Date.today().addDays(-1).format();
            objPPolMem.End_Date__c=Date.today().addDays(1).format();
            objPPolMem.Policy_Member_External_ID__c='00|ODS|6925004434817|12345678|34567|2013-07-011';
            objPPolMem.Member__c = objPrv.Id;
            objPPolMem.Policy__c = objPpol.Id;
            objPPolMem.Relationship_Code__c = '00';
            insert objPPolMem;
            system.assertEquals(objPPolMem.Relationship__c, 'Self');
            
            HUM_Webservice_Callout__c oHUMCodeEditDetails = new HUM_Webservice_Callout__c();
            oHUMCodeEditDetails.name = 'ClaimsService_GetClaimsCodeEdit_V3';
            oHUMCodeEditDetails.Active__c = true;
            oHUMCodeEditDetails.Certificate_Name__c = 'testts';
            oHUMCodeEditDetails.End_Point_URL__c = 'https://testst-cmservicews.hum.com/';
            oHUMCodeEditDetails.Timeout__c = '120000';
            oHUMCodeEditDetails.Content_Type__c = 'application/json';
            oHUMCodeEditDetails.Request_Type__c = 'POST';
            insert oHUMCodeEditDetails;
            system.assert(oHUMCodeEditDetails.id != null);
            system.assert(oHUMCodeEditDetails.name == 'ClaimsService_GetClaimsCodeEdit_V3');
            
            Account objAcc2 = HUMTestDataHelperTest.getAccount(); 
            objAcc2.FirstName = 'Account';
            objAcc2.LastName = 'AccountTest';
            objAcc2.Tenant_Id__c = '00';
            objAcc2.Enterprise_ID__c = '1005577525';
            objAcc2.General_Account__c = true;
            objAcc2.Mbr_Gen_Key__c = '4131413039909';
            objAcc2.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();
            insert objAcc2;
            System.assertEquals(objAcc2.Tenant_Id__c,'00');  
            
            Policy_Member__c objPPolMem2 = new Policy_Member__c();
            objPPolMem2.Name = 'testpolmemPrv1';
            objPPolMem2.Relationship__c = 'Self';
            objPPolMem2.Subscriber__c = objPPolMem.Id;
            objPPolMem2.Effective_Date__c=Date.today().addDays(-2).format();
            objPPolMem2.End_Date__c=Date.today().addDays(-1).format();
            objPPolMem2.Policy_Member_External_ID__c='00|ODS|6925004434817|12345678|34567|2013-07-022';
            objPPolMem2.Member__c = objAcc2.Id;
            objPPolMem2.Policy__c = objPpol.Id;
            objPPolMem2.Relationship_Code__c = '00';
            insert objPPolMem2;
            system.assertEquals(objPPolMem2.Relationship__c, 'Self');
            
            Policy_Member__c objPPolMem3 = new Policy_Member__c();
            objPPolMem3.Name = 'testpolmemPrv3';
            objPPolMem3.Relationship__c = 'Self';
            objPPolMem3.Subscriber__c = objPPolMem.Id;
            objPPolMem3.Effective_Date__c=Date.today().addDays(-2).format();
            objPPolMem3.End_Date__c=Date.today().addDays(-1).format();
            objPPolMem3.Policy_Member_External_ID__c='00|ODS|6925004434817|12345678|34567|2013-25-022';
            objPPolMem3.Member__c = objAcc2.Id;
            objPPolMem3.Policy__c = objPpol.Id;
            objPPolMem3.Relationship_Code__c = '00';
            insert objPPolMem3;
            system.assertEquals(objPPolMem3.Relationship__c, 'Self');
            
            Member_ID__c objMemberID2 = new Member_ID__c();
            objMemberID2.policy_member__c = objPPolMem2.id;
            objMemberID2.policy__c = objpol.Id;
            objMemberID2.Name = 'H1234-03';
            objMemberID2.Member_Card_External_ID__c = 'ODS12355'; 
            objMemberID2.Member_Dependent_Code__c  = '03';        
            objMemberID2.Type__c  = 'SSN';                    
            insert objMemberID2;
            system.assertequals('H1234-03',objMemberID2.Name );
            
             Member_ID__c objMemberID3 = new Member_ID__c();
            objMemberID3.policy_member__c = objPPolMem3.id;
            objMemberID3.policy__c = objpol.Id;
            objMemberID3.Name = 'H1234-03';
            objMemberID3.Member_Card_External_ID__c = 'ODS22355'; 
            objMemberID3.Member_Dependent_Code__c  = '03';        
            objMemberID3.Type__c  = 'SSN';                    
            insert objMemberID3;
            system.assertequals('H1234-03',objMemberID3.Name );
        
            Member_ID__c objMemberID1 = new Member_ID__c();
            objMemberID1.policy_member__c = objPolMem.id;
            objMemberID1.policy__c = objpol.Id;
            objMemberID1.Name = '12345';
            objMemberID1.Member_Card_External_ID__c = 'ODS12345'; 
            objMemberID1.Member_Dependent_Code__c  = '03';        
            objMemberID1.Type__c  = 'SSN';                    
            insert objMemberID1;
            system.assertequals('12345',objMemberID1.Name );
            
            Member_ID__c objMemberID = new Member_ID__c();
            objMemberID.policy_member__c = objPolMem.id;
            objMemberID.policy__c = objpol.Id;
            objMemberID.Name = 'H1234567-03';
            objMemberID.Member_Card_External_ID__c = 'ODS1234'; 
            objMemberID.Member_Dependent_Code__c  = '03'; 
            insert objMemberID;
            system.assertequals('H1234567-03',objMemberID.Name );
      
            //REQ-408299 Inserting applauncher in test class
      List<HUMConstants__c> listOfConstantToInsert = new List<HUMConstants__c>();
          listOfConstantToInsert.add(new HUMConstants__c(Name = 'APPLAUNCHER_ENVIRONMENT', StringValue__c = 'TEST', IntegerValue__c = 0));
          insert listOfConstantToInsert;
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
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        System.runAs (oCCSUser) 
        { 
            Test.StartTest();
            Account oAccount = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account'];  
            System.assertEquals(oAccount.Tenant_Id__c, '00');
            
            String sAccRecType = oAccount.RecordType.Name;
            System.assertEquals(sAccRecType,'Member');
            
            Policy_Member__c oPolMem =  [Select Id,Subscriber__c ,Relationship_Code__c,Relationship__c,Policy__r.Group_Name__r.Home_Office_Account__c, Member__c, Policy__c, Policy_Member_External_ID__c , Policy__r.Product__r.ProductCode from Policy_Member__c where Member__c =: oAccount.Id];
            System.assertEquals(oPolMem.Relationship__c, 'Self');
            
            Member_ID__c newid = [select Member_Dependent_Code__c, Policy_Member__r.Effective_Date__c, Policy_Member__r.End_Date__c, Policy_Member__c, Name from Member_ID__c where name='12345'];
            System.assertEquals(newid.Name,'12345');
            
            HUM_Webservice_Callout__c oSvcHeaderPayment = [Select Request_Type__c,End_Point_URL__c,Certificate_Name__c,Timeout__c,Content_Type__c From HUM_Webservice_Callout__c where name='ClaimsService_GetClaimsDetail_V3' Limit 1];
            
            Test.setCurrentPage(Page.ClaimDetails_VF_HUM_V3);
            ApexPages.currentPage().getParameters().put('Id',oPolMem.Id);
            System.assertEquals(ApexPages.currentPage().getParameters().get('Id'),oPolMem.Id);
            
            ApexPages.currentPage().getParameters().put('Name',oAccount.Name);
            System.assertEquals(ApexPages.currentPage().getParameters().get('Name'),oAccount.Name);
            ApexPages.currentPage().getParameters().put('ClaimType', 'Medical');
            ApexPages.currentPage().getParameters().put('ClaimNbr', '67890');
            ApexPages.currentPage().getParameters().put('ClaimGenKey', '12345');
            ApexPages.currentPage().getParameters().put('MemberId', newid.Name);
            ClaimDetails_C_HUM_V3 oClaimDetails=new ClaimDetails_C_HUM_V3();
            oClaimDetails.getMemberGenKey(oPolMem.id);
            oClaimDetails.bPaymentZeroCheckNum = true;
            oClaimDetails.bPaymentNonZeroCheckNum = false;
            oClaimDetails.sAdjInd = 'Y';
            oClaimDetails.sPlatformCode = 'EM';
            ClaimDetails_C_HUM_V3.EnumYesNo eAdjInd;
            ClaimDetails_C_HUM_V3.EnumStatementType estatement;
            ClaimDetails_C_HUM_V3.EnumDocumentType eDoc;
            ClaimDetails_C_HUM_V3.EnumPlatform eplatform;
            Object objVal = oClaimDetails.claimDetailsRequest();
            System.assertNotEquals(Null,objVal);
            Continuation objCon = new Continuation(120);
            objCon.continuationMethod='processResponse';
            objVal = oClaimDetails.processResponse();
            System.assertEquals(Null,objVal);
            oClaimDetails.processPaymentsResponse();
            Object objVal1 = oClaimDetails.initiatePaymentRequest();
            oClaimDetails.sRequest =null;
            oClaimDetails.processResponse();
            
            Test.stopTest(); 
         }
      }  
      
    /*
     * Method Name : TestDetailscatchblock
     * Description : Used to validate detail request functionality.
     * Return type : void
     * Parameter   : NA
     */
    static testMethod void TestDetailscatchblock()
    {    
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        System.runAs (oCCSUser) 
        { 
            Test.StartTest();
            ClaimDetails_C_HUM_V3 oClaimDetails=new ClaimDetails_C_HUM_V3();
            oClaimDetails.sPlatformCode = 'LV';
            oClaimDetails.sAdjInd = 'Y';
            oClaimDetails.claimDetailsRequest();
            oClaimDetails.initiateMTVRequest();
            string state ='test';
            HttpResponse response = new HttpResponse();
            response =null;
            oClaimDetails.processPay(response);
            oClaimDetails.processPaymentsResponse();
            Object conObject = ClaimDetails_C_HUM_V3.processResponseCodeEdit(state);
            system.assertEquals(conObject,null);
            
            Object objVal = oClaimDetails.claimDetailsRequest();
            Continuation objCon = new Continuation(121);
            objCon.continuationMethod='processResponse';
            objVal = oClaimDetails.processResponse();
            Test.stopTest(); 
         }
      }  
      
      
    /*
     * Method Name : testDeserialize
     * Description : Used to validate deserialization functionality.
     * Return type : void
     * Parameter   : NA
     */
    static testMethod void testDeserialize() 
    {
        Test.setCurrentPage(Page.ClaimDetails_VF_HUM_V3);
        User objUser = [SELECT Id,Name FROM User  WHERE Lastname='test User1' LIMIT 1];
        System.assertEquals('test User1',objUser.Name);
        system.runAs(objUser)
        {
            Test.startTest();
            ClaimDetails_C_HUM_V3 oClaimDetails = new ClaimDetails_C_HUM_V3(); 
            
            HttpResponse oResponse = new HttpResponse();
            oResponse.setHeader('Content-Type', 'application/json');
            string sjson = '{ "ClaimDetailReponse":{ "ClaimDetailResult":{ "ClaimNbr":"201501126071443", "PatRecordNbr":"439370P105748-P1", "DiagCode":{ "Code":[ "V202", "V061", "V0481", "V040" ] }, "DiagIndicator":"9", "ClmMediaCd":"OTHER", "SrcAuthKey":"", "SrcLvCASPrefix":"", "DeficiencyInd":"Y", "DeficiencyField":"SUBSCRIBNM", "DeficiencyDate":"01/12/2015", "DeficiencyDesc":"INVALID SUBSCRIBER NAME SUBMITTED.", "ChargeAmt":"490.00", "BenAllowAmt":"355.84", "ProvWriteOff":"134.16", "BenDenyAmt":"80.00", "MbrRespAmt":"0.00", "PaidAmt":"275.84", "SRCNPIID":"1235130592", "TotalInterest":"0.00", "ExcludeAmt":"367.16", "CobPaidAmt":"0.00", "ClaimDetailLines":{ "ClaimDetailLine":[ { "LineNbr":"1", "ServiceStartDate":"01/05/2015", "ServiceEndDate":"01/05/2015", "CheckNbr":"", "ParInd":"Y", "FeeSchdId":"0000", "ProviderCISContractId":"", "ChargeAmt":"153.00", ' +
               ' "BenAllowAmt":"0.00", "ProvWriteOff":"134.16", "BenDenyAmt":"0.00", "DeductAmt":"0.00", "CopayAmt":"0.00", "CoInsAmt":"0.00", "MbrRespAmt":"0.00", "PaidAmt":"0.00", "ExcludeAmt":"153.00", "BenPayPctCd":"", "CobPaidAmt":"0.00", "LastProcessDate":"01/12/2015", "PaymentCodes":{ "PaymentCode":[ { "PmtCategory":"F1", "PmtCode":"65", "PmtEntity":"", "HIPAAMessage":"F1-Finalized/Payment.65-CLAIM/LINE HAS BEEN PAID.", "LegacyCd":"", "LegacyMessage":"" } ] }, "RemitNumber":"", "PayeeCd":"Group", "SrcClaimLineSequence":"0", "VoucherCd":"", "SrcPOTCode":"11", "POTLongDesc":"OFFICE VISIT", "DiagCode":{ "Code":[ "V202", "V061", "V0481", "V040", "V202", "V061", "V0481", "V040", "V202", "V061", "V0481", "V040", "V202", "V061", "V0481", "V040", "V202", "V061", "V0481", "V040", "V202", "V061", "V0481", "V040", "V202", "V061", "V0481", "V040", "V202", ' +
               ' "V061", "V0481", "V040", "V202", "V061", "V0481", "V040", "V202", "V061", "V0481", "V040", "V202", "V061", "V0481", "V040" ] }, "PaidStatDesc":"PAID", "ServiceCode":"99382", "ServiceDesc":"INITIAL PREVENTIVE MEDICINE NE", "Revenuecode":"", "CPTModCode":"25", "AnesMinutes":"0.0", "ServUnitCnt":"1.0", "CauseCd":"", "ToothCode":"", "ToothSurface":"" }, { "LineNbr":"2", "ServiceStartDate":"01/05/2015", "ServiceEndDate":"01/05/2015", "CheckNbr":"0000000000", "ParInd":"Y", "FeeSchdId":"0000", "ProviderCISContractId":"", "ChargeAmt":"25.00", "BenAllowAmt":"12.30", "ProvWriteOff":"134.16", "BenDenyAmt":"0.00", "DeductAmt":"0.00", "CopayAmt":"0.00", "CoInsAmt":"0.00", "MbrRespAmt":"0.00", "PaidAmt":"12.30", "ExcludeAmt":"12.70", "BenPayPctCd":"", "CobPaidAmt":"0.00", "LastProcessDate":"01/17/2015", "PaymentCodes":{ "PaymentCode":[ { "PmtCategory":"F1", ' +
               ' "PmtCode":"65", "PmtEntity":"", "HIPAAMessage":"F1-Finalized/Payment.65-CLAIM/LINE HAS BEEN PAID.", "LegacyCd":"6H02", "LegacyMessage":"This provider is a member of the Humana Network. Services are discounted according to the negotiated rate." } ] }, ' +
               ' "RemitNumber":"201501210007980", "PayeeCd":"Group", "SrcClaimLineSequence":"0", "VoucherCd":"", "SrcPOTCode":"11", "POTLongDesc":"OFFICE VISIT", "DiagCode":{ "Code":[ "V202", "V061", "V0481", "V040", "V202", "V061", "V0481", "V040", "V202", "V061", "V0481", "V040", "V202", "V061", "V0481", "V040", "V202", "V061", "V0481", "V040", "V202", "V061", "V0481", "V040", "V202", "V061", "V0481", "V040", "V202", "V061", "V0481", "V040", "V202", "V061", "V0481", "V040", "V202", "V061", "V0481", "V040", "V202", "V061", "V0481", "V040" ] }, "PaidStatDesc":"PAID", "ServiceCode":"92551", "ServiceDesc":"SCREENING TEST PURE TONE AIR O", "Revenuecode":"", "CPTModCode":"", "AnesMinutes":"0.0", "ServUnitCnt":"1.0", "CauseCd":"", "ToothCode":"", "ToothSurface":"" }] } } } }';
            oResponse.setBody(sjson);
            oResponse.setStatusCode(200);
            //oClaimDetails.ResponseDeserialize(oResponse); 
            system.assertEquals(oClaimDetails.bShowServiceError,false); 
            Test.stopTest();
      }
    }
    
    /*
     * Method Name : claimLineItemsException
     * Description : This method tests the Exception handling 
     * Return type : void
     * Parameter   : None
     */ 
    static testMethod void claimLineItemsException() 
    {
        Test.setCurrentPage(Page.ClaimDetails_VF_HUM_V3);
        User objUser = [SELECT Id,Name FROM User  WHERE Lastname='test User1' LIMIT 1];
        System.assertEquals('test User1',objUser.Name);
        system.runAs(objUser)
        {
            Test.startTest();
            ClaimDetails_C_HUM_V3 oClaimDetails = new ClaimDetails_C_HUM_V3();
            HttpResponse oResponse = new HttpResponse();
            oResponse.setHeader('Content-Type', 'application/json');
            string sjson  = 'test json';
            oResponse.setBody(sjson);
            oResponse.setStatusCode(200); 
            oClaimDetails.ResponseDeserialize(oResponse); 
            oResponse = new HttpResponse();
            oResponse.setHeader('Content-Type', 'application/json');
            sjson  = 'test json';
            oResponse.setBody(sjson);
            oResponse.setStatusCode(300); 
            oClaimDetails.ResponseDeserialize(oResponse);
            System.assertEquals(true,oClaimDetails.bShowServiceError);
            Test.stopTest();
      }
    } 
    
    /*
     * Method Name : claimLineItemsNullResponse
     * Description : This method tests the Exception for Null Response 
     * Return type : void
     * Parameter   : None
     */ 
    static testMethod void claimLineItemsNullResponse() 
    {
        Test.setCurrentPage(Page.ClaimDetails_VF_HUM_V3);
        User objUser = [SELECT Id,Name FROM User  WHERE Lastname='test User1' LIMIT 1];
        System.assertEquals('test User1',objUser.Name);
        
        system.runAs(objUser )
        {
            Test.startTest();
            ClaimDetails_C_HUM_V3 oClaimDetails = new ClaimDetails_C_HUM_V3();
            HttpResponse oResponse = new HttpResponse();
            oResponse.setHeader('Content-Type', 'application/json');
            string sjson  = '{"data":"test"}';
            oResponse.setBody(sjson);
            oResponse.setStatusCode(200); 
            oClaimDetails.oclaimdetails.bShowMore = true;
            oClaimDetails.ResponseDeserialize(oResponse);
            System.assertEquals(true,oClaimDetails.bShowServiceError);
            Test.stopTest();
      }
    }  
   /*
     * Method Name : claimsMTVRemarks
     * Description : This method tests MTV Remarks 
     * Return type : void
     * Parameter   : None
     */ 
    static testMethod void claimsMTVRemarks() 
    {
        Test.setCurrentPage(Page.ClaimDetails_VF_HUM_V3);
        User u = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
        system.runAs(u)
        {
            Test.startTest();
            ClaimDetails_C_HUM_V3 oClaimDetails = new ClaimDetails_C_HUM_V3();
            ClaimDetailMTVRemarks_DTO_HUM oMTV = new ClaimDetailMTVRemarks_DTO_HUM();
            oMTV.sPagingInd = 'Y';
            oMTV.sSurrogateId = '123456';
            oMTV.sRemarkSurrogateId = '1234567';
            oMTV.sRemarkText = 'Test Remarks';
            oMTV.sRemarkSequenceNumber = '1';
            
            ClaimDetailMTVRemarks_DTO_HUM oMTV1 = new ClaimDetailMTVRemarks_DTO_HUM();
            oMTV1.sPagingInd = 'N';
            oMTV1.sSurrogateId = '123456';
            oMTV1.sRemarkSurrogateId = '123456';
            oMTV1.sRemarkText = 'Test Remarks';
            oMTV1.sRemarkSequenceNumber = '2';
            
            List<ClaimDetailMTVRemarks_DTO_HUM> lstMTV = new List<ClaimDetailMTVRemarks_DTO_HUM>();
            lstMTV.add(oMTV);
            lstMTV.add(oMTV1);
            
            ClaimDetailMTVRemarks_DTO_HUM oMTV2 = new ClaimDetailMTVRemarks_DTO_HUM();
            oMTV2.sPagingInd = 'N';
            oMTV2.sSurrogateId = '123456';
            oMTV2.sRemarkSurrogateId = '123456';
            oMTV2.sRemarkText = 'Test Remarks';
            oMTV2.sRemarkSequenceNumber = '3';
            
            ClaimDetailMTVRemarks_DTO_HUM oMTV3 = new ClaimDetailMTVRemarks_DTO_HUM();
            oMTV3.sPagingInd = 'Y';
            oMTV3.sSurrogateId = '123456';
            oMTV3.sRemarkSurrogateId = '24234324';
            oMTV3.sRemarkText = 'Test Remarks';
            oMTV3.sRemarkSequenceNumber = '4';
            
            oClaimDetails.mapMTV.put('123456',oMTV2);
            oClaimDetails.mapMTV.put('24234324',oMTV3);
            
            ClaimDetails_C_HUM_V3 oDetails = oClaimDetails.getThis();
            System.assertEquals(2, oDetails.mapMTV.size());
            
            sObject osObject = oClaimDetails.getRecord();
            System.assertEquals(Null , osObject );

             List<sObject> listsObj = oClaimDetails.getRecords();
            System.assertEquals(Null , listsObj);
                     
             
            ClaimsServiceDetailsResponse_H_HUM_V2.ClaimDetailResult oResult = new  ClaimsServiceDetailsResponse_H_HUM_V2.ClaimDetailResult();
            ClaimsServiceDetailsResponse_H_HUM_V2.DiagnosisCode oCode = new ClaimsServiceDetailsResponse_H_HUM_V2.DiagnosisCode();
            List<ClaimLineItems_DTO_HUM_V2.ClaimDetailLine> oClaimLineItems = new List<ClaimLineItems_DTO_HUM_V2.ClaimDetailLine>();
            ClaimLineItems_DTO_HUM_V2.ClaimDetailLine oClaimLineItem = new ClaimLineItems_DTO_HUM_V2.ClaimDetailLine();
            oClaimLineItem.sCheckNbr = '0';
            oClaimLineItems.Add(oClaimLineItem);
            List<String> lstDiagCode = new List<String>();
            lstDiagCode.add('78821');
            ocode.Code = lstDiagCode;
            oResult.DiagnosisCode = oCode;
            oClaimDetails.sMTVJsonData ='test';
            oClaimDetails.responseParse(oResult);
            //System.assertEquals(0,oClaimDetails.lstDiagnosisCodes.size());
            //oClaimDetails.bSwitch_2132792 = true;
            object obj2 = oClaimDetails.initiateMTVRequest();
            System.assertNotEquals(null,obj2 );
            oClaimDetails.mapMTV.put('24234324',oMTV3);
            oClaimDetails.processMTVResponse();
            oClaimDetails.prepareMTVRemarkslist(lstMTV);
            System.assertEquals(0,oClaimDetails.lstMTVRemarks.size());
            List<ClaimDetailMTVRemarks_DTO_HUM> lstMTVRemarks1 = new List<ClaimDetailMTVRemarks_DTO_HUM>();
            oClaimDetails.prepareMTVRemarkslist(lstMTVRemarks1 );
            System.assertEquals(true,oClaimDetails.bShowMTVServiceError);   
            oClaimDetails.validateCheckNumberandRemidID(oClaimLineItems); 
            System.assertEquals(true,oClaimDetails.bPaymentZeroCheckNum);               
            HttpResponse response = new HttpResponse();
            response.setBody('{"MTVRemarksResponse": {"PagingInd": "Y","StartRemarkSequenceNumber": "01","CreationDate": "20081213","CreationTime": "073632419584","SurrogateId":"821307363241","RemarksList":{"Remarks":[{"EntityType": "Claim","Identifier": "201506246744894","RemarkSequenceNumber": "01","RemarkType": "TMID","RemarkCategory": "       ","CreationTimestamp": "03/18/2015 10:14:13.283735","RemarkText": "0046121855141                                                                                                                                                                                                                                            ","SurrogateId": "731810141328"},{"EntityType": "Claim","Identifier": "201506246744894","RemarkSequenceNumber": "01","RemarkType": "OI  ","RemarkCategory": "       ","CreationTimestamp": "03/07/2013 02:02:01.405088","RemarkText":"FROM-VAT: VAT ATTEMPTED 02 CALLS,RESPONSE WAS INBOUND,SUBSCRIBER RESPONSE WAS NO  ,CONTACT DATE WAS 03052013","SurrogateId": "930702020140"},{"EntityType": "Claim","Identifier": "201506246744894","RemarkSequenceNumber":"01","RemarkType": "OI  ","RemarkCategory": "       ","CreationTimestamp": "03/01/2013 09:15:02.555065","RemarkText": "TO-VAT: SUBSCRIBER INFORMATION SENT FOR OTHER INSURANCE VERIFICATION THROUGH VOICE AUTOMATED TECHNOLOGY       PROCESS","SurrogateId": "730109150255"}]}}}');
            response.setStatusCode(200);
            Test.setContinuationResponse(oClaimDetails.sRequest, response);
            oClaimDetails.processMTVResponse();
            oClaimDetails.prepareMTVRemarkslist(null);
            Test.stopTest();
      }
    }
    
    static testMethod void claimsMTVRemarks1() 
    {
        Test.setCurrentPage(Page.ClaimDetails_VF_HUM_V3);
        User u = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
        system.runAs(u)
        {
            Test.startTest();
            ClaimDetails_C_HUM_V3 oClaimDetails = new ClaimDetails_C_HUM_V3();
            ClaimDetailMTVRemarks_DTO_HUM oMTV = new ClaimDetailMTVRemarks_DTO_HUM();
            oMTV.sPagingInd = 'Y';
            oMTV.sSurrogateId = '123456';
            oMTV.sRemarkSurrogateId = '1234567';
            oMTV.sRemarkText = 'Test Remarks';
            oMTV.sRemarkSequenceNumber = '1';
            
            ClaimDetailMTVRemarks_DTO_HUM oMTV1 = new ClaimDetailMTVRemarks_DTO_HUM();
            oMTV1.sPagingInd = 'N';
            oMTV1.sSurrogateId = '123456';
            oMTV1.sRemarkSurrogateId = '123456';
            oMTV1.sRemarkText = 'Test Remarks';
            oMTV1.sRemarkSequenceNumber = '2';
            
            List<ClaimDetailMTVRemarks_DTO_HUM> lstMTV = new List<ClaimDetailMTVRemarks_DTO_HUM>();
            lstMTV.add(oMTV);
            lstMTV.add(oMTV1);
            
            ClaimDetailMTVRemarks_DTO_HUM oMTV2 = new ClaimDetailMTVRemarks_DTO_HUM();
            oMTV2.sPagingInd = 'N';
            oMTV2.sSurrogateId = '123456';
            oMTV2.sRemarkSurrogateId = '123456';
            oMTV2.sRemarkText = 'Test Remarks';
            oMTV2.sRemarkSequenceNumber = '3';
            
            ClaimDetailMTVRemarks_DTO_HUM oMTV3 = new ClaimDetailMTVRemarks_DTO_HUM();
            oMTV3.sPagingInd = 'Y';
            oMTV3.sSurrogateId = '123456';
            oMTV3.sRemarkSurrogateId = '24234324';
            oMTV3.sRemarkText = 'Test Remarks';
            oMTV3.sRemarkSequenceNumber = '4';
            
            oClaimDetails.mapMTV.put('123456',oMTV2);
            oClaimDetails.mapMTV.put('24234324',oMTV3);
            
            ClaimDetails_C_HUM_V3 oDetails = oClaimDetails.getThis();
            System.assertEquals(2, oDetails.mapMTV.size());
            
            sObject osObject = oClaimDetails.getRecord();
            System.assertEquals(Null , osObject );

             List<sObject> listsObj = oClaimDetails.getRecords();
            System.assertEquals(Null , listsObj);
                     
             
            ClaimsServiceDetailsResponse_H_HUM_V2.ClaimDetailResult oResult = new  ClaimsServiceDetailsResponse_H_HUM_V2.ClaimDetailResult();
            ClaimsServiceDetailsResponse_H_HUM_V2.DiagnosisCode oCode = new ClaimsServiceDetailsResponse_H_HUM_V2.DiagnosisCode();
            List<ClaimLineItems_DTO_HUM_V2.ClaimDetailLine> oClaimLineItems = new List<ClaimLineItems_DTO_HUM_V2.ClaimDetailLine>();
            ClaimLineItems_DTO_HUM_V2.ClaimDetailLine oClaimLineItem = new ClaimLineItems_DTO_HUM_V2.ClaimDetailLine();
            oClaimLineItem.sCheckNbr = '0';
            oClaimLineItems.Add(oClaimLineItem);
            List<String> lstDiagCode = new List<String>();
            lstDiagCode.add('78821');
            ocode.Code = lstDiagCode;
            oResult.DiagnosisCode = oCode;
            oClaimDetails.sMTVJsonData ='test';
            oClaimDetails.responseParse(oResult);
            System.assertnotEquals(0,oClaimDetails.lstDiagnosisCodes.size());
            oClaimDetails.bSwitch_2132792 = true;
            object obj2 = (Continuation)oClaimDetails.initiateMTVRequest();
            System.assertEquals(null,obj2 );
            oClaimDetails.processResponse();
            oClaimDetails.mapMTV.put('24234324',oMTV3);
            oClaimDetails.processMTVResponse();
            oClaimDetails.prepareMTVRemarkslist(lstMTV);
            System.assertEquals(0,oClaimDetails.lstMTVRemarks.size());
            List<ClaimDetailMTVRemarks_DTO_HUM> lstMTVRemarks1 = new List<ClaimDetailMTVRemarks_DTO_HUM>();
            oClaimDetails.prepareMTVRemarkslist(lstMTVRemarks1 );
            System.assertEquals(true,oClaimDetails.bShowMTVServiceError);   
            oClaimDetails.validateCheckNumberandRemidID(oClaimLineItems); 
            System.assertEquals(true,oClaimDetails.bPaymentZeroCheckNum);               
            HttpResponse response = new HttpResponse();
            response.setBody('{"MTVRemarksResponse": {"PagingInd": "Y","StartRemarkSequenceNumber": "01","CreationDate": "20081213","CreationTime": "073632419584","SurrogateId":"821307363241","RemarksList":{"Remarks":[{"EntityType": "Claim","Identifier": "201506246744894","RemarkSequenceNumber": "01","RemarkType": "TMID","RemarkCategory": "       ","CreationTimestamp": "03/18/2015 10:14:13.283735","RemarkText": "0046121855141                                                                                                                                                                                                                                            ","SurrogateId": "731810141328"},{"EntityType": "Claim","Identifier": "201506246744894","RemarkSequenceNumber": "01","RemarkType": "OI  ","RemarkCategory": "       ","CreationTimestamp": "03/07/2013 02:02:01.405088","RemarkText":"FROM-VAT: VAT ATTEMPTED 02 CALLS,RESPONSE WAS INBOUND,SUBSCRIBER RESPONSE WAS NO  ,CONTACT DATE WAS 03052013","SurrogateId": "930702020140"},{"EntityType": "Claim","Identifier": "201506246744894","RemarkSequenceNumber":"01","RemarkType": "OI  ","RemarkCategory": "       ","CreationTimestamp": "03/01/2013 09:15:02.555065","RemarkText": "TO-VAT: SUBSCRIBER INFORMATION SENT FOR OTHER INSURANCE VERIFICATION THROUGH VOICE AUTOMATED TECHNOLOGY       PROCESS","SurrogateId": "730109150255"}]}}}');
            response.setStatusCode(200);
            Test.setContinuationResponse(oClaimDetails.sRequest, response);
            oClaimDetails.processMTVResponse();
            oClaimDetails.prepareMTVRemarkslist(null);
            Test.stopTest();
      }
    }
    
    /*
     * Method Name : testDeserializePay
     * Description : Used to validate processPay functionality.
     * Return type : void
     * Parameter   : NA
     */
    static testMethod void testDeserializePay() 
    {
        Test.setCurrentPage(Page.ClaimDetails_VF_HUM_V3);
        User objUser = [SELECT Id,Name FROM User  WHERE Lastname='test User1' LIMIT 1];
        System.assertEquals('test User1',objUser.Name);
        system.runAs(objUser)
        {
            Test.startTest();
            ClaimDetails_C_HUM_V3 oClaimDetails = new ClaimDetails_C_HUM_V3();
            HttpResponse oResponse = new HttpResponse();
            oResponse.setHeader('Content-Type', 'application/json');
            string sjson = '{ "GetEhub835XmlResponse":{ "GetEhub835XmlResult":{ "Transaction_835":[ { "CheckNumber":"000034085", "PayeeName":"MAYES COUNTY HMPN LLC", "PayeeAddress":"PO BOX 11456,BELFAST,ME,049154005", "CheckDate":"08/17/2014", "PaymentType":"CHK", "PaymentAmount":"69.97", "ClaimDetails":{ "ClaimDetail":[ { "ClaimNumber":"996377165", "ClaimPaymentAmount":"46.73" }, { "ClaimNumber":"994884560", "ClaimPaymentAmount":"23.24" } ] }, "PaymentTypeID":"", "PayeeID":"285067-0000000000000", "PaymentIssueDate":"08/17/2014", "PaymentStatus":"", "PaymentStatusDate":"" } ] } } }';
            oResponse.setBody(sjson);
            oResponse.setStatusCode(200); 
            oClaimDetails.sPlatformCode = 'EM';
            oClaimDetails.sPaymentLastProcessDate = '06/05/2015 (2)';
            oClaimDetails.processPay(oResponse);
            Test.stopTest();
        }
    }
    
     /*
     * Method Name : testDeserializePay1
     * Description : Used to validate processPay functionality.
     * Return type : void
     * Parameter   : NA
     */
    static testMethod void testDeserializePay1() 
    {
        Test.setCurrentPage(Page.ClaimDetails_VF_HUM_V3);
        User objUser = [SELECT Id,Name FROM User  WHERE Lastname='test User1' LIMIT 1];
        System.assertEquals('test User1',objUser.Name);
        system.runAs(objUser)
        {
            Test.startTest();
            ClaimDetails_C_HUM_V3 oClaimDetails = new ClaimDetails_C_HUM_V3();
            HttpResponse oResponse = new HttpResponse();
            oResponse.setHeader('Content-Type', 'application/json');
            oClaimDetails.eAdjInd = ClaimDetails_C_HUM_V3.EnumYesNo.Y;
            string sjson = '{ "GetEhub835XmlResponse":{ "GetEhub835XmlResult":{ "Transaction_835":[] } } }';
            oResponse.setBody(sjson);
            oResponse.setStatusCode(200); 
            oClaimDetails.sPlatformCode = 'EM';
            oClaimDetails.sPaymentLastProcessDate = '06/05/2015 (2)';
            oClaimDetails.processPay(oResponse);
            Test.stopTest();
        }
    }
    
     /*
     * Method Name : testDeserializePay2
     * Description : Used to validate processPay functionality.
     * Return type : void
     * Parameter   : NA
     */
    static testMethod void testDeserializePay2() 
    {
        Test.setCurrentPage(Page.ClaimDetails_VF_HUM_V3);
        User objUser = [SELECT Id,Name FROM User  WHERE Lastname='test User1' LIMIT 1];
        System.assertEquals('test User1',objUser.Name);
        system.runAs(objUser)
        {
            Test.startTest();
            ClaimDetails_C_HUM_V3 oClaimDetails = new ClaimDetails_C_HUM_V3();
            HttpResponse oResponse = new HttpResponse();
            oResponse.setHeader('Content-Type', 'application/json');
            oClaimDetails.eAdjInd = ClaimDetails_C_HUM_V3.EnumYesNo.N;
            oClaimDetails.sStatus = 'Pended';
            string sjson = '{ "GetEhub835XmlResponse":{ "GetEhub835XmlResult":{ "Transaction_835":[] } } }';
            oResponse.setBody(sjson);
            oResponse.setStatusCode(200); 
            oClaimDetails.sPlatformCode = 'EM';
            oClaimDetails.sPaymentLastProcessDate = '06/05/2015 (2)';
            oClaimDetails.processPay(oResponse);
            Test.stopTest();
        }
    }
    /*
     * Method Name : testDeserializePay3
     * Description : Used to validate processPay functionality.
     * Return type : void
     * Parameter   : NA
     */
    static testMethod void testDeserializePay3() 
    {
        Test.setCurrentPage(Page.ClaimDetails_VF_HUM_V3);
        User objUser = [SELECT Id,Name FROM User  WHERE Lastname='test User1' LIMIT 1];
        System.assertEquals('test User1',objUser.Name);
        system.runAs(objUser)
        {
            Test.startTest();
            ClaimDetails_C_HUM_V3 oClaimDetails = new ClaimDetails_C_HUM_V3();
            HttpResponse oResponse = new HttpResponse();
            oResponse.setHeader('Content-Type', 'application/json');
            oClaimDetails.eAdjInd = ClaimDetails_C_HUM_V3.EnumYesNo.N;
            string sjson = '{ "GetEhub835XmlResponse":{ "GetEhub835XmlResult":{ "Transaction_835":[] } } }';
            oResponse.setBody(sjson);
            oResponse.setStatusCode(200); 
            oClaimDetails.sPlatformCode = 'EM';
            oClaimDetails.sPaymentLastProcessDate = '06/05/2015 (2)';
            oClaimDetails.processPay(oResponse);
            Test.stopTest();
        }
    }
    /*
     * Method Name : testDeserializeCodeEdit
     * Description : Used to validate Code Edit Rationale functionality.
     * Return type : void
     * Parameter   : NA
     */
    static testMethod void testDeserializeCodeEdit() 
    {
        Test.setCurrentPage(Page.ClaimDetails_VF_HUM_V3);
        User objUser = [SELECT Id,Name FROM User  WHERE Lastname='test User1' LIMIT 1];
        System.assertEquals('test User1',objUser.Name);
        system.runAs(objUser)
        {
            Test.startTest();
            ClaimDetails_C_HUM_V3 oClaimDetails = new ClaimDetails_C_HUM_V3();
            HttpResponse oResponse = new HttpResponse();
            oResponse.setHeader('Content-Type', 'application/json');
            string sjson = '{ "GetCodeEditDetailsResponse":{ "RationaleDTO":{ "ExCodes":{ "ExCode":[ "6H0" ] }, "RejectedProcedureCodes":{ "RejectedProcedureDTO":[ { "ExCode":"6H0", "Platform":"EM", "ProcCode":"90837" } ] }, "PaidProcedureCodes":{ "PaidProcedureCode":[ "90837" ] }, "PaidModifers":{ "PaidModifer":[  ] }, "DiagnosisCodes":{ "DiagnosisCode":[ { "DiagnosisCd":"314.01", "DiagnosisCodeIndicator":"Diag9" } ] }, "RationaleDescriptionCodes":{ "RationaleDescriptionDTO":[ { "CodeEditInd":"HumanaCodeEdit", "ExCode":"6H0", "RationaleDesc":" MTV claim, EX Code 6H0 ", "RejectedProcedureCode":"90837" } ] }, "RationaleExceptionCodes":{ "RationaleExceptionDTO":[  ] } } } }';
            oResponse.setBody(sjson);
            oResponse.setStatusCode(200); 
            List<String> lstDiagCode = new List<String>();
            lstDiagCode.add('VS123');
            lstDiagCode.add('AS123');
            List<ClaimLineItems_DTO_HUM_V2.PaymentCode> lstPaymentCode = new List<ClaimLineItems_DTO_HUM_V2.PaymentCode>();
            ClaimLineItems_DTO_HUM_V2.PaymentCode oPaymentCd = new ClaimLineItems_DTO_HUM_V2.PaymentCode();
            oPaymentCd.sLegacyCd = '123'; 
            oPaymentCd.sLegacyMessage = 'Legacy Message';
            lstPaymentCode.add(oPaymentCd);
            ClaimsGetCodeEditDetails_DTO_HUM  ocodeEditDTO = new ClaimsGetCodeEditDetails_DTO_HUM();
            ocodeEditDTO.sClaimNbr = '123';
            Object obj = ClaimDetails_C_HUM_V3.getRationale(lstDiagCode,lstPaymentCode,'EM','9','PAID','123','123','456');
            List<ClaimsGetCodeEditDetails_DTO_HUM>  oClaimCodeEditDTO = ClaimDetails_C_HUM_V3.responseDeserializeCodeEdit(oResponse,ocodeEditDTO);
            system.assertNotEquals(oClaimCodeEditDTO,null);
            Test.stopTest();
      }
    }
    
    /*
     * Method Name : testDeserializeCodeEditNegative
     * Description : Used to validate Code Edit Rationale functionality Negative scenarios.
     * Return type : void
     * Parameter   : NA
     */
    static testMethod void testDeserializeCodeEditNegative() 
    {
        Test.setCurrentPage(Page.ClaimDetails_VF_HUM_V3);
        User objUser = [SELECT Id,Name FROM User  WHERE Lastname='test User1' LIMIT 1];
        System.assertEquals('test User1',objUser.Name);
        system.runAs(objUser)
        {
            Test.startTest();
            ClaimDetails_C_HUM_V3 oClaimDetails = new ClaimDetails_C_HUM_V3();
            HttpResponse oResponse = new HttpResponse();
            oResponse.setHeader('Content-Type', 'application/json');
            string sjson = '';
            oResponse.setBody(sjson);
            oResponse.setStatusCode(200);
            ClaimsGetCodeEditDetails_DTO_HUM  ocodeEditDTO = new ClaimsGetCodeEditDetails_DTO_HUM();
            ocodeEditDTO.sClaimNbr = '123';
            List<ClaimsGetCodeEditDetails_DTO_HUM>  oClaimCodeEditDTO = ClaimDetails_C_HUM_V3.responseDeserializeCodeEdit(oResponse,ocodeEditDTO); 
            List<String> lstDiagCode = new List<String>();
            lstDiagCode.add('VS123');
            lstDiagCode.add('AS123');
            List<ClaimLineItems_DTO_HUM_V2.PaymentCode> lstPaymentCode = new List<ClaimLineItems_DTO_HUM_V2.PaymentCode>();
            ClaimLineItems_DTO_HUM_V2.PaymentCode oPaymentCd = new ClaimLineItems_DTO_HUM_V2.PaymentCode();
            oPaymentCd.sLegacyCd = '123'; 
            oPaymentCd.sLegacyMessage = 'Legacy Message';
            lstPaymentCode.add(oPaymentCd);
            Object obj = ClaimDetails_C_HUM_V3.getRationale(lstDiagCode,lstPaymentCode,'EM','9','','123','123','345');
            System.assertNotEquals(obj , null);
            Test.stopTest();
      }
    }
    
   /*
    * Method Name   :  TestDocListPositiveData
    * Description   :  This method is used to  test prepareDocSearchReq function
    * Return Type   :  void
    * Parameters    :  NA
    */  
    static testMethod void TestDocListPositiveData() 
    {
        
        User objUser = [SELECT Id,Name FROM User  WHERE Lastname='test User1' LIMIT 1];
        System.assertEquals('test User1',objUser.Name);
        system.runAs(objUser)
        {
            Test.startTest();
            
            Account oAccount = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account'];  
            System.assertEquals(oAccount.Tenant_Id__c, '00');
            String sAccRecType = oAccount.RecordType.Name;
            System.assertEquals(sAccRecType,'Member');
            
            Policy_Member__c oPolMem =  [Select Id,Subscriber__c ,Relationship__c,Policy__r.Group_Name__r.Home_Office_Account__c, Member__c, Policy__c, Policy_Member_External_ID__c , Policy__r.Product__r.ProductCode from Policy_Member__c where Member__c =: oAccount.Id];
            System.assertEquals(oPolMem.Relationship__c, 'Self');
            test.setCurrentPage(Page.ClaimDetails_VF_HUM_V3);
            ApexPages.currentPage().getParameters().put('Id',oPolMem.Id); 
            System.assertEquals(ApexPages.currentPage().getParameters().get('Id'),oPolMem.Id);
            ApexPages.currentPage().getParameters().put('PlatformCd','LV'); 
            System.assertEquals(ApexPages.currentPage().getParameters().get('PlatformCd'),'LV');
            ApexPages.currentPage().getParameters().put('MemberId','H01539816'); 
            System.assertEquals(ApexPages.currentPage().getParameters().get('MemberId'),'H01539816');
            DocumentServer__c oDocServerSS = [Select Name, Value__c from DocumentServer__c where name='DocumentServerSS' Limit 1];
            DocumentServer__c oDocServerEOB = [Select Name, Value__c from DocumentServer__c where name='DocumentServerEOB' Limit 1];            
            ClaimDetails_C_HUM_V3 oClaimDetails = new ClaimDetails_C_HUM_V3();
            oClaimDetails.prepareDocSearchReq(ClaimDetails_C_HUM_V3.EnumDocumentType.SS);
            //If condition for EOB Doctype
            oClaimDetails.prepareDocSearchReq(ClaimDetails_C_HUM_V3.EnumDocumentType.EOB);
            HttpResponse response = new HttpResponse();
            response.setBody('{"getDocumentListResponse":{"getDocumentListReturn":{"Search12":[{"TopicResultList":{"TopicResult12":[{"TopicKeyList":{"key":[{"topicName": "EOBMEMBER","PersonId": "","statementBeginDate": "","statementEndDate": "","statementType": ""}]},"DocumentKey": "AFP WI EOB|EOBSTMT  |20151231045205|LVH53778960021102861 |4"}]}}]}}}'); 
            Test.setContinuationResponse(oClaimDetails.sClaimStatementsRequest, response);
            oClaimDetails.processClaimStatementsResponse();
            Test.stopTest();
        }
    }  
    
    /*
    * Method Name   :  TestClaimStatementsPositiveData() 
    * Description   :  This method is used to  test positive scenario for ClaimStatements controller
    * Return Type   :  void
    * Parameters    :  NA
    */  
    static testMethod void TestClaimStatementsPositiveData() 
    {
        
        User objUser = [SELECT Id,Name FROM User  WHERE Lastname='test User1' LIMIT 1];
        System.assertEquals('test User1',objUser.Name);
        system.runAs(objUser)
        {
            Test.startTest();
            Account oAccount = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account'];  
            System.assertEquals(oAccount.Tenant_Id__c, '00');
            String sAccRecType = oAccount.RecordType.Name;
            System.assertEquals(sAccRecType,'Member');
            
            Policy_Member__c oPolMem =  [Select Id,Subscriber__c ,Relationship__c,Policy__r.Group_Name__r.Home_Office_Account__c, Member__c, Policy__c, Policy_Member_External_ID__c , Policy__r.Product__r.ProductCode from Policy_Member__c where Member__c =: oAccount.Id];
            System.assertEquals(oPolMem.Relationship__c, 'Self');
            
            HUM_Webservice_Callout__c oSvcHeaderPayment = [Select Request_Type__c,End_Point_URL__c,Certificate_Name__c,Timeout__c,Content_Type__c From HUM_Webservice_Callout__c where name='MobiusService_GetDocumentList' Limit 1];
            
            DocumentServer__c oDocServerSS = [Select Name, Value__c from DocumentServer__c where name='DocumentServerSS' Limit 1];
            DocumentServer__c oDocServerEOB = [Select Name, Value__c from DocumentServer__c where name='DocumentServerEOB' Limit 1]; 
            
            test.setCurrentPage(Page.ClaimDetails_VF_HUM_V3);
            ApexPages.currentPage().getParameters().put('Id',oPolMem.Id); 
            System.assertEquals(ApexPages.currentPage().getParameters().get('Id'),oPolMem.Id);
            ApexPages.currentPage().getParameters().put('PlatformCd','LV'); 
            System.assertEquals(ApexPages.currentPage().getParameters().get('PlatformCd'),'LV');
            ApexPages.currentPage().getParameters().put('MemberId','H01539816'); 
            System.assertEquals(ApexPages.currentPage().getParameters().get('MemberId'),'H01539816');
                      
            ClaimDetails_C_HUM_V3 oClaimDetails = new ClaimDetails_C_HUM_V3();
            // Invoke the continuation by calling the action method
            Continuation conti = (Continuation)oClaimDetails.initClaimStatementsRequest();
            // Verify that the continuation has the proper requests
            Map<String, HttpRequest> requests = conti.getRequests();
            system.assert(requests.size() == 1);
            system.assert(requests.get(oClaimDetails.sClaimStatementsRequest) != null);
            // Perform mock callout 
            HttpResponse response = new HttpResponse();
            response.setBody('{"getDocumentListResponse":{"getDocumentListReturn":{"DocumentSearch":[{"TopicResultList":{"TopicResult":[{"TopicKeyList":{"TopicKey":[{"topicName": "EOBMEMBER","PersonId": "","statementBeginDate": "","statementEndDate": "","statementType": ""}]},"DocumentKey": "AFP WI EOB|EOBSTMT  |20151231045205|LVH53778960021102861 |4"}]}}]}}}'); 
            Test.setContinuationResponse(oClaimDetails.sClaimStatementsRequest, response);
            // Invoke callback method     
            Object result = Test.invokeContinuationMethod(oClaimDetails, conti);  
            System.assertEquals(null, result);
           //False Response Scenario 
           response.setBody('Mock Test Body');
           result = Test.invokeContinuationMethod(oClaimDetails, conti);    
           System.assertEquals(null, result);
           Test.stopTest();    
        }
    } 
    
       /*
    * Method Name   :  TestProviderClaimStatementsPositiveData() 
    * Description   :  This method is used to  test positive scenario for Provider ClaimStatements controller
    * Return Type   :  void
    * Parameters    :  NA
    */  
    static testMethod void TestProviderClaimStatementsPositiveData() 
    {
        
        User objUser = [SELECT Id,Name FROM User  WHERE Lastname='test User1' LIMIT 1];
        System.assertEquals('test User1',objUser.Name);
        system.runAs(objUser)
        {
            Test.startTest();
            Account oAccount = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where Name = 'Provider'];  
            System.assertEquals(oAccount.Tenant_Id__c, '00');
            String sAccRecType = oAccount.RecordType.Name;
            System.assertEquals(sAccRecType,'Provider');
            
            Policy_Member__c oPolMem =  [Select Id,Subscriber__c ,Relationship__c,Policy__r.Group_Name__r.Home_Office_Account__c, Member__c,Member__r.id, Policy__c, Policy_Member_External_ID__c , Policy__r.Product__r.ProductCode from Policy_Member__c where Member__c =: oAccount.Id];
            System.assertEquals(oPolMem.Relationship__c, 'Self');
            
            HUM_Webservice_Callout__c oSvcHeaderPayment = [Select Request_Type__c,End_Point_URL__c,Certificate_Name__c,Timeout__c,Content_Type__c From HUM_Webservice_Callout__c where name='MobiusService_GetDocumentList' Limit 1];
            
            DocumentServer__c oDocServerEOR = [Select Name, Value__c from DocumentServer__c where name='DocumentServerEOR' Limit 1];
            
            test.setCurrentPage(Page.ClaimDetails_VF_HUM_V3);
            ApexPages.currentPage().getParameters().put('Id',oPolMem.Id); 
            System.assertEquals(ApexPages.currentPage().getParameters().get('Id'),oPolMem.Id);
            ApexPages.currentPage().getParameters().put('PlatformCd','LV'); 
            System.assertEquals(ApexPages.currentPage().getParameters().get('PlatformCd'),'LV');
            ApexPages.currentPage().getParameters().put('MemberId',oPolMem.Member__r.id); 
            System.assertEquals(ApexPages.currentPage().getParameters().get('MemberId'),oPolMem.Member__r.id);
                      
            ClaimDetails_C_HUM_V3 oClaimDetails = new ClaimDetails_C_HUM_V3();
            // Invoke the continuation by calling the action method
            Continuation conti = (Continuation)oClaimDetails.initPrvdrClaimStatementsRequest();
            Test.stopTest();    
        }
    }   
    
    /*
    * Method Name   :  TestClaimStatementsNegativeData() 
    * Description   :  This method is used to  test positive scenario for ClaimStatements controller
    * Return Type   :  void
    * Parameters    :  NA
    */  
     static testMethod void TestClaimStatementsNegativeData() 
      {
        User objUser = [SELECT Id,Name FROM User  WHERE Lastname='test User1' LIMIT 1];
        System.assertEquals('test User1',objUser.Name);
        system.runAs(objUser)
        {
            Test.startTest();
            
            Account oAccount = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account'];  
            System.assertEquals(oAccount.Tenant_Id__c, '00');
            
            Policy_Member__c oPolMem =  [Select Id,Subscriber__c ,Relationship__c,Policy__r.Group_Name__r.Home_Office_Account__c, Member__c, Policy__c, Policy_Member_External_ID__c , Policy__r.Product__r.ProductCode from Policy_Member__c where Member__c =: oAccount.Id];
            System.assertEquals(oPolMem.Relationship__c, 'Self');
            
            test.setCurrentPage(Page.ClaimDetails_VF_HUM_V3);
            ApexPages.currentPage().getParameters().put('Id',oPolMem.Id); 
            System.assertEquals(ApexPages.currentPage().getParameters().get('Id'),oPolMem.Id);
            ApexPages.currentPage().getParameters().put('MemberId','H01539816'); 
            System.assertEquals(ApexPages.currentPage().getParameters().get('MemberId'),'H01539816');
            DocumentServer__c oDocServerSS = [Select Name, Value__c from DocumentServer__c where name='DocumentServerSS' Limit 1];
            DocumentServer__c oDocServerEOB = [Select Name, Value__c from DocumentServer__c where name='DocumentServerEOB' Limit 1];            
            ClaimDetails_C_HUM_V3 oClaimDetails = new ClaimDetails_C_HUM_V3();
            oClaimDetails.prepareDocSearchReq(ClaimDetails_C_HUM_V3.EnumDocumentType.EOB);  
            oClaimDetails.processClaimStatementsResponse();
            Test.stopTest();
        }
    }
    /*
    * Method Name   :  TestMTVCalloutData() 
    * Description   :  This method is used to  test positive scenario for ClaimStatements controller
    * Return Type   :  void
    * Parameters    :  NA
    */  
    static testMethod void TestMTVCalloutData() 
    {  
        User objUser = [SELECT Id,Name FROM User  WHERE Lastname='test User1' LIMIT 1];
        System.assertEquals('test User1',objUser.Name);
        system.runAs(objUser)
        {
            Test.startTest();
            ClaimDetails_C_HUM_V3 oClaimDetails = new ClaimDetails_C_HUM_V3();
            // Invoke the continuation by calling the action method
            Continuation cont = (Continuation)oClaimDetails.initiateMTVRequest();
            // Verify that the continuation has the proper requests
            Map<String, HttpRequest> requests = cont.getRequests();
            system.assert(requests.size() == 1);
            system.assert(requests.get(oClaimDetails.sRequest) != null);
            // Perform mock callout 
            HttpResponse response = new HttpResponse();
            response.setBody('{"MTVRemarksResponse": {"PagingInd": "Y","StartRemarkSequenceNumber": "01","CreationDate": "20081213","CreationTime": "073632419584","SurrogateId":"821307363241","RemarksList":{"Remarks":[{"EntityType": "Claim","Identifier": "201506246744894","RemarkSequenceNumber": "01","RemarkType": "TMID","RemarkCategory": "       ","CreationTimestamp": "03/18/2015 10:14:13.283735","RemarkText": "0046121855141                                                                                                                                                                                                                                            ","SurrogateId": "731810141328"},{"EntityType": "Claim","Identifier": "201506246744894","RemarkSequenceNumber": "01","RemarkType": "OI  ","RemarkCategory": "       ","CreationTimestamp": "03/07/2013 02:02:01.405088","RemarkText":"FROM-VAT: VAT ATTEMPTED 02 CALLS,RESPONSE WAS INBOUND,SUBSCRIBER RESPONSE WAS NO  ,CONTACT DATE WAS 03052013","SurrogateId": "930702020140"},{"EntityType": "Claim","Identifier": "201506246744894","RemarkSequenceNumber":"01","RemarkType": "OI  ","RemarkCategory": "       ","CreationTimestamp": "03/01/2013 09:15:02.555065","RemarkText": "TO-VAT: SUBSCRIBER INFORMATION SENT FOR OTHER INSURANCE VERIFICATION THROUGH VOICE AUTOMATED TECHNOLOGY       PROCESS","SurrogateId": "730109150255"}]}}}'); 
            Test.setContinuationResponse(oClaimDetails.sRequest, response);
            // Invoke callback method     
            Object result = Test.invokeContinuationMethod(oClaimDetails, cont);  
            System.assertEquals(null, result);
           //False Response Scenario 
           response.setBody('Mock Test Body');
           result = Test.invokeContinuationMethod(oClaimDetails, cont);
           System.assertEquals(null, result);           
           Test.stopTest();    
        }
    }     
    static testMethod void TestMTVCalloutData1() 
    {  
        User objUser = [SELECT Id,Name FROM User  WHERE Lastname='test User1' LIMIT 1];
        System.assertEquals('test User1',objUser.Name);
        system.runAs(objUser)
        {
            Test.startTest();
            ClaimDetails_C_HUM_V3 oClaimDetails = new ClaimDetails_C_HUM_V3();
            // Invoke the continuation by calling the action method
            oClaimDetails.bSwitch_2132792 = true;
            Continuation cont = (Continuation)oClaimDetails.initiateMTVRequest();
            // Verify that the continuation has the proper requests
                    
           Test.stopTest();    
        }
    } 
   /*
    * Method Name   :  TestClaimDetailCalloutData() 
    * Description   :  This method is used to  test positive scenario for ClaimStatements controller
    * Return Type   :  void
    * Parameters    :  NA
    */  
    static testMethod void TestClaimDetailCalloutData() 
    {  
        User objUser = [SELECT Id,Name FROM User  WHERE Lastname='test User1' LIMIT 1];
        System.assertEquals('test User1',objUser.Name);
        system.runAs(objUser)
        {
            Test.startTest();
            
            Account oAccount = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account'];  
            System.assertEquals(oAccount.Tenant_Id__c, '00');
            
            String sAccRecType = oAccount.RecordType.Name;
            System.assertEquals(sAccRecType,'Member');
            
            Policy_Member__c oPolMem =  [Select Id,Subscriber__c ,Relationship__c,Policy__r.Group_Name__r.Home_Office_Account__c, Member__c, Policy__c, Policy_Member_External_ID__c , Policy__r.Product__r.ProductCode from Policy_Member__c where Member__c =: oAccount.Id];
            System.assertEquals(oPolMem.Relationship__c, 'Self');
            
            HUM_Webservice_Callout__c oSvcHeaderPayment = [Select Request_Type__c,End_Point_URL__c,Certificate_Name__c,Timeout__c,Content_Type__c From HUM_Webservice_Callout__c where name='ClaimsService_GetClaimsDetail_V3' Limit 1];
            
            Member_ID__c newid = [select Member_Dependent_Code__c, Policy_Member__r.Effective_Date__c, Policy_Member__r.End_Date__c, Policy_Member__c, Name from Member_ID__c where name='H1234567-03'];
            System.assertEquals(newid.Name,'H1234567-03');
            
            Test.setCurrentPage(Page.ClaimDetails_VF_HUM_V3);
            ApexPages.currentPage().getParameters().put('Id',oPolMem.Id);
            System.assertEquals(ApexPages.currentPage().getParameters().get('Id'),oPolMem.Id);
            
            ApexPages.currentPage().getParameters().put('Name',oAccount.Name);
            System.assertEquals(ApexPages.currentPage().getParameters().get('Name'),oAccount.Name);
            ApexPages.currentPage().getParameters().put('ClaimType', 'Medical');
            ApexPages.currentPage().getParameters().put('ClaimNbr', '67890');
            ApexPages.currentPage().getParameters().put('ClaimGenKey', '12345');
            ApexPages.currentPage().getParameters().put('MemberId', newid.Name);
            ApexPages.currentPage().getParameters().put('ProviderId','391351762');
            ApexPages.currentPage().getParameters().put('Npi','12345678');
            ClaimDetails_C_HUM_V3 oClaimDetails = new ClaimDetails_C_HUM_V3();
            oClaimDetails.sAdjInd = 'Y'; 
            oClaimDetails.oClaimDetails.bShowMore = true; 
            oClaimDetails.oClaimDetails.sProviderSpecFirstRecord = 'Y'; 
            Continuation con = (Continuation)oClaimDetails.claimDetailsRequest(); 
            Map<String, HttpRequest> requests = con.getRequests();
            system.assert(requests.size() == 1);
            system.assert(requests.get(oClaimDetails.sRequest) != null);
            HttpResponse response = new HttpResponse();
            response.setBody('{"ClaimDetailReponse":{ "ClaimDetailResult":{   "ClaimNbr":"201501126071443", "PatRecordNbr":"439370P105748-P1","DiagCode":{ "Code":[ "V202", "V061", "V0481", "V040" ]},"ClmMediaCd":"OTHER", "SrcAuthKey":"0000000000014","SrcLvCASPrefix":"","DeficiencyInd":"Y","DeficiencyField":"SUBSCRIBNM", "DeficiencyDate":"01/12/2015","DeficiencyDesc":"INVALID SUBSCRIBERNAME SUBMITTED.","ChargeAmt":"490.00","BenAllowAmt":"355.84", "ProvWriteOff":"134.16","BenDenyAmt":"80.00","DeductAmt":"0.00","CopayAmt":"0.00","CoInsAmt":"0.00","MbrRespAmt":"0.00","PaidAmt":"275.84","SRCNPIID":"1235130592","TotalInterest":"0.00","ClaimDetailLines":{"ClaimDetailLine":[{"LineNbr":"1","ServiceStartDate":"01/05/2015","ServiceEndDate":"01/05/2015","CheckNbr":"","ParInd":"Y","FeeSchdId":"0000","ProviderCISContractId":"","ChargeAmt":"153.00","BenAllowAmt":"0.00","ProvWriteOff":"134.16","BenDenyAmt":"0.00","DeductAmt":"0.00","CopayAmt":"0.00","CoInsAmt":"0.00","MbrRespAmt":"0.00","PaidAmt":"0.00","ExcludeAmt":"153.00","BenPayPctCd":"","CobPaidAmt":"0.00","LastProcessDate":"01/12/2015","PaymentCodes":{"PaymentCode":[{ "PmtCategory":"F1", "PmtCode":"65","PmtEntity":"","HIPAAMessage":"F1-Finalized/Payment.65-CLAIM/LINE HAS BEEN PAID.","LegacyCd":"", "LegacyMessage":""}]},"RemitNumber":"","PayeeCd":"Group","SrcClaimLineSequence":"","VoucherCd":"","SrcPOTCode":"11","POTLongDesc":"OFFICE VISIT","DiagCode":{ "Code":[ "V202", "V061", "V0481", "V040","V202", "V061", "V0481", "V040", "V202", "V061", "V0481","V040", "V202", "V061", "V0481", "V040", "V202", "V061","V0481", "V040", "V202", "V061", "V0481", "V040", "V202","V061", "V0481", "V040", "V202", "V061", "V0481", "V040","V202", "V061", "V0481", "V040", "V202", "V061", "V0481","V040", "V202", "V061", "V0481", "V040" ] },"ServiceCode":"99382","ServiceDesc":"INITIAL PREVENTIVE MEDICINE NE","Revenuecode":"","CPTModCode":"25","AnesMinutes":"0.0","ServUnitCnt":"1.0","CauseCd":"","ToothCode":"","ToothSurface":""}]} }}}');
            Test.setContinuationResponse(oClaimDetails.sRequest, response);
            Object result = Test.invokeContinuationMethod(oClaimDetails, con);  
            System.assertEquals(null, result);
            response.setBody('Mock Test Body');
            List<Member_Id__c> lstMembers = [Select Id,Name from Member_Id__c where name='H1234567-03'];
            Id [] fixedSearchResults= new Id[1];
            fixedSearchResults[0] = lstMembers[0].Id;
            Test.setFixedSearchResults(fixedSearchResults);
            ID polmemId=oClaimDetails.getMemberParameters(newid.Name);
            result = Test.invokeContinuationMethod(oClaimDetails, con);   
            system.assertEquals(result,null);        
            Test.stopTest();    
        }
    }         
    
     /*
     * Method Name : testPaymentService
     * Description : Used to validate Nonzero payment check number.
     * Return type : void
     * Parameter   : NA
     */
     static testMethod void testPaymentService()
    {    
       User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        System.runAs (oCCSUser) 
        { 
            Test.StartTest();
            Account oAccount = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account'];  
            System.assertEquals(oAccount.Tenant_Id__c, '00');
            
            String sAccRecType = oAccount.RecordType.Name;
            System.assertEquals(sAccRecType,'Member');
            
            Policy_Member__c oPolMem =  [Select Id,Subscriber__c ,Relationship_Code__c,Relationship__c,Policy__r.Group_Name__r.Home_Office_Account__c, Member__c, Policy__c, Policy_Member_External_ID__c , Policy__r.Product__r.ProductCode from Policy_Member__c where Member__c =: oAccount.Id];
            System.assertEquals(oPolMem.Relationship__c, 'Self');
            
            Member_ID__c newid = [select Member_Dependent_Code__c, Policy_Member__r.Effective_Date__c, Policy_Member__r.End_Date__c, Policy_Member__c, Name from Member_ID__c where name='12345'];
            System.assertEquals(newid.Name,'12345');
            
            HUM_Webservice_Callout__c oSvcHeaderPayment = [Select Request_Type__c,End_Point_URL__c,Certificate_Name__c,Timeout__c,Content_Type__c From HUM_Webservice_Callout__c where name='ClaimsService_GetClaimsDetail_V3' Limit 1];
            
            Test.setCurrentPage(Page.ClaimDetails_VF_HUM_V3);
            ApexPages.currentPage().getParameters().put('Id',oPolMem.Id);
            System.assertEquals(ApexPages.currentPage().getParameters().get('Id'),oPolMem.Id);
            ApexPages.currentPage().getParameters().put('Name',oAccount.Name);
            System.assertEquals(ApexPages.currentPage().getParameters().get('Name'),oAccount.Name);
            ApexPages.currentPage().getParameters().put('ClaimType', 'Medical');
            ApexPages.currentPage().getParameters().put('ClaimNbr', '67890');
            ApexPages.currentPage().getParameters().put('ClaimGenKey', '12345');
            ApexPages.currentPage().getParameters().put('MemberId', newid.Name);
            ClaimDetails_C_HUM_V3 oClaimDetails=new ClaimDetails_C_HUM_V3();
            oClaimDetails.bPaymentZeroCheckNum = true;
            oClaimDetails.bPaymentNonZeroCheckNum = true;
            oClaimDetails.sAdjInd = 'Y';
            oClaimDetails.sPlatformCode = 'EM';
            oClaimDetails.sPaymentCheckNumber = '0012500';
            
            Object objVal = oClaimDetails.claimDetailsRequest();
            System.assertNotEquals(Null,objVal);
            Continuation objCon = new Continuation(120);
            objCon.continuationMethod='processResponse';
            objVal = oClaimDetails.processResponse();
            System.assertEquals(Null,objVal);
            oClaimDetails.processPaymentsResponse();
            Object objVal1 = oClaimDetails.initiatePaymentRequest();
            oClaimDetails.sRequest =null;
            oClaimDetails.processResponse();
            ClaimsDocumentsList_DTO_HUM docList = new ClaimsDocumentsList_DTO_HUM();
            ClaimMobiusDetails_S_HUM oPrvdrStatements = new ClaimMobiusDetails_S_HUM();
            docList.sDocumentKey = 'sdsa';
            docList.sPersonId = 'daf';
            docList.sTopicName = 'Test';
            HttpResponse oResponse = new HttpResponse();
            string sjson = '{"getDocumentListResponse": {"getDocumentListReturn": {"DocumentSearch": [{"TopicResultList": {"TopicResult": [{"TopicKeyList":{"TopicKey":[{"topicName": "PersonId","PersonId": "1001981051","MemberGenKey": "4131413039909","statementBeginDate": "2019-10-13","statementEndDate": "2019-10-14","statementType": "XBSF",       "statementDescription": "Family Roll-up  SmartEOB"}]},"DocumentKey": "AFP VIEWDIRECTSS|AFPTEST   |20191014062823|41314130399092019101320191014X|10","PrintedDate": "2019-10-14T06:28:23"}]}}]}}}';
            oResponse.setHeader('Content-Type', 'application/json');
            oResponse.setBody(sjson);
            Test.setContinuationResponse(oClaimDetails.sPrvdrClaimStatementsRequest, oResponse);
            List<ClaimsDocumentsList_DTO_HUM> lstClaimsStatementsResp1 = new List<ClaimsDocumentsList_DTO_HUM>();
            lstClaimsStatementsResp1.add(docList);
            oClaimDetails.sPolMemID = oPolMem.Id;
            oClaimDetails.processPrvdrClaimStatementsResponse(); 
            Test.stopTest(); 
         }
      } 

    /*
     * Method Name : testPaymentService1
     * Description : Used to validate Nonzero payment check number.
     * Return type : void
     * Parameter   : NA
     */
     static testMethod void testPaymentService1()
    {    
       User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        System.runAs (oCCSUser) 
        { 
            Test.StartTest();
            Account oAccount = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account'];  
            System.assertEquals(oAccount.Tenant_Id__c, '00');
            
            String sAccRecType = oAccount.RecordType.Name;
            System.assertEquals(sAccRecType,'Member');
            
            Policy_Member__c oPolMem =  [Select Id,Subscriber__c ,Relationship_Code__c,Relationship__c,Policy__r.Group_Name__r.Home_Office_Account__c, Member__c, Policy__c, Policy_Member_External_ID__c , Policy__r.Product__r.ProductCode from Policy_Member__c where Member__c =: oAccount.Id];
            System.assertEquals(oPolMem.Relationship__c, 'Self');
            
            Member_ID__c newid = [select Member_Dependent_Code__c, Policy_Member__r.Effective_Date__c, Policy_Member__r.End_Date__c, Policy_Member__c, Name from Member_ID__c where name='12345'];
            System.assertEquals(newid.Name,'12345');
            
            HUM_Webservice_Callout__c oSvcHeaderPayment = [Select Request_Type__c,End_Point_URL__c,Certificate_Name__c,Timeout__c,Content_Type__c From HUM_Webservice_Callout__c where name='ClaimsService_GetClaimsDetail_V3' Limit 1];
            
            Test.setCurrentPage(Page.ClaimDetails_VF_HUM_V3);
            ApexPages.currentPage().getParameters().put('Id',oPolMem.Id);
            System.assertEquals(ApexPages.currentPage().getParameters().get('Id'),oPolMem.Id);
            ApexPages.currentPage().getParameters().put('Name',oAccount.Name);
            System.assertEquals(ApexPages.currentPage().getParameters().get('Name'),oAccount.Name);
            ApexPages.currentPage().getParameters().put('ClaimType', 'Medical');
            ApexPages.currentPage().getParameters().put('ClaimNbr', '67890');
            ApexPages.currentPage().getParameters().put('ClaimGenKey', '12345');
            ApexPages.currentPage().getParameters().put('MemberId', newid.Name);
            ClaimDetails_C_HUM_V3 oClaimDetails=new ClaimDetails_C_HUM_V3();
            oClaimDetails.bPaymentZeroCheckNum = true;
            oClaimDetails.bPaymentNonZeroCheckNum = true;
            oClaimDetails.sAdjInd = 'Y';
            oClaimDetails.sPlatformCode = 'EM';
            oClaimDetails.sPaymentCheckNumber = '0012500';
            
            Object objVal = oClaimDetails.claimDetailsRequest();
            System.assertNotEquals(Null,objVal);
            Continuation objCon = new Continuation(120);
            objCon.continuationMethod='processResponse';
            objVal = oClaimDetails.processResponse();
            System.assertEquals(Null,objVal);
            oClaimDetails.processPaymentsResponse();
            Object objVal1 = oClaimDetails.initiatePaymentRequest();
            oClaimDetails.sRequest =null;
            oClaimDetails.processResponse();
            ClaimsDocumentsList_DTO_HUM docList = new ClaimsDocumentsList_DTO_HUM();
            ClaimMobiusDetails_S_HUM oPrvdrStatements = new ClaimMobiusDetails_S_HUM();
            docList.sDocumentKey = 'sdsa';
            docList.sPersonId = 'daf';
            docList.sTopicName = 'Test';
            HttpResponse oResponse = new HttpResponse();
            string sjson = '{"getDocumentListResponse": {"getDocumentListReturn": {"DocumentSearch": []}}}';
            oResponse.setHeader('Content-Type', 'application/json');
            oResponse.setBody(sjson);
            Test.setContinuationResponse(oClaimDetails.sPrvdrClaimStatementsRequest, oResponse);
            List<ClaimsDocumentsList_DTO_HUM> lstClaimsStatementsResp1 = new List<ClaimsDocumentsList_DTO_HUM>();
            lstClaimsStatementsResp1.add(docList);
            oClaimDetails.sPolMemID = oPolMem.Id;
            oClaimDetails.processPrvdrClaimStatementsResponse(); 
            Test.stopTest(); 
         }
      } 
      /*
     * Method Name : testPaymentService1
     * Description : Used to validate Nonzero payment check number.
     * Return type : void
     * Parameter   : NA
     */
     static testMethod void testPaymentServiceHed()
     {    
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
         system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
         System.runAs (oCCSUser) 
         { 
             Test.StartTest();
             Account oAccount = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account'];  
             System.assertEquals(oAccount.Tenant_Id__c, '00');
             
             String sAccRecType = oAccount.RecordType.Name;
             System.assertEquals(sAccRecType,'Member');
             
             Policy_Member__c oPolMem =  [Select Id,Subscriber__c ,Relationship_Code__c,Relationship__c,Policy__r.Group_Name__r.Home_Office_Account__c, Member__c, Policy__c, Policy_Member_External_ID__c , Policy__r.Product__r.ProductCode from Policy_Member__c where Member__c =: oAccount.Id];
             System.assertEquals(oPolMem.Relationship__c, 'Self');
             
             Member_ID__c newid = [select Member_Dependent_Code__c, Policy_Member__r.Effective_Date__c, Policy_Member__r.End_Date__c, Policy_Member__c, Name from Member_ID__c where name='12345'];
             System.assertEquals(newid.Name,'12345');
             
             HUM_Webservice_Callout__c oSvcHeaderPayment = [Select Request_Type__c,End_Point_URL__c,Certificate_Name__c,Timeout__c,Content_Type__c From HUM_Webservice_Callout__c where name='ClaimsService_GetClaimsDetail_V3' Limit 1];
             
             Test.setCurrentPage(Page.ClaimDetails_VF_HUM_V3);
             ApexPages.currentPage().getParameters().put('Id',oPolMem.Id);
             System.assertEquals(ApexPages.currentPage().getParameters().get('Id'),oPolMem.Id);
             ApexPages.currentPage().getParameters().put('Name',oAccount.Name);
             System.assertEquals(ApexPages.currentPage().getParameters().get('Name'),oAccount.Name);
             ApexPages.currentPage().getParameters().put('ClaimType', 'Medical');
             ApexPages.currentPage().getParameters().put('ClaimNbr', '67890');
             ApexPages.currentPage().getParameters().put('ClaimGenKey', '12345');
             ApexPages.currentPage().getParameters().put('MemberId', newid.Name);
             ApexPages.currentPage().getHeaders().put('USER-AGENT','Chrome/');
             ApexPages.currentPage().getParameters().put('PlatformCd','EM');
             ClaimDetails_C_HUM_V3 oClaimDetails=new ClaimDetails_C_HUM_V3();
             oClaimDetails.bPaymentZeroCheckNum = true;
             oClaimDetails.bPaymentNonZeroCheckNum = true;
             oClaimDetails.sAdjInd = 'Y';
             oClaimDetails.sPlatformCode = 'EM';
             oClaimDetails.sPaymentCheckNumber = '0012500';
             
             Object objVal = oClaimDetails.claimDetailsRequest();
             System.assertNotEquals(Null,objVal);
             Continuation objCon = new Continuation(120);
             objCon.continuationMethod='processResponse';
             objVal = oClaimDetails.processResponse();
             System.assertEquals(Null,objVal);
             oClaimDetails.processPaymentsResponse();
             Object objVal1 = oClaimDetails.initiatePaymentRequest();
             oClaimDetails.sRequest =null;
             oClaimDetails.processResponse();
             ClaimsDocumentsList_DTO_HUM docList = new ClaimsDocumentsList_DTO_HUM();
             ClaimMobiusDetails_S_HUM oPrvdrStatements = new ClaimMobiusDetails_S_HUM();
             docList.sDocumentKey = 'sdsa';
             docList.sPersonId = 'daf';
             docList.sTopicName = 'Test';
             HttpResponse oResponse = new HttpResponse();
             string sjson = '{"getDocumentListResponse": {"getDocumentListReturn": {"DocumentSearch": []}}}';
             oResponse.setHeader('Content-Type', 'application/json');
             oResponse.setBody(sjson);
             Test.setContinuationResponse(oClaimDetails.sPrvdrClaimStatementsRequest, oResponse);
             List<ClaimsDocumentsList_DTO_HUM> lstClaimsStatementsResp1 = new List<ClaimsDocumentsList_DTO_HUM>();
             lstClaimsStatementsResp1.add(docList);
             oClaimDetails.sPolMemID = oPolMem.Id;
             oClaimDetails.processPrvdrClaimStatementsResponse(); 
             Test.stopTest(); 
          }
       } 
      /*
     * Method Name : testPaymentServiceZeroChkNum
     * Description : Used to validate payment service with zero check number.
     * Return type : void
     * Parameter   : NA
     */
   static testMethod void testPaymentServiceZeroChkNum()
    {    
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        System.runAs (oCCSUser) 
        { 
            Test.StartTest();
            Account oAccount = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account'];  
            System.assertEquals(oAccount.Tenant_Id__c, '00');
            
            String sAccRecType = oAccount.RecordType.Name;
            System.assertEquals(sAccRecType,'Member');
            
            Policy_Member__c oPolMem =  [Select Id,Subscriber__c ,Relationship_Code__c,Relationship__c,Policy__r.Group_Name__r.Home_Office_Account__c, Member__c, Policy__c, Policy_Member_External_ID__c , Policy__r.Product__r.ProductCode from Policy_Member__c where Member__c =: oAccount.Id];
            System.assertEquals(oPolMem.Relationship__c, 'Self');
            
            Member_ID__c newid = [select Member_Dependent_Code__c, Policy_Member__r.Effective_Date__c, Policy_Member__r.End_Date__c, Policy_Member__c, Name from Member_ID__c where name='12345'];
            System.assertEquals(newid.Name,'12345');
            
            HUM_Webservice_Callout__c oSvcHeaderPayment = [Select Request_Type__c,End_Point_URL__c,Certificate_Name__c,Timeout__c,Content_Type__c From HUM_Webservice_Callout__c where name='ClaimsService_GetClaimsDetail_V3' Limit 1];
            
            Test.setCurrentPage(Page.ClaimDetails_VF_HUM_V3);
            ApexPages.currentPage().getParameters().put('Id',oPolMem.Id);
            System.assertEquals(ApexPages.currentPage().getParameters().get('Id'),oPolMem.Id);
            
            ApexPages.currentPage().getParameters().put('Name',oAccount.Name);
            System.assertEquals(ApexPages.currentPage().getParameters().get('Name'),oAccount.Name);
            ApexPages.currentPage().getParameters().put('ClaimType', 'Medical');
            ApexPages.currentPage().getParameters().put('ClaimNbr', '67890');
            ApexPages.currentPage().getParameters().put('ClaimGenKey', '12345');
            ApexPages.currentPage().getParameters().put('MemberId', newid.Name);
            ClaimDetails_C_HUM_V3 oClaimDetails=new ClaimDetails_C_HUM_V3();
            oClaimDetails.bPaymentZeroCheckNum = true;
            oClaimDetails.bPaymentNonZeroCheckNum = true;
            oClaimDetails.sAdjInd = 'Y';
            oClaimDetails.sPlatformCode = 'EM';
            oClaimDetails.sPaymentCheckNumber = '000';
            oClaimDetails.sPaymentLastProcessDate = '01/05/2016(12)';
            Object objVal = oClaimDetails.claimDetailsRequest();
            System.assertNotEquals(Null,objVal);
            Continuation objCon = new Continuation(120);
            objCon.continuationMethod='processResponse';
            objVal = oClaimDetails.processResponse();
            System.assertEquals(Null,objVal);
            oClaimDetails.processPaymentsResponse();
            Object objVal1 = oClaimDetails.initiatePaymentRequest();
            oClaimDetails.sRequest =null;
            oClaimDetails.processResponse();
            
            Test.stopTest(); 
         }
      }
    /*
     * Method Name : testprocessPrvdrClaimStatementsResponse
     * Description : Used to validate payment service and applauncher custom setting for batch 5.
     * Return type : void
     * Parameter   : NA
     */
    static testMethod void testprocessPrvdrClaimStatementsResponse()
    {    
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        System.runAs (oCCSUser) 
        { 
            ClaimDetails_C_HUM_V3 oClaimDetails=new ClaimDetails_C_HUM_V3();
            ClaimsDocumentsList_DTO_HUM docList = new ClaimsDocumentsList_DTO_HUM();
            ClaimMobiusDetails_S_HUM oPrvdrStatements = new ClaimMobiusDetails_S_HUM();
            docList.sDocumentKey = 'sdsa';
            docList.sPersonId = 'daf';
            docList.sTopicName = 'Test';
            HttpResponse oResponse = new HttpResponse();
            string sjson = '{ "ClaimDetailReponse":{ "ClaimDetailResult":{ "ClaimNbr":"201501126071443", "PatRecordNbr":"439370P105748-P1", "DiagCode":{ "Code":[ "V202", "V061", "V0481", "V040" ] }, "DiagIndicator":"9", "ClmMediaCd":"OTHER", "SrcAuthKey":"", "SrcLvCASPrefix":"", "DeficiencyInd":"Y", "DeficiencyField":"SUBSCRIBNM", "DeficiencyDate":"01/12/2015", "DeficiencyDesc":"INVALID SUBSCRIBER NAME SUBMITTED.", "ChargeAmt":"490.00", "BenAllowAmt":"355.84", "ProvWriteOff":"134.16", "BenDenyAmt":"80.00", "MbrRespAmt":"0.00", "PaidAmt":"275.84", "SRCNPIID":"1235130592", "TotalInterest":"0.00", "ExcludeAmt":"367.16", "CobPaidAmt":"0.00", "ClaimDetailLines":{ "ClaimDetailLine":[ { "LineNbr":"1", "ServiceStartDate":"01/05/2015", "ServiceEndDate":"01/05/2015", "CheckNbr":"", "ParInd":"Y", "FeeSchdId":"0000", "ProviderCISContractId":"", "ChargeAmt":"153.00", ' +
               ' "BenAllowAmt":"0.00", "ProvWriteOff":"134.16", "BenDenyAmt":"0.00", "DeductAmt":"0.00", "CopayAmt":"0.00", "CoInsAmt":"0.00", "MbrRespAmt":"0.00", "PaidAmt":"0.00", "ExcludeAmt":"153.00", "BenPayPctCd":"", "CobPaidAmt":"0.00", "LastProcessDate":"01/12/2015", "PaymentCodes":{ "PaymentCode":[ { "PmtCategory":"F1", "PmtCode":"65", "PmtEntity":"", "HIPAAMessage":"F1-Finalized/Payment.65-CLAIM/LINE HAS BEEN PAID.", "LegacyCd":"", "LegacyMessage":"" } ] }, "RemitNumber":"", "PayeeCd":"Group", "SrcClaimLineSequence":"0", "VoucherCd":"", "SrcPOTCode":"11", "POTLongDesc":"OFFICE VISIT", "DiagCode":{ "Code":[ "V202", "V061", "V0481", "V040", "V202", "V061", "V0481", "V040", "V202", "V061", "V0481", "V040", "V202", "V061", "V0481", "V040", "V202", "V061", "V0481", "V040", "V202", "V061", "V0481", "V040", "V202", "V061", "V0481", "V040", "V202", ' +
               ' "V061", "V0481", "V040", "V202", "V061", "V0481", "V040", "V202", "V061", "V0481", "V040", "V202", "V061", "V0481", "V040" ] }, "PaidStatDesc":"PAID", "ServiceCode":"99382", "ServiceDesc":"INITIAL PREVENTIVE MEDICINE NE", "Revenuecode":"", "CPTModCode":"25", "AnesMinutes":"0.0", "ServUnitCnt":"1.0", "CauseCd":"", "ToothCode":"", "ToothSurface":"" }, { "LineNbr":"2", "ServiceStartDate":"01/05/2015", "ServiceEndDate":"01/05/2015", "CheckNbr":"0000000000", "ParInd":"Y", "FeeSchdId":"0000", "ProviderCISContractId":"", "ChargeAmt":"25.00", "BenAllowAmt":"12.30", "ProvWriteOff":"134.16", "BenDenyAmt":"0.00", "DeductAmt":"0.00", "CopayAmt":"0.00", "CoInsAmt":"0.00", "MbrRespAmt":"0.00", "PaidAmt":"12.30", "ExcludeAmt":"12.70", "BenPayPctCd":"", "CobPaidAmt":"0.00", "LastProcessDate":"01/17/2015", "PaymentCodes":{ "PaymentCode":[ { "PmtCategory":"F1", ' +
               ' "PmtCode":"65", "PmtEntity":"", "HIPAAMessage":"F1-Finalized/Payment.65-CLAIM/LINE HAS BEEN PAID.", "LegacyCd":"6H02", "LegacyMessage":"This provider is a member of the Humana Network. Services are discounted according to the negotiated rate." } ] }, ' +
               ' "RemitNumber":"201501210007980", "PayeeCd":"Group", "SrcClaimLineSequence":"0", "VoucherCd":"", "SrcPOTCode":"11", "POTLongDesc":"OFFICE VISIT", "DiagCode":{ "Code":[ "V202", "V061", "V0481", "V040", "V202", "V061", "V0481", "V040", "V202", "V061", "V0481", "V040", "V202", "V061", "V0481", "V040", "V202", "V061", "V0481", "V040", "V202", "V061", "V0481", "V040", "V202", "V061", "V0481", "V040", "V202", "V061", "V0481", "V040", "V202", "V061", "V0481", "V040", "V202", "V061", "V0481", "V040", "V202", "V061", "V0481", "V040" ] }, "PaidStatDesc":"PAID", "ServiceCode":"92551", "ServiceDesc":"SCREENING TEST PURE TONE AIR O", "Revenuecode":"", "CPTModCode":"", "AnesMinutes":"0.0", "ServUnitCnt":"1.0", "CauseCd":"", "ToothCode":"", "ToothSurface":"" }] } } } }';
          
            oResponse.setHeader('Content-Type', 'application/json');
            oResponse.setBody(sjson);
            Test.setContinuationResponse(oClaimDetails.sPrvdrClaimStatementsRequest, oResponse);
            List<ClaimsDocumentsList_DTO_HUM> lstClaimsStatementsResp1 = new List<ClaimsDocumentsList_DTO_HUM>();
            lstClaimsStatementsResp1.add(docList);
            Account oAccount = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account'];  
            System.assertEquals(oAccount.Tenant_Id__c, '00');
            String sAccRecType = oAccount.RecordType.Name;
            System.assertEquals(sAccRecType,'Member');
            Policy_Member__c oPolMem =  [Select Id,Subscriber__c ,Relationship_Code__c,Relationship__c,Policy__r.Group_Name__r.Home_Office_Account__c, Member__c, Policy__c, Policy_Member_External_ID__c , Policy__r.Product__r.ProductCode from Policy_Member__c where Member__c =: oAccount.Id];
            System.assertEquals(oPolMem.Relationship__c, 'Self');
            oClaimDetails.sPolMemID = oPolMem.Id;
            oClaimDetails.processPrvdrClaimStatementsResponse();
            oClaimDetails.sRemittenceIds = 'test#tes';
            oClaimDetails.initPrvdrClaimStatementsRequest();
            ApexPages.currentPage().getParameters().put('PlatformCd','LV'); 
            oClaimDetails.sPlatformCode = 'LV';
           // oClaimDetails.bPermissionSetApplauncher = true;
            oClaimDetails.setInformationMessage();
           // oClaimDetails.bPermissionSetApplauncher = false;
            oClaimDetails.setInformationMessage();
            oClaimDetails.sDepartmentData = 'test';
            oClaimDetails.sjsonPrvdrClaimStatementsdata = 'test';
           // oClaimDetails.sRelation = 'testRelation';
            oClaimDetails.sPaymentMethod = 'testRelation';
           // oClaimDetails.sCauseCodeMentorLinkURL = 'testRelation';
            oClaimDetails.bPaymentZeroCheckNum = false;
            oClaimDetails.sAdjInd = 'N';
            oClaimDetails.initiatePaymentRequest();
            oClaimDetails.sAdjInd = 'Y';
            string state ='test';
            HttpResponse response = new HttpResponse();
            oClaimDetails.processPay(response);
            oClaimDetails.sPlatformCode = 'EM';
            oClaimDetails.processPay(response);
            System.assertEquals('Y', oClaimDetails.sAdjInd );
             oClaimDetails.listErrorLogs = null;
            List<Error_log__c> lsterr = new List<Error_log__c>();
            Error_log__c err = new Error_log__c();
            err.class_name__c = 'Test';
            lsterr.add(err);
            oClaimDetails.listErrorLogs = lsterr;  
            oClaimDetails.SaveException();
            oClaimDetails.sRequest = null;
            oClaimDetails.processResponse();
        }
    }
    
     /*
    * Method Name   :  TestClaimDetailStmtsCallout() 
    * Description   :  This method is used to  test positive scenario for ClaimStatements controller
    * Return Type   :  void
    * Parameters    :  NA
    */  
    static testMethod void TestClaimDetailStmtsCallout() 
    {  
        User objUser = [SELECT Id,Name FROM User  WHERE Lastname='test User1' LIMIT 1];
        System.assertEquals('test User1',objUser.Name);
        system.runAs(objUser)
        {
            Test.startTest();
            Account oAccount = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'AccountTest'];  
            System.assertEquals(oAccount.Tenant_Id__c, '00');
            
            Policy_Member__c oPolMem =  [Select Id,Subscriber__c ,Relationship__c,Policy__r.Group_Name__r.Home_Office_Account__c, Member__c, Policy__c, Policy_Member_External_ID__c , Policy__r.Product__r.ProductCode from Policy_Member__c where Member__c =: oAccount.Id limit 1];
            System.assertEquals(oPolMem.Relationship__c, 'Self');
            List<Member_ID__c> newid = [select Member_Dependent_Code__c, Policy_Member__r.Effective_Date__c, Policy_Member__r.End_Date__c, Policy_Member__c, Name from Member_ID__c where name='H1234-03'];
            System.assertEquals(newid[0].Name,'H1234-03');
            
            Test.setCurrentPage(Page.ClaimDetails_VF_HUM_V3);
            ApexPages.currentPage().getParameters().put('Id',oPolMem.Id);
            System.assertEquals(ApexPages.currentPage().getParameters().get('Id'),oPolMem.Id);
            
            ApexPages.currentPage().getParameters().put('Name',oAccount.Name);
            System.assertEquals(ApexPages.currentPage().getParameters().get('Name'),oAccount.Name);
            ApexPages.currentPage().getParameters().put('ClaimGenKey', '12345');
            ApexPages.currentPage().getParameters().put('MemberId', newid[0].Name);
            ClaimDetails_C_HUM_V3 oClaimDetails = new ClaimDetails_C_HUM_V3();           
            oClaimDetails.lstSplitData.add('H1234');
            oClaimDetails.lstSplitData.add('03');
            List<Member_Id__c> lstMembers = [Select Id,Name from Member_Id__c where name='H1234-03'];
            Id [] fixedSearchResults= new Id[2];
            fixedSearchResults[0] = lstMembers[0].Id;
            fixedSearchResults[1] = lstMembers[1].Id;
            Test.setFixedSearchResults(fixedSearchResults);
            ID polmemId=oClaimDetails.getMemberParameters('H1234-03');
            ClaimsDocumentsList_DTO_HUM docList = new ClaimsDocumentsList_DTO_HUM();
            ClaimMobiusDetails_S_HUM oPrvdrStatements = new ClaimMobiusDetails_S_HUM();
            docList.sDocumentKey = 'sdsa';
            docList.sPersonId = 'daf';
            docList.sTopicName = 'Test';
            HttpResponse oResponse = new HttpResponse();
            string sjson = '{"getDocumentListResponse": {"getDocumentListReturn": {"DocumentSearch": [{"TopicResultList": {"TopicResult": [{"TopicKeyList":{"TopicKey":[{"topicName": "PersonId","PersonId": "1001981051","MemberGenKey": "4131413039909","statementBeginDate": "2019-10-13","statementEndDate": "2019-10-14","statementType": "XBSF",       "statementDescription": "Family Roll-up  SmartEOB"}]},"DocumentKey": "AFP VIEWDIRECTSS|AFPTEST   |20191014062823|41314130399092019101320191014X|10","PrintedDate": "2019-10-14T06:28:23"}]}}]}}}';
            oResponse.setHeader('Content-Type', 'application/json');
            oResponse.setBody(sjson);
            Test.setContinuationResponse(oClaimDetails.sPrvdrClaimStatementsRequest, oResponse);
            List<ClaimsDocumentsList_DTO_HUM> lstClaimsStatementsResp1 = new List<ClaimsDocumentsList_DTO_HUM>();
            lstClaimsStatementsResp1.add(docList);
            oClaimDetails.sPolMemID = oPolMem.Id;
            oClaimDetails.processPrvdrClaimStatementsResponse();               
            Test.stopTest();  
        }
    }

        static testMethod void TestClaimDetailforHP() 
    {
        User objUser = [SELECT Id,Name FROM User  WHERE Lastname='test User1' LIMIT 1];
        System.assertEquals('test User1',objUser.Name);
        system.runAs(objUser)
        {
            Test.startTest();
            ClaimDetails_C_HUM_V3 oClaimDet = new ClaimDetails_C_HUM_V3();
            oClaimDet.bSwitch_4200713 = true;
            oClaimDet.sPlatformCode = 'HP';
            oClaimDet.sProduct = 'MED';
            oClaimDet.setInformationMessage();
            Test.stopTest();  
        }
    }

    

}