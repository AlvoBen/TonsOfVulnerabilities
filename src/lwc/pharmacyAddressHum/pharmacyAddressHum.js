/*--
File Name        : pharmacyAddressHum.js
Version          : 1.0
Created Date     : 08/20/2021
Function         : Lightning Web Component used to display and edit ePost Addresses
Modification Log :
* Developer                          Date                  Description
*************************************************************************************************
* Kiran Bhuvanagiri                 08/20/2021            User Story 2421627: Original Version
* Himalay Patel                     09/07/2021            DF 3686 - Apply button is clickable before address selection has been made_HP_RXRF
* Kiran Bhuvanagiri                	09/30/2021            REQ - 2507884 T1PRJ0002517 - MF 2 - Case Documentation for Demographic Updates (RXRF) (ID# 35a)
* Ashok Kumar Nutalapati            09/30/2021            REQ - 2593758 T1PRJ0002517 - MF 2 - New case button, create case, & open new case in a subtab (RXRF) (ID# 35b)
* Himalay Patel                     10/15/2021            User Story 2718738: Update Address Selection Screen - Sort Order (RxRF) (ID# 91f)
* Himalay Patel                     10/15/2021            User Story 2690528: Error Message for Inactivating an Address - Status Change Warning (RxRF) (ID# 95)
* Ashok Kumar Nutalapati            11/02/2021            REQ - 2762008 - New Address LWC Mainstream
* Himalay Patel                     11/05/2021            User Story 2761687- Activate/Deactivate Addresses (CRMS) (ID# 91) -- Warning Message Update.
* Kiran Bhuvanagiri                 11/08/2021            User Story - 2810831: Pharmacy Address Comment Enhancement (RXRF)
* Aaron Speakman                    11/24/2021            DF-4229 - Adding DC to State list
* Kiran Bhuvanagiri                 01/04/2022            User Story 2759501: Case Documentation for Address Changes: UI Updates New/ Existing Case  (RxRF)
* Kiran Bhuvanagiri					02/16/2022			  User Story 2935436: Address Component in Lightning - (CRMS)
* Kiran Bhuvanagiri					05/23/2022			  User Story 2882456: Case Documentation for Demographic Updates (CRMS)
* Kalyani Pachpol					06/02/2022			  DF-4995
* Nirmal Garg												06/03/2022						DF-5000 Fix
* Jonathan Dickinson		        09/27/2022		      REQ - 3751914
* Aishwarya Pawar                                 	      03/02/2023                 US 4315305  systematically associate interaction to new/existing cases for the HP logging scenarios
* Nirmal Garg						  07/27/2023	                US4902305
**************************************************************************************************/

import { LightningElement, api, track } from 'lwc';
import getMemberDetails from "@salesforce/apexContinuation/Pharmacy_LC_HUM.invokeGetMemberService";
import updateMemberAddresses from "@salesforce/apexContinuation/Pharmacy_LC_HUM.updateGetMemberAddresses";
import { USPSRecomendationRequest, ePostUpdateAddressRequest } from './pharmacyAddressHum_Helper';
import createNewCase from "@salesforce/apex/Pharmacy_LC_HUM.createPharmacyCaseAndRedirect";
import getInteractionType from "@salesforce/apex/Pharmacy_C_HUM.getInteractionQueryResults";
import US2593758SwitchLabel from '@salesforce/label/c.US2593758SwitchLabel';
import { toastMsge } from 'c/crmUtilityHum';
import { attachInteractionToCase } from 'c/genericCaseActionHum';
import genericCaseActionHum from 'c/genericCaseActionHum';

export default class pharmacyAddressHum extends genericCaseActionHum {

    //Inputs needed for the initial service call
    @api enterpriseId;
    @api networkId;
    @api recordId;
    @api policyId = '';
    @api interactionId = '';
    @api calledfrom;
    //Manages if ePost data is returned or not on inital load
    @track loaded = false;
    @track displayErrorMessage = false;
    @track ePostLoading = false;
    @track statusError = false;
    @track showUSPSSecondError = false;
    //Modify by Himalay for ID91
    @track infoMessage = false;
    @track toggleAddresskey = '';
    @track warningMessage = '';

    //Basic data structures to manage address data and inputs
    @track addresses = {};
    addressMap = new Map();
    selectedAddress = new Map();
    uniqueAddressData = new Map();
    @track setOfAddressType = new Set();
    @track selectedOptions = [];
    @track addressToValidate = '';
    @track selectedAddressStr = '';
    @track trackAddressClickBy = '';

    //Used to keep track of which page should display
    @track bShowAddressSelection = true;
    @track bShowAddressChange = false;
    @track bShowAddAddress = false;
    @track bShowUSPSValidation = false;

    @track bIsEditButton = false;
    @track bShowOtherForm = false;
    @track bShowMappedAddresses = false;
    @track bEditBackClicked = false;
    @track bIsApplyButton = true;
    @track newApplyButton = true;

    //Variables for the address edit form
    @track updateAddressLine1;
    @track updateAddressLine2;
    @track updateAddressCity
    @track updateAddressState;
    @track updateAddressZip;

    //Variables for the add address form
    @track addAddress1;
    @track addAddress2;
    @track addAddressCity
    @track addAddressState;
    @track addAddressZip;
    //Kiran Changes
    @track selectedAddressKey;
    @track interactionType;
    @track newAddressLiteralType;

    selectedAddressId;
    @track addressToUpdate = new USPSRecomendationRequest('', '', '', '', '');

    //Variable for check New Address or Address Change Apply button click
    @track checkAddAddress;
    initialAddress;

    //Variables for Success Message Related
    @track successMessage = false;
    @track addAddressStr = '';
    @track updatedAddressStr = '';

    @track bShowExistingCaseModal = false;
    @track isNewCase = true;
    @track uspsAddressForExistingCase;


    sFullAddressForEachType = '';
    isLightning;

    connectedCallback() {
        if (this.loaded != true) {

            getMemberDetails({
                memID: this.enterpriseId,
                networkId: this.networkId,
                sRecordId: this.recordId
            }).
                then(data => {
                    data = JSON.parse(data);
                    if (data && data != {} && data.objPharDemographicDetails.Address != {}) {
                        this.updateDemographicData(data.objPharDemographicDetails);
                        this.addresses = this.prepareAddressData(data.objPharDemographicDetails.Address);
                        this.displayErrorMessage = false;
                        this.loaded = true;
                        for (let address of this.addresses) {
                            this.addressMap.set(address.UniqueKey, address);
                            if (!this.uniqueAddressData.has(address.AddressKey)) {
                                let tempStr = address.AddressLine1;
                                if (address.AddressLine1 != '' && address.AddressLine1 != null) {
                                    tempStr += ' ' + address.AddressLine2;
                                }
                                tempStr += ', ' + address.City + ' ' + address.StateCode + ' ' + address.ZipCode;
                                this.uniqueAddressData.set(address.AddressKey, tempStr);
                            }
                        }
                        if (this.interactionId != '' && this.interactionId != null && this.interactionId != undefined) {
                            this.getInterActionValue();
                        }

                    } else {
                        this.displayErrorMessage = true;
                        this.loaded = true;
                    }
                }).catch(e => {
                    this.displayErrorMessage = true;
                    this.loaded = true;
                    console.log('Error : ' + JSON.stringify(e));
                })
        }
        this.isLightning = this.calledfrom && this.calledfrom.toUpperCase() === 'LIGHTNING' ? true : false;
    }

    getInterActionValue() {
        getInteractionType({
            interActionRecordId: JSON.stringify(this.interactionId)
        }).then(interActionTypeValue => {
            this.interactionType = interActionTypeValue;
        }).catch(error => {
            console.log('Error');
        });
    }

    renderedCallback() {
        if (this.bShowAddressSelection && this.selectedAddress.size > 0 && this.bEditBackClicked) {
            for (let key of this.selectedAddress.keys()) {
                let target = this.template.querySelector(`[data-checkboxid="${key}"]`);
                target.checked = true;
            }
            this.bEditBackClicked = false;
        }
    }

    //Process address data from ePost to add additional fields.
    prepareAddressData(inputData) {
        for (let inputAddress of inputData) {
            if (inputAddress.IsActive == 'true') {
                inputAddress.Status = true;
            } else {
                inputAddress.Status = false;
            }
            inputAddress.UniqueKey = inputAddress.AddressType + '_' + inputAddress.AddressKey;

            let tempStr = inputAddress.AddressLine1;
            if (inputAddress.AddressLine2 != '' && inputAddress.AddressLine2 != null) {
                tempStr += ' ' + inputAddress.AddressLine2;
            }
            inputAddress.Street = tempStr;

            var type = inputAddress.AddressType.toUpperCase();
            if (type != 'A') {
                inputAddress.ShowToggle = false;
            } else {
                inputAddress.ShowToggle = true;
            }
        }
        var tempsortAddressData = this.sortAddressData(inputData);
        return this.sortAddressData2(tempsortAddressData);
    }

    //Sorts address data by address type Z-A
    sortAddressData(inputData) {
        inputData.sort(function (a, b) {
            var type1 = a.AddressType.toUpperCase();
            var type2 = b.AddressType.toUpperCase();
            if (type1 > type2) {
                return -1;
            }
            if (type1 < type2) {
                return 1;
            }
            // names must be equal
            return 0;
        });
        return inputData;
    }
    //Custom Sorts address data by address type A-Z
    sortAddressData2(inputData) {
        inputData.sort(function (a, b) {
            var type1 = a.AddressType.toUpperCase();
            var type2 = b.AddressType.toUpperCase();
            if (type1 == 'P' && type2 == 'B') {
                return 1;
            }
            if (type1 == 'B' && type2 == 'P') {
                return -1;
            }
            if (type1 > type2) {
                return -1;
            }
            if (type1 < type2) {
                return 1;
            }
            var status1 = a.Status;
            var status2 = b.Status;

            if (status1 && status1 != status2) {
                return -1;
            }
            if (status2 && status1 != status2) {
                return 1;
            }
            var city1 = a.City.toUpperCase();
            var city2 = b.City.toUpperCase();
            if (city1 > city2) {
                return 1;
            }
            if (city1 < city2) {
                return -1;
            }
            return 0;
        });
        return inputData;
    }

    saveToEpost(request, calledFrom, elementID, callExistingCase, callNewCase) {
        this.ePostLoading = true;
        let sInitialAddress = '';
        let sAddressType = '';
        var hasInteraction = false;

        updateMemberAddresses({
            enterprise: this.enterpriseId,
            networkId: this.networkId,
            sRecordId: this.recordId,
            addressRequestJSON: request
        }).then(data => {
            data = JSON.parse(data);
            if (data && data != {} && data.Error != '' && (data.Fault?.faultcode === undefined || data.Fault?.faultcode == '')) {
                if (calledFrom == 'handleUSPSSaveAddresses') {
                    if (US2593758SwitchLabel.toUpperCase() === 'Y') {
                        for (let [key, address] of this.selectedAddress.entries()) {
                            sInitialAddress = address.AddressLine1;
                            if (address.AddressLine2 != '' && address.AddressLine2 != null) {
                                sInitialAddress += ' ' + address.AddressLine2;
                            }
                            sInitialAddress += ', ' + address.City + ' ' + address.StateCode + ' ' + address.ZipCode;
                            sAddressType += address.AddressTypeLiteral + ', ';
                            address.fullAddress = sInitialAddress;
                        }
                        if (this.interactionId != undefined && this.interactionId != '')
                            hasInteraction = true;

                        //Case Comment
                        sAddressType = sAddressType.substring(0, sAddressType.length - 2)

                        if (this.bIsEditButton) {
                            this.sFullAddressForEachType = '';
                            if (hasInteraction) {
                                if (this.selectedOptions.length < 2) {
                                    this.initialAddress = this.interactionType + ' contacted us to Update ' + sAddressType + ' Address from ' + sInitialAddress + ' to ';
                                } else {
                                    this.initialAddress = this.interactionType + ' contacted us to Update ' + sAddressType + '\n' + 'from: ';
                                    for (let [key, address] of this.selectedAddress.entries()) { //For ID 35d
                                        if (address.AddressType != 'A') {
                                            this.sFullAddressForEachType += address.AddressTypeLiteral + ', ' + address.fullAddress + '\n';
                                        }
                                    }
                                    this.initialAddress += this.sFullAddressForEachType + 'to ';
                                }
                            } else {
                                if (this.selectedOptions.length < 2) {
                                    this.initialAddress = 'We were contacted to Update ' + sAddressType + ' Address from ' + sInitialAddress + ' to ';
                                } else {
                                    this.initialAddress = 'We were contacted to Update ' + sAddressType + '\n' + 'from: ';
                                    for (let [key, address] of this.selectedAddress.entries()) { //For ID 35d
                                        if (address.AddressType != 'A') {
                                            this.sFullAddressForEachType += address.AddressTypeLiteral + ', ' + address.fullAddress + '\n';
                                        }
                                    }
                                    this.initialAddress += this.sFullAddressForEachType + 'to ';
                                }
                            }
                        }
                        let tempStr = this.addressToUpdate.AddressLine1;
                        if (this.addressToUpdate.AddressLine2 != '' && this.addressToUpdate.AddressLine2 != null && this.addressToUpdate.AddressLine2 != undefined) {
                            tempStr += ' ' + this.addressToUpdate.AddressLine2;
                        }
                        tempStr += ', ' + this.addressToUpdate.City + ' ' + this.addressToUpdate.StateCode + ' ' + this.addressToUpdate.ZipCode;
                        this.updatedAddressStr = tempStr;
                        sAddressType = this.addressToUpdate.AddressTypeLiteral;
                        if (this.bIsEditButton) {
                            this.initialAddress += this.updatedAddressStr;
                        } else {
                            if (hasInteraction) {
                                this.initialAddress = this.interactionType + ' contacted us to add ' + this.newAddressLiteralType + ' address as ' + this.updatedAddressStr;
                            } else {
                                this.initialAddress = 'We were contacted to add ' + this.newAddressLiteralType + ' address as ' + this.updatedAddressStr;
                            }
                        }
                        if (this.isLightning) {
                            const uspsValidationComponent = this.template.querySelector('c-usps-validation-hum');
                            if (uspsValidationComponent) {
                                uspsValidationComponent.createAddressObject(this.initialAddress);
                            }
                        }
                    }
                    this.selectedAddress = new Map();
                    this.selectedOptions = [];
                    this.addAddress2 = '';
                    this.addAddressType = '';
                    this.addAddress1 = '';
                    this.addAddressCity = '';
                    this.addAddressState = '';
                    this.addAddressZip = '';
                    this.addressToValidate = '';
                    this.selectedAddressStr = '';
                    this.selectedAddressKey = '';
                    this.bShowOtherForm = false;
                    this.bShowMappedAddresses = false;
                    this.goToPage('addressSelection');
                }
                this.refreshAddresses(calledFrom);
                if (callExistingCase == true) {
                    this.bShowExistingCaseModal = false;
                    this.displaySuccessNotification();
                    this.attachInteraction(this.existingcaseid);
                    var caseDetails = { id: this.existingcaseid, number: this.existingcasenumber, comment: this.initialAddress };
                    this.dispatchEvent(new CustomEvent(
                        'openExistingCaseFromAddress',
                        {
                            detail: caseDetails,
                            bubbles: true,
                            composed: true,
                        }
                    ));
                }
                if (callNewCase == true) {
                    this.disableNewCase = true;
                    if (!this.isLightning) {
                        createNewCase({
                            sInteractionId: this.interactionId,
                            sObjectId: this.policyId,
                            sAccountId: this.recordId

                        }).then(result => {
                            var sCaseRecordId = result.substring(0, 18);
                            var sCaseNumber = result.substring(19, 32);
                            var caseDetails = { id: sCaseRecordId, number: sCaseNumber, comment: this.initialAddress };
                            this.dispatchEvent(new CustomEvent(
                                'openNewCaseFromAddress',
                                {
                                    detail: caseDetails,
                                    bubbles: true,
                                    composed: true,
                                }
                            ));
                            this.disableNewCase = false;
                        }).catch(error => {
                            console.log('Error Occured', error);
                            this.disableNewCase = false;
                        });
                    }
                }
            } else {
                this.ePostLoading = false;
                if (calledFrom == 'handleUSPSSaveAddresses') {
                    this.showUSPSSecondError = true;
                } else {
                    this.statusError = true;
                    this.undoSwitchToggle(elementID);
                }
            }
        }).catch(e => {
            this.ePostLoading = false;
            if (calledFrom == 'handleUSPSSaveAddresses') {
                this.showUSPSSecondError = true;
            }
            else {
                this.statusError = true;
                this.undoSwitchToggle(elementID);
            }
            console.log('Error : ' + JSON.stringify(e));
        })
    }
    attachInteraction(caseID) {
        let attachInteractionResponse;
        attachInteractionToCase(caseID).then(result => {
            if (result) {
                attachInteractionResponse = result;
            }
        }).catch(error => {
            console.log('Error Occured in attachInteraction', error);
        });
    }
    displaySuccessNotification() {
        if (this.isLightning) {
            toastMsge('Success!', 'Address has successfully changed for this Member', 'success', 'sticky');
        } else {
            this.successMessage = true;
        }
    }

    undoSwitchToggle(elementID) {
        let target = this.template.querySelector(`[data-id="${elementID}"]`);
        if (target.checked) {
            target.checked = false;
        } else {
            target.checked = true;
        }
    }

    refreshAddresses(calledFrom) {
        getMemberDetails({
            memID: this.enterpriseId,
            networkId: this.networkId,
            sRecordId: this.recordId
        }).
            then(data => {
                this.ePostLoading = false;
                data = JSON.parse(data);
                if (data && data != {} && data.objPharDemographicDetails.Address != {}) {
                    this.updateDemographicData(data.objPharDemographicDetails);
                    this.addresses = this.prepareAddressData(data.objPharDemographicDetails.Address);
                    for (let address of this.addresses) {
                        this.addressMap.set(address.UniqueKey, address);
                        if (!this.uniqueAddressData.has(address.AddressKey)) {
                            let tempStr = address.AddressLine1;
                            if (address.AddressLine2 != '' && address.AddressLine2 != null) {
                                tempStr += ' ' + address.AddressLine2;
                            }
                            tempStr += ', ' + address.City + ' ' + address.StateCode + ' ' + address.ZipCode;
                            this.uniqueAddressData.set(address.AddressKey, tempStr);
                            this.addAddressStr = tempStr;
                        }
                    }
                    this.bIsEditButton = false;

                    if (US2593758SwitchLabel.toUpperCase() === 'Y' && calledFrom == 'handleUSPSSaveAddresses' && this.isNewCase == true) {
                        this.displaySuccessNotification();
                    }
                } else {
                    this.statusError = true;
                }
            }).catch(e => {
                this.ePostLoading = false;
                this.statusError = true;
                console.log('Error : ' + JSON.stringify(e));
            })
    }

    updateDemographicData(data) {
        if (this.calledfrom && this.calledfrom === 'Lightning') {
            this.dispatchEvent(new CustomEvent(
                'updateaddress',
                {
                    detail: data,
                    bubbles: true,
                    composed: true,
                }
            ));
        }
        this.dispatchEvent(new CustomEvent(
            'updateDemographics',
            {
                detail: data,
                bubbles: true,
                composed: true,
            }
        ));
    }

    handleShowButton(event) {
        var eventKey = event.target.dataset.checkboxid;
        var tempKey = '';
        var eventAddresstype = event.target.dataset.addresstype;

        if (event.target.checked == true) {
            for (let selectedKey of this.selectedAddress.keys()) {
                if (selectedKey.charAt(0) === eventAddresstype) {
                    tempKey = selectedKey;
                }
            }

            if (tempKey != '') {
                let target = this.template.querySelector(`[data-checkboxid="${tempKey}"]`);
                target.checked = false;
                this.selectedAddress.delete(tempKey);
            }

            this.selectedAddress.set(eventKey, this.addressMap.get(eventKey));
        } else {
            if (this.selectedAddress.has(eventKey)) {
                this.selectedAddress.delete(eventKey);
            }
        }

        if (this.selectedAddress.size > 0) {
            var setOfType = new Set();
            this.selectedAddress.forEach(data => {
                setOfType.add(data.AddressTypeLiteral);
            });
            this.setOfAddressType = setOfType;
            this.bIsEditButton = true;
        } else {
            this.bIsEditButton = false;
        }
    }

    handleAddressChange(event) {
        this.addressToValidate = event.detail.value;
        if (event.detail.value == 'Add New Address') {
            this.updateAddressLine1 = '';
            this.updateAddressLine2 = '';
            this.updateAddressCity = '';
            this.updateAddressState = '';
            this.updateAddressZip = '';
            this.bShowOtherForm = true;
            this.bShowMappedAddresses = false;
            this.bIsApplyButton = true;
            this.trackAddressClickBy = 'AddAddress';
        } else {
            this.bShowOtherForm = false;
            this.bShowMappedAddresses = true;
            this.selectedAddressKey = event.detail.value;
            this.selectedAddressStr = this.uniqueAddressData.get(event.detail.value);
            this.bIsApplyButton = false;
        }
    }

    displayMappedAddressUpdates(addressKey) {
        this.bShowMappedAddresses = true;
        this.selectedAddressStr = this.addressToValidate;
    }

    addAddressClick() {
        this.goToPage('addAddress');
    }

    editAddressClick() {
        this.selectedOptions = [];

        for (let [key, address] of this.selectedAddress.entries()) {
            let temp_Obj = {
                label: address.AddressTypeLiteral,
                name: key
            }
            this.selectedOptions.push(temp_Obj);
        }

        this.goToPage('editAddress');
    }

    handleRemove(event) {
        const name = event.target.name;
        this.selectedOptions = this.selectedOptions.filter(item => item.name !== name);
        if (this.selectedOptions.length == 0) {
            this.handleCancel();
        } else if (this.selectedAddress.has(name)) {
            this.selectedAddress.delete(name);
        }
    }

    handleCancel() {
        this.selectedOptions = [];
        this.addAddressType = '';
        this.addAddress1 = '';
        this.addAddress2 = '';
        this.addAddressCity = '';
        this.addAddressState = '';
        this.addAddressZip = '';
        this.selectedAddress = new Map();
        this.addressToValidate = '';
        this.checkAddAddress = '';

        this.selectedAddressStr = '';
        this.selectedAddressKey = '';
        this.bShowOtherForm = false;
        this.bIsEditButton = false;
        this.newApplyButton = true;
        this.bShowMappedAddresses = false;
        this.goToPage('addressSelection');

    }

    handleEditBack() {
        this.addressToValidate = '';
        this.selectedAddressStr = '';
        this.selectedAddressKey = '';
        this.bShowOtherForm = false;
        this.bShowMappedAddresses = false;
        this.bEditBackClicked = true;
        this.goToPage('addressSelection');

    }

    handleAddApply() {
        const isInputFilled = [...this.template.querySelectorAll('lightning-input')]
            .reduce((validSoFar, inputField) => {
                inputField.reportValidity();
                return validSoFar && inputField.checkValidity();
            }, true);

        const isDropdownfilled = [...this.template.querySelectorAll('lightning-combobox')]
            .reduce((validSoFar, inputField) => {
                inputField.reportValidity();
                return validSoFar && inputField.checkValidity();
            }, true);


        if (isInputFilled && isDropdownfilled) {
            this.checkAddAddress = 'New Address';
            this.addressToUpdate = new USPSRecomendationRequest(this.addAddress1, this.addAddress2, this.addAddressCity, this.addAddressState, this.addAddressZip);
            this.goToPage('uspsValidation');
        }
    }

    handleEditApply() {
        const isInputFilled = [...this.template.querySelectorAll('lightning-input')]
            .reduce((validSoFar, inputField) => {
                inputField.reportValidity();
                return validSoFar && inputField.checkValidity();
            }, true);

        const isDropdownfilled = [...this.template.querySelectorAll('lightning-combobox')]
            .reduce((validSoFar, inputField) => {
                inputField.reportValidity();
                return validSoFar && inputField.checkValidity();
            }, true);

        if (isInputFilled && isDropdownfilled) {
            this.checkAddAddress = 'Address Change';

            if (this.addressToValidate == 'Add New Address') {
                this.addressToUpdate = new USPSRecomendationRequest(this.updateAddressLine1, this.updateAddressLine2, this.updateAddressCity, this.updateAddressState, this.updateAddressZip);
            } else {
                for (let [key, value] of this.addressMap.entries()) {
                    if (value.AddressKey == this.addressToValidate) {
                        this.addressToUpdate = new USPSRecomendationRequest(value.AddressLine1, value.AddressLine2, value.City, value.StateCode, value.ZipCode);
                        break;
                    }
                }
            }

            this.goToPage('uspsValidation');
        }
    }

    //Used to control which page should display within the component
    goToPage(nameOfPage) {
        switch (String(nameOfPage)) {
            case 'addressSelection':
                this.bShowAddressChange = false;
                this.bShowAddAddress = false;
                this.bShowUSPSValidation = false;
                this.bShowAddressSelection = true;
                break;
            case 'editAddress':
                this.bShowAddressSelection = false;
                this.bShowAddAddress = false;
                this.bShowUSPSValidation = false;
                this.bShowAddressChange = true;
                break;
            case 'addAddress':
                this.bShowAddressSelection = false;
                this.bShowAddressChange = false;
                this.bShowUSPSValidation = false;
                this.bShowAddAddress = true;
                break;
            case 'uspsValidation':
                this.bShowAddressSelection = false;
                this.bShowAddressChange = false;
                this.bShowAddAddress = false;
                this.bShowUSPSValidation = true;
                break;
            default:
                this.bShowAddressChange = false;
                this.bShowAddAddress = false;
                this.bShowUSPSValidation = false;
                this.bShowAddressSelection = true;
        }
    }

    //Handlers for Add Address form
    handleaddAddressType(event) {
        this.addAddressType = event.target.value;

        this.selectedOptions = [];
        let temp_Obj = {
            label: event.target.options.find(opt => opt.value === event.detail.value).label,
            name: this.addAddressType
        }
        this.selectedOptions.push(temp_Obj);
        this.handleApplyButtonEnable();
    }

    handleaddAddress1(event) {
        this.addAddress1 = event.target.value;
        this.handleApplyButtonEnable();
        event.stopPropagation();
    }

    handleaddAddress2(event) {
        this.addAddress2 = event.target.value;
        event.stopPropagation();
    }

    handleaddCity(event) {
        let inputVal = event.target.value;
        event.target.value = inputVal.replace(/[0-9]/g, "");
        this.addAddressCity = event.target.value;
        this.handleApplyButtonEnable();
        event.stopPropagation();
    }

    handleaddState(event) {
        this.addAddressState = event.target.value;
        this.handleApplyButtonEnable();
    }

    handleZipChange(event) {
        let inputVal = event.target.value;
        if (!isFinite(inputVal)) {
            event.target.value = inputVal.toString().slice(0, -1);
        }
        this.addAddressZip = event.target.value;
        this.handleApplyButtonEnable();
    }
    handleApplyButtonEnable() {
        if (this.addAddressState != undefined && this.addAddressState != ''
            && this.addAddressZip != undefined && this.addAddressZip != ''
            && this.addAddressCity != undefined && this.addAddressCity != ''
            && this.addAddress1 != undefined && this.addAddress1 != ''
            && this.addAddressType != undefined && this.addAddressType != '') {
            this.newApplyButton = false;
        } else {
            this.newApplyButton = true;
        }

    }
    handleZipChangeClear(event) {
        event.stopPropagation();
    }

    //Handlers for Edit Page form
    handleEditAddress1(event) {
        this.updateAddressLine1 = event.target.value;
        this.handlebIsApplyButtonEnable();
        event.stopPropagation();
    }

    handleEditAddress2(event) {
        this.updateAddressLine2 = event.target.value;
        event.stopPropagation();
    }

    handleEditCity(event) {
        let inputVal = event.target.value;
        event.target.value = inputVal.replace(/[0-9]/g, "");
        this.updateAddressCity = event.target.value;
        this.handlebIsApplyButtonEnable();
        event.stopPropagation();
    }

    handleEditState(event) {
        this.updateAddressState = event.target.value;
        this.handlebIsApplyButtonEnable();
    }

    handleEditZipChange(event) {
        let inputVal = event.target.value;
        if (!isFinite(inputVal)) {
            event.target.value = inputVal.toString().slice(0, -1);
        }
        this.updateAddressZip = event.target.value;
        this.handlebIsApplyButtonEnable();
    }

    handleEditZip(event) {
        event.stopPropagation();
    }
    handlebIsApplyButtonEnable() {
        if (this.updateAddressState != undefined & this.updateAddressState != ''
            && this.updateAddressZip != undefined && this.updateAddressZip != ''
            && this.updateAddressCity != undefined && this.updateAddressCity != ''
            && this.updateAddressLine1 != undefined && this.updateAddressLine1 != '') {
            //this.bIsApplyButton = false;
        } else {
            this.bIsApplyButton = true;
        }

    }

    handleOtherCancel(event) {
        if (this.selectedAddressStr == '') {
            this.addressToValidate = '';
        }
        this.bShowOtherForm = false;
        if (this.trackAddressClickBy == 'ModifyAddress') {
            this.bIsApplyButton = false;
        } else if (this.trackAddressClickBy == 'AddAddress') {
            this.bIsApplyButton = true;
        }
    }

    handleOtherDone(event) {
        const isInputFilled = [...this.template.querySelectorAll('lightning-input')]
            .reduce((validSoFar, inputField) => {
                inputField.reportValidity();
                return validSoFar && inputField.checkValidity();
            }, true);

        const isDropdownfilled = [...this.template.querySelectorAll('lightning-combobox')]
            .reduce((validSoFar, inputField) => {
                inputField.reportValidity();
                return validSoFar && inputField.checkValidity();
            }, true);

        let otherStr = this.updateAddressLine1;
        if (this.updateAddressLine1 != '' && this.updateAddressLine1 != null) {
            otherStr += ' ' + this.updateAddressLine2;
        }
        otherStr += ', ' + this.updateAddressCity + ' ' + this.updateAddressState + ' ' + this.updateAddressZip;

        if (isInputFilled && isDropdownfilled) {
            this.selectedAddressStr = otherStr;
            this.selectedAddressKey = 'Add New Address';
            this.bShowOtherForm = false;
            this.addressToValidate = 'Add New Address';
            this.bShowMappedAddresses = true;
            this.bIsApplyButton = false;
        }
        this.handlebIsApplyButtonEnable();
    }

    handleModifyAddress(event) {
        this.bShowOtherForm = true;
        this.bIsApplyButton = true; // US 2935436
        this.trackAddressClickBy = 'ModifyAddress';

        if (this.selectedAddressKey != 'Add New Address') {
            for (let [key, value] of this.addressMap.entries()) {
                if (value.AddressKey == this.selectedAddressKey) {
                    this.updateAddressLine1 = value.AddressLine1;
                    this.updateAddressLine2 = value.AddressLine2;
                    this.updateAddressCity = value.City;
                    this.updateAddressState = value.StateCode;
                    this.updateAddressZip = value.ZipCode;
                }
            }
        }
    }

    handleModifyRemoveAddress(event) {
        this.bShowMappedAddresses = false;
        this.bShowOtherForm = false;
        this.addressToValidate = '';
        this.selectedAddressStr = '';
        this.bIsApplyButton = true;
    }

    //Handlers for USPS
    handleUSPSCancel() {
        this.selectedOptions = [];
        this.addAddressType = '';
        this.addAddress1 = '';
        this.addAddress2 = '';
        this.addAddressCity = '';
        this.addAddressState = '';
        this.addAddressZip = '';
        this.selectedAddress = new Map();
        this.addressToValidate = '';
        this.checkAddAddress = '';

        this.selectedAddressStr = '';
        this.selectedAddressKey = '';
        this.bShowOtherForm = false;
        this.bIsEditButton = false;
        this.bShowMappedAddresses = false;
        this.goToPage('addressSelection');
        this.bShowUSPSValidation = false;
    }

    handleUSPSBack() {
        if (this.checkAddAddress == 'New Address') {
            this.goToPage('addAddress');
        } else {
            this.goToPage('editAddress');
        }

    }

    handleStatusChange(event) {
        this.toggleAddresskey = event.target.value;
        if ((event.target.value).includes('A_')) {
            this.warningMessage = 'Would you like to keep the status update made to this member?';
        }
        this.infoMessage = true;
    }

    handleStatusError(event) {
        event.preventDefault();
        if ((event.target.id).includes('errorId')) {
            this.statusError = false;
        }
    }

    handleUSPSSaveAddresses(event) {
        let uspsAddressRequest = new ePostUpdateAddressRequest(this.enterpriseId, this.networkId);
        uspsAddressRequest.EditMemberRequest.members[0].addresses.address = event.detail.address;
        let literalStartChar = event.detail.address[0].addressType;
        if (literalStartChar == 'S') {
            this.newAddressLiteralType = 'Shipping';
        } else if (literalStartChar == 'P') {
            this.newAddressLiteralType = 'Permanent';
        } else if (literalStartChar == 'B') {
            this.newAddressLiteralType = 'Billing';
        } else {
            this.newAddressLiteralType = 'Alternate';
        }
        this.showUSPSSecondError = false;
        this.uspsAddressForExistingCase = uspsAddressRequest;
        if (event.detail.isNewCase == true) {
            this.ePostLoading = true;
            this.saveToEpost(JSON.stringify(uspsAddressRequest), 'handleUSPSSaveAddresses', null, false, true);
        } else {
            this.ePostLoading = false;
            if (!this.isLightning) {
                if (US2593758SwitchLabel.toUpperCase() === 'Y') {
                    this.bShowExistingCaseModal = true;
                }
            } else {
                this.saveToEpost(JSON.stringify(this.uspsAddressForExistingCase), 'handleUSPSSaveAddresses', null, true, false);
            }
        }
    }

    @track existingcaseid;
    @track existingcasenumber;
    handleSelectedExistingCase(event) {
        this.bShowExistingCaseModal = false;
        this.existingcaseid = event.detail.caseid;
        this.existingcasenumber = event.detail.casenumber;
        this.saveToEpost(JSON.stringify(this.uspsAddressForExistingCase), 'handleUSPSSaveAddresses', null, true, false);
    }


    handleRemoveUSPSSelection(event) {
        this.selectedAddress.delete(event.detail);
        this.selectedOptions = this.selectedOptions.filter(item => item.name !== event.detail);
    }

    handleSuccessMessage(event) {
        event.preventDefault();
        if ((event.target.id).includes('successId')) {
            this.successMessage = false;
        }
    }
    handleWarningMessage(event) {
        this.undoSwitchToggle(this.toggleAddresskey);
        this.toggleAddresskey = '';
        this.infoMessage = false;
    }

    handleWarningYes(event) {

        const addresskey = this.toggleAddresskey;
        let addressData;

        if (this.addressMap.has(addresskey)) {
            addressData = this.addressMap.get(addresskey);
            let toggleAddressRequest = new ePostUpdateAddressRequest(this.enterpriseId, this.networkId);
            var addressDetails = toggleAddressRequest.EditMemberRequest.members[0].addresses.address[0];
            addressDetails.addressLine1 = addressData.AddressLine1;
            addressDetails.addressLine2 = addressData.AddressLine2;
            addressDetails.addressType = addressData.AddressType;
            addressDetails.city = addressData.City;
            addressDetails.stateCode = addressData.StateCode;
            addressDetails.zipCode = addressData.ZipCode;
            if (addressData.IsActive == 'true') {
                addressDetails.isActive = 'false';
            } else {
                addressDetails.isActive = 'true';
            }

            this.saveToEpost(JSON.stringify(toggleAddressRequest), 'handleStatusChange', addresskey, false);
        }
        this.toggleAddresskey = '';
        this.infoMessage = false;
    }

    handleNewCase(event) {
        createNewCase({
            sInteractionId: this.interactionId,
            sObjectId: this.policyId,
            sAccountId: this.recordId

        }).then(result => {
            var sCaseRecordId = result.substring(0, 18);
            var sCaseNumber = result.substring(19, 32);
            var caseDetails = { id: sCaseRecordId, number: sCaseNumber, comment: this.initialAddress };
            this.dispatchEvent(new CustomEvent(
                'openNewCaseFromAddress',
                {
                    detail: caseDetails,
                    bubbles: true,
                    composed: true,
                }
            ));
        }).catch(error => {
            console.log('Error Occured', error);
        });
    }

    getInterActionValue() {
        getInteractionType({
            interActionRecordId: JSON.stringify(this.interactionId)
        }).then(interActionTypeValue => {
            this.interactionType = interActionTypeValue;
        }).catch(error => {
            console.log('Error');
        });
    }

    //Generates lists for UI forms
    get addressTypes() {
        return [
            {
                "label": "Shipping",
                "value": "S"
            },
            {
                "label": "Billing",
                "value": "B"
            },
            {
                "label": "Permanent",
                "value": "P"
            },
            {
                "label": "Alternate",
                "value": "A"
            }
        ];
    }

    get items() {
        let addressList = [];
        let temp_Obj = {
            label: 'Add New Address',
            value: 'Add New Address'
        }
        addressList.push(temp_Obj);
        for (let [key, addressStr] of this.uniqueAddressData.entries()) {
            let temp_Obj = {
                label: addressStr,
                value: key
            }
            addressList.push(temp_Obj);
        }
        return addressList;
    }

    get stateOptions() {
        return [
            {
                "label": "AL",
                "value": "AL"
            },
            {
                "label": "AK",
                "value": "AK"
            },
            {
                "label": "AZ",
                "value": "AZ"
            },
            {
                "label": "AR",
                "value": "AR"
            },
            {
                "label": "CA",
                "value": "CA"
            },
            {
                "label": "CO",
                "value": "CO"
            },
            {
                "label": "CT",
                "value": "CT"
            },
            {
                "label": "DE",
                "value": "DE"
            },
            {
                "label": "DC",
                "value": "DC"
            },
            {
                "label": "FL",
                "value": "FL"
            },
            {
                "label": "GA",
                "value": "GA"
            },
            {
                "label": "HI",
                "value": "HI"
            },
            {
                "label": "ID",
                "value": "ID"
            },
            {
                "label": "IL",
                "value": "IL"
            },
            {
                "label": "IN",
                "value": "IN"
            },
            {
                "label": "IA",
                "value": "IA"
            },
            {
                "label": "KS",
                "value": "KS"
            },
            {
                "label": "KY",
                "value": "KY"
            },
            {
                "label": "LA",
                "value": "LA"
            },
            {
                "label": "ME",
                "value": "ME"
            },
            {
                "label": "MD",
                "value": "MD"
            },
            {
                "label": "MA",
                "value": "MA"
            },
            {
                "label": "MI",
                "value": "MI"
            },
            {
                "label": "MN",
                "value": "MN"
            },
            {
                "label": "MS",
                "value": "MS"
            },
            {
                "label": "MO",
                "value": "MO"
            },
            {
                "label": "MT",
                "value": "MT"
            },
            {
                "label": "NE",
                "value": "NE"
            },
            {
                "label": "NV",
                "value": "NV"
            },
            {
                "label": "NH",
                "value": "NH"
            },
            {
                "label": "NJ",
                "value": "NJ"
            },
            {
                "label": "NM",
                "value": "NM"
            },
            {
                "label": "NY",
                "value": "NY"
            },
            {
                "label": "NC",
                "value": "NC"
            },
            {
                "label": "ND",
                "value": "ND"
            },
            {
                "label": "OH",
                "value": "OH"
            },
            {
                "label": "OK",
                "value": "OK"
            },
            {
                "label": "OR",
                "value": "OR"
            },
            {
                "label": "PA",
                "value": "PA"
            },
            {
                "label": "PR",
                "value": "PR"
            },
            {
                "label": "RI",
                "value": "RI"
            },
            {
                "label": "SC",
                "value": "SC"
            },
            {
                "label": "SD",
                "value": "SD"
            },
            {
                "label": "TN",
                "value": "TN"
            },
            {
                "label": "TX",
                "value": "TX"
            },
            {
                "label": "UT",
                "value": "UT"
            },
            {
                "label": "VT",
                "value": "VT"
            },
            {
                "label": "VA",
                "value": "VA"
            },
            {
                "label": "WA",
                "value": "WA"
            },
            {
                "label": "WV",
                "value": "WV"
            },
            {
                "label": "WI",
                "value": "WI"
            },
            {
                "label": "WY",
                "value": "WY"
            }
        ];
    }

    handleCloseExistingCaseModal(event) {
        this.bShowExistingCaseModal = false;
    }

    get customcss() {
        if (this.calledfrom && this.calledfrom.toUpperCase() === 'LIGHTNING') {
            return 'container';
        } else {
            return 'spinnerWrapper';
        }
    }

}