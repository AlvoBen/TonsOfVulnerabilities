/**************************************************************************** 
Apex Class Name  : MTVRemarksServiceGetClaimsResponse_T_HUM
Version          : 1.0  
Created Date     : February 18,2016
Function         : test class for MTVRemarksServiceGetClaimsResponse_H_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* ChiranjeeviRao Ravuri   30111                02/18/2016                  Original Version
* ChiranjeeviRao Ravuri   30351                03/02/2016                  Modified as per new response structure changes 
***************************************************************************************************/
@isTest
private Class MTVRemarksServiceGetClaimsResponse_T_HUM 
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
     * Method Name : testMTVRemarksServiceGetClaimsResponse
     * Description : Test method for MTVRemarksServiceGetClaimsResponse_H_HUM
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testMTVRemarksServiceGetClaimsResponse()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser)
        {
          Test.StartTest();
           MTVRemarksServiceGetClaimsResponse_H_HUM oResponse = new MTVRemarksServiceGetClaimsResponse_H_HUM();
           MTVRemarksServiceGetClaimsResponse_H_HUM.MTVRemarksResponse oMTVRemarksResponse = new MTVRemarksServiceGetClaimsResponse_H_HUM.MTVRemarksResponse();
           MTVRemarksServiceGetClaimsResponse_H_HUM.RemarksList remarksList = new MTVRemarksServiceGetClaimsResponse_H_HUM.RemarksList();
           MTVRemarksServiceGetClaimsResponse_H_HUM.Remarks remarks = new MTVRemarksServiceGetClaimsResponse_H_HUM.Remarks();
           system.assert(oResponse!=null);
          Test.StopTest();
        }
    }  
    
}