/*******************************************************************************************************************************
Apex Class Name : Public class MTVRemarks_Member_T_HUM
Version         : 1.0
Created On      : 04/05/2019   
Function        : This test class is used to cover MTVRemarks_Member_C_HUM and MTVRemarks_Member_S_HUM
Test Class      : MTVRemarks_Member_T_HUM
                 
Modification Log: 
*  Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*  Suraj patil                                        06/01/2019                  Original version
*  Abhishek Mangutkar								  05/05/2021				  User Story 2230063 : Humana constants Cleanup Claims and MTV Claim Remarks
*******************************************************************************************************************************************************************/
@isTest
public class MTVRemarks_Member_T_HUM 
{
    Public Static final String PHARMACYSPECIALIST_PROFILE_HUM = 'Humana Pharmacy Specialist';
    /*
    * Method Name : setUpTestData
    * Description : This method creates test data for test class
    * Return type : None
    * Parameter   : None
    */
    @testSetup 
    static void setUpTestData()
    {
        User objUser = HUMTestDataHelperTest.createTestUser('User1',PHARMACYSPECIALIST_PROFILE_HUM);   
        insert objUser;

        system.runAs(objUser)
        {
            // Insert Member Account
            Account objMemAccount = HUMTestDataHelperTest.getAccount();
            objMemAccount.FirstName = 'Test FName';
            objMemAccount.LastName = 'LName'; 
            objMemAccount.source_platform_code__c = 'EM' ;
            objMemAccount.Enterprise_Id__c = String.valueof(2000);
            objMemAccount.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            objMemAccount.Birthdate__c = String.valueOf(System.today().month()+'/'+system.today().day()+'/'+System.today().year());
            insert objMemAccount;
            system.assertEquals(objMemAccount.LastName == 'LName', true);

            // Policy
            Policy__c oMedicalPolicy = HUMTestDataHelperTest.getPolicy();
            oMedicalPolicy.Name = 'Medical Product';
            oMedicalPolicy.source_cust_cov_key__c = '12345678901234567890' ; 
            oMedicalPolicy.Group_Name__c = objMemAccount.Id;
            oMedicalPolicy.Major_LOB__c ='EPO';          
            insert oMedicalPolicy;
            system.assertEquals(oMedicalPolicy.Name == 'Medical Product', true);

            Policy__c oDentalPolicy = HUMTestDataHelperTest.getPolicy();
            oDentalPolicy.Name = 'Dental Product';
            oDentalPolicy.source_cust_cov_key__c = '12345678901234567890' ; 
            oDentalPolicy.Group_Name__c = objMemAccount.Id;
            oDentalPolicy.Major_LOB__c ='EPO';          
            insert oDentalPolicy;

             //Medical Product
            Product2 objMedProduct = new Product2();
            objMedProduct.Name = 'MED';
            objMedProduct.Product_Type__c = 'Med';
            objMedProduct.productCode = 'MED';
            insert objMedProduct;
            system.assert(objMedProduct.Product_Type__c == 'Med');


            // Policy Member
            Policy_Member__c objMedPolMem = HUMTestDataHelperTest.getPolicyMember();
            objMedPolMem.Member__c = objMemAccount.Id;
            objMedPolMem.name = '12591';
            objMedPolMem.Policy__c = oMedicalPolicy.Id;
            objMedPolMem.Relationship__c = 'self';    
            objMedPolMem.end_date__c = '12/31/2015'; 
            objMedPolMem.Effective_Date__c='01/01/2015';       
            objMedPolMem.Policy_Member_External_ID__c = '00|ODS|123456789|89|89|02-01-2001';
            insert objMedPolMem;
            system.assertEquals(objMedPolMem.Relationship__c == 'self',true);          
  
            HUM_Webservice_Callout__c owebService = new HUM_Webservice_Callout__c();
            owebService.Name = 'GetMTVRemarks';
            owebService.Active__c = true;
            owebService.Request_Type__c = 'POST';
            owebService.End_Point_URL__c = 'https://int-crmservicews.humana.com/MTVRemarksService_EE/GetMTVRemarks';
            owebService.Timeout__c = '12000';
            owebService.Certificate_Name__c = 'Humana_GuidanceCare';
            insert owebService;
        }
            User objUser2 = HUMTestDataHelperTest.createTestUser('User2', 'Customer Care Specialist');
            insert objUser2;
    }
    
     /*
    * Method Name : initiateMTVRequestTest
    * Functionality : initiateMTVRequestTest is to test call service for getting remarks from 'GetMTVRemarks' service.
    * Return type : NA.
    */ 
    @isTest 
    static void initiateMTVRequestTest()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        policy_member__c policyObj = [select id,name, member__c, end_date__c, Effective_Date__c,  Policy__r.Product__r.ProductCode, Policy__r.Major_LOB__c, policy__r.source_cust_cov_key__c, policy__r.Coverage_Plan_Effective_Date__c, policy__r.Coverage_Plan_End_Date__c from policy_member__c where name = '12591' limit 1];
        System.runAs(testUser)
        {
            Test.startTest();
            PolicyMemberDetail_DTO_HUM dtoObj = new PolicyMemberDetail_DTO_HUM();
            dtoObj.policyMemberID = policyObj.id;
            dtoObj.policyID = policyObj.Policy__c;
            dtoObj.status = 'Active';
            dtoObj.endDate = policyObj.end_date__c;
            dtoObj.soldProductKeyValue = 'test';
            dtoObj.groupMemberNumber = '176366'; 
            MTVRemarks_Member_C_HUM mtvclass = new MTVRemarks_Member_C_HUM();
            mtvclass.sPolicyMemberName='testname';
            Continuation continueObj = (Continuation)MTVRemarks_Member_C_HUM.initiateMTVRequest(dtoObj.policyMemberID);
            HttpResponse httpRes = new Httpresponse ();
            String resBodyString ='{ "MTVRemarksResponse":{ "ResponseStatus":{ "StatusCode":"0", "StatusDescription":"SUCCESS", "ErrorMessage":"" }, "RemarkCount":"4", "IdType":"Member", "IdentifierId":"000005027", "SubIdentifier":"01", "RemarkList":{ "Remarks":[ { "RemarkType":"TMID", "RemarkCategory":"", "CreatedDate":"2017-09-21", "RemarkText":"0051678525512" }, { "RemarkType":"TMID", "RemarkCategory":"", "CreatedDate":"2018-12-11", "RemarkText":"0053811233293" }, { "RemarkType":"TMID", "RemarkCategory":"", "CreatedDate":"2018-09-16", "RemarkText":"0053343889311" }, { "RemarkType":"TMID", "RemarkCategory":"", "CreatedDate":"2017-05-23", "RemarkText":"0051185913909" } ] } } }';
            httpRes.setBody(resBodyString);
            system.assert(httpRes.getBody() == resBodyString);         
            string reqType = '';
            List<string>labelGetmember = new List<string>();
            List<string>labelGetmemberEmpty = New List<string>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,httpRes);
            MTVRemarks_Member_C_HUM.processMTVResponse(labelGetmember,continueObj);
            MTVRemarks_Member_C_HUM.processMTVResponse(labelGetmemberEmpty,continueObj);
            Test.stopTest();
            
        }
    }
    
    /*
    * Method Name : testMTVDTOResponse
    * Functionality : Test method for MTVRemarksServiceGetMemberResponse_H_HUM .
    * Return type : NA.
    */  
    @isTest
    static void testMTVDTOResponse()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser)
        {
            Test.StartTest();
            MTVRemarksServiceGetMemberResponse_H_HUM oResponse = new MTVRemarksServiceGetMemberResponse_H_HUM();
            MTVRemarksServiceGetMemberResponse_H_HUM.MTVRemarksResponse oMTVreponse = new MTVRemarksServiceGetMemberResponse_H_HUM.MTVRemarksResponse();
            List<MTVRemarksServiceGetMemberResponse_H_HUM.RemarkList> oremarklst = new List<MTVRemarksServiceGetMemberResponse_H_HUM.RemarkList>();
            MTVRemarksServiceGetMemberResponse_H_HUM.Remarks oremarks = new MTVRemarksServiceGetMemberResponse_H_HUM.Remarks();
            MTVRemarksServiceGetMemberResponse_H_HUM.ResponseStatus orespstatus = new MTVRemarksServiceGetMemberResponse_H_HUM.ResponseStatus();
            system.assert(oResponse!=null);
            Test.StopTest();
        }
    }  
    
}