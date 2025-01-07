/**************************************************************************** 
Apex Class Name  : ClinicalAuthDetailsRequest_T_HUM_V2
Version          : 1.0  
Created Date     : December 06, 2019
Function         : test class for ClinicalAuthDetailsRequest_H_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Imran                                       12/06/2019                    Original Versio
***************************************************************************************************/
@isTest
private Class ClinicalAuthDetailsRequest_T_HUM_V2
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
     * Method Name : testClinicalAuthDetailsRequest
     * Description : Test method for  ClinicalAuthDetailsRequest_H_HUM
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testClinicalAuthDetailsRequest()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser)
        {
          Test.StartTest();
            ClinicalAuthDetailsRequest_H_HUM_V2 oRequest = new  ClinicalAuthDetailsRequest_H_HUM_V2();
            ClinicalAuthDetailsRequest_H_HUM_V2.AuthDetailRequest oAuthReq = new ClinicalAuthDetailsRequest_H_HUM_V2.AuthDetailRequest();             
            oAuthReq.AuthID= '093013426';                        
            oRequest.AuthDetailRequest = oAuthReq;
            system.assert(oRequest.AuthDetailRequest != null);
          Test.StopTest();
        }
    }  
}