import { LightningElement, api, wire, track } from 'lwc';
import TIME_ZONE from "@salesforce/i18n/timeZone";
import { NavigationMixin } from 'lightning/navigation';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import { encodeDefaultFieldValues } from 'lightning/pageReferenceUtils';
import DATE_VALIDATION_TEXT from '@salesforce/label/c.Valid_Format_For_Calendar_Date';
import CACHE_SETUP_ERROR_TEXT from '@salesforce/label/c.CRMRetail_CacheSetup_ErrorText';
import ONSITE from '@salesforce/label/c.Icon_Label_For_Onsite_Interaction';
import SCH_ONSITE from '@salesforce/label/c.Icon_Label_For_ScheduledOnsite_Interaction';
import VIRTUAL from '@salesforce/label/c.Icon_Label_For_Virtual_Interaction';
import SCH_VIRTUAL from '@salesforce/label/c.Icon_Label_For_ScheduledVirtual_Interaction';
import RELEASE_DATE from '@salesforce/label/c.CRMRetail_Release_Date';
import GO_365_ELIGIBLE from '@salesforce/label/c.CRM_Retail_Go365_Eligibility';
import newEventValidation from '@salesforce/apex/CRMRetail_InteractionsEvents_LC_HUM.isNewEvtButtonVisible';
import getEventRecords from '@salesforce/apex/CRMRetail_InteractionsEvents_LC_HUM.getEvents';
import getLocationAndDateValues from '@salesforce/apex/CRMRetail_InteractionsEvents_LC_HUM.getLocationAndDateValues';
import {publish, subscribe, APPLICATION_SCOPE, MessageContext} from 'lightning/messageService';
import CRMRetailMessageChannel from '@salesforce/messageChannel/CRMRetailMessageChannel__c';
import crmRetailStylesheet from '@salesforce/resourceUrl/CRMRetail_InteractionsDataTable_SR_HUM';
import { loadStyle } from 'lightning/platformResourceLoader';

const columns = [
    { label: 'Interaction Reason', fieldName: 'eventURL', type: 'url',initialWidth: 250,  sortable: true,
        cellAttributes:{class:'overflowClass'},
        typeAttributes: {label: { fieldName: 'name' }, target: '_blank', tooltip: { fieldName:'tooltip'}}},
    { label: 'Start', fieldName: 'startdatetime', type: 'text', sortable: true, cellAttributes:{class:'overflowClass'} },
    { label: 'End', fieldName: 'enddatetime', type: 'text', sortable: true, cellAttributes:{class:'overflowClass'} } 
];
const data = [
];
const flattenData = [
];
const selectedRecordList = [];
const eventRecordsList = [];

export default class CRMRetail_Events_LWC_HUM extends NavigationMixin(LightningElement) {
    data = data;
    columns = columns;
    isCssLoaded = false; 
    @api switchmap;
    @wire(MessageContext)
    messageContext;
    @track flattenData = flattenData;
    @api label;
    @track today;
    @track sortedBy;
    @track sortedDirection;
    @track showButton = false;
    @track isShowSpinner = false;
    @track subscription = null;
    label = {
        DATE_VALIDATION_TEXT
    };
    renderedCallback() {            
        if(this.switchmap){
            if(!this.switchmap.Switch_3782843){
                if(this.isCssLoaded) return            
                    loadStyle(this,crmRetailStylesheet+'/CRMRetail_ScrollBar_CSS.css').then(()=>{
                        this.isCssLoaded = true;            
                })
                .catch(error=>{
                    this.generateErrorMessage(error.body.exceptionType, error.body.message);
                });       
            }
        }            
    }
    connectedCallback(){        
        this.subscribeToMessageChannel();
        this.initiateProcess();
    }

    initiateProcess(){
        this.isShowSpinner = true;
        newEventValidation()
            .then(result => {
                if(result.isEligible === 'true'){
                    this.showButton = true;
                }
                this.today = result.currIntDate;
                if(this.today){
                    this.getEvents();
                }
            })
            .catch(error => {
                this.error = error;
                this.isShowSpinner = false;
                this.generateErrorMessage(error.body.exceptionType, error.body.message);
        });
    }

    @api reLoadEvents() {
       this.initiateProcess(); 
    }
    
    getEvents(){
        getEventRecords({ displayDate : String(this.today)})
            .then(result => {
                if(result){
                    this.data = result;
                    this.flattenData = this.flattenResponse();
                }
                this.isShowSpinner = false;
            })
            .catch(error => {
                this.error = error;
                this.isShowSpinner = false;
                this.generateErrorMessage(CACHE_SETUP_ERROR_TEXT);
        });
    }
    flattenResponse(){
        var gcmEventList = this.data;
        var gcmEventFlatList = [];
        var flatFieldList = this.eventFlatFields();
        var rlDate = RELEASE_DATE;        
        var ReleaseDateTime = new Date(rlDate).toISOString();
        for(var i = 0; i < gcmEventList.length; i++) {
            var obj = {};
            obj["tooltip"] = '';
            for(var j = 0; j < flatFieldList.length; j++) {
                var fieldMapValue = this.eventFieldMap(flatFieldList[j]);
                if(fieldMapValue.includes(".")) {
                    var mapArr = fieldMapValue.split(".");
                    var sValue = "";
                    sValue = (typeof gcmEventList[i][mapArr[0]]) ? gcmEventList[i][mapArr[0]][mapArr[1]] : null;
                    obj[flatFieldList[j]] = sValue;
                }
                else {
                    var iFieldMapVal = gcmEventList[i][fieldMapValue];
                    var allDayFieldValue = this.eventFieldMap(flatFieldList[flatFieldList.indexOf("isAllDay")]);
                    var localDate = (typeof iFieldMapVal) ? new Date(iFieldMapVal) : null;
                    if ((flatFieldList[j] === "startdatetime" || flatFieldList[j] === "enddatetime") && localDate){         
                        var localTime = (gcmEventList[i][allDayFieldValue]) ? "11:59 PM" : localDate.toLocaleTimeString('en-US', {timeZone: TIME_ZONE}).replace(/(.*)\D\d+/,'$1');
                        obj[flatFieldList[j]] = (typeof iFieldMapVal) ? localTime : null;
                    }
                    else {
                        obj[flatFieldList[j]] = (typeof iFieldMapVal) ? iFieldMapVal : null;
                    }  
		        }
            }		            
            obj["tooltip"] += "Event Name: " + obj['name'];             
            var eDateTime = obj["startDate"];
            if(eDateTime){                    
                var eventDateTime = new Date(eDateTime).toISOString();
                if(obj["go365Eligible"] && eventDateTime >= ReleaseDateTime){
                    obj["tooltip"] += "\n" + GO_365_ELIGIBLE;
                }
            }  
            obj["tooltip"] += "\n" + ONSITE + ":";
            obj["tooltip"] += obj["onsite"] != null ? obj['onsite']:'0';
            obj["tooltip"] += "\n" + VIRTUAL + ":";
            obj["tooltip"] += obj["virtual"] != null ? obj['virtual']:'0';
            obj["tooltip"] += "\n" + SCH_ONSITE + ":";
            obj["tooltip"] += obj["onsiteScheduled"] != null ?  obj['onsiteScheduled']:'0';
            obj["tooltip"] += "\n" + SCH_VIRTUAL + ":";
            obj["tooltip"] += obj["virtualScheduled"] != null ?  obj['virtualScheduled']:'0';   
            obj.eventURL = '/lightning/r/Event/' + gcmEventList[i].Id + '/view'  //CHECKMARX CHNAGE
            gcmEventFlatList.push(obj);
            this.isShowSpinner = false;
        }
        return gcmEventFlatList;  
    }
    eventFlatFields() {
        var flatFields = ['id', 'name', 'startdatetime', 'enddatetime', 'location', 'isAllDay', 'onsite', 'virtual', 'onsiteScheduled', 'virtualScheduled', 'go365Eligible', 'startDate'];
        return flatFields;
    }
    eventFieldMap(field) {
        var gcmEventFieldObj = {
            id: 'Id',
            name: 'Reason__r.Name',
            startdatetime: 'StartDateTime',
            enddatetime: 'EndDateTime',
            location: 'Location',
            isAllDay: 'IsAllDayEvent',
            onsiteScheduled: 'Scheduled_Onsite_Attendee_Count__c',
            virtualScheduled: 'Scheduled_Virtual_Attendee_Count__c',
            onsite: 'Known_Attendee_Count__c',
            virtual: 'Virtual_Attendee_Count__c',
            go365Eligible : 'Reason__r.isGo365Eligible__c',
            startDate : 'StartDateTime'
        };
        return gcmEventFieldObj[field];
    }
    handleClick(){
        getLocationAndDateValues({sEventSelectedDate:this.today})
            .then(result => {                
                let resultStartTime = result.startTime;
                let resultendTime = result.endTime;
                const defaultValues = encodeDefaultFieldValues({
                    WhatId : result.location,
                    StartDateTime :resultStartTime,
                    EndDateTime : resultendTime                    
                });
                this[NavigationMixin.Navigate]({
                    type: 'standard__objectPage',
                    attributes: {
                        objectApiName: 'Event',
                        actionName: 'new'
                    },
                    state: {
                        defaultFieldValues: defaultValues,
                        navigationLocation: 'RELATED_LIST'
                    }
                });
            })
            .catch(error => {
                this.error = error;
                this.generateErrorMessage(error.body.exceptionType, error.body.message);
        });
    }
    handleRefresh(){
        this.isShowSpinner = true;
        if(this.switchmap){
            if(this.switchmap.Switch_3516893){
                this.flattenData = [];
            }
        }
        this.selectedRecordList = [];
        this.template.querySelector('lightning-datatable').selectedRows = [];
        this.getEvents();
    }
    handleDateChange(event){
        const isInputsCorrect = [...this.template.querySelectorAll('lightning-input')]
            .reduce((validSoFar, inputField) => {
                inputField.reportValidity();
                return validSoFar && inputField.checkValidity();
            }, true);
        if(isInputsCorrect){
            this.isShowSpinner = true;
            this.today = event.target.value;
            this.getEvents();
        }
    }
    updateColumnSorting(event){
        this.sortedBy = event.detail.fieldName;
        this.sortedDirection = event.detail.sortDirection;
        this.sortData(this.sortedBy, this.sortedDirection);
    }
    sortData(fieldName, sortDirection){
        var data = JSON.parse(JSON.stringify(this.flattenData));
        var reverse = sortDirection == 'asc' ? 1 : -1;
        fieldName = (fieldName === "eventURL") ? "name" : fieldName;
        var key = function (a) { return a[fieldName] };	
        if(fieldName === "startdatetime" || fieldName === "enddatetime"){
            var rowsLst = this.data;
            var selDate = (rowsLst && rowsLst[0] && rowsLst[0].StartDateTime) ? rowsLst[0].StartDateTime : '';

            if(selDate){
                var sDate = selDate.substring(0,10);
                data.sort(function (a, b) {                
                    var a = key(a) ? new Date(sDate + ' '+ key(a).toLowerCase().trim()) : '';
                    var b = key(b) ? new Date(sDate + ' '+ key(b).toLowerCase().trim()) : '';
                    return reverse * ((a > b) - (b > a));
                }); 
            }            
        }else{
            data.sort(function (c, d) {           
                var c = key(c) ? key(c).toLowerCase().trim() : '';
                var d = key(d) ? key(d).toLowerCase().trim() : '';
                return reverse * ((c > d) - (d > c));
            }); 
        }    
        this.flattenData = data;
    }
    getSelectedRow(event){
        var selectedRows = event.detail.selectedRows;
        var allSelectedRows = [];
        var selEvents = [];
        var todayDt = new Date();
        var calendarDate = new Date(this.today);
		todayDt.setHours(calendarDate.getHours());
        todayDt.setMinutes(calendarDate.getMinutes());
        todayDt.setSeconds(calendarDate.getSeconds());
        selectedRows.forEach(function(row) {
            allSelectedRows.push(row.id);
        });
        this.selectedRecordList = allSelectedRows;
        if(typeof this.selectedRecordList) {
            if(this.selectedRecordList.length > 0) {
                var selRecords = this.selectedRecordList;
                var eventRecords = this.data;
                
                for(var i = 0; i < selRecords.length; i++) {
                    for(var j = 0; j < eventRecords.length; j++) {
                        if(selRecords[i] === eventRecords[j].Id) {
                            selEvents.push(eventRecords[j]);
                        }
                    }
                }
            }
            this.triggerEvent('fetchSelectedEvents', selEvents);
        }
    }
    triggerEvent(eventOrigin, eventRows) {
        var payloadData = {eventRows : eventRows,
            eventOrigin : eventOrigin,
            calendarDate : String(this.today)};
        publish(this.messageContext,CRMRetailMessageChannel,payloadData);
    }
    subscribeToMessageChannel() {
        if (!this.subscription) {
          this.subscription =
            subscribe(
              this.messageContext,
              CRMRetailMessageChannel,
              message => {
                this.handleEventTraverse(message);
              },
              { scope: APPLICATION_SCOPE }
            );
        }
      }
      handleEventTraverse(message){
        if(message.eventOrigin === 'Interactions' && message.RefreshEventsTable === true){            
            this.selectedRecordList = [];
            this.template.querySelector('lightning-datatable').selectedRows = [];
            this.getEvents();
        }
      }
    generateErrorMessage(errTitle, errMessage) {
        var toastEvent = new ShowToastEvent({
            title: errTitle,
            message: errMessage,
            variant: 'error',
            mode: 'sticky'
        });
        this.dispatchEvent(toastEvent);
    }
}