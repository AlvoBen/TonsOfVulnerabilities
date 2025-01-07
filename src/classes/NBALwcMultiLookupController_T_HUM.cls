/*******************************************************************************************************************************
Class Name    : NBALwcMultiLookupController_T_HUM 
Version         : 1.0
Created On      : 5/6/2021
Function        : Test Class for GetNBARecommendations
                  
Modification Log: 
* Developer Name         Code Review              Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Apurva Urkude                                   5/5/2021                  original version 
********************************************************************************************************************************/

@isTest(seeAllData= false)
public class NBALwcMultiLookupController_T_HUM  {

    /*
    * Method Name : setUpTestData
    * Description : This method creates test data for test class
    * Return type : None
    * Parameter   : None
    */
    
     @testSetup static void setUpTestData() {
        
        //User
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        oUser.Current_Queue__c = 'Cincinnati CRU1';
        oUser.Network_User_Id__c = 'SXS2222';
        insert oUser;
        System.assertEquals(oUser.LastName, 'test User1');
        
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
        
        System.assertEquals(oAcc1.id!=null, true);
        System.assertEquals(oAcc1.Agent_Type__c ,'Broker','Value not matched');
        
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
        System.assertEquals(oInte1.id!=null, true);
        System.assertEquals(oInte1.Interacting_With_type__c ,'Member','Value not matched');
        
        
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
         
         //Switch Data
          CRMFunctionality_ONOFF_Switch__c crm3 = new CRMFunctionality_ONOFF_Switch__c();
            crm3.IsON__c =true;
            crm3.Name = '2164068';
            crm3.Scrum_Team__c = 'Lego';
            crm3.Story_Title__c = '    REQ -2164068 NBA Checkmarx Fix switch';
            crm3.Release_Name__c = '05/28/2021';
            insert crm3;
        
        //Standard Controller
        ApexPages.StandardController sc = new ApexPages.StandardController(oAcc);
         NBALwcMultiLookupController myController = new NBALwcMultiLookupController();
        String recNameTemp='abcd';
        Id recIdTemp=listOfRecommendation[0].Id;
        NBALwcMultiLookupController.SObjectResult sobj1= new NBALwcMultiLookupController.SObjectResult();
        NBALwcMultiLookupController.SObjectResult sobj= new NBALwcMultiLookupController.SObjectResult(recNameTemp,recIdTemp);
           }

  
    @isTest
    public static void testgetResults() { 
        Test.StartTest();
        NBALwcMultiLookupController myController = new NBALwcMultiLookupController();
        List<Recommendation> listOfRecom = new List<Recommendation>();
        listOfRecom.add(new Recommendation(Name = 'Recom2', Alert_Type__c = 'Guidance', Show_To__c = 'Member', Description = 'context 1',  Message__c = 'dynamic1',
                                                    Location__c = 'All Pages', AcceptanceLabel = 'Accept',  RejectionLabel = 'Reject', ActionReference = 'Save_Encrypted_Flow',
                                                    Interacting_With_Type__c = 'Member',priority__c =1));
        insert listOfRecom;
        String recNameTemp='abcd';
        Id recIdTemp=listOfRecom[0].Id;
        NBALwcMultiLookupController.SObjectResult sobj1= new NBALwcMultiLookupController.SObjectResult();
        NBALwcMultiLookupController.SObjectResult sobj= new NBALwcMultiLookupController.SObjectResult(recNameTemp,recIdTemp);
        String ObjectName='Work_Queue_Setup__c';
        String fieldName='Name';
        String value='lou';
        List<String> selectedRecId = new List<String>();
        selectedRecId.add(listOfRecom[0].Id);        
        NBALwcMultiLookupController.getResults(ObjectName,fieldName,value,selectedRecId);
        Test.StopTest();
        
    }
  
    
    @isTest
    public static void testgetResultsnegative() {
        Test.StartTest();
         //Switch Data
          CRMFunctionality_ONOFF_Switch__c crm3 = new CRMFunctionality_ONOFF_Switch__c();
            crm3.IsON__c =false;
            crm3.Name = '2164068';
            crm3.Scrum_Team__c = 'Lego';
            crm3.Story_Title__c = '    REQ -2164068 NBA CheckMarx Fix switch';
            crm3.Release_Name__c = '05/28/2021';
            insert crm3;
        String ObjectName='Work_Queue_Setup__c';
        String fieldName='Name';
        String value='X';
       List<String> selectedRecId = new List<String>();
       selectedRecId=NULL;
        NBALwcMultiLookupController.getResults(ObjectName,fieldName,value,selectedRecId);
        Test.stopTest();
    }
    
    @isTest
    public static void TestsaveJunctionRecord()
    {
    Test.StartTest();
          List<Recommendation> RecommId =[SELECT Id FROM Recommendation];     
           String recId = String.valueOf(RecommId);
        
         List<Work_Queue_Setup__c> workQueudIds = [SELECT Id FROM Work_Queue_Setup__c limit 1];  
        List<string>  workQueudIdsToSave= new List<string> ();
         
        for(Work_Queue_Setup__c a: workQueudIds){
          workQueudIdsToSave.add(String.valueOf(a));
        }
         List<Recommendation_Work_Queue_Juntion__c> recIdtosave = [Select Id, Name, Work_Queue_Setup__c  From Recommendation_Work_Queue_Juntion__c 
                                                                                      Where Recommendation__c =: recId ];
                    
    NBALwcMultiLookupController.saveJunctionRecord(recId,workQueudIdsToSave);
    Test.StopTest();
    }
        @isTest
    public static void TestNegativesaveJunctionRecord()
    {
    Test.StartTest();
        //Switch Data
          CRMFunctionality_ONOFF_Switch__c crm3 = new CRMFunctionality_ONOFF_Switch__c();
            crm3.IsON__c =false;
            crm3.Name = '2164068';
            crm3.Scrum_Team__c = 'Lego';
            crm3.Story_Title__c = '    REQ -2164068 NBA CheckMarx Fix Switch';
            crm3.Release_Name__c = '05/28/2021';
            insert crm3;
        List<Recommendation> RecommId =[SELECT Id FROM Recommendation];     
        String recId = String.valueOf(RecommId);

         List<Work_Queue_Setup__c> workQueudIds = [SELECT Id FROM Work_Queue_Setup__c limit 1];  
        List<string>  workQueudIdsToSave= new List<string> ();
         
        for(Work_Queue_Setup__c a: workQueudIds){
          workQueudIdsToSave.add(String.valueOf(a));
        }
         List<Recommendation_Work_Queue_Juntion__c> recIdtosave = [Select Id, Name, Work_Queue_Setup__c  From Recommendation_Work_Queue_Juntion__c 
                                                                                      Where Recommendation__c =: recId ];
                    
    NBALwcMultiLookupController.saveJunctionRecord(recId,workQueudIdsToSave);
    Test.StopTest();
    }
    

    public static testMethod void testloadWorkQueueRecord(){
         Test.startTest();
        List<Recommendation> RecommId =[SELECT Id FROM Recommendation];     
        String recId = String.valueOf(RecommId);
 
       List<Recommendation_Work_Queue_Juntion__c> listOfworkQueues = [Select Work_Queue_Setup__c, Recommendation__r.Name, Work_Queue_Setup__r.Name From Recommendation_Work_Queue_Juntion__c 
            Where Recommendation__c =:recId];

        NBALwcMultiLookupController.loadWorkQueueRecord(recId);        
          Test.stopTest();
    }
    
    
   
    public static testMethod void getNameOfRecommendation () {
   
         Test.startTest();
        List<Recommendation> recommendationList =  [SELECT Id, Name FROM Recommendation LIMIT 1];  
        String recomm= recommendationList[0].Id; 
        NBALwcMultiLookupController.getNameOfRecommendation(recomm);
          Test.stopTest();
    } 
}