/**
@Author      : Pradeepkumar Dani
@name        : CaseAttachments_T_HUM
@CreateDate  : 10/30/2017
@Description : This test class ensures CaseAttachments_C_HUM coverage
@Version     : 1.0
@reference   : CaseAttachments_C_HUM

Modification Log:
* Developer Name           Code Review                 Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Pradeepkumar Dani          142531                 10/30/2017                     Original version
* Pradeepkumar Dani          157391                 12/14/2017                     Datatable updates
* Srivinas damera            293796                 09/30/2018                     REQ - 369721 - eHub DSS 2.0 upgrade
* Mohammed Noor                                     09/18/2020                     User Story 1334690 - MRM Viewer changes
* Santhi Mandava                                    02/01/2021                     Fixed test class failure issue
********************************************************************************************************************************/
@isTest
private with sharing class CaseAttachments_T_HUM
{
    /*
    * This method creates test data for test class
    * @param None
    * @return None
    */
    @testSetup static void setUpTestData()
    {
        User testUser = HUMTestDataHelperTest.createTestUser('User1', 'Customer Care Specialist');
        insert (new HUMConstants__c(IntegerValue__c=1,StringValue__c='1',Name=' HUMLISTSIZEONE'));
        insert testUser;
        system.assertEquals(testUser.lastname == 'test User1', true);
        HUMTestDataHelperTest.getHUMConstantsData();

        system.runAs(testUser)
        {
            RecordType AccountRecordType = [Select Id from RecordType where Name='Member' and sObjectType='Account'];
        
            //Creating Account Record
            Account accountRec = HUMTestDataHelperTest.getAccount();
            accountRec.OwnerId = testUser.id;
            accountRec.FirstName = 'Test';
            accountRec.LastName = 'Account';
            accountRec.Enterprise_Id__c = '123456';
            accountRec.recordtypeId = AccountRecordType.id;
            accountRec.ETL_Record_Deleted__c = true;
            insert accountRec;
            system.assertEquals(accountRec.recordtypeId==AccountRecordType.id,true);
            
            Case objCase = HUMTestDataHelperTest.getCase();
            objCase.AccountId = accountRec.Id;
            objCase.Interacting_With__c = accountRec.Id;
            insert objCase;
            system.assertNotEquals(objCase, null, 'Value not matched');

            Task objTask = HUMTestDataHelperTest.getTaskRecord();
            objTask.WhatId = objCase.Id;
            objTask.Type = 'Work Task';
            insert objTask;
            system.assertNotEquals(objTask, null, 'Value not matched');

            Attachment__c objAttach = HUMTestDataHelperTest.getCustomAttachment();
            objAttach.Related_To_Task__c = (ID)(objTask.Id);
            objAttach.Related_To_Case__c = objCase.Id;
            objAttach.DocId__c = '1234567';
            objAttach.Version_Id__c = '1';          
            insert objAttach;
            system.assertNotEquals(objAttach, null, 'Value not matched');
                        
            HUM_Webservice_Callout__c oMRMViewerLinkNew = new HUM_Webservice_Callout__c();
            oMRMViewerLinkNew.Name = GLOBAL_CONSTANT_HUM.sMRMViewerURL;                
            oMRMViewerLinkNew.Request_Type__c = 'POST';
            oMRMViewerLinkNew.Active__c = true;
            oMRMViewerLinkNew.End_Point_URL__c = 'https://test.humana.com/';
            insert oMRMViewerLinkNew;
            system.assertNotEquals(oMRMViewerLinkNew, null);
                        
        }
    }

    /*
    * This method is used to test the attachment functionality
    * @param   None
    * @return  None
    */
    static testMethod void testAtts()
    {
        User testUser = [SELECT Id, Name FROM User WHERE LastName = 'test User1'];

        system.runAs(testUser)
        {
            Test.startTest();

            Case caseObj = [SELECT Id FROM Case LIMIT 1];
            ApexPages.currentPage().getParameters().put('Id', caseObj.Id);
            CaseAttachments_C_HUM ca = new CaseAttachments_C_HUM();
            list<CaseAttach_DTO_HUM> lstAtts = CaseAttachments_C_HUM.getJSONData(caseObj.Id);
            system.assertEquals(lstAtts.size() > 0, TRUE);

            list<Attachment__c> lstAtt = [SELECT Id, Attachment_URL_Text__c, (SELECT Id FROM Attachments LIMIT 1) FROM Attachment__c LIMIT 1];
            lstAtt.get(0).Attachment_URL_Text__c = 'Test URL';
            update lstAtt;
            String urlEHub = CaseAttachments_C_HUM.getURL(lstAtt.get(0).Id);
            system.assertEquals(urlEHub == 'Test URL', TRUE);

            lstAtt.get(0).Attachment_URL_Text__c = '';
            update lstAtt;
                        
            Attachment attach = new Attachment();
            attach.Name = 'Unit Test Attachment';
            Blob bodyBlob = Blob.valueOf('Unit Test Attachment Body');
            attach.body = bodyBlob;
            attach.parentId = lstAtt.get(0).Id;
            insert attach;

            urlEHub = CaseAttachments_C_HUM.getURL(lstAtt.get(0).Id);
            system.assertEquals(urlEHub != 'Test URL', TRUE);
            
            CaseAttachments_C_HUM objCA = new CaseAttachments_C_HUM();
            objCA.toGetCaseId = caseObj.Id;
            objCA.verifyLegacyDelete();
            
            lstAtt.get(0).DocId__c = '';
            update lstAtt;
            urlEHub = CaseAttachments_C_HUM.getURL(lstAtt.get(0).Id);
            lstAtt.get(0).DocId__c = '';
            lstAtt.get(0).Attachment_URL_Text__c = '';
            lstAtt.get(0).Version_Id__c = '';
            lstAtt.get(0).Document_Source__c = 'LWC';
            update lstAtt;
            urlEHub = CaseAttachments_C_HUM.getURL(lstAtt.get(0).Id);
            
            Case ObjCase = [Select Id, Owner_queue__c , ownerid , owner__c from Case where Case_Owner__c != 'Cincinati_CRU1' Limit 1];
        		Attachment__c objAttachment = new Attachment__c(Related_to_case__c = ObjCase.id,DocId__c='12345',Version_Id__c='1',Attachment_URL_Text__c='www.google.com');
        		insert objAttachment;
        		Attachment__c queryAttachment = [Select Id , Created_By_Queue__c from Attachment__c where Id = :objAttachment.Id];
        		
				List<Attachment__c> lstAttachments = new List<Attachment__c>();
                lstAttachments.Add(objAttachment);
               
            List<ContentVersion> lstContentVersion = new  List<ContentVersion>();
            //Create Content Version          
            ContentVersion contentVersion = new ContentVersion(
                Title = 'Penguins',
                PathOnClient = 'Penguins.jpg',
                VersionData = Blob.valueOf('Test Content'),
                IsMajorVersion = true,
                FirstPublishLocationId =ObjCase.id
                //Attachment__c=queryAttachment.id
            );
            insert contentVersion;
            contentVersion.Attachment__c=queryAttachment.id;
            update contentVersion;
 			queryAttachment.DocId__c = '';
            queryAttachment.Attachment_URL_Text__c = '';
            queryAttachment.Version_Id__c = '';
            queryAttachment.Document_Source__c = 'LWC';
            update queryAttachment;
            Map<Id,Id> attIdConVerId = new Map<Id,Id>();
            attIdConVerId.put(queryAttachment.id,contentVersion.id);
            urlEHub = CaseAttachments_C_HUM.getURL(queryAttachment.id);
            
            system.assertEquals(urlEHub != 'Test URL', TRUE);
            
            Test.stopTest();
        }
    }
}