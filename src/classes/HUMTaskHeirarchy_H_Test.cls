/*******************************************************************************************************************************
Apex Class Name : HUMTaskHeirarchy_H _Test
Version         : 1.0
Created On      : 10/21/2016
Function        : Test class for Helper Class HUMTaskHeirarchy_H

Modification Log: 
* Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Vamsi Kondragunta             44606                   10/21/2016              Changes for  Req:72833
* Pradeepkumar Dani             99570                   06/08/2017              Intent__c.Classification__c & Intent__c.Etl_Idtfr__c Clean up
* Pradeepkumar Dani            100996                   06/13/2017              Classification__c.Classification_Type__c & Etl_Idftr__c Clean Up
* Santhi Mandava                                        03/12/2019              Removed "DpaaSWebServiceMockImpl" usage.
*******************************************************************************************************************************/
@istest
public class HUMTaskHeirarchy_H_Test {
    
     Static Testmethod void MyUnitTest(){
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        oUser.Current_Queue__c = 'Cincinnati CRU';
        HUM_Task_Heirarchy__c humHierarchy = new HUM_Task_Heirarchy__c();
        system.assertEquals(oUser.lastname =='test User1',true);
        System.runas(oUser){
            insert new HUMConstants__c( Name = 'HUMClosed' , IntegerValue__c = 15, StringValue__c = '15');
            insert new HUMConstants__c( Name = 'HUMWorkTask' , StringValue__c = 'Medicare Address Verification Form');
            Account oAcc = HUMTestDataHelperTest.getAccount(); 
            oAcc.Name='TestAccount1';
            oAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
            insert oAcc;  
             
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
            List<Case> testcases = new List<Case>();
            Case oCase1 = new Case(AccountId  = oAcc.Id, Intent_id__c = intentRec.id, Classification_ID__c = classificationRecDec.id, 
                               Interacting_With_Type__c = 'Group', Interacting_With__c = oAcc.Id, Due_Date__c = Date.Today(),
                               Case_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU', Work_Queue_View_Name__c='Cincinati CRU', Open_Work_Task_Count__c=NULL,
                               Total_Work_Task_Count__c=NULL,Service_Center__c = 'Cincinnati', Department__c = 'CRU', status = 'In Progress');
            testcases.add(oCase1);
            insert testcases;                        
            system.assertEquals(testcases[0].status == 'In Progress',true);
            
            List<Task> testTasks = new List<Task>();    
            Task oTask1 = new Task(WhatId = oCase1.id,type='Medicare Address Verification Form', Task_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU',
                               Work_Queue_View_Name__c='Cincinati CRU', Service_Center__c ='Cincinnati', Department__c='CRU', Open_Work_Task_Count__c=NULL,
                               Total_Work_Task_Count__c=NULL,status='In Progress');
            testTasks.add(oTask1);  
            Task oTask2 = new Task(WhatId = oCase1.id,type='Medicare Address Verification Form',  Task_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU', 
                               Work_Queue_View_Name__c='Cincinati CRU', Service_Center__c ='Cincinnati', Department__c='CRU', Open_Work_Task_Count__c=NULL,
                               Total_Work_Task_Count__c=NULL,status='In Progress');
            //oTask2.humHierarchy.HUM_Parent_TaskID__c= oTask1.id;
            testTasks.add(oTask2);  
            Task oTask3 = new Task(WhatId = oCase1.id,type='Medicare Address Verification Form',  Task_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU', 
                               Work_Queue_View_Name__c='Cincinati CRU', Service_Center__c ='Cincinnati', Department__c='CRU', Open_Work_Task_Count__c=NULL,
                               Total_Work_Task_Count__c=NULL,status='Closed');
            testTasks.add(oTask3);
            insert testTasks;  
            
            //HUMConstants__c HumConstants= new HUMConstants__c(HUMClosed='Closed',HUMWorkTask='Work Task');
            //Insert HumConstants;
            
            HUM_Task_Heirarchy__c HUMHeirarchy = new HUM_Task_Heirarchy__c(Hum_CaseID__c=oCase1.Id,HUM_Parent_TaskID__c=Null,HUM_Dependent_TaskID__c=oTask2.Id,Immediate_Parent__c=False);
            insert HUMHeirarchy;
            
             HUM_Task_Heirarchy__c HUMHeirarchy1 = new HUM_Task_Heirarchy__c(Hum_CaseID__c=oCase1.Id,HUM_Dependent_TaskID__c =oTask2.Id, HUM_Parent_TaskID__c=oTask1.Id);
            insert HUMHeirarchy1;
            
             HUM_Task_Heirarchy__c HUMHeirarchy2 = new HUM_Task_Heirarchy__c(Hum_CaseID__c=oCase1.Id,HUM_Dependent_TaskID__c =oTask2.Id,HUM_Parent_TaskID__c=oTask1.Id);
            insert HUMHeirarchy2;
            
            List<HUM_Task_Heirarchy__c> lstTaskNewValues = New List<HUM_Task_Heirarchy__c>();
            lstTaskNewValues.Add(HUMHeirarchy);
            lstTaskNewValues.Add(HUMHeirarchy1);
            lstTaskNewValues.Add(HUMHeirarchy2);
            HUMTaskHeirarchy_H  obj = new HUMTaskHeirarchy_H();
            obj.CountofOpenTaskForParent(lstTaskNewValues);
        }
        
    }
static void setUpTestData()
    {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        oUser.Current_Queue__c = 'Cincinnati CRU';
        HUM_Task_Heirarchy__c humHierarchy = new HUM_Task_Heirarchy__c();
        HUMTaskHeirarchy_H HUMHierarchyI = new HUMTaskHeirarchy_H();
        insert oUser;
        system.assertEquals(oUser.lastname =='test User1',true);        
        system.runAs(oUser)
        {
            Account oAcc = HUMTestDataHelperTest.getAccount(); 
            oAcc.Name='TestAccount1';
            oAcc.Source_Platform_Code__c = 'CA';
            oAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
            insert oAcc;            
           System.assertEquals(oAcc.Source_Platform_Code__c == 'CA', true);
             
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
            List<Case> testcases = new List<Case>();
            Case oCase1 = new Case(AccountId  = oAcc.Id, Intent_id__c = intentRec.id, Classification_ID__c = classificationRecDec.id, 
                               Interacting_With_Type__c = 'Group', Interacting_With__c = oAcc.Id, Due_Date__c = Date.Today(),
                               Case_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU', Work_Queue_View_Name__c='Cincinati CRU', Open_Work_Task_Count__c=NULL,
                               Total_Work_Task_Count__c=NULL,Service_Center__c = 'Cincinnati', Department__c = 'CRU', status = 'In Progress');
            testcases.add(oCase1);
            Case oCase2 = new Case(AccountId = oAcc.Id, Intent_id__c = intentRec.id, Classification_ID__c = classificationRecDec.id, 
                               Interacting_With_Type__c = 'Group',Interacting_With__c = oAcc.Id, Due_Date__c = Date.Today(),
                               Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c = 'Cincinnati CRU', Work_Queue_View_Name__c = 'Cincinati CRU', Open_Work_Task_Count__c=NULL,
                               Total_Work_Task_Count__c=NULL,Service_Center__c = 'Cincinnati', Department__c = 'CRU', status ='In Progress'); 
            
            testcases.add(oCase2);           
            insert testcases;                        
            system.assertEquals(testcases[0].status == 'In Progress',true);
            
            List<Task> testTasks = new List<Task>();    
            Task oTask1 = new Task(WhatId = oCase1.id,type='Work Task', Task_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU',
                               Work_Queue_View_Name__c='Cincinati CRU', Service_Center__c ='Cincinnati', Department__c='CRU', Open_Work_Task_Count__c=NULL,
                               Total_Work_Task_Count__c=NULL,status='In Progress');
            testTasks.add(oTask1);  
            Task oTask2 = new Task(WhatId = oCase2.id,type='Work Task',  Task_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU', 
                               Work_Queue_View_Name__c='Cincinati CRU', Service_Center__c ='Cincinnati', Department__c='CRU', Open_Work_Task_Count__c=NULL,
                               Total_Work_Task_Count__c=NULL,status='In Progress');
            //oTask2.humHierarchy.HUM_Parent_TaskID__c= oTask1.id;
            testTasks.add(oTask2);  
            Task oTask3 = new Task(WhatId = oCase2.id,type='Work Task',  Task_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU', 
                               Work_Queue_View_Name__c='Cincinati CRU', Service_Center__c ='Cincinnati', Department__c='CRU', Open_Work_Task_Count__c=NULL,
                               Total_Work_Task_Count__c=NULL,status='Closed');
            testTasks.add(oTask3);
            insert testTasks;    
         }
    }
private static  void OverAllMainTestMethod()
    {
        List<Task> lstOfTasksToUpdate = new List<Task>();
        HUMTaskHeirarchy_H objHUM= new HUMTaskHeirarchy_H();
        User oUser = [select id, name,lastname from user where lastname='test User1' limit 1];
        system.assertEquals(oUser.lastname =='test User1',true); 
        system.runAs(oUser)
        {
                Test.startTest();
                 HUMTaskHeirarchy_H objsHUM= new HUMTaskHeirarchy_H();
                Test.stopTest();
        }
    }     
}