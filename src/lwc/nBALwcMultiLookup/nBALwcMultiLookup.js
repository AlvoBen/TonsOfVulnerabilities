import { LightningElement,api,track } from 'lwc';
import getResults from '@salesforce/apex/NBALwcMultiLookupController.getResults';
import saveJunctionRecord from '@salesforce/apex/NBALwcMultiLookupController.saveJunctionRecord';
import loadWorkQueueRecord from '@salesforce/apex/NBALwcMultiLookupController.loadWorkQueueRecord';
import getNameOfRecommendation from '@salesforce/apex/NBALwcMultiLookupController.getNameOfRecommendation';

function urlParam(name){
    var results = new RegExp('[\?&]' + name + '=([^&#]*)').exec(window.location.href);
    if (results == null){
       return null;
    }
    else {
       return decodeURI(results[1]) || 0;
    }
}

export default class nBALwcMultiLookup extends LightningElement {
    @api objectName = 'Work_Queue_Setup__c';
    @api fieldName = 'Name';
    @api Label;
    @track searchRecords = [];
    @track selectedRecords = [];
    @api required = false;
    @api iconName = 'action:new_account'
    @api LoadingText = false;
    @track txtclassname = 'slds-combobox slds-dropdown-trigger slds-dropdown-trigger_click';
    @track messageFlag = false; 
    displayNoDataMessage = false;
    searchField(event) {

        this.displayNoDataMessage =  false;
       
        var currentText = event.target.value;
        var selectRecId = [];
        for(let i = 0; i < this.selectedRecords.length; i++){
            selectRecId.push(this.selectedRecords[i].recId);
        }
        this.LoadingText = true;
        getResults({ ObjectName: this.objectName, fieldName: this.fieldName, value: currentText, selectedRecId : selectRecId })
        .then(result => {
            this.searchRecords= result;
            this.LoadingText = false;
            
            this.txtclassname =  result.length > 0 ? 'slds-combobox slds-dropdown-trigger slds-dropdown-trigger_click slds-is-open' : 'slds-combobox slds-dropdown-trigger slds-dropdown-trigger_click';
            if(currentText.length > 0 && result.length == 0) {
                this.messageFlag = true;
            }
            else {
                this.messageFlag = false;
            }

            if(this.selectRecordId != null && this.selectRecordId.length > 0) {
                this.iconFlag = false;
                this.clearIconFlag = true;
            }
            else {
                this.iconFlag = true;
                this.clearIconFlag = false;
            }
        })
        .catch(error => {
            console.log('-------error-------------'+error);
        });
        
    }
    
   setSelectedRecord(event) {
        var recId = event.currentTarget.dataset.id;
        this.workQueueIdList.push(recId);
        var selectName = event.currentTarget.dataset.name;
        let newsObject = { 'recId' : recId ,'recName' : selectName };
        this.selectedRecords.push(newsObject);
        this.txtclassname =  'slds-combobox slds-dropdown-trigger slds-dropdown-trigger_click';
        let selRecords = this.selectedRecords;
		this.template.querySelectorAll('lightning-input').forEach(each => {
            each.value = '';
        });
        const selectedEvent = new CustomEvent('selected', { detail: {selRecords}, });
        // Dispatches the event.
        this.dispatchEvent(selectedEvent);
    }

    removeRecord (event){
        let selectRecId = [];
        this.workQueueIdList = [];
        for(let i = 0; i < this.selectedRecords.length; i++){
            if(event.detail.name !== this.selectedRecords[i].recId)
                selectRecId.push(this.selectedRecords[i]);
                
        }
        this.selectedRecords = [...selectRecId];
        let selRecords = this.selectedRecords;
             // this.workQueueIdList.push(selRecords);  
        
         for(let i=0; i<selRecords.length; i++){
            let newsObject = { 'recId' : selRecords[i].recId ,'recName' : selRecords[i].recName };
            
            this.workQueueIdList.push( selRecords[i].recId); 
            }
         
        const selectedEvent = new CustomEvent('selected', { detail: {selRecords}, });
        // Dispatches the event.
        this.dispatchEvent(selectedEvent);
    }

    renderedCallback(){
        let searchboxInput = this.template.querySelector('[data-id="userinput"]');
        searchboxInput.focus();
       

    }

    recommendationId;
    @api recommendationIdToFetch; 
    recommendationName = '';
    @api recommendationIdString;
   
    connectedCallback() {
		
        this.recommendationId = urlParam('id');
        
        loadWorkQueueRecord({
            recommendationIdToFetch : this.recommendationId
            
        }).then(data =>{
            if(data !=null){
               
for(let i=0; i<data.length; i++){
let newsObject = { 'recId' : data[i].Work_Queue_Setup__c ,'recName' : data[i].Work_Queue_Setup__r.Name };
this.selectedRecords.push(newsObject);
let newsObjectToSave = { 'workQueueToSave' : data[i].Work_Queue_Setup__c };

this.workQueueIdList.push( data[i].Work_Queue_Setup__c); 
}
        
            }
        }).catch(error => {
            if(error!=null){
                console.log('error occured.');
            }
        })
        getNameOfRecommendation({
            recommendationIdString : this.recommendationId
            
        }).then(data =>{ 
            if(data !=null){
                this.recommendationName =  data;
            }
        }).catch(error => {
            if(error!=null){
                console.log('error occured.');
            }
        })
    }

     workQueueIdList = []
     @api recommendationIdToSave;
     @api workQueudIdsToSave;
	 @api isLoading  = false;
   
    saveRecord(){
	    this.isLoading = true;
        this.displayNoDataMessage =  false;
        saveJunctionRecord({
            recommendationIdToSave : this.recommendationId,
            workQueudIdsToSave : this.workQueueIdList
        }).then(data =>{
            if(data !=null){
                this.displayNoDataMessage =  true;
				this.isLoading  = false;
            }
        }).catch(error => {
            if(error!=null){
                console.log('error occured.'); 
				this.isLoading  = false;				
                
            }
        })
        
    }

    hideNoDataMsg(){
        this.displayNoDataMessage =  false;
    }

    resetRecord(){
		this.txtclassname =  'slds-combobox slds-dropdown-trigger slds-dropdown-trigger_click';
        this.template.querySelectorAll('lightning-input').forEach(each => {
            each.value = '';
        });
        var currentText = '';
        var selectRecId = [];
        this.messageFlag = false;
        this.isLoading = true;
        this.displayNoDataMessage = false;
        this.selectedRecords = [];
        this.recommendationId = urlParam('id');
        
        loadWorkQueueRecord({
            recommendationIdToFetch : this.recommendationId
            
        }).then(data =>{
            if(data !=null){
				this.isLoading  = false;           
for(let i=0; i<data.length; i++){
let newsObject = { 'recId' : data[i].Work_Queue_Setup__c ,'recName' : data[i].Work_Queue_Setup__r.Name };
this.selectedRecords.push(newsObject);


}
        
            }
        }).catch(error => {
            if(error!=null){
                console.log('error occured.');
				this.isLoading  = false;
            }
        })


    }
}