/**************************************************************************** 
Apex Class Name  : PharmacyGetMember_CreditCard_DTO_HUM   
Version          : 1.0  
Created Date     : Feb 16, 2017
Function         : Wrapper class for GetMember Pharmacy service to store Credit card details
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*------------------------------------------------------------------------------------------------------------
* Sharan Shanmugam							  02/16/2017			Original version - Implementation of REQ - 296663 --> View credit card information from getMember details
* Sharan Shnamugam                            03/22/2017            REQ - 300147 : Create finance request 
* Rajesh Keswani           82227              03/30/2017            REQ - 302791: financial offline service mode 
* Shailesh Mali                               08/26/2020            REQ - 1152078 - PR00094920- MF 1  - Humana Pharmacy- Ability to create realtime one time credit card charges in CRM  (RxXP) (ID# 13)
***************************************************************************************************/
public class PharmacyGetMember_CreditCard_DTO_HUM 
{
	public List<RightSourceGetMemberResponse_DTO_HUM.CreditCardDetail> CreditCard  = new List<RightSourceGetMemberResponse_DTO_HUM.CreditCardDetail>(); 
	public String sAccNumber = '';
	public String staleData = ''; 
	public string BalanceDueFromDemographic = '';
}