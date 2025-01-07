/*******************************************************************************************************************************
LWC JS Name : coachingMemberSearchPolicyModals.js
Function    : Member Search policy modals

Modification Log: 
Developer Name           Code Review              Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Jasmeen Shangari                              03/09/2021                Policies Modals
* Jasmeen Shangari                              03/31/2021                Replace Group_name__c with Display_Group_Name__C
*********************************************************************************************************************************/
export function getPolicyLayout(oUserGroup) {
    if (oUserGroup) {
        if (oUserGroup.bProvider || oUserGroup.bRcc) {
            return memberPolicyRcc;
        } else if (oUserGroup.bGbo || oUserGroup.bPharmacy || oUserGroup.bGeneral) {
            return memberPolicyGbo;
        }
    }
}

export const memberPolicyGbo = [[
    { "radio": true, "isActionColumn": true, "value": "", "compoundx": false, "interaction": "Policy__c", "fieldName": "Id" },
    {
        "compoundx": true, "label": "Member ID", "compoundvalue": [
            { "text": true, "label": "ID", "value": "10", "fieldName": "Name", "disabled": "No" },
            { "icon": true, "label": "", "iconValue": "", "fieldName": "Member_Coverage_Status__c", "value": "11", "disabled": "No" }]
    },
    { "text": true, "compoundx": false, "label": "Plan Name", "value": "", "fieldName": "PlanName", "disabled": "No" },
    {
        "compoundx": true, "label": "Product", "compoundvalue": [
            { "text": true, "label": "Product", "value": "10", "fieldName": "Product__c", "disabled": "No" },
            { "text": true, "label": "Type", "fieldName": "Product_Type__c", "value": "11", "disabled": "No" }]
    },
    {
        "compoundx": true, "label": "Effective / End Date", "compoundvalue": [
            { "text": true, "label": "Effective", "value": "10", "fieldName": "EffectiveFrom", "disabled": "No" },
            { "text": true, "label": "End", "fieldName": "EffectiveTo", "value": "11", "disabled": "No" }]
    },
    {
        "compoundx": true, "label": "Group", "compoundvalue": [
            { "text": true, "label": "Name", "value": "10", "fieldName": "Display_Group_Name__c", "disabled": "No" },
            { "text": true, "label": "Number", "fieldName": "GroupNumber", "value": "11", "disabled": "No" }]
    },
    { "text": true, "label": "Platform", "fieldName": "Policy_Platform__c", "value": "11", "disabled": "No" }
]];

export const memberPolicyRcc = [[
    { "radio": true, "isActionColumn": true, "value": "", "compoundx": false, "interaction": "MEMBER_POLICY", "fieldName": "Id" },
    {
        "compoundx": true, "label": "Member ID", "compoundvalue": [
            { "text": true, "label": "ID", "value": "10", "fieldName": "Name", "disabled": "No" },
            { "text": true, "label": "Medicare ID", "fieldName": "MedicareId__c", "value": "11", "disabled": "No" },
            { "icon": true, "label": "", "iconValue": "", "fieldName": "Member_Coverage_Status__c", "value": "11", "disabled": "No" }]
    },
    { "text": true, "compoundx": false, "label": "Plan Name", "value": "", "fieldName": "PlanName", "disabled": "No" },
    {
        "compoundx": true, "label": "Product", "compoundvalue": [
            { "text": true, "label": "Product", "value": "10", "fieldName": "Product__c", "disabled": "No" },
            { "text": true, "label": "Type", "fieldName": "Product_Type__c", "value": "11", "disabled": "No" },
            { "text": true, "label": "Code", "value": "", "fieldName": "Product_Type_Code__c", "disabled": "No" }]
    },
    {
        "compoundx": true, "label": "Effective / End Date", "compoundvalue": [
            { "text": true, "label": "Effective", "value": "10", "fieldName": "EffectiveFrom", "disabled": "No" },
            { "text": true, "label": "End", "fieldName": "EffectiveTo", "value": "11", "disabled": "No" }]
    }, {
        "compoundx": true, "label": "Group", "compoundvalue": [
            { "text": true, "label": "Name", "value": "10", "fieldName": "Display_Group_Name__c", "disabled": "No" },
            { "text": true, "label": "Number", "fieldName": "GroupNumber", "value": "11", "disabled": "No" }]
    },
    { "text": true, "label": "Platform", "fieldName": "Policy_Platform__c", "value": "11", "disabled": "No" }
]];