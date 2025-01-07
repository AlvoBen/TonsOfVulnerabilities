/**************************************************************************** 
Apex Class Name  : ClinicalCareManagerDetailsResponse_T_HUM  
Version          : 1.0  
Created Date     : July 6, 2016
Function         : test class for ClinicalCareManagerDetailsResponse_H_HUM 
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* ChiranjeeviRao             34002            7/06/2016                    Original Version
* Shruthi Karanth            34002            7/28/2016                    Restructured as per changes in ICD
***************************************************************************************************/
@isTest
private Class ClinicalCareManagerDetailsResponse_T_HUM 
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
    * Test method for ClinicalCareManagerDetailsResponse_H_HUM
    * @param None
    * @return Void
    */
    static testMethod void testClinicalCareManagerDetailsResponse()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser)
        {
          Test.StartTest();
              ClinicalCareManagerDetailsResponse_H_HUM oResponse = new ClinicalCareManagerDetailsResponse_H_HUM();
              ClinicalCareManagerDetailsResponse_H_HUM.CareManagerResponse cmgrResponse = new ClinicalCareManagerDetailsResponse_H_HUM.CareManagerResponse(); 
              ClinicalCareManagerDetailsResponse_H_HUM.CareManagerDetails cmgrDetails = new ClinicalCareManagerDetailsResponse_H_HUM.CareManagerDetails();
              ClinicalCareManagerDetailsResponse_H_HUM.CareManagerDetail cmgrDetail = new ClinicalCareManagerDetailsResponse_H_HUM.CareManagerDetail();
              ClinicalCareManagerDetailsResponse_H_HUM.QueueOwnerDetails queOwnDetails = new ClinicalCareManagerDetailsResponse_H_HUM.QueueOwnerDetails();
              ClinicalCareManagerDetailsResponse_H_HUM.QueueOwnerDetail queOwnDetail = new ClinicalCareManagerDetailsResponse_H_HUM.QueueOwnerDetail(); 
              system.assert(oResponse !=null);
          Test.StopTest();
        }
    }  
 }