/**************************************************************************** 
Apex Class Name  : PharmacyServiceV2SearchResponse_T_HUM 
Version          : 1.0  
Created Date     : October 29, 2015
Function         : test class for PharmacyServiceV2SearchResponse_H_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Gargey Sharma		        27449             10/29/2015                    Original Version
* Shruthi Karanth			27819			  11/16/2015					Updated the response structure as per ICD . Defect 206691.0001
***************************************************************************************************/
@isTest
private Class PharmacyServiceV2SearchResponse_T_HUM
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
     * Method Name : testPharmacyServiceV2SearchResponse
     * Description : Test method for PharmacyServiceV2SearchResponse_T_HUM
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testPharmacyServiceV2SearchResponse()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        Test.setMock(HttpCalloutMock.class, new PharmacyServiceV2MockResponse_T_HUM());               
        system.runAs(oUser)
        {
          Test.StartTest();
            PharmacyServiceV2SearchResponse_H_HUM oSearchResponse = new PharmacyServiceV2SearchResponse_H_HUM();
            PharmacyServiceV2SearchResponse_H_HUM.PharmacySearchDrugResponse_H_HUM oRespDrugs = new PharmacyServiceV2SearchResponse_H_HUM.PharmacySearchDrugResponse_H_HUM();
            PharmacyServiceV2SearchResponse_H_HUM.PharmacySearchV2DrugSearch_H_HUM oDrugSearch = new PharmacyServiceV2SearchResponse_H_HUM.PharmacySearchV2DrugSearch_H_HUM();
            system.assert(oSearchResponse.SearchDrugResponse == null);
          Test.StopTest();
        }
    }  
}