/*******************************************************************************************************************************
Class Name    : NBA_RecommendationHistory_T_HUM 
Version         : 1.0
Created On      : 11/05/2020
Function        : Test Class for NBA_RecommendationHistory_C_HUM Class
                  
Modification Log: 
* Developer Name         Code Review              Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Sayali Nemade                                  11/05/2020                original version - 100% Coverage
********************************************************************************************************************************/

@isTest(seeAllData= false)
private class NBA_RecommendationHistory_T_HUM {
	/*
	* Method Name : setUpTestData
	* Description : This method creates test data for test class
	* Return type : None
	* Parameter   : None
	*/
	@testSetup static void setUpTestData() {
        
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        oUser.Current_Queue__c = 'Cincinnati CRU1';
        oUser.Network_User_Id__c = 'SXS2222';
        insert oUser;
        System.assertEquals(oUser.LastName, 'test User1');
        
        RecordType AccountRecordType1 = [Select Id from RecordType where Name='Agent/Broker' and sObjectType='Account'];
        
        Account oAcc1 = HUMTestDataHelperTest.getAccount();
        oAcc1.Name = 'Test Account Cases';
        oAcc1.OwnerId = oUser.id;
        oAcc1.Enterprise_Id__c = '123456';
        oAcc1.recordtypeid = AccountRecordType1.id;
        oAcc1.Agent_Type__c = 'Broker';
        insert oAcc1;
        System.assertEquals(oAcc1.id!=null, true);
        System.assertEquals(oAcc1.Agent_Type__c ,'Broker','Value not matched');
        
        List<Recommendation> listOfRecommendation = new List<Recommendation>();
        listOfRecommendation.add(new Recommendation(Name = 'Recom1', Alert_Type__c = 'Guidance', Show_To__c = 'Member', Description = 'context 1',  Message__c = 'dynamic1',
                                                    Location__c = 'All Pages', AcceptanceLabel = 'Accept',  RejectionLabel = 'Reject', ActionReference = 'Save_Encrypted_Flow',
                                                    Interacting_With_Type__c = 'Member',priority__c =1));
        insert listOfRecommendation;
        
        List<Recommendation_History__c> listOfRecommendationHistory = new List<Recommendation_History__c>();
        listOfRecommendationHistory.add(new Recommendation_History__c(Name = 'Message', Old_Value__c = 'Test Alert', Recommendation__c = listOfRecommendation[0].Id,
                                                                   New_Value__c = 'Test Alert Changed'));
        listOfRecommendationHistory.add(new Recommendation_History__c(Name = 'Show To', Old_Value__c = 'Member', Recommendation__c = listOfRecommendation[0].Id,
                                                                   New_Value__c = 'Member'));
        insert listOfRecommendationHistory;
    }
    
    /*
    * Method Name : lstOfRecommFieldHistoryTest
    * Return type : None
    * Parameter   : None
    */
    static testMethod void lstOfRecommFieldHistoryTest() {
        List<Recommendation_History__c> lstRecomHistory = [Select Id,Name,Old_Value__c,New_Value__c,Recommendation__c from Recommendation_History__c];
        String sRecommID = lstRecomHistory[0].Recommendation__c;
        NBA_RecommendationHistory_C_HUM.lstOfRecommHistory(sRecommID);
        NBA_RecommendationHistory_C_HUM.lstOfRecommendation(sRecommID);
    }
}