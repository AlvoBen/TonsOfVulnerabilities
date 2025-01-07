/*******************************************************************************************************************************
LWC JS Name : linkedCaseDetails.js
Function    : This JS serves as controller to linkedCaseDetails.html. 
Modification Log: 
Developer Name                       Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
* Swetha Thuti                      12/10/2021                    Initial Version
*********************************************************************************************************************************/
import { LightningElement, track, api, wire } from 'lwc';
import getLinkedCaseList from '@salesforce/apex/CaseHistoryComponent_LC_HUM.getLinkedCaseList';
export default class LinkedCaseDetails extends LightningElement {
    @track linkedCaseList = [];
    @track nolinkedCases = true;
    @track caseNumber;
    caseId;


    @api
    showCase(caseNumber, caseId) {
        this.caseNumber = caseNumber
        this.caseId = caseId;
        console.log('From Linked Case Details Component : ');
        console.log('Case ID : ' + caseId);
        var letBaseUrl = window.location.origin + '/';
        getLinkedCaseList({ caseId: caseId })
            .then((response) => {
                console.log(response);
                let dataList = JSON.parse(response);
                let data = dataList.data;
                if (data && Array.isArray(data) && data.length > 0) {
                    console.log(JSON.stringify(data));
                    this.linkedCaseList = data.map(elt => {
                        console.log('Element', elt);
                        let obj = {};
                        obj.caseNumber = elt.sCaseNumber;
                        obj.linkedBy = elt.sInfo;
                        obj.linkedWorkQueue = elt.sWorkQueue;
                        obj.caseUrl = letBaseUrl + elt.sCaseId;
                        return obj;
                    });
                    this.nolinkedCases = false;
                    console.log('Post dataPreparation : ' + JSON.stringify(this.linkedCaseList));
                } else {
                    this.nolinkedCases = true;
                    this.caseNumber = 'Linked Cases';
                }
            })
            .catch((e) => console.log('Error', e));
    }

    @api
    refreshCaseLinks() {
        var letBaseUrl = window.location.origin + '/';
        if (!this.caseId) {
            return;
        } else {
            getLinkedCaseList({ caseId: this.caseId })
                .then((response) => {
                    console.log(response);
                    let dataList = JSON.parse(response);
                    let data = dataList.data;
                    if (data && Array.isArray(data) && data.length > 0) {
                        console.log('From Apex : '+JSON.stringify(data));
                        this.linkedCaseList = data.map(elt => {
                            console.log('Element', elt);
                            let obj = {};
                            obj.caseNumber = elt.sCaseNumber;
                            obj.linkedBy = elt.sInfo;
                            obj.linkedWorkQueue = elt.sWorkQueue;
                            obj.caseUrl = letBaseUrl + elt.sCaseId;
                            return obj;
                        });
                        this.nolinkedCases = false;
                        console.log('Post dataPreparation : ' + JSON.stringify(this.linkedCaseList));
                    } else {
                        this.nolinkedCases = true;
                        this.caseNumber = 'Linked Cases';
                    }
                })
                .catch((e) => console.log('Error', e));
        }
    }


}