/*****************************************************************************************
Apex class Name    : InquiryDetails_LT_HUM
Version            : 1.0
Created Date       : 03/24/2021 

Modification Log:
*   Developer                   Code Review             Date               Description
* ------------------------------------------------------------------------------------------------------------------------------   
*  Ritik Agarwal                                      03/24/2021          add method for InquiryNotes callout
*  Kajal Namdev                                       05/13/2021          Removed reference of policy object
*********************************************************************************************************************************/
@isTest
public with sharing class InquiryDetails_LT_HUM {
	/**
    * @testSetup method to create test data
    *<p>
    * @param            NULL
    * @return           void
    */
    @testSetup static void setUpTestData()
    {
        HUM_Webservice_Callout__c inquiryDetailsService = new HUM_Webservice_Callout__c(Name = 'InquiryDetailsService',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c = 'https://int-crmservicews.humana.com/CCPGCPDataRetention/InquiryDetails' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json');
        insert inquiryDetailsService;
        HUM_Webservice_Callout__c inquiryNotesService = new HUM_Webservice_Callout__c(Name = 'InquiryNotesService',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c = 'https://int-crmservicews.humana.com/CCPGCPDataRetention/NotesDetails' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
        insert inquiryNotesService;
        HUM_Webservice_Callout__c inquiryAuditTrailService = new HUM_Webservice_Callout__c(Name = 'InquiryAuditTrailService',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c = 'https://int-crmservicews.humana.com/CCPGCPDataRetention/AuditDetails' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json');
        insert inquiryAuditTrailService;
        HUM_Webservice_Callout__c inquiryDetailAttachmentService = new HUM_Webservice_Callout__c(Name = 'InquiryDetailAttachmentService',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c = 'https://int-crmservicews.humana.com/CCPGCPDataRetention/AttachmentDetails' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
        insert inquiryDetailAttachmentService;
        HUM_Webservice_Callout__c inquiryWebResponseService = new HUM_Webservice_Callout__c(Name = 'InquiryWebResponseService',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c = 'https://int-crmservicews.humana.com/CCPGCPDataRetention/WebResponse' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json');
        insert inquiryWebResponseService;
        HUM_Webservice_Callout__c inquiryTaskDetailsService = new HUM_Webservice_Callout__c(Name = 'InquiryDetailsTaskService',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c = 'https://int-crmservicews.humana.com/CCPGCPDataRetention/TaskDetails' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json');
        insert inquiryTaskDetailsService;
		
        System.assertEquals(inquiryNotesService.Name == 'InquiryNotesService',true);
        
 
            // Insert Member Account
            Account objMemAccount = HUMTestDataHelperTest.getAccount();
            objMemAccount.FirstName = 'Test FName';
            objMemAccount.LastName = 'LName'; 
            objMemAccount.source_platform_code__c = 'EM' ;
            objMemAccount.Enterprise_Id__c = String.valueof(2000);
            objMemAccount.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            objMemAccount.Birthdate__c = String.valueOf(System.today().month()+'/'+System.today().day()+'/'+System.today().year());
            insert objMemAccount;
            System.assertEquals(objMemAccount.LastName == 'LName', true);
            
            //Product
            Product2 oproduct = new Product2();
            oproduct.Name='MED';
            oproduct.Product_Type__c='MA';           
            insert oproduct;
            System.assertEquals(oproduct.Name == 'MED', true);
            
            
            Product2 oproduct2 = new Product2();
            oproduct2.Name='DEN';
            oproduct2.Product_Type__c='CA';           
            insert oproduct2;
            System.assertEquals(oproduct2.Name == 'DEN',true);
            
            
    } 
    
   /**
    * Positive test method for Inquiry Details funtionality.
    *<p>
    * @param            NULL
    * @return           void
    */
    static testMethod void testgetInquiryDetails()
    {
        //policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        Account accObj = new Account();
        accObj.Name = 'Test Account';
        insert accObj;
        String accID = accObj.Id;

            Test.startTest();
            InquiryDetails_LC_HUM inquiryDetails = new InquiryDetails_LC_HUM();
            InquiryDetails_S_HUM oServiceInquirySummary = new InquiryDetails_S_HUM();
            Continuation continueObj = (Continuation)InquiryDetails_LC_HUM.getInquiryDetails('1014741145','100210');
            continueObj.continuationMethod = 'parseInquiryDetails';
            continueObj.state = 'Both';
            HttpResponse httpRes = new Httpresponse ();
            String resBodyString= '{"DRInquiryDetails":{ "CONTACT_ID":"984830342708", "CREATED_BY":"WRKFLOW ", "CREATED_TEAM_NAME":"LCAS FE MED PDP", "CREATED_DEPT_NAME":"Argus FE Med ", "CREATED_SITE_NAME":"Louisville ", "CATEGORY_CD_DESC":"Disregard", "DISPOSITION_DESC":"Disregard", "DateOfService":"", "ResponseShortText":"", "PRIORITY_CD_DESC":"Normal", "OWNER_SITE_NAME":"Phoenix ", "OWNER_DEPT_NAME":"RightSource RX ", "OWNER_TEAM_NAME":"Retail to Mail ", "DCN":"191202803100 ", "CREATED_TS":"2019-12-03 08:27:34.992294", "OWNER_FIRST_NAME":"Cheryl", "OWNER_LAST_NAME":"Beyer" }}';
            httpRes.setBody(resBodyString);
            System.assert(httpRes.getBody() == resBodyString);
            Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
            string reqType = '';
            for(String s : mapDetails.keyset())
            {
                reqType = s;
            } 
            List<string>labelGetmember = new List<string>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,httpRes);
            InquiryDetails_LC_HUM.parseInquiryDetails(labelGetmember,continueObj);
            Test.stopTest();
       
    }
    
    /**
    * Negative test method for Inquiry Single Reference funtionality.
    *<p>
    * @param            NULL
    * @return           void
    */
  static testMethod void testNegInquiryDetails()
    {
        Account accObj = new Account();
        accObj.Name = 'Test Account';
        insert accObj;
        String accID = accObj.Id;

            Test.startTest();
            InquiryDetails_LC_HUM inquirySummary = new InquiryDetails_LC_HUM();
            InquiryDetails_S_HUM oServiceInquirySummary = new InquiryDetails_S_HUM();
            Continuation continueObj = (Continuation)InquiryDetails_LC_HUM.getInquiryDetails('1014741145','100210');
            continueObj.continuationMethod = 'parseInquiryDetails';
            continueObj.state = 'Both';
            HttpResponse httpRes = new Httpresponse ();
            String resBodyString = '';
            httpRes.setBody(resBodyString);
            System.assert(httpRes.getBody() == resBodyString);
            Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
            string reqType = '';
            for(String s : mapDetails.keyset())
            {
                reqType = s;
            } 
            List<string>labelGetmember = new List<string>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,httpRes);
            InquiryDetails_LC_HUM.parseInquiryDetails(labelGetmember,continueObj);
            Test.stopTest();
    }
    
     /**
    * Positive test method for Inquiry Notes funtionality.
    *<p>
    * @param            NULL
    * @return           void
    */
  static testMethod void testInquiryNotes()
    {
        Account accObj = new Account();
        accObj.Name = 'Test Account';
        insert accObj;
        String accID = accObj.Id;

            Test.startTest();

            InquiryDetails_LC_HUM inquirySummary = new InquiryDetails_LC_HUM();
            InquiryDetails_S_HUM oServiceInquirySummary = new InquiryDetails_S_HUM();
            Continuation continueObj = (Continuation)InquiryDetails_LC_HUM.getInquiryNotes('1014741145','100210');
            continueObj.continuationMethod = 'parseInquiryNotes';
            continueObj.state = 'Both';
            HttpResponse httpRes = new Httpresponse ();
            String resBodyString= '{"DRNotes":[ { "NOTE_DESC":"cad2451 -06/17/2020-Closing as inquiries are created as duplicates. No impactto Humana. ", "Task":"Inquiry" }, { "NOTE_DESC":"JXF7823 -12/22/2019-dcn# 191202803100 provider Lynmoore Lawnwood Assisted Living and Memory Care / ADNAN SHARIFF DPM PLLC submitted health assessment regarding review for re admission of mbr ------------------- Transf to provider corr per corr submitted by providers not handled by mbr corr ", "Task":"Inquiry" }, { "NOTE_DESC":"MXC8491 -12/06/2019-MA MEMBER ", "Task":"Inquiry" }, { "NOTE_DESC":"EHL4734 -12/04/2019-This is not a DMR request ", "Task":"Inquiry" }, { "NOTE_DESC":"EHL4734 -12/04/2019-This is not a DMR request ", "Task":"Inquiry"}]}';
            httpRes.setBody(resBodyString);
            System.assert(httpRes.getBody() == resBodyString);
            Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
            string reqType = '';
            for(String s : mapDetails.keyset())
            {
                reqType = s;
            } 
            List<string>labelGetmember = new List<string>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,httpRes);
            InquiryDetails_LC_HUM.parseInquiryNotes(labelGetmember,continueObj);
            Continuation continueObj2 = (Continuation)InquiryDetails_LC_HUM.getInquiryNotes(null,null);
            continueObj2.continuationMethod = 'parseInquiryNotes';
            continueObj2.state = 'Both';
            HttpResponse httpRes2 = new Httpresponse ();
            String resBodyString2= '{"DRNotes":[ { "NOTE_DESC":"cad2451 -06/17/2020-Closing as inquiries are created as duplicates. No impactto Humana. ", "Task":"Inquiry" }, { "NOTE_DESC":"JXF7823 -12/22/2019-dcn# 191202803100 provider Lynmoore Lawnwood Assisted Living and Memory Care / ADNAN SHARIFF DPM PLLC submitted health assessment regarding review for re admission of mbr ------------------- Transf to provider corr per corr submitted by providers not handled by mbr corr ", "Task":"Inquiry" }, { "NOTE_DESC":"MXC8491 -12/06/2019-MA MEMBER ", "Task":"Inquiry" }, { "NOTE_DESC":"EHL4734 -12/04/2019-This is not a DMR request ", "Task":"Inquiry" }, { "NOTE_DESC":"EHL4734 -12/04/2019-This is not a DMR request ", "Task":"Inquiry"}]}';
            httpRes2.setBody(resBodyString2);
            System.assert(httpRes2.getBody() == resBodyString2);
            Map<String, HTTPRequest> mapDetails2 = continueObj2.getRequests();
            string reqType2 = '';
            for(String s : mapDetails2.keyset())
            {
                reqType2 = s;
            } 
            List<string>labelGetmember2 = new List<string>();
            labelGetmember2.add(reqType2);
            Test.setContinuationResponse(reqType2,httpRes2);
            InquiryDetails_LC_HUM.parseInquiryNotes(labelGetmember2, continueObj2);
            Test.stopTest();
    }
    
     /**
    * Positive test method for Inquiry Notes funtionality.
    *<p>
    * @param            NULL
    * @return           void
    */
  static testMethod void testInquiryAuditDetail()
    {
        Account accObj = new Account();
        accObj.Name = 'Test Account';
        insert accObj;
        String accID = accObj.Id;

            Test.startTest();
            InquiryDetails_LC_HUM inquirySummary = new InquiryDetails_LC_HUM();
            InquiryDetails_S_HUM oServiceInquirySummary = new InquiryDetails_S_HUM();
            Continuation continueObj = (Continuation)InquiryDetails_LC_HUM.getInquiryAudit('1014741145','100210');
            continueObj.continuationMethod = 'parseInquiryAudit';
            continueObj.state = 'Both';
            HttpResponse httpRes = new Httpresponse ();
            String resBodyString= '{"DRAuditDetails": [{"ACTION_TYPE_DESC": "Status Change","Modified On": "05/24/2010","Modified By": "IQ669 ","PREVIOUS_DESC": "FROM STATUS Open ","CURRENT_DESC": "TO STATUS Closed "}]}';
            httpRes.setBody(resBodyString);
            System.assert(httpRes.getBody() == resBodyString);
            Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
            string reqType = '';
            for(String s : mapDetails.keyset())
            {
                reqType = s;
            } 
            List<string>labelGetmember = new List<string>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,httpRes);
            InquiryDetails_LC_HUM.parseInquiryAudit(labelGetmember,continueObj);
            Continuation continueObj2 = (Continuation)InquiryDetails_LC_HUM.getInquiryAudit(null,null);
            continueObj2.continuationMethod = 'parseInquiryAudit';
            continueObj2.state = 'Both';
            HttpResponse httpRes2 = new Httpresponse ();
            String resBodyString2= '{"DRAuditDetails": [{"ACTION_TYPE_DESC": "Status Change","Modified On": "05/24/2010","Modified By": "IQ669 ","PREVIOUS_DESC": "FROM STATUS Open ","CURRENT_DESC": "TO STATUS Closed "}]}';
            httpRes2.setBody(resBodyString2);
            System.assert(httpRes2.getBody() == resBodyString2);
            Map<String, HTTPRequest> mapDetails2 = continueObj2.getRequests();
            string reqType2 = '';
            for(String s : mapDetails2.keyset())
            {
                reqType2 = s;
            } 
            List<string>labelGetmember2 = new List<string>();
            labelGetmember2.add(reqType2);
            Test.setContinuationResponse(reqType2,httpRes2);
            InquiryDetails_LC_HUM.parseInquiryAudit(labelGetmember2, continueObj2);
            Test.stopTest();
    }
    
    /**
    * Positive test method for Inquiry Attachment funtionality.
    *<p>
    * @param            NULL
    * @return           void
    */
  static testMethod void testgetInquiryDetailAttachment()
    {
        Account accObj = new Account();
        accObj.Name = 'Test Account';
        insert accObj;
        String accID = accObj.Id;

            Test.startTest();

            InquiryDetails_LC_HUM inquirySummary = new InquiryDetails_LC_HUM();
            InquiryDetails_S_HUM oServiceInquirySummary = new InquiryDetails_S_HUM();
            Continuation continueObj = (Continuation)InquiryDetails_LC_HUM.getInquiryAttachment('1014741145','100210');
            continueObj.continuationMethod = 'parseInquiryAttachment';
            continueObj.state = 'Both';
            HttpResponse httpRes = new Httpresponse ();
            String resBodyString= '{ "DRAttachmentDetails":[ { "ATTACH_TYPE_DESC":"Document", "ATTACH_TYPE":"DO", "DocId":"1667139", "Version_String":"1 ", "ATTACH_DESC":[ { "Field":"FileName", "Value":"text" }, { "Field":"Versionstring", "Value":"2" }, { "Field":"UserID", "Value":"TRN0567 " } ] },{ "ATTACH_TYPE_DESC":"Alert", "ATTACH_TYPE":"DR", "DocId":"1237139", "Version_String":"1 ", "ATTACH_DESC":[ { "Field":"FileName", "Value":"text1" }, { "Field":"Versionstring", "Value":"3" }, { "Field":"UserID", "Value":"sxN0567 " } ] } ] }';
            httpRes.setBody(resBodyString);
            System.assert(httpRes.getBody() == resBodyString);
            Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
            string reqType = '';
            for(String s : mapDetails.keyset())
            {
                reqType = s;
            } 
            List<string>labelGetmember = new List<string>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,httpRes);
            InquiryDetails_LC_HUM.parseInquiryAttachment(labelGetmember,continueObj);
            Continuation continueObj2 = (Continuation)InquiryDetails_LC_HUM.getInquiryAttachment(null,null);
            continueObj2.continuationMethod = 'parseInquiryAttachment';
            continueObj2.state = 'Both';
            HttpResponse httpRes2 = new Httpresponse ();
            String resBodyString2= '{ "DRAttachmentDetails":[ { "ATTACH_TYPE_DESC":"Document", "ATTACH_TYPE":"DO", "DocId":"1667139", "Version_String":"1 ", "ATTACH_DESC":[ { "Field":"FileName", "Value":"text" }, { "Field":"Versionstring", "Value":"2" }, { "Field":"UserID", "Value":"TRN0567 " } ] },{ "ATTACH_TYPE_DESC":"Alert", "ATTACH_TYPE":"DR", "DocId":"1237139", "Version_String":"1 ", "ATTACH_DESC":[ { "Field":"FileName", "Value":"text1" }, { "Field":"Versionstring", "Value":"3" }, { "Field":"UserID", "Value":"sxN0567 " } ] } ] }';
            httpRes2.setBody(resBodyString2);
            System.assert(httpRes2.getBody() == resBodyString2);
            Map<String, HTTPRequest> mapDetails2 = continueObj2.getRequests();
            string reqType2 = '';
            for(String s : mapDetails2.keyset())
            {
                reqType2 = s;
            } 
            List<string>labelGetmember2 = new List<string>();
            labelGetmember2.add(reqType2);
            Test.setContinuationResponse(reqType2,httpRes2);
            InquiryDetails_LC_HUM.parseInquiryAttachment(labelGetmember2, continueObj2);
            Test.stopTest();
    }
    
    /**
    * Positive test method for Inquiry Web Response funtionality.
    *<p>
    * @param            NULL
    * @return           void
    */
  static testMethod void testgetInquiryWebResponse()
    {
        Account accObj = new Account();
        accObj.Name = 'Test Account';
        insert accObj;
        String accID = accObj.Id;

            Test.startTest();
            InquiryDetails_LC_HUM inquirySummary = new InquiryDetails_LC_HUM();
            InquiryDetails_S_HUM oServiceInquirySummary = new InquiryDetails_S_HUM();
            Continuation continueObj = (Continuation)InquiryDetails_LC_HUM.getInquiryWebResponse('1014741145','100210');
            continueObj.continuationMethod = 'parseInquiryWebResponse';
            continueObj.state = 'Both';
            HttpResponse httpRes = new Httpresponse ();
            String resBodyString= '{"DRWebResponse": {" WebQuestion ": "what is inquiry"," Additional Details ": "more",ClaimNumber ": "123",ClaimDOS ": "2015-02-02",SelectAResponse ": "abc",WebResponse ": "additional"}}';
            httpRes.setBody(resBodyString);
            System.assert(httpRes.getBody() == resBodyString);
            Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
            string reqType = '';
            for(String s : mapDetails.keyset())
            {
                reqType = s;
            } 
            List<string>labelGetmember = new List<string>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,httpRes);
            InquiryDetails_LC_HUM.parseInquiryWebResponse(labelGetmember,continueObj);
            Continuation continueObj2 = (Continuation)InquiryDetails_LC_HUM.getInquiryWebResponse(null,null);
            continueObj2.continuationMethod = 'parseInquiryWebResponse';
            continueObj2.state = 'Both';
            HttpResponse httpRes2 = new Httpresponse ();
            String resBodyString2= '{"DRWebResponse": {" WebQuestion ": "what is inquiry"," Additional Details ": "more",ClaimNumber ": "123",ClaimDOS ": "2015-02-02",SelectAResponse ": "abc",WebResponse ": "additional"}}';
            httpRes2.setBody(resBodyString2);
            System.assert(httpRes2.getBody() == resBodyString2);
            Map<String, HTTPRequest> mapDetails2 = continueObj2.getRequests();
            string reqType2 = '';
            for(String s : mapDetails2.keyset())
            {
                reqType2 = s;
            } 
            List<string>labelGetmember2 = new List<string>();
            labelGetmember2.add(reqType2);
            Test.setContinuationResponse(reqType2,httpRes2);
            InquiryDetails_LC_HUM.parseInquiryWebResponse(labelGetmember2, continueObj2);
            Test.stopTest();
    }
    
    /**
    * test method to cover service Response DTO
    *<p>
    * @param            NULL
    * @return           void
    */
    @isTest static void testInquiryDetailsResponse_H_HUM()
    {

            Test.startTest();
            InquiryDetailsResponse_H_HUM oResponse = new InquiryDetailsResponse_H_HUM();
            InquiryDetailsResponse_H_HUM.DRInquiryDetails oInquiryDetails = new InquiryDetailsResponse_H_HUM.DRInquiryDetails();
            oInquiryDetails.CONTACT_ID = '10021222010';
        	oInquiryDetails.CREATED_BY = 'JOhn';
        	oInquiryDetails.CREATED_TEAM_NAME = '';
        	oInquiryDetails.CREATED_DEPT_NAME  = '';
        	oInquiryDetails.CATEGORY_CD_DESC = '';
        	oInquiryDetails.DISPOSITION_DESC = '';
        	oInquiryDetails.DateOfService = '';
        	oInquiryDetails.ResponseShortText = '';
        	oInquiryDetails.PRIORITY_CD_DESC = '';
        	oInquiryDetails.OWNER_SITE_NAME = '';
        	oInquiryDetails.OWNER_DEPT_NAME = '';
        	oInquiryDetails.OWNER_TEAM_NAME = '';
        	oInquiryDetails.DCN = '';
        	oInquiryDetails.CREATED_TS = '';
        	oInquiryDetails.OWNER_FIRST_NAME = '';
        	oInquiryDetails.OWNER_LAST_NAME = '';
        	oInquiryDetails.CREATED_SITE_NAME = '';
            System.assertEquals(oInquiryDetails.CONTACT_ID == '10021222010',true);
            Test.stopTest();
    }
    
    /**
    * test method to cover service Response DTO
    *<p>
    * @param            NULL
    * @return           void
    */
    @isTest static void testInquiryNotesResponse_H_HUM()
    {

            Test.startTest();
            InquiryNotesResponse_H_HUM oResponse = new InquiryNotesResponse_H_HUM();
            InquiryNotesResponse_H_HUM.DRNotes oInquiryNotes = new InquiryNotesResponse_H_HUM.DRNotes();
            oInquiryNotes.NOTE_DESC = 'Test';
            oInquiryNotes.Type = 'Task 1';
            System.assertEquals(oInquiryNotes.NOTE_DESC == 'Test',true);
            InquiryNotesWrapper_DTO_HUM oDRNotesWrap = new InquiryNotesWrapper_DTO_HUM();
            oDRNotesWrap.sType = 'InquiryTest';
            Test.stopTest();
       
    }
    
    /**
    * test method to cover service Response DTO
    *<p>
    * @param            NULL
    * @return           void
    */
    @isTest static void testInquiryWebResponse_H_HUM()
    {

            Test.startTest();
            InquiryWebResponse_Response_H_HUM oResponse = new InquiryWebResponse_Response_H_HUM();
            InquiryWebResponse_Response_H_HUM.DRWebResponse oInquiryNotes = new InquiryWebResponse_Response_H_HUM.DRWebResponse();
            oInquiryNotes.WebQuestion = 'Test';
            oInquiryNotes.ClaimNumber = 'Task 1';
            oInquiryNotes.ClaimDOS = 'Test';
            oInquiryNotes.SelectAResponse = 'Task 1';
            oInquiryNotes.WebResponse = 'Test';
            System.assertEquals(oInquiryNotes.WebQuestion == 'Test',true);
            Test.stopTest();
        
    }
    
    /**
    * test method to cover service Response DTO
    *<p>
    * @param            NULL
    * @return           void
    */
    @isTest static void testInquiryDetAttachResponse_H_HUM()
    {
            Test.startTest();
            List<InquiryDetAttachResponse_H_HUM.DRAttachmentDetails> oDRAttachmentDetailsLst = new List<InquiryDetAttachResponse_H_HUM.DRAttachmentDetails>();
            InquiryDetAttachResponse_H_HUM.DRAttachmentDetails oDRAttachmentDetails = new InquiryDetAttachResponse_H_HUM.DRAttachmentDetails();
            oDRAttachmentDetails.ATTACH_TYPE_DESC = '10021222010';
            oDRAttachmentDetails.ATTACH_TYPE = '1';
            oDRAttachmentDetails.Doc_Id = 'Type1';
            oDRAttachmentDetails.Version_String = 'Medical';
            List<InquiryDetAttachResponse_H_HUM.ATTACH_DESC> oATTACH_DESCLst = new List<InquiryDetAttachResponse_H_HUM.ATTACH_DESC>();
            InquiryDetAttachResponse_H_HUM.ATTACH_DESC oATTACH_DESC = new InquiryDetAttachResponse_H_HUM.ATTACH_DESC();
            oATTACH_DESC.Field = 'Test Field';
            oATTACH_DESC.Value = '0';
            System.assertEquals(oDRAttachmentDetails.ATTACH_TYPE_DESC == '10021222010',true);
            Test.stopTest();
        
    }
    
    /**
    * test method to cover service Response DTO
    *<p>
    * @param            NULL
    * @return           void
    */
    @isTest static void testInquiryDetAuditResponse_H_HUM()
    {

            Test.startTest();
            List<InquiryDetAuditResponse_H_HUM.DRAuditDetails> oDRAuditDetailsLst = new List<InquiryDetAuditResponse_H_HUM.DRAuditDetails>();
            InquiryDetAuditResponse_H_HUM.DRAuditDetails oDRAuditDetails = new InquiryDetAuditResponse_H_HUM.DRAuditDetails();
            oDRAuditDetails.ACTION_TYPE_DESC = '10021222010';
            oDRAuditDetails.Modified_On = '1';
            oDRAuditDetails.CREATED_FIRST_NAME = 'Type1';
            oDRAuditDetails.CREATED_LAST_NAME = 'Medical';
            oDRAuditDetails.PREVIOUS_DESC = 'Medical';
            oDRAuditDetails.CURRENT_DESC = 'Medical';
            System.assertEquals(oDRAuditDetails.ACTION_TYPE_DESC == '10021222010',true);
            InquiryDetAuditWrapper_DTO_HUM oDRAuditDetailsWrap = new InquiryDetAuditWrapper_DTO_HUM();
            oDRAuditDetailsWrap.sModifiedOn = '05/25/2020';
            oDRAuditDetailsWrap.sModifiedBy = 'John Smith';
            Test.stopTest();
        
    }
	
	static testMethod void testgetInquiryTaskDetails()
    {
        Account accObj = new Account();
        accObj.Name = 'Test Account';
        insert accObj;
        String accID = accObj.Id;

            Test.startTest();
            InquiryDetails_C_HUM inquiryDetails = new InquiryDetails_C_HUM();
            inquiryDetails.sContactId = 'test1';
            inquiryDetails.sInquiryId = '1';
            inquiryDetails.sContactInquiry = '123-50';
            
            InquiryDetails_S_HUM oServiceInquirySummary = new InquiryDetails_S_HUM();
            Continuation continueObj = (Continuation)InquiryDetails_LC_HUM.getInquiryTaskDetails('1014741145','100210');
            continueObj.continuationMethod = 'parseInquiryTasks';
            continueObj.state = 'Both';
            HttpResponse httpRes = new Httpresponse (); 
            String resBodyString ='{ "TaskDetails":[ { "PART_ID":"8", "INQUIRY_ID":"1", "STATUS":"", "SHORT_DESC":"", "NOTE_DESC":"WRKFLOW -12/10/2019-", "ACTION_DATE":"12/10/2019", "ACTION_TIME":"15:42:53", "CONTACT_ID":"756244522119", "RESULT_CD":"067", "RESULT_CD_DESC":"Completed", "OWNER_ID":"ABC1314 ", "OWNER_FIRST_NAME":"Aletha", "OWNER_LAST_NAME":"Cates", "OWNER_TEAM_ID":"3381", "OWNER_TEAM_NAME":"Dallas Network ", "OWNER_TEAM_LGNM":"Dallas Network Ops ", "OWNER_DEPT_ID":"137", "OWNER_DEPT_LGNM":"National Network Ops ", "OWNER_SITE_ID":"Ma7", "OWNER_SITE_LGNM":"Ma7", "PRIORITY_CD":"", "PRIORITY_CD_DESC":"", "STATUS_CD":"CL", "CREATED_BY":"WRKFLOW ", "CREATED_FIRST_NAME":" ", "CREATED_LAST_NAME":" ", "AGE":"0", "CREATED_TEAM_ID":"15384", "CREATED_TEAM_NAME":"ParProvideTriag", "CREATED_TEAM_LGNM":"Par Provider - Triage ", "CREATED_DEPT_ID":"349", "CREATED_DEPT_LGNM":"UST Screener Department ", "CREATED_SITE_ID":"LOU", "CREATED_SITE_LGNM":"Louisville ", "CREATED_TS":"12/10/2019", "LASTUPD_TS":"01/03/2020", "LASTUPD_BY":"ABC1314 ", "LASTUPD_FIRST_NAME":"Aletha", "LASTUPD_LAST_NAME":"Cates", "LASTUPD_TEAM_ID":"3381", "LASTUPD_TEAM_NAME":"Dallas Network ", "ACTION_CD":"337", "ACTION_CD_DESC":"Received Humana", "TASK_TYPE":"CD", "TASK_TYPE_DESC":"Case Documentation", "CLOSED_TS":"12/10/2019", "DUE_DATE":"//", "TASK_ID":"1", "AuditDetails":[ { "TASK_ID":"1", "ACTION_TYPE_DESC":"Trans User to User", "Modified_On":"12/10/2019", "CREATED_LAST_NAME":"MATHEWS", "CREATED_FIRST_NAME":"KATHY", "PREVIOUS_DESC":" ", "CURRENT_DESC":" " },{ "TASK_ID":"1", "ACTION_TYPE_DESC":"Trans User", "Modified_On":"12/10/2019", "CREATED_LAST_NAME":"LISA", "CREATED_FIRST_NAME":"REY", "PREVIOUS_DESC":" ", "CURRENT_DESC":" " } ], "NotesDetails":[ { "NOTE_DESC":"WRKFLOW -12/10/2019-", "Task_ID":"1", "Type":"Task" },{ "NOTE_DESC":"FLOW -12/10/2019-", "Task_ID":"1", "Type":"Task" } ],"AttachmentDetails":[ { "CONTACT_ID":"389870230311", "INQUIRY_ID":"1", "TASK_ID":"1", "ATTACH_TYPE_DESC":"Document", "ATTACH_TYPE":"DO", "Doc_Id":"2679875", "Version_String":"1 ", "ATTACH_DESC":[ { "Field":"FileName", "Value":"DMS_QA_10162007_133613.html" }, { "Field":"VersionNumber", "Value":"1" }, { "Field":"UserID", "Value":"CPL5483 " } ] } ] }, { "PART_ID":"8", "INQUIRY_ID":"1", "STATUS":"", "SHORT_DESC":"", "NOTE_DESC":"WRKFLOW -12/10/2019-", "ACTION_DATE":"12/10/2019", "ACTION_TIME":"19:21:52", "CONTACT_ID":"756244522119", "RESULT_CD":"", "RESULT_CD_DESC":"", "OWNER_ID":"ABC1314 ", "OWNER_FIRST_NAME":"Aletha", "OWNER_LAST_NAME":"Cates", "OWNER_TEAM_ID":"3381", "OWNER_TEAM_NAME":"Dallas Network ", "OWNER_TEAM_LGNM":"Dallas Network Ops ", "OWNER_DEPT_ID":"137", "OWNER_DEPT_LGNM":"National Network Ops ", "OWNER_SITE_ID":"Ma7", "OWNER_SITE_LGNM":"Ma7", "PRIORITY_CD":"LL", "PRIORITY_CD_DESC":"Normal", "STATUS_CD":"CL", "CREATED_BY":"WRKFLOW ", "CREATED_FIRST_NAME":" ", "CREATED_LAST_NAME":" ", "AGE":"0", "CREATED_TEAM_ID":"15384", "CREATED_TEAM_NAME":"ParProvideTriag", "CREATED_TEAM_LGNM":"Par Provider - Triage ", "CREATED_DEPT_ID":"349", "CREATED_DEPT_LGNM":"UST Screener Department ", "CREATED_SITE_ID":"LOU", "CREATED_SITE_LGNM":"Louisville ", "CREATED_TS":"12/10/2019", "LASTUPD_TS":"01/03/2020", "LASTUPD_BY":"ABC1314 ", "LASTUPD_FIRST_NAME":"Aletha", "LASTUPD_LAST_NAME":"Cates", "LASTUPD_TEAM_ID":"3381", "LASTUPD_TEAM_NAME":"Dallas Network ", "ACTION_CD":"336", "ACTION_CD_DESC":"Received Department", "TASK_TYPE":"CD", "TASK_TYPE_DESC":"Case Documentation", "CLOSED_TS":"12/10/2019", "DUE_DATE":"09/09/9999", "TASK_ID":"2", "AuditDetails":[ { "TASK_ID":"2", "ACTION_TYPE_DESC":"Trans User to User", "Modified_On":"12/10/2019", "CREATED_LAST_NAME":"Kate", "CREATED_FIRST_NAME":"CHRIS", "PREVIOUS_DESC":" ", "CURRENT_DESC":" " },{ "TASK_ID":"2", "ACTION_TYPE_DESC":"Trans User", "Modified_On":"12/10/2019", "CREATED_LAST_NAME":"Cates", "CREATED_FIRST_NAME":"Aletha", "PREVIOUS_DESC":"CLOSED ", "CURRENT_DESC":"OPENED " } ],"NotesDetails":[ { "NOTE_DESC":"WRKFLOW -12/10/2019-", "Task_ID":"2", "Type":"Task" },{ "NOTE_DESC":"PROCESSFLOW -12/10/2019-", "Task_ID":"2", "Type":"Task" } ],"AttachmentDetails":[ { "CONTACT_ID":"389870230311", "INQUIRY_ID":"1", "TASK_ID":"1", "ATTACH_TYPE_DESC":"Document", "ATTACH_TYPE":"DO", "Doc_Id":"2679875", "Version_String":"1 ", "ATTACH_DESC":[ { "Field":"FileName", "Value":"DMS_QA_10162007_133613.html" }, { "Field":"VersionNumber", "Value":"1" }, { "Field":"UserID", "Value":"CPL5483 " } ] } ] } ] } ';
            httpRes.setBody(resBodyString);
            System.assert(httpRes.getBody() == resBodyString);
            Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
            string reqType = '';
            for(String s : mapDetails.keyset())
            {
                reqType = s;
            } 
            List<string>labelGetmember = new List<string>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,httpRes);
            InquiryDetails_LC_HUM.parseInquiryTaskDetail(labelGetmember,continueObj);
            Test.stopTest();
        
    }
    
    @isTest static void testInquiryTaskDetailsResponse_H_HUM()
    {

            Test.startTest();
            InquiryTaskDetailsResponse_H_HUM oResponse = new InquiryTaskDetailsResponse_H_HUM();
            List<InquiryTaskDetailsResponse_H_HUM.TaskDetails> oInquiryDetailsList = new List<InquiryTaskDetailsResponse_H_HUM.TaskDetails>();
            InquiryTaskDetailsResponse_H_HUM.TaskDetails oInqTaskDet = new InquiryTaskDetailsResponse_H_HUM.TaskDetails();
            InquiryTaskDetailsResponse_H_HUM.NotesDetails oInqTaskNotesDet = new InquiryTaskDetailsResponse_H_HUM.NotesDetails();
            InquiryTaskDetailsResponse_H_HUM.AuditDetails oInqTaskAuditDet = new InquiryTaskDetailsResponse_H_HUM.AuditDetails();
            InquiryTaskDetailsResponse_H_HUM.AttachmentDetails oInqTaskAttachment = new InquiryTaskDetailsResponse_H_HUM.AttachmentDetails();
            InquiryTaskDetailsResponse_H_HUM.ATTACH_DESC oInqTaskAttachmentDescription = new InquiryTaskDetailsResponse_H_HUM.ATTACH_DESC();
            oInqTaskDet.TASK_ID ='1';
            oInqTaskDet.CREATED_TS ='';
            oInqTaskDet.AGE='';
            oInqTaskDet.ACTION_CD_DESC='';
            oInqTaskDet.RESULT_CD_DESC ='';
            oInqTaskDet.SHORT_DESC ='';
            oInqTaskDet.STATUS_CD ='';
            oInqTaskDet.DUE_DATE ='';
            oInqTaskDet.CLOSED_TS ='';  
            oInqTaskDet.CREATED_FIRST_NAME ='';  
            oInqTaskDet.CREATED_LAST_NAME ='';  
            oInqTaskDet.CREATED_BY ='';  
            oInqTaskDet.CREATED_TEAM_NAME ='';  
            oInqTaskDet.CREATED_DEPT_LGNM ='';  
            oInqTaskDet.CREATED_SITE_LGNM ='';
            oInqTaskDet.ACTION_TIME ='';  
            oInqTaskDet.OWNER_SITE_LGNM =''; 
            oInqTaskDet.PRIORITY_CD_DESC =''; 
            oInqTaskDet.OWNER_DEPT_LGNM =''; 
            oInqTaskDet.TASK_TYPE_DESC =''; 
            oInqTaskDet.OWNER_TEAM_NAME =''; 
            oInqTaskDet.OWNER_FIRST_NAME =''; 
            oInqTaskDet.OWNER_LAST_NAME  ='';  
            
            oInqTaskNotesDet.Type = '';
            oInqTaskNotesDet.Task_ID = '';
            oInqTaskNotesDet.NOTE_DESC = '';
             
            oInqTaskAuditDet.TASK_ID = '';
            oInqTaskAuditDet.ACTION_TYPE_DESC = '';
            oInqTaskAuditDet.Modified_On = ''; 
            oInqTaskAuditDet.CREATED_LAST_NAME = ''; 
            oInqTaskAuditDet.CREATED_FIRST_NAME = ''; 
            oInqTaskAuditDet.PREVIOUS_DESC = ''; 
            oInqTaskAuditDet.CURRENT_DESC = ''; 
            
            oInqTaskAttachment.ATTACH_TYPE_DESC  =''; 
            oInqTaskAttachment.ATTACH_TYPE  =''; 
            oInqTaskAttachment.Doc_Id  =''; 
            oInqTaskAttachment.Version_String  =''; 
            
            oInqTaskAttachmentDescription.Field  ='';
            oInqTaskAttachmentDescription.Value  ='';
            
            System.assertEquals(oInqTaskDet.TASK_ID == '1',true);
            InquiryTaskDetailsWrapper_DTO_HUM oInqTaskDetailsWrap = new InquiryTaskDetailsWrapper_DTO_HUM();
            oInqTaskDetailsWrap.taskID = '1';
			oInqTaskDetailsWrap.sUser = 'John Smith';
            oInqTaskDetailsWrap.sCreatedOn = '';
            oInqTaskDetailsWrap.sAge = '';
            oInqTaskDetailsWrap.sActionDesc = '';
            oInqTaskDetailsWrap.sResultDesc = '';
            oInqTaskDetailsWrap.sShortDesc = '';
            oInqTaskDetailsWrap.sStatus = '';
            oInqTaskDetailsWrap.sDueDate = '';
            oInqTaskDetailsWrap.sDateClosed = '';
            oInqTaskDetailsWrap.sCreatedBy = '';
            oInqTaskDetailsWrap.sActionTime = '';
            oInqTaskDetailsWrap.sSrvCtr = '';
            oInqTaskDetailsWrap.sPriorityDesc = '';
            oInqTaskDetailsWrap.sDept = '';
            oInqTaskDetailsWrap.sTaskTypeDesc = '';
            oInqTaskDetailsWrap.sTeam = '';
            
            oInqTaskDetailsWrap.sType = '';
            oInqTaskDetailsWrap.sIDFileName = '';
            oInqTaskDetailsWrap.sAttachType = '';
            oInqTaskDetailsWrap.sVersionNumber = '';
            oInqTaskDetailsWrap.sField = new List<String>();
            oInqTaskDetailsWrap.sValue = new List<String>();
            Test.stopTest();
      
    }
}