/**************************************************************************** 
Apex Class Name  : RTIServiceSummaryResponse_T_HUM
Version          : 
Created Date     : Novemeber 21, 2016
Function         : test class for RTIServiceSummaryResponse_H_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Pallavi Ravishankar                          01/31/2017               Original Version
***************************************************************************************************/

@isTest
private class RTIServiceSummaryResponse_T_HUM {

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
     * Method Name : testRTIServiceSummaryResponse
     * Description : Test method for RTIServiceSummaryResponse_H_HUM
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testRTIServiceSummaryResponse()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];
                        
        system.runAs(oUser)
        {
          Test.StartTest();
            string testResponse = '{\"nbRecord\":1578,\"interactions\":[' + 
                                  '{\"interactionsrckey\":\"key1\",\"sourceDescription\":\"DM\",\"sourceCode\":\"CCPOT\",\"channelCode\":\"DM\",\"channelDescription\":\"DM\",\"directionCode\":\"I\",\"actionTime\":\"2012-01-01T00:00:00z\","dm":{\"categoryCode\":\"Test Category Code\",\"categoryDescription\":\"Test Category Description\",\"startdatetime\":\"2012-01-01T00:00:00z\",\"enddatetime\":\"2012-01-01T00:00:00z\",\"businessArea\":\"Test Business Area\",\"businessSubArea\":\"Test Business Sub Area\",\"statusCode\":\"Test Status Code\",\"statusDescription\":\"Test Status Description\",\"IsFinal\":false,\"departmentName\":\"Test Department Name\"}},' +
                                  '{\"interactionsrckey\":\"key2\",\"sourceDescription\":\"IVR\",\"sourceCode\":\"CCPOT\",\"channelCode\":\"IV\",\"channelDescription\":\"IVR\",\"directionCode\":\"I\",\"actionTime\":\"2012-01-01T00:00:00z\","iv":{\"categoryCode\":\"Test Category Code\",\"categoryDescription\":\"Test Category Description\",\"startdatetime\":\"2012-01-01T00:00:00z\",\"enddatetime\":\"2012-01-01T00:00:00z\",\"interactingWithCode\":\"Test Interacting With\",\"interactingWithDesc\":\"Test Interacting With Desc\",\"statusDescription\":\"Test Status Description\",\"statusCode\":\"Test Status Code\",\"lastModuleName\":\"Test Last Module Name\",\"lastModuleCD\":\"Test Last Module Code\",\"departmentName\":\"Test Department Name\"}},' +
                                  '{\"interactionsrckey\":\"key3\",\"sourceDescription\":\"MA\",\"sourceCode\":\"CCPOT\",\"channelCode\":\"MA\",\"channelDescription\":\"MA\",\"directionCode\":\"I\",\"actionTime\":\"2012-01-01T00:00:00z\","ma":{\"interactingWith\":\"Test Interacting With\"}},' +
                                  '{\"interactionsrckey\":\"key4\",\"sourceDescription\":\"MB\",\"sourceCode\":\"CCPOT\",\"channelCode\":\"MB\",\"channelDescription\":\"MB\",\"directionCode\":\"I\",\"actionTime\":\"2012-01-01T00:00:00z\","mb":{\"interactingWith\":\"Test Interacting With\"}},' +
                                  '{\"interactionsrckey\":\"key5\",\"sourceDescription\":\"WB\",\"sourceCode\":\"CCPOT\",\"channelCode\":\"WB\",\"channelDescription\":\"WB\",\"directionCode\":\"I\",\"actionTime\":\"2012-01-01T00:00:00z\","wb":{\"interactingWith\":\"Test Interacting With\"}}' +                                                                    
                                  ']}';
            try {
                RTIServiceSummaryResponse_H_HUM oResponse = RTIServiceSummaryResponse_H_HUM.parse(testResponse); 
                RTIServiceSummaryResponse_H_HUM.InteractionSummaryResponse oInteractionSummaryResponse =  new  RTIServiceSummaryResponse_H_HUM.InteractionSummaryResponse();
                RTIServiceSummaryResponse_H_HUM.InteractionSummaryResult oInteractionSummaryResult = new RTIServiceSummaryResponse_H_HUM.InteractionSummaryResult();
                List<RTIServiceSummaryResponse_H_HUM.Interaction> oInteractionsList = new  List<RTIServiceSummaryResponse_H_HUM.Interaction>();
                system.assert(oResponse!=null);
                               
            } catch (Exception ex) {
                System.assert(false, ex.getMessage()); // Parse failure
            }
          Test.StopTest();
        }
    }      
}