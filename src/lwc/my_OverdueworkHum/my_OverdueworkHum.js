/************************************************************************************************************************************************************************************
Apex Class Name :  WorkItems_LC_HUM
Created Date     : 02/07/2023
Function         :
**Modification Log
* Developer Name          Code Review #         Date                       Description
************************************************************************************************************************************************************************************
* Shaik Mujeebur Rahaman                        02/07/2023                US-4129681 T1PRJ0170850 - MF 22334 - Lightning - Today's Task (My Due Work)
************************************************************************************************************************************************************************************/
import { LightningElement } from 'lwc';
import getOverdueTasks from "@salesforce/apex/WorkItems_LC_HUM.getOverdueTasks";
import { invokeWorkspaceAPI } from 'c/workSpaceUtilityComponentHum';


const columns = [
    { label: 'Case or Task ID', fieldName: 'Id', type: 'url', typeAttributes: { label: { fieldName: 'subject' }, target: '_self' } },
    { label: 'Due Date', fieldName: 'dueDate', type: 'date-local', sorttable: 'true', typeAttributes: { month: '2-digit', day: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit', hour12: true }, cellAttributes: { class: { fieldName: 'redClass' } } },

]

export default class TaskDueCmp extends LightningElement {
    overdueData = [];

    columns = columns;
    overdueCount;

    sortBy;
    sortDirection;
    showSpinner;


    connectedCallback() {
        this.baseUrl = window.location.origin;
        this.getTasks();
    }

    getTasks() {
        this.showSpinner = true;
        getOverdueTasks()
            .then((result) => {

                //console.log('test',JSON.stringify(result));

                let overdueWrapper = result;
                let tempData = [];
                let tempTask = {};

                for (var odw in overdueWrapper) {
                    tempTask = { 'Id': '', 'field1': '', 'field2': '', 'redClass': 'slds-text-color_error' };
                    tempTask.Id = '/' + overdueWrapper[odw].Id;
                    tempTask.subject = overdueWrapper[odw].field1;
                    tempTask.dueDate = overdueWrapper[odw].field2;
                    tempData.push(tempTask);
                }
                tempData.sort(function (a, b) {
                    return new Date(a.dueDate) - new Date(b.dueDate);
                });

                this.overdueData = tempData;
                this.overdueCount = result.length;

                this.showSpinner = false;
            })
            .catch((error) => {
                //console.log("error==>" , JSON.stringify(error));
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

    renderedCallback() {
        this.updateTabLabel();
    }

    updateTabLabel() {
        invokeWorkspaceAPI('isConsoleNavigation').then(isConsole => {
            if (isConsole) {
                invokeWorkspaceAPI('getFocusedTabInfo').then(focusedTab => {

                    invokeWorkspaceAPI('setTabIcon', {
                        tabId: focusedTab.tabId,
                        icon: 'standard:home'
                    });
                    invokeWorkspaceAPI('setTabLabel', {
                        tabId: focusedTab.tabId,
                        label: 'My Overdue Work'
                    });
                });
            }
        });
    }


}