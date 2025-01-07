import { LightningElement, api } from 'lwc';
import { pubsub } from 'c/pubsubComponent';

export default class DemographicLoadingComponent extends LightningElement {
    @api showLoader = false;

    connectedCallback() {
        try {
            console.log('Demographic Loading Component');
            pubsub.subscribe('toggleLoader', this.renderUI.bind(this));
        }
        catch(error) {
            throw new Error(`${error.message} error at connectedCallback in demographicLoadingComponent.js`);
        }
    }

    renderUI(event) {
        try {
            this.showLoader = event.detail.showLoader;
        }
        catch(error) {
            throw new Error(`${error.message} error at renderUI in demographicLoadingComponent.js`);
        }
    }
}