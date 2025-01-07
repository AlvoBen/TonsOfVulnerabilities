/**************************************************************************** 
Apex Class Name  : AccumsServiceRequest_T_HUM
Version          : 1.0  
Created Date     : January 19, 2016
Function         : test class for AccumsServiceRequest_H_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* ChiranjeeviRao            29511               01/19/2016                    Original Version
* Gargey Sharma				29511				01/27/2016					  Updated as per changed Parameter names
***************************************************************************************************/
@isTest
private Class AccumsServiceRequest_T_HUM
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
     * Method Name : testAccumsServiceRequest
     * Description : Test method for  AccumsServiceRequest_T_HUM
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testAccumsServiceRequest()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser)
        {
          Test.StartTest();
            AccumsServiceRequest_H_HUM oRequest = new AccumsServiceRequest_H_HUM();
            AccumsServiceRequest_H_HUM.GetBenefitAccums getbenReqs = new AccumsServiceRequest_H_HUM.GetBenefitAccums();
            AccumsServiceRequest_H_HUM.request req = new AccumsServiceRequest_H_HUM.request();
            AccumsServiceRequest_H_HUM.BenefitAccumsRequests benAccumsreqs = new AccumsServiceRequest_H_HUM.BenefitAccumsRequests();
            AccumsServiceRequest_H_HUM.BenefitAccumsRequest benAccreq  = new AccumsServiceRequest_H_HUM.BenefitAccumsRequest();
            system.assert(oRequest!=null);
          Test.StopTest();
        }
    }  
}