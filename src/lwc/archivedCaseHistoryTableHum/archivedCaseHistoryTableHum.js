/******************************************************************************************************************************
LWC Name        : ArchivedCaseHistoryTableHum.js
Function        : LWC to display archived case history

Modification Log:
* Developer Name                                Date                          Description
*------------------------------------------------------------------------------------------------------------------------------
* Ashish Kumar/Kajal Namdev                     07/18/2022                    Original Version 
******************************************************************************************************************************/
import { LightningElement, track, api } from 'lwc';
import { getCaseHistoryFormLayout } from './layoutConfig';
import fetchCaseDetailResponse from '@salesforce/apexContinuation/ArchivedCaseDetail_LC_HUM.fetchCaseDetailData';
import fetchLabels from '@salesforce/apex/ArchivedCaseHistory_LC_HUM.fetchLabels';

export default class ArchivedCaseHistoryTableHum extends LightningElement {
    @api isClassic = false;
    @api recordId;
    @api caseHistoryResponse = {};
    @api viewAllCaseHistory;
    @api customLabels;
    @track caseHistoryData = [];
    @track caseHistoryModel;
    @track showViewAll = true;
    @track oViewAllParams = {};
    @track totalRecordCount = 0;
    @track CONSTANTS;

    connectedCallback() {
        fetchLabels().then((result)=> {
            this.CONSTANTS = result;
        })
        this.caseHistoryModel = getCaseHistoryFormLayout();
        try {
            // below if is added to open subtab for case history records if user clicked on View All else it will display history component on case detail tab only.
            if (this.viewAllCaseHistory) {
                fetchCaseDetailResponse({ caseId: this.recordId, ObjectName: 'CaseHistory', StartRow: '1', EndRow: '1000'})
                    .then((result) => {
                        if(result.CaseHistoryResponse.CaseHistoryResponseData)
                        {
                            this.caseHistoryData = result.CaseHistoryResponse.CaseHistoryResponseData;
                            this.totalRecordCount = this.caseHistoryData.length;
                        }
                    }).catch(error => {
                        console.log("error--", JSON.stringify(error));
                    })
                this.showViewAll = false;
                let oParams = {sRecordId:this.recordId, isClassic: this.isClassic};
                this.oViewAllParams = {
                    sOptions: {
                        sAppName: 'ArchivalCaseHistory'//'ArchivalCaseHistory',
                    },
                    oParams: oParams
                }
            }
            else {
                if(this.caseHistoryResponse.CaseHistoryResponseData)
                {
                    this.caseHistoryData = this.caseHistoryResponse.CaseHistoryResponseData;
                    this.totalRecordCount = this.caseHistoryResponse.Header.sTotalRows > 25 ? 25+'+' : this.caseHistoryData.length;
                    let oParams = {sRecordId:this.recordId, viewAllData : true, isClassic: this.isClassic}
                    this.oViewAllParams = {
                        sOptions: {
                            sAppName: this.customLabels.HUMArchival_appName, //'ArchivalCaseHistory',
                            sArchivalScreen: this.customLabels.HUMArchival_screenNameDetail//'ArchivalDetail'
                        },
                        nameOfScreen: this.customLabels.HUMArchival_NameOfScreenHistory,//'Case History',
                        oParams: oParams,
                        sRecordId: this.recordId,
                        viewAllData: true,
                        sArchivalMinRecords: this.customLabels.HUMArchival_ARCHIVALDETAIL_MIN_REC,//hcConstants.ARCHIVALDETAIL_MIN_REC,  //check if we need to convert it in integer.
                        sArchivedCaseSearch: false,
                        sTotalRowCount:  this.caseHistoryResponse.Header.sTotalRows
                    };              
                  }
            }

        } catch (error) {
            console.log('Error ==> ', JSON.stringify(error));
        }
    }
    renderedCallback(){
        if(this.template.querySelector('[data-id="divblock"]'))
        {
            if(this.viewAllCaseHistory && !this.isClassic){
                this.template.querySelector('[data-id="divblock"]').style = "margin-bottom: 50px; width: 100%;";
            }else{
                this.template.querySelector('[data-id="divblock"]').style = "width: 100%;height: 100%;";
        }
        }
    }

    handleUserDetailClassic(event)
    {
        this.dispatchEvent(new CustomEvent('openuserdetailclassic' , { detail : event.detail,  bubbles: true,
            composed: true }));
    }
}