/*
LWC Name        : taskDetailLeftPanel_LWC_HUM.js
Function        : This JS serves as controller for task details to display on left handle panel to taskDetailLeftPanel_LWC_HUM.html

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Bhumika Dodiya                 06/22/2022                  initial version
* Raj Paliwal					 17/02/2023					 US: 4163250 -Task Type (Notification) NEW Task
* Prasuna Pattabhi			     08/24/2023					 US: 4412371 - Market Credentialing Task Fields
****************************************************************************************************************************/

import { LightningElement, track, api, wire  } from 'lwc';
import fetchTaskDetail from '@salesforce/apex/CreateTask_C_LWC_HUM.fetchTaskDetail';
import { loadStyle} from 'lightning/platformResourceLoader';
import TaskQuickActionModalStyle from '@salesforce/resourceUrl/TaskQuickActionModalStyle'; 
import { NavigationMixin } from 'lightning/navigation';
import {subscribe,unsubscribe,MessageContext} from "lightning/messageService";
import REFRESH_TASK_CHANNEL from "@salesforce/messageChannel/refreshTaskDetails__c";
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import TIME_ZONE from '@salesforce/i18n/timeZone';
import credentialingEligible from '@salesforce/apex/CreateTask_C_LWC_HUM.credentialingEligible';

export default class TaskDetailLeftPanel_LWC_HUM extends NavigationMixin(LightningElement) {
    @api recordId;    
    @track showSpinner = false;    
    taskDetail;
    subscription = null;
    time_zone = TIME_ZONE;
    @track taskDetailStartTime;
    @track taskDetailEndTime;
    @track txtcolor;
    @track showMCDProviderFields = false; 

    @wire(MessageContext)
    messageContext;

    /**
     * Method Name : handleSubscribe
     * Function : This method will subscribe message from channel
     */
    handleSubscribe() {
        if (this.subscription) {
            return;
        }
        this.subscription = subscribe(this.messageContext, REFRESH_TASK_CHANNEL, (message) => {
        this.connectedCallback()
        });
    }
    /**
     * Method Name : connectedCallback
     * Function : This method will fetch task detail 
     */
    connectedCallback(){
        this.handleSubscribe();
        Promise.all([
            loadStyle(this, TaskQuickActionModalStyle)
        ]);
        this.showSpinner = true;
        credentialingEligible({Id:this.recordId,objType:'Task'}).then(result=>{
            this.showMCDProviderFields = result;
        });
        fetchTaskDetail({taskId: this.recordId,action: 'LeftPanel'}).then(result => {
            if (result != null) {
                this.taskDetail = result['Task'];
                if(this.taskDetail.Call_Back_End__c && this.taskDetail.Call_Back_End__c){
                    this.taskDetailStartTime = this.taskDetail.Call_Back_Start__c;
                    this.taskDetailStartTime = this.timeformat(this.taskDetailStartTime);
                    this.taskDetailEndTime = this.taskDetail.Call_Back_End__c;
                    this.taskDetailEndTime = this.timeformat(this.taskDetailEndTime);
                }
                if(this.taskDetail.Callback_Requested__c === 'Yes'){
                    this.txtcolor = true;
                }
                else{
                    this.txtcolor = false;
                }
                this.taskDetail = {...this.taskDetail, 'ParentTask': result['ParentTaskId'], 'ParentTaskNumber' : result['ParentTaskNumber'], 'Medicare' : result['Medicare'] };
                this.showSpinner = false;
            }
        })
        .catch(error => {
            console.log('TaskDetailLeftPanel fetchTaskDetail error.......', error.body.message);
            this.showSpinner = false;
            this.dispatchEvent(
                new ShowToastEvent({
                    title: 'Error in TaskDetailLeftPanel.fetchTaskDetail',
                    message: error.body.message,
                    variant: 'error',
                }),
            );
        });
    }

    timeformat(timeval)
    {
        let AMPM = 'AM';
        let timevar = timeval / (60*60*1000);
        timevar = timevar;
        timevar= timevar.toString();
        let newvar = timevar.split('.');
        let hours = newvar[0];
        hours = parseInt(hours);
        let minutes = newvar[1];
        
        if(hours < 12){
            AMPM = 'AM';
        }
        else if(hours > 12){
            hours = hours-12;
            AMPM = 'PM';
        }
        else if(hours = 12){
            hours=hours;
            AMPM = 'PM';
        }
        if(minutes < 10)
        {
            minutes = parseInt(minutes);
            minutes = minutes * 60/10;
        }
        else{
            minutes='0.'+minutes;
            minutes = parseFloat(minutes);
            minutes = minutes * 60;
            minutes = Math.round(minutes);
        }
        if(minutes == undefined){
            minutes = '00';
        }
        if(minutes < 10){
            minutes = '0'+ minutes;
        }
        if(hours < 10){
            hours = '0'+hours;
        }
        
        let str =  hours + ':' + minutes + ' ' + AMPM;
        return str;
    }
    /**
     * Method Name : handleCaseRedirect
     * Function : This method will redirect to case detail page
     */
    handleCaseRedirect(event){ 
        event.preventDefault();
        this[NavigationMixin.Navigate]({
            type: 'standard__recordPage',
            attributes: {
                recordId:  this.taskDetail.WhatId,
                objectApiName: 'Case',
                actionName: 'view'
            }
        }); 
    }
    /**
     * Method Name : handleTaskRedirect
     * Function : This method will redirect to task detail page 
     */
    handleTaskRedirect(event){
        event.preventDefault();
        this[NavigationMixin.Navigate]({
            type: 'standard__recordPage',
            attributes: {
                recordId:  this.taskDetail.ParentTask,
                objectApiName: 'Task',
                actionName: 'view'
            }
        }); 
    }
}