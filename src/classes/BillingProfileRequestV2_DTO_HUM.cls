/******************************************************************************************************************
Apex class Name    : BillingProfileRequestV2_DTO_HUM
Version            : 1.0
Function           : Request DTO for Billing Service
Created On         : May 09 2018
Test Class         : BillingProfileV2DTO_T_HUM

Modification Log:
*   Version             Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*    1.0                Prasad Jandhyala             220187          5/09/2018             Original Version -> REQ - 361942
*    1.1                Vandana Chaudhari            243694          06/01/2018            Billing Service
*    1.2                Prasanthi Kandula                            03/25/2021            US:1437221 Displaying Paid Thru Date for DEN and MED Benefit Summary     
------------------------------------------------------------------------------------------------------------------*/
public with sharing class BillingProfileRequestV2_DTO_HUM
{
	public class PremiumInfo
	{
		public String IncludePremiumInformation = '';
		public String RateLevel = '';
	}

	public class IncludeCriteria
	{
		public String PaymentOption = '';
		public String PayorInfo = '';
		public String CollectionInfo = '';
		public String BillingMode = '';
		public String BillingFrequency = '';
		public String BalanceInfo = '';
		public InvoiceInfo InvoiceInfo;
		public PremiumInfo PremiumInfo;
		public String TransactionInfo = '';
		public String TransactionType = '';
		public String NbrOfTransactions = '';
        public String BillingAddressInfo='';  
        public string GetGroupInfo='';
	}

	public GetBillingAccountInfoRequest GetBillingAccountInfoRequest;

	public class GetBillingAccountInfoRequestCriteria
	{
		public RequestContext RequestContext;
		public SearchCriteriaList SearchCriteria;
		public IncludeCriteria IncludeCriteria;
	}

	public class InvoiceInfo
	{
		public String IncludeInvoiceInformation = '';
		public String NbrOfInvoices = '';
	}

	public class RequestContext
	{
		public String CallingSystemID = '';
		public String UserID = '';
		public String UserIDType = '';
		public String ProcessDate = '';
       public integer ProfileSequenceNbr=0;
	}

	public class GetBillingAccountInfoRequest
	{
		public GetBillingAccountInfoRequestCriteria GetBillingAccountInfoRequestCriteria;
	}

	public class SearchCriteriaList
	{
		public List<SearchCriteria> SearchCriteria;
	}

	public class SearchCriteria
	{
		public String PlatformCD = '';
		public String IDType = '';
		public String SearchID = '';
	}

}