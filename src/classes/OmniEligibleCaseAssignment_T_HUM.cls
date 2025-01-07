/****************************************************************************************
Apex Class Name  : OmniEligibleCaseAssignment_T_HUM
Version          : 1.0 
Created Date     : 12/19/2017
Function         : Test class for OmniEligibleCaseAssignment_H_HUM Apex class
Modification Log :
 *  Developer         Code Review            Date                 Description
 * ----------------------------------------------------------------------------------------           
 * Manoj Chaurasia    167654                 12/19/2017              Original Version  
 * Manoj Chaurasia    178236                 02/01/2018              Used 'In Progress' for new cases 
 * Manoj Chaurasia    186768                 02/16/2018              Set values for case fields(Owner_Queue__c, Case_Owner__c, Work_Queue_View_Name__c)
 * Santhi Mandava                            03/14/2019              Removed "DpaaSWebServiceMockImpl" usage.
 * Pooja Kumbhar						     04/29/2020				 Checkmarx test method without assert error fix.
 * Vamshi Krishna Pemberthi						  06/08/2021 		Code Coverage Improvements
 * Pooja Kumbhar			     07/31/2023		      Test class failure fix for 8_4 release
 *******************************************************************************************/
@isTest
public class OmniEligibleCaseAssignment_T_HUM {
    
/*
* This method creates test data for test class
* @param None
* @return None
*/
    @testSetup static void setUpTestData()
    {
        User usr = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert usr;
        system.assertEquals(usr.lastname=='test User1',true);
		
		User usr1 = HUMTestDataHelperTest.createTestUser('User2','System Administrator');
        insert usr1;
        
        PermissionSet pCheck =[SELECT ID From PermissionSet WHERE Name =:GLOBAL_CONSTANT_HUM.MEDICARECALLS_PERMISSIONSET_HUM LIMIT 1];
        insert new PermissionSetAssignment(AssigneeId = usr.id, PermissionSetId = pCheck.id);
        
   
        //create OmniQueue
            Group oQueue = new Group();
            oQueue.name = 'Test Omni Queue';
            oQueue.type = 'Queue';
            insert oQueue;
        
        QueuesObject oQueueObject = new QueuesObject (QueueID = oQueue.id, SobjectType ='Work_Queue_Setup__c');
        insert oQueueObject ;   
        system.assertNotEquals(oQueueObject,null,'Insert Failed');
        System.assertEquals(oQueueObject.SobjectType, 'Work_Queue_Setup__c');
            
            //Create StaginQueue
            Group sQueue = new Group();
            sQueue.name = 'Test Omni Queue_Stg';
            sQueue.type = 'Queue';
            insert sQueue;
             
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
            omniData.Name = 'Test Omni Queue';
            omniData.Maximum_Cases__c = 50;
            omniData.Omni_Queue_ID__c = oQueue.Id;
            omniData.Staging_Queue_ID__c = sQueue.Id;
            omniData.Staging_Queue_Name__c = sQueue.Name;
            omniData.Routing_Configuration_Name__c = routingConfigData.Id;
            insert omniData; 
            
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
            Omni_Case_Assignment_Rule__c oCARule = new Omni_Case_Assignment_Rule__c();
            oCARule.Name = 'Test Omni Case Assignment Rule';
            oCARule.Evaluation_Order__c = 1;
            oCARule.Omni_Queue__c = omniData.Id;
            oCARule.WorkQueue__c = oWorkQS.Id;
            oCARule.Is_Active__c = true;
            Insert oCARule;

            //Create Omni Rule Criteria
            List<Omni_Rule_Criteria__c> lstRuleCriteria = new List<Omni_Rule_Criteria__c>();
            Omni_Rule_Criteria__c oRuleCriteria = new Omni_Rule_Criteria__c();
            oRuleCriteria.Criteria_Field__c = 'Case Age';
            oRuleCriteria.Criteria_Field_Value__c = '20';
            oRuleCriteria.Criteria_Operator__c = 'not equal to';
            oRuleCriteria.Omni_Case_Assignment_Rule__c = oCARule.Id;
            lstRuleCriteria.add(oRuleCriteria);
            
            oRuleCriteria = new Omni_Rule_Criteria__c();
            oRuleCriteria.Criteria_Field__c = 'Transferred Date';
            oRuleCriteria.Criteria_Field_Value__c = '2017-12-31 23:59:59';
            oRuleCriteria.Criteria_Operator__c = 'not equal to';
            oRuleCriteria.Omni_Case_Assignment_Rule__c = oCARule.Id;
            lstRuleCriteria.add(oRuleCriteria);
            
            oRuleCriteria = new Omni_Rule_Criteria__c();
            oRuleCriteria.Criteria_Field__c = 'Status';
            oRuleCriteria.Criteria_Field_Value__c = 'Test';
            oRuleCriteria.Criteria_Operator__c = 'not equal to';
            oRuleCriteria.Omni_Case_Assignment_Rule__c = oCARule.Id;
            lstRuleCriteria.add(oRuleCriteria);
            
            oRuleCriteria = new Omni_Rule_Criteria__c();
            oRuleCriteria.Criteria_Field__c = 'Escalated';
            oRuleCriteria.Criteria_Field_Value__c = 'false';
            oRuleCriteria.Criteria_Operator__c = 'equals';
            oRuleCriteria.Omni_Case_Assignment_Rule__c = oCARule.Id;
            lstRuleCriteria.add(oRuleCriteria);
            
           
            oRuleCriteria = new Omni_Rule_Criteria__c();
            oRuleCriteria.Criteria_Field__c = 'Due Date';
            oRuleCriteria.Criteria_Field_Value__c = '2016-12-31';
            oRuleCriteria.Criteria_Operator__c = 'less than';
            oRuleCriteria.Omni_Case_Assignment_Rule__c = oCARule.Id;
            lstRuleCriteria.add(oRuleCriteria);
            
            oRuleCriteria = new Omni_Rule_Criteria__c();
            oRuleCriteria.Criteria_Field__c = 'Intent Name';
            oRuleCriteria.Criteria_Field_Value__c = 'XYZ';
            oRuleCriteria.Criteria_Operator__c = 'not equal to';
            oRuleCriteria.Omni_Case_Assignment_Rule__c = oCARule.Id;
            lstRuleCriteria.add(oRuleCriteria);
            
            oRuleCriteria = new Omni_Rule_Criteria__c();
            oRuleCriteria.Criteria_Field__c = 'Follow Up Due Date';
            oRuleCriteria.Criteria_Field_Value__c = 'today';
            oRuleCriteria.Criteria_Operator__c = 'not equal to';
            oRuleCriteria.Omni_Case_Assignment_Rule__c = oCARule.Id;
            lstRuleCriteria.add(oRuleCriteria);
            
            Insert lstRuleCriteria;
            
            
            List<HUMApplicationSettings__c> lstApplicationName = new List<HUMApplicationSettings__c>();
            System.assertNotEquals(lstApplicationName, NULL, 'Values not matched');
            lstApplicationName.add(new HUMApplicationSettings__c(Name = 'CIPHERCLOUD', Active__c = true));
            insert lstApplicationName;
            System.assertNotEquals(lstApplicationName.size(), 0, 'Values not matched');
            
            List<HUMCipherCloudSettings__c> lstCipherCloudSetting = new List<HUMCipherCloudSettings__c>();
            System.assertNotEquals(lstCipherCloudSetting, NULL, 'Values not matched');
            lstCipherCloudSetting.add(new HUMCipherCloudSettings__c(Name = 'APPLICATION_NAME', Value__c='CRMR1'));
            insert lstCipherCloudSetting;
            System.assertNotEquals(lstCipherCloudSetting.size(), 0, 'Values not matched');
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
            
            Policy__c policyRec = new Policy__c();
            policyRec.Group_Name__c = accountRec.Id;
            policyRec.Product__c = null;
            policyRec.Policy_Group_Number__c='11111';
            policyRec.Major_Lob__c = 'MEF';
            insert policyRec;
            system.assertEquals(policyRec.Group_Name__c==accountRec.Id,true);
            
            Policy_Member__c policyMemRec = new Policy_Member__c();
            policyMemRec.Name = '12345';
            policyMemRec.Policy__c = policyRec.Id;
            policyMemRec.Member__c = accountRec.Id; 
            insert policyMemRec;
            system.assertEquals(policyMemRec.Policy__c==policyRec.Id,true);
            
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
                        
            for(Integer index=0; index<200; index++){
            
                Case caseRecord = new Case();
                caseRecord.Owner_Queue__c = 'Test Omni Queue';
                caseRecord.Case_Owner__c = 'Test Omni Queue';
                caseRecord.Status = 'In Progress';
                caseRecord.AccountId = accountRec.id;
                caseRecord.ownerId = usr.Id;
                caseRecord.Classification_Type__c = 'calls';
                caseRecord.Classification_id__c=lstClassification[0].id;
                caseRecord.Intent_id__c = lstIntent[0].id;
                caseRecord.Interacting_With__c = accountRec.id;
                caseRecord.Interacting_With_Type__c = 'Member';
                caseRecord.Interacting_About_Type__c = 'Member';
                caseRecord.Policy_Member__c = null;
                caseRecord.Call_Benefit_Category__c = 'Medicare Part C';
                caseRecord.Autoroute_Status__c = 'Evaluate';
                caseRecord.Origin = 'Inbound Call';
                caseRecord.Omni_Eligible__c = true;
                caseRecord.IsEscalated = false;
                caseRecord.Due_Date__c = Date.today();
                caseRecord.Follow_up_Due_Date__c = Date.today() + 1;
                caseRecord.Owner_Queue__c = 'Louisville RSO Enrollment';
                caseRecord.Case_Owner__c = 'Louisville RSO Enrollment';
                caseRecord.Work_Queue_View_Name__c = 'Creditable Coverage';
                
                lstCase.add(caseRecord);
            }
            Insert lstCase;
        }
    }

/*
* This method is used to test All the methods of CaseTriggerHandler_HUM class
* @param   None
* @return  None
*/
    static testMethod void testAllHandlerMethods()
    {
        User testUser = [Select Id,Name,LastName from User where LastName='test User1'];
        System.assertEquals(testUser.LastName, 'test User1');
        
        System.runAs(testUser)
        {
            Test.startTest();
            OmniEligibleCaseAssignment_B_HUM oBatchClass = new OmniEligibleCaseAssignment_B_HUM();
            Database.executeBatch(oBatchClass);
            Test.stopTest();
        }
    }
	
	static testMethod void testAllHandlerMethodException()
    {
        User testUser = [Select Id,Name,LastName from User where LastName='test User2'];
        System.assertEquals(testUser.LastName, 'test User2');
        
        System.runAs(testUser)
        {
            Test.startTest();
            OmniEligibleCaseAssignment_B_HUM oBatchClass = new OmniEligibleCaseAssignment_B_HUM();
            oBatchClass.endTime = System.now().addHours(-2);
            Database.executeBatch(oBatchClass);
            Test.stopTest();
        }
    }
}