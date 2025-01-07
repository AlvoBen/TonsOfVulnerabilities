/*******************************************************************************************************************************
Apex Class Name : Private class RecommendationInteraction_LT_HUM 
Version         : 1.0
Created On      : 03/28/2023
Function        : This  is the test class for RecommendationInteraction_LC_HUM
Test Class      : 
                 
Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Abhishek Mangutkar                                   03/28/2023                 Original Version
*******************************************************************************************************************************************************************/
@isTest
public class RecommendationInteraction_LT_HUM {
    
    /*
	* Method Name : setUpTestData
	* Description : This method creates test data For RecommendationInteraction_LC_HUM Class
	* Return type : None
	* Parameter   : None
	*/	
	@testSetup static void setUpTestData() {
        
        //User
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        oUser.Current_Queue__c = 'Cincinnati CRU1';
        oUser.Network_User_Id__c = 'SXS2222';
        insert oUser;
        Assert.areEqual(oUser.LastName, 'test User1');
        
        //Record Type
        RecordType AccountRecordType1 = [Select Id from RecordType where Name='Agent/Broker' and sObjectType='Account'];
        RecordType oMemRecType = [SELECT Id FROM RecordType WHERE Name = 'Member' Limit 1];
        RecordType oGroupRecType = [SELECT Id FROM RecordType WHERE Name = 'Group' Limit 1];
        
        //Accounts
        Account oAcc = HUMTestDataHelperTest.getAccount();
		oAcc.Group_Number__c = '675717';
		oAcc.recordtypeid = oMemRecType.Id;
		oAcc.Birthdate__c = '01/01/1984';
		oAcc.firstName = 'Test';
		oAcc.lastName = 'Account';
		oAcc.MiddleName = 'J';
		oAcc.Enterprise_ID__c='1030693160';
		oAcc.Home_Office_Account__c = false;
		insert oAcc;
        
        Account oAcc1 = HUMTestDataHelperTest.getAccount();
        oAcc1.Name = 'Test Account Cases';
        oAcc1.OwnerId = oUser.id;
        oAcc1.Enterprise_Id__c = '123456';
        oAcc1.recordtypeid = AccountRecordType1.id;
        oAcc1.Agent_Type__c = 'Broker';
        insert oAcc1;        
        
        Assert.isNotNull(oAcc1.id);        
        Assert.areEqual(oAcc1.Agent_Type__c ,'Broker','Value not matched');
        
        Account oAcc2 = HUMTestDataHelperTest.getAccount();
        oAcc2.FirstName = 'Test';
        oAcc2.LastName = 'Testtt';
        oAcc2.OwnerId = oUser.id;
        oAcc2.Enterprise_Id__c = '12345256';
        oAcc2.recordtypeid = oMemRecType.id;
        insert oAcc2;
        
        //Interaction
        Interaction__c oInte1 = HUMTestDataHelperTest.getInteraction();
        oInte1.Interacting_With__c = oAcc.Id;
        oInte1.Interacting_With_type__c = 'Member';
        insert oInte1;
        Assert.isNotNull(oInte1.id);
        Assert.areEqual(oInte1.Interacting_With_type__c ,'Member');
                
        //Recommendation
        List<Recommendation> listOfRecommendation = new List<Recommendation>();
        listOfRecommendation.add(new Recommendation(Name = 'Recom1', Alert_Type__c = 'Guidance', Show_To__c = 'Member', Description = 'context 1',  Message__c = 'dynamic1',
                                                    Location__c = 'All Pages', AcceptanceLabel = 'Accept',  RejectionLabel = 'Reject', ActionReference = 'Save_Encrypted_Flow',
                                                    Interacting_With_Type__c = 'Member',priority__c =1));
        insert listOfRecommendation;
        
        //Recommendation Detail
        List<Recommendation_Detail__c> listOfRecommendationDetail = new List<Recommendation_Detail__c>();
        listOfRecommendationDetail.add(new Recommendation_Detail__c( Account__c = oAcc.id, Enterprise_ID__c = oAcc.Enterprise_ID__c,
                                                                   Action__c = 'Acknowledged',Start_Date__c = Date.newInstance(2016, 12, 9), End_Date__c = Date.newInstance(2099, 12, 9),
                                                                   Type__c='Member', External_Id__c='1234555' , Recommendation__c = listOfRecommendation[0].Id));
        insert listOfRecommendationDetail;
        
        //Recommendation Action
        List<Recommendation_Action__c> listOfRecommendationAction = new List<Recommendation_Action__c>();
        listOfRecommendationAction.add(new Recommendation_Action__c(Name = 'RecomAction1', Action__c = 'Acknowledged', Recommendation__c = listOfRecommendation[0].Id,
                                                                   Interaction__c = oInte1.Id, Question_1__c = 'Hello' , Question_2__c = 'Hello', Question_3__c = 'Hello', Question_4__c = 'Hello'));
        listOfRecommendationAction.add(new Recommendation_Action__c(Name = 'RecomAction2', Action__c = 'Acknowledged', Recommendation__c = listOfRecommendation[0].Id,
                                                                   Interaction__c = oInte1.Id,Recommendation_Detail__c = listOfRecommendationDetail[0].Id));
        insert listOfRecommendationAction;
        
        //Standard Controller
        ApexPages.StandardController sc = new ApexPages.StandardController(oAcc);
        RecommendationInteraction_C_HUM hum = new RecommendationInteraction_C_HUM(sc);
        
        //Interaction member
        Interaction_Member__c intMem = new Interaction_Member__c();
        intMem.Interacting_About_Type__c = 'Member';
        intMem.Interaction__c = oInte1.Id;
        intMem.Interacting_About__c = oAcc2.Id;
                        
        insert intMem;
    }
    
        
    /*
    * Method Name : testGetRecommendationInfo
    * Description : This method is used to test RecommendationInteraction_LC_HUM Class
    * Return type : None
    * Parameter   : None 
    */
    static testMethod void testGetRecommendationInfo()
    {
        Test.StartTest();            
            List<Recommendation_Action__c> listRecommendationAction = [SELECT Id, Recommendation__r.Name, Alert_Name__c , 
                            Action__c, Action_Reason__c, Action_DateTime__c,Question_1_Response__c, Question_2_Response__c, Question_3_Response__c, 
                            Question_4_Response__c, Free_Text_Response1__c, Free_Text_Response2__c, Interaction__c, 
                            Question_1__c, Question_2__c, Question_3__c, Question_4__c,
                            CreatedById, Recommendation_Detail__c FROM Recommendation_Action__c 
                            where Name = 'RecomAction1' limit 1];
            String sInteractionID = listRecommendationAction[0].Interaction__c;
            List<RecommendationInteraction_LC_HUM.RecommendationInteractionDTO_LC_HUM> responseDTO = RecommendationInteraction_LC_HUM.getRecommendationInfo(sInteractionID);
            Assert.areEqual(listRecommendationAction[0].Recommendation__r.Name, responseDTO[0].sAlertName);
        Test.StopTest();
    }
}