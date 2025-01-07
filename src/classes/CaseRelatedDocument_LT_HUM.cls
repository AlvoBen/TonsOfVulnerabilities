/**************************************************************************** 
Apex Class Name  : CaseRelatedDocument_LT_HUM
Version          : 1.0  
Created Date     : 09/02/2022

****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Kalyani Pachpol                               09/02/2022                  Original Version
* Jonathan Dickinson                            06/28/2023                  User Story 4759866: T1PRJ0891415 Platform Management- SF - TECH - LEGO - Blocker - ViewDocuments_LS_HUM : Avoid Soql queries inside loops.
***************************************************************************************************/
@isTest
public class CaseRelatedDocument_LT_HUM {
  
	@testSetup static void prepareTestData() 
    {
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u; 
        system.assertEquals(u.LastName,'test User1');
        
        System.runAs(u){
             
            HUM_Webservice_Callout__c calloutSettings1 = new HUM_Webservice_Callout__c();
            calloutSettings1.Name = 'ViewDocuments';
            calloutSettings1.Active__c = True;
            calloutSettings1.Enable_Service_Tester__c = True;
            calloutSettings1.Certificate_Name__c = 'Humana_GuidanceCare_Pileus';
            calloutSettings1.Content_Type__c = 'application/json';
            calloutSettings1.Request_Type__c = 'Post';
            calloutSettings1.Timeout__c = '12000';
            calloutSettings1.End_Point_URL__c = 'https://dev-crmservicews.humana.com/DocumentService/getCorrespondenceAndDocuments';
            insert calloutSettings1;
            System.assert(HUM_Webservice_Callout__c.getValues('ViewDocuments') != NULL);
            
            HUM_Webservice_Callout__c calloutSettings = new HUM_Webservice_Callout__c();
            calloutSettings.Name = 'Medhok_getDocument';
            calloutSettings.Active__c = True;
            calloutSettings.Enable_Service_Tester__c = True;
            calloutSettings.Certificate_Name__c = 'Humana_GuidanceCare_Pileus';
            calloutSettings.Content_Type__c = 'application/json';
            calloutSettings.Request_Type__c = 'Post';
            calloutSettings.Timeout__c = '12000';
            calloutSettings.End_Point_URL__c = 'https://dev-crmservicesvcs.humana.com:48022/DocumentService/getDocument';
            calloutSettings.Password__c ='238F6F83-B8B4-11CF-8771-00A024541EE9';
            insert calloutSettings;
            System.assert(HUM_Webservice_Callout__c.getValues('Medhok_getDocument') != NULL);
            
            DocumentList_DTO_HUM DTOObj = new DocumentList_DTO_HUM();
            DTOObj.sfileName = 'Test';
            DTOObj.sDocumentType = 'Test';
            DTOObj.sCreatedby = 'Test';
            List<DocumentList_DTO_HUM> lstDocumentsDTO = new List<DocumentList_DTO_HUM >();
            lstDocumentsDTO.add(DTOObj); 
        }
    }
    
    
    private static testmethod void testgetdocumentURL()
    {
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        system.runAs(testUser)
        {
            Test.StartTest();
        CaseRelatedDocument_LC_HUM.getdocumentURL();
            Test.StopTest();
        }
    }
    
    private static testmethod void testDocumentPositive()
    {
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        system.runAs(testUser)
        {
            String jsonRequest = '{"getCorrespondenceAndDocumentsRequest":{"context":"Appeal","medhokID":"17612"}}';
           Test.StartTest();
            Continuation con = (Continuation)CaseRelatedDocument_LC_HUM.getCaseDocuments(jsonRequest);
            con.continuationMethod = 'processViewDocumentsResponse';
             HttpResponse response = new HttpResponse();
             String resBodyString ='{ "getCorrespondenceAndDocumentsResponse":{ "correspondencesAndDocuments":[ { "documentId":"TU0vL1VwbG9hZC8vMjAyMi8vMDgvLzI0Ly84MjQzNC8vMzAuYmlu", "documentType":"Other", "fileName":"G_A_Document_testing.xlsx", "userId":"SDT5349", "firstName":"", "lastName":"", "attachmentReceivedDate":"2022-08-24T11:44:07-04:00", "docSource":"Document" } ] } }';
             response.setBody(resBodyString);
            con.state = 'Continuation-1';
            string reqType = 'Continuation-1'; 
            List<string> labelGetmember = new List<string>();
            labelGetmember.add(reqType);
            response.setStatusCode(200);
            Test.setContinuationResponse(reqType, response);
            String responsestr= (string)CaseRelatedDocument_LC_HUM.processViewDocumentsResponse(labelGetmember,resBodyString);
            Boolean bNoDataExist = responsestr.containsIgnoreCase('G_A_Document_testing.xlsx') ? true : false;
            system.assertEquals(true, bNoDataExist);
            system.assertEquals(response.getBody(), resBodyString);
            Test.StopTest();
        }
    }  

    private static testmethod void testDocumentResponseException()
    {
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        system.runAs(testUser)
        {
            String jsonRequest = '{"getCorrespondenceAndDocumentsRequest":{"context":"Appeal","medhokID":"17612"}}';
            Test.StartTest();
            try{
                Continuation con = (Continuation)CaseRelatedDocument_LC_HUM.getCaseDocuments(jsonRequest);
                con.continuationMethod = 'processViewDocumentsResponse';
                HttpResponse response = new HttpResponse();
                response.setStatusCode(200);
                response.setBody('{ "getCorrespondenceAndDocumentsResponse":{ "correspondencesAndDocuments":[ { "documentId":"TU0vL1VwbG9hZC8vMjAyMi8vMDgvLzI0Ly84MjQzNC8vMzAuYmlu", "documentType":"Other", "fileName":"G_A_Document_testing.xlsx", "userId":"SDT5349", "firstName":"", "lastName":"", "attachmentReceivedDate":"2022-08-24T11:44:07-04:00", "docSource":"Document" } ] } }');
                Test.setContinuationResponse('labels', response);
                CaseRelatedDocument_LC_HUM.processViewDocumentsResponse(new List<string>{(String.valueOf(response))},null);
            } catch (AuraHandledException e) {
                system.assertEquals('Script-thrown exception', e.getMessage());
            }

            Test.StopTest();
        }
}
    
    private static testmethod void testDocumentNegative()
    {
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        system.runAs(testUser)
        {
            String jsonRequest;
            Test.StartTest();
            try{
                Continuation con = (Continuation)CaseRelatedDocument_LC_HUM.getCaseDocuments(jsonRequest);
                HttpResponse response = new HttpResponse();
                String resBodyString ='';
                response.setBody(resBodyString);
                string reqType = 'Continuation-1'; 
                List<string> labelGetmember = new List<string>();
                labelGetmember.add(reqType);
                Test.setContinuationResponse(reqType, response);
                String  responsestr=(String)CaseRelatedDocument_LC_HUM.processViewDocumentsResponse(labelGetmember,resBodyString);
            } catch (AuraHandledException e){
                system.assertEquals('Script-thrown exception', e.getMessage());
            }
            Test.StopTest();
        }
    }  
    
    private static testmethod void testDocumentResponseServiceException()
    {
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        system.runAs(testUser)
        {
            String jsonRequest = '{"getCorrespondenceAndDocumentsRequest":{"context":"Appeal","medhokID":"17612"}}';
            Test.StartTest();
            try {
                Continuation con = (Continuation)CaseRelatedDocument_LC_HUM.getCaseDocuments(jsonRequest);
                con.continuationMethod = 'processViewDocumentsResponse';
                HttpResponse response = new HttpResponse();
                String resBodyString ='{ "getCorrespondenceAndDocumentsResponse":{ "correspondencesAndDocuments":[ { "documentId":"TU0vL1VwbG9hZC8vMjAyMi8vMDgvLzI0Ly84MjQzNC8vMzAuYmlu", "documentType":"Other", "fileName":"G_A_Document_testing.xlsx", "userId":"SDT5349", "firstName":"", "lastName":"", "attachmentReceivedDate":"2022-08-24T11:44:07-04:00", "docSource":"Document" } ] } }';
                response.setBody(resBodyString);
                con.state = 'Continuation-1';
                string reqType = 'Continuation-1'; 
                List<string> labelGetmember = new List<string>();
                labelGetmember.add(reqType);
                response.setStatusCode(500);
                Test.setContinuationResponse(reqType, response);
                String responsestr= (string)CaseRelatedDocument_LC_HUM.processViewDocumentsResponse(labelGetmember,resBodyString);
            } catch (AurahandledException e){
                system.assertEquals('Script-thrown exception', e.getMessage());
            }

            Test.StopTest();
        }
}
    
    
}