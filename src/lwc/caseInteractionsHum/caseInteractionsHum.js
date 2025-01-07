/*******************************************************************************************************************************
LWC JS Name : CaseInteractionsHum.js
Function    : This JS serves as helper to CaseInteractionsHum.html 

Modification Log: 
Developer Name           Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
* Gowthami Thota                                          10/10/2022                   initial version
* Prasuna Pattabhi                                        03/02/2023                   US_4290170 and US_4286921
* Prasuna Pattabhi                                        03/07/2023                   US_4290170 and US_4286921 - Missing Code Added
* Prasuna Pattabhi                                        03/20/2023                   DF7403 Fix
* Gowthami Thota                                          03/24/2020                   DF7431 Fix
*********************************************************************************************************************************/
import { LightningElement, track, api, wire} from 'lwc';
import getInteractions from '@salesforce/apex/CaseInteraction_LC_HUM.getInteractions';
import verifyLegacyDelete from '@salesforce/apex/CaseInteraction_LC_HUM.verifyLegacyDelete';
import { openSubTab } from 'c/workSpaceUtilityComponentHum';
import { getLabels } from 'c/customLabelsHum';
import {getRecord} from 'lightning/uiRecordApi';
import CASE_NUMBER from '@salesforce/schema/Case.CaseNumber';
import HUMNoRecords from "@salesforce/label/c.HUMNo_records_to_display";
import saveInteraction from '@salesforce/apex/CaseInteraction_LC_HUM.saveInteraction';
import getCaseData from '@salesforce/apex/CaseInteraction_LC_HUM.getCaseData';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';

export default class CaseInteractionsHum extends LightningElement {
    @api recordId;
    @api issubtab;
    @api recordslength;

    intDataList = [];
    intListSize;
    totalRecords;
    @track noData = false;
    viewAll = false;
    enableNewInteraction = false;
    goTOCaseLabel;
    caseNmbr;
    caseStatus;
    @track oViewAllParams;
    @track labels = getLabels();
	@track isNewInteraction = false;
    @track hasError = false;
    @track errorMsg = '';
    @track interactionLayout = {};
    @track interaction= {};
	@track isNewCaseInteraction = false;
    @track hasCustomError = false;
    @track buttonlabel = '';
    @track caseId;
    @track errMessage = '';
    columns = [{
            label: 'Interaction Number',
            fieldName: 'IntUrl',
            type: 'url',
            typeAttributes: { label: { fieldName: 'InteractionNumber' }, tooltip: { fieldName: 'InteractionNumber' },},
            hideDefaultActions: true
        },
        { label: 'Interaction Origin', fieldName: 'InteractionOrigin', type: 'text', hideDefaultActions: true },
        { label: 'Interacting With', fieldName: 'InteractionWith', type: 'text', hideDefaultActions: true },
        { label: 'Interacting With Type', fieldName: 'InteractionWithType', type: 'text', hideDefaultActions: true },
        { label: 'Created Date', fieldName: 'CreatedDate', type: 'text', hideDefaultActions: true },
        {
            label: 'Created By',
            fieldName: 'CreatedByUrl',
            type: 'url',
            typeAttributes: { label: { fieldName: 'CreatedByName' }, tooltip: { fieldName: 'CreatedByName' },},
            hideDefaultActions: true
        },
        { label: 'Created By Queue', fieldName: 'CreatedByQueue', type: 'text', hideDefaultActions: true },
        { label: 'Associated to Case Date', fieldName: 'AssociatedtoCaseDate', type: 'text', hideDefaultActions: true },
    ];

    connectedCallback() {
		this.caseId = this.recordId;
        this.getInteractionRecs();
        this.disableInteractionButton();
    }
    //wire to get case number
    @wire(getRecord, { recordId: '$recordId', fields: [CASE_NUMBER] })
   
    wireCaseNumber({error,data}) {
        if (error) {
        this.error = error ; 
        } else if (data) {
                this.caseNmbr = data.fields.CaseNumber.value;
                console.log('casenumber in wire', this.caseNmbr);
                this.goTOCaseLabel = 'Case > '+this.caseNmbr;
        }
    }
    // Function to get all theinteraction records associated with the case
    getInteractionRecs() {
        
        try {
            getInteractions({ sCaseRecordId: this.recordId })
                .then(result => {
                    if (result != null && result != undefined) {
                        result = JSON.parse(result);
                        let tempIntData = [];

                        for (var i = 0; i < result.length; i++) {
                            let item = {};
                            item.InteractionNumber = result[i].sInteractionNumber.split(',')[0],
                                item.IntUrl = '/lightning/r/Interaction__c/' + result[i].sInteractionId + '/view',
                                item.InteractionOrigin = result[i].sInteractionOrigin,
                                item.InteractionWith = result[i].sInteractionWith,
                                item.InteractionWithType = result[i].sInteractionWithType,
                                item.CreatedDate = result[i].sCreatedDate,
                                item.CreatedByUrl = '/' + result[i].sCreatedBy.split(',')[2],
                                item.CreatedByName = result[i].sCreatedBy.split(',')[0],
                                item.CreatedByQueue = result[i].sCreatedByQueue;
                            item.AssociatedtoCaseDate = result[i].sAssociatedtoCaseDate;
                            tempIntData.push(item);
                        }

                        if (this.recordslength != false) {
                            this.intDataList = [];
                            if (result.length > 0) {
                                this.recordslength = true;
                            }
                            if (result.length > 6) {
                                this.totalRecords = '6+'
                                for (var i = 0; i < 6; i++) {
                                    this.intDataList.push(tempIntData[i]);
                                }
                            } else {
                                this.totalRecords = result.length;
                                this.intDataList = tempIntData;
                            }
                        } else if (this.recordslength == false) {
                            this.intDataList = [];
                            this.intDataList = tempIntData;
                            this.totalRecords = this.intDataList.length;
                        }
                        this.oViewAllParams = {
                            sRecordId: this.recordId,
                            sRecordsLength: false
                        }
                        // to display no records to display message
                        if(this.intDataList.length < 1){
                            this.noData=true;
                            this.NoRecordsToDosplayMsg=HUMNoRecords;
                        }else{
                            this.noData=false;    
                        }
                    } 
                })
        } catch (error) {
            console.log(error);
        }
    }
    //This method is to check whether New Interaction button should be disabled.  
    async disableInteractionButton(){
        let legacyDelete =  await verifyLegacyDelete ({recordId: this.recordId})
        if(legacyDelete == true ){
            this.enableNewInteraction=true;
        }   
    }
    async onViewAllClick(evnt) {
        openSubTab({
            nameOfScreen: 'CaseInteractions',
            title: 'Interactions',
            oParams: {
                ...this.oViewAllParams
            },
            icon: 'standard:interaction',
        }, undefined, this);
    }
    goBackToCase(event) {
        event.preventDefault();
        this.onHyperLinkClick();

    }
    onHyperLinkClick(event) {
        let data = { title: 'Case', nameOfScreen: 'Case' };
        let pageReference = {       
            type:   'standard__recordPage',
                   attributes:  {            recordId:  this.recordId,            objectApiName:   'case',            actionName:   'view'        }
        }

        openSubTab(data, undefined, this, pageReference, { openSubTab: true, isFocus: true, callTabLabel: false, callTabIcon: false });
    }
	newCaseInteraction() {
        this.isNewCaseInteraction = true;
    }

    handleSave(event) {
        this.buttonlabel = event.target.label;
        let InteractionId = this.template.querySelector('[data-id="InteractionId"]');
        let CaseId = this.template.querySelector('[data-id="CaseId"]');
        InteractionId.reportValidity();
        CaseId.reportValidity();
        if ((InteractionId.value == '' || InteractionId.value == null || InteractionId.value == undefined) || (CaseId.value == '' || CaseId.value == null || CaseId.value == undefined)) {
            this.hasCustomError = true;
            this.errMessage = 'Required fields are highlighted in red.';
        }
    }

    handleError(event) {
        if (event.detail.detail != '') {
            this.hasCustomError = true;
            this.errMessage = event.detail.detail;
        }
    }

    handleSuccess(event) {
        this.hasCustomError = false;
        this.errMessage = '';
        if (this.buttonlabel == 'Save & New') {
            this.isNewCaseInteraction = true;
        } else {
            this.isNewCaseInteraction = false;
        }
        this.caseId = this.recordId;
        let InteractionId = this.template.querySelector('[data-id="InteractionId"]');
        InteractionId.value = '';
        const evt = new ShowToastEvent({
            title: "success",
            message: "Interaction is successfully associated to case.",
            variant: "success"
        });
        this.dispatchEvent(evt);
        this.getInteractionRecs();
    }

    handleCancel(event) {
        this.isNewCaseInteraction = false;
        this.hasCustomError = false;
        this.errMessage = '';
    }
    closeModal() {
            this.isNewCaseInteraction = false;
            this.hasCustomError = false;
            this.errMessage = '';
        }
	handleNewInteractionChange(event){
        if(event && event.target){
            this.interaction[event.target.fieldName] = event.target.value;
        }
    }
	handleCallerNameChange(event) {
        const callerName = this.template.querySelector("[data-id='callerName']");
        this.interaction.Caller_Name__c = callerName.value;
    }
    createNewInteraction(){
		this.interaction = {};
        getCaseData({caseId:this.recordId}).then(result=> {
            this.interaction.Interaction_Origin__c = result.origin;
            this.interaction.Interacting_With_type__c = result.interactingWithType;
            this.interaction.Interacting_With__c = result.interactingWith;
            this.interactionLayout ={
                fields: [
                  { label: 'Interaction Origin', mapping: 'Interaction_Origin__c',required: true,value:this.interaction.Interaction_Origin__c,input:true},       
                  { label: 'Interaction With Type',  mapping: 'Interacting_With_type__c',required: true,value:this.interaction.Interacting_With_type__c,input:true},
                  { label: 'Interaction With', mapping: 'Interacting_With__c',value:this.interaction.Interacting_With__c,input:true},       
                  { label: 'Interaction With Name', mapping: 'Caller_Name__c',value:'',identifier:'callerName',inputWithLength:true}                       
                ]
            };
            this.isNewInteraction = true;
        }).catch(error=>{});        

    }
    closeNewInteractionModal(){
        this.isNewInteraction = false;
        this.hasError = false;
        this.errorMsg = '';
    }

    saveInteraction(){
        this.hasError = false;
        this.errorMsg = '';
        let interactionData = JSON.stringify(this.interaction);
        saveInteraction({interactionData:interactionData,caseId:this.recordId})
            .then(result=>{
                if(result != undefined && result != null && result.isSuccess == "true"){
                    this.interaction.interactionId = result.interactionId;
                    let pageReference = {
                        type:  'standard__recordPage',
                        attributes: { 
                            recordId: this.interaction.interactionId, 
                            objectApiName: 'Interaction__c',
                            actionName:'view'
                        }
                }  
                this.getInteractionRecs();
                const evt = new ShowToastEvent({
                    title: "success",
                    message: "Interaction is created and assocaited to case successfully.",
                    variant: "success"
                });
                this.dispatchEvent(evt);
                let data = { title: 'Case', nameOfScreen: 'Case' };          
                openSubTab(data, undefined, this, pageReference, { openSubTab: true, isFocus: true, callTabLabel: false, callTabIcon: false });                
                this.isNewInteraction = false;
            }else{
                this.hasError = true;
                this.errorMsg = result.errorMsg;
            }            
        });
    }
}