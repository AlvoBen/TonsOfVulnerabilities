/******************************************************************************************************************
Apex class Name    : HIDCMSOrderIdCardResponse_DTO_HUM
Version            : 1.0
Function           : Response DTO for HIDCMS OrderIdCard
Created On         : February 27 2018
Test Class         : HIDCMS_T_HUM

Modification Log:
*   Version             Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------                
*    1.0                Sharan Shanmugam              207348          02/27/2018             Original Version -> REQ - 355980
------------------------------------------------------------------------------------------------------------------*/
public with sharing class HIDCMSOrderIdCardResponse_DTO_HUM 
{
	public OrderMemberIdCardResponseDetail OrderMemberIdCardResponse;
	
	public class OrderMemberIdCardResponseDetail 
	{
		public OrderMemberIdCardResultsDetail OrderMemberIdCardResults;
	}
	
	public class OrderMemberIdCardResultsDetail 
	{
		public List<OrderMemberIdCardResultDetail> OrderMemberIdCardResult;
	}
	
	public class OrderMemberIdCardResultDetail 
	{
		public String CustCovSeqNbr = '';	
		public String CustomerGenKey = '';	
		public String DateOfBirth = '';	
		public String DependentCode = '';	
		public String EffectiveDate = '';	
		public String GroupNbr = '';	
		public IdCardDetails IdCardDetailsDto;
	}
	
	public class IdCardDetails 
	{
		public String CardIndex = '';	
		public String CardOrderDate = '';	
		public String CardsFound = '';	
		public String ErrorCode = '';	
		public String ErrorDescription = '';	
		public String IsError = '';	
		public String OrderInProgress = '';	
	}    
}