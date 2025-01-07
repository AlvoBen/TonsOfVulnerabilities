/*******************************************************************************************************************************
Apex Class Name : RSCreateOneTimePaymentRequest_DTO_HUM.cls
Version         : 1.0
Created On      : 08/26/2020
Function        : Request Structure Class for PharmacyFinancial_C_HUM.

Modification Log:
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Shailesh Mali                                     08/26/2020                   REQ - 1152078 - PR00094920- MF 1  - Humana Pharmacy- Ability to create realtime one time credit card charges in CRM  (RxXP) (ID# 13)
*********************************************************************************************************************************/
public with sharing class RSCreateOneTimePaymentRequest_DTO_HUM {

   public EditMemberRequest EditMemberRequest;
	
	public class EditMemberRequest {
        public List<memberDetail> members;
		public String CustomerServiceId = '';		
	}
	
    public class memberDetail {
		public String EnterprisePersonID = '';		
		public payments payments;  
	}
    
    public class payments {
		public List<payment> payment;
	}    
   
	public class payment {
		public Decimal amount;
        public String creditCardKey='';
		public Boolean isPaymentAuthorized;
		public Date paymentDate{get;set;}
	}
	
}