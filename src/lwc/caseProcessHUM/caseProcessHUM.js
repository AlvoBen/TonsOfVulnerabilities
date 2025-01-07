/*******************************************************************************************************************************
LWC JS Name : caseProcessHUM.js
Function    : This JS serves as controller to caseProcessHUM.html. 

Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Santhi Mandava                                          07/22/2022                  initial version(Grnrtoc component to display case template US #3268999)
* Santhi Mandava                                          09/01/2022                  US3279633 QAA Template changes
* Muthukumar                                              09/08/2022                  US-3279519 update plan demographic
* Muthukumar                                              09/14/2022                  DF-6166 Fix 
* Bhakti Vispute										  11/02/2022				  US-3826151: Lightning- Templates Refresh Issue Changes
* Jasmeen Shangari										  02/15/2023				  US-4226309: Integrate Process Section on Case Edit
* Swapnali Sonawane                                       02/15/2023                  US 4178421 Determine the logic to display templates on New case page and attach case to the template on Launch
* Nirmal Garg										  	  03/02/2022				  DF-7300
* Nirmal Garg                                             04/12/2023                  US4460894
* Vardhman Jain                                           10/20/2023                   US-5009031 update Commercial demographic
*********************************************************************************************************************************/
import { LightningElement, api, track, wire } from 'lwc';
import { CurrentPageReference } from 'lightning/navigation';
import pubSubHum from 'c/pubSubHum';
import { invokeWorkspaceAPI } from "c/workSpaceUtilityComponentHum";
import { getLabels } from 'c/crmUtilityHum';
import removeTemplateSubmission from '@salesforce/apex/CaseProcessesLaunch_LC_Hum.removeTemplateSubmission';

export default class CaseProcessHUM extends LightningElement {
    @api flowDetails;
    @api recordId;
    @track error;
    @track flowname;
    @track flowParamsJSON;
    @track flowObj;
    @track showScreenFlow = false;
    @track pageRef;
    @track statePageRef;
    @track isQAATemplate;
    @track isPlanDemographic;
    @track ispcpUpdate;
    @track isCommercialDemographic;
    @track openModal = false;
    @track bShowModal = false;
    @track showHighlightPanel = true;
    @track labels = getLabels();
    @track isFinished = false;
    @track accountId;
    @track casPersonId;
    @track mtvPersonId;

    @track buttonsConfig = [{
        text: "Cancel",
        isTypeBrand: false,
        eventName: "close"
    }, {
        text: "Continue",
        isTypeBrand: true,
        eventName: "continue"
    }];

    @wire(CurrentPageReference)
    wiredPageRef(pageRef) {
        this.pageRef = pageRef;
        this.statePageRef = this.pageRef.state.c__pageRef;
        this.memId = this.pageRef?.state?.c__planMemberId ?? null;
        this.pageName = this.pageRef?.state?.c__pageName ?? null;
        this.accountId = this.pageRef?.state?.c__accountId ?? null;
    }

    connectedCallback() {
        this.flowObj = JSON.parse(this.flowDetails);
        removeTemplateSubmission({ caseId: this.recordId, isCancel: true })
            .then(result => {
                this.flowname = this.flowObj.templateName;
                this.flowParamsJSON = this.flowObj.flowParamsJSON;
                this.showScreenFlow = (this.flowObj.TemplateType == 'Screen Flow') ? true : false;
                this.casPersonId = this.flowObj?.CASPersonId ?? '';
                this.mtvPersonId = this.flowObj?.MTVPersonId ?? '';
                if (!this.showScreenFlow) {
                    this.setCustomTemplateVisibility(this.flowname);
                }
            })
            .catch(error => {
                this.error = error;
                console.log('Error : ', this.error);
            });
    }

    handleFinish(event) {
        try {
            this.isFinished = true;
            this.showScreenFlow = false;
            this.closeSubtab();
        }
        catch (error) {
            console.log('Error==', error);
        }
    }

    setCustomTemplateVisibility(templateName) {
        switch (templateName) {
            case 'QAA_Complaint':
                this.isQAATemplate = true;
                break;
            case 'Update_Plan_Demographics':
                this.showHighlightPanel = false;
                this.isPlanDemographic = true;
                break;
            case 'Update_Commercial_Demographics':
				this.showHighlightPanel = false;
                this.isCommercialDemographic = true;
                break;
            case 'PCP Update/Change':
                this.ispcpUpdate = true;
                break;
        }
    }

    onCancel() {
        this.bShowModal = true;
    }

    closeModal() {
        this.bShowModal = false;
    }

    closeSubtab() {
        try {

            pubSubHum.fireEvent(this.statePageRef, 'refreshCaseProcessSec', { 'flowName': this.flowname, 'isFinished': this.isFinished });
            invokeWorkspaceAPI('isConsoleNavigation').then(isConsole => {
                if (isConsole) {
                    invokeWorkspaceAPI('getFocusedTabInfo').then(focusedTab => {
                        invokeWorkspaceAPI('disableTabClose', {
                            tabId: focusedTab.tabId,
                            disabled: false
                        });
                        invokeWorkspaceAPI('closeTab', {
                            tabId: focusedTab.tabId
                        });
                    });
                }
            });
        }
        catch (error) {
            console.log('Error==', error);
        }
    }

    handleTemplateSaveSucess() {
        this.isFinished = true;
        this.closeSubtab();
    }

    cancelTemplateCreation() {
        this.isFinished = false;
        this.deletePCPUnsavedData();
        this.closeSubtab();
    }

    deletePCPUnsavedData() {
        if (this.template.querySelector('c-pcp-update-container') != null) {
            this.template.querySelector('c-pcp-update-container').deleteUnsavedData();
        }
    }
}