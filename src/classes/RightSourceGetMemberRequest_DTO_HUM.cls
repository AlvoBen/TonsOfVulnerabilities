/******************************************************************************************************************
Apex class Name    : RightSourceGetMemberRequest_DTO_HUM
Version            : 1.0
Function           : Request DTO for RightSource GetMember
Created On         : December 9 2016
Test Class         : RightSource_T_HUM

Modification Log:
*   Version             Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*    1.0                Vishal Verma                                 12/09/2016             Original Version -> REQ - 291073
*    1.1                Samridh Manucha                              12/22/2016             REQ - 291163
*    1.2                Sharan Shanmugam                             02/07/2017             REQ - 299420
*    1.3                Sharan Shanmugam                             02/09/2017             Modified as part of REQ - 299420
*    2.0                Rajesh Chandgothia            61773          02/09/2017             Fixing Defect 258966.0001
*    2.1                Rajesh Keswani                112036         07/14/2017             Adding network Id in Request
*	 2.2				Pradeep Dani				  302363		 08/13/2018				Added request node for duplicate customers.
* 	 2.3				Joel George								     10/31/2018				REQ - 367769
------------------------------------------------------------------------------------------------------------------*/
public with sharing class RightSourceGetMemberRequest_DTO_HUM
{
	public MemberRequest GetMemberRequest;

	public class MemberRequest
	{
		public String EnterprisePersonID = '';  
		public String LogNoteStartDate = '';
		public String LogNoteEndDate = '';
		public String IncludeActiveLogNotes = '';
		public String IncludeActiveAddresses = '';
		public String IncludeInactiveAddresses = '';
		public String IncludeFinances = '';
		public String IncludeAccountAdjustments = '';
		public String IncludeConsentStatus = '';
		public String IncludeActiveCreditCards = '';
		public String IncludeInactiveCreditCards = '';
		public String IncludeStaleData = '';
		public String IncludeCmrStatus = '';
		public String CustomerServiceId = '';
		public string returnDuplicateCustomers = '';//"r" has be lower case as DP is mapped to lower case.
		public string IncludeSpecialty = '';
		public string includeCommunicationPreferences = '';
	}
}