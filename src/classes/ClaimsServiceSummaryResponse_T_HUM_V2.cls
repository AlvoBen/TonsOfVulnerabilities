/**************************************************************************** 
Apex Class Name  : ClaimsServiceSummaryResponse_T_HUM_V2
Version          : 1.0  
Created Date     : March 29, 2018
Function         : test class for ClaimsServiceSummaryResponse_H_HUM_V2
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Kritika Popat               212620            03/29/2018                 Claims V3 Original Version
***************************************************************************************************/
@isTest
private Class ClaimsServiceSummaryResponse_T_HUM_V2
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
     * Method Name : testClaimsServiceSummaryResponse
     * Description : Test method for ClaimsServiceSummaryResponse_H_HUM
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testClaimsServiceSummaryResponse()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser)
        {
          Test.StartTest();
            ClaimsServiceSummaryResponse_H_HUM_V2 oResponse = new ClaimsServiceSummaryResponse_H_HUM_V2();
            ClaimsServiceSummaryResponse_H_HUM_V2.ClaimSummaryReponse clmsumresp = new ClaimsServiceSummaryResponse_H_HUM_V2.ClaimSummaryReponse();
            ClaimsServiceSummaryResponse_H_HUM_V2.ClaimSummaryResults sumrslts = new ClaimsServiceSummaryResponse_H_HUM_V2.ClaimSummaryResults();
            ClaimsServiceSummaryResponse_H_HUM_V2.ClaimSummaryResult res = new ClaimsServiceSummaryResponse_H_HUM_V2.ClaimSummaryResult();
            system.assert(oResponse!=null);
          Test.StopTest();
        }
    }  
}