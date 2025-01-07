/**************************************************************************** 
Apex Class Name  : ClaimsServiceSummaryResponse_H_HUM_V2   
Version          : 1.0  
Created Date     : March 29, 2018
Function         : Response Helper for GetClaimsSummary method in Claims Service
Test Class       : ClaimsServiceSummaryResponse_T_HUM_V2
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Kritika Popat              212620             03/29/2018                 Claims V3 Original Version
*   Anjani Vaddadi                              06/03/2020                 REQ - 1083200 -CA Incident Wrong Product Type on Claim Details page And claim summary
* Anjani Vaddadi                                06/26/2020                 Rollback REQ -1083200
***************************************************************************************************/
public class ClaimsServiceSummaryResponse_H_HUM_V2   
{
    public ClaimSummaryReponse ClaimSummaryReponse;
    
    public class ClaimSummaryReponse 
    {
        public ClaimSummaryResults ClaimSummaryResults;
        public string EndRecordNumber ='';
        public string TotalRecordNumber = '';
    }
    
    public class ClaimSummaryResults 
    {
        public list<ClaimSummaryResult> ClaimSummaryResult;
    }
    
    public class ClaimSummaryResult 
    {
        public string AdjustmentIndicator = '';
        public string ClaimNumber = '';
        public string ClaimType = '';
        public string ClaimTypeCode = '';
        public string ClaimReceiptDate = '';
        public string ChargeAmount = '';
        Public String DisplayGroupId = '';
        Public String EncounterIndicator = '';
        Public String Id = '';
        public string LineOfBusinessCode = '';
        public string LastProcessDate = '';
        public string DeductibleAmount;
        public string CopaymentAmount;
        public string CoInsuranceAmount;
        public string PaidStatusDescription = '';
        public string StatusDescription = '';
        public string ServiceStartDate = '';
        public string ServiceEndDate = '';
        public string SourceNationalProviderId = '';
        public string ProviderTaxId = '';
        public string PreDeterminationNumber = '';
        public string PreDeterminationIndicator = '';
        public string ProviderName = '';
        public string PlatformCode = '';
        public string PaidAmount = '';
        public string MemberResponsibilityAmount = '';
        public string MemberId;
    }
}