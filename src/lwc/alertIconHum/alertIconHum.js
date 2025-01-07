/*******************************************************************************************************************************
LWC JS Name : alertIconHum.js
Function    : This JS serves as controller to alertIconHum.html.

Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Jonathan Dickinson                                      03/31/2023                initial version - REQ 4365030
 *********************************************************************************************************************************/
import { LightningElement, api, track, wire } from 'lwc';
import alertRefreshMessageChannel from '@salesforce/messageChannel/alertRefresh__c';
import { MessageContext, subscribe, unsubscribe, APPLICATION_SCOPE } from 'lightning/messageService';
import { CurrentPageReference } from 'lightning/navigation';
export default class AlertIconHum extends LightningElement {

    @api
    alertCount;

    @track pageRef;
    alertRefreshSubscription;

    @wire(CurrentPageReference)
    wiredPageRef(pageRef) {
        this.pageRef = pageRef;
    }

    @wire(MessageContext)
    messageContext;

    connectedCallback() {
        this.subscribeToAlertRefreshMessageChannel();
    }

    disconnectedCallback() {
        this.unsubscribeToAlertRefreshMessageChannel();
    }

    get alertCountdisplay() {
        return this.alertCount && this.alertCount > 0 ? true : false;
    }

    handleClick() {
        if (this.alertCount) {
            this.dispatchEvent(new CustomEvent('showalert'));
        }
    }

    subscribeToAlertRefreshMessageChannel() {
        if (!this.alertRefreshSubscription) {
            this.alertRefreshSubscription = subscribe(
                this.messageContext,
                alertRefreshMessageChannel,
                (message) => this.handleAlertRefreshMessage(message),
                { scope: APPLICATION_SCOPE }
            );
        }
    }
    handleAlertRefreshMessage(message) {
        if (this.pageRef && this.pageRef?.attributes && this.pageRef?.attributes?.objectApiName === 'Account') {
            if (this.pageRef && this.pageRef?.attributes && this.pageRef?.attributes?.recordId
                && this.pageRef?.attributes?.recordId === message?.accountId) {
                this.fireRefreshEvent(message);
            }
        } else if (this.pageRef && this.pageRef?.state && this.pageRef?.state?.ws &&
            this.pageRef?.state?.ws?.includes(message?.accountId)) {
            this.fireRefreshEvent(message);
        }
    }

    fireRefreshEvent() {
        this.dispatchEvent(new CustomEvent('refresh'));
    }

    get alertsIconClass() {
        return this.alertCount && this.alertCount > 0 ? 'alerticon' : 'noalerticon';
    }

    unsubscribeToAlertRefreshMessageChannel() {
        unsubscribe(this.alertRefreshSubscription);
        this.alertRefreshSubscription = null;
    }
}