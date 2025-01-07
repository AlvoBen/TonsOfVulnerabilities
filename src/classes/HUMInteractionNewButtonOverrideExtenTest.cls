/****************************************************************************
Apex Class Name  : HUMInteractionNewButtonOverrideExtenTest
Version          : 1.0
Created On       : JULY 15 2014
Function         : Test class to HUMInteractionNewButtonOverrideExtension Apex class

Modification Log :
   Developer Name                 Code Review                                   Date                       Description
* ------------------------------------------------------------------------------------------------------------------------------
   Suryakumari Medicherla          17093                                      15/07/2014             Original Version
   Suryakumari Medicherla          17717                                      05/09/2014             Code added to fix Defect :153752
   SuryaKumari Medicherla          18038                                      09/19/2014             Added code cover the code added for removed SQL for custom settings with Map
   SuryaKumari Medicherla          18795                                      10/27/2014             To avoid URL not foung error from interaction detail page
   SuryaKumari Medicherla          18868                                      10/30/2014             To avoid Test Class failure in other environments
   Shreya Choodamani               20265                                      01/12/2015             Fixed the test class failure
   Manish Kumar                    20291                                      01/12/2015             Fixed the test class failure
   SuryaKumari Medicherla          20579                                      01/28/2015             Fixed the test class failure
   Prachi Bhasin                   33486                                      07/18/2016             Changes made as part of REQ- 270349
   Pradeepkumar Dani               99570                                      06/08/2017             Intent__c.Classification__c & Intent__c.Etl_Idtfr__c Clean up
   Pradeepkumar Dani              100996                                      06/13/2017             Classification__c.Classification_Type__c & Etl_Idftr__c Clean Up
   Santhi Mandava                                                             03/12/2019             Removed "DpaaSWebServiceMockImpl" usage.
**********************************************************************************************************************************/

@isTest
private class HUMInteractionNewButtonOverrideExtenTest
{   
    /**
    * testsetup method to create test data
    *<p>
    * @param            NULL
    * @return           void
    */
    @testSetup static void setup() 
    {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        System.assert(oUser.LanguageLocaleKey == 'en_US');
        System.runAs(oUser)
        {
            Account oAccount = HUMTestDataHelperTest.getAccount();
            oAccount.Name = 'New Case Account';
            oAccount.Group_Number__c  = '123';
            oAccount.BillingState = 'New York';
            oAccount.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
            insert oAccount;
            System.assert(oAccount.Group_Number__c  == '123');
            
            Product2 oProduct = HUMTestDataHelperTest.getProduct();
            oProduct.Name='Mediclaim';
            insert oProduct;
            system.assert(oProduct.Name == 'Mediclaim');
            
            Policy__c oPolicy = HUMTestDataHelperTest.getPolicy();
            oPolicy.Name = 'test123';
            oPolicy.Group_Name__c = oAccount.Id;
            oPolicy.Funding_Type_Code__c = 'Y';
            oPolicy.Product__c = oProduct.Id;
            oPolicy.Issue_State__c = 'IL'; 
            insert oPolicy;
            system.assert(oPolicy.Issue_State__c == 'IL');
            
            Policy_Member__c oPolicyMem = HUMTestDataHelperTest.getPolicyMember();
            oPolicyMem.Name = '12345';
            oPolicyMem.Member__c = oAccount.Id;
            oPolicyMem.Policy__c = oPolicy.Id;            
            oPolicyMem.Relationship__c = 'Self';
            insert oPolicyMem;
            system.assert(oPolicyMem.Relationship__c == 'Self');           
            
            List<Classification__c> lstClassification = new List<Classification__c>();
            Classification__c classificationRec = new Classification__c();
            classificationRec.Name = 'Claims';
            classificationRec.Is_Active__c = true;
            lstClassification.add(classificationRec);
            insert lstClassification;
            system.assertEquals(lstClassification[0].Name=='Claims',true);
            
            List<Intent__c> lstIntent = new List<Intent__c>();
            Intent__c intentRec = new Intent__c();
            intentRec.Name = 'Filling';
            intentRec.Is_Active__c = true;
            lstIntent.add(intentRec);
            insert lstIntent;
            system.assertEquals(intentRec.Name == 'Filling', true);
            Case oCase = new Case(AccountId = oAccount.Id, Intent_Id__c = lstIntent[0].id, Classification_Id__c =lstClassification[0].id, Policy_Member__c= oPolicyMem.Id ,
                             Interacting_With_Type__c = 'Member',Interacting_With__c= oAccount.Id, Due_Date__c=Date.Today(), 
                             Case_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU', Work_Queue_View_Name__c='Cincinati CRU',Service_Center__c ='Cincinnati',Department__c='CRU', status='In Progress');
            oCase.RecordTypeId = Schema.sObjectType.Case.getRecordTypeInfosByName().get('Group Case').getRecordTypeId();
            insert oCase;
            System.assert(oCase.Interacting_With_Type__c == 'Member');
        }
    }
 
    /**
    * createInteractionOnCase
    * <p>
    * Test method to create Case Interaction on Case.
    *
    * @param           N/A
    * @return          void                 
    */ 
   static testMethod void createInteractionOnCase() 
    {       
        User oUser = [SELECT id,Current_Queue__c, LastName from User WHERE  Lastname = 'test User1' LIMIT 1];     
        System.assert(oUser.id != null);
        System.runAs(oUser)
        {
            Case oCase = [Select id,CaseNumber from case];
            HUMSalesforceFieldIds__c oSalesForceIds = new HUMSalesforceFieldIds__c ();
            Interaction__c oInteraction = new Interaction__c(Caller_Name__c = 'member', Interaction_Origin__c = 'Inbound Call');
            insert oInteraction;
            System.assert(oInteraction.Id != null);
            oSalesForceIds.Name = 'id';
            oSalesForceIds.Case_in_Case_Interaction__c = '00NL00000035Rah';
            oSalesForceIds.RelatedTo_Case_in_Attachment__c = '00NL00000036quk';
            oSalesForceIds.RelatedTo_Task_in_Attachment__c = '00NL000000371yT';
            oSalesForceIds.Interaction_in_Case_Interaction__c = '00NL00000035Ram';
            insert oSalesForceIds;
            system.assertNotEquals(oSalesForceIds,null,'Value not matched');
            Test.startTest();
            ApexPages.StandardController ostdController = new ApexPages.StandardController(oCase);
            HUMInteractionNewButtonOverrideExtension oextension = new  HUMInteractionNewButtonOverrideExtension(ostdController );  
            ApexPages.currentPage().getParameters().put('CF' + oSalesForceIds.Case_in_Case_Interaction__c + '_lkid', oCase.id);
            ApexPages.currentPage().getParameters().put('CF' + oSalesForceIds.Case_in_Case_Interaction__c, oCase.CaseNumber );
            oextension.reDirectPage();
            String snextPage = oextension.reDirectPage().getUrl();
            System.assert(snextPage != null);
            
            ApexPages.currentPage().getParameters().put('CF' + oSalesForceIds.Case_in_Case_Interaction__c + '_lkid', '');
            ApexPages.currentPage().getParameters().put('CF' + oSalesForceIds.Case_in_Case_Interaction__c , '');
            ApexPages.currentPage().getParameters().put('CF' + oSalesForceIds.Interaction_in_Case_Interaction__c + '_lkid', oInteraction.id);
            ApexPages.currentPage().getParameters().put('CF' + oSalesForceIds.Interaction_in_Case_Interaction__c , oInteraction.Name );
            oextension.reDirectPage();
            snextPage = oextension.reDirectPage().getUrl();
            System.assert(snextPage != null);
            
            oUser.current_queue__c = 'queue1' ;
            update oUser;
            System.assert(oUser.current_queue__c == 'queue1');
            
            ApexPages.currentPage().getParameters().put('CF' + oSalesForceIds.Case_in_Case_Interaction__c  + '_lkid', oCase.id);
            ApexPages.currentPage().getParameters().put('CF' + oSalesForceIds.Case_in_Case_Interaction__c , oCase.CaseNumber );
            oextension.reDirectPage();
            snextPage = oextension.reDirectPage().getUrl();
            System.assert(snextPage != null);
            
            ApexPages.currentPage().getParameters().put('CF' + oSalesForceIds.Interaction_in_Case_Interaction__c + '_lkid', oInteraction.id);
            ApexPages.currentPage().getParameters().put('CF' + oSalesForceIds.Interaction_in_Case_Interaction__c , oInteraction.Name);
            oextension.reDirectPage();
            snextPage = oextension.reDirectPage().getUrl();
            System.assert(snextPage != null);
            Test.stoptest();
        }
   }
   
    /**
    * createCIForException
    * <p>
    * Test method to cover catch blocks.
    *
    * @param           N/A
    * @return          void                 
    */ 
   static testMethod void createCIForException() 
    {
        User oUser = [SELECT id,Current_Queue__c, LastName from User WHERE  Lastname = 'test User1' LIMIT 1];     
        System.assert(oUser.id != null);
        System.runAs(oUser)
        {
            Case oCase = [Select id,CaseNumber from case];
            HUMSalesforceFieldIds__c oSalesForceIds = new HUMSalesforceFieldIds__c ();
            Interaction__c oInteraction = new Interaction__c(Caller_Name__c = 'member', Interaction_Origin__c = 'Inbound Call');
            insert oInteraction;
            System.assert(oInteraction.Caller_Name__c == 'member');
            oSalesForceIds.Name = 'id';
            oSalesForceIds.Case_in_Case_Interaction__c = '00NL00000035Rah';
            oSalesForceIds.RelatedTo_Case_in_Attachment__c = '00NL00000036quk';
            oSalesForceIds.RelatedTo_Task_in_Attachment__c = '00NL000000371yT';
            oSalesForceIds.Interaction_in_Case_Interaction__c = '00NL00000035Ram';
            insert oSalesForceIds;
            system.assertNotEquals(oSalesForceIds,null,'Value not matched');

            Test.startTest();
            ApexPages.StandardController ostdController = new ApexPages.StandardController(oCase);
            HUMInteractionNewButtonOverrideExtension oextension = new  HUMInteractionNewButtonOverrideExtension(ostdController );  
            ApexPages.currentPage().getParameters().put('CF' + oSalesForceIds.Case_in_Case_Interaction__c + '_lkid', oCase.id);
            ApexPages.currentPage().getParameters().put('CF' + oSalesForceIds.Case_in_Case_Interaction__c, oCase.CaseNumber );
            oextension.reDirectPage();
            String snextPage = oextension.reDirectPage().getUrl();
            System.assert(snextPage != null);
            
            oUser.current_queue__c = 'queue1' ;
            update oUser;
            System.assert(oUser.current_queue__c == 'queue1');
            
            ApexPages.currentPage().getParameters().put('CF' + oSalesForceIds.Case_in_Case_Interaction__c + '_lkid', oCase.id);
            ApexPages.currentPage().getParameters().put('CF' + oSalesForceIds.Case_in_Case_Interaction__c , oCase.CaseNumber );
            oextension.reDirectPage();
            snextPage = oextension.reDirectPage().getUrl();
            System.assert(snextPage != null);
            Test.stopTest();
        }
    } 
      
}