// Model to render Dual status level table
import { getLabels } from 'c/crmUtilityHum';
const labels = getLabels();

// date fields mapping
export const datesMap = {
  sEffectiveDateDeeming: 'sEffectiveDateDeeming',
  sEndDate: 'sEndDate',
  sEffectiveDate: 'sEffectiveDate',
  sEndDateDeeming: 'sEndDateDeeming'
};
export const costShareInfo = [
    [{
      "text": true,
      "label": "Effective Date",
      "compoundx": false,
      "value": "",
      "fieldName": datesMap.sEffectiveDate
    },
    {
      "text": true,
      "label": "End Date",
      "compoundx": false,
      "value": "",
      "fieldName": datesMap.sEndDate
    },
    {
      "text": true,
      "label": "Value",
      "compoundx": false,
      "value": "",
      "fieldName": "sValue",
      "sHelpText": labels.dualStatusValueHelpText_Hum,      
      "sHelpTextAlign": "bottom"
    },
    {
      "text": true,
      "label": "Cost Share Protection Indicator",
      "compoundx": false,
      "value": "",
      "fieldName": "sIndicator",
      "sHelpText": labels.costShareHelpText_Hum,
      "sHelpTextAlign": "left",
      "width": '40%'
    }
]];

// Model to render Deeming period table
export const deemingPeriod = [
    [{
      "text": true,
      "label": "Effective Date",
      "compoundx": false,
      "value": "",
      "fieldName": datesMap.sEffectiveDateDeeming
    },
    {
      "text": true,
      "label": "End Date",
      "compoundx": false,
      "value": "",
      "fieldName": datesMap.sEndDateDeeming
    },
    {
      "text": true,
      "label": "Indicator",
      "compoundx": false,
      "value": "",
      "fieldName": "sIndicatorDeeming"
    }
]];