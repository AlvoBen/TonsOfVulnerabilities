/*
LWC Name        : loggingExistingCaseHistory.js
Function        : LWC to display existing case history.

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Nirmal Garg                     2/16/2021                   initial version
* Jonathan Dickinson		          08/08/2022		              DF - 5516
* Jonathan Dickinson              06/14/2023                  User Story 4705843: T1PRJ0891339 2023 Arch Remediation-SF-Tech-Filter cases having template attached from existing case history logging for process logging
****************************************************************************************************************************/

import { LightningElement, track, api } from 'lwc';
import generateQueryString from '@salesforce/apex/Logging_LC_HUM.getCaseHistory';
import customcss from '@salesforce/resourceUrl/LightningCRMAssets_SR_HUM';
import { loadStyle } from 'lightning/platformResourceLoader';
import { getCaseHistoryLayout } from './layoutConfig';
import { getDateDiffInDays, getLocaleDate, getLabels } from 'c/crmUtilityHum';

const LOAD_RECORDS = 5;
const INITIAL_RECORDS = 3;
export default class LoggingExistingCaseHistory extends LightningElement {
  @track sFromDate;
  @track sToDate;
  @track sMinDate;
  @track sMaxDate;
  @track startDate;
  @track endDate;
  @track serveresult = [];
  @track initialresponse = [];
  @track tempList;
  @api recordId;
  @api selCaseId;
  @track oData = [];
  @track oCaseHistoryModel;
  @track totalCases;
  @track labels = getLabels();
  @track totalCases;
  @track resultsTrue;
  @track showViewAll = false;
  @track isChecked = false;
  @track IsDataFound;
  @track calledFromLogging = true;
  @api filterCasesHavingTemplate = false;

  connectedCallback() {
    this.initialDates();
    this.fetchCaseData();
  }

  /**
   * Applies pre-selected filters to subtab table
   * and CSS from utility commonstyles file
   * after DOM is rendered
   */
  renderedCallback() {
    Promise.all([
      loadStyle(this, customcss + '/CRM_Assets/styles/commonStyles.css')
    ]).catch(error => {
    });
  }

  fetchCaseData() {
    this.oCaseHistoryModel = getCaseHistoryLayout();
    this.callserver();
  }

  initialDates() {
    let todaysdate = new Date();
    this.sMaxDate = todaysdate.toISOString().substring(0, 10);
    let minDate = new Date();
    minDate.setDate(todaysdate.getDate() - 90);
    this.sMinDate = minDate.toISOString().substring(0, 10);
    let sDate = new Date();
    sDate.setDate(todaysdate.getDate() - 13);
    this.sFromDate = sDate.toISOString().substring(0, 10);
    this.startDate = ((sDate.getMonth() + 1).toString().length == 1 ? '0' + (sDate.getMonth() + 1) : (sDate.getMonth() + 1)) + '/' + (sDate.getDate().toString().length === 1 ? '0' + sDate.getDate() : sDate.getDate()) + '/' + sDate.getFullYear();
    this.endDate = ((todaysdate.getMonth() + 1).toString().length == 1 ? '0' + (todaysdate.getMonth() + 1) : (todaysdate.getMonth() + 1)) + '/' + (todaysdate.getDate().toString().length === 1 ? '0' + todaysdate.getDate() : todaysdate.getDate()) + '/' + todaysdate.getFullYear();
    this.sToDate = todaysdate.toISOString().substring(0, 10);
  }

  handleDateChange(event) {
    let datedata = event.detail;
    if (datedata.keyname === 'FromDate') {
      if (datedata?.datevalue?.includes('-')) {
        let sDate = datedata.datevalue.split('-');
        if (sDate.length > 0) {
          this.startDate = sDate[1] + '/' + sDate[2] + '/' + sDate[0];
        }
      } else {
        this.startDate = datedata.datevalue;
      }
    }
    else if (datedata.keyname === 'ToDate') {
      if (datedata?.datevalue?.includes('-')) {
        let eDate = datedata.datevalue.split('-');
        if (eDate.length > 0) {
          this.endDate = eDate[1] + '/' + eDate[2] + '/' + eDate[0];
        }
      } else {
        this.endDate = datedata.datevalue;
      }
    }
  }

  getData() {
    generateQueryString({ objId: this.recordId, startdate: this.startDate, enddate: this.endDate , filterCasesHavingTemplate: this.filterCasesHavingTemplate}).then((result) => {
      this.serveresult = JSON.parse(result);
      this.initialresponse = this.serveresult;
      this.tempList = this.serveresult;
      if (this.serveresult != null && this.serveresult.length > 0) {
        this.processResponse(JSON.parse(result));
      } else {
		  this.resultsTrue = false;
		  this.IsDataFound = false;
		  this.totalCases = 0;
      }
    }).catch((error) => {
      console.log('Error Occured', error);
    });
  }
  callserver() {
    this.getData();
  }

  handleSearch() {
    this.getData();
  }
  processResponse(result) {
    const me = this;
    let dtToday = getLocaleDate(new Date());
    me.response = result.map(item => {
      return {
        ...item,
        isLink: false,
        dcnLink: item.sDCN === 'Yes',
        sDCN: (item.sDCN === 'Yes') ? 'Yes' + '#&;' + item.sdcnURL : 'No',
        isOpenedInTwoWeeks: getDateDiffInDays(dtToday, item.sCreatedDate) < 14
      }
    });
    me.response = [...me.response];
    if (me.response.length == 1) {
      me.response.forEach(k => {
        k.isLink = true;
      })
      this.dispatchEvent(new CustomEvent('selectedcasedata', {
        detail: {
          selectedCaseId: me.response[0].Id,
          selectedCaseNumber : me.response[0].sCaseNum,
          checked : true
        }
      }))
    }
    if (me.response.length > INITIAL_RECORDS) {
      me.oData = me.response.slice(0, INITIAL_RECORDS);
      this.showViewAll = true;
    } else {
      me.oData = me.response;
      this.showViewAll = false;
    }
    me.setTotalRecords(me.response);
  }
  setTotalRecords(oData) {
    this.totalCases = Object.values(oData).length;
    this.resultsTrue = true;
    this.IsDataFound = this.totalCases > 0 ? true : false;
  }
  handlecheckboxcheck(event) {
    this.selCaseId = event.detail.recId;
    this.isChecked = event.detail.checked;
    if (this.template.querySelector('c-standard-table-component-hum') != null) {
      this.template.querySelector('c-standard-table-component-hum').disableCheckBox(event.detail.recId, event.detail.checked);
    }
    this.dispatchEvent(new CustomEvent('selectedcasedata', {
      detail: {
        selectedCaseId: event.detail.recId,
        selectedCaseNumber : this.response.find(k => k.Id === this.selCaseId).sCaseNum,
        checked : this.isChecked
      }
    }))
  }

  onViewAllClick() {
    let casesCount = this.oData.length;
    this.oData = [];
    if (this.response.length <= (casesCount + LOAD_RECORDS)) {
      this.oData = this.response.slice(0, this.response.length);
      this.showViewAll = false;
    } else {
      this.oData = this.response.slice(0, (casesCount + LOAD_RECORDS));
      this.showViewAll = true;
    }
  }

  renderedCallback() {
    if (this.template.querySelector('c-standard-table-component-hum') != null && this.selCaseId) {
      this.template.querySelector('c-standard-table-component-hum').disableCheckBox(this.selCaseId, this.isChecked);
    }
  }

  findByWord(event) {
    let textToSearch = event.detail.value;
    let temp = [];
    if (textToSearch && textToSearch.length >= 3) {
      this.response.forEach(t => {
        if (JSON.stringify(t).toLocaleLowerCase().includes(textToSearch.toLocaleLowerCase())
          && !temp.includes(t)) {
          temp.push(t);
        }
      })
      this.oData = [];
      this.oData = temp;
      this.showViewAll = temp.length > 3 ? true : false;
      this.setTotalRecords(this.oData);
    }else{
    	this.oData = this.response;
    	this.showViewAll = temp.length > 3 ? true : false;
      this.setTotalRecords(this.oData);
    }
  }
}