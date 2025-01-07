/**************************************************************************** 
Apex Class Name  : HealthResourcesWrapper_T_HUM 
Version          : 
Created Date     : April 14th, 2020
Function         : Response Wrapper for ClinicalPrograms_V2 Service In Health resources
Test Class       : Test Class for HealthResourcesWrapper_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
*  Imran                                       04/14/2020                  Original Version
*************************************************************************************************** */
@isTest
public class HealthResourcesWrapper_T_HUM {
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
* Method Name : testHumanaRewardsresponseWrapper
* Description : Test method for GroupClinicalProgramWrapper_HUM
* Return type : NA
* Paramater   : NA
*/
static testMethod void testHealthresponseWrapper()
    {            
        User oCCSUser = [SELECT Id FROM User  WHERE Lastname ='test User1' LIMIT 1];                    
        system.runAs(oCCSUser)
        {
            Test.StartTest();
            HealthResourcesWrapper_HUM.GroupClinicalProgramsRequest oHealthResourcesDTO = new HealthResourcesWrapper_HUM.GroupClinicalProgramsRequest();
            oHealthResourcesDTO.ProgramName='Humana Behavioral Health Autism Program';           
            oHealthResourcesDTO.InterventionVendor = 'HUM';
            oHealthResourcesDTO.BeginDate = '2019-01-01T00:00:00'; 
            oHealthResourcesDTO.EndDate = '9999-12-31T00:00:00';           
            system.assertEquals(oHealthResourcesDTO.EndDate,'9999-12-31T00:00:00');
            Test.StopTest();
       }
    } 
}