/**
 * @description       : 
 * @author            : Chetan Sindhe
 * @group             : 
 * @last modified on  : 11-09-2020
 * @last modified by  : ChangeMeIn@UserSettingsUnder.SFDoc
 * Modifications Log 
 * Ver   Date         Author                               Modification
 * 1.0   11-09-2020   Chetan Sindhe                         Initial Version
**/
@isTest
public with sharing class SearchMember_DTO_T_HUM {
    
    private static testMethod void testSearchMemberResponse_DTO_T_HUM() {
        User usr = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert usr; 
        system.assertEquals(usr.Lastname == 'test User1',true);
        system.runAs(usr) 
        {
            Test.startTest();
            Test.setMock(HttpCalloutMock.class, new SearchMemberMockResponse_T_HUM());
            String result = SearchMember_D_HUM.retrieveSearchResponse('00336008401', 'LV');
            List<SearchMemberResponse_DTO_HUM.Member> memberList = new List<SearchMemberResponse_DTO_HUM.Member>();
            SearchMember_DTO_HUM responseDTO = (SearchMember_DTO_HUM) JSON.deserialize(
                result, SearchMember_DTO_HUM.class);
            System.assertEquals(false, responseDTO.calloutErrored);
            Test.stopTest();
        }
    }

    private static testMethod void testFaultSearchMemberResponse_DTO_T_HUM() {
        User usr = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert usr; 
        system.assertEquals(usr.Lastname == 'test User1',true);
        system.runAs(usr) 
        {
            Test.startTest();
            Test.setMock(HttpCalloutMock.class, new SearchMemberMockResponse_T_HUM());
            String result = SearchMember_D_HUM.retrieveSearchResponse('', 'LV');
            List<SearchMemberResponse_DTO_HUM.Member> memberList = new List<SearchMemberResponse_DTO_HUM.Member>();
            SearchMember_DTO_HUM responseDTO = (SearchMember_DTO_HUM) JSON.deserialize(
                result, SearchMember_DTO_HUM.class);
            System.assertEquals(true, responseDTO.calloutErrored);
            Test.stopTest();
        }
    }

}