/******************************************************************************************************************************
Apex Class Name  : CRMRetail_VisitorInformation_LT_HUM
Version          : 1.0 
Created Date     : 06/29/2022
Function         : Test class to CRMRetail_VisitorInformation_LC_HUM Apex class.
Modification Log :
*    Developer             Code Review            Date                 Description
* ------------------------------------------------------------------------------------------------------------------------------                
*  Sahil Verma	                                 06/29/2022          Original Version - Home Page Modernization
*  Mohamed Thameem	                             07/05/2022          Original Version - Home Page Modernization
*  Vinoth L                                      11/28/2022          REQ-4012388 T1PRJ0154546 / SF/ MF9 Storefront - Ability to View First Time Visit - Currebnt CY in Storefront
*********************************************************************************************************************************/
@isTest
private class CRMRetail_VisitorInformation_LT_HUM {
    
    /*
    * Method Name : prepareTestData
    * Description : Create common test Data
    * Return type : void
    * Parameter   : NA
    */
    @testSetup static void prepareTestData() 
    {       
        List<user> UserList = new List<user>();
        UserList.add(HUMTestDataHelperTest.createTestUser('User1','CRMRetail User'));
        UserList.add(HUMTestDataHelperTest.createTestUser('User2','Standard Platform User'));
        insert UserList;
        System.assert(UserList.size() == 2);
        system.assertEquals(UserList[0].lastname == 'test User1',true);                
                
        PermissionSet SuperAdmin = [SELECT Id FROM PermissionSet WHERE Name =: GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_400SUPERADMIN];
        PermissionSetAssignment supAdmin = new PermissionSetAssignment(AssigneeId =UserList[0].Id, PermissionSetId = SuperAdmin.Id);
        insert supAdmin;
        system.assertEquals(supAdmin.AssigneeId, UserList[0].id);
        
        System.runAs (UserList[0]) 
        {
            Id oUnknownMemRecType = Schema.SObjectType.Account.getRecordTypeInfosByName().get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_RETAIL_VISITOR).getRecordTypeId();
            List<Account> lstOfAccounts = new List<Account>();
            Account oAcc1 = new Account();
            oAcc1.recordtypeid = oUnknownMemRecType;
            oAcc1.Birthdate__c = '01/01/1984';
            oAcc1.firstName = 'Test';
            oAcc1.lastName = 'Account';
            oAcc1.MiddleName = 'J';
            oAcc1.PersonMailingState = 'New York';
            oAcc1.PersonMailingPostalCode = '56605';
            oAcc1.PersonHomePhone = '';                        
            
            Id oMemRecType = Schema.SObjectType.Account.getRecordTypeInfosByName().get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER).getRecordTypeId();
            Account oAcc2 = new Account();
            oAcc2.recordtypeid = Schema.SObjectType.Account.getRecordTypeInfosByName().get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER).getRecordTypeId();
            oAcc2.Birthdate__c = '01/01/1984';
            oAcc2.firstName = 'Test';
            oAcc2.lastName = 'Account';
            oAcc2.MiddleName = 'J';
            oAcc2.PersonMailingState = 'New York';
            oAcc2.PersonMailingPostalCode = '56605';
            oAcc2.PersonHomePhone = '';  
            lstOfAccounts.add(oAcc1);            
            lstOfAccounts.add(oAcc2);
            insert lstOfAccounts;
            System.AssertNotEquals(lstOfAccounts.get(0), NULL);
            System.AssertNotEquals(lstOfAccounts.get(1), NULL);
            
            Storefront_Location__c location1 = new Storefront_Location__c(Name = 'Virginia',Address_Line_1__c = '9005',City__c='Virginia',Zip_Code__c='40222',Org_Facility__c='12445',State__c='Virginia - VA',Maximum_Occupancy__c = 5,Location_Type__c='Lite A');
            insert location1;
            System.AssertEquals(location1.Name, 'Virginia');                         
            
            Storefront_I_Type__c InteractionType1 = new Storefront_I_Type__c(Name ='Social');
            insert InteractionType1;
            
            Storefront_I_Reason__c InteractionReason1 = new Storefront_I_Reason__c(Name = 'Visitor Check-In (1st Time)' ,Interaction_Type__c = InteractionType1.Id);
            insert InteractionReason1;
            system.assertEquals(InteractionReason1.Interaction_Type__c, InteractionType1.id);
            
            Date resetDate = Date.newInstance(2021, 03, 24);
            List<Storefront_Notification__C> lstOfStfNotifications = new List<Storefront_Notification__C>();
            Storefront_Notification__C notificationRec1 = CRMRetail_TestDataFactory_H_HUM.createNotification('First Time Visitor',location1.Id,'Member','Follow',23,False,null,20.0,50.0);
            lstOfStfNotifications.add(notificationRec1);                        
            
            Storefront_Notification__C notificationRec2 = CRMRetail_TestDataFactory_H_HUM.createNotification('Waiver Required',null,'Member','Follow',23,False,null,20.0,50.0);
            lstOfStfNotifications.add(notificationRec2);                        
            
            Storefront_Notification__C notificationRec3 = CRMRetail_TestDataFactory_H_HUM.createNotification('Waiver Required',location1.Id,'Non-Member','Follow',0,False,resetDate,20.0,50.0);
            lstOfStfNotifications.add(notificationRec3);                        
            
            Storefront_Notification__C notificationRec4 = CRMRetail_TestDataFactory_H_HUM.createNotification('First Time Visitor',location1.Id,'Non-Member','Follow',23,True,resetDate,20.0,50.0);
            lstOfStfNotifications.add(notificationRec4);                        
            
            Storefront_Notification__C notificationRec5 = CRMRetail_TestDataFactory_H_HUM.createNotification('Veteran',location1.Id,'Non-Member','Follow',23,false,resetDate,20.0,50.0);
            lstOfStfNotifications.add(notificationRec5);                        
            
            Storefront_Notification__C notificationRec6 = CRMRetail_TestDataFactory_H_HUM.createNotification('Veteran',location1.Id,'Member','Follow',23,True,resetDate,20.0,50.0);
            lstOfStfNotifications.add(notificationRec6);                        
            
            Storefront_Notification__C notificationRec7 = CRMRetail_TestDataFactory_H_HUM.createNotification('New Member',location1.Id,'Non-Member','Follow',23,false,resetDate,20.0,50.0);
            lstOfStfNotifications.add(notificationRec7);                        
            
            Storefront_Notification__C notificationRec8 = CRMRetail_TestDataFactory_H_HUM.createNotification('New Member',location1.Id,'Member','Follow',23,False,resetDate,20.0,50.0);
            lstOfStfNotifications.add(notificationRec8);                        
            
            Storefront_Notification__C notificationRec9= CRMRetail_TestDataFactory_H_HUM.createNotification('Aging In',location1.Id,'Member','Follow',23,False,resetDate,20.0,50.0);
            lstOfStfNotifications.add(notificationRec9);                        
            
            Storefront_Notification__C notificationRec10= CRMRetail_TestDataFactory_H_HUM.createNotification('Aging In',location1.Id,'Non-Member','Follow',23,False,resetDate,20.0,50.0);
            lstOfStfNotifications.add(notificationRec10);                        
            
            Storefront_Notification__C notificationRec11= CRMRetail_TestDataFactory_H_HUM.createNotification('SDoH',location1.Id,'Member','Follow',0,False,resetDate,20.0,50.0);
            lstOfStfNotifications.add(notificationRec11);                        
            
            Storefront_Notification__C notificationRec12= CRMRetail_TestDataFactory_H_HUM.createNotification('SDoH',location1.Id,'Non-Member','Follow',0,False,resetDate,20.0,50.0);
            lstOfStfNotifications.add(notificationRec12);            
            insert lstOfStfNotifications;
            
            system.assertEquals(lstOfStfNotifications.get(0).Notification_Type__c, 'First Time Visitor');
            system.assertEquals(lstOfStfNotifications.get(1).Notification_Type__c, 'Waiver Required');
            system.assertEquals(lstOfStfNotifications.get(2).Notification_Type__c, 'Waiver Required');
            system.assertEquals(lstOfStfNotifications.get(3).Notification_Type__c, 'First Time Visitor');
            system.assertEquals(lstOfStfNotifications.get(4).Notification_Type__c, 'Veteran');
            system.assertEquals(lstOfStfNotifications.get(5).Notification_Type__c, 'Veteran');
            system.assertEquals(lstOfStfNotifications.get(6).Notification_Type__c, 'New Member');
            system.assertEquals(lstOfStfNotifications.get(7).Notification_Type__c, 'New Member');   
            system.assertEquals(lstOfStfNotifications.get(8).Notification_Type__c, 'Aging In');  
            system.assertEquals(lstOfStfNotifications.get(9).Notification_Type__c, 'Aging In');
            system.assertEquals(lstOfStfNotifications.get(10).Notification_Type__c, 'SDoH');     
            system.assertEquals(lstOfStfNotifications.get(11).Notification_Type__c, 'SDoH');

            List<CRMFunctionality_ONOFF_Switch__c> lstOfSwitches = new List<CRMFunctionality_ONOFF_Switch__c>();
            CRMFunctionality_ONOFF_Switch__c Switch_4012388   = new CRMFunctionality_ONOFF_Switch__c(Name = '4012388  ',IsON__c = true); 
            lstOfSwitches.add(Switch_4012388);
            insert lstOfSwitches;
            system.assertEquals(true, lstOfSwitches.get(0).IsON__c);
            
        }        
    }
   
    /**
    * Returns void
    * <p>
    * This method Is used for handeling new virtual visitor.
    *
    * @return  Void
    * @see
    */
    @isTest static void testhandleNewVirtualVisitor()
    {
        
        List<User> oCCSUser = [Select id, UserName from User where LastName = 'test User1' OR LastName = 'test User2' LIMIT 2 ] ;
        system.assertEquals(oCCSUser.size(),2);
        User UserWithAccess=oCCSUser[0];
        User UserWithoutAccess=oCCSUser[1];
        
        system.runAs(UserWithAccess)
        {
            Storefront_Location__c locobj = [Select Id, Name FROM Storefront_Location__c LIMIT 1];
            if(!Cache.Session.contains('currentInteractionDate')) 
            {
                Datetime myDateTime = system.Now();
                String formatted = myDateTime.formatGMT('yyyy-MM-dd');
                Cache.Session.Put('currentInteractionDate', formatted);
            }
            if(!Cache.Session.contains('currentLocation'))
            {
                Cache.Session.Put('currentLocation',locobj.Id +'-'+ locobj.Name);
            }
            Account oAcc1 = new Account();
            oAcc1.recordtypeid = Schema.SObjectType.Account.getRecordTypeInfosByName().get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_RETAIL_VISITOR).getRecordTypeId();
            oAcc1.Birthdate__c = '01/01/1984';
            oAcc1.firstName = 'Test';
            oAcc1.lastName = 'Account';
            oAcc1.MiddleName = 'J';
            oAcc1.PersonMailingState = 'New York';
            oAcc1.PersonMailingPostalCode = '56605';
            oAcc1.PersonHomePhone = '';            
            
            system.assertEquals(oAcc1.firstName, 'Test');
            
            Storefront_NotificationsWrapper_HUM notificationWrap = CRMRetail_VisitorInformation_LC_HUM.handleNewVirtualVisitor(oAcc1);
                        
            try
            {     
                CRMRetail_VisitorInformation_LC_HUM.handleNewVirtualVisitor(new account());
            }
            Catch(Exception e)
            {
                System.assertEquals('System.AuraHandledException', e.getTypeName());
            }
            
        }
        
        system.runAs(UserWithoutAccess)
        {
            Storefront_Location__c locobj = [Select Id, Name FROM Storefront_Location__c LIMIT 1];
            if(!Cache.Session.contains('currentInteractionDate')) 
            {
                Datetime myDateTime = system.Now();
                String formatted = myDateTime.formatGMT('yyyy-MM-dd');
                Cache.Session.Put('currentInteractionDate', formatted);
            }
            if(!Cache.Session.contains('currentLocation'))
            {
                Cache.Session.Put('currentLocation',locobj.Id +'-'+ locobj.Name);
            }
            Account oAcc1 = new Account();
            oAcc1.recordtypeid = Schema.SObjectType.Account.getRecordTypeInfosByName().get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_RETAIL_VISITOR).getRecordTypeId();
            oAcc1.Birthdate__c = '01/01/1984';
            oAcc1.firstName = 'Test';
            oAcc1.lastName = 'Account';
            oAcc1.MiddleName = 'J';
            oAcc1.PersonMailingState = 'New York';
            oAcc1.PersonMailingPostalCode = '56605';
            oAcc1.PersonHomePhone = '';            
            try
            {
                Storefront_NotificationsWrapper_HUM notificationWrap = CRMRetail_VisitorInformation_LC_HUM.handleNewVirtualVisitor(oAcc1);                
            }
            Catch(Exception e)
            {
                System.assertEquals('System.AuraHandledException', e.getTypeName());
            }     
        }
    }
    
    /**
    * Returns void
    * <p>
    * This method Is used for covering catch blocks
    *
    * @return  Void
    * @see
    */
    @isTest static void testCatchClauses()
    {
        User oCCSUser = [Select id, UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        
        User userWithoutAccess = [Select id, UserName from User where LastName = 'test User2' LIMIT 1 ] ;
        system.assertEquals(userWithoutAccess.UserName,'user2user2@humcrmstest.com');
               
        system.runAs(oCCSUser)
        {
            try
            {
                CRMRetail_VisitorInformation_LC_HUM.newVirtualVisitorInteraction(new Account());
            }
            Catch(Exception e)
            {
                System.assertEquals('System.AuraHandledException', e.getTypeName());
            }                                   
        }
        
        Account oAcc2 = [select Id,FirstName,Enterprise_ID__c,CRMRetailNotificationOptOut__c,Veteran_Status__c,Birthdate__c, LastName from Account LIMIT 1];
        system.runAs(userWithoutAccess)
        {
            Storefront_Location__C sfLoc = [SELECT Id,Name,Location_Type__c from Storefront_Location__C LIMIT 1];
            
            if(!Cache.Session.contains(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE)) 
            {
                Datetime myDateTime = system.Now();
                String formatted = myDateTime.formatGMT('yyyy-MM-dd');
                Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE, formatted);
                Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION, sfLoc.Id +'-'+ sfLoc.Name);   
            }
            try
            {
                CRMRetail_VisitorInformation_LC_HUM.newVirtualVisitorInteraction(oAcc2);
            }
            Catch(Exception e)
            {
                System.assertEquals('System.AuraHandledException', e.getTypeName());
            }                                   
        }
    }
    
    /**
    * Returns void
    * <p>
    * This method Is used for covering newVisitorInteractionForVirtualLocation blocks
    *
    * @return  Void
    * @see
    */
    @isTest static void testnewVisitorInteractionForVirtualLocation()
    {
        User oCCSUser = [Select id, UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        Storefront_Location__C sfLoc = [SELECT Id,Name,Location_Type__c from Storefront_Location__C LIMIT 1];        
        system.runAs(oCCSUser)
        {
            sfLoc.Location_Type__c = 'Virtual';
            update sfLoc;
            system.assertEquals(sfLoc.Location_Type__c, 'Virtual');
            Account oAcc1 = new Account();
            oAcc1.recordtypeid = Schema.SObjectType.Account.getRecordTypeInfosByName().get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_RETAIL_VISITOR).getRecordTypeId();
            oAcc1.Birthdate__c = '01/01/1984';
            oAcc1.firstName = 'Test';
            oAcc1.lastName = 'Account';
            oAcc1.MiddleName = 'J';
            oAcc1.PersonMailingState = 'New York';
            oAcc1.PersonMailingPostalCode = '56605';
            oAcc1.PersonHomePhone = '';
            if(!Cache.Session.contains('currentInteractionDate')) 
            {
                Datetime myDateTime = system.Now();
                String formatted = myDateTime.formatGMT('yyyy-MM-dd');
                Cache.Session.Put('currentInteractionDate', formatted);
            }
            if(!Cache.Session.contains('currentLocation'))
            {
                Cache.Session.Put('currentLocation',sfLoc.Id +'-'+ sfLoc.Name);
            }
            Storefront_NotificationsWrapper_HUM notificationWrap = CRMRetail_VisitorInformation_LC_HUM.handleNewVirtualVisitor(oAcc1);
            Account testAcc = [select Id, GCM_Visitor_Barcode__c from Account where Id =: notificationWrap.accountRec.Id];
            system.assertEquals(testAcc.GCM_Visitor_Barcode__c,'Virtual Visitor');                              
        }    
    }

    /**
    * Returns void
    * <p>
    * This method Is used for covering getVisitorInfoPageData blocks
    *
    * @return  Void
    * @see
    */
    @isTest static void testgetVisitorInfoPageData()
    {
        User oCCSUser = [Select id, UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        Storefront_Location__C sfLoc = [SELECT Id,Name,Location_Type__c from Storefront_Location__C LIMIT 1];
        system.runAs(oCCSUser)
        {
            Account oAcc2 = [select Id, LastName from Account LIMIT 1];
            CRMRetail_VisitorInformation_LC_HUM.VisitorPageInfo_Wrapper wrapper = CRMRetail_VisitorInformation_LC_HUM.getVisitorInfoPageData();
            system.assertNotEquals(wrapper,Null);
        }
    }
    
    /**
    * Returns void
    * <p>
    * This method Is used for covering getVisitorInfoPageData blocks
    *
    * @return  Void
    * @see
    */
    @isTest static void TestlogUIException()
    {
        User oCCSUser = [Select id, UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        Storefront_Location__C sfLoc = [SELECT Id,Name,Location_Type__c from Storefront_Location__C LIMIT 1];
        system.runAs(oCCSUser)
        {
            CRMRetail_VisitorInformation_LC_HUM.logUIException('UI Error');
        }
    }
}