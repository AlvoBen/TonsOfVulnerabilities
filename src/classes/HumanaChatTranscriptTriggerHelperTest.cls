/*******************************************************************************************************************************
Apex Trigger Name : HumanaChatTranscriptTrigger 
Version           : 1.0
Created On        : 06/01/2023
Function          : 1.HumanaChatTranscriptTrigger
                    2.HumanaChatTranscriptTriggerHelper                
Modification Log: 
* Modification ID           Developer Name           Code Review                 Date                       Description
*---------------------------------------------------------------------------------------------------------------------------
* 1.0                       Sivaprakash Rajendran                                06/01/2023                 US4595853 - Original Version
* 1.1						Alvaro Madrid										 09/04/2023					US4609720 - US4609720_Async_Web_Chat_changes Fix
*******************************************************************************************************************************/
@isTest private class HumanaChatTranscriptTriggerHelperTest {

    public static String chatTranscript = 'CLIENT_MESSAGE: Testing from Brandon<br>AGENT_MESSAGE: testing<br>CLICKSTREAM: LastName: MORALE<br>CLICKSTREAM: FirstName: KANGHOON<br>AGENT_MESSAGE: test 1<br> AGENT_MESSAGE: test 2<br>CLIENT_MESSAGE: Customer message.<br>';
    public static String formattedContentInsert = '<b>KANGHOON MORALE: Testing from Brandon<br></b><b>test LiveAgent: testing<br></b>CLICKSTREAM: LastName: MORALE<br>CLICKSTREAM: FirstName: KANGHOON<br><b>test LiveAgent: test 1<br></b> <b>test LiveAgent: test 2<br></b><b>KANGHOON MORALE: Customer message.<br></b>';
    public static String formattedContentUpdate = '<b>KANGHOON MORALE: Testing from Brandon<br></b><b>test LiveAgent: testing<br></b>CLICKSTREAM: LastName: MORALE<br>CLICKSTREAM: FirstName: KANGHOON<br><b>test LiveAgent: test 1<br></b> <b>test LiveAgent: test 2<br></b><b>KANGHOON MORALE: Customer message.<br></b><b>test LiveAgent: Thank you<br></b>';
    
    @testsetup
    static void testDataSetup()
    {
        CRMFunctionality_ONOFF_Switch__c objFunctionalitySwitch = new CRMFunctionality_ONOFF_Switch__c();
        objFunctionalitySwitch.IsON__c = true;
        objFunctionalitySwitch.Name = '4595853';
        insert objFunctionalitySwitch;

        Chat_Transcript_Formatting__c chatTranscriptcustomSettings = new Chat_Transcript_Formatting__c();
        chatTranscriptcustomSettings.name    = 'Formatting_RegEx_Pattern';
        String defaultValueofChatContent = Chat_Transcript_Formatting__c.Chat_Content__c.getDescribe().getDefaultValueFormula();
        String defaultValueofNameUpdate = Chat_Transcript_Formatting__c.Name_Update__c.getDescribe().getDefaultValueFormula();
        chatTranscriptcustomSettings.Chat_Content__c = defaultValueofChatContent.substringAfter('\'').substringBefore('\'');
        chatTranscriptcustomSettings.Name_Update__c =  defaultValueofNameUpdate.substringAfter('\'').substringBefore('\'');
        insert chatTranscriptcustomSettings;
    }

    static testMethod void HumanaChatTranscriptSkipTriggerTest() {
        Test.startTest();
        HumanaChatTranscriptTriggerHelper.byPassTrigger = true;
        Humana_Chat_Transcript__c oCustomChat = new Humana_Chat_Transcript__c();
        oCustomChat.Body__c = 'testing';
        insert oCustomChat;
        Test.stopTest();
    }

    static testMethod void TranscriptContentFormatingTest() {
        HumanaChatTranscriptTriggerHelper.byPassTrigger = false;
        User testUser = HUMTestDataHelperTest.createTestUser('LiveAgent', 'Customer Care Specialist');
        insert testUser;
        System.assertEquals(testUser.LastName == 'test LiveAgent', true);
        // RESTRICTED_USER_ID value Is org specific hence it Is Not consdered as GLOBAL_CONTSANTS
        HUMConstants__c hCons2 = New HUMConstants__c(Name ='RESTRICTED_USER_ID',StringValue__c=String.valueOf(testUser.Id));
        insert hCons2;
        System.assert(HUMConstants__c.getValues('RESTRICTED_USER_ID') != NULL);
        System.runAs(testUser)
        {
           Test.startTest();
            List<Case> lstCase = new List<Case>();
            Set <Id> setAcctId = new Set <Id> ();
            
            Account oAccount = HUMTestDataHelperTest.getAccount();
            oAccount.name = 'AccTest';
            oAccount.enterprise_id__C = '1037402785';
            oAccount.OwnerId = testUser.Id;
            insert oAccount;
            
            // Create Case record so as to fetch security indicators.
            Case oCase = HUMTestDataHelperTest.getCase();
            oCase.AccountId = oAccount.Id;
            oCase.CoKY_Case__c = True;
            oCase.General_Case__c = True;
            oCase.DST_Case__c = True;
            oCase.HO_Medical_Case__c = True;
            oCase.OwnerId = testUser.Id;
            insert oCase;

            //Creating Live chat visitor record for simulating actual chat. 
            Livechatvisitor oLVisitor = New Livechatvisitor();
            insert oLVisitor;
            System.assert(oLVisitor.Id <> null);
            List<LiveChatTranscript> lstLiveChatTranscript = new List<LiveChatTranscript>();
  
            LiveChatTranscript oLiveChatTranscript = new LiveChatTranscript();
            oLiveChatTranscript.AccountId__c = oAccount.ID;
            oLiveChatTranscript.LiveChatVisitorId = oLVisitor.id;
            oliveChatTranscript.ChatKey = 'Test_example';
            oliveChatTranscript.chat_type__c = GLOBAL_CONSTANT_HUM.ASYNC_WEB_CHAT;
            oliveChatTranscript.CaseId = oCase.Id;
            setAcctId.add(oAccount.ID);
                      
            insert oliveChatTranscript;
            
            List<Humana_Chat_Transcript__c> lstCustomChatTranscript = new List<Humana_Chat_Transcript__c>();        
            LiveChatTranscript testLiveChatTranscript = [SELECT Id FROM LiveChatTranscript WHERE ChatKey= 'Test_example' AND chat_type__c =: GLOBAL_CONSTANT_HUM.ASYNC_WEB_CHAT LIMIT 1]; 

            Humana_Chat_Transcript__c oCustomChat = new Humana_Chat_Transcript__c();
            oCustomChat.live_chat_id__C = testLiveChatTranscript.Id;
            oCustomChat.Chat_Key__c = 'Test_ChatTrans';
            oCustomChat.Body__c = chatTranscript;
            oCustomChat.ownerId = testUser.Id;
            insert oCustomChat;

            //Read the inserted formated content for the evaluation
            Humana_Chat_Transcript__c customChatobj = [SELECT Body__c FROM Humana_Chat_Transcript__c WHERE Id=:oCustomChat.Id];
            System.assertEquals(customChatobj.Body__c == formattedContentInsert,true );
            
            //Update scenario
            customChatobj.Body__c = customChatobj.Body__c+'AGENT_MESSAGE: Thank you<br>';
            update customChatobj;
            
            //Read the updated formated content for the evaluation
            Humana_Chat_Transcript__c oCustomChatUpdate = [SELECT Body__c FROM Humana_Chat_Transcript__c WHERE Id=:customChatobj.Id];
            System.assertEquals(oCustomChatUpdate.Body__c == formattedContentupdate,true );
            
            Test.stopTest();
        }
    }
}