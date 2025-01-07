/**
 * @description       : 
 * @author            : Chetan Sindhe
 * @group             : 
 * @last modified on  : 11-09-2020
 * @last modified by  : ChangeMeIn@UserSettingsUnder.SFDoc
 * Modifications Log 
 * Ver   Date         Author                               Modification
 * 1.0   11-09-2020   Chetan Sindhe                       Initial Version
**/
@isTest
public with sharing class SearchMember_S_T_HUM {
    
    /*
    * This method creates test data for test class
    * @param        None
    * @return       None
    */
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
            system.assertequals('MBESearchMemberService',objUpdateMemberDemographics.Name);
            system.assertequals('Humana_GuidanceCare_neptune', objUpdateMemberDemographics.Certificate_Name__c);
        }
    }

    /*
    * Method to test SearchMember
    * @param        None
    * @return       None
    */
    private static testmethod void testSuccessSearchMember()
    {
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        system.runAs(testUser)
        { 
            Test.StartTest();
            HttpRequest request = new HttpRequest();
            request = SearchMember_S_HUM.constructRequest('00396694201', 'EM');
            system.assertNotEquals(null, request);
            Test.StopTest();
        }
    }

    /*
    * Method to test exception for SearchMember
    * @param        None
    * @return       None
    */
    private static testmethod void testBlankSearchMember()
    {
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        system.runAs(testUser)
        { 
            Test.StartTest();
          	HttpRequest request = new HttpRequest();
            request = SearchMember_S_HUM.constructRequest('', '');
            System.assertEquals(null, request);
            Test.StopTest();
        }
    }
    
    /*
    * Method to test exception for SearchMember
    * @param        None
    * @return       None
    */
    private static testmethod void testExceptionSearchMember()
    {
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        system.runAs(testUser)
        { 
            Test.StartTest();
          	HttpRequest request = new HttpRequest();
            request = SearchMember_S_HUM.constructRequest(null, null);
            System.assertEquals(null, request);
            Test.StopTest();
        }
    }

    /*
    * Method to test exception for SearchMember
    * @param        None
    * @return       None
    */
    private static testmethod void testMemberIdBlankSearchMember()
    {
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        system.runAs(testUser)
        { 
            Test.StartTest();
          	HttpRequest request = new HttpRequest();
            request = SearchMember_S_HUM.constructRequest('', 'EM');
            System.assertEquals(null, request);
            Test.StopTest();
        }
    }

    /*
    * Method to test exception for SearchMember
    * @param        None
    * @return       None
    */
    private static testmethod void testPlatformValueBlankSearchMember()
    {
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        system.runAs(testUser)
        { 
            Test.StartTest();
          	HttpRequest request = new HttpRequest();
            request = SearchMember_S_HUM.constructRequest('00396694201', '');
            System.assertEquals(null, request);
            Test.StopTest();
        }
    }

}