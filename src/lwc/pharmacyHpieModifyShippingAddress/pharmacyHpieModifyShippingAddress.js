/*
Function        : LWC to Modify Shipping Address .

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Swapnali Sonawane              10/23/2023                    US - 5058187 Pharmacy Edit Order
* Jonathan Dickinson             02/29/2024                 User Story 4835317: T1PRJ0865978 - MF 27481 - C12; Mail Order Management; Pharmacy - Mail Order Pharmacy page gap- Member Consent (Lightning)
* Jonathan Dickinson             03/01/2024                    User Story 5058187: T1PRJ1295995 - (T1PRJ0870026)- MF 27409 HPIE/CRM SF - Tech - C12 Mail Order Management - Pharmacy - "Prescriptions & Order summary" tab - "Edit Order'
*****************************************************************************************************************************/
import { LightningElement, track, api, wire } from 'lwc';
import { USStateList } from 'c/genericUSStateList';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import { publish, MessageContext } from 'lightning/messageService';
import messageChannel from '@salesforce/messageChannel/pharmacyLMSChannel__c';
const SERVICE_ERROR_MESSAGE = 'An error occurred while updating Address for this Member. Please check all inputs and try again.';
const ZIP_CODE_ERROR_MESSAGE = 'The City State and Zip code combination you have entered is not valid.  Please correct these values and save again.';
const SHIPPING = 'Shipping / Active / ';
const PERMANENT = 'Permanent / Active / ';
const BILLING = 'Billing / Active / ';
const ACTIVE = ' / Active / ';
const INACTIVE = ' / InActive / ';
import retrieveAddrStdzResult from '@salesforce/apex/AddressStandardizeList_D_HUM.retrieveAddrStdzResult';
import { USPSRecomendationRequest, addressRequest } from './pharmacyHpieModifyShippingAddressHelper';
import { getUniqueId } from 'c/crmUtilityHum';
import { addNewAddressDetails } from 'c/pharmacyHPIEIntegrationHum';
export default class PharmacyHpieModifyShippingAddress extends LightningElement {
    @api enterpriseId;
    @api recordId
    @api userId;
    @api demographicsData;
    @track selectedAddress;
    @track addresses;
    @track selectedAddressLine = '';
    @track selectedCity = '';
    @track selectedState = '';
    @track selectedCountry = '';
    @track selectedZipcode = '';
    @track uspsLoaded = true;
    @track bDisplayAddress = false;
    @track addressline1 = '';
    @track addresscity = '';
    @track addressline2 = '';
    @track addressstate = '';
    @track addresszip = '';
    @track stateOptions = USStateList();
    @track bVerifybuttonDisbaled = true;
    @track showUspsError;
    @track validatedAddress;
    @track verifyDone = false;
    @track shippingAddressComments;
    @track stateVal = '';
    @track bDisplayUSPSAddress = false;
    @track selectedAddressValue;
    @track uspsSelected = false;
    @track userSelected = false;
    @track addressToUpdate;

    @track demographicsDetails;
    capitalizeFirstLetter(string) {
        return string.charAt(0).toUpperCase() + string.slice(1);
    }

    connectedCallback() {
        this.processDemographicsDetails();
    }

    @api setDemographicsDetails(data) {
        this.demographicsData = data;
        this.processDemographicsDetails();
        this.uspsLoaded = true;
    }
    displayToastEvent(message, variant, title) {
        this.dispatchEvent(new ShowToastEvent({
          title: title,
          message: message,
          variant: variant,
          mode: 'dismissable'
        }));
      }

    processDemographicsDetails() {
        this.addresslist = [];
        this.addresses = [];
        this.demographicsDetails = JSON.parse(this.demographicsData);
        if (this.demographicsDetails && Array.isArray(this.demographicsDetails)
            && this.demographicsDetails.length > 0) {
            let activeAddresses = this.demographicsDetails.filter(k => k?.active === true);
            if (activeAddresses && Array.isArray(activeAddresses) && activeAddresses?.length > 0) {
                activeAddresses.forEach(k => {
                    let uniqueId = getUniqueId();
                    if (k?.z0type?.description?.toLowerCase() === 'shipping') {
                        this.addresslist.push({
                            label: this.capitalizeFirstLetter(k?.z0type?.description?.toLowerCase()) + ACTIVE + k?.addressLine1?.toString().trim() +
                                (k?.addressLine2 != null && k?.addressLine2 != '' ? ',' + k?.addressLine2?.toString().trim() : ''
                                    + ',') + (k?.city ?? '') + ', ' + (k?.stateCode ?? '') + ' ' + (k?.zipCode ?? ''),
                            value: `${uniqueId}-${k?.key ?? ''}`
                        })
                        this.setSelectedAddress(k, uniqueId);
                    } else {
                        this.addresslist.push({
                            label: this.capitalizeFirstLetter(k?.z0type?.description?.toLowerCase()) + ACTIVE + k?.addressLine1?.toString().trim() +
                                (k?.addressLine2 != null && k?.addressLine2 != '' ? ',' + k?.addressLine2?.toString().trim() : ''
                                    + ',') + (k?.city ?? '') + ', ' + (k?.stateCode ?? '') + ' ' + (k?.zipCode ?? ''),
                            value: `${uniqueId}-${k?.key ?? ''}`
                        })
                    }
                })
            }
        }
    }

    handleAddressChange(event) {
        let selectedValue = event?.detail?.value ?? '';
        if (selectedValue && selectedValue?.length > 0) {
            let uniqueId = selectedValue?.split('-')[0];
            let addressKey = selectedValue?.split('-')[1];
            this.setSelectedAddress(this.demographicsDetails?.find(k => k?.key == addressKey), uniqueId);
        }
    }

    setSelectedAddress(k, uniqueId) {
        this.selectedAddressLine = `${k?.addressLine1 ?? ''} ${k?.addressLine2 ?? ''}`;
        this.selectedCity = k?.city ?? '';
        this.selectedCountry = k?.country ?? '';
        this.selectedState = k?.stateCode ?? '';
        this.selectedZipcode = k?.zipCode ?? '';
        this.selectedAddressValue = `${uniqueId}-${k?.key ?? ''}`;
        this.dispatchEvent(new CustomEvent('shippingaddress', {
            detail: {
                'ShippingAddress': k
            }
        }));
    }

    handleClick() {
        this.cleardata();
        this.bDisplayAddress = true;
        this.bVerifybuttonDisbaled = true;
        this.dispatchEvent(new CustomEvent('addaddressclick'));
    }


    cleardata() {
        this.template.querySelectorAll('lightning-input').forEach(t => {
            t.value = '';
        });
        this.addressline1 = '';
        this.addressline2 = '';
        this.addresscity = '';
        this.addresszip = '';
        this.addressstate = '';
        this.stateVal = '';
    }

    handleNewAddress(event) {
        switch (event?.target?.name?.toLowerCase()) {
            case 'addressline1':
                this.addressline1 = event?.target?.value ?? '';
                break;
            case 'addressline2':
                this.addressline2 = event?.target?.value ?? '';
                break;
            case 'city':
                this.addresscity = event?.target?.value ?? '';
                break;
            case 'zipcode':
                this.addresszip = event?.target?.value ?? '';
                break;
            case 'state':
                this.addressstate = event?.target?.value ?? '';
                break;
        }
        this.performaddressValidation();
    }

    performaddressValidation() {
        if (this.addressstate && this.addressstate?.length > 0
            && this.addresscity && this.addresscity?.length > 0
            && this.addresszip && this.addresszip?.length > 0
            && this.addressline1 && this.addressline1?.length > 0) {
            this.bVerifybuttonDisbaled = false;
        } else {
            this.bVerifybuttonDisbaled = true;
        }
    }

    handleVerify() {
        const isInputFilled = [...this.template.querySelectorAll('lightning-input')]
            .reduce((validSoFar, inputField) => {
                inputField.reportValidity();
                return validSoFar && inputField.checkValidity();
            }, true);

        if (isInputFilled) {
            this.callAddressService();
            this.bDisplayAddress = false;
        }
    }

    callAddressService() {
        this.uspsLoaded = false;
        this.addressToUpdate = new USPSRecomendationRequest(this.addressline1, this.addressline2, this.addresscity, this.addressstate, this.addresszip);
        let sAddressStandardizeRequest = JSON.stringify(this.addressToUpdate);
        retrieveAddrStdzResult({
            addressInput: sAddressStandardizeRequest
        }).then(data => {
            let response = JSON.parse(data);
            this.validatedAddress = response && response?.addressStandardizedResponse
                && response.addressStandardizedResponse?.StandardizeAddressResponse
                ? response.addressStandardizedResponse.StandardizeAddressResponse : null;
            if (response && response?.addressStandardizedResponse && !response.addressStandardizedResponse.calloutErrored && this.validatedAddress.AddressLine1 != null) {
                this.uspsLoaded = true;
                this.bDisplayUSPSAddress = true;
                this.showUspsError = false;
                this.selectedAddress = this.validatedAddress;
            } else {
                this.uspsLoaded = true;
                this.showUspsError = true;
                this.bDisplayUSPSAddress = false;
                this.selectedAddress = this.addressToUpdate;
            }
            this.verifyDone = true;
        }).catch(e => {
            console.log('Error : ' + JSON.stringify(e));
            this.uspsLoaded = true;
        })
    }

    handleUSPSCancel() {
        this.bVerifybuttonDisbaled = true;
        this.bDisplayUSPSAddress = false;
        this.showUspsError = false;
        this.verifyDone = false;
        this.cleardata();
        this.bDisplayAddress = true;
        this.processDemographicsDetails();
    }

    handleCancel() {
        this.bDisplayAddress = false;
        this.dispatchEvent(new CustomEvent("showcaseblock"));
        this.processDemographicsDetails();
    }

    handleSaveLog() {
        this.addNewAddressServiceCall();
        this.bDisplayUSPSAddress = false;
        this.showUspsError = false;
    }

    addNewAddressServiceCall() {
        this.uspsLoaded = false;
        const newAddress = new addressRequest(this.enterpriseId, this.organization ?? 'HUMANA', this.userId, '11', 'Shipping', this.selectedAddress?.AddressLine1,
            this.selectedAddress?.AddressLine2 ?? '', this.selectedAddress.City
            , this.selectedAddress.StateCode, this.selectedAddress.ZipCode, 'NO OVERRIDE', 0, this.bDisplayUSPSAddress, true,-1);
        
        addNewAddressDetails(JSON.stringify(newAddress))
            .then(result => {
                if (result) { 
                    this.uspsLoaded = true;
                    this.fireEventToParent();
                    this.displayToastEvent('Shipping addreess has been created successfully', 'success', 'Shipping Address');
                    this.dispatchEvent(new CustomEvent("showcaseblock")); 
                }else{
                    this.uspsLoaded = true;
                    this.displayToastEvent(SERVICE_ERROR_MESSAGE, 'error', 'Error!')
                    this.returnToEditMode();
                }
            }).catch(error => {
                this.uspsLoaded = true;
                this.displayToastEvent(SERVICE_ERROR_MESSAGE, 'error', 'Error!')
                this.returnToEditMode();
                console.log('Update Shpping Address Error :' + JSON.stringify(error));
            })
    }

    callCreateShippingComment() {
        this.shippingAddressComments = `We were contacted to add shipping address as ${this.selectedAddress?.AddressLine1 ?? ''} ${thid.selectedAddress?.AddressLine2 ?? ''} ${this.selectedAddress?.City ?? ''} ${this.selectedAddress?.StateCode ?? ''} ${this.selectedAddress?.ZipCode}`;
        this.dispatchEvent(new CustomEvent('shippingaddresscomments', {
            detail: {
                'ShippingAddressComment': this.shippingAddressComments
            }
        }));
    }
    returnToEditMode() {
        this.showUspsError = false;
        this.bDisplayUSPSAddress = false;
        this.verifyDone = false;
        this.stateVal = this.selectedAddress?.StateCode;
        this.bDisplayAddress = true;
        
    }
    fireEventToParent() {
        this.dispatchEvent(new CustomEvent('calldemographics'));
    }
}