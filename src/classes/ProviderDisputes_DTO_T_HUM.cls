/****************************************************************************************
Apex Class Name  : ProviderDisputes_DTO_T_HUM
Version          : 1.0 
Function         : Test class to ProviderDisputes_DTO_HUM  Apex class 
Modification Log
*      Developer               Code Review            Date                 Description
* --------------------------------------------------------------------------------------------------------        
*    Ajay Chakradhar                                10/15/2019              Original Version     
***********************************************************************************************************/
@isTest
private class ProviderDisputes_DTO_T_HUM
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
            ProviderDisputes_DTO_H_HUM oProviderDisputesObj = new ProviderDisputes_DTO_H_HUM();
            ProviderDisputes_DTO_H_HUM.caseNotifications caseNotiObj = new ProviderDisputes_DTO_H_HUM.caseNotifications();
            ProviderDisputes_DTO_H_HUM.Notifications caseNotificationObj = new ProviderDisputes_DTO_H_HUM.Notifications();
            List<ProviderDisputes_DTO_H_HUM.caseNotifications> lstCaseNotificationObj = new List<ProviderDisputes_DTO_H_HUM.caseNotifications>();
            caseNotiObj.createdBy = 'TestUser';
            caseNotiObj.note = 'Test Comments';
            caseNotiObj.noteDate = '09-25-2019 02:54:58';
            lstCaseNotificationObj.add(caseNotiObj);   
            
            oProviderDisputesObj.sCase	='D19309209812';
            oProviderDisputesObj.sType	='Provider Dispute';
            oProviderDisputesObj.sCategory	='Medical Necessity';
            oProviderDisputesObj.sSubCategory	='Inpatient Hospital';
            oProviderDisputesObj.sStatus	='Open';
            oProviderDisputesObj.sStatusReason	='Pending Initial Review';
            oProviderDisputesObj.sPriority	='Standard';
            oProviderDisputesObj.sDueDate	='05-13-2019';
            oProviderDisputesObj.sProviderName	='Tiffany Nguyen';
            oProviderDisputesObj.sServiceType	='Post Service';
            oProviderDisputesObj.sRequestType	='Dispute';
            oProviderDisputesObj.sDateofDetermination	='05-13-2019 09:33:39';
            oProviderDisputesObj.sProduct	='Medicare';
            oProviderDisputesObj.sReceivedDate	='05-13-2019 09:33:39';
            oProviderDisputesObj.sProviderTaxID	='1144482480';
            oProviderDisputesObj.sDateofService	='05-13-2019 09:33:39';
            oProviderDisputesObj.sClaimNumber	='820191990002933';
            oProviderDisputesObj.sAssignedAnalyst	='Humana Train';
            oProviderDisputesObj.internalError	='error';
            oProviderDisputesObj.medhokID	='39';
            oProviderDisputesObj.context	='ProviderDispute';
            
            caseNotificationObj.caseNotifications = lstCaseNotificationObj;
            oProviderDisputesObj.notifications = caseNotificationObj;
            caseNotiObj.createdBy = 'TestUser';
            caseNotiObj.note = 'Test Comments';
            caseNotiObj.noteDate = '09-25-2019 02:54:58';
            
        }
        Test.stopTest();
    }
}