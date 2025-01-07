import { updateSummaryGroup, updateSSNValue, 
    deriveTypeBasedDisplayValue, deriveEditDisplayValue, deriveComboBoxValue, deriveEligibility, checkBtnEligibility, 
    deriveAVRequestObj, checkValidity, updateAVRequestObj, checkValidityAfterUpdate, injectToField, updateButtonWrapper } from './demographicCommercialHelper';
import { phoneFormatter, dateDeformatter, ssnFormatter, ssnFormatExpr } from 'c/crmserviceHelper';
import SearchVisitor_ZipCodeInvalidMsg_HUM from '@salesforce/label/c.SearchVisitor_ZipCodeInvalidMsg_HUM';
import { phoneFormat } from 'c/crmserviceHelper';
import { zipcodeFormat } from 'c/crmserviceHelper';
import { phoneExtFormat } from 'c/crmserviceHelper';
import US2187347SwicthLabel from '@salesforce/label/c.US2187347SwicthLabel';

export class DrivenComponent {

    AddressListRequest = {
        // Id: "00",
        AddressLine1: "",
        AddressLine2: "",
        // AddressLine3: "",
        City: "",
        StateCode: "",
        ZipCode: "",
        IncludeValidationDetails: false,
        IncludeInputOnError: false,
        IncludeDPV: false,
        IncludeGeoCode: true,
        IncludeEWS: false
    };

    MailingAddressListRequest = {
        AddressLine1: "",
        AddressLine2: "",
        City: "",
        StateCode: "",
        ZipCode: "",
        IncludeValidationDetails: false,
        IncludeInputOnError: false,
        IncludeDPV: false,
        IncludeGeoCode: true,
        IncludeEWS: false
    };

    AddressListResponse = {
        Id: "",
        SuccessFlag: "",
        AddressLine1: "",
        AddressLine2: "",
        City: "",
        StateCode: "",
        ZipCode: "",
        StateID: "",
        ZipCodePlus: "",
        CountyName: "",
        CountyID: "",
        Longitude: "",
        Latitude: "",
        Deliverable: "",
        ReturnCode: "",
        Valid: "",
        ChangeIndicator: ""
    }

    MailingAddressListResponse = {
        Id: "",
        SuccessFlag: "",
        AddressLine1: "",
        AddressLine2: "",
        City: "",
        StateCode: "",
        ZipCode: "",
        StateID: "",
        ZipCodePlus: "",
        CountyName: "",
        CountyID: "",
        Longitude: "",
        Latitude: "",
        Deliverable: "",
        ReturnCode: "",
        Valid: "",
        ChangeIndicator: ""
    }

    modalUSPSDataModel = () => {
        return {
            heading: "Per USPS, did you mean?",
            isError: false,
            dataList: [
                {
                    order: 1,
                    displayData: undefined
                }
            ],
            buttons: [
                {
                    label: "Select",
                    order: 1,
                    variant: "brand",
                    eventName: "uspsAccept",
                    dispatchEventName: "uspsAccept",
                    internal: false,
                    btnClass: 'pub-modal-btn-div btn-yes'
                },
                {
                    label: "Select",
                    order: 2,
                    variant: "brand",
                    eventName: "uspsCancel",
                    dispatchEventName: "uspsCancel",
                    internal: false,
                    btnClass: 'pub-modal-btn-div btn-no'
                }
            ],
            error: {
                message: "",
                buttons: [
                    {
                        label: "Ok",
                        order: 3,
                        variant: "brand",
                        eventName: "uspsCancel",
                        dispatchEventName: "uspsCancel",
                        internal: false
                    }
                ]
            }
        };
    }

    displayFields = () => {
        return [
            {
                order: 1, label: "First Name", value: "", type: "text", 
                fieldName: "FirstName", display: undefined, visibility: [2],
                required: {value: true, message: 'First Name is required'},
                renderLogic:{
                    isCombo: false, isText: true, isTel: false, isEmail: false, isDate: false, 
                    isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
                },
                templateModel: {
                    avfRequestFieldName: 'FirstName', avfValue: '',
                    avfSummary: { sectionName: 'No Title' },
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
                updateModel: {
                    expr: [
                        {
                            fnName: 'deriveRequestValue',
                            fnArgs:  [
                                { order: 0, propertyName: "value", literal: false },
                                { order: 1, propertyName: 1, literal: true },
                                { order: 2, propertyName: "biographics", literal: true },
                                { order: 3, propertyName: "firstname", literal: true },
                                { order: 4, propertyName: 'member', literal: true }
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
                                { order: 1, propertyName: 1, literal: true },
                                { order: 2, propertyName: "membercriterion", literal: true },
                                { order: 3, propertyName: "updaterequesttype", literal: true },
                                { order: 4, propertyName: 'member', literal: true },
                                { order: 5, propertyName: 'BIOGRAPHICS', literal: true }
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
                        fnName: deriveEditDisplayValue, 
                        fnArgs:  [
                            { order: 0, propertyName: "responseMBE", literal: true },
                            { order: 1, propertyName: "FirstName", literal: true },
                            { order: 2, propertyName: 1, literal: true },
                            { order: 3, propertyName: "value", literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "render"
                    },
                    {
                        fnName: deriveEligibility, 
                        fnArgs:  [
                            { order: 0, propertyName: "eligibileEditModel", literal: true },
                            { order: 1, propertyName: "name", literal: true },
                            { order: 2, propertyName: 1, literal: true },
                            { order: 3, propertyName: "disabled", literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "render"
                   },
                   {
                        fnName: updateSummaryGroup, 
                        fnArgs:  [
                            { order: 0, propertyName: "renderDataModel", literal: true },
                            { order: 1, propertyName: [2, 3], literal: true },
                            { order: 2, propertyName: 'isSummary', literal: false },
                            { order: 3, propertyName: 1, literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "change"
                    }
                ],
                pattern: {value: undefined, message: undefined}, 
                minLength: undefined, maxLength: undefined, disabled: undefined,
                note: undefined, isSummary: false
            },
            {
                order: 2, label: "Middle Initial", value: "", type: "text", 
                fieldName: "MiddleInitial", display: undefined, visibility: [2],
                required: {value: undefined, message: undefined},
                renderLogic:{
                    isCombo: false, isText: true, isTel: false, isEmail: false, isDate: false, 
                    isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
                },
                templateModel: {
                    avfRequestFieldName: 'MiddleInitial', avfValue: '',
                    avfSummary: { sectionName: 'No Title' },
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
                updateModel: {
                    expr: [
                        {
                            fnName: 'deriveRequestValue',
                            fnArgs:  [
                                { order: 0, propertyName: "value", literal: false },
                                { order: 1, propertyName: 2, literal: true },
                                { order: 2, propertyName: "biographics", literal: true },
                                { order: 3, propertyName: "middlename", literal: true },
                                { order: 4, propertyName: 'member', literal: true }
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
                                { order: 5, propertyName: 'BIOGRAPHICS', literal: true }
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
                        fnName: deriveEditDisplayValue, 
                        fnArgs:  [
                            { order: 0, propertyName: "responseMBE", literal: true },
                            { order: 1, propertyName: "MiddleInitial", literal: true },
                            { order: 2, propertyName: 2, literal: true },
                            { order: 3, propertyName: "value", literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "render"
                    },
                    {
                        fnName: deriveEligibility, 
                        fnArgs:  [
                            { order: 0, propertyName: "eligibileEditModel", literal: true },
                            { order: 1, propertyName: "name", literal: true },
                            { order: 2, propertyName: 2, literal: true },
                            { order: 3, propertyName: "disabled", literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "render"
                   },
                   {
                        fnName: updateSummaryGroup, 
                        fnArgs:  [
                            { order: 0, propertyName: "renderDataModel", literal: true },
                            { order: 1, propertyName: [1, 3], literal: true },
                            { order: 2, propertyName: 'isSummary', literal: false },
                            { order: 3, propertyName: 2, literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "change"
                    }
                ],
                pattern: {value: undefined, message: undefined}, 
                minLength: undefined, maxLength: 1, disabled: undefined,
                note: undefined, isSummary: false
            },
            {
                order: 3, label: "Last Name", value: "", type: "text", 
                fieldName: "LastName", display: undefined, visibility: [2],
                required: {value: true, message: 'Last Name is Required'},
                renderLogic:{
                    isCombo: false, isText: true, isTel: false, isEmail: false, isDate: false, 
                    isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
                },
                templateModel: {
                    avfRequestFieldName: 'LastName', avfValue: '',
                    avfSummary: { sectionName: 'No Title' },
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
                updateModel: {
                    expr: [
                        {
                            fnName: 'deriveRequestValue',
                            fnArgs:  [
                                { order: 0, propertyName: "value", literal: false },
                                { order: 1, propertyName: 3, literal: true },
                                { order: 2, propertyName: "biographics", literal: true },
                                { order: 3, propertyName: "lastname", literal: true },
                                { order: 4, propertyName: 'member', literal: true }
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
                                { order: 1, propertyName: 3, literal: true },
                                { order: 2, propertyName: "membercriterion", literal: true },
                                { order: 3, propertyName: "updaterequesttype", literal: true },
                                { order: 4, propertyName: 'member', literal: true },
                                { order: 5, propertyName: 'BIOGRAPHICS', literal: true }
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
                        fnName: deriveEditDisplayValue, 
                        fnArgs:  [
                            { order: 0, propertyName: "responseMBE", literal: true },
                            { order: 1, propertyName: "LastName", literal: true },
                            { order: 2, propertyName: 3, literal: true },
                            { order: 3, propertyName: "value", literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "render"
                    },
                    {
                        fnName: deriveEligibility, 
                        fnArgs:  [
                            { order: 0, propertyName: "eligibileEditModel", literal: true },
                            { order: 1, propertyName: "name", literal: true },
                            { order: 2, propertyName: 3, literal: true },
                            { order: 3, propertyName: "disabled", literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "render"
                   },
                   {
                        fnName: updateSummaryGroup, 
                        fnArgs:  [
                            { order: 0, propertyName: "renderDataModel", literal: true },
                            { order: 1, propertyName: [1, 2], literal: true },
                            { order: 2, propertyName: 'isSummary', literal: false },
                            { order: 3, propertyName: 3, literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "change"
                    }
                ],
                pattern: {value: undefined, message: undefined}, 
                minLength: undefined, maxLength: 40, disabled: undefined,
                note: undefined, isSummary: false
            },
            {
                order: 4, label: "Gender", value: "", 
                type: "text", fieldName: "Gender", display: undefined, visibility: [2],
                required: {value: true, message: 'Gender is Required'},
                renderLogic:{
                    isCombo: false, isText: true, isTel: false, isEmail: false, isDate: false, 
                    isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
                },
                templateModel: {
                    avfRequestFieldName: 'Gender', avfValue: '',
                    avfSummary: { sectionName: 'No Title' },
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
                updateModel: {
                    expr: [
                        {
                            fnName: 'deriveRequestValue',
                            fnArgs:  [
                                { order: 0, propertyName: "value", literal: false },
                                { order: 1, propertyName: 4, literal: true },
                                { order: 2, propertyName: "biographics", literal: true },
                                { order: 3, propertyName: "gender", literal: true },
                                { order: 4, propertyName: 'member', literal: true }
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
                                { order: 1, propertyName: 4, literal: true },
                                { order: 2, propertyName: "membercriterion", literal: true },
                                { order: 3, propertyName: "updaterequesttype", literal: true },
                                { order: 4, propertyName: 'member', literal: true },
                                { order: 5, propertyName: 'BIOGRAPHICS', literal: true }
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
                        fnName: deriveEditDisplayValue, 
                        fnArgs:  [
                            { order: 0, propertyName: "responseMBE", literal: true },
                            { order: 1, propertyName: "Gender", literal: true },
                            { order: 2, propertyName: 4, literal: true },
                            { order: 3, propertyName: "value", literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "render"
                    },
                    {
                        fnName: deriveEligibility, 
                        fnArgs:  [
                            { order: 0, propertyName: "eligibileEditModel", literal: true },
                            { order: 1, propertyName: "gender", literal: true },
                            { order: 2, propertyName: 4, literal: true },
                            { order: 3, propertyName: "disabled", literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "render"
                   }
                ],
                pattern: {value: undefined, message: undefined}, 
                minLength: 1, maxLength: 1, disabled: undefined,
                note: undefined, isSummary: false
            },
            {
                order: 5, label: "Birthdate", value: "", type: "date", 
                fieldName: "DateOfBirth", display: undefined, visibility: [2],
                required: {value: true, message: 'Birthdate is Required'},
                renderLogic:{
                    isCombo: false, isText: false, isTel: false, isEmail: false, isDate: true, 
                    isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
                },
                templateModel: {
                    avfRequestFieldName: 'DateOfBirth', avfValue: '',
                    avfSummary: { sectionName: 'No Title' },
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
                updateModel: {
                    expr: [
                        {
                            fnName: 'deriveBirthdateRequestValue',
                            fnArgs:  [
                                { order: 0, propertyName: "value", literal: false },
                                { order: 1, propertyName: 5, literal: true },
                                { order: 2, propertyName: "biographics", literal: true },
                                { order: 3, propertyName: "dateofbirth", literal: true },
                                { order: 4, propertyName: 'member', literal: true }
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
                                { order: 5, propertyName: 'BIOGRAPHICS', literal: true }
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
                        fnName: deriveEditDisplayValue, 
                        fnArgs:  [
                            { order: 0, propertyName: "responseMBE", literal: true },
                            { order: 1, propertyName: "DateOfBirth", literal: true },
                            { order: 2, propertyName: 5, literal: true },
                            { order: 3, propertyName: "value", literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "render"
                    },
                    {
                        fnName: dateDeformatter, 
                        fnArgs:  [
                            { order: 0, propertyName: "value", literal: false }
                        ],
                        fnType: "return",
                        fnOut: ['value'],
                        fnOutSource: [5],
                        fnWhen: "render"
                    },
                    {
                        fnName: deriveEligibility, 
                        fnArgs:  [
                            { order: 0, propertyName: "eligibileEditModel", literal: true },
                            { order: 1, propertyName: "dob", literal: true },
                            { order: 2, propertyName: 5, literal: true },
                            { order: 3, propertyName: "disabled", literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "render"
                    }
                    // {
                    //     fnName: dateDeformatter, 
                    //     fnArgs:  [
                    //         { order: 0, propertyName: "value", literal: false }
                    //     ],
                    //     fnType: "return",
                    //     fnOut: ['value'],
                    //     fnOutSource: [5],
                    //     fnWhen: "change"
                    // }
                ],
                pattern: {value: undefined, message: undefined}, 
                minLength: undefined, maxLength: undefined, disabled: undefined,
                note: undefined, isSummary: false
            },
            {
                order: 6, label: "Social Security Number", value: "", type: "text", 
                fieldName: "Ssn", display: undefined, visibility: [2], ssnValue: '',
                required: {value: false, message: 'SSN is required'},
                renderLogic:{
                    isCombo: false, isText: true, isTel: false, isEmail: false, isDate: false, 
                    isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
                },
                templateModel: {
                    avfRequestFieldName: 'SSN', avfValue: '',
                    avfSummary: { sectionName: 'No Title' },
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
                updateModel: {
                    expr: [
                        {
                            fnName: 'deriveSSNRequestValue',
                            fnArgs:  [
                                { order: 0, propertyName: "ssnValue", literal: false },
                                { order: 1, propertyName: 6, literal: true },
                                { order: 2, propertyName: "criticalbiographics", literal: true },
                                { order: 3, propertyName: "ssn", literal: true },
                                { order: 4, propertyName: 'member', literal: true },
                                { order: 5, propertyName: 'newssn', literal: true }
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
                                { order: 1, propertyName: 6, literal: true },
                                { order: 2, propertyName: "membercriterion", literal: true },
                                { order: 3, propertyName: "updaterequesttype", literal: true },
                                { order: 4, propertyName: 'member', literal: true },
                                { order: 5, propertyName: 'SSN', literal: true }
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
                        fnName: deriveEditDisplayValue, 
                        fnArgs:  [
                            { order: 0, propertyName: "responseMBE", literal: true },
                            { order: 1, propertyName: "Ssn", literal: true },
                            { order: 2, propertyName: 6, literal: true },
                            { order: 3, propertyName: "value", literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "render"
                    },
                    {
                        fnName: deriveEditDisplayValue, 
                        fnArgs:  [
                            { order: 0, propertyName: "responseMBE", literal: true },
                            { order: 1, propertyName: "Ssn", literal: true },
                            { order: 2, propertyName: 6, literal: true },
                            { order: 3, propertyName: "ssnValue", literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "render"
                    },
                    {
                        fnName: updateSSNValue, 
                        fnArgs:  [
                            { order: 0, propertyName: "renderDataModel", literal: true },
                            { order: 1, propertyName: "dirty", literal: true },
                            { order: 2, propertyName: 6, literal: true },
                            { order: 3, propertyName: "ssnValue", literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "change"
                    },
                    {
                        fnName: ssnFormatter,
                        fnArgs:  [
                            { order: 0, propertyName: "value", literal: false }
                        ],
                        fnType: "return",
                        fnOut: ["value"],
                        fnOutSource: [6],
                        fnWhen: "render"
                    },
                    {
                        fnName: ssnFormatter,
                        fnArgs:  [
                            { order: 0, propertyName: "value", literal: false }
                        ],
                        fnType: "return",
                        fnOut: ["value"],
                        fnOutSource: [6],
                        fnWhen: "change"
                    },
                    {
                        fnName: deriveEligibility, 
                        fnArgs:  [
                            { order: 0, propertyName: "eligibileEditModel", literal: true },
                            { order: 1, propertyName: "ssn", literal: true },
                            { order: 2, propertyName: 6, literal: true },
                            { order: 3, propertyName: "disabled", literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "render"
                    }
                ],
                pattern: {value: ssnFormatExpr, message: 'SSN should be of format XXX-XX-XXXX'}, 
                minLength: 9, maxLength: 11, disabled: undefined,
                note: undefined, isSummary: false
            },
            {
                order: 12, label: "Mailing Address 1", value: "", type: "text", 
                fieldName: "AddressLine1", display: undefined, visibility: [1],
                required: {value: true, message: 'Mailing Address 1 is required'},
                renderLogic:{
                    isCombo: false, isText: true, isTel: false, isEmail: false, isDate: false, 
                    isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
                },
                templateModel: {
                    avfRequestFieldName: 'MailingAddress', avfValue: '',
                    avfSummary: { sectionName: 'No Title' },
                    expr: [
                        {
                            fnName: 'concatAddress',
                            fnArgs:  [
                                { order: 0, propertyName: "value", literal: false },
                                { order: 1, propertyName: 13, literal: true },
                                { order: 2, propertyName: "value", literal: true },
                                { order: 3, propertyName: "cod", literal: true },
                            ],
                            fnType: "return",
                            fnOut: ["avfValue"],
                            fnOutSource: [12],
                            fnWhen: "template"
                        }
                    ]
                },
                updateModel: {
                    expr: [
                        {
                            fnName: 'deriveAddressRequestValue',
                            fnArgs:  [
                                { order: 0, propertyName: [12, 13, 14, 15, 16], literal: true },
                                { order: 1, propertyName: ["line1", "line2", "city", "statecode", "zipcode"], literal: true },
                                { order: 2, propertyName: "demographics", literal: true },
                                { order: 3, propertyName: 'member', literal: true },
                                { order: 4, propertyName: 'address', literal: true },
                                { order: 5, propertyName: 'type', literal: true },
                                { order: 6, propertyName: '01', literal: true },
                                { order: 7, propertyName: 'cod', literal: true }
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
                                { order: 1, propertyName: 12, literal: true },
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
                        fnName: deriveTypeBasedDisplayValue, 
                        fnArgs:  [
                            { order: 0, propertyName: "responseMBE", literal: true },
                            { order: 1, propertyName: "AddressLine1", literal: true },
                            { order: 2, propertyName: 12, literal: true },
                            { order: 3, propertyName: "value", literal: true },
                            { order: 4, propertyName: "Type", literal: true },
                            { order: 5, propertyName: "Platform", literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "render"
                    },
                    {
                        fnName: updateButtonWrapper, 
                        fnArgs:  [
                            { order: 0, propertyName: "buttonModel", literal: true },
                            { order: 1, propertyName: "renderDataModel", literal: true },
                            { order: 2, propertyName: "dirty", literal: true },
                            { order: 3, propertyName: [12, 13, 14, 15, 16], literal: true },
                            { order: 4, propertyName: "requestAVObj", literal: true },
                            { order: 5, propertyName: ['AddressLine1', 'City', 'StateCode', 'ZipCode'], literal: true },
                            { order: 6, propertyName: 16, literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "change"
                    },
                    {
                        fnName: deriveEligibility, 
                        fnArgs:  [
                            { order: 0, propertyName: "eligibileEditModel", literal: true },
                            { order: 1, propertyName: "mailingAddress", literal: true },
                            { order: 2, propertyName: 12, literal: true },
                            { order: 3, propertyName: "disabled", literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "render"
                    },
                    {
                        fnName: updateSummaryGroup, 
                        fnArgs:  [
                            { order: 0, propertyName: "renderDataModel", literal: true },
                            { order: 1, propertyName: [13, 14, 15, 16], literal: true },
                            { order: 2, propertyName: 'isSummary', literal: false },
                            { order: 3, propertyName: 12, literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "change"
                    }
                ],
                pattern: {value: undefined, message: undefined}, 
                minLength: undefined, maxLength: 32, disabled: undefined,
                note: undefined, isSummary: false, source: 'EM'
            },
            {
                order: 13, label: "Mailing Address 2", value: "", type: "text", 
                fieldName: "AddressLine2", display: undefined, visibility: [1],
                required: {value: undefined, message: undefined},
                renderLogic:{
                    isCombo: false, isText: true, isTel: false, isEmail: false, isDate: false, 
                    isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
                },
                expr: [
                    {
                        fnName: deriveTypeBasedDisplayValue, 
                        fnArgs:  [
                            { order: 0, propertyName: "responseMBE", literal: true },
                            { order: 1, propertyName: "AddressLine2", literal: true },
                            { order: 2, propertyName: 13, literal: true },
                            { order: 3, propertyName: "value", literal: true },
                            { order: 4, propertyName: "Type", literal: true },
                            { order: 5, propertyName: "Platform", literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "render"
                    },
                    {
                        fnName: updateButtonWrapper, 
                        fnArgs:  [
                            { order: 0, propertyName: "buttonModel", literal: true },
                            { order: 1, propertyName: "renderDataModel", literal: true },
                            { order: 2, propertyName: "dirty", literal: true },
                            { order: 3, propertyName: [12, 13, 14, 15, 16], literal: true },
                            { order: 4, propertyName: "requestAVObj", literal: true },
                            { order: 5, propertyName: ['AddressLine1', 'City', 'StateCode', 'ZipCode'], literal: true },
                            { order: 6, propertyName: 16, literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "change"
                    },
                    {
                        fnName: deriveEligibility, 
                        fnArgs:  [
                            { order: 0, propertyName: "eligibileEditModel", literal: true },
                            { order: 1, propertyName: "mailingAddress", literal: true },
                            { order: 2, propertyName: 13, literal: true },
                            { order: 3, propertyName: "disabled", literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "render"
                    },
                    {
                        fnName: updateSummaryGroup, 
                        fnArgs:  [
                            { order: 0, propertyName: "renderDataModel", literal: true },
                            { order: 1, propertyName: [12, 14, 15, 16], literal: true },
                            { order: 2, propertyName: 'isSummary', literal: false },
                            { order: 3, propertyName: 13, literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "change"
                    }
                ],
                pattern: {value: undefined, message: undefined}, 
                minLength: 0, maxLength: 32, disabled: undefined,
                note: undefined, isSummary: false, source: 'EM'
            },
            {
                order: 14, label: "City", value: "", type: "text", 
                fieldName: "City", display: undefined, visibility: [1],
                required: {value: true, message: 'Mailing City is required'},
                renderLogic:{
                    isCombo: false, isText: true, isTel: false, isEmail: false, isDate: false, 
                    isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
                },
                templateModel: {
                    avfRequestFieldName: 'MailingCityName', avfValue: '',
                    avfSummary: { sectionName: 'No Title' },
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
                expr: [
                    {
                        fnName: deriveTypeBasedDisplayValue, 
                        fnArgs:  [
                            { order: 0, propertyName: "responseMBE", literal: true },
                            { order: 1, propertyName: "City", literal: true },
                            { order: 2, propertyName: 14, literal: true },
                            { order: 3, propertyName: "value", literal: true },
                            { order: 4, propertyName: "Type", literal: true },
                            { order: 5, propertyName: "Platform", literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "render"
                    },
                    {
                        fnName: updateButtonWrapper, 
                        fnArgs:  [
                            { order: 0, propertyName: "buttonModel", literal: true },
                            { order: 1, propertyName: "renderDataModel", literal: true },
                            { order: 2, propertyName: "dirty", literal: true },
                            { order: 3, propertyName: [12, 13, 14, 15, 16], literal: true },
                            { order: 4, propertyName: "requestAVObj", literal: true },
                            { order: 5, propertyName: ['AddressLine1', 'City', 'StateCode', 'ZipCode'], literal: true },
                            { order: 6, propertyName: 16, literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "change"
                    },
                    {
                        fnName: deriveEligibility, 
                        fnArgs:  [
                            { order: 0, propertyName: "eligibileEditModel", literal: true },
                            { order: 1, propertyName: "mailingAddress", literal: true },
                            { order: 2, propertyName: 14, literal: true },
                            { order: 3, propertyName: "disabled", literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "render"
                    },
                    {
                        fnName: updateSummaryGroup, 
                        fnArgs:  [
                            { order: 0, propertyName: "renderDataModel", literal: true },
                            { order: 1, propertyName: [12, 13, 15, 16], literal: true },
                            { order: 2, propertyName: 'isSummary', literal: false },
                            { order: 3, propertyName: 14, literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "change"
                    }
                ],
                pattern: {value: undefined, message: undefined}, 
                minLength: undefined, maxLength: 15, disabled: undefined,
                note: undefined, isSummary: false, source: 'EM'
            },
            {
                order: 15, label: "State", value: "", type: "combobox", 
                fieldName: "StateCode", display: undefined, visibility: [1],
                required: {value: true, message: 'Mailing State is required'},
                renderLogic:{
                    isCombo: true, isText: false, isTel: false, isEmail: false, isDate: false, 
                    isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
                },
                templateModel: {
                    avfRequestFieldName: 'MailingStateCode', avfValue: '',
                    avfSummary: { sectionName: 'No Title' },
                    expr: [
                        {
                            fnName: 'deriveValue',
                            fnArgs:  [
                                { order: 0, propertyName: "value", literal: false }
                            ],
                            fnType: "return",
                            fnOut: ["avfValue"],
                            fnOutSource: [15],
                            fnWhen: "template"
                        }
                    ]
                },
                expr: [
                    {
                        fnName: deriveTypeBasedDisplayValue, 
                        fnArgs:  [
                            { order: 0, propertyName: "responseMBE", literal: true },
                            { order: 1, propertyName: "StateCode", literal: true },
                            { order: 2, propertyName: 15, literal: true },
                            { order: 3, propertyName: "value", literal: true },
                            { order: 4, propertyName: "Type", literal: true },
                            { order: 5, propertyName: "Platform", literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "render"
                    },
                    {
                        fnName: updateButtonWrapper, 
                        fnArgs:  [
                            { order: 0, propertyName: "buttonModel", literal: true },
                            { order: 1, propertyName: "renderDataModel", literal: true },
                            { order: 2, propertyName: "dirty", literal: true },
                            { order: 3, propertyName: [12, 13, 14, 15, 16], literal: true },
                            { order: 4, propertyName: "requestAVObj", literal: true },
                            { order: 5, propertyName: ['AddressLine1', 'City', 'StateCode', 'ZipCode'], literal: true },
                            { order: 6, propertyName: 16, literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "change"
                    },
                    {
                        fnName: deriveComboBoxValue, 
                        fnArgs:  [
                            { order: 0, propertyName: "value", literal: false }
                        ],
                        fnType: "return",
                        fnOut: ['value'],
                        fnOutSource: [15],
                        fnWhen: "render"
                    },
                    {
                        fnName: deriveEligibility, 
                        fnArgs:  [
                            { order: 0, propertyName: "eligibileEditModel", literal: true },
                            { order: 1, propertyName: "mailingAddress", literal: true },
                            { order: 2, propertyName: 15, literal: true },
                            { order: 3, propertyName: "disabled", literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "render"
                    },
                    {
                        fnName: updateSummaryGroup, 
                        fnArgs:  [
                            { order: 0, propertyName: "renderDataModel", literal: true },
                            { order: 1, propertyName: [12, 13, 14, 16], literal: true },
                            { order: 2, propertyName: 'isSummary', literal: false },
                            { order: 3, propertyName: 15, literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "change"
                    }
                ],
                pattern: {value: /-None-/, message: "Mailing State is required"}, 
                minLength: undefined, maxLength: undefined, disabled: undefined,
                note: undefined, isSummary: false, source: 'EM'
            },
            {
                order: 16, label: "Zip Code", value: "", type: "text", 
                fieldName: "ZipCode", display: undefined, visibility: [1],
                required: {value: true, message: 'Mailing Zip Code is required'},
                buttonRender: true, buttonModel: undefined,
                renderLogic:{
                    isCombo: false, isText: true, isTel: false, isEmail: false, isDate: false, 
                    isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
                },
                templateModel: {
                    avfRequestFieldName: 'MailingZipCode', avfValue: '',
                    avfSummary: { sectionName: 'No Title' },
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
                expr: [
                    {
                        fnName: deriveTypeBasedDisplayValue, 
                        fnArgs:  [
                            { order: 0, propertyName: "responseMBE", literal: true },
                            { order: 1, propertyName: "ZipCode", literal: true },
                            { order: 2, propertyName: 16, literal: true },
                            { order: 3, propertyName: "value", literal: true },
                            { order: 4, propertyName: "Type", literal: true },
                            { order: 5, propertyName: "Platform", literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "render"
                    },
                    {
                        fnName: updateAVRequestObj, 
                        fnArgs:  [
                            { order: 0, propertyName: "buttonModel", literal: false },
                            { order: 1, propertyName: "renderDataModel", literal: true },
                            { order: 2, propertyName: "dirty", literal: true },
                            { order: 3, propertyName: [12, 13, 14, 15, 16], literal: true },
                            { order: 4, propertyName: "requestAVObj", literal: true }
                        ],
                        fnType: "return",
                        fnOut: ['buttonModel'],
                        fnOutSource: [16],
                        fnWhen: "change"
                    },
                    {
                        fnName: checkValidityAfterUpdate, 
                        fnArgs:  [
                            { order: 0, propertyName: "buttonModel", literal: false },
                            { order: 1, propertyName: ['AddressLine1', 'City', 'StateCode', 'ZipCode'], literal: true },
                            { order: 2, propertyName: "requestAVObj", literal: true },
                            { order: 3, propertyName: [12, 13, 14, 15, 16], literal: true }
                        ],
                        fnType: "return",
                        fnOut: ['buttonModel'],
                        fnOutSource: [16],
                        fnWhen: "change"
                    },
                    {
                        fnName: deriveEligibility, 
                        fnArgs:  [
                            { order: 0, propertyName: "eligibileEditModel", literal: true },
                            { order: 1, propertyName: "mailingAddress", literal: true },
                            { order: 2, propertyName: 16, literal: true },
                            { order: 3, propertyName: "disabled", literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "render"
                    },
                    {
                        fnName: updateSummaryGroup, 
                        fnArgs:  [
                            { order: 0, propertyName: "renderDataModel", literal: true },
                            { order: 1, propertyName: [12, 13, 14, 15], literal: true },
                            { order: 2, propertyName: 'isSummary', literal: false },
                            { order: 3, propertyName: 16, literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "change"
                    }
                ],
                pattern: {value: zipcodeFormat, message: SearchVisitor_ZipCodeInvalidMsg_HUM}, 
                minLength: 5, maxLength: 9, disabled: undefined,
                note: undefined, isSummary: false, source: 'EM'
            },
            {
                order: 7, label: "Residential Address 1", value: "", type: "text", 
                fieldName: "AddressLine1", display: undefined, visibility: [],
                required: {value: true, message: 'Residential Address 1 is required'},
                renderLogic:{
                    isCombo: false, isText: true, isTel: false, isEmail: false, isDate: false, 
                    isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
                },
                templateModel: {
                    avfRequestFieldName: 'PermanentResidentialAddress', avfValue: '',
                    avfSummary: { sectionName: 'No Title' },
                    expr: [
                        {
                            fnName: 'concatAddress',
                            fnArgs:  [
                                { order: 0, propertyName: "value", literal: false },
                                { order: 1, propertyName: 8, literal: true },
                                { order: 2, propertyName: "value", literal: true },
                                { order: 3, propertyName: "cod", literal: true },
                            ],
                            fnType: "return",
                            fnOut: ["avfValue"],
                            fnOutSource: [7],
                            fnWhen: "template"
                        }
                    ]
                },
                updateModel: {
                    expr: [
                        {
                            fnName: 'deriveAddressRequestValue',
                            fnArgs:  [
                                { order: 0, propertyName: [7, 8, 9, 10, 11], literal: true },
                                { order: 1, propertyName: ["line1", "line2", "city", "statecode", "zipcode"], literal: true },
                                { order: 2, propertyName: "demographics", literal: true },
                                { order: 3, propertyName: 'member', literal: true },
                                { order: 4, propertyName: 'address', literal: true },
                                { order: 5, propertyName: 'type', literal: true },
                                { order: 6, propertyName: '03', literal: true },
                                { order: 7, propertyName: 'cod', literal: true }
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
                                { order: 1, propertyName: 7, literal: true },
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
                        fnName: deriveTypeBasedDisplayValue, 
                        fnArgs:  [
                            { order: 0, propertyName: "responseMBE", literal: true },
                            { order: 1, propertyName: "AddressLine1", literal: true },
                            { order: 2, propertyName: 7, literal: true },
                            { order: 3, propertyName: "value", literal: true },
                            { order: 4, propertyName: "Type", literal: true },
                            { order: 5, propertyName: "Residential", literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "render"
                    },
                    {
                        fnName: updateButtonWrapper, 
                        fnArgs:  [
                            { order: 0, propertyName: "buttonModel", literal: true },
                            { order: 1, propertyName: "renderDataModel", literal: true },
                            { order: 2, propertyName: "dirty", literal: true },
                            { order: 3, propertyName: [7, 8, 9, 10, 11], literal: true },
                            { order: 4, propertyName: "requestAVObj", literal: true },
                            { order: 5, propertyName: ['AddressLine1', 'City', 'StateCode', 'ZipCode'], literal: true },
                            { order: 6, propertyName: 11, literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "change"
                    },
                    {
                        fnName: deriveEligibility, 
                        fnArgs:  [
                            { order: 0, propertyName: "eligibileEditModel", literal: true },
                            { order: 1, propertyName: "residentialAddress", literal: true },
                            { order: 2, propertyName: 7, literal: true },
                            { order: 3, propertyName: "disabled", literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "render"
                    },
                    {
                        fnName: updateSummaryGroup, 
                        fnArgs:  [
                            { order: 0, propertyName: "renderDataModel", literal: true },
                            { order: 1, propertyName: [8, 9, 10, 11], literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "change"
                    }
                ],
                pattern: {value: undefined, message: undefined}, 
                minLength: 0, maxLength: 32, disabled: undefined,
                note: undefined, isSummary: false, source: 'EM'
            },
            {
                order: 8, label: "Residential Address 2", value: "", type: "text", 
                fieldName: "AddressLine2", display: undefined, visibility: [],
                required: {value: undefined, message: undefined},
                renderLogic:{
                    isCombo: false, isText: true, isTel: false, isEmail: false, isDate: false, 
                    isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
                },
                expr: [
                    {
                        fnName: deriveTypeBasedDisplayValue, 
                        fnArgs:  [
                            { order: 0, propertyName: "responseMBE", literal: true },
                            { order: 1, propertyName: "AddressLine2", literal: true },
                            { order: 2, propertyName: 8, literal: true },
                            { order: 3, propertyName: "value", literal: true },
                            { order: 4, propertyName: "Type", literal: true },
                            { order: 5, propertyName: "Residential", literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "render"
                    },
                    {
                        fnName: updateButtonWrapper, 
                        fnArgs:  [
                            { order: 0, propertyName: "buttonModel", literal: true },
                            { order: 1, propertyName: "renderDataModel", literal: true },
                            { order: 2, propertyName: "dirty", literal: true },
                            { order: 3, propertyName: [7, 8, 9, 10, 11], literal: true },
                            { order: 4, propertyName: "requestAVObj", literal: true },
                            { order: 5, propertyName: ['AddressLine1', 'City', 'StateCode', 'ZipCode'], literal: true },
                            { order: 6, propertyName: 11, literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "change"
                    },
                    {
                        fnName: deriveEligibility, 
                        fnArgs:  [
                            { order: 0, propertyName: "eligibileEditModel", literal: true },
                            { order: 1, propertyName: "residentialAddress", literal: true },
                            { order: 2, propertyName: 8, literal: true },
                            { order: 3, propertyName: "disabled", literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "render"
                    },
                    {
                        fnName: updateSummaryGroup, 
                        fnArgs:  [
                            { order: 0, propertyName: "renderDataModel", literal: true },
                            { order: 1, propertyName: [7, 9, 10, 11], literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "change"
                    }
                ],
                pattern: {value: undefined, message: undefined}, 
                minLength: 0, maxLength: 32, disabled: undefined,
                note: undefined, isSummary: false, source: 'EM'
            },
            {
                order: 9, label: "City", value: "", type: "text", 
                fieldName: "City", display: undefined, visibility: [],
                required: {value: true, message: 'City is required'},
                renderLogic:{
                    isCombo: false, isText: true, isTel: false, isEmail: false, isDate: false, 
                    isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
                },
                templateModel: {
                    avfRequestFieldName: 'PermanentResidentialCityName', avfValue: '',
                    avfSummary: { sectionName: 'No Title' },
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
                expr: [
                    {
                        fnName: deriveTypeBasedDisplayValue, 
                        fnArgs:  [
                            { order: 0, propertyName: "responseMBE", literal: true },
                            { order: 1, propertyName: "City", literal: true },
                            { order: 2, propertyName: 9, literal: true },
                            { order: 3, propertyName: "value", literal: true },
                            { order: 4, propertyName: "Type", literal: true },
                            { order: 5, propertyName: "Residential", literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "render"
                    },
                    {
                        fnName: updateButtonWrapper, 
                        fnArgs:  [
                            { order: 0, propertyName: "buttonModel", literal: true },
                            { order: 1, propertyName: "renderDataModel", literal: true },
                            { order: 2, propertyName: "dirty", literal: true },
                            { order: 3, propertyName: [7, 8, 9, 10, 11], literal: true },
                            { order: 4, propertyName: "requestAVObj", literal: true },
                            { order: 5, propertyName: ['AddressLine1', 'City', 'StateCode', 'ZipCode'], literal: true },
                            { order: 6, propertyName: 11, literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "change"
                    },
                    {
                        fnName: deriveEligibility, 
                        fnArgs:  [
                            { order: 0, propertyName: "eligibileEditModel", literal: true },
                            { order: 1, propertyName: "residentialAddress", literal: true },
                            { order: 2, propertyName: 9, literal: true },
                            { order: 3, propertyName: "disabled", literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "render"
                    },
                    {
                        fnName: updateSummaryGroup, 
                        fnArgs:  [
                            { order: 0, propertyName: "renderDataModel", literal: true },
                            { order: 1, propertyName: [7, 8, 10, 11], literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "change"
                    }
                ],
                pattern: {value: undefined, message: undefined}, 
                minLength: 0, maxLength: 15, disabled: undefined,
                note: undefined, isSummary: false, source: 'EM'
            },
            {
                order: 10, label: "State", value: "", type: "combobox", 
                fieldName: "StateCode", display: undefined, visibility: [],
                required: {value: true, message: 'Residential State is required'},
                renderLogic:{
                    isCombo: true, isText: false, isTel: false, isEmail: false, isDate: false, 
                    isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
                },
                templateModel: {
                    avfRequestFieldName: 'PermanentResidentialStateCode', avfValue: '',
                    avfSummary: { sectionName: 'No Title' },
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
                expr: [
                    {
                        fnName: deriveTypeBasedDisplayValue, 
                        fnArgs:  [
                            { order: 0, propertyName: "responseMBE", literal: true },
                            { order: 1, propertyName: "StateCode", literal: true },
                            { order: 2, propertyName: 10, literal: true },
                            { order: 3, propertyName: "value", literal: true },
                            { order: 4, propertyName: "Type", literal: true },
                            { order: 5, propertyName: "Residential", literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "render"
                    },
                    {
                        fnName: deriveComboBoxValue, 
                        fnArgs:  [
                            { order: 0, propertyName: "value", literal: false }
                        ],
                        fnType: "return",
                        fnOut: ['value'],
                        fnOutSource: [10],
                        fnWhen: "render"
                    },
                    {
                        fnName: updateButtonWrapper, 
                        fnArgs:  [
                            { order: 0, propertyName: "buttonModel", literal: true },
                            { order: 1, propertyName: "renderDataModel", literal: true },
                            { order: 2, propertyName: "dirty", literal: true },
                            { order: 3, propertyName: [7, 8, 9, 10, 11], literal: true },
                            { order: 4, propertyName: "requestAVObj", literal: true },
                            { order: 5, propertyName: ['AddressLine1', 'City', 'StateCode', 'ZipCode'], literal: true },
                            { order: 6, propertyName: 11, literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "change"
                    },
                    {
                        fnName: deriveEligibility, 
                        fnArgs:  [
                            { order: 0, propertyName: "eligibileEditModel", literal: true },
                            { order: 1, propertyName: "residentialAddress", literal: true },
                            { order: 2, propertyName: 10, literal: true },
                            { order: 3, propertyName: "disabled", literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "render"
                    },
                    {
                        fnName: updateSummaryGroup, 
                        fnArgs:  [
                            { order: 0, propertyName: "renderDataModel", literal: true },
                            { order: 1, propertyName: [7, 8, 9, 11], literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "change"
                    }
                ],
                pattern: {value: /-None-/, message: "Residential State is required"}, 
                minLength: undefined, maxLength: undefined, disabled: undefined,
                note: undefined, isSummary: false, source: 'EM'
            },
            {
                order: 11, label: "Zip Code", value: "", type: "text", 
                fieldName: "ZipCode", display: undefined, visibility: [],
                required: {value: true, message: 'Residential Zip Code is required'},
                buttonRender: true, buttonModel: undefined,
                renderLogic:{
                    isCombo: false, isText: true, isTel: false, isEmail: false, isDate: false, 
                    isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
                },
                templateModel: {
                    avfRequestFieldName: 'PermanentResidentialZipCode', avfValue: '',
                    avfSummary: { sectionName: 'No Title' },
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
                expr: [
                    {
                        fnName: deriveTypeBasedDisplayValue, 
                        fnArgs:  [
                            { order: 0, propertyName: "responseMBE", literal: true },
                            { order: 1, propertyName: "ZipCode", literal: true },
                            { order: 2, propertyName: 11, literal: true },
                            { order: 3, propertyName: "value", literal: true },
                            { order: 4, propertyName: "Type", literal: true },
                            { order: 5, propertyName: "Residential", literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "render"
                    },
                    {
                        fnName: updateAVRequestObj, 
                        fnArgs:  [
                            { order: 0, propertyName: "buttonModel", literal: false },
                            { order: 1, propertyName: "renderDataModel", literal: true },
                            { order: 2, propertyName: "dirty", literal: true },
                            { order: 3, propertyName: [7, 8, 9, 10, 11], literal: true },
                            { order: 4, propertyName: "requestAVObj", literal: true }
                        ],
                        fnType: "return",
                        fnOut: ['buttonModel'],
                        fnOutSource: [11],
                        fnWhen: "change"
                    },
                    {
                        fnName: checkValidityAfterUpdate, 
                        fnArgs:  [
                            { order: 0, propertyName: "buttonModel", literal: false },
                            { order: 1, propertyName: ['AddressLine1', 'City', 'StateCode', 'ZipCode'], literal: true },
                            { order: 2, propertyName: "requestAVObj", literal: true },
                            { order: 3, propertyName: [7, 8, 9, 10, 11], literal: true }
                        ],
                        fnType: "return",
                        fnOut: ['buttonModel'],
                        fnOutSource: [11],
                        fnWhen: "change"
                    },
                    {
                        fnName: deriveEligibility, 
                        fnArgs:  [
                            { order: 0, propertyName: "eligibileEditModel", literal: true },
                            { order: 1, propertyName: "residentialAddress", literal: true },
                            { order: 2, propertyName: 11, literal: true },
                            { order: 3, propertyName: "disabled", literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "render"
                    },
                    {
                        fnName: updateSummaryGroup, 
                        fnArgs:  [
                            { order: 0, propertyName: "renderDataModel", literal: true },
                            { order: 1, propertyName: [7, 8, 9, 10], literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "change"
                    }
                ],
                pattern: {value: zipcodeFormat, message: SearchVisitor_ZipCodeInvalidMsg_HUM}, 
                minLength: 5, maxLength: 9, disabled: undefined,
                note: undefined, isSummary: false, source: 'EM'
            },
            {
                order: 17, label: "Home Email", value: "", type: "email", 
                fieldName: "PersonEmail", display: undefined, visibility: [1],
                required: {value: false, message: undefined},
                renderLogic:{
                    isCombo: false, isText: false, isTel: false, isEmail: true, isDate: false, 
                    isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
                },
                templateModel: {
                    avfRequestFieldName: 'HomeEmail', avfValue: '',
                    avfSummary: { sectionName: 'No Title' },
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
                updateModel: {
                    expr: [
                        {
                            fnName: 'deriveElectronicRequestValue',
                            fnArgs:  [
                                { order: 0, propertyName: "value", literal: false },
                                { order: 1, propertyName: 17, literal: true },
                                { order: 2, propertyName: "demographics", literal: true },
                                { order: 3, propertyName: "email", literal: true },
                                { order: 4, propertyName: 'member', literal: true },
                                { order: 5, propertyName: 'type', literal: true },
                                { order: 6, propertyName: '01', literal: true },
                                { order: 7, propertyName: 'electronic', literal: true },
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
                                { order: 1, propertyName: 17, literal: true },
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
                        fnName: deriveTypeBasedDisplayValue, 
                        fnArgs:  [
                            { order: 0, propertyName: "responseMBE", literal: true },
                            { order: 1, propertyName: "Address", literal: true },
                            { order: 2, propertyName: 17, literal: true },
                            { order: 3, propertyName: "value", literal: true },
                            { order: 4, propertyName: "Type", literal: true },
                            { order: 5, propertyName: "Primary", literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "render"
                    },
                    {
                        fnName: deriveEligibility, 
                        fnArgs:  [
                            { order: 0, propertyName: "eligibileEditModel", literal: true },
                            { order: 1, propertyName: "homeEmail", literal: true },
                            { order: 2, propertyName: 17, literal: true },
                            { order: 3, propertyName: "disabled", literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "render"
                    }
                ],
                pattern: {value: undefined, message: undefined}, 
                minLength: 0, maxLength: 60, disabled: undefined,
                note: undefined, isSummary: false
            },
            {
                order: 18, label: "Work Email", value: "", type: "email",
                fieldName: 'PersonEmail', display: undefined, visibility: [1],
                required: { value: false, message: undefined }, 
                renderLogic:{
                    isCombo: false, isText: false, isTel: false, isEmail: true, isDate: false, 
                    isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
                },
                templateModel: {
                    avfRequestFieldName: 'WorkEmail', avfValue: '',
                    avfSummary: { sectionName: 'No Title' },
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
                updateModel: {
                    expr: [
                        {
                            fnName: 'deriveElectronicRequestValue',
                            fnArgs:  [
                                { order: 0, propertyName: "value", literal: false },
                                { order: 1, propertyName: 18, literal: true },
                                { order: 2, propertyName: "demographics", literal: true },
                                { order: 3, propertyName: "email", literal: true },
                                { order: 4, propertyName: 'member', literal: true },
                                { order: 5, propertyName: 'type', literal: true },
                                { order: 6, propertyName: '02', literal: true },
                                { order: 7, propertyName: 'electronic', literal: true },
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
                                { order: 1, propertyName: 18, literal: true },
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
                        fnName: deriveTypeBasedDisplayValue, 
                        fnArgs:  [
                            { order: 0, propertyName: "responseMBE", literal: true },
                            { order: 1, propertyName: "Address", literal: true },
                            { order: 2, propertyName: 18, literal: true },
                            { order: 3, propertyName: "value", literal: true },
                            { order: 4, propertyName: "Type", literal: true },
                            { order: 5, propertyName: "Alternate", literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "render"
                    },
                    {
                        fnName: deriveEligibility, 
                        fnArgs:  [
                            { order: 0, propertyName: "eligibileEditModel", literal: true },
                            { order: 1, propertyName: "workEmail", literal: true },
                            { order: 2, propertyName: 18, literal: true },
                            { order: 3, propertyName: "disabled", literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "render"
                    }
                ],
                pattern: {value: undefined, message: undefined}, 
                minLength: 0, maxLength: 60, disabled: undefined,
                note: undefined, isSummary: false
            },
            {
                order: 19, label: 'Home Phone', value: "", type: "tel",
                fieldName: 'PersonPhone', display: undefined, visibility: [1],
                required: {value: false, message: 'Home Phone is required'},
                renderLogic:{
                    isCombo: false, isText: false, isTel: true, isEmail: false, isDate: false, 
                    isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
                },
                templateModel: {
                    avfRequestFieldName: 'HomePhone', avfValue: '',
                    avfSummary: { sectionName: 'No Title' },
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
                updateModel: {
                    expr: [
                        {
                            fnName: 'derivePhoneRequestValue',
                            fnArgs:  [
                                { order: 0, propertyName: "value", literal: false },
                                { order: 1, propertyName: 19, literal: true },
                                { order: 2, propertyName: "demographics", literal: true },
                                { order: 3, propertyName: "number", literal: true },
                                { order: 4, propertyName: 'member', literal: true },
                                { order: 5, propertyName: 'type', literal: true },
                                { order: 6, propertyName: '01', literal: true },
                                { order: 7, propertyName: 'phone', literal: true },
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
                                { order: 1, propertyName: 19, literal: true },
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
                        fnName: deriveTypeBasedDisplayValue, 
                        fnArgs:  [
                            { order: 0, propertyName: "responseMBE", literal: true },
                            { order: 1, propertyName: "PhoneNumber", literal: true },
                            { order: 2, propertyName: 19, literal: true },
                            { order: 3, propertyName: "value", literal: true },
                            { order: 4, propertyName: "Type", literal: true },
                            { order: 5, propertyName: "Home", literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "render"
                    },
                    {
                        fnName: phoneFormatter, 
                        fnArgs:  [
                            { order: 0, propertyName: "value", literal: false }
                        ],
                        fnType: "return",
                        fnOut: ["value"],
                        fnOutSource: [19],
                        fnWhen: "render"
                    },
                    {
                        fnName: phoneFormatter, 
                        fnArgs:  [
                            { order: 0, propertyName: "value", literal: false }
                        ],
                        fnType: "return",
                        fnOut: ["value"],
                        fnOutSource: [19],
                        fnWhen: "change"
                    },
                    {
                        fnName: deriveEligibility, 
                        fnArgs:  [
                            { order: 0, propertyName: "eligibileEditModel", literal: true },
                            { order: 1, propertyName: "homePhone", literal: true },
                            { order: 2, propertyName: 19, literal: true },
                            { order: 3, propertyName: "disabled", literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "render"
                    }
                ],
                pattern: {value: phoneFormat, message: 'PhoneNumber must be in (123) 456-7890 format.'}, 
                minLength: 10, maxLength: 14, disabled: undefined,
                note: undefined, isSummary: false
            },
            {
                order: 20, label: 'Mobile', value: "", type: "tel",
                fieldName: 'PersonPhone', display: undefined, visibility: [],
                required: {value: false, message: undefined},
                renderLogic:{
                    isCombo: false, isText: false, isTel: true, isEmail: false, isDate: false, 
                    isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
                },
                templateModel: {
                    avfRequestFieldName: 'Mobile', avfValue: '',
                    avfSummary: { sectionName: 'No Title' },
                    expr: [
                        {
                            fnName: 'deriveValue',
                            fnArgs:  [
                                { order: 0, propertyName: "value", literal: false }
                            ],
                            fnType: "return",
                            fnOut: ["avfValue"],
                            fnOutSource: [20],
                            fnWhen: "template"
                        }
                    ]
                },
                updateModel: {
                    expr: [
                        {
                            fnName: 'derivePhoneRequestValue',
                            fnArgs:  [
                                { order: 0, propertyName: "value", literal: false },
                                { order: 1, propertyName: 20, literal: true },
                                { order: 2, propertyName: "demographics", literal: true },
                                { order: 3, propertyName: "number", literal: true },
                                { order: 4, propertyName: 'member', literal: true },
                                { order: 5, propertyName: 'type', literal: true },
                                { order: 6, propertyName: '06', literal: true },
                                { order: 7, propertyName: 'phone', literal: true },
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
                                { order: 1, propertyName: 20, literal: true },
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
                        fnName: deriveTypeBasedDisplayValue, 
                        fnArgs:  [
                            { order: 0, propertyName: "responseMBE", literal: true },
                            { order: 1, propertyName: "PhoneNumber", literal: true },
                            { order: 2, propertyName: 20, literal: true },
                            { order: 3, propertyName: "value", literal: true },
                            { order: 4, propertyName: "Type", literal: true },
                            { order: 5, propertyName: "Mobile", literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "render"
                    },
                    {
                        fnName: phoneFormatter, 
                        fnArgs:  [
                            { order: 0, propertyName: "value", literal: false }
                        ],
                        fnType: "return",
                        fnOut: ["value"],
                        fnOutSource: [20],
                        fnWhen: "render"
                    },
                    {
                        fnName: phoneFormatter, 
                        fnArgs:  [
                            { order: 0, propertyName: "value", literal: false }
                        ],
                        fnType: "return",
                        fnOut: ["value"],
                        fnOutSource: [20],
                        fnWhen: "change"
                    }
                ],
                pattern: {value: phoneFormat, message: 'PhoneNumber must be in (123) 456-7890 format.'}, 
                minLength: 10, maxLength: 14, disabled: undefined,
                note: undefined, isSummary: false
            },
            {
                order: 21, label: 'Work Phone', value: "", type: "tel",
                fieldName: 'PersonPhone', display: undefined, visibility: [1],
                required: {value: false, message: undefined},
                renderLogic:{
                    isCombo: false, isText: false, isTel: true, isEmail: false, isDate: false, 
                    isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
                },
                templateModel: {
                    avfRequestFieldName: 'WorkPhone', avfValue: '',
                    avfSummary: { sectionName: 'No Title' },
                    expr: [
                        {
                            fnName: 'deriveValue',
                            fnArgs:  [
                                { order: 0, propertyName: "value", literal: false }
                            ],
                            fnType: "return",
                            fnOut: ["avfValue"],
                            fnOutSource: [21],
                            fnWhen: "template"
                        }
                    ]
                },
                updateModel: {
                    expr: [
                        {
                            fnName: 'derivePhoneRequestValue',
                            fnArgs:  [
                                { order: 0, propertyName: "value", literal: false },
                                { order: 1, propertyName: 21, literal: true },
                                { order: 2, propertyName: "demographics", literal: true },
                                { order: 3, propertyName: "number", literal: true },
                                { order: 4, propertyName: 'member', literal: true },
                                { order: 5, propertyName: 'type', literal: true },
                                { order: 6, propertyName: '02', literal: true },
                                { order: 7, propertyName: 'phone', literal: true },
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
                                { order: 1, propertyName: 21, literal: true },
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
                        fnName: deriveTypeBasedDisplayValue, 
                        fnArgs:  [
                            { order: 0, propertyName: "responseMBE", literal: true },
                            { order: 1, propertyName: "PhoneNumber", literal: true },
                            { order: 2, propertyName: 21, literal: true },
                            { order: 3, propertyName: "value", literal: true },
                            { order: 4, propertyName: "Type", literal: true },
                            { order: 5, propertyName: "Work", literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "render"
                    },
                    {
                        fnName: phoneFormatter, 
                        fnArgs:  [
                            { order: 0, propertyName: "value", literal: false }
                        ],
                        fnType: "return",
                        fnOut: ["value"],
                        fnOutSource: [21],
                        fnWhen: "render"
                    },
                    {
                        fnName: phoneFormatter, 
                        fnArgs:  [
                            { order: 0, propertyName: "value", literal: false }
                        ],
                        fnType: "return",
                        fnOut: ["value"],
                        fnOutSource: [21],
                        fnWhen: "change"
                    },
                    {
                        fnName: deriveEligibility, 
                        fnArgs:  [
                            { order: 0, propertyName: "eligibileEditModel", literal: true },
                            { order: 1, propertyName: "workPhone", literal: true },
                            { order: 2, propertyName: 21, literal: true },
                            { order: 3, propertyName: "disabled", literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "render"
                    },
                    {
                        fnName: updateSummaryGroup, 
                        fnArgs:  [
                            { order: 0, propertyName: "renderDataModel", literal: true },
                            { order: 1, propertyName: [22], literal: true },
                            { order: 2, propertyName: 'isSummary', literal: false },
                            { order: 3, propertyName: 21, literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: US2187347SwicthLabel === 'Y' ? "never" : "change"
                    }
                ],
                pattern: {value: phoneFormat, message: 'PhoneNumber must be in (123) 456-7890 format.'}, 
                minLength: 10, maxLength: 14, disabled: undefined,
                note: undefined, isSummary: false
            },
            {
                order: 22, label: 'Work Phone Ext', value: "", type: "text",
                fieldName: 'PersonPhone', display: undefined, visibility: US2187347SwicthLabel === 'Y' ? [] : [1],
                required: {value: undefined, message: undefined},
                renderLogic:{
                    isCombo: false, isText: true, isTel: false, isEmail: false, isDate: false, 
                    isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
                },
                templateModel: {
                    avfRequestFieldName: 'WorkPhoneExt', avfValue: '',
                    avfSummary: { sectionName: 'No Title' },
                    expr: [
                        {
                            fnName: 'deriveValue',
                            fnArgs:  [
                                { order: 0, propertyName: "value", literal: false }
                            ],
                            fnType: "return",
                            fnOut: ["avfValue"],
                            fnOutSource: [22],
                            fnWhen: "template"
                        }
                    ]
                },
                updateModel: {
                    expr: [
                        {
                            fnName: 'derivePhoneExtRequestValue',
                            fnArgs:  [
                                { order: 0, propertyName: "value", literal: false },
                                { order: 1, propertyName: 22, literal: true },
                                { order: 2, propertyName: "demographics", literal: true },
                                { order: 3, propertyName: "extension", literal: true },
                                { order: 4, propertyName: 'member', literal: true },
                                { order: 5, propertyName: 'type', literal: true },
                                { order: 6, propertyName: '02', literal: true },
                                { order: 7, propertyName: 'phone', literal: true },
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
                                { order: 1, propertyName: 22, literal: true },
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
                        fnName: deriveTypeBasedDisplayValue, 
                        fnArgs:  [
                            { order: 0, propertyName: "responseMBE", literal: true },
                            { order: 1, propertyName: "Extension", literal: true },
                            { order: 2, propertyName: 22, literal: true },
                            { order: 3, propertyName: "value", literal: true },
                            { order: 4, propertyName: "Type", literal: true },
                            { order: 5, propertyName: "Work", literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "render"
                    },
                    {
                        fnName: updateSummaryGroup, 
                        fnArgs:  [
                            { order: 0, propertyName: "renderDataModel", literal: true },
                            { order: 1, propertyName: [21], literal: true },
                            { order: 2, propertyName: 'isSummary', literal: false },
                            { order: 3, propertyName: 22, literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "change"
                    },
                    {
                        fnName: deriveEligibility, 
                        fnArgs:  [
                            { order: 0, propertyName: "eligibileEditModel", literal: true },
                            { order: 1, propertyName: "workPhone", literal: true },
                            { order: 2, propertyName: 22, literal: true },
                            { order: 3, propertyName: "disabled", literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "render"
                    }
                ],
                pattern: {value: phoneExtFormat, message: 'Extension should consist 4 digits; no less no more.'}, 
                minLength: 0, maxLength: 4, disabled: undefined,
                note: undefined, isSummary: false
            }
        ];
    }

    displayButtons = () => {
        return [
            {
                label: "Verify Mailing Address", title: "Standardize Address", 
                order: 2, setDisable: undefined,
                class: undefined, standardStatus: false, 
                requestAVObj: this.MailingAddressListRequest, responseAVObj: this.MailingAddressListResponse,
                expr: [
                    {
                        fnName: deriveAVRequestObj, 
                        fnArgs:  [
                            { order: 0, propertyName: "requestAVObj", literal: false },
                            { order: 1, propertyName: "drivenFieldModel", literal: true },
                            { order: 2, propertyName: [12, 13, 14, 15, 16], literal: true }
                        ],
                        fnType: "return",
                        fnOut: ["requestAVObj"],
                        fnOutSource: [2],
                        fnWhen: "render"
                    },
                    {
                        fnName: checkValidity, 
                        fnArgs:  [
                            { order: 0, propertyName: "requestAVObj", literal: false },
                            { order: 1, propertyName: ['AddressLine1', 'City', 'StateCode', 'ZipCode'], literal: true }
                        ],
                        fnType: "return",
                        fnOut: ["setDisable"],
                        fnOutSource: [2],
                        fnWhen: "render"
                    },
                    {
                        fnName: injectToField, 
                        fnArgs:  [
                            { order: 0, propertyName: "16", literal: true },
                            { order: 1, propertyName: "drivenFieldModel", literal: true },
                            { order: 2, propertyName: "drivenButtonModel", literal: true },
                            { order: 3, propertyName: 2, literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "render"
                    },
                    {
                        fnName: function() {
                            return [12, 13, 14, 15, 16];
                        }, 
                        fnArgs:  [],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "click"
                    },
                    {
                        fnName: checkBtnEligibility, 
                        fnArgs:  [
                            { order: 0, propertyName: "drivenFieldModel", literal: true },
                            { order: 1, propertyName: "drivenButtonModel", literal: true },
                            { order: 2, propertyName: [12, 14, 15, 16], literal: true },
                            { order: 3, propertyName: 2, literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "render"
                    }
                ]
            }
        ];
    }
}