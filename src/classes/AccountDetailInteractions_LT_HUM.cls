/*******************************************************************************************************************************
Apex Class Name : AccountDetailInteractions_LC_HUM
Version         : 1.0
Created On      : 09/25/2020
Function        : This Class serves as test class to AccountDetailInteractions_LC_HUM. 
Test Class      : AccountDetailInteractions_LT_HUM
Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Pradeep Dani                                             10/14/2020                initial version
* Ankima Srivastava					   03/19/2021		     Test Class Fix
*********************************************************************************************************************************/
@isTest
public with sharing class AccountDetailInteractions_LT_HUM
{
    /**
    * @testSetup method to create test data
    *<p>
    * @param            NULL
    * @return           void
    */
    @testSetup static void setUpTestData()
    {
       
        Account objMemAccount = HUMTestDataHelperTest.getAccount();
        objMemAccount.FirstName = 'Test FName';
        objMemAccount.LastName = 'LName'; 
        objMemAccount.source_platform_code__c = 'EM' ;
        objMemAccount.Enterprise_Id__c = String.valueof(2000);
        objMemAccount.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
        objMemAccount.Birthdate__c = String.valueOf(System.today().month()+'/'+system.today().day()+'/'+System.today().year());
        objMemAccount.Home_Office_Account__c = true;
        insert objMemAccount;
        system.assertEquals(objMemAccount.LastName == 'LName', true);
        
        Interaction__c objInteraction = HUMTestDataHelperTest.getInteraction();
        objInteraction.UUID__c = '011717000094440000310S8886667730';
        objInteraction.Interacting_With__c = objMemAccount.Id;
        objInteraction.Interacting_With_type__c = 'Member';
        insert objInteraction;
        System.assertNotEquals(objInteraction, null, 'Interaction not created');
        
        Interaction_Member__c InteractionMember = new Interaction_Member__c();
        InteractionMember.Interacting_About__c = objMemAccount.id;
        InteractionMember.Interaction__c = objInteraction.id;
        InteractionMember.Interacting_About_Type__c = 'Member';
        insert InteractionMember;
        System.assertNotEquals(InteractionMember, null, 'InteractionMember not created');
    }
    
    /**
    * Method to create test interactions retrival
    *<p>
    * @param            NULL
    * @return           void
    */
    private static testMethod void testInteractions()
    {
        User objUser = [Select Id,Name from User LIMIT 1];
        system.runAs(objUser)
        {
            Account oAcc = [SELECT id FROM Account LIMIT 1];
            list<AccountDetailInteractions_LD_HUM.InteractionModal> lstInts = (list<AccountDetailInteractions_LD_HUM.InteractionModal>) AccountDetailInteractions_LC_HUM.getInteractions(oAcc.Id);
            System.assertNotEquals(lstInts, null, 'No Interactions retrived');
        }
    }
}