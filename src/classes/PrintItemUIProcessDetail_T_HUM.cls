/**********************************************************************************************************
Apex Class Name  : PrintItemUIProcessDetail_T_HUM
Version          : 1.0
Created Date     : 03/16/2020
Function         : Test class for PrintItemUIProcessDetail_C_HUM
***********************************************************************************************************
Modification Log:
* Developer Name            Review Number            Date                       Description
-------------------------------------------------------------------------------------------------------------
* Jasmeen Shangari                                  04/09/2020                  Original Version
*************************************************************************************************************/
@isTest
private class PrintItemUIProcessDetail_T_HUM{

    /*
    * This method creates test data for test class
    * @param None
    * @return None
    */
    @testSetup static void setUpTestData()
    {
        User CusCareSup= HUMTestDataHelperTest.createTestUser('CusCareSup','Customer Care Specialist');
        insert CusCareSup;
        system.assertEquals(CusCareSup.lastname=='test CusCareSup',true);
        system.runAs(CusCareSup)
        { 
            Case oCase = new Case(Interacting_With_Type__c = 'Member', status='In Progress',Origin = 'Inbound Call');
            insert oCase;
            system.assert(oCase.Interacting_With_Type__c == 'Member');
                 
            Template__c temp = new Template__c(Description__c = 'Send Print Item: Member',Unique_Template_Name__c='PrintItem');
            insert temp;
            system.assertEquals(temp.Description__c,'Send Print Item: Member');
            
            Template_Field__c tempField = new Template_Field__c(Name = 'Data',Template__c = temp.Id);
            insert tempField;
            system.assertEquals(tempField.Name,'Data');
            
            Template_Submission_Owner__c tOwner = new Template_Submission_Owner__c(Object_Owner_ID__c=string.valueOf(oCase.id),Object_Owner_Type__c='Case' );
            insert tOwner;
            system.assertEquals(tOwner.Object_Owner_Type__c,'Case');
            
            Template_Submission__c submission = new Template_Submission__c(Template__c = temp.id, Submission_Owner__c = tOwner.id);
            insert submission;
            system.assertEquals(submission.Template__c,temp.id);
                    
            Template_Submission_Data__c tempSubData = new Template_Submission_Data__c();
            tempSubData.Value__c = 'Test';
            tempSubData.Template_Field__c = tempField.Id;
            tempSubData.Template_Submission__c = submission.Id;
            insert tempSubData;
            system.assertEquals(tempSubData.Value__c,'Test');
        }
        
    }
    
     /*
   * Method Name : runPrintItemUIProcess
   * Description : This method is used to call PrintItemUIProcessDetail_C_HUM class
   * Return type : void
   * Paramater   : NA
   */ 
   static testMethod void runPrintItemUIProcess()
    {    
        User oCCSUser = [Select id, LastName, UserName from User where LastName = 'test CusCareSup' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.lastname=='test CusCareSup',true);
        System.runAs (oCCSUser) 
         {
               Template_Submission__c temSub = new Template_Submission__c();
               temSub = [select id from Template_Submission__c limit 1];

               Test.setCurrentPage(page.PrintItemUIProcessDetail_VF_HUM);
               ApexPages.currentPage().getParameters().put('relatedProID',temSub.Id);
               PrintItemUIProcessDetail_C_HUM printItemUI = New PrintItemUIProcessDetail_C_HUM();
         }
    }
    
}