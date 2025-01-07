export function getHistoryTimelineModel(name)
{
    if(name === "icons"){
        return historytimelineicons;
    }else if(name === "omsmodel"){
        return omstimelineModel;
    }
}
export const omstimelineModel = [{
    "fieldname" : "hasAction",
    "mappingfield" : "isDropDownVisible"
},{
    "fieldname" : "canCreate",
    "mappingfield" : "canCreateTask"
},{
    "fieldname" : "canEdit",
    "mappingfield" : "canEditTask"
},{
    "fieldname" : "canClose",
    "mappingfield" : "canCloseTask"
},{
    "fieldname" : "commentID",
    "mappingfield" : "caseCommentId"
},{
    "fieldname" : "headerline",
    "mappingfield" : "LogNoteUser"
},{
    "fieldname" : "subheaderline",
    "mappingfield" : "LogNoteMessage"
},
{
    "fieldname" : "icon",
    "mappingfield" : " "
},
{
    "fieldname" : "createddatetime",
    "mappingfield" : "LogNoteLastModified"
},{
    "fieldname" : "createdbydatetime",
    "mappingfield" : "LogNoteDate"
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
            "mappingfield" : "LogNoteCode",
            "islink" : false,
            "object" : ""
        },
        {
            "fieldname" : '',
            "mappingfield" : "LogNoteLastModified"
        }],
        "footer" : {
            "fieldname" : "Comments",
            "mappingfield" : "LogNoteComment"
        }
    }]
}]


export const historytimelineicons = [{
    "iconname" : " ",
    "iconvalue" : "standard:task",
    "iconclass" : "slds-icon_container slds-icon-standard-note slds-timeline__icon",
    "iconsize" : "small"
}]