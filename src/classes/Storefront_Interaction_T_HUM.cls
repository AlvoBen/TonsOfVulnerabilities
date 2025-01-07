/*******************************************************************************************************************************
Apex Class Name     : Storefront_Interaction_T_HUM 
Version             : 1.0
Created Date        : May 22 2020
Function            : This serves as Test class for Storefront Interaction Trigger.

Modification Log: 
* Developer Name           Code Reveiw                     Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Melkisan Selvaraj                                      05/22/2020                 Initial Version
* Luke P. Cecil 	                                     08/19/2020	                User Story 1243103: PR00091869 - MF 11 - CRM Storefront Virtual Visitors: New Virtual Non-Member Visitor
* Abhishek Maurya								         02/17/2021					Production fix - Duplicate check
* Ekta Bist												 07/16/2021					US-2360057 CRM Storefront VNC: Restrict Access to Virtual Locations
* Ekta Bist                                              12/13/2021                 User Story 2792824: T1PRJ0001894 MF7 Storefront Engagement: Go365 Eligible Interaction Reason Hover
* Ekta Bist                                              12/22/2021                 User Story 2789307: T1PRJ0001894 MF7 Storefront Engagement: Account Hover
* Vivek Sharma											 01/13/2022					User Story 2792815: T1PRJ0154546 MF9 Storefront Systematically Remove Notification & SDOH Records When User Deletes an Interaction
* Vivek Sharma                                           02/15/2022                 User Story 2940968: T1PRJ0154546 MF9 Storefront Home Page: Missing 'Event' Interactions
* Ekta Bist                                              03/07/2022                 User Story 3153697 - T1PRJ0154546 MF10 Storefront - Update 'Reason for Visit' Picklist Values on Health Educator 1x1 Tracking Form 
* Anil parvathaneni                                      26/06/2022                 User Story 3252318 - T1PRJ0272555  - Code Coverage issue Resolution - Test Classes  
* Vivek Sharma                                           06/28/2022                 User Story 3450197: T1PRJ0154546 / SF / MF9 Storefront Modernization (Business Admin/Super Admin) - Home (Right Side) 
* Vinoth L                                           	 12/13/2022                 User Story 4046005: T1PRJ0154546 / SF / MF11 Storefront Interaction Duplicate Logic Update
* Vinoth L												 06/24/2023				    User Story 4769775: - T1PRJ0154546 / SF / MF9 Storefront - Carry Forward First Time Visitor to Event Interactions
*******************************************************************************************************************************/
@isTest
private class Storefront_Interaction_T_HUM 
{
	/*
    * This method creates test data for test class
    * @param None
    * @return None
    */
    @testSetup static void setUpTestData() 
    {
        List<User> listOfUserToInsert = new List<User>();
        
        User objAdminUser = HUMTestDataHelperTest.createTestUser('UserRetail','system Administrator');
        listOfUserToInsert.add(objAdminUser);
        
        User objCRMRetailUser = HUMTestDataHelperTest.createTestUser('UserCRMRetail','CRMRetail User');
        listOfUserToInsert.add(objCRMRetailUser);
        
        insert listOfUserToInsert;
        system.assertEquals(listOfUserToInsert.size() , 2);
           
        system.runAs(objCRMRetailUser)
        {                      
            Id oCRMRetailVisitorType =  Schema.SObjectType.Account.getRecordTypeInfosByName().get('CRMRetail Visitor').getRecordTypeId();
            Date waiverDate = Date.today() - 100;        
            Account oAcc = CRMRetail_TestDataFactory_H_HUM.createAccountInstance('987654321', oCRMRetailVisitorType,Date.newInstance(1984, 01, 01),'01/01/1984', 'Daphiner', 'Superman', 'J', '1060920019',false, 'New York', '56605', '(123) 123-1231','test1234',waiverDate,'confirmed-veteran',NULL);
            insert oAcc;
            system.assertNotEquals(oAcc, Null);            
        }
        system.runas(objAdminUser) 
        {                          
            Id oMemRecType = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            
            List<Account> listOfAccountToInsert = new List<Account>(); 
            Account oAcc1 =CRMRetail_TestDataFactory_H_HUM.createObjAccount(oMemRecType,'04/06/1985','Johnathon','Smith','12209','JSmith@gmail.com','','arizona',true);            
            oAcc1.GCM_Visitor_Barcode__c = '987654321'; 
            oAcc1.veteran_status__c='confirmed-veteran';
            
            Id recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            
            Account oAcc = HUMTestDataHelperTest.getAccount();
            oAcc.OwnerId = listOfUserToInsert[0].id;
            oAcc.FirstName = 'Test';
            oAcc.LastName = 'Account';
            oAcc.Enterprise_Id__c = '123456';
            oAcc.recordtypeid = recordTypeId;
            listOfAccountToInsert.add(oAcc) ;    
            
            Account oAcc5 = HUMTestDataHelperTest.getAccount();
            oAcc5.OwnerId = listOfUserToInsert[0].id;
            oAcc5.FirstName = 'Test';
            oAcc5.LastName = 'Account1';
            oAcc5.Enterprise_Id__c = '123456';
            oAcc5.recordtypeid = recordTypeId;
            oAcc5.Waiver_Date__c = Date.newInstance(2007,01,10);
            listOfAccountToInsert.add(oAcc5);  
                        
            Account oAcc2 =CRMRetail_TestDataFactory_H_HUM.createObjAccount(oMemRecType,'04/06/1985','Test','Account1','12209','TAccount@gmail.com','','arizona',true);            
            oAcc2.GCM_Visitor_Barcode__c = '987654323';   
            oAcc2.veteran_status__c='confirmed-veteran';
            oAcc2.Enterprise_Id__c = '123456';
            oAcc2.Waiver_Date__C = system.today();
            listOfAccountToInsert.add(oAcc2); 
            
            Account oAcc3 =CRMRetail_TestDataFactory_H_HUM.createObjAccount(oMemRecType,'04/06/1985','Author','Ben','12209','TAccount@gmail.com','','arizona',true);           
            oAcc3.GCM_Visitor_Barcode__c = '987654322';   
            oAcc3.veteran_status__c='confirmed-veteran';
            oAcc3.Enterprise_Id__c = '123456';
            listOfAccountToInsert.add(oAcc3); 
            
            Account oAcc4 =CRMRetail_TestDataFactory_H_HUM.createObjAccount(oMemRecType,'04/06/1985','Zak','Crawley','12209','ZCrawley@gmail.com','','arizona',true);            
            oAcc4.GCM_Visitor_Barcode__c = '987654324';   
            oAcc4.veteran_status__c='confirmed-veteran';
            oAcc4.Enterprise_Id__c = '123456';
            listOfAccountToInsert.add(oAcc4);
            
            insert listOfAccountToInsert;             
            system.assertEquals(listOfAccountToInsert.get(1).id != null, true);
            system.assertEquals(listOfAccountToInsert.get(1).FirstName, 'Test', 'Value not matched');
            
            List<Storefront_Location__c> listOfLocations = new List<Storefront_Location__c>();
            
            Storefront_Location__c location1 = CRMRetail_TestDataFactory_H_HUM.createLocation('Virtual HNC','Tampa','Tampa','66545','66545','',30);
            location1.Location_Type__c = GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_VIRTUAL;
            listOfLocations.add(location1);
            
            Storefront_Location__c location2 = CRMRetail_TestDataFactory_H_HUM.createLocation();
            listOfLocations.add(location2);
            
            insert listOfLocations; 
            system.assertEquals(listOfLocations.size() , 2);
            
            Storefront_Preference__c defLocation1 = new Storefront_Preference__c(Default_Location__c = location1.Id, User__c = listOfUserToInsert[0].id);
            insert defLocation1;
            system.assertEquals(defLocation1.User__c, listOfUserToInsert[0].id);
            
            Storefront_I_Type__c interactionType1 = new Storefront_I_Type__c(Name ='Social');
            insert interactionType1;
            system.assertEquals(interactionType1.Name , 'Social');
            
            List<Storefront_I_Reason__c> listOfReasons = new List<Storefront_I_Reason__c>();
            
            Storefront_I_Reason__c interactionReason1 = new Storefront_I_Reason__c(Name = 'Social-Cafe' ,Interaction_Type__c = InteractionType1.Id);
            listOfReasons.add(interactionReason1);
           
            Storefront_I_Reason__c interactionReason2 = new Storefront_I_Reason__c(Name = 'Visitor Check-In (1st Time)' ,Interaction_Type__c = InteractionType1.Id);
            listOfReasons.add(interactionReason2);
            
            Storefront_I_Reason__c interactionReason3 = new Storefront_I_Reason__c(Name = 'Health Educator' ,Interaction_Type__c = InteractionType1.Id);
            listOfReasons.add(interactionReason3);

            Storefront_I_Reason__c interactionReason4 = new Storefront_I_Reason__c(Name = 'Waiver Required' ,Interaction_Type__c = InteractionType1.Id);
            listOfReasons.add(interactionReason4);
            
            insert listOfReasons;          
            system.assertEquals(listOfReasons.size() , 4);            
            
            List<Storefront_Interaction__c> listInteractions = new List<Storefront_Interaction__c>();
            
            Storefront_Interaction__c gcmInteractions1 = new Storefront_Interaction__c(Account__c = listOfAccountToInsert[0].Id , Location__c = listOfLocations[0].Id,Reason__c = listOfReasons[0].Id, IsAllDayEvent__c = true, CRM_Retail_Interaction_Visitor_Type__c = 'Member', Interaction_Date__c = Date.Today());
            listInteractions.add(gcmInteractions1);
            
            Storefront_Interaction__c gcmInteractions2 = new Storefront_Interaction__c(Account__c = listOfAccountToInsert[0].Id , Location__c = listOfLocations[0].Id,Reason__c = listOfReasons[1].Id, IsAllDayEvent__c = true, CRM_Retail_Interaction_Visitor_Type__c = 'Member', Interaction_Date__c = Date.Today());
            listInteractions.add(gcmInteractions2);
            
            Storefront_Interaction__c gcmInteractions3 = new Storefront_Interaction__c(Account__c = listOfAccountToInsert[0].Id , Location__c = listOfLocations[0].Id,Reason__c = listOfReasons[3].Id, IsAllDayEvent__c = true, CRM_Retail_Interaction_Visitor_Type__c = 'Member',Category__c = 'Notification', Interaction_Date__c = Date.Today());
            listInteractions.add(gcmInteractions3);
            
            Storefront_Interaction__c gcmInteractions4 = new Storefront_Interaction__c(Account__c = listOfAccountToInsert[0].Id , Location__c = listOfLocations[0].Id,Reason__c = listOfReasons[2].Id, IsAllDayEvent__c = true, CRM_Retail_Interaction_Visitor_Type__c = 'Member', Interaction_Date__c = Date.Today());
            listInteractions.add(gcmInteractions4);
            
            Storefront_Interaction__c gcmInteractions5 = new Storefront_Interaction__c(Account__c = listOfAccountToInsert[4].Id , Location__c = listOfLocations[0].Id,Reason__c = listOfReasons[1].Id, IsAllDayEvent__c = false, CRM_Retail_Interaction_Visitor_Type__c = 'Member', Interaction_Date__c = Date.Today()-10);
            listInteractions.add(gcmInteractions5);
            
            Storefront_Interaction__c gcmInteractions6 = new Storefront_Interaction__c(Account__c = listOfAccountToInsert[3].Id , Location__c = listOfLocations[0].Id,Reason__c = listOfReasons[0].Id, IsAllDayEvent__c = false, CRM_Retail_Interaction_Visitor_Type__c = 'Member', Interaction_Date__c = Date.Today()-10, Storefront_Event_Starttime__c = Date.Today()-10);
            listInteractions.add(gcmInteractions6);
            
            insert listInteractions;
            assert.areEqual(listInteractions.size(), 6);
            
            CRMRetail_SDoHDetermt__c determntObj = new CRMRetail_SDoHDetermt__c(Account__c = listOfAccountToInsert[0].Id, Name = 'Test');
            insert determntObj;
            system.assertEquals(determntObj.Account__c, listOfAccountToInsert[0].Id);        
                        
            
            CRMRetail_OOO_Tracking__c trackingObj = HUMTestDataHelperTest.getHealthEducator();
            trackingObj.Account__c = listOfAccountToInsert[0].Id;
			trackingObj.CRM_Retail_Interaction__c = gcmInteractions1.Id;
            trackingObj.CRM_Retail_Location__c = listOfLocations[0].Id;
            insert trackingObj;
            system.assertEquals(trackingObj.Reason_for_Visit__c, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_REASONTOVISIT );       
            
            HUMTriggerSwitch__c triggerSwitch = new HUMTriggerSwitch__c(Name = 'Storefront_Interaction__c', Exeute_Trigger__c = true);
            insert triggerSwitch;
            system.assertEquals(triggerSwitch.Exeute_Trigger__c, true);
            
            Attendance__c att = CRMRetail_TestDataFactory_H_HUM.createAttendance(location1.Id, system.Today());
            insert att;
            system.assertEquals(att.Visitors_Today__c, 1);
            
            List<CRMFunctionality_ONOFF_Switch__c> listOfSwitch = new List<CRMFunctionality_ONOFF_Switch__c>();
            
            CRMFunctionality_ONOFF_Switch__c switch_1418673 = new CRMFunctionality_ONOFF_Switch__c(Name = '1418673',IsON__c=true);
        	listOfSwitch.add(switch_1418673);
            
            CRMFunctionality_ONOFF_Switch__c switch_2360057 = new CRMFunctionality_ONOFF_Switch__c(Name = '2360057',IsON__c=true);
        	listOfSwitch.add(switch_2360057);
            
            CRMFunctionality_ONOFF_Switch__c switch_2792824 = new CRMFunctionality_ONOFF_Switch__c(Name = '2792824',IsON__c=true);
        	listOfSwitch.add(switch_2792824);
            
            CRMFunctionality_ONOFF_Switch__c switch_2789307 = new CRMFunctionality_ONOFF_Switch__c(Name = '2789307',IsON__c=true);
        	listOfSwitch.add(switch_2789307);
            
            CRMFunctionality_ONOFF_Switch__c Switch_2792815 = new CRMFunctionality_ONOFF_Switch__c(Name = '2792815',IsON__c=true);
        	listOfSwitch.add(Switch_2792815);
            
            CRMFunctionality_ONOFF_Switch__c Switch_2940968 = new CRMFunctionality_ONOFF_Switch__c(Name = '2940968',IsON__c=true);
        	listOfSwitch.add(Switch_2940968);

			CRMFunctionality_ONOFF_Switch__c Switch_4769775 = new CRMFunctionality_ONOFF_Switch__c(Name = '4769775',IsON__c=true);
        	listOfSwitch.add(Switch_4769775);            
            insert listOfSwitch;
            system.assertEquals(listOfSwitch.size() ,7); 
        }         
    }
    /**
    * Returns void
    * <p>
    * This method is used for validating checkFirstTimeVisit method.     
    * @return  Void
    * @see
    */
    @isTest static void testCheckFirstTimeVisit()
    {
        User testUser = [SELECT Id, Name FROM User WHERE LastName = 'test UserRetail'];
        assert.areEqual(testUser.Name , 'test UserRetail');

        system.runAs(testUser)
        {            
            test.startTest();
            List<Storefront_I_Reason__c> lstReasons = [SELECT Id from Storefront_I_Reason__c where Name='Social-Cafe'];
            List<Storefront_Location__c> lstLocations = [SELECT Id from Storefront_Location__c where Name='Virtual HNC'];
            List<Account> lstAccount = [SELECT Id from Account where FirstName='Zak'];
            
            List<Storefront_Interaction__c> lstStfInts = [SELECT Id from Storefront_Interaction__c where (Category__c ='' OR Category__c =: GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_VIRTUAL) AND Storefront_Event_Starttime__c != NULL AND Account__c =: lstAccount[0].Id order by Interaction_Date__c];
            assert.areEqual(0, lstStfInts.size());
            Storefront_Interaction__c gcmInteractions1 = new Storefront_Interaction__c(Account__c = lstAccount[0].Id,Location__c = lstLocations[0].Id,Reason__c = lstReasons[0].Id, IsAllDayEvent__c = false, CRM_Retail_Interaction_Visitor_Type__c = 'Member', Interaction_Date__c = Date.Today()-1, Storefront_Event_Starttime__c = Date.today());            
            insert gcmInteractions1; 
            List<Storefront_Interaction__c> lstsfInt = [SELECT Id,Account__c,First_Time_Visitor__c from Storefront_Interaction__c where Account__c=: lstAccount[0].Id order by Interaction_Date__c desc limit 1];                        
            assert.areEqual(true, lstsfInt[0].First_Time_Visitor__c);
            test.stopTest();
        }
    }
    /**
    * Returns void
    * <p>
    * This method is used for validating processBeforeDelete method. 
    *
    * @return  Void
    * @see
    */
    @isTest static void testprocessBeforeDelete()
    {
        User testUser = [SELECT Id, Name FROM User WHERE LastName = 'test UserRetail'];
        system.assertEquals(testUser.Name , 'test UserRetail');

        system.runAs(testUser)
        {            
            test.startTest();
            Storefront_Interaction__c intObj = [Select Id, IsAllDayEvent__c FROM Storefront_Interaction__c where IsAllDayEvent__c = true LIMIT 1];
            system.assertEquals(intObj.IsAllDayEvent__c, true);
            delete intobj;

            Account accObj = [SELECT Id, Name FROM Account WHERE FirstName = 'Test' AND LastName = 'Account' LIMIT 1];
            system.assertNotEquals(accObj.Id, null);
            List<Storefront_Interaction__c> interactionList = [Select Id, IsAllDayEvent__c FROM Storefront_Interaction__c where IsAllDayEvent__c = true AND Category__c != 'Notification' LIMIT 3];
            
            system.assertEquals(interactionList.size(), 2);
            delete interactionList;
            
            List<Storefront_Interaction__c> interactionList2 = [Select Id, IsAllDayEvent__c FROM Storefront_Interaction__c where IsAllDayEvent__c = true AND Category__c = 'Notification' AND Account__c =: accObj.Id];
            system.assertEquals(interactionList2.size(), 0); 
            
            List<CRMRetail_SDoHDetermt__c> sdohList = [SELECT Id, Name FROM CRMRetail_SDoHDetermt__c WHERE Account__c =: accObj.Id];
            system.assertEquals(sdohList.size(), 0); 
            
            Storefront_Interaction_H_HUM.processBeforeDelete(null);
            
            List < Error_Log__c > lstErrorLogs = [Select Id, Class_Name__c, Method_Name__c from Error_Log__c where Class_Name__c = 'Storefront_Interaction_H_HUM' AND Method_Name__c = 'processBeforeDelete'];
            system.assertEquals(lstErrorLogs.size(), 1);
            
            Storefront_Interaction_H_HUM.processAttendanceBeforeDelete(null);
            
            lstErrorLogs = [Select Id, Class_Name__c, Method_Name__c from Error_Log__c where Class_Name__c = 'Storefront_Interaction_H_HUM' AND Method_Name__c = 'processAttendanceBeforeDelete'];
            system.assertEquals(lstErrorLogs.size(), 1); 
            test.stopTest();
        }
    }
    
    /**
    * Returns void
    * <p>
    * This method is used for validating processBeforeDelete method. 
    *
    * @return  Void
    * @see
    */
    @isTest static void testprocessBeforeInsert()
    {
        User testUser = [SELECT Id, Name FROM User WHERE LastName = 'test UserRetail'];
        system.assertEquals(testUser.Name , 'test UserRetail');

        system.runAs(testUser)
        {            
            Account oAcc = [SELECT Id FROM Account LIMIT 1];
            system.assertNotEquals(oAcc, Null);
            
            Storefront_Location__c location1 = [SELECT Id, Location_Type__c FROM Storefront_Location__c where Name = 'Virginia' LIMIT 1];
            system.assertNotEquals(location1, Null);
            
            Storefront_I_Reason__c InteractionReason1 = [SELECT Id, Name FROM Storefront_I_Reason__c WHERE Name='Visitor Check-In (1st Time)' LIMIT 1];			
            system.assertEquals(InteractionReason1.Name, 'Visitor Check-In (1st Time)');
            
            Storefront_I_Reason__c InteractionReason2 = [SELECT Id, Name FROM Storefront_I_Reason__c WHERE Name='Waiver Required' LIMIT 1];		
            system.assertEquals(InteractionReason2.Name, 'Waiver Required');
            
            Datetime dt = system.today();
            DateTime tm = system.now();
            dt = dt.addHours(tm.hour());
            
            List<Storefront_Interaction__c> lstOfInteractions = new List<Storefront_Interaction__c>();
            Storefront_Interaction__c gcmInteractions1 = new Storefront_Interaction__c(Account__c=oAcc.Id,Location__c=location1.Id,Reason__c=InteractionReason1.Id, IsAllDayEvent__c = true, CRM_Retail_Interaction_Visitor_Type__c = 'Member', Interaction_Date__c = dt);
            lstOfInteractions.add(gcmInteractions1);            
            
            dt = dt.addDays(1);
                        
            Storefront_Interaction__c gcmInteractions2 = new Storefront_Interaction__c(Account__c=oAcc.Id,Location__c=location1.Id,Reason__c=InteractionReason1.Id, IsAllDayEvent__c = true, CRM_Retail_Interaction_Visitor_Type__c = 'Member', Interaction_Date__c = dt);
            lstOfInteractions.add(gcmInteractions2); 
            Storefront_Interaction__c gcmInteractions3 = new Storefront_Interaction__c(Account__c=oAcc.Id,Location__c=location1.Id,Reason__c=InteractionReason2.Id, IsAllDayEvent__c = true, CRM_Retail_Interaction_Visitor_Type__c = 'Member',Category__c = 'Scheduled Virtual', Interaction_Date__c = dt);
            lstOfInteractions.add(gcmInteractions3);
            insert lstOfInteractions;
            system.assertEquals(gcmInteractions1.Account__c, oAcc.Id);
            system.assertEquals(gcmInteractions2.Interaction_Date__c, dt);
            
            List<Attendance__c> attLst = [SELECT Id, Current_Attendance__c  FROM Attendance__c];
            system.assert(attLst.size()>1);
            
            Storefront_Interaction__c gcmInteractionObj = new Storefront_Interaction__c(Account__c = oAcc.Id , Location__c = location1.Id,Reason__c = InteractionReason1.Id, IsAllDayEvent__c = true, CRM_Retail_Interaction_Visitor_Type__c = 'Member',Category__c = 'Scheduled Virtual', Interaction_Date__c = dt);
            insert gcmInteractionObj;
            system.assertEquals(gcmInteractionObj.Account__c, oAcc.Id);
            Storefront_Interaction__c gcmInteractionObj2 = [SELECT Id, Name, Missing_Event__c FROM Storefront_Interaction__c gcmInteractionObj WHERE Interaction_Date__c =: dt LIMIT 1];
            system.assertEquals(gcmInteractionObj2.Missing_Event__c, False);
            
            Storefront_Interaction_H_HUM.processAttendanceOnInsert(null);
            
            List < Error_Log__c > lstErrorLogs = [Select Id, Class_Name__c, Method_Name__c from Error_Log__c where Class_Name__c = 'Storefront_Interaction_H_HUM' AND Method_Name__c = 'processAttendanceOnInsert']; 
            system.assertEquals(lstErrorLogs.size(), 1);            
        }
    }
    
    /**
    * Returns void
    * <p>
    * This method is used for validating calculateEventAttendance method. 
    *
    * @return  Void
    * @see
    */
    @isTest static void testcalculateEventAttendance()
    {
        User testUser = [SELECT Id, Name FROM User WHERE LastName = 'test UserRetail'];
        system.assertEquals(testUser.Name , 'test UserRetail');
        
        Account oAcc = [SELECT Id FROM Account LIMIT 1];
        system.assertNotEquals(oAcc, Null);
        
        Storefront_Location__c location1 = [SELECT Id, Location_Type__c FROM Storefront_Location__c where Name = 'Virginia' LIMIT 1];
        system.assertNotEquals(location1, Null);
        
        Storefront_I_Reason__c InteractionReason1 = [SELECT Id, Name FROM Storefront_I_Reason__c WHERE Name='Social-Cafe' LIMIT 1];			
        system.assertEquals(InteractionReason1.Name, 'Social-Cafe');
        
        List<Storefront_Interaction__c> gcmInteraction1 = [SELECT Id, Name,IsAllDayEvent__c FROM Storefront_Interaction__c WHERE IsAllDayEvent__c = True ];			
        system.assertEquals(gcmInteraction1.get(0).IsAllDayEvent__c, true);
        system.runAs(testUser)
        {   
            Datetime dt = system.today();
            DateTime tm = system.now();
            Event evt = new Event();
            evt.WhatId = location1.Id;
            evt.Reason__c = InteractionReason1.Id;
            evt.ActivityDate = date.today();
            evt.StartDateTime = dt;
            evt.EndDateTime = dt.addHours(1);
            insert evt;
            system.assertEquals(evt.WhatId, location1.Id);
            
            dt = dt.addHours(tm.hour());         
            List<Storefront_Interaction__c> lstOfInteractions = new List<Storefront_Interaction__c>();
            Storefront_Interaction__c gcmInteractions1 = new Storefront_Interaction__c(Storefront_Event_Starttime__c = evt.StartDateTime, Storefront_Event_Endtime__c = evt.EndDateTime, Account__c = oAcc.Id, Location__c = location1.Id, Reason__c = InteractionReason1.Id, IsAllDayEvent__c = false, CRM_Retail_Interaction_Visitor_Type__c = 'Member', Interaction_Date__c = dt);
            lstOfInteractions.add(gcmInteractions1);                            
            
            Storefront_Interaction_H_HUM.calculateEventAttendance(gcmInteraction1, 'insert');
                        
            Storefront_Interaction__c gcmInteractions2 = new Storefront_Interaction__c(Category__c = 'Scheduled Onsite', Storefront_Event_Starttime__c = evt.StartDateTime, Storefront_Event_Endtime__c = evt.EndDateTime, Account__c = oAcc.Id, Location__c = location1.Id, Reason__c = InteractionReason1.Id, IsAllDayEvent__c = true, CRM_Retail_Interaction_Visitor_Type__c = 'Member', Interaction_Date__c = date.today());
            lstOfInteractions.add(gcmInteractions2);            
            
            Storefront_Interaction__c gcmInteractions3 = new Storefront_Interaction__c(Category__c = 'Virtual', Storefront_Event_Starttime__c = evt.StartDateTime, Storefront_Event_Endtime__c = evt.EndDateTime, Account__c = oAcc.Id, Location__c = location1.Id, Reason__c = InteractionReason1.Id, IsAllDayEvent__c = true, CRM_Retail_Interaction_Visitor_Type__c = 'Member', Interaction_Date__c = date.today());
            lstOfInteractions.add(gcmInteractions3);            
            
            Storefront_Interaction__c gcmInteractions4 = new Storefront_Interaction__c(Category__c = 'Scheduled Virtual', Storefront_Event_Starttime__c = evt.StartDateTime, Storefront_Event_Endtime__c = evt.EndDateTime, Account__c = oAcc.Id, Location__c = location1.Id, Reason__c = InteractionReason1.Id, IsAllDayEvent__c = true, CRM_Retail_Interaction_Visitor_Type__c = 'Member', Interaction_Date__c = date.today());
            lstOfInteractions.add(gcmInteractions4);

			Storefront_Interaction__c gcmInteractions5 = new Storefront_Interaction__c(Category__c = '', Storefront_Event_Starttime__c = evt.StartDateTime, Storefront_Event_Endtime__c = evt.EndDateTime, Account__c = oAcc.Id, Location__c = location1.Id, Reason__c = InteractionReason1.Id, IsAllDayEvent__c = true, CRM_Retail_Interaction_Visitor_Type__c = 'Member', Interaction_Date__c = date.today());
            lstOfInteractions.add(gcmInteractions5);            
            insert lstOfInteractions;                 
            system.assertNotEquals(lstOfInteractions.size(),0);
            system.assertEquals(gcmInteractions1.Storefront_Event_Starttime__c, evt.StartDateTime);                                  
            system.assertEquals(gcmInteractions2.Storefront_Event_Starttime__c, evt.StartDateTime);                        
            system.assertEquals(gcmInteractions3.Storefront_Event_Starttime__c, evt.StartDateTime); 
            system.assertEquals(gcmInteractions4.Storefront_Event_Starttime__c, evt.StartDateTime);
            system.assertEquals(gcmInteractions5.Storefront_Event_Starttime__c, evt.StartDateTime);
            delete lstOfInteractions;
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
    @isTest static void testvirtualLocationChecks()
    {
        User testUser = [SELECT Id, Name FROM User WHERE LastName = 'test UserCRMRetail'];
        system.assertEquals(testUser.Name , 'test UserCRMRetail');        
                           
        
        system.runAs(testUser)
        {           
            Account oAcc = [SELECT Id from Account LIMIT 1];
            system.assertNotEquals(oAcc, Null); 
            
            List<Storefront_Location__c> listOfLocation = [SELECT Id, Location_Type__c, Name FROM Storefront_Location__c ORDER BY Name ASC LIMIT 2];
            System.assertEquals(listOfLocation.size(), 2);
            
            Storefront_I_Reason__c interactionReason = [SELECT Id, Name FROM Storefront_I_Reason__c WHERE Name = 'Visitor Check-In (1st Time)' LIMIT 1];			
            System.assertEquals(interactionReason.Name, 'Visitor Check-In (1st Time)');
            
            Datetime dt = System.today();
            DateTime tm = System.now();
            dt = dt.addHours(tm.hour()); 
            
            test.startTest();
            
            Storefront_Interaction__c interactions = new Storefront_Interaction__c(Account__c = oAcc.Id, Location__c = listOfLocation[0].Id, Reason__c = interactionReason.Id, IsAllDayEvent__c = true, CRM_Retail_Interaction_Visitor_Type__c = 'Member', Interaction_Date__c = dt);
            insert interactions;
            System.assertEquals(interactions.Account__c, oAcc.Id);
                
            Storefront_Interaction__c interactionOfVirtualLocation = new Storefront_Interaction__c(Account__c = oAcc.Id, Location__c = listOfLocation[1].Id, Reason__c = interactionReason.Id, IsAllDayEvent__c = true, CRM_Retail_Interaction_Visitor_Type__c = 'Member', Interaction_Date__c = dt);
            
            try
            {
                insert interactionOfVirtualLocation;
            }
            catch(Exception e)
            {
                System.assert(e.getMessage().contains(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_VIRTUAL_LOC_INELIGIBILITY_MSG));
            }
            
            try
            {
                interactions.Location__c = listOfLocation[1].Id;
                update interactions;
            }
            catch(Exception e)
            {
                System.assert(e.getMessage().contains(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_VIRTUAL_LOC_INELIGIBILITY_MSG));
            }
            
            try
            {
                List<CRMFunctionality_ONOFF_Switch__c> listOfSwitch = [Select Id, Name, IsON__c from CRMFunctionality_ONOFF_Switch__c where Name = '2360057' AND IsON__c = true LIMIT 1];
        		System.assertEquals(listOfSwitch.size() , 1);
                
                listOfSwitch[0].IsON__c = false;
                update listOfSwitch[0];
                System.assertEquals(listOfSwitch[0].IsON__c , false);
                
                Storefront_Interaction__c interactionVirtualLocation = new Storefront_Interaction__c(Account__c = oAcc.Id, Location__c = listOfLocation[1].Id, Reason__c = interactionReason.Id, IsAllDayEvent__c = true, CRM_Retail_Interaction_Visitor_Type__c = 'Member', Interaction_Date__c = dt);
				insert interactionVirtualLocation;
                System.assertEquals(interactionVirtualLocation.Account__c,oAcc.Id);
                
                listOfSwitch[0].IsON__c = true;
                update listOfSwitch[0];
                System.assertEquals(listOfSwitch[0].IsON__c , true);
                
                delete interactionVirtualLocation;
            }
            catch(Exception e)
            {
                System.assert(e.getMessage().contains(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_VIRTUAL_LOC_INELIGIBILITY_MSG));
            }
            test.stopTest();              
        }
    }
}