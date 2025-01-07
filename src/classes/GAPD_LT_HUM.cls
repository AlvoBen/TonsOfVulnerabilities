/**************************************************************************************************************************************************
Apex class Name    :  GAPD_LT_HUM
Version            :  1.0
Function           :  This is Test class for GAPD_LC_HUM.
Test Class         :  GAPD_LT_HUM

Modification Log:
 * Developer Name        Code Review #         Date                       Description
 *---------------------------------------------------------------------------------------------------
 * Abhishek Mangutkar                          09/05/2022                 Original Version
 * Aishwarya Pawar                          	10/13/2022                 REQ -3825306 ProviderDispute Service call changes 

 ************************************************************************************************************************************************* */

@isTest
private class GAPD_LT_HUM {
	/*
    * This method creates test data for test class
    * @param        None
    * @return       None
    */
  @testSetup static void prepareTestData() 
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
         CRMFunctionality_ONOFF_Switch__c switchDetails = new CRMFunctionality_ONOFF_Switch__c(Name='2119511',IsON__c=true,Release_Name__c='05/14/2021',Scrum_Team__c='Lego',Story_Title__c='This Switch is to show or hide newly added column for GNA status page');
        insert switchDetails;
        system.assertEquals(oCCSUser.LastName,'test User1');
        
        System.runAs (oCCSUser) 
        {
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

            
            Policy_Member__c objPolMem = new Policy_Member__c();
            objPolMem.Name = 'testpolmem11';
            objPolMem.Relationship__c = 'Self';
            objPolMem.Relationship_Code__c = '00';
            objPolMem.Subscriber__c = objPolMem.Id;
            objPolMem.Effective_Date__c=Date.today().addDays(-1).format();
            objPolMem.End_Date__c=Date.today().addDays(1).format();
            objPolMem.Policy_Member_External_ID__c='00|ODS|6925004434817|12345678|34567|2013-07-01';
            objPolMem.Member__c = objAcc.Id;
            objPolMem.Policy__c = objpol.Id;
            objPolMem.Policy__r = objpol;
            insert objPolMem;
            system.assertEquals(objPolMem.Relationship__c, 'Self');
            
            Policy_Member__c objPolMem1 = new Policy_Member__c();
            objPolMem1.Name = 'testpolmem111';
            objPolMem1.Relationship__c = 'Self';
            objPolMem1.Relationship_Code__c = '00';
            objPolMem1.Subscriber__c = objPolMem.Id;
            objPolMem1.Effective_Date__c=Date.today().addDays(-1).format();
            objPolMem1.End_Date__c=Date.today().addDays(1).format();
            objPolMem1.Policy_Member_External_ID__c='00|ODS|7925004434817|12345678|34567|2013-07-01';
            objPolMem1.Member__c = objAcc1.Id;
            objPolMem1.Policy__c = objpol1.Id;
            objPolMem1.Policy__r = objpol1;
            insert objPolMem1;
            system.assertEquals(objPolMem1.Relationship__c, 'Self');
            
            Policy_Member__c objPolMem2 = new Policy_Member__c();
            objPolMem2.Name = 'testpolmem114';
            objPolMem2.Relationship__c = 'Self';
            objPolMem2.Relationship_Code__c = '00';
            objPolMem2.Subscriber__c = objPolMem1.Id;
            objPolMem2.Effective_Date__c=Date.today().addDays(-1).format();
            objPolMem2.End_Date__c=Date.today().addDays(1).format();
            objPolMem2.Policy_Member_External_ID__c='00|ODS|7925004435817|12345678|34567|2013-07-01';
            objPolMem2.Member__c = objAcc2.Id;
            objPolMem2.Policy__c = objpol2.Id;
            objPolMem2.Policy__r = objpol2;
            insert objPolMem2;
            system.assertEquals(objPolMem2.Relationship__c, 'Self');


                     
            Member_ID__c objMemberID1 = new Member_ID__c();
            objMemberID1.policy_member__c = objPolMem.id;
            objMemberID1.policy__c = objpol.Id;
            objMemberID1.Name = '12345';
            objMemberID1.Member_Card_External_ID__c = 'ODS12345'; 
            objMemberID1.Member_Dependent_Code__c  = '03';
            objMemberID1.Type__c = 'SSN';        
            insert objMemberID1;
            system.assertequals('12345',objMemberID1.Name );
            
            Member_ID__c objMemberID2 = new Member_ID__c();
            objMemberID2.policy_member__c = objPolMem1.id;
            objMemberID2.policy__c = objpol1.Id;
            objMemberID2.Name = 'H12345';
            objMemberID2.Member_Card_External_ID__c = 'ODS123456'; 
            objMemberID2.Member_Dependent_Code__c  = '03';
            objMemberID2.Type__c = 'Member-Id-Base';        
            insert objMemberID2;
            system.assertequals('H12345',objMemberID2.Name);
            
            Member_ID__c objMemberID3 = new Member_ID__c();
            objMemberID3.policy_member__c = objPolMem2.id;
            objMemberID3.policy__c = objpol2.Id;
            objMemberID3.Name = 'H123453';
            objMemberID3.Member_Card_External_ID__c = 'ODS1234564'; 
            objMemberID3.Member_Dependent_Code__c  = '03';
            objMemberID3.Type__c = 'Member-Id-Base';        
            insert objMemberID3;
            system.assertequals('H123453',objMemberID3.Name);
            
            HUM_Webservice_Callout__c calloutSettings = new HUM_Webservice_Callout__c();
            calloutSettings.Name = 'GrievanceAndAppeal';
            calloutSettings.Active__c = True;
            calloutSettings.Enable_Service_Tester__c = True;
            calloutSettings.Certificate_Name__c = 'Humana_GuidanceCare_Pileus';
            calloutSettings.Content_Type__c = 'application/json';
            calloutSettings.Request_Type__c = 'Post';
            calloutSettings.Timeout__c = '12000';
            calloutSettings.End_Point_URL__c = 'https://dev-crmservicews.humana.com/XXX';
            insert calloutSettings;
            
             HUM_Webservice_Callout__c PDcalloutSettings = new HUM_Webservice_Callout__c();
            PDcalloutSettings.Name = 'ProviderCorrespondenceDisputes';
            PDcalloutSettings.Active__c = True;
            PDcalloutSettings.Enable_Service_Tester__c = True;
            PDcalloutSettings.Certificate_Name__c = 'Humana_GuidanceCare_Pileus';
            PDcalloutSettings.Content_Type__c = 'application/json';
            PDcalloutSettings.Request_Type__c = 'Post';
            PDcalloutSettings.Timeout__c = '12000';
            PDcalloutSettings.End_Point_URL__c = 'https://dev-crmservicews.humana.com/XXX';
            insert PDcalloutSettings;
             
        }
    }        
    /*
    * Method to test testGrievanceAndAppealPositive
    * @param        None
    * @return       None
    */
    private static testmethod void testGrievanceAndAppealPositive()
    {
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        system.runAs(testUser)
        {   
            Account accountObj = [Select Enterprise_ID__c from account where LastName = 'Account1'];
            String jsonRequest = '{ "getGrievanceAndAppealByCriteriaRequest": { "thruDate":"2022-08-16T11:50:41", "member": { "externalId":"1070063968" }, "fromDate":"2021-02-14T11:50:41" } }';
            Boolean providerDisputeFlag=false;
            Test.StartTest();
            Continuation conti = (Continuation)GAPD_LC_HUM.getArchiveAttachments(accountObj.Enterprise_ID__c, jsonRequest , providerDisputeFlag);            
            HttpResponse response = new HttpResponse();
            String resBodyString = '{"getGrievanceAndAppealByCriteriaResponse": {"cases": [{"caseNumber": "21096483671",}]}}';
            response.setBody(resBodyString);
            response.setStatusCode(200);
            conti.state = 'Continuation-1';
            string reqType = 'Continuation-1';        
            List<string> labelGetmember = new List<string>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,response);
            String responseStr = (String) GAPD_LC_HUM.processGAPDResponse(labelGetmember,resBodyString);
            Boolean bDataExist = responseStr.containsIgnoreCase( '21096483671') ? true : false;
            system.assertEquals(response.getBody(), resBodyString);            
            Test.StopTest();
        }
    }
 
	/*
    * Method to test testGrievanceAndAppealNegative
    * @param        None
    * @return       None
    */
    private static testmethod void testGrievanceAndAppealNegative()
    {
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        system.runAs(testUser)
        {             
            Account accountObj = [Select Enterprise_ID__c from account where LastName = 'Account1'];
            String jsonRequest;
            Boolean providerDisputeFlag=false;
            Test.StartTest();
            Continuation conti = (Continuation)GAPD_LC_HUM.getArchiveAttachments(accountObj.Enterprise_ID__c, jsonRequest, providerDisputeFlag);
       		HttpResponse response = new HttpResponse();
            String resBodyString = '';
            response.setBody(resBodyString);            
            string reqType = 'Continuation-1';        
            List<string> labelGetmember = new List<string>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,response);
            String responseStr = (String) GAPD_LC_HUM.processGAPDResponse(labelGetmember,resBodyString);
            Boolean bNoDataExist;
            If(responseStr != null){
                bNoDataExist = responseStr.containsIgnoreCase('21096483671') ? false : true;
            } 
            else{
                bNoDataExist = true;
            }
             
            system.assertEquals(true, bNoDataExist);
            system.assertEquals(response.getBody(), resBodyString);
            Test.StopTest();
        }
    }
    
    
    private static testmethod void testProviderDisputePositive()
    {
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        system.runAs(testUser)
        {   
            Account accountObj = [Select Enterprise_ID__c from account where LastName = 'Account1'];
            String jsonRequest = '{"getProviderDisputesByCriteria":{"thruDate":"2022-10-12T14:31:28","member":{"externalId":"1100356833"},"fromDate":"2021-04-12T14:31:28"}}';
            Boolean providerDisputeFlag=true;
            Test.StartTest();
            Continuation conti = (Continuation)GAPD_LC_HUM.getArchiveAttachments(accountObj.Enterprise_ID__c, jsonRequest , providerDisputeFlag);            
            HttpResponse response = new HttpResponse();
            String resBodyString = '{ "getProviderDisputesByCriteriaResponse":{ "providerDisputes": [{"allegation":"Testing Macess to MHK PD to Macess with eDoc.","aor":{"city":"ORLANDO","firstName":"Bryan","lastName":"Prine","state":"FL","type":"Provider","zip":"32891"},"appealsStatusReason":"Pending Initial Review","caseCategory":"Provider Dispute","caseNumber":"D21167446299","category":"Unknown","completionDate":"","createDate":"2021-06-16T12:13:11-04:00","createUser":{"userId":"systemws","firstName":"system","lastName":"ws","email":"email","terminated":"false","accountLocked":"false","comment":"Need for webservice","siteLocation":"Tampa","clientName":"Humana"},"customText1":"PC","disputeType":"Screening","dueDate":"2021-06-18T23:59:59-04:00","intakeDate":"2021-06-16T07:45:00-04:00","intakeMode":"Mail","manualReceivedDate":"","medhokID":"18502","medicalClaims":[],"member":{"medhokId":"5243868","firstName":"NIVIO","lastName":"MCMENAMIN","fullName":"MCMENAMIN, NIVIO","age":"19","dateOfBirth":"2002-12-05T00:00:00-05:00","pcp":{"organizationName":"PEDIM HEALTHCARE","firstName":"","lastName":"PEDIM HEALTHCARE","fullName":"PEDIM HEALTHCARE","npi":"","federalTaxID":"562498335","providerID":"CAS000123384AD","status":""},"daysLastDischarge":"-1","memberId":"1100356833","externalId":"1100356833","gender":"Male","spokenLanguage":"English","writtenLanguage":"English","subscriberId":"1100356833"},"notification":[{"createdBy":"systemws","createdDate":"2021-06-16T12:13:12-04:00","note":"IRID : 7d3d380c-bd1e-49fa-a9ce-1790c330ea30","noteDate":"2021-06-16T00:00:00-04:00","noteType":"Other"},{"createdBy":"systemws","createdDate":"2021-06-16T12:13:12-04:00","note":"DocType : PC","noteDate":"2021-06-16T00:00:00-04:00","noteType":"Other"},{"createdBy":"systemws","createdDate":"2021-06-16T12:13:12-04:00","note":"Received Date : 2021-06-16","noteDate":"2021-06-16T00:00:00-04:00","noteType":"Other"},{"createdBy":"systemws","createdDate":"2021-06-16T12:13:12-04:00","note":"Intake Mode : Mail","noteDate":"2021-06-16T00:00:00-04:00","noteType":"Other"},{"createdBy":"systemws","createdDate":"2021-06-16T12:13:12-04:00","note":"Source Case Number : D21167446299","noteDate":"2021-06-16T00:00:00-04:00","noteType":"Other"},{"createdBy":"systemws","createdDate":"2021-06-16T12:13:12-04:00","note":"EDoc URL : https://int-loufeo1.humana.com/edocs/?ecn=0000005901350740010000100001&docid=21162M000537&doctype=P","noteDate":"2021-06-16T00:00:00-04:00","noteType":"Other"},{"createdBy":"systemws","createdDate":"2021-06-16T12:13:12-04:00","note":"DCN : 21162M000537","noteDate":"2021-06-16T00:00:00-04:00","noteType":"Other"},{"createdBy":"systemws","createdDate":"2021-06-16T12:13:12-04:00","note":"Claim Number(s) : 202006116488833","noteDate":"2021-06-16T00:00:00-04:00","noteType":"Claim Number(s)"}],"priority":"Standard","product":"FL Medicaid","receivedDate":"2021-06-16T07:45:00-04:00","requestType":"Dispute","status":"Open","statusReason":"Pending Initial Review","subCategory":"Unknown","updateDate":"2021-06-17T06:00:44-04:00","updateUser":{"userId":"HUB8186","firstName":"Hemalatha","lastName":"Bandaru","title":"Specialist","email":"hbandaru1@humana.com","terminated":"false","accountLocked":"false","lastLogin":"2022-10-10T03:55:49-04:00"},"validAppeal":"true","wsMemberEligExtAttribute":{"attribute1":"FL","attribute3":"Humana Medical Plan, Inc.","attribute4":"00","attribute5":"H21727522","attribute6":"00","attribute7":"G","attribute8":"MCD","attribute9":"LV","attribute11":"Y0909012","attribute12":"N","attribute13":"20500","attribute14":"Y","attribute19":"7245004732622","attribute20":"7245004732622","attribute21":"690214001","attribute22":"365686138-00","attribute23":"MCD","attribute25":"LV","attribute26":"MCD","attribute27":"MCD","attribute29":"N","attribute30":"004129","attribute32":"N","attribute34":"Medicaid","attribute35":"153","attribute36":"MED"},"wsMemberEligibility":{"medhokId":"11860691","eligGroup":"HUMANA MEDICAL PLAN, INC.","company":"Medicaid","companyLob":"FL-MEDICAID","eligPlan":"MEDICAID GATE CAP DIR","medicaid":"7887954271","medicareno":"9W42MJ5CG05","effectivedate":"2019-09-20T00:00:00-04:00","extId":"a1dbc6d7c1de085776c69b52f573ad6e"},"dateOFServiceDesc":"","wsProvider":{"organizationName":"","firstName":"Bryan","lastName":"Prine","fullName":"Prine, Bryan","npi":"1447330535","federalTaxID":"591680273","providerID":"PIM1535605","status":"","providerDisplayName":"Bryan Prine"}}]}}';
            response.setBody(resBodyString);
            response.setStatusCode(200);
            conti.state = 'Continuation-1';
            string reqType = 'Continuation-1';        
            List<string> labelGetmember = new List<string>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,response);
            String responseStr = (String) GAPD_LC_HUM.processGAPDResponse(labelGetmember,resBodyString);
            Boolean bDataExist = responseStr.containsIgnoreCase('D21167446299') ? true : false;
            system.assertEquals(response.getBody(), resBodyString);            
            Test.StopTest();
        }
    }
    
    private static testmethod void testProviderDisputeNegative()
    {
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        system.runAs(testUser)
        {             
            Account accountObj = [Select Enterprise_ID__c from account where LastName = 'Account1'];
            String jsonRequest;
            Boolean providerDisputeFlag=true;
            Test.StartTest();
            Continuation conti = (Continuation)GAPD_LC_HUM.getArchiveAttachments(accountObj.Enterprise_ID__c, jsonRequest, providerDisputeFlag);
       		HttpResponse response = new HttpResponse();
            String resBodyString = 'faultcode';
            response.setBody(resBodyString);            
            string reqType = 'Continuation-1';        
            List<string> labelGetmember = new List<string>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,response);
            String responseStr = (String) GAPD_LC_HUM.processGAPDResponse(labelGetmember,resBodyString);
            Boolean bNoDataExist;
            If(responseStr != null){
                bNoDataExist = responseStr.containsIgnoreCase('21096483671') ? false : true;
            } 
            else{
                bNoDataExist = true;
            }
             
            system.assertEquals(true, bNoDataExist);
            system.assertEquals(response.getBody(), resBodyString);
            Test.StopTest();
        }
    }
    
}