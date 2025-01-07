/*******************************************************************************************************************************

Class Name : ClaimDetails_LWC_DTO_HUM_V2.cls.

Modification Log: 
  Developer Name           Code Review                      Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------- 
* suraj patil												06/10/2022				  Original Version
* Suraj Patil												04/18/2023				  US-4399869: Claims- Add "Total of Last Processed Date" field/data 
*********************************************************************************************************************************/
public with sharing class ClaimDetails_LWC_DTO_HUM_V2 {
        @AuraEnabled
        public String sClaimNbr {get; set;} 
        
        @AuraEnabled
        public String sClmMediaCd {get; set;}
        
        @AuraEnabled
        public String sPatRecordNbr {get; set;} 
        @AuraEnabled
        public String sSrcAuthKey {get; set;}
        @AuraEnabled
        public boolean bAuthKeyFlag {get; set;}
        @AuraEnabled
        public String sDeficiencyInd {get; set;}  
        @AuraEnabled
        public String sDeficiencyField {get; set;}
        @AuraEnabled
        public String sDeficiencyDate {get; set;}
        @AuraEnabled
        public String sDeficiencyDesc {get; set;}
        @AuraEnabled
        public List<String> lstDiagnosisCodes {get; set;}
        @AuraEnabled
        public String sTin {get; set;}
        @AuraEnabled
        public String sNpi {get; set;}
        
        @AuraEnabled
        public String sSrcLvCASPrefix {get; set;}
        @AuraEnabled
        public String sTotalInterestFooter {get; set;}
        @AuraEnabled
        public String sChargeAmt {get; set;}   
        @AuraEnabled
        public String sBenAllowAmt {get; set;} 
        @AuraEnabled
        public String sProvWriteOff {get; set;}
        @AuraEnabled
        public String sBenDenyAmt {get; set;}
        @AuraEnabled
        public String sMbrRespAmt {get; set;}
        @AuraEnabled
        public String sPaidAmt {get; set;} 
        @AuraEnabled
        public String sTotalInterest {get; set;}
        @AuraEnabled
        public String sExcludeAmt {get; set;}
        @AuraEnabled
        public String sCobPaidAmt {get; set;}
        @AuraEnabled
        public String sDiagIndicator {get; set;} 
        @AuraEnabled
        public String sFirstDiagnosisCode{get; set;} 
        @AuraEnabled
        public String sPaymentMethod {Get; Set;}     
        @AuraEnabled
        Public String sBankCode {Get; Set;}
        @AuraEnabled
        Public boolean bShowMore {Get;Set;}
        @AuraEnabled
        Public string sProviderSpecFirstRecord {get;set;}
        @AuraEnabled
        public string sProviderSpecAllRecords {Get;Set;}
        @AuraEnabled
        Public String listPrimarySpeciality {Get;Set;}
        @AuraEnabled public list<ClaimDetailLine> ClaimDetailLines;
        @AuraEnabled public ClaimDetailLineTotal ClaimDetailLineTotal {get;set;}

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
			@AuraEnabled public string sServiceCodeLink = '';
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
        public class ClaimDetailLineTotal
        {
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
            @AuraEnabled public string sPaidAmt = '';
        }
}