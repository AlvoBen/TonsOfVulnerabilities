/***************************************************************************************************
Apex Class Name  : WorkQueueDownload_T_HUM
Version          : 1.0
Created Date     : June 13th 2016
Function         : Teat Class for WorkQueueDownload_C_HUM
**************************************************************************************************
Modification Log:

* Developer Name       Review                       Date                        Description
*--------------------------------------------------------------------------------------------------
* Sharan Shanmugam                                06/13/2016                   Original version.
* Pradeepkumar Dani		98235					  06/06/2017                   Classification_Type_Old__c Clean up
* Vamshi Krishna Pemberthi						  06/08/2021 		Code Coverage Improvements
***************************************************************************************************/

@isTest
private class WorkQueueDownload_T_HUM 
{
    /**
    * testsetup method to create test data
    *<p>
    * @param            NULL
    * @return           void
    */
    @testSetup 
    static void createCustomData() 
    {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','System Administrator');
        oUser.Current_Queue__c = 'Test';
        insert oUser;
        System.assertEquals(oUser.Current_Queue__c, 'Test');
        Group grp = new Group(Name = 'Test PGQ' ,  developerName = 'test1' , Type='Regular');
        Group oGrp;
        System.runas(oUser)
        {
            insert grp;
            System.assertEquals(grp.Name, 'Test PGQ');
            
            GroupMember grpMem = HUMTestDataHelperTest.getGrpMems(grp.id , oUser.id);
            insert grpMem ;
            System.assertEquals(grpMem.GroupId, grp.id);
            
            oGrp = new Group(Name = 'Cincit Calls', Type = 'QUEUE');
            insert oGrp;
            System.assertEquals(oGrp.Type, 'QUEUE');

            QueuesObject oQueueObject = new QueuesObject (QueueID = oGrp.id, SobjectType ='Work_Queue_Setup__c');
            insert oQueueObject;
            System.assertEquals(oQueueObject.QueueID, oGrp.id);
            
			Classification_Type__c ct = new Classification_Type__c(Name = 'Test', Is_Active__c = true, Guidance_Alert_Limit__c = 2);
			insert ct;
			system.assertEquals(ct.Name, 'Test');

            //Insert Work Queue Setup
            Work_Queue_Setup__c oWQS = new Work_Queue_Setup__c();
            oWQS.Name = 'Cincit Calls';
            oWQS.Service_Center__c='Cincit';
            oWQS.Department__c='Calls';
            oWQS.Classification_Type__c=ct.Id;
            oWQS.IsActive__c = true;
            oWQS.OwnerId= oGrp.id;
            oWQS.Public_Group_Queue_Id__c = oQueueObject.Id;
            oWQS.General__c = true;
            oWQS.HO_Medical__c = true;
            oWQS.HO_Dental__c = true;
            insert oWQS;
            System.assertEquals(oWQS.Name, 'Cincit Calls');
            
            //Insert Queue View
            Queue_View__c queueView = HUMTestDataHelperTest.getQueueView();
            queueView.Work_Queue_Name__c = oWQS.Id;
            queueView.IsActive__c = false;
            insert queueView;
            System.assertEquals(queueView.Work_Queue_Name__c, oWQS.Id);
            
            //Insert Queue View Item
            Queue_View_Item__c oQVI = new Queue_View_Item__c();
            oQVI.API_Name__c= 'Topic';
            oQVI.Field_Operator__c = 'Equals';
            oQVI.Field_Value__c = 'Test Topic';
            oQVI.Queue_View__c = queueView.Id;
            insert oQVI;
            System.assertEquals(oQVI.API_Name__c, 'Topic');
            
        }
    }   
    
    /**
    * Method Is used to test work queue and queue view download
    *<p>
    * @param            NULL
    * @return           void   
    */
    private static testmethod void workQueueDownload()        
    {
        User oUser = [Select Id , Name from User where Username= 'user1user1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
        Work_Queue_Setup__c  oWQS =  [Select Id , Name, Service_Center__c, Department__c, Classification_Type__r.Name, Activate_Deactivate__c, IsActive__c  from Work_Queue_Setup__c where Name = 'Cincit Calls' Limit 1];
        Queue_View__c queueView = [Select id, Name , Work_Queue_Name__c, IsActive__c from Queue_View__c where Work_Queue_Name__c =: oWQS.Id];
        Queue_View_Item__c oQVI = [Select API_Name__c, Field_Operator__c, Field_Value__c, Queue_View__c from Queue_View_Item__c where Queue_View__c =: queueView.id];
        system.runas(oUser)
        {  
            HUMTestDataHelperTest.getHUMConstantsData();
            //Set Current Page                
            Test.setCurrentPage(Page.WorkQueueDownload_VF_HUM);
            WorkQueueDownload_C_HUM wqDownload = new WorkQueueDownload_C_HUM();
            System.assertEquals(oWQS.Name ,wqDownload.lstQueueView[0].Work_Queue_Name__r.Name);
        }
    }
	
	private static testmethod void workQueueDownloadException()        
    {
        User oUser = [Select Id , Name from User where Username= 'user1user1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
        Work_Queue_Setup__c  oWQS =  [Select Id , Name, Service_Center__c, Department__c, Classification_Type__r.Name, Activate_Deactivate__c, IsActive__c  from Work_Queue_Setup__c where Name = 'Cincit Calls' Limit 1];
        Queue_View__c queueView = [Select id, Name , Work_Queue_Name__c, IsActive__c from Queue_View__c where Work_Queue_Name__c =: oWQS.Id];
        Queue_View_Item__c oQVI = [Select API_Name__c, Field_Operator__c, Field_Value__c, Queue_View__c from Queue_View_Item__c where Queue_View__c =: queueView.id];
        system.runas(oUser)
        {  
            HUMTestDataHelperTest.getHUMConstantsData();
            //Set Current Page   
            
            WorkQueueDownload_C_HUM wqDownload = new WorkQueueDownload_C_HUM();
            WorkQueueDownload_S_HUM exDownload = new WorkQueueDownload_S_HUM();
            exDownload.initialize(null);
            System.assertEquals(oWQS.Name ,wqDownload.lstQueueView[0].Work_Queue_Name__r.Name);
        }
    }
}