/**************************************************************************** 
Apex Class Name  : AccumsServiceResponse_H_HUM 
Version          : 1.0  
Created Date     : January 19, 2016
Function         : response helper for Accums Service
Test Class       : AccumsServiceResponse_T_HUM 
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Gargey Sharma            	29511             01/19/2016                   Original Version
* Gargey Sharma				29511 			  01/27/2016				   Updated as per changed Parameter names
***************************************************************************************************/
public class AccumsServiceResponse_H_HUM 
{	
	public GetBenefitAccumsResponse GetBenefitAccumsResponse;
	public class GetBenefitAccumsResponse 
	{
		public GetBenefitAccumsResult GetBenefitAccumsResult;
	}
	public class GetBenefitAccumsResult
	{
		public BenefitAccumsResponseHeader BenefitAccumsResponseHeader;
		public BenefitAccumsResponses BenefitAccumsResponses;
	}
	public class BenefitAccumsResponseHeader
	{
		public String AsOfDate = '';
		public String ErrorMessage = '';
		public String ErrorType = '';
		public String ResponseTime = '';
		public String TransactionDateTime = '';
		public String TransactionId = '';
		public String ValidationResults = '';
	}
	public class BenefitAccumsResponses
	{
		public list<BenefitAccumsResponse> BenefitAccumsResponse;
	}
	public class BenefitAccumsResponse
	{
		public String BenefitAccumsDataFound = '';
		public BenefitAccumsDataList BenefitAccumsDataList;
		public BenefitAccumsRequestInfo BenefitAccumsRequestInfo;
		public String ErrorMessage = '';
		public String ErrorType = '';
	}
	public class BenefitAccumsDataList
	{
		public list<BenefitAccumsData> BenefitAccumsData;
	}
	public class BenefitAccumsData 
	{
		public String AccumAmt = '';
		public String AccumBegDate = '';
		public String AccumEndDate = '';
		public String AccumIndivCd = '';
		public String AccumIndivCdDesc = '';
		public String AccumLimit = '';
		public String AccumParCd = '';  
		public String AccumParCdDesc = '';
		public String AccumRemaining = '';
		public String AccumSrcCd = '';
		public String AccumTypeCd = '';
		public String AccumTypeCdDesc = '';
		public String BenFreqCnt = '';
		public String BenTypeCd = '';
		public String BenTypeCdDesc = '';
		public String DeductTypeCd = ''; 
		public String FullDescription = '';
		public String SrcApplCd = '';
		public String SrcPlatformCd = '';
	}
	public class BenefitAccumsRequestInfo
	{
		public String CustomerCoverageSequenceNumber = '';
		public String CustomerGenKey = '';
		public String IsVaild = '';
		public String MemberPersonGenKey = '';
		public String SubscriberPersonGenKey = '';
		public String ValidationResults = '';
	}
}