/***************************************************************************************************************** 
Apex Class Name  : MemberAncillary_S_T_HUM
Version          : 1.0  
Created Date     : 05/31/2019
Function         : Test Class for MemberAncillary_S_HUM

Modification Log:
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------  
* Manikanta                       356372                 05/31/2019          Original Version - REQ - 379006 Ancillary service test methods
* Manikanta                                             06/07/2019          REQ - 379006 Made code changes to align with the DP changes
 ******************************************************************************************************************************/
@isTest
private Class MemberAncillary_S_T_HUM
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
        
        system.assertEquals(objpol.Source_Cust_Cov_Key__c,'123456789');
        system.assertEquals(objpol.Group_Name__c,objAcc.Id);
        system.assertEquals(objpol.Name,'testPolicy'); 
        
        insert objpol;
        system.assert(objpol.Name!=null);
    
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
        
        system.assertEquals(objPolMem.Relationship__c, 'Self');
        system.assertEquals(objPolMem.Name, 'Policy Member PM1');
        system.assertEquals(objPolMem.Policy_Member_External_ID__c, '00|ODS|6925004434817|12345678|34567|2013-07-01');
        
        insert objPolMem;       
        system.assert(objPolMem.Name!=null);
        
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        System.assertEquals(oUser.LastName, 'test User1');
        
        HUM_Webservice_Callout__c oGetMemberAncillary = new HUM_Webservice_Callout__c(Request_Type__c = 'POST', Name = 'Search_GetMemberAncillary',
        End_Point_URL__c = 'https://testst-cmservicews.hum.com', Certificate_Name__c = 'testts', Timeout__c = '12000',
        Content_Type__c = 'application/json', Active__c = true);
        insert oGetMemberAncillary;
        system.assert(oGetMemberAncillary.id!=null);
        system.assert(oGetMemberAncillary.name == 'Search_GetMemberAncillary');
    }
   
    /*
    * Method Name : TestassignValuesToAncillaryReq
    * Description : Used to test assignValuesToAncillaryReq method
    * Return type : void
    * Paramater   : NA
    */
    testMethod static void TestassignValuesToAncillaryReq()
    {
         Policy_Member__c oPolMember = [SELECT Id, Policy__r.Client_Number__c, Group_Number__c, Effective_Date__c, Policy_Platform__c, Member__r.Name, Policy__r.Product__r.Sold_Product_Key_Value__c, Policy__r.Group_Name__r.Source_Customer_Key__c, Product_Type__c from Policy_Member__c where Name = 'Policy Member PM1' limit 1];
         HUM_Webservice_Callout__c oServiceCallout = [select Name, Certificate_Name__c, End_Point_URL__c, Timeout__c, Content_Type__c, Request_Type__c from HUM_Webservice_Callout__c where name='Search_GetMemberAncillary' limit 1];   

         List<String> IEntityTypeList = new List<String>{'Premium', 'Deductible'};
         String sMemIdBase = 'H30972880';
         String sDependentCode = '00';     
         
         User oUser = [SELECT Id, LastName FROM User  WHERE Lastname ='test User1' LIMIT 1];
         System.assertEquals('test User1',oUser.LastName);
         system.runAs(oUser)
         {
             Test.startTest();
             MemberAncillary_S_HUM oMemberAncillary = new MemberAncillary_S_HUM();
             MemberAncillaryRequest_H_HUM oMemberAncillaryReq = new MemberAncillaryRequest_H_HUM();
             oMemberAncillaryReq = oMemberAncillary.assignValuesToAncillaryReq(IEntityTypeList, oPolMember, sMemIdBase, sDependentCode);
             system.assert(oMemberAncillaryReq!=null);
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
         system.runAs(oUser)
         {
              Test.startTest();
              MemberAncillary_S_HUM oMemberAncillary = new MemberAncillary_S_HUM();
          
              HttpRequest oRequest = new HttpRequest();
              HUM_Webservice_Callout__c oSvcAncillary = HUM_Webservice_Callout__c.getValues('Search_GetMemberAncillary');          
              String sRequestBody = '{"getMemberAncillary":{"includes":{"IAncillaryEntity":{"ICoverageIdentifier": {},"ICostShareInformation": {},"ICommunityEngagementExemptCode": {},"ITypeOfAssistance": {},"IDeductibleAmount": {},"IEntityTypeList":["Premium","Deductible","MedicallyFrail","Rewards","JuvenilleJustice","PenaltyReasonIndicator","SuspensionReasonIndicator","HospiceIndicator","FosterCareIndicator","GuardianshipIndicator"],"IHeadofHousehold": {},"ILTSS": {}}},"ancillaryRequest":[{"SequenceId": "1","CoverageIdentifier":{"MemberIdBase": "H66692336","DependentCode": "00","PlatformCode": "LV","CoverageStartDate": "2019-01-01","GroupId": "Y3495","DivisionId": "Y3495001","ProductId": "7333340101"}}]}}';                    
                  
              oRequest = oMemberAncillary.setupAncillaryRequest(oRequest, oSvcAncillary, sRequestBody);
              system.assert(oRequest!=null);
              Test.stopTest();  
         }
    }
   
    /*
    * Method Name : TestProcessResponse
    * Description : Used to test processResponse method
    * Return type : void
    * Paramater   : NA
    */    
    testMethod static void TestProcessResponse()
    {
         User oUser = [Select Id,lastname, Current_Queue__c from User where LastName = 'test User1'];
         System.assertEquals('test User1',oUser.LastName);
         system.runAs(oUser)
         {
              Test.startTest();
              MemberAncillary_S_HUM oMemberAncillary = new MemberAncillary_S_HUM();
              MemberAncillaryResponse_DTO_HUM ancillaryInfo = new MemberAncillaryResponse_DTO_HUM ();
              String sjson = '{"getMemberAncillaryResponse":{"responseHeader":{"environment": "INT", "context":{"clientSessionId": "G0x7f22da06bc08-1D", "server": "LOUWEBWTL25S02", "transactionId": "0180de59-a07d-4162-9f32-1b06f4d66647"}, "metrics":{"RequestTimeStamp": "2019-06-07T09:11:52.6", "ResponseTimeStamp": "2019-06-07T09:11:52.7", "duration": 61}},"responseStatus":{"status": "SUCCESS", "message":{"code": "MASI0001", "details": "Member Ancillary details returned successfully."}},"AncillaryEntityList":[{ "CoverageIdentifier":{"MemberIdBase": "H78898283", "DependentCode": "00", "PlatformCode": "LV", "CoverageStartDate": null, "GroupId": "0Y3521", "DivisionId": "0Y3521", "ProductId": "7592490201" }, "EntityTypeList":[{ "Type": "Premium", "Value": "12.00", "StartDate": "2019-03-01", "EndDate": "9999-12-31"},{"Type": "MedicallyFrail", "Value": "58", "StartDate": "2019-03-01", "EndDate": "9999-12-31" },{ "Type": "Rewards", "Value": "A", "StartDate": "2019-03-01", "EndDate": "9999-12-31"},{ "Type": "JuvenilleJustice", "Value": "Y", "StartDate": "2019-03-01", "EndDate": "9999-12-31"},{"Type": "PenaltyReasonIndicator", "Value": "CP", "StartDate": "2019-03-01", "EndDate": "9999-12-31"},{"Type": "SuspensionReasonIndicator", "Value": "CS", "StartDate": "2019-03-01", "EndDate": "9999-12-31"},{ "Type": "HospiceIndicator", "Value": "HP", "StartDate": "2019-03-01", "EndDate": "9999-12-31"},{"Type": "FosterCareIndicator", "Value": "N", "StartDate": "2019-03-01", "EndDate": "9999-12-31"},{"Type": "GuardianshipIndicator", "Value": "Y", "StartDate": "2019-03-01", "EndDate": "9999-12-31"}],"HeadofHousehold":{"FirstName": "RTNDIN", "LastName": "RTHNSAN", "PostalAddress":{ "AddressLine1": "10855 NW 45TH ST", "AddressLine2": "", "City": "CORAL SPRINGS", "State": "12", "Zip": "33065"}}, "LTSS":{ "OtherHealthIndicators": "", "SpecialCond": "SCE", "LtcProgramRecommendation": "", "ProdPlanId": "", "ProviderCode": "", "LevelOfCareDate": null, "LevelOfCareCode": "", "SpecialNeedsNote": "", "MedicaidAidCategoryCode": "MS", "MedicaidBeginDate": null, "MedicaidEndDate": null, "ACWM": "" }, "CostShareInformation": "181Y", "CommunityEngagementExemptCode": "E", "TypeOfAssistance": "ABDM", "DeductibleAmount": 12}]}}';
              ancillaryInfo = oMemberAncillary.processResponse(sjson);      
              system.assertEquals(ancillaryInfo.MemberIdBase ,'H78898283');
              system.assertEquals(ancillaryInfo.PlatformCode,'LV');
              system.assert(ancillaryInfo!=null);
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
         system.runAs(oUser)
         {
              Test.startTest();
              
              MemberAncillary_S_HUM oMemberAncillary1 = new MemberAncillary_S_HUM();
          
              HttpRequest oRequest1;
              HUM_Webservice_Callout__c oSvcAncillary1;
              String sRequestBody1;
                  
              oRequest1 = oMemberAncillary1.setupAncillaryRequest(oRequest1, oSvcAncillary1, sRequestBody1);
              system.assert(oRequest1==null);              
         }
    }
}