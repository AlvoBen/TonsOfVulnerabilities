/*******************************************************************************************************************************
Apex Class Name : IndividualMedicareEoc_T_HUM.cls
Version         : 1.0
Created On      : 04/16/2018
Function        : Class contains test methods for the Individual Medicare Evidence of coverage redirect page.

Modification Log: 
 * Developer Name            Code Review                Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Dinesh Subramaniyan                                  04/16/2018                Original Version
 * Asish Behera                                         06/05/2018                REQ - 366095 -Add Individual Medicare Summary of Benefits link to the Member Policy Page (RF)
 ********************************************************************************************************************************/
@isTest
private class  IndividualMedicareEoc_T_HUM 
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
			objMedProduct.Name ='MED';
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
			oMedicalPolicy.Medicare_Segment_ID__c = '000';  
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


		}
	}  

	/*
	 * Method Name : testQueryPolicyMemberRec
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
			Test.setCurrentPage(page.IndividualMedicareEocRedirect_VF_HUM);
			ApexPages.currentPage().getParameters().put('Id',policyObj.Id);
			IndividualMedicareEoc_C_HUM InMeEOC = new IndividualMedicareEoc_C_HUM();
			InMeEOC.updateURLAndLaunch();

			//Invoke the method to verify updateURLAndLaunch of the class IndividualMedicareSob_C_HUM
			IndividualMedicareSobRedirect_C_HUM InMeSOB = new IndividualMedicareSobRedirect_C_HUM();
			InMeSOB.updateURLAndLaunch();
			Test.stopTest();
		}
	}

}