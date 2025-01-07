/***********************************************************************************************************
Apex Class Name  : TaskCommentsDataTable_LT_HUM
Version          : 1.0
Function         : Test class for TaskCommentsDataTable_LT_HUM
************************************************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Manoj M                   Vinay L             30/06/22                    Test class for TaskCommentsDataTable_LD_HUM.cls
* Prasuna Pattabhi                            08/24/23                 US 4412371 Market Credentialing Task Fields
************************************************************************************************************/
@isTest
Private with sharing Class TaskCommentsDataTable_LT_HUM {
 /*
* This method creates test data for test class
* @param None
* @return None
*/
    @testSetup static void setUpTestData()
    {     
        User oAdminUser = HUMTestDataHelperTest.createTestUser('Admin','system Administrator');
        oAdminUser.Current_Queue__C = 'Cincinati CRU1';
        insert oAdminUser;
        System.assertEquals(oAdminUser.LastName,'test Admin');
        
        User objUser1 = HUMTestDataHelperTest.createTestUser('User2','Customer Care Supervisor');
        objUser1.IsActive = true;
        insert objUser1;
        system.assertEquals(objUser1.lastname=='test User2',true);
        
        User objUser2 = HUMTestDataHelperTest.createTestUser('User4','Customer Care Specialist');
        objUser2.IsActive = true;
        insert objUser2;
        system.assertEquals(objUser2.lastname=='test User4',true);
        
        PermissionSet pRSOHP = [SELECT ID From PermissionSet WHERE Name = 'CRMS_206_CCSHumanaPharmacyAccess' LIMIT 1];
        insert New PermissionSetAssignment(AssigneeId = objUser1.id, PermissionSetId = pRSOHP.id);
        
        User objUser3 = HUMTestDataHelperTest.createTestUser('User3','Humana Pharmacy Specialist');
        objUser3.IsActive = true;
        insert objUser3;
        system.assertEquals(objUser3.lastname=='test User3',true);
        
         PermissionSet pRSOHP1 = [SELECT ID From PermissionSet WHERE Name = 'CRMS_300_Humana_Pharmacy_Supervisor' LIMIT 1];
        insert New PermissionSetAssignment(AssigneeId = objUser3.id, PermissionSetId = pRSOHP1.id);
        
        system.runAs(oAdminUser)
        {
            // Insert Member Account
            Account objMemAccount = HUMTestDataHelperTest.getAccount();
            objMemAccount.FirstName = 'Test FName';
            objMemAccount.LastName = 'LName'; 
            objMemAccount.source_platform_code__c = 'EM' ;
            objMemAccount.Enterprise_Id__c = String.valueof(2000);
            objMemAccount.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            objMemAccount.Birthdate__c = String.valueOf(System.today().month()+'/'+system.today().day()+'/'+System.today().year());
            insert objMemAccount;
            system.assert(objMemAccount.LastName == 'LName');
            
            // Insert Member Account
            Account objMemAccountLV = HUMTestDataHelperTest.getAccount();
            objMemAccountLV.FirstName = 'Test FName';
            objMemAccountLV.LastName = 'LName2'; 
            objMemAccountLV.source_platform_code__c = 'LV' ;
            objMemAccountLV.Enterprise_Id__c = String.valueof(2001);
            objMemAccountLV.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            objMemAccountLV.Birthdate__c = String.valueOf(System.today().month()+'/'+system.today().day()+'/'+System.today().year());
            insert objMemAccountLV;
            system.assert(objMemAccountLV.LastName == 'LName2');
            
            //Case Object Test Data
            
            Case caseRecord = new Case();
            caseRecord.Owner_Queue__c = 'Louisville RSO Calls Support';
            caseRecord.Status = 'In Progress';
            caseRecord.AccountId = objMemAccount.id;
            caseRecord.ownerId = objUser1.Id;
            caseRecord.Interacting_With__c = objMemAccount.id;
            caseRecord.Interacting_With_Type__c = 'Member';
            
            insert caseRecord;
            System.assertequals(caseRecord.Status,'In Progress');            
            
            //Task  data
            Task objTask = new Task();
            objTask.Status = 'In Progress';
            objTask.Subject = 'Test Task Record';
            objTask.ActivityDate= System.today();
            objTask.Type = 'Work Task';
            objTask.WhatId=caseRecord.Id;
            insert objTask;
            
            //Task  data
            Task objTask1 = new Task();
            objTask1.Status = 'In Progress';
            objTask1.Subject = 'Test Task Record';
            objTask1.ActivityDate= null;
            objTask1.Type = 'Work Task';
            objTask1.WhatId=caseRecord.Id;
            insert objTask1;
          
            Task_Field_History__c tcObj = new Task_Field_History__c();
            tcObj.SObjFieldName__c='Description';
            tcObj.SObjFieldValue__c = '(TEST) Comment';
            tcObj.SObjID__c = objTask.Id;
            insert tcObj;
            System.assertEquals(tcObj.SObjFieldValue__c, '(TEST) Comment');
            
            Task_Field_History__c tcObjMod = new Task_Field_History__c();
            tcObjMod.SObjFieldName__c='Description';
            tcObjMod.SObjFieldValue__c = 'TEST Comment';
            tcObjMod.SObjID__c = objTask.Id;
            insert tcObjMod;
            System.assertEquals(tcObjMod.SObjFieldValue__c, 'TEST Comment');
        } 
    }
    
 /*
* Method Name : testTaskCommentCCS
* Description : This method Is used To test generate, update & 
delete task comments from Customer Care Specialist profile.
* Return type : None
* Parameter   : None
*/
    
    
    Static testMethod void testTaskCommentCCS()
    {
        User testUser = [Select Id, Name from User where LastName='test User2' limit 1];        
        System.runAs(testUser)
        {
            Test.startTest();
            List<Task> objTask = [Select id,WhatId from Task limit 1];
            List<Task> objTask1 = [Select id,WhatId from Task where ActivityDate=null limit 1];
            List<Task_Field_History__c> tcObjList = new List<Task_Field_History__c> ();
            tcObjList = [Select ID,Created_At__c,SObjFieldName__c,SObjFieldValue__c,CreatedBy.Name,ParentFieldHistory_Id__c,SObjID__c, LastModifiedDate, LastModifiedBy.Name , CreatedDate from Task_Field_History__c   WHERE   SObjID__c = :objTask[0].id];
            TaskCommentsDataTable_LD_HUM.getCurrentUserProfileName();            
            TaskCommentsDataTable_LD_HUM.generateTaskComments(objTask[0].id);
            TaskCommentsDataTable_LD_HUM.closeTask(objTask[0].id);
            TaskCommentsDataTable_LD_HUM.getTaskRelatedToCase(objTask[0].WhatId);
            TaskCommentsDataTable_LD_HUM.getTaskRelatedToCase(objTask1[0].Id);
            TaskCommentsDataTable_LD_HUM.getTaskRelatedToCase(objTask[0].Id);
            TaskCommentsDataTable_LD_HUM.updateTaskCommentData(tcObjList[0].Id ,tcObjList[0].SObjFieldValue__c);
            string taskCommentData='[{"SObjFieldValue__c":"Testing", "SObjID__c":"'+objTask[0].ID+'"}]';
            TaskCommentsDataTable_LD_HUM.saveTaskCommentData(objTask[0].id,taskCommentData);
            try{
                TaskCommentsDataTable_LD_HUM.saveTaskCommentData('1234','test');
            }
            catch (Exception e) {
               
            }
            
            Test.stopTest();
        }
    }
    
    Static testMethod void testCatchCover()
    {
        User testUser = [Select Id, Name from User where LastName='test User4' limit 1];        
        System.runAs(testUser)
        {
            Test.startTest();
           
            try{
                TaskCommentsDataTable_LD_HUM.saveTaskCommentData('1234','test');
                TaskCommentsDataTable_LD_HUM.closeTask('1234');
            }
            catch (Exception e) {
            }
            
            Test.stopTest();
        }
    }
    
     Static testMethod void testUserLoggedIn()
    {
        User testUser = [Select Id, Name from User where LastName='test User3' limit 1];        
        System.runAs(testUser)
        {
            TaskCommentsDataTable_LD_HUM.checkPermissionForCommentDropDown();
        }
        
        User testUser4 = [Select Id, Name from User where LastName='test User4' limit 1];        
        System.runAs(testUser4)
        {
            TaskCommentsDataTable_LD_HUM.checkPermissionForCommentDropDown();
        }
    }
    /**
    * 
    *<p>
    * This method is used to cover credentialingClosable method on TaskCommentsDataTable_LD_HUM Class.
    * @param            NULL
    * @return           void
    */
    static testMethod void testCredentialingEligible(){
        
        User credentialingSpec = HUMTestDataHelperTest.createTestUser('Credentialing','Customer Care Specialist');
        insert credentialingSpec;
        system.assertEquals(credentialingSpec.lastname=='test Credentialing',true);     
       
    
        system.runAS(credentialingSpec){
           
            RecordType AccountRecordType = [Select Id from RecordType where Name='Member' and sObjectType='Account'];
            
            Account accountRec = HUMTestDataHelperTest.getAccount();
            accountRec.OwnerId = credentialingSpec.id;
            accountRec.FirstName = 'Test';
            accountRec.LastName = 'Account';
            accountRec.Enterprise_Id__c = '123456';
            accountRec.recordtypeId = AccountRecordType.id;
            insert accountRec;
            system.assertEquals(accountRec.recordtypeId==AccountRecordType.id,true);		
            
            Classification_Type__c ctRec = new Classification_Type__c();
            ctRec.name = 'Provider';
            ctRec.Is_Active__c=true;
            insert ctRec; 
             
            Classification__c classificationRecDec = new Classification__c();
            classificationRecDec.Name = 'Provider Requests';
            classificationRecDec.Is_Active__c = true;
            insert classificationRecDec;

            Intent__c intentRecDec = New Intent__c();
            intentRecDec.Name = 'LA Medicaid Cred Workflow';
            intentRecDec.Is_Active__c = true;
            insert intentRecDec;
                
            CTCI_Junction__c ctciRec = New CTCI_Junction__c();
            ctciRec.Classification_Type__c = ctRec.id;
            ctciRec.Classification__c = classificationRecDec.Id;
            ctciRec.Intent__c = intentRecDec.id;
            ctciRec.IsActive__c = true;
            insert ctciRec;

            Case oCase = new Case(AccountId=accountRec.Id, 
                CTCI_List__c = ctciRec.Id, CoKY_Case__c = True,
                Interacting_With_Type__c = 'Member',Interacting_With__c=accountRec.Id, 
                Due_Date__c=Date.Today(), Case_Owner__c=UserInfo.getUserName(), 
                Owner_Queue__c = 'Market National Network Ops', Work_Queue_View_Name__c='Cincinati CRU1',
                Service_Center__c ='Cincinnati',Department__c='CRU1', status='In Progress',
                Tenant_Id__c = '00',Interacting_About_Type__c = 'Member');
            insert oCase;

            Task oTasknt = HUMTestDataHelperTest.getTaskRecord();
            oTasknt.WhatId = oCase.Id;
            oTasknt.Task_Owner__c = 'User1';
            oTasknt.Owner_Queue__c ='Market National Network Ops';            
            oTasknt.status = 'In Progress';
            oTasknt.Service_Center__c = 'Cincinnati';
            oTasknt.Department__c = 'CRU1';
            oTasknt.Callback_Requested__c = 'Yes';
            oTasknt.Time_Zone__c = 'EST - Eastern';
            oTasknt.Call_Back_Start__c = datetime.now().time();
            oTasknt.Call_Back_End__c= datetime.now().time();
            oTasknt.Description = 'Test Comments1';
            oTasknt.QueueOrUserId__c = credentialingSpec.Id;
            oTasknt.Type ='Notification Task';
            oTasknt.RecordTypeId = Schema.sObjectType.Task.getRecordTypeInfosByName().get('Service CRM').getRecordTypeId();
            insert oTasknt;

            Test.startTest();   
            TaskCommentsDataTable_LD_HUM.credentialingClosable(oTasknt.Id);
            Test.stopTest();

      }    
    }
}