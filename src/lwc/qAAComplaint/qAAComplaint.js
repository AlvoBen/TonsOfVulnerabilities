/*******************************************************************************************************************************
LWC JS Name : qAAComplaint.js
Function    : controller file to qAAComplaint.html. This is used to display QAA template

Modification Log: 
Developer Name           Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
* Santhi Mandava                                          08/30/2022                   Original version
* visweswararao j                                         11/01/2022                   User Story 3698842: T1PRJ0170850 - MF 19080 - Lightning - Templates/QAA & Limitations of Template
* Aishwarya Pawar                                         05/02/2023                   REQ - 4497923
********************************************************************************************************************************/
import { LightningElement, track, api, wire } from 'lwc';
import getQAATemplateDetails from '@salesforce/apex/QAAComplaint_LC_HUM.getQAATemplateDetails';
import saveQAATemplate from '@salesforce/apex/QAAComplaint_LC_HUM.saveQAATemplate';
import { qaaLayOutDetails, providerResultLayout, providerFields, customizedQuestions, qaaConstants, qaaInputFields, customizedLabels } from './layoutConfig';
import startRequest from '@salesforce/apexContinuation/ProviderSearch_LC_HUM.search';
import { getRecord } from 'lightning/uiRecordApi';
import MEMBER_NAME from '@salesforce/schema/Case.Account.Name';
import { NavigationMixin } from 'lightning/navigation';
import { getLabels } from 'c/crmUtilityHum';

export default class QAAComplaint extends NavigationMixin(LightningElement) {
    @api recordId;
    @track lstQAAList;
    @track oDetails = {};
    @track isDataPresent = false;
    @track bShowTable = false;
    @track accountList;
    @track resultreceived = [];
    @track labels = getLabels();
    @track oLayout;
    @track previousSelectedRow;
    @track bProviderSelected = false;
    @track bSelectedProviderValid = false;
    @track bProviderFieldDisabled = false;
    @track sAccountName;
    @track providerErrors = [];
    @track isSearching = false;
    @track careIssueHelpText = qaaConstants.CARE_ISSUE_HELP;
    @track qaaInputFieldsList = [];
    @track bDatafound = false;
    @track bResultFound = false;
    @track formData = {
        sTaxID: '',
        sNPI: '',
        sFacilityName: '',
        sFirstName: '',
        sLastName: '',
        sState: 'none',
        sSpeciality: 'none',
        sPostalCode: '',
        sAddress: '',
        sCity: ''
    };
    @track sIncidentDate = '';

    @wire(getRecord, {
        recordId: '$recordId',
        fields: [MEMBER_NAME]
    }) wireCallBack({ error, data }) {
        if (error) {
            this.error = error;
        } else if (data) {
            debugger;
            this.sAccountName = data.fields.Account.displayValue;
            this.getQAATemplateInfo();
        }
    }

    getQAATemplateInfo() {
        getQAATemplateDetails()
            .then(result => {
                if (result) {
                    this.lstQAAList = this.prepareQuestionsList(result);
                    this.setQuestionVisibility(this.oDetails.MembersParentQuestion.oField.questionRec.Question_Label__c, this.oDetails.MembersParentQuestion.oField.sSelectedDropDownOption);
                }
            })
            .catch(error => {
                console.log('Error : ', error);
            });
    }

    prepareQuestionsList(qaaDetails) {
        qaaLayOutDetails.lstLinks = [];
        qaaDetails.forEach((ques, index) => {
            ques = this.prepareDropDownOptions(ques);
            this.prepareLayOut(ques);
        });
        this.oDetails.SubmittingPerson.oField.sValue = this.sAccountName;
        this.isDataPresent = true;
        return qaaDetails;
    }

    prepareLayOut(ques) {
        for (const [key, value] of Object.entries(qaaLayOutDetails)) {
            if (value.Label == ques.questionRec.Question_Label__c) {
                value.oField = ques;
                if (qaaInputFields.includes(key)) this.qaaInputFieldsList.push(ques);
                if (customizedLabels[key]) value.oField.questionRec.Question_Label__c = customizedLabels[key];
                break;
            } else if (ques.questionRec.Question_Type__c == 'Link') {
                ques.questionRec.Display_text__c = ques.questionRec.Display_text__c.replace('http:', 'https:')
                qaaLayOutDetails.lstLinks.push(ques);
                break;
            }
            else {
                let isFound = false;
                for (const [key, value] of Object.entries(customizedQuestions)) {
                    if (ques.questionRec.Question_Label__c.includes(value)) {
                        if (value == qaaConstants.MEM_ADVISE) {
                            if (ques.questionRec.Question_Label__c.includes(qaaConstants.MEM_SUBMITTING_COMPLAINT)) {
                                ques.questionRec.Question_Label__c = ques.questionRec.Question_Label__c.replaceAll('"', '');
                                qaaLayOutDetails[key].oField = ques;
                                isFound = true;
                                break;
                            }
                        } else {
                            ques.questionRec.Question_Label__c = ques.questionRec.Question_Label__c.replace(value, `<b>${value}</b>`);
                            qaaLayOutDetails[key].oField = ques;
                            isFound = true;
                            break;
                        }
                    }
                }
                if (isFound) break;
            }
        }
        this.oDetails = qaaLayOutDetails;
    }

    prepareDropDownOptions(ques) {
        let lstDropDownValues = [];
        if (ques.questionRec.Question_Type__c == 'Drop down') {
            if (ques.questionRec.Question_Label__c != 'Provider State') {
                ques.lstDropdownOptions.forEach(val => {
                    lstDropDownValues.push({ 'label': val, 'value': val });
                });
            } else {
                for (const [key, value] of Object.entries(ques.mapState)) {
                    lstDropDownValues.push({ 'label': value, 'value': key });
                }
            }
            ques.lstDropDownOptionsTemp = lstDropDownValues;
        }
        if (ques.questionRec.Question_Label__c == qaaConstants.PROVIDER_OR_GROUP) ques.sValue = ques.sSelectedDropDownOption = 'Provider';
        else if (ques.questionRec.Question_Label__c == qaaConstants.QAA_PARENT_QUESTION) ques.sValue = ques.sSelectedDropDownOption = 'Yes';
        return ques;
    }

    handleProviderDataChange(event) {
        this.setValue(event);
        for (const [key, value] of Object.entries(providerFields)) {
            if (this.oDetails[key].oField.questionRec.Question_Label__c == event.target.dataset.name) {
                this.formData[value] =this.oDetails[key].oField.sValue= event.target.value;
                break;
            }
        }
       
    }

    isNumeric(sVal) {
        let isNumber = [...sVal].every(i => '0123456789'.includes(i));
        return isNumber;
    }

    handleSelectionChange(event) {
        this.setValue(event);
        if (event.target.dataset.name == qaaConstants.QAA_PARENT_QUESTION) {
            this.setQuestionVisibility(event.target.dataset.name, event.target.value);
        } else if (event.target.dataset.name == qaaConstants.ACCESSIBILITY_CARE_ISSUE) {
            this.setProviderFieldsAccess(event);
        } else if (event.target.dataset.name == 'State') {
            this.formData['sState'] =this.oDetails.ProviderState.oField.sSelectedDropDownOption= event.target.value;
        }
    }

    setProviderFieldsAccess(event){
        if(event.target.value == 'Yes' || event.target.value == 'No'){
           
           if(!this.bProviderSelected){
            this.oDetails.ProviderORGroup.oField.sValue = this.oDetails.ProviderORGroup.oField.sSelectedDropDownOption = 'Provider';
            this.setProviderFieldsVisibility();
            }
           
        }

    }

    handleProviderSelectionChange(event) {
        this.setValue(event);
        this.oDetails.ProviderORGroup.oField.sValue = this.oDetails.ProviderORGroup.oField.sSelectedDropDownOption = event.target.value;
        this.setProviderFieldsVisibility();
    }

    setProviderErrorVisibility(displayString) {
        let provErrDiv = this.template.querySelector('.providerErr');
        if (provErrDiv) provErrDiv.style.display = displayString;
    }

    handleDataChange(event) {
        this.setValue(event);
    }

    handleDateChange(event) {
        if (event.target.value && event.target.value.length === 10) {
            let sArr = event.target.value.split('-');
            this.sIncidentDate = `${sArr[1]}/${sArr[2]}/${sArr[0]}`;
        }
        this.setValue(event);
    }

    setValue(event) {
        for (const ele of this.lstQAAList) {
            if (ele.questionRec.Question_Label__c == event.target.dataset.name) {
                ele.sValue = event.target.value;
                if (ele.questionRec.Question_Type__c == 'Drop down') {
                    ele.sSelectedDropDownOption = event.target.value;
                }
                break;
            }
        }
    }

    setQuestionVisibility(questionName, sValue) {
        this.lstQAAList.forEach(ques => {
            if (ques.questionRec.Parent_Question__r && ques.questionRec.Parent_Question__r.Question_Label__c == questionName) {
                if (ques.questionRec.Dependent_Value__c) {
                    let dependentValues = ques.questionRec.Dependent_Value__c.split(';');
                    ques.boolDisplayQuestion = (dependentValues.includes(sValue)) ? true : false;
                }
            }
        });
        this.setProviderFieldsVisibility();
    }

    setProviderFieldsVisibility() {
        if (this.oDetails.ProviderORGroup.oField.sSelectedDropDownOption == qaaConstants.PROVIDER) {
            this.oDetails.ProviderFirstName.oField.boolDisplayQuestion = true;
            this.oDetails.ProviderLastName.oField.boolDisplayQuestion = true;
            this.oDetails.ProviderFacilityGroup.oField.boolDisplayQuestion = false;
            this.oDetails.ProviderFacilityGroup.oField.sValue =this.formData.sFacilityName= '';
        } else if (this.oDetails.ProviderORGroup.oField.sSelectedDropDownOption == qaaConstants.FACILITY_GROUP) {
            this.oDetails.ProviderFirstName.oField.boolDisplayQuestion = false;
            this.oDetails.ProviderLastName.oField.boolDisplayQuestion = false;
            this.oDetails.ProviderFacilityGroup.oField.boolDisplayQuestion = true;
            this.oDetails.ProviderFirstName.oField.sValue = this.formData.sFirstName = '';
            this.oDetails.ProviderLastName.oField.sValue = this.formData.sLastName = '';
        }
    }

    renderedCallback() {
        let firstDiv = this.template.querySelector('.firstDiv');
        if (firstDiv) {
            let iHeight = firstDiv.offsetHeight;
            let secDiv = this.template.querySelector('.secDiv');
            if (secDiv) {
                iHeight = (iHeight * 3) - 20;
                secDiv.style.height = `${iHeight}px`;
            }
        }
    }

    validateProviderFields() {
        debugger;
        let isValid = true;
        this.providerErrors = [];
        if (!(this.oDetails.ProviderTaxID.oField.sValue || this.oDetails.ProviderNPI.oField.sValue || this.oDetails.ProviderFacilityGroup.oField.sValue
            || this.oDetails.ProviderState.oField.sValue || this.oDetails.ProviderFirstName.oField.sValue || this.oDetails.ProviderLastName.oField.sValue
            || this.oDetails.ProviderCity.oField.sValue || this.oDetails.ProviderAddress.oField.sValue || this.oDetails.ProviderZip.oField.sValue)) {
            this.showHideProviderError(true, this.labels.providerCombinationError_Hum, 'block');
            isValid = false;
            return isValid;
        }
        if (!this.validateIndividualFieldsData()) isValid = false;
        if (!isValid) this.setProviderErrorVisibility('block');
        return isValid;
    }

    validateIndividualFieldsData() {
        let bIsValid = true;
        if (!this.validateNPI()) bIsValid = false;
        if (!this.validateTaxId()) bIsValid = false;
        if (this.oDetails.ProviderORGroup.oField.sValue == qaaConstants.FACILITY_GROUP) {
            if (!this.validateFacilityGroupName()) bIsValid = false;
        }
        else {
            if (!this.validateFirstName()) bIsValid = false;
            if (!this.validateLastName()) bIsValid = false;
        }
        if (!this.validatePostalCode()) bIsValid = false;
        if (!this.validateProviderSearchCombination()) bIsValid = false;
        return bIsValid;
    }

    validateFirstName() {
        let firstnamePatteren = /^[a-zA-Z0-9-.\' , &]*$/;
        if (this.oDetails.ProviderFirstName.oField.sValue && !firstnamePatteren.test(this.oDetails.ProviderFirstName.oField.sValue)) {
            this.providerErrors.push(this.labels.providerFirstNameError_Hum);
            return false;
        }
        return true;
    }

    validateLastName() {
        let bValid = true;
        let sPatteren = /^[a-zA-Z0-9-.\' , &]*$/;
        if (this.oDetails.ProviderLastName.oField.sValue) {
            if (!sPatteren.test(this.oDetails.ProviderLastName.oField.sValue)) {
                this.providerErrors.push(this.labels.providerLastNameError_Hum);
                bValid = false;
            }
            if (this.oDetails.ProviderLastName.oField.sValue.length < 2) {
                this.providerErrors.push(this.labels.providerLastNameLengthError_Hum);
                bValid = false;
            }
        }
        return bValid;
    }

    validateFacilityGroupName() {
        let bValid = true;
        let sPatteren = /^[a-zA-Z0-9-.\' , &]*$/;
        if (this.oDetails.ProviderFacilityGroup.oField.sValue) {
            if (!sPatteren.test(this.oDetails.ProviderFacilityGroup.oField.sValue)) {
                this.providerErrors.push(this.labels.providerFacilityGroupError_Hum);
                bValid = false;
            }
            if (this.oDetails.ProviderFacilityGroup.oField.sValue.length < 3) {
                this.providerErrors.push(this.labels.providerFacilityGroupLengthError_Hum);
                bValid = false;
            }
        }
        return bValid;
    }

    validateNPI() {
        if (this.oDetails.ProviderNPI.oField.sValue && (this.oDetails.ProviderNPI.oField.sValue.length != 10 || !this.isNumeric(this.oDetails.ProviderNPI.oField.sValue))) {
            this.providerErrors.push(this.labels.providerNPIError_Hum);
            return false;
        }
        return true;
    }

    validateTaxId() {
        if (this.oDetails.ProviderTaxID.oField.sValue && (this.oDetails.ProviderTaxID.oField.sValue.length != 9 || !this.isNumeric(this.oDetails.ProviderTaxID.oField.sValue))) {
            this.providerErrors.push(this.labels.providerTaxError_Hum);
            return false;
        }
        return true;
    }

    validatePostalCode() {
        if (this.oDetails.ProviderZip.oField.sValue && (this.oDetails.ProviderZip.oField.sValue.length != 5 || !this.isNumeric(this.oDetails.ProviderZip.oField.sValue))) {
            this.providerErrors.push(this.labels.providerZipCodeError_Hum);
            return false;
        }
        return true;
    }

    validateProviderSearchCombination() {
        let isValid = true;
        if (!(this.oDetails.ProviderTaxID.oField.sValue || this.oDetails.ProviderNPI.oField.sValue || this.oDetails.ProviderFacilityGroup.oField.sValue
            || this.oDetails.ProviderState.oField.sValue || this.oDetails.ProviderFirstName.oField.sValue || this.oDetails.ProviderLastName.oField.sValue)) {
            isValid = false;
        }
        if (!this.oDetails.ProviderTaxID.oField.sValue && !this.oDetails.ProviderNPI.oField.sValue && ((this.oDetails.ProviderORGroup.oField.sValue == qaaConstants.FACILITY_GROUP && (!this.oDetails.ProviderFacilityGroup.oField.sValue
            || !this.oDetails.ProviderState.oField.sValue)) || (this.oDetails.ProviderORGroup.oField.sValue == qaaConstants.PROVIDER && (!this.oDetails.ProviderState.oField.sValue || !this.oDetails.ProviderFirstName.oField.sValue || !this.oDetails.ProviderLastName.oField.sValue)))) {
            isValid = false;
        }
        if (!isValid) {
            this.providerErrors.push(this.labels.providerCombinationError_Hum);
            return false;
        }
        return isValid;
    }

    handleSearchClick() {
        let sSortBy = 'sFirstName';
        this.bShowTable = false;
        this.bResultFound = false;
        if (this.validateProviderFields()) {
            this.bResultFound = true;
            this.showHideProviderError(true, '', 'none');
            this.isSearching = true;
            startRequest({ searchFormData: this.formData })
                .then(result => {
                    if (result) {
                        let count = 1;
                        this.accountList = JSON.parse(result).map(item => {
                            item.sUniqueId = count;
                            item.sFacilityName = `${item.sFirstName} ${item.sLastName}`;
                            count = count + 1;
                            return {
                                ...item
                            }
                        });

                        this.isSearching = false;
                        this.oLayout = JSON.parse(JSON.stringify(providerResultLayout));
                        if (this.oDetails.ProviderORGroup.oField.sValue == qaaConstants.FACILITY_GROUP) {
                            this.oLayout[0][1].compoundvalue[1].label = 'Facility/Group Name';
                            this.oLayout[0][1].compoundvalue[1].fieldName = 'sFacilityName';
                            this.oLayout[0][1].compoundvalue.splice(0, 1);
                            sSortBy = "sFacilityName";
                        }
                        this.accountList = this.accountList.length > 0 ? this.SortArr(this.accountList, sSortBy) : '';
                        this.resultreceived = this.accountList
                        this.bShowTable = true;
                        this.bDatafound = this.accountList && this.accountList?.length > 0 ? true : false;
                    }
                }).catch(error => {
                    console.log('Error Occured', error);
                    this.isSearching = false;
                });
        }
    }

    SortArr(arr, sField) {
        try {
            arr.sort((a, b) => {
                const nameA = a[sField] ? a[sField].toUpperCase().trim() : '';
                const nameB = b[sField] ? b[sField].toUpperCase().trim() : '';
                if (nameA < nameB) {
                    return -1;
                }
                if (nameA > nameB) {
                    return 1;
                }
                return 0;
            });
            return arr;
        }
        catch (err) {
            return err;
        }
    }
    handleRowSelection(event) {
        this.showHideProviderError(true, '', 'none');
        if (this.previousSelectedRow) {
            this.previousSelectedRow.style.backgroundColor = '';
        }
        if (event.detail) {
            event.detail.style.backgroundColor = 'lightgray';
            this.previousSelectedRow = event.detail;
        }
        let sId = event.detail.children[1].children[0].innerHTML;
        const selectedRow = this.resultreceived.find((ele) => ele.sUniqueId == sId);

        if (selectedRow) {
            for (const [key, value] of Object.entries(providerFields)) {
                if (key != qaaConstants.PROVIDER_STATE && key != qaaConstants.PROVIDER_TAX_ID) this.oDetails[key].oField.sValue = selectedRow[value];
                else if (key == qaaConstants.PROVIDER_STATE) this.oDetails[key].oField.sValue = this.oDetails[key].oField.sSelectedDropDownOption = selectedRow[value];
                else {
                    let lst = selectedRow[value].split(',');
                    this.oDetails[key].oField.sValue = lst[0];
                }
            }
            if (this.oDetails.ProviderORGroup.oField.sValue == 'Facility/Group') this.oDetails.ProviderLastName.oField.sValue = '';
            if (!this.oDetails.ProviderTaxID.oField.sValue && !this.oDetails.ProviderNPI.oField.sValue) {
                this.showHideProviderError(true, this.labels.providerTAXIDAndNPIIDCombinationError_Hum, 'block');
                this.bSelectedProviderValid = false;
            }
            else {
                this.bSelectedProviderValid = true;
            }
            this.bProviderSelected = true;
            this.bProviderFieldDisabled = true;
        }
    }

    showHideProviderError(isResetRequired, sErrorMessage, sErrStyle) {
        if (isResetRequired) this.providerErrors = [];
        if (sErrorMessage) this.providerErrors.push(sErrorMessage);
        this.setProviderErrorVisibility(sErrStyle);
    }

    handleClearClick(event) {
        for (const [key, value] of Object.entries(providerFields)) {
            this.oDetails[key].oField.sValue = this.oDetails[key].oField.sSelectedDropDownOption = this.formData[value] = '';
        }
        this.bProviderSelected = false;
        this.bProviderFieldDisabled = false;
        this.bSelectedProviderValid = false;
        this.bShowTable = false;
        this.resultreceived = [];
        this.showHideProviderError(true, '', 'none');
        this.oDetails.ProviderTaxID.oField.boolDisplayQuestion = false;
        this.oDetails.ProviderNPI.oField.boolDisplayQuestion = false;
        this.oDetails.ProviderTaxID.oField.boolDisplayQuestion = true;
        this.oDetails.ProviderNPI.oField.boolDisplayQuestion = true;
        this.bResultFound = false;
    }

    handleComplaintDetailsChange(event) {
        this.setValue(event);
        this.validateComplaintDetails(event.target.value, 'complDiv', 'errDiv');
    }

    validateTemplateData(sVal) {
        let bIsValid = true;
        if (this.oDetails.MembersParentQuestion.oField.sValue == 'Yes') {
            bIsValid = this.validateInputElement([...this.template.querySelectorAll('lightning-input.inputEle')], bIsValid);
            let tempIsValid = this.validateComplaintDetails(this.oDetails.ComplaintDetails.oField.sValue, 'complDiv', 'errDiv');
            bIsValid = bIsValid ? tempIsValid : bIsValid;
            bIsValid = this.validateInputElement([...this.template.querySelectorAll('lightning-combobox.inputEle')], bIsValid);

        } else {
            this.oDetails.SubmittingPerson.oField.sValue = '';
            this.oDetails.ProviderORGroup.oField.sValue = this.oDetails.ProviderORGroup.oField.sSelectedDropDownOption = '';
        }
        return bIsValid;
    }

    validateInputElement(inputelements, bIsValid) {
        inputelements.forEach(ele => {
            if (!ele.checkValidity()) {
                ele.reportValidity();
                bIsValid = false;
            }
        });
        return bIsValid;
    }

    validateComplaintDetails(sVal) {
        let isValid = true;
        let complaintDiv = this.template.querySelector('.complDiv');
        let errDiv = this.template.querySelector('.errDiv');
        if (sVal) {
            if (complaintDiv) complaintDiv.classList.remove('slds-has-error');
            if (errDiv) errDiv.style.display = 'none';
        } else {
            complaintDiv.classList.add('slds-has-error');
            if (errDiv) errDiv.style.display = 'block';
            isValid = false;
        }
        return isValid;
    }

    handleSaveClick(event) {
        if (this.validateTemplateData()) {
            this.oDetails.ProcessType.oField.sValue = 'QAA Process';
            delete this.lstQAAList.lstDropDownOptionsTemp;
            delete this.qaaInputFieldsList.lstDropDownOptionsTemp;
            for (let obj of this.qaaInputFieldsList) {
                if (obj.questionRec.Question_Label__c === 'Date of the incident/service') {
                    obj.sValue = this.sIncidentDate;
                    break;
                }
            }
            const jsonString = JSON.stringify({ lstResponses: this.qaaInputFieldsList });
            saveQAATemplate({ scaseid: this.recordId, jsonqaadata: jsonString })
                .then(results => {
                    const objSaveEvent = new CustomEvent('savesucess', {});
                    this.dispatchEvent(objSaveEvent);
                })
                .catch(error => { console.log('error:', error); });
        }
    }
}