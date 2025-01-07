/*******************************************************************************************************************************
LWC JS Name : genericCaseCommentLogging.js
Function    : This JS serves as controller to genericCaseCommentLogging.html.

Modification Log:
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Abhishek Mangutkar                                      04/05/2022                 Original Version - US3198914
* Nirmal Garg											  04/12/2022			     DF-4775-Added logic to pass object API name for case edit page
* Nirmal Garg											  07/13/2022				 US-3184447-Optimization of invoice request flow.
* Jonathan Dickinson                                      09/27/2022                 REQ - 3751914
* Nirmal Garg                                             09/30/2022                 Auth/Reff Case redirect
* Swapnali Sonawane                                  	  11/01/2022                 US- 3729809 Migration of the UI enhancements in the addresses section
* Abhishek Mangutkar                                  	  03/01/2023                 US 4286520 Remove logic for assign member plan id for logging cases
* Aishwarya Pawar                                 	      03/02/2023                 US 4315305  systematically associate interaction to new/existing cases for the HP logging scenarios
* Abhishek Mangutkar                                      03/13/2023                  US-4365921
* Jonathan Dickinson                                      06/14/2023                 User Story 4705843: T1PRJ0891339 2023 Arch Remediation-SF-Tech-Filter cases having template attached from existing case history logging for process logging
* Vishal Shinde                                           29/02/2024                 US - 5142800-Mail Order Management - Pharmacy - "Prescriptions & Order Summary" tab - Prescriptions – Create Order
* Pinky Vijur                                             03/04/2024                 User Story 5428942: T1PRJ0865978- MF21712 Mail Order Management; Pharmacy- Guided Flow- Web Issues- Launch from pharmacy
*********************************************************************************************************************************/
import { LightningElement, api, track, wire } from 'lwc';
import createNewCase from '@salesforce/apex/Logging_LC_HUM.createNewCase';
import attachProcessToCase from '@salesforce/apex/Logging_LC_HUM.updateWhoId';
import { openLWCSubtab, invokeWorkspaceAPI } from 'c/workSpaceUtilityComponentHum';
import existingCaseTemplate from './template/existingCaseTemplate.html';
import newCaseTemplate from './template/newCaseTemplate.html';
import noPopOverTemplate from './template/noPopOverCaseLoggingTemplate.html';
import pharmacyTemplate from './template/pharmacyCaseLogging.html';
import { CurrentPageReference } from 'lightning/navigation';
import { attachInteractionToCase, getInteractionId } from 'c/genericCaseActionHum';
import genericCaseActionHum from 'c/genericCaseActionHum';
import assignValuesForInactivateRx from '@salesforce/apex/Logging_LC_HUM.assignValuesForInactivateRx';


export default class GenericCaseCommentLogging extends genericCaseActionHum {

    @api recordId;
    @api responseobj;
    @api hidePopover;
    @api buttonConfig;
    @api submit;
    @api pharmacyTemp;
    @api isCreditCard;
	@api planMemberId;
    @api disabled;

    @api noPopOver = false;
    @api useSaveData = false;
    @track caseId;
    @track caseNumber;
    @track loggingStarted = false;
    @track loggingMessage = '';
    @track logToNewCase = true;
    @track logToExistingCase = false;
    @track viewExistingCasePanel = false;
    @track disableLogFinishButton = false;
    @track showCaseId;
    @api filterCasesHavingTemplate = false;
    @track bShowModal = false;
    @track webIssueFlow = false;

    connectedCallback() {
        if (this.responseobj && this.responseobj.source) {
            if (this.responseobj.source.toLowerCase() === 'pharmacywebissuesguidedflow') {
                this.webIssueFlow = true;
            }
            else {
                this.webIssueFlow = false;
            }
        }
    }


    render() {
        if (this.noPopOver)
        {this.disableLogFinishButton = true;
            if (this.logToNewCase) {
                return noPopOverTemplate;
            } else if (this.logToExistingCase) {
                return existingCaseTemplate;
            }
        } else if(this.pharmacyTemp == true){
	        {   
                this.disableLogFinishButton = true;
                if (this.logToNewCase) {
                    this.useSaveData = true;
                    return pharmacyTemplate;
                } else if (this.logToExistingCase) {
                    return existingCaseTemplate;
                }
	        }
	}
	else{
            if (this.logToNewCase) {
                return newCaseTemplate;
            } else if (this.logToExistingCase) {
                this.disableLogFinishButton = true;
                return existingCaseTemplate;
            }
        }
    }

    handleCaseCheckBoxSelect(event) {
        if(event.detail.checked){
            this.caseId = event.detail.selectedCaseId;
            this.caseNumber = event.detail.selectedCaseNumber;
            this.disableLogFinishButton = false;
            if(this.template.querySelector('.slds-button') != null){
                this.template.querySelector('.slds-button').removeAttribute("disabled");
            }
        }else{
            this.caseId = null;
            this.caseNumber = null;
            this.disableLogFinishButton = true;
            if(this.template.querySelector('.slds-button') != null){
                this.template.querySelector('.slds-button').setAttribute("disabled", "");
            }
        }
    }


    toggleLogTo(event) {
        if (this.logToNewCase) {
            this.logToNewCase = false;
            this.logToExistingCase = true;
        }
        else {
            this.logToExistingCase = false;
            this.logToNewCase = true;
            this.existingCaseId = null;
            this.disableLogFinishButton = true;
        }
    }

    get getcustomstyle() {
        let wInnerWidth = window.innerWidth - 100;
        let customstyle = `top:20px;width:${wInnerWidth}px;left:20px;`;
        return customstyle
    }

    handlePharmacyCancel() {
        this.dispatchEvent(new CustomEvent('cancel'));
    }

    handleSaveLog() {
        this.dispatchEvent(new CustomEvent('savedata', {
            detail: {ExistCase:false}}));
    }

    @api
    handleLog(resObj,existCase)
    {
        this.responseobj = resObj;
        if (existCase)
        {
            if (this.cid) {
                this.caseId = this.cid;
            }
            if (this.cnumber) {
                this.caseNumber = this.cnumber;
            }
            this.logToNewCase=false;
        }
        this.handleClick();
    }
    handleSaveLogCase(){
        this.dispatchEvent(new CustomEvent('savedata', {
            detail: {ExistCase:true}}));
        this.logToNewCase = true; 
        this.pharmacyTemp = true;
    }

    handleClick() {

        this.disableLogFinishButton = true;
        this.loggingStarted = true;
        if (this.logToNewCase) {
			if(this.planMemberId){
                this.recordId = this.planMemberId;
            }
            this.loggingMessage = 'Creating new case';
			let interactionId = getInteractionId();
            createNewCase({ sObjectId: this.recordId, calledfrom: this.responseobj.source, newInteractionId: interactionId }).then(result => {
                if (result && result.includes('-')) {
                    let arrResult = result.toString().split('-');
                    this.caseId = arrResult[0];
                    this.caseNumber = arrResult[1];
                    console.log('Case Id - ' + this.caseId);
                    console.log('Case Number - ' + this.caseNumber);

                    if(this.responseobj?.createOrderCheck === true){
                        this.dispatchEvent(new CustomEvent('passcasenumber', { 
                            detail: {
                                msg: this.caseNumber 
                            }
                        }))
                    } 
                }
                if (this.responseobj && this.responseobj?.attachProcessToCase) {
                    this.updateWhoId();
                } else {
                    this.dispatchEvent(new CustomEvent('finish'));
                    this.redirectToCaseEditPage();
                }
            }).catch(error => {
                console.log(typeof(error) === 'object' ? JSON.stringify(error) : error);
            });
        }
        else {
            if (this.responseobj && this.responseobj?.attachProcessToCase) {
                this.updateWhoId();
            } else {
                this.loggingMessage = `Currently logging to case ${this.caseNumber}`;
                this.dispatchEvent(new CustomEvent('finish'));
                if(this.responseobj?.redirecttocaseedit){
                  this.redirectToCaseEditPage();
                } else {
                  this.attachInteraction();
                  this.redirectToCaseDetailPage();
                }

            }
        }
    }
    attachInteraction() {
        let attachInteractionResponse;
        attachInteractionToCase(this.caseId).then(result => {
            if (result) {
                attachInteractionResponse = result;
            }
        }).catch(error => {
            console.log('Error Occured in attachInteraction', error);
        });
    }

    updateWhoId = () => {
        this.loggingMessage = `Currently logging to case ${this.caseNumber}`;
        let processSids=[];
        if(this.responseobj && this.responseobj?.data && Array.isArray(this.responseobj.data)){
            this.responseobj.data.forEach(k =>{
                processSids.push(k.SID)
            })
        }
        attachProcessToCase({ processIds: processSids, caseid: this.caseId }).then(result => {
            if (result && result === true) {
                if (this.responseobj.source === 'inactivateRx') {
                    assignValuesForInactivateRx({ caseId: this.caseId }).then(result => {
                        if (result && result === true) {
                this.dispatchEvent(new CustomEvent('finish'));
                this.redirectToCaseEditPage();
                        } else {
                            this.redirectToCaseEditPage();
                        }
                    });
                } else {
                    this.dispatchEvent(new CustomEvent('finish'));
                    this.redirectToCaseEditPage();
                }
            }
      			else{
                  if(this.responseobj && this.responseobj?.redirecttocaseedit){
                    this.redirectToCaseEditPage();
                  } else if (this.responseobj && this.responseobj?.redirecttocasedetail) {
                      this.attachInteraction();
                    this.redirectToCaseDetailPage();
                  }
        				  else {
        					console.log('issue');
        				  }
			        }
        })
    }

    get headerclass(){
        if(this.responseobj && this.responseobj.source){
            if (this.responseobj.source.toLowerCase() === 'invoice' || this.responseobj.source.toLowerCase() ==='web issues') {
                return "slds-modal__header"
            }else{
                return "slds-modal__header slds-theme_success slds-theme_alert-texture";
            }
        }
    }

    redirectToCaseEditPage = () => {
        let casedata = {};
        casedata.Id = this.caseId;
        casedata.caseComment = this.responseobj?.caseComment ?? '';
        casedata.objApiName = 'Case';
		casedata.templateName = this.responseobj && this.responseobj?.source && this.responseobj.source.toLowerCase() === 'invoice' ? 'invoice' : null;
        openLWCSubtab('caseInformationComponentHum', casedata, { label: 'Edit Case', icon: 'standard:case' });
    }
	async redirectToCaseDetailPage() {
        let pageref = {
            type: 'standard__recordPage',
            attributes: {
                recordId: this.caseId,
                objectApiName: 'Case',
                actionName: 'view'
            },
        }
        let focusedTab = await invokeWorkspaceAPI('getFocusedTabInfo');
        if (await invokeWorkspaceAPI('isConsoleNavigation')) {
            await invokeWorkspaceAPI('openSubtab', {
                parentTabId: (focusedTab.hasOwnProperty('isSubtab') && focusedTab.isSubtab) ? focusedTab.parentTabId : focusedTab.tabId,
                pageReference: pageref
            });
        }

    }
	
	@wire(CurrentPageReference)
	getStateParameters(currentPageReference) {
		if (currentPageReference) {
			let urlStateParameters = currentPageReference.state;
			if(urlStateParameters && urlStateParameters?.c__PlanMemberId){
                this.planMemberId = urlStateParameters?.c__PlanMemberId ?? null;
            }
		}
	}
}