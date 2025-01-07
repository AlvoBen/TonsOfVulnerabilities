/*
JS Controller        : pharmacyInvoiceRequestHum
Version              : 1.0
Created On           : 09/06/2023
Function             : Component to display to pharmacy invoice request flow.

Modification Log: 
* Developer Name                      Date                         Description
* Nirmal Garg                         09/06/2023                   Original Version
*------------------------------------------------------------------------------------------------------------------------------
*/
import { LightningElement, wire, track, api } from 'lwc';
import init from '@salesforce/apex/PharmacyInvoiceRequest_LC_HUM.init';
import { ShowToastEvent } from "lightning/platformShowToastEvent";
import { invoiceRequestHelper } from './invoiceRequestHelper';
import invoiceCaseComment from '@salesforce/label/c.InvoiceCaseComment';
import pharmacyGetOrderMessage from '@salesforce/label/c.PHARMACYGETORDER_DATEMSG';
import unsavedModalMsgHum from '@salesforce/label/c.unsavedModalMsgHum';
import UnsavedChangesHeader from '@salesforce/label/c.UnsavedChangesHeader';
const MAX_INVOICE_REQUEST = 3;
export default class PharmacyInvoiceRequestHum extends LightningElement {
    @api demographicsDetails;
    @api invoiceDetails;
    @api recordId;
    @api profileDetails;
    @track invoiceRequest = false;
    @track hasProcess = false;
    @track processData;
    @track flowParams;
    @track flowname;
    @track bIssueOccurred = false;
    @track loaded = false;
    @track setSIDs = new Set();
    @track invoiceProcessData = [];
    @track invoiceRequestFinalScreen = false;
    @track bIsFlowFinished = false;
    @track didFlowFail = false;
    @track isConfirmingFlowExit = false;
    @track displayLogging = false;
    @track modalButtonConfig = [{
        text: 'No',
        isTypeBrand: false,
        eventName: 'no'
    }, {
        text: 'Yes',
        isTypeBrand: true,
        eventName: 'yes'
    }]
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

    @track promptclass = "slds-modal__header slds-theme_error slds-theme_alert-texture";
    @track showCaseTemplateMode = 'existingCase';
    @track viewExistingCasePanel = false;
    @track calledFromExisting = true;
    @track invoiverequestobj = {};
    @track filterCasesHavingTemplate = true;

    labels = {
        pharmacyGetOrderMessage,
        unsavedModalMsgHum,
        UnsavedChangesHeader,
        invoiceCaseComment
    }

    get flowParamsJSON() {
        if (this.flowParams && this.flowParams?.flowParams) {
            return JSON.stringify(this.flowParams.flowParams);
        }
    }

    connectedCallback() {
        this.initialSetUp();
        this.flowname = "Pharmacy_Invoice_Request_Flow";
        this.firstRequest = true;
        this.invoiceRequest = true;
    }

    initialSetUp() {
        let submissionData;
        this.getData().then(result => {
            this.processData = JSON.parse(result);
            this.hasProcess = this.processData && this.processData?.hasProcess
                && this.processData?.hasProcess === 'true' ? true : false;
            submissionData = this.processData?.SubmissionData ?? '';
            this.flowParams = new invoiceRequestHelper(this.hasProcess, submissionData,
                this.processData.SubmissionID, this.processData.TN, this.profileDetails?.Email ?? '', this.getAddress('BILLING'),
                this.getAddress('HOME'), this.getAddress("SHIPPING"));
            this.flowname = "Pharmacy_Invoice_Request_Flow";
            this.firstRequest = true;
            this.loaded = true;
        }).catch(error => {
            console.log(typeof error === "object" ? JSON.stringify(error) : error);
            this.bIssueOccurred = true;
            let errorMessage = error && error.body && error.body.message ? error.body.message : error;
            this.displayToastEvent("Error!", "error", errorMessage);
            console.log("Error Invoice request:", errorMessage);
            this.loaded = true;
        });
    }

    getAddress(type) {
        let address = this.demographicsDetails && this.demographicsDetails?.Addresses
            && Array.isArray(this.demographicsDetails?.Addresses) && this.demographicsDetails?.Addresses?.length > 0
            ? this.demographicsDetails?.Addresses?.find(k => k?.z0type?.description?.toUpperCase() === type?.toUpperCase()) : '';
        return address && Object.keys(address)?.length > 0 ? `${address?.addressLine1 ?? ''} ${address?.addressLine2 ?? ''}, ${address?.city ?? ''}, ${address?.stateCode ?? ''} ${address?.zipCode ?? ''}` : '';
    }

    displayToastEvent(message, variant, title) {
        this.dispatchEvent(
            new ShowToastEvent({
                title: title,
                message: message,
                variant: variant,
                mode: "dismissable",
            })
        );
    }

    getData() {
        let _caseid = this.recordId;
        return new Promise(function (resolve, reject) {
            init({
                caseid: _caseid,
            }).then((result) => {
                resolve(result);
            }).catch((error) => {
                reject(error);
            });
        });
    }

    handleflowabort() {
        if (this.hasProcess) {
            this.invoiceRequest = false;
            this.fireEventToParent();
        } else {
            this.toggleHideFlowModalComponents();
            if (!this.didFlowFail) {
                this.isConfirmingFlowExit = true;

            } else {
                this.invoiceRequest = false;
                this.fireEventToParent();
            }
        }
    }

    fireEventToParent() {
        this.dispatchEvent(new CustomEvent('closeinvoice'));
    }

    handleExistingCaseToggle() {
        this.viewExistingCasePanel = false;
        this.invoiceRequest = true;
    }

    handleNewCaseToggle() {
        this.invoiceRequest = false;
        this.viewExistingCasePanel = true;
    }

    handleYes() {
        this.firstRequest = false;
        this.invoiceRequest = true;
        this.bIsFlowFinished = false;
        this.invoiceProcessData.unshift({
            label: "New Invoice Request",
            value: '',
            SID: '',
            processExist: false
        })
    }

    handleNo() {
        this.firstRequest = false;
        this.invoiceRequest = false;
        this.createLoggingData();
        this.invoiceRequestFinalScreen = true;
        this.bIsFlowFinished = false;
        this.displayLogging = true;
    }

    createLoggingData() {
        this.invoiverequestobj = {
            header: 'Invoice Request',
            data: this.invoiceProcessData,
            tablayout: true,
            source: 'invoice',
            caseComment: this.labels.invoiceCaseComment,
            attachProcessToCase: true,
            headertype: 'info',
            redirecttocaseedit: true
        }
    }

    handleflowfinished(event) {
        let data = event.detail;
        let sidvalue;
        if (data && data?.outputParams) {
            let snode = data.outputParams.find(k => k.name === 'SID')
            if (snode) {
                sidvalue = snode.value;
                if (!this.setSIDs?.has(sidvalue)) {
                    this.setSIDs.add(sidvalue);
                }
            }
            this.invoiceProcessData = this.invoiceProcessData.filter(k => k.processExist === true);
            let existProcess = this.invoiceProcessData.find(k => k.SID === sidvalue);
            if (existProcess === null || existProcess === undefined) {
                this.invoiceProcessData.unshift({
                    label: "Invoice  " + (this.invoiceProcessData?.length + 1),
                    value: data.outputParams.find(k => k.name === 'Invoice_Summary_Data').value,
                    SID: sidvalue,
                    processExist: true
                });
            }
            if (this.invoiceProcessData?.length === MAX_INVOICE_REQUEST) {
                this.bIsFlowFinished = false;
                this.invoiceRequest = false;
                this.displayLogging = true;
                this.createLoggingData();
                this.invoiceRequestFinalScreen = true;
            } else {
                this.firstRequest = false;
                this.bIsFlowFinished = true;
                this.invoiceRequest = false;
            }
        }
    }

    handleflowfailed(event) {
        this.didFlowFail = true;
    }

    handleCancelFlowExit() {
        this.toggleHideFlowModalComponents();
        this.invoiceRequest = true;
        this.isConfirmingFlowExit = false;
    }

    handleContinueFlowExit() {
        this.toggleHideFlowModalComponents();
        this.invoiceRequest = false;
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

    handleFinishLogging() {
        this.invoiceRequest = false;
        this.firstRequest = false;
        this.invoiceRequestFinalScreen = false;
        this.invoiceProcessData = [];
        this.setSIDs.clear();
        this.fireEventToParent();
    }
}