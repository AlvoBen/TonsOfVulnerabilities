/**
 * @description       : 
 * @author            : Chetan Sindhe
 * @group             : 
 * @last modified on  : 11-09-2020
 * @last modified by  : ChangeMeIn@UserSettingsUnder.SFDoc
 * Modifications Log 
 * Ver   Date         Author                               Modification
 * 1.0   11-03-2020   Chetan Sindhe                        Initial Version
**/
public with sharing class SearchMemberResponse_DTO_HUM {
    
    public SearchMemberResponse SearchMemberResponse { get; set; }

    public class SearchMemberResponse {
        public List<Member> MemberList { get; set; }
    }

    public class Member {
        public string DateOfBirth { get; set; }
        public string FirstName { get; set; }
        public string Gender { get; set; }
        public string LastName { get; set; }
        public string MiddleInitial { get; set; }
        public string Prefix { get; set; }
        public string Suffix { get; set; }
        public string MiddleName { get; set; }
        public string Ssn { get; set; }
        public string MasterId { get; set; }
    }

}