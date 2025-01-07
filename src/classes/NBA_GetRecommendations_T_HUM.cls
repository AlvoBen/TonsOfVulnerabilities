/*******************************************************************************************************************************
Class Name    : NBA_GetRecommendations_T_HUM 
Version         : 1.0
Created On      : 10/20/2020
Function        : Test Class for GetNBARecommendations
                  
Modification Log: 
* Developer Name         Code Review              Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Sayali Nemade                                   10/20/2020              original version - 92% Coverage
********************************************************************************************************************************/

@isTest(seeAllData= false)
public class NBA_GetRecommendations_T_HUM {

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
        
        RecordType oMemRecType = [SELECT Id FROM RecordType WHERE Name = 'Member' Limit 1];
        RecordType oGroupRecType = [SELECT Id FROM RecordType WHERE Name = 'Group' Limit 1];
        
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
		oAcc1.Group_Number__c = '1234';
		oAcc1.Name = 'AccountG';
		oAcc1.recordtypeid = oGroupRecType.Id;
		oAcc1.Birthdate__c = '01/01/1984';
		oAcc1.Enterprise_ID__c='1030693160';
		oAcc1.Home_Office_Account__c = false;
		oAcc1.Mbr_Gen_Key__c = '334567';
		insert oAcc1;
		
		System.AssertNotEquals(oAcc, NULL);

        Interaction__c oInte1 = HUMTestDataHelperTest.getInteraction();
        oInte1.Interacting_With__c = oAcc.Id;
        oInte1.Interacting_With_type__c = 'Member';
        insert oInte1;
        System.assertEquals(oInte1.id!=null, true);
        System.assertEquals(oInte1.Interacting_With_type__c ,'Member','Value not matched');
        	
        	
        //Medical Product
        Product2 objMedProduct = new Product2();
        objMedProduct.Name ='MED';
        objMedProduct.Product_Type__c = 'Med';
        objMedProduct.productCode = 'MED';
        objMedProduct.Sold_Product_Key_Value__c = 'MC3HH016';
        insert objMedProduct;
        System.assert(objMedProduct.Product_Type__c == 'Med');
        
       	Policy__c oMedicalPolicy = HUMTestDataHelperTest.getPolicy();
        oMedicalPolicy.Name = 'Medical Product';
        oMedicalPolicy.source_cust_cov_key__c = '12345678901234567890' ; 
        oMedicalPolicy.Group_Name__c = oAcc1.Id;
        oMedicalPolicy.Product__c = objMedProduct.Id;  
        oMedicalPolicy.Major_LOB__c ='EPO';
        oMedicalPolicy.Platform__c = 'EM';//SC - Added    
        oMedicalPolicy.Benefit_Coverage__c = 'SFA03104';  //SC - Added
		oMedicalPolicy.Source_Cust_Cov_Key__c='abc';			
        insert oMedicalPolicy;
        System.assert(oMedicalPolicy.Name == 'Medical Product');
        
        List<Recommendation> listOfRecommendation = new List<Recommendation>();
        listOfRecommendation.add(new Recommendation(Name = 'Recom1', Alert_Type__c = 'Guidance', Show_To__c = 'Member', Description = 'context 1',  Message__c = 'dynamic1',
                                                    Location__c = 'All Pages', AcceptanceLabel = 'Accept',  RejectionLabel = 'Reject', ActionReference = 'Save_Encrypted_Flow',
                                                    Interacting_With_Type__c = 'Member'));
        listOfRecommendation.add(new Recommendation(Name = 'Recom2', Alert_Type__c = 'Guidance', Show_To__c = 'Member', Description = 'context 1',  Message__c = 'dynamic1',
                                                    Location__c = 'All Pages', AcceptanceLabel = 'Accept',  RejectionLabel = 'Reject', ActionReference = 'Save_Encrypted_Flow',
                                                    Interacting_With_Type__c = 'Member',Link_Name_1__c='Disease Management Programs Overview',
                                                   Link_Path_1__c = 'https://dctm.humana.com/Mentor/xWeb/viewtopic.aspx?sChronicleID=0900092982d32129&dl=0&searchID=VI-8d7dae40dff2b41&row=0&mode=Mentor&launchId=1586272136538',
                                                   Link_Name_2__c='Disease Management', Link_Path_2__c = 'https://test.com',Link_Name_3__c = 'Management',
                                                   Link_Path_3__c = 'https://test1.com',Link_Name_4__c='Campaign', Link_Path_4__c = 'https://test4.com',Link_Name_5__c = 'Health Management',
                                                   Link_Path_5__c = 'https://test5.com'));
        insert listOfRecommendation;
        
        List<Recommendation_Detail__c> listOfRecommendationDetail = new List<Recommendation_Detail__c>();
        listOfRecommendationDetail.add(new Recommendation_Detail__c( Account__c = oAcc.id, Enterprise_ID__c = oAcc.Enterprise_ID__c,
                                                                   Action__c = 'Acknowledged',Start_Date__c = Date.newInstance(2016, 12, 9), End_Date__c = Date.newInstance(2099, 12, 9),
                                                                   Type__c='Member', External_Id__c='1234555' , Recommendation__c = listOfRecommendation[0].Id));
        listOfRecommendationDetail.add(new Recommendation_Detail__c( Account__c = oAcc.id, Enterprise_ID__c = oAcc.Enterprise_ID__c,
                                                                   Action__c = 'Acknowledged',Start_Date__c = Date.newInstance(2016, 12, 9), End_Date__c = Date.newInstance(2099, 12, 9),
                                                                   Type__c='Member', External_Id__c='1234555121' , Recommendation__c = listOfRecommendation[1].Id));
        insert listOfRecommendationDetail;
        
        List<Recommendation_Action__c> listOfRecommendationAction = new List<Recommendation_Action__c>();
        listOfRecommendationAction.add(new Recommendation_Action__c(Name = 'RecomAction1', Action__c = 'Acknowledged', Recommendation__c = listOfRecommendation[0].Id,
                                                                   Interaction__c = oInte1.Id));
        listOfRecommendationAction.add(new Recommendation_Action__c(Name = 'RecomAction2', Action__c = 'Acknowledged', Recommendation__c = listOfRecommendation[0].Id,
                                                                   Interaction__c = oInte1.Id,Recommendation_Detail__c = listOfRecommendationDetail[0].Id));
        listOfRecommendationAction.add(new Recommendation_Action__c(Name = 'RecomAction2', Action__c = 'Acknowledged', Recommendation__c = listOfRecommendation[1].Id,
                                                                   Interaction__c = oInte1.Id,Recommendation_Detail__c = listOfRecommendationDetail[1].Id));
        listOfRecommendationAction.add(new Recommendation_Action__c(Name = 'RecomAction3', Action__c = 'Acknowledged', Recommendation__c = listOfRecommendation[1].Id,
                                                                   Interaction__c = oInte1.Id,Recommendation_Detail__c = listOfRecommendationDetail[1].Id));
        insert listOfRecommendationAction;
        
        
     }
    
    /*
    * Method Name : getListOfRecommendationsToDisplayTest
    * Description : This method is used to test the constructor GetNBARecommendations_T_HUM class.
    * Return type : None
    * Parameter   : None
    */
    @isTest
    static void getListOfRecommendationsToDisplayTest()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        List<Recommendation_Action__c> listOfRecommendationAct = [Select Id, Name from Recommendation_Action__c];
        System.runAs(testUser)
        {
            Test.StartTest();
            NBA_GetRecommendations nbaRecomms = new NBA_GetRecommendations();
            
            list<list<NBA_Recommendation_Wrapper>> lstRecommendationsToDisplay = new  list<list<NBA_Recommendation_Wrapper>>(); 
        	List<NBA_Recommendation_Wrapper> lstRecommendations = new List<NBA_Recommendation_Wrapper>();
        	NBA_Recommendation_Wrapper objrecommendation = new NBA_Recommendation_Wrapper();
            lstRecommendations.add(objrecommendation);
            lstRecommendationsToDisplay.add(lstRecommendations);
            
            List<List<String>> RecommAction = new List<List<String>>();
            List<String> lstRecommAct = new List<String>();
            lstRecommAct.add(listOfRecommendationAct[0].id);
            RecommAction.add(lstRecommAct);
            NBA_GetRecommendations.getListOfRecommendationsToDisplay(RecommAction);
            
            List<List<String>> RecommAction1 = new List<List<String>>();
            List<String> lstRecommAct1 = new List<String>();
            lstRecommAct1.add(listOfRecommendationAct[1].id);
            RecommAction1.add(lstRecommAct1);
            NBA_GetRecommendations.getListOfRecommendationsToDisplay(RecommAction1);
            Test.StopTest();
        }
    }
    
     /*
    * Method Name : getListOfRecommendationsToDisplayTest
    * Description : This method is used to test the constructor GetNBARecommendations_T_HUM class.
    * Return type : None
    * Parameter   : None
    */
    @isTest
    static void getListOfRecommendationsToDisplayTestNeg()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        
        List<Recommendation_Action__c> listOfRecommendationAct = [Select Id, Name from Recommendation_Action__c];
        System.runAs(testUser)
        {
            Test.StartTest();
            NBA_GetRecommendations nbaRecomms = new NBA_GetRecommendations();
            
            list<list<NBA_Recommendation_Wrapper>> lstRecommendationsToDisplay = new  list<list<NBA_Recommendation_Wrapper>>(); 
        	List<NBA_Recommendation_Wrapper> lstRecommendations = new List<NBA_Recommendation_Wrapper>();
        	NBA_Recommendation_Wrapper objrecommendation = new NBA_Recommendation_Wrapper();
            lstRecommendations.add(objrecommendation);
            lstRecommendationsToDisplay.add(lstRecommendations);
            
            List<List<String>> RecommAction = new List<List<String>>();
            List<String> lstRecommAct = new List<String>();
            lstRecommAct.add(listOfRecommendationAct[2].id);
            RecommAction.add(lstRecommAct);
            NBA_GetRecommendations.getListOfRecommendationsToDisplay(RecommAction);
            
            List<List<String>> RecommAction1 = new List<List<String>>();
            List<String> lstRecommAct1 = new List<String>();
            lstRecommAct1.add(listOfRecommendationAct[3].id);
            RecommAction1.add(lstRecommAct1);
            NBA_GetRecommendations.getListOfRecommendationsToDisplay(RecommAction1);
            Test.StopTest();
        }
    }
}