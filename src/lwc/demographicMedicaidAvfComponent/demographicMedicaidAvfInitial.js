import { phoneFormat, zipcodeFormat } from 'c/crmserviceHelper';
import { AddressListRequest } from './demographicMedicaidAvfModel';
import { deducePredecessor, deduceSuccessorFromLanding, deduceSuccessorFromTemporary } from './demographicMedicaidAvfHelper';
import { checkValidity, deduceCheckHelper, formatPhoneNumber, checkBtnValidity, deduceVerifyBtnClass, deduceAVRequestObj } from './demographicMedicaidAvfHelper';
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
    r2m: { 2: 9, 3: 10, 4: 11, 5: 12, 6: 13, 7: 14 },
    m2r: { 9: 2, 10: 3, 11: 4, 12: 5, 13: 6, 14: 7 }
};

const flowFieldModel = () => {
    return [
        {
            order: 1, label: "Which address do you want to update?", value: "", 
            type: "radioGroup", fieldName: "addressUdpateType", pageNumber: 1,
            required: {value: true, message: "Atleast select one address type option"},
            renderLogic:{
                isCombo: false, isText: false, isTel: false, isEmail: false, isDate: false, 
                isCheck: false, isRadio: false, isRadioGroup: true
            },
            options: [
                { label: 'Residential', value: 'residential', id: "radio-res-1", checked: false },
                { label: 'Mailing', value: 'mailing', id: "radio-mail-1", checked: false },
                { label: 'Both', value: 'both', id: "radio-temp-1", checked: false }
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
            order: 2, label: "Residential Address 1", value: "", type: "text", 
            fieldName: "AddressLine1", pageNumber: 2, 
            visibility: [
                {journey: "residential", mode: "residential"}, 
                {journey: "mailing", mode: "residential"}, 
                {journey: "both", mode: "residential"}
            ],
            required: {value: true, message: "Residential Address 1 is required"},
            renderLogic:{
                isCombo: false, isText: true, isTel: false, isEmail: false, isDate: false, 
                isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
            },
            templateModel: {
                avfRequestFieldName: 'PermanentResidentialAddress', avfValue: '',
                avfSummary: { sectionName: 'Residential Address Information', title: 'Medicaid Address Update Summary' },
                expr: [
                    {
                        fnName: 'concatAddress',
                        fnArgs:  [
                            { order: 0, propertyName: "value", literal: false },
                            { order: 1, propertyName: 3, literal: true },
                            { order: 2, propertyName: "value", literal: true },
                            { order: 3, propertyName: "mdu", literal: true },
                        ],
                        fnType: "return",
                        fnOut: ["avfValue"],
                        fnOutSource: [2],
                        fnWhen: "template"
                    }
                ]
            },
            updateModel: {
                expr: [
                    {
                        fnName: 'deriveAddressRequestValue',
                        fnArgs:  [
                            { order: 0, propertyName: [2, 3, 4, 5, 6, 7], literal: true },
                            { order: 1, propertyName: ["line1", "line2", "city", "statecode", "zipcode", "countyname"], literal: true },
                            { order: 2, propertyName: "demographics", literal: true },
                            { order: 3, propertyName: 'member', literal: true },
                            { order: 4, propertyName: 'address', literal: true },
                            { order: 5, propertyName: 'type', literal: true },
                            { order: 6, propertyName: '03', literal: true },
                            { order: 7, propertyName: 'mdu', literal: true }
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
                            { order: 1, propertyName: 2, literal: true },
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
            order: 3, label: "Residential Address 2", value: "", type: "text", 
            fieldName: "AddressLine2", pageNumber: 2, 
            visibility: [
                {journey: "residential", mode: "residential"}, 
                {journey: "mailing", mode: "residential"}, 
                {journey: "both", mode: "residential"}
            ],
            required: {value: undefined, message: undefined},
            renderLogic:{
                isCombo: false, isText: true, isTel: false, isEmail: false, isDate: false, 
                isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
            },
            pattern: {value: undefined, message: undefined}, minLength: undefined, maxLength: 32,
            note: undefined, isSummary: true
        },
        {
            order: 4, label: "Residential City", value: "", type: "text", 
            fieldName: "City", pageNumber: 2,
            visibility: [
                {journey: "residential", mode: "residential"}, 
                {journey: "mailing", mode: "residential"}, 
                {journey: "both", mode: "residential"}
            ],
            required: {value: true, message: "Residential City is required"},
            renderLogic:{
                isCombo: false, isText: true, isTel: false, isEmail: false, isDate: false, 
                isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
            },
            templateModel: {
                avfRequestFieldName: 'PermanentResidentialCityName', avfValue: '',
                avfSummary: { sectionName: 'Residential Address Information', title: 'Medicaid Address Update Summary' },
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
            pattern: {value: undefined, message: undefined}, minLength: undefined, maxLength: 15,
            note: undefined, isSummary: true
        },
        {
            order: 5, label: "Residential State", value: "", type: "combobox", 
            fieldName: "StateCode", pageNumber: 2, 
            visibility: [
                {journey: "residential", mode: "residential"}, 
                {journey: "mailing", mode: "residential"}, 
                {journey: "both", mode: "residential"}
            ],
            required: {value: true, message: "Residential State is required"},
            renderLogic:{
                isCombo: true, isText: false, isTel: false, isEmail: false, isDate: false, 
                isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
            },
            templateModel: {
                avfRequestFieldName: 'PermanentResidentialStateCode', avfValue: '',
                avfSummary: { sectionName: 'Residential Address Information', title: 'Medicaid Address Update Summary' },
                expr: [
                    {
                        fnName: 'deriveValue',
                        fnArgs:  [
                            { order: 0, propertyName: "value", literal: false }
                        ],
                        fnType: "return",
                        fnOut: ["avfValue"],
                        fnOutSource: [5],
                        fnWhen: "template"
                    }
                ]
            },
            pattern: {value: undefined, message: undefined}, minLength: undefined, maxLength: undefined,
            note: undefined, isSummary: true
        },
        {
            order: 6, label: "Residential Zip Code", value: "", type: "text", 
            fieldName: "ZipCode", pageNumber: 2,
            visibility: [
                {journey: "residential", mode: "residential"}, 
                {journey: "mailing", mode: "residential"}, 
                {journey: "both", mode: "residential"}
            ],
            required: {value: true, message: "Residential Zip Code is required"},
            renderLogic:{
                isCombo: false, isText: true, isTel: false, isEmail: false, isDate: false, 
                isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
            },
            templateModel: {
                avfRequestFieldName: 'PermanentResidentialZipCode', avfValue: '',
                avfSummary: { sectionName: 'Residential Address Information', title: 'Medicaid Address Update Summary' },
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
            pattern: {value: zipcodeFormat, message: SearchVisitor_ZipCodeInvalidMsg_HUM}, 
            minLength: 5, maxLength: 9,
            note: undefined, isSummary: true
        },
        {
            order: 7, label: "Residential County", value: "", type: "text", 
            fieldName: "CountyName", pageNumber: 2, renderButton: true, 
            visibility: [ 
                {journey: "residential", mode: "residential"}, 
                {journey: "mailing", mode: "residential"}, 
                {journey: "both", mode: "residential"}
            ],
            required: {value: true, message: "Residential County is required"},
            renderLogic:{
                isCombo: false, isText: true, isTel: false, isEmail: false, isDate: false, 
                isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
            },
            templateModel: {
                avfRequestFieldName: 'PermanentResidentialCountyName', avfValue: '',
                avfSummary: { sectionName: 'Residential Address Information', title: 'Medicaid Address Update Summary' },
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
            pattern: {value: undefined, message: undefined}, 
            minLength: undefined, maxLength: undefined,
            note: undefined, isSummary: true,
            osa: {isOSA: undefined, message: undefined, class: undefined}
        },
        {
            order: 8, label: `Mailing Address same as Residential Address?`, 
            value: "",
            visibility: [
                {journey: "residential", mode: "residential"},
                {journey: "both", mode: "residential"}
            ],
            type: "radioGroup", fieldName: "residentailMailDifferent", pageNumber: 2,
            required: {value: true, message: "Answer is required to proceed further"},
            renderLogic:{
                isCombo: false, isText: false, isTel: false, isEmail: false, isDate: false, 
                isCheck: false, isRadio: false, isRadioGroup: true
            },
            templateModel: {
                avfRequestFieldName: 'DoesMemberHaveMailAddressSameAsPermAddress', avfValue: '',
                avfSummary: { sectionName: 'Residential Address Information', title: 'Medicaid Address Update Summary' },
                expr: [
                    {
                        fnName: 'deriveValue',
                        fnArgs:  [
                            { order: 0, propertyName: "valueLabel", literal: false }
                        ],
                        fnType: "return",
                        fnOut: ["avfValue"],
                        fnOutSource: [8],
                        fnWhen: "template"
                    }
                ]
            },
            options: [
                { label: 'Yes', value: 'summary', id: "radio-1", checked: false },
                { label: 'No', value: 'skip', id: "radio-2", checked: false }
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
                        { order: 1, propertyName: "value", literal: false },
                        { order: 2, propertyName: "r2m", literal: true },
                        { order: 2, propertyName: "radio-1", literal: true }
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
            order: 9, label: "Mailing Address 1", value: "", type: "text", 
            fieldName: "AddressLine1", pageNumber: 3,
            visibility: [
                {journey: "residential", mode: "mailing"}, 
                {journey: "mailing", mode: "mailing"},
                {journey: "both", mode: "mailing"},
            ],
            required: {value: true, message: "Mailing Address 1 is required"},
            renderLogic:{
                isCombo: false, isText: true, isTel: false, isEmail: false, isDate: false, 
                isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
            },
            templateModel: {
                avfRequestFieldName: 'MailingAddress', avfValue: '',
                avfSummary: { sectionName: 'Mailing Address Information', title: 'Medicaid Address Update Summary' },
                expr: [
                    {
                        fnName: 'concatAddress',
                        fnArgs:  [
                            { order: 0, propertyName: "value", literal: false },
                            { order: 1, propertyName: 10, literal: true },
                            { order: 2, propertyName: "value", literal: true },
                            { order: 3, propertyName: "mdu", literal: true },
                        ],
                        fnType: "return",
                        fnOut: ["avfValue"],
                        fnOutSource: [9],
                        fnWhen: "template"
                    }
                ]
            },
            updateModel: {
                expr: [
                    {
                        fnName: 'deriveAddressRequestValue',
                        fnArgs:  [
                            { order: 0, propertyName: [9, 10, 11, 12, 13, 14], literal: true },
                            { order: 1, propertyName: ["line1", "line2", "city", "statecode", "zipcode", "countyname"], literal: true },
                            { order: 2, propertyName: "demographics", literal: true },
                            { order: 3, propertyName: 'member', literal: true },
                            { order: 4, propertyName: 'address', literal: true },
                            { order: 5, propertyName: 'type', literal: true },
                            { order: 6, propertyName: '01', literal: true },
                            { order: 7, propertyName: 'mdu', literal: true }
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
                            { order: 1, propertyName: 2, literal: true },
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
            order: 10, label: "Mailing Address 2", value: "", type: "text", 
            fieldName: "AddressLine2", pageNumber: 3,
            visibility: [
                {journey: "residential", mode: "mailing"}, 
                {journey: "mailing", mode: "mailing"},
                {journey: "both", mode: "mailing"},
            ],
            required: {value: undefined, message: undefined},
            renderLogic:{
                isCombo: false, isText: true, isTel: false, isEmail: false, isDate: false, 
                isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
            },
            pattern: {value: undefined, message: undefined}, minLength: undefined, maxLength: 32,
            note: undefined, isSummary: true
        },
        {
            order: 11, label: "Mailing City", value: "", type: "text", 
            fieldName: "City", pageNumber: 3,
            visibility: [
                {journey: "residential", mode: "mailing"}, 
                {journey: "mailing", mode: "mailing"},
                {journey: "both", mode: "mailing"},
            ],
            required: {value: true, message: "Mailing City is required"},
            renderLogic:{
                isCombo: false, isText: true, isTel: false, isEmail: false, isDate: false, 
                isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
            },
            templateModel: {
                avfRequestFieldName: 'MailingCityName', avfValue: '',
                avfSummary: { sectionName: 'Mailing Address Information', title: 'Medicaid Address Update Summary' },
                expr: [
                    {
                        fnName: 'deriveValue',
                        fnArgs:  [
                            { order: 0, propertyName: "value", literal: false }
                        ],
                        fnType: "return",
                        fnOut: ["avfValue"],
                        fnOutSource: [11],
                        fnWhen: "template"
                    }
                ]
            },
            pattern: {value: undefined, message: undefined}, minLength: undefined, maxLength: 15,
            note: undefined, isSummary: true
        },
        {
            order: 12, label: "Mailing State", value: "", type: "combobox", 
            fieldName: "StateCode", pageNumber: 3,
            visibility: [
                {journey: "residential", mode: "mailing"}, 
                {journey: "mailing", mode: "mailing"},
                {journey: "both", mode: "mailing"},
            ],
            required: {value: true, message: "Mailing State is required"},
            renderLogic:{
                isCombo: true, isText: false, isTel: false, isEmail: false, isDate: false, 
                isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
            },
            templateModel: {
                avfRequestFieldName: 'MailingStateCode', avfValue: '',
                avfSummary: { sectionName: 'Mailing Address Information', title: 'Medicaid Address Update Summary' },
                expr: [
                    {
                        fnName: 'deriveValue',
                        fnArgs:  [
                            { order: 0, propertyName: "value", literal: false }
                        ],
                        fnType: "return",
                        fnOut: ["avfValue"],
                        fnOutSource: [12],
                        fnWhen: "template"
                    }
                ]
            },
            pattern: {value: undefined, message: undefined}, minLength: undefined, maxLength: undefined,
            note: undefined, isSummary: true
        },
        {
            order: 13, label: "Mailing Zip Code", value: "", type: "text", 
            fieldName: "ZipCode", pageNumber: 3,
            visibility: [
                {journey: "residential", mode: "mailing"}, 
                {journey: "mailing", mode: "mailing"}, 
                {journey: "both", mode: "mailing"},
            ],
            required: {value: true, message: "Mailing Zip Code is required"},
            renderLogic:{
                isCombo: false, isText: true, isTel: false, isEmail: false, isDate: false, 
                isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
            },
            templateModel: {
                avfRequestFieldName: 'MailingZipCode', avfValue: '',
                avfSummary: { sectionName: 'Mailing Address Information', title: 'Medicaid Address Update Summary' },
                expr: [
                    {
                        fnName: 'deriveValue',
                        fnArgs:  [
                            { order: 0, propertyName: "value", literal: false }
                        ],
                        fnType: "return",
                        fnOut: ["avfValue"],
                        fnOutSource: [13],
                        fnWhen: "template"
                    }
                ]
            },
            pattern: {value: zipcodeFormat, message: SearchVisitor_ZipCodeInvalidMsg_HUM}, 
            minLength: 5, maxLength: 9,
            note: undefined, isSummary: true
        },
        {
            order: 14, label: "Mailing County", value: "", type: "text", 
            fieldName: "CountyName", pageNumber: 3, renderButton: true, 
            visibility: [
                {journey: "residential", mode: "mailing"}, 
                {journey: "mailing", mode: "mailing"}, 
                {journey: "both", mode: "mailing"},
            ],
            required: {value: true, message: "Mailing County is required"},
            renderLogic:{
                isCombo: false, isText: true, isTel: false, isEmail: false, isDate: false, 
                isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
            },
            templateModel: {
                avfRequestFieldName: 'MailingCounty', avfValue: '',
                avfSummary: { sectionName: 'Mailing Address Information', title: 'Medicaid Address Update Summary' },
                expr: [
                    {
                        fnName: 'deriveValue',
                        fnArgs:  [
                            { order: 0, propertyName: "value", literal: false }
                        ],
                        fnType: "return",
                        fnOut: ["avfValue"],
                        fnOutSource: [14],
                        fnWhen: "template"
                    }
                ]
            },
            pattern: {value: undefined, message: undefined}, 
            minLength: undefined, maxLength: undefined,
            note: undefined, isSummary: true
        },
        {
            order: 15, label: `Residential Address same as Mailing Address?`, 
            value: "",
            visibility: [
                { journey: "mailing", mode: "mailing" }
            ],
            type: "radioGroup", fieldName: "residentailMailDifferent", pageNumber: 3,
            required: {value: true, message: "Answer is required to proceed further"},
            renderLogic:{
                isCombo: false, isText: false, isTel: false, isEmail: false, isDate: false, 
                isCheck: false, isRadio: false, isRadioGroup: true
            },
            templateModel: {
                avfRequestFieldName: 'DoesMemberHaveResAddressSameAsMailAddress', avfValue: '',
                avfSummary: { sectionName: 'Mailing Address Information', title: 'Medicaid Address Update Summary' },
                expr: [
                    {
                        fnName: 'deriveValue',
                        fnArgs:  [
                            { order: 0, propertyName: "valueLabel", literal: false }
                        ],
                        fnType: "return",
                        fnOut: ["avfValue"],
                        fnOutSource: [15],
                        fnWhen: "template"
                    }
                ]
            },
            options: [
                { label: 'Yes', value: 'summary', id: "radio-6", checked: false },
                { label: 'No', value: 'skip', id: "radio-7", checked: false }
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
                        { order: 1, propertyName: "value", literal: false },
                        { order: 2, propertyName: "m2r", literal: true },
                        { order: 3, propertyName: "radio-6", literal: true }
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
                        { order: 0, propertyName: "7", literal: true },
                        { order: 1, propertyName: "2,4,5,6,7", literal: true },
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
                        { order: 0, propertyName: "14", literal: true },
                        { order: 1, propertyName: "9,11,12,13,14", literal: true },
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
                title: "Medicare Supplement or Medicaid Plans Address Update",
                buttons: [
                    {
                        label: "Next", title: "Next", order: 1, setDisable: true,
                        identifier: {type: "navigation", value: "next"},
                        buttonMsg: { display: true, message: 'Click Next' }
                    }
                ],
                buttonMsgs: [ { display: true, message: 'Click Next' } ],
                fields: flowFieldModel.apply(this).filter(flowField => flowField.pageNumber === 1)
            },
            isOSARender: false,
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
                        class: "slds-m-left_x-small",
                        buttonMsg: { display: false, message: 'After the address is verified, click Next' }
                    },
                    {
                        label: "Next", title: "Next", order: 2, setDisable: true,
                        identifier: {type: "navigation", value: "next"},
                        class: "slds-m-left_x-small",
                        buttonMsgs: { display: true, message: 'After the address is verified, click Next' }
                    }
                ],
                buttonMsgs: [{ display: true, message: 'After the address is verified, click Next' }],
                fields: flowFieldModel.apply(this).filter(flowField => flowField.pageNumber === 2)
            },
            isOSARender: false,
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
                        class: "slds-m-left_x-small",
                        buttonMsg: { display: false, message: 'After the address is verified, click Next' }
                    },
                    {
                        label: "Next", title: "Next", order: 2, setDisable: true,
                        identifier: {type: "navigation", value: "next"},
                        class: "slds-m-left_x-small",
                        buttonMsg: { display: true, message: 'After the address is verified, click Next' }
                    }
                ],
                buttonMsgs: [{ display: true, message: 'After the address is verified, click Next' }],
                fields: flowFieldModel.apply(this).filter(flowField => flowField.pageNumber === 3)
            },
            isOSARender: false,
            pageSuccessor: undefined,
            pagePredecessor: undefined,
            pageLanding: false
        },
        {
            pageName: "summary",
            pageNumber: 4, 
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
                buttonMsgs: [{ display: false, message: 'After the address is verified, click Next' }],
                summaryMsg: true,
                fields: flowFieldModel.apply(this).filter(flowField => flowField.visible === true),
            },
            isOSARender: false,
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
                order: 2,
                variant: "brand",
                eventName: 'uspsReject',
                dispatchEventName: "uspsactiontrigger",
                internal: false,
                btnClass: 'pub-modal-btn-div btn-no',
                display: true,
                showMessage: false,
                message: OSAUndetermineMessage
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