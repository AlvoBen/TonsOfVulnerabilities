import { LightningElement, api, track } from 'lwc';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import getQueueList from '@salesforce/apex/HUMQueueSelection_LC.init'
import saveQ from '@salesforce/apex/HUMQueueSelection_LC.saveQueue'
import Id from '@salesforce/user/Id';
import { updateRecord } from 'lightning/uiRecordApi';

export default class QueueSelectionHum extends LightningElement {
    currentQueue;
    isLoading = true;
    showNoQError = false;
    @track queueList = [];
    @track hasRendered = false;
    @api fieldList = ["Current_Queue__c"]; // current queue field on User record
    showEditField;
    @api userId = Id;

    handleSuccess(event) {
        this.showEditField = false;
    }
    handleEdit() {
        this.showEditField = !this.showEditField;
    }

    connectedCallback() { 
        getQueueList().then((result) => {
            if (result) {
                this.queueList = [];

                if(result.currentQueue ==  undefined ){ // if there is only no currnt Queue for user show error msg
                    this.showNoQError = true;   
                }
                if (result.queueOptions != undefined) {
                    
                    result.queueOptions.forEach(option => {
                        this.queueList.push({ label: option, value: option });
                    });
                }
                this.selQueue = result.selQueue;
                this.currentQueue = result.currentQueue;
            }
            this.isLoading = false;
        })
        .catch((error) => {
            const event = new ShowToastEvent({
                title: "Error",
                variant: "error",
                message: error.body.message
            });
            this.dispatchEvent(event);
        });
    }

    async handleChange(event) {

        this.selQueue = event.target.value;
        this.currentQueue = this.selQueue;

        saveQ({ selQ: this.selQueue }).then((result) => {
            if (result != undefined) {
                if (result == 'Success') {
                   this.showNoQError = false;
                    updateRecord({fields: { Id: this.userId }})
                            .then(() => {
                                
                                const event = new ShowToastEvent({
                                    title: "Success",
                                    variant: "success",
                                    message: 'Queue selection saved successfully'
                                });
                                this.dispatchEvent(event);
                                this.showEditField = false;
                            });
                    
                } else {
                    const event = new ShowToastEvent({
                        title: "Error",
                        variant: "error",
                        message: result
                    });
                    this.dispatchEvent(event);
                }
            }
        }).catch((error) => {
            const event = new ShowToastEvent({
                title: "Error in Saving Queue",
                variant: "error",
                message: error.body.message
            });
            this.dispatchEvent(event);
        })
    }

}