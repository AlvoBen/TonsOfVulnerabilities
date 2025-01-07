/******************************************************************************************************************
Apex class Name    : HIDCMSMemberSearchResponse_DTO_HUM
Version            : 1.0
Function           : Response DTO for HIDCMS MemberSearch
Created On         : February 27 2018
Test Class         : HIDCMS_T_HUM

Modification Log:
*   Version             Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------                
*    1.0                Sharan Shanmugam             207348       02/27/2018             Original Version -> REQ - 355975
------------------------------------------------------------------------------------------------------------------*/
public with sharing class HIDCMSMemberSearchResponse_DTO_HUM 
{
	public MemberSearchResponse MemberSearchResponse;
	
	public class MemberSearchResponse 
	{
		public List<MemberSearchDTO> MemberSearchDetail;
		public String IsError;
		public String ErrorMsg = '';
	}
	
	public class MemberSearchDTO 
        
	{
		public String FirstName = '';	
		public String CardRequestDate = '';	
		public String LastName = '';	
		public String RequestDate = '';
		public String BusinessSegment = '';	
		public String GroupNbr = '';	
		public String GroupName = '';	 
		public String Status = '';	
		public String Reason = '';
		public String ReasonToolTip = '';	
		public String CardType = '';	
		public String MemberId = '';	
		public String DependentCode = '';	
		public String StatusDate = '';	
		public String ExpirationDate = ''; 
		public String CardTypeDesc = '';
       
	}    
}