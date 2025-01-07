/**********************************************************************************************************************************************************
Apex Class Name  : HUMCaseTrigger_Test
Version          : 1.0
Created Date     : March 08 2016
Function         : Test class for HUMCaseTrigger
***********************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------------
* Avinash Choubey            30464                    03/08/2016                  Original version
* Avinash Choubey            33344                    07/15/2016                  Changes made as part of REQ- 255151
* Pradeepkumar Dani			 99570					  06/08/2017				  Intent__c.Classification__c & Intent__c.Etl_Idtfr__c Clean up
* Pradeepkumar Dani			100996 					  06/13/2017				  Classification__c.Classification_Type__c & Etl_Idftr__c Clean Up
* Harika Devi Kalidindi     112000                    07/20/2017                  REQ - 323435 :Added Policy_Group_Number__c field value while inserting policy
* Gowthami Thota                                      08/14/2023                  QAS test class fix
************************************************************************************************************************************************************/

@isTest
public with sharing class HUMCaseTrigger_Test
{
    
    /**
    * This method creates test data for test class 
    *
    * @param  None      
    * @return  Void
    */
    @testSetup static void setUpTestData()
    {
        User usr = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert usr;
        system.assertEquals(usr.lastname=='test User1',true);
        system.runAs(usr)
        {
            RecordType AccountRecordType = [Select Id from RecordType where Name='Member' and sObjectType='Account'];
            
            //Creating Account Record
            Account accountRec = HUMTestDataHelperTest.getAccount();
            accountRec.OwnerId = usr.id;
            accountRec.FirstName = 'Test';
            accountRec.LastName = 'Account';
            accountRec.Enterprise_Id__c = '123456';
            accountRec.recordtypeid = AccountRecordType.id;
            insert accountRec;
            system.assertEquals(accountRec.recordtypeid == AccountRecordType.id,true);
            
            Policy__c policyRec = new Policy__c();
            policyRec.Group_Name__c = accountRec.Id;
            policyRec.Policy_Group_Number__c='11111';
            insert policyRec;
            system.assertEquals(policyRec.Group_Name__c == accountRec.Id,true);
            
            Policy_Member__c policyMemRec = new Policy_Member__c();
            policyMemRec.Name = '12345';
            policyMemRec.Policy__c = policyRec.Id;
            insert policyMemRec;
            system.assertEquals(policyMemRec.Policy__c == policyRec.Id,true);
            
            List<Classification__c> lstClassification = new List<Classification__c>();
            Classification__c classificationRec = new Classification__c();
            classificationRec.Name = 'Benefits';
            classificationRec.Is_Active__c = true;
            lstClassification.add(classificationRec);
            
            Classification__c classificationRecDec = new Classification__c();
            classificationRecDec.Name = 'Humana Pharmacy';
            classificationRecDec.Is_Active__c = true;
            lstClassification.add(classificationRecDec);
            insert lstClassification;
            system.assertEquals(lstClassification[0].Name == 'Benefits',true);
            
            List<Intent__c> lstIntent = new List<Intent__c>();
            Intent__c intentRec = new Intent__c();
            intentRec.Name = 'Rewards';
            intentRec.Is_Active__c = true;
            lstIntent.add(intentRec);
            
            Intent__c intentRecDec = new Intent__c();
            intentRecDec.Name = 'Estimate';
            intentRecDec.Is_Active__c = true;
            lstIntent.add(intentRecDec);
            insert lstIntent;
			system.assertEquals(intentRecDec.Name == 'Estimate', true);

            List<Case> lstCase = new List<Case>();
            Case caseRecord1 = new Case();
            caseRecord1.Owner_Queue__c = 'Cincinati CRU';
            caseRecord1.Status = 'In Progress';
            caseRecord1.AccountId = accountRec.id;
            caseRecord1.ownerId = usr.Id;
            caseRecord1.Classification_Type__c = 'Calls';
            caseRecord1.Classification_id__c = lstClassification[0].id;
            caseRecord1.Intent_id__c = lstIntent[0].id;
            caseRecord1.Interacting_With__c = accountRec.id;
            caseRecord1.Interacting_With_Type__c = 'Member';
            caseRecord1.Interacting_About_Type__c = 'Member';
            caseRecord1.Policy_Member__c = policyMemRec.Id;
            lstCase.add(caseRecord1);
            
            Case caseRecord2 = new Case();
            caseRecord2.Owner_Queue__c = 'cincinati Calls';
            caseRecord2.Status = 'In Progress';
            caseRecord2.AccountId = accountRec.id;
            caseRecord2.ownerId = usr.Id;
            caseRecord2.Classification_Type__c = 'Calls (RSO)';
            caseRecord2.Classification_id__c = lstClassification[0].id;
            caseRecord2.Intent_id__c = lstIntent[0].id;
            caseRecord2.Interacting_With__c = accountRec.id;
            caseRecord2.Interacting_With_Type__c = 'Member';
            caseRecord2.Interacting_About_Type__c = 'Member';
            caseRecord2.Policy_Member__c = policyMemRec.Id;
            lstCase.add(caseRecord2);
            
            Case caseRecord3 = new Case();
            caseRecord3.Owner_Queue__c = 'Louisville RSO Calls Support';
            caseRecord3.Status = 'In Progress';
            caseRecord3.AccountId = accountRec.id;
            caseRecord3.ownerId = usr.Id;
            caseRecord3.Classification_Type__c = 'Claims (RSO)';
            caseRecord3.Classification_id__c=lstClassification[0].id;
            caseRecord3.Intent_id__c = lstIntent[0].id;
            caseRecord3.Interacting_With__c = accountRec.id;
            caseRecord3.Interacting_With_Type__c = 'Member';
            caseRecord3.Interacting_About_Type__c = 'Member';
            caseRecord3.Policy_Member__c = policyMemRec.Id;
            lstCase.add(caseRecord3);
            insert lstCase;
            system.assertEquals(lstCase[0].Status == 'In Progress',true);
            
            List<HUMEncryptedFields__c> lstCS = new List<HUMEncryptedFields__c>();
            lstCS.add(new HUMEncryptedFields__c(Name = 'CommentBod', ObjectName__c = 'CaseComment', FieldName__c = 'CommentBody', EncryptionScheme__c = 'Stateless_AES_Encryption_without_Search'));
            insert lstCS;
            insert new HUMConstants__c [] {new HUMConstants__c( Name = 'HUMCaseComment', StringValue__c = 'CaseComment', IntegerValue__c = 0),
                                            new HUMConstants__c(Name = 'HUMStatelessAESEncryptionwithoutSearch', StringValue__c = 'Stateless_AES_Encryption_without_Search', IntegerValue__c = 0),
                                            new HUMConstants__c(Name = 'ZERO', StringValue__c = '0', IntegerValue__c = 0),
                                            new HUMConstants__c(Name = 'HUMExclamation', StringValue__c = '!', IntegerValue__c = 0),
                                            new HUMConstants__c(Name = 'HUMDoubleStar', StringValue__c = '**', IntegerValue__c = 0),
                                            new HUMConstants__c(Name = 'AMP', StringValue__c = '&', IntegerValue__c = 0),
                                            new HUMConstants__c(Name = 'HUMValueOne', StringValue__c = '1', IntegerValue__c = 1),
                                            new HUMConstants__c(Name = 'HUMOpenBrace', StringValue__c = '(', IntegerValue__c = 0),
                                            new HUMConstants__c(Name = 'HUMAT', StringValue__c = '@', IntegerValue__c = 0),
                                            new HUMConstants__c(Name = 'HUMDash', StringValue__c = '-', IntegerValue__c = 0),
                                            new HUMConstants__c(Name = 'HUMCommentBody', StringValue__c = 'CommentBody', IntegerValue__c = 0)  };
            insert new HUMTriggerSwitch__c (name='Case', Exeute_Trigger__c = true);
            insert new HUMQueueViewBatchSettings__c(name = 'QueueViewBatchSetup', TriggerExecutionRequired__c = true);
            insert new HUMCaseUpdateAllowedProfies__c(name = 'Customer Care Specialist');
        }
    }
    
    /**
    * This method is used to test update operation 
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void testBeforeAndAfterUpdate()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        
        System.runAs(testUser)
        {
            Test.startTest();
            Boolean bintentreq;
            List<case> caseRecords = [Select id from case where status='In Progress'];
            for(Case  objCase: caseRecords)
            {
            objCase.complaint__c = 'No';
            objCase.G_A_Rights_Given__c = 'No';
            }
            update caseRecords;
            System.assertEquals(caseRecords[0].G_A_Rights_Given__c == 'No', true);
            Test.stopTest();
        }
    }
    
    /**
    * This method is used to test Insert operation 
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void testBeforeAndAfterInsert()
    {
        User testUser = [Select Id, Name from User where LastName='test User1'];
        
        System.runAs(testUser)
        {
            Test.startTest();
            Account accountRec = [select id from Account where FirstName = 'Test' limit 1];
            List<Case> lstCase = new List<Case>();
            Case caseRecord1 = new Case();
            caseRecord1.Owner_Queue__c = 'Cincinati CRU';
            caseRecord1.Status = 'In Progress';
            caseRecord1.AccountId = accountRec.id;
            caseRecord1.ownerId = testUser.Id;
            caseRecord1.Classification_Type__c = 'Calls';
            caseRecord1.Interacting_With__c = accountRec.id;
            caseRecord1.Interacting_With_Type__c = 'Member';
            caseRecord1.Interacting_About_Type__c = 'Member';
            lstCase.add(caseRecord1);
            
            Case caseRecord2 = new Case();
            caseRecord2.Owner_Queue__c = 'cincinati Calls';
            caseRecord2.Status = 'In Progress';
            caseRecord2.AccountId = accountRec.id;
            caseRecord2.ownerId = testUser.Id;
            caseRecord2.Classification_Type__c = 'Calls (RSO)';
            caseRecord2.Interacting_With__c = accountRec.id;
            caseRecord2.Interacting_With_Type__c = 'Member';
            caseRecord2.Interacting_About_Type__c = 'Member';
            lstCase.add(caseRecord2);
            
            Case caseRecord3 = new Case();
            caseRecord3.Owner_Queue__c = 'Louisville RSO Calls Support';
            caseRecord3.Status = 'In Progress';
            caseRecord3.AccountId = accountRec.id;
            caseRecord3.ownerId = testUser.Id;
            caseRecord3.Classification_Type__c = 'Claims (RSO)';
            caseRecord3.Interacting_With__c = accountRec.id;
            caseRecord3.Interacting_With_Type__c = 'Member';
            caseRecord3.Interacting_About_Type__c = 'Member';
            lstCase.add(caseRecord3);
            
            insert lstCase;
            system.assertEquals(lstCase[0].Status=='In Progress', true);
            
            Test.stopTest();
        }
    }
}