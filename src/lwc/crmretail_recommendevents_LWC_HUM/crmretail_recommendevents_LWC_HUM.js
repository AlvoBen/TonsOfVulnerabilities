/*
*******************************************************************************************************************************
File Name        : crmretail_recommendevents_LWC_HUM.js
Version          : 1.0 
Created Date     : 08/10/2022
Function         : Lightning web component used for the Scan Card functionality.
Modification Log :
* Developer                 Code review         Date                  Description
*******************************************************************************************************************************
* Navajit Sarkar       	                	    08/10/2022            Original Version
*/
import { LightningElement,api,track } from 'lwc';
import TIME_ZONE from '@salesforce/i18n/timeZone';
import {loadStyle} from 'lightning/platformResourceLoader'
import datatableCSS from '@salesforce/resourceUrl/CRMRetail_RecommendEvents_SR_HUM';
import createNewInteractions from '@salesforce/apex/CRMRetail_RecommendEvents_C_HUM.createNewInteractionsForRecommendation';
import getRecommendedEvents from '@salesforce/apex/CRMRetail_RecommendEvents_C_HUM.getRecommendedEvents';
import { recommendationConstants } from './crmretail_recommendevents_utility';
import {labels} from 'c/crmretail_interactionutility_LWC_HUM';
const columns = [
    { label: 'Event', fieldName: 'ReasonName', type: 'text',  sortable: true},
    { label: 'Date', fieldName: 'StartDate', type: 'text'},
    { label: 'Start Time', fieldName: 'StartDateTime', type: 'text'},
    { label: 'End Time', fieldName: 'EndDateTime', type: 'text'},
    {label: 'Virtual Event', fieldName: 'IsVirtual', type: 'button', sortable: true,
    typeAttributes:{iconName: { fieldName: 'virtualIconName' },name: 'isVirtualEvent',variant:'base',title: { fieldName: 'virtualIcon' },disabled: false,class:'virtualIconClass'}}
   ];
export default class Crmretail_recommendevents_LWC_HUM extends LightningElement {
    @api receiveDataForEvents;
    @api selectedInteraction;
    selectedEvents=[];
    columns=columns;
    data;
    result;
    selectedeventMap={};
    res;
    isSubmitDisabled=true;
    isCheckboxDisabled = true;
    noRecords=false;
    duplicateIntList;
    isCreateTask=false;
    activeSections=[];
    @track finalArray = [];
    isSpinnerVisible=false;
    recommendedEvents=false;
    upcomingEvents=false;
    allLabels = labels;
    recommendationsHeading = labels.CRMRetail_RecommendationHeading_HUM;
    optionText =labels.CRMRetail_RecommendationOptionText_HUM;
    followupText = labels.CRMRetail_RecommendationFollowupText_HUM;
    noRecommendationMsg = labels.CRMRetail_NoRecommendationText_HUM;
    submitButtonLabel = labels.CRMRetail_RecommendationSubmitButton_HUM;
    locationName;
    upcomingEventsMsg=labels.CRMRetail_UpcomingEventsMessage_HUM;
    eventNote = labels.CRMRetail_RecommendEventsNote_HUM;
    virtualEventFound = false;
    renderedCallback()
    {
        var datatableCSSFile = datatableCSS + '/CRMRetail_EventRecommendationTable_CSS.css';
        loadStyle(this,datatableCSSFile);
    }
    connectedCallback(){
        this.isSpinnerVisible=true;
        if(this.selectedInteraction && this.selectedInteraction.Account__c)
        {
            getRecommendedEvents({accId: this.selectedInteraction.Account__c})
            .then((response) =>{
            if(response &&  JSON.stringify(response) != '{}')
            {
                var responseVal;
                if(response.noEvents)
                    this.noRecords = true;
                else if(response.recommendedEvents)
                {
                    responseVal = response.recommendedEvents;
                    this.recommendedEvents = true;
                }
                else if(response.upcomingEvents)
                {                
                    this.locationName = this.selectedInteraction.Location__r.Name;
                    responseVal=response.upcomingEvents;
                    this.upcomingEvents = true;                    
                }
                for(const key in responseVal)
                {
                    var tempArray=[];                   
                    for(const element in responseVal[key])
                    {
                        var sTime = new Date(responseVal[key][element].StartDateTime);
                        var eTime = new Date(responseVal[key][element].EndDateTime);
                        let obj={};
                        obj.accordianName=key;
                        obj.eventId = responseVal[key][element].EventId;
                        obj.ReasonName = responseVal[key][element].ReasonName;
                        obj.ReasonId = responseVal[key][element].ReasonId;
                        obj.IsAllDayEvent = responseVal[key][element].IsAllDayEvent;
                        obj.isVirtualEvent= responseVal[key][element].IsvirtualEvent;
                        if(!this.virtualEventFound && obj.isVirtualEvent)
                        {
                            this.virtualEventFound = true;
                        }                            
                        obj.virtualIconName = (responseVal[key][element].IsvirtualEvent) ? "utility:check" : '';                    
                        obj.StartDate = sTime.toLocaleDateString();
                        obj.StartDateTimeActual = sTime;
                        obj.StartDateTime = sTime.toLocaleTimeString('en-US', {timeZone: TIME_ZONE}).replace(/(.*)\D\d+/,'$1');
                        obj.EndDateTimeActual = eTime;
                        obj.EndDateTime = eTime.toLocaleTimeString('en-US', {timeZone: TIME_ZONE}).replace(/(.*)\D\d+/,'$1');                        
                        tempArray.push(obj);
                        
                    }  
                    if(tempArray && tempArray.length>0)
                        this.finalArray.push({key:key,value:tempArray}); 
                }
                if(this.finalArray)  
                this.noRecords = false;  
            }
            else
            {
            this.noRecords = true;
            this.finalArray = null; 
            }            
            this.isSpinnerVisible=false;
            })
            .catch(
                (error)=>{
                    this.triggerEvent(recommendationConstants.STRING_GENERATEERRORMESSAGE);
                }
            );
        }
        
    }
    handleRowSelection(event)
    {
        this.selectedEvents=[];
        
        if(event)
        {            
            this.selectedeventMap[event.target.parentElement.name]=event.detail.selectedRows;
            for(const key in this.selectedeventMap)
                {
                    if(this.selectedeventMap[key].length>0){
                        for(const elemt in this.selectedeventMap[key])
                        {                            
                            this.selectedEvents.push(this.selectedeventMap[key][elemt]);                           
                            
                        }
                    }                     
                             
                }          
        }
        this.isSubmitDisabled =(this.selectedEvents.length > 0) ? false : true;
        this.isCheckboxDisabled = (this.selectedEvents.length > 0 )? (this.recommendedEvents ? false : true) :true;        
	if(this.selectedEvents.length == 0)
        {
            this.template.querySelector('[data-id="taskCheckbox"]').checked = false;
        }
        
    }
    closeModal(){       
        const closeevent1 = new CustomEvent(recommendationConstants.CLOSEMODAL);
        this.dispatchEvent(closeevent1);
    }
    handleSubmit(event){       
        this.isSpinnerVisible=true;
        this.isSubmitDisabled=true;
        this.isCheckboxDisabled = true;
        this.genericModalLabel=''; 
        var selectedInteractionArray = [];
        selectedInteractionArray.push(this.selectedInteraction);        
        var evtList=[];  
        var virtualEventExist;     
        for(var i=0;i<this.selectedEvents.length;i++){
            if(!this.selectedEvents[i].isVirtualEvent)
                evtList.push({"ReasonId":this.selectedEvents[i].ReasonId,
                "isAllDayEvent":this.selectedEvents[i].IsAllDayEvent,
                "StartDateTime":this.selectedEvents[i].StartDateTimeActual,
                "EndDateTime":this.selectedEvents[i].EndDateTimeActual,
                "ReasonName":this.selectedEvents[i].ReasonName,
                "IsvirtualEvent":this.selectedEvents[i].isVirtualEvent,
                "EventId":this.selectedEvents[i].eventId
            });
            else
            virtualEventExist = true;  
        }
        this.selectedEvents=evtList;
        var sCategoryType = recommendationConstants.STRING_EVENTRECOMMENDATION;
        if(virtualEventExist)
        {
            this.triggerEvent(recommendationConstants.STRING_VIRTUALEVENT_FOUND);            
        }
        if(evtList.length>0)
        {
            createNewInteractions({sinteractionList:JSON.stringify(selectedInteractionArray), sintReasonList : JSON.stringify(this.selectedEvents), categoryType:sCategoryType,isTaskCreationCheck:this.isCreateTask})
            .then(result=>{ 
                this.isSpinnerVisible=false;
                if(result && JSON.stringify(result) != '{}')
                {
                    if(result.Error){
                        this.triggerEvent(recommendationConstants.STRING_GENERATEERRORMESSAGE);
                    }
                    if(result.isSuccess === recommendationConstants.STRING_TRUE){               
                        
                        this.triggerEvent(recommendationConstants.STRING_CHECKINSUCCESS);
                    } 
                    if(result.isSuccess === recommendationConstants.STRING_FALSE || result.Duplicates){               
                        if(result.Duplicates){
                            var dupList = JSON.parse(result.Duplicates);
                            this.dupInteractionList=[];
                            this.dupInteractionList = dupList;                    
                        }
                        this.triggerEvent(recommendationConstants.STRING_DUPLICATEINTERACTION);                 
                    }
                }
                else{
                        this.triggerEvent(recommendationConstants.STRING_GENERATEERRORMESSAGE);
                }             
                                    
            })
            .catch(error=>{
                this.triggerEvent(recommendationConstants.STRING_GENERATEERRORMESSAGE);
            });         
        }
        this.isSpinnerVisible=false;                
        this.selectedEvents=[];
        this.selectedeventMap = [];           
    }
    
    triggerEvent(eventOrigin) 
    {
        var message = {}; 
        switch(eventOrigin)
        {           
            case recommendationConstants.STRING_CHECKINSUCCESS:
                message = { "value" : eventOrigin };
                break;
            case recommendationConstants.STRING_DUPLICATEINTERACTION:
                message = { "value" : eventOrigin, "DupIntList" : this.dupInteractionList};
                break;   
            case recommendationConstants.STRING_GENERATEERRORMESSAGE:
                message = {"value" : eventOrigin}; 
                break;
            case recommendationConstants.STRING_VIRTUALEVENT_FOUND:
                message = {"value" : eventOrigin};  
                break;                     
        }
        this.dispatchEvent(new CustomEvent('recommendationevent',{detail:message}));  
    }
    handleCheckboxClick(event)
    {
        this.isCreateTask = event.target.checked;
    }

    handleSectionToggle(event) {
        const openSections = event.detail.openSections;
        this.activeSections = openSections;        
    }
}