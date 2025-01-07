/**************************************************************************** 
Apex Class Name  : AccumsServiceResponse_T_HUM
Version          : 1.0  
Created Date     : January 19, 2016
Function         : test class for AccumsServiceResponse_H_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Gargey Sharma             29511            01/20/2016                    Original Version
***************************************************************************************************/
@isTest
private Class AccumsServiceResponse_T_HUM
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
     * Method Name : testAccumsServiceResponse
     * Description : Test method for AccumsServiceResponse_H_HUM
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testAccumsServiceResponse()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser)
        {
          Test.StartTest();
            AccumsServiceResponse_H_HUM oResponse = new AccumsServiceResponse_H_HUM();
            AccumsServiceResponse_H_HUM.GetBenefitAccumsResponse getbenresp = new AccumsServiceResponse_H_HUM.GetBenefitAccumsResponse();
            AccumsServiceResponse_H_HUM.GetBenefitAccumsResult getbenresult = new AccumsServiceResponse_H_HUM.GetBenefitAccumsResult();
            AccumsServiceResponse_H_HUM.BenefitAccumsResponseHeader benrespheader = new AccumsServiceResponse_H_HUM.BenefitAccumsResponseHeader();
            AccumsServiceResponse_H_HUM.BenefitAccumsResponses benAccResps = new AccumsServiceResponse_H_HUM.BenefitAccumsResponses();
            AccumsServiceResponse_H_HUM.BenefitAccumsResponse benAccResp = new AccumsServiceResponse_H_HUM.BenefitAccumsResponse();
            AccumsServiceResponse_H_HUM.BenefitAccumsDataList benAccDataList = new AccumsServiceResponse_H_HUM.BenefitAccumsDataList();
            AccumsServiceResponse_H_HUM.BenefitAccumsData benAccData = new AccumsServiceResponse_H_HUM.BenefitAccumsData();
            AccumsServiceResponse_H_HUM.BenefitAccumsRequestInfo benAccReqInfo = new AccumsServiceResponse_H_HUM.BenefitAccumsRequestInfo();
            system.assert(oResponse!=null);
          Test.StopTest();
        }
    }  
}