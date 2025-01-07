/**************************************************************************** 
Apex Class Name  : ClaimsServiceDetailsResponse_T_HUM_V2
Version          : 1.0  
Created Date     : March 06, 2018
Function         : Test class for ClaimsServiceDetailsResponse_H_HUM_V2
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Prasanthi Kandula            212620           03/06/2018                   Original Version
***************************************************************************************************/
@isTest
private Class ClaimsServiceDetailsResponse_T_HUM_V2
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
     * Method Name : testClaimsServiceDetailResponse
     * Description : Test method for ClaimsServiceDetailsResponse_H_HUM
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testClaimsServiceDetailResponse()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser)
        {
            Test.StartTest();
            ClaimsServiceDetailsResponse_H_HUM_V2 oResponse = new ClaimsServiceDetailsResponse_H_HUM_V2();
            ClaimsServiceDetailsResponse_H_HUM_V2.ClaimDetailReponse oClmDetailRes = new ClaimsServiceDetailsResponse_H_HUM_V2.ClaimDetailReponse();
            ClaimsServiceDetailsResponse_H_HUM_V2.ClaimDetailResult oClmDetailRslt = new ClaimsServiceDetailsResponse_H_HUM_V2.ClaimDetailResult();
            ClaimsServiceDetailsResponse_H_HUM_V2.ClaimDetailLines oClmdetlines = new ClaimsServiceDetailsResponse_H_HUM_V2.ClaimDetailLines();
            ClaimsServiceDetailsResponse_H_HUM_V2.DiagnosisCode oDiagcode = new ClaimsServiceDetailsResponse_H_HUM_V2.DiagnosisCode();
            ClaimsServiceDetailsResponse_H_HUM_V2.ClaimDetailLine oClmdetline = new ClaimsServiceDetailsResponse_H_HUM_V2.ClaimDetailLine();
            ClaimsServiceDetailsResponse_H_HUM_V2.PaymentCodes oPaycodes = new ClaimsServiceDetailsResponse_H_HUM_V2.PaymentCodes();
            ClaimsServiceDetailsResponse_H_HUM_V2.PaymentDetail oPaydetail = new ClaimsServiceDetailsResponse_H_HUM_V2.PaymentDetail();
            system.assert(oResponse!=null);
            Test.StopTest();
        }
    }  
}