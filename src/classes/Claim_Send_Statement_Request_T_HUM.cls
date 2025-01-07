/**************************************************************************** 
Apex Class Name  : Claim_Send_Statement_Request_T_HUM
Version          : 1.0  
Created Date     : October 04,2018
Function         : test class for Claim_Send_Statement_Request_H_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Vijaya Koriprolu                             10/04/2018                Original Version
***************************************************************************************************/
@isTest
private Class Claim_Send_Statement_Request_T_HUM
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
     * Method Name : testCasesSendRequestStatement
     * Description : Test method for Claim_Send_Statement_Request_H_HUM 
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testCasesSendRequestStatement()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser)
        {
            Test.StartTest();
            Claim_Send_Statement_Request_H_HUM oRequest = new Claim_Send_Statement_Request_H_HUM();
            Claim_Send_Statement_Request_H_HUM.StandardizeAddressRequest oCasesIntSum = new Claim_Send_Statement_Request_H_HUM.StandardizeAddressRequest();
            system.assert(oRequest != null);
            Test.StopTest();
        }
    }  
}