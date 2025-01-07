/****************************************************************************
Apex Class Name  : GuidedProcessDeleteInvalidSub_BT_HUM 
Version          : 1.0 
Created On       : 05/12/2016
Function         : Test class to GuidedProcessDeleteInvalidSub_B_HUM Apex class.
Modification Log :
* Developer Name              Code Review                   Date                       Description
*-----------------------------------------------------------------------------------------------------------------------
* Praveen Kumar Parimi          27942                     05/12/2016                 Original Version       
*************************************************************************************************************************/
@isTest
private class GuidedProcessDeleteInvalidSub_BT_HUM 
{
    /*
     * Method Name : testdata
     * Description : This method will load the test Template data to test the batch functionality. 
     * Return type : Void
     * Paramater   : NA
     */     
    @testSetup static void testDataSetup()
    {
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u;
        system.assertEquals(u.Id != Null,true);
        system.assertEquals(u.Lastname == 'test User1',true);
         
        System.runAs(u)
        {  
        Account oAccount = new Account();
        oAccount.Name = 'New Case Account';
        oAccount.Group_Number__c  = '123';
        oAccount.BillingState = 'New York';
        oAccount.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
        insert oAccount;
        System.assert(oAccount.id!= null);
        system.assertEquals(oAccount.Group_Number__c,'123');         
                    
        Template__c oTempate = new Template__c(Description__c ='Authorization/Referral Requirements');
        insert oTempate;
        system.assertNotEquals(oTempate,null,'Value not matched');
        system.assertEquals(oTempate.Description__c ,'Authorization/Referral Requirements'); 
        
        Template_Field__c oTemplateField = new Template_Field__c(Name= 'SubmissionData', Data_Type__c= 'String',Template__c = oTempate.Id );
        insert oTemplateField ;
        system.assertNotEquals(oTemplateField ,null,'Value not matched'); 
        system.assertEquals(oTemplateField.Name,'SubmissionData');   
        
        Template_Submission_Owner__c  oTemplateSubmsissionOwner = new Template_Submission_Owner__c(Object_Owner_ID__c = 'a9b09f4d-0911-4f05',  Object_Owner_Type__c = 'Case')  ;
        insert oTemplateSubmsissionOwner;
        system.assertNotEquals(oTemplateSubmsissionOwner,null,'Value not matched'); 
        system.assertEquals(oTemplateSubmsissionOwner.Object_Owner_ID__c ,'a9b09f4d-0911-4f05');  
        
        Template_Submission__c oTempalteSubmsision = new Template_Submission__c(Template__c =oTempate.Id, Submission_Owner__c = oTemplateSubmsissionOwner.Id, Version__c = 1.00 );
        insert oTempalteSubmsision;
        system.assertNotEquals(oTempalteSubmsision,null,'Value not matched');  
        
        Template_Submission_Data__c oTemplateSubmissionData = new Template_Submission_Data__c(Template_Field__c = oTemplateField.Id , Template_Submission__c = oTempalteSubmsision.Id ,value__c ='' );
        insert oTemplateSubmissionData ;
        system.assertNotEquals(oTemplateSubmissionData ,null,'Value not matched');
        }
        }
        
    /*
    * Method Name   :    testbatch
    * Description   :    This method is used to test the batch class
    * Return Type   :    NA
    * Parameters    :    NA
    */
    static testMethod void testbatch()
    {    
       User u = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];                      
       system.runAs(u)
       {
       Test.startTest();
       GuidedProcessDeleteInvalidSub_B_HUM oBatch = new GuidedProcessDeleteInvalidSub_B_HUM();
       Database.executeBatch(oBatch); 
       Test.stopTest();
       List<Template_Submission_Owner__c>  lstoTSO = [select ID,Name,Object_Owner_ID__c, Object_Owner_Type__c from Template_Submission_Owner__c  where object_owner_Id__c = 'a9b09f4d-0911-4f05'];
       System.assertEquals(lstoTSO.size(),0);
       }    
    }
}