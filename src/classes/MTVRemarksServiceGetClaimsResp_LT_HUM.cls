/**************************************************************************** 
Apex Class Name  : MTVRemarksServiceGetClaimsResp_LT_HUM
Version          : 1.0  
Created Date     : 06/01/2022
Function         : test class for MTVRemarksServiceGetClaimsResponse_LH_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Prashant Moghe                                06/01/2022                  Original Version
***************************************************************************************************/
@isTest
private Class MTVRemarksServiceGetClaimsResp_LT_HUM 
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
     * Description : Test method for MTVRemarksServiceGetClaimsResponse_LH_HUM
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testMTVRemarksServiceGetClaimsResponse()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser)
        {
          Test.StartTest();
           MTVRemarksServiceGetClaimResponse_LH_HUM oResponse = new MTVRemarksServiceGetClaimResponse_LH_HUM();
           MTVRemarksServiceGetClaimResponse_LH_HUM.MTVRemarksResponse oMTVRemarksResponse = new MTVRemarksServiceGetClaimResponse_LH_HUM.MTVRemarksResponse();
           MTVRemarksServiceGetClaimResponse_LH_HUM.RemarksList remarksList = new MTVRemarksServiceGetClaimResponse_LH_HUM.RemarksList();
           MTVRemarksServiceGetClaimResponse_LH_HUM.Remarks remarks = new MTVRemarksServiceGetClaimResponse_LH_HUM.Remarks();
           system.assert(oResponse!=null);
          Test.StopTest();
        }
    }  
    
}