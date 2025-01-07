/**************************************************************************** 
Apex Class Name  : PriorAuthHubRequest_DTO_HUM
Version          : 1.0  
Created Date     : May 26,2017
Function         : request helper for Prior Auth Hub Service
Test Class       : PriorAuthHubRequest_T_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Sumit Pharande                              26/05/2017                   Original Version
***************************************************************************************************/
public class PriorAuthHubRequest_DTO_HUM
{
    public GetPriorAuthStatusHistoryRequest GetPriorAuthStatusHistoryRequest;
    
    public class GetPriorAuthStatusHistoryRequest
    {
        public string MemberIdentity ='';
        public Integer MemberIdentifier;
        //pass 1 in MemberIdentifier if sending SDRPersonID and 2 for MemberID
    }
}