/****************************************************************************************
Apex Class Name  : AuthSummary_T_HUM
Version          : 1.0 
Created Date     : January 04 2016
Function         : Test class to Authsummary_c_Hum  Apex class 
    Modification Log
 *      Developer               Code Review            Date                 Description
 * --------------------------------------------------------------------------------------------------------        
 *   Kritika Popat               29042             01/04/2016              Original Version     
 *   Ranjeeth N                  29042             01/11/2016              Modified class to increase code coverage
 *   Sruthi Adivikolanu          29646             02/01/2016              updated the test class to increase code coverage.
 *   Ranjeeth N                  30647             03/04/2016              changed the custom setting values of callout mapping.
 *   Santhi Mandava              30647             03/26/2016              Fixed code coverage issue.  
 *   Prasanthi Kandula           30854             04/06/2016              Updated test method and increased code coverage.
 *   Praveen Kumar Parimi        28468             05/26/2016              Removed fetchInteractionId() method since Req#196131 is cancelled.
 *   Santhi Mandava              40488             09/20/2016              Fixed test class failure issue.
 *   Harika Devi Kalidindi       103812            06/21/2017              Fixed Code coverage issue as a part of REQ - 320375
 *   Sai Ayyagari                184451            02/21/2018              REQ - 350853 Fix for HUMExceptionHelper, store the list of exceptions capture
 *   Riya Agarwal                                  01/05/2021              REQ - 1835131 - PR00095212- MF1 -SF -Auto-Populate Custom Benefit Grid
 *	 Rahul Chaurasia							   05/28/2021			   Req 2275290 - Test Code Coverage Increase
 *   Anuradha Gajbhe                               06/18/2021              User Story 2315210 MF 6 - SF - Tech - Performance Tuning - Authorization Summary and Detail Pages.
  ***********************************************************************************************************/
@isTest
private class AuthSummary_T_HUM
{
     /*
     * Method Name : setup
     * Description : Create common test Data
     * Return type : void
     * Paramater   : NA
     */ 
      @testSetup static void setup() 
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
                oHUMAuthSummaryService.Certificate_Name__c='testts';
                oHUMAuthSummaryService.End_Point_URL__c='https://testst-crmservicews.humana.com/';
                oHUMAuthSummaryService.Timeout__c='120000';
                oHUMAuthSummaryService.Content_Type__c='application/json';
                oHUMAuthSummaryService.Request_Type__c='POST';
                insert oHUMAuthSummaryService;
                system.assert(oHUMAuthSummaryService.id!=null);
                system.assert(oHUMAuthSummaryService.name == 'Clinical_AuthSummary');

                HUM_Webservice_Callout__c oHUMAuthSummaryServiceV2 = new HUM_Webservice_Callout__c();
                oHUMAuthSummaryServiceV2.name='Clinical_AuthSummary_V2';
                oHUMAuthSummaryServiceV2.Active__c=true;
                oHUMAuthSummaryServiceV2.Certificate_Name__c='testts';
                oHUMAuthSummaryServiceV2.End_Point_URL__c='https://testst-crmservicews.humana.com/';
                oHUMAuthSummaryServiceV2.Timeout__c='120000';
                oHUMAuthSummaryServiceV2.Content_Type__c='application/json';
                oHUMAuthSummaryServiceV2.Request_Type__c='POST';
                insert oHUMAuthSummaryServiceV2;
                system.assert(oHUMAuthSummaryServiceV2.id!=null);
                system.assert(oHUMAuthSummaryServiceV2.name == 'Clinical_AuthSummary_V2');

                HUM_Webservice_Callout__c oHUMAuthSummaryServicedetail = new HUM_Webservice_Callout__c();
                oHUMAuthSummaryServicedetail.name='Clinical_AuthDetail';
                oHUMAuthSummaryServicedetail.Active__c=true;
                oHUMAuthSummaryServicedetail.Certificate_Name__c='Humana_GuidanceCare_Venus';
                oHUMAuthSummaryServicedetail.End_Point_URL__c='https://testtest-crmservicews.humana.com';
                oHUMAuthSummaryServicedetail.Timeout__c='120000';
                oHUMAuthSummaryServicedetail.Content_Type__c='application/json';
                oHUMAuthSummaryServicedetail.Request_Type__c='POST';
                insert oHUMAuthSummaryServicedetail ;
                system.assert(oHUMAuthSummaryServicedetail.id!=null);
                system.assert(oHUMAuthSummaryServicedetail.name == 'Clinical_AuthDetail');
               
               HUM_Webservice_Callout__c oHUAuthDetailServicedetail = new HUM_Webservice_Callout__c();
                oHUAuthDetailServicedetail.name='Clinical_AuthDetail_V2';
                oHUAuthDetailServicedetail.Active__c=true;
                oHUAuthDetailServicedetail.Certificate_Name__c='Humana_GuidanceCare_Venus';
                oHUAuthDetailServicedetail.End_Point_URL__c='https://testtest-crmservicews.humana.com';
                oHUAuthDetailServicedetail.Timeout__c='120000';
                oHUAuthDetailServicedetail.Content_Type__c='application/json';
                oHUAuthDetailServicedetail.Request_Type__c='POST';
                insert oHUAuthDetailServicedetail ;
                system.assert(oHUAuthDetailServicedetail.id!=null);
                system.assert(oHUAuthDetailServicedetail.name == 'Clinical_AuthDetail_V2');
                             }
        }  
    /*
     * Method Name : authSummary
     * Description : To call the authsummary details to check the values
     * Return type : void
     * Paramater   : NA
     */ 
    static testMethod void authSummary() 
    {
        Test.setCurrentPage(Page.Authsummary_VF_Hum);
        Policy_Member__c polMember = [Select id,Name from Policy_Member__c where Name = 'Policy Member PM1' limit 1];
        system.assert(polMember.name == 'Policy Member PM1');

        Account oAccount = [Select Id, Name from Account where Name = 'Test Account Cases1' limit 1];
        system.assert(oAccount.name == 'Test Account Cases1');
        Provider_functionality_switch__c oProvSwitch = new Provider_functionality_switch__c();
        oProvSwitch.Activate_New_Authorization_Service__c = true;
        insert oProvSwitch;
        //Add ID to URL
        ApexPages.currentPage().getParameters().put('Id',polMember.Id);
        system.assertEquals(ApexPages.currentPage().getParameters().get('Id'),polMember.Id);
        ApexPages.currentPage().getParameters().put('Name',oAccount.Name);
        system.assertEquals(ApexPages.currentPage().getParameters().get('Name'),oAccount.Name);

        User u = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
    system.runAs(u)
        {
            Test.startTest();
            Authsummary_c_Hum  objSummary = new Authsummary_c_Hum();
            objSummary.initiaterequest();
            Continuation objCon = new Continuation(120);
            objCon.continuationMethod='processResponse';
            objSummary.bTotalRecordFlag = true;
            AuthSummary_S_HUM obj = new AuthSummary_S_HUM();
            obj.totalRecordFound = 10;
            objSummary.processResponse();

            HttpResponse res = new HttpResponse();
            res.setHeader('Content-Type', 'application/json');
            string sjson  = '{ "AuthSummaryResponse":{ "AuthSummaryList":{ "AuthSummary":['+
    '{ "AuthID":"093033626", "AuthType":"Inpatient", "AuthStatus":"Pended", "CustomerGenkey":"0600004396117", "FacilityProviderName":"Gonzalez, Jorge", "GroupId":"R0877001", "RequestingProviderName":"Gonzalez, Jorge", "LastModifiedDateTime":"2015-12-04T02:58:34.29", "TreatingProviderName":"Gonzalez, Jorge", "InpatientDetails":{ "AdmissionDate":"2015-12-04T00:00:00", "AdmissionType":"Inpt Rehab", "BedType":"", "DischargeDate":"2015-12-05T00:00:00", "TotalDaysApproved":"0", "LastCoveredDate":"" }, "OutpatientDetails":{ "FirstDay":"0001-01-01T00:00:00", "LastDay":"0001-01-01T00:00:00", "ServiceType":"" } },'+
    '{ "AuthID":"093033556", "AuthType":"BHInpatient", "AuthStatus":"Approved", "CustomerGenkey":"0600004396117", "FacilityProviderName":"DASCO NORTON HME", "GroupId":"R0877001", "RequestingProviderName":"DASCO NORTON HME", "LastModifiedDateTime":"2015-11-27T05:49:38.813", "TreatingProviderName":"DASCO NORTON HME", "InpatientDetails":{ "AdmissionDate":"2015-11-27T00:00:00", "AdmissionType":"Inpatient Psychiatric", "BedType":"", "DischargeDate":"0001-01-01T00:00:00", "TotalDaysApproved":"0", "LastCoveredDate":"" }, "OutpatientDetails":{ "FirstDay":"0001-01-01T00:00:00", "LastDay":"0001-01-01T00:00:00", "ServiceType":"" } },'+
    '{ "AuthID":"093013814", "AuthType":"Outpatient", "AuthStatus":"Approved", "CustomerGenkey":"0600004396117", "FacilityProviderName":"TRI COUNTY DOCTORS INC", "GroupId":"R0877003", "RequestingProviderName":"TRI COUNTY DOCTORS INC", "LastModifiedDateTime":"2015-11-09T06:07:28.23", "TreatingProviderName":"TRI COUNTY DOCTORS INC", "InpatientDetails":{ "AdmissionDate":"0001-01-01T00:00:00", "AdmissionType":"", "BedType":"", "DischargeDate":"0001-01-01T00:00:00", "TotalDaysApproved":"0", "LastCoveredDate":"" }, "OutpatientDetails":{ "FirstDay":"2014-03-14T00:00:00", "LastDay":"2014-04-12T00:00:00", "ServiceType":"Observation" } }]'+ 
    '}, "TotalRecordsFound":"50" } }';

            res.setBody(sjson);
            res.setStatusCode(200); 
            objSummary.responseparse(res);
            system.assertEquals(objSummary.bshowError,true);

            list<AuthRefferal_Wrapper_DTO_HUM> AuthResponseList  = new list<AuthRefferal_Wrapper_DTO_HUM>();
            AuthRefferal_Wrapper_DTO_HUM obwrapper = new AuthRefferal_Wrapper_DTO_HUM();
             obwrapper.sAuthorizationOrReferralNumber ='1234';
             System.assertEquals('1234',obwrapper.sAuthorizationOrReferralNumber);

             obwrapper.sAuthorizationType = 'ALL';
             System.assertEquals('ALL',obwrapper.sAuthorizationType);

             obwrapper.sOverallStatus  = 'SS'; 
             System.assertEquals('SS',obwrapper.sOverallStatus);      

             obwrapper.sAdmFirstDay  = '22';
             System.assertEquals('22',obwrapper.sAdmFirstDay);

             obwrapper.sDischargeLastDay = '22';
             System.assertEquals('22',obwrapper.sDischargeLastDay);

             obwrapper.sServiceType = 'test';
             System.assertEquals('test',obwrapper.sServiceType);

             obwrapper.sTreatingProvider ='ets';
             System.assertEquals('ets',obwrapper.sTreatingProvider);

             obwrapper.sFacility = 'facility';
             System.assertEquals('facility',obwrapper.sFacility);

             obwrapper.sRequestingrovider = 'tetsrqstprv';
             System.assertEquals('tetsrqstprv',obwrapper.sRequestingrovider);

             obwrapper.sGroupId = '12345';
             System.assertEquals('12345',obwrapper.sGroupId);

             obwrapper.bIsAccessible  =  true;
             System.assertEquals(true, obwrapper.bIsAccessible);

             AuthResponseList.add(obwrapper);
             AuthRefferal_Wrapper_DTO_HUM obwrappernegative = new AuthRefferal_Wrapper_DTO_HUM();
             obwrappernegative .sAuthorizationOrReferralNumber ='1234';
             System.assertEquals('1234',obwrappernegative.sAuthorizationOrReferralNumber);

             obwrappernegative .sAuthorizationType = 'ALL';
             System.assertEquals('ALL',obwrappernegative.sAuthorizationType);

             obwrappernegative .sOverallStatus  = 'SS'; 
             System.assertEquals('SS',obwrappernegative.sOverallStatus);      

             obwrappernegative.sAdmFirstDay  = '22';
             System.assertEquals('22',obwrappernegative.sAdmFirstDay);

             obwrappernegative.bIsAccessible  =  false;
             System.assertEquals(false, obwrappernegative .bIsAccessible);
             AuthResponseList.add(obwrappernegative);

             objSummary.finalAuthsummarylist(AuthResponseList);
            objSummary.bshowresults =true;
            objSummary.lstAuthResponse=AuthResponseList;


            HttpResponse res1 = new HttpResponse();
            res1.setHeader('Content-Type', 'application/json');
            string sjson1  = '{ "AuthSummaryResponse":{ "AuthSummaryList":{ "AuthSummary":['+
    '{ "AuthID":"093033626", "AuthType":"Inpatient", "AuthStatus":"Pended", "CustomerGenkey":"0600004396117", "FacilityProviderName":"Gonzalez, Jorge", "GroupId":"R0877001", "RequestingProviderName":"Gonzalez, Jorge", "LastModifiedDateTime":"2015-12-04T02:58:34.29", "TreatingProviderName":"Gonzalez, Jorge", "InpatientDetails":{ "AdmissionDate":"2015-12-04T00:00:00", "AdmissionType":"Inpt Rehab", "BedType":"", "DischargeDate":"2015-12-05T00:00:00", "TotalDaysApproved":"0", "LastCoveredDate":"" }, "OutpatientDetails":{ "FirstDay":"0001-01-01T00:00:00", "LastDay":"0001-01-01T00:00:00", "ServiceType":"" } },'+
    '{ "AuthID":"093033556", "AuthType":"BHInpatient", "AuthStatus":"Approved", "CustomerGenkey":"0600004396117", "FacilityProviderName":"DASCO NORTON HME", "GroupId":"R0877001", "RequestingProviderName":"DASCO NORTON HME", "LastModifiedDateTime":"2015-11-27T05:49:38.813", "TreatingProviderName":"DASCO NORTON HME", "InpatientDetails":{ "AdmissionDate":"2015-11-27T00:00:00", "AdmissionType":"Inpatient Psychiatric", "BedType":"", "DischargeDate":"0001-01-01T00:00:00", "TotalDaysApproved":"0", "LastCoveredDate":"" }, "OutpatientDetails":{ "FirstDay":"0001-01-01T00:00:00", "LastDay":"0001-01-01T00:00:00", "ServiceType":"" } },'+
    '{ "AuthID":"093013814", "AuthType":"Outpatient", "AuthStatus":"Approved", "CustomerGenkey":"0600004396117", "FacilityProviderName":"TRI COUNTY DOCTORS INC", "GroupId":"R0877003", "RequestingProviderName":"TRI COUNTY DOCTORS INC", "LastModifiedDateTime":"2015-11-09T06:07:28.23", "TreatingProviderName":"TRI COUNTY DOCTORS INC", "InpatientDetails":{ "AdmissionDate":"0001-01-01T00:00:00", "AdmissionType":"", "BedType":"", "DischargeDate":"0001-01-01T00:00:00", "TotalDaysApproved":"0", "LastCoveredDate":"" }, "OutpatientDetails":{ "FirstDay":"2014-03-14T00:00:00", "LastDay":"2014-04-12T00:00:00", "ServiceType":"Observation" } }]'+ 
    '}, "TotalRecordsFound":"50" } }';

            res1.setBody(sjson1);
            res1.setStatusCode(300); 
            objSummary.responseparse(res1);
            system.assertEquals(objSummary.bshowserviceError,true);

            HttpResponse res2 = new HttpResponse();
            res2.setHeader('Content-Type', 'application/json');
            string sjson2  = '{ "AuthSummaryResponse":{ "AuthSummaryList":{ "AuthSummary":['+']'+ '}, "TotalRecordsFound":"0" } }';

            res2.setBody(sjson2);
            res2.setStatusCode(200); 
            objSummary.responseparse(res2);
            system.assertEquals(objSummary.bshowError,true);

            HttpResponse res3 = new HttpResponse();
            res3.setHeader('Content-Type', 'application/json');
            string sjson3  = '{ "AuthorizationsResponse":{ "Authorizations":{ "Authorization":[ { "AuthID":"093020165", "AuthType":"Outpatient", "AuthStatus":"Pended", "UpdatedDate":"2014-12-16T05:49:53.427", "CreatedDate":"2014-11-20T01:46:22.03", "DateOfService":"", "ContactMethod":"Web-Availity", "IsReferral":"N", "CustomerGenkey":"8700004224713", "GroupId":"627426", "PlatformCode":"MTV", "CoverageSequenceNumber":"106", "EffectiveDate":"2012-01-01T00:00:00", "InpatientDetails":{ "AdmissionDate":"", "AdmissionType":"", "BedType":"", "DischargeDate":"", "TotalDaysApproved":"0", "LastCoveredDate":"" },'+
    ' "OutpatientDetails":{ "FirstDay":"2014-11-20T00:00:00", "LastDay":"2014-12-20T00:00:00", "ServiceType":"Consultation with Treatment" }, "ProcedureDetails":{ "ProcedureDetail":[ { "Code":"11921", "Description":"Tattooing, intradermal introduction of insoluble opaque pigments to correct color defects of skin, including micropigmentation; 6.1 to 20.0 sq cm", "ServiceCode":"", "Status":"Pended", "Visit":{ "RequestedUnits":"1", "ApprovedUnits":"1", "TypeOfUnits":"Number of Visits" } } ] }, "DiagnosisDetails":{ "DiagnosisDetail":[ { "Type":"PrimaryDiagnosis", "Code":"N18.2", "Description":"Chronic kidney disease, stage 2 (mild)", "Status":"Pended" } ] },'+
    ' "ProviderDetails":{ "ProviderDetail":[ { "Type":"TreatingProvider", "Id":"187710093124", "Name":"", "TaxId":"260353034", "ParticipatingStatus":"Par", "AddressDetails":{ "Address":[ { "AddressType":"Primary Service", "Street_1":"225 Abraham Flexner Way", "Street_2":"", "City":"Louisville", "County":"", "State":"KY", "ZipCode":"40202", "ZipCode_ext":"1846", "PhoneNumber":"502-561-4263" } ] } }, { "Type":"Facility", "Id":"187710093124", "Name":"Norton, Artis", "TaxId":"260353034", "ParticipatingStatus":"Par", "AddressDetails":{ "Address":[ '+
    '{ "AddressType":"Primary Service", "Street_1":"225 Abraham Flexner Way", "Street_2":"", "City":"Louisville", "County":"", "State":"KY", "ZipCode":"40202", "ZipCode_ext":"1846", "PhoneNumber":"502-561-4263" } ] } }, { "Type":"RequestingProvider", "Id":"101290403525", "Name":"Ames, Frederick", "TaxId":"746001118", "ParticipatingStatus":"Par", "AddressDetails":{ "Address":[ { "AddressType":"Primary Service", "Street_1":"225 Abraham Flexner Way", "Street_2":"", "City":"Louisville", "County":"", "State":"KY", "ZipCode":"40202", "ZipCode_ext":"1846", "PhoneNumber":"502-561-4263" } ] } } ] }, '+
    '"RequestType":"Pre-Auth", "StatusReason":"" } ] }, "MemberInfo":{ "FirstName":"JAMES", "LastName":"BREITLING", "MiddleName":"A", "DateOfBirth":"01/15/1951", "Gender":"M", "SubscriberId":"001482523", "DependentCode":"01", "ODS_MemberGenkey":"1005447107", "SdrPersonID":"1005447107" }, "TotalRecordsFound":"1" } }';

            res3.setBody(sjson3);
            res3.setStatusCode(200); 
            objSummary.checkAuthId(res3);

            objSummary.SUserinputAuthId ='123456';
            ApexPages.currentPage().getParameters().put('authIdAF', '123456');
            objSummary.authDetailsRequest();
			
            HUM_Webservice_Callout__c obj1 = [select id from HUM_Webservice_Callout__c where name='Clinical_AuthDetail_V2'];
            delete obj1;
            objSummary.authDetailsRequest();
            objSummary.authDetailscheckRequest();
            
            objSummary.processResponseDetail();
            System.assertEquals(objSummary.bAuthidexists,true);

            ApexPages.currentPage().getParameters().put('interactionId', 'url.com&interactionId=a0W2345678Htblqase');            
            objSummary.securityError(true);
            objSummary.setErrorMessage();
            res3.setStatusCode(300);
            objSummary.checkAuthId(res3);
            System.assertEquals(objSummary.bshowserviceError,true);

            string sjson4  = '{ "AuthorizationsResponse":{}';
            res3.setBody(sjson4);
            objSummary.checkAuthId(res3);

            objSummary.SUserinputAuthId ='';
            objSummary.sRecId = '';
            objSummary.authDetailsRequest();
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
        Test.setCurrentPage(Page.Authsummary_VF_Hum);
        Policy_Member__c polMember = [Select id from Policy_Member__c where Name = 'Policy Member PM1' limit 1];
        Account oAccount = [Select Id, Name from Account where Name = 'Test Account Cases1' limit 1];

        User u = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
    system.runAs(u)
        {
            Test.startTest();
            ApexPages.currentPage().getParameters().put('Id',polMember.Id);
            list<AuthRefferal_Wrapper_DTO_HUM> AuthResponseList  = new list<AuthRefferal_Wrapper_DTO_HUM>();
            Authsummary_c_Hum  objSummary = new Authsummary_c_Hum();
            AuthRefferal_Wrapper_DTO_HUM obwrappernegative = new AuthRefferal_Wrapper_DTO_HUM();
            obwrappernegative .sAuthorizationOrReferralNumber ='1234';
            System.assertEquals('1234',obwrappernegative.sAuthorizationOrReferralNumber);

            obwrappernegative .sAuthorizationType = 'ALL';
            System.assertEquals('ALL',obwrappernegative.sAuthorizationType);

            obwrappernegative .sOverallStatus  = 'SS'; 
            System.assertEquals('SS',obwrappernegative.sOverallStatus);   

            obwrappernegative.sAdmFirstDay  = '22';
            System.assertEquals('22',obwrappernegative.sAdmFirstDay);

            obwrappernegative.bIsAccessible  =  false;
            System.assertEquals(false, obwrappernegative .bIsAccessible);

            AuthResponseList.add(obwrappernegative);
            AuthRefferal_Wrapper_DTO_HUM obwrappernegative1 = new AuthRefferal_Wrapper_DTO_HUM();
            obwrappernegative1.sAuthorizationOrReferralNumber ='1234';
            System.assertEquals('1234',obwrappernegative1.sAuthorizationOrReferralNumber);

            obwrappernegative1.sAuthorizationType = 'ALL';
            System.assertEquals('ALL',obwrappernegative1.sAuthorizationType);

            obwrappernegative1.sOverallStatus  = 'SS'; 
            System.assertEquals('SS',obwrappernegative1.sOverallStatus);  

            obwrappernegative1.sAdmFirstDay  = '22';
            System.assertEquals('22',obwrappernegative1.sAdmFirstDay);

            obwrappernegative1.bIsAccessible  =  true;
            System.assertEquals(true, obwrappernegative1 .bIsAccessible);

            AuthResponseList.add(obwrappernegative1);
            objSummary.finalAuthsummarylist(AuthResponseList);
            objSummary.bshowresults =false;

            List<AuthRefferal_Wrapper_DTO_HUM> Authemptylist = new List<AuthRefferal_Wrapper_DTO_HUM>();
            objSummary.finalAuthsummarylist(Authemptylist);
            System.assertEquals(Authemptylist.size(),0);

            AuthSummary_C_HUM obj = objSummary.getThis();
            System.assertEquals(false,obj.bshowresults);

            List<sObject> lst = objSummary.getRecords();
            System.assertEquals(Null,lst);

            sObject objS = objSummary.getRecord();
            System.assertEquals(Null,objS);
            
             List<Error_Log__c> lstLog = New List<Error_Log__c>();
            Error_Log__c error = New Error_Log__c(Status__c = 'AuthSummary');
            lstLog.add(error);
            
            objSummary.listErrorLogs = lstLog;
            System.assert(objSummary.listErrorLogs.size() > 0);

            test.stoptest();
         }
     }

     static testMethod void authSummaryOldService()
    {
        Test.setCurrentPage(Page.Authsummary_VF_Hum);
        Policy_Member__c polMember = [Select id,Name from Policy_Member__c where Name = 'Policy Member PM1' limit 1];
        system.assert(polMember.name == 'Policy Member PM1');

        Account oAccount = [Select Id, Name from Account where Name = 'Test Account Cases1' limit 1];
        system.assert(oAccount.name == 'Test Account Cases1');
        
         Provider_functionality_switch__c oProvSwitch1 = new Provider_functionality_switch__c();
                oProvSwitch1.Activate_New_Authorization_Service__c = false;
                insert oProvSwitch1;

        //Add ID to URL
        ApexPages.currentPage().getParameters().put('Id',polMember.Id);
        system.assertEquals(ApexPages.currentPage().getParameters().get('Id'),polMember.Id);
        ApexPages.currentPage().getParameters().put('Name',oAccount.Name);
        system.assertEquals(ApexPages.currentPage().getParameters().get('Name'),oAccount.Name);

        User u = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
    system.runAs(u)
        {
            Test.startTest();
            Authsummary_c_Hum  objSummary = new Authsummary_c_Hum();
            objSummary.initiaterequest();
            Test.stopTest();
         }
    }
	
	
	static testMethod void authSummaryNeg() 
    {
        Test.setCurrentPage(Page.Authsummary_VF_Hum);
        Policy_Member__c polMember = [Select id,Name from Policy_Member__c where Name = 'Policy Member PM1' limit 1];
        system.assert(polMember.name == 'Policy Member PM1');

        Account oAccount = [Select Id, Name from Account where Name = 'Test Account Cases1' limit 1];
        system.assert(oAccount.name == 'Test Account Cases1');
        Provider_functionality_switch__c oProvSwitch = new Provider_functionality_switch__c();
        oProvSwitch.Activate_New_Authorization_Service__c = false;
        insert oProvSwitch;
        //Add ID to URL
        ApexPages.currentPage().getParameters().put('Id',polMember.Id);
        system.assertEquals(ApexPages.currentPage().getParameters().get('Id'),polMember.Id);
        ApexPages.currentPage().getParameters().put('Name',oAccount.Name);
        system.assertEquals(ApexPages.currentPage().getParameters().get('Name'),oAccount.Name);

        User u = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
      system.runAs(u)
        {
            Test.startTest();
            Authsummary_c_Hum  objSummary = new Authsummary_c_Hum();
             objSummary.sProviderName='abc';
            objSummary.initiaterequest();
            Continuation objCon = new Continuation(120);
            objCon.continuationMethod='processResponse';
            objSummary.processResponse();

            list<AuthRefferal_Wrapper_DTO_HUM> AuthResponseList  = new list<AuthRefferal_Wrapper_DTO_HUM>();
            AuthRefferal_Wrapper_DTO_HUM obwrapper = new AuthRefferal_Wrapper_DTO_HUM();
             obwrapper.sAuthorizationOrReferralNumber ='1234';
             System.assertEquals('1234',obwrapper.sAuthorizationOrReferralNumber);

             obwrapper.sAuthorizationType = 'ALL';
             System.assertEquals('ALL',obwrapper.sAuthorizationType);

             obwrapper.sOverallStatus  = 'SS'; 
             System.assertEquals('SS',obwrapper.sOverallStatus);      

             obwrapper.sAdmFirstDay  = '22';
             System.assertEquals('22',obwrapper.sAdmFirstDay);

             obwrapper.sDischargeLastDay = '22';
             System.assertEquals('22',obwrapper.sDischargeLastDay);

             obwrapper.sServiceType = 'test';
             System.assertEquals('test',obwrapper.sServiceType);

             obwrapper.sTreatingProvider ='ets';
             System.assertEquals('ets',obwrapper.sTreatingProvider);

             obwrapper.sFacility = 'facility';
             System.assertEquals('facility',obwrapper.sFacility);

             obwrapper.sRequestingrovider = 'tetsrqstprv';
             System.assertEquals('tetsrqstprv',obwrapper.sRequestingrovider);

             obwrapper.sGroupId = '12345';
             System.assertEquals('12345',obwrapper.sGroupId);

             obwrapper.bIsAccessible  =  true;
             System.assertEquals(true, obwrapper.bIsAccessible);

             AuthResponseList.add(obwrapper);

            objSummary.finalAuthsummarylist(AuthResponseList);
            objSummary.bshowresults =true;
            objSummary.lstAuthResponse=AuthResponseList;
            
            HttpResponse res1 = new HttpResponse();
            res1.setHeader('Content-Type', 'application/json');
            string sjson1  = '{ "AuthSummaryResponse":{ "AuthSummaryList":{ "AuthSummary":['+
    '{ "AuthID":"093033626", "AuthType":"Inpatient", "AuthStatus":"Pended", "CustomerGenkey":"0600004396117", "FacilityProviderName":"Gonzalez, Jorge", "GroupId":"R0877001", "RequestingProviderName":"Gonzalez, Jorge", "LastModifiedDateTime":"2015-12-04T02:58:34.29", "TreatingProviderName":"Gonzalez, Jorge", "InpatientDetails":{ "AdmissionDate":"2015-12-04T00:00:00", "AdmissionType":"Inpt Rehab", "BedType":"", "DischargeDate":"2015-12-05T00:00:00", "TotalDaysApproved":"0", "LastCoveredDate":"" }, "OutpatientDetails":{ "FirstDay":"0001-01-01T00:00:00", "LastDay":"0001-01-01T00:00:00", "ServiceType":"" } },'+
    '{ "AuthID":"093033556", "AuthType":"BHInpatient", "AuthStatus":"Approved", "CustomerGenkey":"0600004396117", "FacilityProviderName":"DASCO NORTON HME", "GroupId":"R0877001", "RequestingProviderName":"DASCO NORTON HME", "LastModifiedDateTime":"2015-11-27T05:49:38.813", "TreatingProviderName":"DASCO NORTON HME", "InpatientDetails":{ "AdmissionDate":"2015-11-27T00:00:00", "AdmissionType":"Inpatient Psychiatric", "BedType":"", "DischargeDate":"0001-01-01T00:00:00", "TotalDaysApproved":"0", "LastCoveredDate":"" }, "OutpatientDetails":{ "FirstDay":"0001-01-01T00:00:00", "LastDay":"0001-01-01T00:00:00", "ServiceType":"" } },'+
    '{ "AuthID":"093013814", "AuthType":"Outpatient", "AuthStatus":"Approved", "CustomerGenkey":"0600004396117", "FacilityProviderName":"TRI COUNTY DOCTORS INC", "GroupId":"R0877003", "RequestingProviderName":"TRI COUNTY DOCTORS INC", "LastModifiedDateTime":"2015-11-09T06:07:28.23", "TreatingProviderName":"TRI COUNTY DOCTORS INC", "InpatientDetails":{ "AdmissionDate":"0001-01-01T00:00:00", "AdmissionType":"", "BedType":"", "DischargeDate":"0001-01-01T00:00:00", "TotalDaysApproved":"0", "LastCoveredDate":"" }, "OutpatientDetails":{ "FirstDay":"2014-03-14T00:00:00", "LastDay":"2014-04-12T00:00:00", "ServiceType":"Observation" } }]'+ 
    '}, "TotalRecordsFound":"50" } }';

            res1.setBody(sjson1);
            res1.setStatusCode(300); 
            objSummary.responseparse(res1);
            system.assertEquals(objSummary.bshowserviceError,true);

            HttpResponse res2 = new HttpResponse();
            res2.setHeader('Content-Type', 'application/json');
            string sjson2  = '{ "AuthorizationsResponse":{ "Authorizations":{ "Authorization":[ { "AuthID":"093020165", "AuthType":"Outpatient", "AuthStatus":"Pended", "UpdatedDate":"2014-12-16T05:49:53.427", "CreatedDate":"2014-11-20T01:46:22.03", "DateOfService":"", "ContactMethod":"Web-Availity", "IsReferral":"N", "CustomerGenkey":"8700004224713", "GroupId":"627426", "PlatformCode":"MTV", "CoverageSequenceNumber":"106", "EffectiveDate":"2012-01-01T00:00:00", "InpatientDetails":{ "AdmissionDate":"", "AdmissionType":"", "BedType":"", "DischargeDate":"", "TotalDaysApproved":"0", "LastCoveredDate":"" },'+
    ' "OutpatientDetails":{ "FirstDay":"2014-11-20T00:00:00", "LastDay":"2014-12-20T00:00:00", "ServiceType":"Consultation with Treatment" }, "ProcedureDetails":{ "ProcedureDetail":[ { "Code":"11921", "Description":"Tattooing, intradermal introduction of insoluble opaque pigments to correct color defects of skin, including micropigmentation; 6.1 to 20.0 sq cm", "ServiceCode":"", "Status":"Pended", "Visit":{ "RequestedUnits":"1", "ApprovedUnits":"1", "TypeOfUnits":"Number of Visits" } } ] }, "DiagnosisDetails":{ "DiagnosisDetail":[ { "Type":"PrimaryDiagnosis", "Code":"N18.2", "Description":"Chronic kidney disease, stage 2 (mild)", "Status":"Pended" } ] },'+
    ' "ProviderDetails":{ "ProviderDetail":[ { "Type":"TreatingProvider", "Id":"187710093124", "Name":"", "TaxId":"260353034", "ParticipatingStatus":"Par", "AddressDetails":{ "Address":[ { "AddressType":"Primary Service", "Street_1":"225 Abraham Flexner Way", "Street_2":"", "City":"Louisville", "County":"", "State":"KY", "ZipCode":"40202", "ZipCode_ext":"1846", "PhoneNumber":"502-561-4263" } ] } }, { "Type":"Facility", "Id":"187710093124", "Name":"Norton, Artis", "TaxId":"260353034", "ParticipatingStatus":"Par", "AddressDetails":{ "Address":[ '+
    '{ "AddressType":"Primary Service", "Street_1":"225 Abraham Flexner Way", "Street_2":"", "City":"Louisville", "County":"", "State":"KY", "ZipCode":"40202", "ZipCode_ext":"1846", "PhoneNumber":"502-561-4263" } ] } }, { "Type":"RequestingProvider", "Id":"101290403525", "Name":"Ames, Frederick", "TaxId":"746001118", "ParticipatingStatus":"Par", "AddressDetails":{ "Address":[ { "AddressType":"Primary Service", "Street_1":"225 Abraham Flexner Way", "Street_2":"", "City":"Louisville", "County":"", "State":"KY", "ZipCode":"40202", "ZipCode_ext":"1846", "PhoneNumber":"502-561-4263" } ] } } ] }, '+
    '"RequestType":"Pre-Auth", "StatusReason":"" } ] }, "MemberInfo":{ "FirstName":"JAMES", "LastName":"BREITLING", "MiddleName":"A", "DateOfBirth":"01/15/1951", "Gender":"M", "SubscriberId":"001482523", "DependentCode":"01", "ODS_MemberGenkey":"1005447107", "SdrPersonID":"1005447107" }, "TotalRecordsFound":"1" } }';

            res2.setBody(sjson2);
            res2.setStatusCode(200); 
            objSummary.checkAuthId(res2);

            objSummary.SUserinputAuthId ='123456';
            ApexPages.currentPage().getParameters().put('authIdAF', '123456');
            objSummary.authDetailsRequest();
            objSummary.authDetailscheckRequest();

            objSummary.processResponseDetail();
            System.assertEquals(objSummary.bAuthidexists,true);

            ApexPages.currentPage().getParameters().put('interactionId', 'url.com&interactionId=a0W2345678Htblqase');            
            objSummary.securityError(true);
            objSummary.setErrorMessage();
            res2.setStatusCode(300);
            objSummary.checkAuthId(res2);
            System.assertEquals(objSummary.bshowserviceError,true);

            objSummary.SUserinputAuthId ='';
            objSummary.sRecId = '';
            objSummary.authDetailsRequest();
            Test.stopTest();
         }
    }
    
    /**
    * Method to Test MentorGridService .
    */ 
    static testMethod void testMentorGridLaunch()
    {
        Test.setCurrentPage(Page.Authsummary_VF_Hum);
        Policy_Member__c polMember = [Select id,Name from Policy_Member__c where Name = 'Policy Member PM1' limit 1];
        system.assert(polMember.name == 'Policy Member PM1');

        Account oAccount = [Select Id, Name from Account where Name = 'Test Account Cases1' limit 1];
        system.assert(oAccount.name == 'Test Account Cases1');

        //Add ID to URL
        ApexPages.currentPage().getParameters().put('Id',polMember.Id);
        system.assertEquals(ApexPages.currentPage().getParameters().get('Id'),polMember.Id);
        ApexPages.currentPage().getParameters().put('Name',oAccount.Name);
        system.assertEquals(ApexPages.currentPage().getParameters().get('Name'),oAccount.Name);
        
        HUM_Webservice_Callout__c ServiceDetails = new HUM_Webservice_Callout__c(Name = 'KMDocument', Certificate_Name__c = 'Test_Certificate', Active__c = true, End_Point_URL__c = 'https://int-crmservicews.humana.com/KMDocumentServiceREST/HasSearchResults', Timeout__c = '12000', Request_Type__c = 'POST', Content_Type__c = 'application/json');
        insert ServiceDetails;
        User u = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
        System.runAs(u)
        {
            Test.startTest();
            AuthSummary_C_HUM auth = new AuthSummary_C_HUM();
            AuthSummary_C_HUM.builtHttpRequest(ServiceDetails);
            auth.MentorGridLaunch();
            auth.MentorGridService();
            auth.parseKMDResponse();
            MentorServiceRequest_DTO_HUM KDMRequestDTO = new MentorServiceRequest_DTO_HUM();
            AuthSummary_D_HUM.RequestStructureKDMService(KDMRequestDTO);
            MentorServiceResponse_DTO_HUM KMServiceResponse = new MentorServiceResponse_DTO_HUM();
            AuthSummary_D_HUM.ParseStructureKMDocument(KMServiceResponse);
            Test.stopTest();           
        }
        
    }
}