/*

LWC Name        : ClaimDetailHighlightsPanelHum.js
Function        : Highlight Panel for Claim Detail Page

Modification Log:
* Developer Name                  Date                         Description
****************************************************************************************************************************
* Aishwarya Pawar                 01/20/2023                   REQ-4134643 - Added alert on Claim detail Page

****************************************************************************************************************************
*/
import { api, LightningElement,track, wire } from 'lwc';
import {MessageContext, publish, subscribe, APPLICATION_SCOPE, unsubscribe} from 'lightning/messageService';
import CONNECTOR_CHANNEL from '@salesforce/messageChannel/updateLablelHUM__c';
import { CurrentPageReference } from 'lightning/navigation';
import { performLogging, setEventListener, checkloggingstatus, clearLoggedValues, getLoggingKey } from 'c/loggingUtilityHum';
import pubSubHum from 'c/pubSubHum';
import { getRecord } from 'lightning/uiRecordApi';
import USER_ID from '@salesforce/user/Id';
import PROFILE_NAME_FIELD from '@salesforce/schema/User.Profile.Name';
import NETWORK_ID_FIELD from '@salesforce/schema/User.Network_User_Id__c';
import CURRENT_QUEUE from '@salesforce/schema/User.Current_Queue__c';
import getPolicyDetails from '@salesforce/apex/PoliciesSearchResults_LC_HUM.getPoliciesHighlightPanel';

export default class ClaimDetailHighlightsPanelHum extends LightningElement {
    @api sClaimNbr = '';
    @api bRendered = false;
    showLoggingIcon = true;
    @track profileName;
    @track netWorkId;
    @track workQueue;
    @track sMemberPlanId;
    @api recordId;
	 @api clmSubType;
    @track startLogging = false;
    @track accountId;
    collectedLoggedData = [];
    autoLogging = true;
    @wire(MessageContext)
    messageContext;
	@track displayAlertIcon=false;
	@wire(CurrentPageReference)
    currentPageReference(pageRef) {
        this.pageRef = pageRef;
    }
	  @wire(getRecord, {
        recordId: USER_ID,
        fields: [PROFILE_NAME_FIELD,NETWORK_ID_FIELD,CURRENT_QUEUE]
      }) 

     wireuser({ error, data }) {
        if (error) {
            this.error = error;
        } else if (data) {
            this.profilename = data.fields.Profile.value.fields.Name.value;
           
            this.netWorkId = data.fields.Network_User_Id__c.value;
            
            this.workQueue = data.fields.Current_Queue__c.value;
            
        }
    }

	getAccountId(){
        getPolicyDetails({
            recId: this.recordId
        })
        .then(oData => {
            this.accountId = oData.Member.Id;
			this.clmSubType = 'Claim: ' + this.sClaimNbr;
            this.displayAlertIcon=true;
        })
        .catch(error => {
            console.error("Error in getPolicyDetails-->", error);
        });
    }
    async connectedCallback(){
		let url = '';
        url = this.pageRef.attributes.url;
        let navData = url.split('&');
        let newObj = {
        };
        navData.map(item => {
            let splittedData = item.split('=');
            newObj[splittedData[0]] = splittedData[1];
        });
        this.sClaimNbr = newObj.ClaimNbr;
        this.recordId = newObj.recordId;
        await this.getAccountId();
        this.subscribeToMessageChannel();
        
	 this.loggingkey = getLoggingKey();
        this.startLogging = checkloggingstatus(
            this.loggingkey ? this.loggingkey : getLoggingKey()
        );
        pubSubHum.registerListener(
            'loggingevent',
            this.loggingEventHandler.bind(this),
            this
        );
    }
    unsubscribeToMessageChannel() {     
        unsubscribe(this.subscription);
        this.subscription = null;
      }
  
    disconnectedCallback() {
          this.unsubscribeToMessageChannel();
      }

    subscribeToMessageChannel(){
        if (!this.subscription) {
            this.subscription = subscribe(
                this.messageContext,
                CONNECTOR_CHANNEL,
                (message) => this.handleMessage(message),
                { scope: APPLICATION_SCOPE }
                
            );
        }
    }
    handleMessage(message) {
        if(message)
        { 
            this.sClaimNbr = message.claimNumber;  
        }
        if (this.sClaimNbr == message.claimNumber){
            this.unsubscribeToMessageChannel();
        }
    }

    copyToClipboard(event)
    {
        let content= event.currentTarget.dataset.cont;
        let tempTextAreaField = document.createElement('textarea');
        tempTextAreaField.value = content;
        document.body.appendChild(tempTextAreaField);
        tempTextAreaField.select();
        document.execCommand('copy');
        tempTextAreaField.remove();
    }


    handleLogging(event) {
        if (this.startLogging) {
            performLogging(
                event,
                this.createRelatedField(),
                'billingSummaryHum',
                this.loggingkey ? this.loggingkey : getLoggingKey(),
                this.pageRef
            );
        }
    }
     createRelatedField() {
        return [
            {
                label: 'Claim Details',
                value: this.sClaimNbr
            }
        ];
    }

    loggingEventHandler(data) {
        if (data.MessageName === 'StartLogging') {
            this.startLogging = true;
        }
        if (data.MessageName === 'StopLogging') {
            this.startLogging = false;
            clearLoggedValues(this.loggingkey);
        }
    }
}