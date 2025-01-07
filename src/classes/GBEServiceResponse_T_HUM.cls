/**************************************************************************** 
Apex Class Name  : GBEServiceResponse_T_HUM
Version          : 1.0  
Created Date     : February 09,2016
Function         : test class for GBEServiceResponse_H_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* ChiranjeeviRao Ravuri    29854               02/09/2016                    Original Version
* ChiranjeeviRao Ravuri    30345               03/10/2016                   REQ-255066:Made changes as per new response structure
***************************************************************************************************/
@isTest
private Class GBEServiceResponse_T_HUM
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
     * Method Name : testGBEServiceResponse
     * Description : Test method for GBEServiceResponse_H_HUM
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testGBEServiceResponse()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser)
        {
          Test.StartTest();
            GBEServiceResponse_H_HUM oResponse = new GBEServiceResponse_H_HUM();
            GBEServiceResponse_H_HUM.GroupInfo groupInfo = new GBEServiceResponse_H_HUM.GroupInfo();
            GBEServiceResponse_H_HUM.Benefit benefit = new GBEServiceResponse_H_HUM.Benefit();
            GBEServiceResponse_H_HUM.Exchange exchange = new GBEServiceResponse_H_HUM.Exchange();
            GBEServiceResponse_H_HUM.DivisionList divisionList = new GBEServiceResponse_H_HUM.DivisionList();
            GBEServiceResponse_H_HUM.Division division = new GBEServiceResponse_H_HUM.Division();   
            GBEServiceResponse_H_HUM.EffectiveProvisionList effProvList = new GBEServiceResponse_H_HUM.EffectiveProvisionList();
            GBEServiceResponse_H_HUM.EffectiveProvision effProv = new GBEServiceResponse_H_HUM.EffectiveProvision(); 
            GBEServiceResponse_H_HUM.OpenEnrollmentList openEnrollList = new GBEServiceResponse_H_HUM.OpenEnrollmentList(); 
            GBEServiceResponse_H_HUM.OpenEnrollment openEnroll = new GBEServiceResponse_H_HUM.OpenEnrollment();   
            system.assert(oResponse!=null);
          Test.StopTest();
        }
    }  
    
}