/*
LWC Name        : genericDateSelector
Function        : LWC Component to have generic date input functionality

Modification Log:
* Developer Name                  Date                          Description
*------------------------------------------------------------------------------------------------------------------------------
* Nirmal Garg																											Original Version		
* Jonathan Dickinson               07/18/2022                     REQ- 3317212 -- Added public methods for validating input
* Kalyani Pachpol                  07/29/2022                     US-3614274
* Kalyani Pachpol                  08/11/2022                     DF-5835
*****************************************************************************************************************************
*/

import { api, LightningElement } from 'lwc';

const INVALID_DATE_EVENT = 'invaliddate'
const SEND_DATE_EVENT = 'senddatedata';
export default class GenericDateSelector extends LightningElement {

    @api
    dlabel;

    @api
    dvalue;

    @api
    dhelptext;

    @api
    dkeyname;

    @api
    dminvalue;

    @api
    dmaxvalue;
	
    @api
	isRequired;
        @api
    dvarianttype
		@api
    clearDates() {
        this.template.querySelectorAll('lightning-input').forEach((ele) => {
            ele.value = null;
        });
    }	

    sendDateData(event){
	this.dispatchEvent(new CustomEvent('inputchange'));
        let isInputFilled = this.isInputValid();
            if (isInputFilled){
                this.dispatchEvent(new CustomEvent(SEND_DATE_EVENT,{
                    detail : {
                        keyname : this.dkeyname,
                        datevalue : event.target.value
                    },
		    composed: true,
		    
                    bubbles: true
		    
                }))
        }
        else {
            this.dispatchEvent(new CustomEvent(INVALID_DATE_EVENT, {
                detail: {
                    keyname: this.dkeyname,
                    datevalue: event.target.value
                }
            }))
        }
       
    }
    get underflowmessage() {
        if(this.dminvalue && this.dmaxValue){
            return `Value must be ${this.dminvalue} or later.`;
        }
    }

    get overflowmessage() {
        if(this.dminvalue && this.dmaxValue){
            return `Value must be ${this.dmaxValue} or earlier.`;
        }
    }

    @api
    isInputValid() {
        const isInputValid = [...this.template.querySelectorAll('lightning-input')]
            .reduce((validSoFar, inputField) => {
                inputField.reportValidity();
                return validSoFar && inputField.checkValidity();
            }, true);
        return isInputValid;
    }

    @api
    blur() {
        const inputs = [...this.template.querySelectorAll('lightning-input')];
        inputs.forEach(input => {
            input.blur();
        });
    }

    @api
    focus() {
        const inputs = [...this.template.querySelectorAll('lightning-input')];
        inputs.forEach(input => {
            input.focus();
        });
    }
    
    @api
    clearValues(){
        const inputs = [...this.template.querySelectorAll('lightning-input')];
        inputs.forEach(input => {
            input.value='';
        });
    }
}