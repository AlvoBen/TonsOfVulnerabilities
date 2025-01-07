/**********************************************************************************************************************************************************
Apex Class Name  : OmniCaseAssignmentRuleEvaluation_T_HUM
Version          : 1.0
Created Date     : 12/15/2017
Function         : Test class for OmniCaseAssignmentRuleEvaluation_H_HUM
***********************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------------
* Manoj Chaurasia           167654                   03/08/2016                  Original version
* Mohammed Noor             179224                   02/05/2016                  Add Policy Member Field in the Test Data creation.
* Mohammed Noor             213637                   04/10/2018                  REQ - 359240 - Date fields available as Age criteria in Omni Channel
* Syed Mubarak T                                    04/01/2019                  REQ - 383800 - Add TRR Template Fields to Omni Evaluation
* Mohammed Noor                                      08/14/2019                  Changes for additional code coverage.   
* Pooja Kumbhar                                      04/29/2020                  Checkmarx Test Method without assert error fix.
* Vamshi Krishna Pemberthi                           07/08/2021                  Improved Code Coverage
* Jasmeen Shangari									 07/31/2023					 Fixed Test Class Failure
************************************************************************************************************************************************************/

@isTest
public class OmniCaseAssignmentRuleEvaluation_T_HUM {
    
    /*
    * This method creates test data for test class
    * @param None
    * @return None
    */
    @testSetup static void setUpTestData()
    {
        User usr = HUMTestDataHelperTest.createTestUser('humcrmsOmniUser','Customer Care Specialist');
        insert usr;
        system.assertEquals(usr.lastname=='test humcrmsOmniUser',true);
        
        PermissionSet pCheck =[SELECT ID From PermissionSet WHERE Name =:GLOBAL_CONSTANT_HUM.MEDICARECALLS_PERMISSIONSET_HUM LIMIT 1];
        insert new PermissionSetAssignment(AssigneeId = usr.id, PermissionSetId = pCheck.id);
        
        //create OmniQueue
        Group oQueue = new Group();
        oQueue.name = 'Test Omni Queue';
        oQueue.type = 'Queue';
        insert oQueue;
        
        //create OmniQueue
        Group oQueue1 = new Group();
        oQueue1.name = 'Test Omni Queue1';
        oQueue1.type = 'Queue';
        insert oQueue1;
        
        QueuesObject oQueueObject = new QueuesObject (QueueID = oQueue.id, SobjectType ='Work_Queue_Setup__c');
        insert oQueueObject ;   
        system.assertNotEquals(oQueueObject,null,'Insert Failed');
        System.assertEquals(oQueueObject.SobjectType, 'Work_Queue_Setup__c');
        
        QueuesObject oQueueObject1 = new QueuesObject (QueueID = oQueue1.id, SobjectType ='Work_Queue_Setup__c');
        insert oQueueObject1 ;   
        system.assertNotEquals(oQueueObject1,null,'Insert Failed');
        System.assertEquals(oQueueObject1.SobjectType, 'Work_Queue_Setup__c');
        
        //Create StaginQueue
        Group sQueue = new Group();
        sQueue.name = 'Test Omni Queue_Stg1';
        sQueue.type = 'Queue';
        insert sQueue;
        
        //Create StaginQueue - 2
        Group sQueue2 = new Group();
        sQueue2.name = 'Test Omni Queue_Stg2';
        sQueue2.type = 'Queue';
        insert sQueue2;
        
        //Create StaginQueue - 3
        Group sQueue3 = new Group();
        sQueue3.name = 'Test Omni Queue_Stg2';
        sQueue3.type = 'Queue';
        insert sQueue3;
        
        //Assign Queue to user
        GroupMember userData = new GroupMember();
        userData.GroupId = oQueue.Id;
        userData.UserOrGroupId = usr.Id;
        insert userData;
        
        List<OmniRuleCriteria_Fields__mdt> oFields = [Select Label, Field_APIName__c, Field_Type__c From OmniRuleCriteria_Fields__mdt];
        
        system.runAs(usr)
        { 
            //Create Omni Routing Configuration
            Omni_Routing_Configuration__c routingConfigData =  new Omni_Routing_Configuration__c();
            routingConfigData.Name = 'RoutingConfigTest';
            routingConfigData.Is_Active__c = true;
            routingConfigData.Push_Time_Out__c = 100;
            routingConfigData.QueueRoutingConfig_Id__c = null;
            routingConfigData.Routing_Priority__c = 1;
            insert routingConfigData;
            
            //Create Omni Queue
            OmniQueue__c omniData = new OmniQueue__c();
            omniData.Name = 'Test Omni Queue1';
            omniData.Maximum_Cases__c = 50;
            omniData.Omni_Queue_ID__c = oQueue.Id;
            omniData.Staging_Queue_ID__c = sQueue.Id;
            omniData.Staging_Queue_Name__c = sQueue.Name;
            omniData.Routing_Configuration_Name__c = routingConfigData.Id;
            insert omniData;
            
            //Create Omni Queue - 2
            OmniQueue__c omniData2 = new OmniQueue__c();
            omniData2.Name = 'Test Omni Queue2';
            omniData2.Maximum_Cases__c = 50;
            omniData2.Omni_Queue_ID__c = oQueue.Id;
            omniData2.Staging_Queue_ID__c = sQueue2.Id;
            omniData2.Staging_Queue_Name__c = sQueue2.Name;
            omniData2.Routing_Configuration_Name__c = routingConfigData.Id;
            insert omniData2;
            
            //Create Omni Queue - 3 
            OmniQueue__c omniData3 = new OmniQueue__c();
            omniData3.Name = 'Test Omni Queue3';
            omniData3.Maximum_Cases__c = 50;
            omniData3.Omni_Queue_ID__c = oQueue.Id;
            omniData3.Staging_Queue_ID__c = sQueue3.Id;
            omniData3.Staging_Queue_Name__c = sQueue3.Name;
            omniData3.Routing_Configuration_Name__c = routingConfigData.Id;
            insert omniData3;

            //Create omni queue - 4 
            OmniQueue__c omniData4 = new OmniQueue__c();
            omniData4.Name = 'Test Omni Queues1';
            omniData4.Maximum_Cases__c = 50;
            omniData4.Omni_Queue_ID__c = oQueue.Id;
            omniData4.Staging_Queue_ID__c = sQueue.Id;
            omniData4.Staging_Queue_Name__c = sQueue.Name;
            omniData4.Routing_Configuration_Name__c = routingConfigData.Id;
            insert omniData4;
            
            //Create Work Queue
            Work_Queue_Setup__c oWorkQS = new Work_Queue_Setup__c(Public_Group_Name__c = 'Group One', 
                                                                  Service_Center__c = 'Cincinnati', Department__c = 'Calls', Name = 'Test Omni Queue');
            oWorkQS.OwnerId = oQueue.Id;
            insert oWorkQS;
            System.assert(oWorkQS != null);
            System.assertEquals(oWorkQS.Public_Group_Name__c, 'Group One');
            
            //Create Work Queue View
            Queue_View__c oQView = new Queue_View__c();
            oQView.Name = 'Test Queue View';
            oQView.Work_Queue_Name__c = oWorkQS.Id;
            Insert oQView; 
            
            //Create Omni Case Assignment Rule
            List<Omni_Case_Assignment_Rule__c> lstCARule = new List<Omni_Case_Assignment_Rule__c>();
            Omni_Case_Assignment_Rule__c oCARule;
            
            oCARule = new Omni_Case_Assignment_Rule__c();
            oCARule.Name = 'Test Omni Case Assignment Rule1';
            oCARule.Evaluation_Order__c = 1;
            oCARule.Omni_Queue__c = omniData.Id;
            oCARule.WorkQueue__c = oWorkQS.Id;
            oCARule.Is_Active__c = true;
            oCARule.WorkQueueView__c = oQView.Id;
            oCARule.Case_Assignment_Rule_Map__c = '__r.';
            lstCARule.add(oCARule);
            
            oCARule = new Omni_Case_Assignment_Rule__c();
            oCARule.Name = 'Test Omni Case Assignment Rule2';
            oCARule.Evaluation_Order__c = 2;
            oCARule.Omni_Queue__c = omniData2.Id;
            oCARule.WorkQueue__c = oWorkQS.Id;
            oCARule.Is_Active__c = true;
            // oCARule.WorkQueueView__c = oQView.Id;
            lstCARule.add(oCARule);
            
            oCARule = new Omni_Case_Assignment_Rule__c();
            oCARule.Name = 'Test Omni Case Assignment Rule3';
            oCARule.Evaluation_Order__c = 3;
            oCARule.Omni_Queue__c = omniData3.Id;
            //oCARule.WorkQueue__c = oWorkQS.Id;
            oCARule.Is_Active__c = true;
            // oCARule.WorkQueueView__c = oQView.Id;
            lstCARule.add(oCARule);
            
            //-------------------------
            //Create Work Queue
            Work_Queue_Setup__c oWorkQS1 = new Work_Queue_Setup__c(Public_Group_Name__c = 'Group One1', 
                                                                   Service_Center__c = 'Cincinnati', Department__c = 'Calls1', Name = 'Test Omni Queue1');
            oWorkQS1.OwnerId = oQueue1.Id;
            insert oWorkQS1;
            System.assert(oWorkQS1 != null);
            System.assertEquals(oWorkQS1.Public_Group_Name__c, 'Group One1');
            
            oCARule = new Omni_Case_Assignment_Rule__c();
            oCARule.Name = 'Test Omni Case Assignment Rule4';
            oCARule.Evaluation_Order__c = 4;
            oCARule.Omni_Queue__c = omniData.Id;
            oCARule.WorkQueue__c = oWorkQS1.Id;
            oCARule.Is_Active__c = true;
            //oCARule.WorkQueueView__c = oQView.Id;
            lstCARule.add(oCARule);
            
            oCARule = new Omni_Case_Assignment_Rule__c();
            oCARule.Name = 'Test Omni Case Assignment Rule5';
            oCARule.Evaluation_Order__c = 5;
            oCARule.Omni_Queue__c = omniData2.Id;
            oCARule.WorkQueue__c = oWorkQS1.Id;
            oCARule.Is_Active__c = true;
            // oCARule.WorkQueueView__c = oQView.Id;
            lstCARule.add(oCARule);
            
            oCARule = new Omni_Case_Assignment_Rule__c();
            oCARule.Name = 'Test Omni Case Assignment Rule6';
            oCARule.Evaluation_Order__c = 6;
            oCARule.Omni_Queue__c = omniData3.Id;
            //oCARule.WorkQueue__c = oWorkQS.Id;
            oCARule.Is_Active__c = true;
            // oCARule.WorkQueueView__c = oQView.Id;
            lstCARule.add(oCARule);
            //------------------------
            
            Insert lstCARule;
            
            //Create Omni Rule Criteria
            List<Omni_Rule_Criteria__c> lstRuleCriteria = new List<Omni_Rule_Criteria__c>();
            Omni_Rule_Criteria__c oRuleCriteria = new Omni_Rule_Criteria__c();
            oRuleCriteria.Criteria_Field__c = 'Case Age';
            oRuleCriteria.Criteria_Field_Value__c = '20';
            oRuleCriteria.Criteria_Operator__c = 'not equal to';
            oRuleCriteria.Omni_Case_Assignment_Rule__c = lstCARule[0].Id;
            oRuleCriteria.Criteria_Set__c = '1';
            lstRuleCriteria.add(oRuleCriteria);
            
            oRuleCriteria = new Omni_Rule_Criteria__c();
            oRuleCriteria.Criteria_Field__c = 'Status';
            oRuleCriteria.Criteria_Field_Value__c = 'Test';
            oRuleCriteria.Criteria_Operator__c = 'not equal to';
            oRuleCriteria.Omni_Case_Assignment_Rule__c = lstCARule[0].Id;
            oRuleCriteria.Criteria_Set__c = '1';
            lstRuleCriteria.add(oRuleCriteria);
            
            oRuleCriteria = new Omni_Rule_Criteria__c();
            oRuleCriteria.Criteria_Field__c = 'Escalated';
            oRuleCriteria.Criteria_Field_Value__c = 'false';
            oRuleCriteria.Criteria_Operator__c = 'equals';
            oRuleCriteria.Omni_Case_Assignment_Rule__c = lstCARule[0].Id;
            oRuleCriteria.Criteria_Set__c = '1';
            lstRuleCriteria.add(oRuleCriteria);
            
            oRuleCriteria = new Omni_Rule_Criteria__c();
            oRuleCriteria.Criteria_Field__c = 'Trans Code';
            oRuleCriteria.Criteria_Field_Value__c = 'ABC';
            oRuleCriteria.Criteria_Operator__c = 'equals';
            oRuleCriteria.Omni_Case_Assignment_Rule__c = lstCARule[0].Id;
            oRuleCriteria.Criteria_Set__c = '1';
            lstRuleCriteria.add(oRuleCriteria);
            
            oRuleCriteria = new Omni_Rule_Criteria__c();
            oRuleCriteria.Criteria_Field__c = 'Processing Time Stamp';
            oRuleCriteria.Criteria_Field_Value__c = 'today';
            oRuleCriteria.Criteria_Operator__c = 'not equal to';
            oRuleCriteria.Omni_Case_Assignment_Rule__c = lstCARule[0].Id;
            oRuleCriteria.Criteria_Set__c = '1';
            lstRuleCriteria.add(oRuleCriteria);
            
            
            oRuleCriteria = new Omni_Rule_Criteria__c();
            oRuleCriteria.Criteria_Field__c = 'Due Date';
            oRuleCriteria.Criteria_Field_Value__c = '2016-12-31';
            oRuleCriteria.Criteria_Operator__c = 'less than';
            oRuleCriteria.Omni_Case_Assignment_Rule__c = lstCARule[0].Id;
            oRuleCriteria.Criteria_Set__c = '1';
            lstRuleCriteria.add(oRuleCriteria);
            
            oRuleCriteria = new Omni_Rule_Criteria__c();
            oRuleCriteria.Criteria_Field__c = 'Intent Name';
            oRuleCriteria.Criteria_Field_Value__c = 'XYZ';
            oRuleCriteria.Criteria_Operator__c = 'not equal to';
            oRuleCriteria.Omni_Case_Assignment_Rule__c = lstCARule[0].Id;
            oRuleCriteria.Criteria_Set__c = '1';
            lstRuleCriteria.add(oRuleCriteria);
            
            oRuleCriteria = new Omni_Rule_Criteria__c();
            oRuleCriteria.Criteria_Field__c = 'Follow Up Due Date';
            oRuleCriteria.Criteria_Field_Value__c = 'today';
            oRuleCriteria.Criteria_Operator__c = 'not equal to';
            oRuleCriteria.Omni_Case_Assignment_Rule__c = lstCARule[0].Id;
            oRuleCriteria.Criteria_Set__c = '1';
            lstRuleCriteria.add(oRuleCriteria);
            
            //2
            oRuleCriteria = new Omni_Rule_Criteria__c();
            oRuleCriteria.Criteria_Field__c = 'Escalated';
            oRuleCriteria.Criteria_Field_Value__c = 'true';
            oRuleCriteria.Criteria_Operator__c = 'equals';
            oRuleCriteria.Omni_Case_Assignment_Rule__c = lstCARule[1].Id;
            oRuleCriteria.Criteria_Set__c = '2';
            lstRuleCriteria.add(oRuleCriteria);
            
            oRuleCriteria = new Omni_Rule_Criteria__c();
            oRuleCriteria.Criteria_Field__c = 'Intent Name';
            oRuleCriteria.Criteria_Field_Value__c = 'Rewards';
            oRuleCriteria.Criteria_Operator__c = 'not equal to';
            oRuleCriteria.Omni_Case_Assignment_Rule__c = lstCARule[1].Id;
            oRuleCriteria.Criteria_Set__c = '2';
            lstRuleCriteria.add(oRuleCriteria);
            
            
            Insert lstRuleCriteria;
            
            
            String criteriaMap = '';
            for (Omni_Rule_Criteria__c  rc:[select Id,Criteria_Set__c from Omni_Rule_Criteria__c where Omni_Case_Assignment_Rule__c =:lstCARule[0].Id])
            {
                if(rc.Criteria_Set__c == '1')
                {
                    criteriaMap += rc.Id+',';
                }
            }
            lstCARule[0].Case_Assignment_Rule_Map__c = criteriaMap;
            update lstCARule[0];
            
            criteriaMap = '';
            for (Omni_Rule_Criteria__c  rc:[select Id,Criteria_Set__c from Omni_Rule_Criteria__c where Omni_Case_Assignment_Rule__c =:lstCARule[1].Id])
            {
                if(rc.Criteria_Set__c == '2')
                {
                    criteriaMap += rc.Id+',';
                }
            }
            lstCARule[1].Case_Assignment_Rule_Map__c = criteriaMap;
            update lstCARule[1];
            
            
            RecordType AccountRecordType = [Select Id from RecordType where Name='Member' and sObjectType='Account'];
            
            //Creating Account Record
            Account accountRec = HUMTestDataHelperTest.getAccount();
            accountRec.OwnerId = usr.id;
            accountRec.FirstName = 'Test';
            accountRec.LastName = 'Account';
            accountRec.Enterprise_Id__c = '123456';
            accountRec.recordtypeId = AccountRecordType.id;
            insert accountRec;
            system.assertEquals(accountRec.recordtypeId==AccountRecordType.id,true);
            
            List<Classification_Type__c> ctList= new list<Classification_Type__c>();
            Classification_Type__c ctRec = new Classification_Type__c();
            ctRec.name = 'calls';
            ctRec.Is_Active__c=true;
            ctList.add(ctRec);
            insert ctList;
            system.assertEquals(ctList.size(),1);
            
            List<Classification__c> lstClassification = new List<Classification__c>();
            Classification__c classificationRec = new Classification__c();
            classificationRec.Name = 'Benefits';
            classificationRec.Is_Active__c = true;
            lstClassification.add(classificationRec);
            
            Classification__c classificationRecDec = new Classification__c();
            classificationRecDec.Name = 'Humana Pharmacy';
            classificationRecDec.Is_Active__c = true;
            lstClassification.add(classificationRecDec);
            insert lstClassification;
            system.assertEquals(lstClassification.size(),2);
            
            List<Intent__c> lstIntent = new List<Intent__c>();
            Intent__c intentRec = new Intent__c();
            intentRec.Name = 'Rewards';
            intentRec.Is_Active__c = true;
            lstIntent.add(intentRec);
            
            Intent__c intentRecDec = new Intent__c();
            intentRecDec.Name = 'Estimate';
            intentRecDec.Is_Active__c = true;
            lstIntent.add(intentRecDec);
            insert lstIntent;
            system.assertEquals(lstIntent.size(),2);
            
            List<CTCI_Junction__c> ctciList = new list<CTCI_Junction__c>();
            CTCI_Junction__c ctciRec = new CTCI_Junction__c();
            ctciRec.Classification_Type__c = ctRec.id;
            ctciRec.Classification__c = classificationRec.Id;
            ctciRec.Intent__c = intentRec.id;
            ctciRec.IsActive__c = true;
            ctciList.add(ctciRec);
            insert ctciList;
            system.assertEquals(ctciList.size(),1);
            
            List<Case> lstCase = new List<Case>();
            
            
           for(Integer index=0; index<60; index++){
                Case caseRecord;
                caseRecord = new Case();
                caseRecord.Owner_Queue__c = 'Test Omni Queue';
                caseRecord.Status ='Open';// 'In Progress';
                caseRecord.AccountId = accountRec.id;
                caseRecord.Interacting_About_Type__c = 'Member';
                caseRecord.ownerId = usr.Id;
                caseRecord.Classification_Type__c = 'calls';
                caseRecord.Classification_id__c=lstClassification[0].id;
                caseRecord.Intent_id__c = lstIntent[0].id;
                caseRecord.Interacting_With__c = accountRec.id;
                caseRecord.Interacting_With_Type__c = 'Member';
                caseRecord.Policy_Member__c = null;
                caseRecord.Call_Benefit_Category__c = 'Medicare Part C';
                caseRecord.Autoroute_Status__c = 'Evaluate';
                caseRecord.Origin = 'Inbound Call';
                caseRecord.Omni_Eligible__c = true;
                caseRecord.IsEscalated = true;//false;
                caseRecord.Due_Date__c = Date.today();
                caseRecord.Follow_up_Due_Date__c = Date.today() + 1;
                //caseRecord.Transferred_Date__c = DateTime.now();
                caseRecord.Work_Queue_View_Name__c = 'Test Queue View';
                lstCase.add(caseRecord);
                
                caseRecord = new Case();
                caseRecord.Owner_Queue__c = 'Test Omni Queue';
                caseRecord.Status = 'In Progress';
                caseRecord.AccountId = accountRec.id;
                caseRecord.Interacting_About_Type__c = 'Member';                
                caseRecord.ownerId = usr.Id;
                caseRecord.Classification_Type__c = 'calls';
                caseRecord.Classification_id__c=lstClassification[0].id;
                caseRecord.Intent_id__c = lstIntent[0].id;
                caseRecord.Interacting_With__c = accountRec.id;
                caseRecord.Interacting_With_Type__c = 'Member';
                caseRecord.Policy_Member__c = null;
                caseRecord.Call_Benefit_Category__c = 'Medicare Part C';
                caseRecord.Autoroute_Status__c = 'Evaluate';
                caseRecord.Origin = 'Inbound Call';
                caseRecord.Omni_Eligible__c = true;
                caseRecord.IsEscalated = false;
                caseRecord.Due_Date__c = Date.today();
                caseRecord.Follow_up_Due_Date__c = Date.today() + 1;
                //caseRecord.Transferred_Date__c = DateTime.now();
                //caseRecord.Work_Queue_View_Name__c = 'Test Queue View';
                lstCase.add(caseRecord);
                
                caseRecord = new Case();
                // caseRecord.Owner_Queue__c = 'Test Omni Queue';
                caseRecord.Status = 'In Progress';
                caseRecord.AccountId = accountRec.id;
                caseRecord.Interacting_About_Type__c = 'Member';                
                caseRecord.ownerId = usr.Id;
                caseRecord.Classification_Type__c = 'calls';
                caseRecord.Classification_id__c=lstClassification[0].id;
                caseRecord.Intent_id__c = lstIntent[0].id;
                caseRecord.Interacting_With__c = accountRec.id;
                caseRecord.Interacting_With_Type__c = 'Member';
                caseRecord.Policy_Member__c = null;
                caseRecord.Call_Benefit_Category__c = 'Medicare Part C';
                caseRecord.Autoroute_Status__c = 'Evaluate';
                caseRecord.Origin = 'Inbound Call';
                caseRecord.Omni_Eligible__c = true;
                caseRecord.IsEscalated = false;
                caseRecord.Due_Date__c = Date.today();
                caseRecord.Follow_up_Due_Date__c = Date.today() + 1;
                //caseRecord.Transferred_Date__c = DateTime.now();
                //caseRecord.Work_Queue_View_Name__c = 'Test Queue View';
                
                lstCase.add(caseRecord);

                caseRecord = new Case();
                caseRecord.Owner_Queue__c = 'Test Omni Queue';
                caseRecord.Status = 'Closed';
                caseRecord.AccountId = accountRec.id;
                caseRecord.Interacting_About_Type__c = 'Member';                
                caseRecord.ownerId = usr.Id;
                caseRecord.Classification_Type__c = 'calls';
                caseRecord.Classification_id__c=lstClassification[0].id;
                caseRecord.Intent_id__c = lstIntent[0].id;
                caseRecord.Interacting_With__c = accountRec.id;
                caseRecord.Interacting_With_Type__c = 'Member';
                caseRecord.Policy_Member__c = null;
                caseRecord.Call_Benefit_Category__c = 'Medicare Part C';
                caseRecord.Autoroute_Status__c = 'Evaluate';
                caseRecord.Origin = 'Inbound Call';
                caseRecord.Omni_Eligible__c = true;
                caseRecord.IsEscalated = false;
                caseRecord.Complaint__c = 'No';
                caseREcord.G_A_Rights_Given__c='No';
                lstCase.add(caseRecord);

            }
            Insert lstCase; 
            
            
            // Add Test Data for TRR evaluation
            
            Template__c temp = new Template__c();
            temp.Description__c = 'Template Trigger';
            temp.Unique_Template_Name__c = 'TRR_Template' ;
            insert temp;
            
            Template_Field__c tempField = new Template_Field__c();
            tempField.Name = 'Processing Time Stamp';
            tempField.Data_Type__c = 'DATETIME';
            tempField.Template__c = temp.Id;
            insert tempField;
            
            Template_Field__c tempField2 = new Template_Field__c();
            tempField2.Name = 'Trans Code';
            tempField2.Data_Type__c = 'STRING';
            tempField2.Template__c = temp.Id;
            insert tempField2;
            
            Case trrCaseRecord = new Case();
            trrCaseRecord.Owner_Queue__c = 'Test Omni Queue';
            trrCaseRecord.Status = 'In Progress';
            trrCaseRecord.AccountId = accountRec.id;
            trrCaseRecord.Interacting_About_Type__c = 'Member';            
            trrCaseRecord.ownerId = usr.Id;
            trrCaseRecord.Classification_Type__c = 'calls';
            trrCaseRecord.Classification_id__c=lstClassification[0].id;
            trrCaseRecord.Intent_id__c = lstIntent[0].id;
            trrCaseRecord.Interacting_With__c = accountRec.id;
            trrCaseRecord.Interacting_With_Type__c = 'Member';
            trrCaseRecord.Policy_Member__c = null;
            trrCaseRecord.Call_Benefit_Category__c = 'Medicare Part C';
            trrCaseRecord.Autoroute_Status__c = 'Evaluate';
            trrCaseRecord.Origin = 'CRMTRR';
            trrCaseRecord.Type = 'TRR Inquiry';
            trrCaseRecord.Omni_Eligible__c = true;
            trrCaseRecord.IsEscalated = false;
            trrCaseRecord.Due_Date__c = Date.today();
            trrCaseRecord.Subtype__c = 'TRR';
            trrCaseRecord.Follow_up_Due_Date__c = Date.today() + 1;
            //trrCaseRecord.Transferred_Date__c = DateTime.now();
            trrCaseRecord.Work_Queue_View_Name__c = 'Test Queue View';
            
            insert trrCaseRecord;
            
            Template_Submission_Owner__c TSubOwner = new Template_Submission_Owner__c();
            TSubOwner.Object_Owner_ID__c = trrCaseRecord.Id;
            TSubOwner.Object_Owner_Type__c = 'Case';
            Insert TSubOwner;
            
            Template_Submission__c TSubmission = new Template_Submission__c();
            TSubmission.Template__c = temp.Id;
            TSubmission.Tenant_Id__c = '00';
            TSubmission.Version__c = 0;
            TSubmission.Submission_Owner__c = TSubOwner.Id;
            Insert TSubmission;
            
            
            
            List<Template_Submission_Data__c> lstTSubData = new List<Template_Submission_Data__c>();
            Template_Submission_Data__c TSubData = new Template_Submission_Data__c();
            TSubData.Template_Field__c = tempField.Id;  //Reply Code
            TSubData.Template_Submission__c = TSubmission.Id;
            TSubData.Value__c = '2019-03-25 10:25:30';
            lstTSubData.add(TSubData);
            
            TSubData = new Template_Submission_Data__c();
            TSubData.Template_Field__c = tempField2.Id;  //Trans Code
            TSubData.Template_Submission__c = TSubmission.Id;
            TSubData.Value__c = 'ABC';
            lstTSubData.add(TSubData);
            
            insert lstTSubData;
            
            
            
        }
    }
    
    
    /*
    * This method is used to test All the methods of CaseTriggerHandler_HUM class
    * @param   None
    * @return  None
    */
    static testMethod void testAllHandlerMethods()
    {
        User testUser = [Select Id,Name from User where LastName='test humcrmsOmniUser'];
        
        Boolean testResult = false;
        
        System.runAs(testUser)
        {
            
            Test.startTest();
            List<Case> lstNewCase= [Select id,casenumber,
                                    OwnerId, Owner_Queue__c,Work_Queue_View_Name__c,Omni_Eligible__c,
                                    Type, 
                                    Status, 
                                    Origin, 
                                    Priority,
                                    IsEscalated, 
                                    CreatedDate, 
                                    LastModifiedDate, 
                                    Group_Billing_State_Code__c, 
                                    Case_Age__c, 
                                    Case_TAT__c, 
                                    Complaint_Reason__c, 
                                    Complaint__c, 
                                    Days_Since_Last_Modified__c, 
                                    Department__c, 
                                    Due_Date__c, 
                                    Employer_Group_Name__c, 
                                    Follow_Up_Needed__c, 
                                    Exchange_Type__c, 
                                    Follow_up_Due_Date__c, 
                                    Funding_Type__c, 
                                    G_A_Reason__c, 
                                    G_A_Rights_Given__c, 
                                    General_Case__c, 
                                    HO_Dental_Case__c, 
                                    HO_Medical_Case__c, 
                                    Interacting_About_Type__c, 
                                    Interacting_With_Type__c, 
                                    Language_Preference__c, 
                                    Line_of_Business__c, 
                                    Plan_Issue_State__c, 
                                    Plan_Segment_Size__c, 
                                    Policy_Platform__c,
                                    Policy_Member__c,                                   
                                    Product_Type__c, 
                                    Product__c, 
                                    Exchange__c, 
                                    Re_Open_Case_Age__c, 
                                    Re_Open_Case_Date__c, 
                                    Reopened__c, 
                                    Service_Center__c, 
                                    Subtype__c, 
                                    Task_TAT__c, 
                                    Tenant_Id__c, 
                                    Texas_Complaint_Letter_Requested__c, 
                                    Topic__c, 
                                    Apply_to_Complaint__c, 
                                    Email_Sent__c, 
                                    NPI_ID__c, 
                                    Recommended_Process__c, 
                                    Tax_ID__c, 
                                    Case_Referred_To__c, 
                                    Referred_to_Another_Channel__c, 
                                    Selling_Market_Number__c, 
                                    Behavioral_Health__c, 
                                    OpenEnrollmentType__c, 
                                    OpenEnrollment__c, 
                                    Open_Work_Task_Count__c, 
                                    Policy_Product_Code__c, 
                                    Provider_Shipping_State_Code__c, 
                                    Group_Number_for_Policy__c, 
                                    Previous_Owner_Queue__c, 
                                    Complaint_Type__c, 
                                    Autoroute_Status__c, 
                                    Total_Work_Task_Count__c, 
                                    DCN_Formula__c, 
                                    DCN__c, 
                                    Doc_Type__c, 
                                    Classification_Id__r.Name, 
                                    Intent_Id__r.Name, Transferred_Date__c
                                    from case];
             
             //Trr Test
             List<Case> trrCase = new List<Case>();
             Set<String> caseIds = new Set<String>();
             for(Case c:lstNewCase)
             {
                if(c.Subtype__c == 'TRR')
                {
                    trrCase.add(c);
                    caseIds.add(c.Id);
                }
             }  
                               
             OmniCaseAssignmentRuleEvaluation_H_HUM.EvaluateOmniCaseAssignmentRule(trrCase, false);
             OmniCaseAssignmentRuleEvaluation_H_HUM.evaluateforTemplate(caseIds);
                                                
                                    
            //Bulk Test
            OmniCaseAssignmentRuleEvaluation_H_HUM.EvaluateOmniCaseAssignmentRule(lstNewCase, true);
            
            lstNewCase[0].Origin = 'CRMTRR';
            lstNewCase[0].Type = 'TRR Inquiry';
            lstNewCase[0].subtype__c = 'TRR';
            update lstNewCase;
            
            List<Case> lstCase = new List<Case>();
            lstCase.add(lstNewCase[0]);
                        
            //Single record test
            OmniCaseAssignmentRuleEvaluation_H_HUM.EvaluateOmniCaseAssignmentRule(lstCase, false);
            OmniCaseAssignmentRuleEvaluation_H_HUM.getCaseAssignmentRule(lstCase[0]);
            OmniCaseAssignmentRuleEvaluation_H_HUM.getRelatedFieldValue('Classification__c',lstNewCase[0].Classification_Id__c, 'Name');
            OmniCaseAssignmentRuleEvaluation_H_HUM.validateString('Test', 'Test', 'equals');
            OmniCaseAssignmentRuleEvaluation_H_HUM.validateString('Test', 'Test1', 'notequalto');
            OmniCaseAssignmentRuleEvaluation_H_HUM.validateString('Test2', 'Test1', 'startswith');
            OmniCaseAssignmentRuleEvaluation_H_HUM.validateString('Test', 'Test', 'contains');
            OmniCaseAssignmentRuleEvaluation_H_HUM.validateString('Test', 'XYZ', 'doesnotcontain');
            testResult = OmniCaseAssignmentRuleEvaluation_H_HUM.validateString('XYZ', 'XYZ', 'in');
            system.assertEquals(true, testResult);
            testResult = OmniCaseAssignmentRuleEvaluation_H_HUM.validateString('Test', 'XYZ', 'notin');
            system.assertEquals(true, testResult);
            
            OmniCaseAssignmentRuleEvaluation_H_HUM.validateBoolean(True, True, 'equals');
            OmniCaseAssignmentRuleEvaluation_H_HUM.validateBoolean(True, False, 'notequalto');
            
            OmniCaseAssignmentRuleEvaluation_H_HUM.validateDecimal(10, 10, 'equals');
            OmniCaseAssignmentRuleEvaluation_H_HUM.validateDecimal(20.5, 20, 'notequalto');
            OmniCaseAssignmentRuleEvaluation_H_HUM.validateDecimal(6.2, 5.926, 'lessthan');
            OmniCaseAssignmentRuleEvaluation_H_HUM.validateDecimal(6.3123,6.3124, 'greaterthan');
            OmniCaseAssignmentRuleEvaluation_H_HUM.validateDecimal(6.3125,6.3124, 'lessorequal');
            OmniCaseAssignmentRuleEvaluation_H_HUM.validateDecimal(6.3123,6.3124, 'greaterorequal');
            
            
            OmniCaseAssignmentRuleEvaluation_H_HUM.validateDate(Date.valueOf('2017-12-29'), Date.valueOf('2017-12-29'), 'equals');
            OmniCaseAssignmentRuleEvaluation_H_HUM.validateDate(Date.valueOf('2017-12-28'), Date.valueOf('2017-12-29'), 'notequalto');
            OmniCaseAssignmentRuleEvaluation_H_HUM.validateDate(Date.valueOf('2017-12-30'), Date.valueOf('2017-12-29'), 'lessthan');
            OmniCaseAssignmentRuleEvaluation_H_HUM.validateDate(Date.valueOf('2017-12-28'), Date.valueOf('2017-12-29'), 'greaterthan');
            OmniCaseAssignmentRuleEvaluation_H_HUM.validateDate(Date.valueOf('2017-12-29'), Date.valueOf('2017-12-29'), 'lessorequal');
            OmniCaseAssignmentRuleEvaluation_H_HUM.validateDate(Date.valueOf('2017-12-28'), Date.valueOf('2017-12-29'), 'greaterorequal');
            
            OmniCaseAssignmentRuleEvaluation_H_HUM.validateDateTime(DateTime.now(), DateTime.now(), 'equals');
            OmniCaseAssignmentRuleEvaluation_H_HUM.validateDateTime(DateTime.now(), DateTime.now()+1, 'notequalto');
            OmniCaseAssignmentRuleEvaluation_H_HUM.validateDateTime(DateTime.now()+1, DateTime.now(), 'lessthan');
            OmniCaseAssignmentRuleEvaluation_H_HUM.validateDateTime(DateTime.now(), DateTime.now()+1, 'greaterthan');
            OmniCaseAssignmentRuleEvaluation_H_HUM.validateDateTime(DateTime.now()+1, DateTime.now(), 'lessorequal');
            OmniCaseAssignmentRuleEvaluation_H_HUM.validateDateTime(DateTime.now(), DateTime.now()+1, 'greaterorequal');
            
            testResult = OmniCaseAssignmentRuleEvaluation_H_HUM.validateAgeFields((DateTime.now()-1).date(), 2, 'less or equal upto Today');
            system.assertEquals(true, testResult);
            testResult = OmniCaseAssignmentRuleEvaluation_H_HUM.validateAgeFields((DateTime.now()-1).date(), 2, 'greater or equal upto Today');
            system.assertEquals(false, testResult);
            testResult = OmniCaseAssignmentRuleEvaluation_H_HUM.validateAgeFields((DateTime.now()+1).date(), 2, 'less or equal from Today');
            system.assertEquals(true, testResult);
            testResult = OmniCaseAssignmentRuleEvaluation_H_HUM.validateAgeFields((DateTime.now()+1).date(), 2, 'greater or equal from Today');
            system.assertEquals(false, testResult);
            
            Test.stopTest();
        }
        
    }
    
    /*          
    * This method is used to test All the methods of CaseTriggerHandler_HUM class for Exception     
    * @param   None     
    * @return  None     
    */      
    static testMethod void testAllHandlerMethodsForException()      
    {       
        User testUser = [Select Id,Name,LastName from User where LastName='test humcrmsOmniUser'];   
        System.assertEquals(testUser.LastName, 'test humcrmsOmniUser');   
        System.runAs(testUser)      
        {       
            Test.startTest();       
            List<Case> lstNewCase = null;       
            OmniCaseAssignmentRuleEvaluation_H_HUM.EvaluateOmniCaseAssignmentRule(lstNewCase, false);
            Test.stopTest();        
        }       
    }
    /*
    * testing gor listassignmentrules
    */
    static testMethod void testcodeCoverage()
    {
        User testUser = [Select Id,Name from User where LastName='test humcrmsOmniUser'];
        
        Boolean testResult = false;
        
        System.runAs(testUser)
        {
            
            Test.startTest();
            List<Case> lstNewCase= [Select id,casenumber,
                                    OwnerId, Owner_Queue__c,Work_Queue_View_Name__c,Omni_Eligible__c,
                                    Type, 
                                    Status, 
                                    Origin, 
                                    Priority,
                                    IsEscalated, 
                                    CreatedDate, 
                                    LastModifiedDate, 
                                    Group_Billing_State_Code__c, 
                                    Case_Age__c, 
                                    Case_TAT__c, 
                                    Complaint_Reason__c, 
                                    Complaint__c, 
                                    Days_Since_Last_Modified__c, 
                                    Department__c, 
                                    Due_Date__c, 
                                    Employer_Group_Name__c, 
                                    Follow_Up_Needed__c, 
                                    Exchange_Type__c, 
                                    Follow_up_Due_Date__c, 
                                    Funding_Type__c, 
                                    G_A_Reason__c, 
                                    G_A_Rights_Given__c, 
                                    General_Case__c, 
                                    HO_Dental_Case__c, 
                                    HO_Medical_Case__c, 
                                    Interacting_About_Type__c, 
                                    Interacting_With_Type__c, 
                                    Language_Preference__c, 
                                    Line_of_Business__c, 
                                    Plan_Issue_State__c, 
                                    Plan_Segment_Size__c, 
                                    Policy_Platform__c,
                                    Policy_Member__c,                                   
                                    Product_Type__c, 
                                    Product__c, 
                                    Exchange__c, 
                                    Re_Open_Case_Age__c, 
                                    Re_Open_Case_Date__c, 
                                    Reopened__c, 
                                    Service_Center__c, 
                                    Subtype__c, 
                                    Task_TAT__c, 
                                    Tenant_Id__c, 
                                    Texas_Complaint_Letter_Requested__c, 
                                    Topic__c, 
                                    Apply_to_Complaint__c, 
                                    Email_Sent__c, 
                                    NPI_ID__c, 
                                    Recommended_Process__c, 
                                    Tax_ID__c, 
                                    Case_Referred_To__c, 
                                    Referred_to_Another_Channel__c, 
                                    Selling_Market_Number__c, 
                                    Behavioral_Health__c, 
                                    OpenEnrollmentType__c, 
                                    OpenEnrollment__c, 
                                    Open_Work_Task_Count__c, 
                                    Policy_Product_Code__c, 
                                    Provider_Shipping_State_Code__c, 
                                    Group_Number_for_Policy__c, 
                                    Previous_Owner_Queue__c, 
                                    Complaint_Type__c, 
                                    Autoroute_Status__c, 
                                    Total_Work_Task_Count__c, 
                                    DCN_Formula__c, 
                                    DCN__c, 
                                    Doc_Type__c, 
                                    Classification_Id__r.Name, 
                                    Intent_Id__r.Name, Transferred_Date__c
                                    from case];
             
             //Trr Test
             
             List<Case> trrCase = new List<Case>();
             Set<String> caseIds = new Set<String>();
             for(Case c:lstNewCase)
             {
                if(c.Subtype__c == 'TRR')
                {
                    trrCase.add(c);
                    caseIds.add(c.Id);
                }
             }                      
             OmniCaseAssignmentRuleEvaluation_H_HUM.EvaluateOmniCaseAssignmentRule(trrCase, false);
             OmniCaseAssignmentRuleEvaluation_H_HUM.evaluateforTemplate(caseIds);
                                                
                                    
            //Bulk Test
            OmniCaseAssignmentRuleEvaluation_H_HUM.EvaluateOmniCaseAssignmentRule(lstNewCase, true);
            
            lstNewCase[0].Origin = 'CRMTRR';
            lstNewCase[0].Type = 'TRR Inquiry';
            lstNewCase[0].subtype__c = 'TRR';
            lstNewCase[0].Owner_Queue__c = 'Test Omni Queues1';
            update lstNewCase;
            
            List<Case> lstCase = new List<Case>();
            lstCase.add(lstNewCase[0]);
            List<OmniRuleCriteria_Fields__mdt> lstFieldsType = [Select Label, Field_APIName__c, Field_Type__c, ParentObject_APIName__c,Object_API_Name__c From OmniRuleCriteria_Fields__mdt];
            System.debug('test classs mdt list '+lstFieldsType);
                        
            //Single record test
            //OmniCaseAssignmentRuleEvaluation_H_HUM.EvaluateOmniCaseAssignmentRule(lstCase, false);
            OmniCaseAssignmentRuleEvaluation_H_HUM.getCaseAssignmentRule(lstCase[0]);
            OmniCaseAssignmentRuleEvaluation_H_HUM.getRelatedFieldValue('Classification__c',lstNewCase[0].Classification_Id__c, 'Name');
            OmniCaseAssignmentRuleEvaluation_H_HUM.validateString('Test', 'Test', 'equals');
            testResult = OmniCaseAssignmentRuleEvaluation_H_HUM.validateString('XYZ', 'XYZ', 'in');
            system.assertEquals(true, testResult);
            testResult = OmniCaseAssignmentRuleEvaluation_H_HUM.validateString('Test', 'XYZ', 'notin');
            system.assertEquals(true, testResult);
            
            OmniCaseAssignmentRuleEvaluation_H_HUM.EvaluateOmniCaseAssignmentRule(lstCase, false);
            OmniCaseAssignmentRuleEvaluation_H_HUM.calculateCaseAge(new case());
            Map<String, SObject> mapRelatedObjects = OmniCaseAssignmentRuleEvaluation_H_HUM.getRelationshipRecordsBulk(lstNewCase);
            SObject relatedObject;
            relatedObject = OmniCaseAssignmentRuleEvaluation_H_HUM.getRelatedObject('Classification__c', lstNewCase[2].Classification_Id__c);
            System.debug('Testclass   relatedobject'+relatedObject);
            OmniCaseAssignmentRuleEvaluation_H_HUM.getFieldValue(relatedObject , 'Name');
            Test.stopTest();
        }
        
    }
    

    static testMethod void testcodeCoverageNew()
    {
        User testUser = [Select Id,Name from User where LastName='test humcrmsOmniUser'];
        
        Boolean testResult = false;
        
        System.runAs(testUser)
        {
            
            Test.startTest();
            List<Case> lstNewCase= [Select id,casenumber,
                                    OwnerId, Owner_Queue__c,Work_Queue_View_Name__c,Omni_Eligible__c,
                                    Type, 
                                    Status, 
                                    Origin, 
                                    Priority,
                                    IsEscalated, 
                                    CreatedDate, 
                                    LastModifiedDate, 
                                    Group_Billing_State_Code__c, 
                                    Case_Age__c, 
                                    Case_TAT__c, 
                                    Complaint_Reason__c, 
                                    Complaint__c, 
                                    Days_Since_Last_Modified__c, 
                                    Department__c, 
                                    Due_Date__c, 
                                    Employer_Group_Name__c, 
                                    Follow_Up_Needed__c, 
                                    Exchange_Type__c, 
                                    Follow_up_Due_Date__c, 
                                    Funding_Type__c, 
                                    G_A_Reason__c, 
                                    G_A_Rights_Given__c, 
                                    General_Case__c, 
                                    HO_Dental_Case__c, 
                                    HO_Medical_Case__c, 
                                    Interacting_About_Type__c, 
                                    Interacting_With_Type__c, 
                                    Language_Preference__c, 
                                    Line_of_Business__c, 
                                    Plan_Issue_State__c, 
                                    Plan_Segment_Size__c, 
                                    Policy_Platform__c,
                                    Policy_Member__c,                                   
                                    Product_Type__c, 
                                    Product__c, 
                                    Exchange__c, 
                                    Re_Open_Case_Age__c, 
                                    Re_Open_Case_Date__c, 
                                    Reopened__c, 
                                    Service_Center__c, 
                                    Subtype__c, 
                                    Task_TAT__c, 
                                    Tenant_Id__c, 
                                    Texas_Complaint_Letter_Requested__c, 
                                    Topic__c, 
                                    Apply_to_Complaint__c, 
                                    Email_Sent__c, 
                                    NPI_ID__c, 
                                    Recommended_Process__c, 
                                    Tax_ID__c, 
                                    Case_Referred_To__c, 
                                    Referred_to_Another_Channel__c, 
                                    Selling_Market_Number__c, 
                                    Behavioral_Health__c, 
                                    OpenEnrollmentType__c, 
                                    OpenEnrollment__c, 
                                    Open_Work_Task_Count__c, 
                                    Policy_Product_Code__c, 
                                    Provider_Shipping_State_Code__c, 
                                    Group_Number_for_Policy__c, 
                                    Previous_Owner_Queue__c, 
                                    Complaint_Type__c, 
                                    Autoroute_Status__c, 
                                    Total_Work_Task_Count__c, 
                                    DCN_Formula__c, 
                                    DCN__c, 
                                    Doc_Type__c, 
                                    Classification_Id__r.Name, 
                                    Intent_Id__r.Name, Transferred_Date__c
                                    from case where Subtype__C = 'TRR'];
             
             //TRR Test
             OmniCaseAssignmentRuleEvaluation_H_HUM.EvaluateOmniCaseAssignmentRule(lstNewCase, false);
            //Bulk Test
            OmniCaseAssignmentRuleEvaluation_H_HUM.EvaluateOmniCaseAssignmentRule(lstNewCase, true);

            List<Case> lstccase= [Select id,casenumber,OwnerId, Owner_Queue__c,Work_Queue_View_Name__c,Omni_Eligible__c,Type, Status, Origin, Priority,
                                    IsEscalated, CreatedDate, LastModifiedDate, Group_Billing_State_Code__c, Case_Age__c, Case_TAT__c, 
                                    Complaint_Reason__c, Complaint__c, Days_Since_Last_Modified__c, Department__c, Due_Date__c, Employer_Group_Name__c, Follow_Up_Needed__c, Exchange_Type__c, 
                                    Follow_up_Due_Date__c, Funding_Type__c, G_A_Reason__c, G_A_Rights_Given__c, General_Case__c, HO_Dental_Case__c, HO_Medical_Case__c, 
                                    Interacting_About_Type__c, Interacting_With_Type__c, 
                                    Doc_Type__c, 
                                    Classification_Id__r.Name, 
                                    Intent_Id__r.Name, Transferred_Date__c
                                    from case];
            List<Omni_Case_Assignment_Rule__c> lstocar = [SELECT id, Is_Active__c from Omni_Case_Assignment_Rule__c];        
            for(Integer i = 0 ; i < lstocar.size() ; i++)
            {
                lstocar[i].Is_Active__c = false;
            }
            update lstocar;

            List<OmniRuleCriteria_Fields__mdt> lstFieldsType = [Select Label, Field_APIName__c, Field_Type__c, ParentObject_APIName__c,Object_API_Name__c From OmniRuleCriteria_Fields__mdt];
            System.debug('test classs mdt list '+lstFieldsType);
            OmniCaseAssignmentRuleEvaluation_H_HUM.EvaluateOmniCaseAssignmentRule(lstccase, false);
            
            Test.stopTest();
        }
        
    }

    static testMethod void testStaticMethods()
    {
        User testUser = [Select Id,Name from User where LastName='test humcrmsOmniUser'];
        
        Boolean testResult = false;
        
        System.runAs(testUser)
        {
            
            Test.startTest();
            OmniCaseAssignmentRuleEvaluation_H_HUM.validateRuleCriteriaByDataType('boolean','','','in');
            OmniCaseAssignmentRuleEvaluation_H_HUM.validateRuleCriteriaByDataType('double','20,200','200','in');
            OmniCaseAssignmentRuleEvaluation_H_HUM.validateRuleCriteriaByDataType('double','20,200,500','2000','not in');
            OmniCaseAssignmentRuleEvaluation_H_HUM.validateRuleCriteriaByDataType('date','today','06/29/2021','in');
            OmniCaseAssignmentRuleEvaluation_H_HUM.validateRuleCriteriaByDataType('date','06/29/2021','06/29/2021','greater or equal upto Today');
            OmniCaseAssignmentRuleEvaluation_H_HUM.validateRuleCriteriaByDataType('date','20210629','2021-06-29','greater or equal upto Today');
            OmniCaseAssignmentRuleEvaluation_H_HUM.validateRuleCriteriaByDataType('date','20210629','2021-06-29','greater or equal from Today');
            OmniCaseAssignmentRuleEvaluation_H_HUM.validateRuleCriteriaByDataType('date','20210629','2021-06-29','greater or equal');
            OmniCaseAssignmentRuleEvaluation_H_HUM.validateRuleCriteriaByDataType('datetime','06/29/2021','2021-06-29 09:03:45','greater or equal upto Today');
            OmniCaseAssignmentRuleEvaluation_H_HUM.validateRuleCriteriaByDataType('datetime','20210629','2021-06-29','greater or equal upto Today');
            OmniCaseAssignmentRuleEvaluation_H_HUM.validateRuleCriteriaByDataType('datetime','20210629','2021-06-29','greater or equal from Today');
            OmniCaseAssignmentRuleEvaluation_H_HUM.validateRuleCriteriaByDataType('datetime','20210629','2021-06-29','greater or equal');
            Test.stopTest();
        }
        
    }
}