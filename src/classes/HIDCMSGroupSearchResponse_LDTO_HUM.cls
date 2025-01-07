/******************************************************************************************************************
Apex class Name    : HIDCMSGroupSearchResponse_LDTO_HUM
Version            : 1.0
Function           : Response DTO for HIDCMS GroupSearch
Created On         : February 27 2018
Test Class         : 

Modification Log:
*   Version             Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------                
*    1.0                Suraj Patil                                    03/03/2022             Original Version REQ - 2865657
------------------------------------------------------------------------------------------------------------------*/
public with sharing class HIDCMSGroupSearchResponse_LDTO_HUM 
{
    @AuraEnabled public  GroupSearchResponseDTO   GroupSearchResponse ;

    public class GroupSearchResponseDTO 
    {
        @AuraEnabled public String IsError = '';
        @AuraEnabled public String ErrorMsg = '';
        @AuraEnabled public List<GroupSearchDetailDTO> GroupSearchDetail;
    }

    public class GroupSearchDetailDTO
    {
        @AuraEnabled public String CardCount = '';
        @AuraEnabled public String CardRequestDate = '';
        @AuraEnabled public String GroupName = '';
        @AuraEnabled public String Platform = '';
        @AuraEnabled public String GroupNbr = '';
    }

}