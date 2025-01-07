﻿export const grievanceAndAppealsDetailLayout = [

    {
        "fieldname": "Due Date",
        "mappingfield": "sDueDate",
        "gridSize": "slds-col slds-size_1-of-3",
        "labelClass": 'slds-p-top_small slds-cell-wrap',
        "valueClass": 'slds-p-vertical_x-small slds-text-body_small slds-text-color_weak slds-cell-wrap'

    },
    {
        "fieldname": "Date of Determination",
        "mappingfield": "sDateofDetermination",
        "gridSize": "slds-col slds-size_1-of-3",
        "labelClass": 'slds-p-top_small slds-cell-wrap',
        "valueClass": 'slds-p-vertical_x-small slds-text-body_small slds-text-color_weak slds-cell-wrap'

    },
    {
        "fieldname": "Date of Service",
        "mappingfield": "sDatesOfService",
        "gridSize": "slds-col slds-size_1-of-3",
        "labelClass": 'slds-p-top_small slds-cell-wrap',
        "valueClass": 'slds-p-vertical_x-small slds-text-body_small slds-text-color_weak slds-cell-wrap'

    },
    {
        "fieldname": "Service Type",
        "mappingfield": "sServiceType",
        "gridSize": "slds-col slds-size_1-of-3",
        "labelClass": 'slds-p-top_small slds-cell-wrap',
        "valueClass": 'slds-p-vertical_x-small slds-text-body_small slds-text-color_weak slds-cell-wrap'

    },
    {
        "fieldname": "Request Type",
        "mappingfield": "sRequestType",
        "gridSize": "slds-col slds-size_1-of-3",
        "labelClass": 'slds-p-top_small slds-cell-wrap',
        "valueClass": 'slds-p-vertical_x-small slds-text-body_small slds-text-color_weak slds-cell-wrap'

    },
    {
        "fieldname": "Provider Tax ID",
        "mappingfield": "sProviderTaxID",
        "gridSize": "slds-col slds-size_1-of-3",
        "labelClass": 'slds-p-top_small slds-cell-wrap',
        "valueClass": 'slds-p-vertical_x-small slds-text-body_small slds-text-color_weak slds-cell-wrap'

    },

    {
        "fieldname": "Assigned Analyst",
        "mappingfield": "sAssignedAnalyst",
        "gridSize": "slds-col slds-size_3-of-3",
        "labelClass": 'slds-p-top_x-small slds-cell-wrap',
        "valueClass": 'slds-p-vertical_x-small slds-text-body_small slds-text-color_weak slds-cell-wrap'

    },
    {
        "fieldname": "Claim  Numbers",
        "mappingfield": "sClaimNumber",
        "gridSize": "slds-col slds-size_3-of-3",
        "labelClass": 'slds-p-top_x-small slds-cell-wrap',
        "valueClass": 'slds-p-vertical_x-small slds-text-body_small slds-text-color_weak slds-cell-wrap',

    },
    {
        "fieldname": "Member ID",
        "mappingfield": "sMemberCardId",
        "gridSize": "slds-col slds-size_3-of-3",
        "labelClass": 'slds-p-top_small slds-cell-wrap',
        "valueClass": 'slds-p-vertical_x-small slds-text-body_small slds-text-color_weak slds-cell-wrap'


    },
    {
        "fieldname": "Description of Issue /Appeal report reason ",
        "mappingfield": "sAllegation",
        "gridSize": "slds-col slds-size_3-of-3",
        "labelClass": 'slds-p-top_x-small slds-cell-wrap',
        "valueClass": 'slds-p-vertical_x-small slds-text-body_small slds-text-color_weak slds-cell-wrap',

    },
    {
        "fieldname": "Final Resolution ",
        "mappingfield": "sFinalResolutionCaseNote",
        "gridSize": "slds-col slds-size_3-of-3",
        "labelClass": 'slds-p-top_x-small slds-cell-wrap',
        "valueClass": 'slds-p-vertical_x-small slds-text-body_small slds-text-color_weak slds-cell-wrap',

    }
]


    export const commenttimelinemodel = [
        {
            "fieldname": "icon",
        },
        {
            "fieldname": "headerline",
            "mappingfield": "note"
        },
        {
            "fieldname": "createddatetime",
            "mappingfield": "noteDate"
        },
        {
            "fieldname": "subheaderline",
            "mappingfield": "createdBy"
        }
        ,
        {
            "fieldname": "expanded",
            "mappingfield": "index"
        }
        ,
        {
            "fieldname": "wrapheaderline",
        },
        {
            "compoundvalue": true,
            "fieldname": "compoundvalues",
            "compoundvalues": [{

                "body" : [{
                    "fieldname" : "Notes",
                    "mappingfield" : "note",
                    "isText" : true,
                    "gridSize" : "slds-col slds-size_4-of-4",
                    "labelstyle" : 'padding-bottom: .5rem;',
                    "valuestyle" : 'font-weight:500; margin-left: 0rem; margin-bottom: 1rem ;',
                    "copyableField":true
                }]
                
            }
            ]
        }
    
    ]

export  function getModel(name) {
    switch (name) {
        case "detailsLayout":
            return grievanceAndAppealsDetailLayout;
            break;
        case "timelineModel":
            return commenttimelinemodel;
            break;
        }
}