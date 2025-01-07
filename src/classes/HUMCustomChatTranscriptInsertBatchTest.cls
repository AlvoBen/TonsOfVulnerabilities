/**********************************************************************************************************************
Apex Class: HUMCustomChatTranscriptInsertBatchTest
Description: Test class for HUMCustomChatTranscriptInsertBatch class
Created By: Sagar Tapadia
Created On:  05/28/2015

Modification Log :
-----------------------------------------------------------------------------------------------------------------------
* Developer         Code Review        Date                    Description
* --------------------------------------------------------------------------------------------------------------------
* Sagar Tapadia        23562          05/28/2015               CA5027355: Live chat Transcript fix
* Sagar Tapadia        25275          08/05/2015               Added value for Issue field so that it is picked up for encryption.Line#68
* Pradeepkumar Dani    34627          02/22/2016               Updated html body and html supervisor chat body REQ - 224011
* Pradeepkumar Dani    34627          08/04/2016               Updated class with APEX standards - REQ - 270091
* Shiva Pasumarty                     04/03/2017               REQ - 301383  Added Endtime while inserting LiveChat Records
* Shiva Pasumarty      88337          04/28/2017               REQ - 311381 CA Ticket - 6730699 - Added custom setting value for ChatTranscriptCustomDate
* Sharan Shanmugam                    01/04/2018               REQ - 350764  Modified logic to populate HO_Dental__c field of Humana chat
* Rajesh Keswani       314114         10/31/2018               REQ - 377098 Update HUMCustomChatTranscriptInsertBatch to account indicators if case Is Not associated.
* Santhi Mandava                      03/12/2019               Removed "DpaaSWebServiceMockImpl" usage.
***********************************************************************************************************************/
@isTest private class HUMCustomChatTranscriptInsertBatchTest {
    public static String CRON_EXP = '0 0 0 15 3 ? 2024';
    
    /**
    * setUpTestData is a testSetup method create test date.
    * <p>
    * This method creates test data for test class
    *
    * @return void
    */
    @testSetup static void setUpTestData()
    {
        User testUser = HUMTestDataHelperTest.createTestUser('SystemAdmin_User', 'System Administrator');
        insert testUser;
        system.assertEquals(testUser.Id <> NULL,true);
        string Dt1 = '01/01/2017 08:30 AM';
        //loading HUMConstants Custom Settings data
        HUMTestDataHelperTest.getHUMConstantsData();
        HUMConstants__c hCons2 = new HUMConstants__c(Name='RESTRICTED_USER_ID',StringValue__c=String.valueOf(testUser.Id));
        insert hCons2;
        HUMConstants__c hCons3 = new HUMConstants__c(Name='ChatTranscriptCustomDate',StringValue__c=String.valueOf(Dt1));
        insert hCons3;
        system.assertEquals(hCons2.StringValue__c==String.valueOf(testUser.Id),true);
        
        //Create Account records for testing.
        Account oAcc = HUMTestDataHelperTest.getAccount();
        oAcc.name = 'AccTest';
        oAcc.enterprise_id__C = '1037402785';
        oAcc.CoKY_Account__c = true;
        oAcc.General_Account__c = true;
        oAcc.Home_Office_Account__c = false;
        insert oAcc;
        system.assertEquals(oAcc.Id <> NULL, true);
        
        //Create Case record so as to fetch security indicators.
        Case oCase = HUMTestDataHelperTest.getCase();
        oCase.AccountId = oAcc.Id;
        oCase.CoKY_Case__c = true;
        oCase.General_Case__c = true;
        oCase.HO_Dental_Case__c = true;
        oCase.HO_Medical_Case__c = true;
        insert oCase;
        system.assertEquals(oCase.Id <> NULL, true);
        system.assertEquals(oCase.AccountId == oAcc.Id, true);
        
        //Creating Live chat visitor record for simulating actual chat. 
        Livechatvisitor oLVisitor = new Livechatvisitor();
        insert oLVisitor;
        system.assertEquals(oLVisitor.Id <> NULL, true);        
        
        List<LiveChatTranscript> lstLiveChatTranscript = new List<LiveChatTranscript>();        
        //For testing trigger bulkification
        Integer randomNumber;
        for(Integer iInt =0; iInt<200; iInt++)
        {
            randomNumber = Integer.valueof((math.random() * 100000));
            LiveChatTranscript oLiveChatTranscript = new LiveChatTranscript();
            oLiveChatTranscript.AccountId = oAcc.ID;
            oLiveChatTranscript.LiveChatVisitorId = oLVisitor.id;
            oliveChatTranscript.ChatKey = 'Test'+ randomNumber;
            if(iInt == 2) oliveChatTranscript.CaseId = oCase.Id;
            oliveChatTranscript.OwnerId=testUser.Id;
            oliveChatTranscript.Body = 'Testing<br>Testing<br>Testing<br>Testing<br>Testing<br>';
            oliveChatTranscript.SuperVisorTranscriptBody='Test supervisor<br>Test supervisor<br>Test supervisor<br>Test supervisor<br>';
            oliveChatTranscript.Issue__c='TEST CDATA Test Issue';
            oliveChatTranscript.Endtime = system.now();
            oliveChatTranscript.Status = 'Completed';
            lstLiveChatTranscript.add(oLiveChatTranscript);
        }        
        insert lstLiveChatTranscript;
        system.assertEquals(lstLiveChatTranscript.size() <> 0, true);
        system.assertEquals(lstLiveChatTranscript[0].Id <> NULL, true);
                //Creating LivechatTranscript
        LiveChatTranscriptEvent oLivechatTranscript = new LiveChatTranscriptEvent();
        oLivechatTranscript.LiveChatTranscriptId = lstLiveChatTranscript[0].id;
        oLivechatTranscript.AgentId = testUser.Id;
        oLivechatTranscript.Type = 'ChatRequest';
        oLivechatTranscript.Time = System.Today();
        insert oLivechatTranscript;
        List<Humana_Chat_Transcript__c> lstCustomChatTranscript = new List<Humana_Chat_Transcript__c>();        
        for(Integer iInt = 0; iInt<200; iInt++)
        {
            Humana_Chat_Transcript__c oCustomChat = new Humana_Chat_Transcript__c();
            if(iInt == 2) oCustomChat.Chat_Key__c = 'Test'+iInt;
            oCustomChat.OwnerId = testUser.Id;
            lstCustomChatTranscript.add(oCustomChat);
        }        
        insert lstCustomChatTranscript;        
        system.assertEquals(lstCustomChatTranscript.size() <> 0, true);
        system.assertEquals(lstCustomChatTranscript[0].Id <> NULL, true);
    }
    
    /**
    * testCustomRecordBatchCreation is test method.
    * <p>
    * This method tests batch class updating custom chat record.
    *
    * @return void
    */
    Static testMethod void testCustomRecordBatchCreation() {
        User testUser = [Select id,Name from User where LastName='test SystemAdmin_User'];  
        system.runAs(testUser)
        {
            Test.startTest();
            // Schedule the test job
            String jobId = System.schedule('ScheduleApexClassTest', CRON_EXP, new HUMCustomChatTranscriptInsertBatch());
            // Get the information from the CronTrigger API object 
            CronTrigger ct = [SELECT Id, CronExpression, TimesTriggered,NextFireTime FROM CronTrigger WHERE id = :jobId];
            // Verify the expressions are the same
            System.assertEquals(CRON_EXP, ct.CronExpression);
            Test.stopTest();
        }
    }  
    /**
    * testCustomRecordBatchCreationNegative is test method.
    * <p>
    * This method is used to cover the exceptions.
    *
    * @return void
    */
    Static testMethod void testCustomRecordBatchCreationNegative() {
        User fetchUser = [Select id,Name from User where LastName='test SystemAdmin_User'];
        system.runAs(fetchUser)
        {
            Test.startTest();
            // Schedule the test job
            String jobId = System.schedule('ScheduleApexClassTest', CRON_EXP, new HUMCustomChatTranscriptInsertBatch());
            // Get the information from the CronTrigger API object 
            CronTrigger ct = [SELECT Id, CronExpression, TimesTriggered,NextFireTime FROM CronTrigger WHERE id = :jobId];
            // Verify the expressions are the same
            System.assertEquals(CRON_EXP, ct.CronExpression);
            Test.stopTest();
        }
        
    }  
}