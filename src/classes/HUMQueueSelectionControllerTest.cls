/****************************************************************************
Apex Class Name  : HUMQueueSelectionControllerTest 
Version          : 1.0
Created Date     : JULY 07 2014
Function         : Test class to HUMQueueSelectionController Apex class

Modification Log :
* Developer Name                  Code Review                        Date                 Description
* ------------------------------------------------------------------------------------------------------------------------------
*  Suryakumari Medicherla            17093                        07/07/2014             Original Version
*  Suryakumari Medicherla            18032                        09/18/2014             Added code to cover the logic added for defect:154410 
*  Suryakumari Medicherla            18257                        09/30/2014             Added lines of code to assign values to Interaction About Type when user trying to create a case from account,Defect:156164
*  Suryakumari Medicherla            18795                        10/27/2014             Added line of code to display error message when creating Case Interaction from Interaction detail page
*  Mrudula Jaddu                     19591                        12/01/2014             Code change as per CC#19591
*  SuryaKumari Medicherla            19911                        12/11/2014             Code modified to replace custom settings with custom object (REQ-72809)
*  Asha Deepika Koyyana              20572                        01/28/2015             Fixed test class failure for Defect#164139  
*  Kritika Popat                     28219                        11/20/2015             Worked on changes related to REQ 161520, code coverage, new standards
*  Manish Kumar                      24880                        04/11/2016             Worked on incorporating Logging changes
*  Prachi Bhasin                     33486                        07/18/2016             Changes made as part of REQ- 270349 
*  Pradeepkumar Dani                 99570                        06/08/2017             Intent__c.Classification__c & Intent__c.Etl_Idtfr__c Clean up
*  Pradeepkumar Dani                100996                        06/13/2017             Classification__c.Classification_Type__c & Etl_Idftr__c Clean Up
*  Santhi Mandava                                                 03/12/2019             Removed "DpaaSWebServiceMockImpl" usage.
*  Melkisan Selvaraj											  08/26/2019			 Launch Compass Changes
*  Mohammed Noor                                                  09/01/2021             Changes to increase code coverage
**********************************************************************************************************************************/
@isTest                    
private class HUMQueueSelectionControllerTest 
{
   /**
    * testsetup method to create test data
    *<p>
    * @param            NULL
    * @return           void
    */
     @testSetup static void prepareTestData() 
    {   
        User oCCSUser = HUMTestDataHelperTest.createTestUser('QSelUser1','Customer Care Specialist');
        insert oCCSUser;
        system.assertEquals(oCCSUser.LastName,'test QSelUser1');
        
        User oCCSUser1 = HUMTestDataHelperTest.createTestUser('QSelUser2','Customer Care Specialist');
        oCCSUser1.Current_Queue__c = 'Louisville RSO Calls Support';
        insert oCCSUser1;
        system.assertEquals(oCCSUser1.LastName,'test QSelUser2');

        System.runAs (oCCSUser) 
        { 
            Group oGroup1 = HUMTestDataHelperTest.getQueues('Group one');
            insert oGroup1;
            System.assertEquals(oGroup1.Name,'Group one');
            
            Group oGroup3 = HUMTestDataHelperTest.getQueues('Group Three');
            insert oGroup3;
            System.assertEquals(oGroup3.Name,'Group Three');
            
            Group oGroup2 = HUMTestDataHelperTest.getQueues('Group Two');
            insert oGroup2;
            System.assertEquals(oGroup2.Name,'Group Two');
            
            QueueSObject oQueue2 = HUMTestDataHelperTest.getCaseQueues(oGroup2.Id);
            insert oQueue2;
            System.assertEquals(oQueue2.SobjectType,'Case');
            
            GroupMember GroupMem = HUMTestDataHelperTest.getGrpMems(oGroup1.id, oCCSUser.id);
            insert GroupMem;
            System.assertEquals(GroupMem.GroupId,oGroup1.id);
            
            GroupMember GroupMem2 = HUMTestDataHelperTest.getGrpMems(oGroup2.id, oCCSUser.id);
            insert GroupMem2; 
            System.assertEquals(GroupMem2.GroupId,oGroup2.id);
            
            Group oGrp=new Group(Name='Group_Two', Type='QUEUE');
            insert oGrp;
            System.assertEquals(oGrp.Name,'Group_Two');
            
            Classification_Type__c CTObj = new Classification_Type__c(name='Calls (RSO)',Mentor_Function__c='Contact Center RSO');
            insert CTObj;
            System.assertEquals(CTObj.Name,'Calls (RSO)');
            
            QueuesObject oQueueObject = new QueuesObject (QueueID = oGrp.id, SobjectType ='Work_Queue_Setup__c');
            insert oQueueObject ;   
            System.assertEquals(oQueueObject.SobjectType,'Work_Queue_Setup__c');
            
            Work_Queue_Setup__c oWQS = new Work_Queue_Setup__c(Public_Group_Name__c = 'Group one', Service_Center__c = 'Group',  Department__c = 'Two', Name = 'Group_Two', Classification_Type__c=CTObj.Id);
            oWQS.OwnerId = oGrp.Id;            
            insert oWQS;
            System.assertEquals(oWQS.Public_Group_Name__c,'Group one');
            
            Account oAccount = HUMTestDataHelperTest.getAccount();
            oAccount.Name = 'New Case Account';
            oAccount.Group_Number__c  = '123';
            oAccount.BillingState = 'New York';
            oAccount.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Provider').getRecordTypeId();
            insert oAccount;
            System.assertEquals(oAccount.Group_Number__c,'123');
            
            ConsumerID__c oConsumer = new ConsumerID__c(Account__c = oAccount.id, Consumer_ID__c = '12345');
            insert oConsumer;
            System.assertEquals(oConsumer.Consumer_ID__c,'12345');
            
            HUMCaseNPI_TAXID__c oTaxNPI = new HUMCaseNPI_TAXID__c(Name = 'Provider');
            insert oTaxNPI;
            System.assertEquals(oTaxNPI.Name,'Provider');
            
            HUMCaseInteractionAboutType__c oCaseInterAbotType = new HUMCaseInteractionAboutType__c();
            System.assert(oCaseInterAbotType != null);            
            oCaseInterAbotType.Name = 'Provider';
            oCaseInterAbotType.CaseRecordType__c = 'Provider';
            oCaseInterAbotType.CaseInteraction_About_Type__c = 'Provider';
            insert oCaseInterAbotType;
            System.assertEquals(oCaseInterAbotType.CaseRecordType__c,'Provider');
            
            Product2 oProduct = HUMTestDataHelperTest.getProduct();
            oProduct.Name='Mediclaim';
            insert oProduct;
            system.assertEquals(oProduct.Name,'Mediclaim');
            
            Policy__c oPolicy = HUMTestDataHelperTest.getPolicy();
            oPolicy.Name = 'test123';
            oPolicy.Group_Name__c = oAccount.Id;
            oPolicy.Funding_Type_Code__c = 'Y';
            oPolicy.Product__c = oProduct.Id;
            oPolicy.Issue_State__c = 'IL'; 
            insert oPolicy;
            system.assertEquals(oPolicy.Issue_State__c, 'IL');
            
            Policy_Member__c oPolicyMem = HUMTestDataHelperTest.getPolicyMember();
            oPolicyMem.Name = '12345';
            oPolicyMem.Member__c = oAccount.Id;
            oPolicyMem.Policy__c = oPolicy.Id;            
            oPolicyMem.Relationship__c = 'Self';
            insert oPolicyMem;
            system.assertEquals(oPolicyMem.Name, '12345');
            
            Case oCase = HUMTestDataHelperTest.getCase();
            oCase.AccountId = oAccount.Id;
            oCase.Status = 'In Progress';
            oCase.Case_Owner__c = UserInfo.getUserName();
            oCase.Owner_Queue__c = 'Dental Calls';
            insert oCase;
            System.assertEquals(oCase.Owner_Queue__c, 'Dental Calls');
            
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

            Case oCase1 = new Case(AccountId = oAccount.Id, Intent_Id__c = lstIntent[0].id, Classification_Id__c =lstClassification[0].id, Policy_Member__c= oPolicyMem.Id ,
                             Interacting_With_Type__c = 'Member',Interacting_With__c= oAccount.Id, Due_Date__c=Date.Today(), 
                             Case_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU', Work_Queue_View_Name__c='Cincinati CRU',Service_Center__c ='Cincinnati',Department__c='CRU', status='In Progress');
            oCase1.RecordTypeId = Schema.sObjectType.Case.getRecordTypeInfosByName().get('Provider Case').getRecordTypeId();
            insert oCase1;
            System.assertEquals(oCase1.Interacting_With_Type__c, 'Member');
            
            Task oTask = HUMTestDataHelperTest.getTaskRecord();
            oTask.WhatId = oCase.Id;
            oTask.Status = 'In Progress';
            oTask.Task_Owner__c = UserInfo.getUserName();
            oTask.Owner_Queue__c = UserInfo.getUserName();            
            insert oTask;
            System.assertEquals(oTask.WhatId,oCase.Id);
           
            
            Interaction__c oInteraction = HUMTestDataHelperTest.getInteraction();
            oInteraction.Interacting_With__c = oAccount.id;
            oInteraction.Interacting_With_type__c = 'Provider' ;
            oInteraction.Caller_Name__c = '111';
            insert oInteraction;
            System.assertEquals(oInteraction.Interacting_With_type__c, 'Provider');
        
            Interaction_Member__c oIntMem = HUMTestDataHelperTest.getInteractionMember(); 
            oIntMem.Interaction__c = oInteraction.id;
            oIntMem.Interacting_About__c = oAccount.id;
            oIntMem.Interacting_About_Type__c = 'Provider';
            insert oIntMem; 
            System.assertEquals(oIntMem.Interacting_About__c, oAccount.id);
                         
        }
    }
    
    /**
    * testPopulateSF
    *<p>
    * Used to run positive scenerios of populateSF
    * @param            NULL
    * @return           void
    */ 
    @isTest
    static void testPopulateSF()
    {
        User oCCSUser = [Select id,UserName,LastName from User where LastName = 'test QSelUser1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.LastName,'test QSelUser1');
        
        System.runAs(oCCSUser)
        {
            List<Work_Queue_Setup__c> wqLst = [Select id, Name, Public_Group_Name__c, Classification_Type__r.Mentor_Function__c, Classification_Type__r.Name FROM Work_Queue_Setup__c WHERE Public_Group_Name__c = 'Group one'];
            system.assertEquals(wqLst[0].Public_Group_Name__c,'Group one');
            system.assertEquals(wqLst[0].Classification_Type__r.Mentor_Function__c, 'Contact Center RSO');
            HUMQueueSelectionController oController6 = new HUMQueueSelectionController();
            Test.startTest();
            oController6.sSelectedQueue = wqLst[0].Name;
            oController6.saveSubFunction();
            oController6.saveQueue();
            oController6.populateSF(wqLst[0].Classification_Type__r.Mentor_Function__c);
            Test.stopTest();
        }
    }
    
   /**
    * runPositiveData
    *<p>
    * Used to run positive scenerios of test data
    * @param            NULL
    * @return           void
    */ 
    @isTest
    static void runCaseData()
    {    
        User oCCSUser = [Select id,UserName,LastName from User where LastName = 'test QSelUser1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.LastName,'test QSelUser1');
        
        System.runAs(oCCSUser)
        {
            Test.startTest();
            Group oGrp1 = [Select Id,Name from Group where Name = 'Group one'];
            System.assertEquals(oGrp1.Name,'Group one');
            
            Account oAcc = [Select Id,RecordtypeId,Name from Account where Group_Number__c = '123'];
            System.assertEquals(oAcc.Name,'New Case Account');
            
            Case oCaseRec = [select Id,Case_Number__c,RecordType.Name,Status  from Case where Owner_Queue__c = 'Dental Calls'];
            System.assertEquals(oCaseRec.Status,'In Progress');
            
            Policy__c oPol = [select Id,Name from Policy__c where Funding_Type_Code__c = 'Y'];
            System.assertEquals(oPol.Name,'test123');
            
            String sCaseId = oCaseRec.Id;
            String sCaseNum = oCaseRec.Case_Number__c;
            
            Task oTask = [select Id,Task_Number__c,WhatId from Task where WhatId =: oCaseRec.Id];
            System.assertEquals(oTask.WhatId, oCaseRec.Id);
           
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMObjectType, HUMConstants.OBJECT_CASE);
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMobjectNumber, sCaseNum);
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMObjectId, sCaseId);
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMActionType, HUMConstants.HUMQueueYesForEdit);
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMParentObjectId,oPol.Id);
            HUMQueueSelectionController oController = new HUMQueueSelectionController();
            System.assert(oController != null);            
            oController.sSelectedQueue = oGrp1.Name;
            oController.init();
            oController.saveQueue();
            oController.getQueue();
            System.assert(oController.getQueue() != null); 
            System.assertEquals(oController.getQueue(), 'Group one');
            System.assertEquals(oController.sTabtitle, oCaseRec.Case_Number__c);
            
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMNextPage, 'TaskEditPage');
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMId, oTask.id);
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMObjectType,  HUMConstants.OBJECT_TASK);
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMWhatId, oAcc.id);
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMobjectNumber, oTask.Task_Number__c);
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMTask_Number, oTask.Task_Number__c);
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMActionType, HUMConstants.HUMQueueYesForEdit);
            HUMQueueSelectionController oController1 = new HUMQueueSelectionController();
            oController1.sSelectedQueue = oGrp1.Name;
            oController1.init();
            oController1.saveQueue();
            oController1.getQueue(); 
            System.assertEquals(oController.getQueue(), 'Group one');
            System.assertEquals(oController1.sTabtitle, oTask.Task_Number__c);    
            
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMNextPage, 'TaskEditPage');
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMId, oTask.id);
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMObjectType,  HUMConstants.OBJECT_TASK);
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMWhatId, oAcc.id);
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMobjectNumber, oTask.Task_Number__c);
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMActionType, HUMConstants.HUMQueueYesForEdit);
            HUMQueueSelectionController oController16 = new HUMQueueSelectionController();

            oController16.saveQueue();
            System.assertEquals(oController16.sTabtitle, oTask.Task_Number__c);

            
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMNextPage, 'TaskEditPage');
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMId, oTask.id);
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMObjectType,  HUMConstants.OBJECT_TASK);
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMWhatId, oAcc.id);
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMTask_Number, oTask.Task_Number__c);
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMobjectNumber, oTask.Task_Number__c);
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMActionType, HUMConstants.HUMQueueYesForAttachment); 
            HUMQueueSelectionController oController2 = new HUMQueueSelectionController();
            oController2.sSelectedQueue = oGrp1.Name;

            oController2.init();
            oController2.saveQueue();
            oController2.getQueue();
            System.assertEquals(oController.getQueue(), 'Group one');
            System.assertEquals(oController1.sTabtitle, oTask.Task_Number__c);
            System.assert(oController2.getQueue() != null);
            
            
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMObjectType, HUMConstants.OBJECT_CASE);
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMobjectNumber, sCaseNum);
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMObjectId, sCaseId);
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMActionType, HUMConstants.HUMQueueYesForInteraction);
            HUMQueueSelectionController oController3= new HUMQueueSelectionController();
            System.assert(oController3 != null);  
            oController3.sSelectedQueue = oGrp1.Name;
            oController3.init();
            oController3.saveQueue();
            oController3.getQueue();
            System.assertEquals(oController3.getQueue(), 'Group one');
            
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMObjectType,  HUMConstants.OBJECT_TASK);
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMWhatId, oAcc.id);
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMTask_Number, oTask.Task_Number__c);
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMActionType, '');
            HUMQueueSelectionController oController4 = new HUMQueueSelectionController();
            oController4.sSelectedQueue = oGrp1.Name;
            oController4.init();
            oController4.saveQueue();
            oController4.getQueue();  
            System.assert(oController4.getQueue() != null);
            System.assertEquals(oController4.sTabtitle, 'New Task');
            HUMQueueSelectionController oController5 = new HUMQueueSelectionController();
            oController5.sAssignedQueue = oGrp1.Name;
            oController5.getQueueList();
            System.assertEquals(oController5.getQueueList().size(), 1);
            Test.stopTest();
        }
    }
   
   /**
    * runCaseInsertionData
    *<p>
    * Used to run positive scenerios of test data
    * @param            NULL
    * @return           void
    */ 
    @isTest
    static void runCaseInsertionData()
    {    
        User oCCSUser = [Select id,UserName,LastName from User where LastName = 'test QSelUser1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.LastName,'test QSelUser1');
        String sBaseURL = URL.getSalesforceBaseUrl().toExternalForm();
        
        System.runAs(oCCSUser)
        {
            Test.startTest();
            Group oGrp2 = [Select Id,Name from Group where Name = 'Group one'];
            System.assertEquals(oGrp2.Name,'Group one');
            
            Account oAcc = [Select Id,RecordtypeId,Name from Account where Group_Number__c = '123'];
            System.assertEquals(oAcc.Name,'New Case Account');
            
            Interaction__c oInteraction = [select Id,Interacting_With__c,Interacting_With_type__c from Interaction__c  where Caller_Name__c = '111'];
            System.assertEquals(oInteraction.Interacting_With__c, oAcc.id);
            
            Policy__c oPol = [select Id,Name from Policy__c where Funding_Type_Code__c = 'Y'];
            System.assertEquals(oPol.Name,'test123');
            
            Policy_Member__c oPolicyMem = [select Id,Member__c from Policy_Member__c where Name = '12345'];
            System.assertEquals(oPolicyMem.Member__c, oAcc.Id);
            
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMObjectType,HUMConstants.OBJECT_CASE); 
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMActionType, HUMConstants.HUMQueueYesFromAccountRelatedList); 
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMParentObjectId, oAcc.id);
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMcaseRecordType, oAcc.recordTypeId);
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMCaseStandardURL,  sBaseURL+ '/' +oAcc.id+'&interactionId='+oInteraction.id);
            HUMQueueSelectionController oController = new HUMQueueSelectionController();
            System.assert(oController != null);
            oController.sSelectedQueue = oGrp2.Name;
            oController.init();
            oController.getQueue();
            oController.getQueueList();
            System.assertEquals(oController.getQueue(), 'Group Two1');
            System.assertEquals(oController.objCase.Interacting_About_Type__c, 'Provider');
            
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMObjectType,HUMConstants.OBJECT_CASE); 
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMActionType, HUMConstants.HUMQueueYesFromAccountRelatedList); 
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMParentObjectId, oPol.id);
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMcaseRecordType, oAcc.recordTypeId);
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMCaseStandardURL,  sBaseURL+ '/' +oPol.id+'&interactionId='+oInteraction.id + '&chatId=' + oInteraction.id);
            HUMQueueSelectionController oController2 = new HUMQueueSelectionController();
            System.assert(oController2 != null);
            oController2.sSelectedQueue = oGrp2.Name;
            oController2.init();
            oController2.getQueue();
            oController2.getQueueList();
            System.assertEquals(oController2.getQueue(), 'Group Two1');
            System.assertEquals(oController2.objCase.Interacting_About_Type__c, 'Member');
            System.assertEquals(oController2.objCase.Policy__c, oPol.Id);
            
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMObjectType,HUMConstants.OBJECT_CASE); 
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMActionType, HUMConstants.HUMQueueYesFromCaseHistory); 
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMParentObjectId, oPolicyMem.id); 
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMCaseStandardURL,  sBaseURL+ '/' +oPolicyMem.id+'&interactionId='+oInteraction.id);
            HUMQueueSelectionController oController1 = new HUMQueueSelectionController();
            System.assert(oController1 != null);  
            oController1.sSelectedQueue = oGrp2.Name;
            oController1.init();
            oController1.getQueue();
            oController1.getQueueList();
            System.assert(oController1.getQueue() != null);
            System.assertEquals(oController1.getQueue(), 'Group Two1');
            System.assertEquals(oController1.objCase.Policy_Member__c, oPolicyMem.id);
            System.assertEquals(oController2.objCase.Interacting_About_Type__c, 'Member');
            Test.stopTest();
        }
    }

    /**
    * testErrMessages
    *<p>
    * Used to cover the Error messages
    * @param            NULL
    * @return           void
    */ 
    @isTest
    static void testErrMessages()
    {
        User oCCSUser = [Select id,UserName,LastName,Current_Queue__c from User where LastName = 'test QSelUser2' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.LastName,'test QSelUser2');
        String sBaseURL = URL.getSalesforceBaseUrl().toExternalForm();
        
        
        System.runAs(oCCSUser)
        {
            Account acc = new Account(Name='TstAcct',NPI_Id__c='1234567890');
            Insert acc;
            Test.startTest();
            
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMObjectType,HUMConstants.OBJECT_CASE); 
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMActionType, HUMConstants.HUMQueueYesForEdit);
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMobjectNumber, '');
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMTask_Number, 'T12345');                         
            HUMQueueSelectionController oController = new HUMQueueSelectionController();            
            System.assert(oController != null);
            
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMObjectType,HUMConstants.OBJECT_TASK); 
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMActionType, HUMConstants.HUMQueueYesForInteraction); 
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMObjectId, 'TestID');
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMobjectNumber, '');            
            HUMQueueSelectionController oController1 = new HUMQueueSelectionController();            
            System.assert(oController1 != null);
            oController1.init();

            ApexPages.currentPage().getParameters().put(HUMConstants.HUMObjectType,'TestObject'); 
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMActionType, HUMConstants.HUMQueueYesFromAccountRelatedList); 
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMObjectId, acc.Id);
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMNextPage, 'TestPage');            
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMParentObjectId, acc.id);
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMAccRecordTypeName, 'Provider');
            //ApexPages.currentPage().getParameters().put(HUMConstants.HUMcaseRecordType, 'Provider Case');
            
            HUMQueueSelectionController oController2 = new HUMQueueSelectionController();            
            System.assert(oController2 != null);
            oController2.returnURL(oCCSUser);
            oController2.returnURLForCase();
            Test.stopTest();
        }        
    }    
}