/***************************************************************************************************************** 
Apex Class Name  : MemberAncillary_S_T_HUM_V2
Version          : 1.0  
Created Date     : 05/31/2019
Function         : Test Class for MemberAncillary_S_HUM_V2

Modification Log:
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------  
 * Manikanta                       356372                 05/31/2019          Original Version - REQ - 379006 Ancillary service test methods
 * Manikanta                                             06/07/2019          REQ - 379006 Made code changes to align with the DP changes
 *   Yogesh Gupta                                         11/04/2019         V2 Version - REQ-403709 Test class for Ancillary Service for Medicaid Id
 *   Yogesh Gupta										  11/27/2019		 Defect - 450407 Response structure changes for State KY
 ******************************************************************************************************************************/
@isTest
private class MemberAncillary_S_T_HUM_V2
{
    /*
     * Method Name : dataSetup
     * Description : Test method to setup common test data for all test methods
     * Return type : void
     * Paramater   : NA
     */
    @testSetup static void dataSetup()
    {
        Account objAcc = HUMTestDataHelperTest.getAccount(); 
        objAcc.FirstName ='Test';
        objAcc.LastName = 'Account';
        objAcc.Tenant_Id__c = '00';
        objAcc.Enterprise_ID__c = '1005577525';
        objAcc.General_Account__c = true;
        objAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();
        insert objAcc;
        System.assertEquals(objAcc.Tenant_Id__c,'00');
        System.assertEquals(objAcc.Enterprise_ID__c,'1005577525');
        System.assertEquals(objAcc.LastName,'Account');
        
        Product2 oProd = new Product2(Name = 'Laptop X200',Segment_Type__c='MCR',ProductCode='MED', Major_LOB__c='MER', Sold_Product_Key_Value__c = '7333340101');
        insert oProd;
        System.assertNotEquals(oProd.Id, NULL, 'Value not matched');
          
        Policy__c objpol = HUMTestDataHelperTest.getPolicy();
        objpol.Name='testPolicy';
        objpol.Group_Name__c = objAcc.id;
        objpol.Major_LOB__c = 'MER';
        objpol.Source_Cust_Cov_Key__c = '123456789';
        objpol.Product__c = oProd.Id;
        objpol.Product__r = oProd;
        objpol.Platform__c = 'LV';
        
        System.assertEquals(objpol.Source_Cust_Cov_Key__c,'123456789');
        System.assertEquals(objpol.Group_Name__c,objAcc.Id);
        System.assertEquals(objpol.Name,'testPolicy'); 
        
        insert objpol;
        System.assert(objpol.Name!=null);
    
        Policy_Member__c objPolMem = new Policy_Member__c();
        objPolMem.Name = 'Policy Member PM1';
        objPolMem.Relationship__c = 'Self';
        objPolMem.Subscriber__c = objPolMem.Id;
        objPolMem.Effective_Date__c='01/01/2019';
        objPolMem.End_Date__c='11/16/2016';
        objPolMem.Policy_Member_External_ID__c='00|ODS|6925004434817|12345678|34567|2013-07-01';
        objPolMem.Member__c = objAcc.Id;
        objPolMem.Policy__c = objpol.Id;
        objPolMem.Policy__r = objpol;
        
        System.assertEquals(objPolMem.Relationship__c, 'Self');
        System.assertEquals(objPolMem.Name, 'Policy Member PM1');
        System.assertEquals(objPolMem.Policy_Member_External_ID__c, '00|ODS|6925004434817|12345678|34567|2013-07-01');
        
        insert objPolMem;       
        System.assert(objPolMem.Name!=null);
        
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        System.assertEquals(oUser.LastName, 'test User1');
        
        HUM_Webservice_Callout__c oGetMemberAncillary = new HUM_Webservice_Callout__c(Request_Type__c = 'POST', Name = 'GetMemberAncillary',
        End_Point_URL__c = 'https://testst-cmservicews.hum.com', Certificate_Name__c = 'testts', Timeout__c = '12000',
        Content_Type__c = 'application/json', Active__c = true);
        insert oGetMemberAncillary;
        System.assert(oGetMemberAncillary.id!=null);
        System.assert(oGetMemberAncillary.name == 'GetMemberAncillary');
        
    }
   
    /*
    * Method Name : TestassignValuesToAncillaryReqLA
    * Description : Used to test assignValuesToAncillaryReq method
    * Return type : void
    * Paramater   : NA
    */
    testMethod static void TestassignValuesToAncillaryReqLA()
    {
         Policy__c policy = [select id from policy__c where name = 'testPolicy'];
         policy.Issue_State__c  = 'LA';
         update policy;
         policy_member__c oPolMember = [select id, policy__c, Policy__r.Client_Number__c, Group_Number__c, Effective_Date__c, Policy_Platform__c, Member__r.Name, Policy__r.Product__r.Sold_Product_Key_Value__c, Policy__r.Group_Name__r.Source_Customer_Key__c, Product_Type__c,Issue_State__c from policy_member__c where Name = 'Policy Member PM1' limit 1];
         oPolMember.policy__c = policy.Id;
         update oPolMember;
         HUM_Webservice_Callout__c oSvcAncillary1 = new HUM_Webservice_Callout__c();
         oSvcAncillary1.Name = 'GetMemberAncillary';
         oSvcAncillary1.Active__c = true;
         oSvcAncillary1.Request_Type__c = 'POST';
         oSvcAncillary1.End_Point_URL__c = 'https://dev-crmservicews.humana.com/AncillaryServiceREST_V2/getMemberAncillary';
         oSvcAncillary1.Timeout__c = '12000';
         oSvcAncillary1.Certificate_Name__c = 'Humana_GuidanceCare_Hoth';
         insert oSvcAncillary1;
         HUM_Webservice_Callout__c oServiceCallout = [select Name, Certificate_Name__c, End_Point_URL__c, Timeout__c, Content_Type__c, Request_Type__c from HUM_Webservice_Callout__c where name='GetMemberAncillary' limit 1];   
         String sMemIdBase = 'H78898282';
         String sDependentCode = '00';     
         User oUser = [SELECT Id, LastName FROM User  WHERE Lastname ='test User1' LIMIT 1];
         System.assertEquals('test User1',oUser.LastName);
         System.runAs(oUser)
         {
             Test.startTest();
             MemberAncillary_S_HUM_V2 oMemberAncillary = new MemberAncillary_S_HUM_V2();
             MemberAncillaryRequest_H_HUM_V2 oMemberAncillaryReq = new MemberAncillaryRequest_H_HUM_V2();
             oMemberAncillaryReq = oMemberAncillary.assignValuesToAncillaryReq(oPolMember, sMemIdBase, sDependentCode);
             System.assert(oMemberAncillaryReq!=null);
             Test.stopTest();
         }
    }
    
     /*
    * Method Name : TestassignValuesToAncillaryReqKY
    * Description : Used to test assignValuesToAncillaryReq method
    * Return type : void
    * Paramater   : NA
    */
    testMethod static void TestassignValuesToAncillaryReqKY()
    {
         Policy__c policy = [select id from policy__c where name = 'testPolicy'];
         policy.Issue_State__c  = 'KY';
         update policy;
         policy_member__c oPolMember = [select id, policy__c, Policy__r.Client_Number__c, Group_Number__c, Effective_Date__c, Policy_Platform__c, Member__r.Name, Policy__r.Product__r.Sold_Product_Key_Value__c, Policy__r.Group_Name__r.Source_Customer_Key__c, Product_Type__c,Issue_State__c from policy_member__c where Name = 'Policy Member PM1' limit 1];
         oPolMember.policy__c = policy.Id;
         update oPolMember;
         HUM_Webservice_Callout__c oSvcAncillary1 = new HUM_Webservice_Callout__c();
         oSvcAncillary1.Name = 'GetMemberAncillary';
         oSvcAncillary1.Active__c = true;
         oSvcAncillary1.Request_Type__c = 'POST';
         oSvcAncillary1.End_Point_URL__c = 'https://dev-crmservicews.humana.com/AncillaryServiceREST_V2/getMemberAncillary';
         oSvcAncillary1.Timeout__c = '12000';
         oSvcAncillary1.Certificate_Name__c = 'Humana_GuidanceCare_Hoth';
         insert oSvcAncillary1;
         HUM_Webservice_Callout__c oServiceCallout = [select Name, Certificate_Name__c, End_Point_URL__c, Timeout__c, Content_Type__c, Request_Type__c from HUM_Webservice_Callout__c where name='GetMemberAncillary' limit 1];   
         String sMemIdBase = 'H78898282';
         String sDependentCode = '00';     
         User oUser = [SELECT Id, LastName FROM User  WHERE Lastname ='test User1' LIMIT 1];
         System.assertEquals('test User1',oUser.LastName);
         System.runAs(oUser)
         {
             Test.startTest();
             MemberAncillary_S_HUM_V2 oMemberAncillary = new MemberAncillary_S_HUM_V2();
             MemberAncillaryRequest_H_HUM_V2 oMemberAncillaryReq = new MemberAncillaryRequest_H_HUM_V2();
             oMemberAncillaryReq = oMemberAncillary.assignValuesToAncillaryReq(oPolMember, sMemIdBase, sDependentCode);
             System.assert(oMemberAncillaryReq!=null);
             Test.stopTest();
         }
    }
    
    /*
    * Method Name : TestSetupAncillaryRequest
    * Description : Used to test setupAncillaryRequest method
    * Return type : void
    * Paramater   : NA
    */  
    testMethod static void TestSetupAncillaryRequest()
    {
        User oUser = [Select Id,lastname, Current_Queue__c from User where LastName = 'test User1'];
        System.assertEquals('test User1',oUser.LastName);
        HUM_Webservice_Callout__c oSvcAncillary1 = new HUM_Webservice_Callout__c();
        oSvcAncillary1.Name = 'GetMemberAncillary';
        oSvcAncillary1.Active__c = true;
        oSvcAncillary1.Request_Type__c = 'POST';
        oSvcAncillary1.End_Point_URL__c = 'https://dev-crmservicews.humana.com/AncillaryServiceREST_V2/getMemberAncillary';
        oSvcAncillary1.Timeout__c = '12000';
        oSvcAncillary1.Certificate_Name__c = 'Humana_GuidanceCare_Hoth';
        insert oSvcAncillary1;
         System.runAs(oUser)
         {
              Test.startTest();
              MemberAncillary_S_HUM_V2 oMemberAncillary = new MemberAncillary_S_HUM_V2();
              HttpRequest oRequest = new HttpRequest();
              HUM_Webservice_Callout__c oSvcAncillary = [Select Request_Type__c,End_Point_URL__c,Certificate_Name__c,Timeout__c,Content_Type__c From HUM_Webservice_Callout__c Limit 1];
              String sRequestBody = '{"getMemberAncillary":{"ancillaryRequest": {"includes":[{"ICostShareInformation": true,"ICommunityEngagementExemptCode": true,"IDeductibleAmount": true,"ITypeOfAssistance": true,"IPremium": true,"IMedicallyFrail": true,"IRewards": true,"IJuvenilleJustice": true,"IPenaltyReasonIndicator": true,"ISuspensionReasonIndicator": true,"IHospiceIndicator": true,"IFosterCareIndicator": true,"IGuardianshipIndicator": true,"IHeadOfHousehold": true,"ILTSS": true,"IBehavioralHealthCode": false,"ICapitationCode": false,"ICaseTypeCode": false,"IChishlmCase": false,"IChoiceCode": false,"IClosureCode": false,"ICoverageEndDate": false,"IDepartmentOfCorrection": false,"IHighRiskPregancyCode": false,"IMedicalAssistanceCategoryCode" : false,"IMultipleBirthIndicator": false, "INewBornMothersReferenceId": false,"IRenewalCode": false,"IRenewalDate": false,"ICoordinatedSystemOfCare ": false}],"Criteria": {"SequenceId": 1,"memberIdentifier": {"MemberIdBase": "H78898282","DependentCode": "00","PlatformCode": "LV","CoverageStartDate": "2019-03-01","GroupId": "0Y3521","DivisionId": "0Y352102","ProductId": "7592490201"},"StateCode": "KY" }}}}';
              oRequest = oMemberAncillary.setupAncillaryRequest(oRequest, oSvcAncillary, sRequestBody);
              System.assert(oRequest!=null);
              Test.stopTest();  
         }
    }
   
    /*
    * Method Name : TestProcessResponseKY
    * Description : Used to test processResponse method
    * Return type : void
    * Paramater   : NA
    */    
    testMethod static void TestProcessResponseKY()
    {
         User oUser = [Select Id,lastname, Current_Queue__c from User where LastName = 'test User1'];
         System.assertEquals('test User1',oUser.LastName);
         Policy__c policy = [select id from policy__c where name = 'testPolicy'];
         policy.Issue_State__c  = 'KY';
         update policy;
         policy_member__c policyObj1 = [select id, policy__c, Policy__r.Client_Number__c, Group_Number__c, Effective_Date__c, Policy_Platform__c, Member__r.Name, Policy__r.Product__r.Sold_Product_Key_Value__c, Policy__r.Group_Name__r.Source_Customer_Key__c, Product_Type__c,Issue_State__c from policy_member__c where Name = 'Policy Member PM1' limit 1];
         policyObj1.policy__c = policy.Id;
         update policyObj1;
         System.runAs(oUser)
         {
              Test.startTest();
              MemberAncillary_S_HUM_V2 oMemberAncillary = new MemberAncillary_S_HUM_V2();
              MemberAncillaryResponse_DTO_HUM_V2 ancillaryInfo = new MemberAncillaryResponse_DTO_HUM_V2 ();
              //Defect - 450407 Response structure changes for State KY
			  String sjson = '{ "getMemberAncillaryResponse":{ "responseHeader":{ "environment":"INT", "context":{ "clientSessionId":"G0x7f45c3d28c08-1D", "server":"LOUWEBWTL25S01", "transactionId":"5bab0fa4-d753-4e1c-8df0-1b06f4d6621e" }, "metrics":{ "RequestTimeStamp":"2019-11-27T08:18:03.4", "ResponseTimeStamp":"2019-11-27T08:18:03.4", "duration":2.0 } }, "responseStatus":{ "status":"SUCCESS", "code":"MASI0001", "description":"Member Ancillary details returned successfully", "requestMessageList":[ ] }, "AncillaryEntityList":[ { "LTSS":{ "otherHealthIndicators":"", "specialCond":"", "ltcProgramRecommendation":"", "prodPlanId":"", "providerCode":"", "levelOfCareDate":"", "levelOfCareCode":"", "specialNeedsNote":"", "medicaidAidCategoryCode":"", "medicaidBeginDate":"", "medicaidEndDate":"", "ACWM":"" }, "HeadOfHousehold":{ "FirstName":"RACHEL", "LastName":"DDROP", "postalAddress":{ "addressLine1":"112 OSBOURNE WAY", "addressLine2":"", "city":"GEORGETOWN", "zip":"40324", "state":"21" } }, "CostShareInformation":"191Y", "CommunityEngagementExemptCode":"W", "DeductibleAmount":21.0, "TypeOfAssistance":"ADLT", "JuvenilleJustice":"N", "FosterCareIndicator":"Y", "GuardianshipIndicator":"N", "HospiceIndicator":"N", "MedicallyFrail":"A1", "PenaltyReasonIndicator":{ "penaltyValue":"CP", "penaltyStartDate":"02/02/2019", "penaltyEndDate":"12/31/9999" }, "Premium":{ "premiumValue":"11.00", "premiumStartDate":"06/01/2019", "premiumEndDate":"12/31/9999" }, "Rewards":"A", "SuspensionReasonIndicator":"CS" } ] } }';
              ancillaryInfo = oMemberAncillary.processResponse(sjson);      
              System.assert(ancillaryInfo!=null);
              Test.stopTest();
         }
    }
   
    /*
    * Method Name : TestSetupAncillaryNegative
    * Description : Used to test setupAncillaryRequest method negative scenario
    * Return type : void
    * Paramater   : NA
    */  
    testMethod static void TestSetupAncillaryNegative()
    {
         User oUser = [Select Id,lastname, Current_Queue__c from User where LastName = 'test User1'];
         System.assertEquals('test User1',oUser.LastName);
         System.runAs(oUser)
         {
              Test.startTest();
              MemberAncillary_S_HUM_V2 oMemberAncillary1 = new MemberAncillary_S_HUM_V2();
              HttpRequest oRequest1;
              HUM_Webservice_Callout__c oSvcAncillary1;
              String sRequestBody1;
              oRequest1 = oMemberAncillary1.setupAncillaryRequest(oRequest1, oSvcAncillary1, sRequestBody1);
              System.assert(oRequest1==null); 
              Test.stopTest();             
         }
    }
    
    /*
    * Method Name : testProcessResponseLA
    * Description : Used to test processResponse method
    * Return type : void
    * Paramater   : NA
    */    
    testMethod static void TestProcessResponseLA()
    {
         User oUser = [Select Id,lastname, Current_Queue__c from User where LastName = 'test User1'];
         System.assertEquals('test User1',oUser.LastName);
         Policy__c policy = [select id from policy__c where name = 'testPolicy'];
         policy.Issue_State__c  = 'LA';
         update policy;
         policy_member__c policyObj1 = [select id, policy__c, Policy__r.Client_Number__c, Group_Number__c, Effective_Date__c, Policy_Platform__c, Member__r.Name, Policy__r.Product__r.Sold_Product_Key_Value__c, Policy__r.Group_Name__r.Source_Customer_Key__c, Product_Type__c,Issue_State__c from policy_member__c where Name = 'Policy Member PM1' limit 1];
         policyObj1.policy__c = policy.Id;
         update policyObj1;
         System.runAs(oUser)
         {
              Test.startTest();
              MemberAncillary_S_HUM_V2 oMemberAncillary = new MemberAncillary_S_HUM_V2();
              MemberAncillaryResponse_DTO_HUM_V2 ancillaryInfo = new MemberAncillaryResponse_DTO_HUM_V2();
              HttpResponse resBilling = new Httpresponse(); 
              String sjson = '{"getMemberAncillaryResponse":{ "AncillaryEntityList":[ { "MaintenanceReason":"MaintenanceReason", "HandicapIndicator":"Y", "CoverageEndDate":"2021-05-30", "ChishlmCase":{ "startDate":"2021-05-16", "endDate":"2021-06-16", "manager":"Louxi52di01" }, "CapitationCode":"CapitationCode", "ChoiceCode":"ChoiceCode", "HighRiskPregancyCode":"HighRiskPregancyCode", "BehavioralHealthCode":"BehavioralHealthCode", "ClosureCode":"ClosureCode", "RenewalDate":"05/16/2021 3:41:19 PM", "RenewalCode":"RenewalCode", "MultipleBirthIndicator":"MultipleBirthIndicator", "MedicalAssistanceCategoryCode":"MedicalAssistanceCategoryCode", "CoordinatedSystemOfCare":{ "admitDate":"2021-05-16", "dischargeDate":"2021-05-18" }, "NewBornMothersReferenceId":"NewBornMothersReferenceId", "CaseTypeCode":"CaseTypeCode", "DepartmentOfCorrection":{ "type":"type", "startDate":"2023-05-17", "endDate":"2021-05-16", "preReleaseDate":"2020-05-16", "locationIndicator":"Y" } } ] } }'; 
              resBilling.setBody(sjson );
              resBilling.setStatusCode(200);
              system.assert(resBilling.getBody() == sjson);
              MemberAncillaryResponse_H_HUM_V2 objResponseDTO = new MemberAncillaryResponse_H_HUM_V2();
              objResponseDTO=(MemberAncillaryResponse_H_HUM_V2)System.JSON.deserialize(resBilling.getbody(), MemberAncillaryResponse_H_HUM_V2.class); 
              ancillaryInfo = oMemberAncillary.processResponse(sjson);   
              System.assert(ancillaryInfo!=null);
              Test.stopTest();
         }
    }
    
        /*
    * Method Name : testAncillaryResponseDTO
    * Description : Test method for MemberAncillaryResponse_DTO_T_HUM_V2 
    * Return type : void
    * Paramater   : NA
    */
    static testMethod void testAncillaryResponseDTO()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        System.runAs(oUser)
        {
            Test.startTest();
            
            MemberAncillaryResponse_DTO_HUM_V2 oRequest = new MemberAncillaryResponse_DTO_HUM_V2 ();
            oRequest.CostShareInformation = 'CostShareInformation';
            oRequest.CommunityEngagementExemptCode = 'CommunityEngagementExemptCode';
            oRequest.TypeOfAssistance = 'TypeOfAssistance';
            oRequest.DeductibleAmount = 3.14;
            oRequest.MemberIdBase = 'MemberIdBase';
            oRequest.DependentCode = 'DependentCode';
            oRequest.PlatformCode = 'PlatformCode';
            oRequest.CoverageStartDate = datetime.parse('01/01/2019 10:36 AM'); 
            oRequest.GroupId = 'GroupId';
            oRequest.DivisionId = 'DivisionId';
            oRequest.ProductId = 'ProductId';
            oRequest.Type = 'Type';
            oRequest.premiumValue = 'premiumValue';
            oRequest.premiumStartDate = 'premiumStartDate';
            oRequest.premiumEndDate = 'premiumEndDate';
            oRequest.penaltyValue = 'penaltyValue';
            oRequest.penaltyStartDate = 'penaltyStartDate';
            oRequest.penaltyEndDate = 'penaltyEndDate';
            oRequest.suspensionValue = 'suspensionValue';
            oRequest.rewardsValue = 'rewardsValue';
            oRequest.FirstName = 'FirstName';
            oRequest.LastName = 'LastName';
            oRequest.fullName = oRequest.FirstName+oRequest.LastName;
            oRequest.AddressLine1 = 'AddressLine1';
            oRequest.AddressLine2 = 'AddressLine2';
            oRequest.City = 'City';
            oRequest.State = 'State';
            oRequest.Zip = 'Zip';
            oRequest.fullAddress = oRequest.AddressLine1+oRequest.AddressLine2+oRequest.City+oRequest.State+oRequest.Zip;
            oRequest.OtherHealthIndicators = 'OtherHealthIndicators';
            oRequest.SpecialCond = 'SpecialCond';
            oRequest.LtcProgramRecommendation = 'LtcProgramRecommendation';
            oRequest.ProdPlanId = 'ProdPlanId';
            oRequest.ProviderCode = 'ProviderCode';
            oRequest.LevelOfCareDate = '01/01/2019'; //DateTime.parse('01/01/2019 11:46 AM');
            oRequest.LevelOfCareCode = 'LevelOfCareCode';
            oRequest.SpecialNeedsNote = 'SpecialNeedsNote';
            oRequest.MedicaidAidCategoryCode = 'MedicaidAidCategoryCode';
            oRequest.MedicaidBeginDate = '02/01/2019'; //DateTime.parse('01/01/2019 11:46 AM');
            oRequest.MedicaidEndDate = '03/01/2019'; //DateTime.parse('01/01/2019 11:46 AM');
            oRequest.ACWM = 'ACWM';
            oRequest.Manager = 'Manager';
            oRequest.Preminum = 'Preminum';
            oRequest.MedicallyFrail = 'MedicallyFrail';
            oRequest.Rewards = 'Rewards';
            oRequest.JuvenilleJustice = 'JuvenilleJustice';
            oRequest.PenaltyReasonIndicator = 'PenaltyReasonIndicator';
            oRequest.SuspensionReasonIndicator = 'SuspensionReasonIndicator';
            oRequest.HospiceIndicator = 'HospiceIndicator';
            oRequest.ForesterCareIndicator = 'ForesterCareIndicator';
            oRequest.GuardianshipIndicator = 'GuardianshipIndicator';
            oRequest.CoverageEndDate = 'CoverageEndDate';
            oRequest.CapitationCode = 'CapitationCode';
            oRequest.ChoiceCode = 'ChoiceCode';
            oRequest.HighRiskPregancyCode = 'HighRiskPregancyCode';
            oRequest.BehavioralHealthCode = 'BehavioralHealthCode';
            oRequest.ClosureCode = 'ClosureCodeClosureCode';
            oRequest.RenewalCode = 'RenewalCode';
            oRequest.MultipleBirthIndicator = 'MultipleBirthIndicator';
            oRequest.MedicalAssistanceCategoryCode = 'MedicalAssistanceCategoryCode';
            oRequest.NewBornMothersReferenceId = 'NewBornMothersReferenceId';
            oRequest.CaseTypeCode = 'CaseTypeCode';
            oRequest.AdmitDate = 'AdmitDate';
            oRequest.dischargeDate = 'dischargeDate';
            oRequest.StateCode = 'StateCode';
            oRequest.StartDate = 'StartDate';
            oRequest.EndDate = 'EndDate';
            oRequest.Doctype = 'Doctype';
            oRequest.DocstartDate = 'DocstartDate';
            oRequest.DocendDate = 'DocendDate';
            oRequest.DocpreReleaseDate = 'DocpreReleaseDate';
            oRequest.DoclocationIndicator = 'DoclocationIndicator';
            oRequest.DocstateCode = 'DocstateCode';
            oRequest.RenewalDate = 'RenewalDate';
            oRequest.MaintenanceReason = 'MaintenanceReason';
            oRequest.HandicapIndicator = 'HandicapIndicator';
            System.assert(oRequest != null);
            Test.stopTest();
        }
    }  
    
     /*
     * Method Name : getAncillaryinfo
     * Description : Test method for MemberAncillaryResponse_H_HUM_V2 class
     * Return type : void
     * Paramater   : NA
     */ 
    static testMethod void getAncillaryinfo() 
    {
        User u = [SELECT Id, Lastname FROM User  WHERE Lastname='test User1' LIMIT 1];
        System.assertEquals(u.Lastname, 'test User1');
        System.runAs(u)
        {
            
            
            Test.startTest();
            MemberAncillaryResponse_H_HUM_V2 oMemberAncillary = new MemberAncillaryResponse_H_HUM_V2();
            MemberAncillaryResponse_H_HUM_V2.AncillaryEntityList oAncillaryEntityList = new MemberAncillaryResponse_H_HUM_V2.AncillaryEntityList();
            MemberAncillaryResponse_H_HUM_V2.HeadOfHousehold oHeadOfHousehold = new MemberAncillaryResponse_H_HUM_V2.HeadOfHousehold();
            MemberAncillaryResponse_H_HUM_V2.ChishlmCase oChishlmCase = new MemberAncillaryResponse_H_HUM_V2.ChishlmCase();
            MemberAncillaryResponse_H_HUM_V2.LTSS oLTSS = new MemberAncillaryResponse_H_HUM_V2.LTSS();
            MemberAncillaryResponse_H_HUM_V2.CoordinatedSystemOfCare oCoordinatedSystemOfCare = new MemberAncillaryResponse_H_HUM_V2.CoordinatedSystemOfCare();
            MemberAncillaryResponse_H_HUM_V2.DepartmentOfCorrection oDepartmentOfCorrection = new MemberAncillaryResponse_H_HUM_V2.DepartmentOfCorrection();
            MemberAncillaryResponse_H_HUM_V2.postalAddress opostalAddress = new MemberAncillaryResponse_H_HUM_V2.postalAddress();
            Test.stopTest();
        }
    }
	
	/*
     * Method Name : getEnterpriseID
     * Description : Used to validate  getMemberName, getMemberIDObjDetails and getEnterpriseID methods
     * Return type : void
     * Parameter   : NA
     */
    static testMethod void getEnterpriseID()
    {            
        User u = [SELECT Id, Lastname FROM User  WHERE Lastname='test User1' LIMIT 1];
        System.assertEquals(u.Lastname, 'test User1');
        Account oAcc = [Select Id, Name, Tenant_Id__c FROM Account where LastName = 'Account'];
        System.assertEquals(oAcc.Tenant_Id__c, '00');
        Policy_Member__c oPolMember = [SELECT Id, Policy__r.Client_Number__c,Group_Number__c, Effective_Date__c, Policy_Platform__c, Member__r.name, Policy__r.Product__r.Sold_Product_Key_Value__c , policy__r.source_cust_cov_key__c ,Product_Type__c FROM POLICY_Member__C WHERE Member__c = :oAcc.Id];
        id sRecId=oPolMember.id;
        System.assertNotEquals(sRecId, null);
        Test.StartTest();
		String EnterpriseID = MemberAncillaryRequest_D_HUM_V2.getEnterpriseId(sRecId);
        List<POLICY_Member__C> oMemberList1 = MemberAncillaryRequest_D_HUM_V2.getMemberName(sRecId); 
        List<Member_ID__c> oMemberIDList = MemberAncillaryRequest_D_HUM_V2.getMemberIDObjDetails(sRecId);
        System.assertEquals(EnterpriseID!=null,true);
        Test.StopTest();
             
    }  
}