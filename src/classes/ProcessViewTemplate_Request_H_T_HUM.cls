/****************************************************************************************************************
Apex Class Name  : ProcessViewTemplate_Request_H_T_HUM
Version          : 1.0
Created Date     : 07/09/2019
Function         : Test class for ProcessViewTemplate_Request_H_HUM
******************************************************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
*  Vijaya Koriprolu          27546            07/09/2019                  Original Version
******************************************************************************************************************/
@isTest
private Class ProcessViewTemplate_Request_H_T_HUM
{
    /*
    * Method Name : testSetup 
    * Description : Test method to setup common test data for all test methods     
    * Return type : void     
    * Paramater   : NA  
    */
    @testSetup 
    static void testSetup()
    {        
        HUMTestDataHelperTest.getHUMConstantsData();
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
    }
    
    /*
    * Method Name : testProcessViewTemplate_Request_H_HUM
    * Description : Test method for ProcessViewTemplate_Request_H_HUM
    * Return type : NA
    * Paramater   : NA
    */
    static testMethod void testProcessViewTemplate_Request_H_HUM()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser)
        {
            Test.StartTest();
            ProcessViewTemplate_Request_H_HUM oRequest = new ProcessViewTemplate_Request_H_HUM();
            ProcessViewTemplate_Request_H_HUM.GetProcessesValue oProcessDtl = new ProcessViewTemplate_Request_H_HUM.GetProcessesValue();
            system.assert(oRequest != null);
            Test.StopTest();
        }
    }  
}