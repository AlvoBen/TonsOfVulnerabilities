/******************************************************************************************************************
Apex class Name    : CRMRetail_Contacted_T_HUM
Version            : 1.0 
Function           : This class is used as test class for contacted button of the task
Created On         : 29th Sept, 2021
Test Class         : 
 
Modification Log:
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0            Ekta Bist                                       9/29/2021            Original Version 2561850 
*   2.0            Vivek Sharma                                    08/22/2022           User Story 3581472: T1PRJ0154546 / SF / MF9 Storefront: Ability to Create Follow Up Task From Visitor Check-Ins
*   3.0            Vinoth L                                        09/14/2022           Task Import Case Sensitivity Fix
*   4.0            Vinoth L                                        11/28/2022           REQ-4012388 T1PRJ0154546 / SF/ MF9 Storefront - Ability to View First Time Visit - Currebnt CY in Storefront
*   5.0			   Vinoth L                      				   01/05/2023           User Story 4107848: T1PRJ0154546 / SF / MF3 Storefront: Update Visitor ID & Type Population Logic
------------------------------------------------------------------------------------------------------------------- */
@isTest
public class CRMRetail_Contacted_T_HUM {
    /**
    * This method is used to insert the data only once and called automicticaly in all methods 
    *
    * @param  None      
    * @return  Void
    */
    @testSetup static void setup()
    {   
        List<user> lstUsers = new List<user>();
        User oUser = HUMTestDataHelperTest.createTestUser('User2','CRMRetail User');        
        lstUsers.add(oUser);
        insert lstUsers; 
        System.assertEquals('test User2',lstUsers[0].LastName);
        
        PermissionSet SuperAdmin = [SELECT Id FROM PermissionSet WHERE Name = 'CRMRetail_400_Super_Admin'];
        PermissionSetAssignment supAdmin = new PermissionSetAssignment(AssigneeId = oUser.id, PermissionSetId = SuperAdmin.Id);
        insert supAdmin;
        system.assertEquals(supAdmin.AssigneeId, oUser.id);
        
        Id recordId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
        System.assertNotEquals(recordId, null);
        
        System.runAs(oUser) 
        {   
            List<Account> listAccount = new List<Account>();
            
            Account objAcc = CRMRetail_TestDataFactory_H_HUM.createObjAccount(recordId, '09/13/1990', 'Luke','Lee','','','','', false);
            objAcc.Enterprise_ID__c = '12345';
            listAccount.add(objAcc);
            
            Account objAc = CRMRetail_TestDataFactory_H_HUM.createObjAccount(recordId, '09/13/1990', 'Adam','Lee','','','','', false);  
            listAccount.add(objAc);
            
            insert listAccount;
            System.assertEquals(listAccount.size(), 2);
            
            Storefront_I_Type__c objIntType = CRMRetail_TestDataFactory_H_HUM.createInteractionType('Consumer Edu');
            
            insert objIntType;
            System.assertEquals(objIntType.Name,'Consumer Edu');
            
            List<Storefront_I_Reason__c> lstReason = new List<Storefront_I_Reason__c>();
            
            Storefront_I_Reason__c objIntReason = CRMRetail_TestDataFactory_H_HUM.createInteractionReason('consumer test',null);
            objIntReason.Interaction_Type__c = objIntType.Id;
            lstReason.add(objIntReason);
            
            Storefront_I_Reason__c objIntReason1 = CRMRetail_TestDataFactory_H_HUM.createInteractionReason('Visitor Check-In',null);
            objIntReason1.Interaction_Type__c = objIntType.Id;
            lstReason.add(objIntReason1);
            
            Storefront_I_Reason__c objIntReason2 = CRMRetail_TestDataFactory_H_HUM.createInteractionReason(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_FIRSTSTIME_CHECKIN,null);
            objIntReason2.Interaction_Type__c = objIntType.Id;
            lstReason.add(objIntReason2);
            
            insert lstReason;
            System.assertEquals(lstReason.size(),3);
            
            Storefront_Location__c location = CRMRetail_TestDataFactory_H_HUM.createLocation('Tulsa','Street no. 1','Texas','12345','83684','california',23);
            
            insert location;
            System.assertEquals(location.Name,'Tulsa');            
            
            List<Storefront_Interaction__c> lstInteractions= new List<Storefront_Interaction__c>();
            
            Storefront_Interaction__c objInteraction1 = CRMRetail_TestDataFactory_H_HUM.createInteraction(listAccount[0].Id, location.Id, lstReason[0].Id, false, 'Member', null);
            objInteraction1.Visitor_ID__c = 'H123457437';
            objInteraction1.Category__c = 'Virtual';
            objInteraction1.Interaction_Date__c =  System.Today();
            lstInteractions.add(objInteraction1);
            
            Storefront_Interaction__c objInteract = CRMRetail_TestDataFactory_H_HUM.createInteraction(listAccount[0].Id, location.Id, lstReason[1].Id, false, 'Member', null);
            objInteract.Category__c = 'Virtual';
            objInteract.Visitor_ID__c = '';
            objInteract.Interaction_Date__c =  System.Today();
            lstInteractions.add(objInteract);
            
            insert lstInteractions;
            System.assertEquals(lstInteractions.size(),2);
            
            List<CRMFunctionality_ONOFF_Switch__c> lstOfSwitches = new List<CRMFunctionality_ONOFF_Switch__c>();
            CRMFunctionality_ONOFF_Switch__c csCRMOnOffSwitch = new  CRMFunctionality_ONOFF_Switch__c(Name = '1243084', IsON__c = True);
            CRMFunctionality_ONOFF_Switch__c Switch_2561850 = new CRMFunctionality_ONOFF_Switch__c(Name = '2561850',IsON__c = true);
            CRMFunctionality_ONOFF_Switch__c Switch_3573183   = new CRMFunctionality_ONOFF_Switch__c(Name = '3573183  ',IsON__c = true); 
            CRMFunctionality_ONOFF_Switch__c Switch_4012388   = new CRMFunctionality_ONOFF_Switch__c(Name = '4012388  ',IsON__c = true); 
            lstOfSwitches.add(csCRMOnOffSwitch);
            lstOfSwitches.add(Switch_2561850);
            lstOfSwitches.add(Switch_3573183);
            lstOfSwitches.add(Switch_4012388);
            insert lstOfSwitches;                        
            System.assertEquals(lstOfSwitches.get(0).Name,'1243084');
            system.assertEquals(true, lstOfSwitches.get(1).IsON__c);
            
            List<Task> lstTask = new List<Task>();
            
            Task objTask = new Task(Subject = 'consumer test', status = 'Not Started', CRM_Retail_Location__c = location.Id, WhatId = listAccount[0].Id,CRMRetail_Automated__c=true);
            lstTask.add(objTask);
            
            Task objT = new Task(Subject = 'consumer test', Priority = 'Low' ,status = 'Not Started', CRM_Retail_Location__c = location.Id, WhatId = listAccount[1].Id,CRMRetail_Automated__c=true);
            lstTask.add(objT);
            
            Task obTask1 = new Task(Subject = 'consumer test', Priority = 'Low', status = 'Not Started', CRM_Retail_Location__c = location.Id, WhatId = listAccount[0].Id,CRMRetail_Automated__c=false);
            lstTask.add(obTask1);
            
            Task obTask2 = new Task(Subject = 'consumer test', Priority = 'High', status = 'Not Started', CRM_Retail_Location__c = location.Id, WhatId = listAccount[0].Id,CRMRetail_Automated__c=false);
            lstTask.add(obTask2);
            
            insert lstTask;
            System.assertEquals(true,lstTask[0].CRMRetail_Automated__c);
            System.assertEquals(true,lstTask[1].CRMRetail_Automated__c);
            System.assertEquals(false,lstTask[2].CRMRetail_Automated__c);
            System.assertEquals(false,lstTask[3].CRMRetail_Automated__c);
            
            if(!Cache.Session.contains('currentLocation')){
              Cache.Session.Put('currentLocation',location.Id +'-'+ location.Name);
            }
            String name = location.name;
            CRMRetail_HomePage_LC_HUM.getorSetLocationInCache('',false);
            string LName = (String)Cache.Session.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION);
            LName = LName.split('-', 2)[1];
            system.assertEquals(LName,location.name);
            
            Date resetDate = Date.today() - 90; 
            Map<String,Set<String>> mpOfStNotif = new Map<String,Set<String>>(); 
            List<Storefront_Notification__C> lstOfStfNotifications = new List<Storefront_Notification__C>();
            Set<String> stOfNotifTypes = new Set<String>();
            stOfNotifTypes = GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_INTERACTION_NOTIFICATION_FIELDS;
            stOfNotifTypes.add('First Time Visitor');            
            mpOfStNotif.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER,stOfNotifTypes);            
            mpOfStNotif.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_NON_MEMBER,stOfNotifTypes); 
            Storefront_Notification__C notificationRec;
            for(String notifTypes: mpOfStNotif.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER)){
                notificationRec = CRMRetail_TestDataFactory_H_HUM.createNotification(notifTypes,location.Id,GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER,'Follow',23,False,resetDate,20.0,50.0);
                lstOfStfNotifications.add(notificationRec);
            } 
            for(String notifTypes: mpOfStNotif.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_NON_MEMBER)){
                notificationRec = CRMRetail_TestDataFactory_H_HUM.createNotification(notifTypes,location.Id,GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_NON_MEMBER,'Follow',23,False,resetDate,20.0,50.0);                
                lstOfStfNotifications.add(notificationRec); 
            } 
            insert lstOfStfNotifications; 
            system.assertEquals(12,lstOfStfNotifications.size());  
            
            if(!Cache.Session.contains('currentLocation')){
                Cache.Session.Put('currentLocation',location.Id +'-'+ location.Name);
            }
            if(!Cache.Session.contains('currentInteractionDate')) {
                Datetime myDateTime = system.Now(); 
                String formatted = myDateTime.formatGMT('yyyy-MM-dd');
                Cache.Session.Put('currentInteractionDate', formatted);
            }  
        }
    }        
    @isTest static void testCompletedTask()
    {                
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');        
        system.runAs(oUser)
        {
            Test.startTest();
            List<Task> lstTask = new List<Task>();
            lstTask = [Select id, Status,Subject,CRM_Retail_Location__c, WhatId from Task where Status = 'Not Started' AND Priority = 'High' AND CRMRetail_Automated__c=false LIMIT 1];            
            System.assertEquals('Not Started',lstTask[0].Status);            
            lstTask[0].Status = 'Completed';
            update lstTask[0];
            System.assertEquals('Completed',lstTask[0].Status);
            lstTask[0].CRMRetail_Automated__c = true;
            update lstTask[0];
            System.assertEquals(true,lstTask[0].CRMRetail_Automated__c);
            Map<String,Object> mapOfNotificationWrap = CRMRetail_Contacted_HUM.getInteractionInfo(lstTask[0].Id);
            System.assertEquals(true,mapOfNotificationWrap.containsKey(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK_WARNING_KEY));
            System.assertEquals(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK_COMPLETED_MESSAGE,mapOfNotificationWrap.get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK_WARNING_KEY));
            
            List<Task> lstTask1 = [Select id, Status,Subject,CRM_Retail_Location__c, WhatId from Task where CRMRetail_Automated__C = false LIMIT 1];            
            System.assertEquals(lstTask1.size(), 1);
            Map<String,Object> mapOfNotificationWrap1 = CRMRetail_Contacted_HUM.getInteractionInfo(lstTask1[0].Id);            
            System.assertEquals(true,mapOfNotificationWrap1.containsKey(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK_WARNING_KEY));
            System.assertEquals(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK_COMPLETED_MESSAGE,mapOfNotificationWrap1.get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK_WARNING_KEY));
            Test.stopTest();
        }
    }
    
    @isTest static void testInProgressTask()
    {  
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        {
            Test.startTest();
            List<Task> lstTask = new List<Task>();
            List<Task> lstTaskToUpd = new List<Task>();
            lstTask = [Select id, Status , Subject,CRM_Retail_Location__c, WhatId  from Task where Status != 'Completed'  AND Priority = 'Low'];
            System.assertEquals(lstTask.size(), 2);
            
            Storefront_I_Reason__c sfReason = [SELECT Id,Name,Inactive__c from Storefront_I_Reason__c where Name='consumer test'];
            sfReason.Inactive__c = true;
            update sfReason;
            System.assertEquals(true,sfReason.Inactive__c);
            
            Map<String,Object> mapOfNotificationWrap2 = CRMRetail_Contacted_HUM.getInteractionInfo(lstTask[0].Id);
            System.assertEquals(mapOfNotificationWrap2.get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK_ERROR_KEY),GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK_INACTIVE_MESSAGE);
            
            sfReason.Name = lstTask.get(0).Subject + 'Other';
            update sfReason;
            System.assertEquals(lstTask.get(0).Subject + 'Other',sfReason.Name);
            
            Map<String,Object> mapOfNotificationWrap1 = CRMRetail_Contacted_HUM.getInteractionInfo(lstTask[0].Id);            
            System.assertEquals(true,mapOfNotificationWrap1.containsKey(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK_ERROR_KEY)); 
            
            for(Task tk: lstTask){
                tk.subject = null;
                lstTaskToUpd.add(tk);
            }
            update lstTaskToUpd ;
            System.assertEquals(lstTaskToUpd.size(), 2);
            
            mapOfNotificationWrap1 = CRMRetail_Contacted_HUM.getInteractionInfo(lstTask[0].Id);            
            System.assertEquals(true,mapOfNotificationWrap1.containsKey(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK_ERROR_KEY));                        
            
            CRMFunctionality_ONOFF_Switch__c Switch_2561850 = [SELECT id,IsON__c FROM CRMFunctionality_ONOFF_Switch__c WHERE Name = '2561850' LIMIT 1];
            Switch_2561850.IsON__c = false; 
            update Switch_2561850;
            System.assertEquals(Switch_2561850.IsON__c,false);
            Map<String,Object> mapOfNotificationWrap = CRMRetail_Contacted_HUM.getInteractionInfo(lstTask[0].Id);            
            System.assertEquals(true,mapOfNotificationWrap1.containsKey(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK_ERROR_KEY));
            Test.stopTest();
        }
    }       
    
    @isTest static void testVirtualLocation()
    {  
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        {
            Test.startTest();
            List<Task> lstTask = new List<Task>();
            lstTask = [Select id, Status,Subject,CRM_Retail_Location__c, WhatId  from Task where Status != 'Completed'  AND Priority = 'Low'];            
            System.assertEquals(lstTask.size(), 2); 
            
            List<Storefront_Location__c> lsstLocation = new List<Storefront_Location__c>();
            lsstLocation = [Select id, Location_Type__c from Storefront_Location__c Limit 1];
            System.assertEquals(lsstLocation.size(), 1);
            
            lsstLocation[0].Location_Type__c = GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_VIRTUAL;
            update lsstLocation[0];  
            System.assertEquals(lsstLocation[0].Location_Type__c, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_VIRTUAL);
             
            Map<String,Object> mapOfNotificationWrap = CRMRetail_Contacted_HUM.getInteractionInfo(lstTask[0].Id);                        
            System.assertEquals(false,mapOfNotificationWrap.containsKey(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK_ERROR_KEY));              
            Test.stopTest();
        }
    }    
    
    @isTest static void testOtherTask1()
    {  
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        {            
            Test.startTest();
            String currentLocation;
            List<Account> oAcc = [select Id, Waiver_Date__c,LastName from Account Limit 1]; 
            system.assertEquals(oAcc[0].Waiver_Date__c, null);
            boolean flag=true;  
            String  jsonInput='[{"waiverDate":"'+ flag +'","accId":"' + oAcc.get(0).Id + '"},{"veteran":"'+ flag +'","accId":"' + oAcc.get(0).Id + '"},{"agingIn":"'+ flag +'","accId":"' + oAcc.get(0).Id + '"},{"SDoH":"'+ flag +'","accId":"' + oAcc.get(0).Id + '"},{"newMember":"'+ flag +'","accId":"' + oAcc.get(0).Id + '"}]';                        
            List<Storefront_Location__C> currLoc = [Select Id,Name,Location_Type__C from Storefront_Location__C where Name= 'Tulsa' LIMIT 1];
            system.assertEquals(currLoc.get(0).Name,'Tulsa');
            currentLocation = currLoc.get(0).Id;
            List<String> accIds = new List<String>();            
            accIds.add(oAcc[0].Id);
            Boolean bool = CRMRetail_Contacted_HUM.acknowledgeNotifications(jsonInput,accIds,currentLocation);
            system.assertEquals(bool, true);            
            Test.stopTest();                       
        }
    }        
    @isTest static void testDuplicateInteraction()
    {  
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        {            
            Test.startTest();
            String currentLocation;
            Account oAcc = [select Id, Waiver_Date__c,FirstName,LastName from Account where FirstName='Luke' and LastName='Lee' Limit 1]; 
            system.assertEquals('Luke',oAcc.FirstName);
            system.assertEquals('Lee',oAcc.LastName);
            Task tsk = [select Id,WhatId,WhoId from Task where CRMRetail_Automated__c = true and WhatId =: oAcc.Id Limit 1];	 
            system.assertEquals(oAcc.Id,tsk.WhatId);
            if(tsk != NULL){
                Map<String,Object> mapOfNotificationWrap = CRMRetail_Contacted_HUM.getInteractionInfo(tsk.Id);                 
                Storefront_NotificationsWrapper_HUM sfNotWrap = (Storefront_NotificationsWrapper_HUM)mapOfNotificationWrap.get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK_NOTIFICATIONDATA_KEY);                                               
                System.assertEquals(oAcc.Id,sfNotWrap.accountRec.Id);
                Map<String,Boolean> switchMap = CRMRetail_Contacted_HUM.fetchSwitchResults();
                system.assertEquals(true, switchMap.get('Switch_4012388'));
            }
            Test.stopTest();
        }
    }
    @isTest static void testModernizedAndOldFlow()
    {  
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        {
            List<Storefront_Location__c> lsstLocation = new List<Storefront_Location__c>();
            lsstLocation = [Select id, Location_Type__c from Storefront_Location__c Limit 1];
            System.assertEquals(lsstLocation.size(), 1);
            
            lsstLocation[0].Location_Type__c = 'Lite C';
            update lsstLocation[0];  
            System.assertEquals(lsstLocation[0].Location_Type__c, 'Lite C');                       
            
            Test.startTest();
            
            List<Task> lstTask = new List<Task>();
            List<Task> lstTaskToUpd = new List<Task>();
            lstTask = [Select id, Status , Subject,CRM_Retail_Location__c, WhatId  from Task where Status != 'Completed'  AND Priority = 'Low'];
            System.assertEquals(lstTask.size(), 2);
            
            Storefront_I_Reason__c sfReason = [SELECT Id,Name,Inactive__c from Storefront_I_Reason__c where Name='consumer test'];
            sfReason.Inactive__c = false;
            update sfReason;
            System.assertEquals(false,sfReason.Inactive__c);
            
            Map<String,Object> mapOfNotificationWrap = CRMRetail_Contacted_HUM.getInteractionInfo(lstTask[0].Id);
            System.assertEquals(false,mapOfNotificationWrap.containsKey(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK_ERROR_KEY));                                  
            lstTaskToUpd = [Select id, Status , Subject,CRM_Retail_Location__c, WhatId  from Task where Id=: lstTask[0].Id];
            System.assertEquals('Completed',lstTaskToUpd[0].Status);
            
            Test.stopTest();
        }
    }
}