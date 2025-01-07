/**************************************************************************** 
Apex Class Name  : HealthResourcesResponse_DTO_T_HUM 
Version          : 
Created Date     : April 14th, 2020
Function         : Response Wrapper for Rewardservice Service In Humana Rewards
Test Class       : Test Class for HealthResourcesResponse_DTO_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
*  Imran                                       04/14/2020                  Original Version
*  Ranjeeth N                                 08/13/2020                   updated test class
*************************************************************************************************** */
@isTest
private class HealthResourcesResponse_DTO_T_HUM {
/*
* Method Name : testSetup 
* Description : Test method to setup common test data for all test methods     
* Return type : void     
* Paramater   : NA  
*/
@testSetup 
    static void testSetup()
    {        
        //HUMTestDataHelperTest.getHUMConstantsData();
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
    }
/*
* Method Name : testHealthresponseWrapper
* Description : Test method for ClaimsServiceDetailsResponse_H_HUM
* Return type : NA
* Paramater   : NA
*/
  static testMethod void testHealthresponsehelper()
    {            
           User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser)
        {
          Test.StartTest(); 
          HealthResourcesResponse_DTO_HUM ohealthres = new HealthResourcesResponse_DTO_HUM();  
          HealthResourcesResponse_DTO_HUM.GroupClinicalProgramsResponse oClincalres = new HealthResourcesResponse_DTO_HUM.GroupClinicalProgramsResponse();
          HealthResourcesResponse_DTO_HUM.GroupClinicalProgramsDetails oclicnalPrg= new HealthResourcesResponse_DTO_HUM.GroupClinicalProgramsDetails();
          system.assert(ohealthres !=null);   
            Test.StopTest();
       }
    } 
}