/*****************************************************************************************
Apex class Name    : HUMTaskListViewControllerTest 
Version            : 1.0
Function           : This is test class for HUMTaskListViewController
Created Date       : 4 Aug 2014

    Modification Log
*   Developer                    Code Review         Date               Description
* ------------------------------------------------------------------------------------------------------------------------------                
*   Manish Kumar                 17093               08/04/2014         Original Version
*   Manish Kumar                 17492               08/28/2014         Modified acording to code review : 17521
*   Shruti Poundarik             17804               09/09/2014         Added logic to increase coverage
*   Manish Kumar                 18221               09/29/2014         Added Code review#. Defect#155745
*   Manish Kumar                 18221               09/29/2014         Modified according to code review : 18221
*   Manish Kumar                 18331               10/07/2014         Modified according to code review : 18331
*   Mrudula Jaddu                18811               10/29/2014         Modified according to code review : 18811
*   Manish Kumar                 19218               11/12/2014         Code fix for defect#160822, #160818
*   SuryaKumari Medicherla       19911               12/11/2014         Code modified to replace custom settings with custom object (REQ-72809)
*   Manish Kumar                 20383               13/01/2015         Added logic for REQ#72784
*   Manish Kumar                 21555               13/03/2015         Added logic to fix Defect#172474.0001. Multiple user should not be able to claim same Case/Task.
                                                                        Code added/modified from line#172-176
*   Manish Kumar                 21713               21/03/2015         Added logic to fix test class failures. populating Work_Queue_Name__c field while creating Queue view record.
                                                                        Code added/modified from line#141 & 143
*   Manish Kumar                                     02/05/2015         Code fix as part of Case/Task optimization. Custom sortning removed and OOB sorting functionality implemented.
                                                                        Removed few more local variables.
*   Santhi Mandava                                   04/10/2015         Fixed test class failure issue.
*   Cody Sanders                 29406               02/03/2016         Added testSetup method and Business Logic-specific tests.
*   Harshith Mandya              33402               05/24/2106         Defect Fix # 227277.0001 - removing time specific assert statements
*   Harsha N                     33402               20/07/2016         Made changes for req - 270351 to replace CI fields and code cleanup
*   Pradeepkumar Dani            42551               10/05/2016         REQ-00669, REQ-244446, REQ-244447, REQ-244449 Changes
*   Vamsi Kondragunta            42634               10/07/2016         Changes As part of req :283728
*   Pradeepkumar Dani            99570               06/08/2017         Intent__c.Classification__c & Intent__c.Etl_Idtfr__c Clean up
*   Pradeepkumar Dani           100996               06/13/2017         Classification__c.Classification_Type__c & Etl_Idftr__c Clean Up
*   Pradeepkumar Dani           102268               06/16/2017         Case Clean Up (Classification__c,Intent__c,Classification_Ctci__c,Intent_Ctci__c,Classification_Text__c,Intent_Text__c)
*   Santhi Mandava                                   03/12/2019         Removed "DpaaSWebServiceMockImpl" usage.
*****************************************************************************************************************************/

@isTest
private class HUMTaskListViewControllerTest 
{
    /**
    * Test method used for overall testing for HUMTaskListViewController.
    * <p>
    * @return   :    void
    * @param    :    NA
    */
    @testSetup static void setup()
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        oCCSUser.Current_Queue__C = 'Cincinnati CRU1';
        insert oCCSUser; 
        System.assertEquals(oCCSUser.LastName,'test User1');

        User oCCSuperUser = HUMTestDataHelperTest.createTestUser('User3','Customer Care Supervisor');
        oCCSuperUser.Current_Queue__C = 'Cincinnati CRU1';
        insert oCCSuperUser; 
        System.assertEquals(oCCSuperUser.LastName,'test User3');

        User oAdminUser = HUMTestDataHelperTest.createTestUser('User2','system Administrator');
        oCCSUser.Current_Queue__C = 'Cincinnati CRU1';
        insert oAdminUser;
        System.assertEquals(oAdminUser.LastName,'test User2');

        system.runAs(oCCSuperUser)
        {
            User oCCS = [ SELECT Id, UserName , Name FROM User WHERE LastName = 'test User1' ];
            User oCCSup = [ SELECT Id, UserName ,Name FROM User WHERE LastName = 'test User3'];

            string sNewUserName = oCCS.Name; 
            id idNewUserId = oCCS.Id;

            List<RecordType> lstRecordTypeIds = [SELECT Id  FROM RecordType WHERE Name = 'Member Case' LIMIT 1 ];
            id idMemberCaseRecordType = lstRecordTypeIds[0].Id;

            Test.loadData(HUMConstants__c.sObjectType,'HUMConsTestData');

            Group oGroup = new Group(Type='Queue', Name='Cincinnati CRU1');
            insert oGroup ;
            system.assertEquals(oGroup.Type, 'Queue', 'Value not matched');

            QueueSObject oQueue = new QueueSObject(SobjectType='Case', QueueId=oGroup.Id);
            insert oQueue;
            system.assertEquals(oQueue.SobjectType, 'Case', 'Value not matched');

            Group oGroup1 = new Group(Type='Regular', Name='PG Cincinnati CRU1');
            insert oGroup1;
            system.assertEquals(oGroup1.Type, 'Regular', 'Value not matched');

            GroupMember oGroupMember1 = new GroupMember(GroupId = oGroup1.Id, UserOrGroupId = oCCS.Id);
            insert oGroupMember1;
            system.assertEquals(oGroupMember1.GroupId, oGroup1.Id, 'Value not matched');

            GroupMember oGroupMember2 = new GroupMember(GroupId = oGroup1.Id, UserOrGroupId = oCCSup.Id);
            insert oGroupMember2;
            system.assertEquals(oGroupMember2.GroupId, oGroup1.Id, 'Value not matched');

            GroupMember oGroupMember3 = new GroupMember(GroupId = oGroup.Id, UserOrGroupId = oCCS.Id);
            insert oGroupMember3;
            system.assertEquals(oGroupMember3.GroupId, oGroup.Id, 'Value not matched');

            GroupMember oGroupMember4 = new GroupMember(GroupId = oGroup.Id, UserOrGroupId = oCCSup.Id);
            insert oGroupMember4;
            system.assertEquals(oGroupMember4.GroupId, oGroup.Id, 'Value not matched');

            Account oAccount = new Account(Name='TestAccount1');
            insert oAccount;
            system.assertEquals(oAccount.Name,'TestAccount1','Value not matched');

            Classification__c classificationRec = new Classification__c();
            classificationRec.Name = 'Enrollment/Effective Date Correction';
            classificationRec.Is_Active__c = true;
            insert classificationRec;
            system.assertEquals(classificationRec.Name, 'Enrollment/Effective Date Correction', 'Value not matched');

            Intent__c intentRec = new Intent__c();
            intentRec.Name = 'Rewards';
            intentRec.Is_Active__c = true;
            insert intentRec;
            system.assertEquals(intentRec.Name , 'Rewards','Value not matched');

            List<Classification_Type__c> ctList= new list<Classification_Type__c>();
            Classification_Type__c ctRec = new Classification_Type__c();
            ctRec.name = 'Claims (RSO)';
            ctRec.Is_Active__c=true;
            ctList.add(ctRec);          
            insert ctList;
            system.assertEquals(ctList.size(),1);
            
            
            List<CTCI_Junction__c> ctciList = new list<CTCI_Junction__c>();
            CTCI_Junction__c ctciRec = new CTCI_Junction__c();
            ctciRec.Classification_Type__c = ctRec.id;
            ctciRec.Classification__c = classificationRec.Id;
            ctciRec.Intent__c = intentRec.id;
            ctciRec.IsActive__c = true;
            ctciList.add(ctciRec);
            insert ctciList;
            system.assertEquals(ctciList.size(),1);
            
            List<Case> lstCase = new List<Case>();
            Case oCase1 = new Case(ownerid=oGroup.Id,AccountId=oAccount.Id, Interacting_With__c=oAccount.Id, Due_Date__c=Date.Today(), 
                                   Case_Owner__c = 'Cincinati CRU1', Work_Queue_View_Name__c='Other', Owner_Queue__c='Cincinati CRU1', status='In Progress', 
                                   Origin = 'Inbound Call', Type = 'Customer Inquiry', RecordTypeId = idMemberCaseRecordType, Interacting_With_Type__c = oAccount.Id, 
                                   Interacting_With_Name__c = 'TestAccount1', Interacting_About_Type__c = 'Member', 
                                   Classification_Type__c = 'Calls', Classification_Id__c = classificationRec.Id,
                                   G_A_Rights_Given__c = 'No', Complaint__c = 'Yes', CTCI_List__c = ctciRec.Id,
                                   Complaint_Reason__c = 'Customer Service Quality', Behavioral_Health__c = 'None');
            system.assertEquals(oCase1.Origin, 'Inbound Call', 'Value not matched');

            Case oCase2 = new Case(ownerid=idNewUserId,AccountId=oAccount.Id, Interacting_With__c=oAccount.Id, Due_Date__c=Date.Today(), Case_Owner__c=sNewUserName, Work_Queue_View_Name__c='Other' , Owner_Queue__c=sNewUserName, status='In Progress', 
                                   Origin = 'Inbound Call', Type = 'Customer Inquiry', RecordTypeId = idMemberCaseRecordType, Interacting_With_Type__c = oAccount.Id, Interacting_With_Name__c = 'TestAccount1', Interacting_About_Type__c = 'Member', 
                                   Classification_Type__c = 'Calls', Classification_Id__c = classificationRec.Id, CTCI_List__c = ctciRec.Id, G_A_Rights_Given__c = 'No', Complaint__c = 'Yes', Complaint_Reason__c = 'Customer Service Quality', Behavioral_Health__c = 'None');
            system.assertEquals(oCase1.Origin, 'Inbound Call', 'Value not matched');

            lstCase.add(oCase1);
            lstCase.add(oCase2);
            insert lstCase;
            system.assertEquals(lstCase.size(), 2 , 'Value not matched');

            Task oTask1 = new Task(WhatId=oCase1.Id,Type='Work Task', QueueOrUserId__c=idNewUserId, OwnerId=idNewUserId, ActivityDate=Date.Today(), Work_Queue_View_Name__c='Other' , Task_Owner__c=sNewUserName, Owner_Queue__c='Cincinati CRU1', Status='In Progress', Priority='Low');
            insert oTask1;
            system.assertEquals(oTask1.Type, 'Work Task' , 'Value not matched');

            List<Work_Queue_Setup__c> lstWQS = new List<Work_Queue_Setup__c>();
            Group oGrp=new Group(Name='Cincinnati_CRU1', Type='QUEUE');
            insert oGrp;
            System.assertEquals(oGrp.Name,'Cincinnati_CRU1','Value not matched');

            QueuesObject oQueueObject = new QueuesObject (QueueID = oGrp.id, SobjectType ='Work_Queue_Setup__c');
            insert oQueueObject ;   
            System.assertEquals(oQueueObject.SobjectType,'Work_Queue_Setup__c','Value not matched');

            Work_Queue_Setup__c oWQSelector = new Work_Queue_Setup__c(Name = 'Cincinnati_CRU1');
            oWQSelector.Public_Group_Name__c = 'PG Cincinnati CRU1';
            oWQSelector.Service_Center__c = 'Cincinnati';
            oWQSelector.Department__c = 'CRU1';
            oWQSelector.General__c = true;
            oWQSelector.HO_Medical__c = true;
            oWQSelector.OwnerId = oGrp.id;
            lstWQS.add(oWQSelector);
            insert lstWQS;
            System.assertEquals(oWQSelector.Service_Center__c,'Cincinnati','Value not matched');

            List<Queue_View__c> lstQV = new List<Queue_View__c>();
            Queue_View__c oQueueView1= new Queue_View__c(Queue_View_Name__c='Other', Work_Queue_Name__c=lstWQS[0].Id, Department__c='CRU1', Group_Name__c = 'PG Cincinnati CRU1' ,Queue_Name__c='Cincinnati CRU1', Service_Center__c='Cincinnati');
            System.assertEquals(oQueueView1.Queue_View_Name__c, 'Other', 'Value not matched');
            lstQV.add(oQueueView1);
            Queue_View__c oQueueView2= new Queue_View__c(Work_Queue_Name__c=lstWQS[0].Id, Department__c='CRU1', Group_Name__c = 'PG Cincinnati CRU1' ,Queue_Name__c='Cincinnati CRU1', Service_Center__c='Cincinnati');
            System.assertEquals(oQueueView1.Queue_View_Name__c, 'Other', 'Value not matched');
            lstQV.add(oQueueView2);
            insert lstQV;
            system.assertEquals(lstQV.size(), 2 , 'Value not matched');

        }   
    }

    /**
    * Test method to test specialist use cases for HUMTaskListView.
    * <p>
    * @return   :    void
    * @param    :    NA
    */
    static testMethod void testAllSpecialist()
    {
        User oCCSUser = [ SELECT Id, Name, UserName FROM User WHERE LastName = 'test User1'];
        system.runAs(oCCSUser)
        {
            HUMTaskListViewController oController = new HUMTaskListViewController();
            oController.initializeWorkQueue();
            oController.getCategoriesdd1();
            oController.getCategoriesdd2();

            system.assert(oController.sViewCategorydd1 == 'All');

            List<SelectOption> assignedList = oController.getSelectedAssign();
            system.assert(assignedList.Size() == 3);            
            List<SelectOption> selectedList = oController.getSelectedView();
            system.assert(selectedList.Size() == 3);

            Integer totSize = oController.getTotal_size();
            system.assert(totSize== 0);

            oController.getCaseTask();
            system.assert(oController.lstcaseTaskListHelper.size() > 0);
            Apexpages.CurrentPage().getParameters().put('CaseTaskId', oController.lstcaseTaskListHelper[0].sCasetaskId);
            oController.updateWrapper();
            system.assert(oController.isReadMap.containskey(oController.lstcaseTaskListHelper[0].sCasetaskId));
            oController.getCaseTask();          
            oController.sViewCategorydd1 = 'Cincinnati CRU1';
            oController.getCaseTask();            
            oController.sViewCategorydd2 = 'Other';
            oController.getCaseTask();            
            oController.sViewCategorydd2 = 'Test';
            oController.getCaseTask();            
            List<SelectOption> pullWQoptionslist = oController.getPullWorkQueueOptions();
            system.assert(pullWQoptionslist.Size() != 0);
            List<SelectOption> pullWQVoptionslist = oController.getPullWorkViewOptions();
            system.assert(pullWQVoptionslist.Size() != 0);
            List<SelectOption> pullWorkoptionslist = oController.getPullWorkFilterByOptions();
            system.assert(pullWorkoptionslist.Size() != 0);

            //Error Message
            oController.pullWorkItem();
            system.assert(oController.bPullWorkItem);
            oController.getCaseTask();
            system.assert(oController.bDisableApexMessage); 
            oController.getDefaulterCaseIDs();
            //Else block
            oController.pullWorkItem();
            oController.sSelPullWorkFilterBy = 'Due Date';
            oController.sSelPullWorkView = 'Other';
            oController.sSelPullWorkQueue = 'Cincinnati CRU1';
            oController.getCaseTask();
            system.assert(oController.lstcaseTaskListHelper.isEmpty());

            oController.sSelPullWorkFilterBy = 'Age';
            oController.sSelPullWorkView = 'Other';
            oController.sSelPullWorkQueue = 'Cincinnati CRU1';
            oController.getCaseTask();
            system.assert(oController.lstcaseTaskListHelper.isEmpty());

            oController.sSelPullWorkView = 'All';
            oController.PullWorkAction(); 
            system.assert(!oController.bPullWorkItem);

            oController.populateUnassigndd2Initially();
            system.assert(!String.isBlank(oController.sviewCategorydd1));

            oController.populateUnassigndd2();
            system.assert(oController.sviewCategorydd2 ==  'All');

            oController.getPrevious();
            oController.getNext();
            oController.getDisablePrevious();
            oController.getDisableNext();
            oController.getTotal_size();
            oController.getPageFirstRecord();
            oController.getPageLastRecord();
            Boolean bDisableChangeOwner = oController.getDisableChangeOwner();
            system.assert(bDisableChangeOwner);

            Case objCase = [Select Id FROM Case LIMIT 1];
            oController.oDMLCase = objCase;
            oController.pullWorkDML();
            system.assert(oController.oDMLCase.Id != objCase.Id);

            Task objTask =  [Select Id FROM Task LIMIT 1] ;
            oController.oDMLTask = objTask;
            oController.pullWorkDML();
            system.assert(oController.oDMLTask.Id != objTask.Id);            
        }  
    }

    /**
    * Test method to test Supervisor use cases for HUMTaskListView.
    * <p>
    * @return   :    void
    * @param    :    NA
    */
    static testMethod void testAllSupervisor()
    {
        User oCCSUser = [ SELECT Id, Name, UserName FROM User WHERE LastName = 'test User3'];
        system.runAs(oCCSUser)
        {
            HUMTaskListViewController oController = new HUMTaskListViewController();
            oController.initializeWorkQueue();
            oController.getCategoriesdd1();
            oController.getCategoriesdd2();

            system.assert(oController.sViewCategorydd1 == 'All');

            List<SelectOption> assignedList = oController.getSelectedAssign();
            system.assert(assignedList.Size() == 3);            
            List<SelectOption> selectedList = oController.getSelectedView();
            system.assert(selectedList.Size() == 3);

            Integer totSize = oController.getTotal_size();
            system.assert(totSize== 0);

            oController.getCaseTask();

            Apexpages.CurrentPage().getParameters().put('CaseTaskId', '12211');
            oController.updateWrapper();

            oController.getCaseTask();          
            oController.sViewCategorydd1 = 'Cincinnati CRU1';
            oController.getCaseTask();            
            oController.sViewCategorydd2 = 'Other';
            oController.getCaseTask();            
            oController.sViewCategorydd2 = 'Test';
            oController.getCaseTask();            
            List<SelectOption> pullWQoptionslist = oController.getPullWorkQueueOptions();
            system.assert(pullWQoptionslist.Size() != 0);
            List<SelectOption> pullWQVoptionslist = oController.getPullWorkViewOptions();
            system.assert(pullWQVoptionslist.Size() != 0);
            List<SelectOption> pullWorkoptionslist = oController.getPullWorkFilterByOptions();
            system.assert(pullWorkoptionslist.Size() != 0);

            //Error Message
            oController.pullWorkItem();
            system.assert(oController.bPullWorkItem);
            oController.getCaseTask();
            system.assert(oController.bDisableApexMessage); 
            oController.getDefaulterCaseIDs();
            //Else block
            oController.pullWorkItem();
            oController.sSelPullWorkFilterBy = 'Due Date';
            oController.sSelPullWorkView = 'Other';
            oController.sSelPullWorkQueue = 'Cincinnati CRU1';
            oController.getCaseTask();
            system.assert(oController.lstcaseTaskListHelper.isEmpty());

            oController.sSelPullWorkFilterBy = 'Age';
            oController.sSelPullWorkView = 'Other';
            oController.sSelPullWorkQueue = 'Cincinnati CRU1';
            oController.getCaseTask();
            system.assert(oController.lstcaseTaskListHelper.isEmpty());

            oController.sSelPullWorkView = 'All';
            oController.PullWorkAction(); 
            system.assert(!oController.bPullWorkItem);

            oController.populateUnassigndd2Initially();
            system.assert(!String.isBlank(oController.sviewCategorydd1));

            oController.populateUnassigndd2();
            system.assert(oController.sviewCategorydd2 ==  'All');

            oController.getPrevious();
            oController.getNext();
            oController.getDisablePrevious();
            oController.getDisableNext();
            oController.getTotal_size();
            oController.getPageFirstRecord();
            oController.getPageLastRecord();
            Boolean bDisableChangeOwner = oController.getDisableChangeOwner();
            system.assert(bDisableChangeOwner);


            oController.lstcaseTaskListHelper = Null;
            oController.getCaseTask();
            oController.mapWorkQueueSetup= Null;
            oController.getCategoriesdd1();
            oController.getCategoriesdd2();
            oController.getPagination(null);     
        }  
    }

    /**
    * Test method to test exception use cases for HUMTaskListView.
    * <p>
    * @return   :    void
    * @param    :    NA
    */
    static testMethod void testException()
    {
        User oCCSUser = [ SELECT Id, Name, UserName FROM User WHERE LastName = 'test User3'];
        system.runAs(oCCSUser)
        {
            HUMTaskListViewController oController = new HUMTaskListViewController();
            oController.initializeWorkQueue();
            oController.getCategoriesdd1();
            oController.getCategoriesdd2();

            system.assert(oController.sViewCategorydd1 == 'All');

            List<SelectOption> assignedList = oController.getSelectedAssign();
            system.assert(assignedList.Size() == 3);            
            List<SelectOption> selectedList = oController.getSelectedView();
            system.assert(selectedList.Size() == 3);

            Integer totSize = oController.getTotal_size();
            system.assert(totSize== 0);

            oController.getCaseTask();

            oController.lstcaseTaskListHelper = Null;
            oController.getCaseTask();
            oController.mapWorkQueueSetup= Null;
            oController.getCategoriesdd1();
            oController.getCategoriesdd2();
            oController.getPagination(null);

            oController.sSelPullWorkFilterBy=GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYONLY_HUM;
            oController.pullPriorityCreateAndExecute(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYONLY_HUM);

            oController.sSelPullWorkFilterBy=GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYTSKONLY_HUM;
            oController.pullPriorityCreateAndExecute(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYTSKONLY_HUM);

            oController.sSelPullWorkFilterBy=GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYCASEONLY_HUM;
            oController.pullPriorityCreateAndExecute(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYCASEONLY_HUM);

            oController.sSelPullWorkFilterBy=GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYHIGHANDCRTCLONLY_HUM;
            oController.pullPriorityCreateAndExecute(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYHIGHANDCRTCLONLY_HUM);

            oController.sSelPullWorkFilterBy=GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYNORMALONLY_HUM;
            oController.pullPriorityCreateAndExecute(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYNORMALONLY_HUM);  


        }
    }
}