/******************************************************************************************************************
LWC Name           : PharmacyRXClaimsSummaryLWC.js
Version            : 1.0
Function           : This js componennt contains logic to fetch and displayPharmacy Rx Claims Summary information on UI.
Created On         : July 14 2020
Test Class         : PharmacyRXClaimsSummary_T_HUM
*******************************************************************************************************************
Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Shailesh Mali                                      07/14/2020                 Original Version - REQ - 891651 -- PR00094920- MF 1- Rx Claims- Pharmacy (Rx) Claims Summary page (RxXP) 
* Shailesh Mali										 07/21/2020				    REQ - 891651 -- Defect Fixed.
* Shailesh Mali										 07/22/2020				    REQ - 891651 -- Defect Fixed.
* Shailesh Mali										 07/24/2020				    REQ - 891651 -- DF-1197 Defect Fixed.
* Ranadheer Goud								     07/31/2020					REQ - 1285346 -- PR00094920- MF 1 - SF - TECH - Claims Summary Page - Alternate Accordion Feature (RxXP) (ID# 29) 
*******************************************************************************************************************/

import {
    LightningElement,
    track,
    api,
    wire
} from 'lwc';

import getDataFromService from '@salesforce/apexContinuation/PharmacyRXClaimsSummary_C_HUM.GetClaims';
import getAccordionSwitchValue from '@salesforce/apex/PharmacyRXClaimsSummary_C_HUM.accordionSwitchValue';

const columns = [{
        label: 'Time',
        fieldName: 'FillTime',
        type: 'text',
        sortable: true,
        hideDefaultActions: true
    },
    {
        label: 'Fill Date',
        fieldName: 'FillDate',
        type: 'date',
        typeAttributes: {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric'
        },
        sortable: true,
        hideDefaultActions: true
    },
    {
        label: 'Claim ID',
        fieldName: 'ClaimID',
        type: 'button',
        sortable: true,
        typeAttributes: {
            label: {
                fieldName: 'ClaimID'
            },
            variant: 'base',
            title: {
                fieldName: 'ClaimID'
            },
            name: {
                fieldName: 'ClaimID'
            }
        },
        hideDefaultActions: true

    },
    {
        label: 'Rx Number',
        fieldName: 'RxNumber',
        type: 'text',
        sortable: true,
        hideDefaultActions: true
    },
    {
        label: 'Drug Name',
        fieldName: 'DrugName',
        type: 'text',
        sortable: true,
        initialWidth:200                    
    },
    {
        label: 'Quantity',
        fieldName: 'Quantity',
        type: 'text',
        sortable: true,
        hideDefaultActions: true,
        initialWidth:100
    },
    {
        label: 'Days Supply',
        fieldName: 'DaysSupply',
        type: 'text',
        sortable: true,
        hideDefaultActions: true,
        initialWidth:120
    },
    {
        label: 'Claim Status',
        fieldName: 'ClaimStatus',
        type: 'text',
        sortable: true,
        hideDefaultActions: true,
        initialWidth:115
    },
    {
        label: 'Client ID',
        fieldName: 'ClientID',
        type: 'text',
        sortable: true,
        hideDefaultActions: true,
        initialWidth:100
    },
    {
        label: 'Pharmacy Name',
        fieldName: 'PharmacyName',
        type: 'text',
        sortable: true
    }

];
export default class PharmacyRXClaimSummaryLWC extends LightningElement {
    columns = columns;
    @track pharmacyRXData;
    showFilters = true;
    @track showAdditionFiltersValues = false;
    @track hideFilterButtonAndLink = true;
    @track showHideFilters = true;
    showCompare = false;
    @track toCompareRows = [];
    @track allClaimIds = [];
    displayErrorMessage = false;
    displayNoDataMessage = false;
    disabledClearFilter = false;
    error;
    accordionSwitch = false;
   

    //Calling Apex Method by passing memberGenKey,PolicyStartDate and PolicyEndtDate
    @api memberGenKey;
    @api policyStartDate;
    @api policyEndDate;

    @wire(getAccordionSwitchValue) accordionSwitchMethod({
        error,data
    }){
        if(data !=null && data !=undefined){
            this.accordionSwitch = data;
        }
    }

    connectedCallback() { 
        getDataFromService({
                memberGenKey: this.memberGenKey,
                policyStartDate: this.policyStartDate,
                policyEndDate: this.policyEndDate
            })
            .then(data => {               
                this.disabledClearFilter = true;
                 console.log('data@@'+JSON.stringify(data));
                if (data != null && data != undefined && data !='No Data' && data !='Integration Error' && data.length > 0) {
                    this.displayErrorMessage = false;
                    this.displayNoDataMessage = false;
                    if (data[0] != undefined && data[0] != null && data[0] != '') {
                        this.customerValue = data[0].CustomerValue;
                        this.clientValue = data[0].ClientValue;
                    }
                    this.sortedPharmacydata = this.sortDataOnLoad(data);
                    this.pharmacyRXData = this.sortedPharmacydata;
                    this.copyPharmacyRXData = this.pharmacyRXData;
                    this.showPagination(this.recordsPerPage, this.copyPharmacyRXData.length);
                } else if (data != null && data != undefined ) {
                    if(data =='No Data'){
                        this.displayNoDataMessage = true;
                    }else if(data =='Integration Error'){
                        this.displayErrorMessage = true;
                    }
                    this.pharmacyRXData = [];
                    this.startingRecord1 = 0;
                    this.setColumnWidthWhenNoData();
                    this.accordionSwitch = false;
                } else if ((error != null && error != undefined) && (data == null || data == undefined)) {
                    this.displayErrorMessage = true;
                    this.pharmacyRXData = [];
                     this.startingRecord1 = 0;
                     this.setColumnWidthWhenNoData();
                     this.accordionSwitch = false;
                }
            })
            .catch(error => {
                if (error != null && error != undefined) {
                    this.error = error;
                    this.pharmacyRXData = [];
                    this.displayErrorMessage = true;
                    this.startingRecord1 = 0;
                    this.setColumnWidthWhenNoData();
                    this.accordionSwitch = false;
                }
            });
    }

    setColumnWidthWhenNoData(){
        var columnsNoData = [{
            label: 'Time',
            fieldName: 'FillTime',
            type: 'text',
            sortable: true,
            hideDefaultActions: true
        },
        {
            label: 'Fill Date',
            fieldName: 'FillDate',
            type: 'date',
            typeAttributes: {
                day: '2-digit',
                month: '2-digit',
                year: 'numeric'
            },
            sortable: true,
            hideDefaultActions: true
        },
        {
            label: 'Claim ID',
            fieldName: 'ClaimID',
            type: 'button',
            sortable: true,
            typeAttributes: {
                label: {
                    fieldName: 'ClaimID'
                },
                variant: 'base',
                title: {
                    fieldName: 'ClaimID'
                },
                name: {
                    fieldName: 'ClaimID'
                }
            },
            hideDefaultActions: true   
        },
        {
            label: 'Rx Number',
            fieldName: 'RxNumber',
            type: 'text',
            sortable: true,
            hideDefaultActions: true
        },
        {
            label: 'Drug Name',
            fieldName: 'DrugName',
            type: 'text',
            sortable: true                 
        },
        {
            label: 'Quantity',
            fieldName: 'Quantity',
            type: 'text',
            sortable: true,
            hideDefaultActions: true
        },
        {
            label: 'Days Supply',
            fieldName: 'DaysSupply',
            type: 'text',
            sortable: true,
            hideDefaultActions: true
        },
        {
            label: 'Claim Status',
            fieldName: 'ClaimStatus',
            type: 'text',
            sortable: true,
            hideDefaultActions: true
        },
        {
            label: 'Client ID',
            fieldName: 'ClientID',
            type: 'text',
            sortable: true,
            hideDefaultActions: true
        },
        {
            label: 'Pharmacy Name',
            fieldName: 'PharmacyName',
            type: 'text',
            sortable: true
        }
    
    ];

        this.columns = columnsNoData;
    }
    
    hideError(){
        this.displayErrorMessage = false;
    }

    hideNoDataMsg() {
        this.displayNoDataMessage = false;
    }
    //Claim Status
    get claimStatusComboBoxOption() {
        return [{
                label: 'PAID',
                value: 'PAID'
            },
            {
                label: 'DENIED',
                value: 'DEN'
            },
            {
                label: 'REVERSED',
                value: 'REV'
            },
            {
                label: 'PENDING',
                value: 'PEND'
            },
            {
                label: 'OTHER',
                value: 'OTH'
            }
        ];
    }

    navigateToDetail(e) {
        const ClaimID = e.detail.row.ClaimID;
        const AuthorizationNumber = e.detail.row.AuthorizationNumber;
        const event = new CustomEvent('rowclick', {
            detail: {
                ClaimID:this.memberGenKey,
                AuthorizationNumber:AuthorizationNumber
            }
        });
        if (e.detail.action.name === 'view') {
            this.handleRowAction(e);
        } else {
            this.dispatchEvent(event);
        }
    }

    // Row Action event to show the details of the record
    @track bShowModal = false;
    handleRowAction(event) {
        const row = event.detail.row;
        this.record = row;

    }

    // to close modal window set 'bShowModal' tarck value as false
    closeModal() {
        this.showCompare = false;
    }

    //OnLoad - Sort Data Functionality -- START -- //
    //FillDate Descending, then PharmacyName asc, then DrugName asc
    sortDataOnLoad(tempArray) {
        let parseData = JSON.parse(JSON.stringify(tempArray));
        parseData.sort(function (a, b) {
            let o1 = a["FillDate"];
            let o2 = b["FillDate"];

            let p1 = a["PharmacyName"];
            let p2 = b["PharmacyName"];

            let q1 = a["DrugName"];
            let q2 = b["DrugName"];

            if (new Date(o1) < new Date(o2)) return 1;
            if (new Date(o1) > new Date(o2)) return -1;
            if (p1 < p2) return -1;
            if (p1 > p2) return 1;
            if (q1 < q2) return -1;
            if (q1 > q2) return 1;
            return 0;
        });
        return parseData;
    }
    //OnLoad - Sort Data Functionality -- END -- //


    //Sorting Functionality  -- START -- //
    updateColumnSorting(event) {
        var fieldName = event.detail.fieldName;
        var sortDirection = event.detail.sortDirection;
        this.sortedBy = fieldName;
        this.sortedDirection = sortDirection;
        this.sortData(fieldName, sortDirection);
        this.resetSelectedRows(this.template.querySelector('.PharmacyClaimsDataTable').selectedRows);
    }

    sortData(fieldname, direction) {
        let parseData = JSON.parse(JSON.stringify(this.pharmacyRXData));
        // Return the value stored in the field
        let keyValue = (a) => {
            return a[fieldname];
        };
        // cheking reverse direction
        let isReverse = direction === 'asc' ? 1 : -1;
        // sorting data
        parseData.sort((x, y) => {
            x = keyValue(x) ? keyValue(x) : ''; // handling null values
            y = keyValue(y) ? keyValue(y) : '';
            // sorting values based on direction   
            if (fieldname === 'FillDate') {
                return (isReverse * ((new Date(x) > new Date(y)) - (new Date(y) > new Date(x))));
            } else if (fieldname === 'ClientID' || fieldname === 'ClaimID' || fieldname === 'RxNumber' || fieldname === 'DaysSupply') {
                return (isReverse * ((parseInt(x, 10) > parseInt(y, 10)) - (parseInt(y, 10) > parseInt(x, 10))));
            } else if(fieldname === 'Quantity'){
                return (isReverse * ((parseFloat(x, 10) > parseFloat(y, 10)) - (parseFloat(y, 10) > parseFloat(x, 10))));
            } 
            else if(fieldname === 'DrugName'){
                return (isReverse * ('' + x).localeCompare(y));
            }
            else {
                return (isReverse * ((x > y) - (y > x)));
            }
        });
        this.pharmacyRXData = parseData;
    }
    //Sorting Functionality  -- END -- //


    //Message Hide Static Message  -- START -- //
    @track hideMessage = true;
    hideStaticMessage(event) {
        if (this.hideMessage === true) {
            this.hideMessage = false;
        }
    }
    //Message Hide Static Message  -- END -- //



    //Number of Records to display in table based on the entries Functionality -- START -- //
    @track recordsPerPage;
    @track pageNumber;
    recordsPerPage = '25';

    get comboBoxOption() {
        return [{
                label: '5/page',
                value: '5'
            },
            {
                label: '10/page',
                value: '10'
            },
            {
                label: '25/page',
                value: '25'
            },
            {
                label: '50/page',
                value: '50'
            },
            {
                label: '75/page',
                value: '75'
            },
            {
                label: '100/page',
                value: '100'
            }
        ];
    }
    //handle record display based combobox value change
    handleComboBoxChange(event) {
        const comboBoxValue = event.target.value;
        var allRecords;
        if (this.filterAndPageLength === true){
            allRecords = this.filterDataArray;            
        }
        else
            allRecords = this.copyPharmacyRXData;
        var tempArray = [];
        this.pageNumber = 1;
        this.recordsPerPage = comboBoxValue;
        for (let i = 0; i < this.recordsPerPage; i++) {
            if (allRecords.length > i) {
                tempArray.push(allRecords[i]);
            }
        }
        this.pharmacyRXData = tempArray;

        //Pagination -- on load showing data           
        this.showPagination(this.recordsPerPage, allRecords.length);
    }


    //Pagination -- on PAge Load Functionality -- START -- //
    showPagination(recordsPerPageVar, tempData) {
        this.totalRecordCount = tempData;
        this.page = 1;
        this.pageSize = recordsPerPageVar;
        //this.totalPage = 1;
        this.totalPage = Math.ceil(this.totalRecordCount / recordsPerPageVar);
        this.startingRecord = (this.totalRecordCount > 0) ? 1 : 0;
        this.startingRecord1 = this.startingRecord;
        this.endingRecord = recordsPerPageVar; // based on comboBox default option

        this.endingRecord = (this.endingRecord > this.totalRecordCount) ?
            this.totalRecordCount : this.endingRecord;
        //initialize button properties
        this.isFirstButton = false;
        this.isSecondButton = false;
        this.isThirdButton = false;

        this.changeVariantPrevious = 'Neutral';
        this.changeVariantNext = 'Neutral';
        this.changeVariantFirst = 'Neutral';
        this.changeVariantSecond = 'Neutral';
        this.changeVariantThird = 'Neutral';
        // to disable the Previous or Next button on page load based on the records
        if (this.totalPage === 1) {
            this.isFirstPage = true; //previous button disabled
            this.isLastPage = true; //next button disabled
            this.changeVariantPrevious = 'Neutral';
            this.changeVariantNext = 'Neutral';
            this.isFirstButton = true; //1 button visible 
            this.changeVariantFirst = 'brand'; //'1' button showing as brand 
            this.isSecondButton = false; // '2' button is hide
            this.isThirdButton = false; // '3' button is hide            
        } else if (this.totalPage > 1) {
            this.isFirstPage = true; //previous button disabled
            this.changeVariantPrevious = 'Neutral';
            this.isLastPage = false; //next button is enabled            

            this.isFirstButton = true; //1 button visible 
            this.changeVariantFirst = 'brand'; //'1' button showing as brand 
            this.showButtonFirst = '1';

            if (this.totalPage >= 2) {
                this.isSecondButton = true;
                this.showButtonSecond = '2';
            }

            if (this.totalPage >= 3) {
                this.isThirdButton = true;
                this.showButtonThird = '3';
            }
        }
    }
    //Pagination -- on PAge Load Functionality -- END -- //
 setTextBoxValues(){
    if(this.template.querySelector('[data-id="fromFillDate"]') != null)
        this.x1 = this.template.querySelector('[data-id="fromFillDate"]').value;
    if(this.template.querySelector('[data-id="toFillDate"]') != null)
        this.x2 = this.template.querySelector('[data-id="toFillDate"]').value;
    if(this.template.querySelector('[data-id="claimId"]') != null)
        this.x3 = this.template.querySelector('[data-id="claimId"]').value;
    if(this.template.querySelector('[data-id="rxNumberId"]') != null)
        this.x4 = this.template.querySelector('[data-id="rxNumberId"]').value;
    if(this.template.querySelector('[data-id="drugNameId"]') != null)
        this.x5 = this.template.querySelector('[data-id="drugNameId"]').value;
    if(this.template.querySelector('[data-id="quantityId"]')!= null)   
        this.x6 = this.template.querySelector('[data-id="quantityId"]').value ;
    if(this.template.querySelector('[data-id="claimStatusId"]') != null)
        this.x7 = this.template.querySelector('[data-id="claimStatusId"]').value ;
    if(this.template.querySelector('[data-id="pharmacyNameId"]') != null)
        this.x8 = this.template.querySelector('[data-id="pharmacyNameId"]').value ;
    if(this.template.querySelector('[data-id="clientId"]') != null)  
        this.x9 = this.template.querySelector('[data-id="clientId"]').value ;
    if(this.template.querySelector('[data-id="daysSupplyId"]') != null)
        this.x10 = this.template.querySelector('[data-id="daysSupplyId"]').value;  
 }
    //Filter Display Functionality -- START -- //
    showFilterOptions(event) {
        if (this.showHideFilters === true) {
            this.showHideFilters = false;  
            this.setTextBoxValues();
        } else {
            this.showHideFilters = true;
            this.showAdditionFiltersValues = false;
            this.hideFilterButtonAndLink = true;
            this.showValuesFromShowTextBox = true;
        }
    }

    x1;x2;x3;x4;x5;x6;x7;x8;x9;x10;
    showValuesFromShowTextBox = false;  
    showValuesFromHideTextBox = false;    
     
    showAdditionalFilters(event) {
        if (this.showAdditionFiltersValues === false) {
            this.showAdditionFiltersValues = true;
            this.hideFilterButtonAndLink = false;  
            this.showValuesFromHideTextBox = true;
            this.x1 = this.template.querySelector('[data-id="fromFillDate"]').value;
            this.x2 = this.template.querySelector('[data-id="toFillDate"]').value;
            this.x3 = this.template.querySelector('[data-id="claimId"]').value;
            this.x4 = this.template.querySelector('[data-id="rxNumberId"]').value;
            this.x5 = this.template.querySelector('[data-id="drugNameId"]').value;
        } else {
            this.showAdditionFiltersValues = false;
            this.hideFilterButtonAndLink = true;
        }
    }
    renderedCallback(){
        if(this.showValuesFromShowTextBox === true && this.template != null){
            if(this.template.querySelector('[data-id="fromFillDate"]') != null)
                this.template.querySelector('[data-id="fromFillDate"]').value = this.x1;

            if(this.template.querySelector('[data-id="toFillDate"]') != null)
                this.template.querySelector('[data-id="toFillDate"]').value = this.x2;

            if(this.template.querySelector('[data-id="claimId"]') != null)
                this.template.querySelector('[data-id="claimId"]').value = this.x3;

            if(this.template.querySelector('[data-id="rxNumberId"]') != null)
                this.template.querySelector('[data-id="rxNumberId"]').value = this.x4;

            if(this.template.querySelector('[data-id="drugNameId"]') != null)
                this.template.querySelector('[data-id="drugNameId"]').value = this.x5;

            this.showValuesFromShowTextBox = false;
        }
        if( this.showValuesFromHideTextBox === true && this.template != null) {
            if(this.template.querySelector('[data-id="quantityId"]') != null)
                this.template.querySelector('[data-id="quantityId"]').value = this.x6;

            if(this.template.querySelector('[data-id="claimStatusId"]') != null)
                this.template.querySelector('[data-id="claimStatusId"]').value =  this.x7;

            if(this.template.querySelector('[data-id="pharmacyNameId"]') != null)
                this.template.querySelector('[data-id="pharmacyNameId"]').value = this.x8;

            if(this.template.querySelector('[data-id="clientId"]') != null)
                this.template.querySelector('[data-id="clientId"]').value =  this.x9;

            if(this.template.querySelector('[data-id="daysSupplyId"]') != null)
                this.template.querySelector('[data-id="daysSupplyId"]').value = this.x10;
        
        this.showValuesFromHideTextBox = false;
        }
    }

    hideAdditionalFilters(event) {
        if (this.showAdditionFiltersValues === true) {
            this.showAdditionFiltersValues = false;
            this.hideFilterButtonAndLink = true;
            this.showValuesFromHideTextBox = true;
            this.setTextBoxValues();
        } else {
            this.showAdditionFiltersValues = true;
            this.hideFilterButtonAndLink = false;
        }
    }
    //Filter Display Functionality -- START -- //


    //Show-Hide Columns Functionality -- START -- //
    @track value = ['FillTime', 'FillDate', 'ClaimID', 'RxNumber', 'DrugName', 'Quantity', 'DaysSupply', 'ClaimStatus', 'ClientID', 'PharmacyName'];
    get options() {
        return [{
                label: 'Time',
                value: 'FillTime'
            },
            {
                label: 'Fill Date',
                value: 'FillDate'
            },
            {
                label: 'Claim ID',
                value: 'ClaimID'
            },
            {
                label: 'Rx Number',
                value: 'RxNumber'
            },
            {
                label: 'Drug Name',
                value: 'DrugName'
            },
            {
                label: 'Quantity',
                value: 'Quantity'
            },
            {
                label: 'Days Supply',
                value: 'DaysSupply'
            },
            {
                label: 'Claim Status',
                value: 'ClaimStatus'
            },
            {
                label: 'Client ID',
                value: 'ClientID'
            },
            {
                label: 'Pharmacy Name',
                value: 'PharmacyName'
            }
        ];
    }

    handleShowHideColumnsChange(e) {
        const selectedColumnValues = ['FillTime', 'FillDate', 'ClaimID', 'RxNumber', 'DrugName', 'Quantity', 'DaysSupply', 'ClaimStatus', 'ClientID', 'PharmacyName'];
        this.value = e.detail.value;

        var TimeArrayObj = {
            label: 'Time',
            fieldName: 'FillTime',
            type: 'text',
            sortable: true,
            hideDefaultActions: true
        }

        var FillDateArrayObj = {
            label: 'Fill Date',
            fieldName: 'FillDate',
            type: 'date',
            typeAttributes: {
                day: '2-digit',
                month: '2-digit',
                year: 'numeric'
            },
            sortable: true,
            hideDefaultActions: true
        }

        var ClaimIDArrayObj = {
            label: 'Claim ID',
            fieldName: 'ClaimID',
            type: 'button',
            sortable: true,
            typeAttributes: {
                label: {
                    fieldName: 'ClaimID'
                },
                variant: 'base',
                title: {
                    fieldName: 'ClaimID'
                },
                name: {
                    fieldName: 'ClaimID'
                }
            },
            hideDefaultActions: true
        }

        var RxNumberArrayObj = {
            label: 'Rx Number',
            fieldName: 'RxNumber',
            type: 'text',
            sortable: true,
            hideDefaultActions: true
        }

        var DrugNameArrayObj = {
            label: 'Drug Name',
            fieldName: 'DrugName',
            type: 'text',
            sortable: true,
            initialWidth: 200
        }

        var QuantityArrayObj = {
            label: 'Quantity',
            fieldName: 'Quantity',
            type: 'text',
            sortable: true,
            hideDefaultActions: true,
			initialWidth: 100
        }

        var DaysSupplyArrayObj = {
            label: 'Days Supply',
            fieldName: 'DaysSupply',
            type: 'text',
            sortable: true,
            hideDefaultActions: true,
			initialWidth: 120
        }

        var ClaimStatusArrayObj = {
            label: 'Claim Status',
            fieldName: 'ClaimStatus',
            type: 'text',
            sortable: true,
            hideDefaultActions: true,
			initialWidth: 115
        }

        var ClientIDArrayObj = {
            label: 'Client ID',
            fieldName: 'ClientID',
            type: 'text',
            sortable: true,
            hideDefaultActions: true,
			initialWidth: 100
        }

        var PharmacyNameArrayObj = {
            label: 'Pharmacy Name',
            fieldName: 'PharmacyName',
            type: 'text',
            sortable: true
        }

        const finalVal = selectedColumnValues.filter(element => this.value.includes(element));
        this.gridColumns1 = [];
        if (finalVal.length === 0) {
            this.gridColumns1.push(ClaimIDArrayObj);
        } else {
            for (let i = 0; i < finalVal.length; i++) {
                if (finalVal[i] === 'FillTime') {
                    this.gridColumns1.push(TimeArrayObj);
                } else if (finalVal[i] === 'FillDate') {
                    this.gridColumns1.push(FillDateArrayObj);
                } else if (finalVal[i] === 'ClaimID') {
                    this.gridColumns1.push(ClaimIDArrayObj);
                } else if (finalVal[i] === 'RxNumber') {
                    this.gridColumns1.push(RxNumberArrayObj);
                } else if (finalVal[i] === 'DrugName') {
                    this.gridColumns1.push(DrugNameArrayObj);
                } else if (finalVal[i] === 'Quantity') {
                    this.gridColumns1.push(QuantityArrayObj);
                } else if (finalVal[i] === 'ClaimStatus') {
                    this.gridColumns1.push(ClaimStatusArrayObj);
                } else if (finalVal[i] === 'PharmacyName') {
                    this.gridColumns1.push(PharmacyNameArrayObj);
                } else if (finalVal[i] === 'ClientID') {
                    this.gridColumns1.push(ClientIDArrayObj);
                } else if (finalVal[i] === 'DaysSupply') {
                    this.gridColumns1.push(DaysSupplyArrayObj);
                } else {
                    this.gridColumns1 = [];
                }
            }
        }
        this.columns = this.gridColumns1;
        this.resetSelectedRows();
    }
    //Show-Hide Column Functionality -- END -- //





    //Filter Data Functionality -- START -- //
    validationcheck = false;
    fromToDateValidation(event) {
        var fromDateFieldValue;
        var toDateFieldValid;
        if (event.target.name === 'fillDateInput1' || event.target.name === 'fillDateInput2') {
            fromDateFieldValue = this.template.querySelector('[data-id="fromFillDate"]').value;
            toDateFieldValid = this.template.querySelector('[data-id="toFillDate"]').value;
        }
        if ((fromDateFieldValue != '' && fromDateFieldValue != undefined) && (toDateFieldValid != '' && toDateFieldValid != undefined)) {
            if (toDateFieldValid < fromDateFieldValue) {
                this.validationcheck = true;
            } else {
                this.validationcheck = false;
            }
        }
    }


    filterData(filterWithNameValueMap) {
        this.filterAndWithReverse = false;
        var allRecords = this.copyPharmacyRXData;
        var tempArray = [];
        let fromDateFilter;
        let toDateFilter;
        let fromDateVar, toDateVar, claimVar, rxNumberVar, drugNameVar, quantityVar, claimStatusVar, pharmacyNameVar, clientVar, daysSupplyVar;
        let expressionForFilter = '';

        for (let [key, value] of filterWithNameValueMap) {
            if (key === 'fillDateInput1')
                fromDateVar = filterWithNameValueMap.get(key);
            if (key === 'fillDateInput2')
                toDateVar = filterWithNameValueMap.get(key);
            if (key === 'claimIdInput')
                claimVar = filterWithNameValueMap.get(key);
            if (key === 'rxNumberIdInput')
                rxNumberVar = filterWithNameValueMap.get(key);
            if (key === 'drugNameIdInput')
                drugNameVar = filterWithNameValueMap.get(key);
            if (key === 'quantityIdInput')
                quantityVar = filterWithNameValueMap.get(key);
            if (key === 'claimStatusIdInput')
                claimStatusVar = filterWithNameValueMap.get(key);
            if (key === 'pharmacyNameIdInput')
                pharmacyNameVar = filterWithNameValueMap.get(key);
            if (key === 'clientIdInput')
                clientVar = filterWithNameValueMap.get(key);
            if (key === 'daysSupplyIdInput')
                daysSupplyVar = filterWithNameValueMap.get(key);
        }

        fromDateFilter = this.formatedDate(fromDateVar);
        toDateFilter = this.formatedDate(toDateVar);


        for (let i = 0; i < allRecords.length; i++) {
            let FillDateFilter = this.formatedDate(allRecords[i].FillDate);
            if (
                ((fromDateFilter != undefined && fromDateFilter != null) ? (fromDateFilter <= FillDateFilter) : true) &&
                ((toDateFilter != undefined && toDateFilter != null) ? (toDateFilter >= FillDateFilter) : true) &&

                ((fromDateFilter != undefined && fromDateFilter != null && toDateFilter != undefined && toDateFilter != null) ? ((fromDateFilter <= FillDateFilter) && (toDateFilter >= FillDateFilter)) : true) &&
                (claimVar != undefined ? allRecords[i].ClaimID.includes(claimVar) : true) &&
                (rxNumberVar != undefined ? allRecords[i].RxNumber.includes(rxNumberVar) : true) &&
                (drugNameVar != undefined ? allRecords[i].DrugName.includes(drugNameVar) : true) &&
                (quantityVar != undefined ? allRecords[i].Quantity.includes(quantityVar) : true) &&
                (claimStatusVar != undefined ? allRecords[i].ClaimStatus.includes(claimStatusVar) : true) &&
                (pharmacyNameVar != undefined ? allRecords[i].PharmacyName.includes(pharmacyNameVar) : true) &&
                (clientVar != undefined ? allRecords[i].ClientID.includes(clientVar) : true) &&
                (daysSupplyVar != undefined ? allRecords[i].DaysSupply.includes(daysSupplyVar) : true)
            ) {
                tempArray.push(allRecords[i]);
            }
        }

        if (tempArray != null && tempArray.length > 0) {
            this.filterAndPageLength = true;
            let tempArryaForPageLength = [];
            this.filterDataArray = tempArray;
            for (let j = 0; j < this.recordsPerPage; j++) {
                if (tempArray[j] != undefined)
                    tempArryaForPageLength.push(tempArray[j]);
            }
            if (this.filterDataArray.length === allRecords.length)
                this.filterAndPageLength = false;
            this.pharmacyRXData = tempArryaForPageLength;
            this.showPagination(this.recordsPerPage, tempArray.length);
        } else {
            this.filterAndPageLength = false;
            this.pharmacyRXData = tempArray;
            this.filterDataArray = tempArray;
            this.showPagination(this.recordsPerPage, tempArray.length);
        }
        this.filterAndWithReverse = false;
        this.firstTimeSearch = false;
        
        if(filterWithNameValueMap.size > 0)
            this.disabledClearFilter = false;
        else
            this.disabledClearFilter = true;
    }

    setValuesinFilterTextBox(inputName, val) {
        if (inputName === 'fillDateInput1')
            this.fillDateFromValue = val;
        else if (inputName === 'fillDateInput2')
            this.fillDateToValue = val;
        else if (inputName === 'claimIdInput')
            this.claimIdInputVal = val;
        else if (inputName === 'rxNumberIdInput')
            this.rxNumberIdInputVal = val;
        else if (inputName === 'drugNameIdInput')
            this.drugNameIdInputVal = val;
        else if (inputName === 'quantityIdInput')
            this.quantityIdInputVal = val;
        else if (inputName === 'claimStatusIdInput')
            this.claimStatusIdInputVal = val;
        else if (inputName === 'pharmacyNameIdInput')
            this.pharmacyNameIdInputVal = val;
        else if (inputName === 'clientIdInput')
            this.clientIdInputVal = val;
        else if (inputName === 'daysSupplyIdInput')
            this.daysSupplyIdInputVal = val;
    }

    checkFilterValues(inputVal) {
        if (inputVal != '' && inputVal != null && inputVal != undefined) {
            return true;
        } else return false;
    }

    sVal = '';
    fillDateFromValue = '';
    fillDateToValue = '';
    claimIdInputVal = '';
    rxNumberIdInputVal = '';
    drugNameIdInputVal = '';
    quantityIdInputVal = '';
    claimStatusIdInputVal = '';
    pharmacyNameIdInputVal = '';
    clientIdInputVal = '';
    daysSupplyIdInputVal = '';
    firstTimeSearch = true;
    filterWithNameValueMap = new Map();
    filterAndWithReverse = false;
    filterDataArray = [];
    filterAndPageLength = false;

    searchTable(event) {
        this.disabledClearFilter = false;
        this.filterAndPageLength = false;
        this.validationcheck = false;
        this.fromToDateValidation(event);
        if (this.validationcheck) return;

        this.filterAndWithReverse = false;
        var allRecords;
        var tempArray = [];
        let sVal1;
        this.filterWithNameValueMap.clear();
        this.sVal = event.target.value;
        var inputName = event.target.name;

        this.setValuesinFilterTextBox(inputName, event.target.value);

        if (event.keyCode === 8 || event.keyCode === 46 ||
            (event.type === 'change' && inputName === 'claimStatusIdInput') ||
            (event.type === 'change' && inputName === 'fillDateInput1') ||
            (event.type === 'change' && inputName === 'fillDateInput2')) //for back-space keyboard keyword
        {

            if (event.keyCode === 46) {
                event.target.value = '';
                this.setValuesinFilterTextBox(inputName, event.target.value);
            } else if (event.type === 'change' && inputName === 'claimStatusIdInput') {
                this.sVal = event.target.value;
                this.setValuesinFilterTextBox(inputName, event.target.value);
            } else if (event.type === 'change' && (inputName === 'fillDateInput1' || inputName === 'fillDateInput2')) {
                this.sVal = event.target.value;
                this.setValuesinFilterTextBox(inputName, event.target.value);
            } else {
                event.target.value = this.sVal.substring(0, this.sVal.length - 1);
                this.sVal = event.target.value;
                this.setValuesinFilterTextBox(inputName, event.target.value);
                if (this.sVal === undefined || this.sVal === null || this.sVal === '')
                    this.sVal = '';
            }


            if (this.sVal === '')
                this.firstTimeSearch = true;
            else
                this.firstTimeSearch = false;

            if (this.checkFilterValues(this.fillDateFromValue)) {
                this.filterWithNameValueMap.set('fillDateInput1', this.fillDateFromValue);
            }
            if (this.checkFilterValues(this.fillDateToValue)) {
                this.filterWithNameValueMap.set('fillDateInput2', this.fillDateToValue);
            }
            if (this.checkFilterValues(this.claimIdInputVal)) {
                this.filterWithNameValueMap.set('claimIdInput', this.claimIdInputVal);
            }
            if (this.checkFilterValues(this.rxNumberIdInputVal)) {
                this.filterWithNameValueMap.set('rxNumberIdInput', this.rxNumberIdInputVal);
            }
            if (this.checkFilterValues(this.drugNameIdInputVal)) {
                this.filterWithNameValueMap.set('drugNameIdInput', this.drugNameIdInputVal);
            }
            if (this.checkFilterValues(this.quantityIdInputVal)) {
                this.filterWithNameValueMap.set('quantityIdInput', this.quantityIdInputVal);
            }
            if (this.checkFilterValues(this.claimStatusIdInputVal)) {
                this.filterWithNameValueMap.set('claimStatusIdInput', this.claimStatusIdInputVal);
            }
            if (this.checkFilterValues(this.pharmacyNameIdInputVal)) {
                this.filterWithNameValueMap.set('pharmacyNameIdInput', this.pharmacyNameIdInputVal);
            }
            if (this.checkFilterValues(this.clientIdInputVal)) {
                this.filterWithNameValueMap.set('clientIdInput', this.clientIdInputVal);
            }
            if (this.checkFilterValues(this.daysSupplyIdInputVal)) {
                this.filterWithNameValueMap.set('daysSupplyIdInput', this.daysSupplyIdInputVal);
            }

            this.filterData(this.filterWithNameValueMap);
            this.filterAndWithReverse = true;
        }


        if (this.sVal != '' && this.sVal != null && this.sVal != undefined)
            sVal1 = (this.sVal).toUpperCase();

        if (this.firstTimeSearch)
            allRecords = this.copyPharmacyRXData;
        else
            allRecords = this.filterDataArray;

        let fromDateFilter;
        let toDateFilter;
        if (this.fillDateFromValue != '' && this.fillDateToValue != '') {
            fromDateFilter = this.formatedDate(this.fillDateFromValue);
            toDateFilter = this.formatedDate(this.fillDateToValue);
        }

        if ( (sVal1!= null && sVal1 != undefined && sVal1 !='') &&this.filterAndWithReverse === false && (allRecords != null && allRecords != undefined)) {
            for (let i = 0; i < allRecords.length; i++) {
                if (this.fillDateFromValue != null && this.fillDateFromValue.length === 10 && (this.fillDateToValue === '' || this.fillDateToValue === null)) {
                    let fromDate = this.formatedDate(sVal1);
                    if (fromDate != null && fromDate <= this.formatedDate(allRecords[i].FillDate)) {
                        tempArray.push(allRecords[i]);
                    }
                    this.firstTimeSearch = false;
                } else if (this.fillDateToValue != null && this.fillDateToValue.length === 10 && (this.fillDateFromValue === '' || this.fillDateFromValue === null)) {
                    let toDate = this.formatedDate(sVal1);
                    if (toDate != null && toDate >= this.formatedDate(allRecords[i].FillDate)) {
                        tempArray.push(allRecords[i]);
                    }
                    this.firstTimeSearch = false;
                } else if (this.fillDateFromValue != null && this.fillDateToValue != null && this.fillDateToValue.length === 10 && this.fillDateFromValue.length === 10 &&
                    (inputName === 'fillDateInput1' || inputName === 'fillDateInput2')) {
                    let FillDateFilter = this.formatedDate(allRecords[i].FillDate);
                    if ((fromDateFilter <= FillDateFilter) && (toDateFilter >= FillDateFilter)) {
                        tempArray.push(allRecords[i]);
                    }
                    this.firstTimeSearch = false;
                }
                if (inputName === 'claimIdInput' && (allRecords[i].ClaimID != '' && allRecords[i].ClaimID.indexOf(sVal1) !== -1)) {
                    tempArray.push(allRecords[i]);
                    this.firstTimeSearch = false;
                }
                if (inputName === 'rxNumberIdInput' && (allRecords[i].RxNumber != '' && allRecords[i].RxNumber.indexOf(sVal1) !== -1)) {
                    tempArray.push(allRecords[i]);
                    this.firstTimeSearch = false;
                }
                if (inputName === 'drugNameIdInput' && (allRecords[i].DrugName != '' && allRecords[i].DrugName.toUpperCase().indexOf(sVal1) !== -1)) {
                    tempArray.push(allRecords[i]);
                    this.firstTimeSearch = false;
                }
                if (inputName === 'quantityIdInput' && (allRecords[i].Quantity != '' && allRecords[i].Quantity.indexOf(sVal1) !== -1)) {
                    tempArray.push(allRecords[i]);
                    this.firstTimeSearch = false;
                }
                if (inputName === 'claimStatusIdInput' && (allRecords[i].ClaimStatus != '' && allRecords[i].ClaimStatus.toUpperCase().indexOf(sVal1) !== -1)) {
                    tempArray.push(allRecords[i]);
                    this.firstTimeSearch = false;
                }
                if (inputName === 'pharmacyNameIdInput' && (allRecords[i].PharmacyName != '' && allRecords[i].PharmacyName.toUpperCase().indexOf(sVal1) !== -1)) {
                    tempArray.push(allRecords[i]);
                    this.firstTimeSearch = false;
                }
                if (inputName === 'clientIdInput' && (allRecords[i].ClientID != '' && allRecords[i].ClientID.indexOf(sVal1) !== -1)) {
                    tempArray.push(allRecords[i]);
                    this.firstTimeSearch = false;
                }
                if (inputName === 'daysSupplyIdInput' && (allRecords[i].DaysSupply != '' && allRecords[i].DaysSupply.indexOf(sVal1) !== -1)) {
                    tempArray.push(allRecords[i]);
                    this.firstTimeSearch = false;
                }
            }

            if (tempArray.length > 0 && this.sVal != '') {
                let tempArryaForPageLength = [];
                this.filterDataArray = tempArray;
                this.filterAndPageLength = true;
                for (let j = 0; j < this.recordsPerPage; j++) {
                    if (tempArray[j] != undefined)
                        tempArryaForPageLength.push(tempArray[j]);
                }
                this.pharmacyRXData = tempArryaForPageLength;
                this.showPagination(this.recordsPerPage, tempArray.length);
            } else if (tempArray.length === 0 && this.sVal === '') {
                for (let i = 0; i < this.recordsPerPage; i++) {
                    if (tempArray[i] != undefined)
                        tempArray.push(this.copyPharmacyRXData[i]);
                }
                this.pharmacyRXData = tempArray;
                this.showPagination(this.recordsPerPage, this.copyPharmacyRXData.length);
            } else if (tempArray.length === 0 && this.sVal != '') {
                this.pharmacyRXData = tempArray;
                this.filterDataArray = tempArray;
                this.showPagination(this.recordsPerPage, tempArray.length);
            }
        }
    }


    formatedDate(inputDate) {
        if (inputDate != null && inputDate != undefined && inputDate != '' && inputDate.length === 10) {
            var date = new Date(inputDate);

            if (!isNaN(date.getTime())) {
                // Months use 0 index.
                let dateFormat = date.getMonth() + 1 + '/' + date.getDate() + '/' + date.getFullYear();
                return new Date(dateFormat);
            }
        }
        return null;
    }
    //Filter Data Functionality -- end -- //



    //Reset Data - clear filters -- START --//
    resetData() {
        this.showValuesFromShowTextBox = false;
        this.showValuesFromHideTextBox = false;
        this.template.querySelector('[data-id="fromFillDate"]').value = '';
        this.template.querySelector('[data-id="toFillDate"]').value = '';
        this.template.querySelector('[data-id="claimId"]').value = '';
        this.template.querySelector('[data-id="rxNumberId"]').value = '';
        this.template.querySelector('[data-id="drugNameId"]').value = '';
        this.fillDateFromValue = '';
        this.fillDateToValue = '';
        this.claimIdInputVal = '';
        this.rxNumberIdInputVal = '';
        this.drugNameIdInputVal = '';
        this.quantityIdInputVal = '';
        this.claimStatusIdInputVal = '';
        this.pharmacyNameIdInputVal = '';
        this.clientIdInputVal = '';
        this.daysSupplyIdInputVal = '';
        this.x1 = '';
        this.x2 = '';
        this.x3 = '';
        this.x4 = '';
        this.x5 = '';
        this.x6 = '';
        this.x7 = '';
        this.x8 = '';
        this.x9 = '';
        this.x10 = '';
        if (this.showAdditionFiltersValues === true) {
            this.template.querySelector('[data-id="quantityId"]').value = '';
            this.template.querySelector('[data-id="claimStatusId"]').value = '';
            this.template.querySelector('[data-id="pharmacyNameId"]').value = '';
            this.template.querySelector('[data-id="clientId"]').value = '';
            this.template.querySelector('[data-id="daysSupplyId"]').value = '';
        }
        this.fillDateFromValue = '';
        this.fillDateToValue = '';
        let tempArray = [];
        for (let i = 0; i < this.recordsPerPage; i++) {
			if(this.copyPharmacyRXData[i] != undefined)
            tempArray.push(this.copyPharmacyRXData[i]);
        }
        this.pharmacyRXData = tempArray;
        this.showPagination(this.recordsPerPage, this.copyPharmacyRXData.length);
        this.firstTimeSearch = true; // setting true value of filter condition on reseting data
        this.filterAndWithReverse = false;
        this.validationcheck = false;
        this.filterAndPageLength = false;
        this.disabledClearFilter = true;
    }
    //Reset Data - clear filters -- END --//



    //Pagination Functionality -- START -- //
    @track page = 1; //this is initialize for 1st page
    @track pages = [];
    @track startingRecord = 1; //start record position per page
    @track startingRecord1 = 1;
    @track endingRecord = 0; //end record position per page
    @track pageSize = this.recordsPerPage; //default value we are assigning
    @track totalRecordCount = 0; //total record count received from all retrieved records
    @track totalPage = 0; //total number of page is needed to display all records
    @track items = []; //it contains all the records.
    showButtonFirst = '1';
    showButtonSecond = '2';
    showButtonThird = '3';
    firstButtonClick = false;
    secondButtonClick = false;
    thirdButtonClick = false;
    next = false;
    previous = false;

    i = 1;
    isFirstButton = false;
    isSecondButton = false;
    isThirdButton = false;

    changeVariantFirst = 'Neutral';
    changeVariantSecond = 'Neutral';
    changeVariantThird = 'Neutral';

    handleNextPage() {
        this.template.querySelector('.PharmacyClaimsDataTable').selectedRows = null;
        this.toCompareRowsTable = null;
        this.showComparison = false;

        if ((this.page < this.totalPage) && this.page !== this.totalPage) {
            this.next = true;
            this.page = this.page + 1; //increase page by 1
            this.isLastPage = false;
            this.isFirstPage = false;
            this.setButtonProperties('next');
            this.displayRecordPerPage(this.page);
        }
    }

    handlePreviousPage() {
        this.template.querySelector('.PharmacyClaimsDataTable').selectedRows = null;
        this.toCompareRowsTable = null;
        this.showComparison = false;
        if (this.page > 1) {
            this.previous = true;
            this.page = this.page - 1;
            this.isLastPage = false;
            this.isFirstPage = false;

            this.setButtonProperties('previous');
            this.displayRecordPerPage(this.page);
        }
    }

    setButtonProperties(previousORnext) {
        this.changeVariantPrevious = 'Neutral';
        this.changeVariantNext = 'Neutral';
        let first = parseInt(this.showButtonFirst, 10);
        let second = parseInt(this.showButtonSecond, 10);
        let third = parseInt(this.showButtonThird, 10);

        this.isFirstButton = true;
        this.isSecondButton = true;
        this.isThirdButton = true;

        if (previousORnext === 'previous') {
            this.changeVariantPrevious = 'brand';
            if (this.totalPage >= 1 && this.totalPage <= 3) {
                if (this.page === 1) {
                    this.changeVariantFirst = 'brand';
                    this.changeVariantSecond = 'Neutral';
                    this.changeVariantThird = 'Neutral';
                } else if (this.page === 2) {
                    this.changeVariantFirst = 'Neutral';
                    this.changeVariantSecond = 'brand';
                    this.changeVariantThird = 'Neutral';
                }
                //For Total pages == 2
                if (this.totalPage === 2) // for 2 page
                {
                    this.isThirdButton = false;
                }
            } else if (this.totalPage > 3) // for equal to or more than 3 pages
            {
                if (this.page === 1) {
                    if (this.changeVariantSecond === 'brand')
                        this.i = 0;
                    this.changeVariantFirst = 'brand';
                    this.changeVariantSecond = 'Neutral';
                    this.changeVariantThird = 'Neutral';
                } else if (this.page === 2) {
                    if (this.changeVariantSecond === 'brand') this.i = 1;
                    else if (this.changeVariantThird === 'brand') this.i = 0;
                    if (this.changeVariantFirst === 'brand')
                        this.changeVariantFirst = 'brand';
                    else {
                        this.changeVariantFirst = 'Neutral';
                        this.changeVariantSecond = 'brand';
                        this.changeVariantThird = 'Neutral';
                    }
                } else {
                    this.i = 1;
                    if (this.changeVariantFirst === 'brand') {
                        this.changeVariantSecond = 'Neutral';
                        this.changeVariantThird = 'Neutral';
                    } else if (this.changeVariantSecond === 'brand') {
                        this.changeVariantFirst = 'Neutral';
                        this.changeVariantThird = 'Neutral';
                    } else {
                        this.changeVariantFirst = 'Neutral';
                        this.changeVariantSecond = 'Neutral';
                        this.changeVariantThird = 'brand';
                    }
                }
                this.showButtonFirst = first - this.i;
                this.showButtonSecond = second - this.i;
                this.showButtonThird = third - this.i;
            }
        }

        if (previousORnext === 'next') {
            this.changeVariantNext = 'brand';
            if (this.totalPage >= 1) {
                if (this.totalPage === 2) // for 2 page
                {
                    this.isThirdButton = false;

                    if (this.page === 1) {
                        this.changeVariantFirst = 'brand';
                        this.changeVariantSecond = 'Neutral';
                        this.changeVariantThird = 'Neutral';
                    } else if (this.page === 2) {
                        this.changeVariantSecond = 'brand';
                        this.changeVariantFirst = 'Neutral';
                        this.changeVariantThird = 'Neutral';
                    }
                } else if (this.totalPage === 3) // for 3 page
                {
                    if (this.page === 1) {
                        this.changeVariantFirst = 'brand';
                        this.changeVariantSecond = 'Neutral';
                        this.changeVariantThird = 'Neutral';
                    } else if (this.page === 2) {
                        this.changeVariantSecond = 'brand';
                        this.changeVariantFirst = 'Neutral';
                        this.changeVariantThird = 'Neutral';
                    } else {
                        this.changeVariantSecond = 'Neutral';
                        this.changeVariantFirst = 'Neutral';
                        this.changeVariantThird = 'brand';
                    }
                } else if (this.totalPage > 3) // for more than 3 pages
                {
                    if ((this.totalPage - this.page) === 1 && this.changeVariantFirst === 'brand') {
                        this.i = 0;
                        this.changeVariantSecond = 'brand';
                        this.changeVariantFirst = 'Neutral';
                        this.changeVariantThird = 'Neutral';
                    } else if ((this.totalPage - this.page) === 1 && this.changeVariantSecond === 'brand') {
                        this.i = 1;
                    } else if ((this.totalPage - this.page) === 0 && this.changeVariantSecond === 'brand') {
                        this.i = 0;
                        this.changeVariantThird = 'brand';
                        this.changeVariantFirst = 'Neutral';
                        this.changeVariantSecond = 'Neutral';
                    } else {
                        this.i = 1;
                        if (this.changeVariantFirst === 'brand') {
                            this.changeVariantSecond = 'Neutral';
                            this.changeVariantThird = 'Neutral';
                        } else if (this.changeVariantSecond === 'brand') {
                            this.changeVariantFirst = 'Neutral';
                            this.changeVariantThird = 'Neutral';
                        } else if (this.changeVariantThird === 'brand') {
                            this.changeVariantFirst = 'Neutral';
                            this.changeVariantSecond = 'Neutral';
                        }
                    }

                    this.showButtonFirst = first + this.i;
                    this.showButtonSecond = second + this.i;
                    this.showButtonThird = third + this.i;
                }
            }
        }
    }

    showCurrentPageData(event) {
        this.template.querySelector('.PharmacyClaimsDataTable').selectedRows = null;
        this.toCompareRowsTable = null;
        this.showComparison = false;
        if (this.totalPage === 1 && event.target.title === 'first') {
            this.page = 1;
            this.isLastPage = true;
            this.isFirstPage = true;
            this.changeVariantFirst = 'brand';
            this.changeVariantSecond = 'Neutral';
            this.changeVariantThird = 'Neutral';
        } else if (this.totalPage === 2) {
            if (event.target.title === 'first') {
                this.page = 1;
                this.changeVariantFirst = 'brand';
                this.changeVariantSecond = 'Neutral';
            } else if (event.target.title === 'second') {
                if (this.changeVariantFirst === 'brand' || this.changeVariantPrevious === 'brand')
                    this.page = this.page + 1;
                this.changeVariantFirst = 'Neutral';
                this.changeVariantSecond = 'brand';
            }
            this.changeVariantPrevious = 'Neutral';
            this.changeVariantNext = 'Neutral';
        } else if (this.totalPage >= 3) {
            if (event.target.title === 'first') {
                if (this.totalPage === 3)
                    this.page = 1;
                else if (this.changeVariantThird === 'brand' || this.changeVariantNext === 'brand')
                    this.page = this.page - 2;
                else if (this.changeVariantSecond === 'brand')
                    this.page = this.page - 1;

                this.changeVariantFirst = 'brand';
                this.changeVariantSecond = 'Neutral';
                this.changeVariantThird = 'Neutral';
            } else if (event.target.title === 'second') {
                if (this.changeVariantFirst === 'brand')
                    this.page = this.page + 1;
                else if (this.changeVariantThird === 'brand' || this.changeVariantNext === 'brand')
                    this.page = this.page - 1;
                else if (this.changeVariantPrevious === 'brand')
                    this.page = this.page + 2;

                this.isFirstPage = false;
                this.isLastPage = false;
                this.changeVariantFirst = 'Neutral';
                this.changeVariantSecond = 'brand';
                this.changeVariantThird = 'Neutral';
            } else if (event.target.title === 'third') {
                this.thirdButtonClick = true;
                if (this.changeVariantFirst === 'brand' || this.changeVariantPrevious === 'brand')
                    this.page = this.page + 2;
                else if (this.changeVariantSecond === 'brand')
                    this.page = this.page + 1;
                this.changeVariantFirst = 'Neutral';
                this.changeVariantSecond = 'Neutral';
                this.changeVariantThird = 'brand';
            }
            this.changeVariantPrevious = 'Neutral';
            this.changeVariantNext = 'Neutral';
        }
        this.displayRecordPerPage(this.page);
    }

    //this method displays records page by page
    displayRecordPerPage(page) {
        var allRecords;
        if (this.filterAndPageLength === true)
            allRecords = this.filterDataArray;
        else
            allRecords = this.copyPharmacyRXData;
        console.log('displayRecordPerPage@@')
        this.items = allRecords;
        this.totalRecordCount = allRecords.length;
        //let's say for 2nd page, it will be => "Displaying 6 to 10 of 23 records. Page 2 of 5"
        // page = 2; pageSize = 5; startingRecord = 5, endingRecord = 10
        // so, slice(5,10) will give 5th to 9th records.

        
        this.startingRecord1 = ((page - 1) * this.pageSize) + 1;
        this.startingRecord = ((page - 1) * this.pageSize);      
        this.endingRecord = (this.pageSize * page);

        this.endingRecord = (this.endingRecord > this.totalRecordCount) ?
            this.totalRecordCount : this.endingRecord;

        if (this.page === this.totalPage) {
            this.isLastPage = true;
            this.changeVariantNext = 'Neutral';
            this.isFirstPage = false;
        }
        if (this.page === 1) {
            this.showButtonFirst = '1';
            this.showButtonSecond = '2';
            this.showButtonThird = '3';
            this.isFirstPage = true;
            this.changeVariantPrevious = 'Neutral';
            this.isLastPage = false;
        }
        this.pharmacyRXData = this.items.slice(this.startingRecord, this.endingRecord);
    }
    //Pagination Functionality -- END -- // 

	//Accordion Functionality -- START -- // 
    getSelectedRow(event) {
        this.toCompareRows = event.detail.selectedRows;
        if (this.toCompareRows && this.toCompareRows.length) {
            this.showComparison = true;
        }
        else {
            this.showComparison = false;
        }
        this.toCompareRowsTable = event.detail.selectedRows.map(r => {
            var returnMap = {};
            returnMap.ClaimID = r.ClaimID;
            returnMap.row = [r];
            returnMap.isExpanded = true;
            return returnMap;
        });
        if (!this.expandAdded) {
            this.eachRowColumn = this.columns;
            var addButton = {
                label: 'View',
                type: 'button-icon',
                initialWidth: 75,
                typeAttributes: {
                    name: 'expand',
                    iconName: 'utility:choice',
                    title: 'Preview',
                    variant: 'border-filled',
                    alternativeText: 'View'
                },
                hideDefaultActions: true
            };
            for (var col of this.eachRowColumn){
                col.hideDefaultActions = true;
            }
            
            this.eachRowColumn.unshift(addButton);
            this.expandAdded = true;
        }
    }
    get compareDisabled() {
        return !(this.toCompareRows && this.toCompareRows.length && this.showComparison);
    }
    compareNow(event) {
        var elem = this.template.querySelector('.detailedRows');
        var elemTop = elem.getBoundingClientRect().top;
        elemTop += window.scrollY?window.scrollY:window.pageYOffset;
        window.scrollTo(0, elemTop );
 
    }
    expandRow(event) {
        if (event.detail.action.name === 'expand') {
            var indexToExpand = event.target.dataset.name;
            this.toCompareRowsTable[indexToExpand].isExpanded = !this.toCompareRowsTable[indexToExpand].isExpanded;
        } else {
            const ClaimID = event.detail.row.ClaimID;
            const AuthorizationNumber = event.detail.row.AuthorizationNumber;
            const e = new CustomEvent('rowclick', {
                detail: {
                    ClaimID:this.memberGenKey,
                    AuthorizationNumber:AuthorizationNumber
                }
            });
            this.dispatchEvent(e);

        }

       }
    gotoTop() {
        var elem = this.template.querySelector('.PharmacyClaimsDataTable');
        var elemTop = elem.getBoundingClientRect().top;
        elemTop += window.scrollY?window.scrollY:window.pageYOffset;
        window.scrollTo(0, elemTop);
    }
    get deductibleMessageColumn() {
        var columns = [
            { label: 'Deductible Messages', fieldName: 'DM', hideDefaultActions: true },
        ];
        return columns;
    }
    resetSelectedRows(selectedRows){
        if(selectedRows){
            this.template.querySelector('.PharmacyClaimsDataTable').selectedRows = selectedRows;
        }else{
            this.template.querySelector('.PharmacyClaimsDataTable').selectedRows = [];
            this.toCompareRowsTable = null;
            this.showComparison = false;
        }
       
    }

    @track
    toCompareRowsTable = [];
    showComparison = false;

	//Accordion Functionality -- END -- //
}