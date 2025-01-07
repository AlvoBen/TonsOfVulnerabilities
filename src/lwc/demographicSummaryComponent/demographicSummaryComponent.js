import { LightningElement, api } from 'lwc';
import { buttonList } from './demographicSummaryInitial';
import { pubsub } from 'c/pubsubComponent';
import US1529400_WorkEmail from '@salesforce/label/c.US1529400_WorkEmail';
import US1529400_HomeEmail from '@salesforce/label/c.US1529400_HomeEmail';
import US1529400SwitchLabel from '@salesforce/label/c.US1529400SwitchLabel';
import US1529400_None from '@salesforce/label/c.US1529400_None';

export default class DemographicSummaryPage extends LightningElement {
    @api titleName;
    @api buttonArray = buttonList;
    @api inputArray;
    @api display = false;
    @api flowOrder;
    @api displayButtons;
    @api displayBtnReadMsg = false;
    @api displayBtnMsg = false;
    @api btnMessage;
    @api btnReadMessage;
    @api displaybottom = false;
    @api isEnd = false;
    @api flowMessage;
    @api readMsgClass;

    connectedCallback() {
        try {
            console.log('Demographic Summary Component');
            pubsub.subscribe('launchsummary', this.toggleSummary.bind(this));
            pubsub.subscribe('toggleSummary', this.renderUI.bind(this));
        }
        catch(error) {
            throw new Error(`${error.message} error at connectedCallback in demographicSummaryComponent.js`);
        }
    }

    renderUI(e) {
        let isShow = false;
        if(typeof e.detail.source !== 'undefined') {
            if(e.detail.source === 'avf') {
                this.display = false;
            }
            else {
                this.display = e.detail.display;
            }
            isShow = e.detail.display;
        }
        else {
            this.display = e.detail.display;
            isShow = e.detail.display;
        }
        
        if(isShow) {
            this.displayButtons = false;
            pubsub.publish('toggleOverlay', {
                detail: { toggleValue: true }
            });
            pubsub.publish('toggleLoader', {
                detail: { showLoader: true }
            });
            pubsub.publish('launchFinishModal', {
                detail: { display: true, initial: true, data: undefined }
            });
        }
    }

    triggerInstructions(event) {
        try {
            let btnOrder = parseInt(event.target.getAttribute("data-id"));
            var eventName = ""; var i = 0;
            for(i = 0; i < buttonList.length; i++) {
                if(buttonList[i].order === btnOrder) {
                    eventName = buttonList[i].eventName;
                    break;
                }
            }
            pubsub.publish('triggernextprevious', {
                detail: { eventName: eventName, summaryData: this.inputArray }
            });
            pubsub.publish('emissionFromSummary', {
                detail: { eventName: eventName, order: this.flowOrder }
            });
            // this.display = false;
        }
        catch(error) {
            throw new Error(`${error.message} error at triggerInstructions in demographicSummaryComponent.js`);
        }
    }

    toggleSummary(e) {
        try {
            this.display = true;
            this.displayButtons = true;
            this.inputArray = this.processDateFields.apply(this, [e.detail.data]);
            if(US1529400SwitchLabel.toUpperCase() === 'Y'){
                for(const field of this.inputArray){
                    if(field.label === US1529400_WorkEmail && field.value === ''){
                        field.value = US1529400_None.toUpperCase();
                    }
                    if(field.label === US1529400_HomeEmail && field.value === ''){
                        field.value = US1529400_None.toUpperCase();
                    }
                }
            }
            // this.inputArray = e.detail.data;
            this.titleName = e.detail.title;
            this.flowOrder = (typeof e.detail.order !== "undefined") ? e.detail.order : 0;
            if(e.detail.mode === 'GBO') {
                this.buttonArray[1].label = e.detail.buttonName;
                this.displayBtnMsg = e.detail.msgCriteria.showBtnMessage;
                this.displayBtnReadMsg = e.detail.msgCriteria.showReadMsg;
                this.btnReadMessage = `Changes cannot be made to this request after Submit is clicked.`;
                this.btnMessage = `Click Submit to process the changes or Click Previous to go back and make changes.`;
                this.displaybottom = e.detail.bottom;
                this.readMsgClass = 'btn-msg-read-div';
            }
            if(e.detail.mode === 'RSO') {
                this.buttonArray[1].label = e.detail.buttonName;
                this.displayBtnMsg = e.detail.msgCriteria.showBtnMessage;
                this.displayBtnReadMsg = e.detail.msgCriteria.showReadMsg;
                if(e.detail.isEnd) {
                    this.btnReadMessage = `Changes cannot be made to this request after Submit is clicked.`;
                    this.readMsgClass = 'btn-msg-read-div';
                }
                else {
                    this.btnReadMessage = `The following information will be updated.`;
                    this.readMsgClass = 'btn-msg-read-div-flow';
                }
                this.btnMessage = `Click Submit to process the changes or Click Previous to go back and make changes.`;
                this.displaybottom = e.detail.bottom;
                this.isEnd = e.detail.isEnd;
                this.flowMessage = `Click Next to Complete ${e.detail.nextName}`;
            }
        }
        catch(error) {
            throw new Error(`${error.message} error at toggleSummary in demographicSummaryComponent.js`);
        }
    }

    processDateFields() {
        try {
            let inputDataArray = arguments[0];
            for (const input of inputDataArray) {
                if(input.type === 'date') {
                    let date = new Date(input.value.replace(/-/g, '/').replace(/T.+/, ''));
                    let month = date.getMonth() + 1;
                    let day = date.getDate();
                    let year = date.getFullYear();
                    input.value = `${(month < 10) ? '0' : ''}${month}/${(day < 10) ? 0 : ''}${day}/${year}`;
                }
            }
            return inputDataArray;
        }
        catch(error) {
            throw new Error(`${error.message} error at processDateFields in demographicSummaryComponent.js`);
        }
    }
}