/*******************************************************************************************************************************
LWC JS Name : layoutConfig.js
Function    : This JS serves as helper to accountDetailSubgroupDivisionsHum.js

Modification Log: 
Developer Name           Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
* Surendra Vemulapalli                                  09/16/2021                    initial version
*********************************************************************************************************************************/

export function getDivisionLayout(platform) {
       if(platform==='EM')
       {
                 return subGrpDivisionMTVx;
        }else{ 
                  return subGrpDivision;
        }
           
      }

const subGrpDivisionMTVx = [
          [{
            "text": true,
            "label": "Division-Class ID Number",
            "value": "",
            "compoundx": false,
            "fieldName": "sSubGroupID"
          },
          {
            "text": true,
            "label": "Division-Class Name",
            "value": "",
            "compoundx": false,
            "fieldName": "sSubGroupName"
          },
          {
            "text": true,
            "label": "Go365 Indicator",
            "value": "",
            "compoundx": false,
            "fieldName": "sVitalityIndicator"
          },
          {
            "text": true,
            "label": "Unit Count",
            "value": "",
            "compoundx": false,
            "fieldName": "sUnitCount"
          },
          {
            "text": true,
            "label": "Unit",
            "value": "",
            "compoundx": false,
            "fieldName": "sUnit"
          },
          {
            "text": true,
            "label": "Effective Provisions",
            "value": "",
            "compoundx": false,
            "fieldName": "sEffectiveProvision"
          },
          {
           "label": "MTVx",
                    "compoundx": true,
                    "compoundvalue": [{
                      "link": true,
                      "icon": false,
                      "hidden": true,
                      "label" : "MTVx",
                      "fieldName": "sMTVLink",
                      "value":"",
                      "disabled": "No",
                      "navToItem": "",
                      "actionName": "EXTERNAL_REF"
            }]
          }]
        ];

const subGrpDivision = [
          [{
            "text": true,
            "label": "Division-Class ID Number",
            "value": "",
            "compoundx": false,
            "fieldName": "sSubGroupID"
          },
          {
            "text": true,
            "label": "Division-Class Name",
            "value": "",
            "compoundx": false,
            "fieldName": "sSubGroupName"
          },
          {
            "text": true,
            "label": "Go365 Indicator",
            "value": "",
            "compoundx": false,
            "fieldName": "sVitalityIndicator"
          },
          {
            "text": true,
            "label": "Unit Count",
            "value": "",
            "compoundx": false,
            "fieldName": "sUnitCount"
          },
          {
            "text": true,
            "label": "Unit",
            "value": "",
            "compoundx": false,
            "fieldName": "sUnit"
          },
          {
            "text": true,
            "label": "Effective Provisions",
            "value": "",
            "compoundx": false,
            "fieldName": "sEffectiveProvision"
          }]
        ];