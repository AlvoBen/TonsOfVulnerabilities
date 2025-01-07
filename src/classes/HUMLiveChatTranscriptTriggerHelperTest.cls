/*******************************************************************************************************************************
Apex Class Name :    HUMLiveChatTranscriptTriggerHelperTest 
Version: 1.0
Created On      : 09/17/2014
Function        :    1. This class serves as testclass for 
					 a.HUMLiveChatTranscriptTriggerHelper.
                     b.HUMLiveChatTranscriptTrigge
        c.HUMChatTranscriptTrigger
        d.HUMChatTranscriptTriggerHelper
        Modification Log:    
 * Modification ID	Developer Name          Code Review               Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------
 * 1.0				Ninad Patil             18240                   09/26/2014                 Original Version
 * 1.1				Ninad Patil				18240					09/29/2014				   Fixed Internal code reveiw comment.
 * 1.2				Ninad Patil				18771					10/24/2014				   Error handleing code Added. 
 * 1.3				Sagar Tapadia			20293					01/12/2015				   Updated test class as per latest changes.
 * 1.4				Sagar Tapadia			22921					05/07/2015				   Changes for User Story:186099 - Remove Email Sending functionality on transcript error.
 * 1.5 				Sagar Tapadia			23562					05/28/2015				   CA5027355:Changes done to test class as after trigger methods were removed.
 * 1.6				Pradeepkumar Dani		34627 					08/04/2016			 	   Updated class with APEX standards - REQ - 270091
 * 1.7				Avinash Choubey      					        02/21/2017               REQ - 301383  Uncommented Version
 * 1.8              Rajesh Chandgothia	    140400		            10/18/2017			      REQ - 332357 increase Test Coverage
 * 1.8              Rajesh Chandgothia	    152418		            11/29/2017			      REQ - 333809 Associate Interaction to New Case
 * 1.9 				Joel George										12/12/2019				   Humana Unsecure Chat Changes
 * 2.0				Joel George										09/07/2020					Test class coverage
 * 2.1              Shivani Shrivastav                              08/24/2023                  Test Class coverage for Asynch Chat Url Update
 ****************************************************************************************************************************/
@isTest private class HUMLiveChatTranscriptTriggerHelperTest 
{
    /**
    * setUpTestData Is testSetup method.
    * <p>
    * This method creates test data for test class
    *
    * @return void
    */
    @testSetup static void setUpTestData()
    {
        User testUser = HUMTestDataHelperTest.createTestUser('Customer_Case_specialist_User', 'Customer Care Specialist');
        testUser.Current_Queue__c = 'Louisville RSO Enrollment';
        insert testUser;
        System.assertEquals(testUser.lastname == 'test Customer_Case_specialist_User', true);
        // RESTRICTED_USER_ID value Is org specific hence it Is Not consdered as GLOBAL_CONTSANTS
        HUMConstants__c hCons2 = New HUMConstants__c(Name ='RESTRICTED_USER_ID',StringValue__c=String.valueOf(testUser.Id));
        insert hCons2;
        System.assert(HUMConstants__c.getValues('RESTRICTED_USER_ID') != NULL);
        System.runAs(testUser)
        {
           Test.startTest();
            List<Account> lstAccount = new List<Account>();
            List<Case> lstCase = new List<Case>();
            Set <Id> setAcctId = new Set <Id> ();
            delete hCons2;
            //Create Account records for testing.
            for(Integer i=0;i<5;i++)
            {
                Account oAcc = HUMTestDataHelperTest.getAccount();
                oAcc.name = 'AccTest'+i;
                oAcc.enterprise_id__C = '1037402785';
                oAcc.OwnerId = testUser.Id;
                setAcctId.add(testUser.Id);
                lstAccount.add(oAcc);
            }
            insert lstAccount;
            System.assertEquals('1037402785', string.valueOf([SELECT Enterprise_Id__c FROM Account WHERE Id=:lstAccount[0].Id][0].Enterprise_Id__c));
            // Create Case record so as to fetch security indicators.
            For (Integer i= 0;i<5;i++)
            {
                Case oCase = HUMTestDataHelperTest.getCase();
                oCase.AccountId = lstAccount[i].Id;
                oCase.CoKY_Case__c = True;
                oCase.General_Case__c = True;
                oCase.DST_Case__c = True;
                oCase.HO_Medical_Case__c = True;
                oCase.OwnerId = testUser.Id;
                lstCase.add(oCase);
            }
            insert lstCase;
            System.assertEquals(testUser.Id, [SELECT OwnerId FROM Case WHERE Id=:lstCase[0].Id][0].OwnerId);
            //Creating Live chat visitor record for simulating actual chat. 
            Livechatvisitor oLVisitor = New Livechatvisitor();
            insert oLVisitor;
            System.assert(oLVisitor.Id <> null);
            List<LiveChatTranscript> lstLiveChatTranscript = new List<LiveChatTranscript>();
            //For testing trigger bulkification
            for(Integer iInt =0; iInt<5; iInt++)
            {
                LiveChatTranscript oLiveChatTranscript = new LiveChatTranscript();
                oLiveChatTranscript.AccountId__c = lstAccount[iInt].ID;
                oLiveChatTranscript.LiveChatVisitorId = oLVisitor.id;
                oliveChatTranscript.ChatKey = 'Test'+iInt+'_example';
                oliveChatTranscript.CaseId = lstCase[iInt].Id;
                lstLiveChatTranscript.add(oLiveChatTranscript);
            }       
            insert lstLiveChatTranscript;
            System.assertEquals(oLVisitor.id, [SELECT LiveChatVisitorId FROM LiveChatTranscript WHERE Id=:lstLiveChatTranscript[0].Id][0].LiveChatVisitorId);
            List<Humana_Chat_Transcript__c> lstCustomChatTranscript = new List<Humana_Chat_Transcript__c>();        
            for(Integer iInt = 0;iInt<5;iInt++)
            {
                Humana_Chat_Transcript__c oCustomChat = new Humana_Chat_Transcript__c();
                oCustomChat.Chat_Key__c = 'Test'+iInt+'_example';
                lstCustomChatTranscript.add(oCustomChat);
            }       
            insert lstCustomChatTranscript;
            System.assertNotEquals(lstCustomChatTranscript[0].id, null);
            Test.stopTest();
        }
    }
    
    /*
    * testGetLiveChatTrasncript Method for perfomring the tests on trigger actions.
    * <p>
    * This method tests insertion of Standard Trasncript object.
    *
    * @return void
    */
    Static testMethod void testGetLiveChatTrasncript() 
    {
        User objUsr = [SELECT Id, Name FROM User WHERE LastName='test Customer_Case_specialist_User'];
            System.runAs(objUsr)
        {
            Test.startTest();
            //Trigger point
            LiveChatTranscript lChat =[SELECT Id, CaseId FROM LiveChatTranscript WHERE ChatKey='Test0_example' LIMIT 1];
            //Asserting that live chat transcript record was created.
            System.assert(lChat.Id <> null);
            Test.stopTest();
        }
    }
    
     Static testMethod void testGetLiveChatTrasncriptException() 
    {
        User testUser = HUMTestDataHelperTest.createTestUser('Customer_Case_specialist_Users', 'Customer Care Specialist');
        insert testUser;
        System.assertEquals(testUser.lastname == 'test Customer_Case_specialist_Users', true);
        // RESTRICTED_USER_ID value Is org specific hence it Is Not consdered as GLOBAL_CONTSANTS
        HUMConstants__c hCons2 = New HUMConstants__c(Name ='RESTRICTED_USER_ID',StringValue__c=String.valueOf(testUser.Id));
        insert hCons2;
        System.assert(HUMConstants__c.getValues('RESTRICTED_USER_ID') != NULL);
        System.runAs(testUser)
        {
           Test.startTest();
            List<Account> lstAccount = new List<Account>();
            List<Case> lstCase = new List<Case>();
            Set <Id> setAcctId = new Set <Id> ();
            for(Integer i=0;i<10;i++)
            {
                Account oAccount = HUMTestDataHelperTest.getAccount();
                oAccount.name = 'AccTest'+i;
                oAccount.enterprise_id__C = '1037402785';
                oAccount.OwnerId = testUser.Id;
                
                lstAccount.add(oAccount);
            }
            insert lstAccount;
            System.assertEquals('1037402785', string.valueOf([SELECT Enterprise_Id__c FROM Account WHERE Id=:lstAccount[0].Id][0].Enterprise_Id__c));
            // Create Case record so as to fetch security indicators.
            For (Integer i= 0;i<10;i++)
            {
                Case oCase = HUMTestDataHelperTest.getCase();
                oCase.AccountId = lstAccount[i].Id;
                oCase.CoKY_Case__c = True;
                oCase.General_Case__c = True;
                oCase.DST_Case__c = True;
                oCase.HO_Medical_Case__c = True;
                oCase.OwnerId = testUser.Id;
                lstCase.add(oCase);
            }
            insert lstCase;
            System.assertEquals(testUser.Id, [SELECT OwnerId FROM Case WHERE Id=:lstCase[0].Id][0].OwnerId);
            //Creating Live chat visitor record for simulating actual chat. 
            Livechatvisitor oLVisitor = New Livechatvisitor();
            insert oLVisitor;
            System.assert(oLVisitor.Id <> null);
            List<LiveChatTranscript> lstLiveChatTranscript = new List<LiveChatTranscript>();
            //For testing trigger bulkification
            for(Integer iInt =0; iInt<10; iInt++)
            {
                LiveChatTranscript oLiveChatTranscript = new LiveChatTranscript();
                oLiveChatTranscript.AccountId__c = lstAccount[iInt].ID;
                oLiveChatTranscript.LiveChatVisitorId = oLVisitor.id;
                oliveChatTranscript.ChatKey = 'Test'+iInt+'example2';
                oliveChatTranscript.CaseId = lstCase[iInt].Id;
                setAcctId.add(lstAccount[iInt].ID);
                lstLiveChatTranscript.add(oLiveChatTranscript);
            }       
            insert lstLiveChatTranscript;
            System.assertEquals(oLVisitor.id, [SELECT LiveChatVisitorId FROM LiveChatTranscript WHERE Id=:lstLiveChatTranscript[0].Id][0].LiveChatVisitorId);
            List<Humana_Chat_Transcript__c> lstCustomChatTranscript = new List<Humana_Chat_Transcript__c>();        
            for(Integer iInt = 0;iInt<10;iInt++)
            {
                Humana_Chat_Transcript__c oCustomChat = new Humana_Chat_Transcript__c();
                oCustomChat.Chat_Key__c = 'Test'+iInt+'_example';
                lstCustomChatTranscript.add(oCustomChat);
            }       
            insert lstCustomChatTranscript;
            System.assertNotEquals(lstCustomChatTranscript[0].id, null);
            Test.stopTest();
        }
    }
	 /*
    * testassociateCaseinteractionToTranscript Method for perfomring the tests on trigger actions.
    * <p>
    * This method tests The Interaction Number is displayed in the Interaction section on the Case page
    *
    * @return void
    */
    Static testMethod void testassociateCaseToInteraction() 
    {
        User testUser = HUMTestDataHelperTest.createTestUser('LiveChatTranscriptUser', 'Customer Care Specialist');
        insert testUser;
        System.assertEquals(testUser.lastname == 'test LiveChatTranscriptUser', true);
        System.runAs(testUser)
        {
           Test.startTest();
            List<Account> lstAccount = new List<Account>();
            List<Case> lstCase = new List<Case>();
            Set <Id> setAcctId = new Set <Id> ();
            //Create Account records for testing
           Id devRecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            for(Integer i=0;i<5;i++)
            {
                Account oAcc = HUMTestDataHelperTest.getAccount();
                oAcc.FirstName = 'AccTest'+i;
                oAcc.LastName ='Account';
                oAcc.enterprise_id__C = String.valueof(2000);
                oAcc.OwnerId = testUser.Id;
                oAcc.recordtypeId = devRecordTypeId;
                setAcctId.add(testUser.Id);
                lstAccount.add(oAcc);
            }
            insert lstAccount;
            System.assertEquals(String.valueof(2000), String.valueOf([SELECT Enterprise_Id__c FROM Account WHERE Id=:lstAccount[0].Id][0].Enterprise_Id__c));
            // Create Case record so as to fetch security indicators.
            For (Integer i= 0;i<5;i++)
            {
                Case oCase = HUMTestDataHelperTest.getCase();
                oCase.AccountId = lstAccount[i].Id;
                oCase.CoKY_Case__c = True;
                oCase.General_Case__c = True;
                oCase.DST_Case__c = True;
                oCase.HO_Medical_Case__c = True;
                oCase.OwnerId = testUser.Id;
                lstCase.add(oCase);
            }
            insert lstCase;
            System.assertEquals(testUser.Id, [SELECT OwnerId FROM Case WHERE Id=:lstCase[0].Id][0].OwnerId);
            //Creating Live chat visitor record for simulating actual chat. 
            Livechatvisitor oLVisitor = New Livechatvisitor();
            insert oLVisitor;
            System.assert(oLVisitor.Id <> null);
			//Creating Interaction record for Account type Member
            Interaction__c objInteraction = new Interaction__c(); 
            objInteraction.Interacting_With__c = lstAccount[0].ID;
            objInteraction.Interacting_With_type__c = 'Member';
            objInteraction.Interaction_Origin__c = 'Web Chat';
            insert objInteraction ; 
            System.assert(objInteraction.Id <> null);
            List<LiveChatTranscript> lstLiveChatTranscript = new List<LiveChatTranscript>();
            //For testing trigger bulkification
            for(Integer iInt =0; iInt<5; iInt++)
            {
                LiveChatTranscript oLiveChatTranscript = new LiveChatTranscript();
                oLiveChatTranscript.AccountId__c = lstAccount[iInt].ID;
                oLiveChatTranscript.LiveChatVisitorId = oLVisitor.id;
                oliveChatTranscript.ChatKey = 'Test'+iInt+'example1';
                oliveChatTranscript.CaseId = lstCase[iInt].Id;
                oliveChatTranscript.Interaction__c= objInteraction.id;
                lstLiveChatTranscript.add(oLiveChatTranscript);
            }  
            insert lstLiveChatTranscript;
            System.assertEquals(oLVisitor.id, [SELECT LiveChatVisitorId FROM LiveChatTranscript WHERE Id=:lstLiveChatTranscript[0].Id][0].LiveChatVisitorId);
            List<Humana_Chat_Transcript__c> lstCustomChatTranscript = new List<Humana_Chat_Transcript__c>();        
            for(Integer iInt = 0;iInt<5;iInt++)
            {
                Humana_Chat_Transcript__c oCustomChat = new Humana_Chat_Transcript__c();
                oCustomChat.Chat_Key__c = 'Test'+iInt+'_example';
                lstCustomChatTranscript.add(oCustomChat);
            }       
            insert lstCustomChatTranscript;
            System.assertNotEquals(lstCustomChatTranscript[0].id,null);
            List<Case_Interaction__c> lstCaseIInteractions = new List<Case_Interaction__c>();
            for (LiveChatTranscript lstCaseIInteraction: lstLiveChatTranscript) 
            {
                Case_Interaction__c objCaseInteraction = new Case_Interaction__c();
                if(lstCaseIInteraction.CaseId != Null)
                    objCaseInteraction.Case__c = lstCaseIInteraction.CaseId;
                 if(lstCaseIInteraction.Interaction__c != Null)    
                    objCaseInteraction.Interaction__c = lstCaseIInteraction.Interaction__c;
                 lstCaseIInteractions.add(objCaseInteraction);
            }
            Test.stopTest();
        }
    }
    
    Static testMethod void testBeforeUpdateScenario() 
    {
        List<User> lstUser = new List<User>();
        
        User testUser = HUMTestDataHelperTest.createTestUser('LiveChatTranscriptUser', 'Customer Care Specialist');
        testUser.Current_Queue__c = 'testQueue';
        
        User testUser2 = HUMTestDataHelperTest.createTestUser('LiveChatTranscriptUser2', 'Customer Care Specialist');
        testUser2.Current_Queue__c = 'testQueue';
        
        lstUser.add(testUser);
        lstUser.add(testUser2);
        insert lstUser;
        
        System.assertEquals(testUser.lastname == 'test LiveChatTranscriptUser', true);
        System.runAs(testUser)
        {
           Test.startTest();
           
           Work_Queue_Setup__c wQueue =  new Work_Queue_Setup__c();
           wQueue.Name='testQueue';
           wQueue.IsActive__c = true;
           wQueue.Department__c = 'test';
           wQueue.Service_Center__c = 'test';
           insert wQueue;
           
            List<Account> lstAccount = new List<Account>();
            List<Case> lstCase = new List<Case>();
            Set <Id> setAcctId = new Set <Id> ();
            //Create Account records for testing
            Id devRecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            for(Integer i=0;i<5;i++)
            {
                Account oAcc = HUMTestDataHelperTest.getAccount();
                oAcc.FirstName = 'AccTest'+i;
                oAcc.LastName ='Account';
                oAcc.enterprise_id__C = String.valueof(2000);
                oAcc.OwnerId = testUser.Id;
                oAcc.recordtypeId = devRecordTypeId;
                setAcctId.add(testUser.Id);
                lstAccount.add(oAcc);
            }
            insert lstAccount;
            System.assertEquals(String.valueof(2000), String.valueOf([SELECT Enterprise_Id__c FROM Account WHERE Id=:lstAccount[0].Id][0].Enterprise_Id__c));
            // Create Case record so as to fetch security indicators.

                Case oCase = HUMTestDataHelperTest.getCase();
                oCase.AccountId = lstAccount[0].Id;
                oCase.CoKY_Case__c = True;
                oCase.General_Case__c = True;
                oCase.DST_Case__c = True;
                oCase.HO_Medical_Case__c = True;
                oCase.OwnerId = testUser.Id;


            insert oCase;
            System.assertEquals(testUser.Id, [SELECT OwnerId FROM Case WHERE Id=:oCase.Id][0].OwnerId);
            //Creating Live chat visitor record for simulating actual chat. 
            Livechatvisitor oLVisitor = New Livechatvisitor();
            insert oLVisitor;
            System.assert(oLVisitor.Id <> null);
            //Creating Interaction record for Account type Member
            Interaction__c objInteraction = new Interaction__c(); 
            objInteraction.Interacting_With__c = lstAccount[0].ID;
            objInteraction.Interacting_With_type__c = 'Member';
            objInteraction.Interaction_Origin__c = 'Web Chat';
            insert objInteraction ; 
            System.assert(objInteraction.Id <> null);
            

            Case_Interaction__c objCaseInteraction = new Case_Interaction__c();

            objCaseInteraction.Case__c = oCase.Id;  
            objCaseInteraction.Interaction__c = objInteraction.Id;
            insert objCaseInteraction;
            
            
            List<LiveChatTranscript> lstLiveChatTranscript = new List<LiveChatTranscript>();
            List<LiveChatTranscript> lstLiveChatTranscriptToUpdate = new List<LiveChatTranscript>();
            LiveChatTranscript oLiveChatTranscript = new LiveChatTranscript();
            for(Integer iInt =0; iInt<5; iInt++)
            {
                oLiveChatTranscript = new LiveChatTranscript();
                oLiveChatTranscript.LiveChatVisitorId = oLVisitor.id;
                oliveChatTranscript.ChatKey = 'Test'+iInt+'example3';
                oliveChatTranscript.Interaction__c = objInteraction.id;     
                oliveChatTranscript.caseid = oCase.Id;
                oliveChatTranscript.Status =  'InProgress';
                lstLiveChatTranscript.add(oLiveChatTranscript);
            }
            
            if(!lstLiveChatTranscript.IsEmpty())
            insert lstLiveChatTranscript;
        
            for(LiveChatTranscript LCT : lstLiveChatTranscript)
            { 
                LCT.chat_type__c = 'Humana Secure';                           
            }
            oCase.OwnerId = testUser2.Id;
            update oCase;
            
            
            if(!lstLiveChatTranscript.IsEmpty())
            update lstLiveChatTranscript;
        
            System.assertNotEquals(lstLiveChatTranscript[0].Interaction__c,null);
          

            Test.stopTest();
        }
    }
    
    Static testMethod void testBeforeUpdateScenario2() 
    {
        User testUser = HUMTestDataHelperTest.createTestUser('LiveChatTranscriptUser', 'Customer Care Specialist');
        insert testUser;
        System.assertEquals(testUser.lastname == 'test LiveChatTranscriptUser', true);
        System.runAs(testUser)
        {
           Test.startTest();
            List<Account> lstAccount = new List<Account>();
            List<Case> lstCase = new List<Case>();
            Set <Id> setAcctId = new Set <Id> ();
            //Create Account records for testing
           Id devRecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            for(Integer i=0;i<5;i++)
            {
                Account oAcc = HUMTestDataHelperTest.getAccount();
                oAcc.FirstName = 'AccTest'+i;
                oAcc.LastName ='Account';
                oAcc.enterprise_id__C = String.valueof(2000);
                oAcc.OwnerId = testUser.Id;
                oAcc.recordtypeId = devRecordTypeId;
                setAcctId.add(testUser.Id);
                lstAccount.add(oAcc);
            }
            insert lstAccount;
            System.assertEquals(String.valueof(2000), String.valueOf([SELECT Enterprise_Id__c FROM Account WHERE Id=:lstAccount[0].Id][0].Enterprise_Id__c));
            // Create Case record so as to fetch security indicators.
            For (Integer i= 0;i<5;i++)
            {
                Case oCase = HUMTestDataHelperTest.getCase();
                oCase.AccountId = lstAccount[i].Id;
                oCase.CoKY_Case__c = True;
                oCase.General_Case__c = True;
                oCase.DST_Case__c = True;
                oCase.HO_Medical_Case__c = True;
                oCase.OwnerId = testUser.Id;
                lstCase.add(oCase);
            }
            insert lstCase;
            System.assertEquals(testUser.Id, [SELECT OwnerId FROM Case WHERE Id=:lstCase[0].Id][0].OwnerId);
            //Creating Live chat visitor record for simulating actual chat. 
            Livechatvisitor oLVisitor = New Livechatvisitor();
            insert oLVisitor;
            System.assert(oLVisitor.Id <> null);
            //Creating Interaction record for Account type Member
            Interaction__c objInteraction = new Interaction__c(); 
            objInteraction.Interacting_With__c = lstAccount[0].ID;
            objInteraction.Interacting_With_type__c = 'Member';
            objInteraction.Interaction_Origin__c = 'Web Chat';
            insert objInteraction ; 
            System.assert(objInteraction.Id <> null);
            

            Case_Interaction__c objCaseInteraction = new Case_Interaction__c();

            objCaseInteraction.Case__c = lstCase[0].Id;  
            objCaseInteraction.Interaction__c = objInteraction.Id;
            insert objCaseInteraction;
            
            
            List<LiveChatTranscript> lstLiveChatTranscript = new List<LiveChatTranscript>();
            List<LiveChatTranscript> lstLiveChatTranscriptToUpdate = new List<LiveChatTranscript>();
            LiveChatTranscript oLiveChatTranscript = new LiveChatTranscript();
            for(Integer iInt =0; iInt<5; iInt++)
            {
				oLiveChatTranscript = new LiveChatTranscript();
				oLiveChatTranscript.LiveChatVisitorId = oLVisitor.id;
				oliveChatTranscript.ChatKey = 'Test'+iInt+'_example';
                oliveChatTranscript.Status =  'InProgress';
				lstLiveChatTranscript.add(oLiveChatTranscript);
			}
			
			if(!lstLiveChatTranscript.IsEmpty())
			insert lstLiveChatTranscript;
		
			for(LiveChatTranscript LCT : lstLiveChatTranscript)
			{
                oliveChatTranscript.caseid = lstCase[0].Id;
                oliveChatTranscript.Interaction__c = objInteraction.id;   
                LCT.chat_type__c = 'Humana Secure';             
                
            }
            
            if(!lstLiveChatTranscript.IsEmpty())
            update lstLiveChatTranscript;
        
            

            for(LiveChatTranscript LCT : lstLiveChatTranscript)
            {
                LCT.CaseId = null; 
                
            }
            
            if(!lstLiveChatTranscript.IsEmpty())
            update lstLiveChatTranscript;
            System.assertEquals(lstLiveChatTranscript[0].CaseId,null);

            Test.stopTest();
        }
    }
    //Test Class for Async Page Marker Url Update in CRM
    static testMethod void testUpdateURLForAsycnChat() 
    {
        Test.startTest();
        //set up custom settings
        Chat_Transcript_Formatting__c chatTranscriptcustomSettings = new Chat_Transcript_Formatting__c();
        chatTranscriptcustomSettings.name    = 'Formatting_RegEx_Pattern';        
        chatTranscriptcustomSettings.URL_Update__c = 'MARKER_URL:(.*?<br>)';
        insert chatTranscriptcustomSettings;
        insert new CRMFunctionality_ONOFF_Switch__c(Name = '4609720',IsON__c=true,Release_Name__c='08/18/2023',Scrum_Team__c='Dragons',Story_Title__c='Async Page Marker Url displays in CRM');

        //create test data  
        Livechatvisitor oLVisitor = New Livechatvisitor();
        insert oLVisitor;
        System.assert(oLVisitor.Id <> null);
        LiveChatTranscript ChatTranscript = new LiveChatTranscript();
        chatTranscript.chat_type__c = GLOBAL_CONSTANT_HUM.ASYNC_WEB_CHAT;
        chatTranscript.Body = 'MARKER_URL: https://impl.digital.nuance.com/humana-sandbox/index.html?test=default<br>';
        chatTranscript.LiveChatVisitorId = oLVisitor.id;
        insert ChatTranscript;
    
        //Read the updated formated content for the evaluation
        LiveChatTranscript liveChatObj = [SELECT URL__c FROM LiveChatTranscript WHERE Id=:ChatTranscript.Id];
        System.assertEquals('https://impl.digital.nuance.com/humana-sandbox/index.html?test=default',liveChatObj.URL__c); 
        Test.stopTest();

    }
    
}