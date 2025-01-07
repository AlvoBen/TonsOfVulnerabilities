/*******************************************************************************************************************************
LWC JS Name : TaskHistoryTableHum.js
Function    : This component is used to show datatbel for task history on task page

Modification Log: 
Developer Name           Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
* Shailesh Bagade                                        22/06/22                       initial version
*********************************************************************************************************************************/
import { LightningElement, api, wire, track } from 'lwc';
import getTaskFieldTracking from '@salesforce/apex/taskViewControllerHUM.getTaskFieldTracking';
import { invokeWorkspaceAPI, openSubTab } from "c/workSpaceUtilityComponentHum";

import { toastMsge} from "c/crmUtilityHum";
import { NavigationMixin } from "lightning/navigation";
/*columns to display*/
const column = [
    { label : 'Date', fieldName : 'taskCreatedDate'},
    { label : 'Modified By', fieldName : 'taskCreatedBy'},
    { label : 'Modified By Queue', fieldName : 'taskModifiedByQueue'},
    { label : 'Actions', fieldName : 'taskChangeAction'}
]
export default class TaskHistoryTableHum extends NavigationMixin(LightningElement) {
    @api pageRefData; //this variable is used for getting URL attributes that coming from other pages
    @api encodedData; //It'll store the task id when view all is clicked from taskHistoryView component
    @track loaded;
    goTOTaskLabel
    column = column;

    connectedCallback(){
        this.encodedData = JSON.parse(this.encodedData);
        this.goTOTaskLabel = 'Tasks > '+this.encodedData.taskNumber;
        this.taskHistoryDetaildata();
    }
    /**
     * Method: taskHistoryDetaildata
     * @param {*} event 
     * Function: this method is used to get task hstory data till 3500 records
     */
    async taskHistoryDetaildata() {
        getTaskFieldTracking({ recId: this.encodedData.recordId }).then(result => {
      
            this.taskView = result;
            
            this.loaded = true;
            
          })
          .catch(error => {
            this.loaded = true;
            let message = error.body.message.replace(/&amp;/g, "&").replace(/&quot;/g, '"');
            toastMsge('', message, 'error', 'pester');
          });
    }
    /**
     * Method: goBackToTask
     * @param {*} event 
     * Function: this method is used to prevent default events
     */
    goBackToTask(event){
        event.preventDefault();
        this.onHyperLinkClick();
        
    }
    /**
     * Method: onHyperLinkClick
     * @param {*} event 
     * Function: this method is used to navigate back to task 
     */
    onHyperLinkClick(event){
        let data = {title: 'Task',nameOfScreen:'Task'};
        let pageReference = {
               type: 'standard__recordPage',
               attributes: {
                   recordId: this.encodedData.recordId,
                   objectApiName: 'Task',
                   actionName: 'view'
               }
        } 
    
        openSubTab(data, undefined, this, pageReference, {openSubTab:true,isFocus:true,callTabLabel:false,callTabIcon:false});
    }
}