/**************************************************************************** 
Apex Class Name  : ClaimSendStmt_Mbr_ServiceResponse_H_HUM
Version          : 1.0  
Function         : Response helper for Member Claim Send Statement
Test Class       : ClaimSendStmt_Mbr_ServiceResponse_HT_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Kritika Popat           295053                09/28/2018                 Original Version
***************************************************************************************************/
public class ClaimSendStmt_Mbr_ServiceResponse_H_HUM
{
    public ReprintResponse ReprintResponse;
    
    public class ReprintResponse
    {
        public ReprintResult ReprintResult;
    }

    public class ReprintResult
    {
        public String Status ='';
        public String Description = '';
    }    
}