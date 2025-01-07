/**************************************************************************************************************
Apex Class Name : LoggingHelper_T_HUM
Version         : 1.0
Created On      : 03/16/2015
Function        : Test class for LoggingHelper_C_HUM

* Modification Log:
* Developer Name            Code Review                 Date                       Description
*----------------------------------------------------------------------------------------------------------------
* Santhi Mandava                                        03/16/2015                 Original version
*****************************************************************************************************************/
@isTest
private class LoggingHelper_T_HUM
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
            Account objAcc = [Select Id,Name from Account];
            System.assertEquals('test',objAcc.Name);
            
            Policy__c objPol = [Select Id,Platform__c from Policy__c ];
            System.assertEquals('LV',objPol.Platform__c);
            
            Policy_Member__c objPolMem = [Select Id,Source_Coverage_ID__c from Policy_Member__c];
            System.assertEquals('12345',objPolMem.Source_Coverage_ID__c);
            
            
            String sObjId = LoggingHelper_H_HUM.getAccountId(objAcc.Id, 'Account');
            System.assertEquals(objAcc.Id, sObjId );
            
            sObjId = LoggingHelper_H_HUM.getAccountId(objPol.Id, 'Policy__c');
            System.assertEquals(objAcc.Id,sObjId );
            
            sObjId = LoggingHelper_H_HUM.getAccountId(objPolMem.Id, 'Policy_Member__c');
            System.assertEquals(objAcc.Id,sObjId );
        }
    }
}