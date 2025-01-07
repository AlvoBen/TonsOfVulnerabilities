/**************************************************************************** 
Apex Class Name  : MobiusServiceGetDocListResponse_LT_HUM 
Version          : 1.0  
Created Date     : 06/01/2022
Function         : test class for MobiusServiceGetDocListResponse_LH_HUM  
****************************************************************************

Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Prashant Moghe 	   	                   		06/01/2022                 Original Version
***************************************************************************************************/
@isTest
private class MobiusServiceGetDocListResponse_LT_HUM 
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
            MobiusServiceGetDocListResponse_LH_HUM oResponse = new MobiusServiceGetDocListResponse_LH_HUM();
            MobiusServiceGetDocListResponse_LH_HUM.getDocumentListResponse getDoc = new MobiusServiceGetDocListResponse_LH_HUM.getDocumentListResponse();
            MobiusServiceGetDocListResponse_LH_HUM.getDocumentListReturn getDocret = new MobiusServiceGetDocListResponse_LH_HUM.getDocumentListReturn();
            MobiusServiceGetDocListResponse_LH_HUM.DocumentSearch DocSearch = new MobiusServiceGetDocListResponse_LH_HUM.DocumentSearch();
            MobiusServiceGetDocListResponse_LH_HUM.TopicResultList topicreslst = new MobiusServiceGetDocListResponse_LH_HUM.TopicResultList();
            MobiusServiceGetDocListResponse_LH_HUM.TopicResult topicres = new MobiusServiceGetDocListResponse_LH_HUM.TopicResult();
            MobiusServiceGetDocListResponse_LH_HUM.TopicKeyList topickeylst = new MobiusServiceGetDocListResponse_LH_HUM.TopicKeyList();
            MobiusServiceGetDocListResponse_LH_HUM.TopicKey topickey = new MobiusServiceGetDocListResponse_LH_HUM.TopicKey();
            system.assert(oResponse != null);
          Test.StopTest(); 
        }
    }  
}