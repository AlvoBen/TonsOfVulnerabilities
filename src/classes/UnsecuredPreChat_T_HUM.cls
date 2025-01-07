/*******************************************************************************************************************************
Apex Class Name : UnsecuredPreChat_T_HUM 
Version         : 1.0
Created On      : 12/17/2019
Function        : Test class for UnsecuredPreChat_C_HUM

Modification Log: 
 * Modification ID  Developer Name          Code Review               Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------
 * 1.0              Melkisan Selvaraj                              12/17/2019                  Original Version
 * 1.1              T Prasanna Sai Kumar                           03/11/2021                  added new method to increase the coverage
 ****************************************************************************************************************************/
@isTest
private class UnsecuredPreChat_T_HUM 
{
    //list of Web Chat Custom Setting records.
    private static List<HUMWebChatConstants__c> lstWebChat = new List<HUMWebChatConstants__c>();
    
    //Instance of Controller class
    Private static UnsecuredPreChat_C_HUM controllerObj = new UnsecuredPreChat_C_HUM();
    
    //Test User creation
    Private static User testUser = HUMTestDataHelperTest.createTestUser('SystemAdmin_User','System Administrator');
    /**
    * This method creates test data for test class
    *
    * @param  none
    * @return none
    */
    @testSetup static void setUpTestData()
    {
        lstWebChat.add(new HUMWebChatConstants__c(name='DeploymentJS',value__c='www.humanachatTestclass.com'));
        insert lstWebChat;
        system.assertEquals('www.humanachatTestclass.com', String.valueOf([Select Value__c from HUMWebChatConstants__c where Name='DeploymentJS'].Value__c));
        
        Account oAcc = HUMTestDataHelperTest.getAccount();
        oAcc.firstname = 'AccTest';
        oAcc.lastName = 'AccLast';
        oAcc.enterprise_id__C = '1013082169';
        oAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
        insert oAcc;
        system.assertEquals('1013082169', String.valueOf([Select Enterprise_Id__c from Account where Id=:oAcc.Id LIMIT 1].Enterprise_Id__c));
        
        Interaction__C oInt = new Interaction__C();
        oInt.Caller_name__C = 'Test Caller';
        oInt.Interacting_with_type__C = 'Member';
        oInt.Interacting_with__c = oAcc.Id;
        oInt.Interaction_Origin__c = 'Inbound Call';
        insert oInt ;
        system.assertEquals(oInt.id, [Select Id from Interaction__c where Interacting_With__c=:oAcc.Id LIMIT 1].Id);    

        LiveChatVisitor olcv = new LivechatVisitor();
        insert olcv;

        Livechattranscript oLCT = new Livechattranscript();
        oLCT.LiveChatVisitorId = olcv.Id;
        oLCT.Secondary_Chat_ID__c = 'test';
        oLCT.Interaction__c = oInt.Id;
        insert oLCT ;       
  }
    
    /**
    * This methods tests methods of UnsecuredPreChat_C_HUM class
    *
    * @param  none
    * @return none
    */
   @isTest Static void testUnsecuredController()
    {
        system.runAs(testUser)
        {
            Account oAcc = [Select Id, FirstName, Enterprise_Id__c from Account where enterprise_id__C = '1013082169'];
            system.assertEquals('AccTest',oAcc.FirstName);
            if(null != oAcc)
            {
                Interaction__c oInt = [Select Id,Name from Interaction__c where Interacting_With__c=:oAcc.Id];
                if(null != oInt)
                {
                    Apexpages.currentpage().getparameters().put('attachedRecords','"Interaction__c":"'+oInt.id +'"');
                    Apexpages.currentpage().getparameters().put('endpoint','"Button_id"= "123456789987654"&"session_id"="103-395-678"');
                    Test.startTest();
                    controllerObj = new UnsecuredPreChat_C_HUM();
                    controllerObj.postchatinit();                    
                    controllerObj.initVariable();
                    UnsecuredPreChat_C_HUM.findInteractionName('test');
                    system.assertNotEquals(null, Go365UnsecureChat_C_HUM.findInteractionName('test'));
                    controllerObj.goToHUMSaveChatTranscriptPage();
                    UnsecuredPreChat_C_HUM.onCommitTranscript('786788',testUser.Id);       
                    Test.stopTest();
                }
            }
        }
    }
     /**
    * This methods tests else condition scenarios of UnsecuredPreChat_C_HUM class
    *
    * @param  none
    * @return none
    */
  @isTest Static void unsecuretestController()
    {
        system.runAs(testUser)
        {
            Account oAcc = [Select Id, FirstName, Enterprise_Id__c from Account where enterprise_id__C = '1013082169'];
            system.assertEquals('AccTest',oAcc.FirstName);
            if(null != oAcc)
            {
                Interaction__c oInt = [Select Id,Name from Interaction__c where Interacting_With__c=:oAcc.Id];
                if(null != oInt)
                {
                    Apexpages.currentpage().getparameters().put('transcript','"Interaction__c":"'+oInt.id +'"');
                    Test.startTest();
                    controllerObj = new UnsecuredPreChat_C_HUM();
                    controllerObj.postchatinit();
                    UnsecuredPreChat_C_HUM.onCommitTranscript('',testUser.Id);
                    Test.stopTest();
                }
            }
        }
    }   
}