/**************************************************************************** 
Apex Class Name  : EPSServiceSearchGetPCPRequest_T_HUM 
Version          : 1.0  
Created Date     : March 04, 2016
Function         : test class for EPSServiceSearchGetPCPRequest_H_HUM  
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Gargey Sharma		        30353            03/04/2016                    Original Version
***************************************************************************************************/
@isTest
private Class EPSServiceSearchGetPCPRequest_T_HUM 
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
     * Method Name : testEPSServiceSearchGetPCPRequest
     * Description : Test method for EPSServiceSearchGetPCPRequest_H_HUM 
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testEPSServiceSearchGetPCPRequest()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];               
        system.runAs(oUser)
        {
          Test.StartTest();
            EPSServiceSearchGetPCPRequest_H_HUM oRequest = new EPSServiceSearchGetPCPRequest_H_HUM();
            EPSServiceSearchGetPCPRequest_H_HUM.PcpRequest pcpreq = new EPSServiceSearchGetPCPRequest_H_HUM.PcpRequest();
            EPSServiceSearchGetPCPRequest_H_HUM.MemberContext memCon = new EPSServiceSearchGetPCPRequest_H_HUM.MemberContext();
            EPSServiceSearchGetPCPRequest_H_HUM.Member mem = new EPSServiceSearchGetPCPRequest_H_HUM.Member();
            EPSServiceSearchGetPCPRequest_H_HUM.Coverage coverge = new EPSServiceSearchGetPCPRequest_H_HUM.Coverage();
            system.assert(oRequest != null);
          Test.StopTest(); 
        }
    }  
}