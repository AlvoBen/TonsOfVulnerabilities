/******************************************************************************************************************
Apex class Name    : MemberLevelSearch_DTO_HUM
Version            : 1.0
Function           : DTO Class for MemberSearch
Created On         : February 27 2018
Test Class         : MemberIdCards_T_HUM

Modification Log:
*   Version             Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*    1.0                Melkisan Selvaraj             207348         02/27/2018             Original Version -> REQ - 355975
------------------------------------------------------------------------------------------------------------------*/
public with sharing class MemberLevelSearch_DTO_HUM 
{
   public map<string,list<HIDCMSMemberSearchResponse_DTO_HUM.MemberSearchDTO>> mapMemberLevelDTO ;
   public list<HIDCMSMemberSearchResponse_DTO_HUM.MemberSearchDTO> parentNodes;
}