export function getRTITimelineModel(name) {
    switch (name) {
        case "icons":
            return rtitimelineicons;
            break;
        case "rtitimelinemodel":
            return rtitimelinemodel;
            break;
        case "textchannelbody":
            return textchannelbody;
            break;
        case "emailchannelbody":
            return emailchannelbody;
            break;
        case "printchannelbody":
            return printchannelbody;
            break;
        case "webchannelbody":
            return webchannelbody;
            break;
        case "vatchannelbody":
            return vatchannelbody;
            break;
        case "inboundcommunicationbody":
            return inboundcommunicationbody;
            break;

    }
}
export const rtitimelinemodel = [
    {
        "fieldname": "icon",
        "mappingfield": "sChannelCode"
    },
    {
        "fieldname": "headerline",
        "mappingfield": "title"
    },
    {
        "fieldname": "subheaderline",
        "mappingfield": "sMessageName"
    },
    {
        "fieldname": "createddatetime",
        "mappingfield": "dDateAndTime"
    },
    {
        "fieldname": "interactionKey",
        "mappingfield": "sInteractionkey"
    }
    ,
    {
        "compoundvalue": true,
        "fieldname": "compoundvalues",
        "compoundvalues": [{

            "body": []
            ,
            "footer": {
                "displaylogging": false
            }
        }
        ]
    }
    ,
    {
        "fieldname": "url",
        "mappingfield": ""
    }

]

export const rtitimelineicons = [
    {
        "iconname": "email",
        "iconvalue": "standard:email",
        "iconclass": "slds-icon_container slds-timeline__icon",
        "iconsize": "small"
    },
    {
        "iconname": "print",
        "iconvalue": "utility:print",
        "iconclass": "slds-icon_container print slds-timeline__icon",
        "iconsize": "x-small",
        "customicon": true,
    },
    {
        "iconname": "vat",
        "iconvalue": "standard:system_and_global_variable",
        "iconclass": "slds-icon_container slds-timeline__icon",
        "iconsize": "small"
    },
    {
        "iconname": "web",
        "iconvalue": "action:web_link",
        "iconclass": "slds-icon_container actionicon slds-timeline__icon",
        "iconsize": "x-small",
        "customicon": true,
        "customiconclass": "webactionicon"
    },
    {
        "iconname": "text",
        "iconvalue": "standard:text",
        "iconclass": "slds-icon_container slds-timeline__icon",
        "iconsize": "small"
    },
    {
        "iconname": "ivr",
        "iconvalue": "custom:custom75",
        "iconclass": "slds-icon_container slds-timeline__icon",
        "iconsize": "small"
    }, {
        "iconname": "mobile app",
        "iconvalue": "standard:apps",
        "iconclass": "slds-icon_container slds-timeline__icon",
        "iconsize": "small"
    }, {
        "iconname": "mobile browser",
        "iconvalue": "custom:custom29",
        "iconclass": "slds-icon_container slds-timeline__icon",
        "iconsize": "small",
    }
]



export const textchannelbody = [
    {
        "fieldname": "Name",
        "mappingfield": "Name",
        "isIcon": false,
        "gridSize": "slds-col slds-size_1-of-4",
        "labelstyle": 'padding-bottom: .5rem;',
        "valuestyle": 'margin-left: 0rem; margin-bottom: 1rem;font-weight: 500;',
        "isText": true
    }, {
        "fieldname": "Communication Channel",
        "mappingfield": "sChannelCode",
        "isIcon": false,
        "isText": true,
        "gridSize": "slds-col slds-size_1-of-4",
        "labelstyle": 'padding-bottom: .5rem;',
        "valuestyle": 'margin-left: 0rem; margin-bottom: 1rem;font-weight: 500;'
    },
    {
        "fieldname": "Recipient Telephone number",
        "mappingfield": "PhoneNumber",
        "isIcon": false,
        "isText": true,
        "gridSize": "slds-col slds-size_1-of-4",
        "labelstyle": 'padding-bottom: .5rem;',
        "valuestyle": 'margin-left: 0rem; margin-bottom: 1rem;font-weight: 500;'
    },
    {
        "fieldname": "Communication",
        "isIcon": true,
        "buttonicons": [{
            "buttoniconname": "View",
            "buttoniconvalue": "utility:preview",
            "buttoniconclass": "communication-icons",
            "buttonvariant": "border-filled",
            "hidden": true
        }
        ],
        "gridSize": "slds-col slds-size_1-of-4",
        "labelstyle": 'padding-bottom: .5rem;',
        "valuestyle": 'margin-left: 0rem; margin-bottom: 1rem; display: inline-flex;'
    },
    {
        "fieldname": "Status",
        "mappingfield": "statusDescription",
        "isIcon": false,
        "isText": true,
        "gridSize": "slds-col slds-size_1-of-4",
        "labelstyle": 'padding-bottom: .5rem;',
        "valuestyle": 'margin-left: 0rem; margin-bottom: 1rem;font-weight: 500;'
    },
    {
        "fieldname": "Sub Area",
        "mappingfield": "subtitle",
        "isIcon": false,
        "isText": true,
        "gridSize": "slds-col slds-size_1-of-4",
        "labelstyle": 'padding-bottom: .5rem;',
        "valuestyle": 'margin-left: 0rem; margin-bottom: 1rem;font-weight: 500;'
    },

    {
        "fieldname": "Message Name",
        "mappingfield": "sMessageName",
        "isIcon": false,
        "isText": true,
        "gridSize": "slds-col slds-size_2-of-4",
        "labelstyle": 'padding-bottom: .5rem;',
        "valuestyle": 'margin-left: 0rem; margin-bottom: 1rem;font-weight: 500;'
    },
    {
        "fieldname": "Interaction Id",
        "mappingfield": "sInteractionkey",
        "isIcon": false,
        "hidden": true,
        "gridSize": "slds-col slds-size_1-of-4",
        "labelstyle": 'padding-bottom: .5rem;',
        "valuestyle": 'margin-left: 0rem; margin-bottom: 1rem;font-weight: 500;'
    },

]

export const emailchannelbody = [
    {
        "fieldname": "Name",
        "mappingfield": "Name",
        "isIcon": false,
        "isText": true,
        "gridSize": "slds-col slds-size_1-of-4",
        "labelstyle": 'padding-bottom: .5rem;',
        "valuestyle": 'margin-left: 0rem; margin-bottom: 1rem;font-weight: 500;'
    }, {
        "fieldname": "Communication Channel",
        "mappingfield": "sChannelCode",
        "isIcon": false,
        "isText": true,
        "gridSize": "slds-col slds-size_1-of-4",
        "labelstyle": 'padding-bottom: .5rem;',
        "valuestyle": 'margin-left: 0rem; margin-bottom: 1rem;font-weight: 500;'
    },
    {
        "fieldname": "Recipient Email",
        "mappingfield": "sRecipientName",
        "isIcon": false,
        "isText": true,
        "gridSize": "slds-col slds-size_1-of-4",
        "labelstyle": 'padding-bottom: .5rem;',
        "valuestyle": 'margin-left: 0rem; margin-bottom: 1rem;font-weight: 500;'
    },
    {
        "fieldname": "Communication",
        "isIcon": true,
        "buttonicons": [{
            "buttoniconname": "View",
            "buttoniconvalue": "utility:preview",
            "buttoniconclass": "communication-icons",
            "buttonvariant": "border-filled",
            "hidden": true
        }
        ],
        "gridSize": "slds-col slds-size_1-of-4",
        "labelstyle": 'padding-bottom: .5rem;',
        "valuestyle": 'margin-left: 0rem; margin-bottom: 1rem; display: inline-flex;',
    },
    {
        "fieldname": "Status",
        "mappingfield": "statusDescription",
        "isIcon": false,
        "isText": true,
        "gridSize": "slds-col slds-size_1-of-4",
        "labelstyle": 'padding-bottom: .5rem;',
        "valuestyle": 'margin-left: 0rem; margin-bottom: 1rem;font-weight: 500;'
    },
    {
        "fieldname": "Sub Area",
        "mappingfield": "subtitle",
        "isIcon": false,
        "isText": true,
        "gridSize": "slds-col slds-size_1-of-4",
        "labelstyle": 'padding-bottom: .5rem;',
        "valuestyle": 'margin-left: 0rem; margin-bottom: 1rem;font-weight: 500;'
    },

    {
        "fieldname": "Message Name",
        "mappingfield": "sMessageName",
        "isIcon": false,
        "isText": true,
        "gridSize": "slds-col slds-size_2-of-4",
        "labelstyle": 'padding-bottom: .5rem;',
        "valuestyle": 'margin-left: 0rem; margin-bottom: 1rem;font-weight: 500;'
    },
    {
        "fieldname": "Interaction Id",
        "mappingfield": "sInteractionkey",
        "isIcon": false,
        "hidden": true,
        "gridSize": "slds-col slds-size_1-of-4",
        "labelstyle": 'padding-bottom: .5rem;',
        "valuestyle": 'margin-left: 0rem; margin-bottom: 1rem;font-weight: 500;'
    },

]


export const printchannelbody = [
    {
        "fieldname": "Name",
        "mappingfield": "Name",
        "isIcon": false,
        "hidden": false,
        "gridSize": "slds-col slds-size_1-of-4",
        "isText": true,
        "labelstyle": 'padding-bottom: .5rem;',
        "valuestyle": 'margin-left: 0rem; margin-bottom: 1rem; font-weight: 500;'
    }, {
        "fieldname": "Communication Channel",
        "mappingfield": "sChannelCode",
        "isIcon": false,
        "hidden": false,
        "gridSize": "slds-col slds-size_1-of-4",
        "isText": true,
        "labelstyle": 'padding-bottom: .5rem;',
        "valuestyle": 'margin-left: 0rem; margin-bottom: 1rem;font-weight: 500;'
    },
    {
        "fieldname": "Sub Area",
        "mappingfield": "subtitle",
        "isIcon": false,
        "hidden": false,
        "gridSize": "slds-col slds-size_1-of-4",
        "isText": true,
        "labelstyle": 'padding-bottom: .5rem;',
        "valuestyle": 'margin-left: 0rem; margin-bottom: 1rem;font-weight: 500;'
    },
    {
        "fieldname": "Communication",
        "isIcon": true,
        "hidden": false,
        "gridSize": "slds-col slds-size_1-of-4",
        "buttonicons": [{
            "buttoniconname": "View",
            "buttoniconvalue": "utility:preview",
            "buttoniconclass": "communication-icons",
            "buttonvariant": "border-filled",
            "hidden": true
        }, {
            "buttoniconname": "Resend",
            "buttoniconvalue": "utility:send",
            "buttoniconclass": "communication-icons",
            "buttonvariant": "border-filled",
            "hidden": true
        }
        ],
        "labelstyle": 'padding-bottom: .5rem;',
        "valuestyle": 'margin-left: 0rem; margin-bottom: 1rem; display: inline-flex;',
    },
    {
        "fieldname": "Type",
        "mappingfield": "title",
        "isIcon": false,
        "hidden": false,
        "gridSize": "slds-col slds-size_1-of-4",
        "isText": true,
        "labelstyle": 'padding-bottom: .5rem;',
        "valuestyle": 'margin-left: 0rem; margin-bottom: 1rem;font-weight: 500;'
    },
    {
        "fieldname": "Message Name",
        "mappingfield": "sMessageName",
        "isIcon": false,
        "hidden": false,
        "gridSize": "slds-col slds-size_3-of-4",
        "isText": true,
        "labelstyle": 'padding-bottom: .5rem;',
        "valuestyle": 'margin-left: 0rem; margin-bottom: 1rem;font-weight: 500;'
    },
    {
        "fieldname": "Interaction Id",
        "mappingfield": "sInteractionkey",
        "isIcon": false,
        "hidden": true,
        "gridSize": "slds-col slds-size_1-of-4",
        "labelstyle": 'padding-bottom: .5rem;',
        "valuestyle": 'margin-left: 0rem; margin-bottom: 1rem;font-weight: 500;'
    },


]


export const webchannelbody = [
    {
        "fieldname": "Name",
        "mappingfield": "Name",
        "isIcon": false,
        "hidden": false,
        "gridSize": "slds-col slds-size_1-of-4",
        "isText": true,
        "labelstyle": 'padding-bottom: .5rem;',
        "valuestyle": 'margin-left: 0rem; margin-bottom: 1rem; font-weight: 500;'
    }, {
        "fieldname": "Communication Channel",
        "mappingfield": "sChannelCode",
        "isIcon": false,
        "hidden": false,
        "gridSize": "slds-col slds-size_1-of-4",
        "isText": true,
        "labelstyle": 'padding-bottom: .5rem;',
        "valuestyle": 'margin-left: 0rem; margin-bottom: 1rem;font-weight: 500;'
    },
    {
        "fieldname": "Sub Area",
        "mappingfield": "subtitle",
        "isIcon": false,
        "hidden": false,
        "gridSize": "slds-col slds-size_1-of-4",
        "isText": true,
        "labelstyle": 'padding-bottom: .5rem;',
        "valuestyle": 'margin-left: 0rem; margin-bottom: 1rem;font-weight: 500;'
    },
    {
        "fieldname": "Communication",
        "isIcon": true,
        "hidden": false,
        "gridSize": "slds-col slds-size_1-of-4",
        "buttonicons": [{
            "buttoniconname": "View",
            "buttoniconvalue": "utility:preview",
            "buttoniconclass": "communication-icons",
            "buttonvariant": "border-filled",
            "hidden": true
        }
        ],
        "labelstyle": 'padding-bottom: .5rem;',
        "valuestyle": 'margin-left: 0rem; margin-bottom: 1rem; display: inline-flex;',
    },
    {
        "fieldname": "Type",
        "mappingfield": "title",
        "isIcon": false,
        "hidden": false,
        "gridSize": "slds-col slds-size_1-of-4",
        "isText": true,
        "labelstyle": 'padding-bottom: .5rem;',
        "valuestyle": 'margin-left: 0rem; margin-bottom: 1rem;font-weight: 500;'
    },
    {
        "fieldname": "Message Name",
        "mappingfield": "sMessageName",
        "isIcon": false,
        "hidden": false,
        "gridSize": "slds-col slds-size_3-of-4",
        "isText": true,
        "labelstyle": 'padding-bottom: .5rem;',
        "valuestyle": 'margin-left: 0rem; margin-bottom: 1rem;font-weight: 500;'
    },
    {
        "fieldname": "Interaction Id",
        "mappingfield": "sInteractionkey",
        "isIcon": false,
        "hidden": true,
        "gridSize": "slds-col slds-size_1-of-4",
        "labelstyle": 'padding-bottom: .5rem;',
        "valuestyle": 'margin-left: 0rem; margin-bottom: 1rem;font-weight: 500;'
    },


]
export const vatchannelbody = [
    {
        "fieldname": "Name",
        "mappingfield": "Name",
        "isIcon": false,
        "isText": true,
        "gridSize": "slds-col slds-size_1-of-4",
        "labelstyle": 'padding-bottom: .5rem;',
        "valuestyle": 'margin-left: 0rem; margin-bottom: 1rem;font-weight: 500;'

    }, {
        "fieldname": "Communication Channel",
        "mappingfield": "sChannelCode",
        "isIcon": false,
        "isText": true,
        "gridSize": "slds-col slds-size_1-of-4",
        "labelstyle": 'padding-bottom: .5rem;',
        "valuestyle": 'margin-left: 0rem; margin-bottom: 1rem;font-weight: 500;'
    },
    {
        "fieldname": "Campaign name ",
        "mappingfield": "campaignName",
        "isIcon": false,
        "isText": true,
        "gridSize": "slds-col slds-size_1-of-4",
        "labelstyle": 'padding-bottom: .5rem;',
        "valuestyle": 'margin-left: 0rem; margin-bottom: 1rem;font-weight: 500;'
    },
    {
        "fieldname": "Campaign summary",
        "isIcon": true,
        "buttonicons": [{
            "buttoniconname": "View",
            "buttoniconvalue": "utility:preview",
            "buttoniconclass": "communication-icons",
            "buttonvariant": "border-filled",
            "hidden": true
        }
        ],
        "gridSize": "slds-col slds-size_1-of-4",
        "labelstyle": 'padding-bottom: .5rem;',
        "valuestyle": 'margin-left: 0rem; margin-bottom: 1rem; display: inline-flex;',
    },
    {
        "fieldname": "Status",
        "mappingfield": "statusDescription",
        "isIcon": false,
        "isText": true,
        "gridSize": "slds-col slds-size_1-of-4",
        "labelstyle": 'padding-bottom: .5rem;',
        "valuestyle": 'margin-left: 0rem; margin-bottom: 1rem;font-weight: 500;'
    },
    {
        "fieldname": "Telephone number",
        "mappingfield": "PhoneNumber",
        "isIcon": false,
        "isText": true,
        "gridSize": "slds-col slds-size_1-of-4",
        "labelstyle": 'padding-bottom: .5rem;',
        "valuestyle": 'margin-left: 0rem; margin-bottom: 1rem;font-weight: 500;'
    },
    {
        "fieldname": "Status Result",
        "mappingfield": "subStatusDesc",
        "isIcon": false,
        "isText": true,
        "gridSize": "slds-col slds-size_2-of-4",
        "labelstyle": 'padding-bottom: .5rem;',
        "valuestyle": 'margin-left: 0rem; margin-bottom: 1rem;font-weight: 500;'
    },
    {
        "fieldname": "Interaction Id",
        "mappingfield": "sInteractionkey",
        "isIcon": false,
        "hidden": true,
        "gridSize": "slds-col slds-size_1-of-4",
        "labelstyle": 'padding-bottom: .5rem;',
        "valuestyle": 'margin-left: 0rem; margin-bottom: 1rem;font-weight: 500;'
    },

]

export const inboundcommunicationbody = [
    {
        "fieldname": "No Data Section",
        "fieldvalue": "No Additional Information Available",
        "isnodatasection": true
    },
]