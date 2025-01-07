/*
Function        : LWC to display Pharmacy Web Issues flow launched from Pharmacy .

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Pinky Vijur                     03/01/2024                User Story 5428942: T1PRJ0865978- MF21712 Mail Order Management; Pharmacy- Guided Flow- Web Issues- Launch from pharmacy
*****************************************************************************************************************************/
import { LightningElement, track,api,wire } from 'lwc';
import { CurrentPageReference } from 'lightning/navigation';
import { openLWCSubtab,invokeWorkspaceAPI } from 'c/workSpaceUtilityComponentHum';
import { getLabels } from 'c/crmUtilityHum';

export default class PharmacyWebIssueFlowHum extends LightningElement {
    @api flowName;
    @api flowParams;
    @track webIssueProcessData = [];
    @track webIssueObj = {};
    @track recordId;
    @track webIssuesFinalScreen = false;
    @track filterCasesHavingTemplate = false;
    @track pharmacy = false;
    @track setSIDs = new Set();
    @track bShowModal = false;
    @track labels = getLabels();
    @track showHighlightPanel = true;
    @track hidePopover = true;
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
    }

    get flowParamsJSON() {
       return this.flowParams;
    }
 
    handleflowfinished(event) {
                this.recordId = JSON.parse((this.flowParams)).find(k=>k.name==="Case_ID").value
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
                    this.webIssueProcessData = this.webIssueProcessData.filter(k => k.processExist === true);
                    let existProcess = this.webIssueProcessData.find(k => k.SID === sidvalue);
                    if (existProcess === null || existProcess === undefined) {
                        this.webIssueProcessData.unshift({
                            label: "Web Issue " + (this.webIssueProcessData?.length + 1),
                            value: data.outputParams.find(k => k.name === 'WebIssues_Summary_Data').value,
                            SID: sidvalue,
                            processExist: true
                        });
                    }
                        this.displayLogging = true;
                        this.createLoggingData();
                        this.webIssuesFinalScreen = true;
                        this.flowName =  false;
                      //  this.closesubtab();
                }
        } 
    
    createLoggingData() {
        this.webIssueObj  = {
            header: 'Web Issue',
            data: this.webIssueProcessData,
            tablayout: true,
            source: 'pharmacyWebIssuesGuidedFlow',
            caseComment: 'Mail Order Pharmacy Web Issues process attached to the case and transferred to the Web Ticket team.',
            attachProcessToCase: true,
            headertype: 'info',
            redirecttocaseedit: true
        }
    }
    handleFinishLogging() {
       
        this.webIssuesFinalScreen = false;
        this.webIssueProcessData = [];
        this.setSIDs.clear();
        this.flowName = false;
       this.closesubtab();
    }
    closeModal() {
        this.bShowModal = false;
    }

    cancelTemplateCreation() {
        this.isFinished = false;
        this.closesubtab();
    }

    async closesubtab(){
        const focusedTab = await invokeWorkspaceAPI("getFocusedTabInfo");
      this.toFocusTab();
        await invokeWorkspaceAPI("closeTab", {
            tabId: focusedTab.tabId
          });
    }
 async toFocusTab(tabId) {
        try {
            await invokeWorkspaceAPI("focusTab", {
                tabId: tabId
            });
            this.refreshSubtab(tabId);
        } catch (error) {
            console.log("error in toFocusTab--", error);
        }
    }
    async refreshSubtab(tabId) {
        try {
            await invokeWorkspaceAPI("refreshTab", {
                tabId: tabId
            });
        } catch (error) {
            console.log('error in refresh tab function--', error);
        }
    }
    onCancel() {
        this.bShowModal = true;
    }
    handleCloseLoggingPopup(event){
        this.webIssuesFinalScreen = false;
    }
}