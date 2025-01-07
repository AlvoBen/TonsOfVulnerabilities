public class ClaimLineItems_LDTO_HUM
{
    public ClaimDetailLines ClaimDetailLines;
    public class ClaimDetailLines 
    {
        @AuraEnabled public list<ClaimDetailLine> ClaimDetailLine;
    }
    
    public class ClaimDetailLine 
    {
       @AuraEnabled public boolean hideBool;
         @AuraEnabled public string Id ='';
       // @AuraEnabled public string sLineNbr ='';
        @AuraEnabled public string sLastProcessDate = '';
        @AuraEnabled public string sServiceStartDate = '';
        @AuraEnabled public string sServiceEndDate = '';
        @AuraEnabled public string sServiceCode = '';
        @AuraEnabled public string sServiceDesc = '';
        @AuraEnabled public string sChargeAmt = '';
        @AuraEnabled public string sBenAllowAmt = '';
        @AuraEnabled public string sProvWriteOff = '';
        @AuraEnabled public string sExcludeAmt = '';
        @AuraEnabled public string sBenDenyAmt = '';
        @AuraEnabled public string sCopayAmt = '';
        @AuraEnabled public string sDeductAmt = '';
        @AuraEnabled public string sCoInsAmt = '';
        @AuraEnabled public string sCobPaidAmt = '';
        @AuraEnabled public string sMbrRespAmt = '';
        @AuraEnabled public string sTotalInterest = '';
        @AuraEnabled public string sPaidAmt = '';
        @AuraEnabled public string sExLineItem = '';
        @AuraEnabled public string sPaidStatDesc = '';
        @AuraEnabled public string sRevenuecode = '';
        @AuraEnabled public string sCPTModCode = '';
        @AuraEnabled public string sAnesMinutes = '';
        @AuraEnabled public string sServUnitCnt = '';
        @AuraEnabled public string sCauseCd = '';
        @AuraEnabled public string sSrcPOTCode = '';
        @AuraEnabled public string CPTModCode = '';
        @AuraEnabled public string sPOTLongDesc = '';
        @AuraEnabled public string sFeeSchdId = '';
        @AuraEnabled public string sServicingFacility = '';
        @AuraEnabled public string sTIN = '';
        @AuraEnabled public string sNPI = '';
        @AuraEnabled public string sProviderCISContractId = '';
        @AuraEnabled public string sParInd = '';
        @AuraEnabled public DiagCode DiagCode;
        @AuraEnabled public string sToothCode = '';
        @AuraEnabled public string sToothSurface = '';
        @AuraEnabled public string sCheckNbr = '';
        @AuraEnabled public string sRemitNumber = '';        
        @AuraEnabled public PaymentCodes PaymentCodes = Null;
        @AuraEnabled public string sSrcClaimLineSequence = '';      
        @AuraEnabled public string sPayeeCd = '';
        @AuraEnabled public String sBankCode ='';                
        @AuraEnabled public String sEncounterIndicator = '';    
        @AuraEnabled public string sClaimProcessOptIndicator = '';
        @AuraEnabled public string sClaimProcessOptDescription = '';        
    }
    
    public class DiagCode 
    {
        @AuraEnabled public list<string> Code;
    }
    
    public class PaymentCodes 
    {
        @AuraEnabled public list<PaymentCode> PaymentCode;
    }
    
    public class PaymentCode 
    {
        @AuraEnabled public string sLegacyCd = '';
        @AuraEnabled public string sLegacyMessage = '';
        @AuraEnabled public string sEORDescription = '';
    }
    
}