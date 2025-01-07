/**************************************************************************** 
Apex Class Name  : ClaimsServicePaymentDetailsRequest_T_HUM
Version          : 1.0  
Created Date     : February 26,2016
Function         : test class for ClaimsServicePaymentDetailsRequest_H_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Gargey Sharma			     30282            02/26/2016                    Original Version
***************************************************************************************************/
@isTest
private Class ClaimsServicePaymentDetailsRequest_T_HUM
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
     * Method Name : testClaimsServicePaymentDetailsRequest
     * Description : Test method for ClaimsServicePaymentDetailsRequest_H_HUM
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testClaimsServicePaymentDetailsRequest()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser)
        {
          Test.StartTest();
            ClaimsServicePaymentDetailsRequest_H_HUM oRequest = new ClaimsServicePaymentDetailsRequest_H_HUM();
            ClaimsServicePaymentDetailsRequest_H_HUM.GetPaymentDetails paymentDetails = new ClaimsServicePaymentDetailsRequest_H_HUM.GetPaymentDetails();
            system.assert(oRequest != null);
          Test.StopTest();
        }
    }  
}