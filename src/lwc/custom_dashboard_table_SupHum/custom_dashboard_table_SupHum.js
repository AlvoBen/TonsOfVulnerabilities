/*
LWC Name        : custom_dashboard_tableSupHum.html
Function        : Custom_dashboard_table used to show datable for suervisor profile

Modification Log:
* Developer Name                  Date                          Description
*------------------------------------------------------------------------------------------------------------------------------
* Shailesh B                      07/03/2022                    Original Version 
* Gowthami T                      07/03/2022                    Original Version 
****************************************************************************************************************************/

import { LightningElement, track, api, wire } from 'lwc';
import {
    subscribe,
    unsubscribe,
    MessageContext
} from "lightning/messageService";
import CASE_CHANNEL from "@salesforce/messageChannel/CaseTaskData__c";
import closeCase from '@salesforce/apex/Hum_CaseTasks_LWC.closeCase';
import ccprofileLabel from '@salesforce/label/c.HUMAgencyCCSupervisor';
import NoWork_CaseTaskView_HUM from '@salesforce/label/c.NoWork_CaseTaskView_HUM';
import NoWork_CCSupervisor_CaseTaskView_HUM from '@salesforce/label/c.NoWork_CCSupervisor_CaseTaskView_HUM';
import sPharmacyUserSupervisorYesLabel from '@salesforce/label/c.PharmacyUserSupervisorYes_HUM';
import updateOwnerOfAssignedRecords from '@salesforce/apex/Hum_CaseTasks_LWC.updateOwnerOfAssignedRecords';
import CheckBoxInput_CaseTaskView_HUM from '@salesforce/label/c.CheckBoxInput_CaseTaskView_HUM';
import CloseCase_SuccessMsg from '@salesforce/label/c.CloseCase_SuccessMsg';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';

import { loadStyle } from "lightning/platformResourceLoader";
import WrappedHeaderTable from "@salesforce/resourceUrl/WrappedHeaderTable";

export default class Custom_dashboard_table_SupHum extends LightningElement {
    label = {
        CloseCase_SuccessMsg,
        CheckBoxInput_CaseTaskView_HUM,
        ccprofileLabel,
        sPharmacyUserSupervisorYesLabel,
        NoWork_CCSupervisor_CaseTaskView_HUM,
        NoWork_CaseTaskView_HUM
    };
    @api columnSet;
    @api columns1;
    @track mapCaseDTO = {};
    @api columns
    subscription = null;
    @track sortBy;
    @track caseTaskList;
    //pagination logic
    @track value;
    @track error;
    @api sortedDirection = 'asc';
    @api sortedBy = 'Name';
    @api searchKey = '';
    result;
    @track draftValues = [];
    @track page = 1;
    @track items = [];
    @track startingRecord = 1;
    @track endingRecord = 0;
    @track pageSize = 10;
    @track totalRecountCount = 0;
    @track totalPage = 0;
    isLoading = false;
    @track allPresentViewOnPanel = [];
    sortDirection = 'asc';
    currentProfile;
    sPharmacyUser;
    @api originalCaseTaskList = [];
    connectedCallback() {

        this.handleSubscribe();
    }
    renderedCallback() {
        if (!this.stylesLoaded) {
            Promise.all([loadStyle(this, WrappedHeaderTable)])
                .then(() => {
                    this.stylesLoaded = true;
                })
                .catch((error) => {
                });
        }
    }
    @wire(MessageContext)
    messageContext;
    count;
    handleSubscribe() {
        if (this.subscription) {
            return;
        }
        this.subscription = subscribe(this.messageContext, CASE_CHANNEL, (message) => {
            //push the outside part of () into array to match, to show unmatched views in red color
            for (let i = 0; i < message.allPresentViewOnPanel.length; i++) {
                if (message.allPresentViewOnPanel[i].indexOf('(') > -1) {
                    this.allPresentViewOnPanel.push(message.allPresentViewOnPanel[i].substring(0, message.allPresentViewOnPanel[i].indexOf('(')).trim());
                }
            }
            this.originalCaseTaskList = message.tableData;
            this.totalOpenTaskCount = 0;
            this.totalOpenCaseCount = 0;
            for(let i = 0; i< this.originalCaseTaskList.length; i++){
                if(this.originalCaseTaskList[i].sFilterType == 'Case'){
                    this.totalOpenCaseCount += 1; 
                }
                else{
                    this.totalOpenTaskCount += 1; 
                    }
                
            }
            this.dispatchEvent( new CustomEvent( 'fetchopencasetaskcount', {
            detail: {caseCount : this.totalOpenCaseCount , taskCount : this.totalOpenTaskCount }
        } ) );
            this.originalCaseTaskList = message.tableData;
            this.showTablePagination(message.tableData);
            if (message.currentProfile) {
                this.currentProfile = message.currentProfile;
            }
            if (message.sPharmacyUser)
                this.sPharmacyUser = message.sPharmacyUser
        });
    }
    /*
    * Method called when cases/tasks selected on results
    */
    handleClick(event) {
        let cCount = this.selection.length;
        //check for case owner change, all case should be selected, otheriwse disable button
        let isTaskSelected = false;
        let idsToPush = [];
        let mapCaseDTO = {};
        for (let i = 0; i < cCount; i++) {
            //idsToPush.push(this.alltableRowsSelectedWrp[i].sCaseTaskId);
            idsToPush = this.selection;
            if (this.alltableRowsSelectedWrp[i].sCaseTaskId.slice(0, 3) === '00T') {
                isTaskSelected = true;
            }
            mapCaseDTO[this.alltableRowsSelectedWrp[i].sCaseTaskId] = this.alltableRowsSelectedWrp[i];
        }
        this.mapCaseDTO = mapCaseDTO;
        const detail = {
            cCount,
            isTaskSelected,
            idsToPush,
            mapCaseDTO
        };
        const checkboxEvent = new CustomEvent('checkboxcheck', {
            detail
        });
        this.dispatchEvent(checkboxEvent);
    }
    selection = [];
    alltableRowsSelectedWrp = [];
    /*
    * Method fired upon rowSelection event 
    */
    rowSelection(evt) {
        // List of selected items from the data table event.
        let updatedItemsSet = new Set();
        // List of selected items we maintain.
        let selectedItemsSet = new Set(this.selection);
        // List of items currently loaded for the current view.
        let loadedItemsSet = new Set();
        let alltableRowsSelectedSet = new Set(this.alltableRowsSelectedWrp);
        this.caseTaskList.map((event) => {
            loadedItemsSet.add(event);
        });
        //sCaseTaskId
        if (evt.detail.selectedRows) {
            evt.detail.selectedRows.map((event) => {
                updatedItemsSet.add(event);
            });
            // Add any new items to the selection list
            updatedItemsSet.forEach((item) => {
                if (!selectedItemsSet.has(item.sCaseTaskId)) {
                    selectedItemsSet.add(item.sCaseTaskId);
                    alltableRowsSelectedSet.add(item);
                }
            });
        }
        loadedItemsSet.forEach((item) => {
            if (selectedItemsSet.has(item.sCaseTaskId) && !updatedItemsSet.has(item)) {
                // Remove any items that were unselected.
                selectedItemsSet.delete(item.sCaseTaskId);
                alltableRowsSelectedSet.delete(item);
            }
        });
        this.selection = [...selectedItemsSet];
        this.alltableRowsSelectedWrp = [...alltableRowsSelectedSet];
        this.attachrecord();
    }
    attachrecord() {
        this.template.querySelector(
            '[data-id="datarow"]'
        ).selectedRows = this.selection;
    }
    /*
    * Method handles Pagination of results 
    */
    @api
    showTablePagination(tableData) {
        this.caseTaskList = tableData.map(item => ({
            ...item,
            sCaseTaskUrl: item.sFilterType === 'Case' ? '/lightning/r/Case/' + item['sCaseTaskId'] + '/view' : '/lightning/r/Task/' + item['sCaseTaskId'] + '/view',
            sCaseComment: item['sCaseTaskComment'],//'Last comment created on 22/02/2020 09:30 AM',
            sInteractingAboutIdUrl: item.sInteractingAbout !== undefined ? '/lightning/r/Account/' + item.sAccount + '/view' : '',
            sInteractingWithIdUrl: item.sInteractingWithId !== undefined ? '/lightning/r/Account/' + item.sInteractingWithId + '/view' : '',
            accountColor: "slds-text-color_success",
            viewColor: !this.allPresentViewOnPanel.includes(item.sCaseTaskView) ? 'slds-text-color_error' : null
        }));

        this.page = 1;
        this.count = 1;
        this.items = this.caseTaskList;
        this.totalRecountCount = this.caseTaskList.length;
        this.totalPage = Math.ceil(this.totalRecountCount / this.pageSize);
        this.caseTaskList = this.items.slice(0, this.pageSize);

        this.endingRecord = this.pageSize;
        this.error = undefined;
        //send 1 out of Totalrecord record st page to dashboard component through event
        this.sendRecordCount(1);
    }
    /*
     * Generic method to handle toast messages
     * @param {*} title 
     * @param {*} message 
     * @param {*} variant 
    */
    showToast(title, message, variant) {
        const event = new ShowToastEvent({
            title: title,
            message: message,
            variant: variant,
            mode: 'dismissable'
        });
        this.dispatchEvent(event);
    }
    /*
    * method called upon clicking Save on Case Close 
    */   
    @api
    handleCaseCloseSaveChild(obj, caseComment, caseStatus) {
        let unassignedList = [];
        let rows = this.alltableRowsSelectedWrp;
        rows.forEach(draft => {
            unassignedList.push(draft.sCaseTaskId);
        });
        this.isLoading = true;

        closeCase({
            lstOfUnassignedRecords: unassignedList,
            allWorkQueueList: obj.allWorkQueueList,
            lstWorkQ: obj.lstWorkQ,
            lstView: obj.lstView,
            lstFilterBy: obj.lstFilterBy,
            lstWorkItems: obj.lstWorkItems,
            lstSecurityGrp: obj.lstSecurityGrp,
            scasecom: caseComment,
            scasestatus: caseStatus,
            mapCaseDTO1: JSON.stringify(this.mapCaseDTO),
            lstItemAge: obj.lstItemAge,
            lstTeamMembers: obj.lstTeamMembers
        })
            .then((result) => {
                if (result && result.length > 0) {
                    let unclosedCases = [];
                    for (let i = 0; i < result.length; i++) {
                        if (unassignedList.indexOf(result[i].sCaseTaskId) !== -1) {
                            let item = result[i];
                            unclosedCases.push({
                                'id': item.sCaseTaskId,
                                'number': item.sCaseTaskNumber,
                                'viewUrl': '/lightning/r/Case/' + item.sCaseTaskId + '/view'
                            });
                        }
                    }
                    this.isLoading = false;
                    if (unclosedCases.length > 0) {
                        //if failed
                        //Code for Close  button -Validation 3 check the handler on custom_dashboardV1S
                        this.showTablePagination(result);
                        this.originalCaseTaskList = result;
                        const updateErrorEvent = new CustomEvent('closecaseerror', {
                            detail: {
                                caseIds: unclosedCases
                            }
                        });
                        this.dispatchEvent(updateErrorEvent);
                    }
                    else {
                        //if sucess
                        this.showTablePagination(result)
                        this.originalCaseTaskList = result;
                        this.showToast('Success', this.label.CloseCase_SuccessMsg, 'success');
                    }
                    this.clearRows();

                }

            })
            .catch((error) => {
            })
        //clear records and make casecount =0
        let cCount = 0;
        const checkboxEvent = new CustomEvent('checkboxcheck', {
            detail: cCount
        });
        this.dispatchEvent(checkboxEvent);
    }
    /*
    * Method handles Accepted Selected logic and makes call to apex
    */
    @api
    handleAcceptSelectedChild(obj) {
        let unassignedList = [];
        this.isLoading = true;
        let rows = this.alltableRowsSelectedWrp;
        rows.forEach(draft => {
            if (draft.sOwner !== obj.currentUserName) {
                unassignedList.push(draft.sCaseTaskId);
            }
        });
        if (unassignedList.length > 0 && unassignedList !== null) {
            //call apex
            updateOwnerOfAssignedRecords({
                lstUnassignedRecords: unassignedList,
                queueData: obj.allWorkQueueList,
                queueValue: obj.lstWorkQ,
                allQueueViewList: this.allPresentViewsToWorkQ,
                viewVlue: obj.lstView,
                filterValue: obj.lstFilterBy,
                assignWorkValue: obj.lstWorkItems,
                secFilter: obj.lstSecurityGrp,
                lstItemAgeValue: obj.lstItemAge
            })
                .then((result) => {
                    if (result != null) {
                        this.showTablePagination(result);
                        this.originalCaseTaskList = result;
                        this.clearRows();

                    } else {
                        if (this.currentProfile == this.label.ccprofileLabel || this.sPharmacyUser == this.label.sPharmacyUserSupervisorYesLabel)
                            this.showToast('Error', this.label.NoWork_CCSupervisor_CaseTaskView_HUM, 'Error');
                        else
                            this.showToast('Error', this.label.NoWork_CaseTaskView_HUM, 'Error');
                    }
                    this.isLoading = false;
                })
                .catch((error) => {
                })
        } else {
            //show error
            this.clearRows();
            this.showToast('Error', this.label.CheckBoxInput_CaseTaskView_HUM, 'Error');
            this.isLoading = false;
        }
        //clear records and make casecount =0

        let cCount = 0;
        const checkboxEvent = new CustomEvent('checkboxcheck', {
            detail: cCount
        });
        this.dispatchEvent(checkboxEvent);
    }
    /*
    * Method sens record count through event to Parent
    */
    sendRecordCount(count) {
        this.count = count;
        const pageEvent = new CustomEvent('pagecountnumber', {
            detail: {
                pageCount: count,
                totalRecountCount: this.totalRecountCount
            }
        });
        this.dispatchEvent(pageEvent);
    }

    previousHandler() {
        if (this.page > 1) {
            this.page = this.page - 1; //decrease page by 1
            this.displayRecordPerPage(this.page);
            this.sendRecordCount(this.count - 10)
        }
        this.attachrecord()
    }
    /*
    * clicking on next button this method will be called
    */
    nextHandler() {
        if ((this.page < this.totalPage) && this.page !== this.totalPage) {
            this.page = this.page + 1; //increase page by 1
            this.displayRecordPerPage(this.page);
            this.sendRecordCount((this.page - 1) * 10 + 1)
        }
        this.attachrecord()
    }
    /*
    *this method displays records page by page
    */
    displayRecordPerPage(page) {
        this.startingRecord = ((page - 1) * this.pageSize);
        this.endingRecord = (this.pageSize * page);
        this.endingRecord = (this.endingRecord > this.totalRecountCount) ?
            this.totalRecountCount : this.endingRecord;
        this.caseTaskList = this.items.slice(this.startingRecord, this.endingRecord);
        this.startingRecord = this.startingRecord + 1;
    }
    @api
    addDeleteColumns(columnsLst) {
        const addfields = [];
        this.columns = [];
        addfields.push(...this.columns1);
        if (columnsLst.length > 0)
            addfields.push(...columnsLst);
        this.columns = addfields;
    }
    handleSortAccountData(event) {
        this.sortBy = event.detail.fieldName;
        this.sortDirection = event.detail.sortDirection;
        
	const cloneData = [...this.caseTaskList ];
        cloneData.sort( this.sortByData( this.sortBy, this.sortDirection === 'asc' ? 1 : -1 ) );
        this.caseTaskList = cloneData;
    }
    
    sortByData( field, reverse, primer ) {

        const key = primer
            ? function( x ) {
                  return primer(x[field]);
              }
            : function( x ) {
                  return x[field];
              };

        return function( a, b ) {
            a = key(a);
            b = key(b);
            return reverse * ( ( a > b ) - ( b > a ) );
        };

    }


    @api
    SelectedRows() {
        return this.alltableRowsSelectedWrp;
    }
    /*
    * Method used to clear selected rows
    */
    @api
    clearRows() {
        this.alltableRowsSelectedWrp = []
        this.selection = [];
        this.template.querySelector('lightning-datatable').selectedRows = [];
    }
    @api
    onSearchKeyUp(searchKeyWord) {
        let columns = [];
        columns = [...this.columnSet];
        if (this.columns && Array.isArray(this.columns) && this.columns.length > 0)
            this.columns.forEach(column => columns.push(column['fieldName']));
        if (searchKeyWord && searchKeyWord != '') {
            let filteredArray = this.originalCaseTaskList.filter(row => columns.some(column => row[column] && String(row[column])?.toLowerCase()?.includes(searchKeyWord.toLowerCase())));
            this.showTablePagination(filteredArray);
            this.attachrecord();
        } else {
            this.showTablePagination(this.originalCaseTaskList);
            this.attachrecord();
        }
    }
}