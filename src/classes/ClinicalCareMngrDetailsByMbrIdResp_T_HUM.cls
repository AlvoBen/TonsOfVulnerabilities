/**************************************************************************** 
Apex Class Name  : ClinicalCareMngrDetailsByMbrIdResp_T_HUM
Version          : 1.0  
Created Date     : August 7, 2018
Function         : ClinicalCareMngrDetailsByMbrIdResp_T_HUM 
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Divya Agrawal                               8/07/2018                    Original Version
***************************************************************************************************/
@isTest
private Class ClinicalCareMngrDetailsByMbrIdResp_T_HUM
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
    * Test method for ClinicalCareMngrDetailsByMbrIdResp_H_HUM
    * @param None
    * @return Void
    */
    static testMethod void testClinicalCareManagerbyMemberIdResponse()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser)
        {
          Test.StartTest();
              ClinicalCareMngrDetailsByMbrIdResp_H_HUM oResponse = new ClinicalCareMngrDetailsByMbrIdResp_H_HUM();
              ClinicalCareMngrDetailsByMbrIdResp_H_HUM.CareManagerbyMemberIdResponse cmgrResponse = new ClinicalCareMngrDetailsByMbrIdResp_H_HUM.CareManagerbyMemberIdResponse(); 
              ClinicalCareMngrDetailsByMbrIdResp_H_HUM.CareManagerDetails cmgrDetails = new ClinicalCareMngrDetailsByMbrIdResp_H_HUM.CareManagerDetails();
              ClinicalCareMngrDetailsByMbrIdResp_H_HUM.CareManager cmgrDetail = new ClinicalCareMngrDetailsByMbrIdResp_H_HUM.CareManager();
              
              System.assert(oResponse !=null);
          Test.StopTest();
        }
    }  
 }