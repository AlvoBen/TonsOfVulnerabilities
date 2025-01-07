/**
 * @description       : 
 * @author            : ChangeMeIn@UserSettingsUnder.SFDoc
 * @group             : 
 * @last modified on  : 07-23-2020
 * @last modified by  : ChangeMeIn@UserSettingsUnder.SFDoc
 * Modifications Log 
 * Ver   Date         Author                               Modification
 * 1.0   07-23-2020   ChangeMeIn@UserSettingsUnder.SFDoc   Initial Version
**/
public with sharing class GroupOfRecords_DTO_HUM {
    
    public List<Details> details { get; set; }

    public class Details {
        public string platform { get; set; }
        public boolean primary { get; set; }
        public string policyMemberId { get; set; }
        public EligiblityResponse_DTO_HUM.GroupRecord product { get; set; }
    }

}