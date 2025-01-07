/**************************************************************************** 
Apex Class Name  : ClaimDetailsService_LT_HUM 
Version          : 1.0  
Created Date     : June 06, 2022
Function         : Test class for ClaimDetailsService_LC_HUM
****************************************************************************
Modification Log:
* Developer Name          Date                    Description
*---------------------------------------------------------------------------------------------------
* Anuradha Gajbhe         10/11/2023              Original Version
***************************************************************************************************/
@isTest
public class ClaimDetailsService_LT_HUM {
    
    /*
     * Method Name : prepareTestData
     * Description : Used to create wrapper class data
     * Return type : void
     * Parameter   : NA
     */
    @testSetup static void prepareTestData() 
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','system Administrator');
        oCCSUser.Current_Queue__c = 'Cincinnati CRU';
        insert oCCSUser;
        system.assertEquals(oCCSUser.LastName,'test User1');
        
        System.runAs (oCCSUser) 
        {
            HUMCertificateURL__c hSSRedirectURL = new HUMCertificateURL__c(Name = 'HSSRedirectURL',CertificateListURL__c = 'https://hss.humana.com/ITSecurity/HSSOneClick/LaunchHSS.aspx?HIDDENTARGET=');
            insert hSSRedirectURL;
            system.assert(HUMCertificateURL__c.getValues('HSSRedirectURL') != NULL);
            HUMCertificateURL__c oCauseCodeMentorLinkURL = new HUMCertificateURL__c(Name = 'CauseCodeMentorLinkURL',CertificateListURL__c = 'https://hss.humana.com/ITSecurity/HSSOneClick/LaunchHSS.aspx?HIDDENTARGET=');
			insert oCauseCodeMentorLinkURL;
            system.assert(HUMCertificateURL__c.getValues('CauseCodeMentorLinkURL') != NULL);
            HUMCertificateURL__c oMentorRedirectURL = new HUMCertificateURL__c(Name = 'MentorRedirectURL',CertificateListURL__c = 'https://hss.humana.com/ITSecurity/HSSOneClick/LaunchHSS.aspx?HIDDENTARGET=');
			insert oMentorRedirectURL;
            system.assert(HUMCertificateURL__c.getValues('MentorRedirectURL') != NULL);
            
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
	    
	    	HUM_Webservice_Callout__c oHUMClaimSummaryService1 = new HUM_Webservice_Callout__c();
            oHUMClaimSummaryService1.name = 'ClaimsService_GetClaimsCodeEdit_V3';
            oHUMClaimSummaryService1.Active__c = true;
            oHUMClaimSummaryService1.Certificate_Name__c = 'testts';
            oHUMClaimSummaryService1.End_Point_URL__c = 'https://testst-cmservicews.hum.com/';
            oHUMClaimSummaryService1.Timeout__c = '120000';
            oHUMClaimSummaryService1.Content_Type__c = 'application/json';
            oHUMClaimSummaryService1.Request_Type__c = 'POST';
            insert oHUMClaimSummaryService1;
            system.assert(oHUMClaimSummaryService1.id != null);
            system.assert(oHUMClaimSummaryService1.name == 'ClaimsService_GetClaimsCodeEdit_V3');
            
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
            
            if(cstGCP!=null) 
            {
                string sGCPUrl = cstGCP.End_Point_URL__c;
            }
            if(cstCASWEB!=null)
            {
                string sCASWEBUrl = cstCASWEB.End_Point_URL__c;
            }
            
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
            objAcc.Mbr_Gen_Key__c = '12345';
            objAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();
            insert objAcc;
            System.assertEquals(objAcc.Tenant_Id__c,'00');
            
            Account objAcc1 = HUMTestDataHelperTest.getAccount(); 
            objAcc1.FirstName = 'Test again';
            objAcc1.LastName = 'Account1';
            objAcc1.Tenant_Id__c = '00';
            objAcc1.Enterprise_ID__c = '1005577525';
            objAcc1.General_Account__c = true;
            objAcc1.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();
            insert objAcc1;
            System.assertEquals(objAcc1.Tenant_Id__c,'00');
            
            Account objAcc2 = HUMTestDataHelperTest.getAccount(); 
            objAcc2.FirstName = 'Test memberid';
            objAcc2.LastName = 'Account2';
            objAcc2.Tenant_Id__c = '00';
            objAcc2.Enterprise_ID__c = '1005587525';
            objAcc2.General_Account__c = true;
            objAcc2.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();
            insert objAcc2;
            System.assertEquals(objAcc2.Tenant_Id__c,'00');
            
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
            insert objpol;
            system.assertEquals(objpol.Source_Cust_Cov_Key__c,'123456789');
            
            Policy__c objpol1 = HUMTestDataHelperTest.getPolicy();
            objpol1.Name='testPolicy';
            objpol1.Group_Name__c = objAcc1.Id;
            objpol1.Major_LOB__c = 'MER';
            objpol1.Source_Cust_Cov_Key__c = '123456789';
            objpol1.Product__c = oProd.Id;
            objpol1.Product__r = oProd;
            objpol1.Platform__c = 'LV';
            insert objpol1;
            system.assertEquals(objpol1.Source_Cust_Cov_Key__c,'123456789');
            
            Policy__c objpol2 = HUMTestDataHelperTest.getPolicy();
            objpol2.Name='testPolicy1';
            objpol2.Group_Name__c = objAcc2.Id;
            objpol2.Major_LOB__c = 'MER';
            objpol2.Source_Cust_Cov_Key__c = '123456789';
            objpol2.Product__c = oProd.Id;
            objpol2.Product__r = oProd;
            objpol2.Platform__c = 'LV';
            insert objpol2;
            system.assertEquals(objpol2.Source_Cust_Cov_Key__c,'123456789');
            
            MemberPlan objPolMem = new MemberPlan();
            objPolMem.Name = 'testpolmem11';
            objPolMem.SubscriberPlanId__c = objPolMem.Id;
            objPolMem.EffectiveFrom=date.parse(Date.today().addDays(-1).format());
            objPolMem.EffectiveTo=Date.parse(Date.today().addDays(1).format());
            objPolMem.Member_Plan_External_Id__c='00|ODS|6925004434817|12345678|34567|2013-07-01';
            objPolMem.MemberId = objAcc.Id;
            objPolMem.Policy__c = objpol.Id;
            objPolMem.Policy__r = objpol;
            objPolMem.Relationship_Code__c='00';
            insert objPolMem;
            system.assertEquals(objPolMem.Relationship_Code__c, '00');
            
            MemberPlan objPolMem1 = new MemberPlan();
            objPolMem1.Name = 'testpolmem111';
            objPolMem1.Relationship_Code__c = '00';
            objPolMem1.SubscriberPlanId__c = objPolMem.Id;
            objPolMem1.EffectiveFrom=Date.parse(Date.today().addDays(-1).format());
            objPolMem1.EffectiveTo=Date.parse(Date.today().addDays(1).format());
            objPolMem1.Member_Plan_External_Id__c='00|ODS|7925004434817|12345678|34567|2013-07-01';
            objPolMem1.MemberId = objAcc1.Id;
            objPolMem1.Policy__c = objpol1.Id;
            objPolMem1.Policy__r = objpol1;
            insert objPolMem1;
            system.assertEquals(objPolMem1.Name, 'testpolmem111');
            
            MemberPlan objPolMem2 = new MemberPlan();
            objPolMem2.Name = 'testpolmem114';
            objPolMem2.Relationship_Code__c = '00';
            objPolMem2.SubscriberPlanId__c = objPolMem1.Id;
            objPolMem2.EffectiveFrom=Date.parse(Date.today().addDays(-1).format());
            objPolMem2.EffectiveTo=Date.parse(Date.today().addDays(1).format());
            objPolMem2.Member_Plan_External_Id__c='00|ODS|7925004435817|12345678|34567|2013-07-01';
            objPolMem2.MemberId = objAcc2.Id;
            objPolMem2.Policy__c = objpol2.Id;
            objPolMem2.Policy__r = objpol2;
            insert objPolMem2;
            system.assertEquals(objPolMem2.Name, 'testpolmem114');
      
            List<HUMConstants__c> listOfConstantToInsert = new List<HUMConstants__c>();
            listOfConstantToInsert.add(new HUMConstants__c(Name = 'APPLAUNCHER_ENVIRONMENT', StringValue__c = 'TEST', IntegerValue__c = 0));
            insert listOfConstantToInsert; 
            
            CustomLink__c testLink = new CustomLink__c(Name='Test',Download_MentorLink__c = true,URL__c = 'https://TestURL');
            insert testLink;
        
        }
    }
    
    @isTest Static string testgetApplauncherEnvironment()
    {
        string testURL = '';
        testURL = ClaimDetailsService_LC_HUM.getApplauncherEnvironment();
        system.assertNotEquals(testURL, 'testpolmem111');
        return testURL;
    }

	@isTest Static void testassignValuesToHelper()
    {
        ClaimDetailsService_LC_HUM.assignValuesToHelper('12345678');
        system.assertNotEquals('testpolmem11', 'testpolmem111');
    }
    
    @isTest Static void testclaimDetailsRequest()
    {        
        String json2 = '{"ClaimDetailReponse": {"ClaimDetailResult": {    "ClaimNumber": "820232190011819",    "PatientRecordNumber": "10000044365",    "DiagnosisCode": {"Code":    [       "I119",       "E1140",       "F411",       "F320",       "C61"    ]},    "DiagnosisIndicator": "10",    "ClaimMediaCode": "EDI",    "ClaimProcessOptIndicator": "Y",    "ClaimProcessOptDescription": "Humana is responsible for claim adjudication/payment",    "SourceAuthKeyId": "000000000",    "SourceLvCASPrefixCode": "57",    "DeficiencyIndicator": "",    "DeficiencyField": "",    "DeficiencyDate": "",    "DeficiencyDescription": "",    "ChargeAmount": "75.56",    "BenefitAllowedAmount": "75.56",    "ProviderWriteOff": "0.00",    "BenefitDeniedAmount": "0.00",    "MemberResponsibilityAmount": "0.00",    "PaidAmount": "0.00",    "ProviderTaxId": "583856888",    "SourceNationalProviderId": "1760444947",    "ProviderSpecialties": [{"Description": "Gen & Fam Pract  [CAS]"}],    "TotalInterestAmount": "0.00",    "ExcludeAmount": "0.00",    "COBPaidAmount": "0.00",    "BankAccountCode": "",    "SourceReferralNumber": [],    "ClaimDetailLines": {"ClaimDetailLine": [   {       "LineNumber": "1",       "ServiceStartDate": "08/01/2023",       "ServiceEndDate": "08/01/2023",       "CheckNumber": "",       "ParIndicator": "Y",       "ClaimProcessOptDescription": "Humana is responsible for claim adjudication/payment",       "ClaimProcessOptIndicator": "Y",       "EncounterIndicator": "S",       "FeeScheduleId": "0005",       "ProviderCISContractId": "000127979",       "ChargeAmount": "75.56",       "BenefitAllowedAmount": "75.56",       "ProviderWriteOff": "0.00",       "BenefitDeniedAmount": "0.00",       "DeductibleAmount": "0.00",       "CopaymentAmount": "0.00",       "CoInsuranceAmount": "0.00",       "MemberResponsibilityAmount": "0.00",       "PaidAmount": "0.00",       "ExcludeAmount": "0.00",       "BenefitPayPCTCode": "000",       "COBPaidAmount": "0.00",       "LastProcessDate": "08/08/2023",       "PaymentCodes": {"PaymentDetail": [      {          "PaymentCategory": "F4",          "PaymentCode": "16",          "PaymentEntity": "",          "HIPAAMessage": "F4-Finalized/Adjuction Complete.16-CLAIM/ENCOUNTER HAS BEEN FORWARDED TO ENTITY",          "LegacyCode": "6H02<",          "LegacyMessage": "This provider is a member of the Humana Network. Services are discounted according to the negotiated rate."       }]},       "ProviderDetails": [         {"ProviderReasonCode": "6H<02","ProviderReasonMessage": "This provider is a member of the Humana Network.  Services are discounted according to the negotiated rate."}],       "ProviderReasonCodes ": ["6H02"],       "RemittanceNumber": "202308095000126",       "PayeeCode": "Encounter",       "SourceClaimLineSequence": "001",       "VoucherCode": "K",       "PlaceofTreatmentCode": "11",       "PlaceofTreatmentDescription": "",       "DiagnosisCode": {"Code":       [          "I119",          "E1140",          "F411",          "F320",          "C61"       ]},       "PaidStatus": "PREPAID",       "ServiceCode": "99213*",       "ServiceDescription": "PHYSICIAN VISIT",       "Revenuecode": "",       "CPTModifierCode": "",       "AnesthesiaMinuteNumber": "0",       "ServiceUnitCount": "1",       "CauseOfIllnessCode": "ILL",       "ToothCode": "",       "ToothSurfaceCode": ""    }]},    "PaymentMethod": "NON" }}}';
        Continuation con1 = (Continuation) ClaimDetailsService_LC_HUM.claimDetailsRequest('12345678');
        con1.continuationMethod='processResponse';
        HttpResponse response11 = new HttpResponse();
        response11.setStatusCode(200);
        response11.setBody(json2);
        
        HttpResponse oResponse = New HttpResponse();
        oResponse.setHeader('Content-Type', 'application/json');
        oResponse.setHeader('SOAPAction', '');    
        oResponse.setHeader('Connection','keep-alive');
        oResponse.setBody(json2);
        oResponse.setStatusCode(200); 
        
        ClaimDetailsService_LC_HUM.responseDeserialize(oResponse);
		       
        ClaimsServiceDetailsResponse_LH_HUM tResponse = new ClaimsServiceDetailsResponse_LH_HUM();
        tResponse = (ClaimsServiceDetailsResponse_LH_HUM)JSON.deserialize(oResponse.getBody(),ClaimsServiceDetailsResponse_LH_HUM.class); 
        if(tResponse.ClaimDetailReponse != null)
        {
            ClaimDetails_LWC_DTO_HUM_V2 lstClaimlineItems = new ClaimDetails_LWC_DTO_HUM_V2();
            ClaimsServiceDetailsResponse_LH_HUM.ClaimDetailReponse oClaimDetailsResponse = tResponse.ClaimDetailReponse;
            ClaimsServiceDetailsResponse_LH_HUM.ClaimDetailResult oClaimDetailsResult = oClaimDetailsResponse.ClaimDetailResult;
            ClaimsServiceDetailsResponse_LH_HUM.ClaimDetailLines oClaimDetailLines = oClaimDetailsResult.ClaimDetailLines; 
            List<ClaimsServiceDetailsResponse_LH_HUM.ClaimDetailLine> lstClaimDetail = oClaimDetailLines.ClaimDetailLine;
            
            ClaimsServiceDetailsResponse_LH_HUM.PaymentCodes oPaymntCodes = lstClaimDetail[0].PaymentCodes;
            List<ClaimsServiceDetailsResponse_LH_HUM.PaymentDetail> lstPaymentCodes = oPaymntCodes.PaymentDetail;
            List<ClaimsServiceDetailsResponse_LH_HUM.ProviderDetails> lstProviderDetails = lstClaimDetail[0].ProviderDetails;
            ClaimDetailsService_LC_HUM.getProviderCodes(lstProviderDetails);
            ClaimDetailsService_LC_HUM.getPayCodes(lstPaymentCodes);
        }

        String json3 = ' {"GetCodeEditDetailsResponse": {"RationaleDTO": {    "ExCodes": {"ExCode": ["Z25"]},    "RejectedProcedureCodes": {"RejectedProcedureDTO": [   {       "ExCode": "Z25",       "Platform": "EM",       "ProcCode": "95226"    }]},    "PaidProcedureCodes": {"PaidProcedureCode": ["92507"]},    "PaidModifers": {"PaidModifer": []},    "DiagnosisCodes": {"DiagnosisCode": [   {       "DiagnosisCd": "493.00",       "DiagnosisCodeIndicator": "Diag9"    }]},    "RationaleDescriptionCodes": {"RationaleDescriptionDTO": [   {       "CodeEditInd": "HumanaCodeEdit",       "ExCode": "Z25",       "RationaleDesc": "Services performed should address the patients symptoms reported by the diagnosis codes. One or more of the diagnosis codes submitted are considered clinically unexpected or unlikely to support the procedure or services performed. ",       "RejectedProcedureCode": "95226"    }]},    "RationaleExceptionCodes": {"RationaleExceptionDTO": []} }}}';	
        list<string> lstDiagnosisCd = new list<string>{'I119','E1140','F411','F320','C61'};
        string paymentCode = '[{"sLegacyCd":"6H02","sLegacyMessage":"This provider is a member of the Humana Network. Services are discounted according to the negotiated rate.","sEORDescription":"test"}]';
        
        Continuation con2 = (Continuation) ClaimDetailsService_LC_HUM.getRationale(lstDiagnosisCd,paymentCode,'LV','10','DENIED' , '', '99213','820232190011819');
        HttpResponse response = new HttpResponse();
        response.setStatusCode(200);
        response.setBody(json3);
        system.assertEquals(response.getBody(), json3);
    
    }
    
    @isTest Static void testgetCauseCodeCustomSettingDetails()
    {
    	User testUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
    	system.assertNotEquals(testUser.UserName, 'test User1');
    	Account oAccount = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account'];  
        ClaimDetailsService_LC_HUM.getCauseCodeCustomSettingDetails();
    }

    @isTest Static void testgetClaimInquiryToolLink()
    {
        system.assertNotEquals('test', 'test User1');
        ClaimDetailsService_LC_HUM.getClaimInquiryToolLink();
    }
    
    @isTest Static void testPositiveData1()
    {
        ClaimDetailsService_LC_HUM.initiatePaymentRequest('Y','','820232230255133','LV');     
        Continuation conti1 = (Continuation) ClaimDetailsService_LC_HUM.initiatePaymentRequest('Y','','820232230255133','LV');     
        conti1.continuationMethod='processPay';
        conti1.state='820232230255133';
        HttpResponse oResponse1 = new HttpResponse();
        oResponse1.setHeader('Content-Type', 'application/json');
        oResponse1.setHeader('SOAPAction', '');    
        oResponse1.setHeader('Connection','keep-alive');
        string sJson5 = '{"StandardizeAddressResponse": {    "SuccessFlag": "Y",    "AddressLine1": "8900 SHELBYVILLE RD APT 5",    "City": "LOUISVILLE",    "StateCode": "KY",    "ZipCode": "40222",    "StateID": "21",    "ZipCodePlus": "8596",    "CountyName": "JEFFERSON",    "CountyID": "111",    "Valid": "true",    "ChangeIndicator": "Change" }}}}';
        oResponse1.setBody(sJson5);
        oResponse1.setStatusCode(200);
        List<string> newStr1 = new List<string> ();
        newstr1.add(sJson5);
        ClaimDetailsService_LC_HUM.processPay(newStr1,'820232230255133');
        
        string json4 = '{"GetCodeEditDetailsResponse": {"RationaleDTO": {    "ExCodes": {"ExCode": ["Z25"]},    "RejectedProcedureCodes": {"RejectedProcedureDTO": [   {       "ExCode": "Z25",       "Platform": "EM",       "ProcCode": "95226"    }]},    "PaidProcedureCodes": {"PaidProcedureCode": ["92507"]},    "PaidModifers": {"PaidModifer": []},    "DiagnosisCodes": {"DiagnosisCode": [   {       "DiagnosisCd": "493.00",       "DiagnosisCodeIndicator": "Diag9"    }]},    "RationaleDescriptionCodes": {"RationaleDescriptionDTO": [   {       "CodeEditInd": "HumanaCodeEdit",       "ExCode": "Z25",       "RationaleDesc": "Services performed should address the patients symptoms reported by the diagnosis codes. One or more of the diagnosis codes submitted are considered clinically unexpected or unlikely to support the procedure or services performed. ",       "RejectedProcedureCode": "95226"    }]},    "RationaleExceptionCodes": {"RationaleExceptionDTO": []} }}}';
        HttpResponse oResponse = New HttpResponse();
        oResponse.setHeader('Content-Type', 'application/json');
        oResponse.setHeader('SOAPAction', '');    
        oResponse.setHeader('Connection','keep-alive');
        oResponse.setBody(json4);
        oResponse.setStatusCode(200); 
        ClaimsGetCodeEditDetails_LDTO_HUM oCodeEditState = new ClaimsGetCodeEditDetails_LDTO_HUM();
        oCodeEditState.sClaimNbr = '820213490132420';
        ClaimDetailsService_LC_HUM.responseDeserializeCodeEdit(oResponse,oCodeEditState);
               
        HttpRequest oRequest = new HttpRequest();
        HUM_Webservice_Callout__c oServiceCalloutClaimsCodeEdit = HUM_Webservice_Callout__c.getValues('ClaimsService_GetClaimsCodeEdit_V3');  
        List<string> mystr = new List<string>();
        ClaimDetailsService_LC_HUM.processPay(mystr,'State');
        ClaimDetailsService_LC_HUM.processResponseCodeEdit(mystr,'State');
		ClaimDetailsService_LC_HUM.setHttpRequestCodeEdit(oRequest,oServiceCalloutClaimsCodeEdit,'');
        ClaimDetailsService_LC_HUM.processResponse(mystr, 'State');
        String sJSONPay = '{"GetEhub835XmlResponse": {"GetEhub835XmlResult": {"Transaction_835": [{    "CheckNumber": "231670215001881",    "PayeeName": "DFD HEALTH SERVICES INC",    "PayeeAddress": "2742 SW 8TH ST,MIAMI,FL,331354636",    "CheckDate": "06/15/2023",    "PaymentType": "ACH",    "PaymentAmount": "361.00",    "ClaimDetails": {"ClaimDetail":    [             {          "ClaimNumber": "820231560000514",          "ClaimPaymentAmount": "76.00"       },             {          "ClaimNumber": "820231560000511",          "ClaimPaymentAmount": "114.00"       },             {          "ClaimNumber": "820231560000510",          "ClaimPaymentAmount": "171.00"       }    ]},    "PaymentTypeID": "031100209",    "PayeeID": "823198",    "PaymentIssueDate": "06/15/2023",    "PaymentStatus": "",    "PaymentStatusDate": "6/15/2023" }]}}}';
        system.assertNotEquals('test', 'test User1');
        HUMCertificateURL__c objHssURL = HUMCertificateURL__c.getValues('HSSRedirectURL');
    }
    
    
    
    @isTest Static void testgetAccountDetails()
    {
        User testUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
    	MemberPlan oMemPlan = [Select Id,Name from MemberPlan where Name = 'testpolmem11']; 
        system.assertEquals(oMemPlan.Name, 'testpolmem11'); 
        System.runAs(testUser)
        {
            MemberPlan objTestMem = new MemberPlan();
            objTestMem = ClaimDetailsService_LC_HUM.getAccountDetails(oMemPlan.Id);
        } 
    }
    
    @isTest Static void testgetCASApplauncherData()
    {
        User testUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        MemberPlan oMemPlan = [Select Id,Name from MemberPlan where Name = 'testpolmem11']; 
        system.assertEquals(oMemPlan.Name, 'testpolmem11'); 
        ClaimDetailsService_LC_HUM.getCASApplauncherData(oMemPlan.Id);   
    }
    
        
    @isTest Static void testExceptionData()
    {
        ClaimDetailsService_LC_HUM.claimDetailsRequest('');
        ClaimDetailsService_LC_HUM.responseDeserialize(null);
        
        String jsonEx = '{"ClaimDetailReponse": {"ClaimDetailResult": {     "PatientRecordNumber": "10000044365",    "DiagnosisCode": {"Code":    [       "I119",       "E1140",       "F411",       "F320",       "C61"    ]},    "DiagnosisIndicator": "10",    "ClaimMediaCode": "EDI",    "ClaimProcessOptIndicator": "Y",    "ClaimProcessOptDescription": "Humana is responsible for claim adjudication/payment",    "SourceAuthKeyId": "00",    "SourceLvCASPrefixCode": "57",    "DeficiencyIndicator": "",    "DeficiencyField": "",    "DeficiencyDate": "",    "DeficiencyDescription": "",    "ChargeAmount": "75.56",    "BenefitAllowedAmount": "75.56",    "ProviderWriteOff": "0.00",    "BenefitDeniedAmount": "0.00",    "MemberResponsibilityAmount": "0.00",    "PaidAmount": "0.00",    "ProviderTaxId": "583856888",    "SourceNationalProviderId": "1760444947",    "ProviderSpecialties": [{"Description": "Gen & Fam Pract  [CAS]"}],    "TotalInterestAmount": "0.00",    "ExcludeAmount": "0.00",    "COBPaidAmount": "0.00",    "BankAccountCode": "",    "SourceReferralNumber": [],    "ClaimDetailLines": {"ClaimDetailLine": [   {       "LineNumber": "1",       "ServiceStartDate": "08/01/2023",       "ServiceEndDate": "08/01/2023",       "CheckNumber": "",       "ParIndicator": "Y",       "ClaimProcessOptDescription": "Humana is responsible for claim adjudication/payment",       "ClaimProcessOptIndicator": "Y",       "EncounterIndicator": "S",       "FeeScheduleId": "0005",       "ProviderCISContractId": "000127979",       "ChargeAmount": "75.56",       "BenefitAllowedAmount": "75.56",       "ProviderWriteOff": "0.00",       "BenefitDeniedAmount": "0.00",       "DeductibleAmount": "0.00",       "CopaymentAmount": "0.00",       "CoInsuranceAmount": "0.00",       "MemberResponsibilityAmount": "0.00",       "PaidAmount": "0.00",       "ExcludeAmount": "0.00",       "BenefitPayPCTCode": "000",       "COBPaidAmount": "0.00",       "LastProcessDate": "08/08/2023",       "PaymentCodes": {"PaymentDetail": [      {          "PaymentCategory": "F4",          "PaymentCode": "16",          "PaymentEntity": "",          "HIPAAMessage": "F4-Finalized/Adjuction Complete.16-CLAIM/ENCOUNTER HAS BEEN FORWARDED TO ENTITY",          "LegacyCode": "6H02",          "LegacyMessage": "This provider is a member of the Humana Network. Services are discounted according to the negotiated rate."       }]},       "ProviderDetails": [         {"ProviderReasonCode": "6H02","ProviderReasonMessage": "This provider is a member of the Humana Network.  Services are discounted according to the negotiated rate."}],       "ProviderReasonCodes ": ["6H02"],       "RemittanceNumber": "202308095000126",       "PayeeCode": "Encounter",       "SourceClaimLineSequence": "001",       "VoucherCode": "K",       "PlaceofTreatmentCode": "11",       "PlaceofTreatmentDescription": "",       "DiagnosisCode": {"Code":       [          "I119",          "E1140",          "F411",          "F320",          "C61"       ]},       "PaidStatus": "PREPAID",       "ServiceCode": "99213*",       "ServiceDescription": "PHYSICIAN VISIT",       "Revenuecode": "",       "CPTModifierCode": "",       "AnesthesiaMinuteNumber": "0",       "ServiceUnitCount": "1",       "CauseOfIllnessCode": "ILL",       "ToothCode": "",       "ToothSurfaceCode": ""    }]},    "PaymentMethod": "NON" }}}';
        HttpResponse oResponse1 = New HttpResponse();
        oResponse1.setHeader('Content-Type', 'application/json');
        oResponse1.setHeader('SOAPAction', '');    
        oResponse1.setHeader('Connection','keep-alive');
        oResponse1.setBody(jsonEx);
        oResponse1.setStatusCode(200); 
        ClaimDetailsService_LC_HUM.responseDeserialize(oResponse1);
		list<string> lstDiagnosisCd1 = new list<string>{'I119','E1140','F411','F320','C61'};
        string paymentCode1 = '[{"sLegacyCd":"6H0","sLegacyMessage":"This provider is a member of the Humana Network. Services are discounted according to the negotiated rate.","sEORDescription":"test"}]';
		ClaimDetailsService_LC_HUM.getRationale(lstDiagnosisCd1,paymentCode1,'LV','10','PAID' , '', '99213','820232190011819');
		
        List<ClaimDetails_LWC_DTO_HUM_V2.PaymentCode> lstPaymentCodes= (List<ClaimDetails_LWC_DTO_HUM_V2.PaymentCode>) JSON.deserialize(paymentCode1, List<ClaimDetails_LWC_DTO_HUM_V2.PaymentCode>.class);
        ClaimDetailsService_LC_HUM.fetchExcodeLineItem(null);
        
        ClaimsGetCodeEditDetails_LDTO_HUM oCodeEditState1 = new ClaimsGetCodeEditDetails_LDTO_HUM();
        oCodeEditState1.sClaimNbr = '820213490132420';
        System.assertEquals(oCodeEditState1.sClaimNbr,'820213490132420');
        ClaimDetailsService_LC_HUM.responseDeserializeCodeEdit(null,oCodeEditState1);
        
        CustomLink__C mylink = [select id,Download_MentorLink__c,URL__c from CustomLink__c where Name = 'Test' LIMIT 1 ] ;
        ClaimDetailsService_LC_HUM.redirectMentorDocument(mylink.id,'','Test');
        ClaimDetailsService_LC_HUM.redirectMentorDocument('Test','','Test');
    }
    
    
    @isTest Static void testLSMethod()
    {
        list<string> lstDiagnosisCd2 = new list<string>();
        lstDiagnosisCd2.add('I119');
        lstDiagnosisCd2.add('E1140');
        lstDiagnosisCd2.add('F411');
        lstDiagnosisCd2.add('F320');
        lstDiagnosisCd2.add('C61');   
                
		string paymentCode2 = '[{"sEORDescription":"This charge was applied to the plan maximum out-of-pocket.","sLegacyCd":"77M4","sLegacyMessage":"This charge was applied to the plan maximum out-of-pocket."},{"sEORDescription":"This provider is a member of the Humana Network.  Services are discounted according to the negotiated rate.","sLegacyCd":"6H01","sLegacyMessage":"This provider is a member of the Humana Network. Services are discounted according to the negotiated rate."}]';	
        List<ClaimDetails_LWC_DTO_HUM_V2.PaymentCode> lstPaymentCodes = (List<ClaimDetails_LWC_DTO_HUM_V2.PaymentCode>) JSON.deserialize(paymentCode2, List<ClaimDetails_LWC_DTO_HUM_V2.PaymentCode>.class);
        
        ClaimDetailsService_LS_HUM claimDetailsServiceLS = new ClaimDetailsService_LS_HUM ();
        ClaimCodeEditDetailRequest_LH_HUM res = new ClaimCodeEditDetailRequest_LH_HUM();
        
        string paymentCode1 = '[{"sLegacyCd":"6H0","sLegacyMessage":"This provider is a member of the Humana Network. Services are discounted according to the negotiated rate.","sEORDescription":"test"}]';
        List<ClaimLineItems_LDTO_HUM.PaymentCode> lstPaymentCodes1= (List<ClaimLineItems_LDTO_HUM.PaymentCode>) JSON.deserialize(paymentCode1, List<ClaimLineItems_LDTO_HUM.PaymentCode>.class);
        
        claimDetailsServiceLS.codeEditRationale(lstDiagnosisCd2,lstPaymentCodes1,'LV','10','PAID','','99213');
        
        claimDetailsServiceLS.processResponse(null);
        claimDetailsServiceLS.codeEditPaidProcedureCd('','');
        claimDetailsServiceLS.codeEditPaidModifier('');
        claimDetailsServiceLS.codeEditDiagnosisCodes(lstDiagnosisCd2,'10');
        
        HttpRequest oRequest = new HttpRequest();
        String sRequestBody = '';
        HUM_Webservice_Callout__c oServiceCalloutClaimsCodeEdit = HUM_Webservice_Callout__c.getValues('ClaimsService_GetClaimsCodeEdit_V3');  
        claimDetailsServiceLS.setHttpRequestCodeEdit(oRequest,oServiceCalloutClaimsCodeEdit,sRequestBody);
        ClaimsServiceCodeEditDtlResponse_H_HUM.GetCodeEditDetailsResponse oClaimsCodeEditResponse = new ClaimsServiceCodeEditDtlResponse_H_HUM.GetCodeEditDetailsResponse ();
        
        string oClaimsCodeEditResponse1 = '{"GetCodeEditDetailsResponse": {"RationaleDTO": {    "ExCodes": {"ExCode": ["02J"]},    "RejectedProcedureCodes": {"RejectedProcedureDTO": [   {       "ExCode": "02J",       "Platform": "EM",       "ProcCode": "A4550"    }]},    "PaidProcedureCodes": {"PaidProcedureCode": ["A4550"]},    "PaidModifers": {"PaidModifer": []},    "DiagnosisCodes": {"DiagnosisCode": [   {       "DiagnosisCd": "2392",       "DiagnosisCodeIndicator": "Diag9"    }]},    "RationaleDescriptionCodes": {"RationaleDescriptionDTO": [   {       "CodeEditInd": "iHealthCodeEdit",       "ExCode": "02J",       "RationaleDesc": "",       "RejectedProcedureCode": ""    }]},    "RationaleExceptionCodes": {"RationaleExceptionDTO": []}}}}';
        ClaimsServiceCodeEditDtlResponse_H_HUM oResponseCodeEdit = (ClaimsServiceCodeEditDtlResponse_H_HUM )JSON.deserialize(oClaimsCodeEditResponse1,ClaimsServiceCodeEditDtlResponse_H_HUM.class); 
        
        oClaimsCodeEditResponse = oResponseCodeEdit.GetCodeEditDetailsResponse;
        claimDetailsServiceLS.codeEditDetailsResponse(oClaimsCodeEditResponse,'820213490132420');
        string oClaimsPaymentResponse1 = '{"GetEhub835XmlResponse": {"GetEhub835XmlResult": {"Transaction_835": [{    "CheckNumber": "1002433070",    "PayeeName": "ANESTHESIOLOGY CONSULTANTS",    "PayeeAddress": "PO BOX 934959,ATLANTA,GA,311934959",    "CheckDate": "04/28/2013",    "PaymentType": "CHK",    "PaymentAmount": "630",    "ClaimDetails": {"ClaimDetail":    [             {          "ClaimNumber": "201304206682568",          "ClaimPaymentAmount": "157.5"       },             {          "ClaimNumber": "201304206718250",          "ClaimPaymentAmount": "157.5"       },             {          "ClaimNumber": "201304206437730",          "ClaimPaymentAmount": "157.5"       },             {          "ClaimNumber": "201304206600678",          "ClaimPaymentAmount": "157.5"       }    ]},    "PaymentTypeID": "",    "PayeeID": "224673-3520913310000",    "PaymentIssueDate": "04/28/2013",    "PaymentStatus": "",    "PaymentStatusDate": "" }]}}} ';
        claimDetailsServiceLS.processResponse(oClaimsPaymentResponse1);
        
        String json8 = '{"ClaimDetailReponse": {"ClaimDetailResult": {    "ClaimNumber": "820232190011819",    "PatientRecordNumber": "10000044365",    "DiagnosisCode": {"Code":    [       "I119",       "E1140",       "F411",       "F320",       "C61"    ]},    "DiagnosisIndicator": "10",    "ClaimMediaCode": "EDI",    "ClaimProcessOptIndicator": "Y",    "ClaimProcessOptDescription": "Humana is responsible for claim adjudication/payment",    "SourceAuthKeyId": "000000000",    "SourceLvCASPrefixCode": "57",    "DeficiencyIndicator": "",    "DeficiencyField": "",    "DeficiencyDate": "",    "DeficiencyDescription": "",    "ChargeAmount": "75.56",    "BenefitAllowedAmount": "75.56",    "ProviderWriteOff": "0.00",    "BenefitDeniedAmount": "0.00",    "MemberResponsibilityAmount": "0.00",    "PaidAmount": "0.00",    "ProviderTaxId": "583856888",    "SourceNationalProviderId": "1760444947",    "ProviderSpecialties": [{"Description": "Gen & Fam Pract  [CAS]"}],    "TotalInterestAmount": "0.00",    "ExcludeAmount": "0.00",    "COBPaidAmount": "0.00",    "BankAccountCode": "",    "SourceReferralNumber": [],    "ClaimDetailLines": {"ClaimDetailLine": [   {       "LineNumber": "1",       "ServiceStartDate": "08/01/2023",       "ServiceEndDate": "08/01/2023",       "CheckNumber": "",       "ParIndicator": "Y",       "ClaimProcessOptDescription": "Humana is responsible for claim adjudication/payment",       "ClaimProcessOptIndicator": "Y",       "EncounterIndicator": "S",       "FeeScheduleId": "0005",       "ProviderCISContractId": "000127979",       "ChargeAmount": "75.56",       "BenefitAllowedAmount": "75.56",       "ProviderWriteOff": "0.00",       "BenefitDeniedAmount": "0.00",       "DeductibleAmount": "0.00",       "CopaymentAmount": "0.00",       "CoInsuranceAmount": "0.00",       "MemberResponsibilityAmount": "0.00",       "PaidAmount": "0.00",       "ExcludeAmount": "0.00",       "BenefitPayPCTCode": "000",       "COBPaidAmount": "0.00",       "LastProcessDate": "08/08/2023",       "PaymentCodes": {"PaymentDetail": [      {          "PaymentCategory": "F4",          "PaymentCode": "16",          "PaymentEntity": "",          "HIPAAMessage": "F4-Finalized/Adjuction Complete.16-CLAIM/ENCOUNTER HAS BEEN FORWARDED TO ENTITY",          "LegacyCode": "6H02<",          "LegacyMessage": "This provider is a member of the Humana Network. Services are discounted according to the negotiated rate."       }]},       "ProviderDetails": [         {"ProviderReasonCode": "6H<02","ProviderReasonMessage": "This provider is a member of the Humana Network.  Services are discounted according to the negotiated rate."}],       "ProviderReasonCodes ": ["6H02"],       "RemittanceNumber": "202308095000126",       "PayeeCode": "Encounter",       "SourceClaimLineSequence": "001",       "VoucherCode": "K",       "PlaceofTreatmentCode": "11",       "PlaceofTreatmentDescription": "",       "DiagnosisCode": {"Code":       [          "I119",          "E1140",          "F411",          "F320",          "C61"       ]},       "PaidStatus": "PREPAID",       "ServiceCode": "99213*",       "ServiceDescription": "PHYSICIAN VISIT",       "Revenuecode": "",       "CPTModifierCode": "",       "AnesthesiaMinuteNumber": "0",       "ServiceUnitCount": "1",       "CauseOfIllnessCode": "ILL",       "ToothCode": "",       "ToothSurfaceCode": ""    }]},    "PaymentMethod": "NON" }}}';
		HttpResponse sResponse = New HttpResponse();
		sResponse.setHeader('Content-Type', 'application/json');
		sResponse.setHeader('SOAPAction', '');    
		sResponse.setHeader('Connection','keep-alive');
		sResponse.setBody(json8);
		sResponse.setStatusCode(200); 
        system.assertNotEquals(json8,'');
        
        ClaimsServiceDetailsResponse_LH_HUM oResponse = new ClaimsServiceDetailsResponse_LH_HUM();
        oResponse = (ClaimsServiceDetailsResponse_LH_HUM)JSON.deserialize(sResponse.getBody(),ClaimsServiceDetailsResponse_LH_HUM.class); 
        if(oResponse.ClaimDetailReponse != null)
        {
            ClaimsServiceDetailsResponse_LH_HUM.ClaimDetailReponse oClaimDetailsResponse = oResponse.ClaimDetailReponse;
            ClaimsServiceDetailsResponse_LH_HUM.ClaimDetailResult oClaimDetailsResult = oClaimDetailsResponse.ClaimDetailResult;
			claimDetailsServiceLS.claimLineItems(oClaimDetailsResult,'','','');
        } 
	}
}