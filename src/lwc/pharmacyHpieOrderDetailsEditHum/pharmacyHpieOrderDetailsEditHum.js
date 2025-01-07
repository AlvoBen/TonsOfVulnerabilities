/*
Function        : LWC PharmacyHpieOrderDetailsEditHum.

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Swapnali Sonawane              10/23/2023                    US - 5058187 Pharmacy Edit Order
* Jonathan Dickinson             12/04/2023                    US - 5058187
* Jonathan Dickinson             02/29/2024                    User Story 4835317: T1PRJ0865978 - MF 27481 - C12; Mail Order Management; Pharmacy - Mail Order Pharmacy page gap- Member Consent (Lightning)
* Jonathan Dickinson             03/01/2024                    User Story 5058187: T1PRJ1295995 - (T1PRJ0870026)- MF 27409 HPIE/CRM SF - Tech - C12 Mail Order Management - Pharmacy - "Prescriptions & Order summary" tab - "Edit Order'
*****************************************************************************************************************************/
import { LightningElement, api, track } from 'lwc';
import ordernotprocessederror from "@salesforce/label/c.OrderNotProcessedErr";
import ExpiredCreditCardErr from "@salesforce/label/c.ExpiredCreditCardErr";
import poboxerror from "@salesforce/label/c.POERROR_PHARMACY_HUM";
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import PHARMACY_REGULAR_MAIL from "@salesforce/label/c.PHARMACY_REGULAR_MAIL";
import PHARMACY_EXPEDITED_CHARGE from "@salesforce/label/c.PHARMACY_EXPEDITED_CHARGE";
import PHARMACY_EXPEDITED_NO_CHARGE from "@salesforce/label/c.PHARMACY_EXPEDITED_NO_CHARGE";
import PHARMACY_SHIPPINGMETHOD_FIVE_ONE_ONE from "@salesforce/label/c.PHARMACY_SHIPPINGMETHOD_FIVE_ONE_ONE";
import PHARMACY_SHIPPINGMETHOD_ONE from "@salesforce/label/c.PHARMACY_SHIPPINGMETHOD_ONE";
import PHARMACY_SHIPPINGMETHOD_TWO from "@salesforce/label/c.PHARMACY_SHIPPINGMETHOD_TWO";
import OTC_Message from "@salesforce/label/c.OTC_Message";
const CREDTCARD_HIDDEN_NUMBER = ' ********';
const CREDITCARD_EXP_CONST = ' exp ';
const CREDITCARD_AUTO_CONST = '(Auto) ';
const EDIT_CARD_ERROR = ' You must Select a Card.';
const PHARMACY_MEMBERCONSENT_LOGNOTECODE_HUM = 'CONSENT';
const PHARMACY_MEMBERCONSENT_LOGNOTEMESSAGEFIRSTHALF_HUM ='Member consent given on ';
const PHARMACY_MEMBERCONSENT_LOGNOTEAT_HUM = ' at ';
const PHARMACY_MEMBERCONSENT_LOGNOTEFOR_HUM = ' for ';
const PHARMACY_MEMBERCONSENT_LOGNOTE_ORDER_NUM_HUM = 'Order #';
const ORDRERROR = 'ORDR0023';
const LOG_NOTE_ERROR = 'Problem in sending Log Notes. If you continue to receive this message, contact CSS HelpDesk.';
import { updateOrders } from 'c/pharmacyHPIEIntegrationHum';
import { addFamilyNote } from 'c/genericPharmacyLogNotesIntegrationHum';
import { UpdateOrderRequest_DTO_HUM } from './updateOrderRequest';
import { toastMsge, getFormatDate } from 'c/crmUtilityHum';

const INVOICE_CODE = 6;
const INVOICE_DESC = 'BILL PAYMENT LATER';

const CARD_CODE = 2;
const CARD_DESC = 'PAYMENT CARD';

export default class PharmacyHpieOrderDetailsEditHum extends LightningElement {

  @api enterpriseId;
  @api recordId;
  @api networkId;
  @api userId;
  @api accData;
  @api orderDetailsData;
  @api capType;
  @api memberConsent;
  @api orderparentnode;
  @api prescriptions;
  @api spayer;
  @api isMemberConsentRequired = false;
  consentGivenRxKeys = [];
  numberOfApprovedConsents;
  isMemberConsentQueue = false;

  @api
  get orderDetailsResponse() {
      return this._orderDetailsResponse;
  }
  
  set orderDetailsResponse(value) {
      this._orderDetailsResponse = value;
      this.processOrderDetails();
  }

  @api
  get pharmacydemodetails() {
      return this._pharmacydemodetails;
  }
  
  set pharmacydemodetails(value) {
      this._pharmacydemodetails = value;
      this.processDemoDetails();

      if (this.template.querySelector('c-pharmacy-hpie-modify-shipping-address') != null) {
        this.template.querySelector('c-pharmacy-hpie-modify-shipping-address').setDemographicsDetails(this.pharmacydemodetails);
      }
  }

  @api
  get financeDetails() {
      return this._financeDetails;
  }
  
  set financeDetails(value) {
      this._financeDetails = value;
      this.processFinanceDetails();
  }

  @track _orderDetailsResponse;
  @track _pharmacydemodetails;
  @track _financeDetails;
  sMaxDate;
  sMinDate;
  orderReleaseDate;
  displayOTCMessage;
  selectedShippingMethod = '1';
  lstPaymentMethods = [];
  commercialUser;
  creditCardData;
  paymentMethod;
  loaded = true;
  checkCode = false;
  deletedItems = [];
  deleteItemEvent = false;
  createAndEditCreditCardComponentMode;
  selectedRowData;
  isCreateAndEditCreditCardVisible;
  createOrderPrescriptionItems = [];
  @track calledFromEditOrder = true;
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
  label = {
    poboxerror,
    ordernotprocessederror,
    ExpiredCreditCardErr 
  }
  creditKey = 0;
  shippingKey;
  billingKey;
  shippingAddress;
  shippingHomeStreet;
  orderPrescriptions;
  
  connectedCallback() {
    this.setDateLimits();
  }
  
  setDateLimits() {
    let todaysdate = new Date();
    this.sMaxDate = todaysdate.toISOString().substring(0, 10);
    let minDate = new Date();
    minDate.setDate(todaysdate.getDate() - 90);
    this.sMinDate = minDate.toISOString().substring(0, 10);
  }

  processOrderDetails() {
    let releaseDate = this.orderDetailsResponse.releaseDate;
    if (releaseDate) {
      let rDate = new Date(releaseDate);
      rDate.setDate(rDate.getDate() + 1);
      this.orderReleaseDate = rDate.toISOString().substring(0, 10);
    }
    else {
      this.orderReleaseDate = '';
    }
    this.displayOTCMessage = this.orderDetailsResponse.OTC === 'Yes' ? true : false;

    this.shippingAddress = this.orderDetailsResponse?.shippingAddress;
    this.shippingHomeStreet = `${this.shippingAddress?.addressLine1 ?? ''} ${this.shippingAddress?.addressLine2 ?? ''}`;
    this.orderPrescriptions = JSON.parse(JSON.stringify(this.orderDetailsResponse?.orderPrescriptions));
    this.isMemberConsentQueue = this.orderDetailsResponse?.currentQueue?.toUpperCase() === 'MEMBER CONSENT' ? true : false;
    this.checkCode = this.orderDetailsResponse?.currentQueue?.toUpperCase() === 'FINANCE' ? true:false;   
  }

  processDemoDetails() {
    let pharmacydemodetails = JSON.parse(this.pharmacydemodetails);
    this.billingKey =  pharmacydemodetails != null ? pharmacydemodetails.find(k => k.active === true && k.z0type.description == 'BILLING')?.key:'';
    this.shippingKey = pharmacydemodetails != null ? pharmacydemodetails.find(k => k.active === true && k.z0type.description == 'SHIPPING')?.key : '';
  }

  processFinanceDetails() {

    this.lstPaymentMethods = [];

    this.lstPaymentMethods.push({
      label: 'Invoice',
      value: 111
    });
    this.creditCardData = {};
    if(this.financeDetails && Object.hasOwn(this.financeDetails, 'paymentCards') && Array.isArray(this.financeDetails.paymentCards) && this.financeDetails.paymentCards.length > 0) {
      Object.assign(this.creditCardData, this.financeDetails);
      this.creditCardData.paymentCards = this.creditCardData.paymentCards.filter(k => k.active === true);
      if (this.creditCardData.paymentCards && this.creditCardData.paymentCards.length > 0) {
  
        this.creditCardData.paymentCards.forEach(k => {
          this.lstPaymentMethods.push({
            label: k.autoCharge === true ? CREDITCARD_AUTO_CONST + k?.z0type?.description + CREDTCARD_HIDDEN_NUMBER + k?.tokenKey.slice(-4) + CREDITCARD_EXP_CONST + `${k.expirationMonth < 10 ? '0' + k.expirationMonth : k.expirationMonth}/${k.expirationYear.toString().slice(2)}` :
              k?.z0type?.description + CREDTCARD_HIDDEN_NUMBER + k?.tokenKey.slice(-4) + CREDITCARD_EXP_CONST + `${k.expirationMonth < 10 ? '0' + k.expirationMonth : k.expirationMonth}/${k.expirationYear.toString().slice(2)}`,
            value: k.key
          });
          if (k.autoCharge === true) {
            this.paymentMethod = k.key;
          }
        })
      }
    }
  }

  handlePaymentChange(event) {
    this.paymentMethod = Number(event.target.value);
  }
  handleDateChange(event) {
    this.dateChangedEvent = true;
    this.orderReleaseDate = event.detail.datevalue;
    if (event.detail.datevalue) {
      let selectedReleaseDate = event.detail.datevalue;
      const [year, month, day] = selectedReleaseDate.split('-');
      this.selectedDate = month + '/' + day + '/' + year;
    }
  }

  handleShippingChange(event) {
    this.selectedShippingMethod = event.target.value;
  }


  handleNewClick() {
    if (this.createAndEditCreditCardComponentMode !== 'edit') {
      this.createAndEditCreditCardComponentMode = 'new';
    }
    this.isCreateAndEditCreditCardVisible = true;
  }
  handleCreditKey(event) {
    this.creditKey = event?.detail?.creditKey;
  }
  handleEditClick() {
    this.isCreateAndEditCreditCardVisible = false;
    this.createAndEditCreditCardComponentMode = '';

    if (this.paymentMethod !== 111 && this.paymentMethod !== null
      && this.paymentMethod !== undefined) {
      this.createAndEditCreditCardComponentMode = 'edit'
      this.selectedRowData = null;
      this.selectedRowData = this.creditCardData.paymentCards.find(k => k.key === this.paymentMethod);
      this.creditKey = this.paymentMethod;
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

  @api saveeditorder() {
    this.editOrder();
  }
  editOrder() {
    this.loaded = false;
    let paymentDate = getFormatDate(new Date().toISOString(), 'yyyy-mm-dd');
    
    let isCOPY_250 = this.orderDetailsResponse?.currentQueue?.toUpperCase() === 'FINANCE' && this.orderDetailsResponse?.financeCode?.toUpperCase() === 'CPAY_250' ? true : false;

    this.preparePrescriptionScript();

    let paymentMethodCode;
    let paymentMethodDesc;
    let canUpdateOrder = false;

    if (this.paymentMethod === 111) {
      paymentMethodCode = INVOICE_CODE;
      paymentMethodDesc = INVOICE_DESC;
      canUpdateOrder = true;
    } else {
        paymentMethodCode = CARD_CODE;
        paymentMethodDesc = CARD_DESC;
        const paymentCard = this.creditCardData.paymentCards.find(k => k.key === this.paymentMethod);
        canUpdateOrder = paymentCard ? this.isCardExpired(paymentCard) : false;
    }

    if (canUpdateOrder) {
      let request = new UpdateOrderRequest_DTO_HUM(this.userId, this.enterpriseId,this.orderDetailsResponse?.orderId, this.selectedShippingMethod, this.orderReleaseDate, this.paymentMethod, this.shippingKey, 
        this.billingKey, paymentDate, this.createOrderPrescriptionItems, isCOPY_250, paymentMethodCode, paymentMethodDesc);
      updateOrders(JSON.stringify(request))
        .then((result) => { 
          if (result != null) {
            if (Object.hasOwn(result, 'order')) {
              if (result?.order?.orderId) {
                this.displayToastEvent('Order Number : ' + result?.order?.orderId, 'success', 'success!');
                
                if(this.consentGivenRxKeys.length > 0) {
                  this.sendLogNotes();
                }
                
                this.loaded = true;
                this.dispatchEvent(new CustomEvent('ordereditsuccess'));
              }
            } else if (Object.hasOwn(result, 'errors') && Array.isArray(result.errors) && result.errors.length > 0 && result.errors.some(err => err.code === ORDRERROR)) {
              this.displayToastEvent(this.label.poboxerror, 'error', 'Error!');
              this.loaded = true;
            }
            else {
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
        })
    } else {
      this.displayToastEvent(this.label.ExpiredCreditCardErr, 'error', 'Error!')
      this.loaded = true;
    }

  }

  isCardExpired(paymentCard) {
    let currentdate = new Date();
    let months;
    let expiMonth = paymentCard.expirationMonth;
    let expiYear = paymentCard.expirationYear;
    let expidate = new Date(expiYear, expiMonth - 1, 1);
    months = (expidate.getFullYear() - currentdate.getFullYear()) * 12;
    months -= currentdate.getMonth() + 1;
    months += expidate.getMonth();
    return months < 0 ? false : true;
  }

  sendLogNotes() {
    let notes = [];
    let message = `${PHARMACY_MEMBERCONSENT_LOGNOTEMESSAGEFIRSTHALF_HUM}${getFormatDate(new Date())}
    ${PHARMACY_MEMBERCONSENT_LOGNOTEAT_HUM}${new Date().toLocaleTimeString()}
    ${PHARMACY_MEMBERCONSENT_LOGNOTEFOR_HUM}${PHARMACY_MEMBERCONSENT_LOGNOTE_ORDER_NUM_HUM}
    ${this.orderDetailsResponse?.orderId}`;

    this.consentGivenRxKeys.forEach(() => {
        notes.push({
          noteCode:PHARMACY_MEMBERCONSENT_LOGNOTECODE_HUM,
          logNote: message
        });
    });

    addFamilyNote(this.userId, this.enterpriseId, this.organization ?? 'HUMANA', this.accData, notes)
      .then(result => {
        if (result === null || result === undefined) {
          this.displayToastEvent(LOG_NOTE_ERROR, 'error', 'Error!');
        } else {
          this.handleLogNoteAdded();
        }
      }).catch(error => {
        this.displayToastEvent(LOG_NOTE_ERROR, 'error', 'Error!');
      })
    this.consentGivenRxKeys = [];
  }

  handleDeleteScriptKey(event) {
    this.deletedItems.push(event?.detail?.deletescriptkey);
  }

  displaySuccessNotification() {
    toastMsge(
      'Success!',
      'The order was successfully updated!',
      'success',
      'sticky'
    );
  }
  handleCallDemographics() {
    this.dispatchEvent(new CustomEvent('calldemographics'));
  }
  handleSave() {
    this.isCreateAndEditCreditCardVisible = false;
    this.createAndEditCreditCardComponentMode = '';
    this.dispatchEvent(new CustomEvent('ordercreditcardsave'));
  }
  preparePrescriptionScript() {
    this.createOrderPrescriptionItems = [];
    this.numberOfApprovedConsents = 0;

    if (this.orderPrescriptions && this.orderPrescriptions.length > 0) {
      this.orderPrescriptions.forEach(h => {
        const scriptObject = {};
        scriptObject.scriptKey = h.key;
        if (this.deletedItems.includes(h.key)) {
          scriptObject.rxConsent = false;
        } else {
          scriptObject.rxConsent = this.orderDetailsResponse?.currentQueue?.toUpperCase() !== 'FINANCE' && this.consentGivenRxKeys.includes(h.key) ? true : null;
        }
        this.createOrderPrescriptionItems.push(scriptObject);
      })
    }
  }
  
  handleCancel() {
    this.isCreateAndEditCreditCardVisible = false;
    this.createAndEditCreditCardComponentMode = '';
  }

  updateShippingAddress(event) {
    if (event?.detail?.ShippingAddress?.key) {
      this.shippingKey = event.detail.ShippingAddress.key;
    }
  }

  handleConsentGiven(event) {
      // if we get this event, consent was given for the rx
      this.consentGivenRxKeys.push(event.detail.rxKey);
  }

  handleLogNoteAdded() {
    this.dispatchEvent(new CustomEvent('lognoteadded'));
  }
}