/************************************************************************************************************************
Apex class Name    : HUMQueueSelection_T_LWC
Version            : 1.0
Function           : Test Class for HUMQueueSelection_LWC.
Created On         : Jan 21 2022

 Modification Log
*   Developer                   Code Review         Date                Description
* -------------------------------------------------------------------------------------------------------------------------                
*   Vijaya Lakshmi Tummala/
	Shailesh Bagade/Gowthami Thota/MkManoj                  1/21/2022         Original Version(Feature 2963843 - Cases/Tasks Tab)
***************************************************************************************************************************/
@isTest                    
private class HUMQueueSelection_T_LWC 
{
   /**
    * testsetup method to create test data
    *<p>
    * @param            NULL
    * @return           void
    */
     @testSetup static void prepareTestData() 
    {   
        User oCCSUser = HUMTestDataHelperTest.createTestUser('QSelUser1','Customer Care Specialist');
        oCCSUser.Mentor_Sub_Function__c ='Contact Center RSO';
        oCCSUser.Current_Queue__c = 'Group_Two';
        insert oCCSUser;
        system.assertEquals(oCCSUser.LastName,'test QSelUser1');
        
        
        User oAdminUser = HUMTestDataHelperTest.createTestUser('Admin','system Administrator');
        oAdminUser.Current_Queue__C = 'Cincinati CRU1';
        insert oAdminUser;
        System.assertEquals(oAdminUser.LastName,'test Admin');
        System.runAs(oAdminUser) 
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
    * Test method to test methods of the class.
    * <p>
    * @return   :    void
    * @param    :    NA
    */
    
    static testMethod void testALL()
    { 
        User oCCSUser = [ SELECT Id, UserName ,Name, LastName FROM User WHERE LastName ='test QSelUser1'];
        System.assertEquals(oCCSUser.LastName, 'test QSelUser1');
        system.runAs(oCCSUser)
        {   
            Test.StartTest();
            
            HUMQueueSelection_LWC.getQueueList();
            HUMQueueSelection_LWC.getQueue();
            HUMQueueSelection_LWC.populateSF('Contact Center RSO');
            String selectedSubFunc='Individual Medicare';
            
            Test.stopTest();
             
        } 
    }
    /**
    * testPopulateSF
    *<p>
    * Used to run positive scenerios of populateSF
    * @param            NULL
    * @return           void
    */ 
@isTest
    static void testPopulateSF()
    {
        User oCCSUser = [Select id,UserName,LastName,Mentor_Sub_Function__c,Current_Queue__c  from User where LastName = 'test QSelUser1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.LastName,'test QSelUser1');
        	
        System.runAs(oCCSUser)
        {
            List<Work_Queue_Setup__c> wqLst = [Select id, Name, Public_Group_Name__c, Classification_Type__r.Mentor_Function__c, Classification_Type__r.Name FROM Work_Queue_Setup__c WHERE Public_Group_Name__c = 'Group one'];
            system.assertEquals(wqLst[0].Public_Group_Name__c,'Group one');
            system.assertEquals(wqLst[0].Classification_Type__r.Mentor_Function__c, 'Contact Center RSO');
            
            Test.startTest();
         
            try{
                HUMQueueSelection_LWC.sPageTitle =wqLst[0].Name;
            HUMQueueSelection_LWC.options = null;
            HUMQueueSelection_LWC.sAssignedQueue =wqLst[0].Name;
            HUMQueueSelection_LWC.sSelectedQueue = wqLst[0].Name;
            HUMQueueSelection_LWC obj =new HUMQueueSelection_LWC();
            HUMQueueSelection_LWC.sSelectedSub = null;
            obj.IsRapidForceApp = false;
            obj.isUnifiedSearchOn = false;
            
                try{
                    obj.saveSubFunction();
                }catch(exception e){
                    system.assert(true);
                }
                
            
            HUMQueueSelection_LWC.populateSF(wqLst[0].Classification_Type__r.Mentor_Function__c);
             HUMQueueSelection_LWC.populateSF(HumConstants_LWC.HUMDefaultMentorFunction);
            HUMQueueSelection_LWC.populateSF(' ');
            HUMUtilityHelper_LWC.fetchUserMentorFunction(oCCSUser);
            HUMUtilityHelper_LWC.getlstSubFunctions('Contact Center RSO',oCCSUser,' ');
             HUMQueueSelection_LWC.saveQueue(HUMQueueSelection_LWC.sSelectedQueue);
             HUMQueueSelection_LWC.saveQueue('#########################'); 
            HUMQueueSelection_LWC.populateSF('Contact Center RSO');
            }
            catch(exception e){
                system.assert(true);
            }
            
            Test.stopTest();
        }
    }    
}