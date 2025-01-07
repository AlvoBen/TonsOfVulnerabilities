/***************************************************************************************************
Apex Class Name :  WorkQueueActivateDeactivate_T_HUM
Version: 1.0
Created Date     : Jun 13th 2016
Function         : Test Class for WorkQueueActivateDeactivate_C_HUM
**************************************************************************************************
Modification Log: 

* Developer Name       Review                       Date                        Description
*--------------------------------------------------------------------------------------------------
* Harshith Mandya       30706                     06/13/2016                    Original Version
* Harshith Mandya       50014                     11/30/2106                    Implemented REQ - 290990 -  removed all lookup related fields from
                                                                                Case/ Task / Interaction / Attachment To Work Queue Setup And Queue View
* Pradeepkumar Dani     98235                     06/06/2017                    Classification_Type_Old__c Clean up
* Pradeepkumar Dani     102268                    06/16/2017                    Case Clean Up (Classification__c,Intent__c,Classification_Ctci__c,Intent_Ctci__c,Classification_Text__c,Intent_Text__c)
* Prudhvi Pamarthi      154304                   01/19/2018                    REQ - 328929 Fix for HUMExceptionHelper: DML 
*   Suresh Sunkari      272809                   08/16/2018                     REQ - 366108 Implemented it.  Regarding Transfer Open cases and Tasks
* Vamshi Krishna Pemberthi						  06/08/2021 		Code Coverage Improvements
***************************************************************************************************/

@isTest
Private Class WorkQueueActivateDeactivate_T_HUM
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
        oUser.Current_Queue__c = 'Cincit Calls';
        insert oUser;
        System.assertEquals('User1@test.com',oUser.Email);
        
        System.runas(oUser)
        {
            Group grp = New Group(Name = 'Test PGQ' ,  developerName = 'test1' , Type='Regular');
            insert grp;
            System.assertEquals('Test PGQ',grp.Name);
            
            GroupMember grpMem = HUMTestDataHelperTest.getGrpMems(grp.id, oUser.id);
            insert grpMem ;
            System.assertEquals(grp.id,grpMem.GroupId);
            
            Group oGrp;
            oGrp = New Group(Name = 'Cincit Calls', Type = 'QUEUE');
            insert oGrp;
            System.assertEquals('Cincit Calls',oGrp.Name);
            
            Group oGrp2;
            oGrp2 = New Group(Name = 'Cincit Cal', Type = 'QUEUE');
            insert oGrp2;
            System.assertEquals('Cincit Cal',oGrp2.Name);

            QueuesObject oQueueObject = New QueuesObject(QueueID = oGrp.id, SobjectType ='Work_Queue_Setup__c');
            insert oQueueObject;
            System.assertEquals(oGrp.id,oQueueObject.QueueID);
            
            QueuesObject oQueueObject2 = New QueuesObject(QueueID = oGrp2.id, SobjectType ='Work_Queue_Setup__c');
            insert oQueueObject2;
            System.assertEquals(oGrp2.id,oQueueObject2.QueueID);
            
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
            insert queueView;
            System.assertEquals(oWQS.Id,queueView.Work_Queue_Name__c);
            
            
            
            //Insert  Topics
            Work_Queue_Topic__c queueTopic = New Work_Queue_Topic__c(name= 'Test');
            insert queueTopic;
            System.assertEquals(queueTopic.name,'Test');
            
            Work_Queue_Topic__c queueTopic1 = New Work_Queue_Topic__c(name= 'NA');
            insert queueTopic1;
            System.assertEquals(queueTopic1.name,'NA');
            
            //Insert Workqueue and Topic Junction
            Work_Queue_Setup__c oWQS2 = New Work_Queue_Setup__c();
            oWQS2.Name = 'Cincit Cal';
            oWQS2.Service_Center__c='Cincit';
            oWQS2.Department__c='Cal';
            oWQS2.Classification_Type__c = ct.Id;
            oWQS2.IsActive__c = true;
            oWQS2.OwnerId= oGrp.id;
            oWQS2.Public_Group_Queue_Id__c = oQueueObject.Id;
            oWQS2.General__c = true;
            oWQS2.HO_Medical__c = true;
            oWQS2.HO_Dental__c = true;
            insert oWQS2;
            System.assertEquals('Cincit Cal',oWQS2.Name);
            
            Work_Queue_Junction__c oWQJ = New Work_Queue_Junction__c();
            oWQJ.Name = 'Cincit Cal Test Jn';
            oWQJ.Work_Queue_Setup_Name__c = oWQS2.Id;
            oWQJ.Work_Queue_Topic_Name__c = queueTopic.Id;
            insert oWQJ;
            System.assertEquals('Cincit Cal Test Jn',oWQJ.Name);
            
            
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
            Case c = HUMTestDataHelperTest.getCase();
            c.Origin = 'Inbound Call';
            c.Type = 'Customer Inquiry';
            c.CTCI_List__c = ctciRec.Id ;
            c.Status = 'Closed';
            c.Owner_Queue__c = 'Cincit Calls';
            insert c; 
            System.assertEquals('Inbound Call',c.Origin);
        }
    }
    
    /**
    * Test method for acception cases in Activation.
    *<p>
    * @param    NULL
    * @return   void   
    */
    Private Static testmethod void testMethodDeactivateException()
    {
        User oUser = [Select Id , Name from User where Username= 'user1user1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
        Work_Queue_Setup__c  oWQS =  [Select Id , Name, Service_Center__c, Department__c, Classification_Type__r.Name, Activate_Deactivate__c, IsActive__c  from Work_Queue_Setup__c 
                                        where Name = 'Cincit Calls' Limit 1];
        Queue_View__c queueView = [Select id, Name , Work_Queue_Name__c, IsActive__c from Queue_View__c where Work_Queue_Name__c =: oWQS.Id];
        Case c = [Select id, status from Case where  Owner_Queue__c = 'Cincit Calls'];
        
        system.runas(oUser)
        {        
            HUMTestDataHelperTest.getHUMConstantsData();
            //Set Current Page        
            Test.setCurrentPage(Page.WorkQueueActivateDeactivate_VF_HUM);        
            //Add ID to URL       
            ApexPages.currentPage().getParameters().put('id',oWQS.Id);
            c.Status = 'In Progress';
            update c;
            Test.startTest();
            WorkQueueActivateDeactivate_C_HUM wqController = New WorkQueueActivateDeactivate_C_HUM();
            wqController.getActiveDeactive(); 
            system.assertEquals('Deactivate', wqController.sActivateDeactivate);
            wqController.activateDeactivate();
			wqController.CheckValidation();
            ApexPages.Message[] messages = ApexPages.getMessages();
            System.assertEquals(1,messages.size());
			wqController.OnFinishPage();
            Test.stopTest();
            oWQS =  [Select Id , Name, Service_Center__c, Department__c, Classification_Type__r.Name, Activate_Deactivate__c, IsActive__c  from Work_Queue_Setup__c where Name = 'Cincit Calls' Limit 1];
            system.assertEquals(true, oWQS.IsActive__c);
        }
    }
	
	Private Static testmethod void testMethodDeactivateException1()
    {
        User oUser = [Select Id , Name from User where Username= 'user1user1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
        Work_Queue_Setup__c  oWQS =  [Select Id , Name, Service_Center__c, Department__c, Classification_Type__r.Name, Activate_Deactivate__c, IsActive__c  from Work_Queue_Setup__c 
                                        where Name = 'Cincit Calls' Limit 1];
        Queue_View__c queueView = [Select id, Name , Work_Queue_Name__c, IsActive__c from Queue_View__c where Work_Queue_Name__c =: oWQS.Id];
        Case c = [Select id, status from Case where  Owner_Queue__c = 'Cincit Calls'];
        
        system.runas(oUser)
        {        
            HUMTestDataHelperTest.getHUMConstantsData();
            //Set Current Page        
            Test.setCurrentPage(Page.WorkQueueActivateDeactivate_VF_HUM);        
            //Add ID to URL       
            ApexPages.currentPage().getParameters().put('id',oWQS.Id);
            c.Status = 'In Progress';
            update c;
            Test.startTest();
            WorkQueueActivateDeactivate_C_HUM wqController = New WorkQueueActivateDeactivate_C_HUM();
            wqController.getActiveDeactive(); 
            system.assertEquals('Deactivate', wqController.sActivateDeactivate);
            wqController.activateDeactivate();
            wqController.CheckValidation();

            ApexPages.Message[] messages = ApexPages.getMessages();
            System.assertEquals(1,messages.size());
            wqController.bEmptyWQTPerrorFlag= false;
            wqController.OnFinishPage();

            Test.stopTest();
            oWQS =  [Select Id , Name, Service_Center__c, Department__c, Classification_Type__r.Name, Activate_Deactivate__c, IsActive__c  from Work_Queue_Setup__c where Name = 'Cincit Calls' Limit 1];
            system.assertEquals(true, oWQS.IsActive__c);
        }
    }
    
    /**
    * Test method for Deactivation
    *<p>
    * @param    NULL
    * @return   void   
    */
    Private Static testmethod void testMethodDeactivate()
    {
        User oUser = [Select Id , Name, Current_Queue__c from User where Username= 'user1user1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
        Work_Queue_Setup__c  oWQS =  [Select Id , Name, Service_Center__c, Department__c, Classification_Type__r.Name, Activate_Deactivate__c, IsActive__c  from Work_Queue_Setup__c where Name = 'Cincit Calls' Limit 1];
        Queue_View__c queueView = [Select id, Name, Work_Queue_Name__c, IsActive__c from Queue_View__c where Work_Queue_Name__c =: oWQS.Id];
        Case c = [Select id, status from Case where  Owner_Queue__c = 'Cincit Calls'];
        
        system.runas(oUser)
        {        
            HUMTestDataHelperTest.getHUMConstantsData();
            //Set Current Page        
            Test.setCurrentPage(Page.WorkQueueActivateDeactivate_VF_HUM);        
            //Add ID to URL       
            ApexPages.currentPage().getParameters().put('id',oWQS.Id);
            
            Test.startTest();
            WorkQueueActivateDeactivate_C_HUM wqController = New WorkQueueActivateDeactivate_C_HUM();
            wqController.getActiveDeactive(); 
			wqController.OnFinishPage();            
            system.assertEquals('Deactivate', wqController.sActivateDeactivate);
            system.assertEquals('Closed', c.status);
            wqController.activateDeactivate();
            wqController.CheckValidation();

            ApexPages.Message[] messages = ApexPages.getMessages();
            //System.assertEquals(0,messages.size());
            System.assertEquals(1,messages.size());
            wqController.OnFinishPage();
            Test.stopTest();
            oWQS =  [Select Id , Name, Service_Center__c, Department__c, Classification_Type__r.Name, Activate_Deactivate__c, IsActive__c  from Work_Queue_Setup__c where Name = 'Cincit Calls' Limit 1];
            system.assertEquals(false, oWQS.IsActive__c);
            oUser = [Select Id , Name, Current_Queue__c from User where Username= 'user1user1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
            system.assertEquals(null, oUser.Current_Queue__c);
        }
    }
    
    /**
    * Test method for Activation
    *<p>
    * @param    NULL
    * @return   void   
    */
    Private Static testmethod void testMethodActivate()
    {
        User oUser = [Select Id , Name from User where Username= 'user1user1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
        Work_Queue_Setup__c  oWQS =  [Select Id , Name, Service_Center__c, Department__c, Classification_Type__r.Name, Activate_Deactivate__c, IsActive__c  from Work_Queue_Setup__c where Name = 'Cincit Calls' Limit 1];
        Queue_View__c queueView = [Select id, Name, Work_Queue_Name__c, IsActive__c from Queue_View__c where Work_Queue_Name__c =: oWQS.Id];
        Case c = [Select id, status from Case where  Owner_Queue__c = 'Cincit Calls' limit 1];
        
        system.runas(oUser)
        {        
            HUMTestDataHelperTest.getHUMConstantsData();
            
            oWQS.IsActive__c = false;
            update oWQS;
            
            //Set Current Page        
            Test.setCurrentPage(Page.WorkQueueActivateDeactivate_VF_HUM);        
            //Add ID to URL       
            ApexPages.currentPage().getParameters().put('id',oWQS.Id);
            
            Test.startTest();
            WorkQueueActivateDeactivate_C_HUM wqController = New WorkQueueActivateDeactivate_C_HUM();
            wqController.getActiveDeactive();
            system.assertEquals('Activate', wqController.sActivateDeactivate); 
            wqController.activateDeactivate();
			wqController.CheckValidation();
            ApexPages.Message[] messages = ApexPages.getMessages();
            System.assertEquals(0,messages.size());
            List<Error_Log__c> lstLog = new List<Error_Log__c>();            
            Error_Log__c error = new Error_Log__c(Status__c = 'WorkQueueActivateDeactivate_C_HUM');            
            lstLog.add(error);                        
            wqController.listErrorLogs = lstLog;            
            System.assert(wqController.listErrorLogs.size() > 0); 
			wqController.OnFinishPage();
            Test.stopTest();
            oWQS =  [Select Id , Name, Service_Center__c, Department__c, Classification_Type__r.Name, Activate_Deactivate__c, IsActive__c  from Work_Queue_Setup__c where Name = 'Cincit Calls' Limit 1];
            system.assertEquals(true, oWQS.IsActive__c);
        }
    }
    
    /**
    * Test method for Activation Exception
    *<p>
    * @param    NULL
    * @return   void   
    */
    Private Static testmethod void testMethodActivateException()
    {
        User oUser = [Select Id , Name from User where Username= 'user1user1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
        
        system.runas(oUser)
        {        
            HUMTestDataHelperTest.getHUMConstantsData();
            //Set Current Page        
            Test.setCurrentPage(Page.WorkQueueActivateDeactivate_VF_HUM); 
                  
            Test.startTest();
            
                WorkQueueActivateDeactivate_C_HUM wqController = New WorkQueueActivateDeactivate_C_HUM();
                wqController.getActiveDeactive();
                wqController.activateDeactivate();
				wqController.CheckValidation();
                ApexPages.Message[] messages = ApexPages.getMessages();
                System.assertEquals(0,messages.size());
                wqController.OnFinishPage();
            Test.stopTest();
        }
    }
	
	Private Static testmethod void testMethodActivateException1()
    {
        User oUser = [Select Id , Name from User where Username= 'user1user1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
        Work_Queue_Setup__c  oWQS =  [Select Id , Name, Service_Center__c, Department__c, Classification_Type__r.Name, Activate_Deactivate__c, IsActive__c  from Work_Queue_Setup__c 
                                        where Name = 'Cincit Calls' Limit 1];
        Queue_View__c queueView = [Select id, Name , Work_Queue_Name__c, IsActive__c from Queue_View__c where Work_Queue_Name__c =: oWQS.Id];
        Case c = [Select id, status from Case where  Owner_Queue__c = 'Cincit Calls'];

        system.runas(oUser)
        {        
            HUMTestDataHelperTest.getHUMConstantsData();
            //Set Current Page        
            Test.setCurrentPage(Page.WorkQueueActivateDeactivate_VF_HUM); 
            ApexPages.currentPage().getParameters().put('id', null);
            Test.startTest();
            
                WorkQueueActivateDeactivate_C_HUM wqController = New WorkQueueActivateDeactivate_C_HUM();
                
                
                wqController.sQueue='';
                wqController.DependentPickListController();
                wqController.OnFinishPage();
               // queueView.IsActive__c= null;
                //wqController.getActiveDeactive();
               // wqController.activateDeactivate();
                wqController.CheckValidation();
                ApexPages.Message[] messages = ApexPages.getMessages();
                //System.assertEquals(0,messages.size());
                //wqController.bEmptyWQTPerrorFlag= false;
                wqController.OnFinishPage();
                
                WorkQueueActivateDeactivate_C_HUM wqController1 = New WorkQueueActivateDeactivate_C_HUM();
                wqController1.OnFinishPage();

            Test.stopTest();
        }

        
    }

    Private Static testmethod void testMethodActivateException2()
    {
        User oUser = [Select Id , Name from User where Username= 'user1user1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
        Work_Queue_Setup__c  oWQS =  [Select Id , Name, Service_Center__c, Department__c, Classification_Type__r.Name, Activate_Deactivate__c, IsActive__c  from Work_Queue_Setup__c 
                                        where Name = 'Cincit Calls' Limit 1];
        Queue_View__c queueView = [Select id, Name , Work_Queue_Name__c, IsActive__c from Queue_View__c where Work_Queue_Name__c =: oWQS.Id];
        Case c = [Select id, status from Case where  Owner_Queue__c = 'Cincit Calls'];
        system.runas(oUser)
        {        
            HUMTestDataHelperTest.getHUMConstantsData();
            //Set Current Page        
            Test.setCurrentPage(Page.WorkQueueActivateDeactivate_VF_HUM); 
            ApexPages.currentPage().getParameters().put('id', 'assasasa');
            Test.startTest();
            
                WorkQueueActivateDeactivate_C_HUM wqController = New WorkQueueActivateDeactivate_C_HUM();
            
            Test.stopTest();
        }
    }
    

}