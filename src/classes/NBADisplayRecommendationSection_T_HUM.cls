/*******************************************************************************************************************************
Class Name    : NBADisplayRecommendationSection_T_HUM 
Version         : 1.0
Created On      : 10/20/2020
Function        : Test Class for NBADisplayRecommendationSection_C_HUM
                  
Modification Log: 
* Developer Name         Code Review              Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Sayali Nemade                                   10/20/2020              original version - 90% Coverage
* Aishwarya Pawar								27/4/2022				  REQ 2948386

********************************************************************************************************************************/

@isTest(seeAllData= false)
public class NBADisplayRecommendationSection_T_HUM {
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
        
        RecordType AccountRecordType1 = [Select Id from RecordType where Name='Agent/Broker' and sObjectType='Account'];
        
        Account oAcc1 = HUMTestDataHelperTest.getAccount();
        oAcc1.Name = 'Test Account Cases';
        oAcc1.OwnerId = testUser.id;
        oAcc1.Enterprise_Id__c = '123456';
        oAcc1.recordtypeid = AccountRecordType1.id;
        oAcc1.Agent_Type__c = 'Broker';
        oAcc1.Birthdate__c = '12/31/2088';
        insert oAcc1;
        System.assertEquals(oAcc1.id!=null, true);
        System.assertEquals(oAcc1.Agent_Type__c ,'Broker','Value not matched');

        Interaction__c oInte1 = HUMTestDataHelperTest.getInteraction();
        oInte1.Interacting_With__c = oAcc1.Id;
        oInte1.Interacting_With_type__c = 'Agent';
        insert oInte1;
        System.assertEquals(oInte1.id!=null, true);
        System.assertEquals(oInte1.Interacting_With_type__c ,'Agent','Value not matched');
        	
        //Medical Product
        Product2 objMedProduct = new Product2();
        objMedProduct.Name ='MCD';
        objMedProduct.Product_Type__c = 'MCD';
        objMedProduct.productCode = 'MCD';
        objMedProduct.Sold_Product_Key_Value__c = 'MC3HH016';
        insert objMedProduct;
        System.assert(objMedProduct.Product_Type__c == 'MCD');
        System.assertEquals(objMedProduct.id!=null, true);
        
       	Policy__c oMedicalPolicy = HUMTestDataHelperTest.getPolicy();
        oMedicalPolicy.Name = 'Medical Product';
        oMedicalPolicy.source_cust_cov_key__c = '12345678901234567890' ; 
        oMedicalPolicy.Group_Name__c = oAcc1.Id;
        oMedicalPolicy.Product__c = objMedProduct.Id;  
        oMedicalPolicy.Major_LOB__c ='EPO';
        oMedicalPolicy.Platform__c = 'EM';//SC - Added    
        oMedicalPolicy.Benefit_Coverage__c = 'SFA03104';  //SC - Added
		oMedicalPolicy.Source_Cust_Cov_Key__c='abc';
        oMedicalPolicy.Issue_State__c='OH';
        insert oMedicalPolicy;
        System.assert(oMedicalPolicy.Name == 'Medical Product');
        
        Policy_Member__c objMedPolMem = HUMTestDataHelperTest.getPolicyMember();
        objMedPolMem.Member__c = oAcc1.Id;
        objMedPolMem.Policy__c = oMedicalPolicy.Id;
        objMedPolMem.Relationship__c = 'self';
        objMedPolMem.end_date__c = '12/31/2015';
        objMedPolMem.Effective_Date__c = '01/01/2015';
      	objMedPolMem.Policy_Member_External_ID__c = '00|ODS|123456789|89|89|02-01-2001';
        objMedPolMem.Redetermination_Date__c = Date.valueOf('2022-06-05');
       	insert objMedPolMem;
       System.assert(objMedPolMem.Relationship__c == 'self');
        
        List<Recommendation> listOfRecommendation = new List<Recommendation>();
        listOfRecommendation.add(new Recommendation(Name = 'Recom1', Alert_Type__c = 'Guidance', Show_To__c = 'Member', Description = 'context 1',  Message__c = 'dynamic1',
                                                    Location__c = 'All Pages', AcceptanceLabel = 'Accept',  RejectionLabel = 'Reject', ActionReference = 'Save_Encrypted_Flow',
                                                    Interacting_With_Type__c = 'Member'));
        
        listOfRecommendation.add(new Recommendation(Name = 'Recom2', Alert_Type__c = 'Command', Show_To__c = 'Member', Description = 'context 1',  Message__c = 'dynamic1',
                                                    Location__c = 'All Pages', AcceptanceLabel = 'Accept',  RejectionLabel = 'Reject', ActionReference = 'Save_Encrypted_Flow',
                                                    Interacting_With_Type__c = 'Member'));
        insert listOfRecommendation;
        
        List<Recommendation_Action__c> listOfRecommendationAction = new List<Recommendation_Action__c>();
        listOfRecommendationAction.add(new Recommendation_Action__c(Name = 'RecomAction1', Action__c = 'Acknowledged', Recommendation__c = listOfRecommendation[0].Id,
                                                                   Interaction__c = oInte1.Id));
       	listOfRecommendationAction.add(new Recommendation_Action__c(Name = 'RecomAction2', Action__c = 'Acknowledged', Recommendation__c = listOfRecommendation[1].Id,
                                                                   Interaction__c = oInte1.Id));
       	insert listOfRecommendationAction;
    }
    
    /*
    * Method Name : initTest
    * Description : This method is used to test the constructor NBADisplayRecommendationSection_C_HUM class.
    * Return type : None
    * Parameter   : None
    */
    @isTest
    static void initTest()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        Account acc = [Select Id,Name from Account where Name='Test Account Cases' LIMIT 1];
        Policy__c pol = [Select Id, Name from Policy__c where Name = 'Medical Product' LIMIT 1];
        Interaction__c testTnt = [Select Id,Name from Interaction__c where Interacting_With_type__c='Agent' limit 1];
        List<Recommendation> listOfRecommendation = [Select Id, Name from Recommendation];
        List<Recommendation_Action__c> listOfRecommendationAct = [Select Id, Name,Interaction__c,Recommendation__c from Recommendation_Action__c where Name = 'RecomAction1'];
        List<Recommendation_Action__c> listOfCommandRecommendationAct = [Select Id, Name,Interaction__c,Recommendation__c from Recommendation_Action__c where Name = 'RecomAction2'];

        System.runAs(testUser)
        {
            Test.StartTest();
            NBADisplayRecommendationSection_C_HUM nbaRecommDisplay = new NBADisplayRecommendationSection_C_HUM();
            PageReference pageRef = Page.NBA_Recommendation_Display_VF_HUM;
            Test.setCurrentPage(pageRef);
            ApexPages.CurrentPage().getParameters().put('id',acc.id);
            ApexPages.CurrentPage().getParameters().put('sPageName','Group');
            ApexPages.CurrentPage().getParameters().put('polId',pol.id);
            nbaRecommDisplay.bInteractionId = true;
            nbaRecommDisplay.interactionId = testTnt.Id;
            nbaRecommDisplay.lstRecommendationActionId = listOfRecommendation[0].Id;
            nbaRecommDisplay.sAlertCount = 1;
            nbaRecommDisplay.tabId = true;
            listOfRecommendationAct[0].Interaction__c = testTnt.Id;
            nbaRecommDisplay.URLString = 'https://humanaservice--alert.my.salesforce.com/0012900000YdTMf?ent=Account&interactionId=a0U29000007nskiEAA';
            nbaRecommDisplay.init();
            nbaRecommDisplay.updateRecommnedationBasedOnAction();
            Test.StopTest();
        }
       
    }
    /*
    * Method Name : NegativeInitTest
    * Description : This method is used to test the constructor NBADisplayRecommendationSection_C_HUM class.
    * Return type : None
    * Parameter   : None
    */
    @isTest
    static void NegativeInitTest()
    {
         User testUser = [Select Id,Name from User where LastName='test User1'];
        Account acc = [Select Id,Name from Account where Name='Test Account Cases' LIMIT 1];
        List<Recommendation> listOfRecommendation = [Select Id, Name from Recommendation];
        List<Recommendation_Action__c> listOfRecommendationAct = [Select Id, Name,Interaction__c,Recommendation__c from Recommendation_Action__c where Name = 'RecomAction1'];
        List<Recommendation_Action__c> listOfCommandRecommendationAct = [Select Id, Name,Interaction__c,Recommendation__c from Recommendation_Action__c where Name = 'RecomAction2'];

        System.runAs(testUser)
        {
            Test.StartTest();
            NBADisplayRecommendationSection_C_HUM nbaRecommDisplay = new NBADisplayRecommendationSection_C_HUM();
            PageReference pageRef = Page.NBA_Recommendation_Display_VF_HUM;
            Test.setCurrentPage(pageRef);
            ApexPages.CurrentPage().getParameters().put('id',acc.id);
            ApexPages.CurrentPage().getParameters().put('sPageName','Person Account');
            nbaRecommDisplay.bInteractionId = false;
            nbaRecommDisplay.interactionId = null;
            nbaRecommDisplay.lstRecommendationActionId = listOfRecommendation[0].Id;
            nbaRecommDisplay.sAlertCount = 1;
            nbaRecommDisplay.tabId = true;
            nbaRecommDisplay.URLString = 'https://humanaservice--alert.my.salesforce.com/0012900000YdTMf?ent=Account';
            nbaRecommDisplay.init();
            nbaRecommDisplay.updateRecommnedationBasedOnAction();
            Test.StopTest();
        }
        
    }
 }