/*******************************************************************************************************************************
LWC JS Name : layoutConfig.js
Function    : This JS serves as helper to accountDetailCaseHistoryHum.js

Modification Log: 
Developer Name           Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
* Pradeep Dani                                          12/18/2020                    initial version
* Supriya Shastri                                       02/26/2020                    US-1915755
* Gowthami Thota                                        09/23/2021                    US-1749572 MF1-Case History - GBO Page Updates
*********************************************************************************************************************************/
export function getCaseHistoryLayout(oUserGroup) {
    if (oUserGroup) {
        if(oUserGroup.bGbo){
            return casehistoryGbo;
        }
        if (oUserGroup.bPharmacy) {
            return casehistoryPharmacy;
        }
        else {
            return casehistory;
        }
    }
}

export const casehistoryGbo = [[
    { "isActionColumn": true, "isCheckbox": true, "compoundx": false, "label": "Link", "value": "", "isLink":false, "fieldName": "Id", "disabled": "No" },
    {
        "compoundx": true, "label": "Case Information", "value": "Mem", "compoundvalue": [
            { "hidden": true, "Id": true, "label": "Id", "fieldName": "Id", "value": "", "disabled": "No" },
            { "link": true, "linkwithtooltip": true, "label": "Case No", "fieldName": "sCaseNum", "value": "11", "disabled": "No" },
            { "text": true, "label": "Product", "value": "10", "fieldName": "sProduct", "disabled": "No" },
            { "text": true, "label": "Origin", "value": "10", "fieldName": "sOrigin", "disabled": "No" },
            { "text": true, "label": "Created by", "value": "10", "fieldName": "sCreatedBy", "disabled": "No" },
            { "text": true, "label": "Created by Queue", "value": "10", "fieldName": "sCreatedByQueue", "disabled": "No" },            
        ]
    },
    {
        "compoundx": true, "label": "Case Dates", "value": "Plan", "compoundvalue": [
            { "text": true, "label": "Date Opened", "value": "10", "fieldName": "sCreatedDate", "disabled": "No", "customFunc": "CASE_CREATION" },
            { "text": true, "label": "Date Closed", "value": "10", "fieldName": "sClosedDate", "disabled": "No" },
            { "text": true, "label": "Follow-Up Date", "value": "10", "fieldName": "sFollowUpDate", "disabled": "No" },
            { "icon": true, "label": "Status", "fieldName": "sStatus", "value": "10", "disabled": "No" }
        ]
    },
    {
        "compoundx": true, "label": "Classification & Intent", "value": "Pend", "compoundvalue": [
            { "text": true, "label": "Classification", "fieldName": "sClassification", "value": "11", "disabled": "No" },
            { "text": true, "label": "Intent", "value": "10", "fieldName": "sIntent", "disabled": "No" }
          
        ]
    },
    {
        "compoundx": true, "label": "Interaction", "value": "App", "compoundvalue": [
            { "text": true, "label": "Name", "fieldName": "sInteractingWith", "value": "11", "disabled": "No" },
            { "text": true, "label": "With", "fieldName": "sInteractingWith", "value": "11", "disabled": "No" },
            { "text": true, "label": "With Type", "value": "10", "fieldName": "sInteractingWithType", "disabled": "No" },
            { "text": true, "label": "About", "value": "10", "fieldName": "sInteractingAbout", "disabled": "No" },
            { "text": true, "label": "About Type", "value": "10", "fieldName": "sInteractingAboutType", "disabled": "No" }
        ]
    },
    {
        "compoundx": true, "label": "Case Contacts", "value": "App", "compoundvalue": [
            { "text": true, "label": "Owner Queue", "fieldName": "sOwnerQueue", "value": "11", "disabled": "No" },
            { "text": true, "label": "Last Modified by", "value": "10", "fieldName": "sLastModifiedBy", "disabled": "No" },
            { "text": true, "label": "Last Modified by Queue", "value": "10", "fieldName": "sLastModifiedByQueue", "disabled": "No" }
        ]
    }
]];


export const casehistory = [[
    { "isActionColumn": true, "isCheckbox": true, "compoundx": false, "label": "Link", "value": "", "isLink":false,"fieldName": "Id", "disabled": "No" },
    {
        "compoundx": true, "label": "Case Information", "value": "Mem", "compoundvalue": [
            { "hidden": true, "Id": true, "label": "Id", "fieldName": "Id", "value": "", "disabled": "No" },
            { "link": true, "linkwithtooltip": true, "label": "Case No", "fieldName": "sCaseNum", "value": "11", "disabled": "No" },
            { "text": true, "label": "Type", "value": "10", "fieldName": "sType", "disabled": "No" },
            { "text": true, "label": "Origin", "value": "10", "fieldName": "sOrigin", "disabled": "No" },
            { "text": true, "label": "Priority", "value": "10", "fieldName": "sPriority", "disabled": "No" }
        ]
    },
    {
        "compoundx": true, "label": "Case Dates", "value": "Plan", "compoundvalue": [
            { "text": true, "label": "Date Opened", "value": "10", "fieldName": "sCreatedDate", "disabled": "No", "customFunc": "CASE_CREATION" },
            { "text": true, "label": "Date Closed", "value": "10", "fieldName": "sClosedDate", "disabled": "No" },
            { "text": true, "label": "Follow-Up Date", "value": "10", "fieldName": "sFollowUpDate", "disabled": "No" },
            { "icon": true, "label": "Status", "fieldName": "sStatus", "value": "10", "disabled": "No" }
        ]
    },
    {
        "compoundx": true, "label": "Classification & Intent", "value": "Pend", "compoundvalue": [
            { "text": true, "label": "Classification", "fieldName": "sClassification", "value": "11", "disabled": "No" },
            { "text": true, "label": "Intent", "value": "10", "fieldName": "sIntent", "disabled": "No" },
            { "link": true, "label": "DCN Present", "value": "11", "fieldName": "sDCN", "disabled": "No","linkToChange":"dcnLink","navToItem": "","actionName": "EXTERNAL_REF"},
            { "text": true, "label": "Complaint", "value": "10", "fieldName": "sComplaint", "disabled": "No" }
        ]
    },
    {
        "compoundx": true, "label": "Interaction", "value": "App", "compoundvalue": [
            { "text": true, "label": "With", "fieldName": "sInteractingWith", "value": "11", "disabled": "No" },
            { "text": true, "label": "With Type", "value": "10", "fieldName": "sInteractingWithType", "disabled": "No" },
            { "text": true, "label": "About", "value": "10", "fieldName": "sInteractingAbout", "disabled": "No" }
        ]
    },
    {
        "compoundx": true, "label": "Case Contacts", "value": "App", "compoundvalue": [
            { "text": true, "label": "Owner Queue", "fieldName": "sOwnerQueue", "value": "11", "disabled": "No" },
            { "text": true, "label": "Created by Queue", "value": "10", "fieldName": "sCreatedByQueue", "disabled": "No" },
            { "text": true, "label": "Last Modified by Queue", "value": "10", "fieldName": "sLastModifiedByQueue", "disabled": "No" }
        ]
    }
]];

export const casehistoryPharmacy = [[
    { "isActionColumn": true, "isCheckbox": true, "compoundx": false, "label": "Link", "value": "", "isLink":false, "fieldName": "Id", "disabled": "No" },
    {
        "compoundx": true, "label": "Case Information", "value": "Mem", "compoundvalue": [
            { "hidden": true, "Id": true, "label": "Id", "fieldName": "Id", "value": "", "disabled": "No" },
            { "link": true, "linkwithtooltip": true, "label": "Case No", "fieldName": "sCaseNum", "value": "11", "disabled": "No" },
            { "text": true, "label": "Product", "value": "10", "fieldName": "sProduct", "disabled": "No" },
            { "text": true, "label": "Origin", "value": "10", "fieldName": "sOrigin", "disabled": "No" },
            { "text": true, "label": "Priority", "value": "10", "fieldName": "sPriority", "disabled": "No" }
        ]
    },
    {
        "compoundx": true, "label": "Case Dates & Type", "value": "Plan", "compoundvalue": [
            { "text": true, "label": "Date Opened", "value": "10", "fieldName": "sCreatedDate", "disabled": "No", "customFunc": "CASE_CREATION" },
            { "text": true, "label": "Date Closed", "value": "10", "fieldName": "sClosedDate", "disabled": "No" },
            { "text": true, "label": "Type", "value": "10", "fieldName": "sType", "disabled": "No" },
            { "icon": true, "label": "Status", "fieldName": "sStatus", "value": "10", "disabled": "No" }
        ]
    },
    {
        "compoundx": true, "label": "Classification & Intent", "value": "Pend", "compoundvalue": [
            { "text": true, "label": "Classification", "fieldName": "sClassification", "value": "11", "disabled": "No" },
            { "text": true, "label": "Intent", "value": "10", "fieldName": "sIntent", "disabled": "No" },
            { "link": true, "label": "DCN Present", "value": "11", "fieldName": "sDCN", "disabled": "No","linkToChange":"dcnLink","navToItem": "","actionName": "EXTERNAL_REF"}
        ]
    },
    {
        "compoundx": true, "label": "Interaction", "value": "App", "compoundvalue": [
            { "text": true, "label": "With", "fieldName": "sInteractingWith", "value": "11", "disabled": "No" },
            { "text": true, "label": "With Type", "value": "10", "fieldName": "sInteractingWithType", "disabled": "No" },
            { "text": true, "label": "About", "value": "10", "fieldName": "sInteractingAbout", "disabled": "No" }
        ]
    },
    {
        "compoundx": true, "label": "Case Contacts", "value": "App", "compoundvalue": [
            { "text": true, "label": "Owner Queue", "fieldName": "sOwnerQueue", "value": "11", "disabled": "No" },
            { "text": true, "label": "Created by Queue", "value": "10", "fieldName": "sCreatedByQueue", "disabled": "No" },
            { "text": true, "label": "Last Modified by Queue", "value": "10", "fieldName": "sLastModifiedByQueue", "disabled": "No" }
        ]
    }
]];