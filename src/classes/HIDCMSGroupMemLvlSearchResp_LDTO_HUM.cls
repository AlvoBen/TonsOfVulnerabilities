/******************************************************************************************************************
Apex class Name    : HIDCMSGroupMemLvlSearchResp_LDTO_HUM
Version            : 1.0
Function           : Response DTO for HIDCMS GroupMemberLevelSearch
Created On         : February 27 2018
Test Class         : 

Modification Log:
*   Version             Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------                
*    1.0                Prasad Jandhyala            220815             02/27/2018             Original Version REQ - 2865657
------------------------------------------------------------------------------------------------------------------*/
public with sharing class HIDCMSGroupMemLvlSearchResp_LDTO_HUM 
{
    @AuraEnabled public GroupMembeLevelSearchResponseDTO GroupMembeLevelSearchResponse;

    public class GroupMembeLevelSearchResponseDTO 
    {
        @AuraEnabled public String HasData = '';
        @AuraEnabled public String IsError = '';
        @AuraEnabled public String ErrorMsg = '';
        @AuraEnabled public List<GroupMemberLevelSearchDetailDTO> GroupMemberLevelSearchDetail;
    }
    
    public class GroupMemberLevelSearchDetailDTO 
    {
        @AuraEnabled public String  FirstName = '';
        @AuraEnabled public String  CardRequestDate = '';
        @AuraEnabled public String  LastName  = '';
        @AuraEnabled public String  BusinessSegment  = '';
        @AuraEnabled public String  CardFormat  = '';
        @AuraEnabled public String  Status  = '';
        @AuraEnabled public String  Reason  = '';
        @AuraEnabled public String  ReasonToolTip = '';
        @AuraEnabled public String  CardType  = '';
        @AuraEnabled public String  CardDesc  = '';
        @AuraEnabled public String  MemberId = '';
        @AuraEnabled public String  DependentCode = '';
        @AuraEnabled public String  StatusDate  = '';
        @AuraEnabled public String  ExpirationDate  = '';
        @AuraEnabled public String  GroupNbr  = '';
        @AuraEnabled public String  GroupName  = '';
        @AuraEnabled public String  CardTypeDesc = '';
    }
}