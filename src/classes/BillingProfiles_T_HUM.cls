/*******************************************************************************************************************************
Apex Class Name : BillingProfiles_T_HUM
Version         : 1.0
Created On      : 12/03/2015
Function        : Test class for BillingProfile_DTO_HUM

* Modification Log:
* Developer Name            Code Review                 Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Santhi Mandava            29052                       12/03/2015                 Original version
* Santhi Mandava            29422                       01/24/2016                 Renamed class name and replaced BillingProfileWrapper_H_HUM class name with BillingProfile_DTO_HUM
*******************************************************************************************************************************/

@isTest
private class BillingProfiles_T_HUM
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
    * Method Name : testfunctionality
    * Description : This method is used to test the group billing functionality.
    * Return type : void
    * Parameter   : NA
    */
    static testMethod void testfunctionality()
    {
        User oUser = [Select Id, Current_Queue__c,Network_User_Id__c  from User where LastName = 'test User1'];
        System.assertEquals('Cincinnati CRU1', oUser.Current_Queue__c);
        
        System.runAs(oUser)
        {
            List<BillingProfile_DTO_HUM> lstValues = BillingTestDataSetup_T_HUM.createBillingWrapperList();
            lstValues.Sort();
            System.assertEquals(2,lstValues[1].iProfileNumber );
        }
    }
}