/**************************************************************************** 
Apex Class Name  : MobiusServiceGetDocListRequest_T_HUM 
Version          : 1.0  
Created Date     : January 06, 2016
Function         : test class for MobiusServiceGetDocListRequest_H_HUM  
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Gargey Sharma             29147            01/06/2016                    Original Version
* Gargey Sharma             30253            03/02/2016                    Made changes as method name changed to GetDocumentList
***************************************************************************************************/
@isTest
private class MobiusServiceGetDocListRequest_T_HUM 
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
            MobiusServiceGetDocListRequest_H_HUM oRequest = new MobiusServiceGetDocListRequest_H_HUM();
            MobiusServiceGetDocListRequest_H_HUM.getDocumentList getDoclst = new MobiusServiceGetDocListRequest_H_HUM.getDocumentList();
            MobiusServiceGetDocListRequest_H_HUM.getDocument Doc = new MobiusServiceGetDocListRequest_H_HUM.getDocument();
            MobiusServiceGetDocListRequest_H_HUM.DocumentSearch DocSearch = new MobiusServiceGetDocListRequest_H_HUM.DocumentSearch();
            MobiusServiceGetDocListRequest_H_HUM.TopicSearchList topiclst = new MobiusServiceGetDocListRequest_H_HUM.TopicSearchList();
            MobiusServiceGetDocListRequest_H_HUM.TopicSearch topicsearch = new MobiusServiceGetDocListRequest_H_HUM.TopicSearch();
            system.assert(oRequest != null);
          Test.StopTest(); 
        }
    }  
}