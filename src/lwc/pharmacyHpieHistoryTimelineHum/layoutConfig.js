export function getHistoryTimelineModel(name)
{
    if(name === "icons"){
        return historytimelineicons;
    }else if(name === "omsmodel"){
        return omstimelineModel;
	}else if(name === 'dateFilter'){
        return dateFilter;
    }else if(name === "casemodel"){
        return casetimelinemodel;
    }else if(name === 'patientmodel')
    {
        return patienttimelineModel;
    }else if(name === 'familymodel')
    {
        return familytimelineModel;
    }
}
export const casetimelinemodel = [{
    "fieldname" : "headerline",
    "mappingfield" : "sCaseNum,sClassification,sIntent"
},{
    "fieldname" : "subheaderline",
    "mappingfield" : "lCaseComments"
},
{
    "fieldname" : "icon",
    "mappingfield" : "sOrigin"
},
{
    "fieldname" : "createddatetime",
    "mappingfield" : "sCreatedDate"
},
{
    "compoundvalue" : true,
    "fieldname" : "compoundvalues",
    "compoundvalues" : [{
        "header" : {
            "fieldname" : '',
            "mappingfield" : "sClassification,sIntent",
            "gridSize" : "slds-col slds-size_1-of-1"
        },
        "body" : [{
            "fieldname" : "Case",
            "mappingfield" : "sCaseNum",
            "islink" : true,
            "object" : "Case",
            "gridSize" : "slds-col slds-size_1-of-4",
            "labelstyle" : 'display: inline;font-weight:bold;',
            "valuestyle" : 'display:inline;cursor:pointer;margin-left:10px;'
        },{
            "fieldname" : "Status",
            "mappingfield" : "sStatus",
            "gridSize" : "slds-col slds-size_1-of-4",
            "isText" : true,
            "labelstyle" : 'display: inline;font-weight:bold;',
            "valuestyle" : 'display:inline;margin-left:10px;'
        },{
            "fieldname" : "With",
            "mappingfield" : "sInteractingWith",
            "gridSize" : "slds-col slds-size_1-of-4",
            "isText" : true,
            "labelstyle" : 'display: inline;font-weight:bold;',
            "valuestyle" : 'display:inline;margin-left:10px;'
        },{
            "fieldname" : "Owner Queue",
            "mappingfield" : "sOwnerQueue",
            "gridSize" : "slds-col slds-size_1-of-4",       
            "isText" : true,
            "labelstyle" : 'display: inline;font-weight:bold;',
            "valuestyle" : 'display:inline;margin-left:10px;'
        },{
            "fieldname" : "Product",
            "mappingfield" : "sProduct",
            "gridSize" : "slds-col slds-size_1-of-4",
            "isText" : true,
            "labelstyle" : 'display: inline;font-weight:bold;',
            "valuestyle" : 'display:inline;margin-left:10px;'
        },{
            "fieldname" : "Date Opened",
            "mappingfield" : "sOpenedDate",
            "gridSize" : "slds-col slds-size_1-of-4",
            "isText" : true,
            "labelstyle" : 'display: inline;font-weight:bold;',
            "valuestyle" : 'display:inline;margin-left:10px;'
        },{
            "fieldname" : "With Type",
            "mappingfield" : "sInteractingWithType",
            "gridSize" : "slds-col slds-size_1-of-4",
            "isText" : true,
            "labelstyle" : 'display: inline;font-weight:bold;',
            "valuestyle" : 'display:inline;margin-left:10px;'
        },{
            "fieldname" : "Created By Queue",
            "mappingfield" : "sCreatedByQueue",
           	"gridSize" : "slds-col slds-size_1-of-4",
            "isText" : true,
            "labelstyle" : 'display: inline;font-weight:bold;',
            "valuestyle" : 'display:inline;margin-left:10px;'
        },{
            "fieldname" : "Origin",
            "mappingfield" : "sOrigin",
            "gridSize" : "slds-col slds-size_1-of-4",
            "isText" : true,
            "labelstyle" : 'display: inline;font-weight:bold;',
            "valuestyle" : 'display:inline;margin-left:10px;'
        },{
            "fieldname" : "Date Closed",
            "mappingfield" : "sClosedDate",
            "gridSize" : "slds-col slds-size_1-of-4",
            "isText" : true,
            "labelstyle" : 'display: inline;font-weight:bold;',
            "valuestyle" : 'display:inline;margin-left:10px;'
        },{
            "fieldname" : "DCN Present",
            "mappingfield" : "sDCN",
            "gridSize" : "slds-col slds-size_1-of-4",
            "isText" : true,
            "labelstyle" : 'display: inline;font-weight:bold;',
            "valuestyle" : 'display:inline;margin-left:10px;'
        },{
            "fieldname" : "Last Modified By Queue",
            "mappingfield" : "sLastModifiedByQueue",
            "gridSize" : "slds-col slds-size_1-of-4",
            "isText" : true,
            "labelstyle" : 'display: inline;font-weight:bold;',
            "valuestyle" : 'display:inline;margin-left:10px;'
        },{
            "fieldname" : "Priority",
            "mappingfield" : "sPriority",
            "gridSize" : "slds-col slds-size_1-of-4",
            "isText" : true,
            "labelstyle" : 'display: inline;font-weight:bold;',
            "valuestyle" : 'display:inline;margin-left:10px;'
        },{
            "fieldname" : "Type",
            "mappingfield" : "sType",
            "gridSize" : "slds-col slds-size_1-of-4",
            "isText" : true,
            "labelstyle" : 'display: inline;font-weight:bold;',
            "valuestyle" : 'display:inline;margin-left:10px;'
        },{
            "fieldname" : "About",
            "mappingfield" : "sInteractingAbout",
            "gridSize" : "slds-col slds-size_1-of-4",
            "isText" : true,
            "labelstyle" : 'display: inline;font-weight:bold;',
            "valuestyle" : 'display:inline;margin-left:10px;'
        },{
            "fieldname" : "CaseId",
            "mappingfield" : "Id",
            "hidden" : true,
            "object" : ""
        }],
        "footer" : {
            "fieldname" : "Comment",
            "mappingfield" : "lCaseComments",
            "gridSize" : "slds-col slds-size_1-of-1",
            "labelstyle" : 'display: inline;font-weight:bold;',
            "valuestyle" : 'display:inline;margin-left:10px;'
        }
    }]
}]
export const historytimelineicons = [{
    "iconname" : "oms",
    "iconvalue" : "standard:note",
    "iconclass" : "slds-icon_container slds-icon-standard-note slds-timeline__icon",
    "iconsize" : "small"
},{
    "iconname" : "Email",
    "iconvalue" : "standard:email",
    "iconclass" : "slds-icon_container slds-icon-standard-email slds-timeline__icon",
    "iconsize" : "small"
},{
    "iconname" : "Mail",
    "iconvalue" : "custom:custom105",
    "iconclass" : "slds-icon_container  slds-icon-standard-custom105 slds-timeline__icon",
    "iconsize" : "small"
},{
    "iconname" : "Inbound Call",
    "iconvalue" : "utility:incoming_call",
    "iconclass" : "slds-icon_container  customicon slds-timeline__icon",
    "iconsize" : "x-small",
    "customicon" : true,
},{
    "iconname" : "Outbound Call",
    "iconvalue" : "utility:outbound_call",
    "iconclass" : "slds-icon_container  customicon slds-timeline__icon",
    "iconsize" : "x-small",
    "customicon" : true,
},{
    "iconname" : "Walk-In",
    "iconvalue" : "utility:trail",
    "iconclass" : "slds-icon_container customicon slds-timeline__icon",
    "iconsize" : "x-small",
    "customicon" : true,
},{
    "iconname" : "IVR",
    "iconvalue" : "standard:activations",
    "iconclass" : "slds-icon_container  slds-icon-standard-activations slds-timeline__icon",
    "iconsize" : "small"
},{
    "iconname" : "Fax",
    "iconvalue" : "utility:print",
    "iconclass" : "slds-icon_container  customicon slds-timeline__icon",
    "iconsize" : "x-small",
    "customicon" : true,
},{
    "iconname" : "Correspondence",
    "iconvalue" : "utility:comments",
    "iconclass" : "slds-icon_container  customicon slds-timeline__icon",
    "iconsize" : "x-small",
    "customicon" : true,
},{
    "iconname" : "Social Media",
    "iconvalue" : "standard:social",
    "iconclass" : "slds-icon_container  slds-icon-standard-social slds-timeline__icon",
    "iconsize" : "small"
},{
    "iconname" : "Watson Voice",
    "iconvalue" : "custom:custom35",
    "iconclass" : "slds-icon_container  slds-icon-standard-social slds-timeline__icon",
    "iconsize" : "small"
},{
    "iconname" : "Service Inquiry",
    "iconvalue" : "utility:questions_and_answers",
    "iconclass" : "slds-icon_container  customicon slds-timeline__icon",
    "iconsize" : "x-small",
    "customicon" : true,
},{
    "iconname" : "Web Chat",
    "iconvalue" : "standard:live_chat_visitor",
    "iconclass" : "slds-icon_container  slds-icon-standard-live-chat-visitor slds-timeline__icon",
    "iconsize" : "small"
},{
    "iconname" : "Watson Voice",
    "iconvalue" : "utility:voicemail_drop",
    "iconclass" : "slds-icon_container  customicon slds-timeline__icon",
    "iconsize" : "x-small",
    "customicon" : true,
},{
    "iconname" : "NINA Web Chat",
    "iconvalue" : "custom:custom75",
    "iconclass" : "slds-icon_container  slds-icon-standard-rtc-presence slds-timeline__icon",
    "iconsize" : "small"
},{
    "iconname" : "Secure Mail",
    "iconvalue" : "standard:case_email",
    "iconclass" : "slds-icon_container  slds-icon-standard-case-email slds-timeline__icon",
    "iconsize" : "small"
},{
    "iconname" : "Watson Web Chat",
    "iconvalue" : "standard:sms",
    "iconclass" : "slds-icon_container  slds-icon-standard-sms slds-timeline__icon",
    "iconsize" : "small"
},{
    "iconname" : "Internal Process",
    "iconvalue" : "standard:work_queue",
    "iconclass" : "slds-icon_container  slds-icon-standard-work-queue slds-timeline__icon",
    "iconsize" : "small"
}]
export const patienttimelineModel = [{
    "fieldname" : "headerline",
    "mappingfield" : "PatientLogNoteCode"
},{
    "fieldname" : "subheaderline",
    "mappingfield" : "PatientLogNoteMessage"
},
{
    "fieldname" : "icon",
    "mappingfield" : "oms"
},
{
    "fieldname" : "createddatetime",
    "mappingfield" : "PatientLogNoteDate"
},
{
    "compoundvalue" : true,
    "fieldname" : "compoundvalues",
    "compoundvalues" : [{
        "header" : {
            "fieldname" : 'Header',
            "mappingfield" : "PatientLogNoteCode",
            "gridSize" : "slds-col slds-size_1-of-1"
        },
        "body" : [{
            "fieldname" : "User ID",
            "mappingfield" : "PatientLogNoteUser",
            "isText" : true,
            "labelstyle" : 'display:inline;font-weight:bold;',
            "valuestyle" : 'display:inline;margin-left:10px;',
            "gridSize" : "slds-col slds-size_1-of-1"
        }],
        "footer" : {
            "fieldname" : "Comment",
            "mappingfield" : "PatientLogNoteMessage",
            "labelstyle" : 'display: inline;font-weight:bold;',
            "valuestyle" : 'display:inline;margin-left:10px;',
            "gridSize" : "slds-col slds-size_1-of-1"
        }
    }]
}]
export const familytimelineModel = [{
    "fieldname" : "headerline",
    "mappingfield" : "FamilyLogNoteCode"
},{
    "fieldname" : "subheaderline",
    "mappingfield" : "FamilyLogNoteMessage"
},
{
    "fieldname" : "icon",
    "mappingfield" : "oms"
},
{
    "fieldname" : "createddatetime",
    "mappingfield" : "FamilyLogNoteDate"
},
{
    "compoundvalue" : true,
    "fieldname" : "compoundvalues",
    "compoundvalues" : [{
        "header" : {
            "fieldname" : '',
            "mappingfield" : "FamilyLogNoteCode",
            "gridSize" : "slds-col slds-size_1-of-1"
        },
        "body" : [{
            "fieldname" : "User ID",
            "mappingfield" : "FamilyLogNoteUser",
            "isText" : true,
            "labelstyle" : 'display:inline;font-weight:bold;',
            "valuestyle" : 'display:inline;margin-left:10px;',
            "gridSize" : "slds-col slds-size_1-of-1"
        }],
        "footer" : {
            "fieldname" : "Comment",
            "mappingfield" : "FamilyLogNoteMessage",
            "labelstyle" : 'display: inline;font-weight:bold;',
            "valuestyle" : 'display:inline;margin-left:10px;',
            "gridSize" : "slds-col slds-size_1-of-1"
        }
    }]
}]


export const omstimelineModel = [{
    "fieldname" : "headerline",
    "mappingfield" : "LogNoteCode"
},{
    "fieldname" : "subheaderline",
    "mappingfield" : "LogNoteMessage"
},
{
    "fieldname" : "icon",
    "mappingfield" : "oms"
},
{
    "fieldname" : "createddatetime",
    "mappingfield" : "LogNoteDate"
},
{
    "compoundvalue" : true,
    "fieldname" : "compoundvalues",
    "compoundvalues" : [{
        "header" : {
            "fieldname" : '',
            "mappingfield" : "LogNoteCode",
            "gridSize" : "slds-col slds-size_1-of-1"
        },
        "body" : [{
            "fieldname" : "User ID",
            "mappingfield" : "LogNoteUser",
            "isText" : true,
            "labelstyle" : 'display:inline;font-weight:bold;',
            "valuestyle" : 'display:inline;margin-left:10px;',
            "gridSize" : "slds-col slds-size_1-of-1"
        }],
        "footer" : {
            "fieldname" : "Comment",
            "mappingfield" : "LogNoteMessage",
            "labelstyle" : 'display: inline;font-weight:bold;',
            "valuestyle" : 'display:inline;margin-left:10px;',
            "gridSize" : "slds-col slds-size_1-of-1"
        }
    }]
}]

export const dateFilter = [ 
    { label: "Last 14 days", value: "14" },
{ label: "Last 30 days", value: "30" },
{ label: "Last 60 days", value: "60" },
{ label: "Last 90 days", value: "90" },
{ label: "All", value: "All" }]