/*******************************************************************************************************************************
Apex Class Name     : CRMRetail_RecommendEvents_T_HUM
Version             : 1.0
Created Date        : 08/05/2022
Function            : This serves as the test class for CRMRetail_RecommendEvents_C_HUM class.

Modification Log: 
* Developer Name           Code Reveiw                     Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Vivek Sharma                                          08/05/2022                  Original version
* Vinoth L                                              09/20/2022                  09/23 QAS Fix
* Vinoth L                                              02/01/2023                  02/24 Test class Fix
*******************************************************************************************************************************/
@isTest 
private class CRMRetail_RecommendEvents_T_HUM {
    /*
    * This method creates test data for test class
    * @param None
    * @return None
    */
    @testSetup static void setUpTestData() 
    {
        List<User> lstOfUsers = new List<User>();
        User u= HUMTestDataHelperTest.createTestUser('UserTest','CRMRetail User');        
        User u1= HUMTestDataHelperTest.createTestUser('User_Read_Only','Read Only');
        
        lstOfUsers.add(u);                        
        lstOfUsers.add(u1);
        insert lstOfUsers;
        
        system.assertEquals('test UserTest',lstOfUsers.get(0).lastname);
        system.assertEquals('test User_Read_Only',lstOfUsers.get(1).lastname);
        
        List<PermissionSetAssignment> lstOfPermissionSetAssignments = new List<PermissionSetAssignment>();
        PermissionSet SuperAdmin = [SELECT Id FROM PermissionSet WHERE Name = 'CRMRetail_400_Super_Admin'];        
        PermissionSetAssignment supAdmin = new PermissionSetAssignment(AssigneeId = lstOfUsers[0].id, PermissionSetId = SuperAdmin.Id);
        lstOfPermissionSetAssignments.add(supAdmin);            
        
        insert lstOfPermissionSetAssignments;        
        system.assertEquals(lstOfUsers[0].id,lstOfPermissionSetAssignments.get(0).AssigneeId);
        
        Id MemberRecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER).getRecordTypeId();
                
        system.runas(u){
            List<Account> lstOfAccounts = new List<Account>();
            Account oAcc1 = CRMRetail_TestDataFactory_H_HUM.createAccountInstance('H12345678', MemberRecordTypeId,Date.newInstance(1984, 01, 01),'01/01/1984', 'Test', 'Account', 'O', '1060920018',false, 'New York', '56605', '(123) 123-1231','test1234',Date.today()-100,null,NULL);
            lstOfAccounts.add(oAcc1);
            insert lstOfAccounts;
            system.assertNotEquals(null,lstOfAccounts[0].Id);
            
            List<CRMFunctionality_ONOFF_Switch__c> listOfSwitch = new List<CRMFunctionality_ONOFF_Switch__c>();
            CRMFunctionality_ONOFF_Switch__c switch_3510484 = new CRMFunctionality_ONOFF_Switch__c(Name = '3510484',IsON__c=true);
            listOfSwitch.add(switch_3510484);
            insert listOfSwitch;
            system.assertEquals(listOfSwitch.size() ,1);
            
            List<Storefront_Location__c> listOfLocations = new List<Storefront_Location__c>();
            Storefront_Location__c location1 = CRMRetail_TestDataFactory_H_HUM.createLocation('Recommendation Testing','Tampa2','Tampa2','66512','66512','',50);
			Storefront_Location__c location2 = CRMRetail_TestDataFactory_H_HUM.createLocation('Recommendation Testing 2','Tampa3','Tampa3','66513','66513','',50);            
            Storefront_Location__c location3 = CRMRetail_TestDataFactory_H_HUM.createLocation('Recommendation Testing 3','Tampa4','Tampa4','66514','66514','',50);            
            listOfLocations.add(location1);
            listOfLocations.add(location2);
            listOfLocations.add(location3);
            insert listOfLocations; 
            system.assertEquals(3,listOfLocations.size());
            system.assertNotEquals(null,listOfLocations[0].Id); 
            system.assertNotEquals(null,listOfLocations[1].Id);
            system.assertNotEquals(null,listOfLocations[2].Id);
                        
            List<Storefront_I_Type__c> lstSfIntTypes = new List<Storefront_I_Type__c>();
            Storefront_I_Type__c InteractionType1 = CRMRetail_TestDataFactory_H_HUM.createInteractionType('Social1');
            lstSfIntTypes.add(InteractionType1);            
            Storefront_I_Type__c InteractionType2 = CRMRetail_TestDataFactory_H_HUM.createInteractionType('Community1');
            lstSfIntTypes.add(InteractionType2);            
            Storefront_I_Type__c InteractionType3 = CRMRetail_TestDataFactory_H_HUM.createInteractionType('Health Training1');
            lstSfIntTypes.add(InteractionType3);
            
            Storefront_I_Type__c InteractionType4 = CRMRetail_TestDataFactory_H_HUM.createInteractionType('National-Health & Lifestyle1');
            lstSfIntTypes.add(InteractionType4);
            Storefront_I_Type__c InteractionType5 = CRMRetail_TestDataFactory_H_HUM.createInteractionType('Consumer Education1');
            lstSfIntTypes.add(InteractionType5);
            Storefront_I_Type__c InteractionType6 = CRMRetail_TestDataFactory_H_HUM.createInteractionType('General1');
            lstSfIntTypes.add(InteractionType6);
            Storefront_I_Type__c InteractionType7 = CRMRetail_TestDataFactory_H_HUM.createInteractionType('Social Education1');
            lstSfIntTypes.add(InteractionType7);
            Storefront_I_Type__c InteractionType8 = CRMRetail_TestDataFactory_H_HUM.createInteractionType('Social Education2');
            lstSfIntTypes.add(InteractionType8);
            
            insert lstSfIntTypes;           
            system.assertEquals(8,lstSfIntTypes.size());
                        
            List<Storefront_I_Reason__c> interactionReason = new List<Storefront_I_Reason__c>();
            Storefront_I_Reason__c InteractionReason1 = CRMRetail_TestDataFactory_H_HUM.createInteractionReason('Visitor Referral Channel1',InteractionType1.Id);
            interactionReason.add(InteractionReason1);            
            Storefront_I_Reason__c InteractionReason2 = CRMRetail_TestDataFactory_H_HUM.createInteractionReason('Phone Call1' ,InteractionType1.Id);
            interactionReason.add(InteractionReason2);
            Storefront_I_Reason__c InteractionReason3 = CRMRetail_TestDataFactory_H_HUM.createInteractionReason('INT REASON121' ,InteractionType1.Id);
            interactionReason.add(InteractionReason3);
            Storefront_I_Reason__c InteractionReason4 = CRMRetail_TestDataFactory_H_HUM.createInteractionReason('Vitality Week - Cooking Demo1' ,InteractionType2.Id);
            interactionReason.add(InteractionReason4);
            Storefront_I_Reason__c InteractionReason5 = CRMRetail_TestDataFactory_H_HUM.createInteractionReason('Community Use - Local Community Partner1' ,InteractionType2.Id);
            interactionReason.add(InteractionReason5);
            Storefront_I_Reason__c InteractionReason6 = CRMRetail_TestDataFactory_H_HUM.createInteractionReason('CRMRetail_RecommendEvents_C_HUM Test11' ,InteractionType3.Id);
            interactionReason.add(InteractionReason6);
            Storefront_I_Reason__c InteractionReason7 = CRMRetail_TestDataFactory_H_HUM.createInteractionReason('Technology - Technology Class 21' ,InteractionType4.Id);
            interactionReason.add(InteractionReason7);
            Storefront_I_Reason__c InteractionReason8 = CRMRetail_TestDataFactory_H_HUM.createInteractionReason('Social - Volunteer/Charity Drive1' ,InteractionType5.Id);
            interactionReason.add(InteractionReason8);
            Storefront_I_Reason__c InteractionReason9 = CRMRetail_TestDataFactory_H_HUM.createInteractionReason('Spanish New Member Orientation or ANOC1' ,InteractionType6.Id);
            interactionReason.add(InteractionReason9);
            Storefront_I_Reason__c InteractionReason10 = CRMRetail_TestDataFactory_H_HUM.createInteractionReason('Activity Craft1' ,InteractionType7.Id);
            interactionReason.add(InteractionReason10); 
            Storefront_I_Reason__c InteractionReason11 = CRMRetail_TestDataFactory_H_HUM.createInteractionReason('Visitor Check-In' ,InteractionType7.Id);
            interactionReason.add(InteractionReason11);
            Storefront_I_Reason__c InteractionReason12 = CRMRetail_TestDataFactory_H_HUM.createInteractionReason('Social - Volunteer/Charity Drive2' ,InteractionType8.Id);
            interactionReason.add(InteractionReason12);
            Storefront_I_Reason__c InteractionReason13 = CRMRetail_TestDataFactory_H_HUM.createInteractionReason('Visitor Check-In (1st Time)' ,InteractionType8.Id);
            interactionReason.add(InteractionReason13);
            insert InteractionReason;            
            system.assertEquals(13,InteractionReason.size());
            
            List<Storefront_Interaction__c> gcmInteractions = new List<Storefront_Interaction__c>();
            Storefront_Interaction__c GcmInteraction;
            for(Storefront_Location__c locObj : listOfLocations){
                if(locObj.Name != 'Recommendation Testing 3'){
                    GcmInteraction = CRMRetail_TestDataFactory_H_HUM.createInteraction(lstOfAccounts.get(0).Id,locObj.Id,InteractionReason.get(0).Id, True,'Member', system.today()-29); 
                    gcmInteractions.add(GcmInteraction);
                    GcmInteraction = CRMRetail_TestDataFactory_H_HUM.createInteraction(lstOfAccounts.get(0).Id,locObj.Id,InteractionReason.get(1).Id, True,'Member', system.today()-1); 
                    gcmInteractions.add(GcmInteraction); 
                }                
            }
            for(Storefront_I_Reason__c sfRsn: interactionReason){
                for(Storefront_Location__c locObj : listOfLocations){
                    if(locObj.Name != 'Recommendation Testing 3'){
                        GcmInteraction = CRMRetail_TestDataFactory_H_HUM.createInteraction(lstOfAccounts.get(0).Id,locObj.Id,sfRsn.Id, True,'Member', system.today()-29); 
                        gcmInteractions.add(GcmInteraction);
                        GcmInteraction = CRMRetail_TestDataFactory_H_HUM.createInteraction(lstOfAccounts.get(0).Id,locObj.Id,sfRsn.Id, True,'Member', system.today()-1); 
                        gcmInteractions.add(GcmInteraction); 
                    }                
                }
            }                        
            Storefront_I_Reason__C gcmIntRsn = [select id,Name from Storefront_I_Reason__c where Name='Visitor Check-In' LIMIT 1];            
            system.assertEquals('Visitor Check-In',gcmIntRsn.Name);
            GcmInteraction = CRMRetail_TestDataFactory_H_HUM.createInteraction(lstOfAccounts.get(0).Id,listOfLocations.get(0).Id,gcmIntRsn.Id, True,'Member', system.today()); 
            gcmInteractions.add(GcmInteraction);
            Date dt = system.today()+11;
            GcmInteraction = CRMRetail_TestDataFactory_H_HUM.createInteraction(lstOfAccounts.get(0).Id,listOfLocations.get(0).Id,InteractionReason12.Id, True,'Member', dt); 
            GcmInteraction.Storefront_Event_Starttime__c = dt;
            gcmInteractions.add(GcmInteraction);
            insert gcmInteractions;
            system.assertEquals(58,gcmInteractions.size());
            
            List<Event> events = new List<Event>();            
            event evnt;
            for(Integer i=0;i<2;i++){
                evnt = CRMRetail_TestDataFactory_H_HUM.createEvent(InteractionReason.get(i).id ,listOfLocations.get(0).Id ,system.Now().addDays(10).addHours(4) ,system.Now().addDays(10).addHours(5), System.today().addDays(10),false, 'Busy',1);            
                events.add(evnt);  
                evnt = CRMRetail_TestDataFactory_H_HUM.createEvent(InteractionReason.get(i).id ,listOfLocations.get(0).Id ,system.Now().addDays(4).addHours(4) ,system.Now().addDays(4).addHours(5), System.today().addDays(4),false, 'Busy',1);            
                events.add(evnt);
            }  
            for(Integer i=9;i>=3;i--){
                evnt = CRMRetail_TestDataFactory_H_HUM.createEvent(InteractionReason.get(i).id ,listOfLocations.get(0).Id ,system.Now().addDays(10).addHours(4) ,system.Now().addDays(10).addHours(5), System.today().addDays(10),false, 'Busy',1);            
                events.add(evnt);
            }
                    
            insert events;
            System.assertEquals(11,events.size());
            System.assertEquals(InteractionReason.get(0).id,events[0].Reason__c);
            System.assertEquals(InteractionReason.get(0).id,events[1].Reason__c);
            
            List<HUMTriggerSwitch__c> triggerSwitchList = new List<HUMTriggerSwitch__c>();
            HUMTriggerSwitch__c triggerSwitch = new HUMTriggerSwitch__c();
            triggerSwitch.Exeute_Trigger__c = true;
            triggerSwitch.Name = 'Storefront_Interaction__c';
            triggerSwitchList.add(triggerSwitch);  
            insert triggerSwitchList;            
            system.assertEquals(triggerSwitchList.size(), 1);
        } 
        
    }
    /*
    * This method is used to cover the upcoming events scenario 
    * & no-recommendation for events in next 7 days
    * @param None
    * @return None
    */
    @isTest static void testUpcomingEvents(){
        User testUser1 = [SELECT Id, LastName FROM User WHERE LastName = 'test UserTest'];
        System.assertEquals('test UserTest',testUser1.LastName);
        
        test.startTest();  
        system.runAs(testUser1)
        {            
            Storefront_Location__c locobj =[Select Id,Name from Storefront_Location__c where Name='Recommendation Testing 2' LIMIT 1];
            String locationVal = locobj.Id + '-' + locobj.Name;
            string formatedDate = date.today().year() + '-' + date.today().month() + '-' + date.today().day();            
            Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION, locationVal);        
            Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE,formatedDate);
            
            Account acc = [SELECT Id,Birthdate__c, Name FROM Account Where CreatedById =: testUser1.Id LIMIT 1];
            System.assertEquals('01/01/1984',acc.Birthdate__c);
            
            List<Event> lstEvents = [SELECT Id from Event where What.Name='Recommendation Testing 2'];
            if(lstEvents != NULL && lstEvents.size() > 0){
                delete lstEvents;
            }
            List<Event> lstEvent = [SELECT Id from Event where What.Name='Recommendation Testing 2'];
            System.assertEquals(0,lstEvent.size());
            Storefront_I_Type__c sfIType = CRMRetail_TestDataFactory_H_HUM.createInteractionType('Upcoming Event Type');
            insert sfIType;
            Storefront_I_Type__c sfType = [SELECT Id,Name from Storefront_I_Type__c where Name='Upcoming Event Type' LIMIT 1];
            system.assertEquals(sfType.Name,sfIType.Name);
            Storefront_I_Reason__c sfIRsn = CRMRetail_TestDataFactory_H_HUM.createInteractionReason('Upcoming Event', sfIType.Id);
            insert sfIRsn;
            Storefront_I_Reason__C sfRsn = [SELECT Id,NAme from Storefront_I_Reason__C where Name='Upcoming Event' LIMIT 1];
            System.assertEquals(sfRsn.Name,sfIRsn.Name);
            Event evnt = CRMRetail_TestDataFactory_H_HUM.createEvent(sfIRsn.Id,locobj.Id ,system.Now().addDays(4).addHours(4) ,system.Now().addDays(4).addHours(5), System.today().addDays(4),false, 'Busy',0);            
            insert evnt;    
            Event sfEvt = [SELECT Id,Reason__r.Name from Event where Reason__r.Name='Upcoming Event' LIMIT 1];
            system.assertEquals('Upcoming Event',sfEvt.Reason__r.Name);
             
            Map<String,Map<String,List<CRMRetail_RecommendEvents_C_HUM.RecommendationEventsWrapper>>> returnedMap = CRMRetail_RecommendEvents_C_HUM.getRecommendedEvents(acc.Id);            
            System.assertEquals(True, returnedMap.containsKey(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_UPCOMING_EVENTS));  
            System.assertNotEquals(0, returnedMap.size()); 
            
            Event sfEvent = [SELECT Id,Reason__r.Name from Event where What.Name='Recommendation Testing 2' LIMIT 1];
            system.assertEquals('Upcoming Event',sfEvent.Reason__r.Name);
            sfEvent.StartDateTime = System.today()+10;
            update sfEvent;
            sfEvent = [SELECT Id,Reason__r.Name,StartDateTime from Event where What.Name='Recommendation Testing 2' LIMIT 1];
            System.assertEquals(System.today()+10,sfEvent.StartDateTime);
            returnedMap = CRMRetail_RecommendEvents_C_HUM.getRecommendedEvents(acc.Id);            
            System.assertEquals(True, returnedMap.containsKey(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_NO_EVENTS));                                    
        }
        test.stopTest();
    }
    /*
    * This method is used to cover the no-recommendation scenario
    * @param None
    * @return None
    */
    @isTest static void testNoRecommendations()
    {
        User testUser = [SELECT Id, LastName FROM User WHERE LastName = 'test UserTest'];
        System.assertEquals('test UserTest',testUser.LastName);
        
        test.startTest();  
        system.runAs(testUser)
        {            
            Storefront_Location__c locobj =[Select Id,Name from Storefront_Location__c where Name='Recommendation Testing 3' LIMIT 1];                        
            String locationVal = locobj.Id + '-' + locobj.Name;            
            string formatedDate = date.today().year() + '-' + date.today().month() + '-' + date.today().day();            
            Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION, locationVal);        
            Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE,formatedDate);
            system.assertEquals(formatedDate,Cache.Session.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE));
            system.assertEquals(locationVal,Cache.Session.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION));
            
            Account acc = [SELECT Id,Birthdate__c,CreatedById, Name FROM Account where CreatedById =: testUser.Id LIMIT 1];
            System.assertEquals(acc.CreatedById,testUser.Id);
            System.assertEquals('01/01/1984',acc.Birthdate__c);
            Map<String,Map<String,List<CRMRetail_RecommendEvents_C_HUM.RecommendationEventsWrapper>>> returnedMap = CRMRetail_RecommendEvents_C_HUM.getRecommendedEvents(acc.Id);            
            System.assertNotEquals(0, returnedMap.size());             
            System.assertEquals(True, returnedMap.containsKey(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_NO_EVENTS));
        }
        test.stopTest();
    }
    /*
    * This method is used to cover all the valid scenarios
    * @param None
    * @return None
    */
    @isTest static void testRecommendedEvents()
    {
        User testUser = [SELECT Id, LastName FROM User WHERE LastName = 'test UserTest'];
        System.assertEquals('test UserTest',testUser.LastName);
        
        test.startTest();  
        system.runAs(testUser)
        {            
            Storefront_Location__c locobj =[Select Id,Name from Storefront_Location__c where Name='Recommendation Testing' LIMIT 1];                        
            String locationVal = locobj.Id + '-' + locobj.Name;            
            string formatedDate = date.today().year() + '-' + date.today().month() + '-' + date.today().day();            
            Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION, locationVal);        
            Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE,formatedDate);
            system.assertEquals(formatedDate,Cache.Session.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE));
            system.assertEquals(locationVal,Cache.Session.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION));
            
            Account acc = [SELECT Id,CreatedById, Name FROM Account where CreatedById =: testUser.Id LIMIT 1];
            System.assertEquals(acc.CreatedById,testUser.Id);
            
            Map<String,Map<String,List<CRMRetail_RecommendEvents_C_HUM.RecommendationEventsWrapper>>> returnedMap = CRMRetail_RecommendEvents_C_HUM.getRecommendedEvents(acc.Id);            
            System.assertEquals(True,returnedMap.containsKey(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_RECOMMENDED_EVENTS));                       
            System.assertNotEquals(0, returnedMap.size());			
            
            List<Storefront_Interaction__c> intList = [SELECT category__c,Storefront_Event_Endtime__c, Storefront_Event_Starttime__c, Missing_Event__c, Account__c,Account__r.Name, Aging_In__c, SDoH__c, Veteran__c, New_Member__c, First_Time_Visitor__c, Reason__c, Location__c, Interaction_Date__c, Visitor_ID__c, CRM_Retail_Interaction_Visitor_Type__c, isAllDayEvent__c FROM Storefront_Interaction__c where CreatedById=:testUser.Id AND Reason__r.Name='Visitor check-In' AND Account__c=: acc.Id AND Interaction_Date__C=:system.today() LIMIT 1];                        
            System.assertEquals('Test O Account',intList[0].account__r.Name);
            String intListString = JSON.serialize(intList);            
            Set<String> resonList = returnedMap.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_RECOMMENDED_EVENTS).keySet();            
            List<String> lstStrings = new List<String>();
            lstStrings.addAll(resonList);            
            List<CRMRetail_RecommendEvents_C_HUM.RecommendationEventsWrapper> recEvntLst = returnedMap.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_RECOMMENDED_EVENTS).get(lstStrings[0]);            
            String recMapString = JSON.serialize(recEvntLst);            
            
            Map<String, String> retMap = CRMRetail_RecommendEvents_C_HUM.createNewInteractionsForRecommendation(intListString, recMapString, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_EVENTRECOMMENDATION, True);            
            System.assertEquals('true',retMap.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_ISSUCCESS));
            
            List<Storefront_Interaction__c> lstNew = new List<Storefront_Interaction__c>();
            List<Storefront_Interaction__c> lstNew1 = new List<Storefront_Interaction__c>();    
            Storefront_Interaction__c sfInt = new Storefront_Interaction__c();
            sfInt = CRMRetail_TestDataFactory_H_HUM.getInteractionInstance(intList,'Visitor Referral Channel1',GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_SCH_ONSITE);
            lstNew.add(sfInt);
            Map<String,Storefront_NotificationsWrapper_HUM> mapOfNotificationsWrapToAccId = new Map<String,Storefront_NotificationsWrapper_HUM>();
        	Map<String, String> retMap1 = CRMRetail_HomePage_LC_HUM.commitInteractions(new List<Storefront_Interaction__c>(lstNew),mapOfNotificationsWrapToAccId,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_EVENTRECOMMENDATION);                       
            CRMFunctionality_ONOFF_Switch__c crmSwitch = [SELECT Id,Name,IsOn__c from CRMFunctionality_ONOFF_Switch__c where Name='3510484'];
        	crmSwitch.IsON__c = false;
        	update crmSwitch;
        	crmSwitch = [SELECT Id,Name,IsOn__c from CRMFunctionality_ONOFF_Switch__c where Name='3510484'];
        	system.assertEquals(false,crmSwitch.IsOn__c);
            
            lstNew.clear();
            sfInt = new Storefront_Interaction__c();
            sfInt = CRMRetail_TestDataFactory_H_HUM.getInteractionInstance(intList,'Visitor Referral Channel1',GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_SCH_ONSITE);
            lstNew.add(sfInt);
            Map<String, String> retMap2 = CRMRetail_HomePage_LC_HUM.commitInteractions(new List<Storefront_Interaction__c>(lstNew),mapOfNotificationsWrapToAccId,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_EVENTRECOMMENDATION);            		          
            System.assertEquals('true',retMap.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_ISSUCCESS));
        }
        test.stopTest();   
    }
    /*
    * This method is used to cover exceptions
    * @param None
    * @return None
    */
    @isTest static void testInsufficientTaskAccessOnInsert()
    {
        User testNonStorefrontUser = [SELECT Id, LastName FROM User WHERE LastName = 'test User_Read_Only'];
        System.assertEquals('test User_Read_Only',testNonStorefrontUser.LastName);
        User testUser = [SELECT Id, LastName FROM User WHERE LastName = 'test UserTest'];
        System.assertEquals('test UserTest',testUser.LastName);
        test.startTest();  
        List<Storefront_Interaction__C> intList = new List<Storefront_Interaction__C>(); 
        system.runAs(testUser){
            intList = [SELECT Id, Name, Missing_Event__c, Account__r.Id,Account__r.PersonContactId,Account__r.Name, Aging_In__c, SDoH__c, Veteran__c, New_Member__c, First_Time_Visitor__c, Reason__r.Name,Reason__c, Location__r.Id, Interaction_Date__c, Visitor_ID__c, CRM_Retail_Interaction_Visitor_Type__c, isAllDayEvent__c,Storefront_Event_StartTime__c FROM Storefront_Interaction__c where Interaction_Date__c >=: system.now()+8 LIMIT 1];                                                           
 		}                                                           
        system.runAs(testNonStorefrontUser)
        {
            List<BusinessHours> listBusinesshours = [SELECT id from BusinessHours Where name =: GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_BUSINESSHOURS];            
            try{                
                if(intList != NULL && intList.size() > 0 && listBusinesshours != NULL && listBusinesshours.size() >0){
                    CRMRetail_RecommendEvents_C_HUM.createTasks(intList, listBusinesshours[0]);                              
                }  
                if(intList != NULL && intList.size() > 0){
                    Map<String,Storefront_NotificationsWrapper_HUM> mapOfNotificationsWrapToAccId = new Map<String,Storefront_NotificationsWrapper_HUM>();
                    Map<String, String> retMap2 = CRMRetail_HomePage_LC_HUM.commitInteractions(new List<Storefront_Interaction__c>(intList),mapOfNotificationsWrapToAccId,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_EVENTRECOMMENDATION);                    	
                }
            }catch(exception e){
                system.assertEquals(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_SCRIPT_THROWN_EXCEPTION,e.getMessage());                
            }
        }        
        test.stopTest();
    }
}