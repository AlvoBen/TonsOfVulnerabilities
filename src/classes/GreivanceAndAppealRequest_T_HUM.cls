/****************************************************************************************
Apex Class Name  : GreivanceAndAppealRequest_T_HUM
Version          : 1.0 
Created Date     : November 05 2018
Function         : Test class to GreivanceAndAppealRequest_H_HUM  Apex class 
Modification Log
*      Developer               Code Review            Date                 Description
* --------------------------------------------------------------------------------------------------------        
*   Ajay Chakradhar                                11/05/2018              Original Version     
***********************************************************************************************************/
@isTest
private class GreivanceAndAppealRequest_T_HUM
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
            GreivanceAndAppealRequest_H_HUM docResponseObj = new GreivanceAndAppealRequest_H_HUM();
            GreivanceAndAppealRequest_H_HUM.GetGrievanceAndAppealByCriteriaRequest greAppealObj = new GreivanceAndAppealRequest_H_HUM.GetGrievanceAndAppealByCriteriaRequest();
            GreivanceAndAppealRequest_H_HUM.Member memberObj = new GreivanceAndAppealRequest_H_HUM.Member();
            memberObj.externalId = 'Test External';
            greAppealObj.fromDate = 'test';
            greAppealObj.member = memberObj;
            greAppealObj.thruDate = 'Test';
            docResponseObj = GreivanceAndAppealRequest_H_HUM.parse('{"getCorrespondenceAndDocumentsResponse":{"CorrespondencesAndDocuments":[{"ProgramName":"PersonalNurse","CareManagerName":"SusanSmith"},{"ProgramName":"HumanaBeginnings","CareManagerName":"TestCareManagerName"}]}}');
        }
        Test.stopTest();
    }
}