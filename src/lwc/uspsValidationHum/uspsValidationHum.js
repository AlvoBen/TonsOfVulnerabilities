/*--
File Name        : uspsValidationHum.js
Version          : 1.0
Created Date     : 08/20/2021
Function         : Lightning Web Component used to validate an address against USPS
Modification Log :
* Developer                          Date                  Description
*************************************************************************************************
* Kiran Bhuvanagiri                 08/20/2021            User Story 2421627: Original Version
* Kiran Bhuvanagiri                 01/04/2022            User Story 2759501: Case Documentation for Address Changes: UI Updates New/ Existing Case  (RxRF)
* Kiran Bhuvanagiri					05/23/2022			  User Story 2882456: Case Documentation for Demographic Updates (CRMS)
* Jonathan Dickinson		        09/27/2022		      REQ - 3751914
* Jonathan Dickinson			    09/22/2023				   User Story 5061288: T1PRJ0870026   MF 27406 HPIE/CRM - SF Tech- Mail Order Management - Pharmacy -  Details tab - Address section
**************************************************************************************************/

import { LightningElement, api, track } from 'lwc';
import retrieveAddrStdzResult from '@salesforce/apex/AddressStandardizeList_D_HUM.retrieveAddrStdzResult';
import uspsAddressNotConfirmedMsg from '@salesforce/label/c.uspsAddressNotConfirmedMsg';
import { ePostAddressDTO } from './uspsValidationHum_Helper';
import US2593758SwitchLabel from '@salesforce/label/c.US2593758SwitchLabel';


export default class uspsValidationHum extends LightningElement {

    @api recordId;
    @track uspsLoaded;
    @track isError = false;
    @track showValidatedAddress = false;

    @api addressToUpdate;
    @api setOfType;

    @track validatedAddress;
    @track disableSaveButton = true;

    @track showUspsError;
    @api showEpostError;

    @track selectedAddress;

    @track inputAddressSelected = false;
    @track uspsAddressSelected = false;

    sUspsErrorMsg = '';

    @track isNewCase = true;
    @track isExistingCase;
    @api isLightning;
    @track radioValue;
    @track submit = true;
    @track useSaveData = true;
    @track noPopOver = true;
    @track CaseId;
    @track CaseNo;
    @track addressDataObj;

    get addressOptions() {
        return [
            { label: '', value: 'Address' },
        ];
    }
    get USPSAddrsOptions() {
        return [
            { label: '', value: 'USPSAdd' },
        ];
    }

    constructor() {
        super();
        this.uspsLoaded = false;
        this.sUspsErrorMsg = uspsAddressNotConfirmedMsg;

    }

    connectedCallback() {
        let sAddressStandardizeRequest = JSON.stringify(this.addressToUpdate);
        this.showEpostError = false;
        retrieveAddrStdzResult({
            addressInput: sAddressStandardizeRequest
        }).
            then(data => {
                let response = JSON.parse(data);
                this.validatedAddress = response?.addressStandardizedResponse?.StandardizeAddressResponse;
                this.uspsLoaded = true;
                if (!response?.addressStandardizedResponse?.calloutErrored && this.validatedAddress?.AddressLine1) {
                    this.showValidatedAddress = true;
                    if(this.isLightning) {
                        this.radioValue = this.USPSAddrsOptions.find(el => el.value === 'USPSAdd') ? 'USPSAdd' : '';
                        this.disableSaveButton = false;
                        this.uspsAddressSelected = true;
                        this.selectedAddress = this.validatedAddress;
                    }
                } else {
                    this.isError = true;
                    this.radioValue = this.addressOptions.find(el => el.value === 'Address') ? 'Address' : '';
                    this.showUspsError = true;
                    this.inputAddressSelected = true;
                    this.disableSaveButton = false;
                    this.selectedAddress = this.addressToUpdate;
                }
            }).catch(e => {
                console.log('Error : ' + JSON.stringify(e));
                this.errorMessage = JSON.stringify(e);
                this.uspsLoaded = true;
            })

    }

    handleInputAddressSelect(event) {
        this.inputAddressSelected = !this.inputAddressSelected;
        this.uspsAddressSelected = false;
        this.selectedAddress = this.addressToUpdate;

        if (this.inputAddressSelected) {
            this.disableSaveButton = false;
        } else {
            this.disableSaveButton = true;
        }
        event.preventDefault(); //Prevents reload
    }

    handleUspsAddressSelect(event) {
        this.uspsAddressSelected = !this.uspsAddressSelected;
        this.inputAddressSelected = false;
        this.selectedAddress = this.validatedAddress;

        if (this.uspsAddressSelected) {
            this.disableSaveButton = false;
        } else {
            this.disableSaveButton = true;
        }
        event.preventDefault(); //Prevents reload
    }

    handleCancel() {
        this.showUspsError = false;
        this.showEpostError = false;
        this.dispatchEvent(new CustomEvent('cancel'));
    }

    handleBack() {
        this.showUspsError = false;
        this.showEpostError = false;
        this.dispatchEvent(new CustomEvent('back'));
    }

    handleRemove(event) {
        const name = event.target.name;
        this.setOfType = this.setOfType.filter(item => item.name !== name);
        if (this.setOfType.length == 0) {
            this.handleCancel();
        } else {
            const removeSelection = new CustomEvent("deletetypeselection", {
                detail: name,
            });
            this.dispatchEvent(removeSelection);
        }
    }

    uspsHandleSave(event) {
        const ePostAddressesDTO = [];

        for (let addressType of this.setOfType) {
            let addressTypeChar = addressType.label.charAt(0);

            let ePostAddress = new ePostAddressDTO(this.selectedAddress?.AddressLine1, this.selectedAddress?.AddressLine2, this.selectedAddress?.City, this.selectedAddress?.StateCode, this.selectedAddress?.ZipCode, addressTypeChar, this.uspsAddressSelected);
            ePostAddressesDTO.push(ePostAddress);
        }
        console.log(ePostAddressesDTO);
        if (ePostAddressesDTO.length > 0) {
            const ePostAddressesEvent = new CustomEvent("uspssaveaddresses", {
                detail: {
                    address : ePostAddressesDTO,
                    isNewCase : true
                }
            });
            this.dispatchEvent(ePostAddressesEvent);
        } else {
            this.showEpostError = true;
        }
    }

    handleNewCase(event){
        const ePostAddressesDTO = [];

        for (let addressType of this.setOfType) {
            let addressTypeChar = addressType.label.charAt(0);

            let ePostAddress = new ePostAddressDTO(this.selectedAddress?.AddressLine1, this.selectedAddress?.AddressLine2, this.selectedAddress?.City, this.selectedAddress?.StateCode, this.selectedAddress?.ZipCode, addressTypeChar, this.uspsAddressSelected);
            ePostAddressesDTO.push(ePostAddress);
        }
        console.log(ePostAddressesDTO);
        if (ePostAddressesDTO.length > 0) {
            const ePostAddressesEvent = new CustomEvent("uspssaveaddresses", {
                detail: {
                    address : ePostAddressesDTO,
                    isNewCase : true
                }
            });
            this.dispatchEvent(ePostAddressesEvent);
        } else {
            this.showEpostError = true;
        }
    }

    handleExistingCase(event){
        const ePostAddressesDTO = [];
		this.showUspsError = false;
		this.showEpostError = false;
        for (let addressType of this.setOfType) {
            let addressTypeChar = addressType.label.charAt(0);

            let ePostAddress = new ePostAddressDTO(this.selectedAddress?.AddressLine1, this.selectedAddress?.AddressLine2, this.selectedAddress?.City, this.selectedAddress?.StateCode, this.selectedAddress?.ZipCode, addressTypeChar, this.uspsAddressSelected);
            ePostAddressesDTO.push(ePostAddress);
        }
        if (ePostAddressesDTO.length > 0) {
            const ePostAddressesEvent = new CustomEvent("uspssaveaddresses", {
                detail: {
                    address : ePostAddressesDTO,
                    isNewCase : false
                }
            });
            this.dispatchEvent(ePostAddressesEvent);
        } else {
            this.showEpostError = true;
        }
    }

    handleHideErrorMsg(event) {
        event.preventDefault();
        if ((event.target.id).includes('uspsErrorBtnLightning') || (event.target.id).includes('uspsErrorBtnClassic')) {
            this.showUspsError = false;
        }
        if ((event.target.id).includes('ePostErrorBtn')) {
            this.showEpostError = false;
        }
    }

    get hasAddressLogging() {
		return (US2593758SwitchLabel.toUpperCase() === 'Y');
	}

    handleOptionChange(event) {
        let selectedAddOption = event.detail.value;
        if (selectedAddOption === 'USPSAdd') {
            this.uspsAddressSelected = true;
            this.selectedAddress = this.validatedAddress;
        } else {
            this.uspsAddressSelected = false;
            this.selectedAddress = this.addressToUpdate;
        }
        this.disableSaveButton = false;
    }

    handleSaveLog(event)
    {
        if (event)
        {
            this.isExistingCase = event.detail.ExistCase;

            if (this.isExistingCase) {
                this.handleExistingCase();
            } else {
                this.handleNewCase();
            }
        }
    }

    @api
    createAddressObject(initialAddress) {

            this.addressDataObj = {
                header : 'Successfully Processed',
                tablayout : false,
                source : 'Address',
                caseComment : initialAddress,
                message : 'Address Added Successfully',
                redirecttocaseedit : true
            }

            const commentLoggingComponent = this.template.querySelector('c-generic-case-comment-logging')
            if (commentLoggingComponent) {
                commentLoggingComponent.handleLog(this.addressDataObj, this.isExistingCase);
            }
    }

}