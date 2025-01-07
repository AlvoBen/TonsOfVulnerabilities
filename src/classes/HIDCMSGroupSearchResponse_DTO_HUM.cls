/******************************************************************************************************************
Apex class Name    : HIDCMSGroupSearchResponse_DTO_HUM
Version            : 1.0
Function           : Request DTO for HIDCMS GroupSearch
Created On         : February 27 2018
Test Class         : 

Modification Log:
*   Version             Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------                
*    1.0                Prasad Jandhyala             212450         02/27/2018             Original Version -> REQ - 355978
------------------------------------------------------------------------------------------------------------------*/
public with sharing class HIDCMSGroupSearchResponse_DTO_HUM 
{
    public  GroupSearchResponseDTO   GroupSearchResponse ;

    public class GroupSearchResponseDTO 
    {
        public String IsError = '';
        public String ErrorMsg = '';
        public List<GroupSearchDetailDTO> GroupSearchDetail;
    }

    public class GroupSearchDetailDTO
    {
        public String CardCount = '';
        public String CardRequestDate = '';
        public String GroupName = '';
        public String Platform = '';
        public String GroupNbr = '';
    }

}