/**************************************************************************** 
Apex Class Name  : EPSServiceSearchGetPCPResponse_T_HUM 
Version          : 1.0  
Created Date     : March 04, 2016
Function         : test class for EPSServiceSearchGetPCPResponse_H_HUM  
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Gargey Sharma		        30353             03/04/2016                   Original Version
***************************************************************************************************/
@isTest
private Class EPSServiceSearchGetPCPResponse_T_HUM 
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
     * Method Name : testEPSServiceSearchGetPCPResponse
     * Description : Test method for EPSServiceSearchGetPCPResponse_H_HUM 
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testEPSServiceSearchGetPCPResponse()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];               
        system.runAs(oUser)
        {
          Test.StartTest();
            EPSServiceSearchGetPCPResponse_H_HUM oResponse = new EPSServiceSearchGetPCPResponse_H_HUM();
            EPSServiceSearchGetPCPResponse_H_HUM.PcpResponse resp = new EPSServiceSearchGetPCPResponse_H_HUM.PcpResponse();
            EPSServiceSearchGetPCPResponse_H_HUM.EnterpriseProviders entProv = new EPSServiceSearchGetPCPResponse_H_HUM.EnterpriseProviders();
            EPSServiceSearchGetPCPResponse_H_HUM.Demographics demogra = new EPSServiceSearchGetPCPResponse_H_HUM.Demographics();
            EPSServiceSearchGetPCPResponse_H_HUM.UniqueId uid = new EPSServiceSearchGetPCPResponse_H_HUM.UniqueId();
            EPSServiceSearchGetPCPResponse_H_HUM.Addresses add = new EPSServiceSearchGetPCPResponse_H_HUM.Addresses();
            EPSServiceSearchGetPCPResponse_H_HUM.Phones phn = new EPSServiceSearchGetPCPResponse_H_HUM.Phones();
            EPSServiceSearchGetPCPResponse_H_HUM.ProviderName pname = new EPSServiceSearchGetPCPResponse_H_HUM.ProviderName();
            EPSServiceSearchGetPCPResponse_H_HUM.SourceProviders srcProv = new EPSServiceSearchGetPCPResponse_H_HUM.SourceProviders();
            EPSServiceSearchGetPCPResponse_H_HUM.SourceIdentifiers sourceiden = new EPSServiceSearchGetPCPResponse_H_HUM.SourceIdentifiers();
            EPSServiceSearchGetPCPResponse_H_HUM.Identifiers iden = new EPSServiceSearchGetPCPResponse_H_HUM.Identifiers();
            EPSServiceSearchGetPCPResponse_H_HUM.PcpInfo pcpinf = new EPSServiceSearchGetPCPResponse_H_HUM.PcpInfo();
            system.assert(oResponse != null);
          Test.StopTest(); 
        }
    }  
}