/******************************************************************************************************************
Apex class Name    : CRMRetail_Task_Import_T_HUM
Version            : 1.0
Function           : This class is used as test class for CRMRetail_Task_Import_Helper_HUM
Created On         : Nov 02 ,2021
Test Class         : 

Modification Log:
*   Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   Vinoth L                                        10/06/2021          User Story - 2578364 T1PRJ0001894 - MF 12 - CRM Storefront Task Management: Import Function
*   Abhishek Maurya                                 10/06/2021          User Story - 2578377 -CRM Storefront Task Management: Import File Confirmation
* 	Vinoth L                      		       		10/20/2021          User Story - 2578436 T1PRJ0001894 - MF 12 - CRM Storefront Task Management: Add Tasks
* 	Abhishek Maurya               	               	10/20/2021          User Story - 2578417 T1PRJ0001894 - MF 12 - CRM Storefront Task Management: Add Task Validation
*   Ekta Bist                                       10/20/2021          User Story - 2578506 T1PRJ0001894 - MF 12 - CRM Storefront Task Management: Export Errors
*   Ekta Bist                                       12/14/2021          User Story - 2956323 Task Import BreakFix
* 	Vinoth L                      		       		03/09/2022          03/11 Test class coverage fix
*   Vinoth L                                        09/14/2022          Task Import Case Sensitivity Fix
------------------------------------------------------------------------------------------------------------------- */
@isTest
private class CRMRetail_Task_Import_T_HUM {
    /**
* This method is used to insert the data only once and called automicticaly in all methods 
*
* @param  None      
* @return  Void
*/
    @testSetup static void setup()
    {   
        User oUser = HUMTestDataHelperTest.createTestUser('User2','CRMRetail User');
        insert oUser;  
        System.assertNotEquals(oUser, null);
        
        List<CRMFunctionality_ONOFF_Switch__c> lstSwitches = new List<CRMFunctionality_ONOFF_Switch__c>();
        CRMFunctionality_ONOFF_Switch__c csCRMOnOffSwitch = new  CRMFunctionality_ONOFF_Switch__c(Name = '2578377', IsON__c = True);
        CRMFunctionality_ONOFF_Switch__c Switch_3573183 = new CRMFunctionality_ONOFF_Switch__c(Name = '3573183', IsON__c = true);
        lstSwitches.add(csCRMOnOffSwitch);
        lstSwitches.add(Switch_3573183);
        insert lstSwitches;
        System.assertEquals(csCRMOnOffSwitch.Name,'2578377');
        System.assertEquals(Switch_3573183.Name,'3573183');
        
        System.runAs(oUser) 
        {   
            
            List<RecordType> listRecordType = [SELECT Name, SobjectType,IsPersonType FROM RecordType WHERE SobjectType='Account' AND (Name =: GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_RETAIL_VISITOR OR Name =: GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER) ORDER BY Name ASC];
            System.assertEquals(listRecordType.size(),2);
            
            Product2 prod = new Product2(Name = 'MED',Family = 'Hardware');
            insert prod;
            System.assertEquals(prod.Name, 'MED');
            
            Id pricebookId = Test.getStandardPricebookId();
            
            PricebookEntry standardPrice = new PricebookEntry(Pricebook2Id = pricebookId, Product2Id = prod.Id, UnitPrice = 10000, IsActive = true);
            insert standardPrice;
            System.assertEquals(standardPrice.Product2Id, prod.Id);
            
            Account objMemberAcc = CRMRetail_TestDataFactory_H_HUM.createObjAccount(listRecordType[1].Id,'09/13/1990','Luke','Lee','99801','llee1@test.com','Juneau','Alaska',false);    
            objMemberAcc.ETL_Record_Deleted__c = false;
            
            insert objMemberAcc;
            System.assertNotEquals(objMemberAcc, null);
            
            Account objMemberAcc1 = CRMRetail_TestDataFactory_H_HUM.createObjAccount(listRecordType[1].Id,'09/13/1990','Adam','Lee','99801','adam@test.com','Juneau','Alaska',false);    
            objMemberAcc1.ETL_Record_Deleted__c = false;
            
            insert objMemberAcc1;
            System.assertNotEquals(objMemberAcc1, null);
            
            Policy__c objPolicy1 = CRMRetail_TestDataFactory_H_HUM.createPolicy(objMemberAcc1.Id,'123');
            objPolicy1.Product__c = prod.Id;
            update objPolicy1;
            System.assertEquals(objPolicy1.Policy_Group_Number__c,'123');
            
            DateTime d1 = Date.Today().addDays(30) ;
            String endDate = d1.format('MM/dd/yyyy') ;
            
            DateTime d2 = Date.Today().addDays(-90) ;
            String effdate = d2.format('MM/dd/yyyy') ;
            
            Policy_Member__c objPolicyMember1 = CRMRetail_TestDataFactory_H_HUM.createPolicyMember(objMemberAcc1.Id, objPolicy1.Id, 'H12345743723', effDate, endDate, false); 
            System.assertEquals(objPolicyMember1.Name, 'H12345743723');  
            
            Member_ID__c obMemberId1 = CRMRetail_TestDataFactory_H_HUM.createMemberId(objPolicyMember1.Id, '12345', '3245' , objPolicy1.Id , '' , '');
            System.assertEquals(obMemberId1.Name, '12345'); 
            
            Policy__c objPolicy = CRMRetail_TestDataFactory_H_HUM.createPolicy(objMemberAcc.Id,'123');
            objPolicy.Product__c = prod.Id;
            update objPolicy;
            System.assertEquals(objPolicy.Policy_Group_Number__c,'123');
            
            
            Policy_Member__c objPolicyMember = CRMRetail_TestDataFactory_H_HUM.createPolicyMember(objMemberAcc.Id, objPolicy.Id, 'H12345743723', effDate, endDate, false);
            System.assertEquals(objPolicyMember.Name, 'H12345743723');            
            
            Member_ID__c obMemberId = CRMRetail_TestDataFactory_H_HUM.createMemberId(objPolicyMember.Id, '12345', '3245' , objPolicy.Id , '' , '');
            System.assertEquals(obMemberId.Name, '12345'); 
            
            Storefront_Location__c location = CRMRetail_TestDataFactory_H_HUM.createLocation('Tulsa','Street no. 1','Texas','12345','83684','california',23);
            insert location;
            System.assertEquals(location.Name,'Tulsa');
            
            Storefront_I_Type__c objIntType = CRMRetail_TestDataFactory_H_HUM.createInteractionType('Consumer Edu');
            insert objIntType;
            System.assertEquals(objIntType.Name,'Consumer Edu');
            
            Storefront_I_Reason__c objIntReason2 = CRMRetail_TestDataFactory_H_HUM.createInteractionReason('consumer test1',null);
            objIntReason2.Interaction_Type__c = objIntType.Id;
            insert objIntReason2;
            System.assertEquals(objIntReason2.Name,'consumer test1');
        }
    }
    @isTest static void testfetchSwitchResults()
    {
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        {
            Test.StartTest();  
            Map<String,Boolean> mapOfSwitchResult = CRMRetail_Task_Import_Helper_HUM.fetchSwitchResults();
            system.assertEquals(mapOfSwitchResult.get('isShowTaskImportBtn'),true);
            Test.StopTest();
        }
    }
    @isTest static void testvalidateTaskFieldsOnImport()
    {
        User oUser = [select Id, Name, Alias from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        {            
            Test.StartTest(); 
            Id [] fixedSearchResults= new Id[1];
            List<Member_ID__c> lstMemberId = [Select Id, Name from Member_ID__c where name = '12345'];
            fixedSearchResults[0] = lstMemberId[0].Id;
            Test.setFixedSearchResults(fixedSearchResults);
            DateTime activityDate = Date.Today().addDays(5) ;
            String dateAct = activityDate.format('MM/dd/yyyy') ;
            List<CRMRetail_Import_DTO_HUM> lstImprt = new List<CRMRetail_Import_DTO_HUM>();
            CRMRetail_Import_DTO_HUM obImprt = new CRMRetail_Import_DTO_HUM();
            obImprt.MEMBERID =  '12345';
            obImprt.LOCATION =  'Tulsa';
            obImprt.ASSIGNEDTO = oUser.Alias;
            obImprt.SUBJECT =  'consumer test1';
            obImprt.PRIORITY =  'Low';
            obImprt.DUEDATE =  dateAct;
            lstImprt.add(obImprt);
            Map<String,Object> mapOfvalidTField = CRMRetail_Task_Import_Helper_HUM.validateTaskFieldsOnImport(JSON.serialize(lstImprt));
            List<CRMRetail_Import_DTO_HUM> invalidCount = (List<CRMRetail_Import_DTO_HUM>)mapOfvalidTField.get('InvalidRecord');
            system.assertEquals(invalidCount.size(),1);
            Test.StopTest();
        }
    }
    @isTest static void testEmptyTaskFieldsOnImport()
    {
        User oUser = [select Id, Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        {
            Test.StartTest(); 
            Id [] fixedSearchResults= new Id[1];
            List<Member_ID__c> lstMemberId = [Select Id, Name from Member_ID__c where name = '12345'];
            fixedSearchResults[0] = lstMemberId[0].Id;
            Test.setFixedSearchResults(fixedSearchResults);
            List<CRMRetail_Import_DTO_HUM> lstImprt = new List<CRMRetail_Import_DTO_HUM>();
            CRMRetail_Import_DTO_HUM obImprt = new CRMRetail_Import_DTO_HUM();
            obImprt.MEMBERID =  '' ;
            obImprt.LOCATION =  '' ;
            obImprt.ASSIGNEDTO = '' ;
            obImprt.SUBJECT =  '' ;
            obImprt.PRIORITY =  '' ;
            obImprt.DUEDATE =  '' ;
            lstImprt.add(obImprt);
            CRMRetail_Import_DTO_HUM obImport = new CRMRetail_Import_DTO_HUM();
            obImport.MEMBERID =  '12345' ;
            obImport.LOCATION =  'TestLocation' ;
            obImport.ASSIGNEDTO = '' ;
            obImport.SUBJECT =  '' ;
            obImport.PRIORITY =  '' ;
            obImport.DUEDATE =  '' ;
            lstImprt.add(obImport);
            Map<String,Object> mapOfvalidTField = CRMRetail_Task_Import_Helper_HUM.validateTaskFieldsOnImport(JSON.serialize(lstImprt));
            List<CRMRetail_Import_DTO_HUM> invalidCount = (List<CRMRetail_Import_DTO_HUM>)mapOfvalidTField.get('InvalidRecord');
            system.assertEquals(invalidCount.size(),2);
            Test.StopTest();
        }
    }
    @isTest static void testvalidateDupTaskFields()
    {
        User oUser = [select Id, Name, Alias from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        {
            DateTime activityDate = Date.Today().addDays(5) ;
            String dateAct = activityDate.format('MM/dd/yyyy') ;            
            
            Test.StartTest(); 
            Id [] fixedSearchResults= new Id[1];
            List<Member_ID__c> lstMemberId = [Select Id, Name from Member_ID__c where name = '12345'];
            fixedSearchResults[0] = lstMemberId[0].Id;
            Test.setFixedSearchResults(fixedSearchResults);
            List<CRMRetail_Import_DTO_HUM> lstImprt = new List<CRMRetail_Import_DTO_HUM>();
            CRMRetail_Import_DTO_HUM obImprt = new CRMRetail_Import_DTO_HUM();
            obImprt.MEMBERID =  '12345';
            obImprt.LOCATION =  'Tulsa';
            obImprt.ASSIGNEDTO = oUser.Alias;
            obImprt.SUBJECT =  'consumer test1';
            obImprt.PRIORITY =  'Low';
            obImprt.DUEDATE =  dateAct;
            lstImprt.add(obImprt);
            
            
            Map<String,Object> mapOfvalidTField = CRMRetail_Task_Import_Helper_HUM.validateTaskFieldsOnImport(JSON.serialize(lstImprt));
            List<CRMRetail_Import_DTO_HUM> invalidCount = (List<CRMRetail_Import_DTO_HUM>)mapOfvalidTField.get('InvalidRecord');
            system.assertEquals(invalidCount.size(),1);
            
            Test.StopTest();
        }
    }
    @isTest static void testvalidateTaskOnImport()
    {
        User oUser = [select Id, Name, Alias from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        {            
            Test.StartTest(); 
            Id [] fixedSearchResults= new Id[1];
            List<Member_ID__c> lstMemberId = [Select Id, Name from Member_ID__c where name = '12345'];
            fixedSearchResults[0] = lstMemberId[0].Id;
            Test.setFixedSearchResults(fixedSearchResults);
            List<CRMRetail_Import_DTO_HUM> lstImprt = new List<CRMRetail_Import_DTO_HUM>();
            DateTime activityDate = Date.Today().addDays(5) ;
            String dateAct = activityDate.format('MM/dd/yyyy') ;
            CRMRetail_Import_DTO_HUM obImprt = new CRMRetail_Import_DTO_HUM();
            obImprt.MEMBERID =  '12345';
            obImprt.LOCATION =  'Tulsa';
            obImprt.ASSIGNEDTO = oUser.Alias;
            obImprt.SUBJECT =  'consumer test1';
            obImprt.PRIORITY =  'Low';
            obImprt.DUEDATE =  dateAct;
            lstImprt.add(obImprt);
            Map<String,Object> mapOfvalidTField = CRMRetail_Task_Import_Helper_HUM.validateTaskFieldsOnImport(JSON.serialize(lstImprt));
            List<CRMRetail_Import_DTO_HUM> invalidCount = (List<CRMRetail_Import_DTO_HUM>)mapOfvalidTField.get('InvalidRecord');
            system.assertEquals(invalidCount.size(),1);
            Test.StopTest();
        }
    }
    @isTest static void testDuplicateRecord()
    {
        User oUser = [select Id, Name, Alias from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        {            
            PermissionSet SuperAdmin = [SELECT Id FROM PermissionSet WHERE Name = 'CRMRetail_400_Super_Admin'];
            PermissionSetAssignment supAdmin = new PermissionSetAssignment(AssigneeId = oUser.id, PermissionSetId = SuperAdmin.Id);
            insert supAdmin;
            system.assertEquals(supAdmin.AssigneeId, oUser.id);
            
            List<Account> lstAccount = new List<Account>();
            lstAccount = [Select id from Account LIMIT 1];
            System.assertEquals(lstAccount.size(), 1);
            
            List<Storefront_Location__c> lstLocation = new List<Storefront_Location__c>();
            lstLocation = [Select Id, Name from Storefront_Location__c LIMIT 1];
            System.assertEquals(lstLocation.size(),1);

            DateTime activityDate = Date.Today().addDays(5) ;
            String dateAct = activityDate.format('MM/dd/yyyy') ;
            
            Id crmRecordTypeId = Schema.SObjectType.Task.getRecordTypeInfosByName().get('CRMRetail Task').getRecordTypeId();
            Task objTask = new Task(Priority = 'Low', Status = 'Not Started', CRMRetail_Automated__c = true, Recordtypeid = crmRecordTypeId, WhatId = lstAccount[0].Id, crm_retail_location__c = lstLocation[0].Id, OwnerId = oUser.Id, Subject = 'consumer test1', ActivityDate = Date.newInstance(activityDate.year(), activityDate.month(), activityDate.day()));
            insert objTask;
            
            Test.StartTest();             
            Id [] fixedSearchResults= new Id[2];
            List<Member_ID__c> lstMemberId = [Select Id, Name from Member_ID__c where name = '12345'];
            fixedSearchResults[0] = lstMemberId[0].Id;
            fixedSearchResults[1] = lstMemberId[1].Id;
            Test.setFixedSearchResults(fixedSearchResults);            
            List<CRMRetail_Import_DTO_HUM> lstImprt = new List<CRMRetail_Import_DTO_HUM>();
            CRMRetail_Import_DTO_HUM obImprt = new CRMRetail_Import_DTO_HUM();
            obImprt.MEMBERID =  '12345';
            obImprt.LOCATION =  'Tulsa';
            obImprt.ASSIGNEDTO = oUser.Alias;
            obImprt.SUBJECT =  'consumer test1';
            obImprt.PRIORITY =  'Low';
            obImprt.DUEDATE =  dateAct; 
            lstImprt.add(obImprt);                        

            CRMRetail_Import_DTO_HUM obImprt1 = new CRMRetail_Import_DTO_HUM();
            obImprt1.MEMBERID =  '12345';
            obImprt1.LOCATION =  'Tulsa';
            obImprt1.ASSIGNEDTO = oUser.Alias;
            obImprt1.SUBJECT =  'consumer test1';
            obImprt1.PRIORITY =  'Low';
            obImprt1.DUEDATE =  dateAct; 
            lstImprt.add(obImprt1);			
            
            CRMRetail_Task_Import_Helper_HUM.isTestClassContext = true;      
            Map<String,Object> mapOfvalidTField = CRMRetail_Task_Import_Helper_HUM.validateTaskFieldsOnImport(JSON.serialize(lstImprt));
            List<CRMRetail_Import_DTO_HUM> invalidCount = (List<CRMRetail_Import_DTO_HUM>)mapOfvalidTField.get('InvalidRecord');            
            system.assertEquals(invalidCount.size(),2);
            Test.StopTest();
        }
    }
    
    @isTest static void testInvalidDueDate()
    {
        User oUser = [select Id, Name, Alias from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        {
            Test.StartTest(); 
            Id [] fixedSearchResults= new Id[1];
            List<Member_ID__c> lstMemberId = [Select Id, Name from Member_ID__c where name = '12345'];
            fixedSearchResults[0] = lstMemberId[0].Id;
            Test.setFixedSearchResults(fixedSearchResults);
            List<CRMRetail_Import_DTO_HUM> lstImprt = new List<CRMRetail_Import_DTO_HUM>();
            CRMRetail_Import_DTO_HUM obImprt = new CRMRetail_Import_DTO_HUM();
            obImprt.MEMBERID =  '12345';
            obImprt.LOCATION =  'Tulsa';
            obImprt.ASSIGNEDTO = oUser.Alias;
            obImprt.SUBJECT =  'consumer test1';
            obImprt.PRIORITY =  'Low';
            obImprt.DUEDATE =  '1/1/2021'; 
            lstImprt.add(obImprt);
            Map<String,Object> mapOfvalidTField = CRMRetail_Task_Import_Helper_HUM.validateTaskFieldsOnImport(JSON.serialize(lstImprt));
            List<CRMRetail_Import_DTO_HUM> invalidCount = (List<CRMRetail_Import_DTO_HUM>)mapOfvalidTField.get('InvalidRecord');
            system.assertEquals(invalidCount.size(),1);
            Test.StopTest();
        }
    }
    
    @isTest static void insertTaskRecord()
    {
        User oUser = [select Id, Name, Alias from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        {    
            
            PermissionSet SuperAdmin = [SELECT Id FROM PermissionSet WHERE Name = 'CRMRetail_400_Super_Admin'];
            PermissionSetAssignment supAdmin = new PermissionSetAssignment(AssigneeId = oUser.id, PermissionSetId = SuperAdmin.Id);
            insert supAdmin;
            system.assertEquals(supAdmin.AssigneeId, oUser.id);

            Id [] fixedSearchResults= new Id[1];
            List<Member_ID__c> lstMemberId = [Select Id, Name from Member_ID__c where name = '12345'];
            fixedSearchResults[0] = lstMemberId[0].Id;
            Test.setFixedSearchResults(fixedSearchResults);
            Test.StartTest();
            DateTime d = Date.Today().addDays(2) ;
            String dateStr = d.format('MM/dd/yyyy') ;
            List<CRMRetail_Import_DTO_HUM> lstImprt = new List<CRMRetail_Import_DTO_HUM>();
            CRMRetail_Import_DTO_HUM obImprt = new CRMRetail_Import_DTO_HUM();
            obImprt.MEMBERID =  '12345';
            obImprt.LOCATION =  'Tulsa';
            obImprt.ASSIGNEDTO = oUser.Alias;
            obImprt.SUBJECT =  'consumer test1';
            obImprt.PRIORITY =  'Low';
            obImprt.DUEDATE =  dateStr;
            lstImprt.add(obImprt);
            
            CRMRetail_Task_Import_Helper_HUM.isTestClassContext   = false;
            Map<String,Object> mapOfvalidTField = CRMRetail_Task_Import_Helper_HUM.validateTaskFieldsOnImport(JSON.serialize(lstImprt));            
            System.assertEquals(mapOfvalidTField.get('ValidCount'), 1);
            
            CRMFunctionality_ONOFF_Switch__c switch_3573183 = CRMFunctionality_ONOFF_Switch__c.getValues('3573183');
            switch_3573183.IsON__c = false;
            update switch_3573183;
            system.assertEquals(switch_3573183.IsON__c, false);
            
            DateTime d1 = Date.Today().addDays(1) ;
            String dateStr1 = d1.format('MM/dd/yyyy') ;
            obImprt.DUEDATE = dateStr1;
            lstImprt.add(obImprt);
            Map<String,Object> mapOfvalidTField1 = CRMRetail_Task_Import_Helper_HUM.validateTaskFieldsOnImport(JSON.serialize(lstImprt));           
            System.assertEquals(mapOfvalidTField1.get('ValidCount'), 1);
            
            Test.StopTest();
        }
    }
    @isTest static void insertTaskRecordFailure()
    {
        User oUser = [select Id, Name, Alias from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        {    
            
            PermissionSet SuperAdmin = [SELECT Id FROM PermissionSet WHERE Name = 'CRMRetail_400_Super_Admin'];
            PermissionSetAssignment supAdmin = new PermissionSetAssignment(AssigneeId = oUser.id, PermissionSetId = SuperAdmin.Id);
            insert supAdmin;
            system.assertEquals(supAdmin.AssigneeId, oUser.id);

            Id [] fixedSearchResults= new Id[1];
            List<Member_ID__c> lstMemberId = [Select Id, Name from Member_ID__c where name = '12345'];
            fixedSearchResults[0] = lstMemberId[0].Id;
            Test.setFixedSearchResults(fixedSearchResults);
            Test.StartTest();
            DateTime d = Date.Today().addDays(2) ;
            String dateStr = d.format('MM/dd/yyyy') ;
            List<CRMRetail_Import_DTO_HUM> lstImprt = new List<CRMRetail_Import_DTO_HUM>();
            CRMRetail_Import_DTO_HUM obImprt = new CRMRetail_Import_DTO_HUM();
            obImprt.MEMBERID =  '12345';
            obImprt.LOCATION =  'Tulsa';
            obImprt.ASSIGNEDTO = oUser.Alias;
            obImprt.SUBJECT =  'consumer test1';
            obImprt.PRIORITY =  'Low';
            obImprt.DUEDATE =  dateStr;
            lstImprt.add(obImprt);

            CRMRetail_Task_Import_Helper_HUM.isTestClassContext   = true;
            Map<String,Object> mapOfvalidTField = CRMRetail_Task_Import_Helper_HUM.validateTaskFieldsOnImport(JSON.serialize(lstImprt));
            List<CRMRetail_Import_DTO_HUM> invalidCount = (List<CRMRetail_Import_DTO_HUM>)mapOfvalidTField.get('InvalidRecord');
            system.assertEquals(invalidCount.size(),1);
            Test.StopTest();
        }
    }
    
    @isTest static void insert200TaskRecord()
    {
        User oUser = [select Id, Name, Alias from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        {    
            
            PermissionSet SuperAdmin = [SELECT Id FROM PermissionSet WHERE Name = 'CRMRetail_400_Super_Admin'];
            PermissionSetAssignment supAdmin = new PermissionSetAssignment(AssigneeId = oUser.id, PermissionSetId = SuperAdmin.Id);
            insert supAdmin;
            system.assertEquals(supAdmin.AssigneeId, oUser.id);
            
            List<Policy__c> lstPolicy = [Select Id, Policy_Group_Number__c from Policy__c where Policy_Group_Number__c = '123'];
            System.assertEquals(lstPolicy.size(), 2);
            
            List<Policy_Member__c> lstPolicyMember = [Select ID,Member__c from Policy_Member__c where name = 'H12345743723'];
            System.assertEquals(lstPolicyMember.size(), 2);
            
            List<Member_ID__c> lstMemberID = new List<Member_ID__c>();
            List<CRMRetail_Import_DTO_HUM> lstImprt = new List<CRMRetail_Import_DTO_HUM>();
            CRMRetail_Import_DTO_HUM obImprt;
            
            Member_ID__c objMember;
            
            DateTime d = Date.Today().addDays(2) ;
            String dateStr = d.format('MM/dd/yyyy') ;
            
            List<Storefront_Location__c> lstLocation = new List<Storefront_Location__c>();
            lstLocation = [Select Id, Name from Storefront_Location__c where name = 'Tulsa' LIMIT 1];
            System.assertEquals(lstLocation.size(),1);
            
            for(Integer i = 0; i <= 201; i++){
                objMember = new Member_ID__c();
                objMember.Policy_Member__c = lstPolicyMember[0].Id;
                objMember.Name = '12345'+i;
                objMember.Member_Dependent_Code__c = '3245';
                objMember.Policy__c = lstPolicy[0].Id;
                lstMemberID.add(objMember);
                
                obImprt = new CRMRetail_Import_DTO_HUM();
                obImprt.MEMBERID =  '12345'+i;
                obImprt.LOCATION =  'Tulsa';
                obImprt.ASSIGNEDTO = oUser.Alias;
                obImprt.SUBJECT =  'consumer test1';
                obImprt.PRIORITY =  'Low';
                obImprt.DUEDATE =  dateStr;
                lstImprt.add(obImprt);
            }
            
            insert lstMemberID;
            System.assertEquals(lstImprt.size(),202);
            
            Id crmRecordTypeId = Schema.SObjectType.Task.getRecordTypeInfosByName().get('CRMRetail Task').getRecordTypeId();
            Task objTask = new Task(Priority = 'Low', Status = 'Not Started', CRMRetail_Automated__c = true, Recordtypeid = crmRecordTypeId, WhatId = lstPolicyMember[0].Member__c, crm_retail_location__c = lstLocation[0].Id, OwnerId = oUser.Id, Subject = 'consumer test1', ActivityDate = d.date());
            insert objTask;
            System.assertEquals(objTask.Subject,'consumer test1');
            
            Id [] fixedSearchResults= new Id[1];
            Map<Id, Member_ID__c> mapOfMemberId = new Map<Id, Member_ID__c>([Select Id, Name, ETL_Record_Deleted__c, Policy_Member__r.Member__r.PersonContactId,Policy_Member__r.Member__c,Policy_Member__r.Status__c, Policy_Member__r.ETL_Record_Deleted__c from Member_ID__c]);
            List<Id> memberId = new List<Id>();
            memberId.addAll(mapOfMemberId.keySet());
            fixedSearchResults = memberId;
            Test.setFixedSearchResults(fixedSearchResults);
            Test.StartTest();
            CRMRetail_Task_Import_Helper_HUM.isTestClassContext   = false;
            Map<String,Object> mapOfvalidTField = CRMRetail_Task_Import_Helper_HUM.validateTaskFieldsOnImport(JSON.serialize(lstImprt));
            Test.StopTest();
        }
    }    
}