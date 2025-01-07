import { LightningElement, api, wire, track } from 'lwc';
import {ShowToastEvent} from 'lightning/platformShowToastEvent';
import getIndicatorsValue from '@salesforce/apex/CRMRetail_Custom_Notification_C_HUM.getVisitorIndicators';
import validateAccess from '@salesforce/apex/CRMRetail_Custom_Notification_C_HUM.validateAccess';
import EDIT_TEXT from '@salesforce/label/c.CRMRetail_Edit_Notifications';
import NOTIF_HEADER from '@salesforce/label/c.CRMRetail_Notifications_Header';
import SUCCESS_TEXT from '@salesforce/label/c.CRMRetail_NotificationSuccessText';

export default class CRMRetail_Custom_Notification_Section_LWC_HUM extends LightningElement {
    @api recordId;
    @api open;
    @api label;
    @track isModalOpen = false;
    @track isAdmin = false;
    @track indicators = [];
    label = {
        EDIT_TEXT,
        NOTIF_HEADER
    };
    
    connectedCallback(){
        if(typeof this.open === 'undefined') this.open = true;
        getIndicatorsValue({ recId : this.recordId})
            .then(result => {
                var data = JSON.parse(result);
                var field = {};
                for(var key in data){
                    if(data[key] === true || data[key] === false || data[key] === 'true'){
                        field.isBoolean = true;
                    }else{
                        field.isBoolean = false;
                    }
                    field.label = key;
                    field.value = data[key];                    
                    this.indicators.push({...field});
                }
            })
            .catch(error => {                
                this.error = error;
            });
        validateAccess()
            .then(result => {
                this.isAdmin = result;                
            })
            .catch(error => {
                this.error = error;
            });        
    }
    get sectionClass(){
        return this.open ? 'slds-accordion__section slds-is-open' : 'slds-accordion__section';
    }
    get btnClass(){
        return this.open ? 'utility:chevrondown' : 'utility:chevronright';
    }
    handleClick(){
        this.open = !this.open;
    }
    openModal() {
        this.isModalOpen = true;
    }
    closeModal() {
        this.isModalOpen = false;
    }
    submitDetails() {
        this.isModalOpen = false;
        this.showToast();
    }
    showToast(){
        const evt = new ShowToastEvent({
            message : SUCCESS_TEXT,
            variant : 'success'
        });
        this.dispatchEvent(evt);
    }
}