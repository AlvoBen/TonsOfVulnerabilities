/******************************************************************************************************************
Apex class Name    : ApplicationSearch_Response_DTO_HUM
Version            : 1.0
Function           : Request DTO for queue AM Search response
Created On         : 12/18/2017
Test Class         : 

Modification Log:
*   Version        Developer                     Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0             Syed Mubarak                                    04/30/2019          Original version
*   1.1				Kiran Kumar B									07/05/2022			User Story - 2568652: Billing- Search Enrollment tab- add 'Billing Payment Option Type' field
*   1.0             Ravikanth									    05/18/2023          User Story 4404737: T1PRJ0191994- MF 126, 127- Display Member Race and Ethnicity in CRM Service- ‘Member Application Detail’ page (Classic)
*	1.2 			Vardhman Jain								    05/18/2023          US 4407458- Race and ethnicity story.
------------------------------------------------------------------------------------------------------------------- */
public class ApplicationSearch_Response_DTO_HUM
{
    public EnrollmentResponseWrapper EnrollmentResponse;
    public class EnrollmentResponseWrapper
    {
        public List<enrollmentDetailWrapper> EnrollmentDetail;
    }
    
    public class enrollmentDetailWrapper
        {
            public string SysAssnNumber = '';
            public string FirstName = '';
            public string MiddleName = '';
            public string LastName = '';
            public string NameSuffix = '';
            public string MedicareId = '';
            public string SSN = '';
            public string DOB = '';
            public string MailAddress1 = '';
            public string MailAddress2 = '';
            public string MailCity = '';
            public string MailState = '';
            public string MailZipCode = '';
            public string CountyName = '';
            public string OtherAddress1 = '';
            public string OtherAddress2 = '';
            public string OtherCity = '';
            public string OtherState = '';
            public string OtherZipCode = '';
            public string HomePhone = '';
            public string WorkPhone = '';
            public string GenderCode = '';
            public string MemberLangPreference = '';
            public string SmokeIndicator = '';
            public string DisabledIndicator = '';
            public string GroupId = '';
            public string BSN = '';
            public string SalesRepName = '';
            public string SalesRepSSN = '';
            public string SalesType = '';
            public string CoverageEffectiveDate = '';
            public string CoverageTermDate = '';
            public string ContractId = '';
            public string PlanBenefitPackage = '';
            public string PCPNumber = '';
            public string PCPName = '';
            public string MedicaidId = '';
            public string ESRDIndicator = '';
            public string PaymentOptIndicator = '';
			public string PaymentOptType = '';
            public string ElectionTypeCd = '';
            public string SignatureType = '';
            public string SignatureDate = '';
            public string MarketReceiptDate = '';
            public string DCNNumber = '';
            public string OSBEffectiveDate = '';
            public string OSBPackage = '';
            public string EmployerId = '';
            public string ApplicationId = '';
            public string OECConfirmationId = '';
            public string PartAEligibleDate = '';
            public string PartBEligibleDate = '';
            public string SepReasonCode = '';
            public string FileDate = '';
            public string ProcessedDate = '';
            public string ApplicationChannel = '';
            public string CurGroupId = '';
            public string POAFirstName = '';
            public string POALastName = '';
            public string POARelationType = '';
            public string POAPhone = '';
            public string FileTrackId = '';
            public string RecipientTrackId = '';
            public string JobName = '';
            public string CreationTs = '';
            public string BarCodeBase = '';
            public string BarCodeId1 = '';
            public string BarCodeId2 = '';
            public string BarCodeId3 = '';
            public string BarCodeId4 = '';
            public string BarCodeId5 = '';
            public string BarCodeId6 = '';
            public string BarCodeId7 = '';
            public string ExchangeSenderId = '';
            public string ExchangeSubscriberId = '';
            public string ExchangeMemberId = '';
            public string ApplicantRelationType = '';
            public string QHPId = '';
            public string CSRLevel = '';
            public string CSREffectiveDate = '';
            public string CSREndDate = '';
            public string APTCAmount = '';
            public string APTCEffectiveDate = '';
            public string APTCEndDate = '';
            public string ExchangeTransactionId = '';
            public string SearchType = '';
            public string SegmentId = '';
            public string SEPDate = '';
            public string PlanYear = '';
			//US 4407458
            public string RaceCd = '';
            public string EthinicityCd = '';
        }
    
}