/******************************************************************************************************************
Apex class Name    : HIDCMSOrderIdCardResponse_LDTO_HUM
Version            : 1.0
Function           : Response LDTO for HIDCMS OrderIdCard
Created On         : February 02 2023
Test Class         : HIDCMS_LT_HUM

Modification Log:
*   Version             Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------                
*    1.0                Anuradha Gajbhe                                 02/02/2023          * Anuradha Gajbhe                                       02/02/2023                   User Story 4060591: Lightning - ID Cards - RCC Auto create Case for ID Cards: Ability to automatically create a case when an ID Card Request is successfully submitted.
------------------------------------------------------------------------------------------------------------------*/
public with sharing class HIDCMSOrderIdCardResponse_LDTO_HUM 
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
	    public String MemberId   = '';
        public String PlanType = '';
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