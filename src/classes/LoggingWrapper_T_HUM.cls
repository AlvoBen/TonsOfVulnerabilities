/**************************************************************************************************************
Apex Class Name : LoggingWrapper_T_HUM
Version         : 1.0
Created On      : 03/28/2016
Function        : Test class for LoggingWrapper_DTO_HUM

* Modification Log:
* Developer Name            Code Review                 Date                       Description
*----------------------------------------------------------------------------------------------------------------
* Santhi Mandava            24069                       03/28/2016                 Original version
* Santhi Mandava            27512                       05/12/2016                 Changed to support LoggingWrapper_DTO_HUM new variables
*****************************************************************************************************************/
@isTest
private class LoggingWrapper_T_HUM
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
        system.runAs(oUser)
        {
            LoggingTestDataSetup_T_HUM.createTestData();
        }
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
            LoggingWrapper_DTO_HUM objClass = new LoggingWrapper_DTO_HUM();
            objClass.sImageString = 'Sample Image';
            objClass.sAttachmentName = 'Test Attachment' ;
            objClass.sCreatedBy = 'Santhi';
            objClass.sCreatedByQueue = 'Cincinaati CRU';
            objClass.sSelectedCaseId = '1234567890';
            objClass.sAccountId = '1234567890';
            objClass.sAuthId = '123456780';
            objClass.sType = 'Auth Summary';
            objClass.sJsonMap = 'Sample JSON';
            objClass.sJsonStandardMap = 'Sample JSON';
            System.assertEquals( 'Sample Image',objClass.sImageString );
        }
    }
}