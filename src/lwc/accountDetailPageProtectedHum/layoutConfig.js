export function accFieldsLayout(){
    const detail = [
        {label: 'Account Name', mapping: 'Name', value: ''},
        {label: 'Account Record Type', mapping: 'RecordType', value: ''},
        {label: 'Security Group', mapping: 'Security_Groups__c', value: ''},
    ];
    return detail;
}

export function getPolicyLayout(recType) {
    if(recType==='Business Account'){
       return groupPolicy;
    }else{
       return memberPolicy;
    }
    
    
}
const groupPolicy = [[
    
    { "accordian": true, "emptycell": true, "isActionColumn": true, "compoundx": false, "label": "", "value": "", "fieldName": "Id", "disabled": "No" },
    {
        "compoundx": false, "label": "Action"
    },
    { 
        "compoundx": false, "label": "Policy ID "
    },
    {
        "compoundx": false, "label": "Plan Name",
    },
    {
        "compoundx": false, "label": "Product",
    },
    {
        "compoundx": false, "label": "Effective/End Date",
    },
    {
        "compoundx": false, "label": "Group",
    },
    {
        "compoundx": false, "label": "Platform",
    }
]];
const memberPolicy = [[
    {
        "isIcon": true,
        "fieldName": "isIconVisible",
        "title":"You do not have permission to view this protected member's data.",
        "width":"4%"
    },
    {
        "button": true,
        "value": "callTransfer",
        "disabled": "No",
        "label": "Action",
        "compoundx": false,
        "fieldName": "",
        "compoundvalue": [{
            "button": true,
            "buttonlabel": "Call Transfer",
            "value": "",
            "event": "utilityPopout", // event as utilityPopout to call LMS instead of pubsub
            "disabled": "No",
            "type_large": false,
            "type_small": true,
            "fieldName": "Id",
            "rowData": {}
        }],
        "width":"8%"
    },
    {
        "compoundx": true, "label": "Policy ID",
        "compoundvalue": [{ "text": true, "label": "ID", "value": "", "fieldName": "Name", "disabled": "No" }]
    },
    { "compoundx": false, "label": "Plan Name", "text": true, "value": "", "fieldName": "Plan.Name", "disabled": "No" },
    {
        "compoundx": true, "label": "Product",
        "compoundvalue": [
            { "text": true, "label": "Product", "value": "", "fieldName": "Product__c", "disabled": "No" },
            { "text": true, "label": "Type", "fieldName": "Product_Type__c", "value": "", "disabled": "No" },
            { "text": true, "label": "Code", "value": "", "fieldName": "Product_Type_Code__c", "disabled": "No" }]
    },
    {
        "compoundx": true, "label": "Effective / End Date",
        "compoundvalue": [
            { "text": true, "label": "Effective", "value": "", "fieldName": "EffectiveFrom", "disabled": "No" },
            { "text": true, "label": "End", "fieldName": "EffectiveTo", "value": "", "disabled": "No" },
            { "boolean": true, "label": "Legacy Delete", "fieldName": "ETL_Record_Deleted__c", "value": "", "disabled": "No"}]
    },
    {
        "compoundx": true, "label": "Group",
        "compoundvalue": [
            { "text": true, "label": "Name", "value": "", "fieldName": "Display_Group_Name__c", "disabled": "No" },
            { "text": true, "label": "Number", "fieldName": "GroupNumber", "value": "", "disabled": "No" }]
    },
    { "compoundx": false, "label": "Platform", "text": true, "value": "", "fieldName": "Policy_Platform__c", "disabled": "No" },
]];