/******************************************************************************************************************************
LWC Name : phoneBookHUM.js
Function    : This LWC component contains the phonebook related functionality

Modification Log: 
Developer Name           Code Review              Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Arpit Jain/Navajit Sarkar                     03//2021                     PhoneBook Development(Consult and Blind Transfer)
* Arpit Jain                                    07/12/2021                   US-2357061 : IVR Call End or Transfer
* Arpit jain                                    08/02/2021                   PubSub Change : pubSubComponent replaced by pubSubHum
* Harshada Kamble                               06/01/2022                   US2191493_DF-4988 & US2649919_DF-4889 fix
* Harshada Kamble                               08/19/2022                   User Story 3653389: PCC Genesys Migration - Transfer to Provider Survey
* Harshada Kamble                               09/30/2022                   User Story 3791679: T1PRJ0036776: Genesys Softphone Transfer Phone Book Not Returning All Routing Rules Available for Members (INC1894888) 
* Vardhman Jain                                 02/01/2023                   User Story: 3604417:Lightning - Consumer/CPD/HO Restriction Group/Genesys Soft Phone App
* Anil Pavithran                                02/03/2023                   US 3979946: T1PRJ0036776: PCC VOC Survey Transfer Identification 
* Harshada Kamble                               03/06/2023                   User Story 4299570: T1PRJ0170850: Lightning- Phonebook- Secure Payment UI Controls-Ebilling
* Sivaprakash Rajendran                         05/04/2023                   US 4579434 - T1PRJ0036776: Enterprise - Caller Type Population with Phonebook - Lightning (Genesys)
* Kiran Kumar Kotni                             05/10/2023                   User Story 4420291: T1PRJ0036776: Change 2 Field Names - Correct RxE implementation & Provider Survey Checkbox
* Mayur Pardeshi                                02/05/2024                   User Story 5668340: T1PRJ0036776: Alphabetize Phone Book
*******************************************************************************************************************************-->
*/
import { LightningElement, track, api, wire } from 'lwc';
import { CurrentPageReference } from 'lightning/navigation';
import getAllPhoneBookRules from '@salesforce/apexContinuation/PhoneBook_LC_HUM.getAllPhoneBookRules';
import getDefaultPH from '@salesforce/apexContinuation/PhoneBook_LC_HUM.getDefaultPH';
import callTransfer from '@salesforce/apexContinuation/PhoneBook_LC_HUM.callTransfer';
import insertIntegrationmapping from '@salesforce/apex/PhoneBook_LC_HUM.insertIntegrationmapping';
import consultBlindSaveCallData from '@salesforce/apexContinuation/PhoneBook_LC_HUM.consultBlindSaveCallData';
import getMemberPlanRecord from '@salesforce/apex/PhoneBook_LC_HUM.getMemberPlanRecord';
import createInteraction from '@salesforce/apex/PhoneBook_LC_HUM.createInteraction';
import updateUUIDInInteraction from '@salesforce/apex/PhoneBook_LC_HUM.updateUUIDInInteraction';
import getaccountWithRecordType from '@salesforce/apex/PhoneBook_LC_HUM.getaccountWithRecordType';
import PureCloudEvents from "@salesforce/messageChannel/purecloud__ClientEvent__c";
import { getRecord } from 'lightning/uiRecordApi';
import USER_ID from '@salesforce/user/Id';
import CallCenter_FIELD from '@salesforce/schema/User.CallCenterId';
import {
  publish, subscribe,
  unsubscribe,
  APPLICATION_SCOPE,
  MessageContext
} from "lightning/messageService";
import { phoneBookConstants } from './phoneBookConstantsHum';
import { getData, getRoutingXMLBody, getUSLocaleTime, getFilteredPhoneBookList } from './phoneBookHelper';
import pubSubHum from 'c/pubSubHum';
import fetchSwitchs from '@salesforce/apex/PhoneBook_LC_HUM.fetchSwitchs';
import getSecurePaymentsDetails from '@salesforce/apex/PhoneBook_LC_HUM.getSecurePaymentsDetails';
import PHONEBOOKIMAGE from '@salesforce/resourceUrl/Phonebook_Img_HUM';
import PHONEBOOKLMS from "@salesforce/messageChannel/phoneBookHum__c";
import getSurveyTransferNumber from '@salesforce/apex/PhoneBook_LC_HUM.getSurveyTransferNumber';
import fetchPermissionSetAccess from '@salesforce/apex/PhoneBook_LC_HUM.fetchPermissionSetAccess';
import surveyTransferSaveCallData from '@salesforce/apexContinuation/PhoneBook_LC_HUM.surveyTransferSaveCallData';
import updateInteractionAttribute from '@salesforce/apex/PhoneBook_LC_HUM.updateInteractionAttribute';
import getCallerTypeForPhoneBook from '@salesforce/apex/PhoneBook_LC_HUM.getCallerTypeForPhoneBook';
import PHONEBOOKCALLERTYPELMS from "@salesforce/messageChannel/phoneBookCallerTypeHum__c"; //Added for US#4579434 - Caller type population
import MYAHLMS from "@salesforce/messageChannel/myAssistantAtHumana_CMP_LMS__c"; // Added for US#4951430 - AI Chat Assistant Panel
import isMyAHSwitchedON from '@salesforce/apex/MyAssistantAtHumana_LC_HUM.getMyAHOnOffSwitchBooleanValue'; //MyAH Feature Switch - Added for US#4951430

export default class PhoneBookHum extends LightningElement {
  PhoneBookImg = PHONEBOOKIMAGE + '/Phonebook_Img_Hum/PhoneBook_grey.png';
  SecurePayImg = PHONEBOOKIMAGE + '/Phonebook_Img_Hum/SecurePay_grey.png';
  PhoneBookImgGreen = PHONEBOOKIMAGE + '/Phonebook_Img_Hum/PhoneBook_Green.png';
  SecurePayImgGreen = PHONEBOOKIMAGE + '/Phonebook_Img_Hum/SecurePay_Green.png';
  isPhoneBook = true;
  isSecurePay = false;
  @track callervalue;
  @track phonevalue;
  @track PHoptions;
  @track data;
  @track isButtonDisabled = true;
  @track isSurveyButtonVisible = false;
  @track isDialButtonDisabled = true;
  @track isDefaultselected = false;
  @track displayError = false;
  @track errorMsg;
  @track activeCall = false;
  @track activeOutboundCall = false;
  @track dialNo = '';
  @track showInfoPopup = false;
  @track infoData;
  @track activeInboundCall = false;
  @track isCallTransferClicked = false;
  @track searchForLabel = phoneBookConstants.QUEUE_NAME;
  @track loadSpinner = false;
  @track securepaymentvalue;
  @track securepaymentoptions;
  @track isSecureButtonDisabled = true;
  @track enableSecurePay = false;
  @track surveyTransferNo;
  @track isCRMSPCCPermissionSetUser;
  @wire(MessageContext)
  messageContext;
  transferSubscription = null;
  phoneBookCallerTypeSubscription = null;
  activeCallData;
  interactionId;
  phoneBookArray = [];
  allPhonebooklist;
  loginval;
  error;
  allQueues;
  Phonebookrules;
  allPhoneBookRules;
  genesysuser;
  allactiveCallData;
  uuid;
  ismanualTransfer;
  switches;
  secureFlowid;
  Switch_2484363;
  Switch_2649919;
  Switch_3653389;
  Switch_3791679;
  Switch_3979946;
  Switch_4579434; //Added for US#4579434 - Caller type population
  Switch_5668340;//Added for US5668340 -Alphbetize call reasons
  callerTypeValue;
  featureSwitchMyAH = false;
  
  //Added changes for DF-4988 fix
  Switch_2191493 =false;
  oBillingProfileInfo;
  @wire(CurrentPageReference)
  wiredPageRef(pageRef) {
    this.pageRef = pageRef;
  }
  @wire(getRecord, {
    recordId: USER_ID,
    fields: [CallCenter_FIELD]
  }) wireuser({
    error,
    data
  }) {
    if (error) {
    }
    else if (data) {
      this.genesysuser = data.fields.CallCenterId.value;
    }
  }

  @track columns = [
    {
      label: phoneBookConstants.QUEUE_NAME, fieldName: phoneBookConstants.QUEUE_FIELD, wrapText: true, hideDefaultActions: true
    },
    {
      type: 'button',
      label: 'Info',
      initialWidth: 80,
      typeAttributes:
      {
        variant: "Neutral",
        label: 'Info',
        name: 'Info',
        title: 'Info',
        disabled: false,
        value: ''
      }
    }];
  get calleroptions() {
    //Added changes for DF-4988 fix
    if(this.Switch_2191493 == true)
    {
      return [
        { label: phoneBookConstants.SELECT, value: phoneBookConstants.SELECT },
        { label: phoneBookConstants.MEMBER, value: phoneBookConstants.MEMBER },
        { label: phoneBookConstants.PROVIDER, value: phoneBookConstants.PROVIDER },
        { label: phoneBookConstants.GROUP, value: phoneBookConstants.GROUP },
        { label: phoneBookConstants.AGENT, value: phoneBookConstants.AGENT },
        { label: phoneBookConstants.UNKNOWN, value: phoneBookConstants.UNKNOWN }
      ];  
    }
    else
    {
      return [
        { label: phoneBookConstants.SELECT, value: phoneBookConstants.SELECT },
        { label: phoneBookConstants.MEMBER, value: phoneBookConstants.MEMBER },
        { label: phoneBookConstants.PROVIDER, value: phoneBookConstants.PROVIDER },
        { label: phoneBookConstants.GROUP, value: phoneBookConstants.GROUP },
        { label: phoneBookConstants.AGENT, value: phoneBookConstants.AGENT }
      ];
    }
  }

  handleSecurePaymentChange(event) {
    let selectedPayment = event.detail.value;
    if (selectedPayment != phoneBookConstants.SELECT && this.activeCall) {
      this.isSecureButtonDisabled = false;
    }
    else {
      this.isSecureButtonDisabled = true;
    }
  }

  handleSecureTransfer(event) {
    var obj = this.pageRef;
    var recId = obj.attributes.recordId;
    var paymentOption;
    var billingCardName;
    var billingCardfirstName;
    var billingCardLastName;

    this.template.querySelectorAll("lightning-combobox").forEach(element => {
      if (element.name == "securepayment") {
        paymentOption = element.value;
      }
    });
    this.template.querySelectorAll("lightning-input").forEach(element => {
      if (element.name == "BillingName") billingCardfirstName = element.value.trim();
    });
    this.template.querySelectorAll("lightning-input").forEach(element => {
      if (element.name == "BillingLastName") billingCardLastName = element.value.trim();
    });
    billingCardName = billingCardfirstName.concat(' ', billingCardLastName);

    if (obj.type == 'standard__recordPage' && obj.attributes.objectApiName == 'MemberPlan'  && paymentOption!='eBilling') {
      var payload_data;
      var logObject = { CardBillingName: billingCardName, SecureTransferType: paymentOption };
      this.dispatchEvent(new CustomEvent('securepayevt', { detail: logObject }));
      if (billingCardfirstName && billingCardfirstName != '' && billingCardLastName && billingCardLastName != '') {

        getMemberPlanRecord({ recid: recId })
          .then(response => {
            if (response != null) {
              var memberIdBase = response.Member_Id_Base__c ? response.Member_Id_Base__c : '';
              var dependentCode = response.Member_Dependent_Code__c ? response.Member_Dependent_Code__c : '';
              var enterpriseId = response.Member.Enterprise_ID__c ? response.Member.Enterprise_ID__c : '';
              var platformCd = response.Policy_Platform__c ? response.Policy_Platform__c : '';
              var subscriberIdBase = response.Member_Id_Base__c ? response.Member_Id_Base__c : '';

              switch (paymentOption) {
                case 'Pharmacy':
                  if (memberIdBase && memberIdBase != '' && dependentCode && dependentCode != '' && enterpriseId && enterpriseId != '') {
                    payload_data = 'TargetSystem:EPOST|MemberId:' + memberIdBase + '|DependentCode:' + dependentCode + '|MasterId:' + enterpriseId + '|BillingCardFirstName:' + billingCardfirstName + '|BillingCardLastName:' + billingCardLastName + '|UUID:' + this.uuid;
                    this.callSecureFlow(payload_data);
                  }
                  else {
                    alert('If you are trying to setup the payment for CBIS/Pharmacy, open and focus the Policy page for which the payment needs to be setup.');
                  }
                  break;
                case 'CBIS':
                  if (memberIdBase && memberIdBase != '' && platformCd && platformCd != '' && subscriberIdBase && subscriberIdBase != '') {
                    payload_data = 'TargetSystem:CBIS|ActionType:A|MemberIdBase:' + memberIdBase + '|SubscriberIdBase:' + subscriberIdBase + '|BillingCardFirstName:' + billingCardfirstName + '|BillingCardLastName:' + billingCardLastName + '|PlatformCd:' + platformCd + '|UUID:' + this.uuid;
                    this.callSecureFlow(payload_data);
                  }
                  else {
                    alert('If you are trying to setup the payment for CBIS/Pharmacy, open and focus the Policy page for which the payment needs to be setup.');
                  }
                  break;
              }
            }
          })
          .catch(error => {
          });
      }
      else {
        alert('Please enter "Card Holder Name" in order to invoke secure Payment flow. First Name and Last Name are required.');
      }
    }
    else if(paymentOption!='eBilling'){
      alert('If you are trying to setup the payment for CBIS/Pharmacy, open and focus the Policy page for which the payment needs to be setup.');
    }
    else if (obj.type == 'standard__navItemPage' && obj.attributes.apiName == 'Billing_Summary' &&  paymentOption=='eBilling') 
    {
        var payload_data;
        var logObject = { CardBillingName: billingCardName, SecureTransferType: paymentOption };
        this.dispatchEvent(new CustomEvent('securepayevt', { detail: logObject }));

        if (!this.oBillingProfileInfo || !this.oBillingProfileInfo.MemberIdBase || !this.oBillingProfileInfo.ProfileSequenceNumber ) {
          alert("Please select a row and click on secure pay button on the ebilling page for which payment needs to be configured.");
        }
        else if (!billingCardfirstName || !billingCardLastName) 
        {
          alert('Please enter "Card Holder Name" in order to invoke secure Payment flow. First Name and Last Name are required.');
        }
        else {
            if(this.oBillingProfileInfo != null){
               payload_data ="TargetSystem:" + this.oBillingProfileInfo.TargetSystem + "|ActionType:" + this.oBillingProfileInfo.ActionType + "|PartyKey:" + this.oBillingProfileInfo.PartyKey +
               "|ProfileSequenceNumber:" + this.oBillingProfileInfo.ProfileSequenceNumber + "|MemberIdBase:" + this.oBillingProfileInfo.MemberIdBase +
               "|SubscriberIdBase:" + this.oBillingProfileInfo.SubscriberIDBase + "|BillingCardFirstName:" + billingCardfirstName + "|BillingCardLastName:" + billingCardLastName +
               "|PlatformCd:" + this.oBillingProfileInfo.PlatformCd + "|UUID:" + this.uuid;
 
             this.callSecureFlow(payload_data);
           }
        }
    }
    else if(paymentOption=='eBilling'){
      alert('If you are trying to setup the payment for ebilling, open ebilling page and click on Secure pay button for which payment needs to be setup.')
    }
  }

  /*
   * Params - {payload_data - userData details pipe seperated}
   * Description - this method prepares the payload to be sent on click of Secure transfer button.
   */
  callSecureFlow(payload_data) {
    let idval = this.interactionId;
    var payload = '{ "type": "PureCloud.Interaction.updateState","data": {"action": "secureSession", "id": "' + idval + '", "secureSessionContext": {"flowId": "' + this.secureFlowid + '", "userData": "' + payload_data + '"}}}'
    publish(this.messageContext, PureCloudEvents, JSON.parse(payload));
  }

  getRowAction(event) {
    this.showInfoPopup = true;
    const selectedRows = event.detail.row;
    this.infoData = selectedRows;
  }

  closeModal() {
    this.showInfoPopup = false;
  }

  @api
  handleLogin(loginval) {
    this.loginval = loginval;
    if (!loginval) {
      this.resetPhoneBook();
    }
    let sPermissionSetName = phoneBookConstants.CRMS_685_PCC_Access_HUM;
    fetchPermissionSetAccess({ sPermissionSetName:sPermissionSetName})
    .then(result => {
      if(result != null)
        this.isCRMSPCCPermissionSetUser = result;
    })
    .catch(error => {
      this.error = error;
    });
  }

  @api
  handleincomingcall(callervalue, searchModal, interactionId, uuid) {
    this.interactionId = interactionId;
    this.uuid = uuid;
    let pharray = [];
    pharray = this.phoneBookArray;
    pharray.push({ label: phoneBookConstants.TRANSFER_PHONE_BOOK, value: phoneBookConstants.TRANSFER_PHONE_BOOK });
    this.PHoptions = [...pharray];
    this.callervalue = callervalue;
    this.template.querySelectorAll("lightning-combobox").forEach(element => {
      if (element.name === "phonebook") {
        element.value = phoneBookConstants.TRANSFER_PHONE_BOOK;
      }
    });
    //Added Fix for DF-4989
    if(this.Switch_2649919 == true)
    {
      this.displayPhoneBook();
    }
    this.activeCall = true;
    this.enableSecurePay = true;
    this.activeOutboundCall = false;
    this.activeInboundCall = true;
    this.searchForLabel = phoneBookConstants.CALL_REASON;
    this.searchByCallReason();
    this.securepaymentvalue = phoneBookConstants.SELECT;
    this.isSecureButtonDisabled = true;
    this.closeModal();
    this.getDefaultPhoneBookRules(callervalue, searchModal);
    this.template.querySelectorAll("lightning-input").forEach(element => {
      if (element.name === "search-for" || element.name === "DialNo") {
        element.value = '';
      }
    });
    this.template.querySelector('lightning-datatable').maxRowSelection = 0;
    this.template.querySelector('lightning-datatable').maxRowSelection = 1;
    this.isButtonDisabled = true;
    this.isDialButtonDisabled = true;
    if(this.Switch_3653389 && this.Switch_3653389 == true)
    {
          this.isSurveyButtonVisible =true;
          getSurveyTransferNumber()
          .then(result => {
            if(result != null)
              this.surveyTransferNo = result;
          })
          .catch(error => {
            this.error = error;
          });
    }
  }

  @api
  handleoutboundcall(interactionId, refreshVal, uuid) {
    this.interactionId = interactionId;
    this.activeCall = true;
    this.activeOutboundCall = true;
    this.activeInboundCall = false;
    var obj = this.pageRef;
    var recId = obj.attributes.recordId;
    var objName = obj.attributes.objectApiName;
    var UUID = this.interactionId;
    this.uuid = uuid;
    var callOrigin = phoneBookConstants.OUTBOUND_CALL;
    this.template.querySelectorAll("lightning-combobox").forEach(element => {
      if (element.name === "phonebook") {
        element.value = phoneBookConstants.SELECT;
      }
    });
    if (!refreshVal && this.switches['Switch_2230077'] != false) {
      if (obj.type == 'standard__recordPage' && (objName == 'MemberPlan' || objName == 'Account')) {
        getaccountWithRecordType({ recId: recId, obj: objName })
          .then(response => {
            if (response != null) {
              this.createOutboundInteraction(UUID, String(Object.keys(response)), this.interactionId, callOrigin, String(Object.values(response)));
            }
          }).catch(error => {
            this.error = error;
          });
      }
    }
  }
  createOutboundInteraction(UUID, intwith, genIntId, callOrigin, accountType) {
    switch (accountType) {
      case 'Agent/Broker': accountType = 'Agent'; break;
      case 'Unknown Member': accountType = 'Unknown-Member'; break;
      case 'Unknown Group': accountType = 'Unknown-Group'; break;
      case 'Unknown Provider': accountType = 'Unknown-Provider'; break;
      case 'Unknown Agent/Broker': accountType = 'Unknown-Agent'; break;
    }
    createInteraction({ uuid: UUID, interactingWith: intwith, genInteractionId: genIntId, interactingWithType: accountType, origin: callOrigin })
      .then(response => {
        updateUUIDInInteraction({ interactionId: response })
          .then(uuidresponse => {
            if (uuidresponse != '') {
              var payload = '{"type": "PureCloud.Interaction.addCustomAttributes", "data": {"id": "' + genIntId + '","attributes": {"uuiData": "' + uuidresponse + '"}}}';
              publish(this.messageContext, PureCloudEvents, JSON.parse(payload));
              if(this.featureSwitchMyAH) {
                publish(this.messageContext, MYAHLMS, {msgType: "Phonebook", msginteractingAboutId: intwith}); // Added for US#4951430
              }
              this.uuid = uuidresponse;
            }
          }).catch(error => {
          });
      })
      .catch(error => {
        this.error = error;
      });
  }

  @api
  handleDisconnectCall() {
    if (this.activeInboundCall) {
      this.phoneBookArray.pop();
      this.PHoptions = [...this.phoneBookArray];
    }
    this.resetPhoneBook();
    this.closeModal();
    this.interactionId = '';
    this.uuid = '';
    this.oBillingProfileInfo = '';
  }

  getSelectedName(event) {
    this.ismanualTransfer = phoneBookConstants.ISMANUAL_N;
    const selectedRows = event.detail.selectedRows;
    if (selectedRows.length > 0) {
      let selectedContact;
      for (let i = 0; i < selectedRows.length; i++) {
        selectedContact = selectedRows[i].Contact;
      }
      if (this.activeCall === false) {
        this.dialNo = selectedContact;
        this.isButtonDisabled = true;
        this.isSurveyButtonVisible =false;
      }
      else {
        this.template.querySelectorAll("lightning-input").forEach(element => {
          if (element.name === "DialNo") {
            element.value = selectedContact;
          }
        });
        this.isButtonDisabled = false;
        if(this.Switch_3653389 && this.Switch_3653389 == true)
        {
            this.isSurveyButtonVisible =true;
        }
      }
    }
  }

  handlesearch(event) {
    if (event.target.value != '') {
      var selectedph;
      this.template.querySelectorAll("lightning-combobox").forEach(element => {
        if (element.name === "phonebook") {
          selectedph = element.value;
        }
      });
      let activedata = [];
      activedata = this.activeCallData;
      let allQueues = [];
      allQueues = this.allQueues;
      let newallQueues = [];
      if (selectedph === phoneBookConstants.TRANSFER_PHONE_BOOK) {
        this.searchByCallReason();
        for (let key in activedata) {
          if ((((activedata[key]).hasOwnProperty(phoneBookConstants.CATEGORY_CD) && ((activedata[key].CategoryCd).toLowerCase()).includes((event.target.value).toLowerCase())))
            && newallQueues.length < 100) {
            newallQueues.push(activedata[key]);
          }
        }
      }
      else if (selectedph != undefined && selectedph != phoneBookConstants.SELECT) {
        this.searchByQueueName();
        let selectedEnterprisePhRules = this.Phonebookrules[selectedph].TransferNumber.TransferNumberList;
        for (let key in selectedEnterprisePhRules) {
          if (((selectedEnterprisePhRules[key].QueueName).toLowerCase()).includes((event.target.value).toLowerCase())
            && newallQueues.length < 100) {
            newallQueues.push(selectedEnterprisePhRules[key]);
          }
        }
      }
      else {
        this.searchByQueueName();
        for (let key in allQueues) {
          if (((allQueues[key].QueueName).toLowerCase()).includes((event.target.value).toLowerCase())
            && newallQueues.length < 100) {
            newallQueues.push(allQueues[key]);
          }
        }
      }
      this.allPhoneBookRules = newallQueues;
      this.data = getData(this.allPhoneBookRules, selectedph, this.Switch_3791679,this.Switch_5668340);
    }
    else {
      var inp = this.template.querySelectorAll("lightning-combobox");
      let Phonebookrules = this.Phonebookrules;
      var otherPhoneBookRules;
      var ref = this;
      var selectedPhonebook;
      inp.forEach(function (element) {
        if (element.name === "phonebook") {
          selectedPhonebook = element.value;
          if (element.value === phoneBookConstants.TRANSFER_PHONE_BOOK || element.value == undefined || element.value == '') {
            otherPhoneBookRules = ref.activeCallData;
            if (element.value === phoneBookConstants.TRANSFER_PHONE_BOOK) {
              ref.searchByCallReason();
            }
          }
          else {
            if (Phonebookrules[element.value] && Phonebookrules[element.value] != null) {
              otherPhoneBookRules = Phonebookrules[element.value].TransferNumber.TransferNumberList;
              ref.searchByQueueName();
            }
          }
        }
      });
      this.allPhoneBookRules = otherPhoneBookRules;
      this.data = getData(this.allPhoneBookRules, selectedPhonebook,this.Switch_3791679,this.Switch_5668340);
    }
    this.template.querySelector('lightning-datatable').maxRowSelection = 0;
    this.template.querySelector('lightning-datatable').maxRowSelection = 1;
  }

  handleCallerChange(event) {
    let selectedcaller = event.detail.value;
    if (this.allactiveCallData) {
      let phBookRulesList = this.allactiveCallData;
      this.filterCallerType(phBookRulesList, selectedcaller);
      this.template.querySelector('lightning-datatable').maxRowSelection = 0;
      this.template.querySelector('lightning-datatable').maxRowSelection = 1;
      this.template.querySelectorAll("lightning-input").forEach(element => {
        if (element.name === "search-for") {
          element.value = '';
        }
      });
    }
  }

  handleChange(event) {
    if (event.detail.value != phoneBookConstants.TRANSFER_PHONE_BOOK) {
      this.searchForLabel = phoneBookConstants.QUEUE_NAME;
      this.searchByQueueName();
      this.isDefaultselected = false;
      if (this.Phonebookrules[event.detail.value] && this.Phonebookrules[event.detail.value] != null) {
        if (this.Phonebookrules[event.detail.value].TransferNumber && this.Phonebookrules[event.detail.value].TransferNumber != null) {
          this.allPhoneBookRules = this.Phonebookrules[event.detail.value].TransferNumber.TransferNumberList;
        }
      }
      else {
        this.allPhoneBookRules = '';
      }
    }
    else {
      this.searchForLabel = phoneBookConstants.CALL_REASON;
      this.searchByCallReason();
      this.allPhoneBookRules = this.activeCallData;
    }
    this.data = getData(this.allPhoneBookRules, event.detail.value,this.Switch_3791679,this.Switch_5668340);
    this.template.querySelector('lightning-datatable').maxRowSelection = 0;
    this.template.querySelector('lightning-datatable').maxRowSelection = 1;
    this.template.querySelectorAll("lightning-input").forEach(element => {
      if (element.name === "search-for") {
        element.value = '';
      }
    });
  }

  handledialChange(event) {
    this.ismanualTransfer = phoneBookConstants.ISMANUAL_Y;
    var numEntered = event.detail.value
    var regPat = /^(\d{7}|\d{10})$/;
    if (this.activeCall) {
      if (event.target.value != '') {
        if (!numEntered.match(regPat)) {
          this.isButtonDisabled = true;
        }
        else {
          this.isButtonDisabled = false;
        }
      }
      else {
        this.isButtonDisabled = true;
      }
    }
    else {
      this.isButtonDisabled = true;
    }
  }

  handleConsultTransfer(event) {
    this.dispatchEvent(new CustomEvent('consultblindclicked', {}));
    var transferNo;
    this.template.querySelectorAll("lightning-input").forEach(element => {
      if (element.name === "DialNo") {
        transferNo = element.value;
      }
    });
    var now = getUSLocaleTime(new Date());
    let transferattributes = {
      ISMANUAL_TRANSFER: String(this.ismanualTransfer),
      TRANSFER_STIM: now,
      TRANSFER_NUMBER: transferNo,
      TRANSFER_TYPE: phoneBookConstants.WARM,
      UUID: this.uuid,
      isCallTransferClicked: this.isCallTransferClicked
    }
    if (this.switches['Switch_2357061'] != false) {
      this.loadSpinner = true;
      consultBlindSaveCallData({ transferattributes: transferattributes })
        .then(response => {
          this.handleTransfer(phoneBookConstants.CONSULT, transferNo);
          this.loadSpinner = false;
        })
        .catch(error => {
          this.error = error;
        });
    }
    else {
      this.handleTransfer(phoneBookConstants.CONSULT, transferNo);
    }
  }

  handleBlindTransfer(event) {
    this.dispatchEvent(new CustomEvent('consultblindclicked', {}));
    var transferNo;
    this.template.querySelectorAll("lightning-input").forEach(element => {
      if (element.name === "DialNo") {
        transferNo = element.value;
      }
    });
    var now = getUSLocaleTime(new Date());
    let transferattributes = {
      ISMANUAL_TRANSFER: String(this.ismanualTransfer),
      TRANSFER_STIM: now,
      TRANSFER_NUMBER: transferNo,
      TRANSFER_TYPE: phoneBookConstants.COLD,
      UUID: this.uuid,
      isCallTransferClicked: this.isCallTransferClicked
    }
    if (this.switches['Switch_2357061'] != false) {
      this.loadSpinner = true;
      consultBlindSaveCallData({ transferattributes: transferattributes })
        .then(response => {
          this.handleTransfer(phoneBookConstants.BLIND, transferNo);
          this.loadSpinner = false;
        })
        .catch(error => {
          this.error = error;
        });
    }
    else {
      this.handleTransfer(phoneBookConstants.BLIND, transferNo);
    }
  }

  /*
   * Params - {action-consult/blind , transfer number}
   * Description - this method prepares the payload to be sent on click of consult/blind transfer button.
   */
  handleTransfer(action, transferNo) {
    const idval = this.interactionId;
    var payload = '{ "type": "PureCloud.Interaction.updateState", "data": { "action": "' + action + '", "id": "' + idval + '", "participantContext": { "transferTarget": "' + transferNo + '", "transferTargetType": "address" } } }';
    publish(this.messageContext, PureCloudEvents, JSON.parse(payload));
    this.dispatchEvent(new CustomEvent('opencti'));
  }

  /*
   * Params - {caller type of the calling customer, searchModal- GetCallData responnse}
   * Description - this method is used to fetch the default Phonebook rules on incoming call.
   */
  getDefaultPhoneBookRules(callertype, searchModal) {
    let xmlRoutingRequestBody = getRoutingXMLBody(searchModal);
    getDefaultPH({ body: xmlRoutingRequestBody })
      .then(result => {
        if (!result.includes(phoneBookConstants.CONTACT_ASSISTANCE)) {
          let jsonres = JSON.parse(result);
          let phBookRulesList = jsonres.RouteSoftphoneXmlResponse.CallTransferRec.CallTransferRecList;
          this.allactiveCallData = phBookRulesList;
          this.filterCallerType(phBookRulesList, callertype);
        }
        else {
          this.displayError = true;
          this.errorMsg = result;
          this.data = '';
        }
      })
      .catch(error => {
        this.error = error;
      });
  }

  /*
   * Params - {}
   * Description - this method is used to reset all the variables at disconnect/logout.
   */
  resetPhoneBook() {
    this.activeCall = false;
    this.enableSecurePay = false;
    this.isButtonDisabled = true;
    this.isSurveyButtonVisible =false;
    this.isDialButtonDisabled = true;
    this.activeOutboundCall = false;
    this.activeInboundCall = false;
    this.callervalue = '';
    this.data = '';
    this.dialNo = '';
    this.activeCallData = '';
    this.allactiveCallData = '';
    this.displayError = false;
    this.searchForLabel = phoneBookConstants.QUEUE_NAME;
    this.isSecureButtonDisabled = true;
    var select = phoneBookConstants.SELECT;
    this.securepaymentvalue = [...select];
    this.template.querySelectorAll("lightning-input").forEach(element => {
      if (element.name == "search-for" || element.name == "DialNo" || element.name == "BillingName" || element.name == "BillingLastName") {
        element.value = '';
      }
    });
    this.template.querySelectorAll("lightning-combobox").forEach(element => {
      if (element.name === "phonebook") {
        element.value = phoneBookConstants.SELECT;
      }
    });
    this.searchByQueueName();
    this.oBillingProfileInfo = '';
  }

  /*
   * Params - {}
   * Description - this method is called from parent component to reset the secure payment things.
   */
  @api
  resetSecurePaymentParams() {
    this.template.querySelectorAll("lightning-input").forEach(element => {
      if (element.name == "BillingName" || element.name == "BillingLastName") {
        element.value = '';
      }
    });
    var select = phoneBookConstants.SELECT;
    this.isSecureButtonDisabled = true;
    this.securepaymentvalue = [...select];
    this.oBillingProfileInfo = '';
  }

  /*
   * Params - {List of phonebookrules which needs to be filtered , callertype}
   * Description - this method filters the phonebookrules list based uptio the caller type value selected.
   */
  filterCallerType(phBookRulesList, callertype) {
    this.activeCallData = getFilteredPhoneBookList(phBookRulesList, callertype);
    var selectedphonebook;
    this.template.querySelectorAll("lightning-combobox").forEach(element => {
      if (element.name === "phonebook") {
        selectedphonebook = element.value;
      }
    });
    if (selectedphonebook === phoneBookConstants.TRANSFER_PHONE_BOOK) {
      this.data = getData(this.activeCallData, selectedphonebook,this.Switch_3791679,this.Switch_5668340);
    }
  }

  /*
   * Params - {}
   * Description - this method changes the table header and field values to Call Reason.
   */
  searchByCallReason() {
    this.columns.shift();
    this.columns.unshift({
      label: phoneBookConstants.CALL_REASON, fieldName: phoneBookConstants.REASON_FIELD, wrapText: true, hideDefaultActions: true
    });
    this.columns = [...this.columns];
  }

  /*
   * Params - {}
   * Description - this method changes the table header and field values to Queue Name.
   */
  searchByQueueName() {
    this.columns.shift();
    this.columns.unshift({
      label: phoneBookConstants.QUEUE_NAME, fieldName: phoneBookConstants.QUEUE_FIELD, wrapText: true, hideDefaultActions: true
    });
    this.columns = [...this.columns];
  }

  /*
   * Params - {MemberPlanID,Account Id and UUID in object form}
   * Description - this method is called on click of call transfer button from Policy page.
   */
  handleCallTransfer(result) {
    if (result.messageToSend.callTransfer) {
		this.displayError = false;
      if (this.genesysuser) {
        this.closeModal();
        this.dispatchEvent(new CustomEvent('transfercall'));
        if (this.activeCall) {
          //Added Fix for DF-4989
          if(this.Switch_2649919 == true)
          {
            this.displayPhoneBook();
          }
          this.searchForLabel = phoneBookConstants.CALL_REASON;
          this.searchByCallReason();
          if (this.PHoptions[this.PHoptions.length - 1].value != phoneBookConstants.TRANSFER_PHONE_BOOK) {
            let pharray = [];
            pharray = this.phoneBookArray;
            pharray.push({ label: phoneBookConstants.TRANSFER_PHONE_BOOK, value: phoneBookConstants.TRANSFER_PHONE_BOOK });
            this.PHoptions = [...pharray];
          }
          this.template.querySelectorAll("lightning-combobox").forEach(element => {
            if (element.name === "phonebook") {
              element.value = phoneBookConstants.TRANSFER_PHONE_BOOK;
            }
          });
          let callTransferData = {
            accId: result.messageToSend.callTransfer.messageToSend[0].rowData.Member ? result.messageToSend.callTransfer.messageToSend[0].rowData.Member.Id : '',
            MemberPlanId: result.messageToSend.callTransfer.messageToSend[0].rowData ? result.messageToSend.callTransfer.messageToSend[0].rowData.Id : '',
            interactionId: '',
            uuid: this.uuid
          };
		  if(result?.messageToSend?.callTransfer?.messageToSend[0]?.rowData?.isLocked == true){
            this.displayError = true;
            this.errorMsg = phoneBookConstants.Protected_Message;
          }
          this.data = '';
          this.template.querySelectorAll("lightning-input").forEach(element => {
            if (element.name === "search-for") {
              element.value = '';
            }
          });
          this.template.querySelectorAll("lightning-input").forEach(element => {
            if (element.name === "DialNo") {
              element.value = '';
            }
          });
          this.template.querySelector('lightning-datatable').maxRowSelection = 0;
          this.template.querySelector('lightning-datatable').maxRowSelection = 1;
          this.isButtonDisabled = true;
          if(this.Switch_3653389 && this.Switch_3653389 == true)
          {
              this.isSurveyButtonVisible =true;
          }
          callTransfer({ callTransferData: callTransferData })
            .then(result => {
              let callertype = '';
              this.template.querySelectorAll("lightning-combobox").forEach(element => {
                if (element.name === "callerType") {
                  callertype = element.value;
                }
              });
              let jsonres = JSON.parse(result);
              let phBookRulesList = [];
              phBookRulesList = jsonres.RouteSoftphoneXmlResponse.CallTransferRec.CallTransferRecList;
              this.allactiveCallData = phBookRulesList;
              this.filterCallerType(phBookRulesList, callertype);
              this.isCallTransferClicked = true;
              insertIntegrationmapping({ callTransferData: callTransferData })
                .then(result => {
                })
                .catch(error => {
                  this.error = error;
                });
            })
            .catch(error => {
              this.error = error;
            });
        }
        else {
          this.resetPhoneBook();
        }
      }
    }
  }

  /*
   * Params - {}
   * Description - this method is called on load of page and will fetch enterprise phonebook rules.
   */
  async fetchAllPhoneBookRules() {
    try {
      const result = await getAllPhoneBookRules();
      if (result != null) {
        let phoneBookOptions = [];
        let jsonresult = JSON.parse(result);
        let obj = {};
        let allQueues = [];
        let phBookRulesList = jsonresult.GetAllPhoneBookRulesResponse.AllPhoneBookRulesResult.AllPhoneBookRulesResultList;
        phoneBookOptions.push({ label: phoneBookConstants.SELECT, value: phoneBookConstants.SELECT });
        phBookRulesList.forEach(function (eachRule) {
          phoneBookOptions.push({ label: eachRule.Key, value: eachRule.Key });
          obj[eachRule.Key] = eachRule;
          let transferNumberList = eachRule.TransferNumber.TransferNumberList;
          transferNumberList.forEach(function (eachTransferNumber) {
            allQueues.push(eachTransferNumber);
          });
        });
        this.phoneBookArray = phoneBookOptions;
        this.PHoptions = phoneBookOptions;
        this.allQueues = allQueues;
        this.Phonebookrules = obj;
      }
    }
    catch (error) {
      this.error = error;
    }
  }

  /*
   * Params - {}
   * Description - this method is called on load of page and will fetch all the switches.
   */
  fetchallSwitchs() {
    fetchSwitchs()
      .then(response => {
        this.switches = response;
        if (this.switches['Switch_2484363'] != false) {
          this.Switch_2484363 = true;
        }
        if (this.switches['Switch_2649919'] != false) {
          this.Switch_2649919 = true;
          this.displayPhoneBook();
        }
        //Added changes for DF-4988 fix
        if (this.switches['Switch_2191493'] != false) {
          this.Switch_2191493 = true;
        }
        if (this.switches['Switch_3653389'] != false) {
          this.Switch_3653389 = true;
        }
        if (this.switches['Switch_3791679'] != false) {
          this.Switch_3791679 = true;
        }
        if (this.switches['Switch_3979946'] != false) {
          this.Switch_3979946 = true;
        }
        //Added for US#4579434 - Caller type population
        if(this.switches['Switch_4579434'] != false) {
          this.Switch_4579434 = true;
        }
        if(this.switches['Switch_5668340'] != false) {
          this.Switch_5668340 = true;
        }
        
      })
      .catch(error => {
      });
  }

  /*
   * Params - {}
   * Description - this method is called on load of page and will fetch dropdown values for secure payment types and secureFlowId.
   */
  fetchSecurePaymentsDetails() {
    getSecurePaymentsDetails()
      .then(result => {
        this.secureFlowid = Object.values(result)[0];
        let securepaymentarr = [];
        securepaymentarr.push({ label: phoneBookConstants.SELECT, value: phoneBookConstants.SELECT });
        for (let key in Object.keys(result)) {
          securepaymentarr.push({ label: Object.keys(result)[key], value: Object.keys(result)[key] })
        }
        this.securepaymentoptions = securepaymentarr;
      })
      .catch(error => {
        this.error = error;
      });
  }

  displayPhoneBook() {
    this.isPhoneBook = true;
    this.isSecurePay = false;
    this.template.querySelector(".phoneBookTab").style = "display:block;width:100%";
    this.template.querySelector(".SecurePayTab").style = "display:none";
  }
  displaySecurePay() {
    this.isPhoneBook = false;
    this.isSecurePay = true;
    this.template.querySelector(".phoneBookTab").style = "display:none";
    this.template.querySelector(".SecurePayTab").style = "display:block;width:100%";
  }

  subscribeToMessageChannel() {
    if (!this.transferSubscription) {
      this.transferSubscription =
        subscribe(
          this.messageContext,
          PHONEBOOKLMS,
          message => {
            if(message?.messageToSend?.callTransfer?.messageToSend[0]?.rowData?.SecurePay != null){ 
              this.handleSecurePay(message);
            }
            else{
              this.handleCallTransfer(message);
            }
          },
          { scope: APPLICATION_SCOPE }
        );
    }
    
    //Added for US#4579434 - Caller type population
    if (!this.phoneBookCallerTypeSubscription) {
      this.phoneBookCallerTypeSubscription =
        subscribe(
          this.messageContext,
          PHONEBOOKCALLERTYPELMS,
          message => {
             this.handleInteractingWithType(message);
          }
        );
    }
    
    
  }
  unsubscribeToMessageChannel() {
    unsubscribe(this.transferSubscription);
    this.transferSubscription = null;
    //Added for US#4579434 - Caller type population
    unsubscribe(this.phoneBookCallerTypeSubscription);
    this.phoneBookCallerTypeSubscription = null;
  }
  connectedCallback() {
    this.fetchAllPhoneBookRules();
    this.fetchallSwitchs();
    this.fetchSecurePaymentsDetails();
    this.subscribeToMessageChannel();
    isMyAHSwitchedON()
    .then(switchState=>{
      if(switchState === true){
        this.featureSwitchMyAH = true;
      }
    })
    .catch(error=>{})
  }
  disconnectedCallback() {
    this.unsubscribeToMessageChannel();
  }

  renderedCallback() {
    if (this.isRendered) {
      return;
    }
    this.isRendered = true;
    let style = document.createElement('style');
    style.innerText = '.customphtable th .slds-cell-fixed{background-color: rgb(0, 112, 210); color:white;pointer-events: none;}';
    this.template.querySelector(".customphtable").appendChild(style);
    let stylerow = document.createElement('style');
    stylerow.innerText = '.customphtable tr th .slds-hyphenate{width : 100%;}';
    this.template.querySelector(".customphtable").appendChild(stylerow);
  }

  handleSurveyTransfer(event) {
    
    this.ismanualTransfer = phoneBookConstants.ISMANUAL_Y;
      this.dispatchEvent(new CustomEvent('SurveyTransferClicked', {}));

      var now = getUSLocaleTime(new Date());
      let transferattributes = {
        ISMANUAL_TRANSFER: String(this.ismanualTransfer),
        TRANSFER_NUMBER: this.surveyTransferNo,
        TRANSFER_TYPE: phoneBookConstants.COLD,
        UUID: this.uuid,
        WATSON_TRANSFER_TS: now,
        isCallTransferClicked: this.isCallTransferClicked
      }
      if(this.uuid != null)
      {
        updateInteractionAttribute({ uuid: this.uuid })
            .then(result => {
                console.log('UpdateInteractionAttributeSuccess'+ result);
            }).catch(error => {
              this.error = error;
            });
      }

      if (this.switches['Switch_2357061'] != false) {
        this.loadSpinner = true;
        surveyTransferSaveCallData({ transferattributes: transferattributes })
          .then(response => {
            this.handleTransfer(phoneBookConstants.BLIND, this.surveyTransferNo);
            this.loadSpinner = false;
          })
          .catch(error => {
            this.error = error;
          });
      }
      else {
        this.handleTransfer(phoneBookConstants.BLIND, this.surveyTransferNo);
      }
    }

    
   /*
   * Params - {Secure pay data in object form}
   * Description - this method is called on click of Secure transfer button from Member Billing Profile page.
   */
  handleSecurePay(result) {
        if (this.genesysuser) {
          this.closeModal();
          this.dispatchEvent(new CustomEvent('transfercall'));
            this.oBillingProfileInfo = result.messageToSend.callTransfer.messageToSend[0].rowData.SecurePay;
                this.displaySecurePay();
    }
  }

  //Added for US#4579434 - Caller type population
  handleInteractingWithType(message){
    if(this.Switch_4579434 == true){
        if(message.interactingWithTypeValue !='' && message.interactingWithTypeValue !=null){
            getCallerTypeForPhoneBook({ interactingWithType: message.interactingWithTypeValue })
            .then(result => {
              this.callerTypeValue = result;
            }).catch(error => {
              this.error = error;
            });
        }else{
          this.callerTypeValue = 'Unknown';
        }

        //Force Update the CallerType dropdown value
        setTimeout(()=>{
          if(this.callerTypeValue !=''){
                this.template.querySelectorAll("lightning-combobox").forEach(element => {
                  if (element.name === "callerType") {
                    element.value = this.callerTypeValue;
                  }
                });
            }
        },200);
    }
  }

}