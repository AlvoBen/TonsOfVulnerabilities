/*******************************************************************************************************************************
LWC JS Name : layoutConfig.js
Function    : This JS serves as helper to accountDetailHighlightsHum.js. 

Modification Log: 
Developer Name           Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
* Pradeep Dani                                          12/18/2020                    initial version
* Supriya Shastri                                       02/23/2021                    address field modification  
* Mohan Kumar N                                         03/02/2021                     US #: 1749525
* Ashish/Ritik                                          03/15/2021                    Removed Medicaid_Id__c reference from Account
* Supriya                                               08/23/2021                    US-2363825 Standardized icons
* Abhishek Mangutkar								    05/09/2022				   	  US-2871585
* Supriya Shastri				                            03/16/2021				      US-1985154
* Hima Bindu Ramayanam                                  07/12/2023				      User Story 4802575: T1PRJ0865978 - INC2410933/Consumer/Toggling between Person Account tabs displays information from 'other' tab
*********************************************************************************************************************************/
export const detailConstants = {
    rtMem: 'Member',
    rtUnMem: 'Unknown Member',
    rtGrp: 'Group',
    rtUnGrp: 'Unknown Group',
    rtPr: 'Provider',
    rtUnPr: 'Unknown Provider',
    rtAg: 'Agent/Broker',
    rtunAg: 'Unknown Agent/Broker'
}
export function getRTLayout(sRecordType, oUserGroup) {
    const { rtMem, rtUnMem, rtGrp, rtUnGrp, rtPr, rtUnPr, rtAg, rtunAg} = detailConstants;

    switch (sRecordType) {
        case rtMem:
        case rtUnMem:
            if (oUserGroup && oUserGroup.bPharmacy) {
                return memTypeRcc;
            } else if (oUserGroup && oUserGroup.bProvider) {
                return memTypeRcc;
            } else if (oUserGroup && oUserGroup.bGbo) {
                return memTypeProviderGbo;
            } else {
                return memTypeRcc;
            }
            break;
        case rtGrp:
        case rtUnGrp:
            return grpType;
            break;
        case rtPr:
        case rtUnPr:
            if (oUserGroup && oUserGroup.bPharmacy) {
                return providerTypeHp;
            } else if (oUserGroup && oUserGroup.bProvider) {
                return providerTypePcc;
            } else {
                return providerTypeRcc;
            }
            break;
        case rtAg:
        case rtunAg:
            return agencyTypeGbo;
    }
};

const memTypeHp = {
    object: { name: "Person Account", icon: { size: "large", name: "standard:person_account" } },
    recodDetail: {
        name: "", mapping: "Name", fields: [
            { label: "Birthdate", bIsCustom: false, mapping: "Birthdate__c" },
            { label: "Age", bIsCustom: true, mapping: "sAge", bAge: true },
            { label: "Gender", bIsCustom: false, mapping: "Gender__c" },
            { label: "Legacy Delete", bIsCustom: false, mapping: "ETL_Record_Deleted__c", donotlog: true },
            { label: "Security Group(s)", bIsCustom: false, mapping: "Security_Groups__c" },
            { label: "Home Phone", bIsCustom: true, mapping: "PersonHomePhone", bIsHome: true },
            { label: "Mailing Address", bIsCustom: true, bAddress: true, mapping: "PersonMailingAddress" }
        ]
    },
    actions: [
        { label: "utility:notification", customEventName: "notification", string: false, button: false, icon: true, classname: "" },
        { label: "Verify Demographics", customEventName: "verifyDemographics", string: true, button: false, icon: false, classname: "" },
        { label: "Verify", customEventName: "Edit", string: false, button: true, icon: false, value: "Edit", classname: "slds-button slds-button_outline-brand slds-float_right" },
        { label: "Verify Family", customEventName: "Edit2", string: false, button: true, icon: false, value: "Edit", classname: "slds-button slds-button_outline-brand" }
    ]
};

const memTypeProviderPcc = {
    object: { name: "Member Account", icon: { size: "large", name: "standard:person_account" } },
    recodDetail: {
        name: "", mapping: "Name", fields: [
            { label: "Birthdate", bIsCustom: false, mapping: "Birthdate__c" },
            { label: "Age", bIsCustom: true, mapping: "sAge", bAge: true },
            { label: "Gender", bIsCustom: false, mapping: "Gender__c" },
            { label: "Mailing Address", bIsCustom: true, bAddress: true, mapping: "PersonMailingAddress" },
            { label: "Residential Address", bIsCustom: true, bAddress: true, mapping: "ShippingAddress" },
            { label: "Legacy Delete", bIsCustom: false, mapping: "ETL_Record_Deleted__c", donotlog: true }
        ]
    },
    actions: []
};

const memTypeProviderGbo = {
    object: { name: "Person Account", icon: { size: "large", name: "standard:person_account" } },
    recodDetail: {
        name: "", mapping: "Name", fields: [
            { label: "Birthdate", bIsCustom: false, mapping: "Birthdate__c", copyToClipBoard: true },
            { label: "Gender", bIsCustom: false, mapping: "Gender__c" },
            { label: "Mailing Address", bIsCustom: true, bAddress: true, mapping: "PersonMailingAddress", copyToClipBoard: true },
            { label: "Home Phone", bIsCustom: true, mapping: "PersonHomePhone", bIsHome: true, copyToClipBoard: true },
            { label: "Work Phone", bIsCustom: true, mapping: "PersonOtherPhone", bIsWork: true, copyToClipBoard: true },
            { label: "Home Email", bIsCustom: false, mapping: "PersonEmail", copyToClipBoard: true },
            { label: "Work Email", bIsCustom: false, mapping: "Work_Email__c", copyToClipBoard: true }
        ]
    },
    actions: []
};

const memTypeRcc = {
    object: { name: "Person Account", icon: { size: "large", name: "standard:person_account" } },
    recodDetail: {
        name: "", mapping: "Name", fields: [
            { label: "Residential Address", bIsCustom: true, rAddress:true, bAddress: false, mapping: "ShippingAddress" , copyToClipBoard: true},
            { label: "Mailing Address", bIsCustom: true, mAddress: true,  bAddress: false, mapping: "PersonMailingAddress" , copyToClipBoard: true},
            { label: "Birthdate", bIsCustom: false, mapping: "Birthdate__c" , copyToClipBoard: true},
            { label: "Gender", bIsCustom: false, mapping: "Gender__c" },
            { label: "Home Phone", bIsCustom: true, mapping: "PersonHomePhone", bIsHome: true , copyToClipBoard: true},
            { label: "Home Email", bIsCustom: false, mapping: "PersonEmail" , copyToClipBoard: true},
            { label: "Security Group(s)", bIsCustom: false, mapping: "Security_Groups__c" }
        ]
    },
    actions: [
        { label: "utility:notification", customEventName: "notification", string: false, button: false, icon: true, classname: "" },
        { label: "Verify Demographics", customEventName: "verifyDemographics", string: true, button: false, icon: false, classname: "" },
        { label: "Verify", customEventName: "Edit", string: false, button: true, icon: false, value: "Edit", classname: "slds-button slds-button_outline-brand slds-float_right" },
        { label: "Verify Family", customEventName: "Edit2", string: false, button: true, icon: false, value: "Edit", classname: "slds-button slds-button_outline-brand slds-float_right" }
    ]
};

const grpType = {
    object: { name: "Business Account", icon: { size: "large", name: "standard:account" } },
    recodDetail: {
        mapping: "Name", fields: [
            { label: "Account Name", bIsCustom: false, mapping: "Name" },
            { label: "Group Number", bIsCustom: false, mapping: "Group_Number__c" },
            { label: "Group Contact Name", bIsCustom: false, mapping: "Group_Contact_Name__c" },
            { label: "Phone", bIsCustom: true, mapping: "Phone", bIsPhone: true },
            { label: "Phone Ext", bIsCustom: false, mapping: "Phone_Ext__c" }
        ]
    },
    actions: []
};

const providerTypeRcc = {
    object: { name: "Account", icon: { size: "large", name: "standard:account" }, bIsProviderType: true },
    recodDetail: {
        mapping: "Name", fields: [
            { label: "Account Record Type", bIsCustom: true, mapping: "RecordTypeName", bRecordTypeName: true },
            { label: "Billing Address", bIsCustom: true, bAddress: true, mapping: "BillingAddress" },
            { label: "DBA", bIsCustom: false, mapping: "DBA__c" },
            { label: "NPI ID", bIsCustom: false, mapping: "NPI_ID__c" },
            { label: "Primary Specialty", bIsCustom: false, mapping: "Description" },
            { label: "Phone", bIsCustom: true, mapping: "Phone", bIsPhone: true },
            { label: "Phone Ext", bIsCustom: false, mapping: "Phone_Ext__c" }
        ]
    },
    actions: []
};

const providerTypePcc = {
    object: { name: "Account", icon: { size: "large", name: "standard:account" }, bIsProviderType: true },
    recodDetail: {
        mapping: "Name", fields: [
            { label: "Account Record Type", bIsCustom: true, mapping: "RecordTypeName", bRecordTypeName: true },
            { label: "Organization", bIsCustom: false, mapping: "Name" },
            { label: "NPI", bIsCustom: false, mapping: "NPI_ID__c" },
            { label: "Tax ID", bIsCustom: true, mapping: "sTaxIds", bTaxIds: true },
            { label: "Billing Address", bIsCustom: true, bAddress: true, mapping: "BillingAddress" },
            { label: "Primary Specialty", bIsCustom: false, mapping: "Description" },
            { label: "Phone", bIsCustom: true, mapping: "Phone", bIsPhone: true }
        ]
    },
    actions: []
};

const providerTypeHp = {
    object: { name: "Account", icon: { size: "large", name: "standard:account" }, bIsProviderType: true },
    recodDetail: {
        mapping: "Name", fields: [
            { label: "Account Record Type", bIsCustom: true, mapping: "RecordTypeName", bRecordTypeName: true },
            { label: "Primary Specialty", bIsCustom: false, mapping: "Description" },
            { label: "Billing Address", bIsCustom: true, bAddress: true, mapping: "BillingAddress" },
            { label: "Phone", bIsCustom: true, mapping: "Phone", bIsPhone: true },
            { label: "Secondary Specialty", bIsCustom: false, mapping: "Primary_Specialty__c" }
        ]
    },
    actions: []
};

const agencyTypeHp = {
    object: { name: "Account", icon: { size: "large", name: "standard:account" } },
    recodDetail: {
        mapping: "Name", fields: [
            { label: "Agent Type", bIsCustom: false, mapping: "Agent_Type__c" },
            { label: "Tax ID", bIsCustom: true, mapping: "sTaxIds", bTaxIds: true },
            { label: "Agent ID", bIsCustom: false, mapping: "Agent_ID__c" },
            { label: "Producer Status", bIsCustom: false, mapping: "Producer_Status__c" },
            { label: "Phone", bIsCustom: true, mapping: "Phone", bIsPhone: true },
            { label: "Work Email", bIsCustom: false, mapping: "Work_Email__c" },
            { label: "Billing Address", bIsCustom: true, bAddress: true, mapping: "BillingAddress" }
        ]
    },
    actions: []
};

const agencyTypeRcc = {
    object: { name: "Account", icon: { size: "large", name: "standard:account" } },
    recodDetail: {
        mapping: "Name", fields: [
            { label: "Agent Type", bIsCustom: false, mapping: "Agent_Type__c" },
            { label: "Billing Address", bIsCustom: true, bAddress: true, mapping: "BillingAddress" },
            { label: "Phone", bIsCustom: true, mapping: "Phone", bIsPhone: true },
            { label: 'Phone Ext', bIsCustom: false, mapping: 'Phone_Ext__c' },
            { label: "Work Email", bIsCustom: false, mapping: "Work_Email__c" },
            { label: "Gender", bIsCustom: false, mapping: "Gender__c" },
            { label: "Producer Status", bIsCustom: false, mapping: "Producer_Status__c" },
            { label: "Agent ID", bIsCustom: false, mapping: "Agent_ID__c" }
        ]
    },
    actions: []
};

const agencyTypeGbo = {
    object: { name: "Account", icon: { size: "large", name: "standard:account" } },
    recodDetail: {
        mapping: "Name", fields: [
            { label: "Agent Type", bIsCustom: false, mapping: "Agent_Type__c" },
            { label: "Agent ID", bIsCustom: false, mapping: "Agent_ID__c" },
            { label: "Tax ID", bIsCustom: true, mapping: "sTaxIds", bTaxIds: true },
            { label: "Producer Status", bIsCustom: false, mapping: "Producer_Status__c" },
            { label: "Phone", bIsCustom: true, mapping: "Phone", bIsPhone: true },
            { label: "Work Email", bIsCustom: false, mapping: "Work_Email__c" },
            { label: "Billing Address", bIsCustom: true, bAddress: true, mapping: "BillingAddress" }
        ]
    },
    actions: []
};