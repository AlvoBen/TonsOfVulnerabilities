/************************************************************************************************************************************************************************************
Apex Class Name :  WorkItems_LC_HUM
Created Date     : 02/07/2023
Function         :
**Modification Log
* Developer Name          Code Review #         Date                       Description
************************************************************************************************************************************************************************************
* Shaik Mujeebur Rahaman                        02/07/2023                US-4129681 T1PRJ0170850 - MF 22334 - Lightning - Today's Task (My Due Work)
* Shaik Mujeebur Rahaman                        03/04/2023                US-4424960 T1PRJ0170850 - MF 22334 - Lightning -My Due Work Component Overlays 
************************************************************************************************************************************************************************************/
import { LightningElement, api, track, wire } from 'lwc';
import getOverdueTasks from "@salesforce/apex/WorkItems_LC_HUM.getOverdueTasks";
import getNeardueTasks from "@salesforce/apex/WorkItems_LC_HUM.getNeardueTasks";
import { NavigationMixin } from 'lightning/navigation';

const columns = [
    { label: 'Case or Task ID', fieldName: 'Id', type: 'url', hideDefaultActions: true, typeAttributes: { label: { fieldName: 'subject' }, target: '_self', tooltip: { fieldName: 'comment' } } },
    { label: 'Due Date', fieldName: 'dueDate', type: 'date-local', hideDefaultActions: true, sorttable: 'true', typeAttributes: { month: '2-digit', day: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit', hour12: true }, cellAttributes: { class: { fieldName: 'redClass' } } },

];

export default class TaskDueCmp extends NavigationMixin(LightningElement) {
    overdueData = [];
    neardueData = [];
    columns = columns;
    overdueCount;
    neardueCount;
    sortBy;
    sortDirection;
    showSpinner;
    noOverdueRecords;
    noNeardueRecords;

    connectedCallback() {
        this.baseUrl = window.location.origin;
        this.getTasks();
    }

    getTasks() {
        this.showSpinner = true;
        getOverdueTasks()
            .then((result) => {
                if (result.length > 0) {
                    this.noOverdueRecords = false;
                    let overdueWrapper = result;
                    overdueWrapper.sort(function (a, b) {
                        return new Date(a.field2) - new Date(b.field2);
                    });
                    let tempData = [];
                    let tempTask = {};
                    if (result.length >= 6) {
                        for (var odw = 0; odw < 6; odw++) {
                            tempTask = { 'Id': '', 'field1': '', 'field2': '', 'redClass': 'slds-text-color_error', 'comment': '' };
                            tempTask.Id = '/' + overdueWrapper[odw].Id;
                            tempTask.subject = overdueWrapper[odw].field1;
                            tempTask.dueDate = overdueWrapper[odw].field2;
                            tempTask.comment = overdueWrapper[odw].comment;
                            tempData.push(tempTask);
                        }
                    } else {
                        for (var odw = 0; odw < result.length; odw++) {
                            tempTask = { 'Id': '', 'field1': '', 'field2': '', 'redClass': 'slds-text-color_error', 'comment': '' };
                            tempTask.Id = '/' + overdueWrapper[odw].Id;
                            tempTask.subject = overdueWrapper[odw].field1;
                            tempTask.dueDate = overdueWrapper[odw].field2;
                            tempTask.comment = overdueWrapper[odw].comment;
                            tempData.push(tempTask);
                        }
                    }

                    this.overdueData = tempData;
                    if (result.length > 6) {
                        this.overdueCount = '6+';
                    }
                    else {
                        this.overdueCount = result.length;
                    }
                }
                else {
                    this.overdueCount = 0;
                    this.noOverdueRecords = true;
                }
                this.showSpinner = false;
            })
            .catch((error) => {
                console.log("error==>", error);
            });

        getNeardueTasks()
            .then((results) => {
                if (results.length > 0) {
                    this.noNeardueRecords = false;
                    let neardueWrapper = results;
                    neardueWrapper.sort(function (a, b) {
                        return new Date(a.field2) - new Date(b.field2);
                    });
                    let tempData = [];
                    let tempTask = {};
                    if (results.length >= 6) {
                        for (var ndw = 0; ndw < 6; ndw++) {
                            tempTask = { 'Id': '', 'field1': '', 'field2': '', 'redClass': '', 'comment': '' };
                            tempTask.Id = '/' + neardueWrapper[ndw].Id;
                            tempTask.subject = neardueWrapper[ndw].field1;
                            tempTask.dueDate = neardueWrapper[ndw].field2;
                            tempTask.comment = neardueWrapper[ndw].comment;
                            tempData.push(tempTask);
                        }
                    } else {
                        for (var ndw = 0; ndw < results.length; ndw++) {
                            tempTask = { 'Id': '', 'field1': '', 'field2': '', 'redClass': '', 'comment': '' };
                            tempTask.Id = '/' + neardueWrapper[ndw].Id;
                            tempTask.subject = neardueWrapper[ndw].field1;
                            tempTask.dueDate = neardueWrapper[ndw].field2;
                            tempTask.comment = neardueWrapper[ndw].comment;
                            tempData.push(tempTask);
                        }
                    }

                    this.neardueData = tempData;
                    if (results.length > 6) {
                        this.neardueCount = '6+';
                    }
                    else {
                        this.neardueCount = results.length;
                    }

                } else {
                    this.neardueCount = 0;
                    this.noNeardueRecords = true;
                }
                this.showSpinner = false;
            })
            .catch((error) => {
                console.log("error==>", JSON.stringify(error));
            });
    }
    doOverdueSorting(event) {
        this.sortBy = event.detail.fieldName;
        this.sortDirection = event.detail.sortDirection;
        this.sortOverdueData(this.sortBy, this.sortDirection);
    }

    sortOverdueData(fieldname, direction) {
        let parseData = JSON.parse(JSON.stringify(this.overdueData));
        let keyValue = (a) => {
            return a[fieldname];
        };
        let isReverse = direction === 'asc' ? 1 : -1;
        parseData.sort((x, y) => {
            x = keyValue(x) ? keyValue(x) : '';
            y = keyValue(y) ? keyValue(y) : '';
            return isReverse * ((x > y) - (y > x));
        });
        this.overdueData = parseData;
    }

    doNeardueSorting(event) {
        this.sortBy = event.detail.fieldName;
        this.sortDirection = event.detail.sortDirection;
        this.sortNeardueData(this.sortBy, this.sortDirection);
    }

    sortNeardueData(fieldname, direction) {
        let parseData = JSON.parse(JSON.stringify(this.neardueData));
        let keyValue = (a) => {
            return a[fieldname];
        };
        let isReverse = direction === 'asc' ? 1 : -1;
        parseData.sort((x, y) => {
            x = keyValue(x) ? keyValue(x) : '';
            y = keyValue(y) ? keyValue(y) : '';
            return isReverse * ((x > y) - (y > x));
        });
        this.neardueData = parseData;
    }

    viewAllOverdue() {
        var compDefinition = {
            componentDef: "c:my_OverdueworkHum"
        };
        // Base64 encode the compDefinition JS object
        var encodedCompDef = btoa(JSON.stringify(compDefinition));
        this[NavigationMixin.Navigate]({
            type: 'standard__webPage',
            attributes: {
                url: '/one/one.app#' + encodedCompDef,
            },
        },
            true
        )
    }

    viewAllNeardue() {
        var compDefinition = {
            componentDef: "c:myneardueworkHum"
        };
        // Base64 encode the compDefinition JS object
        var encodedCompDef = btoa(JSON.stringify(compDefinition));
        this[NavigationMixin.Navigate]({
            type: 'standard__webPage',
            attributes: {
                url: '/one/one.app#' + encodedCompDef,
            },
        },
            true
        )
    }
}