/***************************************************************************************************
Apex Class Name  : InactiveLocationOnCRMRetailEvent_T_HUM
Version          : 1.0
Created Date     : 07/09/2019
Function         : Test Class for InactiveLocationOnCRMRetailEvent_H_HUM
**************************************************************************************************
Modification Log:

* Developer Name       Review                       Date                        Description
*--------------------------------------------------------------------------------------------------
*  Santhosh Ganji                               07/09/2019                    Original Version 
*  Vinoth L                                     11/13/2020				      To prevent duplicate insert and update,delete/update prevention when Interaction exist
*  Abhishek Maurya								02/17/2021					  Production fix - Duplicate check
*  Ekta Bist									07/16/2021		              CRM Storefront VNC: Restrict Access to Virtual Locations
*  Vinoth L										05/31/2023					  Edit attendance field permissions on Event
***************************************************************************************************/
@isTest
private class InactiveLocationOnCRMRetailEvent_T_HUM { 
   
    /**
    *  This method creates test data for test class
    *  @name setUpTestData
    *  @param NA
    *  @return NA
    *  @throws NA
    *  @see 
    */
    @testSetup static void setUpTestData()
    {                        
        List<User> lstUsers = new List<User>();
        User oUser1 = HUMTestDataHelperTest.createTestUser('User1','CRMRetail User');
        lstUsers.add(oUser1);
        User oUser2 = HUMTestDataHelperTest.createTestUser('User2','CRMRetail User');
        lstUsers.add(oUser2);
        insert lstUsers;
        Assert.areEqual(lstUsers.size(),2);
        
        List<PermissionSet> lstPrmsSet = [SELECT Id,Name FROM PermissionSet WHERE Name = 'CRMRetail_400_Super_Admin' OR Name = 'CRMRetail_100_coordinator'];
        List<PermissionSetAssignment> psAssign = new List<PermissionSetAssignment>();
        PermissionSetAssignment supAdmin;
        PermissionSetAssignment coordinatorUsr;
        for(PermissionSet prms : lstPrmsSet){
            if(prms.Name == 'CRMRetail_400_Super_Admin')
            {
                supAdmin = new PermissionSetAssignment(AssigneeId = oUser1.id, PermissionSetId = prms.Id);
                psAssign.add(supAdmin);
            }
            else if(prms.Name == 'CRMRetail_100_coordinator')
            {
                coordinatorUsr = new PermissionSetAssignment(AssigneeId = oUser2.id, PermissionSetId = prms.Id);
                psAssign.add(coordinatorUsr);
            }            
        }                                
        insert psAssign;        
        Assert.areEqual(psAssign.size(),2);                
        
        system.runAs(oUser2){            
            List<Storefront_Location__c> listOfLocation = new List<Storefront_Location__c>();
            
            Storefront_Location__c location1 = new Storefront_Location__c(Name = 'Biloxl',Address_Line_1__c = '8710',City__c='Biloxl',Zip_Code__c='40321',Org_Facility__c='23122',State__c='Biloxl - B',Inactive_indicator__c = false);
            listOfLocation.add(location1);
            
            Storefront_Location__c location2 = new Storefront_Location__c(Name = 'Virginia',Address_Line_1__c = '9005',City__c='Virginia',Zip_Code__c='40222',Org_Facility__c='12445',State__c='Virginia - VA',Inactive_indicator__c = false);
            listOfLocation.add(location2);
            
            Storefront_Location__c location3 = new Storefront_Location__c(Name = 'Austin',Address_Line_1__c = '1233',City__c='Atlanta',Zip_Code__c='12561',Org_Facility__c='13414',State__c='Atlanta',Inactive_indicator__c = true);
            listOfLocation.add(location3); 
            
            insert listOfLocation;
            system.assertEquals(listOfLocation.size() , 3);
            
            List<Storefront_I_Type__c> listOfType = new List<Storefront_I_Type__c>();
            
            Storefront_I_Type__c InteractionType1 = new Storefront_I_Type__c(Name ='Social');
            listOfType.add(InteractionType1);
            
            Storefront_I_Type__c InteractionType2 = new Storefront_I_Type__c(Name ='Test');
            listOfType.add(InteractionType2);
            
            insert listOfType;
            System.assertEquals(listOfType.size() , 2);
            
            List<Storefront_I_Reason__c> listOfReason = new List<Storefront_I_Reason__c>();
            
            Storefront_I_Reason__c InteractionReason1 = new Storefront_I_Reason__c(Name = 'Social-Cafe' ,Interaction_Type__c = listOfType[0].Id);
            listOfReason.add(InteractionReason1);
            
            Storefront_I_Reason__c InteractionReason3 = new Storefront_I_Reason__c(Name = 'Social-Cafe2' ,Interaction_Type__c = listOfType[0].Id);
            listOfReason.add(InteractionReason3);
            
            Storefront_I_Reason__c InteractionReason2 = new Storefront_I_Reason__c(Name = 'Test2' ,Interaction_Type__c = listOfType[1].Id);
            listOfReason.add(InteractionReason2); 
            
            insert listOfReason;
            system.assertEquals(listOfReason.size() , 3);            
            
            List<Event> lstTestEvents = new List<Event>();
            
            Event event1 = CRMRetail_TestDataFactory_H_HUM.createEvent(listOfReason[0].Id,listOfLocation[0].Id ,system.Now().addHours(4) ,system.Now().addHours(5),system.today(),false,'Busy',0);
            lstTestEvents.add(event1); 
            
            Event event2 = CRMRetail_TestDataFactory_H_HUM.createEvent(listOfReason[0].Id,listOfLocation[1].Id , system.Now().addHours(4) ,system.Now().addHours(5),system.today(),false,'Busy',1);
            lstTestEvents.add(event2);  
            
            Event event3 = CRMRetail_TestDataFactory_H_HUM.createEvent(listOfReason[1].Id,listOfLocation[0].Id ,system.Now().addHours(4) , system.Now().addHours(6),system.today(),false,'Busy',0);             
            lstTestEvents.add(event3); 
            
            Event event4 = CRMRetail_TestDataFactory_H_HUM.createEvent(listOfReason[2].Id, listOfLocation[1].Id ,system.Now().addHours(1) , system.Now().addHours(6),system.today(),false, 'Busy',0);                      
            lstTestEvents.add(event4); 
            
            Event event5 = CRMRetail_TestDataFactory_H_HUM.createEvent(listOfReason[1].Id, listOfLocation[2].Id,system.Now().addHours(1) ,  system.Now().addHours(6),system.today(),false, 'Busy',0);
            lstTestEvents.add(event5);  
            
            Event event6 = CRMRetail_TestDataFactory_H_HUM.createEvent(listOfReason[2].Id, listOfLocation[0].Id,system.Now().addHours(1) , system.Now().addHours(6),system.today(),false, 'Busy',0);
            lstTestEvents.add(event6);         
            
            insert lstTestEvents;
            system.assertEquals(lstTestEvents.size(), 6);                
            
            List<CRMFunctionality_ONOFF_Switch__c> listOfSwitch = new List<CRMFunctionality_ONOFF_Switch__c>();            
            CRMFunctionality_ONOFF_Switch__c switch_4662486 = new CRMFunctionality_ONOFF_Switch__c(Name = '4662486',IsON__c=true);
            listOfSwitch.add(switch_4662486);
            
            insert listOfSwitch;
            
            system.assertEquals(1,listOfSwitch.size());
            
            HUMTriggerSwitch__c objTrigswitch = new HUMTriggerSwitch__c ();
            objTrigswitch.name ='Event';
            objTrigswitch.Exeute_Trigger__c =true;
            insert objTrigswitch;
            system.assertEquals(objTrigswitch.name,'Event');
        }
    }
    /**
    *  This method tests the insert,update logic on Event object 
    *  @name testCheckEventAttendanceWithNonAdmins
    *  @param NA
    *  @return NA
    *  @throws exception - Checks for all Events when updating a record if the user has the required permission
    *  @see 
    */
    @isTest static void testCheckEventAttendanceWithNonAdmins()
    {
        List<User> lstUsers = [select Id,Name from User where name = 'test User2' limit 1]; 
        Assert.areEqual(1,lstUsers.size());                        
                
        system.runAs(lstUsers[0])
        {    
            Test.startTest();
            Storefront_I_Reason__c sfRsn  = [SELECT Id,Name,CreatedById from Storefront_I_Reason__c where Name='Social-Cafe' limit 1];
            Assert.areEqual('Social-Cafe', sfRsn.Name);
            
            Event eve = [SELECT Id,Scheduled_Onsite_Attendee_Count__c,CreatedById from Event where CreatedById=: lstUsers[0].Id and Reason__c=:sfRsn.Id limit 1];
            Assert.areEqual(lstUsers[0].Id,eve.CreatedById);
            
            Storefront_Location__C sfLoc = [SELECT Id,Name from Storefront_Location__c where CreatedById=:lstUsers[0].Id and Name='Biloxl'];
            Assert.areEqual('Biloxl', sfLoc.Name);            
            try{  
                eve.Scheduled_Onsite_Attendee_Count__c = 1;
                update eve; 
            }
            catch(DMLException e) 
            {     
                Assert.isTrue(e.getMessage().contains(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_EVT_ATTND_UPDATE_ERROR_MSG));
            }                         
            try{
                eve = CRMRetail_TestDataFactory_H_HUM.createEvent(sfRsn.Id, sfLoc.Id,system.Now().addHours(1) , system.Now().addHours(6),system.today(),false, 'Busy',0);
                eve.Scheduled_Onsite_Attendee_Count__c = 1;
                eve.Known_Attendee_Count__c = 1;
                insert eve;
            }
            catch(DMLException e) 
            { 
                Assert.isTrue(e.getMessage().contains(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_EVT_ATTND_UPDATE_ERROR_MSG));
            }  
        }        
    }
    /**
    *  This method tests the insert logic on Event object 
    *  @name testCheckEventAttendanceWithAdmins
    *  @param NA
    *  @return NA
    *  @see 
    */        
    @isTest static void testCheckEventAttendanceWithAdmins()
    {
        List<User> lstUsers = [select Id,Name from User where name = 'test User1' limit 1]; 
        Assert.areEqual(1,lstUsers.size()); 
        
        System.runAs(lstUsers[0]){ 
            Test.startTest();
            Storefront_I_Reason__c sfRsn  = [SELECT Id,Name,CreatedById from Storefront_I_Reason__c where Name='Social-Cafe' limit 1];
            Assert.areEqual('Social-Cafe', sfRsn.Name);                                    
            
            Event eve = [SELECT Id,Scheduled_Onsite_Attendee_Count__c,CreatedBy.Name from Event where CreatedBy.Name= 'test User2' and Reason__c=:sfRsn.Id limit 1];
            Assert.areEqual('test User2',eve.CreatedBy.Name);
            eve.Scheduled_Onsite_Attendee_Count__c = 1;
            eve.Known_Attendee_Count__c = 1;
            update eve; 
            
            List<Event> lstEve = [SELECT Id,Reason__r.Name from Event where Scheduled_Onsite_Attendee_Count__c > 0 and Known_Attendee_Count__c>0];                
            Assert.areEqual(1,lstEve.size()); 
            Assert.areEqual('Social-Cafe',lstEve[0].Reason__r.Name);            
            Test.stopTest();
        }   
    }
    /**
    *  This method tests the insert logic on Event object 
    *  @name testCheckForLocationOnInsert
    *  @param NA
    *  @return NA
    *  @throws exception - Checks for all Events when inserting a record and throws an exception if inactive Location selected.
    *  @see 
    */
    static testMethod void testCheckForLocationOnInsert()
    {
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        system.assertEquals(oUser.Name,'test User1');
        
        system.runAs(oUser)
        { 
            Storefront_Location__c location1 = new Storefront_Location__c(Name = 'Atlanta',Address_Line_1__c = '2341',City__c='Atlanta',Zip_Code__c='23143',Org_Facility__c='51466',State__c='Atlanta',Inactive_indicator__c = true);
            insert location1;
            system.assertEquals(location1.Name,'Atlanta');            
            
            Storefront_I_Type__c InteractionType1 = new Storefront_I_Type__c(Name ='Social123');
            insert InteractionType1;
            system.assertEquals(InteractionType1.Name,'Social123');
                
            Storefront_I_Reason__c InteractionReason1 = new Storefront_I_Reason__c(Name = 'Social-Cafe12' ,Interaction_Type__c = InteractionType1.Id);
            insert InteractionReason1;
            system.assertEquals(InteractionReason1.Name,'Social-Cafe12');
           
            Event event2 = new Event(Reason__c= InteractionReason1.Id, WhatId= location1.Id ,StartDateTime = system.Now().addHours(4) , EndDateTime =  system.Now().addHours(5), ShowAs = 'Busy');
            insert event2; 
            system.assertEquals(event2.Reason__c,InteractionReason1.Id);
            
            Event event1 = new Event(Reason__c= InteractionReason1.Id, WhatId= location1.Id ,StartDateTime = system.Now().addHours(4) , EndDateTime =  system.Now().addHours(5), ShowAs = 'Busy');
            
            try 
            {
              insert event1;
              system.assertEquals(event1.Reason__c,InteractionReason1.Id);
            }
            catch(DMLException e) 
            {
                system.assert(e.getMessage().contains(System.Label.INACTIVE_LOCATION_CRMRETAIL_HUM)); 
            } 
        }
    }
    
    /**
    *  This method tests the update logic on event object 
    *  @name testCheckForLocationOnUpdate
    *  @param NA
    *  @return NA
    *  @throws exception - Checks for all Events when updating a record and throws an exception if inactive Location selected.
    *  @see 
    */
    static testMethod void testCheckForLocationOnUpdate()
    {
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        system.assertEquals(oUser.Name,'test User1');
        
        system.runAs(oUser)
        { 
             Storefront_Location__c location = new Storefront_Location__c(Name = 'TestLocation',Address_Line_1__c = '9008',City__c='TestCity',Zip_Code__c='40555',Org_Facility__c='12345',State__c='Test - TA');
             insert location;
             system.assertEquals(location.Name,'TestLocation');
             
             Storefront_Location__c location1 = new Storefront_Location__c(Name = 'Atlanta',Address_Line_1__c = '2341',City__c='Atlanta',Zip_Code__c='23143',Org_Facility__c='51466',State__c='Atlanta',Inactive_indicator__c = true);
             insert location1;
             system.assertEquals(location1.Name,'Atlanta');
            
             Storefront_I_Type__c InteractionType1 = new Storefront_I_Type__c(Name ='Social12');
             insert InteractionType1;
             system.assertEquals(InteractionType1.Name,'Social12');
                
             Storefront_I_Reason__c InteractionReason1 = new Storefront_I_Reason__c(Name = 'Social-Cafe12' ,Interaction_Type__c = InteractionType1.Id);
             insert InteractionReason1;
             system.assertEquals(InteractionReason1.Name,'Social-Cafe12');
            
             Event event = new Event(Reason__c= InteractionReason1.Id, WhatId= location.Id ,StartDateTime = system.Now().addHours(4) , EndDateTime =  system.Now().addHours(5), ShowAs = 'Busy');
             insert event; 
             system.assertEquals(event.Reason__c,InteractionReason1.Id);
             
             event.whatid = location1.Id;
             try 
             {
             	update event;
                system.assertEquals(event.whatid,location1.Id);
             }
              catch(DMLException e) 
              {
                 system.assert(e.getMessage().contains(System.Label.INACTIVE_LOCATION_CRMRETAIL_HUM)); 
              } 
        }
    }	
    /**
    *  This method tests the Delete logic on Event object 
    *  @name testCheckForDelete
    *  @param NA
    *  @return NA
    *  @throws exception - Checks for the interaction when deleting a record and throws an exception if interaction exist for that event.
    *  @see 
    */
    static testMethod void testCheckForDelete()
    {
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        system.assertEquals(oUser.Name, 'test User1');               
        
        system.runAs(oUser) 
        {          	                   
             Storefront_I_Type__c InteractionType1 = [select Name from Storefront_I_Type__c where Name = 'Social'];
             system.assertEquals(InteractionType1.Name,'Social');
             
             Storefront_I_Reason__c InteractionReason1 = [Select Name,Interaction_Type__c from Storefront_I_Reason__c where Name='Social-Cafe'];
             system.assertEquals(InteractionReason1.Name,'Social-Cafe');
             
             Storefront_Location__c location1 =[select Name,Address_Line_1__c,City__c,Zip_Code__c,Org_Facility__c,State__c,Inactive_indicator__c from Storefront_Location__c where Name='Biloxl'];
             system.assertEquals(location1.Name,'Biloxl');
             
             Storefront_Location__c location2 =[select Name,Address_Line_1__c,City__c,Zip_Code__c,Org_Facility__c,State__c,Inactive_indicator__c from Storefront_Location__c where Name='Virginia'];             
             system.assertEquals(location2.Name,'Virginia'); 
             
             Event event2 = [select Id,Reason__c,Scheduled_Total_Attendance__c,Total_Attendance_Count__c from Event where WhatId =: location2.Id AND Reason__c =: InteractionReason1.Id];             
             system.assertEquals(event2.Reason__c,InteractionReason1.Id);                          
             
             try
             {                        
                delete event2;                            
             	system.assertEquals(event2.Reason__c,InteractionReason1.Id);              
             }
             catch(DMLException e) 
             {
                 system.assertNotEquals(e.getMessage(),NULL); 
             }
        }
    }
    /**
    *  This method tests the Insert logic on Event object 
    *  @name checkForDuplicateOnInsertAndUpdate
    *  @param NA
    *  @return NA
    *  @throws exception - Checks for the duplicate when inserting a record and throws an exception if that interaction details already exist.
    *  @see  
    */
    static testMethod void testCheckForDuplicate()
    {
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        system.assertEquals(oUser.Name, 'test User1');
        
        system.runAs(oUser) 
        {          	 
             Storefront_I_Type__c InteractionType1 = [select Name from Storefront_I_Type__c where Name = 'Social'];
             system.assertEquals(InteractionType1.Name,'Social');
             
             Storefront_I_Reason__c InteractionReason1 = [Select Name,Interaction_Type__c from Storefront_I_Reason__c where Name='Social-Cafe'];
             system.assertEquals(InteractionReason1.Name,'Social-Cafe');
             
             Storefront_Location__c location1 =[select Name,Address_Line_1__c,City__c,Zip_Code__c,Org_Facility__c,State__c,Inactive_indicator__c from Storefront_Location__c where Name='Biloxl'];
             system.assertEquals(location1.Name,'Biloxl');
             
             Storefront_Location__c location2 =[select Name,Address_Line_1__c,City__c,Zip_Code__c,Org_Facility__c,State__c,Inactive_indicator__c from Storefront_Location__c where Name='Virginia'];
             system.assertEquals(location2.Name,'Virginia'); 
             
             Event event2 = [select Reason__c,WhatId,isAllDayEvent from Event where WhatId =: location1.Id AND Reason__c =: InteractionReason1.Id];
             system.assertEquals(event2.Reason__c,InteractionReason1.Id);  
             
             Event event4 = [select Reason__c,WhatId,isAllDayEvent from Event where WhatId =: location1.Id AND Reason__c =: InteractionReason1.Id];
             system.assertEquals(event4.Reason__c,InteractionReason1.Id);  
             
             try 
             {             	 
                 event2.WhatId = location2.Id;                  
                 update event2;
                 system.assertEquals(event2.WhatId,location2.Id);
              
                 event4.WhatId = location2.Id;
                 event4.Reason__c = InteractionReason1.Id;
                 update event4;                 
                 system.assertEquals(event4.Reason__c,InteractionReason1.Id);                 
             }
             catch(Exception e)
             {
             	system.assertNotEquals(e.getMessage(),NULL);	    
             }
        }
    } 
    
    static testMethod void testCheckForDuplicateOnUpdate()
    {
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        system.assertEquals(oUser.Name, 'test User1');
        
        system.runAs(oUser) 
        { 
             Storefront_I_Type__c InteractionType1 = [select Name from Storefront_I_Type__c where Name = 'Social'];
             system.assertEquals(InteractionType1.Name,'Social');
             
             Storefront_I_Reason__c InteractionReason1 = [Select Name,Interaction_Type__c from Storefront_I_Reason__c where Name='Social-Cafe'];
             system.assertEquals(InteractionReason1.Name,'Social-Cafe');
             
             Storefront_Location__c location1 =[select Name,Address_Line_1__c,City__c,Zip_Code__c,Org_Facility__c,State__c,Inactive_indicator__c from Storefront_Location__c where Name='Biloxl'];
             system.assertEquals(location1.Name,'Biloxl');
             
             Event event2 = [select Reason__c,WhatId,isAllDayEvent from Event where WhatId =: location1.Id AND Reason__c =: InteractionReason1.Id];
             system.assertEquals(event2.Reason__c,InteractionReason1.Id);  
             
             Event event4 = [select Reason__c,WhatId,isAllDayEvent from Event where WhatId =: location1.Id AND Reason__c =: InteractionReason1.Id];
             system.assertEquals(event4.Reason__c,InteractionReason1.Id);
             
             try
             {  
                 event2.Reason__c = InteractionReason1.Id;
                 update event2; 
                 system.assertEquals(event2.Reason__c,InteractionReason1.Id);
                
                 event4.isAllDayEvent = true;
                 update event4; 
                 system.assertEquals(event4.isAllDayEvent,true);
             }
             catch(DMLException e)
             {
               	system.assertNotEquals(e.getMessage(),NULL);   
			 }
        }
    } 
    /**
    *  This method tests the Udapte logic on Event object 
    *  @name testCheckForUpdate
    *  @param NA
    *  @return NA
    *  @throws exception - Checks for interaction when editing a record and throws an exception.
    *  @see 
    */
    static testMethod void testCheckForUpdate()
    {
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.assertEquals(oUser.Name, 'test User1');
       
        System.runAs(oUser) 
        {          	
             Storefront_I_Type__c InteractionType1 = [select Name from Storefront_I_Type__c where Name = 'Social'];
             system.assertEquals(InteractionType1.Name,'Social');             
             
             Storefront_I_Reason__c InteractionReason1 = [Select Name,Interaction_Type__c from Storefront_I_Reason__c where Name='Social-Cafe'];
             system.assertEquals(InteractionReason1.Name,'Social-Cafe');
             
             Storefront_Location__c location1 =[select Name,Address_Line_1__c,City__c,Zip_Code__c,Org_Facility__c,State__c,Inactive_indicator__c from Storefront_Location__c where Name='Biloxl'];
             system.assertEquals(location1.Name,'Biloxl');
             
             Storefront_I_Reason__c InteractionReason2 = [Select Name,Interaction_Type__c from Storefront_I_Reason__c where Name='Social-Cafe2'];                                                             
             system.assertEquals(InteractionReason2.Name,'Social-Cafe2');
             
             Event event2 = [select Scheduled_Onsite_Attendee_Count__c,Reason__c,WhatId,isAllDayEvent from Event where WhatId =: location1.Id AND Reason__c =: InteractionReason1.Id];
             system.assertEquals(event2.Reason__c,InteractionReason1.Id);     
             
             try
             {
                  event2.Reason__c = InteractionReason2.Id;                   
                  event2.Scheduled_Onsite_Attendee_Count__c=1;
                  update event2;      
                  system.assertEquals(event2.Reason__c,InteractionReason2.Id);
             }
             catch(DMLException e) 
             {
                 system.assertNotEquals(e.getMessage(),NULL); 
             }
        }
    } 
    
    /**
    * Returns void
    * <p>
    * This method is used for validating Virtual Location Check method. 
    *
    * @return  Void
    * @see
    */
    static testMethod void testvirtualLocationChecks()
    {
        User  oUser = [select Id,Name from User where name = 'test User2' limit 1];                
        system.assertEquals(oUser.Name, 'test User2');
       
        System.runAs(oUser) 
        {          	
             Storefront_I_Type__c InteractionType1 = [select Name from Storefront_I_Type__c where Name = 'Social'];
             system.assertEquals(InteractionType1.Name,'Social');             
             
             Storefront_I_Reason__c InteractionReason1 = [Select Name,Interaction_Type__c from Storefront_I_Reason__c where Name='Test2'];
             system.assertEquals(InteractionReason1.Name,'Test2');
            
             Storefront_Location__c location3 = new Storefront_Location__c(Name = 'Virtual HNC',Address_Line_1__c = '3457',City__c='Virtual HNC',Zip_Code__c='45678',Org_Facility__c='45321',State__c='Atlanta',Inactive_indicator__c = false, Location_Type__c = 'Virtual');
             insert location3;
             
             Storefront_I_Reason__c InteractionReason2 = [Select Name,Interaction_Type__c from Storefront_I_Reason__c where Name='Social-Cafe2'];                                                             
             system.assertEquals(InteractionReason2.Name,'Social-Cafe2');
            
             test.startTest();
             try
             {
                 Event eventRef=new Event();
                 eventRef.Reason__c = InteractionReason1.Id;
                 eventRef.WhatId = location3.Id;
                 eventRef.startDateTime = system.Now().addHours(4); 
                 eventRef.endDateTime = system.Now().addHours(5);
                 eventRef.activityDate = system.today();
                 eventRef.isRecurrence = false;
                 eventRef.showAs = 'Busy';
                 eventRef.Scheduled_Onsite_Attendee_Count__c = 0; 
                 insert eventRef;
             }
            catch(Exception e)
            {
                System.assert(e.getMessage().contains(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_VIRTUAL_LOC_INELIGIBILITY_MSG));
            }
            
            
            Event event2 = [select Scheduled_Onsite_Attendee_Count__c,Reason__c,WhatId,isAllDayEvent from Event where Reason__c =: InteractionReason1.Id Limit 1];
            system.assertEquals(event2.Reason__c,InteractionReason1.Id);    
            
            try{
                event2.WhatId = location3.Id;
                update event2;                
            } 
            catch(Exception e)
            {                
                System.assert(e.getMessage().contains(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_VIRTUAL_LOC_INELIGIBILITY_MSG));
            }
            test.stopTest();           
        }
    }
}