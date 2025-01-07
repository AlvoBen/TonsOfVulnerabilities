/**************************************************************************** 
Apex Class Name  : ClinicalCareManagerDetailsRequest_T_HUM 
Version          : 1.0  
Created Date     : July 6, 2016
Function         : test class for ClinicalCareManagerDetailsRequest_H_HUM 
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* ChiranjeeviRao             34002             7/06/2016                    Original Version
***************************************************************************************************/
@isTest
private Class ClinicalCareManagerDetailsRequest_T_HUM 
{

    /*
    * This is a test method to setup common test data for all test method
    * @param None
    * @return Void
    */
    @testSetup 
    static void testSetup()
    {        
        HUMTestDataHelperTest.getHUMConstantsData();
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
    }
     
    /*
    * Test method for ClinicalCareManagerDetailsRequest_H_HUM
    * @param None
    * @return Void
    */
    static testMethod void testClinicalCareManagerDetailsRequest()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser)
        {
          Test.StartTest();
          ClinicalCareManagerDetailsRequest_H_HUM oRequest = new ClinicalCareManagerDetailsRequest_H_HUM();
          ClinicalCareManagerDetailsRequest_H_HUM.CareManagerRequest cmgrRequest = new ClinicalCareManagerDetailsRequest_H_HUM.CareManagerRequest();
          system.assert(oRequest!=null);
          Test.StopTest();
        }
    }  
 }