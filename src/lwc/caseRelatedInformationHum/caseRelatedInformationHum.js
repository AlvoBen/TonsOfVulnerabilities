/*******************************************************************************************************************************
LWC JS Name : CaseRelatedInformationHum.js
Function    : This JS serves as helper to CaseRelatedInformationHum.html 

Modification Log: 
Developer Name           Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
* Gowthami Thota                                          10/10/2022                   initial version
*********************************************************************************************************************************/
import { LightningElement, track, api } from 'lwc';
import getCaseRelatedInfo from '@salesforce/apex/CaseRelatedInfo_LC_HUM.getCaseRelatedInfo';
import getCaseNumber from '@salesforce/apex/CaseRelatedInfo_LC_HUM.getCaseNumber';
import { openSubTab } from 'c/workSpaceUtilityComponentHum';
import { getLabels } from 'c/customLabelsHum';
export default class CaseRelatedInformationHum extends LightningElement {
    @api recordId;
    @api recordslength;
    @api breadCrumbItems;
    @api bInfiniteScroll = false;
    @api issubtab;
    
    totalRecords;
    displayAccordianData = false;
    noLogData = false;
    @track loggeddata = [];
    @track displayLogData = [];
    loggedItem = [];
    goTOCaseLabel;
    caseNumber;
    @track labels = getLabels();
    @track oViewAllParams;
    logColumns = [
        { label: "Source" },
        { label: "Type" },
        { label: "Attachment ID" },
        { label: "Created Date" },
        { label: 'Created By' }
    ];
    connectedCallback() {
        this.getCCPGCPAttachments();
        this.getCaseNo();
    }
    async getCaseNo(){
        let getCase =  await getCaseNumber ({recordId: this.recordId})
        this.caseNumber =getCase;
        this.goTOCaseLabel = 'Case > '+this.caseNumber;
    }
    getCCPGCPAttachments() {
        try {
            getCaseRelatedInfo({ sCaseId: this.recordId })
                .then(result => {
                    if (result != null && result != undefined) {
                        result = JSON.parse(result);
                        let tempLogData = [];
                        if(result.length>0){
                            for (var i = 0; i < result.length; i++) {
                                let item = {};
                                item.sourceSys = result[i].sourceSys;
                                item.attachmentType = result[i].attachmentType;
                                item.attachmentId = result[i].attachmentId;
                                item.loggedDateTime = result[i].loggedDateTime;
                                item.loggedBy = result[i].loggedBy;
                                item.sCaseNumber = result[i].sCaseNumber;
                                item.Name = result[i].Name;
                                item.Value = result[i].Value;
                                tempLogData.push(item);
                            }
                            if(this.recordslength != false){
                                this.loggeddata = [];
                                if (result.length > 0) {
                                    this.recordslength = true;
                                }
                                if (result.length > 6) {
                                    this.totalRecords = '6+'
                                    for (var i = 0; i < 6; i++) {
                                        this.loggeddata.push(tempLogData[i]);
                                    }
                                }
                                else {
                                    this.totalRecords = result.length;
                                    this.loggeddata = tempLogData;
                                }
                            }
                            else if (this.recordslength == false){
                                this.loggeddata = [];
                                this.loggeddata = tempLogData;
                                this.totalRecords = this.loggeddata.length; 
                            }
                            this.oViewAllParams = {
                                sRecordId: this.recordId,
                                sRecordsLength: false
                            }
                        }
                        else{
                            this.noLogData = true;
                            this.totalRecords = result.length;
                        }
                    }
                })
        }
        catch (error) {
            console.log(error);
        }
    }
    handleAccordian(event) {
        if (event.target.iconName === 'utility:chevronright') {
            this.displayAccordianData = true;
            event.target.iconName = 'utility:chevrondown'
        } else if (event.target.iconName === 'utility:chevrondown') {
            this.displayAccordianData = false;
            event.target.iconName = 'utility:chevronright'
        }
        const rowId = event.target.dataset.id;
        let target = this.template.querySelector(`c-related-info-log-item-hum[data-id="${rowId}"]`);
        if (this.displayAccordianData == true) {
            target.classList.remove('slds-hide');
            target.classList.add('slds-show');
        }
        else if (this.displayAccordianData == false) {
            target.classList.remove('slds-show');
            target.classList.add('slds-hide');
        }
    }
    async onViewAllClick(evnt) {
        openSubTab({
            nameOfScreen: 'RelatedInformation',
            title: 'Related Information',
            oParams: {
                ...this.oViewAllParams
            },
            icon: 'standard:case',
        }, undefined, this);
    }
    goBackToCase(event){
        event.preventDefault();
        this.onHyperLinkClick();
        
    }
    onHyperLinkClick(event){
        let data = {title: 'Case',nameOfScreen:'Case'};
        let pageReference = {
               type: 'standard__recordPage',
               attributes: {
                   recordId: this.recordId,
                   objectApiName: 'case',
                   actionName: 'view'
               }
        } 
    
        openSubTab(data, undefined, this, pageReference, {openSubTab:true,isFocus:true,callTabLabel:false,callTabIcon:false});
    }

}