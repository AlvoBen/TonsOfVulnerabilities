/******************************************************************************************************************************
LWC Name : pureCloudEventsHum.js
Function    : This LWC component contains the phonebook and search-Integration components

Modification Log: 
Developer Name           Code Review              Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Arpit Jain/Navajit Sarkar                     02/09/2021                  Original Version
* Nikhil Malhotra                               02/22/2021                  Implementing softphone logging.               
* Nikhil Malhotra                               03/05/2021                  Adding additional info on CallLog Information field in PureCloudCallLog object.
* Arpit Jain/Navajit Sarkar                     03/09/2021                  PhoneBook Related Changes
* Nikhil Malhotra                               05/26/2021                  Bringing call connected functionality from 'connect' category of Interaction event to 'change' category.
* Arpit Jain/Navajit Sarkar                     07/12/2021                  US-2357061 : IVR Call End or Transfer, US-2230090
* Arpit jain                                    08/02/2021                  PubSub Change : pubSubComponent replaced by pubSubHum
* Arpit Jain                                    10/01/2022                  PubSub replaced by Lightning Message Channel
* Harshada Kamble                               01/05/2023                  US 3944531-T1PRJ0036776: UCID not mapping to UUID field in Salesforce CRM (INC2009416)
* Harshada Kamble                               02/03/2023                  US 3980680: T1PRJ0036776: RxE - Save Campaign name and keycode to CRM interactions
* Santhi Mandava                                02/06/2013                  User Story 4082261,4084543: Display interaction information on search page and account detail page.
* Harshada Kamble/Anil Pavithran                03/05/2023                  User Story 4144165: T1PRJ0307696: CRM Service: Format is incorrect/Unable to authenticate - DOB Format
* Kiran Kumar Kotni                             05/10/2023                  User Story 4420291: T1PRJ0036776: Change 2 Field Names - Correct RxE implementation & Provider Survey Checkbox
* Harshada Kamble                                06/22/2023                  User Story 4707880: T1PRJ0036776: Lightning - Multi-member transfer call handling - Provider (Genesys)
*******************************************************************************************************************************-->
*/
import { LightningElement, wire } from "lwc";
import {
    publish,
    subscribe,
    unsubscribe,
    APPLICATION_SCOPE,
    MessageContext
} from "lightning/messageService";

import { NavigationMixin, CurrentPageReference } from 'lightning/navigation';
import pubSubHum from 'c/pubSubHum';
import startRequest from '@salesforce/apexContinuation/PhoneBook_LC_HUM.getCallData';
import createRecord from '@salesforce/apex/PhoneBook_LC_HUM.createRecord';
import getClonedInteractionAboutId from '@salesforce/apex/PhoneBook_LC_HUM.getClonedInteractionAboutId';
import PureCloudEvents from "@salesforce/messageChannel/purecloud__ClientEvent__c";
import disconnectSaveCallData from '@salesforce/apexContinuation/PhoneBook_LC_HUM.disconnectSaveCallData';
import getAboutAccountId from '@salesforce/apex/PhoneBook_LC_HUM.getAboutAccountId';
import fetchSwitchs from '@salesforce/apex/PhoneBook_LC_HUM.fetchSwitchs';
import createAutoInteraction from '@salesforce/apex/PhoneBook_LC_HUM.createAutoInteraction';
import getHoldTime from '@salesforce/apex/PhoneBook_LC_HUM.getHoldTime';
import { phoneBookConstants } from './pureCloudEventsConstantsHum';
import { getSearchMapping } from './pureCloudEventsHelper';
import { getLabels } from './pureCloudEventsHelper';
import PhoneBook_SecurePay_SUCCESS_HUM from '@salesforce/label/c.PhoneBook_SecurePay_SUCCESS_HUM';
import PHONEBOOKLMS from "@salesforce/messageChannel/phoneBookHum__c";
import MYAHLMS from "@salesforce/messageChannel/myAssistantAtHumana_CMP_LMS__c"; // Added for US#4951430 - AI Chat Assistant Panel
import isMyAHSwitchedON from '@salesforce/apex/MyAssistantAtHumana_LC_HUM.getMyAHOnOffSwitchBooleanValue'; //MyAH Feature Switch - Added for US#4951430
import hasMyAHAccess from '@salesforce/customPermission/CRMS_900_My_Assistant_At_Humana'; // Added for US#4951430 - AI Chat Assistant Panel
export default class pureCloudEventsHum extends NavigationMixin(LightningElement) {
    @wire(MessageContext)
    messageContext;
    subscription = null;
    ackSubscription = null;
    eventlogchecklogout;
    response;
    uuid;
    genesysInteractionId;
    receivedMessage;
    isDisabled = false;
    isDisabledUnsb = true;
    isSearchLoaded = false;
    isTransferredCall = false;
    isConsultBlindClicked = false;
    isrefresh = false;
    switches;
    secureSession = false;
    oldtimestamp;
    securePayOption;
    billingCardName;
    securePayDetails;
    tabPageRef;
    holdTime;
    timeIntervalInstance;
    labels = getLabels();
    featureSwitchMyAH = false;
    @wire(CurrentPageReference)
    wiredPageRef(pageRef) {
        this.pageRef = pageRef;
        if (this.pageRef) {
            if (this.pageRef.attributes.apiName == 'CRMSearch') {
                this.tabPageRef = true;
            }
        }
    }

    subscribeevents() {
        this.isDisabled = true;
        this.isDisabledUnsb = false;
        if (this.subscription) {
            return;
        }
        this.subscription = subscribe(
            this.messageContext,
            PureCloudEvents,
            eventData => {
                this.callPureCloudEvents(eventData);
            },
            { scope: APPLICATION_SCOPE }
        );
    }
    callPureCloudEvents(eventData) {
        if (eventData) {
            if (eventData.type === 'UserAction' && eventData.category === 'login') {
                var callAction = phoneBookConstants.LOGGED_IN;
                var callLogInfo = phoneBookConstants.CRM_STRIDE + JSON.stringify(eventData);
                var reason = eventData.category;
                this.eventlogchecklogout = false;
                this.template.querySelector('c-phone-book-hum').handleLogin(true);
                this.createLogs(callAction, callLogInfo, '', '', reason, '', '', '');
            }
            if (eventData.type === 'UserAction' && eventData.category === 'logout' && this.eventlogchecklogout === false) {
                var callAction = phoneBookConstants.LOGGED_OUT;
                var callLogInfo = phoneBookConstants.CRM_STRIDE + JSON.stringify(eventData);
                var reason = eventData.category;
                this.eventlogchecklogout = true;
                this.template.querySelector('c-phone-book-hum').handleLogin(false);
                this.createLogs(callAction, callLogInfo, '', '', reason, '', '', '');
            }
            if (eventData.type === 'Interaction' && eventData.category === 'change' &&
                eventData.data.new.isDisconnected === false && eventData.data.new.isConnected === true &&
                eventData.data.old.isDisconnected === false && eventData.data.old.isConnected === false &&
                eventData.data.new.direction === phoneBookConstants.INBOUND && eventData.data.new.isInternal != true) {
                var interactionId = eventData.data.new.id;
                var callAction = phoneBookConstants.INTERACTION_CONNECTED;
                var callLogInfo = phoneBookConstants.CRM_STRIDE + JSON.stringify(eventData);
                var reason = 'connected';
                var category = 'change';
                var SPExtrnlId = eventData.data.new.id;
                this.genesysInteractionId = eventData.data.new.id;
                var callLogType = phoneBookConstants.INBOUND;
                this.isrefresh = false;
                var popoutval = true;
                if (this.switches['Switch_2546987'] != false) {
                    this.dispatchEvent(new CustomEvent('disablepopout', {
                        detail: { popoutval }
                    }));
                }
                if (eventData.data.new.attributes) {
                    if (eventData.data.new.attributes.hasOwnProperty(phoneBookConstants.UUIDATA)) {
                        var uuid = eventData.data.new.attributes.uuiData;
                        this.uuid = uuid;
                    }
                    else if (eventData.data.new.attributes.hasOwnProperty(phoneBookConstants.Participant_uuiData)) {
                        var uuid = eventData.data.new.attributes[phoneBookConstants.Participant_uuiData];
                        this.uuid = uuid;
                    }
                    var attributesvalue ={};
                    var newAttributesObj = {};
                    var calllogattributes =null;
                    if (this.switches[phoneBookConstants.updateCampaignNameAndKeycodeSwitch] != false) {
                        if(eventData.data.new.attributes != undefined){
                            attributesvalue = eventData.data.new.attributes
                             for(let key in attributesvalue){
                               if(key.startsWith('participant.')){
                                   let newKey = key.replace('participant.','').toLowerCase().replace(/\s/g,'') 
                                   newAttributesObj[newKey] = attributesvalue[key]
                               }
                               else{
                                   let newKey = key.toLowerCase().replace(/\s/g,'')
                                   newAttributesObj[newKey] = attributesvalue[key]
                               }
                           }
                           calllogattributes = JSON.stringify(newAttributesObj);
                           }
                   }
                   else{
                    if (eventData.data.new.attributes.hasOwnProperty(phoneBookConstants.Participant_CampaignName) || 
                        eventData.data.new.attributes.hasOwnProperty(phoneBookConstants.Participant_Keycode )) {
                        
                        attributesvalue.campaignname = eventData.data.new.attributes[phoneBookConstants.Participant_CampaignName];
                        attributesvalue.keycode = eventData.data.new.attributes[phoneBookConstants.Participant_Keycode];
                        calllogattributes = JSON.stringify(attributesvalue); 
                    }
                }
                    this.getCallData(interactionId, uuid, callAction, callLogInfo, callLogType, SPExtrnlId, reason, category,calllogattributes);
                }
                this.isConsultBlindClicked = false;
            }
            if (eventData.type === 'PureCloud.Interaction.updateState' && eventData.data.action === 'secureSession') {
                this.secureSession = true;
            }
            if (eventData.type === 'Interaction' && eventData.category === 'change' &&
                eventData.data.new.isDisconnected === false && eventData.data.new.isConnected === true &&
                eventData.data.old.isDisconnected === false && eventData.data.old.isConnected === true &&
                eventData.data.new.attributes && eventData.data.new.attributes.hasOwnProperty(phoneBookConstants.SF_StatusPayment) && this.secureSession === true) {
                var statusPayment = eventData.data.new.attributes[phoneBookConstants.SF_StatusPayment] ? eventData.data.new.attributes[phoneBookConstants.SF_StatusPayment] : '';
                var failReason = eventData.data.new.attributes[phoneBookConstants.SF_FailureReason] ? eventData.data.new.attributes[phoneBookConstants.SF_FailureReason] : '';
                var interactionId = eventData.data.new.id;
                if ((statusPayment != '' || failReason != '') && this.oldtimestamp != eventData.data.new.attributes[phoneBookConstants.SF_TimeStamp]) {
                    this.secureSession = false;
                    if (statusPayment.toLowerCase().includes(PhoneBook_SecurePay_SUCCESS_HUM.toLowerCase())) {
                        alert(phoneBookConstants.SUCCESS_CREDITCARD);
                        this.template.querySelector('c-phone-book-hum').resetSecurePaymentParams();
                    }
                    else {
                        alert(statusPayment + '. ' + failReason);
                    }
                    this.oldtimestamp = eventData.data.new.attributes[phoneBookConstants.SF_TimeStamp];
                    var callAction = phoneBookConstants.INTERACTION_SECURE_TRANSFER;
                    var callLogConstructor = Object.assign({}, this.securePayDetails, eventData.data.new.attributes);
                    delete callLogConstructor.uuiData;
                    var callLogInfo = phoneBookConstants.CRM_STRIDE + JSON.stringify(callLogConstructor).replaceAll('Participant.', '');
                    var reason = 'connected';
                    var category = 'change';
                    this.createLogs(callAction, callLogInfo, '', interactionId, reason, category, '', this.uuid);
                }
            }
            if (eventData.type === 'Interaction' && eventData.category === 'connect' &&
                eventData.data.isDisconnected === false && eventData.data.isConnected === true &&
                eventData.data.direction === phoneBookConstants.OUTBOUND) {
                var interactionId = eventData.data.id;
                var callAction = phoneBookConstants.INTERACTION_CONNECTED;
                var callLogInfo = phoneBookConstants.CRM_STRIDE + JSON.stringify(eventData);
                var reason = 'connected';
                var category = 'change';
                var SPExtrnlId = eventData.data.id;
                var callLogType = phoneBookConstants.OUTBOUND;
                this.isrefresh = false;
                this.template.querySelector('c-phone-book-hum').handleoutboundcall(interactionId, this.isrefresh, '');
                this.createLogs(callAction, callLogInfo, callLogType, SPExtrnlId, reason, category, '', '');
                var popoutval = true;
                if (this.switches['Switch_2546987'] != false) {
                    this.dispatchEvent(new CustomEvent('disablepopout', {
                        detail: { popoutval }
                    }));
                }
            }
            if (eventData.type === 'UserAction' && eventData.category === 'status') {
                var callAction = phoneBookConstants.STATUS_CHANGED + eventData.data.status;
                var callLogInfo = phoneBookConstants.CRM_STRIDE + JSON.stringify(eventData);
                var SPExtrnlId = eventData.data.id;
                var reason = eventData.category;
                this.createLogs(callAction, callLogInfo, '', SPExtrnlId, reason, '', '', '');
            }
            if (eventData.type === 'UserAction' && eventData.category === 'station') {
                var callAction = phoneBookConstants.STATION;
                var callLogInfo = phoneBookConstants.CRM_STRIDE + JSON.stringify(eventData);
                var SPExtrnlId = eventData.data.id;
                var reason = eventData.category;
                this.createLogs(callAction, callLogInfo, '', SPExtrnlId, reason, '', '', '');
            }
            if (eventData.type === 'Interaction' && eventData.category === 'add' && eventData.data.isInternal != true) {
                var callAction = 'Interaction :  ' + eventData.category;
                var callLogInfo = phoneBookConstants.CRM_STRIDE + JSON.stringify(eventData);
                var category = 'change';
                var reason = 'connected';
                var SPExtrnlId = eventData.data.id;
                this.genesysInteractionId = eventData.data.id;
                this.isrefresh = true;
                var uuid = '';
                var popoutval = true;
                if (this.switches['Switch_2546987'] != false) {
                    this.dispatchEvent(new CustomEvent('disablepopout', {
                        detail: { popoutval }
                    }));
                }
                if (eventData.data.isConnected === true && eventData.data.direction === phoneBookConstants.INBOUND) {
                    this.oldtimestamp = eventData.data.attributes ? eventData.data.attributes[phoneBookConstants.SF_TimeStamp] : '';
                    var interactionId = eventData.data.id;
                    var callLogType = phoneBookConstants.INBOUND;
                    if (eventData.data.attributes) {
                        if (eventData.data.attributes.hasOwnProperty(phoneBookConstants.UUIDATA)) {
                            uuid = eventData.data.attributes.uuiData;
                            this.uuid = uuid;
                        }
                        else if (eventData.data.attributes.hasOwnProperty(phoneBookConstants.Participant_uuiData)) {
                            uuid = eventData.data.attributes[phoneBookConstants.Participant_uuiData];
                            this.uuid = uuid;
                        }
                        this.getCallData(interactionId, uuid, callAction, callLogInfo, callLogType, SPExtrnlId, reason, category,calllogattributes);
                    }
                }
                else if (eventData.data.isConnected === true && eventData.data.direction === phoneBookConstants.OUTBOUND) {
                    this.oldtimestamp = eventData.data.attributes ? eventData.data.attributes[phoneBookConstants.SF_TimeStamp] : '';
                    var interactionId = eventData.data.id;
                    var callLogType = phoneBookConstants.OUTBOUND;
                    if (eventData.data.attributes) {
                        if (eventData.data.attributes.hasOwnProperty(phoneBookConstants.UUIDATA)) {
                            uuid = eventData.data.attributes.uuiData;
                            this.uuid = uuid;
                        }
                        else if (eventData.data.attributes.hasOwnProperty(phoneBookConstants.Participant_uuiData)) {
                            uuid = eventData.data.attributes[phoneBookConstants.Participant_uuiData];
                            this.uuid = uuid;
                        }
                    }
                    this.createLogs(callAction, callLogInfo, callLogType, SPExtrnlId, '', category, '', uuid);
                    this.template.querySelector('c-phone-book-hum').handleoutboundcall(interactionId, this.isrefresh, this.uuid);
                }
            }
            if (eventData.type === 'Interaction' && eventData.category === 'blindTransfer') {
                var callAction = 'Interaction :  ' + eventData.category;
                var callLogInfo = phoneBookConstants.CRM_STRIDE + JSON.stringify(eventData);
                var category = 'change';
                var SPExtrnlId = eventData.data;
                this.createLogs(callAction, callLogInfo, '', SPExtrnlId, '', category, '', '');
            }
            if (eventData.type === 'Interaction' && eventData.category === 'consultTransfer') {
                var callAction = 'Interaction :  ' + eventData.category;
                var callLogInfo = phoneBookConstants.CRM_STRIDE + JSON.stringify(eventData);
                var category = 'change';
                var SPExtrnlId = eventData.data;
                this.createLogs(callAction, callLogInfo, '', SPExtrnlId, '', category, '', '');
            }
            if (eventData.type === 'Interaction' && eventData.category === 'disconnect') {
                var callAction = phoneBookConstants.CALL_DISCONNECTED;
                var callLogInfo = phoneBookConstants.CRM_STRIDE + JSON.stringify(eventData);
                var reason = 'disconnected';
                var SPExtrnlId = eventData.data.id;
                localStorage.setItem('tabFlagL', 1);
                var popoutval = false;
                clearInterval(this.timeIntervalInstance);
                if (this.switches['Switch_2546987'] != false) {
                    this.dispatchEvent(new CustomEvent('disablepopout', {
                        detail: { popoutval }
                    }));
                }
                if (eventData.data.attributes) {
                    if (eventData.data.attributes.hasOwnProperty(phoneBookConstants.UUIDATA)) {
                        this.uuid = eventData.data.attributes.uuiData;
                    }
                    else if (eventData.data.attributes.hasOwnProperty(phoneBookConstants.Participant_uuiData)) {
                        this.uuid = eventData.data.attributes[phoneBookConstants.Participant_uuiData];
                    }
                }
                var startoDate;
                if(this.switches['Switch_4954802'] == true){
                    if (eventData.data.startTime != undefined && eventData.data.startTime !='' ) {
                        startoDate = new Date(eventData.data.startTime);  
                    }
                    else{
                        startoDate = new Date(eventData.data.endTime);  
                    }
                }
                else{
                    startoDate = new Date(eventData.data.connectedTime);
                }
                var startsDateESTFormatted = startoDate.toLocaleString('en-US', { timeZone: 'America/New_York', hour12: true, month: '2-digit', day: '2-digit', year: 'numeric', hour: "2-digit", minute: "2-digit", second: "2-digit" });
                var callStartTime = startsDateESTFormatted.replaceAll(',', '');
                var endoDate = new Date(eventData.data.endTime);
                var endsDateESTFormatted = endoDate.toLocaleString('en-US', { timeZone: 'America/New_York', hour12: true, month: '2-digit', day: '2-digit', year: 'numeric', hour: "2-digit", minute: "2-digit", second: "2-digit" });
                var callEndTime = endsDateESTFormatted.replaceAll(',', '');
                let disconnectattributes = {
                    AGENT_CALL_START_TIME: callStartTime,
                    isConsultBlindClicked: this.isConsultBlindClicked,
                    isTransferredCall: this.isTransferredCall,
                    AGENT_CALL_END_TIME: callEndTime,
                    UUID: this.uuid,
                    interactionId: SPExtrnlId,
                    Outbound: eventData.data.direction === phoneBookConstants.OUTBOUND ? true : false
                }
                if (this.switches['Switch_2357061'] != false && this.uuid != '' && this.uuid != undefined) {
                    disconnectSaveCallData({ disconnectattributes: disconnectattributes })
                        .then(response => {
                            this.isConsultBlindClicked = false;
                            this.isTransferredCall = false;
                            this.uuid = '';
                            this.isrefresh = false;
                            this.genesysInteractionId = '';
                        })
                        .catch(error => {
                            this.error = error;
                            this.isConsultBlindClicked = false;
                            this.isTransferredCall = false;
                            this.uuid = '';
                            this.isrefresh = false;
                            this.genesysInteractionId = '';
                        });
                }
                this.template.querySelector('c-phone-book-hum').handleDisconnectCall();
                this.createLogs(callAction, callLogInfo, '', SPExtrnlId, reason, '', '', '');
            }
            if (eventData.type === 'Interaction' && eventData.category === 'acw') {
                var callAction = phoneBookConstants.AWC_COMPLETED;
                var callLogInfo = phoneBookConstants.CRM_STRIDE + JSON.stringify(eventData);
                var reason = 'acw_completed';
                var SPExtrnlId = eventData.data.id;
                this.createLogs(callAction, callLogInfo, '', SPExtrnlId, reason, '', '', '');
            }
            if (eventData.type === 'UserAction' && eventData.category === 'routingStatus') {
                var callAction = phoneBookConstants.ROUTING_STATUS + eventData.data;
                var callLogInfo = phoneBookConstants.CRM_STRIDE + JSON.stringify(eventData);
                var reason = eventData.category;
                this.createLogs(callAction, callLogInfo, '', '', reason, '', '', '');
            }
            if (eventData.type === 'Interaction' && eventData.category === 'change' &&
                eventData.data.new.state === 'HELD' && eventData.data.old.state === 'CONNECTED' && this.switches['Switch_2760576'] != false) {
                this.startTimer(1);
            }
            if (eventData.type === 'Interaction' && eventData.category === 'add' && eventData.data.state === 'HELD'
                && this.switches['Switch_2760576'] != false) {
                var timeinterval = (new Date().getTime() - localStorage.getItem('RefreshStartTime')) / 1000;
                this.startTimer(parseInt(localStorage.getItem('HoldTime')) + parseInt(timeinterval));
            }
            if (eventData.type === 'Interaction' && eventData.category === 'change' &&
                eventData.data.old.state === 'HELD' && eventData.data.new.state === 'CONNECTED' && this.switches['Switch_2760576'] != false) {
                clearInterval(this.timeIntervalInstance);
                localStorage.setItem('HoldTime', 0);
                localStorage.setItem('RefreshStartTime', 0);
            }
        }
    }

    startTimer(val) {
        var ref = this;
        var count = val;
        this.timeIntervalInstance = setInterval(function () {
            count++;
            localStorage.setItem('HoldTime', count);
            localStorage.setItem('RefreshStartTime', new Date().getTime());
            if (count > ref.holdTime) {
                var sAlertTime = new Date().getTime();
                alert(ref.labels.Hold_Popup);
                count = parseInt((new Date().getTime() - sAlertTime) / 1000);
                if (count > ref.holdTime) {
                    count = count % parseInt(ref.holdTime);
                }
            }
        }, 1000);
    }

    /*
    * Params - {callAction, callLogInfo, callLogType, SPExtrnlId, reason, category, caller, uuid}
    * Description - this method is used to create pureCloudLogs for every Genesys events getting fired.
    */
    createLogs(callAction, callLogInfo, callLogType, SPExtrnlId, reason, category, caller, uuid) {
        createRecord({
            callAction: callAction,
            callInfoLog: callLogInfo,
            callLogType: callLogType,
            SPExtrnlId: SPExtrnlId,
            reason: reason,
            category: category,
            caller: caller,
            UUID: uuid
        })
            .then(result => {
            })
            .catch(error => {
                this.error = error;
            });
    }

    /*
    * Params - {interactionId, uuid, callAction, callLogInfo, callLogType, SPExtrnlId, reason, category}
    * Description - this method is called on call connected the fetch the data from IVR using getCallData webservice.
    */
    getCallData(interactionId, uuid, callAction, callLogInfo, callLogType, SPExtrnlId, reason, category,calllogattributes) {
        var uuidResponse = uuid;
        var MemberDOBFormatSwitch;
        if (this.switches[phoneBookConstants.MemberDOBFormatSwitch] != false) { 
         MemberDOBFormatSwitch =true; 
        }
        startRequest({ uuid: uuidResponse })
            .then(response => {
                var responseModal = response;
                this.getSearchResults(interactionId, uuid, responseModal, callAction, callLogInfo, callLogType, SPExtrnlId, reason, category,calllogattributes);
            })
            .catch(error => {
                this.error = error;
                let searchModal = {};
                let searchObj = {};
                let searchTypeA_R = ['', ''];

                searchTypeA_R.forEach(function (item) {
                    Object.assign(searchObj, getSearchMapping('', item, MemberDOBFormatSwitch));
                });
                this.handleNavigate(searchObj);
                this.template.querySelector('c-phone-book-hum').handleincomingcall(phoneBookConstants.SELECT, searchModal, interactionId, uuid);
            });
    }

    getSearchResults(interactionId, uuid, res, callAction, callLogInfo, callLogType, SPExtrnlId, reason, category,calllogattributes) {
        let response = JSON.parse(res);
        var MemberDOBFormatSwitch;
        if (this.switches[phoneBookConstants.MemberDOBFormatSwitch] != false) { 
            MemberDOBFormatSwitch =true; 
        }
        if (response) {
            let callDataValue = response.GetCallDataResponse.GetCallDataResult.CallDataValues.CallDataValuesList;
            const searchModal = {};
            var caller = '';
            callDataValue.map(item => {
                searchModal[item.DataName] = item.DataValue;
            });
            this.createLogs(callAction, callLogInfo, callLogType, SPExtrnlId, reason, category, caller, uuid);
            //US 3944531-T1PRJ0036776: UCID not mapping to UUID field in Salesforce CRM (INC2009416)- Added check for Spanish(S) caller type
            if (searchModal[phoneBookConstants.INQR_TYP] === phoneBookConstants.MEMBER_M || searchModal[phoneBookConstants.INQR_TYP] === phoneBookConstants.MEMBER_S) {
                this.template.querySelector('c-phone-book-hum').handleincomingcall(phoneBookConstants.MEMBER, searchModal, interactionId, uuid);
            }
            if (searchModal[phoneBookConstants.INQR_TYP] === phoneBookConstants.GROUP_G || searchModal[phoneBookConstants.INQR_TYP] === phoneBookConstants.EMPLOYER_E) {
                this.template.querySelector('c-phone-book-hum').handleincomingcall(phoneBookConstants.GROUP, searchModal, interactionId, uuid);
            }
            if (searchModal[phoneBookConstants.INQR_TYP] === phoneBookConstants.AGENT_A || searchModal[phoneBookConstants.INQR_TYP] === phoneBookConstants.BROKER_B) {
                this.template.querySelector('c-phone-book-hum').handleincomingcall(phoneBookConstants.AGENT, searchModal, interactionId, uuid);
            }
            //US 3944531-T1PRJ0036776: UCID not mapping to UUID field in Salesforce CRM (INC2009416)- Added check for PPI caller type
            if (searchModal[phoneBookConstants.INQR_TYP] === phoneBookConstants.PROVIDER_P || searchModal[phoneBookConstants.INQR_TYP] === phoneBookConstants.PROVIDER_PPI) {
                this.template.querySelector('c-phone-book-hum').handleincomingcall(phoneBookConstants.PROVIDER, searchModal, interactionId, uuid);
            }
            if (searchModal[phoneBookConstants.INQR_TYP] === phoneBookConstants.UNKNOWN_U) {
                this.template.querySelector('c-phone-book-hum').handleincomingcall(phoneBookConstants.UNKNOWN, searchModal, interactionId, uuid);
            }
            if (!searchModal.hasOwnProperty(phoneBookConstants.INQR_TYP)) {
                this.template.querySelector('c-phone-book-hum').handleincomingcall(phoneBookConstants.UNKNOWN, searchModal, interactionId, uuid);
            }
            if (this.switches['Switch_4707880'] == false){
                if ((searchModal.hasOwnProperty(phoneBookConstants.ISMANUAL_TRANSFER) && searchModal[phoneBookConstants.ISMANUAL_TRANSFER] != '') || (searchModal.hasOwnProperty(phoneBookConstants.NUMBER_OF_TRANSFERS) && searchModal[phoneBookConstants.NUMBER_OF_TRANSFERS] != '')) {
                    this.isTransferredCall = true;
                }
                else {
                    this.isTransferredCall = false;
                }
            }
            if (searchModal.hasOwnProperty(phoneBookConstants.TRANSFER_ETIM) && searchModal[phoneBookConstants.TRANSFER_ETIM] === '' && this.isrefresh) {
                this.isConsultBlindClicked = true;
            }
            else {
                this.isConsultBlindClicked = false;
            }
            let searchObj = {};
            let searchTypeA_R = [phoneBookConstants.INQR_TYP, phoneBookConstants.INQA_TYP];
            if ((searchModal[phoneBookConstants.INQR_TYP] === searchModal[phoneBookConstants.INQA_TYP]) || (searchModal[phoneBookConstants.INQR_TYP] != '' && searchModal[phoneBookConstants.INQA_TYP] == '')) {
                Object.assign(searchObj, getSearchMapping(searchModal, searchModal[phoneBookConstants.INQR_TYP], phoneBookConstants.INQR_TYP, MemberDOBFormatSwitch));
            }
            else {
                searchTypeA_R.forEach(function (item) {
                    Object.assign(searchObj, getSearchMapping(searchModal, searchModal[item], item, MemberDOBFormatSwitch));
                });
            }
            if (this.switches['Switch_2230090'] != false) {
                if (this.isTransferredCall) {
                    if (localStorage.getItem('tabFlagL') == 1) {
                        localStorage.setItem('tabFlagL', 0);
                        getAboutAccountId({ UUID: this.uuid, isrefresh: this.isrefresh, authIndicator: searchModal['AUTHENTICATION_IND'], genesysInteractionId: this.genesysInteractionId, searchModal: searchModal, calllogattributes: calllogattributes })
                            .then(response => {
                                if (response && response.aboutId) {
                                     const aboutId = response.aboutId;
                                     const accIntIds = { "aboutId":aboutId,"sInteracId": response.sIntId};
                                     //Passing interaction id to account detail page to display data in interaction log panel on account page for transferred call.
                                        const navigateToAboutPage = new CustomEvent('openAboutPage', {
                                        detail: { accIntIds }
                                    });
                                    this.dispatchEvent(navigateToAboutPage);
                                }
                                else {
                                //Passing interaction id to search page to display interaction information on search page.
                                if(response && response.sIntId) searchObj["sInteractionId"] = response.sIntId;
                                    this.handleNavigate(searchObj);
                                }
                            })
                            .catch(error => {
                                this.error = error;
                            });
                    }
                    else {
                        getClonedInteractionAboutId({ UUID: this.uuid })
                            .then(response => {
                                if (response && response.aboutId) {
                                   const aboutId = response.aboutId;
                                   //Passing interaction id to account detail page to display data in interaction log panel on account page for transferred call.
                                    const accIntIds = {"aboutId":aboutId,"sInteracId": response.sIntId};
                                    const navigateToAboutPage = new CustomEvent('openAboutPage', {
                                        detail: { accIntIds }
                                    });
                                    this.dispatchEvent(navigateToAboutPage);
                                }
                                else {
                                    //Passing interaction id to search page to display interaction information on search page.
                                if(response && response.sIntId) searchObj["sInteractionId"] = response.sIntId;
                                    this.handleNavigate(searchObj);
                                }
                            })
                            .catch(error => {
                                this.error = error;
                            });
                    }
                }
                else {
                    if (localStorage.getItem('tabFlagL') == 1) {
                        localStorage.setItem('tabFlagL', 0);
                        createAutoInteraction({ searchModal: searchModal, UUID: this.uuid, isrefresh: this.isrefresh, genesysInteractionId: this.genesysInteractionId,calllogattributes: calllogattributes })
                            .then(response => {
                                if(this.featureSwitchMyAH) {
                                    publish(this.messageContext, MYAHLMS, {msgType: "PureCloudEvent", searchModal: searchModal}); // Added for US#4951430
                                  }
                            searchObj["sInteractionId"] = response;
                                this.handleNavigate(searchObj);
                            })
                            .catch(error => {
                                this.handleNavigate(searchObj);
                            })
                    }
                    else {
                            this.handleNavigate(searchObj);
                    }
                }
            }
            else {
                if (localStorage.getItem('tabFlagL') == 1) {
                    localStorage.setItem('tabFlagL', 0);
                    createAutoInteraction({ searchModal: searchModal, UUID: this.uuid, isrefresh: this.isrefresh, genesysInteractionId: this.genesysInteractionId,calllogattributes: calllogattributes })
                        .then(response => {
                            if(this.featureSwitchMyAH) {
                                publish(this.messageContext, MYAHLMS, {msgType: "PureCloudEvent", searchModal: searchModal}); // Added for US#4951430
                                }
                        searchObj["sInteractionId"] = response;
                            this.handleNavigate(searchObj);
                        })
                        .catch(error => {
                            this.handleNavigate(searchObj);
                        })
                }
                else {
                    this.handleNavigate(searchObj);
                }
            }
        }
    }
    /*
    * Params - {dataModal having the details about the search page to be opened}
    * Description - this method is used to navigate to the CRM search tab.
    */
    handleNavigate(dataModal) {
        this[NavigationMixin.Navigate]({
            type: 'standard__navItemPage',
            attributes: {
                apiName: 'CRMSearch'
            },
        });
        if (this.tabPageRef == true) {
            this.isSearchLoaded = true;
        }
        if (this.pageRef.attributes.type == 'utility') {
            setTimeout(() => {
                publish(this.messageContext, PHONEBOOKLMS, { messageToSend: { callData: dataModal } });
            }, 500);
        }
        else {
            if (this.isSearchLoaded) {
                setTimeout(() => {
                    publish(this.messageContext, PHONEBOOKLMS, { messageToSend: { callData: dataModal } });
                }, 200);

            } else {
                this.sendCallInfo(dataModal);
            }
        }
    }

    sendCallInfo(dataModal) {
        setTimeout(() => {

            if (this.isSearchLoaded) {
                publish(this.messageContext, PHONEBOOKLMS, { messageToSend: { callData: dataModal } });
            }
            else {
                this.sendCallInfo(dataModal);
            }
        }, 10);
    }

    updateAcknowlegement(result) {
        this.isSearchLoaded = true;
    }
    handleCallTransfer() {
        this.dispatchEvent(new CustomEvent('parentcalltransfer'));
    }
    openCtiHandler() {
        this.dispatchEvent(new CustomEvent('parentopencti'));
    }
    consultBlindHandler() {
        this.isConsultBlindClicked = true;
    }
    getSecurePayData(event) {
        this.securePayDetails = event.detail;
    }
    fetchallSwitchs() {
        fetchSwitchs()
            .then(response => {
                this.switches = response;
            })
            .catch(error => {
            })
    }
    fetchHoldTime() {
        getHoldTime({ sTobefetched: 'HoldTime' })
            .then(response => {
                this.holdTime = response;
            })
            .catch(error => {
            })
    }
    subscribeToMessageChannel() {
        if (!this.ackSubscription) {
            this.ackSubscription =
                subscribe(
                    this.messageContext,
                    PHONEBOOKLMS,
                    message => {
                        this.updateAcknowlegement(message);
                    },
                    { scope: APPLICATION_SCOPE }
                );
        }
    }
    unsubscribeToMessageChannel() {
        unsubscribe(this.ackSubscription);
        this.ackSubscription = null;
    }
    connectedCallback() {
        localStorage.setItem('tabFlagL', 1);
        this.fetchallSwitchs();
        this.fetchHoldTime();
        this.subscribeevents();
        this.subscribeToMessageChannel();
        isMyAHSwitchedON()
        .then(switchState=>{
          if(switchState === true && hasMyAHAccess){
            this.featureSwitchMyAH = true;
          }
        })
        .catch(error=>{})
    }
    disconnectedCallback() {
        this.unsubscribeToMessageChannel();
    }
}