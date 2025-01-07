/*******************************************************************************************************************************
LWC JS Name : ProviderSearchCaseLookupHum.js
Function    : This LWC component used to search providers in case edit page
Modification Log:
Developer Name           Code Review              Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* SravanKumar Ch                                   11/05/2023                 Provider Search popup initial version
***********************************************************************************************************************************************/

import { LightningElement, track, wire, api } from 'lwc';
import getStateValues from '@salesforce/apex/SearchUtilty_H_HUM.getStateValues';
import getSpecialityOptions from '@salesforce/apex/ProviderSearch_LC_HUM.getSpeciality';
import { getLabels, isSpecialCharsExists, hcConstants,toastMsge } from "c/crmUtilityHum"; 

export default class ProviderSearchCaseLookupHum extends LightningElement {
    @track stateOptions = [];
    @track noResults = false;
    @track specialityOpts = [];
    @track labels = getLabels();
    @track showErrorMsg = false;
    @track showErrorMsgNew = false;
    @track stateValue;
    @track specialtyValue;
    @track showValidationMsg = false;
    @track showSpecialityError = false;
    @track isFormValid = true;

    @api get accountSearchHistory(){
        return this.formData;
    }

    @track formData = {
        sTaxID: this.sTaxID,
        sNPI: this.sNPI,
        sFacilityName: this.sFacilityName,
        sFirstName: this.sFirstName,
        sLastName: this.sLastName,
        sState: this.sState,
        sPostalCode: this.sPostalCode,
        sSpeciality: this.sSpeciality
    };

    @track formState = {
        isTaxId: false,
        isNPI: false,
        isFacility: false,
        isFirstName: false,
        isLastName: false,
        isPostalCode: false,
        isSpecialty: false
    };

    sTaxID = '';
    sNPI = '';
    sFacilityName = '';
    sFirstName = '';
    sLastName = '';
    sState = '';
    sPostalCode = '';
    sSpeciality = '';  
    @api encodeddata;
    @api selectedField ='';


    connectedCallback() {
        this.noResults = false; 
        getStateValues().then(data => {
            if (data) {
                const sOptions = [];
                for (let key in data) {
                    sOptions.push({ label: key, value: data[key] });
                }
                this.stateOptions = sOptions;
            }
        }).catch(error => {
            console.log('Error Occured', error);
        });

        getSpecialityOptions().then(opts => {
            if (opts) {
                var optsArray = [];
                for (let key in opts) {
                    optsArray.push({ label: key, value: opts[key] });
                }
                this.specialityOpts = optsArray;
            }
        }).catch(error => {
            console.log('Error Occured', error);
        });
    }

    @api
    encodedValues(encodedData) {
        let sField = encodedData.sField;
        this.formData['sField'] = sField;
    }

    /**
     * update data on field change
     * @param {*} evnt 
     */
    onFieldChange(evnt) {
        const fieldKey = evnt.currentTarget.getAttribute('data-id');
        this.formData[fieldKey] = evnt.target.value;
    }

    verifySpecialChars(field, errorMsg) {
        if (isSpecialCharsExists(field.value)) {
            this.updateFieldValidation(field, errorMsg);
            this.isFormValid = false;
        }
    }

    verifyFieldLength(field, length, errMessage) {
        if (field.value.length < length) {
            this.updateFieldValidation(field, errMessage);
            this.isFormValid = false;
        }
    }

    validateFieldData(regEx, field, errorMessage) {
        const me = this;
        if (!regEx.test(field.value)) {
            me.updateFieldValidation(field, errorMessage);
            me.isFormValid = false;
        }
    }

    validateProviderFrom() {
        const me = this;
        let cont = 0;
        let fields = 6;
        me.clearSearchResults();
        let hasFieldValue = false;
        me.showErrorMsg = false;
        me.showErrorMsgNew = false;
        me.isFormValid = true;
        const inpFields = this.template.querySelectorAll(".inputfield");
        const reZipCode = new RegExp(/\d{5}/);
        const reWordSelect = new RegExp(/\w+/);

        inpFields.forEach(function (field) {
            field.required = false;
            me.updateFieldValidation(field, ""); // Reset Errors
            if (field.value) {
                hasFieldValue = true;
                me.showErrorMsg = false;
            }
            switch (field.name) {
                case 'pLastName':
                    me.formState.lastNameField = field;
                    me.formState.isLastName = field.value ? true : false;
                    if (field.value) {
                        me.verifySpecialChars(field, 'The only special characters that can be used are an apostrophe, a period, a dash or a comma in the Last Name field.');
                        if (field.value.length < 2) me.verifyFieldLength(field, 2, 'Last Name must be at least 2 characters');
                    }
                    break;
                case 'pFirstName':
                    me.formState.firstNameField = field;
                    me.formState.isFirstName = field.value ? true : false;
                    if (field.value) {
                        me.verifySpecialChars(field, 'The only special characters that can be used are an apostrophe, a period, a dash, a comma or an ampersand in the First Name field.');
                    }
                    break;
                case 'pFacilityName':
                    me.formState.facilityField = field;
                    me.formState.isFacility = field.value ? true : false;
                    if (field.value) {
                        me.verifySpecialChars(field, 'Please enter Alpha-Numeric characters for Facility Name field');
                        me.validateFieldData(reWordSelect, field, 'Please enter Alpha-Numeric characters for Facility Name field');
                    }
                    break;
                case  'pNPI': /**Validacion de NPI */
                    me.formState.npiField = field;
                    me.formState.isNPI = field.value ? true : false;
                    if (field.value) {
                        if (field.value.length < 10) me.verifyFieldLength(field, 10, 'NPI must be 10 Characters');
                        me.verifySpecialChars(field, 'Please enter Alpha-Numeric characters for NPI field');
                        me.validateFieldData(reWordSelect, field, 'Please enter Alpha-Numeric characters for NPI field');
                    }
                    break;
                case  'pTaxID': /**Validacion de NPI */
                    me.formState.taxIdField = field;
                    me.formState.isTaxId = field.value ? true : false;
                    if (field.value) {
                        if (field.value.length < 9) me.verifyFieldLength(field, 9, 'Tax ID must be 9 Characters');
                        me.verifySpecialChars(field, 'Please enter Alpha-Numeric characters for Tax ID field');
                        me.validateFieldData(reWordSelect, field, 'Please enter Alpha-Numeric characters for Tax ID field');
                    }
                    break;
                case  'pZipCode': /**Validacion de NPI */
                    me.formState.postalCodeField = field;
                    me.formState.isPostalCode = field.value ? true : false;
                    if (field.value) {
                        me.validateFieldData(reZipCode, field, me.labels.memberSearchZipEnterHum)
                    }
                    break;
                default:
                    break;
            }
        });
        if (!hasFieldValue && !me.stateValue && !me.specialtyValue) {
            me.showErrorMsg = true;
            me.isFormValid = false;
            return;
        }

        if(this.isFormValid){
            if(!me.validateFacilityCombination()) return;
            if(!me.checkUniqueProviderSearch()) return;
            if(!me.validateLastNameSearchCombination()) return;
            if(!me.validateFirstNameSearchCombination()) return;
            if(!me.validateStateSearchCombination()) return;
            if(!me.validateZipcodeSearchCombination()) return;
            if(!me.validateSpecialitySearchCombination()) return;
        }
        if (me.isFormValid) {
            this.template.querySelector('c-provider-search-lookup-table-hum').handleProviderSearchEvent(me.formData);
        }
    }

    validateFacilityCombination(){
        let isValid = true;
        if(((this.formState.isTaxId && this.formState.isFacility) || this.formState.isFacility) && 
        (this.formState.isFirstName ||this.formState.isLastName)){
            this.isFormValid = isValid = false;
            this.updateFieldValidation(this.formState.facilityField, 'Facility/Provider Group and First/Last Name search criteria cannot be used together in a search execution. Please remove one of those search criterions and/or use a different combination.');
        }
        return isValid;
    }

    checkUniqueProviderSearch()
    {
        let bUniqueueSearchFlag = true;
        if(this.formState.isFacility && (this.formState.isNPI || this.checkIsFieldsEmpty('Facility')))
        {
            bUniqueueSearchFlag = false;
        }
        if(this.formState.isNPI && (this.formState.isFacility || this.checkIsFieldsEmpty(undefined)))
        {
            bUniqueueSearchFlag = false;
        }
        if(this.formState.isPostalCode && this.checkIsFieldsEmpty('PostalCode'))
        {
            bUniqueueSearchFlag = false;
        }
        if(this.specialtyValue && this.checkIsFieldsEmpty('Speciality'))
        {
            bUniqueueSearchFlag = false;
        }
        if(this.formState.isFirstName && this.formState.isLastName && this.checkIsFieldsEmpty('FNLN'))
        {
            bUniqueueSearchFlag = false;
        }
        if(!bUniqueueSearchFlag)
        {      
            this.isFormValid = false;
            this.showErrorMsgNew = true;
        }
        return bUniqueueSearchFlag;
    }

    validateLastNameSearchCombination()
    {
        let isValid = true;
        if(this.formState.isLastName && !this.formState.isFirstName) 
        {
            this.isFormValid = isValid = false;
            this.updateFieldValidation(this.formState.lastNameField, 'Please search using First Name + Last Name + any other Secondary Criteria (optional)');
        }
        return isValid;
     }

    validateFirstNameSearchCombination()
    {
        let isValid = true;
        if(this.formState.isFirstName && !this.formState.isLastName) 
        {
            this.isFormValid = isValid = false;
            this.updateFieldValidation(this.formState.firstNameField, 'Please search using First Name + Last Name + any other Secondary Criteria (optional)');
        }
        return isValid;
    }

    validateStateSearchCombination()
    {
        let isValid = true;
        if(this.stateValue) 
        {
            if(!this.formState.isFacility && (!this.formState.isFirstName || !this.formState.isLastName) && (!this.formState.isTaxId || !this.formState.isPostalCode)) 
            {
                this.isFormValid = isValid = false;
                this.showValidationMsg = true;
            }
        }
        return isValid;
    }

    validateZipcodeSearchCombination()
    {
        let isValid = true;
        if(this.formState.isPostalCode) 
        {
            if(!this.formState.isFacility && (!this.formState.isFirstName || !this.formState.isLastName) && !this.formState.isTaxId)
            {
                this.isFormValid = isValid = false;
                this.updateFieldValidation(this.formState.postalCodeField, 'Please search using Zip Code + any of the following secondary criteria: First Name + Last Name,  Facility/Provider Group');
            }
        }
        return isValid;
    }

    validateSpecialitySearchCombination()
      {
        let isValid = true;
          if(this.specialtyValue && !this.formState.isFirstName && !this.formState.isLastName) 
          {
            this.isFormValid = isValid = false;
            this.showSpecialityError = true;
          }
          return isValid;
      }

    checkIsFieldsEmpty(sFieldName)
    {
        if((!sFieldName && (this.formState.isFirstName || this.formState.isLastName || this.stateValue || 
          this.formState.isPostalCode || this.specialtyValue)) ||
          (sFieldName && sFieldName == 'Facility' && (this.formState.isFirstName || this.formState.isLastName  || this.specialtyValue)) ||
          (sFieldName && sFieldName == 'PostalCode' && (this.specialtyValue || this.formState.isNPI)) ||
          (sFieldName && sFieldName== 'Speciality' && (this.stateValue || this.formState.isPostalCode ||
            this.formState.isNPI || this.formState.isFacility)) ||
          (sFieldName && sFieldName == 'FNLN' && (this.formState.isNPI || this.formState.isFacility)))
          {
               return true;
          }
        
        return false;
    }

    clearSearchResults() {
        this.template.querySelector('c-provider-search-lookup-table-hum').resetResults();
    }

    verifyValueWithRegEx(regEx, field, errorMessage) {
        if (!regEx.test(field.value)) {
            this.updateFieldValidation(field, errorMessage);
            return true;
        }
    }

    fireCustomEvent(eventName, data) {
        const customEvent = new CustomEvent(eventName, {
            detail: data
        });
        this.dispatchEvent(customEvent);
    }

    handleDropDownChange(evnt) {
        const fieldKey = evnt.currentTarget.getAttribute('data-id');
        const { value } = evnt.target;
        if (value !== hcConstants.OPTION_NONE) {
            this.formData[fieldKey] = evnt.target.value;
            this.highlightFieldsProvider();
        } else {
            this.formData[fieldKey] = "";
            this.removehighlightProvider();
        }
    }

    /**
   * Handle state clear
   * @param {*} event 
   */
    clearStateHandler(event) {
        this.formData.sState = "";
        this.stateValue = false;
        this.showValidationMsg = false;
      }

    /**
   * Handle specialty clear
   * @param {*} event 
   */
    clearSpecialtyHandler(event) {
        this.formData.sSpeciality = "";
        this.specialtyValue = false;
        this.showSpecialityError = false;
      }

    /**
   * Handle state chagne
   * @param {*} event 
   */
  stateSelectionHandler(event) {
    let stateVal = event.detail.value;
    this.formData.sState = stateVal;
    if(stateVal.length) {
      this.stateValue = true;
      this.highlightFieldsProvider();
    } else {
      this.formData.sState = "";
     this.removehighlightProvider();
    }
  }

   /**
   * Handle specialty chaage
   * @param {*} event 
   */
  specialtySelectionHandler(event) {
    let specialtyVal = event.detail.value;
    this.formData.sSpeciality = specialtyVal;
    if(specialtyVal.length) {
      this.specialtyValue = true;
      this.highlightFieldsProvider();
    } else {
      this.formData.sSpeciality = "";
     this.removehighlightProvider();
    }
  }

    highlightFieldsProvider() {
        var inp = this.template.querySelectorAll(".NameHighlightProvider");
        inp.forEach(function (element) {
            element.required = true;
            element.reportValidity();
        }, this);
    }

    removehighlightProvider() {
        if (document.activeElement.tagName != 'INPUT') {
            var inp = this.template.querySelectorAll(".NameHighlightProvider");
            //Updated validation rule to remove highlight from name fields based on Statevalue selected
            const { sFirstName, sLastName, sState } = this.formData;
            inp.forEach(function (element) {
                element.required = (undefined !== sState && sState.length) ?
                                   ((undefined !== sFirstName && sFirstName.length) && (undefined !== sLastName && sLastName.length)) ? 
                                     false : true : false;
                element.reportValidity();
            }, this);
        }
    }

    enterSeach(event) {
        if (event.keyCode === 13) {
            this.handleSearch();
        }
    }

     @api
    handleResetProvider() {
        const me = this;
        const inputFields = this.template.querySelectorAll(".inputfield");

        inputFields.forEach((field) => {
            field.value = '';
            field.required = false;
            me.updateFieldValidation(field, "");
        });
        me.showErrorMsg = false;
        me.showErrorMsgNew = false;
        me.showValidationMsg = false;
        me.showSpecialityError = false;
        me.stateValue = false;
        me.specialtyValue = false;
        me.formData = {
            sTaxID: '',
            sNPI: '',
            sFacilityName: '',
            sFirstName: '',
            sLastName: '',
            sState: '',
            sPostalCode: '',
            sSpeciality: ''
        };
        const gDropDowns = this.template.querySelectorAll("c-generic-drop-down-hum");
        gDropDowns.forEach(field => {
            field.reset();
        });
        me.clearSearchResults();
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

    handleSearch(event) {
        this.formData.sUnknownProviderSearch = false;
        this.validateProviderFrom();
    }

    handleUnknownSearch(event) {
        this.formData.sUnknownProviderSearch = true;
        this.validateProviderFrom();
    }

    handleProviderAccSelection(event){
        console.log('SRAVAN--LookUp COMP ID--',event.detail.accId);
        console.log('SRAVAN--LookUp COMP NAME--',event.detail.accName);
        const addCartEvent = new CustomEvent('provideraccountselection', {
            detail: {
                accId: event.detail.accId,
                accName : event.detail.accName,
                sTaxId : event.detail.sTaxId,
                sNPIId :event.detail.sNPIId,
                formData : this.encodeddata
            }
        });
        this.dispatchEvent(addCartEvent);
    }
}