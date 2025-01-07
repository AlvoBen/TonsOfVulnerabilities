/******************************************************************************************************************
Apex class Name    : RightSourceEditMemberRequest_DTO_HUM
Version            : 1.0
Function           : Request DTO for RightSource Edit member
Created On         : 02/02/2017
Test Class         : RightSource_T_HUM

Modification Log:
*   Version             Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------                
*    1.0                Rajesh Chandgothia            60688            02/01/2017            Original Version -> REQ - 291077
*    1.1                Rajesh Keswani           	  66068            02/20/2017            Added capType field  -> REQ - 301418
*    1.2                Sumit Pharande          	  76689            03/15/2017            Added isSpendingAccount field  -> REQ - 301512
*    1.3                Sumit Pharande          	  76689            03/15/2017            Added consentBeginDate field  -> REQ - 304862
*    1.4                Sumit Pharande                                 03/22/2017            Added consentEndDate field  -> REQ - 304862
*    1.5                Rajesh Keswani            	  112036           07/14/2017            Adding network Id in Request
* 	 1.6				Joel George										10/31/2018			 REQ - 367769
------------------------------------------------------------------------------------------------------------------*/
public with sharing class RightSourceEditMemberRequest_DTO_HUM
{
    
    public class communicationPreference
	{
		public String commPrefType ='';
		public String commPrefValue = '';	
	}
	
	public class communicationPreferences
	{
        public List<communicationPreference> communicationPreference;   
	}
	
    public GetEditMemberRequest EditMemberRequest;
    
    public class GetEditMemberRequest 
     {
       public List<memberDetail> members;
       public string CustomerServiceId = '';
     }
    public class memberDetail
     {
       public String EnterprisePersonID = '';  
       public string email = '';
       public addressDetails addresses;
       public creditCardDetails creditCards;
       public customerDetail customerDetail;
       public customerPreference customerPreference;
       public logNoteDetails logNotes;    
       public phoneDetails phones;
       public communicationPreferences communicationPreferences; 
     } 
    public class customerPreference
     {
         public string capType;
     }
     
    public class addressDetails
     {
         public List<addressDetail> address;
     } 
    public class addressDetail
     {
        public String addressKey = '';  
        public String addressLine1 = '';    
        public String addressLine2 = '';  
        public String addressType = '';
        public String addressName = '';  
        public String city = '';  
        public String isActive = '';    
        public String overrideReasonCode = '';  
        public String stateCode = '';
        public String uspsValidate = ''; 
        public String zipCode = '';     
     }
    public class creditCardDetails
     {
        public List<creditCardDetail> creditCard;
     }
    public class creditCardDetail
     {
        public String creditCardKey = '';  
        public String creditCardType = '';    
        public String expirationMonth = '';  
        public String expirationYear = '';
        public String firstName= '';  
        public String isActive= '';  
        public String isAutoCharge = '';    
        public String lastName = '';  
        public String middleName = '';
        public String tokenKey = ''; 
        public String isSpendingAccount= ''; 		
     }
    public class customerDetail
     {
        public String cmrStatus = '';
		public String consentBeginDate = '';
        public String email = '';
        public string consentEndDate= '';         
     }    
    public class logNoteDetails
     {
        public List<logNoteDetail> logNote;
     }
    public class logNoteDetail
     {
        public String logNoteCode = '';  
        public String logNoteDate = '';    
        public String logNoteKey = '';  
        public String logNoteMessage = '';
        public String logNoteUser= '';
     }
    public class phoneDetails
     {
        public List<phoneDetail> phone;
     }  
    public class phoneDetail
     {
        public String phoneKey = '';  
        public String phoneType = '';    
        public String phoneValue = '';  
     } 
  }