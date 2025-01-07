/*****************************************************************************************
Apex class Name    : AuthSummary_Service_T_HUM
Version            : 1.0
Function           : This is testclass for AuthSummary_S_HUM                 
Created Date       : 5 JAN 2016

Modification Log:
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------  
 *   Sruthi Adivikolanu            29042                     01/05/2016          Original Version
 *   Sruthi Adivikolanu            29646                     02/01/2016          updated the test class to increase code coverage.
 *   Ranjeeth N                                              03/04/2016          Fixed test class while checkin
 *   Santhi Mandava                                          03/26/2016          Fixed code coverage issue. 
 *   Praveen Parimi/Prasanthi      33128                     07/18/2016          Defect fix for D-00471. 
 *   Harika Devi Kalidindi         103812                    06/21/2017          Fixed code coverage issues as a part of REQ - 320375
  *   Imran                                                   12/06/2019          Fixed code coverage issues as a part of REQ - 416071
 ******************************************************************************************************************************/
@isTest
private with sharing class AuthSummary_Service_T_HUM
{
    /*
    * Method Name : setup
    * Description : To create test data
    * Return type : NA
    * Parameter   : NA
    */
    @testSetup 
    private static void setup() 
    {
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u;
        system.assertEquals(u.Id != Null,true);
        system.assertEquals(u.Lastname == 'test User1',true);
            
        System.runAs(u)
        {
            //Creating Account Record
            Account acc1 = HUMTestDataHelperTest.getAccount();
            acc1.Name = 'Test Account Cases1';
            acc1.Birthdate__c='01/15/1951';
            acc1.Enterprise_ID__c = '1005447107';
            insert acc1;
            system.assertEquals(acc1.Id != Null,true);
            system.assertEquals(acc1.Name == 'Test Account Cases1',true);
            
            //Creating Policy Record
            Policy__c pol = HUMTestDataHelperTest.getPolicy();
            pol.Group_Name__c = acc1.Id;
            pol.Name = 'Test Policy';
            insert pol;
            system.assertEquals(pol.Id != Null,true);
            system.assertEquals(pol.Name,'Test Policy');
            system.assertEquals(pol.Group_Name__c == acc1.Id,true);
            
            //Insert Policy Member
            Policy_Member__c polMember = HUMTestDataHelperTest.getPolicyMember();
            polMember.Name='Policy Member PM1';
            polMember.Policy__c=pol.Id;
            polMember.Member__c=acc1.Id;
            insert polMember;
            system.assertEquals(polMember.Id != Null,true);
            system.assertEquals(polMember.Name,'Policy Member PM1');
            system.assertEquals(polMember.Policy__c,pol.Id);
            
            HUM_Webservice_Callout__c oHUMAuthSummaryService = new HUM_Webservice_Callout__c();
            oHUMAuthSummaryService.name='Clinical_AuthSummary';
            oHUMAuthSummaryService.Active__c=true;
            oHUMAuthSummaryService.Certificate_Name__c='test';
            oHUMAuthSummaryService.End_Point_URL__c='https://testst-crmservicews.humana.com';
            oHUMAuthSummaryService.Timeout__c='120000';
            oHUMAuthSummaryService.Content_Type__c='application/json';
            oHUMAuthSummaryService.Request_Type__c='POST';
            insert oHUMAuthSummaryService;
            system.assert(oHUMAuthSummaryService.id!=null);
            system.assert(oHUMAuthSummaryService.name == 'Clinical_AuthSummary');
            
           
        }
  }
    /*
    * Method Name : requestTestMethod
    * Description : To test request and response 
    * Return type : NA
    * Parameter   : NA
    */     
    @isTest 
    private static void requestTestMethod() 
    {  
        Provider_functionality_switch__c oProvSwitch = new Provider_functionality_switch__c();
        oProvSwitch.Activate_New_Authorization_Service__c = true;
        insert oProvSwitch;
        User u = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
      
        system.runAs(u)
        {
            Policy_Member__c polMember = [Select id from Policy_Member__c where Name = 'Policy Member PM1' limit 1];
            Account objact = [select Id,Enterprise_ID__c  from Account where name = 'Test Account Cases1' limit 1];
            HttpRequest request1 = new HttpRequest();
            HUM_Webservice_Callout__c oHUMAuthSummaryService = HUM_Webservice_Callout__c.getAll().values();
            string Response = '{ "AuthSummaryResponse":{ "AuthSummaryList":{ "AuthSummary":['+
                            '{ "AuthID":"093033626", "AuthType":"Inpatient", "AuthStatus":"Pended", "CustomerGenkey":"0600004396117", "FacilityProviderName":"Gonzalez, Jorge", "GroupId":"R0877001", "RequestingProviderName":"Gonzalez, Jorge", "LastModifiedDateTime":"2015-12-04T02:58:34.29", "TreatingProviderName":"Gonzalez, Jorge", "InpatientDetails":{ "AdmissionDate":"2015-12-04T00:00:00", "AdmissionType":"Inpt Rehab", "BedType":"", "DischargeDate":"2015-12-05T00:00:00", "TotalDaysApproved":"0", "LastCoveredDate":"" }, "OutpatientDetails":{ "FirstDay":"0001-01-01T00:00:00", "LastDay":"0001-01-01T00:00:00", "ServiceType":"" } },'+
                            '{ "AuthID":"093033556", "AuthType":"BHInpatient", "AuthStatus":"Approved", "CustomerGenkey":"0600004396117", "FacilityProviderName":"DASCO NORTON HME", "GroupId":"R0877001", "RequestingProviderName":"DASCO NORTON HME", "LastModifiedDateTime":"2015-11-27T05:49:38.813", "TreatingProviderName":"DASCO NORTON HME", "InpatientDetails":{ "AdmissionDate":"2015-11-27T00:00:00", "AdmissionType":"Inpatient Psychiatric", "BedType":"", "DischargeDate":"0001-01-01T00:00:00", "TotalDaysApproved":"0", "LastCoveredDate":"" }, "OutpatientDetails":{ "FirstDay":"0001-01-01T00:00:00", "LastDay":"0001-01-01T00:00:00", "ServiceType":"" } },'+
                            '{ "AuthID":"093013814", "AuthType":"Outpatient", "AuthStatus":"Approved", "CustomerGenkey":"0600004396117", "FacilityProviderName":"TRI COUNTY DOCTORS INC", "GroupId":"R0877003", "RequestingProviderName":"TRI COUNTY DOCTORS INC", "LastModifiedDateTime":"2015-11-09T06:07:28.23", "TreatingProviderName":"TRI COUNTY DOCTORS INC", "InpatientDetails":{ "AdmissionDate":"0001-01-01T00:00:00", "AdmissionType":"", "BedType":"", "DischargeDate":"0001-01-01T00:00:00", "TotalDaysApproved":"0", "LastCoveredDate":"" }, "OutpatientDetails":{ "FirstDay":"2014-03-14T00:00:00", "LastDay":"2014-04-12T00:00:00", "ServiceType":"Observation" } }]'+ 
                            '}, "TotalRecordsFound":"50" } }';
          

           
            Test.startTest();
            Authsummary_s_Hum objAuthS = new Authsummary_s_Hum();
            objAuthS.buildRequestv2(polMember.id);
            List<AuthRefferal_Wrapper_DTO_HUM> Authsummarylist = new  List<AuthRefferal_Wrapper_DTO_HUM>();
            Authsummarylist  = objAuthS.processResponse(Response);
           
            
            string sdate  = '02--08-1990:11:22:000T';
            objAuthS.formateDate(sdate  );
            string sJson  = '{ "AuthorizationsResponse":{ "Authorizations":{ "Authorization":[ { "AuthID":"093020165", "AuthType":"Outpatient", "AuthStatus":"Pended", "UpdatedDate":"2014-12-16T05:49:53.427", "CreatedDate":"2014-11-20T01:46:22.03", "DateOfService":"", "ContactMethod":"Web-Availity", "IsReferral":"N", "CustomerGenkey":"8700004224713", "GroupId":"627426", "PlatformCode":"MTV", "CoverageSequenceNumber":"106", "EffectiveDate":"2012-01-01T00:00:00", "InpatientDetails":{ "AdmissionDate":"", "AdmissionType":"", "BedType":"", "DischargeDate":"", "TotalDaysApproved":"0", "LastCoveredDate":"" },'+
                            ' "OutpatientDetails":{ "FirstDay":"2014-11-20T00:00:00", "LastDay":"2014-12-20T00:00:00", "ServiceType":"Consultation with Treatment" }, "ProcedureDetails":{ "ProcedureDetail":[ { "Code":"11921", "Description":"Tattooing, intradermal introduction of insoluble opaque pigments to correct color defects of skin, including micropigmentation; 6.1 to 20.0 sq cm", "ServiceCode":"", "Status":"Pended", "Visit":{ "RequestedUnits":"1", "ApprovedUnits":"1", "TypeOfUnits":"Number of Visits" } } ] }, "DiagnosisDetails":{ "DiagnosisDetail":[ { "Type":"PrimaryDiagnosis", "Code":"N18.2", "Description":"Chronic kidney disease, stage 2 (mild)", "Status":"Pended" } ] },'+
                            ' "ProviderDetails":{ "ProviderDetail":[ { "Type":"TreatingProvider", "Id":"187710093124", "Name":"", "TaxId":"260353034", "ParticipatingStatus":"Par", "AddressDetails":{ "Address":[ { "AddressType":"Primary Service", "Street_1":"225 Abraham Flexner Way", "Street_2":"", "City":"Louisville", "County":"", "State":"KY", "ZipCode":"40202", "ZipCode_ext":"1846", "PhoneNumber":"502-561-4263" } ] } }, { "Type":"Facility", "Id":"187710093124", "Name":"Norton, Artis", "TaxId":"260353034", "ParticipatingStatus":"Par", "AddressDetails":{ "Address":[ '+
                            '{ "AddressType":"Primary Service", "Street_1":"225 Abraham Flexner Way", "Street_2":"", "City":"Louisville", "County":"", "State":"KY", "ZipCode":"40202", "ZipCode_ext":"1846", "PhoneNumber":"502-561-4263" } ] } }, { "Type":"RequestingProvider", "Id":"101290403525", "Name":"Ames, Frederick", "TaxId":"746001118", "ParticipatingStatus":"Par", "AddressDetails":{ "Address":[ { "AddressType":"Primary Service", "Street_1":"225 Abraham Flexner Way", "Street_2":"", "City":"Louisville", "County":"", "State":"KY", "ZipCode":"40202", "ZipCode_ext":"1846", "PhoneNumber":"502-561-4263" } ] } } ] }, '+
                            '"RequestType":"Pre-Auth", "StatusReason":"" } ] }, "MemberInfo":{ "FirstName":"JAMES", "LastName":"BREITLING", "MiddleName":"A", "DateOfBirth":"01/15/1951", "Gender":"M", "SubscriberId":"001482523", "DependentCode":"01", "ODS_MemberGenkey":"1005447107", "SdrPersonID":"1005447107" }, "TotalRecordsFound":"1" } }';
                            
            ClinicalAuthDetailsResponse_H_HUM oresponse = new ClinicalAuthDetailsResponse_H_HUM();
            oresponse = (ClinicalAuthDetailsResponse_H_HUM)JSON.deserialize(sJson,ClinicalAuthDetailsResponse_H_HUM.class);
            objAuthS.checkAuthSecurity(oresponse,polMember.id);
            system.assertEquals(objAuthS.checkAuthSecurity(oresponse,polMember.id),true);
           
            HttpRequest request = new HttpRequest();
            HUM_Webservice_Callout__c svcHeaders = HUM_Webservice_Callout__c.getValues('Clinical_AuthSummary');
            objAuthS.setupRequest(request,svcHeaders,polMember.Id);
            system.assertEquals(Null,objAuthS.sBodyReq);
            
            Test.stopTest();
        }    
    }
             /*
     * Method Name : testsecurity
     * Description : Showing Auth Summary List based on security class result. 
     * Return type : void
     * Paramater   : NA
     */ 
    static testMethod void testsecurity() 
    {
    Provider_functionality_switch__c oProvSwitch1 = new Provider_functionality_switch__c();
         oProvSwitch1.Activate_New_Authorization_Service__c = false;
         insert oProvSwitch1;
                
        User u = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];        
        system.runAs(u)
        {
            Policy_Member__c polMember = [Select id from Policy_Member__c where Name = 'Policy Member PM1' limit 1];
            Account objact = [select Id,Enterprise_ID__c  from Account where name = 'Test Account Cases1' limit 1];
            HttpRequest request1 = new HttpRequest();
            HUM_Webservice_Callout__c oHUMAuthSummaryService = HUM_Webservice_Callout__c.getAll().values();
            string Response = '{ "AuthSummaryResponse":{ "AuthSummaryList":{ "AuthSummary":['+
                            '{ "AuthID":"093033626", "AuthType":"Inpatient", "AuthStatus":"Pended", "CustomerGenkey":"0600004396117", "FacilityProviderName":"Gonzalez, Jorge", "GroupId":"R0877001", "RequestingProviderName":"Gonzalez, Jorge", "LastModifiedDateTime":"2015-12-04T02:58:34.29", "TreatingProviderName":"Gonzalez, Jorge", "InpatientDetails":{ "AdmissionDate":"2015-12-04T00:00:00", "AdmissionType":"Inpt Rehab", "BedType":"", "DischargeDate":"2015-12-05T00:00:00", "TotalDaysApproved":"0", "LastCoveredDate":"" }, "OutpatientDetails":{ "FirstDay":"0001-01-01T00:00:00", "LastDay":"0001-01-01T00:00:00", "ServiceType":"" } },'+
                            '{ "AuthID":"093033556", "AuthType":"BHInpatient", "AuthStatus":"Approved", "CustomerGenkey":"0600004396117", "FacilityProviderName":"DASCO NORTON HME", "GroupId":"R0877001", "RequestingProviderName":"DASCO NORTON HME", "LastModifiedDateTime":"2015-11-27T05:49:38.813", "TreatingProviderName":"DASCO NORTON HME", "InpatientDetails":{ "AdmissionDate":"2015-11-27T00:00:00", "AdmissionType":"Inpatient Psychiatric", "BedType":"", "DischargeDate":"0001-01-01T00:00:00", "TotalDaysApproved":"0", "LastCoveredDate":"" }, "OutpatientDetails":{ "FirstDay":"0001-01-01T00:00:00", "LastDay":"0001-01-01T00:00:00", "ServiceType":"" } },'+
                            '{ "AuthID":"093013814", "AuthType":"Outpatient", "AuthStatus":"Approved", "CustomerGenkey":"0600004396117", "FacilityProviderName":"TRI COUNTY DOCTORS INC", "GroupId":"R0877003", "RequestingProviderName":"TRI COUNTY DOCTORS INC", "LastModifiedDateTime":"2015-11-09T06:07:28.23", "TreatingProviderName":"TRI COUNTY DOCTORS INC", "InpatientDetails":{ "AdmissionDate":"0001-01-01T00:00:00", "AdmissionType":"", "BedType":"", "DischargeDate":"0001-01-01T00:00:00", "TotalDaysApproved":"0", "LastCoveredDate":"" }, "OutpatientDetails":{ "FirstDay":"2014-03-14T00:00:00", "LastDay":"2014-04-12T00:00:00", "ServiceType":"Observation" } }]'+ 
                            '}, "TotalRecordsFound":"50" } }';
          

           
            Test.startTest();
            Authsummary_s_Hum objAuthS = new Authsummary_s_Hum();
            objAuthS.buildRequestv2(polMember.id);
            List<AuthRefferal_Wrapper_DTO_HUM> Authsummarylist = new  List<AuthRefferal_Wrapper_DTO_HUM>();
            Authsummarylist  = objAuthS.processResponse(Response);
           
            
            string sdate  = '02--08-1990:11:22:000T';
            objAuthS.formateDate(sdate  );
            string sJson  = '{ "AuthorizationsResponse":{ "Authorizations":{ "Authorization":[ { "AuthID":"093020165", "AuthType":"Outpatient", "AuthStatus":"Pended", "UpdatedDate":"2014-12-16T05:49:53.427", "CreatedDate":"2014-11-20T01:46:22.03", "DateOfService":"", "ContactMethod":"Web-Availity", "IsReferral":"N", "CustomerGenkey":"8700004224713", "GroupId":"627426", "PlatformCode":"MTV", "CoverageSequenceNumber":"106", "EffectiveDate":"2012-01-01T00:00:00", "InpatientDetails":{ "AdmissionDate":"", "AdmissionType":"", "BedType":"", "DischargeDate":"", "TotalDaysApproved":"0", "LastCoveredDate":"" },'+
                            ' "OutpatientDetails":{ "FirstDay":"2014-11-20T00:00:00", "LastDay":"2014-12-20T00:00:00", "ServiceType":"Consultation with Treatment" }, "ProcedureDetails":{ "ProcedureDetail":[ { "Code":"11921", "Description":"Tattooing, intradermal introduction of insoluble opaque pigments to correct color defects of skin, including micropigmentation; 6.1 to 20.0 sq cm", "ServiceCode":"", "Status":"Pended", "Visit":{ "RequestedUnits":"1", "ApprovedUnits":"1", "TypeOfUnits":"Number of Visits" } } ] }, "DiagnosisDetails":{ "DiagnosisDetail":[ { "Type":"PrimaryDiagnosis", "Code":"N18.2", "Description":"Chronic kidney disease, stage 2 (mild)", "Status":"Pended" } ] },'+
                            ' "ProviderDetails":{ "ProviderDetail":[ { "Type":"TreatingProvider", "Id":"187710093124", "Name":"", "TaxId":"260353034", "ParticipatingStatus":"Par", "AddressDetails":{ "Address":[ { "AddressType":"Primary Service", "Street_1":"225 Abraham Flexner Way", "Street_2":"", "City":"Louisville", "County":"", "State":"KY", "ZipCode":"40202", "ZipCode_ext":"1846", "PhoneNumber":"502-561-4263" } ] } }, { "Type":"Facility", "Id":"187710093124", "Name":"Norton, Artis", "TaxId":"260353034", "ParticipatingStatus":"Par", "AddressDetails":{ "Address":[ '+
                            '{ "AddressType":"Primary Service", "Street_1":"225 Abraham Flexner Way", "Street_2":"", "City":"Louisville", "County":"", "State":"KY", "ZipCode":"40202", "ZipCode_ext":"1846", "PhoneNumber":"502-561-4263" } ] } }, { "Type":"RequestingProvider", "Id":"101290403525", "Name":"Ames, Frederick", "TaxId":"746001118", "ParticipatingStatus":"Par", "AddressDetails":{ "Address":[ { "AddressType":"Primary Service", "Street_1":"225 Abraham Flexner Way", "Street_2":"", "City":"Louisville", "County":"", "State":"KY", "ZipCode":"40202", "ZipCode_ext":"1846", "PhoneNumber":"502-561-4263" } ] } } ] }, '+
                            '"RequestType":"Pre-Auth", "StatusReason":"" } ] }, "MemberInfo":{ "FirstName":"JAMES", "LastName":"BREITLING", "MiddleName":"A", "DateOfBirth":"01/15/1951", "Gender":"M", "SubscriberId":"001482523", "DependentCode":"01", "ODS_MemberGenkey":"1005447107", "SdrPersonID":"1005447107" }, "TotalRecordsFound":"1" } }';
                            
            ClinicalAuthDetailsResponse_H_HUM oresponse = new ClinicalAuthDetailsResponse_H_HUM();
            oresponse = (ClinicalAuthDetailsResponse_H_HUM)JSON.deserialize(sJson,ClinicalAuthDetailsResponse_H_HUM.class);
            objAuthS.checkAuthSecurity(oresponse,polMember.id);
            system.assertEquals(objAuthS.checkAuthSecurity(oresponse,polMember.id),true);
           
            HttpRequest request = new HttpRequest();
            HUM_Webservice_Callout__c svcHeaders = HUM_Webservice_Callout__c.getValues('Clinical_AuthSummary');
            objAuthS.setupRequest(request,svcHeaders,polMember.Id);
            system.assertEquals(Null,objAuthS.sBodyReq);
            
            Test.stopTest();
    }
}
            }