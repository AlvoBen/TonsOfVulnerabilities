/*
Function        : LWC to display demographics details .

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Nirmal Garg                    07/18/2022                 user story 4861950, 4861945
* Atul Patil                    07/28/2023                  user story 4861950, 4861945
*****************************************************************************************************************************/
import { LightningElement, api, track } from 'lwc';
import { updateCapType } from 'c/pharmacyHPIEIntegrationHum';
import { toastMsge } from "c/crmUtilityHum";
export default class PharmacyCaptypeHum extends LightningElement {
    @track capType;
    @api preferenceDetails;
    @api enterpriseId;
    @api userId;
    @api organization;
    @track capTypeFlag;
    @track displayCapTypePopup = false;
    @track loaded = true;

    @api setPreferenceDetails(data) {
        this.preferenceDetails = data;
        this.displayCapType();
    }

    displayCapType() {
        this.capType = this.preferenceDetails?.preference?.capType?.code ?? '';
        this.capTypeFlag = this.capType && this.capType?.toLowerCase() === 's' ? true : false;
    }

    connectedCallback() {
        this.displayCapType();
    }

    toggleCapType(event) {
        this.displayCapTypePopup = true;
    }

    handleUpdateCapType(event) {
        this.displayCapTypePopup = false;
        this.loaded = false;
        this.callService(event);
    }

    callService(event) {
        updateCapType(this.enterpriseId, this.userId, this.organization ?? 'HUMANA', event?.detail?.capType)
            .then(result => {
                this.loaded = true;
                this.callPreferenceService();
                toastMsge('Success', 'Cap Type Updated', 'success', 'dismissible');
            }).catch(error => {
                this.loaded = true;
                console.log(error);
                toastMsge('Failed', 'Failed to Update Cap Type', 'error', 'dismissible');
            })
    }

    callPreferenceService() {
        this.dispatchEvent(new CustomEvent('callpreference', {
            bubbles: true,
            composed: true
        }));
    }

    handleCloseModal() {
        this.displayCapTypePopup = false;
    }

    showToast(strTitle, strMessage, strStyle) {
        this.dispatchEvent(new ShowToastEvent({
            title: strTitle,
            message: strMessage,
            variant: strStyle
        }));
    }
}