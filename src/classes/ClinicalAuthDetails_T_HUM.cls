/****************************************************************************************
Apex Class Name  : ClinicalAuthDetails_T_HUM 
Version          : 1.0 
Created Date     : January 01 2016
Function         : Test class to ClinicalAuthDetails_C_HUM  Apex class
Modification Log :
 *      Developer               Code Review            Date                 Description
 * ------------------------------------------------------------------------------------------------           
 *   Prasanthi Kandula            29039                01/01/2016              Original Version 
 *   Ranjeeth Nagishetty          22847                03/04/2016              Fixed test class failure while checkin
 *   Ranadheer                                           05/18/2021                  Code coverage
 **************************************************************************************************/
@isTest
private class ClinicalAuthDetails_T_HUM 
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
           RecordType oRT = [select id from RecordType where sObjectType='Account' and Name='Group'];
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
                
                Policy_Member__c opolMember = HUMTestDataHelperTest.getPolicyMember();
                opolMember.Name = 'Policy Member PM1';
                opolMember.Policy__c = oPolicy.Id;
                opolMember.Member__c = oAccount.Id;
                insert opolMember;
                system.assertEquals(opolMember.Id != Null,true);
                system.assertEquals(opolMember.Name,'Policy Member PM1');
                system.assertEquals(opolMember.Policy__c,oPolicy.Id);
                
                HUM_Webservice_Callout__c oHUMAuthDetailsService = new HUM_Webservice_Callout__c();
                oHUMAuthDetailsService.name='Clinical_AuthDetail';
                oHUMAuthDetailsService.Active__c=true;
                oHUMAuthDetailsService.Certificate_Name__c='test';
                oHUMAuthDetailsService.End_Point_URL__c='https://testtest-crmservicews.humana.com/';
                oHUMAuthDetailsService.Timeout__c='120000';
                oHUMAuthDetailsService.Content_Type__c='application/json';
                oHUMAuthDetailsService.Request_Type__c='POST';
                insert oHUMAuthDetailsService;
                system.assert(oHUMAuthDetailsService.id!=null);
                system.assert(oHUMAuthDetailsService.name == 'Clinical_AuthDetail');
                
                HUM_Webservice_Callout__c oHUMAuthDetailsServiceV2 = new HUM_Webservice_Callout__c();
                oHUMAuthDetailsServiceV2.name='Clinical_AuthDetail_V2';
                oHUMAuthDetailsServiceV2.Active__c=true;
                oHUMAuthDetailsServiceV2.Certificate_Name__c='Humana_GuidanceCare_Pluto';
                oHUMAuthDetailsServiceV2.End_Point_URL__c='https://testtest-crmservicews.humana.com/';
                oHUMAuthDetailsServiceV2.Timeout__c='120000';
                oHUMAuthDetailsServiceV2.Content_Type__c='application/json';
                oHUMAuthDetailsServiceV2.Request_Type__c='POST';
                insert oHUMAuthDetailsServiceV2;
                system.assert(oHUMAuthDetailsServiceV2.id!=null);
                system.assert(oHUMAuthDetailsServiceV2.name == 'Clinical_AuthDetail_V2');
				
				HUM_Webservice_Callout__c oHUMAuthDetailsServiceV3 = new HUM_Webservice_Callout__c();
                oHUMAuthDetailsServiceV3.name='AuthDetails_Communication_Records';
                oHUMAuthDetailsServiceV3.Active__c=true;
                oHUMAuthDetailsServiceV3.Certificate_Name__c='test';
                oHUMAuthDetailsServiceV3.End_Point_URL__c='https://testtest-crmservicews.humana.com/';
                oHUMAuthDetailsServiceV3.Timeout__c='120000';
                oHUMAuthDetailsServiceV3.Content_Type__c='application/json';
                oHUMAuthDetailsServiceV3.Request_Type__c='POST';
                insert oHUMAuthDetailsServiceV3;
                system.assert(oHUMAuthDetailsServiceV3.id!=null);
                system.assert(oHUMAuthDetailsServiceV3.name == 'AuthDetails_Communication_Records');

				HUM_Webservice_Callout__c oHUMAuthDetailsServiceV4 = new HUM_Webservice_Callout__c();
                oHUMAuthDetailsServiceV4.name='AuthDetails_Letters';
                oHUMAuthDetailsServiceV4.Active__c=true;
                oHUMAuthDetailsServiceV4.Certificate_Name__c='test';
                oHUMAuthDetailsServiceV4.End_Point_URL__c='https://testtest-crmservicews.humana.com/';
                oHUMAuthDetailsServiceV4.Timeout__c='120000';
                oHUMAuthDetailsServiceV4.Content_Type__c='application/json';
                oHUMAuthDetailsServiceV4.Request_Type__c='POST';
                insert oHUMAuthDetailsServiceV4;
                system.assert(oHUMAuthDetailsServiceV4.id!=null);
                system.assert(oHUMAuthDetailsServiceV4.name == 'AuthDetails_Letters');				
				
            }
        } 
         
    /*
     * Method Name : authSummaryOutPatient
     * Description : This method tests Authorization Summary for OutPatient
     * Return type : void
     * Parameter   : None
     */ 
     
    static testMethod void authSummaryOutPatient() 
    {
        Test.setCurrentPage(Page.ClinicalAuthDetails_VF_HUM);
        Policy_Member__c oPolMember = [Select id from Policy_Member__c where Name = 'Policy Member PM1' limit 1];
        ApexPages.currentPage().getParameters().put('RecId',oPolMember.Id);
        system.assertEquals(ApexPages.currentPage().getParameters().get('RecId'),oPolMember.Id);
        ApexPages.currentPage().getParameters().put('AuthId','093032249');
        system.assertEquals(ApexPages.currentPage().getParameters().get('RecId'),oPolMember.Id);
        Provider_functionality_switch__c oProvSwitch = new Provider_functionality_switch__c();
        oProvSwitch.Activate_New_Authorization_Service__c = false;
        insert oProvSwitch;

        User oUser = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
        system.runAs(oUser)
        {
            Test.startTest();
            ClinicalAuthDetails_C_HUM oDetails = new ClinicalAuthDetails_C_HUM ();
            oDetails.processResponse();   
            HttpResponse oResponse = new HttpResponse();
            oResponse.setHeader('Content-Type', 'application/json');
            string sJson  = '{ "AuthorizationsResponse":{ "Authorizations":{ "Authorization":[ { "AuthID":"093020165", "AuthType":"Outpatient", "AuthStatus":"Pended", "UpdatedDate":"2014-12-16T05:49:53.427", "CreatedDate":"2014-11-20T01:46:22.03", "DateOfService":"", "ContactMethod":"Web-Availity", "IsReferral":"N", "CustomerGenkey":"8700004224713", "GroupId":"627426", "PlatformCode":"MTV", "CoverageSequenceNumber":"106", "EffectiveDate":"2012-01-01T00:00:00", "InpatientDetails":{ "AdmissionDate":"", "AdmissionType":"", "BedType":"", "DischargeDate":"", "TotalDaysApproved":"0", "LastCoveredDate":"" },'+
                            ' "OutpatientDetails":{ "FirstDay":"2014-11-20T00:00:00", "LastDay":"2014-12-20T00:00:00", "ServiceType":"Consultation with Treatment" }, "ProcedureDetails":{ "ProcedureDetail":[ { "Code":"11921", "Description":"Tattooing, intradermal introduction of insoluble opaque pigments to correct color defects of skin, including micropigmentation; 6.1 to 20.0 sq cm", "ServiceCode":"", "Status":"Pended", "Visit":{ "RequestedUnits":"1", "ApprovedUnits":"1", "TypeOfUnits":"Number of Visits" } } ] }, "DiagnosisDetails":{ "DiagnosisDetail":[ { "Type":"PrimaryDiagnosis", "Code":"N18.2", "Description":"Chronic kidney disease, stage 2 (mild)", "Status":"Pended" } ] },'+
                            ' "ProviderDetails":{ "ProviderDetail":[ { "Type":"TreatingProvider", "Id":"187710093124", "Name":"", "TaxId":"260353034", "ParticipatingStatus":"Par", "AddressDetails":{ "Address":[ { "AddressType":"Primary Service", "Street_1":"225 Abraham Flexner Way", "Street_2":"", "City":"Louisville", "County":"", "State":"KY", "ZipCode":"40202", "ZipCode_ext":"1846", "PhoneNumber":"502-561-4263" } ] } }, { "Type":"Facility", "Id":"187710093124", "Name":"Norton, Artis", "TaxId":"260353034", "ParticipatingStatus":"Par", "AddressDetails":{ "Address":[ '+
                            '{ "AddressType":"Primary Service", "Street_1":"225 Abraham Flexner Way", "Street_2":"", "City":"Louisville", "County":"", "State":"KY", "ZipCode":"40202", "ZipCode_ext":"1846", "PhoneNumber":"502-561-4263" } ] } }, { "Type":"RequestingProvider", "Id":"101290403525", "Name":"Ames, Frederick", "TaxId":"746001118", "ParticipatingStatus":"Par", "AddressDetails":{ "Address":[ { "AddressType":"Primary Service", "Street_1":"225 Abraham Flexner Way", "Street_2":"", "City":"Louisville", "County":"", "State":"KY", "ZipCode":"40202", "ZipCode_ext":"1846", "PhoneNumber":"502-561-4263" } ] } } ] }, '+
                            '"RequestType":"Pre-Auth", "StatusReason":"" } ] }, "MemberInfo":{ "FirstName":"JAMES", "LastName":"BREITLING", "MiddleName":"A", "DateOfBirth":"01/15/1951", "Gender":"M", "SubscriberId":"001482523", "DependentCode":"01", "ODS_MemberGenkey":"1005447107", "SdrPersonID":"1005447107" }, "TotalRecordsFound":"1" } }';
            oResponse.setBody(sJson);
            oResponse.setStatusCode(200); 
            oDetails.responsewrap(oResponse);
            system.assert(oDetails!=null); 
            system.assertEquals(oDetails.bServiceError,false);
            Test.stopTest();    
      }
    }
    
	/*
     * Method Name : callCommunicationService
     * Description : This method tests Communication Service
     * Return type : void
     * Paramater   : None
     */ 
    public static testmethod void callCommunicationServiceTest() {
		Test.setCurrentPage(Page.ClinicalAuthDetails_VF_HUM);
        User u = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
        Provider_functionality_switch__c oProvSwitch = new Provider_functionality_switch__c();
        oProvSwitch.Activate_New_Authorization_Service__c = false;
        insert oProvSwitch;
        system.runAs(u)
        {
            Test.startTest();
			Account acc = [SELECT Id, Enterprise_ID__c, Birthdate__c FROM Account LIMIT 1];
            ClinicalAuthDetails_C_HUM oClinical = new ClinicalAuthDetails_C_HUM ();
			if(acc != null) {
				oClinical.sEnterpriseID = acc.Enterprise_ID__c;
				oClinical.sDateOfBirth = acc.Birthdate__c;
			}
			oClinical.sCommunicationPageNumber = '20';
			oClinical.sAuthID = 'aldjl2312lkadja';
			
			Continuation conti = (Continuation)oClinical.callCommunicationService();
			// Verify that the continuation has the proper requests
			Map<String, HttpRequest> requests = conti.getRequests();
			system.assert(requests.size() == 1);
			system.assert(requests.get(oClinical.sRequest) != null);
			
			// Perform mock callout 
            HttpResponse oResponse = new HttpResponse();
            oResponse.setHeader('Content-Type', 'application/json');
            string sjson  = '{"CommunicationRecordsResponse":{"CommunicationRecordsList":{"TotalRecordsFound":"1","CommunicationRecords":[{"RecordType":"Test RT","CreatedDate":"2020-12-10","CreatedBy":"Test","LastModifiedBy":"Test","Department":"Test Dept","ContactMethod":"Phone","ContactType":"Dealer","ContactAttemptNo":"123123","ContactResult":["Test","Test2","Test3"],"ContactResultOther":"Test Contact Other","Notes":"Test","Details":"Test"}]}}}';
            oResponse.setBody(sjson);
            oResponse.setStatusCode(200);
			Test.setContinuationResponse(oClinical.sRequest, oResponse);
			// Invoke callback method
			Object result = Test.invokeContinuationMethod(oClinical, conti);			
            // result is the return value of the callback
			System.assertEquals(null, result);
            system.assertEquals(oClinical.bServiceError,false);
            Test.stopTest(); 
		}
    }
	
	/*
     * Method Name : callLettersService
     * Description : This method tests Letter Service
     * Return type : void
     * Paramater   : None
     */ 
    public static testmethod void callLettersServiceTest() {
		Test.setCurrentPage(Page.ClinicalAuthDetails_VF_HUM);
        User u = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
        Provider_functionality_switch__c oProvSwitch = new Provider_functionality_switch__c();
        oProvSwitch.Activate_New_Authorization_Service__c = false;
        insert oProvSwitch;
        system.runAs(u)
        {
            Test.startTest();
            ClinicalAuthDetails_C_HUM oClinical = new ClinicalAuthDetails_C_HUM ();
			Policy_Member__c pm = [SELECT Id FROM Policy_Member__c LIMIT 1];
			if(pm != null) {
				oClinical.sPolicyMemRecID = pm.Id;
			}
			oClinical.sAuthID = 'aldjl2312lkadja';
			oClinical.sLettersPageNumber = '20';
			oClinical.lstLettersRecords = new List<ClinicalAuthDetailsWrapper_DTO_HUM>();
			
			Continuation conti = (Continuation)oClinical.callLettersService();
			// Verify that the continuation has the proper requests
			Map<String, HttpRequest> requests = conti.getRequests();
			system.assert(requests.size() == 1);
			system.assert(requests.get(oClinical.sRequest) != null);
			
			// Perform mock callout 
            HttpResponse oResponse = new HttpResponse();
            oResponse.setHeader('Content-Type', 'application/json');
            string sjson  = '{"LettersResponse": {"LettersRecordsList":{"TotalRecordsFound" : "1", "LettersRecords":[{"LetterType":"Test", "AssociatedAuthID":"13123", "AssociatedCaseID":"12312", "CreatedDate": "2020-12-10", "CreatedBy": "Test", "TemplateName":"Test Template", "Status":"Test Status", "FullfillmentStatus":"Test F Status", "P2POfferedinLetter":"Test", "LetterName":"TestL Name", "VoidRemarks":"Test Void Remarks", "LastModifiedBy": "Test", "LastModifiedDate":"2020-10-15", "FilePath":"test/path"}]}}}';
            oResponse.setBody(sjson);
            oResponse.setStatusCode(200);
			Test.setContinuationResponse(oClinical.sRequest, oResponse);
			// Invoke callback method
			Object result = Test.invokeContinuationMethod(oClinical, conti);			
            // result is the return value of the callback
			System.assertEquals(null, result);
            system.assertEquals(oClinical.bServiceError,false);
            Test.stopTest(); 
		}
    }
	
    
    /*
     * Method Name : authSummaryInPatient
     * Description : This method tests Authorization Summary for InPatient
     * Return type : void
     * Paramater   : None
     */ 
    static testMethod void authSummaryInPatient() 
    {
        Test.setCurrentPage(Page.ClinicalAuthDetails_VF_HUM);
        User u = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
        Provider_functionality_switch__c oProvSwitch = new Provider_functionality_switch__c();
        oProvSwitch.Activate_New_Authorization_Service__c = false;
        insert oProvSwitch;
        system.runAs(u)
        {
            Test.startTest();
            ClinicalAuthDetails_C_HUM oClinical = new ClinicalAuthDetails_C_HUM ();
             oClinical.authDetailsRequest();
            HttpResponse oResponse = new HttpResponse();
            oResponse.setHeader('Content-Type', 'application/json');
            string sjson  = '{ "AuthorizationsResponse":{ "Authorizations":{ "Authorization":[ { "AuthID":"093020423", "AuthType":"Inpatient", "AuthStatus":"Pended", "UpdatedDate":"2014-11-24T00:51:20.413", "CreatedDate":"2014-11-24T00:51:20.413", "DateOfService":"", "ContactMethod":"Web-Availity", "IsReferral":"N", "CustomerGenkey":"8700004224713", "GroupId":"627426", "PlatformCode":"MTV", "CoverageSequenceNumber":"106", "EffectiveDate":"2012-01-01T00:00:00", "InpatientDetails":{ "AdmissionDate":"2014-11-24T00:00:00", "AdmissionType":"Inpt Non-ER", "BedType":"Acute Med-Surg", "DischargeDate":"", "TotalDaysApproved":"0", "LastCoveredDate":"0001-01-01T00:00:00" }, '+
                            '"OutpatientDetails":{ "FirstDay":"", "LastDay":"", "ServiceType":"" }, "ProcedureDetails":{ "ProcedureDetail":[ { "Code":"11000", "Description":"Debridement of extensive eczematous or infected skin; up to 10% of body surface", "ServiceCode":"", "Status":"Pended", "Visit":{ "RequestedUnits":"0", "ApprovedUnits":"0", "TypeOfUnits":"Number of Visits" } } ] }, "DiagnosisDetails":{ "DiagnosisDetail":[ { "Type":"PrimaryDiagnosis", "Code":"250.00", "Description":"DIAB W/O COMP TYPE II/UNS NOT STATED UNCNTRL", "Status":"Pended" } ] }, "ProviderDetails":{ "ProviderDetail":[ { "Type":"RequestingProvider", "Id":"105303371420", "Name":"Willbur, Kelly", "TaxId":"351951862", "ParticipatingStatus":"Par",'+
                            ' "AddressDetails":{ "Address":[ { "AddressType":"Primary Billing", "Street_1":"2305 GREEN VALLEY RD", "Street_2":"", "City":"New Albany", "County":"", "State":"", "ZipCode":"", "ZipCode_ext":"", "PhoneNumber":"" } ] } }, { "Type":"TreatingProvider", "Id":"ADMINPRO", "Name":"NASSIM AND ASSOCIATES, NASSIM", "TaxId":"111222333", "ParticipatingStatus":"Non-Par", "AddressDetails":{ "Address":[ { "AddressType":"Primary Billing", "Street_1":"2305 GREEN VALLEY RD", "Street_2":"", "City":"New Albany", "County":"", "State":"", "ZipCode":"", "ZipCode_ext":"", "PhoneNumber":"" } ] } }, { "Type":"Facility", "Id":"ADMINPRO", "Name":"NORTON HOSPITAL", "TaxId":"111222333", "ParticipatingStatus":"Non-Par", '+
                            '"AddressDetails":{ "Address":[ { "AddressType":"Primary Billing", "Street_1":"2305 GREEN VALLEY RD", "Street_2":"", "City":"New Albany", "County":"", "State":"", "ZipCode":"", "ZipCode_ext":"", "PhoneNumber":"" } ] } } ] }, "RequestType":"Concurrent", "StatusReason":"" } ] }, "MemberInfo":{ "FirstName":"JAMES", "LastName":"BREITLING", "MiddleName":"A", "DateOfBirth":"01/15/1951", "Gender":"M", "SubscriberId":"001482523", "DependentCode":"01", "ODS_MemberGenkey":"1005447107", "SdrPersonID":"1005447107" }, "TotalRecordsFound":"1" }, '+
							'"CommunicationRecordsResponse":{"TotalRecordsFound":"1","CommunicationRecords":[{"RecordType":"Test RT","CreatedDate":"2020-12-10","CreatedBy":"Test","LastModifiedBy":"Test","Department":"Test Dept","ContactMethod":"Phone","ContactType":"Dealer","ContactAttemptNo":"123123","ContactResult":["Test","Test2","Test3"],"ContactResultOther":"Test Contact Other","Notes":"Test","Details":"Test"}]},'+
							'"LettersResponse":{"TotalRecordsFound":"1","LettersRecords":[{"LetterType":"Test","AssociatedAuthID":"13123","AssociatedCaseID":"12312","CreatedDate":"2020-12-10","CreatedBy":"Test","TemplateName":"Test Template","Status":"Test Status","FullfillmentStatus":"Test F Status","P2POfferedinLetter":"Test","LetterName":"TestL Name","VoidRemarks":"Test Void Remarks","LastModifiedBy":"Test","LastModifiedDate":"2020-10-15","FilePath":"test/path"}]}'+
							'}';
            oResponse.setBody(sjson);
            oResponse.setStatusCode(200); 
            oClinical.responsewrap(oResponse); 
            system.assertEquals(oClinical.bServiceError,false);
            Test.stopTest();
               
      }
    }
	    
     /*
     * Method Name : authSummaryException
     * Description : This method tests the Exception handling 
     * Return type : void
     * Parameter   : None
     */ 
    static testMethod void authSummaryException() 
    {
        Test.setCurrentPage(Page.ClinicalAuthDetails_VF_HUM);
        User u = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
        system.runAs(u)
        {
            Test.startTest();
            ClinicalAuthDetails_C_HUM oClinical = new ClinicalAuthDetails_C_HUM ();
            HttpResponse oResponse = new HttpResponse();
            oResponse.setHeader('Content-Type', 'application/json');
            string sjson  = 'test json';
            oResponse.setBody(sjson);
            oResponse.setStatusCode(200); 
            oClinical.responsewrap(oResponse); 
            system.assertEquals(oClinical.bServiceError,true);
      }
    }

    static testMethod void authSummaryOutPatientNewService() 
    {
        Test.setCurrentPage(Page.ClinicalAuthDetails_VF_HUM);
        Policy_Member__c oPolMember = [Select id from Policy_Member__c where Name = 'Policy Member PM1' limit 1];
        ApexPages.currentPage().getParameters().put('RecId',oPolMember.Id);
        system.assertEquals(ApexPages.currentPage().getParameters().get('RecId'),oPolMember.Id);
        ApexPages.currentPage().getParameters().put('AuthId','093032249');
        system.assertEquals(ApexPages.currentPage().getParameters().get('RecId'),oPolMember.Id);
        Provider_functionality_switch__c oProvSwitch = new Provider_functionality_switch__c();
        oProvSwitch.Activate_New_Authorization_Service__c = true;
        insert oProvSwitch;
        User oUser = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
        system.runAs(oUser)
        {
            Test.startTest();
            ClinicalAuthDetails_C_HUM oDetails = new ClinicalAuthDetails_C_HUM ();
            oDetails.authDetailsRequest();
            oDetails.processResponse();   
            HttpResponse oResponse = new HttpResponse();
            oResponse.setHeader('Content-Type', 'application/json');
            string sJson  = '{ "AuthSummaryResponse":{ "AuthSummaryList":{ "AuthSummary":[ { "AuthID":"093055783", "AuthType":"Inpatient", "AuthStatus":"Denied", "CustomerGenkey":"", "FacilityProviderName":"Norton", "GroupId":"CSKY", "RequestingProviderName":"Norton", "LastModifiedDateTime":"2019-10-11T11:07:30.11", "TreatingProviderName":"Norton", "InpatientDetails":{ "AdmissionDate":"2019-10-11T00:00:00", "AdmissionType":"Inpt Non-ER", "BedType":"", "DischargeDate":"2019-10-11T00:00:00", "TotalDaysApproved":"0", "LastCoveredDate":"" }, "OutpatientDetails":{ "FirstDay":"0001-01-01T00:00:00", "LastDay":"0001-01-01T00:00:00", "ServiceType":"" } } ] }, "TotalRecordsFound":"1" } }';
            oResponse.setBody(sJson);
            oResponse.setStatusCode(200); 
            oDetails.responsewrap(oResponse);
            system.assert(oDetails!=null);           
            Test.stopTest();    
      }
    }
}