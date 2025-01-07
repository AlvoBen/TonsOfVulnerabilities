/*****************************************************************************************************************************
Component Name   : pharmacyHpieCreateAndEditCreditCardHum.js
Version          : 1.0
Function         : lwc to display create and edit credit card component

Modification Log:
* Developer Name           Code Review                 Date                         Description
*---------------------------------------------------------------------------------------------------------------------
* Jonathan Dickinson							       08/17/2023					User Story 4908778: T1PRJ0870026 - SF - Tech - C12 Mail Order Management - Pharmacy - Finance tab
* Jonathan Dickinson			                       09/04/2023					User Story 4999697: T1PRJ0870026 MF27456 HPIE/CRM - SF Tech- Mail Order Management - Pharmacy - Finance tab - Lightning - Edit Credit Card, One time payment
* Jonathan Dickinson                                   03/01/2024                   User Story 5404385: T1PRJ1374973: DF8347/8350: REGRESSION_Lightning_Error Messages Issue and typographical Corrections
*******************************************************************************************************************************************************************/

import { LightningElement, api, track, wire } from 'lwc';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import AUTOCHARGEERROR_CREDITCARD_HUM from '@salesforce/label/c.AUTOCHARGEERROR_CREDITCARD_HUM';
import HUMPCIWarningMessage from '@salesforce/label/c.HUMPCIWarningMessage';
import cardAttachedToOrderError from '@salesforce/label/c.HUMPCIErrorMessage';
import genericCreditCardSaveError from '@salesforce/label/c.PHARMACY_GENERIC_CARD_SAVE_ERROR';
import createAndEditCreditCardSuccessMessage from "@salesforce/label/c.CreateAndEditCreditCardSuccessMessage";
import { getFormatDate } from 'c/crmUtilityHum';
import { getPCIToken } from './pciHelper';
import { cards, getCardImage, getCardType } from './creditconfig';
import { addNewCreditCard, updateCreditCard, getOrders } from 'c/pharmacyHPIEIntegrationHum';
import { addOrUpdateCardRequest } from './addOrUpdateCardRequest';
const CARD_NUMBER_SPACE = '  ';
const NON_AMEX_CARD_NUMBER_LENGTH = 16;
const AMEX_CARD_NUMBER_LENGTH = 15;
const DEFAULT_CARD_NUMBER_INPUT_MAX_LENGTH = 16;
const CURRENT_CENTURY = 2000;
const NEW_CREDIT_CARD_KEY_VAL = -1;
const ORDER_PAYMENT_CARD_CODE = 2;
const ORDER_OPEN_STATUS = 'OPEN';

export default class PharmacyHpieCreateAndEditCreditCardHum extends LightningElement {

    @api
    creditCardData;

    @api
    recordId;

    @api
    enterpriseId;

    @api
    networkId;

    @api 
    order;

    @api 
    userId;

    get notLoaded() {
        return !this.isLoaded;
    }

    componentModeSetting;
    selectedRow;
    isLoaded = true;
    cardType;
    isSaveSuccessful;
    PCIURL;
    defaultFormat = /(\d{1,4})/g;

    cardTypeEl;

    areFieldsDisabled;
    isAutoChargeInputDisabled;
    cardNumberInputMaxLength;
    tokenRetrieved;
    displayedCVV = '***';
    displayedCardNumber;
    displayedStatus;
    displayedIsAutoCharge;
    cardTypeElInnerHTML;

    creditCardNumber;
    creditCardFirstName;
    creditCardMiddleName;
    creditCardLastName;
    creditCardFullName;
    creditCardExpDate;
    creditCardExpirationYear;
    creditCardExpirationMonth;
    creditCardIsAutoCharge;
    creditCardStatus;
    creditCardKey;
    creditCardIsSpendingAcc;
    creditCardToken;
    cardTypeDesc;

    WarningMessage = HUMPCIWarningMessage;
    attachedOrders = false;
    creditCardOwnerName;
    previousAutoChargeVal;
    AcknowledgedMsg = false;
    isAcknowledged = false;
    creditCardDataObj = {};
    showCaseCommentBox = false;

    @track isPharmacyTemp = true;

    labels = {
        createAndEditCreditCardSuccessMessage,
        genericCreditCardSaveError,
        cardAttachedToOrderError
    }

    cards = cards;

    @api
    get componentMode() {
        return this.componentModeSetting;
    }

    set componentMode(value) {
        this.componentModeSetting = value;
        this.resetProperties();
        this.disableFieldsOnEdit();
        this.setCardDefaultValuesOnNew();
    }

    @api
    get selectedRowData() {
        return this.selectedRow;
    }

    set selectedRowData(value) {
        this.selectedRow = value;
        if (this.componentModeSetting === 'edit' && this.selectedRow) {
            this.addEditData();
        }
    }


    renderedCallback() {
        this.cardTypeEl = this.template.querySelector('.card-type');
        this.setCardTypeElement();
    }



    setCardDefaultValuesOnNew() {
        if (this.componentModeSetting === 'new') {
            this.creditCardIsSpendingAcc = false;
            this.creditCardIsAutoCharge = false;
            this.creditCardStatus = true;
            this.creditCardKey = -1;
        }
    }

    disableFieldsOnEdit() {
        if (this.componentModeSetting === 'edit') {
            this.areFieldsDisabled = true;
        } else {
            this.areFieldsDisabled = false;
        }
    }

    addEditData() {
        this.creditCardFirstName = this.selectedRow.firstName;
        this.creditCardMiddleName = this.selectedRow.middleName;
        this.creditCardLastName = this.selectedRow.lastName;
        this.creditCardExpDate = `${this.selectedRow?.expirationMonth.toString().padStart(2, '0')}/${this.selectedRow?.expirationYear.toString().slice(-2)}`;
        this.cardType = this.selectedRow.z0type.code;
        this.cardTypeDesc = this.selectedRow.z0type.description;
        this.creditCardIsAutoCharge = this.selectedRow.autoCharge;
        this.displayedIsAutoCharge = this.selectedRow.autoCharge;
        this.previousAutoChargeVal = this.selectedRow.autoCharge ? 'Y' : 'N';
        this.creditCardStatus = this.selectedRow.active;
        this.displayedStatus = this.selectedRow.active ? 'A' : 'I';
        this.isAutoChargeInputDisabled = !this.selectedRow.active;
        this.creditCardKey = this.selectedRow.key;
        this.creditCardExpirationYear = this.selectedRow.expirationYear;
        this.creditCardExpirationMonth = this.selectedRow.expirationMonth;
        this.creditCardIsSpendingAcc = this.selectedRow.spendingAccount;
        this.creditCardNumber = this.selectedRow.tokenKey;
        this.determineEditCardNumberString(this.selectedRow.tokenKey.slice(-4));
        this.setOwnerNameDisplay();
    }

    determineEditCardNumberString(lastFourDigits) {
        let cardNumberString;

        if (this.cardType !== '4') {
            cardNumberString = lastFourDigits.padStart(NON_AMEX_CARD_NUMBER_LENGTH, '*');
            cardNumberString = this.displayedCardNumber = this.formatNonAmexCardNumber(cardNumberString);
        } else {
            cardNumberString = lastFourDigits.padStart(AMEX_CARD_NUMBER_LENGTH, '*');
            cardNumberString = this.displayedCardNumber = this.formatAmexCardNumber(cardNumberString);
        }

        this.setCardInputMaxLength(cardNumberString);
    }

    get statusOptions() {
        return [
            { label: 'A', value: 'A' },
            { label: 'I', value: 'I' },
        ];
    }

    handleNewCardNumberChange(e) {
        let tempdata = e.target.value.replace(/\s/g, '');
        if (!isNaN(tempdata)) {
            this.creditCardNumber = e.target.value.replace(/\s/g, '');
            this.determineNewCardType(e.target.value);
            this.updateNewCardNumberDisplayandInput(e.target.value);
            this.setCardInputMaxLength(e.target.value);
        } else {
            e.target.value = tempdata != null && tempdata.length > 0 ?
                tempdata.substring(0, tempdata.length - 1) : '';
            this.determineNewCardType(e.target.value);
            this.updateNewCardNumberDisplayandInput(e.target.value);
            this.setCardInputMaxLength(e.target.value);
        }
    }

    updateNewCardNumberDisplayandInput(cardNum) {
        if (cardNum) {
            if (this.componentModeSetting === 'new') {
                cardNum = cardNum.replace(/\s/g, '');

                if (this.cardType !== '4') {
                    this.displayedCardNumber = this.formatNonAmexCardNumber(cardNum);
                } else {
                    this.displayedCardNumber = this.formatAmexCardNumber(cardNum);
                }
            }
        } else {
            this.displayedCardNumber = '';
        }
    }

    determineNewCardType(cardNum) {
        if (this.componentModeSetting === 'new') {
            let patternFound = false;
            for (const card of Array.from(this.cards)) {
                if (card.pattern.test(this.creditCardNumber)) {
                    this.cardType = card.type;
                    this.cardTypeDesc = card.description;
                    this.setCardTypeElement();
                    patternFound = true;
                    break;
                }
            }
            if (patternFound === false) {
                this.cardType = '';
                this.setCardTypeElement();
            }
        }
    }

    setCardTypeElement() {
        if (this.cardTypeEl) {
            this.cardTypeEl.innerHTML = this.cardType ? getCardImage(this.cardType) : getCardImage("");
        }
    }

    formatNonAmexCardNumber(cardNum) {
        return cardNum.match(/[\S]{1,4}/g).join(CARD_NUMBER_SPACE);
    }

    formatAmexCardNumber(cardNum) {
        return cardNum.match(/((?<=[\S]{6})[\S]{1,5})|((?<=[\S]{4})[\S]{1,6})|[\S]{1,4}/g).join(CARD_NUMBER_SPACE);
    }

    setCardInputMaxLength(cardNum) {
        let numSpaces = 0;
        let cardFound = false;
        Array.from(cardNum).forEach(el => {
            if (el === ' ') {
                numSpaces += 1;
            }
        })

        this.cards.forEach(el => {
            if (el.type === this.cardType) {
                this.cardNumberInputMaxLength = el.length + numSpaces;
                cardFound = true;
            }
        })
        if (cardFound === false) {
            this.cardNumberInputMaxLength = DEFAULT_CARD_NUMBER_INPUT_MAX_LENGTH + numSpaces;
        }
    }

    handleFirstNameChange(e) {
        this.creditCardFirstName = e.target.value;
        this.setOwnerNameDisplay();
    }
    handleMiddleNameChange(e) {
        this.creditCardMiddleName = e.target.value;
        this.setOwnerNameDisplay();
    }
    handleLastNameChange(e) {
        this.creditCardLastName = e.target.value;
        this.setOwnerNameDisplay();
    }
    setOwnerNameDisplay() {
        this.creditCardOwnerName = `${this.creditCardFirstName ? this.creditCardFirstName : ''}${this.creditCardMiddleName ? ' ' + this.creditCardMiddleName + ' ' : ' '}${this.creditCardLastName ? this.creditCardLastName : ''}`;
    }
    handleExpDateChange(e) {
        if (!isNaN(e.target.value)) {
            this.creditCardExpDate = e.target.value;
        } else if (e.target.value.includes('/')) {
            this.creditCardExpDate = e.target.value;
        } else {
            this.creditCardExpDate = e.target.value != null && e.target.value.length > 0 ?
                e.target.value.substring(0, e.target.value.length - 1) : '';
            e.target.value = this.creditCardExpDate;
        }
    }

    validateExpDate() {
        let dateInput = this.template.querySelector('.exp-date-input');
        const dateFormat = /^(0[1-9]|1[0-2]|[1-9])\/?([0-9]{2})$/;

        if (dateInput.value && !dateFormat.test(dateInput.value)) {
            dateInput.setCustomValidity('Invalid format. Please enter the expiration date in MM/YY format.');
        } else {
            dateInput.setCustomValidity('');
        }

        dateInput.reportValidity();

    }

    validateCardNumber() {

        let cardNumInput = this.template.querySelector('.card-number-input');
        if (cardNumInput.value && !(this.luhnCheck())) {
            cardNumInput.setCustomValidity('Invalid card number. Please try again.');
        } else {
            cardNumInput.setCustomValidity('');
        }
        cardNumInput.reportValidity();
    }

    luhnCheck() {
        let odd = true;
        let sum = 0;

        const digits = (this.creditCardNumber + "").split("").reverse();

        for (let digit of Array.from(digits)) {
            digit = parseInt(digit, 10);
            if ((odd = !odd)) {
                digit *= 2;
            }
            if (digit > 9) {
                digit -= 9;
            }
            sum += digit;
        }

        return sum % 10 === 0;
    }

    validateFirstName() {
        let firstNameInput = this.template.querySelector('.first-name-input');
        if (!firstNameInput.value) {
            firstNameInput.setCustomValidity('Complete this field.');
        } else {
            firstNameInput.setCustomValidity('');
        }
        firstNameInput.reportValidity();
    }

    validateLastName() {
        let lastNameInput = this.template.querySelector('.last-name-input');
        if (!lastNameInput.value) {
            lastNameInput.setCustomValidity('Complete this field.');
        } else {
            lastNameInput.setCustomValidity('');
        }
        lastNameInput.reportValidity();
    }

    validateIsAutoCharge() {
        if (this.creditCardData.paymentCards) {
            const checkForAutoChargeCards = function (card) {
                return card.autoCharge;
            }
            let autoChargeInput = this.template.querySelector('.auto-charge-input');
            if (autoChargeInput.checked &&
                (this.componentModeSetting === 'new' || !this.selectedRow.autoCharge) &&
                this.creditCardData.paymentCards.some(checkForAutoChargeCards)) {
                autoChargeInput.setCustomValidity(`Error`);
                this.dispatchEvent(new ShowToastEvent({
                    title: 'Invalid Input',
                    message: AUTOCHARGEERROR_CREDITCARD_HUM,
                    variant: 'error',
                    mode: 'sticky'
                }));
            } else {
                autoChargeInput.setCustomValidity('');
            }
            autoChargeInput.reportValidity();
        }
    }

    validateStatus() {
        let statusInput = this.template.querySelector('.status-input');
        if (!statusInput.value) {
            statusInput.setCustomValidity('Complete this field.');
        } else {
            statusInput.setCustomValidity('');
        }
        statusInput.reportValidity();
    }

    handleAutoChargeChange(e) {
        this.creditCardIsAutoCharge = e.target.checked;
        this.displayedIsAutoCharge = e.target.checked;
        this.validateIsAutoCharge();
    }
    handleStatusChange(e) {
        if (e.target.value === 'A') {
            this.creditCardStatus = true;
            this.isAutoChargeInputDisabled = false;
        } else {
            this.creditCardStatus = false;
            this.isAutoChargeInputDisabled = true;
            this.displayedIsAutoCharge = false;
            this.creditCardIsAutoCharge = false;
        }
    }

    resetProperties() {
        this.creditCardNumber = '';
        this.creditCardFirstName = '';
        this.creditCardMiddleName = '';
        this.creditCardLastName = '';
        this.creditCardFullName = '';
        this.creditCardExpDate = '';
        this.creditCardIsAutoCharge = '';
        this.creditCardStatus = '';
        this.creditCardOwnerName = '';
        this.displayedCardNumber = '';
        this.displayedIsAutoCharge = false;
        this.displayedStatus = 'A';
        this.cardType = '';
        this.cardTypeDesc = '';
        this.creditCardToken = '';
    }

    addOrUpdateCreditCard() {
        return new Promise((resolve, reject) => {
            try {
                if (this.componentModeSetting === 'new') {
                    this.getTempToken()
                        .then(response => {
                            this.creditCardToken = response;
                            this.tokenRetrieved = true;
                            addNewCreditCard(this.getRequest())
                            .then(result => {
                                this.isSaveSuccessful = result != null && Object.hasOwn(result, 'identifier') ? true : false;
                                resolve(true);
                            }).catch(error => {
                                this.isSaveSuccessful = false;
                                reject(false)
                            })
                        }).catch(error => {
                            this.tokenRetrieved = false;
                            this.isLoaded = true;
                            reject(error);
                        });
                } else if (this.componentModeSetting === 'edit') {
                    updateCreditCard(this.getRequest())
                    .then(result => {
                        this.isSaveSuccessful = result != null && Object.hasOwn(result, 'identifier') ? true : false;
                        resolve(true);
                    }).catch(error => {
                        this.isSaveSuccessful = false;
                        reject(false)
                    })
                }
            } catch (error) {
                this.isLoaded = true;
                reject(error);
            }
        });
    }

    getRequest() {
        return new addOrUpdateCardRequest(this.enterpriseId, this.userId, this.organization ?? 'HUMANA', 
        this.componentModeSetting === 'new' ? NEW_CREDIT_CARD_KEY_VAL : this.creditCardKey, this.cardType, this.cardTypeDesc, 
        this.creditCardExpirationMonth, CURRENT_CENTURY + this.creditCardExpirationYear, this.creditCardFirstName, this.creditCardMiddleName ?? '',
        this.creditCardLastName, this.creditCardStatus, this.creditCardIsAutoCharge, this.creditCardIsSpendingAcc, this.creditCardToken);
    }
   
    validateInputs() {
        this.validateCardNumber();
        this.validateExpDate();
        let month, year;
        [month, year] = this.creditCardExpDate.split('/');
        this.creditCardExpirationMonth = Number(month);
        this.creditCardExpirationYear = Number(year);
        this.validateFirstName();
        this.validateLastName();
        this.validateIsAutoCharge();
        this.validateStatus();
    }

    getTempToken() {
        return new Promise((resolve, reject) => {
            getPCIToken(this.creditCardNumber).then(result => {
                resolve(result);
            }).catch(error => {
                this.isLoaded = true;
                reject(error);
            })
        });
    }

    handleAcknowledge() {
        this.AcknowledgedMsg = false;
        this.isAcknowledged = true;
        this.handleSaveAndLog();
    }

    async handleSaveAndLog() {
        let cardNumInput = this.template.querySelector('.card-number-input');
        let dateInput = this.template.querySelector('.exp-date-input');
        let firstNameInput = this.template.querySelector('.first-name-input');
        let lastNameInput = this.template.querySelector('.last-name-input');
        let autoChargeInput = this.template.querySelector('.auto-charge-input');
        let statusInput = this.template.querySelector('.status-input');
        this.validateInputs();

        if (this.isAcknowledged === false && this.previousAutoChargeVal === 'Y' && statusInput.value === 'I' && !this.creditCardIsAutoCharge) {
            this.AcknowledgedMsg = true;
        }
        else {
            this.AcknowledgedMsg = false;
        }
        if (this.AcknowledgedMsg === false && cardNumInput.checkValidity() && dateInput.checkValidity() && firstNameInput.checkValidity() && lastNameInput.checkValidity() && autoChargeInput.checkValidity() && statusInput.checkValidity()) {
            this.isLoaded = false;
            this.addOrUpdateCreditCard().then(result => {
                if (result && this.isSaveSuccessful) {
                    this.isLoaded = true;
                    this.creditCardDataObj = {};
                    this.creditCardDataObj = {
                        header: "Successfully Processed",
                        caseComment: this.generateCaseComment(cardNumInput),
                        source: "Credit Card",
                        tablayout: false,
                        message: this.labels.createAndEditCreditCardSuccessMessage,
                        redirecttocaseedit : true
                    }
                    this.showCaseCommentBox = true;
                    this.resetProperties();
                } else {
                    this.isLoaded = true;
                    this.dispatchSaveErrorToast(this.labels.genericCreditCardSaveError);
                }
                this.isLoaded = true;
            }).catch(error => {
                // If we are getting an error due to a card being attached to an OPEN order while making the card inactive, get order ids of the orders where the card is attached
                if(this.componentModeSetting === 'edit' && this.creditCardStatus === false) {
                    this.getOrdersWithCardAttached();
                } else {
                    this.isLoaded = true;
                    this.dispatchSaveErrorToast(this.labels.genericCreditCardSaveError);
                }
            })
        }
    }

    getOrderDates() {
        let todaysdate = new Date();
        let sDate = new Date();
        sDate.setDate(todaysdate.getDate() - 90);
        let startDate = ((sDate.getMonth() + 1).toString().length === 1 ? '0' + (sDate.getMonth() + 1) : (sDate.getMonth() + 1)) + '/' + (sDate.getDate().toString().length === 1 ? '0' + sDate.getDate() : sDate.getDate()) + '/' + sDate.getFullYear();
        let endDate = ((todaysdate.getMonth() + 1).toString().length === 1 ? '0' + (todaysdate.getMonth() + 1) : (todaysdate.getMonth() + 1)) + '/' + (todaysdate.getDate().toString().length === 1 ? '0' + todaysdate.getDate() : todaysdate.getDate()) + '/' + todaysdate.getFullYear();
        return {startDate, endDate};
    }

    getOrdersWithCardAttached() {
        let {startDate, endDate} = this.getOrderDates();
        getOrders(this.enterpriseId, this.userId, this.organization ?? 'HUMANA', getFormatDate(startDate, 'yyyy-mm-dd'), getFormatDate(endDate, 'yyyy-mm-dd'))
        .then(result => {
            if (result && result?.orders && Array.isArray(result?.orders) && result?.orders?.length > 0) {
                let activeOrdersWithCardAttached = result.orders.filter(order => order?.status?.description === ORDER_OPEN_STATUS && order?.billing?.payment?.method?.code === ORDER_PAYMENT_CARD_CODE && order?.billing?.payment?.card?.key === this.creditCardKey)?.map(order => order.orderId);
                if(activeOrdersWithCardAttached && Array.isArray(activeOrdersWithCardAttached) && activeOrdersWithCardAttached.length > 0) {
                    let cardAttachedToOrderMsg = `${this.labels.cardAttachedToOrderError} ${activeOrdersWithCardAttached.join(', ')}`;
                    this.isLoaded = true;
                    this.dispatchSaveErrorToast(cardAttachedToOrderMsg);
                } else {
                    this.isLoaded = true;
                    this.dispatchSaveErrorToast(this.labels.genericCreditCardSaveError);
                }
            } else {
                this.isLoaded = true;
                this.dispatchSaveErrorToast(this.labels.genericCreditCardSaveError);
            }
        }).catch(error => {
            this.isLoaded = true;
            this.dispatchSaveErrorToast(this.labels.genericCreditCardSaveError);
        })
    }

    dispatchSaveErrorToast(message) {
        this.dispatchEvent(new ShowToastEvent({
            title: 'An Error Occurred',
            message: message,
            variant: 'error',
        }));
    }

    generateCaseComment(cardNumInput) {
        let creditCardComment;
        if (this.componentModeSetting === 'new') {
            let cardnumber = cardNumInput.value;
            if (cardnumber) {
                cardnumber = cardnumber.substring(cardnumber.length - 4);
            }
            let sCaseComments = `Added ${getCardType(this.cardType)} credit card ending ${cardnumber} , Expiration: ${this.creditCardExpDate} , Auto Charge: ${!this.creditCardIsAutoCharge ? ' No' : this.creditCardIsAutoCharge? ' Yes' : ''}, Status: ${this.creditCardStatus ? ' Active' : !this.creditCardStatus ? ' Inactive' : ''}`;
            if (this.order) {
                this.dispatchEvent(new CustomEvent('creditcardcomment', {
                    detail: {
                        'CreditCardComment': sCaseComments
                    }
                }));
            }
            return sCaseComments;
        } else if (this.componentModeSetting === 'edit') {
            let sCaseCommentsOld = '';
            let sCaseCommentsNew = '';
            let ccExpOldMonth = this.selectedRow?.expirationMonth.toString().padStart(2, '0');
            let ccExpOldYear = this.selectedRow.expirationYear != null ? this.selectedRow.expirationYear.toString().slice(-2) : '';
            if (this.creditCardExpDate && this.creditCardExpDate.includes('/')) {
                if ((this.creditCardExpDate.split('/')[0] != ccExpOldMonth) || (this.creditCardExpDate.split('/')[1] != ccExpOldYear)) {
                    sCaseCommentsOld += ` Expiration: ${ccExpOldMonth}/${ccExpOldYear}`;
                    sCaseCommentsNew += ` Expiration: ${this.creditCardExpDate.split('/')[0]}/${this.creditCardExpDate.split('/')[1]}`;
                }
            }

            let ccAutochargeOld = this.selectedRow.autoCharge;
            let ccAutochargeNew = this.creditCardIsAutoCharge;
            if (ccAutochargeNew != ccAutochargeOld) {
                sCaseCommentsOld += ` Auto Charge: ${ccAutochargeOld === false ? ' No' : ccAutochargeOld === true ? ' Yes' : ''}`;
                sCaseCommentsNew += ` Auto Charge: ${ccAutochargeNew === false ? ' No' : ccAutochargeNew === true ? ' Yes' : ''}`;
            }

            let ccOldstatus = this.selectedRow.active;
            let ccNewStatus = this.creditCardStatus;
            if (ccOldstatus != ccNewStatus) {
                sCaseCommentsOld += ` Status: ${ccOldstatus === false ? ' Inactive' : ccOldstatus === true ? ' Active' : ''}`;
                sCaseCommentsNew += ` Status: ${ccNewStatus === false ? ' Inactive' : ccNewStatus === true ? ' Active' : ''}`;
            }
            
            creditCardComment = 'Updated credit card ending in ' + this.selectedRow.tokenKey.slice(-4) + ' from ' + sCaseCommentsOld + ' to ' + sCaseCommentsNew;
            if (this.order) {
                this.dispatchEvent(new CustomEvent('creditcardcomment', {
                    detail: {
                        'CreditCardComment': creditCardComment
                    }
                }));
            }
            return `Updated credit card ending in ${this.selectedRow.tokenKey.slice(-4)} from ${sCaseCommentsOld} to ${sCaseCommentsNew}`;
        }
    }

    handleCancel() {
        const cancelEvent = new CustomEvent('cancel');
        this.dispatchEvent(cancelEvent);
        this.resetProperties();
    }

    handleFinishLogging() {
        this.showCaseCommentBox = false;
        const saveAndLogEvent = new CustomEvent('save');
        this.dispatchEvent(saveAndLogEvent);
    }
}