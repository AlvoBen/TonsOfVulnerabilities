/*
JS Controller        : PharmacyCreditCardInformationHum
Version              : 1.0
Created On           : 11/9/2021
Function             : Component to display to pharmacy member credit card information

Modification Log:
* Developer Name                      Date                         Description
* Jonathan Dickinson                   11/9/2021                   Original Version
* Nirmal Garg                         04/05/2022                    US-3198914 - Added case comment logic
* Abhishek Mangutkar                  02/06/2022                   US3996413- Fixing Pharmacy Issues
*------------------------------------------------------------------------------------------------------------------------------
*/
import { LightningElement, wire, api, track } from 'lwc';
import getExpiredAndExpiringSoonIconURLs from '@salesforce/apex/MemberIcons_LC_HUM.getExpiredAndExpiringSoonIconURLs';
import { CurrentPageReference } from 'lightning/navigation';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import invokeOneTimeCreditCardPayment from '@salesforce/apexContinuation/PharmacyFinancial_LC_HUM.invokeOneTimeCreditCardPayment';
import { publish, MessageContext } from 'lightning/messageService';
import humanaPharmacyLMS from '@salesforce/messageChannel/pharmacyLMSChannel__c';
import getMemberDetails from "@salesforce/apexContinuation/Pharmacy_LC_HUM.invokeGetMemberService";
import creditCardPaymentSuccessMessage from "@salesforce/label/c.CreditCardPaymentSuccessMessage";
import cardpaymentsuccessmsg from '@salesforce/label/c.FINANCIAL_CARDPAYMENT_SUCESS';
import cardpaymenterrormessage from '@salesforce/label/c.FINANCIAL_CARDPAYMENT_ERROR';
const ICON_ARROW_UP = 'utility:arrowup';
const ICON_ARROW_DOWN = 'utility:arrowdown';
const CREDTCARD_HIDDEN_NUMBER = ' ********';
const CREDITCARD_EXP_CONST = ' exp ';
const CREDITCARD_AUTO_CONST = '(Auto) ';
import pubSubHum from 'c/pubSubHum';
export default class PharmacyCreditCardInformationHum extends LightningElement {

    @api recordId;
    @api enterpriseId;
    @api networkId;
    @api pharmacydemographicdetails;
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
    balanceDue;
    selectedCardComboData;
    showConfirmationModal;
    selectedcardTypeLiteral;
    selectedCardLast4Digits;
    enteredAmount = null;
    enteredDate = this.getTodayDate();
    minDateValue = this.getTodayDate();

    isCardNotAvailable = true;

    isDataExists;
    loaded = true;
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

    async connectedCallback() {
        if (this.pharmacydemographicdetails != null) {
            this.creditCardData = this.pharmacydemographicdetails?.CreditCardsDetail ?? null;
        }
    }

    handleEditClick(event) {
        if (event && event?.detail) {
            this.isCreateAndEditCreditCardVisible = false;
            this.selectedRowData = this.pharmacydemographicdetails && this.pharmacydemographicdetails?.CreditCardsDetail
                && this.pharmacydemographicdetails?.CreditCardsDetail?.CreditCard && Array.isArray(this.pharmacydemographicdetails.CreditCardsDetail.CreditCard)
                && this.pharmacydemographicdetails.CreditCardsDetail.CreditCard.length > 0 ?
                this.pharmacydemographicdetails.CreditCardsDetail.CreditCard.find(k => k.CreditCardKey === event?.detail?.carddata?.CreditCardKey) : null;
            this.selectedCardComboData = this.selectedRowData.CreditCardKey;
            this.showSelectedCardData = true;
            this.createAndEditCreditCardComponentMode = 'edit';
            this.isCreateAndEditCreditCardVisible = true;
            this.scrollToCreditCardView();
        }
    }

    getTodayDate() {
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
        this.callGetMemberService();
        this.scrollToCardDetailsView();
		this.disableNewCreditCardButton = false;
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

    callGetMemberService() {
        getMemberDetails({
            memID: this.enterpriseId,
            networkId: this.networkId,
            sRecordId: this.recordId
        }).
            then(data => {
                if (data) {
                    let response = JSON.parse(data);
                    this.pharmacydemographicdetails = response.objPharDemographicDetails;
                    this.updateChildComponents();
                }
            }).catch(e => {
                console.log('error ' + e);
            })
    }

    updateChildComponents() {
        try {
            let payload = { messageDetails: this.pharmacydemographicdetails?.CreditCardsDetail?.CreditCard ?? 'No Credit Card', MessageName: "CreditCardDetailsUpdate" };
            
            pubSubHum.fireEvent(this.pageRef, 'CreditCardDetailsUpdate', payload);
            if (this.template.querySelector('c-pharmacy-credit-card-details-hum') != null) {
                this.template.querySelector('c-pharmacy-credit-card-details-hum').updateData(this.pharmacydemographicdetails.CreditCardsDetail);
            }
            if (this.template.querySelector('c-pharmacy-credit-card-payment-hum') != null) {
                this.template.querySelector('c-pharmacy-credit-card-payment-hum').updateData(this.pharmacydemographicdetails.CreditCardsDetail);
            }
        }
        catch (err) {
            console.log('An error occured -> ', err);
        }
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