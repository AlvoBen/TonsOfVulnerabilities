/****************************************************************************************
Apex Class Name  : GreivanceAndAppealDTO_T_HUM
Version          : 1.0 
Created Date     : November 05 2018
Function         : Test class to GreivanceAndAppeal_DTO_HUM  Apex class 
Modification Log
*      Developer               Code Review            Date                 Description
* --------------------------------------------------------------------------------------------------------        
*    Ajay Chakradhar                                11/05/2018              Original Version     
***********************************************************************************************************/
@isTest
private class GreivanceAndAppealDTO_T_HUM
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
            GreivanceAndAppeal_DTO_HUM docDTOObj = new GreivanceAndAppeal_DTO_HUM();
            GreivanceAndAppeal_DTO_HUM.caseNotifications caseNotiObj = new GreivanceAndAppeal_DTO_HUM.caseNotifications();
            GreivanceAndAppeal_DTO_HUM.Notifications caseNotificationObj = new GreivanceAndAppeal_DTO_HUM.Notifications();
            docDTOObj.context = 'test file Name';
            docDTOObj.medhokID = 'test Doc Name';
            docDTOObj.sCase = 'test Created Name';
            docDTOObj.context = 'test file Name';
            docDTOObj.sCategory = 'test Doc Name';
            docDTOObj.sDateofDetermination = 'test Created Name';
            docDTOObj.sDueDate = 'test file Name';
            docDTOObj.sPriority = 'test Doc Name';
            docDTOObj.sRequestType = 'test Created Name';
            docDTOObj.sServiceType = 'test file Name';
            docDTOObj.sStatus = 'test Doc Name';
            docDTOObj.sStatusReason = 'test Created Name';            
            docDTOObj.sType = 'test Created Name';  
            docDTOObj.notifications = caseNotificationObj;
            caseNotiObj.createdBy = 'Test';
            caseNotiObj.note = 'Test';
            caseNotiObj.noteDate = 'Test';
        }
        Test.stopTest();
    }
}