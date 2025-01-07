/**************************************************************************** 
Apex Class Name  : EPSSRESTV1SearchProviderResponse_T_HUM 
Version          : 1.0  
Created Date     : April 11, 2016
Function         : test class for EPSSRESTV1SearchProviderResponse_H_HUM  
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Gargey Sharma             30905             04/11/2016                   Original Version
***************************************************************************************************/
@isTest
private Class EPSSRESTV1SearchProviderResponse_T_HUM 
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
     * Method Name : testEPSSRESTV1SearchProviderResponse
     * Description : Test method for EPSSRESTV1SearchProviderResponse_H_HUM 
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testEPSSRESTV1SearchProviderResponse()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];               
        system.runAs(oUser)
        {
          Test.StartTest();
            EPSSRESTV1SearchProviderResponse_H_HUM oResponse = new EPSSRESTV1SearchProviderResponse_H_HUM();
            EPSSRESTV1SearchProviderResponse_H_HUM.SearchProviderResponse seaResp = new EPSSRESTV1SearchProviderResponse_H_HUM.SearchProviderResponse();
            EPSSRESTV1SearchProviderResponse_H_HUM.EnterpriseProviders entProv = new EPSSRESTV1SearchProviderResponse_H_HUM.EnterpriseProviders();
            EPSSRESTV1SearchProviderResponse_H_HUM.Demographics demo = new EPSSRESTV1SearchProviderResponse_H_HUM.Demographics();
            EPSSRESTV1SearchProviderResponse_H_HUM.Address add = new EPSSRESTV1SearchProviderResponse_H_HUM.Address();
            EPSSRESTV1SearchProviderResponse_H_HUM.Phone phn = new EPSSRESTV1SearchProviderResponse_H_HUM.Phone();
            EPSSRESTV1SearchProviderResponse_H_HUM.ProviderName provName = new EPSSRESTV1SearchProviderResponse_H_HUM.ProviderName(); 
            EPSSRESTV1SearchProviderResponse_H_HUM.SourceProvider srcProv = new EPSSRESTV1SearchProviderResponse_H_HUM.SourceProvider();
            EPSSRESTV1SearchProviderResponse_H_HUM.Specialties spec = new EPSSRESTV1SearchProviderResponse_H_HUM.Specialties();
            EPSSRESTV1SearchProviderResponse_H_HUM.Identifier iden = new EPSSRESTV1SearchProviderResponse_H_HUM.Identifier();
            EPSSRESTV1SearchProviderResponse_H_HUM.IdentifierDetail idenDet = new EPSSRESTV1SearchProviderResponse_H_HUM.IdentifierDetail(); 
            EPSSRESTV1SearchProviderResponse_H_HUM.Taxonomy txnmy = new EPSSRESTV1SearchProviderResponse_H_HUM.Taxonomy();
            EPSSRESTV1SearchProviderResponse_H_HUM.UniqueID uniID = new EPSSRESTV1SearchProviderResponse_H_HUM.UniqueID();
            system.assert(oResponse != null);
          Test.StopTest(); 
        }
    }  
}