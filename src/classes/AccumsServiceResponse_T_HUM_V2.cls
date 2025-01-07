/**************************************************************************** 
Apex Class Name  : AccumsServiceResponse_T_HUM_V2
Version          : 2.0  
Created Date     : January 20, 2020
Function         : test class for AccumsServiceResponse_H_HUM_V2
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Gargey Sharma              29511            01/20/2016                   Original Version
* Shailesh Mali	                              01/20/2020                   Version - 2, REQ - 791947 Accumulator SOAP to REST Conversion
***************************************************************************************************/
@isTest
private class AccumsServiceResponse_T_HUM_V2 {
	
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
        User oUser = HUMTestDataHelperTest.createTestUser('AccumsServiceResponseUser1','Customer Care Specialist');
        insert oUser;
    }
    
    /*
     * Method Name : testAccumsServiceResponse
     * Description : Test method for AccumsServiceResponse_H_HUM_V2
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testAccumsServiceResponse()
    {            
        User oUser = [select Id,Name from User where name = 'test AccumsServiceResponseUser1' limit 1];                
        system.runAs(oUser)
        {
          Test.StartTest();
            AccumsServiceResponse_H_HUM_V2 oResponse = new AccumsServiceResponse_H_HUM_V2();
            AccumsServiceResponse_H_HUM_V2.GetBenefitAccumsResponse getbenresp = new AccumsServiceResponse_H_HUM_V2.GetBenefitAccumsResponse();
            AccumsServiceResponse_H_HUM_V2.GetBenefitAccumsResult getbenresult = new AccumsServiceResponse_H_HUM_V2.GetBenefitAccumsResult();
            AccumsServiceResponse_H_HUM_V2.BenefitAccumsResponseHeader benrespheader = new AccumsServiceResponse_H_HUM_V2.BenefitAccumsResponseHeader();
            AccumsServiceResponse_H_HUM_V2.BenefitAccumsResponses benAccResps = new AccumsServiceResponse_H_HUM_V2.BenefitAccumsResponses();
            AccumsServiceResponse_H_HUM_V2.BenefitAccumsResponse benAccResp = new AccumsServiceResponse_H_HUM_V2.BenefitAccumsResponse();
            AccumsServiceResponse_H_HUM_V2.BenefitAccumsDataList benAccDataList = new AccumsServiceResponse_H_HUM_V2.BenefitAccumsDataList();
            AccumsServiceResponse_H_HUM_V2.BenefitAccumsData benAccData = new AccumsServiceResponse_H_HUM_V2.BenefitAccumsData();
            AccumsServiceResponse_H_HUM_V2.BenefitAccumsRequestInfo benAccReqInfo = new AccumsServiceResponse_H_HUM_V2.BenefitAccumsRequestInfo();
            system.assert(oResponse!=null);
          Test.StopTest();
        }
    }  
}