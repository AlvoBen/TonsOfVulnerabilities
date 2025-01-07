/**************************************************************************************************************** 
Apex Class Name  : ClaimsSummary_Service_T_HUM_V2
Version          : 1.0  
Created Date     : March 06, 2018
Function         : Test class for ClaimsSummary_S_HUM_V2
******************************************************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*----------------------------------------------------------------------------------------------------------------
* Prasanthi Kandula          212620             03/06/2018             REQ - 290133 - Updated logic as part of claims service v3 changes to display 500 claims at a time on button click
* Suresh Gottipati                              06/15/2018             REQ - 366902 - Claim Summary section in Claim Summary page
* Santhi Mandava                                03/12/2019             Removed "DpaaSWebServiceMockImpl" usage
* Santhi Mandava                                04/15/2019             Replaced "Member_Id_Value__c" with Name
* Suraj Patil 				277418				03/11/2021			   REQ - 1279689 Claims old version Decommission 
* Prasanthi Kandula                           07/02/2021             US - 2356070 Claims Summary performance improvement 
******************************************************************************************************************/
@isTest
private Class ClaimsSummary_Service_T_HUM_V2
{
   /*
    *  This method is used to prepare test data
    *  @name   prepareTestData
    *  @param  NA
    *  @return NA
    *  @throws NA
    */  
    @testSetup static void prepareTestData() 
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertEquals(oCCSUser.LastName,'test User1');
        
        System.runAs (oCCSUser) 
        {
            HUM_Webservice_Callout__c oHUMClaimSummaryService = new HUM_Webservice_Callout__c();
            oHUMClaimSummaryService.name='ClaimsService_GetClaimsSummary_V3';
            oHUMClaimSummaryService.Active__c=true;
            oHUMClaimSummaryService.Certificate_Name__c='testts';
            oHUMClaimSummaryService.End_Point_URL__c='https://testst-cmservicews.hum.com';
            oHUMClaimSummaryService.Timeout__c='120000';
            oHUMClaimSummaryService.Content_Type__c='application/json';
            oHUMClaimSummaryService.Request_Type__c='POST';
            insert oHUMClaimSummaryService;
            system.assert(oHUMClaimSummaryService.id!=null);
            system.assert(oHUMClaimSummaryService.name == 'ClaimsService_GetClaimsSummary_V3');
            
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
            objPolMem.Name = 'testpolmem11';
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
            system.assertEquals(objPolMem.Name, 'testpolmem11');
            system.assertEquals(objPolMem.Policy_Member_External_ID__c, '00|ODS|6925004434817|12345678|34567|2013-07-01');
            
            Policy_Member__c objPolMem2 = new Policy_Member__c();
            objPolMem2.Name = 'testpolmem22';
            objPolMem2.Relationship__c = 'Self';
            objPolMem2.Subscriber__c = objPolMem2.Id;
            objPolMem2.Effective_Date__c= '05/05/2016';
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
             
            Member_ID__c objMemberID1 = new Member_ID__c();
            objMemberID1.policy_member__c = objPolMem.id;
            objMemberID1.policy__c = objpol.Id;
            objMemberID1.Name = '21313211';
            objMemberID1.Member_Card_External_ID__c = 'ODS12345'; 
            objMemberID1.Member_Dependent_Code__c  = '03';        
            insert objMemberID1;
            system.assertequals('21313211',objMemberID1.Name );
            system.assertequals('ODS12345',objMemberID1.Member_Card_External_ID__c );
            
            Member_ID__c objMemberID2 = new Member_ID__c();
            objMemberID2.policy_member__c = objPolMem2.id;
            objMemberID2.policy__c = objpol.Id;
            objMemberID2.Name = '123456';
            objMemberID2.Member_Card_External_ID__c = 'ODS123456'; 
            objMemberID2.Member_Dependent_Code__c  = '00';        
            insert objMemberID2;
        }
    }
    
   /*
    *  This method is used to run positive scenarios for Medical claim data
    *  @name   runPositiveMedicalData
    *  @param  NA
    *  @return NA
    *  @throws NA
    */  
    static testMethod void runPositiveMedicalData()
    {    
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
           
        System.runAs (oCCSUser) 
        { 
 
            Account oAccount = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account'];  
            System.assertEquals(oAccount.Tenant_Id__c, '00');
            
            String sAccRecType = oAccount.RecordType.Name;
            System.assertEquals(sAccRecType,'Member');
            
            Policy_Member__c oPolMem =  [Select Id,Name,Subscriber__c ,Relationship__c,Policy__r.Group_Name__r.Home_Office_Account__c, Member__c, Policy__c, Policy_Member_External_ID__c , Policy__r.Product__r.ProductCode from Policy_Member__c where Member__c =: oAccount.Id];
            System.assertEquals(oPolMem.Relationship__c, 'Self');
            
            Member_ID__c oMemberId = [select Member_Dependent_Code__c, Policy_Member__r.Effective_Date__c, Policy_Member__r.End_Date__c, Policy_Member__c, Name from Member_ID__c where name='21313211' LIMIT 1];
            System.assertEquals(oMemberId.Name,'21313211');
            
            HUM_Webservice_Callout__c oServiceCallout = [select Name, Certificate_Name__c, End_Point_URL__c, Timeout__c, Content_Type__c, Request_Type__c from HUM_Webservice_Callout__c where name='ClaimsService_GetClaimsSummary_V3' limit 1];   
            System.assertEquals(oServiceCallout.Certificate_Name__c,'testts');
            
             String Response = '{"ClaimSummaryReponse": {"ClaimSummaryResults": {"ClaimSummaryResult":    [{"AdjustmentIndicator": "N",'+
        '"ClaimNumber": "433921091","ClaimTypeCode": "A","ClaimType": "Ambulatory","ClaimReceiptDate": "11/22/2016",'+
        '"ChargeAmount": "1111.86","CopaymentAmount": "0.00","CoInsuranceAmount": "0.00",'+
        '"DeductibleAmount": "0.00","DisplayGroupId": "","EncounterIndicator": "",'+
        '"Id": "59b9fb6f2736be02a0ee3445","LineOfBusinessCode": "",'+
        '"LastProcessDate": "11/26/2016","MemberId": "","MemberResponsibilityAmount": "999999999.99",'+
        '"PaidAmount": "0.00","PlatformCode": "LV","PreDeterminationIndicator": "","PreDeterminationNumber": "","ProviderTaxId": "452337175",'+
        '"SourceNationalProviderId": "1184918864","ServiceEndDate": "05/08/2016","ServiceStartDate": "05/05/2016","StatusDescription": "COMPLETED",'+
        '"PaidStatusDescription": "Paid"},'+
        '{"AdjustmentIndicator": "N",'+
        '"ClaimNumber": "433921091","ClaimTypeCode": "A","ClaimType": "Ambulatory","ClaimReceiptDate": "11/22/2016",'+
        '"ChargeAmount": "1111.86","CopaymentAmount": "0.00","CoInsuranceAmount": "0.00",'+
        '"DeductibleAmount": "0.00","DisplayGroupId": "","EncounterIndicator": "",'+
        '"Id": "59b9fb6f2736be02a0ee3445","LineOfBusinessCode": "",'+
        '"LastProcessDate": "11/26/2016","MemberId": "H56499728-00","MemberResponsibilityAmount": "999999999.99",'+
        '"PaidAmount": "0.00","PlatformCode": "LV","PreDeterminationIndicator": "","PreDeterminationNumber": "","ProviderTaxId": "452337175",'+
        '"SourceNationalProviderId": "1184918864","ServiceEndDate": "05/08/2016","ServiceStartDate": "05/05/2016","StatusDescription": "COMPLETED",'+
        '"PaidStatusDescription": "Paid"}'+
        ']},"EndRecordNumber": "2","TotalRecordNumber": "2"}}';

        String sRecId = oPolMem.id;
            Test.StartTest();
            ClaimsSummary_S_HUM_V2 oClaimsSummary = new ClaimsSummary_S_HUM_V2 ();
            Set<String> setPolicyMembers  = new Set<String>();
            setPolicyMembers.add(oPolMem.Name);
            oClaimsSummary.buildRequest(oPolMem.id,'1',setPolicyMembers);
            oClaimsSummary.buildRequestGoButton('433921091');
            List<ClaimsSummary_Wrapper_DTO_HUM> lstClaimsummary = new  List<ClaimsSummary_Wrapper_DTO_HUM>();
            lstClaimsummary  = oClaimsSummary.processResponse(Response);
            System.assertNotEquals(lstClaimsummary.size(),0);
            oClaimsSummary.SummaryGoButton(lstClaimsummary,'608146239');
            oClaimsSummary.SummaryGoButton(lstClaimsummary,'433921091');
            oClaimsSummary.noOnClickGO(Response);
            HttpRequest oRequest = new HttpRequest();
            oClaimsSummary.setupRequest(oRequest,oServiceCallout,sRecId,'1',setPolicyMembers);
            oClaimsSummary.setupGoButtonRequest(oRequest,oServiceCallout,'433921091');
            Test.stopTest();  
        }
    }
    
   /*
    *  This method is used to run positive scenarios for Dental claim data
    *  @name   runPositiveDentalData
    *  @param  NA
    *  @return NA
    *  @throws NA
    */ 
    static testMethod void runPositiveDentalData()
    {         
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
           
        System.runAs (oCCSUser) 
        {  
            Account oAccount = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account' limit 1];  
            System.assertEquals(oAccount.Tenant_Id__c, '00');
            
            String sAccRecType = oAccount.RecordType.Name;
            System.assertEquals(sAccRecType,'Member');
            
            Policy_Member__c oPolMem =  [Select Id,Name,Subscriber__c ,Relationship__c,Policy__r.Group_Name__r.Home_Office_Account__c, Member__c, Policy__c, Policy_Member_External_ID__c , Policy__r.Product__r.ProductCode from Policy_Member__c where Member__c =: oAccount.Id];
            System.assertEquals(oPolMem.Relationship__c, 'Self');
            
            Member_ID__c oMemberId = [select Member_Dependent_Code__c, Policy_Member__r.Effective_Date__c, Policy_Member__r.End_Date__c, Policy_Member__c, Name from Member_ID__c where name='21313211' LIMIT 1];
            System.assertEquals(oMemberId.Name,'21313211');
            
            HUM_Webservice_Callout__c oServiceCallout = [select Name, Certificate_Name__c, End_Point_URL__c, Timeout__c, Content_Type__c, Request_Type__c from HUM_Webservice_Callout__c where name='ClaimsService_GetClaimsSummary_V3' limit 1];   
            System.assertEquals(oServiceCallout.Certificate_Name__c,'testts');
            
            String Response = '{"ClaimSummaryReponse": {"ClaimSummaryResults": {"ClaimSummaryResult":    [{"AdjustmentIndicator": "N",'+
                           '"ClaimNumber": "433921091","ClaimTypeCode": "A","ClaimType": "Ambulatory","ClaimReceiptDate": "11/22/2016",'+
                           '"ChargeAmount": "1111.86","CopaymentAmount": "0.00","CoInsuranceAmount": "0.00",'+
                           '"DeductibleAmount": "0.00","DisplayGroupId": "","EncounterIndicator": "",'+
                           '"Id": "59b9fb6f2736be02a0ee3445","LineOfBusinessCode": "",'+
                           '"LastProcessDate": "11/26/2016","MemberId": "123456-00","MemberResponsibilityAmount": "999999999.99",'+
                           '"PaidAmount": "0.00","PlatformCode": "LV","PreDeterminationIndicator": "","PreDeterminationNumber": "","ProviderTaxId": "452337175",'+
                           '"SourceNationalProviderId": "1184918864","ServiceEndDate": "05/05/2016","ServiceStartDate": "05/05/2016","StatusDescription": "COMPLETED",'+
                           '"PaidStatusDescription": "Paid"},'+
                           '{"AdjustmentIndicator": "N",'+
                           '"ClaimNumber": "433921091","ClaimTypeCode": "A","ClaimType": "Ambulatory","ClaimReceiptDate": "11/22/2016",'+
                           '"ChargeAmount": "1111.86","CopaymentAmount": "0.00","CoInsuranceAmount": "0.00",'+
                           '"DeductibleAmount": "0.00","DisplayGroupId": "","EncounterIndicator": "",'+
                           '"Id": "59b9fb6f2736be02a0ee3445","LineOfBusinessCode": "",'+
                           '"LastProcessDate": "11/26/2016","MemberId": "H56499728-00","MemberResponsibilityAmount": "999999999.99",'+
                           '"PaidAmount": "0.00","PlatformCode": "LV","PreDeterminationIndicator": "","PreDeterminationNumber": "","ProviderTaxId": "452337175",'+
                           '"SourceNationalProviderId": "1184918864","ServiceEndDate": "05/05/2016","ServiceStartDate": "05/05/2016","StatusDescription": "COMPLETED",'+
                           '"PaidStatusDescription": "Paid"}'+
                           ']},"EndRecordNumber": "2","TotalRecordNumber": "2"}}';
             String sRecId = oPolMem.id;
            
            Test.StartTest();
            HttpRequest oRequest = new HttpRequest();
            ClaimsSummary_S_HUM_V2 oClaimsSummary = new ClaimsSummary_S_HUM_V2 ();
            List<ClaimsSummary_Wrapper_DTO_HUM> lstClaimsummary = New  List<ClaimsSummary_Wrapper_DTO_HUM>();
            lstClaimsummary  = oClaimsSummary.processResponse(Response);            
            System.assertNotEquals(lstClaimsummary.size(),0);
            Set<String> setPolicyMembers  = new Set<String>();
            setPolicyMembers.add(oPolMem.Name);
            oClaimsSummary.setupRequest(oRequest,oServiceCallout,sRecId,'1',setPolicyMembers);
            Member_ID__c oMemberIdRec = [select Member_Dependent_Code__c, Policy_Member__r.Effective_Date__c, Policy_Member__r.End_Date__c, Policy_Member__c, Name from Member_ID__c where name='123456' LIMIT 1];
            List<Member_ID__c> lstMemId = new List<Member_ID__c>();
            lstMemId.add(oMemberId);
            oClaimsSummary.updateGroupNumber(lstMemId,lstClaimsummary);
            oClaimsSummary.convertStringToDate('10');
            lstMemId = new List<Member_ID__c>();
            lstMemId.add(oMemberIdRec);
            oClaimsSummary.updateGroupNumber(lstMemId,lstClaimsummary);
            Policy_Member__c oPolMember =  [Select Id,Name,Subscriber__c ,end_date__c, Relationship__c,Policy__r.Group_Name__r.Home_Office_Account__c, Member__c, Policy__c, Policy_Member_External_ID__c , Policy__r.Product__r.ProductCode from Policy_Member__c where name='testpolmem22' limit 1];
            oPolMember.end_date__c = '05/05/2016';
            update oPolMember;
            lstMemId = new List<Member_ID__c>();
            Member_ID__c oMbrId = [select Member_Dependent_Code__c, Policy_Member__r.Effective_Date__c, Policy_Member__r.End_Date__c, Policy_Member__c, Name from Member_ID__c where name='123456' LIMIT 1];
            lstMemId.add(oMbrId);
            oClaimsSummary.updateGroupNumber(lstMemId,lstClaimsummary);
            Test.stopTest();  
        }
    }           
            
   /*
    *  This method is used to run negatove scenarios
    *  @name   testNegativeData
    *  @param  NA
    *  @return NA
    *  @throws NA
    */ 
    static testMethod void testNegativeData()
    {   
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
           
        System.runAs (oCCSUser) 
        {  
            Account oAccount = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account2'];  
            System.assertEquals(oAccount.Tenant_Id__c, '01');
            
            String sAccRecType = oAccount.RecordType.Name;
            System.assertEquals(sAccRecType,'Member');
            
            Policy_Member__c oPolMem =  [Select Id,Name,Subscriber__c ,Relationship__c,Policy__r.Group_Name__r.Home_Office_Account__c, Member__c, Policy__c, Policy_Member_External_ID__c , Policy__r.Product__r.ProductCode from Policy_Member__c where Member__c =: oAccount.Id];
            System.assertEquals(oPolMem.Relationship__c, 'Self');
            
            Member_ID__c oMemberId = [select Member_Dependent_Code__c, Policy_Member__r.Effective_Date__c, Policy_Member__r.End_Date__c, Policy_Member__c, Name from Member_ID__c where name='21313211' LIMIT 1];
            System.assertEquals(oMemberId.Name,'21313211');
            
            HUM_Webservice_Callout__c oServiceCallout = [select Name, Certificate_Name__c, End_Point_URL__c, Timeout__c, Content_Type__c, Request_Type__c from HUM_Webservice_Callout__c where name='ClaimsService_GetClaimsSummary_V3' limit 1];   
            System.assertEquals(oServiceCallout.Certificate_Name__c,'testts');
            
             String Response = '{"ClaimSummaryReponse": {"ClaimSummaryResults": {"ClaimSummaryResult":    [{"AdjustmentIndicator": "N",'+
                           '"ClaimNumber": "433921091","ClaimTypeCode": "A","ClaimType": "Ambulatory","ClaimReceiptDate": "11/22/2016",'+
                           '"ChargeAmount": "1111.86","CopaymentAmount": "0.00","CoInsuranceAmount": "0.00",'+
                           '"DeductibleAmount": "0.00","DisplayGroupId": "","EncounterIndicator": "",'+
                           '"Id": "59b9fb6f2736be02a0ee3445","LineOfBusinessCode": "",'+
                           '"LastProcessDate": "11/26/2016","MemberId": "","MemberResponsibilityAmount": "999999999.99",'+
                           '"PaidAmount": "0.00","PlatformCode": "LV","PreDeterminationIndicator": "","PreDeterminationNumber": "","ProviderTaxId": "452337175",'+
                           '"SourceNationalProviderId": "1184918864","ServiceEndDate": "05/08/2016","ServiceStartDate": "05/05/2016","StatusDescription": "COMPLETED",'+
                           '"PaidStatusDescription": "Paid"},'+
                           '{"AdjustmentIndicator": "N",'+
                           '"ClaimNumber": "433921091","ClaimTypeCode": "A","ClaimType": "Ambulatory","ClaimReceiptDate": "11/22/2016",'+
                           '"ChargeAmount": "1111.86","CopaymentAmount": "0.00","CoInsuranceAmount": "0.00",'+
                           '"DeductibleAmount": "0.00","DisplayGroupId": "","EncounterIndicator": "",'+
                           '"Id": "59b9fb6f2736be02a0ee3445","LineOfBusinessCode": "",'+
                           '"LastProcessDate": "11/26/2016","MemberId": "H56499728-00","MemberResponsibilityAmount": "999999999.99",'+
                           '"PaidAmount": "0.00","PlatformCode": "LV","PreDeterminationIndicator": "","PreDeterminationNumber": "","ProviderTaxId": "452337175",'+
                           '"SourceNationalProviderId": "1184918864","ServiceEndDate": "05/08/2016","ServiceStartDate": "05/05/2016","StatusDescription": "COMPLETED",'+
                           '"PaidStatusDescription": "Paid"}'+
                           ']},"EndRecordNumber": "2","TotalRecordNumber": "2"}}';
            string invalidResponse = '{"ClaimSummary":{}}';
            String sRecId = oPolMem.id;
            Test.StartTest();
            HttpRequest oRequest = new HttpRequest();
            ClaimsSummary_S_HUM_V2 oClaimsSummary = new ClaimsSummary_S_HUM_V2 ();
            List<ClaimsSummary_Wrapper_DTO_HUM> lstClaimsummary = new  List<ClaimsSummary_Wrapper_DTO_HUM>();
            lstClaimsummary  = oClaimsSummary.processResponse(Response);
            System.assertNotEquals(lstClaimsummary.size(),0);
            List<ClaimsSummary_Wrapper_DTO_HUM> lstClaimsummaryEmpty = oClaimsSummary.processResponse(invalidResponse);
            System.assertEquals(lstClaimsummaryEmpty.size(),0);
            Set<String> setPolicyMembers  = new Set<String>();
            setPolicyMembers.add(oPolMem.Name);
            oClaimsSummary.setupRequest(oRequest,oServiceCallout,sRecId,'1',setPolicyMembers);
            Test.stopTest();  
        }
    }                    

    
}