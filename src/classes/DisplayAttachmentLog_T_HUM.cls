/**************************************************************************************************************
Apex Class Name : DisplayAttachmentLog_T_HUM
Version         : 1.0
Created On      : 03/28/2016
Function        : Test class for DisplayAttachmentLog_C_HUM

* Modification Log:
* Developer Name            Code Review                 Date                       Description
*----------------------------------------------------------------------------------------------------------------
* Santhi Mandava                                        03/28/2016                 Original version
* Manish Kumar                                          07/18/2016                 Worked on REQ-275930. Enabled encryption for logged records 
* Santhi Mandava                                        10/17/2018                 Implemented platform migration changes
* Santhi Mandava                                        03/12/2019                 Replaced "Endpoint_Url__c" with Endpoint_Url_HUM__c.
*****************************************************************************************************************/
@isTest
private class DisplayAttachmentLog_T_HUM
{
    /*
    * Method Name : setup
    * Description : This method is used to create test data for the class 
    * Return type : void
    * Parameter   : NA
    */
    @testSetup static void setup() 
    {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        oUser.Current_Queue__c = 'Cincinnati CRU1';
        oUser.Network_User_Id__c = 'SXS2222';
        insert oUser;
        System.assertEquals(oUser.LastName, 'test User1');
        system.runAs(oUser)
        {
            LoggingTestDataSetup_T_HUM.createTestData();
            List<Case> lstCases = [Select Id,Intent_ID__r.name ,Intent_Id__c from Case where Intent_ID__r.name =: 'Billing'];
            System.assertEquals(1,lstCases.size());
            Attachment_log__c  objAttchLog = LoggingTestDataSetup_T_HUM.createAttachmentLog(lstCases[0].Id);
            System.assertEquals('Auth Summary',objAttchLog.Attachment_Type__c);
            
            Endpoint_Url_HUM__c epurl = new Endpoint_Url_HUM__c(Name='CipherCloudUrl', Remote_Url__c='http://ds.ciphercloud.com/ds/');
            insert epurl;
            System.assertNotEquals(Null,epurl.Id);
            
            HUMConstants__c objCon = new HUMConstants__c(Name='ATTACHMENT_MIGRATION_DATE',StringValue__c='11/03/2018',IntegerValue__c=0);
            insert objCon;
            System.assertNotEquals(Null,objCon.Id);
            
            HUMCipherCloudSettings__c hcs = new HUMCipherCloudSettings__c(Name='APPLICATION_NAME', Value__c='CRMSTEST');
            insert hcs;
            System.assertNotEquals(Null,hcs.Id);
            
             for(integer i = 0; i<=4;i++)
            {
                LoggingTestDataSetup_T_HUM.createAttachment(objAttchLog.Id);
            }
        }
    }
    
    /*
    * Method Name : testFunctionality
    * Description : This method is used to test main functionality.
    * Return type : void
    * Parameter   : NA
    */
    static testMethod void testFunctionality() 
    {
        User oUser = [Select Id,Network_User_Id__c from User where LastName = 'test User1'];
        System.assertEquals('SXS2222', oUser.Network_User_Id__c);
        
        System.runAs(oUser)
        {
            test.startTest();
            List<Attachment_Log__c> lstAttachs = [Select Id from Attachment_Log__c limit 50000];
            System.assertnotEquals(lstAttachs.size(),0);
            ApexPages.currentPage().getParameters().put('attachId', lstAttachs[0].Id);
            ApexPages.currentPage().getParameters().put('createdbyqueue', 'Cincinaati CRU');
            ApexPages.currentPage().getParameters().put('stype', 'Auth Summary');
            DisplayAttachmentLog_C_HUM objClass = new DisplayAttachmentLog_C_HUM();
            System.assertEquals(6, objClass.lstAttachments.size());
            
            List<Attachment> lstAttchments = [Select Id from Attachment];
            
            String latAttId = '';
            For(Attachment obj : lstAttchments)
            {
                latAttId = latAttId + '#' +obj.Id;
            }
            ApexPages.currentPage().getParameters().put('pageIds', latAttId );
            objClass.getCurrentPageImages();
            System.assertNotEquals(Null, DisplayAttachmentLog_C_HUM.sJSONString );
            test.stopTest();
        }
    }
}