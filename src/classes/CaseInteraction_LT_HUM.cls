/*******************************************************************************************************************************
Apex Class Name : CaseInteraction_LT_HUM
Version         : 1.0
Created On      : 02/24/2023
Function        : This class is test class for CaseInteraction_LC_HUM

Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*	 1.0			Gowthami Thota									 02/28/2023				  Added method to cover getInteractions
* 	 2.0			Prasuna Pattabhi                                        03/02/2023                   US_4290170 and US_4286921
*/
@isTest
private class CaseInteraction_LT_HUM {
    /*
    * This method creates test data for test class
    * @param None
    * @return None
    */
 static testMethod void testCaseInteraction()
    {
        User testUser = HUMTestDataHelperTest.createTestUser('CaseInteractionUser','Customer Care Specialist');
        insert testUser;
        system.assertEquals(testUser.lastname=='test CaseInteractionUser',true);
        system.runAs(testUser){ 

            Case objCase = HUMTestDataHelperTest.getCase();
            insert objCase;
            system.assertNotEquals(objCase,null,'Value not matched');
            
            Interaction__c interaction = new Interaction__c ();
            interaction.Caller_Name__c ='TEST';
            interaction.Interacting_With_type__c='NEW';
            interaction.Interaction_Origin__c='Inbound Call';
            insert interaction;
            system.assertNotEquals(interaction,null,'Value not matched');
            
            Case_Interaction__c cc1= new Case_Interaction__c(Case__c = objCase.Id, Interaction__c=interaction.Id);
            insert cc1;
            system.assertNotEquals(cc1,null,'Value not matched');

            Test.startTest();
            CaseInteraction_LC_HUM.getInteractions(objCase.Id);
            CaseInteraction_LC_HUM.verifyLegacyDelete(ObjCase.id);
            Test.stopTest();
            
        }  
    }
	
	/*
    * This method test the new interaction changes
    * @param None
    * @return None
    */
	static testMethod void createNewInteraction(){
        User testUser = HUMTestDataHelperTest.createTestUser('CaseInteractionUser','Customer Care Specialist');
        insert testUser;
        system.assertEquals(testUser.lastname=='test CaseInteractionUser',true);
        system.runAs(testUser){ 

            Case objCase = HUMTestDataHelperTest.getCase();
            insert objCase;
            system.assertNotEquals(objCase,null,'Value not matched');
            
            Interaction__c interaction = new Interaction__c ();
            interaction.Caller_Name__c ='TEST';
            interaction.Interacting_With_type__c='NEW';
            interaction.Interaction_Origin__c='Inbound Call';
            insert interaction;
            system.assertNotEquals(interaction,null,'Value not matched');

            Test.startTest();
            String interactionData = '{"Caller_Name__c":"test","Interacting_With_type__c":"New","Interaction_Origin__c":"Inbound Call"}';
            CaseInteraction_LC_HUM.saveInteraction(interactionData,objCase.Id);

            interactionData = '{"Caller_Name__c":"test","Interacting_With_type__c":"Member","Interaction_Origin__c":"Inbound Call"}';
            CaseInteraction_LC_HUM.saveInteraction(interactionData,objCase.Id);

            CaseInteraction_LC_HUM.getCaseData(objCase.Id);
            Test.stopTest();
            
        }  
    }
}