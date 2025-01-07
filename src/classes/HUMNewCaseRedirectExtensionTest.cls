/****************************************************************************
Apex Class Name  : HUMNewCaseRedirectExtensionTest
Version          : 1.0
Created On       : JUNE 06 2014
Function         : Test class for HUMNewCaseRedirectExtension apex class

Modification Log :
     Developer Name            Code Review                  Date                   Description
* ------------------------------------------------------------------------------------------------------------------------------
     Sagar Tapadia               17093                       06/06/2014             Original Version
     Pradeep Raju                17093                       06/21/2014             updated it based on Review Comments 
     Pradeep Raju                17093                       06/21/2014             Added Test.startTest and Test.stopTest. Also added more assert statements
     SuryaKumari Medicherla      17093                       07/19/2014             Added functionality to increase code coverage
     Santhi Mandava              18032                       09/16/2014             Removed hardcoded urls.
     SuryaKumari Medicherla      18032                       09/18/2014             Added code to avoid users to create a case without being in a Queue
     SuryaKumari Medicherla      18257                       09/30/2014             Added lines of code to assign values to Interaction About Type when user trying to create a case from account,Defect:156164
     SuryaKumari Medicherla      18446                       10/10/2014             Added variable to display new cases in different tabs,Defect:157175,157474
     Mrudula Jaddu               19591                       12/01/2014             code change as per CC#19591    
     SuryaKumari Medicherla      19911                       12/12/2014             Code modified to replace custom settings with custom object (REQ-72809)  
     RanjeethNagishetty          20424                       01/12/2014             Test class updated 
     Asha Deepika Koyyana        20572                       01/28/2015             Fixed test class failure for Defect#164139
     Manish Kumar                27139                       10/14/2015             Modified test class with new standards
     Kritika Popat               28219                       11/20/2015             Worked on changes related to REQ 161520 and code coverage 
     Prachi Bhasin               33486                       07/18/2016             Changes made as part of REQ- 270349
     Pradeepkumar Dani           99570                       06/08/2017             Intent__c.Classification__c & Intent__c.Etl_Idtfr__c Clean up
     Pradeepkumar Dani          100996                       06/13/2017             Classification__c.Classification_Type__c & Etl_Idftr__c Clean Up
     Vandana Chaudhari                                       07/24/2018             Code changes as per REQ - 370601 CA Ticket 7646745 Fix: Web Chat Auto Create Case
     Santhi Mandava                                          03/12/2019             Removed "DpaaSWebServiceMockImpl" usage.
     Kiran Bhuvanagiri                                       09/22/2020             User Story - 1445907: Update 'Created by Queue', 'Last Modified By Queue' and Case Record Type for PDP Pilot Users (CRMS)
     Vamshi Krishna Pemberthi                                06/08/2021        		Code Coverage Improvements
	 Vamshi Krishna Pemberthi                                06/17/2021        		Code Coverage Improvements
	 Ashok Kumar Nutalapati            						 09/30/2021             REQ - 2593758 T1PRJ0002517 - MF 2 - New case button, create case, & open new case in a subtab (RXRF) (ID# 35b)
**********************************************************************************************************************************/

@isTest
private class HUMNewCaseRedirectExtensionTest
{
   /**
    * testsetup method to create test data
    *<p>
    * @param            NULL
    * @return           void
    */
    @testSetup private static void loadTestData()
    {
        User oUser = HUMTestDataHelperTest.createTestUser('User1', 'Customer Care Specialist');
        insert oUser;
        System.assertEquals(oUser.LastName, 'test User1');
        
        Group oGrp = NULL;
        Group oGrp2 = NULL;
        Group oGrp3 = NULL;
        
        oGrp=new Group(Name='Cincinnati_Calls1', Type='QUEUE');
        insert oGrp;
        
        oGrp3=new Group(Name='PDP Pilot Humana Pharmacy Calls PGQ', Type='Regular');
        insert oGrp3;
        
        GroupMember grpMem1 = new GroupMember();
        grpMem1.UserOrGroupId = oUser.id;
        grpMem1.GroupId = oGrp3.Id;
        Insert grpMem1;
        system.debug('grpMem1--'+grpMem1);
        // Query your permission set name from Organization that you want to test.
        PermissionSet ps = [SELECT Id FROM PermissionSet WHERE Name = 'CRMS_205_CCSPDPPharmacyPilot'];
       
        // Assign the above inserted user for the above Permission Set.
        PermissionSetAssignment psa = new PermissionSetAssignment();
        psa.AssigneeId = oUser.Id;
        psa.PermissionSetId = ps.Id;
        insert psa;
        
        system.assertNotEquals(oGrp,null,'Value not matched');
        System.assertEquals(oGrp.Name, 'Cincinnati_Calls1');
        
        oGrp2 = new Group(Name='CincinnatiCalls1', Type='QUEUE');
        insert oGrp2;
        system.assertNotEquals(oGrp2,null,'Value not matched');
        System.assertEquals(oGrp2.Name, 'CincinnatiCalls1');
        
        QueuesObject oQueueObject = new QueuesObject (QueueID = oGrp.id, SobjectType ='Work_Queue_Setup__c');
        insert oQueueObject ;   
        system.assertNotEquals(oQueueObject,null,'Insert Failed');
        System.assertEquals(oQueueObject.SobjectType, 'Work_Queue_Setup__c');
        
        QueuesObject oQueueObject2 = new QueuesObject (QueueID = oGrp2.id, SobjectType ='Work_Queue_Setup__c');
        insert oQueueObject2;   
        system.assertNotEquals(oQueueObject2,null,'Queue Not inserted');
        System.assertEquals(oQueueObject2.SobjectType, 'Work_Queue_Setup__c');
        
        System.runAs(oUser)
        {
            Classification_Type__c ctRec0 = New Classification_Type__c();
                ctRec0.name = 'HP Finance Ops';
                ctRec0.Is_Active__c=true;           
            Insert ctRec0;
            
            Work_Queue_Setup__c oWQS = new Work_Queue_Setup__c(Classification_Type__c = ctRec0.id,Public_Group_Name__c = 'Group one', Service_Center__c = 'Cincinnati', Department__c = 'Calls1', Name = 'Cincinnati_Calls1');
            oWQS.OwnerId = oGrp.Id;
            insert oWQS;
            System.assert(oWQS != null);
            System.assertEquals(oWQS.Public_Group_Name__c, 'Group one');
            
            Account oAccount = HUMTestDataHelperTest.getAccount();
            oAccount.Name = 'New Case Account';
            oAccount.Group_Number__c  = '123';
            oAccount.BillingState = 'New York';
            oAccount.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Provider').getRecordTypeId();
            insert oAccount;
            System.assert(oAccount.id != null);
            System.assertEquals(oAccount.BillingState, 'New York');
            
            ConsumerID__c oConsumer = new ConsumerID__c(Account__c = oAccount.id, Consumer_ID__c = '12345');
            insert oConsumer;
            System.assert(oConsumer.id != null);
            System.assertEquals(oConsumer.Account__c, oAccount.id);
            
            
            HUMCaseInteractionAboutType__c oCaseInterAbotType = new HUMCaseInteractionAboutType__c();
            oCaseInterAbotType.Name = 'Provider';
            oCaseInterAbotType.CaseRecordType__c = 'Provider';
            oCaseInterAbotType.CaseInteraction_About_Type__c = 'Provider';
            insert oCaseInterAbotType;
            System.assert(oCaseInterAbotType.id != null);
            System.assertEquals(oCaseInterAbotType.CaseInteraction_About_Type__c, 'Provider');
            
            HUMCaseNPI_TAXID__c oTaxNPI = new HUMCaseNPI_TAXID__c(name = 'Provider');
            insert oTaxNPI;
            System.assert(oTaxNPI.id != null);
            System.assertEquals(oTaxNPI.name, 'Provider');
            
            Product2 oProduct = HUMTestDataHelperTest.getProduct();
            oProduct.Name='Mediclaim';
            insert oProduct;
            system.assertNotEquals(oProduct,null,'Product Object Insert Failed');
            System.assertEquals(oProduct.Name, 'Mediclaim');
            
            Policy__c oPolicy = HUMTestDataHelperTest.getPolicy();
            oPolicy.Name = 'test123';
            oPolicy.Group_Name__c = oAccount.Id;
            oPolicy.Funding_Type_Code__c = 'Y';
            oPolicy.Product__c = oProduct.Id;
            oPolicy.Issue_State__c = 'IL'; 
            insert oPolicy;
            system.assertNotEquals(oPolicy, null, 'Policy Object Insert Failed');
            System.assertEquals(oPolicy.Product__c, oProduct.Id);
            System.assertEquals(oPolicy.Funding_Type_Code__c, 'Y');
            
            Policy_Member__c oPolicyMem = HUMTestDataHelperTest.getPolicyMember();
            oPolicyMem.Name = '12345';
            oPolicyMem.Member__c = oAccount.Id;
            oPolicyMem.Policy__c = oPolicy.Id;            
            oPolicyMem.Relationship__c = 'Self';
            insert oPolicyMem;
            system.assertNotEquals(oPolicyMem, null, 'Policy Object Insert Failed');
            System.assertEquals(oPolicyMem.Relationship__c, 'Self');
            System.assertEquals(oPolicyMem.Policy__c, oPolicy.Id);
            
            List<Classification__c> lstClassification = new List<Classification__c>();
            Classification__c classificationRec = new Classification__c();
            classificationRec.Name = 'Claims';
            classificationRec.Is_Active__c = true;
            lstClassification.add(classificationRec);

            Classification__c classificationRec1 = new Classification__c();
            classificationRec1.Name = 'Billing (Finance Ops)';
            classificationRec1.Is_Active__c = true;
            lstClassification.add(classificationRec1); 
            
            Classification__c classificationRec2 = new Classification__c();
            classificationRec2.Name = 'Billing Issue (CT & TM)';
            classificationRec2.Is_Active__c = true;
            lstClassification.add(classificationRec2);
            
            insert lstClassification;
            system.assertEquals(lstClassification[0].Name=='Claims',true);
            
            List<Intent__c> lstIntent = new List<Intent__c>();
            Intent__c intentRec = new Intent__c();
            intentRec.Name = 'Filling';
            intentRec.Is_Active__c = true;
            lstIntent.add(intentRec);

            Intent__c intentRec1 = new Intent__c();
            intentRec1.Name = 'Invoice Requests';
            intentRec1.Is_Active__c = true;
            lstIntent.add(intentRec1);

            Intent__c intentRec2 = new Intent__c();
            intentRec2.Name = 'Make a Payment/Payment Changes';
            intentRec2.Is_Active__c = true;
            lstIntent.add(intentRec2);

            insert lstIntent;
            system.assertEquals(intentRec.Name == 'Filling', true);
            
            List<CTCI_Junction__c> ctciList = New list<CTCI_Junction__c>();
            CTCI_Junction__c ctciRec = New CTCI_Junction__c();
            ctciRec.Classification_Type__c = ctRec0.id;
            ctciRec.Classification__c = lstClassification[1].Id;
            ctciRec.Intent__c = lstIntent[1].id;
            ctciRec.IsActive__c = true;
            ctciList.add(ctciRec);
            
            CTCI_Junction__c ctciRec1 = New CTCI_Junction__c();
            ctciRec1.Classification_Type__c = ctRec0.id;
            ctciRec1.Classification__c = lstClassification[2].Id;
            ctciRec1.Intent__c = lstIntent[1].id;
            ctciRec1.IsActive__c = true;
            ctciList.add(ctciRec1);

            CTCI_Junction__c ctciRec2 = New CTCI_Junction__c();
            ctciRec2.Classification_Type__c = ctRec0.id;
            ctciRec2.Classification__c = lstClassification[2].Id;
            ctciRec2.Intent__c = lstIntent[2].id;
            ctciRec2.IsActive__c = true;
            ctciList.add(ctciRec2);

            Insert ctciList;

            Case oCase = new Case(AccountId = oAccount.Id, Intent_Id__c = lstIntent[0].id, Classification_Id__c =lstClassification[0].id, Policy_Member__c = oPolicyMem.Id,
                             Interacting_With_Type__c = 'Member',Interacting_With__c = oAccount.Id, Due_Date__c = Date.Today(), 
                             Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU', Work_Queue_View_Name__c='Cincinati CRU', Service_Center__c = 'Cincinnati',Department__c='CRU', status='In Progress');
            oCase.RecordTypeId = Schema.sObjectType.Case.getRecordTypeInfosByName().get('Provider Case').getRecordTypeId();
            insert oCase;
            System.assert(oCase.Interacting_With_Type__c == 'Member');
            System.assertEquals(oCase.Intent_Id__c, lstIntent[0].id);
            System.assertEquals(oCase.Classification_Id__c, lstClassification[0].id);
            
            
            Interaction__c oInteraction = HUMTestDataHelperTest.getInteraction();
            oInteraction.Interacting_With__c = oAccount.id;
            oInteraction.Interacting_With_type__c = 'Provider' ;
            oInteraction.Caller_Name__c = '111';
            insert oInteraction;
            System.assert(oInteraction.Interacting_With_type__c == 'Provider');
            System.assertEquals(oInteraction.Caller_Name__c, '111');
        
            Interaction_Member__c oIntMem = HUMTestDataHelperTest.getInteractionMember(); 
            oIntMem.Interaction__c = oInteraction.id;
            oIntMem.Interacting_About__c = oAccount.id;
            oIntMem.Interacting_About_Type__c = 'Provider';
            insert oIntMem; 
            System.assert(oIntMem.Interacting_About__c == oAccount.id);
            System.assertEquals(oIntMem.Interacting_About_Type__c, 'Provider');
            
            Case oCase1 = new Case(AccountId = oAccount.Id,Intent_Id__c = lstIntent[0].id, Classification_Id__c =lstClassification[0].id, Policy_Member__c = oPolicyMem.Id,
                             Interacting_With_Type__c = 'Member',Interacting_With__c= oAccount.Id, Due_Date__c=Date.Today(), 
                             Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c = 'Cincinnati CRU', Work_Queue_View_Name__c = 'Cincinati CRU', Service_Center__c = 'Cincinnati',Department__c = 'CRU', status = 'In Progress');
            oCase1.RecordTypeId = Schema.sObjectType.Case.getRecordTypeInfosByName().get('Group Case').getRecordTypeId();
            insert oCase1;
            System.assert(oCase1.Interacting_With_Type__c == 'Member');
            System.assertEquals(oCase.Intent_Id__c, lstIntent[0].id);
            System.assertEquals(oCase.Classification_Id__c, lstClassification[0].id);
            
             insert new HUMConstants__c [] { 
                new HUMConstants__c(Name = 'HUMChatIdParameter',StringValue__c='&chatId',IntegerValue__c=0),
                new HUMConstants__c(Name = 'OBJECT_ACCOUNT',StringValue__c='Account',IntegerValue__c=0),
                new HUMConstants__c(Name = 'HUM_Object_Policy',StringValue__c='Policy__C',IntegerValue__c=0),
                new HUMConstants__c(Name = 'OBJECT_POLICYMEMBER',StringValue__c='Policy_Member__c',IntegerValue__c=0)
                };  

                    Case invoiceRequestCase = new Case(AccountId = oAccount.Id,Intent_Id__c = lstIntent[1].id, Classification_Id__c =lstClassification[1].id, Policy_Member__c = oPolicyMem.Id,
                             Interacting_With_Type__c = 'Member',Interacting_With__c= oAccount.Id, Due_Date__c=Date.Today(), Classification_Type__c = 'HP Finance Ops',
                             Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c = 'Cincinnati CRU', Work_Queue_View_Name__c = 'Cincinati CRU', Service_Center__c = 'Cincinnati',Department__c = 'CRU', status = 'In Progress');
            invoiceRequestCase.RecordTypeId = Schema.sObjectType.Case.getRecordTypeInfosByName().get('HP Member Case').getRecordTypeId();
            insert invoiceRequestCase;
            
                        Case invoiceRequestCase2 = new Case(AccountId = oAccount.Id,Intent_Id__c = lstIntent[1].id, Classification_Id__c =lstClassification[2].id, Policy_Member__c = oPolicyMem.Id,
                             Interacting_With_Type__c = 'Member',Interacting_With__c= oAccount.Id, Due_Date__c=Date.Today(), Classification_Type__c = 'HP Finance Ops',
                             Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c = 'Cincinnati CRU', Work_Queue_View_Name__c = 'Cincinati CRU', Service_Center__c = 'Cincinnati',Department__c = 'CRU', status = 'In Progress');
            invoiceRequestCase2.RecordTypeId = Schema.sObjectType.Case.getRecordTypeInfosByName().get('HP Member Case').getRecordTypeId();
            insert invoiceRequestCase2;

            Case invoiceRequestCase3 = new Case(AccountId = oAccount.Id,Intent_Id__c = lstIntent[2].id, Classification_Id__c =lstClassification[1].id, Policy_Member__c = oPolicyMem.Id,
                             Interacting_With_Type__c = 'Member',Interacting_With__c= oAccount.Id, Due_Date__c=Date.Today(), Classification_Type__c = 'HP Finance Ops',
                             Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c = 'Cincinnati CRU', Work_Queue_View_Name__c = 'Cincinati CRU', Service_Center__c = 'Cincinnati',Department__c = 'CRU', status = 'In Progress');
            invoiceRequestCase3.RecordTypeId = Schema.sObjectType.Case.getRecordTypeInfosByName().get('HP Member Case').getRecordTypeId();
            insert invoiceRequestCase3;

        }
    }
    
   /**
    * createNewCaseFromAccount
    * <p>
    * Test method to create case from Account relatedlist
    * @param            NULL
    * @return           void
    */
    static testMethod void createNewCaseFromAccount() 
    {
        User oUser = [Select id,Name,Current_queue__c from User where LastName = 'test User1' LIMIT 1 ] ;
        String sBaseURL = URL.getSalesforceBaseUrl().toExternalForm();
        
        System.runAs(oUser)
        {
            Account oAccount = [Select id, Name, Group_Number__c, BillingState, recordTypeId From Account Where Name = 'New Case Account' Limit 1];
            System.assert(oAccount.id != null);
            System.assertEquals(oAccount.BillingState, 'New York');
            
            Case oCase = [Select AccountId,Intent_Id__c,Classification_Id__c,Intent_Id__r.Name,Classification_Id__r.Name,Policy_Member__c,Interacting_With_Type__c,Interacting_With__c,Due_Date__c,Case_Owner__c,
                            Owner_Queue__c,Work_Queue_View_Name__c,Service_Center__c,Department__c,status
                            From Case Where AccountId=:oAccount.Id limit 1];
            System.assert(oCase.Id != null);
            System.assertEquals(oCase.Intent_Id__r.Name, 'Filling');
            System.assertEquals(oCase.Classification_Id__r.Name, 'Claims');
            
            Case oInvoiceReqCase = [Select AccountId,Intent_Id__c,Classification_Id__c,Intent_Id__r.Name,Classification_Id__r.Name,Policy_Member__c,Interacting_With_Type__c,Interacting_With__c,Due_Date__c,Case_Owner__c,
                            Owner_Queue__c,Work_Queue_View_Name__c,Service_Center__c,Department__c,status
                            From Case Where Classification_Type__c = 'HP Finance Ops' AND Classification_Id__r.Name = 'Billing (Finance Ops)' AND Intent_Id__r.Name != 'Make a Payment/Payment Changes'];
            
            Interaction__c oInteraction = [Select Interacting_With__c,Interacting_With_type__c,Caller_Name__c From Interaction__c Where Interacting_With__c=:oAccount.id];
            System.assert(oInteraction.id != null);
            System.assertEquals(oInteraction.Caller_Name__c, '111');
            
            Group oGrp = [Select Name,Type From Group Where Name='Cincinnati_Calls1' AND Type='QUEUE' limit 1];
            Group oGrp2 = [Select Name,Type From Group Where Name='CincinnatiCalls1' AND Type='QUEUE' limit 1];
            System.assertEquals(oGrp.Name, 'Cincinnati_Calls1');
            System.assertEquals(oGrp2.Name, 'CincinnatiCalls1');
            
            Work_Queue_Setup__c oWQS = [Select Public_Group_Name__c,Service_Center__c,Department__c,Name,OwnerId From Work_Queue_Setup__c Where OwnerId=:oGrp.Id];
            System.assertEquals(oWQS.Public_Group_Name__c, 'Group one');
            
            ApexPages.StandardController oStdController = new ApexPages.StandardController(oCase);
            ApexPages.currentPage().getParameters().put('objId', oAccount.id);
            HUMNewCaseRedirectExtension onewCase = new HUMNewCaseRedirectExtension(oStdController);
            HUMNewCaseRedirectExtension onewCase2 = new HUMNewCaseRedirectExtension(oStdController,true);
            System.assertEquals(onewCase2.objectId, oAccount.id);
			HUMNewCaseRedirectExtension onewCase4 = new HUMNewCaseRedirectExtension(oStdController,false,true);
			
            
            HUMNewCaseRedirectExtension caseRedExt = new HUMNewCaseRedirectExtension(NULL);
            caseRedExt.objectID = oAccount.Id;
            caseRedExt.sObjectName = oAccount.Id.getSobjectType().getDescribe().getName();
            caseRedExt.sStandardUrlForTab = '.com/' + oAccount.Id;
            System.assertEquals(caseRedExt.objectId, oAccount.id);
            
            CRMFunctionality_ONOFF_Switch__c objCS = new CRMFunctionality_ONOFF_Switch__c(
                Name = '1445907',
                IsON__c=true);
            
            insert objCS;
            
            ApexPages.StandardController oStdControllerIR = new ApexPages.StandardController(oInvoiceReqCase);
            ApexPages.currentPage().getParameters().put('objId', oAccount.id);
            HUMNewCaseRedirectExtension onewCaseIR = new HUMNewCaseRedirectExtension(oStdControllerIR);
            HUMNewCaseRedirectExtension onewCaseIR2 = new HUMNewCaseRedirectExtension(oStdController,true);
            
            Test.startTest();
            LoggingProcessLogic_S_HUM.bInvoiseRequest = true;
            
            caseRedExt.funcOnPageLoad();
            caseRedExt.saveCase();
            
            onewCaseIR.funcOnPageLoad();
            
            onewCaseIR.sStandardURLForTab = sBaseURL + '/' + oAccount.id + '&interactionId=' + oInteraction.id;
            onewCaseIR.saveCase();
            
            System.assertEquals(caseRedExt.objCase.Tax_ID__c, '12345');
            onewCase.funcOnPageLoad();
            onewCase.sStandardURLForTab = sBaseURL + '/' + oAccount.id + '&interactionId=' + oInteraction.id;
            onewCase.saveCase();
            System.assert(onewCase.objCase != null);
            System.assertEquals(onewCase.objCase.Interacting_About_Type__c, 'Provider');
            
            oUser.Current_queue__c = 'Cincinnati_Calls1';
            update oUser;
            System.assertEquals(oUser.Current_queue__C, 'Cincinnati_Calls1' );
            
            HUMNewCaseRedirectExtension onewCase3 = new HUMNewCaseRedirectExtension(oStdController);
            onewCase3.funcOnPageLoad();
            onewCase3.sStandardURLForTab = sBaseURL + '/' + oAccount.id + '&interactionId=' + oInteraction.id;
            onewCase3.saveCase();
            System.assert(onewCase3.objCase != null);
            
            oWQS.OwnerId= oGrp2.Id;
            update oWQS;
            system.assertNotEquals(oWQS.Work_Queue_Name__c, 'CincinnatiCalls1', 'Value matched');
            
            onewCase2.funcOnPageLoad();
            onewCase2.sStandardURLForTab = sBaseURL + '/' + oAccount.id + '&interactionId=' + oInteraction.id;
            onewCase2.saveCase();
            System.assert(onewCase2.objCase != null);
            System.assertEquals(onewCase2.objCase.Interacting_About_Type__c, 'Provider');
            System.assertEquals(onewCase2.objCase.Tax_ID__c, '12345'); 
            
            
            HUMNewCaseRedirectExtension caseRedExtWebChat = new HUMNewCaseRedirectExtension(oStdController);
             caseRedExtWebChat.objectID =  oAccount.Id;
             caseRedExtWebChat.sObjectName =  oAccount.Id.getSobjectType().getDescribe().getName();
             caseRedExtWebChat.sStandardUrlForTab ='&chatId';
             caseRedExtWebChat.funcOnPageLoad();
             caseRedExtWebChat.saveCase();  
             
            Test.stopTest();
        } 
    }
    
   /**
    * createNewCaseFromPolicy
    * <p>
    * Test method to create case from Policy relatedlist
    * @param            NULL
    * @return           void
    */
    static testMethod void createNewCaseFromPolicy() 
    {
        User oUser = [Select id,Name,Current_queue__c from User where LastName = 'test User1' LIMIT 1 ] ;
        String sBaseURL = URL.getSalesforceBaseUrl().toExternalForm();
        
        System.runAs(oUser)
        {
            Account oAccount = [Select id, Name, Group_Number__c, BillingState, recordTypeId From Account Where Name = 'New Case Account' Limit 1];
            System.assert(oAccount.id != null);
            System.assertEquals(oAccount.BillingState, 'New York');
            
            Case oCase = [Select AccountId,Intent_Id__c,Classification_Id__c,Intent_Id__r.Name,Classification_Id__r.Name,Policy_Member__c,Interacting_With_Type__c,Interacting_With__c,Due_Date__c,Case_Owner__c,
                            Owner_Queue__c,Work_Queue_View_Name__c,Service_Center__c,Department__c,status
                            From Case Where AccountId=:oAccount.Id limit 1];
            System.assert(oCase.Id != null);
            System.assertEquals(oCase.Intent_Id__r.Name, 'Filling');
            System.assertEquals(oCase.Classification_Id__r.Name, 'Claims');
            
            Policy__c oPolicy = [Select Id, Name, Group_Name__c from Policy__c where Group_Name__c =: oAccount.Id];  
            
            
            Interaction__c oInteraction = [Select Interacting_With__c,Interacting_With_type__c,Caller_Name__c From Interaction__c Where Interacting_With__c=:oAccount.id];
            System.assert(oInteraction.id != null);
            System.assertEquals(oInteraction.Caller_Name__c, '111');
            
            Group oGrp = [Select Name,Type From Group Where Name='Cincinnati_Calls1' AND Type='QUEUE' limit 1];
            Group oGrp2 = [Select Name,Type From Group Where Name='CincinnatiCalls1' AND Type='QUEUE' limit 1];
            System.assertEquals(oGrp.Name, 'Cincinnati_Calls1');
            System.assertEquals(oGrp2.Name, 'CincinnatiCalls1');
            
            Work_Queue_Setup__c oWQS = [Select Work_Queue_Name__c, Public_Group_Name__c,Service_Center__c,Department__c,Name,OwnerId From Work_Queue_Setup__c Where OwnerId=:oGrp.Id];
            System.assertEquals(oWQS.Public_Group_Name__c, 'Group one');
            
            ApexPages.StandardController oStdController = new ApexPages.StandardController(oCase);
            ApexPages.currentPage().getParameters().put('objId', oPolicy.id);
            HUMNewCaseRedirectExtension onewCase = new HUMNewCaseRedirectExtension(oStdController);
            System.assert(onewCase != null);            
            HUMNewCaseRedirectExtension onewCase2 = new HUMNewCaseRedirectExtension(oStdController);
            System.assert(onewCase != null);
            
            HUMNewCaseRedirectExtension oCaseRedExt = new HUMNewCaseRedirectExtension(NULL);
            oCaseRedExt.objectID = oPolicy.Id;
            oCaseRedExt.sObjectName = oPolicy.Id.getSobjectType().getDescribe().getName();
            oCaseRedExt.sStandardUrlForTab = '.com/' + oPolicy.Id;
            
            
            HUMNewCaseRedirectExtension oCaseRedExt1 = new HUMNewCaseRedirectExtension(NULL);
            oCaseRedExt1.objectID = oPolicy.Id;
            oCaseRedExt1.sObjectName = oPolicy.Id.getSobjectType().getDescribe().getName();
            oCaseRedExt1.sStandardUrlForTab = '.com/' + oPolicy.Id + '&chatId=' + oPolicy.Id;
            
            
            Test.startTest();
            
            oCaseRedExt.funcOnPageLoad();
            oCaseRedExt.saveCase();
            oCaseRedExt1.funcOnPageLoad();
            oCaseRedExt1.saveCase();
            System.assertEquals(oCaseRedExt1.objCase.Interacting_About_Type__c, 'Provider');
            System.assertEquals(oCaseRedExt1.objCase.Policy__c, oPolicy.Id);
            
            onewCase.funcOnPageLoad();
            onewCase.sStandardURLForTab = sBaseURL + '/' +  oPolicy.Id + '&interactionId=' + oInteraction.id;
            onewCase.saveCase();
            System.assert(onewCase.objCase != null);
            System.assertEquals(onewCase.objCase.Policy__c, oPolicy.Id);
            
            oUser.Current_queue__c = 'Cincinnati_Calls1';
            update oUser;
            System.assertEquals(oUser.Current_queue__C, 'Cincinnati_Calls1');
            
            HUMNewCaseRedirectExtension onewCase3 = new HUMNewCaseRedirectExtension(oStdController);
            onewCase3.funcOnPageLoad();
            onewCase3.sStandardURLForTab = sBaseURL + '/' +  oPolicy.Id + '&interactionId=' + oInteraction.id;
            onewCase3.saveCase();
            System.assertEquals(onewCase3.objCase.Policy__c, oPolicy.Id);
            System.assertEquals(onewCase3.objCase.Interacting_With_Type__c, 'Provider');
            
            oWQS.OwnerId= oGrp2.Id;
            update oWQS;
            system.assertNotEquals(oWQS.Work_Queue_Name__c, 'CincinnatiCalls1', 'Value matched');
            
            onewCase2.funcOnPageLoad();
            onewCase2.sStandardURLForTab = sBaseURL + '/' +  oPolicy.Id + '&interactionId=' + oInteraction.id;
            onewCase2.saveCase();
            System.assert(onewCase2.objCase != null);
            System.assertEquals(onewCase2.objCase.Policy__c, oPolicy.Id);
            System.assertEquals(onewCase2.objCase.Interacting_About_Type__c, 'Provider');
            
            oUser.Current_queue__c = 'Cincinnati Calls1';
            update oUser;
            System.assert(oUser.Current_queue__c == 'Cincinnati Calls1');
            HUMNewCaseRedirectExtension onewCase4 = new HUMNewCaseRedirectExtension(oStdController);
            onewCase4.funcOnPageLoad();
            System.assertEquals(onewCase4.objCase.Policy__c, oPolicy.Id);
            onewCase4.sStandardURLForTab = sBaseURL + '/' +  oPolicy.Id + '&chatId=' + oInteraction.id;
            onewCase4.saveCase();
            System.assert(onewCase4.objCase != null);
            System.assertEquals(onewCase4.objCase.Policy__c, oPolicy.Id);
            System.assertEquals(onewCase4.objCase.Tax_ID__c, '12345');            
           
            Test.stopTest();
        } 
    }
    
   /**
    * createNewCaseFromPolicyMember
    * <p>
    * Test method to create case from PolicyMember relatedlist
    * @param            NULL
    * @return           void
    */
    static testMethod void createNewCaseFromPolicyMember() 
    {
        User oUser = [Select id,Name,Current_queue__c from User where LastName = 'test User1' LIMIT 1 ] ;
        
        String sBaseURL = URL.getSalesforceBaseUrl().toExternalForm();
        
        System.runAs(oUser)
        {
            Test.startTest();
            
            Account oAccount = [Select Name, Group_Number__c, BillingState, recordTypeId From Account Where Name = 'New Case Account' Limit 1];
            System.assert(oAccount.id != null);
            System.assertEquals(oAccount.BillingState, 'New York');
            
            Case oCase = [Select AccountId,Intent_Id__c,Classification_Id__c,Intent_Id__r.Name,Classification_Id__r.Name,Policy_Member__c,Interacting_With_Type__c,Interacting_With__c,Due_Date__c,Case_Owner__c,
                            Owner_Queue__c,Work_Queue_View_Name__c,Service_Center__c,Department__c,status
                            From Case Where AccountId=:oAccount.Id limit 1];
            System.assert(oCase.Id != null);
            System.assertEquals(oCase.Intent_Id__r.Name, 'Filling');
            System.assertEquals(oCase.Classification_Id__r.Name, 'Claims');
            
            Interaction__c oInteraction = [Select Interacting_With__c,Interacting_With_type__c,Caller_Name__c From Interaction__c Where Interacting_With__c=:oAccount.id];
            System.assert(oInteraction.id != null);
            System.assertEquals(oInteraction.Caller_Name__c, '111');
            
            Policy_Member__c oPolicyMem = [Select Name,Member__c,Policy__c,Relationship__c From Policy_Member__c Where Member__c =: oAccount.Id];
            
            Interaction_Member__c oIntMem = [Select Interaction__c,Interacting_About__c,Interacting_About_Type__c,Interaction__r.Interacting_With_type__c,Interaction__r.Caller_Name__c From Interaction_Member__c Where Interaction__c=:oInteraction.id];

            LoggingProcessLogic_S_HUM.sPolicyMemberId = oPolicyMem.id;
			LoggingProcessLogic_S_HUM.interactionRecord = oIntMem;

            ApexPages.StandardController oStdController = new ApexPages.StandardController(oCase);
            ApexPages.currentPage().getParameters().put('objId', oPolicyMem.id);
            HUMNewCaseRedirectExtension onewCase = new HUMNewCaseRedirectExtension(oStdController);
        
            onewCase.funcOnPageLoad();
            System.assertNotEquals(onewCase.objCase.Policy_Member__c, null);
            onewCase.sStandardURLForTab = sBaseURL + '/' + oPolicyMem.id + '&interactionId=' + oInteraction.id;
            onewCase.saveCase();
            oUser.Current_queue__c = 'Cincinnati_Calls1';
            update oUser;
            System.assertEquals(oUser.Current_queue__C, 'Cincinnati_Calls1');
            
            HUMNewCaseRedirectExtension onewCase3 = new HUMNewCaseRedirectExtension(oStdController);
            onewCase3.funcOnPageLoad();
            onewCase3.sStandardURLForTab = sBaseURL + '/' + oPolicyMem.id + '&interactionId=' + oInteraction.id;
            onewCase3.saveCase();
            System.assert(onewCase3.objCase != null);
            System.assertEquals(onewCase3.objCase.Policy_Member__c, oPolicyMem.id);
            delete oIntMem;
            onewCase.saveCase();
            System.assertNotEquals(onewCase.objCase.Interacting_About_Type__c, null);
            onewCase3.checkPermissionandQueue();
            onewCase.sCurrentQueue = 'PDP Pilot Humana Pharmacy Calls';
            onewCase.saveCase();
            List<Case_Interaction__c> lstCaseInteraction = [Select Id From Case_Interaction__c Where Case__c =: onewCase.objCase.id AND Interaction__c =: oInteraction.id]; 
            System.assertEquals(lstCaseInteraction.size(), 0);
            
            Test.stopTest();
        } 
    }

    static testMethod void createNewCaseFromPolicyMemberPharma() 
    {
        User oUser = [Select id,Name,Current_queue__c from User where LastName = 'test User1' LIMIT 1 ] ;
        
        String sBaseURL = URL.getSalesforceBaseUrl().toExternalForm();
        
        System.runAs(oUser)
        {
            Test.startTest();
            
            Account oAccount = [Select Name, Group_Number__c, BillingState, recordTypeId From Account Where Name = 'New Case Account' Limit 1];
            System.assert(oAccount.id != null);
            System.assertEquals(oAccount.BillingState, 'New York');
            Case oCase = [Select AccountId,Intent_Id__c,Classification_Id__c,Intent_Id__r.Name,Classification_Id__r.Name,Policy_Member__c,Interacting_With_Type__c,Interacting_With__c,Due_Date__c,Case_Owner__c,
                            Owner_Queue__c,Work_Queue_View_Name__c,Service_Center__c,Department__c,status
                            From Case Where Classification_Type__c = 'HP Finance Ops' AND Classification_Id__r.Name = 'Billing (Finance Ops)' AND Intent_Id__r.Name = 'Make a Payment/Payment Changes'];
            
            System.assert(oCase.Id != null);
            System.assertEquals(oCase.Intent_Id__r.Name, 'Make a Payment/Payment Changes');
            System.assertEquals(oCase.Classification_Id__r.Name, 'Billing (Finance Ops)');
            
            Interaction__c oInteraction = [Select Interacting_With__c,Interacting_With_type__c,Caller_Name__c From Interaction__c Where Interacting_With__c=:oAccount.id];
            System.assert(oInteraction.id != null);
            System.assertEquals(oInteraction.Caller_Name__c, '111');
            
            Policy_Member__c oPolicyMem = [Select Name,Member__c,Policy__c,Relationship__c From Policy_Member__c Where Member__c =: oAccount.Id];
            
            Interaction_Member__c oIntMem = [Select Interaction__c,Interacting_About__c,Interacting_About_Type__c From Interaction_Member__c Where Interaction__c=:oInteraction.id];

            LoggingProcessLogic_S_HUM.sPolicyMemberId = oPolicyMem.id;

            ApexPages.StandardController oStdController = new ApexPages.StandardController(oCase);
            ApexPages.currentPage().getParameters().put('objId', oPolicyMem.id);
            HUMNewCaseRedirectExtension onewCase = new HUMNewCaseRedirectExtension(oStdController);
        
            onewCase.funcOnPageLoad();
            System.assertNotEquals(onewCase.objCase.Policy_Member__c, null);
            onewCase.sStandardURLForTab = sBaseURL + '/' + oPolicyMem.id + '&interactionId=' + oInteraction.id;
            onewCase.saveCase();
            oUser.Current_queue__c = 'Cincinnati_Calls1';
            update oUser;
            System.assertEquals(oUser.Current_queue__C, 'Cincinnati_Calls1');
            
            List<Case_Interaction__c> lstCaseInteraction = [Select Id From Case_Interaction__c Where Case__c =: onewCase.objCase.id AND Interaction__c =: oInteraction.id]; 
            System.assertEquals(lstCaseInteraction.size(), 0);
            
            Test.stopTest();
        } 
    }
    static testMethod void createNewCaseException() 
    {
        User oUser = [Select id,Name,Current_queue__c from User where LastName = 'test User1' LIMIT 1 ] ;
        String sBaseURL = URL.getSalesforceBaseUrl().toExternalForm();
        
        System.runAs(oUser)
        {
            Test.startTest();

            CRMFunctionality_ONOFF_Switch__c objCSEx = new CRMFunctionality_ONOFF_Switch__c(
                Name = '1445907',
                IsON__c=true);
                insert objcsEx;

            HUMNewCaseRedirectExtension ncase= new HUMNewCaseRedirectExtension(null);

            HUMNewCaseRedirectExtension ncase1= new HUMNewCaseRedirectExtension(null, true);
            Test.stopTest();
        }
    }
}