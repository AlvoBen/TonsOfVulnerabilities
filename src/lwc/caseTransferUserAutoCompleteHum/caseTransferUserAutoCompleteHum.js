/*******************************************************************************************************************************
LWC JS Name : CaseTrasnferUserAutoCompleteHum.js
Function    : This JS serves as helper to CaseTrasnferUserAutoCompleteHum.html 

Modification Log: 
Developer Name           Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
* Ankima Srivastava                                      03/01/2022                   initial version
* Ritik                                                  03/10/2022                   Added User autopouplate and disable feature
*********************************************************************************************************************************/
import { api, LightningElement, track, wire } from 'lwc';
import searchObject from '@salesforce/apex/CaseTransferAutoUser_LC_HUM.searchObject';
import { hcConstants, eventKeys } from "c/crmUtilityHum";


export default class customLookUp extends LightningElement {
    @api sQueueName; //Queue name coming from parent customTransferServiceDeptHum
    @api searchTerm = ""; //inputValue
   
    @track objName = 'User';
    @track selectedName; //this will hold the user name selected
    @track records; //this will fetch the list of user names
    @track isValueSelected;
    @track responseReady = false;
    @track disableUsrFld = false
    //css
    @track boxClass = 'slds-combobox slds-dropdown-trigger slds-dropdown-trigger_click slds-has-focus';
    @track inputClass = '';
    noResultsMsg = false;
    @track hasRendered = true;

    /**
    * Method Name: disabledUserField
    * Function:handles the user field to disable or enable based on service center and department values and called from casetransferServiceDeptHum
    */
   @api
    disabledUserField(searchValue, isDisable){
        if(!(searchValue === 'NOT_ERASE')){
        this.isValueSelected = false;
        this.responseReady = false;
        this.searchTerm = '';
        this.disableUsrFld = isDisable;
        }else{
            this.disableUsrFld = isDisable;
        }
        if (isDisable) {
            this.noResultsMsg = false;
        }
    }

    renderedCallback() {
        if (this.searchTerm) {
            if (this.hasRendered === true) {
                this.isValueSelected = true;
                this.selectedName = this.searchTerm;
            }
            this.hasRendered = false;
        }
    }

    /**
    * Method Name: fetchUserValue
    * Function:used to get user field value on click of save  and called from casetransferServiceDeptHum
    */
   @api
   fetchUserValue(){
       return this.template.querySelector('input') ? this.template.querySelector('input').value : null;  
   }
    /**
    * Method Name: handleClick
    * Function:handles the focus part of user input box
    */
    handleClick() {
        this.inputClass = 'slds-has-focus';
        this.boxClass = 'slds-combobox slds-dropdown-trigger slds-dropdown-trigger_click slds-has-focus slds-is-open';
    }
    /**
     * Method Name: onSelect
     * Function: this function fetched the value of selected user details and transfer it to parent cmp
     */
    onSelect(event) {
        this.responseReady = false;
        let selectedId = event.currentTarget.dataset.id;
        let selectedName = event.currentTarget.dataset.name;

        const userSelected = new CustomEvent("populateduserdetails", {
            detail: {
                userId: selectedId,
                userName: selectedName
            }
        });
        this.dispatchEvent(userSelected);

        this.isValueSelected = true;
        this.noResultsMsg = false;
        this.selectedName = selectedName;

        this.boxClass = 'slds-combobox slds-dropdown-trigger slds-dropdown-trigger_click slds-has-focus';
    }
    /**
     * Method Name: handleRemovePill
     * Function: this helps in removing the text once cross icon is selected
     */
    handleRemovePill() {
        let selectedId = null;
        let selectedName = null;

        const userSelected = new CustomEvent("populateduserdetails", {
            detail: {
                userId: selectedId,
                userName: selectedName
            }
        });
        this.dispatchEvent(userSelected);
        this.searchTerm = '';
        this.isValueSelected = false;
        this.responseReady = false;
    }
    /**
     * Method Name: onChange
     * Function:this is called everytime user enters some value and it fetches the list of users
     */
    onChange(event) {
       this.hasRendered = false;
        this.searchTerm = event.target.value;
        let value = this.searchTerm;
        if (value.length <= hcConstants.MIN_SEARCH_CHAR) {
            this.records = '';
            this.responseReady = false;
            this.noResultsMsg = false;
        }
        if (value.length > hcConstants.MIN_SEARCH_CHAR) {
            if (!this.sQueueName) {
                this.noResultsMsg = true;
            }
            this.populateUserDetails(value);
        } 

    }
    async populateUserDetails(val) {
        try {
            const result = await searchObject({ sObjectName: this.objName, sQueueName: this.sQueueName, sQuery: val });
            if (result && result.length > 0) {
                this.records = result;
                this.responseReady = true;
            } else {
                this.responseReady = false;
                this.noResultsMsg = true;
            }
        } catch (error) {
            console.log('Error---> ', error);
        }
    }

     /**
     * Handle input focus out handler
     */
    onFocusOutHandler(event) {    
    }


}