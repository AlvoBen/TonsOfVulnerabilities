/**************************************************************************** 
Apex Class Name  : AccumsServiceRequest_H_HUM_V2 
Version          : 2.0  
Created Date     : January 20, 2020 
Function         : Request helper for GetBenefitAccums method in Accums Service
Test Class       : AccumsServiceRequest_T_HUM_V2
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* ChiranjeeviRao Ravuri     29511           01/19/2016                    Original Version
* Gargey Sharma				29511   		01/27/2016					  Updated as per changed Parameter names
* Shailesh Mali								01/20/2020					  Version-2, REQ-791947 Accumulator SOAP To REST Conversion
***************************************************************************************************/
public class AccumsServiceRequest_H_HUM_V2 {
    public GetBenefitAccums GetBenefitAccums;
	public class GetBenefitAccums 
	{
		public request request;
	}
	public class request 
	{
		public String AsOfDate = '';	
		public BenefitAccumsRequests BenefitAccumsRequests;
	}
	public class BenefitAccumsRequests 
	{
		public list<BenefitAccumsRequest> BenefitAccumsRequest;
	}
	public class BenefitAccumsRequest 
	{
		public String MemberDependentCode = '';
		public String MemberId = '';
	}
}