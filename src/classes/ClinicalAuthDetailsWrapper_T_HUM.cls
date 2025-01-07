/**************************************************************************** 
Apex Class Name  : ClinicalAuthDetailsWrapper_T_HUM
Version          : 1.0  
Created Date     : December 22, 2015
Function         : Test class for ClinicalAuthDetailsWrapper_DTO_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Prasanthi Kandula        29039              12/22/2015                  Original Version
* Ranjeeth N               29648              02/01/2016                  changed the class  name in log
***************************************************************************************************/
@isTest
private Class ClinicalAuthDetailsWrapper_T_HUM
{
   /*
    * Method Name : testSetup 
    * Description : Test method to setup common test data for all test methods     
    * Return type : void     
    * Parameter   : NA  
    */
    @testSetup 
    static void testSetup()
    {        
        HUMTestDataHelperTest.getHUMConstantsData();
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
     }
    
    /*
     * Method Name : testClinicalAuthDetailsWrapper
     * Description : Test method for  ClinicalAuthDetailsWrapper_H_HUM
     * Return type : NA
     * Parameter   : NA
     */
    static testMethod void testClinicalAuthDetailsWrapper()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.assertEquals(oUser.Id != Null,true);
        system.runAs(oUser)
        {
          Test.StartTest();
            ClinicalAuthDetailsWrapper_DTO_HUM oRequest = new  ClinicalAuthDetailsWrapper_DTO_HUM();
            oRequest.sAuthID = '093032249';
            System.assertEquals('093032249',oRequest.sAuthID);
            oRequest.sAuthType = 'Inpatient';
            System.assertEquals('Inpatient',oRequest.sAuthType);
            oRequest.sAuthStatus = 'Pended';
            System.assertEquals('Pended',oRequest.sAuthStatus );
            oRequest.sUpdatedDate = '2015-08-18T07:48:17.653';
            System.assertEquals('2015-08-18T07:48:17.653',oRequest.sUpdatedDate );
            oRequest.sCreatedDate = '2015-08-18T07:48:17.653';
            System.assertEquals('2015-08-18T07:48:17.653',oRequest.sCreatedDate );
            oRequest.sDateOfService = '2015-08-18T07:48:17.653'; 
            System.assertEquals('2015-08-18T07:48:17.653',oRequest.sDateOfService );
            oRequest.sContactMethod = 'Web-Availity'; 
            System.assertEquals('Web-Availity',oRequest.sContactMethod );
            oRequest.sIsReferral = 'True'; 
            System.assertEquals('True',oRequest.sIsReferral );
            oRequest.sStatusReason = 'InPatient';
            System.assertEquals('InPatient',oRequest.sStatusReason );
            oRequest.sCustomerGenkey = '968514523';
            System.assertEquals('968514523',oRequest.sCustomerGenkey );
            oRequest.sGroupId = '123589654';
            System.assertEquals('123589654',oRequest.sGroupId );
            oRequest.sPlatformCode = 'EM'; 
            System.assertEquals('EM',oRequest.sPlatformCode );
            oRequest.sCoverageSequenceNumber = '968514523';
            System.assertEquals('968514523',oRequest.sCoverageSequenceNumber );
            oRequest.sEffectiveDate = '2015-09-01T00:00:00';
            System.assertEquals('2015-09-01T00:00:00',oRequest.sEffectiveDate );
            oRequest.sRequestType = 'Pre-Auth';
            System.assertEquals('Pre-Auth',oRequest.sRequestType );
            oRequest.sAdmissionDate = '2015-09-01T00:00:00';
            System.assertEquals('2015-09-01T00:00:00',oRequest.sAdmissionDate );
            oRequest.sDischargeDate = '2015-09-01T00:00:00';
            System.assertEquals('2015-09-01T00:00:00',oRequest.sDischargeDate );
            oRequest.sBedType = 'Inpatient';
            System.assertEquals('Inpatient',oRequest.sBedType );
            oRequest.sAdmissionType = 'Inpt Rehab';
            System.assertEquals('Inpt Rehab',oRequest.sAdmissionType );
            oRequest.sTotalDaysApproved = '0';
            System.assertEquals('0',oRequest.sTotalDaysApproved );
            oRequest.sLastCoveredDate = '0001-01-01T00:00:00';
            System.assertEquals('0001-01-01T00:00:00',oRequest.sLastCoveredDate );
            oRequest.sServiceType = 'PrimaryDiagnosis';
            System.assertEquals('PrimaryDiagnosis',oRequest.sServiceType );
            oRequest.sOPRequestType = 'Test';
            System.assertEquals('Test',oRequest.sOPRequestType );
            oRequest.sFirstDay = '2015-09-01T00:00:00';
            System.assertEquals('2015-09-01T00:00:00',oRequest.sFirstDay );
            oRequest.sLastDay = '2015-09-20T00:00:00';
            System.assertEquals('2015-09-20T00:00:00',oRequest.sLastDay );
            oRequest.iTotalDays = 20;
            System.assertEquals(20,oRequest.iTotalDays );
            oRequest.sDiagnosisType = 'PrimaryDiagnosis';
            System.assertEquals('PrimaryDiagnosis',oRequest.sDiagnosisType );
            oRequest.sDiagnosisCode = '250.00';
            System.assertEquals('250.00',oRequest.sDiagnosisCode );
            oRequest.sDiagnosisDescription = 'Diabetes mellitus without mention of complication';
            System.assertEquals('Diabetes mellitus without mention of complication',oRequest.sDiagnosisDescription );
            oRequest.sDiagnosisStatus = 'Pended';
            System.assertEquals('Pended',oRequest.sDiagnosisStatus );
            oRequest.sProcedureCode = '253.0';
            System.assertEquals('253.0',oRequest.sProcedureCode );
            oRequest.sProcedureDescription = 'Test Procedure';
            System.assertEquals('Test Procedure',oRequest.sProcedureDescription );
            oRequest.sProcedureServiceCode = 'Approved';
            System.assertEquals('Approved',oRequest.sProcedureServiceCode );
            oRequest.sProcedureStatus = 'Approved';
            System.assertEquals('Approved',oRequest.sProcedureStatus );
            oRequest.sProcedureRequestingUnits = '3';
            System.assertEquals('3',oRequest.sProcedureRequestingUnits );
            oRequest.sProcedureAuthorizedUnits = '3';
            System.assertEquals('3',oRequest.sProcedureAuthorizedUnits );
            oRequest.sProcedureTypeofUnits = 'Number Of Visits';
            System.assertEquals('Number Of Visits',oRequest.sProcedureTypeofUnits );
            oRequest.sReqProviderCity = 'LOUISVILLE';
            System.assertEquals('LOUISVILLE',oRequest.sReqProviderCity );
            oRequest.sReqProviderState = 'KY';
            System.assertEquals('KY',oRequest.sReqProviderState );
            oRequest.sReqProviderPhnNum ='5028995559';
            System.assertEquals('5028995559',oRequest.sReqProviderPhnNum );
            oRequest.sReqProviderStreet = '4010 DUPONT CIRCLE STE 276';
            System.assertEquals('4010 DUPONT CIRCLE STE 276',oRequest.sReqProviderStreet );
            oRequest.sReqProviderZipCode = '40207';
            System.assertEquals('40207',oRequest.sReqProviderZipCode );
            oRequest.sReqProviderID = '101290403525';
            System.assertEquals('101290403525',oRequest.sReqProviderID );
            oRequest.sReqProviderName = 'Ames, Frederick';
            System.assertEquals('Ames, Frederick',oRequest.sReqProviderName );
            oRequest.sReqProviderTaxID = '746001118';
            System.assertEquals('746001118',oRequest.sReqProviderTaxID );
            oRequest.sReqProviderPartStatus = 'Par';
            System.assertEquals('Par',oRequest.sReqProviderPartStatus );
            oRequest.sReqProviderType = 'RequestingProvider';
            System.assertEquals('RequestingProvider',oRequest.sReqProviderType );
          Test.StopTest();
        }
    }  
}