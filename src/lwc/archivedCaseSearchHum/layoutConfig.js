/*******************************************************************************************************************************
LWC JS Name : layoutConfig.js
Function    : This JS serves as helper to archivedCaseHistory.js

Modification Log: 
Developer Name           Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------

*********************************************************************************************************************************/
export function getArchivalSearchLayout() {
    return archivalSearchLayout;
}

export const archivalSearchLayout = [[
    {
        "compoundx": true, "label": "Case Information", "value": "Mem", "compoundvalue": [
            { "hidden": true, "Id": true, "label": "Id", "fieldName": "ssfid", "value": "", "disabled": "No" },
            { "link": true, "linkwithtooltip": true, "label": "Case No", "fieldName": "sCaseNumber", "value": "11", "disabled": "No",  },
            { "text": true, "label": "Type", "value": "10", "fieldName": "sType", "disabled": "No" },
            { "text": true, "label": "Origin", "value": "10", "fieldName": "sOrigin", "disabled": "No" },
            { "text": true, "label": "Priority", "value": "10", "fieldName": "sPriority", "disabled": "No" },
        ]
    },
    {
        "compoundx": true, "label": "Case Dates", "value": "Plan", "compoundvalue": [
            { "text": true, "label": "Date Opened", "value": "10", "fieldName": "sCreatedDate", "disabled": "No", "customFunc": "CASE_CREATION" },
            { "text": true, "label": "Date Closed", "value": "10", "fieldName": "sClosedDate", "disabled": "No" },
            { "text": true, "label": "Follow-Up Date", "value": "10", "fieldName": "sFollow_up_Due_Date", "disabled": "No" },
            { "icon": true, "label": "Status", "fieldName": "sStatus", "value": "10", "disabled": "No" }
        ]
    },
    {
        "compoundx": true, "label": "Classification & Intent", "value": "Pend", "compoundvalue": [
            { "text": true, "label": "Classification", "fieldName": "sClassification", "value": "11", "disabled": "No" },
            { "text": true, "label": "Intent", "value": "10", "fieldName": "sIntent", "disabled": "No" },
            { "text": true, "label": "DCN Present", "value": "10", "fieldName": "sDCN", "disabled": "No" },
            { "text": true, "label": "Complaint", "value": "10", "fieldName": "sComplaint", "disabled": "No" }
          
        ]
    },
    {
        "compoundx": true, "label": "Interaction", "value": "App", "compoundvalue": [
            { "text": true, "label": "With", "fieldName": "sInteracting_With", "value": "11", "disabled": "No" },
            { "text": true, "label": "With Type", "value": "10", "fieldName": "sInteracting_With_Type", "disabled": "No" },
            { "text": true, "label": "About", "value": "10", "fieldName": "sInteracting_About", "disabled": "No" },
        ]
    },
    {
        "compoundx": true, "label": "Case Contacts", "value": "App", "compoundvalue": [
            { "text": true, "label": "Owner Queue", "fieldName": "sOwner_Queue", "value": "11", "disabled": "No" },
            { "text": true, "label": "Created by Queue", "value": "10", "fieldName": "sCreated_By_Queue", "disabled": "No" },
            { "text": true, "label": "Last Modified by Queue", "value": "10", "fieldName": "sLastModifiedby_Queue", "disabled": "No" }
        ]
    }
]];