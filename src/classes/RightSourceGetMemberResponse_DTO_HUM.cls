/******************************************************************************************************************
Apex class Name    : RightSourceGetMemberResponse_DTO_HUM
Version            : 1.0
Function           : Response DTO for RightSource GetMember
Created On         : December 9 2016
Test Class         : RightSource_T_HUM

Modification Log:
*   Version             Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*    1.0                Vishal Verma                                 12/09/2016             Original Version -> REQ - 291073
*    1.1                Samridh Manucha                              12/22/2016             REQ - 291163
*    1.2                Rajesh Chandgothia                           01/02/2017             REQ - 291083
*	 1.3				Rajesh Keswani                58348          01/27/2017             Implementation of REQ - 296662 - View Address
*	 1.4				Sharan Shanmugam							 02/09/2017				Modified as part of REQ - 299420
*    1.5                Sumit Pharande                               03/15/2017             Modified as part of REQ - 304862-Added AddressKey
*	 1.6				Pradeep Dani				  302363		 08/13/2018				Added response node for duplicate customers.
*    1.7				Joel George								     10/31/2018				REQ - 367769
*    1.8               Melkisan Selvaraj							 03/04/2019             REQ - 387544 : Splunk error logging Issues
*	 1.9				Lakshmi Madduri								  10/30/2019			Adding customerTypeLiteral
------------------------------------------------------------------------------------------------------------------*/
public with sharing class RightSourceGetMemberResponse_DTO_HUM
{
	
	public class communicationPreference
	{
	public String commPrefType ='';
	public String commPrefValue = '';
	}
	
	public class communicationPreferences
	{
	public List<communicationPreference> communicationPreference ;
	}
	
	public MemberReponse GetMemberReponse;

	public class MemberReponse
	{
		public CustomersDetail Customers;
		public DuplicateCustomers DuplicateCustomers;
	}

	public class CustomersDetail
	{
		public List<CustomerDetail> Customer;
		public string ErrorDescription = '';
	}

	public class CustomerDetail
	{
		public CustomerIdentityDetail CustomerIdentity;
		public CustomerPreference CustomerPreference;
		public CustomerDetails CustomerDetail;
		public AddressesDetail Addresses;
		public PhonesDetail Phones;
		public String StaleData;
		public AccountNumberDetail AccountNumber;
		public FinanceDetail Finance;
		public PaymentDetail Payment;
		public AllergiesDetail Allergies;
		public CreditCardsDetail CreditCards;
		public HealthConditionsDetail HealthConditions;
		public LogNotesDetail LogNotes;
		public PlansDetail Plans;
		public communicationPreferences communicationPreferences;
	}

	public class CustomerIdentityDetail
	{
		public String BirthDate = '';
		public CustomerKeysDetail CustomerKeys;
		public String FirstName = '';
		public String MiddleName = '';
		public String LastName = '';
		public String Email = '';
	}

	public class CustomerKeysDetail
	{
		public List<CustomerKeyDetail> CustomerKey;
	}

	public class DuplicateCustomers
	{
		public List<CustomerKeyDetail> CustomerKey;
	}

	public class CustomerKeyDetail
	{
		public String CustomerType = '';
		public String CustomerValue = '';
		public String CustomerTypeLiteral='';
	}

	public class CustomerPreference
	{
		public String CapType = '';
		public String CapTypeLiteral = '';
	}

	public class CustomerDetails
	{
		public String NeedsMemberConsent = '';
		public String ConsentBeginDate = '';
		public String ConsentEndDate = '';
		public String MemberConsentStatus = '';
		public String CMRStatus = '';
		public String CMRStatusLiteral = '';
	}

	public class AddressesDetail
	{
		public List<AddressDetail> Address;
	}

	public class AddressDetail
	{
		public String AddressKey ='';
		public String AddressType = ''; 
		public String AddressName = '';
		public String AddressTypeLiteral = '';
		public String AddressLine1 = '';
		public String AddressLine2 = '';
		public String City = '';
		public String StateCode = '';
		public String ZipCode = '';
		public String IsActive = ''; 
	}

	public class PhonesDetail
	{
		public List<PhoneDetail> Phone;
	}

	public class PhoneDetail
	{
		public String PhoneKey = '';
		public String PhoneType = '';
		public String PhoneValue = ''; 
	}

	public class AccountNumberDetail
	{
		public String CustomerValue = '';
	}

	public class FinanceDetail
	{
		public AdjustmentsDetail Adjustments;
		public String CreditLimit = '';
		public String OutstandingBalance = '';
		public PaymentSchedulesDetail PaymentSchedules;
	}

	public class PaymentDetail
	{
		public NonCreditCardDetail NonCreditCard;
	}

	public class NonCreditCardDetail
	{
		public String PaymentNumber = '';
		public String Type = '';
	}

	public class AdjustmentsDetail
	{
		public List<AdjustmentDetail> Adjustment;
	}

	public class AdjustmentDetail
	{
		public String AdjustmentAmount = '';
		public String AdjustmentDate = '';
		public String AdjustmentType = '';
	}

	public class PaymentSchedulesDetail
	{
		public List<PaymentScheduleDetail> PaymentSchedule;
	}

	public class PaymentScheduleDetail
	{
		public String PaymentDueDate = '';
		public String PaymentMadeDate = '';
		public String PaymentType = '';
		public String SourceApplicationCode = '';
		public String SourceApplicationCodeLiteral = '';  
	}

	public class AllergiesDetail
	{
		public List<AllergyDetail> Allergy;
	}

	public class AllergyDetail
	{
		public String HealthKey = '';
		public String HealthDescription = ''; 
	}

	public class CreditCardsDetail
	{
		public List<CreditCardDetail> CreditCard;
	}

	public class CreditCardDetail
	{
		public String CreditCardKey = '';
		public String CreditCardType = '';
		public String CreditCardTypeLiteral = '';
		public String FirstName = '';
		public String LastName = '';
		public String MiddleName = '';
		public String ExpirationMonth = '';
		public String ExpirationYear = '';
		public String IsActive = '';
		public String IsAutoCharge = '';
		public String CreditCardLast4Digits = '';
		public String IsSpendingAccount = '';
	}

	public class HealthConditionsDetail
	{
		public List<HealthConditionDetail> HealthCondition;
	}

	public class HealthConditionDetail
	{
		public String HealthDescription = ''; 
	}

	public class LogNotesDetail
	{
		public List<LogNoteDetail> LogNote;
	}

	public class LogNoteDetail
	{
		public String LogNoteCode = '';
		public String LogNoteDate = '';
		public String LogNoteKey = '';
		public String LogNoteMessage = '';
		public String LogNoteUser = '';
	}

	public class PlansDetail
	{
		public List<PlanDetail> Plan;
	}

	public class PlanDetail
	{
		public String PlanEffectiveDate = '';
		public String PlanTerminationDate = '';
		public String PlanType = '';
		public String Payer = '';  
	}
}