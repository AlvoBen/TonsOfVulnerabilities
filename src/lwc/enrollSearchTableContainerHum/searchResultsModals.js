/*******************************************************************************************************************************
LWC Name        : searchResultsModals.js
Function        : Enrollment Search Results table data model

* Modification Log:
* Version         Developer Name                  	Date                      Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*   1.0            Mohan Kumar N                  	11/21/2020                Original Version 
*   2.0            Supriya Shastri                	05/07/2021                US-2199980 Field name changes
*   3.0            Kajal Namdev/Vardhman Jain     	04/29/2022                US-3334446 Field label changes First/Last
*   4.0            Pavan Kumar M                 	07/14/2022                US-3334298 
*   5.0            Muthukumar                     	07/29/2022                US-3255798 Field action name changes
*   6.0            Bhakti Vispute					10/17/2022				  User Story 3334298: T1PRJ0170850 - Account Management - Unknown Records - Display Search Results on Enrollment Search Page
*   7.0            Visweswararao Jayavarapu         30/08/2022                User Story 3481400: Enrollment Search / H1 Market Place Tab - Detail Page Creation and Field Population
*   8.0            Visweswararao Jayavarapu         30/08/2022                User Story 3483233: T1PRJ0170850- MF 20035- Lightning - - Enrollment Search / H1 Market Place - Applications Misc Tab fields population
*   9.0            Visweswararao Jayavarapu         30/08/2022                User Story 3481578: T1PRJ0170850- MF 20035- Lightning - - Enrollment Search / H1 Market Place - Billing Information Tab fields population
*   10.0           Visweswararao Jayavarapu         30/08/2022                User Story 3482496: T1PRJ0170850- MF 20035- Lightning - - Enrollment Search / H1 Market Place - Benefit & AgentTab fields population
*   11.0           Nilanjana Sanyal                 02/17/2023                User Story 4003126: FirstName and Lastname link implementation in Enrollment Search Tab 
                                                                                                 (for Track Enrollment Status, CBIS, H1 marketplace, Customer Interface/Metavance and Automated Enrollment History)
																								   Plus regression defect fix for Application Search
*   12.0           Visweswararao Jayavarapu        30/08/2022                 User Story 4415779: T1PRJ0865978 - MF24875 - Consumer/Application ID on Search Enrollment Hyperlink to FastApp
******************************************************************************************************************************************************************************************************************************************************************************/

import { hcConstants } from 'c/crmUtilityHum';

const { TES, CBIS, CIM, MARKETSEARCH, AUTOENROLL, TRR, APPSEARCH } = hcConstants;
const automatedEnroll = [
  [{
    "compoundx": true,
    "label": "Member Information",
    "compoundvalue": [{
      "text": true,
      "label": "First Name",
      "value": "",
      "fieldName": "sFirstName",
       "actionName" :"ENROLLMENT_SEARCH_FNLN",
      "disabled": "No",
	  "link": true,
      "Id": false
    }, {
      "text": true,
       "label": "Last Name",
      "fieldName": "sLastName",
      "value": "",
      "actionName" :"ENROLLMENT_SEARCH_FNLN",
      "disabled": "No",
	  "link": true,
      "Id": false
    }, 
	{
      "hidden": true,
      "Id": true,
      "label": "Id",
      "fieldName": "sExternalID",
      "value": "",
      "disabled": "No"
    },
	{
      "text": true,
      "label": "Birthdate",
      "fieldName": "sDOB",
      "value": "",
      "disabled": "No"
    }]
  }, {
    "compoundx": true,
    "label": "Plan Information",
    "compoundvalue": [{
      "text": true,
      "label": "Platform",
      "value": "",
      "fieldName": "sPlatform",
      "disabled": "No"
    }, {
      "text": true,
      "label": "Product",
      "fieldName": "sProduct",
      "value": "",
      "disabled": "No"
    }, {
      "text": true,
      "label": "Group ID",
      "fieldName": "sGroupId",
      "value": "",
      "disabled": "No"
    },
    {
      "text": true,
      "label": "LOB",
      "fieldName": "sLOB",
      "value": "",
      "disabled": "No"
    }],
  }, {
    "compoundx": true,
    "label": "Pend Information",
    "compoundvalue": [{
      "text": true,
      "label": "Pend",
      "value": "",
      "fieldName": "",
      "disabled": "No"
    }, {
      "text": true,
      "label": "Pend Key",
      "fieldName": "sPendKey",
      "value": "",
      "disabled": "No"
    }, {
      "text": true,
      "label": "Date & Time",
      "fieldName": "",
      "value": "",
      "disabled": "No"
    }]
  }, {
    "compoundx": true,
    "label": "Application Date",
    "compoundvalue": [{
      "text": true,
      "label": "Effective Date",
      "value": "",
      "fieldName": "sEffectiveDate",
      "disabled": "No"
    }, {
      "text": true,
      "label": "End Date",
      "fieldName": "sEndDate",
      "value": "",
      "disabled": "No"
    }]
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
      "disabled": "No",
      "type_large": true,
      "type_small": false,
      "event": "eInteractingWithnAbout",
      "rowData": {}
    }, {
      "button": true,
      "buttonlabel": "With",
      "value": "method2",
      "disabled": "No",
      "type_small": true,
      "type_large": false,
      "event": "eInteractingWith",
      "rowData": {}
    }, {
      "button": true,
      "buttonlabel": "About",
      "value": "method3",
      "disabled": "No",
      "type_small": true,
      "type_large": false,
      "event": "eInteractingAbout",
      "rowData": {}
    }]
  }
  ]];

const customerInterface = [
  [{
    "compoundx": true,
    "label": "Member Information",
    "compoundvalue": [{
      "text": true,
      "label": "First Name",
      "value": "",
      "fieldName": "sFirstName",
      "actionName" :"ENROLLMENT_SEARCH_FNLN",
      "disabled": "No",
	  "link": true,
      "Id": false
    }, {
      "text": true,
       "label": "Last Name",
      "fieldName": "sLastName",
      "actionName" :"ENROLLMENT_SEARCH_FNLN",
      "value": "",
      "disabled": "No",
	  "link": true,
      "Id": false
    },
	{
      "hidden": true,
      "Id": true,
      "label": "Id",
      "fieldName": "sExternalID",
      "value": "",
      "disabled": "No"
    },
	{
      "text": true,
      "label": "Birthdate",
      "fieldName": "sDOB",
      "value": "",
      "disabled": "No"
    }]
  }, {
    "compoundx": true,
    "label": "Plan Information",
    "compoundvalue": [{
      "text": true,
      "label": "Platform",
      "value": "",
      "fieldName": "sPlatform",
      "disabled": "No"
    }, {
      "text": true,
      "label": "Product",
      "fieldName": "sProduct",
      "value": "",
      "disabled": "No"
    }, {
      "text": true,
      "label": "LOB",
      "fieldName": "sLOB",
      "value": "",
      "disabled": "No"
    }],
  }, {
    "compoundx": true,
    "label": "Application Date",
    "compoundvalue": [{
      "text": true,
      "label": "Effective Date",
      "value": "",
      "fieldName": "sEffectiveDate",
      "disabled": "No"
    }, {
      "text": true,
      "label": "End Date",
      "fieldName": "sEndDate",
      "value": "",
      "disabled": "No"
    }]
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
      "disabled": "No",
      "type_large": true,
      "type_small": false,
      "event": "eInteractingWithnAbout",
      "rowData": {}
    }, {
      "button": true,
      "buttonlabel": "With",
      "value": "method2",
      "disabled": "No",
      "type_small": true,
      "type_large": false,
      "event": "eInteractingWith",
      "rowData": {}
    }, {
      "button": true,
      "buttonlabel": "About",
      "value": "method3",
      "disabled": "No",
      "type_small": true,
      "type_large": false,
      "event": "eInteractingAbout",
      "rowData": {}   
    }]
  }
  ]];

const marketPlaceModal = [
  [{
    "compoundx": true,
    "label": "Member Information",
    "compoundvalue": [{
      "text": true,
      "label": "First Name",
      "value": "",
      "fieldName": "sFirstName",
      "actionName" :"ENROLLMENT_SEARCH_FNLN",
      "disabled": "No",
	  "link": true,
      "Id": false
    }, {
      "text": true,
      "label": "Last Name",
      "fieldName": "sLastName",
      "actionName" :"ENROLLMENT_SEARCH_FNLN",
      "value": "",
      "disabled": "No",
	  "link": true,
      "Id": false
    }, 
	{
    "hidden": true,
    "Id": true,
    "label": "Id",
    "fieldName": "sExternalID",
    "value": "",
    "disabled": "No"
  },
	{
      "text": true,
      "label": "Birthdate",
      "fieldName": "sDOB",
      "value": "",
      "disabled": "No"
    }]
  }, {
    "compoundx": true,
    "label": "Member Location",
    "compoundvalue": [{
      "text": true,
      "label": "State",
      "value": "",
      "fieldName": "sState",
      "disabled": "No"
    }, {
      "text": true,
      "label": "Zip Code",
      "fieldName": "sZip",
      "value": "",
      "disabled": "No"
    }],
  }, {
    "compoundx": true,
    "label": "Exchange ID",
    "compoundvalue": [{
      "link": true,
      "label": "Exchange ID",
      "value": "",
      "fieldName": "sExchangeID",
      "disabled": "No",
	  "actionName": "openH1AppDetailPage"
    }]
  }, {
    "compoundx": true,
    "label": "Application Date",
    "compoundvalue": [{
      "text": true,
      "label": "Effective Date",
      "value": "",
      "fieldName": "sEffectiveDate",
      "disabled": "No"
    }, {
      "text": true,
      "label": "End Date",
      "fieldName": "sEndDate",
      "value": "",
      "disabled": "No"
    }]
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
      "disabled": "No",
      "type_large": true,
      "type_small": false,
      "event": "eInteractingWithnAbout",
      "rowData": {}
    }, {
      "button": true,
      "buttonlabel": "With",
      "value": "method2",
      "disabled": "No",
      "type_small": true,
      "type_large": false,
      "event": "eInteractingWith",
      "rowData": {}
    }, {
      "button": true,
      "buttonlabel": "About",
      "value": "method3",
      "disabled": "No",
      "type_small": true,
      "type_large": false,
      "event": "eInteractingAbout",
      "rowData": {}
    }]
  }
  ]];
const trackEnrolModal = [
  [{
    "compoundx": true,
    "label": "Member Information",
    "compoundvalue": [{
      "text": true,
      "label": "First Name",
      "value": "",
      "fieldName": "fname",
      "actionName" :"ENROLLMENT_SEARCH_FNLN",
      "disabled": "No",
	  "link": true,
      "Id": false
    }, {
      "text": true,
      "label": "Last Name",
      "fieldName": "lname",
      "actionName" :"ENROLLMENT_SEARCH_FNLN",
      "value": "",
      "disabled": "No",
	  "link": true,
      "Id": false
    },
	{
      "hidden": true,
      "Id": true,
      "label": "Id",
      "fieldName": "sExternalID",
      "value": "",
      "disabled": "No"
    },
	{
      "text": true,
      "label": "Birthdate",
      "fieldName": "dob",
      "value": "",
      "disabled": "No"
    }]
  }, {
    "compoundx": true,
    "label": "Plan Information",
    "compoundvalue": [{
      "text": true,
      "label": "Platform",
      "value": "",
      "fieldName": "platform",
      "disabled": "No"
    }, {
      "text": true,
      "label": "Product",
      "fieldName": "product",
      "value": "",
      "disabled": "No"
    }, {
      "text": true,
      "label": "Group ID",
      "fieldName": "groupId",
      "value": "",
      "disabled": "No"
    }, {
      "text": true,
      "label": "LOB",
      "fieldName": "lob",
      "value": "",
      "disabled": "No"
    }],
  }, {
    "compoundx": true,
    "label": "Pend Information",
    "compoundvalue": [{
      "text": true,
      "label": "Pend",
      "value": "",
      "fieldName": "pend",
      "disabled": "No"
    }, {
      "text": true,
      "label": "Pend Key",
      "fieldName": "pendKey",
      "value": "",
      "disabled": "No"
    }, {
      "text": true,
      "label": "Date & Time",
      "fieldName": "datetimepend",
      "value": "",
      "disabled": "No"
    }]
  }, {
    "compoundx": true,
    "label": "Application Dates",
    "compoundvalue": [{
      "text": true,
      "label": "Effective Date",
      "value": "",
      "fieldName": "effectiveDate",
      "disabled": "No"
    }, {
      "text": true,
      "label": "End Date",
      "fieldName": "endDate",
      "value": "",
      "disabled": "No"
    }]
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
      "disabled": "No",
      "type_large": true,
      "type_small": false,
      "event": "eInteractingWithnAbout",
      "rowData": {}
    }, {
      "button": true,
      "buttonlabel": "With",
      "value": "method2",
      "disabled": "No",
      "type_small": true,
      "type_large": false,
      "event": "eInteractingWith",
      "rowData": {}
    }, {
      "button": true,
      "buttonlabel": "About",
      "value": "method3",
      "disabled": "No",
      "type_small": true,
      "type_large": false,
      "event": "eInteractingAbout",
      "rowData": {}
    }
    ]
  }
  ]
];

const cbisModal = [
  [{
    "compoundx": true,
    "label": "Member Information",
    "compoundvalue": [
      {
        "text": true,
        "label": "First Name",
        "fieldName": "sFirstName",
        "value": "",
        "disabled": "No",
        "actionName" :"ENROLLMENT_SEARCH_FNLN",
		"link": true,
      "Id": false
      },
      {
        "text": true,
        "label": "Last Name",
        "value": "",
        "fieldName": "sLastName",
        "actionName" :"ENROLLMENT_SEARCH_FNLN",
        "disabled": "No",
		"link": true,
      "Id": false
      },
	  {
        "hidden": true,
        "Id": true,
        "label": "Id",
        "fieldName": "sExternalID",
        "value": "",
        "disabled": "No"
      },
	  {
        "text": true,
        "label": "Birthdate",
        "value": "",
        "fieldName": "sDOB",
        "disabled": "No"
      }]
  }, {
    "compoundx": true,
    "label": "Plan Information",
    "compoundvalue": [
      {
        "text": true,
        "label": "Platform",
        "fieldName": "sPlatform",
        "value": "",
        "disabled": "No"
      },
      {
        "text": true,
        "label": "Product",
        "value": "",
        "fieldName": "sProduct",
        "disabled": "No"
      }, {
        "text": true,
        "label": "Group ID",
        "value": "",
        "fieldName": "sGroupId",
        "disabled": "No"
      }, {
        "text": true,
        "label": "LOB",
        "value": "",
        "fieldName": "sLOB",
        "disabled": "No"
      }]
  }, {
    "compoundx": true,
    "label": "Pend Information",
    "compoundvalue": [
      {
        "text": true,
        "label": "Pend",
        "fieldName": "sPend",
        "value": "",
        "disabled": "No"
      },
      {
        "text": true,
        "label": "Pend Key",
        "value": "",
        "fieldName": "sPendKey",
        "disabled": "No"
      }, {
        "text": true,
        "label": "Date & Time",
        "value": "",
        "fieldName": "sDateTime",
        "disabled": "No"
      }]
  },
  {
    "compoundx": true,
    "label": "Application Dates",
    "compoundvalue": [
      {
        "text": true,
        "label": "Effective Date",
        "fieldName": "sEffectiveDate",
        "value": "",
        "disabled": "No"
      },
      {
        "text": true,
        "label": "End Date",
        "value": "",
        "fieldName": "sEndDate",
        "disabled": "No"
      }]
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
      "disabled": "No",
      "type_large": true,
      "type_small": false,
      "event": "eInteractingWithnAbout",
      "rowData": {}
    }, {
      "button": true,
      "buttonlabel": "With",
      "value": "method2",
      "disabled": "No",
      "type_small": true,
      "type_large": false,
      "event": "eInteractingWith",
      "rowData": {}
    }, {
      "button": true,
      "buttonlabel": "About",
      "value": "method3",
      "disabled": "No",
      "type_small": true,
      "type_large": false,
      "event": "eInteractingAbout",
      "rowData": {}
    }
    ]
  }
  ]];

const appSearchModal = [
  [
    {
      "compoundx": true,
      "label": "Member Information",
      "compoundvalue": [{
        "text": true,
        "label": "First Name",
        "value": "",
        "fieldName": "sFirstName",
        "disabled": "No",
		"link": false,
      "Id": false
      },
      {
        "text": true,
        "label": "Last Name",
        "fieldName": "sLastName",
        "value": "",
        "disabled": "No",
		"link": false,
      "Id": false
      },
      {
        "date": true,
        "label": "Birthdate",
        "fieldName": "sDOB",
        "value": "",
        "disabled": "No"
      },
      {
        "text": true,
        "label": "Medicare Claim Number",
        "fieldName": "sMedicareId",
        "value": "",
        "disabled": "No"
      }],
    },
    {
      "compoundx": true,
      "label": "Plan Information",
      "compoundvalue": [
        {
          "text": true,
          "label": "Contract",
          "fieldName": "sContract",
          "value": "",
          "disabled": "No"
        },
        {
          "text": true,
          "label": "PBP",
          "value": "",
          "fieldName": "sPBP",
          "disabled": "No"
        },
        {
          "text": true,
          "label": "Segment ID",
          "fieldName": "sSegmentNumber",
          "value": "",
          "disabled": "No"
        },
        {
          "text": true,
          "label": "Group ID",
          "fieldName": "sGroupId",
          "value": "",
          "disabled": "No"
        },
        {
          "text": true,
          "label": "BSN",
          "fieldName": "sBSN",
          "value": "",
          "disabled": "No"
        }],
    },
    {
      "compoundx": true,
      "label": "Application Information",
      "compoundvalue": [{
        "text": true,
        "label": "OEC Confirmation",
        "value": "",
        "fieldName": "sOECConfirmationId",
        "disabled": "No"
      },
      {
        "text": true,
        "label": "Channel",
        "fieldName": "sApplicationChannel",
        "value": "",
        "disabled": "No"
      },
      {
        "link": true,
        "label": "Details",
        "fieldName": "details",
        "value": "",
        "disabled": "No",
		"actionName": "openAppDetailPage"
      },
      {
        "link": true,
        "label": "Application ID",
        "fieldName": "application",
        "value": "",
        "disabled": "No",
        "actionName":"fastAppLink"
      },
      {
        "text": true,
        "label": "Web Unique ID",
        "fieldName": "sWebUniqueId",
        "value": "",
        "disabled": "No"
      }
      ],
    },
    {
      "compoundx": true,
      "label": "Application Dates",
      "compoundvalue": [{
        "date": true,
        "label": "Effective Date",
        "value": "",
        "fieldName": "sCovEffDt",
        "disabled": "No"
      },
      {
        "date": true,
        "label": "Received Date",
        "fieldName": "sReceivedDate",
        "value": "",
        "disabled": "No"
      },
      {
        "text": true,
        "label": "Raw OEC Year",
        "fieldName": "sRawOECYear",
        "value": "",
        "disabled": "No"
      }],
    },
    {
      "button": true,
      "value": "3",
      "disabled": "No",
      "label": "Select Interaction",
      "compoundx": false,
      "fieldName": "Select Interaction",
      "compoundvalue": [{
        "button": true,
        "buttonlabel": "With & About",
        "value": "method1",
        "event": "",
        "disabled": "No",
        "type_large": true,
        "type_small": false,
        "event": "eInteractingWithnAbout",
        "rowData": {}
      },
      {
        "button": true,
        "buttonlabel": "With",
        "value": "method2",
        "event": "",
        "disabled": "No",
        "type_small": true,
        "type_large": false,
        "event": "eInteractingWith",
        "rowData": {}
      },
      {
        "button": true,
        "buttonlabel": "About",
        "value": "method3",
        "event": "",
        "disabled": "No",
        "type_small": true,
        "type_large": false,
        "event": "eInteractingAbout",
        "rowData": {}
      }
      ]
    }
  ]];

const trrModal = [
  [{
    "compoundx": true,
    "label": "Member Information",
    "compoundvalue": [{
      "text": true,
      "label": "First Name",
      "value": "",
      "fieldName": "sFirstName",
      "disabled": "No",
	    "link": false,
      "Id": false
    },
    {
      "text": true,
      "label": "Last Name",
      "fieldName": "sLastName",
      "value": "",
      "disabled": "No",
	    "link": false,
      "Id": false
    },
    {
      "text": true,
      "label": "Medicare Claim Number",
      "fieldName": "sMedicareClaim",
      "value": "",
      "disabled": "No"
    }],
  },
  {
    "compoundx": true,
    "label": "Plan Information",
    "compoundvalue": [{
      "text": true,
      "label": "PBP",
      "value": "",
      "fieldName": "sPBP",
      "disabled": "No"
    },
    {
      "text": true,
      "label": "Segment ID",
      "fieldName": "sSegmentNumber",
      "value": "",
      "disabled": "No"
    },
    {
      "text": true,
      "label": "Contract",
      "fieldName": "sContract",
      "value": "",
      "disabled": "No"
    },
    {
      "link": true,
      "label": "Details",
      "fieldName": "TRRresultdetails",
      "value": "",
      "disabled": "No",
	  "actionName": "openTRRDetailPage"
    }],
  },
  {
    "compoundx": false,
    "text": true,
    "value": "3",
    "disabled": "No",
    "label": "Error Message",
    "fieldName": "sErrorMessage"
  },
  {
    "compoundx": true,
    "label": "Reply Information",
    "compoundvalue": [{
      "text": true,
      "label": "Reply Code",
      "fieldName": "sReplyCode",
      "value": "",
      "disabled": "No"
    }, {
      "date": true,
      "label": "Effective Date",
      "value": "",
      "fieldName": "sEffectiveDate",
      "disabled": "No"
    },
    {
      "date": true,
      "label": "Humana Received Date",
      "fieldName": "sHumanaReceivedDate",
      "value": "",
      "disabled": "No"
    }],
  },
  {
    "button": true,
    "value": "3",
    "disabled": "No",
    "label": "Select Interaction",
    "compoundx": false,
    "fieldName": "Select Interaction",
    "compoundvalue": [{
      "button": true,
      "buttonlabel": "With & About",
      "value": "method1",
      "event": "",
      "disabled": "No",
      "type_large": true,
      "type_small": false,
      "event": "eInteractingWithnAbout",
      "rowData": {}
    },
    {
      "button": true,
      "buttonlabel": "With",
      "value": "method2",
      "event": "",
      "disabled": "No",
      "type_small": true,
      "type_large": false,
      "event": "eInteractingWith",
      "rowData": {}
    },
    {
      "button": true,
      "buttonlabel": "About",
      "value": "method3",
      "event": "",
      "disabled": "No",
      "type_small": true,
      "type_large": false,
      "event": "eInteractingAbout",
      "rowData": {}
    }
    ]
  }
  ]];


export const getSearchFormModel = (screenName) => {
  let modal;
  switch (screenName) {
    case TES:
      modal = trackEnrolModal;
      break;
    case AUTOENROLL:
      modal = automatedEnroll;
      break;
    case CBIS:
      modal = cbisModal;
      break;
    case CIM:
      modal = customerInterface;
      break;
    case MARKETSEARCH:
      modal = marketPlaceModal;
      break;
    case TRR:
      modal = trrModal;
      break;
    case APPSEARCH:
      modal = appSearchModal;
      break;
    default:
  }
  return modal;
}