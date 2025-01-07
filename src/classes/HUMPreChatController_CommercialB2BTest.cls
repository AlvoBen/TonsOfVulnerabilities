/*******************************************************************************************************************************
Apex Class Name : HUMPreChatController_CommercialB2BTest 
Version         : 1.0
Created On      : 09/02/2020
Function        : 1. This class serves as testclass for HUMPreChatController_CommercialB2B & HUMPreChatResultWrapper_CommercialB2B
Modification Log: 
* Modification ID  Developer Name          Code Review               Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* 1.0				Akshay Pai     				 	        		09/02/2020	                Original Version(REQ-1133858)
****************************************************************************************************************************/
@isTest
private class HUMPreChatController_CommercialB2BTest
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
        insert lstWebChat;
        system.assertEquals('www.humanachatTestclass.com', String.valueOf([Select Value__c from HUMWebChatConstants__c where Name='TestURL'].Value__c));
        
        List<Account> accountList = new List<Account>();
        Account oAcc = HUMTestDataHelperTest.getAccount();
        oAcc.name = 'AccTest';
        oAcc.Group_Number__c = '1037402785';
        oAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByDeveloperName().get('Group').getRecordTypeId();
        accountList.add(oAcc);
        
        Account oAcc1 = HUMTestDataHelperTest.getAccount();
        oAcc1.name = 'AccTest1';
        oAcc1.Agent_ID__c = '1037402785';
        oAcc1.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByDeveloperName().get('Agent_Broker').getRecordTypeId();
        accountList.add(oAcc1);
        
        insert accountList;	
        List<Account> accountInsertList = [Select Id,Group_Number__c,Agent_ID__c from Account ];
        system.assertEquals(accountInsertList.size(),2);
        system.assertEquals('1037402785', String.valueOf(accountInsertList[0].Group_Number__c));
        system.assertEquals('1037402785', String.valueOf(accountInsertList[1].Agent_ID__c));
                
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
    static testMethod void testPreChatinNonDevMode() 
    {
        User testUser = HUMTestDataHelperTest.createTestUser('SystemAdmin_User','System Administrator');
        system.runAs(testUser)
        {
            //create Account records for testing.
            List<Account> oAccList = [Select Id, FirstName, Enterprise_Id__c from Account order by createdDate];
            system.assertEquals(oAccList.size(), 2);
            
			Policy_Member__c polMem = [Select Id,Name from Policy_Member__c where Member__c=:oAccList[0].Id];
            system.assertNotEquals(polMem.name, Null);
            
            Test.startTest();
            List<LiveChatButton> lstLCB =   [SELECT Id, DeveloperName, MasterLabel FROM LiveChatButton WHERE Type = 'Standard' limit 1];
            Apexpages.currentpage().getparameters().put('buttonId',lstLCB[0].Id);
            HUMPreChatController_CommercialB2B oHUMPreChat = new HUMPreChatController_CommercialB2B();      
            //simulating initialization of variables
            oHUMPreChat.webChatTranscriptDetail = 'test chat';
            oHUMPreChat.initVariable();
            oHUMPreChat.sTrans = 'as';     
                        
            //passing Dummy values to simulate Encryption-Decryption Process.
            String sLsessionID = GLOBAL_CONSTANT_HUM.SESSIONID_LIVEAGENT_HUM ;
            String sSourceSystem = 'IVAWatson';
            String sCustomerType = 'Group';
            String sCustomerId = HUMUtilityHelper.populateEncryptedValue('1037402785');
            String sClassification = 'Home Office (RES)';
            String sIntent = 'Reship';
            String sPolicyNo = HUMUtilityHelper.populateEncryptedValue(polMem.name);
            String sStartDate = '01/01/2015'; 
            String sEndDate = '12/31/2015';
            String sBotTranscript = HUMUtilityHelper.populateEncryptedValue('This is for test class');
            
            //Check for Group customer type and valid sCustomerId
            HUMPreChatResultWrapper_CommercialB2B oWrapper = HUMPreChatController_CommercialB2B.findAccountByCustomerId (sSourceSystem,sCustomerType,sCustomerId, sClassification, sIntent, sPolicyNo, sStartDate, sEndDate,sLsessionID,sBotTranscript);
            system.assertEquals(oWrapper.sAccId, oAccList[0].Id); 
			system.assertEquals(oWrapper.sDecryptLsessionID, '0005004408117');
            system.assertEquals(oWrapper.sDecryptTranscript, 'This is for test class');
            
            //Check for Agent customer type and valid sCustomerId
            sCustomerType = 'Agent';
            HUMPreChatResultWrapper_CommercialB2B oWrapper2 = HUMPreChatController_CommercialB2B.findAccountByCustomerId (sSourceSystem,sCustomerType,sCustomerId, sClassification, sIntent, sPolicyNo, sStartDate, sEndDate,sLsessionID,sBotTranscript);
            system.assertEquals(oWrapper2.sAccId, oAccList[1].Id);
			system.assertEquals(oWrapper2.sDecryptLsessionID, '0005004408117');
            system.assertEquals(oWrapper2.sDecryptTranscript, 'This is for test class');			
            
			//Check for sCustomerId encrypted using invalid algorithm
            sCustomerId = 'aa189c1077319577bb8765f5af0b04e9';
            oWrapper2 = HUMPreChatController_CommercialB2B.findAccountByCustomerId (sSourceSystem,sCustomerType,sCustomerId, sClassification, sIntent, sPolicyNo, sStartDate, sEndDate,sLsessionID,sBotTranscript);
            system.assertEquals(oWrapper2, null); 
            
			//Check for blank/null sCustomerId
            sCustomerId = '';
            oWrapper2 = HUMPreChatController_CommercialB2B.findAccountByCustomerId (sSourceSystem,sCustomerType,sCustomerId, sClassification, sIntent, sPolicyNo, sStartDate, sEndDate,sLsessionID,sBotTranscript);
            system.assertEquals(oWrapper2, null); 
            
			//Check for blank/null sCustomerType
            sCustomerType = '';
            oWrapper2 = HUMPreChatController_CommercialB2B.findAccountByCustomerId (sSourceSystem,sCustomerType,sCustomerId, sClassification, sIntent, sPolicyNo, sStartDate, sEndDate,sLsessionID,sBotTranscript);
            system.assertEquals(oWrapper2, null);			
			
			ApexPages.currentPage().getparameters().put('chatDetails','"InteractionName":"'+oWrapper.sIntId+'"');
            oHUMPreChat.postchatinit();
            
            ApexPages.currentPage().getparameters().put('transcript','test');
            oHUMPreChat.postchatinit();
            
            Test.stopTest();
        }
    }
    
    /**
	* This method tests logErrors method
	*
	* @param  none
	* @return none
	*/
    static testMethod void testlogErrors()
    {
        User testUser = HUMTestDataHelperTest.createTestUser('SystemAdmin_User','System Administrator');
        //Generate Mock Service
        system.runAs(testUser)
        {
            Test.startTest();
            HUMPreChatController_CommercialB2B.logErrors('EnterpriseID is null', 'HUMPreChatController_CommercialB2B','findAccountByCustomerId');
            Test.stopTest();
        }
    }
       
    /**
	* This methods tests goToHUMSaveChatTranscriptPage method
	*
	* @param  none
	* @return none
	*/
    Static testMethod void testgoToHUMSaveChatTranscriptPage()
    {
        Test.startTest();
        HUMPreChatController_CommercialB2B objHUMPreChatController = New HUMPreChatController_CommercialB2B();
        objHUMPreChatController.goToHUMSaveChatTranscriptPage();
        System.assert(true,objHUMPreChatController.goToHUMSaveChatTranscriptPage()!= NULL); 
        Test.stopTest();
    }
}