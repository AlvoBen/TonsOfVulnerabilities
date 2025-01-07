/***************************************************************************************************************************************
Apex Class Name  : HUMNewInteractionControllerTest
Version          : 1.0
Created Date     : Jan 13 2015
Function         : Test class for HUMNewInteractionController
****************************************************************************************************************************************

Modification Log:

* Developer Name           Review Number              Date                       Description
*---------------------------------------------------------------------------------------------------------------------------------------
* Pradeep Raju                 20311               08/04/2014                    Original Version
* Asha Deepika Koyyana         20576               01/28/2015                    Code Modified to fix test class failure Defect # 164139  
* Praveen Kumar Parimi         27066               10/12/2015                    Updated the user context for data insertion  
* Praveen Kumar Parimi         94071               05/18/2017                    REQ-313373/Fix for CA ticket 6746409
* Santhi Mandava                                   03/12/2019                    Removed "DpaaSWebServiceMockImpl" usage.
****************************************************************************************************************************************/

@isTest
public with sharing class HUMNewInteractionControllerTest
{
    /*
    * Method Name : setUpTestData
    * Description : This method creates test data for test class
    * Return type : None
    * Paramater   : None
    */
    @testSetup static void setUpTestData()
    {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');        
        insert oUser;        
        system.assertNotEquals(oUser,null,'​Value not matched');
        system.runAs(oUser)
        {        
        //Inserting the Account records
        RecordType AccountRecordType = [Select Id from RecordType where Name='Member' and sObjectType='Account'];
        Account acc = HUMTestDataHelperTest.getAccount();
        acc.LastName = 'Test';
        acc.recordtypeid = AccountRecordType.id;
        insert acc;        
        system.assertEquals(acc.id!=null,true);
        system.assertEquals(acc.LastName,'Test','Value not matched');
        //Inserting the Case records
        Case csc = HUMTestDataHelperTest.getCase();
        csc.AccountId = acc.id;
        csc.Interacting_With__c = acc.id;
        csc.Interacting_With_Type__c = 'Member';
        csc.origin = 'Inbound Call';
        insert csc;        
        system.assertEquals(csc.id!=null,true);
        system.assertEquals(csc.Interacting_With_Type__c,'Member','Value not matched'); 
        
        //for Account related interactions
        RecordType oAccRecordType = [Select Id from RecordType where Name='Group' and sObjectType='Account'];
        Account oAcc = new Account();
        oAcc.Name = 'Test Group';
        oAcc.recordtypeid = oAccRecordType.id;
        insert oAcc;        
        system.assertEquals(oAcc.id!=null,true);
        system.assertEquals(oAcc.Name,'Test Group','Value not matched');
        }
    }
    
    /*
    * Method Name : testInteractionFromAccount
    * Description : Test method to create interaction from Account
    * Return type : void
    * Paramater   : NA
    */
    static testMethod void testInteractionFromAccount()
    {
        User oUser = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
        system.assert(ouser.id!=null);
        system.runAs(oUser)
        {       
       
            Account acc = [SELECT Id,Name from Account where Name ='Test Group' LIMIT 1];
            system.assertEquals(acc.Name,'Test Group','Value not matched');
            
            ApexPages.currentPage().getParameters().put('id',acc.id);
            Test.startTest();        
            HUMNewInteractionController humNewInt = new HUMNewInteractionController ();
            humNewInt.redirectToInteraction();
            humNewInt.save();
            system.assertEquals(humNewInt.interactionRec.id!=null,false);
            humNewInt.cancel();        
            Test.stopTest();
        }
    }
    /*
    * Method Name : testInteractionFromCase
    * Description : Test method to create interaction from case
    * Return type : void
    * Paramater   : NA
    */
    static testMethod void testInteractionFromCase()
    {
        User oUser = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
        system.assert(ouser.id!=null);
        system.runAs(oUser)
        {       
       
        Account acc = [SELECT Id,LastName from Account where LastName ='Test' LIMIT 1];
        system.assertEquals(acc.LastName,'Test','Value not matched');
        Case csc = [SELECT Id,Interacting_With_Type__c from Case where Interacting_With_Type__c ='Member' and origin ='Inbound Call' LIMIT 1];
        system.assertEquals(csc.Interacting_With_Type__c,'Member','Value not matched');        
        ApexPages.currentPage().getParameters().put('id',csc.id);
        
        Test.startTest();        
        HUMNewInteractionController humNewInt = new HUMNewInteractionController ();
        humNewInt.redirectToInteraction();
        humNewInt.save();
        system.assertEquals(humNewInt.interactionRec.id!=null,true);
        humNewInt.cancel();        
        Test.stopTest();
        }
    }
    
    /*
    * Method Name : testInteractionFromCaseNegative
    * Description : Negative test method to create interaction from case
    * Return type : void
    * Paramater   : NA
    */
    static testMethod void testInteractionFromCaseNegative()
    {
       
        Test.startTest();
        User oUser = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
        system.assert(ouser.id!=null);
        system.runAs(oUser)
        {
        HUMNewInteractionController humNewInt = new HUMNewInteractionController();
        humNewInt.save();
        humNewInt.cancel();
        humNewInt.redirectToInteraction();
        system.assertEquals(humNewInt.interactionRec.id==null,true);        
        Test.stopTest();
        }
    }
}