export function getAttachmentItemFields(type)
{
    switch(type.toLowerCase()){
        case "print" :
            return printFields;                             
        case "web" :
            return webFields;                              
        case "email" :
            return emailFields;                               
        case "text" :
            return textFields;                              
        case "vat" :
            return vatFields;                             
    }
}

export const emailFields = [
    {
        "displayName" : "Message Name",
        "value" : "sMessageName",
        "customOrderNumber": 1
    },
    {
        "displayName" : "Recipient Email",
        "value" : "sRecipientName",
        "customOrderNumber": 2
    },
    {
        "displayName" : "Status",
        "value" : "statusDescription",
        "customOrderNumber": 3
    },
    {
        "displayName" : "Date",
        "value" : "dDateAndTime",
        "customOrderNumber": 4
    },
    {
        "displayName" : "Tracking ID",
        "value" : "trackingID",
        "customOrderNumber": 5
    },
    {
        "displayName" : "Communication",
        "value" : "sViewUrl",
        "customOrderNumber": 6
    },
    {
        "displayName" : "MsgDefCode",
        "value" : "sMsgDefCode",
        "customOrderNumber": 7
    },
];

export const printFields = [
    {
        "displayName" : "Message Name",
        "value" : "sMessageName",
        "customOrderNumber": 1
    },
    {
        "displayName" : "Type",
        "value" : "title",
        "customOrderNumber": 2
    },
    {
        "displayName" : "Sub Area",
        "value" : "subtitle",
        "customOrderNumber": 3
    },
    {
        "displayName" : "Date",
        "value" : "dDateAndTime",
        "customOrderNumber": 4
    },
    {
        "displayName" : "Tracking ID",
        "value" : "trackingID",
        "customOrderNumber": 5
    },
    {
        "displayName" : "Communication",
        "value" : "sViewUrl",
        "customOrderNumber": 6
    },
    {
        "displayName" : "MsgDefCode",
        "value" : "sMsgDefCode",
        "customOrderNumber": 7
    },
];

export const textFields = [
    {
        "displayName" : "Message Name",
        "value" : "sMessageName",
        "customOrderNumber": 1
    },
    {
        "displayName" : "Recipient Telephone Number",
        "value" : "PhoneNumber",
        "customOrderNumber": 2
    },
    {
        "displayName" : "Status",
        "value" : "statusDescription",
        "customOrderNumber": 3
    },
    {
        "displayName" : "Date",
        "value" : "dDateAndTime",
        "customOrderNumber": 4
    },
    {
        "displayName" : "Tracking ID",
        "value" : "trackingID",
        "customOrderNumber": 5
    },
    {
        "displayName" : "Communication",
        "value" : "sViewUrl",
        "customOrderNumber": 6
    },
    {
        "displayName" : "MsgDefCode",
        "value" : "sMsgDefCode",
        "customOrderNumber": 7
    },
];

export const vatFields = [
    {
        "displayName" : "Campaign Name",
        "value" : "campaignName",
        "customOrderNumber": 1
    },
    {
        "displayName" : "Telephone Number",
        "value" : "PhoneNumber",
        "customOrderNumber": 2
    },
    {
        "displayName" : "Status",
        "value" : "statusDescription",
        "customOrderNumber": 3
    },
    {
        "displayName" : "Status Result",
        "value" : "subStatusDesc",
        "customOrderNumber": 4
    },
    {
        "displayName" : "Date",
        "value" : "dDateAndTime",
        "customOrderNumber": 5
    },
    {
        "displayName" : "Tracking ID",
        "value" : "trackingID",
        "customOrderNumber": 6
    },
    {
        "displayName" : "Campaign Summary",
        "value" : "sViewUrl",
        "customOrderNumber": 7
    },
    {
        "displayName" : "MsgDefCode",
        "value" : "sMsgDefCode",
        "customOrderNumber": 8
    },
];

export const webFields = [
    {
        "displayName" : "Message Name",
        "value" : "sMessageName",
        "customOrderNumber": 1
    },
    {
        "displayName" : "Type",
        "value" : "title",
        "customOrderNumber": 2
    },
    {
        "displayName" : "Sub Area",
        "value" : "subtitle",
        "customOrderNumber": 3
    },
    {
        "displayName" : "Date",
        "value" : "dDateAndTime",
        "customOrderNumber": 4
    },
    {
        "displayName" : "Tracking ID",
        "value" : "trackingID",
        "customOrderNumber": 5
    },
    {
        "displayName" : "Communication",
        "value" : "sViewUrl",
        "customOrderNumber": 6
    },
    {
        "displayName" : "MsgDefCode",
        "value" : "sMsgDefCode",
        "customOrderNumber": 7
    },
];