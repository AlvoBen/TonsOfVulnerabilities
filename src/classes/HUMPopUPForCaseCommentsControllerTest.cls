/*****************************************************************************************
Apex class Name    : HUMPopUPForCaseCommentsControllerTest
Version            : 1.0
Function           : Test class for HUMPopUPForCaseCommentsController
Created Date       : 13 Jan 2015

Modification Log:
*   Developer                   Code Review             Date               Description
* ------------------------------------------------------------------------------------------------------------------------------                
*   Manish Kumar                21062                  01/13/2015         Original Version
*   Manish Kumar                21737                  03/23/2015         Code fix for defect#176720. Should not display more than 200 chars.Code added/modified line#45
*   Prachi Bhasin               33486                  07/18/2016         Changes made as part of REQ- 270349 
*   Pradeepkumar Dani           99570                  06/08/2017         Intent__c.Classification__c & Intent__c.Etl_Idtfr__c Clean up
*   Pradeepkumar Dani           100996                 06/13/2017         Classification__c.Classification_Type__c & Etl_Idftr__c Clean Up
*   Prudhvi Pamarthi            165023                 01/19/2018         REQ - 328929 Fix for HUMExceptionHelper: DML 
*   Santhi Mandava                                     03/12/2019         Removed "DpaaSWebServiceMockImpl" usage.
*********************************************************************************************************************************/

@isTest
private class HUMPopUPForCaseCommentsControllerTest
{
    /*
    * Test method for overall code coverage
    * @param     NA
    * @return    void
    */
    @testSetup static void setup()
    {
        Test.startTest();        
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertNotEquals(oCCSUser, NULL, 'Value not matched');
        
        System.RunAs(oCCSUser)
        {
            Account oAcc = HUMTestDataHelperTest.getAccount(); 
            oAcc.Name='TestAccount1';
            oAcc.Source_Platform_Code__c = 'CA';
            insert oAcc;
            system.assertEquals(oAcc.Name=='TestAccount1',true);
            
            List<Classification__c> lstClassification = new List<Classification__c>();
            Classification__c classificationRec = new Classification__c();
            classificationRec.Name = 'Claims';
            classificationRec.Is_Active__c = true;
            lstClassification.add(classificationRec);           
            
            insert lstClassification;
            system.assertEquals(lstClassification[0].Name=='Claims',true);
            
            List<Intent__c> lstIntent = new List<Intent__c>();
            Intent__c intentRec = new Intent__c();
            intentRec.Name = 'Filling';
            intentRec.Is_Active__c = true;
            lstIntent.add(intentRec);

            insert lstIntent;
            system.assertEquals(intentRec.Name == 'Filling', true);

            Case oCase = new Case(AccountId=oAcc.Id, Intent_Id__c = lstIntent[0].id, Classification_Id__c =lstClassification[0].id, General_Case__c =true, 
                               Interacting_With_Type__c = 'Member', Interacting_With__c=oAcc.Id, Due_Date__c=Date.Today(),
                               Case_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU', Work_Queue_View_Name__c='Cincinati CRU',
                               Service_Center__c ='Cincinnati', Department__c='CRU', status='In Progress');
            oCase.Complaint__c = 'Yes';
            oCase.Complaint_Reason__c = 'Claims';
            oCase.Classification_Type__c = 'Calls';
            oCase.G_A_Rights_Given__c ='No';
            oCase.Tenant_Id__c = '00';
            oCase.General_Case__c = true;
            insert oCase;
            System.assertEquals(oCase.G_A_Rights_Given__c =='No',true);
            
            HUMConstants__c oHUMObj = new HUMConstants__c(Name='HUMLastCaseComment', StringValue__c=Label.HUMNoCaseComments);
            insert oHUMObj;
            System.assertEquals(oHUMObj.Name=='HUMLastCaseComment',true);
            
            HUMConstants__c oHUMObj1 = new HUMConstants__c(Name='CaseCommentSize', StringValue__c= '200');
            insert oHUMObj1;
            System.assertEquals(oHUMObj1.Name=='CaseCommentSize',true);
            
            HUMConstants__c oHUMObj2 = new HUMConstants__c(Name='HUMConstantOne', IntegerValue__c= 1);
            insert oHUMObj2;
            System.assertEquals(oHUMObj2.Name=='HUMConstantOne',true);
        }
    }
    
    /**
    * This is a test method for HUMPopUpForcommentsControllerTest 
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void testHUMPopUpForcommentsControllerTest()
    {
        HUMTestDataHelperTest.getHUMConstantsData();
        User oUser = [SELECT Id,lastname FROM User  WHERE Lastname='test User1' LIMIT 1];
        system.assertEquals(oUser.lastname=='test User1',true);
        
        System.runAs(oUser) 
        {
            Case oCase = [Select id from case limit 1];
            HUMPopUPForCaseCommentsController oHandler = new HUMPopUPForCaseCommentsController();
            
            oHandler.sCaseId = oCase.Id;
            String sComment = oHandler.sCaseComment;
            String sLastModifiedDate = oHandler.sLastModifiedDate;
            Boolean bDisp = oHandler.bDateDisplay;
            oHandler.getComments();
            System.assertEquals(sComment, System.Label.HUMNoCaseComments);
            System.assertEquals(bDisp, false);
            List<Error_Log__c> lstLog = new List<Error_Log__c>();            
            Error_Log__c error = new Error_Log__c(Status__c = 'HUMPopUPForCaseCommentsController');            
            lstLog.add(error);                        
            oHandler.listErrorLogs = lstLog;            
            System.assert(oHandler.listErrorLogs.size() > 0);
        }
    }
}