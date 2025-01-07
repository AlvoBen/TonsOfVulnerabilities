/**************************************************************************** 
Apex Class Name  : EPSServiceSearchProviderRequest_T_HUM 
Version          : 1.0  
Created Date     : March 04, 2016
Function         : test class for EPSServiceSearchProviderRequest_H_HUM  
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Gargey Sharma		        30353             03/04/2016                   Original Version
***************************************************************************************************/
@isTest
private Class EPSServiceSearchProviderRequest_T_HUM 
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
     * Method Name : testEPSServiceSearchProviderRequest
     * Description : Test method for EPSServiceSearchProviderRequest_H_HUM 
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testEPSServiceSearchProviderRequest()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];               
        system.runAs(oUser)
        {
          Test.StartTest();
            EPSServiceSearchProviderRequest_H_HUM oRequest = new EPSServiceSearchProviderRequest_H_HUM();
            EPSServiceSearchProviderRequest_H_HUM.SearchRequest sreq = new EPSServiceSearchProviderRequest_H_HUM.SearchRequest();
            EPSServiceSearchProviderRequest_H_HUM.SearchCriteria searchcri = new EPSServiceSearchProviderRequest_H_HUM.SearchCriteria();
            EPSServiceSearchProviderRequest_H_HUM.Address add = new EPSServiceSearchProviderRequest_H_HUM.Address();
            EPSServiceSearchProviderRequest_H_HUM.Identifiers idens = new EPSServiceSearchProviderRequest_H_HUM.Identifiers();
            EPSServiceSearchProviderRequest_H_HUM.Filter fil = new EPSServiceSearchProviderRequest_H_HUM.Filter();
            EPSServiceSearchProviderRequest_H_HUM.Identifier iden = new EPSServiceSearchProviderRequest_H_HUM.Identifier();
            system.assert(oRequest != null);
          Test.StopTest(); 
        }
    }  
}