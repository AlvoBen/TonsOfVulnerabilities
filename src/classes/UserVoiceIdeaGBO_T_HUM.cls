/**************************************************************************************************************************************************
Apex class Name    :  UserVoiceIdeaGBO_T_HUM
Version            :  1.0
Function           :  This is the test class for UserVoiceIdeaGBO_C_HUM  class

   Modification Log:
    
 *   Developer                   Code Review             Date               Description
 * -------------------------------------------------------------------------------------------------------------------------------------------------  
 *  Sumanth Naredla                                   04/05/2019          Original Version
 *
************************************************************************************************************************************************* */
@isTest
private class UserVoiceIdeaGBO_T_HUM {
    
     /*
    * Method Name : prepareTestData
    * Description : Used to Insert custom setting record
    * Return type : void
    * Paramater   : NA
    */
    @testSetup static void prepareTestData(){
            List<UserVoiceForum_HUM__c> lstobjUserVoiceForum = new List<UserVoiceForum_HUM__c>();
            lstobjUserVoiceForum.add(new UserVoiceForum_HUM__c(Name='GBORapidForce',ForumId__c = '920323'));
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
           UserVoiceIdeaGBO_C_HUM userVoiceClsObj = new UserVoiceIdeaGBO_C_HUM();           
           Test.stopTest();           
        }
    }
}