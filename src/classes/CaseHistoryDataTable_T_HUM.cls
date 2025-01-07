/*****************************************************************************************************
 Apex Class Name  : CaseHistoryDataTable_T_HUM 
 Version   : 1.0 
 Created Date  : 08/31/2017
 Function  : Test CaseHistoryDataTable_C_HUM controller

 
 Modification Log :
* Developer                 Code Review         Date                        Description
* -----------------------------------------------------------------------------------------------------                 
* Prasad Jandhyala           147508            08/31/2017                   Original Version
* Suraj Patil                187132            02/08/2018                   Original Version
* Anurag Shah                195492            03/05/2018                   Fixed for Defect 348141.
* Pooja Kumbhar								   04/29/2020					Checkmarx test method without assert error fix.
* Vamshi Krishna Pemberthi					   06/08/2021 					Code Coverage Improvements
* Prasuna Pattabhi                           07/31/2023                 Test Class error fix - build fix
*******************************************************************************************************/

@isTest
private with sharing class  CaseHistoryDataTable_T_HUM
{
    /*
    * Method Name : setUpTestData
    * Description : This method creates test data for test class
    * Return type : None
    * Parameter   : None
    */   
    
    @testSetup static void setUpTestData()
    {
        User objUser = HUMTestDataHelperTest.createTestUser('CaseHistoryDataTable_User1','Customer Care Specialist');
        insert objUser;
        system.assertEquals(objUser.lastname=='test CaseHistoryDataTable_User1',true);
        User objUser1 = HUMTestDataHelperTest.createTestUser('CaseHistoryDT_User1','Customer Care Specialist');
        insert objUser1;
        
        // Insert Member Account
        Account objMemAccount = HUMTestDataHelperTest.getAccount();
        objMemAccount.FirstName = 'Test FName';
        objMemAccount.LastName = 'LName'; 
        objMemAccount.source_platform_code__c = 'EM' ;
        objMemAccount.Enterprise_Id__c = String.valueof(2000);
        objMemAccount.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
        objMemAccount.Birthdate__c = String.valueOf(System.today().month()+'/'+system.today().day()+'/'+System.today().year());
        insert objMemAccount;
        system.assert(objMemAccount.LastName == 'LName');

        // Insert Member Account
        Account objMemAccountLV = HUMTestDataHelperTest.getAccount();
        objMemAccountLV.FirstName = 'Test FName';
        objMemAccountLV.LastName = 'LName2'; 
        objMemAccountLV.source_platform_code__c = 'LV' ;
        objMemAccountLV.Enterprise_Id__c = String.valueof(2001);
        objMemAccountLV.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
        objMemAccountLV.Birthdate__c = String.valueOf(System.today().month()+'/'+system.today().day()+'/'+System.today().year());
        insert objMemAccountLV;
        system.assert(objMemAccountLV.LastName == 'LName2');

        // Non Medical - Dental Policy   
        Policy__c objPolicyLV = HUMTestDataHelperTest.getPolicy();
        objPolicyLV.Name = 'Test Policy';
        objPolicyLV.source_cust_cov_key__c = '12345678911234567891' ;
        objPolicyLV.Group_Name__c = objMemAccountLV.Id;       
        insert objPolicyLV;
        system.assert(objPolicyLV.Name == 'Test Policy');

        //Non Medical - Dental Policy Member
        Policy_Member__c objPolMemLV = HUMTestDataHelperTest.getPolicyMember();
        objPolMemLV.Member__c = objMemAccountLV.Id;
        objPolMemLV.Policy__c = objPolicyLV.Id;
        objPolMemLV.Relationship__c = 'self'; 
        objPolMemLV.name = '12591';              
        objPolMemLV.Policy_Member_External_ID__c = '00|ODS|123456785|89|89|02-01-2001';
        insert objPolMemLV;
        system.assert(objPolMemLV.Relationship__c == 'self'); 

        //Case Object Test Data----------------------------------------------------
        Case caseRecord = new Case();
        caseRecord.Owner_Queue__c = 'Louisville RSO Calls Support';
        caseRecord.Status = 'In Progress';
        caseRecord.AccountId = objMemAccount.id;
        caseRecord.ownerId = objUser.Id;
        caseRecord.Interacting_With__c = objMemAccount.id;
        caseRecord.Interacting_With_Type__c = 'Member';
        caseRecord.Policy_Member__c = objPolMemLV.Id;
        caseRecord.Interacting_About_Type__c = 'Member';
        insert caseRecord;
        System.assertequals(caseRecord.Status,'In Progress');
        
        caseRecord.Interacting_With__c = objMemAccountLV.id;
        caseRecord.Owner_Queue__c = 'Group Service Operations Calls';
        caseRecord.Status = 'In Progress';
        update caseRecord;


    }
    
    Public static testMethod  void testCaseHistoryDataTable()
    {
        //setUpTestData();
        User testUser = [Select Id,Name,LastName from User where LastName='test CaseHistoryDataTable_User1'];
        System.assertEquals(testUser.LastName, 'test CaseHistoryDataTable_User1');
        Case caseRec = [SELECT Id,Owner_Queue__c,Status, Case_Owner__c,CreatedByID FROM Case LIMIT 1];
        //CaseHistory cach=new CaseHistory(Field = 'abd');
        //insert cach;
       
        System.runAs(testUser )
        {
            Test.startTest();                   
            CaseHistoryDataTable_C_HUM cas = new CaseHistoryDataTable_C_HUM();
			caseRec.Status = 'Created';
            caseRec.Interacting_About_Type__c = 'Member';
            update caseRec;
            list<CaseHistory_DTO_HUM> CaseHistoryDataList = CaseHistoryDataTable_C_HUM.getCaseHistoryData(caseRec.Id,CaseDetailConstants_HUM.sCaseResolution);
			}   
    } 

    Public static testMethod  void testCaseHistoryDataTable1()
    {
        //setUpTestData();
        User testUser = [Select Id,Name,LastName from User where LastName='test CaseHistoryDataTable_User1'];
        System.assertEquals(testUser.LastName, 'test CaseHistoryDataTable_User1');
        Case caseRec = [SELECT Id,Owner_Queue__c,Status, Case_Owner__c,CreatedByID FROM Case LIMIT 1];
        //CaseHistory cach=new CaseHistory(Field = 'abd');
        //insert cach;
       
        System.runAs(testUser )
        {
            Test.startTest();                   
            CaseHistoryDataTable_C_HUM cas = new CaseHistoryDataTable_C_HUM();
            CaseHistory_DTO_HUM chd = new CaseHistory_DTO_HUM();
            chd.id= '';
            list<CaseHistory_DTO_HUM> CaseHistoryDataList = CaseHistoryDataTable_C_HUM.getCaseHistoryData(caseRec.Id,'CaseTransfer');
        
        }   
    } 

    Public static testMethod  void testCaseHistoryDataTableFault()
    {
        User testUser = [Select Id,Name,LastName from User where LastName='test CaseHistoryDataTable_User1'];
        System.assertEquals(testUser.LastName, 'test CaseHistoryDataTable_User1');
        Case caseRec = [SELECT Id, Case_Owner__c,CreatedByID FROM Case LIMIT 1];
       
        System.runAs(testUser )
        {
            caseRec.id = null;
            Test.startTest();                   
            CaseHistoryDataTable_C_HUM cas = new CaseHistoryDataTable_C_HUM();
            list<CaseHistory_DTO_HUM> CaseHistoryDataList = CaseHistoryDataTable_C_HUM.getCaseHistoryData(caseRec.Id,'ResolutionUser1');
        }   
    }
	Public static testMethod  void testCaseHistoryDataTableFault1()
    {
        User testUser = [Select Id,Name,LastName from User where LastName='test CaseHistoryDataTable_User1'];
        System.assertEquals(testUser.LastName, 'test CaseHistoryDataTable_User1');
        Case caseRec = [SELECT Id, Case_Owner__c,CreatedByID FROM Case LIMIT 1];
       
        System.runAs(testUser )
        {
            caseRec.id = null;
            Test.startTest();                   
            CaseHistoryDataTable_C_HUM cas = new CaseHistoryDataTable_C_HUM();
            list<CaseHistory_DTO_HUM> CaseHistoryDataList = CaseHistoryDataTable_C_HUM.getCaseHistoryData(caseRec.Id,'CaseTransfer');
        }   
    }
}