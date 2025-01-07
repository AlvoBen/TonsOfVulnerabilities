/*
LWC Name        : PharmacyVerifyDemographics.js
Function        : LWC to display Verify Demography

Modification Log:
* Developer Name                  Date                         Description
*---------------------------------------------------------------------------------------------------------------------------------------------------------------
* Vishal Shinde                 7/28/2023	                   US-4833019-Mail Order Management - Pharmacy - “Accounts” tab – Verify Demographics - Lightning   
*****************************************************************************************************************************************************************/
import { LightningElement, api, wire } from 'lwc';
import getDetailsDemographics from '@salesforce/apexContinuation/VerifyDemograhics_LC_HUM.APIService';
import { getLabels, getLocaleDate } from 'c/crmUtilityHum';
import { publish, MessageContext, subscribe, unsubscribe } from 'lightning/messageService';
import humanaPharmacyLMS from '@salesforce/messageChannel/pharmacyLMSChannel__c';
import { loadStyle } from 'lightning/platformResourceLoader';
import customcss from '@salesforce/resourceUrl/LightningCRMAssets_SR_HUM';

export default class PharmacyVerifyDemographics extends LightningElement {
    @api recordId;
    @api bDisplayBox;
    @wire(MessageContext) messageContext;

    labels = getLabels();
    daysSinceVerified;
    lastVerifiedDate;
    lastVerifiedBy;


    connectedCallback() {
        this.bDisplayBox = this.bDisplayBox === 'true' ? true : false;
        this.processVerifyDemographicsData(this.labels.HUM_OnLoad);
        this.subscribeToMessageChannel();
    }


    renderedCallback() {
        Promise.all([
            loadStyle(this, customcss + '/CRM_Assets/styles/commonStyles.css')
        ]).catch(error => {
        });
    }

    subscribeToMessageChannel() {
        if (!this.subscription) {
            this.subscription = subscribe(
                this.messageContext,
                humanaPharmacyLMS,
                (message) => this.handleMessage(message)
            );
        }
    }

    handleMessage(message) {
        if (!this.isEmptyObj(message)) {
            if (message?.MessageName === 'fireVerifyDemographicsService') {
                this.processVerifyDemographicsData(message.messageDetails);
            }
        }
    }

    isEmptyObj(obj) {
        return obj && Object.keys(obj)?.length === 0 ? true : false;
    }

    processVerifyDemographicsData(sActionType) {
        getDetailsDemographics({ recId: this.recordId, action: sActionType }).then(result => {
            if (result && Array.isArray(result) && result?.length > 0) {
                this.daysSinceVerified = result[0]?.iDaysSinceLastVerified ?? '';
                this.lastVerifiedBy = `${result[0]?.sLastVerifiedBy ?? ''}-${result[0]?.networkUserId ?? ''}`;
                let verifiedDateLast = result[0]?.sLastVerifiedOn ?? '';
                let lVDate = new Date(verifiedDateLast)
                this.lastVerifiedDate = getLocaleDate(lVDate.toISOString().split('T')[0]);
                this.callLMSEvent(sActionType);
            }
        }).catch(error => {
            console.log("Error Occured", error);
        });
    }

    callLMSEvent(sActionType) {
        if (this.daysSinceVerified > 90 || sActionType === this.labels.HUM_OnClick) {
            let message = { messageDetails: this.daysSinceVerified, MessageName: "VerifyDemographics" };
            publish(this.messageContext, humanaPharmacyLMS, message);
        }
    }

    unsubscribeToMessageChannel() {
        unsubscribe(this.subscription);
        this.subscription = null;
    }


    disconnectedCallback() {
        this.unsubscribeToMessageChannel();
    }

}