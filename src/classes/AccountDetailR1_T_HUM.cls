/******************************************************************************************************************
Apex class Name    : AccountDetailR1_T_HUM
Version            : 1.0
Function           : Test Class for AccountDetailR1_C_HUM
Created On         : December 13 2016

Modification Log:
*   Version				Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------                
*    1.0  				Vishal Verma			      51411           12/13/2016             Original Version 
* 	 1.1				Sayali Nemade 				                  08/27/2020             REQ - 1170241 MF 3 - Archived CCP Reference Summary Page - High Level  (Overall look) 
*    1.2                Anjani Vaddadi                                03/17/2021             User Story#-1260081 -T1PRJ0003372 - MF 2, MF 3, MF 4 - Veteran Update Functionlity
*    1.3 				Ajay Chakradhar                               05/13/2021             REQ-2288131 - Improve Test Code Coverage
*    1.4                Vardhman Jain                                 30/09/2021             User story 1801212 PR00094010 - MVP MF 6 - Person Account
*    1.5                Vardhman Jain                                 01/10/2021             User story_2615671_Veterans/Update Option in Dropdown	
******************************************************************************************************************/
@isTest
private class  AccountDetailR1_T_HUM 
{
	/**
    * Test Setup method to create all the data required for testing.
    *
    * @param     NULL
    * @return    void 
    */
	 @testSetup	 static void setUpTestData()
	 {
	 	User oCCSUser = HUMTestDataHelperTest.createTestUser('UserCCS1','Customer Care Specialist');
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
            oMemAccount.HRA_HNA_Completed__c='{"HRACompletedDate":"2023-04-12","HRAStatus":"Y","HRAExpiryDate":"2025-04-12"}';
            accList.add(oMemAccount);
            
	        Account oProviderAccount =  HUMTestDataHelperTest.getAccount();
	        oProviderAccount.recordtypeid = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Provider').getRecordTypeId();
	        oProviderAccount.Enterprise_Id__c = '1000102';
	        oProviderAccount.name= 'duck';
	        oProviderAccount.NPI_ID__c='0123456789';
	        oProviderAccount.Individual_First_Name__c='testname';
	        oProviderAccount.Individual_Last_Name__c='firstname';
	        oProviderAccount.Provider_Classification__c='I';
	        oProviderAccount.billingstate='Arizona';
	        oProviderAccount.billingpostalcode='12345';
	        accList.add(oProviderAccount);
	        
	        Account oGroupAccount = HUMTestDataHelperTest.getAccount();
            oGroupAccount.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
            oGroupAccount.Enterprise_Id__c = '1000103';
            oGroupAccount.Name = 'TestGroup00';
            oGroupAccount.Group_Number__c = '00';
            oGroupAccount.BillingState = 'Alabama';
            accList.add(oGroupAccount);
            
            Account oAgencyAccount = HUMTestDataHelperTest.getAccount();
            oAgencyAccount.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Agent/Broker').getRecordTypeId();
            oAgencyAccount.Enterprise_Id__c = '1000104';
            oAgencyAccount.Individual_First_Name__c = 'Agent00';
            oAgencyAccount.Individual_Last_Name__c = 'Agent00';
            oAgencyAccount.Name = 'Agent00' ;
            oAgencyAccount.Agent_Type__c = 'Agency';
            oAgencyAccount.BillingState = 'Alabama';
            oAgencyAccount.Agent_Id__c = '000';
            accList.add(oAgencyAccount);
            
            Account oUnknownMember =new Account();
            oUnknownMember.FirstName='test';
            oUnknownMember.LastName = 'Unknown Member';
            oUnknownMember.Enterprise_Id__c = '1000105';
			//User story_2615671_Veterans/Update Option in Dropdown start
			oUnknownMember.Veteran_Status__c = 'confirmed-veteran';
			//User story_2615671_Veterans/Update Option in Dropdown end
	        oUnknownMember.recordtypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Unknown Member').getRecordTypeId();
	        accList.add(oUnknownMember);
	        
	        Account oUnknownGroupAccount = HUMTestDataHelperTest.getAccount();
            oUnknownGroupAccount.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Unknown Group').getRecordTypeId();
            oUnknownGroupAccount.Enterprise_Id__c = '1000106';
            oUnknownGroupAccount.Name = 'TestGroup01';
            oUnknownGroupAccount.Group_Number__c = '00';
            oUnknownGroupAccount.BillingState = 'Alabama';
            accList.add(oUnknownGroupAccount);
            
            if(accList != NULL) insert accList;
            system.assert(accList.size() == 6);
            
            Account acc = [Select id , recordtype.Name from Account where Enterprise_id__C  = '1000101'];
            system.assert(acc.RecordTYpe.Name == 'Member');
            
            ConsumerID__c oConsumerID = new ConsumerID__c();
            oConsumerID.Account__c = oUnknownMember.id;
            oConsumerID.Consumer_ID__c = '987654321';
            oConsumerID.ID_Type__c = 'TAXID';
            insert oConsumerID;
            
            List<Interaction__c> interactionlst = new list<Interaction__c>();
			for(Integer i = 0; i< 2500; i++)
			{
			    Interaction__c intr = new Interaction__c();
			    intr.Interacting_With__c = acc.Id;
			    intr.Interacting_With_type__c = 'Member';
			    intr.Interaction_Origin__c = 'Web';
			    interactionlst.add(intr);    
			}
			if(interactionlst != NULL) insert interactionlst;
			system.assert(interactionlst.size() == 2500);
			
			livechatvisitor lcvI = new livechatvisitor();
            insert lcVI;
            System.assert(lcVI.Id != null); 
            
			List<LiveChatTranscript> chatTranscriptList = new List<LiveChatTranscript>();
			for(Integer i = 0; i< 2500; i++)
			{
			    LiveChatTranscript chat = new LiveChatTranscript();
			    chat.AccountId = acc.Id;
			    chat.LIVECHATVISITORID=lcVI.Id;
                chat.Secondary_Chat_ID__c=lcVI.Id + '_' + i;
			    chatTranscriptList.add(chat);    
			}
			if(chatTranscriptList != Null)insert chatTranscriptList;
			system.assert(chatTranscriptList.size() == 2500);            
			CRMFunctionality_ONOFF_Switch__c objSwitch1 = new CRMFunctionality_ONOFF_Switch__c();
            objSwitch1.IsON__c = true;
            objSwitch1.Name = '754843';
            insert objSwitch1;
            CRMFunctionality_ONOFF_Switch__c objSwitch2 = new CRMFunctionality_ONOFF_Switch__c();
            objSwitch2.IsON__c = true;
            objSwitch2.Name = '1071924';
            insert objSwitch2;
            CRMFunctionality_ONOFF_Switch__c objSwitch3 = new CRMFunctionality_ONOFF_Switch__c();
            objSwitch3.IsON__c = true;
            objSwitch3.Name = '1441116';
            insert objSwitch3;
            CRMFunctionality_ONOFF_Switch__c objSwitch4 = new CRMFunctionality_ONOFF_Switch__c();
            objSwitch4.IsON__c = true;
            objSwitch4.Name = '2860577';
            insert objSwitch4;
            CRMFunctionality_ONOFF_Switch__c objSwitch5 = new CRMFunctionality_ONOFF_Switch__c();
            objSwitch5.IsON__c = false;
            objSwitch5.Name = '3315370';
            insert objSwitch5;
            CRMFunctionality_ONOFF_Switch__c objSwitch6 = new CRMFunctionality_ONOFF_Switch__c();
            objSwitch6.IsON__c = false;
            objSwitch6.Name = '1041229';
            insert objSwitch6;
            CRMFunctionality_ONOFF_Switch__c objSwitch7 = new CRMFunctionality_ONOFF_Switch__c();
            objSwitch7.IsON__c = false;
            objSwitch7.Name = '2127870';
            insert objSwitch7;
        }
	 }
	 
	/**
    * This method is used to Test Member Records
    *
    * @param     NULL
    * @return    void 
    */
	static testMethod void AccountDetail_Member()
	{
		User testUser = [Select Id,Name from User where LastName='test UserCCS1' Limit 1];
        PermissionSet ps1 = [SELECT Id FROM PermissionSet WHERE Name = 'CRMS_1220_Pharmacy_Demographic_Update'];
        PermissionSetAssignment psa1 = new PermissionSetAssignment();
        psa1.AssigneeId = testUser.Id;
        psa1.PermissionSetId = ps1.Id;
        insert psa1;
        PermissionSet ps2 = [SELECT Id FROM PermissionSet WHERE Name = 'CRMS_1210_Commercial_Demographic_Update'];
        PermissionSetAssignment psa2 = new PermissionSetAssignment();
        psa2.AssigneeId = testUser.Id;
        psa2.PermissionSetId = ps2.Id;
        insert psa2;
        PermissionSet ps3 = [SELECT Id FROM PermissionSet WHERE Name = 'CRMS_1200_MedicareMedicaid_Demographic_Update'];
        PermissionSetAssignment psa3 = new PermissionSetAssignment();
        psa3.AssigneeId = testUser.Id;
        psa3.PermissionSetId = ps3.Id;
        insert psa3;
        system.runAs(testUser)
        {
        	Test.StartTest();
        		Account acc = [Select id , Name, RecordType.Name , RecordTYpeId  ,(Select Id from LiveChatTranscripts), (Select Id from Interactions1__r), Veteran_Status__c, VA_Health_Enrollee__c from Account where Enterprise_id__C  = '1000101'];
				Test.setCurrentPage(page.AccountDetailR1_VF_HUM);
		  		ApexPages.currentPage().getParameters().put('id', acc.id);
				pageRef();
        		AccountDetailR1_C_HUM accDetail = new AccountDetailR1_C_HUM(new ApexPages.StandardController(acc));
	            	String jsonStr = '{"HRACompletedDate":"2023-04-12","HRAStatus":"Y","HRAExpiryDate":"2025-04-12"}';
	            	accDetail.getHRAStatus(jsonStr);
	            	accDetail.assignAgnosticCustomer();
	                accDetail.closePopup();
	                accDetail.saveAndContPopup();
        		system.assert(acc.RecordTYpe.Name == 'Member');
        	Test.StopTest();
        }
	}

	/**
    * This method is used to Test Provider Records
    *
    * @param     NULL
    * @return    void 
    */
	static testMethod void AccountDetail_Provider()
	{
		User testUser = [Select Id,Name from User where LastName='test UserCCS1' Limit 1 ];
        system.runAs(testUser)
        {
        	Test.StartTest();
				pageRef();
        		Account acc = [Select id ,RecordType.Name , RecordTYpeId  ,(Select Id from LiveChatTranscripts), (Select Id from Interactions1__r) from Account where Enterprise_id__C  = '1000102'];
        		AccountDetailR1_C_HUM accDetail = new AccountDetailR1_C_HUM(new ApexPages.StandardController(acc));
        		system.assert(acc.RecordTYpe.Name == 'Provider');
        	Test.StopTest();
        }
	}

	/**
    * This method is used to Test Group Records
    *
    * @param     NULL
    * @return    void 
    */
	static testMethod void AccountDetail_Group()
	{
		User testUser = [Select Id,Name from User where LastName='test UserCCS1' Limit 1];
        system.runAs(testUser)
        {
        	Test.StartTest();
			    pageRef();
        		Account acc = [Select id ,RecordType.Name , RecordTYpeId  ,(Select Id from LiveChatTranscripts), (Select Id from Interactions1__r) from Account where Enterprise_id__C  = '1000103'];
        		AccountDetailR1_C_HUM accDetail = new AccountDetailR1_C_HUM(new ApexPages.StandardController(acc));
        		system.assert(acc.RecordTYpe.Name == 'Group');
                        accDetail.bGroup = true;
                        accDetail.createFloatingList();
                        system.assertEquals(true, accDetail.bGroup);
        	Test.StopTest();
        }
	}

	/**
    * This method is used to Test Agency Records
    *
    * @param     NULL
    * @return    void 
    */
	static testMethod void AccountDetail_Agency()
	{
		User testUser = [Select Id,Name from User where LastName='test UserCCS1' Limit 1];
        system.runAs(testUser)
        {
        	Test.StartTest();
				pageRef();
        		Account acc = [Select id ,RecordType.Name , RecordTYpeId  ,(Select Id from LiveChatTranscripts), (Select Id from Interactions1__r) from Account where Enterprise_id__C  = '1000104'];
        		AccountDetailR1_C_HUM accDetail = new AccountDetailR1_C_HUM(new ApexPages.StandardController(acc));
        		system.assert(acc.RecordTYpe.Name == 'Agent/Broker');
        	Test.StopTest();
        }
	}
	
	/**
    * This method is used to Test Unknown Member Records
    *
    * @param     NULL
    * @return    void 
    */
	static testMethod void AccountDetail_UnknownMember()
	{
		User testUser = [Select Id,Name from User where LastName='test UserCCS1' Limit 1];
        system.runAs(testUser)
        {
        	Test.StartTest();
				pageRef();
        		Account acc = [Select id , Name, RecordType.Name , RecordTYpeId  ,(Select Id from LiveChatTranscripts), (Select Id from Interactions1__r), Veteran_Status__c, VA_Health_Enrollee__c from Account where Enterprise_id__C  = '1000105'];
        		AccountDetailR1_C_HUM accDetail = new AccountDetailR1_C_HUM(new ApexPages.StandardController(acc));
        		system.assert(acc.RecordTYpe.Name == 'Unknown Member');
        	Test.StopTest();
        }
	}
	
	/**
    * This method is used to Test Unknown Group Records
    *
    * @param     NULL
    * @return    void 
    */
	static testMethod void AccountDetail_UnknowGroupr()
	{
		User testUser = [Select Id,Name from User where LastName='test UserCCS1' Limit 1];
        system.runAs(testUser)
        {
        	Test.StartTest();
				pageRef();
        		Account acc = [Select id ,RecordType.Name , RecordTYpeId  ,(Select Id from LiveChatTranscripts), (Select Id from Interactions1__r) from Account where Enterprise_id__C  = '1000106'];
        		AccountDetailR1_C_HUM accDetail = new AccountDetailR1_C_HUM(new ApexPages.StandardController(acc));
        		system.assert(acc.RecordTYpe.Name == 'Unknown Group');
        	Test.StopTest();
        }
	}
    /**
* This method is used to cover Unified search methods Test Member Records
*
* @param     NULL
* @return    void 
*/
    static testMethod void unifiedSearchDetails()
    {
        User testUser = [Select Id,Name from User where LastName='test UserCCS1' Limit 1];
        Id permissionSetId = [SELECT Id FROM PermissionSet WHERE Name = 'CRMS_310_UnifiedDocumentSearch'].Id;
        PermissionSetAssignment psa = new PermissionSetAssignment(PermissionSetId = permissionSetId, AssigneeId = testUser.id);
        insert psa;

        system.runAs(testUser)
        {
            Test.StartTest();
            CRMFunctionality_ONOFF_Switch__c obj = new CRMFunctionality_ONOFF_Switch__c();
            obj.Name = 'Unified Search Switch';
            obj.IsON__c = true;
            insert obj;
			
            Account acc = [Select id ,RecordType.Name , RecordTYpeId  ,(Select Id from LiveChatTranscripts), (Select Id from Interactions1__r),Name,Veteran_Status__c, VA_Health_Enrollee__c from Account where Enterprise_id__C  = '1000101'];
            Interaction__c intr = new Interaction__c();
                intr.Interacting_With__c = acc.Id;
                intr.Interacting_With_type__c = 'Member';
                intr.Interaction_Origin__c = 'Web';
            insert intr;
            Interaction_Member__c intrmem = new Interaction_Member__c();
            intrmem.Interacting_About_Type__c = 'Member';
             intrmem.Interacting_About__c = acc.id;
            intrmem.Interaction__c =intr.id;
            insert intrmem;
            
            Test.setCurrentPage(page.AccountDetailR1_VF_HUM);
            ApexPages.currentPage().getParameters().put('id', acc.id);
            ApexPages.currentPage().getParameters().put('interactionId', intr.id);
            pageRef();
            AccountDetailR1_C_HUM accDetail = new AccountDetailR1_C_HUM(new ApexPages.StandardController(acc));
            system.assert(acc.RecordTYpe.Name == 'Member');
            Test.StopTest();
        }
    } 
	 static void pageRef(){
        PageReference pageref = Page.AccountDetailR1_VF_HUM;    
             pageref.getHeaders().put('USER-AGENT','chrome');
             Test.setCurrentPage(pageref);
    }
}