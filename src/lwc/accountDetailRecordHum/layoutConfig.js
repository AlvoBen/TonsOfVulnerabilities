/*******************************************************************************************************************************
LWC JS Name : layoutConfig.js
Function    : This JS serves as helper to accountDetailRecordHum.js. 

Modification Log: 
Developer Name           Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
* Pradeep Dani                                          12/18/2020                    initial version
* Arpit Jain                                            02/12/2021                    Click To Dial
* Joel George											03/19/2021					  DF 2698 Fix
* Ritik Agarwal											05/22/2021					  Fixed Mapping for FIPS
* Abhishek Mangutkar								    05/09/2022				   	  US-2871585
* Supriya Shastri				                        03/16/2021				      US-1985154
* Muthu kumar                                           06/17/2022                    DF-5050
* Muthu kumar                                           06/21/2022                    DF-5050 v2
* Santhi Mandava                                        06/15/2023                    US 4525669: Display HRA/HNA flag/indicator (Y/N) on the Person Account Page
* Swarnalina Laha										09/19/2023					  US 4873911: T1PRJ0865978 - MF26825 - C01/Consumer/Add missing fields to Unknown Provider Account details Page
*********************************************************************************************************************************/
export function getRTLayout(sRecordType, oUserGroup) {
    const rtMem = 'Member';
    const rtUnMem = 'Unknown Member';
    const rtGrp = 'Group';
    const rtUnGrp = 'Unknown Group';
    const rtPr = 'Provider';
    const rtUnPr = 'Unknown Provider';
    const rtAg = 'Agent/Broker';
    const rtunAg = 'Unknown Agent/Broker';

    switch (sRecordType) {
        case rtMem:
        case rtUnMem:
            if (!(oUserGroup.bPharmacy || oUserGroup.bProvider)) {
                return memTypeHp;
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
            return providerType;
            break;
        case rtAg:
        case rtunAg:
            return agencyType;
            break;
    }
};

const memTypeHp = [{
    title: 'Account Details',
    fields: [{ label: 'Account Name', mapping: 'Name' , copyToClipBoard: true , class:'copyName'},
    { label: 'Home Phone', mapping: 'PersonHomePhone', bIsHome: true, bIsCustom: true, copyToClipBoard: true },
    { label: 'Account Record Type', mapping: 'RecordTypeName', bIsCustom: true, bRecordTypeName: true },
    { label: 'Mobile', mapping: 'PersonMobilePhone', bIsMobile: true, bIsCustom: true },
    { label: 'Gender', mapping: 'Gender__c' },
    { label: 'Work Phone', mapping: 'PersonOtherPhone', bIsWork: true, bIsCustom: true },
    { label: 'Birthdate', mapping: 'Birthdate__c' , copyToClipBoard: true, class:'bday'},
    { label: 'Work Phone Ext', mapping: 'Work_Phone_Ext__c' },
    { label: 'Age', mapping: 'sAge', bIsCustom: true, bAge: true },
    { label: 'Home Email', mapping: 'PersonEmail' },
    { label: 'Security Group(s)', mapping: 'Security_Groups__c' },
    { label: 'Work Email', mapping: 'Work_Email__c'},
    { label: 'Legacy Delete', mapping: 'ETL_Record_Deleted__c' },
    { label: 'HRA / HNA Completed', mapping: 'HRA_HNA_Completed__c',bIsCustom: true,isHRAHNA:true}]
}, {
    title: 'Contact Information',
    fields: [{ label: 'Mailing Address', mapping: 'PersonMailingAddress', bAddress: true, copyToClipBoard: true },
    { label: 'Federal Information Processing Standards (FIPS)', mapping: 'Shipping_FIPS_Code__c' ,class:'copyfips'},
    { label: 'Residential Address', mapping: 'ShippingAddress', bAddress: true },
    { label: 'County Name', mapping: 'Shipping_FIPS_Desc__c'}]
}];

const memTypeRcc = [{
    title: 'Account Details',
    fields: [{ label: 'Account Name', mapping: 'Name' , copyToClipBoard: true },
    { label: 'Security Group(s)', mapping: 'Security_Groups__c' },
    { label: 'Gender', mapping: 'Gender__c' },
    { label: 'Legacy Delete', mapping: 'ETL_Record_Deleted__c' },
    { label: 'Birthdate', mapping: 'Birthdate__c' , copyToClipBoard: true},
    { label: 'Account Record Type', mapping: 'RecordTypeName', bIsCustom: true, bRecordTypeName: true },
    { label: 'Age', mapping: 'sAge', bIsCustom: true, bAge: true },
    { label: 'HRA / HNA Completed', mapping: 'HRA_HNA_Completed__c',bIsCustom: true,isHRAHNA:true}]
   
}, {
    title: 'Contact Information',
    fields: [
    { label: 'Home Phone', mapping: 'PersonHomePhone', bIsHome: true, bIsCustom: true ,copyToClipBoard: true},
    { label: 'Work Email', mapping: 'Work_Email__c', copyToClipBoard: true },
    { label: 'Mobile', mapping: 'PersonMobilePhone', bIsMobile: true, bIsCustom: true, copyToClipBoard: true },
    { label: 'Mailing Address', mapping: 'PersonMailingAddress', bAddress: true , copyToClipBoard: true},
    { label: 'Work Phone', mapping: 'PersonOtherPhone', bIsWork: true, bIsCustom: true },
    { label: 'Residential Address', mapping: 'ShippingAddress', bAddress: true, copyToClipBoard: true},
    { label: 'Work Phone Ext', mapping: 'Work_Phone_Ext__c' },
    { label: 'Federal Information Processing Standards (FIPS)', mapping: 'Shipping_FIPS_Code__c' , copyToClipBoard: true },
    { label: 'Home Email', mapping: 'PersonEmail' , copyToClipBoard: true },
    { label: 'County Name', mapping: 'Shipping_FIPS_Desc__c',copyToClipBoard: true }
]
}];


const grpType = [{
    title: 'Business Account Details',
    fields: [{ label: 'Account Name', mapping: 'Name' },
    { label: 'Group Number', mapping: 'Group_Number__c' },
    { label: 'Account Record Type', mapping: 'RecordTypeName', bIsCustom: true, bRecordTypeName: true },
    { label: 'SIC Code', mapping: 'Sic' }]
}, {
    title: 'Group Details',
    fields: [{ label: 'Original Medical Effective Date', mapping: ' ', bIsCustom: true, bGroupEffectiveDate: true },
    { label: 'Medical Group Enrollment Count', mapping: ' ', bIsCustom: true, bGroupEnrolledSubscriberCountMedical: true },
    { label: 'Next Renewal Date', mapping: ' ', bIsCustom: true, bGroupNextRenewalDate: true },
    { label: 'Dental Group Enrollment Count', mapping: ' ', bIsCustom: true, bGroupEnrolledSubscriberCountDental: true },
    { label: 'EDI', mapping: ' ', bIsCustom: true, bEDIValue: true },
    { label: 'Group Update Frequency', mapping: ' ', bIsCustom: true, bGroupUpdateFrequency: true },
    { label: 'Humana Can Change', mapping: ' ', bIsCustom: true, bHumanaCanChange: true },
    { label: 'Frequency Details', mapping: ' ', bIsCustom: true, bFrequencyDetails: true }]
}];

const providerType = [{
    title: 'Business Account Details',
    fields: [{ label: 'Parent Account', mapping: 'ParentAccountId' },
    { label: 'NPI ID', mapping: 'NPI_ID__c' },
    { label: 'Account Name', mapping: 'Name' },
    { label: 'DBA', mapping: 'DBA__c' },
    { label: 'Account Record Type', mapping: 'RecordTypeName', bIsCustom: true, bRecordTypeName: true },
	{ label: 'Provider Type Code', mapping: 'Provider_Type_Code__c' },
    { label: 'Degree', mapping: 'Degree__c' },
    { label: 'Primary Speciality', mapping: 'Description' },
    { label: 'Individual First Name', mapping: 'Individual_First_Name__c' },
    { label: 'Gender', mapping: 'Gender__c' },
    { label: 'Individual Last Name', mapping: 'Individual_Last_Name__c' },
    { label: 'Birthdate', mapping: 'Birthdate__c' }]
}, {
    title: 'Contact Information',
    fields: [{ label: 'Billing Address', mapping: 'BillingAddress', bAddress: true },
    { label: 'Mailing Address', mapping: 'PersonMailingAddress', bAddress: true },
    { label: 'Phone Ext', mapping: 'Phone_Ext__c' },
	{ label: 'Phone', mapping: 'Phone', bIsPhone: true, bIsCustom: true },
	{ label: 'Work Email', mapping: 'Work_Email__c', copyToClipBoard: false }]
}];

const agencyType = [{
    title: 'Business Account Details',
    fields: [{ label: 'Account Name DBA', mapping: 'Name' },
    { label: 'Account Record Type', mapping: 'RecordTypeName', bIsCustom: true, bRecordTypeName: true },
    { label: 'Gender', mapping: 'Gender__c' },
    { label: 'Agent ID', mapping: 'Agent_ID__c' },
    { label: 'Birthdate', mapping: 'Birthdate__c' },
    { label: 'Agent Type', mapping: 'Agent_Type__c' },
    { label: 'Product status', mapping: 'Producer_Status__c' }]
}, {
    title: 'Contact Information',
    fields: [{ label: 'Billing Address', mapping: 'BillingAddress', bAddress: true },
    { label: 'Work Email', mapping: 'Work_Email__c', copyToClipBoard: true },
    { label: 'Phone', mapping: 'Phone', bIsPhone: true, bIsCustom: true },
    { label: 'Phone Ext', mapping: 'Phone_Ext__c' }]
}];