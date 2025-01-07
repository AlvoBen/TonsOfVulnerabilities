/*******************************************************************************************************************************************************
Apex Class Name  : ClaimsPaymentDetails_DTO_HUM
Version          : 1.0
Created Date     : 04/23/2016
Function         : This class is wrapper class to store Claim Payment details data from service
Test Class       : ClaimsPaymentDetails_T_HUM
Modification Log :

* Developer Name            Review Number           Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Muralidhar Kollu          26844                04/23/2016                 Original Version
* Kritika P Popat           26844                05/02/2016                 Added Remit Number
* Pallavi Ravishankar       29226                06/02/2016                 Added Payment URL for req 263142
* Prasanthi Kandula         31877                07/05/2016                 REQ - 270129 Added sequenceNumber to display on Payments Table
* Muralidhar Kollu          31877                07/05/2016                 Defect # 232217.0001 - 249360: Payee field not returning correct values    
* Prasanthi Kandula         36268                08/22/2016                 REQ - 277892 Defect 235497: Added bPaymentInfoMsg
************************************************************************************************************************************************/

public with sharing class ClaimsPaymentDetails_DTO_HUM
{   

    public string sLastProcessDate {get; set;}
    public string sCheckNumber {get; set;}
    public string sPayeeName {get; set;}
    public string sPayeeAddress {get; set;}
    public string sCheckDate {get; set;}
    public string sPaymentType {get; set;}
    public string sPaymentAmount {get; set;}
    public string sPaymentTypeID {get; set;}
    public string sPayeeID {get; set;}
    public string sPaymentIssueDate {get; set;}
    public string sPaymentStatus {get; set;}
    public string sPaymentStatusDate {get; set;}
    public string sRemit {get;set;}  
    public string sPaymentUrl {get; set;}
    public string sSequenceNum {get; set;}
    public string sPayee {get; set;} 
    public boolean bPaymentInfoMsg {get; set;} 
    public string sBankAccCode {get; set;}    
    public ClaimDetails ClaimDetails;    
    
    public class ClaimDetails
    {
        public list<ClaimDetail> ClaimDetail;
    }
    
    public class ClaimDetail
    {
        public string ClaimNumber ='';
        public string ClaimPaymentAmount = '';
    }    
        
}