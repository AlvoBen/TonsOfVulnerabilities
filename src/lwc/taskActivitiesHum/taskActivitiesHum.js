/*******************************************************************************************************************************
LWC JS Name : TaskActivitiesHum.js
Function    : This component is used show task associated to cases on case page

Modification Log: 
Developer Name           Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
* Shailesh Bagade                                        22/06/22                       initial version
* Shailesh Bagade                                        28/07/2022                     US-3560805 Close Tasks/Activity Section of Case Details 
*********************************************************************************************************************************/

import { LightningElement, track, api, wire } from 'lwc';
import { getHistoryTimelineModel } from './layoutConfig';
import getTaskRelatedToCase from '@salesforce/apex/TaskCommentsDataTable_LD_HUM.getTaskRelatedToCase'; 
import { NavigationMixin } from 'lightning/navigation';
import closeTask from '@salesforce/apex/TaskCommentsDataTable_LD_HUM.closeTask';
import {
    subscribe,
    unsubscribe,
    MessageContext
  } from "lightning/messageService";
  import REFRESH_TASK_CHANNEL from "@salesforce/messageChannel/refreshTaskDetails__c";
  import { toastMsge} from "c/crmUtilityHum";

export default class TaskActivitiesHum extends NavigationMixin(LightningElement) {
    showTaskNumberURL = true;
    parentSpinner
    refreshCasePage = true;
    currentTaskId;
    @api recordId;
    showTaskEditPanel = false;
    title;
    action;
    @track iOnloadCount = 5;
    @track buttonsConfig = [{
        text: 'Cancel',
        isTypeBrand: false,
        eventName: 'close'
    }, {
        text: 'Save',
        isTypeBrand: true,
        eventName: 'save'
    }];
    
    @track historyTimelineData = []; //assign it to response api to be sent to inner cmp
    @track iTotalcount; //assigntotal count os tasks to case to be sent to inner cmp
    @track filteredcount = 5; // record shown 
    @track counter = 1;
    @track totalresults = []; 
    @track showbutton = false; 
    @api encodedData;
    @api pageRefData; //this variable is used for getting URL attributes that coming from other pages
    isEdited = false;
    bLogCodeVisible = false;
    @track profile;
    @track loaded;
    showSpinner;
    isCommentSaved;
    subscription = null;
    hideFooterheader = true;

    @wire(MessageContext)
    messageContext;

    handleSubscribe() {
        if (this.subscription) {
            return;
        }
        this.subscription = subscribe(this.messageContext, REFRESH_TASK_CHANNEL, (message) => {
            this.connectedCallback()
        });
    }


    connectedCallback() {
        this.getTaskComments();
    }

    /**
     * Method: getTaskComments
     * @param {*} eve this method gets the associated tasks for cases
     * Function: this
     */
    @api
    getTaskComments() {
        getTaskRelatedToCase({ objID: this.recordId }).then((result) => {
            let respArray = [];
            let caseComments = (JSON.parse(result)).lTaskCommentDTO;
            this.profile = (JSON.parse(result)).profileName;
            
            this.loaded = true;
            this.parentSpinner = false;
            if (caseComments != null) {
                caseComments.forEach(oms => {
                    let obj = {};
                    getHistoryTimelineModel("omsmodel").forEach(x => {
                        if (x.fieldname === 'icon') {
                            obj[x.fieldname] = this.getIcon(x.mappingfield);
                        } else if (x.compoundvalue) {
                            let objComp = {};
                            let compvalues = x.compoundvalues;
                            compvalues.forEach(t => {
                                if (t.hasOwnProperty("header")) {
                                    objComp["header"] = this.getHeaderValues(t["header"], oms);
                                }
                                if (t.hasOwnProperty("body")) {
                                    objComp["body"] = this.getBodyValues(t["body"], oms)
                                }
                                if (t.hasOwnProperty("footer")) {
                                    objComp["footer"] = this.getFootervalues(t["footer"], oms)
                                }
                            });
                            obj[x.fieldname] = objComp;
                            objComp = null;
                        }
                        else {
                            obj[x.fieldname] = oms.hasOwnProperty(x.mappingfield) ? oms[x.mappingfield] : '';
                        }
                    });
                    respArray.push(obj);
                });
            }
            
            this.historyTimelineData = respArray;
            this.totalresults = this.historyTimelineData;
            this.historyTimelineData.toggle = false
            this.iTotalcount = this.historyTimelineData.length;
			if(this.template.querySelector('c-generic-history-timeline-hum') != null){
			   setTimeout(() => this.template.querySelector('c-generic-history-timeline-hum').refreshCount(this.isEdited));}
        }).catch((error) => {
            this.loaded = true;
            this.parentSpinner = false;
        });
    }

    getIcon(iconname) {
        return getHistoryTimelineModel("icons").find(x => x.iconname === iconname);
    }

    /**
     * Method: getHeaderValues
     * @param {*} eve this method gets the header comonent for task
     * Function: this
     */
    getHeaderValues(header, omsdata) {
        let objheader = {};
        //let headervalues = header.mappingfield.split(',');
        let headervalue = '';
        header.mappingfield.split(',').forEach(t => {
            headervalue += (omsdata.hasOwnProperty(t) ? omsdata[t] != null ? omsdata[t] : '' : '') + '/';
        })

        headervalue = headervalue.endsWith('/') ? headervalue.substring(0, headervalue.length - 1) : headervalue;
        objheader[header.fieldname] = headervalue;
        objheader.fieldname = objheader.fieldname;
        objheader.fieldvalue = headervalue;
        return objheader;
    }

    /**
     * Method: handleTasksForActivities
     * @param {*} eve this method populates the edit modal for task edit from dropdown
     * Function: this
     */
     handleEditTask(event){
        this.title = 'Edit Task';
        this.action = 'Edit';
        this.showTaskEditPanel = true;
        this.currentTaskId = event.detail.value; 
    }
    /**
     * Method: handleCreateDTask
     * @param {*} eve this method populates the create depedent form modal for task edit from dropdown on case page
     * Function: this
     */
    handleCreateDTask(event){
        this.title = 'Dependent Task';
        this.action = 'New';
        this.showTaskEditPanel = true;
        this.currentTaskId = event.detail.value; 

    }

    getBodyValues(bodymodel, omsdata) {
        let objbody = [];
        bodymodel.forEach(b => {
            objbody.push({
                fieldname: b.fieldname,
                fieldvalue: omsdata.hasOwnProperty(b.mappingfield) ? omsdata[b.mappingfield] : '',
                islink: b.islink ? true : false,
                object: b.object,
                hidden: b.hidden ? true : false
            });
        });
        return objbody;
    }

    getFootervalues(footer, omsdata) {
        let objfooter = {};
        //let headervalues = header.mappingfield.split(',');
        let footervalue = '';
        footer.mappingfield.split(',').forEach(t => {
            footervalue += (omsdata.hasOwnProperty(t) ? omsdata[t] != null ? omsdata[t] : '' : '') + '/';
        })
        footervalue = footervalue.endsWith('/') ? footervalue.substring(0, footervalue.length - 1) : footervalue;
        //objfooter[footer.fieldname] = footervalue;
        objfooter.fieldname = footer.fieldname;
        objfooter.fieldvalue = footervalue;
        return objfooter;
    }


    /**
     * Method: handleScroll
     * @param {*} eve this method adds more elements when show more clicked
     * Function: this
     */
    handleScroll(event) {
        if (event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight) {
            if (this.filteredcount != undefined && this.iTotalcount != undefined) {
                if (this.filteredcount < this.iTotalcount) {
                    if ((this.filteredcount + this.loadcount) < this.iTotalcount) {
                        this.historyTimelinedata = this.totalresults.slice(0, (this.filteredcount + this.loadcount));
                        this.filteredcount = this.filteredcount + this.loadcount;
                    }
                    else {
                        this.historyTimelinedata = this.totalresults.slice(0, this.iTotalcount);
                        this.filteredcount = this.iTotalcount;
                        this.showbutton = true;
                    }
                }
                else {
                    this.filteredcount = this.iTotalcount;
                }
            }
        }
    }
    cancelClicked(){
        if(this.action === 'New'){
            this.template.querySelector('c-create-task_-l-w-c_-h-u-m').onCancelClick();
        }
        else{
            this.closeModal()
        }
    }
    /**
     * Method: closeModal
     * @param {*} eve this method closes the popup
     * Function: this
     */
    closeModal() {
        this.showTaskEditPanel = false;    
        this.showSpinner = false;

        // window.location.reload()
    }
    
    /**
     * saveFormEdit- calls save function of edit task popup 
     * 
     */
    async saveFormEdit() {
        this.showSpinner = true
        await this.template.querySelector('c-create-task_-l-w-c_-h-u-m').onSaveClick();
        this.showSpinner = false;
        
    }
    handleCloseTask(event){
        this.parentSpinner =true;
        closeTask({
            taskId: event.detail.value
        }).then(result => {
            let sTaskNumber = result;
            this.showSpinner = false;
            let sUpdateSuccessMsg = `Task ${sTaskNumber} was successfully saved`;
            
            toastMsge("", sUpdateSuccessMsg, "success", "pester");
            this.getTaskComments();
            
        })
        .catch((error) => {
            this.parentSpinner = false;
            let message = error.body.message
            .replace(/&amp;/g, "&")
            .replace(/&quot;/g, '"');
          toastMsge("", message, "error", "pester");
        });
    }
    navigateToTaskPage(event){
        
        this[NavigationMixin.Navigate]({
            type: 'standard__recordPage',
            attributes: {
                recordId: event.detail.value,
                objectApiName: 'Task',
                actionName: 'view'
            },
        });
    }

}