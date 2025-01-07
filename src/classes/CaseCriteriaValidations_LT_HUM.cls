/*******************************************************************************************************************************
Apex Class Name : CaseCriteriaValidations_LT_HUM
Version         : 1.0
Created On      : 4/5/2022
Function        : This class is test class for CaseCriteriaValidations_LT_HUM

Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0            Isha Gupta                                       4/5/2022               Original Version    
*/

@isTest
public class CaseCriteriaValidations_LT_HUM {
    
    /**
* Test Setup method to create all the data required for testing.
*
* @param     NULL
* @return    void 
*/
    @testSetup	 static void setUpTestData()
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('UserCCS1','Customer Care Specialist');
        oCCSUser.Current_Queue__c='test_queue';
        insert oCCSUser;
        system.assert(oCCSUser.LastName == 'test UserCCS1');
        system.runAs(oCCSUser){
            List<Account> accList = new List<Account>();
            Account oMemAccount = HUMTestDataHelperTest.getAccount();
            oMemAccount.Group_Number__c = '987654321';
            oMemAccount.Enterprise_Id__c = '1000101';
            oMemAccount.recordtypeid = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            oMemAccount.firstName = 'Daphiner';
            oMemAccount.lastName = 'Superman';
            oMemAccount.Veteran_Status__c ='confirmed-veteran';
            oMemAccount.VA_Health_Enrollee__c ='true';
            oMemAccount.Home_Office_Account__c = false;
            accList.add(oMemAccount);
            if(accList != NULL) insert accList;
            system.assert(accList.size() == 1);
            
            MemberPlan mp = new MemberPlan();
            mp.Name = 'test member plan';
            mp.MemberId = oMemAccount.id;
            insert mp;
        }
        
    }
    
    /**
* This method is used to test valid inputs for case creation
*
* @param     NULL
* @return    void 
*/
    static testMethod void inputsForValidationTest()
    {
        User ccs = [Select id from user limit 1];
        String accountId  = [SELECT id from Account LIMIT 1].id;
        String memPlanid  = [SELECT id from MemberPlan LIMIT 1].id;
        String inputParams = '{"accountId":"'+accountId+'","memPlanId":"'+memPlanid+'"}';
        system.runAs(ccs){
            CaseCriteriaValidations_LH_HUM.InputParamsModel inp = new CaseCriteriaValidations_LH_HUM.InputParamsModel();
            CaseCriteriaValidations_LH_HUM.DataModel dm = (CaseCriteriaValidations_LH_HUM.DataModel) CaseCriteriaValidations_LH_HUM.inputsForValidation(inputParams);
            System.assertEquals('Member',dm.interactingWithRecType);
        }
    }
}