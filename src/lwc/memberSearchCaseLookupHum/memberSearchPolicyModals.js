/*******************************************************************************************************************************
LWC JS Name : memberSearchPolicyModals.js
Function    : Member Search policy modals

Modification Log: 
Developer Name           Code Review              Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Mohan Kumar N                                  11/25/2020                Policies Modals
* Ashish Kumar                                   03/26/2021                Replaced Group_Name with Display_Group_Name__C for MemberPlan Object
* Mohan kumar N                                  05/18/2021                US: 2272975- Launch member plan detail
* Santhi Mandava                                09/29/2022                   US3398901- Homeoffice/CPD changes
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
    { "radio": true, "interaction": 'MEMBER_POLICY', "isActionColumn": true, "value": "", "compoundx": false, "actionName": "MEMBER_POLICY", "fieldName": "Id" },
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
    {"text": true, "label": "Security", "fieldName": "Plan.Payer.Security_Groups__c", "value": "11", "disabled": "No" },
    { "text": true, "label": "Platform", "fieldName": "Policy_Platform__c", "value": "11", "disabled": "No" }
  ]];
  
  export const memberPolicyRcc = [[
    { "radio": true, "interaction": 'MEMBER_POLICY', "isActionColumn": true, "value": "", "compoundx": false, "actionName": "MEMBER_POLICY", "fieldName": "Id" },
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
    {"text": true, "label": "Security", "fieldName": "Plan.Payer.Security_Groups__c", "value": "11", "disabled": "No" },
    { "text": true, "label": "Platform", "fieldName": "Policy_Platform__c", "value": "11", "disabled": "No" }
  ]];