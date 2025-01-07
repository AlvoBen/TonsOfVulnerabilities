/*********************************************************************************************************
Apex Class Name : UserAssociationCreateEdit_T_HUM 
Version         : 1.0
Created On      : 06/29/2018
Function        : Test class for UserAssociationCreateEdit_C_HUM 

* Modification Log:
* Developer Name            Code Review                 Date                       Description
*-----------------------------------------------------------------------------------------------------------
* Anurag Chaturvedi                                    06/29/2018                   Original version
* Mukul Kumar                                          08/30/2018                 REQ-371438 add three variable to cover the 
                                                                                   coverage of the class
************************************************************************************************************/
@isTest private class UserAssociationCreateEdit_T_HUM { 
/* 
* Method Name : setUpTestData 
* Description : This method creates test data For test Class 
* Return type : None 
* Parameter : None */ 

@testSetup static void setUpTestData() { 
    User objUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist'); 
    insert objUser; 
    system.runAs(objUser) { 
        Account objMemAccount = HUMTestDataHelperTest.getAccount(); 
        objMemAccount.FirstName = 'Test FName'; 
        objMemAccount.LastName = 'LName'; 
        objMemAccount.Individual_First_Name__c = 'Test FName'; 
        objMemAccount.Individual_Last_Name__c = 'LName'; 
        objMemAccount.Account_Security_EndDate__c = Date.today();
        objMemAccount.Account_Security_Access__c = 'Test';
        objMemAccount.Account_Security_Answer__c='Testing';
        objMemAccount.Account_Security_Question__c='What is u r name';
        objMemAccount.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId(); 
        insert objMemAccount; 
        System.assertEquals(objMemAccount.LastName == 'LName', true); 
        
     }   
     }  
     
@isTest  static  void testPositive()
     {
        User testUser = [Select Id, Name from User limit 1];
        System.assertEquals(testUser!=null, true);
        Account acc =[Select Id, Name,FirstName from Account where FirstName ='Test FName' limit 1];
        System.assertEquals(acc!=null, true);
		
        system.runAs(testUser )
        {
            Test.startTest();
            Test.setCurrentPage(page.UserAssociationCreateEdit_VF_HUM);
            ApexPages.currentPage().getParameters().put('Id',acc.Id); 
            ApexPages.currentPage().getParameters().put('mode','edit');
            UserAssociationCreateEdit_C_HUM usrCreateEdit = new UserAssociationCreateEdit_C_HUM(new ApexPages.StandardController(acc));
			usrCreateEdit.sPassword='password1234';
            usrCreateEdit.sSecurityQuestion ='hws you';
            usrCreateEdit.sSecurityAnswer='fine';
            usrCreateEdit.saveAccount();
            usrCreateEdit.showDetails();
            Test.stopTest();
        }
     }
}