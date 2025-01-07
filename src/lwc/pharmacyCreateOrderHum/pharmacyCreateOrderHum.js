/*
LWC Name        : PharmacyCreateOrderHum.js
Function        : LWC to for pharmacy create order.

Modification Log:
* Developer Name                  Date                         Description
* Nirmal Garg                    10/14/2021                   Original Version
* Nirmal Garg              		 03/17/2022                   DF-4652 fix
*	Nirmal Garg					 04/05/2022					  US3198914 - Added case comment logging logic.	
* Kalyani Pachpol                07/29/2022					 US-3614274	
* Aishwarya Pawar				08/18/2021					Defect DF-5872 fix
* Aishwarya Pawar                  08/24/2022                   DF-5946
* Swapnali Sonawane               11/01/2022                   US- 3729809 Migration of the UI enhancements in the addresses section.
* Jonathan Dickinson              01/20/2023                   User Story 4146084: T1PRJ0865978 - MF 23528 -SF - TECH Mail Order Pharmacy- Address/Close the gaps identified during user testing. - 3
* Nirmal Garg					 07/27/2023	                US4902305
* Vishal Shinde                  10/10/2023                 User Story 5002422- Mail Order Management; Pharmacy - identify Error Messaging and parameters (Lightning)
****************************************************************************************************************************/
import { api, LightningElement, track, wire } from 'lwc';
import { CreateOrderDTO } from './createorderrequest';
import invokeCreateEditOrder from '@salesforce/apexContinuation/PharmacyCreateOrder_LC_HUM.invokecreateEditOrderDetail';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import humanaPharmacyLMS from '@salesforce/messageChannel/pharmacyLMSChannel__c';
import { publish, MessageContext, subscribe, unsubscribe } from 'lightning/messageService';
import getMemberDetails from "@salesforce/apexContinuation/Pharmacy_LC_HUM.invokeGetMemberService";
import poboxerror from "@salesforce/label/c.POERROR_PHARMACY_HUM";
import ExpiredCreditCardErr from "@salesforce/label/c.ExpiredCreditCardErr";
import ordernotprocessederror from "@salesforce/label/c.OrderNotProcessedErr";
import sendLogNotesToEPost from '@salesforce/apexContinuation/PharmacyCreateOrder_LC_HUM.invokeLogNoteRequest';
const CREDTCARD_HIDDEN_NUMBER = ' ********';
const CREDITCARD_EXP_CONST = ' exp ';
const CREDITCARD_AUTO_CONST = '(Auto) ';
const EDIT_CARD_ERROR = ' You must Select a Card.';
const ADDERROR = 'ADDR12006';
const LOG_NOTE_ERROR = '"Problem in sending Log Notes. If you continue to receive this message, contact CSS HelpDesk.';
export default class PharmacyCreateOrderHum extends LightningElement {

    @api
    prescriptions;

    @api
    enterpriseId;

    @api
    recordId;

    @api
    networkId;

    @api
    pharmacydemodetails;

    @wire(MessageContext)
    messageContext;

    label = {
        poboxerror,
        ordernotprocessederror,
        ExpiredCreditCardErr
    }
    @track submitDisabled;
    @track selectedRowData;
    @track creditCardData = {};
    @track selectedShippingMethod;
    @track lstPaymentMethods = [];
    @track selectedPaymentMethod;
    @track selectedCreditCard;
    @track selectedShippingAddress;
    @track createOrderPrescriptionItems = [];
    @track commercialUser;
    @track bPrescriptionExist = true;
    @track loaded = true;
    @track isCreateAndEditCreditCardVisible = false;
    @track createAndEditCreditCardComponentMode;
    @track numberOfApprovedConsents;
    @track ordermessage = '';
    @track createOrderDataObj = {};
    @track orderReleaseDate;

    @track sMinDate;
    @track sStartDate;
    @track releaseDate;
    @track isSubmit = true;
    @track isPharmacyTemp = true;
    @track capType;
    showCaseCommentBox = true;
    @track chkCreditCardExpiryBol= false;

    shippingoptions = [{
        label: '(1) Regular Mail',
        value: '1'
    }, {
        label: '(2) Expedited Mail - $6.99 Charge',
        value: '2'
    }, {
        label: '(511) Expedited No charge',
        value: '511'
    }]

    @track isCreateOrder = true;
    @track isCreditcard = false;
    @track shippingAddressComment;
    @track creditCardComment;

    handleShippingChange(event) {
        this.selectedShippingMethod = event.target.value;
        this.validateInputs();
    }

    goToSummary() {
        this.dispatchEvent(new CustomEvent("gotosummary"));
    }

    updateShippingAddress(event) {
        if (event.detail) {
            this.selectedShippingAddress = event.detail.ShippingAddress;
        }
    }

    getAddressComments(event) {
        if (event.detail) {
            this.shippingAddressComment = event.detail.ShippingAddressComment;
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


    handleShowCase() {
        this.showCaseCommentBox = true;
    }


    sendLogNotes() {
        sendLogNotesToEPost({ numberOfConsentGiven: this.numberOfApprovedConsents, sMemID: this.enterpriseId, networkID: this.networkId, sRecordId: this.recordId })
            .then(result => {
                if (result == null || result == undefined) {
                    this.displayToastEvent(LOG_NOTE_ERROR, 'error', 'Error!');
                }
            }).catch(error => {
                console.log(JSON.stringify(error));
                this.displayToastEvent(LOG_NOTE_ERROR, 'error', 'Error!');
            })
    }


    handleAddAddress() {
        this.showCaseCommentBox = false;
    }


    initializeData() {
        this.isCreateOrder = true;
        this.lstPaymentMethods = [];
        this.pharmacydemodetails = JSON.parse(JSON.stringify(this.pharmacydemodetails));
        this.selectedShippingAddress = this.pharmacydemodetails != null
            && this.pharmacydemodetails.Address != null ? this.pharmacydemodetails.Address.find(k => k.AddressType === 'S' && k.IsActive === 'true') : null;
        let creditCards = this.pharmacydemodetails?.CreditCardsDetail?.CreditCard;
        creditCards = creditCards != null && creditCards.length > 0 ? creditCards.filter(k => k.IsActive === 'true') : [];
        this.commercialUser = this.pharmacydemodetails != null && this.pharmacydemodetails.NeedsMemberConsent != null ?
            this.pharmacydemodetails.NeedsMemberConsent : null;
        this.creditCardData = JSON.parse(JSON.stringify(this.pharmacydemodetails.CreditCardsDetail));
        if (this.pharmacydemodetails.CapType != null) {
            this.capType = this.pharmacydemodetails.CapType;
        }
        this.lstPaymentMethods.push({
            label: 'Invoice',
            value: '111'
        });
        if (creditCards && creditCards.length > 0) {
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
        }
        this.bPrescriptionExist = this.prescriptions.length > 0 ? false : true;
        this.selectedShippingMethod = '1';
    }

    connectedCallback() {
        this.initializeData();
        this.subscribeToMessageChannel();
        this.initialDates();
        this.validateInputs();
    }

    subscribeToMessageChannel() {
        if (!this.subscription) {
            this.subscription = subscribe(
                this.messageContext,
                humanaPharmacyLMS,
                (message) => this.handleMessage(message)

            );
        }
    }

    handleMessage(message) {
        if (message.MessageName === 'UpdatePrescriptions') {
            this.AddPrescriptions(message.messageDetails);
        }
    }

    AddPrescriptions(messageDetails) {
        let presdata = messageDetails;
        if (presdata && presdata.length > 0) {
            let clondata = [...this.prescriptions];
            presdata.forEach(k => {
                clondata.push({
                    prescriptions: k.currentPresciption,
                    icon: k.icon
                });
            })
            this.prescriptions = clondata;
        }
    }

    unsubscribeToMessageChannel() {
        unsubscribe(this.subscription);
        this.subscription = null;

    }

    disconnectedCallback() {
        this.unsubscribeToMessageChannel();
    }

    handlePaymentChange(event) {
        this.paymentMethod = event.target.value;
        this.validateInputs();
    }

    generateCaseComment(orderkey) {
        let sCaseComment = `Placed order ${orderkey} for the following:`;
        this.prescriptions.forEach(h => {
            sCaseComment += `Rx# ${h.prescriptions.RXNumber},`
        });
        sCaseComment = sCaseComment.replace(/,\s*$/, ".");
        if (this.paymentMethod === '111') {
            sCaseComment += ` Payment Method: Invoice`;
        } else {
            let creditcard = {};
            creditcard = this.pharmacydemodetails.CreditCardsDetail.CreditCard.find(k => k.CreditCardKey === this.paymentMethod);
            if (creditcard != undefined && creditcard != '') {
                sCaseComment += ` Payment Method: Credit Card ${creditcard.CreditCardLast4Digits} - ${creditcard.ExpirationMonth}/${creditcard.ExpirationYear}`;
            }
        }
        if (this.selectedShippingAddress.AddressLine2.length > 0) {
            sCaseComment += `, Shipping Address: ${this.selectedShippingAddress.AddressLine1},${this.selectedShippingAddress.AddressLine2},${this.selectedShippingAddress.City},${this.selectedShippingAddress.StateCode},${this.selectedShippingAddress.ZipCode}`;
        } else {
            sCaseComment += `, Shipping Address: ${this.selectedShippingAddress.AddressLine1},${this.selectedShippingAddress.City},${this.selectedShippingAddress.StateCode},${this.selectedShippingAddress.ZipCode}`;
        }
        sCaseComment += `, Release Date: ${this.releaseDate}` + '\n';
        if (this.creditCardComment != '' && this.creditCardComment != undefined) sCaseComment += this.creditCardComment + '. ';
        if (this.shippingAddressComment != '' && this.shippingAddressComment != undefined) sCaseComment += this.shippingAddressComment;

        return sCaseComment;
    }

    handleInvalidReleaseDate(event) {
        this.submitDisabled = true;
    }

    validateInputs() {
        let validReleaseDate = true;
        const isDropdownfilled = [...this.template.querySelectorAll('lightning-combobox')]
            .reduce((validSoFar, inputField) => {
                inputField.reportValidity();
                return validSoFar && inputField.checkValidity();
            }, true);
        if (this.template.querySelector('c-generic-date-selector') != null) {
            validReleaseDate = this.template.querySelector('c-generic-date-selector').isInputValid();
        }
        this.submitDisabled = (isDropdownfilled && this.paymentMethod && this.selectedShippingMethod && validReleaseDate && !this.bPrescriptionExist) ? false : true;
    }

    handleSubmitClick(event) {
        let isCaseExist = event.detail.ExistCase;
        let moveToRouting = {};
        moveToRouting.orderKey = [];
        let PaymentMethods = {};
        let paymentType = [];
        let creditcard = {};
        const request = {};
        let scriptItem = {};
        let d = new Date();
        this.preparePrescriptionScript();
        if (this.paymentMethod === '111') {
            PaymentMethods.paymentMethodType = 'Invoice';
            paymentType.push(PaymentMethods);
            creditcard = null;
             this.chkCreditCardExpiryBol=true
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
        request.CreateAndEditOrderRequest = new CreateOrderDTO(this.enterpriseId, this.networkId, '', this.releaseDate,
            '', moveToRouting, '', this.selectedShippingAddress, paymentType, creditcard,
            scriptItem, this.selectedShippingMethod);
        console.log(JSON.stringify(request));
        this.loaded = false;
    if(this.chkCreditCardExpiryBol){
        invokeCreateEditOrder({ createEditObj: JSON.stringify(request), sRecordId: this.recordId })
            .then(result => {
                if (result != null && result != undefined) {
                    console.log(result);
                    if (result.includes('orderKey')) {
                        let response = JSON.parse(result);
                        if (response && response.orderKey) {
                            this.ordermessage = `Order #${response.orderKey} has been successfully submitted`;
                            this.loaded = true;
                            this.createOrderDataObj = {};
                            this.createOrderDataObj = {
                                header: "Successfully Processed",
                                source: "Create Order",
                                tablayout: false,
                                message: this.ordermessage,
                                caseComment: this.generateCaseComment(response.orderKey),
                                redirecttocaseedit: true
                            }
                            if (this.template.querySelector('c-generic-case-comment-logging') != null) {
                                this.template.querySelector('c-generic-case-comment-logging').handleLog(this.createOrderDataObj, isCaseExist);
                            }
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
                console.log('Create Order Error : ' + error.body.message);
                this.displayToastEvent(this.label.ordernotprocessederror, 'error', 'Error!')
                this.loaded = true;
            })
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


    getCreditCardComment(event) {
        if (event.detail) {
            this.creditCardComment = event.detail.CreditCardComment;
        }
    }

    handleAddPrescription() {
        this.dispatchEvent(new CustomEvent('addprescription', {
            detail: {
                addeddprescriptions: this.prescriptions
            }
        }))
    }

    preparePrescriptionScript() {
        this.createOrderPrescriptionItems = [];
        this.numberOfApprovedConsents = 0;
        if (this.prescriptions && this.prescriptions.length > 0) {
            this.prescriptions.forEach(h => {
                const scriptObject = {};
                scriptObject.scriptKey = h.prescriptions.RXNumber;
                if (this.commercialUser != undefined && this.commercialUser != null && this.commercialUser == 'false') {
                    let d = new Date();
                    scriptObject.consentDateTime = (d.getMonth() + 1).toString() + "/" + d.getDate().toString() + "/" + d.getFullYear().toString();
                }
                else {
                    const consentString = '';
                    const changedConsentString = h.prescriptions.Consent;
                    scriptObject.scriptKey = h.prescriptions.RXNumber;
                    if (h.prescriptions.RxConsent === 'CA') {
                        scriptObject.consentStatus = h.prescriptions.RxConsent;
                        let d = new Date();
                        scriptObject.consentDateTime = (d.getMonth() + 1).toString() + "/" + d.getDate().toString() + "/" + d.getFullYear().toString();
                        this.numberOfApprovedConsents = this.numberOfApprovedConsents + 1;
                    } else if (h.prescriptions.RxConsent === 'CR') {
                        scriptObject.consentStatus = h.prescriptions.RxConsent;
                    }
                    else {
                        scriptObject.consentStatus = h.prescriptions.RxConsent;
                    }
                }
                this.createOrderPrescriptionItems.push(scriptObject);
            })
        }
    }

    handleRemovePrescription(event) {
        if (event.detail) {
            this.prescriptions = this.prescriptions.filter(k => k.prescriptions.RXNumber != event.detail.RXNumber);
            this.bPrescriptionExist = this.prescriptions.length > 0 ? false : true;
        }
    }

    @api
    pharmacydata(data) {
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

    handleCancelClick() {
        this.dispatchEvent(new CustomEvent("gotosummary"));
    }

    renderNewCardForm() {
        return new Promise((resolve) => {
            if (this.createAndEditCreditCardComponentMode !== 'edit') {
                this.createAndEditCreditCardComponentMode = 'new';
            }
            this.isCreateAndEditCreditCardVisible = true;
            this.showCaseCommentBox = false;
            resolve('');
        });
    }

    renderEditCardForm() {
        return new Promise((resolve) => {
            this.showCaseCommentBox = false;
            this.isCreateAndEditCreditCardVisible = false;
            if (this.paymentMethod != '111' && this.paymentMethod != null
                && this.paymentMethod != undefined) {
                if (this.createAndEditCreditCardComponentMode !== 'new') {
                    this.createAndEditCreditCardComponentMode = 'edit';
                }
                this.selectedRowData = null;
                this.selectedRowData = this.pharmacydemodetails.CreditCardsDetail.CreditCard.find(k => k.CreditCardKey === this.paymentMethod);
                this.isCreateAndEditCreditCardVisible = true;
                resolve('');
            } else {
                this.createAndEditCreditCardComponentMode = '';
                this.displayToastEvent(EDIT_CARD_ERROR, 'warning', 'warning!');
            }
        });
    }

    handleNew() {
        this.renderNewCardForm()
            .then(() => this.scrollToCreditCardView())
            .catch((error) => console.log('handleNew ' + error));
    }

    handleEdit() {
        this.renderEditCardForm()
            .then(() => this.scrollToCreditCardView())
            .catch((error) => console.log('handleEdit ' + error));
    }

    scrollToCreditCardView() {
        let carddiv = this.template.querySelector('.createeditcreditcard');
        if (carddiv) {
            carddiv.scrollIntoView(true);
        }
    }

    scrollToTop() {
        let topdiv = this.template.querySelector('.topdiv');
        if (topdiv) {
            topdiv.scrollIntoView(true);
        }
    }

    handleSave() {
        this.isCreateAndEditCreditCardVisible = false;
        this.createAndEditCreditCardComponentMode = '';
        this.callGetMemberService();
        this.showCaseCommentBox = true;
    }


    handleCancel() {
        this.isCreateAndEditCreditCardVisible = false;
        this.showCaseCommentBox = true;
        this.createAndEditCreditCardComponentMode = '';
        this.scrollToTop();
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
        }).then(data => {
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

    handleFinishLogging() {
        this.showCaseCommentBox = false;
        this.goToSummary();
        this.sendLogNotes();
    }

    initialDates() {
        let sDate = new Date();
        this.sStartDate = sDate.toISOString().substring(0, 10);
        this.sMinDate = sDate.toISOString().substring(0, 10);
        this.releaseDate = String(sDate.getMonth() + 1).padStart(2, '0') + '/' + String(sDate.getDate()).padStart(2, '0') + '/' + sDate.getFullYear();
    }

    handleDateChange(event) {
        let datedata = event.detail;
        if (datedata.keyname === 'ReleaseDate') {
            this.sStartDate = datedata.datevalue;
            if (datedata.datevalue.includes('-')) {
                let sDate = datedata.datevalue.split('-');
                if (sDate.length > 0) {
                    this.releaseDate = sDate[1] + '/' + sDate[2] + '/' + sDate[0];
                }
            } else {
                this.releaseDate = datedata.datevalue;
            }
            this.validateInputs();
        }
    }
}