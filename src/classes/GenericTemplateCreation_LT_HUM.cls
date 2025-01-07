/**************************************************************************************************************************************************
Apex class Name    :  GrievanceAndAppeal_LT_HUM
Version            :  1.0
Function           :  This is Test class for GrievanceAndAppeal_LC_HUM.
Test Class         :  GrievanceAndAppeal_LT_HUM

Modification Log:
* Developer Name        Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Nirmal Garg                         09/05/2022                 Original Version
* Divya Bhamre                        04/25/2023                 Fixing Test Class
************************************************************************************************************************************************* */

@isTest
public class GenericTemplateCreation_LT_HUM {
    
    @testSetup static void setUpTestData(){
        
        User objUser = HUMTestDataHelperTest.createTestUser('User1', 'Customer Care Specialist');
        insert objUser;
        System.assertEquals(objUser.lastname == 'test User1', true);
        RecordType caseRecordType = [Select Id from RecordType where Name='Closed Member Case' and sObjectType='Case' limit 1];      
        RecordType AccountRecordType = [Select Id from RecordType where Name='Member' and sObjectType='Account' limit 1];
        
        System.runAs(objUser){
            Account accountRec = HUMTestDataHelperTest.getAccount();
            accountRec.OwnerId = objUser.id;
            accountRec.FirstName = 'Test';
            accountRec.LastName = 'Account';
            accountRec.Enterprise_Id__c = '123456';
            accountRec.recordtypeId = AccountRecordType.id;
            insert accountRec;
            system.assertEquals(accountRec.recordtypeId==AccountRecordType.id,true);
            
            Policy__c policyRec = New Policy__c();
            policyRec.Group_Name__c = accountRec.Id;
            policyRec.Product__c = null;
            policyRec.Policy_Group_Number__c='11111';
            policyRec.Major_Lob__c = 'MEF';
            insert policyRec;
            system.assertEquals(policyRec.Group_Name__c==accountRec.Id,true);
            
            Policy_Member__c policyMemRec = New Policy_Member__c();
            policyMemRec.Name = '12345';
            policyMemRec.Policy__c = policyRec.Id;
            policyMemRec.Member__c = accountRec.Id; 
            insert policyMemRec;
            system.assertEquals(policyMemRec.Policy__c==policyRec.Id,true);
            
            Case objCase = HUMTestDataHelperTest.getCase();            
            objCase.Guided_Process_Flag__c = True;
            objCase.Origin = GLOBAL_CONSTANT_HUM.TRR_ORIGIN;
            objCase.Owner_Queue__c = 'Louisville RSO Enrollment';
            objCase.Case_Owner__c = 'Louisville RSO Enrollment';
            objCase.status = GLOBAL_CONSTANT_HUM.CASE_STATUS_PENDING_RESPONSE;
            objCase.ChatKey__c = 'TestKey';
            objCase.Policy_Member__c = policyMemRec.Id;
            objCase.AccountID = accountRec.Id;
            insert objCase;
            
            Template__c template = new Template__c();
            template.Description__c = 'PCP/PCD Questions';
            template.Unique_Template_Name__c = 'PCP/PCD Questions' ;
            insert template;
            //submission owner
            Template_Submission_Owner__c ownerObj = New Template_Submission_Owner__c();
            ownerObj.Object_Owner_ID__c = objCase.Id;
            ownerObj.Object_Owner_Type__c = 'Case';
            insert ownerObj;
            //submission
             Template_Submission__c  submissionObj = New Template_Submission__c();
            submissionObj.Submission_Owner__c = ownerObj.Id;
            submissionObj.Template__c = template.id;
            insert submissionObj;
                       
            list<Template_Field__c> lstTemplateFlds = new list<Template_Field__c>();           
            Template_Field__c templateFld = new Template_Field__c();
            templateFld.Name = 'Processing Time Stamp';
            templateFld.Data_Type__c = 'DATETIME';
            templateFld.Template__c = template.Id;
            lstTemplateFlds.add(templateFld);
            
            Template_Field__c templateFld1 = new Template_Field__c();
            templateFld1.Name = 'Trans Code';
            templateFld1.Data_Type__c = 'STRING';
            templateFld1.Template__c = template.Id;
            lstTemplateFlds.add(templateFld1);
            insert lstTemplateFlds;	
                
            Template_Submission_Data__c submissionDataObj = New Template_Submission_Data__c();
            submissionDataObj.Value__c = '12345';
            submissionDataObj.Template_Field__c = templateFld.Id;
           // submissionDataObj.Name__c = templateFld.Name;_
            submissionDataObj.Template_Submission__c = submissionObj.Id;
            insert submissionDataObj;
            
            
        }
    }
    static testMethod void getTemplateFields()
    {
        User u = [SELECT Id,Name FROM User where LastName='test User1' ];
         String templateId = [Select id,Name,Data_Type__c,template__c from Template_Field__c where Name = 'Processing Time Stamp'].template__c;        
        System.runAs(u){      
            try{
               Test.startTest();
             
                List<Account> acc = [Select id from Account limit 1 ];
               List<Template_Field__c> templateList= GenericTemplateCreation_LC_HUM.getTemplateFields(templateId);
               system.assertNotEquals(0, templateList.size());
        
               List<Template_Field__c> templateListforNull= GenericTemplateCreation_LC_HUM.getTemplateFields('');       
               system.assertEquals(null, templateListforNull);
                
               Object listObjForException= GenericTemplateCreation_LC_HUM.getTemplateFields('testing');
               system.assertEquals('Error', listObjForException);  
                
 			   Test.stopTest();
            }catch(Exception e){
                system.assertEquals('Script-thrown exception', e.getMessage());
            }
            
        }
    }

    static testmethod void testgetTemplateDetails(){
        User testUser = [Select Id,Name from User where LastName='test User1'];
        Template__c templatePCPUpdate = [Select Id,Description__c,Unique_Template_Name__c from Template__c where Unique_Template_Name__c = 'PCP/PCD Questions' Limit 1];
        System.runAs(testUser)
        {
             try{
               Test.startTest();
                List<Account> acc = [Select id from Account limit 1 ];
             List<Template__c> lstTemplate = GenericTemplateCreation_LC_HUM.getTemplateDetails('PCP/PCD Questions'); 
            system.assertNotEquals(0, lstTemplate.size());
                 
               List<Template__c> templateListforNull= GenericTemplateCreation_LC_HUM.getTemplateDetails('');
               system.assertEquals(null, templateListforNull);
                
               Object listObjForException= GenericTemplateCreation_LC_HUM.getTemplateDetails('testing');
               system.assertEquals('Error', listObjForException);  
 			   Test.stopTest();
            }catch(Exception e){
                system.assertEquals('Script-thrown exception', e.getMessage());
            }   
        }
    }
  
    static testMethod void getTemplateDataDetails()
    {
        User u = [SELECT Id,Name FROM User where LastName='test User1' ];
        String tempSubmissionId = [SELECT Id, Name__c, Value__c,Template_Submission__c, createdby.name FROM Template_Submission_Data__c].Template_Submission__c;
        System.runAs(u){
            try{
               Test.startTest();
                List<Account> acc = [Select id from Account limit 1 ];

               List<Template_Submission_Data__c> templateList= GenericTemplateCreation_LC_HUM.getTemplateDataDetails(tempSubmissionId);
               system.assertNotEquals(0, templateList.size());
                 
               List<Template_Submission_Data__c> templateListforNull= GenericTemplateCreation_LC_HUM.getTemplateDataDetails('');
               system.assertEquals(null, templateListforNull);
                
               Object listObjForException= GenericTemplateCreation_LC_HUM.getTemplateDataDetails('testing');
               system.assertEquals('Error', listObjForException);  
 			   Test.stopTest();
            }catch(Exception e){
                system.assertEquals('Script-thrown exception', e.getMessage());
            }     
        }
    }
      
    
    static testMethod void getTemplateSubmissionOwnerId()
    {
        User u = [SELECT Id,Name FROM User where LastName='test User1' ];
        System.runAs(u){
            try{
               Test.startTest();
                List<Account> acc = [Select id from Account limit 1 ];
                String caseId = [Select id,Case_Owner__c from Case limit 1].id;
                
               string listObj= GenericTemplateCreation_LC_HUM.getTemplateSubmissionOwnerId(caseId);
               system.assertNotEquals(null,listObj);
                 
               Object listObjForException= GenericTemplateCreation_LC_HUM.getTemplateSubmissionOwnerId('testing');
               system.assertEquals(null, listObjForException);  
 		Test.stopTest();
            }catch(Exception e){
                system.assertEquals('Script-thrown exception', e.getMessage());
            }    
        }
    }    
}