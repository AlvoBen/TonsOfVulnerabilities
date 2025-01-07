/******************************************************************************************************************
Apex class Name    : UpdateChatTranscriptService_T_HUM
Version            : 1.0
Function           : This class is used as test class for update transcript Rest Service
Created On         : January 04 ,2022
Test Class         : 

Modification Log:
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0            Alvaro Madrid                                    1/04/2022          Original Version

------------------------------------------------------------------------------------------------------------------- */

@isTest
public class UpdateChatTranscriptService_T_HUM {
	/**
    * This method is used to update the data only once and called automicticaly in all methods 
    *
    * @param  None      
    * @return  Void
    */
    @testSetup static void setup()
    {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','System Administrator');
        insert oUser;
      
        System.runAs(oUser) 
        {
            //Classification
            Classification__c classificationRecDec = new Classification__c();
            classificationRecDec.Name = 'Affidavit';
            classificationRecDec.Is_Active__c = true;
            insert classificationRecDec;
            system.assertEquals(classificationRecDec.Name == 'Affidavit',true);
            
            //Intent
            Intent__c intentRec = new Intent__c();
            intentRec.Name = 'New';           
            intentRec.Is_Active__c = true;
            insert intentRec;
            system.assertEquals(intentRec.name == 'New',true);
            
            //Add Account
            List<Account> lstAccount = new  List<Account>();
            Account acc = new Account( Enterprise_Id__c = '223300104', firstname = 'test1' ,lastname = 'method1' , Birthdate__c= '01/01/1990',  ETL_Record_Deleted__c = false , recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId());
            lstAccount.Add(acc);
            Insert lstAccount;
            system.assertNotEquals(lstAccount, null , 'Accounts not created');
             
            //Case Details
            Case caseObj = HUMTestDataHelperTest.getCase();
            caseObj.Interacting_With__c = lstAccount[0].Id;
            caseObj.Interacting_With_Name__c = 'Test Name';
            caseObj.Classification_ID__c = classificationRecDec.ID;
            caseObj.Intent_ID__c  = intentRec.id;
            caseobj.Interacting_With_Type__c = 'Member';
            insert caseObj ;
            System.assertEquals(caseobj.Interacting_With_Type__c == 'Member', true);
            
            Interaction__c intObj = new Interaction__c();
            intObj.Interacting_With__c = lstAccount[0].Id;
            intObj.Interacting_With_type__c = 'Member';
            intObj.Interaction_Origin__c = 'Fax';
            insert intObj;
            
            LiveChatVisitor oLCV = new LiveChatVisitor();
            insert oLCV;

            LiveChatTranscript liveChatObj = new LiveChatTranscript();
            liveChatObj.caseId = caseObj.Id;   
            liveChatObj.Body ='08:56:19 Chat ID: Tue Aug 3rd 2021, 8:56 amEngagement ID 571151544565161906208:56:19 You: Well hello there.08:56:20 Winston enters chat (as Winston)08:56:57 EffectiveDate: 06/12/2020,EndDate: 06/12/2021,FirstName: John,ID: 12345,LastName: Doe,MemberIdDependentCode: 456,PlanName: xxxxxx,PolicyGroupID: 987654321,PolicyMember: 08909766609:02:34 You: Customer connection lost. The customer lost network connectivity09:03:06 Winston: test209:18:52 Chat ID: Tue Aug 3rd 2021, 9:18 amEngagement ID 571151544574028984109:18:52 You: Well hello there.09:18:53 Winston: enters chat (as Winston)09:19:08 EffectiveDate: 06/12/2020,EndDate: 06/12/2021,FirstName: John,ID: 12345,LastName: Doe,MemberIdDependentCode: 456,PlanName: xxxxxx,PolicyGroupID: 987654321,PolicyMember: 08909766609:24:00 Winston: test 309:24:00 You: Chat Session Ended due to agent leaving the chatroom09:29:44 Chat ID: Tue Aug 3rd 2021, 9:29 amEngagement ID 571151544578301955509:29:44 You: Well hello there.09:29:45 AGENT_JOINS_CHAT enters chat (as Winston)09:30:00 ID: 1234509:30:00 LastName: Doe09:30:00 FirstName: John09:30:00 PlanName: xxxxxx09:30:00 EndDate: 06/12/202109:30:00 PolicyMember: 08909766609:30:00 PolicyGroupID: 98765432109:30:00 EffectiveDate: 06/12/202009:30:00 MemberIdDependentCode: 45609:33:54 AGENT_ENDS_CHAT: exits chat09:33:54 bavery_agt2s Follow Up Notes: test 409:33:54 CLIENT_DISCONNECTS: Chat Session Ended due to agent leaving the chatroom09:35:14 Chat ID: Tue Aug 3rd 2021, 9:35 amEngagement ID 571151544580464658109:35:14 You: Well hello there.09:35:15 AGENT_JOINS_CHAT enters chat (as Winston)09:35:24 ID: 1234509:35:24 LastName: Doe09:35:24 FirstName: John09:35:24 PlanName: xxxxxx09:35:24 EndDate: 06/12/202109:35:24 PolicyMember: 08909766609:35:24 PolicyGroupID: 98765432109:35:24 EffectiveDate: 06/12/202009:35:24 MemberIdDependentCode: 45609:35:28 CaseNumber: 100001147713509:41:03 CLIENT_DISCONNECTS: Customer connection lost. The customer lost network connectivity09:50:46 AGENT_ENDS_CHAT: Agent exits chat09:50:46 bavery_agt2s Follow Up Notes: test 509:53:13 Chat ID: Tue Aug 3rd 2021, 9:53 amEngagement ID 571151544587536035609:53:13 You: Well hello there.09:53:14 AGENT_JOINS_CHAT: Agent enters chat (as Winston)09:56:32 CLIENT_DISCONNECTS: 08:56:19 Chat ID: Tue Aug 3rd 2021, 8:56 amEngagement ID 571151544565161906208:56:19 You: Well hello there.08:56:20 Winston: Agent enters chat (as Winston)08:56:57 EffectiveDate: 06/12/2020,EndDate: 06/12/2021,FirstName: John,ID: 12345,LastName: Doe,MemberIdDependentCode: 456,PlanName: xxxxxx,PolicyGroupID: 987654321,PolicyMember: 08909766609:02:34 You: Customer connection lost. The customer lost network connectivity09:03:06 Winston: test209:18:52 Chat ID: Tue Aug 3rd 2021, 9:18 amEngagement ID 571151544574028984109:18:52 You: Well hello there.09:18:53 Winston: Agent enters chat (as Winston)09:19:08 EffectiveDate: 06/12/2020,EndDate: 06/12/2021,FirstName: John,ID: 12345,LastName: Doe,MemberIdDependentCode: 456,PlanName: xxxxxx,PolicyGroupID: 987654321,PolicyMember: 08909766609:24:00 Winston: test 309:24:00 You: Chat Session Ended due to agent leaving the chatroom09:29:44 Chat ID: Tue Aug 3rd 2021, 9:29 amEngagement ID 571151544578301955509:29:44 You: Well hello there.09:29:45 AGENT_JOINS_CHAT: Agent bavery_agt2 enters chat (as Winston)09:30:00 ID: 1234509:30:00 LastName: Doe09:30:00 FirstName: John09:30:00 PlanName: xxxxxx09:30:00 EndDate: 06/12/202109:30:00 PolicyMember: 08909766609:30:00 PolicyGroupID: 98765432109:30:00 EffectiveDate: 06/12/202009:30:00 MemberIdDependentCode: 45609:33:54 AGENT_ENDS_CHATexits chat09:33:54 bavery_agt2s Follow Up Notes: test 409:33:54 CLIENT_DISCONNECTS: Chat Session Ended due to agent leaving the chatroom09:35:14 Chat ID: Tue Aug 3rd 2021, 9:35 amEngagement ID 571151544580464658109:35:14 You: Well hello there.09:35:15 AGENT_JOINS_CHAT: Agent enters chat (as Winston)09:35:24 ID: 1234509:35:24 LastName: Doe09:35:24 FirstName: John09:35:24 PlanName: xxxxxx09:35:24 EndDate: 06/12/202109:35:24 PolicyMember: 08909766609:35:24 PolicyGroupID: 98765432109:35:24 EffectiveDate: 06/12/202009:35:24 MemberIdDependentCode: 45609:35:28 CaseNumber: 100001147713509:41:03 CLIENT_DISCONNECTS: Customer connection lost. The customer lost network connectivity09:50:46 AGENT_ENDS_CHAT: exits chat09:50:46 bavery_agt2s Follow Up Notes: test 509:53:13 Chat ID: Tue Aug 3rd 2021, 9:53 amEngagement ID 571151544587536035609:53:13 You: Well hello there.09:53:14 AGENT_JOINS_CHAT: Agent enters chat (as Winston)09:56:32 CLIENT_DISCONNECTS: 08:56:19 Chat ID: Tue Aug 3rd 2021, 8:56 amEngagement ID 571151544565161906208:56:19 You: Well hello there.08:56:20 Winston: Agent enters chat (as Winston)08:56:57 EffectiveDate: 06/12/2020,EndDate: 06/12/2021,FirstName: John,ID: 12345,LastName: Doe,MemberIdDependentCode: 456,PlanName: xxxxxx,PolicyGroupID: 987654321,PolicyMember: 08909766609:02:34 You: Customer connection lost. The customer lost network connectivity09:03:06 Winston: test209:18:52 Chat ID: Tue Aug 3rd 2021, 9:18 amEngagement ID 571151544574028984109:18:52 You: Well hello there.09:18:53 Winston: enters chat (as Winston)09:19:08 EffectiveDate: 06/12/2020,EndDate: 06/12/2021,FirstName: John,ID: 12345,LastName: Doe,MemberIdDependentCode: 456,PlanName: xxxxxx,PolicyGroupID: 987654321,PolicyMember: 08909766609:24:00 Winston: test 309:24:00 You: Chat Session Ended due to agent leaving the chatroom09:29:44 Chat ID: Tue Aug 3rd 2021, 9:29 amEngagement ID 571151544578301955509:29:44 You: Well hello there.09:29:45 AGENT_JOINS_CHAT: Agent enters chat (as Winston)09:30:00 ID: 1234509:30:00 LastName: Doe09:30:00 FirstName: John09:30:00 PlanName: xxxxxx09:30:00 EndDate: 06/12/202109:30:00 PolicyMember: 08909766609:30:00 PolicyGroupID: 98765432109:30:00 EffectiveDate: 06/12/202009:30:00 MemberIdDependentCode: 45609:33:54 AGENT_ENDS_CHAT: Agent  exits chat09:33:54 bavery_agt2s Follow Up Notes: test 409:33:54 CLIENT_DISCONNECTS: Chat Session Ended due to agent leaving the chatroom09:35:14 Chat ID: Tue Aug 3rd 2021, 9:35 amEngagement ID 571151544580464658109:35:14 You: Well hello there.09:35:15 AGENT_JOINS_CHAT: Agent enters chat (as Winston)09:35:24 ID: 1234509:35:24 LastName: Doe09:35:24 FirstName: John09:35:24 PlanName: xxxxxx09:35:24 EndDate: 06/12/202109:35:24 PolicyMember: 08909766609:35:24 PolicyGroupID: 98765432109:35:24 EffectiveDate: 06/12/202009:35:24 MemberIdDependentCode: 45609:35:28 CaseNumber: 100001147713509:41:03 CLIENT_DISCONNECTS: Customer connection lost. The customer lost network connectivity09:50:46 AGENT_ENDS_CHAT: Agent  exits chat09:50:46 bavery_agt2s Follow Up Notes: test 509:53:13 Chat ID: Tue Aug 3rd 2021, 9:53 amEngagement ID 571151544587536035609:53:13 You: Well hello there.09:53:14 AGENT_JOINS_CHAT: enters chat (as Winston)09:56:32 CLIENT_DISCONNECTS: 08:56:19 Chat ID: Tue Aug 3rd 2021, 8:56 amEngagement ID 571151544565161906208:56:19 You: Well hello there.08:56:20 Winston: enters chat (as Winston)08:56:57 EffectiveDate: 06/12/2020,EndDate: 06/12/2021,FirstName: John,ID: 12345,LastName: Doe,MemberIdDependentCode: 456,PlanName: xxxxxx,PolicyGroupID: 987654321,PolicyMember: 08909766609:02:34 You: Customer connection lost. The customer lost network connectivity09:03:06 Winston: test209:18:52 Chat ID: Tue Aug 3rd 2021, 9:18 amEngagement ID 571151544574028984109:18:52 You: Well hello there.09:18:53 Winston: Agent enters chat (as Winston)09:19:08 EffectiveDate: 06/12/2020,EndDate: 06/12/2021,FirstName: John,ID: 12345,LastName: Doe,MemberIdDependentCode: 456,PlanName: xxxxxx,PolicyGroupID: 987654321,PolicyMember: 08909766609:24:00 Winston: test 309:24:00 You: Chat Session Ended due to agent leaving the chatroom09:29:44 Chat ID: Tue Aug 3rd 2021, 9:29 amEngagement ID 571151544578301955509:29:44 You: Well hello there.09:29:45 AGENT_JOINS_CHAT: Agent enters chat (as Winston)09:30:00 ID: 1234509:30:00 LastName: Doe09:30:00 FirstName: John09:30:00 PlanName: xxxxxx09:30:00 EndDate: 06/12/202109:30:00 PolicyMember: 08909766609:30:00 PolicyGroupID: 98765432109:30:00 EffectiveDate: 06/12/202009:30:00 MemberIdDependentCode: 45609:33:54 AGENT_ENDS_CHAT: Agent exits chat09:33:54 bavery_agt2s Follow Up Notes: test 409:33:54 CLIENT_DISCONNECTS: Chat Session Ended due to agent leaving the chatroom09:35:14 Chat ID: Tue Aug 3rd 2021, 9:35 amEngagement ID 571151544580464658109:35:14 You: Well hello there.09:35:15 AGENT_JOINS_CHAT: Agent enters chat (as Winston)09:35:24 ID: 1234509:35:24 LastName: Doe09:35:24 FirstName: John09:35:24 PlanName: xxxxxx09:35:24 EndDate: 06/12/202109:35:24 PolicyMember: 08909766609:35:24 PolicyGroupID: 98765432109:35:24 EffectiveDate: 06/12/202009:35:24 MemberIdDependentCode: 45609:35:28 CaseNumber: 100001147713509:41:03 CLIENT_DISCONNECTS: Customer connection lost. The customer lost network connectivity09:50:46 AGENT_ENDS_CHAT: exits chat09:50:46 bavery_agt2s Follow Up Notes: test 509:53:13 Chat ID: Tue Aug 3rd 2021, 9:53 amEngagement ID 571151544587536035609:53:13 You: Well hello there.09:53:14 AGENT_JOINS_CHAT: Agent enters chat (as Winston)09:56:32 CLIENT_DISCONNECTS:';
            liveChatObj.Chat_Type__c ='IVANINA-Escalated';
            liveChatObj.Issue__c = 'test';
            liveChatObj.Category__c = 'test';
            liveChatObj.URL__c = 'test';
            liveChatObj.AverageResponseTimeVisitor = 1;
            liveChatObj.MaxResponseTimeVisitor = 1;
            liveChatObj.VisitorMessageCount = 1;
            liveChatObj.OperatorMessageCount = 1;
            liveChatObj.RequestTime =  DateTime.ValueOfGMT('2020-06-23 12:00:00');
            liveChatObj.EndTime =  DateTime.ValueOfGMT('2020-06-23 12:00:00');
            liveChatObj.StartTime =  DateTime.ValueOfGMT('2020-06-23 12:00:00');
            liveChatObj.Interaction__c = intObj.Id;
            liveChatObj.Status = GLOBAL_CONSTANT_HUM.HUM_COMPLETED;
            liveChatObj.LiveChatVisitorId = oLCV.Id;
            liveChatObj.Successful_Resolution__c ='Yes';
            insert liveChatObj;

            User testUser = HUMTestDataHelperTest.createTestUser('Customer_Case_specialist_User', 'Customer Care Specialist');
            testUser.Current_Queue__c = 'Louisville RSO Enrollment';
            insert testUser;
            System.assertEquals(testUser.lastname == 'test Customer_Case_specialist_User', true);
            // RESTRICTED_USER_ID value Is org specific hence it Is Not consdered as GLOBAL_CONTSANTS
            HUMConstants__c hCons2 = New HUMConstants__c(Name ='RESTRICTED_USER_ID',StringValue__c=String.valueOf(testUser.Id));
            insert hCons2;
            System.assert(HUMConstants__c.getValues('RESTRICTED_USER_ID') != NULL);
        }
    }
    
    @isTest static void testUpdateHUMChatTranscriptService()
    {
        HUMTestDataHelperTest.getHUMConstantsData();
        User oUser = [SELECT Id,lastname  FROM User  WHERE Lastname ='test User1' LIMIT 1];
        system.assertEquals(oUser.lastname == 'test User1',true);
        
        List<Case> lstCase =  [Select id,CaseNumber from Case where Interacting_With_Name__c='Test Name'];
        List<Interaction__c> lstInt =  [Select id,Name from Interaction__c where Interacting_With_type__c='Member'];
        LiveChatTranscript liveChatObj = [SELECT id, name FROM LiveChatTranscript LIMIT 1]; 

        System.runAs(oUser) 
        {
            UpdateChatTranscriptRequest_DTO_HUM request = new UpdateChatTranscriptRequest_DTO_HUM();
            UpdateChatTranscriptRequest_DTO_HUM.UpdateChatTranscriptRequest  updateChatTranscriptRequest  = new UpdateChatTranscriptRequest_DTO_HUM.UpdateChatTranscriptRequest();
            
            updateChatTranscriptRequest.TranscriptNumber = liveChatObj.Name;
            updateChatTranscriptRequest.InteractionNumber = lstInt[0].Name;
            updateChatTranscriptRequest.CaseNumber = lstCase[0].CaseNumber ;   
            updateChatTranscriptRequest.TranscriptBody ='08:56:19 Chat ID: Tue Aug 3rd 2021, 8:56 amEngagement ID 571151544565161906208:56:19 You: Well hello there.08:56:20 Winston enters chat (as Winston)08:56:57 EffectiveDate: 06/12/2020,EndDate: 06/12/2021,FirstName: John,ID: 12345,LastName: Doe,MemberIdDependentCode: 456,PlanName: xxxxxx,PolicyGroupID: 987654321,PolicyMember: 08909766609:02:34 You: Customer connection lost. The customer lost network connectivity09:03:06 Winston: test209:18:52 Chat ID: Tue Aug 3rd 2021, 9:18 amEngagement ID 571151544574028984109:18:52 You: Well hello there.09:18:53 Winston: enters chat (as Winston)09:19:08 EffectiveDate: 06/12/2020,EndDate: 06/12/2021,FirstName: John,ID: 12345,LastName: Doe,MemberIdDependentCode: 456,PlanName: xxxxxx,PolicyGroupID: 987654321,PolicyMember: 08909766609:24:00 Winston: test 309:24:00 You: Chat Session Ended due to agent leaving the chatroom09:29:44 Chat ID: Tue Aug 3rd 2021, 9:29 amEngagement ID 571151544578301955509:29:44 You: Well hello there.09:29:45 AGENT_JOINS_CHAT enters chat (as Winston)09:30:00 ID: 1234509:30:00 LastName: Doe09:30:00 FirstName: John09:30:00 PlanName: xxxxxx09:30:00 EndDate: 06/12/202109:30:00 PolicyMember: 08909766609:30:00 PolicyGroupID: 98765432109:30:00 EffectiveDate: 06/12/202009:30:00 MemberIdDependentCode: 45609:33:54 AGENT_ENDS_CHAT: exits chat09:33:54 bavery_agt2s Follow Up Notes: test 409:33:54 CLIENT_DISCONNECTS: Chat Session Ended due to agent leaving the chatroom09:35:14 Chat ID: Tue Aug 3rd 2021, 9:35 amEngagement ID 571151544580464658109:35:14 You: Well hello there.09:35:15 AGENT_JOINS_CHAT enters chat (as Winston)09:35:24 ID: 1234509:35:24 LastName: Doe09:35:24 FirstName: John09:35:24 PlanName: xxxxxx09:35:24 EndDate: 06/12/202109:35:24 PolicyMember: 08909766609:35:24 PolicyGroupID: 98765432109:35:24 EffectiveDate: 06/12/202009:35:24 MemberIdDependentCode: 45609:35:28 CaseNumber: 100001147713509:41:03 CLIENT_DISCONNECTS: Customer connection lost. The customer lost network connectivity09:50:46 AGENT_ENDS_CHAT: Agent exits chat09:50:46 bavery_agt2s Follow Up Notes: test 509:53:13 Chat ID: Tue Aug 3rd 2021, 9:53 amEngagement ID 571151544587536035609:53:13 You: Well hello there.09:53:14 AGENT_JOINS_CHAT: Agent enters chat (as Winston)09:56:32 CLIENT_DISCONNECTS: 08:56:19 Chat ID: Tue Aug 3rd 2021, 8:56 amEngagement ID 571151544565161906208:56:19 You: Well hello there.08:56:20 Winston: Agent enters chat (as Winston)08:56:57 EffectiveDate: 06/12/2020,EndDate: 06/12/2021,FirstName: John,ID: 12345,LastName: Doe,MemberIdDependentCode: 456,PlanName: xxxxxx,PolicyGroupID: 987654321,PolicyMember: 08909766609:02:34 You: Customer connection lost. The customer lost network connectivity09:03:06 Winston: test209:18:52 Chat ID: Tue Aug 3rd 2021, 9:18 amEngagement ID 571151544574028984109:18:52 You: Well hello there.09:18:53 Winston: Agent enters chat (as Winston)09:19:08 EffectiveDate: 06/12/2020,EndDate: 06/12/2021,FirstName: John,ID: 12345,LastName: Doe,MemberIdDependentCode: 456,PlanName: xxxxxx,PolicyGroupID: 987654321,PolicyMember: 08909766609:24:00 Winston: test 309:24:00 You: Chat Session Ended due to agent leaving the chatroom09:29:44 Chat ID: Tue Aug 3rd 2021, 9:29 amEngagement ID 571151544578301955509:29:44 You: Well hello there.09:29:45 AGENT_JOINS_CHAT: Agent bavery_agt2 enters chat (as Winston)09:30:00 ID: 1234509:30:00 LastName: Doe09:30:00 FirstName: John09:30:00 PlanName: xxxxxx09:30:00 EndDate: 06/12/202109:30:00 PolicyMember: 08909766609:30:00 PolicyGroupID: 98765432109:30:00 EffectiveDate: 06/12/202009:30:00 MemberIdDependentCode: 45609:33:54 AGENT_ENDS_CHATexits chat09:33:54 bavery_agt2s Follow Up Notes: test 409:33:54 CLIENT_DISCONNECTS: Chat Session Ended due to agent leaving the chatroom09:35:14 Chat ID: Tue Aug 3rd 2021, 9:35 amEngagement ID 571151544580464658109:35:14 You: Well hello there.09:35:15 AGENT_JOINS_CHAT: Agent enters chat (as Winston)09:35:24 ID: 1234509:35:24 LastName: Doe09:35:24 FirstName: John09:35:24 PlanName: xxxxxx09:35:24 EndDate: 06/12/202109:35:24 PolicyMember: 08909766609:35:24 PolicyGroupID: 98765432109:35:24 EffectiveDate: 06/12/202009:35:24 MemberIdDependentCode: 45609:35:28 CaseNumber: 100001147713509:41:03 CLIENT_DISCONNECTS: Customer connection lost. The customer lost network connectivity09:50:46 AGENT_ENDS_CHAT: exits chat09:50:46 bavery_agt2s Follow Up Notes: test 509:53:13 Chat ID: Tue Aug 3rd 2021, 9:53 amEngagement ID 571151544587536035609:53:13 You: Well hello there.09:53:14 AGENT_JOINS_CHAT: Agent enters chat (as Winston)09:56:32 CLIENT_DISCONNECTS: 08:56:19 Chat ID: Tue Aug 3rd 2021, 8:56 amEngagement ID 571151544565161906208:56:19 You: Well hello there.08:56:20 Winston: Agent enters chat (as Winston)08:56:57 EffectiveDate: 06/12/2020,EndDate: 06/12/2021,FirstName: John,ID: 12345,LastName: Doe,MemberIdDependentCode: 456,PlanName: xxxxxx,PolicyGroupID: 987654321,PolicyMember: 08909766609:02:34 You: Customer connection lost. The customer lost network connectivity09:03:06 Winston: test209:18:52 Chat ID: Tue Aug 3rd 2021, 9:18 amEngagement ID 571151544574028984109:18:52 You: Well hello there.09:18:53 Winston: enters chat (as Winston)09:19:08 EffectiveDate: 06/12/2020,EndDate: 06/12/2021,FirstName: John,ID: 12345,LastName: Doe,MemberIdDependentCode: 456,PlanName: xxxxxx,PolicyGroupID: 987654321,PolicyMember: 08909766609:24:00 Winston: test 309:24:00 You: Chat Session Ended due to agent leaving the chatroom09:29:44 Chat ID: Tue Aug 3rd 2021, 9:29 amEngagement ID 571151544578301955509:29:44 You: Well hello there.09:29:45 AGENT_JOINS_CHAT: Agent enters chat (as Winston)09:30:00 ID: 1234509:30:00 LastName: Doe09:30:00 FirstName: John09:30:00 PlanName: xxxxxx09:30:00 EndDate: 06/12/202109:30:00 PolicyMember: 08909766609:30:00 PolicyGroupID: 98765432109:30:00 EffectiveDate: 06/12/202009:30:00 MemberIdDependentCode: 45609:33:54 AGENT_ENDS_CHAT: Agent  exits chat09:33:54 bavery_agt2s Follow Up Notes: test 409:33:54 CLIENT_DISCONNECTS: Chat Session Ended due to agent leaving the chatroom09:35:14 Chat ID: Tue Aug 3rd 2021, 9:35 amEngagement ID 571151544580464658109:35:14 You: Well hello there.09:35:15 AGENT_JOINS_CHAT: Agent enters chat (as Winston)09:35:24 ID: 1234509:35:24 LastName: Doe09:35:24 FirstName: John09:35:24 PlanName: xxxxxx09:35:24 EndDate: 06/12/202109:35:24 PolicyMember: 08909766609:35:24 PolicyGroupID: 98765432109:35:24 EffectiveDate: 06/12/202009:35:24 MemberIdDependentCode: 45609:35:28 CaseNumber: 100001147713509:41:03 CLIENT_DISCONNECTS: Customer connection lost. The customer lost network connectivity09:50:46 AGENT_ENDS_CHAT: Agent  exits chat09:50:46 bavery_agt2s Follow Up Notes: test 509:53:13 Chat ID: Tue Aug 3rd 2021, 9:53 amEngagement ID 571151544587536035609:53:13 You: Well hello there.09:53:14 AGENT_JOINS_CHAT: enters chat (as Winston)09:56:32 CLIENT_DISCONNECTS: 08:56:19 Chat ID: Tue Aug 3rd 2021, 8:56 amEngagement ID 571151544565161906208:56:19 You: Well hello there.08:56:20 Winston: enters chat (as Winston)08:56:57 EffectiveDate: 06/12/2020,EndDate: 06/12/2021,FirstName: John,ID: 12345,LastName: Doe,MemberIdDependentCode: 456,PlanName: xxxxxx,PolicyGroupID: 987654321,PolicyMember: 08909766609:02:34 You: Customer connection lost. The customer lost network connectivity09:03:06 Winston: test209:18:52 Chat ID: Tue Aug 3rd 2021, 9:18 amEngagement ID 571151544574028984109:18:52 You: Well hello there.09:18:53 Winston: Agent enters chat (as Winston)09:19:08 EffectiveDate: 06/12/2020,EndDate: 06/12/2021,FirstName: John,ID: 12345,LastName: Doe,MemberIdDependentCode: 456,PlanName: xxxxxx,PolicyGroupID: 987654321,PolicyMember: 08909766609:24:00 Winston: test 309:24:00 You: Chat Session Ended due to agent leaving the chatroom09:29:44 Chat ID: Tue Aug 3rd 2021, 9:29 amEngagement ID 571151544578301955509:29:44 You: Well hello there.09:29:45 AGENT_JOINS_CHAT: Agent enters chat (as Winston)09:30:00 ID: 1234509:30:00 LastName: Doe09:30:00 FirstName: John09:30:00 PlanName: xxxxxx09:30:00 EndDate: 06/12/202109:30:00 PolicyMember: 08909766609:30:00 PolicyGroupID: 98765432109:30:00 EffectiveDate: 06/12/202009:30:00 MemberIdDependentCode: 45609:33:54 AGENT_ENDS_CHAT: Agent exits chat09:33:54 bavery_agt2s Follow Up Notes: test 409:33:54 CLIENT_DISCONNECTS: Chat Session Ended due to agent leaving the chatroom09:35:14 Chat ID: Tue Aug 3rd 2021, 9:35 amEngagement ID 571151544580464658109:35:14 You: Well hello there.09:35:15 AGENT_JOINS_CHAT: Agent enters chat (as Winston)09:35:24 ID: 1234509:35:24 LastName: Doe09:35:24 FirstName: John09:35:24 PlanName: xxxxxx09:35:24 EndDate: 06/12/202109:35:24 PolicyMember: 08909766609:35:24 PolicyGroupID: 98765432109:35:24 EffectiveDate: 06/12/202009:35:24 MemberIdDependentCode: 45609:35:28 CaseNumber: 100001147713509:41:03 CLIENT_DISCONNECTS: Customer connection lost. The customer lost network connectivity09:50:46 AGENT_ENDS_CHAT: exits chat09:50:46 bavery_agt2s Follow Up Notes: test 509:53:13 Chat ID: Tue Aug 3rd 2021, 9:53 amEngagement ID 571151544587536035609:53:13 You: Well hello there.09:53:14 AGENT_JOINS_CHAT: Agent enters chat (as Winston)09:56:32 CLIENT_DISCONNECTS:';
            updateChatTranscriptRequest.ChatType ='IVANINA-Escalated';
            updateChatTranscriptRequest.SourceApp ='IVANINA';
            updateChatTranscriptRequest.SourceKey ='Y123es';
            updateChatTranscriptRequest.SuccessfulResolution ='Yes';

            request.UpdateChatTranscriptRequest = updateChatTranscriptRequest;

            RestRequest req = new RestRequest();

            String JSONMsg = System.JSON.serialize(request);
            RestResponse res = new RestResponse();

            req.requestURI = '/LiveChatTranscriptService_V1.0/UpdateChatTranscript';  //Request URL
            req.httpMethod = 'PATCH';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);

            RestContext.request = req;
            RestContext.response= res;

            Test.StartTest();               
            UpdateChatTranscriptService_I_HUM.updateChatTranscriptInfo();
            RestResponse response = RestContext.response;

            UpdateChatTranscriptResponse_DTO_HUM chatResponse = new UpdateChatTranscriptResponse_DTO_HUM();
            chatResponse = (UpdateChatTranscriptResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), UpdateChatTranscriptResponse_DTO_HUM.class);
            system.assertEquals(chatResponse.UpdateChatTranscriptResponse.Success =='true',true); 
            Test.StopTest();
        }
    }

    @isTest static void testfaultHUMChatTranscriptService()
    {
        HUMTestDataHelperTest.getHUMConstantsData();
        User oUser = [SELECT Id,lastname  FROM User  WHERE Lastname ='test User1' LIMIT 1];
        system.assertEquals(oUser.lastname == 'test User1',true);
        
        List<Case> lstCase =  [Select id,CaseNumber from Case where Interacting_With_Name__c='Test Name'];
        List<Interaction__c> lstInt =  [Select id,Name from Interaction__c where Interacting_With_type__c='Member'];
        LiveChatTranscript liveChatObj = [SELECT id, name FROM LiveChatTranscript LIMIT 1]; 

        System.runAs(oUser) 
        {
            UpdateChatTranscriptRequest_DTO_HUM request = new UpdateChatTranscriptRequest_DTO_HUM();
            UpdateChatTranscriptRequest_DTO_HUM.UpdateChatTranscriptRequest  updateChatTranscriptRequest  = new UpdateChatTranscriptRequest_DTO_HUM.UpdateChatTranscriptRequest();
            
            updateChatTranscriptRequest.TranscriptNumber = liveChatObj.Name;       
            updateChatTranscriptRequest.TranscriptBody ='test';       
            updateChatTranscriptRequest.InteractionNumber = lstInt[0].Name;
            updateChatTranscriptRequest.CaseNumber = lstCase[0].CaseNumber ;   
            updateChatTranscriptRequest.ChatType ='IVANINA-Escalated';
            updateChatTranscriptRequest.SourceApp ='IVANINA';
            updateChatTranscriptRequest.SourceKey ='Y123es';
            updateChatTranscriptRequest.SuccessfulResolution ='Yes';
            
            request.UpdateChatTranscriptRequest = updateChatTranscriptRequest;

            RestRequest req = new RestRequest();

            String JSONMsg = System.JSON.serialize(request);
            RestResponse res = new RestResponse();

            req.requestURI = '/LiveChatTranscriptService_V1.0/UpdateChatTranscript';  //Request URL
            req.httpMethod = 'PATCH';//HTTP Request Type
            req.requestBody = Blob.valueof('{{ ChatTranscript Fail' + JSONMsg);

            System.debug('req.requestBody: ' + req.requestBody );
            RestContext.request = req;
            RestContext.response= res;

            Test.StartTest();               
            UpdateChatTranscriptService_I_HUM.updateChatTranscriptInfo();
            RestResponse response = RestContext.response;

            UpdateChatTranscriptResponse_DTO_HUM chatResponse = new UpdateChatTranscriptResponse_DTO_HUM();
            chatResponse = (UpdateChatTranscriptResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), UpdateChatTranscriptResponse_DTO_HUM.class);
            System.debug('chatResponse: Success: ' + chatResponse.UpdateChatTranscriptResponse.Success + ' ErrorMessage: ' + chatResponse.UpdateChatTranscriptResponse.ErrorMessage );
            system.assertEquals(chatResponse.UpdateChatTranscriptResponse.Success =='false',true); 
            
            updateChatTranscriptRequest.ChatType = null;
            request.UpdateChatTranscriptRequest = updateChatTranscriptRequest;
            JSONMsg = System.JSON.serialize(request);
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            UpdateChatTranscriptService_I_HUM.updateChatTranscriptInfo();
            response = RestContext.response;
            chatResponse = (UpdateChatTranscriptResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), UpdateChatTranscriptResponse_DTO_HUM.class);
            system.assertEquals(chatResponse.UpdateChatTranscriptResponse.Success =='false',true); 
            
            updateChatTranscriptRequest.SuccessfulResolution = 'Yas';
            updateChatTranscriptRequest.ChatType ='IVANINA-Escalated';
            request.UpdateChatTranscriptRequest = updateChatTranscriptRequest;
            JSONMsg = System.JSON.serialize(request);
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            UpdateChatTranscriptService_I_HUM.updateChatTranscriptInfo();
            response = RestContext.response;
            chatResponse = (UpdateChatTranscriptResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), UpdateChatTranscriptResponse_DTO_HUM.class);
            system.assertEquals(chatResponse.UpdateChatTranscriptResponse.ErrorMessage == System.Label.chatTranscriptServiceInvalidSR,true); 

            updateChatTranscriptRequest.SuccessfulResolution = 'No';
            updateChatTranscriptRequest.InteractionNumber = null;
            request.UpdateChatTranscriptRequest = updateChatTranscriptRequest;
            JSONMsg = System.JSON.serialize(request);
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            UpdateChatTranscriptService_I_HUM.updateChatTranscriptInfo();
            response = RestContext.response;
            chatResponse = (UpdateChatTranscriptResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), UpdateChatTranscriptResponse_DTO_HUM.class);
            system.assertEquals(chatResponse.UpdateChatTranscriptResponse.ErrorMessage == System.Label.chatTranscriptServiceInteraction,true);

            Test.StopTest();
        }
    }
}