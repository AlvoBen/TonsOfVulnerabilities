/*******************************************************************************************************************************************************************************************
Apex Class Name  : CustomInteractionLog_T_HUM
Version          : 1.0
Created Date     : JUNE 09 2014
Function         : Test class to CustomInteractionLog_C_HUM Apex class
Modification Log :

* Modification Id          Developer Name        Code Reveiw                  Date                       Description
*------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* 1.0                      Ninad Patil              16484                   09/06/2014                 Original Version
* 1.1                      Ninad Patil              17100                   08/05/2014                 Resubmitting after coding standards correction.
* 1.2                      Ninad Patil              18313                   10/07/2014                 Fixed code review comment : sAccId removed.
* 1.3                      Ninad Patil              19532                   18/11/2014                 Added interaction origin field on interaction.
* 1.4                      Shreya Choodamani        20233                   01/07/2015                 Updated the test class to account for the methods created for REQQ - 66088.
* 1.5                      Shreya Choodamani        #####                   01/12/2015                 Updated the test class to fix test class failures
* 1.6                      SuryaKumari Medicherla   20579                   01/28/2015                 Updated the test class to fix test class failures
* 1.7                      Pradeep Raju             24021                   07/08/2015                 Updated the code as part of review comments from Review No - 24021
* 1.8                      Sagar Tapadia            25983                   09/09/2015                 Added Assert Statements as per review comments from Review 25983.
* 1.9                      Sharan Shanmugam         29837                   02/10/2016                 Re raising it for review as part of Story REQ - 220870
* 1.10                     Prudhvi Pamarthi         138505                  11/13/2017                 Updated test class as part of REQ - 328935
* 1.11                     Santhi Mandava                                   03/12/2019                 Removed "DpaaSWebServiceMockImpl" usage.
* 1.12			           Sahil Verma/Navajit Sarkar			            07/19/2021		           US#2287945 : Adding Authentication test methods
* 1.13                     Harshada Kamble/Anil Pavithran                   05/05/2023                 US4461937 T1PRJ0036776: SFDC Ability to Manually Modify Authentication Status (Multi Members) in Lightning - Genesys
*****************************************************************************************************************************************************************************************/
@isTest
private class CustomInteractionLog_T_HUM
{
    /*
    * Method Name : setUpTestData
    * Description : This method creates test data for test class
    * Return type : None
    * Paramater   : None
    */
    @testSetup static void setUpTestData()
    {
        User testUser = HUMTestDataHelperTest.createTestUser('Customer_Case_specialist_User', 'Customer Care Specialist');
        insert testUser;
        system.assertEquals('aCustomer_Case_specialist_User',String.valueOf([Select CommunityNickName from User Where id=:testUser.Id].CommunityNickName));
        
        Account oAcc = HUMTestDataHelperTest.getAccount();
        oAcc.FirstName = 'TestAcc';
        oAcc.LastName = 'TestAcc';
        oAcc.ownerId = testUser.Id;
        oAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
        insert oAcc;
        system.assertEquals('Member',String.valueOf([Select RecordType.Name from Account where id=:oAcc.Id].RecordType.Name));
        
        Interaction__C oInt = new Interaction__C();
        oInt.Caller_name__C = 'Test Caller';
        oInt.Interacting_with_type__C = 'Member';
        oInt.Interacting_with__c = oAcc.Id;
        oInt.Interaction_Origin__c = 'Inbound Call';
        oInt.OwnerId = testUser.Id;
        insert oInt ;
        system.assertEquals(oAcc.Id,[Select Interacting_With__c from Interaction__c where id=:oInt.Id].Interacting_With__c);
        
        Interaction_Member__c oIntMem = new Interaction_Member__c(
                                        Interaction__c = oInt.id,
                                        Interacting_About__c = oAcc.Id,
                                        Interacting_About_Type__c = 'Member'
                                        );
        insert oIntMem;
        system.assertEquals(oInt.id,[Select Interaction__c from Interaction_Member__c where id=:oIntMem.id].Interaction__c);
                
        
        Case oCase = HUMTestDataHelperTest.getCase();
        oCase.AccountId = oAcc.Id;
        oCase.OwnerId = testUser.id;
        insert oCase;
        system.assertEquals([Select AccountId from Case Where id=:oCase.Id].AccountId,oAcc.Id);
                
        Casecomment oCaseCom = New Casecomment();
        oCaseCom.ParentId = oCase.Id;
        oCaseCom.CommentBody = 'Test Comment';  
        insert oCaseCom;     
        
		insert New HUM_Webservice_Callout__c( Name = 'GetOffersV3', Request_Type__c='POST', Content_Type__c='application/json', Certificate_Name__c = 'Test_Certificaton', Active__c = true, End_Point_URL__c = 'https://testURL-crmservicews.humana.com/getOffers/GetOffers', Timeout__c = '12000');
		insert New HUMConstants__c( Name = 'GetOffers_Switch', StringValue__C='YES');
		insert New HUMConstants__c( Name = 'LogAlertV3_Switch', StringValue__C='YES');
	 
    }
    
    /*
    * Method Name   :    testInitializeInteraction()
    * Description   :    This method tests initialisation Of interaction.
    * Return Type   :    None
    * Parameters    :    None
    */
    Static testMethod void testInitializeInteraction()
    {
        User testUser = [Select Id, Name from User where LastName='test Customer_Case_specialist_User'];
        System.runAs(testUser)
        {
            Account oAcc = [Select id from Account where FirstName = 'TestAcc'];
            
            ApexPages.currentPage().getParameters().put('id',oAcc.Id);
            Interaction__c oInt = [Select id, name, Caller_name__C,Authenticated__c, Interacting_with_type__C, Interacting_with__c, UUID__c,
                                          Interaction_Origin__c from Interaction__c where Interacting_with_type__C = 'Member'];
            Interaction_Member__c oIntMem = [Select id from Interaction_Member__c where Interacting_About_Type__c = 'Member'];
            
            String oUrl = url.getsalesforcebaseurl().toexternalform();
            Test.startTest();
            CustomInteractionLog_C_HUM oHUNIntLogCont = New CustomInteractionLog_C_HUM();
            
            //Simulating Failure Condition for Invalid interaction number
            oHUNIntLogCont.sStandardUrl = oUrl + '/' + oAcc.id +'&interactionId=123';
            oHUNIntLogCont.initializeInteraction();
            system.assert(oHUNIntLogCont.oFetchInteraction.id==null);
            
            //Constructing URL which will be checked while initializing Interaction.
            oHUNIntLogCont.sStandardUrl = oUrl + '/' + oAcc.id +'&interactionId='+oInt.Id;
            oHUNIntLogCont.initializeInteraction();
            system.assertEquals(oHUNIntLogCont.oFetchInteraction.Id,oInt.Id);
            Test.stopTest();
        }
    }
    
    /*
    * Method Name   :    testInteractionSave()
    * Description   :    This method tests save Of interaction.
    * Return Type   :    None
    * Parameters    :    None
    */
    Static testMethod void testInteractionSave()
    {
        User testUser = [Select Id, Name from User where LastName='test Customer_Case_specialist_User'];
        System.runAs(testUser)
        {
            Account oAcc = [Select id,Name from Account where FirstName = 'TestAcc'];
            Interaction__c oInt = [Select id, name, Caller_name__C,Authenticated__c, Interacting_with_type__C, UUID__c, Interacting_with__c,
                                          Interaction_Origin__c,Call_Category_Description__c,Survey_ID__c,Genesys_Interaction_ID__c,Call_Data__c from Interaction__c where Interacting_with_type__C = 'Member'];
            
            ApexPages.currentPage().getParameters().put('id',oAcc.Id);
                        
            Case oCase = [Select id from case where AccountId=:oAcc.Id];            
            Casecomment oCaseCom = [Select id from casecomment where ParentId=:oCase.Id]; 
                       
            List<HUMConstants__c> lstCstSettingAccount = new List<HUMConstants__c> ();
            lstCstSettingAccount.add(new HUMConstants__c(Name = 'OBJECT_ACCOUNT',  StringValue__C = 'Account'));
            lstCstSettingAccount.add(new HUMConstants__c(Name = 'OBJECT_CASE',  StringValue__C = 'Case'));
            insert lstCstSettingAccount;
            system.assertEquals('Account',String.valueOf([Select StringValue__c from HUMConstants__c where Name='OBJECT_ACCOUNT'].StringValue__c));
            
            test.startTest();
            //Initializing the main controller
            CustomInteractionLog_C_HUM oHUNIntLogCont = new CustomInteractionLog_C_HUM();
            oHUNIntLogCont.sAccountId = oAcc.id;
            oHUNIntLogCont.oFetchInteraction = oInt;
            oHUNIntLogCont.sInteractionId = oInt.id;
            
            //Saving the Interaction record.
            oHUNIntLogCont.save();
            System.assertEquals(oHUNIntLogCont.oFetchInteraction.id,oInt.Id);
            
            //Saving the Interaction record for null id.
            oHUNIntLogCont.sAccountId = null;
            oHUNIntLogCont.save();
            System.assertEquals(oHUNIntLogCont.oFetchInteraction.Name,oInt.Name);
            

            //Saving and requerying the interaction record.
            oHUNIntLogCont.saveInteractionRecord();
            System.assertEquals(oHUNIntLogCont.oFetchInteraction.Name,oInt.Name);
            
            //Simulating Case Association to Interaction. 
            //Checking whether Case Interaction record created or Not.
            oHUNIntLogCont.sAccountId = oCase.id;
            oHUNIntLogCont.save();
            list<Case_Interaction__C> lstCaseInteraction = [SELECT ID,case__C,interaction__C from Case_interaction__c
                                                                   WHERE case__c = :oCase.id AND Interaction__C = :oInt.id];
            system.assert(lstCaseInteraction.size()==1);
            
            //Simulating on Change event of "Interacting About" field.
            oHUNIntLogCont.sAccountId = oAcc.id;
            oHUNIntLogCont.onChangeInqAbt();
            system.assertEquals(oHUNIntLogCont.sAccName,oAcc.Name);

            oHUNIntLogCont.saveInteractionAndRequery(oInt);
            
            //Flushing the Interaction record.
            oHUNIntLogCont.SaveAndNew();
            System.assert(String.isBlank(oHUNIntLogCont.oFetchInteraction.Caller_name__c));
            
            //validating AssociateCaseButton for Account record. 
            oHUNIntLogCont.recordid = oAcc.id;
            oHUNIntLogCont.validateAssociateCaseButton();
            System.assertEquals(oHUNIntLogCont.bAssignCase, false);
            

            //validating AssociateCaseButton for Case record.
            oHUNIntLogCont.recordid = oCase.id;
            oHUNIntLogCont.validateAssociateCaseButton();
            System.assertEquals(oHUNIntLogCont.bAssignCase, true);
            
            //validating AssociateCaseButton for Casecomment record.
            oHUNIntLogCont.recordid = oCaseCom.Id;
            oHUNIntLogCont.validateAssociateCaseButton();
            //boolean bAssignCase = true;
            System.assertEquals(oHUNIntLogCont.bAssignCase, false);                     

            //disabling AssociateCaseButton. 
            oHUNIntLogCont.disableAssociateCaseButton();
            System.assertEquals(oHUNIntLogCont.bAssignCase, false);
                                

            //Deleting custom setting so as to test exception handeling in save function.
            delete lstCstSettingAccount;
            oHUNIntLogCont.save();
            system.assert(ApexPages.hasMessages());
            
            test.stopTest();
        }
    }
    
    /*
     * Method Name : testAuthentication
     * Description : This method tests if the label in the interaction log panel is 'authenticated'
     * Return type : None
     * Paramater   : None
     */    
    static testMethod void testAuthentication(){
        Test.startTest();
        CustomInteractionLog_C_HUM oHUNIntLogCont = new CustomInteractionLog_C_HUM();
        //setting the Authentication Indicator value to true
        oHUNIntLogCont.oFetchInteraction.Authenticated__c = true;
        oHUNIntLogCont.oHUMIntMemers.Authenticated__c =true;
        oHUNIntLogCont.authCheck = true;
        //validating authCheckLabel value
        String authLabel = oHUNIntLogCont.authCheckLabel;
        system.assertEquals(authLabel, 'Authenticated');
        oHUNIntLogCont.authCheckHandler();
        Test.stopTest();
    }

    /*
     * Method Name : testUnauthentication
     * Description : This method tests if the label in the interaction log panel is 'Unauthenticated'
     * Return type : None
     * Paramater   : None
     */    
    static testMethod Void testUnauthentication(){
        Test.startTest();
        CustomInteractionLog_C_HUM oHUNIntLogCont = new CustomInteractionLog_C_HUM();
        //setting the Authentication Indicator value to true
        oHUNIntLogCont.oFetchInteraction.Authenticated__c = false;
        oHUNIntLogCont.oHUMIntMemers.Authenticated__c =false;
        oHUNIntLogCont.authCheck = false;        
         //validating authCheckLabel value
        string authLabel1 = oHUNIntLogCont.authCheckLabel;
        system.assertEquals(authLabel1, 'UnAuthenticated');
        oHUNIntLogCont.authCheckHandler();
        Test.stopTest();
    }
  
}