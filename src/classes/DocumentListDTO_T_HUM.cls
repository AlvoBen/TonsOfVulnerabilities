/****************************************************************************************
Apex Class Name  : DocumentListDTO_T_HUM
Version          : 1.0 
Created Date     : November 05 2018
Function         : Test class to DocumentList_DTO_HUM  Apex class 
Modification Log
*      Developer               Code Review            Date                 Description
* --------------------------------------------------------------------------------------------------------        
*   Rajesh                                         11/05/2018              Original Version     
***********************************************************************************************************/
@isTest
private class DocumentListDTO_T_HUM
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
            DocumentList_DTO_HUM docDTOObj = new DocumentList_DTO_HUM();
            docDTOObj.sfileName = 'test file Name';
            docDTOObj.sDocumentType = 'test Doc Name';
            docDTOObj.sCreatedby = 'test Created Name';
            docDTOObj.sDocumentId ='TU0vL091dGJvdW5';
            docDTOObj.internalError = 'test internal error';
        }
        Test.stopTest();
    }
}