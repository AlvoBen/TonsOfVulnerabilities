/**************************************************************************** 
Apex Class Name  : RTIServiceDetailRequest_T_HUM
Version          : 1.0
Created Date     : January,12 2017
Function         : test class for RTIServiceDetailRequest_H_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review           Date                   Description
*---------------------------------------------------------------------------------------------------
*  Harika Devi Kalidindi                       01/12/2017             Original Version 
*  Shivam Anand              106733            06/28/2017            RTI Print View Link Service Call Changes
***************************************************************************************************/

@isTest
private class RTIServiceDetailRequest_T_HUM 
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
        system.assertEquals(oUser.LastName,'test User1'); 
    }
    
   /*
    * Method Name : testGetRTIServiceDetailsRequest
    * Description : Test method for RTIServiceDetailRequest_H_HUM
    * Return type : NA
    * Paramater   : NA
    */
    static testMethod void testGetRTIServiceDetailsRequest()
    {            
        User oUser = [Select id,UserName from User where name = 'test User1' limit 1];
        system.assertEquals(oUser.UserName,'user1user1@humcrmstest.com'); 
        system.runAs(oUser)
        {
            Test.StartTest();
            RTIServiceDetailRequest_H_HUM oRequest = new RTIServiceDetailRequest_H_HUM();
            RTIServiceDetailRequest_H_HUM.GetInteractionDetails rtiServiceDetail = new RTIServiceDetailRequest_H_HUM.GetInteractionDetails();
            system.assert(rtiServiceDetail != null); 
              Test.StopTest();
        }
    }      
}