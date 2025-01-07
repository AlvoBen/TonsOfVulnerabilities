/****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Trina Ghosh                               07/28/2020                 Original Version
***************************************************************************************************/
public class ClaimSendStmt_ServiceRequest_H_HUM {
    public String DistributionChannel ='';
    public String SystemOfRecordId = '';//New Name 
    public String MessageFulfillmentId = '';//OLD Name
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
    public String AttachmentCategory = '';
    public String ReprintSystemOfRecord = '';
   
    //Recipient Roles
    public RecipientRoleDesigned RecipientRoleDesigned = null;
    public RecipientRoleMember RecipientRoleMember = null;
    public RecipientRoleOther RecipientRoleOther = null;
	public RecipientRoleProvider RecipientRoleProvider = null; 
    
    //Wrapper class for Recipient Role of Provider
    public class RecipientRoleProvider
    {
        public String ProviderId = '';
        public String ProviderIdType = '';
    } 
    
	//Wrapper class for Recipient Role of Other
    public class RecipientRoleOther
    {
        public String OtherId = '';
        public String OtherSystemCode = '';
        public String OtherSystemSuffix = '';
    }
    
	//Wrapper class for Recipient Role of Member
    public class RecipientRoleMember
    {
        public String MemberCardid = '';
        public String DependentCode = '';
        public String PlatformCode = '';
        public String SDRPersonId = '';
        public String MemberGenKey = '';
    }
    
	//Wrapper class for Recipient Role of Designed
    public class RecipientRoleDesigned
    {
        public String DesignedId = '';
        public String DesignedType = '';
    }

}