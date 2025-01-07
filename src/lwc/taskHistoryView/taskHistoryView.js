/*******************************************************************************************************************************
LWC JS Name : TaskHistoryView.js
Function    : This component is used to task history section on task detail page 

Modification Log: 
Developer Name           Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
* Shailesh Bagade                                        22/06/22                       initial version
*********************************************************************************************************************************/
import { LightningElement, wire, track, api } from 'lwc';
import getTaskFieldTracking from '@salesforce/apex/taskViewControllerHUM.getTaskFieldTracking';
import { toastMsge} from "c/crmUtilityHum";
import { openLWCSubtab } from 'c/workSpaceUtilityComponentHum';
import {
  subscribe,
  unsubscribe,
  MessageContext
} from "lightning/messageService";
import REFRESH_TASK_CHANNEL from "@salesforce/messageChannel/refreshTaskDetails__c";

export default class TaskHistoryView extends LightningElement {
    @track loaded;
    @track taskView = [];
    @api recordId;
    taskSize;
    taskNumber;
    subscription = null;

    @wire(MessageContext)
    messageContext;
    /**
     * Method: handleSubscribe
     * @param {*} event 
     * Function: this method is used to capture data from other component edittasks, create task etc
     */
    handleSubscribe() {
      if (this.subscription) {
          return;
      }
      this.subscription = subscribe(this.messageContext, REFRESH_TASK_CHANNEL, (message) => {
          this.loaded = false
          this.connectedCallback()
      });
    }

    connectedCallback(){
        this.handleSubscribe();
        this.taskHistoryDetaildata();
    }
    /**
     * Method: taskHistoryDetaildata
     * @param {*} event 
     * Function: this method is used to get field history tracking data for task fields
     */
    async taskHistoryDetaildata() {
      getTaskFieldTracking({ recId: this.recordId }).then(result => {
      
        if(result !=null && result[0]){
          this.taskView.push(result[0]);
          this.taskNumber = result[0].taskNumber;
        }
        
        if(result !=null && result[1])
          this.taskView.push(result[1]);
        this.taskSize = result ? result.length : 0;
        
        this.loaded = true;
      })
      .catch(error => {
        this.loaded = true;
        let message = error.body.message.replace(/&amp;/g, "&").replace(/&quot;/g, '"');
        toastMsge("", message, "error", "pester");
      });
    }
  /**
   * Method: handleViewAllURL
   * @param {*} event 
   * Function: this method is used to navigate and open new tab on clicked view all
   */
    handleViewAllURL(event){
        event.preventDefault();
        
        const rawdata = {
            "recordId"   :  this.recordId,
            "taskNumber" :  this.taskNumber
          }
        openLWCSubtab('taskHistoryTableHum',JSON.stringify(rawdata),{label:this.taskNumber,icon:'standard:case'});
    }
}