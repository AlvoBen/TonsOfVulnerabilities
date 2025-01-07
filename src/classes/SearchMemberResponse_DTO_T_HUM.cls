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
public with sharing class SearchMemberResponse_DTO_T_HUM {
    
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
            SearchMemberResponse_DTO_HUM respDTO = new SearchMemberResponse_DTO_HUM();
            SearchMemberResponse_DTO_HUM.SearchMemberResponse smrespDTO = new SearchMemberResponse_DTO_HUM.SearchMemberResponse();
            SearchMemberResponse_DTO_HUM.Member memberrespDTO = new SearchMemberResponse_DTO_HUM.Member();
            memberrespDTO.FirstName = 'LINDSYE';
            memberrespDTO.LastName = 'DADSON';
            memberrespDTO.MasterId = '1007485919';
            memberrespDTO.MiddleInitial = 'X';
            memberrespDTO.MiddleName = 'X';
            memberrespDTO.Ssn = '565562506';
            memberrespDTO.Suffix = 'Suffix info was not found';
            memberrespDTO.Prefix = 'Prefix info was not found';
            memberrespDTO.DateOfBirth = '02/05/1961';
            memberrespDTO.Gender = '';
            memberList.add(memberrespDTO);
            smrespDTO.MemberList = memberList;
            respDTO.SearchMemberResponse = smrespDTO;
            Boolean isMatch = false;
            for(SearchMemberResponse_DTO_HUM.Member member : responseDTO.searchResponseDTO.SearchMemberResponse.MemberList) {
                if(member.MasterId == memberrespDTO.MasterId) {
                    isMatch = true;
                    break;
                }
            }
            System.assertEquals(true, isMatch);
            Test.stopTest();
        }
        
    }
}