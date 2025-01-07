/*******************************************************************************************************************************
LWC JS Name : layoutConfig.js
Function    : This JS serves as helper to accountDetailPolicyHum.js

Modification Log: 
Developer Name           Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
* Pradeep Dani                                          12/18/2020                    initial version
* Ritik Agarwal                                         02/18/2021                    remove status__C reference from Member_coverage_status__C
* Arpit Jain/Navajit Sarkar                             03/10/2021                    Call Transfer button changes
* Ashish Kumar                                          03/26/2021                    Replaced Group_Name with Display_Group_Name__C for MemberPlan Object
* Supriya Shastri                                       10/27/2021                 US-2440592 : Account Detail Page Redesign
* Vardhman Jain                                           12/22/2022                 US3879280: Lightning - Consumer/Implement for CPD/General Search & Person Account Page
* Deepak Khandelwal                                      16/02/2023                    US4146763: Lightning - OSB Medicare, OSB Vision and Fitness-ODS Feed
* Vardhman Jain                                          16/08/2023                   US4813842:T1PRJ1097507- MF26942 - C01/Account Management/Pharmacy - Person Account- Block Access to Non-Medical Plans
*********************************************************************************************************************************/
import { getLabels } from 'c/crmUtilityHum';
const labels = getLabels();

export function getPolicyLayout(oUserGroup) {
    if (oUserGroup) {
        if (oUserGroup.bProvider) {
            return memberPolicyProviderPcc;
        } else if (oUserGroup.bRcc || oUserGroup.bPharmacy || oUserGroup.bGeneral) {
            return memberPolicyRcc;
        } else if (oUserGroup.bGbo) {
            return memberPolicy;
        }
    }
}

const memberPolicyRcc = [[
	{
        "isIcon": true,
        "fieldName": "isIconVisible",
        "isLock":"isLocked"
    },
    {
        "button": true,
        "value": "callTransfer",
        "disabled": "No",
        "label": "",
        "compoundx": false,
        "fieldName": "",
        "compoundvalue": [{
            "button": true,
            "buttonlabel": "Call Transfer",
            "value": "",
            "event": "utilityPopout", // event as utilityPopout to call LMS instead of pubsub
            "disabled": "No",
            "type_large": true,
            "type_small": false,
            "fieldName": "Id",
            "rowData": {}
        }]
    },
    { "accordian": true, "emptycell": true, "isActionColumn": true, "compoundx": false, "label": "", "value": "", "fieldName": "Id", "disabled": "No" },
    {
        "compoundx": true, "label": "Member ID",
        "headerHelpText": labels.Hum_Policy_MemberId_Help,
        "compoundvalue": [
            { "hidden": true, "Id": true, "label": "Id", "fieldName": "Id", "value": "", "disabled": "No" },
             { "link": true, "linkwithtooltip": true, "label": "ID", "value": "", "fieldName": "Name", "disabled": "No","linkToChange":'nameLink',"isLock":"isLocked","isOSB":false,"isHpuc":"isHpu"},
            { "text": true, "label": "Medicare ID", "fieldName": "MemberId", "value": "", "disabled": "No" },
            { "icon": true, "label": "", "fieldName": "Member_Coverage_Status__c", "value": "", "disabled": "No" }]
    },
    { "compoundx": false, "label": "Plan Name", "text": true, "value": "", "fieldName": "PlanName", "disabled": "No" },
    {
        "compoundx": true, "label": "Product",
        "compoundvalue": [
            { "text": true, "label": "Product", "value": "", "fieldName": "Product__c", "disabled": "No" },
            { "text": true, "label": "Code", "value": "", "fieldName": "Product_Type_Code__c", "disabled": "No" },
            { "text": true, "label": "Type", "fieldName": "Product_Type__c", "value": "", "disabled": "No" }]
    },
    {
        "compoundx": true, "label": "Effective / End Date",
        "compoundvalue": [
            { "text": true, "label": "Effective", "value": "", "fieldName": "EffectiveFrom", "disabled": "No" },
            { "text": true, "label": "End", "fieldName": "EffectiveTo", "value": "", "disabled": "No" },
            {"hidden": true, "boolean": true, "isCheckbox": true, "label": "Legacy Delete", "fieldName": "ETL_Record_Deleted__c", "value": "", "disabled": "true"}]
    },
    {
        "compoundx": true, "label": "Group",
        "compoundvalue": [
            { "text": true, "label": "Name", "value": "", "fieldName": "Display_Group_Name__c", "disabled": "No" },
            { "text": true, "label": "Number", "fieldName": "GroupNumber", "value": "", "disabled": "No" }]
    }
]];

const memberPolicy = [[
	{
        "isIcon": true,
        "fieldName": "isIconVisible",
        "isLock":"isLocked"
    },
    {
        "button": true,
        "value": "callTransfer",
        "disabled": "No",
        "label": "",
        "compoundx": false,
        "fieldName": "",
        "compoundvalue": [{
            "button": true,
            "buttonlabel": "Call Transfer",
            "value": "",
            "event": "utilityPopout", // event as utilityPopout to call LMS instead of pubsub
            "disabled": "No",
            "type_large": true,
            "type_small": false,
            "fieldName": "Id",
            "rowData": {}
        }]
    },
    { "accordian": true, "emptycell": true, "isActionColumn": true, "compoundx": false, "label": "", "value": "", "fieldName": "Id", "disabled": "No" },
    {
        "compoundx": true, "label": "Member ID",
        "headerHelpText": labels.Hum_Policy_MemberId_Help,
        "compoundvalue": [
            { "hidden": true, "Id": true, "label": "Id", "fieldName": "Id", "value": "", "disabled": "No" },
            { "link": true, "linkwithtooltip": true, "label": "ID", "value": "", "fieldName": "Name", "disabled": "No", "linkToChange":'nameLink',"isLock":"isLocked","isOSB":false,"isHpuc":"isHpu"},
            { "text": true, "label": "Medicare ID", "fieldName": "MemberId", "value": "", "disabled": "No" },
            { "icon": true, "label": "", "fieldName": "Member_Coverage_Status__c", "value": "", "disabled": "No" }]
    },
   
    {
        "compoundx": true, "label": "Product",
        "compoundvalue": [
            { "text": true, "label": "Product", "value": "", "fieldName": "Product__c", "disabled": "No" },
            { "text": true, "label": "Type", "fieldName": "Product_Type__c", "value": "", "disabled": "No" }]
    },
    {
        "compoundx": true, "label": "Effective / End Date",
        "compoundvalue": [
            { "text": true, "label": "Effective", "value": "", "fieldName": "EffectiveFrom", "disabled": "No" },
            { "text": true, "label": "End", "fieldName": "EffectiveTo", "value": "", "disabled": "No" },
        {"hidden": true, "boolean": true, "isCheckbox": true, "label": "Legacy Delete", "fieldName": "ETL_Record_Deleted__c", "value": "", "disabled": "true"}]
    },
    {
        "compoundx": true, "label": "Group",
        "compoundvalue": [
            { "text": true, "label": "Name", "value": "", "fieldName": "Display_Group_Name__c", "disabled": "No" },
            { "text": true, "label": "Number", "fieldName": "GroupNumber", "value": "", "disabled": "No" }]
    },
    { "compoundx": false, "label": "Last Updated", "text": true, "value": "", "fieldName": "LastModifiedDate", "disabled": "No" },
]];

const memberPolicyProviderPcc = [[
	{
        "isIcon": true,
        "fieldName": "isIconVisible",
        "isLock":"isLocked"
    },
    {
        "button": true,
        "value": "callTransfer",
        "disabled": "No",
        "label": "",
        "compoundx": false,
        "fieldName": "",
        "compoundvalue": [{
            "button": true,
            "buttonlabel": "Call Transfer",
            "value": "",
            "event": "utilityPopout", // event as utilityPopout to call LMS instead of pubsub
            "disabled": "No",
            "type_large": true,
            "type_small": false,
            "fieldName": "Id",
            "rowData": {}
        }]
    },
    { "radio": true, "isActionColumn": true, "value": "", "compoundx": false, "interaction": "DetailMemberPolicy", "fieldName": "Id" },
    { "accordian": true, "emptycell": true, "isActionColumn": true, "compoundx": false, "label": "", "value": "", "fieldName": "Id", "disabled": "No" },
    {
        "compoundx": true, "label": "Member ID",
        "headerHelpText": labels.Hum_Policy_MemberId_Help,
        "compoundvalue": [
            { "hidden": true, "Id": true, "label": "Id", "fieldName": "Id", "value": "", "disabled": "No" },
             { "link": true, "linkwithtooltip": true, "label": "ID", "value": "", "fieldName": "Name", "disabled": "No","linkToChange":'nameLink',"isLock":"isLocked","isOSB":false,"isHpuc":"isHpu"},
            { "text": true, "label": "Medicare ID", "fieldName": "MemberId", "value": "", "disabled": "No" },
            { "icon": true, "label": "", "fieldName": "Member_Coverage_Status__c", "value": "", "disabled": "No" }]
    },
    { "compoundx": false, "label": "Plan Name", "text": true, "value": "", "fieldName": "PlanName", "disabled": "No" },
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
            {"hidden": true, "boolean": true, "isCheckbox": true, "label": "Legacy Delete", "fieldName": "ETL_Record_Deleted__c", "value": "", "disabled": "true"}]
    },
    {
        "compoundx": true, "label": "Group",
        "compoundvalue": [
            { "text": true, "label": "Name", "value": "", "fieldName": "Display_Group_Name__c", "disabled": "No" },
            { "text": true, "label": "Number", "fieldName": "GroupNumber", "value": "", "disabled": "No" }]
    }
]];