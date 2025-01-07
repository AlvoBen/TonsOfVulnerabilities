/*******************************************************************************************************************************
Apec Class Name : NBA_Recommendation_Display_T_HUM 
Version         : 1.0
Created On      : 10/20/2020
Function        : Test Class for NBA_Recommendation_Display_C_HUM
                  
Modification Log: 
* Developer Name         Code Review              Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Sayali Nemade                                   10/20/2020              original version - 100% Coverage
********************************************************************************************************************************/

@isTest(seeAllData= false)
public class NBA_Recommendation_Display_T_HUM {
/*
* Method Name : setUpTestData
* Description : This method creates test data for test class
* Return type : None
* Parameter   : None
*/

    @testSetup static void setUpTestData() {
        
        User testUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert testUser;
        System.assertEquals(testUser.lastname=='test User1',true);
        
        List<Recommendation> listOfRecommendation = new List<Recommendation>();
        listOfRecommendation.add(new Recommendation(Name = 'Recom1', Alert_Type__c = 'Guidance', Show_To__c = 'Member', Description = 'context 1',  Message__c = 'dynamic1',
                                                    Location__c = 'All Pages', AcceptanceLabel = 'Accept',  RejectionLabel = 'Reject', ActionReference = 'Save_Encrypted_Flow',
                                                    Interacting_With_Type__c = 'Member'));
        insert listOfRecommendation;
        
        RecordType AccountRecordType1 = [Select Id from RecordType where Name='Agent/Broker' and sObjectType='Account'];
        
        Account oAcc1 = HUMTestDataHelperTest.getAccount();
        oAcc1.Name = 'Test Account Cases';
        oAcc1.OwnerId = testUser.id;
        oAcc1.Enterprise_Id__c = '123456';
        oAcc1.recordtypeid = AccountRecordType1.id;
        oAcc1.Agent_Type__c = 'Broker';
        insert oAcc1;
        System.assertEquals(oAcc1.id!=null, true);
        System.assertEquals(oAcc1.Agent_Type__c ,'Broker','Value not matched');

        Interaction__c oInte1 = HUMTestDataHelperTest.getInteraction();
        oInte1.Interacting_With__c = oAcc1.Id;
        oInte1.Interacting_With_type__c = 'Agent';
        insert oInte1;
        System.assertEquals(oInte1.id!=null, true);
        System.assertEquals(oInte1.Interacting_With_type__c ,'Agent','Value not matched');
    }
    
    /*
    * Method Name : Recommendation_Display_C_HUMTest
    * Description : This method is used to test the constructor NBA_Recommendation_Display_C_HUM class.
    * Return type : None
    * Parameter   : None
    */
    @isTest
    static void Recommendation_Display_C_HUMTest()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        Interaction__c testTnt = [Select Id,Name from Interaction__c where Interacting_With_type__c='Agent' limit 1];
		List<Recommendation> listOfRecommendation = [Select Id, Name from Recommendation];
        System.runAs(testUser)
        {
            Test.StartTest();
            	Test.setCurrentPage(page.NBA_Recommendation_Display_VF_HUM);
            	ApexPages.currentPage().getParameters().put('InteractionId', testTnt.id);
            	ApexPages.currentPage().getParameters().put('lstRecommendationActionIds', listOfRecommendation[0].Id);
				NBA_Recommendation_Display_C_HUM recommDisplay = new NBA_Recommendation_Display_C_HUM();
			Test.StopTest();
        }
    }
    
   
}