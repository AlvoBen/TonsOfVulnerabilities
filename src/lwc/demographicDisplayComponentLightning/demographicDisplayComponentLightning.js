import { LightningElement, api, track } from 'lwc';
import { pubsub } from 'c/pubsubComponent';
import {uConstants} from 'c/updatePlanDemographicConstants'; 

export default class DemographicDisplayComponentLightning extends LightningElement {
    @api accountReadOnlyOutput = [];
    @track display = false;
    @api hideDisplay = false;
    @api rendered = false;
    @track currDemoTitle = uConstants.Current_Demographics;

    connectedCallback() {
        try {
            this.subscriptionEngine.apply(this);
        }
        catch(error) {
            throw new Error(`${error.message} error at connectedCallback in demographicDisplayComponentLightning.js`);
        }
    }

    subscriptionEngine() {
        try {
            pubsub.subscribe('renderDisplayComponent', this.renderUI.bind(this));
            pubsub.subscribe('processMainNext', this.toggleUI.bind(this));
        }
        catch(error) {
            throw new Error(`${error.message} error at subscriptionEngine in demographicDisplayComponentLightning.js`);
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