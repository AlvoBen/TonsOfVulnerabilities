/*****************************************************************************************************************************************************************************
Class Name      : CleanUpUnencryptedImages_T_HUM
Version         : 1.0
Created On      : 11/01/2016
Function        : Test class for CleanUpUnencryptedImages_H_HUM

  Modification Log:             Code Review                    Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Manish Kumar                                                11/01/2016                  Original Version
 * Pallavi R                                                   11/02/2016                  Added logic to EncryptAttachmentTest method           
 * Santhi Mandava                                              03/12/2019                  Removed "DpaaSWebServiceMockImpl" usage.
 *******************************************************************************************************************************/
 
@isTest
private class CleanUpUnencryptedImages_T_HUM
{

   /*
    *  This method is used to create data for test class
    *  @name   setupData
    *  @param  NA
    *  @return Void
    *  @throws NA
    */  
    @testSetup static void setupData()
    {
        User ccsUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        ccsUser.Network_User_Id__c = 'MXK6727';
        insert ccsUser;
        system.assertNotEquals(ccsUser, NULL, 'Value not matched');
        
        System.runAs(ccsUser)
        {
         Account acc = HUMTestDataHelperTest.getAccount();
         acc.name = 'Test Account';
         insert acc;
         System.assert(acc.Id != NULL);
         
         HUMConstants__c constant = new HUMConstants__c(Name='HUMLISTSIZEONE', IntegerValue__c=1);
         insert constant;
         System.assert(constant.Id != NULL);
         
         List<Attachment> lstAttachment = new List<Attachment>();
         for(integer i = 0 ; i < 200 ; i++)
         {
             lstAttachment.add(new Attachment(
                 Body = Blob.valueOf('This is a file text'),
                 Name = 'LoggingAttachment.png' + i,
                 ContentType = 'data:image/png;base64',
                 ParentId = acc.Id
             ));
         }
         
         insert lstAttachment;
         System.assertEquals(lstAttachment.size(), 200);
        }
    }
    
    /*
    *  This method is ued to test encryption of File records from Attachment
    *  @name   encryptAttachmentTest
    *  @param  NA
    *  @return Void
    *  @throws NA
    */ 
    static testmethod void encryptAttachmentTest()
    {
        User oUser = [Select Id, Network_User_Id__c  from User where LastName = 'test User1'];
        System.assertEquals('MXK6727', oUser.Network_User_Id__c);
        
        System.runAs(oUser)
        {
            List<Attachment> attList = [Select Id, Body From Attachment Where ContentType = 'data:image/png;base64' Order By Name];
            System.assert(attList.size() == 200);
            
            Test.startTest();
                List<Attachment> encryptedList = CleanUpUnencryptedImages_H_HUM.getFileEncrypted(attList);
            Test.stopTest();
            
            for(integer i = 0 ; i < encryptedList.size() ; i++)
            {
                System.assertEquals(encryptedList[i].Body.toString(), 'This is a file text');
            }
        }
    }
}