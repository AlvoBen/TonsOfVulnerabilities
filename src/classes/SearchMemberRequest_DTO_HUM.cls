/**
 * @description       : 
 * @author            : Chetan Sindhe
 * @group             : 
 * @last modified on  : 11-09-2020
 * @last modified by  : ChangeMeIn@UserSettingsUnder.SFDoc
 * Modifications Log 
 * Ver   Date         Author                               Modification
 * 1.0   11-03-2020   Chetan Sindhe                         Initial Version
**/
public with sharing class SearchMemberRequest_DTO_HUM {
    
    public SearchMember SearchMember { get; set; }
    
    public class SearchMember {
        public Member Member { get; set; }
    }

    public class Member {
        public Contact Contact { get; set; }
        public MedicaidIdentifierList MedicaidIdentifierList { get; set; }
        public MedicareIdentifierList MedicareIdentifierList { get; set; }
        public SearchCriteria SearchCriteria { get; set; }
        public PlatformPointerList PlatformPointerList { get; set; }
        public SearchableMemberIdentifierList SearchableMemberIdentifierList { get; set; }
    }

    public class Contact {
        public ContactProfile ContactProfile { get; set; }
    } 

    public class ContactProfile {
        public PhoneList PhoneList { get; set; }
        public EmailList EmailList { get; set; }
        public AddressList AddressList { get; set; }
    }

    public class PhoneList {
        public string PersonPhone { get; set; }
    }

    public class EmailList {
        public string PersonEmail { get; set; }
    }

    public class AddressList {
        public string PersonAddress { get; set; }
    }

    public class MedicaidIdentifierList {
        public string MedicaidIdentifier { get; set; }
    }

    public class MedicareIdentifierList {
        public string MedicareIdentifier { get; set; }
    }

    public class SearchCriteria {
        public SearchAttribute SearchAttribute { get; set; }
    }

    public class SearchAttribute {
        public MemberIdBase MemberIdBase { get; set; }
        public DependentCode DependentCode { get; set; }
        public SourcePlatform SourcePlatform { get; set; }
    }

    public class MemberIdBase {
        public string SearchText { get; set; }
    }

    public class DependentCode {
        public string SearchText { get; set; }
    }

    public class SourcePlatform {
        public string SearchText { get; set; }
    }

    public class PlatformPointerList {
        public string PlatformProxyKey { get; set; }
    }

    public class SearchableMemberIdentifierList {
        public string SearchableMemberIdentifier { get; set; }
    }
}