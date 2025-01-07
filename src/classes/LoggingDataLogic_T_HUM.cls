/**********************************************************************************************************************************************************************
Apex Class Name  : LoggingDataLogic_T_HUM
Version          : 1.0 
Created On       : 03/15/2016
Function         : Test class to LoggingDataLogic_D_HUM apex class

Modification Log :
* Developer Name          Code Review                   Date                       Description
*----------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Santhi Mandava          24069                         03/15/2016                 Original Version
* Santhi Mandava          24308                         04/19/2016                 Defect 223172.0001 fixed. 
* Santhi Mandava          33921                         04/19/2016                 Modified test class as per defect 223172.0001 fix.
* Santhi Mandava          35034                         04/19/2016                 Modified test class as per defect 230816.0001 fix.
* Manish Kumar                                          08/24/2016                 Worked on REQ#275929 for DPaaS error handling.
* Lakshmi Madduri                                       04/20/2021                 US-1911605 SonarQube Fix Validation
* Lakshmi Madduri                                       05/05/2021                 2235919 - Separate constant class for logging framework
***********************************************************************************************************************************************************************/

@isTest
private class LoggingDataLogic_T_HUM
{
    /*
    * Method Name : setup
    * Description : This method is used to create test data for the class 
    * Return type : void
    * Parameter   : NA
    */
    @testSetup static void setup() 
    {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        oUser.Current_Queue__c = 'Cincinnati CRU1';
        oUser.Network_User_Id__c = 'SXS2222';
        insert oUser;
        System.assertEquals(oUser.LastName, 'test User1');
        system.runAs(oUser)
        {
            LoggingTestDataSetup_T_HUM.createTestData();
            LoggingTestDataSetup_T_HUM.createAttachmentLog([Select Id From Case Limit 1].Id);
            LoggingTestDataSetup_T_HUM.createAttachment([Select Id From Attachment_Log__c Limit 1].Id);
            Error_Log__c errorLog = new Error_Log__c(Error_Message__c = 'Test Error',
                Request_Sent__c = false,
                class_name__c = 'Sample Class',
                Method_Name__c = 'Sample Method',
                Type__c = 'CUSTOM_EXCEPTION');
            insert errorLog;
            System.assertNotEquals(Null, errorLog.Id);
        }
    }
    
    /*
     * Method Name : testFunctionality
     * Description : This is the main method to test the over all functionality.
     * Return type : void
     * Paramater   : None
     */ 
    @isTest static void testFunctionality() 
    {
        User oUser = [Select Id,Network_User_Id__c from User where LastName = 'test User1'];
        System.assertEquals('SXS2222', oUser.Network_User_Id__c);
        
        System.runAs(oUser)
        {
            test.startTest();
            Account objAcc = [Select Id,Name from Account Limit 1];
            System.assertEquals('test',objAcc.Name);
            
            List<Case> lstCases = LoggingDataLogic_D_HUM.retrieveAccountCases(objAcc.Id, DateTime.Now(), DateTime.Now(), '', 1000);
            System.assertNotEquals(lstCases, null);
            
            String sEntpId = LoggingDataLogic_D_HUM.getAccountEnterpriseID(objAcc.Id);
            System.assertEquals('123456', sEntpId );
             
            Policy__c objpolicy = [Select Id From Policy__c where Group_Name__c =:objAcc.Id];
            String sAccId = LoggingDataLogic_D_HUM.getAccountIdFromPolicy(objpolicy.Id);
            system.assertEquals(objAcc.Id,sAccId );
            
            Policy_Member__c objpolicymem = [Select Id From Policy_Member__c where Policy__c =:objpolicy.Id];
            sAccId = LoggingDataLogic_D_HUM.getAccountIdFromPolicyMember(objpolicymem.Id);
            system.assertEquals(objAcc.Id,sAccId );
            
            User objUser = LoggingDataLogic_D_HUM.getUser(oUser.Id);
            System.assertEquals('SXS2222',objUser.Network_User_Id__c);
            
            List<Case> lstLogCases = LoggingDataLogic_D_HUM.retrieveCases(objAcc.Id,'Auth:093028246','093028246','CreatedDate' , 'DESC');
            
            System.assertEquals('Cincinnati CRU1',LoggingDataLogic_D_HUM.getUserCurrentQueue());
            
            String queue = LoggingDataLogic_D_HUM.getUserQueue(oUser.Id);
            System.assertEquals(queue,'Cincinnati CRU1');
            
            Error_Log__c errObj = [Select Id,Method_Name__c from Error_Log__c where class_name__c = 'Sample Class'];
            System.assertEquals('Sample Method',errObj.Method_Name__c);

            errObj = LoggingDataLogic_D_HUM.getErrorLogObject(errObj.Id);
            System.assertNotEquals('',errObj.Error_Message__c);
            test.stopTest();
        }
    }
    
    /*
     * Method Name : testsetupFunctionality
     * Description : This is the main method to test functionality specific to logging
     * Return type : void
     * Paramater   : None
     */ 
    @isTest static void testsetupFunctionality() 
    {
        User oUser = [Select Id,Network_User_Id__c from User where LastName = 'test User1'];
        System.assertEquals('SXS2222', oUser.Network_User_Id__c);
        
        System.runAs(oUser)
        {
            test.startTest();
            
            Account objAcc = [Select Id, Name from Account Limit 1];
            System.assertEquals('test',objAcc.Name);
            
            Policy_Member__c objpolicymem = [Select Id From Policy_Member__c limit 1];
            System.assertNotEquals(objpolicymem, null);
            
            Case cse = [Select Id,CaseNumber from Case Limit 1];
            System.assertNotEquals(cse.Id, null);
            
            List<Case> lstCases = LoggingDataLogic_D_HUM.retrieveAccountCases(objAcc.Id, DateTime.Now(), DateTime.Now(), cse.Id, 1000);
            List<Case> lstCasesNeg = LoggingDataLogic_D_HUM.retrieveAccountCases(objAcc.Id, DateTime.Now(), DateTime.Now(), '', null);
            System.assertNotEquals(lstCases, null);
            System.assertNotEquals(lstCasesNeg, null);
            
            List<Case> lstCases1 = LoggingDataLogic_D_HUM.retrievePharmacyOrderCases(objAcc.Id, DateTime.Now(), DateTime.Now(), cse.Id, 1000);
            List<Case> lstCases2 = LoggingDataLogic_D_HUM.retrievePharmacyOrderCases(objAcc.Id, DateTime.Now(), DateTime.Now(), '', 1000);
            List<Case> lstCases3 = LoggingDataLogic_D_HUM.retrievePharmacyOrderCases(objAcc.Id, DateTime.Now(), DateTime.Now(), cse.Id, null);
            List<Case> lstCases4 = LoggingDataLogic_D_HUM.retrievePharmacyInvoiceCases(objAcc.Id, objpolicymem.Id, DateTime.Now(), DateTime.Now(), cse.Id, null);
            System.assertNotEquals(lstCases1, null);
            System.assertNotEquals(lstCases2, null);
            System.assertNotEquals(lstCases3, null);
            System.assertNotEquals(lstCases4, null);
            
            Attachment_Log__c attachLog = [Select Id,isDeleted__c from Attachment_Log__c Limit 1];
            System.assertNotEquals(attachLog.Id, null);
            
            Attachment attach = [Select Id from Attachment Limit 1];
            System.assertNotEquals(attach.Id, null);
            
            List<Attachment_Log__c> lstAttach = LoggingDataLogic_D_HUM.getLogDetails('CaseId', 'CreatedDate', 'DESC');
            System.assertNotEquals(lstAttach, null);
            
            LoggingDataLogic_D_HUM.deleteLogDetails(attachLog.Id);
            System.assertNotEquals(attachLog.isDeleted__c, true);
            
            Integer counter = LoggingDataLogic_D_HUM.getCaseCount(objAcc.Id, DateTime.Now(), DateTime.Now());
            System.assertEquals(counter, 0);
            
            String caseNum = LoggingDataLogic_D_HUM.getCaseNumber(cse.Id);
            System.assertEquals(caseNum, cse.caseNumber);
            
            Case cse1 = LoggingDataLogic_D_HUM.getCaseDetails(cse.Id);
            System.assertEquals(cse1.Id, cse.id);
            
            List<Attachment> lstAttachment = LoggingDataLogic_D_HUM.getAttachmentDetails(attachLog.Id);
            System.assertEquals(lstAttachment.size(), 2);
            
            ID attachId = LoggingDataLogic_D_HUM.insertAttachementLogRecord(New Attachment_Log__c(
                                                                Attachment_Description__c ='Sample Description1',
                                                                Attachment_Key__c ='Sample Key1',
                                                                Attachment_Type__c ='Auth Summary1',
                                                                Attachment_Sub_type__c = 'Auth:093028246',
                                                                Case__c = cse.Id,
                                                                Source_System__c = 'CRM',
                                                                Created_By__c = UserInfo.getName()));
            System.assertNotEquals(attachId, NULL);
            
            LoggingDataLogic_D_HUM.insertAttachementRecord(new Attachment(
                                                            Body = Blob.valueOf('screenshotData'),
                                                            ContentType = 'data:image/png;base64',
                                                            Description = '',
                                                            Name = 'TestAttachment',
                                                            OwnerId = UserInfo.getUserId(),
                                                            parentId = attachLog.Id));
            
            List<Attachment_Log__c> lstAttachLog = LoggingDataLogic_D_HUM.getAttachmentRecord(attach.Id);
            System.assertEquals(lstAttachLog.size(), 0);

            List<Attachment_Log__c> lstAttachCheck = LoggingDataLogic_D_HUM.getLogDetails(cse.Id, 'CreatedDate', 'DESC');
            System.assertEquals(lstAttachCheck.size(), 1);
            List<Attachment_Log__c> lstAttachLogInserted = LoggingDataLogic_D_HUM.getAttachmentRecord(attachId);
            System.assertEquals(lstAttachLogInserted.size(), 1);
            
            LoggingDataLogic_D_HUM.updateAttachmentRecord(new List<Attachment_Log__c>{attachLog});
            
            test.stopTest();
        }
    }
}