// Todo: these modals should be moved to metadata
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
        "label": "Policy Member ID",
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
            "fieldName": "Coverage_Plan_Effective_Date__c",
            "disabled": "No"
        }, {
            "text": true,
            "label": "End",
            "fieldName": "Coverage_Plan_end_date__c",
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
        "fieldName": "Interaction_Origin__c"
    },
    {
        "text": true,
        "label": "Interacting With Type",
        "compoundx": false,
        "value": "",
        "fieldName": "Interacting_With_type__c"
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
        "fieldName": "Created_By_Queue__c"
    }
    ]];

export const groupAccountPolicies = [
    [{
        "compoundx": true,
        "label": "Policy",
        "compoundvalue": [{
            "link": true,
            "label": "Name",
            "value": "10",
            "fieldName": "Name",
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
            "fieldName": "Coverage_Plan_Effective_Date__c",
            "disabled": "No"
        }, {
            "text": true,
            "label": "End",
            "fieldName": "Coverage_Plan_end_date__c",
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
            "fieldName": "Segment_Indicator__c",
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

export const subGrpDivision = [
    [{
        "text": true,
        "label": "Divisions-SubGroups ID",
        "value": "",
        "compoundx": false,
        "fieldName": "sSubGroupID"
    },
    {
        "text": true,
        "label": "Divisions-SubGroups Name",
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
            "fieldName": "dd",
            "value": "MTVx",
            "disabled": "No"
        }]
    }]
];