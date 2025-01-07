/*******************************************************************************************************************************
Apex Class Name : HUMLaunchGCPFromCaseControllerTest
Version         : 1.3
Created On      : 7/31/2014
Function        : This class tests HUMLaunchGCPFromCaseController

Modification Log: 
 * Developer Name           Code Review                 Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Pranali Choudhari        17788                       07/31/2014                 Original Version
 * Pranali Choudhari        17788                       08/06/2014                 Modified to incorporate additional test scenarios
 * Pranali Choudhari        17819                       09/14/2014                 Changes based on changes to main class
 * Shruthi Karanth          17819                       09/14/2014                 Removed hardcoded URLs
 * Vivek Batham             18083                       09/17/2014                 Changes to call decryption mock service
 * Pranali Choudhari        18083                       09/22/2014                 Changes to provider test case
 * Manish Kumar             18575                       10/15/2014                 Modified code for Defect#158177
 * Asha Deepika Koyyana     20572                       01/28/2015                 Fixed test class failure for Defect#164139  
 * Praveen Kumar Parimi     22420                       04/16/2015                 Added logic to test Contract ID for FIMMAS Members  
 * Vishal Verma             27889                       11/16/2015                 REQ - 237002  : Removing Decryption from Apex. 
 * Suresh Gottipati         240518                      5/31/2018                  REQ - 366200 - Added Member_Id.Member_Id_Value field in code which is replacement of Name Field
 * Santhi Mandava                                       04/04/2019                 Fixed test class failure for REQ - 389571
 * Santhi Mandava                                       04/15/2019                 Replaced "Member_Id_Value__c" with Name
 *********************************************************************************************************************************/

@isTest
private class HUMLaunchGCPFromCaseControllerTest
{
    /*    
    * Method Name : testSetup
    * Description : Test method to setup common test data for all test methods    
    * Return type : void    
    * Paramater   : NA    
    */
    @testSetup static void setup() 
    {
        User userObj = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert userObj;
        System.assertEquals([Select Name from User where id= :userObj.id ].name,'test User1');
        System.runAs(userObj)
        {
            HUM_Webservice_Callout__c cstGCPTest = new HUM_Webservice_Callout__c(Name = 'HUMLaunchGCP', End_Point_URL__c= 'test');
            insert cstGCPTest;
            System.assertEquals(cstGCPTest.Name,'HUMLaunchGCP');
            
            Schema.Sobjecttype objType = Schema.getGlobalDescribe().get('Account');
        
            Account oAcc = HUMTestDataHelperTest.getAccount();
            oAcc.FirstName = 'test';
            oAcc.LastName = 'account';
            oAcc.RecordTypeId = HUMUtilityHelper.getRecordTypeID(objType, 'Member');
            oAcc.Enterprise_ID__c = '1007896';  
            insert oAcc;
            System.assertEquals(oAcc.Enterprise_ID__c,'1007896');
            
            Account oAccGrp = HUMTestDataHelperTest.getAccount();
            oAccGrp.Name = 'test';
            oAccGrp.Enterprise_id__c = '112111';
            oAccGrp.RecordTypeId = HUMUtilityHelper.getRecordTypeID(objType, 'Group');
            insert oAccGrp;
            System.assertEquals(oAccGrp.Enterprise_ID__c,'112111');
            
            Account oAccProv = HUMTestDataHelperTest.getAccount();
            oAccProv.Name = 'test';
            oAccProv.RecordTypeId = HUMUtilityHelper.getRecordTypeID(objType, 'Provider');
            oAccProv.Enterprise_id__c = '112112';
            insert oAccProv;
            System.assertEquals(oAccProv.Enterprise_ID__c,'112112');
            
            Account oAccAgent = HUMTestDataHelperTest.getAccount();
            oAccAgent.Name = 'test';
            oAccAgent.Agent_Type__c = 'Agency';
            oAccAgent.Enterprise_id__c  = '112113';
            oAccAgent.RecordTypeId = HUMUtilityHelper.getRecordTypeID(objType, 'Agent/Broker');
            insert oAccAgent;
            System.assertEquals(oAccAgent.Enterprise_ID__c,'112113');
            
            Account oAccBroker = HUMTestDataHelperTest.getAccount();
            oAccBroker.Name = 'test';
            oAccBroker.Agent_Type__c = 'Broker';
            oAccBroker.Enterprise_id__c  = '112114';
            oAccBroker.RecordTypeId = HUMUtilityHelper.getRecordTypeID(objType, 'Agent/Broker');
            insert oAccBroker;
            System.assertEquals(oAccBroker.Enterprise_ID__c,'112114');
            
            Account oAccUnGrp = HUMTestDataHelperTest.getAccount();
            oAccUnGrp.Name = 'test';
            oAccUnGrp.RecordTypeId = HUMUtilityHelper.getRecordTypeID(objType, 'Unknown Group');
            oAccUnGrp.Agent_Type__c = 'DelegatedAgent';
            oAccUnGrp.Group_Number__c = '13324';
            oAccUnGrp.Enterprise_id__c  = '13324';
            insert oAccUnGrp;
            System.assertEquals(oAccUnGrp.Enterprise_ID__c,'13324');
            
            Account oAccUnMember = HUMTestDataHelperTest.getAccount();
            oAccUnMember.FirstName = 'test';
            oAccUnMember.LastName = 'acc';
            oAccUnMember.RecordTypeId = HUMUtilityHelper.getRecordTypeID(objType, 'Unknown Member');
            oAccUnMember.Agent_Type__c = 'Agency';
            oAccUnMember.Enterprise_id__c  = '13325';
            insert oAccUnMember;
            System.assertEquals(oAccUnMember.Enterprise_ID__c,'13325');
            
            ConsumerID__c oConsumer = new ConsumerId__c(Name='abc',Consumer_ID__c = '12345', Account__c = oAccAgent.Id);
            insert oConsumer;
            System.assertEquals(oConsumer.Name,'abc');
            
            ConsumerID__c oConsumerBrk = new ConsumerId__c(Name='pqr',Consumer_ID__c = '67890', Account__c = oAccBroker.Id);
            insert oConsumerBrk;
            System.assertEquals(oConsumerBrk.Name,'pqr');
            
            Policy__c oPolicy1 = HUMTestDataHelperTest.getPolicy();
            oPolicy1.Group_Name__c = oAccGrp.Id;
            oPolicy1.Platform__c = 'FM';
            insert oPolicy1;
            System.assertNotEquals(oPolicy1,null,'Value not matched');
            System.assertEquals(oPolicy1.Platform__c,'FM');
            
            Policy_Member__c oPolMem1 = HUMTestDataHelperTest.getPolicyMember();
            oPolMem1.Member__c = oAcc.Id;
            oPolMem1.Policy__c = oPolicy1.Id;
            oPolMem1.Effective_Date__c = string.valueOf(system.today());
            insert oPolMem1;
            System.assertEquals(oPolMem1.Effective_Date__c,string.valueOf(system.today()));
        
            Member_ID__c oMemId = HUMTestDataHelperTest.getmemberID();
            oMemId.Policy_Member__c = oPolMem1.Id;
            oMemId.Policy__c = oPolicy1.Id; 
            oMemId.Type__c = 'ContractID';
            oMemId.Name = '002998941';
            OMemId.Member_Card_External_ID__c = '12345678';
            insert oMemId;          
            System.assertEquals(oMemId.Type__c,'ContractID');
    
            Policy__c oPolicy = HUMTestDataHelperTest.getPolicy();
            oPolicy.Group_Name__c = oAcc.Id;
            oPolicy.Platform__c = 'DM';
            insert oPolicy;
            System.assertEquals(oPolicy.Platform__c,'DM');
    
            Policy_Member__c oPolMem = HUMTestDataHelperTest.getPolicyMember();
            oPolMem.Member__c = oAcc.Id;
            oPolMem.Policy__c = oPolicy.Id;
            oPolMem.Effective_Date__c = string.valueOf(system.today());
            insert oPolMem;
            System.assertEquals(oPolMem.Effective_Date__c,string.valueOf(system.today()));
    
            List<Case> csList = new List<Case>();
            Case oCase = HUMTestDataHelperTest.getCase();
            oCase.Origin = 'Inbound Call';
            oCase.AccountId = oAcc.Id;
            oCase.Interacting_With__c = oAcc.Id;
            oCase.Policy_Member__c = oPolMem1.Id;
            csList.add(oCase);
             
            oCase = HUMTestDataHelperTest.getCase ();
            oCase.Origin = 'Inbound Call';
            oCase.AccountId = oAcc.Id;
            oCase.Interacting_With__c = oAcc.Id;
            oCase.Policy_Member__c = null;
            csList.add(oCase);          
            
            oCase = HUMTestDataHelperTest.getCase();
            oCase.Origin = 'Inbound Call';
            oCase.AccountId = oAcc.Id;
            oCase.Interacting_With__c = oAcc.Id;
            oCase.Policy_Member__c =oPolMem.Id;
            csList.add(oCase);
            
            oCase = HUMTestDataHelperTest.getCase();
            oCase.Origin = '';
            oCase.AccountId = oAccProv.Id;
            oCase.Interacting_With__c = oAccProv.Id;
            csList.add(oCase);
            
            oCase = HUMTestDataHelperTest.getCase();
            oCase.Origin = 'Outbound Call';
            oCase.AccountId = oAccGrp.Id;
            oCase.Interacting_With__c = oAccGrp.Id;
            csList.add(oCase);
            
            oCase = HUMTestDataHelperTest.getCase();
            oCase.Origin = '';
            oCase.AccountId = oAccAgent.Id;
            oCase.Interacting_With__c = oAccAgent.Id;
            csList.add(oCase);
            
            oCase = HUMTestDataHelperTest.getCase();
            oCase.Origin = '';
            oCase.AccountId = oAccBroker.Id;
            oCase.Interacting_With__c = oAccBroker.Id;
            csList.add(oCase);
            
            oCase = HUMTestDataHelperTest.getCase();
            oCase.Origin = '';
            oCase.Interacting_About_Type__c = 'Unknown-Group';
            oCase.Interacting_With_Type__c = 'Unknown-Group';
            oCase.AccountId = oAccUnGrp.Id;
            oCase.Interacting_With__c = oAccUnGrp.Id;
            csList.add(oCase);
            
            oCase = HUMTestDataHelperTest.getCase();
            oCase.Origin = '';
            oCase.AccountId = oAccUnMember.Id;
            oCase.Interacting_With__c = oAccUnMember.Id;
            csList.add(oCase);
            
            insert csList;
            system.assertEquals(csList.size(),9);
            
        }       
    }
    
    /*
     * Method Name : testGetInputDetailsMember
     * Description : Test method to assign parameters when Accounts related to case are of Record Type Member 
     * Return type : void
     * Paramater   : NA
     */     
    static testMethod void testGetInputDetailsMember()
    {
        User userObj = [select Id,Name from User where name = 'test User1' limit 1];
        System.runAs(userObj)
        {
            test.startTest();
                Case oCase = [Select Id , AccountId , Account.Enterprise_Id__c from Case where Account.Enterprise_Id__c = '1007896' Limit 1];
                ApexPages.StandardController stdCntrl = new ApexPages.StandardController(oCase);
                HUMLaunchGCPFromCaseController clsLaunchGCP = new HUMLaunchGCPFromCaseController(stdCntrl);
                clsLaunchGCP.sCaseId = null;
                List<Error_log__c> lstErr = clsLaunchGCP.listErrorLogs;
            test.stopTest();
    
            system.AssertEquals(clsLaunchGCP.sInteractingAboutId, '1007896');
            system.AssertEquals(clsLaunchGCP.sInteractingWithId, '1007896');
        }
    }

    /*
     * Method Name : testGetInputDetailsMemberWithPolicy
     * Description : Test method to assign parameters when Accounts related to case are of Record Type Member and Policy Member on case is not null
     * Return type : void
     * Paramater   : NA
     */ 
    static testMethod void testGetInputDetailsMemberWithPolicy()
    {
       
       User userObj = [select Id,Name from User where name = 'test User1' limit 1];
        System.runAs(userObj)
        {
            test.startTest();
                Case oCase = [Select Id , AccountId , Account.Enterprise_Id__c from Case where Policy_Member__r.Policy__r.Platform__c = 'DM' And Account.Enterprise_Id__c = '1007896' limit 1];
                ApexPages.StandardController stdCntrl = new ApexPages.StandardController(oCase);
                HUMLaunchGCPFromCaseController clsLaunchGCP = new HUMLaunchGCPFromCaseController(stdCntrl);
                clsLaunchGCP.listErrorLogs = new list<Error_log__c>();
                List<Error_log__c> lstErr = clsLaunchGCP.listErrorLogs;
            test.stopTest();
            
            system.AssertEquals(clsLaunchGCP.bIsPolicyMemberPresent, true);
            system.AssertEquals(clsLaunchGCP.sInteractingWithCovergeEffDate, string.valueOf(system.today()));
        }
    }
     /*
     * Method Name : testGetInputDetailsMemberWithoutPolicymem
     * Description : Test method to assign parameters when Accounts related to case are of Record Type Member and Policy Member on case is null
     * Return type : void
     * Paramater   : NA
     */ 
    static testMethod void testGetInputDetailsMemberWithoutPolicymem()
    {
        User userObj = [select Id,Name from User where name = 'test User1' limit 1];
        System.runAs(userObj)
        {
            test.startTest();
                Case oCase = [Select Id , AccountId , Account.Enterprise_Id__c from Case where Policy_Member__c = null And Account.Enterprise_Id__c = '1007896' limit 1];
                ApexPages.StandardController stdCntrl = new ApexPages.StandardController(oCase);
                HUMLaunchGCPFromCaseController clsLaunchGCP = new HUMLaunchGCPFromCaseController(stdCntrl);
            test.stopTest();
            
            system.AssertEquals(clsLaunchGCP.bIsPolicyMemberPresent, false);
        }

    }
    

     /*
     * Method Name : testGetInputDetailsMemberWithFIMMASPolicy
     * Description : Test method to assign parameters when Accounts related to case are of Record Type Member and Policy Member on case is of FIMMAS Type
     * Return type : void
     * Paramater   : NA
     */ 
    static testMethod void testGetInputDetailsMemberWithFIMMASPolicy()
    {
        User userObj = [select Id,Name from User where name = 'test User1' limit 1];
        System.runAs(userObj)
        {
            test.startTest();
                Member_id__c memberIdObj = [Select Id , Name from Member_id__C limit 1];
                Case oCase = [Select Id , AccountId , Account.Enterprise_Id__c from Case where Policy_Member__r.Policy__r.Platform__c = 'FM' And Account.Enterprise_Id__c = '1007896' limit 1];
                Policy_Member__c PolEffDate= [Select Id,CreatedDate,Name,Effective_Date__c from Policy_Member__c  where Member__c =:oCase.Account.Id limit 1];
                ApexPages.StandardController stdCntrl = new ApexPages.StandardController(oCase);
                HUMLaunchGCPFromCaseController clsLaunchGCP = new HUMLaunchGCPFromCaseController(stdCntrl);
            test.stopTest();
            
            system.AssertEquals(clsLaunchGCP.bIsPolicyMemberPresent, true);
            system.AssertEquals(clsLaunchGCP.sInteractingAboutCovergeEffDate , PolEffDate.Effective_Date__c);
        }
                  
    }
    
   
    /*
     * Method Name : testGetInputDetailsGroup
     * Description : Test method to assign parameters when Accounts related to case are of Record Type Group 
     * Return type : void
     * Paramater   : NA
     */ 
    static testMethod void testGetInputDetailsGroup()
    {
        User userObj = [select Id,Name from User where name = 'test User1' limit 1];
        System.runAs(userObj)
        {
            test.startTest();
                Case oCase = [Select Id , AccountId , Account.Enterprise_Id__c from Case where Account.Enterprise_Id__c = '112111' limit 1];
                ApexPages.StandardController stdCntrl = new ApexPages.StandardController(oCase);
                HUMLaunchGCPFromCaseController clsLaunchGCP = new HUMLaunchGCPFromCaseController(stdCntrl);
            test.stopTest();
            
            system.AssertEquals(clsLaunchGCP.sInteractingAboutType, 'KE');
            system.AssertEquals(clsLaunchGCP.sInteractingWithType, 'KE');
        }
    }

    /*
     * Method Name : testGetInputDetailsProvider
     * Description : Test method to assign parameters when Accounts related to case are of Record Type Provider 
     * Return type : void
     * Paramater   : NA
     */ 
    static testMethod void testGetInputDetailsProvider()
    {
       User userObj = [select Id,Name from User where name = 'test User1' limit 1];
        System.runAs(userObj)
        {
            test.startTest();
                Case oCase = [Select Id , AccountId , Account.Enterprise_Id__c from Case where Account.Enterprise_Id__c = '112112' limit 1];
                ApexPages.StandardController stdCntrl = new ApexPages.StandardController(oCase);
                HUMLaunchGCPFromCaseController clsLaunchGCP = new HUMLaunchGCPFromCaseController(stdCntrl);
            test.stopTest();
            
            system.AssertEquals(clsLaunchGCP.sInteractingAboutProviderId, '');
            system.AssertEquals(clsLaunchGCP.sInteractingWithProviderId, '');
        }
    }

    /*
     * Method Name : testGetInputDetailsAgent
     * Description : Test method to assign parameters when Accounts related to case are of  Type Agent 
     * Return type : void
     * Paramater   : NA
     */     
    static testMethod void testGetInputDetailsAgent()
    {
        User userObj = [select Id,Name from User where name = 'test User1' limit 1];
        System.runAs(userObj)
        {
            test.startTest();
                ConsumerID__c   objConsumer = [Select Consumer_ID__c from ConsumerID__c where name = 'abc' limit 1];
                Case oCase = [Select Id , AccountId , Account.Enterprise_Id__c from Case where Account.Enterprise_Id__c = '112113' limit 1];
                ApexPages.StandardController stdCntrl = new ApexPages.StandardController(oCase);
                HUMLaunchGCPFromCaseController clsLaunchGCP = new HUMLaunchGCPFromCaseController(stdCntrl);
            test.stopTest();
            
            system.AssertEquals(clsLaunchGCP.sInteractingAboutAgentId, objConsumer.Consumer_ID__c);
            system.AssertEquals(clsLaunchGCP.sInteractingWithAgentId, objConsumer.Consumer_ID__c); 
        }
            
    }
    
    /*
     * Method Name : testGetInputDetailsBroker
     * Description : Test method to assign parameters when Accounts related to case are of  Type Broker 
     * Return type : void
     * Paramater   : NA
     */     
    static testMethod void testGetInputDetailsBroker()
    {
        User userObj = [select Id,Name from User where name = 'test User1' limit 1];
        System.runAs(userObj)
        {
            test.startTest();
                ConsumerID__c   objConsumer = [Select Consumer_ID__c from ConsumerID__c where name='pqr' limit 1];
                Case oCase = [Select Id , AccountId , Account.Enterprise_Id__c from Case where Account.Enterprise_Id__c = '112114' limit 1];
                ApexPages.StandardController stdCntrl = new ApexPages.StandardController(oCase);
                HUMLaunchGCPFromCaseController clsLaunchGCP = new HUMLaunchGCPFromCaseController(stdCntrl);
            test.stopTest();
            
            system.AssertEquals(clsLaunchGCP.sInteractingAboutAgentId, objConsumer.Consumer_ID__c);
            system.AssertEquals(clsLaunchGCP.sInteractingWithAgentId, objConsumer.Consumer_ID__c); 
        }
            
    }

    /*
     * Method Name : testGetInputDetailsUnknownGroup
     * Description : Test method to assign parameters when Accounts related to case are of Record Type Unknown Group 
     * Return type : void
     * Paramater   : NA
     */ 
    static testMethod void testGetInputDetailsUnknownGroup()
    {
        User userObj = [select Id,Name from User where name = 'test User1' limit 1];
        System.runAs(userObj)
        {
            test.startTest();
                Case oCase = [Select Id , AccountId , Account.Enterprise_Id__c from Case where Account.Enterprise_Id__c = '13324' limit 1];
                ApexPages.StandardController stdCntrl = new ApexPages.StandardController(oCase);
                HUMLaunchGCPFromCaseController clsLaunchGCP = new HUMLaunchGCPFromCaseController(stdCntrl);
                clsLaunchGCP.getUnknownAccountDetails();
            test.stopTest();
            system.AssertEquals(clsLaunchGCP.sInteractingWithMemberIdBase, null);
        }
    }

    /*
     * Method Name : testGetInputDetailsUnknownMember
     * Description : Test method to assign parameters when Accounts related to case are of Record Type Unknown Member 
     * Return type : void
     * Paramater   : NA
     */ 
    static testMethod void testGetInputDetailsUnknownMember()
    {
        User userObj = [select Id,Name from User where name = 'test User1' limit 1];
        System.runAs(userObj)
        {
            test.startTest();
                Case oCase = [Select Id , AccountId , Account.Enterprise_Id__c from Case where Account.Enterprise_Id__c = '13325' limit 1];
                ApexPages.StandardController stdCntrl = new ApexPages.StandardController(oCase);
                HUMLaunchGCPFromCaseController clsLaunchGCP = new HUMLaunchGCPFromCaseController(stdCntrl);
                clsLaunchGCP.getUnknownAccountDetails();
            test.stopTest();
            system.AssertEquals(clsLaunchGCP.sInteractingAboutSubType, 'A');
            system.AssertEquals(clsLaunchGCP.sInteractingWithSubType, 'A');   
        }
    }
    
    /*
     * Method Name : testExceptions
     * Description : Test method to test exceptions
     * Return type : void
     * Paramater   : NA
     */     
    static testMethod void testExceptions()
    {
        User userObj = [select Id,Name from User where name = 'test User1' limit 1];
        System.runAs(userObj)
        {
            test.startTest();
                Case oCase = new Case();
                ApexPages.StandardController stdCntrl = new ApexPages.StandardController(oCase);
                HUMLaunchGCPFromCaseController clsLaunchGCP = new HUMLaunchGCPFromCaseController(stdCntrl);
            test.stopTest();
    
            system.AssertEquals(clsLaunchGCP.sInteractingAboutId, null);
        }
    }
}