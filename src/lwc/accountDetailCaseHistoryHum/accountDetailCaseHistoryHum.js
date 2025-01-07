/*******************************************************************************************************************************
LWC JS Name : accountDetailCaseHistoryHum.js
Function    : This JS serves as controller to accountDetailCaseHistoryHum.html. 

Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Akshay K                                               11/24/2020                    initial version(azure # 1614696)
* Mohan kumar N                                          03/22/2021                    created on date auto format fix
* Supriya                                                04/12/2021                    US-2032407 case history subtab filters  
* Mohan                                                  06/02/2021                    US-2176313 Apply fileter on open cases click  
* Kajal Namdev                                           08/12/2021                    US-2306063 Group account label update
* Gowthami Thota                                         10/29/2021                    US-1749572 & 2477381 MF1-Case History - GBO Page Updates & Case History Filters
* Gowthami Thota                                         12/03/2021                    US- 2081786 Case Management - Case Linking
* Abhishek Mangutkar			 					                  	 05/09/2022				             US-2871585
* Ritik Agarwal                                          11/01/2022                    Added new case button
* Ankima/Isha                                            07/18/2022                    Added condition to show info message for archived case functionality
* Dinesh Subramaniyan                                    03/17/2023                   DF-7129: REG_PROD Lightning: In Agency/Broker Account page
* Muthukumar											 09/19/2023				  	  Disablement of new case creation for legacy member (Account Level)
* Prasuna Pattabhi                                      10/06/2023          4809356 Pharmacy - Person Account Page - "Interacting With Type" Filter Missing on Case History section - (Lightning)
* Prasuna Pattabhi                                      10/11/2023          Defect 8207
***********************************************************************************************************************************/
import { LightningElement, track, api, wire } from 'lwc';
import generateQueryString from '@salesforce/apex/CaseHistoryComponent_LC_HUM.generatecasehistory';
import { getDateDiffInDays, getUserGroup, getPickListValues, getLocaleDate, getFilterData, getPillFilterValues, getFinalFilterList, getLabels, hcConstants, getFormattedDate } from 'c/crmUtilityHum';
import { getCaseHistoryLayout } from './layoutConfig';
import linkSelectedCases from '@salesforce/apex/CaseHistoryComponent_LC_HUM.linkSelectedCases';
import customcss from '@salesforce/resourceUrl/LightningCRMAssets_SR_HUM';
import { loadStyle } from 'lightning/platformResourceLoader'; 
import { getRecord } from 'lightning/uiRecordApi';
import { NavigationMixin } from 'lightning/navigation';
import { ShowToastEvent } from "lightning/platformShowToastEvent";
import { openLWCSubtab } from 'c/workSpaceUtilityComponentHum';
import archivedLinkSwitchValue from "@salesforce/apex/CaseHistoryComponent_LC_HUM.archivedLinkSwitchValue";
import caseHistoryInfoMsg from "@salesforce/label/c.HUMArchival_CaseHistoryInfo";
import isCRMFunctionalityONJS from '@salesforce/apex/HUMUtilityHelper_LWC.isCRMFunctionalityONJS';

export default class AccountDetailCaseHistoryHum extends NavigationMixin (LightningElement) {
  @track response = [];
  @track oData = [];
  @track resultsTrue = false;
  @track enableNewbutton = true;
  @track enableLinkbutton = true;
  @track totalPolicies;
  @track count = 0;
  @track resultantarray = [];
  @track serveresult = [];
  @api recordId;
  @api objectApiName;
  @api sRecordTypeName;
  @api bInfiniteScroll;
  @api breadCrumbItems;
  @api title = 'Case History';
  @api showViewAll = false;
  @track tempList;
  @track filterFldValues = {};
  @track filterObj = {};
  @track pillFilterValues = [];
  @track tmp = [];
  @track labels = getLabels();
  @track initialresponse = [];
  @track isHidden = 'slds-hidden slds-is-absolute';
  @track showDateClass = 'slds-popover slds-popover_small slds-popover_prompt slds-is-absolute ch-custom-date-popover';
  @track bShowDatePicker = this.isHidden;
  @track toDate;
  @track fromDate;
  @track filterObjKeys = [];
  @track responseToSend;
  @track isProvider = false;
  @track keyword;
  @track oCaseHistoryModel;
  @track subTabDetails;
  @api oAppliedFilters;  
  @track oViewAllParams = {};
  @track isGroupCaseHistory = false;
  @track iconName = 'utility:filterList';
  @track isGBO = false;
  @track isbPharmacy = false;
  @track isbProvider = false;
  @track isbRcc = false;
  @track isGeneral = false;
  @track bShowMessage = false;
  @track bShowWarning = false;
  @track bInvalidQueueWarning = false;
  @track selectedCaseIds = [];
  @track loggingRelatedField = [{
        label : "Case Information",
        mappingField : "sCaseNum"
    }];
     //added for Archived Cases
  @track showArchivedCase=true;
  @track showArchivalInfoMsg=caseHistoryInfoMsg;
  wireFieldToPass = [];
  @track grayOutCheckBox = false;
  @track isSwitchOn4809356 = false;

  @wire(isCRMFunctionalityONJS, { sStoryNumber: ['4809356'] })
    switchFuntion({ error, data }) {
        if (data) {
            this.isSwitchOn4809356 = data['4809356'];
        }
        if (error) {
            console.log('error---', error)
        }
    }
  /**
   * Applies pre-selected filters to subtab table
   * and CSS from utility commonstyles file
   * after DOM is rendered
   */
  renderedCallback() {
    Promise.all([
      loadStyle(this,customcss+'/CRM_Assets/styles/commonStyles.css')
    ]).catch(error=>{
    });
  }

  @wire(getRecord, {
        recordId: '$recordId', fields: '$wireFieldToPass'
 })
 wiredAccount({
    error,
    data
 }) {
    if (data) {
       this.sRecordTypeName = data.recordTypeInfo ?data.recordTypeInfo.name : 'xyz';
       this.fetchCaseData(this.sRecordTypeName);
	   if(data.id.startsWith("001")){
        this.grayOutCheckBox = data.fields.ETL_Record_Deleted__c.value;
       }else{
        this.grayOutCheckBox = data.fields.Member.value.fields.ETL_Record_Deleted__c.value;
       }
    }
    else if(error){
       console.log('error in wire--',error);   
    }
}

connectedCallback() {
  if(this.recordId != 'undefined'){
    if(this.recordId.startsWith("001")){
    this.wireFieldToPass = ['Account.Name','Account.ETL_Record_Deleted__c'];

  }else{
    this.wireFieldToPass = ['MemberPlan.Name','MemberPlan.Member.ETL_Record_Deleted__c'];
  }
  }
   archivedLinkSwitchValue().then((result)=>{
       this.showArchivedCase = result;
   });
 }
fetchCaseData(recType) {
    const me = this;
    me.showViewAll = this.bInfiniteScroll===undefined;
    let oUserGroup =  getUserGroup(); 
    this.oViewAllParams = {
        sRecordTypeName : recType,
        sRecordId : this.recordId,
        oUserGroup 
    }
	
	if(oUserGroup.bGbo === true ){
      this.isGBO = true;
    }
    else if(oUserGroup.bPharmacy === true){
      this.isbPharmacy = true;
    }
    else if(oUserGroup.bProvider === true){
      this.isbProvider = true;
    }
    else if(oUserGroup.bRcc === true){
      this.isbRcc = true;
    }
    else if(oUserGroup.bGeneral === true){
      this.isGeneral = true;
    }

    this.oCaseHistoryModel = getCaseHistoryLayout(oUserGroup);
    if (recType === hcConstants.PROVIDER || recType === hcConstants.AGENCY) {
      this.isProvider = true;
      this.enableNewbutton = false;
    }
    if(recType === hcConstants.GROUP){
      this.isGroupCaseHistory = true;
    }
    this.callserver();

    // listen focus out off datepcker 
    /* this.datePickerFocusOutListener = function (evnt) {
      const bDateContainer = evnt.toElement && evnt.toElement.classList
        && evnt.toElement.classList.contains('ch-custom-date-popover');
      if (bDateContainer === false && me.bShowDatePicker === me.showDateClass) {
        me.bShowDatePicker = me.isHidden;
        me.filterByRange();
      }
    };
    document.addEventListener('click', this.datePickerFocusOutListener, true); */

    this.datePickerFocusOutListener = function (event) {
        if(!me.template.querySelector('.ch-date-picker').contains(event.target)){
          console.log("outside clicked")
        }
    }
      document.addEventListener('click', this.datePickerFocusOutListener, false);
      
  }

  /**
   * Remove event listeners on disconnect
   */
  disconnectedCallback() {
    document.removeEventListener('click', this.datePickerFocusOutListener);
  }

  /**
   * on Form items focus , hide custom date picker
   */
  onFormItemFocus() {
    const me = this;
    if (me.bShowDatePicker === me.showDateClass) {
      me.bShowDatePicker = me.isHidden;
      me.filterByRange();
    }
  }

  callserver() {
    generateQueryString({ objID: this.recordId }).then((result) => {
      this.serveresult = JSON.parse(result);
      this.initialresponse = this.serveresult;
      this.tempList = this.serveresult;
      if (this.serveresult && this.serveresult.length > 0) {
          this.filterFldValues = getPickListValues(['sClassification', 'sIntent', 'sStatus','sProduct','sOrigin','sInteractingWithType','sOwnerQueue','sDCN'], this.serveresult);

      }
      this.processResponse(JSON.parse(result));
      this.handlePreselectedFilters();
    }).catch((error) => {
      console.log('Error Occured', error);
    });
  }

  processResponse(result) {
    const me = this;
    let dtToday = getLocaleDate(new Date());
    me.response = result.map(item => {
      return {
        ...item,
        isLink: false,
        dcnLink: item.sDCN==='Yes',
        sDCN : (item.sDCN==='Yes') ? 'Yes' +'#&;'+ item.sdcnURL : 'No',
        isOpenedInTwoWeeks: getDateDiffInDays(dtToday, item.sCreatedDate) < 14
      }
    });
    if(!me.oAppliedFilters || Object.keys(me.oAppliedFilters).length === 0){
      me.oData = [...me.response];
      me.setTotalRecords(me.oData);
    } else {
        me.iconName = 'standard:filter';
    }
  }

  /**
   * Applies pre-selected filters to subtab table
   * after DOM is rendered
   */
  handlePreselectedFilters() {
      if(this.oAppliedFilters) {
   this.filterObj = JSON.parse(JSON.stringify(this.oAppliedFilters));
   if(this.oAppliedFilters.hasOwnProperty('searchByWord')){
       this.findByWord(undefined, this.oAppliedFilters['searchByWord']);
      }
    if(this.oAppliedFilters.hasOwnProperty('sCreatedDate')){
      this.template.querySelector(".birthdate-FromInput").value = this.oAppliedFilters['sCreatedDate'][0] ;
      this.template.querySelector(".birthdate-ToInput").value = this.oAppliedFilters['sCreatedDate'][1];
      this.formatDateForFilter(this.oAppliedFilters['sCreatedDate'][0], this.oAppliedFilters['sCreatedDate'][1], 'preSelect');
    } 
    if(this.oAppliedFilters.hasOwnProperty('sClassification') || this.oAppliedFilters.hasOwnProperty('sIntent') || this.oAppliedFilters.hasOwnProperty('sStatus') || (this.isSwitchOn4809356 == true && this.oAppliedFilters.hasOwnProperty('sInteractingWithType'))){
      this.selectFilter();
    } 
    }
  }

  setTotalRecords(oData) {
    this.totalPolicies = Object.values(oData).length;
    this.resultsTrue = true;
  }

  getFormattedDate(filterValue) {
    var oDate = filterValue.split("-")
    return oDate[1] + "/" + oDate[2] + "/" + oDate[0];
  }

  selectFilter(event) {
    const me = this;
    if (event) {  //on selection of filters by user
      var filterName = event.target.name;
      var filterValue = event.detail.value;
      if (filterName == 'sCreatedDate') {
        filterValue = this.getFormattedDate(filterValue);
      }
      if (filterName === 'clear') {
        this.clearData();
        return;
      }
      this.filterObj = getFilterData(filterName, filterValue, this.filterObj);
      if (this.pillFilterValues && !this.pillFilterValues.some(arg => arg["key"] == filterName && arg["value"] == filterValue)) {
        this.pillFilterValues = getPillFilterValues(filterName, filterValue, this.pillFilterValues);
      }
    } else {  //if filters are pre-selected
      let tempObj = JSON.parse(JSON.stringify(this.filterObj));
      let filterSelectors =  this.template.querySelectorAll('lightning-combobox');
      Object.keys(tempObj).forEach(function(key){
        filterSelectors.forEach(inp => {
          if( inp.name === key) {
            if(Array.isArray(tempObj[key])) {
              tempObj[key].forEach(val => {
                inp.value = val;
              })
            } else {
              inp.value = tempObj[key];
            }

          }
        });
        if(Array.isArray(tempObj[key])) {
          tempObj[key].forEach(val => {
            if (key !== 'searchByWord' && key !== 'sCreatedDate') {
              me.pillFilterValues = getPillFilterValues(key, val, me.pillFilterValues); //for multi-select combobox, updates dropdown with last selected value
            }
          })
        } else {
          me.pillFilterValues = getPillFilterValues(key, tempObj[key], me.pillFilterValues);  //for single select, updates dropdown with selected value
        }
      });
    }
    let tempFilters = this.filterObj;
    this.getFilterList(this.response,tempFilters);
    this.updateFilters();
  }

  getFilterList(data, filterProperties) {
    this.oViewAllParams.filters = filterProperties;
    const me = this;
    let filterListData = {};
    filterListData = getFinalFilterList(data, filterProperties, this.tmp);
    this.tmp = filterListData.response;
    let uniqueChars = filterListData.uniqueList;
    this.totalPolicies = uniqueChars.length;
    this.serveresult = uniqueChars;
    if (this.serveresult.length <= 0) {
      me.template.querySelector('c-standard-table-component-hum').noDataMessage = me.labels.Hum_NoResultsFound;
    }
    me.oData = me.serveresult;
    me.setTotalRecords(me.oData);
  }

  handleRemove(event) {
    this.tmp = [];
    const dataToGet = event.detail.data;
    const pillValues = event.detail.pillList;
    if (dataToGet === 'noData') {
      delete this.filterObj['sClassification'];
      delete this.filterObj['sIntent'];
      delete this.filterObj['sStatus'];
	  delete this.filterObj['sOrigin'];
      delete this.filterObj['sProduct'];
      delete this.filterObj['sInteractingWithType'];
      delete this.filterObj['sOwnerQueue'];
      delete this.filterObj['sDCN'];
      this.serveresult = [];
      if (Object.keys(this.filterObj).length > 0) {
        this.getFilterList(this.response, this.filterObj);
      } else {
        this.filterObj = {};
        this.template.querySelector('c-standard-table-component-hum').computecallback(this.response);
        this.totalPolicies = this.tempList.length;
      }
    }
    else {
      this.filterObj = dataToGet;
      this.getFilterList(this.response, dataToGet);
    }

    this.pillFilterValues = pillValues;
    let fields = this.template.querySelectorAll('lightning-combobox');
    fields.forEach(function (item) {
      item.value = '';
    })
    this.updateFilters();
  }

  handlecheckboxcheck(event) {
    let recId = event.detail.recId;
    let bGeneralCase;
    let bDentalCase;
    let bMedicalcase;
    let caseSecurity = undefined;
      this.response.forEach((item)=>{
        item.isLink = item.isLink===false?item.Id===recId:true;
        bGeneralCase = item.bGeneralCase;
        bDentalCase = item.bDentalCase;
        bMedicalcase = item.bMedicalcase;
      });
      if (bDentalCase == true || bMedicalcase == true) {
          caseSecurity = true;
        }
        else if (bGeneralCase == true) {
          caseSecurity = false;
        }
        else {
          caseSecurity = undefined;
        }
      if (event.detail.checked) {
        this.selectedCaseIds.push(recId+'_'+caseSecurity);
      this.count = this.count + 1;
      if (this.count == 2) {
        this.enableLinkbutton = false;
      }
    }
    else {
      var i = this.selectedCaseIds.indexOf(recId +'_'+ caseSecurity);
            if (i != -1) {
              this.selectedCaseIds.splice(i, 1);
            }
      this.response.forEach((item)=>{
        item.isLink = item.Id===recId?false:item.isLink;
      });
      this.count = this.count - 1;
      if (this.count < 2) {
        this.enableLinkbutton = true;
      }
      else {
        this.enableLinkbutton = false;
      }
    }
  }

  handleLinkCase(){
    linkSelectedCases({ sSelectedCaseIds: this.selectedCaseIds}).then(result => {
      this.bData = result;
      if(this.bData.bShowMessage){
        var sMsg = 'The selected cases have been linked successfully';
        this.showToast("", sMsg ,"success","pester");
        this.resetValues();
      }
      else
      if (this.bData.bShowWarning) {
        var sMsg =  'A link already exists between the selected cases.\n Please make another selection';
        this.showToast("", sMsg, "error","pester");
        this.resetValues();
      }
      else
      if (this.bData.bInvalidQueueWarning) {
        var sMsg =  'Your assigned queue is no longer Active, please \n select an Active queue and try linking the cases \n again';
        this.showToast("", sMsg, "error","pester");
        this.resetValues();
      }
    })
  }
  resetValues(){
      setTimeout(() =>{
      this.enableLinkbutton = true;
      this.template.querySelector("c-standard-table-component-hum").clearRows();
      this.count = 0;
      this.selectedCaseIds = []; //clears the previous selection
      this.template.querySelector("c-standard-table-component-hum").refreshLinkedCasesTables();
    },5000);
  }

   /**
     * Generiic method to handle toast messages
     * @param {*} strTitle 
     * @param {*} strMessage 
     * @param {*} strStyle 
     */
    showToast(strTitle, strMessage, strStyle, strMode) {
      console.log('391 Inside ShowToast');
      this.dispatchEvent(
          new ShowToastEvent({
              title: strTitle,
              message: strMessage,
              variant: strStyle,
              mode: strMode
          })
      );
  }

  clearData() {
    this.serveresult = [];
    this.resultantarray = [];
    this.pillFilterValues = [];
    this.filterObj = {};
    this.tmp = [];
    this.clearInputFields();
    this.totalPolicies = this.tempList.length;
    this.serveresult = this.tempList;
    this.template.querySelector('c-standard-table-component-hum').computecallback(this.response);

    this.bShowDatePicker = this.isHidden;
    this.fromDate = "";
    this.toDate = "";
    this.updateFilters();
  }

  /**
   * clear Input field selection
   */
  clearInputFields(){
    let fields = this.template.querySelectorAll('lightning-combobox');
    let inputfields = this.template.querySelectorAll('lightning-input');
    inputfields.forEach(function (item) {
      item.value = '';
    })
    fields.forEach(function (item) {
      item.value = '';
    })
  }

  enableNew(event) {
    if (event.target.checked) { this.enableNewbutton = false; }
    else { this.enableNewbutton = true; }
  }

  findByWord(event, wordFilterValue) {
    let count = 1;
    const me = this;
    let value ;
    if (event) { //checks if keyword is entered by user for filtering
      let element = event.target;
      value = element.value;
  } else {  ////makesure value is comes from main table to subtab
      value = wordFilterValue[0];
      this.template.querySelector('.inputfield').value = value;
  }

  if (value.length > hcConstants.MIN_SEARCH_CHAR) { // check if filterval is greater than 2 then only create filter object and change the iconto blue
      this.filterObj['searchByWord'] = [value];
  }
  else {                                    // make sure if filterval is greater less than 2 then change icon clour to white again and remove searchByWord filter property from filterObj
      if (this.filterObj.hasOwnProperty('searchByWord')) {
          delete this.filterObj['searchByWord'];
          count = 0;
      }
  }
    console.log("filter obj after key change", this.filterObj)
    if (Object.keys(this.filterObj).length > 0) {
      this.getFilterList(this.response, this.filterObj);
    } else {
      if(count == 0){
        this.showViewAll = true;
        this.template.querySelector('c-standard-table-component-hum').computecallback(this.response);
      }
      this.totalPolicies = this.tempList.length;
    }
    this.updateFilters();
  }

  datePickerShow(event) {
    if (this.bShowDatePicker === this.showDateClass) {
      this.bShowDatePicker = this.isHidden;
      this.filterByRange(event);
    } else {
      this.bShowDatePicker = this.showDateClass;
      if (Array.isArray(this.filterObj['sCreatedDate'])) {  //sets pre-selected date values on DOM
        this.template.querySelector('.birthdate-FromInput').value = this.filterObj['sCreatedDate'][0];
        this.template.querySelector('.birthdate-ToInput').value = this.filterObj['sCreatedDate'][1];
      }
    }
  }

  formatDateOnkeyupHandler(event) {
    this.toDate = this.toDate ? this.toDate : "";
    this.fromDate = this.fromDate ? this.fromDate : "";
    var messageFrom = this.template.querySelector(".message-FromInput");
    var messageTo = this.template.querySelector(".message-ToInput");
    const { value, name } = event.target;

    if (name == "toDate") {
      if (value) {
        this.toDate = getFormattedDate(value);
      }

      if (this.fromDate.length == 10) {
        this.validateFormatDate(this.fromDate, messageFrom);

      } else if (this.fromDate.length < 10) {
        messageFrom.setCustomValidity("");
        messageFrom.reportValidity();
        return false;
      }
    }

    if (name == "fromDate") {
      if (value) {
        this.fromDate = getFormattedDate(value);
      }

      if (this.toDate.length == 10) {
        this.validateFormatDate(this.toDate, messageTo);

      } else if (this.toDate.length < 10) {
        messageTo.setCustomValidity("");
        messageTo.reportValidity();
        return false;
      }
    }

  }

  validateFormatDate(string, message) {
    var regex = /^[0-9]{2}[\/][0-9]{2}[\/][0-9]{4}$/g;
    if (regex.test(string)) {
      var isDate = new Date(string);
      var dd = isDate.getDate();

      if (isNaN(dd)) {
        message.setCustomValidity("Invalid date");
        message.reportValidity();
        return false;
      }
      message.setCustomValidity("");
      message.reportValidity();
      return false;
    } else {
      message.setCustomValidity("Invalid Format");
      message.reportValidity();
      return false;
    }
  }

  formatDateForFilter(from, to, conditionDate) {
    console.log({from}, {to})
    let dateInputF;
    let dateInputT;
    if ((from || to) && conditionDate === 'manual') { //if user enters date values for filtering
       dateInputF = from.value.split("/");
       dateInputT = to.value.split("/");
    }else if(conditionDate === 'preSelect'){
       dateInputF = from.split("/");
       dateInputT = to.split("/");
    }
   if(dateInputF || dateInputT){
      var fInputDate = new Date(dateInputF[2], dateInputF[0] - 1, dateInputF[1], 0, 0, 0, 0);
      var tInputDate = new Date(dateInputT[2], dateInputT[0] - 1, dateInputT[1], 0, 0, 0, 0);

      if (fInputDate && tInputDate) {
        const fInputDatemod = (fInputDate.getMonth() + 1) + '/' + (fInputDate.getDate() > 9 ? fInputDate.getDate() : '0' + fInputDate.getDate()) + '/' + fInputDate.getFullYear();
        const tInputDatemod = (tInputDate.getMonth() + 1) + '/' + (tInputDate.getDate() > 9 ? tInputDate.getDate() : '0' + tInputDate.getDate()) + '/' + tInputDate.getFullYear();

        this.filterObj = {
          ...this.filterObj,
          sCreatedDate: [fInputDatemod, tInputDatemod]
        }
      }
      else {
        delete this.filterObj['sCreatedDate'];
      }
    }
    if (Object.keys(this.filterObj).length > 0) {
      this.getFilterList(this.response, this.filterObj);
    }
    else {
      this.totalPolicies = this.tempList.length;
      this.showViewAll = true;
      this.template.querySelector('c-standard-table-component-hum').computecallback(this.response);
    }
    this.updateFilters();
  }

  /**
   * Update Filters with view all params
   */
  updateFilters() {
    Object.assign(this.filterObj,this.filterObj);
    this.oViewAllParams.filters = this.filterObj;
    if (Object.keys(this.filterObj).length === 0) {
      this.iconName = 'utility:filterList';
    }else{
      this.iconName = 'standard:filter';
    }
  }

  filterByRange(event) {
    var fromDateInput = this.template.querySelector(".birthdate-FromInput");
    var toDateInput = this.template.querySelector(".birthdate-ToInput");
    if (fromDateInput.value && toDateInput.value) { 
	this.formatDateForFilter(fromDateInput, toDateInput, 'manual'); 
	}
  }

/* Open External DCN link page 
  *
  */
handleclicked(event){
  const config = {
      type: 'standard__webPage',
      attributes: {
          url: event.detail.payLoad
      }
  };
  this[NavigationMixin.Navigate](config);
}

createNewCase(event) {
  openLWCSubtab('caseInformationComponentHum',this.recordId,{label:'New Case',icon:'standard:case'});
}

openArchivedCases(event) {
  openLWCSubtab('archivedCaseSearchHum',this.recordId,{label:'Archived Case History',icon:'standard:account'});
}
}