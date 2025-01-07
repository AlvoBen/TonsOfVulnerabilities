/*
LWC Name        : customLookupHum.js
Function        : JS for customLookupHum.html

Modification Log:
* Developer Name                  Date                          Description
*------------------------------------------------------------------------------------------------------------------------------
* Shailesh B                      07/03/2022                    Original Version
*****************************************************************************************************************************/
import { LightningElement, api, track } from 'lwc';
import fetchRecords from '@salesforce/apex/HumCustomLookupController_LWC.fetchRecords';

export default class CustomLookupHum extends LightningElement {
    @api disableUserLookup;
    @api objectName;
    @api fieldName;
    @api value;
    @api iconName;
    @api label;
    @api placeholder;
    @api className;
    @api required = false;
    @track searchString;
    @track selectedRecord;
    @track recordsList;
    @track message;
    @track showPill = false;
    @track showSpinner = false;
    @track showDropdown = false;
    @api selectedWorkQueue;
    @api twoCharSearchEnabled;

    connectedCallback() {
        if (this.value)
            this.fetchData();
    }
     /*
    * method called when on change of searchstring, we have param to search after two char or we can 
    * search from first char
    */
     searchRecords(event) {
        this.searchString = event.target.value;
        if(this.twoCharSearchEnabled && this.searchString.length > 1){
            this.SearchUser();
        }
        else if(!this.twoCharSearchEnabled){
            this.SearchUser();
        }
       
    }
    /** Method to call search engine */
    SearchUser(){
        if (this.searchString) {
            this.fetchData();
        } else {
            this.showDropdown = false;
        }
    }
    //getter for passing selected value from UI to remain selection on UI
    @api
    get scopeValues() {
        return this.selectedRecord;
    }

    set scopeValues(value) {
        console.log('selectedRecord value '+value);
        if(value){
            this.selectedRecord = value;
            this.value = this.selectedRecord.value;
            this.showDropdown = false;
            this.showPill = true;
        }
    }
    /*
    * method called onmoudedown and dispatch event with userName and userId
    */
    selectItem(event) {
        if (event.currentTarget.dataset.key) {
            var index = this.recordsList.findIndex(x => x.value === event.currentTarget.dataset.key)
            if (index != -1) {
                this.selectedRecord = this.recordsList[index];
                this.value = this.selectedRecord.value;
                this.showDropdown = false;
                this.showPill = true;
            }
        }
        this.dispatchEvent(new CustomEvent('userselect', {
            detail: {
                 selectedRecord : this.selectedRecord
            }
        }));
    }
     /*
    * method called onremove pill item and clear the values
    */
   @api
    removeItem() {
        this.showPill = false;
        this.value = '';
        this.selectedRecord = '';
        this.searchString = '';

        this.dispatchEvent(new CustomEvent('userselect', {
            detail: {
                selectedRecord : null
            }
        }));
    }
    /*
    * method called onclick of user input
    */
    showRecords() {
        if (this.recordsList && this.searchString) {
            this.showDropdown = true;
        }
    }

    blurEvent() {
        this.showDropdown = false;
    }
    /*
    * method for Apex call to fetch user data 
    */
    fetchData() {
        this.showSpinner = true;
        this.message = '';
        this.recordsList = [];
        fetchRecords({
            objectName: this.objectName,
            filterField: this.fieldName,
            selectedWorkQueue: this.selectedWorkQueue,
            searchString: this.searchString,
            value: this.value
        })
            .then(result => {
                if (result && result.length > 0) {
                    if (this.value) {
                        this.selectedRecord = result[0];
                        this.showPill = true;
                    } else {
                        this.recordsList = result;
                    }
                } else {
                    this.message = "No Records Found for '" + this.searchString + "'";
                }
                this.showSpinner = false;
            }).catch(error => {
                this.message = error.message;
                this.showSpinner = false;
            })
        if (!this.value) {
            this.showDropdown = true;
        }
    }
}