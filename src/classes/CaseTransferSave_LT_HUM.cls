/*******************************************************************************************************************************
Apex Class Name : CaseTransferSave_LT_HUM
Version         : 1.0
Created On      : 4/7/2022
Function        : This class is test class for CaseTransferSave_LC_HUM

Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0            Aakash Aggarwal                                    4/7/2021               	Original Version 
*    2.0            Vijaya Lakshmi Tummala                             2/2/2023               	US 4128928
*    3.0             Gowthami Thota                                    07/31/2023              Test Class Fix for UAT 
*/
@isTest
public class CaseTransferSave_LT_HUM {
    
/**
* Test Setup method to create all the data required for testing.
*
* @param     NULL
* @return    void 
*/
    @testSetup static void setUpTestData(){
        Group g1 = new Group(Name='test_queue', type='Queue');
        insert g1;
        
        QueuesObject q1 = new QueueSObject(QueueID = g1.id,SobjectType = 'Work_Queue_Setup__c');
        insert q1;
        
        Group g2 = new Group(Name='test_queue2', type='Queue');
        insert g2;
        
        QueuesObject q2 = new QueueSObject(QueueID = g2.id,SobjectType = 'Work_Queue_Setup__c');
        insert q2;

        User oCCSUser = HUMTestDataHelperTest.createTestUser('UserCCS1','Customer Care Specialist');
        oCCSUser.Current_Queue__c='test_queue';
        insert oCCSUser;
        
        system.assert(oCCSUser.LastName == 'test UserCCS1');
        
        system.runAS(oCCSUser)
        {
            List<Account> accList = new List<Account>();            
            
            Account oMemAccount = HUMTestDataHelperTest.getAccount();
            oMemAccount.Group_Number__c = '987654321';
            oMemAccount.Enterprise_Id__c = '1000101';
            oMemAccount.recordtypeid = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            oMemAccount.firstName = 'Daphiner';
            oMemAccount.lastName = 'Superman';
            oMemAccount.Veteran_Status__c ='confirmed-veteran';
            oMemAccount.VA_Health_Enrollee__c ='true';
            oMemAccount.Home_Office_Account__c = false;
            accList.add(oMemAccount);
            if(accList != NULL) insert accList;
            system.assert(accList.size() == 1);
            
            MemberPlan mp = new MemberPlan();
            mp.Name = 'test member plan';
            mp.MemberId = oMemAccount.id;
            insert mp;
            
            Case cs = new Case();
            cs.Status = 'In Progress';
            cs.AccountId = accList[0].Id;
            cs.Topic__c = 'Select a Topic';
            cs.Interacting_With__c = accList[0].Id;
            cs.Interacting_With_Type__c  = 'Member';
            cs.Interacting_About_Type__c  = 'Member';
            cs.Service_Center__c = 'Louisville';
            cs.Department__c = 'Medicare Level 2 Claims';
            cs.Escalation_Indicator__c='Immediate Leader';
            insert cs;
            
            Classification_Type__c ct = new Classification_Type__c();
            ct.Name = 'Medical CRU/Corr';
            insert ct;
            
            classification__c cl = new classification__c();
            cl.Name = 'Access To Care';
            insert cl;
            
            Intent__c it = new Intent__c();
            it.Name = 'test intent';
            insert it;
            
            CTCI_Junction__c ctci = new CTCI_Junction__c();
            ctci.Classification__c = cl.id;
            ctci.Classification_Type__c = ct.id;
            ctci.Intent__c = it.id;
            ctci.IsActive__c = true;
            insert ctci;
            
            
            Work_Queue_Setup__c wq = new Work_Queue_Setup__c();
            wq.IsActive__c = TRUE;
            wq.OwnerId = g1.id;
            wq.Classification_Type__c = ct.id;
            insert wq;
        }
    }

/**
* This method is used to test the condition if ownerId is not null in wrapper in saveTransfer functionality
*
* @param     NULL
* @return    void 
*/
    static testmethod void testSaveTransfer() {
        User u = [Select id from user limit 1];
        system.runAs(u){
        CRMFunctionality_ONOFF_Switch__c objCSEligibility = new CRMFunctionality_ONOFF_Switch__c(
            Name = '1414643',
            IsON__c = true,
            Release_Name__c = '03/30/2022',
            Scrum_Team__c = 'PR00094919',
            Story_Title__c = 'Case Transfer');
        insert objCSEligibility; 
        Case cs = [SELECT id,ownerId,Status,Classification_Type__c,Escalation_Indicator__c,Escalation_Indicator_Date__c,Type,Owner_Queue__c,Priority,Transferred_Date__c,Case_Owner__c,Service_Center__c,Transfer_Reason__c,Department__c,Topic__c  from Case LIMIT 1];
        Classification_Type__c clsType = [Select id from Classification_Type__c LIMIT 1];
        String caseTransferInput = '{"oCaseRec":'+JSON.serialize(cs)+',"caseComment":"casecommnettest","ownerId":"'+cs.ownerId+'","ownerName":"Juan Acevedo Jr","mapServCenDeptWorkQueueSetup":{"Louisville#Medicare Level 2 Claims":{"Name":"Louisville Medicare Level 2 Claims","Classification_Type__c":"'+clsType.Id+'","CokY__c":false,"Department__c":"Medicare Level 2 Claims","DST__c":false,"General__c":true,"HO_Dental__c":false,"HO_Medical__c":false,"Contract_Protected_Data__c":false}} }';
        CaseTransferSave_LD_HUM.saveCaseTransfer(caseTransferInput); 
        CaseTransferSave_LD_HUM.CaseTransferWrapper ctw = new CaseTransferSave_LD_HUM.CaseTransferWrapper();
        CaseTransferSave_LD_HUM.calEscalationIndDate(cs);
        }
    }

/**
* This method is used to test the condition if User__c is not null in case in saveTransfer functionality
*
* @param     NULL
* @return    void 
*/
    static testmethod void testUseronSaveTransfer() {
         User u = [Select id from user limit 1];
        system.runAs(u){
        Case cs = [SELECT id,ownerId,Status,Classification_Type__c,Escalation_Indicator__c,Escalation_Indicator_Date__c,Type,Owner_Queue__c,Priority,Transferred_Date__c,Case_Owner__c,Service_Center__c,Transfer_Reason__c,Department__c,Topic__c  from Case LIMIT 1];
        cs.User__c = userinfo.getUserId();
        Classification_Type__c clsType = [Select id from Classification_Type__c LIMIT 1];
        String caseTransferInput = '{"oCaseRec":'+JSON.serialize(cs)+',"caseComment":"casecommnettest","ownerId":"'+cs.ownerId+'","ownerName":"Juan Acevedo Jr","mapServCenDeptWorkQueueSetup":{"Louisville#Medicare Level 2 Claims":{"Name":"Louisville Medicare Level 2 Claims","Classification_Type__c":"'+clsType.Id+'","CokY__c":false,"Department__c":"Medicare Level 2 Claims","DST__c":false,"General__c":true,"HO_Dental__c":false,"HO_Medical__c":false,"Contract_Protected_Data__c":false}} }';
        CaseTransferSave_LD_HUM.saveCaseTransfer(caseTransferInput); 
        CaseTransferSave_LD_HUM.CaseTransferWrapper ctw = new CaseTransferSave_LD_HUM.CaseTransferWrapper();
        CaseTransferSave_LD_HUM.calEscalationIndDate(cs);
        }
    }
	
/**
* This method is used to test the condition if Service Center is Provider, Department is Correspondence Task to MHK & Type is MHK Dispute Task on case in saveTransfer functionality
*
* @param     NULL
* @return    void 
*/
    static testmethod void testServiceCenternDeptonSaveTransfer() {
	 User u = [Select id from user limit 1];
        system.runAs(u){
        CRMFunctionality_ONOFF_Switch__c objCSEligibility = new CRMFunctionality_ONOFF_Switch__c(
            Name = '1414643',
            IsON__c = true,
            Release_Name__c = '03/30/2022',
            Scrum_Team__c = 'PR00094919',
            Story_Title__c = 'Case Transfer');
        insert objCSEligibility;
        Case cs = [SELECT id,ownerId,Status,Classification_Type__c,Escalation_Indicator__c,Escalation_Indicator_Date__c,Type,Owner_Queue__c,Priority,Transferred_Date__c,Case_Owner__c,Service_Center__c,Transfer_Reason__c,Department__c,Topic__c  from Case LIMIT 1];
        cs.Service_Center__c = 'Provider';
        cs.Department__c = 'Correspondence Task to MHK';
        cs.Type = 'MHK Dispute Task';
        Classification_Type__c clsType = [Select id from Classification_Type__c LIMIT 1];
        String caseTransferInput = '{"oCaseRec":'+JSON.serialize(cs)+',"caseComment":"","ownerId":"","ownerName":"Juan Acevedo Jr","mapServCenDeptWorkQueueSetup":{"Louisville#Medicare Level 2 Claims":{"Name":"Louisville Medicare Level 2 Claims","OwnerId":"'+cs.ownerId+'","Classification_Type__c":"'+clsType.Id+'","CokY__c":false,"Department__c":"Medicare Level 2 Claims","DST__c":false,"General__c":true,"HO_Dental__c":false,"HO_Medical__c":false,"Contract_Protected_Data__c":false}} }';
        CaseTransferSave_LD_HUM.saveCaseTransfer(caseTransferInput); 
        cs.Type  = '';
        CaseTransferSave_LD_HUM.saveCaseTransfer(caseTransferInput); 
        CaseTransferSave_LD_HUM.CaseTransferWrapper ctw = new CaseTransferSave_LD_HUM.CaseTransferWrapper();
        }
    }
/**
* This method is used to test the condition if Case Type other than MHK Task is transferred to 'Correspondence Task To MHK' Department in saveTransfer functionality
*
* @param     NULL
* @return    void 
*/
    static testmethod void testTypeonSaveTransfer() {
         User u = [Select id from user limit 1];
        system.runAs(u){
        CRMFunctionality_ONOFF_Switch__c objCSEligibility = new CRMFunctionality_ONOFF_Switch__c(
            Name = '1414643',
            IsON__c = true,
            Release_Name__c = '03/30/2022',
            Scrum_Team__c = 'PR00094919',
            Story_Title__c = 'Case Transfer');
        insert objCSEligibility;
        Case cs = [SELECT id,ownerId,Status,Classification_Type__c,Type,Owner_Queue__c,Priority,Transferred_Date__c,Case_Owner__c,Service_Center__c,Transfer_Reason__c,Department__c,Topic__c  from Case LIMIT 1];
        cs.Service_Center__c = 'Provider';
        cs.Department__c = 'Correspondence Task to MHK';
        cs.Type = '';
        Classification_Type__c clsType = [Select id from Classification_Type__c LIMIT 1];
        String caseTransferInput = '{"oCaseRec":'+JSON.serialize(cs)+',"caseComment":"","ownerId":"","ownerName":"Juan Acevedo Jr","mapServCenDeptWorkQueueSetup":{"Louisville#Medicare Level 2 Claims":{"Name":"Louisville Medicare Level 2 Claims","OwnerId":"'+cs.ownerId+'","Classification_Type__c":"'+clsType.Id+'","CokY__c":false,"Department__c":"Medicare Level 2 Claims","DST__c":false,"General__c":true,"HO_Dental__c":false,"HO_Medical__c":false,"Contract_Protected_Data__c":false,"Work_Queue_Name__c":"Market_Quality_Operations_Compliance"}} }';
        CaseTransferSave_LD_HUM.saveCaseTransfer(caseTransferInput); 
        CaseTransferSave_LD_HUM.CaseTransferWrapper ctw = new CaseTransferSave_LD_HUM.CaseTransferWrapper();
        }
    }
/**
* This method is used to test the condition of without switch in saveTransfer functionality
*
* @param     NULL
* @return    void 
*/
    static testmethod void testwithoutSwitchinSaveTransfer() {
         User u = [Select id from user limit 1];
        system.runAs(u){
        Case cs = [SELECT id,ownerId,Status,Classification_Type__c,Type,Owner_Queue__c,Priority,Transferred_Date__c,Case_Owner__c,Service_Center__c,Transfer_Reason__c,Department__c,Topic__c  from Case LIMIT 1];
        Classification_Type__c clsType = [Select id from Classification_Type__c LIMIT 1];
        String caseTransferInput = '{"oCaseRec":'+JSON.serialize(cs)+',"caseComment":"","ownerId":"","ownerName":"Juan Acevedo Jr","mapServCenDeptWorkQueueSetup":{"Louisville#Medicare Level 2 Claims":{"Name":"Louisville Medicare Level 2 Claims","OwnerId":"'+cs.ownerId+'","Classification_Type__c":"'+clsType.Id+'","CokY__c":false,"Department__c":"Medicare Level 2 Claims","DST__c":false,"General__c":true,"HO_Dental__c":false,"HO_Medical__c":false,"Contract_Protected_Data__c":false,"Work_Queue_Name__c":"Market_Quality_Operations_Compliance"}} }';
        CaseTransferSave_LD_HUM.saveCaseTransfer(caseTransferInput); 
        CaseTransferSave_LD_HUM.CaseTransferWrapper ctw = new CaseTransferSave_LD_HUM.CaseTransferWrapper();
        }
    }
/**
* This method is used to test the condition if Service Center is Louisville & Department is Grievance and Appeals on case in saveTransfer functionality
*
* @param     NULL
* @return    void 
*/
   static testmethod void testSnDonSaveTransfer() {
         User u = [Select id from user limit 1];
        system.runAs(u){
        CRMFunctionality_ONOFF_Switch__c objCSEligibility = new CRMFunctionality_ONOFF_Switch__c(
            Name = '1414643',
            IsON__c = true,
            Release_Name__c = '03/30/2022',
            Scrum_Team__c = 'PR00094919',
            Story_Title__c = 'Case Transfer');
        insert objCSEligibility;
        Case cs = [SELECT id,ownerId,Status,Classification_Type__c,Type,Owner_Queue__c,Priority,Transferred_Date__c,Case_Owner__c,Service_Center__c,Transfer_Reason__c,Department__c,Topic__c  from Case LIMIT 1];
        cs.Service_Center__c = 'Louisville';
        cs.Department__c = 'Grievance and Appeals';
        cs.Type = '';
        Classification_Type__c clsType = [Select id from Classification_Type__c LIMIT 1];
        String caseTransferInput = '{"oCaseRec":'+JSON.serialize(cs)+',"caseComment":"","ownerId":"","ownerName":"Juan Acevedo Jr","mapServCenDeptWorkQueueSetup":{"Louisville#Medicare Level 2 Claims":{"Name":"Louisville Medicare Level 2 Claims","OwnerId":"'+cs.ownerId+'","Classification_Type__c":"'+clsType.Id+'","CokY__c":false,"Department__c":"Medicare Level 2 Claims","DST__c":false,"General__c":true,"HO_Dental__c":false,"HO_Medical__c":false,"Contract_Protected_Data__c":false,"Work_Queue_Name__c":"Market_Quality_Operations_Compliance"}} }';
        CaseTransferSave_LD_HUM.saveCaseTransfer(caseTransferInput); 
        CaseTransferSave_LD_HUM.CaseTransferWrapper ctw = new CaseTransferSave_LD_HUM.CaseTransferWrapper();
        }
    } 
/**
* This method is used to test the exception scenario having no current queue of the loggedin user
*
* @param     NULL
* @return    void 
*/
    static testmethod void testHandleException() {
        try{
            CRMFunctionality_ONOFF_Switch__c objCSEligibility = new CRMFunctionality_ONOFF_Switch__c(
                Name = '1414643',
                IsON__c = true,
                Release_Name__c = '03/30/2022',
                Scrum_Team__c = 'PR00094919',
                Story_Title__c = 'Case Transfer');
            insert objCSEligibility;
            Case cs = [SELECT id,ownerId,Status,Classification_Type__c,Type,Owner_Queue__c,Priority,Transferred_Date__c,Case_Owner__c,Service_Center__c,Transfer_Reason__c,Department__c,Topic__c  from Case LIMIT 1];
            cs.Interacting_With_Type__c = '';
            //update cs;
            Classification_Type__c clsType = [Select id from Classification_Type__c LIMIT 1];
            String caseTransferInput = '{"oCaseRec":'+JSON.serialize(cs)+',"caseComment":"","ownerId":"","ownerName":"Juan Acevedo Jr","mapServCenDeptWorkQueueSetup":{"Louisville#Medicare Level 2 Claims":{"Name":"Louisville Medicare Level 2 Claims","OwnerId":"'+cs.ownerId+'","Classification_Type__c":"'+clsType.Id+'","CokY__c":false,"Department__c":"Medicare Level 2 Claims","DST__c":false,"General__c":true,"HO_Dental__c":false,"HO_Medical__c":false,"Contract_Protected_Data__c":false,"Work_Queue_Name__c":"Market_Quality_Operations_Compliance"}} }';
            CaseTransferSave_LD_HUM.saveCaseTransfer(caseTransferInput); 
            CaseTransferSave_LD_HUM.CaseTransferWrapper ctw = new CaseTransferSave_LD_HUM.CaseTransferWrapper();
        }
        catch(Exception e){}
    }
}