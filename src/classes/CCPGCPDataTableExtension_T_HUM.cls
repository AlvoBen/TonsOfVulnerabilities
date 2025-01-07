/**
   @Author      : Prasad Jandhyala
   @name        : CCPGCPDataTableExtension_T_HUM
   @CreateDate  : 10/30/2017
   @Description : This test class ensures CCPGCPDataTableExtension_T_HUM coverage
   @Version     : 1.0
   @reference   : CCPGCPDataTableExtension_T_HUM
   
   Modification Log: 
 * Developer Name           Code Review                 Date                         Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Prasad Jandhyala         149219                     10/30/2017                     Original version   
 * Rakesh Patni				319302					   01/30/2019					  REQ-380402 . Claim Hyperlink Changes
 ********************************************************************************************************************************/

@isTest
private with sharing class CCPGCPDataTableExtension_T_HUM {
     /*
    * This method creates test data for test class
    * @param None
    * @return None
    */
    @testSetup static void setUpTestData()
    {
        User testUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert testUser;
        system.assertEquals(testUser.lastname=='test User1',true);
        system.runAs(testUser)
        { 
            Case objCase = HUMTestDataHelperTest.getCase();
            insert objCase;
            system.assertNotEquals(objCase,null,'Value not matched');
    
            List<Attachment_Log__c> caseAttachmentLogList = new List<Attachment_Log__c>();
            Attachment_Log__c attachmentLog;
            for(Integer i =0; i< 8; i++) {
	            attachmentLog = new Attachment_Log__c();
	            attachmentLog.Case__c = objCase.Id;
	            attachmentLog.isDeleted__c = false;
	            if(i<4) {
	                attachmentLog.Attachment_Type__c = 'CRM Attachment';
	                attachmentLog.Source_System__c = 'CRM';
	            }
	            else {
	                attachmentLog.Attachment_Type__c = 'Auth Summary';
	                attachmentLog.Source_System__c = 'CCP';
	            }
            caseAttachmentLogList.add(attachmentLog);
            }
            insert caseAttachmentLogList;
            
            
            List<Attachment_Log_Item__c> caseAttachmentLogItemList = new List<Attachment_Log_Item__c>();
            Attachment_Log_Item__c attachmentLogItem;
            for(Integer i =0; i< 2; i++) {
                attachmentLogItem = new Attachment_Log_Item__c();
                attachmentLogItem.Name = 'Log Item '+String.valueOf(i)+' Name';
                attachmentLogItem.Value__c = 'Log Item '+String.valueOf(i)+' Value';
                attachmentLogItem.Attachment_Log__c = caseAttachmentLogList[(i+20)/4].id;
                caseAttachmentLogItemList.add(attachmentLogItem);
                
            }
            insert caseAttachmentLogItemList;
			HUM_Webservice_Callout__c webCall = new HUM_Webservice_Callout__c(Name = GLOBAL_CONSTANT_HUM.HUMGetClaimSummary_V3, Certificate_Name__c = 'Humana_GuidanceCare_Alderaan', Active__c = true, End_Point_URL__c = 'https://dev-crmservicews.humana.com/ClaimsService_V3/GetClaimsSummary', Timeout__c = '12000', Request_Type__c = 'POST', Content_Type__c = 'application/json');
			insert webCall;
			
			Endpoint_Url_HUM__c oCheckUrl=new Endpoint_Url_HUM__c();
            oCheckURL.name='IMSTicketDetailURL';
            oCheckUrL.Remote_Url__c='test2';
            insert oCheckUrl;
            System.assertEquals(oCheckURL.name,'IMSTicketDetailURL');
			
			Endpoint_Url_HUM__c oCheckUrlApproval =new Endpoint_Url_HUM__c();
            oCheckUrlApproval.name='IMSTicketApprovalURL';
            oCheckUrlApproval.Remote_Url__c='test3';
            insert oCheckUrlApproval;
            System.assertEquals(oCheckUrlApproval.name,'IMSTicketApprovalURL');
        }   
    }
    
    /*
    * This method is used to test the attachment funtionality
    * @param   None
    * @return  None
    */
    static testMethod void testAtts()
    {
		CCPGCPDataTableExtension_C_HUM obj = new CCPGCPDataTableExtension_C_HUM();
        User testUser = [SELECT Id,Name FROM User WHERE LastName='test User1'];
        
        system.runAs(testUser)
        {
            Test.startTest();
            
            Case caseObj = [SELECT Id FROM Case LIMIT 1];
            CCPGCPDataTableExtension_C_HUM ccpGCP = new CCPGCPDataTableExtension_C_HUM();
            list<CCPGCPAttachmentDTO_C_HUM> lstAtts = CCPGCPDataTableExtension_C_HUM.getCCPGCPAttachments(caseObj.Id);
            system.assertEquals( lstAtts.size() > 0, TRUE);
            
            Test.stopTest();
        } 
    }
	/*
	* This method is used to test the claim number funtionality
	* @param   None
	* @return  None
	*/
	static testmethod void testRemote() {
		User testUser = [SELECT Id, Name FROM User WHERE LastName = 'test User1'];
		system.runAs(testUser)
		{
			Test.startTest();
			Case caseObj = [SELECT Id FROM Case LIMIT 1];
			Continuation continueObj2 = (Continuation)CCPGCPDataTableExtension_C_HUM.fetchClaimsSummary('', caseObj.id);
			system.assertEquals(continueObj2, null);
			HUM_Webservice_Callout__c webCall = [Select name from HUM_Webservice_Callout__c limit 1];

			List<string> lstring = new list<string>();
			Object obj;
			Continuation continueObj = (Continuation)CCPGCPDataTableExtension_C_HUM.fetchClaimsSummary('123456', caseObj.id);
			CCPGCPDataTableExtension_C_HUM.processResponse(lstring, obj);
			lstring.add('mockLabel');
			CCPGCPDataTableExtension_C_HUM.processResponse(lstring, obj);

			delete webCall;
			CCPGCPDataTableExtension_C_HUM.fetchClaimsSummary('123456', caseObj.id);

			Test.stopTest();
		}
	}
}