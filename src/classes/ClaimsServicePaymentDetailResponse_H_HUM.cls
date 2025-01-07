/**************************************************************************** 
Apex Class Name  : ClaimsServicePaymentDetailResponse_H_HUM 
Version          : 1.0  
Created Date     : February 26, 2016
Function         : response helper for GetPaymentDetails method in claims service
Test Class       : ClaimsServicePaymentDetailResponse_T_HUM 
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Gargey Sharma            	30282             02/26/2016                   Original Version
* Gargey Sharma            	30282             03/11/2016                   Changed Transaction_835 to list, to accomodate multiple responses
* Gargey Sharma            	30970             04/13/2016                   Added ClaimDetails
***************************************************************************************************/
public class ClaimsServicePaymentDetailResponse_H_HUM 
{
	public GetEhub835XmlResponse GetEhub835XmlResponse;
	
	public class GetEhub835XmlResponse 
	{
		public GetEhub835XmlResult GetEhub835XmlResult;
	}
	
	public class GetEhub835XmlResult 
	{
		public list<Transaction_835> Transaction_835;
	}
	
	public class Transaction_835 
	{
		public string CheckNumber = '';
		public string PayeeName = '';
		public string PayeeAddress = '';
		public string CheckDate = '';
		public string PaymentType = '';
		public string PaymentAmount = '';
		public ClaimDetails ClaimDetails;
		public string PaymentTypeID = '';
		public string PayeeID = '';
		public string PaymentIssueDate = '';
		public string PaymentStatus = '';
		public string PaymentStatusDate = '';
	}
	
	public class ClaimDetails
	{
		public list<ClaimDetail> ClaimDetail;
	}
	
	public class ClaimDetail
	{
		public string ClaimNumber = '';
		public string ClaimPaymentAmount = '';
	}
}