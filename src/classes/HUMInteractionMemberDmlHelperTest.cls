/****************************************************************************
Apex Class Name  : HUMInteractionMemberDmlHelperTest
Version          : 1.0 
Created Date     : JUNE 25 2014
Function         : Test class to HUMInteractionMemberDmlHelper Apex class
Modification Log :

 * Modification ID         Developer Name        Code Review               Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------
 * 1.0                     Ninad Patil                 16484                     25/06/2014                 Original Version
 * 1.1                     Ninad Patil                 17100                     05/08/2014                 Resubmitting After coding standard correction.
 * 1.2                     Ninad Patil                 17197                     08/11/2014                 Fixed smoke testing issue. 
 * 1.3                     Chaitanya Kumar             17722                     09/03/2014                 Created upsertConsumerIDs
 * 1.4                     Ranjeeth nagishetty         20287                     01/10/2015                 REQ-95950 validation rule
 * 1.5                     SuryaKumari Medicherla      20579                     01/28/2015                 Code modified to fix test class failure
 * 1.6                     Pradeep Veeravali           21397                     03/09/2015                 Modified method testUpsertAccount to fix CC Issue 201
 * 1.7                     Pradeep Veeravali           21519                     03/12/2015                 Modified method testUpsertAccount to revert back for fix Isssue 201
 * 1.8                     PraveenKumarParimi          27872                     11/17/2015                 Added method testInsertAccount
 * 1.9                     Santhi Mandava                                        03/12/2019                 Removed "DpaaSWebServiceMockImpl" usage.
 **********************************************************************************************************************************/
@isTest
private class HUMInteractionMemberDmlHelperTest
{
    /*
     * Method Name   :    testSaveInteraction() 
     * Description   :    This method tests 'Interaction member save' functionality.   
     * Return Type   :    void
     * Parameters    :    None
     */
    static testMethod void testSaveInteraction() 
    {        
        Interaction__c oInte = HUMTestDataHelperTest.getInteraction();
        insert oInte;
        System.assertNotEquals(oInte, NULL);
        List<Interaction_Member__c> lstInteractionMember = new List<Interaction_Member__c>();
        List<Account>lstAcc = new List<Account>();
        string smemberRecordtypeId=  Schema.SObjectType.Account.getRecordTypeInfosByName().get('Provider').getRecordTypeId();
        for(Integer i=0; i<200 ; i++)
        {
            lstAcc.add(new Account(name ='New Case Account' + i,recordtypeid =smemberRecordtypeId));
        }
        insert(lstAcc);
        for(Account oAcc : lstAcc)
        { 
            lstINteractionMember.add(
                    new Interaction_member__C(Interacting_About__c = oAcc.id,
                    Interaction__c = oInte.id,Interacting_About_Type__c = 'Provider'));
        }   
        Test.startTest();                                                   
        HUMInteractionMemberDmlHelper oHUMSaveInt = new HUMInteractionMemberDmlHelper();
        oHUMSaveInt.createInteractionMembers(lstInteractionMember);

        //Asserting if interaction Member records are created or not.
        System.assert(lstInteractionMember[0].id !=NULL);
        Test.stopTest();
    }
    /*
     * Method Name   :    testDeleteInteraction() 
     * Description   :    This method tests deletion of Interaction member functionality.   
     * Return Type   :    void
     * Parameters    :    None
     */
    static testMethod void testDeleteInteraction() 
    {        
        Interaction__c oInte = HUMTestDataHelperTest.getInteraction();
        insert oInte;
        System.assertNotEquals(oInte, NULL);
        List<Interaction_Member__c> lstInteractionMember = new List<Interaction_Member__c>();
        List<Account>lstAcc = new List<Account>();
        for(Integer i=0; i<200 ; i++)
        {
            lstAcc.add( new Account(name ='New Case Account' + i));
        }
        insert(lstAcc);
        for(Account oAcc : lstAcc)
        { 
            lstInteractionMember.add(
                    new Interaction_member__C(Interacting_About__c = oAcc.id,
                    Interaction__c = oInte.id));

        }
        Test.startTest();                                                   
        HUMInteractionMemberDmlHelper oHUMSaveInt = new HUMInteractionMemberDmlHelper();
        oHUMSaveInt.createInteractionMembers(lstInteractionMember);

        //Asserting if interaction Member records are created or not.
        //System.assert(lstINteractionMember[0].id !=NULL);

        Set<ID> setIDsToBeDel = new Set<ID>();
        for(Integer i=0; i<200; i++)
        {
            setIDsToBeDel.Add(lstAcc[i].ID);
        }
        oHUMSaveInt.deleteInqAbt(setIDsToBeDel,oInte.ID);
        List<Interaction_member__C> lstInqAbt = [SELECT ID FROM Interaction_member__C 
                                                 WHERE ID IN :setIDsToBeDel] ;
        //Asserting if all the interaction Member records are deleted Properly.                                                                          
        System.assert(lstInqAbt.size()== 0);
        Test.stopTest();
    }

    /*
     * Method Name : testSaveInteractionInNonDeveloperMode
     * Description : This method tests creation of case in non developer mode.
     * Return type : Void
     * Paramater   : None
     */
    static testMethod void testSaveInteractionInNonDeveloperMode() 
    {
        User testUser = HUMTestDataHelperTest.createTestUser('Customer_Case_specialist_User', 'Customer Care Specialist');
        System.assertNotEquals(testUser , NULL);
        system.runAs(testUser)
        {
            testSaveInteraction();
        }
    }

    /*
     * Method Name : testDeleteInteractionInNonDeveloperMode
     * Description : This method tests deletion of case in non developer mode.
     * Return type : Void
     * Paramater   : None
     */
    static testMethod void testDeleteInteractionInNonDeveloperMode() 
    {        
        User testUser = HUMTestDataHelperTest.createTestUser('Customer_Case_specialist_User', 'Customer Care Specialist');
        insert testUser;
        System.assertNotEquals(testUser , NULL);
        Interaction__c oInte = HUMTestDataHelperTest.getInteraction();
        insert oInte;
        System.assertNotEquals(oInte, NULL);
        List<Interaction_Member__c> lstInteractionMember = new List<Interaction_Member__c>();
        List<Account>lstAcc = new List<Account>();
        string sProviderRecordtypeId=  Schema.SObjectType.Account.getRecordTypeInfosByName().get('Provider').getRecordTypeId();
        for(Integer i=0; i<200 ; i++)
        {
            lstAcc.add( new Account(name ='New Case Account' + i,recordtypeid = sProviderRecordtypeId));
        }
        insert(lstAcc);
        for(Account oAcc : lstAcc)
        { 
            lstInteractionMember.add(
                    new Interaction_member__C(Interacting_About__c = oAcc.id,
                    Interaction__c = oInte.id ,Interacting_About_Type__c ='Provider'));            
        }
        insert  lstInteractionMember;
        System.assertNotEquals(lstInteractionMember, NULL);
        system.runAs(testUser)
        {
            Test.startTest();
            HUMInteractionMemberDmlHelper oHUMSaveInt = new HUMInteractionMemberDmlHelper();
            Set<ID> setIDsToBeDel = new Set<ID>();
            for(Integer i=0; i<200; i++)
            {
                setIDsToBeDel.Add(lstAcc[i].ID);
            }
            try
            {
                oHUMSaveInt.deleteInqAbt(setIDsToBeDel,oInte.ID);
            }
            catch(Exception e)
            {     
                //Asserting to check whether Error is thrown as Interaction members are being deleted by different User.
                system.assert(e.getMessage().contains(System.Label.HUMDiffUserErr));
            }   
            Test.StopTest();
        }
    }

    /*
     * Method Name : testCaseExistanceError
     * Description : This Test methods simulates the deletion of interaction member record for which Case is created.
     * Return type : Void
     * Paramater   : None
     */
    static testMethod void testCaseExistanceError() 
    {
        User testUser = HUMTestDataHelperTest.createTestUser('Customer_Case_specialist_User', 'Customer Care Specialist');
        insert testUser;
        System.assertNotEquals(testUser, NULL);
        Interaction__c oInte = HUMTestDataHelperTest.getInteraction();
        insert oInte;
        System.assertNotEquals(oInte, NULL);
        List<Interaction_Member__c> lstInteractionMember = new List<Interaction_Member__c>();
        List<Case_Interaction__C> lstCaseInteraction = new List<Case_Interaction__C>();
        List<Case> lstCase = new List<Case>();
        List<Account>lstAcc = new List<Account>();
        string sProviderRecordtypeId=  Schema.SObjectType.Account.getRecordTypeInfosByName().get('Provider').getRecordTypeId();
        //Creating accounts for association with Interaction Members and Cases.
        for(Integer i=0; i<200 ; i++)
        {
            lstAcc.add( new Account(name ='New Case Account' + i,recordtypeId =sProviderRecordtypeId));
        }
        insert(lstAcc);
        for(Account oAcc : lstAcc)
        { 
            lstInteractionMember.add(
                    new Interaction_member__C(Interacting_About__c = oAcc.id,
                    Interaction__c = oInte.id,Interacting_About_Type__c ='Provider' ));
            Case oCase = HUMTestDataHelperTest.getCase();
            oCase.AccountId = oAcc.Id; 
            lstCase.add(oCase);                         
        }
        insert lstCase;
        for(Case oCase : lstCase)
        { 
            lstCaseInteraction.add(new Case_Interaction__c(Case__C = oCase.id, Interaction__C = oInte.id));                     
        }
        insert lstCaseInteraction;
        System.assertNotEquals(lstCaseInteraction, NULL);
        insert  lstInteractionMember;
        System.assertNotEquals(lstInteractionMember, NULL);
        Test.startTest();
        HUMInteractionMemberDmlHelper oHUMSaveInt = new HUMInteractionMemberDmlHelper();
        Set<ID> setIDsToBeDel = new Set<ID>();
        for(Integer i=0; i<200; i++)
        {
            setIDsToBeDel.Add(lstAcc[i].ID);
        }
        try
        {
            oHUMSaveInt.deleteInqAbt(setIDsToBeDel,oInte.ID);
        }
        catch(Exception e)
        {                
            //Asserting to check whether exception is thrown if Interaction member being deleted has case associated with it.    
            system.assert(e.getMessage().contains(System.Label.HUMCaseCreatedErr));
        }
        system.runAs(testUser)
        {
            oHUMSaveInt = new HUMInteractionMemberDmlHelper();
            setIDsToBeDel = new Set<ID>();
            for(Integer i=0; i<200; i++)
            {
                setIDsToBeDel.Add(lstAcc[i].ID);
            }
            try
            {
                oHUMSaveInt.deleteInqAbt(setIDsToBeDel,oInte.ID);
            }
            catch(Exception e)
            {    
                //Asserting to check if Interaction member record being deleted is created by different user. 
                system.assert(e.getMessage().contains(System.Label.HUMDiffUserErr));
            }   
        }
        Test.StopTest();
    }
    /*
     * Method Name   :    testUpsertAccount() 
     * Description   :    This method tests Upsert Account functionality.   
     * Return Type   :    void
     * Parameters    :    None
     */
    static testMethod void testUpsertAccount() 
    {        
        User u = HUMTestDataHelperTest.createTestUser('TestUser1','System Administrator'); 
        insert u;
        System.assertNotEquals(u, NULL);
        HUMTestDataHelperTest.getHUMConstantsData();

        //Creating list of accounts to be upsertd.
        List<Account>lstAcc = new List<Account>();
        System.runas(u) 
        {  
            ID agentRecordtypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Agent/Broker').getRecordTypeId();
            for(Integer i=0; i<200 ; i++)            
            {   
                    lstAcc.add(new Account(name ='New Case Account' + i, Account_External_ID__c = '1234'+i,recordtypeid=agentRecordtypeId,Work_Email__c='jlpxvohr@wi.rr.com'));                
            }   
            Test.startTest();                                                   
            lstAcc = HUMInteractionMemberDmlHelper.upsertAccountForInteractionID(lstAcc);

            //Asserting if interaction Member records are created or not.
            System.assert(lstAcc[0].id !=NULL);
            Test.stopTest();
        }
    }
/*
     * Method Name   :    testInsertAccount() 
     * Description   :    This method tests Insert Account functionality.   
     * Return Type   :    void
     * Parameters    :    None
     */
    static testMethod void testInsertAccount() 
    {        
        User u = HUMTestDataHelperTest.createTestUser('TestUser1','System Administrator'); 
        insert u;
        System.assertNotEquals(u, NULL);
        HUMTestDataHelperTest.getHUMConstantsData();

        //Creating list of accounts to be upsertd.
        List<Account>lstAcc = new List<Account>();
        System.runas(u) 
        {  
            ID groupRecordtypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
            for(Integer i=0; i<200 ; i++)            
            {   
                    lstAcc.add(new Account(name ='New Case Account' + i, Account_External_ID__c = '1234'+i,recordtypeid=groupRecordtypeId));                
            }   
            Test.startTest();                                                   
            lstAcc = HUMInteractionMemberDmlHelper.insertAccountForInteractionID(lstAcc);

            //Asserting if interaction Member records are created or not.
            System.assert(lstAcc[0].id !=NULL);
            Test.stopTest();
        }
    }
    /*
     * Method Name   :    upsertConsumerIds
     * Description   :    This method tests Upsert ConsumerID functionality.   
     * Return Type   :    void
     * Parameters    :    NA
     */
    static testMethod void upsertConsumerIds() 
    {        

        User u = HUMTestDataHelperTest.createTestUser('TestUser1','System Administrator'); 
        insert u;
        System.assertNotEquals(u, NULL);
        HUMTestDataHelperTest.getHUMConstantsData();

        System.runas(u) 
        {    
            Account acc = HUMTestDataHelperTest.getAccount();
            acc.Name='Test Field';
            acc.Account_External_Id__c='1231';
            acc.RecordtypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Agent/Broker').getRecordTypeId();
            insert acc;
            System.assertNotEquals(acc, NULL);
            List<ConsumerId__c> lstConsumerIds = new List<ConsumerId__c>{
                                                new ConsumerId__c(consumer_id__c='2342',Consumer_External_ID__c='2343242',Account__c=acc.id),
                                                new ConsumerId__c(consumer_id__c='23a42',Consumer_External_ID__c='234d3242',Account__c=acc.id)};

            Test.startTest();

            HUMInteractionMemberDmlHelper.upsertConsumerIDofAccount(lstConsumerIds);

            Test.stopTest();

            //Assert a account record is created
            List<ConsumerId__c> lstConsumers = [Select id from ConsumerId__c limit 2];
            System.assertEquals(lstConsumers.size(),2);    
        }   
    }

}