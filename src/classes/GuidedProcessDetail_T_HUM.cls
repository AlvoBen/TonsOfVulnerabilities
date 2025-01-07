/****************************************************************************
Apex Class Name  : GuidedProcessDetail_T_HUM 
Version          : 1.0 
Created On       : 05/12/2016
Function         : Test class to GuidedProcessDetail_C_HUM Apex class

Modification Log :
* Developer Name              Code Review                   Date                       Description
*-----------------------------------------------------------------------------------------------------------------------
* Praveen Kumar Parimi          27942                     05/12/2016                 Original Version     
* Avinash Choubey               33390                     07/11/2016                 Changes made as part of REQ- 255151  
*************************************************************************************************************************/
@isTest
private class GuidedProcessDetail_T_HUM
{
     /*
     * This is the test method which loads data for GuidedProcessDetail_T_HUM. 
     * @param     None
     * @return    void
     */ 
    @testSetup static void testData()
    {
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u;
        system.assertEquals(u.Lastname == 'test User1',true);
         
        System.runAs(u)
        {         
            Account acc1 = HUMTestDataHelperTest.getAccount();
            acc1.Name = 'Test Account Cases1';
            acc1.Birthdate__c='01/15/1951';
            acc1.Enterprise_ID__c = '1005447107';
            insert acc1;
            system.assertEquals(acc1.Name == 'Test Account Cases1',true);
                
            Policy__c pol = HUMTestDataHelperTest.getPolicy();
            pol.Group_Name__c = acc1.Id;
            pol.Name = 'Test Policy';
            insert pol;
            system.assertEquals(pol.Name,'Test Policy');
            system.assertEquals(pol.Group_Name__c == acc1.Id,true);
                
            Policy_Member__c polMember = HUMTestDataHelperTest.getPolicyMember();
            polMember.Name='Policy Member PM1';
            polMember.Policy__c=pol.Id;
            polMember.Member__c=acc1.Id;
            insert polMember;
            system.assertEquals(polMember.Name,'Policy Member PM1');
            system.assertEquals(polMember.Policy__c,pol.Id);  
            
            Case oCase = new Case(AccountId=acc1.Id, General_Case__c = true, Interacting_With_Type__c = 'Group',Interacting_With__c=acc1.Id, Due_Date__c=Date.Today(), Case_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU1', Work_Queue_View_Name__c='Cincinati CRU1',Service_Center__c ='Cincinnati',Department__c='CRU1', status='In Progress');
            insert oCase;
            system.assertNotEquals(oCase,null,'Value not matched');
                 
            Template__c oTempate = new Template__c(Description__c ='Authorization/Referral Requirements');
            insert oTempate;
            system.assertNotEquals(oTempate,null,'Value not matched');
            system.assertEquals(oTempate.Description__c ,'Authorization/Referral Requirements');   
        
            Template_Field__c oTemplateField = new Template_Field__c(Name= 'SubmissionData', Data_Type__c= 'String',Template__c = oTempate.Id );
            insert oTemplateField ;
            system.assertNotEquals(oTemplateField ,null,'Value not matched'); 
            system.assertEquals(oTemplateField.Name,'SubmissionData');   
            
            Template_Submission_Owner__c  oTemplateSubmsissionOwner = new Template_Submission_Owner__c(Object_Owner_ID__c = oCase.Id,  Object_Owner_Type__c = 'Case')  ;
            insert oTemplateSubmsissionOwner;
            system.assertNotEquals(oTemplateSubmsissionOwner,null,'Value not matched'); 
            system.assertEquals(oTemplateSubmsissionOwner.Object_Owner_Type__c  ,'Case');  
            
            Template_Submission__c oTempalteSubmsision = new Template_Submission__c(Template__c =oTempate.Id, Submission_Owner__c = oTemplateSubmsissionOwner.Id, Version__c = 1.00 );
            insert oTempalteSubmsision;
            system.assertNotEquals(oTempalteSubmsision,null,'Value not matched');  
            
            Template_Submission_Data__c oTemplateSubmissionData = new Template_Submission_Data__c(Template_Field__c = oTemplateField.Id , Template_Submission__c = oTempalteSubmsision.Id ,value__c ='TestValue' );
            insert oTemplateSubmissionData ;
            system.assertNotEquals(oTemplateSubmissionData ,null,'Value not matched'); 
        }
    }
    
    /*
     * This is the test method which is used to test the GuidedProcessDetail_C_HUM. 
     * @param     None
     * @return    void
     */     
    private static testMethod void testGuidedProcessDetail()
    {
        User u = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];                      
        system.runAs(u)
        {       
            Test.StartTest();    
            List<case> LstPolMember = [select id,Account.Name from Case where General_Case__c = true LIMIT 1];        
            ApexPages.CurrentPage().getParameters().put('CaseId',LstPolMember[0].id);         
            GuidedProcessDetail_C_HUM aRef = new GuidedProcessDetail_C_HUM();        
            aRef.sCaseId = LstPolMember[0].id; 
            system.assertEquals(aRef.sCaseId,LstPolMember[0].id);         
            aRef.sPolicyname = LstPolMember[0].Account.Name;
            system.assertEquals(aRef.sPolicyname,LstPolMember[0].Account.Name); 
            aRef.getDetailValue();
            aRef.getPolicyMemberName();
            system.assertEquals(aRef.getDetailValue(),'TestValue'); 
            system.assertEquals(aRef.getPolicyMemberName(),'Test Account Cases1');                
            Test.StopTest();                            
        }
    }
    
     /*
     * This is the test method which is used to test the GuidedProcessDetail_C_HUM. 
     * @param    NA
     * @return   void
     */     
    private static testMethod void testGuidedProcessDetailNegative()
    {
        User u = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];                      
        system.runAs(u)
        {       
            Test.StartTest();    
            List<case> LstPolMember = [select id,Account.Name from Case where General_Case__c = true LIMIT 1];                 
            ApexPages.CurrentPage().getParameters().put('CaseId',null);         
            GuidedProcessDetail_C_HUM aRef = new GuidedProcessDetail_C_HUM();  
            List<Error_Log__c> lsterror = aRef.listErrorLogs ;     
            aRef.sCaseId = null; 
            system.assertEquals(aRef.sCaseId,null);         
            aRef.sPolicyname = null;
            system.assertEquals(aRef.sPolicyname,null); 
            aRef.getDetailValue();
            aRef.getPolicyMemberName(); 
            system.assertEquals(aRef.getDetailValue(),null); 
            system.assertEquals(aRef.getPolicyMemberName(),null);               
            Test.StopTest();                            
        }
    }
}