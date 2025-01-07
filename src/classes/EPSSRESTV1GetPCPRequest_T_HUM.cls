/**************************************************************************** 
Apex Class Name  : EPSSRESTV1GetPCPRequest_T_HUM 
Version          : 1.0  
Created Date     : April 11, 2016
Function         : test class for EPSSRESTV1GetPCPRequest_H_HUM  
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Gargey Sharma             30905            04/11/2016                    Original Version
***************************************************************************************************/
@isTest
private Class EPSSRESTV1GetPCPRequest_T_HUM 
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
     * Method Name : testEPSSRESTV1GetPCPRequest
     * Description : Test method for EPSSRESTV1GetPCPRequest_H_HUM 
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testEPSSRESTV1GetPCPRequest()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];               
        system.runAs(oUser)
        {
          Test.StartTest();
            EPSSRESTV1GetPCPRequest_H_HUM oRequest = new EPSSRESTV1GetPCPRequest_H_HUM();
            EPSSRESTV1GetPCPRequest_H_HUM.GetPcpRequest pcpreq = new EPSSRESTV1GetPCPRequest_H_HUM.GetPcpRequest();
            EPSSRESTV1GetPCPRequest_H_HUM.MemberContext memCon = new EPSSRESTV1GetPCPRequest_H_HUM.MemberContext();
            EPSSRESTV1GetPCPRequest_H_HUM.Member mem = new EPSSRESTV1GetPCPRequest_H_HUM.Member();
            EPSSRESTV1GetPCPRequest_H_HUM.Coverage coverge = new EPSSRESTV1GetPCPRequest_H_HUM.Coverage();
            system.assert(oRequest != null);
          Test.StopTest(); 
        }
    }  
}