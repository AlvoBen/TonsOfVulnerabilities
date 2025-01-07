/*******************************************************************************************************************************
LWC JS Name : unknownAgentFormHum.js
Function    : This JS serves as controller to unknownAgentFormHum.html. 

Modification Log: 
Developer Name           Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
* Saikumar Boga                                       07/13/2021                    initial version for Unknown Agent/Broker Form
* Ankit Avula                                         09/01/2021                    US2365934 populate state field with zipcode input
* Nilanjana Sanyal                                    04/28/2022                    US-3334453 : Show toast message for unknown agent/broaker creation and 
                                                                                                 Fixes for zip code validation
* Bhakti Vispute									03/16/2023						DF-7362																								 																								 																								 
********************************************************************************************************************************/
import { LightningElement, api, track } from 'lwc';
import getStateValues from '@salesforce/apex/SearchUtilty_H_HUM.getStateValues';
import { getLabels, toastMsge, isSpecialCharsExists, hcConstants } from "c/crmUtilityHum";
import createAccountConsumerRecrd from '@salesforce/apex/UnknownProvider_LC_HUM.createAccountConsumerRecord';
import Assets from '@salesforce/resourceUrl/CustomHelpText';
import { loadStyle } from 'lightning/platformResourceLoader';
import searchStateCodeWS from '@salesforce/apex/StateCodeUtility_WS_HUM.searchStateCode';


export default class unknownAgentFormHum extends LightningElement {

    @api modalOpen = false;
    showErrorMsg = false;
    uapcvalue = 'Agency';
    uaaccountName = '';
    uaccname;
    uafirstName = '';
    ualastName = '';
    uataxID = '';
    uaagentID = '';
    uabillingStreet = '';
    uabillingPostalCode = '';
    uabillingCity = '';
    uabillingState = '-None-';
    uaphone = '';
    uaworkEmail = '';


    @track stateOptions = [];
    @track labels = getLabels();
    disableNameField = false;
    disableAccountNameField = false;
    accountId;
    boolIsOnceSubmit = false;
   



    // Share the search values
    @track searchFormData = {
        sTaxID: this.uataxID,
        sAgentId: this.uaagentID,
        sAgencyName: this.uaaccountName,
        sFirstName: this.uafirstName,
        sLastName: this.ualastName,
        sState: this.uabillingState,
        sAgentType: this.uapcvalue,
        isUnknownAgencySearch: this.isUnknownAgencySearch,
    };
   


    sTaxID = '';
    sAgencyName = '';
    sAgentId = '';
    sFirstName = '';
    sLastName = '';
    sState = '';
    sAgentType = '';
    hasRendered = true;

    renderedCallback() {
        Promise.all([
            loadStyle(this, Assets)
        ])
		if(this.modalOpen && this.hasRendered){
            let fnameInputEle = this.template?.querySelector('lightning-input[data-id="firstName"]');
            let lnameInputEle = this.template?.querySelector('lightning-input[data-id="lastName"]');
            let accIdEle = this.template?.querySelector('lightning-input[data-id="accountName"]');
            fnameInputEle.required = this.uapcvalue === 'Agency' ? false:true;
            lnameInputEle.required = this.uapcvalue === 'Agency' ? false:true;
            accIdEle.required = this.uapcvalue === 'Agency' ? true:false;
        }
    }


    @api handleValueChange(popFormData) {
        this.modalOpen = true;
        this.uataxID = '';
        console.log('popFormData' + JSON.stringify(popFormData));
        // Get the formData from Search and 	
        if (popFormData) {
            console.log('**');
            this.uataxID = popFormData.sTaxID
            this.uaagentID = popFormData.sAgentId
            this.uapcvalue = popFormData.sAgentType
            this.uabillingState = popFormData.sState ? popFormData.sState : '-None-';
            if (this.uapcvalue === 'Agency') {
                this.disableNameField = true;
                this.disableAccountNameField = false;
				this.uaaccountName =  popFormData.sFirstName;
				this.uaccname = true;
                this.uafirstName = '';
                this.ualastName = '';
            }
            else if (this.uapcvalue === 'All'){
                this.disableNameField = false;
                this.disableAccountNameField = true;
                this.uapcvalue = 'Broker'
				this.uafirstName = popFormData.sFirstName;
                this.ualastName =  popFormData.sLastName;
				this.uaccname = false;
                this.uaaccountName = '';
            }
        }
    }


  

    clearStateHandler(event) {
        this.uabillingState = event.detail.value;
        if (this.boolIsOnceSubmit) {
            this.template.querySelector(".statelookup").showValidationError();
        }
    }

    get pcoptions() {
        return [
            { label: 'Agency', value: 'Agency' },
            { label: 'Broker', value: 'Broker' }
        ];
    }


    connectedCallback() {

        this.disableNameField = this.uapcvalue === 'Agency' ? true : false;

       
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
    

    handlePCChange(event) {
        const meCmp = this;
		let fnameInputEle = this.template?.querySelector('lightning-input[data-id="firstName"]');
        let lnameInputEle = this.template?.querySelector('lightning-input[data-id="lastName"]');
        let accIdEle = this.template?.querySelector('lightning-input[data-id="accountName"]');
        fnameInputEle.required = event?.detail?.value === 'Agency' ? false:true;
        lnameInputEle.required = event?.detail?.value === 'Agency' ? false:true;
        accIdEle.required = event?.detail?.value === 'Agency' ? true:false;
		 
        const inputFields = this.template.querySelectorAll(".inputfield");
        this.template.querySelector(".statelookup").removeValidationError();
        inputFields.forEach(function (field) {
            meCmp.updateFieldValidation(field, '');
        });
        this.uapcvalue = event.detail.value;
        if (event.detail.value === 'Agency') {
            this.disableNameField = event.detail.value === 'Agency' ? true : false;
            this.disableAccountNameField = false;
            this.uafirstName = '';
            this.ualastName = '';
        }
        if (event.detail.value === 'Broker') {
            this.disableAccountNameField = event.detail.value === 'Broker' ? true : false;
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
        } else if (event.target.name === 'agentID') {
            this.uaagentID = event.target.value;
        } else if (event.target.name === 'billingStreet') {
            this.uabillingStreet = event.target.value;
        } else if (event.target.name === 'billingPostalCode') {
            this.uabillingPostalCode = event.target.value;
			if(this.uabillingPostalCode && ((this.uabillingPostalCode.length >0 && this.uabillingPostalCode.length < 5) || !this.isNumeric(this.uabillingPostalCode)))
            {   
                
                this.updateFieldValidation(this.template.querySelector('.billingPostal') , this.labels.memberSearchZipEnterHum);
            }
            else 
            {
               this.updateFieldValidation(this.template.querySelector('.billingPostal') , '');
            }
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

    }
	isNumeric(sVal){
        let isNumber = [...sVal].every(i=>'0123456789'.includes(i));       
        return isNumber;
    }

    verifyValueWithRegEx(regEx, field, errorMessage) {
        if (!regEx.test(field.value)) {
            console.log(JSON.stringify('1' + field));
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
        console.log(JSON.stringify(field));
        field.setCustomValidity(message);
        field.reportValidity();
    }


    handleSaveClick(event) {
        this.boolIsOnceSubmit = true;
        const me = this;
        me.showErrorMsg = false;
        let isFormInvalid = false;
        const inpFields = this.template.querySelectorAll(".inputfield");
        const reAgent = new RegExp(/^\d+$/);
        const reTaxId = new RegExp(/\w{9}/);
		const reFirstname = new RegExp(/^[a-zA-Z0-9-.\' , &]*$/);
        const reLastname = new RegExp(/^[a-zA-Z0-9-.' ,]*$/);
        const fields = { 'sobjectType': 'Account' };
        let isSaveForm = false
        if (!this.uaccname) {
            fields['Individual_First_Name__c'] = this.uafirstName;
            fields['Individual_Last_Name__c'] = this.ualastName;
            fields['Name'] = this.uafirstName + ' ' + this.ualastName;
        }
        fields['Agent_Type__c'] = this.uapcvalue;
        if (this.uaccname) {
            fields['Name'] = this.uaaccountName;
        }
        console.log("==Input Fields== ", inpFields);

        /*Billing state validation*/
        let isBillingStateInvalid = this.template.querySelector(".statelookup").showValidationError();

        inpFields.forEach(function (field) {

            me.updateFieldValidation(field, '');
            if (fields['Agent_Type__c'] == 'Broker') {
                if (field.name == "firstName") {
                    if (!field.value) {
                        isFormInvalid = true;
                        me.updateFieldValidation(field, me.labels.Hum_FirstNameError);
                    }
					else if (field.value) {
                        if (me.verifyValueWithRegEx(reFirstname, field, me.labels.HumSearchFirstNameAlphaNumericValidation)) {
                            isFormInvalid = true;
                            return;
                        } 
                    }
                }

                if (field.name == "lastName") {
                    if (!field.value) {
                        isFormInvalid = true;
                        me.updateFieldValidation(field, me.labels.Hum_LastNameError);
                    }
                    else if (field.value && field.value.length < 2) {
                        isFormInvalid = true;
                        me.updateFieldValidation(field, me.labels.HumSearchLastNameCharacterValidation);
                    }
					 else if (field.value)
                    {
                        if (me.verifyValueWithRegEx(reLastname, field, me.labels.HumSearchLastNameAlphaNumericValidation)) {
                            isFormInvalid = true;
                             
                    }
                }
                }
            } else {
                if (field.name == "accountName") {
                    if (!field.value) {
                        isFormInvalid = true;
                        me.updateFieldValidation(field, me.labels.Hum_AccountNameError);
                    }
					else if (field.value)
                    {
                        if (me.verifyValueWithRegEx(reFirstname, field, me.labels.HumSearchAgencyNameValidation)) {
                            isFormInvalid = true;
                            return;  
                    }
                }
                }
            }
            
                
            if (field.name == "taxID") { /**Validacion de tax id*/
                if (field.value) {
                    if (me.verifyValueWithRegEx(reTaxId, field, me.labels.HumProviderSearchTaxIDLength)) {
                        isFormInvalid = true;
                        return;
                    } 
                   
                }
            } 
            if (field.name == "agentID") { /**Validacion de Agent ID */
                if (field.value) {
                    if (me.verifyValueWithRegEx(reAgent, field, me.labels.HumUnknownAgentValidation)) {
                        isFormInvalid = true;
                        return;
                    } 
                   
                }
            } 
            
            if (field.name == "billingPostalCode") { /**Validacion  ZipCode*/
                if (field.value) {
                    if (me.verifyValueWithRegEx(reAgent, field, me.labels.HumZipcodeCharacterLimit)) {
                        isFormInvalid = true;
                       
                        
                    } 
                   
                }
                if (field.value && field.value.length < 5) {
                    
                    isFormInvalid = true;
                   me.updateFieldValidation(field, me.labels.HumZipcodeCharacterLimit);
                   
                }
            }  
            


        });

        
       
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
            else if (field.name == 'agentID') {
                validateFinal.agentID = field.reportValidity();
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
                        me.updateFieldValidation(field, me.labels.HumUnknownAgentEmailValidation);
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
                    me.updateFieldValidation(field, me.labels.HumPhoneCharacterLimit);
                    validateFinal.phone = false;
                }
                else {
                    me.updateFieldValidation(field, '');
                    validateFinal.phone = true;
                }
            }
			 else if (field.name == 'billingPostalCode') {
                validateFinal.billingPostalCode = field.reportValidity();
				 
               
            }
        });
        isFormInvalid = true;
      

        if (this.uapcvalue == 'Agency') {
           if ((validateFinal.firstName && validateFinal.lastName && validateFinal.phone && validateFinal.workEmail && !isBillingStateInvalid && validateFinal.accountName && validateFinal.billingPostalCode) && ((validateFinal.taxID && validateFinal.agentID))) {
                isFormInvalid = false;
            }
        }
        else {
            if ((validateFinal.firstName && validateFinal.lastName && validateFinal.phone && validateFinal.workEmail && !isBillingStateInvalid && validateFinal.accountName && validateFinal.billingPostalCode) && ((validateFinal.taxID && validateFinal.agentID))) {
                isFormInvalid = false;
            }
        }

        if (isFormInvalid) {
            return;
        }


        fields['Agent_ID__c'] = this.uaagentID;
        fields['BillingCity'] = this.uabillingCity;
        fields['BillingStateCode'] = this.uabillingState;
        fields['BillingStreet'] = this.uabillingStreet;
        fields['BillingPostalCode'] = this.uabillingPostalCode;
        fields['Phone'] = this.getFormatedPhoneNum(this.uaphone);
        fields['Work_Email__c'] = this.uaworkEmail;
        fields['HealthCloudGA__TaxId__c'] = this.uataxID;


        if (!isFormInvalid) {


            if (this.uaagentID) {
                this.searchFormData['sTaxID'] = '';
                this.searchFormData['sAgentId'] = this.uaagentID;
                this.searchFormData['sAgencyName'] = '';
                this.searchFormData['sFirstName'] = '';
                this.searchFormData['sLastName'] = '';
                this.searchFormData['sState'] = '';
                this.searchFormData['sAgentType'] = this.uapcvalue;
                this.searchFormData.isUnknownAgencySearch = true;
            }
           if (this.uataxID) {
                this.searchFormData['sTaxID'] = this.uataxID;
                this.searchFormData['sAgentId'] = '';
                this.searchFormData['sAgencyName'] = '';
                this.searchFormData['sFirstName'] = '';
                this.searchFormData['sLastName'] = '';
                this.searchFormData['sState'] = '';
                this.searchFormData['sAgentType'] = this.uapcvalue;
                this.searchFormData.isUnknownAgencySearch = true;
            }
           if(fields['Name'] && !this.uataxID && !this.uaagentID){
            this.searchFormData['sTaxID'] = '';
            this.searchFormData['sAgentId'] = '';
            this.searchFormData['sAgencyName'] = this.uaaccountName;
            this.searchFormData['sFirstName'] = this.uafirstName;
            this.searchFormData['sLastName'] = this.ualastName;
            this.searchFormData['sState'] = this.uabillingState;
            this.searchFormData['sAgentType'] = this.uapcvalue;
            this.searchFormData.isUnknownAgencySearch = true;

           }


            /* Create Method For Unknown Agent/Broker Form */

            createAccountConsumerRecrd({ newRecord: fields })

                .then(result => {
                    if (result) {
                        console.log('--result--' +result);

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
		this.closeModal();
        toastMsge('Success!', 'Account created successfully', 'Success','');
    }


    clearAccountdata() {
        this.disableAccountNameField = false;
        this.showErrorMsg = false;
        this.disableNameField = this.uapcvalue === 'Agency' ? true : false;
        this.uapcvalue = 'Agency';
        this.uaaccountName = '';
        this.uafirstName = '';
        this.ualastName = '';
        this.uataxID = '';
        this.uaagentID = '';
        this.uabillingStreet = '';
        this.uabillingPostalCode = '';
        this.uabillingCity = '';
        this.uabillingState = '-None-';
        this.uaphone = '';
        this.uaworkEmail = '';
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
                    this.updateFieldValidation(event.target, this.labels.HumSearchLastNameCharacterValidation);
                }
                else {
                    this.updateFieldValidation(event.target, '');
                }
            }
            else {
                this.updateFieldValidation(event.target, this.labels.Hum_LastNameError);
            }
        }
        else if (event.target.name == 'agentID') {
            if (this.uaagentID) {
                const reAgent = new RegExp(/^\d+$/);
                let boolReg = reAgent.test(this.uaagentID);
                if (!boolReg) {
                    this.updateFieldValidation(event.target, this.labels.HumUnknownAgentValidation);
                }
                else if (boolReg) {
                    this.updateFieldValidation(event.target, '');
                }
            }
            else {
                this.updateFieldValidation(event.target, '');
            }
        }
        else if (event.target.name == 'taxID') {
            if (this.uataxID) {
                const reTaxId = new RegExp(/\w{9}/);
                let boolReg = reTaxId.test(this.uataxID);
                if (!boolReg) {
                    this.updateFieldValidation(event.target, this.labels.HumProviderSearchTaxIDLength);
                }
                else if (boolReg) {
                    this.updateFieldValidation(event.target, '');
                }
            }
            else {
                this.updateFieldValidation(event.target, '');
            }
        }

        else if (event.target.name == 'workEmail') {
            if (this.uaworkEmail) {
                const reEmail = new RegExp(/^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/);
                let boolReg = reEmail.test(this.uaworkEmail);
                if (!boolReg) {
                    this.updateFieldValidation(event.target, this.labels.HumUnknownAgentEmailValidation);
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
                this.updateFieldValidation(event.target, this.labels.HumPhoneCharacterLimit);
            }
            else {
                this.updateFieldValidation(event.target, '');
            }
        }
		 else if(event.target.name === 'billingPostalCode')
        {
            if(this.uabillingPostalCode && this.uabillingPostalCode.length >0 && this.uabillingPostalCode.length < 5 )
            {   
                this.updateFieldValidation(this.template.querySelector('.billingPostal') , this.labels.memberSearchZipEnterHum);
            }
            else 
            {
               this.updateFieldValidation(this.template.querySelector('.billingPostal') , '');
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

    /*
        searchStateCode will get the state value and populate the state field with the input as the zip code
    */
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