/*****************************************************************************************************************************************************************************************************************************************************
Apex Class Name  : UnknownToKnownMember_T_HUM
Version          : 1.0
Function         : Test class to move from unknown to known member
Version                   Developer Name                  Date                       Description
1.0                      Bhumika S                       2/1/2022           User Story 3010498: T1PRJ0314829 - MF 1 - Quality - Ability to move cases from an unknown to a known Member (RF)
1.1                      Bhumika S                       2/22/2022          Fixed Test class defect
**************************************************************************************************************************************************/
@isTest
public class UnknownToKnownMember_T_HUM {
     /*
    * Method Name : setUpTestData
    * Description : This method creates test data for test class
    * Return type : None
    * Paramater   : None
    */
    @testSetup static void setUpTestData()
    {
        User testAdminUser = HUMTestDataHelperTest.createTestUser('System_Administrator_User', 'System Administrator');
        insert testAdminUser;
        User testUser = HUMTestDataHelperTest.createTestUser('Customer_Case_specialist_User', 'Customer Care Specialist');
        insert testUser;
        system.assertEquals('aCustomer_Case_specialist_User',String.valueOf([Select CommunityNickName from User Where id=:testUser.Id].CommunityNickName));        
        
        Account oAccUM = HUMTestDataHelperTest.getAccount();
        oAccUM.FirstName = 'Unknown Member TestAcc';
        oAccUM.LastName = 'UM TestAcc';
        oAccUM.ownerId = testUser.Id;
        oAccUM.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Unknown Member').getRecordTypeId();
        insert oAccUM;
        system.assertEquals('Unknown Member',String.valueOf([Select RecordType.Name from Account where id=:oAccUM.Id].RecordType.Name));
        
       
        Account oAcc = HUMTestDataHelperTest.getAccount();
        oAcc.FirstName = 'Known Member TestAcc';
        oAcc.LastName = 'KM TestAcc';
        oAcc.ownerId = testUser.Id;
        oAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
        insert oAcc;
        system.assertEquals('Member',String.valueOf([Select RecordType.Name from Account where id=:oAcc.Id].RecordType.Name));
            
        //Inserting the Case records to unknown member account
        Case oCase = HUMTestDataHelperTest.getCase();
        oCase.AccountId = oAccUM.Id;
        oCase.OwnerId = testUser.id;
        insert oCase;
        system.assertEquals([Select AccountId from Case Where id=:oCase.Id].AccountId,oAccUM.Id);
        
        //Inserting the Case records to unknown member account
        Case oCase1 = HUMTestDataHelperTest.getCase();
        oCase1.AccountId = oAcc.Id;
        oCase1.OwnerId = testUser.id;
        insert oCase1;
        system.assertEquals([Select AccountId from Case Where id=:oCase1.Id].AccountId,oAcc.Id);
            
            
        //Create an Interaction
        Interaction__C oInt = new Interaction__C();
        //oInt.Caller_name__C = 'Test Caller';
        oInt.Interacting_with_type__C = 'Unknown-Member';
        oInt.Interacting_with__c = oAccUM.Id;
        oInt.Interaction_Origin__c = 'Inbound Call';
        oInt.OwnerId = testUser.Id;
        insert oInt ;
        system.assertEquals(oAccUM.Id,[Select Interacting_With__c from Interaction__c where id=:oInt.Id].Interacting_With__c);
            
        //Create an Interaction Member
        Interaction_Member__c oIntMem = new Interaction_Member__c(
                                        Interaction__c = oInt.id,
                                        Interacting_About__c = oAccUM.Id,
                                        Interacting_About_Type__c = 'Unknown-Member'
                                        );
        insert oIntMem;
        system.assertEquals(oInt.id,[Select Interaction__c from Interaction_Member__c where id=:oIntMem.id].Interaction__c);
                    
         //Create an Case Interaction
         Case_Interaction__c oCaseInt = new Case_Interaction__c();
         oCaseInt.Case__c = oCase.Id;
         oCaseInt.Interaction__c = oInt.Id;
         insert oCaseInt;
    }
    
    /*
    * Method Name : testInteractionFromAccount
    * Description : Test method to create interaction from Account
    * Return type : void
    * Paramater   : NA
    */
    static testMethod void testInteractionFromAccount()
    {
        User testUser = [Select Id, Name from User where LastName='test Customer_Case_specialist_User'];
        System.runAs(testUser)
        {
            Account oAcc = [Select id from Account where FirstName = 'Known Member TestAcc'];
            Case oCase = [Select id from Case where Account.FirstName = 'Unknown Member TestAcc' Limit 1 ];
            ApexPages.currentPage().getParameters().put('id',oCase.Id);
            Interaction__c oInteraction = new Interaction__c();
            oInteraction.Interacting_With__c = oAcc.Id;
            oInteraction.Interacting_With_type__c = 'Member';
            Test.startTest();        
                UnknownToKnownMember_C_HUM humNewInt = new UnknownToKnownMember_C_HUM ();
                humNewInt.interactionRec = oInteraction;
                humNewInt.oCase.AccountId = oAcc.Id;
                humNewInt.editAndMove();
                humNewInt.getopenAccountLookup();
                system.assertEquals(humNewInt.interactionRec.id!=null,false);
            Test.stopTest();
        }       
    }
    
    /*
    * Method Name : testInteractionFromAccountNegative
    * Description : Test method to create interaction from Account negative
    * Return type : void
    * Paramater   : NA
    */
    static testMethod void testInteractionFromAccountNegative()
    {
        User testUser = [Select Id, Name from User where LastName='test Customer_Case_specialist_User'];
        System.runAs(testUser)
        {
            Account oAcc = [Select id from Account where FirstName = 'Known Member TestAcc'];
            Case oCase = [Select id from Case where Account.FirstName = 'Unknown Member TestAcc' Limit 1 ];
            oCase.Interacting_With_Type__c='Member';
            ApexPages.currentPage().getParameters().put('id',oCase.Id);
            Interaction__c oInteraction = new Interaction__c();
            oInteraction.Interacting_With__c = oAcc.Id;
            oInteraction.Interacting_With_type__c = 'Unknown-Member';                      
            Test.startTest();        
                UnknownToKnownMember_C_HUM humNewInt = new UnknownToKnownMember_C_HUM ();
                humNewInt.interactionRec = oInteraction;
                humNewInt.IntWithName= 'test' ;
                humNewInt.bMemberFlag =true;
                humNewInt.bIntAboutFlag=true;
                humNewInt.bIntWithFlag=true;                
                humNewInt.editAndMove();
                system.assertEquals(humNewInt.interactionRec.id!=null,false);
            Test.stopTest();
        }       
    } 
    
    /*
    * Method Name : testInteractionCatchBlock
    * Return type : void
    * Paramater   : NA
    */
   static testMethod void testInteractionCatchBlock()
    {
        User testUser = [Select Id, Name from User where LastName='test Customer_Case_specialist_User'];
        System.runAs(testUser)
        {
            Account oAcc = [Select id from Account where FirstName = 'Known Member TestAcc'];
            Case oCase = [Select id from Case where Account.FirstName = 'Known Member TestAcc' limit 1];
            ApexPages.currentPage().getParameters().put('id',oCase.Id);
            Test.startTest();        
                UnknownToKnownMember_C_HUM humNewInt = new UnknownToKnownMember_C_HUM ();          
                humNewInt.editAndMove();               
            Test.stopTest();
        }       
    } 
    
}