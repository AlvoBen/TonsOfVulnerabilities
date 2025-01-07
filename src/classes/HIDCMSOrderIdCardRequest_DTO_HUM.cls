/******************************************************************************************************************
Apex class Name    : HIDCMSOrderIdCardRequest_DTO_HUM
Version            : 1.0
Function           : Request DTO for HIDCMS OrderIdCard
Created On         : February 27 2018
Test Class         : HIDCMS_T_HUM

Modification Log:
*   Version             Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------                
*    1.0                Sharan Shanmugam             207348           02/27/2018             Original Version -> REQ - 355980
------------------------------------------------------------------------------------------------------------------*/
public with sharing class HIDCMSOrderIdCardRequest_DTO_HUM 
{
	public OrderMemberIdCardDetail OrderMemberIdCard;
	
	public class OrderMemberIdCardDetail
	{
		public List<memberDetail> memberDto;
	}
	
	public class memberDetail 
	{
		public String CustCovSeqNbr = '';	
		public String CustGenKey;	
		public String DateOfBirth = '';	
		public String GroupNbr;	
		public String MemberGenKey;	
		public String MemberPersonId = '';	
		public String MemberId = '';	
		public String PlanType = '';
		public String Platform = '';	
		public String SubscriberGenKey;	
		public String SubscriberPersonId = '';	
		public String SubscriberId = '';
	}	    
}