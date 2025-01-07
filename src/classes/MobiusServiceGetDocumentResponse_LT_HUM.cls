/**************************************************************************** 
Apex Class Name  : MobiusServiceGetDocumentResponse_LT_HUM 
Version          : 1.0  
Created Date     : 06/01/2022  
Function         : test class for MobiusServiceGetDocumentResponse_LH_HUM  
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Prashant Moghe		                        06/01/2022                 Original Version
***************************************************************************************************/
@isTest
private class MobiusServiceGetDocumentResponse_LT_HUM 
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
     * Method Name : testMobiusServiceGetDocumentResponse
     * Description : Test method for MobiusServiceGetDocumentResponse_H_HUM 
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testMobiusServiceGetDocumentResponse()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];               
        system.runAs(oUser)
        {
          Test.StartTest();
            MobiusServiceGetDocumentResponse_LH_HUM oResponse = new MobiusServiceGetDocumentResponse_LH_HUM();
            MobiusServiceGetDocumentResponse_LH_HUM.getDocumentResponse docResp = new MobiusServiceGetDocumentResponse_LH_HUM.getDocumentResponse(); 
          	system.assert(oResponse != null);
          Test.StopTest(); 
        }
    }  
}