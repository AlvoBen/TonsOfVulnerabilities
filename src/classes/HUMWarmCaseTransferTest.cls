/*****************************************************************************************
Apex class Name    : HUMWarmCaseTransferTest
Version            : 1.0
Function           : Test Class for HUMWarmCaseTransfer                   
Created Date       : 22 Sept 2016


Modification Log:
 *   Developer                   Code Review             Date               Description
 * -------------------------------------------------------------------------------------------------------------------------------                
 *  Shikhar Mehta                 40792                22/09/2016          Original Version
 *  Shikhar Mehta                                      05/10/2016          Req - 282376
 *  Vamsi Kondragunta             49555                11/29/2016          Defect fix for 249973 and 250228
 *  Pradeepkumar Dani             99570                06/08/2017          Intent__c.Classification__c & Intent__c.Etl_Idtfr__c Clean up
 *  Pradeepkumar Dani            100996                06/13/2017          Classification__c.Classification_Type__c & Etl_Idftr__c Clean Up
 *  Prudhvi Pamarthi              122198               08/23/2017          Fix for REQ-326278 work queue view name is blank after warm case transfer  
 *  Jasmeen Shangari								   07/31/2023		   Fixed Test Class Failure 
************************************************************************************************************************/

@isTest
private class HUMWarmCaseTransferTest
{   
    
    @testSetup static void setup() 
    {
        User oCurrentUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        oCurrentUser.Current_Queue__c = 'Cincinnati CRU1';
        insert oCurrentUser;
        system.assertEquals( oCurrentUser.Current_Queue__c,'Cincinnati CRU1');
        
        Group oGrp=new Group(Name='Cincinnati_CRU1',DeveloperName='Cincinnati_CRU1', Type='QUEUE');
        insert oGrp;
        system.assertEquals(oGrp.Name,'Cincinnati_CRU1');
        
        GroupMember gm= new GroupMember(); 
        gm.GroupId=oGrp.id;
        gm.UserOrGroupId = oCurrentUser.id;
        insert gm;
        User oUser = [Select Id, Name, email from User where Id=:gm.UserOrGroupId LIMIT 1];
        system.assertEquals(oUser.Name,'test User1');
        
        QueuesObject oQueueObject = new QueuesObject (QueueID = oGrp.id, SobjectType ='Work_Queue_Setup__c');
        insert oQueueObject ;   
        system.assertEquals(oQueueObject.SobjectType, 'Work_Queue_Setup__c');
        
        System.runAs(oCurrentUser)
        {
            Account oAcc = HUMTestDataHelperTest.getAccount(); 
            oAcc.Name='TestAccount1';
            oAcc.Source_Platform_Code__c = 'CA';
            oAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
            insert oAcc;
            system.assertEquals(oAcc.Name,'TestAccount1');
            
            Work_Queue_Setup__c oWQS = new Work_Queue_Setup__c(Name=oGrp.Name);
            oWQS.Service_Center__c ='Cincinnati';
            oWQS.Department__c ='CRU1';
            oWQS.General__c =true;
            oWQS.HO_Medical__c =true;
            oWQS.TenantID__c ='00';             
            insert oWQS;
            system.assertEquals(oWQS.TenantID__c ,'00');
            
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
            System.assertEquals(intentRec.Name, 'Filling');

            Case oCase = new Case(AccountId=oAcc.Id, Intent_Id__c = lstIntent[0].id, General_Case__c =true, Classification_Id__c =lstClassification[0].id,
                                  Interacting_About_Type__c = 'Group',Interacting_With_Type__c = 'Group', Interacting_With__c=oAcc.Id, Due_Date__c=Date.Today(),
                                  Case_Owner__c=UserInfo.getUserName(), Owner_Queue__c='TestAccount1', 
                                  Service_Center__c ='Cincinnati', Department__c='CRU1', status='Pending Transfer');
            oCase.Complaint__c = 'Yes'                   ;
            oCase.Complaint_Reason__c = 'Claims';
            oCase.Classification_Type__c = 'Calls';
            oCase.recordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Provider Case').getRecordTypeId();
            oCase.G_A_Rights_Given__c ='No';oCase.User__c= oCurrentUser.id; oCase.Transferred_From__c=oCurrentUser.id; oCase.Warm_Case_Transfer_Action__c = null;oCase.Transferred_Date__c =system.now();
            insert oCase;                                             
        }
    }
    
    private static testMethod void overallTestMethod()
    {   
        
        User oCurrentUser = [SELECT id,Current_Queue__c, LastName from User WHERE  Lastname = 'test User1' LIMIT 1];
        System.runAs(oCurrentUser)
        {       
            Account oAcc = [SELECT id, Name, Source_Platform_Code__c from Account WHERE Name='TestAccount1' LIMIT 1 ];          
            Group oGrp= [SELECT id, Name from Group WHERE Name='Cincinnati_CRU1'];
            QueuesObject oQueueObject = [SELECT id, QueueID, SobjectType  from QueuesObject WHERE QueueID = :oGrp.id limit 1];
            Work_Queue_Setup__c oWQS = [SELECT id, Name, Service_Center__c, Department__c, General__c, TenantID__c, HO_Medical__c from Work_Queue_Setup__c WHERE Name=:oGrp.Name limit 1];
            oCurrentUser.Current_Queue__c = oWQS.Name;
            update oCurrentUser;
            system.assertEquals(oCurrentUser.Current_Queue__c,oWQS.Name);
            Work_Queue_Setup__c lstWorkQueueSetup = [select Department__c, Service_Center__c from Work_Queue_Setup__c where name = :oCurrentUser.Current_Queue__c];
            system.assertEquals(lstWorkQueueSetup.Service_Center__c,oWQS.Service_Center__c);
            system.assertEquals(lstWorkQueueSetup.Department__c,oWQS.Department__c);
            
            Case oCase = [Select Id,Warm_Case_Transfer_Action__c,Case_Owner__c,Status,CaseNumber, User__r.Name,OwnerId, Transferred_From__r.Name, Service_Center__c, Department__c from Case where User__c=:oCurrentUser.id AND Warm_Case_Transfer_Action__c = null LIMIT 1];
            
            oCase.Service_Center__c = lstWorkQueueSetup.Service_Center__c;
            oCase.Department__c =  lstWorkQueueSetup.Department__c;                          
            update oCase;          
            ApexPages.currentPage().getParameters().put('id', oCase.id);
            HUMWarmCaseTransfer objHandler = new HUMWarmCaseTransfer();   
            test.startTest(); 
            objHandler.updCaseId=oCase.Id;
            objHandler.updCaseOwnerName=oCase.User__r.Name;
            objHandler.Cancel();           
            objHandler.Reject();
            HUMWarmCaseTransfer.updateCaseRecord(oCase.Id,oCase.User__r.Name);
            objHandler.getCases();
            
            test.stopTest();
        }
    }
}