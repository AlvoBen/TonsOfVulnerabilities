/*******************************************************************************************************************************
Apex Class Name : GenericCaseAction_LT_HUM
Version         : 1.0
Created On      : 2/18/2023
Function        : This class is test class for GenericCaseAction_LC_HUM

Modification Log: 
* Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
* Swapnali Sonawane                                   2/18/2023               US 4178421 Determine the logic to display templates on New case page and attach case to the template on Launch
* Divya Bhamre                                        04/25/2023              Fixing Test Class
* Kalyani Pachpol                                     07/13/2023              US-4810367
*/

@isTest
public class GenericCaseAction_LT_HUM {
    
 /**
* Test Setup method to create all the data required for testing.
*
* @param     NULL
* @return    void 
*/
    @testSetup   static void setUpTestData()
    {
        User u = new User(
            ProfileId = [SELECT Id FROM Profile WHERE Name = 'Humana Pharmacy Specialist'].Id,
            LastName = 'last',
            Email = 'puser000@a.com',
            Username = 'puser000@a.com' + System.currentTimeMillis(),
            CompanyName = 'TEST',
            Title = 'title',
            Alias = 'alias',
            TimeZoneSidKey = 'America/Los_Angeles',
            EmailEncodingKey = 'UTF-8',
            LanguageLocaleKey = 'en_US',
            LocaleSidKey = 'en_US'
        );
        insert u;
        
        PermissionSet ps = [SELECT Id FROM PermissionSet WHERE Name = 'CRMS_1210_Commercial_Demographic_Update'];
        
        insert new PermissionSetAssignment(AssigneeId = u.id, PermissionSetId = ps.Id);
        
       
        
        System.runAs(u){
             Work_Queue_Setup__c wqs = New Work_Queue_Setup__c(Name = 'Louisville Other Insurance Investigation');
        insert wqs;
            List<Account> accList = new List<Account>();            
            
            Account oMemAccount = HUMTestDataHelperTest.getAccount();
            oMemAccount.Group_Number__c = '987654321';
            oMemAccount.Enterprise_Id__c = '1000101';
            oMemAccount.recordtypeid = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            oMemAccount.firstName = 'Daphiner';
            oMemAccount.lastName = 'Superman';
            oMemAccount.Veteran_Status__c ='confirmed-veteran';
            oMemAccount.VA_Health_Enrollee__c ='true';
            oMemAccount.Home_Office_Account__c = false;
            accList.add(oMemAccount);
            if(accList != NULL) insert accList;
            system.assert(accList.size() == 1);
            
            Product2 prd = new Product2();
            prd.Product_Type__c = 'MAPD';
            prd.Name = 'test product';
            insert prd;
            
            PurchaserPlan pp = new PurchaserPlan();
            pp.Product__c = prd.id;
            pp.Name = 'test plan';
            insert pp;
            
            MemberPlan mp = new MemberPlan();
            mp.Name = 'test member plan';
            mp.MemberId = oMemAccount.id;
            mp.PlanId = pp.id;
            insert mp;
            
            Case cs = new Case();
            cs.Status = 'In Progress';
            cs.AccountId = accList[0].Id;
            cs.Member_Plan_Id__c = mp.id;
            cs.Origin = 'Email';
            insert cs;
            
            Case cse = new Case();
            cse.Status = 'In Progress';
            cse.AccountId = accList[0].Id;
            cse.Member_Plan_Id__c = mp.id;
            cse.Origin = 'Web Chat';
            insert cse;
            
            Case csee = new Case();
            csee.Status = 'In Progress';
            csee.AccountId = accList[0].Id;
            csee.Member_Plan_Id__c = mp.id;
            csee.Origin = 'Web Chat';
            csee.Area_of_Focus__c = 'Career';
            csee.Case_Owner_Queue__c = wqs.id;
            csee.RecordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Member Case').getRecordTypeId();
            insert csee;
            
            Template__c tem = new Template__c();
            tem.Unique_Template_Name__c = '786888';
            tem.Description__c  = 'Medicare OI Form';
            insert tem;
            
            Template_Field__c tf = new Template_Field__c();
            tf.Name='BIN';
            tf.Template__c = tem.id;
            insert tf;
            
            Template_Submission_Owner__c tso = new Template_Submission_Owner__c();
            tso.Object_Owner_ID__c = cs.id;
            tso.Object_Owner_Type__c = 'Case';
            insert tso;
            
            Template_Submission__c ts = new Template_Submission__c();
            ts.Submission_Owner__c = tso.id;
            ts.Template__c = tem.id;
            insert ts;
            
            Template_Submission_Data__c tsd = new Template_Submission_Data__c();
            tsd.Template_Submission__c = ts.id;
            tsd.Template_Field__c = tf.id;
            tsd.Value__c = '12345';
            insert tsd;
            
            Template_Submission_Owner__c tso1 = new Template_Submission_Owner__c();
            tso1.Object_Owner_ID__c = cse.id;
            tso1.Object_Owner_Type__c = 'Case';
            insert tso1;
            
            Template_Submission__c ts1 = new Template_Submission__c();
            ts1.Submission_Owner__c = tso1.id;
            ts1.Template__c = tem.id;
            insert ts1;
            
            Template_Submission_Data__c tsd1 = new Template_Submission_Data__c();
            tsd1.Template_Submission__c = ts.id;
            tsd1.Template_Field__c = tf.id;
             insert tsd1;
             
             Case_Action__c objCaseAction = new Case_Action__c(Name='QAA',Action_version__c='1',Active__c=true);
            insert objCaseAction;
    
            Question__c objQues = new Question__c(Name = 'QS-001',Actions__c=objCaseAction.Id,Active__c=true,Question_Label__c='Complaint Details',Sequence_Number__c=1,Question_Type__c='Text');
            insert objQues;
            
            Case_Action_Association__c objCaseActAssociation = new Case_Action_Association__c(Case__c=cse.Id,Case_Action__c = objCaseAction.id);
            insert objCaseActAssociation;
            
            Case_Action_Response__c objResponse = new Case_Action_Response__c(Action__c=objCaseActAssociation .Id,Case_Question__c=objQues.Id,Process_Question_Response__c='Test');
            insert objResponse;
			
			Interaction__c oInteraction = new Interaction__c(Interaction_Origin__c = 'InboundCall', UUID__c='128459461', Caller_Name__c='Guest Guestuser');
            insert oInteraction;
			
			Interaction__c oInteraction2 = new Interaction__c(Interaction_Origin__c = 'InboundCall', UUID__c='128459463', Caller_Name__c='Guest Guestuser');
            insert oInteraction2;
            
            Case_Interaction__c CIRec = new Case_Interaction__c();
            CIRec.Case__c = cs.Id;
            CIRec.Interaction__c = oInteraction.Id;
            insert CIRec;
        }
    }
    
    
    /**
    * This method is used to test the existing mapping of interaction to a case.
    *
    * @param     NULL
    * @return    void 
    */    
    static testMethod void checkForExistingMappingTestPositive()
    {
        User u = [SELECT Id FROM User where Email = 'puser000@a.com' ];
        System.runAs(u){
            String caseId = [Select id from Case where origin = 'Email'].id;
            Interaction__c oInteraction = [select id, UUID__c from Interaction__c where UUID__c='128459461' limit 1];
            Boolean bIsInteractionAttached = GenericCaseAction_LC_HUM.checkForExistingMapping(caseId,oInteraction.id );
            Assert.isTrue(bIsInteractionAttached);
        }
    }
    
    /**
    * This method is used to test the existing mapping of interaction to a case.
    *
    * @param     NULL
    * @return    void 
    */    
    static testMethod void checkForExistingMappingTestNegative()
    {
        User u = [SELECT Id FROM User where Email = 'puser000@a.com' ];
        System.runAs(u){
            String caseId = [Select id from Case where origin = 'Email'].id;
            Interaction__c oInteraction = [select id, UUID__c from Interaction__c where UUID__c='128459463' limit 1];
            Boolean bIsInteractionAttached = GenericCaseAction_LC_HUM.checkForExistingMapping(caseId,oInteraction.id );
            Assert.isFalse(bIsInteractionAttached);
 			bIsInteractionAttached = GenericCaseAction_LC_HUM.checkForExistingMapping('',oInteraction.id );
            Assert.isFalse(bIsInteractionAttached);
        }
    }
    
     /**
* This method is used to test the template submission owener ID for a case.
*
* @param     NULL
* @return    void 
*/
    static testMethod void getCaseDetailsTest()
    {
        User u = [SELECT Id FROM User where Email = 'puser000@a.com' ];
        System.runAs(u){
			List<Account> acc = [Select id from Account limit 1 ];
            String caseId = [Select id from Case where origin = 'Email' limit 1].id;
          	
             try{
                 List<Case> listObj= GenericCaseAction_LC_HUM.getCaseDetails(caseId);
                 system.assertNotEquals(0, listObj.size());
                 
                 List<Case> listObjForNull= GenericCaseAction_LC_HUM.getCaseDetails('');
                 system.assertEquals(null, listObjForNull);
                 
                 Object listObjForException= GenericCaseAction_LC_HUM.getCaseDetails('testing');
                 system.assertEquals('Error', listObjForException);
                                  
            }catch(Exception e){
                system.assertEquals('Script-thrown exception', e.getMessage());
            }
           
            
        }
    }
    
    static testMethod void testgetMemberPlanDetailsException(){
        User u = [SELECT Id FROM User where Email = 'puser000@a.com' ];
        System.runAs(u)
        {
            Test.startTest();
            try{
                 List<Case> listObjForException= GenericCaseAction_LC_HUM.getCaseDetails('testing');
            }catch(exception ex){
                system.assertNotEquals('error', ex.getMessage());
            }
            Test.stopTest();
        }
    }    
    static testMethod void testgetMemberPlanDetailsnegative(){
        User u = [SELECT Id FROM User where Email = 'puser000@a.com' ];
        System.runAs(u)
        {
            Test.startTest();
            List<Case> listObjForNull= GenericCaseAction_LC_HUM.getCaseDetails('');
                 system.assertEquals(null, listObjForNull);
            Test.stopTest();
        }
    }
    
    
}