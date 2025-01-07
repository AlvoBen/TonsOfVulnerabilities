import { hcConstants } from "c/crmUtilityHum";
export const legacyCasehistory = [
  [{
    "compoundx": true, 
    "label": "Reference Details",
    "compoundvalue": [{
      "text": true,
      "label": "Reference ID",
      "value": "",
      "fieldName": "sReferenceID",
      "disabled": "No"
    }, {
      "link": true,
      "icon": false,
      "navToItem": "",
      "label": "Inquiry ID",
      "fieldName": "Inquirydata",
      "value": "",
      "disabled": "No",
      "actionName": "inquirydetails"
    }, {
      "text": true,
      "label": "Priority",
      "fieldName": "sPriority",
      "value": "",
      "disabled": "No"
    }, {
      "text": true,
      "label": "Type",
      "fieldName": "sType",
      "value": "",
      "disabled": "No"
    }, {
      "icon": true,
      "label": "",
      "fieldName": "sStatus",
      "value": "10",
      "disabled": "No"
    }]
  }, {
    "compoundx": true,
    "label": "Inquiry Information",
    "compoundvalue": [{
      "text": true,
      "label": "Created on",
      "value": "",
      "fieldName": "sCreatedOn",
      "disabled": "No"
    }, {
      "text": true,
      "label": "Category",
      "fieldName": "",
      "value": "sCategory",
      "disabled": "No"
    }, {
      "text": true,
      "label": "Reason",
      "fieldName": "sReason",
      "value": "",
      "disabled": "No"
    }, {
      "text": true,
      "label": "Disposition",
      "fieldName": "sDisposition",
      "value": "",
      "disabled": "No"
    }, {
      "text": true,
      "label": "Task",
      "fieldName": "sTasks",
      "value": "10",
      "disabled": "No"
    }]
  }, {
    "compoundx": true,
    "label": "Inquired For",
    "compoundvalue": [{
      "text": true,
      "label": "Type",
      "value": "",
      "fieldName": "sInquiryType",
      "disabled": "No"
    }, {
      "text": true,
      "label": "ID",
      "fieldName": "sInquiredForID",
      "value": "",
      "disabled": "No"
    }, {
      "text": true,
      "label": "Name",
      "fieldName": "sInquiredForName",
      "value": "",
      "disabled": "No"
    }]
  }, {
    "compoundx": true,
    "label": "Inquired About",
    "actionName" : hcConstants.INQUIRED_ABOUT,
    "compoundvalue": [{
      "text": true,
      "label": "Type",
      "value": "",
      "fieldName": "sInquiredAboutType",
      "disabled": "No"
    }, {
      "text": true,
      "label": "ID",
      "fieldName": "sInquiredAboutID",
      "value": "",
      "disabled": "No"
    }, {
      "text": true,
      "label": "Name",
      "fieldName": "sInquiredAboutName",
      "value": "",
      "disabled": "No"
    }, {
      "text": true,
      "label": "Policy",
      "fieldName": "sPolicy",
      "value": "",
      "disabled": "No"
    }]
  }, {
    "compoundx": true,
    "label": "Owner",
    "compoundvalue": [{
      "text": true,
      "label": "Owned by",
      "value": "",
      "fieldName": "sOwnedBy",
      "disabled": "No"
    }, {
      "text": true,
      "label": "Owner Team",
      "fieldName": "sOwnerTeam",
      "value": "",
      "disabled": "No"
    }, {
      "text": true,
      "label": "Owner Department",
      "fieldName": "sOwnerDept",
      "value": "",
      "disabled": "No"
    }, {
      "text": true,
      "label": "Received Date",
      "fieldName": "sReceivedDate",
      "value": "",
      "disabled": "No"
    }]
  }]
];