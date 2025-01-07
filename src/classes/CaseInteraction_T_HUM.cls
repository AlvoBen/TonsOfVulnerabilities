/**************************************************************************** 
Apex Class Name  : CaseInteraction_T_HUM
Version          : 1.0  
Created Date     : October 30 ,2017
Function         : test class for CaseInteraction_C_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Sumit Pharande                             31/10/2017                   Original Version
* Pradeepkumar Dani        149126            11/17/2017                      re-factoring
* Santhi Mandava                                    02/01/2021                     Fixed test class failure issue
***************************************************************************************************/
@isTest
private with sharing class CaseInteraction_T_HUM
{
    /*
    * This method creates test data for test class
    * @param None
    * @return None
    */
    @testSetup static void setUpTestData()
    {
        User testUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert testUser;
        system.assertEquals(testUser.lastname=='test User1',true);
        system.runAs(testUser)
        { 
            Case objCase = HUMTestDataHelperTest.getCase();
            insert objCase;
            system.assertNotEquals(objCase,null,'Value not matched');
            
            Task objTask = HUMTestDataHelperTest.getTaskRecord();
            objTask.WhatId = objCase.Id;
            objTask.Type='Work Task';
            insert objTask;
            system.assertNotEquals(objTask,null,'Value not matched');
            
            Interaction__c interaction = new Interaction__c ();
            interaction.Caller_Name__c ='TEST';
            interaction.Interacting_With_type__c='NEW';
            interaction.Interaction_Origin__c='Inbound Call';
            insert interaction;
            system.assertNotEquals(interaction,null,'Value not matched');
            
            Case_Interaction__c cc1= new Case_Interaction__c(Case__c = objCase.Id, Interaction__c=interaction.Id);
            insert cc1;
            system.assertNotEquals(cc1,null,'Value not matched');
            
            HUMSalesforceFieldIds__c oSalesForceIds = new HUMSalesforceFieldIds__c ();
            
            oSalesForceIds.Name = 'id';
            oSalesForceIds.Case_in_Case_Interaction__c = '00NL00000035Rah';
            oSalesForceIds.RelatedTo_Case_in_Attachment__c = '00NL00000036quk';
            oSalesForceIds.RelatedTo_Task_in_Attachment__c = '00NL000000371yT';
            oSalesForceIds.Interaction_in_Case_Interaction__c = '00NL00000035Ram';
            insert oSalesForceIds;
            system.assertNotEquals(oSalesForceIds,null,'Value not matched');
        }  
    }
    
    /*
    * Method Name : testcomments
    * Description : This method Is used To test the Controller
    * Return type : None
    * Parameter   : None
    */
    static testMethod void testInte()
    {
        User testUser = [SELECT Id,Name FROM User WHERE LastName='test User1'];
        
        system.runAs(testUser)
        {
            Test.startTest();
            
            Case caseObj = [SELECT Id FROM Case LIMIT 1];
            Case oCase = [Select id,CaseNumber from case];
            ApexPages.currentPage().getParameters().put('Id', caseObj.Id);
            CaseInteraction_C_HUM ca = new CaseInteraction_C_HUM();
            CaseInteraction_C_HUM.getInteraction(caseObj.Id);
            system.assertEquals( ca.columnName <> null, TRUE);
            
            Test.stopTest();
        } 
    }
}