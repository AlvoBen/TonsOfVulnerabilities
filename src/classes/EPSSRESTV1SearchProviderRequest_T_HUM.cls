/**************************************************************************** 
Apex Class Name  : EPSSRESTV1SearchProviderRequest_T_HUM 
Version          : 1.0  
Created Date     : April 11, 2016
Function         : test class for EPSSRESTV1SearchProviderRequest_H_HUM  
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Gargey Sharma             30905             04/11/2016                   Original Version
***************************************************************************************************/
@isTest
private Class EPSSRESTV1SearchProviderRequest_T_HUM 
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
     * Method Name : testEPSSRESTV1SearchProviderRequest
     * Description : Test method for EPSSRESTV1SearchProviderRequest_H_HUM 
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testEPSSRESTV1SearchProviderRequest()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];               
        system.runAs(oUser)
        {
          Test.StartTest();
            EPSSRESTV1SearchProviderRequest_H_HUM oRequest = new EPSSRESTV1SearchProviderRequest_H_HUM();
            EPSSRESTV1SearchProviderRequest_H_HUM.SearchProviderRequest sreq = new EPSSRESTV1SearchProviderRequest_H_HUM.SearchProviderRequest();
            EPSSRESTV1SearchProviderRequest_H_HUM.SearchCriteria searchcri = new EPSSRESTV1SearchProviderRequest_H_HUM.SearchCriteria();
            EPSSRESTV1SearchProviderRequest_H_HUM.Address add = new EPSSRESTV1SearchProviderRequest_H_HUM.Address();
            EPSSRESTV1SearchProviderRequest_H_HUM.Identifiers idens = new EPSSRESTV1SearchProviderRequest_H_HUM.Identifiers();
            EPSSRESTV1SearchProviderRequest_H_HUM.Filter fil = new EPSSRESTV1SearchProviderRequest_H_HUM.Filter();
            EPSSRESTV1SearchProviderRequest_H_HUM.Identifier iden = new EPSSRESTV1SearchProviderRequest_H_HUM.Identifier();
            system.assert(oRequest != null);
          Test.StopTest(); 
        }
    }  
}