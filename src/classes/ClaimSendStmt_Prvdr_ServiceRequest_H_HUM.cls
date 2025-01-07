/**************************************************************************** 
Apex Class Name  : ClaimSendStmt_Prvdr_ServiceRequest_H_HUM
Version          : 1.0  
Function         : Request helper for Provider Claim Send Statement
Test Class       : ClaimSendStmt_Pvdr_ServiceRequest_HT_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Kritika Popat            295053               09/28/2018                 Original Version
***************************************************************************************************/
public class ClaimSendStmt_Prvdr_ServiceRequest_H_HUM
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
    public RecipientRoleProvider RecipientRoleProvider;
    
    public class RecipientRoleProvider
    {
        public String ProviderId = '';
        public String ProviderIdType = '';
    }
    
}