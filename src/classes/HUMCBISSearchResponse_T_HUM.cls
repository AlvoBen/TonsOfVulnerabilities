/**************************************************************************** 
Apex Class Name  : HUMCBISSearchResponse_T_HUM
Version          : 1.0  
Created Date     : May 13,2021
Function         : test class for HUMCBISSearchResponse_T_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Firoja Begam                               05/13/2021                    Original Version
***************************************************************************************************/
@isTest
private Class HUMCBISSearchResponse_T_HUM
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
     * Method Name : testCbisRestServiceResponse
     * Description : Test method for HUMCBISSearchResponse_H_HUM
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testCbisRestServiceResponse()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser)
        {
          Test.StartTest();
            HUMCBISSearchResponse_H_HUM oResponse = new HUMCBISSearchResponse_H_HUM();
            HUMCBISSearchResponse_H_HUM.GetPendHistoryTermsDetailsResponse getPendHistoryTermsDetailsResponse = new HUMCBISSearchResponse_H_HUM.GetPendHistoryTermsDetailsResponse();
            HUMCBISSearchResponse_H_HUM.ResponseItems responseItems = new HUMCBISSearchResponse_H_HUM.ResponseItems();
            HUMCBISSearchResponse_H_HUM.ResponseItem responseItem = new HUMCBISSearchResponse_H_HUM.ResponseItem();
            HUMCBISSearchResponse_H_HUM.Address address = new HUMCBISSearchResponse_H_HUM.Address();
            HUMCBISSearchResponse_H_HUM.Policy policy = new HUMCBISSearchResponse_H_HUM.Policy(); 
            system.assert(oResponse!=null);
          Test.StopTest();
        }
    }  
    
}