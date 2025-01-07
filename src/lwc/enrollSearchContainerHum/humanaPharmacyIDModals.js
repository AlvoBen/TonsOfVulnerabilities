/*LWC Name        : interactionCmp.js
Function        : LWC container to display Interaction log component in Lightning strides app.

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Deepak Khandelwal                   04/14/2023              US-4462413--MF29006 - Genesys - Create interaction for
                                                              FN/LN hyperlink & Save and Continue button from Enrollment Search for UnKnown Member

*****************************************************************************************************************************/

export const getHPColumnLayout = [
    [{
      "radio": true,
      "isActionColumn": true,
      "value": "",
      "chk": "",
      "compoundx": false,
      "interaction": "Member Plan",
      "fieldName": "Id",
      "actionName": 'FIREINTERACTIONS'
    },
    {
      "label": "Name",
      "fieldName": "Name",
      "value": "11",
      "disabled": "No",
      "compoundx": true,
      "compoundvalue": [{
        "Id": true,
        "link": false,
        "icon": false,
        "hidden": true,
        "label": "Id",
        "fieldName": "Id",
        "value": "11",
        "disabled": "No"
      },
      {
        "link": true,
        "icon": false,
        "label": "First Name, M.I.",
        "fieldName": "FirstName",
        "value": "11",
        "disabled": "No",
        "actionName": 'MEMBER_SEARCH_FNLN'
      },
      {
        "Id": true,
        "link": false,
        "icon": false,
        "hidden": true,
        "label": "Id",
        "fieldName": "Id",
        "value": "11",
        "disabled": "No"
      },
      {
        "link": true,
        "icon": false,
        "label": "Last Name",
        "fieldName": "LastName",
        "value": "11",
        "disabled": "No",
        "actionName": 'MEMBER_SEARCH_FNLN'
      },
      {
        "text": true,
        "iconcompoundx": false,
        "label": "Birthdate",
        "fieldName": "Birthdate__c",
        "value": "11"
      },]
    },
    {
      "label": "Mail Order Pharmacy ID",
      "fieldName": "hpLink",
      "value": "11",
      "disabled": "No",
      "compoundx": true,
      "compoundvalue": [{
        "Id": true,
        "link": false,
        "icon": false,
        "hidden": true,
        "label": "Id",
        "fieldName": "Id",
        "value": "11",
        "disabled": "No"
      },
      {
        "link": true,
        "hidden": true,
        "icon": false,
        "label": "Mail Order Pharmacy ID",
        "fieldName": "hpLink",
        "value": "11",
        "disabled": "No",
        "actionName": 'HP_Link_MS'
      },
      {
        "text": true,
        "label": "HP First Name",
        "fieldName": "FirstName",
        "hidden": true,
        "value": "11",
        "disabled": "Yes"
        
      },
      {
        "text": true,
        "label": "HP Last Name",
        "fieldName": "LastName",
        "hidden": true,
        "value": "11",
        "disabled": "Yes"
      },
      {
        "text": true,
        "label": "EnterpriseID",
        "fieldName": "enterpriseID",
        "hidden": true,
        "value": "11",
        "disabled": "Yes"
      }]
    },
    
    {
      "compoundx": true,
      "iconcompoundx": false,
      "label": "Demographics",
      "compoundvalue": [{
        "text": true,
        "label": "State",
        "fieldName": "PersonMailingState",
        "value": "11",
        "disabled": "No"
      },
      {
        "text": true,
        "label": "Zip Code",
        "fieldName": "PersonMailingPostalCode",
        "value": "11",
        "disabled": "No"
      },
      {
        "text": true,
        "label": "Phone",
        "fieldName": "PersonHomePhone",
        "value": "11",
        "disabled": "No",
        "bIsPhone": true
      }],
    },
    {
      "icon": true,
      "label": "Record Type",
      "value": "",
      "iconcompoundx": true,
      "compoundx": true,
      "compoundvalue": [{
        "text": true,
        "label": "State",
        "fieldName": "ETL_Record_Deleted__c",
        "value": "11",
        "disabled": "No"
      },
      {
        "text": true,
        "label": "Phone",
        "fieldName": "RecordType",
        "value": "11",
        "disabled": "No"
      }],
      "iconprop": true,
      "fieldName": "RecordType",
      "fieldIcon": "ETL_Record_Deleted__c"
    },
    {
      "icon": true,
      "isActionColumn": false,
      "isHiddenCol": true,
      "label": "",
      "value": "",
      "membericon": true,
      "iconprop": true,
      "fieldName": ""
    },
    {
      "button": true,
      "value": "3",
      "disabled": "No",
      "label": "Select Interaction",
      "compoundx": false,
      "fieldName": "BillingState",
      "compoundvalue": [{
        "button": true,
        "buttonlabel": "With & About",
        "value": "method1",
        "event": "InteractingWithnAbout",
        "fieldName": "disable",
        "buttondisabled": false,
        "type_large": true,
        "type_small": false,
		"rowData": {}
      },
      {
        "button": true,
        "buttonlabel": "With",
        "value": "method2",
        "event": "InteractingWith",
        "fieldName": "disable",
        "buttondisabled": false,
        "type_small": true,
        "type_large": false,
		"rowData": {}
      },
      {
        "button": true,
        "buttonlabel": "About",
        "value": "method3",
        "event": "InteractingAbout",
        "fieldName": "disable",
        "buttondisabled": false,
        "type_small": true,
        "type_large": false,
		"rowData": {}
      }]
    }
    ]];
    export const getWithoutHPColumnLayout = [
      [{
        "radio": true,
        "isActionColumn": true,
        "value": "",
        "chk": "",
        "compoundx": false,
        "interaction": "Member Plan",
        "fieldName": "Id",
        "actionName": 'FIREINTERACTIONS'
      },
      {
        "label": "Name",
        "fieldName": "Name",
        "value": "11",
        "disabled": "No",
        "compoundx": true,
        "compoundvalue": [{
          "Id": true,
          "link": false,
          "icon": false,
          "hidden": true,
          "label": "Id",
          "fieldName": "Id",
          "value": "11",
          "disabled": "No"
        },
        {
          "link": true,
          "icon": false,
          "label": "First Name, M.I.",
          "fieldName": "FirstName",
          "value": "11",
          "disabled": "No",
          "actionName": 'MEMBER_SEARCH_FNLN'
        },
        {
          "Id": true,
          "link": false,
          "icon": false,
          "hidden": true,
          "label": "Id",
          "fieldName": "Id",
          "value": "11",
          "disabled": "No"
        },
        {
          "link": true,
          "icon": false,
          "label": "Last Name",
          "fieldName": "LastName",
          "value": "11",
          "disabled": "No",
          "actionName": 'MEMBER_SEARCH_FNLN'
        },
        {
          "text": true,
          "iconcompoundx": false,
          "label": "Birthdate",
          "fieldName": "Birthdate__c",
          "value": "11"
        },]
      },    
      {
        "compoundx": true,
        "iconcompoundx": false,
        "label": "Demographics",
        "compoundvalue": [{
          "text": true,
          "label": "State",
          "fieldName": "PersonMailingState",
          "value": "11",
          "disabled": "No"
        },
        {
          "text": true,
          "label": "Zip Code",
          "fieldName": "PersonMailingPostalCode",
          "value": "11",
          "disabled": "No"
        },
        {
          "text": true,
          "label": "Phone",
          "fieldName": "PersonHomePhone",
          "value": "11",
          "disabled": "No",
          "bIsPhone": true
        }],
      },
      {
        "icon": true,
        "label": "Record Type",
        "value": "",
        "iconcompoundx": true,
        "compoundx": true,
        "compoundvalue": [{
          "text": true,
          "label": "State",
          "fieldName": "ETL_Record_Deleted__c",
          "value": "11",
          "disabled": "No"
        },
        {
          "text": true,
          "label": "Phone",
          "fieldName": "RecordType",
          "value": "11",
          "disabled": "No"
        }],
        "iconprop": true,
        "fieldName": "RecordType",
        "fieldIcon": "ETL_Record_Deleted__c"
      },
      {
        "icon": true,
        "isActionColumn": false,
        "isHiddenCol": true,
        "label": "",
        "value": "",
        "membericon": true,
        "iconprop": true,
        "fieldName": ""
      },
      {
        "button": true,
        "value": "3",
        "disabled": "No",
        "label": "Select Interaction",
        "compoundx": false,
        "fieldName": "BillingState",
        "compoundvalue": [{
          "button": true,
          "buttonlabel": "With & About",
          "value": "method1",
          "event": "InteractingWithnAbout",
          "fieldName": "disable",
          "buttondisabled": false,
          "type_large": true,
          "type_small": false,
		   "rowData": {}
        },
        {
          "button": true,
          "buttonlabel": "With",
          "value": "method2",
          "event": "InteractingWith",
          "fieldName": "disable",
          "buttondisabled": false,
          "type_small": true,
          "type_large": false,
		   "rowData": {}
        },
        {
          "button": true,
          "buttonlabel": "About",
          "value": "method3",
          "event": "InteractingAbout",
          "fieldName": "disable",
          "buttondisabled": false,
          "type_small": true,
          "type_large": false,
		  "rowData": {}
        }]
      }
      ]];