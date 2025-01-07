/**************************************************************************** 
Apex Class Name  : LiveChatTranscript_T_HUM
Version          : 1.0  
Created Date     : October 31 ,2017
Function         : test class for LiveChatTranscript_C_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Sumit Pharande            148798           31/10/2017                   Original Version
* Pradeepkumar Dani		    148798			 11/17/2017				      re-factoring
* Ravikanth Samudrala 						 05-18-2023                   US-4531288 - T1PRJ0901110 MF1.11 - Additional Fields (blue marked) to the CRM PDF - CRM and MHK 
*************************************************************************************************/
@isTest
private with sharing class LiveChatTranscript_T_HUM
{
    /*
	* This method creates test data for test class
	* @param None
	* @return None
	*/
    @testSetup static void setUpTestData()
    {
        User testUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert testUser;
        User testAgent = HUMTestDataHelperTest.createTestUser('Agent1','Customer Care Specialist');
        insert testAgent;
        system.assertEquals(testUser.lastname=='test User1',true);
        system.runAs(testUser)
        { 
            Account objMemAccount = HUMTestDataHelperTest.getAccount();
            objMemAccount.FirstName = 'Test FName';
            objMemAccount.LastName = 'LName'; 
            objMemAccount.source_platform_code__c = 'EM' ;
            objMemAccount.Enterprise_Id__c = String.valueof(2000);
            objMemAccount.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            objMemAccount.Birthdate__c = String.valueOf(System.today().month()+'/'+system.today().day()+'/'+System.today().year());
            insert objMemAccount;
            system.assert(objMemAccount.LastName == 'LName');
            
            Case objCase = HUMTestDataHelperTest.getCase();
            insert objCase;
            system.assertNotEquals(objCase,null,'Value not matched');
            
            LiveChatVisitor visit = new LiveChatVisitor();
            insert visit ;
            
            LiveChatTranscript livechat = new LiveChatTranscript();
            livechat.AccountId__c = objMemAccount.Id;
            
            livechat.CaseId = objCase.Id;
            livechat.StartTime =DateTime.now();
            livechat.EndTIme =DateTime.now();
            livechat.Status = 'Completed';
            livechat.LiveChatVisitorID=visit.Id;
            insert livechat ;
            system.assertNotEquals(livechat,null,'Value not matched');
            
            LiveChatTranscriptEvent livechatEvent = new LiveChatTranscriptEvent();            
            livechatEvent.Time =DateTime.now();
            livechatEvent.AgentId = testAgent.Id;
            livechatEvent.Type  = 'ChatRequest';
            livechatEvent.Detail = 'Completed';
            livechatEvent.LiveChatTranscriptId = livechat.Id;
            insert livechatEvent ;
            system.assertNotEquals(livechatEvent,null,'Value not matched');
        }  
    }
    
    /*
	* This method is used to test the attachment funtionality
	* @param   None
	* @return  None
	*/
    static testMethod void testAtts()
    {
        User testUser = [SELECT Id,Name FROM User WHERE LastName='test User1'];
        
        system.runAs(testUser)
        {
            Test.startTest();
            
            Case caseObj = [SELECT Id FROM Case LIMIT 1];
            
            LiveChatTranscript_C_HUM lc = new LiveChatTranscript_C_HUM();
            LiveChatTranscript_C_HUM.getTranscript(caseObj.Id);
            system.assertEquals( lc.columnName <> null, TRUE);
            
            Test.stopTest();
        } 
    }
    
      /*
	* This method is used to test the attachment funtionality
	* @param   None
	* @return  None
	*/
    static testMethod void testLiveChatTranscriptEvent()
    {
        User testUser = [SELECT Id,Name FROM User WHERE LastName='test User1'];
        
        system.runAs(testUser)
        {
            Test.startTest();
            
            Case caseObj = [SELECT Id FROM Case LIMIT 1];
            
            LiveChatTranscript_C_HUM lc = new LiveChatTranscript_C_HUM();
            LiveChatTranscript_C_HUM.getLiveChatEventDTOList(caseObj.Id);
            Test.stopTest();
        } 
    }
}