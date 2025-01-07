/* 
LWC Name        : enrollmentFormHum
Function        : Enrollment Search Form

*Modification Log:
* Developer Name                  Date                         Description
*
* RajKishore                      11/21/2020                Original Version 
* Supriya                         11/27/2020                Validation fixes
* Supriya                         05/07/2021                US-2199980 field name changes
* Firoja Begam                    10/08/2021                Character Limits - Application Search / Transaction Reply Report
* */
import { getLabels } from "c/crmUtilityHum";
const labels = getLabels();

export const ciFormMeta = [{
      "text": true,
      "val": "",
      "disabled": "No",
      "label": "Social Security Number(SSN)",
      "fieldName": "sSSN",
      "class": "inputfield mandatoryHighlight ssn-id",
      "helpText": labels.HUM_SSN_HelpText,
      "maxLength": 9
    }, {
      "date": true,
      "val": "",
      "disabled": "No",
      "size": 6,
      "label": "Start Date",
      "fieldName": "sEffectiveDate",
      "class": "inputfield mandatoryHighlight effect-start-date"
    },
    {
      "date": true,
      "val": "",
      "disabled": "No",
      "size": 6,
      "label": "End Date",
      "fieldName": "sEndDate",
      "class": "inputfield mandatoryHighlight effect-end-date"
    }];

  export const cimCbisFormMeta = [{
    "text": true,
    "val": "",
    "disabled": "No",
    "label": "Social Security Number(SSN)",
    "fieldName": "sSSN",
    "class": "cim-cbis-SSN   inputfield",
    "helpText": labels.HUM_SSN_HelpText,
    "maxLength": 9,
    "requiredMsg":"Social Security Number (SSN) must be 9 digits"
  },
  {
    "text": true,
    "val": "",
    "disabled": "No",
    "label": "First Name",
    "fieldName": "fName",
    "maxLength": 25,
    "class":"cim-cbis-fn cim-cbis-input inputfield",
    "helpText": labels.enrollmentSearchFormStartHelpText,
    "requiredMsg":"Please enter First Name"

  }, {
    "text": true,
    "val": "",
    "disabled": "No",
    "label": "Last Name",
    "fieldName": "lName",
    "maxLength": 25,
    "class":"cim-cbis-ln cim-cbis-input inputfield",
    "helpText": labels.enrollmentSearchFormLastNameHelpText,
    "requiredMsg":"Please enter Last Name"
  },
  {
    "date": true,
    "val": "",
    "disabled": "No",
    "label": "Birthdate",
    "fieldName": "DOB",
    "class":"cim-cbis-dob cim-cbis-input birthdate-input inputfield",
    "requiredMsg": "A Date of Birth is required to search for CIM members"
  },
  {
    "picklist": true,
    "val": "",
    "disabled": "No",
    "label": "State",
    "fieldName": "state",
    "class":"cim-cbis-st cim-cbis-input inputfield",
    "requiredMsg":"Complete this field."
  }];
  
export const cbisFormMeta = [{
      "text": true,
      "val": "",
      "disabled": "No",
      "label": "First Name",
      "fieldName": "fName",
      "maxLength": 25,
      "class":"cbis-input inputfield",
      "helpText": labels.enrollmentSearchFormStartHelpText,
      "requiredMsg":"Please enter First Name"

    }, {
      "text": true,
      "val": "",
      "disabled": "No",
      "label": "Last Name",
      "fieldName": "lName",
      "maxLength": 25,
      "class":"cbis-input inputfield",
      "helpText": labels.enrollmentSearchFormLastNameHelpText,
      "requiredMsg":"Please enter Last Name"
    },
    {
      "date": true,
      "val": "",
      "disabled": "No",
      "label": "Birthdate",
      "fieldName": "DOB",
      "class":"cbis-input birthdate-input inputfield",
      "requiredMsg": "A Date of Birth is required to search for CBIS members"
    },
    {
      "picklist": true,
      "val": "",
      "disabled": "No",
      "label": "State",
      "fieldName": "state",
      "class":"cbis-input inputfield",
      "requiredMsg": "A State is required to search for CBIS members"
    }];

export const trrFormMeta =  [{
      "text": true,
      "value": "",
      "disabled": "No",
      "label": "Medicare ID",
      "fieldName": "medicareId",
      "maxLength": "12",
      "class":"inputfield formfield medicare-id"
    },
    {
      "text": true,
      "value": "",
      "disabled": "No",
      "label": "First Name",
      "fieldName": "fName",
      "maxLength": 25,
      "helpText": labels.enrollmentSearchFormStartHelpText,
      "class": "inputfield combo-field mandatoryHighlight formfield firstname",
      "maxlength": "25"
    },
    {
      "text": true,
      "value": "",
      "disabled": "No",
      "label": "Last Name",
      "fieldName": "lName",
      "maxLength": 25,
      "helpText": labels.enrollmentSearchFormLastNameHelpText,
      "class": "inputfield combo-field mandatoryHighlight formfield lastname"
    },
    {
      "date": true,
      "value": "",
      "disabled": "No",
      "label": "Birthdate",
      "fieldName": "DOB",
      "class": "inputfield birthdate-input date-field combo-field mandatoryHighlight formfield"
    },
    {
      "date": true,
      "value": "",
      "disabled": "No",
      "size": 6,
      "label": "Received Start Date",
      "fieldName": "Hum_ReceivedDateFrom",
      "class": "inputfield date-field humana-start-date"
    },
    {
      "date": true,
      "value": "",
      "disabled": "No",
      "size": 6,
      "label": "Received End Date",
      "fieldName": "Hum_ReceivedDateTo",
      "class": "inputfield date-field humana-end-date"
    }];

export const appSearchFormMeta = [{
      "text": true,
      "value": "",
      "disabled": "No",
      "label": "Medicare ID",
      "fieldName": "medicareId",
      "maxLength": "12",
      "class": "inputfield formfield medicare-id"
    },
    {
      "text": true,
      "value": "",
      "disabled": "No",
      "label": "Medicaid ID",
      "fieldName": "sMedicaidId",
      "maxLength": "20",
      "class": "inputfield formfield medicaid-id"
    },
    {
      "text": true,
      "class": "inputfield",
      "value": "",
      "disabled": "No",
      "label": "Application ID",
      "fieldName": "sApplicationId",
      "maxLength": "16",
      "class": "inputfield formfield app-id"
    },
    {
      "text": true,
      "value": "",
      "disabled": "No",
      "size": 6,
      "label": "Bar Code",
      "fieldName": "sBarCode",
      "maxLength": "10",
      "class": "inputfield formfield barcode"
    },
    {
      "text": true,
      "value": "",
      "disabled": "No",
      "size": 6,
      "label": "OEC Confirmation",
      "fieldName": "sOECConfirmationId",
      "maxLength": "14",
      "class": "inputfield formfield oec-code"
    },
    {
      "text": true,
      "value": "",
      "disabled": "No",
      "label": "First Name",
      "fieldName": "fName",
      "maxLength": 25,
      "class": "inputfield formfield combo-field mandatoryHighlight firstname"
    },
    {
      "text": true,
      "value": "",
      "disabled": "No",
      "label": "Last Name",
      "fieldName": "lName",
      "maxLength": 25,
      "class": "inputfield formfield combo-field mandatoryHighlight lastname"
    },
    {
      "date": true,
      "value": "",
      "disabled": "No",
      "label": "Birthdate",
      "fieldName": "DOB",
      "class": "inputfield formfield birthdate-input combo-field mandatoryHighlight"
    },
    {
      "date": true,
      "value": "",
      "disabled": "No",
      "size": 6,
      "label": "Effective Start Date",
      "fieldName": "sEffectiveDateFrom",
      "class": "inputfield humana-start-date date-field"
    },
    {
      "date": true,
      "value": "",
      "disabled": "No",
      "size": 6,
      "label": "Effective End Date	",
      "fieldName": "sEffectiveDateTo",
      "class": "inputfield humana-end-date date-field"
    },
    {
      "date": true,
      "value": "",
      "disabled": "No",
      "size": 6,
      "label": "Received Start Date",
      "fieldName": "sReceivedDateFrom",
      "class": "inputfield humana-start-date date-field"
    },
    {
      "date": true,
      "value": "",
      "disabled": "No",
      "size": 6,
      "label": "Received End Date	",
      "fieldName": "sReceivedDateTo",
      "class": "inputfield humana-end-date date-field"
    }];