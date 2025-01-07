/**************************************************************************** 
Apex Class Name  : ClaimsSummary_T_HUM_V2 
Version          : 1.0  
Created Date     : March 06, 2018
Function         : Test class for ClaimsSummary_C_HUM_V2 
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                    Description
*---------------------------------------------------------------------------------------------------
 *   Prasanthi Kandula       212620             03/06/2018           REQ - 290133 - Updated logic as part of claims service v3 changes to display 500 claims at a time on button click
*    Anurag Shah             235263             05/23/2018           Reverting the Resolution changes.
*   Suresh Gottipati                            06/15/2018           REQ - 366902 - Claim Summary section in Claim Summary page.
*   Praveen Kumar Parimi       230338             07/12/2018           Adding CAS web UI references for their coverage.
*   Santhi Mandava                                04/15/2019           Replaced "Member_Id_Value__c" with Name 
*   Yogesh Gupta								09/23/2019           REQ-402887 Inserting applauncher custom setting for in test class
*   Prasanthi Kandula                           07/02/2021           US - 2356070 Claims Summary performance improvement 
***************************************************************************************************/
@isTest
private Class ClaimsSummary_T_HUM_V2
{
    /*
     * Method Name : prepareTestData
     * Description : Used to create wrapper class data
     * Return type : void
     * Paramater   : NA
     */
    @testSetup static void prepareTestData() 
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertEquals(oCCSUser.LastName,'test User1');
        
        System.runAs (oCCSUser) 
        {
            HUM_Webservice_Callout__c oHUMClaimSummaryService = new HUM_Webservice_Callout__c();
            oHUMClaimSummaryService.name = 'ClaimsService_GetClaimsSummary_V3';
            oHUMClaimSummaryService.Active__c = true;
            oHUMClaimSummaryService.Certificate_Name__c = 'testts';
            oHUMClaimSummaryService.End_Point_URL__c = 'https://testst-cmservicews.hum.com/';
            oHUMClaimSummaryService.Timeout__c = '120000';
            oHUMClaimSummaryService.Content_Type__c = 'application/json';
            oHUMClaimSummaryService.Request_Type__c = 'POST';
            insert oHUMClaimSummaryService;
            system.assert(oHUMClaimSummaryService.id != null);
            system.assert(oHUMClaimSummaryService.name == 'ClaimsService_GetClaimsSummary_V3');
            
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
            
            List<HUMConstants__c> lstConstants = new List<HUMConstants__c>();
            lstConstants.add(new HUMConstants__c(Name = 'HOME_OFFICE_ALL',StringValue__c = 'Home Office All',IntegerValue__c = 0));
            lstConstants.add(new HUMConstants__c(Name = 'HOME_OFFICE_MEDICAL',StringValue__c = 'HOME_OFFICE_MEDICAL',IntegerValue__c = 0));
            lstConstants.add(new HUMConstants__c(Name = 'HOME_OFFICE_DENTAL',StringValue__c = 'HOME_OFFICE_DENTAL',IntegerValue__c = 0));
            insert lstConstants;
            system.assertEquals(lstConstants.size(), 3); 
               
            Account objAcc = HUMTestDataHelperTest.getAccount(); 
            objAcc.FirstName = 'Test';
            objAcc.LastName = 'Account';
            objAcc.Tenant_Id__c = '00';
            objAcc.Enterprise_ID__c = '1005577525';
            objAcc.General_Account__c = true;
            objAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();
            insert objAcc;
            System.assertEquals(objAcc.Tenant_Id__c,'00');
            
            Product2 oProd = new Product2(Name = 'Laptop X200',Segment_Type__c = 'MCR',ProductCode = 'MED', Major_LOB__c = 'MER');
            insert oProd;
            System.assertNotEquals(oProd.Id, NULL, 'Value not matched');
            
            Policy__c objpol = HUMTestDataHelperTest.getPolicy();
            objpol.Name = 'testPolicy';
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
            objPolMem.Name = 'H5649972800';
            objPolMem.Relationship__c = 'Self';
            objPolMem.Relationship_Code__c = '00';
            objPolMem.Subscriber__c = objPolMem.Id;
            objPolMem.Effective_Date__c = Date.today().addDays(-1).format();
            objPolMem.End_Date__c = Date.today().addDays(1).format();
            objPolMem.Policy_Member_External_ID__c = '00|ODS|6925004434817|12345678|34567|2013-07-01';
            objPolMem.Member__c = objAcc.Id;
            objPolMem.Policy__c = objpol.Id;
            objPolMem.Policy__r = objpol;
            insert objPolMem;
            system.assertEquals(objPolMem.Relationship__c, 'Self');
            
            Member_ID__c objMemberID1 = new Member_ID__c();
            objMemberID1.policy_member__c = objPolMem.id;
            objMemberID1.policy__c = objpol.Id;
            objMemberID1.Name = '12345';
            objMemberID1.Member_Card_External_ID__c = 'ODS12345'; 
            objMemberID1.Member_Dependent_Code__c  = '03';
            objMemberID1.Type__c = 'SSN';               
            insert objMemberID1;
            system.assertequals('12345',objMemberID1.Name );
            
            //REQ-402887 Inserting applauncher in custom setting for test class
            List<HUMConstants__c> listOfConstantToInsert = new List<HUMConstants__c>();
            listOfConstantToInsert.add(new HUMConstants__c(Name = 'APPLAUNCHER_ENVIRONMENT', StringValue__c = 'TEST', IntegerValue__c = 0));
            insert listOfConstantToInsert; 
        }
    }
    
     /*
     * Method Name : runData
     * Description : Used to run scenerios of test data
     * Return type : void
     * Paramater   : NA
     */ 
    static testMethod void runPositiveData()
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
            
            Policy_Member__c oPolMem =  [Select Id,Name,Subscriber__c ,Relationship__c,Policy__r.Group_Name__r.Home_Office_Account__c, Member__c, Policy__c, Policy_Member_External_ID__c , Policy__r.Product__r.ProductCode from Policy_Member__c where Member__c =: oAccount.Id];
            System.assertEquals(oPolMem.Relationship__c, 'Self');
             
            Test.setCurrentPage(Page.ClaimSummary_VF_HUM_V2);
            ApexPages.currentPage().getParameters().put('Id',oPolMem.Id);
            System.assertEquals(ApexPages.currentPage().getParameters().get('Id'),oPolMem.Id);
            ApexPages.currentPage().getParameters().put('Name',oAccount.Name);
            System.assertEquals(ApexPages.currentPage().getParameters().get('Name'),oAccount.Name); 
                       
            string json1 = '{"ClaimSummaryReponse": {"ClaimSummaryResults": {"ClaimSummaryResult":    [{"AdjustmentIndicator": "N",'+
                           '"ClaimNumber": "433921091","ClaimTypeCode": "A","ClaimType": "Ambulatory","ClaimReceiptDate": "11/22/2016",'+
                           '"ChargeAmount": "1111.86","CopaymentAmount": "0.00","CoInsuranceAmount": "0.00",'+
                           '"DeductibleAmount": "0.00","DisplayGroupId": "","EncounterIndicator": "",'+
                           '"Id": "59b9fb6f2736be02a0ee3445","LineOfBusinessCode": "",'+
                           '"LastProcessDate": "11/26/2016","MemberId": "","MemberResponsibilityAmount": "999999999.99",'+
                           '"PaidAmount": "0.00","PlatformCode": "LV","PreDeterminationIndicator": "","PreDeterminationNumber": "","ProviderTaxId": "452337175",'+
                           '"SourceNationalProviderId": "1184918864","ServiceEndDate": "05/08/2016","ServiceStartDate": "05/05/2016","StatusDescription": "COMPLETED",'+
                           '"PaidStatusDescription": "Paid"}]},"EndRecordNumber": "1","TotalRecordNumber": "1"}}';
                          
            String json2 = '{"ClaimSummaryReponse": {"ClaimSummaryResults": {"ClaimSummaryResult":    [{"AdjustmentIndicator": "N",'+
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
            
            
            
            ClaimsSummary_C_HUM_V2 oClaimsSummary = New ClaimsSummary_C_HUM_V2();
            oClaimsSummary.sStartCount1='claims';
             
            oClaimsSummary.initiateRequest();
            Continuation objCon = New Continuation(120);
            objCon.continuationMethod='processResponse';
            System.assertEquals('processResponse', objCon.continuationMethod);
            oClaimsSummary.processResponse();
            
            HttpResponse oResponse = New HttpResponse();
            oResponse.setHeader('Content-Type', 'application/json');
            oResponse.setHeader('SOAPAction', '');    
            oResponse.setHeader('Connection','keep-alive');
            oResponse.setBody(json2);
            oResponse.setStatusCode(200); 
            
            Set<String> oSet = new Set<String>();
            oSet.add('H5649972800');
            
            oClaimsSummary.setPolicyMembers = oSet;
            oClaimsSummary.responseparseGoButton(oResponse);
            
            ClaimsSummary_C_HUM_V2 oClaimsSummary1 = New ClaimsSummary_C_HUM_V2();
            
            oClaimsSummary1.setPolicyMembers = oSet;
            system.assertEquals(oSet,oClaimsSummary1.setPolicyMembers);
            oClaimsSummary1.sClaimSearch = '433921091';
            system.assertEquals('433921091',oClaimsSummary1.sClaimSearch);
            oClaimsSummary1.sClaimNbr = '433921091';
            system.assertEquals('433921091',oClaimsSummary1.sClaimNbr);
            oClaimsSummary1.searchClaim();
            oClaimsSummary1.sClaimNbr = '';
            system.assertEquals('',oClaimsSummary1.sClaimNbr);
            oClaimsSummary1.sClaimSearch = '';
            system.assertEquals('',oClaimsSummary1.sClaimSearch);
            oClaimsSummary1.searchClaim();
            
            Continuation objCon1 = New Continuation(120);
            objCon1.continuationMethod = 'processGoButtonResponse';
            system.assertEquals('processGoButtonResponse',objCon1.continuationMethod);
            oClaimsSummary1.processGoButtonResponse();
            
            HttpResponse oResponse1 = New HttpResponse();
            oResponse1.setHeader('Content-Type', 'application/json');
            oResponse1.setHeader('SOAPAction', '');    
            oResponse1.setHeader('Connection','keep-alive');
            
                 
            oResponse1.setBody(json1);
            oResponse1.setStatusCode(200); 
            oClaimsSummary1.responseparseGoButton(oResponse1);
            oClaimsSummary1.infoSet();
            oClaimsSummary1.responseparse(oResponse1);
            oResponse1.setStatusCode(800); 
            oClaimsSummary1.responseparseGoButton(oResponse1);
            oClaimsSummary1.responseparse(oResponse1);
            
            
            List<ClaimsSummary_Wrapper_DTO_HUM> ClaimResponseList  = New list<ClaimsSummary_Wrapper_DTO_HUM>();
            ClaimsSummary_Wrapper_DTO_HUM objectWrapper = New ClaimsSummary_Wrapper_DTO_HUM();
            objectWrapper.sDeductAmt = '123';
            System.assertEquals('123', objectWrapper.sDeductAmt);
            objectWrapper.sCopayAmt = '123';
            System.assertEquals('123', objectWrapper.sCopayAmt);
            objectWrapper.sCoInsAmt = '123';
            System.assertEquals('123', objectWrapper.sCoInsAmt);
            ClaimResponseList.add(objectWrapper);
            oClaimsSummary1.formDropdown(ClaimResponseList);
            
            Test.stopTest();  
        }
    }
     /*
     * Method Name : claimSummaryException
     * Description : This method tests the Exception handling 
     * Return type : void
     * Parameter   : None
     */ 
    Static testMethod void claimSummaryException() 
    {
        Test.setCurrentPage(Page.ClaimSummary_VF_HUM_V2);
        User oCCSUser = [Select id, UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        {
            Test.startTest();
            
            Account oAccount = [Select Id, Name, Tenant_Id__c, RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account'];  
            System.assertEquals(oAccount.Tenant_Id__c, '00');
            
            Policy_Member__c oPolMem =  [Select Id,Subscriber__c ,Relationship__c,Policy__r.Group_Name__r.Home_Office_Account__c, Member__c, Policy__c, Policy_Member_External_ID__c , Policy__r.Product__r.ProductCode from Policy_Member__c where Member__c =: oAccount.Id];
            System.assertEquals(oPolMem.Relationship__c, 'Self');
            
            String sAccRecType = oAccount.RecordType.Name;
            System.assertEquals(sAccRecType,'Member');
            
            Test.setCurrentPage(Page.ClaimSummary_VF_HUM_V2);
            ApexPages.currentPage().getParameters().put('Id', oPolMem.Id);
            
            ClaimsSummary_C_HUM_V2 oClaimsSummary = new ClaimsSummary_C_HUM_V2();
            oClaimsSummary.sClaimNbr = '433921091';
            system.assertEquals('433921091',oClaimsSummary.sClaimNbr);
            oClaimsSummary.initiateRequest();
            oClaimsSummary.processResponse();
            oClaimsSummary.bHomeOfcMedDen = true;
            oClaimsSummary.bHomeOficeMessage = false;
            oClaimsSummary.sGCPURL = '';
            oClaimsSummary.sMemId = '';
            oClaimsSummary.sRelation = '';
            oClaimsSummary.iTotalRecords = 2; 
            oClaimsSummary.sClaimsSummary = '';
            oClaimsSummary.claimgotoResponse = null;
            HttpResponse oResponse = new HttpResponse();
            oResponse.setHeader('Content-Type', 'application/json');
            string sjson  = '{"GetClaimsSummary": []}';
            oResponse.setBody(sjson);
            oResponse.setStatusCode(203); 
            oClaimsSummary.responseparse(oResponse); 
            oClaimsSummary.getMemberName(null);
            system.assertEquals(oClaimsSummary.bShowError,false);
            ClaimsSummaryHOCheck_C_HUM_V2 oHOcheck = new ClaimsSummaryHOCheck_C_HUM_V2();
            oHOcheck.isNonHoUser(oCCSUser.id);            
            }
      }
}