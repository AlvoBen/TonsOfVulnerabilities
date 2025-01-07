/***********************************************************************************************************************************************************************
Apex Class Name :  QueueViewItemCreation_T_HUM
Version: 1.0
Created Date     : Jun 01 2016
Function         : Test class for QueueViewItemCreation_C_HUM Class
************************************************************************************************************************************************************************
Modification Log: 

* Developer Name            Review Number            Date                       Description
------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Sharan Shanumgam              29395             06/01/2016                    Original Version
* Sharan Shanmugam				                  01/23/2017			        Increased code coverage
* Pradeepkumar Dani	            98235	          06/06/2017                    Classification_Type_Old__c Clean up
* Rajesh Chandgothia	        140400	          10/18/2017			        REQ - 332357 increase Test Coverage
* Suresh Sunkari                339349            02/22/2019			        REQ - 377482 Report on Business Segments within the platform - ALOB
* Manikanta                     		          07/11/2019                    REQ - 399177 - Added logic to resolve the queue view item issue (collection size exceeds maximum size of 1,000)
* Vinay Lingegowda                                05/03/2021			        T1PRJ0001827- IR 5050 - Update Queue View - Details, New and Edit page - Add SLA fields (CRM)
* Vamshi Krishna Pemberthi						  06/08/2021 		Code Coverage Improvements
* Prasuna Pattabhi                                09/06/2021                       Code Coverage Improvements
*************************************************************************************************************************************************************************/
@isTest
Public with sharing Class QueueViewItemCreation_T_HUM
{
    
     /**
    * testsetup method to create test data
    *<p>
    * @param            NULL
    * @return           void
    */
    @testSetup 
    Static void createCustomData() 
    {
        User oUser = HUMTestDataHelperTest.createTestUser('QueueViewUser1','System Administrator');
        oUser.Current_Queue__c ='Test';
        insert oUser;
        System.assertEquals(oUser.Current_Queue__c, 'Test');
        Group grp = New Group(Name = 'Test PGQ' ,  developerName = 'test1' , Type='Regular');
        Group oGrp;
        Group grp1 = New Group(Name = 'Test Queue View PGV' ,  developerName = 'testPGV1' , Type='Regular');        
        List < Queue_View_Item__c > lstQVI = [Select id, Name, API_Name__c from Queue_View_Item__c where createdById =: oUser.id];
        System.runas(oUser)
        {
            insert grp;
            System.assertEquals(grp.developerName, 'test1');
            insert grp1;
            System.assertEquals(grp1.developerName, 'testPGV1');
            GroupMember grpMem = HUMTestDataHelperTest.getGrpMems(grp.id , oUser.id);
            insert grpMem ;
            System.assertEquals(grpMem.GroupId, grp.id);

            oGrp = New Group(Name = 'Test', Type = 'QUEUE');
            insert oGrp;
            System.assertEquals(oGrp.Type, 'QUEUE');
            QueuesObject oQueueObject = New QueuesObject (QueueID = oGrp.id, SobjectType ='Work_Queue_Setup__c');
            insert oQueueObject;
            System.assertEquals(oQueueObject.QueueID, oGrp.id);
            
			Classification_Type__c ct = New Classification_Type__c(Name = 'Test', Is_Active__c = true, Guidance_Alert_Limit__c = 2);
            insert ct;
            System.assertEquals(ct.Name, 'Test');

        Work_Queue_Setup__c oWQS = HUMTestDataHelperTest.getWorkQueue();    
            oWQS.Service_Center__c ='Cinci';
            oWQS.Department__c ='Calls';
            oWQS.Classification_Type__c = ct.Id;
            oWQS.OwnerId = oGrp.id;
            oWQS.Public_Group_Queue_Id__c = grp.Id;
            insert oWQS;
            System.assertEquals(oWQS.Public_Group_Queue_Id__c, grp.Id);
            Queue_View__c qw = New Queue_View__c();
            qw.Name = 'Test Queue View';
            qw.Work_Queue_Name__c = oWQS.id;
            qw.Public_Group_View_Id__c = grp1.id;
            insert qw;
            System.assertEquals(qw.Public_Group_View_Id__c, grp1.id);
        }
        
    }
  
    /**
    * Method Is used to test Queue View edit
    *<p>
    * @param            NULL
    * @return           void   
    */
    Static testMethod void testQueueViewEdit()
    {
        User oUser = [Select Id , Name from User where Username= 'QueueViewUser1QueueViewUser1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
        System.runas(oUser)
        {
            Work_Queue_Setup__c wqs = [Select Id from Work_Queue_Setup__c limit 1];
            ApexPages.CurrentPage().getParameters().put('qId',wqs.id);
            QueueViewItemCreation_C_HUM humQW = New QueueViewItemCreation_C_HUM();
            Test.startTest();
            humQW.initialize();
            humQW.queueView.Accountable_Line_of_Business__c = 'GSO';
            humQW.queueViewItem.API_Name__c = 'Topic';
            humQW.queueViewItem.Field_Operator__c = 'equals';
            humQW.queueViewItem.Field_Value__c = 'Test,Top';
            humQW.addQVI();
            System.assertNotEquals(0, humQW.mapWrapper.size());
            System.assert(humQW.lstQViewItemWrapper[0].objQViewItem.API_Name__c == 'Topic');
        humQW.save();
            List<Queue_View_Item__c> lstQVI = [Select id, Name, API_Name__c from Queue_View_Item__c where createdById =: oUser.id];
            Pagereference pgRef = humQW.cancel();
            System.assertequals(String.Valueof(pgRef),String.ValueOf(new PageReference('/'+wqs.id)));
            Test.stopTest();   
        } 
    }
    
    /**
    * Method Is used to test Queue View creation
    *<p>
    * @param            NULL
    * @return           void  
    */
    static testMethod void testQueueViewCreation()
    {
        User oUser = [Select Id , Name from User where Username= 'QueueViewUser1QueueViewUser1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
        System.runas(oUser)
        {
            Work_Queue_Setup__c wqs =  new Work_Queue_Setup__c();
            ApexPages.CurrentPage().getParameters().put('id',wqs.id);
            QueueViewItemCreation_C_HUM humQW = new QueueViewItemCreation_C_HUM();
            Test.startTest();
            humQW.initialize();
            humQW.queueView.Accountable_Line_of_Business__c = 'GSO';
            humQW.queueViewItem.API_Name__c = 'Topic';
            humQW.queueViewItem.Field_Operator__c = 'equals';
            humQW.queueViewItem.Field_Value__c = 'Test';
            humQW.queueView.name = 'Test QV';
            humQW.addQVI();
            system.assertNotEquals(0, humQW.mapWrapper.size());            
            system.assert(humQW.lstQViewItemWrapper[0].objQViewItem.API_Name__c == 'Topic');
            Test.stopTest();   
        } 
    }
    
    /**
    * Method Is used to test Queue View creation
    *<p>
    * @param            NULL
    * @return           void  
    */
    static testMethod void testQueueViewCreation1()
    {
        User oUser = [Select Id , Name from User where Username= 'QueueViewUser1QueueViewUser1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
        System.runas(oUser)
        {
            Work_Queue_Setup__c wqs = [Select Id from Work_Queue_Setup__c limit 1];
            ApexPages.CurrentPage().getParameters().put('qId',wqs.id);
            
            QueueViewItemCreation_C_HUM humQW = new QueueViewItemCreation_C_HUM();
            Test.startTest();
            humQW.initialize();
            humQW.queueView.Accountable_Line_of_Business__c = 'GSO';
            humQW.queueViewItem.API_Name__c = 'Topic';
            humQW.queueViewItem.Field_Operator__c = 'equals';
            humQW.queueViewItem.Field_Value__c = 'Test';
            humQW.addQVI();
            system.assert(humQW.lstQViewItemWrapper[0].objQViewItem.API_Name__c == 'Topic');
            humQW.removeQVI();
            system.assertEquals(0, humQW.mapWrapper.size());
            humQW.save();            
            humQW.updatePGV();
            List<Group> lstGrp = [Select id, Name from Group where createdById =: oUser.id];
            system.assertEquals(3, lstGrp.size());
            Pagereference pgRef = humQW.cancel();
            System.assertequals(String.Valueof(pgRef),String.ValueOf(new PageReference('/'+wqs.id)));
            humQW.queueView.name = '';
            humQW.save();            
            Test.stopTest();
               
        } 
    }
    
    /**
    * Method Is used to test Queue View creation
    *<p>
    * @param            NULL
    * @return           void   
    */
    static testMethod void testQueueViewCreation2()
    {
        User oUser = [Select Id , Name from User where Username= 'QueueViewUser1QueueViewUser1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
        System.runas(oUser)
        {
            Queue_View__c wqs = [Select Id , Work_Queue_Name__c from Queue_View__c limit 1];
            ApexPages.CurrentPage().getParameters().put('id',wqs.id);
            QueueViewItemCreation_C_HUM humQW = new QueueViewItemCreation_C_HUM();
            Test.startTest();
            humQW.initialize();
            humQW.queueView.Accountable_Line_of_Business__c = 'GSO';
            humQW.queueViewItem.API_Name__c = 'Topic';
            humQW.queueViewItem.Field_Operator__c = 'equals';
            humQW.queueViewItem.Field_Value__c = 'Test';
            humQW.addQVI();
            system.assert(humQW.lstQViewItemWrapper[0].objQViewItem.API_Name__c == 'Topic');
            humQW.removeQVI();
            system.assertNotEquals(0, humQW.mapWrapper.size());
            humQW.queueView.name = '';
            humQW.save();
            ApexPages.Message[] messages = ApexPages.getMessages();
            System.assertEquals(1,messages.size());
            Test.stopTest();   
        } 
    }
   
    /**
    * Method Is used to test Queue View creation
    *<p>
    * @param            NULL
    * @return           void   
    */
    static testMethod void testQueueViewCreation3()
    {
        User oUser = [Select Id , Name from User where Username= 'QueueViewUser1QueueViewUser1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
        System.runas(oUser)
        {
            Queue_View__c qw = [Select Id,Name from Queue_View__c limit 1][0];
            test.StartTest();
            ApexPages.CurrentPage().getParameters().put('id',qw.id);
            QueueViewItemCreation_C_HUM humQW = new QueueViewItemCreation_C_HUM();
            humQW.initialize();
            humQW.queueView.Accountable_Line_of_Business__c = 'GSO';
            humQW.queueViewItem.API_Name__c = 'Topic';
            humQW.queueViewItem.Field_Operator__c = 'equals';
            humQW.queueViewItem.Field_Value__c = 'Test';
            humQW.addQVI();
            system.assert(humQW.lstQViewItemWrapper[0].objQViewItem.API_Name__c == 'Topic');
            humQW.removeQVI();
            system.assertNotEquals(0, humQW.mapWrapper.size());
            humQW.save();
            List<Queue_View_Item__c> lstQVI = [Select id, Name, API_Name__c from Queue_View_Item__c where createdById =: oUser.id];
            humQW.sQVType = qw.id;
            Pagereference pgRef = humQW.populatePGV();
            system.assertEquals(false, String.isBlank(humQW.publicGroupRecordName));
            humQW.updatePGV();
            List<Group> lstGrp = [Select id, Name from Group where createdById =: oUser.id];
            system.assertEquals(3, lstGrp.size());
            Test.Stoptest();   
        } 
    }
    
    /**
    * Method Is used to test Queue View creation
    *<p>
    * @param            NULL
    * @return           void   
    */
    static testMethod void testQueueViewCreation4()
    {
        User oUser = [Select Id , Name from User where Username= 'QueueViewUser1QueueViewUser1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
        System.runas(oUser)
        {
            Group oGrp = new Group(Name = 'Test', Type = 'QUEUE');
            insert oGrp;
            System.assert(oGrp.Name == 'Test');
            QueuesObject oQueueObject = new QueuesObject (QueueID = oGrp.id, SobjectType ='Work_Queue_Setup__c');
            insert oQueueObject;
            System.assert(oQueueObject.SobjectType == 'Work_Queue_Setup__c');
            Group grp1 = new Group(Name = 'Test Queue View PGV15' ,  developerName = 'testPGV15' , Type='Regular');
            insert grp1;

			Classification_Type__c ct = new Classification_Type__c(Name = 'Test Creation4', Is_Active__c = true, Guidance_Alert_Limit__c = 2);
			insert ct;
			system.assertEquals(ct.Name, 'Test Creation4');

            Work_Queue_Setup__c oWQS = HUMTestDataHelperTest.getWorkQueue();    
            oWQS.Service_Center__c='Cinci';
            oWQS.Department__c='Calls';
            oWQS.Classification_Type__c = ct.Id;
            oWQS.OwnerId= oGrp.id;
            oWQS.Public_Group_Queue_Id__c = oQueueObject.Id;
            oWQS.SLA_In_Days__c = 1;
            insert oWQS;
            System.assert(oWQS.Classification_Type__c == ct.Id);
            Queue_View__c qw = new Queue_View__c();
            qw.Name = 'Test Queue View';
            qw.Work_Queue_Name__c = oWQS.id;
            qw.Public_Group_View_Id__c = oGrp.id;
            insert qw;
            Test.StartTest();
            ApexPages.CurrentPage().getParameters().put('retURL','/'+oWQS.id);            
            QueueViewItemCreation_C_HUM humQW = new QueueViewItemCreation_C_HUM();
            humQW.initialize();
            humQW.fetchWorkQueueSLADetails();
            humQW.queueView.Accountable_Line_of_Business__c = 'GSO';
            humQW.queueViewItem.API_Name__c = 'Topic';
            humQW.queueViewItem.Field_Operator__c = 'equals';
            humQW.queueViewItem.Field_Value__c = 'Test';
            humQW.addQVI();
            system.assert(humQW.lstQViewItemWrapper.size() == 1);//objQViewItem.API_Name__c == 'Topic');
            humQW.removeQVI();            
            humQW.sQVType = qw.Id;
            Pagereference pgRef = humQW.populatePGV();
            system.assertEquals(false, String.isBlank(humQW.publicGroupRecordName));
            humQW.updatePGV();
            List<Group> lstGrp = [Select id, Name from Group where createdById =: oUser.id];
            system.assertNotEquals(0, lstGrp.size());
            humQW.queueView.name = 'test';
            humQW.queueView.Work_Queue_Name__c = oWQS.ID;
            humQW.bIsEdit = true;
            humQW.queueView.Public_Group_View_Id__c = oGrp.id;
            humQW.save(); 
            List<Queue_View_Item__c> lstQVI1 = [Select id, Name from Queue_View_Item__c where createdById =: oUser.id];
            Test.StopTest();
        } 
    }
   
    /**
    * Method Is used to test Queue View creation
    *<p>
    * @param            NULL
    * @return           void   
    */
    static testMethod void testQueueViewCreation5()
    {
        User oUser = [Select Id , Name from User where Username= 'QueueViewUser1QueueViewUser1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
        System.runas(oUser)
        {
            Group oGrp = new Group(Name = 'Test', Type = 'QUEUE');
            insert oGrp;
            System.assert(oGrp.Name == 'Test');
            QueuesObject oQueueObject = new QueuesObject (QueueID = oGrp.id, SobjectType ='Work_Queue_Setup__c');
            insert oQueueObject;
            System.assert(oQueueObject.SobjectType == 'Work_Queue_Setup__c');
            Group grp1 = new Group(Name = 'Test Queue View PGV15' ,  developerName = 'testPGV15' , Type='Regular');
            insert grp1;

			Classification_Type__c ct = new Classification_Type__c(Name = 'Test Creation5', Is_Active__c = true, Guidance_Alert_Limit__c = 2);
			insert ct;
			system.assertEquals(ct.Name, 'Test Creation5');

            Work_Queue_Setup__c oWQS = HUMTestDataHelperTest.getWorkQueue();    
            oWQS.Service_Center__c='Cinci';
            oWQS.Department__c='Calls';
            oWQS.Classification_Type__c = ct.Id;
            oWQS.OwnerId= oGrp.id;
            oWQS.Public_Group_Queue_Id__c = oQueueObject.Id;
            insert oWQS;
            System.assert(oWQS.Classification_Type__c == ct.Id);
            Queue_View__c qw = new Queue_View__c();
            qw.Name = 'Test Queue View';
            qw.Work_Queue_Name__c = oWQS.id;
            qw.Public_Group_View_Id__c = oGrp.id;
            insert qw;
            Test.StartTest();
            ApexPages.CurrentPage().getParameters().put('retURL','/'+oWQS.id);
            
            QueueViewItemCreation_C_HUM humQW = new QueueViewItemCreation_C_HUM();
            humQW.initialize();
            humQW.sQVType = qw.Id;
            Pagereference pgRef = humQW.populatePGV();
            system.assertEquals(false, String.isBlank(humQW.publicGroupRecordName));
            humQW.updatePGV();
            List<Group> lstGrp = [Select id, Name from Group where createdById =: oUser.id];
            system.assertEquals(5, lstGrp.size());
            ApexPages.Message[] messages = ApexPages.getMessages();
            system.assertNotEquals(0,messages.size());
            Test.Stoptest();   
        } 
    }
    
    /**
    * Method Is used to test Queue View creation
    *<p>
    * @param            NULL
    * @return           void   
    */
    static testMethod void testQueueViewCreation6()
    {
        User oUser = [Select Id , Name from User where Username= 'QueueViewUser1QueueViewUser1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
        System.runas(oUser)
        {
            Work_Queue_Setup__c wqs = [Select Id from Work_Queue_Setup__c limit 1];
            ApexPages.CurrentPage().getParameters().put('qId',wqs.id);
            
            QueueViewItemCreation_C_HUM humQW = new QueueViewItemCreation_C_HUM();
            Test.startTest();
            humQW.initialize();
            humQW.queueView.Accountable_Line_of_Business__c = 'GSO';
            humQW.addQVI();
            humQW.queueView.Name = 'Test';
            Pagereference pgRef = humQW.populatePGV();
            system.assertEquals(false, String.isBlank(humQW.publicGroupRecordName));
            humQW.removeQVI();
            system.assertEquals(0, humQW.mapWrapper.size());
            humQW.updatePGV();
            Pagereference pageRef = humQW.cancel();
            System.assertequals(String.Valueof(pageRef),String.ValueOf(new PageReference('/'+wqs.id)));
            humQW.queueView.name = '';
            humQW.save();
            ApexPages.Message[] messages = ApexPages.getMessages();
            system.assertNotEquals(0,messages.size());
            List<Queue_View_Item__c> lstQVI = [Select id, Name from Queue_View_Item__c where createdById =: oUser.id];
            system.assertEquals(0, lstQVI.size());
            Test.stopTest();
               
        } 
    }
   
    /**
    * Method Is used to test removing Queue View
    *<p>
    * @param            NULL
    * @return           void   
    */
    static testMethod void testRemoveQueueView()
    {
        User oUser = [Select Id , Name from User where Username= 'QueueViewUser1QueueViewUser1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
        System.runas(oUser)
        {
            QueueViewItemCreation_C_HUM humQW = new QueueViewItemCreation_C_HUM();
            Test.startTest();
            humQW.initialize();
            humQW.mapWrapper.clear();
            humQW.removeQVI();  
            system.assertEquals(0, humQW.lstQviewItemWrapper.size());
            Test.stopTest();
        } 
    }  
	
	/**
     * Method Is used to test removing Queue View
    *<p>
    * @param            NULL
    * @return           void   
    */
    static testMethod void testRemoveQueueView1()
    {
        User oUser = [Select Id , Name from User where Username= 'QueueViewUser1QueueViewUser1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
        System.runas(oUser)
        {
            Work_Queue_Setup__c wqs = [Select Id from Work_Queue_Setup__c limit 1];
            ApexPages.CurrentPage().getParameters().put('qId',wqs.id);
            
            QueueViewItemCreation_C_HUM humQW = new QueueViewItemCreation_C_HUM();
            QueueViewItemCreation_S_HUM humQVI = new QueueViewItemCreation_S_HUM();
            Test.startTest();
            humQW.initialize();
            humQW.queueView.Accountable_Line_of_Business__c = 'GSO';
            humQW.queueViewItem.API_Name__c = 'Topic';
            humQW.queueViewItem.Field_Operator__c = 'equals';
            humQW.queueViewItem.Field_Value__c = 'Test';
            humQW.addQVI();
            humQW.queueView.Accountable_Line_of_Business__c = 'Clinical';
            humQW.queueViewItem.API_Name__c = 'Topic';
            humQW.queueViewItem.Field_Operator__c = 'equals';
            humQW.queueViewItem.Field_Value__c = 'Test1';
            humQW.addQVI();
            system.assertEquals(2, humQW.lstQviewItemWrapper.size());
            humQW.iCurrentCounter = 0;
            if(humQW.iCurrentCounter == 0)
            {                
                humQVI.removeQVI(humQW);
            }
            system.assertEquals(1, humQW.lstQviewItemWrapper.size());
            humQW.save();                     
            Test.stopTest();               
        } 
    }
    
    /**
    * Method Is used to test exception in Queue View creation
    *<p>
    * @param            NULL
    * @return           void   
    */
    static testMethod void testQueueViewException()
    {
        User oUser = [Select Id , Name from User where Username= 'QueueViewUser1QueueViewUser1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
        Queue_View__c wqs = [Select Id , Name,Public_Group_View_Id__c, Work_Queue_Name__c,Work_Queue_Name__r.Public_Group_Queue_Id__c from Queue_View__c where Name = 'Test Queue View' limit 1];
        System.runas(oUser)
        {
            ApexPages.CurrentPage().getParameters().put('retURL',wqs.Work_Queue_Name__c);
            System.debug('Work_Queue_Name__c' + wqs.Work_Queue_Name__c);
            ApexPages.CurrentPage().getParameters().put('id',wqs.id);
            QueueViewItemCreation_C_HUM humQW = new QueueViewItemCreation_C_HUM();
            Test.startTest();
            humQW.initialize();
            humQW.bRenderQWI = false;
            humQW.bIsEdit = false;
            humQW.queueView.Accountable_Line_of_Business__c = 'GSO';
            humQW.queueViewItem.API_Name__c = 'Topic';
            humQW.queueViewItem.Field_Operator__c = 'equals';
            humQW.queueViewItem.Field_Value__c = 'Test';
            humQW.addQVI();
            system.assert(humQW.lstQViewItemWrapper[0].objQViewItem.API_Name__c == 'Topic');
            system.assertNotEquals(0, humQW.mapWrapper.size());
            humQW.save();
            ApexPages.Message[] messages = ApexPages.getMessages();
            system.assertNotEquals(0,messages.size());
            QueueViewItemCreation_S_HUM.createGroupMembers(wqs.id);
            Test.stopTest();   
        } 
    }
    static testMethod void testQueueViewEditNegetive()
    {
        User oUser = [Select Id , Name from User where Username='QueueViewUser1QueueViewUser1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
        System.runAs(oUser)
        {

            Work_Queue_Setup__c wqs = [Select Id from Work_Queue_Setup__c limit 1];
            ApexPages.CurrentPage().getParameters().put('qId',wqs.id);
            QueueViewItemCreation_C_HUM humQW = new QueueViewItemCreation_C_HUM();
            Test.startTest();
            humQW.initialize();
            humQW.queueView.Accountable_Line_of_Business__c = 'GSO';
            humQW.queueViewItem.API_Name__c = 'ComplaintType';
            humQW.queueViewItem.Field_Operator__c = 'equals';
            humQW.queueViewItem.Field_Value__c = 'Test,Top';
            
            humQW.addQVI();
            humQW.sWQSId = null;
            humQW.bShowTable = true;
            System.assertNotEquals(0, humQW.mapWrapper.size());
            System.assert(humQW.lstQViewItemWrapper[0].objQViewItem.API_Name__c == 'ComplaintType');
            QueueViewItemCreation_S_HUM oQueueViewItemCreation_S_HUM = new QueueViewItemCreation_S_HUM();
            PageReference pageRef = oQueueViewItemCreation_S_HUM.populatePGV(humQW);
            System.assertEquals(String.Valueof(pageRef),null);
			Test.stopTest();   
        } 
    } 

static testMethod void testDeleteQVI()
    {
        User oUser = [Select Id , Name from User where Username= 'QueueViewUser1QueueViewUser1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
        System.runas(oUser)
        {
            Work_Queue_Setup__c oWQS = [Select Id, SLA_In_Days__c from Work_Queue_Setup__c limit 1];
            oWQS.SLA_In_Days__c = 1;
            update oWQS;
            Test.StartTest();
            ApexPages.CurrentPage().getParameters().put('qId',oWQS.id); 
            ApexPages.CurrentPage().getParameters().put('retURL','/'+oWQS.id);            
            QueueViewItemCreation_C_HUM humQW = new QueueViewItemCreation_C_HUM();
            QueueViewItemCreation_D_HUM humD = new QueueViewItemCreation_D_HUM();
            humQW.initialize();
            humQW.fetchWorkQueueSLADetails();
            humQW.queueView.Accountable_Line_of_Business__c = 'GSO';
            humQW.queueViewItem.API_Name__c = 'Topic';
            humQW.queueViewItem.Field_Operator__c = 'equals';
            humQW.queueViewItem.Field_Value__c = 'Test, Test1';
            humQW.addQVI();
            humD.deleteQVI(humQW.lstDeleteQueueViewItem);
            humQW.save();
            Test.StopTest();
        } 
    }	
    static testMethod void testExceptions()
    {
        User queueViewItemUser13 = [Select Id , Name from User where Username= 'QueueViewUser1QueueViewUser1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
        System.runas(queueViewItemUser13)
        {
            Test.StartTest();                        
            QueueViewItemCreation_C_HUM humQW = new QueueViewItemCreation_C_HUM();
            humQW.mapQV=null;            
            humQW.addQVI();
            Test.StopTest();
        } 
    }
    static testMethod void testQueueViewCreation7()
    {
        User queueViewItemUser14 = [Select Id , Name from User where Username= 'QueueViewUser1QueueViewUser1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
        System.runas(queueViewItemUser14)
        {
            Queue_View__c qw = [Select Id,Name from Queue_View__c limit 1][0];
            test.StartTest();
            ApexPages.CurrentPage().getParameters().put('id',qw.id);
            QueueViewItemCreation_C_HUM humQW = new QueueViewItemCreation_C_HUM();
            humQW.initialize();
            humQW.queueView.Accountable_Line_of_Business__c = 'GSO';
            humQW.queueViewItem.API_Name__c = 'EmployerGroupName';
            humQW.queueViewItem.Field_Operator__c = 'equals';
            humQW.queueViewItem.Field_Value__c = 'Test';
            humQW.addQVI();            
            humQW.save();            
            Test.Stoptest();   
        } 
    }
    /**
    * Method Is used to test Queue View creation
    *<p>
    * @param            NULL
    * @return           void   
    */
    static testMethod void testQueueViewCreation8()
    {
        User queueViewItemUser15 =[Select Id , Name from User where Username= 'QueueViewUser1QueueViewUser1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
        System.runas(queueViewItemUser15)
        {
          	
            Group oGrp = new Group(Name = 'Test31', Type = 'QUEUE');
			insert oGrp;
			System.assert(oGrp.Name == 'Test31');
            QueuesObject oQueueObject = new QueuesObject (QueueID = oGrp.id, SobjectType ='Work_Queue_Setup__c');
			insert oQueueObject;
			System.assert(oQueueObject.SobjectType == 'Work_Queue_Setup__c');
           
			Group grp1 = new Group(Name = 'Test Queue View PGV31' ,  developerName = 'testPGV31' , Type='Regular');
			insert grp1;
            System.assertEquals(grp1.developerName, 'testPGV31');
            
			Classification_Type__c ct = new Classification_Type__c(Name = 'Test Creation4', Is_Active__c = true, Guidance_Alert_Limit__c = 2);
			insert ct;
			system.assertEquals(ct.Name, 'Test Creation4');

			Work_Queue_Setup__c oWQS = HUMTestDataHelperTest.getWorkQueue();    
			oWQS.Service_Center__c='Cinci';
			oWQS.Department__c='Calls';
			oWQS.Classification_Type__c = ct.Id;
			oWQS.OwnerId= oGrp.id;
			oWQS.Public_Group_Queue_Id__c = grp1.Id;
			oWQS.SLA_In_Days__c = 1;
			insert oWQS;
			System.assert(oWQS.Classification_Type__c == ct.Id);
			
			Queue_View__c qw = new Queue_View__c();
            
			qw.Name = 'Test Queue View';
			qw.Work_Queue_Name__c = oWQS.id;
			qw.Public_Group_View_Id__c = grp1.id;
    		insert qw;        
            
            
            Test.StartTest();
            
            ApexPages.CurrentPage().getParameters().put('retURL','/'+oWQS.id);            
            QueueViewItemCreation_C_HUM humQW = new QueueViewItemCreation_C_HUM();
            humQW.initialize();
            humQW.fetchWorkQueueSLADetails();
            humQW.queueView.Accountable_Line_of_Business__c = 'GSO';
            humQW.queueViewItem.API_Name__c = 'Topic';
            humQW.queueViewItem.Field_Operator__c = 'equals';
            humQW.queueViewItem.Field_Value__c = 'Test';
            humQW.addQVI();
            system.assert(humQW.lstQViewItemWrapper.size() == 1);
            humQW.removeQVI();            
            humQW.sQVType = qw.Id;
            Pagereference pgRef = humQW.populatePGV();
            system.assertEquals(false, String.isBlank(humQW.publicGroupRecordName));
            humQW.updatePGV();
            List<Group> lstGrp = [Select id, Name from Group where createdById =: queueViewItemUser15.id];
            system.assertNotEquals(0, lstGrp.size());
            humQW.queueView.Name=qw.Name;
            humQW.bIsEdit = false;
            humQW.queueView.Work_Queue_Name__c = oWQS.id;
            humQW.save(); 
            
            Test.StopTest();
        } 	

    }
}