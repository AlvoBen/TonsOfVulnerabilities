/*****************************************************************************************
Apex class Name    : PopUpComments_T_HUM
Version            : 1.0
Function           : Test class for PopUpComments_C_HUM
Created Date       : August 1 2017

Modification Log:
*   Developer          Date               Description
* ------------------------------------------------------------------------------------------------------------------------------                
*   Siddharth Mehta    08/01/2017         Original Version
*   Santhi Mandava     03/12/2019         Removed "DpaaSWebServiceMockImpl" usage.
*********************************************************************************************************************************/

@isTest
private class PopUpComments_T_HUM {

   /*
    * Test Setup for overall code coverage
    * @param     NA
    * @return    void
    */
    @testSetup static void setup()
    {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        Group oGrp = new Group(Type='Queue', Name='Cincinnati_CRU1', DeveloperName = 'Cincinnati_CRU1');
        insert oGrp;
        system.assert(oGrp.Type =='Queue');
        GroupMember gm= new GroupMember(); 
        gm.GroupId=oGrp.id;
        gm.UserOrGroupId = oUser.id;
        insert gm;
            
        User oCUser = [Select Id, Name, email from User where Id=:gm.UserOrGroupId LIMIT 1];
        system.assertEquals(gm.GroupId,oGrp.id);

        QueuesObject oQueueObject = new QueuesObject (QueueID = oGrp.id, SobjectType ='Work_Queue_Setup__c');
        insert oQueueObject ;  
        System.assert(oQueueObject.SobjectType == 'Work_Queue_Setup__c');
        
        System.runAs (oUser) 
        {
            Account oAccount = HUMTestDataHelperTest.getAccount();
            oAccount.Name = 'New Case Account';
            oAccount.Group_Number__c  = '123';
            oAccount.BillingState = 'New York';
            oAccount.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
            insert oAccount;
            System.assert(oAccount.BillingState == 'New York');
            
            List<HUMConstants__c> lstHUMConstants = new List<HUMConstants__c>();
            lstHUMConstants.add(new HUMConstants__c(Name='HUMConstantOne', StringValue__c='1', IntegerValue__c = 1));
            lstHUMConstants.add(new HUMConstants__c(Name='HUMLISTSIZEZERO', StringValue__c='',IntegerValue__c = 0));
            lstHUMConstants.add(new HUMConstants__c(Name='HUMCompleted',StringValue__c='Completed'));
            lstHUMConstants.add(new HUMConstants__c(Name='HUMClosed',StringValue__c='Closed'));
            lstHUMConstants.add(new HUMConstants__c(Name='HUMWorkTask',StringValue__c='Work Task'));
            lstHUMConstants.add(new HUMConstants__c(Name='HUMNotificationTask',StringValue__c='Notification Task'));
            insert lstHUMConstants;
            System.assert(lstHUMConstants.size() > 0 );
            
            Classification_Type__c oClassType = new Classification_Type__c();
            oClassType.Guidance_Alert_Limit__c = 2;
            oClassType.Is_Active__c = true;
            insert oClassType;
            
            Work_Queue_Setup__c oWqs = new Work_Queue_Setup__c(Name=oGrp.Name);
            oWqs.Public_Group_Name__c ='Cincinnati CRU1';
            oWqs.Service_Center__c ='Cincinnati';
            oWqs.Department__c ='CRU1';
            oWQS.CokY__c =true;
            oWqs.TenantID__c ='00';
            oWqs.Classification_Type__c = oClassType.Id;  
            oWqs.IsActive__c = TRUE;            
            oWqs.OwnerId = oGrp.Id; 
            insert oWqs;
            system.assert(oWqs.TenantID__c == '00');
            
            HUMTaskRecordTypes__c oRecTypes =  new HUMTaskRecordTypes__c(Name='RecordType');
            oRecTypes.ServiceCRMRecTypeName__c = 'Service CRM';
            oRecTypes.CSIRecordTypeName__c = 'Service CRM';
            insert oRecTypes;
            system.assert(oRecTypes.CSIRecordTypeName__c == 'Service CRM');
            
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
            system.assertEquals(lstIntent[0].name == 'Filling',true);
            
            CTCI_Junction__c oCTCIRec = new CTCI_Junction__c();
            oCTCIRec.Classification__c = lstClassification[0].id;
            oCTCIRec.Intent__c = lstIntent[0].id;
            oCTCIRec.Classification_Type__c = oClassType.id;
            oCTCIRec.IsActive__c = true;
            insert oCTCIRec;
            system.assertEquals(oCTCIRec.IsActive__c == true, true);
            
            Case oCase = new Case(AccountId=oAccount.Id, CTCI_List__c = oCTCIRec.Id, CoKY_Case__c = True,Interacting_With_Type__c = 'Group',
            Interacting_With__c=oAccount.Id, Due_Date__c=Date.Today(), Case_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU1', Work_Queue_View_Name__c='Cincinati CRU1',
            Service_Center__c ='Cincinnati',Department__c='CRU1', status='In Progress',Tenant_Id__c = '00');
            insert oCase;
            system.assert(oCase.AccountId == oAccount.Id);
           
            CaseComment caseCommentRec = new CaseComment();
            
            caseCommentRec.ParentId = oCase.Id;
            caseCommentRec.CommentBody = 'Inserting Case Comments';
            List<CaseComment> lstCaseComment = new List<CaseComment>{caseCommentRec};
                
            Database.insert(lstCaseComment,false);
            
            Task oTask = HUMTestDataHelperTest.getTaskRecord();
            oTask.WhatId = oCase.Id;
            oTask.Task_Owner__c = 'User1';
            oTask.Owner_Queue__c ='Cincinnati CRU1';            
            oTask.status = 'In Progress';
            oTask.QueueOrUserId__c = oUser.Id;
            insert oTask;
            System.assert(oTask.status == 'In Progress');
            
             Task_Field_History__c oTaskFieldHistory = new Task_Field_History__c 
                                                            (Created_At__c=System.now(),
                                                            SObjFieldName__c = GLOBAL_CONSTANT_HUM.CASEMGMT_DESCRIPTION_HUM,
                                                            SObjFieldValue__c='Inserting Task Comments', 
                                                            SObjID__c= oTask.id);
            insert oTaskFieldHistory;
        }
   
        
    }
    
    /**
    * This is a test method for  Comments on Case
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void testPopUpForcommentsForCaseTest()
    {
        HUMTestDataHelperTest.getHUMConstantsData();
        User oUser = [SELECT Id,lastname FROM User  WHERE Lastname='test User1' LIMIT 1];
        system.assertEquals(oUser.lastname=='test User1',true);
        
        System.runAs(oUser) 
        {
            Case oCase = [Select id from case limit 1];
            PopUpComments_C_HUM oCommentsController = new PopUpComments_C_HUM();
            
            oCommentsController.sObjectId = oCase.Id;
            String sComment = oCommentsController.sComments;
            String sLastModifiedDate = oCommentsController.sLastModifiedDate;
            Boolean bDisp = oCommentsController.bDateDisplay;
           
             System.assertEquals(sComment, 'Inserting Case Comments');         
            System.assertEquals(bDisp, true);
        }
    } 
    
    /**
    * This is a test method for Comments on Task
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void testPopUpForcommentsForTaskTest()
    {
        HUMTestDataHelperTest.getHUMConstantsData();
        User oUser = [SELECT Id,lastname FROM User  WHERE Lastname='test User1' LIMIT 1];
        system.assertEquals(oUser.lastname=='test User1',true);
        
        System.runAs(oUser) 
        {
            Task oTask = [Select id from Task limit 1];
            PopUpComments_C_HUM oCommentsController = new PopUpComments_C_HUM();
            
            oCommentsController.sObjectId = oTask.Id;
            String sComment = oCommentsController.sComments;
            String sLastModifiedDate = oCommentsController.sLastModifiedDate;
            Boolean bDisp = oCommentsController.bDateDisplay;
           
            System.assertEquals(sComment, 'Inserting Task Comments');         
            System.assertEquals(bDisp, true);
        }
    }   
    
      /**
    * This is a test method for Exception
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void testPopUpForcommentsForExceptionTest()
    {
        
        HUMTestDataHelperTest.getHUMConstantsData();
        User oUser = [SELECT Id,lastname FROM User  WHERE Lastname='test User1' LIMIT 1];
        system.assertEquals(oUser.lastname=='test User1',true);
        
        System.runAs(oUser) 
        {
            
            PopUpComments_C_HUM oCommentsController = new PopUpComments_C_HUM();
            
            oCommentsController.sObjectId = 'ABCDEFGH';
           
        }
    }   
    
}