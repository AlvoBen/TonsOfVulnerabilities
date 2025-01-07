/**************************************************************************** 
Apex Class Name  : ClaimsServiceCodeEditDetailRequest_T_HUM
Version          : 1.0  
Created Date     : February 19,2016
Function         : test class for ClaimsServiceCodeEditDetailRequest_H_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Gargey Sharma			     30059             02/19/2016                 Original Version
***************************************************************************************************/
@isTest
private Class ClaimsServiceCodeEditDetailRequest_T_HUM
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
     * Method Name : testClaimsServiceCodeEditDetailRequest
     * Description : Test method for ClaimsServiceCodeEditDetailRequest_H_HUM
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testClaimsServiceCodeEditDetailRequest()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser)
        {
          Test.StartTest();
            ClaimsServiceCodeEditDetailRequest_H_HUM oRequest = new ClaimsServiceCodeEditDetailRequest_H_HUM();
            ClaimsServiceCodeEditDetailRequest_H_HUM.GetCodeEditDetails getcodeeditdet = new ClaimsServiceCodeEditDetailRequest_H_HUM.GetCodeEditDetails();
            ClaimsServiceCodeEditDetailRequest_H_HUM.RationaleDTO raDTO = new ClaimsServiceCodeEditDetailRequest_H_HUM.RationaleDTO(); 
            ClaimsServiceCodeEditDetailRequest_H_HUM.DiagnosisCodes dcodes = new ClaimsServiceCodeEditDetailRequest_H_HUM.DiagnosisCodes();
            ClaimsServiceCodeEditDetailRequest_H_HUM.ExCodes ecodes = new ClaimsServiceCodeEditDetailRequest_H_HUM.ExCodes();
            ClaimsServiceCodeEditDetailRequest_H_HUM.PaidModifers pmod= new ClaimsServiceCodeEditDetailRequest_H_HUM.PaidModifers();
            ClaimsServiceCodeEditDetailRequest_H_HUM.PaidProcedureCodes ppc = new ClaimsServiceCodeEditDetailRequest_H_HUM.PaidProcedureCodes();
            ClaimsServiceCodeEditDetailRequest_H_HUM.RejectedProcedureCodes rpc = new ClaimsServiceCodeEditDetailRequest_H_HUM.RejectedProcedureCodes();
            ClaimsServiceCodeEditDetailRequest_H_HUM.RejectedProcedureDTO rejproDTO = new ClaimsServiceCodeEditDetailRequest_H_HUM.RejectedProcedureDTO();
            ClaimsServiceCodeEditDetailRequest_H_HUM.DiagnosisCode diagcode = new ClaimsServiceCodeEditDetailRequest_H_HUM.DiagnosisCode();
            system.assert(oRequest != null);
          Test.StopTest();
        }
    }  
}