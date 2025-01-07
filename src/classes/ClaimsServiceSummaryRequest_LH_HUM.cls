/**************************************************************************** 
Apex Class Name  : ClaimsServiceSummaryRequest_H_HUM
Version          : 1.0  
Created Date     : 6/10/2022
Function         : Request helper for GetClaimsSummary method in Claims service
Test Class       : 
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
*   Suraj Patil                                      	 06/10/2022         Original Version 
***************************************************************************************************/
public class ClaimsServiceSummaryRequest_LH_HUM
{   
   @AuraEnabled public GetClaimsSummary GetClaimsSummary;
   public class GetClaimsSummary 
   {
        @AuraEnabled public list<MemberRequests> MemberRequests;
        @AuraEnabled public string ClaimNumber = '';
        @AuraEnabled public string ProviderTaxId =''; 
        @AuraEnabled public string ServiceEffectiveDate = '';   
        @AuraEnabled public string ServiceEndDate = '';
        @AuraEnabled public string StartCount = '';
    }
    
    public class MemberRequests 
    {
        @AuraEnabled public string MemberId = '';
        @AuraEnabled public string MemberDependentCode = '';
    }    
}