/*
*******************************************************************************************************************************
File Name        : crmretail_lightning_datatable_LWC_HUM.js
Version          : 1.0 
Created Date     : 07/19/2022
Function         : Generic lightning datatable for interactions tab
Modification Log :
* Developer                Date                  Description
*******************************************************************************************************************************
* Lakshmi Madduri      	  07/19/2022            Original Version
* Sahil Verma         	  08/09/2022            US-3551183: T1PRJ0154546 / SF / MF9 Storefront: Modernization - Interactions/Events - Ability to Search Visitor Check-Ins
* Vivek Sharma            08/17/2022            User Story 3581472: T1PRJ0154546 / SF / MF9 Storefront: Ability to Create Follow Up Task From Visitor Check-Ins
* Navajit Sarkar		  09/08/2022			DF-6112 - Event Hover was showing incorrect checkin value after checkin from recommendation modal 
* Lakshmi Madduri         09/12/2022            (9/23) - Observations Fix
* Navajit Sarkar          09/27/2022            User Story 3782843: MF9 Storefront: Modernization - Interactions/Events - Ability to View Calendar Events        
*/
import { LightningElement,api,wire } from 'lwc';
import {flattenResponse,getRowActions} from "c/crmretail_interactionutility_LWC_HUM";
import noHeader from '@salesforce/resourceUrl/CRMRetail_HidePageHeader_SR_HUM';
import { encodeDefaultFieldValues } from 'lightning/pageReferenceUtils';
import datatableCSS from '@salesforce/resourceUrl/CRMRetail_InteractionsDataTable_SR_HUM';
import multiLineCSS from '@salesforce/resourceUrl/CRMRetail_multiLineToast_SR_HUM';
import {loadStyle} from 'lightning/platformResourceLoader';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import {NavigationMixin } from 'lightning/navigation';
import acknowledgeNotifications from '@salesforce/apex/CRMRetail_HomePage_LC_HUM.acknowledgeNotifications';
import getSDohAcronym from '@salesforce/apex/CRMRetail_InteractionsEvents_LC_HUM.getSDohAcronym';
import deduceHEAssociation from '@salesforce/apex/CRMRetail_InteractionsEvents_LC_HUM.deduceHEAssociation';
import createNewInteractions from '@salesforce/apex/CRMRetail_InteractionsEvents_LC_HUM.createNewInteractions';
import deleteInteractions from '@salesforce/apex/CRMRetail_InteractionsEvents_LC_HUM.deleteInteraction';
import {labels,allConstants} from 'c/crmretail_interactionutility_LWC_HUM';
import {publish,subscribe,APPLICATION_SCOPE,MessageContext} from 'lightning/messageService';
import CRMRetailMessageChannel from '@salesforce/messageChannel/CRMRetailMessageChannel__c';
import timezone from '@salesforce/i18n/timeZone';

import cancelButtonLabel from '@salesforce/label/c.CRMRetail_Cancel_ButtonLabel';

var allLabels = labels;
var constants = allConstants;
export default class Crmretail_lightning_datatable_LWC_HUM extends NavigationMixin(LightningElement)
{
    @api columns;
    @api selectedDate;
    @api selectedLocation;
    @api result;
    @api id;
    switches;
    interactions;
    title;
    @api interactionsOnsite;
    @api interactionsVirtual;
    accIdEligibleForBellIcon;
    accIdEligibleForRecommendation;
    data =[];
    displayRecords =[];
    dupInteractionList=[];
    hideCheckBox;
    showNotificationModal;
    notificationData;
    rowDataForOOOTracking;
    isShowOOORecordForm;
    selectedEvents;
    evtCalendarDate
    selectedIntRecords=[];
    currentPageNumber=0;
    showGenericModal = false;
    isAttended=false;
    isSpinnerVisible=false;
    disableScheduleButton = true;
    disableIntButton = true;
    sortedBy;
    sortedDirection;
    genericModalHeader;
    pageCount;
    numOfRecords;
    disableFirstandPreviousBtn;
    disableNextandLastBtn;
    buttonId;
    isPagination;
    tableName;
    genericModalLabel;
    isDelete;
    deleteRow;
    selectedRows=[];
    sortedField;
    sourcePageName = labels.CRMRetail_Interactions_PageName;
    @wire(MessageContext)
    messageChannel;
    isOnLoad=false;
    paddingForPagination;
    eventRecommendation;
    selectedInteraction;
    taskRecordTypeId;
    cancelButtonLabel = cancelButtonLabel;

    renderedCallback(){
        var customCSS = noHeader + '/CRMRetail_HidePageHeader.css';
        var datatableCSSFile = datatableCSS + '/CRMRetail_Interactions_CSS_HUM.css';
        var toastMsg = multiLineCSS + '/CRMRetail_multiLineToastCss.css';
        loadStyle(this,customCSS).then(()=>{
            loadStyle(this,datatableCSSFile).then(()=>{
                loadStyle(this,toastMsg);
            });
        });
    }
    @api intermediateMethod(checkin,onsite,virtual,isSearch,switches){
        this.switches = switches;
        this.result = checkin;
        this.interactionsOnsite = onsite;
        this.interactionsVirtual = virtual;
        if(this.result)
        {
            this.taskRecordTypeId = this.result.taskRecordTypeID;
            if(!this.isOnLoad) this.addRowAction();
            window.isNewHealthEducator = this.result[constants.REQ_PERMSN_SET];
            window.selectedDate = this.result.SelectedDate;
            this.hideCheckBox=false;   
            this.accIdEligibleForBellIcon=this.result.accIdEligibleForBellIcon;
            this.accIdEligibleForRecommendation=this.result.accIdEligibleForRecommendation;           
     		if(this.result.NotificationsRecord){
                 window.listOfNotifications = JSON.parse(this.result.NotificationsRecord);
            } 
            this.paddingForPagination = 'slds-p-bottom_small slds-p-left_small paginationCheckin';
            let interactions = JSON.parse(this.result.CheckInInteractions);
            this.setVariables(constants.CHECKIN,this.result.CheckinCount,constants.CHECKINBTN,interactions,allLabels.VISITOR_CHECKINS,isSearch);                      
        }
        if(this.interactionsOnsite){
            if(!this.isOnLoad) this.addRowAction()
            this.hideCheckBox=true;
            this.accIdEligibleForBellIcon=null;
            this.accIdEligibleForRecommendation=null;
            this.paddingForPagination = 'slds-p-bottom_small slds-p-left_small';
            let interactions = JSON.parse(this.interactionsOnsite.interactions);
            this.setVariables(constants.ONSITE,this.interactionsOnsite.OnsiteCount,constants.ONSITEBTN,interactions,allLabels.ONSITE_INTERACTIONS,isSearch);                          
        }

        if(this.interactionsVirtual){
            if(!this.isOnLoad) this.addRowAction()
            this.hideCheckBox=true;
            this.accIdEligibleForBellIcon=null;
            this.accIdEligibleForRecommendation=null;
            this.paddingForPagination = 'slds-p-bottom_small slds-p-left_small';
            let interactions = JSON.parse(this.interactionsVirtual.interactions);
            this.setVariables(constants.VIRTUAL,this.interactionsVirtual.VirtualCount,constants.VIRTUALBTN,interactions,allLabels.VIRTUAL_INTERACTIONS,isSearch); 
        }
        subscribe(
            this.messageChannel,
            CRMRetailMessageChannel,
            message => {
                if(message.eventOrigin === constants.FETCH_SELECTED_EVENTS){
                    this.selectedEvents = message.eventRows;
                    this.evtCalendarDate = message.calendarDate;
                    this.handleRowSelection(null);
                }  
            },
            { scope: APPLICATION_SCOPE }
          );
    }
    addRowAction(){
        var column = JSON.stringify(this.columns);
        var objColumn = JSON.parse(column);
        var actionAttr = {
            type: constants.ACTION,
            typeAttributes: { 
                rowActions: this.fetchRowActions.bind(this)
            }
        };
        objColumn.push(actionAttr);
        this.columns = objColumn;
        this.isOnLoad=true;
    }
    setVariables(tableName,count,buttonId,interactions,tableTitle,isSearch){
        this.interactions = interactions;
        this.numOfRecords = count;        
        this.data = flattenResponse(this.interactions,this.accIdEligibleForBellIcon,this.accIdEligibleForRecommendation); 
        this.displayRecords=this.data;
        this.isSpinnerVisible=false;
        this.tableName=tableName;
        this.title = tableTitle;
        let numberofPages = Math.ceil(this.numOfRecords/10);
        this.pageCount = numberofPages > 0 ? numberofPages : 1;
        if(isSearch){
            this.isPagination = false;
        }
        if(!this.isPagination){
            this.currentPageNumber=1;
            this.buttonId = buttonId;
            this.disableFirstandPreviousBtn = (this.currentPageNumber == 1) ? true : false;
            this.disableNextandLastBtn = (this.currentPageNumber == this.pageCount) ? true : false;
        }
    }
    handleFirstbtnClick(){
        this.currentPageNumber = 1;
        this.isPagination = true;      
        this.disableFirstandPreviousBtn=true;
        this.disableNextandLastBtn=false;
        this.isSpinnerVisible = true;
        this.eventToReload(0,this.tableName,this.sortedField,this.sortedDirection);
    }
    handlePreviousbtnClick(){
        this.currentPageNumber = this.currentPageNumber-1;
        this.isPagination = true;  
        this.isSpinnerVisible = true; 
        if(this.currentPageNumber == 1){
            this.disableFirstandPreviousBtn=true;
            this.disableNextandLastBtn=false;
        }
        else{
            this.disableFirstandPreviousBtn=false;
            this.disableNextandLastBtn=false;
        }
        this.eventToReload((this.currentPageNumber-1)*10,this.tableName,this.sortedField,this.sortedDirection);     
    }
    handleNextbtnClick(){
        let pageNo = this.currentPageNumber;
        this.currentPageNumber = pageNo+1;
        this.isPagination = true;   
        this.isSpinnerVisible = true;
        if(this.currentPageNumber == this.pageCount){
            this.disableNextandLastBtn = true;
            this.disableFirstandPreviousBtn = false;
        }
        else{
            this.disableNextandLastBtn = false;
            this.disableFirstandPreviousBtn = false;
        }
        this.eventToReload((pageNo)*10,this.tableName,this.sortedField,this.sortedDirection);       
    }
    handleLastbtnClick(){
        this.currentPageNumber = this.pageCount;
        this.disableNextandLastBtn = true;
        this.isPagination = true; 
        this.isSpinnerVisible = true;  
        this.disableFirstandPreviousBtn = false;
        this.eventToReload((this.pageCount-1)*10,this.tableName,this.sortedField,this.sortedDirection);        
    }
    fetchRowActions(row,doneCallback){
        let actions=[];
        actions = getRowActions(row,window.selectedDate,window.isNewHealthEducator,this.switches);
        doneCallback(actions);
    }
    handleRowAction(event){      
        const action = event.detail.action;
        const row = event.detail.row;
        switch(action.name) {
            case constants.SHOW_NOTIFICATION:
                var keys = Object.keys(window.listOfNotifications);
                if(keys.includes(row.accountId))
                {
                    var notifObj = listOfNotifications[row.accountId];
                    if(row.sDoH)
                    {      
                        getSDohAcronym({accRef :notifObj.accountRec})
                        .then((result)=>{
                            notifObj.sdohAcronym = result;
                        });
                        this.notificationData = notifObj;                           
                    }
                    else{
                        this.notificationData = notifObj;
                    }
                    this.showNotificationModal = true;
                }
                break;
            case constants.EDIT_INTERACTIONS:
                this[NavigationMixin.Navigate]({
                    type:'standard__recordPage',
                    attributes:{
                        recordId:row.id,
                        actionName:'edit'
                    },
                });               
                break;
            case constants.DELETE_INTERACTIONS:
                this.dupInteractionList=[];
                this.genericModalLabel='';
                if(row.reasonName.includes(allLabels.HEALTH_EDUCATOR))
                {
                    deduceHEAssociation({recordId : row.id})
                    .then((result)=>{
                        if(result){
                            this.genericModalLabel = labels.DELETE_HE_INTERACTION;
                        }
                        else{
                            this.genericModalLabel = labels.DELETE_INTERACTION+' '+row.name+' ?';
                        }
                        this.deleteRow = row.id;
                        this.showGenericModal = true;
                        this.genericModalHeader = allLabels.CONFIRM_DELETE_INTERACTION;
                        this.isDelete=true;
                    });
                }
                else{
                    this.genericModalLabel = labels.DELETE_INTERACTION+' '+row.name+' ?';
                    this.deleteRow = row.id;
                    this.showGenericModal = true;
                    this.genericModalHeader = allLabels.CONFIRM_DELETE_INTERACTION;
                    this.isDelete=true;
                }
                
                break;
            case constants.HEALTH_EDUCATOR:
                this.rowDataForOOOTracking = row;
                this.isShowOOORecordForm = true;
                break;
            case constants.ATTENDED:
                var evtList=[];
                this.selectedEvents=[];
                this.selectedIntRecords=[];
                for(var i=0;i<this.interactions.length;i++){
                    if(this.interactions[i].Id === row.id){
                        evtList.push({'ReasonId':this.interactions[i].Reason__r.Id,
                        'isAllDayEvent':this.interactions[i].isAllDayEvent__c,
                        'StartDateTime':this.interactions[i].Storefront_Event_Starttime__c,
                        'EndDateTime':this.interactions[i].Storefront_Event_Endtime__c,
                        'ReasonName':this.interactions[i].Reason__r.Name});
                        this.selectedIntRecords.push(this.interactions[i]);
                        break;
                    }
                }
                this.isAttended=true;
                this.selectedEvents=evtList;                
                this.createInteraction();
                break;
	        case constants.FOLLOWUP_TASK:
                const defaultValues = encodeDefaultFieldValues({
                    Subject :  allLabels.CRMRetail_FollowUp,
                    WhoId : row.personContactId,
                    WhatId : row.accountId,
                    CRM_Retail_Location__c : row.locationId
                });
                this[NavigationMixin.Navigate]({
                    type: 'standard__objectPage',
                    attributes: {
                        objectApiName: 'Task',
                        actionName: 'new'
                    },
                    state: {
                        defaultFieldValues: defaultValues,
                        navigationLocation: 'RELATED_LIST',
                        recordTypeId: this.taskRecordTypeId
                    }
                });
                break;
            case constants.SHOWRECOMMENDATIONS:
               for(var i=0;i<this.interactions.length;i++){
                    if(this.interactions[i].Id === row.id){
                        this.selectedInteraction = this.interactions[i];
                        break;
                    }
                }                 
                this.eventRecommendation=true;                
            default:
                break;            
        }
    }
    handleNotificationAck(event){
        var isWaiverDateRecAvailable;
        var notificationAck = event.detail.data;
        this.showNotificationModal = false;        
        if(notificationAck.ack===true){
            this.isSpinnerVisible=true;            
            if(notificationAck.waiverDate==true){
                isWaiverDateRecAvailable = true;
            }
            var lstOfRecToUpdate=[];
            lstOfRecToUpdate.push(notificationAck);
            var lstOfAccountIds =[];
            lstOfAccountIds.push(notificationAck.accId);  
            if(lstOfRecToUpdate && lstOfRecToUpdate.length>0){
                acknowledgeNotifications({inputJSON: JSON.stringify(lstOfRecToUpdate),accIds : lstOfAccountIds,currentLocation :notificationAck.currentLocation})
                .then((result)=>{                   
                    if(result){
                        if(isWaiverDateRecAvailable===true){                    
                            this.generateToastMessage(allLabels.WAIVER_EXPIRATION_SUCCESS, allLabels.SUCCESS_TEXT,allLabels.SUCCESS_VARIANT,constants.DISMISSIBLE);                            
                            
                        }
                    }
                    else{
                        this.generateToastMessage(allLabels.WAIVER_EXPIRATION_ERROR, allLabels.WAIVER_FAILURE,allLabels.SUCCESS_VARIANT,constants.DISMISSIBLE);
                    }
                    this.eventToReload(0,'',this.sortedField,this.sortedDirection);
                    this.isSpinnerVisible=false;
                })
                .catch(error=>{
                    this.isSpinnerVisible=false;
                    this.generateToastMessage(allLabels.ERROR, allLabels.SIGNED_ERROR,allLabels.ERROR_VARIANT,constants.DISMISSIBLE);
                });                
                        
            }         
        }
    }
    generateToastMessage(title, msg, type,sMode) {
        const event = new ShowToastEvent({
            title: title,
            message: msg,
            variant : type,            
            mode : sMode
        });
        this.dispatchEvent(event);
    }
    closeOOOModal(){
        this.isShowOOORecordForm = false;
    }
    handleRowSelection(event){
        if(event){
            this.selectedIntRecords=[];
            var selectedRows = event.detail.selectedRows;
            selectedRows.forEach(row=>{           
                for(var i=0;i<this.interactions.length;i++){
                    if(this.interactions[i].Id === row.id){
                        var obj = this.interactions[i];  
                        this.selectedIntRecords.push(obj); 
                        break;             
                    }
                }
            }); 
        }  
        if(this.selectedIntRecords && this.selectedEvents && this.selectedIntRecords.length>0 && this.selectedEvents.length>0){
            this.derivebtnEnable();
        } 
        else{
            this.disableIntButton = true;
            this.disableScheduleButton = true;
        }
    }
    derivebtnEnable(){
        this.evtCalendarDate = this.evtCalendarDate.substring(0,10);
        var arr = this.evtCalendarDate.split('-');
        var formattedCalendarDate = arr[1]+'/'+arr[2]+'/'+arr[0];
        var todayDate = new Date();
        var calendarDate = new Date(formattedCalendarDate.toLocaleString('en-US', {timeZone: timezone}));
        var localTodayDate = new Date(todayDate.toLocaleString('en-US', {timeZone: timezone}));
        var localCalendarDate  = new Date(calendarDate);
        localCalendarDate.setHours(0,0,0,0);
        localTodayDate.setHours(0,0,0,0);
        var Difference_In_Time = localCalendarDate.getTime() - localTodayDate.getTime(); 
        var Difference_In_Days = Difference_In_Time / (1000 * 3600 * 24); 
        if(localCalendarDate.getTime() == localTodayDate.getTime())
        {
            this.disableScheduleButton = false;
            if(window.selectedDate.toString() == this.evtCalendarDate.toString())
                this.disableIntButton = false;
        }
        else if(localCalendarDate.getTime() > localTodayDate.getTime() && Difference_In_Days < 90) 
            this.disableScheduleButton = false;
        else if(window.selectedDate.toString() == this.evtCalendarDate.toString())
            this.disableIntButton = false;
    }
    createInteraction(event){
        this.isSpinnerVisible=true;
        this.isPagination=false;
        this.selectedRows = [];
        this.genericModalLabel='';
        var evtList=[];
        if(!this.isAttended){
            for(var i=0;i<this.selectedEvents.length;i++){
                    evtList.push({'ReasonId':this.selectedEvents[i].Reason__r.Id,
                    'isAllDayEvent':this.selectedEvents[i].IsAllDayEvent,
                    'StartDateTime':this.selectedEvents[i].StartDateTime,
                    'EndDateTime':this.selectedEvents[i].EndDateTime,
                    'ReasonName':this.selectedEvents[i].Reason__r.Name});
            }
            this.selectedEvents=evtList;
        }      
        var sCategoryType = (this.isAttended) ? constants.SCHEDULED : event.target.name;
        createNewInteractions({sinteractionList:JSON.stringify(this.selectedIntRecords), sintReasonList : JSON.stringify(this.selectedEvents), categoryType:sCategoryType})
        .then(result=>{ 
            this.eventToReload(0,'',this.sortedField,this.sortedDirection);
            if(result.Error){
                this.generateToastMessage(allLabels.ERROR, result.Error,allLabels.ERROR_VARIANT,constants.DISMISSIBLE);
            }
            if(result.isSuccess === 'true' || (result.NotificationsRecords && result.NotificationsRecords.length>0)){               
                if(result.NotificationsRecords.length>0){
                    this.notificationData= result.NotificationsRecords;
                    this.showNotificationModal = true;
                }                                                 
                let msg='';
                if(sCategoryType == constants.SCHEDULEDVIRTUAL || sCategoryType == constants.SCHEDLEDONSITE){
                    msg = allLabels.SCH_INTERACTION_CREATED;
                }
                else{
                    msg = allLabels.INTERACTIONS_CREATED;
                }
                this.generateToastMessage(allLabels.WAIVER_EXPIRATION_SUCCESS, msg,allLabels.SUCCESS_VARIANT,constants.DISMISSIBLE);
            } 
            if(result.isSuccess === 'false' || result.Duplicates){
                if(result.Duplicates){
                    var dupList = JSON.parse(result.Duplicates);
                    this.dupInteractionList=[];
                    this.dupInteractionList = dupList;
                    this.showGenericModal = true;
                    this.genericModalHeader = allLabels.DUPLICATE_INTERACTIONS;
                }
                
            } 
            this.triggerEvent();                      
        })
        .catch(error=>{
            this.generateToastMessage(allLabels.ERROR, allLabels.UNEXPECTED_ERROR,allLabels.ERROR_VARIANT,constants.DISMISSIBLE);
        });
        this.isSpinnerVisible=false;
        this.selectedIntRecords=[];
        this.selectedEvents=[];
        this.disableScheduleButton = true;
        this.disableIntButton = true;
            
    }
    updateColumnSorting(evt){
        this.isPagination=false;
        this.sortedBy = evt.detail.fieldName;
        this.sortedDirection = evt.detail.sortDirection;
        if(this.pageCount ==1){
            this.sortData(this.sortedBy,this.sortedDirection);
        }
        else{
            this.isSpinnerVisible=true;
            var sortedBy = this.sortedBy;
            if(sortedBy === "accountURL"){
                sortedBy = "Account__r.Name";
            }  else if(sortedBy === "interactionReasonURL"){
                sortedBy = "Reason__r.Name";
            } else if(sortedBy === "Date") {
                sortedBy = "Interaction_Date__c";
            } else if(sortedBy === "category") {
                sortedBy = "Category__c";
            } else if(sortedBy === "lastModifiedByName") {
                sortedBy = "LASTMODIFIEDBY.NAME";
            } else if(sortedBy === "createdByName") {
                sortedBy = "CreatedBy.Name";
            } else if(sortedBy === "isMember") {
                sortedBy = "CRM_Retail_Interaction_Visitor_Type__c";
            } else if(sortedBy === "isMissingEvent") {
                sortedBy = "Missing_Event__c";
            }
            this.sortedField = sortedBy; 
            this.eventToReload(0,this.tableName,this.sortedField,this.sortedDirection);
        }       
    }
    sortData(fieldName,sortDirection){
        var data = this.displayRecords;
        var reverse = (sortDirection == 'asc') ? 1 : -1;
        var getFieldvalue = function (row) { return row[fieldName] };
        if (fieldName == 'Date' || fieldName == 'waiverDate') {
            data.sort(function (a, b) { //Date sorting
                var a1 = getFieldvalue(a) ? new Date(getFieldvalue(a)) : '';
                var b1 = getFieldvalue(b) ? new Date(getFieldvalue(b)) : '';
                return reverse * ((a1 > b1) - (b1 > a1));//sorting logic to return +1 or -1
            });
        }
        else {//Text sorting
            data.sort(function (a, b) {
                var a2 = getFieldvalue(a) ? getFieldvalue(a).toString().toLowerCase().trim() : '';//To handle null values , uppercase records during sorting
                var b2 = getFieldvalue(b) ? getFieldvalue(b).toString().toLowerCase().trim() : '';
                return reverse * ((a2 > b2) - (b2 > a2));//sorting logic to return +1 or -1
            });
        }
       this.displayRecords = JSON.parse(JSON.stringify(data));       
    }
    handleDelteConfirmation(){
        this.isDelete=false;
        this.showGenericModal=false;
        deleteInteractions({interactionid:this.deleteRow})
        .then(result=>{
           if(result){   
            this.isPagination = false;         
            this.triggerEvent();     
            this.eventToReload(0,'',this.sortedField,this.sortedDirection);
			this.generateToastMessage(allLabels.WAIVER_EXPIRATION_SUCCESS, allLabels.INTERACTIONS_DELETED,allLabels.SUCCESS_VARIANT,constants.DISMISSIBLE);			
           }
        })
		.catch(error=>{
            this.generateToastMessage(allLabels.ERROR, allLabels.UNEXPECTED_ERROR,allLabels.ERROR_VARIANT,constants.DISMISSIBLE);
		});
    }
	eventToReload(rowsToSkip,tableName,sortedBy,sortDir){
		this.dispatchEvent(new CustomEvent(constants.RELOAD_INTERACTIONS,
		{
			detail:{
				'numberOfRowsToSkip':rowsToSkip,
				'table':tableName,
				'sortedBy':sortedBy,
				'sortDirection':sortDir
			}
		}));
    }
    handleGenericModalClose(){
        this.showGenericModal=false;
        this.isDelete=false;
    }
    triggerEvent() {
        var payloadData = {eventOrigin:constants.INTERACTIONS, RefreshEventsTable:true};
        publish(this.messageChannel,CRMRetailMessageChannel,payloadData);
    }
    handleNotificationClose(){
        this.showNotificationModal=false;
    }
    OOOSuccess(){
        this.generateToastMessage(allLabels.WAIVER_EXPIRATION_SUCCESS, allLabels.OOO_TRACKING_RECORD_SUCCESS,allLabels.SUCCESS_VARIANT,constants.DISMISSIBLE);
        this.closeOOOModal();
    }    
    handleRecommendationClose()
    {
        this.eventRecommendation=false;
    }
    handleRecommendationEvent(event)
    {
        var eventValue = event.detail.value;                      
	    switch (eventValue){
            case constants.STRING_CHECKINSUCCESS:
                    this.eventRecommendation=false;
                    var msg = allLabels.SCH_INTERACTION_CREATED;
                    this.triggerEvent();
                    this.eventToReload(0,'',this.sortedField,this.sortedDirection);
                    this.generateToastMessage(allLabels.WAIVER_EXPIRATION_SUCCESS, msg,allLabels.SUCCESS_VARIANT,constants.DISMISSIBLE);                   
                    break;
            case constants.STRING_DUPLICATEINTERACTION:  
                    this.eventRecommendation=false;                
                    this.dupInteractionList = event.detail.DupIntList;
                    this.showGenericModal = true;
                    this.genericModalHeader = allLabels.DUPLICATE_INTERACTIONS;
                    break;
            case constants.STRING_GENERATEERRORMESSAGE:
                    this.eventRecommendation=false; 
                    this.generateToastMessage(allLabels.ERROR, allLabels.UNEXPECTED_ERROR,allLabels.ERROR_VARIANT,constants.DISMISSIBLE);
                    break;
            case constants.STRING_VIRTUALEVENT_FOUND:
                    this.eventRecommendation=false; 
                    this.generateToastMessage(allLabels.CRMRetail_Warning_Text, allLabels.CRMRetail_RecommendEventsNote_HUM,allLabels.CRMRetail_Warning_Text,constants.DISMISSIBLE);  
                    break;  
        } 
    }
}