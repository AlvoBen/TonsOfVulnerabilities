/*******************************************************************************************************************************
LWC JS Name : custom_dashboard_table_CsrHum.js
Function    : This JS serves as controller to custom_dashboard_table_CsrHum.html. 

Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Shailesh Bagade                                         12/29/2021                  initial version(Feature 2963843 - Cases/Tasks Tab)
* Gowthami T                                              07/03/2022                  Original Version
*********************************************************************************************************************************/
import { LightningElement, track, api, wire } from 'lwc';
import {
    subscribe,
    unsubscribe,
    MessageContext
} from "lightning/messageService";
import CASE_CHANNEL from "@salesforce/messageChannel/CaseTaskDataCSR__c";
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import { loadStyle } from "lightning/platformResourceLoader";
import WrappedHeaderTable from "@salesforce/resourceUrl/WrappedHeaderTable";
export default class Custom_dashboard_table_CsrHum extends LightningElement {

    @api columnSet;
    @api columns1;
    @track mapCaseDTO = {};
    @api columns
    subscription = null;
    @track sortBy;
    @track sortDirection = 'asc';
    @track caseTaskList;
    //pagination
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
    currentProfile;
    sPharmacyUser;
    originalCaseTaskList = [];
    selection = [];
    alltableRowsSelectedWrp = [];
    connectedCallback() {

        this.handleSubscribe();
    }
    /*
     * Applies wrap text to table headers
     * and CSS from utility WrappedHeaderTable file
    */
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
            this.showTablePagination(message.tableData);
            if (message.currentProfile) {
                this.currentProfile = message.currentProfile;
            }
            if (message.sPharmacyUser)
                this.sPharmacyUser = message.sPharmacyUser
        });
    }
    
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
     * Handles Pagination on table
    */
    @api
    showTablePagination(tableData) {
        this.caseTaskList = tableData.map(item => ({
            ...item,
            sCaseTaskUrl: item.sFilterType === 'Case' ? '/lightning/r/Case/' + item['sCaseTaskId'] + '/view' : '/lightning/r/Task/' + item['sCaseTaskId'] + '/view',
            sCaseComment: item['sCaseTaskComment'],//'Last comment created on 22/02/2020 09:30 AM',
            sInteractingAboutIdUrl: item.sInteractingAbout !== undefined ? '/lightning/r/Account/' + item.sAccount + '/view' : '',
            accountColor: "slds-text-color_success",
            sInteractingWithIdUrl: item.sInteractingWithId !== undefined ? '/lightning/r/Account/' + item.sInteractingWithId + '/view' : '',
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
     * Method to send page count and total record count to parent
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
    /*
     * this method will be called by clicking on previous button
    */
    previousHandler() {
        if (this.page > 1) {
            this.page = this.page - 1; //decrease page by 1
            this.displayRecordPerPage(this.page);
            this.sendRecordCount(this.count - 10)
        }
        this.attachrecord()
    }
    /*
     * this method will be called by clicking on next button
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
     * this method displays records page by page
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
    /*
     * this method provides the selected rows/records
    */
    @api
    SelectedRows() { 
        return this.alltableRowsSelectedWrp;
    }
    /* 
     * this method clears the selected rows
    */
    @api
    clearRows() {
        this.alltableRowsSelectedWrp = []
        this.selection = [];
        this.template.querySelector('lightning-datatable').selectedRows = [];
    }
    /*
     * this method handles search on case/task tab
    */
    @api
    onSearchKeyUp(searchKeyWord) {
        let columns = [];
        columns = [...this.columnSet];
        if (this.columns && Array.isArray(this.columns) && this.columns.length > 0)
            this.columns.forEach(column => columns.push(column['fieldName']));
        if (searchKeyWord && searchKeyWord != '') {
            let filteredArray = this.originalCaseTaskList.filter(row => columns.some(column => row[column] && String(row[column])?.toLowerCase()?.includes(searchKeyWord.toLowerCase())));
            this.showTablePagination(filteredArray);
        } else {
            this.showTablePagination(this.originalCaseTaskList);
        }
    }
}