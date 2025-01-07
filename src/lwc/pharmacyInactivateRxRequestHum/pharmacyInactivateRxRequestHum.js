/*
JS Controller        : pharmacyInactivateRxRequestHum
Version              : 1.0
Created On           : 12/15/2023
Function             : Component to display to pharmacy inactivate rx request flow.

Modification Log: 
* Developer Name                      Date                         Description
* Isaac Chung                        12/13/2023                   Original Version
*------------------------------------------------------------------------------------------------------------------------------
*/
import { LightningElement, wire, track, api } from 'lwc';
import invoiceCaseComment from '@salesforce/label/c.InvoiceCaseComment';
import pharmacyGetOrderMessage from '@salesforce/label/c.PHARMACYGETORDER_DATEMSG';
import unsavedModalMsgHum from '@salesforce/label/c.unsavedModalMsgHum';
import UnsavedChangesHeader from '@salesforce/label/c.UnsavedChangesHeader';
import { ShowToastEvent } from "lightning/platformShowToastEvent";
import { picklistOptions } from './picklistOptions';
import getTemplateNum from '@salesforce/apex/PharmacyInactivateRx_LC_HUM.getInactivateRxTN'
export default class PharmacyInactivateRxRequestHum extends LightningElement {
    @api recordId;
    @api prescriptions;
    @track hasProcess = false;
    @track flowParams;
    @track loaded = false;
    @track didFlowFail = false;
    @track isConfirmingFlowExit = false;
    @track promptclass = "slds-modal__header slds-theme_error slds-theme_alert-texture";
    @track finalPrescriptions;
    @track filterKeyword = '';
    @track filteredPrescriptions = [];
    @track isFirstScreen = true;
    @track isSecondScreen = false;
    @track showReasonWarning = false;
    @track warningMessage = '';
    @track showTooltip = false;
    @track inactivateRxFinalScreen = false;
    @track filterCasesHavingTemplate = true;
    @track inactivateRequestObj = {};
    @track startLoadFlow = false;
    @track summaryData = '';
    @track loadFlowData = [];
    @track processData = [];
    @track rxModal = false;
    @track reasonOptions = picklistOptions;
    @track templateNumber;
    @track buttonConfig = [{
        label: "Cancel",
        class: "slds-var-m-right_small",
        eventname: "cancel",
        variant: "brand-outline"
    }, {
        label: "Continue",
        class: "slds-var-m-right_small",
        eventname: "continue",
        variant: "brand-outline"
    }]

    @wire(getTemplateNum)
    templateNumResult({error, data}) {
        if (data) {
            this.templateNumber = data;
        } else if (error) {
            console.log(error, "error retrieving template number")
        }
    }
    labels = {
        pharmacyGetOrderMessage,
        unsavedModalMsgHum,
        UnsavedChangesHeader,
        invoiceCaseComment
    }

    get flowParamsJSON() {
        if (this.flowParams) {
            return JSON.stringify(this.flowParams);
        }
    }


    
    get isNextDisabled() {
        return !this.finalPrescriptions.some(p => p.inactivate) || this.showReasonWarning
    }

    get tooltipMessage() {
        return this.isNextDisabled ? 'Select at least one item to inactivate' : '';
    }

    get submissionData() {
        const activePrescriptions = this.finalPrescriptions.filter(p => p.inactivate)

        const clinical = activePrescriptions.filter(p => p.reasonCategory === 'clinical')
        const nonclinical = activePrescriptions.filter(p => p.reasonCategory === 'nonclinical')

        return {clinical, nonclinical}
    }



    connectedCallback() {
        this.initialSetUp();
    }

    initialSetUp() {
        this.finalPrescriptions = this.prescriptions.map(p => ({
            ...p,
            requestReason: "",
            reasonCategory: "",
            inactivate: false,
            isDropdownDisabled: true,
            needsReason: false
        }))
        this.filteredPrescriptions = this.finalPrescriptions;
        this.rxModal = true;
        this.loaded = true;
    }

    showError(message) {
        this.dispatchEvent(new ShowToastEvent({
            title: 'Error',
            message: message,
            variat: 'error',
            mode: 'dismissable'
        }))
    }

    handleflowabort() {
        if (this.hasProcess) {
            this.fireEventToParent();
        } else {
            this.toggleHideFlowModalComponents();
            if (!this.didFlowFail) {
                this.isConfirmingFlowExit = true;

            } else {
                this.fireEventToParent();
            }
        }
    }

    fireEventToParent() {
        this.dispatchEvent(new CustomEvent('closeinactiverx'));
    }
    
    
    handleflowfailed(event) {
        this.didFlowFail = true;
    }

    handleCancelFlowExit() {
        this.toggleHideFlowModalComponents();
        this.isConfirmingFlowExit = false;
    }

    handleContinueFlowExit() {
        this.toggleHideFlowModalComponents();
        this.isConfirmingFlowExit = false;
        this.fireEventToParent();
    }

    toggleHideFlowModalComponents() {
        const flowModalComponents = this.template.querySelectorAll(
            ".flow-modal-component"
        );
        flowModalComponents.forEach(function (node) {
            node.classList.toggle("slds-hidden");
        });
    }
    
    handleSearch(event) {
        this.filterKeyword = event.target.value.toLowerCase();
        this.applyFilter();
    }

    applyFilter() {
        this.filteredPrescriptions = this.finalPrescriptions.filter(rx => {
            return rx.dispensedDrug.toLowerCase().includes(this.filterKeyword) || rx.key.toLowerCase().includes(this.filterKeyword) || rx.writtenDrug.toLowerCase().includes(this.filterKeyword)
        })
    }
    
    handleReasonChange(event) {
        const prescId = event.target.dataset.id;
        const selectedValue = event.detail.value
        const selectedOption = this.reasonOptions.find(o => o.value === selectedValue)

        if (selectedOption) {
            const selectedCategory = selectedOption.category
            const presc = this.finalPrescriptions.find(p => p.key === prescId);
            if (presc) {
                presc.requestReason = selectedValue;
                presc.reasonCategory = selectedCategory;
            }
            
        }
        this.updateReasonFlags()
    }
    
    handleCheckboxChange(event) {
        const prescId = event.target.dataset.id;
        const isChecked = event.target.checked;
        
        const checkedCount = this.finalPrescriptions.reduce((count, p) => {
            return count + (p.inactivate ? 1 : 0)
        }, 0)

        if (isChecked && checkedCount >= 10) {
            this.showError('You can only inactivate up to 10 prescriptions')
            event.target.checked = false
            return
        }

        const presc = this.finalPrescriptions.find(p => p.key === prescId);
        if (presc) {
            presc.inactivate = isChecked;
            presc.isDropdownDisabled = !isChecked;
        }
        this.updateReasonFlags()
    }

    updateReasonFlags() {
        this.showReasonWarning = false;
        this.finalPrescriptions.forEach(p => {
            if (p.inactivate && !p.requestReason) {
                const pToUpdate = this.finalPrescriptions.find(pres => p.id === pres.id)
                if (pToUpdate) {
                    pToUpdate.needsReason = true
                    this.showReasonWarning = true;
                }
            } else {
                const pToUpdate = this.finalPrescriptions.find(pres => p.id === pres.id)
                if (pToUpdate) {
                    pToUpdate.needsReason = false;
                }
            }
        })
    }

    handleNextClick() {
        if (!this.showReasonWarning) {
            this.isFirstScreen = false;
            this.isSecondScreen = true;
            this.flowParams = this.submissionData
        }
    }

    handleNavigateBack() {
        this.isFirstScreen = true;
        this.isSecondScreen = false;
    }

    handleSummaryFinish() {

        this.isSecondScreen = false;
        this.isFirstScreen = false;
        this.summaryData = this.formatDataToHTML(this.submissionData);
        this.loadFlowData = [
            {
                name: 'ParamInputSID',
                type: 'String',
                value: '',
            },
            {
                name: "Invoice_Summary_Data",
                type: "String",
                value: this.summaryData,
            },
            {
                name: "TN",
                type: "String",
                value: this.templateNumber,
            }
        ]
        this.startLoadFlow = true;

    }

    handleEncryptFinish(event) {
        let data = event.detail;
        if (data.status === 'FINISHED_SCREEN') {
            if (data && data?.outputVariables) {
                let snode = data.outputVariables.find(k => k.name === 'SID')
                this.processData = [{
                    label: "InactivateRx",
                    value: data.outputVariables.find(k => k.name === 'Invoice_Summary_Data').value,
                    SID: snode.value,
                    processExist: true
                }];

                this.rxModal = false;
                this.inactivateRxFinalScreen = true;
                this.createLoggingData();
            }
        } else if (data.status === 'ERROR') {
            this.isSecondScreen = false;
            this.isFirstScreen = true;
            this.inactivateRxFinalScreen = false;
            this.fireEventToParent();
        }
    }

    createLoggingData() {
        this.inactivateRequestObj = {
            header: 'Inactivate Rx',
            data: this.processData,
            tablayout: true,
            source: 'inactivateRx',
            caseComment: this.formatDataToText(this.submissionData),
            attachProcessToCase: true,
            headertype: 'info',
            redirecttocaseedit: true
        }
    }

    handleFinishLogging() {
        this.isSecondScreen = false;
        this.isFirstScreen = true;
        this.inactivateRxFinalScreen = false;
        this.fireEventToParent();
    }

    formatDataToHTML(data) {
        let htmlSummary = ''

        const formatPrescriptions = (pres) => {
            return `<div>
                        <p><strong>Rx:</strong> ${pres.key}</p>
                        <p><strong>Dispensed Drug:</strong> ${pres.dispensedDrug}</p>
                        <p><strong>Request Reason:</strong> ${pres.requestReason}</p>
                    </div>
                    <br>
            `
        }

        if (data.clinical && data.clinical.length > 0) {
            htmlSummary += '<h2>Clinical Prescriptions</h2>'
            data.clinical.forEach(p => {
                htmlSummary += formatPrescriptions(p)
            })
        }

        
        if (data.nonclinical && data.nonclinical.length > 0) {
            htmlSummary += '<h2>Non Clinical Prescriptions</h2>'
            data.nonclinical.forEach(p => {
                htmlSummary += formatPrescriptions(p)
            })
        }
        return htmlSummary;
    }

    formatDataToText(data) {
        let textSummary = ''

        const formatPrescriptions = (pres) => {
            return `{RxNumber: ${pres.key},  Dispensed Drug: ${pres.dispensedDrug},  Request Reason: ${pres.requestReason}}.\n`
        }

        if (data.clinical && data.clinical.length > 0) {
            textSummary += 'Clinical Prescriptions: \n['
            data.clinical.forEach(p => {
                textSummary += formatPrescriptions(p)
            })
            textSummary += ']\n\n'
        }

        
        if (data.nonclinical && data.nonclinical.length > 0) {
            textSummary += 'Non Clinical Prescriptions: \n['
            data.nonclinical.forEach(p => {
                textSummary += formatPrescriptions(p)
            })
            textSummary += ']\n\n'
        }
        return textSummary;
    }
}