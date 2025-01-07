import { deriveDisplayValue, deriveAddress, deriveEmail, 
         derivePhoneNumber, deriveOptionVisibility, deriveOptionDisability } from './demographicCommercialHelper';
import { ageCalculator, ssnFormatter, phoneFormatter } from 'c/crmserviceHelper';
import US1441116SwitchLabel from '@salesforce/label/c.US1441116_Switch_Label';
import US1441116_ContactCommFields from '@salesforce/label/c.US1441116_ContactCommFields';
import US1441116_CriticalCommFields from '@salesforce/label/c.US1441116_CriticalCommFields';
import US1441116_MAUFields from '@salesforce/label/c.US1441116_MAUFields';
import US1441116_MDUFields from '@salesforce/label/c.US1441116_MDUFields';

export class DisplayComponent {

    displayFields = () => {
        return [
            {
                order: 1, label: "First Name", value: "", type: "text", 
                fieldName: "FirstName", 
                required: {value: undefined, message: undefined},
                renderLogic:{
                    isCombo: false, isText: true, isTel: false, isEmail: false, isDate: false, 
                    isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
                },
                expr: [
                    {
                        fnName: deriveDisplayValue, 
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
                    }
                ],
                pattern: {value: undefined, message: undefined}, 
                minLength: undefined, maxLength: undefined, disabled: undefined,
                note: undefined, isSummary: false
            },
            {
                order: 2, label: "Middle Initial", value: "", type: "text", 
                fieldName: "MiddleInitial",
                required: {value: undefined, message: undefined},
                renderLogic:{
                    isCombo: false, isText: true, isTel: false, isEmail: false, isDate: false, 
                    isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
                },
                expr: [
                    {
                        fnName: deriveDisplayValue, 
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
                    }
                ],
                pattern: {value: undefined, message: undefined}, 
                minLength: undefined, maxLength: undefined, disabled: undefined,
                note: undefined, isSummary: false
            },
            {
                order: 3, label: "Last Name", value: "", type: "text", 
                fieldName: "LastName",
                required: {value: undefined, message: undefined},
                renderLogic:{
                    isCombo: false, isText: true, isTel: false, isEmail: false, isDate: false, 
                    isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
                },
                expr: [
                    {
                        fnName: deriveDisplayValue, 
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
                    }
                ],
                pattern: {value: undefined, message: undefined}, 
                minLength: undefined, maxLength: undefined, disabled: undefined,
                note: undefined, isSummary: false
            },
            {
                order: 4, label: "Gender", value: "", 
                type: "text", fieldName: "Gender",
                required: {value: undefined, message: undefined},
                renderLogic:{
                    isCombo: false, isText: true, isTel: false, isEmail: false, isDate: false, 
                    isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
                },
                expr: [
                    {
                        fnName: deriveDisplayValue, 
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
                    }
                ],
                pattern: {value: undefined, message: undefined}, 
                minLength: undefined, maxLength: undefined, disabled: undefined,
                note: undefined, isSummary: false
            },
            {
                order: 5, label: "Birthdate", value: "", type: "text", 
                fieldName: "DateOfBirth",
                required: {value: undefined, message: undefined},
                renderLogic:{
                    isCombo: false, isText: true, isTel: false, isEmail: false, isDate: false, 
                    isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
                },
                expr: [
                    {
                        fnName: deriveDisplayValue, 
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
                    }
                ],
                pattern: {value: undefined, message: undefined}, 
                minLength: undefined, maxLength: undefined, disabled: undefined,
                note: undefined, isSummary: false
            },
            {
                order: 6, label: "Age", value: "", type: "text", 
                fieldName: "DateOfBirth",
                required: {value: undefined, message: undefined},
                renderLogic:{
                    isCombo: false, isText: true, isTel: false, isEmail: false, isDate: false, 
                    isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
                },
                expr: [
                    {
                        fnName: deriveDisplayValue, 
                        fnArgs:  [
                            { order: 0, propertyName: "responseMBE", literal: true },
                            { order: 1, propertyName: "DateOfBirth", literal: true },
                            { order: 2, propertyName: 6, literal: true },
                            { order: 3, propertyName: "value", literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "render"
                    },
                    {
                        fnName: ageCalculator, 
                        fnArgs:  [
                            { order: 0, propertyName: "value", literal: false }
                        ],
                        fnType: "return",
                        fnOut: ["value"],
                        fnOutSource: [6],
                        fnWhen: "render"
                    },
                ],
                pattern: {value: undefined, message: undefined}, 
                minLength: undefined, maxLength: undefined, disabled: undefined,
                note: undefined, isSummary: false
            },
            {
                order: 7, label: "Social Security Number", value: "", type: "text", 
                fieldName: "Ssn", ssnValue: '',
                required: {value: undefined, message: undefined},
                renderLogic:{
                    isCombo: false, isText: true, isTel: false, isEmail: false, isDate: false, 
                    isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
                },
                expr: [
                    {
                        fnName: deriveDisplayValue, 
                        fnArgs:  [
                            { order: 0, propertyName: "responseMBE", literal: true },
                            { order: 1, propertyName: "Ssn", literal: true },
                            { order: 2, propertyName: 7, literal: true },
                            { order: 3, propertyName: "value", literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "render"
                    },
                    {
                        fnName: deriveDisplayValue, 
                        fnArgs:  [
                            { order: 0, propertyName: "responseMBE", literal: true },
                            { order: 1, propertyName: "Ssn", literal: true },
                            { order: 2, propertyName: 7, literal: true },
                            { order: 3, propertyName: "ssnValue", literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "render"
                    },
                    {
                        fnName: ssnFormatter,
                        fnArgs:  [
                            { order: 0, propertyName: "value", literal: false }
                        ],
                        fnType: "return",
                        fnOut: ["value"],
                        fnOutSource: [7],
                        fnWhen: "render"
                    }
                ],
                pattern: {value: undefined, message: undefined}, 
                minLength: undefined, maxLength: undefined, disabled: undefined,
                note: undefined, isSummary: false
            },
            // {
            //     order: 8, label: "Residential Address", value: "", type: "text", 
            //     fieldName: "PersonAddress",
            //     required: {value: undefined, message: undefined},
            //     renderLogic:{
            //         isCombo: true, isText: true, isTel: false, isEmail: false, isDate: false, 
            //         isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
            //     },
            //     expr: [
            //         {
            //             fnName: deriveAddress, 
            //             fnArgs:  [
            //                 { order: 0, propertyName: "AddressLine1", literal: true },
            //                 { order: 1, propertyName: "City", literal: true },
            //                 { order: 2, propertyName: "StateCode", literal: true },
            //                 { order: 3, propertyName: "ZipCode", literal: true },
            //                 { order: 4, propertyName: "County", literal: true },
            //                 { order: 5, propertyName: "Type", literal: true },
            //                 { order: 6, propertyName: "Residential", literal: true },
            //                 { order: 7, propertyName: "EM", literal: true }
            //             ],
            //             fnType: "return",
            //             fnOut: ["value"],
            //             fnOutSource: [8],
            //             fnWhen: "render"
            //         }
            //     ],
            //     pattern: {value: undefined, message: undefined}, 
            //     minLength: undefined, maxLength: undefined, disabled: undefined,
            //     note: undefined, isSummary: false
            // },
            {
                order: 9, label: "Mailing Address", value: "", type: "text", 
                fieldName: "PersonAddress",
                required: {value: undefined, message: undefined},
                renderLogic:{
                    isCombo: false, isText: true, isTel: false, isEmail: false, isDate: false, 
                    isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
                },
                expr: [
                    {
                        fnName: deriveAddress, 
                        fnArgs:  [
                            { order: 0, propertyName: "AddressLine1", literal: true },
                            { order: 1, propertyName: "AddressLine2", literal: true },
                            { order: 2, propertyName: "City", literal: true },
                            { order: 3, propertyName: "StateCode", literal: true },
                            { order: 4, propertyName: "ZipCode", literal: true },
                            { order: 5, propertyName: "County", literal: true },
                            { order: 6, propertyName: "Type", literal: true },
                            { order: 7, propertyName: "Platform", literal: true },
                            { order: 8, propertyName: "currentPlatform", literal: true }
                        ],
                        fnType: "return",
                        fnOut: ["value"],
                        fnOutSource: [9],
                        fnWhen: "render"
                    }
                ],
                pattern: {value: undefined, message: undefined}, 
                minLength: undefined, maxLength: undefined, disabled: undefined,
                note: undefined, isSummary: false
            },
            {
                order: 10, label: "Home Email", value: "", type: "text", 
                fieldName: "PersonEmail",
                required: {value: undefined, message: undefined},
                renderLogic:{
                    isCombo: false, isText: true, isTel: false, isEmail: false, isDate: false, 
                    isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
                },
                expr: [
                    {
                        fnName: deriveEmail, 
                        fnArgs:  [
                            { order: 0, propertyName: "Address", literal: true },
                            { order: 1, propertyName: "Type", literal: true },
                            { order: 2, propertyName: "Primary", literal: true }
                        ],
                        fnType: "return",
                        fnOut: ["value"],
                        fnOutSource: [10],
                        fnWhen: "render"
                    }
                ],
                pattern: {value: undefined, message: undefined}, 
                minLength: undefined, maxLength: undefined, disabled: undefined,
                note: undefined, isSummary: false
            },
            {
                order: 11, label: "Work Email", value: "", type: "text",
                fieldName: 'PersonEmail',
                required: { value: undefined, message: undefined }, 
                renderLogic:{
                    isCombo: false, isText: true, isTel: false, isEmail: false, isDate: false, 
                    isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
                },
                expr: [
                    {
                        fnName: deriveEmail, 
                        fnArgs:  [
                            { order: 0, propertyName: "Address", literal: true },
                            { order: 1, propertyName: "Type", literal: true },
                            { order: 2, propertyName: "Alternate", literal: true }
                        ],
                        fnType: "return",
                        fnOut: ["value"],
                        fnOutSource: [11],
                        fnWhen: "render"
                    }
                ],
                pattern: {value: undefined, message: undefined}, 
                minLength: undefined, maxLength: undefined, disabled: undefined,
                note: undefined, isSummary: false
            },
            {
                order: 12, label: 'Home Phone', value: "", type: "text",
                fieldName: 'PersonPhone',
                required: {value: undefined, message: undefined},
                renderLogic:{
                    isCombo: false, isText: true, isTel: false, isEmail: false, isDate: false, 
                    isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
                },
                expr: [
                    {
                        fnName: derivePhoneNumber, 
                        fnArgs:  [
                            { order: 0, propertyName: "PhoneNumber", literal: true },
                            { order: 1, propertyName: "Type", literal: true },
                            { order: 2, propertyName: "Home", literal: true }
                        ],
                        fnType: "return",
                        fnOut: ["value"],
                        fnOutSource: [12],
                        fnWhen: "render"
                    },
                    {
                        fnName: phoneFormatter, 
                        fnArgs:  [
                            { order: 0, propertyName: "value", literal: false }
                        ],
                        fnType: "return",
                        fnOut: ["value"],
                        fnOutSource: [12],
                        fnWhen: "render"
                    }
                ],
                pattern: {value: undefined, message: undefined}, 
                minLength: undefined, maxLength: undefined, disabled: undefined,
                note: undefined, isSummary: false
            },
            // {
            //     order: 13, label: 'Mobile', value: "", type: "text",
            //     fieldName: 'PersonPhone',
            //     required: {value: undefined, message: undefined},
            //     renderLogic:{
            //         isCombo: false, isText: true, isTel: false, isEmail: false, isDate: false, 
            //         isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
            //     },
            //     expr: [
            //         {
            //             fnName: derivePhoneNumber, 
            //             fnArgs:  [
            //                 { order: 0, propertyName: "PhoneNumber", literal: true },
            //                 { order: 1, propertyName: "Type", literal: true },
            //                 { order: 2, propertyName: "Mobile", literal: true }
            //             ],
            //             fnType: "return",
            //             fnOut: ["value"],
            //             fnOutSource: [13],
            //             fnWhen: "render"
            //         },
            //         {
            //             fnName: phoneFormatter, 
            //             fnArgs:  [
            //                 { order: 0, propertyName: "value", literal: false }
            //             ],
            //             fnType: "return",
            //             fnOut: ["value"],
            //             fnOutSource: [13],
            //             fnWhen: "render"
            //         }
            //     ],
            //     pattern: {value: undefined, message: undefined}, 
            //     minLength: undefined, maxLength: undefined, disabled: undefined,
            //     note: undefined, isSummary: false
            // },
            {
                order: 14, label: 'Work Phone', value: "", type: "text",
                fieldName: 'PersonPhone',
                required: {value: undefined, message: undefined},
                renderLogic:{
                    isCombo: false, isText: true, isTel: false, isEmail: false, isDate: false, 
                    isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
                },
                expr: [
                    {
                        fnName: derivePhoneNumber, 
                        fnArgs:  [
                            { order: 0, propertyName: "PhoneNumber", literal: true },
                            { order: 1, propertyName: "Type", literal: true },
                            { order: 2, propertyName: "Work", literal: true }
                        ],
                        fnType: "return",
                        fnOut: ["value"],
                        fnOutSource: [14],
                        fnWhen: "render"
                    },
                    {
                        fnName: phoneFormatter, 
                        fnArgs:  [
                            { order: 0, propertyName: "value", literal: false }
                        ],
                        fnType: "return",
                        fnOut: ["value"],
                        fnOutSource: [14],
                        fnWhen: "render"
                    }
                ],
                pattern: {value: undefined, message: undefined}, 
                minLength: undefined, maxLength: undefined, disabled: undefined,
                note: undefined, isSummary: false
            }
        ];
    }
    
}

export class DriverComponent {

    displayOptions = () => {
        return [
            {   order: 1, label: 'Contact Demographic Update', value: 'COD', visible: undefined,
                selected: undefined, disabled: undefined, status: undefined, isAVF: false,
                title: 'Contact Demographic Update Summary', eventName: 'launchsummary',
                expr: [
                    {
                        fnName: deriveOptionVisibility,
                        fnArgs: [
                            { order: 0, propertyName: "always", literal: true}
                        ],
                        fnType: 'return',
                        fnOut: ['visible'],
                        fnOutSource: [1],
                        fnWhen: 'render'
                    },
                    {
                        fnName: deriveOptionDisability,
                        fnArgs: [
                            { order: 0, propertyName: "isServiceFailed", literal: true}
                        ],
                        fnType: 'return',
                        fnOut: ['disabled'],
                        fnOutSource: [1],
                        fnWhen: 'render'
                    }
                ],
                //US1441116 Start - Updated description into Array format to maintain uniformity 
                description: US1441116SwitchLabel.toUpperCase() === 'Y' ? 
                [{ 
                    message: US1441116_ContactCommFields, class: 'flow-note', show: true 
                }]  : US1441116_ContactCommFields
                //US1441116 End 
            },
            {   order: 2, label: 'Critical Demographic Update', value: 'CRD',  visible: undefined, 
                selected: undefined, disabled: undefined, status: undefined, isAVF: false,
                title: 'Critical Demographic Update Summary', eventName: 'launchsummary',
                expr: [
                    {
                        fnName: deriveOptionVisibility,
                        fnArgs: [
                            { order: 0, propertyName: "always", literal: true}
                        ],
                        fnType: 'return',
                        fnOut: ['visible'],
                        fnOutSource: [2],
                        fnWhen: 'render'
                    },
                    {
                        fnName: deriveOptionDisability,
                        fnArgs: [
                            { order: 0, propertyName: "isServiceFailed", literal: true}
                        ],
                        fnType: 'return',
                        fnOut: ['disabled'],
                        fnOutSource: [2],
                        fnWhen: 'render'
                    }
                ],
                //US1441116 Start - Updated description into Array format to maintain uniformity 
                description: US1441116SwitchLabel.toUpperCase() === 'Y' ? 
                [{ 
                    message: US1441116_CriticalCommFields, class: 'flow-note', show: true 
                }]  : US1441116_CriticalCommFields
                //US1441116 End
            },
            {   order: 3, label: 'Medicare Address Update', value: 'MAU', visible: undefined,
                selected: undefined, disabled: undefined, status: undefined, isAVF: true,
                title: 'Medicare Address Update Job Aid', eventName: 'launchavf',
                expr: [
                    {
                        fnName: deriveOptionVisibility,
                        fnArgs: [
                            { order: 0, propertyName: "isMedicare", literal: true}
                        ],
                        fnType: 'return',
                        fnOut: ['visible'],
                        fnOutSource: [3],
                        fnWhen: 'render'
                    },
                    {
                        fnName: deriveOptionDisability,
                        fnArgs: [
                            { order: 0, propertyName: "isServiceFailed", literal: true}
                        ],
                        fnType: 'return',
                        fnOut: ['disabled'],
                        fnOutSource: [3],
                        fnWhen: 'render'
                    }
                ],
                //US1441116 Start - Updated description into Array format to maintain uniformity 
                description: US1441116SwitchLabel.toUpperCase() === 'Y' ? 
                [{ 
                    message: US1441116_MAUFields, class: 'flow-note', show: true 
                }]  : US1441116_MAUFields
                //US1441116 End
            },
            {   order: 4, label: 'Medicaid/Supp Address Update', value: 'MDU', visible: undefined,
                selected: undefined, disabled: undefined, status: undefined, isAVF: true,
                title: 'Medicaid Address Update Job Aid', eventName: 'launchmedicaid',
                expr: [
                    {
                        fnName: deriveOptionVisibility,
                        fnArgs: [
                            { order: 0, propertyName: "isMedicaid", literal: true}
                        ],
                        fnType: 'return',
                        fnOut: ['visible'],
                        fnOutSource: [4],
                        fnWhen: 'render'
                    },
                    {
                        fnName: deriveOptionDisability,
                        fnArgs: [
                            { order: 0, propertyName: "isServiceFailed", literal: true}
                        ],
                        fnType: 'return',
                        fnOut: ['disabled'],
                        fnOutSource: [4],
                        fnWhen: 'render'
                    }
                ],
                //US1441116 Start - Updated description into Array format to maintain uniformity 
                description: US1441116SwitchLabel.toUpperCase() === 'Y' ? 
                [{ 
                    message: US1441116_MDUFields, class: 'flow-note', show: true 
                }]  : US1441116_MDUFields
                //US1441116 End
            }
        ];
    }

}

export class EligibilityModel {

    groupRecord = {
        productTypeCode: '',
        productType: '',
        product: '',
        platformCode: '',
        groupId: '',
        exchangeType: '',
        exchangeIndicator: false,
        ediGroupIndicator: false,
        dualDemoIndicator: false,
        asoIndicator: false
    }

    editableDemographicType = {
        workPhone: "",
        workEmail: "",
        ssn: "",
        residentialAddress: "",
        name: "",
        mailingAddress: "",
        homePhone: "",
        homeEmail: "",
        gender: "",
        dob: ""
    }

    eligibility = {
        template: '',
        ruleName: '',
        iseligible: false,
        heirarchy: 0,
        groupRecord: this.groupRecord,
        error: false,
        criticalHierarchyMessage: '',
        contactHierarchyMessage: '',
        routingMessage: '',
        editableDemographicType: this.editableDemographicType,
        message: ''
    };
}