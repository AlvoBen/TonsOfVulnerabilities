/****************************************************************************
Apex Class Name  : HUMCaseCloneOverrideExtensionTest
Version          : 1.0
Created On       : OCTOBER 02 2014
Function         : Test class to HUMCaseCloneOverrideExtension Apex class

Modification Log :
*    Developer                 CodeReview            Date                 Description
* ------------------------------------------------------------------------------------------------------------------------------
*    Manuel Johnson               18329            10/02/2014             Original Version
*    Shruti Poundarik             18329            10/03/2014             Modified test class
*    SuryaKumari Medicherla       19911            12/10/2014             Code modified to replace custom settings with custom object (REQ-72809)
*    Santhi Mandava               20576            01/28/2015             Code Modified to fix test class failure Defect # 164139   
*    Reena Rajan                  21065            02/20/2015             Code Modified to cover validation rules created for REQ-96597
*    SuryaKumari Medicherla       29930            09/28/2015             Code modified to increase code coverage
*    Avinash Choubey              30485            03/15/2016             Code modified to increase code coverage as per defect# 219161 fix
*    Avinash Choubey              33344            07/11/2016             Chnages made as part of REQ- 255151
*    Sharan Shanmugam                              11/21/2016             Changes made to increase the code coverage 
*    Sharan Shanmugam                              02/07/2017             Code modified as part of REQ - 296936 
*    Pallavi Ravishankar          80484            03/30/2017             Code modified as per new cict model - REQ 306360
*    Sumit Sharma                 89120            05/03/2017             Code modified as per new - REQ -307653
*    Pradeepkumar Dani            99570            06/08/2017             Intent__c.Classification__c & Intent__c.Etl_Idtfr__c Clean up
*    Pradeepkumar Dani            100996           06/13/2017             Classification__c.Classification_Type__c & Etl_Idftr__c Clean Up
*    Pallavi Ravishankar                           02/09/2018             REQ - 352481 - Cloning appeal Cases 
*    Pallavi Ravishankar                           05/30/2018             REQ 363576 - Rollback Resolution Clone Case Functionality  
*    Santhi Mandava                                03/12/2019             Removed "DpaaSWebServiceMockImpl" usage.
**********************************************************************************************************************************/

@isTest
private class HUMCaseCloneOverrideExtensionTest
{
    
    /**
    * This Method is used to setup test data. 
    *
    * @param  None      
    * @return  Void
    */
    @testSetup static void setup()  
    {  
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        System.assert(oUser.id != null);
        
        system.runAs(oUser)
        {
            Group oGrp1 = new Group(Name = 'Test1', Type = 'QUEUE');
            insert oGrp1;
            System.assert(oGrp1.Name == 'Test1');
            Group oGrp2 = new Group(Name = 'Test2', Type = 'QUEUE');
            insert oGrp2;
            System.assert(oGrp2.Name == 'Test2');
            
            QueuesObject oQueueObject1 = new QueuesObject (QueueID = oGrp1.id, SobjectType ='Work_Queue_Setup__c');
            insert oQueueObject1;
            System.assert(oQueueObject1.id != null);
            QueuesObject oQueueObject2 = new QueuesObject (QueueID = oGrp2.id, SobjectType ='Work_Queue_Setup__c');
            insert oQueueObject2;
            System.assert(oQueueObject2.id != null);
                               
            Classification_Type__c objcT = new Classification_Type__c();
            objcT.Name ='CallRSOtype';
            objcT.Is_Active__c = true;
            insert objcT;
            System.assertEquals(objcT.Name,'CallRSOtype');
            
            Work_Queue_Setup__c oWQS1 = new Work_Queue_Setup__c();
            oWQS1.Name='Test1';
            oWQS1.Service_Center__c='Test1';
            oWQS1.Department__c='Test1';
            oWQS1.Classification_Type__c=objcT.id;
            oWQS1.OwnerId= oGrp1.id;
            oWQS1.isActive__c = true;
            insert oWQS1;
            System.assert(oWQS1.id != null);
            System.assert(oWQS1.Name == 'Test1');
            
            Work_Queue_Setup__c oWQS2 = new Work_Queue_Setup__c();
            oWQS2.Name='Test2';
            oWQS2.Service_Center__c='Test2';
            oWQS2.Department__c='Test2';
            oWQS2.Classification_Type__c=objcT.id;
            oWQS2.OwnerId= oGrp2.id;
            insert oWQS2;
            System.assert(oWQS2.id != null);
            System.assert(oWQS2.Name == 'Test2');
            
            oUser.Current_Queue__c = oWQS1.Name;
            update oUser;
            System.assert(oUser.Current_Queue__c == 'Test1');
            
            //Create Test Group Account, Member Account, Product, Policy
            Account oAccount = HUMTestDataHelperTest.getAccount();
            oAccount.Name = 'New Case Account';
            oAccount.Group_Number__c  = '123';
            oAccount.BillingState = 'New York';
            oAccount.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
            insert oAccount;
            System.assert(oAccount.id!= null);
            System.assert(oAccount.Name == 'New Case Account');
            
            Account oAccountRec = HUMTestDataHelperTest.getAccount();
            oAccountRec.FirstName = 'General';
            oAccountRec.LastName  = 'Member';
            oAccountRec.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            insert oAccountRec;
            System.assert(oAccountRec.id!= null);
            System.assert(oAccountRec.FirstName == 'General');
            
            Product2 oProduct = HUMTestDataHelperTest.getProduct();
            oProduct.Name='Mediclaim';
            insert oProduct;
            system.assertNotEquals(oProduct.id,null,'Value not matched');
            System.assert(oProduct.Name == 'Mediclaim');
            
            Policy__c oPolicy = HUMTestDataHelperTest.getPolicy();
            oPolicy.Name = 'test123';
            oPolicy.Group_Name__c = oAccount.Id;
            oPolicy.Funding_Type_Code__c = 'Y';
            oPolicy.Product__c = oProduct.Id;
            oPolicy.Issue_State__c = 'IL'; 
            insert oPolicy;
            System.assert(oPolicy.id != null);
            System.assert(oPolicy.Name == 'test123');
            
            Policy_Member__c oPolicyMem = HUMTestDataHelperTest.getPolicyMember();
            oPolicyMem.Name = '12345';
            oPolicyMem.Member__c = oAccountRec.Id;
            oPolicyMem.Policy__c = oPolicy.Id;            
            oPolicyMem.Relationship__c = 'Self';
            insert oPolicyMem;
            System.assert(oPolicyMem.id != null);
            System.assert(oPolicyMem.Name == '12345');
            
            List<Classification__c> lstClassification = new List<Classification__c>();
            Classification__c classificationRec = new Classification__c();
            classificationRec.Name = 'Test1';
            classificationRec.Is_Active__c = true;
            lstClassification.add(classificationRec);
        
            Classification__c classificationRecDec = new Classification__c();
            classificationRecDec.Name = 'Test2';
            classificationRecDec.Is_Active__c = true;
            lstClassification.add(classificationRecDec);
            insert lstClassification;
            system.assertEquals(lstClassification[0].Name=='Test1', true);
        
            List<Intent__c> lstIntent = new List<Intent__c>();
            Intent__c intentRec = new Intent__c();
            intentRec.Name = 'Test1';
            intentRec.Is_Active__c = true;
            lstIntent.add(intentRec);
        
            Intent__c intentRecDec = new Intent__c();
            intentRecDec.Name = 'Test2';
            intentRecDec.Is_Active__c = true;
            lstIntent.add(intentRecDec);
            insert lstIntent;
            system.assertEquals(intentRec.Name == 'Test1', true);

            CTCI_Junction__c objCTCI = new CTCI_Junction__c();
            objCTCI.Classification__c=lstClassification[0].id;
            objCTCI.Classification_Type__c= objcT.id;
            objCTCI.Intent__c =lstIntent[0].id;
            objCTCI.IsActive__c=true;
            insert objCTCI ;
            system.assertNotEquals(Null,objCTCI.id);
            
            Case caseRecord = new Case();
            caseRecord.Owner_Queue__c = 'Test1';
            caseRecord.Status = 'In Progress';
            caseRecord.AccountId = oAccountRec.id;
            caseRecord.ownerId = oUser.Id;
            caseRecord.Classification_Type__c = 'Test1';
            caseRecord.Classification_id__c = lstClassification[0].id;
            caseRecord.Intent_id__c = lstIntent[0].id;
            caseRecord.Interacting_With__c = oAccountRec.id;
            caseRecord.Interacting_With_Type__c = 'Member';
            caseRecord.Policy_Member__c = oPolicyMem.Id;
            caseRecord.CTCI_List__c=objCTCI.id;
            insert caseRecord;
            system.assertEquals(caseRecord.Status == 'In Progress', true);
            
            Case caseRecord1 = new Case();
            caseRecord1.Owner_Queue__c = 'Test1';
            caseRecord1.Status = 'Closed';
            caseRecord1.AccountId = oAccountRec.id;
            caseRecord1.ownerId = oUser.Id;
            caseRecord1.Classification_Type__c = 'Test1';
            caseRecord1.Classification_id__c = lstClassification[0].id;
            caseRecord1.Intent_id__c = lstIntent[0].id;
            caseRecord1.Interacting_With__c = oAccountRec.id;
            caseRecord1.Interacting_With_Type__c = 'Member';
            caseRecord1.Policy_Member__c = oPolicyMem.Id;
            caseRecord1.Complaint__c = 'No';
            caseRecord1.G_A_Rights_Given__c = 'No';
            caseRecord1.CTCI_List__c=objCTCI.id;
            insert caseRecord1;
            system.assertEquals(caseRecord1.Status == 'Closed', true);
            
            Case caseRecord2 = new Case();
            caseRecord2.Owner_Queue__c = 'Test1';
            caseRecord2.Status = 'Closed';
            caseRecord2.AccountId = oAccountRec.id;
            caseRecord2.ownerId = oUser.Id;
            caseRecord2.Classification_Type__c = 'Test1';
            caseRecord2.Classification_id__c = lstClassification[0].id;
            caseRecord2.Intent_id__c = lstIntent[0].id;
            caseRecord2.Interacting_With__c = oAccountRec.id;
            caseRecord2.Interacting_With_Type__c = 'Member';
            caseRecord2.Policy_Member__c = oPolicyMem.Id;
            caseRecord2.Complaint__c = 'No';
            caseRecord2.G_A_Rights_Given__c = 'No';
            caseRecord2.CTCI_List__c=objCTCI.id;
            caseRecord2.RecordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get(GLOBAL_CONSTANT_HUM.CLOSED_MEDICARE_CASE).getRecordTypeId(); 
            insert caseRecord2;
            system.assertEquals(caseRecord2.Status == 'Closed', true);
            
            
        }
    }
    
    /**
    * This is a Test Method to clone a Case 
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void cloneCase() 
    {
        User oUser = [SELECT Id,Current_Queue__c FROM User WHERE Lastname='test User1' LIMIT 1];
        system.assertNotEquals(oUser.id, null, 'Value not matched');
        oUser.Current_Queue__c = 'Test1';
        update oUser;
        system.runAs(oUser)
        {
            //Start test
            Test.startTest();
            List<Case> lstCase = [Select Id,RecordTypeId from Case where status = 'In Progress' limit 1];
            HUMCaseCloneOverrideExtension extension = new  HUMCaseCloneOverrideExtension(new ApexPages.StandardController(lstCase[0]));
            extension.cloneCase();                      
            system.assertEquals(2, [select count() from Case where Status = 'In Progress']);
                       
            oUser.Current_Queue__c ='Test2';
            update oUser;
            System.assert(oUser.Current_Queue__c == 'Test2');
           
            HUMCaseCloneOverrideExtension caseExtension = new  HUMCaseCloneOverrideExtension(new ApexPages.StandardController(lstCase[0]));
            caseExtension.cloneCase();
            system.assertEquals(3, [select count() from Case where Status = 'In Progress']);
            
            oUser.Current_Queue__c ='';
            update oUser;
            System.assert(oUser.Current_Queue__c == '');
            
            caseExtension = new  HUMCaseCloneOverrideExtension(new ApexPages.StandardController(lstCase[0]));
            caseExtension.cloneCase();
            system.assertEquals(3, [select count() from Case where Status = 'In Progress']);
            
            Test.stopTest();     
        }
    }
    
    /**
    * This is a Test Method to clone a Case 
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void closedCloneCase() 
    {
        User oUser = [SELECT Id,Current_Queue__c FROM User WHERE Lastname='test User1' LIMIT 1];
        system.assertNotEquals(oUser.id, null, 'Value not matched');
        oUser.Current_Queue__c = 'Test1';
        update oUser;
        system.runAs(oUser)
        {
            //Start test
            Test.startTest();
            List<Case> lstCase = [Select Id,RecordTypeId from Case where status = 'Closed' limit 1];
            HUMCaseCloneOverrideExtension extension = new  HUMCaseCloneOverrideExtension(new ApexPages.StandardController(lstCase[0]));
            extension.cloneCase();                      
            system.assertEquals(2, [select count() from Case where Status = 'In Progress']);
                       
            oUser.Current_Queue__c ='Test2';
            update oUser;
            System.assert(oUser.Current_Queue__c == 'Test2');
           
            HUMCaseCloneOverrideExtension caseExtension = new  HUMCaseCloneOverrideExtension(new ApexPages.StandardController(lstCase[0]));
            caseExtension.cloneCase();
            system.assertEquals(3, [select count() from Case where Status = 'In Progress']);
            
            oUser.Current_Queue__c ='';
            update oUser;
            System.assert(oUser.Current_Queue__c == '');
            
            caseExtension = new  HUMCaseCloneOverrideExtension(new ApexPages.StandardController(lstCase[0]));
            caseExtension.cloneCase();
            system.assertEquals(3, [select count() from Case where Status = 'In Progress']);
            
            Test.stopTest();     
        }
    }
    
    /**
    * This is a Test Method to clone a Medicare Case 
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void medicareCloneCase() 
    {        
        User oUser = [SELECT Id,Current_Queue__c FROM User WHERE Lastname='test User1' LIMIT 1];
        System.assertNotEquals(oUser.Id, null, 'User is not present');
        oUser.Current_Queue__c = 'Test1';
        update oUser;
        System.runAs(oUser)
        {   
            Test.startTest();
            List<Case> lstCase = [SELECT Id, RecordTypeId FROM Case WHERE RecordType.Name = :GLOBAL_CONSTANT_HUM.CLOSED_MEDICARE_CASE LIMIT 1];
            HUMCaseCloneOverrideExtension extension = new  HUMCaseCloneOverrideExtension(new ApexPages.StandardController(lstCase[0]));
            extension.cloneCase();                      
            Test.stopTest();     
        }
    }
    
}