/******************************************************************************************************************************
Apex Class Name  : guidedFlowAuthSummary_LT_Hum
Version          : 1.0 
Created Date     : 24th Feb 2022
Function         : Test Class for guidedFlowAuthSummary_LC_Hum
Test Class       : 
Modification Log :
 *  Developer              Code Review         Date                 Description
 * ------------------------------------------------------------------------------------------------------------------------------                
 *  Pallavi Shewale                           02/24/2022            Original Version     
 *  Pinky Vijur                               08/03/2022            User Story 3648522: T1PRJ0533784 - CR7/MF 10 - Procedure Code Search - Test Class Improvements
********************************************************************************************************************************/

@isTest
private class  guidedFlowAuthSummary_LT_Hum{
    @testSetup  static void setUpTestData() {
        User objUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert objUser;
        HUM_Webservice_Callout__c serviceDetails = new HUM_Webservice_Callout__c(Name = 'CheckAuthRequired',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c =' https://dev-crmservicews.humana.com/CheckAuthRequired/IsAuthRequired' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
        insert serviceDetails;
        Template__c temp = new Template__c(Description__c ='Authorization/Referral Requirements');
        insert temp;
        //Template field
        Template_Field__c oTemplateField = new Template_Field__c(Name= 'SubmissionData', Data_Type__c= 'String',Template__c = temp.Id );
        insert oTemplateField ;
        system.runAs(objUser)
        {
            // Insert Member Account
            Account objMemAccount = HUMTestDataHelperTest.getAccount();
            objMemAccount.FirstName = 'Test FName';
            objMemAccount.LastName = 'LName'; 
            objMemAccount.source_platform_code__c = 'EM' ;
            objMemAccount.Enterprise_Id__c = String.valueof(2000);
            objMemAccount.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            objMemAccount.Birthdate__c = String.valueOf(System.today().month()+'/'+system.today().day()+'/'+System.today().year());
            insert objMemAccount;
            system.assertEquals(objMemAccount.LastName == 'LName', true);
            // Policy
            Policy__c oMedicalPolicy = HUMTestDataHelperTest.getPolicy();
            oMedicalPolicy.Name = 'Medical Product';
            oMedicalPolicy.source_cust_cov_key__c = '12345678901234567890' ; 
            oMedicalPolicy.Group_Name__c = objMemAccount.Id;
            oMedicalPolicy.Major_LOB__c ='EPO';          
            insert oMedicalPolicy;
            system.assertEquals(oMedicalPolicy.Name == 'Medical Product', true);
            // Policy Member
            Policy_Member__c objMedPolMem = HUMTestDataHelperTest.getPolicyMember();
            objMedPolMem.Member__c = objMemAccount.Id;
            objMedPolMem.Policy__c = oMedicalPolicy.Id;
            objMedPolMem.Relationship__c = 'self';    
            objMedPolMem.end_date__c = '12/31/2015'; 
            objMedPolMem.Effective_Date__c='01/01/2015';       
            objMedPolMem.Policy_Member_External_ID__c = '00|ODS|123456789|89|89|02-01-2001';
            insert objMedPolMem;
            system.assertEquals(objMedPolMem.Relationship__c == 'self',true);
            
             List<Humana_Codes_Desc__c> listOfHumanaCodesDesc = new List<Humana_Codes_Desc__c>();  
                Humana_Codes_Desc__c humCode = new Humana_Codes_Desc__c();
                humCode.Code_Set__c='wewewe';
                humCode.Description__c='Add to pharmacy lifetime maximum';
                humCode.Name='C-2127';
                humCode.Status__c='A';
                humCode.Code_External_ID__c = 'ExternalID test';
                insert humCode;

        }

       
    }
    
    static testMethod void testDTOResponse() {
     
     User testUser = [Select Id, Name from User where LastName='test User1' limit 1];
        System.runAs(testUser ) {                    
        Test.startTest();
        string result = guidedFlowAuthSummary_LC_Hum.searchDetails('pharmacy');
        Boolean bDataExist =  result.containsIgnoreCase('pharmacy') ? true : false;

            system.assertEquals(bDataExist,false);
        Test.stopTest(); 
        }
    }
       static testMethod void searchDetailsTest3(){
       
       User testUser = [Select Id, Name from User where LastName='test User1' limit 1];
        System.runAs(testUser ) {
        Humana_Codes_Desc__c humCode = new Humana_Codes_Desc__c();
        humCode.Code_Set__c='CPT';
        humCode.Description__c='Add to pharmacy lifetime maximum';
        humCode.Name='TestHum';
        humCode.Status__c='A';
        humCode.Code_External_ID__c = 'ExternalIDD test';
        insert humCode; 
        
        string[] SearchStrings = new string[1];
        SearchStrings[0] = humCode.Id;
        test.setFixedSearchResults(SearchStrings);
        
        
        
            Test.startTest();
            string result = guidedFlowAuthSummary_LC_Hum.searchDetails('TestHum');
             Boolean bDataExist =  result.containsIgnoreCase('pharmacy') ? true : false;

            system.assertEquals(bDataExist,true);

            Test.stopTest();
            }
         
        }
          static testMethod void testflow()
          {
            User testUser = [Select Id, Name from User where LastName='test User1' limit 1];
              System.runAs(testUser ) {
               Test.startTest();
               string result =  guidedFlowAuthSummary_LC_Hum.getSID('this is a test');
               system.assertEquals(result != null,true);
               Test.stopTest();
               }
          }
       
    Static testMethod void testinvokeAuthRefRequest(){
        User testUser = [Select Id, Name from User where LastName='test User1' limit 1];
     policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
     String poliID = policyObj.id;

       System.runAs(testUser){
             Test.startTest();             
            Continuation continueObj;
            Httpresponse resCheckAuth;
            String reqType;
            string sObj =  '{"IsAuthRequiredRequest":{"Member":{"MemberID":"H83577289","DOB":"03/31/1939","LastName":null,"FirstName":null},"Provider":[{"NPI":"1659395838","TaxID":null,"ProviderID":null}],"ServiceDetails":{"ProcedureCodes":[{"Code":"43280"},{"Code":"47135"},{"Code":"73721"},{"Code":"86335"}],"DateOfService":"01/05/2022"},"ContactDetails":{"ContactName":"Jill Williams","ContactPhoneNumber":"5025801000"}}}' ;
            continueObj = (Continuation)guidedFlowAuthSummary_LC_Hum.invokeAuthRefRequest(poliID,sObj );
            continueObj.continuationMethod = 'parseAuthRefResponse';
            continueObj.state = '1070177218_RXK4284_0015C000002T9uJ';
            resCheckAuth = new Httpresponse ();
            String EditRes = '{ "IsAuthRequiredResponse":{ "TransactionDetails":{ "Application":"CRM", "TransactionTs":"2022-02-24T08:42:13.866682-05:00", "SessionID":"72df79e3-fe57-4395-9f24-28db8fddbe3f", "Environment":"Test", "AuthorizationType":"Outpatient", "TotalResponseTime":"1580" }, "MemberDetails":{ "MemberID":"H83577289", "DOB":"1939-03-31T00:00:00", "LastName":"DEUINK", "FirstName":"GOGA", "MiddleName":"U", "Gender":"M" }, "ServiceDetails":{ "ProcedureCodes":[ { "Code":"86335", "IsAuthRequired":"See Notes", "ProcedureCodeMessage":"" }, { "Code":"43280", "IsAuthRequired":"See Notes", "ProcedureCodeMessage":"" }, { "Code":"47135", "IsAuthRequired":"See Notes", "ProcedureCodeMessage":"" }, { "Code":"73721", "IsAuthRequired":"See Notes", "ProcedureCodeMessage":"" } ], "DateOfService":"2022-01-05T00:00:00", "PolicyLOB":"MEDICAL PRODUCTS", "CDRNumber":"", "ReferralNote":"", "RedirectionMessage":"Thanks for contacting Humana about this member. If Transplant care, call Humana at 866-421-5663. Otherwise, to complete this request call WellMed Network of FL at 877-757-4440 or fax request to 866-322-7276.", "IsAuthRequired":"", "QuestionnaireRequired":"false", "ClinicalDocumentsRequired":"false", "QNRMessage":"" }, "Message":"Thanks for contacting Humana about this member. If Transplant care, call Humana at 866-421-5663. Otherwise, to complete this request call WellMed Network of FL at 877-757-4440 or fax request to 866-322-7276." } }' ;
            resCheckAuth.setBody(EditRes);
            system.assert(resCheckAuth.getBody() == EditRes);
            reqType = 'Continuation-1';        
            List<string> labelGetmember = new List<string>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,resCheckAuth);
            Object result = guidedFlowAuthSummary_LC_Hum.parseAuthRefResponse(labelGetmember,continueObj);
            String Message = 'Thanks for contacting Humana about this member. If Transplant care, call Humana at 866-421-5663. Otherwise, to complete this request call WellMed Network of FL at 877-757-4440 or fax request to 866-322-7276.';
            // Boolean bDataExist =  Json.serialize(result).containsIgnoreCase('H83577289') ? true : false;
            System.assertEquals(Message, result);
            
            
             Test.stopTest();
          

        
        }
    
    
    }
    
     static testMethod void testinvokeAuthRefRequestNegative(){
        
        User testUser = [Select Id, Name from User where LastName='test User1' limit 1];
        policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        String poliID = policyObj.id;
         
        System.runAs(testUser){
            Test.startTest();
            Continuation continueObj;
            Httpresponse resCheckAuth;
            String reqType;
            string sObj =  '{"IsAuthRequiredRequest":{"Member":{"MemberID":"H83577289","DOB":"03/31/1939","LastName":null,"FirstName":null},"Provider":[{"NPI":"1659395838","TaxID":null,"ProviderID":null}],"ServiceDetails":{"ProcedureCodes":[{"Code":"43280"},{"Code":"47135"},{"Code":"73721"},{"Code":"86335"}],"DateOfService":"01/05/2022"},"ContactDetails":{"ContactName":"Jill Williams","ContactPhoneNumber":"5025801000"}}}' ;
            continueObj = (Continuation)guidedFlowAuthSummary_LC_Hum.invokeAuthRefRequest(poliID,sObj );
            continueObj.continuationMethod = 'parseAuthRefResponse';
            continueObj.state = '1070177218_RXK4284_0015C000002T9uJ';
            resCheckAuth = new Httpresponse ();
            String EditRes = '{ "IsAuthRequiredResponse":';
            resCheckAuth.setBody(EditRes);
            system.assert(resCheckAuth.getBody() == EditRes);
            reqType = 'Continuation-1';        
            List<string> labelGetmember = new List<string>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,resCheckAuth);
            Object result = guidedFlowAuthSummary_LC_Hum.parseAuthRefResponse(labelGetmember,continueObj);
            Boolean bDataExist = Json.serialize(result).containsIgnoreCase('H83577289') ? true : false;
            System.assertEquals(False, bDataExist);
             String Message = 'Authorization requirements were unable to be determined. See your leader for assistance.';

            System.assertEquals(Message, result);
            Test.stopTest();
        }
        
    }

     Static testMethod void testinvokeAuthProcedureCodeMessageRequest()
     {
        User testUser = [Select Id, Name from User where LastName='test User1' limit 1];
        policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        String poliID = policyObj.id;
           System.runAs(testUser){
             Test.startTest();             
            Continuation continueObj;
            Httpresponse resCheckAuth;
            String reqType;
            string sObj =  '{"IsAuthRequiredRequest":{"Member":{"MemberID":"590060508","DOB":"08/24/1958"},"ServiceDetails":{"DateOfService":"07/27/2022","ProcedureCodes":[{"Code":"50551"}]},"ContactDetails":{"ContactPhoneNumber":"(361) 118-2676","ContactName":"LEXUIS G DIMARZIO"},"Provider":[{"ProviderType":"Requesting","NPI":"1427096122","TaxID":"111222333","ProviderID":""}]}}';
            continueObj = (Continuation)guidedFlowAuthSummary_LC_Hum.invokeAuthRefRequest(poliID,sObj );
            continueObj.continuationMethod = 'parseAuthRefResponse';
            continueObj.state = '1070177218_RXK4284_0015C000002T9uJ';
            resCheckAuth = new Httpresponse ();
            String EditRes = '{ "IsAuthRequiredResponse":{ "TransactionDetails":{ "Application":"CRM", "TransactionTs":"2022-07-27T09:10:43.0423051-04:00", "SessionID":"e3e2aaf7-6af1-4983-bb97-9ee274d50c5e", "Environment":"INT", "AuthorizationType":"Outpatient", "TotalResponseTime":"1471" }, "MemberDetails":{ "MemberID":"590060508", "DOB":"1958-08-24T00:00:00", "LastName":"DIMARZIO", "FirstName":"LEXUIS", "MiddleName":"G", "Gender":"M" }, "ServiceDetails":{ "ProcedureCodes":[ { "Code":"50551", "IsAuthRequired":"Y", "ProcedureCodeMessage":"" } ], "DateOfService":"2022-07-27T00:00:00", "PolicyLOB":"MEDICAL PRODUCTS", "CDRNumber":"", "ReferralNote":"", "RedirectionMessage":" ", "IsAuthRequired":"", "QuestionnaireRequired":"false", "ClinicalDocumentsRequired":"false", "QNRMessage":"" }, "Message":"" } }' ;
            resCheckAuth.setBody(EditRes);
            system.assert(resCheckAuth.getBody() == EditRes);
            reqType = 'Continuation-1';        
            List<string> labelGetmember = new List<string>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,resCheckAuth);
            Object result = guidedFlowAuthSummary_LC_Hum.parseAuthRefResponse(labelGetmember,continueObj);
            String Message = 'Authorization is Required';
            //Boolean bDataExist =  Json.serialize(result).containsIgnoreCase('H83577289') ? true : false;
            System.assertEquals(Message, result);
             Test.stopTest();
        }
     }

     Static testMethod void testinvokeAuthProcedureCodeMessageNRequest()
     {
        User testUser = [Select Id, Name from User where LastName='test User1' limit 1];
        policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        String poliID = policyObj.id;

       System.runAs(testUser){
             Test.startTest();             
            Continuation continueObj;
            Httpresponse resCheckAuth;
            String reqType;
            string sObj =  '{"IsAuthRequiredRequest":{"Member":{"MemberID":"H34305826","DOB":"03/24/2008"},"ServiceDetails":{"DateOfService":"07/18/2022","ProcedureCodes":[{"Code":"50551"}]},"ContactDetails":{"ContactPhoneNumber":"(414) 913-6177","ContactName":"KEISHA A F WYROSDICK"},"Provider":[{"ProviderType":"Requesting","NPI":"1427096122","TaxID":"111222333","ProviderID":""}]}}';
            continueObj = (Continuation)guidedFlowAuthSummary_LC_Hum.invokeAuthRefRequest(poliID,sObj );
            continueObj.continuationMethod = 'parseAuthRefResponse';
            continueObj.state = '1070177218_RXK4284_0015C000002T9uJ';
            resCheckAuth = new Httpresponse ();
            String EditRes = '{ "IsAuthRequiredResponse":{"TransactionDetails":{ "Application":"CRM", "TransactionTs":"2022-07-18T06:39:10.8780621-04:00","SessionID":"670f3b61-cab0-4efd-b382-19926e73c523", "Environment":"INT","AuthorizationType":"Outpatient", "TotalResponseTime":"814" },"MemberDetails":{ "MemberID":"H34305826", "DOB":"2008-03-24T00:00:00","LastName":"WYROSDICK", "FirstName":"KEISHA A", "MiddleName":"F", "Gender":"M" }, "ServiceDetails":{ "ProcedureCodes":[ { "Code":"50551", "IsAuthRequired":"N", "ProcedureCodeMessage":"" } ], "DateOfService":"2022-07-18T00:00:00", "PolicyLOB":"MEDICAL PRODUCTS", "CDRNumber":"", "ReferralNote":"", "RedirectionMessage":" ", "IsAuthRequired":"", "QuestionnaireRequired":"false","ClinicalDocumentsRequired":"false", "QNRMessage":"" }, "Message":"" } }';
            resCheckAuth.setBody(EditRes);
            system.assert(resCheckAuth.getBody() == EditRes);
            reqType = 'Continuation-1';        
            List<string> labelGetmember = new List<string>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,resCheckAuth);
            Object result = guidedFlowAuthSummary_LC_Hum.parseAuthRefResponse(labelGetmember,continueObj);
            String Message = 'Authorization is NOT Required';
            //Boolean bDataExist =  Json.serialize(result).containsIgnoreCase('H83577289') ? true : false;
            System.assertEquals(Message, result);
             Test.stopTest();        
        }    
    }
    Static testMethod void testinvokeAuthIsNotDeterminedRequest()
    {
        User testUser = [Select Id, Name from User where LastName='test User1' limit 1];
        policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        String poliID = policyObj.id;

       System.runAs(testUser){
             Test.startTest();             
            Continuation continueObj;
            Httpresponse resCheckAuth;
            String reqType;
            string sObj =  '{"IsAuthRequiredRequest":{"Member":{"MemberID":"670843823","DOB":"07/16/1971"},"ServiceDetails":{"DateOfService":"07/28/2022","ProcedureCodes":[{"Code":"32097"}]},"ContactDetails":{"ContactPhoneNumber":"(865) 976-3273","ContactName":"RHONICA COENEN"},"Provider":[{"ProviderType":"Requesting","NPI":"1427096122","TaxID":"111222333","ProviderID":""}]}}';
            continueObj = (Continuation)guidedFlowAuthSummary_LC_Hum.invokeAuthRefRequest(poliID,sObj );
            continueObj.continuationMethod = 'parseAuthRefResponse';
            continueObj.state = '1070177218_RXK4284_0015C000002T9uJ';
            resCheckAuth = new Httpresponse ();
            String EditRes = '{ "IsAuthRequiredResponse":{"TransactionDetails":{ "Application":"CRM", "TransactionTs":"2022-07-18T06:39:10.8780621-04:00","SessionID":"670f3b61-cab0-4efd-b382-19926e73c523", "Environment":"INT","AuthorizationType":"Outpatient", "TotalResponseTime":"814" },"MemberDetails":{ "MemberID":"H34305826", "DOB":"2008-03-24T00:00:00","LastName":"WYROSDICK", "FirstName":"KEISHA A", "MiddleName":"F", "Gender":"M" }, "ServiceDetails":{ "ProcedureCodes":[ { "Code":"50551", "IsAuthRequired":"", "ProcedureCodeMessage":"" } ], "DateOfService":"2022-07-18T00:00:00", "PolicyLOB":"MEDICAL PRODUCTS", "CDRNumber":"", "ReferralNote":"", "RedirectionMessage":" ", "IsAuthRequired":"", "QuestionnaireRequired":"false","ClinicalDocumentsRequired":"false", "QNRMessage":"" }, "Message":"" } }';
            resCheckAuth.setBody(EditRes);
            system.assert(resCheckAuth.getBody() == EditRes);
            reqType = 'Continuation-1';        
            List<string> labelGetmember = new List<string>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,resCheckAuth);
            Object result = guidedFlowAuthSummary_LC_Hum.parseAuthRefResponse(labelGetmember,continueObj);
            String Message = 'Authorization requirements were unable to be determined. See your leader for assistance.';
            //Boolean bDataExist =  Json.serialize(result).containsIgnoreCase('H83577289') ? true : false;
            System.assertEquals(Message, result);            
            Test.stopTest();
        }
      }
     static testMethod void testRequestAuthRefRequestService(){
        User testUser = [Select Id, Name from User where LastName='test User1' limit 1];
        system.runAs(testUser)
        { 
            Test.StartTest();
            AuthRefRequest_DTO_HUM objAuthRefRequest = new AuthRefRequest_DTO_HUM();
            AuthRefRequest_DTO_HUM.Isauthrequired objIsAuthRequired = new AuthRefRequest_DTO_HUM.Isauthrequired();
            AuthRefRequest_DTO_HUM.MemberDetail objMemberDetailReq = new AuthRefRequest_DTO_HUM.MemberDetail();
            AuthRefRequest_DTO_HUM.Servicedetails objServiceDetailsReq = new AuthRefRequest_DTO_HUM.Servicedetails();
            AuthRefRequest_DTO_HUM.Procedurecode objProcedurecodeReq = new AuthRefRequest_DTO_HUM.Procedurecode();
            AuthRefRequest_DTO_HUM.Contactdetails objContactDetails = new AuthRefRequest_DTO_HUM.Contactdetails();
            List<AuthRefRequest_DTO_HUM.Procedurecode> procedureCodeReqList = new List<AuthRefRequest_DTO_HUM.Procedurecode>();
            List<AuthRefRequest_DTO_HUM.Provider> providerList = new List<AuthRefRequest_DTO_HUM.Provider>();
            AuthRefRequest_DTO_HUM.Provider objProvider = new AuthRefRequest_DTO_HUM.Provider();
            objMemberDetailReq.MemberID = 'H34305826';
            objMemberDetailReq.DOB = '03/24/2008';
            objServiceDetailsReq.DateOfService = '07/18/2022';
            objProcedurecodeReq.Code = '50551';
            procedureCodeReqList.add(objProcedurecodeReq);
            objServiceDetailsReq.ProcedureCodes = procedureCodeReqList;
            objContactDetails.ContactPhoneNumber = '(414) 913-6177';
            objContactDetails.ContactName = 'KEISHA A F WYROSDICK';
            objProvider.ProviderType = 'Requesting';
            objProvider.NPI = '1427096122';
            objProvider.TaxID = '111222333';
            objProvider.ProviderID = '';
            providerList.add(objProvider);
            objIsAuthRequired.Member = objMemberDetailReq;
            objIsAuthRequired.Provider = providerList;
            objIsAuthRequired.ServiceDetails = objServiceDetailsReq;
            objIsAuthRequired.ContactDetails = objContactDetails;
            objAuthRefRequest.IsAuthRequiredRequest = objIsAuthRequired;
            System.assertNotEquals(null, objAuthRefRequest);
            Test.stopTest();
            
        }
    }
     static testMethod void testResponseCheckAuthService(){
        User testUser = [Select Id, Name from User where LastName='test User1' limit 1];
        system.runAs(testUser)
        { 
            Test.StartTest();
            AuthRefResponse_DTO_HUM objAuthRefResponse = new AuthRefResponse_DTO_HUM();
            AuthRefResponse_DTO_HUM.Isauthrequiredresponse objIsAuthRequiredResponse = new AuthRefResponse_DTO_HUM.Isauthrequiredresponse();
           // AddressStandardizeResponse_DTO_HUM objAddressStandardize = new AddressStandardizeResponse_DTO_HUM();
            AuthRefResponse_DTO_HUM.Transactiondetails objTransactiondetails = new AuthRefResponse_DTO_HUM.Transactiondetails();
            AuthRefResponse_DTO_HUM.Memberdetails objMemberDetails = new AuthRefResponse_DTO_HUM.Memberdetails();
            AuthRefResponse_DTO_HUM.Procedurecode objProcedurecode = new AuthRefResponse_DTO_HUM.Procedurecode();
            AuthRefResponse_DTO_HUM.Servicedetails objServiceDetails = new AuthRefResponse_DTO_HUM.Servicedetails();
            List<AuthRefResponse_DTO_HUM.Procedurecode> objProcedureCodeList = new List<AuthRefResponse_DTO_HUM.Procedurecode>();
            objTransactiondetails.Application = 'CRM';
            objTransactiondetails.TransactionTs = '2022-02-24T08:42:13.866682-05:00';
            objTransactiondetails.SessionID = '72df79e3-fe57-4395-9f24-28db8fddbe3f';
            objTransactiondetails.Environment = 'Test';
            objTransactiondetails.AuthorizationType ='Outpatient';
            objTransactiondetails.TotalResponseTime = '1580';
            objMemberDetails.MemberID = 'H83577289';
            objMemberDetails.DOB = '1939-03-31T00:00:00';
            objMemberDetails.FirstName = 'DEUINK';
            objMemberDetails.LastName = 'GOGA';
            objMemberDetails.MiddleName = 'U';
            objMemberDetails.Gender = 'M';
            objServiceDetails.DateOfService = '2022-01-05T00:00:00';
            objServiceDetails.PolicyLOB = 'MEDICAL PRODUCTS';
            objServiceDetails.RedirectionMessage = 'Thanks for contacting Humana about this member. If Transplant care, call Humana at 866-421-5663. Otherwise, to complete this request call WellMed Network of FL at 877-757-4440 or fax request to 866-322-7276.';
            objServiceDetails.CDRNumber = '';
            objServiceDetails.ReferralNote = '';
            objServiceDetails.IsAuthRequired = '';
            objServiceDetails.QuestionnaireRequired = 'false';
            objServiceDetails.ClinicalDocumentsRequired = 'false';
            objServiceDetails.QNRMessage = '';
            objProcedurecode.Code = '86335';
            objProcedurecode.IsAuthRequired = 'See Notes';
            objProcedurecode.ProcedureCodeMessage = '';
            objProcedureCodeList.add(objProcedurecode);
            objIsAuthRequiredResponse.Message = 'Thanks for contacting Humana about this member. If Transplant care, call Humana at 866-421-5663. Otherwise, to complete this request call WellMed Network of FL at 877-757-4440 or fax request to 866-322-7276.';
            objIsAuthRequiredResponse.TransactionDetails = objTransactiondetails;
            objIsAuthRequiredResponse.MemberDetails = objMemberDetails;
            objIsAuthRequiredResponse.ServiceDetails = objServiceDetails;
            objAuthRefResponse.IsAuthRequiredResponse = objIsAuthRequiredResponse;
            System.assertNotEquals(null,objAuthRefResponse);
            Test.stopTest();
        }
    }

}