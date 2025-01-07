/*
LWC Name        : pcpSummaryScreenHum.html
Function        : LWC to display pcp provider search screen.

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Nirmal Garg                     09/26/2022                 initial version US2961209
****************************************************************************************************************************/

import { LightningElement, api, track } from 'lwc';
import { getTemplateDataDetails } from 'c/genericTemplateDataCreationHum';
import { getModel } from './layoutConfig';
const SF_QUESTION = "Did you receive a Service Fund edit when attempting to change the member's PCP";
export default class PcpSummaryScreenHum extends LightningElement {
    @api encodedData;
    @track tempSubmissionId;
    @track templateName;
    @track templateModel = getModel('templateDataModel');
    @track templateData;
    @track pcpQuestions = [];
    @track pcpPhysicianSearch = [];
    @track pcpServiceFundQuestions = [];
    @track displaySFSection = false;
    @track PCPQuestionsData = [];
    @track PCPProviderSearchData = [];
    @track PCPServiceFundData = [];
    @track displayPCPQuestionScreen=false;
    connectedCallback() {
        if (this.encodedData && this.encodedData?.tempSubmissionId) {
            this.tempSubmissionId = this.encodedData?.tempSubmissionId??'';
            this.templateName = this.encodedData?.templateName??'';
        }
        if (this.tempSubmissionId) {
            this.getTemplateData();
        }
    }

    getTemplateData() {
        getTemplateDataDetails(this.tempSubmissionId)
            .then(result => {
                if (result && Array.isArray(result) && result.length > 0) {
                    this.templateData = result;
                    this.ProcessPCPProviderSearchData();
                    this.ProcessPCPQuestionsData();
                } else {
                    console.log('No data')
                }
            }).catch(error => {
                console.log(error);
            })
    }

    ProcessPCPQuestionsData() {
        this.PCPQuestionsData = [];
        if (this.templateModel && Array.isArray(this.templateModel) && this.templateModel.length > 0) {
            this.templateModel.forEach(k => {
                if (k && k?.Screen__c && k.Screen__c.toLocaleLowerCase() === 'pcpquestion') {
                    if (this.templateData && Array.isArray(this.templateData) && this.templateData.length > 0) {
                        let data = this.templateData.find(h => h?.Name__c.toLocaleLowerCase() === k.Template_Field_Name__c.toLocaleLowerCase());
                        if (data && data?.Value__c) {
                          this.displayPCPQuestionScreen = true;
                            this.PCPQuestionsData.push({
                                label: k.label,
                                value: data.Value__c,
                                order: k.Order__c
                            })
                        }
                    }
                }
            })
        }
        if (this.PCPQuestionsData && this.PCPQuestionsData.length > 0) {
            this.PCPQuestionsData.sort((a, b) => {
                return a.order > b.order ? 1 : -1;
            })
        }

    }

    getUniqueId() {
        return Math.random().toString(16).slice(2);
    }

    ProcessPCPProviderSearchData() {
        this.PCPProviderSearchData = [];
        let providername='';
        let provideraddress='';
        if (this.templateModel && Array.isArray(this.templateModel) && this.templateModel.length > 0) {
            this.templateModel.forEach(k => {
                if (k && k?.Screen__c && k.Screen__c.toLocaleLowerCase() === 'pcpprovidersearch') {
                    if (this.templateData && Array.isArray(this.templateData) && this.templateData.length > 0) {
                        let data = this.templateData.find(h => h?.Name__c.toLocaleLowerCase() === k.Template_Field_Name__c.toLocaleLowerCase());
                        if (data && k?.Display) {
                            this.PCPProviderSearchData.push({
                                Id: this.getUniqueId(),
                                label: k.label,
                                value: data.Value__c,
                                order: k.Order__c
                            })
                        }else if(k?.JoinField && k?.JoinField.toLocaleLowerCase()==='name'){
                            providername = data?.Value__c ?? '';
                        }else if(k?.JoinField && k?.JoinField.toLocaleLowerCase()==='address'){
                            provideraddress = data?.Value__c ?? '';
                        }
                    }
                }
            })
        }
        this.PCPProviderSearchData.push({
            label : 'Name and Address of the new Physician selected',
            value : `${providername} ,${provideraddress}`,
            order : '4'
        })
        if (this.PCPProviderSearchData && this.PCPProviderSearchData.length > 0) {
            this.PCPProviderSearchData.sort((a, b) => {
                return a.order > b.order ? 1 : -1;
            })
        }
        let sfquestion = this.templateData.find(k => k?.Name__c.toLocaleLowerCase() === SF_QUESTION.toLocaleLowerCase());
        if (sfquestion && sfquestion?.Value__c.toLocaleLowerCase() === 'yes') {
            this.displaySFSection = true;
            this.ProcessPCPServiceFundData();
        }

    }

    ProcessPCPServiceFundData() {
        this.PCPServiceFundData = [];
        if (this.templateModel && Array.isArray(this.templateModel) && this.templateModel.length > 0) {
            this.templateModel.forEach(k => {
                if (k && k?.Screen__c && k.Screen__c.toLocaleLowerCase() === 'pcpservicefund') {
                    if (this.templateData && Array.isArray(this.templateData) && this.templateData.length > 0) {
                        let data = this.templateData.find(h => h?.Name__c.toLocaleLowerCase() === k.Template_Field_Name__c.toLocaleLowerCase());
                        if (data) {
                            this.PCPServiceFundData.push({
                                label: k.label,
                                value: data.Value__c,
                                order: k.Order__c
                            })
                        }
                    }
                }
            })
        }
        if (this.PCPServiceFundData && this.PCPServiceFundData.length > 0) {
            this.PCPServiceFundData.sort((a, b) => {
                return a.order > b.order ? 1 : -1;
            })
        }

    }
}