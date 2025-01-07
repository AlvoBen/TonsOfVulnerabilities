/*******************************************************************************************************************************
LWC JS Name : MemberSearchCaseLookupHum.js
Function    : This LWC component used to search members in case edit page
Modification Log:
Developer Name           Code Review              Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Santhi Mandava                                  11/05/2023                   Member Search popup initial version
***********************************************************************************************************************************************/

import { track, api, wire } from "lwc";
import { NavigationMixin } from 'lightning/navigation';
import getCustomInputFieldPermission from '@salesforce/apex/SearchResultsBasedOnPermission_LH_HUM.getCustomFieldsToDisplay';
import saveForm from '@salesforce/apex/MemberSearch_LC_HUM.searchRecords';
import getStateValues from '@salesforce/apex/SearchUtilty_H_HUM.getStateValues';
import { CurrentPageReference } from 'lightning/navigation';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import {
    isSpecialCharsExists, getLocaleDate, hcConstants, getUserGroup, getFormattedDate,
     setSessionItem, isDateValid, compareDate,  getBaseUrl, hasSystemToolbar
} from "c/crmUtilityHum";
import { getLabels } from 'c/customLabelsHum';
import { getRecord } from 'lightning/uiRecordApi';
import USER_ID from '@salesforce/user/Id';
import PROFILE_NAME_FIELD from '@salesforce/schema/User.Profile.Name';
import NETWORK_ID_FIELD from '@salesforce/schema/User.Network_User_Id__c';
import CURRENT_QUEUE from '@salesforce/schema/User.Current_Queue__c';
import { getWithoutHPColumnLayout } from './humanaPharmacyIDModals';
import pharmacyServiceErrorMessage from '@salesforce/label/c.PHARMACY_DEMOGRAPHIC_FILTER_ERROR';
import crmserviceHelper from 'c/crmserviceHelper';
import pubSubHum from 'c/pubSubHum';
import Pharmacy_Mem_Not_found from "@salesforce/label/c.Pharmacy_Mem_Not_found";
import CRMRetail_Error_Label from "@salesforce/label/c.CRMRetail_Error_Label";
const arrFields = ['Account.Account_Security_Answer__c', 'Account.Account_Security_Question__c', 'Account.Account_Security_EndDate__c', 'Account.Account_Security_Access__c'];
export default class MemberSearchCaseLookupHum extends NavigationMixin(crmserviceHelper) {

    @api noRecordlabel;
    @api inputType = 'Input';
    @api screenType = 'MemberSearch';

    @api get accountSearchHistory(){
        return this.formData;
    }

    @api get suffixpermission() {
        return this.inputFieldSuffix;
    }

    @api get groupNumberPermission() {
        return this.inputFieldGroupNumber;
    }

    @api get pidPermission() {
        return this.inputFieldPid;
    }
    @api isNavigatedFrmGrp;

    
    @track stateOptions = [];
    @track showErrorMsg = false;
    @track isFormValid = true;
    @track labels = getLabels();
    @track oUserGroup = getUserGroup();
    @track newpolicy = [];
    @track hpColumnLayout = [];
    @track stateValue;
    @track showValidationMsg = false;
    @track showMedicareValidationMsg = false;
    @track profileName;
    @track netWorkId;
    @track workQueue;

    @track accountId = '';

    preSelectedPolicyId = ""; // this property used to preserve policy preselection and pass to account detail when click name
    
    @track pharmacyMemberNotFoundMsg = Pharmacy_Mem_Not_found;
    @track pharmacyservicemsg = pharmacyServiceErrorMessage;
    @track errorHeader = CRMRetail_Error_Label;
    @wire(CurrentPageReference) pageRef;
    @wire(getRecord, { recordId: '$accountId', fields: arrFields })
    wiredAccount({
        error,
        data
    }) {
        if (data) {
            const termDate = data.fields.Account_Security_EndDate__c.value ? getLocaleDate(data.fields.Account_Security_EndDate__c.value) : null;
            const todayDate = getLocaleDate(new Date());
            if (compareDate(termDate, todayDate) === 1) {
                this.selMemberAccId = this.accountId;
            }
        }
        else {
            console.error('Error', error);
        }
    }

    @wire(getRecord, {
        recordId: USER_ID,
        fields: [PROFILE_NAME_FIELD, NETWORK_ID_FIELD, CURRENT_QUEUE]
    }) wireuser({
        error,
        data
    }) {
        if (error) {
            this.error = error;
        } else if (data) {
            this.profilename = data.fields.Profile.value.fields.Name.value;
            this.netWorkId = data.fields.Network_User_Id__c.value;
            this.workQueue = data.fields.Current_Queue__c.value;
        }
    }

    sSSN = '';
    sMemberid = '';
    sSuffix = '';
    sBirthdate = '';
    sPhone = '';
    sGroupNumber = '';
    sPID = '';
    sState = '';
    sPostalCode = '';
    rxNumber = '';
    sFirstName = '';
    sLastName = '';
    hasData = false;
    showNoDataView = false;
    resultsTrue = true;
    accountList = [];
    originalAccountList = [];
    accountCount = '';
    inputFieldSuffix;
    inputFieldGroupNumber;
    inputFieldPid;
    policyList;
    NoPharmacyAccount = 'Yes';

    @track formData = {
        sMemberid: this.sMemberid,
        sSuffix: this.sSuffix,
        sBirthdate: this.sBirthdate,
        sPhone: this.sPhone,
        sGroupNumber: this.sGroupNumber,
        sPID: this.sPID,
        sState: this.sState,
        sPostalCode: this.sPostalCode,
        rxNumber: this.rxNumber,
        sFirstName: this.sFirstName,
        sLastName: this.sLastName,
        sUnknownMemCheck: false
    };

    @track formState = {
        isMemberid: false,
        isSuffix: false,
        isBirthdate: false,
        isPhone: false,
        isGroupNumber: false,
        isPID: false,
        isState: false,
        isPostalCode: false,
        isFirstName: false,
        isLastName: false
    };

    @api encodeddata;

    @api
    encodedValues(encodedData) {
        this.sFirstName = encodedData.sFirstName;
        this.sLastName = encodedData.sLastName;
        this.sMemberid = encodedData.sMemberid;
        this.sBirthdate = encodedData.sBirthdate;
        this.sSuffix = encodedData.sSuffix;
        this.sState = encodedData.sState;
        this.sPostalCode = encodedData.sPostalCode;
        let sField = encodedData.sField;
        let hasValue = false;
        if(encodedData.sFirstName || encodedData.sLastName || encodedData.sMemberid || encodedData.sBirthdate ||
            encodedData.sPostalCode || encodedData.sState){
                hasValue =true;
            }
        let me = this;
        this.formData['sField'] = sField;
        if(hasValue){
        this.template.querySelectorAll(".inputfield").forEach(function (field) {
            if (field.name == "firstName") {
                field.value = me.sFirstName;
            } else if (field.name == "lastName") {
                field.value = me.sLastName;
            } else if (field.name == "state") {
                field.value = me.sState;
            } else if (field.name == "ID") {
                field.value = me.sMemberid;
            } else if (field.name == "birthdate") {
                field.value = me.sBirthdate;
            } else if (field.name == "zipCode") {
                field.value = me.sPostalCode;
            } 
        });
        if(this.sState) {
            if(me.template.querySelector("c-generic-drop-down-hum") != undefined &&
                me.template.querySelector("c-generic-drop-down-hum") != null
            ) {
                let sLabel = this.getStateLabel(this.sState);
               if(sLabel) me.template.querySelector("c-generic-drop-down-hum").preSelectValue(sLabel);
            }
        }
        this.formData['sFirstName'] = this.sFirstName;
        this.formData['sLastName'] = this.sLastName;
        this.formData['sMemberid'] = this.sMemberid;
        this.formData['sBirthdate'] = this.sBirthdate;
        this.formData['sPostalCode'] = this.sPostalCode;
        this.formData['sState'] = this.sState;

        setTimeout(() => {
            this.handleValidation();
        }, 1);
        }
    }

    getStateLabel(sVal){
        let stateLabel = '';
        for(let i =0 ;i<= this.stateOptions.length -1 ;i++){
            if(this.stateOptions[i].value == sVal){
                stateLabel = this.stateOptions[i].label;
                break;
            }
        }
        return stateLabel;
    }
    connectedCallback() {
        getCustomInputFieldPermission({ sInput: this.inputType, sScreenType: this.screenType }).then(result => {
            const currentDate = new Date();
            const strResult = JSON.stringify(result);
            this.inputFieldSuffix = strResult.includes("Suffix");
            this.inputFieldGroupNumber = strResult.includes("groupNumber");
            this.inputFieldPid = strResult.includes("PID");
            this.today = currentDate.getFullYear() + '-' + (currentDate.getMonth() + 1) + '-' + (currentDate.getDate() > 9 ? currentDate.getDate() : '0' + currentDate.getDate());

            if (this.encodeddata && this.encodeddata != null) {
                this.encodedValues(this.encodeddata);
            }
        }).catch(error => {
            console.log("Error Occured", error);
        });
        getStateValues().then(data => {
            if (data) {
                for (let key in data) {
                    const opt = { label: key, value: data[key] };
                    this.stateOptions = [...this.stateOptions, opt];
                }
            }
        }).catch(error => {
            console.log("Error Occured", error);
        });
    }

    /**
     * Standard table container css
     */
    get containerCss() {
        return hasSystemToolbar ? 'searchpage-results-system slds-var-m-left_small' : 'searchpage-results slds-var-m-left_small'
    }

    /**
     * Handle state chagne
     * @param {*} event
     */
    stateSelectionHandler(event) {
        let stateVal = event.detail.value;
        this.formData.sState = stateVal;
        if (stateVal.length) {
            this.stateValue = true;
            this.highlightFields();
        } else {
            this.formData.sState = "";
            this.removehighlight();
        }
    }

    /**
     * update data on field change
     * @param {*} evnt
     */
    onFieldChange(evnt) {
        const fieldKey = evnt.currentTarget.getAttribute('data-id');
        this.formData[fieldKey] = evnt.target.value;
        if (fieldKey == 'sMemberid') {
            this.sSSN = evnt.target.value;
        }
    }
    

    /**
     * Validate date format and trigger remove highlight method
     * @param {*} event
     */
    onDateFieldBlur(event) {
        const me = this;
        const field = event.target;
        if (field.value !== "") {
            me.updateFieldValidation(field, isDateValid(field.value) ? "" : me.labels.HumStartnEndDate);
        }
        this.highlightByFieldValue(event);
    }

    isStringEmpty(input) {
        if (input != null && input != undefined && input != '' && input?.length >= 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
    * Format date as user enters keys
    * @param {*} event
    */
    formatDateOnKeyUp(event) {
        if (event.keyCode === 8 || event.keyCode === 46) { //exclude backspace and delete key
            return;
        }
        let dtValue = event.target.value;
        dtValue = getFormattedDate(dtValue);
        event.target.value = dtValue;
        if (isDateValid(dtValue)) {
            this.formData.sBirthdate = dtValue;
        }
        this.enterSeach(event)
    }

    hasAccessToHP(item) {
        let pharmcyLink;
        this.hpColumnLayout = getWithoutHPColumnLayout;
        return pharmcyLink;
    }

    /**
     * toggle validation highlights
     */
    validateHighlightedFields() {
        if ((this.sBirthdate && !this.sPostalCode && !this.sState && !this.sFirstName && !this.sLastName) ||
            (!this.sBirthdate && this.sPostalCode && !this.sState && !this.sFirstName && !this.sLastName) ||
            (!this.sBirthdate && !this.sPostalCode && this.sState && !this.sFirstName && !this.sLastName)) {
            this.highlightFields();
            return false;
        } else {
            this.removehighlight();
            return true;
        }
    }

    searchMembers(){
        const me = this;
        const sLockMessage = this.labels.LimitedAccessMessage;
        this.hasData = false;
        this.accountList = null;
        setTimeout(() => {
            if (me.isFormValid) {
                me.resultsTrue = true;
                    // this.formData = {
                    //     ...this.formData,
                    //     sPhone: this.getFormatedPhoneNum(this.phone)
                    // };
                    saveForm({ formMemberSearchWrapper: this.formData }).then(result => {
                        let res = result.lstAccounts;
                        this.hasData = res.length > 0;
                        this.accountCount = res.length;

                        this.showNoDataView = !this.hasData;
                        if (this.hasData) {
                            this.accountList = res.map(item => ({
                                Id: item.Id,
                                RecordType: item.RecordType.Name,
                                FirstName: (item.MiddleName) ? item.FirstName + ', ' + item.MiddleName : item.FirstName,
                                LastName: item.LastName,
                                Birthdate__c: item.Birthdate__c,
                                PersonMailingState: item.PersonMailingState,
                                PersonMailingPostalCode: item.PersonMailingPostalCode,
                                PersonHomePhone: item.PersonHomePhone,
                                ETL_Record_Deleted__c: item.ETL_Record_Deleted__c,
                                disabled: item.ETL_Record_Deleted__c,
                                enterpriseID: this.hasAccessToHP(item),
                                hpLink: ''
                            }));
                        }
                        else {
                            this.noRecordlabel = this.labels.memberSearchNoResultsHum;
                        }
                        if (this.accountList && this.accountList.length > 0) {
                            this.originalAccountList = this.accountList;
                        }
                        this.setUnknownFlag(false);
                    }).catch(err => {
                        this.showToast(this.labels.crmSearchError, this.labels.crmToastError, "error");
                        this.setUnknownFlag(false);
                        console.log("Error Occured", err);
                    });
            }
            else {
                this.clearSearchResults();
            }
        }, 100);
        pubSubHum.fireEvent(this.pageRef, "CLEAR_MEMBER_POLICY_INT", {});
    }

    /**
     * Validating Form
     * @param {*} event
     */
    handleValidation(event) {
        const me = this;
        let hasFieldValue = false;
        this.policyList = null;
        
        
        this.showNoDataView = false;
        let dateField = this.template.querySelector(".birthdate-input");
        //Added for US#4456352 - Member information search using Member ID not returning expected results  
        //Check the DOB value and reset the form data and local assignments if its empty before validating
        if(dateField.value === ''){
            this.sBirthdate = '';
            this.formData.sBirthdate = '';
        }
       
        const reWordSelect = new RegExp(/\w+/);
        const reZip = new RegExp(/\d{5}/);
        me.isFormValid = true;
        let isPrimaryCriteriaPresent = false;
        me.showValidationMsg = false;
        me.showMedicareValidationMsg = false;
        me.clearSearchResults();
        const formFields = me.template.querySelectorAll(".inputfield");
        formFields.forEach(function (field) {
            field.required = false;
            me.updateFieldValidation(field, "");
            if (field.value) {
                hasFieldValue = true;
                me.showErrorMsg = false;
            }
            switch (field.name) {
                case 'lastName':
                    me.formState.lastNameField = field;
                    me.formState.isLastName = field.value ? true : false;
                    if(me.formState.isLastName){
                        me.verifySpecialChars(field, me.labels.memberSearchSpecialCharLastNameHum);
                        if (field.value.length < 2) me.verifyFieldLength(field, 2, me.labels.memberSearchLastNameHum);
                    }
                    break;
                case 'firstName':
                    me.formState.firstNameField = field;
                    me.formState.isFirstName = field.value ? true : false;
                    if(me.formState.isFirstName) me.verifySpecialChars(field, me.labels.memberSearchSpecialCharFirstNameHum);
                    break;
                case 'zipCode':
                    me.formState.zipCodeField = field;
                    me.formState.isPostalCode = field.value ? true : false;
                    if(me.formState.isPostalCode){
                        me.validateFieldData(reZip, field, me.labels.HumZipcodeCharacterLimit);
                        if (field.value.length < 5) {
                            me.verifyFieldLength(field, 5, me.labels.memberSearchZipEnterHum);
                        }
                    }
                    break;
                case 'ID':
                    me.formState.idField = field;
                    me.formState.isMemberid = field.value ? true : false;
                    if(me.formState.isMemberid){
                        me.verifyFieldLength(field, 3, me.labels.memberSearchIDCriteriaHum);
                        me.verifySpecialChars(field, me.labels.memberSearchIDAlphaCriteriaHum);
                        me.validateFieldData(reWordSelect, field, me.labels.memberSearchIDAlphaCriteriaHum);
                    }
                    break;
                case 'birthdate':
                    me.formState.birthDateField = field;
                    me.formState.isBirthdate = field && field.value ? true : false ;
                    if(me.formState.isBirthdate){
                        if (!isDateValid(field.value)) {
                            me.updateFieldValidation(field, me.labels.HumStartnEndDate);
                            me.isFormValid = false;
                        }
                        else if (compareDate(getLocaleDate(field.value), getLocaleDate(new Date())) === 1) {
                            me.updateFieldValidation(field, hcConstants.BIRTH_DATE_NOT_FUTURE);
                            me.isFormValid = false;
                        } 
                    }
                    break;
                default:
            }
        });
        if (!dateField.reportValidity()) {
            hasFieldValue = true;
        }
        if (hasFieldValue || me.stateValue) {
            me.showErrorMsg = false;
        }
        else {
            me.showErrorMsg = true;
            me.isFormValid = false;
        }
        if(me.formState.isMemberid || (me.formState.isMemberid && me.formState.isSuffix)
           || (me.formState.isFirstName && me.formState.isLastName))
        {
            isPrimaryCriteriaPresent = true;
        }
        if(!isPrimaryCriteriaPresent){
            if(!this.validateBirthDateCombination()) return;
            if(!this.validateZipCodeCombination()) return;
            if(!this.validateStateCombination()) return;
            if(!this.validateNameCombination()) return;
        }
        if(this.isFormValid)  
        me.showValidationMsg  = false;
        me.showMedicareValidationMsg = false;
        this.searchMembers();
    }

    validateBirthDateCombination(){
        let isValid = true;
        if(this.formState.isBirthdate){
            if(!this.formState.isFirstName && !this.formState.isLastName && !this.formState.isMemberid) {
                this.isFormValid = isValid = false;
                this.updateFieldValidation(this.formState.birthDateField, this.labels.HUMMedicareErrorBirthDateSearch);
                return isValid;
            }else if(!this.formState.isFirstName || !this.formState.isLastName){
                this.isFormValid = isValid = false;
                this.updateFieldValidation(this.formState.birthDateField, this.labels.HumErrorBirthDateSearchCombination);
                return isValid;
            }
        }
        return isValid;
    }

    validateZipCodeCombination(){
        let isValid = true;
        if(this.formState.isPostalCode){
            if(!this.formState.isFirstName && !this.formState.isLastName && !this.formState.isMemberid) {
                this.isFormValid = isValid = false;
                this.updateFieldValidation(this.formState.zipCodeField, this.labels.HUMMedicareErrorZipCodeSearch);
                return isValid;
            }else if(!this.formState.isFirstName || !this.formState.isLastName){
                this.isFormValid = isValid = false;
                this.updateFieldValidation(this.formState.zipCodeField, this.labels.HUMMedicareErrorZipCodeSearch);
                return isValid;
            }
        }
        return isValid;
    }

    validateStateCombination(){
        let isValid = true;
        if(this.stateValue){
            if(!this.formState.isFirstName && !this.formState.isLastName && !this.formState.isMemberid) {
                this.showMedicareValidationMsg = true;
                this.isFormValid = isValid = false;
                return isValid;
            }else if(!this.formState.isFirstName || !this.formState.isLastName){
                this.showMedicareValidationMsg = true;
                this.isFormValid = isValid = false;
                return isValid;
            }
        }
        return isValid;
    }

    validateNameCombination(){
        let isValid = true;
        if(this.formState.isFirstName && !this.formState.isLastName && !this.formState.isMemberid 
                    && !this.stateValue && !this.formState.isBirthdate && !this.formState.isPostalCode){
            this.isFormValid = isValid = false;
            this.updateFieldValidation(this.formState.firstNameField, this.labels.HumErrorMedicareFirstNameSearch);
            return isValid;
        }else if(this.formState.isLastName && !this.formState.isFirstName && !this.formState.isMemberid 
            && !this.stateValue && !this.formState.isBirthdate && !this.formState.isPostalCode){
            this.isFormValid = isValid = false;
            this.updateFieldValidation(this.formState.lastNameField, this.labels.HumErrorMedicareLastNameSearch);
            return isValid;
        }
        return isValid;
    }

    setUnknownFlag(flag) {
        this.formData.sUnknownMemCheck = flag;
    }

    /**
     * Validate field length
     * @param {*} field
     * @param {*} length
     * @param {*} errMessage
     */
    verifyFieldLength(field, length, errMessage) {
        if (field.value.length < length) {
            this.updateFieldValidation(field, errMessage);
            this.isFormValid = false;
        }
    }

    /**
     * Validate field data using provided regular expression
     * @param {*} regEx
     * @param {*} field
     * @param {*} errorMessage
     */
    validateFieldData(regEx, field, errorMessage) {
        const me = this;
        if (!regEx.test(field.value)) {
            me.updateFieldValidation(field, errorMessage);
            me.isFormValid = false;
        }
    }
    /**
     * Verify for special characters
     * @param {*} errorMsg
     */
    verifySpecialChars(field, errorMsg) {
        if (isSpecialCharsExists(field.value)) {
            this.updateFieldValidation(field, errorMsg);
            this.isFormValid = false;
        } if (field.name === 'phone') {
            if (/[^a-z]/i.test(field.value)) {
                this.updateFieldValidation(field, errorMsg);
                this.isFormValid = false;
            }
        }
    }

    handleInteraction(event) {
        const me = this;      
        me.accountId = event.detail.Id;
        let res = me.accountList.find(t => t.Id === me.accountId);
        
        const addCartEvent = new CustomEvent('accountselection', {
            detail: {
                accId: res?.Id,
                accName : res?.FirstName + ' ' +res?.LastName,
                formData : this.formData
            }
        });
        this.dispatchEvent(addCartEvent);
    }


    /**
     * Update Field validation with message
     * @param {*} field
     * @param {*} message
     */
    updateFieldValidation(field, message) {
        field.setCustomValidity(message);
        field.reportValidity();
    }

    handleUnknownSearch(event) {
        this.formData.sUnknownMemCheck = true;
        this.handleValidation(event);
    }

    /**
     * Reset form on click of reset button
     * @param {*} event
     */
    @api
    handleReset(event) {
        const me = this;
        this.formData.sMemberid = '';
        this.formData.sBirthdate = '';
        this.formData.sState = '';
        this.formData.sPostalCode = '';
        this.formData.rxNumber = '';
        this.formData.sFirstName = '';
        this.formData.sLastName = '';

        this.sMemberid = '';
        this.sBirthdate = '';
        this.sState = '';
        this.sPostalCode = '';
        this.sFirstName = '';
        this.sLastName = '';
 
        this.formData.sUnknownMemCheck = false;
        this.template.querySelector("c-generic-drop-down-hum").reset();

        this.template.querySelectorAll("lightning-input").forEach((field) => {
            field.required = false;
            field.value = "";
            me.updateFieldValidation(field, "");
        });

        me.showErrorMsg = false;
        me.showValidationMsg = false;
        me.showMedicareValidationMsg = false;
        me.stateValue = false;

        this.clearSearchResults();
        pubSubHum.fireEvent(this.pageRef, "CLEAR_MEMBER_POLICY_INT", {});
    }

    clearSearchResults() {
        this.accountList = null;
        this.hasData = false;
        this.showNoDataView = false;
        this.policyList = null;
        this.noPolicyData = false;
        
        this.preSelectedPolicyId = "";
    }

    clearStateHandler(event) {
        this.formData.sState = "";
        this.stateValue = false;
        this.showValidationMsg = false;
        this.showMedicareValidationMsg = false;
    }
    /**
     * Hightlight validation failures
     */
    highlightFields() {
        this.showErrorMsg = false;
        let inp = this.template.querySelectorAll(".NameHighlight");
        inp.forEach(function (element) {
            element.required = true;
            element.reportValidity();
        }, this);
    }

    /**
     * Hightlight validation failures
     */
    highlightID() {
        this.showErrorMsg = false;
        let inp = this.template.querySelectorAll(".IDHighlight");
        inp.forEach(function (element) {
            element.required = true;
            element.reportValidity();
        }, this);
    }

    /**
     * Hide no input error
     */
    removeError() {
        this.showErrorMsg = false;
    }

    /**
     * Remove validation failures
     */
    removehighlight() {
        if (document.activeElement.tagName != 'INPUT') {
            let inp = this.template.querySelectorAll(".NameHighlight");
            //Updated validation rule to remove highlight from name fields based on Statevalue selected
            const { sFirstName, sLastName, sState } = this.formData;
            inp.forEach(function (element) {
                element.required = (sState.length) ?
                    (sFirstName.length && sLastName.length) ? false : true : false;
                element.reportValidity();
            }, this);
        }
    }

    highlightByFieldValue(event) {
        this.toggleHighLight(event.target.value);
    }

    highlightIDByFiield(event) {
        this.toggleIDHighLight(event.target.value);
    }

    highlightByZipCode(event) {
        this.formData.sPostalCode = event.target.value;
        if (event.target.value !== "") {
            this.highlightFields();
        } else {
            this.removehighlight();
        }
    }

    toggleHighLight(value) {
        if (value) {
            this.highlightFields();
        } else {
            this.removehighlight();
        }
    }

    toggleIDHighLight(value) {
        if (document.activeElement.tagName != 'INPUT') {
            let inp = this.template.querySelectorAll(".IDHighlight");
            inp.forEach(function (element) {
                element.required = false;
                element.reportValidity();
            }, this);
        }
    }

    
    enterSeach(event) {
        if (event.keyCode === 13) {
            this.handleValidation();
        }
    }

    showToast(strTitle, strMessage, strStyle) {
        this.dispatchEvent(new ShowToastEvent({
            title: strTitle,
            message: strMessage,
            variant: strStyle
        }));
    }

    navigateToSearchEnroll() {
        const detail = { tabVal: 'searchEnroll', memberId: this.sSSN };
        const tabNavigate = new CustomEvent('tabnavigation', { detail });
        this.dispatchEvent(tabNavigate);
    }

    /**
     * Event listenr to listen hyper link click on the standard table
     * @param {*} evnt
     */
    onHyperLickClick(evnt) {
        const me = this;
        const accountId = evnt.detail.accountId;
        const policyTable = me.template.querySelector(`[data-id='member-policy']`);
        let selPolicyId = policyTable && policyTable.selectedRecordId();
        if (!selPolicyId) {
            selPolicyId = this.preSelectedPolicyId;
        }
        if (selPolicyId) {
            setSessionItem(hcConstants.MEMBER_POLICY_ID, selPolicyId + '##' + accountId);
            const url = `${getBaseUrl()}/lightning/r/MemberPlan/${selPolicyId}/view?ws=%2Flightning%2Fr%2FAccount%2F${accountId}%2Fview?%2Fc__NoPharmacyMember%2F${this.NoPharmacyAccount}`;

            this[NavigationMixin.Navigate]({
                type: 'standard__webPage',
                attributes: { //set account id here, and get the id on highlights panel to nav
                    url
                }
            });
        }
        else {
            this.navigateToViewAccountDetail(accountId, 'Account', 'view');
        }
    }  
}