/*******************************************************************************************************************************
LWC JS Name : coachingPasswordFormHum.js
Function    : This JS serves as controller to passwordFormHum.html. 

Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Prudhvi Pamarthi                                        05/22/2021                 Initial version
*********************************************************************************************************************************/
import { LightningElement, api, track } from 'lwc';
import { editForm } from './coachingPasswordFormModel';
import { ShowToastEvent } from "lightning/platformShowToastEvent";
import { getLabels } from "c/coachUtilityHum";
import saveFormdata from '@salesforce/apex/UserAssociatedInfoCoach_D_HUM.performPopupPasswordDetailsUpdate';
import getPasswordValues from '@salesforce/apex/UserAssociatedInfoCoach_D_HUM.getPopupOperationValue';
import customcss from '@salesforce/resourceUrl/LightningCRMAssets_SR_HUM';
import { loadStyle } from 'lightning/platformResourceLoader';

export default class coachingPasswordFormHum extends LightningElement {
    @api isEdit;
    @api isAcknowledgeForm;
    @api formData;
    @api recordId;
    @track edtForm;
    @track labels = getLabels();
    @track showValidationMsg = false;
    @track isFormValid = true;
    @track isModified = false;
    @track presavedVals = {};
    @api fieldsClass = "create-form password-form-container";
    currentDate;
    termDate;
    @track passwordData = {};

    connectedCallback() {
        this.edtForm = editForm;
        if (this.isEdit) {
            this.edtForm.forEach(field => {
                field.errorClass = 'new-fields slds-p-top_x-small';  // removes error highlights on load of edit password form
            });
        } else {
            this.handleHighlightsonLoad();
        }
        getPasswordValues({ sAccountId: this.recordId }).then(response => { // apex call to get and store values saved by user
            if (response) {
                this.presavedVals = response;
            }
        }).catch(err => {
            this.showToast(this.labels.modalErrorToastHum, "", "error");
        });
    }

    /**
     * Loads common styles
     */
    renderedCallback() {
        Promise.all([
            loadStyle(this, customcss + '/CRM_Assets/styles/commonStyles.css')
        ]).catch(error => {
            console.log('Error Occured', error);
        });
    }

    /**Shows success toast message
     * on successful submission of inputs
     */
    handleSuccess() {
        this.showToast(this.labels.passwordSuccessHum, "", "success");
        this.dispatchEvent(new CustomEvent('submission'));  //Fires event to open accordian and display updated fields
    }

    /**
     * Updates object with user inputs and dates
     * and makes apex call to save as response
     */
    submitValues() {
        this.passwordData = {
            ...this.passwordData,
            effectiveDate: this.currentDate,
            terminatedDate: this.termDate
        }
        saveFormdata({ popupDetailsWrapper: JSON.stringify(this.passwordData), recId: this.recordId }).then(res => {
            if (res) {
                this.handleSuccess();
            }
        }).catch(err => {
            this.showToast(this.labels.modalErrorToastHum, "", "error");
        });
    }

    /**
     * Adds user inputs to an object and
     * checks for validations based on form type before saving
     */
    @api
    saveFields(event) {
        this.getDates();
        this.template.querySelectorAll('lightning-input').forEach(element => {
            this.passwordData[element.name] = element.value;
        });
        if (this.isEdit) {
            this.handleEditSave(event);
        }
        else {
            this.handleValidation();    //Check validation only for Create password form
            if (this.isFormValid) {
                this.submitValues();
            }
        }
    }

    /**
     * Toggles error highlights based on form type
     * on load of form
     * @param {} 
     */
    @api
    handleHighlightsonLoad(event) {
        const me = this;
        me.edtForm.forEach(field => {
            field.errorClass = (!me.isEdit) ? 'new-fields error-highlights slds-p-top_x-small' : 'new-fields slds-p-top_x-small';
            field.showValidation = false;
        })
    }

    /**
     * Handles selective saving functionality on
     * edit password form
     * @param {*} 
     */
    handleEditSave(event) {
        const me = this;
        const aInpFields = this.template.querySelectorAll('lightning-input');
        aInpFields.forEach(inp => {
            if (inp.value.trim().length === 0) {
                this.passwordData[inp.name] = me.presavedVals[inp.name];    //Updates empty field with pre-saved user data
            }
        });
        this.submitValues();    //Method call to submit updated values
    }

    /**
     * Handles user input validations for
     * create password form
     */
    handleValidation() {
        let valArr = [];
        this.template.querySelectorAll('lightning-input').forEach(element => {
            this.edtForm.forEach(field => {
                if (element.name === field.fieldName) {
                    console.log("after trimming", element.value.trim().length);
                    field.errorClass = (element.value.trim().length === 0) ? "new-fields error-highlights slds-p-top_x-small" : "new-fields slds-p-top_x-small";     //Shows error highlight borders
                    field.showValidation = (element.value.trim().length === 0) ? true : false;   //Shows field-based validation messages
                    valArr.push(element.value);
                }
            });
        });
        this.isFormValid = !valArr.some(item => item.trim().length === 0);   //Sets validity boolean to false if any of form inputs are empty
    }

    /**
     * Sets the effective date and termination date
     * for user in date format
     */
    getDates() {
        let d = new Date();
        let month = '' + (d.getMonth() + 1);
        let day = '' + d.getDate();
        let year = d.getFullYear();

        if (month.length < 2)
            month = '0' + month;
        if (day.length < 2)
            day = '0' + day;
        this.currentDate = [year, month, day].join('-');
        this.termDate = [year + 2, month, day].join('-');
    }

    /**
     * Verifies if user has made any changes to input fields
     * before closing the modal without saving
     */
    @api
    hasData() {
        let valArr = [];
        this.template.querySelectorAll('lightning-input').forEach(element => {
            valArr.push(element.value);
        });
        this.dispatchEvent(new CustomEvent('modify', { detail: valArr.some(item => item.trim().length !== 0) })); //Fire event to prompt user for unsaved changes if form is modified
    }

    /**
     * Resets all inputs and validations
     */
    @api
    resetFields() {
        this.template.querySelectorAll(
            'lightning-input'
        ).forEach(inputFields => {
            this.passwordData[inputFields.name] = "";
            inputFields.value = "";
        })
        this.edtForm.forEach(field => {
            field.showValidation = false;
            field.errorClass = "new-fields slds-p-top_x-small";
        });
    }

    /**
     * Generiic method to handle toast messages
     * @param {*} strTitle 
     * @param {*} strMessage 
     * @param {*} strStyle 
     */
    showToast(strTitle, strMessage, strStyle) {
        this.dispatchEvent(
            new ShowToastEvent({
                title: strTitle,
                message: strMessage,
                variant: strStyle,
            })
        );
    }

    /**
     * Generic method to fire custom events
     * @param {} eventName 
     * @param {*} detail 
     */
    fireEvent(eventName, detail) {
        const tabNavigate = new CustomEvent(eventName, { detail });
        this.dispatchEvent(tabNavigate);
    }
}