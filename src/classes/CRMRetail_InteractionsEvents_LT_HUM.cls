/*******************************************************************************************************************************
Apex Class Name     : CRMRetail_InteractionsEvents_LT_HUM
Version             : 1.0
Created Date        : 07/18/2022
Function            : This serves as the test class for CRMRetail_InteractionsEvents_LC_HUM class.

Modification Log: 
* Developer Name           Code Reveiw                     Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Mohamed Thameem                                         07/18/2022                 User Story 3450197: T1PRJ0154546 / SF / MF9 Storefront Modernization (Business Admin/Super Admin) - Home (Right Side)
* Sahil Verma                                             08/09/2022                 US : 3551183 - T1PRJ0154546 / SF / MF9 Storefront: Modernization - Interactions/Events - Ability to Search Visitor Check-Ins
* Vivek Sharma                                            08/22/2022                 User Story 3581472: T1PRJ0154546 / SF / MF9 Storefront: Ability to Create Follow Up Task From Visitor Check-Ins
* Vinoth L									   			  09/11/2022				 User Story 3791721: 09/23 Observations Fix
* Navajit Sarkar                                          11/03/2022                 User Story 3866537: T1PRJ0154546 / SF / MF5 Storefront: Reporting of recommended events
* Mohamed Thameem                                         01/05/2023                 User Story 2792916: T1PRJ0154546 / SF / MF9 Storefront Home Page: Visitor Interactions Search 
* Vinoth L                                                01/06/2022                 User Story 4012388: T1PRJ0154546 / SF/ MF9 Storefront - Ability to View First Time Visit - Current CY in Storefront
* Vinoth L                                                03/13/2023                 Health Educator - Populate SDoH Records fix
* Mohamed Thameem                                         07/28/2023                 User Story 3957580: T1PRJ0154546 / SF / MF9 Storefront: Modernization - Interactions/Events - Ability to Search Created By 
* Vinoth L                      				  		  08/26/2023           	 	 User Story 5012060: T1PRJ0154546 / DP / MF9 Storefront - Add preferred name on Account
*******************************************************************************************************************************/
@isTest
public class CRMRetail_InteractionsEvents_LT_HUM {
    
    /*
    * This method creates test data for test class
    * @param None
    * @return None
    */
    @testSetup static void setUpTestData() 
    {
        List<User> lstOfUsers = new List<User>();
        User u= HUMTestDataHelperTest.createTestUser('UserRetail','CRMRetail User');
        User u1= HUMTestDataHelperTest.createTestUser('UserTest','Customer Care Specialist');
        
        lstOfUsers.add(u);                
        lstOfUsers.add(u1);
        insert lstOfUsers;
        
        system.assertEquals('test UserRetail',lstOfUsers.get(0).lastname);
        system.assertEquals('test UserTest',lstOfUsers.get(1).lastname);
        
        List<PermissionSetAssignment> lstOfPermissionSetAssignments = new List<PermissionSetAssignment>();
        PermissionSet SuperAdmin = [SELECT Id FROM PermissionSet WHERE Name = 'CRMRetail_400_Super_Admin'];
        PermissionSetAssignment supAdmin = new PermissionSetAssignment(AssigneeId = u.id, PermissionSetId = SuperAdmin.Id);
        lstOfPermissionSetAssignments.add(supAdmin);        
        insert lstOfPermissionSetAssignments;
        
        system.assertEquals(u.id,lstOfPermissionSetAssignments.get(0).AssigneeId);
        
        Id MemberRecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER).getRecordTypeId();
        
        system.runas(u) 
        {
            List<HUM_Webservice_Callout__c> lstOfCstmSetting = new List<HUM_Webservice_Callout__c>();
            HUM_Webservice_Callout__c customSDoh=new HUM_Webservice_Callout__c( Name = 'CRMRetail_SDoH' ,Request_Type__c='POST', Certificate_Name__c = 'DummyCertificate', Active__c = true, End_Point_URL__c = 'http://www.google.com', Timeout__c = '12000');  
            HUM_Webservice_Callout__c cRMRetail_SDoH_Create=new HUM_Webservice_Callout__c( Name = 'CRMRetail_SDoH_Create' ,Request_Type__c='POST', Certificate_Name__c = 'DummyCertificate', Active__c = true, End_Point_URL__c = 'http://www.google.com', Timeout__c = '12000');           
            HUM_Webservice_Callout__c cRMRetail_SDoH_Update=new HUM_Webservice_Callout__c( Name = 'CRMRetail_SDoH_Update' ,Request_Type__c='POST', Certificate_Name__c = 'DummyCertificate', Active__c = true, End_Point_URL__c = 'http://www.google.com', Timeout__c = '12000');           
            HUM_Webservice_Callout__c cRMRetail_SDoH_Delete=new HUM_Webservice_Callout__c( Name = 'CRMRetail_SDoH_Delete' ,Request_Type__c='POST', Certificate_Name__c = 'DummyCertificate', Active__c = true, End_Point_URL__c = 'http://www.google.com', Timeout__c = '12000');           
            HUM_Webservice_Callout__c cRMRetail_SDoH_GetAllData=new HUM_Webservice_Callout__c( Name = 'CRMRetail_GetAllData' ,Request_Type__c='POST', Certificate_Name__c = 'DummyCertificate', Active__c = true, End_Point_URL__c = 'http://www.google.com', Timeout__c = '12000');
            
            lstOfCstmSetting.add(customSDoh);
            lstOfCstmSetting.add(cRMRetail_SDoH_Create);
            lstOfCstmSetting.add(cRMRetail_SDoH_Update);
            lstOfCstmSetting.add(cRMRetail_SDoH_Delete);
            lstOfCstmSetting.add(cRMRetail_SDoH_GetAllData);
            insert lstOfCstmSetting;
            
            system.assertEquals('CRMRetail_SDoH',lstOfCstmSetting[0].name);
            system.assertEquals('CRMRetail_SDoH_Create',lstOfCstmSetting[1].name);
            system.assertEquals('CRMRetail_SDoH_Update',lstOfCstmSetting[2].name);
            system.assertEquals('CRMRetail_SDoH_Delete',lstOfCstmSetting[3].name);
            system.assertEquals('CRMRetail_GetAllData',lstOfCstmSetting[4].name);
            
            List<Account> lstOfAccounts = new List<Account>();
            Account oAcc1 = CRMRetail_TestDataFactory_H_HUM.createAccountInstance('test1234', MemberRecordTypeId,Date.newInstance(1984, 01, 01),'01/01/1984', 'Member', 'Account', 'O', '1060920018',false, 'New York', '56605', '(123) 123-1231','GC7654321',Date.today()-100,null,NULL);
            lstOfAccounts.add(oAcc1);
            
            Account oAcc2 = CRMRetail_TestDataFactory_H_HUM.createAccountInstance('test1234', MemberRecordTypeId,Date.newInstance(1999, 01, 01),'01/01/1999', 'Tom', 'Hill', 'O', '1900291029',false, 'New York', '56605', '(123) 123-1231','GC7654390',Date.today()-100,null,NULL);
            lstOfAccounts.add(oAcc2);
            insert lstOfAccounts;
            
            system.assertEquals(2,lstOfAccounts.size());
            system.assertEquals('GC7654321',lstOfAccounts[0].GCM_Visitor_Barcode__c);
            system.assertEquals('GC7654390',lstOfAccounts[1].GCM_Visitor_Barcode__c);
            
            List<Storefront_Location__c> listOfLocations = new List<Storefront_Location__c>();
            Storefront_Location__c location1 = CRMRetail_TestDataFactory_H_HUM.createLocation('Virtual HNC','Tampa','Tampa','66545','66545','',30);
            location1.Location_Type__c = 'Provider';
            listOfLocations.add(location1);

			Storefront_Location__c location2 = CRMRetail_TestDataFactory_H_HUM.createLocation('Tulsa','abc','abc','91021','91021','',30);
            location2.Location_Type__c = 'Lite C';
            listOfLocations.add(location2);            
            insert listOfLocations; 
            
            system.assertEquals(2,listOfLocations.size());
            system.assertEquals('Virtual HNC',listOfLocations[0].Name);
            system.assertEquals('Tulsa',listOfLocations[1].Name);
            
            Storefront_I_Type__c InteractionType1 = new Storefront_I_Type__c(Name ='Social');
            insert InteractionType1;
            
            system.assertEquals('Social',InteractionType1.Name);
            
            List<Storefront_I_Reason__c> interactionReason = new List<Storefront_I_Reason__c>();
            
            Storefront_I_Reason__c InteractionReason1 = CRMRetail_TestDataFactory_H_HUM.createInteractionReason(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_VISITOR_CHECKIN ,InteractionType1.Id);
            interactionReason.add(InteractionReason1);
            Storefront_I_Reason__c InteractionReason2 = CRMRetail_TestDataFactory_H_HUM.createInteractionReason('Test Reason' ,InteractionType1.Id);
            interactionReason.add(InteractionReason2);
            Storefront_I_Reason__c InteractionReason3 = CRMRetail_TestDataFactory_H_HUM.createInteractionReason(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_HEALTH_EDUCATOR_REASON ,InteractionType1.Id);
            interactionReason.add(InteractionReason3);
            insert InteractionReason;            
            system.assertEquals(InteractionType1.id,InteractionReason.get(0).Interaction_Type__c);
            
            List<Storefront_Interaction__c> gcmInteractions = new List<Storefront_Interaction__c>();
            Storefront_Interaction__c GcmInteraction1 = CRMRetail_TestDataFactory_H_HUM.createInteraction(lstOfAccounts.get(0).Id,listOfLocations.get(0).Id,InteractionReason.get(0).Id, False,'Member', system.today()+1);
            Storefront_Interaction__c GcmInteraction2 = CRMRetail_TestDataFactory_H_HUM.createInteraction(lstOfAccounts.get(0).Id,listOfLocations.get(0).Id,InteractionReason.get(1).Id, True,'Member', system.today()+1); 
            Storefront_Interaction__c GcmInteraction3 = CRMRetail_TestDataFactory_H_HUM.createInteraction(lstOfAccounts.get(1).Id,listOfLocations.get(1).Id,InteractionReason.get(1).Id, True,'Member', system.today()+1); 
            Storefront_Interaction__c GcmInteraction4 = CRMRetail_TestDataFactory_H_HUM.createInteraction(lstOfAccounts.get(1).Id,listOfLocations.get(1).Id,InteractionReason.get(1).Id, True,'Member', system.today()); 
            GcmInteraction3.Category__c = GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_SCH_ONSITE; 
            GcmInteraction1.SDoH__c = True;
            GcmInteraction2.SDoH__c = True;            
            GcmInteraction2.Category__c = GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_SCH_VIRTUAL;            
            gcmInteractions.add(GcmInteraction1);  
            gcmInteractions.add(GcmInteraction2);
            gcmInteractions.add(GcmInteraction3);
            gcmInteractions.add(GcmInteraction4);
            Storefront_Interaction__c GcmInteraction;
            for(Integer i=GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_RECOMMENDATION_ELIGIBILITY_COUNT+2; i>0;i--){
                GcmInteraction = CRMRetail_TestDataFactory_H_HUM.createInteraction(lstOfAccounts.get(0).Id,listOfLocations.get(0).Id,InteractionReason.get(1).Id, True,'Member', system.today()-i); 
                gcmInteractions.add(GcmInteraction);
            }
            insert gcmInteractions;
            
            SYstem.assertEquals(16,gcmInteractions.size());
            
            List<Event> events = new List<Event>();
            event evnt1 = CRMRetail_TestDataFactory_H_HUM.createEvent(InteractionReason.get(0).id ,listOfLocations.get(0).Id ,system.Now().addHours(4) ,system.Now().addHours(5), System.today(),false, 'Busy',1);
            evnt1.WhatId= listOfLocations.get(0).Id;
            evnt1.IsAllDayEvent = false;
            events.add(evnt1);
            insert events;
            
            System.assertEquals(1,events.size());
            
            List<CRMRetail_OOO_Tracking__c> trackingObjList= new List<CRMRetail_OOO_Tracking__c>();
            CRMRetail_OOO_Tracking__c trackingObj1 = CRMRetail_TestDataFactory_H_HUM.createOOORecord(oAcc1.Id,location1.Id,'BMI Screening','Food Insecurity',TRUE);
            CRMRetail_OOO_Tracking__c trackingObj2 = CRMRetail_TestDataFactory_H_HUM.createOOORecord(oAcc1.Id,location1.Id,'BMI Screening','Financial Strain',TRUE);
            trackingObj1.CRM_Retail_Interaction__c = gcmInteractions.get(0).Id;
            trackingObj2.CRM_Retail_Interaction__c = gcmInteractions.get(1).Id;
            trackingObjList.add(trackingObj1);
            trackingObjList.add(trackingObj2);
            insert trackingObjList;
            
            system.assertEquals(2,trackingObjList.size());
            
            List<CRMFunctionality_ONOFF_Switch__c> lstOfCRMSwitches = new List<CRMFunctionality_ONOFF_Switch__c>();
            CRMFunctionality_ONOFF_Switch__c switch_searchResult = new CRMFunctionality_ONOFF_Switch__c();
            switch_searchResult.IsON__c = true;
            switch_searchResult.Name = '3551183';
            lstOfCRMSwitches.add(switch_searchResult);
            CRMFunctionality_ONOFF_Switch__c switch_3510484 = new CRMFunctionality_ONOFF_Switch__c();
            switch_3510484.IsON__c = true;
            switch_3510484.Name = '3510484';
            lstOfCRMSwitches.add(switch_3510484);
            CRMFunctionality_ONOFF_Switch__c switch_3581472 = new CRMFunctionality_ONOFF_Switch__c();
            switch_3581472.IsON__c = true;
            switch_3581472.Name = '3581472';
            lstOfCRMSwitches.add(switch_3581472);
            CRMFunctionality_ONOFF_Switch__c switch_3782843 = new CRMFunctionality_ONOFF_Switch__c();
            switch_3782843.IsON__c = true;
            switch_3782843.Name = '3782843';
            lstOfCRMSwitches.add(switch_3782843);
            CRMFunctionality_ONOFF_Switch__c switch_3866537 = new CRMFunctionality_ONOFF_Switch__c();
            switch_3866537.IsON__c = true;
            switch_3866537.Name = '3866537';
            lstOfCRMSwitches.add(switch_3866537);
            CRMFunctionality_ONOFF_Switch__c switch_4012388 = new CRMFunctionality_ONOFF_Switch__c();
            switch_4012388.IsON__c = true;
            switch_4012388.Name = '4012388';
            lstOfCRMSwitches.add(switch_4012388);
            CRMFunctionality_ONOFF_Switch__c switch_2792916 = new CRMFunctionality_ONOFF_Switch__c();
            switch_2792916.IsON__c = true;
            switch_2792916.Name = '2792916';
            lstOfCRMSwitches.add(switch_2792916);
            CRMFunctionality_ONOFF_Switch__c switch_3957580 = new CRMFunctionality_ONOFF_Switch__c();
            switch_3957580.IsON__c = true;
            switch_3957580.Name = '3957580';
            lstOfCRMSwitches.add(switch_3957580);
            CRMFunctionality_ONOFF_Switch__c switch_5012060 = new CRMFunctionality_ONOFF_Switch__c();
            switch_5012060.IsON__c = true;
            switch_5012060.Name = '5012060';
            lstOfCRMSwitches.add(switch_5012060);
            insert lstOfCRMSwitches;
            assert.areEqual(lstOfCRMSwitches.size(), 9);
            
        }
        
    }
    
    
    /*
    * This method to cover FirstTimeCalendarVisit
    * @param None
    * @return None
    */
    @isTest static void testFirstTimeCalendarVisit()
    {
        User testUser = [SELECT Id, LastName FROM User WHERE LastName = 'test UserRetail'];        
        assert.areEqual('test UserRetail',testUser.LastName);
        
        List<Account> lstAccs = [SELECT Id,Name,GCM_Visitor_Barcode__c,FirstName,LastName,RecordType.Name from Account where FirstName ='Tom' AND LastName='Hill'];
        assert.areEqual('Tom O Hill',lstAccs[0].Name);
        
        List<Storefront_Interaction__c> lstInt = [SELECT Id,Name,Reason__r.Name,Reason__c,Category__c,Location__c,Interaction_Date__c,Visitor_ID__c,Account__c,Veteran__c,New_Member__C,Aging_In__C,Storefront_Vstr_Indicator__c,SDoH__c,CRM_Retail_Interaction_Visitor_Type__c  from Storefront_Interaction__c where Account__c=:lstAccs[0].Id];
        assert.areEqual('Test Reason',lstInt[0].Reason__r.Name);
        
        List<Storefront_I_Reason__c> lstIntReason = [SELECT Id,Name from Storefront_I_Reason__c where Name='Test Reason'];
        assert.areEqual('Test Reason',lstIntReason[0].Name);
        
        List<CRMRetail_InteractionsEvents_LC_HUM.EventsWrapper> eventWrapper = new List<CRMRetail_InteractionsEvents_LC_HUM.EventsWrapper>();
        CRMRetail_InteractionsEvents_LC_HUM.EventsWrapper wrapper1 = new CRMRetail_InteractionsEvents_LC_HUM.EventsWrapper();
        wrapper1.ReasonId = lstIntReason[0].id;
        wrapper1.IsAllDayEvent = false;
        wrapper1.StartDateTime = System.now();
        wrapper1.EndDateTime = System.now();
        wrapper1.ReasonName = lstIntReason[0].Name;
        eventWrapper.add(wrapper1);                
        
        String displayDate = String.valueOf(system.Now().dateGMT()); 
        Storefront_Location__c locobj =[Select Id,Name from Storefront_Location__c LIMIT 1];
        String locationVal = locobj.Id + '-' + locobj.Name;
        string formatedDate = system.now().format('yyyy-MM-dd');
        
        Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION, locationVal);        
        Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE,formatedDate);
        
        System.runAs(testUser){
            test.startTest();
            Test.setMock(HttpCalloutMock.class, new CRMRetail_SDoH_Helper_T_HUM.CRMRetail_ConsolidatedService_Success()); 
            Map<String,String> mpOfRes = CRMRetail_InteractionsEvents_LC_HUM.createNewInteractions(JSON.serialize(lstInt),JSON.serialize(eventWrapper),'scheduled');            
            Storefront_Interaction__c sfInt = [SELECT Id,Account__r.FirstName,Account__r.LastName,Account__r.CRMRetail_PreferredName__c from Storefront_Interaction__c order by CreatedDate DESC LIMIT 1];
            assert.areEqual((sfInt.Account__r.FirstName.substring(0,1)+sfInt.Account__r.LastName.substring(0,1)),sfInt.Account__r.CRMRetail_PreferredName__c);            
            assert.areEqual('true',mpOfRes.get('isSuccess'));            
            test.stopTest(); 
        }
    }
    /*
    * This method to cover deleteInteraction
    * @param None
    * @return None
    */
    @isTest static void testdeleteInteraction()
    {
        User testUser = [SELECT Id, LastName FROM User WHERE LastName = 'test UserRetail'];
        User testUserWithoutAccess = [SELECT Id, LastName FROM User WHERE LastName = 'test UserTest'];
        
        System.assertEquals('test UserRetail',testUser.LastName);
        System.assertEquals('test UserTest',testUserWithoutAccess.LastName);
        
        test.startTest();   
        
        String InteractionId = [select Id from Storefront_Interaction__c limit 1].id;
        
        system.runAs(testUser)
        {
            Boolean isdeleted = CRMRetail_InteractionsEvents_LC_HUM.deleteInteraction(InteractionId);   
            System.assertEquals(true,isdeleted);
        }
        system.runAs(testUserWithoutAccess)
        {
            try{
                InteractionId = [select Id from Storefront_Interaction__c limit 1].id;
                CRMRetail_InteractionsEvents_LC_HUM.deleteInteraction(InteractionId);   
            }
            catch(Exception e){
                System.assertEquals('System.AuraHandledException', e.getTypeName());
            }
        }
        test.stopTest();   
    }
    
    /*
    * This method to cover getEvent
    * @param None
    * @return None
    */
    @isTest static void testgetEvents()
    {    
        User testUser = [SELECT Id, LastName FROM User WHERE LastName = 'test UserRetail'];        
        System.assertEquals('test UserRetail',testUser.LastName);
        
        system.runAs(testUser)
        {
            test.startTest();               
            
            String displayDate = String.valueOf(system.Now().dateGMT()); 
            Storefront_Location__c locobj =[Select Id,Name from Storefront_Location__c LIMIT 1];
            String locationVal = locobj.Id + '-' + locobj.Name;
            string formatedDate = date.today().year() + '/' + date.today().month() + '/' + date.today().day();
            
            Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION, locationVal);        
            Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE,formatedDate);
            
            List <Event> returnedEvents = CRMRetail_InteractionsEvents_LC_HUM.getEvents(displayDate);
            System.assertEquals(returnedEvents.size(),1);
            
            try
            {
                Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION, '');        
                returnedEvents = CRMRetail_InteractionsEvents_LC_HUM.getEvents(displayDate);
            }
            catch(exception e)
            {
                System.assertEquals('System.AuraHandledException', e.getTypeName());
            }            
            test.stopTest();   
        }
    }
    
    
    /*
    * This method to cover testisNewEvtButtonVisible
    * @param None
    * @return None
    */
    @isTest static void testisNewEvtButtonVisible()
    {
        User testUser = [SELECT Id, LastName FROM User WHERE LastName = 'test UserRetail'];        
        System.assertEquals('test UserRetail',testUser.LastName);
        
        system.runAs(testUser)
        {
            test.startTest();               
            
            Storefront_Location__c locobj =[Select Id,Name from Storefront_Location__c LIMIT 1];
            String locationVal = locobj.Name + '-' + locobj.Id;
            Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION, locationVal);
            Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE, date.today().format());
            Map<String,String> returnMap = CRMRetail_InteractionsEvents_LC_HUM.isNewEvtButtonVisible();
            System.assertEquals(true,Boolean.valueOf(returnMap.get('isEligible')));
            
            Cache.Session.Remove(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE);
            returnMap = CRMRetail_InteractionsEvents_LC_HUM.isNewEvtButtonVisible();
            System.assertEquals(true,Boolean.valueOf(returnMap.get('isEligible')));
            
            test.stopTest();
        }
    }
    
    /*
    * This method to cover testgetLocationAndDateValues
    * @param None
    * @return None
    */
    @isTest static void testgetLocationAndDateValues()
    {
        User testUser = [SELECT Id, LastName FROM User WHERE LastName = 'test UserRetail'];        
        System.assertEquals('test UserRetail',testUser.LastName);
        
        system.runAs(testUser)
        {
            test.startTest();               
            
            Storefront_Location__c locobj =[Select Id,Name from Storefront_Location__c LIMIT 1];
            String locationVal = locobj.Id + '-' + locobj.Name;
            string formatedDate = date.today().year() + '/' + date.today().month() + '/' + date.today().day();
            
            Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION, locationVal);        
            Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE,formatedDate);
            
            Map<String,Object> dateMap = CRMRetail_InteractionsEvents_LC_HUM.getLocationAndDateValues(formatedDate);
            System.assertEquals(locobj.Id ,String.valueOf(dateMap.get('location')));
            
            try
            { 
                CRMRetail_InteractionsEvents_LC_HUM.sSelectedDate=null;
                CRMRetail_InteractionsEvents_LC_HUM.getLocationAndDateValues(formatedDate);
                
            }
            catch(exception e)
            {
                System.assertEquals('System.AuraHandledException', e.getTypeName());
            }
            
            test.stopTest();
        }
    }
    
    /*
    * This method cover getSDohAcronym method
    * @param None
    * @return None
    */
    @isTest static void testgetSDohAcronym(){
        
        Account objAcc = [select Id,FirstName,Enterprise_ID__c,CRMRetailNotificationOptOut__c,Veteran_Status__c,Birthdate__c, LastName from Account LIMIT 1];
        User testUser = [SELECT Id, LastName FROM User WHERE LastName = 'test UserRetail'];
        String locationVal = '';
        
        System.assertEquals('test UserRetail',testUser.LastName);
        
        system.runAs(testUser)
        {
            test.startTest();
            
            Test.setMock(HttpCalloutMock.class, new CRMRetail_SDoH_Helper_T_HUM.CRMRetail_SDoH_MockImpl()); 
            Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE, String.valueof(date.today()));
            String returnVal = CRMRetail_InteractionsEvents_LC_HUM.getSDohAcronym(objAcc);
            
            System.assertEquals(returnVal, '');
            
            String recId=[select id from Storefront_Interaction__c limit 1].id;
            Boolean returnFlag = CRMRetail_InteractionsEvents_LC_HUM.deduceHEAssociation(recId);
            
            System.assertEquals(true, returnFlag);
            
            
            try
            {
                Account acc;
                CRMRetail_InteractionsEvents_LC_HUM.getSDohAcronym(acc);
            }
            catch(Exception e)
            {
                System.assertEquals('System.AuraHandledException', e.getTypeName());
            }
            
            test.stopTest();   
        }
    }
    
    
    /*
    * This method cover retrieveInteractions
    * @param None
    * @return None
    */
    @isTest static void testretrieveInteractions()
    {
        User testUser = [SELECT Id, LastName FROM User WHERE LastName = 'test UserRetail'];
        String locationVal = '';
        
        System.assertEquals('test UserRetail',testUser.LastName);
        
        system.runAs(testUser)
        {
            test.startTest();   
            
	        List<Storefront_Interaction__c> recId=[select id,Account__r.FirstName,category__c,Interaction_Date__c from Storefront_Interaction__c];
            Storefront_Location__c locobj =[Select Id,Name,Location_Type__c from Storefront_Location__c where Location_Type__c = 'Provider' LIMIT 1 ];
            locationVal = locobj.Id + '-' + locobj.Name;
            Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION, locationVal);
            Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE, String.valueof(date.today()));
            
            String sortedBy='';
            Integer rowsToskip = 0;
            String sortedDirection='desc';
            String sortedTable=GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_CHECK_IN.toLowercase();
            
            Map<String,Object> interactionMap = CRMRetail_InteractionsEvents_LC_HUM.retrieveInteractions(rowsToskip,sortedBy,sortedDirection,sortedTable,recId[0].Account__r.FirstName,'Name');
            System.assertEquals((String)interactionMap.get('SelectedLocation'),'Virtual HNC');
            
            sortedBy='Account__r.LastModifiedDate';
            rowsToskip = 2;
            sortedDirection='asc';
            sortedTable=GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_CATEGORY_ONSITE.toLowercase();
            CRMRetail_InteractionsEvents_LC_HUM.sSelectedDate=String.valueof(date.today());
            CRMRetail_InteractionsEvents_LC_HUM.sLocation=locationVal;
            interactionMap = CRMRetail_InteractionsEvents_LC_HUM.retrieveInteractions(rowsToskip,sortedBy,sortedDirection,sortedTable,'','Interaction Reason');
            
            System.assertEquals((String)interactionMap.get('SelectedLocation'),'Virtual HNC');
            
            sortedBy='Account__r.Name';
            rowsToskip = 0;
            sortedDirection='asc';
            sortedTable=GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_VIRTUAL.toLowercase();
            CRMRetail_InteractionsEvents_LC_HUM.sSelectedDate=String.valueof(date.today());
            CRMRetail_InteractionsEvents_LC_HUM.sLocation=locationVal;
            interactionMap = CRMRetail_InteractionsEvents_LC_HUM.retrieveInteractions(rowsToskip,sortedBy,sortedDirection,sortedTable,'','Name');
            
            System.assertEquals((String)interactionMap.get('SelectedLocation'),'Virtual HNC');
            
            locationVal = locobj.Id + '-' + locobj.Name;
            CRMRetail_InteractionsEvents_LC_HUM.sSelectedDate=String.valueof(date.today());
            CRMRetail_InteractionsEvents_LC_HUM.sLocation=locationVal;
            String emptySortBy;
            interactionMap = CRMRetail_InteractionsEvents_LC_HUM.retrieveInteractions(rowsToskip,emptySortBy,sortedDirection,sortedTable,'','Name');
            
            System.assertEquals((String)interactionMap.get('SelectedLocation'),'Virtual HNC');
            
            CRMRetail_InteractionsEvents_LC_HUM.sSelectedDate=String.valueof(date.today());
            CRMRetail_InteractionsEvents_LC_HUM.sLocation=locationVal;
            
            sortedBy='Account__r.Name';
            rowsToskip = 2;
            sortedDirection='asc';
            sortedTable=GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_CATEGORY_ONSITE.toLowercase();
            interactionMap = CRMRetail_InteractionsEvents_LC_HUM.retrieveInteractions(rowsToskip,sortedBy,sortedDirection,sortedTable,'','Name');
            
            System.assertEquals((String)interactionMap.get('SelectedLocation'),'Virtual HNC');
            
            sortedTable=GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_CHECK_IN.toLowercase();
            CRMRetail_InteractionsEvents_LC_HUM.sLocation=locationVal;
            interactionMap = CRMRetail_InteractionsEvents_LC_HUM.retrieveInteractions(rowsToskip,sortedBy,sortedDirection,sortedTable,'','Name');
            
            System.assertEquals((String)interactionMap.get('SelectedLocation'),'Virtual HNC');
            
            CRMRetail_InteractionsEvents_LC_HUM.sLocation=locationVal;
            sortedTable=GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_VIRTUAL.toLowercase();
            interactionMap = CRMRetail_InteractionsEvents_LC_HUM.retrieveInteractions(rowsToskip,sortedBy,sortedDirection,sortedTable,'','Name');
            
            System.assertEquals((String)interactionMap.get('SelectedLocation'),'Virtual HNC');
            Storefront_Location__c locobjTulsa =[Select Id,Name from Storefront_Location__c where Name = 'Tulsa'];

            CRMRetail_InteractionsEvents_LC_HUM.sLocation= locobjTulsa.Id + '-' + locobjTulsa.Name;
            sortedTable=GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_VIRTUAL.toLowercase();
            interactionMap = CRMRetail_InteractionsEvents_LC_HUM.retrieveInteractions(0,null,null,null,'test','Created By');
            
            List<Storefront_interaction__c>  onsiteList = (List<Storefront_interaction__c>)JSON.deserialize(String.valueOf(interactionMap.get('OnsiteInteractions')), List<Storefront_interaction__c>.class);

            Assert.isTrue(onsiteList[0].createdBy.Name.tolowercase().contains('test'));
            
            try
            {
                CRMRetail_InteractionsEvents_LC_HUM.sSelectedDate='Invalid Date';
                CRMRetail_InteractionsEvents_LC_HUM.retrieveInteractions(rowsToskip,sortedBy,sortedDirection,sortedTable,'','Name');
            }
            catch(Exception e)
            {
                System.assertEquals('System.AuraHandledException', e.getTypeName());
            }
            
            test.stopTest();
            
        }
    }
    
     /*
    * This method cover createNewInteractions
    * @param None
    * @return None
    */
    @isTest static void testcreateNewInteractions()
    {
        User testUser = [SELECT Id, LastName FROM User WHERE LastName = 'test UserRetail'];
        String locationVal = '';
        
        System.assertEquals('test UserRetail',testUser.LastName);
        
        system.runAs(testUser)
        {
            test.startTest();   
            Test.setMock(HttpCalloutMock.class, new CRMRetail_SDoH_Helper_T_HUM.CRMRetail_ConsolidatedService_Success());  
            Storefront_Location__c locobj =[Select Id,Name from Storefront_Location__c LIMIT 1];
            locationVal = locobj.Id + '-' + locobj.Name;
            Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION, locationVal);
            Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE, String.valueof(date.today()));
            
            List<Storefront_Interaction__c> InteractionList = [select SDoH__c,Account__r.Enterprise_Id__c,New_Member__c,Aging_In__c,Storefront_Event_Starttime__c,Veteran__c,Id,Interaction_Date__c,Name,Account__c,Account__r.Name,Reason__c,Reason__r.Name,Location__c,LASTMODIFIEDDATE,IsAllDayEvent__c,CRM_Retail_Interaction_Visitor_Type__c,Visitor_ID__c, Category__c,Storefront_Vstr_Indicator__c from Storefront_Interaction__c where Category__c=:GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_SCH_ONSITE OR category__c=:GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_SCH_VIRTUAL LIMIT 1];            
            List<CRMRetail_InteractionsEvents_LC_HUM.EventsWrapper> eventWrapper = new List<CRMRetail_InteractionsEvents_LC_HUM.EventsWrapper>();
            
            Storefront_I_Reason__c IReason = [select id,name from Storefront_I_Reason__c where name=:GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_VISITOR_CHECKIN limit 1];
            system.assertEquals(IReason.Name, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_VISITOR_CHECKIN);
            
            CRMRetail_InteractionsEvents_LC_HUM.EventsWrapper wrapper = new CRMRetail_InteractionsEvents_LC_HUM.EventsWrapper();
            wrapper.ReasonId = IReason.id;
            wrapper.IsAllDayEvent = true;
            wrapper.StartDateTime = System.now();
            wrapper.EndDateTime = System.now();
            wrapper.ReasonName = IReason.Name;
            eventWrapper.add(wrapper);
            
            CRMRetail_InteractionsEvents_LC_HUM.EventsWrapper wrapper1 = new CRMRetail_InteractionsEvents_LC_HUM.EventsWrapper();
            wrapper1.ReasonId = IReason.id;
            wrapper1.IsAllDayEvent = false;
            wrapper1.StartDateTime = System.now();
            wrapper1.EndDateTime = System.now();
            wrapper1.ReasonName = IReason.Name;
            eventWrapper.add(wrapper1);
            
            System.assertEquals(wrapper.ReasonName, IReason.Name);
            
            String sinteractionList=json.serialize(InteractionList);
            String sintReasonList = json.serialize(eventWrapper);
            String categoryType = 'scheduled';
            
            Map<String,String> interaction= CRMRetail_InteractionsEvents_LC_HUM.createNewInteractions(sinteractionList, sintReasonList, categoryType);
            System.assertEquals(interaction.get('isSuccess'), 'true');
            
            categoryType = GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_SCH_ONSITE;
            interaction = CRMRetail_InteractionsEvents_LC_HUM.createNewInteractions(sinteractionList, sintReasonList, categoryType); 
            System.assertEquals(interaction.get('isSuccess'), 'true');
                        
            categoryType = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_EVENTRECOMMENDATION;
            interaction = CRMRetail_InteractionsEvents_LC_HUM.createNewInteractions(sinteractionList, sintReasonList, categoryType);
            string recevtInt = interaction.get('successList');
            List<string> recEvntLst = (List<string>)Json.deserialize(recevtInt,List<string>.class);
            List<Storefront_Interaction__c> InteractionList1 = [select Storefront_Is_EventRecmded__c,Id,Interaction_Date__c,Name,Account__c,Account__r.Name,Reason__c,Reason__r.Name,Location__c,IsAllDayEvent__c,Category__c from Storefront_Interaction__c where Id=:recEvntLst[0] LIMIT 1];
            System.assertEquals(InteractionList1[0].Storefront_Is_EventRecmded__c, true);

        }
    }

    /*
    * This method cover createNewInteractions for Health Educator type
    * @param None
    * @return None
    */
    @isTest static void testHealthEducatorInteractions()
    {
        User testUser = [SELECT Id, LastName FROM User WHERE LastName = 'test UserRetail'];
        String locationVal = '';
        
        System.assertEquals('test UserRetail',testUser.LastName);
        
        system.runAs(testUser)
        {            
            HUM_Webservice_Callout__c hmClout = [SELECT Id,Name,Request_Type__c,Certificate_Name__c,Active__c,End_Point_URL__c,Timeout__c from HUM_Webservice_Callout__c where Name = 'CRMRetail_SDoH' limit 1];
            system.assertEquals('CRMRetail_SDoH',hmClout.Name);
            Test.setMock(HttpCalloutMock.class, new CRMRetail_SDoH_Helper_T_HUM.CRMRetail_SDoH_MockImpl()); 
            test.startTest();               
            Storefront_Location__c locobj =[Select Id,Name from Storefront_Location__c LIMIT 1];
            locationVal = locobj.Id + '-' + locobj.Name;
            Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION, locationVal);
            Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE, String.valueof(date.today()));
            
            List<Storefront_Interaction__c> InteractionList = [select SDoH__c,Account__r.Enterprise_Id__c,New_Member__c,Aging_In__c,Storefront_Event_Starttime__c,Veteran__c,Id,Interaction_Date__c,Name,Account__c,Account__r.Name,Reason__c,Reason__r.Name,Location__c,LASTMODIFIEDDATE,IsAllDayEvent__c,CRM_Retail_Interaction_Visitor_Type__c,Visitor_ID__c, Category__c,Storefront_Vstr_Indicator__c from Storefront_Interaction__c where Category__c=:GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_SCH_ONSITE OR category__c=:GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_SCH_VIRTUAL LIMIT 1];            
            system.assertEquals(1, InteractionList.size());
            List<CRMRetail_InteractionsEvents_LC_HUM.EventsWrapper> eventWrapper = new List<CRMRetail_InteractionsEvents_LC_HUM.EventsWrapper>();
            
            List<Storefront_I_Reason__c> lstReason = [select id,name from Storefront_I_Reason__c where name=:GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_HEALTH_EDUCATOR_REASON LIMIT 1];
            system.assertEquals(1,lstReason.size());
            
            CRMRetail_InteractionsEvents_LC_HUM.EventsWrapper wrapper = new CRMRetail_InteractionsEvents_LC_HUM.EventsWrapper();
            wrapper.ReasonId = lstReason[0].id;
            wrapper.IsAllDayEvent = true;
            wrapper.StartDateTime = System.now();
            wrapper.EndDateTime = System.now();
            wrapper.ReasonName = lstReason[0].Name;
            eventWrapper.add(wrapper);
            
            System.assertEquals(wrapper.ReasonName, lstReason[0].Name);
            
            String sinteractionList=json.serialize(InteractionList);
            String sintReasonList = json.serialize(eventWrapper);
            String categoryType = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_CATEGORY_ONSITE.toLowercase();
                                    
            Map<String,String> interaction= CRMRetail_InteractionsEvents_LC_HUM.createNewInteractions(sinteractionList, sintReasonList, categoryType);
            System.assertEquals(interaction.get('isSuccess'), 'true'); 
        }
    }
    
    /* This method cover catch block of createScheduledInteraction
    * @param None
    * @return None
    */
    @isTest static void testcreateScheduledInteraction()
    {
        User testUser = [SELECT Id, LastName FROM User WHERE LastName = 'test UserRetail'];
        String category='virtual';
        
        System.assertEquals('test UserRetail',testUser.LastName);
        
        Storefront_Location__c locobj =[Select Id,Name,Location_Type__c from Storefront_Location__c LIMIT 1];
        system.runAs(testUser)
        {
            
            test.startTest();               
            try{
                CRMRetail_InteractionsEvents_LC_HUM.createScheduledInteraction(new List<Storefront_Interaction__c>(),category,locobj);
            }
            catch(Exception e){
                System.assertEquals('System.AuraHandledException', e.getTypeName());
            }            
            test.stopTest();   
        }
    }

}