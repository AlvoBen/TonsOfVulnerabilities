/**************************************************************************************************************
Apex Class Name : CaseLoggedInfoDataTable_T_HUM
Version         : 1.0
Created On      : 11/17/2017
Function        : Test class for CaseLoggedInfoDataTable_C_HUM

* Modification Log:
* Developer Name            Code Review                 Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Rakesh Patni                 149184                  11/17/2017                 Original version
**************************************************************************************************************************/
@isTest
private class CaseLoggedInfoDataTable_T_HUM
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
        	Test.startTest();
            LoggingTestDataSetup_T_HUM.createTestData();
            List<Case> lCase = [Select id from case limit 5];
            LoggingTestDataSetup_T_HUM.createAttachmentLog(lCase[0].id);
            Test.stopTest();
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
        	Test.startTest();
            List<Case> lstCases = [Select Id from Case limit 5];
            System.assertEquals(3,lstCases.size());
            Attachment_log__c atch = [Select id from Attachment_log__c where Case__c=:lstCases[0].id limit 1];
            CaseLoggedInfoDataTable_C_HUM cont = new CaseLoggedInfoDataTable_C_HUM();
            CaseLoggedInfoDataTable_C_HUM.getJSONData(lstCases[0].id);
            CaseLoggedInfoDataTable_C_HUM.deleteLogAttachments(atch.id,lstCases[0].id);
            Test.stopTest();
        }
    }
}