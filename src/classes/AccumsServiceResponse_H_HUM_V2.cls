/**************************************************************************** 
Apex Class Name  : AccumsServiceResponse_H_HUM_V2 
Version          : 2.0  
Created Date     : January 20, 2020
Function         : response helper for Accums Service
Test Class       : AccumsServiceResponse_T_HUM_V2 
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Gargey Sharma            	29511             01/19/2016                   Original Version
* Gargey Sharma				29511 			  01/27/2016				   Updated as per changed Parameter names
* Shailesh Mali								  01/20/2020			       Version - 2, REQ - 791947 Accumulator SOAP to REST Conversion
* Shailesh Mali								  02/04/2020			       REQ - 749402 Accumulator Description for Dental Product
***************************************************************************************************/
public class AccumsServiceResponse_H_HUM_V2 {
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
        //public String ValidationResults = '';
		public List<String> ValidationResults = null;
	}
	public class BenefitAccumsResponses
	{
		public list<BenefitAccumsResponse> BenefitAccumsResponse;
	}
	public class BenefitAccumsResponse
	{
		public String BenefitAccumsDataFound = '';
        public String ErrorMessage = '';
		public String ErrorType = '';
        public BenefitAccumsDataList BenefitAccumsDataList;
		public BenefitAccumsRequestInfo BenefitAccumsRequestInfo;		
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
        public String VisitLimit = '';
        public String VisitUsed = '';
        public String VisitRemaining = '';    
        public DentalAccumsResponse DentalAccumsResponse;
	}
    public class DentalAccumsResponse{
        public string BenefitName= '';
        public string BenefitDescription = '';
        public string IsDeductible = '';
        public string IvrBenefitCode ='';
        public string MayContainCarryOverCredit = '';
    }
	public class BenefitAccumsRequestInfo
	{
		public String CustomerCoverageSequenceNumber = '';
		public String CustomerGenKey = '';
		public String IsVaild = '';
		public String MemberPersonGenKey = '';
		public String SubscriberPersonGenKey = '';
		//public String ValidationResults = '';
		public List<String> ValidationResults = null;
	}
}