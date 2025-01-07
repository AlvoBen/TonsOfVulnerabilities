/***********************************************************************************************************
Apex Class Name  : CreateTask_T_LWC_HUM
Version          : 1.0
Created On      : 23/06/2022
Function         : Test class for CreateTask_T_LWC_HUM
************************************************************************************************************
Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Manoj M K                                         23/06/2022               Original Version
*    2.0         Prasuna Pattabhi                           07/31/2023                 Test Class error fix - build fix
*    3.0         Prasuna Pattabhi                            08/24/23                 US 4412371 Market Credentialing Task Fields
*/

@isTest
private class CreateTask_T_LWC_HUM
{
    
    /**
    * testsetup method to create test data
    *<p>
    * @param            NULL
    * @return           void
    */
    @testSetup static void prepareTestData()
    {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Supervisor');
        oUser.Current_Queue__c='Cincinnati_CRU1';
        insert oUser;
        User HPUser = HUMTestDataHelperTest.createTestUser('HPUser','Humana Pharmacy Specialist');
        HPUser.Current_Queue__c = 'Cincinnati CRU1';
        insert HPUser;
        
        PermissionSet ps = [SELECT Id FROM PermissionSet WHERE Name = 'CRMS_300_Humana_Pharmacy_Supervisor'];
        insert new PermissionSetAssignment(AssigneeId = HPUser.id, PermissionSetId = ps.Id);
        
        Group oGrp = new Group(Type='Queue', Name='Cincinnati_CRU1', DeveloperName = 'Cincinnati_CRU1');
        insert oGrp;
        system.assert(oGrp.Type =='Queue');
        Group oGrp1 = new Group(Type='Queue', Name='Cincinnati CRU1', DeveloperName = 'Cincinnati_CRU12');
        insert oGrp1;
        system.assert(oGrp1.Type =='Queue');
        Group oGrp2 = new Group(Type='Queue', Name='LV Medicare Member Communications', DeveloperName = 'Cincinnati_CRU123');
        insert oGrp2;
        system.assert(oGrp2.Type =='Queue');
        GroupMember gm= new GroupMember(); 
        gm.GroupId=oGrp.id;
        gm.UserOrGroupId = oUser.id;
        insert gm;
        
        User oCUser = [Select Id, Name, email from User where Id=:gm.UserOrGroupId LIMIT 1];
        system.assertEquals(gm.GroupId,oGrp.id);
        
        
        QueuesObject oQueueObject = new QueuesObject (QueueID = oGrp.id, SobjectType ='Work_Queue_Setup__c');
        insert oQueueObject ;  
        System.assert(oQueueObject.SobjectType == 'Work_Queue_Setup__c');
        
        QueuesObject oQueueObject1 = new QueuesObject (QueueID = oGrp1.id, SobjectType ='Work_Queue_Setup__c');
        insert oQueueObject1 ;  
        System.assert(oQueueObject1.SobjectType == 'Work_Queue_Setup__c');
        
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
            oWqs.Public_Group_Name__c ='PG Cincinnati CRU1';
            oWqs.Service_Center__c ='Cincinnati';
            oWqs.Department__c ='CRU1';
            oWQS.CokY__c =true;
            oWqs.TenantID__c ='00';
            oWqs.Classification_Type__c = oClassType.Id;  
            oWqs.IsActive__c = TRUE;            
            oWqs.OwnerId = oGrp.Id; 
            insert oWqs;
            system.assert(oWqs.TenantID__c == '00');
            
            Work_Queue_Setup__c oWqs1 = new Work_Queue_Setup__c(Name=oGrp2.Name);
            oWqs1.Public_Group_Name__c ='LV Medicare Member Communications PGQ';
            oWqs1.Service_Center__c ='Louisville';
            oWqs1.Department__c ='Medicare Member Communications';
            oWqs1.CokY__c =true;
            oWqs1.TenantID__c ='00';
            oWqs1.Classification_Type__c = oClassType.Id;  
            oWqs1.IsActive__c = TRUE;            
            oWqs1.OwnerId = oGrp1.Id; 
            insert oWqs1;
            system.assert(oWqs1.TenantID__c == '00');
            
            
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
                                  Service_Center__c ='Cincinnati',Department__c='CRU1', status='In Progress',Tenant_Id__c = '00',Interacting_About_Type__c = 'Group');
            insert oCase;
            system.assert(oCase.AccountId == oAccount.Id);
            
            Work_Queue_Topic__c owrkqtopic = new Work_Queue_Topic__c();
            owrkqtopic.Name = 'Work Queue Topic';
            insert owrkqtopic;
            system.assertEquals(owrkqtopic.Name,'Work Queue Topic');
            
            Work_Queue_Junction__c owrkqjunction = new Work_Queue_Junction__c();
            owrkqjunction.Name = 'Work Queue Junction';
            owrkqjunction.Work_Queue_Setup_Name__c = oWQS.id;
            owrkqjunction.Work_Queue_Topic_Name__c = owrkqtopic.id;
            insert owrkqjunction;
            
            Work_Queue_Topic__c owrkqtopic1 = new Work_Queue_Topic__c();
            owrkqtopic1.Name = 'Oral Grievance';
            insert owrkqtopic1;
            system.assertEquals(owrkqtopic1.Name,'Oral Grievance');
            
            Work_Queue_Junction__c owrkqjunction1 = new Work_Queue_Junction__c();
            owrkqjunction1.Name = 'Work Queue Junction';
            owrkqjunction1.Work_Queue_Setup_Name__c = oWQS1.id;
            owrkqjunction1.Work_Queue_Topic_Name__c = owrkqtopic1.id;
            insert owrkqjunction1;
            
            
            oCase.Tenant_Id__c = '00';
            oCase.Interacting_About_Type__c = 'Group';
            update oCase;
            system.assert(oCase.Tenant_Id__c == '00');
            
            
            Task oTasknt = HUMTestDataHelperTest.getTaskRecord();
            oTasknt.WhatId = oCase.Id;
            oTasknt.Task_Owner__c = 'User1';
            oTasknt.Owner_Queue__c ='Cincinnati CRU1';            
            oTasknt.status = 'In Progress';
            oTasknt.Service_Center__c = 'Cincinnati';
            oTasknt.Department__c = 'CRU1';
            oTasknt.Callback_Requested__c = 'Yes';
            oTasknt.Time_Zone__c = 'EST - Eastern';
            oTasknt.Call_Back_Start__c = null;
            oTasknt.Call_Back_End__c= null;
            oTasknt.Description = 'Test Comments1';
            oTasknt.QueueOrUserId__c = oUser.Id;
            oTasknt.Type ='Notification Task';
            oTasknt.Call_Back_Start__c = datetime.now().time();
            oTasknt.Call_Back_End__c= datetime.now().time();
            oTasknt.RecordTypeId = Schema.sObjectType.Task.getRecordTypeInfosByName().get('Service CRM').getRecordTypeId();
            insert oTasknt;
            System.assert(oTasknt.status == 'In Progress');
            
            Task oTasknt1 = HUMTestDataHelperTest.getTaskRecord();
            oTasknt1.WhatId = oCase.Id;
            oTasknt1.OwnerId= oUser.Id;
            oTasknt1.Task_Owner__c = 'User1';
            oTasknt1.Owner_Queue__c ='Cincinnati CRU1';            
            oTasknt1.status = 'In Progress';
            oTasknt1.Service_Center__c = 'Cincinnati';
            oTasknt1.Department__c = 'CRU1';
            oTasknt1.Callback_Requested__c = 'No';
            oTasknt1.Time_Zone__c = 'EST - Eastern';
            oTasknt1.Call_Back_Start__c =datetime.now().time();
            oTasknt1.Call_Back_End__c= datetime.now().time();
            oTasknt1.Description = 'Test Comments1';
            oTasknt1.QueueOrUserId__c = oUser.Id;
            oTasknt1.Type ='Notification Task';
            insert oTasknt1;
            System.assert(oTasknt1.status == 'In Progress');
            
            HUM_Task_Heirarchy__c HUMHeirarchy1 = new HUM_Task_Heirarchy__c(Hum_CaseID__c=oCase.Id,HUM_Dependent_TaskID__c =oTasknt.Id, HUM_Parent_TaskID__c=oTasknt1.Id);
            insert HUMHeirarchy1;
            
        }
    }     
    
    
    /*<p>
    * This method is used to cover creating task from dependent button of CreateTask_C_LWC_HUM Class.
    * @param            NULL
    * @return           void
    */
    static testMethod void testMethodTaskFromDependent()
    {       
        User oUser = [ select id,Name,Current_Queue__c from user where LastName='test User1'  limit 1 ];
        try{
            
            System.runAs (oUser) 
            {
                
                Group oGrp1 = [Select id,Name , DeveloperName from Group where  Name='Cincinnati_CRU1' limit 1];
                Case oCase = [ select  id from case   limit 1];
                List<Task> oTask = [ select  id,RecordTypeId, Task_Number__c, Subject,ActivityDate,Priority,Status,Type,Description,OwnerId,
                                    Service_Center__c,Department__c,Topic__c,Work_Queue_View_Name__c,QueueOrUserId__c from task   limit 1];
                Work_Queue_setup__c oWQS = new Work_Queue_setup__c();
                oWQS = [Select Name,id,Classification_Type__r.Name,Classification_Type__c, CokY__c, Department__c, DST__c, General__c, HO_Dental__c,
                        HO_Medical__c, Public_Group_Name__c, Queue_Manager__c, Service_Center__c, TenantID__c, Work_Queue_Name__c,OwnerId from Work_Queue_Setup__c limit 1];
                string sServiceCenter = oWQS.Service_Center__c;
                
                Task TaskData= new Task();
                TaskData.WhatId= oCase.Id;
                TaskData.ActivityDate=Date.today();
                TaskData.Priority='Normal';
                TaskData.Status='In Progress';
                TaskData.Type='Work Task';
                TaskData.Description='hellow';
                TaskData.OwnerId= oUser.id;
                TaskData.Service_Center__c= 'Cincinnati';
                TaskData.Department__c= 'CRU1';
                TaskData.Topic__c='Work Queue Topic';
                TaskData.Owner_Queue__c ='Cincinnati CRU1';
                TaskData.Callback_Requested__c = 'Yes';
                TaskData.Time_Zone__c = 'EST - Eastern';
                TaskData.Call_Back_Start__c = null;
                TaskData.Call_Back_End__c= null;
                TaskData.Task_Owner__c= oUser.Name;
                TaskData.QueueOrUserId__c = oUser.Id;
                TaskData.Call_Back_Start__c = datetime.now().time();
                TaskData.Call_Back_End__c= datetime.now().time();
                
                TaskData.QueueOrUserId__c= oUser.Name;
                TaskData.Owner_Queue__c='Cincinnati CRU1';
                TaskData.RecordTypeId = Schema.sObjectType.Task.getRecordTypeInfosByName().get('Service CRM').getRecordTypeId();
                insert TaskData;
                
                Task_Field_History__c oTaskFH = new Task_Field_History__c ();
                oTaskFH.SObjID__c= TaskData.Id;
                oTaskFH.SObjFieldName__c='Priority';
                oTaskFH.SObjFieldValue__c='Critical';
                oTaskFH.Created_At__c = System.now();
                insert oTaskFH;
                
                HUM_Task_Heirarchy__c TaskHeir = new HUM_Task_Heirarchy__c();
                TaskHeir.HUM_Parent_TaskID__c=TaskData.id;
                
                Map<string,Object> mapTask =new Map<string,Object>();
                mapTask.put('taskFields',TaskData);
                mapTask.put('taskHistoryFields',TaskHeir);
                
                Map<string,Object> mapTask2 =new Map<string,Object>();
                mapTask2.put('taskFields',TaskData);
                
                Test.startTest();
                CreateTask_C_LWC_HUM.getWorkQueueDetail();
                CreateTask_C_LWC_HUM.getLstServiceCenter(TaskData.id);
                CreateTask_C_LWC_HUM.getDepartments(sServiceCenter,TaskData.id);
                CreateTask_C_LWC_HUM.updateTaskSubject(oTask[0].Id); 
                CreateTask_C_LWC_HUM.fetchTaskDetail(oTask[0].id,'LeftPanel');
                CreateTask_C_LWC_HUM.getMedicareId(oCase.id);
                CreateTask_C_LWC_HUM.createTask(JSON.serialize(mapTask));
                CreateTask_C_LWC_HUM.createTask(JSON.serialize(mapTask2));
                Test.stopTest();
                
            }  
        }
        catch(Exception ex){
            HUMExceptionHelper.logErrors(ex, 'CreateTask_T_LWC_HUM', 'testMethodTaskFromDependent');
        }
        
    } 
    
    
    /* 
    *<p>
    * This method is used to cover creating task from case detail page of CreateTask_C_LWC_HUM Class.
    * @param            NULL
    * @return           void
    */
    
    static testMethod void testMethodTaskFromCase()
    {       
        User oUser = [ select id,Current_Queue__c,Name from user where LastName='test User1'  limit 1 ];
        Case oCase = [ select  id from case   limit 1];
        
        try{
            System.runAs (oUser) {
                
                Work_Queue_setup__c oWQS = new Work_Queue_setup__c();
                oWQS = [Select Name,id,Classification_Type__r.Name,Classification_Type__c, CokY__c, Department__c, DST__c, General__c, HO_Dental__c,
                        HO_Medical__c, Public_Group_Name__c, Queue_Manager__c, Service_Center__c, TenantID__c, Work_Queue_Name__c,OwnerId from Work_Queue_Setup__c limit 1];
                
                CreateTask_C_LWC_HUM.TopicANdUserLookupWrapper topicUser =new CreateTask_C_LWC_HUM.TopicANdUserLookupWrapper();
                topicUser= CreateTask_C_LWC_HUM.getTopics('Cincinnati','CRU1'); 
                
                CreateTask_C_LWC_HUM.TaskWrapper TaskData =new CreateTask_C_LWC_HUM.TaskWrapper();            
                
                TaskData.WhatId= oCase.Id;
                TaskData.ActivityDate=String.valueOf(system.today());
                TaskData.Priority='High';
                TaskData.Status='Pending';
                TaskData.Type='Work Task';
                TaskData.Description='hiiiii';
                TaskData.OwnerId= oUser.id;
                TaskData.ServiceCenter= 'Cincinnati';
                TaskData.Department= 'CRU1';
                TaskData.Topic='Work Queue Topic';
                TaskData.WorkQueueName=topicUser.workQueue;
                TaskData.QueueOrUserId= oUser.Id;
                
                Map<string,Object> mapTask =new Map<string,Object>();
                mapTask.put('taskFields',TaskData);
                
                Test.startTest();        
                CreateTask_C_LWC_HUM.createTask(JSON.serialize(mapTask)); 
                 CreateTask_C_LWC_HUM.getDepartments('','1234'); 
                Test.stopTest();                                              
            }             
        } 
        catch(Exception ex){
            HUMExceptionHelper.logErrors(ex, 'CreateTask_T_LWC_HUM', 'testMethodTaskFromDependent');
        }
    }
    /**
    * 
    *<p>
    * This method is used to cover edit task creation of CreateTask_C_LWC_HUM Class.
    * @param            NULL
    * @return           void
    */
    
    static testMethod void testMethodEditTask()
    {  
        User oUser = [ select id,Current_Queue__c,Name from user where LastName='test User1'  limit 1 ];
        Case oCase = [ select  id from case   limit 1];
        
        try{
            System.runAs (oUser) {
                
                Work_Queue_setup__c oWQS = new Work_Queue_setup__c();
                oWQS = [Select Name,id,Classification_Type__r.Name,Classification_Type__c, CokY__c, Department__c, DST__c, General__c, HO_Dental__c,
                        HO_Medical__c, Public_Group_Name__c, Queue_Manager__c, Service_Center__c, TenantID__c, Work_Queue_Name__c,OwnerId from Work_Queue_Setup__c limit 1];
                
                Task oTask = [ select  id,WhatId,RecordTypeId, Task_Number__c, Subject,ActivityDate,Priority,Status,Type,Description,OwnerId,
                              Service_Center__c,Department__c,Topic__c,Work_Queue_View_Name__c,QueueOrUserId__c from task   limit 1];
                
                CreateTask_C_LWC_HUM.TopicANdUserLookupWrapper topicUser =new CreateTask_C_LWC_HUM.TopicANdUserLookupWrapper();
                topicUser= CreateTask_C_LWC_HUM.getTopics('Cincinnati','CRU1'); 
                
                
                Map<string,String> mapTask =new Map<string,String>();
                mapTask.put('Id',oTask.Id);
                mapTask.put('ActivityDate',String.valueOf(system.today()));
                mapTask.put('Priority','High');
                mapTask.put('Status','Pending');
                mapTask.put('Type','Work Task');
                mapTask.put('Description','test1');
                mapTask.put('OwnerId', oUser.id);
                mapTask.put('ServiceCenter','Cincinnati');
                mapTask.put('Department','CRU1');
                mapTask.put('Topic','Work Queue Topic');
                mapTask.put('WorkQueueName',topicUser.workQueue);
                mapTask.put('QueueOrUserId',oUser.id);
                
                
                Map<string,String> mapTask1 =new Map<string,String>();
                mapTask1.put('Id',oTask.Id);
                mapTask1.put('ActivityDate',String.valueOf(system.today()));
                mapTask1.put('Priority','High');
                mapTask1.put('Status','Pending');
                mapTask1.put('Type','Work Task');
                mapTask1.put('Description','test1');
                mapTask1.put('OwnerId', null);
                mapTask1.put('ServiceCenter','Cincinnati');
                mapTask1.put('Department','CRU1');
                mapTask1.put('Topic','Work Queue Topic');
                mapTask1.put('WorkQueueName',topicUser.workQueue);
                mapTask1.put('QueueOrUserId',null);
                
                Test.startTest();
                CreateTask_C_LWC_HUM.editTask(JSON.serialize(mapTask));
                CreateTask_C_LWC_HUM.editTask(JSON.serialize(mapTask1));
                CreateTask_C_LWC_HUM.editTask(null);
                Test.stopTest();          
            }
        }
        catch(Exception ex){
            HUMExceptionHelper.logErrors(ex, 'CreateTask_T_LWC_HUM', 'testMethodEditTask');
        }
    }
    
    /**
    * 
    *<p>
    * This method is used to test as HpUser of CreateTask_C_LWC_HUM Class.
    * @param            NULL
    * @return           void
    */
    
    static testMethod void testHPUser()
    {  
        User oUserHP = [ select id,Current_Queue__c,Name from user where LastName='test HPUser'  limit 1 ];
        
        try{
            System.runAs (oUserHP) {
                
                Account oAccount = HUMTestDataHelperTest.getAccount();
                oAccount.Name = 'New Case Account';
                oAccount.Group_Number__c  = '123';
                oAccount.BillingState = 'New York';
                oAccount.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
                insert oAccount;
                System.assert(oAccount.BillingState == 'New York');
                
                Case oCase = new Case(AccountId=oAccount.Id, CoKY_Case__c = True,Interacting_With_Type__c = 'Group',
                                      Interacting_With__c=oAccount.Id, Due_Date__c=Date.Today(), Case_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU1', Work_Queue_View_Name__c='Cincinati CRU1',
                                      Service_Center__c ='Cincinnati',Department__c='CRU1', status='In Progress',Tenant_Id__c = '00');
                insert oCase;
                system.assert(oCase.AccountId == oAccount.Id);
                
                Task oTask = HUMTestDataHelperTest.getTaskRecord();
                oTask.WhatId = oCase.Id;
                oTask.OwnerId= oUserHP.Id;
                oTask.Task_Owner__c = 'User1';
                oTask.Owner_Queue__c ='Cincinnati CRU1';            
                oTask.status = 'In Progress';
                oTask.Service_Center__c = 'Cincinnati';
                oTask.Department__c = 'CRU1';
                oTask.Callback_Requested__c = 'No';
                oTask.Time_Zone__c = 'EST - Eastern';
                oTask.Call_Back_Start__c =datetime.now().time();
                oTask.Call_Back_End__c= datetime.now().time();
                oTask.Description = 'Test Comments1';
                oTask.QueueOrUserId__c = oUserHP.Id;
                oTask.Type ='Notification Task';
                insert oTask;
                System.assert(oTask.status == 'In Progress');
                
                Case oCase1 = [ select  id from case   limit 1];
                List<Task> oTask1 = [ select  id,RecordTypeId, Task_Number__c, Subject,ActivityDate,Priority,Status,Type,Description,OwnerId,
                                     Service_Center__c,Department__c,Topic__c,Work_Queue_View_Name__c,QueueOrUserId__c from task   limit 1];
                Work_Queue_setup__c oWQS = new Work_Queue_setup__c();
                oWQS = [Select Name,id,Classification_Type__r.Name,Classification_Type__c, CokY__c, Department__c, DST__c, General__c, HO_Dental__c,
                        HO_Medical__c, Public_Group_Name__c, Queue_Manager__c, Service_Center__c, TenantID__c, Work_Queue_Name__c,OwnerId from Work_Queue_Setup__c limit 1];
                string sServiceCenter = oWQS.Service_Center__c;
                
                Test.startTest(); 
                CreateTask_C_LWC_HUM.getLstServiceCenter(oTask1[0].id);
                CreateTask_C_LWC_HUM.getDepartments(sServiceCenter,oTask1[0].id);              
                CreateTask_C_LWC_HUM.TopicANdUserLookupWrapper topicUser =new CreateTask_C_LWC_HUM.TopicANdUserLookupWrapper();
                topicUser= CreateTask_C_LWC_HUM.getTopics('Louisville','Medicare Member Communications'); 
                topicUser= CreateTask_C_LWC_HUM.getTopics('Dental','Dedicated ASO Calls'); 
                CreateTask_C_LWC_HUM.getLstServiceCenter(null);
                
                Test.stopTest();
            }
        }
        catch(Exception ex){
            HUMExceptionHelper.logErrors(ex, 'CreateTask_T_LWC_HUM', 'testHPUser');
        }
    }
    
    /**
    * 
    *<p>
    * This method is used to cover catch block of getMedicareId method on CreateTask_C_LWC_HUM Class.
    * @param            NULL
    * @return           void
    */
    
    static testMethod void testCatchgetMedicareId()
    {  
        User oUser = [ select id,Current_Queue__c,Name from user where LastName='test User1'  limit 1 ];
        
        try{
            System.runAs (oUser) {
                Test.startTest(); 
                CreateTask_C_LWC_HUM.getMedicareId('1234');    
                Test.stopTest();
            }
        }
        catch(Exception ex){
            HUMExceptionHelper.logErrors(ex, 'CreateTask_T_LWC_HUM', 'testCatchgetMedicareId');
        }
    }
    
    /**
    * 
    *<p>
    * This method is used to cover credentialingEligible,getCredentialingTaskPicklist method on CreateTask_C_LWC_HUM Class.
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
            CreateTask_C_LWC_HUM.credentialingEligible(oTasknt.Id,'Task');
            CreateTask_C_LWC_HUM.getCredentialingTaskPicklist();
            CreateTask_C_LWC_HUM.credentialingEligible(oCase.Id,'');
            Test.stopTest();

      }    
    }
}