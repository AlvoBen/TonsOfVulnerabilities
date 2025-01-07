/******************************************************************************************************************************
LWC Name        : archivedCaseCommentsTableHum.js
Function        : LWC to display archived case comments

Modification Log:
* Developer Name                                Date                          Description
*------------------------------------------------------------------------------------------------------------------------------
* Ashish Kumar/Kajal Namdev                     07/18/2022                    Original Version 
******************************************************************************************************************************/

import { LightningElement, api, track } from 'lwc';
import fetchCaseDetailResponse from '@salesforce/apexContinuation/ArchivedCaseDetail_LC_HUM.fetchCaseDetailData';
import fetchLabels from '@salesforce/apex/ArchivedCaseHistory_LC_HUM.fetchLabels';

export default class archivedCaseDetailContainerHum extends LightningElement {
    @api recordId;
    @api isClassic = false;
    @api encodedData;
    @track caseHistoryResponse;
    @track taskResponse;
    @track caseComments;
    @track caseData;
    @track load = false;
    @track customLabels;
    connectedCallback() {
        fetchLabels()
        .then((result) => {
            this.customLabels = result;
        }).catch(error =>{

        })
        fetchCaseDetailResponse({ caseId: this.encodedData, ObjectName: 'All', StartRow: '1', EndRow: '50' })
            .then((result) => {
                if (result.sError) {
                    this.showPageMsg(false, result.sErrorMsg);
                } else {
                    this.load = true;
                    this.caseData = result.CaseDetailResponse;
                    this.taskResponse = result.CaseTasksResponse;
                    this.caseHistoryResponse = result.CaseHistoryResponse;
                    this.caseComments = result.CaseCommentsResponse;
                }
            }).catch(error => {
                console.log("error detail--", JSON.stringify(error));
            })
    }
    /**
    * show top page error msge
    * @param {*} isValid - true means there is no any kind of error present on page after verify all validations criteira
    *  @param {*} headerMsg - the top header error msge
    */
    showPageMsg(isValid, headerMsg) {
        const errHeader = this.template.querySelector(".page-error-message");
        if (!isValid) {
            errHeader.innerHTML =
                '<div class="slds-m-horizontal_small slds-p-around_small hc-error-header" style="color:white; font-size:20px; background: #c23934;border-radius:0.3rem">Review the error on this page.</div>' +
                '<p class="slds-p-horizontal_large slds-p-vertical_small" style="color:#c23934; font-size: 13px;">' +
                headerMsg +
                "</p>";
        } else {
            errHeader.innerHTML = " ";
        }
    }
}