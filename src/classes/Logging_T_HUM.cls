/**************************************************************************************************************
Apex Class Name : Logging_T_HUM 
Version         : 1.0
Created On      : 03/16/2016
Function        : Test class for Logging_C_HUM 

* Modification Log:
* Developer Name            Code Review                 Date                       Description
*----------------------------------------------------------------------------------------------------------------
* Santhi Mandava            24069                       03/16/2016                 Original version
* Santhi Mandava                                        04/19/2016                 Defect 223172.0001 fixed. 
* Santhi Mandava                                        03/12/2019                 Replaced "Endpoint_Url__c" with Endpoint_Url_HUM__c.
*****************************************************************************************************************/
@isTest
private class Logging_T_HUM
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
            Endpoint_Url_HUM__c epurl = new Endpoint_Url_HUM__c(Name='CipherCloudUrl', Remote_Url__c='http://ds.ciphercloud.com/ds/');
            insert epurl;
            HUMCipherCloudSettings__c hcs = new HUMCipherCloudSettings__c(Name='APPLICATION_NAME', Value__c='CRMSTEST');
            insert hcs;
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
            test.startTest();
            ApexPages.currentPage().getParameters().put('type', 'Auth:093028246');
            Account objAcc = [Select Id,Name from Account Limit 1];
            System.assertEquals('test',objAcc.Name);
            
            Logging_C_HUM objClass = new Logging_C_HUM();
            objClass.record_Id = objAcc.Id;
            
            objClass.retrieveCaseReocords();
            System.assertEquals(0,objClass.lstCases.size());
            
            System.assertEquals(true,objClass.displayMsg);
            System.assertEquals(null,objClass.log_color);
            
            Logging_C_HUM.updateQueueLogic();
            System.assertEquals(null,objClass.logged_color);
            System.assertEquals(null,objClass.log_area);
            test.stopTest();
        }
    }
}