import { LightningElement, api } from 'lwc';
import { pubsub } from 'c/pubsubComponent';

export default class ErrorDisplayComponent extends LightningElement {
    @api showError = false;
    @api errorMessage = '';

    connectedCallback() {
        try {
            console.log('Error Display Component');
            pubsub.subscribe('showError', this.toggleError.bind(this));
        }
        catch(error) {
            throw new Error(`${error.message} error at connectedCallback in errorDisplayComponent.js`);
        }
        
    }

    toggleError(event) {
        try {
            this.showError = true;
            this.errorMessage = event.detail.errorMessage;
            this.errorTitle = event.detail.errorTitle;
        }
        catch(error) {
            throw new Error(`${error.message} error at toggleError in errorDisplayComponent.js`);
        }
    }

    closeToast() {
        try {
            this.showError = false;
        }
        catch(error) {
            throw new Error(`${error.message} error at closeToast in errorDisplayComponent.js`);
        }
    }
}