/*******************************************************************************************************************************
Test Class Name : ArchivedCaseDetail_LT_HUM
Version         : 1.0
Created On      : 07/18/2022   
Function        : Test class of ArchivedCaseDetail_LC_HUM/LS/LT classes.
                 
Modification Log: 
*  Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*  Ashish Kumar/Kajal Namdev		                   07/18/2022                  Original version
**********************************************************************************************************************************************************/
@isTest
public class ArchivedCaseDetail_LT_HUM {
    
    
    /*
* Method Name : setUpTestData
* Description : This method creates test data for test class
* Return type : None
* Parameter   : None
*/
    @testSetup 
    static void setUpTestData()
    {
        HUM_Webservice_Callout__c inquiryDetailsService = new HUM_Webservice_Callout__c(Name = 'ArchivedCaseDetailService',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c = 'https://int-crmservicews.humana.com/CCPGCPDataRetention/InquiryDetails' ,Timeout__c = '12000',Request_Type__c = 'GET',Content_Type__c ='application/json');
        insert inquiryDetailsService; 
        Endpoint_Url_HUM__c endpointURL1 = new Endpoint_Url_HUM__c(Name = 'SSO_LOGINURL', Remote_Url__c = 'https://int-crmservicews.humana.com/CCPGCPDataRetention/InquiryDetails');
        Endpoint_Url_HUM__c endpointURL2 = new Endpoint_Url_HUM__c(Name = 'DCNSpider2URL', Remote_Url__c = 'https://int-crmservicews.humana.com/CCPGCPDataRetention/InquiryDetails');
		insert endpointURL1;
        insert endpointURL2;
        User objUser = HUMTestDataHelperTest.createTestUser('User1',GLOBAL_CONSTANT_HUM.PHARMACYSPECIALIST_PROFILE_HUM);   
        insert objUser;
        System.runAs(objUser)
        {
        Product2 oProd = new Product2(Name = 'Laptop X200',Segment_Type__c='MCR',ProductCode='MED', Major_LOB__c='MER');
        insert oProd;
        System.assertNotEquals(oProd.Id, NULL, 'Value not matched');
    
        Account objMemAccount = HUMTestDataHelperTest.getAccount();
        objMemAccount.FirstName = 'Test FName';
        objMemAccount.LastName = 'LName'; 
        objMemAccount.source_platform_code__c = 'EM' ;
        objMemAccount.Enterprise_Id__c = String.valueof(2000);
        objMemAccount.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
        objMemAccount.Birthdate__c = String.valueOf(System.today().month()+'/'+system.today().day()+'/'+System.today().year());
        insert objMemAccount;
        system.assertEquals(objMemAccount.LastName == 'LName', true); 
        
        PurchaserPlan oMedicalPolicy = new PurchaserPlan();
        oMedicalPolicy.Policy_Group_Number__c='11111';
        oMedicalPolicy.Name = 'Medical Product';
        oMedicalPolicy.source_cust_cov_key__c = '12345678901234567890' ; 
        oMedicalPolicy.PayerId = objMemAccount.Id;
        oMedicalPolicy.Major_LOB__c ='EPO';  
        oMedicalPolicy.Platform__c = 'EM';
        insert oMedicalPolicy;
        system.assertEquals(oMedicalPolicy.Name == 'Medical Product', true);
        
        MemberPlan objMemberplan = new MemberPlan();
        objMemberplan.Name='MemberPlan1';
        objMemberplan.MemberId=objMemAccount.id;
        objMemberPlan.Member_Dependent_Code__c = '1234567890';
        objMemberPlan.Member_Id_Base__c = 'test23456';
        objMemberPlan.GroupNumber = '737803';
        objMemberPlan.PlanId= oMedicalPolicy.Id;
        objMemberPlan.Member_Plan_External_Id__C = '00|ODS|6925004434817|12345678|34567|2013-07-01';
        insert objMemberplan; 
        system.assert(objMemberPlan.GroupNumber == '737803');
        
        Policy__c objpol = HUMTestDataHelperTest.getPolicy();
        objpol.Name='testPolicy';
        objpol.Group_Name__c = objMemAccount.Id;
        objpol.Major_LOB__c = 'MER';
        objpol.Source_Cust_Cov_Key__c = '123456789';
        objpol.Product__c = oProd.Id;
        objpol.Platform__c = 'LV';
        insert objpol;
        system.assertEquals(objpol.Source_Cust_Cov_Key__c,'123456789');
        
        Policy_Member__c objPolMem = new Policy_Member__c();
        objPolMem.Name = 'testpolmem11';
        objPolMem.Relationship__c = 'Self';
        objPolMem.Relationship_Code__c = '00';
        objPolMem.Subscriber__c = objPolMem.Id;
        objPolMem.Effective_Date__c=Date.today().addDays(-1).format();
        objPolMem.End_Date__c=Date.today().addDays(1).format();
        objPolMem.Policy_Member_External_ID__c='00|ODS|6925004434817|12345678|34567|2013-07-01';
        objPolMem.Member__c = objMemAccount.Id;
        objPolMem.Policy__c = objpol.Id;
        objPolMem.Policy__r = objpol;
        insert objPolMem;
        system.assertEquals(objPolMem.Relationship__c, 'Self');
    
        classification__c cl = new classification__c();
        cl.Name = 'Access To Care';
        insert cl;
        
        Intent__c it = new Intent__c();
        it.Name = 'test intent';
        insert it;
        HUMConstants__c objCSClientId = new HUMConstants__c(
            Name='HUMArchival_clientId',
            StringValue__c = '6d16ad37e1154c65b2a878d1803d9dab');
        insert objCSClientId;
        HUMConstants__c objCSClientSecret = new HUMConstants__c(
            Name='HUMArchival_clientSecret',
            StringValue__c = '16e4f105e35b46B4AF983c0a2A3330df');
        insert objCSClientSecret;
    }
    }
        
     @isTest 
    static void testFetchCaseDetailWithPolicyMemberId()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        Policy_Member__c polMemberObj = [select id from Policy_Member__c where Name = 'testpolmem11' limit 1];
        Account accountObj = [Select id,Name from Account limit 1];
        classification__c cl = [select id, name from Classification__c limit 1];
        Intent__c intentobj = [select id, name from Intent__c limit 1];

       	System.runAs(testUser)
        {
            
            Test.startTest();
            ArchivedCaseDetail_LC_HUM inquiryDetails = new ArchivedCaseDetail_LC_HUM();
            Continuation continueObj = (Continuation)ArchivedCaseDetail_LC_HUM.fetchCaseDetailData('1014741145','All','25','50');
            continueObj.continuationMethod = 'parseCaseDetails';
            HttpResponse httpRes = new Httpresponse ();
			String resBodyString = '{"CaseCommentsResponse":{"Header":{"sTotalRows":"26"},"CaseCommentsResponseData":[{"sId":"00a2h0000015i3uAAA","sParentId":"5002h000005MZsVAAW","sCreatedDate":"2022-05-31T06:55:46.000+0000","sCommentBody":"Sample","sCreatedById":"' + testUser.Id + '","sLastModifiedById":"0052h0000030XXjAAM","sLastModifiedDate":"2022-05-31T06:55:46.000+0000"}]},"CaseHistoryResponse":{"Header":{"sTotalRows":"25"},"CaseHistoryResponseData":[{"sId":"0172h0000Bn4je3AQA","sCaseId":"5002h000006atsiAAA","sCreatedById":"0052h000003I73FAAS","sCreatedDate":"2022-06-02T06:55:46.000+0000","sField":"Case_owner","sNewValue":"DentalCalls","sOldValue":"Calls"}]},"CaseDetailResponse":{"sId":"5002h000006pp5kAAA","sCaseNumber":"1000228871612","sOrigin":"InboundCall","sSubtype":"ABC","sType":"CustomerInquiry","sOwner_Queue":"HumanaPharmacyClinicalServices","sCase_Owner":"Ashish","sStatus":"InProgress","sRecordTypeId":"012F00000013LYXIA2","sCreatedDate":"2022-07-06T17:08:10.000Z","sInteracting_With_Type":"RumanBois","sInteracting_About_Type":"RumanBois","sAccountId":"'+accountObj.Id+'","sInteracting_With":"0012h00000f9VJAAA2","sClassification_Id":"'+cl.Id+'","sPolicy_Member":"'+polMemberObj.Id+'","sMember_Plan_Id":"","sIntent_Id":"'+intentobj.Id+'","sPriority":"Normal","sDoc_Type":"Doc","sDCN":"200601109032","sClassification_Type":"DelayedOrder","sTopic":"AVX","sCase_Age":"","sInteracting_With_Name":"","sG_A_Reason":"","sG_A_Rights_Given":"","sComplaint":"","sComplaint_Reason":"","sComplaint_Type":"","sLanguage_Preference":"","sOpenEnrollment":"true","sOpenEnrollmentType":"","sAccount_MedicareID":"","sFollow_up_Due_Date":"","sCase_Referred_To":"","sResponse_Status":"","sRe_Open_Case_Age":"","sRe_Open_Case_Date":"","sCreatedById":"' + testUser.Id + '","sLastModifiedById":"'+ testUser.Id +'","sCreated_By_Queue":"","sLastModifiedby_Queue":"","sWork_Queue_View_Name":"","sDCN_Type":"claim","sDCN_URL":"https://int-loufeo1.humana.com/edocs/?ecn=0000005837681930010000100001&docid=20210C000000&doctype=P"},"CaseTasksResponse":{"Header":{"sTotalRows":"25"},"CaseTasksResponseData":[{"sId":"00T2h00000Er8EdEAJ","sTask_Number":"T79975291","sActivityDate":"2022-04-20","sType":"WorkTask","sStatus":"InProgress","sPriority":"Normal","sTask_Age":"123","sCreated_DateTime":"2022-05-31T06:55:46.000+0000","sDate_Time_Closed":"2022-05-31T06:55:46.000+0000","sTask_Owner":"mastere","sOwner_Queue":"LouisvilleRSOCallsSupport","sRecordTypeId":"012F00000013O82IAE"},{"sId":"00T2h00000Er8EdEAJ","sTask_Number":"T7997529","sActivityDate":"2022-06-20","sType":"WorkTask","sStatus":"InProgress","sPriority":"Normal","sTask_Age":"123","sCreated_DateTime":"2022-05-31T06:55:46.000+0000","sDate_Time_Closed":"2022-05-31T06:55:46.000+0000","sTask_Owner":"mastere","sOwner_Queue":"LouisvilleRSOCallsSupport","sRecordTypeId":"012F00000013O82IAE"}]}}';            httpRes.setBody(resBodyString);
            httpRes.setStatusCode(200);
            System.assert(httpRes.getBody() == resBodyString);
            
            string reqType = 'Continuation-1';        
            List<string>labelGetmember = new List<string>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,httpRes);
            
            ArchivedCaseDetail_LC_HUM.parseCaseDetails(labelGetmember,continueObj);
            Test.stopTest();
       	}

    }
    
    @isTest 
    static void testFetchCaseDetail()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        MemberPlan memberPlanObj = [select id,Name,PlanId from MemberPlan where Name = 'MemberPlan1' limit 1];
        Account accountObj = [Select id,Name from Account limit 1];
        classification__c cl = [select id, name from Classification__c limit 1];
        Intent__c intentobj = [select id, name from Intent__c limit 1];

       	System.runAs(testUser)
        {
            
            Test.startTest();
            ArchivedCaseDetail_LC_HUM inquiryDetails = new ArchivedCaseDetail_LC_HUM();
            Continuation continueObj = (Continuation)ArchivedCaseDetail_LC_HUM.fetchCaseDetailData('1014741145','All','25','50');
            continueObj.continuationMethod = 'parseCaseDetails';
            HttpResponse httpRes = new Httpresponse ();
            String resBodyString = '{"CaseCommentsResponse":{"Header":{"sTotalRows":"26"},"CaseCommentsResponseData":[{"sId":"00a2h0000015i3uAAA","sParentId":"5002h000005MZsVAAW","sCreatedDate":"2022-05-31T06:55:46.000+0000","sCommentBody":"Sample","sCreatedById":"' + testUser.Id + '","sLastModifiedById":"0052h0000030XXjAAM","sLastModifiedDate":"2022-05-31T06:55:46.000+0000"}]},"CaseHistoryResponse":{"Header":{"sTotalRows":"25"},"CaseHistoryResponseData":[{"sId":"0172h0000Bn4je3AQA","sCaseId":"5002h000006atsiAAA","sCreatedById":"0052h000003I73FAAS","sCreatedDate":"2022-06-02T06:55:46.000+0000","sField":"Case_owner","sNewValue":"DentalCalls","sOldValue":"Calls"}]},"CaseDetailResponse":{"sId":"5002h000006pp5kAAA","sCaseNumber":"1000228871612","sOrigin":"InboundCall","sSubtype":"ABC","sType":"CustomerInquiry","sOwner_Queue":"HumanaPharmacyClinicalServices","sCase_Owner":"Ashish","sStatus":"InProgress","sRecordTypeId":"012F00000013LYXIA2","sCreatedDate":"2022-07-06T17:08:10.000Z","sInteracting_With_Type":"RumanBois","sInteracting_About_Type":"RumanBois","sAccountId":"'+accountObj.Id+'","sInteracting_With":"0012h00000f9VJAAA2","sClassification_Id":"'+cl.Id+'","sMember_Plan_Id":"'+memberPlanObj.Id+'","sIntent_Id":"'+intentobj.Id+'","sPriority":"Normal","sDoc_Type":"Doc","sDCN":"200601109032","sClassification_Type":"DelayedOrder","sTopic":"AVX","sCase_Age":"","sInteracting_With_Name":"","sG_A_Reason":"","sG_A_Rights_Given":"","sComplaint":"","sComplaint_Reason":"","sComplaint_Type":"","sLanguage_Preference":"","sOpenEnrollment":"true","sOpenEnrollmentType":"","sAccount_MedicareID":"","sFollow_up_Due_Date":"","sCase_Referred_To":"","sResponse_Status":"","sRe_Open_Case_Age":"","sRe_Open_Case_Date":"","sCreatedById":"' + testUser.Id + '","sLastModifiedById":"'+ testUser.Id +'","sCreated_By_Queue":"","sLastModifiedby_Queue":"","sWork_Queue_View_Name":"","sDCN_Type":"ddd","sDCN_URL":"https://int-loufeo1.humana.com/edocs/?ecn=0000005837681930010000100001&docid=20210C000000&doctype=P"},"CaseTasksResponse":{"Header":{"sTotalRows":"25"},"CaseTasksResponseData":[{"sId":"00T2h00000Er8EdEAJ","sTask_Number":"T79975291","sActivityDate":"2022-04-20","sType":"WorkTask","sStatus":"InProgress","sPriority":"Normal","sTask_Age":"123","sCreated_DateTime":"2022-05-31T06:55:46.000+0000","sDate_Time_Closed":"2022-05-31T06:55:46.000+0000","sTask_Owner":"mastere","sOwner_Queue":"LouisvilleRSOCallsSupport","sRecordTypeId":"012F00000013O82IAE"},{"sId":"00T2h00000Er8EdEAJ","sTask_Number":"T7997529","sActivityDate":"2022-06-20","sType":"WorkTask","sStatus":"InProgress","sPriority":"Normal","sTask_Age":"123","sCreated_DateTime":"2022-05-31T06:55:46.000+0000","sDate_Time_Closed":"2022-05-31T06:55:46.000+0000","sTask_Owner":"mastere","sOwner_Queue":"LouisvilleRSOCallsSupport","sRecordTypeId":"012F00000013O82IAE"}]}}';
            httpRes.setBody(resBodyString);
            httpRes.setStatusCode(200);
            System.assert(httpRes.getBody() == resBodyString);
            
            string reqType = 'Continuation-1';        
            List<string>labelGetmember = new List<string>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,httpRes);
            
            ArchivedCaseDetail_LC_HUM.parseCaseDetails(labelGetmember,continueObj);
            Test.stopTest();
       	}

    }
    
    @isTest 
    static void testFetchCaseDetailError2000()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        MemberPlan memberPlanObj = [select id,Name,PlanId from MemberPlan where Name = 'MemberPlan1' limit 1];
        Account accountObj = [Select id,Name from Account limit 1];
        classification__c cl = [select id, name from Classification__c limit 1];
        Intent__c intentobj = [select id, name from Intent__c limit 1];

       	System.runAs(testUser)
        {
            
            Test.startTest();
            ArchivedCaseDetail_LC_HUM inquiryDetails = new ArchivedCaseDetail_LC_HUM();
            Continuation continueObj = (Continuation)ArchivedCaseDetail_LC_HUM.fetchCaseDetailData('1014741145','All','25','50');
            continueObj.continuationMethod = 'parseCaseDetails';
            HttpResponse httpRes = new Httpresponse ();
            String resBodyString = '{"CaseCommentsResponse":{"Header":{"sTotalRows":"26"},"CaseCommentsResponseData":[{"sId":"00a2h0000015i3uAAA","sParentId":"5002h000005MZsVAAW","sCreatedDate":"2022-05-31T06:55:46.000+0000","sCommentBody":"Sample","sCreatedById":"' + testUser.Id + '","sLastModifiedById":"0052h0000030XXjAAM","sLastModifiedDate":"2022-05-31T06:55:46.000+0000"}]},"CaseHistoryResponse":{"Header":{"sTotalRows":"25"},"CaseHistoryResponseData":[{"sId":"0172h0000Bn4je3AQA","sCaseId":"5002h000006atsiAAA","sCreatedById":"0052h000003I73FAAS","sCreatedDate":"2022-06-02T06:55:46.000+0000","sField":"Case_owner","sNewValue":"DentalCalls","sOldValue":"Calls"}]},"CaseDetailResponse":{"sId":"5002h000006pp5kAAA","sCaseNumber":"1000228871612","sOrigin":"InboundCall","sSubtype":"ABC","sType":"CustomerInquiry","sOwner_Queue":"HumanaPharmacyClinicalServices","sCase_Owner":"Ashish","sStatus":"InProgress","sRecordTypeId":"012F00000013LYXIA2","sCreatedDate":"2022-07-06T17:08:10.000Z","sInteracting_With_Type":"RumanBois","sInteracting_About_Type":"RumanBois","sAccountId":"'+accountObj.Id+'","sInteracting_With":"0012h00000f9VJAAA2","sClassification_Id":"'+cl.Id+'","sMember_Plan_Id":"'+memberPlanObj.Id+'","sIntent_Id":"'+intentobj.Id+'","sPriority":"Normal","sDoc_Type":"Doc","sDCN":"200601109032","sClassification_Type":"DelayedOrder","sTopic":"AVX","sCase_Age":"","sInteracting_With_Name":"","sG_A_Reason":"","sG_A_Rights_Given":"","sComplaint":"","sComplaint_Reason":"","sComplaint_Type":"","sLanguage_Preference":"","sOpenEnrollment":"true","sOpenEnrollmentType":"","sAccount_MedicareID":"","sFollow_up_Due_Date":"","sCase_Referred_To":"","sResponse_Status":"","sRe_Open_Case_Age":"","sRe_Open_Case_Date":"","sCreatedById":"' + testUser.Id + '","sLastModifiedById":"'+ testUser.Id +'","sCreated_By_Queue":"","sLastModifiedby_Queue":"","sWork_Queue_View_Name":"","sDCN_Type":"ddd","sDCN_URL":"https://int-loufeo1.humana.com/edocs/?ecn=0000005837681930010000100001&docid=20210C000000&doctype=P"},"CaseTasksResponse":{"Header":{"sTotalRows":"25"},"CaseTasksResponseData":[{"sId":"00T2h00000Er8EdEAJ","sTask_Number":"T79975291","sActivityDate":"2022-04-20","sType":"WorkTask","sStatus":"InProgress","sPriority":"Normal","sTask_Age":"123","sCreated_DateTime":"2022-05-31T06:55:46.000+0000","sDate_Time_Closed":"2022-05-31T06:55:46.000+0000","sTask_Owner":"mastere","sOwner_Queue":"LouisvilleRSOCallsSupport","sRecordTypeId":"012F00000013O82IAE"},{"sId":"00T2h00000Er8EdEAJ","sTask_Number":"T7997529","sActivityDate":"2022-06-20","sType":"WorkTask","sStatus":"InProgress","sPriority":"Normal","sTask_Age":"123","sCreated_DateTime":"2022-05-31T06:55:46.000+0000","sDate_Time_Closed":"2022-05-31T06:55:46.000+0000","sTask_Owner":"mastere","sOwner_Queue":"LouisvilleRSOCallsSupport","sRecordTypeId":"012F00000013O82IAE"}]}}';
            httpRes.setBody(resBodyString);
            httpRes.setStatusCode(2000);
            System.assert(httpRes.getBody() == resBodyString);
            
            string reqType = 'Continuation-1';        
            List<string>labelGetmember = new List<string>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,httpRes);
            
            ArchivedCaseDetail_LC_HUM.parseCaseDetails(labelGetmember,continueObj);
            Test.stopTest();
       	}

    }

    @isTest 
    static void testFetchCaseDetailError()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        MemberPlan memberPlanObj = [select id,Name,PlanId from MemberPlan where Name = 'MemberPlan1' limit 1];
        Account accountObj = [Select id,Name from Account limit 1];
        classification__c cl = [select id, name from Classification__c limit 1];
        Intent__c intentobj = [select id, name from Intent__c limit 1];

       	System.runAs(testUser)
        {
            
            Test.startTest();
            ArchivedCaseDetail_LC_HUM inquiryDetails = new ArchivedCaseDetail_LC_HUM();
            Continuation continueObj = (Continuation)ArchivedCaseDetail_LC_HUM.fetchCaseDetailData('1014741145','All','25','50');
            continueObj.continuationMethod = 'parseCaseDetails';
            HttpResponse httpRes = new Httpresponse ();
            String resBodyString = '{"CaseCommentsResponse":{"Header":{"sTotalRows":"26"},"CaseCommentsResponseData":[{"sId":"00a2h0000015i3uAAA","sParentId":"5002h000005MZsVAAW","sCreatedDate":"2022-05-31T06:55:46.000+0000","sCommentBody":"Sample","sCreatedById":"' + testUser.Id + '","sLastModifiedById":"0052h0000030XXjAAM","sLastModifiedDate":"2022-05-31T06:55:46.000+0000"}]},"CaseHistoryResponse":{"Header":{"sTotalRows":"25"},"CaseHistoryResponseData":[{"sId":"0172h0000Bn4je3AQA","sCaseId":"5002h000006atsiAAA","sCreatedById":"0052h000003I73FAAS","sCreatedDate":"2022-06-02T06:55:46.000+0000","sField":"Case_owner","sNewValue":"DentalCalls","sOldValue":"Calls"}]},"CaseDetailResponse":{"sId":"5002h000006pp5kAAA","sCaseNumber":"1000228871612","sOrigin":"InboundCall","sSubtype":"ABC","sType":"CustomerInquiry","sOwner_Queue":"HumanaPharmacyClinicalServices","sCase_Owner":"Ashish","sStatus":"InProgress","sRecordTypeId":"012F00000013LYXIA2","sCreatedDate":"2022-07-06T17:08:10.000Z","sInteracting_With_Type":"RumanBois","sInteracting_About_Type":"RumanBois","sAccountId":"'+accountObj.Id+'","sInteracting_With":"0012h00000f9VJAAA2","sClassification_Id":"'+cl.Id+'","sMember_Plan_Id":"'+memberPlanObj.Id+'","sIntent_Id":"'+intentobj.Id+'","sPriority":"Normal","sDoc_Type":"Doc","sDCN":"200601109032","sClassification_Type":"DelayedOrder","sTopic":"AVX","sCase_Age":"","sInteracting_With_Name":"","sG_A_Reason":"","sG_A_Rights_Given":"","sComplaint":"","sComplaint_Reason":"","sComplaint_Type":"","sLanguage_Preference":"","sOpenEnrollment":"true","sOpenEnrollmentType":"","sAccount_MedicareID":"","sFollow_up_Due_Date":"","sCase_Referred_To":"","sResponse_Status":"","sRe_Open_Case_Age":"","sRe_Open_Case_Date":"","sCreatedById":"' + testUser.Id + '","sLastModifiedById":"'+ testUser.Id +'","sCreated_By_Queue":"","sLastModifiedby_Queue":"","sWork_Queue_View_Name":"","sDCN_Type":"ddd","sDCN_URL":"https://int-loufeo1.humana.com/edocs/?ecn=0000005837681930010000100001&docid=20210C000000&doctype=P"},"CaseTasksResponse":{"Header":{"sTotalRows":"25"},"CaseTasksResponseData":[{"sId":"00T2h00000Er8EdEAJ","sTask_Number":"T79975291","sActivityDate":"2022-04-20","sType":"WorkTask","sStatus":"InProgress","sPriority":"Normal","sTask_Age":"123","sCreated_DateTime":"2022-05-31T06:55:46.000+0000","sDate_Time_Closed":"2022-05-31T06:55:46.000+0000","sTask_Owner":"mastere","sOwner_Queue":"LouisvilleRSOCallsSupport","sRecordTypeId":"012F00000013O82IAE"},{"sId":"00T2h00000Er8EdEAJ","sTask_Number":"T7997529","sActivityDate":"2022-06-20","sType":"WorkTask","sStatus":"InProgress","sPriority":"Normal","sTask_Age":"123","sCreated_DateTime":"2022-05-31T06:55:46.000+0000","sDate_Time_Closed":"2022-05-31T06:55:46.000+0000","sTask_Owner":"mastere","sOwner_Queue":"LouisvilleRSOCallsSupport","sRecordTypeId":"012F00000013O82IAE"}]}}';
            httpRes.setBody(resBodyString);
            httpRes.setStatusCode(500);
            System.assert(httpRes.getBody() == resBodyString);
            
            string reqType = 'Continuation-1';        
            List<string>labelGetmember = new List<string>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,httpRes);
            
            ArchivedCaseDetail_LC_HUM.parseCaseDetails(labelGetmember,continueObj);
            Test.stopTest();
       	}

    }

}