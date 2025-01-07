/****************************************************************************
Apex Class Name  : HUMQueueSelection_LT_HUM
Version          : 1.0
Created Date     : November 02 2022
Function         : Test class to HUMQueueSelection_LC Apex class

Modification Log :
* Developer Name                  Code Review                        Date                 Description
* ------------------------------------------------------------------------------------------------------------------------------
*  Manohar                                                        11/02/2022             Original Version
*  Manohar                                                        12/05/2022             Test Class fix - User Permssion issue for Inserting groups
**********************************************************************************************************************************/
@isTest
public class HUMQueueSelection_LT_HUM {
     /**
    * testsetup method to create test data
    *<p>
    * @param            NULL
    * @return           void
    */
    @testSetup static void prepareTestData() 
    {   
        List<User> usrList = New List<User>();
        User oCCSUser = HUMTestDataHelperTest.createTestUser('QSelUser1','Customer Care Specialist');
        User ostandUser = HUMTestDataHelperTest.createTestUser('QselStdUser','Standard User');
        User oadminSUser = HUMTestDataHelperTest.createTestUser('QSelAmdinUser','System Administrator');
        usrList.add(oCCSUser);
        usrList.add(ostandUser);
        usrList.add(oadminSUser);
        insert usrList;
        system.assertEquals(oCCSUser.LastName,'test QSelUser1');

        System.runAs (oadminSUser) 
        { 
            Group oGroup1 = HUMTestDataHelperTest.getQueues('Group one');
            insert oGroup1;
            System.assertEquals(oGroup1.Name,'Group one');
            
            Group oGroup3 = HUMTestDataHelperTest.getQueues('Group Three');
            insert oGroup3;
            System.assertEquals(oGroup3.Name,'Group Three');
            
            Group oGroup2 = HUMTestDataHelperTest.getQueues('Group Two');
            insert oGroup2;
            System.assertEquals(oGroup2.Name,'Group Two');
            
            QueueSObject oQueue2 = HUMTestDataHelperTest.getCaseQueues(oGroup2.Id);
            insert oQueue2;
            System.assertEquals(oQueue2.SobjectType,'Case');
            
            GroupMember GroupMem = HUMTestDataHelperTest.getGrpMems(oGroup1.id, oCCSUser.id);
            insert GroupMem;
            System.assertEquals(GroupMem.GroupId,oGroup1.id);
            
            GroupMember GroupMem2 = HUMTestDataHelperTest.getGrpMems(oGroup2.id, oCCSUser.id);
            insert GroupMem2; 
            System.assertEquals(GroupMem2.GroupId,oGroup2.id);
            
            Group oGrp=new Group(Name='Group_Two', Type='QUEUE');
            insert oGrp;
            System.assertEquals(oGrp.Name,'Group_Two');
            
            Classification_Type__c CTObj = new Classification_Type__c(name='Calls (RSO)',Mentor_Function__c='Contact Center RSO');
            insert CTObj;
            System.assertEquals(CTObj.Name,'Calls (RSO)');
            
            QueuesObject oQueueObject = new QueuesObject (QueueID = oGrp.id, SobjectType ='Work_Queue_Setup__c');
            insert oQueueObject ;   
            System.assertEquals(oQueueObject.SobjectType,'Work_Queue_Setup__c');
            
            Work_Queue_Setup__c oWQS = new Work_Queue_Setup__c(Public_Group_Name__c = 'Group one', Service_Center__c = 'Group',  Department__c = 'Two', Name = 'Group_Two', Classification_Type__c=CTObj.Id);
            oWQS.OwnerId = oGrp.Id;            
            insert oWQS;
            System.assertEquals(oWQS.Public_Group_Name__c,'Group one');
            
            Account oAccount = HUMTestDataHelperTest.getAccount();
            oAccount.Name = 'New Case Account';
            oAccount.Group_Number__c  = '123';
            oAccount.BillingState = 'New York';
            oAccount.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Provider').getRecordTypeId();
            insert oAccount;
            System.assertEquals(oAccount.Group_Number__c,'123');
            
            ConsumerID__c oConsumer = new ConsumerID__c(Account__c = oAccount.id, Consumer_ID__c = '12345');
            insert oConsumer;
            System.assertEquals(oConsumer.Consumer_ID__c,'12345');
            
            HUMCaseNPI_TAXID__c oTaxNPI = new HUMCaseNPI_TAXID__c(Name = 'Provider');
            insert oTaxNPI;
            System.assertEquals(oTaxNPI.Name,'Provider');
            
            HUMCaseInteractionAboutType__c oCaseInterAbotType = new HUMCaseInteractionAboutType__c();
            System.assert(oCaseInterAbotType != null);            
            oCaseInterAbotType.Name = 'Provider';
            oCaseInterAbotType.CaseRecordType__c = 'Provider';
            oCaseInterAbotType.CaseInteraction_About_Type__c = 'Provider';
            insert oCaseInterAbotType;
            System.assertEquals(oCaseInterAbotType.CaseRecordType__c,'Provider');
            
            Product2 oProduct = HUMTestDataHelperTest.getProduct();
            oProduct.Name='Mediclaim';
            insert oProduct;
            system.assertEquals(oProduct.Name,'Mediclaim');
            
            Policy__c oPolicy = HUMTestDataHelperTest.getPolicy();
            oPolicy.Name = 'test123';
            oPolicy.Group_Name__c = oAccount.Id;
            oPolicy.Funding_Type_Code__c = 'Y';
            oPolicy.Product__c = oProduct.Id;
            oPolicy.Issue_State__c = 'IL'; 
            insert oPolicy;
            system.assertEquals(oPolicy.Issue_State__c, 'IL');
            
            Policy_Member__c oPolicyMem = HUMTestDataHelperTest.getPolicyMember();
            oPolicyMem.Name = '12345';
            oPolicyMem.Member__c = oAccount.Id;
            oPolicyMem.Policy__c = oPolicy.Id;            
            oPolicyMem.Relationship__c = 'Self';
            insert oPolicyMem;
            system.assertEquals(oPolicyMem.Name, '12345');
            
            Case oCase = HUMTestDataHelperTest.getCase();
            oCase.AccountId = oAccount.Id;
            oCase.Status = 'In Progress';
            oCase.Case_Owner__c = UserInfo.getUserName();
            oCase.Owner_Queue__c = 'Dental Calls';
            insert oCase;
            System.assertEquals(oCase.Owner_Queue__c, 'Dental Calls');
            
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
            system.assertEquals(intentRec.Name == 'Filling', true);

            Case oCase1 = new Case(AccountId = oAccount.Id, Intent_Id__c = lstIntent[0].id, Classification_Id__c =lstClassification[0].id, Policy_Member__c= oPolicyMem.Id ,
                             Interacting_With_Type__c = 'Member',Interacting_With__c= oAccount.Id, Due_Date__c=Date.Today(), 
                             Case_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU', Work_Queue_View_Name__c='Cincinati CRU',Service_Center__c ='Cincinnati',Department__c='CRU', status='In Progress');
            oCase1.RecordTypeId = Schema.sObjectType.Case.getRecordTypeInfosByName().get('Provider Case').getRecordTypeId();
            insert oCase1;
            System.assertEquals(oCase1.Interacting_With_Type__c, 'Member');
            
            Task oTask = HUMTestDataHelperTest.getTaskRecord();
            oTask.WhatId = oCase.Id;
            oTask.Status = 'In Progress';
            oTask.Task_Owner__c = UserInfo.getUserName();
            oTask.Owner_Queue__c = UserInfo.getUserName();            
            insert oTask;
            System.assertEquals(oTask.WhatId,oCase.Id);
           
            
            Interaction__c oInteraction = HUMTestDataHelperTest.getInteraction();
            oInteraction.Interacting_With__c = oAccount.id;
            oInteraction.Interacting_With_type__c = 'Provider' ;
            oInteraction.Caller_Name__c = '111';
            insert oInteraction;
            System.assertEquals(oInteraction.Interacting_With_type__c, 'Provider');
        
            Interaction_Member__c oIntMem = HUMTestDataHelperTest.getInteractionMember(); 
            oIntMem.Interaction__c = oInteraction.id;
            oIntMem.Interacting_About__c = oAccount.id;
            oIntMem.Interacting_About_Type__c = 'Provider';
            insert oIntMem; 
            System.assertEquals(oIntMem.Interacting_About__c, oAccount.id);
                         
        }
    }

     /**
    * testPopulateQueues1
    *<p>
    * Used to run positive scenerios of populateSF
    * @param            NULL
    * @return           void
    */ 
    @isTest
    static void testPopulateQueues1()
    {
        User oCCSUser = [Select id,UserName,LastName from User where LastName = 'test QSelUser1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.LastName,'test QSelUser1');
        
        System.runAs(oCCSUser)
        {
            List<Work_Queue_Setup__c> wqLst = [Select id, Name, Public_Group_Name__c, Classification_Type__r.Mentor_Function__c, Classification_Type__r.Name FROM Work_Queue_Setup__c WHERE Public_Group_Name__c = 'Group one'];
            system.assertEquals(wqLst[0].Public_Group_Name__c,'Group one');
            system.assertEquals(wqLst[0].Classification_Type__r.Mentor_Function__c, 'Contact Center RSO');
            Test.startTest();
            HUMQueueSelection_LC.init();
            HUMQueueSelection_LC.saveQueue('Group one');
            Test.stopTest();
        }
    }

     /**
    * testPopulateQueues2
    *<p>
    * Used to populate of Queues
    * @param            NULL
    * @return           void
    */ 
    @isTest
    static void testPopulateQueues2()
    {
        User oCCSUser = [Select id,UserName,LastName from User where LastName = 'test QSelUser1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.LastName,'test QSelUser1');

        Group GrpUser = [Select id, name from Group where name = 'Group one'];
        GroupMember GrpMemUser = [Select id from GroupMember where groupid =: GrpUser.id AND UserOrGroupId =: oCCSUser.id];
        delete GrpMemUser;

        System.runAs(oCCSUser)
        {
            List<Work_Queue_Setup__c> wqLst = [Select id, Name, Public_Group_Name__c, Classification_Type__r.Mentor_Function__c, Classification_Type__r.Name FROM Work_Queue_Setup__c WHERE Public_Group_Name__c = 'Group one'];
            system.assertEquals(wqLst[0].Public_Group_Name__c,'Group one');
            system.assertEquals(wqLst[0].Classification_Type__r.Mentor_Function__c, 'Contact Center RSO');
            Test.startTest();
            HUMQueueSelection_LC.init();
            HUMQueueSelection_LC.saveQueue('Group one');
            Test.stopTest();
        }
    }

    /**
    * testPopulateQueues3
    *<p>
    * Used to populate of Queues
    * @param            NULL
    * @return           void
    */ 
    @isTest
    static void testPopulateQueues3()
    {
        User oCCSUser = [Select id,UserName,LastName from User where LastName = 'test QSelUser1' LIMIT 1 ] ;

        User stdUser = [Select id,UserName,LastName from User where LastName = 'test QselStdUser' LIMIT 1 ] ;

        system.assertEquals(oCCSUser.LastName,'test QSelUser1');

        Group GrpUser = [Select id, name from Group where name = 'Group one'];
        GroupMember GrpMemUser = [Select id from GroupMember where groupid =: GrpUser.id AND UserOrGroupId =: oCCSUser.id];
        delete GrpMemUser;

        System.runAs(oCCSUser)
        {
            List<Work_Queue_Setup__c> wqLst = [Select id, Name, Public_Group_Name__c, Classification_Type__r.Mentor_Function__c, Classification_Type__r.Name FROM Work_Queue_Setup__c WHERE Public_Group_Name__c = 'Group one'];
            system.assertEquals(wqLst[0].Public_Group_Name__c,'Group one');
            system.assertEquals(wqLst[0].Classification_Type__r.Mentor_Function__c, 'Contact Center RSO');
            Test.startTest();
            HUMQueueSelection_LC.init();
            Test.stopTest();
        }

        System.runAs(stdUser){
            HUMQueueSelection_LC.saveQueue('Group one');
        }
    }
}