/*
JS Controller        : sendPrintItemContainerHum
Version              : 1.0
Created On           : 10/12/2022
Function             : Component to display Send Print Item Template.

Modification Log:
* Developer Name                    Date                         Description
* Abhishek Mangutkar               10/12/2022                    US-3837985 RTI - attach to case upon resend  functionality
*------------------------------------------------------------------------------------------------------------------------------
*/

import { LightningElement, api, track } from 'lwc';
import getdatafromTemplateSubmission from '@salesforce/apex/CaseProcessesLaunch_LC_Hum.getdatafromTemplateSubmission';

export default class SendPrintItemContainerHum extends LightningElement {

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
        if (this.templateId) {
            this.getTemplateData();
        }
    }
    
    getTemplateData() {
        getdatafromTemplateSubmission({ sTemplateName: this.templateName, templateId: this.templateId })
            .then(result => {
                if (result) {
                    this.responseDetails = result;
                    this.responseDetails.lstTemplateSubmittionData.forEach(element => {
                        if (element.Name__c == 'Data') {
                            this.templateData = element.Value__c;
                        }
                    });
                }
            })
            .catch(error => {
                this.error = true;
                console.log('error while getting template data: ' + JSON.stringify(error));
            });
    }
}