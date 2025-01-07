/***************************************************************************************************
Apex Class Name :  TopicDeactivation_T_HUM
Version: 1.0
Created Date     : June 13th 2016
Function         : Teat Class for TopicDeactivation_C_HUM
**************************************************************************************************
Modification Log:  

* Developer Name         Review                       Date                        Description
*--------------------------------------------------------------------------------------------------
* Sharan Shanmugam                                06/13/2016                     Original version.
* Harshith Mandya 		  50014				      11/30/2106		   			 Implemented REQ - 290990 -  removed all lookup related fields from 
  																				 Case/ Task / Interaction / Attachment To Work Queue Setup And Queue View 
* Pradeepkumar Dani		  98235					  06/06/2017                     Classification_Type_Old__c Clean up
* Pradeepkumar Dani		  102268 				  06/16/2017					 Case Clean Up (Classification__c,Intent__c,Classification_Ctci__c,Intent_Ctci__c,Classification_Text__c,Intent_Text__c)
***************************************************************************************************/

@isTest
Private Class TopicDeactivation_T_HUM
{
	/**
    * testsetup method to create test data
    *<p>
    * @param  			NULL
    * @return 			void
    */
	@testSetup 
	Static void createCustomData() 
	{
        User oUser = HUMTestDataHelperTest.createTestUser('User1','System Administrator');
        oUser.Current_Queue__c = 'Test';
        insert oUser;
        System.assertEquals(oUser.Current_Queue__c, 'Test');
        Group grp = New Group(Name = 'Test PGQ' ,  developerName = 'test' , Type='Regular');
        Group oGrp;
        System.runas(oUser)
	    {
	    	insert grp;
    		System.assertEquals(grp.Name, 'Test PGQ');
	    	
	    	GroupMember grpMem = HUMTestDataHelperTest.getGrpMems(grp.id, oUser.id);
        	insert grpMem ;
        	System.assertEquals(grpMem.GroupId, grp.id);
	    	
			oGrp = New Group(Name = 'Cincit Calls', Type = 'QUEUE');
            insert oGrp;
            System.assertEquals(oGrp.Type, 'QUEUE');

            QueuesObject oQueueObject = New QueuesObject(QueueID = oGrp.id, SobjectType ='Work_Queue_Setup__c');
            insert oQueueObject;
            System.assertEquals(oQueueObject.QueueID, oGrp.id);
			
			Classification_Type__c ct = new Classification_Type__c(Name = 'Test', Is_Active__c = true, Guidance_Alert_Limit__c = 2);
			insert ct;
			system.assertEquals(ct.Name, 'Test');

	    	//Insert Work Queue Setup
	    	Work_Queue_Setup__c oWQS = New Work_Queue_Setup__c();
	    	oWQS.Name = 'Cincit Calls';
            oWQS.Service_Center__c='Cincit';
            oWQS.Department__c='Calls';
            oWQS.Classification_Type__c = ct.Id;
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
			Queue_View_Item__c oQVI = New Queue_View_Item__c();
			oQVI.API_Name__c= 'Topic';
			oQVI.Field_Operator__c = 'Equals';
			oQVI.Field_Value__c = 'Test Topic';
            oQVI.Queue_View__c = queueView.Id;
            insert oQVI;
            System.assertEquals(oQVI.API_Name__c, 'Topic');
            
			//Insert Work Queue Topic
			Work_Queue_Topic__c oWQT = New Work_Queue_Topic__c();
			oWQT.Name = 'Test Topic'; 
			oWQT.IsActive__c = true;
			insert oWQT;
			System.assertEquals(oWQT.Name, 'Test Topic');
			
			//Insert Work Queue Junction
			Work_Queue_Junction__c oWQJ = New Work_Queue_Junction__c();
			oWQJ.Name = 'Test Junction';
			oWQJ.Work_Queue_Setup_Name__c = oWQS.id;
			oWQJ.Work_Queue_Topic_Name__c = oWQT.Id;
			insert oWQJ;
			System.assertEquals(oWQJ.Work_Queue_Setup_Name__c, oWQS.id);
			
			List<Classification_Type__c> ctList= new list<Classification_Type__c>();
	        Classification_Type__c ctRec = new Classification_Type__c();
	        ctRec.name = 'calls';
	        ctRec.Is_Active__c=true;
	        ctList.add(ctRec);	        
	        insert ctList;
	        system.assertEquals(ctList.size(),1);
            
            List<Classification__c> lstClassification = new List<Classification__c>();
            Classification__c classificationRec = new Classification__c();
            classificationRec.Name = 'Billing';
            classificationRec.Is_Active__c = true;
            lstClassification.add(classificationRec);			
            insert lstClassification;
            system.assertEquals(lstClassification[0].Name=='Billing',true);
                
            List<Intent__c> lstIntent = new List<Intent__c>();
            Intent__c intentRec = new Intent__c();
            intentRec.Name = 'Make Payment';
            intentRec.Is_Active__c = true;
            lstIntent.add(intentRec);
            insert lstIntent;
            system.assertEquals(lstIntent[0].Name=='Make Payment',true);
            
            List<CTCI_Junction__c> ctciList = new list<CTCI_Junction__c>();
	        CTCI_Junction__c ctciRec = new CTCI_Junction__c();
	        ctciRec.Classification_Type__c = ctRec.id;
	        ctciRec.Classification__c = classificationRec.Id;
	        ctciRec.Intent__c = intentRec.id;
	        ctciRec.IsActive__c = true;
	        ctciList.add(ctciRec);
	        insert ctciList;
	        system.assertEquals(ctciList.size(),1);
	        
			 //Insert Case
			Case oCase = HUMTestDataHelperTest.getCase();
			oCase.Origin = 'Inbound Call';
			oCase.Type = 'Customer Inquiry';
			oCase.CTCI_List__c = ctciRec.Id;
			oCase.Status = 'In Progress';
			oCase.Topic__c = 'Test Topic';
			oCase.Owner_Queue__c = 'Cincit Calls';
			insert oCase; 
			System.assertEquals('Inbound Call',oCase.Origin);
			
			Task oTask = HUMTestDataHelperTest.getTaskRecord();
            oTask.WhatId = oCase.Id;
            oTask.Task_Owner__c = 'User1';
            oTask.Owner_Queue__c = 'Cincit Calls';           
            oTask.status = 'In Progress';
            oTask.QueueOrUserId__c = oUser.Id;
            oTask.Work_Queue_View_Name__c = 'Other';
            oTask.Topic__c = 'Test Topic';
            insert oTask;
            System.assertEquals('User1',oTask.Task_Owner__c);
            
	    }
	}	
	
	/**
    * Method Is used to test deactivation of topic
    *<p>
    * @param  			NULL
    * @return 			void   
    */
	Private Static testmethod void deactivateTopic()        
	{
		User oUser = [Select Id , Name from User where Username= 'user1user1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
		Work_Queue_Setup__c  oWQS =  [Select Id , Name, Service_Center__c, Department__c, Classification_Type__r.Name, Activate_Deactivate__c, IsActive__c  from Work_Queue_Setup__c where Name = 'Cincit Calls' Limit 1];
		Work_Queue_Topic__c oWQT = [Select id, Name, IsActive__c from Work_Queue_Topic__c where Name = 'Test Topic' limit 1];
		Queue_View__c queueView = [Select id, Name, Work_Queue_Name__c, IsActive__c from Queue_View__c where Work_Queue_Name__c =: oWQS.Id];
		Queue_View_Item__c oQVI = [Select API_Name__c, Field_Operator__c, Field_Value__c, Queue_View__c from Queue_View_Item__c where Queue_View__c =: queueView.id];
		Work_Queue_Junction__c oWQJ = [Select id, Name, Work_Queue_Setup_Name__c, Work_Queue_Topic_Name__c from Work_Queue_Junction__c where Work_Queue_Topic_Name__c =: oWQT.Id And Work_Queue_Setup_Name__c =: oWQS.id];
		Case c = [Select id, status,Owner_Queue__c from Case where  Owner_Queue__c = :oWQS.Name];
		Task oTask = [Select id, status, Topic__c from Task where Work_Queue_View_Name__c = 'Other' limit 1];
		
		system.runas(oUser)
		{  
			HUMTestDataHelperTest.getHUMConstantsData();
			//Set Current Page                
			Test.setCurrentPage(Page.TopicDeactivation_VF_HUM);
			//Add ID to URL               
			ApexPages.currentPage().getParameters().put('wqId',oWQS.Id); 
			
			c.status = system.label.HUM_WQClosed;
	        update c;
	        System.assertEquals(system.label.HUM_WQClosed,c.status);
	        
	        oTask.status = system.label.HUM_WQClosed;
	        update oTask;
	        System.assertEquals(system.label.HUM_WQClosed,oTask.status);
			
			Test.startTest();
			TopicDeactivation_C_HUM topicdeactivate = New TopicDeactivation_C_HUM();
			topicdeactivate.sTopic = oWQT.id;
			topicdeactivate.deactivateTopic();
			oWQT = [Select id, Name, IsActive__c from Work_Queue_Topic__c where Name = 'Test Topic' limit 1];
			system.assertEquals(false, oWQT.IsActive__c);
			Pagereference pgRef = topicdeactivate.cancel();
			System.assertequals(String.Valueof(pgRef),String.ValueOf(New PageReference('/'+oWQS.id)));
			Test.stopTest();
			
		}
		
	}
	
	/**
    * Method Is used to test deactivation of topic
    *<p>
    * @param  			NULL
    * @return 			void   
    */
	Private Static testmethod void deactivateTopicException()        
	{
		User oUser = [Select Id , Name from User where Username= 'user1user1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
		Work_Queue_Setup__c  oWQS =  [Select Id , Name, Service_Center__c, Department__c, Classification_Type__r.Name, Activate_Deactivate__c, IsActive__c  from Work_Queue_Setup__c where Name = 'Cincit Calls' Limit 1];
		Work_Queue_Topic__c oWQT = [Select id, Name, IsActive__c from Work_Queue_Topic__c where Name = 'Test Topic' limit 1];
		Queue_View__c queueView = [Select id, Name, Work_Queue_Name__c, IsActive__c from Queue_View__c where Work_Queue_Name__c =: oWQS.Id];
		Queue_View_Item__c oQVI = [Select API_Name__c, Field_Operator__c, Field_Value__c, Queue_View__c from Queue_View_Item__c where Queue_View__c =: queueView.id];
		Work_Queue_Junction__c oWQJ = [Select id, Name, Work_Queue_Setup_Name__c, Work_Queue_Topic_Name__c from Work_Queue_Junction__c where Work_Queue_Topic_Name__c =: oWQT.Id And Work_Queue_Setup_Name__c =: oWQS.id];
		Case c = [Select id, status,Owner_Queue__c from Case where  Owner_Queue__c = :oWQS.Name];
		Task oTask = [Select id, status from Task where Work_Queue_View_Name__c = 'Other'];
		
		system.runas(oUser)
		{  
			HUMTestDataHelperTest.getHUMConstantsData();
			//Set Current Page                
			Test.setCurrentPage(Page.TopicDeactivation_VF_HUM);
			//Add ID to URL               
			ApexPages.currentPage().getParameters().put('wqId',oWQS.Id); 
			
			c.status = system.label.HUM_WQClosed;
	        update c;
	        System.assertEquals(system.label.HUM_WQClosed,c.status);
			
			Test.startTest();
			TopicDeactivation_C_HUM topicdeactivate = New TopicDeactivation_C_HUM();
			topicdeactivate.sTopic = oWQT.id;
			topicdeactivate.deactivateTopic();
			oWQT = [Select id, Name, IsActive__c from Work_Queue_Topic__c where Name = 'Test Topic' limit 1];
			//system.assertEquals(true, oWQT.IsActive__c);
			Pagereference pgRef = topicdeactivate.cancel();
			System.assertequals(String.Valueof(pgRef),String.ValueOf(New PageReference('/'+oWQS.id)));
			Test.stopTest();
		}
	}
}