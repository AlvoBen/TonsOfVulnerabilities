import { LightningElement, api } from 'lwc';
import { pubsub } from 'c/pubsubComponent';

export default class ModalComponent extends LightningElement {
    @api modelInput;
    @api modalData;

    @api isError = false;

    @api displayModal = false;
    @api modalPopUpData;
    currentOrder;
    buttonOrder;

    connectedCallback() {
        try {
            console.log('Modal Component');
            this.modalData = this.modelInput;
            this.isError = (this.modelInput) ? this.modelInput.isError : false;
            this.subscriptionEngine();
        }
        catch(error) {
            throw new Error(`${error.message} error at connectedCallback in modalComponent.js`);
        }
    }

    toggleErrorMode(e) {
        try {
            this.isError = (this.isError) ? false : true;
        }
        catch(error) {
            throw new Error(`${error.message} error at toggleErrorMode in modalComponent.js`);
        }
    }

    triggerInstructions(event) {
        try {
            let eventName = event.target.getAttribute("data-name");
            let dispatchEventName = event.target.getAttribute("data-ename");
            const triggeredEvent = new CustomEvent(dispatchEventName, {
                detail: eventName
            });
            this.dispatchEvent(triggeredEvent);
        }
        catch(error) {
            throw new Error(`${error.message} error at triggerInstructions in modalComponent.js`);
        }
    }

    subscriptionEngine() {
        try {
            pubsub.subscribe('renderUSPSComponent', this.renderUI.bind(this));
        }
        catch(error) {
            throw new Error(`${error.message} error at subscriptionEngine in modalComponent.js`);
        }
    }

    renderUI(event) {
        try {
            this.displayModal = event.detail.displayModal;
            this.modalPopUpData = event.detail.modalPopUpData;
            this.isError = event.detail.isError;
            this.currentOrder = event.detail.currentFieldOrder;
            this.buttonOrder = (event.detail.currentButtonOrder) ? event.detail.currentButtonOrder : undefined;
            pubsub.publish('toggleOverlay', {
                detail: { toggleValue: true }
            });
        }
        catch(error) {
            throw new Error(`${error.message} error at renderUI in modalComponent.js`);
        }
    }

    processBtnClick(e) {
        try {
            let eventName = e.target.dataset.ename;
            let btnOrder = e.target.dataset.order;
            if(eventName.toLowerCase() === 'uspsaccept') {
                pubsub.publish(eventName, {
                    detail: { fieldOrder: this.currentOrder, buttonOrder: (this.buttonOrder) ? this.buttonOrder : btnOrder }
                });
                pubsub.publish('isOverrided', {
                    detail: { isAddressOverrided: false, fieldOrder: this.currentOrder, buttonOrder: (this.buttonOrder) ? this.buttonOrder : btnOrder }
                });
            }
            if(eventName.toLowerCase() === 'uspscancel') {
                pubsub.publish('isAddressBad', {
                    detail: { data: false, isAddressVerified: true }
                });
                pubsub.publish('isOverrided', {
                    detail: { isAddressOverrided: true, fieldOrder: this.currentOrder, buttonOrder: (this.buttonOrder) ? this.buttonOrder : btnOrder }
                });
            }
            this.displayModal = false;
            pubsub.publish('toggleOverlay', {
                detail: { toggleValue: false }
            });
        }
        catch(error) {
            throw new Error(`${error.message} error at processBtnClick in modalComponent.js`);
        }
    }
}