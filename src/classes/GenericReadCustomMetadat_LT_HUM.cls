/*****************************************************************************************
Apex class Name    : GenericReadCustomMetadat_LT_HUM
Version            : 1.0
Function           : Test class for GenericReadCustomMetadat_LC_HUM.
Created Date       : 06/01/2023
Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
*Abhishek Mangutkar                                       06/01/2023                 Original Version
****************************************************************************************************************/

@isTest
public class GenericReadCustomMetadat_LT_HUM {
    
    @testSetup static void setUpTestData(){
        User objUser = HUMTestDataHelperTest.createTestUser('User1', 'Customer Care Specialist');
        insert objUser;
        Assert.areEqual(objUser.lastname,'test User1');
        System.runAs(objUser){
            
            Account objMemAccount = HUMTestDataHelperTest.getAccount();
            objMemAccount.FirstName = 'Test FName';
            objMemAccount.LastName = 'LName';
            objMemAccount.source_platform_code__c = 'EM';
            objMemAccount.Enterprise_Id__c = String.valueof(2000);
            objMemAccount.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            objMemAccount.Birthdate__c = String.valueOf(System.today().month() + '/' + System.today().day() + '/' + System.today().year());
            objMemAccount.PersonMailingPostalCode = '40302';
            
            insert objMemAccount;
            Assert.areEqual(objMemAccount.LastName, 'LName');
            
            Product2 objMedProduct = new Product2();
            objMedProduct.Name ='MED';
            objMedProduct.Product_Type__c = 'Med';
            objMedProduct.productCode = 'MED';
            objMedProduct.Sold_Product_Key_Value__c = 'MC3HH016';
            insert objMedProduct;
            Assert.areEqual(objMedProduct.Product_Type__c, 'Med');
            
            PurchaserPlan objPurchaserPlan = New PurchaserPlan();
            objPurchaserPlan.Name = 'MED';
            objPurchaserPlan.Major_LOB__c = 'MED';
            objPurchaserPlan.Source_Major_LOB__c = 'MCD';
            objPurchaserPlan.Product__c = objMedProduct.id;
            insert objPurchaserPlan;
            // Insert Member Account
            
            MemberPlan objMemberPlan = New MemberPlan();
            objMemberPlan.Member = objMemAccount;
            objMemberPlan.planId = objPurchaserPlan.id;
            objMemberPlan.MemberId = objMemAccount.Id;
            objMemberPlan.EffectiveTo =Date.valueOf('2025-12-31');
            objMemberPlan.EffectiveFrom = Date.valueOf('2015-12-31');
            objMemberPlan.Member_Plan_External_Id__c  = '00|ODS|123456789|89|89|02-01-2001';
            objMemberPlan.name = '123456789';
            insert objMemberPlan;
            Assert.areEqual(objMemberPlan.Member_Plan_External_Id__c, '00|ODS|123456789|89|89|02-01-2001');
                        
        }
    }
    
    static testMethod void testGetMetadataValuesPositive(){
        Test.StartTest();
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        System.runAs(testUser)
        {
            GenericReadCustomMetadat_LC_HUM objMetadata = new GenericReadCustomMetadat_LC_HUM();
            List<LightningPageControls__mdt> response = GenericReadCustomMetadat_LC_HUM.getMetadataValues('DentalBenefitsWaitingPeriods');
            if (response != null && response.size() > 0){            
                Assert.areEqual(response[0].LightningPage__r.PageName__c, 'DentalBenefitsWaitingPeriods');
            }else{
                Assert.areEqual(response, null);
            }
            Test.StopTest();            
        }
    }
    
    static testMethod void testGetMetadataValuesNegative(){
        Test.StartTest();
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        System.runAs(testUser)
        {
            GenericReadCustomMetadat_LC_HUM objMetadata = new GenericReadCustomMetadat_LC_HUM();
            List<LightningPageControls__mdt> response = GenericReadCustomMetadat_LC_HUM.getMetadataValues('');
            Assert.areEqual(response, null);
            Test.StopTest();            
        }
    }

}