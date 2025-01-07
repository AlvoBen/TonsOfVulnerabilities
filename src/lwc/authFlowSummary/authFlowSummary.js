/*
LWC JS Name : AuthFlowSummary.js
Function    : LWC component to display Auth Summary template 

Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Aishwarya Pawar                                         12/26/2022                 initial version(REQ 4028814)
                                              
********************************************************************************************************************************
*/
import { LightningElement, api, track } from 'lwc';
import getdatafromTemplateSubmission from '@salesforce/apex/CaseProcessesLaunch_LC_Hum.getdatafromTemplateSubmission';


export default class AuthFlowSummary extends LightningElement {
    @api encodedData;
    @track templateId;
    @track templateName;
    @track responseDetails;
    @track templateData;
    @track error = false;

    connectedCallback() {
        if (this.encodedData && this.encodedData?.Id) {
            this.templateId = this.encodedData?.Id ?? '';
           
            this.templateName = this.encodedData?.templatename ?? '';
        }
        if (this.templateId && this.templateId.length > 0) {
            this.getTemplateData();
        }
    }
    
    getTemplateData() {
        getdatafromTemplateSubmission({ sTemplateName: this.templateName, templateId: this.templateId })
            .then(result => {
                this.responseDetails = (result && result?.lstTemplateSubmittionData) ? result?.lstTemplateSubmittionData : null;
                if (this.responseDetails && Array.isArray(this.responseDetails) && this.responseDetails.length > 0) {
                    this.responseDetails.forEach(element => {
                        this.templateData = element?.Value__c ?? '';
                    });
                }
            })
            .catch(error => {
                this.error = true;
                console.log('error while getting template data: ' + JSON.stringify(error));
            });
    }
}