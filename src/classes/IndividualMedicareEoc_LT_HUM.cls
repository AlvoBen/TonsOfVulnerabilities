/*******************************************************************************************************************************
Apex Class Name : IndividualMedicareEoc_LT_HUM.cls
Version         : 1.0
Created On      : 18/01/2023
Function        : Class contains test methods for the Individual Medicare Evidence of coverage redirect page.

Modification Log:
 *   Developer                   Code Review             Date               Description
 * --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- 
 *  Vishal Shinde                                     18/01/2023           4167387- Implementation of Links & landing pages- Person account page and Medical Plan member page - Part2
 ************************************************************************************************************************************************************************************/

@isTest
private class  IndividualMedicareEoc_LT_HUM 
{
	/*
	 * Method Name : setUpTestData
	 * Description : This method creates test data for test class
	 * Return type : None
	 * Parameter   : None
	 */
	@testSetup static void setUpTestData()
	{
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u;
        system.assertEquals(u.Id != Null,true);
        system.assertEquals(u.Lastname == 'test User1',true);
        
        
		system.runAs(u)
		{
             Account acc1 = HUMTestDataHelperTest.getAccount();
             acc1.FirstName = 'Test Account';
			 acc1.LastName = 'Cases1';
			 acc1.source_platform_code__c = 'EM' ;
             acc1.Enterprise_Id__c = String.valueof(2000);
			 acc1.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
			 acc1.Birthdate__c = String.valueOf(System.today().month()+'/'+system.today().day()+'/'+System.today().year());
			 insert acc1;
			 system.assertEquals(acc1.Id != Null,true);
             system.assertEquals(acc1.FirstName == 'Test Account',true);
            
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
			oMedicalPolicy.Group_Name__c = acc1.Id;
			oMedicalPolicy.Product__c = objMedProduct.Id;  
			oMedicalPolicy.Major_LOB__c ='PDP';    
			oMedicalPolicy.Contract_Number__c = '1234567890';
			oMedicalPolicy.PBP_Code__c = '12345';  
			oMedicalPolicy.Medicare_Segment_ID__c = '000';  
			insert oMedicalPolicy;
			system.assert(oMedicalPolicy.Name == 'Medical Product');

			//Medical Policy Member
			Policy_Member__c objMedPolMem = HUMTestDataHelperTest.getPolicyMember();
			objMedPolMem.Member__c = acc1.Id;
			objMedPolMem.Policy__c = oMedicalPolicy.Id;
			objMedPolMem.Relationship__c = 'self';    
			objMedPolMem.name = 'medpolmem';
			objMedPolMem.end_date__c = '12/31/2015'; 
			objMedPolMem.Effective_Date__c='01/01/2015';       
			objMedPolMem.Policy_Member_External_ID__c = '00|ODS|123456789|89|89|02-01-2001';
			insert objMedPolMem;
			system.assert(objMedPolMem.Relationship__c == 'self'); 

            
            MemberPlan objMembplanOI1 = new MemberPlan();
            objMembplanOI1.Name='Name New';
            objMembplanOI1.MemberId=acc1.id;
            objMembplanOI1.Policy__c = oMedicalPolicy.Id;
            objMembplanOI1.Member_Dependent_Code__c = 'H1234567890';
            objMembplanOI1.Member_Id_base__c = 'test23456';
            objMembplanOI1.GroupNumber = '672534';
            objMembplanOI1.EffectiveFrom = Date.newInstance(2020, 01, 01);
            objMembplanOI1.EffectiveTo = Date.newInstance(1999,12, 31);
            insert objMembplanOI1;

		}
	}  

	/*
	 * Method Name : testQueryPolicyMemberRec
	 * Description : This method is used to test the Mentor Page Redirect funtionality for medicare policy
	 * Return type : None
	 * Parameter   : None
	 */
	static testMethod void testMedicarePositive()
	{
		User testUser = [Select Id,Name from User where LastName='test User1'];
		
		 MemberPlan objPLan = [Select Id from MemberPlan limit 1];
        
		System.runAs(testUser)
		{
			Test.startTest();
            system.debug('objPLan.Id'+objPLan.Id);
            IndividualMedicareEoc_LC_HUM.updateURLAndLaunch(objPLan.Id);
            IndividualMedicareEoc_LC_HUM.updateURLAndLaunch('');
			Test.stopTest();
		}
	}
    
    static testMethod void testMedicareNegative()
	{
		User testUser = [Select Id,Name from User where LastName='test User1'];
		
        MemberPlan objPLan = [Select Id from MemberPlan limit 1];
        
		System.runAs(testUser)
		{
			Test.startTest();
            system.debug('objPLan.Id'+objPLan.Id);
            IndividualMedicareEoc_LC_HUM.updateURLAndLaunch('');
			Test.stopTest();
		}
	}
}