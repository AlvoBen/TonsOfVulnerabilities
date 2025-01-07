/**************************************************************************** 
Apex Class Name  : HUMCBISSearchRequest_T_HUM
Version          : 1.0  
Created Date     : May 13,2021
Function         : test class for HUMCBISSearchRequest_T_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Firoja Begam                              05/13/2021                   Original Version
***************************************************************************************************/
@isTest
private Class HUMCBISSearchRequest_T_HUM
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
     * Method Name : testCBISRestServiceRequest
     * Description : Test method for GBEServiceRequest_H_HUM
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testCbisRestServiceRequest()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser)
        {
          Test.StartTest();
            HUMCBISSearchRequest_H_HUM oRequest = new HUMCBISSearchRequest_H_HUM();
            HUMCBISSearchRequest_H_HUM.GetPendHistoryTermsDetails specialtyRequestDTO = new HUMCBISSearchRequest_H_HUM.GetPendHistoryTermsDetails();
            system.assert(oRequest!=null);
          Test.StopTest();
        }
    }  
    
}