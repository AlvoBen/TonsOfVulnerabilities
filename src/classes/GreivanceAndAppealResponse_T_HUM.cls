/****************************************************************************************
Apex Class Name  : GreivanceAndAppealResponse_T_HUM
Version          : 1.0 
Created Date     : November 05 2018
Function         : Test class to GreivanceAndAppealResponse_H_HUM  Apex class 
Modification Log
*      Developer               Code Review            Date                 Description
* --------------------------------------------------------------------------------------------------------        
*   Ajay Chakradhar                                 11/05/2018              Original Version     
***********************************************************************************************************/
@isTest
private class GreivanceAndAppealResponse_T_HUM
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
            GreivanceAndAppealResponse_H_HUM docResponseObj = new GreivanceAndAppealResponse_H_HUM();
            docResponseObj = GreivanceAndAppealResponse_H_HUM.parse('{"getCorrespondenceAndDocumentsResponse":{"CorrespondencesAndDocuments":[{"ProgramName":"PersonalNurse","CareManagerName":"SusanSmith"},{"ProgramName":"HumanaBeginnings","CareManagerName":"TestCareManagerName"}]}}');
        }
        Test.stopTest();
    }
}