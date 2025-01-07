/*******************************************************************************************************************************************************
Apex Class Name  : ClaimsSummary_Wrapper_DTO_HUM
Version          : 1.0
Created Date     : 02/25/2016
Function         : This class is wrapper class to store data from service
Test Class       : ClaimsSummary_Wrapper_T_HUM
*****************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number           Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Kritika Popat                24466               02/25/2016                                Original Version
* Samridh Manucha                                  05/16/2016                Defect 225119 -Changing mapping for ProdType
* Anurag Shah                201051                03/13/2018          	 	 REQ - 354520:: SF - USER - Research/Attachments - MA Post Claims Summary.
************************************************************************************************************************************************/

public with sharing class ClaimsSummary_Wrapper_DTO_HUM
{
    public String sAdjustInd {set; get;}
    public String sClaimNbr {set; get;}
    public String sClaimType {set; get;}              
    public String sClaimTypeCd {set; get;}
    public String sClmGenKey {set; get;}
    public String sClmReceiptDate {set; get;}
    public String sChargeAmt {get; set;}
    public String sDispGrpID {set; get;}
    public String sEncounterIndicator {set; get;}
    public String sLastProcessDate {set; get;}
    public string sMbrRespAmt {set; get;}
    public string sPaidAmt {set; get;}
    public string sPlatformCd {set; get;}
    public string sPreDeterminationIndicator {set; get;}
    public string sPreDeterminationNumber {set; get;}
    public string sProviderID {set; get;}
    public string sProviderName {set; get;}
    public string sSRCNPIID {set; get;}
    public string sServiceEndDate {set; get;}
    public string sServiceStartDate {set; get;}
    public string sStatusDesc {set; get;}
    public string sDeductAmt {set; get;}
    public string sCopayAmt {set; get;}
    public string sCoInsAmt {set; get;}
    public string sMemberId {set; get;}
    public string sLOBCd {set; get;}
  
}