/**************************************************************************** 
Apex Class Name  : RTIServiceDetailResponse_T_HUM
Version          : 
Created Date     : June 28, 2017
Function         : test class for RTIServiceDetailResponse_H_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Shivam Anand               106733             28/06/2017                  Original Version
***************************************************************************************************/

@isTest    
private class RTIServiceDetailResponse_T_HUM {  

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
    * Method Name : testRTIServiceDetailResponse
    * Description : Test method for RTIServiceDetailResponse_H_HUM
    * Return type : NA
    * Paramater   : NA
    */
    static testMethod void testRTIServiceDetailResponse()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];             
        system.runAs(oUser)
        {
            Test.StartTest();
            string testResponse = '{\"nbRecord\":1578,' +
                                    '\"mas\":[{\"actionTime\":\"2012-01-01T00:00:00z\",\"siteId\":\"Test Site Id\",\"pageId\":\"Test Page Id\",\"actionTypeDesc\":\"Test Action Type Desc\",\"code\":\"Test Code\",\"elementName\":\"Test Element Name\",\"eventActionTypeCD\":\"Test Action Type CD\",\"eventName\":\"Test Event Name\",\"prodName\":\"Test Prod Name\",\"purpose\":\"Test Purpose\",\"shopActionTypeCD\":\"Test Shop Action Type CD\"}],' +
                                    '\"mbs\":[{\"actionTime\":\"2012-01-01T00:00:00z\",\"siteId\":\"Test Site Id\",\"pageId\":\"Test Page Id\",\"actionTypeDesc\":\"Test Action Type Desc\",\"code\":\"Test Code\",\"elementName\":\"Test Element Name\",\"eventActionTypeCD\":\"Test Action Type CD\",\"eventName\":\"Test Event Name\",\"prodName\":\"Test Prod Name\",\"purpose\":\"Test Purpose\",\"shopActionTypeCD\":\"Test Shop Action Type CD\"}],' + 
                                    '\"wbs\":[{\"actionTime\":\"2012-01-01T00:00:00z\",\"siteId\":\"Test Site Id\",\"pageId\":\"Test Page Id\",\"actionTypeDesc\":\"Test Action Type Desc\",\"code\":\"Test Code\",\"elementName\":\"Test Element Name\",\"eventActionTypeCD\":\"Test Action Type CD\",\"eventName\":\"Test Event Name\",\"prodName\":\"Test Prod Name\",\"purpose\":\"Test Purpose\",\"shopActionTypeCD\":\"Test Shop Action Type CD\"}],' + 
                                    '\"ivs\":[{\"actionName\":\"Test Action Name\",\"actionTime\":\"2012-01-01T00:00:00z\",\"businessComments\":\"Test Business Comments\",\"businessDesc\":\"Test Business Description\",\"businessVal\":\"Test Business Value\",\"memberResp\":\"Test Member Response\",\"moduleName\":\"Test Module Name\"}]' +                                      
                                   '}';        
            try 
            {
                RTIServiceDetailResponse_H_HUM oResponse = RTIServiceDetailResponse_H_HUM.parse(testResponse); 
                RTIServiceDetailResponse_H_HUM.InteractionDetailResponse oInteractionSummaryResponse =  new  RTIServiceDetailResponse_H_HUM.InteractionDetailResponse();
                RTIServiceDetailResponse_H_HUM.InteractionDetailResult   oInteractionSummaryResult = new RTIServiceDetailResponse_H_HUM.InteractionDetailResult();
                List<RTIServiceDetailResponse_H_HUM.PRINT> oInteractionsList = new  List<RTIServiceDetailResponse_H_HUM.PRINT>();
                system.assert(oResponse!=null);               
            } 
            catch (Exception ex) 
            {
                System.assert(false, ex.getMessage()); // Parse failure
            }
          Test.StopTest();
        }
    }        
}