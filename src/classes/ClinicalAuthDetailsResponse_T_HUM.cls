/**************************************************************************** 
Apex Class Name  : ClinicalAuthDetailsResponse_T_HUM 
Version          : 1.0  
Created Date     : December 15, 2015
Function         : test class for ClinicalAuthDetailsResponse_H_HUM  
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* ChiranjeeviRao Ravuri    28658               12/15/2015                    Original Version
* Ranadheer                                   05/18/2021                  Code coverage
***************************************************************************************************/
@isTest
private Class ClinicalAuthDetailsResponse_T_HUM 
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
     * Method Name : testClinicalAuthDetailsResponse
     * Description : Test method for  ClinicalAuthDetailsResponse_H_HUM
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testClinicalAuthDetailsResponse()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];              
        system.runAs(oUser)
        {
          Test.StartTest();
          
           ClinicalAuthDetailsResponse_H_HUM  oResponse = new ClinicalAuthDetailsResponse_H_HUM();
            ClinicalAuthDetailsResponse_H_HUM.AuthorizationsResponse oAuthResponse = new ClinicalAuthDetailsResponse_H_HUM.AuthorizationsResponse();
            ClinicalAuthDetailsResponse_H_HUM.Authorizations oAuths = new ClinicalAuthDetailsResponse_H_HUM.Authorizations();
            List<ClinicalAuthDetailsResponse_H_HUM.Authorization> lstAuth = new List<ClinicalAuthDetailsResponse_H_HUM.Authorization>();
            ClinicalAuthDetailsResponse_H_HUM.Authorization oAuth = new ClinicalAuthDetailsResponse_H_HUM.Authorization();
            ClinicalAuthDetailsResponse_H_HUM.InpatientDetails oInPatDetails = new ClinicalAuthDetailsResponse_H_HUM.InpatientDetails();
            ClinicalAuthDetailsResponse_H_HUM.OutpatientDetails oOutPatDetails = new ClinicalAuthDetailsResponse_H_HUM.OutpatientDetails();
            ClinicalAuthDetailsResponse_H_HUM.ProcedureDetails oProcDetails = new ClinicalAuthDetailsResponse_H_HUM.ProcedureDetails();
            ClinicalAuthDetailsResponse_H_HUM.ProcedureDetail oProcDet = new ClinicalAuthDetailsResponse_H_HUM.ProcedureDetail();
            ClinicalAuthDetailsResponse_H_HUM.Visit oVisit = new ClinicalAuthDetailsResponse_H_HUM.Visit();
            ClinicalAuthDetailsResponse_H_HUM.DiagnosisDetails oDiaDetails = new ClinicalAuthDetailsResponse_H_HUM.DiagnosisDetails();
            ClinicalAuthDetailsResponse_H_HUM.DiagnosisDetail oDiaDet = new ClinicalAuthDetailsResponse_H_HUM.DiagnosisDetail();
            ClinicalAuthDetailsResponse_H_HUM.ProviderDetails oProvDetails = new ClinicalAuthDetailsResponse_H_HUM.ProviderDetails();
            ClinicalAuthDetailsResponse_H_HUM.ProviderDetail oProvDet = new ClinicalAuthDetailsResponse_H_HUM.ProviderDetail();
            ClinicalAuthDetailsResponse_H_HUM.AddressDetails oAddDetails = new ClinicalAuthDetailsResponse_H_HUM.AddressDetails();
            ClinicalAuthDetailsResponse_H_HUM.Address oAdd = new ClinicalAuthDetailsResponse_H_HUM.Address();
            ClinicalAuthDetailsResponse_H_HUM.MemberInfo oMembInfo = new ClinicalAuthDetailsResponse_H_HUM.MemberInfo();
			
			ClinicalAuthDetailsResponse_H_HUM.CommunicationRecordsResponse commRecordsResp = new ClinicalAuthDetailsResponse_H_HUM.CommunicationRecordsResponse();
			ClinicalAuthDetailsResponse_H_HUM.LettersResponse lettersResponse = new ClinicalAuthDetailsResponse_H_HUM.LettersResponse();
			ClinicalAuthDetailsResponse_H_HUM.CommunicationRecords commRecords = new ClinicalAuthDetailsResponse_H_HUM.CommunicationRecords();
			ClinicalAuthDetailsResponse_H_HUM.LettersRecords letterRecords = new ClinicalAuthDetailsResponse_H_HUM.LettersRecords();
            
            oAuth.AuthID = '093013426';
            lstAuth.add(oAuth);
            oAuths.Authorization = lstAuth;
            oAuthResponse.Authorizations = oAuths;
            oResponse.AuthorizationsResponse = oAuthResponse; 
            system.assert(oResponse.AuthorizationsResponse.Authorizations.Authorization[0].AuthID != null);
            
            
          Test.StopTest(); 
        }
    }  
}