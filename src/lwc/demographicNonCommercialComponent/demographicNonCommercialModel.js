import { deriveDisplayValue, deriveAddress, deriveResidentialAddress, deriveEmail, updateSSNValue, 
    derivePhoneNumber, deriveOptionVisibility, deriveOptionDisability, updateSummaryGroup, 
    deriveTypeBasedDisplayValue, deriveEditDisplayValue, deriveEligibility, derivePermissionSetCheck } from './demographicNonCommercialHelper';
import { ageCalculator, ssnEncrypter, phoneFormatter, dateDeformatter } from 'c/crmserviceHelper';
import { phoneFormat } from 'c/crmserviceHelper';
import { phoneExtFormat } from 'c/crmserviceHelper';
import US2081540_SwitchLabel from '@salesforce/label/c.US2081540_SwitchLabel';
import MBEResidentialAddressType from '@salesforce/label/c.MBEResidentialAddressType';
import MBEMailingAddressType from '@salesforce/label/c.MBEMailingAddressType';
import MBEHomeEmailType from '@salesforce/label/c.MBEHomeEmailType';
import MBEWorkEmailType from '@salesforce/label/c.MBEWorkEmailType';
import MBEHomePhoneType from '@salesforce/label/c.MBEHomePhoneType';
import MBEMobilePhoneType from '@salesforce/label/c.MBEMobilePhoneType';
import MBEWorkPhoneType from '@salesforce/label/c.MBEWorkPhoneType';
import MBEWorkPhoneExtType from '@salesforce/label/c.MBEWorkPhoneExtType';
import US1441116SwitchLabel from '@salesforce/label/c.US1441116_Switch_Label';
import US1441116_GuidanceMessage from '@salesforce/label/c.US1441116_GuidanceMessage';
import US1441116_ContactFields from '@salesforce/label/c.US1441116_ContactFields';
import US1441116_CriticalFields from '@salesforce/label/c.US1441116_CriticalFields';
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
                    fnName: ssnEncrypter,
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
       {
           order: 8, label: "Residential Address", value: "", type: "text", 
           fieldName: "PersonAddress",
           required: {value: undefined, message: undefined},
           renderLogic:{
               isCombo: true, isText: true, isTel: false, isEmail: false, isDate: false, 
               isCheck: false, isRadio: false, isRadioGroup: false, isTextArea: false
           },
           expr: [
               {
                   fnName: deriveResidentialAddress, 
                   fnArgs:  [
                       { order: 0, propertyName: "AddressLine1", literal: true },
                       { order: 1, propertyName: "AddressLine2", literal: true },
                       { order: 2, propertyName: "City", literal: true },
                       { order: 3, propertyName: "StateCode", literal: true },
                       { order: 4, propertyName: "ZipCode", literal: true },
                       { order: 5, propertyName: "County", literal: true },
                       { order: 6, propertyName: "StartDate", literal: true },
                       { order: 7, propertyName: "EndDate", literal: true },
                       { order: 8, propertyName: "Type", literal: true },
                       { order: 9, propertyName: (US2081540_SwitchLabel.toUpperCase() === 'Y' ? MBEResidentialAddressType : "Residential"), literal: true }, //Changed hardcoded value Residential to custom label
                       { order: 10, propertyName: "currentPlatform", literal: true }
                   ],
                   fnType: "return",
                   fnOut: ["value"],
                   fnOutSource: [8],
                   fnWhen: "render"
               }
           ],
           pattern: {value: undefined, message: undefined}, 
           minLength: undefined, maxLength: undefined, disabled: undefined,
           note: undefined, isSummary: false
       },
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
                       { order: 7, propertyName: (US2081540_SwitchLabel.toUpperCase() === 'Y' ? MBEMailingAddressType : "Platform"), literal: true }, //Changed hardcoded value Platform to custom label
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
                       { order: 2, propertyName: (US2081540_SwitchLabel.toUpperCase() === 'Y' ? MBEHomeEmailType : "Primary"), literal: true } //Changed hardcoded value Primary to custom label
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
                       { order: 2, propertyName: (US2081540_SwitchLabel.toUpperCase() === 'Y' ? MBEWorkEmailType : "Alternate"), literal: true } ////Changed hardcoded value Alternate to custom label
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
                       { order: 2, propertyName: (US2081540_SwitchLabel.toUpperCase() === 'Y' ? MBEHomePhoneType : "Home"), literal: true } //Changed hardcoded value Home to custom label
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
       {
           order: 13, label: 'Mobile', value: "", type: "text",
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
                       { order: 2, propertyName: (US2081540_SwitchLabel.toUpperCase() === 'Y' ? MBEMobilePhoneType : "Mobile"), literal: true } //Changed hardcoded value Mobile to Cell using custom label
                   ],
                   fnType: "return",
                   fnOut: ["value"],
                   fnOutSource: [13],
                   fnWhen: "render"
               },
               {
                   fnName: phoneFormatter, 
                   fnArgs:  [
                       { order: 0, propertyName: "value", literal: false }
                   ],
                   fnType: "return",
                   fnOut: ["value"],
                   fnOutSource: [13],
                   fnWhen: "render"
               }
           ],
           pattern: {value: undefined, message: undefined}, 
           minLength: undefined, maxLength: undefined, disabled: undefined,
           note: undefined, isSummary: false
       },
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
                       { order: 2, propertyName: (US2081540_SwitchLabel.toUpperCase() === 'Y' ? MBEWorkPhoneType : "Work"), literal: true } //Changed hardcoded value Work to custom label
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
                message: US1441116_ContactFields, 
                class: 'flow-note', show: true 
            }] : 
            US1441116_ContactFields
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
               },
               {    //US1441116 Start - This function works in disabling Critical Demographic Update checkbox    
                    fnName: derivePermissionSetCheck,
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
			[
                {   message: US1441116_CriticalFields, 
                    class: 'flow-note', show: true 
                },
                { 
                    message: US1441116_GuidanceMessage,
                    class: 'flow-note flow-note-color', 
                    show: derivePermissionSetCheck.apply(this), 
                    isMsg: true 
                }
            ] : 
            US1441116_CriticalFields
            //US1441116 End
       },
       {   order: 3, label: 'Address Update', value: 'MAU', visible: undefined,
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
            }]  :  
            US1441116_MAUFields
            //US1441116 End		
       },
       {   order: 4, label: 'Address Update', value: 'MDU', visible: undefined,
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
            }]  : 
            US1441116_MDUFields
            //US1441116 End
       }
   ];
}

}

export class DrivenComponent {

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
                expr: [{
                    fnName: 'deriveValue',
                    fnArgs:  [
                        { order: 0, propertyName: "value", literal: false }
                    ],
                    fnType: "return",
                    fnOut: ["avfValue"],
                    fnOutSource: [5],
                    fnWhen: "template"
                }]
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
            ],
            pattern: {value: undefined, message: undefined}, 
            minLength: undefined, maxLength: undefined, disabled: undefined,
            note: undefined, isSummary: false
        },
        {
            order: 6, label: "Social Security Number", value: "", type: "text", 
            fieldName: "Ssn", display: undefined, visibility: [], ssnValue: '',
            required: {value: true, message: 'SSN is required'},
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
                    fnName: ssnEncrypter,
                    fnArgs:  [
                        { order: 0, propertyName: "value", literal: false }
                    ],
                    fnType: "return",
                    fnOut: ["value"],
                    fnOutSource: [6],
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
            pattern: {value: undefined, message: undefined}, 
            minLength: 9, maxLength: 11, disabled: undefined,
            note: undefined, isSummary: false
        },
        {
            order: 7, label: "Home Email", value: "", type: "email", 
            fieldName: "PersonEmail", display: undefined, visibility: [1],
            required: {value: false, message: 'Home Email is required'},
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
                        fnOutSource: [7],
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
                            { order: 1, propertyName: 7, literal: true },
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
                        { order: 1, propertyName: "Address", literal: true },
                        { order: 2, propertyName: 7, literal: true },
                        { order: 3, propertyName: "value", literal: true },
                        { order: 4, propertyName: "Type", literal: true },
                        { order: 5, propertyName: (US2081540_SwitchLabel.toUpperCase() === 'Y' ? MBEHomeEmailType : "Primary"), literal: true } //Changed hardcoded value Primary to custom label
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
                            { order: 2, propertyName: 7, literal: true },
                            { order: 3, propertyName: "disabled", literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "render"
                    }
            ],
            pattern: {value: undefined, message: undefined}, 
            minLength: undefined, maxLength: 60, disabled: undefined,
            note: undefined, isSummary: false
        },
        {
            order: 8, label: "Work Email", value: "", type: "email",
            fieldName: 'PersonEmail', display: undefined, visibility: [1],
            required: { value: false, message: 'Work Email is required' }, 
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
                        fnOutSource: [8],
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
                            { order: 1, propertyName: 8, literal: true },
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
                            { order: 1, propertyName: 8, literal: true },
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
                        { order: 2, propertyName: 8, literal: true },
                        { order: 3, propertyName: "value", literal: true },
                        { order: 4, propertyName: "Type", literal: true },
                        { order: 5, propertyName: (US2081540_SwitchLabel.toUpperCase() === 'Y' ? MBEWorkEmailType : "Alternate"), literal: true } //Changed hardcoded value Alternate to custom label
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
                            { order: 2, propertyName: 8, literal: true },
                            { order: 3, propertyName: "disabled", literal: true }
                        ],
                        fnType: "void",
                        fnOut: undefined,
                        fnOutSource: undefined,
                        fnWhen: "render"
                    }
            ],
            pattern: {value: undefined, message: undefined}, 
            minLength: undefined, maxLength: 60, disabled: undefined,
            note: undefined, isSummary: false
        },
        {
            order: 9, label: 'Home Phone', value: "", type: "tel",
            fieldName: 'PersonPhone', display: undefined, visibility: [1],
            required: {value: true, message: 'Home Phone is required'},
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
                        fnOutSource: [9],
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
                            { order: 1, propertyName: 9, literal: true },
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
                            { order: 1, propertyName: 9, literal: true },
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
                        { order: 2, propertyName: 9, literal: true },
                        { order: 3, propertyName: "value", literal: true },
                        { order: 4, propertyName: "Type", literal: true },
                        { order: 5, propertyName: (US2081540_SwitchLabel.toUpperCase() === 'Y' ? MBEHomePhoneType : "Home"), literal: true } //Changed hardcoded value Home to custom label
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
                    fnOutSource: [9],
                    fnWhen: "render"
                },
                {
                    fnName: phoneFormatter, 
                    fnArgs:  [
                        { order: 0, propertyName: "value", literal: false }
                    ],
                    fnType: "return",
                    fnOut: ["value"],
                    fnOutSource: [9],
                    fnWhen: "change"
                },
                {
                        fnName: deriveEligibility, 
                        fnArgs:  [
                            { order: 0, propertyName: "eligibileEditModel", literal: true },
                            { order: 1, propertyName: "homePhone", literal: true },
                            { order: 2, propertyName: 9, literal: true },
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
            order: 10, label: 'Mobile', value: "", type: "tel",
            fieldName: 'PersonPhone', display: undefined, visibility: [1],
            required: {value: false, message: 'Mobile is required'},
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
                        fnOutSource: [10],
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
                            { order: 1, propertyName: 10, literal: true },
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
                            { order: 1, propertyName: 10, literal: true },
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
                        { order: 2, propertyName: 10, literal: true },
                        { order: 3, propertyName: "value", literal: true },
                        { order: 4, propertyName: "Type", literal: true },
                        { order: 5, propertyName: (US2081540_SwitchLabel.toUpperCase() === 'Y' ? MBEMobilePhoneType : "Mobile"), literal: true } //Changed hardcoded value Mobile to Cell using custom label
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
                    fnOutSource: [10],
                    fnWhen: "render"
                },
                {
                    fnName: phoneFormatter, 
                    fnArgs:  [
                        { order: 0, propertyName: "value", literal: false }
                    ],
                    fnType: "return",
                    fnOut: ["value"],
                    fnOutSource: [10],
                    fnWhen: "change"
                }
            ],
            pattern: {value: phoneFormat, message: 'PhoneNumber must be in (123) 456-7890 format.'}, 
            minLength: 10, maxLength: 14, disabled: undefined,
            note: undefined, isSummary: false
        },
        {
            order: 11, label: 'Work Phone', value: "", type: "tel",
            fieldName: 'PersonPhone', display: undefined, visibility: [1],
            required: {value: false, message: 'Work Phone is required'},
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
                        fnOutSource: [11],
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
                            { order: 1, propertyName: 11, literal: true },
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
                            { order: 1, propertyName: 11, literal: true },
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
                        { order: 2, propertyName: 11, literal: true },
                        { order: 3, propertyName: "value", literal: true },
                        { order: 4, propertyName: "Type", literal: true },
                        { order: 5, propertyName: (US2081540_SwitchLabel.toUpperCase() === 'Y' ? MBEWorkPhoneType : "Work"), literal: true } //Changed hardcoded value Work to custom label
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
                    fnOutSource: [11],
                    fnWhen: "render"
                },
                {
                    fnName: phoneFormatter, 
                    fnArgs:  [
                        { order: 0, propertyName: "value", literal: false }
                    ],
                    fnType: "return",
                    fnOut: ["value"],
                    fnOutSource: [11],
                    fnWhen: "change"
                },
                {
                    fnName: deriveEligibility, 
                    fnArgs:  [
                        { order: 0, propertyName: "eligibileEditModel", literal: true },
                        { order: 1, propertyName: "workPhone", literal: true },
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
                        { order: 1, propertyName: [12], literal: true },
                        { order: 2, propertyName: 'isSummary', literal: false },
                        { order: 3, propertyName: 11, literal: true }
                    ],
                    fnType: "void",
                    fnOut: undefined,
                    fnOutSource: undefined,
                    fnWhen: "change"
                }
            ],
            pattern: {value: phoneFormat, message: 'PhoneNumber must be in (123) 456-7890 format.'}, 
            minLength: 10, maxLength: 14, disabled: undefined,
            note: undefined, isSummary: false
        },
        {
            order: 12, label: 'Work Phone Ext', value: "", type: "text",
            fieldName: 'PersonPhone', display: undefined, visibility: [1],
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
                        fnOutSource: [12],
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
                            { order: 1, propertyName: 12, literal: true },
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
                        { order: 1, propertyName: "Extension", literal: true },
                        { order: 2, propertyName: 12, literal: true },
                        { order: 3, propertyName: "value", literal: true },
                        { order: 4, propertyName: "Type", literal: true },
                        { order: 5, propertyName: (US2081540_SwitchLabel.toUpperCase() === 'Y' ? MBEWorkPhoneExtType : "Work"), literal: true } //Changed hardcoded value Work to custom label
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
                        { order: 1, propertyName: [11], literal: true },
                        { order: 2, propertyName: 'isSummary', literal: false },
                        { order: 3, propertyName: 12, literal: true }
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
                        { order: 2, propertyName: 12, literal: true },
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