/**************************************************************************** 
Apex Class Name  : ClaimsServiceSummaryRequest_H_HUM_V2
Version          : 1.0  
Created Date     : March 06, 2018
Function         : Request helper for GetClaimsSummary method in Claims service
Test Class       : ClaimsServiceSummaryRequest_T_HUM_V2
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Kritika Popat              212620            03/06/2018                  New Claims Summary V3 Request  
***************************************************************************************************/
public class ClaimsServiceSummaryRequest_H_HUM_V2
{   
   public GetClaimsSummary GetClaimsSummary;
   public class GetClaimsSummary 
   {
        public list<MemberRequests> MemberRequests;
        public string ClaimNumber = '';
        public string ProviderTaxId =''; 
        public string ServiceEffectiveDate = '';   
        public string ServiceEndDate = '';
        public string StartCount = '';
    }
    
    public class MemberRequests 
    {
        public string MemberId = '';
        public string MemberDependentCode = '';
    }    
}