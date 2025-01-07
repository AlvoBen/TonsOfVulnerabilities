import { phoneFormat, zipcodeFormat } from 'c/crmserviceHelper';
import { AddressListRequest } from './demographicMedicareAvfModel';
import { deducePredecessor, deduceSuccessorFromLanding, deduceSuccessorFromTemporary, deduceSuccessorFromMailing, validateAbsenceEndDate } from './demographicMedicareAvfHelper';
import { checkValidity, deduceCheckHelper, formatPhoneNumber, checkBtnValidity, deduceVerifyBtnClass, deduceAVRequestObj, retrofitValues, deduceIfInFuture } from './demographicMedicareAvfHelper';
import SearchVisitor_PhoneMsg_HUM from '@salesforce/label/c.SearchVisitor_PhoneMsg_HUM';
import SearchVisitor_ZipCodeInvalidMsg_HUM from '@salesforce/label/c.SearchVisitor_ZipCodeInvalidMsg_HUM';
import OSAUndetermineMessage from '@salesforce/label/c.OSAUndetermineMessage';

const commonSelector = "[data-mode=editInput]";

const flowRenderObject = {
    model: "flowDataModel",
    leftProperty: "dirty",
    rightProperty: "reactive",
    renderProperty: "flowDataOutput"
};

const flowButtonRenderObject = {
    model: "flowButtonDataModel",
    leftProperty: "dirty",
    rightProperty: "reactive"
}

const copyFieldMap = {
    r2m: { 5: 15, 29:30, 6: 16, 7: 17, 8: 18, 9: 19 },
    m2r: { 15: 5, 30:29, 16: 6, 17: 7, 18: 8, 19: 9 },
    t2m: { 22: 15, 31:30, 23: 16, 24: 17, 25: 18, 26: 19 }
};

const flowFieldModel = () => {
    return [
        {
            order: 1, label: "Person Speaking With", value: "", type: "text", 
            fieldName: "personName", pageNumber: 1,
            required: {value: true, message: "Person Speaking With is required"},
            renderLogic:{
                isCombo: false, isText: true, isTel: false, isEmail: false, isDate: false, 
                isCheck: false, isRadio: false, isRadioGroup: false
            },
            templateModel: {
                avfRequestFieldName: 'PersonSpeakingWith', avfValue: '',
                avfSummary: { sectionName: 'Interaction and Address Update Request', title: 'Medicare Address Update Summary' },
                expr: [
                    {
                        fnName: 'deriveValue',
                        fnArgs:  [
                            { order: 0, propertyName: "value", literal: false }
                        ],
                        fnType: "return",
                        fnOut: ["avfValue"],
                        fnOutSource: [1],
                        fnWhen: "template"
                    }
                ]
            },
            pattern: {value: undefined, message: undefined}, minLength: undefined, maxLength: undefined,
            note: "Must be Member of Valid POA, unless member gives consent", isSummary: true
        },
        {
            order: 2, label: "Relationship to Member", value: "", type: "text", 
            fieldName: "personRelation", pageNumber: 1,
            required: {value: true, message: "Relationship to Member is required"},
            renderLogic:{
                isCombo: false, isText: true, isTel: false, isEmail: false, isDate: false, 
                isCheck: false, isRadio: false, isRadioGroup: false
            },
            templateModel: {
                avfRequestFieldName: 'RelationToMember', avfValue: '',
                avfSummary: { sectionName: 'Interaction and Address Update Request', title: 'Medicare Address Update Summary' },
                expr: [
                    {
                        fnName: 'deriveValue',
                        fnArgs:  [
                            { order: 0, propertyName: "value", literal: false }
                        ],
                        fnType: "return",
                        fnOut: ["avfValue"],
                        fnOutSource: [2],
                        fnWhen: "template"
                    }
                ]
            },
            pattern: {value: undefined, message: undefined}, minLength: undefined, maxLength: undefined,
            note: undefined, isSummary: true
        },
        {
            order: 3, label: "Residential Phone Number", value: "", type: "tel", 
            fieldName: "personPhone", pageNumber: 1,
            required: {value: true, message: "Residential Phone Number is required"},
            renderLogic:{
                isCombo: false, isText: false, isTel: true, isEmail: false, isDate: false, 
                isCheck: false, isRadio: false, isRadioGroup: false
            },
            templateModel: {
                avfRequestFieldName: 'PermanentResidentialPhoneNumber', avfValue: '',
                avfSummary: { sectionName: 'Temporary Address Information', title: 'Medicare Address Update Summary' },
                expr: [
                    {
                        fnName: 'deriveValue',
                        fnArgs:  [
                            { order: 0, propertyName: "value", literal: false }
                        ],
                        fnType: "return",
                        fnOut: ["avfValue"],
                        fnOutSource: [3],
                        fnWhen: "template"
                    }
                ]
            },
            expr: [
                {
                    fnName: "formatPhoneNumber", 
                    fnArgs:  [
                        { order: 0, propertyName: "value", literal: false }
                    ],
                    fnType: "return",
                    fnOut: "value",
                    fnOutSource: "self",
                    fnWhen: "change"
                }
            ],
            formatPhoneNumber: formatPhoneNumber,
            pattern: {value: phoneFormat, message: SearchVisitor_PhoneMsg_HUM}, 
            minLength: undefined, maxLength: 14,
            note: undefined
        },
        {
            order: 4, label: "What address is the Member calling to update?", value: "", 
            type: "radioGroup", fieldName: "addressUdpateType", pageNumber: 1,
            required: {value: true, message: "Atleast select one address type option"},
            renderLogic:{
                isCombo: false, isText: false, isTel: false, isEmail: false, isDate: false, 
                isCheck: false, isRadio: false, isRadioGroup: true
            },
            options: [
                { label: 'Residential', value: 'residential', id: "radio-res-1", checked: false },
                { label: 'Mailing', value: 'mailing', id: "radio-mail-1", checked: false },
                { label: 'Temporary', value: 'temporary', id: "radio-temp-1", checked: false }
            ],
            templateModel: {
                avfRequestFieldName: 'WhatAddressMemberCallingToUpdate', avfValue: '',
                avfSummary: { sectionName: 'Interaction and Address Update Request', title: 'Medicare Address Update Summary' },
                expr: [
                    {
                        fnName: 'deriveValue',
                        fnArgs:  [
                            { order: 0, propertyName: "value", literal: false }
                        ],
                        fnType: "return",
                        fnOut: ["avfValue"],
                        fnOutSource: [4],
                        fnWhen: "template"
                    }
                ]
            },
            expr: [
                {
                    fnName: "checkValidity", 
                    fnArgs:  [
                        { order: 0, propertyName: "options", literal: false },
                        { order: 1, propertyName: "required", literal: false }
                    ],
                    fnType: "return",
                    fnOut: "validityResult",
                    fnOutSource: "self",
                    fnWhen: "render"
                },
                {
                    fnName: "deduceCheck", 
                    fnArgs:  [
                        { order: 0, propertyName: "options", literal: false },
                        { order: 1, propertyName: "value", literal: false },
                        { order: 2, propertyName: "purge", literal: true }
                    ],
                    fnType: "return",
                    fnOut: "options",
                    fnOutSource: "self",
                    fnWhen: "change"
                },
                {
                    fnName: "deducePredecessor", 
                    fnArgs:  [],
                    fnType: "return",
                    fnOut: "pagePredecessor",
                    fnOutSource: "parent",
                    fnWhen: "render"
                },
                {
                    fnName: "deduceSuccessor", 
                    fnArgs:  [
                        { order: 0, propertyName: "options", literal: false }
                    ],
                    fnType: "return",
                    fnOut: "pageSuccessor",
                    fnOutSource: "parent",
                    fnWhen: "change"
                }
            ],
            deduceCheck: deduceCheckHelper,
            checkValidity: checkValidity,
            deducePredecessor: deducePredecessor,
            deduceSuccessor: deduceSuccessorFromLanding,
            validityResult: {},
            pattern: {value: undefined, message: undefined}, 
            minLength: undefined, maxLength: undefined,
            note: undefined
        },
        {
            order: 5, label: "Permanent Residential Address Line 1", value: "", type: "text", 
            fieldName: "AddressLine1", pageNumber: 2, 
            visibility: [
                {journey: "mailing", mode: "residential"}, 
                {journey: "residential", mode: "residential"}, 
                {journey: "temporary", mode: "residential"}
            ],
            required: {value: true, message: "Permanent Residential Address Line 1 is required"},
            renderLogic:{
                isCombo: false, isText: true, isTel: false, isEmail: false, isDate: false, 
                isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
            },
            templateModel: {
                avfRequestFieldName: 'PermanentResidentialAddress', avfValue: '',
                avfSummary: { sectionName: 'Residential Address Information', title: 'Medicare Address Update Summary' },
                expr: [
                    {
                        fnName: 'concatAddress',
                        fnArgs:  [
                            { order: 0, propertyName: "value", literal: false },
                            { order: 1, propertyName: 29, literal: true },
                            { order: 2, propertyName: "value", literal: true },
                            { order: 3, propertyName: "mau", literal: true },
                        ],
                        fnType: "return",
                        fnOut: ["avfValue"],
                        fnOutSource: [5],
                        fnWhen: "template"
                    }
                ]
            },
            updateModel: {
                expr: [
                    {
                        fnName: 'deriveAddressRequestValue',
                        fnArgs:  [
                            { order: 0, propertyName: [5, 29, 6, 7, 8, 9], literal: true },
                            { order: 1, propertyName: ["line1", "line2", "city", "statecode", "zipcode", "countyname"], literal: true },
                            { order: 2, propertyName: "demographics", literal: true },
                            { order: 3, propertyName: 'member', literal: true },
                            { order: 4, propertyName: 'address', literal: true },
                            { order: 5, propertyName: 'type', literal: true },
                            { order: 6, propertyName: '03', literal: true },
                            { order: 7, propertyName: 'mau', literal: true }
                        ],
                        fnType: "void",
                        fnOut: [],
                        fnOutSource: [],
                        fnWhen: "update"
                    },
                    {
                        fnName: 'deriveRequestUpdateType',
                        fnArgs:  [
                            { order: 0, propertyName: "isSummary", literal: false },
                            { order: 1, propertyName: 5, literal: true },
                            { order: 2, propertyName: "membercriterion", literal: true },
                            { order: 3, propertyName: "updaterequesttype", literal: true },
                            { order: 4, propertyName: 'member', literal: true },
                            { order: 5, propertyName: 'DEMOGRAPHICS', literal: true }
                        ],
                        fnType: "void",
                        fnOut: [],
                        fnOutSource: [],
                        fnWhen: "update"
                    }
                ]
            },
            expr: [
                {
                    fnName: "retrofitValues", 
                    fnArgs:  [
                        { order: 0, propertyName: 5, literal: true }
                    ],
                    fnType: "void",
                    fnOut: undefined,
                    fnOutSource: undefined,
                    fnWhen: "change"
                }
            ],
            retrofitValues: retrofitValues,
            pattern: {value: undefined, message: undefined}, minLength: undefined, maxLength: 32,
            note: undefined, isSummary: true
        },
        {
            order: 29, label: "Permanent Residential Address Line 2", value: "", type: "text", 
            fieldName: "AddressLine2", pageNumber: 2, 
            visibility: [
                {journey: "mailing", mode: "residential"}, 
                {journey: "residential", mode: "residential"}, 
                {journey: "temporary", mode: "residential"}
            ],
            required: {value: undefined, message: undefined},
            renderLogic:{
                isCombo: false, isText: true, isTel: false, isEmail: false, isDate: false, 
                isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
            },
            pattern: {value: undefined, message: undefined}, minLength: undefined, maxLength: 32,
            note: `Residential address cannot be P.O. Box unless the member is homeless or travels in a RV. When entering an address, please do not enter any special characters (i.e, #,%,$)`, isSummary: true
        },
        {
            order: 6, label: "Residential City", value: "", type: "text", 
            fieldName: "City", pageNumber: 2,
            visibility: [
                {journey: "mailing", mode: "residential"}, 
                {journey: "residential", mode: "residential"}, 
                {journey: "temporary", mode: "residential"}
            ],
            required: {value: true, message: "Residential City is required"},
            renderLogic:{
                isCombo: false, isText: true, isTel: false, isEmail: false, isDate: false, 
                isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
            },
            templateModel: {
                avfRequestFieldName: 'PermanentResidentialCityName', avfValue: '',
                avfSummary: { sectionName: 'Residential Address Information', title: 'Medicare Address Update Summary' },
                expr: [
                    {
                        fnName: 'deriveValue',
                        fnArgs:  [
                            { order: 0, propertyName: "value", literal: false }
                        ],
                        fnType: "return",
                        fnOut: ["avfValue"],
                        fnOutSource: [6],
                        fnWhen: "template"
                    }
                ]
            },
            pattern: {value: undefined, message: undefined}, minLength: undefined, maxLength: 15,
            note: undefined, isSummary: true
        },
        {
            order: 7, label: "Residential State", value: "", type: "combobox", 
            fieldName: "StateCode", pageNumber: 2, 
            visibility: [
                {journey: "mailing", mode: "residential"}, 
                {journey: "residential", mode: "residential"}, 
                {journey: "temporary", mode: "residential"}
            ],
            required: {value: true, message: "Residential State is required"},
            renderLogic:{
                isCombo: true, isText: false, isTel: false, isEmail: false, isDate: false, 
                isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
            },
            templateModel: {
                avfRequestFieldName: 'PermanentResidentialStateCode', avfValue: '',
                avfSummary: { sectionName: 'Residential Address Information', title: 'Medicare Address Update Summary' },
                expr: [
                    {
                        fnName: 'deriveValue',
                        fnArgs:  [
                            { order: 0, propertyName: "value", literal: false }
                        ],
                        fnType: "return",
                        fnOut: ["avfValue"],
                        fnOutSource: [7],
                        fnWhen: "template"
                    }
                ]
            },
            pattern: {value: undefined, message: undefined}, minLength: undefined, maxLength: undefined,
            note: undefined, isSummary: true
        },
        {
            order: 8, label: "Residential Zip Code", value: "", type: "text", 
            fieldName: "ZipCode", pageNumber: 2,
            visibility: [
                {journey: "mailing", mode: "residential"}, 
                {journey: "residential", mode: "residential"}, 
                {journey: "temporary", mode: "residential"}
            ],
            required: {value: true, message: "Residential Zip Code is required"},
            renderLogic:{
                isCombo: false, isText: true, isTel: false, isEmail: false, isDate: false, 
                isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
            },
            templateModel: {
                avfRequestFieldName: 'PermanentResidentialZipCode', avfValue: '',
                avfSummary: { sectionName: 'Residential Address Information', title: 'Medicare Address Update Summary' },
                expr: [
                    {
                        fnName: 'deriveValue',
                        fnArgs:  [
                            { order: 0, propertyName: "value", literal: false }
                        ],
                        fnType: "return",
                        fnOut: ["avfValue"],
                        fnOutSource: [8],
                        fnWhen: "template"
                    }
                ]
            },
            pattern: {value: zipcodeFormat, message: SearchVisitor_ZipCodeInvalidMsg_HUM}, 
            minLength: 5, maxLength: 9,
            note: undefined, isSummary: true
        },
        {
            order: 9, label: "Residential County", value: "", type: "text", 
            fieldName: "CountyName", pageNumber: 2, renderButton: true, osaRender: true,
            visibility: [
                {journey: "mailing", mode: "residential"}, 
                {journey: "residential", mode: "residential"}, 
                {journey: "temporary", mode: "residential"}
            ],
            required: {value: true, message: "Residential County is required"},
            renderLogic:{
                isCombo: false, isText: true, isTel: false, isEmail: false, isDate: false, 
                isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
            },
            templateModel: {
                avfRequestFieldName: 'PermanentResidentialCountyName', avfValue: '',
                avfSummary: { sectionName: 'Residential Address Information', title: 'Medicare Address Update Summary' },
                expr: [
                    {
                        fnName: 'deriveValue',
                        fnArgs:  [
                            { order: 0, propertyName: "value", literal: false }
                        ],
                        fnType: "return",
                        fnOut: ["avfValue"],
                        fnOutSource: [9],
                        fnWhen: "template"
                    }
                ]
            },
            pattern: {value: undefined, message: undefined}, 
            minLength: undefined, maxLength: undefined,
            note: "Enter the actual county name, NOT the code", isSummary: true,
            osa: {isOSA: undefined, message: undefined, class: undefined}
        },
        {
            order: 10, label: "Date began living at address", value: "", type: "date",
            fieldName: 'resdientialLivingSince', pageNumber: 2, 
            visibility: [
                {journey: "mailing", mode: "residential"}, 
                {journey: "residential", mode: "residential"}, 
                {journey: "temporary", mode: "residential"},
                {journey: "mailing", mode: "residentialTemporary"}
            ],
            required: { value: true, message: "Date began living at address is required" }, 
            renderLogic:{
                isCombo: false, isText: false, isTel: false, isEmail: false, isDate: true, 
                isCheck: false
            },
            templateModel: {
                avfRequestFieldName: 'PermanentResidentialStartDate', avfValue: '',
                avfSummary: { sectionName: 'Residential Address Information', title: 'Medicare Address Update Summary' },
                expr: [
                    {
                        fnName: 'deriveValue',
                        fnArgs:  [
                            { order: 0, propertyName: "value", literal: false }
                        ],
                        fnType: "return",
                        fnOut: ["avfValue"],
                        fnOutSource: [10],
                        fnWhen: "template"
                    }
                ]
            },
            pattern: {value: undefined, message: undefined}, minLength: undefined, maxLength: undefined,
            note: "Allowed format is M/D/YYYY. System will translate it into MM/DD/YYYY internally", isSummary: true,
            expr: [
                {
                    fnName: "deduceIfInFuture", 
                    fnArgs:  [
                        { order: 0, propertyName: "value", literal: false }
                    ],
                    fnType: "void",
                    fnOut: [],
                    fnOutSource: [],
                    fnWhen: "change"
                }
            ],
            deduceIfInFuture: deduceIfInFuture
        },
        {
            order: 11, label: `Does the member have a mailing address different from their permanent residential address?`, 
            value: "",
            visibility: [
                {journey: "residential", mode: "residential"}
            ],
            type: "radioGroup", fieldName: "residentailMailDifferent", pageNumber: 2,
            required: {value: true, message: "Answer is required to proceed further"},
            renderLogic:{
                isCombo: false, isText: false, isTel: false, isEmail: false, isDate: false, 
                isCheck: false, isRadio: false, isRadioGroup: true
            },
            templateModel: {
                avfRequestFieldName: 'DoesMemberHaveMailAddressDiffFromPermAddress', avfValue: '',
                avfSummary: { sectionName: 'Residential Address Information', title: 'Medicare Address Update Summary' },
                expr: [
                    {
                        fnName: 'deriveValue',
                        fnArgs:  [
                            { order: 0, propertyName: "valueLabel", literal: false }
                        ],
                        fnType: "return",
                        fnOut: ["avfValue"],
                        fnOutSource: [11],
                        fnWhen: "template"
                    }
                ]
            },
            options: [
                { label: 'Yes', value: 'mailing', id: "radio-1", checked: false },
                { label: 'No', value: 'mailingTemporary', id: "radio-2", checked: false }
            ],
            mapKey: "r2m", 
            expr: [
                {
                    fnName: "checkValidity", 
                    fnArgs:  [
                        { order: 0, propertyName: "options", literal: false },
                        { order: 1, propertyName: "required", literal: false }
                    ],
                    fnType: "return",
                    fnOut: "validityResult",
                    fnOutSource: "self",
                    fnWhen: "render"
                },
                {
                    fnName: "deduceCheck", 
                    fnArgs:  [
                        { order: 0, propertyName: "options", literal: false },
                        { order: 1, propertyName: "value", literal: false },
                        { order: 2, propertyName: "r2m", literal: true },
                        { order: 3, propertyName: "radio-2", literal: true }
                    ],
                    fnType: "return",
                    fnOut: "options",
                    fnOutSource: "self",
                    fnWhen: "change"
                },
                {
                    fnName: "deducePredecessor", 
                    fnArgs:  [],
                    fnType: "return",
                    fnOut: "pagePredecessor",
                    fnOutSource: "parent",
                    fnWhen: "render"
                },
                {
                    fnName: "deduceSuccessor", 
                    fnArgs:  [
                        { order: 0, propertyName: "options", literal: false }
                    ],
                    fnType: "return",
                    fnOut: "pageSuccessor",
                    fnOutSource: "parent",
                    fnWhen: "change"
                }
            ],
            deduceCheck: deduceCheckHelper,
            checkValidity: checkValidity, 
            deducePredecessor: deducePredecessor,
            deduceSuccessor: deduceSuccessorFromLanding,
            validityResult: {},
            pattern: {value: undefined, message: undefined}, 
            minLength: undefined, maxLength: undefined,
            note: undefined, isSummary: true
        },
        {
            order: 12, label: `Does the member have a temporary address?`, 
            value: "",
            visibility: [
                {journey: "mailing", mode: "residential"}, 
                {journey: "mailing", mode: "residentialTemporary"}
            ],
            type: "radioGroup", fieldName: "residentailMailDifferent", pageNumber: 2,
            required: {value: true, message: "Answer is required to proceed further"},
            renderLogic:{
                isCombo: false, isText: false, isTel: false, isEmail: false, isDate: false, 
                isCheck: false, isRadio: false, isRadioGroup: true
            },
            options: [
                { label: 'Yes', value: 'temporary', id: "radio-3", checked: false },
                { label: 'No', value: 'summary', id: "radio-4", checked: false }
            ],
            templateModel: {
                avfRequestFieldName: 'DoesResMemberHaveTemporaryAddress', avfValue: '',
                avfSummary: { sectionName: 'Residential Address Information', title: 'Medicare Address Update Summary' },
                expr: [
                    {
                        fnName: 'deriveValue',
                        fnArgs:  [
                            { order: 0, propertyName: "valueLabel", literal: false }
                        ],
                        fnType: "return",
                        fnOut: ["avfValue"],
                        fnOutSource: [12],
                        fnWhen: "template"
                    }
                ]
            },
            expr: [
                {
                    fnName: "checkValidity", 
                    fnArgs:  [
                        { order: 0, propertyName: "options", literal: false },
                        { order: 1, propertyName: "required", literal: false }
                    ],
                    fnType: "return",
                    fnOut: "validityResult",
                    fnOutSource: "self",
                    fnWhen: "render"
                },
                {
                    fnName: "deduceCheck", 
                    fnArgs:  [
                        { order: 0, propertyName: "options", literal: false },
                        { order: 1, propertyName: "value", literal: false }
                    ],
                    fnType: "return",
                    fnOut: "options",
                    fnOutSource: "self",
                    fnWhen: "change"
                },
                {
                    fnName: "deducePredecessor", 
                    fnArgs:  [],
                    fnType: "return",
                    fnOut: "pagePredecessor",
                    fnOutSource: "parent",
                    fnWhen: "render"
                },
                {
                    fnName: "deduceSuccessor", 
                    fnArgs:  [
                        { order: 0, propertyName: "options", literal: false }
                    ],
                    fnType: "return",
                    fnOut: "pageSuccessor",
                    fnOutSource: "parent",
                    fnWhen: "change"
                }
            ],
            deduceCheck: deduceCheckHelper,
            checkValidity: checkValidity, 
            deducePredecessor: deducePredecessor,
            deduceSuccessor: deduceSuccessorFromLanding,
            validityResult: {},
            pattern: {value: undefined, message: undefined}, 
            minLength: undefined, maxLength: undefined,
            note: undefined, isSummary: true
        },
        {
            order: 13, label: `Does the member want their mail sent to their temporary address?`, 
            value: "",
            visibility: [
                {journey: "temporary", mode: "residential"}
            ],
            type: "radioGroup", fieldName: "residentailMailDifferent", pageNumber: 2,
            required: {value: true, message: "Answer is required to proceed further"},
            renderLogic:{
                isCombo: false, isText: false, isTel: false, isEmail: false, isDate: false, 
                isCheck: false, isRadio: false, isRadioGroup: true
            },
            templateModel: {
                avfRequestFieldName: 'DoesMemberWantMailSentToTemporaryAddress', avfValue: '',
                avfSummary: { sectionName: 'Residential Address Information', title: 'Medicare Address Update Summary' },
                expr: [
                    {
                        fnName: 'deriveValue',
                        fnArgs:  [
                            { order: 0, propertyName: "valueLabel", literal: false }
                        ],
                        fnType: "return",
                        fnOut: ["avfValue"],
                        fnOutSource: [13],
                        fnWhen: "template"
                    }
                ]
            },
            options: [
                { label: 'Yes', value: 'summary', id: "radio-5", checked: false },
                { label: 'No', value: 'proxy', id: "radio-6", checked: false }
            ],
            mapKey: "t2m",
            expr: [
                {
                    fnName: "checkValidity", 
                    fnArgs:  [
                        { order: 0, propertyName: "options", literal: false },
                        { order: 1, propertyName: "required", literal: false }
                    ],
                    fnType: "return",
                    fnOut: "validityResult",
                    fnOutSource: "self",
                    fnWhen: "render"
                },
                {
                    fnName: "deduceCheck", 
                    fnArgs:  [
                        { order: 0, propertyName: "options", literal: false },
                        { order: 1, propertyName: "value", literal: false },
                        { order: 2, propertyName: "t2m", literal: true },
                        { order: 3, propertyName: "radio-5", literal: true }
                    ],
                    fnType: "return",
                    fnOut: "options",
                    fnOutSource: "self",
                    fnWhen: "change"
                },
                {
                    fnName: "deducePredecessor", 
                    fnArgs:  [],
                    fnType: "return",
                    fnOut: "pagePredecessor",
                    fnOutSource: "parent",
                    fnWhen: "render"
                },
                {
                    fnName: "deduceSuccessor", 
                    fnArgs:  [
                        { order: 0, propertyName: "options", literal: false }
                    ],
                    fnType: "return",
                    fnOut: "pageSuccessor",
                    fnOutSource: "parent",
                    fnWhen: "change"
                }
            ],
            deduceCheck: deduceCheckHelper,
            checkValidity: checkValidity, 
            deducePredecessor: deducePredecessor,
            deduceSuccessor: deduceSuccessorFromLanding,
            validityResult: {},
            pattern: {value: undefined, message: undefined}, 
            minLength: undefined, maxLength: undefined,
            note: undefined, isSummary: true
        },
        {
            order: 14, label: `Does the member want their mailing address different from their residential address?`, 
            value: "",
            visibility: [
                {journey: "temporary", mode: "proxy"}
            ],
            type: "radioGroup", fieldName: "residentailMailDifferent", pageNumber: 6,
            required: {value: true, message: "Answer is required to proceed further"},
            renderLogic:{
                isCombo: false, isText: false, isTel: false, isEmail: false, isDate: false, 
                isCheck: false, isRadio: false, isRadioGroup: true
            },
            templateModel: {
                avfRequestFieldName: 'DoesMemberWantMailAddressDiffFromResAddress', avfValue: '',
                avfSummary: { sectionName: 'Residential Address Information', title: 'Medicare Address Update Summary' },
                expr: [
                    {
                        fnName: 'deriveValue',
                        fnArgs:  [
                            { order: 0, propertyName: "valueLabel", literal: false }
                        ],
                        fnType: "return",
                        fnOut: ["avfValue"],
                        fnOutSource: [14],
                        fnWhen: "template"
                    }
                ]
            },
            options: [
                { label: 'Yes', value: 'mailingSecondary', id: "radio-11", checked: false },
                { label: 'No', value: 'summary', id: "radio-12", checked: false }
            ],
            mapKey: "r2m",
            expr: [
                {
                    fnName: "checkValidity", 
                    fnArgs:  [
                        { order: 0, propertyName: "options", literal: false },
                        { order: 1, propertyName: "required", literal: false }
                    ],
                    fnType: "return",
                    fnOut: "validityResult",
                    fnOutSource: "self",
                    fnWhen: "render"
                },
                {
                    fnName: "deduceCheck", 
                    fnArgs:  [
                        { order: 0, propertyName: "options", literal: false },
                        { order: 1, propertyName: "value", literal: false },
                        { order: 2, propertyName: "r2m", literal: true },
                        { order: 3, propertyName: "radio-12", literal: true }
                    ],
                    fnType: "return",
                    fnOut: "options",
                    fnOutSource: "self",
                    fnWhen: "change"
                },
                {
                    fnName: "deducePredecessor", 
                    fnArgs:  [],
                    fnType: "return",
                    fnOut: "pagePredecessor",
                    fnOutSource: "parent",
                    fnWhen: "render"
                },
                {
                    fnName: "deduceSuccessor", 
                    fnArgs:  [
                        { order: 0, propertyName: "options", literal: false }
                    ],
                    fnType: "return",
                    fnOut: "pageSuccessor",
                    fnOutSource: "parent",
                    fnWhen: "change"
                }
            ],
            deduceCheck: deduceCheckHelper,
            checkValidity: checkValidity, 
            deducePredecessor: deducePredecessor,
            deduceSuccessor: deduceSuccessorFromLanding,
            validityResult: {},
            pattern: {value: undefined, message: undefined}, 
            minLength: undefined, maxLength: undefined,
            note: undefined, isSummary: true
        },
        {
            order: 15, label: "Mailing Address Line 1", value: "", type: "text", 
            fieldName: "AddressLine1", pageNumber: 3,
            visibility: [
                {journey: "mailing", mode: "mailing"}, 
                {journey: "residential", mode: "mailing"}, 
                {journey: "temporary", mode: "mailingSecondary"},
            ],
            required: {value: true, message: "Mailing Address is required"},
            renderLogic:{
                isCombo: false, isText: true, isTel: false, isEmail: false, isDate: false, 
                isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
            },
            templateModel: {
                avfRequestFieldName: 'MailingAddress', avfValue: '',
                avfSummary: { sectionName: 'Mailing Address Information', title: 'Medicare Address Update Summary' },
                expr: [
                    {
                        fnName: 'concatAddress',
                        fnArgs:  [
                            { order: 0, propertyName: "value", literal: false },
                            { order: 1, propertyName: 30, literal: true },
                            { order: 2, propertyName: "value", literal: true },
                            { order: 3, propertyName: "mau", literal: true },
                        ],
                        fnType: "return",
                        fnOut: ["avfValue"],
                        fnOutSource: [15],
                        fnWhen: "template"
                    }
                ]
            },
            updateModel: {
                expr: [
                    {
                        fnName: 'deriveAddressRequestValue',
                        fnArgs:  [
                            { order: 0, propertyName: [15, 30, 16, 17, 18, 19], literal: true },
                            { order: 1, propertyName: ["line1", "line2", "city", "statecode", "zipcode", "countyname"], literal: true },
                            { order: 2, propertyName: "demographics", literal: true },
                            { order: 3, propertyName: 'member', literal: true },
                            { order: 4, propertyName: 'address', literal: true },
                            { order: 5, propertyName: 'type', literal: true },
                            { order: 6, propertyName: '01', literal: true },
                            { order: 7, propertyName: 'mau', literal: true }
                        ],
                        fnType: "void",
                        fnOut: [],
                        fnOutSource: [],
                        fnWhen: "update"
                    },
                    {
                        fnName: 'deriveRequestUpdateType',
                        fnArgs:  [
                            { order: 0, propertyName: "isSummary", literal: false },
                            { order: 1, propertyName: 15, literal: true },
                            { order: 2, propertyName: "membercriterion", literal: true },
                            { order: 3, propertyName: "updaterequesttype", literal: true },
                            { order: 4, propertyName: 'member', literal: true },
                            { order: 5, propertyName: 'DEMOGRAPHICS', literal: true }
                        ],
                        fnType: "void",
                        fnOut: [],
                        fnOutSource: [],
                        fnWhen: "update"
                    }
                ]
            },
            pattern: {value: undefined, message: undefined}, minLength: undefined, maxLength: 32,
            note: undefined, isSummary: true
        },
        {
            order: 30, label: "Mailing Address Line 2", value: "", type: "text", 
            fieldName: "AddressLine2", pageNumber: 3,
            visibility: [
                {journey: "mailing", mode: "mailing"}, 
                {journey: "residential", mode: "mailing"}, 
                {journey: "temporary", mode: "mailingSecondary"},
            ],
            required: {value: undefined, message: undefined},
            renderLogic:{
                isCombo: false, isText: true, isTel: false, isEmail: false, isDate: false, 
                isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
            },
            pattern: {value: undefined, message: undefined}, minLength: undefined, maxLength: 32,
            note: 'Mailing address cannot be P.O. Box unless the member is homeless or travels in a RV. When entering an address, please do not enter any special characters (i.e, #,%,$)', isSummary: true
        },
        {
            order: 16, label: "Mailing City", value: "", type: "text", 
            fieldName: "City", pageNumber: 3,
            visibility: [
                {journey: "mailing", mode: "mailing"}, 
                {journey: "residential", mode: "mailing"}, 
                {journey: "temporary", mode: "mailingSecondary"},
            ],
            required: {value: true, message: "Mailing City is required"},
            renderLogic:{
                isCombo: false, isText: true, isTel: false, isEmail: false, isDate: false, 
                isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
            },
            templateModel: {
                avfRequestFieldName: 'MailingCityName', avfValue: '',
                avfSummary: { sectionName: 'Mailing Address Information', title: 'Medicare Address Update Summary' },
                expr: [
                    {
                        fnName: 'deriveValue',
                        fnArgs:  [
                            { order: 0, propertyName: "value", literal: false }
                        ],
                        fnType: "return",
                        fnOut: ["avfValue"],
                        fnOutSource: [16],
                        fnWhen: "template"
                    }
                ]
            },
            pattern: {value: undefined, message: undefined}, minLength: undefined, maxLength: 15,
            note: undefined, isSummary: true
        },
        {
            order: 17, label: "Mailing State", value: "", type: "combobox", 
            fieldName: "StateCode", pageNumber: 3,
            visibility: [
                {journey: "mailing", mode: "mailing"}, 
                {journey: "residential", mode: "mailing"}, 
                {journey: "temporary", mode: "mailingSecondary"},
            ],
            required: {value: true, message: "Mailing State is required"},
            renderLogic:{
                isCombo: true, isText: false, isTel: false, isEmail: false, isDate: false, 
                isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
            },
            templateModel: {
                avfRequestFieldName: 'MailingStateCode', avfValue: '',
                avfSummary: { sectionName: 'Mailing Address Information', title: 'Medicare Address Update Summary' },
                expr: [
                    {
                        fnName: 'deriveValue',
                        fnArgs:  [
                            { order: 0, propertyName: "value", literal: false }
                        ],
                        fnType: "return",
                        fnOut: ["avfValue"],
                        fnOutSource: [17],
                        fnWhen: "template"
                    }
                ]
            },
            pattern: {value: undefined, message: undefined}, minLength: undefined, maxLength: undefined,
            note: undefined, isSummary: true
        },
        {
            order: 18, label: "Mailing Zip Code", value: "", type: "text", 
            fieldName: "ZipCode", pageNumber: 3,
            visibility: [
                {journey: "mailing", mode: "mailing"}, 
                {journey: "residential", mode: "mailing"}, 
                {journey: "temporary", mode: "mailingSecondary"},
            ],
            required: {value: true, message: "Mailing Zip Code is required"},
            renderLogic:{
                isCombo: false, isText: true, isTel: false, isEmail: false, isDate: false, 
                isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
            },
            templateModel: {
                avfRequestFieldName: 'MailingZipCode', avfValue: '',
                avfSummary: { sectionName: 'Mailing Address Information', title: 'Medicare Address Update Summary' },
                expr: [
                    {
                        fnName: 'deriveValue',
                        fnArgs:  [
                            { order: 0, propertyName: "value", literal: false }
                        ],
                        fnType: "return",
                        fnOut: ["avfValue"],
                        fnOutSource: [18],
                        fnWhen: "template"
                    }
                ]
            },
            pattern: {value: zipcodeFormat, message: SearchVisitor_ZipCodeInvalidMsg_HUM}, 
            minLength: 5, maxLength: 9,
            note: undefined, isSummary: true
        },
        {
            order: 19, label: "Mailing County", value: "", type: "text", 
            fieldName: "CountyName", pageNumber: 3, renderButton: true, 
            visibility: [
                {journey: "mailing", mode: "mailing"}, 
                {journey: "residential", mode: "mailing"}, 
                {journey: "temporary", mode: "mailingSecondary"},
            ],
            required: {value: true, message: "Mailing County is required"},
            renderLogic:{
                isCombo: false, isText: true, isTel: false, isEmail: false, isDate: false, 
                isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
            },
            templateModel: {
                avfRequestFieldName: 'MailingCounty', avfValue: '',
                avfSummary: { sectionName: 'Mailing Address Information', title: 'Medicare Address Update Summary' },
                expr: [
                    {
                        fnName: 'deriveValue',
                        fnArgs:  [
                            { order: 0, propertyName: "value", literal: false }
                        ],
                        fnType: "return",
                        fnOut: ["avfValue"],
                        fnOutSource: [19],
                        fnWhen: "template"
                    }
                ]
            },
            expr: [
                {
                    fnName: "deducePredecessor", 
                    fnArgs:  [],
                    fnType: "return",
                    fnOut: "pagePredecessor",
                    fnOutSource: "parent",
                    fnWhen: "render"
                },
                {
                    fnName: "deduceSuccessor", 
                    fnArgs:  [
                        { order: 0, propertyName: "options", literal: false }
                    ],
                    fnType: "return",
                    fnOut: "pageSuccessor",
                    fnOutSource: "parent",
                    fnWhen: "change"
                }
            ],
            deducePredecessor: deducePredecessor,
            deduceSuccessor: deduceSuccessorFromMailing,
            pattern: {value: undefined, message: undefined}, 
            minLength: undefined, maxLength: undefined,
            note: "Enter the actual county name, NOT the code", isSummary: true
        },
        {
            order: 20, label: `Does the member have a temporary address?`, 
            value: "", 
            visibility: [
                {journey: "residential", mode: "mailing"},
                {journey: "residential", mode: "mailingTemporary"}
            ],
            type: "radioGroup", fieldName: "residentailMailDifferent", pageNumber: 3,
            required: {value: true, message: "Answer is required to proceed further"},
            renderLogic:{
                isCombo: false, isText: false, isTel: false, isEmail: false, isDate: false, 
                isCheck: false, isRadio: false, isRadioGroup: true
            },
            templateModel: {
                avfRequestFieldName: 'DoesMailMemberHaveTemporaryAddress', avfValue: '',
                avfSummary: { sectionName: 'Mailing Address Information', title: 'Medicare Address Update Summary' },
                expr: [
                    {
                        fnName: 'deriveValue',
                        fnArgs:  [
                            { order: 0, propertyName: "valueLabel", literal: false }
                        ],
                        fnType: "return",
                        fnOut: ["avfValue"],
                        fnOutSource: [20],
                        fnWhen: "template"
                    }
                ]
            },
            options: [
                { label: 'Yes', value: 'temporary', id: "radio-yes-1", checked: false },
                { label: 'No', value: 'summary', id: "radio-no-1", checked: false }
            ],
            expr: [
                {
                    fnName: "checkValidity", 
                    fnArgs:  [
                        { order: 0, propertyName: "options", literal: false },
                        { order: 1, propertyName: "required", literal: false }
                    ],
                    fnType: "return",
                    fnOut: "validityResult",
                    fnOutSource: "self",
                    fnWhen: "render"
                },
                {
                    fnName: "deduceCheck", 
                    fnArgs:  [
                        { order: 0, propertyName: "options", literal: false },
                        { order: 1, propertyName: "value", literal: false }
                    ],
                    fnType: "return",
                    fnOut: "options",
                    fnOutSource: "self",
                    fnWhen: "change"
                },
                {
                    fnName: "deducePredecessor", 
                    fnArgs:  [],
                    fnType: "return",
                    fnOut: "pagePredecessor",
                    fnOutSource: "parent",
                    fnWhen: "render"
                },
                {
                    fnName: "deduceSuccessor", 
                    fnArgs:  [
                        { order: 0, propertyName: "options", literal: false }
                    ],
                    fnType: "return",
                    fnOut: "pageSuccessor",
                    fnOutSource: "parent",
                    fnWhen: "change"
                }
            ],
            deduceCheck: deduceCheckHelper,
            checkValidity: checkValidity, 
            deducePredecessor: deducePredecessor,
            deduceSuccessor: deduceSuccessorFromLanding,
            validityResult: {},
            pattern: {value: undefined, message: undefined}, 
            minLength: undefined, maxLength: undefined,
            note: undefined, isSummary: true
        },
        {
            order: 21, label: `Does the member have a residential address different from their mailing address provided?`, 
            value: "", 
            visibility: [
                {journey: "mailing", mode: "mailing"}
            ],
            type: "radioGroup", fieldName: "residentailMailDifferent", pageNumber: 3,
            required: {value: true, message: "Answer is required to proceed further"},
            renderLogic:{
                isCombo: false, isText: false, isTel: false, isEmail: false, isDate: false, 
                isCheck: false, isRadio: false, isRadioGroup: true
            },
            templateModel: {
                avfRequestFieldName: 'DoesMemberHaveResAddressDiffFromMailAddress', avfValue: '',
                avfSummary: { sectionName: 'Mailing Address Information', title: 'Medicare Address Update Summary' },
                expr: [
                    {
                        fnName: 'deriveValue',
                        fnArgs:  [
                            { order: 0, propertyName: "valueLabel", literal: false }
                        ],
                        fnType: "return",
                        fnOut: ["avfValue"],
                        fnOutSource: [21],
                        fnWhen: "template"
                    }
                ]
            },
            options: [
                { label: 'Yes', value: 'residential', id: "radio-yes-1", checked: false },
                { label: 'No', value: 'residentialTemporary', id: "radio-no-1", checked: false }
            ],
            mapKey: "m2r",
            expr: [
                {
                    fnName: "checkValidity", 
                    fnArgs:  [
                        { order: 0, propertyName: "options", literal: false },
                        { order: 1, propertyName: "required", literal: false }
                    ],
                    fnType: "return",
                    fnOut: "validityResult",
                    fnOutSource: "self",
                    fnWhen: "render"
                },
                {
                    fnName: "deduceCheck", 
                    fnArgs:  [
                        { order: 0, propertyName: "options", literal: false },
                        { order: 1, propertyName: "value", literal: false },
                        { order: 2, propertyName: "m2r", literal: true },
                        { order: 3, propertyName: "radio-no-1", literal: true }
                    ],
                    fnType: "return",
                    fnOut: "options",
                    fnOutSource: "self",
                    fnWhen: "change"
                },
                {
                    fnName: "deducePredecessor", 
                    fnArgs:  [],
                    fnType: "return",
                    fnOut: "pagePredecessor",
                    fnOutSource: "parent",
                    fnWhen: "render"
                },
                {
                    fnName: "deduceSuccessor", 
                    fnArgs:  [
                        { order: 0, propertyName: "options", literal: false }
                    ],
                    fnType: "return",
                    fnOut: "pageSuccessor",
                    fnOutSource: "parent",
                    fnWhen: "change"
                }
            ],
            deduceCheck: deduceCheckHelper,
            checkValidity: checkValidity, 
            deducePredecessor: deducePredecessor,
            deduceSuccessor: deduceSuccessorFromLanding,
            validityResult: {},
            pattern: {value: undefined, message: undefined}, 
            minLength: undefined, maxLength: undefined,
            note: undefined, isSummary: true
        },
        {
            order: 22, label: "Temporary Address Line 1", value: "", type: "text", 
            fieldName: "AddressLine1", pageNumber: 4,
            visibility: [
                {journey: "residential", mode: "temporary"},
                {journey: "mailing", mode: "temporary"},
                {journey: "temporary", mode: "temporary"}
            ],
            required: {value: true, message: "Temporary Address is required"},
            renderLogic:{
                isCombo: false, isText: true, isTel: false, isEmail: false, isDate: false, 
                isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
            },
            templateModel: {
                avfRequestFieldName: 'TemporaryAddress', avfValue: '',
                avfSummary: { sectionName: 'Temporary Address Information', title: 'Medicare Address Update Summary' },
                expr: [
                    {
                        fnName: 'concatAddress',
                        fnArgs:  [
                            { order: 0, propertyName: "value", literal: false },
                            { order: 1, propertyName: 31, literal: true },
                            { order: 2, propertyName: "value", literal: true }
                        ],
                        fnType: "return",
                        fnOut: ["avfValue"],
                        fnOutSource: [22],
                        fnWhen: "template"
                    }
                ]
            },
            pattern: {value: undefined, message: undefined}, minLength: undefined, maxLength: 32,
            note: undefined, isSummary: true
        },
        {
            order: 31, label: "Temporary Address Line 2", value: "", type: "text", 
            fieldName: "AddressLine2", pageNumber: 4,
            visibility: [
                {journey: "residential", mode: "temporary"},
                {journey: "mailing", mode: "temporary"},
                {journey: "temporary", mode: "temporary"}
            ],
            required: {value: undefined, message: undefined},
            renderLogic:{
                isCombo: false, isText: true, isTel: false, isEmail: false, isDate: false, 
                isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
            },
            pattern: {value: undefined, message: undefined}, minLength: undefined, maxLength: 32,
            note: 'Temporary address cannot be P.O. Box unless the member is homeless or travels in a RV. When entering an address, please do not enter any special characters (i.e, #,%,$)', isSummary: true
        },
        {
            order: 23, label: "Temporary City", value: "", type: "text", 
            fieldName: "City", pageNumber: 4,
            visibility: [
                {journey: "residential", mode: "temporary"},
                {journey: "mailing", mode: "temporary"},
                {journey: "temporary", mode: "temporary"}
            ],
            required: {value: true, message: "Temporary City is required"},
            renderLogic:{
                isCombo: false, isText: true, isTel: false, isEmail: false, isDate: false, 
                isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
            },
            templateModel: {
                avfRequestFieldName: 'TemporaryCityName', avfValue: '',
                avfSummary: { sectionName: 'Temporary Address Information', title: 'Medicare Address Update Summary' },
                expr: [
                    {
                        fnName: 'deriveValue',
                        fnArgs:  [
                            { order: 0, propertyName: "value", literal: false }
                        ],
                        fnType: "return",
                        fnOut: ["avfValue"],
                        fnOutSource: [23],
                        fnWhen: "template"
                    }
                ]
            },
            pattern: {value: undefined, message: undefined}, minLength: undefined, maxLength: 15,
            note: undefined, isSummary: true
        },
        {
            order: 24, label: "Temporary State", value: "", type: "combobox", 
            fieldName: "StateCode", pageNumber: 4,
            visibility: [
                {journey: "residential", mode: "temporary"},
                {journey: "mailing", mode: "temporary"},
                {journey: "temporary", mode: "temporary"}
            ],
            required: {value: true, message: "Temporary State is required"},
            renderLogic:{
                isCombo: true, isText: false, isTel: false, isEmail: false, isDate: false, 
                isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
            },
            templateModel: {
                avfRequestFieldName: 'TemporaryStateCode', avfValue: '',
                avfSummary: { sectionName: 'Temporary Address Information', title: 'Medicare Address Update Summary' },
                expr: [
                    {
                        fnName: 'deriveValue',
                        fnArgs:  [
                            { order: 0, propertyName: "value", literal: false }
                        ],
                        fnType: "return",
                        fnOut: ["avfValue"],
                        fnOutSource: [24],
                        fnWhen: "template"
                    }
                ]
            },
            pattern: {value: undefined, message: undefined}, minLength: undefined, maxLength: undefined,
            note: undefined, isSummary: true
        },
        {
            order: 25, label: "Temporary Zip Code", value: "", type: "text", 
            fieldName: "ZipCode", pageNumber: 4,
            visibility: [
                {journey: "residential", mode: "temporary"},
                {journey: "mailing", mode: "temporary"},
                {journey: "temporary", mode: "temporary"}
            ],
            required: {value: true, message: "Temporary Zip Code is required"},
            renderLogic:{
                isCombo: false, isText: true, isTel: false, isEmail: false, isDate: false, 
                isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
            },
            templateModel: {
                avfRequestFieldName: 'TemporaryZipCode', avfValue: '',
                avfSummary: { sectionName: 'Temporary Address Information', title: 'Medicare Address Update Summary' },
                expr: [
                    {
                        fnName: 'deriveValue',
                        fnArgs:  [
                            { order: 0, propertyName: "value", literal: false }
                        ],
                        fnType: "return",
                        fnOut: ["avfValue"],
                        fnOutSource: [25],
                        fnWhen: "template"
                    }
                ]
            },
            pattern: {value: zipcodeFormat, message: SearchVisitor_ZipCodeInvalidMsg_HUM}, 
            minLength: 5, maxLength: 9,
            note: undefined, isSummary: true
        },
        {
            order: 26, label: "Temporary County", value: "", type: "text", 
            fieldName: "CountyName", pageNumber: 4, renderButton: true, osaRender: true, 
            visibility: [
                {journey: "residential", mode: "temporary"},
                {journey: "mailing", mode: "temporary"},
                {journey: "temporary", mode: "temporary"}
            ],
            required: {value: true, message: "Temporary County is required"},
            renderLogic:{
                isCombo: false, isText: true, isTel: false, isEmail: false, isDate: false, 
                isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
            },
            templateModel: {
                avfRequestFieldName: 'TemporaryCountyName', avfValue: '',
                avfSummary: { sectionName: 'Temporary Address Information', title: 'Medicare Address Update Summary' },
                expr: [
                    {
                        fnName: 'deriveValue',
                        fnArgs:  [
                            { order: 0, propertyName: "value", literal: false }
                        ],
                        fnType: "return",
                        fnOut: ["avfValue"],
                        fnOutSource: [26],
                        fnWhen: "template"
                    }
                ]
            },
            pattern: {value: undefined, message: undefined}, 
            minLength: undefined, maxLength: undefined,
            note: "Enter the actual county name, NOT the code", isSummary: true,
            osa: {isOSA: undefined, message: undefined, class: undefined}
        },
        {
            order: 27, label: "Temporary Absence Start Date", value: "", type: "date",
            fieldName: 'resdientialLivingSince', pageNumber: 4, 
            visibility: [
                {journey: "residential", mode: "temporary"},
                {journey: "mailing", mode: "temporary"},
                {journey: "temporary", mode: "temporary"}
            ],
            required: { value: true, message: "Absence Start Date is required" }, 
            renderLogic:{
                isCombo: false, isText: false, isTel: false, isEmail: false, isDate: true, 
                isCheck: false
            },
            templateModel: {
                avfRequestFieldName: 'TemporaryOSAStartDate', avfValue: '',
                avfSummary: { sectionName: 'Temporary Address Information', title: 'Medicare Address Update Summary' },
                expr: [
                    {
                        fnName: 'deriveValue',
                        fnArgs:  [
                            { order: 0, propertyName: "value", literal: false }
                        ],
                        fnType: "return",
                        fnOut: ["avfValue"],
                        fnOutSource: [27],
                        fnWhen: "template"
                    }
                ]
            },
            minValue: undefined,
            pattern: {value: undefined, message: undefined}, minLength: undefined, maxLength: undefined,
            note: undefined, isSummary: true
        },
        {
            order: 28, label: "Temporary Absence End Date", value: "", type: "date",
            fieldName: 'resdientialLivingSince', pageNumber: 4, 
            visibility: [
                {journey: "residential", mode: "temporary"},
                {journey: "mailing", mode: "temporary"},
                {journey: "temporary", mode: "temporary"}
            ],
            required: { value: true, message: "Absence End Date is required" }, 
            renderLogic:{
                isCombo: false, isText: false, isTel: false, isEmail: false, isDate: true, 
                isCheck: false
            },
            templateModel: {
                avfRequestFieldName: 'TemporaryOSAEndDate', avfValue: '',
                avfSummary: { sectionName: 'Temporary Address Information', title: 'Medicare Address Update Summary' },
                expr: [
                    {
                        fnName: 'deriveValue',
                        fnArgs:  [
                            { order: 0, propertyName: "value", literal: false }
                        ],
                        fnType: "return",
                        fnOut: ["avfValue"],
                        fnOutSource: [28],
                        fnWhen: "template"
                    }
                ]
            },
            expr: [
                {
                    fnName: "deducePredecessor", 
                    fnArgs:  [],
                    fnType: "return",
                    fnOut: "pagePredecessor",
                    fnOutSource: "parent",
                    fnWhen: "render"
                },
                {
                    fnName: "deduceSuccessor", 
                    fnArgs:  [],
                    fnType: "return",
                    fnOut: "pageSuccessor",
                    fnOutSource: "parent",
                    fnWhen: "change"
                },
                {
                    fnName: "validateAbsenceEndDate", 
                    fnArgs:  [
                        { order: 0, propertyName: 27, literal: true }
                    ],
                    fnType: "return",
                    fnOut: "minValue",
                    fnOutSource: "self",
                    fnWhen: "render"
                }
            ],
            deducePredecessor: deducePredecessor,
            deduceSuccessor: deduceSuccessorFromTemporary,
            validateAbsenceEndDate: validateAbsenceEndDate,
            minValue: undefined,
            pattern: {value: undefined, message: undefined}, minLength: undefined, maxLength: undefined,
            note: undefined, isSummary: true
        }
    ];
}

const flowButtonModel = () => {
    return [
        {
            label: "Verify Residential Address", title: "Standardize Address", 
            order: 1, setDisable: undefined, displayMode: "residential", 
            pageNumber: 2, 
            identifier: {type: "callout", value: "standardizeAddress"},
            class: undefined, requestAVObj: AddressListRequest, 
            expr: [
                {
                    fnName: "checkValidity", 
                    fnArgs:  [
                        { order: 0, propertyName: "9", literal: true },
                        { order: 1, propertyName: "5,6,7,8,9", literal: true },
                        { order: 2, propertyName: "displayMode", literal: false }
                    ],
                    fnType: "return",
                    fnOut: "setDisable",
                    fnOutSource: "self",
                    fnWhen: "render"
                },
                {
                    fnName: "deduceClass", 
                    fnArgs:  [
                        { order: 0, propertyName: "setDisable", literal: false }
                    ],
                    fnType: "return",
                    fnOut: "class",
                    fnOutSource: "self",
                    fnWhen: "render"
                },
                {
                    fnName: "deriveAVRequestObj", 
                    fnArgs:  [
                        { order: 0, propertyName: "requestAVObj", literal: false }
                    ],
                    fnType: "return",
                    fnOut: "requestAVObj",
                    fnOutSource: "self",
                    fnWhen: "render"
                }
            ],
            checkValidity: checkBtnValidity,
            deduceClass: deduceVerifyBtnClass,
            deriveAVRequestObj: deduceAVRequestObj
        },
        {
            label: "Verify Mailing Address", title: "Standardize Address", 
            order: 2, setDisable: undefined, displayMode: "mailing", 
            pageNumber: 3, 
            identifier: {type: "callout", value: "standardizeAddress"},
            class: undefined, requestAVObj: AddressListRequest, 
            expr: [
                {
                    fnName: "checkValidity", 
                    fnArgs:  [
                        { order: 0, propertyName: "19", literal: true },
                        { order: 1, propertyName: "15,16,17,18,19", literal: true },
                        { order: 2, propertyName: "displayMode", literal: false }
                    ],
                    fnType: "return",
                    fnOut: "setDisable",
                    fnOutSource: "self",
                    fnWhen: "render"
                },
                {
                    fnName: "deduceClass", 
                    fnArgs:  [
                        { order: 0, propertyName: "setDisable", literal: false }
                    ],
                    fnType: "return",
                    fnOut: "class",
                    fnOutSource: "self",
                    fnWhen: "render"
                },
                {
                    fnName: "deriveAVRequestObj", 
                    fnArgs:  [
                        { order: 0, propertyName: "requestAVObj", literal: false }
                    ],
                    fnType: "return",
                    fnOut: "requestAVObj",
                    fnOutSource: "self",
                    fnWhen: "render"
                }
            ],
            checkValidity: checkBtnValidity,
            deduceClass: deduceVerifyBtnClass,
            deriveAVRequestObj: deduceAVRequestObj
        },
        {
            label: "Verify Temporary Address", title: "Standardize Address", 
            order: 3, setDisable: undefined, displayMode: "temporary", 
            pageNumber: 4, 
            identifier: {type: "callout", value: "standardizeAddress"},
            class: undefined, requestAVObj: AddressListRequest, 
            expr: [
                {
                    fnName: "checkValidity", 
                    fnArgs:  [
                        { order: 0, propertyName: "26", literal: true },
                        { order: 1, propertyName: "22,23,24,25,26", literal: true },
                        { order: 2, propertyName: "displayMode", literal: false }
                    ],
                    fnType: "return",
                    fnOut: "setDisable",
                    fnOutSource: "self",
                    fnWhen: "render"
                },
                {
                    fnName: "deduceClass", 
                    fnArgs:  [
                        { order: 0, propertyName: "setDisable", literal: false }
                    ],
                    fnType: "return",
                    fnOut: "class",
                    fnOutSource: "self",
                    fnWhen: "render"
                },
                {
                    fnName: "deriveAVRequestObj", 
                    fnArgs:  [
                        { order: 0, propertyName: "requestAVObj", literal: false }
                    ],
                    fnType: "return",
                    fnOut: "requestAVObj",
                    fnOutSource: "self",
                    fnWhen: "render"
                }
            ],
            checkValidity: checkBtnValidity,
            deduceClass: deduceVerifyBtnClass,
            deriveAVRequestObj: deduceAVRequestObj
        }
    ];
}

const flowPageModel = () => {
    return [
        {
            pageName: "landing",
            pageNumber: 1, 
            pageFeatures: {
                title: "Interaction and Address Update Request",
                buttons: [
                    {
                        label: "Next", title: "Next", order: 1, setDisable: true,
                        identifier: {type: "navigation", value: "next"}
                    }
                ],
                buttonMsgs: [{ display: false, message: 'Click Next' }],
                fields: flowFieldModel.apply(this).filter(flowField => flowField.pageNumber === 1)
            },
            isOSARequired: false,
            pageSuccessor: undefined,
            pagePredecessor: undefined,
            pageLanding: true
        },
        {
            pageName: "residential",
            pageNumber: 2, 
            pageFeatures: {
                title: "Residential",
                buttons: [
                    {
                        label: "Previous", title: "Previous", order: 1, setDisable: false,
                        identifier: {type: "navigation", value: "previous"},
                        class: "slds-m-left_x-small"
                    },
                    {
                        label: "Next", title: "Next", order: 2, setDisable: true,
                        identifier: {type: "navigation", value: "next"},
                        class: "slds-m-left_x-small"
                    }
                ],
                buttonMsgs: [{ display: true, message: 'After the address is verified, click Next' }],
                fields: flowFieldModel.apply(this).filter(flowField => flowField.pageNumber === 2)
            },
            isOSARequired: true,
            pageSuccessor: undefined,
            pagePredecessor: undefined,
            pageLanding: false
        },
        {
            pageName: "mailing",
            pageNumber: 3, 
            pageFeatures: {
                title: "Mailing Address Information",
                buttons: [
                    {
                        label: "Previous", title: "Previous", order: 1, setDisable: false,
                        identifier: {type: "navigation", value: "previous"},
                        class: "slds-m-left_x-small"
                    },
                    {
                        label: "Next", title: "Next", order: 2, setDisable: true,
                        identifier: {type: "navigation", value: "next"},
                        class: "slds-m-left_x-small"
                    }
                ],
                buttonMsgs: [{ display: true, message: 'After the address is verified, click Next' }],
                fields: flowFieldModel.apply(this).filter(flowField => flowField.pageNumber === 3)
            },
            isOSARequired: false,
            pageSuccessor: undefined,
            pagePredecessor: undefined,
            pageLanding: false
        },
        {
            pageName: "temporary",
            pageNumber: 4, 
            pageFeatures: {
                title: "Temporary Address",
                buttons: [
                    {
                        label: "Previous", title: "Previous", order: 1, setDisable: false,
                        identifier: {type: "navigation", value: "previous"},
                        class: "slds-m-left_x-small"
                    },
                    {
                        label: "Next", title: "Next", order: 2, setDisable: true,
                        identifier: {type: "navigation", value: "next"},
                        class: "slds-m-left_x-small"
                    }
                ],
                buttonMsgs: [{ display: true, message: 'After the address is verified, click Next' }],
                fields: flowFieldModel.apply(this).filter(flowField => flowField.pageNumber === 4)
            },
            isOSARequired: true,
            pageSuccessor: undefined,
            pagePredecessor: undefined,
            pageLanding: false
        },
        {
            pageName: "summary",
            pageNumber: 5, 
            pageFeatures: {
                title: "Summary",
                buttons: [
                    {
                        label: "Previous", title: "Previous", order: 1, setDisable: false,
                        identifier: {type: "navigation", value: "previous"},
                        class: "slds-m-left_x-small"
                    },
                    {
                        label: "Submit", title: "Submit", order: 2, setDisable: true,
                        identifier: {type: "navigation", value: "finish"},
                        class: "slds-m-left_x-small"
                    }
                ],
                buttonMsgs: [
                    { display: false, message: 'Click Submit to process the changes or Click Previous to go back and make changes.' },
                    { display: false, message: 'Changes cannot be made to this request after Submit is clicked.' }
                ],
                summaryMsg: true,
                fields: flowFieldModel.apply(this).filter(flowField => flowField.visible === true),
            },
            isOSARequired: false,
            pageSuccessor: undefined,
            pagePredecessor: undefined,
            pageLanding: false
        },
        {
            pageName: "proxy",
            pageNumber: 6, 
            pageFeatures: {
                title: "Residential",
                buttons: [
                    {
                        label: "Previous", title: "Previous", order: 1, setDisable: false,
                        identifier: {type: "navigation", value: "previous"},
                        class: "slds-m-left_x-small"
                    },
                    {
                        label: "Next", title: "Next", order: 2, setDisable: true,
                        identifier: {type: "navigation", value: "next"},
                        class: "slds-m-left_x-small"
                    }
                ],
                buttonMsgs: [{ display: false, message: 'Click Next' }],
                fields: flowFieldModel.apply(this).filter(flowField => flowField.pageNumber === 6)
            },
            isOSARequired: false,
            pageSuccessor: undefined,
            pagePredecessor: undefined,
            pageLanding: false
        }
    ];
};

const breadCrumbs = function() {
};

const modalUSPSErrorModel = () => {
    return {
        heading: undefined,
        isError: true,
        dataList: [],
        buttons: [],
        error: {
            message: "The address you entered is not valid. Please enter a valid address.",
            buttons: [
                {
                    label: "Close",
                    order: 1,
                    variant: "brand",
                    eventName: "uspsReject",
                    dispatchEventName: "uspsactiontrigger",
                    internal: false
                }
            ]
        }
    };
};

const modalUSPSDataModel = () => {
    return {
        heading: "Per USPS, did you mean?",
        isError: false,
        dataList: [
            {
                order: 1,
                displayData: undefined
            },
            {
                order: 2,
                displayData: undefined
            }
        ],
        buttons: [
            {
                label: "Select",
                order: 1,
                variant: "brand",
                eventName: "uspsAccept",
                dispatchEventName: "uspsactiontrigger",
                internal: false,
                btnClass: 'pub-modal-btn-div btn-yes',
                display: true,
                showMessage: false,
                message: undefined
            },
            {
                label: "Select",
                order:2,
                variant: "brand",
                eventName: undefined,
                dispatchEventName: undefined,
                internal: true,
                btnClass: 'pub-modal-btn-div btn-no',
                display: true,
                showMessage: false,
                message: OSAUndetermineMessage,
                fallback: { eventName: 'uspsReject', dispatchEventName: 'uspsactiontrigger' }
            }
        ],
        error: {
            message: OSAUndetermineMessage,
            buttons: [
                {
                    label: "Back",
                    order: 3,
                    variant: "brand",
                    eventName: undefined,
                    dispatchEventName: undefined,
                    internal: true,
                    btnClass: 'pub-modal-btn-div',
                    display: true
                },
                {
                    label: "Ok",
                    order: 4,
                    variant: "brand",
                    eventName: "uspsReject",
                    dispatchEventName: "uspsactiontrigger",
                    internal: false,
                    btnClass: 'pub-modal-btn-div',
                    display: true
                },
            ]
        }
    };
}

export {
    flowRenderObject,
    flowPageModel,
    flowFieldModel,
    commonSelector,
    breadCrumbs,
    flowButtonModel,
    flowButtonRenderObject,
    modalUSPSDataModel,
    modalUSPSErrorModel,
    copyFieldMap
}