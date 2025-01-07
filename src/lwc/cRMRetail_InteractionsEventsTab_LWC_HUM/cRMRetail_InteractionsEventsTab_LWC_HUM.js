/*
*******************************************************************************************************************************
File Name        : cRMRetail_InteractionsEventsTab_LWC_HUM.js
Version          : 1.0 
Created Date     : 07/19/2022
Function         : Parent LWC to hold interactions and events child components
Modification Log :
* Developer                Date                  Description
*******************************************************************************************************************************
* Lakshmi Madduri      	  07/19/2022            Original Version
* Sahil Verma             08/05/2022            US-3551183: T1PRJ0154546 / SF / MF9 Storefront: Modernization - Interactions/Events - Ability to Search Visitor Check-Ins
* Navajit Sarkar          09/27/2022            User Story 3782843: MF9 Storefront: Modernization - Interactions/Events - Ability to View Calendar Events        
* Mohamed Thameem         01/05/2023            User Story 2792916: T1PRJ0154546 / SF / MF9 Storefront Home Page: Visitor Interactions Search
*/
import { LightningElement } from 'lwc';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import getInteractionsonLoad from '@salesforce/apex/CRMRetail_InteractionsEvents_LC_HUM.retrieveInteractions';
import fetchSwitch from '@salesforce/apex/CRMRetail_HomePage_LC_HUM.fetchSwitchResults';
import NameLabel from '@salesforce/label/c.CRMRetail_Name';
import {labels,allConstants} from 'c/crmretail_interactionutility_LWC_HUM';
var customLabels = labels;
var constants = allConstants;
export default class CRMRetail_InteractionsEventsTab_LWC_HUM extends LightningElement {
    sortedBy;
    sortedDirection;
    selectedLocation;
    selectedDate;
    onsiteInteractions;
    virtualInteractions;
    response;
    numberOfRowsToSkip;
    sortedTable;
    isReload;
    isSpinnerVisible=true;
    readOnly=true;
    switchMap;
    switchMapLoaded;
    searchString;
    searchType;
    connectedCallback(){
        this.isReload = false;
        this.numberOfRowsToSkip=0;
        this.getSwitchResults();
        this.getInteractions(false);       
    }

    LocDateChange() {
        this.isSpinnerVisible=true;
        this.template.querySelector('c-crmretail_checkin_interactions_-l-w-c_-h-u-m').searchText = '';
        this.template.querySelector('c-crmretail_checkin_interactions_-l-w-c_-h-u-m').value = NameLabel;
        this.template.querySelector('c-crmretail_checkin_interactions_-l-w-c_-h-u-m').placeholderText = NameLabel;
        this.searchString = '';
        this.searchType = '';
        this.getInteractions(false);   
        this.template.querySelector('c-c-r-m-retail_-events_-l-w-c_-h-u-m').reLoadEvents();
        
    }
    getSwitchResults(){
        fetchSwitch()
        .then(result => {
            if(result){
                this.switchMap = result;
                this.switchMapLoaded = true;
            }
        })
        .catch(error => {
            this.switchMapLoaded = true;
            this.isSpinnerVisible=false; 
            this.generateToastMessage(customLabels.ERROR, customLabels.UNEXPECTED_ERROR,customLabels.ERROR_VARIANT,customLabels.DISMISSIBLE);
        });
    }
    reload(event){
        this.isReload=true;
        if(event.detail){
            this.numberOfRowsToSkip = event.detail.numberOfRowsToSkip;
            this.sortedTable = event.detail.table;  
            this.sortedBy = event.detail.sortedBy;
            this.sortedDirection = event.detail.sortDirection;
            if(this.sortedTable == constants.CHECKIN && this.searchType == customLabels.CRMRetail_Interaction_Reason) {
                this.searchString = '';
            } 
            if(!this.sortedTable) this.isSpinnerVisible=true;          
        }
        this.getInteractions(false);        
    }
    getInteractions(isSearch){
        getInteractionsonLoad({numberOfRowsToSkip: this.numberOfRowsToSkip, sortedBy : this.sortedBy, sortedDirection : this.sortedDirection, sortedTable : this.sortedTable, searchString: this.searchString,searchType: this.searchType})
        .then((result)=>{
            this.selectedDate = result.SelectedDate;
            this.selectedLocation = result.SelectedLocation;
            if(this.selectedDate && this.selectedLocation && this.selectedLocation != 'None'){
                this.response = result;
                let objOnsite = {'interactions':result.OnsiteInteractions,'OnsiteCount':result.OnsiteCount};
                this.onsiteInteractions = objOnsite;
                let objVirtual = {'interactions':result.VirtualInteractions,'VirtualCount':result.VirtualCount};
                this.virtualInteractions = objVirtual;    

                //Refresh child components
                switch(this.sortedTable){
                    case constants.CHECKIN:
			            if(this.searchType != customLabels.CRMRetail_Interaction_Reason || !(this.searchString)){
                            this.template.querySelector('c-crmretail_checkin_interactions_-l-w-c_-h-u-m').loadInteractions(this.response,null,null,isSearch);
			            }
                        break;
                    case constants.VIRTUAL:
                        this.template.querySelector('c-crm_retail_virtual_interactions_-l-w-c_-h-u-m').loadInteractions(null,null,this.virtualInteractions,isSearch);
                        break;
                    case constants.ONSITE:
                        this.template.querySelector('c-crm_retail_onsite_interactions_-l-w-c_-h-u-m').loadInteractions(null,this.onsiteInteractions,null,isSearch);
                        break;
                    default:
                            this.template.querySelector('c-crm_retail_virtual_interactions_-l-w-c_-h-u-m').loadInteractions(null,null,this.virtualInteractions,isSearch);
                            this.template.querySelector('c-crm_retail_onsite_interactions_-l-w-c_-h-u-m').loadInteractions(null,this.onsiteInteractions,null,isSearch);
   			                if(this.searchType != customLabels.CRMRetail_Interaction_Reason || !(this.searchString)){
                                this.template.querySelector('c-crmretail_checkin_interactions_-l-w-c_-h-u-m').loadInteractions(this.response,null,null,isSearch);
			                }
                        break;
                }
                this.numberOfRowsToSkip=0;
                this.tableName='';
                this.isSpinnerVisible=false;
            }
            else{
                this.generateToastMessage(customLabels.ERROR, customLabels.CACHE_SETUP_ERROR_TEXT,customLabels.ERROR_VARIANT,customLabels.DISMISSIBLE);
            }        
        })
        .catch((error)=>{
            this.isSpinnerVisible=false; 
            this.generateToastMessage(customLabels.ERROR, customLabels.UNEXPECTED_ERROR,customLabels.ERROR_VARIANT,customLabels.DISMISSIBLE);
        });
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

    searchResultsHandler(event){
        if(event.detail){
            this.numberOfRowsToSkip = 0;
            this.sortedTable = null;  
            this.sortedBy = null;
            this.sortedDirection = null; 
            this.searchString = event.detail.searchString;
            this.searchType = event.detail.searchType;
        }
        this.getInteractions(true);
        this.isSpinnerVisible=true;
    }
}