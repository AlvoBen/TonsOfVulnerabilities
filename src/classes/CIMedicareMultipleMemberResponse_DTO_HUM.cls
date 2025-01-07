/**
   @Author Gargey Sharma
   @name CIMedicareMultipleMemberResponse_DTO_HUM
   @CreateDate 01/13/2017
   @Description Response helper for CI Medicare Multiple Member Service
   @Version 1.0
   @reference REQ - 296804
   @Test Class PowerOfAttorney_T_HUM
*/
public with sharing class CIMedicareMultipleMemberResponse_DTO_HUM 
{
	public CIMedicareResponse CIMedicareResponse;
	
	public class CIMedicareResponse
	{
		public list<GetMedicareMemberInfo> GetMedicareMemberInfo;
	}
	
	public class GetMedicareMemberInfo
	{
		public MedicareMemberLISSNPInfoResponse MedicareMemberLISSNPInfoResponse;
	}
	
	public class MedicareMemberLISSNPInfoResponse
	{
		public string MedicareID = '';
		public string MemberCardID = '';
		public string LISIndicator  = '';
		public string SubsidyEffectDate = '';
		public string SubsidyEndDate = '';
		public string LISSubsidyLevel = '';
		public string LISCopayCategory = '';
		public string LTSSIndicator = '';
		public string ESRDIndicator = '';
		public string SPAPIndicator = '';
		public string SNPIndicator = '';
		public string SNPTypeCode = '';
		public string POAIndicator = '';
		public string POALastName = '';
		public string POAFirstName = '';
		public string POAPhoneNumber = '';
		public string POAType = '';
		public string POAApplDate = '';
		public Errors Errors;
	}
    
    public class Errors
    {
    	public Error Error;
    }
    
    public class Error
    {
    	public string Code = '';
    	public string Description = '';
    }
}