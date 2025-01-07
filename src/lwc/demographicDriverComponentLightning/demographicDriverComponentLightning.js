import { LightningElement, api, track } from 'lwc';
import { pubsub } from 'c/pubsubComponent';
import { optionToggler, flowListenerGenerator, flowListenerProcessor, emitToggleIndex, deriveFlowStatus } from './demographicDriverHelperLightning';
import US1441116SwitchLabel from '@salesforce/label/c.US1441116_Switch_Label';
import US1441116_RSO from '@salesforce/label/c.US1441116_RSO';
import {uConstants} from 'c/updatePlanDemographicConstants'; 

export default class DemographicDriverComponentLightning extends LightningElement {
    @api demographicUpdateOption = [];
    @api displayNote = false;
    @api sequence = false;
    @api hideDriver = false;
    @api showHeader = false; 
    flowListener = [];
    errorTrace = {
        stack: undefined
    };
    //US1441116 
    @api toggleUS1441116 = false;
    currentTemplate = ''; 
    @track chooseDemoMsg = uConstants.Choose_Demo_UpdateMsg; 
    @track medicareMsg = uConstants.MedicareAdd_Msg; 

    connectedCallback() {
        try {
            this.subscriptionEngine();
        }
        catch(error) {
            throw new Error(`${(this.errorTrace.stack) ? (this.errorTrace.stack + ' ' + error.message) : error.message }`);
        }
    }

    subscriptionEngine() {
        try {
            pubsub.subscribe('renderDriverComponent', this.renderUI.bind(this));
            pubsub.subscribe('processMainNext', this.toggleUI.bind(this));
            pubsub.subscribe('emissionFromSummary', this.processSummaryAftermath.bind(this));
        }
        catch(error) {
            throw new Error(`${(this.errorTrace.stack) ? (this.errorTrace.stack + ' ' + error.message) : error.message }`);
        }
    }

    renderUI(event) {
        try {
            this.showHeader = true;
            this.demographicUpdateOption = event.detail.displayData;
            this.displayNote = event.detail.displayNote;
            this.sequence = event.detail.sequence;
            //US1441116 Start - Checking the Template Name
            this.currentTemplate = event.detail.templateName;
            this.toggleUS1441116 = US1441116SwitchLabel === 'Y' ? true : false;
            //US1441116 End
            flowListenerGenerator.apply(this);
        }
        catch(error) {
            throw new Error(`${(this.errorTrace.stack) ? (this.errorTrace.stack + ' ' + error.message) : error.message }`);
        }
    }

    toggleUpdateMode(e) {
        try {
            optionToggler.apply(this, [e]);
            flowListenerProcessor.apply(this, [e]);
            emitToggleIndex.apply(this);
            pubsub.publish('emissionFromDriver', {
                detail: { data: this.flowListener }
            });
        }
        catch(error) {
            throw new Error(`${(this.errorTrace.stack) ? (this.errorTrace.stack + ' ' + error.message) : error.message }`);
        }
    }

    toggleUI() {
        try {
            this.hideDriver = !this.hideDriver;
        }
        catch(error) {
            throw new Error(`${(this.errorTrace.stack) ? (this.errorTrace.stack + ' ' + error.message) : error.message }`);
        }
    }

    processSummaryAftermath(event) {
        try {
            let eventName = event.detail.eventName;
            let order = event.detail.order;
            let isInProgress = false;
            switch(eventName.toLowerCase()) {
                case "finishevent":
                    for (const option of this.demographicUpdateOption) {
                        if(option.order === +order) {
                            option.status = 'Complete';
                            option.disabled = true;
                            pubsub.publish('initiateUpdateModel', {
                                detail: { visibility: option.order }
                            });
                            break;
                        }
                    }
                    deriveFlowStatus.apply(this, [order]);
                    for (const flow of this.flowListener) {
                        if(flow.status === 'In Progress') {
                            isInProgress = true;
                            break;
                        }
                    }
                    if(isInProgress) {
                        pubsub.publish('toggleSummary', {
                            detail: { display: false }
                        });
                        pubsub.publish('emissionDuringFlow');
                        emitToggleIndex.apply(this);
                    }
                    else {
                        pubsub.publish('toggleSummary', {
                            detail: { display: true }
                        });
                    }
                    pubsub.publish('emissionFromDriver', {
                        detail: { data: this.flowListener }
                    });
                    break;
                case "previousevent":
                    pubsub.publish('toggleSummary', {
                        detail: { display: false }
                    });
                    pubsub.publish('emissionDuringFlow');
                    break;
                default:
                    break;
            }
        }
        catch(error) {
            throw new Error(`${(this.errorTrace.stack) ? (this.errorTrace.stack + ' ' + error.message) : error.message }`);
        }
    }
}