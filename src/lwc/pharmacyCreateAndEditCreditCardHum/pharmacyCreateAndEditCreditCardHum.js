/*****************************************************************************************************************************
Component Name   : PharmacyCreateAndEditCreditCardHum.js
Version          : 1.0
Function         : lwc to display create and edit credit card component

Modification Log:
* Developer Name           Code Review                 Date                         Description
*---------------------------------------------------------------------------------------------------------------------
* Jonathan Dickinson																	11/09/2021					Original Version
* Aishwarya Pawar                                    3/25/2022						Defect Fix 4664
* Nirmal Garg																					04/05/2022					US-3198914-Added case comment logging
* Swapnali Sonawane                                  11/01/2022                       US- 3729809 Migration of the UI enhancements in the addresses section
* Abhishek Mangutkar                                  03/02/2023                       US- 4315274 Systematically associate interaction to new cases-New case button- MOP page
* Nirmal Garg                                         04/20/2023                       US - 4511380 TECH Pharmacy; RCC-PCI Updates
* Jonathan Dickinson                                  03/01/2024                       User Story 5404385: T1PRJ1374973: DF8347/8350: REGRESSION_Lightning_Error Messages Issue and typographical Corrections
*******************************************************************************************************************************************************************/

import { LightningElement, api, track, wire } from 'lwc';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import createOrEditCreditCardInfo from '@salesforce/apexContinuation/PharmacyFinancial_LC_HUM.invokeGetEditMemService';
import AUTOCHARGEERROR_CREDITCARD_HUM from '@salesforce/label/c.AUTOCHARGEERROR_CREDITCARD_HUM';
import HUMPCIWarningMessage from '@salesforce/label/c.HUMPCIWarningMessage';
import HUMPCIErrorMessage from '@salesforce/label/c.HUMPCIErrorMessage';
import createAndEditCreditCardSuccessMessage from "@salesforce/label/c.CreateAndEditCreditCardSuccessMessage";
import { getPCIToken } from './pciHelper';
import { cards, getCardImage, getCardType } from './creditconfig';
const CARD_NUMBER_SPACE = '  ';
const NON_AMEX_CARD_NUMBER_LENGTH = 16;
const AMEX_CARD_NUMBER_LENGTH = 15;
const DEFAULT_CARD_NUMBER_INPUT_MAX_LENGTH = 16;

export default class PharmacyCreateAndEditCreditCardHum extends LightningElement {

    @api
    creditCardData;

    @api
    recordId;

    @api
    enterpriseId;

    @api
    networkId;


    @api order;

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

    WarningMessage = HUMPCIWarningMessage;
    errorMessage = HUMPCIErrorMessage;
    attachedOrders = false;
    creditCardOwnerName;
    previousAutoChargeVal;
    AcknowledgedMsg = false;
    isAcknowledged = false;
    creditCardDataObj = {};
    showCaseCommentBox = false;

    @track isPharmacyTemp = true;

    label = {
        createAndEditCreditCardSuccessMessage
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
            this.creditCardIsSpendingAcc = 'false';
            this.creditCardIsAutoCharge = 'false';
            this.creditCardStatus = 'true';
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
        this.creditCardFirstName = this.selectedRow.FirstName;
        this.creditCardMiddleName = this.selectedRow.MiddleName;
        this.creditCardLastName = this.selectedRow.LastName;
        this.creditCardExpDate = `${this.selectedRow?.ExpirationMonth < 10 ? '0' + this.selectedRow?.ExpirationMonth : this.selectedRow?.ExpirationMonth}/${this.selectedRow?.ExpirationYear.slice(2)}`;
        this.cardType = this.selectedRow.CreditCardType;
        this.creditCardIsAutoCharge = this.selectedRow.IsAutoCharge;
        this.displayedIsAutoCharge = this.selectedRow.IsAutoCharge === 'true' ? true : false;
        this.previousAutoChargeVal = this.selectedRow.IsAutoCharge === 'true' ? 'Y' : 'N';
        this.creditCardStatus = this.selectedRow.IsActive;
        this.displayedStatus = this.selectedRow.IsActive === 'true' ? 'A' : 'I';
        this.isAutoChargeInputDisabled = this.selectedRow.IsActive === 'true' ? false : true;
        this.creditCardKey = this.selectedRow.CreditCardKey;
        this.creditCardExpirationYear = this.selectedRow.ExpirationYear;
        this.creditCardExpirationMonth = `${this.selectedRow.ExpirationMonth < 10 ? '0' + this.selectedRow.ExpirationMonth : this.selectedRow.ExpirationMonth}`;
        this.creditCardIsSpendingAcc = this.selectedRow.IsSpendingAccount;
        this.determineEditCardNumberString(this.selectedRow.CreditCardLast4Digits);
        this.setOwnerNameDisplay();
    }

    determineEditCardNumberString(lastFourDigits) {
        let cardNumberString;

        if (this.cardType !== 'A') {
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

                if (this.cardType !== 'A') {
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
        if (this.creditCardData.CreditCard) {
            const checkForAutoChargeCards = function (card) {
                return card.IsAutoCharge === 'true';
            }
            let autoChargeInput = this.template.querySelector('.auto-charge-input');
            if (autoChargeInput.checked &&
                (this.componentModeSetting === 'new' || this.selectedRow.IsAutoCharge !== 'true') &&
                this.creditCardData.CreditCard.some(checkForAutoChargeCards)) {
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
        this.creditCardIsAutoCharge = e.target.checked.toString();
        this.displayedIsAutoCharge = e.target.checked;
        this.validateIsAutoCharge();
    }
    handleStatusChange(e) {
        if (e.target.value === 'A') {
            this.creditCardStatus = 'true';
            this.isAutoChargeInputDisabled = false;
        } else {
            this.creditCardStatus = 'false';
            this.isAutoChargeInputDisabled = true;
            this.displayedIsAutoCharge = false;
            this.creditCardIsAutoCharge = 'false';
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
    }

    createOrEditCreditCard() {
        let isNew;
        return new Promise((resolve, reject) => {
            try {
                if (this.componentModeSetting === 'new') {
                    this.getTempToken()
                        .then(response => {
                            this.creditCardToken = response;
                            this.tokenRetrieved = true;
                            isNew = 'true';
                            this.addUpdateCreditCard(isNew).then(result => {
                                resolve(result);
                            }).catch(error => {
                                this.isLoaded = true;
                                reject(error);
                            })
                        }).catch(error => {
                            this.tokenRetrieved = false;
                            this.isLoaded = true;
                            reject(error);
                        });
                } else if (this.componentModeSetting === 'edit') {
                    isNew = 'false';
                    this.addUpdateCreditCard(isNew).then(result => {
                        resolve(result);
                    }).catch(error => {
                        reject(error);
                    })
                }
            } catch (error) {
                this.isLoaded = true;
                reject(error);
            }
        });
    }

    addUpdateCreditCard(isNew) {
        return new Promise((resolve, reject) => {
            createOrEditCreditCardInfo({
                enterprise: this.enterpriseId, sCreditCardKey: this.creditCardKey,
                exMonth: this.creditCardExpirationMonth, exYear: this.creditCardExpirationYear, sActive: this.creditCardStatus, sAutoCharge: this.creditCardIsAutoCharge,
                sFSA: this.creditCardIsSpendingAcc, sFirstName: this.creditCardFirstName, sMiddleName: this.creditCardMiddleName,
                sLastName: this.creditCardLastName, sTokenKey: this.creditCardToken, sCreditType: this.cardType,
                isInsert: isNew, networkID: this.networkId, sRecordId: this.recordId
            }).then(result => {
                let data = JSON.parse(result);
                if (data != null && JSON.stringify(data).toLowerCase().includes('faultcode')) {
                    this.isSaveSuccessful = false;
                    this.attachedOrders = false;
                } else if (data != null && Array.isArray(data.EditMemberResponse.Orders.Order) && data.EditMemberResponse.Orders.Order.length == 0) {
                    this.isSaveSuccessful = true;
                    this.attachedOrders = false;
                }
                else if (data != null && Array.isArray(data.EditMemberResponse.Orders.Order) && data.EditMemberResponse.Orders.Order.length > 0) {
                    this.isSaveSuccessful = false;
                    this.attachedOrders = true;
                    this.errorMessage = `${this.errorMessage} ${data.EditMemberResponse.Orders.Order.map(order => order.orderKey).join(', ')}`;
                }
                resolve(true);
            }).catch(error => {
                this.isSaveSuccessful = false;
                reject(false);
            });
        });
    }

    validateInputs() {
        this.validateCardNumber();
        this.validateExpDate();
        [this.creditCardExpirationMonth, this.creditCardExpirationYear] = this.creditCardExpDate.split('/');
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

        if (this.isAcknowledged == false && this.previousAutoChargeVal == 'Y' && statusInput.value == 'I' && this.creditCardIsAutoCharge == 'false') {
            this.AcknowledgedMsg = true;
        }
        else {
            this.AcknowledgedMsg = false;
        }
        if (this.AcknowledgedMsg == false && cardNumInput.checkValidity() && dateInput.checkValidity() && firstNameInput.checkValidity() && lastNameInput.checkValidity() && autoChargeInput.checkValidity() && statusInput.checkValidity()) {
            this.isLoaded = false;
            this.createOrEditCreditCard().then(result => {
                if (result) {
                    if (this.isSaveSuccessful && !this.attachedOrders) {
                        this.isLoaded = true;
                        this.creditCardDataObj = {};
                        this.creditCardDataObj = {
                            header: "Successfully Processed",
                            caseComment: this.generateCaseComment(cardNumInput, dateInput, autoChargeInput, statusInput),
                            source: "Credit Card",
                            tablayout: false,
                            message: this.label.createAndEditCreditCardSuccessMessage,
                        }
                        this.showCaseCommentBox = true;
                        this.resetProperties();
                    } else if (!this.isSaveSuccessful && this.attachedOrders) {
                        this.isLoaded = true;
                        this.dispatchEvent(new ShowToastEvent({
                            title: 'An Error Occurred',
                            message: this.errorMessage,
                            variant: 'error',
                            mode: 'dismissable',
                        }));
                        this.errorMessage = HUMPCIErrorMessage;
                    }
                    else if (!this.isSaveSuccessful && !this.attachedOrders) {
                        this.isLoaded = true;
                        this.dispatchEvent(new ShowToastEvent({
                            title: 'An Error Occurred',
                            message: 'An error occurred while attempting to save the card information',
                            variant: 'error',
                        }));
                    }
                }
                this.isLoaded = true;
            }).catch(error => {
                this.isLoaded = true;
            })
        }
    }

    generateCaseComment(cardNumInput, dateInput, autoChargeInput, statusInput) {
        let creditCardComment;
        if (this.componentModeSetting === 'new') {
            let cardnumber = cardNumInput.value;
            if (cardnumber) {
                cardnumber = cardnumber.substring(cardnumber.length - 4);
            }
            let sCaseComments = `Added ${getCardType(this.cardType)} credit card ending ${cardnumber} , Expiration: ${this.creditCardExpDate} , Auto Charge: ${this.creditCardIsAutoCharge === 'false' ? ' No' : this.creditCardIsAutoCharge === 'true' ? ' Yes' : ''}, Status: ${this.creditCardStatus === 'true' ? ' Active' : this.creditCardStatus === 'false' ? ' Inactive' : ''}`;
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
            let ccExpOldMonth = this.selectedRow.ExpirationMonth < 10 ? '0' + this.selectedRow.ExpirationMonth : this.selectedRow.ExpirationMonth;
            let ccExpOldYear = this.selectedRow.ExpirationYear != null ? this.selectedRow.ExpirationYear.substring(2) : '';
            if (this.creditCardExpDate && this.creditCardExpDate.includes('/')) {
                if ((this.creditCardExpDate.split('/')[0] != ccExpOldMonth) || (this.creditCardExpDate.split('/')[1] != ccExpOldYear)) {
                    sCaseCommentsOld += ` Expiration: ${ccExpOldMonth}/${ccExpOldYear}`;
                    sCaseCommentsNew += ` Expiration: ${this.creditCardExpDate.split('/')[0]}/${this.creditCardExpDate.split('/')[1]}`;
                }
            }
            if (this.creditCardIsAutoCharge) {
                let ccAutochargeOld = this.selectedRow.IsAutoCharge;
                let ccAutochargeNew = this.creditCardIsAutoCharge;
                if (ccAutochargeNew != ccAutochargeOld) {
                    sCaseCommentsOld += ` Auto Charge: ${ccAutochargeOld === 'false' ? ' No' : ccAutochargeOld === 'true' ? ' Yes' : ''}`;
                    sCaseCommentsNew += ` Auto Charge: ${ccAutochargeNew === 'false' ? ' No' : ccAutochargeNew === 'true' ? ' Yes' : ''}`;
                }
            }
            if (this.creditCardStatus) {
                let ccOldstatus = this.selectedRow.IsActive;
                let ccNewStatus = this.creditCardStatus;
                if (ccOldstatus != ccNewStatus) {
                    sCaseCommentsOld += ` Status: ${ccOldstatus === 'false' ? ' Inactive' : ccOldstatus === 'true' ? ' Active' : ''}`;
                    sCaseCommentsNew += ` Status: ${ccNewStatus === 'false' ? ' Inactive' : ccNewStatus === 'true' ? ' Active' : ''}`;
                }
            }
            creditCardComment = 'Updated credit card ending in ' + this.selectedRow.CreditCardLast4Digits + ' from ' + sCaseCommentsOld + ' to ' + sCaseCommentsNew;
            if (this.order) {
                this.dispatchEvent(new CustomEvent('creditcardcomment', {
                    detail: {
                        'CreditCardComment': creditCardComment
                    }
                }));
            }
            return `Updated credit card ending in ${this.selectedRow.CreditCardLast4Digits} from ${sCaseCommentsOld} to ${sCaseCommentsNew}`;
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