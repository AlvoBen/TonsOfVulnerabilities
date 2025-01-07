/*******************************************************************************************************************************
LWC JS Name : GenericDateRangePicker.js
Function    : This JS serves as controller to GenericDateRangePicker.html. 

Modification Log: 
Developer Name                  Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Mohan kumar               03/10/2021                    US: 2039867
* Mohan Kumar N             04/16/2021                    Fix for DF: 2891
* Supriya                   04/20/2021                    Fix for DF-2899
*********************************************************************************************************************************/

import { LightningElement, track, api } from 'lwc';
import {getLabels, getFormattedDate, isDateValid } from 'c/crmUtilityHum';


export default class GenericDateRangePicker extends LightningElement {
    @api label; // label of the field
    @api placeholder; // Field place holder text for the dropdown button
    @track fromDate;
    @track toDate;
    @track cssSection = "slds-hidden slds-is-absolute";
    @track bShowDatePicker = false;
    @track customLabels = getLabels();
    fieldName= {
      toDate: "toDate",
      fromDate: "fromDate"
    };

    connectedCallback() {
        const me = this;
        
        // listen focus out off datepcker 
        this.datePickerFocusOutListener = function (evnt) {
          const bDateContainer = evnt.toElement && evnt.toElement.classList
            && evnt.toElement.classList.contains('ch-custom-date-popover');
            if (bDateContainer === false && me.bShowDatePicker) {
              me.hidePicker(true);
            }
        };
        
        document.addEventListener('click', this.datePickerFocusOutListener, false);
    }

    /**
     * Hides Date Picker
     */
    @api
    hidePicker(bHide, resetForm = false) {
      const isHidden = 'slds-hidden slds-is-absolute';
      const showDateClass = 'slds-popover slds-popover_small slds-popover_prompt slds-is-absolute ch-custom-date-popover';
    
      const me = this;
      if(bHide){        
        me.cssSection = isHidden;
        me.bShowDatePicker = false;
        if (resetForm) {
          me.resetForm();
        } else {
          me.fireDatePickerClose();
        }
      }
      else{
        me.cssSection = showDateClass;
        me.bShowDatePicker = true;
      }
    }

    /**
     * Show and hide the date picker
     * @param {*} event 
     */
    datePickerShow(event) {
        if (this.bShowDatePicker) {
          this.hidePicker(true);
        } else {
          this.hidePicker(false);
        }
    }

    /**
     * Format Date as user enters
     * @param {*} event 
     */
    formatDateOnkeyupHandler(event) {
        this.toDate = this.toDate ? this.toDate : "";
        this.fromDate = this.fromDate ? this.fromDate : "";
        
        var dateInput = event.target.name;
    
        if (dateInput == this.fieldName.toDate) {
            this.toDate = getFormattedDate(event.target.value);
        }
    
        if (dateInput == this.fieldName.fromDate) {
            this.fromDate = getFormattedDate(event.target.value);
        }    
    }

    /**
     * Validate Date field 
     * @param {*} event 
     */
    validateDateFiled(event) {
      const me = this;
      const inValidDtMsg = "Invalid date";
      const fieldId = event.currentTarget.getAttribute('data-id');  
      
      const selField = fieldId === 'from-field' ? me.getField('.message-FromInput') : me.getField('.message-ToInput');
  
        if (!isDateValid(event.target.value)) {
          me.setCustomValidation(selField, inValidDtMsg);
          selField.reportValidity();
        }
        else{
          me.setCustomValidation(selField, "");
          selField.reportValidity();
        }
    }

    /**
     * Returns html element for the given selector
     * @param {*} selector 
     */
    getField(selector){
      return this.template.querySelector(selector);
    }

    /**
     * Update custom validation
     * @param {*} field 
     * @param {*} sMessage 
     */
    setCustomValidation(field, sMessage) {
      field.setCustomValidity(sMessage);
    }

    /**
     * Fire datepickerclose event on close of the date picker
     */
    fireDatePickerClose(){
        const { fromDate, toDate } = this;
        const detail = {
          fromDate,
          toDate
        }
        
        this.dispatchEvent(new CustomEvent('datepickerclose',{
          detail
        }));
    }

    /**
     * Reset Date picker form
     */
    @api
    resetForm(){
      this.fromDate = '';
      this.toDate = '';
    }
}