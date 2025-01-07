/*******************************************************************************************************************************
LWC JS Name : humlookupConfig.js
Function    : This JS serves as helper to humlookupSearch.js

Modification Log: 
Developer Name                Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
* Surendra Vemulapalli                                                           initial version
*********************************************************************************************************************************/

export function getFielddetails(interactingwith) {
 
       if(interactingwith)
       {
          return cinteractingWith
        }else{ 
          return cinteractionnum;
        }
           
      }

const cinteractingWith = [
          [{
            "radio": true,
            "isActionColumn": true,
            "value": "",
            "chk": "",
            "compoundx": false,
            "fieldName": "Id",
            "actionName": 'FIREINTERACTIONS'
          },
          {
            "text": true,
            "label": "Account Name",
            "value": "",
            "compoundx": false,
            "fieldName": "Name"
          },
          {
            "text": true,
            "label": "Birthdate",
            "value": "",
            "compoundx": false,
            "fieldName": "Birthdate__c"
          },
          {
            "text": true,
            "label": "Account Site",
            "value": "",
            "compoundx": false,
            "fieldName": "Site"
          },
          {
            "text": true,
            "label": "Account Owner Alias",
            "value": "",
            "compoundx": false,
            "fieldName": ""
          },
          {
            "text": true,
            "label": "Type",
            "value": "",
            "compoundx": false,
            "fieldName": "Type"
          }]
        ];

const cinteractionnum = [
          [
            {
              "radio": true,
              "isActionColumn": true,
              "value": "",
              "chk": "",
              "compoundx": false,
              "fieldName": "Id",
              "actionName": 'FIREINTERACTIONS'
            },
          {
            "text": true,
            "label": "Interaction #",
            "value": "",
            "compoundx": false,
            "fieldName": "Name"
          },
          {
            "text": true,
            "label": "Name",
            "value": "",
            "compoundx": false,
            "fieldName": "Caller_Name__c"
          },
          {
            "text": true,
            "label": "Interacting With",
            "value": "",
            "compoundx": false,
            "fieldName": "Interacting_With__c"
          },
          {
            "text": true,
            "label": "Interacting With Type",
            "value": "",
            "compoundx": false,
            "fieldName": "Interacting_With_type__c"
          },
          {
            "text": true,
            "label": "Created By",
            "value": "",
            "compoundx": false,
            "fieldName": "CreatedById"
          }]
        ];