export const memPlanLayout = [[
  {
    "radio": true,
    "isActionColumn": true,
    "value": "10",
    "compoundx": false,
    "interaction": "Member Plan",
    "fieldName": "Id",
    "actionName": "MEMBER_PLAN",
    "disabled": "false",
    "checked": "false"
  }, {
    "compoundx": false,
    "text": true,
    "label": "Plan Member",
    "value": "10",
    "fieldName": "Name",
    "disabled": "No"
  }, {
    "compoundx": true,
    "label": "Product",
    "compoundvalue": [{
      "text": true,
      "label": "Product",
      "value": "10",
      "fieldName": "Product__c",
      "disabled": "No"
    }, {
      "text": true,
      "label": "Product Type",
      "fieldName": "Product_Type__c",
      "value": "11",
      "disabled": "No"
    }]
  }, {
    "compoundx": true,
    "label": "Dates",
    "compoundvalue": [{
      "text": true,
      "label": "Effective Date",
      "value": "10",
      "fieldName": "EffectiveFrom",
      "disabled": "No"
    }, {
      "text": true,
      "label": "End Date",
      "fieldName": "EffectiveTo",
      "value": "11",
      "disabled": "No"
    }, {
      "icon": true,
      "text": true,
      "label": "Status",
      "fieldName": "Member_Coverage_Status__c",
      "value": "11",
      "disabled": "No"
    }]
  }, {
    "compoundx": true,
    "label": "Group",
    "compoundvalue": [{
      "text": true,
      "label": "Group Name",
      "value": "10",
      "fieldName": "Display_Group_Name__c",
      "disabled": "No"
    }, {
      "text": true,
      "label": "Group Number",
      "fieldName": "GroupNumber",
      "value": "11",
      "disabled": "No"
    }]
  }, {
    "compoundx": false,
    "hidden": true, "boolean": true, "isCheckbox": true, "label": "Legacy Delete", "fieldName": "ETL_Record_Deleted__c", "value": "", "disabled": "true"
  }
]];