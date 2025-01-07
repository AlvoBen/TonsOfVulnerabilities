/*******************************************************************************************************************************************************
Apex Class Name  : ClaimsSummary_Wrapper_LDTO_HUM
Version          : 1.0
Created Date     : 06/10/2022
Function         : This class is wrapper class to store data from service
Test Class       : 
*****************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number           Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
*   Suraj Patil                                     06/10/2022         			Original Version 
*   Anuradha Gajbhe                                 08/10/2022                  US#3587828 - Claims--Lightning--Additional Capabilities on Claims Line Items: informational Messages Claim Summary 3
************************************************************************************************************************************************/

public with sharing class ClaimsSummary_Wrapper_LDTO_HUM
{
    @AuraEnabled public String sAdjustInd {set; get;}
    @AuraEnabled public String sClaimNbr {set; get;}
    @AuraEnabled public String sClaimType {set; get;}              
    @AuraEnabled public String sClaimTypeCd {set; get;}
    @AuraEnabled public String sClmGenKey {set; get;}
    @AuraEnabled public String sClmReceiptDate {set; get;}
    @AuraEnabled public String sChargeAmt {get; set;}
    @AuraEnabled public String sDispGrpID {set; get;}
    @AuraEnabled public String sEncounterIndicator {set; get;}
    @AuraEnabled public String sLastProcessDate {set; get;}
    @AuraEnabled public string sMbrRespAmt {set; get;}
    @AuraEnabled public string sPaidAmt {set; get;}
    @AuraEnabled public string sPlatformCd {set; get;}
    @AuraEnabled public string sPreDeterminationIndicator {set; get;}
    @AuraEnabled public string sPreDeterminationNumber {set; get;}
    @AuraEnabled public string sProviderID {set; get;}
    @AuraEnabled public string sProviderName {set; get;}
    @AuraEnabled public string sSRCNPIID {set; get;}
    @AuraEnabled public string sServiceEndDate {set; get;}
    @AuraEnabled public string sServiceStartDate {set; get;}
    @AuraEnabled public string sStatusDesc {set; get;}
    @AuraEnabled public string sDeductAmt {set; get;}
    @AuraEnabled public string sCopayAmt {set; get;}
    @AuraEnabled public string sCoInsAmt {set; get;}
    @AuraEnabled public string sMemberId {set; get;}
    @AuraEnabled public string sLOBCd {set; get;}
    @AuraEnabled public string sClmDetailLink {set; get;}
    @AuraEnabled public integer sEndRecordNumber {set; get;}
    @AuraEnabled public integer sTotalRecordNumber {set; get;}
    @AuraEnabled public String sAccountId {set; get;}
  
}