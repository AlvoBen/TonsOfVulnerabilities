/***********************************************************************************************************
Apex Class Name  : CaseClone_LT_HUM
Version          : 1.0
Function         : Test class for CaseClone_LC_HUM
************************************************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Bhumika S /MK Manoj                            09/01/2022                   Test class for CaseClone_LC_HUM.cls
* Dinesh Subramaniyan                           02/27/2023                  US4274185- Systematically Attach Interaction to Cloned Cases
************************************************************************************************************/
@isTest
public class CaseClone_LT_HUM {
    /**
    * This Method is used to setup test data. 
    *
    * @param  None      
    * @return  Void
    */
    @testSetup static void setup()  
    {  
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Supervisor');
        oUser.Current_Queue__c = 'Test1';
        insert oUser;
        System.assert(oUser.id != null);
  
        system.runAs(oUser)
        {
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
            //oWQS1.OwnerId= oGrp1.id;
            oWQS1.isActive__c = true;
            insert oWQS1;
            System.assert(oWQS1.id != null);
            System.assert(oWQS1.Name == 'Test1');
            
          
            
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

            
            Interaction__c intRec1  = new Interaction__c();
        	intRec1.Interaction_origin__c = 'Inbound Call';
        	intRec1.Caller_Name__c = 'Int Auth False';  
        	intRec1.Interacting_With__c = oAccountRec.id;
        	intRec1.Interacting_With_type__c = 'Member';
        	intRec1.Authenticated__c = false;
        	insert intRec1;

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
            caseRecord.Verbal_Consent_Obtained__c='No';
            caseRecord.Interacting_With__c = oAccountRec.id;
            caseRecord.Interacting_With_Type__c = 'Member';
            caseRecord.CTCI_List__c=objCTCI.id;
            caseRecord.RecordTypeId=Schema.SObjectType.Case.getRecordTypeInfosByName().get('Member Case').getRecordTypeId();
            caseRecord.Subtype__c=' ';
            insert caseRecord;
            system.assertEquals(caseRecord.Status == 'In Progress', true);
        
            Case caseRecord2 = new Case();
            caseRecord2.Owner_Queue__c = 'Test1';
            caseRecord2.Status = 'Closed';
            caseRecord2.AccountId = oAccountRec.id;
            caseRecord2.ownerId = oUser.Id;
            caseRecord2.Classification_Type__c = 'Test1';
            caseRecord2.Classification_id__c = lstClassification[0].id;
            caseRecord2.Intent_id__c = lstIntent[0].id;
            caseRecord2.Interacting_With__c = oAccountRec.id;
            caseRecord2.Verbal_Consent_Obtained__c='No';
            caseRecord2.Interacting_With_Type__c = 'Member';
            caseRecord2.Complaint__c = 'No';
            caseRecord2.G_A_Rights_Given__c = 'No';
            caseRecord2.CTCI_List__c=objCTCI.id;
            caseRecord.Subtype__c=' ';
            caseRecord2.RecordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get(GLOBAL_CONSTANT_HUM.CLOSED_MEDICARE_CASE).getRecordTypeId(); 
            insert caseRecord2;
            system.assertEquals(caseRecord2.Status == 'Closed', true);
            
          
            List<HUMCaseCloneFieldsLC__c> lstCsClone = new List<HUMCaseCloneFieldsLC__c>();
            lstCsClone.add(new HUMCaseCloneFieldsLC__c(Name='AccountId' ));
            lstCsClone.add(new HUMCaseCloneFieldsLC__c(Name='Classification_Type__c' ));
            lstCsClone.add(new HUMCaseCloneFieldsLC__c(Name='Interacting_With__c'));
            lstCsClone.add(new HUMCaseCloneFieldsLC__c(Name='Interacting_With_Name__c'));
            lstCsClone.add(new HUMCaseCloneFieldsLC__c(Name='Interacting_With_Type__c'));
            lstCsClone.add(new HUMCaseCloneFieldsLC__c(Name='Origin' ));
            lstCsClone.add(new HUMCaseCloneFieldsLC__c(Name='SubType__c'));  
            lstCsClone.add(new HUMCaseCloneFieldsLC__c(Name='RecordTypeId'));  
            lstCsClone.add(new HUMCaseCloneFieldsLC__c(Name='Verbal_Consent_Obtained__c')); 
            insert lstCsClone;
            system.assertequals(9,lstCsClone.size());
            
            
        }
    }
    
    /**
    * This is a Test Method to clone a Case 
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void testCloneCase() 
    {
        User oUser = [SELECT Id,Current_Queue__c FROM User WHERE Lastname='test User1' LIMIT 1];
      
        system.runAs(oUser)
        {
            //Start test
            Test.startTest();
            List<Case> lstCase = [Select Id,RecordTypeId,case.accountId,CaseNumber from Case where status = 'In Progress'  limit 1];
           List<Case> lstClosedCase = [Select Id,RecordTypeId,case.accountId,CaseNumber from Case where status = 'Closed' and RecordType.Name = :GLOBAL_CONSTANT_HUM.CLOSED_MEDICARE_CASE limit 1];
            List<Interaction__c> lstInteraction = [Select Id from Interaction__c limit 1];
           
            CaseClone_LC_HUM.cloneCase(lstCase[0].id,lstInteraction[0].id);
            CaseClone_LC_HUM.cloneCase(lstClosedCase[0].id,lstInteraction[0].id);
            CaseClone_LC_HUM.getUserCurrentQueue();
            
            oUser.Current_Queue__c ='';
            update oUser;
            System.assert(oUser.Current_Queue__c == '');
            
            CaseClone_LC_HUM.cloneCase(lstCase[0].id,lstInteraction[0].id);
            CaseClone_LC_HUM.getUserCurrentQueue();
            Test.stopTest();     
        }
    }
    
}