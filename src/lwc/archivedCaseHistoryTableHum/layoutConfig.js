export function getCaseHistoryFormLayout(){
    return caseHistoryModel;
}

export const caseHistoryModel = [
    [{
      "text": true,
      "label": "Date",
      "compoundx": false,
      "value": "",
      "fieldName": "sCreatedDate"
    },{
        "text": true,
        "label": "Field",
        "compoundx": false,
        "value": "",
        "fieldName": "sField"
        
      },{
        "label": "User",
        "compoundx": true,
        "compoundvalue": [{
          "link": true,
          "icon": false,
          "hidden": true,
          "fieldName": "sCreatedByName",
          "value": "",
          "disabled": "No"
        }, {
          "hidden": true,
          "Id": true,
          "label": "Id",
          "fieldName": "sCreatedById",
          "value": "",
          "disabled": "No"
        }]
      },{
        "text": true,
        "label": "Original Value",
        "compoundx": false,
        "value": "",
        "fieldName": "sOldValue"
      },{
        "text": true,
        "label": "New Value",
        "compoundx": false,
        "value": "",
        "fieldName": "sNewValue"
      }
    
  ]];