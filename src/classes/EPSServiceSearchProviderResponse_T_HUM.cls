/**************************************************************************** 
Apex Class Name  : EPSServiceSearchProviderResponse_T_HUM 
Version          : 1.0  
Created Date     : March 04, 2016
Function         : test class for EPSServiceSearchProviderResponse_H_HUM  
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Gargey Sharma		        30353             03/04/2016                   Original Version
***************************************************************************************************/
@isTest
private Class EPSServiceSearchProviderResponse_T_HUM 
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
     * Method Name : testEPSServiceSearchProviderResponse
     * Description : Test method for EPSServiceSearchProviderResponse_H_HUM 
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testEPSServiceSearchProviderResponse()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];               
        system.runAs(oUser)
        {
          Test.StartTest();
            EPSServiceSearchProviderResponse_H_HUM oResponse = new EPSServiceSearchProviderResponse_H_HUM();
            EPSServiceSearchProviderResponse_H_HUM.SearchResponse seaResp = new EPSServiceSearchProviderResponse_H_HUM.SearchResponse();
            EPSServiceSearchProviderResponse_H_HUM.EnterpriseProviders entProv = new EPSServiceSearchProviderResponse_H_HUM.EnterpriseProviders();
            EPSServiceSearchProviderResponse_H_HUM.Demographics demo = new EPSServiceSearchProviderResponse_H_HUM.Demographics();
            EPSServiceSearchProviderResponse_H_HUM.Address add = new EPSServiceSearchProviderResponse_H_HUM.Address();
            EPSServiceSearchProviderResponse_H_HUM.Phone phn = new EPSServiceSearchProviderResponse_H_HUM.Phone();
            EPSServiceSearchProviderResponse_H_HUM.ProviderName provName = new EPSServiceSearchProviderResponse_H_HUM.ProviderName(); 
            EPSServiceSearchProviderResponse_H_HUM.SourceProvider srcProv = new EPSServiceSearchProviderResponse_H_HUM.SourceProvider();
            EPSServiceSearchProviderResponse_H_HUM.Specialties spec = new EPSServiceSearchProviderResponse_H_HUM.Specialties();
            EPSServiceSearchProviderResponse_H_HUM.Identifier iden = new EPSServiceSearchProviderResponse_H_HUM.Identifier();
            EPSServiceSearchProviderResponse_H_HUM.IdentifierDetail idenDet = new EPSServiceSearchProviderResponse_H_HUM.IdentifierDetail(); 
            EPSServiceSearchProviderResponse_H_HUM.Taxonomy txnmy = new EPSServiceSearchProviderResponse_H_HUM.Taxonomy();
            EPSServiceSearchProviderResponse_H_HUM.UniqueID uniID = new EPSServiceSearchProviderResponse_H_HUM.UniqueID();
            system.assert(oResponse != null);
          Test.StopTest(); 
        }
    }  
}