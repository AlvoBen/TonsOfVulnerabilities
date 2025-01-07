/**************************************************************************** 
Apex Class Name  : MobiusServiceGetDocListRequest_LT_HUM 
Version          : 1.0  
Created Date     : 06/01/2022
Function         : test class for MobiusServiceGetDocListRequest_LH_HUM  
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Prashant Moghe 	   	                   		    06/01/2022                 Original Version
***************************************************************************************************/
@isTest
private class MobiusServiceGetDocListRequest_LT_HUM 
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
     * Method Name : testMobiusServiceGetDocListRequest
     * Description : Test method for MobiusServiceGetDocListRequest_H_HUM 
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testMobiusServiceGetDocListRequest()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];               
        system.runAs(oUser)
        {
          Test.StartTest();
            MobiusServiceGetDocListRequest_LH_HUM oRequest = new MobiusServiceGetDocListRequest_LH_HUM();
            MobiusServiceGetDocListRequest_LH_HUM.getDocumentList getDoclst = new MobiusServiceGetDocListRequest_LH_HUM.getDocumentList();
            MobiusServiceGetDocListRequest_LH_HUM.getDocument Doc = new MobiusServiceGetDocListRequest_LH_HUM.getDocument();
            MobiusServiceGetDocListRequest_LH_HUM.DocumentSearch DocSearch = new MobiusServiceGetDocListRequest_LH_HUM.DocumentSearch();
            MobiusServiceGetDocListRequest_LH_HUM.TopicSearchList topiclst = new MobiusServiceGetDocListRequest_LH_HUM.TopicSearchList();
            MobiusServiceGetDocListRequest_LH_HUM.TopicSearch topicsearch = new MobiusServiceGetDocListRequest_LH_HUM.TopicSearch();
            system.assert(oRequest != null);
          Test.StopTest(); 
        }
    }  
}