/**************************************************************************** 
Apex Class Name  : ClinicalAuthSummaryResponse_T_HUM 
Version          : 1.0  
Created Date     : December 15, 2015
Function         : test class for ClinicalAuthSummaryResponse_H_HUM  
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* ChiranjeeviRao Ravuri     28658              12/15/2015                    Original Version
***************************************************************************************************/
@isTest
private Class ClinicalAuthSummaryResponse_T_HUM 
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
     * Method Name : testClinicalAuthSummaryResponse
     * Description : Test method for  ClinicalAuthSummaryResponse_H_HUM 
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testClinicalAuthSummaryResponse()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];               
        system.runAs(oUser)
        {
          Test.StartTest();
            ClinicalAuthSummaryResponse_H_HUM oResponse = new ClinicalAuthSummaryResponse_H_HUM();
            ClinicalAuthSummaryResponse_H_HUM.AuthSummaryResponse oAuthResponse = new ClinicalAuthSummaryResponse_H_HUM.AuthSummaryResponse();
            ClinicalAuthSummaryResponse_H_HUM.AuthSummaryList oAuthSummList = new ClinicalAuthSummaryResponse_H_HUM.AuthSummaryList();
            ClinicalAuthSummaryResponse_H_HUM.AuthSummary oAuthSummary = new ClinicalAuthSummaryResponse_H_HUM.AuthSummary();
            List<ClinicalAuthSummaryResponse_H_HUM.AuthSummary> lstAuthSummary = new List<ClinicalAuthSummaryResponse_H_HUM.AuthSummary>();
            ClinicalAuthSummaryResponse_H_HUM.InpatientDetails oInpatDetails = new ClinicalAuthSummaryResponse_H_HUM.InpatientDetails();
            ClinicalAuthSummaryResponse_H_HUM.OutpatientDetails oOutPatDetails = new ClinicalAuthSummaryResponse_H_HUM.OutpatientDetails();
            oAuthSummary.AuthID ='093013426';
            lstAuthSummary.add(oAuthSummary);
            
            oAuthSummList.AuthSummary = lstAuthSummary;
            oAuthResponse.AuthSummaryList = oAuthSummList;
            oResponse.AuthSummaryResponse = oAuthResponse;
             system.assert(oResponse.AuthSummaryResponse.AuthSummaryList.AuthSummary [0].AuthID != null);
          
          Test.StopTest(); 
        }
    }  
}