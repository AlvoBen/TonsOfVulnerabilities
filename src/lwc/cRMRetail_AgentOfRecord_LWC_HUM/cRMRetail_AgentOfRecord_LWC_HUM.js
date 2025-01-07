/*
File Name        : CRMRetail_AgentOfRecord_LWC_HUM.js
Version          : 1.0 
Created Date     : 22/06/2023
Function         : Lightning Web Component used to show AOR details
Modification Log :
* Developer                 Code review         Date                  Description
*************************************************************************************************
* Mohamed Thameem                               22/06/2023            US4736604 - T1PRJ0154546 / SF / MF9 Storefront - AOR UI Component Upgrade[Tech]
**************************************************************************************************
*/
import { LightningElement, api} from 'lwc';
import getAccountRecord from '@salesforce/apex/AgentOfRecord_LTNG_C_HUM.getAccountRecord';
import retrieveAORDetails from '@salesforce/apex/AgentOfRecord_LTNG_C_HUM.retrieveAORDetails';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import { labels, allConstants } from 'c/crmretail_interactionutility_LWC_HUM';
var customLabels = labels;
var constants = allConstants;

export default class CRMRetail_AgentOfRecord_LWC_HUM extends LightningElement {

    aorNumber = '';
    aorName = '';
    isLoaded = false;
    showBody = false;
    aorErrorBody;
    showError = false;
    refreshDisabled = true;
    refreshCounter = 0;
    @api recordId;

    connectedCallback() {
        this.processInitiate();
    }

    processInitiate() {
        try {
            getAccountRecord({ accountId: this.recordId })
                .then(result => {
                    if (result) {
                        if (result[0].RecordType[constants.NAME] == customLabels.CRMRetail_Member) {
                            retrieveAORDetails({ accountId: this.recordId, accEnterpriseId: result[0].Enterprise_ID__c })
                                .then(result => {
                                    if (result) {
                                        if (result[constants.DATA][constants.STATE] === constants.SUCCESS_UPPERCASE) {
                                            this.aorName = result[constants.DATA][constants.AORNAME];
                                            this.aorNumber = result[constants.DATA][constants.AORNUM];
                                            this.refreshDisabled = !this.refreshDisabled ? true : this.refreshDisabled;
                                        }
                                        else if (result[constants.DATA][constants.STATE] === constants.ERROR_UPPERCASE) {
                                            this.refreshDisabled = this.refreshCounter > 1 ? true : false;
                                        }
                                        this.showBody = true;
                                    }
                                    else {
                                        this.refreshDisabled = this.refreshCounter > 1 ? true : false;
                                    }
                                })
                                .catch(error => {
                                    this.refreshDisabled = this.refreshCounter > 1 ? true : false;
                                    this.aorErrorBody = this.processApexErrorMessage(error);
                                    this.showBody = false;
                                    this.showError = true;
                                });
                        }
                        else {
                            this.aorName = constants.NA;
                            this.aorNumber = constants.NA;
                            this.showBody = true;
                        }
                    }
                })
                .catch(error => {
                    this.showBody = true;
                    this.refreshDisabled = this.refreshCounter > 1 ? true : false;
                    this.generateErrorMessage(customLabels.CRMRetail_Error_Label, customLabels.UNEXPECTED_ERROR, customLabels.ERROR_VARIANT, constants.STICKY);
                });
        }
        catch (error) {
            this.showBody = true;
            this.refreshDisabled = this.refreshCounter > 1 ? true : false;
            this.generateErrorMessage(customLabels.CRMRetail_Error_Label, customLabels.UNEXPECTED_ERROR, customLabels.ERROR_VARIANT, constants.STICKY);
        }
    }

    onRefresh() {
        this.showBody = false;
        this.showError = false;
        this.refreshCounter = this.refreshCounter + 1;
        this.processInitiate();
    }

    generateErrorMessage(title, msg, type, sMode) {
        const event = new ShowToastEvent({
            title: title,
            message: msg,
            variant: type,
            mode: sMode
        });
        this.dispatchEvent(event);
    }

    processApexErrorMessage(error) {
        var errMsg = '';
        if (error) {
            errMsg = error;
        }
        else {
            errMsg = "Unknown error";
        }
        return errMsg;
    }
}