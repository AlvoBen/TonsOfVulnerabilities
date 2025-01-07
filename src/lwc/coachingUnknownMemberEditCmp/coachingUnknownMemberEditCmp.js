/*******************************************************************************************************************************
LWC JS Name : coachingUnknownMemberEditCmp.js
Function    : This LWC component for the Unknown Member Creation in Humana Wellness Coaching App.

Modification Log: 
Developer Name           Code Review              Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Mohammed Noor                                   06/04/2021                 Initial version created for USER STORY 2081412.
* Mohammed Noor									  06/30/2021				 US2081412 Highlight Missing Mandatory fields.
* Mohammed Noor									  07/01/2021				 US2081412 Updated the Toast Message display. 
*********************************************************************************************************************************/
import { LightningElement, track, wire } from 'lwc';
import { NavigationMixin } from 'lightning/navigation';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import ACCOUNT_OBJECT from '@salesforce/schema/Account';
import { getObjectInfo } from 'lightning/uiObjectInfoApi';
import { getPicklistValues } from 'lightning/uiObjectInfoApi';
import MAILINGSTATE_FIELD from '@salesforce/schema/Account.PersonMailingStateCode';
import createAccount from '@salesforce/apex/CoachingUnknownMemberEdit_LC_HUM.createUnknownMember';
import { isSpecialCharsExists } from 'c/coachUtilityHum';

import HumSearchPhoneNumericValidation from '@salesforce/label/c.HumSearchPhoneNumericValidation';
import memberSearchSpecialCharFirstNameHum from '@salesforce/label/c.memberSearchSpecialCharFirstNameHum';
import memberSearchSpecialCharLastNameHum from '@salesforce/label/c.memberSearchSpecialCharLastNameHum';
import HumStartnEndDate from '@salesforce/label/c.HumStartnEndDate';
import HumPhoneCharacterLimit from '@salesforce/label/c.HumPhoneCharacterLimit';
import HumPhoneInvalidCharacter from '@salesforce/label/c.HumPhoneInvalidCharacter';
import memberSearchZipEnterHum from '@salesforce/label/c.memberSearchZipEnterHum';
import HumZipcodeCharacterLimit from '@salesforce/label/c.HumZipcodeCharacterLimit';
import HUMValidAccountEmailRegExp from '@salesforce/label/c.HUMValidAccountEmailRegExp';
import EmailFormatError from '@salesforce/label/c.PHARMACY_DEMOGRAPHIC_EMAILFORMAT_ERROR';
import mandatoryFieldError from '@salesforce/label/c.MANDATORYFIELDS_WORKQUEUE_HUM';

export default class CoachingUnknownMemberEditCmp extends NavigationMixin(LightningElement) {

    fName;
    lName;    
    birthDate;    
    phone;
    email;    
    employerName;
    mailingStreet;
    mailingCity;
    mailingStateCode;
    mailingPostalCode;
    mailingCountryCode;

    @track unknMemRecTypeId;    
    @track bIsLoading = false;    
    validationSuccess = true;
    @track formData = {
        FirstName : this.fName,
        LastName : this.lName,        
        Birthdate__c : this.birthDate,        
        PersonHomePhone : this.phone,
        PersonEmail : this.email,        
        Company_Name__c : this.employerName,
        PersonMailingStreet : this.mailingStreet,
        PersonMailingCity : this.mailingCity,
        PersonMailingStateCode : this.mailingStateCode,
        PersonMailingPostalCode : this.mailingPostalCode,
        PersonMailingCountryCode : this.mailingCountryCode
    };

    @track options = [];
    
    /**
     * Get Account object info using wire service
     * @param {*} objectApiName     
     */    
    @wire(getObjectInfo, { objectApiName: ACCOUNT_OBJECT })
    accObjectInfo({data, error}) {        
        if(data) {
            let optionsValues = [];            
            const rtInfos = data.recordTypeInfos;   // map of record type Info
            let rtValues = Object.values(rtInfos);  // getting map values

            for(let i = 0; i < rtValues.length; i++) {
                if(rtValues[i].name === 'Unknown Member') {
                    this.unknMemRecTypeId = rtValues[i].recordTypeId;            
                    break;
                }
            }            
        }
        else if(error) {
            this.showErrorToast(error.body.message);
        }
    }
    
    /**
     * Get the picklist values to be display in State drop down.
     * @param {*} recordTypeId
     * @param {*} fieldApiName     
     */    
    @wire(getPicklistValues, { recordTypeId: "$unknMemRecTypeId", fieldApiName: MAILINGSTATE_FIELD})      
    accountStateInfo({data, error}) {
        if(data) {            
            let picklistVal = Object.values(data.values);
            //create the array to display state picklist values on the UI
            this.options = picklistVal.map(oValue => {
                return {label : oValue.label, value : oValue.value};
            });
        }
        else if(error) {
            this.showErrorToast(error.body.message);        
        }
    }

    /**
     * Get the User Input data
     * @param {*} event
     */
    onFieldChange(event) {
        const fieldKey = event.currentTarget.getAttribute('data-id');
        this.formData[fieldKey] = event.target.value;        
    }

    /**
     * Get the User Input data
     * @param {*} event
     */
    onPhoneFieldChange(event) {
        this.phone = event.target.value;
    }
    
    /**
     * Method to format the phone number
     * @param {*} event
     */
    formatPhoneNumber(event) {
        if(event.keyCode === 8 || event.keyCode === 46) {
            return;
        }
        let phoneNum = event.target.value;
        phoneNum = phoneNum.replace(/[()-]|[ ]/gi, "");
        const onlyNumberRegex = new RegExp(/^\d+$/);
        if(onlyNumberRegex.test(phoneNum)) {
            if (phoneNum.length < 3) {
                phoneNum = '(' + phoneNum;
            } else if(phoneNum.length < 6) {
                phoneNum = '(' + phoneNum.substring(0, 3) + ') ' + phoneNum.substring(3, 6);
            } else if(phoneNum.length > 5) {
                phoneNum = '(' + phoneNum.substring(0, 3) + ') ' + phoneNum.substring(3, 6) + '-' +  phoneNum.substring(6, 10);
            }
        }
        event.target.value = phoneNum;        
    }

    /**
     * Update phone field to the binding property
     * @param {*} event 
     */
    updatePhoneNumber(event) {
        let phVal = event.target.value;
        let phField = this.template.querySelector(".phone-field");
        if (phVal) {
        const fmtPhone = this.getFormatedPhoneNum(phVal, phField);
        event.target.value = fmtPhone;
        this.formData.PersonHomePhone = fmtPhone;
        }
        else {
        this.formData.PersonHomePhone = "";
        }
    }

    /**
     * Method to get the formatted the phone number
     * @param {*} strNumber
     */
    getFormatedPhoneNum(strNumber) {
        if (!strNumber) {
          strNumber = '';
        }
        strNumber = strNumber.replace(/-/g, '');
        const cleaned = ('' + strNumber).replace(/\D/g, '');
        //Check if the input is of correct length
        let match = cleaned.match(/^(\d{3})(\d{3})(\d{4})$/);
        if (match) {
          return '(' + match[1] + ') ' + match[2] + '-' + match[3]
        } else {
            return strNumber;
        }
    }

    /**
     * Method to add the error message to the input fields
     * @param {*} field
     * @param {*} message
     */
    updateFieldValidation(field, message) {        
        field.setCustomValidity(message);
        field.reportValidity();
    }

    /**
     * Method to verify if any special characters exists
     * @param {*} field
     * @param {*} errMessage
     */
    verifySpecialChars(field, errorMessage) {        
        if (isSpecialCharsExists(field.value)) {
            this.updateFieldValidation(field, errorMessage);
            this.validationSuccess = false;
        } 
        if (field.name === 'phone') {
            if (/[^a-z]/i.test(field.value)) {
                this.updateFieldValidation(field, errorMessage);        
                this.validationSuccess = false;
            } 
        }
    }

    /**
     * Method to verify the length of the input value
     * @param {*} field
     * @param {*} length
     * @param {*} errMessage
     */
    verifyFieldLength(field, length, errMessage) {
        if (field.value.length < length) {
            this.updateFieldValidation(field, errMessage);
            this.validationSuccess = false;
            return false;
        }
        return true;
    }

    /**
     * Method to validate the input data for correct format
     * @param {*} regEx
     * @param {*} field
     * @param {*} errMessage
     */
    validateFieldData(regEx, field, errorMessage) {        
        if (!regEx.test(field.value)) {
            this.updateFieldValidation(field, errorMessage);
            this.validationSuccess = false;
        }
    }

    /**
     * Method to perform all the input field validations     
     */
    validateInputFields() {        
        const reZip = new RegExp(/\d{5}/);        
        const rePhone = new RegExp(/^\(?([0-9]{3})\)?[ ]?([0-9]{3})[-]?([0-9]{4})$/);        
        const reEmail = new RegExp(HUMValidAccountEmailRegExp);

        //check if all the mandatory fields have values        
        const formFields = this.template.querySelectorAll(".inputfield");        
        
        for(const field of formFields) {
			this.updateFieldValidation(field, "");  //clear the previous errors.
            if(!field.value) {
                this.showErrorToast(mandatoryFieldError);
                this.validationSuccess = false;
				this.updateFieldValidation(field, " ");
            }
        }
        // If all mandatory fields are fileld then perform data validation
        if(this.validationSuccess) {            
            formFields.forEach( field => {
                if(field.name && field.value) {
                    this.updateFieldValidation(field, "");  //clear the previous errors.
                    switch(field.name) {
                        case 'FirstName' :                            
                            this.verifySpecialChars(field,memberSearchSpecialCharFirstNameHum);
                            break;
                        case 'LastName' :                            
                            this.verifySpecialChars(field,memberSearchSpecialCharLastNameHum);                            
                            break;
                        case 'BirthDate' :                            
                            if(!this.isDateValid(field.value)) {
                                this.updateFieldValidation(field, HumStartnEndDate);
                                this.validationSuccess = false;                                
                            }                            
                            else{   //check for if birthdate > today
                                const inputDate = new Date(field.value);
                                const currDate = new Date();                                
                                if(inputDate > currDate) {
                                    this.updateFieldValidation(field, 'Birth Date value cannot be in future');
                                    this.validationSuccess = false;
                                }
                            }                            
                            break;                        
                        case 'ZipCode' :                            
                            if(this.verifyFieldLength(field, 5, memberSearchZipEnterHum)) {                                
                                this.validateFieldData(reZip, field, HumZipcodeCharacterLimit);
                            }                           
                            break;
                        case 'PhoneNumber' :
                            if(this.verifyFieldLength(field, 14, HumPhoneCharacterLimit)) {
                                this.validateFieldData(rePhone, field, HumPhoneInvalidCharacter);
                            }                            
                            break;
                        case 'email' :                            
                            this.validateFieldData(reEmail, field, EmailFormatError);
                            break;
                    }
                }
            });
        }                
    }

    
    /**
     * Method to handle the save button click
     * @param {*} event     
     */
    handleSaveClick(event) {        
        this.bIsLoading = true;
        this.validationSuccess = true;
        this.validateInputFields();
        if(this.validationSuccess) {
            createAccount({acc : this.formData})
                .then(result => {                                                                
                    let resp = JSON.parse(result);
                    if(resp.status) {
                        if(resp.status === 'success') {
                            let accId = resp.accountId;                               
                            //dispatch even to open the account page in a new Primary tab and close the current tab                         
                            const saveEvt =  new CustomEvent('saveEvent', { 
                                detail : { accId }                    
                            });
                            this.bIsLoading = false;
                            //fire the custom event
                            this.dispatchEvent(saveEvt);         
                        } 
                        else if(resp.status === 'error') {
                            this.bIsLoading = false;
                            if(resp.errorMsg) {
                                this.showErrorToast(resp.errorMsg);
                            }                        
                        }
                    }
                })
                .catch(error => {
                    this.bIsLoading = false;
                    let errMsg = error.body.message;                
                    this.showErrorToast(errMsg);
                });            
        }
        else {
            this.bIsLoading = false;
        }
    }

    /**
     * Method to handle the cancel button click
     * @param {*} event     
     */
    handleCancelClick(event) {
        const close = true;
        const cancelEvent =  new CustomEvent('closeTab', { 
            detail: {close},
        });
        //fire the custom event to close the current tab.
        this.dispatchEvent(cancelEvent);
    }
    
    /**
     * Method to display error message
     * @param {*} sMessage
     */
    showErrorToast(sMessage) {
        const evt = new ShowToastEvent({
            title: 'Error',
            message: sMessage,
            variant: 'error'         
        });
        this.dispatchEvent(evt);
    }

    /**
     * Method to auto format the date as the user types in the value
     * @param {*} event     
     */
    formatDateOnKeyUp(event) {
        if(event.keyCode === 8 || event.keyCode === 46){ //exclude backspace and delete key
          return;
        }
        let dtValue = event.target.value;
        dtValue = this.getFormattedDate(dtValue);
        event.target.value = dtValue;
        if(this.isDateValid(dtValue)){
          this.formData.Birthdate__c = dtValue;
        }        
    }

    /**
     * Method to format the input date value
     * @param {*} dtValue
     * @param {*} seperator
     */
    getFormattedDate(dtValue, seperator = "/") {
        let newDate = dtValue;
        const onlyNumber = new RegExp(/^\d+$/);
        dtValue = dtValue.replace(/[/]/gi, "");
        if (onlyNumber.test(dtValue)) {
          const dtValLength = dtValue.length;
          if (dtValLength === 2) {
            newDate = dtValue + seperator;
          }
          else if (dtValLength === 4) {
            newDate = dtValue.substring(0, 2) + seperator + dtValue.substring(2, 4) + seperator;
          }
          else if (dtValLength > 4 && dtValLength !== 7) { // Not to consider formatting when remove and add month or date
            newDate = dtValue.substring(0, 2) + seperator + dtValue.substring(2, 4) + seperator + dtValue.substring(4, 8);
          }
        }
        return newDate;
    }

    /**
     * Method to validate the input date format
     * @param {*} event     
     */
    isDateValid(dtValue) {
        return dtValue.match(/^\d{2}\/\d{2}\/\d{4}/) &&  new Date(dtValue).getTime() ? true: false;
    }


}