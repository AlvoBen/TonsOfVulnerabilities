/******************************************************************************************************************
Apex class Name    : HIDCMSGroupMemLevelSearchRequest_DTO_HUM
Version            : 1.0
Function           : Request DTO for HIDCMS GroupMemberLevelSearch
Created On         : February 27 2018
Test Class         : 

Modification Log:
*   Version             Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------                
*    1.0                Prasad Jandhyala              220815            02/27/2018             Original Version
*	 1.0				Prerna Prasad				  243782			06/14/2018			   REQ - 366904
------------------------------------------------------------------------------------------------------------------*/
public with sharing class HIDCMSGroupMemLevelSearchRequest_DTO_HUM 
{
    public  GroupMemberLevelSearchRequestDTO  GroupMemberLevelSearchRequest;

    public class  GroupMemberLevelSearchRequestDTO
	{
        public String GroupNbr = '';
        public String RequestGeneratedOn = '';
        public String Platform = '';
        public String FirstName  = '';
        public String LastName  = '';
        public String MemberId   = '';
		public String DependentCode = '';
    }
}