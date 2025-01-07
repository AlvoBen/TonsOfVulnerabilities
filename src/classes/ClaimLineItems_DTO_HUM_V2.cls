/*******************************************************************************************************************************************************
Apex Class Name  : ClaimLineItems_DTO_HUM_V2
Version          : 1.0
Created Date     : 03/06/2018
Function         : This class is wrapper class to serialize after manipulating the data from service
Test Class       : ClaimLineItems_DTO_T_HUM_V2
Modification Log :

* Developer Name            Code Review           Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Prasanthi Kandula           212620              03/06/2018                 Original Version
* Raja Sekhar Reddy M V                           11/12/2018                 REQ - 353088 - Added code for Bank code
* Prasanthi Kandula                               09/19/2019                 REQ - 404535 - Added sEORDescription to view the appropriate Explanation Code description
* Abhishek Mangutkar                              05/24/2021         		 US - 2201523 -  Add 'Claims Payment Responsibility' Field in Claim Line Item Accordion (CRM Service)
************************************************************************************************************************************************/

public class ClaimLineItems_DTO_HUM_V2
{
    public ClaimDetailLines ClaimDetailLines;
    public class ClaimDetailLines 
    {
        public list<ClaimDetailLine> ClaimDetailLine;
    }
    
    public class ClaimDetailLine 
    {
        public string sLineNbr ='';
        public string sLastProcessDate = '';
        public string sServiceStartDate = '';
        public string sServiceEndDate = '';
        public string sServiceCode = '';
        public string sServiceDesc = '';
        public string sChargeAmt = '';
        public string sBenAllowAmt = '';
        public string sProvWriteOff = '';
        public string sExcludeAmt = '';
        public string sBenDenyAmt = '';
        public string sCopayAmt = '';
        public string sDeductAmt = '';
        public string sCoInsAmt = '';
        public string sCobPaidAmt = '';
        public string sMbrRespAmt = '';
        public string sTotalInterest = '';
        public string sPaidAmt = '';
        public string sExLineItem = '';
        public string sPaidStatDesc = '';
        public string sRevenuecode = '';
        public string sCPTModCode = '';
        public string sAnesMinutes = '';
        public string sServUnitCnt = '';
        public string sCauseCd = '';
        public string sSrcPOTCode = '';
        public string CPTModCode = '';
        public string sPOTLongDesc = '';
        public string sFeeSchdId = '';
        public string sServicingFacility = '';
        public string sTIN = '';
        public string sNPI = '';
        public string sProviderCISContractId = '';
        public string sParInd = '';
        public DiagCode DiagCode;
        public string sToothCode = '';
        public string sToothSurface = '';
        public string sCheckNbr = '';
        public string sRemitNumber = '';        
        public PaymentCodes PaymentCodes = Null;
        public string sSrcClaimLineSequence = '';      
        public string sPayeeCd = '';
        public String sBankCode ='';                
        public String sEncounterIndicator = '';    
		public string sClaimProcessOptIndicator = '';
		public string sClaimProcessOptDescription = '';				
    }
    
    public class DiagCode 
    {
        public list<string> Code;
    }
    
    public class PaymentCodes 
    {
        public list<PaymentCode> PaymentCode;
    }
    
    public class PaymentCode 
    {
        public string sLegacyCd = '';
        public string sLegacyMessage = '';
        public string sEORDescription = '';
    }
    
}