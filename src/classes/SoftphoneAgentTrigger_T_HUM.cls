/*******************************************************************************************************************************
Apex Class Name 	: SoftphoneAgentTrigger_T_HUM 
Version           	: 1.0
Created Date    	: August 20 2019
Function          	: Test class for AgentTriggerHandler_H_HUM

Modification Log: 
* Developer Name           Code Reveiw                     Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Lakshmi Madduri          	                       	 08/20/2019                			Original Version 
*******************************************************************************************************************************/
@isTest
private class SoftphoneAgentTrigger_T_HUM 
{
    /*
    * Method Name : setUpTestData
    * Description : This method creates test data for test class
    * Return type : None
    * Parameter   : None
    */
    @testsetup static void setUpTestData()
    {        
        User testUser = HUMTestDataHelperTest.createTestUser('SoftphoneUsers', 'System Administrator');
        insert testUser;
        System.assertEquals(testUser.lastname == 'test SoftphoneUsers', true);
        System.runAs(testUser)
        {
            HUMTriggerSwitch__c objTriggerSwitch = new HUMTriggerSwitch__c();
            objTriggerSwitch.Name='Agent__c';
            objTriggerSwitch.Exeute_Trigger__c = true;
            insert objTriggerSwitch;
            System.assertEquals(objTriggerSwitch.Name == 'Agent__c', true);
            
            Softphone_Sites__c objSoftPhoneSite = new Softphone_Sites__c();
            objSoftPhoneSite.Sites_External_Id__c='12345';
            insert objSoftPhoneSite;
            System.assertEquals(objSoftPhoneSite.Sites_External_Id__c == '12345', true);
            
            SoftphoneLinks__c objSoftPhoneLinks = new SoftphoneLinks__c();
            objSoftPhoneLinks.Sites__c = objSoftPhoneSite.Id;
            insert objSoftPhoneLinks;
            System.assertEquals(objSoftPhoneLinks.Sites__c == objSoftPhoneSite.Id, true);
            
            Softphone_Sites__c objSoftPhoneSite2 = new Softphone_Sites__c();
            objSoftPhoneSite2.Sites_External_Id__c='123456';
            insert objSoftPhoneSite2;
            System.assertEquals(objSoftPhoneSite2.Sites_External_Id__c=='123456', true);
            
            SoftphoneLinks__c objSoftPhoneLinks2 = new SoftphoneLinks__c();
            objSoftPhoneLinks2.Sites__c = objSoftPhoneSite2.Id;
            insert objSoftPhoneLinks2;
            System.assertEquals(objSoftPhoneLinks2.Sites__c == objSoftPhoneSite2.Id, true);
            
            Softphone_Agent__c objSoftPhoneAgent = new Softphone_Agent__c();
            objSoftPhoneAgent.Agent_Extension__c='TestExt';
            objSoftPhoneAgent.Integration_Update__c = false;
            objSoftPhoneAgent.Default_SoftphoneLinks__c = objSoftPhoneLinks.Id;
            objSoftPhoneAgent.Alternate_SoftphoneLinks__c = objSoftPhoneLinks.Id;
            objSoftPhoneAgent.Sites__c=objSoftPhoneLinks.Sites__c;
            objSoftPhoneAgent.User_Id__c=testUser.Id;
            objSoftPhoneAgent.DB2_Status__c='Ready For Insert';
            insert objSoftPhoneAgent;
            System.assertEquals(objSoftPhoneAgent.Integration_Update__c == false, true);
            
            //else if condition
            Softphone_Agent__c objSoftPhoneAgent2 = new Softphone_Agent__c();
            objSoftPhoneAgent2.Agent_Extension__c='TestExt2';
            objSoftPhoneAgent2.Integration_Update__c = true;
            objSoftPhoneAgent2.Default_SoftphoneLinks__c = objSoftPhoneLinks2.Id;
            objSoftPhoneAgent2.Alternate_SoftphoneLinks__c = objSoftPhoneLinks2.Id;
            objSoftPhoneAgent2.Sites__c=objSoftPhoneLinks2.Sites__c;
            objSoftPhoneAgent2.User_Id__c='123456456456765';
            objSoftPhoneAgent2.DB2_Status__c='Ready For Delete';
            insert objSoftPhoneAgent2;
            System.assertEquals(objSoftPhoneAgent2.DB2_Status__c == 'Ready For Delete', true);
            
            Softphone_Agent__c objSoftPhoneAgent3 = new Softphone_Agent__c();
            objSoftPhoneAgent3.Agent_Extension__c='TestExt3';
            objSoftPhoneAgent3.Integration_Update__c = false;
            objSoftPhoneAgent3.Default_SoftphoneLinks__c = objSoftPhoneLinks2.Id;
            objSoftPhoneAgent3.Alternate_SoftphoneLinks__c = objSoftPhoneLinks2.Id;
            objSoftPhoneAgent3.Sites__c=objSoftPhoneLinks2.Sites__c;
            objSoftPhoneAgent3.User_Id__c='12345645645676567';
            objSoftPhoneAgent3.DB2_Status__c='Ready For Insert';
            objSoftPhoneAgent3.DB2_Sync__c=true;
            insert objSoftPhoneAgent3;
            System.assertEquals(objSoftPhoneAgent3.DB2_Status__c == 'Ready For Insert', true);
            
            Softphone_Agent__c objSoftPhoneAgent4 = new Softphone_Agent__c();
            objSoftPhoneAgent4.Agent_Extension__c='TestDel';
            objSoftPhoneAgent4.Integration_Update__c = false;
            objSoftPhoneAgent4.Default_SoftphoneLinks__c = objSoftPhoneLinks2.Id;
            objSoftPhoneAgent4.Alternate_SoftphoneLinks__c = objSoftPhoneLinks2.Id;
            objSoftPhoneAgent4.Sites__c=objSoftPhoneLinks2.Sites__c;
            objSoftPhoneAgent4.User_Id__c='223';
            objSoftPhoneAgent4.DB2_Status__c='Ready For Delete';
            objSoftPhoneAgent4.DB2_Sync__c=true;
            insert objSoftPhoneAgent4;
            System.assertEquals(objSoftPhoneAgent4.DB2_Status__c == 'Ready For Delete', true);
        }        
    }
    /*
    * Method Name : testBeforeInsertAndUpdate
    * Description : This method updates and deletes the data to fire the trigger.
    * Return type : None
    * Parameter   : None
    */
    @isTest static void testBeforeInsertAndUpdate()
    {
        User objUser = [Select id, lastname from user where lastname='test SoftphoneUsers' limit 1];        
        System.runAs(objUser)
        {
            Test.startTest();
            SoftphoneLinks__c objLinks = [SELECT Id,Sites__c from SoftphoneLinks__c where Sites__r.Sites_External_Id__c='123456'];
            Softphone_Agent__c objAgent = [SELECT id,Agent_Extension__c, Integration_Update__c,DB2_Status__c,Default_SoftphoneLinks__c,Alternate_SoftphoneLinks__c from Softphone_Agent__c where Agent_Extension__c='TestExt' limit 1];
            objAgent.DB2_Status__c='Ready For Update';
            objAgent.Default_SoftphoneLinks__c=objLinks.Id;
            objAgent.Sites__c=objLinks.Sites__c;
            objAgent.Alternate_SoftphoneLinks__c = objLinks.Id;
            update objAgent;
            System.assertEquals(objAgent.DB2_Status__c == 'Ready For Update', true);
    
            Softphone_Agent__c objAgentSync = [SELECT id,Agent_Extension__c, Integration_Update__c,DB2_Status__c,Default_SoftphoneLinks__c,Alternate_SoftphoneLinks__c from Softphone_Agent__c where Agent_Extension__c='TestExt' limit 1];
            objAgentSync.DB2_Status__c='Synced';
            objAgentSync.Integration_Update__c=true;
            update objAgentSync;
            System.assertEquals(objAgentSync.DB2_Status__c == 'Synced', true); 
            
            Softphone_Agent__c objAgentUpdate = [SELECT id,Agent_Extension__c, Integration_Update__c,DB2_Status__c,Default_SoftphoneLinks__c,Alternate_SoftphoneLinks__c from Softphone_Agent__c where Agent_Extension__c='TestDel' limit 1];
            objAgentUpdate.DB2_Status__c='Ready For Insert';
            update objAgentUpdate;
            System.assertEquals(objAgentUpdate.DB2_Status__c == 'Ready For Insert', true);
            
            Softphone_Agent__c objAgentDelete = [SELECT id,Agent_Extension__c, Integration_Update__c,DB2_Status__c,Default_SoftphoneLinks__c,Alternate_SoftphoneLinks__c from Softphone_Agent__c where Agent_Extension__c='TestExt2' limit 1];           
            delete objAgentDelete;
            
            Softphone_Agent__c objAgentDelete2 = [SELECT id,Agent_Extension__c, DB2_Sync__c,Integration_Update__c,DB2_Status__c,Default_SoftphoneLinks__c,Alternate_SoftphoneLinks__c from Softphone_Agent__c where Agent_Extension__c='TestExt3' limit 1];
            try
            {
                delete objAgentDelete2;
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