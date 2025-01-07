/****************************************************************************************
Apex Class Name  : CaseRelatedDocument_T_HUM
Version          : 1.0 
Created Date     : November 05 2018
Function         : Test class to CaseRelatedDocument_T_HUM  Apex class 
Modification Log
*      Developer               Code Review            Date                 Description
* --------------------------------------------------------------------------------------------------------        
*   Rajesh                                         11/05/2018              Original Version     
***********************************************************************************************************/
@isTest
private class CaseRelatedDocument_T_HUM
{
    /*
    * Method Name : setup
    * Description : Create common test Data
    * Return type : void
    * Paramater   : NA
    */ 
    static testMethod void testCaseDocuments()
    {
        Test.StartTest();
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u; 
        system.assertEquals(u.Id != Null,true);
        system.assertEquals(u.Lastname == 'test User1',true);        
        System.runAs(u){
            HUM_Webservice_Callout__c calloutSettings = new HUM_Webservice_Callout__c();
            calloutSettings.Name = 'Medhok_getDocument';
            calloutSettings.Active__c = True;
            calloutSettings.Enable_Service_Tester__c = True;
            calloutSettings.Certificate_Name__c = 'Humana_GuidanceCare_Pileus';
            calloutSettings.Content_Type__c = 'application/json';
            calloutSettings.Request_Type__c = 'Post';
            calloutSettings.Timeout__c = '12000';
            calloutSettings.End_Point_URL__c = 'https://dev-crmservicesvcs.humana.com:48022/DocumentService/getDocument';
            insert calloutSettings;
            
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
            
            CaseRelatedDocument_C_HUM caseRelatedObj = new CaseRelatedDocument_C_HUM();
            
            caseRelatedObj.columnHeader = 'test';
            caseRelatedObj.medhokID = 'testmedokId';
            caseRelatedObj.vContext = 'test vContext';
            caseRelatedObj.strHTML = 'strHTML';
            
            String Context = 'MedicalAppeals';
            String MedhokId = '3330';
            
            CaseRelatedDocument_C_HUM.getCaseDocuments(Context,MedhokId);
            caseRelatedObj.showPageMessage();
            HttpResponse response = new HttpResponse();
            response.setBody('{"getCorrespondenceAndDocumentsResponse":{"correspondencesAndDocuments":[{"documentId":"TU0vL091dGJvdW5kLy8yMDE4Ly8xMS8vMjEvLzIwNDE2Ly8xLmJpbg==","documentType":"SP GRV AOR Missing Information","fileName":"SP GRV AOR Missing Information","userId":"LTI2060","firstName":"Lakshmi","lastName":"Itha","attachmentReceivedDate":"2018-11-21T05:51:08-05:00","docSource":"Correspondence"}]}}');
            Test.setContinuationResponse('label', response);
            CaseRelatedDocument_C_HUM.processViewDocumentsResponse(new List<string>{(String.valueOf(response))},null);
        }
        Test.StopTest();
    }
}