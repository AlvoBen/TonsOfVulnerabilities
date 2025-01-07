/*******************************************************************************************************************************
LWC JS Name : qsTaskInformationSection.js
Function    : This JS serves as controller to qsTaskInformationSection.html.

Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Pooja Kumbhar                                         12/09/2022                 initial version(azure #)
* Pooja Kumbhar	                                        06/28/2023                 US4773013 - T1PRJ0865978 - INC2395527 - Lightning Command Center RAID#041: every time user  getting a call creating a task automatically.
*********************************************************************************************************************************/

import { LightningElement, track, api } from 'lwc';

export default class qsTaskInformationSection extends LightningElement {

    @track isCreateTask = false;
    @track staskduedate;
    @track sTaskComment;
    @track taskInformation = [];
    @track errorMessage = [];
    @track isError = false;
    @track today;
    isdisabled = false;
    errormsg = '';
    TaskTypeSelected = 'Notification Task'


    connectedCallback() {
        //set min date value for due date
        this.today = new Date();
        let dd = this.today.getDate();
        let mm = this.today.getMonth() + 1;
        let yyyy = this.today.getFullYear();
        if (dd < 10) {
            dd = '0' + dd;
        }
        if (mm < 10) {
            mm = '0' + mm;
        }
        this.today = yyyy + '-' + mm + '-' + dd;

    }

    //public property to fetch Data and send it back to parent component
    @api get fetchData() {
        return
    }

    set fetchData(value) {
        if (value != 'false') {
           this.taskValidation();
        }
    }

    // to disable the component on warning meassages.
    @api get disable() {
        return
    }
    set disable(flag) {
        this.isdisabled = flag;
    }

    // public property to reset the section
    @api get reset() {
        return
    }

    set reset(value) {
        if (value.split('_')[0] == 'full') {
            this.isCreateTask = false;
            this.TaskTypeSelected = 'Notification Task';
            this.staskduedate = '';
            this.sTaskComment = '';
        }
    }

    @api get taskreset() {
        return
    }

    set taskreset(value) {
        if (value.split('_')[0] == 'true') {
            this.isCreateTask = false;
            this.TaskTypeSelected = 'Notification Task';
            this.staskduedate = '';
            this.sTaskComment = '';
        }
    }

    //task type picklist
    get taskTypeOptions() {
        return [
            { label: '--None--', value: '--None--' },
            { label: 'Work Task', value: 'Work Task' },
            { label: 'Notification Task', value: 'Notification Task' },
            { label: 'Extension Request', value: 'Extension Request' }
        ];
    }

    handleChange(event) {
        if (event.target.checked) {
            this.isCreateTask = true;
        } else {
            this.isCreateTask = false;
            this.TaskTypeSelected = 'Notification Task';
            this.staskduedate = '';
            this.sTaskComment = '';
        }
    }

    handleOnChange(event) {
        if (event.currentTarget.dataset.id == "taskType") {
            this.TaskTypeSelected = event.detail.value;
        } else if (event.currentTarget.dataset.id == "dueDate") {
            this.staskduedate = event.detail.value;
        } else if (event.currentTarget.dataset.id == "comment") {
            this.sTaskComment = event.detail.value;
        }
    }
    taskValidation() {
        if (this.isCreateTask == true) {
            if (this.TaskTypeSelected === '--None--' || this.TaskTypeSelected === '') {
                this.isError = true;
                this.errormsg = 'Please Select Task Type Value.';
            } else if (this.TaskTypeSelected == 'Notification Task' && (this.staskduedate == '' || this.staskduedate == null || this.staskduedate == undefined)) {
                this.isError = true;
                this.errormsg = 'Due Date must be entered for a Notification Task.';
            } else if ((this.staskduedate != '' || this.staskduedate != null || this.staskduedate != undefined) && (this.staskduedate < this.today)) {
                this.isError = true;
                this.errormsg = 'Due Date must be today or a future date.';
            } else {
                this.isError = false;
                this.errormsg = '';
            }
            if (this.isError == true && this.errormsg != '') {
                this.errorMessage = [{
                    MessageType: 'Error',
                    Message: this.errormsg,
                    Source: '',
                    DynamicValue: []
                }];
                this.dispatchEvent(new CustomEvent('datafeed', {
                    detail: { error: this.errorMessage }
                }));
            } else {
                this.taskInformation = {
                    taskType: this.TaskTypeSelected,
                    duedate: this.staskduedate,
                    sTaskComment: this.sTaskComment,
                    isCreateTask: this.isCreateTask
                };
                if (this.taskInformation != '') {
                    this.dispatchEvent(new CustomEvent('datafeed', {
                        detail: { data: this.taskInformation }
                    }));
                }
            }
        } else {

            this.taskInformation = {
                taskType: this.TaskTypeSelected,
                duedate: this.staskduedate,
                sTaskComment: this.sTaskComment,
                isCreateTask: this.isCreateTask
            };
            if (this.taskInformation != '') {
                this.dispatchEvent(new CustomEvent('datafeed', {
                    detail: { data: this.taskInformation }
                }));
            }

        }

    }


}