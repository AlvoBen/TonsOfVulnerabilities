/*******************************************************************************************************************************
LWC JS Name : providerSearchFormHum.js
Function    : This JS serves as controller to providerSearchFormHum.html. 

Modification Log: 
Developer Name           Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
* Pradeep Dani                                          12/18/2020                    initial version
* Arpit Jain/Navajit Sarkar				                02/10/2021				      Modifications for Search and Integration
* Supriya Shastri                                       02/24/2021                    Added generic dropdown
* Saikumar Boga                                         06/14/2021                    Changes Related to Unknown Provider Form
* Firoja Begam                                          10/27/2021                    Provider Search Name validation Rule update on State selection
* Kajal Namdev/Vardhman Jain                            04/22/2021                    US-3334446-Added a toast message in handleCreateSearch method
********************************************************************************************************************************/
import { LightningElement, track, wire, api } from 'lwc';
import getStateValues from '@salesforce/apex/SearchUtilty_H_HUM.getStateValues';
import getSpecialityOptions from '@salesforce/apex/ProviderSearch_LC_HUM.getSpeciality';
import { getLabels, isSpecialCharsExists, hcConstants,toastMsge } from "c/crmUtilityHum"; 


export default class ProviderSearchFormHum extends LightningElement {

    @track stateOptions = [];
    @track noResults = false;
    @track specialityOpts = [];
    @track labels = getLabels();
    @track showErrorMsg = false;
    @track stateValue;
    @track specialtyValue;
    @track showValidationMsg = false;
    @track showSpecialityError = false;
	@track createUnknownPdr = false;
    // @track showhidetable = false;
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

    sTaxID = '';
    sNPI = '';
    sFacilityName = '';
    sFirstName = '';
    sLastName = '';
    sState = '';
    sPostalCode = '';
    sSpeciality = '';  
    @api encodedData;


	 handleCreate(){
        this.createUnknownPdr = true;
        this.template.querySelector("c-unknown-provider-form-hum").handleValueChange(this.formData);
        
        }
    connectedCallback() {
        this.noResults = false;
        if (this.encodedData && this.encodedData != null && Object.keys(this.encodedData).includes('provider')) {
            this.encodedValues(this.encodedData);
          }
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
    encodedValues(encodedDatas) {

        let encodedData = encodedDatas['provider'];
        this.sTaxID = encodedData.sTaxID;
        this.sNPI = encodedData.sNPI;
        this.sFacilityName = encodedData.sFacilityName;
        this.sFirstName = encodedData.sFirstName;
        this.sLastName = encodedData.sLastName;
        this.sState = encodedData.sState;
        this.sPostalCode = encodedData.sPostalCode;
        this.sSpeciality = encodedData.sSpeciality;

        let me = this;
        this.template.querySelectorAll(".inputfield").forEach(function (field) {

            if (field.name == "pFirstName") {
                field.value = me.sFirstName;
            } else if (field.name == "pLastName") {
                field.value = me.sLastName;
            } else if (field.name == "pState") {
                field.value = me.sState;
            } else if (field.name == "pZipCode") {
                field.value = me.sPostalCode;
            } else if (field.name == "pSpeciality") {
                field.value = me.sSpeciality;
            } else if (field.name == "pTaxID") {
                field.value = me.sTaxID;
            } else if (field.name == "pNPI") {
                field.value = me.sNPI;
            } else if (field.name == "pFacilityName") {
                field.value = me.sFacilityName;
            }
        });

        this.formData['sTaxID'] = this.sTaxID;
        this.formData['sNPI'] = this.sNPI;
        this.formData['sFacilityName'] = this.sFacilityName;
        this.formData['sFirstName'] = this.sFirstName;
        this.formData['sLastName'] = this.sLastName;
        this.formData['sState'] = this.sState;
        this.formData['sPostalCode'] = this.sPostalCode;
        this.formData['sSpeciality'] = this.sSpeciality;

        setTimeout(() => {
            this.handleSearch();
        }, 1);
    }

    /**
     * update data on field change
     * @param {*} evnt 
     */
    onFieldChange(evnt) {
        const fieldKey = evnt.currentTarget.getAttribute('data-id');
        this.formData[fieldKey] = evnt.target.value;
    }

    validateProviderFrom() {
        const me = this;
        let cont = 0;
        let fields = 6;
        me.clearSearchResults();
        me.showErrorMsg = false;
        let hasFirstName = false;
        let hasLastName = false;
        let hasZipCode = false;
        let zipCodeField;
        let isFormInvalid = false;
        const inpFields = this.template.querySelectorAll(".inputfield");
        const reZipCode = new RegExp(/\d{5}/);
        const reNpi = new RegExp(/\w{10}/);
        const reTaxId = new RegExp(/\w{9}/);
        const { OPTION_NONE } = hcConstants;

        inpFields.forEach(function (field) {
            me.updateFieldValidation(field, ""); // Reset Errors
            if (!field.value) {
                cont = cont + 1;
            } else if (field.name == "pFirstName") {
                hasFirstName = true;
            } else if (field.name == "pLastName") {
                hasLastName = true;
            } else if (field.name == "pZipCode") {
                hasZipCode = true;
                zipCodeField = field;
            }

        });

        if (cont === fields && !me.stateValue && !me.specialtyValue) {
            me.showErrorMsg = true;
            isFormInvalid = true;
            return;
        } else if (me.stateValue && (!hasFirstName || !hasLastName)) {
            me.showValidationMsg = true;
            isFormInvalid = true;
            return;
        } else if (hasZipCode && (!hasFirstName || !hasLastName)) {
            me.updateFieldValidation(zipCodeField, me.labels.Hum_Zipcode_Name);
            isFormInvalid = true;
            return;
        } else if (me.specialtyValue && (!hasFirstName || !hasLastName)) {
            me.showSpecialityError = true;
            isFormInvalid = true;
            return;
        } else {
            me.showValidationMsg = false;
            me.showSpecialityError = false;
        }

        inpFields.forEach(function (field) {
            if (field.name) {
                me.updateFieldValidation(field, "");
                if (field.name == "pLastName") { /**Validacion de Last Name */
                    if (field.value) {
                        if (field.value.length < 2) {
                            me.updateFieldValidation(field, me.labels.memberSearchLastNameHum);
                            isFormInvalid = true;
                            return;
                        } else if (hasLastName && cont == fields - 1) {
                            me.updateFieldValidation(field, me.labels.Hum_First_Name);
                            isFormInvalid = true;
                            return;
                        }
                    }
                } else if (field.name == "pFirstName") { /**Validacion de First Name */

                    if (hasFirstName && cont == fields - 1) {
                        me.updateFieldValidation(field, me.labels.Hum_First_Name);
                        isFormInvalid = true;
                        return;
                    }
                } else if (field.name == "pNPI") { /**Validacion de NPI */
                    if (field.value) {
                        if (me.verifyValueWithRegEx(reNpi, field, me.labels.HumProviderSearchNPILength)) {
                            isFormInvalid = true;
                            return;
                        }
                    }
                } 
                else if (field.name == "pTaxID") { /**Validacion de NPI */
                    if (field.value) {
                        if (me.verifyValueWithRegEx(reTaxId, field, me.labels.HumProviderSearchTaxIDLength)) {
                            isFormInvalid = true;
                            return;
                        } 
                        else if (field.value.length > 9) {
                            me.updateFieldValidation(field, me.labels.HumProviderSearchTaxIDLength);
                            isFormInvalid = true;
                            return;
                        }
                    }
                } 
                else if (field.name == "pZipCode") {  /**Validacion de Zip Code */
                    if (field.value) {
                        if (me.verifyValueWithRegEx(reZipCode, field, me.labels.memberSearchZipEnterHum)) {
                            isFormInvalid = true;
                            return;
                        }
                    }
                }
            }
        });

        if (!isFormInvalid) {
            this.template.querySelector('c-provider-search-tables-hum').handleProviderSearchEvent(me.formData);
        }
    }

    clearSearchResults() {
        this.template.querySelector('c-provider-search-tables-hum').resetResults();
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
	 /*Method to display the Created Unknown Provider Account*/
    handleCreateSearch(event){
        let response=event.detail;
        toastMsge('Success!','Account created successfully','success','');
        this.template.querySelector('c-provider-search-tables-hum').createProviderSearchEvent(response);
    }
}