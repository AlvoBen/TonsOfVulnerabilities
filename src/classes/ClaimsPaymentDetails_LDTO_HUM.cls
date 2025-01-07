public with sharing class ClaimsPaymentDetails_LDTO_HUM
{   

    @AuraEnabled public string sLastProcessDate {get; set;}
    @AuraEnabled public string sCheckNumber {get; set;}
    @AuraEnabled public string sPayeeName {get; set;}
    @AuraEnabled public string sPayeeAddress {get; set;}
    @AuraEnabled public string sCheckDate {get; set;}
    @AuraEnabled public string sPaymentType {get; set;}
    @AuraEnabled public string sPaymentAmount {get; set;}
    @AuraEnabled public string sPaymentTypeID {get; set;}
    @AuraEnabled public string sPayeeID {get; set;}
    @AuraEnabled public string sPaymentIssueDate {get; set;}
    @AuraEnabled public string sPaymentStatus {get; set;}
    @AuraEnabled public string sPaymentStatusDate {get; set;}
    @AuraEnabled public string sRemit {get;set;}  
    @AuraEnabled public string sPaymentUrl {get; set;}
    @AuraEnabled public string sSequenceNum {get; set;}
    @AuraEnabled public string sPayee {get; set;} 
    @AuraEnabled public boolean bPaymentInfoMsg {get; set;} 
    @AuraEnabled public string sBankAccCode {get; set;}    
    @AuraEnabled public ClaimDetails ClaimDetails;    
    
    public class ClaimDetails
    {
        @AuraEnabled public list<ClaimDetail> ClaimDetail;
    }
    
    public class ClaimDetail
    {
        @AuraEnabled public string ClaimNumber ='';
        @AuraEnabled public string ClaimPaymentAmount = '';
    }    
        
}