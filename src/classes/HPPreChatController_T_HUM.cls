/*******************************************************************************************************************************
Apex Class Name : HPPreChatController_T_HUM 
Version         : 1.0
Created On      : 04/09/2019
Function        : Test class for HPPreChatController_C_HUM

Modification Log: 
 * Modification ID  Developer Name          Code Review               Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------
 * 1.0              Lakshmi Madduri                               04/09/2019              Original Version
 * 1.2              T Prasanna Sai Kumar                          03/23/2021              REQ - 1910466 Live Agent Security Vulnerabilities for SonarQube Scan - HP Live Agent
 * 1.3              Alvaro Madrid                                 07/17/2023              US 4842692 - Decrypt the IVA Bot Transcript from CWP
 ****************************************************************************************************************************/
@isTest
private class HPPreChatController_T_HUM 
{
    //list of Web Chat Custom Setting records.
    private static List<HUMWebChatConstants__c> lstWebChat = new List<HUMWebChatConstants__c>();
    
    /**
    * This method creates test data for test class
    *
    * @param  none
    * @return none
    */
    @testSetup static void setUpTestData()
    {
        lstWebChat.add(new HUMWebChatConstants__c(name='TestURL',value__c='www.humanachatTestclass.com'));
        insert lstWebChat;
        system.assertEquals('www.humanachatTestclass.com', String.valueOf([Select Value__c from HUMWebChatConstants__c where Name='TestURL'].Value__c));
        
        User testUser = HUMTestDataHelperTest.createTestUser('HPPreChat_User','System Administrator');
        testUser.FirstName = 'HPPreChatUser';
        insert testUser;
        system.assertEquals('test HPPreChat_User',testUser.Lastname);
        
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
	}
    
    /**
    * This method tests Perchat page functionality in non developer mode.
    *
    * @param  none
    * @return none
    */
    @isTest static void testPreChatinNonDevMode() 
    {
        User testUser = [select LastName from User where Firstname = 'HPPreChatUser'];
        system.assertEquals('test HPPreChat_User',testUser.Lastname);

        system.runAs(testUser)
        {
            //create Account records for testing.
              Account oAcc = [Select Id, FirstName, Enterprise_Id__c from Account limit 1];
            Interaction__c oInt = [Select Name from Interaction__c where Interacting_With__c=:oAcc.Id];
            
            Test.startTest();
			List<LiveChatButton> lstLCB =   [SELECT Id, DeveloperName, MasterLabel FROM LiveChatButton WHERE Type = 'Standard' limit 1];
			Apexpages.currentpage().getparameters().put('buttonId',lstLCB[0].Id);
			Apexpages.currentpage().getparameters().put('transcript','transcript');
			Apexpages.currentpage().getparameters().put('session_id', 'ST5xoPysnbUn0Gda0tpgRzjPt2pLD30p3qH1CTvvnE3SVtPGkLsLGrgtYKRCxz6K');
			HPPreChatController_C_HUM oHUMPreChat = new HPPreChatController_C_HUM();      
			oHUMPreChat.webChatTranscriptDetail = 'test chat';           	
			oHUMPreChat.initVariable();
			//Code for Encryption
			oHUMPreChat.getEnterpriseId();
			oHUMPreChat.getEncryptedString(oAcc.Enterprise_Id__c);
			
			//getting Interaction Name of Interaction to be passed to URL
			HPPreChatController_C_HUM.findInteractionName(String.valueOf(oInt.id));
			
			//checking negative Test Case by passing account id instead of Interaction id
			HPPreChatController_C_HUM.findInteractionName(String.valueOf(oAcc.Id));              
		
			//passing Dummy value of Decrypted Person Id to simulate Encryption-Decryption Process.
			String sEnterpriseID = '1RaRWHScBnalHJO9D+p98Q==';
			String sMemberGenKey = 'xkjV7NMXFxcrfonPKmHcvw==';
			String sLsessionID = 'ST5xoPysnbUn0Gda0tpgRzjPt2pLD30p3qH1CTvvnE3SVtPGkLsLGrgtYKRCxz6K';
            String sBotTranscript = 'b8F67e6bZiIYUOxk8nHlH10aMxr1C8GVlBxtXRYj4eHQRQ7zNbDYtQs343qqrs1Ufl+/KOdddNe/EbSMZ8yHiiRvXEaxAkO3OqjuTyvi9/g=';

			HPPreChatResultWrapperHelper oWrapper = HPPreChatController_C_HUM.findAccountByPersonId(sEnterpriseID,sMemberGenKey,sLsessionID);
			system.assertEquals(oWrapper.sAccId, oAcc.Id); 
			
            HPPreChatResultWrapperHelper oWrapper2=HPPreChatController_C_HUM.decryptUserIdentifiers(sMemberGenKey,sLsessionID,sEnterpriseID, sBotTranscript);
			system.assertEquals(oWrapper2.sDecryptLsessionID, '890a6f5feac042b59138bdfe3101e847');
			
			//checking negative test cases. Passing null value as String to be decrypted.
			sEnterpriseID = '';
			HPPreChatController_C_HUM.sDecryptMemberGenKey ='';
			oWrapper = HPPreChatController_C_HUM.findAccountByPersonId(sEnterpriseID,sMemberGenKey,sLsessionID);    
            System.assertEquals(null, oWrapper);
						
			//checking negative test cases. Passing wrong encrypted value as String to be decrypted.
			sEnterpriseID = '1RaRWHScBnalHJO9D p98Q=';
			oWrapper = HPPreChatController_C_HUM.findAccountByPersonId(sEnterpriseID,sMemberGenKey,sLsessionID);
            System.assertEquals(null, oWrapper);
			
			//passing false decrypted value to make account null
			sEnterpriseID = '4OL1EqyRRSZRbnyO6qxlkA==';
			oWrapper = HPPreChatController_C_HUM.findAccountByPersonId(sEnterpriseID,sMemberGenKey,sLsessionID);
            System.assertEquals(null, oWrapper);
			Test.stopTest();
        }
    }
    
    /**
    * This methods encrypts body of custom chat and then inserts custom chat record.
    *
    * @param  none
    * @return none
    */
    @isTest static void testCreateCustomTranscript()
    {
        User testUser = [select LastName from User where Firstname = 'HPPreChatUser'];
        system.assertEquals('test HPPreChat_User',testUser.Lastname);
        system.runAs(testUser)
        {
            Test.startTest();
                Account oAccount = HUMTestDataHelperTest.getAccount();
                oAccount.OwnerId = testUser.id;
                oAccount.LastName = 'LastName';
                oAccount.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            
                insert oAccount;
                system.assertEquals(oAccount.LastName, 'LastName');
                Interaction__c oInteraction = HUMTestDataHelperTest.getInteraction();
                oInteraction.Interacting_With__c = oAccount.Id;
                oInteraction.Interacting_With_type__c = 'Member';
                insert oInteraction;
                system.assertEquals(oInteraction.Caller_Name__c, 'Test');
            
                livechatvisitor oLivechatvisitor = New livechatvisitor();
                insert oLivechatvisitor;
                livechattranscript lChat = New livechattranscript(LIVECHATVISITORID = oLivechatvisitor.Id, STATUS ='Completed',  StartTime = system.Now().addminutes(-15),EndTime = system.Now().addminutes(-10)); 
                insert lChat;
                system.assertEquals(lChat.STATUS,'Completed');
                
                HPPreChatController_C_HUM.onCommitTranscript('TESTKEY',String.valueOf(testUser.Id));            	
                ID sID = NULL;
                string output = HPPreChatController_C_HUM.findInteractionName(String.valueOf(sID));                        
                list<Error_Log__c> errorLog = [select Error_Message__c, class_name__c, Method_Name__c from Error_Log__c where class_name__c = 'HPPreChatController_C_HUM' and Method_Name__c = 'findInteractionName'];

                for(Error_Log__c elog : errorLog ){
                    if( elog.Error_Message__c.contains('sIntId is either null or not a valid ID null')){
                        system.assertEquals('sIntId is either null or not a valid ID null', elog.Error_Message__c );
                     }                        
                }
                system.assertEquals(null, output);
            Test.stopTest();
        }
    }
    
    /**
    * This method tests logErrors method
    *
    * @param  none
    * @return none
    */
    @isTest static void testlogErrors()
    {
        User testUser = [select LastName from User where Firstname = 'HPPreChatUser'];
        system.assertEquals('test HPPreChat_User',testUser.Lastname);

        system.runAs(testUser)
        {
            Test.startTest();
            HPPreChatController_C_HUM.logErrors('EnterpriseID is null', 'HPPreChatController_C_HUM','findAccountByPersonId');
            list<Error_Log__c> errorLog = [select Error_Message__c, class_name__c, Method_Name__c from Error_Log__c where Method_Name__c = 'findAccountByPersonId' and  class_name__c = 'HPPreChatController_C_HUM' ];
            for(Error_Log__c elog : errorLog ){
                if( elog.Error_Message__c.contains('EnterpriseID is null')){
                    system.assertEquals('EnterpriseID is null',elog.Error_Message__c);
                }                        
            }         
            Test.stopTest();
        }
    }
    
    /**
    * This methods tests goToHUMSaveChatTranscriptPage method
    *
    * @param  none
    * @return none
    */
    @isTest Static void testgoToHUMSaveChatTranscriptPage()
    {
        User testUser = [select LastName from User where Firstname = 'HPPreChatUser'];
        system.assertEquals('test HPPreChat_User',testUser.Lastname);
        system.runAs(testUser)
        {
        Test.startTest();
        HPPreChatController_C_HUM objHPPreChatController_C_HUM = New HPPreChatController_C_HUM();
        objHPPreChatController_C_HUM.goToHUMSaveChatTranscriptPage();
        System.assert(true,objHPPreChatController_C_HUM.goToHUMSaveChatTranscriptPage()!= NULL); 
        Test.stopTest();
        }
    }
}