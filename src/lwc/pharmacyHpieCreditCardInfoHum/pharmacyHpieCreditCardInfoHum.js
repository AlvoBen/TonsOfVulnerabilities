/*****************************************************************************************************************************
Component Name   : pharmacyHpieCreditCardInfoHum.js
Version          : 1.0
Function         : lwc to display credit card info tab

Modification Log:
* Developer Name           Code Review                 Date                         Description
*---------------------------------------------------------------------------------------------------------------------
* Jonathan Dickinson							       08/17/2023					User Story 4908778: T1PRJ0870026 - SF - Tech - C12 Mail Order Management - Pharmacy - Finance tab
* Jonathan Dickinson			  					   09/04/2023					User Story 4999697: T1PRJ0870026 MF27456 HPIE/CRM - SF Tech- Mail Order Management - Pharmacy - Finance tab - Lightning - Edit Credit Card, One time payment
*******************************************************************************************************************************************************************/

import { LightningElement, wire, api, track } from 'lwc';
import { CurrentPageReference } from 'lightning/navigation';
import { MessageContext } from 'lightning/messageService';
import creditCardPaymentSuccessMessage from "@salesforce/label/c.CreditCardPaymentSuccessMessage";
import cardpaymentsuccessmsg from '@salesforce/label/c.FINANCIAL_CARDPAYMENT_SUCESS';
import cardpaymenterrormessage from '@salesforce/label/c.FINANCIAL_CARDPAYMENT_ERROR';
import pubSubHum from 'c/pubSubHum';

export default class PharmacyHpieCreditCardInfoHum extends LightningElement {

    _financeDetails;

    @api
    get financeDetails() {
        return this._financeDetails;
    }

    set financeDetails(value) {
        if(value){
            this._financeDetails = value;
            this.creditCardData = this.financeDetails?.paymentCards ?? null;
            this.balanceDue = this.financeDetails?.outstandingBalance ?? null;
        }
    }
    
    @api recordId;
    @api enterpriseId;
    @api networkId;
    @api organization;
    @api userId;
    @track oneTimePaymentDataObj = {};

    @track cardtype = {};

    creditCardData = {};
    @track selectedRowData;
    defaultSortDirection = 'asc';
    sortDirection = 'asc';
    sortedBy;
    expiringSoonCreditCardIconURL;
    expiredCreditCardIconURL;
    numCards;
    selectedSortHeader;

    creditCardTypeEl;
    last4DigitsEl;
    cardHolderNameEl;
    expirationDateEl;
    autoChargeEl;
    statusEl;
    FSA_HSA_El;

    selectedCardOwnerName;
    showSelectedCardData = false;
    isSelectedVisaCard = false;
    isSelectedMasterCard = false;
    selectedCardKey;
    lstCardComboData;
    @track balanceDue;
    selectedCardComboData;
    showConfirmationModal;
    selectedcardTypeLiteral;
    selectedCardLast4Digits;
    enteredAmount = null;
    enteredDate = this.getTodaysDate();
    minDateValue = this.getTodaysDate();

    isCardNotAvailable = true;

    isDataExists;
    @track loaded = false;
    isCreateAndEditCreditCardVisible = false;
    createAndEditCreditCardComponentMode;

    showCaseCommentBox = false;
    viewExistingCasePanel = false;

    promptclass = 'slds-modal__header slds-theme_success slds-theme_alert-texture';
    showlogging = true;
    calledFromExisting = true;
    showCaseTemplateMode = 'newCase';

    isEditCreditCardVisible = false;
    label = {
        creditCardPaymentSuccessMessage,
        cardpaymentsuccessmsg,
        cardpaymenterrormessage
    }

    creditCardDeatilsExist = false;
    disableNewCreditCardButton = false;

    @wire(MessageContext)
    messageContext;

    @wire(CurrentPageReference)
    pageRef;
    
    handleEditClick(event) {
        if (event && event?.detail) {
            this.isCreateAndEditCreditCardVisible = false;
            this.selectedRowData = this.creditCardData && Array.isArray(this.creditCardData)
                && this.creditCardData?.length > 0 ?
                this.creditCardData.find(k => k.key === event?.detail?.carddata?.creditCardKey) : null;
            if (this.selectedRowData) {
                this.selectedCardComboData = this.selectedRowData.key;
                this.showSelectedCardData = true;
                this.createAndEditCreditCardComponentMode = 'edit';
                this.isCreateAndEditCreditCardVisible = true;
                this.scrollToCreditCardView();
            }
        }
    }

    getTodaysDate() {
        let today = new Date();
        let dd = String(today.getDate()).padStart(2, '0');
        let mm = String(today.getMonth() + 1).padStart(2, '0');
        let yyyy = today.getFullYear();
        today = yyyy + '-' + mm + '-' + dd;
        return today;
    }

    scrollToCreditCardView() {
        let carddiv = this.template.querySelector('.createeditcreditcard');
        if (carddiv) {
            carddiv.scrollIntoView();
        }
    }

    //NEW/EDIT CARD
    handleNewCreditCard() {
        this.isEditCreditCardVisible = false;
        this.createAndEditCreditCardComponentMode = 'new';
        this.isCreateAndEditCreditCardVisible = true;
        this.selectedRowData = null;
        this.selectedCardComboData = null;
        this.showSelectedCardData = false;
        this.scrollToCreditCardView();
        this.disableNewCreditCardButton = true;
    }

    handleSave() {
        this.isEditCreditCardVisible = false;
        this.isCreateAndEditCreditCardVisible = false;
        this.createAndEditCreditCardComponentMode = '';
        this.scrollToCardDetailsView();
        this.disableNewCreditCardButton = false;
        this.dispatchEvent(new CustomEvent("cardsavesuccess"));
    }

    handleCancel() {
        this.isEditCreditCardVisible = false;
        this.isCreateAndEditCreditCardVisible = false;
        this.createAndEditCreditCardComponentMode = '';
        this.scrollToCardDetailsView();
        this.disableNewCreditCardButton = false;
    }

    scrollToCardDetailsView() {
        let carddetails = this.template.querySelector('.divcarddetails');
        if (carddetails) {
            carddetails.scrollIntoView();
        }
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

    handleFinishLogging() {
        this.showCaseCommentBox = false;
    }

    handleNewCaseToggle() {
        if (this.showCaseTemplateMode === 'existingCase') {
            this.showCaseTemplateMode = 'newCase';
        }
        else {
            this.showCaseTemplateMode = 'existingCase';
        }
    }
}