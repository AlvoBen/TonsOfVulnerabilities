/**************************************************************************** 
Apex Class Name  : ClaimsServiceSummaryRequest_T_HUM_V2
Version          : 1.0  
Created Date     : March 06,2018
Function         : test class for ClaimsServiceSummaryRequest_H_HUM_V2
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Kritika Popat               212620             03/06/2018                    Accommodate New Claims Summary V3 Request  
***************************************************************************************************/
@isTest
private Class ClaimsServiceSummaryRequest_T_HUM_V2
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
     * Method Name : testClaimsServiceSummaryRequest
     * Description : Test method for ClaimsServiceSummaryRequest_H_HUM
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testClaimsServiceSummaryRequest()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser)
        {
          Test.StartTest();
            ClaimsServiceSummaryRequest_H_HUM_V2 oRequest = new ClaimsServiceSummaryRequest_H_HUM_V2();
            ClaimsServiceSummaryRequest_H_HUM_V2.GetClaimsSummary clmSummary = new ClaimsServiceSummaryRequest_H_HUM_V2.GetClaimsSummary();
            ClaimsServiceSummaryRequest_H_HUM_V2.MemberRequests memREQ = new ClaimsServiceSummaryRequest_H_HUM_V2.MemberRequests();
            system.assert(oRequest != null);
          Test.StopTest();
        }
    }  
}