/**************************************************************************** 
Apex Class Name  : ClaimsSummary_LT_HUM 
Version          : 1.0  
Created Date     : June 06, 2022
Function         : Test class for ClaimsSummary_LC_HUM
****************************************************************************
Modification Log:
* Developer Name          Date                    Description
*---------------------------------------------------------------------------------------------------
* Anuradha Gajbhe         10/11/2023              Original Version
* Anuradha Gajbhe         02/29/2024              User Story 5289600: Claims Not Visible - INC2619433 and INC2621261
***************************************************************************************************/
@isTest
private Class ClaimsSummary_LT_HUM
{
    /*
     * Method Name : prepareTestData
     * Description : Used to create wrapper class data
     * Return type : void
     * Paramater   : NA
     */
    @testSetup static void prepareTestData() 
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','System Administrator');
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
            
            if(cstGCP!=null){
                string sGCPUrl = cstGCP.End_Point_URL__c;
            }
            if(cstCASWEB!=null){
                string sCASWEBUrl = cstCASWEB.End_Point_URL__c;
            }
            
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
            
            Account objAcc1 = HUMTestDataHelperTest.getAccount(); 
            objAcc1.FirstName = 'Test1';
            objAcc1.LastName = 'Account1';
            objAcc1.Tenant_Id__c = '01';
            objAcc1.Enterprise_ID__c = '1005577525';
            objAcc1.General_Account__c = true;
            objAcc1.Home_Office_Account__c = true;
            objAcc1.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();
            insert objAcc1;
            System.assertEquals(objAcc1.Tenant_Id__c,'01');
            
            Group oGroup1 = new Group(Type='Regular', Name='Test Group');
            insert oGroup1;
            system.assertEquals(oGroup1.Type, 'Regular', 'Value matched');
            
            GroupMember oGroupMember1 = new GroupMember(GroupId = oGroup1.Id, UserOrGroupId = oCCSUser.Id);
            insert oGroupMember1;
            system.assertEquals(oGroupMember1.GroupId, oGroup1.Id, 'Value matched');
            
            Product2 oProd = new Product2(Name = 'MED',Segment_Type__c = 'MCR',ProductCode = 'MED', Major_LOB__c = 'MER');
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
            
            PurchaserPlan policyObj = New PurchaserPlan();
            policyObj.Policy_Group_Number__c='11111';
            policyObj.Name = 'Policy New';
            policyObj.source_cust_cov_key__c = '12345678901234567890' ; 
            policyObj.PayerId = objAcc.Id;
            policyObj.Major_LOB__c ='EM';
            policyObj.Platform__c = 'EM';
            policyObj.ETL_Record_Deleted__c = false;
            policyObj.Client_Number__c = '58';
            policyObj.Home_Office_Account__c = false;
            policyObj.Policy_Group_Number__c = '12345';
            policyObj.Product__c = oProd.Id;
            policyObj.Product__r = oProd;
            insert policyObj;
            
            MemberPlan objPolMem = new MemberPlan();
            objPolMem.Name = 'H5649972800';
            objPolMem.RelationshipToSubscriber = 'Self';
            objPolMem.Relationship_Code__c = '00';
            objPolMem.SubscriberPlanId__c = objPolMem.Id;
            objPolMem.EffectiveFrom = Date.valueOf(Date.today().addDays(-1));
            objPolMem.EffectiveTo = Date.valueOf(Date.today().addDays(1));
            objPolMem.Member_Plan_External_Id__c = '00|ODS|6925004434817|12345678|34567|2013-07-01';
            objPolMem.SubscriberId = objAcc.Id;
            objPolMem.Policy__c = objpol.Id;
            objPolMem.Policy__r = objpol;
            objPolMem.PlanId = policyObj.Id;
            objPolMem.policy_member__c = objPolMem.Id;
            objPolMem.Member_Dependent_Code__c = '03';
            objPolMem.MemberId = objAcc.Id;
            insert objPolMem;
            system.assertEquals(objPolMem.RelationshipToSubscriber, 'Self');
            
            List<HUMConstants__c> listOfConstantToInsert = new List<HUMConstants__c>();
            listOfConstantToInsert.add(new HUMConstants__c(Name = 'APPLAUNCHER_ENVIRONMENT', StringValue__c = 'TEST', IntegerValue__c = 0));
            insert listOfConstantToInsert; 
        
           	list <CRMFunctionality_ONOFF_Switch__c> lstOfSwitches = new list<CRMFunctionality_ONOFF_Switch__c>();
            CRMFunctionality_ONOFF_Switch__c csCRMOnOffSwitch = new  CRMFunctionality_ONOFF_Switch__c(Name = '3038833', IsON__c = True);
            lstOfSwitches.add(csCRMOnOffSwitch);
            insert lstOfSwitches;                        
            System.assertEquals(lstOfSwitches.get(0).Name,'3038833');
            system.assertEquals(true, lstOfSwitches.get(0).IsON__c);
            
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
            
            Account oAccount1 = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account1'];  
            System.assertEquals(oAccount1.Tenant_Id__c, '01');
            
            group grp = [Select id,name from Group where Name = 'Test Group'];
            
            String sAccRecType = oAccount.RecordType.Name;
            System.assertEquals(sAccRecType,'Member');
            
            MemberPlan oPolMem =  [Select Id,Name,MemberId,SubscriberPlanId__c,Relationship_Code__c,RelationshipToSubscriber,Policy__r.Group_Name__r.Home_Office_Account__c,Policy__r.Client_Number__c, SubscriberId, Policy__c, Member_Plan_External_Id__c , Policy__r.Product__r.ProductCode,EffectiveFrom,GroupNumber,EffectiveTo from MemberPlan where SubscriberId =: oAccount.Id];
            System.assertEquals(oPolMem.RelationshipToSubscriber, 'Self');
             
            ClaimsSummary_LC_HUM.lstPolMember.add(oPolMem);
            ApexPages.currentPage().getParameters().put('Id',oPolMem.Id);
            System.assertEquals(ApexPages.currentPage().getParameters().get('Id'),oPolMem.Id);
            ApexPages.currentPage().getParameters().put('Name',oAccount.Name);
            System.assertEquals(ApexPages.currentPage().getParameters().get('Name'),oAccount.Name); 
                       
            String json1 = '{"ClaimSummaryReponse": {"ClaimSummaryResults": {"ClaimSummaryResult":    [{"AdjustmentIndicator": "N",'+
                           '"ClaimNumber": "202103016833136","ClaimTypeCode": "","ClaimType": "Ambulatory","ClaimReceiptDate": "11/22/2016",'+
                           '"ChargeAmount": "1111.86","CopaymentAmount": "0.00","CoInsuranceAmount": "0.00",'+
                           '"DeductibleAmount": "0.00","DisplayGroupId": "","EncounterIndicator": "",'+
                           '"Id": "59b9fb6f2736be02a0ee3445","LineOfBusinessCode": "",'+
                           '"LastProcessDate": "11/26/2016","MemberId": "H5649972800","MemberResponsibilityAmount": "999999999.99",'+
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
            
            String json12 = '{"ClaimSummaryReponse": {"ClaimSummaryResults": {"ClaimSummaryResult":    [{"AdjustmentIndicator": "N",'+
                           '"ClaimNumber": "202103016833136","ClaimTypeCode": "","ClaimType": "Ambulatory","ClaimReceiptDate": "11/22/2016",'+
                           '"ChargeAmount": "1111.86","CopaymentAmount": "0.00","CoInsuranceAmount": "0.00",'+
                           '"DeductibleAmount": "0.00","DisplayGroupId": "","EncounterIndicator": "",'+
                           '"Id": "59b9fb6f2736be02a0ee3445","LineOfBusinessCode": "",'+
                           '"LastProcessDate": "11/26/2016","MemberId": "H566972800","MemberResponsibilityAmount": "999999999.99",'+
                           '"PaidAmount": "0.00","PlatformCode": "LV","PreDeterminationIndicator": "","PreDeterminationNumber": "","ProviderTaxId": "452337175",'+
                           '"SourceNationalProviderId": "1184918864","ServiceEndDate": "05/08/2016","ServiceStartDate": "05/05/2016","StatusDescription": "COMPLETED",'+
                           '"PaidStatusDescription": "Paid"}]},"EndRecordNumber": "1","TotalRecordNumber": "1"}}';
           
            String json14 = '{"ClaimSummaryReponse": {"ClaimSummaryResults": {"ClaimSummaryResult":    [{"AdjustmentIndicator": "N",'+
                           '"ClaimNumber": "202103016833136","ClaimTypeCode": "","ClaimType": "Ambulatory","ClaimReceiptDate": "11/22/2016",'+
                           '"ChargeAmount": "1111.86","CopaymentAmount": "0.00","CoInsuranceAmount": "0.00",'+
                           '"DeductibleAmount": "0.00","DisplayGroupId": "","EncounterIndicator": "",'+
                           '"Id": "59b9fb6f2736be02a0ee3445","LineOfBusinessCode": "",'+
                           '"LastProcessDate": "11/26/2016","MemberId": "","MemberResponsibilityAmount": "999999999.99",'+
                           '"PaidAmount": "0.00","PlatformCode": "LV","PreDeterminationIndicator": "","PreDeterminationNumber": "","ProviderTaxId": "452337175",'+
                           '"SourceNationalProviderId": "1184918864","ServiceEndDate": "05/08/2016","ServiceStartDate": "05/05/2016","StatusDescription": "COMPLETED",'+
                           '"PaidStatusDescription": "Paid"}]},"EndRecordNumber": "1","TotalRecordNumber": "1"}}';
           
            String json15 = '{"ClaimSummaryReponse": {"ClaimSummaryResults": {"ClaimSummaryResult":    [{"AdjustmentIndicator": "N",'+
                           '"ClaimNumber": "202103016833136","ClaimTypeCode": "","ClaimType": "Ambulatory","ClaimReceiptDate": "11/22/2016",'+
                           '"ChargeAmount": "1111.86","CopaymentAmount": "0.00","CoInsuranceAmount": "0.00",'+
                           '"DeductibleAmount": "0.00","DisplayGroupId": "","EncounterIndicator": "",'+
                           '"Id": "59b9fb6f2736be02a0ee3445","LineOfBusinessCode": "",'+
                           '"LastProcessDate": "11/26/2016","MemberId": "H5649972800","MemberResponsibilityAmount": "999999999.99",'+
                           '"PaidAmount": "0.00","PlatformCode": "LV","PreDeterminationIndicator": "","PreDeterminationNumber": "","ProviderTaxId": "452337175",'+
                           '"SourceNationalProviderId": "1184918864","ServiceEndDate": "05/08/2016","ServiceStartDate": "05/05/2016","StatusDescription": "COMPLETED",'+
                           '"PaidStatusDescription": "Paid"},{"AdjustmentIndicator": "N",'+
                           '"ClaimNumber": "202103016833136","ClaimTypeCode": "","ClaimType": "Ambulatory","ClaimReceiptDate": "11/22/2016",'+
                           '"ChargeAmount": "1111.86","CopaymentAmount": "0.00","CoInsuranceAmount": "0.00",'+
                           '"DeductibleAmount": "0.00","DisplayGroupId": "","EncounterIndicator": "",'+
                           '"Id": "59b9fb6f2736be02a0ee3446","LineOfBusinessCode": "",'+
                           '"LastProcessDate": "11/26/2016","MemberId": "H5649972800","MemberResponsibilityAmount": "999999999.99",'+
                           '"PaidAmount": "0.00","PlatformCode": "LV","PreDeterminationIndicator": "","PreDeterminationNumber": "","ProviderTaxId": "452337175",'+
                           '"SourceNationalProviderId": "1184918864","ServiceEndDate": "05/08/2016","ServiceStartDate": "05/05/2016","StatusDescription": "COMPLETED",'+
                           '"PaidStatusDescription": "Paid"}]},"EndRecordNumber": "2","TotalRecordNumber": "2"}}';
            
            String sclaimNumber = '202103016833136';
            
        	Continuation con = (Continuation) ClaimsSummary_LC_HUM.initiateRequest(oPolMem.Id, '1');
            List<string> mystr = new List<string>();
            ClaimsSummary_LC_HUM.processResponse(mystr,'State');
            
        	Continuation con1 = (Continuation) ClaimsSummary_LC_HUM.initiateRequest(null,null);
            HttpResponse response11 = new HttpResponse();
        	response11.setStatusCode(200);
        	response11.setBody(json2);
            
            Test.setContinuationResponse(ClaimsSummary_LC_HUM.sRequest, response11);
            List<String> labels1 = new List<String>{ClaimsSummary_LC_HUM.sRequest};
            String result1 = (String)ClaimsSummary_LC_HUM.processResponse(labels1,'State');
            
            HttpResponse response = new HttpResponse();
        	response.setStatusCode(200);
        	response.setBody(json2);
            
            Test.setContinuationResponse(ClaimsSummary_LC_HUM.sRequest, response);
            List<String> labels = new List<String>{ClaimsSummary_LC_HUM.sRequest};
            
            ClaimsSummary_LC_HUM oClaimsSummary = New ClaimsSummary_LC_HUM();
            
            ClaimsSummary_LC_HUM.sStartCount1='1';
            object polMemId = '{' + oPolMem.Id + '}';
            ClaimsSummary_LC_HUM.initiateRequest(oPolMem.Id, '1');
            Continuation objCon = New Continuation(120);
            objCon.continuationMethod='processResponse';
            System.assertEquals('processResponse', objCon.continuationMethod);
            
            HttpResponse oResponse = New HttpResponse();
            oResponse.setHeader('Content-Type', 'application/json');
            oResponse.setHeader('SOAPAction', '');    
            oResponse.setHeader('Connection','keep-alive');
            oResponse.setBody(json2);
            oResponse.setStatusCode(200); 
            
            Set<String> oSet = new Set<String>();
            oSet.add('H5649972800');
            
            ClaimsSummary_LC_HUM.setPolicyMembers = oSet;
            
            ClaimsSummary_LC_HUM oClaimsSummary1 = New ClaimsSummary_LC_HUM();
            
            ClaimsSummary_LC_HUM.setPolicyMembers = oSet;
            system.assertEquals(oSet,ClaimsSummary_LC_HUM.setPolicyMembers);
            oClaimsSummary1.sClaimSearch = '433921091';
            system.assertEquals('433921091',oClaimsSummary1.sClaimSearch);
            ClaimsSummary_LC_HUM.sClaimNbr = '433921091';
            system.assertEquals('433921091',ClaimsSummary_LC_HUM.sClaimNbr);
            ClaimsSummary_LC_HUM.sClaimNbr = '';
            system.assertEquals('',ClaimsSummary_LC_HUM.sClaimNbr);
            oClaimsSummary1.sClaimSearch = '';
            system.assertEquals('',oClaimsSummary1.sClaimSearch);
            list<String> oPolLst = new list<String>();
            list<String> oClmLst = new list<String>();
            oPolLst.add('H5649972800');
            oClmLst.add('202103016833136');
            ClaimsSummary_LC_HUM.searchClaim(oPolLst,oClmLst);
            list<String> oPolList = new list<String>();
            list<String> oClmList = new list<String>();
            oPolList.add('');
            oClmList.add('');
            ClaimsSummary_LC_HUM.searchClaim(oPolList,oClmList);
            ClaimsSummary_LC_HUM.searchClaim(null,null);
            
            HttpResponse responseGo1 = new HttpResponse();
        	responseGo1.setStatusCode(200);
            string json3 = '';
        	responseGo1.setBody(json3);
            
            Test.setContinuationResponse(ClaimsSummary_LC_HUM.sGoRequest, responseGo1);
            List<String> labelsGo1 = new List<String>{ClaimsSummary_LC_HUM.sGoRequest};
            
            HttpResponse responseGo = new HttpResponse();
        	responseGo.setStatusCode(200);
        	responseGo.setBody(json1);
            
            Test.setContinuationResponse(ClaimsSummary_LC_HUM.sGoRequest, responseGo);
            List<String> labelsGo = new List<String>{ClaimsSummary_LC_HUM.sGoRequest};
                       
            Continuation objCon1 = New Continuation(120);
            objCon1.continuationMethod = 'processGoButtonResponse';
            system.assertEquals('processGoButtonResponse',objCon1.continuationMethod);
            
            HttpResponse oResponse1 = New HttpResponse();
            oResponse1.setHeader('Content-Type', 'application/json');
            oResponse1.setHeader('SOAPAction', '');    
            oResponse1.setHeader('Connection','keep-alive');
                
            oResponse1.setBody(json1);
            oResponse1.setStatusCode(200); 
            Map<String, list<String>> searchData = new Map<String, list<String>>();
            searchData.put('sMemberIds',oPolLst);
            searchData.put('sClaimNumber',oClmLst);
            object state2 = searchData;
            ClaimsSummary_LC_HUM.responseparseGoButton(oResponse1,state2);
            
            ClaimsSummary_LS_HUM oClaimLS = new ClaimsSummary_LS_HUM();
            oClaimLS.noOnClickGO(json1);
            oClaimLS.buildRequest(oPolMem.Id,'1',oSet);
            oClaimLS.convertStringToDate('10/10/2023');
            oClaimLS.convertStringToDate('10/10');
            
            ClaimsSummary_LC_HUM.responseparse(oResponse1,polMemId);
            oResponse1.setBody(json12);
            ClaimsSummary_LC_HUM.responseparseGoButton(oResponse1,state2);
            oResponse1.setBody(json15);
            ClaimsSummary_LC_HUM.responseparseGoButton(oResponse1,state2);
            string sjson13 = '{"ClaimSummaryReponse":{"ClaimSummaryResults": {"ClaimSummaryResult":    []},"EndRecordNumber": "0","TotalRecordNumber": "0"}}';
            oResponse1.setBody(sjson13);
            ClaimsSummary_LC_HUM.responseparseGoButton(oResponse1,state2);
            oResponse1.setBody(json14);
            ClaimsSummary_LC_HUM.responseparseGoButton(oResponse1,state2);
            
            oResponse1.setStatusCode(800); 
            ClaimsSummary_LC_HUM.responseparseGoButton(oResponse1,polMemId);
            ClaimsSummary_LC_HUM.responseparse(oResponse1,polMemId);
            
            ClaimsSummary_LC_HUM.initiateRequest(null, '1');
            object polMemId1 = '{' + '}';
            List<ClaimsSummary_Wrapper_LDTO_HUM> templstClaimResponse1 = new List<ClaimsSummary_Wrapper_LDTO_HUM>();
            List<String> newlabels = new List<String>();
            
            HttpResponse oResponse3 = new HttpResponse();
            oResponse3.setHeader('Content-Type', 'application/json');
            string sjson  = '{"GetClaimsSummary": []}';
            oResponse3.setBody(sjson);
            oResponse3.setStatusCode(200);
            ClaimsSummary_LC_HUM.responseparse(oResponse3,polMemId);
            ClaimsSummary_LC_HUM.responseparseGoButton(oResponse3,state2);
            
            object state = '{' + oPolMem.Id + '}';
            oResponse1.setStatusCode(200); 
            string sResponseGo ='test';
            ClaimsSummary_LC_HUM.processGoButtonResponse(labelsGo1,state);
            
            List<MemberPlan> lstMemParameters = new List<MemberPlan>();
            lstMemParameters.add(oPolMem);
            List<ClaimsSummary_Wrapper_LDTO_HUM> lstWrapperDentalClaims = new List<ClaimsSummary_Wrapper_LDTO_HUM> ();
            
            ClaimsSummary_Wrapper_LDTO_HUM objectWrapper1 = new ClaimsSummary_Wrapper_LDTO_HUM();
            objectWrapper1.sAdjustInd = 'N';
            objectWrapper1.sClaimNbr = '202103016833136';           
            objectWrapper1.sClaimType = 'Ambulatory';                    
            objectWrapper1.sClaimTypeCd = 'A'; 
            objectWrapper1.sClmGenKey = '123' ;
            objectWrapper1.sClmReceiptDate = '11/22/2016';
            objectWrapper1.sChargeAmt = '$' + '1111';
            objectWrapper1.sDispGrpID = '';
            objectWrapper1.sLOBCd = '';
            objectWrapper1.sClmDetailLink ='';
            objectWrapper1.sLastProcessDate = '11/26/2016';
            objectWrapper1.sMbrRespAmt = '$' + '999999999';
            objectWrapper1.sPaidAmt = '$' + '0.0';
            objectWrapper1.sPlatformCd = 'LV';
            objectWrapper1.sPreDeterminationIndicator = '';
            objectWrapper1.sPreDeterminationNumber = '';
            objectWrapper1.sProviderName = 'Test'; 
            objectWrapper1.sProviderID = '452337175';
            objectWrapper1.sSRCNPIID = '1184918864';
            objectWrapper1.sServiceEndDate = '05/08/2016';
            objectWrapper1.sServiceStartDate ='05/05/2016';
            objectWrapper1.sStatusDesc = 'COMPLETED';
            objectWrapper1.sDeductAmt= '0.0';
            objectWrapper1.sCopayAmt = '';
            objectWrapper1.sCoInsAmt= '';
            objectWrapper1.sMemberId = 'H56499728-00';            
            objectWrapper1.sEndRecordNumber= 1;
            objectWrapper1.sTotalRecordNumber = 1; 
            lstWrapperDentalClaims.add(objectwrapper1);
            
            oClaimLS.updateGroupNumber(lstMemParameters, lstWrapperDentalClaims);
            ClaimsSummary_LC_HUM.formDropdown(lstWrapperDentalClaims);
            ClaimsSecurity_LH_HUM oClaimSec = new ClaimsSecurity_LH_HUM();
            oClaimSec.isMemberHaveHomeOfficePolicies(oPolMem.Id);
            oClaimSec.getMemberAccountID(oPolMem.Id);
            
            ClaimSecurityDataLogic_LD_HUM.getPolicyMemberDetails(oPolMem.MemberId);
            ClaimSecurityDataLogic_LD_HUM.getPublicGroupID('');
            ClaimSecurityDataLogic_LD_HUM.isHomeOfficeMember(oAccount.Id);
            ClaimSecurityDataLogic_LD_HUM.isHomeOfficeMember(oAccount1.Id);
            ClaimSecurityDataLogic_LD_HUM.getGroupMemberIds(grp.id,'');
            
            ClaimsSummary_LD_HUM.requestGroupPlan(oPolMem.Id);
            ClaimsSummary_LD_HUM.requestPoliciesForGroup(oAccount.Id);
            ClaimsSummary_LD_HUM.getLVCASTranslationDetails('');
            ClaimsSummary_LD_HUM.getMemberIDObjDetails(oPolMem.MemberId);
            oPolMem.subscriberid = null;
            update oPolMem;
            ClaimsSummary_LD_HUM.getMemberIDObjDetails(oPolMem.MemberId);
            ClaimsSummary_LD_HUM.getPolicyMemberDetail('');
            
            Test.stopTest();  
        }
    }
     /*
     * Method Name : claimSummaryException
     * Description : This method tests the Exception handling 
     * Return type : void
     * Parameter   : None
     */ 
    static testMethod void claimSummaryException() 
    {
        User oCCSUser = [Select id, UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        {
            Test.startTest();
            
            Account oAccount = [Select Id, Name, Tenant_Id__c, RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account'];  
            System.assertEquals(oAccount.Tenant_Id__c, '00');
            
            MemberPlan oPolMem =  [Select Id,SubscriberPlanId__c ,RelationshipToSubscriber,Policy__r.Group_Name__r.Home_Office_Account__c, SubscriberId, Policy__c, Member_Plan_External_Id__c , Policy__r.Product__r.ProductCode from MemberPlan where SubscriberId =: oAccount.Id];
            System.assertEquals(oPolMem.RelationshipToSubscriber , 'Self');
            
            String sAccRecType = oAccount.RecordType.Name;
            System.assertEquals(sAccRecType,'Member');
            
            ApexPages.currentPage().getParameters().put('Id', oPolMem.Id);
            object polMemId = '{' + '}';
            ClaimsSummary_LC_HUM oClaimsSummary = new ClaimsSummary_LC_HUM();
            ClaimsSummary_LC_HUM.sClaimNbr = '433921091';
            system.assertEquals('433921091',ClaimsSummary_LC_HUM.sClaimNbr);
            ClaimsSummary_LC_HUM.initiateRequest(oPolMem.Id, '1');
            ClaimsSummary_LC_HUM.bHomeOfcMedDen = true;
            ClaimsSummary_LC_HUM.bHomeOficeMessage = false;
            oClaimsSummary.sGCPURL = '';
            oClaimsSummary.sMemId = '';
            oClaimsSummary.sRelation = '';
            ClaimsSummary_LC_HUM.iTotalRecords = 2; 
            ClaimsSummary_LC_HUM.sClaimsSummary = '';
            ClaimsSummary_LC_HUM.claimgotoResponse = null;
            HttpResponse oResponse = new HttpResponse();
            oResponse.setHeader('Content-Type', 'application/json');
            string sjson  = '{"GetClaimsSummary": []}';
            oResponse.setBody(sjson);
            oResponse.setStatusCode(203);
            object state = '{' + oPolMem.Id + '}';
            ClaimsSummary_LC_HUM.responseparse(oResponse,polMemId);
            ClaimsSummary_LC_HUM.responseparseGoButton(oResponse,polMemId);
            
            ClaimsSummary_LC_HUM.processGoButtonResponse(null,null);
            ClaimsSummary_LC_HUM.getMemberName(null);
            ClaimsSummary_LC_HUM oClaimsSummary2 = new ClaimsSummary_LC_HUM();
            ClaimsSummaryHOCheck_LC_HUM oHOcheck = new ClaimsSummaryHOCheck_LC_HUM();
            oHOcheck.isNonHoUser(oCCSUser.id);  
            ClaimsSecurity_LH_HUM oClaimSec = new ClaimsSecurity_LH_HUM();
            oClaimSec.isMemberHaveHomeOfficePolicies(null);
            oClaimSec.getMemberAccountID(null);
            oClaimSec.getPolicyMemberID(null);
            oClaimSec.getCCSUserAccessPolicyNumbers(null,'');
            ClaimSecurityDataLogic_LD_HUM.isHomeOfficeMember(null);
            ID ccsUserID;
            ClaimSecurityDataLogic_LD_HUM.isHomeOfficeMember(ccsUserID);
            Set<ID> allGroupID = new Set<ID>();
            ClaimSecurityDataLogic_LD_HUM.getUserAccessDetails(ccsUserID , allGroupID);
            ClaimsSummary_LD_HUM.getMemberIDObjDetails(oPolMem.Id);
            ClaimsSummary_LD_HUM.getPolicyMemberDetail(null);
            
			ClaimsSummary_LS_HUM oClaimLs = new ClaimsSummary_LS_HUM();           
            oClaimLs.buildRequestGoButton('');
            oClaimLs.buildRequest(null,null,null);
            Set<String> oSet1 = new Set<String>();
            oSet1.add('5');
            OclaimLS.buildRequest(oPolMem.Id,'',oSet1);
            oClaimLs.noOnClickGO(sjson);
            HttpRequest requestGo = new HttpRequest();
            HUM_Webservice_Callout__c oSvcHeaderGoClaims = HUM_Webservice_Callout__c.getValues('ClaimsService_GetClaimsSummary_V3'); 
            oClaimLs.setupGoButtonRequest(requestGo, oSvcHeaderGoClaims, '');
            oClaimLs.updateClaimDetailLinkURL(null);
            oClaimLs.updateGroupNumber(null,null);
            oClaimsSummary.infoSet();
            ClaimsSummary_LC_HUM.bCoveoUser =false;
            ClaimsSummary_LC_HUM.vMemberAccountID = oAccount.Id;
            ClaimsSummary_LC_HUM.sInfoMsg ='';
            oClaimsSummary.sFirstName ='';
            oClaimsSummary.sClientNumber ='';
            
            HttpResponse responseExp = new HttpResponse();
        	responseExp.setStatusCode(2000);
        	responseExp.setBody('');
            
            Test.setContinuationResponse(ClaimsSummary_LC_HUM.sRequest, responseExp);
            List<String> labels11 = new List<String>{ClaimsSummary_LC_HUM.sRequest};
            String result11 = (String)ClaimsSummary_LC_HUM.processResponse(labels11,'State');
            
        }
    }
}