/**
   @Author Gargey Sharma
   @name CIMedicareMultipleMemberRequest_DTO_HUM
   @CreateDate 01/13/2017
   @Description Response helper for CI Medicare Multiple Member Service
   @Version 1.0
   @reference REQ - 296804
   @Test Class PowerOfAttorney_T_HUM
*/
public with sharing class CIMedicareMultipleMemberRequest_DTO_HUM 
{
	public GetMemberInfo GetMemberInfo;
	
	public class GetMemberInfo
	{
		public list<MemberDetails> MemberDetails;
	}
	
	public class MemberDetails
	{
		public string MedicareID = '';
		public string MemberCardID = '';
		public string GroupID = '';
		public string BenefitSequenceNumber = '';
		public string AsOfDate = '';
		public string LISInfo = '';
		public string LTSSInfo = '';
		public string ESRDInfo = '';
		public string SPAPInfo = '';
		public string SNPInfo = '';
		public string POAInfo = '';
	}
}