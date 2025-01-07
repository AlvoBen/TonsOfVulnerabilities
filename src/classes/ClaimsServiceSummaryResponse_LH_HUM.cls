/**************************************************************************** 
Apex Class Name  : ClaimsServiceSummaryResponse_LH_HUM 
Version          : 1.0  
Created Date     : 6/10/2022
Function         : Response Helper for GetClaimsSummary method in Claims Service
Test Class       : ClaimsServiceSummaryResponse_T_HUM_V2
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
*   Suraj Patil                                      	 06/10/2022         Original Version
***************************************************************************************************/
public class ClaimsServiceSummaryResponse_LH_HUM
{
    @AuraEnabled public ClaimSummaryReponse ClaimSummaryReponse;
    
    public class ClaimSummaryReponse 
    {
        @AuraEnabled public ClaimSummaryResults ClaimSummaryResults;
        @AuraEnabled public string EndRecordNumber ='';
        @AuraEnabled public string TotalRecordNumber = '';
    }
    
    public class ClaimSummaryResults 
    {
        @AuraEnabled public list<ClaimSummaryResult> ClaimSummaryResult;
    }
    
    public class ClaimSummaryResult 
    {
        @AuraEnabled public string AdjustmentIndicator = '';
        @AuraEnabled public string ClaimNumber = '';
        @AuraEnabled public string ClaimType = '';
        @AuraEnabled public string ClaimTypeCode = '';
        @AuraEnabled public string ClaimReceiptDate = '';
        @AuraEnabled public string ChargeAmount = '';
        @AuraEnabled public String DisplayGroupId = '';
        @AuraEnabled public String EncounterIndicator = '';
        @AuraEnabled public String Id = '';
        @AuraEnabled public string LineOfBusinessCode = '';
        @AuraEnabled public string ClaimDetailLink = '';
        @AuraEnabled public string LastProcessDate = '';
        @AuraEnabled public string DeductibleAmount;
        @AuraEnabled public string CopaymentAmount;
        @AuraEnabled public string CoInsuranceAmount;
        @AuraEnabled public string PaidStatusDescription = '';
        @AuraEnabled public string StatusDescription = '';
        @AuraEnabled public string ServiceStartDate = '';
        @AuraEnabled public string ServiceEndDate = '';
        @AuraEnabled public string SourceNationalProviderId = '';
        @AuraEnabled public string ProviderTaxId = '';
        @AuraEnabled public string PreDeterminationNumber = '';
        @AuraEnabled public string PreDeterminationIndicator = '';
        @AuraEnabled public string ProviderName = '';
        @AuraEnabled public string PlatformCode = '';
        @AuraEnabled public string PaidAmount = '';
        @AuraEnabled public string MemberResponsibilityAmount = '';
        @AuraEnabled public string MemberId;
    }
}