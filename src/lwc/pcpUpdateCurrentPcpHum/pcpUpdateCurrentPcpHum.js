/*
LWC Name        : pcpUpdateCurrentPcpHum.js
Function        : LWC to update PCP change.

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Nirmal Garg                     04/12/2023                initial version US4460894
* Swapnali Sonawane               08/16/2023                US-4938422 PCP Change Template
*****************************************************************************************************************************/
import { api, LightningElement, track } from 'lwc';
import { getFormatDate } from 'c/crmUtilityHum';
import required_msg_HUM from '@salesforce/label/c.required_msg_HUM';
import currentPCPName from '@salesforce/label/c.CurrentPCPName' 
export default class PcpUpdateCurrentPcpHum extends LightningElement {
    @api currentpcpname;
    @api changeReasonOptions;
    @api otherReason;
    @api changeReason = '';
    @api bpcpquestion = false;
    @api bpcpprovider = false;
    @api effectiveDate ;

    @track otherInputEnabled = false;
    labels = {
        required_msg_HUM,
        currentPCPName
    }

    connectedCallback() {
        this.currentpcpname = this.currentpcpname && this.currentpcpname?.length > 0 ? this.currentpcpname:this.labels.CurrentPCPName;
        this.otherInputEnabled = this.changeReason && this.changeReason?.toLocaleLowerCase() === 'other' ? true : false;
    }
    RenderedCallback(){
        this.effectiveDate='';
        this.currentpcpname = this.currentpcpname && this.currentpcpname?.length > 0 ? this.currentpcpname:'This member does not have a current PCP';
    }
    handleReasonChange(event) {
        this.changeReason = event?.detail?.value ?? '';
        this.otherInputEnabled = this.changeReason && this.changeReason?.toLocaleLowerCase() === 'other' ? true : false;
        this.dispatchEvent(new CustomEvent('reasonchange', {
            detail: {
                question: event.target.dataset.question,
                changeReason: this.changeReason
            }
        }));
    }
    handleEffectiveDateChange(event) {
        this.effectiveDate = event.target.value;
        this.dispatchEvent(new CustomEvent('effectivedatechange', {
            detail: {
                question: event?.target?.dataset?.question ?? '',
                effectiveDate: this.effectiveDate
            }
        }));
    }

    get customcss() {
        return this.bpcpprovider === true ? 'slds-col slds-size_3-of-12' : 'slds-col slds-size_4-of-12';
    }

    get customOtherReasonCSS() {
        return this.bpcpprovider === true ? 'slds-col slds-size_3-of-12' : 'slds-col slds-size_1-of-1';
    }

    handleOtherBlur(event) {
        this.dispatchEvent(new CustomEvent('otherreason', {
            detail: {
                question: event?.target?.dataset?.question,
                otherReason: event?.target?.value ?? ''
            }
        }))
    }

    @api checkvalidity() {
        let isValid = true;
        this.bShowErrorMsg = false;
        let inputFields = this.template.querySelectorAll('.validate');
        inputFields.forEach(inputField => {
            if (!inputField.checkValidity()) {
                inputField.reportValidity();
                isValid = false;
            }
        });
        if (this.changeReason && this.changeReason.toLocaleLowerCase() === 'none') {
            isValid = false;
            this.bShowErrorMsg = true;
        }
        return isValid;
    }
}