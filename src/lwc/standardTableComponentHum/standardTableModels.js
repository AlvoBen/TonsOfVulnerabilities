/*******************************************************************************************************************************
LWC JS Name : standardTableModels.js
Function    : Model for dataTables

Modification Log: 
Developer Name           Code Review              Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Ashish Kumar                                   03/26/2021                Replaced Group_Name with Display_Group_Name__C for MemberPlan Object
* Supriya Shastri                                03/23/2021                US-1999420  
* Ritik                                          05/20/2021                Changes Field Mappings
* Supriya Shastri                                 07/02/2021                 US-2172712
* Arpit Jain                                     07/09/2021                US: 2287947- Authenticated Column in the Interactions list
* Kajal Namdev                                   08/16/2021                US-2306063 grouppolicies label update
* Kajal Namdev                                   08/23/2021                US-2306063 groupAccountPolicies,grouppolicies label update
* Swapnali Sonawane                              05/27/2022                US-3143662 - Medical Plan - Benefit Accumulators
* Krishna Teja Samudrala                         06/03/2022                Added interaction log component related code.
* Anuradha Gajbhe                                04/17/2023                User Story 4461361 - Interaction Creation on agency/broker search results- Interacting With and About Buttons (genesys).
* Raj Paliwal                                    04/17/2023                User Story 4461416 - Interaction Log "Save & Continue" button points to Agency/Broker Business Account Page (genesys).
* Deepak khandelwal                                     08/18/2023                US_4905791 --T1PRJ0865978 - MF26212 - Consumer/Chat Transcripts Record page and Case Detail page is not displayed
****************************************************************************************************************************************/
export const groupSearch = [
  [{
    "radio": true,
    "isActionColumn": true,
    "value": "",
    "compoundx": false,
    "interaction": "Policy__c",
    "fieldName": "Id",
    "actionName": 'FIREINTERACTIONS'
  },
  {
    "compoundx": true,
    "iconcompoundx": false,
    "value": "3",
    "disabled": "No",
    "label": "Group Details",
    "compoundvalue": [{
      "link": true,
      "label": "Group Name",
      "fieldName": "Name",
      "value": "11",
      "actionName":'MEMBER_SEARCH',
      "disabled": "No"
    }, {
      "hidden": true,
      "Id": true,
      "label": "Id",
      "fieldName": "Id",
      "value": "",
      "disabled": "No"
    }, {
      "link": true,
      "label": "Group Number",
      "fieldName": "Group_Number__c",
	  "actionName":'MEMBER_SEARCH',
      "value": "11",
      "disabled": "No"
    }],
  },
  {
    "text": true,
    "label": "Contact Information",
    "value": "",
    "compoundx": true,
    "compoundvalue": [{
      "link": false,
      "label": "Phone",
      "fieldName": "Phone",
      "disabled": "No",
      "bIsPhone": true
    }]
  },
  {
    "compoundx": true,
    "iconcompoundx": false,
    "value": "3",
    "disabled": "No",
    "label": "Demographics",
    "isActionColumn": false,
    "compoundvalue": [{
      "text": true,
      "label": "Street",
      "fieldName": "BillingStreet",
      "value": "11",
      "disabled": "No"
    },
    {
      "text": true,
      "label": "State",
      "fieldName": "BillingState",
      "value": "11",
      "disabled": "No"
    },
    {
      "text": true,
      "label": "Zip Code",
      "fieldName": "BillingPostalCode",
      "value": "11",
      "disabled": "No"
    }],
  },
  {
    "icon": true,
    "label": "Record Type",
    "value": "",
    "compoundx": false,
    "fieldName": "RecordType"
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
      "event": "gInteractingWithnAbout",
      "disabled": "No",
      "type_large": true,
      "type_small": false,
      "rowData":{}
    },
    {
      "button": true,
      "buttonlabel": "With",
      "value": "method2",
      "event": "gInteractingWith",
      "disabled": "No",
      "type_small": true,
      "type_large": false,
      "rowData":{}
    },
    {
      "button": true,
      "buttonlabel": "About",
      "value": "method3",
      "event": "gInteractingAbout",
      "disabled": "No",
      "type_small": true,
      "type_large": false,
      "rowData":{}
    }]
  }
  ]];

export const memberSearch = [
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
    "label": "First Name, M.I.",
    "fieldName": "FirstName",
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
      "hidden": true,
      "label": "First Name, M.I.",
      "fieldName": "FirstName",
      "value": "11",
      "disabled": "No",
      "actionName": 'MEMBER_SEARCH'
    }]
  },
  {
    "label": "Last Name",
    "fieldName": "LastName",
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
      "label": "Last Name",
      "fieldName": "LastName",
      "value": "11",
      "disabled": "No",
      "actionName": 'MEMBER_SEARCH'
    }]
  },
  {
    "text": true,
    "iconcompoundx": false,
    "label": "Birthdate",
    "fieldName": "Birthdate__c"
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
      "event": "",
      "fieldName": "disable",
      "buttondisabled": false,
      "type_large": true,
      "type_small": false
    },
    {
      "button": true,
      "buttonlabel": "With",
      "value": "method2",
      "event": "",
      "fieldName": "disable",
      "buttondisabled": false,
      "type_small": true,
      "type_large": false
    },
    {
      "button": true,
      "buttonlabel": "About",
      "value": "method3",
      "event": "",
      "fieldName": "disable",
      "buttondisabled": false,
      "type_small": true,
      "type_large": false
    }]
  }
  ]];
export const purchaserplan = [
  [{
    "text": true,
    "label": "Plan Name",
    "value": "10",
    "fieldName": "Name",
    "disabled": "No"
  },
  {
    "text": true,
    "label": "Effective From",
    "value": "10",
    "fieldName": "EffectiveFrom",
    "disabled": "No",
    "compoundx": true,
    "compoundvalue": [{
      "text": true,
      "label": "Effective",
      "value": "",
      "fieldName": "EffectiveFrom",
      "disabled": "No"
    }]
  }, {
    "text": true,
    "label": "End Date",
    "value": "",
    "disabled": "No",
    "compoundx": true,
    "compoundvalue": [{
      "text": true,
      "label": "End",
      "value": "",
      "fieldName": "EffectiveTo",
      "disabled": "No"
    }]
  }]
];
export const accountdetailpolicy = [
  [{
    "accordian": true,
    "emptycell": true,
    "isActionColumn": true,
    "compoundx": false,
    "label": "",
    "value": "",
    "fieldName": "Id",
    "disabled": "No"
  }, {
    "compoundx": true,
    "label": "Member Id",
    "compoundvalue": [{
      "text": true,
      "label": "ID",
      "value": "10",
      "fieldName": "Name",
      "disabled": "No"
    }, {
      "text": true,
      "label": "Medicare ID",
      "fieldName": "",
      "value": "11",
      "disabled": "No"
    }, {
      "icon": true,
      "label": "",
      "fieldName": "Member_Coverage_Status__c",
      "value": "11",
      "disabled": "No"
    }]
  }, {
    "boolean": true,
    "isCheckbox": true,
    "isActionColumn": false,
    "compoundx": false,
    "label": "Legacy Delete",
    "value": "",
    "fieldName": "ETL_Record_Deleted__c",
    "disabled": "No"
  }, {
    "compoundx": false,
    "label": "Plan Name",
    "text": true,
    "value": "10",
    "fieldName": "iab_description__c",
    "disabled": "No"

  },
  {
    "compoundx": true,
    "label": "Product",
    "compoundvalue": [{
      "text": true,
      "label": "Product",
      "value": "",
      "fieldName": "Product__c",
      "disabled": "No"
    }, {
      "text": true,
      "label": "Type",
      "fieldName": "Product_Type__c",
      "value": "",
      "disabled": "No"
    }]
  }, {
    "compoundx": true,
    "label": "Effective / End Date",
    "compoundvalue": [{
      "text": true,
      "label": "Effective",
      "value": "10",
      "fieldName": "EffectiveFrom",
      "disabled": "No"
    }, {
      "text": true,
      "label": "End",
      "fieldName": "EffectiveTo",
      "value": "11",
      "disabled": "No"
    }]
  }
  ]];

export const casehistory = [
  [
    {
      "isActionColumn": true,
      "isCheckbox": true,
      "compoundx": false,
      "label": "Link",
      "value": "",
      "fieldName": "",
      "disabled": "No"
    },
    {
      "compoundx": true,
      "label": "Case Information",
      "value": "Mem",
      "compoundvalue": [
        {
          "hidden": true,
          "Id": true,
          "label": "Id",
          "fieldName": "Id",
          "value": "",
          "disabled": "No"
        },
        {
          "link": true,
          "linkwithtooltip": true,
          "label": "Case No",
          "fieldName": "sCaseNum",
          "value": "11",
          "disabled": "No"
        },
        {
          "text": true,
          "label": "Type",
          "value": "10",
          "fieldName": "sType",
          "disabled": "No"
        }, {
          "text": true,
          "label": "Origin",
          "value": "10",
          "fieldName": "sOrigin",
          "disabled": "No"
        }, {
          "text": true,
          "label": "Priority",
          "value": "10",
          "fieldName": "sPriority",
          "disabled": "No"
        }]
    }, {
      "compoundx": true,
      "label": "Case Dates",
      "value": "Plan",
      "compoundvalue": [
        {
          "text": true,
          "label": "Status",
          "fieldName": "sStatus",
          "value": "11",
          "disabled": "No"
        },
        {
          "text": true,
          "label": "Date Opened",
          "value": "10",
          "fieldName": "sCreatedDate",
          "disabled": "No",
          "customFunc": "CASE_CREATION"
        }, {
          "text": true,
          "label": "Date Closed",
          "value": "10",
          "fieldName": "sClosedDate",
          "disabled": "No"
        }, {
          "text": true,
          "label": "Follow-Up Date",
          "value": "10",
          "fieldName": "sFollowUpDate",
          "disabled": "No"
        }]
    }, {
      "compoundx": true,
      "label": "Classification & Intent",
      "value": "Pend",
      "compoundvalue": [
        {
          "text": true,
          "label": "Classification",
          "fieldName": "sClassification",
          "value": "11",
          "disabled": "No"
        },
        {
          "text": true,
          "label": "Intent",
          "value": "10",
          "fieldName": "sIntent",
          "disabled": "No"
        }, {
          "text": true,
          "label": "DCN Present",
          "value": "10",
          "fieldName": "sDCN",
          "disabled": "No"
        }]
    },
    {
      "compoundx": true,
      "label": "Interaction",
      "value": "App",
      "compoundvalue": [
        {
          "text": true,
          "label": "With",
          "fieldName": "sInteractingWith",
          "value": "11",
          "disabled": "No"
        },
        {
          "text": true,
          "label": "With Type",
          "value": "10",
          "fieldName": "sInteractingWithType",
          "disabled": "No"
        },
        {
          "text": true,
          "label": "About",
          "value": "10",
          "fieldName": "sInteractingAbout",
          "disabled": "No"
        }]
    }, {
      "compoundx": true,
      "label": "Case Contacts",
      "value": "App",
      "compoundvalue": [
        {
          "text": true,
          "label": "Owner Queue",
          "fieldName": "sOwnerQueue",
          "value": "11",
          "disabled": "No"
        },
        {
          "text": true,
          "label": "Created By Queue",
          "value": "10",
          "fieldName": "sCreatedByQueue",
          "disabled": "No"
        },
        {
          "text": true,
          "label": "Last Modified by Queue",
          "value": "10",
          "fieldName": "sLastModifiedByQueue",
          "disabled": "No"
        }]
    }
  ]];

export const casehistoryaccordian = [
  [
    {
      "accordian": true,
      "value": "",
      "compoundx": false,
      "fieldName": "Id"
    },
    {
      "compoundx": true,
      "label": "Case Information",
      "value": "Mem",
      "compoundvalue": [
        {
          "text": true,
          "label": "Case No",
          "fieldName": "sCaseNum",
          "value": "11",
          "disabled": "No"
        },
        {
          "text": true,
          "label": "Product",
          "value": "10",
          "fieldName": "sProduct",
          "disabled": "No"
        }, {
          "text": true,
          "label": "Origin",
          "value": "10",
          "fieldName": "sOrigin",
          "disabled": "No"
        }, {
          "text": true,
          "label": "Priority",
          "value": "10",
          "fieldName": "sPriority",
          "disabled": "No"
        }]
    }, {
      "compoundx": true,
      "label": "Case Dates & Type",
      "value": "Plan",
      "compoundvalue": [
        {
          "text": true,
          "label": "Status",
          "fieldName": "sStatus",
          "value": "11",
          "disabled": "No"
        },
        {
          "text": true,
          "label": "Date Opened",
          "value": "10",
          "fieldName": "sCreatedDate",
          "disabled": "No"
        }, {
          "text": true,
          "label": "Date Closed",
          "value": "10",
          "fieldName": "sClosedDate",
          "disabled": "No"
        }, {
          "text": true,
          "label": "Type",
          "value": "10",
          "fieldName": "sType",
          "disabled": "No"
        }]
    }, {
      "compoundx": true,
      "label": "Classification & Intent",
      "value": "Pend",
      "compoundvalue": [
        {
          "text": true,
          "label": "Classification",
          "fieldName": "sClassification",
          "value": "11",
          "disabled": "No"
        },
        {
          "text": true,
          "label": "Intent",
          "value": "10",
          "fieldName": "sIntent",
          "disabled": "No"
        }, {
          "text": true,
          "label": "DCN Present",
          "value": "10",
          "fieldName": "sDCN",
          "disabled": "No"
        }]
    },
    {
      "compoundx": true,
      "label": "Interaction",
      "value": "App",
      "compoundvalue": [
        {
          "text": true,
          "label": "With",
          "fieldName": "sInteractingWith",
          "value": "11",
          "disabled": "No"
        },
        {
          "text": true,
          "label": "With Type",
          "value": "10",
          "fieldName": "sInteractingWithType",
          "disabled": "No"
        },
        {
          "text": true,
          "label": "About",
          "value": "10",
          "fieldName": "sInteractingAbout",
          "disabled": "No"
        }]
    }, {
      "compoundx": true,
      "label": "Case Contacts",
      "value": "App",
      "compoundvalue": [
        {
          "text": true,
          "label": "Owner Queue",
          "fieldName": "sInteractingWith",
          "value": "11",
          "disabled": "No"
        },
        {
          "text": true,
          "label": "Created By Queue",
          "value": "10",
          "fieldName": "sCreatedByQueue",
          "disabled": "No"
        },
        {
          "text": true,
          "label": "Last Modified by Queue",
          "value": "10",
          "fieldName": "sInteractingAbout",
          "disabled": "No"
        }]
    }
  ]];

export const policies = [
  [{
    "compoundx": true,
    "label": "Member ID",
    "compoundvalue": [{
      "text": true,
      "label": "ID",
      "value": "10",
      "fieldName": "Name",
      "disabled": "No"
    }, {
      "text": true,
      "label": "Medicare ID",
      "fieldName": "sMedicareValue",
      "value": "11",
      "disabled": "No"
    }, {
      "icon": true,
      "label": "",
      "iconValue": "",
      "fieldName": "Member_Coverage_Status__c",
      "value": "11",
      "disabled": "No"
    }]
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
      "label": "Type",
      "fieldName": "Product_Type__c",
      "value": "11",
      "disabled": "No"
    }]
  }, {
    "text": true,
    "compoundx": false,
    "label": "Plans Name",
    "value": "",
    "fieldName": "sPlanName",
    "disabled": "No"

  }, {
    "compoundx": true,
    "label": "Effective / End Date",
    "compoundvalue": [{
      "text": true,
      "label": "Effective",
      "value": "10",
      "fieldName": "EffectiveFrom",
      "disabled": "No"
    }, {
      "text": true,
      "label": "End",
      "fieldName": "EffectiveTo",
      "value": "11",
      "disabled": "No"
    }]
  }, {
    "compoundx": true,
    "label": "Group",
    "compoundvalue": [{
      "text": true,
      "label": "Name",
      "value": "10",
      "fieldName": "Display_Group_Name__c",
      "disabled": "No"
    }, {
      "text": true,
      "label": "Number",
      "fieldName": "GroupNumber",
      "value": "11",
      "disabled": "No"
    }]
  }, {
    "text": true,
    "label": "Platform",
    "fieldName": "Policy_Platform__c",
    "value": "11",
    "disabled": "No"
  }
  ]];

export const grouppolicies = [
  [{
    "radio": true,
    "isActionColumn": true,
    "value": "",
    "compoundx": false,
    "interaction": 'GROUP_POLICY',
    "fieldName": "Id"
  }, {
    "compoundx": false,
    "text": true,
    "label": "Name",
    "fieldName": "Name",
    "value": "11",
    "disabled": "No"

  }, {
    "compoundx": true,
    "label": "Product",
    "compoundvalue": [{
      "text": true,
      "label": "Name",
      "value": "10",
      "fieldName": "Product__r.Name",
      "disabled": "No"
    }, {
      "text": true,
      "label": "Type",
      "fieldName": "Product_Type__c",
      "value": "11",
      "disabled": "No"
    }]
  },
  {
    "compoundx": true,
    "label": "Effective / End Date",
    "compoundvalue": [{
      "text": true,
      "label": "Effective",
      "value": "10",
      "fieldName": "EffectiveFrom",
      "disabled": "No"
    }, {
      "text": true,
      "label": "End",
      "fieldName": "EffectiveTo",
      "value": "11",
      "disabled": "No"
    }]
  },
  {
    "text": true,
    "label": "Platform",
    "fieldName": "Platform__c",
    "value": "11",
    "disabled": "No"
  }, {
    "text": true,
    "label": "Issue State",
    "fieldName": "Issue_State__c",
    "value": "11",
    "disabled": "No"
  }

  ]];

  export const interactions = [
    [{
      "label": "Action",
      "compoundx": true,
      "customCss": "results-table-cell-action",
      "compoundvalue": [{
        "link": true,
        "icon": false,
        "hidden": true,
        "fieldName": "Action",
        "value": "",
        "disabled": "No",
        "actionName": 'INTERACTION_EDIT'
      }, {
        "hidden": true,
        "Id": true,
        "label": "Id",
        "fieldName": "Id",
        "value": "",
        "disabled": "No"
      }]
    },
    {
      "label": "Interaction Number",
      "compoundx": true,
      "compoundvalue": [{
        "link": true,
        "icon": false,
        "hidden": true,
        "fieldName": "Name",
        "value": "",
        "disabled": "No"
      }, {
        "hidden": true,
        "Id": true,
        "label": "Id",
        "fieldName": "Id",
        "value": "",
        "disabled": "No"
      }]
    },
    {
      "text": true,
      "label": "Interaction Origin",
      "compoundx": false,
      "value": "",
      "fieldName": "interactionOrigin"
    },
    {
      "text": true,
      "label": "Interacting With Type",
      "compoundx": false,
      "value": "",
      "fieldName": "interactingWithtype"
    },
    {
      "text": true,
      "label": "Last Modified Date",
      "compoundx": false,
      "value": "",
      "fieldName": "LastModifiedDate"
    },
    {
      "text": true,
      "label": "Created By",
      "compoundx": false,
      "value": "",
      "fieldName": "CreatedByNameDate"
    },
    {
      "text": true,
      "label": "Created By Queue",
      "compoundx": false,
      "value": "",
      "fieldName": "createdByQueue"
    },
    {
      "isCheckbox": true,
      "isActionColumn": false,
      "label": "Authenticated",
      "isHiddenCol": true,
      "compoundx": false,
      "value": "",
      "fieldName": "Authenticated__c",
      "isLink": false,
      "disabled": true,
      "bFullWidth": true
    }
    ]];

export const groupAccountPolicies = [
  [{
    "compoundx": true,
    "label": "Group Plan",
    "compoundvalue": [{
      "link": true,
      "label": "Name",
      "value": "10",
      "fieldName": "Name",
      "navToItem":'',
      "actionName":'ACC_PLAN_NAME',
      "disabled": "No"
    }, {
      "icon": true,
      "label": "",
      "fieldName": "Plan_Status__c",
      "value": "11",
      "disabled": "No"
    }]
  }, {
    "compoundx": true,
    "label": "Product",
    "compoundvalue": [{
      "text": true,
      "label": "Product",
      "value": "",
      "fieldName": "ProductName",
      "disabled": "No"
    }, {
      "text": true,
      "label": "Type",
      "fieldName": "Major_LOB__c",
      "value": "",
      "disabled": "No"
    }, {
      "text": true,
      "label": "Description",
      "fieldName": "Product_Description__c",
      "value": "",
      "disabled": "No"
    }, {
      "text": true,
      "label": "Benefit Package ID",
      "fieldName": "Benefit_Coverage__c",
      "value": "",
      "disabled": "No"
    }]
  }, {
    "compoundx": true,
    "label": "Effective / End Date",
    "compoundvalue": [{
      "text": true,
      "label": "Effective",
      "value": "10",
      "fieldName": "EffectiveFrom",
      "disabled": "No"
    }, {
      "text": true,
      "label": "End",
      "fieldName": "EffectiveTo",
      "value": "11",
      "disabled": "No"
    }]
  }, {
    "compoundx": true,
    "label": "Platform",
    "compoundvalue": [{
      "text": true,
      "label": "Platform",
      "value": "10",
      "fieldName": "Platform__c",
      "disabled": "No"
    }, {
      "text": true,
      "label": "Segment Indicator",
      "fieldName": "Business_Segment__c",
      "value": "11",
      "disabled": "No"
    }, {
      "text": true,
      "label": "ASO",
      "fieldName": "ASO__c",
      "value": "11",
      "disabled": "No"
    }]
  }, {
    "text": true,
    "label": "Issue State",
    "fieldName": "Issue_State__c",
    "value": "11",
    "disabled": "No"
  }
  ]];

export const providerResults = [
  [{
    "radio": true,
    "isActionColumn": true,
    "value": "",
    "compoundx": false,
    "fieldName": "sExtID",
    "actionName": 'FIREINTERACTIONS'
  },
  {
    "compoundx": true,
    "iconcompoundx": false,
    "value": "3",
    "disabled": "No",
    "label": "Provider Details",
    "compoundvalue": [{
      "Id": true,
      "link": false,
      "icon": false,
      "hidden": true,
      "label": "Id",
      "fieldName": "sMemberId",
      "value": "11",
      "disabled": "No"
    }, {
      "link": true,
      "label": "Name",
      "fieldName": "sDBA",
      "value": "11",
      "disabled": "No"
    }, {
      "label": "NPI",
      "fieldName": "sNPI",
      "value": "11",
      "disabled": "No"
    }],
  },
  {
    "isViewAll": true,
    "label": "Tax ID",
    "value": "",
    "compoundx": false,
    "fieldName": "sTaxID"
  },
  {
    "compoundx": true,
    "iconcompoundx": false,
    "value": "3",
    "disabled": "No",
    "label": "Demographics",
    "compoundvalue": [{
      "text": true,
      "label": "State",
      "fieldName": "sState",
      "value": "11",
      "disabled": "No"
    },
    {
      "text": true,
      "label": "Zip Code",
      "fieldName": "sPostalCode",
      "value": "11",
      "disabled": "No"
    }],
  },
  {
    "text": true,
    "label": "Speciality",
    "value": "",
    "compoundx": false,
    "fieldName": "sSpeciality"
  },
  {
    "icon": true,
    "label": "Record Type",
    "value": "Provider",
    "compoundx": false,
    "fieldName": "sPend"
  },
  {
    "button": true,
    "value": "3",
    "disabled": "No",
    "label": "Select Interaction",
    "compoundx": false,
    "compoundvalue": [{
      "button": true,
      "buttonlabel": "With & About",
      "value": "method1",
      "event": "",
      "disabled": "No",
      "type_large": true,
      "type_small": false
    },
    {
      "button": true,
      "buttonlabel": "With",
      "value": "method2",
      "event": "",
      "disabled": "No",
      "type_small": true,
      "type_large": false
    },
    {
      "button": true,
      "buttonlabel": "About",
      "value": "method3",
      "event": "",
      "disabled": "No",
      "type_small": true,
      "type_large": false
    }]
  }
  ]];

export const providerInteractions = [
  [{
    "label": "ID",
    "fieldName": "Name",
    "value": "",
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
      "label": "ID",
      "fieldName": "Name",
      "value": "",
      "disabled": "No",
    }]
  },
  {
    "text": true,
    "label": "Interacting With",
    "value": "",
    "compoundx": false,
    "fieldName": "interactionWith"
  },
  {
    "text": true,
    "label": "Interacting About",
    "value": "",
    "compoundx": false,
    "fieldName": "interactionAbout"
  },
  {
    "text": true,
    "label": "Modified Date",
    "value": "",
    "compoundx": false,
    "fieldName": "modifiedDate"
  }]
];

export const providerOpenCases = [
  [{
    "label": "Case ID",
    "fieldName": "urlCaseId",
    "value": "",
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
      "label": "Case ID",
      "fieldName": "urlCaseId",
      "value": "",
      "disabled": "No",
    }]
  },
  {
    "text": true,
    "label": "C&I",
    "value": "",
    "compoundx": true,
    "compoundvalue": [{
      "link": false,
      "label": "Classification",
      "fieldName": "caseClassification",
      "value": "11",
      "disabled": "No"
    }, {
      "link": false,
      "label": "Intent",
      "fieldName": "caseIntention",
      "value": "11",
      "disabled": "No"
    }],
  },
  {
    "text": true,
    "label": "Date Opened/Closed",
    "value": "",
    "compoundx": false,
    "fieldName": "openCaseDate"
  }, {
    "text": true,
    "label": "Status",
    "value": "",
    "compoundx": false,
    "fieldName": "caseStatus"
  }]
];
export const consumerIds = [
  [{
    "text": true,
    "label": "Consumer ID",
    "value": "",
    "compoundx": false,
    "fieldName": "Consumer_ID__c"
  },
  {
    "text": true,
    "label": "Type",
    "value": "",
    "compoundx": false,
    "fieldName": "ID_Type__c"
  }]
];
export const agencyResults = [
  [{
    "radio": true,
    "isActionColumn": true,
    "value": false,
    "compoundx": false,
    "fieldName": "sAgencyExtId",
    "actionName": 'FIREINTERACTIONS'

  },
  {
    "label": "Business Name/First Name",
    "compoundx": true,
    "compoundvalue": [{
      "Id": true,
      "link": false,
      "icon": false,
      "hidden": true,
      "label": "Id",
      "fieldName": "sAgencyExtId",
      "value": "11",
      "disabled": "No"
    },
    {
      "link": true,
      "icon": false,
      "hidden": true,
      "label": "",
      "fieldName": "strFirst",
      "value": "11",
      "disabled": "No",
      "actionName": 'AGENCY_SEARCH_FNLN'
    }]
  },
  {
    "label": "Last Name",
    "compoundx": true,
    "compoundvalue": [{
      "Id": true,
      "link": false,
      "icon": false,
      "hidden": true,
      "label": "Id",
      "fieldName": "sAgencyExtId",
      "value": "11",
      "disabled": "No"
    },
    {
      "link": true,
      "icon": false,
      "hidden": true,
      "label": "",
      "fieldName": "urlLastName",
      "value": "11",
      "disabled": "No",
      "actionName": 'AGENCY_SEARCH_FNLN'
    }]
  },
  {
    "compoundx": true,
    "iconcompoundx": false,
    "value": "",
    "disabled": "No",
    "label": "ID",
    "compoundvalue": [{
      "text": true,
      "label": "Agent ID",
      "fieldName": "strAgentId",
      "value": ""
    },
    {
      "text": true,
      "label": "Tax ID",
      "fieldName": "strTaxId",
      "value": "",
      "disabled": "No"
    }],
  },
  {
    "compoundx": true,
    "iconcompoundx": false,
    "value": "",
    "disabled": "No",
    "label": "Demographics",
    "compoundvalue": [{
      "text": true,
      "label": "Street",
      "fieldName": "strStreet",
      "value": "",
      "disabled": "No"
    },
    {
      "text": true,
      "label": "State",
      "fieldName": "strState",
      "value": "",
      "disabled": "No"
    }, {
      "text": true,
      "label": "Zip Code",
      "fieldName": "strZipCode",
      "value": "",
      "disabled": "No"
    }],
  },
  {
    "icon": true,
    "label": "Record Type",
    "value": "Agent/Broker",
    "compoundx": false,
    "fieldName": "recordType"
  },
  {
    "button": true,
    "value": "3",
    "disabled": "No",
    "label": "Select Interaction",
    "compoundx": false,
    "compoundvalue": [{
      "button": true,
      "buttonlabel": "With & About",
      "value": "method1",
      "event": "aInteractingWithnAbout",
      "disabled": "No",
      "type_large": true,
      "type_small": false,
      "rowData": {}
    },
    {
      "button": true,
      "buttonlabel": "With",
      "value": "method2",
      "event": "aInteractingWith",
      "disabled": "No",
      "type_small": true,
      "type_large": false,
      "rowData": {}
    },
    {
      "button": true,
      "buttonlabel": "About",
      "value": "method3",
      "event": "aInteractingAbout",
      "disabled": "No",
      "type_small": true,
      "type_large": false,
      "rowData": {}
    }]
  }
  ]];


export const chattranscript = [
  [{
    "label": "Chat Transcript Name",
    "compoundx": true,
    "compoundvalue": [{
      "link": true,
      "icon": false,
      "hidden": true,
      "fieldName": "Name",
      "value": "",
      "disabled": "No",
      "navToItem": ""
    },
	 {
      "hidden": true,
      "Id": true,
      "label": "ChatId",
      "fieldName": "ChatId",
      "value": "",
      "disabled": "No"
    }]
  },
  {
    "label": "Case",
    "compoundx": true,
    "compoundvalue": [{
      "link": true,
      "icon": false,
      "hidden": true,
      "fieldName": "CaseNumber",
      "value": "",
      "disabled": "No",
      "navToItem": ""
    },
	{
      "hidden": true,
      "Id": true,
      "label": "CaseId",
      "fieldName": "CaseId",
      "value": "",
      "disabled": "No"
    }]
  },
  {
    "text": true,
    "label": "Owner",
    "compoundx": false,
    "value": "",
    "fieldName": "OwnerName"
  },
  {
    "text": true,
    "label": "Start Time",
    "compoundx": false,
    "value": "",
    "fieldName": "StartTime"
  },
  {
    "text": true,
    "label": "End Time",
    "compoundx": false,
    "value": "",
    "fieldName": "EndTime"
  },
  {
    "text": true,
    "label": "Status",
    "compoundx": false,
    "value": "",
    "fieldName": "Status"
  }
  ]];

export const mtvremarks = [
  [
    {
      "text": true,
      "label": "Entry Type",
      "compoundx": false,
      "value": "",
      "fieldName": "sIdType"
    },
    {
      "compoundx": true,
      "label": "Identifier/Subidentifier",
      "compoundvalue": [{
        "text": true,
        "label": "Identifier",
        "value": "10",
        "fieldName": "sIdentifierId",
        "disabled": "No"
      },{
        "text": true,
        "label": "Subidentifier",
        "fieldName": "sSubIdentifier",
        "value": "11",
        "disabled": "No"
      }]
    },
    {
      "compoundx": true,
      "label": "Type/Category",
      "compoundvalue": [{
        "text": true,
        "label": "Type",
        "value": "10",
        "fieldName": "sRemarkCode",
        "disabled": "No"
      },{
        "text": true,
        "label": "Category",
        "fieldName": "sRemarkCategory",
        "value": "11",
        "disabled": "No"
      }]
    },
    {
      "text": true,
      "label": "Create Date",
      "compoundx": false,
      "value": "",
      "fieldName": "sCreatedDate"
    },
    {
      "text": true,
      "label": "Text",
      "compoundx": false,
      "value": "",
      "fieldName": "sRemarkText"
    }
  ]];
  export const accumsModel = [
    [
      {
        "text": true,
        "label": "Accumulator",
        "compoundx": false,
        "value": "",
        "fieldName": "sAccumulator"
      },
      {
        "text": true,
        "label": "From",
        "compoundx": false,
        "value": "",
        "fieldName": "sFrom"
      },
      {
        "text": true,
        "label": "To",
        "compoundx": false,
        "value": "",
        "fieldName": "sTo"
      },
      {
        "text": true,
        "label": "Limit",
        "compoundx": false,
        "value": "",
        "fieldName": "sLimit"
      }, {
        "text": true,
        "label": "Used",
        "compoundx": false,
        "value": "",
        "fieldName": "sUsed"
      },{
        "text": true,
        "label": "Available",
        "compoundx": false,
        "value": "",
        "fieldName": "sAvailable"
      }
    ]];