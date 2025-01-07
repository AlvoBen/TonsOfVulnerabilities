/**************************************************************************************************************************************************
Apex class Name    :  UserVoiceIdea_T_HUM
Version            :  1.0
Function           :  This is the test class for UserVoiceIdea_C_HUM  class

   Modification Log:
    
 *   Developer                   Code Review             Date               Description
 * -------------------------------------------------------------------------------------------------------------------------------------------------  
 *  Asish Behera         		                 		 11/09/2018          Original Version
 *
************************************************************************************************************************************************* */
@isTest
private class UserVoiceIdea_T_HUM {
	
     /*
    * Method Name : prepareTestData
    * Description : Used to Insert custom setting record
    * Return type : void
    * Paramater   : NA
    */
    @testSetup static void prepareTestData(){
        	List<UserVoiceForum_HUM__c> lstobjUserVoiceForum = new List<UserVoiceForum_HUM__c>();
            lstobjUserVoiceForum.add(new UserVoiceForum_HUM__c(Name='RapidForce',ForumId__c = '914068'));
            Insert lstobjUserVoiceForum;
        	User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        	Insert oCCSUser;
        	system.assertEquals(oCCSUser.LastName,'test User1');
            System.assertEquals(lstobjUserVoiceForum.size(), 1);
    }
    static testmethod void uservoiceWidget() {
    	User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');        
        System.runAs (oCCSUser) 
        { 
           Test.StartTest();
           UserVoiceIdea_C_HUM userVoiceClsObj = new UserVoiceIdea_C_HUM();           
           Test.stopTest();           
        }
    }
}