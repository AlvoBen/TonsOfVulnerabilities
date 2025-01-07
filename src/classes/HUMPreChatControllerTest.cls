/*******************************************************************************************************************************
Apex Class Name : HUMPreChatControllerTest 
Version         : 1.0
Created On      : 09/26/2014
Function        : 1. This class serves as testclass for HUMPreChatController.
Modification Log: 
 * Modification ID  Developer Name          Code Review               Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* 1.0              Ninad Patil                                     09/26/2014                 Original Version
* 1.1              Ninad Patil             18240                   09/29/2014                 Fixed Internal code reveiw comment.
* 1.2              Ninad Patil             18240                   09/30/2014                 Fixed Code reveie comments.
* 1.3              Sagar Tapadia           19007                   11/05/2014                 Parameter names changed in method.
* 1.4              Sagar Tapadia           20293                   01/12/2015                 Added new test method to check the encryption of body.
* 1.5              Sagar Tapadia           20302                   01/13/2015                 Updated encryption method with new parameters.
* 1.6              Vishal Verma            20350                   01/15/2015                 Updated test class for custom settings data change from Humana Vitality to HumanaVitality
* 1.7              Sagar Tapadia           23562                   05/28/2015                 CA5027355:Updated code to handle creation of Custom record of transcript without body.
* 1.7              Shreya Choodamani       25923                   08/28/2015                 made changes to pass the interaction number(name) in the URL for survey monkey as per REQ - 205290.
* 1.8              Sagar Tapadia           27486                   11/02/2015                 REQ-217639: Initalization of Custom Setting at line 30,63 and 64. Also did some changes related to best practices.
* 1.9              Pradeepkumar Dani       34678                   07/29/2016                 Updated changes for - REQ - 270013
* 1.10             Pradeepkumar Dani       34678                   08/02/2016                 Updated class with APEX standards - REQ - 270091
* 1.9              Muralidhar Kollu                                02/23/2017                 REQ - 301488: Add additional logging to help diagnose interaction id issue in prod  
* 1.11             Avinash Choubey         70261                   02/21/2017                 REQ - 301383  put button id in page parameter before calling initVariable method
* 1.12             Muralidhar Kollu        80609                   02/28/2017                 REQ - 306319: Post Web Chat - Additional Debugging on empty interactionId (CA Tix 6197943 and 6351830) 
* 1.13             Santhi Mandava          90926                   05/09/2017                 Modified test class to cover logInteractionDetails method.
* 1.14             Joel George             94916                   05/22/2017                 Modified Test Class to cover logPreChatVariables method. 
* 1.15             Vishal Verma            99430                   06/06/2017                 REQ - 320399 : CA 6197943 -WebChat/Survey Monkey missing Interaction Id fix.
* 1.16             Vishal Verma            110849                  07/17/2017                 REQ - 323377 : CA 6917822 - WebChat / Survey Monkey missing Interaction Id fix
* 1.17             Rajesh Chandgothia	   140400		           10/18/2017			      REQ - 332357 increase Test Coverage
* 1.18             Rajesh C                157145                  12/08/2017                 REQ - 348289 Populate Skill Name in Live Agent Window And Visitor Tab (MyHumana)
* 1.19             Rajesh C        		   171913 	               01/11/2018                 REQ - 350669 CR735: Securing the Save Chat Option - Live Chat & Post Chat Windows 
* 1.20             Akshay Pai                                      07/16/2020                 REQ - 1292117 IVA NINA - Member Secure chat changes
* 1.21             Vishnu Pilli                                    07/29/2021                 MF-22 Accolade Impact Member changes
****************************************************************************************************************************/
@isTest
private class HUMPreChatControllerTest 
{
    /**
    * This method creates test data for test class
    *
    * @param  none
    * @return none
    */
    @testSetup static void setUpTestData()
    {
        List<HUMWebChatConstants__c> lstWebChat = new List<HUMWebChatConstants__c>();
        lstWebChat.add(new HUMWebChatConstants__c(name='TestURL',value__c='www.humanachatTestclass.com'));
        lstWebChat.add(new HUMWebChatConstants__c(name='EnableInteractionLogging',value__c='true'));
        lstWebChat.add(new HUMWebChatConstants__c(name='LogSMClick',value__c='true'));
        lstWebChat.add(new HUMWebChatConstants__c(name='LogPreChat',value__c='true'));
        insert lstWebChat;        
        system.assertEquals('www.humanachatTestclass.com', String.valueOf([Select Value__c from HUMWebChatConstants__c where Name='TestURL'].Value__c));
        system.assertEquals('true', String.valueOf([Select Value__c from HUMWebChatConstants__c where Name='EnableInteractionLogging'].Value__c));
        system.assertEquals('true', String.valueOf([Select Value__c from HUMWebChatConstants__c where Name='LogSMClick'].Value__c));
        system.assertEquals('true', String.valueOf([Select Value__c from HUMWebChatConstants__c where Name='LogPreChat'].Value__c));
        
        Account oAcc = HUMTestDataHelperTest.getAccount();
        oAcc.firstname = 'AccTest';
        oAcc.lastName = 'AccLast';
        oAcc.enterprise_id__C = '1037402785';
        oAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
        insert oAcc;
        system.assertEquals('1037402785', String.valueOf([Select Enterprise_Id__c from Account where Id=:oAcc.Id LIMIT 1].Enterprise_Id__c));
        
        Interaction__C oInt = new Interaction__C();
        oInt.Caller_name__C = 'Test Caller';
        oInt.Interacting_with_type__C = 'Member';
        oInt.Interacting_with__c = oAcc.Id;
        oInt.Interaction_Origin__c = 'Inbound Call';
        insert oInt ;
        system.assertEquals(oInt.id, [Select Id from Interaction__c where Interacting_With__c=:oAcc.Id LIMIT 1].Id);
        
        Policy__c oMedicalPolicy = HUMTestDataHelperTest.getPolicy();
        oMedicalPolicy.Name = 'Medical Product';
        oMedicalPolicy.source_cust_cov_key__c = '12345678901234567890' ; 
        oMedicalPolicy.Group_Name__c = oAcc.Id;
        oMedicalPolicy.Major_LOB__c ='EPO';          
        insert oMedicalPolicy;
        system.assertEquals(oMedicalPolicy.Name == 'Medical Product', true);
        
        Policy_Member__c objMedPolMem = HUMTestDataHelperTest.getPolicyMember();
        objMedPolMem.Member__c = oAcc.Id;
        objMedPolMem.Policy__c = oMedicalPolicy.Id;
        objMedPolMem.Relationship__c = 'self';    
        objMedPolMem.end_date__c = '12/31/2015'; 
        objMedPolMem.Effective_Date__c='01/01/2015';       
        objMedPolMem.Policy_Member_External_ID__c = '00|ODS|123456789|89|89|02-01-2001';
        insert objMedPolMem;
        system.assertEquals(objMedPolMem.Relationship__c == 'self',true);
        
        Classification_Type__c ctRec = New Classification_Type__c();
        ctRec.name = 'Humana Pharmacy Calls';
        ctRec.Is_Active__c=true;
        insert ctRec;
        system.assertEquals(ctRec.Name == 'Humana Pharmacy Calls',true);
        
        Classification__c classificationRec = New Classification__c();
        classificationRec.Name = 'Home Office (RES)';
        classificationRec.Is_Active__c = true;
        insert classificationRec;
        system.assertEquals(classificationRec.Name == 'Home Office (RES)',true);
        
        Intent__c intentRec = New Intent__c();
        intentRec.Name = 'Reship';
        intentRec.Is_Active__c = true;
        insert intentRec;
        system.assertEquals(intentRec.Name == 'Reship',true);
        
        CTCI_Junction__c ctciRec = New CTCI_Junction__c();
        ctciRec.Classification_Type__c = ctRec.id;
        ctciRec.Classification__c = classificationRec.Id;
        ctciRec.Intent__c = intentRec.id;
        ctciRec.IsActive__c = true;
        insert ctciRec;
        system.assertEquals(ctciRec.Intent__c == intentRec.id,true);
    }
    
    /**
    * This method tests Perchat page functionality in non developer mode.
    *
    * @param  none
    * @return none
    */
    @isTest static void testPreChatinNonDevMode() 
    {
        User testUser = HUMTestDataHelperTest.createTestUser('SystemAdmin_UserHP','System Administrator');
        System.assertEquals('test SystemAdmin_UserHP', testUser.LastName);
        system.runAs(testUser)
        {
            //create Account records for testing.
            Account oAcc = [Select Id, FirstName, Enterprise_Id__c from Account limit 1];
            Interaction__c oInt = [Select Name from Interaction__c where Interacting_With__c=:oAcc.Id];
            Policy_Member__c polMem = [Select Id,Name from Policy_Member__c where Member__c=:oAcc.Id];
            system.assertNotEquals(polMem.name, Null);
            
            Test.startTest();
            List<LiveChatButton> lstLCB =   [SELECT Id, DeveloperName, MasterLabel FROM LiveChatButton WHERE Type = 'Standard' limit 1];
            Apexpages.currentpage().getparameters().put('buttonId',lstLCB[0].Id);
            HUMPreChatController oHUMPreChat = new HUMPreChatController();      
            //simulating initialization of variables on Post Chat.
            oHUMPreChat.webChatTranscriptDetail = 'test chat';
            oHUMPreChat.initVariable();
            oHUMPreChat.BtnName ='RSO';
            oHUMPreChat.sTrans = 'as';
            
            ApexPages.currentPage().getparameters().put('attachedRecords','"Interaction__c":"'+oInt.id+'"');
            oHUMPreChat.postchatinit();
            
            ApexPages.currentPage().getparameters().put('transcript','test');
            oHUMPreChat.postchatinit();
            
            //passing Dummy value of Decrypted Person Id to simulate Encryption-Decryption Process.
            String sEnterpriseID = HUMUtilityHelper.populateEncryptedValue('1037402785');
            String sMemberGenKey = GLOBAL_CONSTANT_HUM.MEMGENKEY_LIVEAGENT_HUM;
            String sLsessionID = GLOBAL_CONSTANT_HUM.SESSIONID_LIVEAGENT_HUM ;
            String sSourceSystem = 'NINA';
            String sCustomerType = 'Member';
            String sCustomerId = HUMUtilityHelper.populateEncryptedValue('1037402785');
            String sClassification = 'Home Office (RES)';
            String sIntent = 'Reship';
            String sPolicyNo = HUMUtilityHelper.populateEncryptedValue(polMem.name);
            String sStartDate = '01/01/2015'; 
            String sEndDate = '12/31/2015';
            String sBotTranscript = HUMUtilityHelper.populateEncryptedValue('This is for test class');
            String sVendorIdentifier = 'D97F41349DF0F1F88EBB6DEF45A98D1D1750BEC7C41BD61289F5BFECBA87AC70';
            String sVendorBenefitCode = '4C6C5522BE76403E7E1374B342A233B602B9AB62FF9326FB0BB529403223440E';
            String sMemberIdBase = 'B84ECBE1AB0146B0BA675B544E9B44586E15CCDC64E5EE8B7EB90796F4B01A3F';
            String sMemberIdDependentCode = '1CEC1E745645CA226E18B858840BBDF8B198C1BEF27758ADD0A8B5D032F47DB5';
            
            //Check whether customer Id is mapping to correct account or not when source system available
            HUMPreChatResultWrapperHelper oWrapper = HUMPreChatController.findAccountByPersonId (sEnterpriseID,sSourceSystem,sCustomerType,sCustomerId, sClassification, sIntent, sPolicyNo, sStartDate, sEndDate);   
            system.assertEquals(oWrapper.sAccId, oAcc.Id); 
            
            //Check whether customer Id is mapping to correct account or not when passed wrong classification and intent combination
            sClassification = 'Home Offices (RES)';
            HUMPreChatResultWrapperHelper oWrapper3 = HUMPreChatController.findAccountByPersonId (sEnterpriseID,sSourceSystem,sCustomerType,sCustomerId, sClassification, sIntent, sPolicyNo, sStartDate, sEndDate);   
            system.assertEquals(oWrapper3.sAccId, oAcc.Id);
            
            //Check whether customer Id is mapping to correct account or not when passed wrong policy member details
            sClassification = 'Home Office (RES)';
            sEndDate = '12/31/2016';
            HUMPreChatResultWrapperHelper oWrapper4 = HUMPreChatController.findAccountByPersonId (sEnterpriseID,sSourceSystem,sCustomerType,sCustomerId, sClassification, sIntent, sPolicyNo, sStartDate, sEndDate);   
            system.assertEquals(oWrapper4.sAccId, oAcc.Id);
            
            //Check whether customer Id is mapping to correct account or not when passed wrong policy member details and classification & intent combination
            sClassification = 'Home Offices (RES)';
            HUMPreChatResultWrapperHelper oWrapper5 = HUMPreChatController.findAccountByPersonId (sEnterpriseID,sSourceSystem,sCustomerType,sCustomerId, sClassification, sIntent, sPolicyNo, sStartDate, sEndDate);   
            system.assertEquals(oWrapper5.sAccId, oAcc.Id); 
            
            //Check whether Enterprise Id is mapping to correct account or not when source system not available
            sSourceSystem = '';
            HUMPreChatResultWrapperHelper oWrapper1 = HUMPreChatController.findAccountByPersonId (sEnterpriseID,sSourceSystem,sCustomerType,sCustomerId, sClassification, sIntent, sPolicyNo, sStartDate, sEndDate);   
            system.assertEquals(oWrapper1.sAccId, oAcc.Id); 
            
            //Check decryption of all given eliments
            HUMPreChatResultWrapperHelper oWrapper2=HUMPreChatController.decryptUserIdentifiers(sMemberGenKey,sLsessionID,sBotTranscript);
            system.assertEquals(oWrapper2.sDecryptLsessionID, '0005004408117');
            system.assertEquals(oWrapper2.sDecryptTranscript, 'This is for test class');
            
            //Check decryption of all given eliments
            HUMPreChatResultWrapperHelper objWrapper=HUMPreChatController.decryptClientUserData(sMemberGenKey, sLsessionID, sBotTranscript, sVendorIdentifier, sVendorBenefitCode, sMemberIdBase, sMemberIdDependentCode);
            system.assertEquals(objWrapper.sDecryptLsessionID, '0005004408117');
            system.assertEquals(objWrapper.sDecryptTranscript, 'This is for test class');
            system.assertEquals(objWrapper.sDecryptAcclId, 'ACCL');
            system.assertEquals(objWrapper.sDecryptAcclBenefitCode, 'V-AIMP');
            system.assertEquals(objWrapper.sDecryptMemberId, '114706757');
            system.assertEquals(objWrapper.sDecryptDependentCode, '05');
            
            //checking negative test cases. Passing null value as String to be decrypted.
            sEnterpriseID = '';
            oWrapper = HUMPreChatController.findAccountByPersonId(sEnterpriseID,sSourceSystem,sCustomerType,sCustomerId, sClassification, sIntent, sPolicyNo, sStartDate, sEndDate); 
            system.assertNotEquals(oWrapper, oWrapper2);     
            
            //checking negative test cases. Passing wrong encrypted value as String to be decrypted.
            sEnterpriseID = 'BFF8C711E102400AF195995CB311346B1DEEE35B3804BBD43C440BC66FF5';
            oWrapper = HUMPreChatController.findAccountByPersonId(sEnterpriseID,sSourceSystem,sCustomerType,sCustomerId, sClassification, sIntent, sPolicyNo, sStartDate, sEndDate); 
            system.assertNotEquals(oWrapper, oWrapper2);
            
            oWrapper = HUMPreChatController.findAccountByPersonId('f197ffcdc8fb91d19483dcd112ed2e417599866750584a10a403a1064874b83a0fe6758c69507b72e5c85477ed6e81f5',sSourceSystem,sCustomerType,sCustomerId, sClassification, sIntent, sPolicyNo, sStartDate, sEndDate); 
            system.assertNotEquals(oWrapper, oWrapper2); 
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
        User testUser = HUMTestDataHelperTest.createTestUser('SystemAdmin_User1','System Administrator');
        System.assertEquals('test SystemAdmin_User1', testUser.LastName);
        //Generate Mock Service
        system.runAs(testUser)
        {
            Test.startTest();
            Account oAccount = HUMTestDataHelperTest.getAccount();
            oAccount.OwnerId = testUser.id;
            oAccount.LastName = 'LastName';
            oAccount.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            
            insert oAccount;
            Interaction__c oInteraction = HUMTestDataHelperTest.getInteraction();
            oInteraction.Interacting_With__c = oAccount.Id;
            oInteraction.Interacting_With_type__c = 'Member';
            insert oInteraction;
            
            livechatvisitor oLivechatvisitor = New livechatvisitor();
            insert oLivechatvisitor;
            livechattranscript lChat = New livechattranscript(LIVECHATVISITORID = oLivechatvisitor.Id, STATUS ='Completed',  StartTime = system.Now().addminutes(-15),EndTime = system.Now().addminutes(-10)); 
            insert lChat;
            HUMPreChatController.onCommitTranscript('TESTKEY',String.valueOf(testUser.Id));
            Humana_Chat_Transcript__c objCustomTranscript = [SELECT Id,OwnerId,Chat_Key__c from Humana_Chat_Transcript__c where OwnerId=:testUser.Id];
            System.assertEquals('TESTKEY', objCustomTranscript.Chat_Key__c);
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
        User testUser = HUMTestDataHelperTest.createTestUser('SystemAdmin_User2','System Administrator');
        System.assertEquals('test SystemAdmin_User2', testUser.LastName);
        //Generate Mock Service
        system.runAs(testUser)
        {
            Test.startTest();
            HUMPreChatController.logErrors('EnterpriseID is null', 'HUMPreChatController','findAccountByPersonId');
            Error_Log__c objError = [SELECT Id,Error_Message__c,class_name__c from Error_Log__c where class_name__c='HUMPreChatController' and Method_Name__c='findAccountByPersonId'];
            System.assertEquals('EnterpriseID is null', objError.Error_Message__c);
            Test.stopTest();
        }
    }
    
    /**
    * This methods tests logInteractionError method
    *
    * @param  none
    * @return none
    */
    @isTest static void testlogInteractionError()
    {
        User testUser = HUMTestDataHelperTest.createTestUser('SystemAdmin_User3','System Administrator');
        System.assertEquals('test SystemAdmin_User3', testUser.LastName);
        //Generate Mock Service
        system.runAs(testUser)
        {
            Test.startTest();
            HUMPreChatController.logInteractionDetails('Page Parameters','Header Parameters','https://www.surveymonkey.com/r/MyHumanaChat','');
            HUMPreChatController.logInteractionDetails('Page Parameters','Header Parameters','https://www.surveymonkey.com/r/MyHumanaChat','12345');
            List<Error_Log__c> lstError = [SELECT Id,class_name__c,Method_Name__c from Error_Log__c where class_name__c='HUMPreChatController' and Method_Name__c='logInteractionDetails'];
            System.assert(lstError.size()>0);
            Test.stopTest();
        }
    }
    
    /**
    * This methods tests logPreChatVariables method
    *
    * @param  none
    * @return none
    */
    @isTest static void testlogPreChatVariables()
    {
        User testUser = HUMTestDataHelperTest.createTestUser('SystemAdmin_User4','System Administrator');
        System.assertEquals('test SystemAdmin_User4', testUser.LastName);
        system.runAs(testUser)
        {
            Error_Log__c errObj = new Error_Log__c();
            String errMesg = 'sCustomAccountId: Custom AccountTest , sAccountId: Account TestID ,  sIntId: IntTestID , sIntMemberId: IntMember TestID , sInteractionName: Member Name';
            Test.startTest();
            HUMPreChatController.logPreChatVariables('Custom AccountTest','Account TestID','IntTestID','IntMember TestID','Member Name');
            errObj = [Select Id,Error_Message__c from Error_Log__c ORDER BY CreatedDate Limit 1];
            system.assertEquals(errObj.Error_Message__c,errMesg);
            
            Test.stopTest();
        }
    } 
    
    /**
    * This methods tests testlogPreChatVariablesException method
    *
    * @param  none
    * @return none
    */
    @isTest Static void testlogPreChatVariablesException()
    {
        Test.startTest();
        ApexPages.currentPage().getParameters().put('buttonId','test');
        HUMPreChatController oHUMPreChat = New HUMPreChatController();  
        System.assertEquals(NULL, NULL);
        ApexPages.currentPage().getHeaders().put('Key','test');
        oHUMPreChat.postchatinit();
        System.assertEquals(ApexPages.hasMessages(), true);
        Test.stopTest();
    } 
    
    /**
    * This methods tests goToHUMSaveChatTranscriptPage method
    *
    * @param  none
    * @return none
    */
    @isTest Static void testgoToHUMSaveChatTranscriptPage()
    {
        Test.startTest();
        HUMPreChatController objHUMPreChatController = New HUMPreChatController();
        objHUMPreChatController.goToHUMSaveChatTranscriptPage();
        System.assert(true,objHUMPreChatController.goToHUMSaveChatTranscriptPage()!= NULL); 
        Test.stopTest();
    }
}