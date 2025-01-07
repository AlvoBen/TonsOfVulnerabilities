/*
LWC Name        : pharmacyOrderDetailsHum.js
Function        : LWC to display pharmacy order details.

Modification Log:
* Developer Name                  Date                         Description
*
* Abhishek Mangutkar              03/04/2022                   US - 3103531
* Abhishek Mangutkar              03/09/2022                   US - 3139633
* Nirmal Garg                     03/15/2022                   DF-4635,4670 Fix
* Abhishek Mangutkar              06/21/2022                   DF-5170 Fix
* Swapnali Sonawane               12/05/2022                   US- 3969790 Migration of the order queue detail capability
****************************************************************************************************************************/

import { LightningElement, api, wire, track } from 'lwc';

import invokeGetOrder from '@salesforce/apexContinuation/Pharmacy_LC_HUM.getOrderData';
import { publish, MessageContext, subscribe } from 'lightning/messageService';
import messageChannel from '@salesforce/messageChannel/pharmacyLMSChannel__c';
import humanaPharmacyLMS from '@salesforce/messageChannel/pharmacyLMSChannel__c';
import { getButtonLayout } from './layoutConfig';
import sendRequestReleaseAndCancelOrder from '@salesforce/apexContinuation/PharmacyOrderDetail_LC_HUM.sendRequestReleaseAndCancelOrder';
import { CreateAndEditOrderRequest_DTO, MoveToRouting_DTO, ScriptObject_DTO } from './currentqueuecreateorderrequest';
import { ShowToastEvent } from "lightning/platformShowToastEvent";
import FinanceQueueCode_BalanceCard from "@salesforce/label/c.FinanceQueueCode_BalanceCard";
import FinanceQueueCode_CreditCard from "@salesforce/label/c.FinanceQueueCode_CreditCard";
import REQEST_RELEASE_CANCEL_ORDER_ERROR from "@salesforce/label/c.REQEST_RELEASE_CANCEL_ORDER_ERROR";
import EDIT_ORDER_CLOSE_MESAGE from "@salesforce/label/c.EDIT_ORDER_CLOSE_MESAGE";
import EDIT_ORDER_CANCEL_MESAGE from "@salesforce/label/c.EDIT_ORDER_CANCEL_MESAGE";

const BUTTON_RELEASE = 'Release';
const BUTTON_EDIT = 'Edit';
const BUTTON_CANCEL = 'Cancel';
const QUEUE_FINANCE = 'FINANCE';
const QUEUE_MEMBER_CONSENT = 'MEMBER CONSENT';
const LABLE_CREDITCARD = FinanceQueueCode_CreditCard;
const LABEL_BALANCECARD = FinanceQueueCode_BalanceCard;

export default class PharmacyOrderDetailsHum extends LightningElement {



    @api orderdetails;
    @api orderdetailedresponse;
    @api allorderdetails;
    orderDetailPrescriptions;
    orderDetailsResponse = {};
    checkCode = false;
    orderLineItems = [];

    @api recordid;
    @api omsdetails;
    @api payer;

    capType;
    orderparentnode;
    switchShippmentMethod;
    webOrder = '';
    loaded = false;
    splitOrderNumber;
    @api enterpriseid;
    @api networkid;
    showCurrentQueueButtons = true;
    showReleasePopUpModal = false;
    releaseButtonClicked = false;
    cancelButtonClicked = false;
    editButtonClicked = false;
    showEditPopUpModal = false;
    modalTitle;
    buttonsConfig = [{
        text: 'No',
        isTypeBrand: false,
        eventName: 'no'
    }, {
        text: 'Yes',
        isTypeBrand: true,
        eventName: 'yes'
    }];
    labels = { REQEST_RELEASE_CANCEL_ORDER_ERROR }
    @api pharmacydemographicdetails;

    QueueData = [];
    @api prescriptions;
    @track isQueueAvailable = false;

    @api setDemographicsDetails(data) {
        this.pharmacydemographicdetails = data;
        if (this.template.querySelector('c-pharmacy-order-details-edit-hum') != null) {
            this.template.querySelector('c-pharmacy-order-details-edit-hum').setDemographicsDetails(this.pharmacydemographicdetails);
        }
    }

    connectedCallback() {

        let OCPList = (this.orderdetails && this.orderdetails?.OCP) ? this.orderdetails.OCP.split(',') : [];
        OCPList.forEach(o => {
            if (null != o) {
                this.webOrder += ' ' + o + '';
            }
        })
        this.getOrderDetails();


    }


    isEmptyObj(obj) {
        return Object.keys(obj).length === 0;
    }
    backToOrderSummaryPage(event) {

        this.splitOrderNumber = '';
        this.orderdetails = '';
        this.dispatchEvent(new CustomEvent('backclick'));
    }

    getOrderDetails() {
        this.loaded = false
        let orderkey;
        if (this.splitOrderNumber) {
            orderkey = this.splitOrderNumber;
        }
        else {
            orderkey = this.orderdetails.OrderNumber;
        }
        invokeGetOrder({ orderKeyValue: orderkey, source: this.orderdetails.OrderSource })

            .then(result => {
                let response = JSON.parse(result);
                this.orderparentnode = response.objParentOrder;
                let orderparentnode = response.objParentOrder;
                this.createOrderList(orderparentnode);
                this.createQueueList(orderparentnode);
                let lstScriptKey = '';
                if (orderparentnode != null && orderparentnode != undefined && orderparentnode.OrderItems != null && orderparentnode.OrderItems != undefined) {
                    for (var i = 0; i < orderparentnode.OrderItems.length; i++) {
                        let itemRow = orderparentnode.OrderItems[i];
                        if (itemRow != null && itemRow != undefined) {
                            if (itemRow.ScriptKey != null && itemRow.ScriptKey != undefined) {
                                lstScriptKey = lstScriptKey + itemRow.ScriptKey + ',';
                            }
                        }
                    }
                }

                this.getOrderPrescription(lstScriptKey);
                this.capType = this.pharmacydemographicdetails?.CapType;
                this.fetchButtonLayout();

            })
            .catch(err => {
                console.log("Error occured - " + err);
            });

    }

    getOrderPrescription(lstScriptKey) {
        if (lstScriptKey) {

            let orderkey = lstScriptKey.split(',');
            let tmp = [];
            if (this.prescriptions.length > 0) {
                this.prescriptions.forEach(f => {
                    orderkey.forEach(o => {
                        if (null != o && f.RXNumber === o && !tmp.includes(f)) {
                            tmp.push(f);
                        }
                    })
                })
            }
            this.orderItemPrescriptions = tmp.length > 0 ? tmp : [];
            tmp = [];
            this.orderDetailPrescriptions = this.orderItemPrescriptions;
        }
    }

    createQueueList(orderDetailNode) {
        if (orderDetailNode) {
            if (orderDetailNode?.Queues?.Queue?.length > 0) {
                orderDetailNode.Queues.Queue.forEach(k => {
                    let completionDate;
                    let queueDate = k.QueueDate;
                    if (queueDate) {
                        completionDate = queueDate.substr(5, 2) + "/" + queueDate.substr(8, 2) + "/" + queueDate.substr(0, 4);
                        let hours = queueDate.substr(11, 2);
                        let minutes = queueDate.substr(14, 2);
                        if (hours > 11) {
                            if (hours > 12) hours = hours - 12;
                            completionDate = completionDate + " " + hours + ":" + minutes + " PM";
                        }
                        else {
                            completionDate = completionDate + " " + hours + ":" + minutes + " AM";
                        }
                    }

                    this.QueueData.push({
                        Id: this.getUniqueId(),
                        QueueName: k.QueueName,
                        QueueDate: completionDate,
                        QueueUser: k.QueueUser,
                        cellBorder: 'slds-border_right'
                    });
                })
                this.QueueData.sort(function (a, b) {
                    let dateA = new Date(a.QueueDate);
                    let dateB = new Date(b.QueueDate);
                    return dateA > dateB ? -1 : 1;
                });


            }
            if (orderDetailNode?.CurrentQueue == null || orderDetailNode?.CurrentQueue == undefined || orderDetailNode?.CurrentQueue == "") {
                this.QueueData.push({
                    Id: this.getUniqueId(),
                    QueueName: 'OPEN',
                    QueueDate: '',
                    QueueUser: '',
                    cellBorder: 'slds-border_right'
                });
            }
        }
        this.isQueueAvailable = this.QueueData && Array.isArray(this.QueueData) && this.QueueData.length > 0 ? true : false;
    }

    getUniqueId() {
        return Math.random().toString(16).slice(2);
    }


    createOrderList(result) {
        if (result) {
            this.loaded = true;
            let tempObj = {};
            let creditCardValue = '';
            let address = '';
            if (result.OrderKey != null && result.OrderKey != undefined) tempObj.OrderKey = result.OrderKey;
            else tempObj.OrderKey = '';
            if (result.OrderShipDate != null && result.OrderShipDate != undefined) tempObj.OrderShipDate = result.OrderShipDate;
            else tempObj.OrderShipDate = '';
            if (result.CurrentQueue != null && result.CurrentQueue != undefined) tempObj.CurrentQueue = result.CurrentQueue;
            else tempObj.CurrentQueue = '';
            if (result.CancelReasonCode != null && result.CancelReasonCode != undefined) tempObj.CancelReasonCode = result.CancelReasonCode;
            else tempObj.CancelReasonCode = '';
            if (result.CreditCards != null && result.CreditCards != undefined) {
                if (result.CreditCards.CreditCard != null && result.CreditCards.CreditCard != undefined) {
                    if (result.CreditCards.CreditCard[0].CreditCardType != '') creditCardValue = result.CreditCards.CreditCard[0].CreditCardType;
                    creditCardValue = creditCardValue + ' xxxx-';
                    if (result.CreditCards.CreditCard[0].CreditCardLast4Digits != '') creditCardValue = creditCardValue + ' ' + result.CreditCards.CreditCard[0].CreditCardLast4Digits;
                    if (result.CreditCards.CreditCard[0].ExpirationMonth != '') {
                        if (result.CreditCards.CreditCard[0].ExpirationMonth.length == 1) creditCardValue = creditCardValue + ' 0' + result.CreditCards.CreditCard[0].ExpirationMonth;
                        else creditCardValue = creditCardValue + ' ' + result.CreditCards.CreditCard[0].ExpirationMonth;
                    }
                    if (result.CreditCards.CreditCard[0].ExpirationYear != '') creditCardValue = creditCardValue + '/' + result.CreditCards.CreditCard[0].ExpirationYear.substring(2, 4);
                    tempObj.CreditCard = creditCardValue;
                }
                else tempObj.CreditCard = '';
            }
            else tempObj.CreditCard = '';
            if (result.ShippingInformation != null && result.ShippingInformation != undefined) tempObj.ShippingMethod = result.ShippingInformation.ShippingMethod;
            else tempObj.ShippingMethod = '';
            if (result.ShippingInformation != null && result.ShippingInformation != undefined) tempObj.ShippingTrackingNumber = result.ShippingInformation.ShippingTrackingNumber;
            else tempObj.ShippingTrackingNumber = '';
            if (result.ShippingInformation != null && result.ShippingInformation != undefined) tempObj.Carrier = result.ShippingInformation.ShipperType;
            else tempObj.Carrier = 'N/A';
            if (result.Addresses != "" && result.Addresses != undefined) {
                if (result.Addresses.AddressLine1 != "")
                    address = result.Addresses.AddressLine1;
                if (result.Addresses.AddressLine2 != "" && address != "")
                    address = address + ' ' + result.Addresses.AddressLine2;
                if (result.Addresses.City != "" && address != "")
                    address = address + ' ' + result.Addresses.City;
                if (result.Addresses.StateCode != "" && address != "")
                    address = address + ', ' + result.Addresses.StateCode;
                if (result.Addresses.ZipCode != "" && address != "")
                    address = address + ' ' + result.Addresses.ZipCode;
                tempObj.ShippingAddress = address;
            }
            else tempObj.ShippingAddress = '';
            if (result.CurrentQueue.toUpperCase() == "FINANCE") {
                if (result.exceptionIdentifier) {
                    let finCode = result.exceptionIdentifier;

                    let fnCode = finCode.split(",");
                    tempObj.Finance = '';


                    fnCode.forEach(o => {
                        if (o) {
                            tempObj.Finance += ' ' + o + ' ,';


                        }
                    })
                    tempObj.Finance = tempObj.Finance.replace(/,$/, "");
                }
                this.checkCode = true;
            }
            else {
                tempObj.Finance = '';
            }

            this.orderDetailsResponse = tempObj;
        }
    }

    trackingNumberPopUp() {

        let carrier = this.orderDetailsResponse.Carrier;

        if (carrier != '' && carrier != 'N/A') {
            let trackingNo = this.orderDetailsResponse.ShippingTrackingNumber;


            if (trackingNo != null) {
                if (carrier.toUpperCase() == 'UPS') {
                    window.open("https://wwwapps.ups.com/tracking/tracking.cgi?tracknum=" + trackingNo);
                }
                else if (carrier.toUpperCase() == 'FEDEX') {
                    window.open("https://www.fedex.com/fedextrack/?trknbr=" + trackingNo);
                }
                else if (carrier.toUpperCase() == 'USPS') {
                    window.open("https://tools.usps.com/go/TrackConfirmAction.action?tLabels=" + trackingNo);
                }
            }
        }


    }

    getSplitOrderDetails(event) {
        if (event.detail && event.detail.OrderNumber) {
            this.splitOrderNumber = event.detail.OrderNumber;
            this.orderdetails = this.allorderdetails.find(k => k.OrderNumber === event.detail.OrderNumber);
            this.getOrderDetails();
        }
    }

    fetchButtonLayout() {

        let buttonModal = getButtonLayout();
        buttonModal.forEach(o => {
            o.visible = false;
        });
        let currentQueue = this.orderDetailsResponse?.CurrentQueue ?? null;
        let finCode = this.orderparentnode?.exceptionIdentifier ?? null;
        if (currentQueue != null && currentQueue == QUEUE_FINANCE) {
            if (finCode.includes(',')) {
                let fincodes = finCode.split(',');
                if (Array.isArray(fincodes) && fincodes.length > 0) {
                    fincodes.forEach(k => {
                        if (k && k != '' && LABLE_CREDITCARD.includes(k)) {
                            buttonModal.forEach(o => {
                                if (o.label === BUTTON_EDIT || o.label === BUTTON_CANCEL) {
                                    o.visible = true;
                                }
                            });
                        }
                        if (k != null && k != '' && LABEL_BALANCECARD.includes(k)) {
                            buttonModal.forEach(o => {
                                o.visible = true;
                            });
                        }
                    })
                }
            } else {
                if (finCode != null && finCode != '' && LABLE_CREDITCARD.includes(finCode)) {
                    buttonModal.forEach(o => {
                        if (o.label === BUTTON_EDIT || o.label === BUTTON_CANCEL) {
                            o.visible = true;
                        }
                    });
                }
                if (finCode != null && finCode != '' && LABEL_BALANCECARD.includes(finCode)) {
                    buttonModal.forEach(o => {
                        o.visible = true;
                    });
                }
            }
        }
        else if (currentQueue != null && currentQueue == QUEUE_MEMBER_CONSENT) {
            buttonModal.forEach(o => {
                if (o.label === BUTTON_EDIT || o.label === BUTTON_CANCEL) {
                    o.visible = true;
                }
            });
        }
        this.oButtonModel = buttonModal;
    }

    handleCurrentQueueButtonClick(event) {
        if (event.target.dataset.id === BUTTON_RELEASE) {
            this.modalTitle = 'Release Order';
            this.alertBoxMessage = 'You are about to Release Order #' + this.orderdetails.OrderNumber + '. \n Are you sure?';
            this.showEditPopUpModal = false;
            this.editButtonClicked = false;
            this.cancelButtonClicked = false;
            this.releaseButtonClicked = true;
            this.showReleasePopUpModal = true;
        }
        else if (event.target.dataset.id === BUTTON_CANCEL) {
            this.modalTitle = event.target.dataset.id;
            this.alertBoxMessage = 'You are about to Cancel Order #' + this.orderdetails.OrderNumber + '. \n Are you sure?';
            this.showEditPopUpModal = false;
            this.editButtonClicked = false;
            this.releaseButtonClicked = false;
            this.cancelButtonClicked = true;
            this.showReleasePopUpModal = true;
        }
        else if (event.target.dataset.id === BUTTON_EDIT) {
            this.modalTitle = 'Order Detail # ' + this.orderdetails.OrderNumber;
            this.showReleasePopUpModal = false;
            this.releaseButtonClicked = false;
            this.cancelButtonClicked = false;
            this.editButtonClicked = true;
            this.showEditPopUpModal = true;
        }
    }

    closeCancelReleasePopUpModal() {
        this.showReleasePopUpModal = false;
        if (this.editButtonClicked) {
            this.showEditPopUpModal = true;
            this.modalTitle = 'Order Detail # ' + this.orderdetails.OrderNumber;
        }
    }

    handleCancelReleaseOrder(event) {
        if (this.editButtonClicked) {
            this.showReleasePopUpModal = false;
        }
        else {
            let isCancel = false;
            if (this.releaseButtonClicked) {
                this.requestStructureRSReleaseAndCancelOrder(this.orderDetailsResponse.CurrentQueue, isCancel);
            }
            else {
                isCancel = true;
                this.requestStructureRSReleaseAndCancelOrder(this.orderDetailsResponse.CurrentQueue, isCancel);
            }
        }
    }

    requestStructureRSReleaseAndCancelOrder(currentQueue, isCancel) {
        let overrideconsent = false;
        let copayconsent = false;
        let finCode = this.orderparentnode?.exceptionIdentifier ?? null;
        let lstScriptKey = '';
        if (this.orderparentnode != null && this.orderparentnode != undefined && this.orderparentnode.OrderItems != null && this.orderparentnode.OrderItems != undefined) {
            for (let i = 0; i < this.orderparentnode.OrderItems.length; i++) {
                let itemRow = this.orderparentnode.OrderItems[i];
                if (itemRow != null && itemRow != undefined) {
                    if (itemRow.ScriptKey != null && itemRow.ScriptKey != undefined) {
                        lstScriptKey = lstScriptKey + itemRow.ScriptKey + ',';
                    }
                }
            }
            if (lstScriptKey.length > 0) {
                lstScriptKey = lstScriptKey.substring(0, lstScriptKey.length - 1);
            }
        }
        if (currentQueue != null && currentQueue != undefined && currentQueue != "" && currentQueue == "FINANCE" && finCode != null && finCode.includes("CPAY_250")) {
            overrideconsent = true;
            copayconsent = true;
        }
        else if (currentQueue != null && currentQueue != undefined && currentQueue != "" && currentQueue == "FINANCE" && finCode != null) {
            overrideconsent = true;
            copayconsent = false;
        }

        let moveToRouting = new MoveToRouting_DTO(this.orderdetails.OrderNumber);
        const request = new CreateAndEditOrderRequest_DTO(this.orderdetails.OrderNumber,
            this.enterpriseid, isCancel, this.networkid, overrideconsent, copayconsent, lstScriptKey, moveToRouting);

        sendRequestReleaseAndCancelOrder({ createEditObj: JSON.stringify(request), sRecordId: this.recordid })
            .then(result => {
                this.closeCancelReleasePopUpModal();
                this.getOrderDetails();
            })
            .catch(err => {
                console.log(err);
                this.showToast('Error!', this.labels.REQEST_RELEASE_CANCEL_ORDER_ERROR, 'error');
                this.closeCancelReleasePopUpModal();
            });
    }

    handleOrderEditSuccess() {
        this.showEditPopUpModal = false;
        this.getOrderDetails();
    }

    closeEditOrderModal() {
        this.showEditPopUpModal = false;
        this.modalTitle = 'Close Edit Order';
        this.alertBoxMessage = EDIT_ORDER_CLOSE_MESAGE;
        this.editButtonClicked = true;
        this.showReleasePopUpModal = true;

    }

    cancelEditOrderModal() {
        this.showEditPopUpModal = false;
        this.modalTitle = 'Cancel Edit Order';
        this.alertBoxMessage = EDIT_ORDER_CANCEL_MESAGE;
        this.editButtonClicked = true;
        this.showReleasePopUpModal = true;
    }

    handleSaveEditOrder() {
        this.template.querySelector('c-pharmacy-order-details-edit-hum').saveeditorder();
    }

}