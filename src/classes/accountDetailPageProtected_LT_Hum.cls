/*****************************************************************************************************************************************************************************
    @Apex Class Name  : accountDetailPageProtected_LT_Hum
    @Version          : 1.0
    @Created Date     : 09/19/2022
    @Test Class For   : accountDetailPageProtected_LC_Hum
******************************************************************************************************************************************************************************
Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Muthukumar                                		 09/19/2022                 Original Version
******************************************************************************************************************************************************************************/
@isTest
public class accountDetailPageProtected_LT_Hum {

     /*
    * This method creates test data for test class
    * @param        None
    * @return       None
    */
     @testSetup static void prepareTestData() 
     {  
         id RecordTypeID=Schema.SObjectType.Account.getrecordtypeinfosByname().get('Member').getRecordtypeID();
         
         Account account = HUMTestDataHelperTest.getAccount();
         account.Name = 'Test Account 1';
         account.Enterprise_ID__c = '1102283421';
         insert account;
         
         User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
         insert oCCSUser;
         system.assertEquals(oCCSUser.LastName,'test User1');
     }
    
    /* 
    * @param        None
    * @return       None
    */
    @istest static void getData()
    {
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        Account testAccount = [Select Id,Name,Enterprise_ID__c from Account where Name = 'Test Account 1' Limit 1];
        
        system.runAs(testUser){
            accountDetailPageProtected_LC_Hum.getAccountData(testAccount.Id);
            accountDetailPageProtected_LC_Hum.getMemberPlanData(testAccount.Id);
        }
    }
}