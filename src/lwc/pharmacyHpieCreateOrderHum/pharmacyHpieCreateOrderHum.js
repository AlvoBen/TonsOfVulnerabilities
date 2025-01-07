/*
Function        : LWC PharmacyHpieCreateOrderHum.js

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Vishal Shinde                  29/02/2024                     US - 5142800-Mail Order Management - Pharmacy - "Prescriptions & Order Summary" tab - Prescriptions – Create Order
*****************************************************************************************************************************/
import { LightningElement,track,api } from 'lwc';
import poboxerror from "@salesforce/label/c.POERROR_PHARMACY_HUM";
import ExpiredCreditCardErr from "@salesforce/label/c.ExpiredCreditCardErr";
import ordernotprocessederror from "@salesforce/label/c.OrderNotProcessedErr";
import { CreateOrderRequest_DTO_HUM } from './CreateOrderRequest_DTO_HUM';
import { createFills_DTO_HUM } from './createFills_DTO_HUM';
import { ShowToastEvent } from 'lightning/platformShowToastEvent'; 
import { createOrders,createfills } from 'c/pharmacyHPIEIntegrationHum'; 
const CREDITCARD_AUTO_CONST = '(Auto) ';
const CREDTCARD_HIDDEN_NUMBER = ' ********'; 
const CREDITCARD_EXP_CONST = ' exp ';
const EDIT_CARD_ERROR = ' You must Select a Card.';
import { getFormatDate } from 'c/crmUtilityHum';
import PHARMACY_REGULAR_MAIL from "@salesforce/label/c.PHARMACY_REGULAR_MAIL";
import PHARMACY_EXPEDITED_CHARGE from "@salesforce/label/c.PHARMACY_EXPEDITED_CHARGE";
import PHARMACY_EXPEDITED_NO_CHARGE from "@salesforce/label/c.PHARMACY_EXPEDITED_NO_CHARGE";
import PHARMACY_SHIPPINGMETHOD_FIVE_ONE_ONE from "@salesforce/label/c.PHARMACY_SHIPPINGMETHOD_FIVE_ONE_ONE";
import PHARMACY_SHIPPINGMETHOD_ONE from "@salesforce/label/c.PHARMACY_SHIPPINGMETHOD_ONE";
import PHARMACY_SHIPPINGMETHOD_TWO from "@salesforce/label/c.PHARMACY_SHIPPINGMETHOD_TWO";
const ORDRERROR = 'ORDR0023';

const INVOICE_CODE = 6;
const INVOICE_DESC = 'BILL PAYMENT LATER'; 
 
const CARD_CODE = 2;
const CARD_DESC = 'PAYMENT CARD';


export default class PharmacyHpieCreateOrderHum extends LightningElement {   

    @track _demographicsDetails;
    @track _financeDetails;
    @track _preferenceDetails;
    @track _prescriptions;
    @api enterpriseId; 
    @api recordId;
    @api userId;
    @api finalPrescriptions;
    @track addPrescriptionsFlag= false; 
    @track demographicsDetailsData; 
    @track selectedShippingAddress={};  
    @track loaded = true;
    @track sMinDate;
    @track sStartDate;
    @track submitDisabled; 
    @track capType;
    @track preferenceDetailsData;
    @track prescriptionsData;
    @track bPrescriptionExist = true;
    @track createAndEditCreditCardComponentMode;
    @track isCreateAndEditCreditCardVisible; 
    @track paymentMethod;
    @track lstPaymentMethods = [];
    @track selectedRowData;
    @track creditCardData;
    @track creditKey = 0;
    @track selectedShippingMethod='1';
    @track pharmacyDemoDetails;
    @track isSubmit = true; 
    @track isCreditCard = false;
    @track isPharmacyTemp = true;
    @track showCaseCommentBox = true;
    @track billingKey;
    @track shippingKey;
    @track addressData;
    @track arrayPrescription=[];
    @track creditCardComment; 
    @track isCreateOrder = true;
    @track isCaseExist;
    @track paymentMethodCode;
    @track  paymentMethodDesc;
    @track creditCard = [];
    @track  paymentDate ;
    demoDetailLoaded = false;
    @track chkCreditCardExpiryBol= false;
    @track ordermessage = '';
    @track orderId;
    @api accData;  
    @track consentGivenRxKeys =[];


    @api
    get prescriptions() {
        return this._prescriptions;
    }
  
    set prescriptions(value) {
      if (value) {
        this._prescriptions = value;
        this.prescriptionsData = JSON.parse(JSON.stringify(this.prescriptions));
        this.bPrescriptionExist = this.prescriptions.length > 0 ? false : true;
      }
    }

    @api
    get preferenceDetails() {
        return this._preferenceDetails;
    }
  
    set preferenceDetails(value) {
      if (value) {
        this._preferenceDetails = value;
        this.preferenceDetailsData = JSON.parse(JSON.stringify(this.preferenceDetails));
  
        if (this.preferenceDetailsData.preference.capType != null) {
          this.capType = this.preferenceDetailsData?.preference?.capType?.code ?? '';
        }
      }
    }

    @api
    get demographicsDetails() {  
        return this._demographicsDetails;
    }

    set demographicsDetails(value) {
      if (value) {
        this._demographicsDetails = value;
        this.processDemoDetails();
        this.demoDetailLoaded = true;
        if (this.template.querySelector('c-pharmacy-hpie-modify-shipping-address') != null) {
          this.template.querySelector('c-pharmacy-hpie-modify-shipping-address').setDemographicsDetails(JSON.stringify(this.demographicsDetails.Addresses));
        }
      }
  }

  @api
  get financeDetails() {
      return this._financeDetails;
  }

  set financeDetails(value) { 
    if (value) {
      this._financeDetails = value;
      this.processFinanceDetails(); 
    }
  }
      
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

    connectedCallback(){
        this.validateInputs(); 
        this.initialDates(); 
    } 

    @api
    addPrescription(data) {
        this.prescriptions = data;
        this.prescriptionsData = JSON.parse(JSON.stringify(this.prescriptions))
    }

    initialDates() { 
      let sDate = new Date();
      this.sStartDate = sDate.toISOString().substring(0, 10);
      this.sMinDate = sDate.toISOString().substring(0, 10);
      this.releaseDate = String(sDate.getMonth() + 1).padStart(2, '0') + '/' + String(sDate.getDate()).padStart(2, '0') + '/' + sDate.getFullYear();
    }


    processDemoDetails() {  
      this.demographicsDetailsData = JSON.parse(JSON.stringify(this.demographicsDetails)); 
      this.addressData = this.demographicsDetailsData?.Addresses;
      this.pharmacyDemoDetails =JSON.stringify(this.demographicsDetails?.Addresses);
  
      this.billingKey =  this.addressData != null ? this.addressData.find(k => k.active === true && k.z0type.description === 'BILLING')?.key:'';
      this.selectedShippingAddress = this.addressData != null ? this.addressData.find(k => k.active === true && k.z0type.description === 'SHIPPING') : {};
      this.shippingKey = this.selectedShippingAddress?.key ?? '';
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


    handleShippingChange(event) { 
      this.selectedShippingMethod = event.target.value;
      this.validateInputs();
    }

    handlePaymentChange(event) { 
      this.paymentMethod = +event.target.value; 
      this.validateInputs();
    }


    handleSubmitClick(event) {  
        this.isCaseExist = event.detail.ExistCase;
        this.selectedShippingMethod = this?.selectedShippingMethod ? parseInt(this.selectedShippingMethod):'0';
        this.paymentDate = getFormatDate(new Date().toISOString(), 'yyyy-mm-dd');
        if (this.paymentMethod === 111) {
          this.creditCard = null; 
          this.paymentMethodCode = INVOICE_CODE; 
          this.paymentMethodDesc = INVOICE_DESC;
          this.chkCreditCardExpiryBol=true
        } else {
          if (this.creditCardData.paymentCards && this.creditCardData.paymentCards.length > 0) { 
            this.creditCard = this.creditCardData.paymentCards.find(k => k.key === this.paymentMethod); 
            this.creditKey=Number(this.creditCard?.key); 
              this.paymentMethodCode = CARD_CODE;
              this.paymentMethodDesc = CARD_DESC;
              this.chkCreditCardExpiry(this.creditCard);
          }

        }
      if(this.chkCreditCardExpiryBol){
          const createFillRequest = new createFills_DTO_HUM(this.userId, this.enterpriseId, this.organization ?? 'HUMANA', this.prescriptionsData);
          this.createPrescriptionFills(JSON.stringify(createFillRequest))   
          .then(result => {
            let dataResult =JSON.parse(JSON.stringify(result)); 
            this.arrayPrescription=dataResult?.fills;
              const CreateEditOrderRequest = new CreateOrderRequest_DTO_HUM(this.userId,this.enterpriseId,this.selectedShippingMethod,this.releaseDate,this.creditKey ,this.shippingKey,
                this.billingKey,this.paymentDate,this.arrayPrescription,this.paymentMethodCode,this.paymentMethodDesc);
          this.callCreateOrder(CreateEditOrderRequest);
          }).catch(error => {
            this.displayToastEvent(this.label.ordernotprocessederror, 'error', 'Error!');
            this.selectedShippingMethod = '0';
            this.validateInputs(); 
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
        this.creditCardData.paymentCards.forEach(h => {
          if (selectedCreditCard.key == h.key) {
              if ((h.expirationMonth >= currentmonth && h.expirationYear >= currentyear) || (h.expirationMonth < currentmonth && h.expirationYear > currentyear)) {
              this.chkCreditCardExpiryBol= true
              } else
              this.chkCreditCardExpiryBol= false
              }
          })
      }
  }

  createPrescriptionFills(request) {
    return new Promise((resolve, reject) => {
      createfills(request)
          .then(result => {
              resolve(result);
          }).catch(error => {
              reject(false)
          })
    });
  }

    callCreateOrder(request) { 
        createOrders(JSON.stringify(request))
        .then(result => {
            if (result != null && result != undefined) {
              if (result != null && Object.hasOwn(result.order, 'orderId')) {
                  if (result && result?.order?.orderId) {  
                    this.orderId =result?.order?.orderId;
                    this.ordermessage = `Order #${result?.order?.orderId} has been successfully submitted`;
                    this.loaded = true;
                    this.createOrderDataObj = {};
                    this.createOrderDataObj = {
                        header: "Successfully Processed", 
                        source: "Create Order",
                        tablayout: false,
                        message: this.ordermessage,  
                        caseComment: this.generateCaseComment(result?.order?.orderId),
                        redirecttocaseedit: true,
                        createOrderCheck : true  
                        }
                      if (this.template.querySelector('c-generic-case-comment-logging') != null) {
                          this.template.querySelector('c-generic-case-comment-logging').handleLog(this.createOrderDataObj, this.isCaseExist);
                      }
                  } 
              }
              else if (Object.hasOwn(result, 'errors') && Array.isArray(result.errors) && result.errors.length > 0 && result.errors.some(err => err.code === ORDRERROR)) {
                  this.displayToastEvent(this.label.poboxerror, 'error', 'Error!');
                  this.loaded = true;
                  this.selectedShippingMethod = '0';
                  this.validateInputs(); 
              } else {
                  this.displayToastEvent(this.label.ordernotprocessederror, 'error', 'Error!');
                  this.loaded = true;
                  this.selectedShippingMethod = '0';
                  this.validateInputs(); 
              }
            }
            else {
            this.displayToastEvent(this.label.ordernotprocessederror, 'error', 'Error!'); 
            this.loaded = true;
            this.selectedShippingMethod = '0';
            this.validateInputs(); 
            }
        }).catch(error => {
            this.displayToastEvent(this.label.ordernotprocessederror, 'error', 'Error!')
            this.loaded = true;
            this.selectedShippingMethod = '0';
            this.validateInputs(); 
        })
    }

    handleCaseNumber(event){ 
      if(event.detail.msg){
        this.dispatchEvent(new ShowToastEvent({ 
          title: 'Success!',
          message: 'Order Created with Case Number :'+event.detail.msg,
          variant: 'success',
        }));
      }
    }

    generateCaseComment(orderkey) {
      let sCaseComment = `Placed order ${orderkey} for the following:`;
      this.prescriptionsData.forEach(h => {
          sCaseComment += `Rx# ${h.key},`
      });
      sCaseComment = sCaseComment.replace(/,\s*$/, ".");
      if (this.paymentMethod === '111') {
          sCaseComment += ` Payment Method: Invoice`;
      } else {
        let creditCard = {};
        creditCard = this.financeDetails.paymentCards.find(k => k.key === this.paymentMethod); 
          if (creditCard != undefined && creditCard != '') {
              sCaseComment += ` Payment Method: Credit Card ${creditCard.tokenKey.slice(-4)} - ${creditCard.expirationMonth}/${creditCard.expirationYear}`;
          }
      }
      if (this.selectedShippingAddress.addressLine2.length > 0) { 
          sCaseComment += `, Shipping Address: ${this.selectedShippingAddress.addressLine1},${this.selectedShippingAddress.addressLine2},${this.selectedShippingAddress.city},${this.selectedShippingAddress.stateCode},${this.selectedShippingAddress.zipCode}`;
      } else {
          sCaseComment += `, Shipping Address: ${this.selectedShippingAddress.addressLine1},${this.selectedShippingAddress.city},${this.selectedShippingAddress.stateCode},${this.selectedShippingAddress.zipCode}`;
      }
      sCaseComment += `, Release Date: ${this.releaseDate}` + '\n';
      if (this.creditCardComment != '' && this.creditCardComment != undefined) sCaseComment += this.creditCardComment + '. ';
      if (this.shippingAddressComment != '' && this.shippingAddressComment != undefined) sCaseComment += this.shippingAddressComment;

      return sCaseComment;
  }

  getCreditCardComment(event) {
    if (event.detail) {
        this.creditCardComment = event.detail.CreditCardComment; 
    }
  }

updateShippingAddress(event) { 
  if (event.detail) {
      this.selectedShippingAddress = event.detail.ShippingAddress;  
      this.shippingKey = this.selectedShippingAddress?.key ?? ''; 
  }
}

getAddressComments(event) { 
  if (event.detail) {
      this.shippingAddressComment = event.detail.ShippingAddressComment;
  }
}

handleAddAddress() {
  this.showCaseCommentBox = false;
}

handleShowCase() {
  this.showCaseCommentBox = true;
}

handleAddAddress() {
  this.showCaseCommentBox = false;
}

    
    handleInvalidReleaseDate(event) {
        this.submitDisabled = true;
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
        this.submitDisabled = (isDropdownfilled && this.paymentMethod && (this.selectedShippingMethod!='0') && validReleaseDate && !this.bPrescriptionExist) ? false : true;
    
      }

      handleRemovePrescription(event) {   
        if (event.detail) {
          let KeyValue=event.detail.key
          this.prescriptionsData = this.prescriptionsData.filter(k => k.key != event.detail.key);
          this.bPrescriptionExist = this.prescriptionsData.length > 0 ? false : true;
          this.handlePrescriptionKeyAdd(KeyValue)
        }
      }

      handlePrescriptionKeyAdd(key){ 
        this.dispatchEvent(new CustomEvent('addprescriptionkey', { 
          detail: {
              msg: key
          }
      }))
  }

      handleNewClick() {
        this.renderNewCardForm()
            .then(() => this.scrollToCreditCardView())
            .catch((error) => console.log('handleNew ' + error));
      }

      handleEditClick() {
        this.renderEditCardForm()
        .then(() => this.scrollToCreditCardView())
        .catch((error) => console.log('handleEdit ' + error));
      }

      handleSave() {
        this.isCreateAndEditCreditCardVisible = false;   
        this.createAndEditCreditCardComponentMode = '';
        this.dispatchEvent(new CustomEvent('ordercreditcardsave'));
      }

      handleCallDemographics() {  
        this.dispatchEvent(new CustomEvent('calldemographics'));  
      }

      handleCancel() {
        this.isCreateAndEditCreditCardVisible = false;
        this.showCaseCommentBox = true;
        this.createAndEditCreditCardComponentMode = '';
        this.scrollToTop();
      }

      handleCreditKey(event) {
        this.creditKey = event?.detail?.creditKey; 
      }

      goToSummary() { 
        this.dispatchEvent(new CustomEvent("gotosummary"));
      }

      handleCancelClick() {
        this.dispatchEvent(new CustomEvent("gotosummary")); 
      }

      handleFinishLogging() {
        this.showCaseCommentBox = false;
        this.goToSummary();
      }


      handleAddPrescription(){
        this.addPrescriptionsFlag= true;
        this.dispatchEvent(new CustomEvent('popoverprescription', {
          detail: {
              msg: this.addPrescriptionsFlag
          }
      }))
  }

 
    displayToastEvent(message, variant, title) {
      this.dispatchEvent(new ShowToastEvent({
          title: title,
          message: message,
          variant: variant,
          mode: 'dismissable'
      }));
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

  renderNewCardForm() {
    return new Promise((resolve) => {
        if (this.createAndEditCreditCardComponentMode !== 'edit') {
            this.createAndEditCreditCardComponentMode = 'new';
        }
        this.isCreateAndEditCreditCardVisible = true;
        resolve('');
    });
  }

  renderEditCardForm() {
    return new Promise((resolve) => {
        this.isCreateAndEditCreditCardVisible = false;
        this.createAndEditCreditCardComponentMode = '';
        if (this.paymentMethod != '111' && this.paymentMethod != null
            && this.paymentMethod != undefined) {
            if (this.createAndEditCreditCardComponentMode !== 'new') {
                this.createAndEditCreditCardComponentMode = 'edit';
            }
            this.selectedRowData = null;
            this.selectedRowData = this.creditCardData.paymentCards.find(k => k.key === this.paymentMethod);
            this.creditKey = this.paymentMethod;
            this.isCreateAndEditCreditCardVisible = true;
            resolve('');
        } else {
            this.createAndEditCreditCardComponentMode = '';
            this.displayToastEvent(EDIT_CARD_ERROR, 'warning', 'warning!');
        }
    });
  }
}
