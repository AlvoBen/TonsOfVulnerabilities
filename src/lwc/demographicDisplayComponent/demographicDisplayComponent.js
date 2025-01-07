import { LightningElement, api, track } from 'lwc';
import { pubsub } from 'c/pubsubComponent';

export default class DemographicDisplayComponent extends LightningElement {
    @api accountReadOnlyOutput = [];
    @track display = false;
    @api hideDisplay = false;
    @api rendered = false;

    connectedCallback() {
        try {
            console.log('Demographic Display Component');
            this.subscriptionEngine.apply(this);
        }
        catch(error) {
            throw new Error(`${error.message} error at connectedCallback in demographicDisplayComponent.js`);
        }
    }

    subscriptionEngine() {
        try {
            pubsub.subscribe('renderDisplayComponent', this.renderUI.bind(this));
            pubsub.subscribe('processMainNext', this.toggleUI.bind(this));
        }
        catch(error) {
            throw new Error(`${error.message} error at subscriptionEngine in demographicDisplayComponent.js`);
        }
    }

    renderUI(event) {
        try {
            this.rendered = true;
            this.accountReadOnlyOutput = event.detail.displayData;
        }
        catch(error) {
            throw new Error(`${error.message} error at renderUI in demographicDisplayComponent.js`);
        }
    }

    toggleUI() {
        try {
            this.hideDisplay = !this.hideDisplay;
        }
        catch(error) {
            throw new Error(`${error.message} error at toggleUI in demographicDisplayComponent.js`);
        }
    }
}