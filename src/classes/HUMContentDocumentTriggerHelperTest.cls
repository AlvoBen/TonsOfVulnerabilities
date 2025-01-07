/****************************************************************************
Apex Class Name  : HUMContentDocumentTriggerHelperTest 
Version          : 1.0 
Created Date     : JUNE 09 2014
Function         : Test class to HUMContentDocumentTriggerHelper  Apex class
Modification Log :
 *   Modification ID     Developer          Code Review      Date                 Description
 * ------------------------------------------------------------------------------------------------------------------------------                
 *  Vijaya Lakshmi Tummala                 	 10/17/2022     Original Version-Case Attachment -Feature-3033033
 * 															UserStory -3775271 Creating Case Attachments in Lightning		
**********************************************************************************************************************************/
@IsTest
public class HUMContentDocumentTriggerHelperTest {
 /**
    * Returns void
    * <p>
    * Method to setup Testdata 
    *
    * @return  Void
    * @see
    */
    @testSetup static void setup() 
    {    
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser; 
        System.runas(oUser) 
        {
            Integration_Mapping__c intMap = new Integration_Mapping__c();
            intMap.Mapping__c = 'Mapping:test;HUM';
            insert intMap;
            system.assertEquals(intMap.Mapping__c == 'Mapping:test;HUM',true);
            
            HUMMember_Search_State_Mapping__c cstsetting = new HUMMember_Search_State_Mapping__c(Name = 'test', State__c = 'California', State_Code__c = 'CA');
            insert cstsetting;
            system.assertEquals(cstsetting.Name == 'test',true);
            
            Account oacc = HUMTestDataHelperTest.getAccount();
            oacc.Name = 'Test Account';
            insert oacc;
            system.assertEquals(oacc.Name == 'Test Account',true);
            
            Interaction__c objInterac = new Interaction__c();
            objInterac.Caller_Name__c = 'Test Caller';
            objInterac.Interaction_Origin__c = 'Inbound Call';
            objInterac.UUID__c = '12345';
            insert objInterac;
            system.assertEquals(objInterac.Interaction_Origin__c == 'Inbound Call',true);
            
            Classification__c classificationRecDec = new Classification__c();
            classificationRecDec.Name = 'Benefits';
            classificationRecDec.Is_Active__c = true;
            insert classificationRecDec;
            system.assertEquals(classificationRecDec.Name == 'Benefits',true);
            
            Intent__c intentRec = new Intent__c();
            intentRec.Name = 'Rewards';
            intentRec.Is_Active__c = true;
            insert intentRec;
            system.assertEquals(intentRec.name == 'Rewards',true);
            
            Case caseObj = HUMTestDataHelperTest.getCase();
            caseObj.Classification_ID__c = classificationRecDec.id;
            caseObj.Intent_ID__c = intentRec.id;
            caseObj.Interaction_ID__c = objInterac.Id;
            insert caseObj;
            system.assertEquals(caseObj.Interacting_With_Name__c == 'Test',true);
            
            Integration__c inte = new Integration__c();
            inte.Name = String.escapeSingleQuotes('Soft Phone Routing');
            insert inte;
            
            List<HUMAccountCaseRecordTypes__c> HUMAccountCaseRecordTypesList = new List<HUMAccountCaseRecordTypes__c>();
            HUMAccountCaseRecordTypes__c HUMAccountCaseRecordTypes1 = new HUMAccountCaseRecordTypes__c(Name = 'Group' ,Account_RecordType__c = 'Group' , Case_RecordType__c = 'Group Case');
            HUMAccountCaseRecordTypesList .add(HUMAccountCaseRecordTypes1);
            HUMAccountCaseRecordTypes__c HUMAccountCaseRecordTypes2 = new HUMAccountCaseRecordTypes__c(Name = 'Agent/Broker' ,Account_RecordType__c = 'Agent/Broker' , Case_RecordType__c = 'Agent/Broker Case');
            HUMAccountCaseRecordTypesList .add(HUMAccountCaseRecordTypes2);        
            HUMAccountCaseRecordTypes__c HUMAccountCaseRecordTypes3 = new HUMAccountCaseRecordTypes__c(Name = 'Provider' ,Account_RecordType__c = 'Provider' , Case_RecordType__c = 'Provider Case');
            HUMAccountCaseRecordTypesList .add(HUMAccountCaseRecordTypes3);        
            HUMAccountCaseRecordTypes__c HUMAccountCaseRecordTypes4 = new HUMAccountCaseRecordTypes__c(Name = 'Member' ,Account_RecordType__c = 'Member' , Case_RecordType__c = 'Member Case');
            HUMAccountCaseRecordTypesList .add(HUMAccountCaseRecordTypes4);        
            insert HUMAccountCaseRecordTypesList;
            system.assertEquals(HUMAccountCaseRecordTypesList[0].Name == 'Group',true);
            
            HUMConstants__c cstHumConst = new HUMConstants__c(Name ='testcustomsetting',StringValue__c = 'test',IntegerValue__c = 123);
            insert cstHumConst;
            system.assertEquals(cstHumConst.Name == 'testcustomsetting',true);
            
            Account oAccount = HUMTestDataHelperTest.getAccount();
            oAccount.Name = 'Test Account';
            insert oaccount;
            
            HUMTestDataHelperTest.getHUMConstantsData();            
            
            List<HUMCipherCloudSettings__c> lstApplicationName = new List<HUMCipherCloudSettings__c> ();
            lstApplicationName.add(new HUMCipherCloudSettings__c(Name = 'APPLICATION_NAME',  Value__c = 'CRMR1'));
            insert lstApplicationName;
            system.assertEquals(lstApplicationName[0].Name == 'APPLICATION_NAME',true);
            
            Account acc = HUMTestDataHelperTest.getAccount();
            acc.Name='Test Field';
            acc.Account_External_Id__c='1231';
            acc.RecordtypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Agent/Broker').getRecordTypeId();
            insert acc;
            system.assert(acc.Name == 'Test Field'); 
            
        }
    }
     /**
    * Test method to test when attachments are uploaded from classic
    * <p>
    * @return   :    void
    * @param    :    NA
    */
    static testMethod void testClassicAttachments()
    {
    	   User oUser = [select id, name,lastname,Current_queue__c from user where lastname='test User1' limit 1];
        system.assertEquals(oUser.lastname =='test User1',true); 
        system.runAs(oUser)
        { 
        	Test.StartTest();
        		Case ObjCase = [Select Id, Owner_queue__c , ownerid , owner__c from Case where Case_Owner__c != 'Cincinati_CRU1' Limit 1];
        		Attachment__c objAttachment = new Attachment__c(Related_to_case__c = ObjCase.id,DocId__c='12345',Version_Id__c='1',Attachment_URL_Text__c='www.google.com');
        		insert objAttachment;
        		Attachment__c queryAttachment = [Select Id , Created_By_Queue__c from Attachment__c where Id = :objAttachment.Id];
        		
				List<Attachment__c> lstAttachments = new List<Attachment__c>();
                lstAttachments.Add(objAttachment);
               
            
            //Create Content Version          
            ContentVersion contentVersion = new ContentVersion(
                Title = 'Penguins',
                PathOnClient = 'Penguins.jpg',
                VersionData = Blob.valueOf('Test Content'),
                IsMajorVersion = true,
                FirstPublishLocationId =ObjCase.id,
                Attachment__c=objAttachment.id
            );
            insert contentVersion;   
            
            List<ContentDocument> documents = [SELECT Id, Title,ContentDocument.ParentId, LatestPublishedVersionId FROM ContentDocument where LatestPublishedVersionId =:contentVersion.id limit 1];
            map<Id,ContentDocument> mapCustomDocuments = new map<Id,ContentDocument>();
         for(ContentDocument conDoc:documents)
        {
            mapCustomDocuments.put(conDoc.id,conDoc);
        }
            
            HUMContentDocumentTriggerHelper.onBeforeInsert(documents);
            
            HUMContentDocumentTriggerHelper.onBeforeUpdate(documents,documents,mapCustomDocuments,mapCustomDocuments);
            HUMContentDocumentTriggerHelper.onBeforeDelete(documents);
        	Test.StopTest();
        }
    }
}