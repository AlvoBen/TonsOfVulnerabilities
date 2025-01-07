/**********************************************************************************************************************
Apex Class Name  : HUMAttachmentLogExtensionTest
Version          : 1.0
Created Date     : August 7 2014
Function         : Test class for HUMAttachmentLogExtension
 ****************************************************************************************************************************

 *Modification Log:
 * Developer Name                Code Review           Date                       Description
 *--------------------------------------------------------------------------------------------------------------------------*
 * Keshika Gupta                17734                 08/07/2014                   Original Version
 * Pradeep Raju                 17734                 08/07/2014                   Updated the code to test record fetching
 * Pranali Choudhari            17734                 09/01/2014                   Updated and rewrote code according to best practices
 * Santhi Mandava               20576                 01/28/2015                   Code Modified to fix test class failure Defect # 164139   
 * Santhi Mandava                                     03/12/2019                   Removed "DpaaSWebServiceMockImpl" usage.
 ****************************************************************************************************************************/

@isTest
private class HUMAttachmentLogExtensionTest
{

    /*
     * Method Name   : testRetreiveAttachmentsSuccess
     * Description   : method which tests the attachment functionality logged in as customer care specialist which results in success 
     * Return Type   : void
     * Parameters    : NA
     */ 
    static testMethod void testRetreiveAttachmentsSuccess()
    { 
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u;    
        HUMAttachmentLogExtension attachmentLog;
        system.runAs(u)
        {
            Test.setCurrentPage(Page.HUMAttachmentLog);
            //Create Case
            Case caseRec = HUMTestDataHelperTest.getCase();
            insert caseRec;

            Attachment_Log__c aLog=HUMTestDataHelperTest.getAttachLog();
            alog.Case__c = caseRec.id;
            insert alog;

            Attachment_Log_Item__c aLogItem=HUMTestDataHelperTest.getAttLogItem();
            aLogItem.Attachment_Log__c = aLog.Id;
            insert aLogItem ;

            Test.startTest();
            System.currentPageReference().getParameters().put('recordID',aLog.Id);
            ApexPages.StandardController sc2 = new ApexPages.StandardController(caseRec);
            attachmentLog = new HUMAttachmentLogExtension(sc2);
            attachmentLog.doRetrieveAttachResults();
            attachmentLog.doRetrieveAttachItemResults();
            attachmentLog.sSortExpression  =  'Attachment_Type__c';
            attachmentLog.sSortExpression  =  'Attachment_Type__c';
            attachmentLog.doRetrieveAttachResults();
            List<Error_Log__c> lstErr=attachmentLog.lstLogErrors;
            attachmentLog.lstLogErrors = new List<Error_Log__c>();
            lstErr = attachmentLog.lstLogErrors;            
            Test.stopTest();
        }
        //System.AssertNotEquals(attachmentLog.lstAttachmentRec.size(), 0);
        //System.assertNotEquals(attachmentLog.lstAttachmentItemRec.size(),0);        
    }   

    /*
     * Method Name   : testAttachmentWrongInputs
     * Description   : method which tests the attachment functionality logged in as customer care specialist with wrong inputs given
     * Return Type   : void
     * Parameters    : NA
     */
    static testMethod void testAttachmentWrongInputs()
    {
        Case c = new Case();
        ApexPages.StandardController sc = new ApexPages.StandardController(c);
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u;
        HUMAttachmentLogExtension attachmentLog;
        Boolean bflag;
        system.runAs(u)
        {
            Test.startTest();
            Test.setCurrentPage(Page.HUMAttachmentLog);
            ApexPages.StandardController sc1 = new ApexPages.StandardController(u );
            attachmentLog = new HUMAttachmentLogExtension(sc1);                     
            attachmentLog = new HUMAttachmentLogExtension(sc);
            attachmentLog.doRetrieveAttachResults();
            attachmentLog.getSortDirection();
            attachmentLog.setSortDirection(label.HumAscendingLiteral);
            attachmentLog.sSortExpression  =  'test';
            attachmentLog.getSortDirection();
            attachmentLog.sSortExpression = ''; 
            attachmentLog.doRetrieveAttachResults();                     
            attachmentLog.getSortDirection();           
            System.currentPageReference().getParameters().put('recordID','123434');
            attachmentLog.doRetrieveAttachItemResults();
            Test.stopTest();  
            List<Apexpages.Message> lstMesgs = Apexpages.getMessages();
            bflag = false;
            for(Apexpages.Message msg : lstMesgs)
            {
                if(msg.getDetail().contains(Label.HUM_AttachmentLogInfoError)) bflag = true;
            }          
            System.assert(bflag);
        }
    }
}