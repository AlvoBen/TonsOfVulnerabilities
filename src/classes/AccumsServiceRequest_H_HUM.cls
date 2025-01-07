/**************************************************************************** 
Apex Class Name  : AccumsServiceRequest_H_HUM 
Version          : 1.0  
Created Date     : January 19, 2016 
Function         : Request helper for GetBenefitAccums method in Accums Service
Test Class       : AccumsServiceRequest_T_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* ChiranjeeviRao Ravuri     29511           01/19/2016                    Original Version
* Gargey Sharma				29511   		01/27/2016					  Updated as per changed Parameter names
***************************************************************************************************/
public class AccumsServiceRequest_H_HUM
{
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
		public String CustomerCoverageSequenceNumber = '';	
		public String CustomerGenKey = '';	
		public String MemberPersonGenKey = '';
		public String SubscriberPersonGenKey = '';
	}
   
}