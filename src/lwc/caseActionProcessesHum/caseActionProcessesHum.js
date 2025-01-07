/*
JS Controller        : Case Action Process Hum
Version              : 1.0
Created On           : 01/18/2022
Function             : Component to display to case action processes.

Modification Log: 
* Developer Name                    Date                         Description
* Nirmal Garg                       01/18/2022                   Original Version
* Abhishek Mangutkar                09/05/2022                   US-3668207
* Kalyani Pachpol                   07/12/2023                   US-4810468
*------------------------------------------------------------------------------------------------------------------------------
*/
import { api, LightningElement, track, wire } from "lwc";
import agencyCCSSupervisor from "@salesforce/label/c.HUMAgencyCCSupervisor";
import agencyCCS from "@salesforce/label/c.HUMAgencyCCS";
import pharmacySpecialist from "@salesforce/label/c.PHARMACY_SPECIALIST_PROFILE_NAME";
import availableProcesses from "@salesforce/label/c.HUMAvailableProcess";
import recommendedProcesses from "@salesforce/label/c.HUMRecommendedProcess"
import hasCRMS_300_Humana_Pharmacy_Supervisor from "@salesforce/customPermission/CRMS_300_HP_Supervisor_Custom";
import { getRecord } from "lightning/uiRecordApi";
import USER_ID from "@salesforce/user/Id";
import PROFILE_NAME_FIELD from "@salesforce/schema/User.Profile.Name";
import { ShowToastEvent } from "lightning/platformShowToastEvent";
import { NavigationMixin } from "lightning/navigation";
import { invokeWorkspaceAPI, openLWCSubtab } from 'c/workSpaceUtilityComponentHum';
const ERROR_MESSAGE ="An error occurred while processing your request. Please try after some time";
const CASE_COMMENT ="Mail Order Pharmacy Web Issues process attached to the case and transferred to the Web Ticket team.";
export default class CaseActionProcessesHum extends  NavigationMixin(LightningElement) {
    @api
    recordId;
    @track flowParams;
    @track flowname;
    @track processData;
    @track lstProcess;
    @track bDisplayWebIssueProcess = false;
    @track profilename;
    @track loadWebIssueProcess = false;
    @track bIssueOccurred = false;
    @track bIsFlowFinished = false;
    @track bTicketRequired = false;
    @track url;
    @track isConfirmingFlowExit = false;
    @track isRecordIdValid = false;
    @track didFlowFail = false;
    @track loadGuidedFlow;
    @track hasProcess = false;
    @track bDisplayErrorMessage = false;
    @track bDisplayRecommendedProcess = false;
    @track bDisplayProcesses = false;
    @track pTabId;
    @wire(getRecord, {
        recordId: USER_ID,
        fields: [PROFILE_NAME_FIELD],
    })
    wireuser({ error, data }) {
        if (error) {
            this.error = error;
        } else if (data) {
            this.profilename = data.fields.Profile.value.fields.Name.value;
        }
    }
    label = {
        agencyCCSSupervisor,
        availableProcesses,
        agencyCCS,
        recommendedProcesses,
        pharmacySpecialist
    };
    connectedCallback() {
        this.getTabDetails();
    }

    async getTabDetails(){
        if ( await invokeWorkspaceAPI('isConsoleNavigation')) {
            let focusedTab =  await invokeWorkspaceAPI('getFocusedTabInfo');
            this.pTabId = focusedTab.tabId;
        }
    }
    
    resetvariables() {
        this.flowParams = [];
        this.bDisplayErrorMessage = false;
        this.bIsFlowFinished = false;
        this.hasProcess = false;
        this.bIssueOccurred = false;
        this.loadWebIssueProcess = false;
        this.loadGuidedFlow = false;
    }
    launchProcess(event) {
        let selectedProcessName = event.target.dataset.name;
        this.resetvariables();
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

    processPharmacyWebIssueFinish(response) {
        let data = response.find((t) => t.name === "Ticket_Required");
        if (data) {
            this.bTicketRequired = data.value;
            if (!this.hasProcess) {
                if (this.bTicketRequired) {
                    this.url =
                        "/apex/CaseProcessRedirect_VF_HUM?CaseId=" +
                        this.recordId +
                        "&TabId=&TicketRequired=" +
                        this.bTicketRequired +
                        "&CaseComment=" +
                        CASE_COMMENT;
                } else {
                    this.url =
                        "/apex/CaseProcessRedirect_VF_HUM?CaseId=" +
                        this.recordId +
                        "&TabId=&TicketRequired=" +
                        this.bTicketRequired +
                        "&CaseComment=";
                }
                this.redirectToCasePage();
            }
        }
    }

    finishcheck(event) {
        this.bIsFlowFinished = true;
        this.loadGuidedFlow = false;
        if (
            event != null &&
            event.detail != null &&
            event.detail.outputParams != null
        ) {
            let outputParameters = event.detail.outputParams;
            if (outputParameters) {
                if (this.flowname === "pharmacyWebIssuesGuidedFlow") {
                    this.processPharmacyWebIssueFinish(outputParameters);
                }
            }
        }
    }
    redirectToCasePage() {
        // Navigate to a URL
        /*this[NavigationMixin.Navigate]({
            type: "standard__webPage",
            attributes: {
                url: this.url,
            },
        });*/
        openLWCSubtab('caseInformationComponentHum',this.recordId,{label:'Edit Case',icon:'standard:case'});
    }
    closeModal() {
        if (this.hasProcess) {
            this.loadGuidedFlow = false;
        } else {
            this.toggleHideFlowModalComponents();
            //used to prevent the exit confirmation modal from appearing when exiting the modal when a flow error occurs
            if (!this.didFlowFail) {
                this.isConfirmingFlowExit = true;
            } else {
                this.loadGuidedFlow = false;
            }
        }
    }

    get flowParamsJSON() {
        return JSON.stringify(this.flowParams.flowParams);
    }

    closeErrorMessage() {
        this.bDisplayErrorMessage = false;
    }

    toggleHideFlowModalComponents() {
        const flowModalComponents = this.template.querySelectorAll(
            ".flow-modal-component"
        );
        flowModalComponents.forEach(function (node) {
            node.classList.toggle("slds-hidden");
        });
    }

    handleFlowFailed() {
        this.didFlowFail = true;
    }

    handleCancelFlowExit() {
        this.toggleHideFlowModalComponents();
        this.isConfirmingFlowExit = false;
    }

    handleContinueFlowExit() {
        this.toggleHideFlowModalComponents();
        this.loadGuidedFlow = false;
        this.isConfirmingFlowExit = false;
    }
}