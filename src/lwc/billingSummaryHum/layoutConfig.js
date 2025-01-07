/*******************************************************************************************************************************
LWC JS Name : layoutConfig.js
Function    : This JS serves as helper to billingSummaryHum.js

Modification Log: 
Developer Name           Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
*Anuradha Gajbhe                                          05/23/2022                   User Story 3266251:MF 2923212 - CRM Service Billing Systems Integration: Member Billing Profile Account-Billing List
*Prashant Moghe                                           05/23/2022                   User Story 3271229:MF-2923225 - CRM Service Billing Systems Integration: Member Billing Profile Account Billing APP -Billing List   
*Anuradha Gajbhe                                          03/06/2023                   US#4302387 - Lightning-Phonebook- Secure Payment UI Controls-EBilling (Surge) Genesys impact
*********************************************************************************************************************************/
import { getLabels } from 'c/crmUtilityHum';
const labels = getLabels();

export function getBillingSummaryStructure(){
    return billingSummaryLayout;
}

export const billingSummaryLayout= [[
    {
        "button": true,"value": "securePay","disabled": "no","label": "","compoundx": false,"fieldName": "",
        "compoundvalue": [
            {
            "button": true,"buttonlabel": "Secure Pay","value": "",
            "event": "utilityPopout", // event as utilityPopout to call LMS instead of pubsub
            "disabled": "no",
            "buttondisabled": false,
            "type_large": true,
            "type_small": false,
            "fieldName": "bSecurePaybuttonFlag",
            "rowData": {}
        }]
    },
    {
        "compoundx": true, "label": "Status",
        "compoundvalue": [
            { "text": true, "label": "", "fieldName": "sStatus", "value": "", "disabled": "No" }]

    },
    {
        "compoundx": true, "label": "Date",
        "compoundvalue": [
            { "text": true, "label": "Effective Date", "fieldName": "sEffectiveDate",  "value": "", "disabled": "No" },
            { "text": true, "label": "End Date", "fieldName": "sEndDate", "value": "", "disabled": "No" }]

    },
    {
        "compoundx": true, "label": "Profile",
        "compoundvalue": [
            { "text": true, "label": "Profile/Acct", "fieldName": "sProfileAccountName", "value": "", "Id": false, "disabled": "No" },
            { "text": true, "label": "Group/PID", "fieldName": "sGroupPIDNumber", "value": "", "disabled": "No" },
            { "text": true, "label": "Profile Number", "fieldName": "iProfileNumber", "value": "", "disabled": "No" }]
    },
    {
        "compoundx": true, "label": "Payment Option",
        "headerHelpText": "Additional Guidance is available for Payment Options. Hover over the Payment Option value to view additional guidance",
        "compoundvalue": [
            { "text": true, "label": "", "fieldName": "sRecurringPaymentOption", "value": "", "disabled": "No"}]
            
    },
    {
        "compoundx": true, "label": "Draft Date / Term",
        "compoundvalue": [
            { "text": true, "label": "Draft Date", "fieldName": "sNextRecurringDate", "value": "", "disabled": "No" },
            { "text": true, "label": "Term Reason", "fieldName": "sTermReasonCode", "value": "", "disabled": "No" }]
    },
    {
        "compoundx": true, "label": "Amount",
        "headerHelpText": "Account Balance: Value displayed from 'Current Account Balance (Non-SSA)' field.",
        "compoundvalue": [
            { "text": true, "label": "Net Premium", "fieldName": "dNetMonthlyPremiumAmount", "value": "", "disabled": "No" },
            { "text": true, "label": "Penalties", "fieldName": "dPenalties", "value": "", "disabled": "No" },
            { "text": true, "label": "Account Balance", "fieldName": "dCurrentAccBalNonSSA", "value": "", "disabled": "No" }]
            
    },
    {
        "compoundx": true, "label": "Product",
        "compoundvalue": [
            { "Id": true, "link": false, "icon": false, "hidden": true, "label": "Id", "fieldName": "BillingDetail", "value": "", "disabled": "No"},
            { "link": true, "label": "Billing Detail",  "fieldName": "BillingDetail", "value": "", "disabled": "No", "linkToChange": "sBillingDetailLink", "actionName": "|MBR_BILLING_DETAIL|","pageName": "Member_Billing_Detail", "navToItem": "sBillingDetailLink"},    
            { "Id": true, "link": false, "icon": false, "hidden": true, "label": "Id", "fieldName": "eBilling", "value": "", "disabled": "No"},
            { "link": true, "label": "eBilling",  "fieldName": "eBilling", "value": "", "disabled": "No", "linkToChange": "seBillingLink", "actionName": "EXTERNAL_URL", "navToItem": "seBillingLink","pageName": "Member_Billing_Detail"}
        ]
    }, 
    {
        "compoundx": true, "label": "PBS",
        "compoundvalue": [
            { "Id": true, "link": false, "icon": false, "hidden": true, "label": "Id", "fieldName": "PBS", "value": "", "disabled": "No"},
            { "link": true, "label": "PBS",  "fieldName": "PBS", "value": "", "disabled": "No", "linkToChange": "sPBSNewLink", "actionName": "EXTERNAL_URL", "navToItem": "sPBSNewLink", "pageName": "Member_Billing_Detail"},
            { "Id": true, "link": false, "icon": false, "hidden": true, "label": "Id", "fieldName": "PBSComments", "value": "", "disabled": "No"},
            { "link": true, "label": "PBS Comments",  "fieldName": "PBSComments", "value": "", "disabled": "No", "linkToChange": "sPBSNewCommentsLinks", "actionName": "EXTERNAL_URL", "navToItem": "sPBSNewCommentsLinks", "pageName": "Member_Billing_Detail"}]    
    }
]];