/**************************************************************************** 
Apex Class Name  : UnifiedSearchWindow_T_HUM
Version          : 1.0
Created Date     : 12/02/2020
Function         : used to test the UnifiedSearchWindow_C_HUM apex class

*****************************************************************************************************
Modification Log:
* Developer Name                     Code Review #         Date                       Description
*--------------------------------------------------------------------------------------------------
*  T.P.Sai Kumar Reddy                                     12/02/2020                 original Version

***************************************************************************************************/
@isTest
private class UnifiedSearchWindow_T_HUM
{
    /*
    * Method Name : setup
    * Description : This method is used to create test data for the class 
    * Return type : void
    * Parameter   : NA
    */
    @testSetup static void setup() 
    {
        User oUser = HUMTestDataHelperTest.createTestUser('UnifiedSearchUser1','Customer Care Specialist');
        oUser.FirstName = 'UnifiedSearchUser2';
        insert oUser;
        
        System.assertEquals('test UnifiedSearchUser1', oUser.LastName);     
    }
    /*
    * Method Name : testCoveoErrLog
    * Description : This method is used to test the logCoveoError of controller class 
    * Return type : void
    * Parameter   : NA
    */
    
    @isTest static void testCoveoErrLog()
    {
        User testUser = [Select FirstName,LastName from User where LastName='test UnifiedSearchUser1'];
        system.assertEquals('UnifiedSearchUser2', testUser.FirstName);
        
         Error_Log__c errorlog = new  Error_Log__c();
        system.runAs(testUser)
        { 
            string status= 'Error';
            string errorMessage = 'LWC Component error';
            Test.startTest();
            UnifiedSearchWindow_C_HUM.logCoveoError(status,errorMessage); 
            errorlog = [select Error_Message__c from Error_Log__c LIMIT 1 ];
            system.assertEquals(errorMessage,errorlog.Error_Message__c);
            
            Test.stopTest();
        }    
    } 
    
    /*
    * Method Name : exceptionTestCoveoErrLog
    * Description : This method is used to test the logCoveoError of controller class 
    * Return type : void
    * Parameter   : NA
    **Note        : for raising the exception more than 255 characters needed ***
    */
    
    @isTest static void exceptionTestCoveoErrLog()
    {
        User testUser = [Select FirstName,LastName from User where LastName='test UnifiedSearchUser1'];
        system.assertEquals('UnifiedSearchUser2', testUser.FirstName);
        
        system.runAs(testUser)
        {   
            Error_Log__c errorlog = new  Error_Log__c();
            string status= 'This string is to cover the exception block of main class';
            status += status + status + status+ status;
            string errorMessage = 'LWC Component error';
            Test.startTest();
            UnifiedSearchWindow_C_HUM.logCoveoError(status,errorMessage); 
            errorlog = [select class_name__c from Error_Log__c LIMIT 1];
            system.assertEquals('UnifiedSearchWindow_C_HUM', errorlog.class_name__c);
            
            Test.stopTest();
        }       
    }  
}