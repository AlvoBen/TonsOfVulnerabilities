/**************************************************************************************************************
Apex Class Name : ImageViewer_T_HUM
Version         : 1.0
Created On      : 03/16/2015
Function        : Test class for ImageViewer_C_HUM

* Modification Log:
* Developer Name            Code Review                 Date                       Description
*-------------------------------------------------------------------------------------------------------------------------
* Santhi Mandava            24069                       03/16/2015                 Original version
* Santhi Mandava            27512                       05/12/2016                 Changed test class to support sorting feature.
* Manish Kumar                                          07/18/2016                 Worked on REQ-275930. Enabled encryption for logged records 
*****************************************************************************************************************************/
@isTest
private class ImageViewer_T_HUM
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
        }
    }
    
    /*
    * Method Name : testFunctionality
    * Description : This method is used to test main functionality.
    * Return type : void
    * Parameter   : NA
    */
    static testMethod void testFunctionality() 
    {
        User oUser = [Select Id,Network_User_Id__c from User where LastName = 'test User1'];
        System.assertEquals('SXS2222', oUser.Network_User_Id__c);
        
        System.runAs(oUser)
        {
            test.startTest();
            List<Case> lstCases = [Select Id from Case Limit 1];
            Attachment_Log__c attachlogid = LoggingTestDataSetup_T_HUM.createAttachmentLog(lstCases[0].Id);
            
            for(integer i = 0; i<=4;i++)
            {
                LoggingTestDataSetup_T_HUM.createAttachment(attachlogid.Id);
            }
            
            List<Attachment> lstAttchments = [Select Id from Attachment];
            
            List<String> latAttId = new List<String>();
            For(Attachment obj : lstAttchments)
            {
                latAttId.Add(obj.Id);
            }
            String svalue = JSON.serialize(latAttId);
            ApexPages.currentPage().getParameters().put('attachId', attachlogid.Id );
            ApexPages.currentPage().getParameters().put('pageIds', svalue );
            
            ImageViewer_C_HUM obj = new ImageViewer_C_HUM();
            System.assertNotEquals(obj, NULL);
            obj.loadImageViewer();
            System.assertEquals(6, ImageViewer_C_HUM.lstImages.size());
            test.stopTest();
        }
    }
}