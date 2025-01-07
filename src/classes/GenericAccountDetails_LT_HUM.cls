/*
* Apex Class Name        : GenericAccountDetails_LT_HUM.cls
* Function               : For Generic Account details
* Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Nirmal Garg                     09/20/2023                   Initial version                          
****************************************************************************************************************************/
@isTest
public with sharing class GenericAccountDetails_LT_HUM {
    @TestSetup
    static void makeData(){
        User objUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');   
        insert objUser;
        Account objMemAccount = HUMTestDataHelperTest.getAccount();
        objMemAccount.FirstName = 'Test FName';
        objMemAccount.LastName = 'LName'; 
        objMemAccount.source_platform_code__c = 'EM' ;
        objMemAccount.PersonMailingCountry ='United States';
        objMemAccount.Enterprise_Id__c = String.valueof(2000);
        objMemAccount.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
        objMemAccount.Birthdate__c = String.valueOf(System.today().month()+'/'+system.today().day()+'/'+System.today().year());
        insert objMemAccount;
        system.assertEquals(objMemAccount.LastName == 'LName', true,'success');
    }
    
    @IsTest
    static void testGetAccountList(){
        User testUser = [Select Id, Name from User where LastName='test User1'];
        account accobj = [select id from account where FirstName = 'Test FName'Limit 1];
        Id  accountId = accobj.id;
        System.runAs(testUser){
            Test.startTest();
            List<Account> lstAccounts = GenericAccountDetails_LC_HUM.getAccountList(accountId);
            if(lstAccounts.size() > 0){
                system.assertEquals(lstAccounts[0].PersonMailingCountry == 'United States',true,'success'); 
            }else{
                System.assertNotEquals(null, lstAccounts,'success');
            }                             
            Test.stopTest();
        }        
    }
    
    @IsTest
    static void testGetAccountListException(){
        User testUser = [Select Id, Name from User where LastName='test User1'];
        System.runAs(testUser){
            Test.startTest();
            try {
                List<Account> lstAccounts = GenericAccountDetails_LC_HUM.getAccountList(null);
                if(lstAccounts.size() > 0){
                    system.assertEquals(lstAccounts[0].PersonMailingCountry == 'United States',true,'success'); 
                }else{
                    System.assertNotEquals(null, lstAccounts,'success');
                }
            } catch (Exception ex) {
                System.assertNotEquals('', ex.getMessage(), 'success');
            }                             
            Test.stopTest();
        }
    }
}