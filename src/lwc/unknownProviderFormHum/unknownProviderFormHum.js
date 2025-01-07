/*******************************************************************************************************************************
LWC JS Name : UnknownProviderFormHum.js
Function    : This JS serves as controller to UnknownProviderFormHum.html. 

Modification Log: 
Developer Name           Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
* Saikumar Boga                                       06/14/2021                    initial version for Unknown Provider Form
* Ankit Avula                                         09/01/2021                    US2365934 populate state field with zipcode input
* Ashish Kumar/Vardhman Jain                          04/18/2022                    US-3334329 Unknown Provider validations
* Bhakti Vispute									02/28/2023						US-4234513 Alignment issue/DF6965/SF43989404																								 
* Bhakti Vispute									03/16/2023						DF-7362 fixed
********************************************************************************************************************************/
import { LightningElement, api, track } from 'lwc';
import getSpecialityOptions from '@salesforce/apex/ProviderSearch_LC_HUM.getSpeciality';
import getStateValues from '@salesforce/apex/SearchUtilty_H_HUM.getStateValues';
import { getLabels, isSpecialCharsExists, hcConstants } from "c/crmUtilityHum";
import createAccountConsumerRecrd from '@salesforce/apex/UnknownProvider_LC_HUM.createAccountConsumerRecord';
import searchStateCodeWS from '@salesforce/apex/StateCodeUtility_WS_HUM.searchStateCode';
import Assets from '@salesforce/resourceUrl/CustomHelpText';
import { loadStyle } from 'lightning/platformResourceLoader';



export default class unknownProviderFormHum extends LightningElement {
   
    @api modalOpen = false;
    showErrorMsg = false;
    uapcvalue = 'I';
    uaaccountName = '';
    uaccname;
    uafirstName = '';
    ualastName = '';
    uataxID = '';
    uanpiID = '';
    uaFCPCGroup = '';
    uabillingStreet = '';
    uabillingPostalCode = '';
    uabillingCity = '';
    uabillingState = '-None-';
    uaphone = '';
    uaworkEmail = '';
    uaSpeciality = '-None-';
    @track specialityOpts = [];
    @track specialtyValue;
    @track stateOptions = [];
    @track labels = getLabels();
    disableNameField = false;
    disableAccountNameField = false;
    accountId;
	boolIsOnceSubmit = false;

    isNpiValid = false;
    isTaxValid = false;

 
    // Share the search values
    @track searchFormData = {
        sTaxID: this.uataxID,
        sNPI: this.uanpiID,
        sFacilityName: this.uaFCPCGroup,
        sFirstName: this.uafirstName,
        sLastName: this.ualastName,
        sState: this.uabillingState,
        sPostalCode: this.uabillingPostalCode,
        sSpeciality: this.uaSpeciality
    };


    sTaxID = '';
    sNPI = '';
    sFacilityName = '';
    sFirstName = '';
    sLastName = '';
    sState = '';
    sPostalCode = '';
    sSpeciality = '';
    hasRendered = true;

    renderedCallback(){
        Promise.all([
           loadStyle(this,Assets)
        ])
		if(this.modalOpen && this.hasRendered){
            let accIdEle = this.template?.querySelector('lightning-input[data-id="accountName"]');
            let fnameInputEle = this.template?.querySelector('lightning-input[data-id="firstName"]');
            let lnameInputEle = this.template?.querySelector('lightning-input[data-id="lastName"]');
            accIdEle.required = this.uapcvalue === 'I' ? false:true;
            fnameInputEle.required = this.uapcvalue === 'I' ? true:false;
            lnameInputEle.required = this.uapcvalue === 'I' ? true:false;
        }
   }


    @api handleValueChange(popFormData) {
        this.modalOpen = true;
        this.uataxID = '';
        // Get the formData from Search and 	
        if (popFormData) {

            this.uafirstName = popFormData.sFirstName
            this.ualastName = popFormData.sLastName
            this.uataxID = popFormData.sTaxID
            this.uanpiID = popFormData.sNPI
            this.uaSpeciality = popFormData.sSpeciality ? popFormData.sSpeciality : '-None-';
            this.uabillingPostalCode = popFormData.sPostalCode
            this.uabillingState = popFormData.sState ? popFormData.sState : '-None-';
            this.uapcvalue = popFormData.sFacilityName ? 'F' : 'I';
            this.uaccname = popFormData.sFacilityName?true:false;
            this.uaaccountName = popFormData.sFacilityName ? popFormData.sFacilityName : '';
            if (this.uapcvalue === 'F' || this.uapcvalue === 'G') {
                this.disableNameField = true;
                this.disableAccountNameField = false;
                this.uafirstName = '';
                this.ualastName = '';
            }
            else {
                this.disableNameField = false;
                this.disableAccountNameField = true;
            }
        }
    }


    clearSpecialtyHandler(event) {

        this.specialtyValue = false;
        this.showSpecialityError = false;
    }
	 clearStateHandler(event) {
        this.uabillingState = event.detail.value;
        
        if (this.boolIsOnceSubmit) {
         this.template.querySelector(".statelookup").showValidationError();
        }
	}

    get pcoptions() {
        return [
            { label: 'Facility', value: 'F' },
            { label: 'Individual', value: 'I' },
            { label: 'Provider Group', value: 'G' }
        ];
    }


    connectedCallback() {
       
        this.disableAccountNameField = this.uapcvalue === 'I' ? true : false;
      
    getSpecialityOptions().then(opts => {
            if (opts) {
                var optsArray = [];
                for (let key in opts) {
                if(key == 'None'){
                        optsArray.push({ label: '-None-', value: opts[key] });
                    }
                    else{
                    optsArray.push({ label: key, value: opts[key] });
					}
                }
                this.specialityOpts = optsArray;
            }
        }).catch(error => {
            console.log('Error Occured', error);
        });
        getStateValues().then(data => {
            if (data) {
                const sOptions = [];
                for (let key in data) {
                if(key == 'None'){
                        sOptions.push({ label: '-None-', value: data[key] });
                    }
                    else{
                    sOptions.push({ label: key, value: data[key] });
					}
                }
                this.stateOptions = sOptions;
            }
        }).catch(error => {
            console.log('Error Occured', error);
        });
}
    specialtySelectionHandler(event) {

        this.uaSpeciality = event.detail.value;
		if (this.boolIsOnceSubmit) {
        this.template.querySelector(".speciality").validateSpeciality();
        }
    }
    changeSpeciality(event){
        this.specialityTypedValue = event.detail.value;
         }

    handlePCChange(event) {
        const meCmp = this;
		let accIdEle = this.template?.querySelector('lightning-input[data-id="accountName"]');
        let fnameInputEle = this.template?.querySelector('lightning-input[data-id="firstName"]');
        let lnameInputEle = this.template?.querySelector('lightning-input[data-id="lastName"]');
        accIdEle.required = event?.detail?.value === 'I' ? false:true;
        fnameInputEle.required = event?.detail?.value === 'I' ? true:false;
        lnameInputEle.required = event?.detail?.value === 'I' ? true:false;
		
        const inputFields = this.template.querySelectorAll(".inputfield");
        this.template.querySelector(".statelookup").removeValidationError();
		let accNameField;
        inputFields.forEach(function (field) {
			if(field.name != 'taxID' && field.name != 'npiID' && field.name != 'accountName'){
				meCmp.updateFieldValidation(field, '');
			}
			if(field.name == 'accountName'){
                accNameField = field;
            }
        });
		
		if(this.boolIsOnceSubmit){
            meCmp.updateFieldValidation(accNameField,'');   
        }
		
        this.uapcvalue = event.detail.value;
        if (event.detail.value === 'F' || 'G') {
            this.disableNameField = event.detail.value === 'F' || 'G' ? true : false;
            this.disableAccountNameField = false;
            this.uafirstName = '';
            this.ualastName = '';
        }
        if (event.detail.value === 'I') {
            this.disableAccountNameField = event.detail.value === 'I' ? true : false;
            this.disableNameField = false;
            this.uaaccountName = '';
            this.IsIndividual = true;
            this.uaccname = false;
        }
    }



    stateSelectionHandler(event) {
        this.uabillingState = event.detail.value;
		if (this.boolIsOnceSubmit) {
         this.template.querySelector(".statelookup").showValidationError();
        }
    }

    closeModal() {
	    this.boolIsOnceSubmit = false;
        this.modalOpen = false;
        this.clearAccountdata();
    }


    handleInputChange(event) {
        if (event.target.name === 'accountName') {
            this.uaaccountName = event.target.value;
            this.uaccname = true;
        } else if (event.target.name === 'firstName') {
            this.uafirstName = event.target.value;
        } else if (event.target.name === 'lastName') {
            this.ualastName = event.target.value;
        } else if (event.target.name === 'taxID') {
            this.uataxID = event.target.value;
        } else if (event.target.name === 'npiID') {
            this.uanpiID = event.target.value;
        } else if (event.target.name === 'FCPCGroup') {
            this.uaFCPCGroup = event.target.value;
        } else if (event.target.name === 'billingStreet') {
            this.uabillingStreet = event.target.value;
        } else if (event.target.name === 'billingPostalCode') {
            this.uabillingPostalCode = event.target.value;
            this.searchStateCode(event.target.value);
        } else if (event.target.name === 'billingCity') {
            this.uabillingCity = event.target.value;
        } else if (event.target.name === 'phone') {
            this.uaphone = event.target.value;
        } else if (event.target.name === 'workEmail') {
            this.uaworkEmail = event.target.value;
        }
		if (this.boolIsOnceSubmit) {
            this.validateAll(event);
        }

        const taxInputElment = this.template.querySelector('lightning-input[data-id="taxID"]');
        const npiInputElment = this.template.querySelector('lightning-input[data-id="npiID"]');
        if (event.target.name === 'taxID') {
            if (this.uataxID && this.uataxID.length > 8) {
                npiInputElment.required = false;
                if (this.uanpiID && this.uanpiID.length > 9) { 
                    npiInputElment.required = true;
                    taxInputElment.required=true;
                }
            }
            else {
                npiInputElment.required = true;
                if (this.uanpiID && this.uanpiID.length > 9) {
					taxInputElment.required=false;
                }
            }
        }

        if (event.target.name === 'npiID') {
            if (this.uanpiID && this.uanpiID.length > 9) {
                taxInputElment.required=false;
                if (this.uataxID && this.uataxID.length > 8) {
                    npiInputElment.required = true;
                    taxInputElment.required=true;
                }
            }
            else {
                taxInputElment.required=true;
                if (this.uataxID && this.uataxID.length > 8) {
                    npiInputElment.required = false;
                }
            }
        }


        if ((event.target.name === 'npiID' || event.target.name === 'taxID') && this.boolIsOnceSubmit) {
            this.onChangeValidation();
        }
        
        if(event.target.name === 'billingPostalCode')
        {
            if(this.uabillingPostalCode && this.uabillingPostalCode.length >0 && this.uabillingPostalCode.length < 5 )
            {   
                console.log('inside billing code')
                this.updateFieldValidation(this.template.querySelector('.billingPostal') , this.labels.memberSearchZipEnterHum);
            }
            else 
            {
               this.updateFieldValidation(this.template.querySelector('.billingPostal') , '');
            }
        }

    }

    addMandatoryCheck(field) {
        this.template.querySelector(field).classList.add('mandatory');
    }

    removeMandatoryCheck(field) {
        this.template.querySelector(field).classList.remove('mandatory');
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
    updateFieldValidation(field, message) {
        field.setCustomValidity(message);
        field.reportValidity();
    }


    handleSaveClick(event) {
       
        const me = this;
		this.boolIsOnceSubmit = true;
        me.showErrorMsg = false;      
        let isFormInvalid = false;
        let isTaxInValid = false;
        let isNpiInValid = false;
        const inpFields = this.template.querySelectorAll(".inputfield");
        const reNpi = new RegExp(/\w{10}/);
        const reTaxId = new RegExp(/\w{9}/);
        const fields = { 'sobjectType': 'Account' };
        let isSaveForm = false
        if (!this.uaccname) {
            fields['Individual_First_Name__c'] = this.uafirstName;
            fields['Individual_Last_Name__c'] = this.ualastName;
            fields['Name'] = this.uafirstName + ' ' + this.ualastName;
            }
        fields['Provider_Classification__c'] = this.uapcvalue;
        if (this.uaccname) {
            fields['Name'] = this.uaaccountName;
            }
        console.log("==Input Fields== ", inpFields);
		
        /*Billing state validation*/
        let isBillingStateInvalid = this.template.querySelector(".statelookup").showValidationError();
		let isSpecialityInvalid = this.template.querySelector(".speciality").validateSpeciality();

        inpFields.forEach(function (field) {

            me.updateFieldValidation(field, '');
            if (fields['Provider_Classification__c'] == 'I') {
                if (field.name == "firstName") {
                    if (!field.value) {
                        isFormInvalid = true;
                        me.updateFieldValidation(field, me.labels.Hum_FirstNameError);
                    }
                }

                if (field.name == "lastName") {
                    if (!field.value) {
                        isFormInvalid = true;
                        me.updateFieldValidation(field, me.labels.Hum_LastNameError);
                    }
                    else if (field.value && field.value.length < 2) {
                        isFormInvalid = true;
                        me.updateFieldValidation(field, me.labels.CASE_PROVIDER_LASTNAMELENGTH_ERROR);
                    }
                }
            } else {
                if (field.name == "accountName") {
                    if (!field.value) {
                        isFormInvalid = true;
                        me.updateFieldValidation(field, me.labels.Hum_AccountNameError);
                    }
                }
            }
        });

        /* Custom Validation of NpiID and taxID */

        let validateFinal = {};
        inpFields.forEach(function (field) { //Iterate only when the validation is successfull
           
            if (field.name == 'firstName') {
                validateFinal.firstName = field.reportValidity();
            }
            else if (field.name == 'lastName') {
                validateFinal.lastName = field.reportValidity();
            }
            else if (field.name == 'taxID') {
                validateFinal.taxID = field.reportValidity();
            }
            else if (field.name == 'npiID') {
                validateFinal.npiID = field.reportValidity();
            }
            else if (field.name == 'accountName') {
                validateFinal.accountName = field.reportValidity();
            }
            else if (field.name == 'workEmail') {
                validateFinal.workEmail = field.reportValidity();
				 if (me.uaworkEmail) {
                    const reEmail = new RegExp(/^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/);
                    let boolReg = reEmail.test(me.uaworkEmail);
                    if (!boolReg) {
                        me.updateFieldValidation(field, 'Invalid Email Adress');
                        validateFinal.workEmail = false;
                    }
                    else if (boolReg) {
                        me.updateFieldValidation(field, '');
                        validateFinal.workEmail = true;
                    }
                }
            }
            else if (field.name == 'phone') {
                validateFinal.phone = field.reportValidity();
				let phone = me.getFormatedPhoneNum(me.uaphone);
                if (phone && phone.length != 10) {
                    me.updateFieldValidation(field, "Phone Numbers must be 10 digits");
                    validateFinal.phone = false;
                }
                else {
                    me.updateFieldValidation(field, '');
                    validateFinal.phone = true;
                }
            } else if (field.name == 'billingPostalCode') { 
                validateFinal.billingPostalCode = field.reportValidity();
                if (field.value && field.value!= undefined  && field.value.length > 0 && field.value.length < 5) {
                    me.updateFieldValidation(field, me.labels.memberSearchZipEnterHum);
                    validateFinal.billingPostalCode = false;
                }
                else {
                    me.updateFieldValidation(field, '');
                    validateFinal.billingPostalCode = true;
                }
            } 
		});
		// NPI TAX Validation
        this.onChangeValidation();
        validateFinal.taxID = this.isTaxValid === true ? true : false;
        validateFinal.npiID = this.isNpiValid === true ? true : false;
        isFormInvalid = true;

       if (this.uapcvalue == 'I') {
            if ((validateFinal.firstName && validateFinal.lastName && validateFinal.phone && validateFinal.workEmail && validateFinal.billingPostalCode && !isBillingStateInvalid && isSpecialityInvalid) && ((validateFinal.taxID && validateFinal.npiID))) { 
                isFormInvalid = false;
            }
        }
        else {
            if ((validateFinal.firstName && validateFinal.lastName && validateFinal.phone && validateFinal.workEmail && validateFinal.billingPostalCode && !isBillingStateInvalid && validateFinal.accountName && isSpecialityInvalid) && ((validateFinal.taxID && validateFinal.npiID))) {
                isFormInvalid = false;
            }
        }
        
        if (isFormInvalid) {
            return;
        }

        
        fields['NPI_ID__c'] = this.uanpiID;
        fields['BillingCity'] = this.uabillingCity;
        fields['BillingStateCode'] = this.uabillingState;
        fields['BillingStreet'] = this.uabillingStreet;
        fields['BillingPostalCode'] = this.uabillingPostalCode;
        fields['Phone'] = this.getFormatedPhoneNum(this.uaphone);
        fields['Work_Email__c'] = this.uaworkEmail;
        fields['Description'] = this.uaSpeciality;
        fields['HealthCloudGA__TaxId__c'] = this.uataxID;
        

        if (!isFormInvalid) {


          if (this.uanpiID) {
                this.searchFormData['sTaxID'] = '';
                this.searchFormData['sNPI'] = this.uanpiID;
                this.searchFormData['sFacilityName'] = '';
                this.searchFormData['sFirstName'] = '';
                this.searchFormData['sLastName'] = '';
                this.searchFormData['sState'] = '';
                this.searchFormData['sPostalCode'] = '';
                this.searchFormData['sSpeciality'] = '';
                this.searchFormData.sUnknownProviderSearch = true;
            }
         else if (this.uataxID) {
                this.searchFormData['sTaxID'] = this.uataxID;
                this.searchFormData.sUnknownProviderSearch = true;
                this.searchFormData['sSpeciality'] = null;
                this.searchFormData['sNPI'] = null;
                this.searchFormData['sFacilityName'] = null;
                this.searchFormData['sFirstName'] = null;
                this.searchFormData['sLastName'] = null;
                this.searchFormData['sState'] = null;
                this.searchFormData['sPostalCode'] = null;
                  }


                  /* Create Method For Unknown Provider Form */
        
            createAccountConsumerRecrd({ newRecord: fields })

                .then(result => {
                    if (result) {
                    
                        this.isSaveForm = true;
                        const selectedEvent = new CustomEvent('selected', { detail: this.searchFormData });
                        this.dispatchEvent(selectedEvent);
                        this.clearAccountdata();
                        this.modalOpen = false;
                   }
                }).catch(error => {
                    console.log('--error--' + JSON.stringify(error));
                    
                });

        }
}


    clearAccountdata() {
        this.disableNameField = false;
        this.showErrorMsg = false;
        this.disableAccountNameField = this.uapcvalue === 'I' ? true : false;
        this.uapcvalue = 'I';
        this.uaaccountName = '';
        this.uafirstName = '';
        this.ualastName = '';
        this.uataxID = '';
        this.uanpiID = '';
        this.uaFCPCGroup = '';
        this.uabillingStreet = '';
        this.uabillingPostalCode = '';
        this.uaSpeciality = '';
        this.uabillingCity = '';
        this.uabillingState = '-None-';
        this.uaphone = '';
        this.uaworkEmail = '';
    }
	//Custom Validation on Change of values
    onChangeValidation() {
        let npiField;
        let taxField
        const inputFields = this.template.querySelectorAll(".inputfield");
        inputFields.forEach(function (field) {
            if (field.name == 'taxID') {
                taxField = field;
            }
            if (field.name == 'npiID') {
                npiField = field;
            }
        });
        this.uanpiID;
        this.uataxID;
        if (this.uanpiID || this.uataxID) {

            // both have values
            if (this.uanpiID && this.uataxID) {
                if (this.uanpiID.length == 10 && this.uataxID.length == 9) {
                    this.updateFieldValidation(npiField, '');
                    this.updateFieldValidation(taxField, '');
                    this.isNpiValid = true;
                    this.isTaxValid = true;
                }
                else if (this.uanpiID.length != 10 && this.uataxID.length == 9) {
                    this.updateFieldValidation(npiField, this.labels.HumProviderSearchNPILength);
                    this.updateFieldValidation(taxField, '');
                    this.isNpiValid = false;
                    this.isTaxValid = true;
                }
                else if (this.uanpiID.length == 10 && this.uataxID.length != 9) {
                    this.updateFieldValidation(npiField, '');
                    this.updateFieldValidation(taxField, this.labels.HumProviderSearchTaxIDLength);
                    this.isNpiValid = true;
                    this.isTaxValid = false;
                }
                else {
                    this.updateFieldValidation(npiField, this.labels.HumProviderSearchNPILength);
                    this.updateFieldValidation(taxField, this.labels.HumProviderSearchTaxIDLength);
                    this.isNpiValid = false;
                    this.isTaxValid = false;
                }
            }
            else {
                // only one have values
                if (this.uanpiID) {
                    this.updateFieldValidation(taxField, '');
                    this.isTaxValid = true;
                    if (this.uanpiID.length == 10) {
                        this.updateFieldValidation(npiField, '');
                        this.isNpiValid = true;
                    }else if(this.uanpiID.length <= 10 && this.uanpiID.length > 0){
                        this.updateFieldValidation(npiField, this.labels.HumProviderSearchNPILength);
                        this.isNpiValid = false;
                    }
                    else {
                        this.updateFieldValidation(npiField, 'Enter NPI');
                        this.isNpiValid = false;
                    }
                }
                if (this.uataxID) {
                    this.updateFieldValidation(npiField, '');
                    this.isNpiValid = true;
                    if (this.uataxID.length == 9) {
                        this.updateFieldValidation(taxField, '');
                        this.isTaxValid = true;
                    }else if(this.uataxID.length <= 10 && this.uataxID.length > 0){
                        this.updateFieldValidation(taxField, this.labels.HumProviderSearchTaxIDLength);
                        this.isNpiValid = false;
                    }
                    else {
                        this.updateFieldValidation(taxField, 'Enter Tax ID');
                        this.isTaxValid = false;
                    }
                }
            }
        }
        else if (!this.uanpiID && !this.uataxID) {
            this.updateFieldValidation(npiField, 'Enter NPI');
            this.updateFieldValidation(taxField, 'Enter Tax ID');
            this.isNpiValid = false;
            this.isTaxValid = false;
        }
    }

    validateAll(event) {

        if (event.target.name === 'firstName') {
            if (this.uafirstName) {
                this.updateFieldValidation(event.target, '');
            }
            else {
                this.updateFieldValidation(event.target, this.labels.Hum_FirstNameError);
            }
        } else if (event.target.name === 'lastName') {
            if (this.ualastName) {
                if (this.ualastName.length < 2) {
                    this.updateFieldValidation(event.target, this.labels.CASE_PROVIDER_LASTNAMELENGTH_ERROR);
                }
                else {
                    this.updateFieldValidation(event.target, '');
                }
            }
            else {
                this.updateFieldValidation(event.target, this.labels.Hum_LastNameError);
            }
        }
        
        else if (event.target.name == 'workEmail') {
            if (this.uaworkEmail) {
                const reEmail = new RegExp(/^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/);
                let boolReg = reEmail.test(this.uaworkEmail);
                if (!boolReg) {
                    this.updateFieldValidation(event.target, 'Invalid Email Adress');
                }
                else if (boolReg) {
                    this.updateFieldValidation(event.target, '');
                }
            }
            else {
                this.updateFieldValidation(event.target, '');
            }


        }
            else if (event.target.name == "accountName" && !this.disableAccountNameField ) {
                    if (!event.target.value) {
                        this.updateFieldValidation(event.target, this.labels.Hum_AccountNameError);
                    }
                    else if (event.target.value) {
                        this.updateFieldValidation(event.target, '');
                    }
                }
        else if (event.target.name == 'phone') {
            let phone = this.getFormatedPhoneNum(this.uaphone);
            if (phone && phone.length != 10) {
                this.updateFieldValidation(event.target, "Phone Numbers must be 10 digits");
            }
            else {
                this.updateFieldValidation(event.target, '');
            }
        }
    }

    formatPhoneNumber(phNumber) {
        if (phNumber.length === 1) {
            return '(' + phNumber;
        } else if (phNumber.match(/^\(+(\d{3})$/)) {
            return phNumber + ') ';
        } else if (phNumber.match(/^\(+(\d{3})\)+[ ]?(\d{3})$/)) {
            return phNumber + '-';
        } else {
            return phNumber;
        }
    }


    /**
   * Format phone number on key up
   * @param {*} event 
   */
    formatPhoneNumber(event) {
        if (event.keyCode === 8 || event.keyCode === 46) {
            return;
        }
        let oldphNumber = event.target.value;
        let phNumber = oldphNumber.replace(/\D/g,'');
        phNumber = phNumber.replace(/[()-]|[ ]/gi, "");
        const onlyNumber = new RegExp(/^\d+$/);
        if (onlyNumber.test(phNumber)) {
            if (phNumber.length < 3) {
                phNumber = '(' + phNumber;
            } else if (phNumber.length < 6) {
                phNumber = '(' + phNumber.substring(0, 3) + ') ' + phNumber.substring(3, 6);
            } else if (phNumber.length > 5) {
                phNumber = '(' + phNumber.substring(0, 3) + ') ' + phNumber.substring(3, 6) + '-' + phNumber.substring(6, 10);
            }
        }

        event.target.value = phNumber;
    }



    getFormatedPhoneNum(strNumber) {
        if (!strNumber) {
            strNumber = '';
        }
        strNumber = strNumber.replace(/-/g, '');
        strNumber = strNumber.replace(/\D/g, '');
        return strNumber;
    }

    //Update the state field with zipcode is entered 
    searchStateCode(zipCodeValue){
        let remText = zipCodeValue.replace(/\s/g, "");
        let length = remText.length;
        if(length >= 5){
          searchStateCodeWS({ zipCode: zipCodeValue }).then(
            (result) => {
                if (result) {
                    this.stateOptions.forEach(conValues => {
                        if (conValues.value === result) {
                            this.searchFormData.uabillingState = conValues.label;
                            this.uabillingState = conValues.label;
                        }
                      }); 
                }
            })
        }
    }
}