/**************************************************************************************************************
Apex Class Name : CaseQAATemplate_T_HUM
Version         : 1.0
Created On      : 02/28/2017
Function        : Test class for CaseQAATemplate_C_HUM

* Modification Log:
* Developer Name            Code Review                 Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Santhi Mandava            71723                       02/28/2017                 Original version
* Prasanthi Kandula                                     04/13/2017                 test failure fix
**************************************************************************************************************************/

@isTest
private class CaseQAATemplate_T_HUM
{
    /*
    * Method Name : setup
    * Description : This method is used to create test data for the class 
    * Return type : void
    * Parameter   : NA
    */
    @testSetup static void setup() 
    {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        oUser.Current_Queue__c = 'Cincinnati CRU1';
        oUser.Network_User_Id__c = 'SXS2222';
        insert oUser;
        System.assertEquals(oUser.LastName, 'test User1');
    }
    
    /*
    * Method Name : testFunctionality
    * Description : This method is used to test main functionality.
    * Return type : void
    * Parameter   : NA
    */
    static testMethod void testFunctionality() 
    {
        User oUser = [Select Id,Network_User_Id__c from User where LastName = 'test User1'];
        System.assertEquals('SXS2222', oUser.Network_User_Id__c);
        
        System.runAs(oUser)
        {
            CaseQAATemplate_C_HUM objClass = new CaseQAATemplate_C_HUM();
            System.assertEquals(true, objClass.bIsHavingAdavancedQAAPermission);
        }
    }
}