export function getTaskFormLayout(){
    return taskModel;
}

export const taskModel = [
    [{
      "text": true,
      "label": "Task Number",
      "compoundx": false,
      "value": "",
      "fieldName": "sTask_Number"
    },
    {
      "text": true,
      "label": "Task Type",
      "compoundx": false,
      "value": "",
      "fieldName": "sType"
    },
    {
      "text": true,
      "label": "Due Date",
      "compoundx": false,
      "value": "",
      "fieldName": "sActivityDate"
    },
    {
      "text": true,
      "label": "Status",
      "compoundx": false,
      "value": "",
      "fieldName": "sStatus"
    },
    {
      "text": true,
      "label": "Priority",
      "compoundx": false,
      "value": "",
      "fieldName": "sPriority"
    },
    {
      "text": true,
      "label": "Task Age",
      "compoundx": false,
      "value": "",
      "fieldName": "sTask_Age"
    },
    {
      "text": true,
      "label": "Date/Time Opened",
      "compoundx": false,
      "value": "",
      "fieldName": "sCreated_DateTime"
    },
    {
        "text": true,
        "label": "Date/Time Closed",
        "compoundx": false,
        "value": "",
        "fieldName": "sDate_Time_Closed"
      },
    {
        "text": true,
        "label": "Task Owner",
        "compoundx": false,
        "value": "",
        "fieldName": "sTask_Owner"
      },
      {
        "text": true,
        "label": "Owner Queue",
        "compoundx": false,
        "value": "",
        "fieldName": "sOwner_Queue"
      }
  ]];