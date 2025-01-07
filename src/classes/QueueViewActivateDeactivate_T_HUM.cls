/***************************************************************************************************
Apex Class Name :  QueueViewActivateDeactivate_T_HUM
Version: 1.0
Created Date     : Jun 13th 2016
Function         : Test Class for WorkQueueActivateDeactivate_C_HUM
**************************************************************************************************
Modification Log: 

* Developer Name       Review                       Date                        Description
*--------------------------------------------------------------------------------------------------
* Harshith Mandya                                 06/13/2016                    Original Version
* Sharan Shanmugam		50014				      12/01/2016					Implemented REQ - 290990 -  removed all lookup related fields from
																				Case/ Task / Interaction / Attachment To Work Queue Setup And Queue View
* Pradeepkumar Dani		98235					  06/06/2017                    Classification_Type_Old__c Clean up
* Pradeepkumar Dani		102268	 				  06/16/2017					Case Clean Up (Classification__c,Intent__c,Classification_Ctci__c,Intent_Ctci__c,Classification_Text__c,Intent_Text__c)
* Vamshi Krishna Pemberthi						  06/08/2021 		Code Coverage Improvements
***************************************************************************************************/

@isTest
Private Class QueueViewActivateDeactivate_T_HUM
{    
    /**
    * testsetup method to create test data
    *<p>
    * @param    NULL
    * @return   void
    */ 
    @testSetup 
    Static void createCustomData() 
    {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','System administrator');
        oUser.Current_Queue__c ='Test';
        insert oUser;
        System.assertEquals('Test',oUser.Current_Queue__c);
        
        System.runas(oUser)
        {
            Group grp = New Group(Name = 'Test PGQ' ,  developerName = 'test1' , Type='Regular');
            insert grp;
            System.assertEquals('Test PGQ',grp.Name);
            
            GroupMember grpMem = HUMTestDataHelperTest.getGrpMems(grp.id, oUser.id);
            insert grpMem ;
            System.assertEquals(grpMem.GroupId,grp.Id);
            
            Group oGrp = New Group(Name = 'Cincit Calls', Type = 'QUEUE');
            insert oGrp;
            System.assertEquals('Cincit Calls',oGrp.Name);

            QueuesObject oQueueObject = New QueuesObject(QueueID = oGrp.id, SobjectType ='Work_Queue_Setup__c');
            insert oQueueObject;
            System.assertEquals('Work_Queue_Setup__c',oQueueObject.SobjectType);
            
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
            System.assertEquals('Cincit Calls',oWQS.Name);
            
            //Insert Queue View
            Queue_View__c queueView = HUMTestDataHelperTest.getQueueView();
            queueView.Work_Queue_Name__c = oWQS.Id;
            queueView.IsActive__c = false;
            insert queueView;
            System.assertEquals(false,queueView.IsActive__c);
            
            Queue_View__c queueView1 = New Queue_View__c();
            queueView1.Work_Queue_Name__c = oWQS.Id;
            queueView1.Name = 'Test QV One';
            queueView1.IsActive__c = true;
            insert queueView1;
            System.assertEquals(true,queueView1.IsActive__c);
            
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
            oCase.Status = 'Closed';
            oCase.Owner_Queue__c = 'Cincit Calls';
            insert oCase; 
            System.assertEquals('Inbound Call',oCase.Origin);
            
            Task oTask = HUMTestDataHelperTest.getTaskRecord();
            oTask.WhatId = oCase.Id;
            oTask.Task_Owner__c = 'User1';
            oTask.Owner_Queue__c ='Cincinnati CRU1';            
            oTask.status = 'In Progress';
            oTask.QueueOrUserId__c = oUser.Id;
            oTask.Work_Queue_View_Name__c = 'Other';
            insert oTask;
            System.assertEquals('User1',oTask.Task_Owner__c);
            
            //Insert Case
            Case oCase1 = HUMTestDataHelperTest.getCase();
            oCase1.Origin = 'Inbound Call';
            oCase1.Type = 'Customer Inquiry';
            oCase1.CTCI_List__c = ctciRec.Id;
            oCase1.Status = 'In Progress';
            oCase1.Work_Queue_View_Name__c = 'Test QV One';
            oCase1.Owner_Queue__c = 'Cincit Calls';
            insert oCase1; 
            System.assertEquals('Inbound Call',oCase1.Origin);
        }
        
    }
    
    /**
    * Testing the deactivation of Queue View.
    *<p>
    * @param    NULL
    * @return   void   
    */
    Private Static testmethod void testMethodDeactivate()        
    {
                    
        User oUser = [Select Id , Name from User where Username= 'user1user1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
        Work_Queue_Setup__c  oWQS =  [Select Id , Name, Service_Center__c, Department__c, Classification_Type__r.Name, Activate_Deactivate__c, IsActive__c  from Work_Queue_Setup__c where Name = 'Cincit Calls' Limit 1];
        Queue_View__c queueView = [Select id, Name, Work_Queue_Name__c, IsActive__c from Queue_View__c where Work_Queue_Name__c =: oWQS.Id And Name = 'Other'];
        Case c = [Select id, status from Case where  Owner_Queue__c = 'Cincit Calls' AND status = 'Closed'];
        Task tsk = [SELECT Id, Status FROM Task Where WhatId = :c.Id];        
        
        system.runas(oUser)
        {  
            HUMTestDataHelperTest.getHUMConstantsData();
            //Set Current Page                
            Test.setCurrentPage(Page.QueueViewActivateDeactivate_VF_HUM);  
            
            c.status = system.label.HUM_WQClosed;
            update c;
            System.assertEquals(system.label.HUM_WQClosed,c.status);
            
            tsk.status = system.label.HUM_WQClosed;
            update tsk;
            System.assertEquals(system.label.HUM_WQClosed,tsk.status);
            
            queueView.IsActive__c = true;
            update queueView;
            System.assertEquals(true,queueView.IsActive__c);              
                 
            //Add ID to URL               
            ApexPages.currentPage().getParameters().put('id',queueView.Id);                
                 
            Test.startTest();        
            QueueViewActivateDeactivate_C_HUM qvController = New QueueViewActivateDeactivate_C_HUM();        
            qvController.activateDeactivate(); 
            Test.stopTest();
            queueView = [Select id, Name , Work_Queue_Name__c, IsActive__c from Queue_View__c where Work_Queue_Name__c =: oWQS.Id And Name = 'Other'];
            system.assertEquals(false, queueView.IsActive__c);
        }
    } 
    
    /**
    * Testing the exceptions.
    *<p>
    * @param    NULL
    * @return   void   
    */
    Private Static testmethod void testMethodDeactivateException()        
    {
                    
        User oUser = [Select Id , Name from User where Username= 'user1user1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
        Work_Queue_Setup__c  oWQS =  [Select Id , Name, Service_Center__c, Department__c, Classification_Type__r.Name, Activate_Deactivate__c, IsActive__c  from Work_Queue_Setup__c where Name = 'Cincit Calls' Limit 1];
        Queue_View__c queueView = [Select id, Name, Work_Queue_Name__c, IsActive__c from Queue_View__c where Work_Queue_Name__c =: oWQS.Id And Name = 'Other'];
        Case c = [Select id, status from Case where  Owner_Queue__c = 'Cincit Calls' AND status = 'Closed'];
        
        system.runas(oUser)
        {  
            HUMTestDataHelperTest.getHUMConstantsData();
            //Set Current Page                 
            
            c.status = 'In Progress';
            update c;
            System.assertEquals('In Progress',c.status);
            
            queueView.IsActive__c = true;
            update queueView;    
            System.assertEquals(true,queueView.IsActive__c);           
            
            Test.setCurrentPage(Page.QueueViewActivateDeactivate_VF_HUM); 
                 
            //Add ID to URL               
            ApexPages.currentPage().getParameters().put('id',queueView.Id);                
                 
            Test.startTest();        
            QueueViewActivateDeactivate_C_HUM qvController = New QueueViewActivateDeactivate_C_HUM();        
            qvController.activateDeactivate(); 
            ApexPages.Message[] messages = ApexPages.getMessages();
            System.assertEquals(1,messages.size());
            Test.stopTest();
            queueView = [Select id, Name , Work_Queue_Name__c, IsActive__c from Queue_View__c where Work_Queue_Name__c =: oWQS.Id And Name = 'Other'];
            system.assertEquals(true, queueView.IsActive__c);
        }
    } 
	
	
	Private Static testmethod void testMethodDeactivateException1()        
    {
                    
        User oUser = [Select Id , Name from User where Username= 'user1user1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
        Work_Queue_Setup__c  oWQS =  [Select Id , Name, Service_Center__c, Department__c, Classification_Type__r.Name, Activate_Deactivate__c, IsActive__c  from Work_Queue_Setup__c where Name = 'Cincit Calls' Limit 1];
        Queue_View__c queueView = [Select id, Name, Work_Queue_Name__c, IsActive__c from Queue_View__c where Work_Queue_Name__c =: oWQS.Id And Name = 'Other'];
        Case c = [Select id, status from Case where  Owner_Queue__c = 'Cincit Calls' AND status = 'Closed'];
        
        //system.runas(oUser)
        //{  
            HUMTestDataHelperTest.getHUMConstantsData();
            //Set Current Page                          
           //Test.setCurrentPage(Page.QueueViewActivateDeactivate_VF_HUM); 
                 
            //Add ID to URL               
            ApexPages.currentPage().getParameters().put('id', oWQS.Id);                
            Test.startTest();        
            QueueViewActivateDeactivate_C_HUM qvController = New QueueViewActivateDeactivate_C_HUM();        
            qvController.activateDeactivate(); 
            ApexPages.Message[] messages = ApexPages.getMessages();
            //System.assertEquals(1,messages.size());
            Test.stopTest();
            //queueView = [Select id, Name , Work_Queue_Name__c, IsActive__c from Queue_View__c where Work_Queue_Name__c =: oWQS.Id And Name = 'Other'];
            //system.assertEquals(true, queueView.IsActive__c);
        //}
    } 
     
    /**
    * Testing the activation of Queue view
    *<p>
    * @param    NULL
    * @return   void   
    */ 
    Private Static testmethod void testMethodActivate()        
    {
        User oUser = [Select Id , Name from User where Username= 'user1user1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
        Work_Queue_Setup__c  oWQS =  [Select Id , Name, Service_Center__c, Department__c, Classification_Type__r.Name, Activate_Deactivate__c, IsActive__c  from Work_Queue_Setup__c where Name = 'Cincit Calls' Limit 1];
        
        Queue_View__c queueView = [Select id, Name, Work_Queue_Name__c, IsActive__c from Queue_View__c where Work_Queue_Name__c =: oWQS.Id And Name = 'Other'];
        Case c = [Select id, status from Case where  Owner_Queue__c = 'Cincit Calls' AND status = 'Closed'];
        
        system.runas(oUser)
        {  
            HUMTestDataHelperTest.getHUMConstantsData();
            //Set Current Page                
            Test.setCurrentPage(Page.QueueViewActivateDeactivate_VF_HUM);                
                 
            //Add ID to URL               
            ApexPages.currentPage().getParameters().put('id',queueView.Id);    
            
            Test.startTest();        
            QueueViewActivateDeactivate_C_HUM qvController = New QueueViewActivateDeactivate_C_HUM();        
            qvController.activateDeactivate(); 
            Test.stopTest();
            queueView = [Select id, Name , Work_Queue_Name__c, IsActive__c from Queue_View__c where Work_Queue_Name__c =: oWQS.Id And Name = 'Other'];
            system.assertEquals(true, queueView.IsActive__c);
        }
           
      } 
      
      /**
    * Testing the activation of Queue view
    *<p>
    * @param    NULL
    * @return   void   
    */ 
    Private Static testmethod void testMethodOpenCase()        
    {
        User oUser = [Select Id , Name from User where Username= 'user1user1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
        Work_Queue_Setup__c  oWQS =  [Select Id , Name, Service_Center__c, Department__c, Classification_Type__r.Name, Activate_Deactivate__c, IsActive__c  from Work_Queue_Setup__c where Name = 'Cincit Calls' Limit 1];
        
        Queue_View__c queueView = [Select id, Name, Work_Queue_Name__c, IsActive__c from Queue_View__c where Work_Queue_Name__c =: oWQS.Id And Name = 'Test QV One'];
        Case c = [Select id, status from Case where  Owner_Queue__c = 'Cincit Calls' AND status = 'In Progress'];
        
        system.runas(oUser)
        {  
            HUMTestDataHelperTest.getHUMConstantsData();
            //Set Current Page                
            Test.setCurrentPage(Page.QueueViewActivateDeactivate_VF_HUM);                
                 
            //Add ID to URL               
            ApexPages.currentPage().getParameters().put('id',queueView.Id);    
            
            Test.startTest();        
            QueueViewActivateDeactivate_C_HUM qvController = New QueueViewActivateDeactivate_C_HUM();        
            qvController.activateDeactivate(); 
            Test.stopTest();
            queueView = [Select id, Name , Work_Queue_Name__c, IsActive__c from Queue_View__c where Work_Queue_Name__c =: oWQS.Id And Name = 'Test QV One'];
            system.assertEquals(true, queueView.IsActive__c);
        }
           
      }

}