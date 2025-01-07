export function getClaimDetails(){
    return claimdetails;
}
export const claimdetails = [[
    { "accordian": true, "emptycell": true, "isActionColumn": true, "compoundx": false, "label": "", "value": "", "fieldName": "Id", "disabled": "No" },
    {
        "compoundx": true, "label": "Claim Dates", "value": "dat", "compoundvalue": [
           // { "hidden": true, "Id": true, "label": "Id", "fieldName": "Id", "value": "", "disabled": "No" },
          //  { "link": true, "linkwithtooltip": true, "label": "Case No", "fieldName": "sCaseNum", "value": "11", "disabled": "No" },
            { "text": true, "label": "Last Proc Date", "value": "11", "fieldName": "sLastProcessDate", "disabled": "No" },
          //  { "hidden": true, "label": "Seq #", "fieldName": "sSrcClaimLineSequence", "value": "", "disabled": "No" },
            { "text": true, "label": "Begin DOS", "value": "10", "fieldName": "sServiceStartDate", "disabled": "No" },
            { "text": true, "label": "End DOS", "value": "10", "fieldName": "sServiceEndDate", "disabled": "No" }
        ]
    },
    {
        "compoundx": true, "label": "Procedure", "value": "Proc", "compoundvalue": [
            { "text": true, "label": "EX", "fieldName": "sExLineItem", "value": "11", "disabled": "No" },
            { "text": true, "label": "Proc Cd", "fieldName": "sServiceCode", "value": "10", "disabled": "No" },
            { "text": true, "label": "Proc Desc", "value": "10", "fieldName": "sServiceDesc", "disabled": "No", "customFunc": "CASE_CREATION" },
           // { "text": true, "label": "Date Closed", "value": "10", "fieldName": "sClosedDate", "disabled": "No" },
          //  { "text": true, "label": "Follow Up", "value": "10", "fieldName": "sFollowUpDate", "disabled": "No" },
          //  { "icon": true, "label": "Status", "fieldName": "sStatus", "value": "10", "disabled": "No" }
        ]
    },
    {
        "compoundx": true, "label": "Amounts", "value": "Pend", "compoundvalue": [
            { "text": true, "label": "Billed", "fieldName": "sChargeAmt", "value": "11", "disabled": "No" },
            { "text": true, "label": "Allowed", "value": "10", "fieldName": "sBenAllowAmt", "disabled": "No" },
            { "text": true, "label": "Discount", "value": "10", "fieldName": "sProvWriteOff", "disabled": "No" },
            { "text": true, "label": "Excluded", "value": "10", "fieldName": "sExcludeAmt", "disabled": "No" },
            { "text": true, "label": "Denied", "value": "10", "fieldName": "sBenDenyAmt", "disabled": "No" }
                    
        ]
    },
    {
        "compoundx": true, "label": "Amounts", "value": "otherAmt", "compoundvalue": [
            { "text": true, "label": "Copay", "value": "10", "fieldName": "sCopayAmt", "disabled": "No" },
            { "text": true, "label": "Ded", "value": "10", "fieldName": "sDeductAmt", "disabled": "No" },
            { "text": true, "label": "Coins", "value": "10", "fieldName": "sCoInsAmt", "disabled": "No" }           
        ]
    },
    {
        "compoundx": true, "label": "Remaining Amounts", "value": "mbrAmt", "compoundvalue": [
            { "text": true, "label": "Mbr Resp", "value": "11", "fieldName": "sMbrRespAmt", "disabled": "No" },
            { "text": true, "label": "Paid", "value": "10", "fieldName": "sPaidAmt", "disabled": "No" },
            { "text": true, "label": "OIC Paid", "value": "10", "fieldName": "sCobPaidAmt", "disabled": "No" }
        ]
    },
    {
        "compoundx": true, "label": "Revenue", "value": "revenue", "compoundvalue": [
            { "text": true, "label": "Revenue", "value": "11", "fieldName": "sRevenuecode", "disabled": "No" },
            { "text": true, "label": "Minutes", "value": "10", "fieldName": "sAnesMinutes", "disabled": "No" },
            { "text": true, "label": "Modifier", "value": "10", "fieldName": "sCPTModCode", "disabled": "No" },
            { "text": true, "label": "Cause Code/Pnlty", "value": "10", "fieldName": "sCauseCd", "disabled": "No" }
        ]
    }
]];