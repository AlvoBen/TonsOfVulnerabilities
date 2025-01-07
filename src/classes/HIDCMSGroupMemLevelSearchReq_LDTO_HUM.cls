/******************************************************************************************************************
Apex class Name    : HIDCMSGroupMemLevelSearchReq_LDTO_HUM 
Version            : 1.0
Function           : Request DTO for HIDCMS GroupMemberLevelSearch
Created On         : February 27 2018
Test Class         : 

Modification Log:
*   Version             Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------                
*    1.0                Prasad Jandhyala              220815            02/27/2018             Original Version REQ - 2865657
------------------------------------------------------------------------------------------------------------------*/
public with sharing class HIDCMSGroupMemLevelSearchReq_LDTO_HUM 
{
    @AuraEnabled public  GroupMemberLevelSearchRequestDTO  GroupMemberLevelSearchRequest;

    public class  GroupMemberLevelSearchRequestDTO
    {
        @AuraEnabled public String GroupNbr = '';
        @AuraEnabled public String RequestGeneratedOn = '';
        @AuraEnabled public String Platform = '';
        @AuraEnabled public String FirstName  = '';
        @AuraEnabled public String LastName  = '';
        @AuraEnabled public String MemberId   = '';
        @AuraEnabled public String DependentCode = '';
    }
}