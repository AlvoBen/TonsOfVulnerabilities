/****************************************************************************
Apex Class Name  : AuthReferralReqGuidedProcess_T_HUM
Version          : 1.0 
Created On       : 05/11/2016
Function         : Test class to AuthReferralReqGuidedProcess_C_HUM Apex class

Modification Log :
* Developer Name              Code Review                   Date                       Description
*-----------------------------------------------------------------------------------------------------------------------
* Praveen Kumar Parimi          27942                      05/11/2016                 Original Version       
* Riya Agarwal                                         	   01/13/2021                 REQ - 1835131 - PR00095212- MF1 -SF -Auto-Populate Custom Benefit Grid 
*************************************************************************************************************************/
@isTest
private class AuthReferralReqGuidedProcess_T_HUM 
{
    @testSetup
    static void setUpTestData()
    {
        User testUser = HUMTestDataHelperTest.createTestUser('User2','Customer Care Specialist');
        insert testUser;
        System.assertEquals(testUser.lastname=='test User2',true);
        System.runAs(testUser)
        {
            
            Account objMemAccount = HUMTestDataHelperTest.getAccount();
            objMemAccount.FirstName = 'Test FName';
            objMemAccount.LastName = 'LName'; 
            objMemAccount.Enterprise_Id__c = String.valueof(2000);
            objMemAccount.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            objMemAccount.Birthdate__c = String.valueOf(System.today().month()+'/'+system.today().day()+'/'+System.today().year());
            objMemAccount.Mbr_Gen_key__c ='12323434434';
            insert objMemAccount;
            system.assert(objMemAccount.LastName == 'LName'); 
           
            //Medical Product
            Product2 objMedProduct = new Product2();
            objMedProduct.Name ='MED';
            objMedProduct.Product_Type__c = 'Med';
            objMedProduct.productCode = 'MED';
            objMedProduct.Sold_Product_Key_Value__c = 'MC3HH016';
            insert objMedProduct;
            System.assert(objMedProduct.Product_Type__c == 'Med');
           
            //Medical Policy
            Policy__c oMedicalPolicy = HUMTestDataHelperTest.getPolicy();
            oMedicalPolicy.Name = 'Medical Product';
            oMedicalPolicy.source_cust_cov_key__c = '12345678901234567890' ;
            oMedicalPolicy.Group_Name__c = objMemAccount.Id;
            oMedicalPolicy.Product__c = objMedProduct.Id;
            oMedicalPolicy.Major_LOB__c ='EPO';
            oMedicalPolicy.Platform__c = 'EM';//SC - Added   
            oMedicalPolicy.Benefit_Coverage__c = 'SFA03104';  //SC - Added
            oMedicalPolicy.Source_Cust_Cov_Key__c='abc';           
            insert oMedicalPolicy;
            System.assert(oMedicalPolicy.Name == 'Medical Product');
           
            // Policy Member
            Policy_Member__c objMedPolMem = HUMTestDataHelperTest.getPolicyMember();
            objMedPolMem.Name = 'PolicyMember1';
            objMedPolMem.Member__c = objMemAccount.Id;
            objMedPolMem.Policy__c = oMedicalPolicy.Id;
            objMedPolMem.Relationship__c = 'self';   
            objMedPolMem.end_date__c = '12/31/2015';
            objMedPolMem.Effective_Date__c='01/01/2015';      
            objMedPolMem.Policy_Member_External_ID__c = '00|ODS|123456789|89|89|02-01-2001';
            insert objMedPolMem;
            system.assertEquals(objMedPolMem.Relationship__c == 'self',true);
           
                        
        }
        
    }
    
    /*
     * Method Name : testGuidedProcessParams
     * Description : This is the test method which is used to test the AuthReferralReqGuidedProcess_C_HUM. 
     * Return type : Void
     * Paramater   : NA
     */     
    private static testMethod void testGuidedProcessParams()
    {
        Policy_Member__c polMember = [Select Id,Name,ETL_Record_Deleted__c, Source_Coverage_ID__c, Policy__c, Product__c, Product_Type__c, Member__c, ASO__c,Group_Number__c,Product_Type_Code__c
        From Policy_Member__c Where Name = 'PolicyMember1' limit 1];
        system.assert(polMember.name == 'PolicyMember1');

        Account oAccount = [Select Id, FirstName from Account where FirstName = 'Test FName' limit 1];
        system.assert(oAccount.FirstName == 'Test FName');

        HUM_Webservice_Callout__c ServiceDetails = new HUM_Webservice_Callout__c(Name = 'KMDocument', Certificate_Name__c = 'Test_Certificate', Active__c = true, End_Point_URL__c = 'https://int-crmservicews.humana.com/KMDocumentServiceREST/HasSearchResults', Timeout__c = '12000', Request_Type__c = 'POST', Content_Type__c = 'application/json');
        insert ServiceDetails;
        User u = [SELECT Id FROM User  WHERE Lastname='test User2' LIMIT 1];
        System.runAs(u)
        {
            Test.startTest();
            Test.setCurrentPage(Page.AuthReferralReqGuidedProcess_VF_HUM);
            ApexPages.currentPage().getParameters().put('id', polMember.id);
            ApexPages.currentpage().getParameters().put('pageName', 'authRequirementClaim');
            AuthReferralReqGuidedProcess_C_HUM authReq = new AuthReferralReqGuidedProcess_C_HUM();
            authReq.sRecId = polMember.id; 
            AuthReferralReqGuidedProcess_D_HUM.builtHttpRequest(ServiceDetails);
            authReq.MentorGridLaunch();
            Continuation continueObj = (Continuation)AuthReferralReqGuidedProcess_C_HUM.getMentorGrid(polMember.id);
            continueObj.continuationMethod = 'parseKMDResponse';
            continueObj.state = 'Both';
            HttpResponse httpRes = new Httpresponse (); 
            String resBodyString ='true';
            httpRes.setBody(resBodyString);
            System.assert(httpRes.getBody() == resBodyString);
            Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
            string reqType = '';
            for(String s : mapDetails.keyset())
            {
                reqType = s;
            } 
            List<string>labelGetmember = new List<string>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,httpRes);
            AuthReferralReqGuidedProcess_C_HUM.parseKMDResponse(labelGetmember,continueObj);
            
            MentorServiceRequest_DTO_HUM KDMRequestDTO = new MentorServiceRequest_DTO_HUM(); 
            MentorServiceResponse_DTO_HUM KMServiceResponse = new MentorServiceResponse_DTO_HUM();
            AuthReferralReqGuidedProcess_D_HUM.ParseStructureKMDocument(KMServiceResponse);
            authReq.getSubmissionData();
           authReq.getSubmissionID();
           authReq.getSubmissionvariable(); 
            Flow.Interview.AuthReferralRequirementFlow myflow;
            authReq.myflow = myflow;
            Test.stopTest();
        }
        
    }
}