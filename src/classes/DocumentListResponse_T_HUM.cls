/****************************************************************************************
Apex Class Name  : DocumentListResponse_T_HUM
Version          : 1.0 
Created Date     : November 05 2018
Function         : Test class to DocumentListResponse_H_HUM  Apex class 
Modification Log
*      Developer               Code Review            Date                 Description
* --------------------------------------------------------------------------------------------------------        
*   Rajesh                                         11/05/2018              Original Version     
***********************************************************************************************************/
@isTest
private class DocumentListResponse_T_HUM
{
        /*
    * Method Name : setup
    * Description : Create common test Data
    * Return type : void
    * Paramater   : NA
    */ 
    static testMethod void testSetup(){
        Test.startTest();       
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u; 
        system.assertEquals(u.Id != Null,true);
        system.assertEquals(u.Lastname == 'test User1',true);        
        System.runAs(u){    
            DocumentListResponse_H_HUM docResponseObj = new DocumentListResponse_H_HUM();
            docResponseObj = DocumentListResponse_H_HUM.parse('{"getCorrespondenceAndDocumentsResponse":{"CorrespondencesAndDocuments":[{"documentId":"TU0vL091dGJvdW5kLy8yMDE4Ly8xMS8vMjEvLzIwNDE2Ly8xLmJpbg==","documentType":"SP GRV AOR Missing Information","fileName":"SP GRV AOR Missing Information","userId":"LTI2060","firstName":"Lakshmi","lastName":"Itha","attachmentReceivedDate":"2018-11-21T05:51:08-05:00","docSource":"Correspondence"}]}}');
        }
        Test.stopTest();
    }
}