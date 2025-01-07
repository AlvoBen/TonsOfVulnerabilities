/******************************************************************************************************************
Apex class Name    : BillingProfileRequest_LDTO_HUM
Version            : 1.0
Function           : Request DTO for Billing Service
Created On         : 05/23/2022
Test Class         : BillingProfile_DTO_LT_HUM

Modification Log:
*   Version             Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0                 Suraj Patil                                     05/23/2022          Original Version
------------------------------------------------------------------------------------------------------------------*/
public with sharing class BillingProfileRequest_LDTO_HUM
{
    public class PremiumInfo
    {
        @AuraEnabled public String IncludePremiumInformation = '';
        @AuraEnabled public String RateLevel = '';
    }

    public class IncludeCriteria
    {
        @AuraEnabled public String PaymentOption = '';
        @AuraEnabled public String PayorInfo = '';
        @AuraEnabled public String CollectionInfo = '';
        @AuraEnabled public String BillingMode = '';
        @AuraEnabled public String BillingFrequency = '';
        @AuraEnabled public String BalanceInfo = '';
        @AuraEnabled public InvoiceInfo InvoiceInfo;
        @AuraEnabled public PremiumInfo PremiumInfo;
        @AuraEnabled public String TransactionInfo = '';
        @AuraEnabled public String TransactionType = '';
        @AuraEnabled public String NbrOfTransactions = '';
        @AuraEnabled public String BillingAddressInfo='';  
        @AuraEnabled public string GetGroupInfo='';
    }

    public GetBillingAccountInfoRequest GetBillingAccountInfoRequest;

    public class GetBillingAccountInfoRequestCriteria
    {
        @AuraEnabled public RequestContext RequestContext;
        @AuraEnabled public SearchCriteriaList SearchCriteria;
        @AuraEnabled public IncludeCriteria IncludeCriteria;
    }

    public class InvoiceInfo
    {
        @AuraEnabled public String IncludeInvoiceInformation = '';
        @AuraEnabled public String NbrOfInvoices = '';
    }

    public class RequestContext
    {
        @AuraEnabled public String CallingSystemID = '';
        @AuraEnabled public String UserID = '';
        @AuraEnabled public String UserIDType = '';
        @AuraEnabled public String ProcessDate = '';
       @AuraEnabled public integer ProfileSequenceNbr=0;
    }

    public class GetBillingAccountInfoRequest
    {
        @AuraEnabled public GetBillingAccountInfoRequestCriteria GetBillingAccountInfoRequestCriteria;
    }

    public class SearchCriteriaList
    {
        @AuraEnabled public List<SearchCriteria> SearchCriteria;
    }

    public class SearchCriteria
    {
        @AuraEnabled public String PlatformCD = '';
        @AuraEnabled public String IDType = '';
        @AuraEnabled public String SearchID = '';
    }

}