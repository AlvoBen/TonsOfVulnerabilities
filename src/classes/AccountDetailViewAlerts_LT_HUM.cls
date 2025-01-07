/**************************************************************************** 
Apex Class Name  : AccountDetailViewAlerts_LT_HUM
Version          : 1.0  
Created Date     : August 04/14/2021
Function         : class for dispaly Alerts on account detail page.
Controller class : AccountDetailViewAlerts_LC_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Ritik Agarwal									04/14/2021                 Original Version
* Kajal Namdev									06/11/2021                 Test Failure Fix
* Aishwarya Pawar								10/21/2021                 REQ-268408
* Aishwarya Pawar								27/4/2022				  REQ 2948386
*Aishwarya Pawar                    			05/13/2022                REQ - 3285223 Alert Configuration on Plan Member Page
* Abhishek Mangutkar							07/04/2022				  REQ 3246031
* Nilesh Gadkar					                05/04/2023			      User Story 3421330: T1PRJ0881713 - FR09.09: CRM Service must create and display a command alert notification on the Person Account Page when the HRA flag/indicator = N	

***************************************************************************************************/
@isTest(seeAllData= false)
public With Sharing class AccountDetailViewAlerts_LT_HUM {
    
    @testSetup static void setUpTestData()
    {
        User uObj = [select id from User Where Profile.Name = 'System Administrator' And IsActive=true Limit 1];
        system.runAs(uObj)
        {
            RecordType oMemRecType = [SELECT Id,Name FROM RecordType WHERE Name = 'Member' Limit 1];
            Test.startTest();
            Account oAcc = new Account();
            oAcc.Enterprise_ID__c='1030693160';
            oAcc.Mbr_Gen_Key__c = '1234567890';
            oAcc.recordtypeid = oMemRecType.Id;
            oAcc.firstName = 'Daphiner';
            oAcc.lastName = 'Superman';
			oAcc.Birthdate__c = '02/10/1974';
            insert oAcc;
            System.AssertNotEquals(oAcc, NULL);
            System.AssertEquals(oAcc.firstName, 'Daphiner');
            System.AssertEquals(oAcc.lastName, 'Superman');
            
            Interaction__c oInte1 = HUMTestDataHelperTest.getInteraction();
            oInte1.Interacting_With__c = oAcc.Id;
            oInte1.Interacting_With_type__c = 'Member';
            insert oInte1;
            System.assertEquals(oInte1.id!=null, true);
            System.assertEquals(oInte1.Interacting_With_type__c ,'Member');
            
            List<Recommendation> listOfRecommendation = new List<Recommendation>();
            listOfRecommendation.add(new Recommendation(Name = 'Recom1', Alert_Type__c = 'Guidance', Show_To__c = 'Member', Description = 'context 1',  Message__c = 'dynamic1',
                                                        Location__c = 'All Pages', AcceptanceLabel = 'Accept',  RejectionLabel = 'Reject', ActionReference = 'Save_Encrypted_Flow',
                                                        Interacting_With_Type__c = 'Member'));
            insert listOfRecommendation;
            System.AssertNotEquals(listOfRecommendation, NULL);
            System.AssertEquals(listOfRecommendation[0].Name, 'Recom1');
            System.assertEquals(listOfRecommendation[0].Alert_Type__c,'Guidance');
            System.assertEquals(listOfRecommendation[0].Show_To__c, 'Member');
            
            
            List<Recommendation_Action__c> listOfRecommendationAction = new List<Recommendation_Action__c>();
            listOfRecommendationAction.add(new Recommendation_Action__c(Name = 'RecomAction1',Alert_Type__c='Contact Handling', Action__c = 'Acknowledged', Recommendation__c = listOfRecommendation[0].Id,
                                                                        Interaction__c = oInte1.Id));
            listOfRecommendationAction.add(new Recommendation_Action__c(Name = 'RecomAction2',Alert_Type__c='Guidance', Action__c = 'Acknowledged', Recommendation__c = listOfRecommendation[0].Id,
                                                                        Interaction__c = oInte1.Id));
            insert listOfRecommendationAction;
			System.AssertNotEquals(listOfRecommendationAction, NULL);
            System.AssertEquals(listOfRecommendationAction[0].Name, 'RecomAction1');
            System.AssertEquals(listOfRecommendationAction[0].Recommendation__c,listOfRecommendation[0].Id);
            System.assertEquals(listOfRecommendationAction[0].Interaction__c,oInte1.Id);  
            
            HUMConstants__c HC= new HUMConstants__c(Name='INTERACTION_ID',StringValue__c='true');
            insert HC;
            Test.stopTest();
        }
    }
        /*
    *Method Name    : TestInit
    * Description   : This Method is used for test init method     
    * Return Type   : None
    * Parameters    : none
    */
    static testMethod void TestInit()
    {
        List<Account> lstascc = [select id,recordtypeid,Birthdate__c,Mbr_Gen_Key__c,Enterprise_ID__c from Account where Mbr_Gen_Key__c='1234567890'];
        Interaction__c testTnt = [Select Id,Name from Interaction__c where Interacting_With_type__c='Member' limit 1];
        List<Recommendation> listOfRecommendation = [Select Id, Name from Recommendation];
        List<Recommendation_Action__c> listOfRecommendationAct = [Select Id,Alert_Type__c, Name,Interaction__c,Recommendation__c from Recommendation_Action__c where Name = 'RecomAction1'];  
        AlertDetails_DTO_HUM obj = new AlertDetails_DTO_HUM();
        obj.listRecommendationActionIds = new List<String>();
        obj.listRecommendationActionIds.add(listOfRecommendationAct[0].id); 
        obj.sAlertCount = listOfRecommendationAct.size();
        System.AssertNotEquals(listOfRecommendationAct.size(), NULL);
        System.AssertNotEquals(obj.sAlertCount, NULL);
        obj.sFirstTimeCaller = 'False';
        String MCDProductEligible = 'false';
        obj.tabId = true;
        string urlString = URL.getSalesforceBaseUrl().toExternalForm()+'?'+ViewAlerts_Logic_HUM.STRING_INTERACTIONID_HUM+testTnt.Id;
        obj.interactionId = testTnt.Id;
        System.AssertNotEquals(obj.interactionId, NULL);
        System.AssertNotEquals(testTnt.Id, NULL);
        System.AssertEquals(obj.interactionId, testTnt.Id);
        obj.sPageName = GLOBAL_CONSTANT_LH_HUM.sPersonAccount;
        obj.lstRecommendationActionId = listOfRecommendation[0].Id;
        System.AssertNotEquals(obj.lstRecommendationActionId, NULL);
        System.AssertNotEquals(listOfRecommendation[0].Id, NULL);
        System.AssertEquals( obj.lstRecommendationActionId ,listOfRecommendation[0].Id);
		obj.sAge = lstascc[0].Birthdate__c;
        AccountDetailViewAlerts_LC_HUM.init(null,obj.sPageName,lstascc[0].id,null,urlString);
        ViewAlerts_Logic_HUM.updateRecommnedationBasedOnAction(listOfRecommendationAct[0].id,true);
    }
    
    static testMethod void TestInitFail()
    {
        List<Account> lstascc = [select id,recordtypeid,Mbr_Gen_Key__c,Enterprise_ID__c from Account where Mbr_Gen_Key__c='1234567890'];
        Interaction__c testTnt = [Select Id,Name from Interaction__c where Interacting_With_type__c='Member' limit 1];
        List<Recommendation> listOfRecommendation = [Select Id, Name from Recommendation];
        List<Recommendation_Action__c> listOfRecommendationAct = [Select Id,Alert_Type__c, Name,Interaction__c,Recommendation__c from Recommendation_Action__c where Name = 'RecomAction2'];  

       	AlertDetails_DTO_HUM obj = new AlertDetails_DTO_HUM();
        obj.listRecommendationActionIds = new List<String>();
        obj.listRecommendationActionIds.add(listOfRecommendationAct[0].id); 
        obj.sAlertCount = 1;
        obj.sFirstTimeCaller = 'True';
        String MCDProductEligible = 'false';
        obj.tabId = true;
        string urlString = URL.getSalesforceBaseUrl().toExternalForm();
        obj.interactionId = testTnt.Id;
        System.AssertNotEquals(obj.interactionId, NULL);
        System.AssertNotEquals(testTnt.Id, NULL);
        System.AssertEquals(obj.interactionId, testTnt.Id);
        obj.lstRecommendationActionId = listOfRecommendation[0].Id;
        System.AssertNotEquals(obj.lstRecommendationActionId, NULL);
        System.AssertNotEquals(listOfRecommendation[0].Id, NULL);
        System.AssertEquals(obj.lstRecommendationActionId, listOfRecommendation[0].Id);
        AccountDetailViewAlerts_LC_HUM.init('Group','case',lstascc[0].id,null,'');  
    }
    static testMethod void TestInitNeg()
    {
        List<Account> lstascc = [select id,recordtypeid,Mbr_Gen_Key__c,Enterprise_ID__c from Account where Mbr_Gen_Key__c='1234567890'];
        AlertDetails_DTO_HUM obj = new AlertDetails_DTO_HUM();
        obj.sPageName = 'Person Account';
        AccountDetailViewAlerts_LC_HUM.init(obj.sPageName,null,lstascc[0].id,null,'');  
    }
}