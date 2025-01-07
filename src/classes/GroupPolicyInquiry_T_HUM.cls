/**************************************************************************** 
Apex Class Name  : GroupPolicyInquiry_T_HUM
Version          : 1.0  
Created On       : 07/24/2020 
Function         : Test class for GroupPolicyInquiry_C_HUM 
Test Class       : GroupPolicyInquiry_T_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Sayali Nemade                                07/24/2020                  Added Switch for REQ - 1212111 : MF 3 - CRM User Access to CCP GCP Data in Repository (Legacy Contact History Section - Group)
* Sayali Nemade 				               08/27/2020                  REQ - 1170241 MF 3 - Archived CCP Reference Summary Page - High Level  (Overall look)
***************************************************************************************************/
@isTest public class GroupPolicyInquiry_T_HUM
{
    /*
    * Method Name : setUpTestData
    * Description : This method creates test data for test class
    * Return type : None
    * Parameter   : None
    */
    @testSetup
    static void setUpTestData()
    {
        User testUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert testUser;
        System.assertEquals(testUser.lastname=='test User1',true);
        System.runAs(testUser)
        {
            Account objGrpAccount = HUMTestDataHelperTest.getAccount();
            objGrpAccount.Name = 'New Case Account';
            objGrpAccount.BillingState = 'New York';
            objGrpAccount.Enterprise_Id__c = String.valueof(4000);
            objGrpAccount.Source_Platform_Code__c = 'EM';
			objGrpAccount.Mbr_Gen_Key__c='12334566';
            objGrpAccount.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
            insert objGrpAccount;
            System.assert(objGrpAccount.BillingState == 'New York');
            
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
            oMedicalPolicy.Group_Name__c = objGrpAccount.Id;
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
            objMedPolMem.Member__c = objGrpAccount.Id;
            objMedPolMem.Policy__c = oMedicalPolicy.Id;
            objMedPolMem.Relationship__c = 'self';    
            objMedPolMem.end_date__c = '12/31/2015'; 
            objMedPolMem.Effective_Date__c='01/01/2015';       
            objMedPolMem.Policy_Member_External_ID__c = '00|ODS|123456789|89|89|02-01-2001';
            insert objMedPolMem;
            System.assertEquals(objMedPolMem.Relationship__c == 'self',true);
            
            CRMFunctionality_ONOFF_Switch__c groupInquirySwitch = new CRMFunctionality_ONOFF_Switch__c();
            groupInquirySwitch.IsON__c =true;
            groupInquirySwitch.Name = '1137828';
            groupInquirySwitch.Scrum_Team__c = 'Lego';
            groupInquirySwitch.Story_Title__c = 'This switch is to enable or disable to the functionality of Legacy Contact History Section - Member, Policy Member, Policy, Unknown Member, Agent-Broker, Provider, Group';
            groupInquirySwitch.Release_Name__c = '08/28/2020';
            insert groupInquirySwitch;    
			System.assertEquals(groupInquirySwitch.Name,'1137828');
		} 
    }
    
    /*
    * Method Name : GroupPolicyInquiry_C_HUMTest
    * Functionality : Constructor
    * Return type : NA.
    */
    
    @isTest
    static void GroupPolicyInquiry_C_HUMTest()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
		Policy__c policy = [SELECT Id, Name, Group_Name__c, Platform__c, source_cust_cov_key__c, Product__c, Benefit_Coverage__c, Major_LOB__c FROM Policy__c where Name='Medical Product' limit 1];
        System.runAs(testUser)
        {
            Test.StartTest();
                Test.setCurrentPage(page.GroupPolicyInquiry_VF_HUM);
		  		ApexPages.currentPage().getParameters().put('id', policy.id);	
                GroupPolicyInquiry_C_HUM groupPolicyInq = new GroupPolicyInquiry_C_HUM (new ApexPages.StandardController(policy));
                System.assert(policy.Name == 'Medical Product');
            Test.StopTest();
        }
    }
}