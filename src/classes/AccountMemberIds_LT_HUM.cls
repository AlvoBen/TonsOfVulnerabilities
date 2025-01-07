/*******************************************************************************************************************************
Apex Class Name : AccountMemberIds_LT_HUM
Version         : 1.0
Created On      : 10/07/2021
Function        : This class istest class of AccountMemberIds_LC_HUM 

Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Ajay Chakradhar                                     10/07/2021               Original Version
*******************************************************************************************************************************************************************/
@isTest
public class AccountMemberIds_LT_HUM {
    
    /*
* Method Name : prepareTestData
* Description : Create common test Data
* Return type : void
* Parameter   : NA
*/
    @testSetup static void prepareTestData() 
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertEquals(oCCSUser.LastName,'test User1');
        
        System.runAs (oCCSUser) 
        {
            RecordType oMemRecType = [SELECT Id,Name FROM RecordType WHERE Name = 'Member' Limit 1];
            Test.startTest();
            Account oacc1 = new Account();
            oacc1.firstName='firstName';
            oacc1.LastName='lastName';
            oacc1.MiddleName='middleName';
            oacc1.suffix='111111';
            oacc1.Deceased_Date__c='';
            oacc1.recordtypeid = oMemRecType.Id;
            oacc1.Enterprise_ID__c='1030693161';
            oacc1.PersonEmail='';
            oacc1.PersonMailingCity='';
            oacc1.PersonMailingState='';
            oacc1.PersonMailingStateCode='';
            oacc1.PersonMailingStreet='';
            oacc1.PersonMailingPostalCode='';
            oacc1.PersonHomePhone='';
            oacc1.Home_Office_Account__c=true;
            oacc1.ETL_Record_Deleted__c=false;
            oacc1.Individual_First_Name__c='getWrapperLast';
            oacc1.Individual_Last_Name__c='getWrapper';
            oacc1.SSN__C = '987654321'; 
            insert oacc1;
            
            Account oacc = new Account();
            oacc.firstName='firstName';
            oacc.LastName='lastName';
            oacc.MiddleName='middleName';
            oacc.recordtypeid = oMemRecType.Id;
            insert oacc; 
            
            Test.stopTest();
        }
    }
    
     /*
    * Method Name : TestfetchMemberIdCardData
    * Description : method for test the fetchMemberIdCardData 
    * Return type : void
    * Parameter   : NA
    */
   static testmethod void TestfetchMemberIdCardData() 
    {
      Account acct = [select id from Account where firstName='firstName' limit 1];
      Object account =  AccountMemberIds_LC_HUM.fetchAccountMemberIdData(acct.id);
      List<MemberIdsCard_LS_HUM.MemberIdCardData> memCard = (List<MemberIdsCard_LS_HUM.MemberIdCardData>) account;  
      System.assertEquals(acct!=null,true);  
    }
}