/***********************************************************************************************************
Apex Class Name  : CaseRelatedInfo_LT_HUM
Version          : 1.0
Function         : Test class for CaseRelatedInfo_LC_HUM
************************************************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Gowthami Thota                           		09/01/2022                 Test class for CaseRelatedInfo_LC_HUM.cls
************************************************************************************************************/
@isTest
public class CaseRelatedInfo_LT_HUM {
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
    	}
   }
    
    /*
    * This method is used to test the attachment funtionality
    * @param   None
    * @return  None
    */
    static testMethod void testAtts()
    {
		CaseRelatedInfo_LC_HUM obj = new CaseRelatedInfo_LC_HUM();
        User testUser = [SELECT Id,Name FROM User WHERE LastName='test User1'];
        
        system.runAs(testUser)
        {
            Test.startTest();
            Case caseObj = [SELECT Id FROM Case LIMIT 1];
            List<Attachment_Log__c> attLog = [SELECT Id,Name from Attachment_Log__c LIMIT1];
            CaseRelatedInfo_LC_HUM.getCaseRelatedInfo(caseObj.Id);
            CaseRelatedInfo_LC_HUM.getCaseNumber(caseObj.Id);
            CaseRelatedInfo_LC_HUM.getAttachmentLogItems(attLog[0].Name);
            Test.stopTest();
        } 
    }


}