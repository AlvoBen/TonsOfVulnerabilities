/****************************************************************************************
Apex Class Name :  GenericHelper_CRMRetail_T_HUM
Version: 1.0 
Created Date     : 06/27/2019
Function         : Test class to GenericHelper_CRMRetail_H_HUM Apex class
Modification Log : 
*    Developer               Code Review            Date                 Description 
* ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
*  Chetan S                                     08/23/2019            REQ - 401378 Original Version
*  Ekta Bist                                    12/14/2021            User Story - 2956323 Task Import BreakFix
*  Vivek Sharma                                 02/18/2022            User Story 2940968: T1PRJ0154546 MF9 Storefront Home Page: Missing 'Event' Interactions
*  Vinoth L                                     3/18/2022             User Story 2792978 -T1PRJ0154546 / SF / MF9 Storefront - Search/Add Visitors: Inactive Accounts
*  Vinoth L                                     09/14/2022            User Story 3791721 - 09/23 Observations Fix
*  Vinoth L                      				10/11/2022            User Story 3759560: T1PRJ0154546 / SF / MF4 Storefront: Add Account Information on Tasks
* Srinivasan								   02/03/2024            T1PRJ1371022 : MF: 5571284 / SF / Storefront - CxOne - HIGH/Medium- GenericHelper_CRMRetail_H_HUM VIT37935980 VIT37935988 VIT37935989
****************************************************************************************************************************************************************************/
@isTest
private class GenericHelper_CRMRetail_T_HUM {
   
    /*
    * This method creates test data for test class
    * @param None
    * @return None
    */
    @testSetup static void setUpTestData() 
    {
        User oCCSUser12 = HUMTestDataHelperTest.createTestUser('User12','CRMRetail User');
        insert oCCSUser12;
        
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','System Administrator');
        insert oCCSUser;
        
        User oCCSUser400 = HUMTestDataHelperTest.createTestUser('User400','CRMRetail User');
        insert oCCSUser400;
        
        
        system.assertEquals(oCCSUser.LastName,'test User1'); 
        
        HUM_Webservice_Callout__c oHUMClaimSummaryService = new HUM_Webservice_Callout__c();
        oHUMClaimSummaryService.name = 'HUMMemberWebActivityDetailsService';
        oHUMClaimSummaryService.Active__c = true;
        oHUMClaimSummaryService.Certificate_Name__c = 'testts';
        oHUMClaimSummaryService.End_Point_URL__c = 'https://testst-cmservicews.hum.com/';
        oHUMClaimSummaryService.Timeout__c = '120000';
        oHUMClaimSummaryService.Content_Type__c = 'application/json';
        oHUMClaimSummaryService.Request_Type__c = 'POST';
        insert oHUMClaimSummaryService;
        system.assert(oHUMClaimSummaryService.id != null);
        system.assert(oHUMClaimSummaryService.name == 'HUMMemberWebActivityDetailsService');
        
        List<CRMFunctionality_ONOFF_Switch__c> lstSwitches = new List<CRMFunctionality_ONOFF_Switch__c>();
        CRMFunctionality_ONOFF_Switch__c Switch_1209425 = new CRMFunctionality_ONOFF_Switch__c(Name = '1209425', IsON__c = true);
        lstSwitches.add(Switch_1209425) ;
        
        CRMFunctionality_ONOFF_Switch__c switch_931799 = new CRMFunctionality_ONOFF_Switch__c(Name = '931799', IsON__c = true);
        lstSwitches.add(switch_931799) ;
        
        CRMFunctionality_ONOFF_Switch__c Switch_1396838 = new CRMFunctionality_ONOFF_Switch__c(Name = '1396838', IsON__c = true);
        lstSwitches.add(Switch_1396838) ;
        
        CRMFunctionality_ONOFF_Switch__c switch_1243111 = new CRMFunctionality_ONOFF_Switch__c(Name = '1243111',IsON__c=true);
        lstSwitches.add(switch_1243111) ;
        
        CRMFunctionality_ONOFF_Switch__c switch_3573183 = new CRMFunctionality_ONOFF_Switch__c(Name = '3573183',IsON__c=true);
        lstSwitches.add(switch_3573183) ;
        
        insert lstSwitches;
        system.assertEquals(lstSwitches.size(),5);        
                
        System.runAs (oCCSUser) 
        {  
            
            PermissionSet ps = [SELECT Id  FROM PermissionSet WHERE Name = 'CRMRetail_400_Super_Admin'];
            insert new PermissionSetAssignment(AssigneeId = oCCSUser400.Id, PermissionSetId = ps.Id);
            
            RecordType oMemRecType = [SELECT Id FROM RecordType WHERE Name = 'Member' Limit 1];
            system.assertNotEquals(oMemRecType,null);
            
            RecordType oVistorRecType = [SELECT Id FROM RecordType WHERE Name = 'CRMRetail Visitor' Limit 1];
            system.assertNotEquals(oVistorRecType,null);
            
            List<Account> lstAccount = new List<Account>();
            Account oAcc = CRMRetail_TestDataFactory_H_HUM.createObjAccount(oMemRecType.Id, '01/01/1984', 'Daphiner', 'Superman', '56605', '', '', 'New York', false ); //HUMTestDataHelperTest.getAccount();
            oAcc.Group_Number__c = '987654321';
            oAcc.PersonBirthdate = Date.newInstance(1984, 01, 01);
            oAcc.MiddleName = 'J';
            oAcc.Enterprise_ID__c='1017246970';
            oAcc.Home_Office_Account__c = false;
            oAcc.Waiver_Date__c = system.today().addMonths(-40);
            lstAccount.add(oAcc);
            
            Account oAccNonMem = CRMRetail_TestDataFactory_H_HUM.createObjAccount(oVistorRecType.Id, '01/01/1984', 'Nonmember', 'Visitor', '56605', '', '', 'New York', false);
            oAccNonMem.Group_Number__c = '987654320';
            oAccNonMem.PersonBirthdate = Date.newInstance(1984, 01, 01);
            oAccNonMem.MiddleName = 'J';
            oAccNonMem.Enterprise_ID__c='1017246970';
            oAccNonMem.Home_Office_Account__c = false;
            oAccNonMem.GCM_Visitor_Barcode__c = 'test11234';
            oAccNonMem.Waiver_Date__c =  system.today().addMonths(-24);
            lstAccount.add(oAccNonMem);
            
            Account oAcc1 = CRMRetail_TestDataFactory_H_HUM.createObjAccount(oMemRecType.Id , '01/01/1984', 'Test', 'Account', '56605', '', '', 'New York', false);
            oAcc1.MiddleName = 'J';
            oAcc1.Enterprise_ID__c='1030693160';
            oAcc1.Home_Office_Account__c = false;
            oAcc1.GCM_Visitor_Barcode__c = 'test111234';
            lstAccount.add(oAcc1);
            
            
            Account oAccNonMember = CRMRetail_TestDataFactory_H_HUM.createObjAccount(oVistorRecType.Id, '01/01/1984', 'UNKNOWN', 'Visitor', '56605', '', '', 'New York', false);
            oAccNonMember.Group_Number__c = '987654320';
            oAccNonMember.PersonBirthdate = Date.newInstance(1984, 01, 01);
            oAccNonMember.MiddleName = 'J';
            oAccNonMember.Enterprise_ID__c='1017246970';
            oAccNonMember.Home_Office_Account__c = false;
            oAccNonMember.GCM_Visitor_Barcode__c = 'test11234';
            oAccNonMember.Waiver_Date__c =  system.today().addMonths(-24);
            lstAccount.add(oAccNonMember);
            
            Account oTaskAcc = CRMRetail_TestDataFactory_H_HUM.createObjAccount(oMemRecType.Id , '01/01/1998', 'Alex', 'Stewart', '56605', '', '', 'New York', false);
            oTaskAcc.MiddleName = 'M';
            oTaskAcc.Enterprise_ID__c='1030693189';
            oTaskAcc.Home_Office_Account__c = false;
            oTaskAcc.Deceased_Date__c = Date.today().format();
            lstAccount.add(oTaskAcc);
            
            insert lstAccount;
            System.AssertEquals(lstAccount.size(), 5);
            
            Policy__c oPolicy1 = CRMRetail_TestDataFactory_H_HUM.createPolicy(lstAccount[2].Id,'11111');
            System.AssertEquals(oPolicy1.Policy_Group_Number__c, '11111');
            
            Policy__c oPolicy2 = CRMRetail_TestDataFactory_H_HUM.createPolicy(lstAccount[0].Id,'11111');
            System.AssertEquals(oPolicy2.Policy_Group_Number__c, '11111');
            
            Policy__c oPolicy3 = CRMRetail_TestDataFactory_H_HUM.createPolicy(lstAccount[4].Id,'22222');
            System.AssertEquals(oPolicy3.Policy_Group_Number__c, '22222');
            
            Policy_Member__c oPolicyMember1 = CRMRetail_TestDataFactory_H_HUM.createPolicyMember(lstAccount[2].ID, oPolicy1.ID, '666', '09/01/2018', '09/01/2099', false);
            System.AssertEquals(oPolicyMember1.Name, '666');
            
            Policy_Member__c oPolicyMember2 = CRMRetail_TestDataFactory_H_HUM.createPolicyMember(lstAccount[0].ID, oPolicy2.ID, '666', '09/01/2018', '09/01/2099', false);
            System.AssertEquals(oPolicyMember2.Name, '666');
            
            List<Policy_Member__c> lstPlcyMmbers = new List<Policy_Member__c>();
            String effDate = (Date.today()-10).format();
            String inactiveEndDate = (Date.today()-5).format();
            String activeEndDate = (Date.today()+5).format();
            Policy_Member__c oPmRef3 = CRMRetail_TestDataFactory_H_HUM.createPolicyMemberInstance(lstAccount[4].Id,oPolicy3.Id,'18920192000','01/18/2022',activeEndDate,false);
            Policy_Member__c oPmRef4 = CRMRetail_TestDataFactory_H_HUM.createPolicyMemberInstance(lstAccount[4].Id,oPolicy2.Id,'H6719281000','01/18/2022',inactiveEndDate,false);
            Policy_Member__c oPmRef5 = CRMRetail_TestDataFactory_H_HUM.createPolicyMemberInstance(lstAccount[4].Id,oPolicy1.Id,'H7180291800','01/18/2022',activeEndDate,false);
            lstPlcyMmbers.add(oPmRef3);
            lstPlcyMmbers.add(oPmRef4);
            lstPlcyMmbers.add(oPmRef5);
            insert lstPlcyMmbers;
            
            lstPlcyMmbers = [SELECT Id,Name,Member__C from Policy_Member__c where Member__C=:lstAccount[4].Id];
            system.assertEquals(lstAccount[4].Id,lstPlcyMmbers[0].Member__c);
            
            Member_ID__c oMember = CRMRetail_TestDataFactory_H_HUM.createMemberId(oPolicyMember1.ID, '1234456', '01' , oPolicy1.ID , 'M12344' , '1234456');
            oMember.Source_Application_Code__c = 'LV';
            update oMember;
            System.AssertEquals(oMember.Source_Application_Code__c, 'LV');
            
            Member_ID__c oMember1 = CRMRetail_TestDataFactory_H_HUM.createMemberId(oPolicyMember2.ID, '123445', '02', oPolicy2.ID , 'M12345', '123445');
            oMember1.Source_Application_Code__c = 'L';
            update oMember1;

            Storefront_Location__c location1 = CRMRetail_TestDataFactory_H_HUM.createLocation();
            location1.Maximum_Occupancy__c = 4;
            insert location1;
            system.assertEquals(location1.Name, 'Virginia');
            
            Storefront_I_Type__c InteractionType1 = CRMRetail_TestDataFactory_H_HUM.createInteractionType('Social');
            insert InteractionType1;
            system.assertEquals(InteractionType1.Name, 'Social');
            
            List<Storefront_I_Reason__c> lstReason = new List<Storefront_I_Reason__c>();
            Storefront_I_Reason__c InteractionReason1 = CRMRetail_TestDataFactory_H_HUM.createInteractionReason('Social-Cafe',null);
            InteractionReason1.Interaction_Type__c = InteractionType1.Id;
            lstReason.add(InteractionReason1);
            
            Storefront_I_reason__c InteractionReason2 = CRMRetail_TestDataFactory_H_HUM.createInteractionReason('Visitor Check-In (1st Time)',null);
            InteractionReason2.Interaction_Type__c = InteractionType1.Id;
            lstReason.add(InteractionReason2);
    
            insert lstReason;
            system.assertEquals(lstReason.size(), 2);
            
            List<Storefront_Interaction__c> lstInteraction = new List<Storefront_Interaction__c>();
            Storefront_Interaction__c GcmInteractions1 = CRMRetail_TestDataFactory_H_HUM.createInteraction(lstAccount[0].Id, location1.Id, lstReason[0].Id, false, '', system.today());
            GcmInteractions1.Storefront_Event_Starttime__c = system.now();
            GcmInteractions1.Category__c = 'Virtual';
            lstInteraction.add(GcmInteractions1);
            
            Storefront_Interaction__c GcmInteractions2 = CRMRetail_TestDataFactory_H_HUM.createInteraction(lstAccount[2].Id, location1.Id, lstReason[0].Id, false, '', system.today());
            GcmInteractions2.Storefront_Event_Starttime__c = system.now();
            lstInteraction.add(GcmInteractions2);
            
            Storefront_Interaction__c GcmInteractions3 = CRMRetail_TestDataFactory_H_HUM.createInteraction(lstAccount[2].Id, location1.Id, lstReason[1].Id, false, '', system.today());
            GcmInteractions3.Storefront_Event_Starttime__c = system.now();
            lstInteraction.add(GcmInteractions3);

            insert lstInteraction;
            system.assertEquals(lstInteraction.size(), 3);
            
            HUM_Webservice_Callout__c crmretailServiceConfigObj = new HUM_Webservice_Callout__c();
            crmretailServiceConfigObj.Name = 'AORDetailsFetchService';
            crmretailServiceConfigObj.Certificate_Name__c = 'Humana_GuidanceCare_Alderaan';
            crmretailServiceConfigObj.Active__c = true;
            crmretailServiceConfigObj.End_Point_URL__c ='https://dev-crmservicews.humana.com/ProducerCompensationRelationshipsREST';
            crmretailServiceConfigObj.Service_Name__c = '/producerBookOfBusinessRequest';
            crmretailServiceConfigObj.Timeout__c = '12000';
            crmretailServiceConfigObj.Request_Type__c = 'GET';
            crmretailServiceConfigObj.Content_Type__c = 'application/json'; 
            insert crmretailServiceConfigObj;
            
            Id crmRecordTypeId = Schema.SObjectType.Task.getRecordTypeInfosByName().get('CRMRetail Task').getRecordTypeId();
            system.assertNotEquals(crmRecordTypeId,null);
            
            List<Task> taskList = new List<Task>();
            Task objTask = new Task(Priority = 'Low', Status = 'Not Started', CRMRetail_Automated__c = true, Recordtypeid = crmRecordTypeId, WhatId = lstAccount[0].Id, crm_retail_location__c = location1.Id, OwnerId = oCCSUser.Id, Subject = 'consumer test1', ActivityDate = System.today().addDays(2));
            taskList.add(objTask);
            
            Task objTask1 = new Task(Priority = 'Low', Status = 'Not Started', CRMRetail_Automated__c = true, Recordtypeid = crmRecordTypeId, WhatId = lstAccount[2].Id, crm_retail_location__c = location1.Id, OwnerId = oCCSUser.Id, Subject = 'consumer test2', ActivityDate = System.today().addDays(12));
            taskList.add(objTask1);
            
            Task objTask2 = new Task(Priority = 'Low', Status = 'Not Started', CRMRetail_Automated__c = true, Recordtypeid = crmRecordTypeId, WhatId = lstAccount[0].Id, crm_retail_location__c = location1.Id, OwnerId = oCCSUser.Id, Subject = 'consumer test1', ActivityDate = System.today().addDays(2));
            taskList.add(objTask2);
            
            Task objTask3 = new Task(Priority = 'Low', Status = 'Not Started', CRMRetail_Automated__c = true, Recordtypeid = crmRecordTypeId, WhatId = lstAccount[4].Id, crm_retail_location__c = location1.Id, OwnerId = oCCSUser.Id, Subject = 'consumer test1', ActivityDate = System.today().addDays(2));
            taskList.add(objTask3);
            
            Insert taskList;
            system.assertEquals(objTask.Priority, 'Low');
            system.assertEquals(objTask1.Priority, 'Low');
            system.assertEquals(objTask2.Priority, 'Low');
            system.assertEquals(objTask3.Priority, 'Low');
            
            Storefront_Preference__c DefLocation1 = new Storefront_Preference__c(Default_Location__c = location1.Id, User__c = oCCSUser.id);
            insert DefLocation1;
            system.assertEquals(DefLocation1.User__c, oCCSUser.id);
        }
        System.runAs (oCCSUser400) 
        {  
            List<Storefront_I_Reason__c> lstToProcessDMLs = new List<Storefront_I_Reason__c>();
        	Storefront_I_Reason__C sfRsn = [select id,name,inactive__c from Storefront_I_Reason__C where name ='Social-Cafe'];
            sfRsn.inactive__c = true;
            lstToProcessDMLs.add(sfRsn);
            
            List<Attendance__c> attListtoUpdate = new List<Attendance__c>();
            List<Attendance__c> attendanceList = [SELECT Id, Location__c, Date__c, Visitors_Today__c, First_Time_Visitors_Today__c, Current_Attendance__c FROM Attendance__c limit 1];
    
            list<Storefront_Interaction__c>  updateInteraction = [SELECT Id,Category__c from Storefront_Interaction__c limit 1];
            updateInteraction[0].Category__c = 'Virtual';
            
            list<Storefront_Interaction__c> recInteraction = new list<Storefront_Interaction__c>();
            for(integer i=0; i < 2; i++){
                Storefront_Interaction__c objInt = new Storefront_Interaction__c();
                objInt.Category__c = 'Virtual';
                recInteraction.add(objInt);
            }
            
            GenericHelper_CRMRetail_H_HUM.checkAccessForDML(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_UPSERT_EVENT_TYPE, updateInteraction, 'Storefront_Interaction_H_HUM' , 'processAttendanceBeforeDelete');
            GenericHelper_CRMRetail_H_HUM.checkAccessForDML(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_UPDATE_EVENT_TYPE, updateInteraction, 'Storefront_Interaction_H_HUM' , 'processAttendanceBeforeDelete');   //DK Cxone  
            GenericHelper_CRMRetail_H_HUM.checkAccessForDML(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DELETE_EVENT_TYPE, updateInteraction, 'Storefront_Interaction_H_HUM' , 'processAttendanceBeforeDelete');
            
        }
        system.runAs(oCCSUser400)
        {   
            Test.StartTest();
            Boolean bHasAccess = UtilityHelper_H_AHT_HUM.checkCustomPermissionAccess('CRM_Retail_Virtual_Location_Access');          
            Test.stopTest();
            System.assert(bHasAccess,'CRM_Retail_Virtual_Location_Access');
        }
        system.runAs(oCCSUser400)
        { 
            RecordType oMemRecType = [SELECT Id FROM RecordType WHERE Name = 'Member' Limit 1];
            List<Account> lstAccount = new List<Account>();
            Account oAcc = CRMRetail_TestDataFactory_H_HUM.createObjAccount(oMemRecType.Id, '01/01/1984', 'Daphiner', 'Superman', '56605', '', '', 'New York', false ); //HUMTestDataHelperTest.getAccount();
            oAcc.Group_Number__c = '987654321';
            oAcc.PersonBirthdate = Date.newInstance(1984, 01, 01);
            oAcc.MiddleName = 'J';
            oAcc.Enterprise_ID__c='1017246970';
            oAcc.Home_Office_Account__c = false;
            oAcc.Waiver_Date__c = system.today().addMonths(-40);
            
            upsert oAcc;
            
            Policy__c oPolicy30 = CRMRetail_TestDataFactory_H_HUM.createPolicy(oAcc.Id,'22122');
            System.AssertEquals(oPolicy30.Policy_Group_Number__c, '22122');
            
            Policy_Member__c oPolicyMember10 = CRMRetail_TestDataFactory_H_HUM.createPolicyMember(oAcc.ID, oPolicy30.ID, '666', '09/01/2018', '09/01/2099', false);
            System.AssertEquals(oPolicyMember10.Name, '666');
            account acc = [SELECT Id,Enterprise_ID__c, Name,FirstName,LastName, Waiver_Date__C,Birthdate__c,GCM_Visitor_Barcode__c,CRMRetailNotificationOptOut__c,(SELECT Id,Name,Effective_Date__c,status__c,End_Date__c,Member__C,Product__c,Policy__r.Platform__c,Policy__r.Product__r.Major_LOB__c,Policy__r.Source_Cust_Cov_Key__c,Policy__r.Product__r.ProductCode,Member__r.Enterprise_ID__c,Policy__r.Product_Type__c FROM Policy_Members__r WHERE Member__r.ETL_Record_Deleted__c = false ),Veteran_Status__c,RecordType.Name FROM Account Where Id =: oAcc.Id  LIMIT 1];
            account acc1 = [SELECT Id,Enterprise_ID__c, Name,FirstName,LastName, Waiver_Date__C,Birthdate__c,GCM_Visitor_Barcode__c,CRMRetailNotificationOptOut__c,Veteran_Status__c,RecordType.Name FROM Account Where Id =: oAcc.Id LIMIT 1];
            
            if(acc != null){
            GenericHelper_CRMRetail_H_HUM.formConsolidatedServiceRequest(new List<Account>{acc},true);
                GenericHelper_CRMRetail_H_HUM.formConsolidatedServiceRequest(new List<Account>{acc},false);
                GenericHelper_CRMRetail_H_HUM.formConsolidatedServiceRequest(new List<Account>{acc1},true);
                GenericHelper_CRMRetail_H_HUM.formConsolidatedServiceRequest(new List<Account>{acc1},false);
                
                list<CampaignMember> objAcc = [select Id from CampaignMember limit 1];
               
                
                GenericHelper_CRMRetail_H_HUM.securityCheckFLS(AccessType.UPDATABLE, objAcc);
             
                
                GenericHelper_CRMRetail_H_HUM.checkCustomPermissionAccess('hELLO');
                GenericHelper_CRMRetail_H_HUM.vldtSfntUsr(oCCSUser400.iD);
                GenericHelper_CRMRetail_H_HUM.rtnStfUsrs();
                
                
                    
            }
            
            string dummyJSON ='{"faultResponse":{"Fault":{"faultstring":"InvalidApiKey","detail":{"errorInfo":{"error_message":"connectionissueweekendruined"}}}}}';
            string ERROR_INTEGRATION = HUMConstants__c.getInstance('ERROR_INTEGRATION') != null ? HUMConstants__c.getInstance('ERROR_INTEGRATION').StringValue__c : '';
            CRMRetail_ConsolidatedResponse_DTO_HUM.SDoHError objSDOHError = (CRMRetail_ConsolidatedResponse_DTO_HUM.SDoHError)System.JSON.deserialize(dummyJSON, CRMRetail_ConsolidatedResponse_DTO_HUM.SDoHError.class);
            String sSDOHError = JSON.serialize('{Dummy Error log}');
            GenericHelper_CRMRetail_H_HUM.logFaultError(dummyJSON,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CLASSNAME, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_PARSESERVICERESPONSE, '', '', GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_ERROR);
            GenericHelper_CRMRetail_H_HUM.createErrorLog(String.valueOf(objSDOHError.status),GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CLASSNAME,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_PARSESERVICERESPONSE,objSDOHError.message,ERROR_INTEGRATION);
            GenericHelper_CRMRetail_H_HUM.FLSUpdateCheck('Attendance__c',new List<String>{'Current_Attendance__c','Maximum_Concurrent_Visitors__c','Peak_Time__c'});
            GenericHelper_CRMRetail_H_HUM.curdFLSUCreateUpdateCheck('Storefront_Preference__c',(new List<String>{'Default_Location__c'}),'UPDATE');
            GenericHelper_CRMRetail_H_HUM.curdFLSUCreateUpdateCheck('Storefront_Preference__c',(new List<String>{'Default_Location__c'}),'NOUPDATE');
            
            map<string,object> mpOfVstrIndicators = new map<string,object>();
            mpOfVstrIndicators.putAll((Map<String,Object>)JSON.deserializeUntyped(dummyJSON)); 
            mpOfVstrIndicators.put('First Time Visitor This Year',true);
            GenericHelper_CRMRetail_H_HUM.serializeJSONStructure(mpOfVstrIndicators,'Hi');
            
           
        }
   
        
        system.runAs(oCCSUser12)
        {     
            GenericHelper_CRMRetail_H_HUM.FLSUpdateCheck('Attendance__c',new List<String>{'Current_Attendance__c','Maximum_Concurrent_Visitors__c','Peak_Time__c'});
            list<Storefront_I_Reason__c> objAcc = [select Inactive__c,isGo365Eligible__c from Storefront_I_Reason__c limit 1];
            GenericHelper_CRMRetail_H_HUM.securityCheckFLS(AccessType.UPDATABLE, objAcc);
        }
    }
    
    static testMethod void testMethodForCheckAccessForDML()
    {
        User oCCSUser = [Select id,UserName from User where LastName = 'test User400' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user400user400@humcrmstest.com');
        
        system.runAs(oCCSUser)
        {
            list<CampaignMember> objAcc = [select Id,HealthCloudGA__isVisibleOnPatientCard__c from CampaignMember limit 3];
            
           GenericHelper_CRMRetail_H_HUM.securityCheckFLS(AccessType.CREATABLE, objAcc);
           GenericHelper_CRMRetail_H_HUM.securityCheckFLS(AccessType.READABLE, objAcc);
           GenericHelper_CRMRetail_H_HUM.securityCheckFLS(AccessType.UPSERTABLE, objAcc);
            
            GenericHelper_CRMRetail_H_HUM.securityCheckFLS(AccessType.UPDATABLE, null);
                GenericHelper_CRMRetail_H_HUM.securityCheckFLS(AccessType.CREATABLE, null);
                GenericHelper_CRMRetail_H_HUM.securityCheckFLS(AccessType.READABLE, null);
                GenericHelper_CRMRetail_H_HUM.securityCheckFLS(AccessType.UPSERTABLE, null);
            
            RecordType oMemRecType = [SELECT Id FROM RecordType WHERE Name = 'Member' Limit 1];
            List<Account> lstAccount = new List<Account>();
            Account oAcc = CRMRetail_TestDataFactory_H_HUM.createObjAccount(oMemRecType.Id, '01/01/1984', 'Daphiner', 'Superman', '56605', '', '', 'New York', false ); //HUMTestDataHelperTest.getAccount();
            oAcc.Group_Number__c = '987654321';
            oAcc.PersonBirthdate = Date.newInstance(1984, 01, 01);
            oAcc.MiddleName = 'J';
            oAcc.Enterprise_ID__c='1017246970';
            oAcc.Home_Office_Account__c = false;
            oAcc.Waiver_Date__c = system.today().addMonths(-40);
            lstAccount.add(oAcc);
            
            string dummyJSON = '{    "timestamp": "2024-03-02T06:00:07.253+00:00",    "status": 404,    "error": "Not Found",    "message": "Engagement Index info not found for srdPersonId: 12",    "path": "/lhr/members/12/engagementIndex"}';
            map<string,object> mpOfVstrIndicators = new map<string,object>();
            mpOfVstrIndicators.putAll((Map<String,Object>)JSON.deserializeUntyped(dummyJSON)); 
            mpOfVstrIndicators.put('First Time Visitor This Year',true);
         }
    }
    
    @isTest static void testgetTaskAccountInfo()
    {
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        
        system.runAs(oCCSUser)
        {
            List<Id> accids = new List<Id>();
            Account oAcc = [select Id, Name,GCM_Visitor_Barcode__c,FirstName, LastName, PersonBirthdate, Birthdate__c, PersonMailingState, PersonMailingPostalCode, 
                            recordtypeid,RecordType.Name, PersonHomePhone,Enterprise_ID__c from Account where FirstName = 'Alex' LIMIT 1];
            system.assertEquals(oAcc.FirstName,'Alex');
            
            Task tsk = [SELECT Id,WhatId from Task where WhatId=:oAcc.Id LIMIT 1];
            system.assertEquals(oAcc.Id,tsk.WhatId);
            
            String endDate = (Date.today()+5).format();
            List<Policy_Member__c> lstOfPmbrs = [SELECT Id,End_Date__c,Member__r.Name from Policy_Member__c where End_Date__c=:endDate AND Member__c=:oAcc.Id];
            system.assertEquals((Date.today()+5).format(), lstOfPmbrs.get(0).End_Date__c);
            
            Test.startTest();
            CRMRetail_Result_DTO_HUM taskAccInfo = GenericHelper_CRMRetail_H_HUM.getTaskAccountInfo(tsk.Id);
            Map<String,Object> mapOfResponse = (Map<String,Object>)Json.deserializeUntyped(String.valueOf(taskAccInfo.sResult));
            system.assertEquals(lstOfPmbrs.get(0).Member__r.Name,mapOfResponse.get('Name'));
            
            String effDate = (Date.today()+1).format();
            lstOfPmbrs.get(0).Effective_Date__c = effDate;
            lstOfPmbrs.get(1).Effective_Date__c = effDate;
            update lstOfPmbrs.get(0);
            update lstOfPmbrs.get(1);
            
            lstOfPmbrs = [SELECT Id,End_Date__c,Effective_Date__c,Member__r.Name from Policy_Member__C where Effective_Date__c=: effDate AND Member__c=: oAcc.Id];
            system.assertEquals(effDate,lstOfPmbrs.get(0).Effective_Date__c);
            system.assertEquals(effDate,lstOfPmbrs.get(0).Effective_Date__c);
            
            taskAccInfo = GenericHelper_CRMRetail_H_HUM.getTaskAccountInfo(tsk.Id);
            mapOfResponse = (Map<String,Object>)Json.deserializeUntyped(String.valueOf(taskAccInfo.sResult));
            system.assertEquals(lstOfPmbrs.get(0).Member__r.Name,mapOfResponse.get('Name'));
            
            effDate = (Date.today()-10).format();
            endDate = (Date.today()-5).format();
            lstOfPmbrs.get(0).Effective_Date__c = effDate;
            lstOfPmbrs.get(1).Effective_Date__c = effDate;
            lstOfPmbrs.get(0).End_Date__c = endDate;
            lstOfPmbrs.get(1).End_Date__c = endDate;
            update lstOfPmbrs.get(0);
            update lstOfPmbrs.get(1);
            
            lstOfPmbrs = [SELECT Id,End_Date__c,Effective_Date__c,Member__r.Name from Policy_Member__C where Effective_Date__c=: effDate AND End_Date__c=: endDate AND Member__c=: oAcc.Id];
            system.assertEquals(effDate,lstOfPmbrs.get(0).Effective_Date__c);
            system.assertEquals(effDate,lstOfPmbrs.get(0).Effective_Date__c);
            system.assertEquals(endDate,lstOfPmbrs.get(0).End_Date__c);
            system.assertEquals(endDate,lstOfPmbrs.get(0).End_Date__c);
            
            taskAccInfo = GenericHelper_CRMRetail_H_HUM.getTaskAccountInfo(tsk.Id);
            mapOfResponse = (Map<String,Object>)Json.deserializeUntyped(String.valueOf(taskAccInfo.sResult));
            system.assertEquals(lstOfPmbrs.get(0).Member__r.Name,mapOfResponse.get('Name'));
            Test.stopTest();
        }
    }
    static testMethod void prepareTestDataForSearchCriteria01()
    {
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        
        system.runAs(oCCSUser)
        {
            List<Id> accids = new List<Id>();
            Account oAcc = [select Id, Name,GCM_Visitor_Barcode__c,FirstName, LastName, PersonBirthdate, Birthdate__c, PersonMailingState, PersonMailingPostalCode, 
                            recordtypeid,RecordType.Name, PersonHomePhone,Enterprise_ID__c from Account where FirstName = 'UNKNOWN' LIMIT 1];
            system.assertEquals(oAcc.FirstName,'UNKNOWN');
            CRMFunctionality_ONOFF_Switch__c switch_1209425 = CRMFunctionality_ONOFF_Switch__c.getValues('1209425');
            switch_1209425.IsON__c = false;
            update switch_1209425;
            system.assertEquals(switch_1209425.IsON__c, false);
            CRMFunctionality_ONOFF_Switch__c switch_3573183 = CRMFunctionality_ONOFF_Switch__c.getValues('3573183');
            switch_3573183.IsON__c = false;
            update switch_3573183;
            system.assertEquals(switch_3573183.IsON__c, false);
            Set<String> stOfReasonName = new Set<String>();
            stOfReasonName.add('Import Reason1');
            stOfReasonName.add('Import Reason1');
			Map<Id,Storefront_I_Reason__c> mpOfReasonRecords = GenericHelper_CRMRetail_H_HUM.getIntReasonRecords(stOfReasonName);
            system.assertEquals(0,mpOfReasonRecords.size());
            accids.add(oAcc.Id);
            GenericHelper_CRMRetail_H_HUM GenericHelperController = new GenericHelper_CRMRetail_H_HUM();

            Map<String, Map<String, String>> visitorIdTypeMap = GenericHelperController.fetchvisitorIdType(accids);
            system.assertNotEquals(visitorIdTypeMap.size(), 0);
         }
    }
    
    static testMethod void prepareTestDataForSearchCriteria1()
    {
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        
        system.runAs(oCCSUser)
        {
            List<Id> accids = new List<Id>();
            Account oAcc = [select Id, Name,GCM_Visitor_Barcode__c,FirstName, LastName, PersonBirthdate, Birthdate__c, PersonMailingState, PersonMailingPostalCode, 
                            recordtypeid,RecordType.Name, PersonHomePhone,Enterprise_ID__c from Account where FirstName = 'Test' LIMIT 1];
            system.assertEquals(oAcc.FirstName,'Test');
            accids.add(oAcc.Id);
            GenericHelper_CRMRetail_H_HUM GenericHelperController = new GenericHelper_CRMRetail_H_HUM();

            Map<String, Map<String, String>> visitorIdTypeMap = GenericHelperController.fetchvisitorIdType(accids);
            system.assertNotEquals(visitorIdTypeMap.size(), 0);
         }
    }
    
    
    static testMethod void prepareTestDataForSearchCriteria2()
    {
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        
        system.runAs(oCCSUser)
        {
            List<Id> accids = new List<Id>();
            Account oAcc = [select Id, Name,GCM_Visitor_Barcode__c,FirstName, LastName, PersonBirthdate, Birthdate__c, PersonMailingState, PersonMailingPostalCode, 
                            recordtypeid,RecordType.Name, PersonHomePhone,Enterprise_ID__c from Account where FirstName = 'Daphiner' LIMIT 1];
            system.assertEquals(oAcc.FirstName,'Daphiner');
            accids.add(oAcc.Id);
            GenericHelper_CRMRetail_H_HUM GenericHelperController = new GenericHelper_CRMRetail_H_HUM();

            Map<String, Map<String, String>> visitorIdTypeMap = GenericHelperController.fetchvisitorIdType(accids);
            system.assertNotEquals(visitorIdTypeMap.size(), 0);
         }
    }
    
     /*
    * Method Name : prepareTestDataForSearchCriteria
    * Description : Test method to cover  
    * Return Type : NA
    * Parameters : NA
    */
    static testMethod void prepareTestDataForSearchCriteria()
    {
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        
        system.runAs(oCCSUser)
        {
            List<Id> accids = new List<Id>();
            Account oAcc = [select Id, Name,FirstName, LastName, PersonBirthdate, Birthdate__c, PersonMailingState, PersonMailingPostalCode, 
                            recordtypeid,RecordType.Name, PersonHomePhone,Enterprise_ID__c from Account where LastName = 'Superman' LIMIT 1];
            system.assertEquals(oAcc.LastName,'Superman');
            accids.add(oAcc.Id);
            
            Account oAcc1 = [select Id, Name,FirstName, LastName, PersonBirthdate, Birthdate__c, PersonMailingState, PersonMailingPostalCode, 
                            recordtypeid,RecordType.Name, PersonHomePhone from Account where LastName = 'Account' LIMIT 1];
            
            accids.add(oAcc1.Id);
            
            Account visitor = [select Id, Name,FirstName, LastName, PersonBirthdate, Birthdate__c, PersonMailingState, PersonMailingPostalCode, 
                            recordtypeid,RecordType.Name, PersonHomePhone,Enterprise_ID__c, Waiver_Date__c from Account where LastName = 'Visitor' LIMIT 1];
            
            accids.add(visitor.Id);
            
            List<Storefront_Interaction__c> gcminteractionobj = [select Id, Name,Account__c,Account__r.Name,Reason__c,Location__c,LASTMODIFIEDDATE,Category__c from Storefront_Interaction__c];
            system.assertNotEquals(gcminteractionobj.size(), 0);
            Member_Id__c lstMembers = [Select Id,Name,Member_Id_Value__c from Member_Id__c where Policy_Member__c != NULL LIMIT 1];
            system.assertEquals(lstMembers.Member_Id_Value__c, '1234456');
            
            GenericHelper_CRMRetail_H_HUM GenericHelperController = new GenericHelper_CRMRetail_H_HUM();
            Map<String, String> waiverMap = GenericHelperController.checkWaiverDate(gcminteractionobj);
            system.assertEquals(waiverMap.size(), 1);
            
            CRMFunctionality_ONOFF_Switch__c switch_1396838 = CRMFunctionality_ONOFF_Switch__c.getValues('1396838');
            switch_1396838.IsON__c = false;
            update switch_1396838;
            system.assertEquals(switch_1396838.IsON__c, false);
            
            Map<String, String> waiverMapInactiveSwitch = GenericHelperController.checkWaiverDate(gcminteractionobj);
            system.assertEquals(waiverMap.size(), 1);
            
            Map<String, Map<String, String>> visitorIdTypeMap = GenericHelperController.fetchvisitorIdType(accids);
            system.assertNotEquals(visitorIdTypeMap.size(), 0);
            
            CRMFunctionality_ONOFF_Switch__c switch_1209425 = CRMFunctionality_ONOFF_Switch__c.getValues('1209425');
            switch_1209425.IsON__c = false;
            update switch_1209425;
            system.assertEquals(switch_1209425.IsON__c, false);
            
            Map<String, Map<String, String>> visitorIdTypeMap1 = GenericHelperController.fetchvisitorIdType(accids);
            system.assertNotEquals(visitorIdTypeMap1.size(), 0);
         }
    }
     /*
    * Method Name : testService
    * Description : Test method to cover  
    * Return Type : NA
    * Parameters : NA
    */
    static testMethod void testService()
    {
       User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        
        system.runAs(oCCSUser)
        {
            List<Id> accids = new List<Id>();
            Account oAcc = [select Id, Name,FirstName, LastName, PersonBirthdate, Birthdate__c, PersonMailingState, PersonMailingPostalCode, 
                            recordtypeid,RecordType.Name, PersonHomePhone,Enterprise_ID__c from Account where LastName = 'Superman' LIMIT 1];
            system.assertEquals(oAcc.LastName,'Superman');
           try{
                String callOutServiceName = 'AORDetailsFetchService';
                GenericHelper_CRMRetail_H_HUM.AORServiceResponseWrapper response = new GenericHelper_CRMRetail_H_HUM.AORServiceResponseWrapper();     
                GenericHelper_CRMRetail_H_HUM genericHelperController = new GenericHelper_CRMRetail_H_HUM();
               
                Test.startTest();
                    Test.setMock(HttpCalloutMock.class, new AORMockHttpResponse_LTNG_T_HUM());    
                    response = genericHelperController.aorCalloutCRMRetail(callOutServiceName,oAcc.Enterprise_ID__c);
                    response = genericHelperController.aorCalloutCRMRetail('','');
                Test.stopTest();
                system.assert(response != null);
           }
           catch(Exception e){
               HUMExceptionHelper.logErrors(e, 'GenericHelper_CRMRetail_T_HUM', 'TestclasswithMockData');
           }
       }
    }
    /*
    * Method Name : testDuplicateInteractions
    * Description : Test method to cover checkDuplicateInteractions method
    * Return Type : NA
    * Parameters : NA
    */
    static testMethod void testDuplicateInteractions()
    {
       User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        map<String, boolean> dupInteractionMap = new map<String, boolean>();
        List<String> permissionsetList = new List<String>();
        
        system.runAs(oCCSUser)
        {
            List<Id> accids = new List<Id>();
            Account oAcc = [select Id, Name,FirstName, LastName, PersonBirthdate, Birthdate__c, PersonMailingState, PersonMailingPostalCode, 
                            recordtypeid,RecordType.Name, PersonHomePhone,Enterprise_ID__c from Account where LastName = 'Superman' LIMIT 1];
            List<Storefront_Interaction__c> gcminteractionobj = [select Id, Name,Account__c,Account__r.Name,Reason__c,Location__c,Interaction_Date__c,Storefront_Event_Starttime__c,Visitor_ID__c, Category__c from Storefront_Interaction__c];
            system.assertEquals(oAcc.LastName,'Superman');
           try
           {
               dupInteractionMap = GenericHelper_CRMRetail_H_HUM.checkDuplicateInteractions(gcminteractionobj);
               system.assertNotEquals(dupInteractionMap.size(), 0);
               CRMFunctionality_ONOFF_Switch__c Switch_1243084 = new CRMFunctionality_ONOFF_Switch__c();
               Switch_1243084.IsON__c = true;
               Switch_1243084.Name = '1243084';
               insert Switch_1243084;
               system.assertEquals(Switch_1243084.Name,'1243084');
               dupInteractionMap = GenericHelper_CRMRetail_H_HUM.checkDuplicateInteractions(gcminteractionobj);
               system.assertNotEquals(dupInteractionMap.size(), 0);
               permissionsetList = GenericHelper_CRMRetail_H_HUM.getUserPermissionSet();
               system.assertNotEquals(permissionsetList.size(), 0);
           }
           catch(Exception e){
               HUMExceptionHelper.logErrors(e, 'GenericHelper_CRMRetail_T_HUM', 'testDuplicateInteractions');
           }
       }
    }
    
    /*
    * Method Name : testMaximumAttendance
    * Description : Test method to cover deduceMaxVisitorLimit and evaluateAttendance method
    * Return Type : NA
    * Parameters : NA
    */
    @isTest static void testMaximumAttendance1()
    {
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        
        system.runAs(oCCSUser)
        {
            Storefront_Location__c location1 = [SELECT Id, Name, Maximum_Occupancy__c FROM Storefront_Location__c LIMIT 1];
            location1.Maximum_Occupancy__c = 4;
           update location1;
            String locationVal = location1.Name + '-' + location1.Id;
            Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION, locationVal);
            String testHomeLocation = GenericHelper_CRMRetail_H_HUM.getHomeLocation();            
            system.assert(testHomeLocation != NULL);
            system.assertEquals(testHomeLocation,location1.Name);
            Datetime myDateTime = system.Now();
            String formatted = myDateTime.formatGMT('yyyy-MM-dd');
            Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE, formatted);
            try
            {
                Attendance__c att = GenericHelper_CRMRetail_H_HUM.createAttendance(location1.Id, system.Today());
                insert att;
                system.assertEquals(att.Current_Attendance__c, 1);
                att.Current_Attendance__c = 2;
                update att;
                system.assertEquals(att.Current_Attendance__c, 2);
                att = GenericHelper_CRMRetail_H_HUM.evaluateAttendance(att);
                system.assertEquals(att.Maximum_Concurrent_Visitors__c, 2);
                Decimal capacity = GenericHelper_CRMRetail_H_HUM.deduceMaxVisitorLimit();
                system.assertEquals(capacity, 0);
                
                Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE, date.today().format());
                
                capacity = GenericHelper_CRMRetail_H_HUM.deduceMaxVisitorLimit();
                system.assertEquals(capacity, 0);
                
                Cache.Session.remove(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE);
                
                capacity = GenericHelper_CRMRetail_H_HUM.deduceMaxVisitorLimit();
                system.assertEquals(capacity, 0);
                               
                
            }
            catch(Exception e)
            {
                HUMExceptionHelper.logErrors(e, 'GenericHelper_CRMRetail_T_HUM', 'testMaximumAttendance');
            }
       }
    }
    
    /*
    * Method Name : testMaximumAttendance
    * Description : Test method to cover deduceMaxVisitorLimit and evaluateAttendance method
    * Return Type : NA
    * Parameters : NA
    */
    @isTest static void testMaximumAttendance()
    {
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        
        system.runAs(oCCSUser)
        {
            Storefront_Location__c location1 = [SELECT Id, Name, Maximum_Occupancy__c FROM Storefront_Location__c LIMIT 1];
            location1.Name = location1.Id;
            update location1;
            String locationVal = location1.Name + '-' + location1.Id;
            Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION, locationVal);
            Datetime myDateTime = system.Now();
            String formatted = myDateTime.formatGMT('yyyy-MM-dd');
            Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE, formatted);
            try
            {
                Attendance__c att = GenericHelper_CRMRetail_H_HUM.createAttendance(location1.Id, system.Now().dateGMT());
                insert att;
                system.assertEquals(att.Current_Attendance__c, 1);
                att.Current_Attendance__c = 2;
                update att;
                system.assertEquals(att.Current_Attendance__c, 2);
                att = GenericHelper_CRMRetail_H_HUM.evaluateAttendance(att);
                system.assertEquals(att.Maximum_Concurrent_Visitors__c, 2);
                Decimal capacity = GenericHelper_CRMRetail_H_HUM.deduceMaxVisitorLimit();
                system.assertEquals(capacity, 2);
            }
            catch(Exception e)
            {
                HUMExceptionHelper.logErrors(e, 'GenericHelper_CRMRetail_T_HUM', 'testMaximumAttendance');
            }
       }
    }
    
    @isTest static  void testfetchSwitchResults()
    {
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');       
        system.runAs(oCCSUser)
        {
            try
            {
                Map<String,Boolean> switchMap = new Map<String,Boolean>();
                switchMap = GenericHelper_CRMRetail_H_HUM.fetchSwitchResults();
                System.assertNotEquals(switchMap.size(),0);
            }
            catch(Exception e)
            {
                HUMExceptionHelper.logErrors(e, 'GenericHelper_CRMRetail_T_HUM', 'testfetchSwitchResults');
            }
        }   
    }
    
    @isTest static  void testcheckTaskDuplicates()
    {
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');       
        system.runAs(oCCSUser)
        {
            try
            {
                List<Task> lstTask = [Select id, Subject, WhatId, CRM_Retail_Location__c, OwnerId, ActivityDate from Task LIMIT 2];
                system.assertEquals(lstTask.size(), 2);  
                
                String tempKey = lstTask[0].Subject+'@'+lstTask[0].WhatId+'@'+lstTask[0].CRM_Retail_Location__c+'@'+lstTask[0].OwnerId+'@'+lstTask[0].ActivityDate;
                String tempKey1 = lstTask[1].Subject+'@'+lstTask[1].WhatId+'@'+lstTask[1].CRM_Retail_Location__c+'@'+lstTask[1].OwnerId+'@'+lstTask[1].ActivityDate;
                                
                Map<String,Map<String,Task>> switchMap = new Map<String,Map<String,Task>>();
                Map<String,Task> mapOfTask = new Map<String,Task>();
                mapOfTask.put(tempKey, lstTask[0]);
                mapOfTask.put(tempKey1, lstTask[1]);
                switchMap = GenericHelper_CRMRetail_H_HUM.checkTaskDuplicates(mapOfTask);
                System.assertNotEquals(switchMap.size(),0);
            }
            catch(Exception e)
            {
                HUMExceptionHelper.logErrors(e, 'GenericHelper_CRMRetail_T_HUM', 'testcheckTaskDuplicates');
            }
        }   
    }
    
    @isTest static  void testcheckTaskNonDuplicates()
    {
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');       
        system.runAs(oCCSUser)
        {
            try
            {
                List<Task> lstTask = [Select id, Subject, WhatId, CRM_Retail_Location__c, OwnerId, ActivityDate from Task LIMIT 3];
                system.assertEquals(lstTask.size(), 3);  
                
                String tempKey = 'Test@'+lstTask[0].WhatId+'@'+lstTask[0].CRM_Retail_Location__c+'@'+lstTask[0].OwnerId+'@'+lstTask[0].ActivityDate;
                String tempKey1 = 'Test@'+lstTask[1].WhatId+'@'+lstTask[1].CRM_Retail_Location__c+'@'+lstTask[1].OwnerId+'@'+lstTask[1].ActivityDate;
                String tempKey2 = 'Test@'+lstTask[2].WhatId+'@'+lstTask[2].CRM_Retail_Location__c+'@'+lstTask[2].OwnerId+'@'+lstTask[2].ActivityDate;

                Map<String,Map<String,Task>> switchMap = new Map<String,Map<String,Task>>();
                Map<String,Task> mapOfTask = new Map<String,Task>();
                mapOfTask.put(tempKey, lstTask[0]);
                mapOfTask.put(tempKey1, lstTask[1]);
                mapOfTask.put(tempKey, lstTask[0]);
                switchMap = GenericHelper_CRMRetail_H_HUM.checkTaskDuplicates(mapOfTask);
                System.assertNotEquals(switchMap.size(),0);
            }
            catch(Exception e)
            {
                HUMExceptionHelper.logErrors(e, 'GenericHelper_CRMRetail_T_HUM', 'testcheckTaskNonDuplicates');
            }
        }   
    }
    
    @isTest static  void testgetIntReasonRecords()
    {
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');       
        system.runAs(oCCSUser)
        {
            try
            {
                Map<Id,Storefront_I_Reason__C> mapReason = new Map<Id,Storefront_I_Reason__C>();
                Set<String> setReason = new Set<String>();
                setReason.add('Social-Cafe');
                mapReason = GenericHelper_CRMRetail_H_HUM.getIntReasonRecords(setReason);
                System.assertNotEquals(mapReason.size(),0);
            }
            catch(Exception e)
            {
                HUMExceptionHelper.logErrors(e, 'GenericHelper_CRMRetail_T_HUM', 'testgetIntReasonRecords');
            }
        }   
    }
    
    @isTest static  void testgetUserRecords()
    {
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');       
        system.runAs(oCCSUser)
        {
            try
            {
                Map<Id,User> mapUser = new Map<Id,User>();
                Set<String> setUserName = new Set<String>();
                setUserName.add('user1user1@humcrmstest.com');
                mapUser = GenericHelper_CRMRetail_H_HUM.getUserRecords(setUserName);
                System.assertEquals(mapUser.size(),0);
            }
            catch(Exception e)
            {
                HUMExceptionHelper.logErrors(e, 'GenericHelper_CRMRetail_T_HUM', 'testgetUserRecords');
            }
        }   
    }
    
    @isTest static  void testgetLocation()
    {
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');       
        system.runAs(oCCSUser)
        {
            try
            {
                Map<Id,Storefront_Location__c> mapLocation = new Map<Id,Storefront_Location__c>();
                Set<String> setLocation = new Set<String>();
                setLocation.add('Virginia');
                mapLocation = GenericHelper_CRMRetail_H_HUM.getLocation(setLocation);
                System.assertEquals(mapLocation.size(),1);
            }
            catch(Exception e)
            {
                HUMExceptionHelper.logErrors(e, 'GenericHelper_CRMRetail_T_HUM', 'testgetLocation');
            }
        }   
    }
    
    @isTest static  void testgetMemberRecords()
    {
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');       
        system.runAs(oCCSUser)
        {
            try
            {
                List<Member_ID__C> lstMember = new List<Member_ID__C>(); 
                Map<integer,String> mapOfMemberId = new Map<integer,String>();
                mapOfMemberId.put(1,'{12345}');
                lstMember = GenericHelper_CRMRetail_H_HUM.getMemberRecords(mapOfMemberId);
                System.assertEquals(lstMember.size(),0);
                mapOfMemberId.put(2,'{123456}');
                lstMember = GenericHelper_CRMRetail_H_HUM.getMemberRecords(mapOfMemberId);
                System.assertEquals(lstMember.size(),0);
            }
            catch(Exception e)
            {
                HUMExceptionHelper.logErrors(e, 'GenericHelper_CRMRetail_T_HUM', 'testgetMemberRecords');
            }
        }   
    }
    
    @isTest static  void testgetVirtualLocations()
    {
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');       
        system.runAs(oCCSUser)
        {
            try
            {
                List<Storefront_Location__c> lstLocations = new List<Storefront_Location__c>();
                lstLocations = [Select id, Location_Type__c from Storefront_Location__c LIMIT 1];
                lstLocations[0].Location_Type__c = GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_VIRTUAL;
                update lstLocations[0];
                System.assertEquals(lstLocations.size() , 1);
                Set<Id> setLocationId = new Set<Id>();
                Set<Id> setLocation = new Set<Id>();
                setLocation.add(lstLocations[0].Id);
                setLocationId = GenericHelper_CRMRetail_H_HUM.getVirtualLocations(setLocation);
                System.assertEquals(setLocationId.size(),1);
            }
            catch(Exception e)
            {
                HUMExceptionHelper.logErrors(e, 'GenericHelper_CRMRetail_T_HUM', 'setLocationId');
            }
        }   
    }
    @isTest static  void testgetLocationMdtNames()
    {
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');       
        system.runAs(oCCSUser)
        {
            try
            {
               Set<String> setOflocationMDtName = GenericHelper_CRMRetail_H_HUM.getLocationMdtNames();
               system.assertNotEquals(setOflocationMDtName,NULL);
               system.assert(setOflocationMDtName.contains('Virtual HNC'));
            }
            catch(Exception e)
            {
                HUMExceptionHelper.logErrors(e, 'GenericHelper_CRMRetail_T_HUM', 'setLocationId');
            }
        }   
    }
    @isTest static void testGetHomeLocation(){
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');       
        system.runAs(oCCSUser)
        {
            try
            {
                
            }
            catch(Exception e)
            {
                HUMExceptionHelper.logErrors(e, 'GenericHelper_CRMRetail_T_HUM', 'testGetHomeLocation');
            }
        }
    }
}