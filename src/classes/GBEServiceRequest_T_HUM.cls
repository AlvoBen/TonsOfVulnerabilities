/**************************************************************************** 
Apex Class Name  : GBEServiceRequest_T_HUM
Version          : 1.0  
Created Date     : February 09,2016
Function         : test class for GBEServiceRequest_H_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* ChiranjeeviRao Ravuri    29854               02/09/2016                    Original Version
***************************************************************************************************/
@isTest
private Class GBEServiceRequest_T_HUM
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
     * Method Name : testGBEServiceRequest
     * Description : Test method for GBEServiceRequest_H_HUM
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testGBEServiceRequest()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser)
        {
          Test.StartTest();
            GBEServiceRequest_H_HUM oRequest = new GBEServiceRequest_H_HUM();
            GBEServiceRequest_H_HUM.GroupDetailDTO grpDetailDTO = new GBEServiceRequest_H_HUM.GroupDetailDTO();
            system.assert(oRequest!=null);
          Test.StopTest();
        }
    }  
    
}