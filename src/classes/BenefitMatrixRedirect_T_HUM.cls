/*******************************************************************************************************************************
Apex Class Name : BenefitMatrixRedirect_T_HUM.cls
Version         : 1.0
Created On      : 2016
Function        : Class contains test methods for the BenefitMatrix redirect page.

Modification Log: 
 * Developer Name            Code Review                Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Piyusha Babar                                 	  06/10/2017                 Original Version
 ********************************************************************************************************************************/
@isTest
private class  BenefitMatrixRedirect_T_HUM 
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
            HUMCertificateURL__c MentorRedirectURL = new HUMCertificateURL__c(Name = 'MentorRedirectURL',CertificateListURL__c = 'http://mentorsearch.humana.com/web/mentor.aspx/Search');
            insert MentorRedirectURL;
            system.assert(HUMCertificateURL__c.getValues('MentorRedirectURL') != NULL);
            
            HUMCertificateURL__c HSSRedirectURL = new HUMCertificateURL__c(Name = 'HSSRedirectURL',CertificateListURL__c = 'https://hss.humana.com/ITSecurity/HSSOneClick/LaunchHSS.aspx?HIDDENTARGET=');
            insert HSSRedirectURL;
            system.assert(HUMCertificateURL__c.getValues('HSSRedirectURL') != NULL);
                  
                        
            // Insert Member Account
            Account objMemAccount = HUMTestDataHelperTest.getAccount();
            objMemAccount.FirstName = 'Test FName';
            objMemAccount.LastName = 'LName'; 
            objMemAccount.source_platform_code__c = 'CAS' ;
            objMemAccount.Enterprise_Id__c = String.valueof(2000);
            objMemAccount.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            objMemAccount.Birthdate__c = String.valueOf(System.today().month()+'/'+system.today().day()+'/'+System.today().year());
            insert objMemAccount;
            system.assert(objMemAccount.LastName == 'LName');
            
         
            /*********************************** Medical and Platform = CAS *************************************/
            //Medical Product
            Product2 objMedProduct = new Product2();
            objMedProduct.Name = 'MED';
            objMedProduct.Product_Type__c = 'CAS';
            objMedProduct.productCode = 'MED';
            insert objMedProduct;
            system.assert(objMedProduct.Product_Type__c == 'CAS');
            
            //Medical Policy
            Policy__c oMedicalPolicy = HUMTestDataHelperTest.getPolicy();
            oMedicalPolicy.Name = 'Medical Product';
            oMedicalPolicy.Group_Name__c = objMemAccount.Id;
            oMedicalPolicy.Product__c = objMedProduct.Id;  
            oMedicalPolicy.Platform__c = 'CAS';  
            insert oMedicalPolicy;
            system.assert(oMedicalPolicy.Name == 'Medical Product');
            
            //Medical Policy Member
            Policy_Member__c objMedPolMem = HUMTestDataHelperTest.getPolicyMember();
            objMedPolMem.Member__c = objMemAccount.Id;
            objMedPolMem.Policy__c = oMedicalPolicy.Id;
           objMedPolMem.name = 'medpolmem';
          	insert objMedPolMem;
                        
            /***********************************Medical And Platform = MTV *************************************/
            //Medical Product
            Product2 objMedProduct1 = New Product2();
            objMedProduct1.Name = 'MED';
            objMedProduct1.Product_Type__c = 'MTV';
            objMedProduct1.productCode = 'MED';
            insert objMedProduct1;
            system.assert(objMedProduct1.Product_Type__c == 'MTV');
            
            //Medical Policy
            Policy__c oMedicalPolicy1 = HUMTestDataHelperTest.getPolicy();
            oMedicalPolicy1.Name = 'Medical Product';
            oMedicalPolicy1.Group_Name__c = objMemAccount.Id;
            oMedicalPolicy1.Product__c = objMedProduct1.Id;  
            oMedicalPolicy1.Platform__c = 'MTV';    
            insert oMedicalPolicy1;
            system.assert(oMedicalPolicy1.Name == 'Medical Product');
            
            //Medical Policy Member
            Policy_Member__c objMedPolMem1 = HUMTestDataHelperTest.getPolicyMember();
            objMedPolMem1.Member__c = objMemAccount.Id;
            objMedPolMem1.Policy__c = oMedicalPolicy1.Id;
            objMedPolMem1.name = 'medpolmem1';
            insert objMedPolMem1;
       }
    }  
    
    /*
    * Method Name : testQueryPolicyMemberRec
    * Description : This method is used to test the BenefitMatrix Page Redirect funtionality for medicare policy
    * Return type : None
    * Parameter   : None
    */
    static testMethod void testMedicareCAS()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        policy_member__c policyObj = [Select Id ,Product__c ,Policy_Platform__c,Product_Type__c,Policy__r.Contract_Number__c,Policy__r.PBP_Code__c,
                             Policy__r.Benefit_coverage__c, Policy__r.Group_Number__c
                             From Policy_Member__c 
                             where name = 'medpolmem' limit 1];
      
        System.runAs(testUser)
        {
            Test.startTest();
                Test.setCurrentPage(page.BenefitMatrixRedirect_VF_HUM);
                ApexPages.currentPage().getParameters().put('Id',policyObj.Id);
                BenefitMatrixRedirect_C_HUM benefit = new BenefitMatrixRedirect_C_HUM();
                benefit.benefitMatrixRec();
                system.assert(policyObj.Policy_Platform__c == 'CAS');
            Test.stopTest();
        }
    }
    
    /*
    * Method Name : testQueryPolicyMemberRec
    * Description : This method is used to test the BenefitMatrix Page Redirect funtionality for medicare policy
    * Return type : None
    * Parameter   : None
    */
    static testMethod void testMedicareMTV()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        policy_member__c policyObj = [Select Id ,Product__c ,Policy_Platform__c,Product_Type__c,Policy__r.Contract_Number__c,Policy__r.PBP_Code__c,
                             Policy__r.Benefit_coverage__c, Policy__r.Group_Number__c,Policy__r.Product__r.Name
                             From Policy_Member__c 
                             where name = 'medpolmem1' limit 1];
       
        System.runAs(testUser)
        {
            Test.startTest();
                Test.setCurrentPage(page.BenefitMatrixRedirect_VF_HUM);
                ApexPages.currentPage().getParameters().put('Id',policyObj.Id);
                BenefitMatrixRedirect_C_HUM benefit = new BenefitMatrixRedirect_C_HUM();
                benefit.benefitMatrixRec();
                system.assert(policyObj.Policy_Platform__c == 'MTV');
            Test.stopTest();
        }
    }
      
}