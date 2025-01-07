/*******************************************************************************************************************************
Test Class Name : ArchivedCaseHistory_LT_HUM
Version         : 1.0
Created On      : 07/18/2022   
Function        : Test class of ArchivedCaseHistory_LC_HUM/LS classes.
                 
Modification Log: 
*  Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*  Ankima/Isha		                                   07/18/2022                  Original version
**********************************************************************************************************************************************************/

@isTest
public class ArchivedCaseHistory_LT_HUM {
     /*
* Method Name : setUpTestData
* Description : This method creates test data for test class
* Return type : None
* Parameter   : None
*/
@testSetup 
static void setUpTestData()
{
    HUM_Webservice_Callout__c inquiryDetailsService = new HUM_Webservice_Callout__c(Name = 'ArchivedCaseSearchService',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c = 'https://dev-appgw.humana.com/casearchival-search-api-dev/api/caseSearch/account' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json');
    insert inquiryDetailsService;     
    
    User objUser = HUMTestDataHelperTest.createTestUser('User1','System Administrator');   
    insert objUser; 
 
    
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
        insert objMemberplan; 
        system.assert(objMemberPlan.GroupNumber == '737803');
    
     // Insert Product
     Product2 objProduct = New Product2();
            objProduct.Name = 'MED';
            objProduct.Product_Type__c = 'test';
            insert objProduct;
            system.assert(objProduct.Product_Type__c == 'test');
    // Insert Policy
    Policy__c objPolicy = HUMTestDataHelperTest.getPolicy();
            objPolicy.Name = 'Test Policy';
            objPolicy.Group_Name__c = objMemAccount.Id;
            objPolicy.Product__c = objProduct.Id; 
            objPolicy.Policy_External_ID__c ='3232|3232|323|2323|232|23433'  ;        
            insert objPolicy;
            system.assert(objPolicy.Name == 'Test Policy');
            
     // Insert Policy Members
     Policy_Member__c objPolMem = HUMTestDataHelperTest.getPolicyMember();
            objPolMem.Member__c = objMemAccount.Id;
            objPolMem.Policy__c = objPolicy.Id;
            objPolMem.Relationship__c = 'self'; 
            objPolMem.name = '12345'; 
            objPolMem.end_date__c  = '12/31/9999'  ;  
            objPolMem.Effective_date__c  = '01/01/2014'  ;   
            objPolMem.Policy_Member_External_ID__c = '00|ODS|123456787|89|89|02-01-2001';
            insert objPolMem;
            system.assert(objPolMem.Relationship__c == 'self');
    
    HUMConstants__c objCSConstants = new HUMConstants__c(
          Name='HUMArchival_screenName',
          StringValue__c = 'ArchivalSearch');
          insert objCSConstants;
    HUMConstants__c objCSCharLimit = new HUMConstants__c(
          Name='HUMArchival_characterLimit',
          StringValue__c = '1000');
          insert objCSCharLimit;
    HUMConstants__c objCSClientId = new HUMConstants__c(
          Name='HUMArchival_clientId',
          StringValue__c = '6d16ad37e1154c65b2a878d1803d9dab');
          insert objCSClientId;
    HUMConstants__c objCSClientSecret = new HUMConstants__c(
          Name='HUMArchival_clientSecret',
          StringValue__c = '16e4f105e35b46B4AF983c0a2A3330df');
          insert objCSClientSecret;
    
  classification__c clObj = new classification__c();
        clObj.Name = 'Access To Care';
        insert clObj;
        
  Intent__c itObj = new Intent__c();
        itObj.Name = 'test intent';
        insert itObj;
    
  Classification_Type__c classObj = new Classification_Type__c();
    classObj.Name='Humana Pharmacy Calls';
    insert classObj;
    
    
  CTCI_Junction__c ctci = new CTCI_Junction__c();
    ctci.Classification__c = clObj.id;
    ctci.Intent__c = itObj.id;
    ctci.Log_Code__c ='test code';
    ctci.Classification_Type__c=classObj.id;
    insert ctci;
    
}

@isTest 
static void testFetchCaseDetail()
{
    User testUser = [Select Id,Name from User where LastName='test User1'];
    CTCI_Junction__c ctciObj = [Select id from CTCI_Junction__c where Log_Code__c='test code' ];
    Account accObj =[Select id,Name from Account where FirstName='Test FName'];
    System.runAs(testUser)
    {
        
        Test.startTest();
        ArchivedCaseHistory_LC_HUM inquiryDetails = new ArchivedCaseHistory_LC_HUM();
        Account accountObj = [select id from Account where LastName = 'LName' limit 1];
        Continuation continueObj = (Continuation)ArchivedCaseHistory_LC_HUM.getArchivedCaseDataResponse(accountObj.id,'09/26/2018','09/26/2020',1,50,true,'123456');
        continueObj.continuationMethod = 'parseArchivedResponse';
        HttpResponse httpRes = new Httpresponse ();
        String responseBody='{"CaseSearchResponse":{"Header":{"sTotalRows":"20","sOldestCaseDate":"2017-10-25T14:54:33.000+0000"},"CaseResults":[{"ssfid":"5002h000006PKnrAAG","sAccountId":"'+accObj.id+'","sCaseNumber":"1000181857699","sType":"CustomerInquiry","sOrigin":"WebChat","sPriority":"Normal","sCreatedDate":"2021-10-25T14:54:33.000+0000","sClosedDate":"2021-10-25T14:54:33.000+0000","sFollow_up_Due_Date":"2022-02-09","sStatus":"Close","sCTCI_List":"'+ctciObj.Id+'","sDCN":"No","sComplaint":"No","sInteracting_With":"'+accObj.id+'","sInteracting_With_Type":"Member","sInteracting_About":"JOHNDENLINGER","sOwner_Queue":"WebChat","sCreated_By_Queue":"WebChat","sLastModifiedby_Queue":"WebChat"}]}}';
        httpRes.setBody(responseBody);
        httpRes.setStatusCode(200);
        System.assert(httpRes.getBody() == responseBody);
        
        string reqType = 'Continuation-1';        
        List<string>labelGetmember = new List<string>();
        labelGetmember.add(reqType);
        Test.setContinuationResponse(reqType,httpRes);
        ArchivedCaseHistory_LC_HUM.fetchLabels();
        ArchivedCaseHistory_LC_HUM.parseArchivedResponse(labelGetmember,continueObj);
        Test.stopTest();
    }
    
    
    
}
@isTest 
static void testFetchCaseDetail1()
{
    User testUser = [Select Id,Name from User where LastName='test User1'];
    System.runAs(testUser)
    {
        
        Test.startTest();
        //fetching group 
        Group grp2 = [Select id from Group where DeveloperName='Contract_Protected_Data'];
        GroupMember grpMem2 = new GroupMember();
		grpMem2.UserOrGroupId = testUser.Id;
		grpMem2.GroupId = grp2.Id;
		Insert grpMem2;
       
        ArchivedCaseHistory_LC_HUM inquiryDetails = new ArchivedCaseHistory_LC_HUM();
        MemberPlan memPlan = [select id from MemberPlan where Name='MemberPlan1' limit 1];
        Continuation continueObj = (Continuation)ArchivedCaseHistory_LC_HUM.getArchivedCaseDataResponse(memPlan.id,'09/26/2018','09/26/2020',1,50,true,'123456');
        continueObj.continuationMethod = 'parseArchivedResponse';
        HttpResponse httpRes = new Httpresponse ();
        String responseBody='{"CaseSearchResponse":{"Header":{"sTotalRows":"20","sOldestCaseDate":"2017-10-25T14:54:33.000+0000"},"CaseResults":[{"ssfid":"5002h000006PKnrAAG","sAccountId":"001M000001FM8HNIA1","sCaseNumber":"1000181857699","sType":"CustomerInquiry","sOrigin":"WebChat","sPriority":"Normal","sCreatedDate":"2021-10-25T14:54:33.000+0000","sClosedDate":"2021-10-25T14:54:33.000+0000","sFollow_up_Due_Date":"2022-02-09","sStatus":"Close","sCTCI_List":"a2a2h000000gEhYAAU","sDCN":"No","sComplaint":"No","sInteracting_With":"0012h00000f9VJAAA2","sInteracting_With_Type":"Member","sInteracting_About":"JOHNDENLINGER","sOwner_Queue":"WebChat","sCreated_By_Queue":"WebChat","sLastModifiedby_Queue":"WebChat"}]}}';
        httpRes.setBody(responseBody);
        httpRes.setStatusCode(500);
        System.assert(httpRes.getBody() == responseBody);
        
        string reqType = 'Continuation-1';        
        List<string>labelGetmember = new List<string>();
        labelGetmember.add(reqType);
        Test.setContinuationResponse(reqType,httpRes);
        
        ArchivedCaseHistory_LC_HUM.parseArchivedResponse(labelGetmember,continueObj);
        Test.stopTest();
    }
    
}
@isTest 
static void testFetchCaseDetail2()
{
    User testUser = [Select Id,Name from User where LastName='test User1'];
    System.runAs(testUser)
    {
        
        Test.startTest();
        ArchivedCaseHistory_LC_HUM inquiryDetails = new ArchivedCaseHistory_LC_HUM();
        Account accountObj = [select id,Name from Account where LastName = 'LName' limit 1];
        Continuation continueObj = (Continuation)ArchivedCaseHistory_LC_HUM.getArchivedCaseDataResponse(accountObj.id,'09/26/2018','09/26/2020',1,50,true,'');
        continueObj.continuationMethod = 'parseArchivedResponse';
        HttpResponse httpRes = new Httpresponse ();
        String responseBody='{"CaseSearchResponse":{"Header":{"sTotalRows":"20","sOldestCaseDate":"2017-10-25T14:54:33.000+0000"},"CaseResults":[{"ssfid":"5002h000006PKnrAAG","sAccountId":"'+accountObj.id+'","sCaseNumber":"1000181857699","sType":"CustomerInquiry","sOrigin":"WebChat","sPriority":"Normal","sCreatedDate":"2021-10-25T14:54:33.000+0000","sClosedDate":"2021-10-25T14:54:33.000+0000","sFollow_up_Due_Date":"2022-02-09","sStatus":"Close","sCTCI_List":"a2a2h000000gEhYAAU","sDCN":"No","sComplaint":"No","sInteracting_With":"'+accountObj.id+'","sInteracting_With_Type":"Member","sInteracting_About":"JOHNDENLINGER","sOwner_Queue":"WebChat","sCreated_By_Queue":"WebChat","sLastModifiedby_Queue":"WebChat"}]}}';
        httpRes.setBody(responseBody);
        httpRes.setStatusCode(2000);
        System.assert(httpRes.getBody() == responseBody);
        
        string reqType = 'Continuation-1';        
        List<string>labelGetmember = new List<string>();
        labelGetmember.add(reqType);
        Test.setContinuationResponse(reqType,httpRes);
        ArchivedCaseHistory_LC_HUM.fetchLabels();
        ArchivedCaseHistory_LC_HUM.parseArchivedResponse(labelGetmember,continueObj);
        Test.stopTest();
    }
    
}
@isTest 
static void testFetchCaseDetail3()
{
    User testUser = [Select Id,Name from User where LastName='test User1'];
    System.runAs(testUser)
    {
        
        Test.startTest();
        //fetching group 
        Group grp2 = [Select id from Group where DeveloperName='Contract_Protected_Data'];
        Group grp3 = [Select id from Group where DeveloperName='DST'];
        Group grp4 = [Select id from Group where DeveloperName='Home_Office_All'];
        Group grp5 = [Select id from Group where DeveloperName='Home_Office_Medical_Pharmacy'];
        
     
        GroupMember grpMem2 = new GroupMember();
		grpMem2.UserOrGroupId = testUser.Id;
		grpMem2.GroupId = grp2.Id;
		Insert grpMem2;
        
       GroupMember grpMem3 = new GroupMember();
		grpMem3.UserOrGroupId = testUser.Id;
		grpMem3.GroupId = grp3.Id;
		Insert grpMem3;
        
        GroupMember grpMem4 = new GroupMember();
		grpMem4.UserOrGroupId = testUser.Id;
		grpMem4.GroupId = grp4.Id;
		Insert grpMem4;
        
        GroupMember grpMem5 = new GroupMember();
		grpMem5.UserOrGroupId = testUser.Id;
		grpMem5.GroupId = grp5.Id;
		Insert grpMem5;
        
        ArchivedCaseHistory_LC_HUM inquiryDetails = new ArchivedCaseHistory_LC_HUM();
        MemberPlan memPlan = [select id from MemberPlan where Name='MemberPlan1' limit 1];
        ArchivedCaseHistory_LC_HUM.getArchivedCaseDataResponse(memPlan.id,'09/26/2018','09/26/2020',1,50,true,'123456');
        Test.stopTest();
    }
    
}
    @isTest 
static void testFetchCaseDetail4()
{
    User testUser = [Select Id,Name from User where LastName='test User1'];
    System.runAs(testUser)
    {
        
        Test.startTest();
        //fetching group 
        Group grp2 = [Select id from Group where DeveloperName='Contract_Protected_Data'];
        Group grp3 = [Select id from Group where DeveloperName='DST'];
        Group grp4 = [Select id from Group where DeveloperName='Home_Office_All'];
        Group grp5 = [Select id from Group where DeveloperName='LV_Medicare_Nonstandard_Claims_PGQ'];
        
     
        GroupMember grpMem2 = new GroupMember();
		grpMem2.UserOrGroupId = testUser.Id;
		grpMem2.GroupId = grp2.Id;
		Insert grpMem2;
        
       GroupMember grpMem3 = new GroupMember();
		grpMem3.UserOrGroupId = testUser.Id;
		grpMem3.GroupId = grp3.Id;
		Insert grpMem3;
        
        GroupMember grpMem4 = new GroupMember();
		grpMem4.UserOrGroupId = testUser.Id;
		grpMem4.GroupId = grp4.Id;
		Insert grpMem4;
        
        GroupMember grpMem5 = new GroupMember();
		grpMem5.UserOrGroupId = testUser.Id;
		grpMem5.GroupId = grp5.Id;
		Insert grpMem5;
        
        GroupMember grpMem6 = new GroupMember();
		grpMem6.UserOrGroupId = grp5.Id;
		grpMem6.GroupId = grp4.Id;
		Insert grpMem6;
        
        ArchivedCaseHistory_LC_HUM inquiryDetails = new ArchivedCaseHistory_LC_HUM();
        Policy_Member__c policyMem = [select id from Policy_Member__c where Relationship__c='self' limit 1];
        ArchivedCaseHistory_LC_HUM.getArchivedCaseDataResponse(policyMem.id,'09/26/2018','09/26/2020',1,50,true,'123456');
        Test.stopTest();
    }
    
}

}