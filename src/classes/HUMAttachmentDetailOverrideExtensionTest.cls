/*****************************************************************************************
Apex Class Name :  HUMAttachmentDetailOverrideExtensionTest
Version: 1.0
Function           : This Is test class for HUMAttachmentDetailOverrideExtension
Created Date       :  25 Mar 2015

Modification Log: 
*   Developer                    Code Review         Date               Description
* ------------------------------------------------------------------------------------------------------------------------------  
* Tanushree Hazari                21906            03/25/2015            Original version
* Avinash Choubey                 33390            07/11/2016            Chnages made as part of REQ- 255151
* Shivam Anand                    140629           10/18/2017            REQ - 332334: Code Coverage Fix 
*****************************************************************************************************************************/
@isTest
Private Class HUMAttachmentDetailOverrideExtensionTest
{   
    
     /*
     * This method Is used to insert the data only once And called automicticaly in all methods
     * @param        NA
     * @return       void
     */
    @testSetup static void setup() {
    
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        system.assertEquals(oUser.lastname=='test User1',true);
        
        System.runAs (oUser) 
        {
          Account oAcc = HUMTestDataHelperTest.getAccount(); 
            oAcc.Name='TestAccounoTask';
            oAcc.Source_Platform_Code__c = 'CA';
            oAcc.Tenant_Id__c = '00';
            oAcc.General_Account__c = true;
            insert oAcc;                
            system.assert( oAcc.Name == 'TestAccounoTask');
            
            List<HUMConstants__c> lstConstants = New List<HUMConstants__c>();
            lstConstants.add(New HUMConstants__c(Name='HUMTask',StringValue__c='Task'));
            lstConstants.add(New HUMConstants__c(Name='HUMCase',StringValue__c='Case'));
            lstConstants.add(New HUMConstants__c(Name='HUMClosed',StringValue__c='Closed'));
            lstConstants.add(New HUMConstants__c(Name='HUMSlash',StringValue__c='/'));
            lstConstants.add(New HUMConstants__c(Name='HUMURL',StringValue__c='?nooverride=1'));
            insert lstConstants;
            System.assert(lstConstants[0].Name == 'HUMTask');
            
            Case oCase = New Case(AccountId = oAcc.Id, General_Case__c = True,
                               Interacting_With_Type__c = 'Member', Interacting_With__c = oAcc.Id, Due_Date__c = Date.Today(),
                               Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c = 'Cincinnati CRU', Work_Queue_View_Name__c = 'Cincinati CRU', 
                               Service_Center__c = 'Cincinnati', Department__c = 'CRU', status = 'In Progress');
            insert oCase;               
            system.assert(oCase.status == 'In Progress');             
            
            Task oTask = New Task(WhatId = oCase.id, Task_Owner__c = UserInfo.getUserName(), Owner_Queue__c = 'Cincinnati CRU',
                               Work_Queue_View_Name__c = 'Cincinati CRU', Service_Center__c = 'Cincinnati',Department__c = 'CRU', status = 'In Progress', ActivityDate = Date.Today());
            insert oTask; 
            system.assert(oTask.status == 'In Progress'); 
        }
    }
        
     /*
     * Used to create wrapper class data
     * @param        NA
     * @return       void
     */
    Static testMethod void prepareTestData() 
    {
       User oUser = [SELECT Id, lastname FROM User  WHERE Lastname='test User1' LIMIT 1];
       system.assertEquals(oUser.lastname=='test User1',true);
         
        System.runAs (oUser) 
        {   
           Case oCase = [select AccountId, Interacting_About_Type__c , Origin , 
                           Type , Intent_ID__c , Classification_ID__c , Intent_ID__r.name , Classification_ID__r.name ,
                           Policy_Member__c, Interacting_With_Type__c,
                           Interacting_With__c, Due_Date__c, Classification_Type__c ,
                           Priority , Line_of_Business__c , G_A_Rights_Given__c , 
                           G_A_Reason__c, Complaint__c , Complaint_Reason__c ,
                           Behavioral_Health__c ,status,RecordTypeId,recordType.developerName from Case where status = 'In Progress' limit 1];
            System.assert(oCase.status == 'In Progress');  
            Task oTask = [select id , WhatId, Owner_Queue__c, status, Type from Task where status = 'In Progress' limit 1];
            system.assert(oTask.status == 'In Progress'); 
                 
            ApexPages.currentPage().getParameters().put('id', oTask.id);
            
        
            Attachment__c objAttach = New Attachment__c();
            objAttach.Name='Attachment1';
            objAttach.Related_To_Task__c= oTask.Id;
            objAttach.Related_To_Case__c=oCase.Id;
            insert objAttach;
            system.assert( objAttach.Name =='Attachment1');
        
            Note oNote = New Note();
            oNote.ParentID =objAttach.Id;
            oNote.Title='Note1';
            insert oNote;
            System.assert( oNote.Title =='Note1');
         
            Attachment__c objAttach1 = [SELECT Name, Related_To__c, Related_To_Task__c, Related_To_Case__c FROM Attachment__c where Name = 'Attachment1'];
            System.assertNotEquals(objAttach.Id,null);
            
            PageReference ref = New PageReference('/apex/HUMAttachmentDetailOverride'); 
            Test.setCurrentPage(ref);
            
            HUMAttachmentDetailOverrideExtension oExtension = New HUMAttachmentDetailOverrideExtension(New ApexPages.StandardController(objAttach1));
            Test.startTest();
                
                    PageReference pageRef = oExtension.onPageLoad();
                    String sURL = '/' + objAttach.id + '?nooverride=1';
                    System.assertnotEquals(sURL, null);
                    System.assertEquals(sURL,pageRef.getUrl());
             
            oExtension.sortList();
            Test.stopTest(); 
        }
    } 
    
      
    
    
    
    Static testMethod void runNegativeData()
    {   
        User oCCSUser = [Select id from User where LastName = 'test User1' LIMIT 1 ] ;
        System.runAs(oCCSUser)
        {
            Attachment__c oAttach = New Attachment__c();
            System.assertNotEquals(oAttach,null);
            System.assertEquals(oAttach.Name,null);
            
            PageReference ref = New PageReference('/apex/HUMAttachmentDetailOverride'); 
            Test.setCurrentPage(ref);
                
            HUMAttachmentDetailOverrideExtension oExtension = New HUMAttachmentDetailOverrideExtension(New ApexPages.StandardController(oAttach));
                Test.startTest();
                    PageReference pageRef = oExtension.onPageLoad();
                    System.assertEquals(null,pageRef);
                Test.stopTest(); 
        }
    }
}