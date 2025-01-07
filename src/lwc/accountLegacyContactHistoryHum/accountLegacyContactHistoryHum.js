/*******************************************************************************************************************************
LWC JS Name : AccountLegacyContactHistoryHum.js
Function    : This JS serves as controller to AccountLegacyContactHistoryHum.html. 

Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Ritik Agarwal                                           03/04/2021                    initial version(azure # 2039867)
* Ritik Agarwal                                           03/09/2021                    add logic for filters
* Ritik Agarwal                                           03/10/2021                    add logic for date filters
* Ritik Agarwal                                           03/11/2021                    add logic for date validations and filter messages
* Ritik Agarwal                                           03/21/2021                    add ccp reference number logic
* Mohan Kumar                                             03/22/2021                    fixed HTML issues and designed UI
* Ritik Agarwal                                           04/02/2021                    Added a logic for passing required data to inquiryDetails LWC to open it in subtab
* Mohan Kumar N                                           04/16/2021                    Fix for DF: 2891
* Supriya Shastri                                         04/20/2021                    Fix for DF-2899  
* Supriya Shastri                                         04/23/2021                    Added logic for subtab filters
* Ankima Srivastava                                       08/17/2021                   US: 2233885 - Label Change
* Ankima Srivastava                                       08/18/2021                   US: 2233885 - Label Change update
* Supriya Shastri                                         10/27/2021                   US: 2440592 : Account Detail Page Redesign
* Ankima Srivastava                                       10/28/2021                   US : 2581786 - count implementation
* Abhishek Mangutkar									  08/09/2022                   DF - 5542					
*********************************************************************************************************************************/
import { LightningElement, track, api, wire } from 'lwc';
import getInquirySummary from '@salesforce/apexContinuation/AccLegacyContactHistory_LC_HUM.getInquirySummary';
import getInquirySingleReference from '@salesforce/apexContinuation/AccLegacyContactHistory_LC_HUM.getInquirySingleReference';
import {
    getFormattedDate, getDateYear, hcConstants, getDateDiffInYears, deleteObjProperties,
    getFinalFilterList, getPillFilterValues, getReversedateFormat, getPickListValues, getFilterData
} from "c/crmUtilityHum";
import { getLabels } from 'c/customLabelsHum';
import { legacyCasehistory } from './layoutConfig';
import { getRecord } from 'lightning/uiRecordApi';

export default class AccountLegacyContactHistoryHum extends LightningElement {
    @api recordId;
    @track sRecordTypeName;
    @api showViewAll = false;
    @api bInfiniteScroll;
    @track labels = getLabels();
    @track response = [];
    @track oParams;
    @track legacyHistoryTable;
    @track legacyHistoryTableModel = {
        'sStartDate': hcConstants.LEGACY_SDATE, 'sEndDate': hcConstants.LEGACY_EDATE, 'dateNotExists': false
    };
    @track constants = hcConstants;
    @track filterFldValues = {};
    @track filterObj = {};
    @track tempList;
    @track pillFilterValues = [];
    @api oAppliedFilters;
    @track oViewAllParams = {};
    @track clearFilters = false;
    @track isGroup = false;
    @track isMember = false;
    @track isAgencyProvider = false;
    @track iconName = 'utility:filterList';
    @track totalRecords = 0;
    @track bDataLoaded;
	@track loggingRelatedField = [{
        label : "Referance ID",
        mappingField : "sReferenceID"
    }];
    @track screeName = 'Legacy Contact History';

    @wire(getRecord, {
        recordId: '$recordId',fields: ['Account.Name'] 
     })
     wiredAccount({
        error,
        data
     }) {
        if (data) {
           this.sRecordTypeName = data.recordTypeInfo.name;
           this.oViewAllParams = {
               sRecordTypeName: this.sRecordTypeName,
               sRecordId : this.recordId 
           }
           this.fetchLegacyData(this.sRecordTypeName);
        }
        else if(error){
           console.log('error in wire--',error);   
        }
    }

    fetchLegacyData(recordType) {
        const me = this;
        this.showViewAll = this.bInfiniteScroll===undefined;
        const inputParams = { recId: this.recordId, startCount: null, endCount: null, sStartDate: getReversedateFormat(this.legacyHistoryTableModel.sStartDate), sEndDate: getReversedateFormat(this.legacyHistoryTableModel.sEndDate), sRecordType: this.sRecordTypeName };
        const memberPlanLabel = this.labels.Policy;
        const groupPlanLabel = this.labels.Group_Policy;
        this.legacyHistoryTable = legacyCasehistory;
        this.legacyHistoryTable.forEach(function(element){
            element.forEach(function(elem){
                if(elem.actionName==hcConstants.INQUIRED_ABOUT){
                    elem.compoundvalue.forEach(function(elemVal){
                        if(elemVal.fieldName=='sPolicy' && recordType==hcConstants.MEMBER){
                            elemVal.label=memberPlanLabel;
                        }else if(elemVal.fieldName=='sPolicy' && recordType==hcConstants.GROUP){
                            elemVal.label=groupPlanLabel;
                        }
                    });
                }   
            });
        });
        this.oParams = inputParams;
        this.getInquiryDetails(inputParams);
        if(recordType === hcConstants.GROUP){
            this.isGroup = true;
          }
        else if(recordType === hcConstants.MEMBER){
            this.isMember = true
        }else{
            this.isAgencyProvider = true;
        }

    }
    setTotalRecords(oData) {
        if(oData){
            this.totalRecords = Object.values(oData).length;
        }
    }

    getInquiryDetails(params) {
        getInquirySummary(params).then((result) => {
            this.bDataLoaded = true;
            if(result && result.length > 0 && result[0].bContactDet === true){
                this.provideDataToDOM(result);
            }else{
                 this.template.querySelector('c-standard-table-component-hum').noDataMessage = this.labels.Hum_NoResultsFound;
                this.response = result;
            }
        this.setTotalRecords(result); 
        }).catch(error => {
            console.log('errorINLEGACYSTROY--', error);
        })
    }

    provideDataToDOM(res) {
        let dataPresent = (res && res.length > 0 && res[0].bContactDet === true) ? true : false;
        let userData = res ? (dataPresent ? res : []) : [];
        this.response = userData.map(item => {
            return {
                ...item,
                Inquirydata: item.sInquiryID + '#&;' + 'inqId:' + item.sInquiryID + ' ' + 'refId:' + item.sReferenceID + ' ' + 'recId:' + this.recordId
            }
        });
        this.populateInitialData(this.response);
        this.tempList = this.response;
    }

   /**
   * loads Data to StandardTable on the page load
   */
  populateInitialData(oData){
    const me = this;
    if(me.oAppliedFilters && Object.keys(me.oAppliedFilters).length > 0){
      setTimeout(() => {   // Timeout is required to wait for DOM to load and apply the  pre selected filters
        me.iconName = 'standard:filter';
        me.filterObj = JSON.parse(JSON.stringify({ ...me.oAppliedFilters }));
        me.selectFilter();
        me.findByWord();
      },100);
    }
    else{
      me.response = oData;
    }
    me.filterFldValues = getPickListValues([hcConstants.LEGACY_TYPE, hcConstants.LEGACY_POLICY], me.response);
  }

    /**
     * Description - this method will call when user click on Go button of CCP reference number field
     */
    getCCPReferenceData() {
        this.closeDatePicker();
        let refno = this.template.querySelector('.ref-num-filed');
        getInquirySingleReference({ recId: this.recordId, sReferenceNbr: refno.value, sRecordType: this.sRecordTypeName })
            .then((result) => {
                if(result && result.length > 0 && result[0].bContactDet === true){
                    this.provideDataToDOM(result);
                }else{
                    this.template.querySelector('c-standard-table-component-hum').noDataMessage = this.labels.HUM_MessageOnFilter;  
                    this.response = result;
                }
            this.setTotalRecords(result);
            }).catch(error => {
                console.log('errorINLEGACYSTROYCCCP--', error);
            })
    }

    /** 
     * this method will fire when click on GO button form UI to fetch the data from selected date range
    */
    handleDateValues() {
        this.closeDatePicker();
        let dateFieldsValues = this.template.querySelectorAll('.dateVal');
        const fDateDiff = getDateDiffInYears(dateFieldsValues[0].value, dateFieldsValues[1].value);
        if (fDateDiff <= hcConstants.MAXDATEYEAR) {
            const sDate = getReversedateFormat(dateFieldsValues[0].value);
            const eDate = getReversedateFormat(dateFieldsValues[1].value);
            const inputParams = { recId: this.recordId, startCount: null, endCount: null, sStartDate: sDate, sEndDate: eDate, sRecordType: this.sRecordTypeName };
            this.getInquiryDetails(inputParams);
        } else {
            this.template.querySelector('c-standard-table-component-hum').noDataMessage = this.labels.HUM_DateRangeMessage;
            this.response = [];
        }
    }

    /**
     * Description - this method will fire when user change the value of from and to dates fields 
     */
    formatDateVals() {
        let dateFieldsValues = [...this.template.querySelectorAll('.dateVal')];
        this.legacyHistoryTableModel.dateNotExists = dateFieldsValues.some(dateVal => dateVal.value === undefined || dateVal.value === '');
    }

    /**
     * Description - this method will fire when user click on clear button for Date fields
     */
    clearDateValues() {
        this.closeDatePicker();
        let dateFieldsValues = this.template.querySelectorAll('.dateVal');
        dateFieldsValues[0].value === hcConstants.LEGACY_SDATE && dateFieldsValues[1].value === hcConstants.LEGACY_EDATE ? '' : this.populateDefualtDates(dateFieldsValues);
    }

    /**
     * Description - this method is used to populate default from and end date in date fields on UI
     */
    populateDefualtDates(dateValues) {
        this.legacyHistoryTableModel.sStartDate = hcConstants.LEGACY_SDATE;
        dateValues[1].value = hcConstants.LEGACY_EDATE;
        this.legacyHistoryTableModel.sEndDate = hcConstants.LEGACY_EDATE;
    }

    /**
     * @param - event
     * Description - this method will fire when user click outside of From date field
     */
    validateFromDate(event) {
        let fromDateYear = getDateYear(event.target.value);
        fromDateYear < hcConstants.Date_2K19 ? this.legacyHistoryTableModel.sStartDate = '' : '';
        setTimeout(() => {
            this.formatDateVals();
        }, 1);
    }


    /**
     * Format Date on key up event
     * @param {*} event 
     */
    formatDateOnKeyUp(event) {
        if (event.keyCode === 8 || event.keyCode === 46) { //exclude backspace and delete key
            return;
        }
        let dtValue = event.target.value;
        dtValue = getFormattedDate(dtValue);
        event.target.value = dtValue;
        if (event.currentTarget.getAttribute('data-id') === 'fromField') {
            this.legacyHistoryTableModel.sStartDate = dtValue;
        }
        else {
            this.legacyHistoryTableModel.sEndDate = dtValue;
        }
    }

    /**
     * Handle Created date Range date picker close
     * @param {*} event 
     */
    onDatePickerClose(event) {
        const { fromDate, toDate } = event.detail;
        if (fromDate && toDate) {
            this.iconName = 'standard:filter';
            let filterDateVals = [fromDate, toDate];
            delete this.filterObj['sCreatedDate'];
            filterDateVals.map(item => { this.filterObj = getFilterData('sCreatedDate', item, this.filterObj) });
            this.filterObj = {
                ...this.filterObj,
                sCreatedDate: [fromDate, toDate]
            }
            this.getFilterList(this.tempList, this.filterObj);
            this.updateFilters();
        }
    }

    /**
   * Update Filters with view all params
   */
    updateFilters() {
        this.oViewAllParams = {
            ...this.oViewAllParams,
            filters: this.filterObj
        }
        if (Object.keys(this.filterObj).length === 0) {
            this.iconName = 'utility:filterList';
        }
    }

    /*
       this method will fire when user click on pills to remove pills values
    */
    handleRemove(event) {
        const dataToGet = event.detail.data;
        const pillValues = event.detail.pillList;
        this.closeDatePicker();
        this.setTotalRecords(this.tempList);
        if (dataToGet === 'noData') {
            this.filterObj = deleteObjProperties(this.filterObj, [hcConstants.LEGACY_TYPE, hcConstants.LEGACY_POLICY]);
            if (Object.keys(this.filterObj).length > 0) {
                this.getFilterList(this.tempList, this.filterObj);
            } else {
                this.filterObj = {};
                this.template.querySelector('c-standard-table-component-hum').computecallback(this.tempList);
            }
        }
        else {
            this.filterObj = dataToGet;
            this.getFilterList(this.tempList, dataToGet);
        }

        this.pillFilterValues = pillValues;
        let fields = this.template.querySelectorAll('lightning-combobox');
        fields.forEach(function (item) {
            item.value = '';
        });
        this.updateFilters();
    }

    /*
      this method will fire when user enter valoue in filter by keyword box
   */
    findByWord(event) {
        let count = 1;
        const me = this;
        if (event) {
            let value = event.target.value;
            if (value.length > hcConstants.MIN_SEARCH_CHAR) {
                me.iconName = 'standard:filter';
                this.filterObj['searchByWord'] = [value];
            }
            else {
                me.iconName = 'utility:filterList';
                if (this.filterObj.hasOwnProperty('searchByWord')) {
                    delete this.filterObj['searchByWord'];
                    count = 0;
                }
            }
        } else {
            let tempObj = JSON.parse(JSON.stringify(this.filterObj));
            if (tempObj['searchByWord']) {
                me.template.querySelector('.keyword-field').value = tempObj['searchByWord'].toString();
            }
        }
        (Object.keys(this.filterObj).length > 0) ? this.getFilterList(this.tempList, this.filterObj) : (count === 0 ? this.template.querySelector('c-standard-table-component-hum').computecallback(this.tempList) : 0);
    }

    /* 
      Description - this method will fire when user selectany picklistvalue from UI to apply filter on results.
   */
    selectFilter(event) {
        const me = this;
        if (event) {
            me.iconName = 'standard:filter';
            let filterName = event.target.name;
            let filterValue = event.detail.value;
            this.filterObj = getFilterData(filterName, filterValue, this.filterObj);
            if (this.pillFilterValues && !this.pillFilterValues.some(arg => arg["key"] == filterName && arg["value"] == filterValue)) {
                this.pillFilterValues = getPillFilterValues(filterName, filterValue, this.pillFilterValues);
            }
        } else {
            let tempObj = JSON.parse(JSON.stringify(this.filterObj));
            let filterSelectors = this.template.querySelectorAll('lightning-combobox');
            Object.keys(tempObj).forEach(function (key) {
                filterSelectors.forEach(inp => {
                    if (inp.name === key) {
                        if (Array.isArray(tempObj[key])) {
                            tempObj[key].forEach(val => {
                                inp.value = val;
                            })
                        } else {
                            inp.value = tempObj[key];
                        }

                    }
                });
                if (Array.isArray(tempObj[key])) {
                    tempObj[key].forEach(val => {
                        if (key !== 'sCreatedDate') {
                            me.pillFilterValues = getPillFilterValues(key, val, me.pillFilterValues);
                        }
                    })
                } else {
                    me.pillFilterValues = getPillFilterValues(key, tempObj[key], me.pillFilterValues);
                }
            });

        }
        this.getFilterList(this.tempList, this.filterObj);
        this.updateFilters();
    }

    /**
     * Description - this method will fire when user click on clear filter button from UI and will 
                     will wipe out all filters
     */
    clearData() {
        this.iconName = 'utility:filterList';
        this.closeDatePicker(null, true);
        this.pillFilterValues = [];
        this.oAppliedFilters = {};
        this.filterObj = {};
        let fields = this.template.querySelectorAll('lightning-combobox');
        let inputfields = this.template.querySelector('.filter');
        inputfields.value = '';
        fields.forEach(item => item.value = '');
        this.setTotalRecords(this.tempList);
        this.template.querySelector('c-standard-table-component-hum').computecallback(this.tempList);
        this.updateFilters();
    }

    /*
      @param1 - data(on which filter needs to be applied)
      @param2 - filterProperties(contains filter object by which data(i.e.,param1) is filtered)
      Description - this method will fire lastly to show final filter list on UI i.e., this method is very specific to
                    filter operation means it will come in role when any filters are performed.
   */
  getFilterList(data, filterProperties) {
    let filterListData = {};
    filterListData = getFinalFilterList(data, filterProperties, data, 'sCreatedOn');
    let uniqueChars = filterListData.uniqueList;
    this.response = uniqueChars;
    this.setTotalRecords(this.response);
    if (this.response.length <= 0) {
        this.template.querySelector('c-standard-table-component-hum').noDataMessage = this.labels.HUM_MessageOnFilter;
    }
    this.template.querySelector('c-standard-table-component-hum').computecallback(this.response);

} 

    /**
     * Closes Date Range picker. Outside click will not wirk inside text field elemetns , hence forcefully closing
     */
    closeDatePicker(event, isOnclear = false) {
        const me = this;
        if(!me.eleDateRangePicker){
            me.eleDateRangePicker = this.template.querySelector('c-generic-date-range-picker');
        }
        me.eleDateRangePicker.hidePicker(true, isOnclear);
    }
}