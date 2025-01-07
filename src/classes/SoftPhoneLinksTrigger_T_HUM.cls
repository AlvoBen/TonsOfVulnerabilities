/*******************************************************************************************************************************
Apex Class Name 	: SoftPhoneLinksTrigger_T_HUM 
Version           	: 1.0
Created Date    	: August 20 2019
Function          	: Test class for SoftphoneLinksTriggerHandler_H_HUM

Modification Log: 
* Developer Name           Code Reveiw                     Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Lakshmi Madduri          	                       	 08/20/2019                			Original Version 
*******************************************************************************************************************************/
@isTest
private class SoftPhoneLinksTrigger_T_HUM 
{
    /*
    * Method Name : setUpTestData
    * Description : This method creates test data for test class
    * Return type : None
    * Parameter   : None
    */
    @testsetup static void setUpTestData()
    {
        User testUser = HUMTestDataHelperTest.createTestUser('SoftphoneLinks', 'System Administrator');
        insert testUser;        
        System.assertEquals(testUser.lastname == 'test SoftphoneLinks', true);
        
        System.runAs(testUser)
        {
            HUMTriggerSwitch__c objTriggerSwitch = new HUMTriggerSwitch__c();
            objTriggerSwitch.Name='SoftphoneLinks__c';
            objTriggerSwitch.Exeute_Trigger__c = true;
            insert objTriggerSwitch;
            System.assertEquals(objTriggerSwitch.Name == 'SoftphoneLinks__c', true);
            
            Softphone_Sites__c objSoftPhoneSite = new Softphone_Sites__c();
            objSoftPhoneSite.Sites_External_Id__c='2222';
            objSoftPhoneSite.Name='Mx123';
            insert objSoftPhoneSite;
            System.assertEquals(objSoftPhoneSite.Name=='Mx123', true);
            
            Softphone_Sites__c objSoftPhoneSite2 = new Softphone_Sites__c();
            objSoftPhoneSite2.Sites_External_Id__c='22223';
            insert objSoftPhoneSite2;
            System.assertEquals(objSoftPhoneSite2.Sites_External_Id__c=='22223', true);
            
            SoftphoneLinks__c objSoftPhoneLinks = new SoftphoneLinks__c();
            objSoftPhoneLinks.Sites__c = objSoftPhoneSite.Id;
            objSoftPhoneLinks.Agent__c=1.00;
            objSoftPhoneLinks.ServerName__c='servers'; 
            objSoftPhoneLinks.Name='servers';
            insert objSoftPhoneLinks;
            System.assertEquals(objSoftPhoneLinks.ServerName__c=='servers', true);
            
            SoftphoneLinks__c objSoftPhoneLinks2 = new SoftphoneLinks__c();
            objSoftPhoneLinks2.Sites__c = objSoftPhoneSite.Id;
            objSoftPhoneLinks2.Agent__c=1.00;
            objSoftPhoneLinks2.ServerName__c='servers2';
            objSoftPhoneLinks2.Name='servers2';
            insert objSoftPhoneLinks2;
            System.assertEquals(objSoftPhoneLinks2.ServerName__c=='servers2', true);
        }
    }
    /*
    * Method Name : testUpdate
    * Description : This method updates the test data to fire the trigger.
    * Return type : None
    * Parameter   : None
    */
    @isTest
    static void testUpdate()
    {
        User objUser = [Select id, lastname from user where lastname='test SoftphoneLinks' limit 1];
        System.runAs(objUser)
        {
            Test.startTest();
            Softphone_Sites__c objSites = [SELECT Id, Sites_External_Id__c from Softphone_Sites__c where Sites_External_Id__c='2222' limit 1];            
            SoftphoneLinks__c objLinks = [SELECT Id,Sites__c,Sites__r.Name, Default_Server_Agents__c, ServerName__c, Agent__c from SoftphoneLinks__c where ServerName__c='servers' and Sites__c=:objSites.Id limit 1];
            objLinks.Default_Server_Agents__c=1.00;
            objLinks.ServerName__c='server1';
            update objLinks;
            System.assertEquals(objLinks.ServerName__c=='server1',true);
            
            SoftphoneLinks__c linksToDelete = [SELECT Id, Sites__c,Agent__c from SoftphoneLinks__c where Agent__c>0 limit 1];
            try
            {
                delete linksToDelete;
                System.assert(false);
            }
            catch(Exception e)
            {
                System.assert(true);
            }            
            Test.stopTest();
        }        
    }
}