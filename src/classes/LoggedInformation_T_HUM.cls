/**************************************************************************************************************
Apex Class Name :  LoggedInformation_T_HUM
Version: 1.0
Created On      : 03/16/2015
Function        : Test class for LoggedInformation_C_HUM

* Modification Log
* Developer Name            Code Review                 Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Santhi Mandava                                        03/16/2015                 Original version
* Shivam Anand              140629                      10/10/2017                 Code Coverage Improving
**************************************************************************************************************************/
@isTest
Private Class LoggedInformation_T_HUM
{
    /*
    * Method Name : setup
    * Description : This method Is used To create test data For the Class 
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
    * Description : This method Is used To test main functionality.
    * Return type : void
    * Parameter   : NA
    */
    Static testMethod void testFunctionality() 
    {
        User oUser = [Select Id, Network_User_Id__c from User where LastName = 'test User1'];
        System.assertEquals('SXS2222', oUser.Network_User_Id__c);
        
        System.runAs(oUser)
        {
            List<Case> lstCases = [Select Id from Case];
            System.assertEquals(3,lstCases.size());
            
            ApexPages.StandardController oStdController = New ApexPages.StandardController(lstCases[0]);
            System.assertNotEquals(oStdController,NULL);
            ApexPages.currentPage().getParameters().put('Id', lstCases[0].Id);
            System.assertEquals(ApexPages.currentPage().getParameters().get('Id'),lstCases[0].Id);
            LoggedInformation_C_HUM objClass = New LoggedInformation_C_HUM(oStdController);
            System.assertEquals(lstCases[0].Id, objClass.sCaseId);  
        }
    }
}