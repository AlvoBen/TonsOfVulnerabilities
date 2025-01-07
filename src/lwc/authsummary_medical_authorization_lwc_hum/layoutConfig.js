/*******************************************************************************************************************************
LWC JS Name : layoutConfig.js
Function    : This JS serves as helper to authsummary_medical_authorization_lwc_hum.js

Modification Log: 
Developer Name           Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
Prashant Moghe                                           05/12/2022                    User story 3362694 Authorization Summary table to disply the compound table

*********************************************************************************************************************************/
import { getLabels } from 'c/crmUtilityHum';
const labels = getLabels();

export function getMedicalAuthorizationStructure() {
    return medicalAuthorizationLayout;
}

export const medicalAuthorizationLayout = [
    [
        {
            compoundx: true,
            label: 'Auth/Referral #',
            compoundvalue: [
                {
                    Id: true,
                    link: false,
                    icon: false,
                    hidden: true,
                    label: 'Id',
                    fieldName: 'sAuthorizationOrReferralNumber',
                    value: '',
                    disabled: 'No'
                },
                {
                    link: true,
                    label: '',
                    fieldName: 'sAuthorizationOrReferralNumber',
                    value: '',
                    disabled: 'No',
                    linkToChange: 'sAtuhRefUrl',
                    actionName: '|authsummary_medical_authorization_detail|',
                    pageName: 'authsummary_medical_authorization_detail'
                }
            ]
        },
        {
            compoundx: true,
            label: 'AuthType',
            compoundvalue: [
                {
                    text: true,
                    label: '',
                    fieldName: 'sAuthorizationType',
                    value: '',
                    disabled: 'No'
                }
            ]
        },
        {
            compoundx: true,
            label: 'Overall Status',
            compoundvalue: [
                {
                    text: true,
                    label: '',
                    fieldName: 'sOverallStatus',
                    value: '',
                    Id: false,
                    disabled: 'No'
                }
            ]
        },
        {
            compoundx: true,
            label: 'Date of Service',

            compoundvalue: [
                {
                    text: true,
                    label: 'Admission/First Day ',
                    fieldName: 'sAdmFirstDay',
                    value: '',
                    disabled: 'No'
                },
                {
                    text: true,
                    label: 'Discharge/Last Day',
                    fieldName: 'sDischargeLastDay',
                    value: '',
                    disabled: 'No'
                }
            ]
        },
        {
            compoundx: true,
            label: 'Admission/Service Type',
            compoundvalue: [
                {
                    text: true,
                    label: '',
                    fieldName: 'sServiceType',
                    value: '',
                    disabled: 'No'
                }
            ]
        },
        {
            compoundx: true,
            label: 'Provider',
            compoundvalue: [
                {
                    text: true,
                    label: 'Treating',
                    fieldName: 'sFacility',
                    value: '',
                    disabled: 'No'
                },
                {
                    text: true,
                    label: 'Facility',
                    fieldName: 'sTreatingProvider',
                    value: '',
                    disabled: 'No'
                }
            ]
        },
        {
            compoundx: true,
            label: 'Requesting Provider',
            compoundvalue: [
                {
                    text: true,
                    label: '',
                    fieldName: 'sRequestingrovider',
                    value: '',
                    disabled: 'No'
                }
            ]
        }
    ]
];