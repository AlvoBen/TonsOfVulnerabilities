/**************************************************************************** 
Apex Class Name  : ClaimSendStmt_Other_ServiceRequest_H_HUM
Version          : 1.0  
Function         : Request helper for Member Claim Send Statement
Test Class       : ClaimSendStmt_Othr_ServiceRequest_HT_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Kritika Popat           295053                09/28/2018                 Original Version
***************************************************************************************************/
public class ClaimSendStmt_Other_ServiceRequest_H_HUM
{
    public String DistributionChannel ='';
    public String MessageFulfillmentId = '';
    public String RecipientFirstName = '';
    public String RecipientOrganizationName = '';
    public String RecipientMiddleName = '';
    public String RecipientLastName = '';
    public String RecipientAddressLine1 = '';
    public String RecipientAddressLine2 = '';
    public String RecipientAddressLine3 = '';
    public String RecipientCity = '';
    public String RecipientState = '';
    public String RecipientZip = '';
    public String RecipientFaxNumber = '';
    public String CCPInquiryNumber = '';
    public String SourceTransactionId = '';
    public RecipientRoleOther RecipientRoleOther;

    public class RecipientRoleOther
    {
        public String OtherId = '';
        public String OtherSystemCode = '';
        public String OtherSystemSuffix = '';
    }
}