/*******************************************************************************************************************************
Class Name    : NBARecommendationDetail_T_HUM 
Version         : 1.0
Created On      : 11/03/2020
Function        : Test Class for NBARecommendationDetail_G_HUM Trigger
                  
Modification Log: 
* Developer Name         Code Review              Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Sayali Nemade                                    11/03/2020                original version - 100% Coverage
* Apurva Urkude                                   06/09/2021                 fix the code coverage
********************************************************************************************************************************/

@isTest(seeAllData= false)
private class NBARecommendationDetail_T_HUM {
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
        system.debug('ACC ID'+oAcc1.Id);
		
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
        listOfRecommendation.add(new Recommendation(Name = 'Recom1', Alert_Type__c = 'Guidance', Show_To__c = 'Group + Member', Description = 'context 1',  Message__c = 'dynamic1',
                                                    Location__c = 'All Pages', AcceptanceLabel = 'Accept',  RejectionLabel = 'Reject', ActionReference = 'Save_Encrypted_Flow',
                                                    Interacting_With_Type__c = 'Member'));
        listOfRecommendation.add(new Recommendation(Name = 'Recom2', Alert_Type__c = 'Guidance', Show_To__c = 'Group + Member', Description = 'context 1',  Message__c = 'dynamic1',
                                                    Location__c = 'All Pages', AcceptanceLabel = 'Accept',  RejectionLabel = 'Reject', ActionReference = 'Save_Encrypted_Flow',
                                                    Interacting_With_Type__c = 'Member',Link_Name_1__c='Disease Management Programs Overview',
                                                   Link_Path_1__c = 'https://dctm.humana.com/Mentor/xWeb/viewtopic.aspx?sChronicleID=0900092982d32129&dl=0&searchID=VI-8d7dae40dff2b41&row=0&mode=Mentor&launchId=1586272136538',
                                                   Link_Name_2__c='Disease Management', Link_Path_2__c = 'https://test.com',Link_Name_3__c = 'Management',
                                                   Link_Path_3__c = 'https://test1.com',Link_Name_4__c='Campaign', Link_Path_4__c = 'https://test4.com',Link_Name_5__c = 'Health Management',
                                                   Link_Path_5__c = 'https://test5.com'));
        listOfRecommendation.add(new Recommendation(Name = 'Recom3', Alert_Type__c = 'Guidance', Show_To__c = 'Group + Member', Description = 'context 1',  Message__c = 'dynamic1',
                                                    Location__c = 'Business Account', AcceptanceLabel = 'Accept',  RejectionLabel = 'Reject', ActionReference = 'Save_Encrypted_Flow',
                                                    Interacting_With_Type__c = 'Group'));
        
        listOfRecommendation.add(new Recommendation(Name = 'Recom4', Alert_Type__c = 'Guidance', Show_To__c = 'Group + Member', Description = 'context 1',  Message__c = 'dynamic1',
                                                    Location__c = 'All Pages', AcceptanceLabel = 'Accept',  RejectionLabel = 'Reject', ActionReference = 'Save_Encrypted_Flow',
                                                    Interacting_With_Type__c = 'Member', Active__c = true));
        insert listOfRecommendation;
        
        List<Recommendation_Detail__c> listOfRecommendationDetail = new List<Recommendation_Detail__c>();
        listOfRecommendationDetail.add(new Recommendation_Detail__c( Account__c = oAcc.id, Enterprise_ID__c = oAcc.Enterprise_ID__c,
                                                                   Action__c = 'Acknowledged',Start_Date__c = Date.newInstance(2016, 12, 9), End_Date__c = Date.newInstance(2099, 12, 9),
                                                                   Type__c='Member', External_Id__c='1234555' , Recommendation__c = listOfRecommendation[0].Id));
        listOfRecommendationDetail.add(new Recommendation_Detail__c( Account__c = oAcc.id, Enterprise_ID__c = oAcc.Enterprise_ID__c, 
                                                                   Action__c = 'Acknowledged',Start_Date__c = Date.newInstance(2016, 12, 9), End_Date__c = Date.newInstance(2099, 12, 9),
                                                                   Type__c='Member', External_Id__c='1234555121' , Recommendation__c = listOfRecommendation[1].Id));
        
        listOfRecommendationDetail.add(new Recommendation_Detail__c( Account__c = oAcc1.id, Enterprise_ID__c = oAcc1.Enterprise_ID__c,
                                                                   Action__c = 'Acknowledged',Start_Date__c = Date.newInstance(2016, 12, 9), End_Date__c = Date.newInstance(2099, 12, 9),
                                                                   Type__c='Group', External_Id__c='12345551215' , Recommendation__c = listOfRecommendation[2].Id));
        
        listOfRecommendationDetail.add(new Recommendation_Detail__c( Account__c = oAcc1.id, Enterprise_ID__c = oAcc1.Enterprise_ID__c,
                                                                   Action__c = 'Acknowledged',Start_Date__c = Date.newInstance(2016, 12, 9), End_Date__c = Date.newInstance(2099, 12, 9),
                                                                   Type__c='Group', External_Id__c='123455512157' ,Active__c =true, Recommendation__c = listOfRecommendation[3].Id));
        
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
        
        listOfRecommendationAction.add(new Recommendation_Action__c(Name = 'RecomAction3', Action__c = 'Acknowledged', Recommendation__c = listOfRecommendation[2].Id,
                                                                   Interaction__c = oInte1.Id,Recommendation_Detail__c = listOfRecommendationDetail[2].Id));
        
        listOfRecommendationAction.add(new Recommendation_Action__c(Name = 'RecomAction3', Action__c = 'Acknowledged', Recommendation__c = listOfRecommendation[2].Id,
                                                                   Recommendation_Detail__c = listOfRecommendationDetail[2].Id));
        insert listOfRecommendationAction;
        
        Policy_Member__c objPolMemLV = HUMTestDataHelperTest.getPolicyMember();
        objPolMemLV.Member__c = oAcc.Id;
        objPolMemLV.Policy__c = oMedicalPolicy.Id;
        objPolMemLV.Relationship__c = 'self';
        objPolMemLV.name = '12591';
        objPolMemLV.end_date__c = '12/31/2025';
        objPolMemLV.Effective_Date__c = '01/01/2020';
        objPolMemLV.Policy_Member_External_ID__c = '00|ODS|123456785|89|89|02-01-2001';
        insert objPolMemLV;
        System.assert(objPolMemLV.Relationship__c == 'self');
    }
    
    
    /*
    * Method Name : populateExternalIdAndAccountTest
    * Return type : None
    * Parameter   : None
    */
    static testMethod void populateExternalIdAndAccountTest() {
        test.startTest();
        List<Account> lstAcc = [Select Id,Name,Enterprise_Id__c from Account];
        
        List<Recommendation_Detail__c> listOfRecommendationDetailExisting = [Select Id, Name, Action__c, External_Id__c, Account__c From Recommendation_Detail__c];
        
        List<Recommendation_Detail__c> listOfRecommendationDetailNew = new List<Recommendation_Detail__c>();
        listOfRecommendationDetailNew.add(new Recommendation_Detail__c(Account__c = lstAcc[0].id, Enterprise_ID__c = lstAcc[0].Enterprise_ID__c,
                                                                   Action__c = 'Acknowledged',Start_Date__c = Date.newInstance(2016, 12, 9), End_Date__c = Date.newInstance(2099, 12, 9),
                                                                   Type__c='Member',GenKey__c = '123', External_Id__c='123'));
   NBARecommendationDetail_H_HUM.populateExternalIdAndAccount(listOfRecommendationDetailNew);        
        
        test.stopTest();
    }
    
    /*
    * Method Name : populateExternalIdAndAccountNegTest
    * Return type : None
    * Parameter   : None
    */
    static testMethod void populateExternalIdAndAccountNegTest() {
        test.startTest();
        List<Account> lstAcc = [Select Id,Name,Enterprise_Id__c from Account];
        
        List<Recommendation_Detail__c> listOfRecommendationDetailExisting = [Select Id, Name, Action__c, External_Id__c, Account__c From Recommendation_Detail__c];
        
        List<Recommendation_Detail__c> listOfRecommendationDetailNew = new List<Recommendation_Detail__c>();
        listOfRecommendationDetailNew.add(new Recommendation_Detail__c(Account__c = lstAcc[0].id, Enterprise_ID__c = lstAcc[0].Enterprise_ID__c,
                                                                   Action__c = 'Acknowledged',Start_Date__c = Date.newInstance(2016, 12, 9), End_Date__c = Date.newInstance(2099, 12, 9),
                                                                   Type__c='Member',GenKey__c = '123'));
         listOfRecommendationDetailNew.add(new Recommendation_Detail__c(Account__c = lstAcc[0].id, Enterprise_ID__c = lstAcc[0].Enterprise_ID__c,
                                                                   Action__c = 'Acknowledged',Start_Date__c = Date.newInstance(2016, 12, 9), End_Date__c = Date.newInstance(2099, 12, 9),
                                                                   Type__c='Group',GenKey__c = '12673'));
         listOfRecommendationDetailNew.add(new Recommendation_Detail__c( Enterprise_ID__c = lstAcc[0].Enterprise_ID__c,
                                                                   Action__c = 'Acknowledged',Start_Date__c = Date.newInstance(2016, 12, 9), End_Date__c = Date.newInstance(2099, 12, 9),
                                                                   Type__c='Group',GenKey__c = '12673'));
    
   NBARecommendationDetail_H_HUM.populateExternalIdAndAccount(listOfRecommendationDetailNew);        
        
        test.stopTest();
    }
}