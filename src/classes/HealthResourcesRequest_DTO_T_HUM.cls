/**************************************************************************** 
Apex Class Name  : HealthResourcesRequest_DTO_T_HUM
Version          : 
Created Date     : April 14th, 2020
Function         : Response Wrapper for ClinicalProgramV2 Service In Health Resources
Test Class       : Test Class for HealthResourcesRequest_DTO_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
*  Imran                                      04/14/2020                  Original Version
*************************************************************************************************** */
@isTest 
public class HealthResourcesRequest_DTO_T_HUM
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
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertEquals(oCCSUser.LastName,'test User1');
    } 
/*
* Method Name : testHealthResourcesresponseWrapper
* Description : Test method for GroupClinicalProgramRequest_DTO_HUM
* Return type : NA
* Paramater   : NA
*/
static testMethod void testHumanaRewardsresponseWrapper()
    {            
        User oCCSUser = [SELECT Id FROM User  WHERE Lastname ='test User1' LIMIT 1];                    
        system.runAs(oCCSUser)
        {
            Test.StartTest();            
            HealthResourcesRequest_DTO_HUM oGroupClinicalProgramRequest_DTO_HUM = new HealthResourcesRequest_DTO_HUM();
            HealthResourcesRequest_DTO_HUM.GroupClinicalProgramsRequest oGetRewardsDetailsResponse=  new HealthResourcesRequest_DTO_HUM.GroupClinicalProgramsRequest();
            system.assert(oGroupClinicalProgramRequest_DTO_HUM!=null);
            Test.StopTest();
       }
    } 
}