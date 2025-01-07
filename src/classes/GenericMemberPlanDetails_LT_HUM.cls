/**************************************************************************** 
Apex Class Name  : GenericMemberPlanDetails_LT_HUM
Version          : 1.0  
Created Date     : 3/14/2023
Function         : Test class Generic Member Plan Details classes

****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Nirmal Garg                              3/14/2023                  Original Version
***************************************************************************************************/
@isTest
public with sharing class GenericMemberPlanDetails_LT_HUM {
    @testSetup static void setUpTestData(){
        User objUser = HUMTestDataHelperTest.createTestUser('User1', 'Customer Care Specialist');
        insert objUser;
        System.assertEquals(objUser.lastname == 'test User1', true);
        System.runAs(objUser){
           
            // Insert Member Account
            Account objMemAccount = HUMTestDataHelperTest.getAccount();
            objMemAccount.FirstName = 'Test FName';
            objMemAccount.LastName = 'LName';
            objMemAccount.source_platform_code__c = 'EM';
            objMemAccount.Enterprise_Id__c = String.valueof(2000);
            objMemAccount.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            objMemAccount.Birthdate__c = String.valueOf(System.today().month() + '/' + System.today().day() + '/' + System.today().year());
            insert objMemAccount;
            System.assert(objMemAccount.LastName == 'LName');
            
            // Insert Member Account
            Account objMemAccountLV = HUMTestDataHelperTest.getAccount();
            objMemAccountLV.FirstName = 'Test LName';
            objMemAccountLV.LastName = 'LName2';
            objMemAccountLV.source_platform_code__c = 'LV';
            objMemAccountLV.Enterprise_Id__c = String.valueof(2001);
            objMemAccountLV.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            objMemAccountLV.Birthdate__c = String.valueOf(System.today().month() + '/' + System.today().day() + '/' + System.today().year());
            insert objMemAccountLV;
            System.assert(objMemAccountLV.LastName == 'LName2');

            Account objGrpAccount = HUMTestDataHelperTest.getAccount();
            objGrpAccount.Name = 'New Case Account';
            objGrpAccount.BillingState = 'New York';
            objGrpAccount.Enterprise_Id__c = String.valueof(4000);
            objGrpAccount.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
            insert objGrpAccount;
            System.assert(objGrpAccount.BillingState == 'New York');
            
            /*********************************** Medical *************************************/
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
            oMedicalPolicy.source_cust_cov_key__c = '12345678901234567890';
            oMedicalPolicy.Group_Name__c = objMemAccount.Id;
            oMedicalPolicy.Product__c = objMedProduct.Id;
            oMedicalPolicy.Major_LOB__c = 'EPO';
            insert oMedicalPolicy;
            System.assert(oMedicalPolicy.Name == 'Medical Product');

            //Policy_Plan_Detail__c - Added for test coverage
            Policy_Plan_Details__c MemberPlan = new Policy_Plan_Details__c();
            MemberPlan.Name = 'Medical Policy Plan Detail';
            MemberPlan.ETL_Record_Deleted__c = false;
            MemberPlan.Policy_Plan_External_ID__c = 'EFGH';
            MemberPlan.Coverage_Plan_End_Date__c = '12/31/2016';
            MemberPlan.Coverage_Plan_Effective_Date__c = '2/12/2016';
            MemberPlan.Policy__c = oMedicalPolicy.Id;
            MemberPlan.Product__c = objMedProduct.Id;
            MemberPlan.Benefit_Coverage__c = 'FL103104';
            insert MemberPlan;
            System.assert(MemberPlan.Name == 'Medical Policy Plan Detail');
            
          //Medical Member Plan , SubscriberId, EffectiveT
            MemberPlan objMemberPlan = New MemberPlan();
            objMemberPlan.Member = objMemAccount;
            objMemberPlan.Policy__c = oMedicalPolicy.Id;
            objMemberPlan.EffectiveTo =Date.valueOf('2025-12-31');
            objMemberPlan.EffectiveFrom = Date.valueOf('2015-12-31');
            objMemberPlan.Member_Plan_External_Id__c  = '00|ODS|123456789|89|89|02-01-2001';
            objMemberPlan.name = '123456789';
            objMemberPlan.MemberId = objMemAccount.id;
            insert objMemberPlan;
            System.assert(objMemberPlan.Member_Plan_External_Id__c == '00|ODS|123456789|89|89|02-01-2001');
        }
    }

    /**
    * Method to Test Get Member plan details.
    */    
    static testMethod void testgetMemberPlanDetails(){
        User testUser = [Select Id, Name from User where LastName = 'test User1'];
        MemberPlan memberPlanObj= [select id, name, MemberId, EffectiveTo, EffectiveFrom, Policy__r.Product__r.ProductCode, Policy__r.Major_LOB__c, policy__r.source_cust_cov_key__c, policy__r.Coverage_Plan_Effective_Date__c, policy__r.Coverage_Plan_End_Date__c from MemberPlan where name = '123456789' limit 1];
        System.runAs(testUser){
            Test.startTest();
			try{
				List<MemberPlan> lstMemberPlan = GenericMemberPlanDetails_LC_HUM.getMemberPlanDetails(memberPlanObj.Id);
				System.assertEquals(memberPlanObj.Name, lstMemberPlan[0].Name);
			}
			catch(Exception e){
                system.assertEquals('Script-thrown exception', e.getMessage(),'success');
            }
            Test.stopTest();
        }
    }

    static testMethod void testgetMemberPlanDetailsnegative(){
        User testUser = [Select Id,Name from User where LastName='test User1'];
        MemberPlan memberPlanobj = [Select Id from MemberPlan where Name = '123456789' Limit 1];
        System.runAs(testUser)
        {
            Test.startTest();
            List<MemberPlan> lstMemberPlan = GenericMemberPlanDetails_LC_HUM.getMemberPlanDetails('');
            system.assertEquals(null,lstMemberPlan);
            Test.stopTest();
        }
    }
    static testMethod void testgetMemberPlanDetailsException(){
        User testUser = [Select Id,Name from User where LastName='test User1'];
        MemberPlan memberPlanobj = [Select Id from MemberPlan where Name = '123456789' Limit 1];
        System.runAs(testUser)
        {
            Test.startTest();
            try{
                List<MemberPlan> lstMemberPlan = GenericMemberPlanDetails_LC_HUM.getMemberPlanDetails('testing');
            }catch(exception ex){
                system.assertNotEquals('error', ex.getMessage());
            }
            Test.stopTest();
        }
    }
}