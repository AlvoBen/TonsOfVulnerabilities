/*--
File Name        : loggingExistingCaseModalHum.js
Version          : 1.0 
Created Date     : 01/04/2022   
Function         : Lightning Web Component used to display existing cases and logging 
Modification Log :
* Developer                          Date                  Description
*************************************************************************************************
* Kiran Bhuvanagiri       	         01/04/2022            User Story 2759501: Original Version
* Kiran Bhuvanagiri					 05/23/2022			   User Story 2882456: Case Documentation for Demographic Updates (CRMS) 
**************************************************************************************************/

import { LightningElement, api, track, wire } from 'lwc';
import getCaseLst from '@salesforce/apex/LoggingCaseDataTable_C_HUM.queryCases';
import MoreThan200CaseFound from '@salesforce/label/c.MoreThan200CaseFound';
import NoCaseFound from '@salesforce/label/c.NoCaseFound';

const columns = [
    { label: 'Case Number', fieldName: 'CaseNumber', sortable: true},
    { label: 'Classification Type', fieldName: 'ClassificationType', sortable: true },
    { label: 'Classification', fieldName: 'Classification', sortable: true},
    { label: 'Intent', fieldName: 'Intent', sortable: true},
    { label: 'Status', fieldName: 'Status', sortable: true},
    { label: 'Created Date', fieldName: 'CreatedDate', type: 'date', sortable: true },
    { label: 'Created By', fieldName: 'CreatedBy', sortable: true},
    { label: 'Created By Queue', fieldName: 'CreatedByQueue', sortable: true},
];

export default class LoggingExistingCaseModalHum extends LightningElement {
    //Query input parameters
    @api recordId; //The record Id of the object to be queried.
    @api parentSource; //A string to describe the parent component. 
    @api dateRangeSize = 90; //An integer to reprsent the amount of days between the start and end date. This is 90 by default.

    //Table controls
    @api logBtnText = 'Log'; //A string to configure the log button's text. Default should be 'Log'.
    @track logBtnDisabled = true; //A boolean to diable the log button. This is disabled by default. 
    @api initialPageSize = 10; //An integer to reprsent the size of each page. Default is 10.
    @api runningInClassic = false; //A variable to indicate if the table is being display in Lightning (false) or Classic (true). Default is false.
    @track isModalOpen = false;
    @track isLoading = false;
    @track totalNumberOfCases;

    //Table data and settings
    @track data = [];
    columns = columns;
    defaultSortDirection = 'asc';
    sortDirection = 'asc';
    sortedBy;

    totalRecords;
    pageNo;
    totalPages;
    recordsperpage = 10;
    @track recordsToDisplay;
    pagelinks = [];
    caseNumberInput;
    startDateInput;
    endDateInput;
    pageList = [];
    @track showEntriesValue = '10';

    todayDate;
    @track selectedCaseId;
    @track selectedCaseNumber;
    @track disableSave = true;

    sMessageContent;
    hideNumberButton = false;

    get bShowWarningMessage(){
        if(this.totalNumberOfCases > 200){
            this.hideNumberButton = false;
            this.sMessageContent =  MoreThan200CaseFound;
            return true;
        } else if( this.totalNumberOfCases == 0){
            this.hideNumberButton = true;
            this.sMessageContent =  NoCaseFound;
            return true;
        } else{
            this.hideNumberButton = false;
            return false;
        }
    }

    

    get isNoCase(){
        return this.data.length == 0;
    }

    connectedCallback() {
        var todayD = new Date();
        this.endDateInput =todayD.toISOString();
        let day = new Date(todayD);
        day.setDate(todayD.getDate() - 89);
        this.startDateInput = day.toISOString();
        this.hideNumberButton = false;
        this.openModal();
    }

     get entriesOptions(){
         return [
        {
            label:'10',
            value : '10'
        },
        {
            label:'20',
            value : '20'
        },
        {
            label:'30',
            value : '30'
        },
        {
            label:'50',
            value : '50'
        },
        {
            label:'100',
            value : '100'
        }
    ];
     }  

    openModal() {
        // to open modal set isModalOpen tarck value as true
        // Added Parameter to handle search criteria
        // Initially the params is undefined and all the cases will be fetched
        
        //this.startDateInput = this.endDateInput.getDate()-90;
        this.isLoading = true;
        this.pagelinks = [];
        getCaseLst({
            policyid : this.recordId,
            caseNumber : this.caseNumberInput,
            startDate : this.startDateInput,
            endDate : this.endDateInput
        })
        .then(result => {
            // set @track contacts variable with return contact list from server  
            if(result){
                this.isModalOpen = true;
                let preparedData = this.prepareData(result.caseList);
                this.totalNumberOfCases = result.totalCases;
                this.hideNumberButton = this.totalNumberOfCases == 0 ? true : false;
                this.data = preparedData;
                this.totalRecords = this.data.length;
                this.pageNo = 1;
                this.totalPages = Math.ceil(this.totalRecords / this.recordsperpage);
                this.isLoading = false;
                for (let i = 1; i <= this.totalPages; i++) {
                    this.pagelinks.push(i);
                }
                this.preparePaginationList();
            }
            this.generatePageList();
        })
        .catch(error => {
            console.log(error);
            this.isLoading = false;
        });
    }
    
    // this method is preparing the data with respect to the lightning data table colums
    prepareData(result){
        let tempData = [];
        result.forEach(element => {
            console.log(element);
            let dataLine = {};
            dataLine.Id = element.Id;
            dataLine.CaseNumber = element.CaseNumber;
            dataLine.ClassificationType = element.Classification_Type__c;
            dataLine.Classification = element.hasOwnProperty('Classification_Id__r') ? element.Classification_Id__r.Name : '';
            dataLine.Intent = element.hasOwnProperty('Intent_Id__r') ? element.Intent_Id__r.Name : '';
            dataLine.Status = element.Status;
            dataLine.CreatedBy = element.hasOwnProperty('CreatedBy') ? element.CreatedBy.Name : '';
            dataLine.CreatedDate = element.CreatedDate;
            dataLine.CreatedByQueue = element.hasOwnProperty('Created_By_Queue__c') ? element.Created_By_Queue__c : '';
            tempData.push(dataLine);
        });
        return tempData;
    }

    sortBy(field, reverse, primer) {
        const key = primer
            ? function (x) {
                return primer(x[field]);
            }
            : function (x) {
                return x[field];
            };

        return function (a, b) {
            a = key(a);
            b = key(b);
            return reverse * ((a > b) - (b > a));
        };
    }

    onHandleSort(event) {
        const { fieldName: sortedBy, sortDirection } = event.detail;
        const cloneData = [...this.recordsToDisplay];

        cloneData.sort(this.sortBy(sortedBy, sortDirection === 'asc' ? 1 : -1));
        this.recordsToDisplay = cloneData;
        this.sortDirection = sortDirection;
        this.sortedBy = sortedBy;
    }

    closeModal() {
        // to close modal set isModalOpen tarck value as false
        this.isModalOpen = false;
        //Fire event to indicate cancel was clicked.
        this.dispatchEvent(new CustomEvent ('closemodal'));
    }

    handleRowSelection(event){
        var selectedRows=event.detail.selectedRows;
        console.log(JSON.stringify(selectedRows));
        this.selectedCaseId = selectedRows[0].Id;
        this.selectedCaseNumber = selectedRows[0].CaseNumber;
        this.disableSave = false;
        console.log('Selected case Details' + this.selectedCaseNumber + ' ' + this.selectedCaseId);
        if(selectedRows.length>1)
        {
            var el = this.template.querySelector('lightning-datatable');
            selectedRows=el.selectedRows=el.selectedRows.slice(1);
            event.preventDefault();
            return;
        }
    }

    submitDetails(event) {
        // to close modal set isModalOpen tarck value as false
        //Add your code to call apex method or do some processing
        //this.isModalOpen = false;
        event.preventDefault();
        if(this.selectedCaseId == undefined){
            return ;
        }
        this.dispatchEvent(new CustomEvent('savelog',{
            detail:{
                caseid : this.selectedCaseId,
                casenumber : this.selectedCaseNumber
            }
        }));


    }

    // this method is updating the records to display from the whole list of data acc to page number
    preparePaginationList() {
        let begin = (this.pageNo - 1) * parseInt(this.recordsperpage);
        let end = parseInt(begin) + parseInt(this.recordsperpage);
        this.recordsToDisplay = [];
        this.recordsToDisplay = this.data.slice(begin, end);
        this.disableEnableActions();
    }

    handleSetEntriesChange(event){
        this.showEntriesValue = event.detail.value;
        this.recordsperpage = parseInt(this.showEntriesValue);
        this.pageNo = 1;
        this.totalPages = Math.ceil(this.totalRecords / this.recordsperpage);
        let begin = (this.pageNo - 1) * parseInt(this.recordsperpage);
        let end = parseInt(begin) + parseInt(this.recordsperpage);
        this.recordsToDisplay = this.data.slice(begin, end);
        this.generatePageList();
        this.preparePaginationList();
    }

    // this method is handling the click events of button
    handleClick(event) {
        let label = event.target.label;
        if (label === "First") {
            this.handleFirst();
        } else if (label === "Previous") {
            this.handlePrevious();
        } else if (label === "Next") {
            this.handleNext();
        } else if (label === "Last") {
            this.handleLast();
        }
    }

    // this method is increasing the page number by 1 and also updting the records to display
    handleNext() {
        this.pageNo += 1;
        this.preparePaginationList();
    }

    // this method is decreasing the page number by 1 and also updting the records to display
    handlePrevious() {
        this.pageNo -= 1;
        this.preparePaginationList();
    }

    // this method is updating the page number to 1 and also updating the records to display
    handleFirst() {
        this.pageNo = 1;
        this.preparePaginationList();
    }

    //this method is updating the page number with Total Pages and also updating the records to display in table
    handleLast() {
        this.pageNo = this.totalPages;
        this.preparePaginationList();
    }

    handlePage(button) {
        this.pageNo = button.target.label;
        this.preparePaginationList();
    }


    // this method is enabling and disabling the First, last, Prev and Next button according to page numbers
    disableEnableActions() {
        let buttons = this.template.querySelectorAll("lightning-button");

        buttons.forEach(bun => {
            if (bun.label === this.pageNo) {
                bun.disabled = true;
            } else {
                bun.disabled = false;
            }

           
            if (bun.label === "Previous") {
                 bun.disabled = this.pageNo === 1 ? true : false;
             } else if (bun.label === "Next") {
                 bun.disabled = this.pageNo === this.totalPages ? true : false;
             }
           
        });
    }


    // this method is handling the input changes events and also updating the values to variables
    handleInputChange(event){
        let inputName = event.target.name;
        if(inputName === "caseNumberField"){
            this.caseNumberInput = event.detail.value;
        }
        if(inputName === "startDate"){
            this.startDateInput = event.detail.value;
        }
        if(inputName === "endDate"){
            this.endDateInput = event.detail.value;
        }
        console.log(this.caseNumberInput + ' '+ this.startDateInput + ' ' + this.endDateInput);
    }

    // when Search button is clicked
    // this updates the whole list according to search criteria
    handleSearch(){
        this.openModal();
    }


    // this method is clearing the search fields and also updating the list of data with default list of records
    handleClearFilters(){
        this.endDateInput = undefined;
        this.startDateInput = undefined;
        this.caseNumberInput = undefined;
        this.openModal();
    }

    processMe(event){
        let btnLabel = event.target.label;
        
        if(btnLabel == 'Previous'){
            
            this.pageNo = this.pageNo - 1;
            let begin = (this.pageNo - 1) * parseInt(this.recordsperpage);
            let end = parseInt(begin) + parseInt(this.recordsperpage);
            this.recordsToDisplay = this.data.slice(begin, end);
            this.generatePageList();
            
        } else if(btnLabel == 'Next'){
            this.pageNo = this.pageNo + 1;
            let begin = (this.pageNo - 1) * parseInt(this.recordsperpage);
            let end = parseInt(begin) + parseInt(this.recordsperpage);
            this.recordsToDisplay = this.data.slice(begin, end);
            this.generatePageList();
        
        } else{
            this.pageNo = parseInt(btnLabel);
            let begin = (this.pageNo - 1) * parseInt(this.recordsperpage);
            let end = parseInt(begin) + parseInt(this.recordsperpage);
            this.recordsToDisplay = this.data.slice(begin, end);
            console.log(typeof this.pageNo);
            if(this.pageNo >= 1 || this.pageNo <= this.totalPages){
                this.generatePageList();
            }
        }
       
    }

    get prevBtnDisabled(){
        return this.pageNo == 1 ? true : false;
    }

    get nextBtnDisabled(){
        return (this.pageNo == this.totalPages  || this.totalPages == 0 )? true : false;
    }

    generatePageList(){
       this.pageNo = parseInt(this.pageNo);   
       var pageListTemp = [];
       
        if(this.totalPages >= 1){
            if(this.totalPages <= 10){
                var counter = 2;
                for(counter; counter < (this.totalPages); counter++){
                    pageListTemp.push(counter);
                } 
            }else{
                if(this.pageNo < 5){
                    pageListTemp.push(2, 3, 4, 5, 6);
                } else{
                    if(this.pageNo > (this.totalPages-5)){
                        pageListTemp.push(this.totalPages-5, this.totalPages-4, this.totalPages-3, this.totalPages-2, this.totalPages-1);
                    } else{
                        pageListTemp.push(this.pageNo-3, this.pageNo-2, this.pageNo-1, this.pageNo, this.pageNo+1);
                    }
                }
            }
            this.pageList = pageListTemp;
        }        
    }
}