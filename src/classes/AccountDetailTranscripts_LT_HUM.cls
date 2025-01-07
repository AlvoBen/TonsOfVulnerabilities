/*******************************************************************************************************************************
Apex Class Name : AccountDetailTranscripts_LT_HUM
Version         : 1.0
Created On      : 02/09/2021  
Function        : This Class serves as test class to AccountDetailTranscripts_LC_HUM. 
Test Class      : AccountDetailTranscripts_LT_HUM
Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Joel George                                            02/11/2021               initial version
* Ankima Srivastava										 03/19/2021				  fix to increase code coverage
* Ashish Kumar/Ritik								     09/13/2021		          Increased Code coverage
* Joel George                                            11/10/2021               Increased coverage
* Kajal Namdev                                            11/16/2021               Increased coverage
*********************************************************************************************************************************/
@isTest
public with sharing class AccountDetailTranscripts_LT_HUM
{
    /**
    * @testSetup method to create test data
    *<p>
    * @param            NULL
    * @return           void
    */
    @testSetup static void setUpTestData()
    {
       
        Account objMemAccount = HUMTestDataHelperTest.getAccount();
        objMemAccount.LastName = 'LName'; 
        objMemAccount.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
        objMemAccount.Home_Office_Account__c = true;
        insert objMemAccount;
        system.assertEquals(objMemAccount.LastName == 'LName', true);
        
        Interaction__C oInt = HUMTestDataHelperTest.getInteraction();
        oInt.Interacting_with_type__C = 'Member';
        oInt.Interacting_with__c = objMemAccount.Id;
        insert oInt ;
        system.assertEquals(oInt.id, [Select Id from Interaction__c where Interacting_With__c=:objMemAccount.Id LIMIT 1].Id);    

        Livechatvisitor oLVisitor = New Livechatvisitor();
        insert oLVisitor;
            
        Livechattranscript oLCT = HUMTestDataHelperTest.getLiveChatTranscript();
        oLCT.Interaction__c = oInt.Id;
        oLCT.AccountId = objMemAccount.Id;
        oLCT.LiveChatVisitorId = oLVisitor.id;
        insert oLCT ;   


}
    
    /**
    * Method to create test interactions retrival
    *<p>
    * @param            NULL
    * @return           void
    */
    private static testMethod void testTranscripts()
    {
        User objUser = [Select Id,Name from User LIMIT 1];
        system.runAs(objUser)
        {
            Account oAcc = [SELECT id FROM Account LIMIT 1];
             Livechattranscript oLct = [SELECT id,name,Case.CaseNumber,Owner.Name,AccountId,Status  FROM Livechattranscript LIMIT 1];
            oLct.AccountId = oAcc.id;
            update oLct;
            list<AccountDetailTranscripts_LD_HUM.TranscriptModal> lstInts = (list<AccountDetailTranscripts_LD_HUM.TranscriptModal>) AccountDetailTranscripts_LC_HUM.getTranscripts(oAcc.Id);
            AccountDetailTranscripts_LD_HUM.getTranscripts(oAcc.Id);
            AccountDetailTranscripts_LD_HUM.TranscriptModal obj = new  AccountDetailTranscripts_LD_HUM.TranscriptModal(oLct,'test','test');
            System.assertNotEquals(lstInts, null, 'No Transcripts retrived');
        }
    }
}