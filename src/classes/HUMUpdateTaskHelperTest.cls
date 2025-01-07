/****************************************************************************
Apex Class Name  : HUMUpdateTaskHelperTest 
Version          : 1.0 
Created On       : 7/14/2014
Function         : Test class to HUMUpdateTaskHelper apex class

Modification Log :
* Developer Name          Code Review                   Date                       Description
*-----------------------------------------------------------------------------------------------------------------------
* Mrudula Jaddu             17093                       7/14/14                    Original Version
* Mrudula Jaddu             17878                       9/12/2014                  Modified fields as part of defect 154260  
* Manish Kumar              18365                       10/07/2014                 Code fix according to defefct#157139
* Manish Kumar              18386                       10/08/2014                 Code fix according to defefct#157139
* Mrudula Jaddu             18887                       10/31/2014                 Code fix according to cc#18887
* Mrudula Jaddu             20092                       12/29/2014                 Modified code for REQ-95896
* Santhi Mandava            20517                       01/24/2015                 Fixed Defect #164139.
* Santhi Mandava            21968                       02/24/2015                 As per req-02635, we have created MD relationship between work queue 
                                                                                   set up and queue view,Work queue priority objects so modified code to include new fields.
* Santhi Mandava            22274                       04/10/2015                 Code Modified to fix test class failure
* SuryaKumari Medicherla    22312                       04/13/2015                 Code Modified to fix test class failure
* Avinash Choubey           33332                       07/20/2016                 Chnages made as part of REQ- 270351
* Sharan Shanmugam          50226                       11/30/2106                 Implemented REQ - 290990 -  removed all lookup related fields from 
                                                                                   Case/Task/Interaction/Attachment to Work Queue Setup and Queue View
* Harshith Mandya                                       29/12/2016                 REQ - 296640
* Gargey Sharma             77750                       03/21/2017                 REQ - 304949 : Update Task to save new CTCI Classification and Intent Values 
* Santhi Mandava                                        03/12/2019                 Removed "DpaaSWebServiceMockImpl" usage.
* Mohammed Noor										    05/03/2021				   Increase code coverage.
* Nikhil Malhotra								        09/15/2022				   REQ - 3605480 T1PRJ0342659 / SF / MF1 PCO - Create Last Touchpoint and  Appointment Date Fields on the Lead Record  
* Ajay Chakradhar								        11/02/2022				   Test Class Failure Fix  
* Nikhil Malhotra 										11/11/2022				   DF - 6604 Fixing Log touchpoint Date logic
* Lakshmi Madduri										02/23/2024				   US-5604142 PCO App Deprecation
*************************************************************************************************************************/

@isTest
private class HUMUpdateTaskHelperTest
{    
   
    /**
    * Returns void
    * <p>
    * Method to setup Testdata 
    *
    * @return  Void
    * @see
    */
   
    @testSetup static void setUpTestData()
    {
        List<User> userList = new List<User>();
        User oUser = HUMTestDataHelperTest.createTestUser('UpdTaskUser1','Customer Care Specialist');
        oUser.Current_Queue__c = 'Cincinnati CRU';
        userList.add(oUser);
		
        insert userList;
        system.assertEquals(userList[0].lastname =='test UpdTaskUser1',true);
		insert New HUMTriggerSwitch__c(name = 'Task', Exeute_Trigger__c = true);
        insert New HUMTaskNotAllowedProfiles__c(name = 'CRMRetail User');
        system.runAs(userList[0])
        {
            Work_Queue_Priority_CriteriaLimit_Value__C cv = new Work_Queue_Priority_CriteriaLimit_Value__C();
            cv.name = 'Criteria';
            cv.Value__C = 0;
            insert cv;
            
            Account oAcc = HUMTestDataHelperTest.getAccount(); 
            oAcc.Name='TestAccount1';
            oAcc.Source_Platform_Code__c = 'CA';
            oAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
            insert oAcc;            
           System.assertEquals(oAcc.Source_Platform_Code__c == 'CA', true);
            
            Product2 oProd = HUMTestDataHelperTest.getProduct();
            oProd.Name ='Mediclaim';
            oProd.Product_Type__c = 'Mediclaim';
            insert oProd;            
            system.assertEquals(oProd.name =='Mediclaim',true);
            
            Policy__c oPolicy1 = HUMTestDataHelperTest.getPolicy();
            oPolicy1.Name='test123';
            oPolicy1.Group_Name__c=oAcc.Id;
            oPolicy1.Funding_Type_Code__c='Y';
            oPolicy1.Product__c=oProd.Id;
            oPolicy1.Issue_State__c='IL';  
            insert oPolicy1;            
             system.assertEquals(oPolicy1.Funding_Type_Code__c == 'Y',true);
            
            Policy_Member__c oPolMem1 = HUMTestDataHelperTest.getPolicyMember();
            oPolMem1.Name = '12345';
            oPolMem1.Member__c = oAcc.Id;
            oPolMem1.Policy__c = oPolicy1.Id;            
            oPolMem1.Relationship__c = 'Self';            
            insert oPolMem1;            
             system.assertEquals(oPolMem1.Name == '12345' ,true);
             
             HUMCaseUpdateAllowedProfies__c caseUpdate = new HUMCaseUpdateAllowedProfies__c(Name = 'Customer Care Specialist');
             insert caseUpdate;
            
            dataForQueuesAndViews(); 
            
            HUMQueueViewBatchSettings__c obj = new HUMQueueViewBatchSettings__c(Name='QueueViewBatchSetup',TriggerExecutionRequired__c=true);
            insert obj ;
            Test.loadData(HUMConstants__c.sObjectType,'HUMConsTestData');
            
            Classification__c classificationRecDec = new Classification__c();
            classificationRecDec.Name = 'Claims';
            classificationRecDec.Is_Active__c = true;
            insert classificationRecDec;
            system.assertEquals(classificationRecDec.Name == 'Claims',true);
            
            Intent__c intentRec = new Intent__c();
            intentRec.Name = 'Filling';
            intentRec.Is_Active__c = true;
            insert intentRec;
            system.assertEquals(intentRec.name == 'Filling',true);
            
            Classification_Type__c oClassType = new Classification_Type__c();
            oClassType.Guidance_Alert_Limit__c = 2;
            oClassType.Is_Active__c = true;
            insert oClassType;
            
            CTCI_Junction__c oCTCIRec = new CTCI_Junction__c();
            oCTCIRec.Classification__c = classificationRecDec.id;
            oCTCIRec.Intent__c = intentRec.id;
            oCTCIRec.Classification_Type__c = oClassType.id;
            oCTCIRec.IsActive__c = true;
            insert oCTCIRec;
            system.assertEquals(oCTCIRec.IsActive__c == true, true);
            
            List<Case> testcases = new List<Case>();
            Case oCase1 = new Case(AccountId  = oAcc.Id, CTCI_List__c = oCTCIRec.Id, Policy_Member__c = oPolMem1.Id ,
                               Interacting_With_Type__c = 'Group', Interacting_With__c = oAcc.Id, Due_Date__c = Date.Today(),
                               Case_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU', Work_Queue_View_Name__c='Cincinati CRU',
                               Service_Center__c = 'Cincinnati', Department__c = 'CRU', status = 'In Progress', Topic__c = 'SIU');
            testcases.add(oCase1);
            Case oCase2 = new Case(AccountId = oAcc.Id, CTCI_List__c = oCTCIRec.Id, Policy_Member__c= oPolMem1.Id,
                               Interacting_With_Type__c = 'Group',Interacting_With__c = oAcc.Id, Due_Date__c = Date.Today(),
                               Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c = 'Cincinnati CRU', Work_Queue_View_Name__c = 'Cincinati CRU',
                               Service_Center__c = 'Cincinnati', Department__c = 'CRU', status ='In Progress', Topic__c = 'SIU'); 
            
            testcases.add(oCase2);           
            insert testcases;                        
            system.assertEquals(testcases[0].status == 'In Progress',true);
            
            List<Task> testTasks = new List<Task>();    
            Task oTask1 = new Task(WhatId = oCase1.id,type='Work Task', Task_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU',
                               Work_Queue_View_Name__c='Cincinati CRU', Service_Center__c ='Cincinnati', Department__c='CRU', 
                               status='In Progress' , Topic__c = 'SIU');
            testTasks.add(oTask1);  
            Task oTask2 = new Task(WhatId = oCase2.id,type='Work Task',  Task_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU', 
                               Work_Queue_View_Name__c='Cincinati CRU', Service_Center__c ='Cincinnati', Department__c='CRU', 
                               status='In Progress', Topic__c = 'SIU');
            testTasks.add(oTask2);  
            insert testTasks;            
            system.assertEquals(testTasks[0].status == 'In Progress',true);  

            Task oTask3 = new Task(WhatId = oCase2.id,type='Work Task',  Task_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU', 
                               Work_Queue_View_Name__c='', Service_Center__c ='Cincinnati', Department__c='CRU', 
                               status='In Progress', Topic__c = 'Other');      
            insert oTask3;
            System.assertEquals(oTask3.status, 'In Progress'); 
         }      
    }   
    /**
    * Returns void
    * <p>
    * This is the main method which is used for queue view assignment 
    *
    * @return  Void
    * @see
    */    
    private static testMethod void OverAllMainTestMethod()
    {
        List<Task> lstOfTasksToUpdate = new List<Task>();
        User oUser = [select id, name,lastname from user where lastname='test UpdTaskUser1' limit 1];
        system.assertEquals(oUser.lastname =='test UpdTaskUser1',true); 
        system.runAs(oUser)
        {
            Test.startTest(); 
            HUMUpdateTaskHelper objHandler = new HUMUpdateTaskHelper();
            List<Task> lstOfTasks = [select id,ActivityDate,OwnerId, WhatId ,type, Task_Owner__c, Owner_Queue__c, Work_Queue_View_Name__c, Service_Center__c, Department__c, status, Topic__c from Task];
            map<Id, Case> mapCases = new map<Id, Case>([Select Id, Casenumber FROM Case]);
            list<Id> lstCaseIds = new list<Id>();
            lstCaseIds.addAll(mapCases.keySet());
            system.assertEquals(lstOfTasks[0].status == 'In Progress',true); 
            map<Id, Task> mapTasks = new map<Id, Task>();
            mapTasks.putAll(lstOfTasks);
            list<Id> lstTaskIds = new list<Id>();
            lstTaskIds.addAll(mapTasks.keySet());
            map<Id,string> mapGroups=new map<Id,string>();
            mapGroups.put(lstOfTasks[0].id,'12345');
            mapGroups.put(lstOfTasks[1].id,'12345');
            objHandler.PopulateViewName(lstOfTasks,true,null); 
            lstOfTasks[0].status = 'Pending';
            lstOfTasks[1].status = 'Pending';
            lstOfTasksToUpdate.add(lstOfTasks[0]);
            lstOfTasksToUpdate.add(lstOfTasks[1]);
            system.assertEquals(lstOfTasksToUpdate[0].status == 'Pending',true); 
            update(lstOfTasksToUpdate);
            objHandler.taskDueDateError(lstOfTasks);
            objHandler.updateActivityField(lstOfTasks);
            objHandler.populateSetOfOwnerIds(lstOfTasks);
            objHandler.populateListOfCaseIds(lstOfTasks);
            objHandler.populateListOfTasks(lstOfTasks);
            objHandler.CountofOpenTotalTaskForParent(lstOfTasks, mapTasks);
            objHandler.taskQueues(lstOfTasks, lstCaseIds);
            objHandler.populateData(lstOfTasks, lstCaseIds);
            objHandler.updateTaskQueues(lstOfTasks, mapTasks, lstCaseIds);
            Task oTask = [Select Id ,status,Service_Center__c,Department__c,Topic__c,Owner_Queue__c from Task where Owner_Queue__c='Cincinnati CRU'And Topic__c='Other' limit 1];
            oTask.Topic__c = 'Test245';
            
            objHandler.populateViewName(new List<Task>{oTask},false,new SET<String>{'Cincinnati CRU'});
            
            oTask = [Select Id,status,Service_Center__c,Department__c,Topic__c,Owner_Queue__c,Work_Queue_View_Name__c from Task where  Owner_Queue__c='Cincinnati CRU'And Topic__c='Test245' limit 1];
            System.assertEquals(oTask.Work_Queue_View_Name__c,'Test');
            oTask.Topic__c = 'Humana';
            
            objHandler.populateViewName(new List<Task>{oTask},false,new SET<String>{'Cincinnati CRU'});
            
            oTask = [Select Id,status,Service_Center__c,Department__c,Topic__c,Owner_Queue__c,Work_Queue_View_Name__c from Task where  Owner_Queue__c='Cincinnati CRU'And Topic__c='Humana' limit 1];
            System.assertEquals(oTask.Work_Queue_View_Name__c,'Other');
            
            Test.stopTest();
        }
    }  
    
    /**
    * This method Is used to create queue And queue Views for Cases view assignment
    *
    * @param     NULL
    * @return    void 
    */    
    Private Static void dataForQueuesAndViews()
    {
        Work_Queue_Setup__c oQSetup = New Work_Queue_Setup__c(name ='Cincinnati CRU',Service_Center__c='Cincinnati',Department__c='CRU', isActive__c = True);
        insert oQSetup ;
        system.assertEquals(oQSetup.Name,'Cincinnati CRU');
        
        Queue_View__c oQv1 = New Queue_View__c(Queue_View_Name__c ='Other11', Department__c='CRU', Work_Queue_Name__c=oQSetup.Id , Service_Center__c='Cincinnati', isActive__c = True); 
        insert oQv1;
        system.assertEquals(oQv1.Queue_View_Name__c,'Other11');
        
        Queue_View__c oQv2 = New Queue_View__c(Queue_View_Name__c ='Other111', Department__c='CRU', Work_Queue_Name__c=oQSetup.Id, Service_Center__c='Cincinnati', isActive__c = True); 
        insert oQv2;
        system.assertEquals(oQv2.Queue_View_Name__c,'Other111');
        
        Queue_View__c oQv3 = New Queue_View__c(Queue_View_Name__c ='Other123', Department__c='CRU', Work_Queue_Name__c=oQSetup.Id, Service_Center__c='Cincinnati', isActive__c = True); 
        insert oQv3;
        system.assertEquals(oQv3.Queue_View_Name__c,'Other123');
        
        Queue_View__c oQv4 = New Queue_View__c(Queue_View_Name__c ='Other1234', Department__c='CRU', Work_Queue_Name__c=oQSetup.Id, Service_Center__c='Cincinnati', isActive__c = True); 
        insert oQv4;
        system.assertEquals(oQv4.Queue_View_Name__c,'Other1234');
        
        Queue_View__c oQv5 = new Queue_View__c(Queue_View_Name__c='Test',Name='Test',Service_Center__c='Cincinnati',Department__c='CRU',Work_Queue_Name__c=oQSetup.Id,isActive__c = True);
        insert oQV5;
        system.assertEquals(oQv5.Queue_View_Name__c,'Test');
        
        Queue_View_Item__c oQueueViewRC2 = new Queue_View_Item__c(API_Name__c = 'PlanIssueState' , Field_Operator__c = 'Equals' ,Field_Value__c = 'NA' , Queue_View__c = oQv4.Id);
        insert oQueueViewRC2;            
        system.assertEquals(oQueueViewRC2.API_Name__c , 'PlanIssueState');
        
        Queue_View_Item__c oQueueViewRC3 = new Queue_View_Item__c(API_Name__c = 'Topic' , Field_Operator__c = 'Equals' ,Field_Value__c = 'SIU' , Queue_View__c = oQv2.Id);
        insert oQueueViewRC3;            
        system.assertEquals(oQueueViewRC3.API_Name__c , 'Topic');       
        
        Queue_View_Item__c oQueueViewRC4 = new Queue_View_Item__c(API_Name__c = 'FundingType' , Field_Operator__c = 'Equals' ,Field_Value__c = 'N' , Queue_View__c = oQv3.Id);
        insert oQueueViewRC4;            
        system.assertEquals(oQueueViewRC4.API_Name__c , 'FundingType');   
        
        Queue_View_Item__c oQueueView = new Queue_View_Item__c(API_Name__c = 'Status' , Field_Operator__c = 'Equals' ,Field_Value__c = 'In Progress' , Queue_View__c = oQv1.Id);
        insert oQueueView;            
        system.assertEquals(oQueueView.API_Name__c , 'Status'); 
        
        Queue_View_Item__c oQueueView7 = new Queue_View_Item__c(API_Name__c = 'PlanIssueState' , Field_Operator__c = 'Equals' ,Field_Value__c = 'IL, TX' , Queue_View__c = oQv1.Id);
        insert oQueueView7;           
        system.assertEquals(oQueueView7.API_Name__c , 'PlanIssueState'); 
        
        Queue_View_Item__c oQueueView2 = new Queue_View_Item__c(API_Name__c = 'ProductType' , Field_Operator__c = 'Equals' ,Field_Value__c = 'Mediclaim' , Queue_View__c = oQv1.Id);
        insert oQueueView2;            
        system.assertEquals(oQueueView2.API_Name__c , 'ProductType'); 
        dataForQueuesViewsMore(oQv1, oQv3);   
        dataForQueuesPriority(oQSetup);      
        
        List<Queue_View_Item__c> lstQueueViewItems = new List<Queue_View_Item__c>();
        for(Integer i = 1; i < 250 ;i++)
        {
            Queue_View_Item__c qvi = new Queue_View_Item__c();
            qvi.API_Name__c = 'Topic';
            qvi.Field_Operator__c = 'Equals';
            qvi.Field_Value__c = 'Test'+i;
            qvi.Queue_View__c = oQv5.Id;
            lstQueueViewItems.add(qvi);
        }
        insert lstQueueViewItems;
        
        HUMQueueViewCriteria__c QV1 = new HUMQueueViewCriteria__c(Name = 'Behavioral Health',API_Name__c = 'Behavioral_Health__c');
        HUMQueueViewCriteria__c QV2 = new HUMQueueViewCriteria__c(Name = 'BillingStateCode',API_Name__c = 'Group_Billing_State_Code__c');
        HUMQueueViewCriteria__c QV3 = new HUMQueueViewCriteria__c(Name = 'CaseStatus',API_Name__c = 'Status');
        HUMQueueViewCriteria__c QV4 = new HUMQueueViewCriteria__c(Name = 'Classification',API_Name__c = 'Classification__c'); 
        HUMQueueViewCriteria__c QV5 = new HUMQueueViewCriteria__c(Name = 'EmployerGroupName',API_Name__c = 'Employer_Group_Name__c');
        HUMQueueViewCriteria__c QV6 = new HUMQueueViewCriteria__c(Name = 'FundingType',API_Name__c = 'Funding_Type__c');
        HUMQueueViewCriteria__c QV7 = new HUMQueueViewCriteria__c(Name = 'Intent',API_Name__c = 'Intent__c');
        HUMQueueViewCriteria__c QV8 = new HUMQueueViewCriteria__c(Name = 'InteractingWithType',API_Name__c = 'Interacting_With_Type__c');
        HUMQueueViewCriteria__c QV9 = new HUMQueueViewCriteria__c(Name = 'LastModifiedbyQueue',API_Name__c = 'LastModifiedby_Queue__c');
        HUMQueueViewCriteria__c QV10 = new HUMQueueViewCriteria__c(Name = 'OwnerQueue',API_Name__c = '  Owner_Queue__c');
        HUMQueueViewCriteria__c QV11 = new HUMQueueViewCriteria__c(Name = 'PlanIssueState',API_Name__c = 'Plan_Issue_State__c');
        HUMQueueViewCriteria__c QV12 = new HUMQueueViewCriteria__c(Name = 'PlanSegmentSize',API_Name__c = 'Plan_Segment_Size__c');
        HUMQueueViewCriteria__c QV13 = new HUMQueueViewCriteria__c(Name = 'PolicyPlatform',API_Name__c = 'Policy_Platform__c');
        HUMQueueViewCriteria__c QV14 = new HUMQueueViewCriteria__c(Name = 'PreviousCaseOwnerRole',API_Name__c = 'Previous_Case_Owner_Role__c'); 
        HUMQueueViewCriteria__c QV15 = new HUMQueueViewCriteria__c(Name = 'PreviousOwnerQueue',API_Name__c = 'Previous_Owner_Queue__c');
        HUMQueueViewCriteria__c QV16 = new HUMQueueViewCriteria__c(Name = 'Product Code',API_Name__c = 'Policy_Product_Code__c');
        HUMQueueViewCriteria__c QV17 = new HUMQueueViewCriteria__c(Name = 'ProductType',API_Name__c = 'Product_Type__c');
        HUMQueueViewCriteria__c QV18 = new HUMQueueViewCriteria__c(Name = 'ShippingStateCode',API_Name__c = 'Provider_Shipping_State_Code__c');
        HUMQueueViewCriteria__c QV19 = new HUMQueueViewCriteria__c(Name = 'StateMemResidAdd',API_Name__c = 'State_Mem_Resid_Add__c');
        HUMQueueViewCriteria__c QV20 = new HUMQueueViewCriteria__c(Name = 'Subtype',API_Name__c = ' Subtype__c');
        HUMQueueViewCriteria__c QV21 = new HUMQueueViewCriteria__c(Name = 'TaskStatus',API_Name__c = 'Status');
        HUMQueueViewCriteria__c QV22 = new HUMQueueViewCriteria__c(Name = 'Topic',API_Name__c = 'Topic__c');
        HUMQueueViewCriteria__c QV23 = new HUMQueueViewCriteria__c(Name = 'Type',API_Name__c = 'Type__c');
        insert new List<HUMQueueViewCriteria__c>{QV1,QV2,QV3,QV4,QV5,QV6,QV7,QV8,QV9,QV10,QV11,QV12,QV13,QV14,QV15,QV16,QV17,QV18,QV19,QV20,QV21,QV22,QV23};
    }
    
    /*
     * Method used to create Queue Views for Cases view assignment 
     * Return type : void
     * @paramater   : Queue_View__c, Queue_View__c
     */    
     private static void dataForQueuesViewsMore(Queue_View__c oQv1, Queue_View__c oQv3)
    {
        Queue_View_Item__c oQueueView3 = new Queue_View_Item__c();
        oQueueView3.API_Name__c = 'FundingType';
        oQueueView3.Field_Operator__c = 'Equals';
        oQueueView3.Field_Value__c = 'Y';
        oQueueView3.Queue_View__c = oQv1.Id;            
        insert oQueueView3;            
        system.assertEquals(oQueueView3.API_Name__c,'FundingType');            
        
        Queue_View_Item__c oQueueView4 = new Queue_View_Item__c();
        oQueueView4.API_Name__c = 'Classification';
        oQueueView4.Field_Operator__c = 'Equals';
        oQueueView4.Field_Value__c = 'Claims';
        oQueueView4.Queue_View__c = oQv1.Id;            
        insert oQueueView4;            
        system.assertEquals(oQueueView4.API_Name__c,'Classification'); 
        
        Queue_View_Item__c oQueueView5 = new Queue_View_Item__c();
        oQueueView5.API_Name__c = 'Intent';
        oQueueView5.Field_Operator__c = 'Equals';
        oQueueView5.Field_Value__c = 'Filling';
        oQueueView5.Queue_View__c = oQv1.Id;            
        insert oQueueView5;            
        system.assertEquals(oQueueView5.API_Name__c,'Intent'); 
        
        Queue_View_Item__c oQueueView6 = new Queue_View_Item__c();
        oQueueView6.API_Name__c = 'OwnerQueue';
        oQueueView6.Field_Operator__c = 'Equals';
        oQueueView6.Field_Value__c = 'Cincinatti CRU';
        oQueueView6.Queue_View__c = oQv1.Id;            
        insert oQueueView6;            
        system.assertEquals(oQueueView6.API_Name__c,'OwnerQueue'); 
         
        Queue_View_Item__c oQueueView8 = new Queue_View_Item__c();
        oQueueView8.API_Name__c = 'InteractingWithType';
        oQueueView8.Field_Operator__c = 'Equals';
        oQueueView8.Field_Value__c = 'Member';
        oQueueView8.Queue_View__c = oQv1.Id;            
        insert oQueueView8;            
        system.assertEquals(oQueueView8.API_Name__c,'InteractingWithType'); 
        
        Queue_View_Item__c oQueueView9 = new Queue_View_Item__c();
        oQueueView9.API_Name__c = 'PolicyPlatform';
        oQueueView9.Field_Operator__c = 'Equals';
        oQueueView9.Field_Value__c = 'CA';
        oQueueView9.Queue_View__c = oQv1.Id;            
        insert oQueueView9;            
        system.assertEquals(oQueueView9.API_Name__c,'PolicyPlatform');
        
        Queue_View_Item__c oQueueView10 = new Queue_View_Item__c();
        oQueueView10.API_Name__c = 'StateMemResidAdd';
        oQueueView10.Field_Operator__c = 'Equals';
        oQueueView10.Field_Value__c = 'US';
        oQueueView10.Queue_View__c = oQv3.Id;            
        insert oQueueView10;            
        system.assertEquals(oQueueView10.API_Name__c,'StateMemResidAdd');
        
        Queue_View_Item__c oQueueView11 = new Queue_View_Item__c();
        oQueueView11.API_Name__c = 'PlanSegmentSize';
        oQueueView11.Field_Operator__c = 'Equals';
        oQueueView11.Field_Value__c = 'Do';
        oQueueView11.Queue_View__c = oQv3.Id;            
        insert oQueueView11;             
        system.assertEquals(oQueueView11.API_Name__c,'PlanSegmentSize'); 
        
        Queue_View_Item__c oQueueView12 = new Queue_View_Item__c();
        oQueueView12.API_Name__c = 'PreviousCaseOwnerRole';
        oQueueView12.Field_Operator__c = 'Equals';
        oQueueView12.Field_Value__c = 'XXX';
        oQueueView12.Queue_View__c = oQv3.Id;            
        insert oQueueView12;            
        system.assertEquals(oQueueView12.API_Name__c,'PreviousCaseOwnerRole');               
        
        Queue_View_Item__c oQueueView13 = new Queue_View_Item__c();
        oQueueView13.API_Name__c = 'EmployerGroupName';
        oQueueView13.Field_Operator__c = 'Equals';
        oQueueView13.Field_Value__c = 'ABC';
        oQueueView13.Queue_View__c = oQv3.Id;            
        insert oQueueView13;            
        system.assertEquals(oQueueView13.API_Name__c,'EmployerGroupName');   
        
        Queue_View_Item__c oQueueView14 = new Queue_View_Item__c();
        oQueueView14.API_Name__c = 'Topic';
        oQueueView14.Field_Operator__c = 'Equals';
        oQueueView14.Field_Value__c = 'Potential Plan Load';
        oQueueView14.Queue_View__c = oQv3.Id;            
        insert oQueueView14;             
        system.assertEquals(oQueueView14.API_Name__c,'Topic'); 
        
        Queue_View_Item__c oQueueView15 = new Queue_View_Item__c();
        oQueueView15.API_Name__c = 'Type';
        oQueueView15.Field_Operator__c = 'Equals';
        oQueueView15.Field_Value__c = 'type';
        oQueueView15.Queue_View__c = oQv3.Id;            
        insert oQueueView15;             
        system.assertEquals(oQueueView15.API_Name__c,'Type');            
         
        Queue_View_Item__c oQueueView16 = new Queue_View_Item__c();
        oQueueView16.API_Name__c = 'Subtype';
        oQueueView16.Field_Operator__c = 'Equals';
        oQueueView16.Field_Value__c = 'types1';
        oQueueView16.Queue_View__c = oQv3.Id;            
        insert oQueueView16;             
        system.assertEquals(oQueueView16.API_Name__c,'Subtype');              
         
        Queue_View_Item__c oQueueView17 = new Queue_View_Item__c();
        oQueueView17.API_Name__c = 'oQueueView17';
        oQueueView17.Field_Operator__c = 'Equals';
        oQueueView17.Field_Value__c = 'Cincinatti Calls';
        oQueueView17.Queue_View__c = oQv3.Id;            
        insert oQueueView17;             
        system.assertEquals(oQueueView17.API_Name__c,'oQueueView17'); 
    }
    
     /*
     * This method is used to set the Priority for Queue 
     * Return type : void
     * @paramater   : Work_Queue_Setup__c
     */    
    private static void dataForQueuesPriority(Work_Queue_Setup__c oQSetup)
    {
        Work_Queue_Priority__c oWQP = new Work_Queue_Priority__c (Work_Queue_Name__c = oQSetup.Id);
        // modified field from product to product type as part of defect 154260                   
        oWQP.Criteria_1__c = 'ProductType';
        oWQP.Criteria_2__c = 'FundingType';
        oWQP.Criteria_3__c = 'PlanSegmentSize';
        oWQP.Criteria_4__c = 'PlanIssueState';
        oWQP.Criteria_5__c = 'StateMemResidAdd';
        oWQP.Criteria_6__c = 'EmployerGroupName'; 
        oWQP.Criteria_7__c = 'PolicyPlatform';
        oWQP.Criteria_8__c = 'Subtype';
        oWQP.Criteria_9__c = 'CaseStatus';
        oWQP.Criteria_10__c = 'InteractingWithType';
        oWQP.Criteria_11__c = 'OwnerQueue';
        oWQP.Criteria_12__c = 'PreviousCaseOwnerRole';
        oWQP.Criteria_13__c = 'Classification';
        oWQP.Criteria_14__c = 'Intent';
        oWQP.Criteria_15__c = 'Type';
        oWQP.Criteria_16__c = 'Topic';
        oWQP.Criteria_17__c = 'LastModifiedbyQueue';
        oWQP.Criteria_18__c = 'TaskStatus';
        oWQP.Criteria_19__c = 'Product Code';
        oWQP.Criteria_20__c = 'Behavioral Health';
        oWQP.Criteria_21__c = 'BillingStateCode';
        oWQP.Criteria_22__c = 'ShippingStateCode';            
        oWQP.Priority_1__c = '11';
        oWQP.Priority_2__c = '4';
        oWQP.Priority_3__c = '8';
        oWQP.Priority_4__c = '2';
        oWQP.Priority_5__c = '7';
        oWQP.Priority_6__c = '3';
        oWQP.Priority_7__c = '5';
        oWQP.Priority_8__c = '6';
        oWQP.Priority_9__c = '9';
        oWQP.Priority_10__c = '10';
        oWQP.Priority_11__c = '1';
        oWQP.Priority_12__c = '12';
        oWQP.Priority_13__c = '14';
        oWQP.Priority_14__c = '13';
        oWQP.Priority_15__c = '16';
        oWQP.Priority_16__c = '15';
        oWQP.Priority_17__c = '18';
        oWQP.Priority_18__c = '17';
        oWQP.Priority_19__c = '19';  
        oWQP.Priority_20__c = '20';  
        oWQP.Priority_21__c = '21';  
        oWQP.Priority_22__c = '22';    
        insert oWQP;            
        system.assertEquals(oWQP.Criteria_1__c , 'ProductType');                  
    }   
}