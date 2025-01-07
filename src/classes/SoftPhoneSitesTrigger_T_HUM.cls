/*******************************************************************************************************************************
Apex Class Name 	: SoftPhoneSitesTrigger_T_HUM 
Version           	: 1.0
Created Date    	: August 20 2019
Function          	: Test class for SitesTriggerHandler_H_HUM

Modification Log: 
* Developer Name           Code Reveiw                     Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Lakshmi Madduri          	                       	 08/20/2019                			Original Version 
*******************************************************************************************************************************/
@isTest
private class SoftPhoneSitesTrigger_T_HUM 
{
    /*
    * Method Name : setUpTestData
    * Description : This method creates test data for test class
    * Return type : None
    * Parameter   : None
    */
    @testsetup static void setUpTestData()
    {
        User testUser = HUMTestDataHelperTest.createTestUser('SoftphoneSites', 'System Administrator');
        insert testUser;
        System.assertEquals(testUser.lastname == 'test SoftphoneSites', true);
        System.runAs(testUser)
        {
            HUMTriggerSwitch__c objTriggerSwitch = new HUMTriggerSwitch__c();
            objTriggerSwitch.Name='Sites__c';
            objTriggerSwitch.Exeute_Trigger__c = true;
            insert objTriggerSwitch;
            System.assertEquals(objTriggerSwitch.Name == 'Sites__c', true);
            
            Softphone_Sites__c objSoftPhoneSite = new Softphone_Sites__c();
            objSoftPhoneSite.Sites_External_Id__c='123456';
            insert objSoftPhoneSite;
            System.assertEquals(objSoftPhoneSite.Sites_External_Id__c=='123456', true);
                  
            SoftphoneLinks__c objSoftPhoneLinks = new SoftphoneLinks__c();
            objSoftPhoneLinks.Sites__c = objSoftPhoneSite.Id;
            insert objSoftPhoneLinks;
            System.assertEquals(objSoftPhoneLinks.Sites__c==objSoftPhoneSite.Id, true);
                   
            Softphone_Agent__c objSoftPhoneAgent = new Softphone_Agent__c();
            objSoftPhoneAgent.Agent_Extension__c='TestAgent';
            objSoftPhoneAgent.Integration_Update__c = false;
            objSoftPhoneAgent.Default_SoftphoneLinks__c = objSoftPhoneLinks.Id;
            objSoftPhoneAgent.Alternate_SoftphoneLinks__c = objSoftPhoneLinks.Id;
            objSoftPhoneAgent.Sites__c=objSoftPhoneLinks.Sites__c;
            objSoftPhoneAgent.User_Id__c=testUser.Id;
            objSoftPhoneAgent.DB2_Status__c='Ready For Insert';
            insert objSoftPhoneAgent;
            System.assertEquals(objSoftPhoneAgent.Integration_Update__c == false, true);
        }
    }
    /*
    * Method Name : testUpdate
    * Description : This method updates and deletes the data to fire the trigger.
    * Return type : None
    * Parameter   : None
    */
    @isTest static void testUpdate()
    {
        User objUser = [Select id, lastname from user where lastname='test SoftphoneSites' limit 1];
        System.runAs(objUser)
        {
            Test.startTest();
            Softphone_Sites__c objSites = [SELECT Id,Sites_External_Id__c from Softphone_Sites__c limit 1];
            objSites.Sites_External_Id__c = '123';
            update objSites;
            System.assertEquals(objSites.Sites_External_Id__c == '123', true);
            Softphone_Sites__c objSitesDelete = [SELECT Id,Sites_External_Id__c from Softphone_Sites__c where Agents__c != null limit 1];
            try
            {
              delete objSitesDelete;  
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