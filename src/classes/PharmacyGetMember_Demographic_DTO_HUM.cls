/**************************************************************************** 
Apex Class Name  : PharmacyGetMember_Demographic_DTO_HUM   
Version          : 1.0  
Created Date     : Jan 9, 2017
Function         : Wrapper class for GetMember Pharmacys service to store Demographic details
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*------------------------------------------------------------------------------------------------------------
* Rajesh Chandgothia         55992            01/09/2017                 REQ-296805 Demographic DTO class for Getmember Service
* Rajesh Keswani             58348            01/27/2017                 Implementation of REQ - 296662 - View Address
* Rajesh Keswani             60569  	      02/03/2017                 Fixing Demo comments to put table in single section
* Sumit Pharande             65665       	  02/21/2017                 REQ-300749 Change address presentation on the demographics section
* Vishal Verma               78304            03/16/2017                 REQ-296926 : Added MTM Indicator
* Albin Arul                 78959            03/24/2017                 REQ - 296721 : Implemented Member Prescription section - Member Consent changes
* Rajesh Keswani             82973            03/24/2017               	 REQ - 302791: financial offline service mode
* Joel George								  10/31/2018				   REQ - 367769
* Pinky Vijur                                       06/22/2022                   US - 2892911:T1PRJ0100059 - MF 3- TECH - SF - Update URL for HPIE Services(Edit Member)
***************************************************************************************************/
public class PharmacyGetMember_Demographic_DTO_HUM{
        public String AccountNumber= '';   
        public String FirstName = '';   
        public String MiddleName = '';  
        public String LastName = '';    
        public String Email = '';  
        public String DOB= '';   
        public String PhoneNumber= ''; 
        public String PhoneKey = '';
    	public String AltPhoneKey = '';
        public String PlanType= '';    
        public String BalanceDue= ''; 
        public String MailingAddress= ''; 
        public String MailingCity= '';  		
        public String AltPhone= '';  
        public String EffectiveDate= '';    
        public String AccountLimit= '';  
        public String TerminationDate= '';   
        public String CapType= '';  
        Public String MemberConsent= ''; 
		public String MemberConsentApprovedDate= ''; 
    	public string NeedsMemberConsent= '';
		public String MTMIndicator= '';
		public string staleData = ''; 
        Public List<RightSourceGetMemberResponse_DTO_HUM.AddressDetail> Address  = New List<RightSourceGetMemberResponse_DTO_HUM.AddressDetail>(); 
        Public RightSourceGetMemberResponse_DTO_HUM.CreditCardsDetail CreditCardsDetail = New  RightSourceGetMemberResponse_DTO_HUM.CreditCardsDetail();
        public Boolean IsDeclined = false;
         
}