/****************************************************************************************
Apex Class Name  : ClinicalAuthorizationDetailService_T_HUM
Version          : 1.0 
Created Date     : January 04 2016
Function         : Test class for ClinicalAuthDetails_S_HUM  Apex class
Modification Log :
 *      Developer               Code Review            Date                 Description
 * ----------------------------------------------------------------------------------------           
 *   Prasanthi Kandula            29039               01/04/2016              Original Version  
 *   Ranjeeth Nagishetty          29648               01/28/2016              updated test class
 *   Ranjeeth Nagishetty          22847               03/04/2016              Fixed test class failure while checkin
 *   Santhi Mandava               50212               12/06/2016              Included changes related to total days field and 
                                                                               fixed code coverage issue as well.
 ******************************************************************************************/
@isTest
private class ClinicalAuthorizationDetailService_T_HUM
{
     /*
      * Method Name : setup
      * Description : Create common test Data
      * Return type : void
      * Parameter   : None
      */ 
       @testSetup static void setup() 
      {
           User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
           insert oUser;
           system.assertEquals(oUser.Id != Null,true);
           system.assertEquals(oUser.Lastname == 'test User1',true);
           System.runAs(oUser)
           {
              Account oAccount = HUMTestDataHelperTest.getAccount();
              oAccount.Name = 'Test Account Cases1';
              oAccount.Birthdate__c = '01/15/1951';
              oAccount.Enterprise_ID__c = '1005447107';
              insert oAccount;
              system.assertEquals(oAccount.Id != Null,true);
              system.assertEquals(oAccount.Name == 'Test Account Cases1',true);
            
              Policy__c oPolicy = HUMTestDataHelperTest.getPolicy();
              oPolicy.Group_Name__c = oAccount.Id;
              oPolicy.Name = 'Test Policy';
              insert oPolicy;
              system.assertEquals(oPolicy.Id != Null,true);
              system.assertEquals(oPolicy.Name,'Test Policy');
              system.assertEquals(oPolicy.Group_Name__c == oAccount.Id,true);
            
              Policy_Member__c oPolMember = HUMTestDataHelperTest.getPolicyMember();
              oPolMember.Name = 'Policy Member PM1';
              oPolMember.Policy__c = oPolicy.Id;
              oPolMember.Member__c = oAccount.Id;
              insert oPolMember;
              system.assertEquals(oPolMember.Id != Null,true);
              system.assertEquals(oPolMember.Name,'Policy Member PM1');
            
              HUM_Webservice_Callout__c oHUMAuthDetailsService = new HUM_Webservice_Callout__c();
              oHUMAuthDetailsService.name = 'Clinical_AuthDetail';
              oHUMAuthDetailsService.Active__c = true;
              oHUMAuthDetailsService.Certificate_Name__c = 'test';
              oHUMAuthDetailsService.End_Point_URL__c = 'https://testst-crmservicews.humana.com';
              oHUMAuthDetailsService.Timeout__c = '120000';
              oHUMAuthDetailsService.Content_Type__c = 'application/json';
              oHUMAuthDetailsService.Request_Type__c = 'POST';
              insert oHUMAuthDetailsService;
              system.assert(oHUMAuthDetailsService.id != null);
              system.assert(oHUMAuthDetailsService.name == 'Clinical_AuthDetail');
            }
         }
         
    /*
     * Method Name : authDetails
     * Description : This method tests httprequest and Authorization details
     * Return type : void
     * Parameter   : None
     */ 
    static testMethod void authDetails() 
    {
        Test.setCurrentPage(Page.ClinicalAuthDetails_VF_HUM);
        Policy_Member__c oPolMember = [Select id from Policy_Member__c where Name = 'Policy Member PM1' limit 1];
        HUM_Webservice_Callout__c oServiceCallout = [select Name, Certificate_Name__c, End_Point_URL__c, Timeout__c, Content_Type__c, Request_Type__c from HUM_Webservice_Callout__c where name='Clinical_AuthDetail' limit 1];   
        string sRecId = oPolMember.id;
        string sAuthId = '093032249';
        HttpRequest oRequest = new HttpRequest();
        string sRequestBody = '"PersonID": "1005447107","PersonIDType": "SdrPersonID","DateOfBirth": "1951-01-15T00:00:00","In';
        User oUser = [SELECT Id FROM User  WHERE Lastname ='test User1' LIMIT 1];
        system.runAs(oUser)
        {
            Test.startTest();
            ClinicalAuthDetails_S_HUM oAuthDetailsService = new ClinicalAuthDetails_S_HUM();
            ClinicalAuthDetailsWrapper_DTO_HUM oClinicalWrapper = new ClinicalAuthDetailsWrapper_DTO_HUM();
            oAuthDetailsService.assignValuesToHelperV2(sAuthId,'1005447107','01/15/1951');
            ClinicalAuthDetailsResponse_H_HUM.Authorization oAuthorizations = new ClinicalAuthDetailsResponse_H_HUM.Authorization();
            List<ClinicalAuthDetailsResponse_H_HUM.Authorization> lstAuthorization = new List<ClinicalAuthDetailsResponse_H_HUM.Authorization>();
            oAuthorizations.AuthID = '093032249';   
            oAuthorizations.AuthType = 'Inpatient'; 
            oAuthorizations.AuthStatus = 'Pended'; 
            oAuthorizations.CreatedDate = '2015-08-18T07:48:17.653'; 
            oAuthorizations.RequestType = 'Test';  
            oAuthorizations.ContactMethod = 'Web-Availity'; 
            lstAuthorization.add(oAuthorizations); 
            oClinicalWrapper = oAuthDetailsService.retrieveAuthDetail(lstAuthorization);
            system.assertEquals(oClinicalWrapper.sAuthType,'Inpatient');
            system.assertEquals(oClinicalWrapper.sAuthStatus,'Pended');
            Test.stopTest(); 
        }
     }
    
    /*
     * Method Name : ReferralInformationIP
     * Description : This method tests the Referral Information for Inpatient
     * Return type : void
     * Parameter   : None
     */  
    static testMethod void ReferralInformationIP() 
    {
        Test.setCurrentPage(Page.ClinicalAuthDetails_VF_HUM);
        User oUser = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
        system.runAs(oUser)
        {
           Test.startTest();
           ClinicalAuthDetails_S_HUM oAuthDetailsService = new ClinicalAuthDetails_S_HUM();
           ClinicalAuthDetailsResponse_H_HUM.Authorization oAuthorizations = new ClinicalAuthDetailsResponse_H_HUM.Authorization();
           oauthorizations.InpatientDetails = new ClinicalAuthDetailsResponse_H_HUM.InpatientDetails(); 
           List<ClinicalAuthDetailsResponse_H_HUM.Authorization> lstAuthorization = new List<ClinicalAuthDetailsResponse_H_HUM.Authorization>();
            oAuthorizations.InpatientDetails.AdmissionType = 'Inpt Rehab';   
            oAuthorizations.InpatientDetails.LastCoveredDate = '2015-08-18T07:48:17.653'; 
            oAuthorizations.InpatientDetails.DischargeDate = '2015-08-18T07:48:17.653'; 
            oAuthorizations.InpatientDetails.AdmissionDate = '2015-08-18T07:48:17.653'; 
            oAuthorizations.InpatientDetails.TotalDaysApproved = '0'; 
            lstAuthorization.add(oAuthorizations); 
            ClinicalAuthDetailsWrapper_DTO_HUM oClinicalWrapper = oAuthDetailsService.retrieveReferralInformationIP(lstAuthorization);
            system.assertEquals(oClinicalWrapper.sAdmissionType,'Inpt Rehab');
            system.assertEquals(oClinicalWrapper.sLastCoveredDate,'08/18/2015');
            Test.stopTest(); 
        }
     }
    
    /*
     * Method Name : ReferralInformationOP
     * Description : This method tests the Referral Information for Outpatient
     * Return type : void
     * Parameter   : None
     */  
    static testMethod void ReferralInformationOP() 
    {
        Test.setCurrentPage(Page.ClinicalAuthDetails_VF_HUM);
        User oUser = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
                Provider_functionality_switch__c oProvSwitch = new Provider_functionality_switch__c();
        oProvSwitch.Activate_New_Authorization_Service__c = true;
        insert oProvSwitch;
        system.runAs(oUser)
        {
           Test.startTest();
           ClinicalAuthDetails_S_HUM oAuthDetailsService = new ClinicalAuthDetails_S_HUM();
           ClinicalAuthDetailsResponse_H_HUM.Authorization oAuthorizations = new ClinicalAuthDetailsResponse_H_HUM.Authorization();
           oAuthorizations.OutpatientDetails = new ClinicalAuthDetailsResponse_H_HUM.OutpatientDetails(); 
           List<ClinicalAuthDetailsResponse_H_HUM.Authorization> lstAuthorization = new List<ClinicalAuthDetailsResponse_H_HUM.Authorization>();
            oAuthorizations.OutpatientDetails.FirstDay ='2015-08-18T07:48:17.653';   
            oAuthorizations.OutpatientDetails.LastDay ='2015-08-18T07:48:17.653'; 
            lstAuthorization.add(oAuthorizations); 
            ClinicalAuthDetailsWrapper_DTO_HUM oClinicalWrapper = oAuthDetailsService.retrieveReferralInformationOP(lstAuthorization);
            system.assertEquals(oClinicalWrapper.sFirstDay ,'08/18/2015');
            system.assertEquals(oClinicalWrapper.sLastDay ,'08/18/2015');
            Test.stopTest(); 
        }
     }

         /*
     * Method Name : ReferralInformationOPNewService
     * Description : This method tests the Referral Information for Outpatient
     * Return type : void
     * Parameter   : None
     */  
    static testMethod void ReferralInformationOPNewService() 
    {
        Test.setCurrentPage(Page.ClinicalAuthDetails_VF_HUM);
        Provider_functionality_switch__c oProvSwitch = new Provider_functionality_switch__c();
        oProvSwitch.Activate_New_Authorization_Service__c = true;
        insert oProvSwitch;
        User oUser = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];

        system.runAs(oUser)
        {
           Test.startTest();
           ClinicalAuthDetails_S_HUM oAuthDetailsService = new ClinicalAuthDetails_S_HUM();
           ClinicalAuthDetailsResponse_H_HUM.Authorization oAuthorizations = new ClinicalAuthDetailsResponse_H_HUM.Authorization();
           oAuthorizations.OutpatientDetails = new ClinicalAuthDetailsResponse_H_HUM.OutpatientDetails(); 
           List<ClinicalAuthDetailsResponse_H_HUM.Authorization> lstAuthorization = new List<ClinicalAuthDetailsResponse_H_HUM.Authorization>();
            oAuthorizations.OutpatientDetails.FirstDay ='2015-08-18T07:48:17.653';   
            oAuthorizations.OutpatientDetails.LastDay ='2015-08-18T07:48:17.653'; 
            lstAuthorization.add(oAuthorizations); 
            ClinicalAuthDetailsWrapper_DTO_HUM oClinicalWrapper = oAuthDetailsService.retrieveReferralInformationOP(lstAuthorization);
            system.assertEquals(oClinicalWrapper.sFirstDay ,'08/18/2015');
            system.assertEquals(oClinicalWrapper.sLastDay ,'08/18/2015');
            Test.stopTest(); 
        }
     }
    
    /*
     * Method Name : DiagnosisCodes
     * Description : This method tests the Diagnosis Codes 
     * Return type : void
     * Parameter   : None
     */  
    static testMethod void DiagnosisCodes() 
    {
        Test.setCurrentPage(Page.ClinicalAuthDetails_VF_HUM);
        User oUser = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
        system.runAs(oUser)
        {
           Test.startTest();
           ClinicalAuthDetails_S_HUM oAuthDetailsService = new ClinicalAuthDetails_S_HUM();
           ClinicalAuthDetailsResponse_H_HUM.Authorization oAuthorizations = new ClinicalAuthDetailsResponse_H_HUM.Authorization();
           List<ClinicalAuthDetailsResponse_H_HUM.Authorization> lstAuthorization = new List<ClinicalAuthDetailsResponse_H_HUM.Authorization>();
           ClinicalAuthDetailsResponse_H_HUM.DiagnosisDetail oDiagnosisDetail = new ClinicalAuthDetailsResponse_H_HUM.DiagnosisDetail();
           ClinicalAuthDetailsResponse_H_HUM.DiagnosisDetails oDiagnosisDetails = new ClinicalAuthDetailsResponse_H_HUM.DiagnosisDetails();
           oDiagnosisDetail.Type ='PrimaryDiagnosis';   
           oDiagnosisDetail.Code = '250.0'; 
           oDiagnosisDetail.Description = 'DIAB W/O COMP TYPE II/UNS NOT STATED UNCNTRL'; 
           oDiagnosisDetail.Status = 'Approved'; 
           oDiagnosisDetails.DiagnosisDetail = new List<ClinicalAuthDetailsResponse_H_HUM.DiagnosisDetail>();
           oDiagnosisDetails.DiagnosisDetail.add(oDiagnosisDetail);
           oAuthorizations.DiagnosisDetails = oDiagnosisDetails;
           lstAuthorization.add(oAuthorizations); 
           List<ClinicalAuthDetailsWrapper_DTO_HUM> lstClinicalWrapper = oAuthDetailsService.retrieveDiagnosisCodes(lstAuthorization);
           system.assertEquals(lstClinicalWrapper[0].sDiagnosisType,'PrimaryDiagnosis');
           system.assertEquals(lstClinicalWrapper[0].sDiagnosisStatus ,'Approved');  
           Test.stopTest(); 
        }
     }
    
    /*
     * Method Name : ProcedureCodesIP
     * Description : This method tests the Procedure Codes for InPatient
     * Return type : void
     * Parameter   : None
     */  
    static testMethod void ProcedureCodesIP() 
    {
        Test.setCurrentPage(Page.ClinicalAuthDetails_VF_HUM);
        User oUser = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
        system.runAs(oUser)
        {
           Test.startTest();
           ClinicalAuthDetails_S_HUM oAuthDetailsService = new ClinicalAuthDetails_S_HUM();
           ClinicalAuthDetailsResponse_H_HUM.Authorization oAuthorizations = new ClinicalAuthDetailsResponse_H_HUM.Authorization();
           List<ClinicalAuthDetailsResponse_H_HUM.Authorization> lstAuthorization = new List<ClinicalAuthDetailsResponse_H_HUM.Authorization>();
           ClinicalAuthDetailsResponse_H_HUM.ProcedureDetail oProcedureDetail = new ClinicalAuthDetailsResponse_H_HUM.ProcedureDetail();
           ClinicalAuthDetailsResponse_H_HUM.ProcedureDetails oProcedureDetails = new ClinicalAuthDetailsResponse_H_HUM.ProcedureDetails();
           oProcedureDetail.Code = '250.0'; 
           oProcedureDetail.Description = 'DIAB W/O COMP TYPE II/UNS NOT STATED UNCNTRL'; 
           oProcedureDetail.Status = 'Approved'; 
           oProcedureDetails.ProcedureDetail= new List<ClinicalAuthDetailsResponse_H_HUM.ProcedureDetail>();
           oProcedureDetails.ProcedureDetail.add(oProcedureDetail);
           oAuthorizations.ProcedureDetails = oProcedureDetails;
           lstAuthorization.add(oAuthorizations); 
           List<ClinicalAuthDetailsWrapper_DTO_HUM> lstClinicalWrapper = oAuthDetailsService.retrieveProcedureCodes(lstAuthorization);
           system.assertEquals(lstClinicalWrapper[0].sProcedureStatus,'Approved'); 
           Test.stopTest(); 
        }
     }
    
    /*
     * Method Name : ProviderDetails
     * Description : This method tests the Provider Details and assigns the values to the helper class
     * Return type : void
     * Parameter   : None
     */  
    static testMethod void ProviderDetails() 
    {
        User u = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
        system.runAs(u)
        {
           Test.startTest();
           ClinicalAuthDetails_S_HUM oAuthDetailsService = new ClinicalAuthDetails_S_HUM();
           ClinicalAuthDetailsResponse_H_HUM.Authorization oAuthorizations = new ClinicalAuthDetailsResponse_H_HUM.Authorization();
           List<ClinicalAuthDetailsResponse_H_HUM.Authorization> lstAuthorization = new List<ClinicalAuthDetailsResponse_H_HUM.Authorization>();
           ClinicalAuthDetailsResponse_H_HUM.ProviderDetail oProviderDetail = new ClinicalAuthDetailsResponse_H_HUM.ProviderDetail();
           ClinicalAuthDetailsResponse_H_HUM.Address oAddress = new ClinicalAuthDetailsResponse_H_HUM.Address();
           ClinicalAuthDetailsResponse_H_HUM.AddressDetails oAddressDetail = new ClinicalAuthDetailsResponse_H_HUM.AddressDetails();
           oAddress.Street_1 = '3605 Northgate Ct Ste';
           oAddress.Street_2 = '101';
           oAddress.City = 'New Albany';
           oAddress.State = 'IN';
           oAddress.ZipCode = '10025';
           oAddress.ZipCode_ext = '5210';
           oAddress.PhoneNumber = '9874512569';
           oAddress.AddressType = 'Select Service';
           oAddressDetail.Address= new List<ClinicalAuthDetailsResponse_H_HUM.Address>();
           oAddressDetail.Address.add(oAddress);
           ClinicalAuthDetailsResponse_H_HUM.ProviderDetails oProviderDetails = new ClinicalAuthDetailsResponse_H_HUM.ProviderDetails();
           oProviderDetail.Type = 'RequestingProvider'; 
           oProviderDetail.ID = '101290403525'; 
           oProviderDetail.Name ='James';
           oProviderDetail.ParticipatingStatus = 'Par';
           oProviderDetail.TaxId = '746001118';
           oProviderDetail.AddressDetails = oAddressDetail;
           oProviderDetails.ProviderDetail = new List<ClinicalAuthDetailsResponse_H_HUM.ProviderDetail>();
           oProviderDetails.ProviderDetail.add(oProviderDetail);
           
           oProviderDetail = new ClinicalAuthDetailsResponse_H_HUM.ProviderDetail();
           oAddress = new ClinicalAuthDetailsResponse_H_HUM.Address();          
           oAddress.Street_1 = '3605 Northgate Ct Ste';
           oAddress.Street_2 = '101';
           oAddress.City = 'New Albany';
           oAddress.State = 'IN';
           oAddress.ZipCode = '10025';
           oAddress.ZipCode_ext = '5210';
           oAddress.PhoneNumber = '9874512569';
           oAddress.AddressType = 'Select Service';
           oAddressDetail.Address= new List<ClinicalAuthDetailsResponse_H_HUM.Address>();
           oAddressDetail.Address.add(oAddress);
           oProviderDetail.Type = 'TreatingProvider'; 
           oProviderDetail.ID = '101290403525'; 
           oProviderDetail.Name ='James';
           oProviderDetail.ParticipatingStatus = 'Par';
           oProviderDetail.TaxId = '746001118';
           oProviderDetail.AddressDetails = oAddressDetail;
           oProviderDetails.ProviderDetail.add(oProviderDetail);
           
           oProviderDetail = new ClinicalAuthDetailsResponse_H_HUM.ProviderDetail();
           oAddress = new ClinicalAuthDetailsResponse_H_HUM.Address();          
           oAddress.Street_1 = '3605 Northgate Ct Ste';
           oAddress.Street_2 = '101';
           oAddress.City = 'New Albany';
           oAddress.State = 'IN';
           oAddress.ZipCode = '10025';
           oAddress.ZipCode_ext = '5210';
           oAddress.PhoneNumber = '9874512569';
           oAddress.AddressType = 'Select Service';
           oAddressDetail.Address= new List<ClinicalAuthDetailsResponse_H_HUM.Address>();
           oAddressDetail.Address.add(oAddress);
           oProviderDetail.Type = 'Facility'; 
           oProviderDetail.ID = '101290403525'; 
           oProviderDetail.Name ='James';
           oProviderDetail.ParticipatingStatus = 'Par';
           oProviderDetail.TaxId = '746001118';
           oProviderDetail.AddressDetails = oAddressDetail;
           oProviderDetails.ProviderDetail.add(oProviderDetail);
           oAuthorizations.ProviderDetails = oProviderDetails;
           lstAuthorization.add(oAuthorizations);
           List<ClinicalAuthDetailsWrapper_DTO_HUM> lstProvdetails = oAuthDetailsService.retrieveProviderDetails(lstAuthorization);
           system.assertEquals(3,lstProvdetails.size());
           Test.stopTest(); 
        }
     }
     
    /*
     * Method Name : ProcedureCodesOP
     * Description : This method tests the Procedure Codes for Outpatient
     * Return type : void
     * Parameter   : None
     */  
     static testMethod void ProcedureCodesOP() 
      {
        User oUser = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
        system.runAs(oUser)
        {
           Test.startTest();
           ClinicalAuthDetails_S_HUM oAuthDetailsService = new ClinicalAuthDetails_S_HUM();
           ClinicalAuthDetailsResponse_H_HUM.Authorization oAuthorizations = new ClinicalAuthDetailsResponse_H_HUM.Authorization();
           List<ClinicalAuthDetailsResponse_H_HUM.Authorization> lstAuthorization = new List<ClinicalAuthDetailsResponse_H_HUM.Authorization>();
           ClinicalAuthDetailsResponse_H_HUM.ProcedureDetail oProcedureDetail = new ClinicalAuthDetailsResponse_H_HUM.ProcedureDetail();
           ClinicalAuthDetailsResponse_H_HUM.Visit oVisit = new ClinicalAuthDetailsResponse_H_HUM.Visit();
           oVisit.RequestedUnits = '1';
           oVisit.ApprovedUnits = '1';
           oVisit.TypeOfUnits = 'Number of Visits';
           ClinicalAuthDetailsResponse_H_HUM.ProcedureDetails oProcedureDetails= new ClinicalAuthDetailsResponse_H_HUM.ProcedureDetails();
           oProcedureDetail.Code = '1101'; 
           oProcedureDetail.Description = 'Debridement of extensive eczematous or infected skin; up to 10% of body surface'; 
           oProcedureDetail.Status ='Approved';
           oProcedureDetail.Visit = new ClinicalAuthDetailsResponse_H_HUM.Visit();
           oProcedureDetail.Visit = oVisit;
           oProcedureDetails.ProcedureDetail = new List<ClinicalAuthDetailsResponse_H_HUM.ProcedureDetail>();
           oProcedureDetails.ProcedureDetail.add(oProcedureDetail);
           oAuthorizations.ProcedureDetails = oProcedureDetails;
           lstAuthorization.add(oAuthorizations);
           List<ClinicalAuthDetailsWrapper_DTO_HUM> lstClinicalWrapper = oAuthDetailsService.retrieveProcedureCodesOutPatient(lstAuthorization);
           system.assertEquals(lstClinicalWrapper[0].sProcedureRequestingUnits , '1'); 
           system.assertEquals(lstClinicalWrapper[0].sProcedureCode , '1101');
           Test.stopTest(); 
        }
     }
     
     /*
     * Method Name : testOutPatientTotalDays
     * Description : This method tests the total days field change for out patient.
     * Return type : void
     * Parameter   : None
     */ 
     static testMethod void testOutPatientTotalDays() 
      {
         Test.setCurrentPage(Page.ClinicalAuthDetails_VF_HUM);
        Policy_Member__c oPolMember = [Select id from Policy_Member__c where Name = 'Policy Member PM1' limit 1];
        HUM_Webservice_Callout__c oServiceCallout = [select Name, Certificate_Name__c, End_Point_URL__c, Timeout__c, Content_Type__c, Request_Type__c from HUM_Webservice_Callout__c where name='Clinical_AuthDetail' limit 1];   
        string sRecId = oPolMember.id;
        string sAuthId = '093032249';
        HttpRequest oRequest = new HttpRequest();
        string sRequestBody = '"PersonID": "1005447107","PersonIDType": "SdrPersonID","DateOfBirth": "1951-01-15T00:00:00","In';
        User oUser = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
        system.runAs(oUser)
        {
           Test.startTest();
           ClinicalAuthDetails_S_HUM oAuthDetailsService = new ClinicalAuthDetails_S_HUM();
            ClinicalAuthDetailsWrapper_DTO_HUM oClinicalWrapper = new ClinicalAuthDetailsWrapper_DTO_HUM();
            oAuthDetailsService.assignValuesToHelperV2(sAuthId,'1005447107','01/15/1951');
            ClinicalAuthDetailsResponse_H_HUM.Authorization oAuthorizations = new ClinicalAuthDetailsResponse_H_HUM.Authorization();
            List<ClinicalAuthDetailsResponse_H_HUM.Authorization> lstAuthorization = new List<ClinicalAuthDetailsResponse_H_HUM.Authorization>();
            oAuthorizations.AuthID = '093032249';   
            oAuthorizations.AuthType = 'Outpatient'; 
            oAuthorizations.AuthStatus = 'Pended'; 
            oAuthorizations.CreatedDate = '2015-08-18T07:48:17.653'; 
            oAuthorizations.RequestType = 'Test';  
            oAuthorizations.ContactMethod = 'Web-Availity';
            ClinicalAuthDetailsResponse_H_HUM.OutpatientDetails oOutPatient = new ClinicalAuthDetailsResponse_H_HUM.OutpatientDetails();
            oOutPatient.ServiceType ='Test Service';
            oOutPatient.TotalDaysApproved ='3';  
            oAuthorizations.OutpatientDetails = oOutPatient;
            lstAuthorization.add(oAuthorizations); 
            oClinicalWrapper = oAuthDetailsService.retrieveReferralInformationOP(lstAuthorization);

            system.assertEquals(oClinicalWrapper.iTotalDays,3);
            Test.stopTest(); 
        }
    }
 }