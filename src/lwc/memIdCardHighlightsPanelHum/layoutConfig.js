/*******************************************************************************************************************************
File Name          : memIdCardHighlightsPanelHum.HTML
Version              : 1.0
Created On           : 02/09/2022
Function             : Serves as helper for memIdCardHighlightsPanelHum. 

Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Vamshi Krishna Pemberthi                              02/09/2022                   Initial Version
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
                return memTypeHp;
            } else if (oUserGroup && oUserGroup.bProvider) {
                return memTypeProviderPcc;
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
            if (oUserGroup && oUserGroup.bPharmacy) {
                return agencyTypeHp;
            } else if (oUserGroup && oUserGroup.bGbo) {
                return agencyTypeGbo;
            } else {
                return agencyTypeRcc;
            }
            break;
    }
};

const memTypeHp = {
    object: { name: "Member Account", icon: { size: "large", name: "standard:person_account" } },
    recodDetail: {
        name: "", mapping: "Name", fields: [
            { label: "Birthdate", bIsCustom: false, mapping: "Birthdate__c" },
            { label: "Age", bIsCustom: true, mapping: "sAge", bAge: true },
            { label: "Gender", bIsCustom: false, mapping: "Gender__c" },
            
            { label: "Mailing Address", bIsCustom: true, bAddress: true, mapping: "PersonMailingAddress" },
            { label: "Residential Address", bIsCustom: false, mapping: "PersonMailingCountry" },
            { label: "Legacy Delete", bIsCustom: false, mapping: "ETL_Record_Deleted__c" }
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
            { label: "Residential Address", bIsCustom: false, mapping: "PersonMailingCountry" },
            { label: "Legacy Delete", bIsCustom: false, mapping: "ETL_Record_Deleted__c" }
        ]
    },
    actions: []
};

const memTypeProviderGbo = {
    object: { name: "Member Account", icon: { size: "large", name: "standard:person_account" } },
    recodDetail: {
        name: "", mapping: "Name", fields: [
            { label: "Birthdate", bIsCustom: false, mapping: "Birthdate__c" },
            { label: "Age", bIsCustom: true, mapping: "sAge", bAge: true },
            { label: "Gender", bIsCustom: false, mapping: "Gender__c" },
            
            { label: "Mailing Address", bIsCustom: true, bAddress: true, mapping: "PersonMailingAddress" },
            { label: "Residential Address", bIsCustom: false, mapping: "PersonMailingCountry" },
            { label: "Legacy Delete", bIsCustom: false, mapping: "ETL_Record_Deleted__c" }
        ]
    },
    actions: []
};

const memTypeRcc = {
    object: { name: "Member Account", icon: { size: "large", name: "standard:person_account" } },
    recodDetail: {
        name: "", mapping: "Name", fields: [
            { label: "Birthdate", bIsCustom: false, mapping: "Birthdate__c" },
            { label: "Age", bIsCustom: true, mapping: "sAge", bAge: true },
            { label: "Gender", bIsCustom: false, mapping: "Gender__c" },
            
            { label: "Mailing Address", bIsCustom: true, bAddress: true, mapping: "PersonMailingAddress" },
            { label: "Residential Address", bIsCustom: false, mapping: "PersonMailingCountry" },
            { label: "Legacy Delete", bIsCustom: false, mapping: "ETL_Record_Deleted__c" }
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
    object: { name: "Account", icon: { size: "large", name: "standard:account" } },
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