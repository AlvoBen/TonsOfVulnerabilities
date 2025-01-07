/*******************************************************************************************************************************
Apex Class Name :  CertificateRedirect_T_HUM.cls
Version: 1.0
Created On      : 05/06/2016
Function        : Class contains test methods CertificateRedirect Class.

Modification Log :
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Sharan Shanmugam              27081                 05/06/2016                Original version
* Vishal Verma                  28021                 05/20/2016                Defect 226893 : Added Refdate variable To be passed In MBE Request
* Vishal Verma                  29581                 06/07/2016                Defect 226893 : Removed the webservice coverage As it Is Not used any more.
* Gargey Sharma                 31430                 06/29/2016                Defect 226893 : Updated URL and removed testExceptions method as it did not cover anything
* Prudhvi Pamarthi              171453                01/22/2018                  REQ - 328929 Fix for HUMExceptionHelper: DML
* Melkisan Selvaraj				266572			      07/23/2018			    REQ - 361165 Certificate of Coverage Link
*************************************************************************************************************************************************/
@isTest

private Class CertificateRedirect_T_HUM
{
	/**
	* Method is used to create data in test class.
	*<p>
	* @param  NA
	* @return void
	*/
	@testSetup static void setUpTestData()
{
	User objUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
	insert objUser;
	system.assertEquals(objUser.lastname == 'test User1',true);
	system.runAs(objUser)
	{
		HUMCertificateURL__c certiURL = New HUMCertificateURL__c(Name = 'CertificateURL', CertificateListURL__c = 'https://qa-hss.humana.com/ITSecurity/HSSOneClick/LaunchHSS.aspx?HIDDENTARGET=https://qa-apps.humana.com/eCertNext/default.aspx');
		insert certiURL;
		system.assert(certiURL.Name == 'CertificateURL');
		PermissionSet ps = [SELECT Id FROM PermissionSet WHERE Name = 'CRMS_520_Benefits_Read_Only'];
		PermissionSetAssignment psa = New PermissionSetAssignment();
		psa.AssigneeId = objUser.Id;
		psa.PermissionSetId = ps.Id;
		insert psa;
		system.assert(psa.id != NULL);
		// Insert Member Account
		Account objMemAccount = HUMTestDataHelperTest.getAccount();
		objMemAccount.FirstName = 'Test FName';
		objMemAccount.LastName = 'LName';
		objMemAccount.source_platform_code__c = 'EM';
		objMemAccount.Enterprise_Id__c = String.valueof(2000);
		objMemAccount.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
		objMemAccount.Birthdate__c = String.valueOf(System.today().month() + '/' + system.today().day() + '/' + System.today().year());
		objMemAccount.Mbr_Gen_Key__c = '11111111111';
		insert objMemAccount;
		system.assert(objMemAccount.LastName == 'LName');

		Account objMemAccount1 = HUMTestDataHelperTest.getAccount();
		objMemAccount1.FirstName = 'FName';
		objMemAccount1.LastName = 'LastName';
		objMemAccount1.source_platform_code__c = 'EM';
		objMemAccount1.Enterprise_Id__c = String.valueof(20000);
		objMemAccount1.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
		objMemAccount1.Birthdate__c = String.valueOf(System.today().month() + '/' + system.today().day() + '/' + System.today().year());
		insert objMemAccount1;
		system.assert(objMemAccount1.LastName == 'LastName');

		//Medical Product
		Product2 objMedProduct = New Product2();
		objMedProduct.Name = 'MED';
		objMedProduct.Product_Type__c = 'Med';
		objMedProduct.productCode = 'MED';
		objMedProduct.Sold_Product_Key_Value__c = '9944940101';
		insert objMedProduct;
		system.assert(objMedProduct.Product_Type__c == 'Med');

		//Medical Policy
		Policy__c oMedicalPolicy = HUMTestDataHelperTest.getPolicy();
		oMedicalPolicy.Name = 'Medical Product';
		oMedicalPolicy.source_cust_cov_key__c = '12345678901234567890';
		oMedicalPolicy.Group_Name__c = objMemAccount.Id;
		oMedicalPolicy.Product__c = objMedProduct.Id;
		oMedicalPolicy.Major_LOB__c = 'EPO';
		oMedicalPolicy.Platform__c = 'LV';
		oMedicalPolicy.Business_Segment__c = 'LargeGroup-Commercial';
		oMedicalPolicy.Policy_External_ID__c = '00|ODS|123456789|89|89|02-01-2001';
		insert oMedicalPolicy;
		system.assert(oMedicalPolicy.Name == 'Medical Product');

		//Medical Policy Member
		Policy_Member__c objMedPolMem = HUMTestDataHelperTest.getPolicyMember();
		objMedPolMem.name = 'TestCertificateRedirect';
		objMedPolMem.Member__c = objMemAccount.Id;
		objMedPolMem.Policy__c = oMedicalPolicy.Id;
		objMedPolMem.Relationship__c = 'self';
		objMedPolMem.end_date__c = '12/31/2015';
		objMedPolMem.Effective_Date__c = '01/01/2015';
		objMedPolMem.Policy_Member_External_ID__c = '00|ODS|123456789|89|89|02-01-2001';
		insert objMedPolMem;
		system.assert(objMedPolMem.Relationship__c == 'self');


	}
}

	/**
	* Method Is used to test redirection to external application for Policy Member
	*<p>
	* @param NA
	* @return String
	*/
	@isTest static void testRedirectToCertificate()
	{
		User testUser = [Select Id, Name from User where LastName = 'test User1'];
		policy_member__c policyObj = [Select Id, Name, Member__c, Policy__r.Platform__c, Policy__r.Product__r.Sold_Product_Key_Value__c, Policy__r.Major_LOB_Frm__c, Policy__r.Business_Segment__c, Member__r.Enterprise_ID__c,
			Policy__r.Policy_External_ID__c, Subscriber__c, Effective_Date__c, End_Date__c from policy_member__c where name = 'TestCertificateRedirect' limit 1];
		Policy__c polObj = [Select Id ,Product__r.Name ,Platform__c,Major_LOB__c,Contract_Number__c,PBP_Code__c,Coverage_Plan_Effective_Date__c,Coverage_Plan_End_Date__c,
			Benefit_coverage__c, Group_Number__c,Medicare_Segment_ID__c, Product__r.Sold_Product_Key_Value__c, Major_LOB_Frm__c, Group_Name__r.Enterprise_Id__c, Policy_External_ID__c From Policy__c Where source_cust_cov_key__c = '12345678901234567890' LIMIT 1];
		System.runAs(testUser)
		{
			Test.startTest();
			ApexPages.currentPage().getParameters().put('id',policyObj.Id);
			CertificateRedirect_C_HUM certificateCLass = New CertificateRedirect_C_HUM();
			system.assert(certificateCLass.sURL != NULL);
			Test.stopTest();
		}
	}

	/**
	* Method Is used to test redirection to external application for Policy
	*<p>
	* @param NA
	* @return String
	*/
	@isTest static void testRedirectToCertificatePolicy()
	{
		User testUser = [Select Id, Name from User where LastName = 'test User1'];
		Policy__c polObj = [Select Id, Product__r.Name ,Platform__c,Major_LOB__c,Contract_Number__c,PBP_Code__c,Coverage_Plan_Effective_Date__c,Coverage_Plan_End_Date__c,
			Benefit_coverage__c, Group_Number__c,Medicare_Segment_ID__c, Product__r.Sold_Product_Key_Value__c, Major_LOB_Frm__c, Group_Name__r.Enterprise_Id__c, Policy_External_ID__c From Policy__c Where source_cust_cov_key__c = '12345678901234567890' LIMIT 1];
		System.runAs(testUser)
		{
			Test.startTest();
			ApexPages.currentPage().getParameters().put('PolId',polObj.Id);
			CertificateRedirect_C_HUM certificateCLass = New CertificateRedirect_C_HUM();
			system.assert(certificateCLass.sURL != NULL);
			Test.stopTest();
		}
	}
}