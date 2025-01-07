import { LightningElement, api } from 'lwc';
import { pubsub } from 'c/pubsubComponent';

export default class DemographicMessagesComponent extends LightningElement {
    @api message = '';
    @api displayMessage = false;

    connectedCallback() {
        try {
            console.log('Demographic Messages Component');
            // pubsub.subscribe('launchMessages', this.renderUI.bind(this));
            pubsub.subscribe('toggleMessages', this.renderUI.bind(this));
        }
        catch(error) {
            throw new Error(`${error.message} error at connectedCallback in demographicSummaryComponent.js`);
        }
    }

    renderUI(e) {
        this.message = e.detail.message;
        this.displayMessage = e.detail.display;
    }

    unRenderUI(e) {
        this.message = e.detail.message;
        this.displayMessage = e.detail.display;
    }
}