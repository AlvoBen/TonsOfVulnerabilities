/****************************************************************************************************************************************************
Apex Class Name  : CreateMultipleCaseWrapperTest
Version          : 1.0
Created Date     : Aug 3 2020
Function         : Test class for CreateMultipleCaseWrapper
******************************************************************************************************************************************************

Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------
* Dinesh Subramaniyan                                08/03/2020                   Original Version
*****************************************************************************************************************************************************/
@isTest
private class CreateMultipleCaseWrapperTest
{

     @testSetup static void prepareTestData() 
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        oCCSUser.Current_Queue__c = 'Cincinnati CRU';
        oCCSUser.Network_User_Id__c = 'SXS2222';
        insert oCCSUser;
        system.assertEquals(oCCSUser.LastName,'test User1');
    }
    
 /*
    * Method Name : CreateMultipleCaseWrapper
    * Description : Test method
    * Return type : void
    * Paramater   : NA
    */

     static testMethod void runPositiveData()
    {     
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
               
        System.runAs(oCCSUser)
        {
            Test.startTest();
            CreateMultipleCaseWrapper HumCreateMultipleCaseWrapper = new CreateMultipleCaseWrapper();
            HumCreateMultipleCaseWrapper.sCaseIdentifier = 'DS';
            System.assertEquals(HumCreateMultipleCaseWrapper.sCaseIdentifier,'DS');
            Test.stopTest();
        }
     }
    
}