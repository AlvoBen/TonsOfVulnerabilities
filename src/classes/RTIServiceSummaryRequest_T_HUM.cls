/**************************************************************************** 
Apex Class Name  : RTIServiceSummaryRequest_T_HUM
Version          : 
Created Date     : Novemeber 21, 2016
Function         : test class for RTIServiceSummaryRequest_H_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Kritika Popat                                11/21/2016               test class for RTIServiceSummaryRequest_H_HUM  
***************************************************************************************************/

@isTest
private class RTIServiceSummaryRequest_T_HUM {
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
     * Method Name : testGetRTIServiceSummaryRequest
     * Description : Test method for RTIServiceSummaryRequest_H_HUM
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testGetRTIServiceSummaryRequest()
    {            
        User oUser = [select Id from User where name = 'test User1' limit 1];
                        
        system.runAs(oUser)
        {
          Test.StartTest();
            RTIServiceSummaryRequest_H_HUM oRequest = new RTIServiceSummaryRequest_H_HUM();
            RTIServiceSummaryRequest_H_HUM.GetInteractionsSummary rtiServiceSummary = new RTIServiceSummaryRequest_H_HUM.GetInteractionsSummary();
            system.assert(rtiServiceSummary != null); 
            RTIServiceSummaryRequest_H_HUM.Channel rtiChannels = new RTIServiceSummaryRequest_H_HUM.Channel();
            system.assert(rtiChannels != null); 
          Test.StopTest();
        }
    }      
}