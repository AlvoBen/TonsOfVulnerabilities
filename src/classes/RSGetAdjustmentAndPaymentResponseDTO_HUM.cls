/******************************************************************************************************************
Apex class Name    : RSGetAdjustmentAndPaymentResponseDTO_HUM
Version            : 1.0
Function           : Response DTO for RightSource GetAdjustmentAndPayment Service
Created On         : 02/01/2017
Test Class         : RightSource_T_HUM

Modification Log:
*   Version             Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------                
*    1.0                Rajesh Chandgothia             60688               02/01/2017             Original Version -> REQ - 296897
*    1.1                Melkisan Selvaraj							       03/04/2019             REQ - 387544 : Splunk error logging Issues
------------------------------------------------------------------------------------------------------------------*/

public with Sharing class RSGetAdjustmentAndPaymentResponseDTO_HUM
{
    public AdjustmentsAndPaymentsResponse GetAdjustmentsAndPaymentsResponse ;
    public class AdjustmentsAndPaymentsResponse  
    {
       public AdjustmentDetails Adjustments;
       public PaymentDetails Payments;
    }
     
    public class AdjustmentDetails
    {
        public List<AdjustmentDetail> Adjustment;
		public string ErrorDescription = '';
    }
    public class AdjustmentDetail
    {
        public String AdjustmentAmount = '';
        public String AdjustmentDate = '';
        public String AdjustmentType = '';
    }
    public class PaymentDetails
    {
        public List<PaymentDetail> Payment;
    }
    public class PaymentDetail
    {
        public String PaymentScheduledDate = '';
        public String PaymentProcessedDate = '';
        public String CreditCardTypeLiteral = '';
        public String Last4Digit= '';
        public String ChkOrMoneyOrderNumber  = '';
        public String Amount = '';  
        public String OrderNumber = '';  
        public String SourceApplicationCode = '';
        public String SourceApplicationCodeLiteral = '';   
             
    }
}