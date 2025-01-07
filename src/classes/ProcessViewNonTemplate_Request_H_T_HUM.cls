/**************************************************************************** 
Apex Class Name  : ProcessViewNonTemplate_Request_H_T_HUM 
Version          : 1.0  
Created Date     : 09/01/2019
Function         : test class for  ProcessViewNonTemplate_Request_H_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Pavan Karnam                                04/18/2019                  Original Version
***************************************************************************************************/
@isTest
private Class ProcessViewNonTemplate_Request_H_T_HUM
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
    * Method Name : testProcessViewNonTemplate_Request_H_HUM
    * Description : Test method for ProcessViewNonTemplate_Request_H_HUM 
    * Return type : NA
    * Paramater   : NA
    */
    static testMethod void testProcessViewNonTemplate_Request_H_HUM()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser)
        {
            Test.StartTest();
            ProcessViewNonTemplate_Request_H_HUM oRequest = new ProcessViewNonTemplate_Request_H_HUM();
            ProcessViewNonTemplate_Request_H_HUM.GetProcessesValue oPrcsVal = new ProcessViewNonTemplate_Request_H_HUM.GetProcessesValue();
            system.assert(oRequest != null);
            Test.StopTest();
        }
    }  
}