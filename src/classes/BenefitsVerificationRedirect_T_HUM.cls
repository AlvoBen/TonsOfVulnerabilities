/*****************************************************************************************************************************************************
Apex Class Name :  BenefitsVerificationRedirect_T_HUM 
Version         : 1.0
Created On      : 10/21/2020
Function        : To Test BenefitsVerificationRedirect_C_HUM Functionality.
Test Class      : Test Class for BenefitsVerificationRedirect_C_HUM

Modification Log: 
* Developer Name           Code Review                 Date                        Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------
* Sayali Gavane                                        10/16/2020                   Original Version 
* Prashant Moghe									   08/20/2021					US- 1928764 - T1PRJ0002736 - 2021 MF6 - Security  - CR AEP readiness - Information Security Issues
********************************************************************************************************************************************************************************/
@isTest
private class BenefitsVerificationRedirect_T_HUM
{
    @testSetup static void setUpTestData()
    {
       
	User objUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
   insert objUser;
        System.assertEquals(objUser.lastname == 'test User1',true);
        System.runAs(objUser)
        {
            	// Insert Member Account
            Account objMemAccount = HUMTestDataHelperTest.getAccount();
            objMemAccount.FirstName = 'Test FName';
            objMemAccount.LastName = 'LName'; 
            objMemAccount.source_platform_code__c = 'LV' ;            
            objMemAccount.Enterprise_Id__c = '1000101';//String.valueof(2000);
            objMemAccount.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            objMemAccount.Birthdate__c = String.valueOf(System.today().month()+'/'+System.today().day()+'/'+System.today().year());
            insert objMemAccount;
            System.assertEquals(objMemAccount.LastName == 'LName', true);
            
            //Product
            Product2 oproduct = new Product2();
            oproduct.Name='MED';
            oproduct.Product_Type__c='MA';           
            insert oproduct;
            System.assertEquals(oproduct.Name == 'MED', true);
            
            // Policy
            Policy__c oMedicalPolicy = HUMTestDataHelperTest.getPolicy();
            oMedicalPolicy.Name = 'Medical Product';
            oMedicalPolicy.source_cust_cov_key__c = '12345678901234567890' ; 
            oMedicalPolicy.Group_Name__c = objMemAccount.Id;
            oMedicalPolicy.Major_LOB__c ='EPO';
            oMedicalPolicy.Platform__c='LV';
            oMedicalPolicy.Product__c=oproduct.Id;
            
            insert oMedicalPolicy;
            System.assertEquals(oMedicalPolicy.Name == 'Medical Product', true);
            
            // Policy Member
            Policy_Member__c objMedPolMem = HUMTestDataHelperTest.getPolicyMember();
            objMedPolMem.Member__c = objMemAccount.Id;
            objMedPolMem.Name = 'H123445500';
            objMedPolMem.Policy__c = oMedicalPolicy.Id;
            objMedPolMem.Relationship__c = 'self';    
            objMedPolMem.end_date__c = '12/31/2015'; 
            objMedPolMem.Effective_Date__c='01/01/2015';       
            objMedPolMem.Policy_Member_External_ID__c = '00|ODS|123456789|89|89|02-01-2001';            
            insert objMedPolMem;
            System.assertEquals(objMedPolMem.Relationship__c == 'self',true);
            
            Product2 oproduct2 = new Product2();
            oproduct2.Name='DEN';
            oproduct2.Product_Type__c='CA';           
            insert oproduct2;
            System.assertEquals(oproduct2.Name == 'DEN',true);
            
            Policy__c oMedicalPolicy2 = HUMTestDataHelperTest.getPolicy();
            oMedicalPolicy2.Name = 'Medical Product';
            oMedicalPolicy2.Group_Name__c = objMemAccount.Id;
            oMedicalPolicy2.Major_LOB__c ='EPO';
            oMedicalPolicy2.Platform__c='CB';
            oMedicalPolicy2.Product__c=oproduct2.Id;
            insert oMedicalPolicy2;
            System.assertEquals(oMedicalPolicy2.Platform__c == 'CB',true);
            
            Policy_Member__c objMedPolMem2 = HUMTestDataHelperTest.getPolicyMember();
            objMedPolMem2.Member__c = objMemAccount.Id;
            objMedPolMem2.Policy__c = oMedicalPolicy2.Id;
            objMedPolMem2.Relationship__c = 'not self';  
            objMedPolMem2.end_date__c = '12/31/2015'; 
            objMedPolMem2.Effective_Date__c='01/01/2015';       
            insert objMedPolMem2;
            System.assertEquals(objMedPolMem2.Relationship__c == 'not self',true);
                        
            Member_ID__c objMemId = new Member_ID__c();
            objMemId.Policy_Member__c=objMedPolMem.id;
            objMemId.Policy__c=oMedicalPolicy.id;
            objMemId.Name='12345';
            objMemId.Type__c='Member-Id-Base';
            insert objMemId;
            System.assertEquals(objMemId.Type__c == 'Member-Id-Base',true);
            
            List<Endpoint_Url_HUM__c> lstEndpointBenefits = new List<Endpoint_Url_HUM__c>();
        	lstEndpointBenefits.add(new Endpoint_Url_HUM__c(Name='Benefits_Verification_URL',Remote_Url__c='Sample url'));
        	insert lstEndpointBenefits;
        }
    }
    /*
     * Method Name   :    testcreatePageRef
     * Description   :    This method is for testing the Page Ref Method.
     * Return Type   :    void
     * Parameters    :    NA
    */           
     @isTest
    static void BenefitsVerificationRedirect_C_HUMTest()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        Policy_Member__c polMem = [SELECT id, Member__r.Enterprise_ID__c FROM Policy_Member__c where Name = 'H123445500'];
        System.runAs(testUser)
        {
            Test.StartTest();
            	PageReference pageRef = Page.BenefitsVerificationRedirect_VF;
            	Test.setCurrentPage(pageRef);            	
            	ApexPages.currentPage().getParameters().put('Id', polMem.id);
				System.assertEquals(polMem.id,ApexPages.currentPage().getParameters().get('Id'));
            	BenefitsVerificationRedirect_C_HUM benefitsRedirect = new BenefitsVerificationRedirect_C_HUM();
            	benefitsRedirect.redirectBenefitsVerification();
			Test.StopTest();
        }
    }
}