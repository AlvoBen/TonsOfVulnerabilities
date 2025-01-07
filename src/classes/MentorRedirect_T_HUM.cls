/*******************************************************************************************************************************
Apex Class Name : MentorRedirect_T_HUM.cls
Version         : 1.0
Created On      : 2016
Function        : Class contains test methods for the mentor redirect page.

Modification Log: 
 * Developer Name            Code Review                Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Shreya Choodamani                                  05/06/2016                 Original Version
 * Apoorv Jain                                        08/25/2016                 REQ - 269936 : VOB - Mentor Link for All segments
 * Vishal Verma		                                  05/29/2018			     REQ - 362119 - Group Benefit Tab – Access Benefit Link
 ********************************************************************************************************************************/
@isTest
private class  MentorRedirect_T_HUM 
{
    /*
    * Method Name : setUpTestData
    * Description : This method creates test data for test class
    * Return type : None
    * Parameter   : None
    */
    @testSetup static void setUpTestData()
    {
        User objUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert objUser;
        system.assertEquals(objUser.lastname=='test User1',true);
        system.runAs(objUser)
        {
            HUMCertificateURL__c MentorCertificateURL = new HUMCertificateURL__c(Name = 'MentorRedirectURL',CertificateListURL__c = 'http://test-mentorsearch.humana.com/web/mentor.aspx/Search');
            insert MentorCertificateURL;
            system.assert(HUMCertificateURL__c.getValues('MentorRedirectURL') != NULL);
            HUMCertificateURL__c HSSRedirectURL = new HUMCertificateURL__c(Name = 'HSSRedirectURL',CertificateListURL__c = 'https://hss.humana.com/ITSecurity/HSSOneClick/LaunchHSS.aspx?HIDDENTARGET=');
            insert HSSRedirectURL;
            system.assert(HUMCertificateURL__c.getValues('HSSRedirectURL') != NULL);
            
            PermissionSet ps = [SELECT Id FROM PermissionSet WHERE Name = 'CRMS_520_Benefits_Read_Only'];
            PermissionSetAssignment psa = new PermissionSetAssignment();
            psa.AssigneeId = objUser.Id;
            psa.PermissionSetId = ps.Id;
            insert psa;
            
            //Insert Reference object
            Reference_Business_Line_Product_Grouping__c oref = new Reference_Business_Line_Product_Grouping__c();
            oref.ProductCode__c = 'MED';
            oref.Major_LOB__c = 'PDP';
            oref.Business_Description__c = 'RSO- Medicare';
            insert oref;            
                        
            // Insert Member Account
            Account objMemAccount = HUMTestDataHelperTest.getAccount();
            objMemAccount.FirstName = 'Test FName';
            objMemAccount.LastName = 'LName'; 		
            objMemAccount.source_platform_code__c = 'EM' ;
            objMemAccount.Enterprise_Id__c = String.valueof(2000);
            objMemAccount.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            objMemAccount.Birthdate__c = String.valueOf(System.today().month()+'/'+system.today().day()+'/'+System.today().year());
            insert objMemAccount;
            system.assert(objMemAccount.LastName == 'LName');
            
            // Insert Member Account
            Account objMemAccountLV = HUMTestDataHelperTest.getAccount();
            objMemAccountLV.FirstName = 'Test FName';
            objMemAccountLV.LastName = 'LName2'; 
            objMemAccountLV.source_platform_code__c = 'LV' ;
            objMemAccountLV.Enterprise_Id__c = String.valueof(2001);
            objMemAccountLV.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            objMemAccountLV.Birthdate__c = String.valueOf(System.today().month()+'/'+system.today().day()+'/'+System.today().year());
            insert objMemAccountLV;
            system.assert(objMemAccountLV.LastName == 'LName2');
            
            Account objGrpAccount = HUMTestDataHelperTest.getAccount();
            objGrpAccount.Name = 'New Case Account';
            objGrpAccount.BillingState = 'New York';
            objGrpAccount.Enterprise_Id__c = String.valueof(4000);
            objGrpAccount.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
            insert objGrpAccount;
            system.assert(objGrpAccount.BillingState == 'New York');
            
            /*********************************** Medical *************************************/
            //Medical Product
            Product2 objMedProduct = new Product2();
            objMedProduct.Name = 'MED';
            objMedProduct.Product_Type__c = 'PDP';
            objMedProduct.productCode = 'MED';
            insert objMedProduct;
            system.assert(objMedProduct.Product_Type__c == 'PDP');
            
            //Medical Policy
            Policy__c oMedicalPolicy = HUMTestDataHelperTest.getPolicy();
            oMedicalPolicy.Name = 'Medical Product';
            oMedicalPolicy.source_cust_cov_key__c = '12345678901234567890' ; 
            oMedicalPolicy.Group_Name__c = objMemAccount.Id;
            oMedicalPolicy.Product__c = objMedProduct.Id;  
            oMedicalPolicy.Major_LOB__c ='PDP';    
            oMedicalPolicy.Contract_Number__c = '1234567890';
            oMedicalPolicy.PBP_Code__c = '12345';    
            insert oMedicalPolicy;
            system.assert(oMedicalPolicy.Name == 'Medical Product');
            
            //Medical Policy Member
            Policy_Member__c objMedPolMem = HUMTestDataHelperTest.getPolicyMember();
            objMedPolMem.Member__c = objMemAccount.Id;
            objMedPolMem.Policy__c = oMedicalPolicy.Id;
            objMedPolMem.Relationship__c = 'self';    
            objMedPolMem.name = 'medpolmem';
            objMedPolMem.end_date__c = '12/31/2015'; 
            objMedPolMem.Effective_Date__c='01/01/2015';       
            objMedPolMem.Policy_Member_External_ID__c = '00|ODS|123456789|89|89|02-01-2001';
            insert objMedPolMem;
            system.assert(objMedPolMem.Relationship__c == 'self');
            
            /***********************************Non -  Medical and Platform = MTV *************************************/
            //Medical Product
            Product2 objMedProduct1 = new Product2();
            objMedProduct1.Name = 'DEN';
            objMedProduct1.Product_Type__c = 'MTV';
            objMedProduct1.productCode = 'DEN';
            insert objMedProduct1;
            system.assert(objMedProduct1.Product_Type__c == 'MTV');
            
            //Medical Policy
            Policy__c oMedicalPolicy1 = HUMTestDataHelperTest.getPolicy();
            oMedicalPolicy1.Name = 'Medical Product1';
            oMedicalPolicy1.source_cust_cov_key__c = '12345678901234567890' ; 
            oMedicalPolicy1.Group_Name__c = objMemAccount.Id;
            oMedicalPolicy1.Product__c = objMedProduct1.Id;  
            oMedicalPolicy1.Platform__c ='MTV';    
            oMedicalPolicy1.Contract_Number__c = '1234567890';
            oMedicalPolicy1.PBP_Code__c = '12345';    
            insert oMedicalPolicy1;
            system.assert(oMedicalPolicy1.Name == 'Medical Product1');
            
            //Medical Policy Member
            Policy_Member__c objMedPolMem1 = HUMTestDataHelperTest.getPolicyMember();
            objMedPolMem1.Member__c = objMemAccount.Id;
            objMedPolMem1.Policy__c = oMedicalPolicy1.Id;
            objMedPolMem1.name = 'medpolmem1';
            objMedPolMem1.end_date__c = '12/1/2015'; 
            objMedPolMem1.Effective_Date__c='01/31/2015';       
            insert objMedPolMem1;
            
            /***********************************Non -  Medical and Platform = EM *************************************/
                      
            //Medical Policy
            Policy__c oMedicalPolicy2 = HUMTestDataHelperTest.getPolicy();
            oMedicalPolicy2.Name = 'Medical Produc21';
            oMedicalPolicy2.source_cust_cov_key__c = '12345678901234567890' ; 
            oMedicalPolicy2.Group_Name__c = objMemAccount.Id;
            oMedicalPolicy2.Product__c = objMedProduct1.Id;  
            oMedicalPolicy2.Platform__c ='LV';    
            oMedicalPolicy2.Contract_Number__c = '1234567890';
            oMedicalPolicy2.PBP_Code__c = '12345';    
            insert oMedicalPolicy2;
            system.assert(oMedicalPolicy2.Name == 'Medical Produc21');
            
            //Medical Policy Member
            Policy_Member__c objMedPolMem2 = HUMTestDataHelperTest.getPolicyMember();
            objMedPolMem2.Member__c = objMemAccount.Id;
            objMedPolMem2.Policy__c = oMedicalPolicy2.Id;
            objMedPolMem2.name = 'medpolmem2';
            objMedPolMem2.end_date__c = '12/1/2015'; 
            objMedPolMem2.Effective_Date__c='01/31/2015';       
            insert objMedPolMem2;
        }
    }  
    
    /*
    * Method Name : testMedicare
    * Description : This method is used to test the Mentor Page Redirect funtionality for medicare policy
    * Return type : None
    * Parameter   : None
    */
    static testMethod void testMedicare()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        policy_member__c policyObj = [Select Id ,Product__c ,Policy_Platform__c,Product_Type__c,Policy__r.Contract_Number__c,Policy__r.PBP_Code__c,
                             Policy__r.Benefit_coverage__c, Policy__r.Group_Number__c
                             From Policy_Member__c 
                             where name = 'medpolmem' limit 1];
      
        System.runAs(testUser)
        {
            Test.startTest();
                Test.setCurrentPage(page.MentorRedirect_VF_HUM);
                ApexPages.currentPage().getParameters().put('Id',policyObj.Id);
                MentorRedirect_C_HUM mentor = new MentorRedirect_C_HUM();
                mentor.queryPolicyMemberRec();
                system.assert(policyObj.Product_Type__c == 'PDP');
            Test.stopTest();
        }
    }
    
    /*
    * Method Name : testNonMedicareMTV
    * Description : This method is used to test the Mentor Page Redirect funtionality for non medicare MTV policy
    * Return type : None
    * Parameter   : None
    */
    static testMethod void testNonMedicareMTV()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        policy_member__c policyObj = [Select Id ,Product__c ,Policy_Platform__c,Product_Type__c,Policy__r.Contract_Number__c,Policy__r.PBP_Code__c,
                             Policy__r.Benefit_coverage__c, Policy__r.Group_Number__c,Policy__r.Product__r.Name
                             From Policy_Member__c 
                             where name = 'medpolmem1' limit 1];
       
        System.runAs(testUser)
        {
            Test.startTest();
                Test.setCurrentPage(page.MentorRedirect_VF_HUM);
                ApexPages.currentPage().getParameters().put('Id',policyObj.Id);
                MentorRedirect_C_HUM mentor = new MentorRedirect_C_HUM();
                mentor.queryPolicyMemberRec();
                system.assert(policyObj.Policy_Platform__c == 'MTV');
            Test.stopTest();
        }
    }
    
    /*
    * Method Name : testNonMedicareEM
    * Description : This method is used to test the Mentor Page Redirect funtionality for non medicare EM policy
    * Return type : None
    * Parameter   : None
    */
     static testMethod void testNonMedicareEM()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        policy_member__c policyObj = [Select Id ,Product__c ,Policy_Platform__c,Product_Type__c,Policy__r.Contract_Number__c,Policy__r.PBP_Code__c,
                             Policy__r.Benefit_coverage__c, Policy__r.Group_Number__c,Policy__r.Product__r.Name
                             From Policy_Member__c 
                             where name = 'medpolmem2' limit 1];
        
        System.runAs(testUser)
        {
            Test.startTest();
                Test.setCurrentPage(page.MentorRedirect_VF_HUM);
                ApexPages.currentPage().getParameters().put('Id',policyObj.Id);
                MentorRedirect_C_HUM mentor = new MentorRedirect_C_HUM();
                mentor.queryPolicyMemberRec();
                system.assert(policyObj.Policy_Platform__c == 'LV');                
            Test.stopTest();
        }
    }
    
    /*
    * Method Name : testPolicy
    * Description : This method is used to test the Mentor Page Redirect funtionality for policy records
    * Return type : None
    * Parameter   : None
    */
     static testMethod void testPolicy()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        Policy__c policyObj = [Select Id ,Product__r.Name ,Platform__c,Major_LOB__c,Contract_Number__c,PBP_Code__c,
	                                Benefit_coverage__c, Group_Number__c,Medicare_Segment_ID__c From Policy__c Where name = 'Medical Produc21' limit 1];
        
        System.runAs(testUser)
        {
            Test.startTest();
                Test.setCurrentPage(page.MentorRedirect_VF_HUM);
                ApexPages.currentPage().getParameters().put('PolId',policyObj.Id);
                MentorRedirect_C_HUM mentor = new MentorRedirect_C_HUM();
                mentor.queryPolicyMemberRec();
                system.assert(policyObj.PBP_Code__c == '12345');                
            Test.stopTest();
        }
    }  
}