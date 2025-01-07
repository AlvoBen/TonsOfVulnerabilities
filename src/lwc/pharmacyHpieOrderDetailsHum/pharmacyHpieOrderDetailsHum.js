/*
Function        : LWC to display demographics details .

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Nirmal Garg                    08/25/2023                    Initial version
* Swapnali Sonawane              10/23/2023                    US - 5058187 Pharmacy Edit Order
* Jonathan Dickinson             02/28/2024                    User Story 5394128: T1PRJ1295995 - (T1PRJ0870026) - MF 29256 - Mail Order Pharmacy- Order summary : HPIE updates for split order
* Vishal Shinde                  02/29/2024                    US - HPIE Mail Order Management - Pharmacy - "Prescriptions & Order summary" tab - Finance Queue /Member consent
* Jonathan Dickinson             03/01/2024                    User Story 5058187: T1PRJ1295995 - (T1PRJ0870026)- MF 27409 HPIE/CRM SF - Tech - C12 Mail Order Management - Pharmacy - "Prescriptions & Order summary" tab - "Edit Order'
* Jonathan Dickinson             02/29/2024                    User Story 4835317: T1PRJ0865978 - MF 27481 - C12; Mail Order Management; Pharmacy - Mail Order Pharmacy page gap- Member Consent (Lightning)
*****************************************************************************************************************************/
import { LightningElement, api, wire, track } from 'lwc';
import { getTasks,getOrderDetailsData, updateOrders, releaseOrders } from 'c/pharmacyHPIEIntegrationHum';
import { getFormatDate } from 'c/crmUtilityHum'; 
import { getButtonLayout } from './layoutConfig';
import FinanceQueueCode_BalanceCard from "@salesforce/label/c.FinanceQueueCode_BalanceCard";
import FinanceQueueCode_CreditCard from "@salesforce/label/c.FinanceQueueCode_CreditCard";
import EDIT_ORDER_CLOSE_MESAGE from "@salesforce/label/c.EDIT_ORDER_CLOSE_MESAGE";
import EDIT_ORDER_CANCEL_MESAGE from "@salesforce/label/c.EDIT_ORDER_CANCEL_MESAGE";
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import {CancelOrderRequest_DTO} from './cancelOrderRequestStrucuture';  
import {ReleaseOrderRequest_DTO} from './releaseOrderRequestStrucuture';
import REQEST_RELEASE_CANCEL_ORDER_ERROR from "@salesforce/label/c.REQEST_RELEASE_CANCEL_ORDER_ERROR";
const BUTTON_RELEASE = 'Release';
const BUTTON_EDIT = 'Edit';
const BUTTON_CANCEL = 'Cancel';
const QUEUE_FINANCE = 'FINANCE';
const QUEUE_MEMBER_CONSENT = 'MEMBER CONSENT';
const LABLE_CREDITCARD = FinanceQueueCode_CreditCard;
const LABEL_BALANCECARD = FinanceQueueCode_BalanceCard;
const INVOICE_DESC = 'BILL PAYMENT LATER';
const CARD_DESC = 'PAYMENT CARD';
const INVOICE_PAY_METHOD = 'xxxx- 00/';

export default class PharmacyHpieOrderDetailsHum extends LightningElement {
    @api enterpriseId;
    @api userId;
    @api organization;
    @api prescriptions;
    @api recordId;
    @api demographicsDetails;
    @api orderTasks;
    @api capType;
    @api memberConsent;
    @api accData;
    @api payer;
    @api financeDetails;
    @api selectedOrder;
    @api isMemberConsentRequired = false;
    @track orderDetailsData = {};
    orderDetailsResponse ={};
    @track loaded = false;
    @track checkCode = false;
    @track showCurrentQueueButtons = false;
    @track shippingAddress;
    @track shippingHomeStreet;
    @track orderDetails;
    splitOrderNumber;
    modalTitle;
    showReleasePopUpModal = false;
    releaseButtonClicked = false;
    cancelButtonClicked = false;
    editButtonClicked = false;
    showEditPopUpModal = false;
    pharmacyDemoDetails;
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

    connectedCallback() {
        this.pharmacyDemoDetails = JSON.stringify(this.demographicsDetails?.Addresses);
        this.getOrderDetails();
    }


    @api setFinanceDetails(data) {
        this.financeDetails = data;
    }

    @api setDemographicsDetails(data) {
        this.demographicsDetails = data;
        this.pharmacyDemoDetails = JSON.stringify(this.demographicsDetails?.Addresses);
    }

    getOrderDetails() { 
        this.loaded = false;
        let orderkey;
        if (this.splitOrderNumber) {
            orderkey = this.splitOrderNumber;
        }
        else {
            orderkey = this.selectedOrder.orderId;
        }

        getOrderDetailsData(this.enterpriseId, this.userId, this.organization ?? 'HUMANA', orderkey)
        .then(result => {
            if (result?.order) {
                this.orderDetails = JSON.parse(JSON.stringify(result.order));
                this.processOrderDetails();
            }
            this.loaded = true;
        })
        .catch(err => {
            this.loaded = true;
            console.log("Error occured - " + err);
        });
    }

    processOrderDetails() {
        this.orderDetailsData = {
            orderId: this.orderDetails?.orderId ?? '',
            shippingMethod: this.orderDetails?.shipping?.method?.description ?? '',
            status: this.orderDetails?.status?.description ?? '',
            carrier: this.orderDetails?.shipping?.shipperType ?? '',
            trackingNumber: this.orderDetails?.shipping?.trackingNumber ?? '',
            cancelReason: this.orderDetails?.status?.reason?.description ?? '',
            shippingAddress: this.orderDetails?.shipping?.address?.key ? this.getShippingAddress(this.orderDetails?.shipping?.address?.key) : '',
            createdDate: this.orderDetails?.createdDate ? getFormatDate(this.orderDetails?.createdDate) : '',
            releaseDate: this.orderDetails?.releaseDate ?? '', 
            finishedDate: this.orderDetails?.shippedDate ? getFormatDate(this.orderDetails?.shippedDate) : '',
            paymentMethod: this.getPaymentMethodString(),
            paymentCardkey: this.orderDetails?.billing?.payment?.card?.key ?? '',
            paymentCardDescription: this.orderDetails?.billing?.payment?.method?.description ?? '',
            otc: this.orderDetails?.z0type?.description?.toUpperCase() === 'OTC' ? 'Yes' : 'No',
            source: this.orderDetails?.source ?? '',
            orderTotal: this.getOrderTotalAmount(this.orderDetails),
            copayAmount: this.getCopayTotalAmount(this.orderDetails),
            taxAmount: this.getTaxTotalAmount(this.orderDetails),
            orderPrescriptions: this.getOrderPrescriptions(this.orderDetails),
            currentQueue: this.getCurrentQueue(this.orderTasks),
            financeCode: this.getFinanceCode(this.orderTasks)
        }

        this.orderDetailsResponse = this.orderDetailsData;
        this.checkCode = this.orderTasks && Object.keys(this.orderTasks)?.length > 0
            && this.orderTasks?.task?.name?.toUpperCase() === 'FINANCE' ? true : false;
        this.fetchButtonLayout();
        this.loaded = true;
    }

    getPaymentMethodString() {
        if (this.orderDetails?.billing?.payment?.method?.description) {
            if (this.orderDetails.billing.payment.method.description === INVOICE_DESC) {
                return INVOICE_PAY_METHOD;
            } else if (this.orderDetails.billing.payment.method.description === CARD_DESC) {
                if (this.orderDetails.billing.payment?.card?.key) {
                    let orderCard = this.financeDetails.paymentCards.find(card => card.key === this.orderDetails.billing.payment.card.key);
                    if (orderCard) {
                        return `${orderCard.z0type.description.charAt(0).toUpperCase()} xxxx- ${orderCard.tokenKey.slice(-4)} ${orderCard.expirationMonth.toString().padStart(2, '0')}/${orderCard?.expirationYear.toString().slice(-2)}`;
                    }
                }
            }
        }
        
        return '';
    }

    getSplitOrderDetails(event) {
        if (event?.detail?.OrderNumber) {
            this.splitOrderNumber = event.detail.OrderNumber;
            this.getOrderDetails();
        }
    }

    fetchButtonLayout() {
        let buttonModal = getButtonLayout();
        buttonModal = buttonModal.map((item) => ({
            ...item,
            visible: false
        }))
        let currentQueue = this.orderDetailsData?.currentQueue ?? null;
        let finCode = this.orderDetailsData?.financeCode ?? null;
        if (currentQueue && currentQueue?.length > 0 && currentQueue == QUEUE_FINANCE) {
            if (finCode.includes(',')) {
                let fincodes = finCode.split(',');
                if (Array.isArray(fincodes) && fincodes.length > 0) {
                    fincodes.forEach(k => {
                        if (k && k?.length > 0 && LABLE_CREDITCARD.includes(k)) {
                            buttonModal = buttonModal.map((item) => ({
                                ...item,
                                visible: item?.label === BUTTON_EDIT || item?.label === BUTTON_CANCEL ? true : false
                            }))
                        }
                        if (k && k?.length > 0 && LABEL_BALANCECARD.includes(k)) {
                            buttonModal = buttonModal.map((item) => ({
                                ...item,
                                visible: true
                            }))
                        }
                    })
                }
            } else {
                if (finCode && finCode?.length > 0 && LABLE_CREDITCARD.includes(finCode)) {
                    buttonModal = buttonModal.map((item) => ({
                        ...item,
                        visible: item?.label === BUTTON_EDIT || item?.label === BUTTON_CANCEL ? true : false
                    }))
                }
                if (finCode && finCode?.length > 0 && LABEL_BALANCECARD.includes(finCode)) {
                    buttonModal = buttonModal.map((item) => ({
                        ...item,
                        visible: true
                    }))
                }
            }
        }
        else if (currentQueue != null && currentQueue == QUEUE_MEMBER_CONSENT) {
            buttonModal = buttonModal.map((item) => ({
                ...item,
                visible: (this.isMemberConsentRequired && item?.label === BUTTON_EDIT) || item?.label === BUTTON_CANCEL ? true : false
            }))
        }
        this.oButtonModel = buttonModal;
        this.showCurrentQueueButtons = true;
    }

    getCurrentQueue(tasks) {
        let currentQueue = '';
        if (tasks && Object.keys(tasks)?.length > 0) {
            currentQueue = tasks?.task?.name;
        }
        return currentQueue;
    }

    getFinanceCode(tasks) {
        let financeCodes = [];
        if (tasks && Object.keys(tasks)?.length > 0) {
            if (tasks?.task?.code && tasks?.task?.name?.toUpperCase() === 'FINANCE') {
                financeCodes.push(tasks?.task?.code);
            }
            if (tasks?.lines && Array.isArray(tasks?.lines) && tasks?.lines?.length > 0) {
                tasks?.lines.forEach(k => {
                    if (financeCodes?.findIndex(h => h === k?.task?.code) < 0
                        && k?.task?.name?.toUpperCase() === 'FINANCE') {
                        financeCodes.push(k?.task?.code)
                    }
                })
            }
        }
        return financeCodes?.length > 0 ? financeCodes.join(',') : '';
    }

    getShippingAddress(addressKey) {
        this.shippingAddress = this.demographicsDetails && this.demographicsDetails?.Addresses
            && Array.isArray(this.demographicsDetails?.Addresses) && this.demographicsDetails?.Addresses?.length > 0
            ? this.demographicsDetails?.Addresses?.find(k => k?.key == addressKey) : '';
        this.shippingHomeStreet = `${this.shippingAddress?.addressLine1 ?? ''} ${this.shippingAddress?.addressLine2 ?? ''}`;
        return this.shippingAddress;
    }

    getOrderPrescriptions(order) {
        let prescriptions = [];
        if (order && order?.lines && Array.isArray(order?.lines) && order?.lines?.length > 0) {
            order?.lines?.forEach(k => {
                let prescription = this.prescriptions.find(h => h?.key == k?.product.prescription?.prescriptionKey);
                prescriptions.push({
                    key: k?.product?.prescription?.prescriptionKey ?? '',
                    daysSupply: prescription?.daysSupply ?? '',
                    refillsRemaining: prescription?.refillsRemaining ?? '',
                    status: k?.status?.description ?? '',
                    quantity: k?.product?.quantity ?? '',
                    dispensedDrug: k?.product?.name ?? '',
                    writtenDrug: prescription?.product?.drug?.label ?? '',
                    changedOrder: k?.changedOrder ?? null,
                    icon: prescription?.icon ?? null,
                    copayConsentGiven : prescription?.copay?.copayConsentGiven ?? false,
                    rxConsent: prescription?.consent?.status?.code ?? ''
                })
            })
        }
        return prescriptions;
    }

    getCopayTotalAmount(order) {
        let copayTotal = 0;
        if (order && order?.lines && Array.isArray(order?.lines) && order?.lines?.length > 0) {
            order?.lines?.forEach(k => {
                copayTotal += ((k?.cost?.copayCost?.amount ?? 0));
            })
        }
        return copayTotal + (order?.shipping?.cost?.amount ?? 0);
    }

    getTaxTotalAmount(order) {
        let taxTotal = 0;
        if (order && order?.lines && Array.isArray(order?.lines) && order?.lines?.length > 0) {
            order?.lines?.forEach(k => {
                taxTotal += ((k?.cost?.copayCost?.taxAmount ?? 0) + (k?.cost?.planCost?.taxAmount ?? 0));
            })
        }
        return taxTotal + (order?.shipping?.cost?.taxAmount ?? 0);
    }

    getOrderTotalAmount(order) {
        let orderTotal = 0;
        if (order && order?.lines && Array.isArray(order?.lines) && order?.lines?.length > 0) {
            order?.lines?.forEach(k => {
                orderTotal += ((k?.cost?.copayCost?.amount ?? 0) + (k?.cost?.copayCost?.taxAmount ?? 0)
                    + (k?.cost?.planCost?.amount ?? 0) + (k?.cost?.planCost?.taxAmount ?? 0));
            })
        }
        return orderTotal;
    }

    backToOrderSummaryPage(event) {
        this.splitOrderNumber = '';
        this.orderdetails = '';
        this.dispatchEvent(new CustomEvent('backclick'));
    }

    trackingNumberPopUp() {
        let carrier = this.orderDetailsData.carrier ?? '';
        if (carrier && carrier?.length > 0 && carrier != 'N/A') {
            let trackingNo = this.orderDetailsData.trackingNumber;
            if (trackingNo && trackingNo?.length > 0) {
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

    handleCurrentQueueRefresh() {
        this.loaded = false;
        getTasks(this.enterpriseId, this.userId, this.organization ?? 'HUMANA')
            .then(result => {
                if (result) {
                    this.orderTasks = result && result?.tasks && Array.isArray(result?.tasks)
                        && result?.tasks?.length > 0
                        ? result?.tasks?.find(k => k?.orderId === this.orderDetails?.orderId) : null;
                    this.processOrderDetails();
                }
                this.loaded = true;
            }).catch(error => {
                console.log(error);
                this.loaded = true;
            })
    }
    handleCurrentQueueButtonClick(event) {
        if (event.target.dataset.id === BUTTON_RELEASE) {
            this.modalTitle = 'Release Order';
            this.alertBoxMessage = 'You are about to Release Order #' + this.orderDetails?.orderId  + '. \n Are you sure?';
            this.showEditPopUpModal = false;
            this.editButtonClicked = false;
            this.cancelButtonClicked = false;
            this.releaseButtonClicked = true;
            this.showReleasePopUpModal = true;
            
        }
        else if (event.target.dataset.id === BUTTON_CANCEL) {
            this.modalTitle = event.target.dataset.id;
            this.alertBoxMessage = 'You are about to Cancel Order #' + this.orderDetails?.orderId  + '. \n Are you sure?';
            this.showEditPopUpModal = false;
            this.editButtonClicked = false;
            this.releaseButtonClicked = false;
            this.cancelButtonClicked = true;
            this.showReleasePopUpModal = true;
        }
        else if (event.target.dataset.id === BUTTON_EDIT) {
            this.modalTitle = 'Order Detail # ' + this.orderDetails?.orderId ;
            this.showReleasePopUpModal = false;
            this.releaseButtonClicked = false;
            this.cancelButtonClicked = false;
            this.editButtonClicked = true;
            this.showEditPopUpModal = true;
        }
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
    closeCancelReleasePopUpModal() {
        this.showReleasePopUpModal = false;
        if (this.editButtonClicked) {
            this.showEditPopUpModal = true;
            this.modalTitle = 'Order Detail # ' +  this.orderDetails?.orderId;
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
    
    handleCancelReleaseOrder(event) {
        if (this.editButtonClicked) {
            this.showReleasePopUpModal = false;
        }
        else {
            if (this.releaseButtonClicked) {
                this.requestStructureReleaseOrder(this.orderDetailsResponse?.currentQueue);
            }
            else {
                this.requestStructureCancelOrder();
            }
        }
    }

    requestStructureCancelOrder(){
         const request = new CancelOrderRequest_DTO(this.orderDetails?.orderId,this.enterpriseId,this.userId)
         updateOrders(JSON.stringify(request))
         .then(result => {
             let ResponseResult= result;
             this.dispatchEvent(new ShowToastEvent({
                 title: 'Success!',
                 message: 'Successfully order Cancelled',
                 variant: 'success',
               }));
             this.handleCurrentQueueRefresh();
             this.closeCancelReleasePopUpModal();
             this.dispatchEvent(new CustomEvent("gotoparentcomponent")); 
            this.getOrderDetails();
         })
         .catch(error => {
             console.log(error);
             this.displayToastEvent(this.labels.REQEST_RELEASE_CANCEL_ORDER_ERROR, 'error', 'Error!')
             this.closeCancelReleasePopUpModal();
         });
     } 
     
     requestStructureReleaseOrder(currentQueue){  
         let releaseOrdercheck = false;
         let copayconsent = false;
         let finCode = this.orderDetailsData?.financeCode ?? null; 
         let datakey= this.orderDetailsData?.paymentCardkey; 
         let paymentDescription=this.orderDetailsData?.paymentCardDescription;
         let arrayPrescription =this.orderDetailsData?.orderPrescriptions;
         if(currentQueue != null && currentQueue != undefined && currentQueue != "" && currentQueue == "FINANCE" && finCode != null && finCode.includes("CPAY_250")) {
            releaseOrdercheck = true;
             copayconsent = true;
         }
         else if(currentQueue != null && currentQueue != undefined && currentQueue != "" && currentQueue == "FINANCE" && finCode != null) {
            releaseOrdercheck = false;
             copayconsent = false;
         }   
 
         const request = new ReleaseOrderRequest_DTO(this.orderDetails?.orderId,this.enterpriseId,this.userId,copayconsent,arrayPrescription,datakey,paymentDescription);
         releaseOrders(JSON.stringify(request),releaseOrdercheck)
         .then(result => {
             this.dispatchEvent(new ShowToastEvent({  
                 title: 'Success!',
                 message: 'Successfully Release the order', 
                 variant: 'success',
             }));
             this.handleCurrentQueueRefresh();
             this.closeCancelReleasePopUpModal();  
             this.getOrderDetails();
             this.dispatchEvent(new CustomEvent("gotoparentcomponent"));
         })
         .catch(err => {
             console.log(err);
             this.displayToastEvent(this.labels.REQEST_RELEASE_CANCEL_ORDER_ERROR, 'error', 'Error!')
             this.closeCancelReleasePopUpModal();
         }); 
    }

    handleSaveEditOrder() {
        this.template.querySelector('c-pharmacy-hpie-order-details-edit-hum').saveeditorder();
    }

    handleOrderEditSuccess() {
        this.showEditPopUpModal = false; 
        this.handleCurrentQueueRefresh(); 
        this.getOrderDetails();
        this.dispatchEvent(new CustomEvent("ordereditsuccess"));
    }

    handleCreditCardSave(){
        this.dispatchEvent(new CustomEvent("ordercreditcardsave"));
    }

    handleCallDemographics(){
        this.dispatchEvent(new CustomEvent('calldemographics'));
    }
                      
    handleLogNoteAdded() {
        this.dispatchEvent(new CustomEvent('lognoteadded'));
    }
}