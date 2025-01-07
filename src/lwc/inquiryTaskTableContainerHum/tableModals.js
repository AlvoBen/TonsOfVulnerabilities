/*******************************************************************************************************************************
Developer Name                    Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Mohan kumar N                 04/22/2021                  Intial version
* Mohan Kumar N                 09/01/2021                US: 2160764 - Configure table column width
*********************************************************************************************************************************/

export const inquiryNotes = [
  [{
    "text": true,
    "label": "Types",
    "compoundx": false,
    "value": "",
    "fieldName": "sType"
  },
  {
    "text": true,
    "label": "Notes",
    "compoundx": false,
    "value": "",
    "fieldName": "sNotes",
    "width": "85%"
  }
]];

export const inquiryAuditTrail = [
  [{
    "text": true,
    "label": "Action Type",
    "compoundx": false,
    "value": "",
    "fieldName": "sAttachTypeDesc"
  },{
    "text": true,
    "label": "Modified On",
    "compoundx": false,
    "value": "",
    "fieldName": "sModifiedOn"
  },{
    "text": true,
    "label": "Modified by",
    "compoundx": false,
    "value": "",
    "fieldName": "sModifiedBy"
  },{
    "text": true,
    "label": "Previous value",
    "compoundx": false,
    "value": "",
    "fieldName": "sPREVIOUSDESC"
  },{
    "text": true,
    "label": "Current Value",
    "compoundx": false,
    "value": "",
    "fieldName": "sCURRENTDESC"
  }
]]

export const inquiryAttachments = [
[{
  "accordian": true,
  "emptycell": true,
  "customFunc": false,
  "isActionColumn": true,
  "compoundx": false,
  "fieldName": "Id",
  "disabled": true
},{
  "text": true,
  "label": "Type",
  "compoundx": false,
  "value": "",
  "fieldName": "sAttachTypeDesc"
},{
  "label": "Id/File Name",
  "compoundx": true,
  "width": "75%",
  "compoundvalue": [{
    "link": true,
    "hidden":true, 
    "icon": false,
    "navToItem": "",
    "fieldName": "sDocId",
    "value": "",
    "disabled": "No",
    "actionName": "inquiryattachments"     
  }]
}
]];

export const inquiryTaskList = [
[{
"compoundx": true,  
"label": "Task ID",
"compoundvalue": [{
"link": true,
"hidden":true, 
"icon": false,
"navToItem": "",
"fieldName": "taskId",
"value": "",
"disabled": "No",
"actionName": "taskdetails"     
}]
},{
  "text": true,
  "label": "Created On",
  "compoundx": false,
  "value": "",
  "fieldName": "sCreatedOn"
},{
  "text": true,
  "label": "Age",
  "compoundx": false,
  "value": "",
  "fieldName": "sAge"
},{
  "text": true,
  "label": "Action",
  "compoundx": false,
  "value": "",
  "fieldName": "sActionDesc"
},{
  "text": true,
  "label": "Result",
  "compoundx": false,
  "value": "",
  "fieldName": "sResultDesc"
},{
  "text": true,
  "label": "Short Desc",
  "compoundx": false,
  "value": "",
  "fieldName": "sShortDesc"
},{
  "text": true,
  "label": "Status",
  "compoundx": false,
  "value": "",
  "fieldName": "sStatus"
},{
  "text": true,
  "label": "Date Due",
  "compoundx": false,
  "value": "",
  "fieldName": "sDueDate"
},{
  "text": true,
  "label": "Date Closed",
  "compoundx": false,
  "value": "",
  "fieldName": "sDateClosed"
}
]];

export const taskNotes = [
[{
  "text": true,
  "label": "Types",
  "compoundx": false,
  "value": "",
  "fieldName": "Type"
},
{
  "text": true,
  "label": "Notes",
  "compoundx": false,
  "width": "85%",
  "value": "",
  "fieldName": "NOTE_DESC"
}
]];

export const taskAuditTrail = [
[{
  "text": true,
  "label": "Action Type",
  "compoundx": false,
  "value": "",
  "fieldName": "ACTION_TYPE_DESC"
},{
  "text": true,
  "label": "Modified On",
  "compoundx": false,
  "value": "",
  "fieldName": "Modified_On"
},{
  "text": true,
  "label": "Modified by",
  "compoundx": false,
  "value": "",
  "fieldName": "CREATED_FIRST_NAME"
},{
  "text": true,
  "label": "Previous value",
  "compoundx": false,
  "value": "",
  "fieldName": "PREVIOUS_DESC"
},{
  "text": true,
  "label": "Current Value",
  "compoundx": false,
  "value": "",
  "fieldName": "CURRENT_DESC"
}
]];

export const taskAttachments = [
[{
  "accordian": true,
  "customFunc": false,
  "emptycell": true,
  "isActionColumn": true,
  "compoundx": false,
  "label": "",
  "value": "", 
  "fieldName": "Id",
  "disabled": true
},{
  "text": true,
  "label": "Type",
  "compoundx": false,
  "value": "",
  "fieldName": "ATTACH_TYPE_DESC"
},{
  "label": "Id/File Name",
  "compoundx": true,
  "width": "75%",
  "compoundvalue": [{
    "link": true,
    "hidden":true, 
    "icon": false,
    "navToItem": "",
    "fieldName": "Doc_Id",
    "value": "",
    "disabled": "No",
    "actionName": "taskdetailattachments"     
  }]
}
]];