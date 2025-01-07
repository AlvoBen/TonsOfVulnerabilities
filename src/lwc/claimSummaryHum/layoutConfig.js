/*******************************************************************************************************************************
LWC JS Name : layoutConfig.js
Function    : This JS serves as helper to claimSummaryHum.js
Modification Log: 
  Developer Name           Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
* Anuradha Gajbhe                                           06/10/2022                   Original Version
* Vishal Shinde                                             22/08/2022                   3661714 - Additional Capabilities on Claims Line Items: Hover Over Claim LIST/Summary  
* Sagar G                                                   21/09/2022                   4842466 - T1PRJ0865978 - MF 4796385 - C03, Contact Servicing- Clarity for "ID" and "Pre D" fields- Separate on Claims Summary Page                      
*********************************************************************************************************************************/
import { getLabels } from 'c/crmUtilityHum';
const labels = getLabels();

export function getClaimSummaryStructure(){
    return claimSummaryLayout;
}

export const claimSummaryLayout= [[
    {
        "compoundx": true, "label": "Status",
		 headerHelpText: 'Claim Status',
        "compoundvalue": [
            { "text": true, "label": "Status", "fieldName": "sStatusDesc", "value": "", "disabled": "No" }]

    },
    {
        "compoundx": true, "label": "Date of Service",
        "compoundvalue": [
            { "text": true, "label": "Begin", "fieldName": "sServiceStartDate",  "value": "", "disabled": "No" },
            { "text": true, "label": "End", "fieldName": "sServiceEndDate", "value": "", "disabled": "No" }]

    },
    {
        "compoundx": true, "label": "Claim / Pre-D",
		headerHelpText: 'Type : Claim Type <br> Adj : Adjustment',
        "compoundvalue": [
            { "Id": true, "link": false, "icon": false, "hidden": true, "label": "Id", "fieldName": "sClaimNbr", "value": "11", "disabled": "No"},
            { "link": true, "label": "ID",  "fieldName": "sClaimNbr", "value": "", "disabled": "No", "linkToChange": "sClmDetailLink", "navToItem": "sClmDetailLink", "actionName": "|MBR_CLAIM_SUMMARY|","pageName": "Claim_Details_LWC"},
            { "text": true, "label": "Type", "fieldName": "sClaimType",  "value": "", "disabled": "No" },
            { "text": true, "label": "Adj", "fieldName": "sAdjustInd", "value": "", "disabled": "No" },
            { "text": true, "label": 'Pre-D',"fieldName": 'sPreDeterminationIndicator',"value": '',"disabled": 'No'}]
    },
    {
        "compoundx": true, "label": "Provider",
		headerHelpText:'Provider : Servicing Provider/Facility Name <br> TIN : Tax Identification Number <br> NPI : National Provider Identifier ',
        "compoundvalue": [
            { "text": true, "label": "Name", "fieldName": "sProviderName", "value": "", "Id": false, "disabled": "No" },
            { "text": true, "label": "TIN", "fieldName": "sProviderID", "value": "", "disabled": "No" },
            { "text": true, "label": "NPI", "fieldName": "sSRCNPIID", "value": "", "disabled": "No" }]
    },
    {
        "compoundx": true, "label": "Amount",
		headerHelpText:'Billed Amt : Billed Charge Amount <br> Mbr Resp : Member Responsibility <br> Paid Amt : Paid Amount',
        "compoundvalue": [
            { "text": true, "label": "Billed Amt", "fieldName": "sChargeAmt", "value": "", "disabled": "No" },
            { "text": true, "label": "Mbr Resp", "fieldName": "sMbrRespAmt", "value": "", "disabled": "No" },
            { "text": true, "label": "Paid Amt", "fieldName": "sPaidAmt", "value": "", "disabled": "No" }]
            
    },
    {
        "compoundx": true, "label": "Date",
		headerHelpText:'Last Proc : Last Processed Date <br> Receipt : Receipt Date',
        "compoundvalue": [
            { "text": true, "label": "Last Proc", "fieldName": "sLastProcessDate", "value": "", "disabled": "No" },
            { "text": true, "label": "Receipt", "fieldName": "sClmReceiptDate", "value": "", "disabled": "No" }]
    },
    {
        "compoundx": true, "label": "Product",
        "compoundvalue": [
            { "text": true, "label": "Group#", "fieldName": "sDispGrpID", "value": "", "disabled": "No" },
            { "text": true, "label": "Prod Type", "fieldName": "sLOBCd", "value": "", "disabled": "No" },
            { "text": true, "label": "Platform", "fieldName": "sPlatformCd", "value": "", "disabled": "No" }]    
    }
]];