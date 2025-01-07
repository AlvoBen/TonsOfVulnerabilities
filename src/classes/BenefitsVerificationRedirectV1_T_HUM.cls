/*****************************************************************************************************************************************************
Apex Class Name :  BenefitsVerificationRedirectV1_T_HUM 
Version         : 1.1
Created On      : 04/14/2021
Function        : To Test BenefitsVerificationRedirect_C_HUM Functionality.
Test Class      : Test Class for BenefitsVerificationRedirect_C_HUM

Modification Log: 
* Developer Name           Code Review                 Date                        Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------
* Sayali Gavane                                        10/16/2020                   Original Version 
* Nirmal Garg                                          04/14/2021                   Created new version
********************************************************************************************************************************************************************************/
@isTest
private class BenefitsVerificationRedirectV1_T_HUM
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

            Case objCase = HUMTestDataHelperTest.getCase();
            objCase.AccountId = objMemAccount.Id;
            objCase.Interacting_With__c = objMemAccount.Id;
            insert objCase;
            system.assertNotEquals(objCase, null, 'Value not matched');
           
			Interaction__c objInt = HUMTestDataHelperTest.getInteraction();
            objInt.Interacting_With__c = objMemAccount.Id;
        	objInt.Interacting_With_type__c = 'Member';
        	insert objInt;
        	System.assertEquals(objInt.id!=null, true);
        	System.assertEquals(objInt.Interacting_With_type__c ,'Member');

        }
    }
    /*
     * Method Name   :    testcreatePageRef
     * Description   :    This method is for testing the Page Ref Method.
     * Return Type   :    void
     * Parameters    :    NA
    */           
     @isTest
    static void BenefitsVerificationRedirectV1_C_HUMTest()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        Policy_Member__c polMem = [SELECT id, Member__r.Enterprise_ID__c FROM Policy_Member__c where Name = 'H123445500'];
        System.runAs(testUser)
        {
            Test.StartTest();
            	PageReference pageRef = Page.BenefitsVerificationRedirectV1_VF;
            	Test.setCurrentPage(pageRef);            	
            	ApexPages.currentPage().getParameters().put('Id', polMem.id);
            	BenefitsVerificationRedirectV1_C_HUM benefitsRedirect = new BenefitsVerificationRedirectV1_C_HUM();
            	benefitsRedirect.redirectBenefitsVerification();
			Test.StopTest();
        }
    }
    /*
     * Method Name   :    testcreatePageRef
     * Description   :    This method is for testing the Page Ref Method.
     * Return Type   :    void
     * Parameters    :    NA
    */
    @isTest
    Static void BenefitsVerificationRedirectV1_C_HUMTestCase()
    {
        User testUser = [Select Id, Name from User where LastName='test User1'];
        Case oCase = [SELECT Id FROM Case LIMIT 1];
        System.runAs(testUser)
        {
            Test.StartTest();
            	PageReference pageRef = Page.BenefitsVerificationRedirectV1_VF;
            	Test.setCurrentPage(pageRef);            	
            	BenefitsVerificationRedirectV1_C_HUM benefitsRedirect = New BenefitsVerificationRedirectV1_C_HUM();
            	benefitsRedirect.sCaseId = oCase.Id;
            	benefitsRedirect.redirectBenefitsVerification();
			Test.StopTest();
        }
    }
     /*
     * Method Name   :    testcreatePageRef
     * Description   :    This method is for testing the Page Ref Method.
     * Return Type   :    void
     * Parameters    :    NA
    */       
    @isTest
    Static void BenefitsVerificationRedirectV1_C_HUMTestInt()
    {
        User testUser = [Select Id, Name from User where LastName='test User1'];
        Interaction__c oInt = [Select Id, Name from Interaction__c where Interacting_With_type__c='Member' limit 1];
        System.runAs(testUser)
        {
            Test.StartTest();
            	PageReference pageRef = Page.BenefitsVerificationRedirectV1_VF;
            	Test.setCurrentPage(pageRef);            	
            	BenefitsVerificationRedirectV1_C_HUM benefitsRedirect = New BenefitsVerificationRedirectV1_C_HUM();
            	benefitsRedirect.sInteractionId = oInt.Id;
            	benefitsRedirect.redirectBenefitsVerification();
			Test.StopTest();
        }
    }


    /*
     * Method Name   :    testcreatePageRef
     * Description   :    This method is for testing the Page Ref Method.
     * Return Type   :    void
     * Parameters    :    NA
    */           
    @isTest
    static void BenefitsVerificationRedirectV1_C_HUMTest1()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        Policy_Member__c polMem = [SELECT id, Member__r.Enterprise_ID__c FROM Policy_Member__c where Name = 'H123445500'];
        System.runAs(testUser)
        {
            Test.StartTest();
            	PageReference pageRef = Page.BenefitsVerificationRedirectV1_VF;
            	Test.setCurrentPage(pageRef);            	
            	ApexPages.currentPage().getParameters().put('Id', polMem.id);
            	BenefitsVerificationRedirectV1_C_HUM benefitsRedirect = new BenefitsVerificationRedirectV1_C_HUM();
                benefitsRedirect.decodeTabLink();
                System.assertEquals(null, benefitsRedirect.sDecodedTabUrl);
			Test.StopTest();
        }
    }

    /*
     * Method Name   :    testcreatePageRef
     * Description   :    This method is for testing the Page Ref Method.
     * Return Type   :    void
     * Parameters    :    NA
    */           
    @isTest
    static void BenefitsVerificationRedirectV1_C_HUMTest2()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        Policy_Member__c polMem = [SELECT id, Member__r.Enterprise_ID__c FROM Policy_Member__c where Name = 'H123445500'];
        System.runAs(testUser)
        {
            Test.StartTest();
            	PageReference pageRef = Page.BenefitsVerificationRedirectV1_VF;
            	Test.setCurrentPage(pageRef);            	
                ApexPages.currentPage().getParameters().put('Id', polMem.id);
            	BenefitsVerificationRedirectV1_C_HUM benefitsRedirect = New BenefitsVerificationRedirectV1_C_HUM();
             	benefitsRedirect.bPlanIdSwitch = true;
                benefitsRedirect.generateURL();
                System.assertEquals(null, benefitsRedirect.sCaseId);
                System.assertEquals(null, benefitsRedirect.sInteractionId);
                System.assertNotEquals(null, benefitsRedirect.sBaseURL);
			Test.StopTest();
        }
    }
     /*
     * Method Name   :    testcreatePageRef
     * Description   :    This method Is For testing the Page Ref Method.
     * Return Type   :    void
     * Parameters    :    NA
    */
    @isTest
    Static void BenefitsVerificationRedirectV1_C_HUMTest2Case()
    {
        User testUser = [Select Id, Name from User where LastName='test User1'];
        Case oCase = [SELECT Id FROM Case LIMIT 1];
        System.runAs(testUser)
        {
            Test.StartTest();
            	PageReference pageRef = Page.BenefitsVerificationRedirectV1_VF;
            	Test.setCurrentPage(pageRef);            	
            	BenefitsVerificationRedirectV1_C_HUM benefitsRedirect = New BenefitsVerificationRedirectV1_C_HUM();
            	benefitsRedirect.sCaseId = oCase.Id;
                benefitsRedirect.generateURL();
                System.assertEquals(null, benefitsRedirect.sInteractionId);
                System.assertNotEquals(null, benefitsRedirect.sBaseURL);
			Test.StopTest();
        }
    }
     /*
     * Method Name   :    testcreatePageRef
     * Description   :    This method Is For testing the Page Ref Method.
     * Return Type   :    void
     * Parameters    :    NA
    */
    @isTest
    Static void BenefitsVerificationRedirectV1_C_HUMTest2Int()
    {
        User testUser = [Select Id, Name from User where LastName='test User1'];
         Interaction__c oInt = [Select Id, Name from Interaction__c where Interacting_With_type__c='Member' limit 1];
        System.runAs(testUser)
        {
            Test.StartTest();
            	PageReference pageRef = Page.BenefitsVerificationRedirectV1_VF;
            	Test.setCurrentPage(pageRef);            	
            	BenefitsVerificationRedirectV1_C_HUM benefitsRedirect = New BenefitsVerificationRedirectV1_C_HUM();
            	benefitsRedirect.sInteractionId = oInt.Id;
                benefitsRedirect.generateURL();
                System.assertEquals(null, benefitsRedirect.sCaseId);
                System.assertNotEquals(null, benefitsRedirect.sBaseURL);
			Test.StopTest();
        }
    }
     /*
     * Method Name   :    testcreatePageRef
     * Description   :    This method Is For testing the Page Ref Method.
     * Return Type   :    void
     * Parameters    :    NA
    */
    @isTest
    Static void BenefitsVerificationRedirectV1_C_HUMTest3()
    {
        User testUser = [Select Id, Name from User where LastName='test User1'];
       // Policy_Member__c polMem = [SELECT id, Member__r.Enterprise_ID__c FROM Policy_Member__c where Name = 'H123445500'];
        System.runAs(testUser)
        {
            Test.StartTest();
            	PageReference pageRef = Page.BenefitsVerificationRedirectV1_VF;
            	Test.setCurrentPage(pageRef);            	
               // ApexPages.currentPage().getParameters().put('Id', polMem.id);           	
            	BenefitsVerificationRedirectV1_C_HUM benefitsRedirect = New BenefitsVerificationRedirectV1_C_HUM();
            	benefitsRedirect.bPlanIdSwitch = true;
                system.assertEquals('2017-01-01', benefitsRedirect.DateChangeFormat('01/01/2017'));
                system.assertEquals('', benefitsRedirect.DateChangeFormat('01-01-2017'));
                system.assertEquals('', benefitsRedirect.DateChangeFormat(''));
				system.assertEquals('9999-12-31', benefitsRedirect.DateChangeFormat('12/31/9999'));
				system.assertEquals('', benefitsRedirect.DateChangeFormat('17/31/9999'));
			Test.StopTest();
        }
    }
     /*
     * Method Name   :    testcreatePageRef
     * Description   :    This method Is For testing the Page Ref Method.
     * Return Type   :    void
     * Parameters    :    NA
    */
    @isTest
    Static void DecodeTabLinkTest()
    {
        User testUser = [Select Id, Name from User where LastName='test User1'];
        System.runAs(testUser)
        {
            Test.StartTest();
            	PageReference pageRef = Page.BenefitsVerificationRedirectV1_VF;
            	Test.setCurrentPage(pageRef);            	
        	
            	BenefitsVerificationRedirectV1_C_HUM benefitsRedirect = New BenefitsVerificationRedirectV1_C_HUM();
            	benefitsRedirect.sTabURL = 'Hello';
            	benefitsRedirect.decodeTabLink();
                system.assertEquals(benefitsRedirect.sTabURL, benefitsRedirect.sDecodedTabUrl);
				Test.StopTest();
        }
    }
}