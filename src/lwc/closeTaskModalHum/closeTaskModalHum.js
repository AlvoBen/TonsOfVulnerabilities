/*******************************************************************************************************************************
LWC JS Name : CloseTaskModalHum.js
Function    : This component is present on task details page, it will close the task on click of CloseTask Button

Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
Bhumika S               Vinay L                           04/07/22                 LWC comonent for close task
Prasuna  Pattabhi                                         08/24/23                 US 4412371 Market Credentialing Task Fields
 Vani Shrivastava                                       09/15/23      US 4891201: T1PRJ0865978 C06- Case Management- Case Page- Need toast message for Task creation and close
 Nilesh Gadkar                                            10/13/2023               US 4891201: T1PRJ0865978 Added switch
 Apurva Urkude                                            10/18/2023               Defect Fix-DF8242
*********************************************************************************************************************************/

import { LightningElement,api,track, wire} from 'lwc';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import closeTask from '@salesforce/apex/TaskCommentsDataTable_LD_HUM.closeTask';
import { toastMsge } from 'c/crmUtilityHum';
import { publish, MessageContext } from "lightning/messageService";
import REFRESH_TASK_CHANNEL from "@salesforce/messageChannel/refreshTaskDetails__c";
import credentialingClosable from '@salesforce/apex/TaskCommentsDataTable_LD_HUM.credentialingClosable'; 
import Credentialing_ERROR_MSG from '@salesforce/label/c.Credentialing_ERROR_MSG';
import isCRMFunctionalityONJS from '@salesforce/apex/HUMUtilityHelper_LWC.isCRMFunctionalityONJS';

export default class CloseTaskModalHum extends LightningElement {

    @api recordId; //getting Record Id
    @track b4891201SwitchON = false;
    spinnerVisible=true;
    msg;
    CredentialingErrorMsg = Credentialing_ERROR_MSG;

    @wire(MessageContext)
    messageContext;

    connectedCallback() {
        this.getSwitchData.call(this).then(result => {
            if (result && result === true) {
                this.showNotification(); 
            }
        })
    }

    /**
     * Method: showNotification
     * @param {*} event 
     * Function: this method used to perform closing task from backend
     */
    async showNotification() {
        let showErrorMsg = await credentialingClosable({Id:this.recordId});
        if(showErrorMsg){
            toastMsge('', this.CredentialingErrorMsg, 'error', 'dismissable');
            this.showSpinner = false;
            this.closeQuickAction(); 
            return;
        }
        let TaskNum;    
        let taskClosed = closeTask({ taskId: this.recordId }).then(result => {
        if (result != null) {
            this.TaskNum = result;
        }
        this.showSpinner = false;
        if(this.b4891201SwitchON == true) {
            this.msg= `Task ${this.TaskNum} was successfully closed`;
        } else {
            this.msg= `Task ${this.TaskNum} was successfully saved`;
        }
        const messaage = {
            sourceSystem: this.msg
        };
        publish(this.messageContext, REFRESH_TASK_CHANNEL, messaage); 
        toastMsge('', this.msg, 'success', 'pester');
        this.closeQuickAction();       
        })
        .catch(error => {
            this.showSpinner = false;
            let message = error.body.message.replace(/&amp;/g, "&").replace(/&quot;/g, '"');
            toastMsge('', message, 'error', 'pester');
        });
    }

    /**
     * Method: closeQuickAction
     * @param {*} event 
     * Function: this method is used to close the pop-up modal
     */

    closeQuickAction(){
        const closeQA = new CustomEvent('closeaction');
        // Dispatches the event.
        this.dispatchEvent(closeQA);
    }
    /*Method to fetch the switch details*/
	getSwitchData() {
        return new Promise((resolve, reject) => {
            isCRMFunctionalityONJS({ sStoryNumber: ['4891201'] })
                .then(result => {
                    this.b4891201SwitchON = result['4891201'];
                    resolve(true);
                }).catch(error => {
                    console.log(error);
                    reject(false);
                })
        })
    }
}