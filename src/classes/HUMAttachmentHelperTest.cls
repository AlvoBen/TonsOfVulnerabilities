/****************************************************************************************
Apex Class Name  : HUMAttachmentHelperTest
Version          : 1.0 
Created Date     : September 29, 2014
Function         : Test class to HUMAttachmentHelper Apex class
Modification Log :
 *  Developer       Code Review            Date                 Description
 * ----------------------------------------------------------------------------------------           
 * Sagar Tapadia    18313                09/29/2014              Original Version 
 *******************************************************************************************/
@isTest
private class HUMAttachmentHelperTest {

     /*
     * Method Name : testHUMAttachmentHelper
     * Description : Test method to coverHUMAttachmentHelper
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testHUMAttachmentHelper()
    {
        insert new HUMCipherCloudSettings__c( Name = 'APPLICATION_NAME' , Value__c = 'TEST');
        //Create User
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u;
        system.assert(u.Id != null);
        system.runAs(u)
        {
        	Test.StartTest();
        		HUMAttachmentHelper humAttHelper = new HUMAttachmentHelper();
        		humAttHelper.FileName = 'New';
				system.assert(humAttHelper.FileName != null);
        	Test.StopTest();
        }
    }
}