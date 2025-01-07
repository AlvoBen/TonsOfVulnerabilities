/*******************************************************************************************************************************
LWC JS Name : toothHistoryLwcHum.JS
Function    : This JS serves as Controller to toothHistoryLwcHum

Modification Log: 
Developer Name           Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
* Suraj Patil                             		         05/08/2023               	US#4542585 Dental Plan - Tooth History
* Aishwarya Pawar                             		         06/01/2023               	US#4609632
*********************************************************************************************************************************/
import { LightningElement, api, track, wire } from 'lwc';
import { MessageContext } from 'lightning/messageService';
import CallToothHistoryService from '@salesforce/apexContinuation/Benefits_LC_HUM.getToothHistory';
import loggingcss from '@salesforce/resourceUrl/LightningCRMAssets_SR_HUM';
import { loadStyle } from 'lightning/platformResourceLoader';
import {
  performTableLogging,
  getLoggingKey,
  checkloggingstatus,
} from "c/loggingUtilityHum";
import HUMNoRecords from "@salesforce/label/c.HUMNoRecords";
import { getFinalFilterList } from 'c/crmUtilityHum';
import { CurrentPageReference } from 'lightning/navigation';
const INITIAL_RECORDS = 6;
const columns = [
  { label: "Date of Service" },
  { label: "Tooth" },
  { label: "Surface Code" },
  { label: "ADA Code" },
  { label: "Description" },
  { label: "Claim Number" },
  { label: "Status" },
];
export default class toothHistoryLwcHum extends LightningElement {
    @api memberPlanId;
    @track direction = 'asc';
    @track ToothHistoryRecords =[];
    @track TotalToothHistoryRecords;
    @track columnsHeaders = {
        'Date of Service': 'sDateOfServiceformatted',
        'Tooth': 'sTooth',
        'Surface Code': 'sSurfaceCode',
        'ADA Code': 'sADACode',
        'Description': 'sDescription',
        'Claim Number': 'sClaimNumber',
        'Status': 'sStatus'
    };
    @track columns = columns;
    @track noData = false;
    currentCount;
    @track sortDirHandler;
    @track loggingkey;
    autoLogging = true;
    overfieldname;
    @api recordid;
    @api memberplanname;
    @track SortIconHide= [];
    @track filterObj = {};
    @track currentlySortedField;
    labels = {
        HUMNoRecords
    };
    @track relatedInputField = [
    {
      label: "Plan Member Id",
      value: this.memberplanname,
    },
    {
      label: "Section",
      value: "Tooth History",
    },
    {
      label: "ADA Code",
      mappingField: "sADACode",
    },
  ];
    
    get records() {
        return this.ToothHistoryRecords;
    }

    @wire(MessageContext)
    messageContext;
    @wire(CurrentPageReference)
  	wiredPageRef(currentPageReference) {
    	this.pageRef = currentPageReference;
    	this.loggingkey = getLoggingKey(this.pageRef).then((result) => {
      	this.loggingkey = result;
    });
    }

	get generateLogId(){
        return Math.random().toString(16).slice(2);
    }
	
   connectedCallback() {
    this.getToothHistorydata();
    getLoggingKey(this.pageRef).then((result) => {
      this.loggingkey = result;
    });
    if (this.relatedInputField[0].value == undefined)
      this.relatedInputField[0].value = this.memberplanname;
  }
getToothHistorydata() {
    try {
            CallToothHistoryService({
                sPlanMemberId : this.recordid
            })
            .then((result) => {
                if (result !== null && result != undefined && result != '') {
                    this.ToothHistoryRecords = result;
                    this.direction = 'asc';
                    this.sortData('sDateOfServiceformatted');
                    this.TotalToothHistoryRecords = this.ToothHistoryRecords;
                    if (this.TotalToothHistoryRecords.length > 0 && this.TotalToothHistoryRecords.length <= INITIAL_RECORDS) {
                        this.ToothHistoryRecords = this.TotalToothHistoryRecords; 
                        this.currentCount = this.ToothHistoryRecords.length;
                    } else {
                        this.ToothHistoryRecords = this.TotalToothHistoryRecords.slice(0, INITIAL_RECORDS);
                        this.currentCount = this.ToothHistoryRecords.length + '+';
                    }
                }
                else{
                    this.noData = true;
                    this.currentCount = '0';
                }
            });
            if(this.noData == false && this.autoLogging){
                getLoggingKey(this.pageRef).then(result =>{
                    this.loggingkey = result;
                });
            }
        } 
         catch (e) {
            this.noData = true;
            console.log('Error: ' + e.error);
        }
    }

    renderedCallback() {
        Promise.all([
          loadStyle(this, loggingcss + "/CRM_Assets/styles/logging.css"),
        ]).catch((error) => {
          console.log("Error Occured", error);
        });
      }

	handleLogging(event) {
        if (this.loggingkey && checkloggingstatus(this.loggingkey)) {
            
            performTableLogging(
        event,
        this.ToothHistoryRecords,
        this.relatedInputField,
        this.columns,
        "Dental Benefits - Tooth History",
        this.pageRef,
        this.createRelatedField(),
        this.loggingkey
      );
        } else {
            getLoggingKey(this.pageRef).then((result) => {
                this.loggingkey = result;
                
                if (this.loggingkey && checkloggingstatus(this.loggingkey)) {
                    performTableLogging(
            event,
            this.ToothHistoryRecords,
            this.relatedInputField,
            this.columns,
            "Dental Benefits - Tooth History",
            this.pageRef,
            this.createRelatedField(),
            this.loggingkey
          );
	     }
            });
        }
    }

    createRelatedField() {
        return [
            {
                label: 'Plan Member Id',
                value: this.memberplanname
            }
        ]
    }

    handleSort(event) {
        if(this.noData == false)
        {
            this.sortData(this.columnsHeaders[event.target.outerText]);
        }
    }

    sortData(fieldname,filtersort) {
        let parseData = JSON.parse(JSON.stringify(this.ToothHistoryRecords));
        let keyValue = (a) => {
            return a[fieldname];
        };
        this.currentlySortedField = fieldname;
        if(filtersort !== true)
        this.direction = this.direction === 'asc' ? 'desc' : 'asc';
        let isReverse = this.direction === 'asc' ? 1 : -1;
        parseData.sort((x, y) => {
            x = keyValue(x) ? keyValue(x) : '';
            y = keyValue(y) ? keyValue(y) : '';
            return isReverse * ((x > y) - (y > x));
        });

        this.SortIconHide.SortIcon1 = false;
        this.SortIconHide.SortIcon2 = false;
        this.SortIconHide.SortIcon3 = false;
        this.SortIconHide.SortIcon4 = false;
        this.SortIconHide.SortIcon5 = false;
        this.SortIconHide.SortIcon6 = false;
        this.SortIconHide.SortIcon7 = false;

        if (fieldname == 'sDateOfServiceformatted') {
            this.sortDirHandler = this.direction === 'asc' ? 'utility:arrowup' : 'utility:arrowdown';
            this.SortIconHide.SortIcon1 = true;  
        }
        if (fieldname == 'sTooth') {
            this.sortDirHandler = this.direction === 'asc' ? 'utility:arrowup' : 'utility:arrowdown';
            this.SortIconHide.SortIcon2 = true;  
        }
        if (fieldname == 'sSurfaceCode') {
            this.sortDirHandler = this.direction === 'asc' ? 'utility:arrowup' : 'utility:arrowdown';
            this.SortIconHide.SortIcon3 = true;  
        }
        if (fieldname == 'sADACode') {
            this.sortDirHandler = this.direction === 'asc' ? 'utility:arrowup' : 'utility:arrowdown';
            this.SortIconHide.SortIcon4 = true;  
        }
        if (fieldname == 'sDescription') {
            this.sortDirHandler = this.direction === 'asc' ? 'utility:arrowup' : 'utility:arrowdown';
            this.SortIconHide.SortIcon5 = true;  
        }
        if (fieldname == 'sClaimNumber') {
            this.sortDirHandler = this.direction === 'asc' ? 'utility:arrowup' : 'utility:arrowdown';
            this.SortIconHide.SortIcon6 = true;  
        }
        if (fieldname == 'sStatus') {
            this.sortDirHandler = this.direction === 'asc' ? 'utility:arrowup' : 'utility:arrowdown';
            this.SortIconHide.SortIcon7 = true;  
        }
        this.ToothHistoryRecords = [];
        this.ToothHistoryRecords = parseData;
    }

    findByWord(event) {
        let count = 1;
        const me = this;
        if (event) {
            var element = event.target;
            var value = element.value;
            this.keyword = value;
            if (value.length > 0) {
                this.filterObj['searchByWord'] = [value];
                this.filterObj = {
                    ...this.filterObj,
                    searchByWord: [value],
                };
            } else {
                if (this.filterObj.hasOwnProperty('searchByWord')) {
                    delete this.filterObj['searchByWord'];
                    count = 0;
                }
            }
        }
        if (Object.keys(this.filterObj).length > 0) {
            this.getFilterList(this.TotalToothHistoryRecords, this.filterObj);
        } else {
            if (count == 0) {
                this.ToothHistoryRecords = me.TotalToothHistoryRecords;
                this.sortData(this.currentlySortedField,true);
                this.currentCount = this.ToothHistoryRecords.length;
                if(this.currentCount>6){
                    this.currentCount = '6+'
                }
            }
        }
    }

    getFilterList(data, filterProperties) {
        const me = this;
        let filterListData = {};
        filterListData = getFinalFilterList(data, filterProperties, this.tmp);
        this.serveresult = filterListData.uniqueList;
        this.ToothHistoryRecords = me.serveresult;
        this.sortData(this.currentlySortedField,true);
        this.currentCount = this.ToothHistoryRecords.length;
        if(this.currentCount>6){
            this.currentCount = '6+'
        }
    }

     handleScroll(event) {
        if ((event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight)
            || (event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight - 1)
            || (event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight + 1)) {
            if (this.TotalToothHistoryRecords.length <= (this.ToothHistoryRecords.length + INITIAL_RECORDS)) {
                this.ToothHistoryRecords = this.TotalToothHistoryRecords;
            } else {
                this.ToothHistoryRecords = this.TotalToothHistoryRecords.slice(0, (this.ToothHistoryRecords.length + INITIAL_RECORDS));
            }
            this.currentCount = '6+';
        }
    }
}