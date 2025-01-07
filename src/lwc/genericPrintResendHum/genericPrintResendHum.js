/*
LWC Name        : rtiResendHum.html
Function        : LWC to display send Print Screen

Modification Log:
* Developer Name                  Date                         Description
* Swapnali Sonawane               07/12/2022                   US-3406771 Lightning- RTI - Resend functionality
* Swapnali Sonawane               08/04/2022                   Defect-5517  Verify and Resend functionality
* Sagar Gulleve                   30/09/2022                   US-3676169 MF 3215127- Service - Claims System Integration Claims--Enabling Member Claim Statements Tab Capabilities Member Claim Statements- Send- FAX
* Sagar Gulleve                   30/09/2022                   US-3684888 MF 3215128 - Service - Claims System Integration-Enabling Provider Claim Statements Tab Capabilities: Provider Claims Statements- Fax Send
* Abhishek Mangutkar              10/12/2022                   US-3837985 RTI - attach to case upon resend  functionality
* Aishwarya Pawar                 02/01/2023                   US-4184606 DF - 6919 - Regression - RTI Panel - Select Print Item Issue
* Apurva Urkude			          03/06/2023		           US-4302431 Case Management: Systematically associate interaction to existing case during Process Logging: RTI - Resending Print Letters - Existing Case
* Sagar Gulleve			          03/06/2023                   US-4302417  Case Management - Systematically associate interaction to existing case during Process Logging: Resending Claim Statements (Surge
* Apurva Urkude                   07/28/2023                   US-4839641  INC2402219- Go Live Incident resolve Not seeing an address when printing RTI Communication RAID #
* Anuradha Gajbhe                 10/25/2023                   US: 5211327- TECH - Regression DF 8177 - The View link is not working from process section of the Member and provider claim statements attached to case
* Kiran Bhuvanagiri               12/08/2023                   INC2704731 Provider Claim Statements Fix
* Anuradha Gajbhe		          03/01/2024		           US-5480525: DF- 8386: Address is not updating on Resend for Provider/Member Claim Statements on Case Pop-up.
*************************************************************************************************************************** */
import { LightningElement, track, api, wire } from 'lwc';
import RTI_InfoMessage from '@salesforce/label/c.RTI_InfoMessage';
import { getStateList } from './USStateList';
import getAccDetails from '@salesforce/apexContinuation/Claim_Send_Statement_LC_HUM.getAccountDetails';
import verifyAddress from '@salesforce/apexContinuation/Claim_Send_Statement_LC_HUM.initiateRequest';
import sendStatement from '@salesforce/apexContinuation/Claim_Send_Statement_LC_HUM.initiateSendStmtRequest';
import getDelMethod from '@salesforce/apexContinuation/Claim_Send_Statement_LC_HUM.getRTIDeliveryMethod';
import createCaseInteraction from '@salesforce/apex/Claim_Send_Statement_LC_HUM.createCaseInteraction';
import updateInteractionOnCase from '@salesforce/apex/Claim_Send_Statement_LC_HUM.updateInteractionOnCase';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import CASE_NOTE from "@salesforce/label/c.Case_New_Existing_Note";
import SEND_PRINT_ITEM_ERROR_MSG from "@salesforce/label/c.SEND_PRINT_ITEM_ERROR_MSG";
import SEND_PRINT_ITEM_SUCCESS_RESPONSE from "@salesforce/label/c.SEND_PRINT_ITEM_SUCCESS_RESPONSE";
import SEND_CLAIM_ITEM_ERROR_MSG from '@salesforce/label/c.SEND_CLAIM_ITEM_ERROR_MSG';
import SEND_CLAIM_ITEM_SUCCESS_RESPONSE from '@salesforce/label/c.SEND_CLAIM_ITEM_SUCCESS_RESPONSE';
import createNewCase from '@salesforce/apex/Logging_LC_HUM.createNewCase';
import { openLWCSubtab, invokeWorkspaceAPI } from 'c/workSpaceUtilityComponentHum';
import { CurrentPageReference, NavigationMixin } from 'lightning/navigation';
import { generateTemplateData, updateTemplateSubmissionData } from 'c/genericTemplateDataCreationHum';
const APPLICATION_KEY = '238F6F83-B8B4-11CF-8771-00A024541EE3';
const TEMPLATE_MEMBER = 'Send Print Item: Member';
const TEMPLATE_PROVIDER = 'Send Claim Statement: Provider';
const TEMPLATE_CLAIM_MEMBER = 'Send Claim Statement: Member';
export default class GenericPrintResendHum extends LightningElement {
    @api recId;
    @api memberplanid;
    @api item;
    @api viewlink;
    @api prvdaddr;
    
    @track bAddress1Edit = false;
    @track bCityEdit = false;
    @track bAddress2Edit = false;
    @track bStateEdit = false;
    @track bZipEdit = false;
    @track bRecipientTypeEdit = false;
    loaded = true;
    @track recipientType = [];
    @track AcctData = {};
    @track AcctResponse;
    @track isAddressVerified = true;
    @track isSendPrint = false;
    @track rtiDataToSend = {};
    @track delMethod = [];
    ismemberproviderstatement;
    @api ismemberstatement = false;
    @api isproviderstatement = false;
    @api claimNbr;
    labels = {
        RTI_InfoMessage,
        SEND_PRINT_ITEM_ERROR_MSG,
        SEND_PRINT_ITEM_SUCCESS_RESPONSE,
        SEND_CLAIM_ITEM_ERROR_MSG,
        SEND_CLAIM_ITEM_SUCCESS_RESPONSE
    }
    @track recipientName;
    get stateOptions() {
        return getStateList();
    }

    @api
    resetData(){
        this.recipientName = '';
    }

    @track newCaseId;
	@track existingCaseId;
    @track caseId;
    value = '';
    renderMailUi = true;
    renderFaxUi = false;

    attachCaseNote = CASE_NOTE;
    isExistingCase = false;
    calledFromExistingCase;
    recipientNameToSend;
    @track tempSubmissionOwnerId = '';
    @track tempSubmissionId = '';
    @track tempSubmissionData = [];
    @track processIds = [];
    @track templateFields = [];
    @track templateName;
	@track addressLine2 = '';
	@track bToggleCheck = true;
	@track sDocumentKeyProvider;
	@track sDocumentKeyMember;
	
    @track pageRef;
    @track pageState;
    @track stateValue;
    @track interactionId;

    @wire(CurrentPageReference)
    wiredPageRef(pageRef) {
        this.pageRef = pageRef;
        this.pageState = this.pageRef?.state ?? null;        
        this.getInteractionId();
    }

    getInteractionId() {
		let interactionId = '';
        if (this.pageState && typeof (this.pageState) === 'object') {
            if (this.pageState.hasOwnProperty('ws')) {                
                this.stateValue = this.pageState && this.pageState.hasOwnProperty('ws') ? this.pageState['ws'] : null;
                let tempvalues = this.stateValue && this.stateValue.includes('c__interactionId') ? this.stateValue.split('c__interactionId') : null;
                if (tempvalues && Array.isArray(tempvalues) && tempvalues?.length >= 2) {
                    interactionId = tempvalues[1]?.substring(1,19) ?? null;
                    this.interactionId=interactionId;
                }
                if(tempvalues==null){
                    tempvalues = this.stateValue && this.stateValue.includes('Interaction__c') ? this.stateValue.split('Interaction__c') : null;
                    if (tempvalues && Array.isArray(tempvalues) && tempvalues?.length >= 2) {
                        interactionId = tempvalues[1]?.substring(1,19) ?? null;
                        this.interactionId=interactionId;
                    }
                }
            }
            else if (this.pageState.hasOwnProperty('c__interactionId')) {
                interactionId = this.pageState['c__interactionId'];
                this.interactionId=interactionId;

            }
        }
    }
    get customcss(){
      if(this.ismemberstatement || this.isproviderstatement){
        return 'slds-popover slds-popover_large slds-popover_prompt slds-popover_prompt_top-right claimpopoversection';
      }else{
        return 'slds-popover slds-popover_large slds-popover_prompt slds-popover_prompt_top-right popoverSection';
      }
    }
    connectedCallback() {

        this.getAcctDetailsData(this.recId).then(result => {
            if (result != null) {
                this.AcctResponse = result;
                this.loadInitialData();
            }
        }).catch(error => {
            this.loaded = true;
            console.log("Error occured in getAcctDetailsData- " + JSON.stringify(error));
        })

        this.getDeliveryMethods().then(result => {
            if (result != null) {
                let delMethodData = result;
                delMethodData.forEach(option => {
                    if (option.Method_Name__c.toUpperCase() == 'FAX' && (this.ismemberstatement || this.isproviderstatement)) {
                        this.delMethod.push({
                            label: option.Method_Name__c,
                            value: option.Method_Name_Value__c
                        });
                    }
                    else if (option.Method_Name__c.toUpperCase() == 'MAIL') {
                        this.delMethod.push({
                            label: option.Method_Name__c,
                            value: option.Method_Name_Value__c
                        });

                    }
                    this.value = this.delMethod[0].value;

                });
            }
        });

    }

    renderTemplate(event) {
        let selectedRadioVal = event.detail.value;
        if (selectedRadioVal && selectedRadioVal.toUpperCase() == 'MAIL') {
            this.renderMailUi = true;
            this.renderFaxUi = false;
        } else if (selectedRadioVal && selectedRadioVal.toUpperCase() == 'FAX') {
            this.renderMailUi = false;
            this.renderFaxUi = true;
        }
        this.value = selectedRadioVal;
    }

    createRtiDatatoPrint() {
        this.rtiDataToSend.CatDesc = this.item?.title ?? '';
        this.rtiDataToSend.MsgName = this.item?.sMessageName ?? '';
        this.rtiDataToSend.rtiPageId = this.recId;
        this.rtiDataToSend.SourceCode = this.item?.sSourceCode ?? '';
        this.rtiDataToSend.fulfilId = this.item?.sMessageFulfillmentId ?? '';
        let dateTime = new Date(this.item?.dDateAndTime);
        let date = dateTime.toLocaleDateString('en-US');
        this.rtiDataToSend.DateSent = date;
        this.rtiDataToSend.Viewlink = this.viewlink;
        //new addeed
        this.rtiDataToSend.StatementTypeSerValue = 'rtiMember';
        this.rtiDataToSend.selectedValue = this.value;
        this.rtiDataToSend.sStatementType = 'rtiMember';

    }

    createMemberProviderDatatoPrint() {
        this.rtiDataToSend.DocKey = this.item?.sDocumentKey ?? '';
        this.rtiDataToSend.StatementTypeSerValue = this.item?.sStatementType ?? '';
        this.rtiDataToSend.sPolMemID = this.memberplanid;
        this.rtiDataToSend.selectedValue = this.value;
        this.rtiDataToSend.ClaimNumber = this.claimNbr;
        if (this.ismemberstatement == true) {
	    this.rtiDataToSend.sStatementType = 'Member';
            this.rtiDataToSend.StatementPeriod = this.item?.sStatementBeginDate ? this.item.sStatementBeginDate : '';
            this.rtiDataToSend.EndStatementPeriod = this.item?.sStatementEndDate ? this.item.sStatementEndDate : '';
            this.rtiDataToSend.RemitId = '';
        } else if (this.isproviderstatement == true) {
    	    this.rtiDataToSend.sStatementType = 'Provider';
            this.rtiDataToSend.RemitId = this.item?.sRemitId ?? '';
            this.rtiDataToSend.StatementPeriod = '';
            this.rtiDataToSend.EndStatementPeriod = '';
        }
    }
	
	
    handleAddressVerification() {
        this.verifyAddressProcess(false);
    }

    initiateAddVerification(accData) {
        return new Promise((resolve, reject) => {
            verifyAddress({ sRefAddressLine1: accData.AddressLine1, sRefAddressLine2: accData.AddressLine2, sRefCity: accData.City, sRefStatecode: accData.StateCode, sRefZipcode: accData.ZipCode })
                .then(result => {
                    resolve(result);
                }).catch(error => {
                    reject(error);
                })
        })
    }

    sendPrintStatement(accData, rtiData) {
        return new Promise((resolve, reject) => {
            sendStatement({ sAddressData: accData, sRtiData: rtiData })
                .then(result => {
                    resolve(result);
                }).catch(error => {
                    reject(error);
                })
        })
    }


    handleResendClick() {
        this.resendProcess(false);
    }

    showToastNotification(sTitle, sMessage, sVariant, sMode) {
        const evt = new ShowToastEvent({
            title: sTitle,
            message: sMessage,
            variant: sVariant,
            mode: sMode
        });
        this.dispatchEvent(evt);
    }

    getAcctDetailsData(recordId) {
        return new Promise((resolve, reject) => {
            getAccDetails({ sRecId: recordId })
                .then(result => {
                    resolve(result);
                }).catch(error => {
                    reject(error);
                })
        })
    }

    getDeliveryMethods() {
        return new Promise((resolve, reject) => {
            getDelMethod()
                .then(result => {
                    resolve(result);
                }).catch(error => {
                    reject(error);
                })
        })
    }



    CancelClick() {
        this.loadInitialData();
        const selectedEvent = new CustomEvent("cancelclickselect");
        this.dispatchEvent(selectedEvent);

    }


    handleFaxNumber(event) {

        var faxNumber = event.target.value;
        if (faxNumber.length <= 12) {
            var faxNumberSplit = faxNumber.split('-');
            for (var a = 0; a < faxNumberSplit.length; a++) {
                if (faxNumberSplit[a] != +faxNumberSplit[a]) {
                    faxNumberSplit[a] = faxNumberSplit[a].substr(0, faxNumberSplit[a].length - 1);
                }
            }
            faxNumber = faxNumberSplit.join('-');
            if (faxNumber.length == 4 && faxNumber[3] != '-')
                faxNumber = faxNumber.substring(0, 3) + "-" + faxNumber.substring(3);
            else if (faxNumber.length == 8 && faxNumber[7] != '-')
                faxNumber = faxNumber.substring(0, 7) + "-" + faxNumber.substring(7);
            else if (faxNumber.length >= 10) {
                faxNumber = faxNumber.split('-').join('');
                faxNumber = faxNumber.substring(0, 3) + "-" + faxNumber.substring(3, 6) + "-" + faxNumber.substring(6);
            } else { }
            this.AcctData.FaxNo = faxNumber;
			this.rtiDataToSend.FaxNo = faxNumber;
        }
    }

    handleAddress1(event) {
        this.AcctData.AddressLine1 = event.target.value;
        this.bAddress1Edit = true;
    }

    handlecity(event) {
        let inputVal = event.target.value;
        event.target.value = inputVal.replace(/[0-9]/g, "");
        this.AcctData.City = event.target.value;
        this.bCityEdit = true;
    }

    handleAddress2(event) {
        this.AcctData.AddressLine2 = event.target.value;
        this.bAddress2Edit = true;
        if(this.AcctData.AddressLine2!=null || this.AcctData.AddressLine2!=undefined || !this.AcctData.AddressLine2.isEmpty()){
            this.addressLine2 = event.target.value;
        }
        else{ 
            this.addressLine2 ="";
        }
    }

    handlestate(event) {
        this.AcctData.StateCode = event.target.value;
        this.bStateEdit = true;
    }

    handlezip(event) {
        let inputVal = event.target.value;
        event.target.value = inputVal.replace(/[^[0-9]/g, "");
        this.AcctData.ZipCode = event.target.value;
        this.bZipEdit = true;
    }

    handleRecName(event) {
    	this.recipientName = event.target.value;
        this.AcctData.RecipientName = event.target.value;
        this.recipientNameToSend = event.target.value;
    }
    handleRecType(event) {
        this.rtiDataToSend.recipientType = event.target.value;
        this.bRecipientTypeEdit = true;
    }

    handleNo() {

        if (this.isproviderstatement == true){
            this.recipientName = '';
            this.AcctData.AddressLine1 = (this.prvdaddr?.sRefAddressLine1 != null && this.prvdaddr?.sRefAddressLine1 != undefined) ? this.prvdaddr?.sRefAddressLine1 : '';
            this.AcctData.AddressLine2 = (this.prvdaddr?.sRefAddressLine2 != null && this.prvdaddr?.sRefAddressLine2 != undefined && this.prvdaddr?.sRefAddressLine2 != "null") ? this.prvdaddr?.sRefAddressLine2 : '';
            this.AcctData.StateCode = (this.prvdaddr?.sRefStatecode != null && this.prvdaddr?.sRefStatecode != undefined) ? this.prvdaddr?.sRefStatecode : '';
            this.AcctData.City = (this.prvdaddr?.sRefCity != null && this.prvdaddr?.sRefCity != undefined) ? this.prvdaddr?.sRefCity : '';
            this.AcctData.ZipCode = (this.prvdaddr?.sRefZipcode != null && this.prvdaddr?.sRefZipcode != undefined) ? this.prvdaddr?.sRefZipcode : '';
            
            this.template.querySelector(`[data-id="RecType"]`).value = 'Provider';  
            this.rtiDataToSend.recipientType = 'Provider';        
            
        }else if (this.AcctResponse != null) {
            this.recipientName = '';
            this.AcctData.AddressLine1 = this.AcctResponse.PersonMailingStreet;
            this.AcctData.AddressLine2 = '';
            this.AcctData.StateCode = this.AcctResponse.PersonMailingStateCode;
            this.AcctData.City = this.AcctResponse.PersonMailingCity;
            this.AcctData.ZipCode = this.AcctResponse.PersonMailingPostalCode;
	    this.template.querySelector(`[data-id="RecType"]`).value = 'Member';
             this.rtiDataToSend.recipientType = 'Member';
        }
        this.isAddressVerified = true;
        this.displayExistingPopOver(false);
    }

    handleYes() {
		this.calledFromExistingCase = this.bToggleCheck ? false : true;
        if (this.renderFaxUi) {
            let fieldsvalidity = [...this.template.querySelectorAll('.validateInput')].reduce((validSoFar, field) => {
                return (validSoFar && field.reportValidity());
            }, true);

            if (fieldsvalidity) {
                this.isAddressVerified = true;
                this.resendProcess(this.calledFromExistingCase);
            }

        } else {
            this.isAddressVerified = true;
            this.resendProcess(this.calledFromExistingCase);
        }
        this.calledFromExistingCase = false;

    }

    renderedCallback(){
        let maindiv = this.template.querySelector(`[data-id="maindiv"]`);
        if(maindiv){
            maindiv.classList.add("popoverSection");
        }
    }

    displayExistingPopOver(showExistingCaseBox){
        let existingCaseBox = this.template.querySelector(`[data-id="divPrintExistingCasePopover"]`);
        let resendPopOver = this.template.querySelector(`[data-id="resendPopOver"]`);
        let maindiv = this.template.querySelector(`[data-id="maindiv"]`);
        if (existingCaseBox && showExistingCaseBox) {
            existingCaseBox.classList.replace("slds-hide", "slds-show");
            resendPopOver.classList.replace("slds-show", "slds-hide");
            maindiv.classList.replace("popoverSection", "existingLoggingBox");
        }
        if (existingCaseBox && !showExistingCaseBox) {
            existingCaseBox.classList.replace("slds-show", "slds-hide");
            resendPopOver.classList.replace("slds-hide", "slds-show");
            maindiv.classList.replace("existingLoggingBox", "popoverSection");
        }
    }

    handlePrintCloseExistingCasePopover(){
        this.isExistingCase = false;
        this.displayExistingPopOver(false);
    }

    handleToggle(event) {
        if (!event.target.checked) {
            if (this.verifyInputFields()) {
                this.isExistingCase = true;
                //this.dispatchEvent(new CustomEvent("existingcase"));
                this.displayExistingPopOver(true);
                event.target.checked = true;
            } else {
                event.target.checked = true;
            }
        } else {
			this.bToggleCheck = true;
            this.isExistingCase = false;
        }
    }
    @track deliveryMethod;
    createCaseEvent(calledFromExisting) {
        this.deliveryMethod = this.renderMailUi ? 'Mail' : 'Fax';
        switch(this.rtiDataToSend?.sStatementType){
            case "rtiMember" :
                this.templateName = TEMPLATE_MEMBER;
                break;
            case "Member" :
                this.templateName = TEMPLATE_CLAIM_MEMBER;
                break;
            case "Provider":
                this.templateName = TEMPLATE_PROVIDER;
                break;
        }
        if(calledFromExisting){
            this.caseId = this.existingCaseId;
	    this.checkCaseInteraction();
		this.updateInteractionToCase();
            this.createTemplateData(calledFromExisting);
        }else{
            this.getNewCaseId();
        }
        this.loadInitialData();
    }
    async checkCaseInteraction(){
        await createCaseInteraction({sCaseID:this.existingCaseId, sInteractionID:this.interactionId}).then(result =>{
            let details;
            if(result){
                this.details=result;
            }
        }).catch(error =>{
            console.log('Error while creating case Interaction')
        })
    }
	 async updateInteractionToCase(){
        await updateInteractionOnCase({sCaseID:this.existingCaseId, sInteractionID:this.interactionId}).then(result =>{
            
        }).catch(error =>{
            console.log('Error while updating case')
        })
    }

    getNewCaseId(){
        this.createNewCaseId().then(result =>{
            if(result){
                this.caseId = result.toString().split('-')[0];
                this.createTemplateData(false);
            }
        }).catch(error =>{
            console.log('Error while creating new case.')
        })
    }

    createTemplateData(calledFromExisting){
        this.getTemplateData(this.caseId, this.templateName).then(result => {
            if(result){
                if(this.rtiDataToSend.sStatementType == 'rtiMember'){
                    this.updateTemplateDataRTI(this.tempSubmissionData)
                    .then(result => {
                        if (result) {
                            if(calledFromExisting){
                                this.navigateToCaseDetailsPage();
                            }else{
                                this.redirectToCaseEditPage();
                            }

                        }
                    })
                    .catch(err => {
                        console.log('Error in Upadating Tempplate Data: ', err);
                    });
                }else{
                    this.updateTemplateData(this.tempSubmissionData)
                    .then(result => {
                        if (result) {
                            if(calledFromExisting){
                                this.navigateToCaseDetailsPage();
                            }else{
                                this.redirectToCaseEditPage();
                            }
                        }
                    })
                    .catch(err => {
                        console.log('Error in Upadating Tempplate Data: ', err);
                    });
                }
            }
        }).catch(error => {
            if (!error) {
                console.log('get template data failed');
            }
        });

    }

    redirectToCaseEditPage = () => {
        let casedata = {};
        casedata.Id = this.caseId;
        casedata.objApiName = 'Case';
        openLWCSubtab('caseInformationComponentHum', casedata, { label: 'Edit Case', icon: 'standard:case' });
        this.loaded = true;
    }

    async createNewCaseId() {
        return new Promise((resolve, reject) => {
            createNewCase({ sObjectId: this.recId, calledfrom: 'Logging', newInteractionId:this.interactionId })
                .then(result => {
                    resolve(result);
                }).catch(error => {
                    reject(error);
                })
        })
    }

    async navigateToCaseDetailsPage() {
        let pageref = {
            type: 'standard__recordPage',
            attributes: {
                recordId: this.caseId,
                objectApiName: 'Case',
                actionName: 'view',
            },
        }
        let focusedTab = await invokeWorkspaceAPI('getFocusedTabInfo');
        if (await invokeWorkspaceAPI('isConsoleNavigation')) {
            await invokeWorkspaceAPI('openSubtab', {
                parentTabId: (focusedTab.hasOwnProperty('isSubtab') && focusedTab.isSubtab) ? focusedTab.parentTabId : focusedTab.tabId,
                pageReference: pageref
            });
        }
        this.loaded = true;
    }

    @api
    executeaddressvaerification() {
        this.calledFromExistingCase = true;
		this.bToggleCheck = false;
        if (this.renderMailUi) {
            if (this.isAddressVerified) {
                this.verifyAddressProcess(true);
            }
            else {
                this.handleYes();
            }
        }
        else {
            this.handleYes();
        }
    }

    verifyAddressProcess(calledFromExistingCase) {
        if (this.verifyInputFields()) {
            this.initiateAddVerification(this.AcctData).then(result => {
                if (result != null && result != "null") {
                    let verificationResponse = JSON.parse(result);
                    if (verificationResponse.sValid == 'true') {
                        this.isAddressVerified = true;
                        this.showToastNotification('Success', 'Address Verified Sucessfully.', 'success', 'pester');
                        this.resendProcess(calledFromExistingCase);
                    }
                    else if (verificationResponse.sValid == 'false') {
                        this.isAddressVerified = false;
                    }
                }
            }).catch(error => {
                this.loaded = true;
                console.log("Error occured in handleAddressVerification- " + JSON.stringify(error));
            });
        }
    }

    handlePrintAttachExisting(event) {
        this.displayExistingPopOver(false);
        this.existingCaseId = event.detail.caseId;
        this.executeaddressvaerification();
    }

    resendProcess(calledFromExistingCase) {
        this.isSendPrint = true;
        if (this.ismemberstatement == true || this.isproviderstatement == true){
            this.createMemberProviderDatatoPrint();
        }else{
            this.createRtiDatatoPrint();
        }

        this.sendPrintStatement(this.AcctData, this.rtiDataToSend)
            .then((result) => {
                if (result != null) {
                    let resposnse = JSON.parse(result);
                    if (resposnse == this.labels.SEND_PRINT_ITEM_SUCCESS_RESPONSE || resposnse == this.labels.SEND_CLAIM_ITEM_SUCCESS_RESPONSE) {
                        this.showToastNotification("Success", resposnse, "success", "pester");
                        this.dispatchEvent(new CustomEvent("createcase"));
                        this.createCaseEvent(calledFromExistingCase);
                    } else if (resposnse == this.labels.SEND_PRINT_ITEM_ERROR_MSG || resposnse == this.labels.SEND_CLAIM_ITEM_ERROR_MSG) {
                        this.showToastNotification(
                            'An Error Occured',
                            resposnse,
                            'error',
                            'pester'
                        );
                    }
                } else {
                    if (this.ismemberstatement == true ||this.isproviderstatement == true) {
                        this.showToastNotification(
                            'An Error Occured',
                            this.labels.SEND_CLAIM_ITEM_ERROR_MSG,
                            'error',
                            'pester'
                        );
                    } else {
                        this.showToastNotification(
                            'An Error Occured',
                            this.labels.SEND_PRINT_ITEM_ERROR_MSG,
                            'error',
                            'pester'
                        );
                    }
                }
                this.isSendPrint = false;
                this.CancelClick();
            })
            .catch((error) => {
                if (this.ismemberstatement == true ||this.isproviderstatement == true) {
                    this.showToastNotification(
                        'An Error Occured',
                        this.labels.SEND_CLAIM_ITEM_ERROR_MSG,
                        'error',
                        'pester'
                    );
                } else {
                    this.showToastNotification(
                        'An Error Occured',
                        this.labels.SEND_PRINT_ITEM_ERROR_MSG,
                        'error',
                        'pester'
                    );
                }

                this.CancelClick();
                console.log(
                    "Error occured in handleResendClick- " + JSON.stringify(error)
                );
                this.isSendPrint = false;
            });
    }

    loadInitialData() {
        if (this.isproviderstatement == true) {
            this.ismemberproviderstatement = true;
            const items = [
                { value: 'Provider', label: 'Provider' },
                { value: 'Other', label: 'Other' }
            ];
	    
	    if(this.bRecipientTypeEdit == false){
                this.rtiDataToSend.recipientType = 'Provider';
            }
            this.recipientType = items;
        }
        else if (this.ismemberstatement == true) {
            this.ismemberproviderstatement = true;
            const items = [
                { value: 'Member', label: 'Member' },
                { value: 'Member Representative or Caregiver', label: 'Member Representative or Caregiver' },
                { value: 'Other', label: 'Other' }
            ];

            if(this.bRecipientTypeEdit == false){
                this.rtiDataToSend.recipientType = 'Member';
            }
	    this.recipientType = items;
        }
        else {
            const items = [
                { value: 'Member', label: 'Member' },
                { value: 'Member Representative or Caregiver', label: 'Member Representative or Caregiver' }
            ];
	    
	    if(this.bRecipientTypeEdit == false){
                this.rtiDataToSend.recipientType = 'Member';
            }
            this.recipientType = items;
			this.bToggleCheck = true;
        }

        this.recipientName = '';
        
        if(this.bAddress1Edit == false){
            if (this.isproviderstatement == true){
                this.AcctData.AddressLine1 = (this.prvdaddr?.sRefAddressLine1 != null && this.prvdaddr?.sRefAddressLine1 != undefined) ? this.prvdaddr?.sRefAddressLine1 : '';
            }else{
                this.AcctData.AddressLine1 = this.AcctResponse.PersonMailingStreet;
            }
        }

        if(this.bAddress2Edit == false){
            if (this.isproviderstatement == true){
                this.AcctData.AddressLine2 = (this.prvdaddr?.sRefAddressLine2 != null && this.prvdaddr?.sRefAddressLine2 != undefined && this.prvdaddr?.sRefAddressLine2 != "null") ? this.prvdaddr?.sRefAddressLine2 : '';
            }else{
                this.AcctData.AddressLine2 = "";
            }
        }
        
        if(this.bCityEdit == false){
            if (this.isproviderstatement == true){
                this.AcctData.City = (this.prvdaddr?.sRefCity != null && this.prvdaddr?.sRefCity != undefined) ? this.prvdaddr?.sRefCity : '';
            }else{
                this.AcctData.City = this.AcctResponse.PersonMailingCity;
            }
        }

        if(this.bStateEdit == false){
            if (this.isproviderstatement == true){
                this.AcctData.StateCode = (this.prvdaddr?.sRefStatecode != null && this.prvdaddr?.sRefStatecode != undefined) ? this.prvdaddr?.sRefStatecode : '';
            }else{
                this.AcctData.StateCode = this.AcctResponse.PersonMailingStateCode;
            }
        }

        if(this.bZipEdit == false){
            if (this.isproviderstatement == true){
                this.AcctData.ZipCode = (this.prvdaddr?.sRefZipcode != null && this.prvdaddr?.sRefZipcode != undefined) ? this.prvdaddr?.sRefZipcode : '';
            }else{
                this.AcctData.ZipCode = this.AcctResponse.PersonMailingPostalCode;
            }
        }

        if (this.isproviderstatement == true){
            this.AcctData.Name = this.prvdaddr?.sPrvName;
        }
        else{
            this.AcctData.Name = this.AcctResponse.Name;
        }
        
        this.AcctData.FirstName = this.AcctResponse.FirstName;
        this.AcctData.LastName = this.AcctResponse.LastName;
        this.AcctData.FaxNo = "";
        this.AcctData.RecipientName = this.recipientName ?? "";	

    }

    verifyInputFields() {
        let fieldsvalidity = [
            ...this.template.querySelectorAll(".validateInput")
        ].reduce((validSoFar, field) => {
            return validSoFar && field.reportValidity();
        }, true);

        return fieldsvalidity;
    }

    updateTemplateDataRTI(templateData) {
        let bSuccess = true;
        return new Promise((resolve, reject) => {
            templateData.forEach((tData, index) => {
                if (tData.fields.Template_Field__r.displayValue === 'RTIPrintDeliveryMethod') {
                    //update RTIPrintDeliveryMethod
                    let valueToUpdate = this.deliveryMethod;
                    bSuccess = this.updateTemplate(tData.id, valueToUpdate, tData.fields.Template_Field__r.displayValue);

                }
                else if (tData.fields.Template_Field__r.displayValue === 'RTIPrintRecipientName' && this.deliveryMethod.toUpperCase() == 'MAIL') {
                    //update RTIPrintRecipientName
                    let valueToUpdate = this.recipientNameToSend ? this.recipientNameToSend : '';
                    bSuccess = this.updateTemplate(tData.id, valueToUpdate, tData.fields.Template_Field__r.displayValue);

                }
                else if (tData.fields.Template_Field__r.displayValue === 'RTIPrintRecipientType') {
                    //update RTIPrintRecipientType
                    let valueToUpdate = this.rtiDataToSend.recipientType;
                    bSuccess = this.updateTemplate(tData.id, valueToUpdate, tData.fields.Template_Field__r.displayValue);


                }
                else if (tData.fields.Template_Field__r.displayValue === 'RTIPrintAddressLine1' && this.deliveryMethod.toUpperCase() == 'MAIL') {
                    //update RTIPrintAddressLine1
                    let valueToUpdate = this.AcctData.AddressLine1;
                    bSuccess = this.updateTemplate(tData.id, valueToUpdate, tData.fields.Template_Field__r.displayValue);

                }
                else if (tData.fields.Template_Field__r.displayValue === 'RTIPrintAddressLine2' && this.deliveryMethod.toUpperCase() == 'MAIL') {
                    //update RTIPrintAddressLine2
                    let valueToUpdate = this.addressLine2;
                    bSuccess = this.updateTemplate(tData.id, valueToUpdate, tData.fields.Template_Field__r.displayValue);

                }
                else if (tData.fields.Template_Field__r.displayValue === 'RTIPrintCity' && this.deliveryMethod.toUpperCase() == 'MAIL') {
                    //update RTIPrintCity
                    let valueToUpdate = this.AcctData.City;
                    bSuccess = this.updateTemplate(tData.id, valueToUpdate, tData.fields.Template_Field__r.displayValue);

                }
                else if (tData.fields.Template_Field__r.displayValue === 'RTIPrintState' && this.deliveryMethod.toUpperCase() == 'MAIL') {
                    //update RTIPrintState
                    let valueToUpdate = this.AcctData.StateCode;
                    bSuccess = this.updateTemplate(tData.id, valueToUpdate, tData.fields.Template_Field__r.displayValue);

                }
                else if (tData.fields.Template_Field__r.displayValue === 'RTIPrintZip' && this.deliveryMethod.toUpperCase() == 'MAIL') {
                    //update RTIPrintZip
                    let valueToUpdate = this.AcctData.ZipCode;
                    bSuccess = this.updateTemplate(tData.id, valueToUpdate, tData.fields.Template_Field__r.displayValue);

                }
                else if (tData.fields.Template_Field__r.displayValue === 'RTIPrintWorkQueueName') {
                    //update RTIPrintWorkQueueName
                    let valueToUpdate = this.workQueue;
                    bSuccess = this.updateTemplate(tData.id, valueToUpdate, tData.fields.Template_Field__r.displayValue);

                }
                else if (tData.fields.Template_Field__r.displayValue === 'RTIPrintCategoryType') {

                    //update RTIPrintCategoryType
                    let valueToUpdate = this.rtiDataToSend.CatDesc;
                    bSuccess = this.updateTemplate(tData.id, valueToUpdate, tData.fields.Template_Field__r.displayValue);

                }
                else if (tData.fields.Template_Field__r.displayValue === 'RTIPrintMessageName') {
                    //update RTIPrintMessageName
                    let valueToUpdate = this.rtiDataToSend.MsgName;
                    bSuccess = this.updateTemplate(tData.id, valueToUpdate, tData.fields.Template_Field__r.displayValue);

                }
                else if (tData.fields.Template_Field__r.displayValue === 'Data') {
                    let valueToUpdate = '<FONT FACE="Arial" STYLE="font-size:16px"><u><b>Summary</b></u><br><br> <table style="font-family: Arial, Helvetica, sans-serif; border-collapse: collapse;width: 1200px;font-size:12px;text-align: left;"><thead><tr> <th style="border: 1px solid #ddd;background-color: #D3D3D3;padding: 8px;">Action</th> <th style="border: 1px solid #ddd;background-color: #D3D3D3;padding: 8px;">Message Name</b> </td><th style="border: 1px solid #ddd;background-color: #D3D3D3;padding: 8px;"> Category Type</b></td><th style="border: 1px solid #ddd;background-color: #D3D3D3;padding:  8px;">Date Type</b></td>  <th style="border: 1px solid #ddd;background-color: #D3D3D3;padding: 8px;">Delivery Method</b>  </td></tr></thead><tr><td style="border: 1px solid #ddd;padding: 8px;"> <a style="color:blue" href=' + this.rtiDataToSend.Viewlink + ' target="_blank" >View</a></td>  <td style="border: 1px solid #ddd;padding: 8px;">' + this.rtiDataToSend.MsgName + ' </td>  <td style="border: 1px solid #ddd;padding: 8px;">' + this.rtiDataToSend.CatDesc + '</td>  <td style="border: 1px solid #ddd;padding: 8px;">' + this.rtiDataToSend.DateSent + '</td>  <td style="border: 1px solid #ddd;padding: 8px;">Mail</td></tr> </table>  </FONT> <br/><br/> <br/>   <FONT FACE="Arial" STYLE="font-size:14px"><b>Recipient Name: </b>' + this.recipientNameToSend + '</FONT> <br/><br/><FONT FACE="Arial" STYLE="font-size:14px"><b>Recipient Type: </b>' + this.rtiDataToSend.recipientType + '</FONT> <br/><br/>    <FONT FACE="Arial" STYLE="font-size:14px"><b>Address Line 1: </b>' + this.AcctData.AddressLine1 + '</FONT> <br/><br/>   <FONT FACE="Arial" STYLE="font-size:14px"><b>Address Line 2: </b>' + this.addressLine2 + '</FONT> <br/><br/>   <FONT FACE="Arial" STYLE="font-size:14px"><b>City: </b>' + this.AcctData.City + '</FONT> <br/><br/>     <FONT FACE="Arial" STYLE="font-size:14px"><b>State: </b>' + this.AcctData.StateCode + '</FONT> <br/><br/>   <FONT FACE="Arial" STYLE="font-size:14px"><b>Zip Code : </b>' + this.AcctData.ZipCode + '</FONT>';
                    bSuccess = this.updateTemplate(tData.id, valueToUpdate, tData.fields.Template_Field__r.displayValue);

                }
                else if (tData.fields.Template_Field__r.displayValue === 'RTIPrintDateTime') {
                    //update RTIPrintMessageName
                    let valueToUpdate = this.rtiDataToSend.DateSent;
                    bSuccess = this.updateTemplate(tData.id, valueToUpdate, tData.fields.Template_Field__r.displayValue);

                }

                if (!bSuccess) {
                    reject(bSuccess);
                }
            });
            if (bSuccess) {
                resolve(bSuccess);
            }
        });
    }

    updateTemplate(tempId, valueToUpdate, dispalyValue) {
        updateTemplateSubmissionData(tempId, valueToUpdate, dispalyValue)
            .then(result => {

            })
            .catch(err => {
                console.log('error to update -> ', dispalyValue);
                return false;
            });
        return true;
    }

	updateTemplateData(templateData) {
        let bSuccess = true;
        return new Promise((resolve, reject) => {
            templateData.forEach((tData, index) => {
                if (tData.fields.Template_Field__r.displayValue === 'Data') {
                    //update Data
                    let valueToUpdate = this.populateTemplatedata(this.rtiDataToSend.DocKey, this.rtiDataToSend.sStatementType, this.deliveryMethod);
                    bSuccess = this.updateTemplate(tData.id, valueToUpdate, tData.fields.Template_Field__r.displayValue);

                }
                else if (tData.fields.Template_Field__r.displayValue === 'EndStatementPeriod') {
                    //update EndStatementPeriod
                    let valueToUpdate = this.rtiDataToSend.EndStatementPeriod;
                    bSuccess = this.updateTemplate(tData.id, valueToUpdate, tData.fields.Template_Field__r.displayValue);

                }
                else if (tData.fields.Template_Field__r.displayValue === 'ClaimNumber') {
                    //update ClaimNumber
                    let valueToUpdate = this.rtiDataToSend.ClaimNumber;
                    bSuccess = this.updateTemplate(tData.id, valueToUpdate, tData.fields.Template_Field__r.displayValue);


                }
                else if (tData.fields.Template_Field__r.displayValue === 'RemittanceID') {
                    //update RemittanceID
                    let valueToUpdate = this.rtiDataToSend.RemitId;
                    bSuccess = this.updateTemplate(tData.id, valueToUpdate, tData.fields.Template_Field__r.displayValue);

                }
                else if (tData.fields.Template_Field__r.displayValue === 'DeliveryMethod') {
                    //update DeliveryMethod
                    let valueToUpdate = this.deliveryMethod;
                    bSuccess = this.updateTemplate(tData.id, valueToUpdate, tData.fields.Template_Field__r.displayValue);

                }
                else if (tData.fields.Template_Field__r.displayValue === 'RecipientName') {
                    //update RecipientName
                    let valueToUpdate = this.recipientNameToSend ? this.recipientNameToSend : '';
                    bSuccess = this.updateTemplate(tData.id, valueToUpdate, tData.fields.Template_Field__r.displayValue);

                }
                else if (tData.fields.Template_Field__r.displayValue === 'RecipientType') {
                    //update RecipientType
                    let valueToUpdate = this.rtiDataToSend.recipientType;
                    bSuccess = this.updateTemplate(tData.id, valueToUpdate, tData.fields.Template_Field__r.displayValue);

                }
                else if (tData.fields.Template_Field__r.displayValue === 'AddressLine1'  && this.deliveryMethod.toUpperCase() == 'MAIL') {
                    //update AddressLine1
                    let valueToUpdate = this.AcctData.AddressLine1;
                    bSuccess = this.updateTemplate(tData.id, valueToUpdate, tData.fields.Template_Field__r.displayValue);

                }
                else if (tData.fields.Template_Field__r.displayValue === 'AddressLine2' && this.deliveryMethod.toUpperCase() == 'MAIL') {

                    //update AddressLine2
                    let valueToUpdate = this.addressLine2;
                    bSuccess = this.updateTemplate(tData.id, valueToUpdate, tData.fields.Template_Field__r.displayValue);

                }
                else if (tData.fields.Template_Field__r.displayValue === 'City' && this.deliveryMethod.toUpperCase() == 'MAIL') {
                    //update City
                    let valueToUpdate = this.AcctData.City;
                    bSuccess = this.updateTemplate(tData.id, valueToUpdate, tData.fields.Template_Field__r.displayValue);

                }
                else if (tData.fields.Template_Field__r.displayValue === 'State' && this.deliveryMethod.toUpperCase() == 'MAIL') {
                    let valueToUpdate =  this.AcctData.StateCode;
                    bSuccess = this.updateTemplate(tData.id, valueToUpdate, tData.fields.Template_Field__r.displayValue);

                }
                else if (tData.fields.Template_Field__r.displayValue === 'Zip' && this.deliveryMethod.toUpperCase() == 'MAIL') {
                    //update Zip
                    let valueToUpdate = this.AcctData.ZipCode;
                    bSuccess = this.updateTemplate(tData.id, valueToUpdate, tData.fields.Template_Field__r.displayValue);

                }
                else if (tData.fields.Template_Field__r.displayValue === 'FaxNumber') {
                    //update FaxNumber
                    let valueToUpdate = this.rtiDataToSend.DateSent;
                    bSuccess = this.updateTemplate(tData.id, valueToUpdate, tData.fields.Template_Field__r.displayValue);

                }
                else if (tData.fields.Template_Field__r.displayValue === 'WorkQueueName') {
                    //update WorkQueueName
                    let valueToUpdate = this.workQueue;
                    bSuccess = this.updateTemplate(tData.id, valueToUpdate, tData.fields.Template_Field__r.displayValue);

                }
                else if (tData.fields.Template_Field__r.displayValue === 'DocumentKey') {
                    //update DocumentKey
                    let valueToUpdate = this.rtiDataToSend.DocKey;
                    bSuccess = this.updateTemplate(tData.id, valueToUpdate, tData.fields.Template_Field__r.displayValue);

                }
                else if (tData.fields.Template_Field__r.displayValue === 'ApplicationKey') {
                    //update ApplicationKey
                    let valueToUpdate = APPLICATION_KEY;
                    bSuccess = this.updateTemplate(tData.id, valueToUpdate, tData.fields.Template_Field__r.displayValue);

                }
                else if (tData.fields.Template_Field__r.displayValue === 'StatementType') {
                    //update StatementType
                    let valueToUpdate = this.rtiDataToSend.sStatementType;
                    bSuccess = this.updateTemplate(tData.id, valueToUpdate, tData.fields.Template_Field__r.displayValue);

                }
                else if (tData.fields.Template_Field__r.displayValue === 'BeginStatementPeriod') {
                    //update BeginStatementPeriod
                    let valueToUpdate = this.rtiDataToSend.StatementPeriod;
                    bSuccess = this.updateTemplate(tData.id, valueToUpdate, tData.fields.Template_Field__r.displayValue);

                }

                if (!bSuccess) {
                    reject(bSuccess);
                }
            });
            if (bSuccess) {
                resolve(bSuccess);
            }
        });
    }

    populateTemplatedata(sDocumentKey, sStatementType, selectedValue) {
        let sData;
        let sDocKeyEncode = sDocumentKey.replaceAll( ' ', '%20');
        if(selectedValue.toUpperCase() == 'MAIL' && sStatementType == 'Member')  {
            sData = '<FONT FACE="Arial" STYLE="font-size:16px"><u><b>Summary</b></u><br><br>Claim Number:' +this.rtiDataToSend.ClaimNumber+'<br><br> <table border="1Px"> <tr> <td><b>Action</b></td> <td><b>Statement Type</b></td> <td><b>Begin Statement period</b></td> <td><b>End Statement Period</b></td> </tr> <tr><td> <a href='+sDocKeyEncode+'>View</a></td> <td>'+this.rtiDataToSend.sStatementType+'</td> <td>'+this.rtiDataToSend.StatementPeriod+'</td> <td>'+this.rtiDataToSend.EndStatementPeriod+'</td> </tr> </table> </FONT> <br><br><FONT FACE="Arial" STYLE="font-size:14px"><b>Delivery Method : </b>'+this.deliveryMethod+'</FONT> <br> <FONT FACE="Arial" STYLE="font-size:14px"><b>Recipient Name: </b>'+this.recipientNameToSend+'</FONT> <br> <FONT FACE="Arial" STYLE="font-size:14px"><b>Recipient Type: </b>'+this.rtiDataToSend.recipientType +'</FONT> <br> <FONT FACE="Arial" STYLE="font-size:14px"><b>Address Line 1: </b>'+this.AcctData.AddressLine1+'</FONT> <br> <FONT FACE="Arial" STYLE="font-size:14px"><b>Address Line 2: </b>' + this.addressLine2 + '</FONT> <br> <FONT FACE="Arial" STYLE="font-size:14px"><b>City: </b>'+this.AcctData.City+'</FONT> <br> <FONT FACE="Arial" STYLE="font-size:14px"><b>State: </b>'+this.AcctData.StateCode+'</FONT> <br> <FONT FACE="Arial" STYLE="font-size:14px"><b>Zip Code : </b>'+this.AcctData.ZipCode+'</FONT>';
        }
        else if(selectedValue.toUpperCase() == 'FAX' && sStatementType == 'Member')  {
            sData ='<FONT FACE="Arial" STYLE="font-size:16px"><u><b>Summary</b></u><br><br>Claim Number:' +this.rtiDataToSend.ClaimNumber+'<br><br> <table border="1Px"> <tr> <td><b>Action</b></td> <td><b>Statement Type</b></td> <td><b>Begin Statement period</b></td> <td><b>End Statement Period</b></td> </tr> <tr><td> <a href='+sDocKeyEncode+'>View</a></td> <td>'+this.rtiDataToSend.sStatementType+'</td> <td>'+this.rtiDataToSend.StatementPeriod+'</td> <td>'+this.rtiDataToSend.EndStatementPeriod+'</td> </tr> </table> </FONT> <br><br><FONT FACE="Arial" STYLE="font-size:14px"><b>Delivery Method : </b>'+this.deliveryMethod+'</FONT> <br> <FONT FACE="Arial" STYLE="font-size:14px"><b>Recipient Name: </b>'+this.recipientNameToSend+'</FONT> <br> <FONT FACE="Arial" STYLE="font-size:14px"><b>Recipient Type: </b>'+this.rtiDataToSend.recipientType +'</FONT> <br> <FONT FACE="Arial" STYLE="font-size:14px"><b>Fax number: </b>'+this.rtiDataToSend.FaxNo+'</FONT>';
        }

        else if(selectedValue.toUpperCase() == 'MAIL' && sStatementType == 'Provider')  {
            sData = '<FONT FACE="Arial" STYLE="font-size:16px"><u><b>Summary</b></u><br><br>Claim Number:' +this.rtiDataToSend.ClaimNumber+'<br><br> <table border="1Px"> <tr> <td><b>Action</b></td> <td><b>Remittance ID</b></td> </tr> <tr><td> <a href='+sDocKeyEncode+'>View</a></td> <td>'+this.rtiDataToSend.RemitId+'</td> </tr> </table> </FONT> <br><br><FONT FACE="Arial" STYLE="font-size:14px"><b>Delivery Method : </b>'+this.deliveryMethod+'</FONT> <br> <FONT FACE="Arial" STYLE="font-size:14px"><b>Recipient Name: </b>'+this.recipientNameToSend+'</FONT> <br> <FONT FACE="Arial" STYLE="font-size:14px"><b>Recipient Type: </b>'+this.rtiDataToSend.recipientType +'</FONT> <br> <FONT FACE="Arial" STYLE="font-size:14px"><b>Address Line 1: </b>'+this.AcctData.AddressLine1+'</FONT> <br> <FONT FACE="Arial" STYLE="font-size:14px"><b>Address Line 2: </b>' + this.addressLine2 + '</FONT> <br> <FONT FACE="Arial" STYLE="font-size:14px"><b>City: </b>'+this.AcctData.City+'</FONT> <br> <FONT FACE="Arial" STYLE="font-size:14px"><b>State: </b>'+this.AcctData.StateCode+'</FONT> <br> <FONT FACE="Arial" STYLE="font-size:14px"><b>Zip Code : </b>'+this.AcctData.ZipCode+'</FONT>';
        }
        else if(selectedValue.toUpperCase() == 'FAX' && sStatementType == 'Provider')  {
            sData = '<FONT FACE="Arial" STYLE="font-size:16px"><u><b>Summary</b></u><br><br>Claim Number:' +this.rtiDataToSend.ClaimNumber+'<br><br> <table border="1Px"> <tr> <td><b>Action</b></td> <td><b>Remittance ID</b></td> </tr> <tr><td> <a href='+sDocKeyEncode+'>View</a></td> <td>'+this.rtiDataToSend.RemitId+'</td> </tr> </table> </FONT> <br><br><FONT FACE="Arial" STYLE="font-size:14px"><b>Delivery Method : </b>'+this.deliveryMethod+'</FONT> <br> <FONT FACE="Arial" STYLE="font-size:14px"><b>Recipient Name: </b>'+this.recipientNameToSend+'</FONT> <br> <FONT FACE="Arial" STYLE="font-size:14px"><b>Recipient Type: </b>'+this.rtiDataToSend.recipientType +'</FONT> <br> <FONT FACE="Arial" STYLE="font-size:14px"><b>Fax number: </b>'+this.rtiDataToSend.FaxNo+'</FONT>';
        }
        return sData;
    }

    getTemplateData(caseId, templateName) {
        let bDataCreated = false;
        return new Promise((resolve, reject) => {
            generateTemplateData(templateName, caseId)
                .then(result => {
                    if (result && typeof (result) === 'object') {
                        this.sendPrintItemTemplateDetails = result?.has('template') ? result.get('template')[0] : null;
                        this.templateFields = result?.has('templateFields') ? result.get('templateFields') : null;
                        this.tempSubmissionOwnerId = result?.has('templateSubmissionOwner') ? result.get('templateSubmissionOwner') : null;
                        this.processIds.push(this.tempSubmissionOwnerId);
                        this.tempSubmissionId = result?.has('templateSubmission') ? result.get('templateSubmission') : null;
                        this.tempSubmissionData = result?.has('templateSubmissionData') ? result.get('templateSubmissionData') : null;
                        resolve(true);
                    }
                }).catch(error => {
                    console.log(error);
                    reject(false);
                })
        })
    }
}