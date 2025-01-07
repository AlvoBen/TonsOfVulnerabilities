public class ClaimsPaymentDetailResponse_LH_HUM 
{
    @auraEnabled public GetEhub835XmlResponse GetEhub835XmlResponse;
    
    public class GetEhub835XmlResponse 
    {
        @auraEnabled public GetEhub835XmlResult GetEhub835XmlResult;
    }
    
    public class GetEhub835XmlResult 
    {
        @auraEnabled public list<Transaction_835> Transaction_835;
    }
    
    public class Transaction_835 
    {
        @auraEnabled public string CheckNumber = '';
        @auraEnabled public string PayeeName = '';
        @auraEnabled public string PayeeAddress = '';
        @auraEnabled public string CheckDate = '';
        @auraEnabled public string PaymentType = '';
        @auraEnabled public string PaymentAmount = '';
        @auraEnabled public ClaimDetails ClaimDetails;
        @auraEnabled public string PaymentTypeID = '';
        @auraEnabled public string PayeeID = '';
        @auraEnabled public string PaymentIssueDate = '';
        @auraEnabled public string PaymentStatus = '';
        @auraEnabled public string PaymentStatusDate = '';
    }
    
    public class ClaimDetails
    {
        @auraEnabled public list<ClaimDetail> ClaimDetail;
    }
    
    public class ClaimDetail
    {
        @auraEnabled public string ClaimNumber = '';
        @auraEnabled public string ClaimPaymentAmount = '';
    }
}