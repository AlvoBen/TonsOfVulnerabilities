/*
LWC Name        : pharmacyOrderDetailsEditHum.js
Function        : LWC to display pharmacy order edit details.

Modification Log:
* Developer Name                  Date                         Description
*
* Abhishek Mangutkar              03/09/2022                   US - 3139633
* Nirmal Garg					  03/14/2022				DF-4625,4670 fix	
* Vishal Shinde                  10/10/2023                 User Story 5002422- Mail Order Management; Pharmacy - identify Error Messaging and parameters (Lightning)
****************************************************************************************************************************/

import { LightningElement, api, track, wire } from 'lwc';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import invokecreateEditOrderDetail from '@salesforce/apexContinuation/PharmacyCreateOrder_LC_HUM.invokecreateEditOrderDetail';
import { CreateOrderDTO } from './editorderrequest';
import sendLogNotesToEPost from '@salesforce/apexContinuation/PharmacyCreateOrder_LC_HUM.invokeLogNoteRequest';
import poboxerror from "@salesforce/label/c.POERROR_PHARMACY_HUM";
import ordernotprocessederror from "@salesforce/label/c.OrderNotProcessedErr";
import ExpiredCreditCardErr from "@salesforce/label/c.ExpiredCreditCardErr";
import PHARMACY_REGULAR_MAIL from "@salesforce/label/c.PHARMACY_REGULAR_MAIL";
import PHARMACY_EXPEDITED_CHARGE from "@salesforce/label/c.PHARMACY_EXPEDITED_CHARGE";
import PHARMACY_EXPEDITED_NO_CHARGE from "@salesforce/label/c.PHARMACY_EXPEDITED_NO_CHARGE";
import PHARMACY_SHIPPINGMETHOD_FIVE_ONE_ONE from "@salesforce/label/c.PHARMACY_SHIPPINGMETHOD_FIVE_ONE_ONE";
import PHARMACY_SHIPPINGMETHOD_ONE from "@salesforce/label/c.PHARMACY_SHIPPINGMETHOD_ONE";
import PHARMACY_SHIPPINGMETHOD_TWO from "@salesforce/label/c.PHARMACY_SHIPPINGMETHOD_TWO";
import OTC_Message from "@salesforce/label/c.OTC_Message";
import getMemberDetails from "@salesforce/apexContinuation/Pharmacy_LC_HUM.invokeGetMemberService";
import humanaPharmacyLMS from '@salesforce/messageChannel/pharmacyLMSChannel__c';
import { publish, MessageContext, subscribe, unsubscribe } from 'lightning/messageService';
const CREDTCARD_HIDDEN_NUMBER = ' ********';
const CREDITCARD_EXP_CONST = ' exp ';
const CREDITCARD_AUTO_CONST = '(Auto) ';
const EDIT_CARD_ERROR = ' You must Select a Card.';
const ADDERROR = 'ADDR12006';
const LOG_NOTE_ERROR = 'Problem in sending Log Notes. If you continue to receive this message, contact CSS HelpDesk.';

export default class PharmacyOrderDetailsEditHum extends LightningElement {
    @api enterpriseId;
    @api recordId;
    @api networkId;

    @api orderdetails;
    @api orderDetailsResponse;
    @api capType;
    @api orderparentnode;
    @api orderDetailPrescriptions;
    @api spayer;

    @api pharmacydemodetails;

    loaded = true;
    checkCode = false;
    sMinDate;
    sMaxDate;
    calledFromEditOrder = true;
    selectedShippingMethod;
    lstPaymentMethods = [];

    commercialUser;
    creditCardData;
    paymentMethod;
    createAndEditCreditCardComponentMode;
    selectedRowData;
    isCreateAndEditCreditCardVisible;
    createOrderPrescriptionItems = [];
    numberOfApprovedConsents;
    orderReleaseDate;
    displayOTCMessage = false;
    deleteItemEvent = false;
    deletedItems = [];
    dateChangedEvent = false;
    selectedDate;
  @track chkCreditCardExpiryBol= false;

    label = {
        poboxerror,
    ordernotprocessederror,
    ExpiredCreditCardErr
    }
    shippingoptions = [{
        label: PHARMACY_REGULAR_MAIL,
        value: PHARMACY_SHIPPINGMETHOD_ONE
    }, {
        label: PHARMACY_EXPEDITED_CHARGE,
        value: PHARMACY_SHIPPINGMETHOD_TWO
    }, {
        label: PHARMACY_EXPEDITED_NO_CHARGE,
        value: PHARMACY_SHIPPINGMETHOD_FIVE_ONE_ONE
    }];

    overTheCounterMsg = OTC_Message;

    connectedCallback() {
        this.initializeData();
    }

    @api setDemographicsDetails(data) {
        this.pharmacydemodetails = data;
        this.selectedShippingAddress = this.pharmacydemodetails != null
            && this.pharmacydemodetails?.Address && Array.isArray(this.pharmacydemodetails?.Address)
            && this.pharmacydemodetails?.Address?.length > 0 ? this.pharmacydemodetails.Address.find(k => k.AddressType === 'S' && k.IsActive === 'true') : null;
        const pharmacyaddress = this.pharmacydemodetails?.Address ?? null;
        if (this.template.querySelector('c-pharmacy-modify-shipping-address') != null) {
            this.template.querySelector('c-pharmacy-modify-shipping-address').pharmacydata(this.enterpriseId,
                this.recordId, this.networkId, pharmacyaddress);
        }
    }

    @wire(MessageContext)
    messageContext;

    initializeData() {
        let todaysdate = new Date();
        this.sMaxDate = todaysdate.toISOString().substring(0, 10);
        let minDate = new Date();
        minDate.setDate(todaysdate.getDate() - 90);
        this.sMinDate = minDate.toISOString().substring(0, 10);
        let releaseDate = this.orderdetails.OrderReleaseDate;
        if (releaseDate) {
            let rDate = new Date(releaseDate);
            rDate.setDate(rDate.getDate() + 1);
            this.orderReleaseDate = rDate.toISOString().substring(0, 10);
        }
        else {
            this.orderReleaseDate = '';
        }
        this.displayOTCMessage = this.orderdetails.OTC === 'Yes' ? true : false;
        this.lstPaymentMethods = [];
        this.pharmacydemodetails = JSON.parse(JSON.stringify(this.pharmacydemodetails))
        this.selectedShippingAddress = this.pharmacydemodetails != null
            && this.pharmacydemodetails.Address != null ? this.pharmacydemodetails.Address.find(k => k.AddressType === 'S' && k.IsActive === 'true') : null;
        let creditCards = this.pharmacydemodetails?.CreditCardsDetail?.CreditCard;
        creditCards = creditCards != null && creditCards.length > 0 ? creditCards.filter(k => k.IsActive === 'true') : [];
        this.commercialUser = this.pharmacydemodetails != null && this.pharmacydemodetails.NeedsMemberConsent != null ?
            this.pharmacydemodetails.NeedsMemberConsent : null;
        this.creditCardData = JSON.parse(JSON.stringify(this.pharmacydemodetails.CreditCardsDetail));
        this.lstPaymentMethods.push({
            label: 'Invoice',
            value: '111'
        });

        if (creditCards && creditCards.length > 0) {
            creditCards.forEach(k => {
                this.lstPaymentMethods.push({
                    label: k.IsAutoCharge == 'true' ? CREDITCARD_AUTO_CONST + k.CreditCardTypeLiteral + CREDTCARD_HIDDEN_NUMBER + k.CreditCardLast4Digits + CREDITCARD_EXP_CONST + `${k.ExpirationMonth < 10 ? '0' + k.ExpirationMonth : k.ExpirationMonth}/${k.ExpirationYear.slice(2)}` :
                        k.CreditCardTypeLiteral + CREDTCARD_HIDDEN_NUMBER + k.CreditCardLast4Digits + CREDITCARD_EXP_CONST + `${k.ExpirationMonth < 10 ? '0' + k.ExpirationMonth : k.ExpirationMonth}/${k.ExpirationYear.slice(2)}`,
                    value: k.CreditCardKey
                });
                if (k.IsAutoCharge == 'true') {
                    this.paymentMethod = k.CreditCardKey;
                }
                k.ExpirationDate = `${k.ExpirationMonth < 10 ? '0' + k.ExpirationMonth : k.ExpirationMonth}/${k.ExpirationYear.slice(2)}`;
            })
        }

        this.selectedShippingMethod = '1';
        if (this.orderDetailsResponse.CurrentQueue.toUpperCase() === 'FINANCE') {
            this.checkCode = true;
        }
    }

    handleDateChange(event) {
        this.dateChangedEvent = true;
        this.orderReleaseDate = event.detail.datevalue;
        if (event.detail.datevalue !== '') {
            let selectedReleaseDate = event.detail.datevalue;
            const [year, month, day] = selectedReleaseDate.split('-');
            this.selectedDate = month + '/' + day + '/' + year;
        }
    }

    handleShippingChange(event) {
        this.selectedShippingMethod = event.target.value;
    }

    updateShippingAddress(event) {
        if (event.detail) {
            this.selectedShippingAddress = event.detail.ShippingAddress;
        }
    }

    handleNewClick() {
        if (this.createAndEditCreditCardComponentMode !== 'edit') {
            this.createAndEditCreditCardComponentMode = 'new';
        }
        this.isCreateAndEditCreditCardVisible = true;
    }

    handleEditClick() {
        this.isCreateAndEditCreditCardVisible = false;
        this.createAndEditCreditCardComponentMode = '';

        if (this.paymentMethod != '111' && this.paymentMethod != null
            && this.paymentMethod != undefined) {
            this.createAndEditCreditCardComponentMode = 'edit'
            this.selectedRowData = null;
            this.selectedRowData = this.pharmacydemodetails.CreditCardsDetail.CreditCard.find(k => k.CreditCardKey === this.paymentMethod);
            this.isCreateAndEditCreditCardVisible = true;
        } else {
            this.displayToastEvent(EDIT_CARD_ERROR, 'warning', 'warning!');
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

    //NEW/EDIT CARD
    getKey(e) {
        if (window.event)
            return window.event.keyCode;
        else if (e)
            return e.which;
        else
            return null;
    }

    callGetMemberService() {
        getMemberDetails({
            memID: this.enterpriseId,
            networkId: this.networkId,
            sRecordId: this.recordId
        }).
            then(data => {
                if (data) {
                    let response = JSON.parse(data);
                    this.pharmacydemodetails = response.objPharDemographicDetails;
                    this.initializeData();
                    this.updateCreditCards();
                }
            }).catch(e => {
                console.log('error ' + e);
            })
    }


    updateCreditCards() {
        let message = { messageDetails: this.pharmacydemodetails.CreditCardsDetail, MessageName: "UpdateCreditCards" };
        publish(this.messageContext, humanaPharmacyLMS, message);
    }

    handlePaymentChange(event) {
        this.paymentMethod = event.target.value;
    }

    handleDeleteScriptKey(event) {
        this.deleteItemEvent = true;
        const scriptObject = {};
        scriptObject.scriptKey = event.detail.deletescriptkey;
        scriptObject.consentStatus = 'CR';
        this.deletedItems.push(scriptObject);
    }

    handleSave() {
        this.isCreateAndEditCreditCardVisible = false;
        this.createAndEditCreditCardComponentMode = '';
        this.callGetMemberService();
    }
    handleCancel() {
        this.isCreateAndEditCreditCardVisible = false;
        this.createAndEditCreditCardComponentMode = '';
    }

    preparePrescriptionScript() {
        this.createOrderPrescriptionItems = [];
        this.numberOfApprovedConsents = 0;
        if (this.deleteItemEvent) {
            this.createOrderPrescriptionItems = this.deletedItems;
        }
        else {
            if (this.orderDetailPrescriptions && this.orderDetailPrescriptions.length > 0) {
                this.orderDetailPrescriptions.forEach(h => {
                    const scriptObject = {};
                    scriptObject.scriptKey = h.RXNumber;
                    if (this.commercialUser != undefined && this.commercialUser != null && this.commercialUser == 'false') {
                        let d = new Date();
                        scriptObject.consentDateTime = (d.getMonth() + 1).toString() + "/" + d.getDate().toString() + "/" + d.getFullYear().toString();
                    }
                    else {
                        if (this.orderDetailsResponse.CurrentQueue.toUpperCase() != 'FINANCE') {
                            if (h.Consent === 'CA') {
                                scriptObject.consentStatus = 'CA';
                                let d = new Date();
                                scriptObject.consentDateTime = (d.getMonth() + 1).toString() + "/" + d.getDate().toString() + "/" + d.getFullYear().toString();
                                this.numberOfApprovedConsents = this.numberOfApprovedConsents + 1;
                            } else if (h.Consent === 'CR') {
                                scriptObject.consentStatus = 'CR';
                            } else {
                                scriptObject.consentStatus = h.Consent;
                            }
                        }
                    }
                    this.createOrderPrescriptionItems.push(scriptObject);
                })
            }
        }
    }

    @api saveeditorder() {
        this.editOrder();
    }

    editOrder() {
        let moveToRouting = {};
        moveToRouting.orderKey = [];
        let PaymentMethods = {};
        let paymentType = [];
        let creditcard = {};
        const request = {};
        let scriptItem = {};
        let selectedReleaseDate;
        if (this.dateChangedEvent) {
            selectedReleaseDate = this.selectedDate;
        }
        else {
            selectedReleaseDate = this.orderReleaseDate;
        }
        this.preparePrescriptionScript();
        if (this.paymentMethod === '111') {
            PaymentMethods.paymentMethodType = 'Invoice';
            paymentType.push(PaymentMethods);
            creditcard = null;
        this.chkCreditCardExpiryBol= true;
        } else {
            if (this.pharmacydemodetails && this.pharmacydemodetails.CreditCardsDetail
                && this.pharmacydemodetails.CreditCardsDetail.CreditCard && this.pharmacydemodetails.CreditCardsDetail.CreditCard.length > 0) {
                PaymentMethods.paymentMethodType = 'Credit Card';
                paymentType.push(PaymentMethods);
                creditcard = this.pharmacydemodetails.CreditCardsDetail.CreditCard.find(k => k.CreditCardKey === this.paymentMethod);
          this.chkCreditCardExpiry(this.paymentMethod)
            }
        }
        scriptItem.script = this.createOrderPrescriptionItems;
        let overrideQueueConsent = false;
        if (this.orderDetailsResponse.CurrentQueue === 'FINANCE') {
            overrideQueueConsent = true;
            request.CreateAndEditOrderRequest = new CreateOrderDTO(this.enterpriseId, this.networkId, this.orderdetails.OrderNumber, this.orderReleaseDate,
                '', moveToRouting, '', this.selectedShippingAddress, paymentType, creditcard,
                scriptItem, this.selectedShippingMethod, overrideQueueConsent);
        }
        else {
            request.CreateAndEditOrderRequest = new CreateOrderDTO(this.enterpriseId, this.networkId, this.orderdetails.OrderNumber, this.orderReleaseDate,
                '', moveToRouting, '', this.selectedShippingAddress, paymentType, creditcard,
                scriptItem, this.selectedShippingMethod, overrideQueueConsent);
        }

        this.loaded = false;
    if(this.chkCreditCardExpiryBol){
        invokecreateEditOrderDetail({ createEditObj: JSON.stringify(request), sRecordId: this.recordId })
            .then(result => {
                if (result != null && result != undefined) {
                    if (result.includes('orderKey')) {
                        let response = JSON.parse(result);
                        if (response && response.orderKey) {
                            this.displayToastEvent('Order Number : ' + response.orderKey, 'success', 'success!');

                            this.callGetMemberService();
                            this.sendLogNotes();
                            this.loaded = true;
                            this.dispatchEvent(new CustomEvent('ordereditsuccess'));
                        }
                    }
                    else if (result.includes(ADDERROR)) {
                        this.displayToastEvent(this.label.poboxerror, 'error', 'Error!');
                        this.loaded = true;
                    } else {
                        this.displayToastEvent(this.label.ordernotprocessederror, 'error', 'Error!');
                        this.loaded = true;
                    }
                }
                else {
                    this.displayToastEvent(this.label.ordernotprocessederror, 'error', 'Error!');
                    this.loaded = true;
                }
            }).catch(error => {
                console.log('Create Order Error : ' + error);
                this.displayToastEvent(this.label.ordernotprocessederror, 'error', 'Error!');
                this.loaded = true;
            });
    }
      else{
        this.displayToastEvent(this.label.ExpiredCreditCardErr, 'error', 'Error!')
        this.loaded = true;
      }
  }
  chkCreditCardExpiry(selectedCreditCard) {
    let date = new Date();
    let currentmonth = date.getMonth() + 1;
    let currentyear = date.getFullYear();
    if (this.creditCardData  != null) {
        this.creditCardData.CreditCard.forEach(h => {
          if (selectedCreditCard == h.CreditCardKey) {
            if ((h.ExpirationMonth >= currentmonth && h.ExpirationYear >= currentyear) || (h.ExpirationMonth < currentmonth && h.ExpirationYear > currentyear)) {
              this.chkCreditCardExpiryBol= true
            } else
            this.chkCreditCardExpiryBol= false
            }
         })
      }
    }

    sendLogNotes() {
        sendLogNotesToEPost({ numberOfConsentGiven: this.numberOfApprovedConsents, sMemID: this.enterpriseId, networkID: this.networkId, sRecordId: this.recordId })
            .then(result => {
                if (result == null || result == undefined) {
                    this.displayToastEvent(LOG_NOTE_ERROR, 'error', 'Error!');
                }
            }).catch(error => {
                this.displayToastEvent(LOG_NOTE_ERROR, 'error', 'Error!');
            });
    }

    trackingNumberPopUp() {
        let rowData = this.orderDetailsResponse.Carrier;
        if (rowData != '' && rowData != 'N/A') {
            let xml = this.orderDetailsResponse.ShippingTrackingNumber;
            if (xml != null) {
                if (rowData.toUpperCase() == 'UPS') {
                    window.open("https://wwwapps.ups.com/tracking/tracking.cgi?tracknum=" + xml);
                }
                else if (rowData.toUpperCase() == 'FEDEX') {
                    window.open("https://www.fedex.com/fedextrack/?trknbr=" + xml);
                }
                else if (rowData.toUpperCase() == 'USPS') {
                    window.open("https://tools.usps.com/go/TrackConfirmAction.action?tLabels=" + xml);
                }
            }
        }
    }
}