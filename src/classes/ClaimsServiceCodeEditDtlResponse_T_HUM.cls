/**************************************************************************** 
Apex Class Name  : ClaimsServiceCodeEditDtlResponse_T_HUM
Version          : 1.0  
Created Date     : February 19,2016
Function         : test class for ClaimsServiceCodeEditDtlResponse_H_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Gargey Sharma			     30059            02/19/2016                 Original Version
***************************************************************************************************/
@isTest
private Class ClaimsServiceCodeEditDtlResponse_T_HUM
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
     * Method Name : testClaimsServiceCodeEditDtlResponse
     * Description : Test method for ClaimsServiceCodeEditDtlResponse_T_HUM
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testClaimsServiceCodeEditDtlResponse()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser)
        {
          Test.StartTest();
            ClaimsServiceCodeEditDtlResponse_H_HUM oResponse = new ClaimsServiceCodeEditDtlResponse_H_HUM();
            ClaimsServiceCodeEditDtlResponse_H_HUM.GetCodeEditDetailsResponse getcdeditresp = new ClaimsServiceCodeEditDtlResponse_H_HUM.GetCodeEditDetailsResponse();
            ClaimsServiceCodeEditDtlResponse_H_HUM.RationaleDTO raDTO = new ClaimsServiceCodeEditDtlResponse_H_HUM.RationaleDTO();
            ClaimsServiceCodeEditDtlResponse_H_HUM.ExCodes ecodes = new ClaimsServiceCodeEditDtlResponse_H_HUM.ExCodes();
            ClaimsServiceCodeEditDtlResponse_H_HUM.RejectedProcedureCodes rpcodes = new ClaimsServiceCodeEditDtlResponse_H_HUM.RejectedProcedureCodes();
            ClaimsServiceCodeEditDtlResponse_H_HUM.PaidProcedurecodes ppcodes = new ClaimsServiceCodeEditDtlResponse_H_HUM.PaidProcedurecodes();
            ClaimsServiceCodeEditDtlResponse_H_HUM.PaidModifers pmcodes = new ClaimsServiceCodeEditDtlResponse_H_HUM.PaidModifers();
            ClaimsServiceCodeEditDtlResponse_H_HUM.DiagnosisCodes dcodes = new ClaimsServiceCodeEditDtlResponse_H_HUM.DiagnosisCodes();
            ClaimsServiceCodeEditDtlResponse_H_HUM.RationaleDescriptionCodes rdcodes = new ClaimsServiceCodeEditDtlResponse_H_HUM.RationaleDescriptionCodes();
            ClaimsServiceCodeEditDtlResponse_H_HUM.RationaleExceptionCodes recodes = new ClaimsServiceCodeEditDtlResponse_H_HUM.RationaleExceptionCodes();
            ClaimsServiceCodeEditDtlResponse_H_HUM.RejectedProcedureDTO rejproDTO = new ClaimsServiceCodeEditDtlResponse_H_HUM.RejectedProcedureDTO();
            ClaimsServiceCodeEditDtlResponse_H_HUM.DiagnosisCode diagcode = new ClaimsServiceCodeEditDtlResponse_H_HUM.DiagnosisCode();
            ClaimsServiceCodeEditDtlResponse_H_HUM.RationaleDescriptionDTO radescDTO = new ClaimsServiceCodeEditDtlResponse_H_HUM.RationaleDescriptionDTO();
            ClaimsServiceCodeEditDtlResponse_H_HUM.RationaleExceptionDTO raexcepDTO = new ClaimsServiceCodeEditDtlResponse_H_HUM.RationaleExceptionDTO();
            system.assert(oResponse != null);
          Test.StopTest();
        }
    }  
}