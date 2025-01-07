/****************************************************************************************
Apex Class Name  : ViewDocuments_T_HUM
Version          : 1.0 
Created Date     : November 05 2018
Function         : Test class to ViewDocuments_S_HUM  Apex class 
Modification Log
*      Developer               Code Review            Date                 Description
* --------------------------------------------------------------------------------------------------------        
*   Rajesh                                         11/05/2018              Original Version  
*   Ravikanth Samudrala                            10/07/2022              US -3683154 : T1PRJ0272555- MF 2 SF - TECH - SonarQube 2022 - CRM_MHK : SOQL and DML inside loops.
***********************************************************************************************************/
@isTest
private class ViewDocuments_T_HUM
{
    /*
    * Method Name : setup
    * Description : Test the Documentviewer services
    * Return type : void
    * Paramater   : NA
    */ 
    Static testMethod void testSetup(){
        Test.startTest();
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u; 
        system.assertEquals(u.Id != Null,true);
        system.assertEquals(u.Lastname == 'test User1',true);        
        System.runAs(u)
        {   
            HUM_Webservice_Callout__c oGAService = new HUM_Webservice_Callout__c();
            oGAService.Name = 'ViewDocuments';
            oGAService.Active__c = True;
            oGAService.Enable_Service_Tester__c = True;
            oGAService.Certificate_Name__c = 'Humana_GuidanceCare_Pileus';
            oGAService.Content_Type__c = 'application/json';
            oGAService.Request_Type__c = 'Post';
            oGAService.Timeout__c = '12000';
            oGAService.End_Point_URL__c = 'https://dev-crmservicews.humana.com/DocumentService/getCorrespondenceAndDocuments';
            insert oGAService;
                
            DocumentList_DTO_HUM DTOObj = new DocumentList_DTO_HUM();
            DTOObj.sfileName = 'Test';
            DTOObj.sDocumentType = 'Test';
            DTOObj.sCreatedby = 'Test';
            List<DocumentList_DTO_HUM> lstDocumentsDTO = new List<DocumentList_DTO_HUM >();
            lstDocumentsDTO.add(DTOObj);                     
            
            Continuation con = (Continuation)ViewDocuments_S_HUM.sendViewDocumentsRequest('sContext','sMedhokId'); 
            
            HttpResponse resViewer = new Httpresponse();
            resViewer.setBody('{ "getCorrespondenceAndDocumentsResponse":{ "correspondencesAndDocuments":[ { "documentId":"TU0vL091dGJvdW5kLy8yMDE4Ly8xMC8vMTkvLzcxOTA5Ly8xLmJpbg==", "documentType":"APL Withdrawal", "fileName":"APL Withdrawal", "userId":"mvaghasia", "firstName":"Mahendra", "lastName":"Vaghasia", "attachmentReceivedDate":"2018-10-19T12:29:37-04:00", "docSource":"Correspondence" }, { "documentId":"TU0vL091dGJvdW5kLy8yMDE4Ly8xMC8vMTkvLzcxOTA5Ly8zLmJpbg==", "documentType":"SP APL Verbal Acknowledgement", "fileName":"SP APL Verbal Acknowledgement", "userId":"mvaghasia", "firstName":"Mahendra", "lastName":"Vaghasia", "attachmentReceivedDate":"2018-10-19T12:30:08-04:00", "docSource":"Correspondence" }, { "documentId":"TU0vL1VwbG9hZC8vMjAxOC8vMTAvLzE5Ly8zMzA4MC8vMS5iaW4=", "documentType":"Medical Records", "fileName":"1.pdf", "userId":"mvaghasia", "firstName":"", "lastName":"", "attachmentReceivedDate":"2018-10-19T12:27:17-04:00", "docSource":"Document" }, { "documentId":"TU0vL1VwbG9hZC8vMjAxOC8vMTAvLzE5Ly8zMzA4MC8vMi5iaW4=", "documentType":"Member Document", "fileName":"180821000013001_1.tiff", "userId":"mvaghasia", "firstName":"", "lastName":"", "attachmentReceivedDate":"2018-10-19T12:27:29-04:00", "docSource":"Document" } ] } }');
            resViewer.setStatusCode(200);            
            
        	Test.setContinuationResponse('label', resViewer);
        	ViewDocuments_S_HUM.processResponse((DocumentListResponse_H_HUM)System.JSON.deserialize(resViewer.getbody(),DocumentListResponse_H_HUM.class));
            ViewDocuments_S_HUM.processViewDocumentsResponse(new List<string>{(String.valueOf(resViewer))},null);
            ViewDocuments_S_HUM.DateConverter(string.valueOfGmt(Date.Today()));
       }
        Test.stopTest();
    }
}