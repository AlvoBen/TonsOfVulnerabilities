/******************************************************************************************************************
Apex class Name    : HIDCMSGroupMemLvlSearchResponse_DTO_HUM
Version            : 1.0
Function           : Response DTO for HIDCMS GroupMemberLevelSearch
Created On         : February 27 2018
Test Class         : 

Modification Log:
*   Version             Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------                
*    1.0                Prasad Jandhyala            220815             02/27/2018             Original Version
*    1.1                Melkisan Selvaraj			234588			   05/28/2018             REQ - 363519 Added ReasonTooltip
------------------------------------------------------------------------------------------------------------------*/
public with sharing class HIDCMSGroupMemLvlSearchResponse_DTO_HUM 
{
    public GroupMembeLevelSearchResponseDTO GroupMembeLevelSearchResponse;

    public class GroupMembeLevelSearchResponseDTO 
	{
        public String HasData = '';
        public String IsError = '';
        public String ErrorMsg = '';
        public List<GroupMemberLevelSearchDetailDTO> GroupMemberLevelSearchDetail;
    }
    
    public class GroupMemberLevelSearchDetailDTO 
	{
        public String  FirstName = '';
        public String  CardRequestDate = '';
        public String  LastName  = '';
        public String  BusinessSegment  = '';
        public String  CardFormat  = '';
        public String  Status  = '';
        Public String  Reason  = '';
		public String  ReasonToolTip = '';
        Public String  CardType  = '';
        Public String  CardDesc  = '';
        Public String  MemberId = '';
        public String  DependentCode = '';
        public String  StatusDate  = '';
        public String  ExpirationDate  = '';
        public String  GroupNbr  = '';
        public String  GroupName  = '';
		public String  CardTypeDesc = '';
    }
}