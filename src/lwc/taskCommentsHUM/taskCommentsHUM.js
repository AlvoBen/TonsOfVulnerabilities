/*******************************************************************************************************************************
LWC JS Name : TaskCommentsHUM.js
Function    : This component is present on task details page, it shows the task comment

Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Shailesh Bagade                                        22/06/2022                  initial version 
*********************************************************************************************************************************/
import { LightningElement, track, api, wire } from 'lwc';
import { toastMsge } from 'c/crmUtilityHum';
import { getHistoryTimelineModel } from './layoutConfig';
import generateTaskComments from '@salesforce/apex/TaskCommentsDataTable_LD_HUM.generateTaskComments';
import getCurrentUserProfileName from '@salesforce/apex/TaskCommentsDataTable_LD_HUM.getCurrentUserProfileName';
import saveTaskCommentData from "@salesforce/apex/TaskCommentsDataTable_LD_HUM.saveTaskCommentData";
import {
    subscribe,
    unsubscribe,
    MessageContext
  } from "lightning/messageService";
  import REFRESH_TASK_CHANNEL from "@salesforce/messageChannel/refreshTaskDetails__c";

export default class TaskCommentsHUM extends LightningElement {
    @track enablePopup;
    @api recordId;
    @track onloadCount = 5;
    @track buttonsConfig = 
    [{
        text: 'Cancel',
        isTypeBrand: false,
        eventName: 'close'
    }, {
        text: 'Save',
        isTypeBrand: true,
        eventName: 'save'
    }];
    labels = {
        'label1' : 'Task Comments',
        'label2' : 'Humana Pharmacy Specialist'
    }
    @track historyTimelineData = []; //assign it to response api to be sent to inner cmp
    @track totalcount; //assign count  to response api to be sent to inner cmp
    @track filteredcount = 5; //maximum number of task to be shown
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

    // @wire(MessageContext)
    // messageContext;
    /**
     * used to get response from lms, if the comment is added in edit / create task page
     * 
     */
    //  handleSubscribe() {
    //     if (this.subscription) {
    //         return;
    //     }
    //     this.subscription = subscribe(this.messageContext, REFRESH_TASK_CHANNEL, (message) => {
    //         this.connectedCallback()
    //     });
    //   }

    /**
     * Open modal to create new comment
     * 
     */
     openModal() {
        this.enablePopup = true;
    }
    /**
     * get current user profile
     * 
     */
    connectedCallback() {
        this.getTaskComments();
       console.log('hellow anon');
    }
   
    /**
     * Method: getTaskComments
     * @param {*} event 
     * Function: this method pulls the saved comment from backend
     */
    @api
    getTaskComments() {
        generateTaskComments({ objID: this.recordId }).then((result) => {
            let respArray = [];
            let taskComments = (JSON.parse(result)).lTaskCommentDTO;
            this.profile = (JSON.parse(result)).profileName;
            this.loaded = true;

            if (taskComments != null) {                
                taskComments.forEach(oms => {
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
            this.totalcount = this.historyTimelineData.length;
			if(this.template.querySelector('c-generic-history-timeline-hum') != null){
               setTimeout(() => this.template.querySelector('c-generic-history-timeline-hum').refreshCount(this.isEdited));}
        }).catch((error) => {
            this.loaded = true;
            let message = error.body.message.replace(/&amp;/g, "&").replace(/&quot;/g, '"');
            toastMsge('', message, 'error', 'pester');
        });
    }
    
    /**
     * Method: getIcon
     * @param {*} event 
     * Function: this method fetches the matching icon from layout.js in above method
     */
    getIcon(iconname) {
        return getHistoryTimelineModel("icons").find(x => x.iconname === iconname);
    }

     /**
     * Method: getHeaderValues
     * @param {*} event 
     * Function: this method fetches header value
     */
    getHeaderValues(header, omsdata) {
        let objheader = {};
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
     * Method: getBodyValues
     * @param {*} event 
     * Function: this method fetches inner body for comment
     */
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

    /**
     * Method: getFootervalues
     * @param {*} event 
     * Function: this method fetches footer body for displayig task comment
     */
    getFootervalues(footer, omsdata) {
        let objfooter = {};
        let footervalue = '';
        footer.mappingfield.split(',').forEach(t => {
            footervalue += (omsdata.hasOwnProperty(t) ? omsdata[t] != null ? omsdata[t] : '' : '') + '/';
        })
        footervalue = footervalue.endsWith('/') ? footervalue.substring(0, footervalue.length - 1) : footervalue;
        objfooter.fieldname = footer.fieldname;
        objfooter.fieldvalue = footervalue;
        return objfooter;
    }

    /**
     * Method: handleScroll
     * @param {*} event 
     * Function: this method adds elements under scroller when show more is clicked
     */
    handleScroll(event) {
        if (event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight) {
            if (this.filteredcount != undefined && this.totalcount != undefined) {
                if (this.filteredcount < this.totalcount) {
                    if ((this.filteredcount + this.loadcount) < this.totalcount) {
                        this.historyTimelinedata = this.totalresults.slice(0, (this.filteredcount + this.loadcount));
                        this.filteredcount = this.filteredcount + this.loadcount;
                    }
                    else {
                        this.historyTimelinedata = this.totalresults.slice(0, this.totalcount);
                        this.filteredcount = this.totalcount;
                        this.showbutton = true;
                    }
                }
                else {
                    this.filteredcount = this.totalcount;
                }
            }
        }
    }

    /**
     * Method: deleteComment
     * @param {*} event 
     * Function: this method is used to delete task comment
     */
    deleteComment(){
        //delete comment code
        this.getTaskComments();
        toastMsge("", "Comment was successfully deleted", "success", "dismissable");
    }
    /**
     * Method: saveComment
     * @param {*} event 
     * Function: this method is used to save task comment and fetch new case
     */
    saveComment(){
        this.isEdited = true;
        this.getTaskComments()
    }
    closeModal() {
        this.enablePopup = false;
    }
    /**
     * Checks for user inputs on
     * comments modal on click of save
     */
     async saveForm() {
        this.template.querySelector('c-generic-comments-form-hum').hasData();
    }

    /**
     * Method: modifiedHandler
     * @param {*} event 
     * Function: this method is used capture events from generic comments form
     */
    async modifiedHandler(event) {
    
        try{
            this.showSpinner = true;
            const lstOfCaseComment = [{SObjFieldValue__c: Object.values(event.detail)[0]}];
            const saveForm = await saveTaskCommentData({
                sTaskId: this.recordId,
                taskCommentData: JSON.stringify(lstOfCaseComment)
            });
            
            if (saveForm) {
                this.callCommentSave();
            }
            else{
                this.showSpinner = false;
                this.closeModal();
                toastMsge("", saveForm, "error", "dismissable");
            }
        }
        catch(error){
            this.showSpinner = false;
            let message = error.body.message.replace(/&amp;/g, "&").replace(/&quot;/g, '"');
            toastMsge('', message, 'error', 'pester');
            
        }
    }

    /**
     * Method: callCommentSave
     * @param {*} event 
     * Function: this method is used to save comment in modifiedHanlder
     */
    callCommentSave() {
        this.showSpinner = false;
        this.closeModal();
        this.getTaskComments();
        toastMsge("", "Comment was successfully saved", "success", "dismissable");
    }

}