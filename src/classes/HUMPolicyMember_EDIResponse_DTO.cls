/*******************************************************************************************************************************************************
Apex Class Name  : HUMPolicyMember_EDIResponse_DTO
Version          : 1.0
Created Date     : 01/24/2019
Function         : This class is used as response  for EDI from PolicyMemberDetail_CE_HUM.
Test Class       : 

Modification Log :
* Developer Name            Review Number           Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Shailesh Mali                                  01/24/2019                 Original Version
************************************************************************************************************************************************/
public class HUMPolicyMember_EDIResponse_DTO {
   
   public GetEDIGroupInfoResponse GetEDIGroupInfoResponse;

	public class GetEDIGroupInfoResponse {
		public String Timestamp ='';
		public String ResponseType ='';
		public String ErrorCode ='';
		public String ErrorDescription ='';
		public String RequestGuid ='';
		public List<EdiGroupDetails> EdiGroupDetails;
	}

	public class EdiGroupDetails {
		public String ErrorCode='';
		public String ErrorDescription ='';
		public String GroupId ='';
		public String EDIGroup ='';
		public String Platform ='';
		public String PCP_PCDFlag ='';
		public String Frequency ='';
		public String Day1 ='';
		public String Day2 ='';
		public String Day3 ='';
		public String Day4 ='';
	}
    
}