/*
LWC JS Name : InvoiceSummary.js
Function    : LWC component to display Ivoice Summary template 

Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Aishwarya Pawar                                         02/02/2023                REQ 4211868 - initial version 
* Pinky Vijur                                             03/13/2024                 User Story 5203789: T1PRJ0865978- MF21712 Mail Order Management; Pharmacy- Guided Flow- Web Issues- Launch button /link and existing flow
                                             
********************************************************************************************************************************
*/


import { LightningElement, api, track, wire } from 'lwc';
import { getRelatedListRecords } from 'lightning/uiRelatedListApi';
import { CurrentPageReference } from 'lightning/navigation';
export default class InvoiceSummary extends LightningElement {
    @api encodedData;
    @track templateId;
    @track templateName;
    @track responseDetails;
    @track templateData;
    @track error = false;
        

    @wire(CurrentPageReference)
      wiredPageRef(pageRef) {
          this.pageRef = pageRef;
          this.templateId = this.encodedData?.Id ?? null;
          this.templateName = this.encodedData?.templatename ?? '';
    }

    
    @wire(getRelatedListRecords, {
        parentRecordId: "$templateId",
        relatedListId: 'Template_Submission_Data__r',
        fields: ['Template_Submission_Data__c.Name__c', 'Template_Submission_Data__c.Value__c']
    }) listInfo({ error, data }) {
        if (data) {
            this.responseDetails = data.records;
            this.error = undefined;
            if (this.responseDetails && Array.isArray(this.responseDetails) && this.responseDetails.length > 0) {
                this.responseDetails.forEach(element => {
                    if(this.templateName.toLowerCase().includes('web issues')){
                    if(element?.fields?.Name__c?.value=='SubmissionData'){
                        this.templateData = element?.fields?.Value__c?.value ?? '';
                    }
                }
                else if(this.templateName.toLowerCase().includes('inactivate rx') || this.templateName.toLowerCase().includes('invoice request')){
                    this.templateData = element?.fields?.Value__c?.value ?? '';
                }
                });
            }
        } else if (error) {
            this.error = true;
            this.responseDetails = undefined;
            console.log(error);
        }
    }
}