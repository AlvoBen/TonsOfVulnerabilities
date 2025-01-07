/******************************************************************************************************************************
LWC Name        : archivedCaseTasksTableHum.js
Function        : LWC to display Archived task table

Modification Log:
* Developer Name                                Date                          Description
*------------------------------------------------------------------------------------------------------------------------------
* Ashish Kumar/Kajal Namdev                     07/18/2022                    Original Version 
******************************************************************************************************************************/
import { LightningElement, track, api } from 'lwc';
import { getTaskFormLayout } from './layoutConfig';
import fetchCaseDetailResponse from '@salesforce/apexContinuation/ArchivedCaseDetail_LC_HUM.fetchCaseDetailData';

export default class archivedCaseTasksTableHum extends LightningElement {
    @api isClassic =false;
    @api recordId;
    @api viewAllTasks;
    @api taskResponse = {};
    @api customLabels;
    @track taskData = [];
    @track taskModel;
    @track showViewAll = true;
    @track oViewAllParams = {};
    @track totalRecordCount = 0;
    @track sectionName = 'Task ';
    connectedCallback() {
        this.taskModel = getTaskFormLayout();

        if (this.viewAllTasks) {
            fetchCaseDetailResponse({ caseId: this.recordId, ObjectName: 'Task', StartRow: '1', EndRow: '1000' })
                .then((result) => {
                    if(result.CaseTasksResponse.CaseTasksResponseData)
                    {
                        this.taskData = result.CaseTasksResponse.CaseTasksResponseData;
                        this.totalRecordCount= this.taskData.length;
                        this.sectionName = this.sectionName + '('+this.totalRecordCount+')';
                    }
                    
                }).catch(error => {
                    console.log("error--", JSON.stringify(error));
                })
            this.showViewAll = false;
            this.oViewAllParams = {
                sOptions: {
                    sAppName: 'ArchivalCaseHistory'
                }
            }
        }
        else {
            if(this.taskResponse.CaseTasksResponseData)
            {
                this.taskData = this.taskResponse.CaseTasksResponseData;
                this.totalRecordCount=this.taskResponse.Header.sTotalRows > 25 ? 25+'+' : this.taskData.length;
                this.sectionName = this.sectionName + '('+this.totalRecordCount+')';
                let oParams = {sRecordId:this.recordId, viewAllData : true, isClassic: this.isClassic}
    
                this.oViewAllParams = {
                    sOptions: {
                        sAppName: this.customLabels.HUMArchival_appName,//'ArchivalCaseHistory',
                        sArchivalScreen: this.customLabels.HUMArchival_screenNameDetail,//'ArchivalDetail',
                        isClassic: this.isClassic
                    },
                    sRecordId: this.recordId,
                    nameOfScreen : this.customLabels.HUMArchival_NameOfScreenTask,//'Tasks',
                    oParams: oParams,
                    viewAllData: true,
                    sArchivalMinRecords: this.customLabels.HUMArchival_ARCHIVALDETAIL_MIN_REC,//hcConstants.ARCHIVALDETAIL_MIN_REC,
                    sArchivedCaseSearch: false,
                    sTotalRowCount:  this.taskResponse.Header.sTotalRows

                };
            }
          
        }
    }
    renderedCallback(){
      if(this.template.querySelector('[data-id="divblock"]'))
        {
            if(this.viewAllTasks && !this.isClassic){
                this.template.querySelector('[data-id="divblock"]').style = "margin-bottom: 20px; width: 100%;";
            }else{
                this.template.querySelector('[data-id="divblock"]').style = "width: 100%;height: 100%;";
        }
        }
    }
        
}