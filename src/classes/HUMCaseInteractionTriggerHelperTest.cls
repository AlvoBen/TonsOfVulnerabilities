/****************************************************************************
Apex Class Name :  HUMCaseInteractionTriggerHelperTest 
Version: 1.0 
Created Date     : JUNE 27 2014
Function         : Test class to HUMCaseInteractionTriggerHelper Apex class
Modification Log : 
 * Modification ID        Developer Name              Code Review                        Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------
 * 1.0                    Ninad Patil                 16484                             27/06/2014                  Original Version
 * 1.1                    Ninad Patil                 17100                             08/05/2014                  Resubmitting after coding standards correction.
 * 1.3                    Shreya Choodamani           #####                             01/12/2015                  Fixed Test class error.
 * 1.4                    SuryaKumari Medicherla      20579                             01/12/2015                  Fixed Test class error.
 * 1.5                    Harshith Mandya                                               11/18/2016                  Updated test class to cover Current Queue scenario 
 * 1.6                    Sharan Shanmugam            50226                             11/30/2106                  Implemented REQ - 290990 -  removed all lookup related fields from 
                                                                                                                    Case/Task/Interaction/Attachment to Work Queue Setup and Queue View
 * 1.7                    Pradeepkumar Dani           99570                             06/08/2017                  Intent__c.Classification__c & Intent__c.Etl_Idtfr__c Clean up
 * 1.8                    Pradeepkumar Dani           100996                            06/13/2017                  Classification__c.Classification_Type__c & Etl_Idftr__c Clean Up
 * 1.9                    Pradeepkumar Dani           102268                            06/16/2017                  Case Clean Up (Classification__c,Intent__c,Classification_Ctci__c,Intent_Ctci__c,Classification_Text__c,Intent_Text__c)
 * 1.10                   Santhi Mandava                                                03/12/2019                  Removed "DpaaSWebServiceMockImpl" usage.
 **********************************************************************************************************************************/
@isTest
Private Class HUMCaseInteractionTriggerHelperTest
{
    /**
    * Test method used for overall testing for HUMTaskListViewController.
    * <p>
    * @return   :    void
    * @param    :    NA
    */
    @testSetup static void setup()
    {
        User oCCS = HUMTestDataHelperTest.createTestUser('CCSup','Customer Care Specialist');
        oCCS.Current_Queue__C = 'Cincinati_CRU1';
        insert oCCS; 
        System.assertEquals(oCCS.LastName,'test CCSup');
        
        Group oGrp = New Group(Name ='Cincinati_CRU1', Type='QUEUE');
        insert oGrp;
        System.assertEquals(oGrp.Name,'Cincinati_CRU1','Value not matched');
        
        QueuesObject oQueueObject = New QueuesObject(QueueID = oGrp.id, SobjectType ='Work_Queue_Setup__c');
        insert oQueueObject ;   
        System.assertEquals(oQueueObject.SobjectType,'Work_Queue_Setup__c','Value not matched');
        
        system.runAs(oCCS)
        {           
            List<RecordType> lstRecordTypeMemberIds = [SELECT Id  FROM RecordType WHERE Name = 'Member' LIMIT 1 ];
            id idMemberAccountRecordType = lstRecordTypeMemberIds[0].Id;
            
            List<RecordType> lstRecordTypeCaseIds = [SELECT Id  FROM RecordType WHERE Name = 'Member Case' LIMIT 1 ];
            id idMemberCaseRecordType = lstRecordTypeCaseIds[0].Id;            
            
            Account oAccount = New Account(FirstName ='Test' , LastName='Account1', recordtypeid=idMemberAccountRecordType);
            insert oAccount;
            system.assertEquals(oAccount.FirstName,'Test','Value not matched');
            
            Classification__c classificationRec = New Classification__c();
            classificationRec.Name = 'Enrollment/Effective Date Correction';
            classificationRec.Is_Active__c = true;
            insert classificationRec;
            system.assertEquals(classificationRec.Name, 'Enrollment/Effective Date Correction','Value not matched');
            
            Intent__c intentRec = New Intent__c();
            intentRec.Name = 'Rewards';
            intentRec.Is_Active__c = true;
            insert intentRec;
            system.assertEquals(intentRec.Name , 'Rewards','Value not matched');
            
            List<Classification_Type__c> ctList = new list<Classification_Type__c>();
            Classification_Type__c ctRec = new Classification_Type__c();
            ctRec.name = 'Claims (RSO)';
            ctRec.Is_Active__c = true;
            ctList.add(ctRec);
            insert ctList;
            system.assertEquals(ctList.size(), 1);


            List<CTCI_Junction__c> ctciList = new list<CTCI_Junction__c>();
            CTCI_Junction__c ctciRec = new CTCI_Junction__c();
            ctciRec.Classification_Type__c = ctRec.id;
            ctciRec.Classification__c = classificationRec.Id;
            ctciRec.Intent__c = intentRec.id;
            ctciRec.IsActive__c = true;
            ctciList.add(ctciRec);
            insert ctciList;
            system.assertEquals(ctciList.size(), 1);

            List<Case> lstCase = New List<Case>();
                      
            Case oCase = New Case(ownerid = oCCS.Id, AccountId = oAccount.Id, Interacting_With__c = oAccount.Id, Follow_Up_Due_Date__c = Date.Today(), Due_Date__c = Date.Today(), Case_Owner__c = oCCS.firstname + ' ' + oCCS.lastname, owner__C = oCCS.firstname + ' ' + oCCS.lastname ,  Work_Queue_View_Name__c='Other' , Owner_Queue__c='Cincinati_CRU1', status='In Progress', 
                                   Origin = 'Inbound Call', Type = 'Customer Inquiry', RecordTypeId = idMemberCaseRecordType, Interacting_With_Type__c = 'Member', Interacting_With_Name__c = 'TestAccount1', Interacting_About_Type__c = 'Member', 
                                   Classification_Type__c = 'Calls', Classification_Id__c = classificationRec.Id, CTCI_List__c = ctciRec.Id, G_A_Rights_Given__c = 'No', Complaint__c = 'Yes', Complaint_Reason__c = 'Customer Service Quality', Behavioral_Health__c = 'None');
            system.assertEquals(oCase.Origin, 'Inbound Call', 'Value not matched');
            lstCase.add(oCase);

            If (!lstCase.isEmpty()) insert lstCase;
            
            List<Interaction__C> lstInteraction= New List<Interaction__C>();
            For (Integer i= 0; i<200; i++)
            {
                lstInteraction.add(New Interaction__C(Interaction_Origin__c = 'Inbound Call', Caller_name__C = 'Test Caller'+ i));  
            }
            If (!lstInteraction.isEmpty())insert(lstInteraction);
            system.assert(lstInteraction.size() == 200);
            
            Work_Queue_Setup__c oWQSelector = New Work_Queue_Setup__c(Name = 'Cincinati_CRU1');
            oWQSelector.Public_Group_Name__c = 'PG Cincinati CRU1';
            oWQSelector.IsActive__c = True;
            oWQSelector.Service_Center__c = 'Cincinnati';
            oWQSelector.Department__c = 'CRU1';
            oWQSelector.General__c = true;
            oWQSelector.HO_Medical__c = true;
            oWQSelector.OwnerId = oGrp.id;
            insert oWQSelector;
            System.assertEquals(oWQSelector.Service_Center__c,'Cincinnati','Value not matched');
                  
        }
        
    }

    /**
    * Test method Is used to test insertion of Case Interaction
    * <p>
    * @return   :    void
    * @param    :    NA
    */
    Static testMethod void testCaseInteractionCreation() 
    {
        User oCCSup = [ SELECT Id, UserName, Current_queue__c FROM User WHERE LastName = 'test CCSup'];
        system.runAs(oCCSup)
        {   
            Case objCase = [Select Id from Case limit 1];
            List<Interaction__C> objInteraction = [Select Id , Created_By_Queue__c from Interaction__C limit 2 ];
            Test.startTest();
                Case_interaction__C objCaseInteraction = New Case_interaction__C(interaction__C = objInteraction[0].ID, Case__C = objCase.id);
                insert objCaseInteraction;
                Interaction__c objInteraction2 = [Select Id , Created_By_Queue__c from Interaction__C Where id = :objInteraction[0].ID limit 1 ];
                system.assert(objInteraction2.Created_By_Queue__c == oCCSup.Current_queue__c);
                Case_interaction__C objCaseInteraction1 = New Case_interaction__C(interaction__C = objInteraction[1].ID, Case__C = objCase.id);
                insert objCaseInteraction1;
                Interaction__c objInteraction3 = [Select Id , Created_By_Queue__c from Interaction__C Where id = :objInteraction[1].ID limit 1 ];
                system.assert(objInteraction3.Created_By_Queue__c == oCCSup.Current_queue__c);
            Test.stopTest();
        }
    }
}