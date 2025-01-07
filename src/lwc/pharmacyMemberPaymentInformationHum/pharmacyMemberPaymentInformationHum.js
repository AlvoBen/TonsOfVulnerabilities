/*
JS Controller        : PharmacyMemberPaymentInformation
Version              : 1.0
Created On           : 11/9/2021
Function             : Component to display to pharmacy member payment information

Modification Log: 
* Developer Name                      Date                         Description
* Swapnali Sonawane                   11/9/2021                   Original Version
* Nirmal Garg                         3/3/2022                    US-3001765-Invoice request flow
* Swapnali Sonawane                   06/15/2022                  Defect-5119 On selecting the cancel button, new flow is starting instead of resuming the Existing Flow 
* Nirmal Garg													07/13/2022								 US-3184447-Optimization of invoice request flow.
* Nirmal Garg                         09/30/2022                   Change for Case Redirect
* Jonathan Dickinson                  06/14/2023                  User Story 4705843: T1PRJ0891339 2023 Arch Remediation-SF-Tech-Filter cases having template attached from existing case history logging for process logging
* Vishal Shinde                        10/10/2023                 User Story 5002422- Mail Order Management; Pharmacy - identify Error Messaging and parameters (Lightning)
*------------------------------------------------------------------------------------------------------------------------------
*/
import { LightningElement, api, track, wire } from 'lwc';
import getPayments from '@salesforce/apexContinuation/PharmacyFinancial_LC_HUM.invokeGetAdjPayService';
import { subscribe, MessageContext } from 'lightning/messageService';
import messageChannel from '@salesforce/messageChannel/pharmacyLMSChannel__c';
import init from '@salesforce/apex/PharmacyInvoiceRequest_LC_HUM.init';
import { ShowToastEvent } from "lightning/platformShowToastEvent";
import { getlayout } from './layoutConfig';
import {invoiceRequestHelper} from './guidedflowhelper';
import pharmacyGetOrderMessage from '@salesforce/label/c.PHARMACYGETORDER_DATEMSG';
import unsavedModalMsgHum from '@salesforce/label/c.unsavedModalMsgHum';
import UnsavedChangesHeader from '@salesforce/label/c.UnsavedChangesHeader';
import invoiceCaseComment from '@salesforce/label/c.InvoiceCaseComment';
import Member_Payment_Information_Error from "@salesforce/label/c.Member_Payment_Information";
const ICON_ARROW_UP = 'utility:arrowup';
const ICON_ARROW_DOWN = 'utility:arrowdown';
const INITIAL_LOAD_PAY = 10;
const MAX_INVOICE_REQUEST = 3;
export default class PharmacyMemberPaymentInformationHum extends LightningElement {
    @api enterpriseId;
    @api networkId;
    @api recordId;
    @api shipAddress;
    @api billAddress
    @api perAddress;
     @api emailaddress;
    @track sStartDate;
    @track sEndDate;
    @track loaded = false;
    @track sMinDate;
    @track sMaxDate;
    @track PaymentDetailsData = [];
    @track isPaymentAvailable;
    @track actualPaymentInfo = [];
    @track memberpaymentlayout = getlayout('memberpayment');
    @track setSIDs = new Set();
    @track firstRequest;
    labels = {
        pharmacyGetOrderMessage,
        unsavedModalMsgHum,
        UnsavedChangesHeader,
        invoiceCaseComment
    }
	@track invoiceProcessData = [];
    subscription = null;
    payment = "true";
    adustment = "true";
    @track totalCount = 0;
    @track startDate;
    @track endDate;
    @track filteredcount = 0;
    @track serviceError= false;
    @track serviceErrorMessage;
    @track errorHeader = Member_Payment_Information_Error;
    

    @wire(MessageContext)
    messageContext;

    @track invoiceRequest = false;
    @track bIssueOccurred = false;
    @track loadGuidedFlow;
    @track hasProcess = false;
    @track processData;
    @track flowParams;
    @track flowname;
    @track bIsFlowFinished = false;
    @track didFlowFail = false;
    @track isConfirmingFlowExit = false;
    @track displayLogging = false;
    buttonConfig = [{
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

   	modalButtonConfig = [{
        text: 'No',
        isTypeBrand: false,
        eventName: 'no'
      }, {
        text: 'Yes',
        isTypeBrand: true,
        eventName: 'yes'
      }]
    promptclass = "slds-modal__header slds-theme_error slds-theme_alert-texture";
    showCaseTemplateMode = 'existingCase';
    viewExistingCasePanel = false;
    calledFromExisting = true;
    invoiceRequestFinalScreen = false;
    @track invoiverequestobj = {};
    filterCasesHavingTemplate = true;
    // Encapsulate logic for Lightning message service subscribe and unsubsubscribe
    subscribeToMessageChannel() {
        if (!this.subscription) {
            this.subscription = subscribe(
                this.messageContext,
                messageChannel,
                (message) => this.handleMessage(message)
            );
        }
    }

    // Handler for message received by component
    handleMessage(message) {
        this.getPaymentDetails();
    }


    connectedCallback() {
        this.subscribeToMessageChannel();
        this.initialDates();
        this.getPaymentDetails();
    }

    initialDates() {
        let todaysdate = new Date();
        this.sMaxDate = todaysdate.toISOString().substring(0, 10);
        let minDate = new Date();
        minDate.setMonth(todaysdate.getMonth() - 18);
        this.sMinDate = minDate.toISOString().substring(0, 10);
        let sDate = new Date();
        sDate.setMonth(todaysdate.getMonth() - 3);
        this.sStartDate = sDate.toISOString().substring(0, 10);
        this.startDate = ((sDate.getMonth() + 1).toString().length == 1 ? '0' + (sDate.getMonth() + 1) : (sDate.getMonth() + 1)) + '/' + (sDate.getDate().toString().length === 1 ? '0' + sDate.getDate() : sDate.getDate()) + '/' + sDate.getFullYear();
        this.endDate = ((todaysdate.getMonth() + 1).toString().length == 1 ? '0' + (todaysdate.getMonth() + 1) : (todaysdate.getMonth() + 1)) + '/' + (todaysdate.getDate().toString().length === 1 ? '0' + todaysdate.getDate() : todaysdate.getDate()) + '/' + todaysdate.getFullYear();
        this.sEndDate = todaysdate.toISOString().substring(0, 10);
    }
    handleDateChange(event) {
        let datedata = event.detail;
        if (datedata.keyname === 'StartDate') {

            if (datedata?.datevalue?.includes('-')) {
                let sDate = datedata.datevalue.split('-');
                if (sDate.length > 0) {
                    this.startDate = sDate[1] + '/' + sDate[2] + '/' + sDate[0];
                }
            } else {
                this.startDate = datedata.datevalue;
            }
        }
        else if (datedata.keyname === 'EndDate') {
            if (datedata?.datevalue?.includes('-')) {
                let eDate = datedata.datevalue.split('-');
                if (eDate.length > 0) {
                    this.endDate = eDate[1] + '/' + eDate[2] + '/' + eDate[0];
                }
            } else {
                this.endDate = datedata.datevalue;
            }

        }
        this.getPaymentDetails();
    }

		handleFinishLogging(){ 
        this.invoiceRequest = false;
        this.firstRequest = false;
        this.invoiceRequestFinalScreen = false;
        this.invoiceProcessData =[];
        this.setSIDs.clear();
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
    }

    toggleHideFlowModalComponents() {
        const flowModalComponents = this.template.querySelectorAll(
            ".flow-modal-component"
        );
        flowModalComponents.forEach(function (node) {
            node.classList.toggle("slds-hidden");
        });
    }

    handleflowfinished(event) {
        let data = event.detail;
        let sidvalue;
        if (data && data?.outputParams) {
            let snode = data.outputParams.find(k => k.name === 'SID')
            if (snode) {
                sidvalue = snode.value;
                if(!this.setSIDs.has(sidvalue)){
                    this.setSIDs.add(sidvalue);
                }
            }
            this.invoiceProcessData = this.invoiceProcessData.filter(k => k.processExist === true);
            let existProcess = this.invoiceProcessData.find(k => k.SID === sidvalue);
            if(existProcess === null || existProcess === undefined){
                this.invoiceProcessData.unshift({
                    label : "Invoice  " + (this.invoiceProcessData.length+1 ),
                    value : data.outputParams.find(k => k.name === 'Invoice_Summary_Data').value,
                    SID : sidvalue,
                    processExist : true
                }); 
            }
            if(this.invoiceProcessData.length === MAX_INVOICE_REQUEST){ 
                this.bIsFlowFinished = false;
                this.invoiceRequest = false;
                this.displayLogging = true;
                this.createLoggingData();
                this.invoiceRequestFinalScreen = true;
            }else{ 
                this.firstRequest = false;
                this.bIsFlowFinished = true;
                this.invoiceRequest = false;
            }
        }
    }
    
    handleYes(){
        this.firstRequest = false;
        this.invoiceRequest = true;
        this.bIsFlowFinished = false;
        this.invoiceProcessData.unshift({
            label : "New Invoice Request",
            value : '',
            SID : '',
            processExist : false
        })
    }

    handleNo(){ 
        this.firstRequest = false;
        this.invoiceRequest = false;
        this.createLoggingData();
        this.invoiceRequestFinalScreen = true;
        this.bIsFlowFinished = false;
        this.displayLogging = true;
    }

    createLoggingData(){
        this.invoiverequestobj = {
            header : 'Invoice Request',
            data : this.invoiceProcessData,
            tablayout : true,
            source : 'invoice',
            caseComment : this.labels.invoiceCaseComment,
            attachProcessToCase : true,
            headertype:'info',
            redirecttocaseedit : true
        }
    }

    handleInvoiceRequestClick() {
        let submissionData;
        this.invoiceRequest = true;
        console.log("shipAddress", this.shipAddress)
        this.getData().then(result => {
            this.processData = JSON.parse(result);
            console.log("processData::", this.processData)
            this.hasProcess =
                this.processData != null &&
                    this.processData.hasOwnProperty("hasProcess") &&
                    this.processData.hasProcess === "true"
                    ? true
                    : false;

            if (this.processData.hasOwnProperty("SubmissionData") && this.processData.SubmissionData != null) submissionData = this.processData.SubmissionData;
            else submissionData = '';
            this.flowParams = new invoiceRequestHelper(this.hasProcess,submissionData,
                this.processData.SubmissionID, this.processData.TN, this.emailaddress, this.billAddress,
                this.perAddress, this.shipAddress);
            this.flowname = "Pharmacy_Invoice_Request_Flow";
            this.firstRequest = true;
        }).catch(error => {
            console.log(typeof error === "object" ? JSON.stringify(error) : error);
            this.bIssueOccurred = true;
            let errorMessage = error && error.body && error.body.message ? error.body.message : error;
            this.displayToastEvent("Error!", "error", errorMessage);
        });
    }

    get flowParamsJSON() {
        return JSON.stringify(this.flowParams.flowParams);
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
        console.log("rec id", this.recordId);
        return new Promise(function (resolve, reject) {
            init({
                caseid: _caseid,
            })
                .then((result) => {
                    resolve(result);
                })
                .catch((error) => {
                    reject(error);
                });
        });
    }

    handleflowabort() {
        if (this.hasProcess) {
            this.invoiceRequest = false;
        } else {
            this.toggleHideFlowModalComponents();
            if (!this.didFlowFail) {
                this.isConfirmingFlowExit = true;
                
            } else {
                this.invoiceRequest = false;
            }
        }
    }
    getPaymentDetails() {        
        this.actualPaymentInfo = [];
        this.loaded = false;
        this.isPaymentAvailable = false;
        getPayments({ sMemID: this.enterpriseId, sStartDate: this.startDate, sEndDate: this.endDate, sPayment: this.payment, sAdjustment: this.adustment, networkID: this.networkId, sRecordId: this.recordId })
            .then(result => {
                if (result) {
                    let data = JSON.parse(result);
                    this.loaded = true;
                    if (data.Payments != null && data.Payments != undefined && data.Payments.Payment != null && data.Payments.Payment != undefined) {
                        this.actualPaymentInfo = data.Payments.Payment;
                        //change the amount format
                        this.actualPaymentInfo.forEach(function (item) {
                            if (item.Amount != '') {
                                item.Amount = Number(item.Amount).toFixed(2);
                            }
                        })
                        this.actualPaymentInfo.sort(function (a, b) {
                            let dateA = new Date(a.PaymentProcessedDate);
                            let dateB = new Date(b.PaymentProcessedDate);
                            return dateA > dateB ? -1 : 1;
                        });
                        this.isPaymentAvailable = this.actualPaymentInfo.length > 0 ? true : false;
                        this.filterdetails();
                    }
                    else {
                        this.isPaymentAvailable = false;
                        this.loaded = true;
                    }
                }
                else {
                    this.isPaymentAvailable = false;
                    this.loaded = true;
                }
            }).catch(error => {
                this.loaded = true;
                this.serviceError= true;
                this.serviceErrorMessage=this.errorHeader;
                console.log('Payment Error :' + JSON.stringify(error));
            })
    }

    filterdetails() {
        if (this.actualPaymentInfo != null) {
            if (this.actualPaymentInfo.length <= INITIAL_LOAD_PAY) {
                this.PaymentDetailsData = this.actualPaymentInfo;
                this.totalCount = this.actualPaymentInfo.length;
                this.filteredcount = this.actualPaymentInfo.length;
            } else {
                this.PaymentDetailsData = this.actualPaymentInfo.slice(0, INITIAL_LOAD_PAY);
                this.totalCount = this.actualPaymentInfo.length;
                this.filteredcount = INITIAL_LOAD_PAY;
            }
        }
    }

    createPaymentList(result) {
        let paymentData;
        this.PaymentDetailsData = [];
        paymentData = result;
        for (let j = 1; j <= paymentData.length; j++) {
            this.PaymentDetailsData.push({
                Id: j - 1,
                Payment: paymentData[j - 1]
            });
        }
    }

    handleScroll(event) {
        if (event.target.scrollHeight - Math.round(event.target.scrollTop) === (event.target.clientHeight)) {
            if ((this.filteredcount + INITIAL_LOAD_PAY) >= this.totalCount) {
                this.filteredcount = this.totalCount;
                this.PaymentDetailsData = this.actualPaymentInfo;
            } else {
                this.filteredcount = this.filteredcount + INITIAL_LOAD_PAY;
                this.PaymentDetailsData = this.actualPaymentInfo.slice(0, this.filteredcount);
            }

        }
    }

    sortBy(field, reverse, primer) {
        const key = primer
            ? function (x) {
                return primer(x[field]);
            }
            : function (x) {
                return x[field];
            };

        if (field == "PaymentScheduledDate" || field == "PaymentProcessedDate") {
            return function (a, b) {
                a = new Date(key(a));
                b = new Date(key(b));
                return reverse * ((a > b) - (b > a));
            };
        }
        else if (field == "Amount") {
            return function (a, b) {
                a = key(a);
                b = key(b);
                return reverse * (a - b);
            };
        }
        else {
            return function (a, b) {
                a = key(a);
                b = key(b);
                return reverse * ((a > b) - (b > a));
            };
        }

    }
    handleMouseEnter(event) {
        let header = event.target.dataset.label;
        this.memberpaymentlayout.forEach(element => {
            if (element.label === header) {
                element.mousehover = true,
                    element.mousehovericon = event.target.dataset.iconname === ICON_ARROW_DOWN ? ICON_ARROW_UP : ICON_ARROW_DOWN;
            }
        });
    }

    handleMouseLeave(event) {
        let header = event.target.dataset.label;
        this.memberpaymentlayout.forEach(element => {
            if (element.label === header) {
                element.mousehover = false
            }
        });
    }

    onHandleSort(event) {
        event.preventDefault();
        let header = event.currentTarget.dataset.label;
        let sortedBy = event.currentTarget.getAttribute('data-id');
        let sortDirection = event.currentTarget.dataset.iconname === 'utility:arrowdown' ? 'asc' : 'desc';
        this.memberpaymentlayout.forEach(element => {
            if (element.label === header) {
                element.mousehover = false;
                element.sorting = true;
                element.iconname = element.iconname === 'utility:arrowdown' ? 'utility:arrowup' : 'utility:arrowdown';
            } else {
                element.mousehover = false;
                element.sorting = false;
            }
        });
        const cloneData = [...this.actualPaymentInfo];

        cloneData.sort(this.sortBy(sortedBy, sortDirection === 'asc' ? 1 : -1));
        this.actualPaymentInfo = cloneData;

        this.filteredcount = 0;
        this.totalCount = this.actualPaymentInfo != null ? this.actualPaymentInfo.length : 0;
        this.filterdetails();
    }
    
    handleExistingCaseToggle(){
        this.viewExistingCasePanel = false;
        this.invoiceRequest = true;
    }

    handleNewCaseToggle(){
        this.invoiceRequest = false;
        this.viewExistingCasePanel = true;        
    }
}