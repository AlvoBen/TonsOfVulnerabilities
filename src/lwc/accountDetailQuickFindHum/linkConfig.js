/*******************************************************************************************************************************
LWC JS Name : linkConfig.js
Function    : This JS serves as helper to accountDetailQuickFindHum.js

Modification Log: 
Developer Name           Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
* Pradeep Dani                                          12/18/2020                    initial version
*********************************************************************************************************************************/
export function getLinks(sRecordType, oUserGroup) {
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
            return memTypeRcc;
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

const memTypeRcc = [
    'Physician Finder',
    'Dentist Finder',
    'Pharmacy Finder',
    'Humana ePhonebook',
    'Humana.com',
    'Member Preferences',
    'USPS.com',
];
const grpType = [
    'Group Contacts',
    'Agent Contacts',
    'Group Authorized Recipient Privacy (GARP)',
    'Employer Preference in Communication Contact (EPICC)',
    'Group Billing Profiles',
    'eAdministration',
    'Legacy Rate Quote',
    'New Case Account Manager',
    'SPIDAR',
    'ID Card Management',
    'CI/PATR',
    'MTV Benefit Verification',
    'Physician Finder',
    'Dentist Finder',
    'Pharmacy Finder',
    'Health Resources',
    'Humana.com'
];
const providerType = [
    'Physician Finder',
    'Dentist Finder',
    'Pharmacy Finder',
    'Create Letter - EMME',
    'Humana.com',
];
const agencyType = [
    'Physician Finder',
    'Dentist Finder',
    'Pharmacy Finder',
    'Create Letter - EMME',
    'MTV Benefit Verification',
    'Humana.com',
];