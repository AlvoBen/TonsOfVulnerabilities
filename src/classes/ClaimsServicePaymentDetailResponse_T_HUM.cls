/**************************************************************************** 
Apex Class Name  : ClaimsServicePaymentDetailResponse_T_HUM
Version          : 1.0  
Created Date     : February 26, 2016
Function         : test class for ClaimsServicePaymentDetailResponse_H_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Gargey Sharma				30282			 02/26/2016 				    Original Version
* Gargey Sharma            	30970            04/13/2016                     Added ClaimDetails
***************************************************************************************************/
@isTest
private Class ClaimsServicePaymentDetailResponse_T_HUM
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
     * Method Name : testClaimsServicePaymentDetailResponse
     * Description : Test method for ClaimsServicePaymentDetailResponse_H_HUM
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testClaimsServicePaymentDetailResponse()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser)
        {
          Test.StartTest();
          	ClaimsServicePaymentDetailResponse_H_HUM oResponse = new ClaimsServicePaymentDetailResponse_H_HUM();
            ClaimsServicePaymentDetailResponse_H_HUM.GetEhub835XMLResponse xmlresp = new ClaimsServicePaymentDetailResponse_H_HUM.GetEhub835XMLResponse();
            ClaimsServicePaymentDetailResponse_H_HUM.GetEhub835XMLResult xmlreslt = new ClaimsServicePaymentDetailResponse_H_HUM.GetEhub835XMLResult();
            ClaimsServicePaymentDetailResponse_H_HUM.Transaction_835 trans = new ClaimsServicePaymentDetailResponse_H_HUM.Transaction_835();
            ClaimsServicePaymentDetailResponse_H_HUM.ClaimDetails clmdetails = new ClaimsServicePaymentDetailResponse_H_HUM.ClaimDetails();
            ClaimsServicePaymentDetailResponse_H_HUM.ClaimDetail det = new ClaimsServicePaymentDetailResponse_H_HUM.ClaimDetail();
            system.assert(oResponse!=null);
          Test.StopTest();
        }
    }  
}