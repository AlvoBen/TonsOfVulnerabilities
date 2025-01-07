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
public with sharing class SearchMember_DTO_HUM {

    public SearchMemberResponse_DTO_HUM searchResponseDTO { get; set; }
    public string serviceCalloutError { get; set; }
    public boolean calloutErrored { get; set; }
    public FaultCode_DTO_HUM faultResponseDTO { get; set; }
    public string searchRequestDTO { get; set; }

}