/************************************************************************************************************************
Apex class Name    : Attachment_T_HUM
Version            : 1.0
Created On         : 11/18/2016
Function           : Test class for Attachment_H_HUM


    Modification Log
*   Developer                   Code Review         Date                Description
* -------------------------------------------------------------------------------------------------------------------------                
*   Harshith Mandya                               11/18/2016          Original Version  
*   Pradeepkumar Dani			99570			  06/08/2017		  Intent__c.Classification__c & Intent__c.Etl_Idtfr__c Clean up
*   Pradeepkumar Dani			100996 			  06/13/2017		  Classification__c.Classification_Type__c & Etl_Idftr__c Clean Up
*   Pradeepkumar Dani			102268			  06/16/2017		  Case Clean Up (Classification__c,Intent__c,Classification_Ctci__c,Intent_Ctci__c,Classification_Text__c,Intent_Text__c) 
*   Santhi Mandava                                08/28/2020          Fixed code coverage issue
************************************************************************************************************************/

@isTest
private class Attachment_T_HUM
{
    /**
    * Test method used for setup of test data.
    * <p>
    * @return   :    void
    * @param    :    NA
    */
    @testSetup static void setup()
    {
		User oCCS = HUMTestDataHelperTest.createTestUser('CCSup','Customer Care Specialist');
		oCCS.Current_Queue__C = 'Cincinati_CRU1';
		oCCS.firstname = 'test';
		insert oCCS; 
		System.assertEquals(oCCS.LastName,'test CCSup');
		
		Group oGroup = new Group(Type='Queue', Name='Cincinati CRU1');
		insert oGroup ;
		system.assertEquals(oGroup.Type, 'Queue', 'Value not matched');
		
		QueueSObject oQueue = new QueueSObject(SobjectType='Case', QueueId=oGroup.Id);
		insert oQueue;
		system.assertEquals(oQueue.SobjectType, 'Case', 'Value not matched');
		 
		Group oGroup1 = new Group(Type='Regular', Name='PG Cincinati CRU1');
		insert oGroup1;
		system.assertEquals(oGroup1.Type, 'Regular', 'Value not matched');
		
		GroupMember oGroupMember1 = new GroupMember(GroupId = oGroup1.Id, UserOrGroupId = oCCS.Id);
		insert oGroupMember1;
		system.assertEquals(oGroupMember1.GroupId, oGroup1.Id, 'Value not matched');
		
		GroupMember oGroupMember3 = new GroupMember(GroupId = oGroup.Id, UserOrGroupId = oCCS.Id);
		insert oGroupMember3;
		system.assertEquals(oGroupMember3.GroupId, oGroup.Id, 'Value not matched');
		
		Group oGrp=new Group(Name='Cincinati_CRU1', Type='QUEUE');
		insert oGrp;
		System.assertEquals(oGrp.Name,'Cincinati_CRU1','Value not matched');
		
		QueuesObject oQueueObject = new QueuesObject (QueueID = oGrp.id, SobjectType ='Work_Queue_Setup__c');
		insert oQueueObject ;   
		System.assertEquals(oQueueObject.SobjectType,'Work_Queue_Setup__c','Value not matched');
        
        oCCS = [Select firstName , lastname , name from USer where iD = :oCCS.id];
        system.runAs(oCCS)
        {
        	List<RecordType> lstRecordTypeMemberIds = [SELECT Id  FROM RecordType WHERE Name = 'Member' LIMIT 1 ];
            id idMemberAccountRecordType = lstRecordTypeMemberIds[0].Id;
            
            List<RecordType> lstRecordTypeCaseIds = [SELECT Id  FROM RecordType WHERE Name = 'Member Case' LIMIT 1 ];
            id idMemberCaseRecordType = lstRecordTypeCaseIds[0].Id;            
            
        	Account oAccount = new Account(FirstName='Test' , LastName='Account1', recordtypeid=idMemberAccountRecordType);
            insert oAccount;
            system.assertEquals(oAccount.FirstName,'Test','Value not matched');
            
            List<Classification_Type__c> ctList = new list<Classification_Type__c>();
			Classification_Type__c ctRec = new Classification_Type__c();
			ctRec.name = 'Calls';
			ctRec.Is_Active__c = true;
			ctList.add(ctRec);
			insert ctList;
			system.assertEquals(ctList.size(), 1);
			
			Classification__c classificationRec = new Classification__c();
			classificationRec.Name = 'Enrollment/Effective Date Correction';
			classificationRec.Is_Active__c = true;
			insert classificationRec;
			system.assertEquals(classificationRec.Name, 'Enrollment/Effective Date Correction', 'Value not matched');

			Intent__c intentRec = new Intent__c();
			intentRec.Name = 'Rewards';
			intentRec.Is_Active__c = true;
			insert intentRec;
			system.assertEquals(intentRec.Name, 'Rewards', 'Value not matched');

			List<CTCI_Junction__c> ctciList = new list<CTCI_Junction__c>();
			CTCI_Junction__c ctciRec = new CTCI_Junction__c();
			ctciRec.Classification_Type__c = ctRec.id;
			ctciRec.Classification__c = classificationRec.Id;
			ctciRec.Intent__c = intentRec.id;
			ctciRec.IsActive__c = true;
			ctciList.add(ctciRec);
			insert ctciList;
			system.assertEquals(ctciList.size(), 1);

            List<Case> lstCase = new List<Case>();
            
            Case oCase1 = new Case(ownerid=oGroup.Id , Owner__c = 'Test',AccountId=oAccount.Id, Interacting_With__c=oAccount.Id, Due_Date__c=Date.Today(),Follow_Up_Due_Date__c = Date.Today(),
                                   Case_Owner__c = 'Cincinati_CRU1', Work_Queue_View_Name__c='other', Owner_Queue__c='Cincinati_CRU1', status='In Progress', 
                                   Origin = 'Inbound Call', Type = 'Customer Inquiry', RecordTypeId = idMemberCaseRecordType,Interacting_With_TYpe__c = 'Member',
                                   Interacting_With_Name__c = 'TestAccount1', Interacting_About_Type__c = 'Member', 
                                   Classification_Type__c = 'Calls', Classification_Id__c = classificationRec.Id, CTCI_List__c = ctciRec.Id,
                                   G_A_Rights_Given__c = 'No', Complaint__c = 'Yes', Complaint_Reason__c = 'Customer Service Quality', Behavioral_Health__c = 'None');
            system.assertEquals(oCase1.Origin, 'Inbound Call', 'Value not matched');
            lstCase.add(oCase1);
            
            Case oCase2 = new Case(ownerid=oCCS.Id,AccountId=oAccount.Id, Interacting_With__c=oAccount.Id, Follow_Up_Due_Date__c = Date.Today(), Due_Date__c=Date.Today(), Case_Owner__c= oCCS.firstname + ' ' + oCCS.lastname, owner__C = oCCS.firstname + ' ' + oCCS.lastname ,  Work_Queue_View_Name__c='Other' , Owner_Queue__c='Cincinati_CRU1', status='In Progress', 
                                   Origin = 'Inbound Call', Type = 'Customer Inquiry', RecordTypeId = idMemberCaseRecordType, Interacting_With_Type__c = 'Member', Interacting_With_Name__c = 'TestAccount1', Interacting_About_Type__c = 'Member', 
                                   Classification_Type__c = 'Calls', Classification_Id__c = classificationRec.Id, CTCI_List__c = ctciRec.Id, G_A_Rights_Given__c = 'No', Complaint__c = 'Yes', Complaint_Reason__c = 'Customer Service Quality', Behavioral_Health__c = 'None');
            system.assertEquals(oCase2.Origin, 'Inbound Call', 'Value not matched');
            lstCase.add(oCase2);
            
            if(!lstCase.isEmpty()) insert lstCase;
            
            Work_Queue_Setup__c oWQSelector = new Work_Queue_Setup__c(Name = 'Cincinati_CRU1');
            oWQSelector.Public_Group_Name__c = 'PG Cincinati CRU1';
            oWQSelector.IsActive__c  = true;
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
    * Test method to test when the owner is User
    * <p>
    * @return   :    void
    * @param    :    NA
    */
    static testMethod void testUserAsOwner()
    {
    	User oCCSup = [ SELECT Id, UserName  , Current_queue__c FROM User WHERE LastName = 'test CCSup'];
    	system.runAs(oCCSup)
        {	
        	Test.StartTest();
        		Case ObjCase = [Select Id, Owner_queue__c , ownerid , owner__c from Case where Case_Owner__c != 'Cincinati_CRU1' Limit 1];
        		Attachment__c objAttachment = new Attachment__c(Related_to_case__c = ObjCase.id);
        		insert objAttachment;
        		Attachment__c queryAttachment = [Select Id , Created_By_Queue__c from Attachment__c where Id = :objAttachment.Id];
        		Work_Queue_setup__c wqs = [SELECT id,Name FROM Work_Queue_setup__c Where Name='Cincinati_CRU1'];
        		System.assertEquals(oCCSup.Current_queue__c,queryAttachment.Created_By_Queue__c);
				List<Attachment__c> lstAttachments = new List<Attachment__c>();
                lstAttachments.Add(objAttachment);
                Attachment_H_HUM.updateCreatedByQueue(lstAttachments);
                System.assertEquals(lstAttachments[0].Created_By_Queue__c ,ObjCase.Owner_queue__c);
        	Test.StopTest();
        }
    }
    
    /**
    * Test method to test when the owner is Group
    * <p>
    * @return   :    void
    * @param    :    NA
    */
     static testMethod void testGroupAsOwner()
    {
    	User oCCSup = [ SELECT Id, UserName  , Current_queue__c FROM User WHERE LastName = 'test CCSup'];
    	system.runAs(oCCSup)
        {	
        	Test.StartTest();
        		Case ObjCase = [Select Id, Owner_queue__c , ownerid , owner__c from Case where Case_Owner__c = 'Cincinati_CRU1' Limit 1];
        		Attachment__c objAttachment = new Attachment__c(Related_to_case__c = ObjCase.id);
        		insert objAttachment;
        		Attachment__c queryAttachment = [Select Id , Created_By_Queue__c from Attachment__c where Id = :objAttachment.Id];
        		Work_Queue_setup__c wqs = [SELECT id,Name FROM Work_Queue_setup__c Where Name='Cincinati_CRU1'];
        		System.assertEquals(oCCSup.Current_queue__c,queryAttachment.Created_By_Queue__c);
				List<Attachment__c> lstAttachments = new List<Attachment__c>();
                lstAttachments.Add(objAttachment);
                Attachment_H_HUM.updateCreatedByQueue(lstAttachments);
                System.assertEquals(lstAttachments[0].Created_By_Queue__c ,oCCSup.Current_queue__c);
        	Test.StopTest();
        }
    }
    
}