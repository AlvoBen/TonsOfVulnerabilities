/*******************************************************************************************************************************
Function    : This JS serves as controller to CaseTRRProcessDetails.html. 
Modification Log: 
Developer Name             Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Prasuna Pattabhi       02/01/2023                    Original Version
* Prasuna Pattabhi       02/17/2023                   Defect Fix Column Name change DF7196
*********************************************************************************************************************************/
import { LightningElement, api, track, wire } from 'lwc';
import hasTRRProcess from '@salesforce/apex/CaseProcessesLaunch_LC_Hum.hasTRRProcess';
import { invokeWorkspaceAPI,openSubTab } from "c/workSpaceUtilityComponentHum";

export default class CaseTRRProcessDetails extends LightningElement {

    @api encodedData;
    @track breadCrumbItems = [];
    @track templateData = [];
    @track columns = [
        { label: 'Process Number', fieldName: 'ProcessNumber', type: 'button',target:'_blank',
        typeAttributes: {label: { fieldName: 'ProcessNumber' }, name : 'ProcessNumber',variant:'base'}, hideDefaultActions: true},
        {label: 'Process Type', fieldName: 'ProcessType', hideDefaultActions: true }, 
        {label: 'Created Date', fieldName: 'CreatedDate', hideDefaultActions: true },       
        {label: 'Reply Code', fieldName: 'ReplyCode', hideDefaultActions: true },
        {label: 'Effective Date', fieldName: 'EffectiveDate', hideDefaultActions: true },
        {label: 'Processing Date Stamp', fieldName: 'ProcessedDate', hideDefaultActions: true }      
        
    ];
    @track openMed_Modal = false;
    @track selectedProcessName;
    @track templateId;
    @track recordId;    
    @track caseNumber;
    @track loadDetails = false;
    connectedCallback() {
        if(this.encodedData){
            this.recordId = this.encodedData.recordId;
        }
        this.caseNumber = 'Case > '+this.encodedData.caseNumber;
        this.breadCrumbItems = [
            { "label": 'Process History', "href": '', "eventname": '' },
            { "label": this.encodedData.caseNumber, "href": '', "eventname": '' }
        ];
        this.getTRRDataForProcessNumber();
    }

    getTRRDataForProcessNumber() {
        hasTRRProcess({ sCaseId: this.recordId,showAll:true,noOfRecords:0}).then(result => {
            this.templateData = JSON.parse(result.data);
            this.count = result.totalNoOfProcesses;
            this.loadDetails = true;
        }).catch(error => {
            this.error = error;
        });
    }

    handleRowAction(event){
        let templateData = event.detail.row;
        this.templateId = templateData.Id;
        this.selectedProcessName = templateData.ProcessNumber;
        let tempData = {
            templateName : this.selectedProcessName,
            templateId : this.templateId,
            recordId : this.recordId
        };
        let componentDef = {
            componentDef: "c:displayTRRDetailsLC",            
            attributes: {encodedData: tempData}
        };            
        let encodedComponentDef = btoa(JSON.stringify(componentDef));
        let url =  "/one/one.app#" + encodedComponentDef;
        invokeWorkspaceAPI("getFocusedTabInfo").then(focusedTab => {
            invokeWorkspaceAPI("openSubtab", {
                parentTabId: focusedTab.parentTabId!=null?focusedTab.parentTabId:focusedTab.tabId,                
                url: url,                
                focus: true
            }).then(newTabId=>{
                let tabLabel = this.selectedProcessName;
                invokeWorkspaceAPI("setTabLabel", {tabId: newTabId,label:tabLabel });
                invokeWorkspaceAPI("setTabIcon",{tabId: newTabId,icon: 'standard:process',iconAlt:''});
            });
        });
    }
    
    childCloseEvent(event){
        this.openMed_Modal  = event.detail;
    }

    goBackToCase(event){
        event.preventDefault();
        this.onHyperLinkClick();        
      }

     onHyperLinkClick(event){
        let data = {title: 'Case',nameOfScreen:'Case'};
        let pageReference = {
            type:'standard__recordPage',
            attributes:{
                recordId:this.recordId,
                objectApiName:'case',
                actionName:'view'
            }
        } 
        openSubTab(data, undefined, this, pageReference, {openSubTab:true,isFocus:true,callTabLabel:false,callTabIcon:false});
    }
}