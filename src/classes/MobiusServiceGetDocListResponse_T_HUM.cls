/**************************************************************************** 
Apex Class Name  : MobiusServiceGetDocListResponse_T_HUM 
Version          : 1.0  
Created Date     : January 06, 2016
Function         : test class for MobiusServiceGetDocListResponse_H_HUM  
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Gargey Sharma             29147            01/06/2016                    Original Version
* Gargey Sharma             30154            02/25/2016                    REQ - 252568: Updated request structure as per updates in service
* Gargey Sharma             30253            03/02/2016                    Made changes as method name changed to GetDocumentList
***************************************************************************************************/
@isTest
private class MobiusServiceGetDocListResponse_T_HUM 
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
     * Method Name : testMobiusServiceGetDocListResponse
     * Description : Test method for MobiusServiceGetDocListResponse_H_HUM 
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testMobiusServiceGetDocListResponse()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];               
        system.runAs(oUser)
        {
          Test.StartTest();
            MobiusServiceGetDocListResponse_H_HUM oResponse = new MobiusServiceGetDocListResponse_H_HUM();
            MobiusServiceGetDocListResponse_H_HUM.getDocumentListResponse getDoc = new MobiusServiceGetDocListResponse_H_HUM.getDocumentListResponse();
            MobiusServiceGetDocListResponse_H_HUM.getDocumentListReturn getDocret = new MobiusServiceGetDocListResponse_H_HUM.getDocumentListReturn();
            MobiusServiceGetDocListResponse_H_HUM.DocumentSearch DocSearch = new MobiusServiceGetDocListResponse_H_HUM.DocumentSearch();
            MobiusServiceGetDocListResponse_H_HUM.TopicResultList topicreslst = new MobiusServiceGetDocListResponse_H_HUM.TopicResultList();
            MobiusServiceGetDocListResponse_H_HUM.TopicResult topicres = new MobiusServiceGetDocListResponse_H_HUM.TopicResult();
            MobiusServiceGetDocListResponse_H_HUM.TopicKeyList topickeylst = new MobiusServiceGetDocListResponse_H_HUM.TopicKeyList();
            MobiusServiceGetDocListResponse_H_HUM.TopicKey topickey = new MobiusServiceGetDocListResponse_H_HUM.TopicKey();
            system.assert(oResponse != null);
          Test.StopTest(); 
        }
    }  
}