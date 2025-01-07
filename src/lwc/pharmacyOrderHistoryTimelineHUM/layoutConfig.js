export function getCaseHistoryLayout(oUserGroup) {
    if (oUserGroup) {
        if (oUserGroup.bPharmacy) {
            return casehistoryPharmacy;
        }
        else {
            return casehistory;
        }
    }
}

export function getHistoryTimelineModel(name) {
    if (name === "icons") {
        return historytimelineicons;
    } else if (name === "omsmodel") {
        return omstimelineModel;
    } else if (name === "casemodel") {
        return casetimelinemodel;
    }
}


export const casetimelinemodel = [{
    "fieldname": "headerline",
    "mappingfield": "sCaseNum,sClassification,sIntent"
}, {
    "fieldname": "subheaderline",
    "mappingfield": "lCaseComments"
},
{
    "fieldname": "icon",
    "mappingfield": "sOrigin"
},
{
    "fieldname": "createddatetime",
    "mappingfield": "sCreatedDate"
},
{
    "compoundvalue": true,
    "fieldname": "compoundvalues",
    "compoundvalues": [{
        "header": {
            "fieldname": '',
            "mappingfield": "sClassification,sIntent",
            "gridSize": "slds-col slds-size_1-of-1"
        },
        "body": [{
            "fieldname": "Case",
            "mappingfield": "sCaseNum",
            "islink": true,
            "object": "Case",
            "gridSize": "slds-col slds-size_1-of-4",
            "labelstyle": 'display: inline;font-weight:bold;',
            "valuestyle": 'display:inline;cursor:pointer;margin-left:10px;'
        }, {
            "fieldname": "Status",
            "mappingfield": "sStatus",
            "gridSize": "slds-col slds-size_1-of-4",
            "isText": true,
            "labelstyle": 'display: inline;font-weight:bold;',
            "valuestyle": 'display:inline;margin-left:10px;'
        }, {
            "fieldname": "With",
            "mappingfield": "sInteractingWith",
            "gridSize": "slds-col slds-size_1-of-4",
            "isText": true,
            "labelstyle": 'display: inline;font-weight:bold;',
            "valuestyle": 'display:inline;margin-left:10px;'
        }, {
            "fieldname": "Owner Queue",
            "mappingfield": "sOwnerQueue",
            "gridSize": "slds-col slds-size_1-of-4",
            "isText": true,
            "labelstyle": 'display: inline;font-weight:bold;',
            "valuestyle": 'display:inline;margin-left:10px;'
        }, {
            "fieldname": "Product",
            "mappingfield": "sProduct",
            "gridSize": "slds-col slds-size_1-of-4",
            "isText": true,
            "labelstyle": 'display: inline;font-weight:bold;',
            "valuestyle": 'display:inline;margin-left:10px;'
        }, {
            "fieldname": "Date Opened",
            "mappingfield": "sOpenedDate",
            "gridSize": "slds-col slds-size_1-of-4",
            "isText": true,
            "labelstyle": 'display: inline;font-weight:bold;',
            "valuestyle": 'display:inline;margin-left:10px;'
        }, {
            "fieldname": "With Type",
            "mappingfield": "sInteractingWithType",
            "gridSize": "slds-col slds-size_1-of-4",
            "isText": true,
            "labelstyle": 'display: inline;font-weight:bold;',
            "valuestyle": 'display:inline;margin-left:10px;'
        }, {
            "fieldname": "Created By Queue",
            "mappingfield": "sCreatedByQueue",
            "gridSize": "slds-col slds-size_1-of-4",
            "isText": true,
            "labelstyle": 'display: inline;font-weight:bold;',
            "valuestyle": 'display:inline;margin-left:10px;'
        }, {
            "fieldname": "Origin",
            "mappingfield": "sOrigin",
            "gridSize": "slds-col slds-size_1-of-4",
            "isText": true,
            "labelstyle": 'display: inline;font-weight:bold;',
            "valuestyle": 'display:inline;margin-left:10px;'
        }, {
            "fieldname": "Date Closed",
            "mappingfield": "sClosedDate",
            "gridSize": "slds-col slds-size_1-of-4",
            "isText": true,
            "labelstyle": 'display: inline;font-weight:bold;',
            "valuestyle": 'display:inline;margin-left:10px;'
        }, {
            "fieldname": "DCN Present",
            "mappingfield": "sDCN",
            "gridSize": "slds-col slds-size_1-of-4",
            "isText": true,
            "labelstyle": 'display: inline;font-weight:bold;',
            "valuestyle": 'display:inline;margin-left:10px;'
        }, {
            "fieldname": "Last Modified By Queue",
            "mappingfield": "sLastModifiedByQueue",
            "gridSize": "slds-col slds-size_1-of-4",
            "isText": true,
            "labelstyle": 'display: inline;font-weight:bold;',
            "valuestyle": 'display:inline;margin-left:10px;'
        }, {
            "fieldname": "Priority",
            "mappingfield": "sPriority",
            "gridSize": "slds-col slds-size_1-of-4",
            "isText": true,
            "labelstyle": 'display: inline;font-weight:bold;',
            "valuestyle": 'display:inline;margin-left:10px;'
        }, {
            "fieldname": "Type",
            "mappingfield": "sType",
            "gridSize": "slds-col slds-size_1-of-4",
            "isText": true,
            "labelstyle": 'display: inline;font-weight:bold;',
            "valuestyle": 'display:inline;margin-left:10px;'
        }, {
            "fieldname": "About",
            "mappingfield": "sInteractingAbout",
            "gridSize": "slds-col slds-size_1-of-4",
            "isText": true,
            "labelstyle": 'display: inline;font-weight:bold;',
            "valuestyle": 'display:inline;margin-left:10px;'
        }, {
            "fieldname": "CaseId",
            "mappingfield": "Id",
            "hidden": true,
            "object": ""
        }],
        "footer": {
            "fieldname": "Comment",
            "mappingfield": "lCaseComments",
            "gridSize": "slds-col slds-size_1-of-1",
            "labelstyle": 'display: inline;font-weight:bold;',
            "valuestyle": 'display:inline;margin-left:10px;'
        }
    }]
}]

export const historytimelineicons = [{
    "iconname": "oms",
    "iconvalue": "standard:note",
    "iconclass": "slds-icon_container slds-icon-standard-note slds-timeline__icon",
    "iconsize": "small"
}, {
    "iconname": "Email",
    "iconvalue": "standard:email",
    "iconclass": "slds-icon_container slds-icon-standard-email slds-timeline__icon",
    "iconsize": "small"
}, {
    "iconname": "Mail",
    "iconvalue": "custom:custom105",
    "iconclass": "slds-icon_container  slds-icon-standard-custom105 slds-timeline__icon",
    "iconsize": "small"
}, {
    "iconname": "Inbound Call",
    "iconvalue": "utility:incoming_call",
    "iconclass": "slds-icon_container  customicon slds-timeline__icon",
    "iconsize": "x-small",
    "customicon": true,
}, {
    "iconname": "Outbound Call",
    "iconvalue": "utility:outbound_call",
    "iconclass": "slds-icon_container  customicon slds-timeline__icon",
    "iconsize": "x-small",
    "customicon": true,
}, {
    "iconname": "Walk-In",
    "iconvalue": "utility:trail",
    "iconclass": "slds-icon_container customicon slds-timeline__icon",
    "iconsize": "x-small",
    "customicon": true,
}, {
    "iconname": "IVR",
    "iconvalue": "standard:activations",
    "iconclass": "slds-icon_container  slds-icon-standard-activations slds-timeline__icon",
    "iconsize": "small"
}, {
    "iconname": "Fax",
    "iconvalue": "utility:print",
    "iconclass": "slds-icon_container  customicon slds-timeline__icon",
    "iconsize": "x-small",
    "customicon": true,
}, {
    "iconname": "Correspondence",
    "iconvalue": "utility:comments",
    "iconclass": "slds-icon_container  customicon slds-timeline__icon",
    "iconsize": "x-small",
    "customicon": true,
}, {
    "iconname": "Social Media",
    "iconvalue": "standard:social",
    "iconclass": "slds-icon_container  slds-icon-standard-social slds-timeline__icon",
    "iconsize": "small"
}, {
    "iconname": "Watson Voice",
    "iconvalue": "custom:custom35",
    "iconclass": "slds-icon_container  slds-icon-standard-social slds-timeline__icon",
    "iconsize": "small"
}, {
    "iconname": "Service Inquiry",
    "iconvalue": "utility:questions_and_answers",
    "iconclass": "slds-icon_container  customicon slds-timeline__icon",
    "iconsize": "x-small",
    "customicon": true,
}, {
    "iconname": "Web Chat",
    "iconvalue": "standard:live_chat_visitor",
    "iconclass": "slds-icon_container  slds-icon-standard-live-chat-visitor slds-timeline__icon",
    "iconsize": "small"
}, {
    "iconname": "Watson Voice",
    "iconvalue": "utility:voicemail_drop",
    "iconclass": "slds-icon_container  customicon slds-timeline__icon",
    "iconsize": "x-small",
    "customicon": true,
}, {
    "iconname": "NINA Web Chat",
    "iconvalue": "standard:rtc_presence",
    "iconclass": "slds-icon_container  slds-icon-standard-rtc-presence slds-timeline__icon",
    "iconsize": "small"
}, {
    "iconname": "Secure Mail",
    "iconvalue": "standard:case_email",
    "iconclass": "slds-icon_container  slds-icon-standard-case-email slds-timeline__icon",
    "iconsize": "small"
}, {
    "iconname": "Watson Web Chat",
    "iconvalue": "standard:sms",
    "iconclass": "slds-icon_container  slds-icon-standard-sms slds-timeline__icon",
    "iconsize": "small"
}, {
    "iconname": "Internal Process",
    "iconvalue": "standard:work_queue",
    "iconclass": "slds-icon_container  slds-icon-standard-work-queue slds-timeline__icon",
    "iconsize": "small"
}]

export const omstimelineModel = [{
    "fieldname": "headerline",
    "mappingfield": "LogNoteCode"
}, {
    "fieldname": "subheaderline",
    "mappingfield": "LogNoteMessage"
},
{
    "fieldname": "icon",
    "mappingfield": "oms"
},
{
    "fieldname": "createddatetime",
    "mappingfield": "LogNoteDate"
},
{
    "compoundvalue": true,
    "fieldname": "compoundvalues",
    "compoundvalues": [{
        "header": {
            "fieldname": '',
            "mappingfield": "LogNoteCode"
        },
        "body": [{
            "fieldname": "User ID",
            "mappingfield": "LogNoteUser",
            "islink": false,
            "object": ""
        }],
        "footer": {
            "fieldname": "Comments",
            "mappingfield": "LogNoteMessage"
        }
    }]
}]


export const casehistory = [[
    { "isActionColumn": true, "isCheckbox": true, "compoundx": false, "label": "Link", "value": "", "isLink": false, "fieldName": "Id", "disabled": "No" },
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
            { "text": true, "label": "Status", "fieldName": "sStatus", "value": "11", "disabled": "No" },
            { "text": true, "label": "Date Opened", "value": "10", "fieldName": "sCreatedDate", "disabled": "No", "customFunc": "CASE_CREATION" },
            { "text": true, "label": "Date Closed", "value": "10", "fieldName": "sClosedDate", "disabled": "No" },
            { "text": true, "label": "Follow Up", "value": "10", "fieldName": "sFollowUpDate", "disabled": "No" },
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
    { "isActionColumn": true, "isCheckbox": true, "compoundx": false, "label": "Link", "value": "", "isLink": false, "fieldName": "Id", "disabled": "No" },
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
            { "text": true, "label": "Status", "fieldName": "sStatus", "value": "11", "disabled": "No" },
            { "text": true, "label": "Date Opened", "value": "10", "fieldName": "sCreatedDate", "disabled": "No", "customFunc": "CASE_CREATION" },
            { "text": true, "label": "Date Closed", "value": "10", "fieldName": "sClosedDate", "disabled": "No" },
            { "text": true, "label": "Type", "value": "10", "fieldName": "sType", "disabled": "No" }
        ]
    },
    {
        "compoundx": true, "label": "Classification & Intent", "value": "Pend", "compoundvalue": [
            { "text": true, "label": "Classification", "fieldName": "sClassification", "value": "11", "disabled": "No" },
            { "text": true, "label": "Intent", "value": "10", "fieldName": "sIntent", "disabled": "No" },
            { "text": true, "label": "DCN Present", "value": "10", "fieldName": "sDCN", "disabled": "No" }
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