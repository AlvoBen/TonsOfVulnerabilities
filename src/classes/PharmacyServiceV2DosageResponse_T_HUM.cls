/**************************************************************************** 
Apex Class Name  : PharmacyServiceV2DosageResponse_T_HUM 
Version          : 1.0  
Created Date     : October 29, 2015
Function         : test class for PharmacyServiceV2DosageResponse_H_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Gargey Sharma		        27449             10/29/2015                    Original Version
***************************************************************************************************/
@isTest
private Class PharmacyServiceV2DosageResponse_T_HUM
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
     * Method Name : testPharmacyServiceV2DosageResponse
     * Description : Test method for PharmacyServiceV2DosageResponse_T_HUM
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testPharmacyServiceV2DosageResponse()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        Test.setMock(HttpCalloutMock.class, new PharmacyServiceV2MockResponse_T_HUM());               
        system.runAs(oUser)
        {
          Test.StartTest();
            PharmacyServiceV2DosageResponse_H_HUM oDrugDosage = new PharmacyServiceV2DosageResponse_H_HUM();
            PharmacyServiceV2DosageResponse_H_HUM.PharmacyDrugDosageResponse_H_HUM oGetDrugs = new PharmacyServiceV2DosageResponse_H_HUM.PharmacyDrugDosageResponse_H_HUM();
            PharmacyServiceV2DosageResponse_H_HUM.PharmacyServiceV2DosageDrugs_H_HUM oDrugInfo = new PharmacyServiceV2DosageResponse_H_HUM.PharmacyServiceV2DosageDrugs_H_HUM();
            system.assert(oDrugDosage.DrugDosageResponse == null);
          Test.StopTest();
        }
    }  
}