import { LightningElement, api } from 'lwc';
import { pubsub } from 'c/pubsubComponent';

export default class DemographicUpdateFinishComponent extends LightningElement {
    @api display = false;
    @api displayData = '';
    @api displayDataDummy = [];
    @api className = '';
    @api isSuccess = false;
    @api showIdCardMsg = false;
    @api idCardMsg;

    connectedCallback() {
        console.log('Demographic Update Finish Component');
        this.subscriptionEngine.apply(this);
    }

    subscriptionEngine() {
        pubsub.subscribe('launchFinishModal', this.renderUI.bind(this));
    }

    renderUI(e) {
        if(!e.detail.initial) {
            this.display = e.detail.display;
        }
        if(e.detail.initial) {
            pubsub.publish('startProcuring');
        }
        if(typeof e.detail.data !== "undefined") {
            // this.displayData = e.detail.data;
            this.displayDataDummy.push({ 
                order: this.displayDataDummy.length, 
                data: e.detail.data, 
                isSuccess: e.detail.success, 
                className:  (e.detail.success) ? 'msg-success' : 'msg-failed'
            });
            if(typeof e.detail.idRequest !== 'undefined') {
                if(e.detail.idRequest && e.detail.success) {
                    if(e.detail.template === 'RSO') {
                        this.showIdCardMsg = e.detail.idRequest;
                        this.idCardMsg = '<div>ID Card will be automatically generated for name changes.</div>' + 
                                         '<div>Remind caller that the physical ID Card is received 5-7 days from the request date. ID Card can also be viewed, printed and emailed from the member\'s MyHumana account within 10 says of the issued date.</div>';
                    }
                    else if(e.detail.template === 'GBO') {
                        this.showIdCardMsg = e.detail.idRequest;
                        this.idCardMsg = '<div>ID Cards will be automatically generated for the name change.</div>' + 
                                         '<div>Remind caller to allow 10-14 calendar days for ID Card to arrive.</div>';
                    }
                }
            }
        }
        if(typeof e.detail.success !== "undefined") {
            if(e.detail.success) {
                this.className = 'msg-success';
                this.isSuccess = true;
            }
            else {
                this.className = 'msg-failed';
                this.isSuccess = false;
            }
        }
    }
}