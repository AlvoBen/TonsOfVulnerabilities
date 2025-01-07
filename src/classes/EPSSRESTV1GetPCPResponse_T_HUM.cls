/**************************************************************************** 
Apex Class Name  : EPSSRESTV1GetPCPResponse_T_HUM 
Version          : 1.0  
Created Date     : April 11, 2016
Function         : test class for EPSSRESTV1GetPCPResponse_H_HUM  
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Gargey Sharma             30905             04/11/2016                   Original Version
***************************************************************************************************/
@isTest
private Class EPSSRESTV1GetPCPResponse_T_HUM 
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
     * Method Name : testEPSSRESTV1GetPCPResponse
     * Description : Test method for EPSSRESTV1GetPCPResponse_H_HUM 
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testEPSSRESTV1GetPCPResponse()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];               
        system.runAs(oUser)
        {
          Test.StartTest();
            EPSSRESTV1GetPCPResponse_H_HUM oResponse = new EPSSRESTV1GetPCPResponse_H_HUM();
            EPSSRESTV1GetPCPResponse_H_HUM.GetPcpResponse resp = new EPSSRESTV1GetPCPResponse_H_HUM.GetPcpResponse();
            EPSSRESTV1GetPCPResponse_H_HUM.EnterpriseProviders entProv = new EPSSRESTV1GetPCPResponse_H_HUM.EnterpriseProviders();
            EPSSRESTV1GetPCPResponse_H_HUM.Demographics demogra = new EPSSRESTV1GetPCPResponse_H_HUM.Demographics();
            EPSSRESTV1GetPCPResponse_H_HUM.UniqueId uid = new EPSSRESTV1GetPCPResponse_H_HUM.UniqueId();
            EPSSRESTV1GetPCPResponse_H_HUM.Addresses add = new EPSSRESTV1GetPCPResponse_H_HUM.Addresses();
            EPSSRESTV1GetPCPResponse_H_HUM.Phones phn = new EPSSRESTV1GetPCPResponse_H_HUM.Phones();
            EPSSRESTV1GetPCPResponse_H_HUM.ProviderName pname = new EPSSRESTV1GetPCPResponse_H_HUM.ProviderName();
            EPSSRESTV1GetPCPResponse_H_HUM.SourceProviders srcProv = new EPSSRESTV1GetPCPResponse_H_HUM.SourceProviders();
            EPSSRESTV1GetPCPResponse_H_HUM.SourceIdentifiers sourceiden = new EPSSRESTV1GetPCPResponse_H_HUM.SourceIdentifiers();
            EPSSRESTV1GetPCPResponse_H_HUM.Identifiers iden = new EPSSRESTV1GetPCPResponse_H_HUM.Identifiers();
            EPSSRESTV1GetPCPResponse_H_HUM.PcpInfo pcpinf = new EPSSRESTV1GetPCPResponse_H_HUM.PcpInfo();
            system.assert(oResponse != null);
          Test.StopTest(); 
        }
    }  
}