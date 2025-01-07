/***********************************************************************************************************************************************************************
Apex Class Name :   QueueViewsWithItems_T_HUM
Version: 1.0
Created Date     : Jan 04 2019
Function         : Test class for QueueViewsWithItems_C_HUM Class
************************************************************************************************************************************************************************
Modification Log:  

* Developer Name            Review Number            Date                       Description
------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Pavan Karnam                  329420                  04/01/2019                    Original Version
* Pooja Kumbhar											04/29/2020					  Checkmarx Test method without assert error fix.
* Vamshi Krishna Pemberthi						  		06/08/2021 				Code Coverage Improvements for QueueViewItem_D_HUM
*************************************************************************************************************************************************************************/

@isTest
Public with sharing Class QueueViewsWithItems_T_HUM
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
        User oUser = HUMTestDataHelperTest.createTestUser('User1','System Administrator');
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
            Queue_View__c oQW = New Queue_View__c();
            oQW.Name = 'Test Queue View';
            oQW.Work_Queue_Name__c = oWQS.id;
            oQW.Public_Group_View_Id__c = grp1.id;
            insert oQW;
            System.assertEquals(oQW.Public_Group_View_Id__c, grp1.id);
            Queue_View_Item__c oQVI = New Queue_View_Item__C();
            oQVI.Queue_View__c = oQW.Id;
            insert oQVI;
            
			Queue_View__c oQW1 = New Queue_View__c();
            oQW1.Name = 'Test Queue View1';
            oQW1.Work_Queue_Name__c = oWQS.id;
            oQW1.Public_Group_View_Id__c = grp1.id;
            oQW1.Expression__c = 'Test Queue View1';
            insert oQW1;
            Queue_View_Item__c oQVI1 = New Queue_View_Item__C();
            oQVI1.Queue_View__c = oQW1.Id;
            insert oQVI1;
        }
        
    }
    
     /**
    * Method Is used to test Queue View Creation
    *<p>
    * @param            NULL
    * @return           void   
    */
    Static testMethod void testNewQueueView()
    {
        User oUser = [Select Id , Name, Username from User where Username= 'user1user1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
        System.assertEquals(oUser.Username, 'user1user1@humcrmstest.com');
        System.runas(oUser)
        {
            Work_Queue_Setup__c oWQS = [Select Id, Name from Work_Queue_Setup__c limit 1];
            Queue_View__c oQV=[select Id, Name from Queue_View__c where Name = 'Test Queue View' ];
            Queue_View_Item__c oQVI = [Select id, Name, API_Name__c from Queue_View_Item__c where Queue_View__c =: oQV.Id];
            //Queue_View_Item__c oQVI = [Select id, Name, API_Name__c from Queue_View_Item__c where createdById =: oUser.id];
            String wqsName = oWQS.Name;
            String wqsId = oWQS.id;
            ApexPages.CurrentPage().getParameters().put('Id',oWQS.id);
            Apexpages.StandardController stdController = New Apexpages.StandardController(oWQS);
            QueueViewsWithItems_C_HUM humQW = New QueueViewsWithItems_C_HUM(stdController);
            Test.startTest();
            humQW.newQueueView();
            humQW.editQueueView();
            humQW.editQueueViewItem();
            humQW.sWQSID = oWQS.id;
            humQW.delqviId = oQVI.id;
            humQW.deleteQueueViewItem();
            DateTime ModfiedDate = DateTime.newInstance(2018, 1, 31, 7, 8, 16);
            Test.stopTest();   
        } 
    }
	 Static testMethod void testQueueViewDelete()
    {
        User oUser = [Select Id , Name, Username from User where Username= 'user1user1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
        System.assertEquals(oUser.Username, 'user1user1@humcrmstest.com');
        System.runas(oUser)
        {
            Work_Queue_Setup__c oWQS = [Select Id, Name from Work_Queue_Setup__c limit 1];
            //Queue_View_Item__c oQVI = [Select id, Name, API_Name__c from Queue_View_Item__c where createdById =: oUser.id];
            Queue_View__c lstQV=[select Id, Name, Expression__c from Queue_View__c where Name = 'Test Queue View1' ];
            Queue_view_item__c lstQVI=[SELECT id, Name, API_Name__c from Queue_View_Item__c where Queue_View__c =: lstQV.Id];
            //List<Queue_View__c> lstQV=[select Id, Name, Expression__c from Queue_View__c where Name = 'Test Queue View1' ];
            //List<Queue_view_item__c> lstQVI=[SELECT id, Name, API_Name__c from Queue_View_Item__c where Name = 'Test Queue View1'];
            String wqsName = oWQS.Name;
            String wqsId = oWQS.id;
            ApexPages.CurrentPage().getParameters().put('Id',oWQS.id);
            Apexpages.StandardController stdController = New Apexpages.StandardController(oWQS);
            QueueViewsWithItems_C_HUM humQW = New QueueViewsWithItems_C_HUM(stdController);
            Test.startTest();
            String ns = lstQV.Expression__c + ' '+ lstQVI.Name;
            lstQV.Expression__c = ns;
            update lstQV;
            humQW.newQueueView();
            humQW.editQueueView();
            humQW.editQueueViewItem();
            humQW.sWQSID = oWQS.id;
            humQW.delqviId = lstQVI.id;
            humQW.deleteQueueViewItem();

            DateTime ModfiedDate = DateTime.newInstance(2018, 1, 31, 7, 8, 16);
            Test.stopTest();   
        } 
    }
    }