/*************************************************************************************************************************
Apex Class Name  : WorkQueueCreation_T_HUM
Version          : 1.0
Created Date     : March 30 2016
Function         : Test class to WorkQueueCreation classes

* Modification Log :
*   Developer                Code Review            Date                 Description
* -------------------------------------------------------------------------------------------------------------------------
* Harshith Mandya             30706              03/16/2016            Modified to meet the standards
* Harshith Mandya             30706              06/26/2016            Updated code as per latest standards
* Pradeepkumar Dani           74579              03/09/2017            Modified for the Classification_Type__c reference
* Gargey Sharma               81589              04/04/2017            REQ- 306370 : Updates as per CTCI changes
* Suresh Sunkari		                         09/20/2018            Modified for Req- 374839
* Pooja Kumbhar                                  08/28/2020            Changes done to increase code coverage 
* Prudhvi Pamarthi                               05/05/2021           Modified to increase code coverage
* Tummala Vijaya Lakshmi	                     08/27/2021	          USER STORY 2578061  -T1PRJ0002980/PR00095219 - MF 5 - Contracted Protected Data for Q/V App
* Pooja Kumbhar                                  09/01/2021           Increase code coverage
* Tummala Vijaya Lakshmi						 09/27/2021		      test class failures resolved
* Tummala Vijaya Lakshmi						 10/06/2021		      test class code coverage
***************************************************************************************************************************/
@isTest
private class WorkQueueCreation_T_HUM 
{    
   /**
    * @testSetup method to create test data
    *<p>
    * @param            NULL
    * @return           void
    */
    @testSetup 
    static void createCustomData() 
    {
        User oUser = HUMTestDataHelperTest.createTestUser('WQUser1','System Administrator');
        oUser.Current_Queue__c='Test';
        insert oUser;
        System.assert(oUser.Current_Queue__c == 'Test');
        Group grp = new Group(Name = 'Test PGQ' ,  developerName = 'test1' , Type='Regular');
        Group oGrp;
        Group oGrp1;
        Group oGrp2;
        Group oGrp3;
        Group oGrp4;
        System.runas(oUser)
        {
            insert grp;
            System.assert(grp.Name == 'Test PGQ');
            
            GroupMember grpMem = HUMTestDataHelperTest.getGrpMems(grp.id , oUser.id);
            insert grpMem ;
            System.assert(grpMem.GroupId == grp.id);
            
            oGrp = new Group(Name = 'Cincit Calls', Type = 'QUEUE');
            insert oGrp;
            System.assert(oGrp.Name == 'Cincit Calls');

            QueuesObject oQueueObject = new QueuesObject (QueueID = oGrp.id, SobjectType ='Work_Queue_Setup__c');
            insert oQueueObject;
            System.assert(oQueueObject.SobjectType == 'Work_Queue_Setup__c');
            
            oGrp1 = new Group(Name = 'Cincit Call', Type = 'QUEUE');
            insert oGrp1;
            System.assert(oGrp1.Type == 'QUEUE');

            QueuesObject oQueueObject1 = new QueuesObject (QueueID = oGrp1.id, SobjectType ='Work_Queue_Setup__c');
            insert oQueueObject1;
            System.assert(oQueueObject1.SobjectType == 'Work_Queue_Setup__c');
            
            oGrp2 = new Group(Name = 'Medical Call', Type = 'QUEUE');
            insert oGrp2;
            System.assert(oGrp2.Name == 'Medical Call');

            QueuesObject oQueueObject2 = new QueuesObject (QueueID = oGrp2.id, SobjectType ='Work_Queue_Setup__c');
            insert oQueueObject2;
            System.assert(oQueueObject2.SobjectType == 'Work_Queue_Setup__c');
            
            oGrp3 = new Group(Name = 'Medical Calls', Type = 'QUEUE');
            insert oGrp3;
            System.assert(oGrp3.Name == 'Medical Calls');

            QueuesObject oQueueObject3 = new QueuesObject (QueueID = oGrp3.id, SobjectType ='Work_Queue_Setup__c');
            insert oQueueObject3;
            System.assert(oQueueObject3.SobjectType == 'Work_Queue_Setup__c');
            
            oGrp4 = new Group(Name = 'Medical Calls', Type = 'QUEUE');
            insert oGrp4;
            System.assert(oGrp4.Name == 'Medical Calls');

            QueuesObject oQueueObject4 = new QueuesObject (QueueID = oGrp4.id, SobjectType ='Work_Queue_Setup__c');
            insert oQueueObject4;
            System.assert(oQueueObject4.SobjectType == 'Work_Queue_Setup__c');
            
            Classification_Type__c ct = new Classification_Type__c(Name = 'Test', Is_Active__c = true, Guidance_Alert_Limit__c = 2);
            insert ct;
            system.assertEquals(ct.Name, 'Test');

            Work_Queue_Setup__c oWQS = new Work_Queue_Setup__c();
            oWQS.Name = 'Cincit Calls';
            oWQS.Service_Center__c='Cincit';
            oWQS.Department__c='Calls';
            oWQS.Classification_Type__c = ct.Id;
            oWQS.IsActive__c = false;
            oWQS.OwnerId= oGrp.id;
            oWQS.Public_Group_Queue_Id__c = oQueueObject.Id;
            oWQS.Security_Group__c = 'Home Office All';
            oWQS.General__c = true;
            oWQS.HO_Medical__c = true;
            oWQS.HO_Dental__c = true;
            insert oWQS;
            System.assert(oWQS.Security_Group__c == 'Home Office All');
            
            Queue_View__c queueView = HUMTestDataHelperTest.getQueueView();
            queueView.Work_Queue_Name__c = oWQS.Id;
            queueView.IsActive__c = false;
            User oUser1 = [Select Id , Name from User where Username= 'WQUser1WQUser1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
            queueView.View_Manager__c = oUser1.id;
            insert queueView;
            System.assertEquals(false,queueView.IsActive__c);
            
            Queue_View__c queueView1 = New Queue_View__c();
            queueView1.Work_Queue_Name__c = oWQS.Id;
            queueView1.Name = 'Test QV One';
            queueView1.IsActive__c = true;
            queueView1.View_Manager__c = oUser1.id;
            insert queueView1;
            System.assertEquals(true,queueView1.IsActive__c);
            
            Work_Queue_Setup__c oWQS1 = new Work_Queue_Setup__c();
            oWQS1.Name = 'Cincit Call';
            oWQS1.Service_Center__c='Cincit';
            oWQS1.Department__c='Call';
            oWQS1.Classification_Type__c = ct.Id;
            oWQS1.IsActive__c = false;
            oWQS1.OwnerId= oGrp1.id;
            oWQS1.Public_Group_Queue_Id__c = oQueueObject1.Id;
            oWQS1.Security_Group__c = 'Home Office Dental';
            oWQS1.General__c = true;
            oWQS1.HO_Medical__c = false;
            oWQS1.HO_Dental__c = true;
            insert oWQS1;
            System.assert(oWQS1.Security_Group__c == 'Home Office Dental');
            
            Work_Queue_Setup__c oWQS2 = new Work_Queue_Setup__c();
            oWQS2.Name = 'Medical Call';
            oWQS2.Service_Center__c='Medical';
            oWQS2.Department__c='Call';
            oWQS2.Classification_Type__c = ct.Id;
            oWQS2.IsActive__c = false;
            oWQS2.OwnerId= oGrp2.id;
            oWQS2.Public_Group_Queue_Id__c = oQueueObject2.Id;
            oWQS2.Security_Group__c = 'Home Office Medical/Pharmacy';
            oWQS2.General__c = true;
            oWQS2.HO_Medical__c = true;
            oWQS2.HO_Dental__c = false;
            insert oWQS2;
            System.assert(oWQS2.Security_Group__c == 'Home Office Medical/Pharmacy');
            
            Work_Queue_Setup__c oWQS3 = new Work_Queue_Setup__c();
            oWQS3.Name = 'Medical Calls';
            oWQS3.Service_Center__c='Medical';
            oWQS3.Department__c='Calls';
            oWQS3.Classification_Type__c = ct.Id;
            oWQS3.IsActive__c = false;
            oWQS3.OwnerId= oGrp3.id;
            oWQS3.Public_Group_Queue_Id__c = oQueueObject3.Id;
            oWQS3.Security_Group__c = 'Humana General';
            oWQS3.General__c = true;
            oWQS3.HO_Medical__c = true;
            oWQS3.HO_Dental__c = false;
            insert oWQS3;
            System.assert(oWQS3.Security_Group__c == 'Humana General');
            
            Work_Queue_Setup__c oWQS4 = new Work_Queue_Setup__c();
            oWQS4.Name = 'Medical Calls';
            oWQS4.Service_Center__c='Medical';
            oWQS4.Department__c='Calls';
            oWQS4.Classification_Type__c = ct.Id;
            oWQS4.IsActive__c = false;
            oWQS4.OwnerId= oGrp3.id;
            oWQS4.Public_Group_Queue_Id__c = oQueueObject3.Id;
            oWQS4.Security_Group__c = 'Home Office Medical/Pharmacy';
            oWQS4.General__c = true;
            oWQS4.HO_Medical__c = true;
            oWQS4.HO_Dental__c = false;
            insert oWQS4;
            System.assert(oWQS4.Security_Group__c == 'Home Office Medical/Pharmacy');
            
            HUMTestDataHelperTest.insertTestDataForQueuesPriority(oWQS);
            
            Work_Queue_Topic__c oWQT = new Work_Queue_Topic__c();
            oWQT.Name = 'Test Topic'; 
            insert oWQT;
            System.assert(oWQT.Name == 'Test Topic');
            
            Work_Queue_Junction__c oWQJ = new Work_Queue_Junction__c();
            oWQJ.Name = 'Test Junction';
            oWQJ.Work_Queue_Setup_Name__c = oWQS.id;
            oWQJ.Work_Queue_Topic_Name__c = oWQT.Id;
            insert oWQJ;
            System.assert(oWQJ.Name == 'Test Junction');
            
            HUMSecurityGroupAndIndicators__c securityGroupIndicatorRecord = new HUMSecurityGroupAndIndicators__c ();
            securityGroupIndicatorRecord.Name ='Humana General';
            securityGroupIndicatorRecord.TenantID__c ='00';
            securityGroupIndicatorRecord.General__c =true;
            insert securityGroupIndicatorRecord;
            system.assertEquals('Humana General', securityGroupIndicatorRecord.Name);
            
            List<HUMWorkQueuePriorityCriteria__c> lstWQPCriteria = new List<HUMWorkQueuePriorityCriteria__c>();
            for(Integer i=1;i<=22;i++)
            {
                HUMWorkQueuePriorityCriteria__c wqpCriteria = new HUMWorkQueuePriorityCriteria__c();
                wqpCriteria.Name = 'Criteria_'+i;   
                wqpCriteria.CriteriaValue__c = 'Criteria '+i;
                lstWQPCriteria.add(wqpCriteria);
            }
            insert lstWQPCriteria;
            System.assertEquals(22,lstWQPCriteria.size());
        }
        
    }
       
   /**
    * Test method for edit.
    *<p>
    * @param            NULL
    * @return           void
    */
    static testMethod void testMethodEdit()
    {
        User oUser = [Select Id , Name from User where Username= 'WQUser1WQUser1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
        Work_Queue_Setup__c  oWQS =  [Select Id , Name, Service_Center__c, Department__c, Classification_Type__c, OwnerId, Security_Group__c, General__c, HO_Medical__c, HO_Dental__c  from Work_Queue_Setup__c where Name = 'Cincit Calls' Limit 1]; 
        list<Group> queueGrpRecord = [Select Id, Name, type from Group where createdById =: oUser.Id and type ='Queue' and Name = 'Cincit Calls'];
        Work_Queue_Topic__c oWQT = [Select id, Name from Work_Queue_Topic__c limit 1];
        
        system.runas(oUser)
        {
            HUMTestDataHelperTest.getHUMConstantsData();
            
            apexpages.currentpage().getparameters().put('id' , oWQS.id);
            ApexPages.StandardController sc = new ApexPages.StandardController(oWQS);
            WorkQueueCreation_C_HUM testWQS = new  WorkQueueCreation_C_HUM(sc);
            testWQS.bEditBoolean = true;    
            Test.startTest();
            testWQS.securityGroupSelected = 'Humana General';
            testWQS.enableIndicators();
            system.assertEquals(true, oWQS.General__c);
            testWQS.createWorkQueueGroupPGQ();
            testWQS.updateWorkQueueGroupPGQ();
            system.assertEquals(true, testWQS.bEditBoolean);
            testWQS.createWorkQueue();
            system.assertEquals(true, queueGrpRecord.size() > 0,'A Queue group record should have been created on inserting a queue');
            system.assertEquals('Queue', queueGrpRecord[0].type,'Type of the inserted group should have been  Queue');
            system.assertEquals(queueGrpRecord[0].Id, oWQS.OwnerId, 'The work queue setup record should have a referece to a Queue');
            testWQS.addTopic();
            system.assertEquals(0, testWQS.topicNames.size());
            Test.stopTest();
        }    
    }
        
   /**
    * Test method for edit.
    *<p>
    * @param            NULL
    * @return           void
    */
    static testMethod void testMethodEdit1()
    {
        User oUser = [Select Id , Name from User where Username= 'WQUser1WQUser1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
        Work_Queue_Setup__c  oWQS =  [Select Id , Name, Service_Center__c, Department__c, Classification_Type__c, OwnerId, Security_Group__c, General__c, HO_Medical__c, HO_Dental__c  from Work_Queue_Setup__c where Name = 'Cincit Call' Limit 1]; 
        list<Group> queueGrpRecord = [Select Id, Name, type from Group where createdById =: oUser.Id and type ='Queue' and Name = 'Cincit Call'];
        
        system.runas(oUser)
        {
            HUMTestDataHelperTest.getHUMConstantsData();
            
            apexpages.currentpage().getparameters().put('id' , oWQS.id);
            ApexPages.StandardController sc = new ApexPages.StandardController(oWQS);
            WorkQueueCreation_C_HUM testWQS = new  WorkQueueCreation_C_HUM(sc);
            testWQS.bEditBoolean = true;    
            Test.startTest();
            system.assertEquals('Home Office Dental', oWQS.Security_Group__c);
            Test.stopTest();
        }    
    }
    
   /**
    * Test method for edit.
    *<p>
    * @param            NULL
    * @return           void
    */
    static testMethod void testMethodEdit2()
    {
        User oUser = [Select Id , Name from User where Username= 'WQUser1WQUser1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
        Work_Queue_Setup__c  oWQS =  [Select Id , Name, Service_Center__c, Department__c, Classification_Type__c, OwnerId, Security_Group__c, General__c, HO_Medical__c, HO_Dental__c  from Work_Queue_Setup__c where Name = 'Medical Call' Limit 1]; 
        list<Group> queueGrpRecord = [Select Id, Name, type from Group where createdById =: oUser.Id and type ='Queue' and Name = 'Medical Call'];
        
        system.runas(oUser)
        {
            HUMTestDataHelperTest.getHUMConstantsData();
            
            apexpages.currentpage().getparameters().put('id' , oWQS.id);
            ApexPages.StandardController sc = new ApexPages.StandardController(oWQS);
            WorkQueueCreation_C_HUM testWQS = new  WorkQueueCreation_C_HUM(sc);
            testWQS.bEditBoolean = true;    
            Test.startTest();
            system.assertEquals('Home Office Medical/Pharmacy', oWQS.Security_Group__c);
            Test.stopTest();
        }    
    }
    
   /**
    * Test method for edit.
    *<p>
    * @param            NULL
    * @return           void
    */
    static testMethod void testMethodEdit3()
    {
        User oUser = [Select Id , Name from User where Username= 'WQUser1WQUser1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
        Work_Queue_Setup__c  oWQS =  [Select Id , Name, Service_Center__c, Department__c, Classification_Type__c, OwnerId, Security_Group__c, General__c, HO_Medical__c, HO_Dental__c  from Work_Queue_Setup__c where Name = 'Medical Calls' and Security_Group__c = 'Humana General' Limit 1]; 
        list<Group> queueGrpRecord = [Select Id, Name, type from Group where createdById =: oUser.Id and type ='Queue' and Name = 'Medical Calls'];
        
        system.runas(oUser)
        {
            HUMTestDataHelperTest.getHUMConstantsData();
            
            apexpages.currentpage().getparameters().put('id' , oWQS.id);
            ApexPages.StandardController sc = new ApexPages.StandardController(oWQS);
            WorkQueueCreation_C_HUM testWQS = new  WorkQueueCreation_C_HUM(sc);
            testWQS.bEditBoolean = true;    
            Test.startTest();
            system.assertEquals('Humana General', oWQS.Security_Group__c);
            Test.stopTest();
        }    
    }
    /**
    * Test method for edit.
    *<p>
    * @param            NULL
    * @return           void
    */
  
    static testMethod void testMethodEdit4()
    {
        User oUser = [Select Id , Name from User where Username= 'WQUser1WQUser1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
        list<Group> queueGrpRecord = [Select Id, Name, type from Group where createdById =: oUser.Id and type ='Queue' and Name = 'Cincit Calls']; 
        list<Group> PGQGrpRecord = [Select Id, Name, type from Group where createdById =: oUser.Id and Name = 'Cincit Calls PGQ'];
        
        
        Work_Queue_Setup__c  oWQS =  [Select Id , Name, Service_Center__c, Department__c,Public_Group_Queue_Id__c, Classification_Type__c, OwnerId, Security_Group__c, General__c, HO_Medical__c, HO_Dental__c  from Work_Queue_Setup__c where Name = 'Cincit Calls' Limit 1];
        
        
        Work_Queue_Topic__c oWQT = [Select id, Name from Work_Queue_Topic__c limit 1];
        
        
        system.runas(oUser)
        {
            HUMTestDataHelperTest.getHUMConstantsData();
            
            apexpages.currentpage().getparameters().put('id' , oWQS.id);
            ApexPages.StandardController sc = new ApexPages.StandardController(oWQS);
            WorkQueueCreation_C_HUM testWQS = new  WorkQueueCreation_C_HUM(sc);          
            Work_Queue_Setup__c wqrss=[select Id,name,(SELECT Id, Name,  Group_Name__c, Public_Group_View_Id__c FROM Queue_Views__r) from Work_Queue_Setup__c where Id =: ApexPages.currentPage().getParameters().get('id')];
            List<Queue_View__C> qvlistsss = wqrss.Queue_Views__r;
            testWQS.bEditBoolean = true;    
            Test.startTest();
            testWQS.securityGroupSelected = 'Humana General';
            testWQS.enableIndicators();
            system.assertEquals(true, oWQS.General__c);
            testWQS.createWorkQueueGroupPGQ();
            system.assertEquals(true, testWQS.bEditBoolean);
            testWQS.createWorkQueue();
            system.assertEquals(true, queueGrpRecord.size() > 0,'A Queue group record should have been created on inserting a queue');
            system.assertEquals('Queue', queueGrpRecord[0].type,'Type of the inserted group should have been  Queue');
            system.assertEquals(queueGrpRecord[0].Id, oWQS.OwnerId, 'The work queue setup record should have a referece to a Queue');
            testWQS.addTopic();           
            system.assertEquals(0, testWQS.topicNames.size());
            testWQS.updatepgv();
            WorkQueueCreation_D_HUM testWQD = new WorkQueueCreation_D_HUM();
            testWQD.getWorkQueueCriteriaValues();
            String ManagerID = oUser.Id;
            testWQD.getUserName(ManagerID);
            
            testWQS.selectedSC = 'Medical';
            testWQS.selectedDept = 'Calls';
            WorkQueueCreation_S_HUM testWQCS = new WorkQueueCreation_S_HUM();
            testWQCS.updateWorkQueueName(testWQS);
            testWQCS.removeSpecialCharacterWQS(testWQS);
            testWQCS.removeSpecialCharacterPGQ(testWQS);
            testWQCS.createFilterPriority(testWQS);
            
            Group grp = new Group(Name = 'Cincit Calls PGQ' ,  developerName = 'Cincit_Calls_PGQ' , Type='Regular');
            insert grp;
            Work_Queue_Setup__c  oWQSP =  [Select Id , Name, Service_Center__c, Department__c,Public_Group_Queue_Id__c, Classification_Type__c, OwnerId, Security_Group__c, General__c, HO_Medical__c, HO_Dental__c  from Work_Queue_Setup__c where Name = 'Cincit Calls' Limit 1];
            oWQSP.Public_Group_Queue_Id__c = grp.Id;
            update oWQSP;
            testWQS.workQueueSetup.Public_Group_Name__c = 'Medical Calls PGQ';
            testWQS.workQueueSetup.name = 'Medical Calls';
            testWQCS.updateWorkQueueGroupPGQ(testWQS);
            testWQCS.createWQ(testWQS);
            testWQCS.populateWorkQueueSetupDetailsEdit(testWQS);
            String sSecurity = 'Humana General';
            Id publicgrpId = grp.Id;
            WorkQueueCreation_S_HUM.createGroupMembersPGQSecurity(sSecurity,publicgrpId);
            WorkQueueCreation_S_HUM.createGroupMembersPGQ(oWQSP.id, publicgrpId);           
            testWQCS.callMethod(testWQS);
            
            Test.stopTest();
        }    
    }
   /**
    * Test method for create.
    *<p>
    * @param            NULL
    * @return           void
    */
    static testMethod void testMethodCreate()
    {
        User oUser = [Select Id , Name from User where Username= 'WQUser1WQUser1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
        Classification_Type__c clt = [Select Id, Name from Classification_Type__c Limit 1];
        Work_Queue_Setup__c oWQS = new Work_Queue_Setup__c();
        Case cs = new Case ();
        system.runas(oUser)
        {
            HUMTestDataHelperTest.getHUMConstantsData();
            ApexPages.StandardController sc = new ApexPages.StandardController(oWQS);
            WorkQueueCreation_C_HUM testWQS = new  WorkQueueCreation_C_HUM(sc);
            testWQS.bEditBoolean = false;
            testWQS.selectedSC = 'Cin';
            testWQS.selectedDept = 'Calls';
            testWQS.workQueueSetup.Name = 'Cin Calls';
            testWQS.publicGroupRecord.Name = 'Cin Calls PGQ';
            testWQS.securityGroupSelected = 'Humana General';
            oWQS.General__c = true;
            oWQS.Classification_Type__c = clt.Id;
            testWQS.workQueueSetup.TenantID__c = '00';
            testWQS.topicNames = new List<String>{'Test Topic'};                       
            testWQS.queueView.Name = 'Test Queue View';
            Test.startTest();
            system.assertEquals('Humana General', testWQS.securityGroupSelected);
            testWQS.enableIndicators();
            system.assertEquals(true, testWQS.enableGeneralIndicator);
            testWQS.addTopic();
            system.assertEquals(true, testWQS.topicNames.size()>0);
            testWQS.removeTopicFromList();
            system.assertEquals(1, testWQS.topicNames.size());
            testWQS.createWorkQueueGroupPGQ();
            system.assertEquals(true, testWQS.publicGroupCreationError);
            testWQS.addQueueView();
            system.assertEquals(0, testWQS.uniqueQV.size());
            testWQS.createWorkQueue();
            List <Work_Queue_Setup__c> lstoWQS =  [Select Id , Name, Service_Center__c, Department__c, Classification_Type__c, OwnerId, Public_Group_Queue_Id__c  from Work_Queue_Setup__c where Name = 'Cin Calls' Limit 1]; 
            system.assertEquals(0, lstoWQS.size());
            Test.stopTest();
        }    
    }
          
   /**
    * Test method for exception in create.
    *<p>
    * @param            NULL
    * @return           void
    */
    static testMethod void testMethodCreate1()
    {
        User oUser = [Select Id , Name from User where Username= 'WQUser1WQUser1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
        Classification_Type__c clt = [Select Id, Name from Classification_Type__c Limit 1];
        Work_Queue_Setup__c oWQS = new Work_Queue_Setup__c();
        Case cs = new Case ();
        system.runas(oUser)
        {
            HUMTestDataHelperTest.getHUMConstantsData();
            ApexPages.StandardController sc = new ApexPages.StandardController(oWQS);
            WorkQueueCreation_C_HUM testWQS = new  WorkQueueCreation_C_HUM(sc);
            testWQS.bEditBoolean = false;
            testWQS.selectedSC = 'Cins';
            testWQS.selectedDept = 'Add New';
            testWQS.newDepartmentValue = 'Calls';
            testWQS.workQueueSetup.Name = 'Cins Calls';
            testWQS.publicGroupRecord.Name = 'Cins Calls PGQ';
            testWQS.securityGroupSelected = 'Humana General';
            oWQS.General__c = true;
            oWQS.Classification_Type__c = clt.Id;
            testWQS.workQueueSetup.TenantID__c = '00';
            testWQS.workQueueTopic.Name = 'Add topic';
            
            Test.startTest();
            system.assertEquals('Humana General', testWQS.securityGroupSelected);
            testWQS.enableIndicators();
            system.assertEquals(true, testWQS.enableGeneralIndicator);
            testWQS.updateWorkQueueName();
            system.assertEquals('Cins Calls PGQ', testWQS.publicGroupRecord.Name);
            testWQS.createWorkQueueGroupPGQ();
            system.assertEquals(true, testWQS.publicGroupCreationError);
            testWQS.createWorkQueue();
            testWQS.addTopic();
            testWQS.uniqueTopic.add('Add topic');
            testWQS.addTopic();
            List <Work_Queue_Setup__c> lstoWQS =  [Select Id , Name, Service_Center__c, Department__c, Classification_Type__c, OwnerId, Public_Group_Queue_Id__c  from Work_Queue_Setup__c where Name = 'Cin Calls' Limit 1]; 
            system.assertEquals(0, lstoWQS.size());
            Test.stopTest();
        }    
    }
    
   /**
    * Test method for exception in create.
    *<p>
    * @param            NULL
    * @return           void
    */
    static testMethod void testMethodCreate2()
    {
        User oUser = [Select Id , Name from User where Username= 'WQUser1WQUser1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
        Classification_Type__c clt = [Select Id, Name from Classification_Type__c Limit 1];
        Work_Queue_Setup__c oWQS = new Work_Queue_Setup__c();
        Case cs = new Case ();
        system.runas(oUser)
        {
            HUMTestDataHelperTest.getHUMConstantsData();
            ApexPages.StandardController sc = new ApexPages.StandardController(oWQS);
            WorkQueueCreation_C_HUM testWQS = new  WorkQueueCreation_C_HUM(sc);
            testWQS.bEditBoolean = false;
            testWQS.selectedSC = 'Add New';
            testWQS.newServiceCenterValue = 'Cin';
            testWQS.selectedDept = 'Add New';
            testWQS.newDepartmentValue = 'Calls';
            testWQS.workQueueSetup.Name = 'Cin Calls';
            testWQS.publicGroupRecord.Name = 'Cin Calls PGQ';
            testWQS.securityGroupSelected = 'Humana General';
            oWQS.General__c = true;
            oWQS.Classification_Type__c = clt.Id;
            testWQS.workQueueSetup.TenantID__c = '00';
            
            Test.startTest();
            system.assertEquals('Humana General', testWQS.securityGroupSelected);
            testWQS.enableIndicators();
            system.assertEquals(true, testWQS.enableGeneralIndicator);
            testWQS.updateWorkQueueName();
            system.assertEquals('Cin Calls PGQ', testWQS.publicGroupRecord.Name);
            Test.stopTest();
        }    
    }
  
   /**
    * Test method for exception in create.
    *<p>
    * @param            NULL
    * @return           void
    */
    static testMethod void testMethodCreate4()
    {
        User oUser = [Select Id , Name from User where Username= 'WQUser1WQUser1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
        List<Work_Queue_Setup__c> lstWQS =  [Select Id , Name, Service_Center__c, Department__c, Classification_Type__c, OwnerId, Security_Group__c, General__c, HO_Medical__c, HO_Dental__c  from Work_Queue_Setup__c];
        Classification_Type__c clt = [Select Id, Name from Classification_Type__c Limit 1];
        Work_Queue_Setup__c oWQS = new Work_Queue_Setup__c();
        Case cs = new Case ();
        system.runas(oUser)
        {
            HUMTestDataHelperTest.getHUMConstantsData();
            ApexPages.StandardController sc = new ApexPages.StandardController(oWQS);
            WorkQueueCreation_C_HUM testWQS = new  WorkQueueCreation_C_HUM(sc);
            testWQS.bEditBoolean = false;
            testWQS.selectedSC = 'Cincit';
            testWQS.selectedDept = 'Calls';
            testWQS.workQueueSetup.Name = 'Cincit Call';
            testWQS.publicGroupRecord.Name = 'Cin Calls PGQ';
            oWQS.Classification_Type__c = clt.Id;
            testWQS.securityGroupSelected = 'Humana General';
            oWQS.General__c = true;
            testWQS.workQueueSetup.TenantID__c = '00';
            testWQS.topicNames = new List<String>{'Test Topic'};
            
            
            testWQS.queueView.Name = 'Test Queue View';
            Test.startTest();
            system.assertEquals('Humana General', testWQS.securityGroupSelected);
            testWQS.enableIndicators();
            system.assertEquals(true, testWQS.enableGeneralIndicator);
            testWQS.createWorkQueueGroupPGQ();
            system.assertEquals(true, testWQS.publicGroupCreationError);
            testWQS.createWorkQueue();
            List <Work_Queue_Setup__c> lstoWQS =  [Select Id , Name, Service_Center__c, Department__c, Classification_Type__c, OwnerId, Public_Group_Queue_Id__c  from Work_Queue_Setup__c where Name = 'Cin Calls' Limit 1]; 
            system.assertEquals(0, lstoWQS.size());
            Test.stopTest();
        }    
    }
    
   /**
    * Test method for exception in create.
    *<p>
    * @param            NULL
    * @return           void
    */
    static testMethod void testMethodCreate5()
    {
        User oUser = [Select Id , Name from User where Username= 'WQUser1WQUser1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
        List<Work_Queue_Setup__c> lstWQS =  [Select Id , Name, Service_Center__c, Department__c, Classification_Type__c, OwnerId, Security_Group__c, General__c, HO_Medical__c, HO_Dental__c  from Work_Queue_Setup__c];
        Classification_Type__c clt = [Select Id, Name from Classification_Type__c Limit 1];
        Work_Queue_Setup__c oWQS = new Work_Queue_Setup__c();
        Case cs = new Case ();
        system.runas(oUser)
        {
            HUMTestDataHelperTest.getHUMConstantsData();
            ApexPages.StandardController sc = new ApexPages.StandardController(oWQS);
            WorkQueueCreation_C_HUM testWQS = new  WorkQueueCreation_C_HUM(sc);
            testWQS.bEditBoolean = false;
            testWQS.selectedSC = 'Service';
            testWQS.selectedDept = 'Department';
            testWQS.workQueueSetup.Name = 'Cincit Call';
            testWQS.publicGroupRecord.Name = 'Cin Calls PGQ';
            oWQS.Classification_Type__c = clt.Id;
            testWQS.securityGroupSelected = 'Humana General';
            oWQS.General__c = true;
            testWQS.workQueueSetup.TenantID__c = '00';
            testWQS.topicNames = new List<String>{'Test Topic'};
            testWQS.updateQueueView();
            testWQS.queueView.Name = 'Test Queue View';
            Test.startTest();
            system.assertEquals('Humana General', testWQS.securityGroupSelected);
            testWQS.enableIndicators();
            system.assertEquals(true, testWQS.enableGeneralIndicator);
            testWQS.createWorkQueueGroupPGQ();
            system.assertEquals(true, testWQS.publicGroupCreationError);
            testWQS.createWorkQueue();
            List <Work_Queue_Setup__c> lstoWQS =  [Select Id , Name, Service_Center__c, Department__c, Classification_Type__c, OwnerId, Public_Group_Queue_Id__c  from Work_Queue_Setup__c where Name = 'Cin Calls' Limit 1]; 
            system.assertEquals(0, lstoWQS.size());
            Test.stopTest();
        }    
    }
    
   /**
    * Test method for exception in create.
    *<p>
    * @param            NULL
    * @return           void
    */
    static testMethod void testMethodCreate6()
    {
        User oUser = [Select Id , Name from User where Username= 'WQUser1WQUser1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
        List<Work_Queue_Setup__c> lstWQS =  [Select Id , Name, Service_Center__c, Department__c, Classification_Type__c, OwnerId, Security_Group__c, General__c, HO_Medical__c, HO_Dental__c  from Work_Queue_Setup__c];
        Classification_Type__c clt = [Select Id, Name from Classification_Type__c Limit 1];
        Work_Queue_Setup__c oWQS = new Work_Queue_Setup__c();
        Case cs = new Case ();
        system.runas(oUser)
        {
            HUMTestDataHelperTest.getHUMConstantsData();
            ApexPages.StandardController sc = new ApexPages.StandardController(oWQS);
            WorkQueueCreation_C_HUM testWQS = new  WorkQueueCreation_C_HUM(sc);
            testWQS.bEditBoolean = false;
            testWQS.selectedSC = 'Service';
            testWQS.selectedDept = 'Department';
            testWQS.workQueueSetup.Name = '';
            testWQS.publicGroupRecord.Name = 'Cin Calls PGQ';
            oWQS.Classification_Type__c = clt.Id;
            testWQS.securityGroupSelected = 'Humana General';
            oWQS.General__c = true;
            testWQS.workQueueSetup.TenantID__c = '00';
            testWQS.topicNames = new List<String>{'Test Topic'};
            
            testWQS.queueView.Name = 'Test Queue View';
            Test.startTest();
            system.assertEquals('Humana General', testWQS.securityGroupSelected);
            testWQS.enableIndicators();
            system.assertEquals(true, testWQS.enableGeneralIndicator);
            testWQS.createWorkQueueGroupPGQ();
            system.assertEquals(true, testWQS.publicGroupCreationError);
            testWQS.createWorkQueue();
            List <Work_Queue_Setup__c> lstoWQS =  [Select Id , Name, Service_Center__c, Department__c, Classification_Type__c, OwnerId, Public_Group_Queue_Id__c  from Work_Queue_Setup__c where Name = 'Cin Calls' Limit 1]; 
            system.assertEquals(0, lstoWQS.size());
            Test.stopTest();
        }    
    }
    
   /**
    * Test method for exception in create.
    *<p>
    * @param            NULL
    * @return           void
    */
    static testMethod void testMethodCreate7()
    {
        User oUser = [Select Id , Name from User where Username= 'WQUser1WQUser1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
        List<Work_Queue_Setup__c> lstWQS =  [Select Id , Name, Service_Center__c, Department__c, Classification_Type__c, OwnerId, Security_Group__c, General__c, HO_Medical__c, HO_Dental__c  from Work_Queue_Setup__c];
        Classification_Type__c clt = [Select Id, Name from Classification_Type__c Limit 1];
        Work_Queue_Setup__c oWQS = new Work_Queue_Setup__c();
        Case cs = new Case ();
        system.runas(oUser)
        {
            HUMTestDataHelperTest.getHUMConstantsData();
            ApexPages.StandardController sc = new ApexPages.StandardController(oWQS);
            WorkQueueCreation_C_HUM testWQS = new  WorkQueueCreation_C_HUM(sc);
            testWQS.bEditBoolean = false;
            testWQS.selectedSC = 'Cincit';
            testWQS.selectedDept = 'Callssssssss';
            testWQS.workQueueSetup.Name = null;
            testWQS.publicGroupRecord.Name = 'Cin Calls PGQ';
            oWQS.Classification_Type__c = clt.Id;
            testWQS.securityGroupSelected = 'Humana General';
            oWQS.General__c = true;
            testWQS.workQueueSetup.TenantID__c = '00';
            testWQS.topicNames = new List<String>{'Test Topic'};
            
            testWQS.queueView.Name = 'Test Queue View';
            Test.startTest();
            system.assertEquals('Humana General', testWQS.securityGroupSelected);
            testWQS.enableIndicators();
            system.assertEquals(true, testWQS.enableGeneralIndicator);
            testWQS.createWorkQueueGroupPGQ();
            system.assertEquals(true, testWQS.publicGroupCreationError);
            testWQS.createWorkQueue();
            List <Work_Queue_Setup__c> lstoWQS =  [Select Id , Name, Service_Center__c, Department__c, Classification_Type__c, OwnerId, Public_Group_Queue_Id__c  from Work_Queue_Setup__c where Name = 'Cin Calls' Limit 1]; 
            system.assertEquals(0, lstoWQS.size());
            Test.stopTest();
        }    
    }
    
   /**
    * Test method for exception in create.
    *<p>
    * @param            NULL
    * @return           void
    */
    static testMethod void testMethodCreate8()
    {
        User oUser = [Select Id , Name from User where Username= 'WQUser1WQUser1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
        List<Work_Queue_Setup__c> lstWQS =  [Select Id , Name, Service_Center__c, Department__c, Classification_Type__c, OwnerId, Security_Group__c, General__c, HO_Medical__c, HO_Dental__c  from Work_Queue_Setup__c];
        Classification_Type__c clt = [Select Id, Name from Classification_Type__c Limit 1];
        Work_Queue_Setup__c oWQS = new Work_Queue_Setup__c();
        Case cs = new Case ();
        system.runas(oUser)
        {
            HUMTestDataHelperTest.getHUMConstantsData();
            ApexPages.StandardController sc = new ApexPages.StandardController(oWQS);
            WorkQueueCreation_C_HUM testWQS = new  WorkQueueCreation_C_HUM(sc);
            testWQS.bEditBoolean = false;
            testWQS.selectedSC = 'Cincit';
            testWQS.selectedDept = 'Cals';
            testWQS.workQueueSetup.Name = 'Cincits Calls';
            testWQS.publicGroupRecord.Name = null;
            oWQS.Classification_Type__c = clt.Id;
            testWQS.securityGroupSelected = 'Humana General';
            oWQS.General__c = true;
            testWQS.workQueueSetup.TenantID__c = '00';
            testWQS.topicNames = new List<String>{'Test Topic'};
            
            testWQS.queueView.Name = 'Test Queue View';
            Test.startTest();
            testWQS.enableIndicators();
            system.assertEquals(true, testWQS.enableGeneralIndicator);
            testWQS.createWorkQueueGroupPGQ();
            system.assertEquals(true, testWQS.publicGroupCreationError);
            testWQS.createWorkQueue();
            List <Work_Queue_Setup__c> lstoWQS =  [Select Id , Name, Service_Center__c, Department__c, Classification_Type__c, OwnerId, Public_Group_Queue_Id__c  from Work_Queue_Setup__c where Name = 'Cin Calls' Limit 1]; 
            system.assertEquals(0, lstoWQS.size());
            Test.stopTest();
        }    
    }
    
   /**
    * Test method for exception in create.
    *<p>
    * @param            NULL
    * @return           void
    */
    static testMethod void testMethodCreate9()
    {
        User oUser = [Select Id , Name from User where Username= 'WQUser1WQUser1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
        List<Work_Queue_Setup__c> lstWQS =  [Select Id , Name, Service_Center__c, Department__c, Classification_Type__c, OwnerId, Security_Group__c, General__c, HO_Medical__c, HO_Dental__c  from Work_Queue_Setup__c];
        Classification_Type__c clt = [Select Id, Name from Classification_Type__c Limit 1];
        Work_Queue_Setup__c oWQS = new Work_Queue_Setup__c();
        Case cs = new Case ();
        system.runas(oUser)
        {
            HUMTestDataHelperTest.getHUMConstantsData();
            ApexPages.StandardController sc = new ApexPages.StandardController(oWQS);
            WorkQueueCreation_C_HUM testWQS = new  WorkQueueCreation_C_HUM(sc);
            testWQS.bEditBoolean = false;
            testWQS.selectedSC = '';
            testWQS.selectedDept = '';
            testWQS.workQueueSetup.Name = 'Cincit Cdalls';
            testWQS.publicGroupRecord.Name = 'Cincit Cawlls PGQ';
            oWQS.Classification_Type__c = clt.Id;
            testWQS.securityGroupSelected = 'Humana General';
            oWQS.General__c = true;
            testWQS.workQueueSetup.TenantID__c = '00';
            testWQS.topicNames = new List<String>{'Test Topic'};
            
            testWQS.queueView.Name = 'Test Queue View';
            Test.startTest();
            testWQS.enableIndicators();
            system.assertEquals(true, testWQS.enableGeneralIndicator);
            testWQS.createWorkQueueGroupPGQ();
            system.assertEquals(true, testWQS.publicGroupCreationError);
            testWQS.createWorkQueue();
            List <Work_Queue_Setup__c> lstoWQS =  [Select Id , Name, Service_Center__c, Department__c, Classification_Type__c, OwnerId, Public_Group_Queue_Id__c  from Work_Queue_Setup__c where Name = 'Cin Calls' Limit 1]; 
            system.assertEquals(0, lstoWQS.size());
            Test.stopTest();
        }    
    }
    
   /**
    * Test method for exception in create.
    *<p>
    * @param            NULL
    * @return           void
    */
    static testMethod void testMethodCreate10()
    {
        User oUser = [Select Id , Name from User where Username= 'WQUser1WQUser1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
        List<Work_Queue_Setup__c> lstWQS =  [Select Id , Name, Service_Center__c, Department__c, Classification_Type__c, OwnerId, Security_Group__c, General__c, HO_Medical__c, HO_Dental__c  from Work_Queue_Setup__c];
        Classification_Type__c clt = [Select Id, Name from Classification_Type__c Limit 1];
        Work_Queue_Setup__c oWQS = new Work_Queue_Setup__c();
        Case cs = new Case ();
        system.runas(oUser)
        {
            HUMTestDataHelperTest.getHUMConstantsData();
            ApexPages.StandardController sc = new ApexPages.StandardController(oWQS);
            WorkQueueCreation_C_HUM testWQS = new  WorkQueueCreation_C_HUM(sc);
            testWQS.bEditBoolean = false;
            testWQS.selectedSC = 'Cincit';
            testWQS.selectedDept = 'Callds';
            testWQS.workQueueSetup.Name = 'Cincit Calsssw';
            testWQS.publicGroupRecord.Name = 'Cincit Callds PGQ';
            oWQS.Classification_Type__c = clt.Id;
            testWQS.workQueueSetup.TenantID__c = '00';
            testWQS.securityGroupSelected = 'None';
            testWQS.topicNames = new List<String>{'Test Topic'};
            
            testWQS.queueView.Name = 'Test Queue View';
            Test.startTest();
            testWQS.enableIndicators();
            system.assertEquals(false, testWQS.enableGeneralIndicator);
            testWQS.createWorkQueueGroupPGQ();
            system.assertEquals(true, testWQS.publicGroupCreationError);
            testWQS.createWorkQueue();
            List <Work_Queue_Setup__c> lstoWQS =  [Select Id , Name, Service_Center__c, Department__c, Classification_Type__c, OwnerId, Public_Group_Queue_Id__c  from Work_Queue_Setup__c where Name = 'Cin Calls' Limit 1]; 
            system.assertEquals(0, lstoWQS.size());
            Test.stopTest();
        }    
    }
    
   /**
    * Test method for exception in create.
    *<p>
    * @param            NULL
    * @return           void
    */
    static testMethod void testMethodCreate11()
    {
        User oUser = [Select Id , Name from User where Username= 'WQUser1WQUser1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
        List<Work_Queue_Setup__c> lstWQS =  [Select Id , Name, Service_Center__c, Department__c, Classification_Type__c, OwnerId, Security_Group__c, General__c, HO_Medical__c, HO_Dental__c  from Work_Queue_Setup__c];
        Classification_Type__c clt = [Select Id, Name from Classification_Type__c Limit 1];
        Work_Queue_Setup__c oWQS = new Work_Queue_Setup__c();
        Case cs = new Case ();
        system.runas(oUser)
        {
            HUMTestDataHelperTest.getHUMConstantsData();
            ApexPages.StandardController sc = new ApexPages.StandardController(oWQS);
            WorkQueueCreation_C_HUM testWQS = new  WorkQueueCreation_C_HUM(sc);
            testWQS.bEditBoolean = false;
            testWQS.selectedSC = 'Cincit';
            testWQS.selectedDept = 'Camns';
            testWQS.workQueueSetup.Name = 'Cincit Callmns';
            testWQS.publicGroupRecord.Name = 'Cincit Calls PGQ';
            oWQS.Classification_Type__c = clt.Id;
            testWQS.securityGroupSelected = 'Humana General';
            testWQS.workQueueSetup.TenantID__c = '00';
            testWQS.topicNames = new List<String>{'Test Topic'};
            
            testWQS.queueView.Name = 'Test Queue View';
            Test.startTest();
            testWQS.enableIndicators();
            system.assertEquals(true, testWQS.enableGeneralIndicator);
            testWQS.createWorkQueueGroupPGQ();
            system.assertEquals(true, testWQS.publicGroupCreationError);
            testWQS.createWorkQueue();
            List <Work_Queue_Setup__c> lstoWQS =  [Select Id , Name, Service_Center__c, Department__c, Classification_Type__c, OwnerId, Public_Group_Queue_Id__c  from Work_Queue_Setup__c where Name = 'Cin Calls' Limit 1]; 
            system.assertEquals(0, lstoWQS.size());
            Test.stopTest();
        }    
    }
    
   /**
    * Test method for exception in create.
    *<p>
    * @param            NULL
    * @return           void
    */
    static testMethod void testMethodCreate12()
    {
        User oUser = [Select Id , Name from User where Username= 'WQUser1WQUser1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
        Work_Queue_Setup__c oWQS = new Work_Queue_Setup__c();
        Classification_Type__c clt = [Select Id, Name from Classification_Type__c Limit 1];
        Work_Queue_Priority__c oWQP = new Work_Queue_Priority__c();
        Case cs = new Case ();
        system.runas(oUser)
        {
            HUMTestDataHelperTest.getHUMConstantsData();
            ApexPages.StandardController sc = new ApexPages.StandardController(oWQS);
            WorkQueueCreation_C_HUM testWQS = new  WorkQueueCreation_C_HUM(sc);
            testWQS.bEditBoolean = false;
            testWQS.selectedSC = 'Cin';
            testWQS.selectedDept = 'Calls';
            testWQS.workQueueSetup.Name = 'Cin Calls';
            testWQS.publicGroupRecord.Name = 'Cin Calls PGQ';
            oWQS.Classification_Type__c = clt.Id;
            testWQS.securityGroupSelected = 'Humana General';
            oWQS.General__c = true;
            testWQS.workQueueSetup.TenantID__c = '00';
            testWQS.topicNames = new List<String>{'Test Topic'};          
            testWQS.wqFP.priority_7__c = '666';
            testWQS.queueView.Name = 'Test Queue View';
            Test.startTest();
            testWQS.enableIndicators();
            system.assertEquals(true, testWQS.enableGeneralIndicator);
            testWQS.createWorkQueueGroupPGQ();
            system.assertEquals(true, testWQS.publicGroupCreationError);
            testWQS.createWorkQueue();
            List <Work_Queue_Setup__c> lstoWQS =  [Select Id , Name, Service_Center__c, Department__c, Classification_Type__c, OwnerId, Public_Group_Queue_Id__c  from Work_Queue_Setup__c where Name = 'Cin Calls' Limit 1]; 
            system.assertEquals(0, lstoWQS.size());
            Test.stopTest();
        }    
    }
        
   /**
    * Test method for exception in create.
    *<p>
    * @param            NULL
    * @return           void
    */
    static testMethod void testMethodCreate13()
    {
        User oUser = [Select Id , Name from User where Username= 'WQUser1WQUser1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
        Work_Queue_Setup__c oWQS = new Work_Queue_Setup__c();
        Case cs = new Case ();
        system.runas(oUser)
        {
            HUMTestDataHelperTest.getHUMConstantsData();
            ApexPages.StandardController sc = new ApexPages.StandardController(oWQS);
            WorkQueueCreation_C_HUM testWQS = new  WorkQueueCreation_C_HUM(sc);
            testWQS.bEditBoolean = false;
            testWQS.selectedSC = 'Add New';
            testWQS.selectedDept = 'Calls';
            testWQS.newServiceCenterValue = 'New';
            testWQS.securityGroupSelected = 'Humana General';
            Test.startTest();
            testWQS.enableIndicators();
            system.assertEquals(true, testWQS.enableGeneralIndicator);
            testWQS.updateWorkQueueName();
            system.assertEquals('New Calls PGQ', testWQS.publicGroupRecord.Name);
            
            List <Work_Queue_Setup__c> lstoWQS =  [Select Id , Name, Service_Center__c, Department__c, Classification_Type__c, OwnerId, Public_Group_Queue_Id__c, TenantID__c  from Work_Queue_Setup__c where Name = 'Cin Calls' Limit 1]; 
            system.assertEquals(0, lstoWQS.size());
            
            Test.stopTest();
        }
    }
    
    /**
    * Test method for Updtae workQueuePriority.
    *<p>
    * @param            NULL
    * @return           void
    */
    static testMethod void testMethodCreate14()
    {
      User oUser = [Select Id , Name from User where Username= 'WQUser1WQUser1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
        Work_Queue_Setup__c oWQS = new Work_Queue_Setup__c();
        Case cs = new Case ();
        system.runas(oUser)
        {
            test.startTest();
            HUMTestDataHelperTest.getHUMConstantsData();
            ApexPages.StandardController sc = new ApexPages.StandardController(oWQS);
            Work_Queue_Setup__c workqueuename = [select name from Work_Queue_Setup__c where name = 'Cincit Calls' limit 1];
             Work_Queue_Setup__c workqueuename1 = [select name from Work_Queue_Setup__c where name = 'Medical Call' limit 1];
            Work_Queue_Priority__c workpriority = new Work_Queue_Priority__c();
            workpriority.Work_Queue_Name__c = workqueuename.id;
            insert workpriority;
            
            workpriority.Priority_10__c = 'test';
            update workpriority;
            WorkQueueCreation_D_HUM objWQ= new WorkQueueCreation_D_HUM();
            boolean bpriority =objWQ.upsertWQPriorityObject(workpriority);

            WorkQueueCreation_C_HUM testWQS1 = new  WorkQueueCreation_C_HUM(sc);
            testWQS1.updateWorkQueueGroupPGQ();
            testWQS1.queViewEditFlag();
            testWQS1.deletePreviouslyCreatedGroupObjects();
            testWQS1.updateServiceCenterAndDepartment();
            test.stopTest();
            
        }
    }
    static testMethod void testMethodCreateCC()
    {
        User oUser = [Select Id , Name from User where Username= 'WQUser1WQUser1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
        Classification_Type__c clt = [Select Id, Name from Classification_Type__c Limit 1];
        Work_Queue_Setup__c oWQS = new Work_Queue_Setup__c();
        Case cs = new Case ();
        system.runas(oUser)
        {
            HUMTestDataHelperTest.getHUMConstantsData();
            ApexPages.StandardController sc = new ApexPages.StandardController(oWQS);
            WorkQueueCreation_C_HUM testWQS = new  WorkQueueCreation_C_HUM(sc);
            testWQS.bEditBoolean = false;
            testWQS.selectedSC = 'Cin';
            testWQS.selectedDept = 'Calls';
            testWQS.workQueueSetup.Name = 'Cin Calls';
            testWQS.publicGroupRecord.Name = 'Cin Calls PGQ';
            testWQS.securityGroupSelected = 'Humana General';
            oWQS.General__c = true;
            oWQS.Classification_Type__c = clt.Id;
            testWQS.workQueueSetup.TenantID__c = '00';
            testWQS.topicNames = new List<String>{'Test Topic'};  
            testWQS.queueView.Name = 'Test Queue View';
            testWQS.sPGVName = 'test queue PGV Name length equals thirty six';
            Test.startTest();
            system.assertEquals('Humana General', testWQS.securityGroupSelected);
            testWQS.enableIndicators();
            system.assertEquals(true, testWQS.enableGeneralIndicator);
            testWQS.addTopic();
            system.assertEquals(true, testWQS.topicNames.size()>0);
            testWQS.removeTopicFromList();
            system.assertEquals(1, testWQS.topicNames.size());
            testWQS.createWorkQueueGroupPGQ();
            system.assertEquals(true, testWQS.publicGroupCreationError);
            testWQS.addQueueView();
            system.assertEquals(0, testWQS.uniqueQV.size());
            testWQS.workQueueSetup.Public_Group_Name__c = 'Medical Calls PGQ';
            testWQS.createWorkQueue();
            List <Work_Queue_Setup__c> lstoWQS =  [Select Id , Name, Service_Center__c, Department__c, Classification_Type__c, OwnerId, Public_Group_Queue_Id__c  from Work_Queue_Setup__c where Name = 'Cin Calls' Limit 1]; 
            Test.stopTest();
        }    
    }
    
    /**
    * Test method for Removetopic.
    *<p>
    * @param            NULL
    * @return           void
    */
    static testMethod void testMethodRemovetopic()
    {
        User oUser = [Select Id , Name from User where Username= 'WQUser1WQUser1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
        Work_Queue_Setup__c oWQS = new Work_Queue_Setup__c();
      	system.runas(oUser)
        {
            HUMTestDataHelperTest.getHUMConstantsData();
            ApexPages.StandardController sc = new ApexPages.StandardController(oWQS);
            WorkQueueCreation_C_HUM testWQS = new  WorkQueueCreation_C_HUM(sc);
            
            Test.startTest();
            testWQS.topicNames = new List<String>{'Test Topic'}; 
            testWQS.removedTopic = 'Test Topic';
            testWQS.securityGroupSelected = 'none';
            testWQS.selectedSC = 'Add New';
            testWQS.newServiceCenterValue = '';
            testWQS.selectedDept = 'Add New';
            testWQS.newDepartmentValue = '';
            testWQS.updateWorkQueueGroupPGQ();
            testWQS.removeTopicFromList();
            system.assertEquals(0, testWQS.topicNames.size());
          
            testWQS.selectedSC = '';
            testWQS.selectedDept = '';
            testWQS.bEditMessageFlag = false;
            testWQS.updateWorkQueueGroupPGQ();
            Test.stopTest();
          
        }    
    }
}