/******************************************************************************************************************
Apex class Name    : CRM_Retail_Custom_Dashboard_T_HUM
Version            : 1.0
Function           : This class is used as test class for CRM_Retail_CustomDashboard_C_HUM
Created On         : Feb 21 ,2022

Modification Log: 
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0            Ekta Bist                                       21/02/2022          Original Version 3104178
*   1.1            Lakshmi Madduri                                 8/1/2022            Code coverage for Modernized home page
*   1.2            Mohamed Thameem                                 23/01/2023          REQ- 3866581 Modernizing Dashboard
*  	1.3			   Mohamed Thameem                                 07/07/2023          REQ- 4832304 First Time Visitor Report
------------------------------------------------------------------------------------------------------------------- */

@isTest
private class CRM_Retail_Custom_Dashboard_T_HUM {
    
    @testSetup static void setup()
    {           
        List<User> lstUserInsert = new List<User>();
        
        User oUser = HUMTestDataHelperTest.createTestUser('User2','CRMRetail User');
        lstUserInsert.add(oUser);  
        
        User oUser1 = HUMTestDataHelperTest.createTestUser('User3','CRMRetail User');
        lstUserInsert.add(oUser1); 

		insert lstUserInsert;
        System.assertEquals(lstUserInsert.size(), 2); 
        
        PermissionSet SuperAdmin = [SELECT Id,Name FROM PermissionSet WHERE Name =: GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_400SUPERADMIN];
        System.assertEquals(SuperAdmin.Name, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_400SUPERADMIN);
        
        List<PermissionSetAssignment> lstPermSetAssign = new List<PermissionSetAssignment>();

        PermissionSetAssignment supAdmin = new PermissionSetAssignment(AssigneeId = lstUserInsert[0].id, PermissionSetId = SuperAdmin.Id);
        lstPermSetAssign.add(supAdmin);
                
        PermissionSet servAssc = [SELECT Id, Name FROM PermissionSet WHERE Name =: GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_COORDINATOR];
        System.assertEquals(servAssc.Name, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_COORDINATOR);
        
        PermissionSetAssignment servAssct = new PermissionSetAssignment(AssigneeId = lstUserInsert[1].id, PermissionSetId = servAssc.Id);
        lstPermSetAssign.add(servAssct);
        
        insert lstPermSetAssign;
        System.assertEquals(lstPermSetAssign.size(), 2); 
        
        Storefront_Location__c location;

        System.runAs(oUser) 
        { 
        	List<RecordType> listRecordType = [SELECT Name, SobjectType,IsPersonType FROM RecordType WHERE SobjectType='Account' AND (Name =: GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_RETAIL_VISITOR OR Name =: GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER) ORDER BY Name ASC];
            System.assertEquals(listRecordType.size(),2);
            
            List<Account> lstAccount = new List<Account>();
            
            Account objMemberAcc = CRMRetail_TestDataFactory_H_HUM.createObjAccount(listRecordType[1].Id,'09/13/1990','Luke','Lee','99801','llee1@test.com','Juneau','Alaska',false);    
            objMemberAcc.ETL_Record_Deleted__c = false;
            lstAccount.add(objMemberAcc);
            
            Account objNonMemberAcc = CRMRetail_TestDataFactory_H_HUM.createObjAccount(listRecordType[0].Id,'09/13/1990','Adam','Lee','99801','llee1@test.com','Juneau','Alaska',false);    
            objNonMemberAcc.ETL_Record_Deleted__c = false;
            lstAccount.add(objNonMemberAcc);
            
            insert lstAccount;
            System.assertEquals(lstAccount.size(),2);
            
            Storefront_I_Type__c objIntType = CRMRetail_TestDataFactory_H_HUM.createInteractionType('Consumer Edu');
            insert objIntType;
            System.assertEquals(objIntType.Name,'Consumer Edu');
            
            Storefront_I_Reason__c objIntReason = CRMRetail_TestDataFactory_H_HUM.createInteractionReason('Visitor Check-In (1st Time)',null);
            objIntReason.Interaction_Type__c = objIntType.Id;
            insert objIntReason; 
            System.assertEquals(objIntReason.Name,'Visitor Check-In (1st Time)');
            
            location = CRMRetail_TestDataFactory_H_HUM.createLocation();
            location.Name = 'Tulsa';
            location.Inactive_indicator__c = false;
            insert location;
            System.assertEquals(location.Name,'Tulsa');
            
            Date activityDate = system.today();
            
            List<Storefront_Interaction__c> lstInteraction = new List<Storefront_Interaction__c>();
            
            Storefront_Interaction__c objInt = CRMRetail_TestDataFactory_H_HUM.createInteraction(lstAccount[0].Id, location.Id, objIntReason.Id, false, 'Member', activityDate);
            objInt.Visitor_ID__c = '123';
            objInt.Category__c = '';
            lstInteraction.add(objInt);
            
            Storefront_Interaction__c objInter = CRMRetail_TestDataFactory_H_HUM.createInteraction(lstAccount[1].Id, location.Id, objIntReason.Id, false, 'Non-Member', activityDate);
            objInter.Visitor_ID__c = '123';
            objInter.Category__c = '';
            lstInteraction.add(objInter);
            
            Storefront_Interaction__c objInteract = CRMRetail_TestDataFactory_H_HUM.createInteraction(lstAccount[0].Id, location.Id, objIntReason.Id, false, 'Member', activityDate);
            objInteract.Visitor_ID__c = '12345';
            objInteract.Category__c = '';
            lstInteraction.add(objInteract);
            
            Storefront_Interaction__c objInteraction = CRMRetail_TestDataFactory_H_HUM.createInteraction(lstAccount[1].Id, location.Id, objIntReason.Id, false, 'Non-Member', activityDate);
            objInteraction.Visitor_ID__c = '12345';
            objInteraction.Category__c = '';
            lstInteraction.add(objInteraction);
            
            insert lstInteraction;            
            System.assertEquals(lstInteraction.size(),4);
            
            List<CRMFunctionality_ONOFF_Switch__c> switchList = new List<CRMFunctionality_ONOFF_Switch__c>();
            CRMFunctionality_ONOFF_Switch__c csCRMOnOffSwitch = new  CRMFunctionality_ONOFF_Switch__c(Name = '3104178', IsON__c = True);
            switchList.add(csCRMOnOffSwitch);

            CRMFunctionality_ONOFF_Switch__c reportSwitch = new CRMFunctionality_ONOFF_Switch__c(Name = '4832304',IsON__c=true);
            switchList.add(reportSwitch);
            
            insert switchList;
            
            assert.areEqual(switchList[0].IsON__c,true);
            assert.areEqual(switchList[1].IsON__c,true);
            
        }
        
		User obUser = [Select Id,Name from user where id =: UserInfo.getUserID()]; 
        System.assertEquals(obUser.Id,UserInfo.getUserID());
        
        System.runAs(obUser){
            Storefront_Preference__c objPreference = new Storefront_Preference__c(User__c = obUser.Id, Default_Location__c = location.Id);
            insert objPreference;
            System.assertNotEquals(objPreference,Null);
        }        
    }
    
       
    @isTest static void testCustomDashboardController()
    {    
        
        User oUsr = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUsr.Name,'test User2');
        
        system.runAs(oUsr){
            Test.StartTest(); 
            CRM_Retail_Custom_Dashboard_C_HUM ob = new CRM_Retail_Custom_Dashboard_C_HUM();
            ob.callActionMethod();
            Test.StopTest(); 
        }
    }
        
    @isTest static void testCustomDashboardCntrl()
    {        
        User oUser = [select Id,Name from User where name = 'test User3' limit 1]; 
        system.assertEquals(oUser.Name,'test User3');
        
        system.runAs(oUser){
            Test.StartTest(); 
            CRM_Retail_Custom_Dashboard_C_HUM ob = new CRM_Retail_Custom_Dashboard_C_HUM();
            ob.callActionMethod();
            Test.StopTest(); 
        }
        
    }
    
    @isTest static void testgetLocations()
    {
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser){
            Test.StartTest(); 
            CRM_Retail_Custom_Dashboard_C_HUM ob = new CRM_Retail_Custom_Dashboard_C_HUM();
            List<SelectOption> obSelectOption = new List<SelectOption>();
            obSelectOption = ob.getLocations();           
            Test.StopTest(); 
        }
    }
    
    @isTest static void testfetchReportIds()
    {
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser){
            Test.StartTest(); 
            CRM_Retail_Custom_Dashboard_C_HUM ob = new CRM_Retail_Custom_Dashboard_C_HUM();
            ob.fetchReportIds(true);
            ob.fetchReportIds(false);
            Test.StopTest(); 
        }
    }
    
    @isTest static void testfetchVisitorsAndTasks()
    {
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser){
            Test.StartTest();
            String startDate = String.valueOf(system.today());
            String endDate = String.valueOf(system.today().adddays(2));
        	String str = CRM_Retail_Custom_Dashboard_C_HUM.fetchVisitorsAndTasks('Tulsa','true',startDate,endDate);
            System.assertNotEquals(str, '');
        	String str1 = CRM_Retail_Custom_Dashboard_C_HUM.fetchVisitorsAndTasks('Tulsa','false',startDate,endDate);
            System.assertNotEquals(str1, '');
            String returnValue = CRM_Retail_Custom_Dashboard_C_HUM.fetchVisitorsAndTasks('Tulsa','false','invalidDate','invalidDate');
            System.assertEquals(returnValue, null);
            Test.StopTest();
        }
    }
    
    @isTest static void testsetReportFilter()
    {
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser){
            Test.StartTest();
            CRM_Retail_Custom_Dashboard_C_HUM ob = new CRM_Retail_Custom_Dashboard_C_HUM();
            
            ob.sLocation = 'Tulsa';
            CRM_Retail_Custom_Dashboard_C_HUM.bDisableLoc = false;
            ob.setReportFilter();
            system.assertNotEquals(CRM_Retail_Custom_Dashboard_C_HUM.sFilterForHealthEducator,'');
            
            CRM_Retail_Custom_Dashboard_C_HUM.bDisableLoc = true;
            ob.setReportFilter();
            system.assertNotEquals(CRM_Retail_Custom_Dashboard_C_HUM.sFilter,'');
            
            ob.sLocation = 'Undefined';
            ob.setReportFilter();
            system.assertEquals(CRM_Retail_Custom_Dashboard_C_HUM.sFilter,'');
            
            Test.StopTest();
        }
    }
     @isTest static void testNewHomePageDashboard()
    {
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser){
            Test.StartTest();
            Storefront_Location__c objLoc =[Select Id,Name from Storefront_Location__c WHERE Name = 'Tulsa'];
            Cache.Session.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION, objLoc.Id+'-'+objLoc.Name);
            PageReference pgRef = Page.CRM_Retail_Custom_Dashboard_VF_HOME_HUM;
            Test.setCurrentPage(pgRef);
        	CRM_Retail_Custom_Dashboard_C_HUM objCls = new CRM_Retail_Custom_Dashboard_C_HUM();
            objCls.callActionMethod();           
            Test.StopTest();
        }
    }
    @isTest static void testNewHomePageDashboardScenario2()
    {
        User oUser = [select Id,Name from User where name = 'test User3' limit 1]; 
        system.assertEquals(oUser.Name,'test User3');
        
        system.runAs(oUser){
            Test.StartTest();
            Storefront_Location__c objLoc =[Select Id,Name from Storefront_Location__c WHERE Name = 'Tulsa'];
            Cache.Session.remove(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION);
        	CRM_Retail_Custom_Dashboard_C_HUM objCls = new CRM_Retail_Custom_Dashboard_C_HUM();
            objCls.callActionMethod();           
            Test.StopTest();
        }
    }
}