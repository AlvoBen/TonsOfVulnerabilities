/******************************************************************************************
Apex Class Name  : ClaimDetailsService_T_HUM_V2
Version          : 1.0 
Created Date     : 03/07/2018
Function         : Test class for ClaimDetails_S_HUM_V2  Apex class
Modification Log :
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------  
 *  Prasanthi Kandula               212620              03/07/2018         REQ - 355817 - Incorporated changes as part of Claims V3 service change.
 *  Prasanthi Kandula                                   09/19/2019         REQ - 404535 - Added ProviderDetails to view EOR description
 *  Anjani Vaddadi                                      05/21/2020         REQ - 1031822
 *  Suraj Patil											04/08/2021		   REQ - 1279689 T1PRJ0002606 -2021 MF1- SF- Claims old version Decommission
 *  Pinky Vijur                                       02/22/2023         User Story 4200769: T1PRJ0883127- Author Shutdown- MF 2- Display and Resend Author generated tEOB�s in CRM Service (Classic)
 ******************************************************************************************************************************/
 @isTest
private class ClaimDetailsService_T_HUM_V2
{
     /*
      * Method Name : setup
      * Description : Create common test Data
      * Return type : void
      * Parameter   : None
      */ 
       @testSetup static void setup() 
      {
          
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertEquals(oCCSUser.LastName,'test User1');
        System.runAs (oCCSUser) 
        {
            List<Account> accountList = new List<Account>();   
            Account objAcc = HUMTestDataHelperTest.getAccount(); 
            objAcc.FirstName = 'Test';
            objAcc.LastName = 'Account';
            objAcc.Tenant_Id__c = '00';
            objAcc.Enterprise_ID__c = '1005577525';
            objAcc.General_Account__c = true;
            objAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();
            accountList.add(objAcc);
            System.assertEquals(objAcc.Tenant_Id__c,'00');
            System.assertEquals(objAcc.Enterprise_ID__c,'1005577525');
            System.assertEquals(objAcc.LastName,'Account');
            
            Account objAcc2 = HUMTestDataHelperTest.getAccount(); 
            objAcc2.FirstName = 'Test';
            objAcc2.LastName = 'Account2';
            objAcc2.Tenant_Id__c = '01';
            objAcc2.Enterprise_ID__c = '';
            objAcc2.General_Account__c = true;
            objAcc2.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();
            accountList.add(objAcc2);
            System.assertEquals(objAcc2.Tenant_Id__c,'01');
            System.assertEquals(objAcc2.Enterprise_ID__c,'');
            System.assertEquals(objAcc2.LastName,'Account2');
            insert accountList;
            system.assertequals(accountList.size(),2);
            
            Product2 oProd = new Product2(Name = 'Laptop X200',Segment_Type__c='MCR',ProductCode='MED', Major_LOB__c='MER');
            insert oProd;
            System.assertNotEquals(oProd.Id, NULL, 'Value not matched');
              
            List<Policy__c> policyList = new List<Policy__c>();
            Policy__c objpol = HUMTestDataHelperTest.getPolicy();
            objpol.Name='testPolicy';
            objpol.Group_Name__c = objAcc.Id;
            objpol.Major_LOB__c = 'MER';
            objpol.Source_Cust_Cov_Key__c = '123456789';
            objpol.Product__c = oProd.Id;
            objpol.Product__r = oProd;
            objpol.Platform__c = 'LV';
            policyList.add(objpol);
            system.assertEquals(objpol.Source_Cust_Cov_Key__c,'123456789');
            system.assertEquals(objpol.Group_Name__c,objAcc.Id);
            system.assertEquals(objpol.Name,'testPolicy'); 
            
            Policy__c objpol2 = HUMTestDataHelperTest.getPolicy();
            objpol2.Name='testPolicy2';
            objpol2.Group_Name__c = objAcc2.Id;
            objpol2.Major_LOB__c = 'MER';
            objpol2.Source_Cust_Cov_Key__c = '123456789';
            objpol2.Product__c = oProd.Id;
            objpol2.Product__r = oProd;
            objpol2.Platform__c = 'LV';
            policyList.add(objpol2);
            system.assertEquals(objpol2.Source_Cust_Cov_Key__c,'123456789');
            system.assertEquals(objpol2.Group_Name__c,objAcc2.Id);
            system.assertEquals(objpol2.Name,'testPolicy2');
            insert policyList;
            system.assertequals(policyList.size(),2);
            
            List<Policy_Member__c> polMemberList = new List<Policy_Member__c>();
            Policy_Member__c objPolMem = new Policy_Member__c();
            objPolMem.Name = 'Policy Member PM1';
            objPolMem.Relationship__c = 'Self';
            objPolMem.Subscriber__c = objPolMem.Id;
            objPolMem.Effective_Date__c='12/01/2015';
            objPolMem.End_Date__c='11/16/2016';
            objPolMem.Policy_Member_External_ID__c='00|ODS|6925004434817|12345678|34567|2013-07-01';
            objPolMem.Member__c = objAcc.Id;
            objPolMem.Policy__c = objpol.Id;
            objPolMem.Policy__r = objpol;
            polMemberList.add(objPolMem);
            system.assertEquals(objPolMem.Relationship__c, 'Self');
            system.assertEquals(objPolMem.Name, 'Policy Member PM1');
            system.assertEquals(objPolMem.Policy_Member_External_ID__c, '00|ODS|6925004434817|12345678|34567|2013-07-01');       
            
            Policy_Member__c objPolMem2 = new Policy_Member__c();
            objPolMem2.Name = 'testpolmem22';
            objPolMem2.Relationship__c = 'Self';
            objPolMem2.Subscriber__c = objPolMem2.Id;
            objPolMem2.Effective_Date__c=Date.today().addDays(-1).format();
            objPolMem2.End_Date__c=null;
            objPolMem2.Policy_Member_External_ID__c='00|ODS|6925004434817|12345678|34567|2013-08-01';
            objPolMem2.Member__c = objAcc2.Id;
            objPolMem2.Policy__c = objpol2.Id;
            objPolMem2.Policy__r = objpol2;
            polMemberList.add(objPolMem2);
            system.assertEquals(objPolMem2.Relationship__c, 'Self');
            system.assertEquals(objPolMem2.Name, 'testpolmem22');
            system.assertEquals(objPolMem2.Policy_Member_External_ID__c, '00|ODS|6925004434817|12345678|34567|2013-08-01');
            insert polMemberList;
            system.assertequals(polMemberList.size(),2);
              
            HUM_Webservice_Callout__c oHUMAuthDetailsService = new HUM_Webservice_Callout__c();
            oHUMAuthDetailsService.name = 'Claims_Detail';
            oHUMAuthDetailsService.Active__c = true;
            oHUMAuthDetailsService.Certificate_Name__c = 'test';
            oHUMAuthDetailsService.End_Point_URL__c = 'https://testst-crmservicews.humana.com';
            oHUMAuthDetailsService.Timeout__c = '120000';
            oHUMAuthDetailsService.Content_Type__c = 'application/json';
            oHUMAuthDetailsService.Request_Type__c = 'POST';
            insert oHUMAuthDetailsService;
            system.assert(oHUMAuthDetailsService.id != null);
            system.assert(oHUMAuthDetailsService.name == 'Claims_Detail');
          }
      }
      
    /*
     * Method Name : authDetails
     * Description : This method tests the DiagnosisCodes and  assignValuesToHelper,ClaimDetails methods in the service class
     * Return type : void
     * Parameter   : None
     */ 
    static testMethod void testClaimDetails() 
    {    
        Test.setCurrentPage(Page.ClaimDetails_VF_HUM_V3);
        Policy_Member__c oPolMember = [Select id from Policy_Member__c where Name = 'Policy Member PM1' limit 1];
        HUM_Webservice_Callout__c oServiceCallout = [select Name, Certificate_Name__c, End_Point_URL__c, Timeout__c, Content_Type__c, Request_Type__c from HUM_Webservice_Callout__c where name='Claims_Detail' limit 1];   
        string sRecId = oPolMember.id;
        String sClaimType='Medical';
        String sClaimGenKey= '1071000457108901';
        String sClaimNbr='110156323';
        HttpRequest oRequest = new HttpRequest();
        User oUser = [SELECT Id FROM User  WHERE Lastname ='test User1' LIMIT 1];
        system.runAs(oUser)
        {
            Test.startTest();
            ClaimDetails_S_HUM_V2 oClaimDetails=new ClaimDetails_S_HUM_V2();
            ClaimsServiceDetailsRequest_H_HUM_V2 oClaimDetailsReq = new ClaimsServiceDetailsRequest_H_HUM_V2();  
            oClaimDetailsReq = oClaimDetails.assignValuesToHelper(sClaimType,sClaimNbr,sClaimGenKey);
            system.assertEquals(oClaimDetailsReq.GetClaimDetail.Id,'1071000457108901');
            ClaimsServiceDetailsResponse_H_HUM_V2 oResponse = new ClaimsServiceDetailsResponse_H_HUM_V2();
            ClaimsServiceDetailsResponse_H_HUM_V2.ClaimDetailResult oResult=new ClaimsServiceDetailsResponse_H_HUM_V2.ClaimDetailResult();
            ClaimsServiceDetailsResponse_H_HUM_V2.DiagnosisCode oCode=new ClaimsServiceDetailsResponse_H_HUM_V2.DiagnosisCode();
            List<String> str=new List<String>();
            str.add('78821');
            ocode.Code=str;
            oResult.DiagnosisCode=oCode; 
            oResult.ClaimNumber = 'C4567';
            oResult.ClaimMediaCode = 'Electronic';
            ClaimDetails_DTO_HUM_V2  oClaims= oClaimDetails.ClaimDetails(oResult,true);
            system.assertEquals(oClaims.sClmMediaCd,'Electronic');
            system.assertEquals(oClaims.sClaimNbr,'C4567');
            Test.stopTest();
        }
    }
    
    /*
     * Method Name : claimLineItemExcode
     * Description : This method tests FetchExcodeLineItem
     * Return type : void
     * Parameter   : None
     */ 
    static testMethod void claimLineItemExcode()
    {    
        Test.setCurrentPage(Page.ClaimDetails_VF_HUM_V3);
        Policy_Member__c oPolMember = [Select id from Policy_Member__c where Name = 'Policy Member PM1' limit 1];
        HUM_Webservice_Callout__c oServiceCallout = [select Name, Certificate_Name__c, End_Point_URL__c, Timeout__c, Content_Type__c, Request_Type__c from HUM_Webservice_Callout__c where name='Claims_Detail' limit 1];   
        string sRecId = oPolMember.id;
        String sClaimType='Medical';
        String sClaimGenKey= '1071000457108901';
        String sClaimNbr='110156323';
        HttpRequest oRequest = new HttpRequest();
        User oUser = [SELECT Id FROM User  WHERE Lastname ='test User1' LIMIT 1];
        system.runAs(oUser)
        {
            Test.startTest();
            ClaimDetails_S_HUM_V2 oClaimDetails=new ClaimDetails_S_HUM_V2();
            oClaimDetails.setHttpRequest(oRequest,oServiceCallout,sClaimType);
            ClaimsServiceDetailsRequest_H_HUM_V2 oClaimDetailsReq = new ClaimsServiceDetailsRequest_H_HUM_V2();
            ClaimsServiceDetailsResponse_H_HUM_V2 oResponse = new ClaimsServiceDetailsResponse_H_HUM_V2();
            ClaimsServiceDetailsResponse_H_HUM_V2.PaymentDetail oCode= new ClaimsServiceDetailsResponse_H_HUM_V2.PaymentDetail();
            oCode.LegacyCode='FA1';
            List<ClaimsServiceDetailsResponse_H_HUM_V2.PaymentDetail> PaymentCode= new List<ClaimsServiceDetailsResponse_H_HUM_V2.PaymentDetail>();
            PaymentCode.add(oCode);
            ClaimsServiceDetailsResponse_H_HUM_V2.PaymentCodes oExCode= new ClaimsServiceDetailsResponse_H_HUM_V2.PaymentCodes();
            oExcode.PaymentDetail = PaymentCode;
            String FetchExcodeLineItem= oClaimDetails.FetchExcodeLineItem(oExcode);
            system.assertNotEquals(FetchExcodeLineItem,'' );
            system.assertEquals(FetchExcodeLineItem,'FA1' );
            Test.stopTest();
        }
    }
    
    
   /*
     * Method Name : testClaimLineItems
     * Description : This tests ClaimLineItems method
     * Return type : void
     * Parameter   : None
     */ 
    static testMethod void testClaimLineItems()
    {    
        Test.setCurrentPage(Page.ClaimDetails_VF_HUM_V3);
        Policy_Member__c oPolMember = [Select id from Policy_Member__c where Name = 'Policy Member PM1' limit 1];
        HUM_Webservice_Callout__c oServiceCallout = [select Name, Certificate_Name__c, End_Point_URL__c, Timeout__c, Content_Type__c, Request_Type__c from HUM_Webservice_Callout__c where name='Claims_Detail' limit 1];   
        User oUser = [SELECT Id FROM User  WHERE Lastname ='test User1' LIMIT 1];
        system.runAs(oUser)
        {
             Test.startTest();
             ClaimDetails_S_HUM_V2 oClaimDetails = new ClaimDetails_S_HUM_V2();
             ClaimsServiceDetailsResponse_H_HUM_V2.PaymentDetail oCode = new ClaimsServiceDetailsResponse_H_HUM_V2.PaymentDetail();
             oCode.LegacyCode='FA1';
             List<ClaimsServiceDetailsResponse_H_HUM_V2.PaymentDetail> PaymentCode = new List<ClaimsServiceDetailsResponse_H_HUM_V2.PaymentDetail>();
             PaymentCode.add(oCode);
             ClaimsServiceDetailsResponse_H_HUM_V2.PaymentCodes oExCode = new ClaimsServiceDetailsResponse_H_HUM_V2.PaymentCodes();
             oExcode.PaymentDetail = PaymentCode;
             ClaimsServiceDetailsResponse_H_HUM_V2.ProviderDetails oprovDetails = new ClaimsServiceDetailsResponse_H_HUM_V2.ProviderDetails();
             oprovDetails.ProviderReasonCode = 'FA1';
             oprovDetails.ProviderReasonMessage  = 'Test EOR';
             List<ClaimsServiceDetailsResponse_H_HUM_V2.ProviderDetails> lstProvCode = new List<ClaimsServiceDetailsResponse_H_HUM_V2.ProviderDetails>();
             lstProvCode.add(oprovDetails);
             ClaimsServiceDetailsResponse_H_HUM_V2.ClaimDetailResult oResult =  new ClaimsServiceDetailsResponse_H_HUM_V2.ClaimDetailResult();
             ClaimsServiceDetailsResponse_H_HUM_V2.DiagnosisCode oDiagnosisCode = new ClaimsServiceDetailsResponse_H_HUM_V2.DiagnosisCode();
             List<String> lstDiagnosisCode = new List<String>();
             lstDiagnosisCode.add('78821');
             oDiagnosisCode.Code = lstDiagnosisCode;
             oResult.DiagnosisCode = oDiagnosisCode;
             ClaimsServiceDetailsResponse_H_HUM_V2.ClaimDetailLines oClaimDetailLines = new ClaimsServiceDetailsResponse_H_HUM_V2.ClaimDetailLines();
             ClaimsServiceDetailsResponse_H_HUM_V2.ClaimDetailLine oDetailLine = new ClaimsServiceDetailsResponse_H_HUM_V2.ClaimDetailLine();
             oDetailLine.PaymentCodes=oExCode;
             oDetailLine.DiagnosisCode = oDiagnosisCode;
             oDetailLine.ProviderDetails = lstProvCode;
             List<ClaimsServiceDetailsResponse_H_HUM_V2.ClaimDetailLine>  oLstClaimDetail = new List<ClaimsServiceDetailsResponse_H_HUM_V2.ClaimDetailLine>();
             oLstClaimDetail.add(oDetailLine);
             system.assert(oLstClaimDetail!= null);
             oClaimDetailLines.ClaimDetailLine= oLstClaimDetail;
             oResult.ClaimDetailLines = oClaimDetailLines;
             List<ClaimLineItems_DTO_HUM_V2.ClaimDetailLine> lstClaimLineItems = oClaimDetails.ClaimLineItems(oResult,'PNAME','P123','NPI01');
             String sLineItemFooter = oClaimDetails.LineItemsFooter(oResult,'10.0','15.20','152');
             system.assert(lstClaimLineItems != null);
             system.assertEquals(lstClaimLineItems[0].sServicingFacility, 'PNAME');
             system.assertEquals(lstClaimLineItems[0].sNPI , 'NPI01');
            Test.stopTest();
        }
    }
 
    /*
     * Method Name : testCodeEditDetails
     * Description : This tests CodeEditRationale method
     * Return type : void
     * Parameter   : None
     */ 
    static testMethod void testCodeEditDetails()
    {    
        Test.setCurrentPage(Page.ClaimDetails_VF_HUM_V3);
        Policy_Member__c oPolMember = [Select id from Policy_Member__c where Name = 'Policy Member PM1' limit 1];
        HUM_Webservice_Callout__c oServiceCallout = [select Name, Certificate_Name__c, End_Point_URL__c, Timeout__c, Content_Type__c, Request_Type__c from HUM_Webservice_Callout__c where name='Claims_Detail' limit 1];   
        User oUser = [SELECT Id FROM User  WHERE Lastname ='test User1' LIMIT 1];
        system.runAs(oUser)
        {
             Test.startTest();
             HttpRequest ohttpRequest = new HttpRequest();
             String sClaimType = 'Medical'; 
             ClaimDetails_S_HUM_V2 oClaimDetailService = new ClaimDetails_S_HUM_V2();
             ClaimLineItems_DTO_HUM_V2 oClaimLineItemsDTO = new ClaimLineItems_DTO_HUM_V2();
             List<String> lstDiagnosisCode = new List<String>();
             lstDiagnosisCode.add('VS123');
             lstDiagnosisCode.add('AS123');
             List<ClaimLineItems_DTO_HUM_V2.PaymentCode> lstPaymentCode = new List<ClaimLineItems_DTO_HUM_V2.PaymentCode>();
             ClaimLineItems_DTO_HUM_V2.PaymentCode oPaymentCd = new ClaimLineItems_DTO_HUM_V2.PaymentCode();
             oPaymentCd.sLegacyCd = '123'; 
             oPaymentCd.sLegacyMessage = 'Legacy Message';
             lstPaymentCode.add(oPaymentCd);
             ClaimsServiceCodeEditDetailRequest_H_HUM oCodeEdit = oClaimDetailService.CodeEditRationale(lstDiagnosisCode,lstPaymentCode,'EM','9','PAID','123','123' );
             ClaimsServiceCodeEditDetailRequest_H_HUM oCodeEditRequest = oClaimDetailService.CodeEditRationale(lstDiagnosisCode,lstPaymentCode,'EM','9','DENIED','123','123' );
             oClaimDetailService.setHttpRequestCodeEdit(ohttpRequest,oServiceCallout,sClaimType);
             system.assert(oCodeEdit.GetCodeEditDetails != null);
             system.assert(oCodeEditRequest.GetCodeEditDetails != null);
             Test.stopTest();
        }
    }
    
    /*
     * Method Name : testCodeEditResponse
     * Description : This tests CodeEditDetailsResponse method
     * Return type : void
     * Parameter   : None
     */ 
    static testMethod void testCodeEditResponse()
    {    
        Test.setCurrentPage(Page.ClaimDetails_VF_HUM_V3);
        Policy_Member__c oPolMember = [Select id from Policy_Member__c where Name = 'Policy Member PM1' limit 1];
        HUM_Webservice_Callout__c oServiceCallout = [select Name, Certificate_Name__c, End_Point_URL__c, Timeout__c, Content_Type__c, Request_Type__c from HUM_Webservice_Callout__c where name='Claims_Detail' limit 1];   
        User oUser = [SELECT Id FROM User  WHERE Lastname ='test User1' LIMIT 1];
        system.runAs(oUser)
        {
             Test.startTest();
             ClaimDetails_S_HUM_V2 oClaimDetailService = new ClaimDetails_S_HUM_V2();
             ClaimsServiceCodeEditDtlResponse_H_HUM.GetCodeEditDetailsResponse oGetCodeEdit = new  ClaimsServiceCodeEditDtlResponse_H_HUM.GetCodeEditDetailsResponse();
             List<ClaimsServiceCodeEditDtlResponse_H_HUM.RationaleDescriptionDTO> lstRationale = new List<ClaimsServiceCodeEditDtlResponse_H_HUM.RationaleDescriptionDTO>();
             ClaimsServiceCodeEditDtlResponse_H_HUM.RationaleDescriptionDTO oRationale = new  ClaimsServiceCodeEditDtlResponse_H_HUM.RationaleDescriptionDTO();
             oRationale.CodeEditInd = 'iHealth';
             oRationale.RationaleDesc = 'Rationale Description';
             lstRationale.add(oRationale);
             ClaimsServiceCodeEditDtlResponse_H_HUM.RationaleDescriptionCodes oCodes = new ClaimsServiceCodeEditDtlResponse_H_HUM.RationaleDescriptionCodes(); 
             oCodes.RationaleDescriptionDTO = lstRationale;
             ClaimsServiceCodeEditDtlResponse_H_HUM.RationaleDTO oRationaleDTO = new ClaimsServiceCodeEditDtlResponse_H_HUM.RationaleDTO();
             oRationaleDTO.RationaleDescriptionCodes = oCodes;
             oGetCodeEdit.RationaleDTO  = oRationaleDTO;
             List<ClaimsGetCodeEditDetails_DTO_HUM>  oCodeEditDTO = oClaimDetailService.CodeEditDetailsResponse(oGetCodeEdit);
             System.assertEquals(oCodeEditDTO[0].sCodeEditInd,'iHealth');
             System.assertEquals(oCodeEditDTO[0].sRationaleDesc ,'Rationale Description');
             Test.stopTest();
        }
    }
    
    /*
     * Method Name : testgetSequenceNumberMTV
     * Description : This tests getSequenceNumberMTV method
     * Return type : void
     * Parameter   : None
     */ 
    static testMethod void testgetSequenceNumberMTV()
    {    
        Test.setCurrentPage(Page.ClaimDetails_VF_HUM_V3);
        Policy_Member__c oPolMember = [Select id from Policy_Member__c where Name = 'Policy Member PM1' limit 1];
        HUM_Webservice_Callout__c oServiceCallout = [select Name, Certificate_Name__c, End_Point_URL__c, Timeout__c, Content_Type__c, Request_Type__c from HUM_Webservice_Callout__c where name='Claims_Detail' limit 1];   
        User oUser = [SELECT Id FROM User  WHERE Lastname ='test User1' LIMIT 1];
        system.runAs(oUser)
        {
             Test.startTest();
             ClaimDetails_S_HUM_V2 oClaimDetailService = new ClaimDetails_S_HUM_V2();
             List<ClaimLineItems_DTO_HUM_V2.ClaimDetailLine> lstClaimDetails = new List<ClaimLineItems_DTO_HUM_V2.ClaimDetailLine>();
             ClaimLineItems_DTO_HUM_V2.ClaimDetailLine oClaimDetails = new ClaimLineItems_DTO_HUM_V2.ClaimDetailLine();
             oClaimDetails.sLastProcessDate = '01/01/2016';
             oClaimDetails.sSrcClaimLineSequence = '0' ;
             lstClaimDetails.add(oClaimDetails);
             String sSeqNumber = oClaimDetailService.getSequenceNumberMTV(lstClaimDetails,'LV','MED');
             system.assertEquals(sSeqNumber,'{"01/01/2016":"01/01/2016"}');
			 
			 List<ClaimLineItems_DTO_HUM_V2.PaymentCode> lstpaymentcode = new List<ClaimLineItems_DTO_HUM_V2.PaymentCode>();
             List<ClaimsServiceDetailsResponse_H_HUM_V2.PaymentDetail> lstPaymentCodes = new List<ClaimsServiceDetailsResponse_H_HUM_V2.PaymentDetail>();
             ClaimsServiceDetailsResponse_H_HUM_V2.PaymentDetail oPaymentDetail = new ClaimsServiceDetailsResponse_H_HUM_V2.PaymentDetail();
             oPaymentDetail.LegacyCode = '123';
             oPaymentDetail.LegacyMessage = 'ABC';
             lstPaymentCodes.add(oPaymentDetail);
             lstpaymentcode = ClaimDetails_S_HUM_V2.getPayCodes(lstPaymentCodes);
            
             oPaymentDetail.LegacyCode = '<ABC';
             lstPaymentCodes.add(oPaymentDetail);
             lstpaymentcode = ClaimDetails_S_HUM_V2.getPayCodes(lstPaymentCodes);
            
             List<ClaimsServiceDetailsResponse_H_HUM_V2.ProviderDetails> lstProviderDetails = new List<ClaimsServiceDetailsResponse_H_HUM_V2.ProviderDetails>();
             ClaimsServiceDetailsResponse_H_HUM_V2.ProviderDetails oProviderDetails = new ClaimsServiceDetailsResponse_H_HUM_V2.ProviderDetails();
             oProviderDetails.ProviderReasonCode = '123';
             oProviderDetails.ProviderReasonMessage = 'ABC';
             lstProviderDetails.add(oProviderDetails);
             lstpaymentcode = ClaimDetails_S_HUM_V2.getProviderCodes(lstProviderDetails);
            
             oProviderDetails.ProviderReasonCode = '<ABC';
             lstProviderDetails.add(oProviderDetails);
             lstpaymentcode = ClaimDetails_S_HUM_V2.getProviderCodes(lstProviderDetails);
             Test.stopTest();
        }
    }
}