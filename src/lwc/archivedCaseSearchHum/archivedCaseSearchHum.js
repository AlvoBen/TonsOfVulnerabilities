/*******************************************************************************************************************************
LWC JS Name : archivedCaseHistoryHum.js
Function    : This JS serves as controller to accountDetailCaseHistoryHum.html. 

Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
Isha/Ankima                     07/12/2022                     Original version
Isha/Ankima                     11/25/2022                     Defect Fix
*********************************************************************************************************************************/
import { LightningElement, track, api, wire } from "lwc";
import fetchLabels from "@salesforce/apex/ArchivedCaseHistory_LC_HUM.fetchLabels";
import getArchivedCaseDataResponse from "@salesforce/apexContinuation/ArchivedCaseHistory_LC_HUM.getArchivedCaseDataResponse";

import {
  getDateDiffInDays,
  getUserGroup,
  getPickListValues,
  getLocaleDate,
  getFilterData,
  getPillFilterValues,
  getFinalFilterList,
  getLabels,
  hcConstants,
  getFormattedDate
} from "c/crmUtilityHum";
import { getArchivalSearchLayout } from "./layoutConfig";
import customcss from "@salesforce/resourceUrl/LightningCRMAssets_SR_HUM";
import { loadStyle } from "lightning/platformResourceLoader";
import { getRecord } from "lightning/uiRecordApi";
import { NavigationMixin } from "lightning/navigation";
import { openLWCSubtab } from 'c/workSpaceUtilityComponentHum';
import SystemModstamp from "@salesforce/schema/Account.SystemModstamp";

const flds = [];

export default class archivedCaseSearchHum extends NavigationMixin(LightningElement) {
  @api isClassic=false;
  @track isArchived=true;
  @track response = [];
  @track oData = [];
  @track resultsTrue ;
  @track totalPolicies;
  @track serveresult = [];
  @api recordId='';
  @api bInfiniteScroll;
  @api showViewAll = false;
  @track tempList;
  @track filterFldValues = {};
  @track filterObj = {};
  @track pillFilterValues = [];
  @track tmp = [];
  @track labels = getLabels();
  @track initialresponse = [];
  @track isHidden = "slds-hidden slds-is-absolute";
  @track showDateClass =
    "slds-popover slds-popover_small slds-popover_prompt slds-is-absolute ch-custom-date-popover";
  @track bShowDatePicker = this.isHidden;
  @track toDate;
  @track fromDate;
  @track filterObjKeys = [];
  @track responseToSend;
  @track isProvider = false;
  @track keyword;
  @track oArchivedModel;
  @track subTabDetails;
  @api oAppliedFilters;
  @track oViewAllParams = {};
  @track isGroupCaseHistory = false;
  @track iconName = "utility:filterList";
  @track hasRendered = true;
  @track constants = hcConstants;
  @track ButtonVals=[];
  @track showExtraCases;
  @track totalCasesCount;
  @track disableGoButton;
  @track disableCaseGoButton = true;
  @track inputVal = '';

  @api viewAllArchivedData=false;
  @api sStartCount;
  @api sEndCount;
  @api paramVF;
  @api encodedData;

  
  @track varObj={
    viewButtonWidth : 'width: 114px',
    startCount: 1,
    showOutOfRangeError : false,
    errorMessageVal : '',
    viewButtonCss: 'cursor:pointer',
    widthval : 'width:279%',
    buttonCss : 'display:flex;height: fit-content;padding-top: 10px; width:30%;margin:5px;',
    showButtons : false,
    caseNum:'',
    caseNumberSearch : false,
    showCaseNotFoundMsg : false,
    isGeneral:true,
    caseNumberErrorMsg:'',
    archivalScreen:'',
    infoMsg:'',
    charCount:'',
    displayViewAllMsg : false
  };
  
  handleSubtabOpen(event)
  {
    let customPayload = event.detail;
    if(this.isClassic)
    { 
      this.dispatchEvent(new CustomEvent('openarchivedcasedetail' , { detail : customPayload,  bubbles: true,
        composed: true }));
    }
    else {
      openLWCSubtab('archivedCaseDetailContainerHum',customPayload.recordId,{label:customPayload.tabName,icon:'standard:case'});
    }
  }

  /**
   * Applies pre-selected filters to subtab table
   * and CSS from utility commonstyles file
   * after DOM is rendered
   */
  renderedCallback() {
    Promise.all([
      loadStyle(this, customcss + "/CRM_Assets/styles/commonStyles.css")
    ]).catch((error) => {});
  }
  
  connectedCallback() {
    if(this.recordId=='' || this.recordId==undefined || this.recordId==null){
      this.recordId=this.encodedData;
    }
    if(this.paramVF){
      let Vfdata = JSON.parse(this.paramVF);
      this.viewAllArchivedData =  false; 
      this.sStartCount=Vfdata.sStartCount;
      this.sEndCount=Vfdata.sEndCount;
    }
    this.fetchCustomSettingLabels();
  }
  /*Case Detail */
  openArchivalDetailFun(event){
    openLWCSubtab('archivedCaseDetailContainerHum',event.recordId,{label:event.tabName,icon:'standard:case'});
  }
  

  fetchCaseData() { 
    const me = this;
    me.showViewAll = this.bInfiniteScroll === undefined;
    this.oArchivedModel = getArchivalSearchLayout();
    this.fetchServiceResult('yes');
    this.datePickerFocusOutListener = function (evnt) {
      const bDateContainer =
        evnt.toElement &&
        evnt.toElement.classList &&
        evnt.toElement.classList.contains("ch-custom-date-popover");
      if (bDateContainer === false && me.bShowDatePicker === me.showDateClass) {
        me.bShowDatePicker = me.isHidden;
        me.filterByRange();
      }
    };
    document.addEventListener("click", this.datePickerFocusOutListener, true);
  }

  /**
   * Remove event listeners on disconnect
   */
  disconnectedCallback() {
    document.removeEventListener("click", this.datePickerFocusOutListener);
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
  /**
    * show top page error msge
    * @param {*} isValid - true means there is no any kind of error present on page after verify all validations criteira
    *  @param {*} headerMsg - the top header error msge
    */
  showPageMsg(isValid, headerMsg) {
    const errHeader = this.template.querySelector(".page-error-message");
    if (!isValid) {
        errHeader.innerHTML =
            '<div class="slds-m-horizontal_small slds-p-around_small hc-error-header" style="color:white; font-size:20px; background: #c23934;border-radius:0.3rem">Review the error on this page.</div>' +
            '<p class="slds-p-horizontal_large slds-p-vertical_small" style="color:#c23934; font-size: 13px;">' +
            headerMsg +
            "</p>";
    } else {
        errHeader.innerHTML = " ";
    }
} 

  
fetchCustomSettingLabels(){
  fetchLabels()
  .then((result)=>{
    this.map = result;
    this.varObj.defaultStartDate=this.map?.['HUMArchival_START_DATE_DIFF'];
    this.varObj.defaultEndDate =this.map?.['HUMArchival_END_DATE_DIFF'];
    this.varObj.initToDate =this.subtractMonths(this.map?.['HUMArchival_END_DATE_DIFF'],new Date(),'to'); //html
    this.varObj.initFromDate =this.subtractMonths(this.map?.['HUMArchival_START_DATE_DIFF'], new Date(this.varObj.initToDate),'from'); //html
    this.varObj.oldDateLimit =this.map?.['HUMArchival_OLD_DATE_LIMIT']; //html
    this.varObj.endCount=this.map?.['HUMArchival_ARCHIVALSEARCH_MIN_REC'];
    this.varObj.totalcases=this.map?.['HUMArchival_ARCHIVALSEARCH_MIN_REC']; //html
    this.varObj.lastIndex=this.map?.['HUMArchival_ARCHIVALSEARCH_MIN_REC']; //html
    this.varObj.archivalScreen=this.map?.['HUMArchival_searchScreenName']; //html
    this.varObj.infoMsg=this.map?.['HUMArchival_infoMsg']; //html
	  this.fetchCaseData();
  });
  
}
  fetchServiceResult(indicator){
     let responseReqVal;
     let sendEndCount;
     let fromDateVar;
     let toDateVar
     if(this.varObj.defaultStartDate==this.map?.['HUMArchival_START_DATE_DIFF']){
      fromDateVar = this.varObj.caseNumberSearch === true?'':this.varObj.initFromDate;
     }else{
      fromDateVar = this.varObj.caseNumberSearch === true?'':this.varObj.defaultStartDate;
     }
     if(this.varObj.defaultEndDate ==this.map?.['HUMArchival_END_DATE_DIFF']){
      toDateVar = this.varObj.caseNumberSearch === true?'':this.varObj.initToDate;
     }else{
      toDateVar = this.varObj.caseNumberSearch === true?'':this.varObj.defaultEndDate;
     }
     let startRowVar = this.varObj.caseNumberSearch === true?parseInt(this.map?.['HUMArchival_NO_RECORDS']):this.varObj.startCount;
     let endRowVar = this.varObj.caseNumberSearch === true?parseInt(this.map?.['HUMArchival_NO_RECORDS']):this.varObj.endCount;
     let caseNumberVar = this.varObj.caseNumberSearch === true?this.varObj.caseNum:'';
     if(this.viewAllArchivedData===true){
     this.varObj.showButtons=false;
      endRowVar=this.sEndCount;
      startRowVar=this.sStartCount;
     }else{
        sendEndCount = (parseInt(this.varObj.startCount)+(parseInt(this.map?.['HUMArchival_ARCHIVALSEARCH_MIN_REC'])-1));
        endRowVar=sendEndCount;
        startRowVar=this.varObj.startCount;
        
          if(this.varObj.caseNumberSearch===true){
            
            startRowVar = this.varObj.startCount = parseInt(this.map?.['HUMArchival_NO_RECORDS']);
            endRowVar = this.varObj.endCount = parseInt(this.map?.['HUMArchival_NO_RECORDS']); 
       }
     }
     getArchivedCaseDataResponse({ recId:this.recordId,fromDate: fromDateVar,toDate:toDateVar,startRow:startRowVar,endRow:endRowVar,isArchived:true,caseNumber:caseNumberVar}).then((result)=>{
      this.varObj.displayViewAllMsg=false;
      if(result.sError===true){
      this.showPageMsg(false, result.sErrorMsg);
      this.resultsTrue=false;
      this.disableGoButton=false;
      this.varObj.totalcases = 0;
      }else{
        const dataset =result;
        if(this.varObj.caseNumberSearch === false)
        {
          this.totalCasesCount = dataset.CaseSearchResponse.Header.sTotalRows;
          if((this.totalCasesCount <= parseInt(this.map?.['HUMArchival_ARCHIVALSEARCH_MIN_REC']) || this.totalCasesCount >= parseInt(this.map?.['HUMArchival_ARCHIVALSEARCH_MIN_REC']))  && (dataset.CaseSearchResponse.CaseResults).length == parseInt(this.map?.['HUMArchival_NO_RECORDS'])){
            this.varObj.lastIndex = parseInt(this.map?.['HUMArchival_NO_RECORDS']);
            this.varObj.startCount=parseInt(this.map?.['HUMArchival_NO_RECORDS']);
            this.varObj.totalcases=parseInt(this.map?.['HUMArchival_NO_RECORDS']);
            this.varObj.showButtons = false;
          }else if(this.totalCasesCount <= parseInt(this.map?.['HUMArchival_ARCHIVALSEARCH_MIN_REC'])  && (dataset.CaseSearchResponse.CaseResults).length > parseInt(this.map?.['HUMArchival_NO_RECORDS']) ){
            if(indicator=='yes'){
              this.varObj.lastIndex = this.totalCasesCount;
              this.varObj.startCount=1;
              this.varObj.totalcases=this.totalCasesCount;
              this.varObj.showButtons = false; 
              this.ButtonVals = []; 
              this.processNumberOfButtons();
            }
          }else if(this.totalCasesCount > parseInt(this.map?.['HUMArchival_ARCHIVALSEARCH_MIN_REC'])  && (dataset.CaseSearchResponse.CaseResults).length > parseInt(this.map?.['HUMArchival_NO_RECORDS']) ) {
            this.varObj.displayViewAllMsg=true;
            if(indicator=='yes'){
              
              this.varObj.totalcases=parseInt(this.map?.['HUMArchival_ARCHIVALSEARCH_MIN_REC'])+'+'; 
              this.varObj.lastIndex = parseInt(this.map?.['HUMArchival_ARCHIVALSEARCH_MIN_REC']);
              this.varObj.startCount=1;
              this.ButtonVals = [];
              this.varObj.showButtons = true; 
              this.processNumberOfButtons();
            } 
          }
          if(dataset.CaseSearchResponse.Header.sOldestCaseDate != '' && dataset.CaseSearchResponse.Header.sOldestCaseDate != undefined)
          { 
            this.showExtraCases = true;
          }else{
            this.showExtraCases = false;
          }
        }else if(this.varObj.caseNumberSearch === true){
          this.ButtonVals = [];
          this.showExtraCases = false;
          if((dataset.CaseSearchResponse.CaseResults).length > parseInt(this.map?.['HUMArchival_NO_RECORDS'])){
            this.varObj.showCaseNotFoundMsg = false;
            this.varObj.totalcases = 1;
            this.totalCasesCount = 1;
            this.varObj.startCount = 1;
            this.varObj.lastIndex = 1;
            this.varObj.showButtons = false; 
            this.ButtonVals = []; 
            this.processNumberOfButtons(); 
          }else if((dataset.CaseSearchResponse.CaseResults).length === parseInt(this.map?.['HUMArchival_NO_RECORDS'])){
            this.varObj.totalcases = parseInt(this.map?.['HUMArchival_NO_RECORDS']);
            this.totalCasesCount = parseInt(this.map?.['HUMArchival_NO_RECORDS']);
            this.varObj.startCount = parseInt(this.map?.['HUMArchival_NO_RECORDS']);
            this.varObj.lastIndex = parseInt(this.map?.['HUMArchival_NO_RECORDS']);
            this.varObj.showCaseNotFoundMsg = true;
            this.varObj.caseNumberErrorMsg='No Cases found for the Case Number';
          }
        }
        result = JSON.stringify(dataset.CaseSearchResponse.CaseResults);
        if((dataset.CaseSearchResponse.CaseResults).length == parseInt(this.map?.['HUMArchival_NO_RECORDS']) ){
          this.resultsTrue = false;
          this.disableGoButton=false;
          if(this.inputVal != ''){
            this.disableCaseGoButton = false;
            }else{
              this.disableCaseGoButton = true;
            }
          this.varObj.totalcases = parseInt(this.map?.['HUMArchival_NO_RECORDS']);
        }else{
          this.serveresult = dataset.CaseSearchResponse.CaseResults;
          this.initialresponse = this.serveresult;
          this.tempList = this.serveresult;
          if (this.serveresult && this.serveresult.length > parseInt(this.map?.['HUMArchival_NO_RECORDS'])) {
            this.filterFldValues = getPickListValues(
              [
                "sClassification",
                "sIntent",
                "sStatus",
                "sProduct",
                "sOrigin",
                "sInteractingWithType",
                "sOwnerQueue",
                "sDCN"
              ],
              this.serveresult
            );
          }
          this.processResponse(JSON.parse(result));
          this.disableGoButton=false;
          if(this.inputVal != ''){
            this.disableCaseGoButton = false;
            }else{
              this.disableCaseGoButton = true;
            }
          this.handlePreselectedFilters();
        } 
      }
      })
      .catch((error) => {
        this.disableGoButton=false;
        if(this.inputVal != ''){
          this.disableCaseGoButton = false;
          }else{
            this.disableCaseGoButton = true;
          }
        console.log("Error Occured", error);
      });
    
  
  }
  processNumberOfButtons(){
    const count = this.totalCasesCount;
    var endConst ;
    var numOfButtons='1';
    if(count>parseInt(this.map?.['HUMArchival_ARCHIVALSEARCH_MAX_REC'])){
      this.varObj.buttonCss = 'display:flex;height: fit-content;padding-top: 10px; width:30%;overflow-x: scroll;margin:5px;';
      this.varObj.widthval  = 'width:128%';
      numOfButtons= Math.ceil(count/parseInt(this.map?.['HUMArchival_ARCHIVALSEARCH_MAX_REC']));
      endConst = parseInt(parseInt(this.map?.['HUMArchival_ARCHIVALSEARCH_MAX_REC']));
      this.varObj.endCount=parseInt(this.map?.['HUMArchival_ARCHIVALSEARCH_MAX_REC']);
    }else{
      endConst = parseInt(this.totalCasesCount);
      this.varObj.endCount=this.totalCasesCount;
      this.varObj.buttonCss = 'display:flex;height: fit-content;padding-top: 10px; width:30%;margin:5px;';
      this.varObj.widthval  = 'width:279%';
    }
    var digit = parseInt(this.map?.['HUMArchival_NO_RECORDS']);
    var startConst = parseInt(1);
    var startCnt;
    var lastCnt
    for(var i=0;i<=numOfButtons-1;i++){
         startCnt =digit+startConst;
         lastCnt = digit+endConst;
        if(i==0){
          this.ButtonVals.push({value:'View '+startCnt+ '-'  +lastCnt, disabled:true, buttonWidth:this.varObj.viewButtonWidth});
          let oParams = {sRecordId:this.recordId, sStartCount:this.varObj.startCount , sEndCount : this.varObj.endCount, viewAllData : true,isClassic: this.isClassic}
          let oUserGroup = getUserGroup();
          this.oViewAllParams = {
            sOptions: {
              sAppName: this.map?.['HUMArchival_appName'],
              sArchivalScreen: this.map?.['HUMArchival_screenName'],
              bEventEnabler: true,
              sEventName: 'OpenArchivalDetail',
              isClassic: this.isClassic
            },
            sArchivalMinRecords: parseInt(this.map?.['HUMArchival_ARCHIVALSEARCH_MIN_REC']),
            sArchivedCaseSearch: true,
            sStartCount: startCnt,
            sEndCount: lastCnt,
            nameOfScreen : this.map?.['HUMArchival_nameOfScreenSearch'],
            oParams : oParams,
            sRecordTypeName: 'MemberCase',
            sRecordId: this.recordId,
            viewAllData: true,
            oUserGroup
          };
        }else{
          if(startCnt < 500){
            this.varObj.viewButtonWidth = 'width:114px';
          }
          if(startCnt >= 500 && startCnt<1000){
            this.varObj.viewButtonWidth = 'width:134px';
          }
          if(startCnt >= 1000 && startCnt<10000){
            this.varObj.viewButtonWidth = 'width:141px';
          }
          if(startCnt >= 10000 && lastCnt < 100000){
            this.varObj.viewButtonWidth = 'width:158px';
          }
          if(startCnt >= 100000){
            this.varObj.viewButtonWidth = 'width:168px';
          }
          this.ButtonVals.push({value:'View '+startCnt+ '-'  +lastCnt, disabled:false, buttonWidth:this.varObj.viewButtonWidth});
        }
	      digit = digit+endConst;
	      if ((digit+parseInt(this.map?.['HUMArchival_ARCHIVALSEARCH_MAX_REC']))>count){
		      endConst = count-digit;
	      }
    }
  }

  setCount(event){
    let val = event.target.value;
    this.varObj.endCount = val.split("-")[1];
    this.varObj.startCount = val.split("-")[0];
    this.varObj.startCount = this.varObj.startCount.split(" ")[1];
    var diff = parseInt(this.varObj.endCount)-parseInt(this.varObj.startCount);
    if(diff >= parseInt(this.map?.['HUMArchival_ARCHIVALSEARCH_MIN_REC'])){
    this.varObj.lastIndex = parseInt(this.varObj.startCount) + (parseInt(this.map?.['HUMArchival_ARCHIVALSEARCH_MIN_REC'])-1);
    this.varObj.totalcases = parseInt(this.map?.['HUMArchival_ARCHIVALSEARCH_MIN_REC'])+'+';
    }else{
      this.varObj.lastIndex = parseInt(this.varObj.startCount) + parseInt(diff);
      this.varObj.totalcases = diff+1;
    }
    this.ButtonVals.forEach(bt => { 
    if(bt.value == event.target.value){
      bt.disabled=true;
    }else{
      bt.disabled=false;
    }
    let oUserGroup = getUserGroup();
      let oParams = {sRecordId:this.recordId, sStartCount:this.varObj.startCount ,  isClassic: this.isClassic,sEndCount : this.varObj.endCount, viewAllData : true };
      this.oViewAllParams = {
        sOptions: {
          sAppName: 'ArchivalCaseHistory',
          sArchivalScreen: 'ArchivalSearch',
          bEventEnabler: true,
          sEventName: 'OpenArchivalDetail',
          isClassic: this.isClassic
        },
        sArchivalMinRecords: parseInt(this.map?.['HUMArchival_ARCHIVALSEARCH_MIN_REC']),
        sArchivedCaseSearch: true,
        nameOfScreen : 'Archived Case',
        oParams : oParams,
        sStartCount: this.varObj.startCount,
        sEndCount: this.varObj.endCount,
        sRecordTypeName: 'MemberCase',
        sRecordId: this.recordId,
        viewAllData: true,
        oUserGroup
      };
    });
    this.fetchServiceResult('no');
  }

  getCaseDataFromAPI() {
    this.varObj.showCaseNotFoundMsg=false;
    this.varObj.caseNum = (this.template.querySelector(`[data-id="refField"]`).value);
    const enteredCaseNum = this.varObj.caseNum;
    if(this.varObj.caseNum === '' || (/^[0-9]+$/.test(enteredCaseNum)===false) || enteredCaseNum.length!=13){
      this.varObj.showCaseNotFoundMsg=true;
      this.varObj.caseNumberErrorMsg='No Cases found for the Case Number';
    }else {
    this.varObj.caseNumberSearch= true;
    this.varObj.showCaseNotFoundMsg=false;
    this.varObj.caseNumberErrorMsg='';
    this.varObj.buttonCss = 'display:flex;height: fit-content;padding-top: 10px; width:30%;margin:5px;';
    this.disableGoButton=true;
    this.disableCaseGoButton=true;
    this.fetchServiceResult('no');
    }
  }

  processResponse(result) {
    const me = this;
    let dtToday = getLocaleDate(new Date());
    me.response = result.map((item) => {
      return {
        ...item,
        isLink: false,
        dcnLink: item.sDCN === "Yes",
        caseLink: true,
        sCaseNum: item.sCaseNum + "#&;" + item.Id,
        sDCN: item.sDCN === "Yes" ? "Yes" + "#&;" + item.sdcnURL : "No",
        isOpenedInTwoWeeks: getDateDiffInDays(dtToday, item.sCreatedDate) < 14
      };
    });
    if (!me.oAppliedFilters || Object.keys(me.oAppliedFilters).length === 0) {
      me.oData = [...me.response];
      me.setTotalRecords(me.oData);
    } else {
      me.iconName = "standard:filter";
    }
  }

  /**
   * Applies pre-selected filters to subtab table
   * after DOM is rendered
   */
  handlePreselectedFilters() {
    if (this.oAppliedFilters) {
      this.filterObj = JSON.parse(JSON.stringify(this.oAppliedFilters));
      if (this.oAppliedFilters.hasOwnProperty("searchByWord")) {
        this.findByWord(undefined, this.oAppliedFilters["searchByWord"]);
      }
      if (this.oAppliedFilters.hasOwnProperty("sCreatedDate")) {
        this.template.querySelector(".birthdate-FromInput").value =
          this.oAppliedFilters["sCreatedDate"][0];
        this.template.querySelector(".birthdate-ToInput").value =
          this.oAppliedFilters["sCreatedDate"][1];
        this.formatDateForFilter(
          this.oAppliedFilters["sCreatedDate"][0],
          this.oAppliedFilters["sCreatedDate"][1],
          "preSelect"
        );
      }
      if (
        this.oAppliedFilters.hasOwnProperty("sClassification") ||
        this.oAppliedFilters.hasOwnProperty("sIntent") ||
        this.oAppliedFilters.hasOwnProperty("sStatus")
      ) {
        this.selectFilter();
      }
    }
  }

  setTotalRecords(oData) {
    this.totalPolicies = Object.values(oData).length;
    this.resultsTrue = true;
  }

  getFormattedDate(filterValue) {
    var oDate = filterValue.split("-");
    return oDate[1] + "/" + oDate[2] + "/" + oDate[0];
  }

  selectFilter(event) {
    const me = this;
    if (event) {
      var filterName = event.target.name;
      var filterValue = event.detail.value;
      if (filterName == "sCreatedDate") {
        filterValue = this.getFormattedDate(filterValue);
      }
      if (filterName === "clear") {
        this.clearData();
        return;
      }
      this.filterObj = getFilterData(filterName, filterValue, this.filterObj);
      if (
        this.pillFilterValues &&
        !this.pillFilterValues.some(
          (arg) => arg["key"] == filterName && arg["value"] == filterValue
        )
      ) {
        this.pillFilterValues = getPillFilterValues(
          filterName,
          filterValue,
          this.pillFilterValues
        );
      }
    } else {
      let tempObj = JSON.parse(JSON.stringify(this.filterObj));
      let filterSelectors =
        this.template.querySelectorAll("lightning-combobox");
      Object.keys(tempObj).forEach(function (key) {
        filterSelectors.forEach((inp) => {
          if (inp.name === key) {
            if (Array.isArray(tempObj[key])) {
              tempObj[key].forEach((val) => {
                inp.value = val;
              });
            } else {
              inp.value = tempObj[key];
            }
          }
        });
        if (Array.isArray(tempObj[key])) {
          tempObj[key].forEach((val) => {
            if (key !== "searchByWord" && key !== "sCreatedDate") {
              me.pillFilterValues = getPillFilterValues(
                key,
                val,
                me.pillFilterValues
              ); 
            }
          });
        } else {
          me.pillFilterValues = getPillFilterValues(
            key,
            tempObj[key],
            me.pillFilterValues
          );
        }
      });
    }
    let tempFilters = this.filterObj;
    this.getFilterList(this.response, tempFilters);
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
      me.template.querySelector(
        "c-standard-table-component-hum"
      ).noDataMessage = 'No matching records found';
    }
    me.oData = me.serveresult;
    me.setTotalRecords(me.oData);
  }

  handleRemove(event) {
    this.tmp = [];
    const dataToGet = event.detail.data;
    const pillValues = event.detail.pillList;
    if (dataToGet === "noData") {
      delete this.filterObj["sClassification"];
      delete this.filterObj["sIntent"];
      delete this.filterObj["sStatus"];
      delete this.filterObj["sOrigin"];
      delete this.filterObj["sProduct"];
      delete this.filterObj["sInteractingWithType"];
      delete this.filterObj["sOwnerQueue"];
      delete this.filterObj["sDCN"];
      this.serveresult = [];
      if (Object.keys(this.filterObj).length > 0) {
        this.getFilterList(this.response, this.filterObj);
      } else {
        this.filterObj = {};
        this.template
          .querySelector("c-standard-table-component-hum")
          .computecallback(this.response);
        this.totalPolicies = this.tempList.length;
      }
    } else {
      this.filterObj = dataToGet;
      this.getFilterList(this.response, dataToGet);
    }

    this.pillFilterValues = pillValues;
    let fields = this.template.querySelectorAll("lightning-combobox");
    fields.forEach(function (item) {
      item.value = "";
    });
    this.updateFilters();
  }

  clearData() {
    this.serveresult = [];
    this.pillFilterValues = [];
    this.filterObj = {};
    this.tmp = [];
    this.clearInputFields();
    this.totalPolicies = this.tempList.length;
    this.serveresult = this.tempList;
    this.template
      .querySelector("c-standard-table-component-hum")
      .computecallback(this.response);
    this.bShowDatePicker = this.isHidden;
    this.fromDate = "";
    this.toDate = "";
    this.updateFilters();
  }

  /**
   * clear Input field selection
   */
  clearInputFields() {
    let fields = this.template.querySelectorAll(".resultFilter");
    fields.forEach(function (item) {
      item.value = "";
    });
  }

  findByWord(event, wordFilterValue) {
    if(this.response.length != 0 && this.response != undefined){
    let count = 1;
    const me = this;
    let value;
    if (event) {
      let element = event.target;
      value = element.value;
    } else {
      value = wordFilterValue[0];
      this.template.querySelector(".inputfield").value = value;
    }

    if (value.length > hcConstants.MIN_SEARCH_CHAR) {
      this.filterObj["searchByWord"] = [value];
    } else {
      if (this.filterObj.hasOwnProperty("searchByWord")) {
        delete this.filterObj["searchByWord"];
        count = parseInt(this.map?.['HUMArchival_NO_RECORDS']);
      }
    }
    if (Object.keys(this.filterObj).length > parseInt(this.map?.['HUMArchival_NO_RECORDS'])) {
      this.getFilterList(this.response, this.filterObj);
    } else {
      if (count == parseInt(this.map?.['HUMArchival_NO_RECORDS'])) {
        this.showViewAll = true;
        this.template
          .querySelector("c-standard-table-component-hum")
          .computecallback(this.response);
      }
      this.totalPolicies = this.tempList.length;
    }
    this.updateFilters();
  }
  }

  datePickerShow(event) {
    if (this.bShowDatePicker === this.showDateClass) {
      this.bShowDatePicker = this.isHidden;
      this.filterByRange(event);
    } else {
      this.bShowDatePicker = this.showDateClass;
      if (Array.isArray(this.filterObj["sCreatedDate"])) {
        this.template.querySelector(".birthdate-FromInput").value =
          this.filterObj["sCreatedDate"][0];
        this.template.querySelector(".birthdate-ToInput").value =
          this.filterObj["sCreatedDate"][1];
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
    let dateInputF;
    let dateInputT;
    if ((from || to) && conditionDate === "manual") {
      dateInputF = from.value.split("/");
      dateInputT = to.value.split("/");
    } else if (conditionDate === "preSelect") {
      dateInputF = from.split("/");
      dateInputT = to.split("/");
    }
    if (dateInputF || dateInputT) {
      var fInputDate = new Date(
        dateInputF[2],
        dateInputF[0] - 1,
        dateInputF[1],
        0,
        0,
        0,
        0
      );
      var tInputDate = new Date(
        dateInputT[2],
        dateInputT[0] - 1,
        dateInputT[1],
        0,
        0,
        0,
        0
      );

      if (fInputDate && tInputDate) {
        const fInputDatemod =
          fInputDate.getMonth() +
          1 +
          "/" +
          (fInputDate.getDate() > 9
            ? fInputDate.getDate()
            : "0" + fInputDate.getDate()) +
          "/" +
          fInputDate.getFullYear();
        const tInputDatemod =
          tInputDate.getMonth() +
          1 +
          "/" +
          (tInputDate.getDate() > 9
            ? tInputDate.getDate()
            : "0" + tInputDate.getDate()) +
          "/" +
          tInputDate.getFullYear();

        this.filterObj = {
          ...this.filterObj,
          sCreatedDate: [fInputDatemod, tInputDatemod]
        };
      } else {
        delete this.filterObj["sCreatedDate"];
      }
    }
    if (Object.keys(this.filterObj).length > 0) {
      this.getFilterList(this.response, this.filterObj);
    } else {
      this.totalPolicies = this.tempList.length;
      this.showViewAll = true;
      this.template
        .querySelector("c-standard-table-component-hum")
        .computecallback(this.response);
    }
    this.updateFilters();
  }

  /**
   * Update Filters with view all params
   */
  updateFilters() {
    Object.assign(this.filterObj, this.filterObj);
    this.oViewAllParams.filters = this.filterObj;
    if (Object.keys(this.filterObj).length === 0) {
      this.iconName = "utility:filterList";
    } else {
      this.iconName = "standard:filter";
    }
  }

  filterByRange(event) {
    var fromDateInput = this.template.querySelector(".birthdate-FromInput");
    var toDateInput = this.template.querySelector(".birthdate-ToInput");
    if (fromDateInput.value && toDateInput.value) {
      this.formatDateForFilter(fromDateInput, toDateInput, "manual");
    }
  }

  /* Open External DCN link page
   *
   */
  handleclicked(event) {
    const config = {
      type: "standard__webPage",
      attributes: {
        url: event.detail.payLoad
      }
    };
    this[NavigationMixin.Navigate](config);
  }

  /**
   * Description - this method will fire when user click on clear button for Date fields
   */
  clearDateValues() {
      let dateFieldsValues = this.template.querySelectorAll('.dateVal');
      dateFieldsValues[0].value = '';
      dateFieldsValues[1].value = '';
      this.varObj.errorMessageVal = '';
      this.varObj.showOutOfRangeError = false;
  }

 
  subtractMonths(numOfMonths,dateVar,dateParam) {
    dateVar.setMonth(dateVar.getMonth() - numOfMonths);
    if(dateParam === 'from'){
      dateVar.setDate(dateVar.getDate() + 1);
    }
    var dd = String(dateVar.getDate()).padStart(2, '0');
    var mm =  String(dateVar.getMonth() + 1).padStart(2, '0') ;//January is 0!
   var yyyy = dateVar.getFullYear();
    var dateToReturn = mm + '/' + dd + '/' + yyyy; 
    return dateToReturn;
  }
  /**
   * @param - event
   * Description - this method will fire when user click outside of From date field
   */
  validateFromDate(event) {
    if( event.target.value === ''){
      this.varObj.errorMessageVal = 'Please select a date range between 01/01/2014 and '+this.subtractMonths(this.map?.['HUMArchival_END_DATE_DIFF'],new Date(),'to');
      this.varObj.showOutOfRangeError=true;
    }else{
      let fromDateYear = (event.target.value).split('/');
      if( fromDateYear[2] < 2014  ){
      this.varObj.defaultStartDate = event.target.value;
       this.varObj.errorMessageVal = 'Please select a date range between 01/01/2014 and '+this.subtractMonths(this.map?.['HUMArchival_END_DATE_DIFF'],new Date(),'to');
        this.varObj.showOutOfRangeError=true;
      }else{
        this.varObj.errorMessageVal = '';
        this.finalFromDate = event.target.value;
        this.varObj.showOutOfRangeError=false;
        this.varObj.defaultStartDate = event.target.value;
      }
       }
      }

  validateToDate(event){
    if( event.target.value === '' ){
      this.varObj.errorMessageVal = ' Please select a date range between 01/01/2014 and '+this.subtractMonths(this.map?.['HUMArchival_END_DATE_DIFF'],new Date(),'to');
      this.varObj.showOutOfRangeError=true;
    }else{
    var toDate = new Date(event.target.value);
    var fromDate = new Date(this.varObj.defaultStartDate);
    let fromDateYear = (this.varObj.defaultStartDate).split('/');

    if(toDate < fromDate){
      this.varObj.errorMessageVal = 'To Date cannot be less than From Date';
      this.varObj.showOutOfRangeError=true;
    }else if(toDate > new Date(this.subtractMonths(this.map?.['HUMArchival_END_DATE_DIFF'],new Date(),'to')) ||(fromDateYear[2] < 2014)){
      this.varObj.errorMessageVal = ' Please select a date range between 01/01/2014 and '+this.subtractMonths(this.map?.['HUMArchival_END_DATE_DIFF'],new Date(),'to');
      this.varObj.showOutOfRangeError=true;
    }
    else{
      this.varObj.defaultEndDate = event.target.value;
      this.varObj.errorMessageVal = '';
      this.varObj.showOutOfRangeError=false;
    }
  }
  }

  clearAllFilters(){
      this.template.querySelector(`[data-id="refField"]`).value = '';
      delete this.filterObj["sClassification"];
      delete this.filterObj["sIntent"];
      delete this.filterObj["sStatus"];
      delete this.filterObj["sOrigin"];
      delete this.filterObj["sProduct"];
      delete this.filterObj["sInteractingWithType"];
      delete this.filterObj["sOwnerQueue"];
      delete this.filterObj["sDCN"];
      this.pillFilterValues = [];
      let fields = this.template.querySelectorAll("lightning-combobox");
      fields.forEach(function (item) {
      item.value = "";
      });
      this.updateFilters();
  }

  fetchResultOnDateValues(){
    this.clearAllFilters();
    this.varObj.caseNumberSearch = false;
    this.inputVal = '';
    this.disableCaseGoButton = true;
    this.varObj.showCaseNotFoundMsg = false;
    let fromDateArr;
    let toDateArr;
    let frmDate;
    let toDate;
    let fromValue = this.template.querySelector(`[data-id="fromField"]`).value;
    let toValue = this.template.querySelector(`[data-id="toField"]`).value;
    let alphabetonly = /[^a-zA-Z]/;
    let alphabetnumberic = /(?=.*\d)(?=.*[a-zA-Z])/;
    if(this.template.querySelector(`[data-id="fromField"]`).value === '' || this.template.querySelector(`[data-id="toField"]`).value === '' || !alphabetonly.test(fromValue) || !alphabetonly.test(toValue) || alphabetnumberic.test(toValue) || alphabetnumberic.test(fromValue)){
      this.varObj.errorMessageVal = 'Please select a date range between 01/01/2014 and '+this.subtractMonths(this.map?.['HUMArchival_END_DATE_DIFF'],new Date(),'to');
      this.varObj.showOutOfRangeError=true;
    }else{
      fromDateArr = (this.template.querySelector(`[data-id="fromField"]`).value).split('/');
      toDateArr = (this.template.querySelector(`[data-id="toField"]`).value).split('/');
      frmDate = new Date(this.template.querySelector(`[data-id="fromField"]`).value);
      toDate = new Date(this.template.querySelector(`[data-id="toField"]`).value);
      let fromDateGap = new Date(toDate);
      fromDateGap.setMonth(fromDateGap.getMonth() - 24);
      fromDateGap.setDate(fromDateGap.getDate() + 1);
    if(fromDateArr[2] < 2014 || (fromDateArr[0] < 1 || fromDateArr[0] > 12) || (fromDateArr[1] < 1 || fromDateArr[1] > 31) ){
      this.varObj.errorMessageVal = 'Please select a date range between 01/01/2014 and '+this.subtractMonths(this.map?.['HUMArchival_END_DATE_DIFF'],new Date(),'to');
      this.varObj.showOutOfRangeError=true;
    }else if(toDate < frmDate){
      this.varObj.errorMessageVal = 'To Date cannot be less than From Date';
      this.varObj.showOutOfRangeError=true;
    }else if((toDateArr[0] < 1 || toDateArr[0] > 12) || (toDateArr[1] < 1 || toDateArr[1] > 31)){
      this.varObj.errorMessageVal = 'Please select a date range between 01/01/2014 and '+this.subtractMonths(this.map?.['HUMArchival_END_DATE_DIFF'],new Date(),'to');
      this.varObj.showOutOfRangeError=true;
    }else if(toDate > new Date(this.subtractMonths(this.map?.['HUMArchival_END_DATE_DIFF'],new Date(),'to'))){
      this.varObj.errorMessageVal = ' Please select a date range between 01/01/2014 and '+this.subtractMonths(this.map?.['HUMArchival_END_DATE_DIFF'],new Date(),'to');
      this.varObj.showOutOfRangeError=true;
    }else if ( frmDate > toDate || frmDate < fromDateGap ){
      this.varObj.errorMessageVal = 'Please select a 24 month date range';
      this.varObj.showOutOfRangeError=true;
    }else{
      this.varObj.errorMessageVal = '';
      this.varObj.showOutOfRangeError=false;
      this.disableGoButton=true;
      this.fetchServiceResult('yes');

    }
  }
}

handleChange(event){
  this.inputVal = event.target.value;
  console.log('inpp : '+this.inputVal);
  if(this.inputVal == null || this.inputVal == ''){
    this.disableCaseGoButton = true;
    this.varObj.caseNumberErrorMsg = '';
  }else{
    this.disableCaseGoButton = false;
  }
}

}