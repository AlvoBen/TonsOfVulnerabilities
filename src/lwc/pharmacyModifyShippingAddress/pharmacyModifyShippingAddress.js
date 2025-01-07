/*******************************************************************************************************************************
Component Name : Pharmacy Modify Shipping Address
Version        : 1.0
Created On     : 12/10/2021
Function       : This component used for adding new shipping address or modify shipping address.
                 
Modification Log: 
* Developer Name               Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------------
* Nirmal Garg                  12/10/2021               	Original Version US-2765962
* Viswesrao					   03/20/2021					Changes for US-2938233
* Abhishek Mangutkar           06/14/2022               	Defect Fix - 5112
* Swapnali Sonawane            11/01/2022                   US- 3729809 Migration of the UI enhancements in the addresses section
* Nirmal Garg				   07/27/2023	                US4902305
**************************************************************************************************************************************/
import { LightningElement, track, api, wire } from 'lwc';
import { USStateList } from 'c/genericUSStateList'
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import epostUpdateMember from '@salesforce/apexContinuation/PharmacyCreateOrder_LC_HUM.updateGetMember';
import pharmacyoverridemsg from '@salesforce/label/c.HUMAddressOverrideMsg';
import pharmacymodifyaddresstitle from "@salesforce/label/c.HUMPharmacyModifyAddressTitle";
import pharmacymodifyaddressmsg from "@salesforce/label/c.HUMPharmacyModifyAddressMsg";
import { publish, MessageContext } from 'lightning/messageService';
import messageChannel from '@salesforce/messageChannel/pharmacyLMSChannel__c';
import getMemberDetails from "@salesforce/apexContinuation/Pharmacy_LC_HUM.invokeGetMemberService";
const SERVICE_ERROR_MESSAGE = 'An error occurred while updating Address for this Member. Please check all inputs and try again.';
const ZIP_CODE_ERROR_MESSAGE = 'The City State and Zip code combination you have entered is not valid.  Please correct these values and save again.';
const SHIPPING = 'Shipping / Active / ';
const PERMANENT = 'Permanent / Active / ';
const BILLING = 'Billing / Active / ';
const ACTIVE = ' / Active / ';
const INACTIVE = ' / InActive / ';
import retrieveAddrStdzResult from '@salesforce/apex/AddressStandardizeList_D_HUM.retrieveAddrStdzResult';
import { USPSRecomendationRequest } from './pharmacyModifyShippingAddressHelper';


export default class PharmacyModifyShippingAddress extends LightningElement {
    @api
    enterpriseId;

    @api
    recordId

    @api
    networkId;

    @api
    pharmacymemberaddresss;

    @api
    pharmacymemberdetails;

    @track isPharmacyTemp = true;
    //MessageContext for publishig demographic data.
    @wire(MessageContext)
    messageContext;
    @track options = [];
    @track addressline1 = '';
    @track addresscity = '';
    @track addressline2 = '';
    @track addressstate = '';
    @track addresszip = '';
    bVerifybuttonDisbaled = true;
    bDisplayAddress = false;
    bDisplayUSPSAddress = false;

    overridecode = '';
    selectedAddress = '';
    selectedMemAddres = '';
    @track addresslist = [];
    @track selectedAddressLine = '';
    @track selectedCity = '';
    @track selectedState = '';
    @track selectedCountry = '';
    @track selectedZipcode = '';
    pharmacyaddressBkup = [];

    get stateOptions() {
        return USStateList();
    }

    @track addressToUpdate = new USPSRecomendationRequest('', '', '', '', '');
    @track selectedAddOption;
    @track showUspsError;
    @track validatedAddress;
    @track uspsLoaded = true;
    @track verifyDone = false;
    @track selectedAddress;
    @track shippingAddressComments;
    @track stateVal = '';

    label = {
        pharmacyoverridemsg,
        pharmacymodifyaddresstitle,
        pharmacymodifyaddressmsg
    }

    @api
    pharmacydata(enterpriseid, recordid, networkid, pharmacyaddress) {
        this.enterpriseId = enterpriseid;
        this.recordId = recordid;
        this.networkId = networkid;
        this.pharmacyMemberAddresss = pharmacyaddress;
        this.displayAddressData();
    }

    get options() {
        return this.addresslist;
    }


    capitalizeFirstLetter(string) {
        return string.charAt(0).toUpperCase() + string.slice(1);
    }


    displayAddressData() {
        this.addresslist = [];
        this.options = [];
        this.pharmacyaddressBkup = [];
        let Pharmacybkup = [];
        if (this.pharmacyMemberAddresss && this.pharmacyMemberAddresss.length > 0) {
            this.pharmacyMemberAddresss.forEach((k, i) => {
                Pharmacybkup.push({
                    "value": k,
                    "Index": i
                });
                switch (k.AddressType) {
                    case "S":
                        if (k.IsActive === "true") {
                            this.addresslist.push({
                                label: this.capitalizeFirstLetter(k.AddressTypeLiteral.toLowerCase()) + ACTIVE + k.AddressLine1.toString().trim() +
                                    (k.AddressLine2 != null && k.AddressLine2 != '' ? ',' + k.AddressLine2.toString().trim() : ''
                                        + ',') + k.City + ', ' + k.StateCode + ' ' + k.ZipCode,
                                value: i
                            });
                            this.selectedState = k.StateCode;
                            this.selectedZipcode = k.ZipCode;
                            this.selectedAddressLine = `${k.AddressLine1}${k.AddressLine2 != null && k.AddressLine2 != '' ? ',' + k.AddressLine2 : ''}`;
                            this.selectedCity = k.City;
                            this.selectedMemAddres = i
                        }
                        break;
                    default:
                        if (k.IsActive === "true") {
                            this.addresslist.push({
                                label: this.capitalizeFirstLetter(k.AddressTypeLiteral.toLowerCase()) + ACTIVE + k.AddressLine1.toString().trim() +
                                    (k.AddressLine2 != null && k.AddressLine2 != '' ? ',' + k.AddressLine2.toString().trim() : ''
                                        + ',') + k.City + ', ' + k.StateCode + ' ' + k.ZipCode,
                                value: i
                            });
                        }
                        break;
                }
            })
            this.options = this.addresslist;
            this.pharmacyaddressBkup = Pharmacybkup;
        }
    }

    connectedCallback() {
        this.pharmacyMemberAddresss = this.pharmacymemberdetails?.Address;
        this.displayAddressData();
    }

    handleAddress1(event) {
        this.addressline1 = event.target.value;
        this.performaddressValidation();
    }

    handlecity(event) {
        let inputVal = event.target.value;
        event.target.value = inputVal.replace(/[0-9]/g, "");
        this.addresscity = event.target.value;
        this.performaddressValidation();
    }

    handleAddress2(event) {
        this.addressline2 = event.target.value;
        this.performaddressValidation();
    }

    handlestate(event) {
        this.addressstate = event.target.value;
        this.performaddressValidation();
    }

    handlezip(event) {
        let inputVal = event.target.value;
        event.target.value = inputVal.replace(/[^[0-9]/g, "");
        this.addresszip = event.target.value;
        this.performaddressValidation();
    }

    performaddressValidation() {
        if (this.addressstate != undefined && this.addressstate != ''
            && this.addresscity != undefined && this.addresscity != ''
            && this.addresszip != undefined && this.addresszip != ''
            && this.addressline1 != undefined && this.addressline1 != '') {
            this.bVerifybuttonDisbaled = false;
        } else {
            this.bVerifybuttonDisbaled = true;
        }
    }

    handleUSPSCancel() {
        this.bVerifybuttonDisbaled = true;
        this.bDisplayUSPSAddress = false;
        this.showUspsError = false;
        this.verifyDone = false;
        this.cleardata();
        this.bDisplayAddress = true;
    }

    handleCancel() {
        this.bDisplayAddress = false;
        this.dispatchEvent(new CustomEvent("showcaseblock"));
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

    checkString(inputstring) {
        if (inputstring != null && inputstring != undefined && inputstring != '') {
            return inputstring;
        } else {
            return '';
        }
    }

    displayIncompatibleZipCityMsg() {
        this.bDisplayAddress = true;
        this.displayToastEvent(ZIP_CODE_ERROR_MESSAGE, 'error', 'Error!')
    }

    handleAddressChange(event) {
        this.selectedMemAddres = parseInt(event.detail.value);
        const addressType = event.target.value;
        if (this.pharmacyMemberAddresss && this.pharmacyMemberAddresss.length > 0) {
            let getvaluefromIndex = this.pharmacyaddressBkup.find(k => k.Index === parseInt(event.detail.value)).value;
            const shippingaddress = getvaluefromIndex;
            this.selectedAddress = shippingaddress.AddressLine2 != null ?
                `${shippingaddress.AddressLine1}${shippingaddress.AddressLine2 != null && shippingaddress.AddressLine2 ? shippingaddress.AddressLine2 : ''}\n
            ${shippingaddress.City}, ${shippingaddress.StateCode}, ${shippingaddress.ZipCode}` : `${shippingaddress.AddressLine1}\n
            ${shippingaddress.City}, ${shippingaddress.StateCode}, ${shippingaddress.ZipCode}`;
            this.selectedState = shippingaddress.StateCode;
            this.selectedZipcode = shippingaddress.ZipCode;
            this.selectedAddressLine = `${shippingaddress.AddressLine1}${shippingaddress.AddressLine2 != null && shippingaddress.AddressLine2 != '' ? shippingaddress.AddressLine2 : ''}`;
            this.selectedCity = shippingaddress.City;
            this.dispatchEvent(new CustomEvent('shippingaddress', {
                detail: {
                    'ShippingAddress': shippingaddress
                }
            }));
        }
    }

    updateShippingAddress(event) {
        if (event.detail) {
            this.selectedAddress = event.detail.ShippingAddress;
        }
    }

    displayToastEvent(message, variant, title) {
        this.dispatchEvent(new ShowToastEvent({
            title: title,
            message: message,
            variant: variant,
            mode: 'dismissable'
        }));
    }

    returnToEditMode() {
        this.showUspsError = false;
        this.bDisplayUSPSAddress = false;
        this.verifyDone = false;
        this.stateVal = this.selectedAddress?.StateCode;
        this.bDisplayAddress = true;
        this.bEpostFail = true;
    }

    async epostUpdateMemberCall(enterpriseId, networkId, recordId, addressDTO) {
        return new Promise((resolve, reject) => {
            epostUpdateMember({
                enterprise: enterpriseId, phone: '', AltPhone: '', Email: '', captype: ''
                , networkId: networkId, sRecordId: recordId, addressDto: JSON.stringify(addressDTO)
            }).then(result => {
                resolve(result);
            }).catch(error => {
                reject(error);
            })
        })
    }


    async commitDataToEpost() {
        this.uspsLoaded = false;
        const addressDTO = new AddressDTO(this.selectedAddress.AddressLine1, this.selectedAddress.AddressLine2, this.selectedAddress.City
            , this.selectedAddress.StateCode, this.selectedAddress.ZipCode, this.enterpriseId, this.networkId, this.recordId, this.overridecode);

        this.epostUpdateMemberCall(this.enterpriseId, this.networkId, this.recordId, addressDTO).then(result => {
            if (result) {
                if (typeof (result) === 'string' && result.includes('IncompatibleZipCity')) {
                    this.uspsLoaded = true;
                    this.displayIncompatibleZipCityMsg();
                    this.returnToEditMode();
                } else if (typeof (result) === 'string' && result.includes('faultcode')) {
                    this.uspsLoaded = true;
                    this.displayToastEvent(SERVICE_ERROR_MESSAGE, 'error', 'Error!')
                    this.returnToEditMode();
                }
                else {
                    this.bDisplayUSPSAddress = false;
                    this.callGetMemberService();

                    this.callCreateShippingComment();
                    this.dispatchEvent(new CustomEvent("showcaseblock"))

                }
            } else {
                this.uspsLoaded = true;
                this.displayToastEvent(SERVICE_ERROR_MESSAGE, 'error', 'Error!')
                this.returnToEditMode();
            }
        }).catch(error => {
            this.uspsLoaded = true;
            this.displayToastEvent(SERVICE_ERROR_MESSAGE, 'error', 'Error!');
            this.returnToEditMode();
            console.log('Update Shpping Address Error :' + JSON.stringify(error));
        })
    }

    callCreateShippingComment() {
        let tempStr = this.selectedAddress.AddressLine1;
        if (this.selectedAddress.AddressLine2 != '' && this.selectedAddress.AddressLine2 != null && this.selectedAddress.AddressLine2 != undefined) {
            tempStr += ' ' + this.selectedAddress.AddressLine2;
        }
        tempStr += ', ' + this.selectedAddress.City + ' ' + this.selectedAddress.StateCode + ' ' + this.selectedAddress.ZipCode;
        this.shippingAddressComments = 'We were contacted to add shipping address as ' + tempStr;
        this.dispatchEvent(new CustomEvent('shippingaddresscomments', {
            detail: {
                'ShippingAddressComment': this.shippingAddressComments
            }
        }));
    }

    handleSaveLog() {
        this.commitDataToEpost();
        this.bDisplayUSPSAddress = false;
        this.showUspsError = false;
    }

    callGetMemberService() {

        this.uspsLoaded = false;
        getMemberDetails({
            memID: this.enterpriseId,
            networkId: this.networkId,
            sRecordId: this.recordId
        }).then(data => {
            if (data) {
                this.updateSelectedAddress(data);
                this.uspsLoaded = true;
                this.displayToastEvent('Shipping addreess has been created successfully', 'success', 'Shipping Address');
            } else {
                this.uspsLoaded = true;
            }
        }).catch(e => {
            this.uspsLoaded = true;
            console.log('Error : ' + JSON.stringify(e));
        })
        this.cleardata();
    }


    updateSelectedAddress(result) {
        let response = JSON.parse(result);
        this.publishDemographicData(response.objPharDemographicDetails);
        let shippingaddress = response != null && response.objPharDemographicDetails != null &&
            response.objPharDemographicDetails.Address != null ? response.objPharDemographicDetails.Address.find(k => k.AddressType === 'S') : null;
        if (shippingaddress) {
            this.selectedAddress = shippingaddress.AddressLine2 != null ?
                `${shippingaddress.AddressLine1}\n ${shippingaddress.AddressLine2}\n
            ${shippingaddress.City}, ${shippingaddress.StateCode}, ${shippingaddress.ZipCode}` : `${shippingaddress.AddressLine1}\n
            ${shippingaddress.City}, ${shippingaddress.StateCode}, ${shippingaddress.ZipCode}`;
            this.selectedState = shippingaddress.StateCode;
            this.selectedZipcode = shippingaddress.ZipCode;
            this.selectedAddressLine = `${shippingaddress.AddressLine1}${shippingaddress.AddressLine2 != null && shippingaddress.AddressLine2 != '' ? shippingaddress.AddressLine2 : ''}`;
            this.selectedCity = shippingaddress.City;
        }
        this.pharmacyMemberAddresss = response.objPharDemographicDetails.Address;
        this.dispatchEvent(new CustomEvent('shippingaddress', {
            detail: {
                'ShippingAddress': shippingaddress
            }
        }));
        this.displayAddressData();
    }


    publishDemographicData(demographicDetails) {
        let message = { messageDetails: demographicDetails, MessageName: "UpdateAddress" };
        publish(this.messageContext, messageChannel, message);

        this.dispatchEvent(new CustomEvent(
            'updateaddress',
            {
                detail: demographicDetails,
                bubbles: true,
                composed: true,
            }
        ));
    }
}

export class AddressDTO {
    constructor(addressLine1, addressLine2, addresscity, addressstatecode, addresszipcode,
        enterpriseID, networkId, recordId, overridecode) {
        this.addressReq = new EASRequest(addressLine1, addressLine2, addresscity, addressstatecode,
            addresszipcode);
        this.statusValue = 'Active',
            this.addressValidatedFlag = 'false';
        this.enterpriseID = enterpriseID;
        this.addresstype = 'S';
        this.overrideReasonCode = overridecode;
        this.networkID = networkId;
        this.sRecordId = recordId;
    }
}


export class EASRequest {
    constructor(addressLine1, addressLine2, addresscity, addressstatecode, addresszipcode,) {
        this.AddressLine1 = addressLine1;
        this.AddressLine2 = addressLine2;
        this.City = addresscity;
        this.StateCode = addressstatecode;
        this.ZipCode = addresszipcode;
    }
}