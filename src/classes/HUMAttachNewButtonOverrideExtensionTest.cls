/****************************************************************************
Apex Class Name  : HUMAttachNewButtonOverrideExtensionTest 
Version          : 1.0
Created On       : JULY 28 2014
Function         : Test class to HUMAttachNewButtonOverrideExtension Apex class

Modification Log :
*    Developer                 CodeReview            Date                 Description
* ------------------------------------------------------------------------------------------------------------------------------
*    Suryakumari Medicherla    17093                 28/07/2014             Original Version
*    Suryakumari Medicherla    17717                 05/09/2014             Code added to fix Defect :153752
*    Manish Kumar              17802                 09/09/2014             Test Execution failure fixes in TEST 
*    SuryaKumari Medicherla    18038                 09/19/2014             Added code cover the code added for Removed SQL for custom settings with Map.
*    SuryaKumari Medicherla    20576                 01/28/2015             Code Modified to fix test class failure Defect # 164139 
*    Kritika Popat             22388                 04/16/2015             Code modified for adding Task Type value to fix test class failure Defect#179884.0001 and new validation rule
*    Avinash Choubey           33390                 07/11/2016             Changes made as part of REQ- 255151
*    Santhi Mandava                                  03/12/2019             Removed "DpaaSWebServiceMockImpl" usage.
**********************************************************************************************************************************/

@isTest
private class HUMAttachNewButtonOverrideExtensionTest 
{
    
     /*
     * This method is used to insert the data only once and called automicticaly in all methods
     * @param     NA
     * @return    void
     */
    @testSetup static void setup() {
    
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        system.assertEquals(oUser.lastname=='test User1',true);
       
        System.runAs (oUser) 
        {
            Account oAccount = HUMTestDataHelperTest.getAccount();
            oAccount.Name = 'New Case Account';
            oAccount.Group_Number__c  = '123';
            oAccount.BillingState = 'New York';
            oAccount.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
            insert oAccount;
            System.assert(oAccount.Name == 'New Case Account');
            
            Product2 oProduct = HUMTestDataHelperTest.getProduct();
            oProduct.Name='Mediclaim';
            insert oProduct;
            system.assert(oProduct.Name =='Mediclaim');
            
            Policy__c oPolicy = HUMTestDataHelperTest.getPolicy();
            oPolicy.Name = 'test123';
            oPolicy.Group_Name__c = oAccount.Id;
            oPolicy.Funding_Type_Code__c = 'Y';
            oPolicy.Product__c = oProduct.Id;
            oPolicy.Issue_State__c = 'IL'; 
            insert oPolicy;
            System.assert(oPolicy.Name == 'test123');
            
            Policy_Member__c oPolicyMem = HUMTestDataHelperTest.getPolicyMember();
            oPolicyMem.Name = '12345';
            oPolicyMem.Member__c = oAccount.Id;
            oPolicyMem.Policy__c = oPolicy.Id;            
            oPolicyMem.Relationship__c = 'Self';
            insert oPolicyMem;
            System.assert(oPolicyMem.Name == '12345');
            
            HUMSalesforceFieldIds__c oSalesForceIds = new HUMSalesforceFieldIds__c ();
            oSalesForceIds.Name = 'id';
            oSalesForceIds.Case_in_Case_Interaction__c = '00NL00000035Rah';
            oSalesForceIds.RelatedTo_Case_in_Attachment__c = '00NL00000036quk';
            oSalesForceIds.RelatedTo_Task_in_Attachment__c = '00NL000000371yT';
            insert oSalesForceIds;
            System.assert(oSalesForceIds.Name == 'id');
            
            Case oCase = new Case(AccountId=oAccount.Id, Policy_Member__c= oPolicyMem.Id ,
                             Interacting_With_Type__c = 'Member',Interacting_With__c=oAccount.Id, Due_Date__c=Date.Today(), 
                             Case_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU', Work_Queue_View_Name__c='Cincinati CRU',Service_Center__c ='Cincinnati',Department__c='CRU', status='In Progress');
            oCase.RecordTypeId = Schema.sObjectType.Case.getRecordTypeInfosByName().get('Group Case').getRecordTypeId();
            insert oCase;
            System.assert(oCase.Interacting_With_Type__c == 'Member');
            
            Task oTask = HUMTestDataHelperTest.getTaskRecord();
            oTask.WhatId = oCase.Id;
            oTask.Task_Owner__c = UserInfo.getUserName();
            oTask.Owner_Queue__c = UserInfo.getUserName();            
            oTask.status = 'In Progress';
            oTask.Type='Work Task';
            insert oTask;
            System.assert(oTask.Type == 'Work Task');
        }        
    }
    
    /*
    * Test method to create Attachment from Case detail page
    * @param     NA
    * @return    void
    */
    static testMethod void createAttachfromCase() 
    {
        User testUser = [Select Id, Name,lastname from User where LastName='test User1'];
        system.assertEquals(testUser.lastname=='test User1',true);        
        system.runAs(testUser)
        {               
            Test.startTest();
            Case oCase = [select AccountId, Interacting_About_Type__c , Origin , 
                           Type , Intent_ID__c , Classification_ID__c , Intent_ID__r.name , Classification_ID__r.name ,
                           Policy_Member__c, Interacting_With_Type__c,
                           Interacting_With__c, Due_Date__c, Classification_Type__c ,
                           Priority , Line_of_Business__c , G_A_Rights_Given__c , 
                           G_A_Reason__c, Complaint__c , Complaint_Reason__c ,
                           Behavioral_Health__c ,status,RecordTypeId,recordType.developerName from Case where status = 'In Progress' limit 1];
            System.assert(oCase.status == 'In Progress');  
            Task oTask = [select id ,WhatId,Owner_Queue__c,status,Type from Task where Type = 'Work Task' limit 1];
            System.assert(oTask.Type == 'Work Task');
            ApexPages.StandardController ostdController = new ApexPages.StandardController(oCase);
            HUMAttachmentNewButtonOverrideExtension oextension = new  HUMAttachmentNewButtonOverrideExtension(ostdController);
            HUMAttachmentNewButtonOverrideExtension oextension2 = new  HUMAttachmentNewButtonOverrideExtension(ostdController); 
            ApexPages.currentPage().getParameters().put('CF00Nc00000014ZwJ_lkid', oCase.id);
             ApexPages.currentPage().getParameters().put('attCurrentSubTabIdParam', '00NL00000036quk');
            oextension.reDirectPage();
            String snextPage;
            if(oextension.reDirectPage() != NULL)    snextPage = oextension.reDirectPage().getUrl();
            
            ApexPages.currentPage().getParameters().put('CF00Nc00000014ZwJ_lkid', '');
            ApexPages.currentPage().getParameters().put('taskID', oTask.id);
            oextension.reDirectPage();
            snextPage = oextension.reDirectPage().getUrl();
            
            testUser.current_queue__c = 'queue1' ;
            update testUser;
            System.assert(testUser.current_queue__c == 'queue1');
            
            ApexPages.currentPage().getParameters().put('caseID', oCase.id);
            System.assertEquals(ApexPages.currentPage().getParameters().get('caseID'), oCase.id);
            oextension.reDirectPage();
            oextension.assignValuesForCase(NULL);
            snextPage = oextension.reDirectPage().getUrl();
            
            ApexPages.currentPage().getParameters().put('caseID', '');
            System.assertEquals(ApexPages.currentPage().getParameters().get('caseID'), '');
            ApexPages.currentPage().getParameters().put('taskID', oTask.id);
            oextension.reDirectPage();
            oextension.assignValuesForCase(NULL);
            System.assertEquals(ApexPages.currentPage().getParameters().get('taskID'), oTask.id);
            
            Test.stopTest();     
        }
    }
    
    /*
    * Test method to create Attachment from Task detail page.
    * @param     NA
    * @return    void
    */
    static testMethod void createAttachfromTask() 
    {
        User testUser = [Select Id, Name,lastname from User where LastName='test User1'];
        system.assertEquals(testUser.lastname=='test User1',true);
        
        system.runAs(testUser)
        {               
            Test.startTest();
            Case oCase = [select AccountId, Interacting_About_Type__c , Origin , 
                           Type , Intent_ID__c , Classification_ID__c , Intent_ID__r.name , Classification_ID__r.name ,
                           Policy_Member__c, Interacting_With_Type__c,
                           Interacting_With__c, Due_Date__c, Classification_Type__c ,
                           Priority , Line_of_Business__c , G_A_Rights_Given__c , 
                           G_A_Reason__c, Complaint__c , Complaint_Reason__c ,
                           Behavioral_Health__c ,status,RecordTypeId,recordType.developerName from Case where status = 'In Progress' limit 1];
            System.assert(oCase.status == 'In Progress');  
            Task oTask =[select id ,WhatId,Owner_Queue__c,status,Type from Task where Type = 'Work Task' limit 1];
            System.assert(oTask.Type == 'Work Task');
            ApexPages.StandardController ostdController = new ApexPages.StandardController(oCase);
            HUMAttachmentNewButtonOverrideExtension oextension = new  HUMAttachmentNewButtonOverrideExtension(ostdController);
            ApexPages.currentPage().getParameters().put('taskID', oTask.id);
            oextension.reDirectPage();
            String snextPage = oextension.reDirectPage().getUrl();
            System.assert(snextPage != null);
            
            testUser.current_queue__c = 'queue1' ;
            update testUser;
            System.assert(testUser.current_queue__c == 'queue1');            
            ApexPages.currentPage().getParameters().put('taskID', oTask.id);
            oextension.reDirectPage();
            System.assertEquals(ApexPages.currentPage().getParameters().get('taskID'), oTask.id);
            snextPage = oextension.reDirectPage().getUrl();
            
            Test.stopTest();     
        }
    }
}