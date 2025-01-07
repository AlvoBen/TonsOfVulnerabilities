/******************************************************************************************************************
Apex class Name    : BillingProfileResponseV2_DTO_HUM
Version            : 1.0
Function           : Response DTO for Billing Service 
Created On         : May 08 2018
Test Class         : BillingProfileV2DTO_T_HUM

Modification Log:
*   Version             Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------                
*    1.0                Prasad Jandhyala              220187         5/08/2018             Original Version -> REQ - 361942
*    1.1                Sumit Pharande			      243694		 6/13/2018             Billing Service V2 custom setting
*    1.2                Prasanthi Kandula                            03/25/2021            US:1437221 Displaying Paid Thru Date for DEN and MED Benefit Summary     
------------------------------------------------------------------------------------------------------------------*/
public with sharing class BillingProfileResponseV2_DTO_HUM {
   
    public GetBillingAccountInfoResponse GetBillingAccountInfoResponse;
   
    public class GetBillingAccountInfoResponse 
    {
        public GetBillingAccountInfoResponseList GetBillingAccountInfoResponseList;
    }
    public class GetBillingAccountInfoResponseList 
    {
        public List<GetBillingAccountInfoResponseDetail> GetBillingAccountInfoResponseDetail; 
    }
	public class GetBillingAccountInfoResponseDetail 
    {
        public ResponseStatus ResponseStatus;
        public SearchCriteria SearchCriteria;
        public BillingAccountList BillingAccountList;
    }
  
	public class ResponseStatus 
    {
        public String ReturnCD = '';
        public String ReturnMessage = ''; 
    }
    public class SearchCriteria 
    {
        public String PlatformCD = '';
        public String IDType = '';
        public String SearchID = '';
        public String ProfileSequenceNbr = '';
    }
    public class BillingAccountList 
    {
        public List<BillAccount> BillAccount;
    }
	public class BillAccount 
    {
        public BillingProfile BillingProfile;
        public BalanceList BalanceList; 
        public RecurringPaymentMethodInfo RecurringPaymentMethodInfo;
        public PremiumInfo PremiumInfo;
        public PayorList PayorList;
		public CollectionInfo CollectionInfo;
		public InvoiceList InvoiceList;
        public AddressList AddressList;
	    public TransactionHistoryList TransactionHistoryList;
    }
    public class AddressList
    {
        public List<Address> Address;
    }
    public class Address
    {
        public String Type=''; 
        public String Line1='';
        public String Line2='';
        public String City=''; 
        public String State='';
        public String Zip='';  
        public String ZipPlus='';
    }
	public class InvoiceList
    {
        public List<InvoiceSummary> InvoiceSummary;
    }
    public class InvoiceSummary
    {
        public String InvoiceDate=''; 
        public String CreatedDate=''; 
        public String DueDate=''; 
        public String TotalDueAmt=''; 
        public String TotalBalanceFwdAmt=''; 
        public String TotalCharges=''; 
        public String TotalMemberAdjustments=''; 
        public String TotalPayments=''; 
        public String TotalOtherAdjustments=''; 
        public String InvoiceNbr=''; 
        public String Category=''; 
        public String InvoiceType=''; 
        public String LevelCD=''; 
        public String FrequencyCD=''; 
        public String CycleBeginDate=''; 
        public String CycleEndDate=''; 
        public String CreatedByRequest=''; 
        public String ExemptfromCollection=''; 
        public String TotalWellnessIncentive=''; 
        public String VBSuspPayAmt=''; 
        public String VBOverPayAmt=''; 
        public String VBPastDueAmt=''; 
        public String VBCurrentDueAmt=''; 
        public String VBOtherAdjustmentsAmt=''; 
        public String VBDueAmt=''; 
        public String CoreBalanceForwardAmt=''; 
        public String CorePaymentsAppliedAmt=''; 
        public String CorePastDueAmt=''; 
        public String CodeChargesAmt=''; 
        public String CoreMemberAppliedAmt=''; 
        public String CoreOtherAdjustmentsAmt=''; 
        public String CoreDueAmt=''; 
        public String ServiceFeeAmt='';
        public String CurrentUnpaidBalance=''; 
        public String NextLateFeeDate=''; 
        public String LateFeeStatusCD=''; 
        public String PaidByAutoDebit=''; 
        public String ReconStatusCD=''; 
    }
    
    public class BillingProfile 
    {
        public String DisplayID = ''; 
        public String BillingPartyKey = '';
        public String DisplayIDPlatformCD = '';
        public String PartyKey = '';
        public String ProfileSequenceNbr = '';
        public String ProfileName = '';
        public String AccountKey = '';
        public String AdminOrganizationID = '';
        public String EffectiveDate = '';
        public String EndDate = '';
        public String EndReason = '';
        public String ProfileStatusCD = '';
        public String ComponentTypeCD = '';
        public String ProductTypeCD = '';
        public String ProfileTypeCD = '';
        public String BillMethodCD = '';
        public String BillLevelCD = '';
        public String BillBlockCD = '';
        public String IndividualBillIndicator = ''; 
        public String CustomerAccountBalanceAmt = '';
        public String PaidThroughDate = '';
        public String BillModeCD = ''; 
        public String BillFrequencyCD = '';
        public String AdminDivisionCD = '';
        public String HoldBillIndicator = '';
        public String HoldBillReasonCD = '';
        public String NextBillDate = '';
        public String LastBillDate = '';
        public String LastInvoiceDate = '';
        public String OldestOpenInvoiceDate = '';
        public String CommercialOrMedicareIndicator = '';
        public String GroupOrIndividualBillIndicator = '';
        public String SelfBillIndicator = '';
        public String ReconMethodCD = '';
        public String ReconMethodDate = '';
        public String LagDayCount = '';
        public String FinancialCompanyCD = '';
        public String FinancialMarketCD = '';
        public String SellingLedgerNbr = '';
        public String BillingPlatformCD = ''; 
        public String ProrationPremiumCD = '';
        public String ProrationMODay = '';
        public String RouteInternalIndicator = '';
        public String TemplatePartyKey = '';
        public String TemplateProfileNbr = '';
        public string CPPaidThruDate = '';
    }
	public class BalanceList 
    {
        public List<Balance> Balance;
    }
    public class Balance {
        public String Type='';
        public String Amount='';
    }
    
    public class RecurringPaymentMethodInfo 
    {
        public String PaymentOption = ''; 
        public String EffectiveDate = '';
        public String EndDate = '';
		public String EndReasonCd='';
        public String NextRecurringDate = '';
        public String RecurringAmountTypeCD='';		
        public String EarliestEndDate = '';
        public PaymentAccount PaymentAccount;
		public String DebitDay='';
        public String DebitDayTypeCd='';
        public String CBStatus='';
        public String CBSentDate = '';
        public String CBIssuedFromDate = '';
        public String CBIssuedToDate = '';
    }
	 public class CollectionInfo
    {
        public String DelinquentDate ='';
        public String CollectionStatusCD='';
        public String NextCollectionCd='';
        public String NextCollectionActionDate='';
        public String ProtectUntilDate='';
        public String ExpectedTermActionDate='';
        public String ExpectedTermDate='';
        public String TotalPastDueAmt='';
        public String TotalNonSsaPastDueAmt='';
        public String TotalSsaPastDueAmt='';
        public String NextLetterTypeCD='';
        public String NextLetterID='';
        public String ProtectReasonCD='';
        public String ProtectedDate='';
        public String CollectionProtectedBY='';
        public String MaoPercentage='';
        public String MaoAmt='';
        public String DelinquentSubsidyTypeCD='';
        public CollectionCategoryInfo CollectionCategoryInfo;
        
    }
    public class CollectionCategoryInfo
    {
        public String CategoryType='';
        public String PaidThroughDate='';
        public String DelinquentDate='';
        public String TotalBalanceAmt='';
        public String PastDueAmt='';
        public String NextActionCD='';
        public String NextCollectionDate='';
        public String NextLetterID='';
        public String ExpectedTermDate='';
        public String ExpectedTermActionDate='';
        public String TermCalcCD='';
        public String TermXferCD='';
        public String SubsidyTypeCD='';
        
    }
	
    public class PaymentAccount 
    {
        public String AccountSequenceNbr = '';
		public String PaymentAccountType='';
        public BankAccount BankAccount;
        public CreditCard CreditCard;
    }
	
    public class BankAccount 
    {
	     public String BankName=''; 
         public String TransitNbr=''; 
         public String AccountNbr='';
         public String AccountType='';
         public String TypeDescription='';
         public String AccountHolderName='';
    }
    public class CreditCard 
    {
        public String CardTypeCD='';
        public String ExpiryDate=''; 
        public String DataVaultToken='';
        public String AccountNbr='';
        public String CardHolderName='';
        public CardHolderAddress CardHolderAddress;
    }
      public class CardHolderAddress {
    }
	 public class PremiumInfo 
    {
        public String Premium = '';
        public String AdditionalSupplementalCharges = ''; 
        public String PenaltyAmt = ''; 
        public String SubsidyAmt = '';
        public String Fees = ''; 
        public String OtherAmt = '';
        public String DiscountAmt = '';
        public String NetPremium = '';
    }
    public class PayorList 
    {
        public List<Payor> Payor;
    }
    public class Payor 
    {
        public String Type = '';
        public String PersonID = '';
        public String FirstName = '';
        public String LastName = '';
    } 
    public class TransactionHistoryList
    {
        public List<TransactionDetails> TransactionDetails;
    }
    public class TransactionDetails
    {
			public String TransactionDate='';
			public String CheckNbr='';
			public String TransactionAmt='';
			public String TransactionDesc='';
			public String ReceivedDate='';
			public String TransactionType='';
			public PaymentAccount PaymentAccount;       
    }	
     
}