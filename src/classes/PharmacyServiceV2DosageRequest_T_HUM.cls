/**************************************************************************** 
Apex Class Name  : PharmacyServiceV2DosageRequest_T_HUM 
Version          : 1.0  
Created Date     : October 29, 2015
Function         : test class for PharmacyServiceV2DosageRequest_H_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Gargey Sharma		        27449             10/29/2015                    Original Version
***************************************************************************************************/
@isTest
private Class PharmacyServiceV2DosageRequest_T_HUM
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
     * Method Name : testPharmacyServiceV2DosageRequest
     * Description : Test method for PharmacyServiceV2DosageRequest_H_HUM
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testPharmacyServiceV2DosageRequest()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        Test.setMock(HttpCalloutMock.class, new PharmacyServiceV2MockResponse_T_HUM());               
        system.runAs(oUser)
        {
          Test.StartTest();
            PharmacyServiceV2DosageRequest_H_HUM oRequest = new PharmacyServiceV2DosageRequest_H_HUM();
			PharmacyServiceV2DosageRequest_H_HUM.PharmacyV2DrugDosageRequest_H_HUM oDrugDos = new PharmacyServiceV2DosageRequest_H_HUM.PharmacyV2DrugDosageRequest_H_HUM(); 
			oDrugDos.Requestor = 'CDT';	
			PharmacyServiceV2DosageRequest_H_HUM.PharmacyServiceV2DrugId_H_HUM oDrugReq = new PharmacyServiceV2DosageRequest_H_HUM.PharmacyServiceV2DrugId_H_HUM();
			oDrugReq.Key = 'MedId';	
			oDrugReq.Value = '286606';
			oDrugDos.DrugId = oDrugReq;
			oRequest.DrugDosageRequest = oDrugDos;
            system.assert(oRequest.DrugDosageRequest!= null);
          Test.StopTest();
        }
    }  
}