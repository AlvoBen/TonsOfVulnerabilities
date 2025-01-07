/*****************************************************************************************
Apex Class Name :   HUMWarmCaseTransferBatchTest
Version: 1.0
Function           : Test Class for HUMWarmCaseTransferBatch Job                  
Created Date       


Modification Log:  
 *   Developer                   Code Review             Date               Description
 * -------------------------------------------------------------------------------------------------------------------------------                
 *   Shikhar Mehta                                     05/10/2016            Req - 282376
 *   Pradeepkumar Dani           99570                 06/08/2017            Intent__c.Classification__c & Intent__c.Etl_Idtfr__c Clean up
 *   Pradeepkumar Dani           100996                06/13/2017            Classification__c.Classification_Type__c & Etl_Idftr__c Clean Up
 *   Shreya Choodamani                                 10/11/2017            REQ-332390 : Updated the test class to increase the coverage 
 *   Pooja Kumbhar                                     04/29/2020            Checkmarx Test method without assert error fix
 *   Jasmeen Shangari								   07/31/2023			 Fixed Test Class Failure
 *  ********************************************************************************************************************************************/
 
 @isTest
Private Class HUMWarmCaseTransferBatchTest
{   
    @testSetup static void setup() 
    {
         User oCurrentUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
         oCurrentUser.Current_Queue__c = 'Cincinnati CRU1';
         insert oCurrentUser;
         System.assertEquals( oCurrentUser.Current_Queue__c,'Cincinnati CRU1');
         
         Group oGrp = New Group(Name ='Cincinnati_CRU1',DeveloperName='Cincinnati_CRU1', Type='QUEUE');
         insert oGrp;
         System.assertEquals(oGrp.Name,'Cincinnati_CRU1');
         
         GroupMember gm = New GroupMember(); 
         gm.GroupId=oGrp.id;
         gm.UserOrGroupId = oCurrentUser.id;
         insert gm;
         User oUser = [Select Id, Name, email from User where Id=:gm.UserOrGroupId LIMIT 1];
         System.assertEquals(oUser.Name,'test User1');
         
         QueuesObject oQueueObject = New QueuesObject(QueueID = oGrp.id, SobjectType ='Work_Queue_Setup__c');
         insert oQueueObject ;   
         System.assertEquals(oQueueObject.SobjectType, 'Work_Queue_Setup__c');
         
         System.runAs(oCurrentUser)
         {
             Account oAcc = HUMTestDataHelperTest.getAccount(); 
             oAcc.Name='TestAccount1';
             oAcc.Source_Platform_Code__c = 'CA';
             oAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
             insert oAcc;
             System.assertEquals(oAcc.Name,'TestAccount1');
             
             Work_Queue_Setup__c oWQS = New Work_Queue_Setup__c(Name = oGrp.Name);
             oWQS.Service_Center__c ='Cincinnati';
             oWQS.Department__c ='CRU1';
             oWQS.General__c =true;
             oWQS.HO_Medical__c =true;
             oWQS.TenantID__c ='00';             
             insert oWQS;
             System.assertEquals(oWQS.TenantID__c ,'00');
             
             List<Classification__c> lstClassification = New List<Classification__c>();
             Classification__c classificationRec = New Classification__c();
             classificationRec.Name = 'Claims';
             classificationRec.Is_Active__c = true;
             lstClassification.add(classificationRec);          
             insert lstClassification;
             System.assertEquals(lstClassification[0].Name=='Claims',true);
            
            List<Intent__c> lstIntent = New List<Intent__c>();
            Intent__c intentRec = New Intent__c();
            intentRec.Name = 'Filling';
            intentRec.Is_Active__c = true;
            lstIntent.add(intentRec);
            insert lstIntent;
            System.assertEquals(intentRec.Name, 'Filling');

            Case oCase = New Case(AccountId = oAcc.Id, Intent_Id__c = lstIntent[0].id, General_Case__c = True, Classification_Id__c = lstClassification[0].id,
                                   Interacting_About_Type__c = 'Group',Interacting_With_Type__c = 'Group', Interacting_With__c=oAcc.Id, Due_Date__c=Date.Today(),
                                   Case_Owner__c=UserInfo.getUserName(), Owner_Queue__c='TestAccount1', 
                                   Service_Center__c ='Cincinnati', Department__c='CRU1', status='Pending-Transfer');
                                   oCase.Complaint__c = 'Yes'                   ;
                                   oCase.Complaint_Reason__c = 'Claims';
                                   oCase.Classification_Type__c = 'Calls';
                                   oCase.recordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Provider Case').getRecordTypeId();
                                   oCase.G_A_Rights_Given__c ='No';oCase.User__c= oCurrentUser.id; oCase.Transferred_From__c=oCurrentUser.id; oCase.Warm_Case_Transfer_Action__c = null;oCase.Transferred_Date__c=System.now().addHours(-24);
             insert oCase;                                             
            }
    }
    
    /*
    * Method Name : overallTestMet 
    * Description : Test method To test all the methods In the parent Class    
    * Return type : void     
    * Paramater   : NA  
    */
    Private Static testMethod void overallTestMet()
    {
        User oCurrentUser = [Select Id, Name, Current_Queue__C, LastName from User where LastName= 'test User1'];
        System.assertEquals(oCurrentUser.LastName, 'test User1');
        System.runAs(oCurrentUser)
        {
            Test.startTest();
            Database.QueryLocator QL;
            Database.BatchableContext BC;
            String schedule = '0 0 23 * * ?';
            DateTime hrBefore = System.Now().addHours(-24); 
            List<Case> lstCase = New List<Case>();
            lstCase=[Select Id,Warm_Case_Transfer_Action__c,Case_Owner__c,Status,CaseNumber, User__r.Name,OwnerId, Transferred_From__r.Name from Case where Warm_Case_Transfer_Action__c = null And Transferred_Date__c <=:hrBefore And Status ='Pending-Transfer'];
            HUMWarmCaseTransferBatch objClass = New HUMWarmCaseTransferBatch();
            QL = objClass.start(bc);
            Database.QueryLocatorIterator QIT =  QL.iterator();
            While (QIT.hasNext())
            {
                Case oCase = (Case)QIT.next();            
                lstCase.add(oCase);
            }        
        
            objClass.execute(BC, lstCase);
            objClass.finish(BC);
            System.schedule('Testing Schedule', schedule, objClass);
            Test.stopTest();
        }
    }
}