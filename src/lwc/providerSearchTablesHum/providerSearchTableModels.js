/*******************************************************************************************************************************
LWC JS Name : providerSearchTableModels.js
Function    : This JS serves as helper to providerSearchTablesHum.js. 

Modification Log: 
Developer Name           Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
* Pradeep Dani                                          12/18/2020                    initial version
* Prasuna Pattabhi                                      04/14/2023                    US_447066
* Swarnalina Laha                                       09/27/2023                    US: 5051428: Add Shipping Address
*********************************************************************************************************************************/
export function getProviderSearchLayout(oFormData) {
    let bIsFacilitySearch = true;
    if (oFormData && oFormData.sFirstName != '' && oFormData.sLastName != null) bIsFacilitySearch = false;
    return bIsFacilitySearch ? providerFacilityResultLayout : providerNonFacilityResultLayout;
}

const providerFacilityResultLayout = [
    [{ "radio": true, "isActionColumn": true, "value": "", "compoundx": false, "fieldName": "sExtID", "actionName": 'FIREINTERACTIONS' },
    {
        "compoundx": true, "iconcompoundx": false, "value": "3", "disabled": "No", "label": "Provider Details",
        "compoundvalue": [
            { "Id": true, "link": false, "icon": false, "hidden": true, "label": "Id", "fieldName": "sMemberId", "value": "", "disabled": "No" },
            { "link": true, "label": "Name", "fieldName": "sDBA", "value": "", "disabled": "No" ,"actionName": 'PROVIDER_SEARCH_NAME'},
            { "label": "NPI", "fieldName": "sNPI", "value": "", "disabled": "No" }
        ]
    },
    { "isViewAll": true, "label": "Tax ID", "value": "", "compoundx": false, "fieldName": "sTaxID" },
    {
        "compoundx": true, "iconcompoundx": false, "value": "3", "disabled": "No", "label": "Demographics",
        "compoundvalue": [
            { "text": true, "label": "State", "fieldName": "sState", "value": "", "disabled": "No" },
            { "text": true, "label": "Zip Code", "fieldName": "sPostalCode", "value": "", "disabled": "No" }
        ]
    },
	{
        "compoundx": true, "iconcompoundx": false, "value": "3", "disabled": "No", "label": "Shipping Address",
        "compoundvalue": [
            { "text": true, "label": "State", "value": "", "fieldName": "sServiceState" , "disabled": "No"},
			{ "text": true, "label": "Zip Code", "value": "", "fieldName": "sServicezip", "disabled": "No" },
        ]
    },
    { "text": true, "label": "Specialty", "value": "", "compoundx": false, "fieldName": "sSpeciality" },
    { "icon": true, "label": "Record Type", "value": "Provider", "compoundx": false, "fieldName": "sPend" },
    {
        "button": true, "value": "3", "disabled": "No", "label": "Select Interaction", "compoundx": false,
        "compoundvalue": [
            { "button": true, "buttonlabel": "With & About", "value": "method1", "event": "pInteractingWithnAbout", "disabled": "No", "type_large": true, "type_small": false, "rowData": {} },
            { "button": true, "buttonlabel": "With", "value": "method2", "event": "pInteractingWith", "disabled": "No", "type_small": true, "type_large": false, "rowData": {} },
            { "button": true, "buttonlabel": "About", "value": "method3", "event": "pInteractingAbout", "disabled": "No", "type_small": true, "type_large": false, "rowData": {} }
        ]
    }
    ]
];

const providerNonFacilityResultLayout = [
    [{ "radio": true, "isActionColumn": true, "value": "", "compoundx": false, "fieldName": "sExtID", "actionName": 'FIREINTERACTIONS' },
    {
        "compoundx": true, "iconcompoundx": false, "value": "3", "disabled": "No", "label": "First/Last Name",
        "compoundvalue": [
            { "Id": true, "link": false, "icon": false, "hidden": true, "label": "Id", "fieldName": "sMemberId", "value": "", "disabled": "No" },
            { "link": true, "label": "First Name", "fieldName": "sFirstName", "value": "", "disabled": "No","actionName": 'PROVIDER_SEARCH_NAME' },
            { "link": true, "label": "Last Name", "fieldName": "sLastName", "value": "", "disabled": "No" ,"actionName": 'PROVIDER_SEARCH_NAME'},
        ]
    },
    {
        "compoundx": true, "iconcompoundx": false, "value": "3", "disabled": "No", "label": "Organization",
        "compoundvalue": [
            { "Id": true, "link": false, "icon": false, "hidden": true, "label": "Id", "fieldName": "sMemberId", "value": "", "disabled": "No" },
            { "link": true, "label": "Name", "fieldName": "sDBA", "value": "", "disabled": "No" ,"actionName": 'PROVIDER_SEARCH_NAME'},
            { "label": "NPI", "fieldName": "sNPI", "value": "", "disabled": "No" }
        ]
    },
    { "isViewAll": true, "label": "Tax ID", "value": "", "compoundx": false, "fieldName": "sTaxID" },
    {
        "compoundx": true, "iconcompoundx": false, "value": "3", "disabled": "No", "label": "Demographics",
        "compoundvalue": [
            { "text": true, "label": "State", "fieldName": "sState", "value": "", "disabled": "No" },
            { "text": true, "label": "Zip Code", "fieldName": "sPostalCode", "value": "", "disabled": "No" }
        ]
    },
	{
        "compoundx": true, "iconcompoundx": false, "value": "3", "disabled": "No", "label": "Shipping Address",
        "compoundvalue": [
            { "text": true, "label": "State", "value": "", "fieldName": "sServiceState" , "disabled": "No"},
			{ "text": true, "label": "Zip Code", "value": "", "fieldName": "sServicezip", "disabled": "No" },
        ]
    },
    { "text": true, "label": "Specialty", "value": "", "compoundx": false, "fieldName": "sSpeciality" },
    { "icon": true, "label": "Record Type", "value": "Provider", "compoundx": false, "fieldName": "sPend" },
    {
        "button": true, "value": "3", "disabled": "No", "label": "Select Interaction", "compoundx": false,
        "compoundvalue": [
            { "button": true, "buttonlabel": "With & About", "value": "method1", "event": "pInteractingWithnAbout", "disabled": "No", "type_large": true, "type_small": false, "rowData": {} },
            { "button": true, "buttonlabel": "With", "value": "method2", "event": "pInteractingWith", "disabled": "No", "type_small": true, "type_large": false, "rowData": {} },
            { "button": true, "buttonlabel": "About", "value": "method3", "event": "pInteractingAbout", "disabled": "No", "type_small": true, "type_large": false, "rowData": {} }
        ]
    }
    ]
];