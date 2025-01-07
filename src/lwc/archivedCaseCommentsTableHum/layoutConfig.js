export function getHistoryTimelineModel(name)
{
    if(name === "icons"){
        return historytimelineicons;
    }else if(name === "omsmodel"){
        return omstimelineModel;
    }else if(name === "casemodel"){
        return casetimelinemodel;
    }
}
export const omstimelineModel = [{
    "fieldname" : "hasAction",
    "mappingfield" : "sisDropDownVisible"
},{
    "fieldname" : "canDelete",
    "mappingfield" : "scanDelete"
},{
    "fieldname" : "canEdit",
    "mappingfield" : "scanEdit"
},{
    "fieldname" : "commentID",
    "mappingfield" : "scaseCommentId"
},{
    "fieldname" : "headerline",
    "mappingfield" : "sCreatedByName"
},{
    "fieldname" : "subheaderline",
    "mappingfield" : "sCommentBody"
},
{
    "fieldname" : "icon",
    "mappingfield" : " "
},
{
    "fieldname" : "lastmodifieddatetime",
    "mappingfield" : "sLastModifiedDate"
},{
    "fieldname" : "createdbydatetime",
    "mappingfield" : "sCreatedDate"
},
{
    "compoundvalue" : true,
    "fieldname" : "compoundvalues",
    "compoundvalues" : [{
        "header" : {
            "fieldname" : '',
            "mappingfield" : ""
        },
        "body" : [{
            "fieldname" : "Last Modified By",
            "mappingfield" : "sLastModifiedByName",
            "islink" : false,
            "object" : ""
        },
        {
            "fieldname" : '',
            "mappingfield" : "sLastModifiedDate"
        }],
        "footer" : {
            "fieldname" : "Comments",
            "mappingfield" : "sCommentBody"
        }
    }]
}]

export const historytimelineicons = [{
    "iconname" : " ",
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
    "iconclass" : "slds-icon_container  slds-icon-utility-incoming-call slds-timeline__icon",
    "iconsize" : "small"
},{
    "iconname" : "Outbound Call",
    "iconvalue" : "utility:outbound_call",
    "iconclass" : "slds-icon_container  slds-icon-utility-outbound-call slds-timeline__icon",
    "iconsize" : "small"
},{
    "iconname" : "Walk-In",
    "iconvalue" : "utility:trail",
    "iconclass" : "slds-icon_container  slds-icon-utility-trail slds-timeline__icon",
    "iconsize" : "small"
},{
    "iconname" : "IVR",
    "iconvalue" : "standard:activations",
    "iconclass" : "slds-icon_container  slds-icon-standard-activations slds-timeline__icon",
    "iconsize" : "small"
},{
    "iconname" : "Fax",
    "iconvalue" : "utility:print",
    "iconclass" : "slds-icon_container  slds-icon-utility-print slds-timeline__icon",
    "iconsize" : "small"
},{
    "iconname" : "Correspondence",
    "iconvalue" : "utility:comments",
    "iconclass" : "slds-icon_container  slds-icon-utility-comments slds-timeline__icon",
    "iconsize" : "small"
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
    "iconclass" : "slds-icon_container  slds-icon-utility-questions-and-answers slds-timeline__icon",
    "iconsize" : "small"
},{
    "iconname" : "Web Chat",
    "iconvalue" : "standard:live_chat_visitor",
    "iconclass" : "slds-icon_container  slds-icon-standard-live-chat-visitor slds-timeline__icon",
    "iconsize" : "small"
},{
    "iconname" : "Watson Voice",
    "iconvalue" : "utility:voicemail_drop",
    "iconclass" : "slds-icon_container  slds-icon-utility-voicemail-drop slds-timeline__icon",
    "iconsize" : "small"
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