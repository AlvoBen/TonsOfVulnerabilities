/**************************************************************************** 
Apex Class Name  : ClinicalAuthDetails_LT_HUM
Version          : 1.0  
Created Date     : 07/12/2022
Function         : Test class for ClinicalAuthDetailsRequest_LH_HUM
				   ClinicalAuthDetailsResponse_LH_HUM  
				   ClinicalAuthDetailsWrapper_LDTO_HUM   
				   ClinicalAuthDetails_LC_HUM  
				   ClinicalAuthDetails_LS_HUM  
				   
****************************************************************************
Modification Log:
* Developer Name                                  Date                       Description
*---------------------------------------------------------------------------------------------------
* Dimple Sharma									  10/23/2023				 US- 5266368 Code coverage Improvements
***************************************************************************************************/
@isTest
public class ClinicalAuthDetails_LT_HUM {
    
     /*
    * Method Name : prepareTestData
    * Description : Used to create wrapper class data
    * Return type : void
    * Paramater   : NA
    */
    @testSetup static void prepareTestData() 
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertEquals(oCCSUser.LastName,'test User1');
        
        List<HUM_Webservice_Callout__c> callouts = new List<HUM_Webservice_Callout__c>();
        HUM_Webservice_Callout__c clinicalAuthDetailV2 = new HUM_Webservice_Callout__c(Name ='Clinical_AuthDetail_V2',Certificate_Name__c = 'Humana_GuidanceCare_Pharmacy' ,Active__c = true,End_Point_URL__c ='https://dev-crmservicews.humana.com/test' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
        callouts.add(clinicalAuthDetailV2);
        HUM_Webservice_Callout__c authDetailsLetters = new HUM_Webservice_Callout__c(Name ='AuthDetails_Letters',Certificate_Name__c = 'Humana_GuidanceCare_Pharmacy' ,Active__c = true,End_Point_URL__c ='https://dev-crmservicews.humana.com/test2' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
        callouts.add(authDetailsLetters);
        HUM_Webservice_Callout__c authDetailsCommunicationRecords = new HUM_Webservice_Callout__c(Name ='AuthDetails_Communication_Records',Certificate_Name__c = 'Humana_GuidanceCare_Pharmacy' ,Active__c = true,End_Point_URL__c ='https://dev-crmservicews.humana.com/test3' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
        callouts.add(authDetailsCommunicationRecords);
        HUM_Webservice_Callout__c exceptionTest = new HUM_Webservice_Callout__c(Name ='Exception',Certificate_Name__c = 'exception' ,Active__c = true,End_Point_URL__c ='https://dev-crmservicews.humana.com/test3' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
        callouts.add(exceptionTest);
        insert callouts;
        
        System.runAs (oCCSUser) 
        {
            Account objMemAccount = HUMTestDataHelperTest.getAccount();
            objMemAccount.FirstName = 'Test FName';
            objMemAccount.LastName = 'LName'; 
            objMemAccount.source_platform_code__c = 'EM' ;
            objMemAccount.Enterprise_Id__c = String.valueof(2000);
            objMemAccount.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            objMemAccount.Birthdate__c = String.valueOf(System.today().month()+'/'+system.today().day()+'/'+System.today().year());
            objMemAccount.Home_Office_Account__c = true;
            insert objMemAccount;
            system.assertEquals(objMemAccount.LastName == 'LName', true);
            
            Product2 prod = new Product2(Name='MED');
            insert prod;
            
            PurchaserPlan oMedicalPolicy = new PurchaserPlan();
            oMedicalPolicy.Policy_Group_Number__c='11111';
            oMedicalPolicy.Name = 'Medical Product';
            oMedicalPolicy.source_cust_cov_key__c = '12345678901234567890' ; 
            oMedicalPolicy.PayerId = objMemAccount.Id;
            oMedicalPolicy.Product__c = prod.Id;
            oMedicalPolicy.Major_LOB__c = 'HSA';
            oMedicalPolicy.Effectivefrom  = Date.parse(Date.today().addDays(-400).format());     
            insert oMedicalPolicy;
            
            Policy__c oMedicalPolicy1 = HUMTestDataHelperTest.getPolicy();
            oMedicalPolicy1.Name = 'Medical Product';
            oMedicalPolicy1.source_cust_cov_key__c = '12345678901234567890' ; 
            oMedicalPolicy1.Group_Name__c = objMemAccount.Id;
            oMedicalPolicy1.Major_LOB__c ='EPO';   
            oMedicalPolicy1.Business_Segment__c  = 'Individual-Medicare';
            oMedicalPolicy1.Coverage_Plan_Effective_Date__c  = '02/02/2018';     
            insert oMedicalPolicy1;
            system.assertEquals(oMedicalPolicy1.Name == 'Medical Product', true);
            
            // Policy Member
            MemberPlan policymember = New MemberPlan();
            policymember.RelationshipToSubscriber = 'Self';
            policymember.EffectiveFrom=Date.parse(Date.today().addDays(-1).format());
            policymember.EffectiveTo=Date.parse(Date.today().addDays(1).format());
            policymember.Memberid = objMemAccount.Id;
            policymember.ETL_Record_Deleted__c = false;
            policymember.Policy__c = oMedicalPolicy1.Id;
            policymember.PlanId = oMedicalPolicy.Id;
            policymember.RelationshipToSubscriber = 'self';  
            policymember.Name = 'Test User 1#';       
            policymember.Member_Plan_External_Id__c = '00|ODS|123456789|89|89|02-01-2001';
            insert policymember;
            
        }
    }
    
    static testMethod void authDetailsRequestTest(){
        User testUser = [Select Id, Name from User where LastName ='test User1'];
        Id memPlanId = [SELECT Id FROM MemberPlan LIMIT 1].Id;
        Test.setMock(HttpCalloutMock.class, new ClinicalAuthMock());
        System.runAs(testUser)
        {
            Test.startTest();
            Continuation continueObj = (Continuation)ClinicalAuthDetails_LC_HUM.authDetailsRequest('9908909099', memPlanId);
            
            HttpResponse httpRes = new Httpresponse ();
            String resBodyString = '{"AuthorizationsResponse":{"Authorizations":{"Authorization":[{"AuthID":"9098980","AuthType":"test","AuthStatus":"Success","UpdatedDate":"01-10-2023","CreatedDate":"01-05-2023","DateOfService":"01-09-2023","RequestType":"test","ContactMethod":"test","IsReferral":"true","StatusReason":"test","CustomerGenkey":"089899","GroupId":"985678","PlatformCode":"98988","CoverageSequenceNumber":"1","EffectiveDate":"01/09/2023","DenialReason":"test","ClaimPaymentNotes":"test","NextReviewDate":"01-11-2023","InpatientDetails":{"AdmissionDate":"01-10-2023","DischargeDate":"10-10-2023","BedType":"test","AdmissionType":"test","TotalDaysApproved":"10","LastCoveredDate":"01-06-2023"},"OutpatientDetails":{"FirstDay":"01-10-2023","LastDay":"10-10-2023","ServiceType":"test","TotalDaysApproved":"10"},"ProcedureDetails":{"ProcedureDetail":[{"Code":"123","Description":"test","Status":"test","ServiceCode":"6776","Visit":{"RequestedUnits":"10","ApprovedUnits":"10","TypeOfUnits":"Bed"}}]},"DiagnosisDetails":{"DiagnosisDetail":[{"Type":"Bed","Code":"123","Description":"test","Status":"test"}]},"ProviderDetails":{"ProviderDetail":[{"Type":"Bed","Id":"6776","Name":"test","TaxId":"6758","ParticipatingStatus":"tets","AddressDetails":{"AddressType":"home","Street_1":"test","Street_2":"test2","County":"USA","City":"LA","State":"test","ZipCode":"76768","ZipCode_ext":"090","PhoneNumber":"7878787978"}}]}}]},"MemberInfo":{},"TotalRecordsFound":"1"},"CommunicationRecordsResponse":{"CommunicationRecords":[{"RecordType":"test","CreatedDate":"2023-10-01T15:20:45","CreatedBy":"test","LastModifiedDate":"2023-10-01T15:20:45","LastModifiedDateFormated":"10/01/2023 9:15 AM","LastModifiedBy":"test","Department":"OPD","CommunicationType":"test","ContactMethod":"test","ContactType":"test","ContactAttemptNo":"96","ContactResultOther":"other","Notes":"test","Details":"testyu"}],"TotalRecordsFound":"1"},"LettersResponse":{"LettersRecords":[{"LetterType":"test","AssociatedAuthID":"test","AssociatedCaseID":"test","CreatedBy":"test","CreatedDate":"2023-10-01T15:20:45","TemplateName":"test","Status":"test","FullfillmentStatus":"testt","P2POfferedinLetter":"rtett","LetterName":"test","VoidRemarks":"testt","LastModifiedBy":"test","LastModifiedDate":"2023-10-01T15:20:45","LastModifiedDateFormated":"10/01/2023 9:15 AM","FilePath":"test"}],"TotalRecordsFound":"1"}}';
            //String resBodyString= '{"AuthorizationsResponse":{"Authorizations":{"Authorization":[{"AuthID":"9098980","AuthType":"test","AuthStatus":"Success","UpdatedDate":"01-10-2023","CreatedDate":"01-05-2023","DateOfService":"01-09-2023","RequestType":"test","ContactMethod":"test","IsReferral":"true","StatusReason":"test","CustomerGenkey":"089899","GroupId":"985678","PlatformCode":"98988","CoverageSequenceNumber":"1","EffectiveDate":"01/09/2023","DenialReason":"test","ClaimPaymentNotes":"test","NextReviewDate":"01-11-2023","InpatientDetails":{"AdmissionDate":"01-10-2023","DischargeDate":"10-10-2023","BedType":"test","AdmissionType":"test","TotalDaysApproved":"10","LastCoveredDate":"01-06-2023"},"OutpatientDetails":{"FirstDay":"01-10-2023","LastDay":"10-10-2023","ServiceType":"test","TotalDaysApproved":"10"},"ProcedureDetails":{"ProcedureDetail":[{"Code":"123","Description":"test","Status":"test","ServiceCode":"6776","Visit":{"RequestedUnits":"10","ApprovedUnits":"10","TypeOfUnits":"Bed"}}]},"DiagnosisDetails":{"DiagnosisDetail":[{"Type":"Bed","Code":"123","Description":"test","Status":"test"}]},"ProviderDetails":{"ProviderDetail":[{"Type":"Bed","Id":"6776","Name":"test","TaxId":"6758","ParticipatingStatus":"tets","AddressDetails":{"AddressType":"home","Street_1":"test","Street_2":"test2","County":"USA","City":"LA","State":"test","ZipCode":"76768","ZipCode_ext":"090","PhoneNumber":"7878787978"}}]}}]},"MemberInfo":{},"TotalRecordsFound":"1"},"CommunicationRecordsResponse":{"RecordType":"test","CreatedDate":"01-05-2023","CreatedBy":"test","LastModifiedDate":"2023-10-01 13:15:44","LastModifiedDateFormated":"10/01/2023 9:15 AM","LastModifiedBy":"test","Department":"OPD","CommunicationType":"test","ContactMethod":"test","ContactType":"test","ContactAttemptNo":"96","ContactResultOther":"other","Notes":"test","Details":"testyu"},"LettersResponse":{"LetterType":"test","AssociatedAuthID":"test","AssociatedCaseID":"test","CreatedBy":"test","CreatedDate":"2023-05-01 13:15:44","TemplateName":"test","Status":"test","FullfillmentStatus":"testt","P2POfferedinLetter":"rtett","LetterName":"test","VoidRemarks":"testt","LastModifiedBy":"test","LastModifiedDate":"2023-10-01 13:15:44","LastModifiedDateFormated":"10/01/2023 9:15 AM","FilePath":"test"}}';
            httpRes.setBody(resBodyString);
            system.assert(httpRes.getBody() == resBodyString);
            string reqType = 'Continuation-1';
            List<string>labelGetmember = new List<string>();
            labelGetmember.add(reqType); 
            Test.setContinuationResponse(reqType,httpRes);
            
            ClinicalAuthDetails_LC_HUM.processResponseLWC(labelGetmember,continueObj);
            
            Test.stopTest();
        }
    }
    
    
    static testMethod void callLettersServiceTest(){
        User testUser = [Select Id, Name from User where LastName ='test User1'];
        Id memPlanId = [SELECT Id FROM MemberPlan LIMIT 1].Id;
        Test.setMock(HttpCalloutMock.class, new ClinicalAuthMock());
        System.runAs(testUser)
        {
             Test.startTest();
            Continuation continueObj = (Continuation)ClinicalAuthDetails_LC_HUM.callLettersService('9908909099', memPlanId, '1');
            
            HttpResponse httpRes = new Httpresponse ();
            String resBodyString = '{"LettersResponse":{"LettersRecordsList":{"LettersRecords":[{"LetterType":"test","AssociatedAuthID":"2345","AssociatedCaseID":"57567","CreatedBy":"test","CreatedDate":"2023-10-01T15:20:45","TemplateName":"test","Status":"tewst","FullfillmentStatus":"test","P2POfferedinLetter":"test","LetterName":"test","VoidRemarks":"tets","LastModifiedBy":"test","LastModifiedDate":"2023-10-01T15:20:45","FilePath":"test","LastModifiedDateFormated":"10/01/2023 9:15 AM"}],"TotalRecordsFound":"1"}}}';
            httpRes.setBody(resBodyString);
            system.assert(httpRes.getBody() == resBodyString);
            string reqType = 'Continuation-1';
            List<string>labelGetmember = new List<string>();
            labelGetmember.add(reqType); 
            Test.setContinuationResponse(reqType,httpRes);
            
            ClinicalAuthDetails_LC_HUM.processLettersResponse(labelGetmember,continueObj);
            
            Test.stopTest();
        }
    }
    
    static testMethod void callCommunicationServiceTest(){
        User testUser = [Select Id, Name from User where LastName ='test User1'];
        Id memPlanId = [SELECT Id FROM MemberPlan LIMIT 1].Id;
        Test.setMock(HttpCalloutMock.class, new ClinicalAuthMock());
        System.runAs(testUser)
        {
             Test.startTest();
            Continuation continueObj = (Continuation)ClinicalAuthDetails_LC_HUM.callCommunicationService('9908909099', memPlanId, '1');
            
            HttpResponse httpRes = new Httpresponse ();
            String resBodyString = '{"CommunicationRecordsResponse":{"CommunicationRecordsList":{"CommunicationRecords":[{"RecordType":"test","CreatedBy":"test","CreatedDate":"2023-10-01T15:20:45","Department":"test","CommunicationType":"test","ContactMethod":"tewst","ContactType":"test","ContactAttemptNo":"345","ContactResultOther":"test","Notes":"tets","LastModifiedBy":"test","LastModifiedDate":"2023-10-01T15:20:45","Details":"test","LastModifiedDateFormated":"10/01/2023 9:15 AM"}],"TotalRecordsFound":"1"}}}';
            httpRes.setBody(resBodyString);
            system.assert(httpRes.getBody() == resBodyString);
            string reqType = 'Continuation-1';
            List<string>labelGetmember = new List<string>();
            labelGetmember.add(reqType); 
            Test.setContinuationResponse(reqType,httpRes);
            
            ClinicalAuthDetails_LC_HUM.processCommunicationResponse(labelGetmember,continueObj);
            
            Test.stopTest();
        }
    }
    
    static testMethod void setupGoButtonRequestTest(){
        User testUser = [Select Id, Name from User where LastName ='test User1'];
        HUM_Webservice_Callout__c callout = [SELECT Name, Content_Type__c,Certificate_Name__c,End_Point_URL__c,Timeout__c, Request_Type__c 
                                             FROM HUM_Webservice_Callout__c WHERE Name!='Exception' LIMIT 1];
        System.runAs(testUser)
        {
            Test.startTest();
            HttpRequest req = new ClinicalAuthDetails_LS_HUM().setupGoButtonRequest(new HttpRequest(), callout, '1234');
            Test.stopTest();
            
        }
        Assert.areEqual('test User1', testUser.Name);
    }
    
    static testMethod void setupGoButtonRequestExceptionTest(){
        User testUser = [Select Id, Name from User where LastName ='test User1'];
        HUM_Webservice_Callout__c callout = [SELECT Name, Content_Type__c,Certificate_Name__c,End_Point_URL__c,Timeout__c, Request_Type__c 
                                             FROM HUM_Webservice_Callout__c WHERE Name='Exception' LIMIT 1];
        System.runAs(testUser)
        {
            Test.startTest();
            new ClinicalAuthDetails_LS_HUM().setupGoButtonRequest(new HttpRequest(), callout, '1234');
            new ClinicalAuthDetails_LS_HUM().setupGoButtonRequest(new HttpRequest(), callout, '');
            Test.stopTest();
        }
        Assert.areEqual('test User1', testUser.Name);

    }
    
    // uncomment this method to cover "buildWrapperList" method when it is testvisible
    /*static testMethod void buildWrapperListTest(){
        User testUser = [Select Id, Name from User where LastName ='test User1'];
        String authSummary = '{"AuthSummaryResponse":{"AuthSummaryList":{"AuthSummary":[{"AuthID":"test","AuthType":"Inpatient","AuthStatus":"2023-10-01T15:20:45","CustomerGenkey":"test","FacilityProviderName":"test","RequestingProviderName":"tewst","TreatingProviderName":"test","LastModifiedDateTime":"2023-10-01T15:20:45","InpatientDetails":{"AdmissionDate":"2023-10-01","AdmissionType":"bed","BedType":"test","DischargeDate":"2023-11-02","TotalDaysApproved":"10","LastCoveredDate":"2023-11-02"},"OutpatientDetails":{"FirstDay":"2023-10-01","LastDay":"2023-11-02","ServiceType":"bed"}},{"AuthID":"test2","AuthType":"Outpatient","AuthStatus":"2023-10-01T15:20:45","CustomerGenkey":"test","FacilityProviderName":"test","RequestingProviderName":"tewst","TreatingProviderName":"test","LastModifiedDateTime":"2023-10-01T15:20:45","InpatientDetails":{"AdmissionDate":"2023-10-01","AdmissionType":"bed","BedType":"test","DischargeDate":"2023-11-02","TotalDaysApproved":"10","LastCoveredDate":"2023-11-02"},"OutpatientDetails":{"FirstDay":"2023-10-01","LastDay":"2023-11-02","ServiceType":"bed"}}],"TotalRecordsFound":"1"}}}';
        ClinicalAuthSummaryResponse_H_HUM authSumResp = (ClinicalAuthSummaryResponse_H_HUM) JSON.deserialize(authSummary, ClinicalAuthSummaryResponse_H_HUM.class);
        List<ClinicalAuthSummaryResponse_H_HUM.AuthSummary> lstauth =  authSumResp.AuthSummaryResponse.AuthSummaryList.AuthSummary;
        System.runAs(testUser)
        {
            Test.startTest();
            new ClinicalAuthDetails_LS_HUM().buildWrapperList(lstauth);
            Test.stopTest();
        }
    }*/
    
    static testMethod void updateUrlTest(){
        User testUser = [Select Id, Name from User where LastName ='test User1'];
        AuthRefferal_Wrapper_LDTO_HUM authRef;
        System.runAs(testUser)
        {
            Test.startTest();
            String authRefJSON = '{"sAuthorizationOrReferralNumber":"Auth123","sAuthorizationType":"Inpatient","sOverallStatus":"Approved","sAdmFirstDay":"2023-01-01","sDischargeLastDay":"2023-01-10","sServiceType":"InPatient","sTreatingProvider":"Dr. Smith","sFacility":"Hospital ABC","sRequestingrovider":"Dr. Johnson","sGroupId":"Group123","bIsAccessible":true,"Totalcount":1,"recId":"Record123","sAtuhRefUrl":"/auth/details/Auth123"}';
            AuthRefferal_Wrapper_LDTO_HUM authDet = (AuthRefferal_Wrapper_LDTO_HUM) JSON.deserialize(authRefJSON,AuthRefferal_Wrapper_LDTO_HUM.class);
            authRef = new ClinicalAuthDetails_LS_HUM().updateUrl(authDet);
            authDet.sAuthorizationType = 'Outpatient';
            new ClinicalAuthDetails_LS_HUM().updateUrl(authDet);
            Test.stopTest();
        }
        Assert.areEqual('Group123', authRef.sGroupId);
    }
    
    static testMethod void responseWrapLettersRecsTest(){
        User testUser = [Select Id, Name from User where LastName ='test User1'];
        List<ClinicalAuthDetailsWrapper_LDTO_HUM> clAuthDetWrap;
        String clinicalAuthJSON = '{"sAuthID":"123456","sAuthType":"Pre-authorization","sAuthStatus":"Approved","sUpdatedDate":"2023-01-15T10:30:00Z","sCreatedDate":"2023-01-10T08:45:00Z","sDateOfService":"2023-02-01","sContactMethod":"Phone","sIsReferral":"true","sStatusReason":"Medical Necessity","sCustomerGenkey":"ABC123","sGroupId":"Group123","sPlatformCode":"PlatformA","sCoverageSequenceNumber":"CovSeq456","sEffectiveDate":"2023-02-01","sRequestType":"InPatient","sDenialReason":"","sClaimsPaymentNotes":"Payment received","sNextReviewDate":"2023-03-01","sAdmissionDate":"2023-02-01","sDischargeDate":"2023-02-10","sBedType":"Single","sAdmissionType":"Emergency","sTotalDaysApproved":"10","sLastCoveredDate":"2023-02-10","sServiceType":"","sOPRequestType":"","sFirstDay":"","sLastDay":"","iTotalDays":0,"sDiagnosisType":"Primary","sDiagnosisCode":"ICD-10 Code","sDiagnosisDescription":"Description of the diagnosis","sDiagnosisStatus":"Active","sProcedureCode":"Procedure123","sProcedureDescription":"Description of the procedure","sProcedureStatus":"Completed","sProcedureServiceCode":"","sProcedureRequestingUnits":"","sProcedureAuthorizedUnits":"","sProcedureTypeofUnits":"","sReqProviderCity":"CityA","sReqProviderState":"StateA","sReqProviderPhnNum":"123-456-7890","sReqProviderStreet":"StreetA","sReqProviderZipCode":"12345","sReqProviderID":"ProviderID123","sReqProviderName":"Provider Name","sReqProviderTaxID":"TaxID456","sReqProviderPartStatus":"Active","sReqProviderType":"Hospital","sRecordType":"Authorization","sCommCreatedDate":"2023-01-15T11:45:00Z","sCommCreatedBy":"UserA","sCommLastModifiedDate":"2023-01-15T14:30:00Z","sCommLastModifiedBy":"UserB","sDepartment":"Billing","sCommunicationType":"Outbound","sCommContactMethod":"Email","sContactType":"Phone","sContactAttemptNo":"1","sContactResult":"Successful","lstContactResult":["Successful","Unsuccessful"],"sContactResultOther":"Other result","sNotes":"Additional notes","sDetails":"Additional details","sLettersType":"Medical","sLettersAssociatedAuthID":"123456","sLettersAssociatedCaseID":"Case789","sLettersCreatedBy":"UserC","sLettersCreatedDate":"2023-01-20T09:00:00Z","sLettersTemplateName":"TemplateX","sLettersStatus":"Draft","sLettersFullfillmentStatus":"Not Fulfilled","sLettersP2POfferedinLetter":"Yes","sLettersLetterName":"LetterA","sLettersVoidRemarks":"","sLettersLastModifiedBy":"UserD","sLettersLastModifiedDate":"2023-01-21T15:30:00Z","sFilePath":"/documents/letterA.pdf","bCheck":true}';
        String letterJSON = '{"LettersResponse":{"LettersRecordsList":{"LettersRecords":[{"LetterType":"Medical","AssociatedAuthID":"Auth123","AssociatedCaseID":"Case456","CreatedBy":"UserA","CreatedDate":"2023-10-15T08:30:00Z","TemplateName":"TemplateX","Status":"Draft","FullfillmentStatus":"Not Fulfilled","P2POfferedinLetter":"Yes","LetterName":"LetterA","VoidRemarks":"Voided for specific reason","LastModifiedBy":"UserB","LastModifiedDate":"2023-10-16T14:45:00Z","FilePath":"/documents/LetterA.pdf","LastModifiedDateFormated":"Oct 16, 2023 14:45:00"},{"LetterType":"Legal","AssociatedAuthID":"Auth456","AssociatedCaseID":"Case789","CreatedBy":"UserC","CreatedDate":"2023-10-18T09:15:00Z","TemplateName":"TemplateY","Status":"Sent","FullfillmentStatus":"Fulfilled","P2POfferedinLetter":"No","LetterName":"LetterB","VoidRemarks":"","LastModifiedBy":"UserD","LastModifiedDate":"2023-10-20T11:30:00Z","FilePath":"/documents/LetterB.pdf","LastModifiedDateFormated":"Oct 20, 2023 11:30:00"}],"TotalRecordsFound":2}}}';
        
        ClinicalAuthDetailsWrapper_LDTO_HUM clinicalAuth = (ClinicalAuthDetailsWrapper_LDTO_HUM) JSON.deserialize(clinicalAuthJSON,ClinicalAuthDetailsWrapper_LDTO_HUM.class);
        
        HttpResponse res = new HttpResponse();
        res.setHeader('Content-Type', 'application/json');
        res.setBody(letterJSON);
        res.setStatusCode(200);
        
        System.runAs(testUser)
        {
            Test.startTest();
            clAuthDetWrap = ClinicalAuthDetails_LS_HUM.responseWrapLettersRecs(res, new List<ClinicalAuthDetailsWrapper_LDTO_HUM>{clinicalAuth});
            Test.stopTest();
        }
        Assert.areEqual('Email', clAuthDetWrap[0].sCommContactMethod);
    }
    
    static testMethod void retrieveProcedureCodesOutPatientTest(){
        User testUser = [Select Id, Name from User where LastName ='test User1'];
        List<ClinicalAuthDetailsWrapper_LDTO_HUM> clAuthDetWrap;
        System.runAs(testUser)
        {
            Test.startTest();
            String strjson='{"AuthID":"9098980","AuthType":"test","AuthStatus":"Success","UpdatedDate":"01-10-2023","CreatedDate":"01-05-2023","DateOfService":"01-09-2023","RequestType":"test","ContactMethod":"test","IsReferral":"true","StatusReason":"test","CustomerGenkey":"089899","GroupId":"985678","PlatformCode":"98988","CoverageSequenceNumber":"1","EffectiveDate":"01/09/2023","DenialReason":"test","ClaimPaymentNotes":"test","NextReviewDate":"01-11-2023","InpatientDetails":{"AdmissionDate":"01-10-2023","DischargeDate":"10-10-2023","BedType":"test","AdmissionType":"test","TotalDaysApproved":"10","LastCoveredDate":"01-06-2023"},"OutpatientDetails":{"FirstDay":"01-10-2023","LastDay":"10-10-2023","ServiceType":"test","TotalDaysApproved":"10"},"ProcedureDetails":{"ProcedureDetail":[{"Code":"123","Description":"test","Status":"test","ServiceCode":"6776","Visit":{"RequestedUnits":"10","ApprovedUnits":"10","TypeOfUnits":"Number Of Visits"}}]},"DiagnosisDetails":{"DiagnosisDetail":[{"Type":"Bed","Code":"123","Description":"test","Status":"test"}]},"ProviderDetails":{"ProviderDetail":[{"Type":"Bed","Id":"6776","Name":"test","TaxId":"6758","ParticipatingStatus":"tets","AddressDetails":{"AddressType":"home","Street_1":"test","Street_2":"test2","County":"USA","City":"LA","State":"test","ZipCode":"76768","ZipCode_ext":"090","PhoneNumber":"7878787978"}}]}}';
            clAuthDetWrap = ClinicalAuthDetails_LS_HUM.retrieveProcedureCodesOutPatient(strjson);
            Test.stopTest();
        }
        Assert.areEqual(null, clAuthDetWrap[0].iTotalDays);
    }
    
     static testMethod void retrieveProcedureCodesTest(){
        User testUser = [Select Id, Name from User where LastName ='test User1'];
         List<ClinicalAuthDetailsWrapper_LDTO_HUM> clAuthDetWrap;
        System.runAs(testUser)
        {
            Test.startTest();
            String strjson='{"AuthID":"9098980","AuthType":"test","AuthStatus":"Success","UpdatedDate":"01-10-2023","CreatedDate":"01-05-2023","DateOfService":"01-09-2023","RequestType":"test","ContactMethod":"test","IsReferral":"true","StatusReason":"test","CustomerGenkey":"089899","GroupId":"985678","PlatformCode":"98988","CoverageSequenceNumber":"1","EffectiveDate":"01/09/2023","DenialReason":"test","ClaimPaymentNotes":"test","NextReviewDate":"01-11-2023","InpatientDetails":{"AdmissionDate":"01-10-2023","DischargeDate":"10-10-2023","BedType":"test","AdmissionType":"test","TotalDaysApproved":"10","LastCoveredDate":"01-06-2023"},"OutpatientDetails":{"FirstDay":"01-10-2023","LastDay":"10-10-2023","ServiceType":"test","TotalDaysApproved":"10"},"ProcedureDetails":{"ProcedureDetail":[{"Code":"123","Description":"test","Status":"test","ServiceCode":"6776","Visit":{"RequestedUnits":"10","ApprovedUnits":"10","TypeOfUnits":"Bed"}}]},"DiagnosisDetails":{"DiagnosisDetail":[{"Type":"Bed","Code":"123","Description":"test","Status":"test"}]},"ProviderDetails":{"ProviderDetail":[{"Type":"Bed","Id":"6776","Name":"test","TaxId":"6758","ParticipatingStatus":"tets","AddressDetails":{"AddressType":"home","Street_1":"test","Street_2":"test2","County":"USA","City":"LA","State":"test","ZipCode":"76768","ZipCode_ext":"090","PhoneNumber":"7878787978"}}]}}';
            clAuthDetWrap = ClinicalAuthDetails_LS_HUM.retrieveProcedureCodes(strjson);
            Test.stopTest();
        }
         Assert.areEqual(null, clAuthDetWrap[0].iTotalDays);
    }
    
    static testMethod void retrieveDiagnosisCodesTest(){
        User testUser = [Select Id, Name from User where LastName ='test User1'];
        List<ClinicalAuthDetailsWrapper_LDTO_HUM> clAuthDetWrap;
        System.runAs(testUser)
        {
            Test.startTest();
            String strjson='{"AuthID":"9098980","AuthType":"test","AuthStatus":"Success","UpdatedDate":"01-10-2023","CreatedDate":"01-05-2023","DateOfService":"01-09-2023","RequestType":"test","ContactMethod":"test","IsReferral":"true","StatusReason":"test","CustomerGenkey":"089899","GroupId":"985678","PlatformCode":"98988","CoverageSequenceNumber":"1","EffectiveDate":"01/09/2023","DenialReason":"test","ClaimPaymentNotes":"test","NextReviewDate":"01-11-2023","InpatientDetails":{"AdmissionDate":"01-10-2023","DischargeDate":"10-10-2023","BedType":"test","AdmissionType":"test","TotalDaysApproved":"10","LastCoveredDate":"01-06-2023"},"OutpatientDetails":{"FirstDay":"01-10-2023","LastDay":"10-10-2023","ServiceType":"test","TotalDaysApproved":"10"},"ProcedureDetails":{"ProcedureDetail":[{"Code":"123","Description":"test","Status":"test","ServiceCode":"6776","Visit":{"RequestedUnits":"10","ApprovedUnits":"10","TypeOfUnits":"Bed"}}]},"DiagnosisDetails":{"DiagnosisDetail":[{"Type":"Bed","Code":"123","Description":"test","Status":"test"}]},"ProviderDetails":{"ProviderDetail":[{"Type":"Bed","Id":"6776","Name":"test","TaxId":"6758","ParticipatingStatus":"tets","AddressDetails":{"AddressType":"home","Street_1":"test","Street_2":"test2","County":"USA","City":"LA","State":"test","ZipCode":"76768","ZipCode_ext":"090","PhoneNumber":"7878787978"}}]}}';
            clAuthDetWrap = ClinicalAuthDetails_LS_HUM.retrieveDiagnosisCodes(strjson);
            Test.stopTest();
        }
        
        Assert.areEqual(1, clAuthDetWrap.size());
    }
    
    static testMethod void assignProviderDetailsTest(){
        User testUser = [Select Id, Name from User where LastName ='test User1'];
        List<ClinicalAuthDetailsWrapper_LDTO_HUM> clAuthDetWrap;
        String clinicalAuthJSON = '[{"sReqProviderCity":"CityA","sReqProviderState":"StateA","sReqProviderPhnNum":"123-456-7890","sReqProviderStreet":"StreetA","sReqProviderZipCode":"12345","sReqProviderID":"ProviderID123","sReqProviderName":"Provider Name","sReqProviderTaxID":"TaxID456","sReqProviderPartStatus":"Active","sReqProviderType":"RequestingProvider","sRecordType":"Authorization"},{"sReqProviderCity":"CityA","sReqProviderState":"StateA","sReqProviderPhnNum":"123-456-7890","sReqProviderStreet":"StreetA","sReqProviderZipCode":"12345","sReqProviderID":"ProviderID123","sReqProviderName":"Provider Name","sReqProviderTaxID":"TaxID456","sReqProviderPartStatus":"Active","sReqProviderType":"TreatingProvider","sRecordType":"Authorization"},{"sReqProviderCity":"CityA","sReqProviderState":"StateA","sReqProviderPhnNum":"123-456-7890","sReqProviderStreet":"StreetA","sReqProviderZipCode":"12345","sReqProviderID":"ProviderID123","sReqProviderName":"Provider Name","sReqProviderTaxID":"TaxID456","sReqProviderPartStatus":"Active","sReqProviderType":"Facility","sRecordType":"Authorization"}]';
        List<ClinicalAuthDetailsWrapper_LDTO_HUM> clinicalAuths = (List<ClinicalAuthDetailsWrapper_LDTO_HUM>) JSON.deserialize(clinicalAuthJSON,List<ClinicalAuthDetailsWrapper_LDTO_HUM>.class);
        System.runAs(testUser)
        {
            Test.startTest();
            clAuthDetWrap = ClinicalAuthDetails_LS_HUM.assignProviderDetails(clinicalAuths);
            Test.stopTest();
        }
        Assert.areEqual(3, clAuthDetWrap.size());
    }
    
    static testMethod void populateStandardMapForLoggingForProviderTest(){
       User testUser = [Select Id, Name from User where LastName ='test User1']; 
        String clinicalAuth1 = '{"sReqProviderCity":"CityA","sReqProviderState":"StateA","sReqProviderPhnNum":"123-456-7890","sReqProviderStreet":"StreetA","sReqProviderZipCode":"12345","sReqProviderID":"ProviderID123","sReqProviderName":"Provider Name","sReqProviderTaxID":"TaxID456","sReqProviderPartStatus":"Active","sReqProviderType":"RequestingProvider","sRecordType":"Authorization"}';
        String clinicalAuth2 = '{"sReqProviderCity":"CityA","sReqProviderState":"StateA","sReqProviderPhnNum":"123-456-7890","sReqProviderStreet":"StreetA","sReqProviderZipCode":"12345","sReqProviderID":"ProviderID123","sReqProviderName":"Provider Name","sReqProviderTaxID":"TaxID456","sReqProviderPartStatus":"Active","sReqProviderType":"TreatingProvider","sRecordType":"Authorization"}';
        String clinicalAuth3 = '{"sReqProviderCity":"CityA","sReqProviderState":"StateA","sReqProviderPhnNum":"123-456-7890","sReqProviderStreet":"StreetA","sReqProviderZipCode":"12345","sReqProviderID":"ProviderID123","sReqProviderName":"Provider Name","sReqProviderTaxID":"TaxID456","sReqProviderPartStatus":"Active","sReqProviderType":"Facility","sRecordType":"Authorization"}';
        ClinicalAuthDetailsWrapper_LDTO_HUM oProv;
        System.runAs(testUser)
        {
            Test.startTest();
            oProv = (ClinicalAuthDetailsWrapper_LDTO_HUM) JSON.deserialize(clinicalAuth1,ClinicalAuthDetailsWrapper_LDTO_HUM.class);
            new ClinicalAuthDetails_LS_HUM().populateStandardMapForLoggingForProvider(oProv);
            oProv = (ClinicalAuthDetailsWrapper_LDTO_HUM) JSON.deserialize(clinicalAuth2,ClinicalAuthDetailsWrapper_LDTO_HUM.class);
            new ClinicalAuthDetails_LS_HUM().populateStandardMapForLoggingForProvider(oProv);
            oProv = (ClinicalAuthDetailsWrapper_LDTO_HUM) JSON.deserialize(clinicalAuth3,ClinicalAuthDetailsWrapper_LDTO_HUM.class);
            new ClinicalAuthDetails_LS_HUM().populateStandardMapForLoggingForProvider(oProv);
            Test.stopTest();
        }
        Assert.areEqual('Provider Name', ClinicalAuthDetails_LS_HUM.standardMapForLogging.get('Requesting Provider Name').get('Requesting Provider').toString());
        Assert.areEqual('TaxID456', ClinicalAuthDetails_LS_HUM.standardMapForLogging.get('Treating Provider TaxId').get('Treating Provider TaxId').toString());
        Assert.areEqual('ProviderID123', ClinicalAuthDetails_LS_HUM.standardMapForLogging.get('Facility Provider Id').get('Facility Provider Id').toString());
        
    }
    
    static testMethod void retrieveProviderDetailsTest(){
        User testUser = [Select Id, Name from User where LastName ='test User1'];
        List<ClinicalAuthDetailsWrapper_LDTO_HUM> clAuthDetWrap;
        System.runAs(testUser)
        {
            Test.startTest();
            String strjson='{"AuthID":"9098980","AuthType":"test","AuthStatus":"Success","UpdatedDate":"01-10-2023","CreatedDate":"01-05-2023","DateOfService":"01-09-2023","RequestType":"test","ContactMethod":"test","IsReferral":"true","StatusReason":"test","CustomerGenkey":"089899","GroupId":"985678","PlatformCode":"98988","CoverageSequenceNumber":"1","EffectiveDate":"01/09/2023","DenialReason":"test","ClaimPaymentNotes":"test","NextReviewDate":"01-11-2023","InpatientDetails":{"AdmissionDate":"01-10-2023","DischargeDate":"10-10-2023","BedType":"test","AdmissionType":"test","TotalDaysApproved":"10","LastCoveredDate":"01-06-2023"},"OutpatientDetails":{"FirstDay":"01-10-2023","LastDay":"10-10-2023","ServiceType":"test","TotalDaysApproved":"10"},"ProcedureDetails":{"ProcedureDetail":[{"Code":"123","Description":"test","Status":"test","ServiceCode":"6776","Visit":{"RequestedUnits":"10","ApprovedUnits":"10","TypeOfUnits":"Bed"}}]},"DiagnosisDetails":{"DiagnosisDetail":[{"Type":"Bed","Code":"123","Description":"test","Status":"test"}]},"ProviderDetails":{"ProviderDetail":[{"Type":"Bed","Id":"6776","Name":"test","TaxId":"6758","ParticipatingStatus":"tets","AddressDetails":{"Address":[{"AddressType":"Select Service","Street_1":"test","Street_2":"test2","County":"USA","City":"LA","State":"test","ZipCode":"76768","ZipCode_ext":"090","PhoneNumber":"7878787978"},{"AddressType":"Select Service","Street_1":"test","Street_2":"test2","County":"USA","City":"LA","State":"test","ZipCode":"76768","ZipCode_ext":"","PhoneNumber":"7878787978"}]}}]}}';
            clAuthDetWrap = ClinicalAuthDetails_LS_HUM.retrieveProviderDetails(strjson);
            Test.stopTest();
        }
        Assert.areEqual(3,clAuthDetWrap.size());
    }
    
    static testMethod void retrieveReferralInformationOPTest(){
        User testUser = [Select Id, Name from User where LastName ='test User1'];
        ClinicalAuthDetailsWrapper_LDTO_HUM clAuthDetWrap;
        System.runAs(testUser)
        {
            Test.startTest();
            String strjson='[{"AuthID":"9098980","AuthType":"test","AuthStatus":"Success","UpdatedDate":"01-10-2023","CreatedDate":"01-05-2023","DateOfService":"01-09-2023","RequestType":"test","ContactMethod":"test","IsReferral":"true","StatusReason":"test","CustomerGenkey":"089899","GroupId":"985678","PlatformCode":"98988","CoverageSequenceNumber":"1","EffectiveDate":"01/09/2023","DenialReason":"test","ClaimPaymentNotes":"test","NextReviewDate":"01-11-2023","InpatientDetails":{"AdmissionDate":"01-10-2023","DischargeDate":"10-10-2023","BedType":"test","AdmissionType":"test","TotalDaysApproved":"10","LastCoveredDate":"01-06-2023"},"OutpatientDetails":{"FirstDay":"01-10-2023","LastDay":"10-10-2023","ServiceType":"test","TotalDaysApproved":"10"},"ProcedureDetails":{"ProcedureDetail":[{"Code":"123","Description":"test","Status":"test","ServiceCode":"6776","Visit":{"RequestedUnits":"10","ApprovedUnits":"10","TypeOfUnits":"Bed"}}]},"DiagnosisDetails":{"DiagnosisDetail":[{"Type":"Bed","Code":"123","Description":"test","Status":"test"}]},"ProviderDetails":{"ProviderDetail":[{"Type":"Bed","Id":"6776","Name":"test","TaxId":"6758","ParticipatingStatus":"tets","AddressDetails":{"Address":[{"AddressType":"Select Service","Street_1":"test","Street_2":"test2","County":"USA","City":"LA","State":"test","ZipCode":"76768","ZipCode_ext":"090","PhoneNumber":"7878787978"},{"AddressType":"Select Service","Street_1":"test","Street_2":"test2","County":"USA","City":"LA","State":"test","ZipCode":"76768","ZipCode_ext":"","PhoneNumber":"7878787978"}]}}]}}]';
            List<ClinicalAuthDetailsResponse_LH_HUM.Authorization> lstAuthDetail = (List<ClinicalAuthDetailsResponse_LH_HUM.Authorization>) JSON.deserialize(strjson,List<ClinicalAuthDetailsResponse_LH_HUM.Authorization>.class);
            clAuthDetWrap = ClinicalAuthDetails_LS_HUM.retrieveReferralInformationOP(lstAuthDetail);
            Test.stopTest();
        }
        Assert.areEqual(10,clAuthDetWrap.iTotalDays);
    }
    
    static testMethod void retrieveReferralInformationIPTest(){
        User testUser = [Select Id, Name from User where LastName ='test User1'];
        ClinicalAuthDetailsWrapper_LDTO_HUM clAuthDetWrap;
        System.runAs(testUser)
        {
            Test.startTest();
            String strjson='[{"AuthID":"9098980","AuthType":"test","AuthStatus":"Success","UpdatedDate":"01-10-2023","CreatedDate":"01-05-2023","DateOfService":"01-09-2023","RequestType":"test","ContactMethod":"test","IsReferral":"true","StatusReason":"test","CustomerGenkey":"089899","GroupId":"985678","PlatformCode":"98988","CoverageSequenceNumber":"1","EffectiveDate":"01/09/2023","DenialReason":"test","ClaimPaymentNotes":"test","NextReviewDate":"01-11-2023","InpatientDetails":{"AdmissionDate":"01-10-2023","DischargeDate":"10-10-2023","BedType":"test","AdmissionType":"test","TotalDaysApproved":"10","LastCoveredDate":"01-06-2023"},"OutpatientDetails":{"FirstDay":"01-10-2023","LastDay":"10-10-2023","ServiceType":"test","TotalDaysApproved":"10"},"ProcedureDetails":{"ProcedureDetail":[{"Code":"123","Description":"test","Status":"test","ServiceCode":"6776","Visit":{"RequestedUnits":"10","ApprovedUnits":"10","TypeOfUnits":"Bed"}}]},"DiagnosisDetails":{"DiagnosisDetail":[{"Type":"Bed","Code":"123","Description":"test","Status":"test"}]},"ProviderDetails":{"ProviderDetail":[{"Type":"Bed","Id":"6776","Name":"test","TaxId":"6758","ParticipatingStatus":"tets","AddressDetails":{"Address":[{"AddressType":"Select Service","Street_1":"test","Street_2":"test2","County":"USA","City":"LA","State":"test","ZipCode":"76768","ZipCode_ext":"090","PhoneNumber":"7878787978"},{"AddressType":"Select Service","Street_1":"test","Street_2":"test2","County":"USA","City":"LA","State":"test","ZipCode":"76768","ZipCode_ext":"","PhoneNumber":"7878787978"}]}}]}}]';
            List<ClinicalAuthDetailsResponse_LH_HUM.Authorization> lstAuthDetail = (List<ClinicalAuthDetailsResponse_LH_HUM.Authorization>) JSON.deserialize(strjson,List<ClinicalAuthDetailsResponse_LH_HUM.Authorization>.class);
            clAuthDetWrap = ClinicalAuthDetails_LS_HUM.retrieveReferralInformationIP(lstAuthDetail);
            Test.stopTest();
        }
        Assert.areEqual('10',clAuthDetWrap.sTotalDaysApproved);
    }
    
    static testMethod void retrieveAuthDetailTest(){
        User testUser = [Select Id, Name from User where LastName ='test User1'];
        ClinicalAuthDetailsWrapper_LDTO_HUM clAuthDetWrap;
        System.runAs(testUser)
        {
            Test.startTest();
            String strjson='[{"AuthID":"9098980","AuthType":"test","AuthStatus":"Success","UpdatedDate":"01-10-2023","CreatedDate":"01-05-2023","DateOfService":"01-09-2023","RequestType":"test","ContactMethod":"test","IsReferral":"true","StatusReason":"test","CustomerGenkey":"089899","GroupId":"985678","PlatformCode":"98988","CoverageSequenceNumber":"1","EffectiveDate":"01/09/2023","DenialReason":"test","ClaimPaymentNotes":"test","NextReviewDate":"01-11-2023","InpatientDetails":{"AdmissionDate":"01-10-2023","DischargeDate":"10-10-2023","BedType":"test","AdmissionType":"test","TotalDaysApproved":"10","LastCoveredDate":"01-06-2023"},"OutpatientDetails":{"FirstDay":"01-10-2023","LastDay":"10-10-2023","ServiceType":"test","TotalDaysApproved":"10"},"ProcedureDetails":{"ProcedureDetail":[{"Code":"123","Description":"test","Status":"test","ServiceCode":"6776","Visit":{"RequestedUnits":"10","ApprovedUnits":"10","TypeOfUnits":"Bed"}}]},"DiagnosisDetails":{"DiagnosisDetail":[{"Type":"Bed","Code":"123","Description":"test","Status":"test"}]},"ProviderDetails":{"ProviderDetail":[{"Type":"Bed","Id":"6776","Name":"test","TaxId":"6758","ParticipatingStatus":"tets","AddressDetails":{"Address":[{"AddressType":"Select Service","Street_1":"test","Street_2":"test2","County":"USA","City":"LA","State":"test","ZipCode":"76768","ZipCode_ext":"090","PhoneNumber":"7878787978"},{"AddressType":"Select Service","Street_1":"test","Street_2":"test2","County":"USA","City":"LA","State":"test","ZipCode":"76768","ZipCode_ext":"","PhoneNumber":"7878787978"}]}}]}}]';
            List<ClinicalAuthDetailsResponse_LH_HUM.Authorization> lstAuthDetail = (List<ClinicalAuthDetailsResponse_LH_HUM.Authorization>) JSON.deserialize(strjson,List<ClinicalAuthDetailsResponse_LH_HUM.Authorization>.class);
            clAuthDetWrap = ClinicalAuthDetails_LS_HUM.retrieveAuthDetail(lstAuthDetail);
            Test.stopTest();
        }
        Assert.areEqual('test', clAuthDetWrap.sRequestType);
    }
    
    static testMethod void setHttpRequestTest(){
        User testUser = [Select Id, Name from User where LastName ='test User1'];
        HUM_Webservice_Callout__c callout = [SELECT Name, Content_Type__c,Certificate_Name__c,End_Point_URL__c,Timeout__c, Request_Type__c 
                                             FROM HUM_Webservice_Callout__c WHERE Name!='Exception' LIMIT 1];
        HttpRequest req;
        System.runAs(testUser)
        {
            Test.startTest();
            req = ClinicalAuthDetails_LS_HUM.setHttpRequest(new HttpRequest(), callout, 'test');
            Test.stopTest();
        }
        
        Assert.areEqual('POST',req.getMethod());
    }
    
    static testMethod void checkAuthSecurityTest(){
        User testUser = [Select Id, Name from User where LastName ='test User1'];
        Id memPlanId = [SELECT Id FROM MemberPlan LIMIT 1].Id;
        Boolean isPassed = false;
        System.runAs(testUser)
        {
            Test.startTest();
            String strjson='{"AuthorizationsResponse":{"Authorizations":{"Authorization":[{"AuthID":"Auth123","AuthType":"Pre-authorization","AuthStatus":"Approved","UpdatedDate":"2023-10-15T10:30:00Z","CreatedDate":"2023-10-10T08:45:00Z","DateOfService":"2023-11-01","RequestType":"InPatient","ContactMethod":"Phone","IsReferral":"true","StatusReason":"Medical Necessity","CustomerGenkey":"ABC123","GroupId":"Group123","PlatformCode":"PlatformA","CoverageSequenceNumber":"CovSeq456","EffectiveDate":"2023-11-01","InpatientDetails":{"AdmissionDate":"2023-11-01","DischargeDate":"2023-11-10","BedType":"Single","AdmissionType":"Emergency","TotalDaysApproved":"10","LastCoveredDate":"2023-11-10"},"OutpatientDetails":{"FirstDay":"","LastDay":"","ServiceType":"","TotalDaysApproved":""},"ProcedureDetails":{"ProcedureDetail":[{"Code":"Proc123","Description":"Description of the procedure","Status":"Completed","ServiceCode":"Serv456","Visit":{"RequestedUnits":"5","ApprovedUnits":"5","TypeOfUnits":"Hours"}}]},"DiagnosisDetails":{"DiagnosisDetail":[{"Type":"Primary","Code":"ICD-10 Code","Description":"Description of the diagnosis","Status":"Active"}]},"ProviderDetails":{"ProviderDetail":[{"Type":"Hospital","Id":"ProvID789","Name":"Hospital XYZ","TaxId":"TaxID789","ParticipatingStatus":"Active","AddressDetails":{"Address":[{"AddressType":"Business","Street_1":"123 Main St","City":"CityA","State":"StateA","ZipCode":"12345","PhoneNumber":"123-456-7890"}]}}]},"DenialReason":"","ClaimPaymentNotes":"Payment received","NextReviewDate":"2023-12-01"}]},"MemberInfo":{"FirstName":"John","LastName":"Doe","MiddleName":"","DateOfBirth":"1990-01-15","Gender":"Male","SubscriberId":"Subs123","DependentCode":"","ODS_MemberGenkey":"ODS123","SdrPersonID":"SdrPerson456"},"TotalRecordsFound":"1"},"CommunicationRecordsResponse":{"CommunicationRecords":[{"RecordType":"Communication","CreatedDate":"2023-10-15T11:45:00Z","CreatedBy":"UserA","LastModifiedDate":"2023-10-15T14:30:00Z","LastModifiedBy":"UserB","Department":"Billing","CommunicationType":"Outbound","ContactMethod":"Email","ContactType":"Phone","ContactAttemptNo":"1","ContactResult":["Successful","Unsuccessful"],"ContactResultOther":"Other result","Notes":"Additional notes","Details":"Additional details"}],"TotalRecordsFound":"1"},"LettersResponse":{"LettersRecords":[{"LetterType":"Medical","AssociatedAuthID":"Auth123","AssociatedCaseID":"Case456","CreatedBy":"UserC","CreatedDate":"2023-10-20T09:00:00Z","TemplateName":"TemplateX","Status":"Draft","FullfillmentStatus":"Not Fulfilled","P2POfferedinLetter":"Yes","LetterName":"LetterA","VoidRemarks":"","LastModifiedBy":"UserD","LastModifiedDate":"2023-10-21T15:30:00Z","FilePath":"/documents/LetterA.pdf"}],"TotalRecordsFound":"1"}}';        
            ClinicalAuthDetailsResponse_H_HUM authDet = (ClinicalAuthDetailsResponse_H_HUM) JSON.deserialize(strjson,ClinicalAuthDetailsResponse_H_HUM.class);
            isPassed = new ClinicalAuthDetails_LS_HUM().checkAuthSecurity(authDet,memPlanId);
            Test.stopTest();
        }
        
        Assert.areEqual(true,isPassed);
    }
    
    
    static testMethod void Base(){
        User testUser = [Select Id,LastName, Name from User where LastName ='test User1'];
        
        System.runAs(testUser)
        {
            Test.startTest();
            Test.stopTest();
        }
        
        Assert.areEqual('test User1',testUser.LastName);
    }
    
    
    static testMethod void getSetVariableTest(){
		ClinicalAuthDetails_LC_HUM cAuthLcHum = new ClinicalAuthDetails_LC_HUM();
        ClinicalAuthDetails_LC_HUM.lstCommunicationRecords = new List<ClinicalAuthDetailsWrapper_LDTO_HUM>();
        ClinicalAuthDetails_LC_HUM.lstLettersRecords = new List<ClinicalAuthDetailsWrapper_LDTO_HUM>();
        ClinicalAuthDetails_LC_HUM.oAuthDetailsWrapper = new ClinicalAuthDetailsWrapper_LDTO_HUM();
        ClinicalAuthDetails_LC_HUM.oAuthDetailsWrapperIP = new ClinicalAuthDetailsWrapper_LDTO_HUM();
        ClinicalAuthDetails_LC_HUM.oAuthDetailsWrapperOP = new ClinicalAuthDetailsWrapper_LDTO_HUM();
        ClinicalAuthDetails_LC_HUM.lstProvider = new List<ClinicalAuthDetailsWrapper_LDTO_HUM>();
        ClinicalAuthDetails_LC_HUM.lstDiagnosis = new List<ClinicalAuthDetailsWrapper_LDTO_HUM>();
        ClinicalAuthDetails_LC_HUM.lstProcedure = new List<ClinicalAuthDetailsWrapper_LDTO_HUM>();
        ClinicalAuthDetails_LC_HUM.lstProcedureOP = new List<ClinicalAuthDetailsWrapper_LDTO_HUM>();
        ClinicalAuthDetails_LC_HUM.sCommunicationPageNumber = '1';
        ClinicalAuthDetails_LC_HUM.bServiceError = true;
        ClinicalAuthDetails_LC_HUM.bSearchError = true;
        ClinicalAuthDetails_LC_HUM.bAuthTypeIP = true;
        ClinicalAuthDetails_LC_HUM.bAuthTypeOP = true;
        ClinicalAuthDetails_LC_HUM.sPolicyMemRecID = '67867';
        ClinicalAuthDetails_LC_HUM.standardMapvalue = '67867';
        ClinicalAuthDetails_LC_HUM.sAuthID = '67867';
        ClinicalAuthDetails_LC_HUM.sTotalCommunicationRecords = '6';
        ClinicalAuthDetails_LC_HUM.sTotalLettersRecords = '6';
        ClinicalAuthDetails_LC_HUM.bIsError = true;
        ClinicalAuthDetails_LC_HUM.bShowError = true;
        ClinicalAuthDetails_LC_HUM.bShowMessage = true;
        ClinicalAuthDetails_LC_HUM.sLettersPageNumber = '8';
        ClinicalAuthDetails_LC_HUM.bShowResults = true;
        ClinicalAuthDetails_LC_HUM.bShowServiceError = true;
        ClinicalAuthDetails_LC_HUM.bTotalRecordFlag = true;
        ClinicalAuthDetails_LC_HUM.sRecId = '9876789';
        ClinicalAuthDetails_LC_HUM.sUserinputAuthId = '08989888';  
        
        Assert.areEqual(true,ClinicalAuthDetails_LC_HUM.bAuthTypeOP);
    }
    
    
    static testMethod void testWrapperClass() {
        ClinicalAuthDetailsWrapper_LDTO_HUM wrapperObj = new ClinicalAuthDetailsWrapper_LDTO_HUM();

        wrapperObj.sAuthID = 'Auth123';
        wrapperObj.sAuthType = 'Pre-authorization';
        wrapperObj.sAuthStatus = 'Approved';
        wrapperObj.sUpdatedDate = '2023-10-15T10:30:00Z';
        wrapperObj.sCreatedDate = '2023-10-10T08:45:00Z';
        wrapperObj.sDateOfService = '2023-11-01';
        wrapperObj.sContactMethod = 'Phone';
        wrapperObj.sIsReferral = 'true';
        wrapperObj.sStatusReason = 'Medical Necessity';
        wrapperObj.sCustomerGenkey = 'ABC123';
        wrapperObj.sGroupId = 'Group123';
        wrapperObj.sPlatformCode = 'PlatformA';
        wrapperObj.sCoverageSequenceNumber = 'CovSeq456';
        wrapperObj.sEffectiveDate = '2023-11-01';
        wrapperObj.sRequestType = 'InPatient';
        wrapperObj.sDenialReason = 'Not Applicable';
        wrapperObj.sClaimsPaymentNotes = 'Payment received';
        wrapperObj.sNextReviewDate = '2023-12-01';
        wrapperObj.sAdmissionDate = '2023-11-01';
        wrapperObj.sDischargeDate = '2023-11-10';
        wrapperObj.sBedType = 'Single';
        wrapperObj.sAdmissionType = 'Emergency';
        wrapperObj.sTotalDaysApproved = '10';
        wrapperObj.sLastCoveredDate = '2023-11-10';
        wrapperObj.sServiceType = 'OutPatient';
        wrapperObj.sOPRequestType = 'Regular';
        wrapperObj.sFirstDay = '2023-11-01';
        wrapperObj.sLastDay = '2023-11-05';
        wrapperObj.iTotalDays = 5;
        wrapperObj.sDiagnosisType = 'Primary';
        wrapperObj.sDiagnosisCode = 'ICD-10 Code';
        wrapperObj.sDiagnosisDescription = 'Description of the diagnosis';
        wrapperObj.sDiagnosisStatus = 'Active';
        wrapperObj.sProcedureCode = 'Proc123';
        wrapperObj.sProcedureDescription = 'Description of the procedure';
        wrapperObj.sProcedureStatus = 'Completed';
        wrapperObj.sProcedureServiceCode = 'Serv456';
        wrapperObj.sProcedureRequestingUnits = '3';
        wrapperObj.sProcedureAuthorizedUnits = '3';
        wrapperObj.sProcedureTypeofUnits = 'Days';
        wrapperObj.sReqProviderCity = 'CityA';
        wrapperObj.sReqProviderState = 'StateA';
        wrapperObj.sReqProviderPhnNum = '123-456-7890';
        wrapperObj.sReqProviderStreet = '123 Main St';
        wrapperObj.sReqProviderZipCode = '12345';
        wrapperObj.sReqProviderID = 'ProvID789';
        wrapperObj.sReqProviderName = 'Hospital XYZ';
        wrapperObj.sReqProviderTaxID = 'TaxID789';
        wrapperObj.sReqProviderPartStatus = 'Active';
        wrapperObj.sReqProviderType = 'Hospital';
        wrapperObj.sRecordType = 'Communication';
        wrapperObj.sCommCreatedDate = '2023-10-15T11:45:00Z';
        wrapperObj.sCommCreatedBy = 'UserA';
        wrapperObj.sCommLastModifiedDate = '2023-10-15T14:30:00Z';
        wrapperObj.sCommLastModifiedBy = 'UserB';
        wrapperObj.sDepartment = 'Billing';
        wrapperObj.sCommunicationType = 'Outbound';
        wrapperObj.sCommContactMethod = 'Email';
        wrapperObj.sContactType = 'Phone';
        wrapperObj.sContactAttemptNo = '1';
        wrapperObj.sContactResult = 'Successful';
        wrapperObj.lstContactResult = new List<String>{'Successful', 'Unsuccessful'};
        wrapperObj.sContactResultOther = 'Other result';
        wrapperObj.sNotes = 'Additional notes';
        wrapperObj.sDetails = 'Additional details';
        wrapperObj.sLettersType = 'Medical';
        wrapperObj.sLettersAssociatedAuthID = 'Auth123';
        wrapperObj.sLettersAssociatedCaseID = 'Case456';
        wrapperObj.sLettersCreatedBy = 'UserC';
        wrapperObj.sLettersCreatedDate = '2023-10-20T09:00:00Z';
        wrapperObj.sLettersTemplateName = 'TemplateX';
        wrapperObj.sLettersStatus = 'Draft';
        wrapperObj.sLettersFullfillmentStatus = 'Not Fulfilled';
        wrapperObj.sLettersP2POfferedinLetter = 'Yes';
        wrapperObj.sLettersLetterName = 'LetterA';
        wrapperObj.sLettersVoidRemarks = '';
        wrapperObj.sLettersLastModifiedBy = 'UserD';
        wrapperObj.sLettersLastModifiedDate = '2023-10-21T15:30:00Z';
        wrapperObj.sFilePath = '/documents/LetterA.pdf';
        wrapperObj.bCheck = true;
        
        System.assertEquals('true', wrapperObj.sIsReferral);
    }
    
    @isTest
    static void testClinicalAuthDetailsResponse() {
        // Instantiate the main class
        ClinicalAuthDetailsResponse_LH_HUM mainClassInstance = new ClinicalAuthDetailsResponse_LH_HUM();

        // Test AuthorizationResponse instantiation
        ClinicalAuthDetailsResponse_LH_HUM.AuthorizationsResponse authResponse = new ClinicalAuthDetailsResponse_LH_HUM.AuthorizationsResponse();
        System.assertNotEquals(null, authResponse);

        // Test CommunicationRecordsResponse instantiation
        ClinicalAuthDetailsResponse_LH_HUM.CommunicationRecordsResponse commResponse = new ClinicalAuthDetailsResponse_LH_HUM.CommunicationRecordsResponse();
        System.assertNotEquals(null, commResponse);

        // Test LettersResponse instantiation
        ClinicalAuthDetailsResponse_LH_HUM.LettersResponse lettersResponse = new ClinicalAuthDetailsResponse_LH_HUM.LettersResponse();
        System.assertNotEquals(null, lettersResponse);

        // Test Authorization instantiation
        ClinicalAuthDetailsResponse_LH_HUM.Authorization auth = new ClinicalAuthDetailsResponse_LH_HUM.Authorization();
        System.assertNotEquals(null, auth);
        
        ClinicalAuthDetailsResponse_LH_HUM.InpatientDetails impDet = new ClinicalAuthDetailsResponse_LH_HUM.InpatientDetails();
        ClinicalAuthDetailsResponse_LH_HUM.OutpatientDetails outPat = new ClinicalAuthDetailsResponse_LH_HUM.OutpatientDetails();
        ClinicalAuthDetailsResponse_LH_HUM.ProcedureDetails pDet = new ClinicalAuthDetailsResponse_LH_HUM.ProcedureDetails();
        ClinicalAuthDetailsResponse_LH_HUM.ProcedureDetail pDetail = new ClinicalAuthDetailsResponse_LH_HUM.ProcedureDetail();
        ClinicalAuthDetailsResponse_LH_HUM.Visit visitDet = new ClinicalAuthDetailsResponse_LH_HUM.Visit();
        ClinicalAuthDetailsResponse_LH_HUM.DiagnosisDetails diagDets = new ClinicalAuthDetailsResponse_LH_HUM.DiagnosisDetails();
        ClinicalAuthDetailsResponse_LH_HUM.DiagnosisDetail diagDet = new ClinicalAuthDetailsResponse_LH_HUM.DiagnosisDetail();
        ClinicalAuthDetailsResponse_LH_HUM.ProviderDetails pDets = new ClinicalAuthDetailsResponse_LH_HUM.ProviderDetails();
        ClinicalAuthDetailsResponse_LH_HUM.ProviderDetail pDetailObj = new ClinicalAuthDetailsResponse_LH_HUM.ProviderDetail();
        ClinicalAuthDetailsResponse_LH_HUM.AddressDetails adrsDets = new ClinicalAuthDetailsResponse_LH_HUM.AddressDetails();
        ClinicalAuthDetailsResponse_LH_HUM.Address adrsDet = new ClinicalAuthDetailsResponse_LH_HUM.Address();
        ClinicalAuthDetailsResponse_LH_HUM.MemberInfo memInfo = new ClinicalAuthDetailsResponse_LH_HUM.MemberInfo();
        ClinicalAuthDetailsResponse_LH_HUM.CommunicationRecords cmRec = new ClinicalAuthDetailsResponse_LH_HUM.CommunicationRecords();
        ClinicalAuthDetailsResponse_LH_HUM.LettersRecords letterRec = new ClinicalAuthDetailsResponse_LH_HUM.LettersRecords();
        ClinicalAuthDetailsRequest_LH_HUM.AuthDetailRequest authReq = new ClinicalAuthDetailsRequest_LH_HUM.AuthDetailRequest();
        ClinicalAuthDetailsRequest_LH_HUM.PageMetaData_Communication pComm = new ClinicalAuthDetailsRequest_LH_HUM.PageMetaData_Communication();
        ClinicalAuthDetailsRequest_LH_HUM.Filter_Communication filComm = new ClinicalAuthDetailsRequest_LH_HUM.Filter_Communication();
        ClinicalAuthDetailsRequest_LH_HUM.SortDetail_Communication sortComm = new ClinicalAuthDetailsRequest_LH_HUM.SortDetail_Communication();
        ClinicalAuthDetailsRequest_LH_HUM.PageMetaData_Letters pLetter = new ClinicalAuthDetailsRequest_LH_HUM.PageMetaData_Letters();
        ClinicalAuthDetailsRequest_LH_HUM.Filter_Letters pFilLetter = new ClinicalAuthDetailsRequest_LH_HUM.Filter_Letters();
        ClinicalAuthDetailsRequest_LH_HUM.SortDetail_Letters sortLetter = new ClinicalAuthDetailsRequest_LH_HUM.SortDetail_Letters();
        ClinicalAuthDetails_LS_HUM.oAuthDetailsWrapper = new ClinicalAuthDetailsWrapper_LDTO_HUM();
        ClinicalAuthDetails_LS_HUM.oAuthDetailsWrapperIP = new ClinicalAuthDetailsWrapper_LDTO_HUM();
        ClinicalAuthDetails_LS_HUM.oAuthDetailsWrapperOP = new ClinicalAuthDetailsWrapper_LDTO_HUM();
        ClinicalAuthDetails_LS_HUM.lstProvider = new List<ClinicalAuthDetailsWrapper_LDTO_HUM>();
        ClinicalAuthDetails_LS_HUM.lstDiagnosis = new List<ClinicalAuthDetailsWrapper_LDTO_HUM>();
        ClinicalAuthDetails_LS_HUM.lstProcedure = new List<ClinicalAuthDetailsWrapper_LDTO_HUM>();
        ClinicalAuthDetails_LS_HUM.lstProcedureOP = new List<ClinicalAuthDetailsWrapper_LDTO_HUM>();
        ClinicalAuthDetails_LS_HUM.lstCommunicationRecords = new List<ClinicalAuthDetailsWrapper_LDTO_HUM>();
        ClinicalAuthDetails_LS_HUM.lstLettersRecords = new List<ClinicalAuthDetailsWrapper_LDTO_HUM>();
        
        ClinicalAuthDetails_LS_HUM cAuthLS = new ClinicalAuthDetails_LS_HUM();
        cAuthLS.sLettersPageNumber = '1';
        cAuthLS.sCommunicationPageNumber = '1';
        cAuthLS.bServiceError = true;
        cAuthLS.bSearchError = true;
        cAuthLS.bAuthTypeIP = true;
        cAuthLS.bAuthTypeOP = true;
        cAuthLS.sPolicyMemRecID = '123';
        cAuthLS.standardMapvalue = '343test';
        cAuthLS.sAuthID = '3456';
        cAuthLS.sTotalCommunicationRecords = '1';
        cAuthLS.sTotalLettersRecords = '1';
        cAuthLS.lstFloatingLinks = new List<String>();
        ClinicalAuthDetails_LS_HUM.memPlanId = '12343';
        
        
    }
    
    
    public class ClinicalAuthMock implements HttpCalloutMock{
         public HTTPResponse respond(HTTPRequest req) 
         {        
             // Create a mock response
             HttpResponse res = new HttpResponse();
             res.setHeader('Content-Type', 'application/json');
             String strjson='{"AuthorizationsResponse":{"Authorizations":{"Authorization":[{"AuthID":"9098980","AuthType":"test","AuthStatus":"Success","UpdatedDate":"01-10-2023","CreatedDate":"01-05-2023","DateOfService":"01-09-2023","RequestType":"test","ContactMethod":"test","IsReferral":"true","StatusReason":"test","CustomerGenkey":"089899","GroupId":"985678","PlatformCode":"98988","CoverageSequenceNumber":"1","EffectiveDate":"01/09/2023","DenialReason":"test","ClaimPaymentNotes":"test","NextReviewDate":"01-11-2023","InpatientDetails":{"AdmissionDate":"01-10-2023","DischargeDate":"10-10-2023","BedType":"test","AdmissionType":"test","TotalDaysApproved":"10","LastCoveredDate":"01-06-2023"},"OutpatientDetails":{"FirstDay":"01-10-2023","LastDay":"10-10-2023","ServiceType":"test","TotalDaysApproved":"10"},"ProcedureDetails":{"ProcedureDetail":[{"Code":"123","Description":"test","Status":"test","ServiceCode":"6776","Visit":{"RequestedUnits":"10","ApprovedUnits":"10","TypeOfUnits":"Bed"}}]},"DiagnosisDetails":{"DiagnosisDetail":[{"Type":"Bed","Code":"123","Description":"test","Status":"test"}]},"ProviderDetails":{"ProviderDetail":[{"Type":"Bed","Id":"6776","Name":"test","TaxId":"6758","ParticipatingStatus":"tets","AddressDetails":{"AddressType":"home","Street_1":"test","Street_2":"test2","County":"USA","City":"LA","State":"test","ZipCode":"76768","ZipCode_ext":"090","PhoneNumber":"7878787978"}}]}}]},"MemberInfo":{},"TotalRecordsFound":"1"},"CommunicationRecordsResponse":{"CommunicationRecords":[{"RecordType":"test","CreatedDate":"2023-10-01T15:20:45","CreatedBy":"test","LastModifiedDate":"2023-10-01T15:20:45","LastModifiedDateFormated":"10/01/2023 9:15 AM","LastModifiedBy":"test","Department":"OPD","CommunicationType":"test","ContactMethod":"test","ContactType":"test","ContactAttemptNo":"96","ContactResultOther":"other","Notes":"test","Details":"testyu"}],"TotalRecordsFound":"1"},"LettersResponse":{"LettersRecords":[{"LetterType":"test","AssociatedAuthID":"test","AssociatedCaseID":"test","CreatedBy":"test","CreatedDate":"2023-10-01T15:20:45","TemplateName":"test","Status":"test","FullfillmentStatus":"testt","P2POfferedinLetter":"rtett","LetterName":"test","VoidRemarks":"testt","LastModifiedBy":"test","LastModifiedDate":"2023-10-01T15:20:45","LastModifiedDateFormated":"10/01/2023 9:15 AM","FilePath":"test"}],"TotalRecordsFound":"1"}}';
             res.setBody(strjson);
             res.setStatusCode(200);
             return res;        
         }
    }

}