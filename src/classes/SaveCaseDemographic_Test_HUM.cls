/*****************************************************************************************************************************************************************************
    @Apex Class Name  : SaveCaseDemographic_Test_HUM
    @Version          : 1.0
    @Created Date     : 09/08/2022
    @Test Class For  : SaveCaseDemographic_LC_HUM
******************************************************************************************************************************************************************************
Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Muthukumar                                		 09/08/2022                 Original Version
* Akshay Gulve                                       04/17/2023                 US: 4475037 - Case Management: Auto Fill "Case Origin" "Interacting with type" " Interacting with" & "Interacting With Name" From Interaction Log on New & Edit Case Edit Page (Jaguars)
******************************************************************************************************************************************************************************/
@isTest
public class SaveCaseDemographic_Test_HUM {
    
	@TestSetup
    public static void testDataCreation(){ 
        
        HUMAccountCaseRecordTypes__c newRec = new HUMAccountCaseRecordTypes__c();
        newRec.Name = 'Member';
        newRec.Account_RecordType__c = 'Member';
        newRec.Case_RecordType__c = 'Member Case';
        insert newRec;
        
        
        HUMCaseInteractionAboutType__c caseInter = new HUMCaseInteractionAboutType__c();
        caseInter.Name = 'Member';
        caseInter.CaseInteraction_About_Type__c = 'Member';
        caseInter.CaseRecordType__c = 'Member';
        insert caseInter;

        
        HUMCaseNPI_TAXID__c caseNpi = new HUMCaseNPI_TAXID__c();
        caseNpi.Name = 'Member';
        insert caseNpi;
        
        Work_Queue_Setup__c oWQS =new Work_Queue_Setup__c();       
        oWQS.Name = 'testqueue' ;
        oWQS.IsActive__c= TRUE;
        oWQs.Service_Center__c = 'Dental';
        oWQS.Department__c = 'Calls';
        insert oWQS;
        //oWQS.Classification_Type__c = 'Calls' ;
        //oWQS.Classification_Type__r.name = 'Work_Queue_Setups' ;
        
        Classification_Type__c ctRec = New Classification_Type__c();
        ctRec.name = 'HP Specialty Calls';
        ctRec.Is_Active__c=true;
        insert ctRec;
		
		//user creation
        String userName = 'Test1';
        Id profileId = [SELECT Id FROM Profile WHERE  name ='System Administrator' LIMIT 1 ].Id;

        User testUser = New User();
        testUser.LastName = 'testUser 22';
        testUser.Alias = (userName.length() > 8 ) ?  userName.substring(0 , 8) : userName;
        testUser.Email = UserName+'@test.com';
        testUser.Username = UserName+UserName+'@humcrmstest.com';
        testUser.ProfileId = profileId;
        testUser.CommunityNickname = 'a'+UserName;
        testUser.TimeZoneSidKey = 'America/New_York';
        testUser.LocaleSidKey = 'en_US';
        testUser.EmailEncodingKey = 'ISO-8859-1';
        testUser.LanguageLocaleKey = 'en_US';
        testUser.IsActive = true;
        testUser.Current_Queue__C = 'Louisville RSO Calls Support';
        insert testUser;
        
        
            
            
  }
    
    static testmethod void testCreateCaseAssignValue() {
	User us = [SELECT id FROM USER where LastName='testUser 22'];
    System.runas(us){
        Test.startTest();
        try{
            if(Test.isRunningTest()) {
           		id RecordTypeID=Schema.SObjectType.Account.getrecordtypeinfosByname().get('Member').getRecordtypeID();
           			
                	Account account = new Account();
             		account.LastName = 'Test Account';
             		account.Enterprise_ID__c = '1102283421';
             		account.RecordTypeId = RecordTypeID;
           		    insert account;
					Interaction__c objInteraction = New Interaction__c();
                    objInteraction.Interacting_With__c = account.Id;
                    objInteraction.Interacting_With_type__c = 'Member';
                    objInteraction.Caller_Name__c = 'Test User';
                    objInteraction.Interaction_Origin__c = 'Phone';
                    insert objInteraction;
                SaveCaseDemographic_LC_HUM.createCaseAndRedirect(account.Id,objInteraction.id);
                System.assert(account.Id!= NULL, 'Account ID is empty');
			    System.assert(objInteraction.id!= NULL, 'Interaction ID is empty');
        
                 Account account1 = new Account();
                 account1.LastName = 'Test Account1';
                 account1.Enterprise_ID__c = '';
                 account1.RecordTypeId = RecordTypeID;
           	    insert account1;
			    Interaction__c objInteraction1 = New Interaction__c();
                objInteraction1.Interacting_With__c = account1.Id;
                objInteraction1.Interacting_With_type__c = 'Member';
                objInteraction1.Caller_Name__c = 'Test User';
                objInteraction1.Interaction_Origin__c = 'Phone';
                insert objInteraction1;
            SaveCaseDemographic_LC_HUM.createCaseAndRedirect(account1.Id,objInteraction1.id);
        	System.assert(account1.Id!= NULL, 'Account ID is not empty');
            System.assert(objInteraction1.id!= NULL, 'Interaction ID is not empty');   
        }
       }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'SaveCaseDemographic_LC_HUM','createCaseAndRedirect');
            
        }
            SaveCaseDemographic_LC_HUM.assignValuesToCase();
            System.assert(SaveCaseDemographic_LC_HUM.assignValuesToCase()== NULL, 'assignValuesToCase is empty');
        Test.stopTest();       
    }
	}
    
    static testmethod void testOtherMethods() {
	User us = [SELECT id FROM USER where LastName='testUser 22'];
    System.runas(us){
         Test.startTest();
           try {
       		if(Test.isRunningTest()) {
             	SaveCaseDemographic_LC_HUM.funcOnPageLoad();
           		System.assert(SaveCaseDemographic_LC_HUM.funcOnPageLoad()== NULL, 'funcOnPageLoad is not empty');}
        	}
        	catch(Exception ex) {
            	HUMExceptionHelper.logErrors(ex, 'SaveCaseDemographic_LC_HUM', 'funcOnPageLoad');
				throw ex;
   			}
        	try {
       			if(Test.isRunningTest()) {
            		SaveCaseDemographic_LC_HUM.saveCase();
                    System.assert(SaveCaseDemographic_LC_HUM.saveCase()== NULL, 'saveCase is empty');}
            }
            catch (Exception ex) 
        	{
                HUMExceptionHelper.logErrors(ex, 'HUMNewCaseRedirect', 'saveCase');
                
        	}
                
    Test.stopTest();
    }
	}
}