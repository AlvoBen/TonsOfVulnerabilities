/**
 * @description       : 
 * @author            : Chetan Sindhe
 * @group             : 
 * @last modified on  : 11-09-2020
 * @last modified by  : ChangeMeIn@UserSettingsUnder.SFDoc
 * Modifications Log 
 * Ver   Date         Author                               Modification
 * 1.0   11-05-2020   Chetan Sindhe                       Initial Version
**/
@istest
public with sharing class SearchMember_D_T_HUM {
    
    @testSetup static void prepareTestData() 
    {  
        User oSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oSUser;
      	System.runAs(osUser) 
        {
            HUM_Webservice_Callout__c objUpdateMemberDemographics = new HUM_Webservice_Callout__c();
            objUpdateMemberDemographics.Name = 'MBESearchMemberService';
            objUpdateMemberDemographics.Active__c = True;
            objUpdateMemberDemographics.Enable_Service_Tester__c = True;
            objUpdateMemberDemographics.Certificate_Name__c = 'Humana_GuidanceCare_neptune';
            objUpdateMemberDemographics.Content_Type__c = 'application/json';
            objUpdateMemberDemographics.Request_Type__c = 'POST';
            objUpdateMemberDemographics.Timeout__c = '12000';
            objUpdateMemberDemographics.End_Point_URL__c = 'https://dev-crmservicews.humana.com/MBEPLUSServiceREST';
            objUpdateMemberDemographics.Service_Name__c = '/SearchMember';
            insert objUpdateMemberDemographics;
        } 
    }

    /*
    * Method to test success response for SearcH Member
    * @param        None
    * @return       None
    */
    private static testmethod void testSuccessSearchMember()
    {
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        system.runAs(testUser)
        { 
            Test.StartTest();
          	Test.setMock(HttpCalloutMock.class, new SearchMemberMockResponse_T_HUM());
            String result = SearchMember_D_HUM.retrieveSearchResponse('00336008401', 'LV');
            System.assertNotEquals(null, result);
            Test.StopTest();
        }
    }

     /*
    * Method to test failure response for SearchMember
    * @param        None
    * @return       None
    */
    private static testmethod void testFailureSearchMemberRequest()
    {
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        system.runAs(testUser)
        { 
            Test.StartTest();
          	Test.setMock(HttpCalloutMock.class, new SearchMemberFailureMockResponse_T_HUM());
            String result = SearchMember_D_HUM.retrieveSearchResponse('00336008401', 'LV');
            System.assertNotEquals(null, result);
            Test.StopTest();
        }
    }

    /*
    * Method to test null response for SearchMember
    * @param        None
    * @return       None
    */
    private static testmethod void testNullSearchMemberRequest()
    {
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        system.runAs(testUser)
        { 
            Test.StartTest();
          	Test.setMock(HttpCalloutMock.class, new SearchMemberFailureMockResponse_T_HUM());
            String result = SearchMember_D_HUM.retrieveSearchResponse('', 'LV');
            System.assertNotEquals(null, result);
            Test.StopTest();
        }
    }
}