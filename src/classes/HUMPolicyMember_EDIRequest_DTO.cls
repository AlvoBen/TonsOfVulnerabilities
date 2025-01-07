/*******************************************************************************************************************************************************
Apex Class Name  : HUMPolicyMember_EDIRequest_DTO
Version          : 1.0
Created Date     : 01/24/2019
Function         : This class is used as request for EDI from PolicyMemberDetail_CE_HUM.
Test Class       : 

Modification Log :
* Developer Name            Review Number           Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Shailesh Mali                                  01/24/2019                 Original Version
************************************************************************************************************************************************/
public class HUMPolicyMember_EDIRequest_DTO {
    
    public GetEDIGroupInfoRequest GetEDIGroupInfoRequest;
    
    public class GetEDIGroupInfoRequest {
		public List<GroupDetail> GroupDetail;
	}
	
    public class GroupDetail {
		public String Group_id;
	}

}