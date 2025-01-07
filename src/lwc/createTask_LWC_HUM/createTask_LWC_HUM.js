/*******************************************************************************************************************************
LWC JS Name : CreateTask_LWC_HUM.js
Function    : This used to create new task on case page and dependent task on task page

Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Shailesh Bagade                                        22/06/2022                  initial version 
* Raj Paliwal											 17/02/2023					 US: 4163250 -Task Type (Notification) NEW Task
* Vishal Shinde 										 17/02/2023					 US: 4184082 -Task Type (Notification) EDIT Task
* Dinesh Subramaniyan                                    07/13/2023                  US: 4810481 -RAID#093 (Error Editing Notification Task)
* Prasuna Pattabhi                                       08/24/2023                  US: 4412371 - Marketing Credentialing Task Fields
* Vani Shrivastava                                       09/15/2023                  US :4891201: T1PRJ0865978 C06- Case Management- Case Page- Need toast message for Task creation and close
* Nirmal Garg                                            10/11/2023                  DF-8214 fix
* Nilesh Gadkar                                          10/13/2023                  US :4891201: T1PRJ0865978 switch added
*********************************************************************************************************************************/
import { LightningElement, track, api, wire  } from 'lwc';
import getLstServiceCenter from '@salesforce/apex/CreateTask_C_LWC_HUM.getLstServiceCenter';
import getLstDepartments from '@salesforce/apex/CreateTask_C_LWC_HUM.getDepartments';
import getLstTopics from '@salesforce/apex/CreateTask_C_LWC_HUM.getTopics';
import getMedicareId from '@salesforce/apex/CreateTask_C_LWC_HUM.getMedicareId';
import createTask from '@salesforce/apex/CreateTask_C_LWC_HUM.createTask';
import { CloseActionScreenEvent } from 'lightning/actions';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import { NavigationMixin ,CurrentPageReference} from 'lightning/navigation';
import TASKEDIT_QUEUENOTEXISTS_HUM from '@salesforce/label/c.TASKEDIT_QUEUENOTEXISTS_HUM';
import { loadStyle} from 'lightning/platformResourceLoader';
import TaskQuickActionModalStyle from '@salesforce/resourceUrl/EditTaskQuickActionModalStyle';
import fetchTaskDetail from '@salesforce/apex/CreateTask_C_LWC_HUM.fetchTaskDetail';
import editTask from '@salesforce/apex/CreateTask_C_LWC_HUM.editTask';
import { publish, MessageContext } from "lightning/messageService";
import REFRESH_TASK_CHANNEL from "@salesforce/messageChannel/refreshTaskDetails__c";
import credentialingEligible from '@salesforce/apex/CreateTask_C_LWC_HUM.credentialingEligible';
import getCredentialingTaskPicklist from '@salesforce/apex/CreateTask_C_LWC_HUM.getCredentialingTaskPicklist';
import Credentialing_ERROR_MSG from '@salesforce/label/c.Credentialing_ERROR_MSG';
import getTaskNumber from '@salesforce/apex/CreateTask_C_LWC_HUM.getTaskNumber';
import { toastMsge } from 'c/crmUtilityHum';
import isCRMFunctionalityONJS from '@salesforce/apex/HUMUtilityHelper_LWC.isCRMFunctionalityONJS';
export default class CreateTask_LWC_HUM extends NavigationMixin(LightningElement)  {
    @api recordId;
    @api hideFooterHeader;
    showActionModal; //for unsaved changes popup
    showSpinner = true;
    inputLength = 32000;
    sSelectedWorkQueue
    @api objectApiName;
    @api invokedFlowFrom;
    @track lstTopic = [];
    @track lstDepartment = [];
    @track lstServiceCenter = [];
    @track sServiceCenter;
    @track sDepartment;
    @track sTopic;
    @track sMedicareId;
    @track queueName; // variable used to store the work_queue_name corrospond to serivce and department from workQueuesetup data object
    @track sUser;
	@track sDueDate =null;
	@track sPriority;
	@track sStatus;
	@track sType;
    @track sCallbackRequested;
    @track sTimeZone;
    @track sCallBackSlotStartTime;
    @track sCallBackSlotStartTime1;
    @track sCallBackSlotEndTime;
    @track sCallBackSlotEndTime1;
	@track sComment;
    @track searchUser = null; //variable used to store user name
    @track today;
    @track isTopicDisabled;
    @track isTimeDisabled;
    headerLabel = 'New Task';
    @track medicareSize = '6';
    @track showErrorMsges = false;
    @track b4891201SwitchON = false;
    @api action;
    @track buttonsConfig = [{
        text: 'Continue',
        isTypeBrand: true,
        eventName: 'continue'
    }, {
        text: 'Cancel',
        isTypeBrand: false,
        eventName: 'closeoverlay'
    }];
    @track AMPMvar;
    @track check = false;
    @track check1 = false;
    @track startTimeDropdownBoolean = false;
    @track endTimeDropdownBoolean = false;
    @track endtimebool = false;
    @track checkbool = true;
    @track CallbackRequestedBool = false;
    @track errorbool = false;
    @track formaterrorbool = false;
            
    @track time = [{ id: 1, label: '8:00 AM', value: '08:00 AM'},
                   { id: 2, label: '8:30 AM', value: '08:30 AM'},
                   { id: 3, label: '9:00 AM', value: '09:00 AM'},
                   { id: 4, label: '9:30 AM', value: '09:30 AM'},
                   { id: 5, label: '10:00 AM', value: '10:00 AM'},
                   { id: 6, label: '10:30 AM', value: '10:30 AM'},
                   { id: 7, label: '11:00 AM', value: '11:00 AM'},
                   { id: 8, label: '11:30 AM', value: '11:30 AM'},
                   { id: 9, label: '12:00 PM', value: '12:00 PM'},
                   { id: 10, label: '12:30 PM', value: '12:30 PM'},
                   { id: 11, label: '1:00 PM', value: '1:00 PM'},
                   { id: 12, label: '1:30 PM', value: '1:30 PM'},
                   { id: 13, label: '2:00 PM', value: '2:00 PM'},
                   { id: 14, label: '2:30 PM', value: '2:30 PM'},
                   { id: 15, label: '3:00 PM', value: '3:00 PM'},
                   { id: 16, label: '3:30 PM', value: '3:30 PM'},
                   { id: 17, label: '4:00 PM', value: '4:00 PM'},
                   { id: 18, label: '4:30 PM', value: '4:30 PM'},
                   { id: 19, label: '5:00 PM', value: '5:00 PM'},
                   { id: 20, label: '5:30 PM', value: '5:30 PM'},
                   { id: 21, label: '6:00 PM', value: '6:00 PM'},
                   { id: 22, label: '6:30 PM', value: '6:30 PM'},
                   { id: 23, label: '7:00 PM', value: '7:00 PM'},
                ];

    label = {
        TASKEDIT_QUEUENOTEXISTS_HUM
    };

    FLOW_FROM_TASK = 'Task';
    ACTION_NEW = 'New';
    ACTION_EDIT = 'Edit';
    DEPT_TASK = 'Dependent Task';
    EDIT_TASK = 'Edit Task';
    TASK_STATUS = 'In Progress';
    TASK_TYPE = 'Work Task';
    TASK_PRIORITY = 'Normal';

    @track credentialingTaskPicklist = [];
    @track showMCDProviderFields = false;
    @track credentialingTask='';
    @track credentialingTaskCompletionDate;
    @track caseId;
    @track CredentialingErrorMsg = Credentialing_ERROR_MSG;
    @track showErrorMsgesMCDProvider = false;
    //Status picklist
    get statusOptions(){
        return [
            { label: 'In Progress', value: 'In Progress' },
            { label: 'Pending', value: 'Pending' },
            { label: 'Closed', value: 'Closed' }
        ];
    }

    //task type picklist
    get taskTypeOptions() {
        return [
            { label: 'Work Task', value: 'Work Task' },
            { label: 'Notification Task', value: 'Notification Task' }
        ];
    }

    //Priority picklist
    get priorityOptions() {
        return [
            { label: 'Normal', value: 'Normal' },
            { label: 'High', value: 'High' },
            { label: 'Critical', value: 'Critical' },
        ];
    }

    //Callback Requested picklist
    get CallbackRequestedOptions() {
        return [
            { label: 'Yes', value: 'Yes' },
        ];
    }
    get CallbackRequestedOptions1() {
        return [
            { label: 'No', value: 'No' }
        ];
    }

    //Time Zone Options
    get TimeZoneOptions() {
        if(this.sCallbackRequested === 'Yes'){
            return [
                { label: '--None--', value: '--None--' },
                { label: 'EST - Eastern', value: 'EST - Eastern' },
                { label: 'CST - Central', value: 'CST - Central' },
                { label: 'MST - Mountain', value: 'MST - Mountain' },
                { label: 'PST - Pacific', value: 'PST - Pacific' },
            ];
        }
        if(this.sCallbackRequested === 'No'){
            return null;
        }
    }

    @wire(isCRMFunctionalityONJS, { sStoryNumber: '4891201' })
    switchFuntion({ error, data }) {
        if (data) {
            this.b4891201SwitchON = data['4891201'];
        }
        if (error) {
            console.log('error---', error)
        }
    }

    @wire(MessageContext)
    messageContext;
    
    @wire(CurrentPageReference) 
    taskPageRef;

    connectedCallback() { 
        this.showSpinner =  true;  
        this.setMCDCredentialingData();      
        if(this.invokedFlowFrom === this.FLOW_FROM_TASK && this.action === this.ACTION_NEW){
            this.headerLabel = this.DEPT_TASK;
        } else if(this.action === this.ACTION_EDIT){
            this.headerLabel = this.EDIT_TASK;
        }
        try{
            Promise.all([
                loadStyle(this, TaskQuickActionModalStyle)
            ]);
            this.sStatus = this.TASK_STATUS;
            this.sType = this.TASK_TYPE;
            this.sPriority = this.TASK_PRIORITY;
            this.isTopicDisabled =true;
            //set min date value for due date
            this.today = new Date();
            let dd = this.today.getDate();
            let mm = this.today.getMonth() + 1;
            let yyyy = this.today.getFullYear();
            if(dd < 10){
                dd = '0' + dd;
            }  
            if(mm < 10){
                mm = '0' + mm;
            }                      
            this.today =  yyyy + '-' + mm + '-' + dd;
        } catch(err) {
            this.dispatchEvent(
                new ShowToastEvent({
                    message: err.message,
                    variant: 'error',
                }),
            );
        }
    }
    
    async setMCDCredentialingData(){        
        let objectApiName = 'Case';
        if(this.action === 'Edit'){
            this.caseId = this.taskPageRef.attributes && this.taskPageRef.attributes.recordId ? this.taskPageRef.attributes.recordId:'';
            objectApiName = this.taskPageRef.attributes?this.taskPageRef.attributes.objectApiName:'Case';
        }else{
            this.caseId = this.taskPageRef.state && this.taskPageRef.state.recordId ? this.taskPageRef.state.recordId:'';
        } 
        this.showMCDProviderFields = await credentialingEligible({Id:this.caseId,objType:objectApiName});
        this.credentialingTaskPicklist = await getCredentialingTaskPicklist(); 
    }

    async getServiceCenter() {
        try {
            const result = await getLstServiceCenter({myRecId : this.recordId});
            if (result) {
                this.lstServiceCenter =  this.getSelectOptions(result);  
                this.showSpinner =  false; 
            }            
          } catch (error) {
                this.showSpinner =  false;
                let message = error.body.message.replace(/&amp;/g, "&").replace(/&quot;/g, '"');
                this.dispatchEvent(
                    new ShowToastEvent({
                        message: message,
                        variant: 'error',
                    }),
                );
          }
    }
    
    // This method is created to add endtime by default when start time is selected by difference of 2 hours
    addEndTimeHrs(time){
        var timeFormat = time.split(":");

        try {
            if(timeFormat != null){  
                var hours = timeFormat[0];
                var minutes = timeFormat[1];
                var minutes1 = minutes.split(' ');
                minutes = minutes1[0];
                this.AMPMvar = minutes1[1];
                var hours1 = parseInt(hours);
                if(this.AMPMvar == undefined){
                    let minutes1 = '';
                    minutes1 = minutes;
                    minutes = minutes1.substring(0, 2);
                    this.AMPMvar = minutes1.substring(2, 4);
                }
                if(this.AMPMvar.length>2){
                    this.AMPMvar = this.AMPMvar.substring(0,2);
                }
                var d = new Date();
                d.setHours(hours1 + 2);
                d.setMinutes(minutes);
                d.setSeconds(0);

                if(hours1<=12){
                var twoHoursLater = this.formatAMPM(d);
                this.sCallBackSlotEndTime = twoHoursLater ;
                let sCallBackSlotEndTimeEle = this.template.querySelector(".sCallBackSlotEndTime");
                this.updateFieldValidation(sCallBackSlotEndTimeEle,'');
                this.check1=this.Checkarrival(this.sCallBackSlotEndTime);
                }
            }
        }
        catch(err) {
            console.log('Error :' +err)
        }
    }
    

    formatAMPM(date) {
        var hours = date.getHours();
        var minutes = date.getMinutes();
        var ampm = hours >= 12 ? 'PM' : 'AM';
        minutes = minutes < 10 ? '0'+minutes : minutes;
        hours = hours<10 ? 0+hours : hours;

        if(hours<12){
            hours = hours;
            ampm = this.AMPMvar.toUpperCase();
            
        }else if(hours==12){
            hours = hours;
        }
        else if(hours>12){
            hours = hours-12;
        }

        var sHours = hours.toString();
        var sMinutes = minutes.toString();
        
        if(sHours.length < 2)   sHours = "0" + sHours;
        if(sMinutes.length < 2) sMinutes = "0" + sMinutes;
        
        var strTime = hours + ':' + minutes + ' ' + ampm;
        return strTime;
    }

     /**
     * Method: renderedCallback
     * @param {*} event 
     * Function: this method is used to get and check if medicare id is present for new task task
     */
    renderedCallback(){       
        //Edit task
        if(this.action === 'Edit' && (this.taskDetail == null || this.taskDetail == undefined)){
            this.byPassValidationOnEdit = false;
            fetchTaskDetail({taskId : this.recordId,action: 'Edit'}).then(result => {
                if (result != null) {
                    this.lstServiceCenter = this.getSelectOptions(result['lstServiceCenter']);
                    this.taskDetail = result['Task'];
                    this.sStatus = (this.taskDetail.Status != null? this.taskDetail.Status: '');
                    this.sType = (this.taskDetail.Type != null? this.taskDetail.Type: '');
                    this.sServiceCenter  = (this.taskDetail.Service_Center__c != null? this.taskDetail.Service_Center__c: '');
                    this.lstDepartment = this.getSelectOptions(result['lstDepartment']);
                    this.sDepartment = (this.taskDetail.Department__c != null? this.taskDetail.Department__c: '');
                    let topicData = result['topic'];
                
                    if(topicData.hasOwnProperty('topicList') && topicData.topicList !==null && topicData.topicList.length > 0){
                        this.isTopicDisabled = false;
                        this.lstTopic = this.getSelectOptions(topicData.topicList)
                    } else {
                        this.isTopicDisabled = true;
                        this.lstTopic = null;
                    }
                    this.sTopic = (this.taskDetail.Topic__c != null? this.taskDetail.Topic__c: ''); 
                    this.sPriority = (this.taskDetail.Priority != null? this.taskDetail.Priority: '');
                    this.sUser = result['User'];
                    this.sDueDate = (this.taskDetail.ActivityDate != null? this.taskDetail.ActivityDate: '');
					if(this.sType === 'Work Task'){
                        this.isTimeDisabled = true;
                    } else if(this.sType ==='Notification Task'){
                        this.CallbackRequestedBool = true;
                    }
                    this.sCallbackRequested = (this.taskDetail.Callback_Requested__c != null? this.taskDetail.Callback_Requested__c: '');
                    if (this.sCallbackRequested === 'Yes'){
                        this.sTimeZone = (this.taskDetail.Time_Zone__c != null? this.taskDetail.Time_Zone__c: ''); 
                        this.sCallBackSlotStartTime = (this.taskDetail.Call_Back_Start__c != null? this.taskDetail.Call_Back_Start__c: '');
                        this.sCallBackSlotEndTime =(this.taskDetail.Call_Back_End__c != null? this.taskDetail.Call_Back_End__c: ''); 
                        this.check = true;
                        this.check1 = true;
						this.checkbool = false;
                            if(this.sCallBackSlotStartTime != null){
                            this.sCallBackSlotStartTime = this.timeformat(this.sCallBackSlotStartTime);
                            }
                            if(this.sCallBackSlotEndTime != null){
                            this.sCallBackSlotEndTime = this.timeformat(this.sCallBackSlotEndTime);
                        }
                    }
                    else if (this.sCallbackRequested === 'No'){
                        this.isTimeDisabled = true;
                        this.startTimeDropdownBoolean = false;
                        this.endTimeDropdownBoolean = false;
                    }
                    this.sTaskNumber = (this.taskDetail.Task_Number__c != null? this.taskDetail.Task_Number__c: '');
                    this.queueName = result['Ownerqueue'];                    
                    //get medicare id
                    this.sMedicareId = result['Medicare'];
                    if(this.sMedicareId) {
                        this.medicareSize = '4';
                    }
                    this.credentialingTask = this.taskDetail.Credentialing_Task__c!= null?this.taskDetail.Credentialing_Task__c:'';
                    this.credentialingTaskCompletionDate = this.taskDetail.Task_Completion_Date__c;

                    this.showSpinner = false;
                }
                this.byPassValidationOnEdit = true;
            })
            .catch(error => {
                this.showSpinner = false;
                this.dispatchEvent(
                    new ShowToastEvent({
                        title: 'Error in fetching task detail',
                        message: error.body.message,
                        variant: 'error',
                    }),
                );
            });
        }
        
        if(this.lstServiceCenter.length === 0 && this.recordId){
            this.getServiceCenter();
        }

        if(this.sMedicareId ==null){
            getMedicareId({myRecId : this.recordId}).then(result => {
                if (result != null) {
                    this.sMedicareId = result;
                    if(this.sMedicareId){
                        this.medicareSize = '4';
                    }
                    this.showSpinner = false;
                }
            })
            .catch(error => {
                this.dispatchEvent(
                    new ShowToastEvent({
                        message: error.message,
                        variant: 'error',
                    }),
                );
                this.showSpinner = false;
            });
        }
        
    }
    
    dropdownBool(event){
        if(!this.isTimeDisabled){
            if(event.target.dataset.id ==='sCallBackSlotStartTime'){
                this.startTimeDropdownBoolean = true;
                this.checkbool = true;
                let sCallBackSlotStartTimeEle = this.template.querySelector(".sCallBackSlotStartTime");
                this.updateFieldValidation(sCallBackSlotStartTimeEle,'');
                this.endTimeDropdownBoolean = false;
            }
            if(event.target.dataset.id ==='sCallBackSlotEndTime'){
                this.endTimeDropdownBoolean = true;
                this.checkbool = true;
                let sCallBackSlotEndTimeEle = this.template.querySelector(".sCallBackSlotEndTime");
                this.updateFieldValidation(sCallBackSlotEndTimeEle,'');
                this.startTimeDropdownBoolean = false;
            }
        }
    }

    // To hide the dropdown of start and end time when lost focus
    dropdownhide(event){
        if(!this.isTimeDisabled){
            if(this.checkbool){
                if(event.target.dataset.id ==='sCallBackSlotStartTime'){
                    this.startTimeDropdownBoolean = true;
                }
                else if(event.target.dataset.id ==='sCallBackSlotEndTime'){
                    this.endTimeDropdownBoolean = true;
                }
                else{
                    this.endTimeDropdownBoolean = false;
                    this.startTimeDropdownBoolean = false;
                    if(this.errorbool){
                        if(this.sCallBackSlotStartTime == null || this.sCallBackSlotStartTime === ''){
                            let sCallBackSlotStartTimeEle = this.template.querySelector(".sCallBackSlotStartTime");
                            this.updateFieldValidation(sCallBackSlotStartTimeEle,'You must enter a value in Callback Slot Start Time and Callback Slot End Time field.');
                        }
                        if(this.sCallBackSlotEndTime == null || this.sCallBackSlotEndTime === ''){
                            let sCallBackSlotEndTimeEle = this.template.querySelector(".sCallBackSlotEndTime");
                            this.updateFieldValidation(sCallBackSlotEndTimeEle,'You must enter a value in Callback Slot Start Time and Callback Slot End Time field.');
                        }
                    }
                    if(this.formaterrorbool){
                        if(!this.check){
                            let sCallBackSlotStartTimeEle = this.template.querySelector(".sCallBackSlotStartTime");
                            this.updateFieldValidation(sCallBackSlotStartTimeEle,'Callback Slot Start Time: Invalid Time Format. Acceptable formats: HH:MM AM/PM or H:MM AM/PM');
                        }
                        if(!this.check1){
                            let sCallBackSlotEndTimeEle = this.template.querySelector(".sCallBackSlotEndTime");
                            this.updateFieldValidation(sCallBackSlotEndTimeEle,'Callback Slot End Time: Invalid Time Format. Acceptable formats: HH:MM AM/PM or H:MM AM/PM');
                        }
                    }
                }
            }
        }
    }
    
    modify_time(){
        var start_time = this.sCallBackSlotStartTime.split(" ");
        var time = start_time[0].split(":");
        var stime = time[0];
        let min = time[1];
        let mins = min;
        var a = start_time[1];
        if(min.length>2)
        {
            mins = min.slice(0,2);
            a = min.slice(2,4);
        }
        a= a.toUpperCase();
        if(a === 'PM' && stime<12) stime = parseInt(stime) + 12;
        start_time = stime + ":" + mins + ":00";
    
        var end_time = this.sCallBackSlotEndTime.split(" ");
        var time1 = end_time[0].split(":");
        var etime = time1[0];
        var b = end_time[1];
        let min1 = time1[1];
        let mins1 = min1;
        if(min1.length>2)
        {
            mins1 = min1.slice(0,2);
            b = min1.slice(2,4);
        }
        b = b.toUpperCase();
        if(b==='PM' && etime<12) etime = parseInt(etime) + 12;
        end_time = etime + ":" + mins1+ ":00";
        
        if (start_time != '' && end_time != '') { 
            let starttime = parseInt(stime)*60 + parseInt(min);
            let endtime = parseInt(etime)*60 + parseInt(min1);
            if (endtime <= starttime) {
                this.endtimebool = true;
            }
            else {
                this.endtimebool = false;
            }
        }
    }

    timefun(event){
        this.checkbool = false;
        this.sCallBackSlotStartTime = event.target.innerText;
        this.startTimeDropdownBoolean = false;
        this.addEndTimeHrs(this.sCallBackSlotStartTime);
        this.check=this.Checkarrival(this.sCallBackSlotStartTime);
    }

    timefun1(event){
        this.checkbool=false;
        this.sCallBackSlotEndTime = event.target.innerText;
        this.check1=this.Checkarrival(this.sCallBackSlotEndTime);
        this.endTimeDropdownBoolean = false;    
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
            minutes = '0.'+minutes;
            minutes = parseFloat(minutes);
            minutes = minutes * 60;
            minutes = Math.round(minutes)
        }
        if(minutes == undefined){
            minutes = '00';
        }
        if(minutes < 10){
            minutes = '0'+ minutes;
        }
        if(hours<10 && hours.toString().length<2){
            hours = '0'+hours;
        }

        let str =  hours + ':' + minutes + ' ' + AMPM;
        return str;
    }

    /**
     * Method: handleServiceCenterChange
     * @param {*} event 
     * Function: this method is used to get availabe combination of service center 
     */
    handleServiceCenterChange(event) {
        this.lstDepartment = [];
        this.sServiceCenter = event != undefined? event.detail.value: this.sServiceCenter;
        this.showSpinner = true;
        this.sDepartment = null;        
		this.lstTopic = [];
		this.isTopicDisabled = true;
		this.sTopic = null;
        	getLstDepartments({sServiceCenter: this.sServiceCenter, myRecId : this.recordId}).then(result => {
			if (result != null) {                
				this.lstDepartment = this.getSelectOptions(result);
				this.sDepartment = null;   
			}
			this.showSpinner = false;
		})
		.catch(error => {
			this.showSpinner = false;
			this.dispatchEvent(
				new ShowToastEvent({
					message: error.message,
					variant: 'error',
				}),
			);
		})
    }

    /**
     * Method: handleDepartmentChange
     * @param {*} event 
     * Function: this method is used to get available combination of department center 
     */
    handleDepartmentChange(event) {
        this.showSpinner = true;
        this.sTopic = null;
        this.sDepartment = event != undefined?event.detail.value:this.sDepartment; 
		this.lstTopic = [];
		getLstTopics({sServiceCenter: this.sServiceCenter,sDepartment: this.sDepartment}).then(result => {
			if (result != null) {   
				if(result.hasOwnProperty('topicList') && result.topicList !==null && result.topicList.length > 0){
					this.isTopicDisabled = false;
					this.lstTopic = this.getSelectOptions(result.topicList);
				} else {
					this.isTopicDisabled = true;
					this.lstTopic = null;
				}
				//when department is changed remove the lookup
				this.template.querySelector('c-custom-lookup-hum').removeItem();
				this.sUser = null;
				this.queueName = result.hasOwnProperty('workQueue') ? result.workQueue : null;
				this.showSpinner = false;                    
			}               
		}).catch(error => {
			this.dispatchEvent(
				new ShowToastEvent({
					message: error.message,
					variant: 'error',
				}),
			);
		})
        
    }    
    boolIsOnceSubmit = false;
    /**
     * Method: handleOnChange
     * @param {*} event 
     * Function: this method reposrts the onchange event on fields on new task / dep task modal 
     */
    handleOnChange(event) {
        if(event.currentTarget.dataset.id == "status"){
            this.sStatus = event.detail.value;
            this.startTimeDropdownBoolean = false;
            this.endTimeDropdownBoolean = false;
        } else if(event.currentTarget.dataset.id == "taskType"){
            this.sType = event.detail.value;
            this.startTimeDropdownBoolean = false;
            this.endTimeDropdownBoolean = false;
            if(this.sType ==='Notification Task'){
                this.CallbackRequestedBool = true;
                this.sCallbackRequested = 'Yes'; 
                if(this.sCallbackRequested === 'Yes'){
                    this.isTimeDisabled = false;
                }
                if(this.sTimeZone == null){
                    this.sTimeZone = '--None--';
                }
                if (!this.sCallBackSlotStartTime){
                    this.sCallBackSlotStartTime = null;
                    this.sCallBackSlotStartTime1 = null;
                }
                if (!this.sCallBackSlotEndTime){
                    this.sCallBackSlotEndTime = null;
                    this.sCallBackSlotEndTime1 = null;
                }
            }
            if(this.sType ==='Work Task'){
                this.CallbackRequestedBool = false;
                this.isTimeDisabled = true; 
            } 
        } else if(event.currentTarget.dataset.id == "callbackRequested"){
            this.sCallbackRequested = event.detail.value;
            this.startTimeDropdownBoolean = false;
            this.endTimeDropdownBoolean = false;
            if(this.sCallbackRequested === 'No'){
                this.sTimeZone = null;
                this.sCallBackSlotStartTime = null;
                this.sCallBackSlotEndTime = null;
                this.sCallBackSlotStartTime1 = null;
                this.sCallBackSlotEndTime1 = null;
                this.isTimeDisabled = true;
            } else if(this.sCallbackRequested === 'Yes'){
                this.isTimeDisabled = false;
                this.sTimeZone = '--None--'; 
            }
        } else if(event.currentTarget.dataset.id == "topic"){
            if(!this.isTopicDisabled){
                this.sTopic = event.detail.value;
                this.startTimeDropdownBoolean = false;
                this.endTimeDropdownBoolean = false;
            } 
        }else if(event.currentTarget.dataset.id == "sCallBackSlotStartTime"){
            this.sCallBackSlotStartTime = event.target.value;
            this.check = this.Checkarrival(this.sCallBackSlotStartTime);
            if(this.check){
                this.addEndTimeHrs(this.sCallBackSlotStartTime);
            }
            this.startTimeDropdownBoolean = false;
            this.endTimeDropdownBoolean = false;
        } else if(event.currentTarget.dataset.id == "sCallBackSlotEndTime"){
            this.sCallBackSlotEndTime = event.target.value;
            this.check1 = this.Checkarrival(this.sCallBackSlotEndTime);
            this.startTimeDropdownBoolean = false;
            this.endTimeDropdownBoolean = false;
        } else if(event.currentTarget.dataset.id == "user"){
            this.sUser = event.detail.selectedRecord;
            this.startTimeDropdownBoolean = false;
            this.endTimeDropdownBoolean = false;  
        } else if(event.currentTarget.dataset.id == "priority"){
            this.sPriority = event.detail.value;
            this.startTimeDropdownBoolean = false;
            this.endTimeDropdownBoolean = false; 
        }  else if(event.currentTarget.dataset.id == "dueDate" ){
            this.sDueDate = event.detail.value;
            this.startTimeDropdownBoolean = false;
            this.endTimeDropdownBoolean = false;         
        } else if(event.currentTarget.dataset.id == "timeZone"){
            if(!this.isTimeDisabled){
            this.sTimeZone = event.detail.value;
            this.startTimeDropdownBoolean = false;
            this.endTimeDropdownBoolean = false;
            } 
		}else if(event.currentTarget.dataset.id == "comment"){
            this.sComment = event.detail.value;
            this.startTimeDropdownBoolean = false;
            this.endTimeDropdownBoolean = false;
		} else if(event.currentTarget.dataset.id == "department"){
            this.handleDepartmentChange(event);
            this.startTimeDropdownBoolean = false;
            this.endTimeDropdownBoolean = false;           
        } else if(event.target.name ===  "serviceCenter"){
            this.sServiceCenter = event.detail.value; 
            this.handleServiceCenterChange(event);
            this.startTimeDropdownBoolean = false;
            this.endTimeDropdownBoolean = false;
        }else if(event.target.name ===  "credentialingTask"){
            this.credentialingTask = event.detail.value; 
        }else if(event.target.name ===  "credentialingTaskCompletionDate"){
            this.credentialingTaskCompletionDate = event.detail.value;  
        }
        if (event.currentTarget.dataset.id !== "user" && event.currentTarget.dataset.id !== "comment" ){
            this.validateAll(event);
        }
    }

     /**
     * Method: Checkarrival
     * @param {*} time 
     * Function: this method is used to check the time format (HH:MM AM/PM || H:MM AM/PM) and returns a boolean
     */
    Checkarrival(time) { 
        let time1= time.toString();
        var regex = /^ *(1[0-2]|0[1-9]|[0-9]):[0-5][0-9] *(a|p|A|P)(m|M) *$/;
        var res = regex.test(time1); 
        return res; 
    }

    getSelectOptions(listOptions) {       
        let tmpOptions = [];
        for (let i=0; i < listOptions.length;i++) {
            let option = {
                label: listOptions[i],value: listOptions[i]
            };
            tmpOptions = [ ...tmpOptions, option ];            
        }
        return tmpOptions;
    }

    /**
     * Method: returnSJSON
     * @param {*} event 
     * Function: if task is there, means flow invoked from task page (create dependent task). Else, flow invoked from case page
     * meaning create new task. based on flow invokes it returns the JSON
     */
    returnSJSON(){        
        if(this.invokedFlowFrom === 'Task' && this.action === 'New'){ //Dependent task from task detail page
            //invokes on task details dependent task page
            return  {
                taskFields : {
                    ActivityDate : this.sDueDate, Priority : this.sPriority, Status : this.sStatus, Type : this.sType,
                    Description: this.sComment ?  this.sComment : null, OwnerId : this.sUser != null ? this.sUser.value : null, Service_Center__c : this.sServiceCenter,
                    Department__c : this.sDepartment, Topic__c : this.sTopic,Work_Queue_View_Name__c : this.queueName,    //WhatId : this.recordId, 
                    QueueOrUserId__c : this.sUser != null ? this.sUser.label : null,
                    Credentialing_Task__c:this.showMCDProviderFields?this.credentialingTask:null,
                    Task_Completion_Date__c:this.showMCDProviderFields?this.credentialingTaskCompletionDate:null,
                },
                taskHistoryFields : {
                    HUM_Parent_TaskID__c : this.recordId
                }
            };
        } else if(this.invokedFlowFrom === 'Task' && this.action === 'Edit'){ //edit task 
            if (this.sType === 'Notification Task') {       
                return  {
                    taskFields : {
                Id : this.recordId,ActivityDate : this.sDueDate ? this.sDueDate : null,Priority : this.sPriority,
                Status : this.sStatus,Type : this.sType,Description: this.sComment  != null ? this.sComment : null,
                OwnerId : this.sUser != null ? this.sUser.value : null,ServiceCenter : this.sServiceCenter,
                Department : this.sDepartment,Topic : this.sTopic,workQueueName : this.queueName,QueueOrUserId: this.sUser != null ? this.sUser.label : null,
                CallbackRequested : this.sCallbackRequested, 
                TimeZone : this.sTimeZone, CallBackSlotStartTime : this.sCallBackSlotStartTime1, CallBackSlotEndTime : this.sCallBackSlotEndTime1
                ,credentialingTask:this.showMCDProviderFields?this.credentialingTask:null
                ,taskCompletionDate:this.showMCDProviderFields?this.credentialingTaskCompletionDate:null
            }
        };
        }
        if (this.sType !== 'Notification Task') {         
            return  {
                taskFields : {
            Id : this.recordId,ActivityDate : this.sDueDate ? this.sDueDate : null,Priority : this.sPriority,
            Status : this.sStatus,Type : this.sType,Description: this.sComment  != null ? this.sComment : null,
            OwnerId : this.sUser != null ? this.sUser.value : null,ServiceCenter : this.sServiceCenter,
            Department : this.sDepartment,Topic : this.sTopic,workQueueName : this.queueName,QueueOrUserId: this.sUser != null ? this.sUser.label : null
            ,credentialingTask:this.showMCDProviderFields?this.credentialingTask:null
            ,taskCompletionDate:this.showMCDProviderFields?this.credentialingTaskCompletionDate:null
        }
    };
    }
        } else {             //new task from case page
            //invokes on case details create new task page
            if (this.sType === 'Notification Task')   {        
            return  {
                taskFields : {
                    ActivityDate : this.sDueDate, Priority : this.sPriority, Status : this.sStatus, Type : this.sType,
                    Description: this.sComment ?  this.sComment : null, OwnerId : this.sUser != null ? this.sUser.value : null, WhatId : this.recordId, ServiceCenter : this.sServiceCenter,
                    Department : this.sDepartment, Topic : this.sTopic, workQueueName : this.queueName, QueueOrUserId : this.sUser != null ? this.sUser.label : null, 
                    CallbackRequested : this.sCallbackRequested, TimeZone : this.sTimeZone, CallBackSlotStartTime : this.sCallBackSlotStartTime1, CallBackSlotEndTime : this.sCallBackSlotEndTime1
                    ,credentialingTask:this.showMCDProviderFields?this.credentialingTask:null
                    ,taskCompletionDate:this.showMCDProviderFields?this.credentialingTaskCompletionDate:null
                }
            };             
            } 
            if (this.sType !== 'Notification Task')   {         
                return  {
                    taskFields : {
                        ActivityDate : this.sDueDate, Priority : this.sPriority, Status : this.sStatus, Type : this.sType,
                        Description: this.sComment ?  this.sComment : null, OwnerId : this.sUser != null ? this.sUser.value : null, WhatId : this.recordId, ServiceCenter : this.sServiceCenter,
                        Department : this.sDepartment, Topic : this.sTopic, workQueueName : this.queueName, QueueOrUserId : this.sUser != null ? this.sUser.label : null,
                        credentialingTask:this.showMCDProviderFields?this.credentialingTask:null,
						taskCompletionDate:this.showMCDProviderFields?this.credentialingTaskCompletionDate:null,
                    }
                };             
                }
        }       
    }

    /**
     * Method: onSaveClick
     * @param {*} event 
     * Function: this method saves the task
     */
    @api
    onSaveClick() {   
        try{   
            const jStruct = this.returnSJSON(); 
            this.startTimeDropdownBoolean=false;
            this.endTimeDropdownBoolean=false;           
            let me = this;
			this.boolIsOnceSubmit = true;
			this.showErrorMsges = false;
            this.showErrorMsgesMCDProvider = false;
			let isFormInvalid = false;
            if(me.sType !=='Notification Task'){
                const inpFields = ['serviceCenter','deparment','dueDate','topic','status','priority','taskType'];
                inpFields.forEach(function (field) {
                    let element = me.template.querySelector("."+field);
                    me.updateFieldValidation(element, '');
                    if (field !== "dueDate") {
                        if(field === "topic" && !me.isTopicDisabled && !element.value){
                            me.updateFieldValidation(element, 'Complete this field');
                        } else if(field === "topic" && me.isTopicDisabled){
                            me.updateFieldValidation(element, '');
                        } else if(field !== "topic" && !element.value) {
                            me.updateFieldValidation(element, 'Complete this field');
                        }
                    }  else if(field === 'dueDate'){
                        if(me.sDueDate && me.today > me.sDueDate ){               
                            me.updateFieldValidation(element, 'Due Date must be today or a future date');                           
                        } else if(!me.sDueDate||(me.sDueDate && me.today <= me.sDueDate)){               
                            me.updateFieldValidation(element, '');
                        }
                    }
                });
                this.showHideErrorMsg();
            }
            if(me.sType ==='Notification Task') {

			const inpFields = ['serviceCenter','deparment','dueDate','topic','status','priority','taskType','callbackRequested','timeZone','sCallBackSlotEndTime','sCallBackSlotStartTime'];
			inpFields.forEach(function (field) {
                let element = me.template.querySelector("."+field);
				me.updateFieldValidation(element, '');
                if (field !== "dueDate"  &&  field !== "sCallBackSlotEndTime" &&  field !== "sCallBackSlotStartTime") {
                    if(field === "topic" && !me.isTopicDisabled && !element.value){
                        me.updateFieldValidation(element, 'Complete this field');
                    } else if(field === "topic" && me.isTopicDisabled){
                        me.updateFieldValidation(element, '');
                    } else if (field === 'timeZone' && me.isTimeDisabled){
                        me.updateFieldValidation(element, '');
                    } else if(field === "serviceCenter" && !element.value ) {
                        me.updateFieldValidation(element, 'Complete this field');
                    } else if(field === "deparment" && !element.value) {
                        me.updateFieldValidation(element, 'Complete this field');
                    } else if (field === 'timeZone' && !me.isTimeDisabled && me.sTimeZone === '--None--'){
                        me.updateFieldValidation(element, 'You must enter a value in Time Zone field.');
                    } 
                    
                }  else if(field === 'dueDate'){
                    if(me.sDueDate && me.today > me.sDueDate ){               
                        me.updateFieldValidation(element, 'Due Date must be today or a future date');                           
                    } else if(!me.sDueDate||(me.sDueDate && me.today <= me.sDueDate)){               
                        me.updateFieldValidation(element, '');
                    }
                    if(me.sType ==='Notification Task'){
                        if(me.sDueDate && me.today > me.sDueDate ){               
                            me.updateFieldValidation(element, 'Due Date must be today or a future date');                           
                        } else if(!me.sDueDate){               
                            me.updateFieldValidation(element, 'Due Date must be entered for a Notification task.');
                        } 
                     }
                }  
                else if (field === "timeZone"){
                    if( me.sCallbackRequested === 'Yes'){
                        if(me.sTimeZone === '--None--'){
                            me.updateFieldValidation(element, 'You must enter a value in Time Zone field.');
                        } 
                        else{
                            me.updateFieldValidation(element, '');
                        }
                    }
                    if( me.sCallbackRequested === 'No'){
                        if(!me.sTimeZone && me.isTimeDisabled){
                            me.updateFieldValidation(element, '');
                        }
                    }
                }
                else if (field === "sCallBackSlotStartTime" ){         
                    if( !me.isTimeDisabled && me.sCallbackRequested !== 'No'){
                        if(!me.sCallBackSlotStartTime){
                            me.updateFieldValidation(element, 'You must enter a value in Callback Slot Start Time and Callback Slot End Time field.');
                            me.errorbool = true;
                        }
                        else if(!me.check && !me.isTimeDisabled && me.sCallBackSlotStartTime !== ''){
                            me.updateFieldValidation(element, 'Callback Slot Start Time: Invalid Time Format. Acceptable formats: HH:MM AM/PM or H:MM AM/PM');
                            me.errorbool = true;
                            me.formaterrorbool = true;
                        } 
                        else{
                            me.updateFieldValidation(element, '');
                        }
                    }if(me.sCallbackRequested === 'No'){
                        if(!me.sCallBackSlotStartTime && me.isTimeDisabled){
                            me.updateFieldValidation(element, '');
                        }
                    }
                    
                }
                else if (field === "sCallBackSlotEndTime"){
                    if( !me.isTimeDisabled && me.sCallbackRequested !== 'No'){
                        if(!me.sCallBackSlotEndTime){
                            me.updateFieldValidation(element, 'You must enter a value in Callback Slot Start Time and Callback Slot End Time field.');
                            me.errorbool = true;
                        }
                        else if(!me.check1 && !me.isTimeDisabled && me.sCallBackSlotEndTime !== ''){
                            me.updateFieldValidation(element, 'Callback Slot End Time: Invalid Time Format. Acceptable formats: HH:MM AM/PM or H:MM AM/PM');
                            me.errorbool = true;
                            me.formaterrorbool = true;
                        } 
                        else if(me.check1 && me.check ){
                            me.modify_time();
                            if (me.endtimebool)
                            {
                                me.updateFieldValidation(element, 'Callback Slot End Time must be greater than Callback Slot Start Time.');
                                me.errorbool = true;
                            }
                        }
                        else{
                            me.updateFieldValidation(element, '');
                        }
                    }if(me.sCallbackRequested === 'No'){
                        if(!me.sCallBackSlotEndTime && me.isTimeDisabled){
                            me.updateFieldValidation(element, '');
                        }
                    }   
                }
                else if (field === "callbackRequested"){
                    if( me.sCallbackRequested === 'No'){
                        let sCallBackSlotStartTimeEle = me.template.querySelector(".sCallBackSlotStartTime");
                        me.updateFieldValidation(sCallBackSlotStartTimeEle, '');
                        let sCallBackSlotEndTimeEle = me.template.querySelector(".sCallBackSlotEndTime");
                        me.updateFieldValidation(sCallBackSlotEndTimeEle, '');
                        let timeZoneEle = me.template.querySelector(".timeZone");
                        me.updateFieldValidation(timeZoneEle, '');
                    }
                }
			});
			this.showHideErrorMsg1();
        }
            if(this.showMCDProviderFields){
                this.showHideCredentialingErrorMsg(); 
                this.showErrorMsges = this.showErrorMsges ==true?true:this.showErrorMsgesMCDProvider;
            }
            if(this.showErrorMsges){
                isFormInvalid = true;
                return;
            }
					
			if (!isFormInvalid) {
				this.showSpinner = true;
				if (this.action === 'Edit') {
                    if(!this.isTimeDisabled){
                    this.timeconvert();
                    }
                    const jStruct = this.returnSJSON();
					editTask({taskData : JSON.stringify(jStruct)}).then(result => {
						this.showSpinner = false;
						this.showErrorMsges=false;
						
						this.dispatchEvent(
                            new ShowToastEvent({
                                message: `Task ${this.sTaskNumber} was successfully ${jStruct?.taskFields?.Status?.toLowerCase() === 'closed' ? 'closed' : 'saved'}`,
								variant: 'success',
							}),
						); 
						const messaage = {
							sourceSystem: 'Task ' + this.sTaskNumber+ ' was successfully saved'
						};
						publish(this.messageContext, REFRESH_TASK_CHANNEL, messaage); 
						// reload page when save from case page
						setTimeout(() =>window.location.reload(), 2000);
						const closeQA = new CustomEvent('close');
						// Dispatches the event.
						this.dispatchEvent(closeQA);                
					})
					.catch(error => {
						this.showSpinner = false;
						this.showErrorMsges = false;
						this.dispatchEvent(
							new ShowToastEvent({
								title: 'Error creating record',
								message: error.message,
								variant: 'error',
							}),
						);
						const closeQA = new CustomEvent('close');
						// Dispatches the event.
						this.dispatchEvent(closeQA);
					});                      
				}  else {
                    if(this.sType === 'Notification Task' && !this.isTimeDisabled && this.check1 && this.check){
                    this.timeconvert();
                    }
                    const jStruct = this.returnSJSON();
					this.createTaskFromCase(jStruct); 
				}         
			}			
			this.queueName = null;
        } catch(error) {          
            this.dispatchEvent(new CustomEvent('close')); //Fire 
            this.showSpinner = false;
            this.showToastMsg(error.message, 'error'); 
        }
    }

     /**
     * Method: createTaskFromCase
     * @param {*} event 
     * Function: this method saves the task and navigates to task
     */
    createTaskFromCase(fields){        
        createTask({taskData : JSON.stringify(fields)}).then(result => {
            if(result === this.label.TASKEDIT_QUEUENOTEXISTS_HUM){
                this.showSpinner = false;
                this.dispatchEvent(new CustomEvent('close')); //Fire 
                this.showToastMsg(result, 'error'); 
                return;
            }
            if(this.b4891201SwitchON == true) {
            getTaskNumber({taskID:result}).then(data =>{ 
                if(data != null)
                this.tasknum= data;
                toastMsge('','Task ' + data+ ' was successfully created' , 'success', 'pester');
            })
            }
            this.showSpinner = false;
            this.dispatchEvent(new CustomEvent('close')); //Fire 
            //window.location.reload();
            this.navigateTO(result);
        })
        .catch(error => {
            this.dispatchEvent(new CustomEvent('close')); //Fire 
            this.showSpinner = false;
            this.showToastMsg(error.message, 'error');            
        });
    
     }
    navigateTO(result){
        this[NavigationMixin.Navigate]({
            type: 'standard__recordPage',
            attributes: {
                recordId: result,
                objectApiName: 'Task',
                actionName: 'view'
            }
        }); 
        
    }
    showToastMsg(mess, variant){
        this.dispatchEvent(
            new ShowToastEvent({
                message: mess,
                variant: variant
            })
        );
    }

    timeconvert(){
        let stime= this.sCallBackSlotStartTime;
        let etime= this.sCallBackSlotEndTime;

        stime = stime.split(' ')
        var ampm = stime[1];
        var hoursmins = stime[0].split(':');
        let hours = hoursmins[0];
        let mins = hoursmins[1];
        let min = hoursmins[1];

        if(mins.length>2){
             min = mins.slice(0,2);
             ampm = mins.slice(2,4);
        }

        if(ampm == undefined){
            ampm = mins.slice(2,4);
        }

        ampm = ampm.toUpperCase();

        if(ampm === 'PM' && hours<12){
            hours = parseInt(hours) + 12;
        }
        if(hours<10 && hours.toString().length<2){
            hours = '0'+hours
        }

        this.sCallBackSlotStartTime1 = hours +':'+min+':00.000Z';

        etime = etime.split(' ')
        var ampm1 = etime[1];
        var hoursmins1 = etime[0].split(':');
        let hours1 = hoursmins1[0];
        let mins1 = hoursmins1[1];
        let min1 = hoursmins1[1];

        if(mins1.length>2){
            min1 = mins1.slice(0,2);
            ampm1 = mins1.slice(2,4);
        }
        if(ampm1 == undefined){
            ampm1 = mins1.slice(2,4);
        }
        ampm1=ampm1.toUpperCase();

        if(ampm1 === 'PM' && hours1<12){
            hours1 = parseInt(hours1) + 12;
        }
        if(hours1<10  && hours1.toString().length<2){
            hours1 = '0'+hours1
        }

        this.sCallBackSlotEndTime1 = hours1 +':'+min1+':00.000Z';
    }

    /**
    * Method: validateAll
    * @param {*} event 
    * Function: this method checks validation 
    */
    validateAll(event){ 
        if(this.sType !== 'Notification Task')  {
            if (event.target.name !== 'dueDate' ) {
                if(this.isTopicDisabled){
                    let topicEle = this.template.querySelector(".topic");
                    this.updateFieldValidation(topicEle, '');
                } 
                if (event.target.value) {
                    this.updateFieldValidation(event.target, '');
                } else {
                    this.updateFieldValidation(event.target, 'Complete this field');
                } 
                if(!this.sDueDate ){
                    let dueDateEle = this.template.querySelector(".dueDate");
                    this.updateFieldValidation(dueDateEle, '');
                }
            } else if(event.target.name === 'dueDate'){
                if(this.sDueDate && this.today > this.sDueDate ){               
                    this.updateFieldValidation(event.target, 'Due Date must be today or a future date');
                       
                } else if(!this.sDueDate||(this.sDueDate && this.today <= this.sDueDate)){               
                    this.updateFieldValidation(event.target, '');
                }
            }
            this.showHideErrorMsg();
        }
        if(this.sType === 'Notification Task'){
             if (event.target.name === 'callbackRequested'){
                if( this.sCallbackRequested === 'No'){
                    let sCallBackSlotStartTimeEle = this.template.querySelector(".sCallBackSlotStartTime");
                    this.updateFieldValidation(sCallBackSlotStartTimeEle, '');
                    let sCallBackSlotEndTimeEle = this.template.querySelector(".sCallBackSlotEndTime");
                    this.updateFieldValidation(sCallBackSlotEndTimeEle, '');
                    let timeZoneEle = this.template.querySelector(".timeZone");
                    this.updateFieldValidation(timeZoneEle, '');
                }
            }
            else if (event.target.name !== 'dueDate' && event.target.name !== 'timeZone' && event.target.name !== 'sCallBackSlotStartTime' && event.target.name !== 'sCallBackSlotEndTime') {
                if(this.isTopicDisabled){
                    let topicEle = this.template.querySelector(".topic");
                    this.updateFieldValidation(topicEle, '');
                } 
                if (event.target.value) {
                    this.updateFieldValidation(event.target, '');
                } else {
                    this.updateFieldValidation(event.target, 'Complete this field');
                } 
            } else if(event.target.name === 'dueDate'){
                if(this.sDueDate && this.today > this.sDueDate ){               
                    this.updateFieldValidation(event.target, 'Due Date must be today or a future date');
                    
                } else if(!this.sDueDate||(this.sDueDate && this.today <= this.sDueDate)){               
                    this.updateFieldValidation(event.target, '');
                }
                if(this.sType ==='Notification Task'){
                    let dueDateEle = this.template.querySelector(".dueDate");
                    if(this.sDueDate && this.today > this.sDueDate ){            
                        this.updateFieldValidation(dueDateEle, 'Due Date must be today or a future date');                           
                    } else if(!this.sDueDate){               
                        this.updateFieldValidation(dueDateEle, 'Due Date must be entered for a Notification task.');
                    } 
                }
            }
            else if (event.target.name === 'timeZone'){
                let timeZoneEle = this.template.querySelector(".timeZone");
                if( !this.isTimeDisabled && this.sCallbackRequested !== 'No'){
                    if(this.sTimeZone === '--None--'){
                        this.updateFieldValidation(timeZoneEle, 'You must enter a value in Time Zone field.');
                    } 
                    else{
                        this.updateFieldValidation(timeZoneEle, '');
                    }
                }
                else{
                    this.updateFieldValidation(timeZoneEle, '');
                }
            }
            else if (event.target.name === 'sCallBackSlotStartTime'){
                let sCallBackSlotStartTimeEle = this.template.querySelector(".sCallBackSlotStartTime");
                let sCallBackSlotEndTimeEle = this.template.querySelector(".sCallBackSlotEndTime");
                if( this.sCallbackRequested !== 'No'){
                    if(!this.sCallBackSlotStartTime ){
                        this.updateFieldValidation(sCallBackSlotStartTimeEle, 'You must enter a value in Callback Slot Start Time and Callback Slot End Time field.');
                        this.errorbool = true;
                    } 
                    else{
                        this.updateFieldValidation(sCallBackSlotStartTimeEle, '');
                    }
                }
            }else if (event.target.name === 'sCallBackSlotEndTime'){
                let sCallBackSlotEndTimeEle = this.template.querySelector(".sCallBackSlotEndTime");
                if( this.sCallbackRequested !== 'No'){
                    if(!this.sCallBackSlotEndTime ){
                        this.updateFieldValidation(sCallBackSlotEndTimeEle, 'You must enter a value in Callback Slot Start Time and Callback Slot End Time field.');
                        this.errorbool = true;
                    }
                    else{
                        this.updateFieldValidation(sCallBackSlotEndTimeEle, '');
                    }
                }
            }
            this.showHideErrorMsg1();
        }
        if(this.showMCDProviderFields){
            if(event.target.name === 'credentialingTask' || event.target.name === 'credentialingTaskCompletionDate' || (event.target.name === "status" && this.showErrorMsgesMCDProvider)){
                this.showHideCredentialingErrorMsg();                
            } 
            this.showErrorMsges = this.showErrorMsges ==true?true:this.showErrorMsgesMCDProvider;          
        }
    }
	
    showHideErrorMsg(){
        const inpFields = ['serviceCenter','deparment','dueDate','topic','status','priority','taskType'];
        let validateFinal = {};
        let me = this;
        inpFields.forEach(function (field) { //Iterate only when the validation is successfull	
            let element = me.template.querySelector("."+field);			
            validateFinal[field] = element.reportValidity();
        });
        if (validateFinal.serviceCenter && validateFinal.deparment  && validateFinal.topic && validateFinal.status && validateFinal.priority  &&  validateFinal.taskType  &&  validateFinal.dueDate){
            this.showErrorMsges=false;
        } else {
            this.showErrorMsges=true;
        }
    }

    showHideErrorMsg1(){
        const inpFields = ['serviceCenter','deparment','dueDate','topic','status','priority','taskType','callbackRequested','timeZone','sCallBackSlotStartTime','sCallBackSlotEndTime'];
        let validateFinal = {};
        let me = this;
        let myvar = true;
        inpFields.forEach(function (field) { //Iterate only when the validation is successfull	
            let element = me.template.querySelector("."+field);	
            if(element != null )		
            {
            validateFinal[field] = element.reportValidity();
            myvar = false;    
        } else{
            myvar = true;
        }
        });

        if (validateFinal.serviceCenter && validateFinal.deparment  && validateFinal.topic && validateFinal.status && validateFinal.priority  &&  validateFinal.taskType  &&  validateFinal.dueDate && validateFinal.callbackRequested && validateFinal.timeZone && validateFinal.sCallBackSlotStartTime && validateFinal.sCallBackSlotEndTime){ 
            this.showErrorMsges=false;
        } else if (myvar){
            this.showErrorMsges=false;
        } else {
            this.showErrorMsges=true;
        }
    }

	updateFieldValidation(field, message) {
        field.setCustomValidity(message);
        field.reportValidity();
    }
	
	/**
     * Method: onCancelClick
     * @param {*} event 
     * Function: this method invokes unsaved changes popup on cancel clicked for New task and depedennt task 
     */
    @api
    async onCancelClick() {        
        if(this.invokedFlowFrom === this.FLOW_FROM_TASK && this.action === this.ACTION_EDIT){
            const closeQA = new CustomEvent('close');
            // Dispatches the event.
            this.dispatchEvent(closeQA);   
        }
         else {
            //for and new task from case page, launch popup for changes
            this.showActionModal = true;
        }
    }
      
    onContinueClick(){        
        this.showActionModal = false; //close unsaved changes popup
        this.dispatchEvent(new CloseActionScreenEvent());
        //for dependent task action invoked from aura, close aura modal
        if(this.invokedFlowFrom === this.FLOW_FROM_TASK && this.action === this.ACTION_NEW){
            const closeQA = new CustomEvent('close');
            this.dispatchEvent(closeQA);  
        }
    }

    onUnsavedCancelClick(){
        this.showActionModal = false; //close unsaved changes popup
    }

    showHideCredentialingErrorMsg(){
        this.showErrorMsgesMCDProvider = false;
        let credentialingTaskEle = this.template.querySelector(".credentialingTaskCls");
        let credentialingTaskCompletionDateEle = this.template.querySelector(".credentialingTaskCompletionDateCls");
        if(this.sStatus == 'Closed' && (this.credentialingTask == null || this.credentialingTask == '' || this.credentialingTaskCompletionDate == null || this.credentialingTaskCompletionDate == '')){
            this.showErrorMsgesMCDProvider = true;
            if(this.credentialingTask == null || this.credentialingTask == ''){ 
                this.updateFieldValidation(credentialingTaskEle, 'Complete this field');
            }else{
                this.updateFieldValidation(credentialingTaskEle, '');
            }
            if(this.credentialingTaskCompletionDate == null || this.credentialingTaskCompletionDate == ''){
                this.updateFieldValidation(credentialingTaskCompletionDateEle, 'Complete this field');
            }else{
                this.updateFieldValidation(credentialingTaskCompletionDateEle, '');
            }
        }else{
            this.showErrorMsgesMCDProvider = false;
            this.updateFieldValidation(credentialingTaskEle, '');
            this.updateFieldValidation(credentialingTaskCompletionDateEle, '');
        }    
    }
}