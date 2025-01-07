/******************************************************************************************************************
LWC Name           : genericMultiselectPicklistHum.js
Version            : 1.0
Function           : Component to display generic multiselect picklist.
Created On         : 05/13/2022
*******************************************************************************************************************
Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Nirmal garg                                     05/13/2022                Original Version
* Aishwarya Pawar                                 01/12/2023                Changes for REQ- 4080893

 *******************************************************************************************************************/
import { LightningElement, api, track } from 'lwc';

export default class GenericMultiselectPicklistHum extends LightningElement {
    @api
    keyname;


    @track
    selectedValues = [];

    @api
    placeholdermessage;

    @api
    title;

    @track
    optionsSelected;

    @track optionData = [];
    @track message;
    @track showDropdown = false;
    @track value;
    @track multiselect = true;

	@api
    get selectedPicklistValues() {
        return true;
    }

    set selectedPicklistValues(value) {
        if (value.length > 0) {
            this.totalOptions = value.length;
            this.optionsSelected =
                this.totalOptions > 0
                    ? this.totalOptions + ' Options'
                    : this.placeholdermessage;
            value.forEach((ele) => {
                this.selectedValues.push(ele);
            });
        }
    }

    connectedCallback() {
        this.optionsSelected = this.placeholdermessage;
        if(this.selectedValues && this.selectedValues.length > 0){
            let optionvalues = JSON.parse(JSON.stringify(this.optionData));
            for (let i = 0; i < optionvalues.length; i++) {
                    optionvalues[i].isVisible = true;
                    optionvalues[i].selected = this.optionData.length > 0 && this.optionData.includes(optionvalues[i].value) ? true : false;
            }
            this.optionsSelected = this.optionData.length  > 0 ? this.optionData.length  + ' options' : this.placeholdermessage;
            this.optionData = optionvalues;
        }
    }

    handleComboBox(event) {
        event.currentTarget.classList.toggle('slds-is-open');
        event.currentTarget.classList.toggle('slds-has-focus');
    }
    hidebox(event) {
        this.template.querySelectorAll('.slds-is-open').forEach(k => {
            if (k.classList.contains('slds-is-open')) {
                k.classList.remove('slds-is-open')
            }
        })
        this.template.querySelectorAll('.slds-has-focus').forEach(k => {
            if (k.classList.contains('slds-has-focus')) {
                k.classList.remove('slds-has-focus')
            }
        })
    }

		@api
    get setvalues(){
        return this.selectedValues;
    }
    set setvalues(value){
        if(value != null && value != undefined){
            if(typeof(value) === 'object'){
                if(Array.isArray(JSON.parse(JSON.stringify(value)))){
                    this.selectedValues = JSON.parse(JSON.stringify(value));
                    this.optionsSelected = this.selectedValues.length  > 0 ? this.selectedValues.length  + ' options' : this.placeholdermessage;
                }
            }
        }else{
            this.selectedValues = [];
        }
    }

    @api
    get options(){
        return this.optionData;
    }

    set options(value){
        if(value != null){
            this.optionData = value;
        }
    }
    selectItem(event) {
        let selectedVal = event.currentTarget.dataset.id;
        let count = 0;
        if (selectedVal) {
            let optionvalues = JSON.parse(JSON.stringify(this.optionData));
            for (let i = 0; i < optionvalues.length; i++) {
                if (optionvalues[i].value === selectedVal) {
                    if (this.multiselect) {
                        if (this.selectedValues.includes(optionvalues[i].value)) {
                            this.selectedValues.splice(this.selectedValues.indexOf(optionvalues[i].value), 1);
                        } else {
                            this.selectedValues.push(optionvalues[i].value);
                        }
                        optionvalues[i].selected = optionvalues[i].selected ? false : true;
                    }

                }
                if (optionvalues[i].selected) {
                    count++;
                }
            }
            this.optionData = optionvalues;
            if (this.multiselect) {
                this.optionsSelected = count > 0 ? count + ' Options' : this.placeholdermessage;
            }
            if (this.multiselect) {
                event.preventDefault();
            } else {
                this.showDropdown = false;
            }
        }

    }
    showOptions() {
        if (this.optionData != null) {
            if (this.optionData.length > 0) {
                let optionvalues = JSON.parse(JSON.stringify(this.optionData));
                for (let i = 0; i < optionvalues.length; i++) {
                    optionvalues[i].isVisible = true;
                    optionvalues[i].selected = this.selectedValues.length > 0 && JSON.stringify(this.selectedValues).includes(optionvalues[i].value) ? true : false;
                }
                if (optionvalues.length > 0) {
                    this.showDropdown = true;
                }
                this.optionData = optionvalues;
            }
        }

    }
    blurEvent() {
        let optionvalues = this.optionData != null && this.optionData.length > 0 ? JSON.parse(JSON.stringify(this.optionData))  : [];
                optionvalues.forEach( k=>{
                    if(this.selectedValues.includes(k.value)){
                        k.selected = true;
                    }else{
                        k.selected = false;
                    }
                });
                this.optionData = optionvalues;
            this.showDropdown = false;


       
            this.dispatchEvent(
                new CustomEvent('multiselectfilter', {
                    detail: {
                        keyname: this.keyname,
                        selectedvalues: this.selectedValues
                    },
                    composed: true,
                    bubbles: true
                })
            );
        
    }



    handleSelection(event) {
        let element = event.currentTarget.firstChild;
        if (element.classList.contains("slds-is-selected")) {
            this.selectedValues = this.selectedValues.filter(option => option != event.currentTarget.dataset.value);
        } else {
            this.selectedValues.push(event.currentTarget.dataset.value);
        }
        element.classList.toggle("slds-is-selected");
        this.dispatchEvent(new CustomEvent('multiselectfilter', {
            detail: {
                keyname: this.keyname,
                selectedvalues: this.selectedValues
            }
        }));
        this.optionsSelected = this.selectedValues.length > 0 ? this.selectedValues.length + ' options' : this.placeholdermessage;
    }

    @api
    clearSelection(inputdata) {
        if (inputdata != null) {
            if (this.keyname === inputdata.keyname) {
				if(this.selectedValues.includes(inputdata.value))
                {
                    this.selectedValues.splice(this.selectedValues.indexOf(inputdata.value),1);
                }
        	let optionvalues = this.optionData != null && this.optionData.length > 0 ? JSON.parse(JSON.stringify(this.optionData))  : [];
                optionvalues.forEach( k=>{
                    if(this.selectedValues.includes(k.value)){
                        k.selected = true;
                    }else{
                        k.selected = false;
                    }
                });
                this.optionData = optionvalues;
                this.optionsSelected = this.selectedValues.length > 0 ? this.selectedValues.length + ' options' : this.placeholdermessage;
            }
        }
    }

    @api
    clearDropDowns() {
        this.selectedValues = [];
        let count = 0;
        let optionvalues = this.optionData != null && this.optionData.length > 0 ? JSON.parse(JSON.stringify(this.optionData)) : [];
        if(optionvalues != null && optionvalues.length > 0){
            optionvalues.forEach(k => {
                if(k.hasOwnProperty("selected")){
                    k.selected = false;
                }
                else{
                    k.selected = false;
                }
            })
        }
        this.optionData = optionvalues;
        this.optionsSelected = this.selectedValues.length > 0 ? this.selectedvalues + ' options' : this.placeholdermessage;
    }
}