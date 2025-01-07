/******************************************************************************************************************
Apex class Name    : HIDCMSMemberSearchRequest_DTO_HUM
Version            : 1.0
Function           : Request DTO for HIDCMS MemberSearch
Created On         : February 27 2018
Test Class         : HIDCMS_T_HUM

Modification Log:
*   Version             Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------                
*    1.0                Sharan Shanmugam              207348         02/27/2018             Original Version -> REQ - 355975
------------------------------------------------------------------------------------------------------------------*/
public with sharing class HIDCMSMemberSearchRequest_DTO_HUM 
{
	public MemberSearchRequestDTO  MemberSearchRequest ;
	
	public class MemberSearchRequestDTO 
	{
		public String GroupNumber = '';	
		public String  MemberID = '';	
		public String FromDate = '';	
		public String ToDate = '';	
	}    
}