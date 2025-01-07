/*********************************************************************************************************
Apex Class Name : METEnrollment_T_HUM 
Version         : 1.0
Created On      : 07/10/2016
Function        : Test class for METEnrollment_H_HUM 

* Modification Log:
* Developer Name            Code Review                 Date                       Description
*-----------------------------------------------------------------------------------------------------------
* Prasad Jandhyala             114623                 07/10/2017                  Original version
* Manoj Chaurasia              202594                 03/29/2018                  REQ - 354470 Ability to display TAS combination in CRM
* Prajakta Deshpande                                  08/08/2018                REQ 353087 - Task Action Source Inactive entry (CA - 7619235)
* Samantha Tennyson            		              08/26/2020                  User Story 1318943: PR00091574 - MF 1 - AHT - Ability to create "Quick MET Entries" from MET Enrollment section on Case Edit page (RF)
* Mohammed Noor            		              	  09/01/2021                  Changes to increase code coverage
************************************************************************************************************/

@isTest(seeAllData = false)
private class METEnrollment_T_HUM {
    /**
    *  This method creates test data for test class
    *  @name setUpTestData
    *  @param NA
    *  @return NA
    *  @throws NA
    *  @see 
    */
    @testSetup static void setUpTestData()
    {
        User oUser = HUMTestDataHelperTest.createTestUser('METEnrolUser1','Customer Care Specialist');
        insert oUser;
        system.assertEquals(oUser.lastname,'test METEnrolUser1');
        
        system.runAs(oUser)
        {        
            Account acc1 = HUMTestDataHelperTest.getAccount();
            acc1.Name = 'Test Account Cases1';
            acc1.Birthdate__c='01/15/1951';
            acc1.Enterprise_ID__c = '1005447107';
            insert acc1;        
            system.assertEquals(acc1.Name == 'Test Account Cases1',true);
        
            Policy__c pol = HUMTestDataHelperTest.getPolicy();
            pol.Group_Name__c = acc1.Id;
            pol.Name = 'Test Policy';
            insert pol;
            system.assertEquals(pol.Name,'Test Policy');
            system.assertEquals(pol.Group_Name__c == acc1.Id,true);
        
            Policy_Member__c polMember = HUMTestDataHelperTest.getPolicyMember();
            polMember.Name='Policy Member PM1';
            polMember.Policy__c=pol.Id;
            polMember.Member__c=acc1.Id;
            insert polMember;
            system.assertEquals(polMember.Id != Null,true);
            system.assertEquals(polMember.Name,'Policy Member PM1');
            system.assertEquals(polMember.Policy__c,pol.Id);  
            
            List<Classification_Type__c> ctList= new list<Classification_Type__c>();
            Classification_Type__c ctRec = new Classification_Type__c();
            ctRec.name = 'Claims (RSO)';
            ctRec.Is_Active__c=true;
            ctList.add(ctRec);
            
            insert ctList;
            system.assertEquals(ctList.size(),1);
            
            Work_Queue_Setup__c wqsRecord = new Work_Queue_Setup__c();
            wqsRecord.Name = 'Cincinnati CRU';
            wqsRecord.Classification_Type__c = ctRec.Id;
            insert wqsRecord;
            system.assertEquals(wqsRecord.Name, 'Cincinnati CRU');
            
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
            system.assertEquals(lstIntent[0].Name=='Filling',true);
            
            List<CTCI_Junction__c> ctciList = new list<CTCI_Junction__c>();
            CTCI_Junction__c ctciRec = new CTCI_Junction__c();
            ctciRec.Classification_Type__c = ctRec.id;
            ctciRec.Classification__c = classificationRec.Id;
            ctciRec.Intent__c = intentRec.id;
            ctciRec.IsActive__c = true;
            ctciList.add(ctciRec);
            insert ctciList;
            system.assertEquals(ctciList.size(),1);
            String metTaskType = 'Medicare Case';
            Id metRecordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get(metTaskType).getRecordTypeId();
            
            Case oCase = new Case(  AccountId=acc1.Id,
                                    recordTypeId = metRecordTypeId,
                                    CTCI_List__c = ctciRec.Id,
                                    General_Case__c = true,
                                    Interacting_With_Type__c = 'Group',
                                    Interacting_With__c=acc1.Id, 
                                    Due_Date__c=Date.Today(), 
                                    Case_Owner__c=UserInfo.getUserName(), 
                                    Owner_Queue__c='Cincinnati CRU1', 
                                    Work_Queue_View_Name__c='Cincinati CRU1',
                                    Service_Center__c ='Cincinnati',
                                    Department__c='CRU1', 
                                    status='In Progress',
				                    Type ='TRR Inquiry',
                                    Subtype__c='TRR'
                                 	);
            insert oCase;
            system.assertNotEquals(oCase.Id,null,'Value not matched');
            
            MET_Task__c oTask = new MET_Task__c();
            oTask.Name = 'Test Task';
            oTask.Is_Active__c = true;
            oTask.Case_Subtype__c = 'Routine';
            insert oTask;
            System.assertEquals(oTask.Name,'Test Task');
            
            MET_Action__c oAction = new MET_Action__c();
            oAction.Name = 'Test Action';
            oAction.Is_Active__c = true;
            insert oAction;
            System.assertEquals(oAction.Name,'Test Action');

            MET_Source__c oSource = new MET_Source__c();
            oSource.Name = 'Test Source';
            oSource.Is_Active__c = true; 
            insert oSource; 
            System.assertEquals(oSource.Name,'Test Source');

            MET_Milestone__c oMetMilestone = new MET_Milestone__c();           
            oMetMilestone .Case_Type__c = 'TRR Inquiry';
            oMetMilestone .Case_Subtype__c =  'TRR';
            oMetMilestone .Action__c = oAction.id;
            oMetMilestone .Source_Name__c =  oSource.id;
            oMetMilestone .Task__c =  oTask.id;
            oMetMilestone .Is_Active__c = true;
            oMetMilestone .Is_Milestone__c = true; 
            oMetMilestone.met_entries_assoc__c = 'Resubmission Changes Letter or PCRP; Retransmission no letter';
            oMetMilestone.Met_Entries_Assoc_Order__c = 'Retransmission no letter1;Resubmission Changes Letter or PCRP1';
            insert oMetMilestone ;
            
            METEnrollment_H_HUM.getMultipleMETEntries(oCase.Type , oCase.Subtype__c);
            METEnrollment_H_HUM.getMultipleMETTasks(oCase.Type , oCase.Subtype__c,'Retransmission no letter');
            ApexPages.StandardController scon = new ApexPages.StandardController(oCase);
			CaseEdit_CE_HUM caseEdit = new CaseEdit_CE_HUM(scon);
			caseEdit.sMETEntriesCreationValue = 'Retransmission no letter';
			caseEdit.loadMultipleMETTask();
			caseEdit.getMETEntriesCreation(); 
            METEnrollment_H_HUM.getMultipleMETTasks(oCase.Type , oCase.Subtype__c,'');
            System.assertEquals(oMetMilestone .Action__c,oAction.id);
            
        }
    }

    /*
    * Method Name : testIsMedicareUser
    * Description : This method is used to test if the User is Medicare User or not
    * Return type : None
    * Parameter   : None
    */
    static testMethod void testIsMedicareUser()
    {
        User testUser = [Select Id,Name from User where LastName='test METEnrolUser1'];
        PermissionSet ps = [SELECT Id FROM PermissionSet WHERE Name = 'CRMS_630_MedicareElectionTracking_EnrollmentEdit'];
        PermissionSetAssignment psa = new PermissionSetAssignment();
        psa.AssigneeId = testUser.Id;
        psa.PermissionSetId = ps.Id;
        insert psa;
        System.runAs(testUser)
        {
            Test.startTest();
            Boolean bIsMedicareUser = METEnrollment_H_HUM.isMedicareUser();
            System.assertEquals(bIsMedicareUser,true);
            Test.stopTest();
        }
    }
    
    /*
    * Method Name : testIsMedicareUser
    * Description : This method is used to test if the User is Medicare User or not
    * Return type : None
    * Parameter   : None
    */
    static testMethod void testIsMedicareUserNegative()
    {
        User testUser = [Select Id,Name from User where LastName='test METEnrolUser1'];
        System.runAs(testUser)
        {
            Test.startTest();
            Boolean bIsMedicareUser = METEnrollment_H_HUM.isMedicareUser();
            System.assertEquals(bIsMedicareUser,false);
            Test.stopTest();
        }
    }
    
    /*
    * Method Name : testGetMETTasks
    * Description : This method is used to test if the getMETTasks method returns Tasks related to the Case subtype
    * Return type : None
    * Parameter   : None
    */
    static testMethod void testGetMETTasks()
    {
        User testUser = [Select Id,Name from User where LastName='test METEnrolUser1'];
        System.runAs(testUser)
        {
            Test.startTest();
            List<MET_Task__c> metTasks = METEnrollment_H_HUM.getMETTasks('TRR Inquiry', 'TRR');
            System.assertEquals(metTasks.size(),1);
        }
    }
    
    /*
    * Method Name : testGetMETTasks
    * Description : This method is used to test if the getMETTasks method returns Tasks related to the Case subtype
    * Return type : None
    * Parameter   : None
    */
    static testMethod void testGetMETTasksNegative()
    {
        User testUser = [Select Id,Name from User where LastName='test METEnrolUser1'];
        System.runAs(testUser)
        {
            Test.startTest();
            List<MET_Task__c> metTasks = METEnrollment_H_HUM.getMETTasks('','');
            System.assertEquals(metTasks.size(),0);
            Test.stopTest();
        }
    }
    
    /*
    * Method Name : testGetMETTasks
    * Description : This method is used to test if the getMETTasks method returns Tasks related to the Case subtype
    * Return type : None
    * Parameter   : None
    */
    static testMethod void testGetMETActions()
    {
        User testUser = [Select Id,Name from User where LastName='test METEnrolUser1'];
        System.runAs(testUser)
        {
            Test.startTest();
            MET_Task__c metTask = [Select id, Name from MET_Task__c where Name = 'Test Task' limit 1];
            List<MET_Action__c> metActions = METEnrollment_H_HUM.getMETActions('TRR Inquiry', 'TRR', metTask.Id);
            System.assertEquals(metActions.size(),1);
            Test.stopTest();
        }
    }
    
    /*
    * Method Name : testGetMETTasks
    * Description : This method is used to test if the getMETTasks method returns Tasks related to the Case subtype
    * Return type : None
    * Parameter   : None
    */
    static testMethod void testGetMETActionsNegative()
    {
        User testUser = [Select Id,Name from User where LastName='test METEnrolUser1'];
        System.runAs(testUser)
        {
            Test.startTest();
            List<MET_Action__c> metActions = METEnrollment_H_HUM.getMETActions('','','');
            System.assertEquals(metActions.size(),0);
            Test.stopTest();
        }
    }
    
     /*
    * Method Name : testGetMETTasks
    * Description : This method is used to test if the getMETTasks method returns Tasks related to the Case subtype
    * Return type : None
    * Parameter   : None
    */
    static testMethod void testGetMETSources()
    {
        User testUser = [Select Id,Name from User where LastName='test METEnrolUser1'];
        System.runAs(testUser)
        {
            Test.startTest();
            MET_Action__c metAction = [Select Id, Name from MET_Action__c where Name = 'Test Action' limit 1];
            MET_Task__c metTask = [Select Id, Name from MET_Task__c where Name = 'Test Task' limit 1];
            List<MET_Source__c> metSources = METEnrollment_H_HUM.getMETSources('TRR Inquiry', 'TRR', metTask .Id,metAction.Id);
            System.assertEquals(metSources.size(),1);
            Test.stopTest();
        }
    }
    
    /*
    * Method Name : testGetMETTasks
    * Description : This method is used to test if the getMETTasks method returns Tasks related to the Case subtype
    * Return type : None
    * Parameter   : None
    */
    static testMethod void testGetMETSourcesNegative()
    {
        User testUser = [Select Id,Name from User where LastName='test METEnrolUser1'];
        System.runAs(testUser)
        {
            Test.startTest();
            List<MET_Source__c> metSources = METEnrollment_H_HUM.getMETSources('','','','');
            System.assertEquals(metSources.size(),0);
            Test.stopTest();
        }
    }
    
    /*
    * Method Name : testCreateTasks
    * Description : This method is used to test if the CreateTasks method create Tasks
    * Return type : None
    * Parameter   : None
    */
    static testMethod void testCreateTasks()
    {
        User testUser = [Select Id,Name from User where LastName='test METEnrolUser1'];
        System.runAs(testUser)
        {
            METTaskList_DTO_HUM objTaskListDTO = new METTaskList_DTO_HUM();
            METTaskList_DTO_HUM.METTask_DTO_HUM objTaskDTO = new METTaskList_DTO_HUM.METTask_DTO_HUM();
            objTaskDTO.sTaskId = null;
            objTaskDTO.sTask = [Select id from MET_Task__c where Name = 'Test Task' limit 1].id;
            objTaskDTO.sAction = [Select Id, Name from MET_Action__c where Name = 'Test Action' limit 1].id;
            objTaskDTO.sSource = [Select id from MET_Source__c where Name = 'Test Source' limit 1].id;
            objTaskDTO.sStatus = 'Completed';
			objTaskDTO.sCreatedDate_Sort = '2021-01-01 01:11:10:764';
            objTaskListDTO.listMETTaskDTO = new List<METTaskList_DTO_HUM.METTask_DTO_HUM>();
            objTaskListDTO.listMETTaskDTO.add(objTaskDTO);
			
			METTaskList_DTO_HUM.METTask_DTO_HUM objTaskDTO1 = new METTaskList_DTO_HUM.METTask_DTO_HUM();
            objTaskDTO1.sTaskId = null;
            objTaskDTO1.sTask = [Select id from MET_Task__c where Name = 'Test Task' limit 1].id;
            objTaskDTO1.sAction = [Select Id, Name from MET_Action__c where Name = 'Test Action' limit 1].id;
            objTaskDTO1.sSource = [Select id from MET_Source__c where Name = 'Test Source' limit 1].id;
            objTaskDTO1.sStatus = 'Cancelled';
            objTaskDTO1.sCreatedDate_Sort = '2021-01-01 01:11:10:786';
            objTaskListDTO.listMETTaskDTO.add(objTaskDTO1);
            
			METTaskList_DTO_HUM.METTask_DTO_HUM objTaskDTO2 = new METTaskList_DTO_HUM.METTask_DTO_HUM();
            objTaskDTO2.sTaskId = null;
            objTaskDTO2.sTask = null;
            objTaskDTO2.sAction = null;
            objTaskDTO2.sSource = null;
            objTaskDTO2.sStatus = 'Cancelled';            
            objTaskListDTO.listMETTaskDTO.add(objTaskDTO2);        			
            
			Case caseRecord = [Select id, Complaint__c, RecordType.Name from case where Subtype__c = 'TRR' LIMIT 1];
            System.assert(caseRecord.id!=null);
			METEnrollment_H_HUM.getTasksOfCase(caseRecord.Id);
            Test.setCurrentPage(page.CaseEdit_VF_HUM);
            ApexPages.StandardController scon = new ApexPages.StandardController(caseRecord);
            CaseEdit_CE_HUM ceHum = new CaseEdit_CE_HUM(scon);
            Test.startTest();
            List<Task> metTasks = METEnrollment_H_HUM.createTasks(objTaskListDTO,caseRecord);
            System.assertEquals(metTasks.size(),2);
            upsert metTasks;
            METEnrollment_H_HUM.getTaskListDTO(caseRecord.Id);
            METEnrollment_H_HUM.getTasksOfCase(caseRecord.Id);
            Test.stopTest();
        }
    }
    
    /*
    * Method Name : testGetMETTasks
    * Description : This method is used to test if the getMETTasks method returns Tasks related to the Case subtype
    * Return type : None
    * Parameter   : None
    */
    static testMethod void testCreateTasksNegative()
    {
        User testUser = [Select Id,Name from User where LastName='test METEnrolUser1'];
        System.runAs(testUser)
        {
            METTaskList_DTO_HUM objTaskListDTO = new METTaskList_DTO_HUM();
            METTaskList_DTO_HUM.METTask_DTO_HUM objTaskDTO = new METTaskList_DTO_HUM.METTask_DTO_HUM();
            objTaskDTO.sTaskId = null;
            objTaskDTO.sTask = [Select id from MET_Task__c where Name = 'Test Task' limit 1].id;
            objTaskDTO.sAction = [Select Id, Name from MET_Action__c where Name = 'Test Action' limit 1].id;
            objTaskDTO.sSource = [Select id from MET_Source__c where Name = 'Test Source' limit 1].id;
            objTaskDTO.sStatus = null;
            objTaskListDTO.listMETTaskDTO = new List<METTaskList_DTO_HUM.METTask_DTO_HUM>();
            objTaskListDTO.listMETTaskDTO.add(objTaskDTO);
            Case caseRecord = [Select id from case where Subtype__c = 'TRR' LIMIT 1];
            System.assert(caseRecord.id!=null);
            Test.startTest();
            List<Task> metTasks = METEnrollment_H_HUM.createTasks(objTaskListDTO,caseRecord);
            System.assertEquals(metTasks.size(),1);
            Test.stopTest();
        }
    }
    
    /*
    * Method Name : testGetMETTasks
    * Description : This method is used to test if the getMETTasks method returns Tasks related to the Case subtype
    * Return type : None
    * Parameter   : None
    */
    static testMethod void testCreateTasksWithId()
    {
        User testUser = [Select Id,Name from User where LastName='test METEnrolUser1'];
        System.runAs(testUser)
        {
            Case caseRecord = [Select id from case where Subtype__c = 'TRR' LIMIT 1];
            System.assert(caseRecord.id!=null);
            Task objTask = new Task(WhatID = caseRecord.Id);
            insert objTask;
            METTaskList_DTO_HUM objTaskListDTO = new METTaskList_DTO_HUM(new List<Task>{objTask});
            Test.startTest();
            List<Task> metTasks = METEnrollment_H_HUM.createTasks(objTaskListDTO,caseRecord);
            System.assertEquals(metTasks.size(),1);
            Test.stopTest();
        }
    }
}