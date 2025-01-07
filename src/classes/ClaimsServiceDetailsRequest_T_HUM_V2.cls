/**************************************************************************** 
Apex Class Name  : ClaimsServiceDetailsRequest_T_HUM_V2
Version          : 1.0  
Created Date     : March 06,2018
Function         : test class for ClaimsServiceDetailsRequest_H_HUM_V2
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Prasanthi Kandula          212620             03/06/2018                  Original Version
***************************************************************************************************/
@isTest
private Class ClaimsServiceDetailsRequest_T_HUM_V2
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
     * Method Name : testClaimsServiceDetailsRequest
     * Description : Test method for ClaimsServiceDetailsRequest_H_HUM
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testClaimsServiceDetailsRequest()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser)
        {
            Test.StartTest();
            ClaimsServiceDetailsRequest_H_HUM_V2 oRequest = new ClaimsServiceDetailsRequest_H_HUM_V2();
            ClaimsServiceDetailsRequest_H_HUM_V2.GetClaimDetail clmDetails = new ClaimsServiceDetailsRequest_H_HUM_V2.GetClaimDetail();
            system.assert(oRequest != null);
            Test.StopTest();
        }
    }  
}